package com.ibm.is.sappack.cw.app.services.rdm;

import java.net.HttpURLConnection;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import com.ibm.is.sappack.cw.app.data.rdm.ITable;
import com.ibm.is.sappack.cw.app.data.rdm.ReferenceTableType;
import com.ibm.is.sappack.cw.app.data.rdm.jpa.RdmMapping;
import com.ibm.is.sappack.cw.app.data.rdm.jpa.ReferenceTable;
import com.ibm.is.sappack.cw.app.data.rdm.jpa.ReferenceTableColumn;
import com.ibm.is.sappack.cw.app.data.rdm.jpa.SourceDataCollectionRule;
import com.ibm.is.sappack.cw.app.data.rdm.jpa.TextTable;
import com.ibm.is.sappack.cw.app.data.rdm.jpa.TextTableColumn;
import com.ibm.is.sappack.cw.app.data.rdm.jpa.TranscodingTable;
import com.ibm.is.sappack.cw.app.services.CWDBConnectionFactory;
import com.ibm.is.sappack.cw.app.services.Constants;
import com.ibm.is.sappack.cw.app.services.CwApp;
import com.ibm.is.sappack.cw.app.services.CwAppException;
import com.ibm.is.sappack.cw.app.services.Util;

public class DBOperations {

	private static final String CLASS_NAME = DBOperations.class.getName();
	private static Logger logger = CwApp.getLogger();
	
	// Works for reference and text tables
	public static boolean referenceTableExistsInCWDB(String name) {
		return tableExistsInCWDB(CwApp.getReferenceTableSchema(), name);
	}
	
	public static boolean transcodingTableExistsInCWDB(String name) {
		return tableExistsInCWDB(CwApp.getTranscodingTableSchema(), name);
	}

	public static boolean tableExistsInCWDB(String schema, String tableName) {
		boolean result = false;
		ResultSet rs = null;
		Connection conn = null;
		
		try {
			conn = CWDBConnectionFactory.getConnection();
			DatabaseMetaData meta = conn.getMetaData();
			rs = meta.getTables(null, schema, tableName, null);
			result = rs.next();
		}
		catch (NamingException e) {
			Util.throwInternalErrorToClient(e);
		}
		catch (SQLException e) {
			Util.throwInternalErrorToClient(e);
		}
		finally {
			Util.closeDBObjects(rs, conn);
		}

		return result;
	}

	// finds the ReferenceTable that contains the ID of a rmdSet
	// result is unique because of a one to one relationship between ReferenceTable and RmdSet
	public static ReferenceTable findReferenceTableForRDMSet(String rdmSetId, EntityManager mgr) {
		TypedQuery<ReferenceTable> query = mgr.createNamedQuery("ReferenceTable.retrieveByRDMId", ReferenceTable.class);
		query.setParameter("id", rdmSetId);
		List<ReferenceTable> tableList = query.getResultList();
		
		if (!tableList.isEmpty()) {
			return tableList.get(0);
		}
		
		// No reference table found for the set, probably it's not ours
		logger.fine("No reference table entity found for RDM set id: " + rdmSetId);
		return null;
	}

	// returns all Mappings that are in a relationship with the specified TranscodingTable
	public static List<RdmMapping> getAllMappingsForTT(String ttName, EntityManager manager) {
		TypedQuery<RdmMapping> query = manager.createNamedQuery("RdmMapping.retrieveAllMappingsForTT", RdmMapping.class);
		query.setParameter("ttname", ttName);
		return query.getResultList();
	}

	// Returns all source data collection rules for the given reference table
	public static List<SourceDataCollectionRule> getRulesForReferenceTable(int tableId, EntityManager manager) throws SecurityException, IllegalStateException {
		TypedQuery<SourceDataCollectionRule> query = manager.createNamedQuery("SourceDataCollectionRule.retrieveByReferenceTable", SourceDataCollectionRule.class);
		query.setParameter("id", tableId);
		List<SourceDataCollectionRule> result = query.getResultList();
		return result;
	}
	
	// Returns the existing values of ROLLOUT from the CW AUX.SAP_CHECKTABLES_METADATA table for the given SAP system legacy ID.
	// The result is a list of string values.
	public static List<String> getRolloutValuesFromCWDB(String legacyId) {
		final String METHOD_NAME = "getRolloutValuesFromCWDB(String sapSystemId)"; 
		logger.entering(CLASS_NAME, METHOD_NAME);
		
		Connection conn = null;
		PreparedStatement retrieveRolloutValues = null;
		ResultSet rs = null;
		List<String> result = new ArrayList<String>();

		try {
			conn = CWDBConnectionFactory.getConnection();
			
			String query = "SELECT DISTINCT "
					+ Constants.CW_COLUMN_ROLLOUT + " FROM "
					+ Constants.CW_TABLE_CHECK_TABLES_METADATA + " WHERE "
					+ Constants.CW_COLUMN_LEGACY_ID + "= ?" + " ORDER BY "
					+ Constants.CW_COLUMN_ROLLOUT + " ASC";
			
			logger.fine(query);
			
			retrieveRolloutValues = conn.prepareStatement(query);
			retrieveRolloutValues.setString(1, legacyId);
			logger.fine(Constants.CW_COLUMN_LEGACY_ID + ": " + legacyId);
			rs = retrieveRolloutValues.executeQuery();
			
			while (rs.next()) {
				String value = rs.getString(Constants.CW_COLUMN_ROLLOUT);
				result.add(value);
			}
		}
		catch (SQLException e) {
			Util.throwInternalErrorToClient(e);
		}
		catch (NamingException e) {
			Util.throwInternalErrorToClient(e);
		}
		finally {
			Util.closeDBObjects(rs, retrieveRolloutValues, conn);
		}

		logger.exiting(CLASS_NAME, METHOD_NAME);
		return result;
	}
	
