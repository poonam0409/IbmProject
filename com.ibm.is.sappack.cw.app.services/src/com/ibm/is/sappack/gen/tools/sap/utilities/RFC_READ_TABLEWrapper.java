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
// Module Name : com.ibm.is.sappack.gen.tools.sap.utilities
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.tools.sap.utilities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoTable;

/**
 * This class reads the contents of an SAP table.
 * 
 * It encapsulates the RFC_READ_TABLE function module.
 * 
 * Example: SAPTableExtractor ex = new SAPTableExtractor(des, "DD05Q",
 * Arrays.asList(new String[] { "FORKEY", "CHECKFIELD", "FIELDNAME" }),
 * "CHECKTABLE = 'T005'"); SAPTableExtractor.Result result = ex.performQuery();
 * while (result.nextRow()) { String s = result.getValue("FIELDNAME"); ... }
 */
public class RFC_READ_TABLEWrapper {
	public static final int MAX_COLUMN_WIDTH = 512;

	JCoDestination destination;
	JCoFunction function_RFC_READ_TABLE;
	String tableName;
	List<String> columns;
	String whereClause;
	int maxRows;

	public RFC_READ_TABLEWrapper(JCoDestination destination, String tableName, List<String> columns, String whereClause) throws JCoException {
		this(destination, tableName, columns, whereClause, 0);
	}
	
	public RFC_READ_TABLEWrapper(JCoDestination destination, String tableName, List<String> columns, String whereClause, int maxRows) throws JCoException {
		this.destination = destination;
		this.function_RFC_READ_TABLE = destination.getRepository().getFunction("RFC_READ_TABLE"); //$NON-NLS-1$
		this.tableName = tableName;
		this.columns = columns;
		this.whereClause = whereClause;
		this.maxRows = maxRows;
	}

	private static class ResultImpl implements IResult {

		int currentRowPosition = 0;
		
		JCoTable data;
		List<String> columns;
		int[] columnLength;

		String dataString;
		Map<String, String> currentValue;
		boolean done = false;

		private ResultImpl(int[] colummnLenth, JCoTable data, List<String> columns) {
			this.columnLength = colummnLenth;
			this.data = data;
			this.columns = columns;
			this.done = data.getNumRows() == 0;
		}

		public boolean nextRow() {
			if (done) {
				return false;
			}
			int ix = 0;
			dataString = data.getString("WA"); //$NON-NLS-1$
			currentValue = new HashMap<String, String>();
			int i = 0;
			for (String column : columns) {
				int length = columnLength[i];
				i++;
				String value = null;
				if (ix > dataString.length()) {
					value = ""; //$NON-NLS-1$
				} else if (ix + length > dataString.length()) {
					value = dataString.substring(ix);
				} else {
					value = dataString.substring(ix, ix + length);
				}
				ix += length;
				value = value.trim();
				currentValue.put(column, value);
			}
			if (!data.nextRow()) {
				done = true;
			}
			
			currentRowPosition++;
			
			return true;
		}

		public String getValue(String columnName) {
			return this.currentValue.get(columnName);
		}

		public Map<String, String> getRow() {
			return currentValue;
		}

		@Override
		public int getCurrentRowPosition() {
			return currentRowPosition;
		}

		@Override
		public int getNumberOfRows() {
			return data.getNumRows();
		}
	}

	public IResult performQuery() throws JCoException {
		JCoParameterList imports = this.function_RFC_READ_TABLE.getImportParameterList();
		imports.setValue("QUERY_TABLE", this.tableName); //$NON-NLS-1$
		if (this.maxRows > 0) {
			imports.setValue("ROWCOUNT", maxRows); //$NON-NLS-1$
		}
		JCoParameterList tables = this.function_RFC_READ_TABLE.getTableParameterList();
		JCoTable fields = tables.getTable("FIELDS"); //$NON-NLS-1$
		JCoTable options = tables.getTable("OPTIONS"); //$NON-NLS-1$
		for (String column : columns) {
			fields.appendRow();
			fields.setValue("FIELDNAME", column); //$NON-NLS-1$
		}
		final int optionsLength = 72;

		if (whereClause != null) {
			int start = 0;
			boolean done = false;
			while (!done) {
				int ix = 0;
				if (start + optionsLength >= whereClause.length()) {
					ix = whereClause.length();
					done = true;
				} else {
					ix = whereClause.lastIndexOf(" ", start + optionsLength); //$NON-NLS-1$
					if (ix == -1) {
						throw new RuntimeException("WHERE clause too long"); //$NON-NLS-1$
					}
				}
				String optionEntry = whereClause.substring(start, ix);
				start = ix;

				options.appendRow();
				options.setValue("TEXT", optionEntry); //$NON-NLS-1$
			}
		}

		this.function_RFC_READ_TABLE.execute(this.destination);

		JCoTable data = tables.getTable("DATA"); //$NON-NLS-1$

		// collect string positions
		fields.firstRow();
		int rowLength = 0;
		int[] columnLengths = new int[columns.size()];
		for (int i = 0; i < columnLengths.length; i++) {
			columnLengths[i] = fields.getInt("LENGTH"); //$NON-NLS-1$
			rowLength += columnLengths[i];
			fields.nextRow();
		}
		// RFC_READ_TABLE can only read rows with length up to 512
		if (rowLength > MAX_COLUMN_WIDTH) {
			throw new RuntimeException("Row size too large ( > " + MAX_COLUMN_WIDTH + ")"); //$NON-NLS-1$ //$NON-NLS-2$
		}

		IResult res = new ResultImpl(columnLengths, data, columns);

		return res;
	}

}
