package com.ibm.is.sappack.cw.app.services.rdm.threads;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.NamingException;
import javax.servlet.http.HttpSession;
import javax.transaction.Status;

import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;

import com.ibm.is.sappack.cw.app.data.rdm.RdmSetVersionInfo;
import com.ibm.is.sappack.cw.app.data.rdm.SourceDataId;
import com.ibm.is.sappack.cw.app.data.rdm.jpa.RdmSet;
import com.ibm.is.sappack.cw.app.data.rdm.jpa.ReferenceTable;
import com.ibm.is.sappack.cw.app.data.rdm.jpa.ReferenceTableColumn;
import com.ibm.is.sappack.cw.app.data.rdm.jpa.SourceDataCollectionRule;
import com.ibm.is.sappack.cw.app.services.AbstractCancelableThread;
import com.ibm.is.sappack.cw.app.services.CWDBConnectionFactory;
import com.ibm.is.sappack.cw.app.services.Constants;
import com.ibm.is.sappack.cw.app.services.CwApp;
import com.ibm.is.sappack.cw.app.services.Util;
import com.ibm.is.sappack.cw.app.services.rdm.DBOperations;
import com.ibm.is.sappack.cw.app.services.rdm.clients.RdmSetClient;
import com.ibm.websphere.webmsg.publisher.Publisher;
import com.ibm.websphere.webmsg.publisher.PublisherException;
import com.ibm.websphere.webmsg.publisher.jndijms.BayeuxJmsTextMsg;

public class SourceReferenceDataExporterThread extends AbstractCancelableThread {

	private static final String CLASS_NAME = SourceReferenceDataExporterThread.class.getName();

	private final List<SourceDataId> inputList;
	private RdmSetClient rdmSetClient = null;

	private int tableProgress = 0;
	
	public SourceReferenceDataExporterThread(Publisher publisher, List<SourceDataId> inputList, RdmSetClient rdmSetClient, 
			HttpSession session) {
		super(session, publisher);
		this.inputList = inputList;
		this.rdmSetClient = rdmSetClient;
	}
	
	@Override
	public void run() {
		final String METHOD_NAME = "run()"; 
		logger.entering(CLASS_NAME, METHOD_NAME);

		// Loop over the list of reference tables and export the data to RDM
		for (SourceDataId id : inputList) {
			if (!cancelled) {
				tableProgress++;
				processTable(id);
			}
		}
		// Remove the reference to this thread from the session, so that the garbage collector can clean it up
		session.removeAttribute(Constants.SESSION_ATTRIBUTE_NAME_RDM_EXPORT_THREAD);
		logger.exiting(CLASS_NAME, METHOD_NAME);
	}