	// Returns the existing values of LOB from the CW AUX.SAP_CHECKTABLES_METADATA table for the given SAP system legacy ID and rollout.
	// The result is a list of string values.
	public static List<String> getLobValuesFromCWDB(String legacyId, String rollout) {
		final String METHOD_NAME = "getLobValuesFromCWDB(String legacyId, String rollout)"; 
		logger.entering(CLASS_NAME, METHOD_NAME);
		
		Connection conn = null;
		PreparedStatement retrieveLobValues = null;
		ResultSet rs = null;
		List<String> result = new ArrayList<String>();

		try {
			conn = CWDBConnectionFactory.getConnection();
			
			String query = "SELECT DISTINCT "
					+ Constants.CW_COLUMN_LOB + " FROM "
					+ Constants.CW_TABLE_CHECK_TABLES_METADATA + " WHERE "
					+ Constants.CW_COLUMN_LEGACY_ID + "= ?" + " AND "
					+ Constants.CW_COLUMN_ROLLOUT + "= ?" + " ORDER BY "
					+ Constants.CW_COLUMN_LOB + " ASC";
			
			logger.fine(query);
			
			retrieveLobValues = conn.prepareStatement(query);
			retrieveLobValues.setString(1, legacyId);
			logger.fine(Constants.CW_COLUMN_LEGACY_ID + ": " + legacyId);
			retrieveLobValues.setString(2, rollout);
			logger.fine(Constants.CW_COLUMN_ROLLOUT + ": " + rollout);
			
			rs = retrieveLobValues.executeQuery();
			
			while (rs.next()) {
				String value = rs.getString(Constants.CW_COLUMN_LOB);
				result.add(value);
			}
		}
		catch (SQLException e) {
			Util.throwInternalErrorToClient(e);
		}
		catch (NamingException e) {
			Util.throwInternalErrorToClient(e);
		}
		finally {
			Util.closeDBObjects(rs, retrieveLobValues, conn);
		}

		logger.exiting(CLASS_NAME, METHOD_NAME);
		return result;
	}
	
	// Returns a shallow "preview" list of the check tables from the CW AUX.SAP_CHECKTABLES_METADATA table
	// for the given SAP system legacy ID, rollout and business object.
	public static HashMap<String, ReferenceTable> getCheckTablePreviewFromCWDB(String legacyId, String rollout, String bo) {
		final String METHOD_NAME = "getCheckTablePreviewFromCWDB(String legacyId, String rollout, String bo)"; 
		logger.entering(CLASS_NAME, METHOD_NAME);
		
		Connection conn = null;
		PreparedStatement retrieveCheckTablePreview = null;
		ResultSet rs = null;
		HashMap<String, ReferenceTable> result = new HashMap<String, ReferenceTable>();

		try {
			conn = CWDBConnectionFactory.getConnection();
			
			String query = "SELECT DISTINCT "
					+ Constants.CW_COLUMN_CT_CHECK_TABLE + ", "
					+ Constants.CW_COLUMN_DESCRIPTION + ", "
					+ Constants.CW_COLUMN_CW_TEXT_TABLE	+ ", "
					+ Constants.CW_COLUMN_TRANSCODING_TABLE	+ " FROM "
					+ Constants.CW_TABLE_CHECK_TABLES_METADATA + " WHERE "
					+ Constants.CW_COLUMN_LEGACY_ID + "= ?" + " AND "  
					+ Constants.CW_COLUMN_ROLLOUT + "= ?";
			
			if (!bo.equals(Constants.CW_LOB_WILDCARD)) {
				query += " AND " + Constants.CW_COLUMN_LOB + "= ?";
			}
			
			query += " AND " + Constants.CW_COLUMN_TABLE_TYPE + "='"
					+ Constants.CW_TABLE_TYPE_VALUE + "'" + " AND "			
					// Add scoping condition: only load check tables referenced by active data table fields in the exported BDR
					+ Constants.CW_COLUMN_SAP_CHECK_TABLE
					+ " IN (SELECT DISTINCT " + Constants.CW_COLUMN_SAP_CHECK_TABLE
					+ " FROM " + Constants.CW_TABLE_FIELDS_CONFIG
					+ " LEFT OUTER JOIN (SELECT * FROM " + Constants.CW_TABLE_FIELDS_METADATA + ") FMD ON "
					+ Constants.CW_TABLE_FIELDS_CONFIG + "." + Constants.CW_COLUMN_SAP_TABLENAME + " = FMD." + Constants.CW_COLUMN_SAP_TABLENAME
					+ " AND "
					+ Constants.CW_TABLE_FIELDS_CONFIG + "." + Constants.CW_COLUMN_SAP_FIELDNAME + " = FMD." + Constants.CW_COLUMN_SAP_FIELDNAME
					+ " WHERE " + Constants.CW_TABLE_FIELDS_CONFIG + "." + Constants.CW_COLUMN_ACTIVE + " = '" + Constants.CW_TRUE_VALUE + "'"
					+ " AND " + Constants.CW_TABLE_FIELDS_CONFIG + "." + Constants.CW_COLUMN_ROLLOUT + " = ?)"
					+ " ORDER BY " + Constants.CW_COLUMN_CT_CHECK_TABLE + " ASC";
			
			logger.fine(query);
			
			retrieveCheckTablePreview = conn.prepareStatement(query);
			retrieveCheckTablePreview.setString(1, legacyId);
			logger.fine(Constants.CW_COLUMN_LEGACY_ID + ": " + legacyId);
			retrieveCheckTablePreview.setString(2, rollout);
			logger.fine(Constants.CW_COLUMN_ROLLOUT + ": " + rollout);
			
			if (!bo.equals(Constants.CW_LOB_WILDCARD)) {
				retrieveCheckTablePreview.setString(3, bo);
				logger.fine(Constants.CW_COLUMN_LOB + ": " + bo);
				retrieveCheckTablePreview.setString(4, rollout);
			} else {
				retrieveCheckTablePreview.setString(3, rollout);
			}
			
			rs = retrieveCheckTablePreview.executeQuery();
			
			while (rs.next()) {
				
				// Create a reference table
				ReferenceTable referenceTable = new ReferenceTable();
				referenceTable.setName(rs.getString(Constants.CW_COLUMN_CT_CHECK_TABLE));
				referenceTable.setDescription(rs.getString(Constants.CW_COLUMN_DESCRIPTION));
				referenceTable.setTableType(ReferenceTableType.CHECK_TABLE);
				
				// Add the transcoding table
				TranscodingTable transcodingTable = new TranscodingTable(rs.getString(Constants.CW_COLUMN_TRANSCODING_TABLE));
				referenceTable.setTranscodingTable(transcodingTable);
				
				// Add the text table, if any
				String textTableName = rs.getString(Constants.CW_COLUMN_CW_TEXT_TABLE);
				if (textTableName != null && !textTableName.isEmpty()) { 
					TextTable textTable = new TextTable();
					textTable.setName(textTableName);
					referenceTable.setTextTable(textTable);
				}

				result.put(referenceTable.getName(), referenceTable);
			}
		}
		catch (SQLException e) {
			Util.throwInternalErrorToClient(e);
		}
		catch (NamingException e) {
			Util.throwInternalErrorToClient(e);
		}
		finally {
			Util.closeDBObjects(rs, retrieveCheckTablePreview, conn);
		}

		logger.exiting(CLASS_NAME, METHOD_NAME);
		return result;
	}
	
