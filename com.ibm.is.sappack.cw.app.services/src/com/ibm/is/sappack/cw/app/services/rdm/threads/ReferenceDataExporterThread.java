package com.ibm.is.sappack.cw.app.services.rdm.threads;

import java.net.HttpURLConnection;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.NamingException;
import javax.servlet.http.HttpSession;
import javax.transaction.Status;

import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;

import com.ibm.is.sappack.cw.app.data.Resources;
import com.ibm.is.sappack.cw.app.data.rdm.IColumn;
import com.ibm.is.sappack.cw.app.data.rdm.RdmSetVersionInfo;
import com.ibm.is.sappack.cw.app.data.rdm.jpa.RdmSet;
import com.ibm.is.sappack.cw.app.data.rdm.jpa.ReferenceTable;
import com.ibm.is.sappack.cw.app.data.rdm.jpa.ReferenceTableColumn;
import com.ibm.is.sappack.cw.app.data.rdm.jpa.TextTableColumn;
import com.ibm.is.sappack.cw.app.services.AbstractCancelableThread;
import com.ibm.is.sappack.cw.app.services.CWDBConnectionFactory;
import com.ibm.is.sappack.cw.app.services.Constants;
import com.ibm.is.sappack.cw.app.services.CwApp;
import com.ibm.is.sappack.cw.app.services.CwAppException;
import com.ibm.is.sappack.cw.app.services.Util;
import com.ibm.is.sappack.cw.app.services.rdm.clients.RdmSetClient;
import com.ibm.websphere.webmsg.publisher.Publisher;
import com.ibm.websphere.webmsg.publisher.PublisherException;
import com.ibm.websphere.webmsg.publisher.jndijms.BayeuxJmsTextMsg;

public class ReferenceDataExporterThread extends AbstractCancelableThread {

	private static final String CLASS_NAME = ReferenceDataExporterThread.class.getName();
	
	private static final String SQL_LABEL_TEXTTABLE = "T";

	private Collection<Integer> tableIds;
	private RdmSetClient rdmSetClient = null;
	private String rdmLanguage = null;

	private int tableProgress = 0;
	
	public ReferenceDataExporterThread(Publisher publisher, Collection<Integer> tableIds, RdmSetClient rdmSetClient,
			String rdmLanguage, HttpSession session) {
		super(session, publisher);
		this.tableIds = tableIds;
		this.rdmSetClient = rdmSetClient;
		this.rdmLanguage = rdmLanguage;
	}

	@Override
	public void run() {
		final String METHOD_NAME = "run()"; 
		logger.entering(CLASS_NAME, METHOD_NAME);

		// Loop over the list of reference tables and export the data to RDM
		for (int pk : tableIds) {
			if (!cancelled) {
				tableProgress++;
				processTable(pk);
			}
		}
		// Remove the reference to this thread from the session, so that the garbage collector can clean it up
		session.removeAttribute(Constants.SESSION_ATTRIBUTE_NAME_RDM_EXPORT_THREAD);
		logger.exiting(CLASS_NAME, METHOD_NAME);
	}

