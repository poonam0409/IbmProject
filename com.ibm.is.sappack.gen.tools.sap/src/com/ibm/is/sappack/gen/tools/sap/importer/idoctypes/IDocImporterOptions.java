//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2011, 2014                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.is.sappack.gen.tools.sap.importer.idoctypes
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.tools.sap.importer.idoctypes;


import java.util.List;

import com.ibm.is.sappack.gen.tools.sap.importer.helper.configuration.ImporterOptionsBase;


public class IDocImporterOptions extends ImporterOptionsBase {

	private boolean      createIDocExtractModel   = true;
	private boolean      createIDocLoadModel      = false;
	private boolean      useVarcharTypeOnly       = false;
	private boolean      addSegmentCheckTables    = false;
	private boolean      createTranslationTables  = false;
	private boolean      createCorrectMIHModel    = false;
	private String       segmentTablesPackageName = null;
	private String       checkTablesPackageName   = null;
	private boolean      segmentFieldsNullable    = false;
	private List<String> checkTableBlackList      = null;

   
	static String copyright() {
		return com.ibm.is.sappack.gen.tools.sap.importer.idoctypes.Copyright.IBM_COPYRIGHT_SHORT;
	}

	public boolean isCreateIDocExtractModel() {
		return createIDocExtractModel;
	}

	public void setCreateIDocExtractModel(boolean createIDocExtractModel) {
		this.createIDocExtractModel = createIDocExtractModel;
	}

	public boolean isCreateIDocLoadModel() {
		return createIDocLoadModel;
	}

	public void setCreateIDocLoadModel(boolean createIDocLoadModel) {
		this.createIDocLoadModel = createIDocLoadModel;
	}

	public boolean isUseVarcharTypeOnly() {
		return useVarcharTypeOnly;
	}

	public void setUseVarcharTypeOnly(boolean useVarcharTypeOnly) {
		this.useVarcharTypeOnly = useVarcharTypeOnly;
	}

	public boolean isAddSegmentCheckTables() {
		return addSegmentCheckTables;
	}

	public void setAddSegmentCheckTables(boolean addSegmentCheckTables) {
		this.addSegmentCheckTables = addSegmentCheckTables;
	}

	public boolean isCreateTranslationTables() {
		return createTranslationTables;
	}

	public void setCreateTranslationTables(boolean createTranslationTables) {
		this.createTranslationTables = createTranslationTables;
	}

	public String getSegmentTablesPackageName() {
		return segmentTablesPackageName;
	}

	public void setSegmentTablesPackageName(String segmentTablesPackageName) {
		this.segmentTablesPackageName = segmentTablesPackageName;
	}

	public String getCheckTablesPackageName() {
		return checkTablesPackageName;
	}

	public void setCheckTablesPackageName(String checkTablesPackageName) {
		this.checkTablesPackageName = checkTablesPackageName;
	}

	public boolean isSegmentFieldsNullable() {
		return segmentFieldsNullable;
	}

	public void setSegmentFieldsNullable(boolean segmentFieldsNullable) {
		this.segmentFieldsNullable = segmentFieldsNullable;
	}

	public String getSegmentFieldsDefaultValue() {
		return(getColumDefaultValue());
	}

	public void setSegmentFieldsDefaultValue(String segmentFieldsDefaultValue) {
		setColumnDefaultValue(segmentFieldsDefaultValue);
	}

	public void setChecktableBlackList(List<String> checkTableBlacklist) {
		this.checkTableBlackList = checkTableBlacklist;
	}
	
	public List<String> getChecktableBlackList() {
		return this.checkTableBlackList;
	}

	public void setCreateCorrectMIHModelOption(boolean createCorrectMIHModel) {
		this.createCorrectMIHModel = createCorrectMIHModel;
	}

	public boolean getCreateCorrectMIHModel() {
		return this.createCorrectMIHModel;
	}

}