	private static List<ReferenceTableColumn> getReferenceTableColumnsFromCWDB(ReferenceTable referenceTable, String legacyId, String rollout) {
		final String METHOD_NAME = "getReferenceTableColumnsFromCWDB(ReferenceTable referenceTable, String legacyId, String rollout)"; 
		logger.entering(CLASS_NAME, METHOD_NAME);
		
		Connection conn = null;
		PreparedStatement retrieveReftableColumns = null;
		ResultSet rs = null;
		List<ReferenceTableColumn> result = new ArrayList<ReferenceTableColumn>();
		
		try {
			conn = CWDBConnectionFactory.getConnection();
			
			String query = "SELECT DISTINCT "
					+ Constants.CW_COLUMN_SAP_FIELDNAME	+ ", "
					+ Constants.CW_COLUMN_CW_FIELDNAME + ", "
					+ Constants.CW_COLUMN_TRANSCODING_TABLE_SOURCE_FIELD + ", "
					+ Constants.CW_COLUMN_TRANSCODING_TABLE_TARGET_FIELD + ", "
					+ Constants.CW_COLUMN_LENGTH + ", "
					+ Constants.CW_COLUMN_DOMAIN_NAME + " FROM "
					+ Constants.CW_TABLE_FIELDS_METADATA + " WHERE "
					+ Constants.CW_COLUMN_LEGACY_ID + "= ?" + " AND "  
					+ Constants.CW_COLUMN_ROLLOUT + "= ?" + " AND "
					+ Constants.CW_COLUMN_SAP_TABLENAME + "= ?" + " ORDER BY "
					+ Constants.CW_COLUMN_SAP_FIELDNAME + " ASC";
			
			logger.fine(query);
			
			retrieveReftableColumns = conn.prepareStatement(query);
			retrieveReftableColumns.setString(1, legacyId);
			logger.fine(Constants.CW_COLUMN_LEGACY_ID + ": " + legacyId);
			retrieveReftableColumns.setString(2, rollout);
			logger.fine(Constants.CW_COLUMN_ROLLOUT + ": " + rollout);
			retrieveReftableColumns.setString(3, referenceTable.getSapName());
			logger.fine(Constants.CW_COLUMN_SAP_TABLENAME + ": " + referenceTable.getSapName());
			rs = retrieveReftableColumns.executeQuery();
			
			while (rs.next()) {
				ReferenceTableColumn column = new ReferenceTableColumn();
				column.setReferenceTable(referenceTable);
				column.setName(rs.getString(Constants.CW_COLUMN_CW_FIELDNAME));
				column.setSapName(rs.getString(Constants.CW_COLUMN_SAP_FIELDNAME));
				column.setDomain(rs.getString(Constants.CW_COLUMN_DOMAIN_NAME));
				column.setTranscodingTableSrcName(rs.getString(Constants.CW_COLUMN_TRANSCODING_TABLE_SOURCE_FIELD));
				column.setTranscodingTableTgtName(rs.getString(Constants.CW_COLUMN_TRANSCODING_TABLE_TARGET_FIELD));
				column.setLength(Integer.parseInt(rs.getString(Constants.CW_COLUMN_LENGTH)));
				
				result.add(column);
			}
		}
		catch (SQLException e) {
			Util.throwInternalErrorToClient(e);
		}
		catch (NamingException e) {
			Util.throwInternalErrorToClient(e);
		}
		finally {
			// Don't close the connection, caller will close it
			Util.closeDBObjects(rs, retrieveReftableColumns);
		}

		logger.exiting(CLASS_NAME, METHOD_NAME);
		return result;
	}
	
