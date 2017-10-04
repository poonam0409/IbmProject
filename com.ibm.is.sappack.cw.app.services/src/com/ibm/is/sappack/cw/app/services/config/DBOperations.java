package com.ibm.is.sappack.cw.app.services.config;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.naming.NamingException;
import javax.servlet.http.HttpSession;

import com.ibm.is.sappack.cw.app.data.config.LegacySystem;
import com.ibm.is.sappack.cw.app.services.CWDBConnectionFactory;
import com.ibm.is.sappack.cw.app.services.Constants;
import com.ibm.is.sappack.cw.app.services.CwApp;
import com.ibm.is.sappack.cw.app.services.Util;

public class DBOperations {

	private static final String CLASS_NAME = DBOperations.class.getName();
	private static Logger logger = CwApp.getLogger();
	
	public static enum DatabaseSortOptions {
		SORT_BY_DESCRIPTION_ASC,
		SORT_BY_DESCRIPTION_DESC,
		SORT_BY_LEGACYID_ASC,
		SORT_BY_LEGACYID_DESC,
		SORT_NONE
	}

	public static Map<String, String> getSourceLegacyIdsFromCWDB() {
		return getLegacyIdsFromCWDB(false);
	}

	public static Map<String, String> getLegacyIdsFromCWDB() {
		return getLegacyIdsFromCWDB(true);
	}
	
	// Retrieves the legacy IDs from the CW DB and returns a map between the IDs and short names
	private static Map<String, String> getLegacyIdsFromCWDB(boolean includeTarget) {
		logger.entering(CLASS_NAME, "getLegacyIdsFromCWDB(boolean includeTarget)");
		Connection conn = null;
		PreparedStatement retrieveLegacyIds = null;
		ResultSet legacyIds = null;
		Map<String, String> result = new HashMap<String, String>();

		try {
			conn = CWDBConnectionFactory.getConnection();
			String query = "SELECT " + Constants.CW_LEGACY_ID_COLUMN + " FROM " + Constants.CW_LEGACY_ID_TABLENAME;
			
			if (!includeTarget) {
				query += " WHERE " + Constants.CW_LEGACY_ID_TARGET_SYSTEM_COLUMN + " NOT LIKE " + "'" + Constants.CW_TRUE_VALUE + "'";
				query += " OR " + Constants.CW_LEGACY_ID_TARGET_SYSTEM_COLUMN + " IS NULL";
			}

			logger.fine(query);
			
			retrieveLegacyIds = conn.prepareStatement(query);
			legacyIds = retrieveLegacyIds.executeQuery();
	
			while (legacyIds.next()) {
				String id = legacyIds.getString(Constants.CW_LEGACY_ID_COLUMN).toUpperCase();
				String name = legacyIds.getString(Constants.CW_LEGACY_ID_COLUMN);
				
				if (name.isEmpty()) {
					name = id;
				}
				
				result.put(id, name);
			}
		}
		catch (SQLException e) {
			Util.throwInternalErrorToClient(e);
		}
		catch (NamingException e) {
			Util.throwInternalErrorToClient(e);
		}
		finally {
			Util.closeDBObjects(legacyIds, retrieveLegacyIds, conn);
		}
		
		logger.exiting(CLASS_NAME, "getLegacyIdsFromCWDB(boolean includeTarget)");
		return result;
	}

	// retrieve a single legacy system from the CW database, wrapped into a list
	public static List<LegacySystem> getLegacySystem(String legacyId, HttpSession session) {
		return getLegacySystems(legacyId, DatabaseSortOptions.SORT_BY_LEGACYID_ASC, session, true, true);
	}
	
	// retrieve a single SOURCE legacy system from the CW database, wrapped into a list
	public static List<LegacySystem> getSourceLegacySystem(String legacyId, HttpSession session) {
		return getLegacySystems(legacyId, DatabaseSortOptions.SORT_BY_LEGACYID_ASC, session, true, false);
	}

