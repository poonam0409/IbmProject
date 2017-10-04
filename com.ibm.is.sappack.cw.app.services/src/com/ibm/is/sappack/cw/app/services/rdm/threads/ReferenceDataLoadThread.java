package com.ibm.is.sappack.cw.app.services.rdm.threads;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpSession;

import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;

import com.ibm.is.sappack.cw.app.data.config.LegacySystem;
import com.ibm.is.sappack.cw.app.data.rdm.IColumn;
import com.ibm.is.sappack.cw.app.data.rdm.ITable;
import com.ibm.is.sappack.cw.app.data.rdm.ReferenceTableType;
import com.ibm.is.sappack.cw.app.data.rdm.TableStatus;
import com.ibm.is.sappack.cw.app.data.rdm.jpa.RdmSet;
import com.ibm.is.sappack.cw.app.data.rdm.jpa.ReferenceTable;
import com.ibm.is.sappack.cw.app.data.rdm.jpa.ReferenceTableColumn;
import com.ibm.is.sappack.cw.app.data.rdm.jpa.TextTable;
import com.ibm.is.sappack.cw.app.services.AbstractCancelableThread;
import com.ibm.is.sappack.cw.app.services.CWDBConnectionFactory;
import com.ibm.is.sappack.cw.app.services.Constants;
import com.ibm.is.sappack.cw.app.services.CwApp;
import com.ibm.is.sappack.cw.app.services.CwAppException;
import com.ibm.is.sappack.cw.app.services.RfcDestinationDataProvider;
import com.ibm.is.sappack.cw.app.services.Util;
import com.ibm.is.sappack.cw.app.services.rdm.DBOperations;
import com.ibm.is.sappack.gen.tools.sap.utilities.IResult;
import com.ibm.is.sappack.gen.tools.sap.utilities.SAPDomainExtractor;
import com.ibm.is.sappack.gen.tools.sap.utilities.SAPTableExtractor;
import com.ibm.websphere.webmsg.publisher.Publisher;
import com.ibm.websphere.webmsg.publisher.PublisherException;
import com.ibm.websphere.webmsg.publisher.jndijms.BayeuxJmsTextMsg;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;

public class ReferenceDataLoadThread extends AbstractCancelableThread {

	private static final String CLASS_NAME = ReferenceDataLoadThread.class.getName();

	private static enum LOAD_STATUS {
		LOADING(0), OK(1), FAILED(2), NO_CHANGES(3);

		private final int value;

		LOAD_STATUS(int value) {
			this.value = value;
		}
	}

	private static final String TABLE_STATUS_ROW_COUNT_TOPIC = "/table/rowcount";
	private static final String TABLE_STATUS_ROW_NUMBER_TOPIC = "/table/rownumber";
	private static final String TABLE_STATUS_TOPIC = "/table/status";

	private final Collection<ReferenceTable> referenceTables;
	private LegacySystem legacySystem;
	// The rollout value is needed to get the table metadata for initial loading
	private String rollout = null;
	private boolean initialLoad = true;
	private final String loadLanguage;

	private JCoDestination jcoDestination;
	private int tableProgress;

	// Case 1: Initial loading. The SAP system is given as a parameter and is the same for all tables.
	public ReferenceDataLoadThread(Publisher publisher, Collection<ReferenceTable> referenceTables, LegacySystem legacySystem, String rollout,
			HttpSession session, String loadLanguage) {
		super(session, publisher);
		this.referenceTables = referenceTables;
		this.loadLanguage = loadLanguage;
		this.legacySystem = legacySystem;
		this.rollout = rollout;
	}

	// Case 2: Reloading. The SAP system is read from each individual table object and can be different each time.
	public ReferenceDataLoadThread(Publisher publisher, Collection<ReferenceTable> referenceTables, HttpSession session, String loadLanguage) {
		this(publisher, referenceTables, null, null, session, loadLanguage);
		this.initialLoad = false;
	}