	private static List<TextTableColumn> getTextTableColumnsFromCWDB(TextTable textTable, String legacyId, String rollout) {
		final String METHOD_NAME = "getTextTableColumnsFromCWDB(TextTable textTable, String legacyId, String rollout)"; 
		logger.entering(CLASS_NAME, METHOD_NAME);
		
		Connection conn = null;
		PreparedStatement retrieveTexttableColumns = null;
		ResultSet rs = null;
		List<TextTableColumn> result = new ArrayList<TextTableColumn>();
		
		try {
			conn = CWDBConnectionFactory.getConnection();
			
			String query = "SELECT DISTINCT "
					+ Constants.CW_COLUMN_SAP_FIELDNAME + ", "
					+ Constants.CW_COLUMN_CW_FIELDNAME	+ ", "
					+ Constants.CW_COLUMN_LENGTH + ", "
					+ Constants.CW_COLUMN_KEY_FLAG + ", "
					+ Constants.CW_COLUMN_DOMAIN_NAME + " FROM "
					+ Constants.CW_TABLE_FIELDS_METADATA + " WHERE "
					+ Constants.CW_COLUMN_LEGACY_ID + "= ?" + " AND "  
					+ Constants.CW_COLUMN_ROLLOUT + "= ?" + " AND "
					+ Constants.CW_COLUMN_SAP_TABLENAME + "= ?" + " ORDER BY "
					+ Constants.CW_COLUMN_SAP_FIELDNAME + " ASC";
			
			logger.fine(query);
			
			retrieveTexttableColumns = conn.prepareStatement(query);
			retrieveTexttableColumns.setString(1, legacyId);
			logger.fine(Constants.CW_COLUMN_LEGACY_ID + ": " + legacyId);
			retrieveTexttableColumns.setString(2, rollout);
			logger.fine(Constants.CW_COLUMN_ROLLOUT + ": " + rollout);
			retrieveTexttableColumns.setString(3, textTable.getSapName());
			logger.fine(Constants.CW_COLUMN_SAP_TABLENAME + ": " + textTable.getSapName());
			rs = retrieveTexttableColumns.executeQuery();
			
			while (rs.next()) {
				TextTableColumn column = new TextTableColumn();
				column.setTextTable(textTable);
				column.setName(rs.getString(Constants.CW_COLUMN_CW_FIELDNAME));
				column.setSapName(rs.getString(Constants.CW_COLUMN_SAP_FIELDNAME));
				column.setDomain(rs.getString(Constants.CW_COLUMN_DOMAIN_NAME));
				column.setLength(Integer.parseInt(rs.getString(Constants.CW_COLUMN_LENGTH)));
				column.setKey(Constants.CW_TRUE_VALUE.equals(rs.getString(Constants.CW_COLUMN_KEY_FLAG)));
				
				result.add(column);
			}
		}
		catch (SQLException e) {
			Util.throwInternalErrorToClient(e);
		}
		catch (NamingException e) {
			Util.throwInternalErrorToClient(e);
		}
		finally {

			// Don't close the connection, caller will close it
			Util.closeDBObjects(rs, retrieveTexttableColumns);
		}

		logger.exiting(CLASS_NAME, METHOD_NAME);
		return result;
	}
	
	// Retrieves the text table join condition from the foreign keys table in the CW DB.
	private static String getTextTableJoinConditionFromCWDB(ReferenceTable referenceTable, String legacyId, String rollout) {
		final String METHOD_NAME = "getTextTableJoinConditionFromCWDB(ReferenceTable referenceTable, String legacyId, String rollout)"; 
		logger.entering(CLASS_NAME, METHOD_NAME);
		
		Connection conn = null;
		PreparedStatement retrieveTexttableJoinCondition = null;
		ResultSet rs = null;
		StringBuffer joinCondition = new StringBuffer();
		
		try {
			conn = CWDBConnectionFactory.getConnection();
			
			String query = "SELECT DISTINCT "
					+ Constants.CW_COLUMN_CHECK_FIELD + ", "
					+ Constants.CW_COLUMN_FOREIGN_KEY + ", "
					+ Constants.CW_COLUMN_PRIMPOS + " FROM "
					+ Constants.CW_TABLE_FOREIGN_KEYS + " WHERE "
					+ Constants.CW_COLUMN_LEGACY_ID + "= ?" + " AND "  
					+ Constants.CW_COLUMN_ROLLOUT + "= ?" + " AND "
					+ Constants.CW_COLUMN_SAP_CHECK_TABLE + "= ?" + " AND "
					+ Constants.CW_COLUMN_SAP_TABLENAME + "= ?" + " ORDER BY "
					+ Constants.CW_COLUMN_PRIMPOS + " ASC";
			
			logger.fine(query);
			
			retrieveTexttableJoinCondition = conn.prepareStatement(query);
			retrieveTexttableJoinCondition.setString(1, legacyId);
			logger.fine(Constants.CW_COLUMN_LEGACY_ID + ": " + legacyId);
			retrieveTexttableJoinCondition.setString(2, rollout);
			logger.fine(Constants.CW_COLUMN_ROLLOUT + ": " + rollout);
			retrieveTexttableJoinCondition.setString(3, referenceTable.getSapName());
			logger.fine(Constants.CW_COLUMN_SAP_CHECK_TABLE + ": " + referenceTable.getSapName());
			retrieveTexttableJoinCondition.setString(4, referenceTable.getTextTable().getSapName());
			logger.fine(Constants.CW_COLUMN_SAP_TABLENAME + ": " + referenceTable.getTextTable().getSapName());
			rs = retrieveTexttableJoinCondition.executeQuery();

			while (rs.next()) {
				// Extend text table join condition
				if (!joinCondition.toString().isEmpty()) {
					joinCondition.append(',');
				}
				
				joinCondition.append(rs.getString(Constants.CW_COLUMN_CHECK_FIELD));
				joinCondition.append('=');
				joinCondition.append(rs.getString(Constants.CW_COLUMN_FOREIGN_KEY));
			}
		}
		catch (SQLException e) {
			Util.throwInternalErrorToClient(e);
		}
		catch (NamingException e) {
			Util.throwInternalErrorToClient(e);
		}
		finally {
			// Don't close the connection, caller will close it
			Util.closeDBObjects(rs, retrieveTexttableJoinCondition);
		}

		logger.exiting(CLASS_NAME, METHOD_NAME);
		return joinCondition.toString();
	}
	