	// retrieve a single TARGET legacy system from the CW database, wrapped into a list
	public static List<LegacySystem> getTargetLegacySystem(String legacyId, HttpSession session) {
		return getLegacySystems(legacyId, DatabaseSortOptions.SORT_BY_LEGACYID_ASC, session, false, true);
	}
	
	// retrieve a list of legacy systems from the CW database with an optional sort order
	public static List<LegacySystem> getLegacySystems(DBOperations.DatabaseSortOptions sortOption, HttpSession session) {
		return getLegacySystems(null, sortOption, session, true, true);
	}
	
	// retrieve a list of SOURCE legacy systems from the CW database with an optional sort order
	public static List<LegacySystem> getSourceLegacySystems(DBOperations.DatabaseSortOptions sortOption, HttpSession session) {
		return getLegacySystems(null, sortOption, session, true, false);
	}

	// retrieve a list of TARGET legacy systems from the CW database with an optional sort order
	public static List<LegacySystem> getTargetLegacySystems(DBOperations.DatabaseSortOptions sortOption, HttpSession session) {
		return getLegacySystems(null, sortOption, session, false, true);
	}
	
	// retrieve a list of legacy systems from the CW database with an optional sort order
	// private method with additional parameter to allow for selection of source or target SAP systems only
	// The passwords are retrieved from the HTTP session!
	private static List<LegacySystem> getLegacySystems(String legacyId, DBOperations.DatabaseSortOptions sortOption, HttpSession session,
			boolean retrieveSource, boolean retrieveTarget) {
		logger.entering(CLASS_NAME, "getLegacySystems(String legacyId, DBOperations.DatabaseSortOptions sortOption, HttpSession session, boolean retrieveSource, boolean retrieveTarget)");
		Connection conn = null;
		PreparedStatement retrieveLegacySystems = null;
		PreparedStatement retrieveSapSystems = null;
		ResultSet legacySystems = null;
		ResultSet sapSystems = null;
		List<LegacySystem> systems = new ArrayList<LegacySystem>();
	
		try {
			conn = CWDBConnectionFactory.getConnection();
			String query = "SELECT "
				+ Constants.CW_LEGACY_ID_COLUMN + ", "
				+ Constants.CW_LEGACY_ID_DESCRIPTION_COLUMN	+ ", "
				+ Constants.CW_LEGACY_ID_TARGET_SYSTEM_COLUMN + " FROM "
				+ Constants.CW_LEGACY_ID_TABLENAME + " WHERE ";
			
			boolean useAnd = false;
			
			if (retrieveSource && !retrieveTarget) {
				query += Constants.CW_LEGACY_ID_TARGET_SYSTEM_COLUMN + " IS NULL ";
				useAnd = true;
			}
			else if (!retrieveSource && retrieveTarget) {
				query += Constants.CW_LEGACY_ID_TARGET_SYSTEM_COLUMN + " IS NOT NULL ";
				useAnd = true;
			}
			
			if (legacyId != null) {
				if (useAnd) {
					query += " AND ";
				}
				
				query += Constants.CW_LEGACY_ID_COLUMN + " = ?";
			}
			
			switch(sortOption) {
			case SORT_BY_DESCRIPTION_ASC:
				query = query + " ORDER BY "
				+ Constants.CW_LEGACY_ID_TABLENAME + "."
				+ Constants.CW_LEGACY_ID_DESCRIPTION_COLUMN + " ASC";
				break;
			case SORT_BY_DESCRIPTION_DESC:
				query = query + " ORDER BY "
				+ Constants.CW_LEGACY_ID_TABLENAME + "."
				+ Constants.CW_LEGACY_ID_DESCRIPTION_COLUMN + " DESC";
				break;
			case SORT_BY_LEGACYID_ASC:
				query = query + " ORDER BY "
				+ Constants.CW_LEGACY_ID_TABLENAME + "."
				+ Constants.CW_LEGACY_ID_COLUMN + " ASC";
				break;
			case SORT_BY_LEGACYID_DESC:
				query = query + " ORDER BY "
				+ Constants.CW_LEGACY_ID_TABLENAME + "."
				+ Constants.CW_LEGACY_ID_COLUMN + " DESC";
				break;
			default:
			case SORT_NONE:
				break;
			}
			
			logger.fine(query);
			
			retrieveLegacySystems = conn.prepareStatement(query);
			
			if (legacyId != null) {
				retrieveLegacySystems.setString(1, legacyId);
			}
			
			legacySystems = retrieveLegacySystems.executeQuery();
	
			// fill basic LegacySystem data structure for each member of the result set
			while (legacySystems.next()) {
				LegacySystem system = new LegacySystem();
				system.setLegacyId(legacySystems.getString(Constants.CW_LEGACY_ID_COLUMN).toUpperCase());
				system.setDescription(legacySystems.getString(Constants.CW_LEGACY_ID_DESCRIPTION_COLUMN));
				system.setIsTargetSystem(legacySystems.getString(Constants.CW_LEGACY_ID_TARGET_SYSTEM_COLUMN) != null && !legacySystems.getString(Constants.CW_LEGACY_ID_TARGET_SYSTEM_COLUMN).isEmpty());
				
				// do another query to see if this is an SAP system and retrieve the details
				String query1 = "SELECT "
						+ Constants.CW_LEGACY_ID_SAP_TABLENAME + "."
						+ Constants.CW_LEGACY_ID_COLUMN + ", "
						+ Constants.CW_LEGACY_ID_SAP_TABLENAME + "."
						+ Constants.CW_LEGACY_ID_SAP_HOST_COLUMN + ", "
						+ Constants.CW_LEGACY_ID_SAP_TABLENAME + "."
						+ Constants.CW_LEGACY_ID_SAP_CLIENT_COLUMN + ", "
						+ Constants.CW_LEGACY_ID_SAP_TABLENAME + "."
						+ Constants.CW_LEGACY_ID_SAP_GROUPNAME_COLUMN + ", "
						+ Constants.CW_LEGACY_ID_SAP_TABLENAME + "."
						+ Constants.CW_LEGACY_ID_SAP_LANGUAGE_COLUMN + ", "
						+ Constants.CW_LEGACY_ID_SAP_TABLENAME + "."
						+ Constants.CW_LEGACY_ID_SAP_MESSAGESERVER_COLUMN + ", "
						+ Constants.CW_LEGACY_ID_SAP_TABLENAME + "."
						+ Constants.CW_LEGACY_ID_SAP_ROUTERSTRING_COLUMN + ", "
						+ Constants.CW_LEGACY_ID_SAP_TABLENAME + "."
						+ Constants.CW_LEGACY_ID_SAP_SAPSYSTEMID_COLUMN + ", "
						+ Constants.CW_LEGACY_ID_SAP_TABLENAME + "."
						+ Constants.CW_LEGACY_ID_SAP_SYSTEMNUMBER_COLUMN + ", "
						+ Constants.CW_LEGACY_ID_SAP_TABLENAME + "."
						+ Constants.CW_LEGACY_ID_SAP_USELOADBALANCING_COLUMN + ", "
						+ Constants.CW_LEGACY_ID_SAP_TABLENAME + "."
						+ Constants.CW_LEGACY_ID_SAP_USER_COLUMN + " FROM "
						+ Constants.CW_LEGACY_ID_SAP_TABLENAME + " INNER JOIN "
						+ Constants.CW_LEGACY_ID_TABLENAME + " ON "
						+ Constants.CW_LEGACY_ID_SAP_TABLENAME + "."
						+ Constants.CW_LEGACY_ID_COLUMN + " = "
						+ Constants.CW_LEGACY_ID_TABLENAME + "."
						+ Constants.CW_LEGACY_ID_COLUMN;
				
				logger.fine(query1);

				retrieveSapSystems = conn.prepareStatement(query1);
				sapSystems = retrieveSapSystems.executeQuery();
	
				// if SAP systems have been found in general we look for the one of interest
				// and further fill the LegacySystem object
				while (sapSystems.next()) {
					if (sapSystems.getString(Constants.CW_LEGACY_ID_COLUMN).equalsIgnoreCase(system.getLegacyId())) {
						system.setIsSapSystem(true);
						system.setSapHost(sapSystems.getString(Constants.CW_LEGACY_ID_SAP_HOST_COLUMN));
						system.setSapClient(sapSystems.getString(Constants.CW_LEGACY_ID_SAP_CLIENT_COLUMN));
						system.setSapGroupName(sapSystems.getString(Constants.CW_LEGACY_ID_SAP_GROUPNAME_COLUMN));
						system.setSapLanguage(sapSystems.getString(Constants.CW_LEGACY_ID_SAP_LANGUAGE_COLUMN));
						system.setSapMessageServer(sapSystems.getString(Constants.CW_LEGACY_ID_SAP_MESSAGESERVER_COLUMN));
						system.setSapRouterString(sapSystems.getString(Constants.CW_LEGACY_ID_SAP_ROUTERSTRING_COLUMN));
						system.setSapSystemId(sapSystems.getString(Constants.CW_LEGACY_ID_SAP_SAPSYSTEMID_COLUMN));
						system.setSapSystemNumber(sapSystems.getString(Constants.CW_LEGACY_ID_SAP_SYSTEMNUMBER_COLUMN));
						
						if (sapSystems.getInt(Constants.CW_LEGACY_ID_SAP_USELOADBALANCING_COLUMN) == 1) {
							system.setSapUseLoadBalancing(true);
						}
						else {
							system.setSapUseLoadBalancing(false);
						}
						
						system.setSapUser(sapSystems.getString(Constants.CW_LEGACY_ID_SAP_USER_COLUMN_GENERIC));
						system.setSapPassword((String) session.getAttribute(system.getLegacyId()));
					}
				}
				
				systems.add(system);
			}
		}
		catch (SQLException e) {
			Util.throwInternalErrorToClient(e);
		}
		catch (NamingException e) {
			Util.throwInternalErrorToClient(e);
		}
		finally {
			Util.closeDBObjects(sapSystems, legacySystems, retrieveSapSystems, retrieveLegacySystems, conn);
		}
		
		logger.exiting(CLASS_NAME, "getLegacySystems(String legacyId, DBOperations.DatabaseSortOptions sortOption, HttpSession session, boolean retrieveSource, boolean retrieveTarget)");
		return systems;
	}