	@Override
	public void run() {
		final String METHOD_NAME = "run()";
		logger.entering(CLASS_NAME, METHOD_NAME);

		// Get the JCo destination for the single SAP system if we are doing an initial load
		if (initialLoad) {
			try {
				jcoDestination = RfcDestinationDataProvider.getDestination(legacySystem);
			} catch (JCoException e) {
				Util.handleBatchException(e);
			}
		}

		// loop over the list of reference tables and import the data originally
		// fetched from SAP into the target database
		for (ReferenceTable table : referenceTables) {
			if (cancelled) {
				logger.fine("Cancelled by user");
				break;
			}

			try {
				// Retrieve the complete metadata for the table from CWDB if this is the initial load
				if (initialLoad) {
					if (table.getTableType() == ReferenceTableType.CHECK_TABLE) {
						table = DBOperations.getCheckTableFromCWDB(table.getName(), legacySystem.getLegacyId(), rollout);
					} else {
						table = DBOperations.getDomainTableFromCWDB(table.getName(), legacySystem.getLegacyId(), rollout);
					}
				}

				// publish a message that we're starting the import of a reference table
				publishClientUpdate(TABLE_STATUS_TOPIC, composeOverallUpdate(table, LOAD_STATUS.LOADING));

				// check if the table exists in the CW database
				if (!DBOperations.referenceTableExistsInCWDB(table.getName())) {
					// Status: table missing in CW DB
					table.setTableStatus(TableStatus.MISSING_IN_CW);
					onTableLoadFailed(table);
				}

				// check if the text table exists in the CW database (if any)
				if (table.hasTextTable() && !DBOperations.referenceTableExistsInCWDB(table.getTextTable().getName())) {
					table.setTableStatus(TableStatus.TEXT_TABLE_MISSING_IN_CW);
					// Status: text table missing in CW DB
					onTableLoadFailed(table);
				}

				// check if the reference table exists in the JPA database (which means it has been loaded before)
				checkCWAppDBForTable(table);

				// Get the data from SAP
				JCoDestination tableJcoDestination = getJcoDestination(table);
				if (tableJcoDestination == null) {
					// Status: Reloading failed, SAP system doesn't exist in CWApp DB anymore
					onTableLoadFailed(table);
				}
				
				List<Map<String, String>> extractedSapRefTable = extractSapTable(table, tableJcoDestination);
				List<Map<String, String>> extractedSapTxtTable = null;

				if (!referenceDataHasChanged(extractedSapRefTable, table)) {
					onTableIsUpToDate(table);
				} else {
					int totalRows = extractedSapRefTable.size();
					DBOperations.clearTableInCWDB(table);

					if (table.hasTextTable()) {
						// Always clear the text table as well
						DBOperations.clearTableInCWDB(table.getTextTable());
						
						extractedSapTxtTable = extractSapTable(table.getTextTable(), tableJcoDestination);
						totalRows += extractedSapTxtTable.size();
					}

					// publish the total number rows for this table (reference + text)
					publishClientUpdate(TABLE_STATUS_ROW_COUNT_TOPIC, composeTableUpdate(table, totalRows));

					// save the data to CWDB
					if (!saveTableDataToCwdb(table, extractedSapRefTable, 0)) {
						// Status: failed to save data to CWDB
						onTableLoadFailed(table);
					} else {
						if (table.hasTextTable()) {
							if (!saveTableDataToCwdb(table.getTextTable(), extractedSapTxtTable, table.getRowCount())) {
								// Status: failed to save text table data to CWDB
								onTableLoadFailed(table);
							}
						}
						onTableLoadedSuccessfully(table);
					}
				}
			} catch (JCoException e) {
				// Status: SAP connection error
				onTableLoadFailed(table);
				Util.handleBatchException(e);
			} catch (SQLException e) {
				// Status: internal error
				onTableLoadFailed(table);
				Util.handleBatchException(e);
			} catch (NamingException e) {
				// Status: internal error
				onTableLoadFailed(table);
				Util.handleBatchException(e);
			} catch (CwAppException e) {
				// Status: internal error (in a lower layer), already logged
				onTableLoadFailed(table);
			}
		}

		// Remove the the reference to this thread from the session, so the garbage collector can clean up
		session.removeAttribute(Constants.SESSION_ATTRIBUTE_NAME_RDM_LOAD_THREAD);
		logger.exiting(CLASS_NAME, METHOD_NAME);
	}