	// Retrieves the source data collection rules for a check table from the foreign keys table in the CW DB.
	private static List<SourceDataCollectionRule> getCollRulesForCheckTableFromCWDB(ReferenceTable referenceTable, String legacyId, String rollout) {
		final String METHOD_NAME = "getCollRulesForCheckTableFromCWDB(ReferenceTable referenceTable, String legacyId, String rollout)"; 
		logger.entering(CLASS_NAME, METHOD_NAME);
		
		Connection conn = null;
		PreparedStatement retrieveCollRules = null;
		ResultSet rs = null;
		List<SourceDataCollectionRule> result = new ArrayList<SourceDataCollectionRule>();
		
		try {
			conn = CWDBConnectionFactory.getConnection();
			
			String query = "SELECT DISTINCT "
					+ Constants.CW_COLUMN_SAP_TABLENAME + ", "
					+ Constants.CW_COLUMN_SAP_FIELDNAME	+ ", "
					+ Constants.CW_COLUMN_CHECK_FIELD + ", "
					+ Constants.CW_COLUMN_FOREIGN_TABLE	+ ", "
					+ Constants.CW_COLUMN_FOREIGN_KEY + " FROM "
					+ Constants.CW_TABLE_FOREIGN_KEYS + " WHERE "
					+ Constants.CW_COLUMN_LEGACY_ID + "= ?" + " AND "  
					+ Constants.CW_COLUMN_ROLLOUT + "= ?"	+ " AND "
					+ Constants.CW_COLUMN_SAP_CHECK_TABLE + "= ?";
			
			if (referenceTable.getTextTable() != null) {
				query += " AND " + Constants.CW_COLUMN_SAP_TABLENAME + "<> ?";
			}
			
			query += " ORDER BY " + Constants.CW_COLUMN_SAP_TABLENAME + " ASC" + ", "
					+ Constants.CW_COLUMN_SAP_FIELDNAME + " ASC" + ", "
					+ Constants.CW_COLUMN_CHECK_FIELD + " ASC";
			
			logger.fine(query);
			
			retrieveCollRules = conn.prepareStatement(query);
			retrieveCollRules.setString(1, legacyId);
			logger.fine(Constants.CW_COLUMN_LEGACY_ID + ": " + legacyId);
			retrieveCollRules.setString(2, rollout);
			logger.fine(Constants.CW_COLUMN_ROLLOUT + ": " + rollout);
			retrieveCollRules.setString(3, referenceTable.getSapName());
			logger.fine(Constants.CW_COLUMN_SAP_CHECK_TABLE + ": " + referenceTable.getSapName());
			
			if (referenceTable.getTextTable() != null) {
				retrieveCollRules.setString(4, referenceTable.getTextTable().getSapName());
				logger.fine(Constants.CW_COLUMN_SAP_TABLENAME + ": " + referenceTable.getTextTable().getSapName());
			}
			
			rs = retrieveCollRules.executeQuery();

			// Remember the data table from the last row so that we can combine subsequent rows with the same values into one rule
			String lastDataTable = null;
			String lastDataField = null;
			SourceDataCollectionRule rule = null;
			while (rs.next()) {
				String newDataTable = rs.getString(Constants.CW_COLUMN_SAP_TABLENAME);
				String newDataField = rs.getString(Constants.CW_COLUMN_SAP_FIELDNAME);
				
				if (!newDataTable.equals(lastDataTable) || !newDataField.equals(lastDataField)) {
					
					// Found the first row of a new rule
					rule = new SourceDataCollectionRule();
					rule.setReferenceTable(referenceTable);
					rule.setDataTableName(newDataTable);
					rule.setCollectionRule("");
					result.add(rule);
				}
				
				// Extend current rule
				String ruleString = rule.getCollectionRule();
				
				if (!ruleString.isEmpty()) {
					ruleString += ",";
				}
				
				if (rs.getString(Constants.CW_COLUMN_FOREIGN_KEY) != null) {
					
					// Regular case: <data table column name>=<check table column name>
					ruleString += rs.getString(Constants.CW_COLUMN_FOREIGN_KEY) + "=" + rs.getString(Constants.CW_COLUMN_CHECK_FIELD);
					
					// Exception: rules involving other tables are not supported
					// TODO: keep this rule, the reading part already is smart enough
					if (!rs.getString(Constants.CW_COLUMN_SAP_TABLENAME).equals(rs.getString(Constants.CW_COLUMN_FOREIGN_TABLE))) {
						result.remove(rule);
					}
				}
				else {
					
					// Special case: <literal or wildcard>=<check table column name>
					ruleString += rs.getString(Constants.CW_COLUMN_FOREIGN_TABLE) + "=" + rs.getString(Constants.CW_COLUMN_CHECK_FIELD);
				}
				
				rule.setCollectionRule(ruleString);
				
				lastDataTable = newDataTable;
				lastDataField = newDataField;
			}
		}
		catch (SQLException e) {
			Util.throwInternalErrorToClient(e);
		}
		catch (NamingException e) {
			Util.throwInternalErrorToClient(e);
		}
		finally {

			// Don't close the connection, caller will close it
			Util.closeDBObjects(rs, retrieveCollRules);
		}

		logger.exiting(CLASS_NAME, METHOD_NAME);
		return result;
	}
	