	// add a new legacy system to the CW database
	public static LegacySystem addLegacySystem(LegacySystem system) {
		logger.entering(CLASS_NAME, "addLegacySystem(LegacySystem system)");
		Connection conn = null;
		PreparedStatement insertLegacySystem = null;
		PreparedStatement insertSapSystem = null;
		
		try {
			conn = CWDBConnectionFactory.getConnection();
			String query = null;
			
			if (system.getIsTargetSystem()) {
				String targetSystemStatement = Constants.CW_TRUE_VALUE;
				
				query = "INSERT INTO "
						+ Constants.CW_LEGACY_ID_TABLENAME	+ "("
						+ Constants.CW_LEGACY_ID_COLUMN + ", "
						+ Constants.CW_LEGACY_ID_DESCRIPTION_COLUMN + ", "
						+ Constants.CW_LEGACY_ID_SHORTNAME_COLUMN + ", "
						+ Constants.CW_LEGACY_ID_TARGET_SYSTEM_COLUMN + ") VALUES (?,?,?,?)";

				logger.fine(query);

				insertLegacySystem = conn.prepareStatement(query);
				insertLegacySystem.setString(1, system.getLegacyId());
				insertLegacySystem.setString(2, system.getDescription());
				insertLegacySystem.setString(3, Long.toString(System.currentTimeMillis()));
				insertLegacySystem.setString(4, targetSystemStatement);
			}
			else {
				query = "INSERT INTO "
						+ Constants.CW_LEGACY_ID_TABLENAME	+ "("
						+ Constants.CW_LEGACY_ID_COLUMN + ", "
						+ Constants.CW_LEGACY_ID_DESCRIPTION_COLUMN + ", "
						+ Constants.CW_LEGACY_ID_SHORTNAME_COLUMN + ") VALUES (?,?,?)";

				logger.fine(query);

				insertLegacySystem = conn.prepareStatement(query);
				insertLegacySystem.setString(1, system.getLegacyId());
				insertLegacySystem.setString(2, system.getDescription());
				insertLegacySystem.setString(3, Long.toString(System.currentTimeMillis()));
			}
			
			insertLegacySystem.executeUpdate();
			
			// in case the legacy system is flagged as being a SAP system
			// we need to insert the SAP-specific properties into the additional SAP systems table
			if (system.getIsSapSystem()) {
				String query1 = "INSERT INTO "
							+ Constants.CW_LEGACY_ID_SAP_TABLENAME + "("
							+ Constants.CW_LEGACY_ID_COLUMN + ", "
							+ Constants.CW_LEGACY_ID_SAP_HOST_COLUMN + ", "
							+ Constants.CW_LEGACY_ID_SAP_CLIENT_COLUMN + ", "
							+ Constants.CW_LEGACY_ID_SAP_GROUPNAME_COLUMN + ", "
							+ Constants.CW_LEGACY_ID_SAP_LANGUAGE_COLUMN + ", "
							+ Constants.CW_LEGACY_ID_SAP_MESSAGESERVER_COLUMN + ", "
							+ Constants.CW_LEGACY_ID_SAP_ROUTERSTRING_COLUMN + ", "
							+ Constants.CW_LEGACY_ID_SAP_SAPSYSTEMID_COLUMN + ", "
							+ Constants.CW_LEGACY_ID_SAP_SYSTEMNUMBER_COLUMN + ", "
							+ Constants.CW_LEGACY_ID_SAP_USELOADBALANCING_COLUMN + ", "
							+ Constants.CW_LEGACY_ID_SAP_USER_COLUMN + ") VALUES (?,?,?,?,?,?,?,?,?,?,?)";
				
				logger.fine(query1);
				
				insertSapSystem = conn.prepareStatement(query1);
				insertSapSystem.setString(1, system.getLegacyId());
				insertSapSystem.setString(2, system.getSapHost());
				insertSapSystem.setString(3, system.getSapClient());
				insertSapSystem.setString(4, system.getSapGroupName());
				insertSapSystem.setString(5, system.getSapLanguage());
				insertSapSystem.setString(6, system.getSapMessageServer());
				insertSapSystem.setString(7, system.getSapRouterString());
				insertSapSystem.setString(8, system.getSapSystemId());
				insertSapSystem.setString(9, system.getSapSystemNumber());
				insertSapSystem.setInt(10, (system.getSapUseLoadBalancing() ? 1 : 0));
				insertSapSystem.setString(11, system.getSapUser());
				insertSapSystem.executeUpdate();
			}
			
			// commit the insert transactions
			conn.commit();
		}
		catch (SQLException e) {
			Util.throwInternalErrorToClient(conn, e);
		}
		catch (NamingException e) {
			Util.throwInternalErrorToClient(e);
		}
		finally {
			Util.closeDBObjects(insertSapSystem, insertLegacySystem, conn);
		}
		
		logger.exiting(CLASS_NAME, "addLegacySystem(LegacySystem system)");
		return system;
	}

