//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2009, 2014                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.is.sappack.gen.tools.sap.importer
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.tools.sap.importer;


import java.util.HashMap;
import java.util.HashSet;

import com.ibm.iis.sappack.gen.common.ui.connections.SapSystem;
import com.ibm.iis.sappack.gen.common.ui.jco.RfcDestinationDataProvider;
import com.ibm.is.sappack.gen.tools.sap.constants.Constants;
import com.ibm.is.sappack.gen.tools.sap.model.SapTable;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoTable;


public class SapLogicalTableBrowser {

	static String copyright() {
		return com.ibm.is.sappack.gen.tools.sap.importer.Copyright.IBM_COPYRIGHT_SHORT;
	}

	public static final SapTable[] getTables(String searchTerms, SapSystem selectedSapSystem) throws JCoException {
		JCoDestination destination = RfcDestinationDataProvider.getDestination(selectedSapSystem);

		// Find all tables and structures that match the given search
		// criteria
		JCoFunction function_RFC_READ_TABLE =
		      destination.getRepository().getFunction(Constants.JCO_FUNCTION_RFC_READ_TABLE);
		function_RFC_READ_TABLE.getImportParameterList().setValue(Constants.QUERY_TABLE, Constants.JCO_TABLE_DD02_T);
		function_RFC_READ_TABLE.getImportParameterList().setValue(Constants.ROWCOUNT, Constants.MAX_ROWS_RETURNED);
		function_RFC_READ_TABLE.getImportParameterList().setValue(Constants.DELIMITER, Constants.SAP_COLUMN_DELIMITER);

		JCoTable optionsTable =
		      function_RFC_READ_TABLE.getTableParameterList().getTable(Constants.JCO_RESULTTABLE_OPTIONS);
		optionsTable.appendRow();
		String curLang = destination.getLanguage();
		if (curLang == null || curLang.length() == 0) {
		   curLang = Constants.DEFAULT_USER_LANG;
		}
		String queryPart = String.format(Constants.JCO_QUERY_PART_AND_DDLANGUAGE, curLang);
		optionsTable.setValue(Constants.JCO_PARAMETER_TEXT, Constants.JCO_QUERY_PART_TABNAME_LIKE + 
		                                                    searchTerms + queryPart);

		JCoTable fieldsTable = function_RFC_READ_TABLE.getTableParameterList().getTable(Constants.JCO_RESULTTABLE_FIELDS);
		fieldsTable.appendRow();
		fieldsTable.setValue(Constants.JCO_PARAMETER_FIELDNAME, Constants.JCO_PARAMETER_TABNAME);
		fieldsTable.appendRow();
		fieldsTable.setValue(Constants.JCO_PARAMETER_FIELDNAME, Constants.JCO_PARAMETER_DDTEXT);
		function_RFC_READ_TABLE.execute(destination);
		JCoTable tableNames = function_RFC_READ_TABLE.getTableParameterList().getTable(Constants.JCO_RESULTTABLE_DATA);

		HashMap<String, SapTable> sapTablesAndStructures = new HashMap<String, SapTable>(tableNames.getNumRows());
		if (!tableNames.isEmpty()) {
			do {
				SapTable sapTableOrStructure = new SapTable(tableNames.getString(Constants.JCO_PARAMETER_WA));
				sapTablesAndStructures.put(sapTableOrStructure.getName(), sapTableOrStructure);
			}
			while (tableNames.nextRow());
		}

		HashSet<SapTable> sapTablesOnly = filterTables(searchTerms, selectedSapSystem, sapTablesAndStructures);
		return sapTablesOnly.toArray(new SapTable[sapTablesOnly.size()]);
	}

	@SuppressWarnings("unused")
	private static HashSet<SapTable> filterTablesDeprecated(String searchTerms, SapSystem selectedSapSystem,
	      HashMap<String, SapTable> sapTablesAndStructures) throws JCoException {
		// Find only tables (get them from DD02L) and eliminate all
		// structures
		JCoDestination destination = RfcDestinationDataProvider.getDestination(selectedSapSystem);
		JCoFunction function_RFC_READ_TABLE =
		      destination.getRepository().getFunction(Constants.JCO_FUNCTION_RFC_READ_TABLE);
		function_RFC_READ_TABLE.getImportParameterList().setValue(Constants.QUERY_TABLE, Constants.JCO_TABLE_DD02_L);
		function_RFC_READ_TABLE.getImportParameterList().setValue(Constants.ROWCOUNT, Constants.MAX_ROWS_RETURNED);
		function_RFC_READ_TABLE.getImportParameterList().setValue(Constants.DELIMITER, Constants.SAP_COLUMN_DELIMITER);

		JCoTable optionsTable =
		      function_RFC_READ_TABLE.getTableParameterList().getTable(Constants.JCO_RESULTTABLE_OPTIONS);
		optionsTable.appendRow();
		optionsTable.setValue(Constants.JCO_PARAMETER_TEXT, Constants.JCO_QUERY_PART_TABNAME_LIKE + searchTerms + "'"); //$NON-NLS-1$
		optionsTable.appendRow();
		optionsTable.setValue(Constants.JCO_PARAMETER_TEXT, "AND (TABCLASS IN ('TRANSP', 'CLUSTER', 'POOL')"); //$NON-NLS-1$
		optionsTable.appendRow();
		optionsTable.setValue(Constants.JCO_PARAMETER_TEXT, "OR ( tabclass = 'VIEW' AND viewclass in ( 'D', 'P' )))"); //$NON-NLS-1$
		function_RFC_READ_TABLE.getTableParameterList().setValue(Constants.JCO_RESULTTABLE_OPTIONS, optionsTable);

		JCoTable fieldsTable = function_RFC_READ_TABLE.getTableParameterList().getTable(Constants.JCO_RESULTTABLE_FIELDS);
		fieldsTable.appendRow();
		fieldsTable.setValue(Constants.JCO_PARAMETER_FIELDNAME, Constants.JCO_PARAMETER_TABNAME);

		function_RFC_READ_TABLE.execute(destination);
		JCoTable tableNames = function_RFC_READ_TABLE.getTableParameterList().getTable(Constants.JCO_RESULTTABLE_DATA);
		HashSet<SapTable> sapTables = new HashSet<SapTable>(tableNames.getNumRows());

		do {
			String tableName = tableNames.getString(Constants.JCO_PARAMETER_WA);
			SapTable sapTable = sapTablesAndStructures.get(tableName);
			if (sapTable != null) {
				sapTables.add(sapTable);
			}
		}
		while (tableNames.nextRow());
		return sapTables;
	}

