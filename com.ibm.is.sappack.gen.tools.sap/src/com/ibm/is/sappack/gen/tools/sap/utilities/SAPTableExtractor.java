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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

import com.ibm.is.sappack.gen.tools.sap.constants.Constants;
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
public class SAPTableExtractor {
	static String copyright() {
		return com.ibm.is.sappack.gen.tools.sap.utilities.Copyright.IBM_COPYRIGHT_SHORT;
	}

	public static interface Result {
		boolean nextRow();

		String getValue(String columnName);

		Map<String, String> getRow();
	}

	public static class EmptyResult implements Result {

		@Override
		public Map<String, String> getRow() {
			return null;
		}

		@Override
		public String getValue(String columnName) {
			return null;
		}

		@Override
		public boolean nextRow() {
			return false;
		}

	}

	JCoDestination destination;
	JCoFunction function_RFC_READ_TABLE;
	JCoFunction function_DDIF_FIELDINFO_GET;
	String tableName;
	List<String> columns;
	String whereClause;
	List<String> keyColumns;
	int maxRows;
	boolean skipLengthCheck;

	public boolean isSkipLengthCheck() {
		return skipLengthCheck;
	}

	/**
	 * skips the check if the columns are are less than 512 bytes wide.
	 * If the caller of the API knows that this is true this skips a some additional
	 * metadata calls,
	 * @param skipLengthCheck
	 */
	public void setSkipLengthCheck(boolean skipLengthCheck) {
		this.skipLengthCheck = skipLengthCheck;
	}

	private SAPTableExtractor(JCoDestination destination, String tableName, List<String> keyColumns, List<String> columns, String whereClause) throws JCoException {
		this.destination = destination;
		this.function_RFC_READ_TABLE = destination.getRepository().getFunction("RFC_READ_TABLE"); //$NON-NLS-1$
		this.function_DDIF_FIELDINFO_GET = null;
		this.tableName = tableName;
		this.columns = columns;
		this.whereClause = whereClause;
		this.keyColumns = keyColumns;
		this.maxRows = 0;
		this.skipLengthCheck = false;
	}

	public SAPTableExtractor(JCoDestination destination, String tableName, List<String> columns, String whereClause) throws JCoException {
		this(destination, tableName, null, columns, whereClause);
	}

	public SAPTableExtractor(JCoDestination destination, String tableName, List<String> columns, String whereClause, int maxRows) throws JCoException {
		this(destination, tableName, null, columns, whereClause);
		this.maxRows = maxRows;
	}

	private void determineKeyColumns() throws JCoException {
		if (this.keyColumns != null) {
			return;
		}
		this.keyColumns = new ArrayList<String>();
		JCoParameterList imports = this.function_DDIF_FIELDINFO_GET.getImportParameterList();
		imports.setValue("TABNAME", this.tableName); //$NON-NLS-1$
		this.function_DDIF_FIELDINFO_GET.execute(this.destination);
		JCoParameterList tables = this.function_DDIF_FIELDINFO_GET.getTableParameterList();
		JCoTable dfies_tab = tables.getTable("DFIES_TAB"); //$NON-NLS-1$
		do {
			boolean isKeyColumn = dfies_tab.getString(Constants.JCO_PARAMETER_KEYFLAG).equalsIgnoreCase(Constants.JCO_PARAMETER_VALUE_TRUE);
			if (isKeyColumn) {
				String fieldName = dfies_tab.getString("FIELDNAME"); //$NON-NLS-1$
				this.keyColumns.add(fieldName);
			}
		} while (dfies_tab.nextRow());
	}