	// update an existing legacy system in the CW database
	public static LegacySystem updateLegacySystem(String legacyId, LegacySystem system) {
		logger.entering(CLASS_NAME, "updateLegacySystem(String legacyId, LegacySystem system)");
		Connection conn = null;
		PreparedStatement updateLegacySystem = null;
		PreparedStatement updateSapSystem = null;
		
		try {
			conn = CWDBConnectionFactory.getConnection();

			String query = "UPDATE "
					+ Constants.CW_LEGACY_ID_TABLENAME + " SET "
					+ Constants.CW_LEGACY_ID_DESCRIPTION_COLUMN + "= ?, "
					+ Constants.CW_LEGACY_ID_SHORTNAME_COLUMN + "= ? WHERE "
					+ Constants.CW_LEGACY_ID_TABLENAME + "."
					+ Constants.CW_LEGACY_ID_COLUMN + " = ?";
			
			logger.fine(query);
			
			updateLegacySystem = conn.prepareStatement(query);
			updateLegacySystem.setString(1, system.getDescription());
			updateLegacySystem.setString(2, Long.toString(System.currentTimeMillis()));
			updateLegacySystem.setString(3, legacyId);
			updateLegacySystem.executeUpdate();
			
			// we need to check whether the updated legacy system is a SAP system or not
			if (system.getIsSapSystem()) {

				// in case the legacy system is flagged as being a SAP system
				// we need to update the SAP-specific properties in the
				// additional SAP systems table, too
				String query1 = "UPDATE "
							+ Constants.CW_LEGACY_ID_SAP_TABLENAME + " SET "
							+ Constants.CW_LEGACY_ID_SAP_HOST_COLUMN + "= ?, "
							+ Constants.CW_LEGACY_ID_SAP_CLIENT_COLUMN + "= ?, "
							+ Constants.CW_LEGACY_ID_SAP_GROUPNAME_COLUMN + "= ?, "
							+ Constants.CW_LEGACY_ID_SAP_LANGUAGE_COLUMN + "= ?, "
							+ Constants.CW_LEGACY_ID_SAP_MESSAGESERVER_COLUMN + "= ?, "
							+ Constants.CW_LEGACY_ID_SAP_ROUTERSTRING_COLUMN + "= ?, "
							+ Constants.CW_LEGACY_ID_SAP_SAPSYSTEMID_COLUMN + "= ?, "
							+ Constants.CW_LEGACY_ID_SAP_SYSTEMNUMBER_COLUMN + "= ?, "
							+ Constants.CW_LEGACY_ID_SAP_USELOADBALANCING_COLUMN + "= ?, "
							+ Constants.CW_LEGACY_ID_SAP_USER_COLUMN + "= ? WHERE "
							+ Constants.CW_LEGACY_ID_SAP_TABLENAME + "."
							+ Constants.CW_LEGACY_ID_COLUMN + " = ?";
				
				logger.fine(query1);
				
				updateSapSystem = conn.prepareStatement(query1);
				updateSapSystem.setString(1, system.getSapHost());
				updateSapSystem.setString(2, system.getSapClient());
				updateSapSystem.setString(3, system.getSapGroupName());
				updateSapSystem.setString(4, system.getSapLanguage());
				updateSapSystem.setString(5, system.getSapMessageServer());
				updateSapSystem.setString(6, system.getSapRouterString());
				updateSapSystem.setString(7, system.getSapSystemId());
				updateSapSystem.setString(8, system.getSapSystemNumber());
				updateSapSystem.setInt(9, (system.getSapUseLoadBalancing() ? 1 : 0));
				updateSapSystem.setString(10, system.getSapUser());
				updateSapSystem.setString(11, legacyId);
				updateSapSystem.executeUpdate();
			}
			
			// commit the update transactions
			conn.commit();
		}
		catch (SQLException e) {
			Util.throwInternalErrorToClient(conn, e);
		}
		catch (NamingException e) {
			Util.throwInternalErrorToClient(e);
		}
		finally {
			Util.closeDBObjects(updateSapSystem, updateLegacySystem, conn);
		}
		
		logger.exiting(CLASS_NAME, "updateLegacySystem(String legacyId, LegacySystem system)");

		return system;
	}