	public static HashSet<SapTable> filterTables(String searchTerms, SapSystem selectedSapSystem,
	      HashMap<String, SapTable> sapTablesAndStructures) throws JCoException {
		JCoDestination destination = RfcDestinationDataProvider.getDestination(selectedSapSystem);

		JCoFunction function_RFC_READ_TABLE =
		      destination.getRepository().getFunction(Constants.JCO_FUNCTION_RFC_READ_TABLE);
		function_RFC_READ_TABLE.getImportParameterList().setValue(Constants.QUERY_TABLE, Constants.JCO_TABLE_DD02_L);
		function_RFC_READ_TABLE.getImportParameterList().setValue(Constants.ROWCOUNT, Constants.MAX_ROWS_RETURNED);
		function_RFC_READ_TABLE.getImportParameterList().setValue(Constants.DELIMITER, Constants.SAP_COLUMN_DELIMITER);

		JCoTable optionsTable =
		      function_RFC_READ_TABLE.getTableParameterList().getTable(Constants.JCO_RESULTTABLE_OPTIONS);
		optionsTable.appendRow();
		optionsTable.setValue(Constants.JCO_PARAMETER_TEXT, Constants.JCO_QUERY_PART_TABNAME_LIKE + searchTerms + "'"); //$NON-NLS-1$

		JCoTable fieldsTable = function_RFC_READ_TABLE.getTableParameterList().getTable(Constants.JCO_RESULTTABLE_FIELDS);
		fieldsTable.appendRow();
		fieldsTable.setValue(Constants.JCO_PARAMETER_FIELDNAME, Constants.JCO_PARAMETER_TABNAME);
		fieldsTable.appendRow();
		fieldsTable.setValue(Constants.JCO_PARAMETER_FIELDNAME, Constants.JCO_PARAMETER_TABCLASS);
		fieldsTable.appendRow();
		fieldsTable.setValue(Constants.JCO_PARAMETER_FIELDNAME, Constants.JCO_PARAMETER_VIEWCLASS);

		function_RFC_READ_TABLE.execute(destination);
		JCoTable tableNames = function_RFC_READ_TABLE.getTableParameterList().getTable(Constants.JCO_RESULTTABLE_DATA);
		HashSet<SapTable> sapTables = new HashSet<SapTable>(tableNames.getNumRows());

		if (!tableNames.isEmpty()) {
			do {
				String tableEntry = tableNames.getString(Constants.JCO_PARAMETER_WA);
				String tableName = filterTableEntry(tableEntry);
				if (tableName != null) {
					SapTable sapTable = sapTablesAndStructures.get(tableName);
					if (sapTable != null) {
						sapTables.add(sapTable);
					}
				}
			} while (tableNames.nextRow());
		}

		return sapTables;
	}

	private static String filterTableEntry(String tableEntry) {
		boolean isValid = true;

		// we split the sap table entry using the sap column delimiter
		String[] cols = tableEntry.split("\\" + String.valueOf(Constants.SAP_COLUMN_DELIMITER)); //$NON-NLS-1$
		String tableName = null;
		String tableClass = null;
		String viewClass = null;

		// we either have two or three strings coming from the table entry split
		// in any other case something must be wrong so we simple return nothing
		switch (cols.length) {
		case 2:

			// retrieve the two strings
			tableName = cols[0].trim();
			tableClass = cols[1].trim();

			// see if the second string matches some pre-defined values
			// (the second string being the table class identifier)
			if (!(tableClass.equals(Constants.JCO_TABCLASS_TRANSP) || tableClass.equals(Constants.JCO_TABCLASS_CLUSTER) || tableClass
			      .equals(Constants.JCO_TABCLASS_POOL))) {
				isValid = false;
			}
			break;
		case 3:

			// retrieve the three strings
			tableName = cols[0].trim();
			tableClass = cols[1].trim();
			viewClass = cols[2].trim();

			// we assume that in the case of three strings the second one (table class identifier)
			// can only contain "VIEW" since the third string contains the view class identifier
			if (tableClass.equals(Constants.JCO_TABCLASS_VIEW)) {

				// see if the the view class identifier matches some pre-defined values
				if (!(viewClass.equals(Constants.JCO_VIEWCLASS_D) || viewClass.equals(Constants.JCO_VIEWCLASS_P))) {
					isValid = false;
				}
			}

			// if the table class identifier does not equal "VIEW" but there is a view
			// class identifier something must be wrong so we just return nothing
			else {
				isValid = false;
			}
			break;
		default:
			isValid = false;
			break;
		}

		if (isValid) {
			return tableName;
		}

		return null;
	}
}