	private void processTable(int id) {
		final String METHOD_NAME = "processTable(int id)";
		logger.entering(CLASS_NAME, METHOD_NAME);
		ReferenceTable table = null;
		try {
			jpaTransaction.begin();
			manager.joinTransaction();
			table = manager.find(ReferenceTable.class, id);
			if (table != null) {
				// publish the table row count to the client
				// we publish the double rowCount because we will
				// check fist if the set is uptodate on rdm
				publishNewRow(table, table.getRowCount(), table.getRowCount() * 2);

				// Load the data from the CW DB
				Set<Map<String, String>> tableDataMapSet = getTableDataFromCwdb(table);

				// Create or find the RDM data type
				String rdmTypeId = rdmSetClient.createType(table);

				// Create the RDM set or set version
				String setName = Util.generateTargetRdmSetName(table);
				String rdmSetBaseId;
				String rdmSetVersionId;
				String rdmSetVersion;
				boolean needToExport = true;
				boolean rdmHubIsUpToDate = false;
				// Check if a base set exists
				RdmSetVersionInfo rdmSetVersionInfo = rdmSetClient.getSetByName(setName);
				if (rdmSetVersionInfo != null) {
					// The type already exists
					rdmSetBaseId = rdmSetVersionInfo.getBaseId();
					rdmSetVersion = rdmSetVersionInfo.getVersionNumber();
					rdmSetVersionId = rdmSetVersionInfo.getVersionId();
					RdmSet targetSet = manager.find(RdmSet.class, rdmSetBaseId);
					// need to commit so messages can be published to client
					jpaTransaction.commit();
					// we need to check if the latest set version in RDM Hub is identical to
					// our current set version
					if (targetSet != null) {
						if (rdmSetVersionId.equals(targetSet.getVersionId())
								&& rdmSetClient.isSetUpToDate(rdmSetVersionId, tableDataMapSet, publisher, sessionId)) {
							// the values in the set on rdm are the same that we want to export
							// so there is no need to export, but the RdmSet here was null so we can recreate it
							needToExport = false;
							rdmHubIsUpToDate = true;
						} else {
							// the set it not up to date or the stored version id has been deleted on rdm
							rdmSetVersionId = rdmSetClient.createSetVersion(rdmSetVersionInfo, setName, rdmTypeId);
						}
					} else {
						// there is no set stored in our database, but we can
						// check anyway if the latest version on rdm has
						// the same values like the values we want to export
						if (rdmSetClient.isSetUpToDate(rdmSetVersionId, tableDataMapSet, publisher, sessionId)) {
							// the values are equal, we will not export but we have to create the entities
							// NOTE: the user will just get the status "OK"
							needToExport = false;
						} else {
							// values are not the same we should create a new version
							rdmSetVersionId = rdmSetClient.createSetVersion(rdmSetVersionInfo, setName, rdmTypeId);
						}
					}
				} else {
					// Create a new data set
					rdmSetVersionId = rdmSetClient.createTargetSet(table, rdmTypeId);
					rdmSetBaseId = rdmSetVersionId;
					rdmSetVersion = "1";
				}

				if (needToExport) {
					if (jpaTransaction.getStatus() == Status.STATUS_ACTIVE) {
						jpaTransaction.commit();
					}
					// Load the actual data into the RDM data set
					rdmSetClient.insertIntoSet(rdmSetVersionId, tableDataMapSet, publisher, sessionId,
							tableDataMapSet.size());
				}

				jpaTransaction.begin();
				manager.joinTransaction();
				table = manager.find(ReferenceTable.class, id);
				if (table == null) {
					// This can happen if the table gets deleted in between.
					publishOverallProgress(Constants.EXPORT_STATUS_TABLE_NOT_FOUND);
					jpaTransaction.commit();
					return;
				}
				RdmSet rdmSet = null;
				if (table.getTargetRdmSet() != null) {
					rdmSet = table.getTargetRdmSet();
					if (!rdmSet.getRdmId().equals(rdmSetBaseId)) {
						// The set in RDM has a different base ID, discard the old JPA entity
						table.setTargetRdmSet(null);
						manager.remove(rdmSet);
					}
				}
				if (table.getTargetRdmSet() == null) {
					rdmSet = new RdmSet();
					table.setTargetRdmSet(rdmSet);
					rdmSet.setRdmId(rdmSetBaseId);
					rdmSet.setName(setName);
					rdmSet.setCreated(new Date());
				}
				rdmSet.setUptodate(true);
				rdmSet.setVersion(rdmSetVersion);
				rdmSet.setVersionId(rdmSetVersionId);
				manager.persist(table);
				manager.merge(rdmSet);
				jpaTransaction.commit();
				// publish the table status to the client
				if (rdmHubIsUpToDate) {
					// the values in RDM Hub are the same as in the CW DB
					publishOverallProgress(Constants.EXPORT_STATUS_SET_NOT_CHANGED);
				} else {
					publishOverallProgress(Constants.EXPORT_STATUS_OK);
				}
			} else {
				// This can happen if the table gets deleted in between.
				publishOverallProgress(Constants.EXPORT_STATUS_TABLE_NOT_FOUND);
				jpaTransaction.commit();
			}
		} catch (Exception e) {
			Util.handleBatchException(jpaTransaction, e);
			// Publish the table status to the client
			publishOverallProgress(Constants.EXPORT_STATUS_INTERNAL_ERROR);
		}
		logger.exiting(CLASS_NAME, METHOD_NAME);
	}

