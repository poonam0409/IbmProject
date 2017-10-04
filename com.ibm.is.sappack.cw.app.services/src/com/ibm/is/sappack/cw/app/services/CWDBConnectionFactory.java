package com.ibm.is.sappack.cw.app.services;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.ibm.is.sappack.cw.app.data.Resources;
import com.ibm.is.sappack.cw.app.data.Resources.DATABASE_TYPE;

public final class CWDBConnectionFactory {
	
	private static DataSource dataSource;
	private static Connection connection;
	
	public static synchronized Connection getConnection() throws NamingException, SQLException {
		if (dataSource == null) {
			Context context = InitialContext.doLookup(Resources.INITIAL_CONTEXT_ID);
	        dataSource = (DataSource) context.lookup(Resources.CW_DATABASE_ID);
	        // Sersch: setting the timeout to 10 seconds because the user shouldn't have a reason to wait longer.
	        // This needs further testing. I didn't see this have any effect so far (default timeout is 3 min).
	        dataSource.setLoginTimeout(10);
		}
		
		if (connection == null || connection.isClosed()) {
			connection = dataSource.getConnection();
			connection.setAutoCommit(false);

			// check for the database vendor / type (e.g. DB2 or Oracle) to set the isolation level accordingly
			DATABASE_TYPE dbType = getDatabaseType(connection);
			switch(dbType) {
			case ORACLE:
				connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
				break;
			case DB2:
				connection.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
				break;
			case UNKNOWN:
				throw new SQLException("Unsupported database type");
			}
		}
		
		return connection;
	}
	
	public static DATABASE_TYPE getDatabaseType(Connection connection) throws SQLException {
		DATABASE_TYPE result = null;
		DatabaseMetaData metaData = connection.getMetaData();
		
		if (metaData.getDatabaseProductName().contains(Resources.ORACLE)) {
			result = DATABASE_TYPE.ORACLE;
		} else if (metaData.getDatabaseProductName().contains(Resources.DB2)) {
			result = DATABASE_TYPE.DB2;
		} else {
			result = DATABASE_TYPE.UNKNOWN;
		}
		return result;
	}
}