	private void processTable(SourceDataId sourceDataId) {
		final String METHOD_NAME = "processTable(int id)"; 
		logger.entering(CLASS_NAME, METHOD_NAME);
		logger.fine("Table id: " + sourceDataId.getReferenceTableId());
		logger.fine("SAP system id: " + sourceDataId.getLegacyId());
		
		ReferenceTable table = null;
		try {
			jpaTransaction.begin();
			manager.joinTransaction();
			table = manager.find(ReferenceTable.class, sourceDataId.getReferenceTableId());
			
			if (table != null) {
				logger.fine("Processing reference table: " + table.getName());
				// Finish jpaTransaction to allow Bayeux message to be sent
				jpaTransaction.commit();
				
				publishNewRow(sourceDataId, table);
				
				// Reopen jpaTransaction
				jpaTransaction.begin();
				manager.joinTransaction();
				table = manager.find(ReferenceTable.class, sourceDataId.getReferenceTableId());
				
				List<SourceDataCollectionRule> ruleList = DBOperations.getRulesForReferenceTable(sourceDataId.getReferenceTableId(), manager);
				
				// Retrieve all unique values from the data tables in CW DB
				Set<Map<String, String>> uniqueValueSet = new HashSet<Map<String, String>>();
				for (SourceDataCollectionRule rule : ruleList) {
					logger.fine("Processing rule for data table " + rule.getDataTableName() + ": " + rule.getCollectionRule());
					Set<Map<String, String>> tableValueSet = getTableDataFromCwdb(rule, table, sourceDataId.getLegacyId());
					logger.fine("Found " + tableValueSet.size() + " values.");
					uniqueValueSet.addAll(tableValueSet);
				}
				logger.fine("Total: " + uniqueValueSet.size() + " unique values.");
				
				// Finish jpaTransaction to allow Bayeux message to be sent
				jpaTransaction.commit();
				
				// Publish the row count to the client
				publishOverallProgress(Constants.EXPORT_STATUS_IN_PROGRESS, uniqueValueSet.size());
				
				// Reopen jpaTransaction
				jpaTransaction.begin();
				manager.joinTransaction();
				table = manager.find(ReferenceTable.class, sourceDataId.getReferenceTableId());
				
				// Create or find the RDM data type
				String rdmTypeId = rdmSetClient.createType(table);
				
				// Create the RDM set or just a new version if one exists already
				RdmSet rdmSet = table.getSourceRdmSetForLegacyId(sourceDataId.getLegacyId());
				boolean rdmSetEntityIsNew = false;
				boolean needToExport = true;
				boolean noChanges = false;
				if (rdmSet == null) {
					rdmSetEntityIsNew = true;
					logger.fine("Found no source RDM set entity for this legacy ID, creating new one.");
					rdmSet = new RdmSet();
					rdmSet.setName(Util.generateSourceRdmSetName(table, sourceDataId.getLegacyId()));
					rdmSet.setCreated(new Date());
				}
				
				// Check if a set exists in RDM (we don't care if it's *supposed* to be there according to our records,
				// we will recreate it if it went missing for some reason)
				RdmSetVersionInfo rdmSetVersionInfo = rdmSetClient.getSetByName(rdmSet.getName());
				
				if (rdmSet.getRdmId() != null && (rdmSetVersionInfo == null || !rdmSet.getRdmId().equals(rdmSetVersionInfo.getBaseId()))) {
					// We have an orphan JPA entity without a set present in RDM (or the RDM set has a different ID).
					// Replace the JPA entity.
					manager.remove(rdmSet);
					rdmSetEntityIsNew = true;
					rdmSet = new RdmSet();
					rdmSet.setName(Util.generateSourceRdmSetName(table, sourceDataId.getLegacyId()));
					rdmSet.setCreated(new Date());
				}
				
				jpaTransaction.commit();
				
				if (rdmSetVersionInfo == null) {
					// There is no set in RDM.
					// Create the set and the entity.
					rdmSet.setRdmId(rdmSetClient.createSourceSet(table, rdmTypeId, sourceDataId.getLegacyId()));
					rdmSet.setVersion("1");
					rdmSet.setVersionId(rdmSet.getRdmId()); // The version ID is the same as the base ID for version 1
				} else {
					// The set exists in RDM.
					boolean rdmSetIsUpToDate = rdmSetClient.isSetUpToDate(rdmSetVersionInfo.getVersionId(), uniqueValueSet, publisher, sessionId);

					if (rdmSetEntityIsNew && rdmSetIsUpToDate) {
						// The set in RDM already contains the correct values, but out record is incorrect.
						// Update our record according to RDM but don't export.
						rdmSet.setRdmId(rdmSetVersionInfo.getBaseId());
						rdmSet.setVersion(rdmSetVersionInfo.getVersionNumber());
						rdmSet.setVersionId(rdmSetVersionInfo.getVersionId());
						needToExport = false;
						rdmSet.setCreated(new Date());
						
					} else if (!rdmSetEntityIsNew && rdmSetIsUpToDate) {
						// The set in RDM already contains the correct values.
						// Update our record according to RDM (version info only) but don't export.
						rdmSet.setVersion(rdmSetVersionInfo.getVersionNumber());
						rdmSet.setVersionId(rdmSetVersionInfo.getVersionId());
						needToExport = false;
						noChanges = true;
						
					} else if (rdmSetEntityIsNew && !rdmSetIsUpToDate) {
						// The set in RDM is outdated and our record was incorrect.
						// Update our record according to RDM and create a new set version in RDM.
						rdmSet.setRdmId(rdmSetVersionInfo.getBaseId());
						rdmSet.setVersion(String.valueOf(Integer.parseInt(rdmSetVersionInfo.getVersionNumber()) + 1));
						rdmSet.setVersionId(rdmSetClient.createSetVersion(rdmSetVersionInfo, rdmSet.getName(), rdmTypeId));
	
					} else if (!rdmSetEntityIsNew && !rdmSetIsUpToDate) {
						// The set in RDM is outdated. 
						// Create a new set version in RDM.
						rdmSet.setVersion(String.valueOf(Integer.parseInt(rdmSetVersionInfo.getVersionNumber()) + 1));
						rdmSet.setVersionId(rdmSetClient.createSetVersion(rdmSetVersionInfo, rdmSet.getName(), rdmTypeId));
					}
				}
				
				if (needToExport) {
					if (jpaTransaction.getStatus() == Status.STATUS_ACTIVE) {
						// Finish jpaTransaction to allow progress updates to be sent during the data upload
						jpaTransaction.commit();
					}
					// Load the actual data into the RDM data set
					rdmSetClient.insertIntoSet(rdmSet.getVersionId(), uniqueValueSet, publisher, sessionId, 0);
				}
				// Update the table metadata with info on the new RDM set
				jpaTransaction.begin();
				manager.joinTransaction();
				
				table = manager.find(ReferenceTable.class, sourceDataId.getReferenceTableId());
				if (table == null) {
					// This can happen if the table gets deleted in between.
					publishOverallProgress(Constants.EXPORT_STATUS_TABLE_NOT_FOUND, -1);
					jpaTransaction.commit();
					return;
				}
				rdmSet.setReferenceTable(table);
				rdmSet.setLegacyId(sourceDataId.getLegacyId());
				rdmSet.setUptodate(true);
				manager.persist(table);
				manager.merge(rdmSet);
				// Publish the table status to the client
				if (noChanges){
					// The values in RDM match those in CW DB
					publishOverallProgress(Constants.EXPORT_STATUS_SET_NOT_CHANGED, -1);
				} else {
					publishOverallProgress(Constants.EXPORT_STATUS_OK, -1);
				}
			} else {
				// This can happen if the table gets deleted in between.
				logger.fine("Table not found.");
				publishOverallProgress(Constants.EXPORT_STATUS_TABLE_NOT_FOUND, -1);
			}
			jpaTransaction.commit();
		} catch (Exception e) {
			Util.handleBatchException(jpaTransaction, e);
			// Publish the table status to the client
			publishOverallProgress(Constants.EXPORT_STATUS_INTERNAL_ERROR, -1);
		}

		logger.exiting(CLASS_NAME, METHOD_NAME);
	}
	