	private Set<Map<String, String>> getTableDataFromCwdb(ReferenceTable table) throws NamingException, SQLException {
		final String METHOD_NAME = "getTableDataFromCwdb(ReferenceTable table)"; 
		logger.entering(CLASS_NAME, METHOD_NAME);
		
		Connection connection = null;
		Statement stmt = null;
		Set<Map<String, String>> tableDataMapSet = null;

		try {
			connection = CWDBConnectionFactory.getConnection();
			String query = generateSqlQuery(table);
			stmt = connection.createStatement();

			// Execute the query
			ResultSet resultSet = stmt.executeQuery(query);
			tableDataMapSet = processQueryResultSet(table, resultSet);
		}
		finally {
			Util.closeDBObjects(stmt, connection);
		}
		
		logger.exiting(CLASS_NAME, METHOD_NAME);
		return tableDataMapSet;
	}

	// Iterates over the result set from the CW DB
	// and generates a set of rows represented by maps between column names and values for sending to RDM Hub
	private Set<Map<String, String>> processQueryResultSet(ReferenceTable table, ResultSet resultSet)
			throws SQLException {
		Set<Map<String, String>> tableDataMapList;
		tableDataMapList = new HashSet<Map<String, String>>();
		Map<String, String> row = null;
		
		switch (table.getTableType()) {
		case CHECK_TABLE:
			// If there is a text table, find the description column
			String descriptionColumn = null;
			if (table.getTextTable() != null) {
				descriptionColumn = findDescriptionColumn(table);
			}
	
			while (resultSet.next()) {
				row = new HashMap<String, String>();
				
				// The unique ID concatenating all column values except the client column (e.g. MANDT)
				StringBuffer uniqueId = new StringBuffer();
				boolean uniqueIdIsEmpty = true;
				
				// Add all reference table columns
				for (IColumn column: table.getColumns()) {
					String value = resultSet.getString(column.getName());
					
					// Construct the unique ID
					// The ID components will be sorted alphabetically by column name
					// because the table columns are stored in a TreeSet
					if (!column.getDomain().equalsIgnoreCase(Resources.MANDT_DOMAIN)) {
						if (uniqueId.length() != 0) {
							uniqueId.append(Constants.RDM_CODE_CONCAT_SEP);
						}
						
						uniqueId.append(value);
						
						if (value != null && !value.isEmpty() && !value.equals(Constants.ORACLE_EMPTY_STRING)) {
							uniqueIdIsEmpty = false;
						}
					}
					
					row.put(column.getName(), value);
				}
				
				if (!uniqueIdIsEmpty) {
					row.put(Constants.TABLE_DATA_UNIQUE_ID_TAG, uniqueId.toString());
				} else {
					// Replace empty values with a placeholder.
					row.put(Constants.TABLE_DATA_UNIQUE_ID_TAG, Constants.RDM_EMPTY_VALUE_ID);
				}
				
				// Add the description as a special "column", if any
				if (descriptionColumn != null && resultSet.getString(descriptionColumn) != null && !resultSet.getString(descriptionColumn).equals(Constants.ORACLE_EMPTY_STRING)) {
					row.put(Constants.TABLE_DATA_DESCRIPTION_TAG, resultSet.getString(descriptionColumn));
				}
				
				tableDataMapList.add(row);
			}
			break;
		case DOMAIN_TABLE:
			while (resultSet.next()) {
				row = new HashMap<String, String>();
				
				String uniqueId = resultSet.getString(Constants.DOMAIN_TABLE_COLUMNS[1]);
				if (uniqueId == null || uniqueId.isEmpty() || uniqueId.equals(Constants.ORACLE_EMPTY_STRING)) {
					uniqueId = Constants.RDM_EMPTY_VALUE_ID;
				}
				row.put(Constants.TABLE_DATA_UNIQUE_ID_TAG, uniqueId.toString());
				row.put(Constants.TABLE_DATA_DESCRIPTION_TAG, resultSet.getString(Constants.DOMAIN_TABLE_COLUMNS[2]));
				row.put(Constants.DOMAIN_TABLE_COLUMNS[1], uniqueId.toString());
				
				tableDataMapList.add(row);
			}
			break;
		}
		
		return tableDataMapList;
	}