	@SuppressWarnings("unchecked")
	private boolean saveTableDataToCwdb(ITable table, List<Map<String, String>> extractedSapTable, int completedRows) {
		final String METHOD_NAME = "loadTable(ITable table, List<Map<String, String>> extractedSapTable, int completedRows)";
		logger.entering(CLASS_NAME, METHOD_NAME);

		Connection conn = null;
		PreparedStatement stmt = null;

		boolean success = true;
		try {
			if (cancelled) {
				logger.fine("Cancelled by user");
				// TODO: another possible table status is 'cancelled'
				return false;
			}

			conn = CWDBConnectionFactory.getConnection();
			stmt = conn.prepareStatement(generateInsertStatement(table));

			// Process the data from SAP
			table.setRowCount(extractedSapTable.size());
			int rowProgress = 1;

			for (Map<String, String> sapRow : extractedSapTable) {
				Iterator<IColumn> iterator = table.getColumns().iterator();
				for (int i = 1; iterator.hasNext(); i++) {
					if (cancelled) {
						break;
					}

					IColumn column = iterator.next();
					String value = sapRow.get(column.getSapName());

					// Since Oracle treats empty strings as NULL values, replace them with blanks
					if (value == null || value.isEmpty()) {
						value = Constants.ORACLE_EMPTY_STRING;
					}

					stmt.setString(i, value);
					logger.fine("Column: " + column.getSapName() + ", value: " + value);
				}

				stmt.addBatch();

				// for every 10th row, publish the current table row number to the client
				if (rowProgress % 10 == 0) {
					publishClientUpdate(TABLE_STATUS_ROW_NUMBER_TOPIC, composeTableUpdate(table, completedRows + rowProgress));
				}

				// execute the precompiled batch of SQL statements every 50th row in order to avoid
				// batches that are too large (probable cause for DB2 aborts)
				if (rowProgress % 50 == 0) {
					stmt.executeBatch();
				}

				rowProgress++;
			}

			// execute the remaining batch of SQL statements
			stmt.executeBatch();

			// at this point we need to check whether we need to commit the changes to the database or not
			// in case the task has been cancelled we rollback all changes
			if (cancelled) {
				logger.fine("Cancelled by user");
				success = false;
				conn.rollback();
			} else {
				conn.commit();
			}
		} catch (SQLException e) {
			success = false;
			Util.throwInternalErrorToClient(conn, e);
		} catch (NamingException ne) {
			success = false;
			Util.throwInternalErrorToClient(ne);
		} finally {
			Util.closeDBObjects(stmt, conn);
		}

		logger.exiting(CLASS_NAME, METHOD_NAME);
		return success;
	}

	private boolean referenceDataHasChanged(List<Map<String, String>> extractedSapTable, ReferenceTable table) {
		final String METHOD_NAME = "referenceDataHasChanged(List<Map<String, String>> extractedSapTable, ReferenceTable table)";
		logger.entering(CLASS_NAME, METHOD_NAME);
		logger.fine("Table: " + table.getName());

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		HashSet<String> valuesCWDB = new HashSet<String>();
		HashSet<String> valuesSAP = new HashSet<String>();
		boolean result = false;

		if (table.getTableStatus() == TableStatus.NOT_LOADED) {
			// the table has not been loaded before so we need to load it now
			result = true;
		} else if (table.getRowCount() == 0 && extractedSapTable.size() == 0) {
			// the table is loaded but the are no rows, so we don't need to load it again
			result = false;
		} else if (table.getRowCount() != extractedSapTable.size()) {
			// the row count stored in CW DB and the number of extracted rows from SAP are not the same, so there were changes
			result = true;
		} else {
			// deep check via hash code of all values
			try {
				conn = CWDBConnectionFactory.getConnection();
				stmt = conn.createStatement();

				rs = stmt.executeQuery("SELECT * FROM " + CwApp.getReferenceTableSchema() + ".\"" + table.getName() + '"');

				Collection<ReferenceTableColumn> columns = table.getColumns();
				while (rs.next()) {
					for (ReferenceTableColumn column : columns) {
						String value = rs.getString(column.getName());
						logger.finest("Value from CWDB: " + value);
						valuesCWDB.add(value);
					}
				}

				for (Map<String, String> sapRow : extractedSapTable) {
					for (ReferenceTableColumn column : columns) {
						String value = sapRow.get(column.getSapName());
						// Account for the replaced empty values in CWDB
						if (value == null || value.isEmpty()) {
							value = Constants.ORACLE_EMPTY_STRING;
						}
						logger.finest("Value from SAP: " + value);
						valuesSAP.add(value);
					}
				}
				result = valuesCWDB.hashCode() != valuesSAP.hashCode();
			} catch (NamingException e) {
				Util.handleBatchException(e);
			} catch (SQLException e) {
				Util.handleBatchException(e);
			} finally {
				Util.closeDBObjects(rs, stmt, conn);
			}
		}

		logger.fine("Result: " + (valuesCWDB.hashCode() != valuesSAP.hashCode()));
		logger.exiting(CLASS_NAME, METHOD_NAME);
		return result;
	}
	