	// delete a legacy system from the CW database
	public static void deleteLegacySystem(String legacyId) {
		logger.entering(CLASS_NAME, "deleteLegacySystem(String legacyId)");
		Connection conn = null;
		PreparedStatement deleteSapSystem = null;
		PreparedStatement deleteLegacySystem = null;
	
		try {
			conn = CWDBConnectionFactory.getConnection();

			String query = "DELETE FROM "
				+ Constants.CW_LEGACY_ID_SAP_TABLENAME + " WHERE "
				+ Constants.CW_LEGACY_ID_SAP_TABLENAME + "."
				+ Constants.CW_LEGACY_ID_COLUMN + "= ?";
			
			logger.fine(query);
			
			deleteSapSystem = conn.prepareStatement(query);
			deleteSapSystem.setString(1, legacyId);
			deleteSapSystem.executeUpdate();

			String query1 = "DELETE FROM "
					+ Constants.CW_LEGACY_ID_TABLENAME + " WHERE "
					+ Constants.CW_LEGACY_ID_TABLENAME + "."
					+ Constants.CW_LEGACY_ID_COLUMN + "= ?";
				
			logger.fine(query1);
			
			deleteLegacySystem = conn.prepareStatement(query1);
			deleteLegacySystem.setString(1, legacyId);
			deleteLegacySystem.executeUpdate();
					
			// commit the delete transactions
			conn.commit();
		}
		catch (SQLException e) {
			Util.throwInternalErrorToClient(conn, e);
		}
		catch (NamingException e) {
			Util.throwInternalErrorToClient(e);
		}
		finally {
			Util.closeDBObjects(deleteLegacySystem, deleteSapSystem, conn);
		}
		
		logger.exiting(CLASS_NAME, "deleteLegacySystem(String legacyId)");
	}