	private Set<Map<String, String>> getTableDataFromCwdb(SourceDataCollectionRule rule, ReferenceTable referenceTable, String legacyId) throws NamingException, SQLException {
		final String METHOD_NAME = "getTableDataFromCwdb(SourceDataCollectionRule rule, ReferenceTable referenceTable, String legacyId)"; 
		logger.entering(CLASS_NAME, METHOD_NAME);
		
		Connection connection = null;
		Statement stmt = null;
		ResultSet rs = null;
		Set<Map<String, String>> tableDataMapSet = null;

		try {
			connection = CWDBConnectionFactory.getConnection();
			String query = generateSqlQuery(rule, legacyId);
			stmt = connection.createStatement();
	
			// Execute the query
			rs = stmt.executeQuery(query);
			tableDataMapSet = processQueryResultSet(rule, referenceTable, rs);
		}
		finally {
			Util.closeDBObjects(rs, stmt, connection);
		}
		
		logger.exiting(CLASS_NAME, METHOD_NAME);
		return tableDataMapSet;
	}
	
	// Generate an SQL query that selects all the data we need for exporting
	private String generateSqlQuery(SourceDataCollectionRule rule, String legacyId) {
		final String METHOD_NAME = "generateSqlQuery(SourceDataCollectionRule rule)"; 
		logger.entering(CLASS_NAME, METHOD_NAME);
		StringBuffer querySB = new StringBuffer();

		querySB.append("SELECT DISTINCT ");

		// Construct column list for selection
		String[] columnMappingArray = rule.getCollectionRule().split(",");
		int count = 0;
		for (String columnMapping : columnMappingArray) {
			String dataTableColumnName = columnMapping.split("=")[0];
			if (dataTableColumnName.length() > 0) {
				if (!collectionRuleValidityCheck(dataTableColumnName)) {
					// Invalid or unsupported condition
					continue;
				}
				if (dataTableColumnName.substring(0,1).equals("'")) { // Skip explicit values
					continue;
				}
				if (count > 0) {
					querySB.append(", ");
				}
				querySB.append(CwApp.getDataTableSchema()).append(".").append(rule.getDataTableName()).append(".").append(dataTableColumnName);
				count++;
			}
		}

		querySB.append(" FROM ").append(CwApp.getDataTableSchema()).append(".").append(rule.getDataTableName());
		
		// Filter data by legacy ID
		querySB.append(" WHERE ").append(Constants.CW_COLUMN_LEGACY_ID).append(" LIKE '").append(legacyId).append("'");
		
		logger.fine(querySB.toString());
		
		logger.exiting(CLASS_NAME, METHOD_NAME);
		return querySB.toString();
	}