	// Determine the JCo destination for the given table
	private JCoDestination getJcoDestination(ITable table) throws JCoException {
		JCoDestination destination = null;
		if (initialLoad) {
			// Initial loading, use the selected SAP system
			destination = jcoDestination;
		} else {
			// Reloading. Each reference table has the legacy ID set already. Retrieve the SAP system details from the DB.
			List<LegacySystem> sapSystemAsList = com.ibm.is.sappack.cw.app.services.config.DBOperations.getTargetLegacySystem(table.getLegacyId(), session);		
			if (!sapSystemAsList.isEmpty()) {
				legacySystem = sapSystemAsList.get(0);
				destination = RfcDestinationDataProvider.getDestination(legacySystem);
			} else {
				logger.info("Error: SAP system not found. Table cannot be reloaded: " + table.getName());
			}
		}
		return destination;
	}

	// Extracts the data for the given table from the given JCo destination
	@SuppressWarnings("unchecked")
	private List<Map<String, String>> extractSapTable(ITable table, JCoDestination tableJcoDestination) throws JCoException {
		// Make a list of the SAP names of the table's columns
		List<String> sapColumnNames = new ArrayList<String>();
		Iterator<IColumn> iterator = table.getColumns().iterator();
		while (iterator.hasNext()) {
			IColumn column = iterator.next();
			sapColumnNames.add(column.getSapName());
		}
		
		// Extract the data depending on table type
		IResult result = null;
		if (table.getTableType() == ReferenceTableType.CHECK_TABLE || table instanceof TextTable) {
			// Check table or text table
			SAPTableExtractor extractor = new SAPTableExtractor(tableJcoDestination, table.getSapName(), sapColumnNames, "");
			result = extractor.performQuery();
		} else {
			// Domain table
			SAPDomainExtractor domExtractor = new SAPDomainExtractor(tableJcoDestination, table.getSapName(), loadLanguage);
			result = domExtractor.performQuery();
		}

		// Copy the data into a new list for easier handling and to avoid the IResult limitations (one time iteration)
		List<Map<String, String>> sapDataMapList = new ArrayList<Map<String, String>>();
		while (result.nextRow()) {
			sapDataMapList.add(result.getRow());
		}

		return sapDataMapList;
	}

	@SuppressWarnings("unchecked")
	private String generateInsertStatement(ITable table) {
		StringBuffer insertStatementBuffer = new StringBuffer();
		insertStatementBuffer.append("INSERT INTO ");

		insertStatementBuffer.append('"');
		insertStatementBuffer.append(CwApp.getReferenceTableSchema());
		insertStatementBuffer.append('"');

		insertStatementBuffer.append('.');
		insertStatementBuffer.append(table.getName());
		insertStatementBuffer.append(" (");

		StringBuffer valuesBuffer = new StringBuffer();
		Iterator<IColumn> iterator = table.getColumns().iterator();

		for (int i = 0; iterator.hasNext(); i++) {
			IColumn column = iterator.next();
			insertStatementBuffer.append(column.getName());
			valuesBuffer.append('?');

			if (i < (table.getColumns().size() - 1)) {
				insertStatementBuffer.append(',');
				valuesBuffer.append(',');
			}
		}

		insertStatementBuffer.append(") VALUES (");
		insertStatementBuffer.append(valuesBuffer);
		insertStatementBuffer.append(')');

		logger.fine(insertStatementBuffer.toString());
		return insertStatementBuffer.toString();
	}

	private void onTableLoadedSuccessfully(ReferenceTable table) {
		tableProgress++;

		// persist the reference table in the JPA store
		persistReferenceTable(table);

		// publish the table status to the client
		publishClientUpdate(TABLE_STATUS_TOPIC, composeOverallUpdate(table, LOAD_STATUS.OK));
	}

	private void onTableLoadFailed(ReferenceTable table) {
		tableProgress++;

		// publish the table status to the client
		publishClientUpdate(TABLE_STATUS_TOPIC, composeOverallUpdate(table, LOAD_STATUS.FAILED));
	}

	private void onTableIsUpToDate(ReferenceTable table) {
		table.setTableStatus(TableStatus.UP_TO_DATE);

		// persist the reference table in the JPA store
		persistReferenceTable(table);
		tableProgress++;

		// publish the table status to the client
		publishClientUpdate(TABLE_STATUS_TOPIC, composeOverallUpdate(table, LOAD_STATUS.NO_CHANGES));
	}

