package com.ibm.is.sappack.cw.app.services.bdr;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import com.ibm.is.sappack.cw.app.data.JPAResourceFactory;
import com.ibm.is.sappack.cw.app.data.bdr.jpa.Process;
import com.ibm.is.sappack.cw.app.data.bdr.jpa.ProcessStep;
import com.ibm.is.sappack.cw.app.data.bdr.jpa.Table;
import com.ibm.is.sappack.cw.app.data.bdr.jpa.TableUsage;
import com.ibm.is.sappack.cw.app.services.CWDBConnectionFactory;
import com.ibm.is.sappack.cw.app.services.Constants;
import com.ibm.is.sappack.cw.app.services.CwApp;
import com.ibm.is.sappack.cw.app.services.Util;

public class DBOperations {
	
	private static final String CLASS_NAME = DBOperations.class.getName();
	
	public enum ItemType {
		PROCESS,
		PROCESS_STEP,
		TABLE
	};
	
	public static void jpaDeleteItem(int id, ItemType type) {
		Logger logger = CwApp.getLogger();
		final String METHOD_NAME = "jpaDeleteItem(int id, ItemType type)"; 
		logger.entering(CLASS_NAME, METHOD_NAME);
		
		try {
			EntityManager manager = JPAResourceFactory.getEntityManager();
			UserTransaction transaction = JPAResourceFactory.getUserTransaction();
			transaction.begin();
			manager.joinTransaction();
			switch (type) {
				case PROCESS:
					Process process = manager.find(Process.class, id);
					if (process == null) {
						break;
					}
					// Remove all table usages first.
					// Table usages are handled "manually" since the integrity conditions are too complex
					// to define in JPA.
					List<TableUsage> usageList = getTableUsages(process);
					for (TableUsage tu : usageList) {
						manager.remove(tu);
					}
					manager.remove(process);
					break;
				case PROCESS_STEP:
					ProcessStep processStep = manager.find(ProcessStep.class, id);
					if (processStep == null) {
						break;
					}
					// Remove all table usages first.
					// Table usages are handled "manually" since the integrity conditions are too complex
					// to define in JPA.
					for (TableUsage tu : processStep.getUsages()) {
						manager.remove(tu);
					}
					manager.remove(processStep);
					break;
				case TABLE:
					Table table = manager.find(Table.class, id);
					if (table == null) {
						break;
					}
					manager.remove(table);
					break;
			}
			transaction.commit();
		} catch (NotSupportedException e) {
			Util.throwInternalErrorToClient(e);
		} catch (SystemException e) {
			Util.throwInternalErrorToClient(e);
		} catch (RollbackException e) {
			Util.throwInternalErrorToClient(e);
		} catch (HeuristicMixedException e) {
			Util.throwInternalErrorToClient(e);
		} catch (HeuristicRollbackException e) {
			Util.throwInternalErrorToClient(e);
		}
		
		logger.exiting(CLASS_NAME, METHOD_NAME);
	}
	
	// Recursively collects all descendant TableUsage objects for the given process
	private static List<TableUsage> getTableUsages(Process process) {
		List<TableUsage> result = new ArrayList<TableUsage>();
		if (process.getChildProcesses() != null) {
			for (Process subprocess : process.getChildProcesses()) {
				result.addAll(getTableUsages(subprocess));
			}
		}
		if (process.getProcessSteps() != null) {
			for (ProcessStep processStep : process.getProcessSteps()) {
				for (TableUsage tu : processStep.getUsages()) {
					result.add(tu);
				}
			}
		}
		return result;
	}
	
	// Checks if there is existing exported data for the given ROLLOUT in the CW AUX.SAP_DATATABLES_CONFIG table.
	public static boolean checkExistingExportInCWDB(String rollout) {
		Logger logger = CwApp.getLogger();
		final String METHOD_NAME = "checkExistingExportInCWDB(String rollout)"; 
		logger.entering(CLASS_NAME, METHOD_NAME);
		
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		int count = 0;

		try {
			conn = CWDBConnectionFactory.getConnection();
			
			String query = "SELECT COUNT(*) FROM " + Constants.CW_TABLE_DATA_TABLES_CONFIG;
			query += " WHERE " + Constants.CW_COLUMN_ROLLOUT + " = ?";
			logger.finer(query);
			stmt = conn.prepareStatement(query);
			
			stmt.setString(1, rollout);
			
			rs = stmt.executeQuery();
			
			while (rs.next()) {
				count = rs.getInt(1);
			}
		} catch (SQLException e) {
			Util.throwInternalErrorToClient(e);
		} catch (NamingException e) {
			Util.throwInternalErrorToClient(e);
		} finally {
			Util.closeDBObjects(rs, stmt, conn);
		}
		logger.exiting(CLASS_NAME, METHOD_NAME);
		return (count > 0);
	}
	
	// Returns the existing values of ROLLOUT from the CW AUX.SAP_DATATABLES_CONFIG table for the given SAP system ID.
	// The result is a list of string values. 
	public static List<String> getExportParamValuesFromCWDB(String sapSystemId) {
		Logger logger = CwApp.getLogger();
		final String METHOD_NAME = "getExportParamValuesFromCWDB(String sapSystemId)"; 
		logger.entering(CLASS_NAME, METHOD_NAME);
		
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		List<String> result = new ArrayList<String>();

		try {
			conn = CWDBConnectionFactory.getConnection();
			
			String query = "SELECT DISTINCT " + Constants.CW_COLUMN_ROLLOUT + " FROM " + Constants.CW_TABLE_DATA_TABLES_CONFIG;
			query += " WHERE " + Constants.CW_COLUMN_LEGACY_ID + " = ? ";
			query += " ORDER BY " + Constants.CW_COLUMN_ROLLOUT + " ASC";
			logger.finer(query);
			stmt = conn.prepareStatement(query);
			
			stmt.setString(1, sapSystemId);
			
			rs = stmt.executeQuery();
			
			while (rs.next()) {
				String value = rs.getString(Constants.CW_COLUMN_ROLLOUT);
				result.add(value);
			}
			conn.commit();
		} catch (SQLException e) {
			Util.throwInternalErrorToClient(e);
		} catch (NamingException e) {
			Util.throwInternalErrorToClient(e);
		} finally {
			Util.closeDBObjects(rs, stmt, conn);
		}
		logger.exiting(CLASS_NAME, METHOD_NAME);
		return result;
	}
	
	public static int moveToGT() {
		Logger logger = CwApp.getLogger();
		final String METHOD_NAME = "moveToGT()"; 
		logger.entering(CLASS_NAME, METHOD_NAME);
		
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		int rowsUpdated=0;
		
		try {
			conn = CWDBConnectionFactory.getConnection();
			String query = "UPDATE CWAPP.FIELDUSAGE fu SET fu.GT = 'X' WHERE fu.USE='INSCOPE' AND (fu.GT is null  OR length(trim(fu.GT)) = 0) " +
					"and exists (select 1 from CWAPP.TABLEUSAGE tu WHERE fu.TABLEUSAGE_ID = tu.ID AND tu.APPROVALSTATUS='APPROVED')";
			logger.finer(query);
			stmt = conn.prepareStatement(query);
			rowsUpdated = stmt.executeUpdate();
			conn.commit();
		} catch (SQLException e) {
			Util.throwInternalErrorToClient(e);
		} catch (NamingException e) {
			Util.throwInternalErrorToClient(e);
		} finally {
			Util.closeDBObjects(rs, stmt, conn);
		}
		logger.exiting(CLASS_NAME, METHOD_NAME);
		return rowsUpdated;
	}
}