	// Iterates over the result set from the CW DB
	// and generates a set of rows represented by maps between column names and values for sending to RDM Hub
	private Set<Map<String, String>> processQueryResultSet(SourceDataCollectionRule rule, ReferenceTable referenceTable, ResultSet resultSet)
			throws SQLException {
		final String METHOD_NAME = "processQueryResultSet(SourceDataCollectionRule rule, ReferenceTable referenceTable, ResultSet resultSet)"; 
		logger.entering(CLASS_NAME, METHOD_NAME);
		
		Set<Map<String, String>> tableDataMapSet;
		tableDataMapSet = new HashSet<Map<String, String>>();
		Map<String, String> row = null;
		
		// Use the list of client columns in order to identify and skip them when generating the unique ID 
		Collection<ReferenceTableColumn> mandtColumns = referenceTable.getMandtColumns();

		while (resultSet.next()) {
			logger.finer("Processing a row...");
			row = new HashMap<String, String>();
			
			// The unique ID concatenating all column values except the client column(s) (e.g. MANDT)
			StringBuffer uniqueId = new StringBuffer();
			
			// Add the data from the data table columns as values for the reference table columns
			String[] columnConditionArray = rule.getCollectionRule().split(",");
			boolean foundEmptyValues = false;
			for (String columnCondition : columnConditionArray) {
				String dataTableColumnName = columnCondition.split("=")[0];
				if (!collectionRuleValidityCheck(dataTableColumnName)) {
					// Invalid or unsupported condition
					logger.finest("Skipping unsupported join condition: " + columnCondition);
					continue;
				}
				String value = null;
				
				// Add the name/value pair to the data set
				if (dataTableColumnName.substring(0,1).equals("'")) {
					// Explicit value given in the rule, use it directly
					value = dataTableColumnName.substring(1, dataTableColumnName.length() - 1);
				} else {
					// Regular case: use the value from the data table
					value = resultSet.getString(dataTableColumnName);
				}
				String referenceTableColumnName = columnCondition.split("=")[1];
				row.put(referenceTableColumnName, value);

				if (value == null || value.isEmpty() || value.trim().isEmpty()) {
					foundEmptyValues = true;
					logger.finest("Found empty column value in column " + referenceTableColumnName);
				}
				
				// Construct the unique ID, skipping any client columns
				// The ID components will be sorted alphabetically by column name
				// because the source data collection rule conditions are sorted when the rule is created
				boolean columnIsMandt = false;
				for (ReferenceTableColumn column : mandtColumns) {
					if (column.getName().equals(referenceTableColumnName)) {
						columnIsMandt = true;
					}
				}
				// If non-client column, use it in the unique ID
				if (!columnIsMandt) {
					if (uniqueId.length() != 0) {
						uniqueId.append(Constants.RDM_CODE_CONCAT_SEP);
					}
					uniqueId.append(value);
				}
			}
			
			row.put(Constants.TABLE_DATA_UNIQUE_ID_TAG, uniqueId.toString());
			
			if (!foundEmptyValues) { // Ignore rows with empty values. TODO: any cases where we need them?
				logger.finest("Row is valid: " + uniqueId.toString());
				tableDataMapSet.add(row);
			} else {
				logger.finest("Ignoring row with empty column values: " + uniqueId.toString());
			}
		}
		
		logger.exiting(CLASS_NAME, METHOD_NAME);
		return tableDataMapSet;
	}
	
	// Checks if a data collection rule clause is valid and supported
	// Input: the clause's left side, typically the data table column name, e.g. 'CLIENT' from 'CLIENT=MANDT' 
	private boolean collectionRuleValidityCheck(String dataTableColumnName) {
		if (dataTableColumnName.contains(".")) {
			// Exception: clauses involving other tables. Should be filtered out by the import code anyway. 
			return false;
		}
		if (dataTableColumnName.contains("*")) {
			// Exception: clauses with wildcards
			return false;
		}
		return true;
	}
	
	private void publishNewRow(SourceDataId id, ReferenceTable table) {
		JSONObject tableStatus = new JSONObject();

		try {
			tableStatus.put("number", tableProgress);
			tableStatus.put("legacyId", id.getLegacyIdDescription());
			tableStatus.put("name", table.getName());
			tableStatus.put("tableType", table.getTableType().toString());
			publisher.publish(new BayeuxJmsTextMsg(Constants.COMETD_TOPIC_EXPORT_ROW_COUNT + sessionId, tableStatus.toString()));
		} catch (JSONException e) {
			Util.handleBatchException(e);
		} catch (PublisherException e) {
			Util.handleBatchException(e);
		}
	}

	private void publishOverallProgress(int status, int rowCount) {
		logger.fine("Publishing table status and overall progress...");
		JSONObject update = new JSONObject();

		try {
			update.put("number", tableProgress);
			update.put("status", status);
			update.put("rowcount", rowCount);
			publisher.publish(new BayeuxJmsTextMsg(Constants.COMETD_TOPIC_EXPORT_TABLE_STATUS + sessionId, update.toString()));
		} catch (JSONException e) {
			Util.handleBatchException(e);
		} catch (PublisherException e) {
			Util.handleBatchException(e);
		}
	}
}