	private List<List<String>> determineColumnsForCalls() throws JCoException {
		List<String> keyCols = new ArrayList<String>(this.keyColumns);
		JCoParameterList imports = this.function_RFC_READ_TABLE.getImportParameterList();
		imports.setValue("QUERY_TABLE", this.tableName); //$NON-NLS-1$
		imports.setValue("NO_DATA", "X"); //$NON-NLS-1$ //$NON-NLS-2$
		JCoParameterList tables = this.function_RFC_READ_TABLE.getTableParameterList();
		this.function_RFC_READ_TABLE.execute(this.destination);
		JCoTable fields = tables.getTable("FIELDS"); //$NON-NLS-1$
		int i = 0;
		// first get all key values
		do {
			String fieldName = fields.getString("FIELDNAME"); //$NON-NLS-1$
			if (keyCols.contains(fieldName)) {
				i += fields.getInt("LENGTH"); //$NON-NLS-1$
			}
		} while (fields.nextRow());
		int keyLength = i;

		// now compute all remaining columns
		fields.firstRow();
		List<List<String>> result = new ArrayList<List<String>>();
		List<String> currentColumns = new ArrayList<String>();
		currentColumns.addAll(keyCols);
		do {
			String fieldname = fields.getString("FIELDNAME"); //$NON-NLS-1$
			if ((!keyCols.contains(fieldname)) && this.columns.contains(fieldname)) {
				int j = fields.getInt("LENGTH"); //$NON-NLS-1$
				if (i + j > RFC_READ_TABLEWrapper.MAX_COLUMN_WIDTH) {
					result.add(currentColumns);
					i = keyLength;
					currentColumns = new ArrayList<String>(keyCols);
				}
				i += j;
				currentColumns.add(fieldname);
			}

		} while (fields.nextRow());
		if (!currentColumns.isEmpty()) {
			result.add(currentColumns);
		}
		return result;
	}

	public static class CompositeResult implements Result {
		Iterator<Map<String, String>> iterator;
		Map<String, String> currentRow = null;

		public CompositeResult(Iterator<Map<String, String>> it) {
			this.iterator = it;
		}

		@Override
		public Map<String, String> getRow() {
			return this.currentRow;
		}

		@Override
		public String getValue(String columnName) {
			return this.currentRow.get(columnName);
		}

		@Override
		public boolean nextRow() {
			boolean b = this.iterator.hasNext();
			if (b) {
				this.currentRow = this.iterator.next();
			} else {
				this.currentRow = null;
			}
			return b;
		}

	}

	public Result performQuery() throws JCoException {
		return performQuery(null);
	}

	public Result performQuery(IProgressMonitor monitor) throws JCoException {
		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}
		if (this.skipLengthCheck) {
			RFC_READ_TABLEWrapper ste = new RFC_READ_TABLEWrapper(this.destination, this.tableName, this.columns, this.whereClause, this.maxRows);
			Result res = ste.performQuery();
			return res;			
		}
		if (this.function_DDIF_FIELDINFO_GET == null) {
			this.function_DDIF_FIELDINFO_GET = this.destination.getRepository().getFunction("DDIF_FIELDINFO_GET"); //$NON-NLS-1$
		}
		determineKeyColumns();
		List<List<String>> calls = determineColumnsForCalls();
		if (calls.size() == 1) {
			RFC_READ_TABLEWrapper ste = new RFC_READ_TABLEWrapper(this.destination, this.tableName, calls.get(0), this.whereClause, this.maxRows);
			Result res = ste.performQuery();
			return res;
		}

		// use TreeMap here to have ordering by key
		Map<String, Map<String, String>> result = new TreeMap<String, Map<String, String>>();
		for (List<String> singleCall : calls) {
			if (monitor.isCanceled()) {
				return new EmptyResult();
			}
			List<String> newColumns = new ArrayList<String>();
			newColumns.addAll(singleCall);
			RFC_READ_TABLEWrapper ste = new RFC_READ_TABLEWrapper(this.destination, this.tableName, newColumns, this.whereClause, this.maxRows);
			Result res = ste.performQuery();
			while (res.nextRow()) {
				Map<String, String> row = res.getRow();
				String key = buildSingleKeyFromRow(row);
				Map<String, String> newRow = result.get(key);
				if (newRow == null) {
					newRow = new HashMap<String, String>();
					result.put(key, newRow);
				}
				newRow.putAll(row);
			}
		}
		return new CompositeResult(result.values().iterator());
	}

	private String buildSingleKeyFromRow(Map<String, String> row) {
		StringBuffer res = new StringBuffer();
		for (String key : this.keyColumns) {
			res.append(row.get(key));
		}
		return res.toString();
	}

}