	// Generate an SQL query that selects all the data we need for exporting
	private String generateSqlQuery(ReferenceTable table) {
		StringBuffer querySB = new StringBuffer();
		
		switch (table.getTableType()) {
		case CHECK_TABLE:
			String textTableJoinCondition = null;
			if (table.getTextTable() != null) {
				textTableJoinCondition = generateJoinCondition(table);
			}
			
			if (table.getTextTable() != null && textTableJoinCondition != null) {
				// We have a valid text table, join the reference data with the language-specific descriptions
				String refTableFullName = CwApp.getReferenceTableSchema() + "." + table.getName();
				String textTableFullName = CwApp.getReferenceTableSchema() + "." + table.getTextTable().getName();
				querySB.append("SELECT ");
		
				// Construct column list for selection: all reference
				// table columns plus the description from the text table
				int count = 0;
				for (ReferenceTableColumn c : table.getColumns()) {
					if (count > 0) {
						querySB.append(", ");
					}
					
					querySB.append(refTableFullName).append(".").append(c.getName());
					count++;
				}
				
				querySB.append(", ").append(SQL_LABEL_TEXTTABLE).append(".").append(findDescriptionColumn(table));
	
				// Add the table and text table with language selection
				querySB.append(" FROM ").append(refTableFullName);
				querySB.append(" LEFT OUTER JOIN ( SELECT * FROM ").append(textTableFullName);
				String languageColumn = findLanguageColumn(table);
				querySB.append(" WHERE ").append(textTableFullName).append(".").append(languageColumn).append(" = '").append(rdmLanguage).append("') ").append(SQL_LABEL_TEXTTABLE);
		
				// Add the join condition
				querySB.append(" ON ").append(textTableJoinCondition);
			} else {
				
				// No text table, just extract the reference table data
				querySB.append("SELECT * FROM ").append(CwApp.getReferenceTableSchema() + "." + table.getName());
			}
			break;
		case DOMAIN_TABLE:
			// fully extract the contents of the domain table
			querySB.append("SELECT * FROM ").append(CwApp.getReferenceTableSchema() + "." + table.getName());
			break;
		}
		
		logger.fine(querySB.toString());
		return querySB.toString();
	}

	/**
	 * Generates the WHERE condition that joins the reference table and text table.
	 * Uses the join condition loaded from the source data.
	 * 
	 * SPECIAL CASE: returns NULL if the text table contains additional key columns that are not part of the join condition.
 	 * Our strategy is not to provide descriptions from the text table in this case since we don't have a way to find the right ones.
	 */
	private String generateJoinCondition(ReferenceTable table) {
		int count = 0;
		StringBuffer result = new StringBuffer();
		String[] joinConditionArray = table.getTextTable().getJoinCondition().split(",");

		// Create a list of key column names to check whether all of them will be used in the join condition
		String languageColumn = findLanguageColumn(table);
		List<String> textTableKeyColumnNames = new ArrayList<String>();
		
		for (TextTableColumn column : table.getTextTable().getNonMandtColumns()) { // Skip client number column
			if (column.isKey() && !column.getName().equals(languageColumn)) { // Skip language column
				textTableKeyColumnNames.add(column.getName());
			}
		}
		
		for (String joinCondition : joinConditionArray) {
			String refTableColumnName = joinCondition.split("=")[0];
			// Skip client number columns
			if ("MANDT".equals(refTableColumnName) || "CLIENT".equals(refTableColumnName)) {
				continue;
			}
			String textTableColumnName = joinCondition.split("=")[1];
			if (count > 0) {
				result.append(" AND ");
			}
			result.append(CwApp.getReferenceTableSchema() + "." + table.getName()).append(".").append(refTableColumnName)
				.append(" = ").append(SQL_LABEL_TEXTTABLE).append(".").append(textTableColumnName);
			
			// Remove the processed column from the list of unprocessed key column names
			Iterator<String> columnNameIter = textTableKeyColumnNames.iterator();
			while(columnNameIter.hasNext()) {
				String columnName = columnNameIter.next();
				if (columnName.equals(textTableColumnName)) {
					columnNameIter.remove();
				}
			}
			
			count++;
		}
		
		// Check if we have unprocessed key columns left, log them and return an empty string.
		if (!textTableKeyColumnNames.isEmpty()) {
			for (String columnName : textTableKeyColumnNames) {
				logger.fine("Additional key column in text table: " + columnName + ". Cannot use this text table.");
			}
			return null;
		}
		
		return result.toString();
	}