	// Returns Returns a shallow "preview" list of the domain tables from the CW AUX.SAP_DOMAINTABLES_METADATA table
	// for the given SAP system legacy ID, rollout and business object.
	public static HashMap<String, ReferenceTable> getDomainTablePreviewFromCWDB(String legacyId, String rollout, String bo) {
		final String METHOD_NAME = "getDomainTablePreviewFromCWDB(String legacyId, String rollout, String bo)"; 
		logger.entering(CLASS_NAME, METHOD_NAME);
		
		Connection conn = null;
		PreparedStatement retrieveDomaintablePreview = null;
		ResultSet rs = null;
		HashMap<String, ReferenceTable> result = new HashMap<String, ReferenceTable>();

		try {
			conn = CWDBConnectionFactory.getConnection();
			
			String query = "SELECT DISTINCT "
					+ Constants.CW_COLUMN_DOMAIN_TABLE + ", "
					+ Constants.CW_COLUMN_DOMAIN_TRANSCODING_TABLE + ", "
					+ Constants.CW_COLUMN_DESCRIPTION + " FROM "
					+ Constants.CW_TABLE_DOMAIN_TABLES_METADATA + " WHERE "
					+ Constants.CW_COLUMN_LEGACY_ID + "= ?" + " AND "
					+ Constants.CW_COLUMN_ROLLOUT + "= ?";
			
			if (!bo.equals(Constants.CW_LOB_WILDCARD)) {
				query += " AND " + Constants.CW_COLUMN_LOB + "= ?";
			}

			// Add scoping condition: only load check tables referenced by the table fields selected in BDR
			query += " AND " + Constants.CW_COLUMN_DOMAIN_NAME
				+ " IN (SELECT DISTINCT " + Constants.CW_COLUMN_DOMAIN_NAME
				+ " FROM " + Constants.CW_TABLE_FIELDS_CONFIG
				+ " LEFT OUTER JOIN (SELECT * FROM " + Constants.CW_TABLE_FIELDS_METADATA + ") FMD ON "
				+ Constants.CW_TABLE_FIELDS_CONFIG + "." + Constants.CW_COLUMN_SAP_TABLENAME + " = FMD." + Constants.CW_COLUMN_SAP_TABLENAME
				+ " AND "
				+ Constants.CW_TABLE_FIELDS_CONFIG + "." + Constants.CW_COLUMN_SAP_FIELDNAME + " = FMD." + Constants.CW_COLUMN_SAP_FIELDNAME
				+ " WHERE " + Constants.CW_TABLE_FIELDS_CONFIG + "." + Constants.CW_COLUMN_ACTIVE + " = 'X'"
				+ " AND " + Constants.CW_TABLE_FIELDS_CONFIG + "." + Constants.CW_COLUMN_ROLLOUT + " = ?)"
				+ " ORDER BY " + Constants.CW_COLUMN_DOMAIN_TABLE + " ASC";
			
			logger.fine(query);
			
			retrieveDomaintablePreview = conn.prepareStatement(query);
			retrieveDomaintablePreview.setString(1, legacyId);
			logger.fine(Constants.CW_COLUMN_LEGACY_ID + ": " + legacyId);
			retrieveDomaintablePreview.setString(2, rollout);
			logger.fine(Constants.CW_COLUMN_ROLLOUT + ": " + rollout);
			
			if (!bo.equals(Constants.CW_LOB_WILDCARD)) {
				retrieveDomaintablePreview.setString(3, bo);
				logger.fine(Constants.CW_COLUMN_LOB + ": " + bo);
				retrieveDomaintablePreview.setString(4, rollout);
			} else {
				retrieveDomaintablePreview.setString(3, rollout);
			}

			rs = retrieveDomaintablePreview.executeQuery();
			
			while (rs.next()) {
				
				// Create a reference table
				ReferenceTable referenceTable = new ReferenceTable();
				referenceTable.setName(rs.getString(Constants.CW_COLUMN_DOMAIN_TABLE));
				referenceTable.setDescription(rs.getString(Constants.CW_COLUMN_DESCRIPTION));
				referenceTable.setTableType(ReferenceTableType.DOMAIN_TABLE);
				
				// Add the transcoding table
				TranscodingTable transcodingTable = new TranscodingTable(rs.getString(Constants.CW_COLUMN_DOMAIN_TRANSCODING_TABLE));
				referenceTable.setTranscodingTable(transcodingTable);

				result.put(referenceTable.getName(), referenceTable);
			}
		}
		catch (SQLException e) {
			Util.throwInternalErrorToClient(e);
		}
		catch (NamingException e) {
			Util.throwInternalErrorToClient(e);
		}
		finally {
			Util.closeDBObjects(rs, retrieveDomaintablePreview, conn);
		}
		
		logger.exiting(CLASS_NAME, METHOD_NAME);
		return result;
	}
	
