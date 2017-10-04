//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2014                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.iis.sappack.gen.tools.jobgenerator.rgconf
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.iis.sappack.gen.tools.jobgenerator.rgconf;


public class DBPageProperties {
	private static final String PFX = "DBPage"; //$NON-NLS-1$

	public String VALUE_SUBKEY_ODBC_PASSWORD;
	public String VALUE_SUBKEY_TYPES_COMBO;
	public String VALUE_SUBKEY_ODBC_DATASOURCE;
	public String VALUE_SUBKEY_ODBC_USER;
	public String VALUE_SUBKEY_ODBC_SQL_COND;
	public String VALUE_SUBKEY_ODBC_TABLE_ACTION;
	public String VALUE_SUBKEY_ODBC_CODE_PAGE_SEL;
	public String VALUE_SUBKEY_ODBC_CODE_PAGE_NAME;
	public String VALUE_SUBKEY_ODBC_USE_ALT_SCHEMA;
	public String VALUE_SUBKEY_ODBC_ALT_SCHEMA_NAME;
	public String VALUE_SUBKEY_ODBC_AUTOCOMMIT_MODE;
	public String VALUE_SUBKEY_ODBC_RECORD_COUNT;
	public String VALUE_SUBKEY_ODBC_ARRAY_SIZE;
	public String VALUE_SUBKEY_FILE_NAME;
	public String VALUE_SUBKEY_FILE_UPDATE_MODE;
	public String VALUE_SUBKEY_FILE_FIRST_LINE_COL;
	public String VALUE_SUBKEY_FILE_CODE_PAGE_SEL;

	private boolean isSource;


  	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	


	public DBPageProperties(boolean isSource) {
		this.isSource = isSource;
		this.initializeProperties();

	}

	private void initializeProperties() {
		String pfx = PFX + ".target"; //$NON-NLS-1$
		if (this.isSource) {
			pfx = PFX + ".source"; //$NON-NLS-1$
		}
		VALUE_SUBKEY_TYPES_COMBO          = pfx + ".persistenceTypesCombo"; //$NON-NLS-1$
		VALUE_SUBKEY_ODBC_DATASOURCE      = pfx + ".odbcDataSourceText"; //$NON-NLS-1$
		VALUE_SUBKEY_ODBC_USER            = pfx + ".odbcUserText"; //$NON-NLS-1$
		VALUE_SUBKEY_ODBC_PASSWORD        = pfx + ".odbcPasswordText"; //$NON-NLS-1$
		VALUE_SUBKEY_ODBC_SQL_COND        = pfx + ".odbcSqlWhereCondText"; //$NON-NLS-1$
		VALUE_SUBKEY_ODBC_TABLE_ACTION    = pfx + ".tableActionsCombo"; //$NON-NLS-1$
		VALUE_SUBKEY_ODBC_CODE_PAGE_SEL   = pfx + ".odbcCodePageSelection"; //$NON-NLS-1$
		VALUE_SUBKEY_ODBC_CODE_PAGE_NAME  = pfx + ".odbcCodePageName"; //$NON-NLS-1$
		VALUE_SUBKEY_ODBC_USE_ALT_SCHEMA  = pfx + ".odbcUseAlternateSchema"; //$NON-NLS-1$
		VALUE_SUBKEY_ODBC_ALT_SCHEMA_NAME = pfx + ".odbcAlternateSchema"; //$NON-NLS-1$
		VALUE_SUBKEY_ODBC_RECORD_COUNT    = pfx + ".odbcRecordCount"; //$NON-NLS-1$
		VALUE_SUBKEY_ODBC_ARRAY_SIZE      = pfx + ".odbcArraySize"; //$NON-NLS-1$
		VALUE_SUBKEY_ODBC_AUTOCOMMIT_MODE = pfx + ".odbcAutoCommitOption"; //$NON-NLS-1$
		VALUE_SUBKEY_FILE_NAME            = pfx + ".fileText"; //$NON-NLS-1$
		VALUE_SUBKEY_FILE_UPDATE_MODE     = pfx + ".fileUpdateModeCombo"; //$NON-NLS-1$
		VALUE_SUBKEY_FILE_FIRST_LINE_COL  = pfx + ".firstLineColumnNamesOption"; //$NON-NLS-1$
		VALUE_SUBKEY_FILE_CODE_PAGE_SEL   = pfx + ".fileCodePageSelection"; //$NON-NLS-1$

	}

}
