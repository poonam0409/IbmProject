//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2009, 2012                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.is.sappack.gen.tools.sap.reports
//                                                                             
//*************************-END OF SPECIFICATIONS-**************************
package com.ibm.is.sappack.gen.tools.sap.reports;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CSVFile {

	static String copyright() {
		return Copyright.IBM_COPYRIGHT_SHORT;
	}

	public static final String OPTION_SEPARATOR_CHAR = "OPTION_SEPARATOR_CHAR"; //$NON-NLS-1$

	List<String> columnNames;
	List<Map<String, String>> oldRows;
	Map<String, String> currentRow;
	char separatorChar = ',';

	public CSVFile(List<String> columnNames, boolean includeHeader, Map<String, String> options) {
		this.columnNames = columnNames;
		oldRows = new ArrayList<Map<String, String>>();
		this.currentRow = new HashMap<String, String>();
		if (includeHeader) {
			Map<String, String> header = new HashMap<String, String>();
			for (String column : this.columnNames) {
				header.put(column, column);
			}
			oldRows.add(header);
		}
		String sep = options.get(OPTION_SEPARATOR_CHAR);
		if (!sep.isEmpty()) {
			this.separatorChar = sep.charAt(0);
		}

	}

	public void setInCurrentRow(String columnName, String value) {
		this.currentRow.put(columnName, value);

	}

	public void finishRow() {
		oldRows.add(this.currentRow);
		this.currentRow = new HashMap<String, String>();
	}

	public String getContents() {
		String NL = "\n"; //$NON-NLS-1$
		StringBuffer buf = new StringBuffer();
		for (Map<String, String> row : oldRows) {
			boolean first = true;
			for (String col : columnNames) {
				if (!first) {
					buf.append(separatorChar);
				}
				first = false;
				String value = row.get(col);
				if (value == null) {
					value = ""; //$NON-NLS-1$
				}
				buf.append(value);
			}
			buf.append(NL);
		}
		return buf.toString();
	}
}