	// Returns the complete check table object based on metadata from the CW AUX.SAP_CHECKTABLES_METADATA table
	// for the given table name and SAP system legacy ID.
	public static ReferenceTable getCheckTableFromCWDB(String cwTableName, String legacyId, String rollout) {
		final String METHOD_NAME = "getCheckTableFromCWDB(String cwTableName, String legacyId, String rollout)"; 
		logger.entering(CLASS_NAME, METHOD_NAME);
		
		Connection conn = null;
		PreparedStatement retrieveChecktable = null;
		ResultSet rs = null;
		ReferenceTable referenceTable = null;

		try {
			conn = CWDBConnectionFactory.getConnection();
			
			String query = "SELECT DISTINCT "
					+ Constants.CW_COLUMN_SAP_CHECK_TABLE + ", "
					+ Constants.CW_COLUMN_DESCRIPTION + ", "
					+ Constants.CW_COLUMN_TEXT_TABLE + ", "
					+ Constants.CW_COLUMN_CW_TEXT_TABLE + ", "
					+ Constants.CW_COLUMN_TRANSCODING_TABLE	+ " FROM "
					+ Constants.CW_TABLE_CHECK_TABLES_METADATA + " WHERE "
					+ Constants.CW_COLUMN_CT_CHECK_TABLE + "= ?" + " AND "
					+ Constants.CW_COLUMN_LEGACY_ID + "= ?" + " AND "  
					+ Constants.CW_COLUMN_ROLLOUT + "= ?";
			
			logger.fine(query);
			
			retrieveChecktable = conn.prepareStatement(query);
			retrieveChecktable.setString(1, cwTableName);
			logger.fine(Constants.CW_COLUMN_CT_CHECK_TABLE + ": " + cwTableName);
			retrieveChecktable.setString(2, legacyId);
			logger.fine(Constants.CW_COLUMN_LEGACY_ID + ": " + legacyId);
			retrieveChecktable.setString(3, rollout);
			logger.fine(Constants.CW_COLUMN_ROLLOUT + ": " + rollout);
			rs = retrieveChecktable.executeQuery();
			
			if (rs.next()) {
				
				// Create a reference table
				referenceTable = new ReferenceTable();
				referenceTable.setName(cwTableName);
				referenceTable.setSapName(rs.getString(Constants.CW_COLUMN_SAP_CHECK_TABLE));
				referenceTable.setDescription(rs.getString(Constants.CW_COLUMN_DESCRIPTION));
				referenceTable.setTableType(ReferenceTableType.CHECK_TABLE);

				// Get the columns
				referenceTable.setColumns(getReferenceTableColumnsFromCWDB(referenceTable, legacyId, rollout));
				
				if (referenceTable.getColumns() == null || referenceTable.getColumns().isEmpty()) {
					// No column metadata, panic
					logger.log(Level.SEVERE, "No column metadata for table " + referenceTable.getName());
					throw new CwAppException(HttpURLConnection.HTTP_INTERNAL_ERROR);
				}
				
				// Add the transcoding table
				TranscodingTable transcodingTable = new TranscodingTable(rs.getString(Constants.CW_COLUMN_TRANSCODING_TABLE));
				referenceTable.setTranscodingTable(transcodingTable);
				
				// Add the text table, if any
				String textTableName = rs.getString(Constants.CW_COLUMN_CW_TEXT_TABLE);
				
				if (textTableName != null && !textTableName.isEmpty()) { 
					TextTable textTable = new TextTable();
					textTable.setName(textTableName);
					textTable.setSapName(rs.getString(Constants.CW_COLUMN_TEXT_TABLE));
					referenceTable.setTextTable(textTable);

					// Set the join condition
					referenceTable.getTextTable().setJoinCondition(getTextTableJoinConditionFromCWDB(referenceTable, legacyId, rollout));
					
					// Get the text table columns
					textTable.setColumns(getTextTableColumnsFromCWDB(textTable, legacyId, rollout));
					
					if (textTable.getColumns() == null || textTable.getColumns().isEmpty()) {
						// No column metadata, panic
						logger.log(Level.SEVERE, "No column metadata for table " + textTable.getName());
						throw new CwAppException(HttpURLConnection.HTTP_INTERNAL_ERROR);
					}
				}
				
				// Set the source data collection rules
				referenceTable.setSourceDataCollectionRules(getCollRulesForCheckTableFromCWDB(referenceTable, legacyId, rollout));
			}
			else {
				// Table not found, panic
				logger.log(Level.SEVERE, "No metadata for table " + cwTableName + "!");
				throw new CwAppException(HttpURLConnection.HTTP_INTERNAL_ERROR);
			}
		}
		catch (SQLException e) {
			Util.throwInternalErrorToClient(e);
		}
		catch (NamingException e) {
			Util.throwInternalErrorToClient(e);
		}
		finally {
			Util.closeDBObjects(rs, retrieveChecktable, conn);
		}
		
		logger.exiting(CLASS_NAME, METHOD_NAME);
		return referenceTable;
	}
	
	// Returns the complete domain table object based on metadata from the CW AUX.SAP_DOMAINTABLES_METADATA table
	// for the given table name, SAP system legacy ID, rollout and business object.
	public static ReferenceTable getDomainTableFromCWDB(String cwTableName, String legacyId, String rollout) {
		final String METHOD_NAME = "getDomainTableFromCWDB(String cwTableName, String legacyId, String rollout)"; 
		logger.entering(CLASS_NAME, METHOD_NAME);
		
		Connection conn = null;
		PreparedStatement retrieveDomaintable = null;
		ResultSet rs = null;
		ReferenceTable referenceTable = null;

		try {
			conn = CWDBConnectionFactory.getConnection();
			
			String query = "SELECT "
					+ Constants.CW_COLUMN_DOMAIN_NAME + ", "
					+ Constants.CW_COLUMN_DOMAIN_TRANSCODING_TABLE + ", "
					+ Constants.CW_COLUMN_DESCRIPTION + " FROM "
					+ Constants.CW_TABLE_DOMAIN_TABLES_METADATA + " WHERE "
					+ Constants.CW_COLUMN_DOMAIN_TABLE + "= ? AND "
					+ Constants.CW_COLUMN_LEGACY_ID + "= ?" + " AND "  
					+ Constants.CW_COLUMN_ROLLOUT + "= ?";
			
			logger.fine(query);

			retrieveDomaintable = conn.prepareStatement(query);
			retrieveDomaintable.setString(1, cwTableName);
			logger.fine(Constants.CW_COLUMN_CT_CHECK_TABLE + ": " + cwTableName);
			retrieveDomaintable.setString(2, legacyId);
			logger.fine(Constants.CW_COLUMN_LEGACY_ID + ": " + legacyId);
			retrieveDomaintable.setString(3, rollout);
			logger.fine(Constants.CW_COLUMN_ROLLOUT + ": " + rollout);
			rs = retrieveDomaintable.executeQuery();
			
			if (rs.next()) {
				
				// Create a reference table
				referenceTable = new ReferenceTable();
				referenceTable.setName(cwTableName);
				referenceTable.setSapName(rs.getString(Constants.CW_COLUMN_DOMAIN_NAME));
				referenceTable.setDescription(rs.getString(Constants.CW_COLUMN_DESCRIPTION));
				referenceTable.setTableType(ReferenceTableType.DOMAIN_TABLE);

				// Set the column layout
				for (String columnName : Constants.DOMAIN_TABLE_COLUMNS) {
					ReferenceTableColumn column = new ReferenceTableColumn();
					column.setReferenceTable(referenceTable);
					column.setName(columnName);
					column.setSapName(columnName);
					column.setDomain("");
					// TODO: this data exists in CWDB already, no need to combine strings.
					column.setTranscodingTableSrcName("SOURCE_" + rs.getString(Constants.CW_COLUMN_DOMAIN_NAME));
					column.setTranscodingTableTgtName("TARGET_" + rs.getString(Constants.CW_COLUMN_DOMAIN_NAME));
					
					referenceTable.addColumn(column);
				}

				// Add the transcoding table
				TranscodingTable transcodingTable = new TranscodingTable(rs.getString(Constants.CW_COLUMN_DOMAIN_TRANSCODING_TABLE));
				referenceTable.setTranscodingTable(transcodingTable);
				
				// Set the source data collection rules
				referenceTable.setSourceDataCollectionRules(getCollRulesForDomainFromCWDB(referenceTable, legacyId, rollout));
			}
			else {
				
				// Table not found, panic
				logger.log(Level.SEVERE, "No metadata for table " + cwTableName + "!");
				throw new CwAppException(HttpURLConnection.HTTP_INTERNAL_ERROR);
			}
		}
		catch (SQLException e) {
			Util.throwInternalErrorToClient(e);
		}
		catch (NamingException e) {
			Util.throwInternalErrorToClient(e);
		}
		finally {
			Util.closeDBObjects(rs, retrieveDomaintable, conn);
		}

		logger.exiting(CLASS_NAME, METHOD_NAME);
		return referenceTable;
	}
	