	private void persistReferenceTable(ReferenceTable refTable) {
		final String METHOD_NAME = "persistReferenceTable(ReferenceTable refTable)";
		logger.entering(CLASS_NAME, METHOD_NAME);
		List<ReferenceTable> refTables = null;

		try {
			jpaTransaction.begin();
			manager.joinTransaction();
			if (refTable.getTableStatus() == TableStatus.LOADED) {
				// The table status is LOADED. Update the corresponding JPA entity.
				TypedQuery<ReferenceTable> refquery = manager.createNamedQuery("ReferenceTable.retrieveByName", ReferenceTable.class);
				refquery.setParameter("name", refTable.getName());
				refTables = refquery.getResultList();
				ReferenceTable existingTable = refTables.get(0);

				// We reloaded the reference table. If there is a rdm set, it is no longer up to date.
				RdmSet set = existingTable.getTargetRdmSet();
				if (set != null && set.getRdmId() != null) {
					set.setUptodate(false);
					manager.merge(set);
				}

				existingTable.setRowCount(refTable.getRowCount());
				existingTable.setLastLoad(new Date());
				existingTable.setLegacyId(legacySystem.getLegacyId());

				manager.merge(existingTable);
			} else if (refTable.getTableStatus() == TableStatus.NOT_LOADED) {
				// The table status is NOT_LOADED. Create a new JPA entity.
				refTable.setLastLoad(new Date());
				refTable.setLegacyId(legacySystem.getLegacyId());

				logger.finer("Persisting reference table: " + refTable.getName());
				int numRules = 0;
				if (refTable.getSourceDataCollectionRules() != null) {
					numRules = refTable.getSourceDataCollectionRules().size();
				}
				logger.finest("Number of source data collection rules: " + numRules);
				manager.persist(refTable);
			} else if (refTable.getTableStatus() == TableStatus.UP_TO_DATE) {
				// The table is up to date, update last load timestamp
				TypedQuery<ReferenceTable> refquery = manager.createNamedQuery("ReferenceTable.retrieveByName", ReferenceTable.class);
				refquery.setParameter("name", refTable.getName());
				refTables = refquery.getResultList();
				refTable = refTables.get(0);
				refTable.setLastLoad(new Date());

				manager.merge(refTable);
			}
			jpaTransaction.commit();
		} catch (Exception e) {
			Util.throwInternalErrorToClient(jpaTransaction, e);
		}
		logger.exiting(CLASS_NAME, METHOD_NAME);
	}

	// check whether the given table exists in the CWApp database
	// If so, sets the table status field to "LOADED" and the row count
	private void checkCWAppDBForTable(ReferenceTable refTable) {
		List<ReferenceTable> refTables = null;

		try {
			jpaTransaction.begin();
			manager.joinTransaction();

			TypedQuery<ReferenceTable> refquery = manager.createNamedQuery("ReferenceTable.retrieveByName", ReferenceTable.class);
			refquery.setParameter("name", refTable.getName());
			refTables = refquery.getResultList();

			if (!refTables.isEmpty()) {
				refTable.setTableStatus(TableStatus.LOADED);
				refTable.setRowCount(refTables.get(0).getRowCount());
			}

			jpaTransaction.commit();
		} catch (Exception e) {
			Util.throwInternalErrorToClient(jpaTransaction, e);
		}
	}

	private String composeOverallUpdate(ReferenceTable table, LOAD_STATUS loadStatus) {
		JSONObject updateJson = new JSONObject();

		try {
			updateJson.put("referenceTableId", table.getReferenceTableId());
			updateJson.put("name", table.getName());
			updateJson.put("tableType", table.getTableType().toString());
			updateJson.put("number", tableProgress);
			if (loadStatus != LOAD_STATUS.LOADING) {
				// If we've just begun loading, the row count is unknown
				updateJson.put("rowcount", table.getRowCount());
			}
			updateJson.put("status", loadStatus.value);
			if (table.hasTextTable()) {
				updateJson.put("txttable", table.getTextTable().getName());
			} else {
				updateJson.put("txttable", "");
			}
		} catch (JSONException e) {
			Util.handleBatchException(e);
		}

		return updateJson.toString();
	}

	private String composeTableUpdate(ITable table, int rowCount) {
		JSONObject updateJson = new JSONObject();

		try {
			updateJson.put("name", table.getName());
			updateJson.put("rowcount", rowCount);
		} catch (JSONException e) {
			Util.throwInternalErrorToClient(e);
		}

		return updateJson.toString();
	}
	
	private void publishClientUpdate(String topic, String message) {
		try {
			publisher.publish(new BayeuxJmsTextMsg(topic + sessionId, message));
		} catch (PublisherException e) {
			Util.handleBatchException(e);
		}
	}
}