	public static String getCwSchemaFromCWDB(String areaCode) {
		logger.entering(CLASS_NAME, "getCwSchemaFromCWDB(String areaCode)");
		Connection conn = null;
		PreparedStatement retrieveSchema = null;
		ResultSet schema = null;
		String cwSchemaName = null;
		
		try {
			conn = CWDBConnectionFactory.getConnection();

			String query = "SELECT "
				+ Constants.CW_SCHEMAS_SCHEMA_NAME_COLUMN + " FROM "
				+ Constants.CW_SCHEMAS_TABLENAME + " WHERE "
				+ Constants.CW_SCHEMAS_SCHEMA_TYPE_COLUMN + " = ?";
			
			logger.fine(query);
			
			retrieveSchema = conn.prepareStatement(query);
			retrieveSchema.setString(1, areaCode);
			schema = retrieveSchema.executeQuery();
	
			if (schema.next()) {
				cwSchemaName = schema.getString(Constants.CW_SCHEMAS_SCHEMA_NAME_COLUMN);
			}
			
			if (cwSchemaName == null || cwSchemaName.isEmpty()) {
				logger.severe("CW schema configuration is incomplete, missing schema name for area " + areaCode);
				logger.exiting(CLASS_NAME, "getCwSchemaFromCWDB(String areaCode)");
				
				return null;
			}
		}
		catch (SQLException e) {
			Util.throwInternalErrorToClient(e);
		}
		catch (NamingException e) {
			Util.throwInternalErrorToClient(e);
		}
		finally {
			Util.closeDBObjects(schema, retrieveSchema, conn);
		}
		
		logger.exiting(CLASS_NAME, "getCwSchemaFromCWDB(String areaCode)");

		return cwSchemaName;
	}

}