	// Heuristic: From the reference table's text table columns, take the longest one as the description
	// Precondition: the table MUST have a text table!
	private String findDescriptionColumn(ReferenceTable table) {
		final String METHOD_NAME = "findDescriptionColumn(ReferenceTable table)"; 
		logger.entering(CLASS_NAME, METHOD_NAME);
		
		if (table.getTextTable() == null) {
			logger.severe("findDescriptionColumn: No text table for table " + CwApp.getReferenceTableSchema() + "." + table.getName());
			throw new CwAppException(HttpURLConnection.HTTP_INTERNAL_ERROR); 
		}
		
		int maxLength = 0;
		TextTableColumn longestColumn = null;
		for (TextTableColumn column : table.getTextTable().getColumns()) {
			logger.fine(column.getName() + ": " + column.getLength());
			if (column.getLength() > maxLength) {
				maxLength = column.getLength();
				longestColumn = column;
			} else if (column.getLength() == maxLength) {
				// found another candidate column of equal length
				// use the lexicographically first one to accomodate cases like LINE1, LINE2 etc.
				if (column.getName().compareTo(longestColumn.getName()) < 0) {
					longestColumn = column;
				}
			}
		}
		logger.fine("Description column: " + longestColumn.getName()); 
		
		logger.exiting(CLASS_NAME, METHOD_NAME);
		return longestColumn.getName();
	}

	// Use SAP field domain info to find the language column in the text table
	// Precondition: the table MUST have a text table!
	private String findLanguageColumn(ReferenceTable table) {
		if (table.getTextTable() == null) {
			logger.severe("findLanguageColumn: No text table for table " + CwApp.getReferenceTableSchema() + "." + table.getName());
			throw new CwAppException(HttpURLConnection.HTTP_INTERNAL_ERROR); 
		}
		
		String languageField = null;
		for (TextTableColumn column : table.getTextTable().getColumns()) {
			if ("SPRAS".equals(column.getDomain()) || "SYLANGU".equals(column.getDomain())) {
				if (column.getName().equals("SPRAS")) {
					languageField = column.getName();
					break; // SPRAS has precedence over others (e.g. in T002S)
				}
				languageField = column.getName();
			}
		}
		if (languageField == null) {
			logger.severe("Unable to find a language column in table " + table.getTextTable().getName());
			throw new CwAppException(HttpURLConnection.HTTP_INTERNAL_ERROR); 
		}
		logger.fine("Found language column: " + languageField); 
		return languageField;
	}

	private void publishNewRow(ReferenceTable table, int rowCount, int maximumProgressCount) {
		logger.fine("Publishing table row count for table " + table.getName() + " ...");
		JSONObject tableStatus = new JSONObject();

		try {
			tableStatus.put("number", tableProgress);
			tableStatus.put("name", table.getName());
			tableStatus.put("tableType", table.getTableType().toString());
			tableStatus.put("rowcount", rowCount);
			tableStatus.put("maxProgressCount", maximumProgressCount);
			publisher.publish(new BayeuxJmsTextMsg(Constants.COMETD_TOPIC_EXPORT_ROW_COUNT + sessionId, tableStatus.toString()));
		} catch (JSONException e) {
			Util.handleBatchException(e);
		} catch (PublisherException e) {
			Util.handleBatchException(e);
		}
	}

	private void publishOverallProgress(int status) {
		logger.fine("Publishing table status and overall progress...");
		JSONObject update = new JSONObject();

		try {
			update.put("number", tableProgress);
			update.put("status", status);
			publisher.publish(new BayeuxJmsTextMsg(Constants.COMETD_TOPIC_EXPORT_TABLE_STATUS + sessionId, update.toString()));
		} catch (JSONException e) {
			Util.handleBatchException(e);
		} catch (PublisherException e) {
			Util.handleBatchException(e);
		}
	}
}
