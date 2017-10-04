//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2009, 2011                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.is.sappack.gen.tools.sap.tableextract
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.tools.sap.tableextract;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.datatools.modelbase.sql.datatypes.CharacterStringDataType;
import org.eclipse.datatools.modelbase.sql.datatypes.DataType;
import org.eclipse.datatools.modelbase.sql.datatypes.DateDataType;
import org.eclipse.datatools.modelbase.sql.datatypes.NumericalDataType;
import org.eclipse.datatools.modelbase.sql.tables.Column;
import org.eclipse.datatools.modelbase.sql.tables.Table;

import com.ibm.db.models.db2.luw.LUWColumn;
import com.ibm.iis.sappack.gen.common.ui.connections.SapSystem;
import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.tools.sap.activator.Activator;
import com.ibm.is.sappack.gen.tools.sap.utilities.SAPTableExtractor;
import com.ibm.is.sappack.gen.tools.sap.utilities.SAPTableExtractor.Result;

public class DatabaseTableExtractor extends TableExtractor {
	static String copyright() {
		return com.ibm.is.sappack.gen.tools.sap.tableextract.Copyright.IBM_COPYRIGHT_SHORT;
	}

	Connection jdbcConnection;
	boolean generateDeleteStatement;
	int commitCount;
	boolean continueOnError;

	public DatabaseTableExtractor(List<Table> tables, SapSystem sapSystem, Connection jdbcConnection, boolean generateDeleteStatement, boolean continueOnError, int commitCount, int maxRows) {
		super(tables, sapSystem, maxRows);
		this.jdbcConnection = jdbcConnection;
		this.generateDeleteStatement = generateDeleteStatement;
		this.commitCount = commitCount;
		this.continueOnError = continueOnError;
	}

	@Override
	protected void processTable(Table table, String insertStatementPrefix, Result sapResult, IProgressMonitor monitor) {
		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}
		try {
			insertTableDirectly(table, insertStatementPrefix, sapResult, monitor);
		} catch (SQLException e) {
			Activator.getLogger().log(Level.SEVERE, Messages.LogExceptionMessage, e);
			throw new RuntimeException(e);
		}
	}

	
	private void insertTableDirectly(Table table, String insertStatementPrefix, SAPTableExtractor.Result sapResult, IProgressMonitor monitor) throws SQLException {
		List<Column> columns = new ArrayList<Column>(table.getColumns());
		String insertStatement = insertStatementPrefix + "VALUES ("; //$NON-NLS-1$
		for (int i = 0; i < columns.size(); i++) {
			if (i > 0) {
				insertStatement += ", "; //$NON-NLS-1$
			}
			insertStatement += "?"; //$NON-NLS-1$
		}
		insertStatement += ")"; //$NON-NLS-1$

		String fullTableName = table.getSchema().getName() + "." + table.getName(); //$NON-NLS-1$
		this.jdbcConnection.createStatement().execute("DELETE FROM " + fullTableName); //$NON-NLS-1$

		PreparedStatement prep = jdbcConnection.prepareStatement(insertStatement);

		int tuplesSinceLastCommit = 0;
		while (sapResult.nextRow()) {
			Map<String, String> row = sapResult.getRow();
			for (int i = 0; i < columns.size(); i++) {
				Column col = columns.get(i);
				String val = row.get(col.getName());
				Object jdbcCobject = this.convertStringValueToJDBCObject(col, val);
				prep.setObject(i + 1, jdbcCobject);
			}
			try {
				prep.execute();
			} catch (SQLException sqlexc) {
				if (this.continueOnError) {
					Activator.getLogger().log(Level.INFO, "An SQL Exception occurred, ignoring it", sqlexc); //$NON-NLS-1$
					sqlexc.printStackTrace();
				} else {
					throw sqlexc;
				}
			}
			if (monitor.isCanceled()) {
				jdbcConnection.rollback();
				return;
			}
			tuplesSinceLastCommit++;
			if (commitCount > 0 && tuplesSinceLastCommit >= commitCount) {
				tuplesSinceLastCommit = 0;
				jdbcConnection.commit();
			}
		}
		jdbcConnection.commit();
		prep.close();
		prep = null;
	}

	public static String shortenStringToUTF8Length(String val, int maxUTF8ByteLength) {
		final String ENC = "UTF-8"; //$NON-NLS-1$
		try {
			byte[] utf8bytes = val.getBytes(ENC);
			int utf8Len = utf8bytes.length;
			if (utf8Len > maxUTF8ByteLength) {
				// String constructor cuts off invalid bytes at the end
				val = new String(utf8bytes, 0, maxUTF8ByteLength, ENC);
			}
		} catch (UnsupportedEncodingException exc) {
			exc.printStackTrace();
			throw new RuntimeException(exc);
		}
		return val;
	}

	Object convertStringValueToJDBCObject(Column col, String value) {
		if (value == null) {
			return null;
		}
		DataType dt = col.getDataType();
		if (dt instanceof CharacterStringDataType) {
			CharacterStringDataType csdt = (CharacterStringDataType) dt;
			if (col instanceof LUWColumn) {
				// DB2 workaround
				int l = csdt.getLength();
				value = shortenStringToUTF8Length(value, l);
			}
			return value;
		} else if (dt instanceof DateDataType) {
			String day = value.substring(0, 2);
			String month = value.substring(2, 4);
			String year = value.substring(4);
			java.sql.Date d = java.sql.Date.valueOf(year + "-" + month + "-" + day); //$NON-NLS-1$ //$NON-NLS-2$
			return d;
		} else if (dt instanceof NumericalDataType) {
			// skip leading '*' symbols
			int i = 0;
			while (value.charAt(i) == '*') {
				i++;
			}
			String doubleVal = value.substring(i);
			Double d = new Double(doubleVal);
			return d;
		}
		return value;
	}

	@SuppressWarnings("nls")
	public static void main(String[] args) {
		try {
			String[] strings = new String[] { "abcde", "äböanrü", "\u1234ä" };

			for (String s : strings) {
				System.out.println("Original String: " + s);
				for (int i = 0; i <= s.length()*2; i++) {
					String shortened = shortenStringToUTF8Length(s, i);
					System.out.println("String at max length " + i + ": '" + shortened + "'");
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