	// Retrieves the source data collection rules for a domain table from the fields metadata table in the CW DB.
	private static List<SourceDataCollectionRule> getCollRulesForDomainFromCWDB(ReferenceTable referenceTable, String legacyId, String rollout) {
		final String METHOD_NAME = "getCollRulesForDomainFromCWDB(ReferenceTable referenceTable, String legacyId, String rollout)"; 
		logger.entering(CLASS_NAME, METHOD_NAME);
		
		Connection conn = null;
		PreparedStatement retrieveCollRules = null;
		ResultSet rs = null;
		List<SourceDataCollectionRule> result = new ArrayList<SourceDataCollectionRule>();
		
		try {
			conn = CWDBConnectionFactory.getConnection();
			
			String query = "SELECT DISTINCT "
					+ Constants.CW_COLUMN_CW_TABLENAME + ", "
					+ Constants.CW_COLUMN_CW_FIELDNAME + ", "
					+ Constants.CW_COLUMN_DOMAIN_NAME + " FROM "
					+ Constants.CW_TABLE_FIELDS_METADATA + " WHERE "
					+ Constants.CW_COLUMN_LEGACY_ID + "= ?" + " AND "  
					+ Constants.CW_COLUMN_ROLLOUT + "= ?" + " AND "
					+ Constants.CW_COLUMN_DOMAIN_NAME + "= ?" + " AND "
					
					// Exclude check tables as source data tables
					+ Constants.CW_COLUMN_CW_TABLENAME + " NOT IN (SELECT "
					+ Constants.CW_COLUMN_CT_CHECK_TABLE + " FROM " + Constants.CW_TABLE_CHECK_TABLES_METADATA + ")"
					
					// Exclude IDoc segments as source data tables
					+ " AND " + Constants.CW_COLUMN_SAP_TABLENAME + " NOT IN (SELECT "
					+ Constants.CW_COLUMN_SAP_TABLENAME + " FROM " + Constants.CW_TABLE_DATA_TABLES_METADATA
					+ " WHERE " + Constants.CW_COLUMN_KEY_FIELD + "='" + Constants.CW_KEY_FIELD_VALUE + "')"
					+ " ORDER BY " + Constants.CW_COLUMN_CW_TABLENAME + " ASC";
			
			logger.fine(query);
			
			retrieveCollRules = conn.prepareStatement(query);
			retrieveCollRules.setString(1, legacyId);
			logger.fine(Constants.CW_COLUMN_LEGACY_ID + ": " + legacyId);
			retrieveCollRules.setString(2, rollout);
			logger.fine(Constants.CW_COLUMN_ROLLOUT + ": " + rollout);
			retrieveCollRules.setString(3, referenceTable.getSapName());
			logger.fine(Constants.CW_COLUMN_SAP_CHECK_TABLE + ": " + referenceTable.getSapName());
			rs = retrieveCollRules.executeQuery();

			while (rs.next()) {
				SourceDataCollectionRule rule = new SourceDataCollectionRule();
				rule.setReferenceTable(referenceTable);
				rule.setDataTableName(rs.getString(Constants.CW_COLUMN_CW_TABLENAME));
				rule.setCollectionRule(rs.getString(Constants.CW_COLUMN_CW_FIELDNAME) + "=" + Constants.DOMAIN_TABLE_COLUMNS[1]);
				
				result.add(rule);
			}
		}
		catch (SQLException e) {
			Util.throwInternalErrorToClient(e);
		}
		catch (NamingException e) {
			Util.throwInternalErrorToClient(e);
		}
		finally {

			// Don't close the connection, caller will close it
			Util.closeDBObjects(rs, retrieveCollRules);
		}

		logger.exiting(CLASS_NAME, METHOD_NAME);
		return result;
	}
	
	public static void clearTableInCWDB(ITable table) throws SQLException, NamingException {
		final String METHOD_NAME = "clearTableInCWDB(ITable table)"; 
		logger.entering(CLASS_NAME, METHOD_NAME);
		
		Connection conn = null;
		Statement stmt = null;

		try {
			conn = CWDBConnectionFactory.getConnection();
			stmt = conn.createStatement();

			String deleteSql = "DELETE FROM " + '"' + CwApp.getReferenceTableSchema() + '"' + "." + '"' + table.getName() + '"';

			stmt.executeUpdate(deleteSql);
			conn.commit();
		} finally {
			Util.closeDBObjects(stmt, conn);
		}
		logger.exiting(CLASS_NAME, METHOD_NAME);
	}
}
