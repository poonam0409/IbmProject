package com.ibm.is.sappack.cw.app.data;

import java.sql.Connection;
import java.sql.DatabaseMetaData;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.transaction.UserTransaction;

import org.apache.openjpa.persistence.OpenJPAEntityManager;
import org.apache.openjpa.persistence.OpenJPAPersistence;

import com.ibm.is.sappack.cw.app.data.Resources.DATABASE_TYPE;

public class JPAResourceFactory {

	private static EntityManager entityManager;
	private static UserTransaction userTransaction;

	public static synchronized EntityManager getEntityManager() {
		try {
			if (JPAResourceFactory.entityManager == null) {
				Context context = (javax.naming.Context) InitialContext.doLookup(Resources.INITIAL_CONTEXT_ID);
				entityManager = (EntityManager) context.lookup(Resources.PERSISTENCE_CONTEXT_ID);
			}
			return JPAResourceFactory.entityManager;
		} catch (NamingException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static synchronized UserTransaction getUserTransaction() {
		try {
			if (JPAResourceFactory.userTransaction == null) {
				Context context = (javax.naming.Context) InitialContext.doLookup(Resources.INITIAL_CONTEXT_ID);
				userTransaction = (UserTransaction) context.lookup(Resources.USERTRANSACTION_CONTEXT_ID);
			}
			return JPAResourceFactory.userTransaction;
		} catch (NamingException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	// Not used right now
	public static DATABASE_TYPE getDatabaseType() {
		Connection conn = null;
		
		try {
			EntityManager mgr = getEntityManager();
			OpenJPAEntityManager kmgr = OpenJPAPersistence.cast(mgr);
			
			conn = (Connection) kmgr.getConnection();
			
			DatabaseMetaData metaData = conn.getMetaData();
			
			if (metaData.getDatabaseProductName().contains(Resources.ORACLE)) {
				return DATABASE_TYPE.ORACLE;
			}
			else if (metaData.getDatabaseProductName().contains(Resources.DB2)) {
				return DATABASE_TYPE.DB2;
			}
			
			return DATABASE_TYPE.UNKNOWN;
		}
		catch (Exception e) {
			e.printStackTrace();
			return DATABASE_TYPE.UNKNOWN;
		}
		finally {
			try {
				if (conn != null && !conn.isClosed()) {
					conn.close();
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
