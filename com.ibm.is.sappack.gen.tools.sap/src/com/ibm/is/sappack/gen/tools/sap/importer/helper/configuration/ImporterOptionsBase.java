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
// Module Name : com.ibm.is.sappack.gen.tools.sap.importer.helper.configuration
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************

package com.ibm.is.sappack.gen.tools.sap.importer.helper.configuration;

import com.ibm.iis.sappack.gen.tools.sap.rmconf.RMConfiguration.SupportedDB;
import com.ibm.is.sappack.gen.common.Constants;

public abstract class ImporterOptionsBase {

	public static final int DEFAULT_VARCHAR_LEN_FACTOR = 1;

	public enum RELATIONS {
		NoPKs_NoFKs, SAP_PKs_NoFKs, SAP_PKs_FKs, CW_PKs_NoFKs, CW_SAP_PKs_FKs, CW_PKs_FKs
	};

	public enum DATATYPES {
		USE_ALL, USE_VARCHAR_ONLY
	};

	public enum CHECKTABLEOPTIONS {
		NO_CHECKTABLES, JOINED_CHECK_AND_TEXT_TABLES, TRANSCODING_TABLES, CHECKTABLES
	};

	private RELATIONS relation = RELATIONS.NoPKs_NoFKs;
	private DATATYPES dataType;
	private int varcharLengthFactor = DEFAULT_VARCHAR_LEN_FACTOR;
	private String atomicDomainPkgName = null;
	private String columnDefaultValue;
	private CHECKTABLEOPTIONS checkTableOption;
	private boolean extractTextTables = false;
	private int abapTransferMethod = Constants.ABAP_TRANSFERMETHOD_RFC;
	private boolean enforceForeignKeys = false;
	private boolean createTechnicalFields = false;
	private boolean allowTechnicalFieldsToBeNullable = false;
	private boolean makeFKFieldsNullable = true;
	private SupportedDB targetDataBase = SupportedDB.DB2;
	private int databaseEntityMaxLength = com.ibm.is.sappack.gen.tools.sap.constants.Constants.DB_IDENTIFIER_MAX_LENGTH;

	public SupportedDB getTargetDatabase() {
		return targetDataBase;
	}

	public void setTargetDatabase(SupportedDB targetDataBase) {
		this.targetDataBase = targetDataBase;
	}

	public int getDatabaseEntityMaxLength() {
		return databaseEntityMaxLength;
	}

	public void setDatabaseEntityMaxLength(int databaseEntityMaxLength) {
		this.databaseEntityMaxLength = databaseEntityMaxLength;
	}

	public void setExtractTextTables(boolean extractTextTables) {
		this.extractTextTables = extractTextTables;
	}

	public boolean getExtractTextTables() {
		return this.extractTextTables;
	}

	public boolean isMakeFKFieldsNullable() {
		return makeFKFieldsNullable;
	}

	public void setMakeFKFieldsNullable(boolean makeFKFieldsNullable) {
		this.makeFKFieldsNullable = makeFKFieldsNullable;
	}

	static String copyright() {
		return Copyright.IBM_COPYRIGHT_SHORT;
	}

	public boolean doCreateAtomicDomainPkg() {
		return (this.atomicDomainPkgName != null);
	}

	public int getAbapTransferMethod() {
		return (this.abapTransferMethod);
	}

	public boolean isTechnicalFieldsCanBeNullable() {
		return this.allowTechnicalFieldsToBeNullable;
	}

	public String getAtomicDomainPkgName() {
		return (this.atomicDomainPkgName);
	}

	public CHECKTABLEOPTIONS getChecktableOption() {
		return this.checkTableOption;
	}

	public String getColumDefaultValue() {
		return this.columnDefaultValue;
	}

	public DATATYPES getDataTypeMode() {
		return (this.dataType);
	}

	public RELATIONS getRelationMode() {
		return (this.relation);
	}

	public int getVarcharLengthFactor() {
		return (this.varcharLengthFactor);
	}

	public boolean hasColumnDefaultValue() {
		return ((this.columnDefaultValue != null)) && (!this.columnDefaultValue.trim().isEmpty());
	}

	public boolean isCreateTechnicalFields() {
		return this.createTechnicalFields;
	}

	public boolean isEnforceForeignKey() {
		boolean doEnforceFK;

		switch (this.relation) {
		case CW_PKs_NoFKs:
		case CW_PKs_FKs:
		case CW_SAP_PKs_FKs:
			doEnforceFK = false;
			break;

		default:
			doEnforceFK = enforceForeignKeys;
		}

		return (doEnforceFK);
	}

	public void setAbapTransferMethod(int abapTransferMethod) {
		this.abapTransferMethod = abapTransferMethod;
	}

	public void setAllowTechnicalFieldsToBeNullable(boolean allowTechnicalFieldsToBeNullable) {
		this.allowTechnicalFieldsToBeNullable = allowTechnicalFieldsToBeNullable;
	}

	public void setAtomicDomainPkgName(String pkgName) {
		if (pkgName != null && pkgName.length() > 0) {
			this.atomicDomainPkgName = pkgName;
		} else {
			this.atomicDomainPkgName = null;
		}
	}

	public void setCheckTableOption(CHECKTABLEOPTIONS checktableoption) {
		this.checkTableOption = checktableoption;
	}

	public void setColumnDefaultValue(String columnDefaultValue) {
		this.columnDefaultValue = columnDefaultValue;
	}

	public void setCreateTechnicalFields(boolean createTechnicalFields) {
		this.createTechnicalFields = createTechnicalFields;
	}

	public void setDataTypeMode(DATATYPES datatype) {
		this.dataType = datatype;
	}

	public void setEnforceForeignKeys(boolean enforce) {
		this.enforceForeignKeys = enforce;
	}

	public void setRelationMode(RELATIONS relation) {
		this.relation = relation;
	}

	public void setVarcharLengthFactor(int varcharLengthFactor) {
		if (varcharLengthFactor > 0) {
			this.varcharLengthFactor = varcharLengthFactor;
		} else {
			this.varcharLengthFactor = DEFAULT_VARCHAR_LEN_FACTOR;
		}
	}

} // end of class ImporterOptionsBase
