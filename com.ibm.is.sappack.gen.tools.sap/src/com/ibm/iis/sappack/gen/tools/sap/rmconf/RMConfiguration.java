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
// Module Name : com.ibm.iis.sappack.gen.tools.sap.rmconf
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.iis.sappack.gen.tools.sap.rmconf;


import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

import com.ibm.iis.sappack.gen.common.ui.CWConstants;
import com.ibm.iis.sappack.gen.common.ui.editors.ConfigurationBase;
import com.ibm.iis.sappack.gen.common.ui.editors.PropertiesConstants;
import com.ibm.iis.sappack.gen.common.ui.editors.PropertyInfo;
import com.ibm.iis.sappack.gen.common.ui.editors.PropertyInfoCollection;
import com.ibm.iis.sappack.gen.common.ui.validators.ValidatorBase;
import com.ibm.iis.sappack.gen.tools.sap.rmconf.CWImportOptionsPage.DataTypes;
import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.common.ui.ModeManager;
import com.ibm.is.sappack.gen.common.ui.RMRGMode;
import com.ibm.is.sappack.gen.tools.sap.constants.Constants;
import com.ibm.is.sappack.gen.tools.sap.importer.helper.configuration.ImporterOptionsBase.CHECKTABLEOPTIONS;
import com.ibm.is.sappack.gen.tools.sap.importer.helper.configuration.ImporterOptionsBase.DATATYPES;
import com.ibm.is.sappack.gen.tools.sap.importer.helper.configuration.ImporterOptionsBase;
import com.ibm.is.sappack.gen.tools.sap.importer.helper.configuration.TableImporterOptions;
import com.ibm.is.sappack.gen.tools.sap.importer.helper.field.TechnicalField;
import com.ibm.is.sappack.gen.tools.sap.importer.helper.field.TechnicalFieldInteger;
import com.ibm.is.sappack.gen.tools.sap.importer.helper.field.TechnicalFieldVarchar;
import com.ibm.is.sappack.gen.tools.sap.importer.idoctypes.IDocImporterOptions;


public class RMConfiguration extends ConfigurationBase {

  	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	

	public static final String RMCONF_FILE_EXTENSION = "rmcfg"; //$NON-NLS-1$

	public static enum SupportedDB {
		DB2, Oracle, Custom, DB2forCW
	}

	public final static int CW_PROFILE_IDX_STAGING   = 0;
	public final static int CW_PROFILE_IDX_ALIGNMENT = 1;
	public final static int CW_PROFILE_IDX_PRELOAD   = 2;
	public final static int CW_PROFILE_IDX_CUSTOM    = 3;

	public RMConfiguration(IResource res) throws IOException, CoreException {
		super(res, RMCONF_FILE_EXTENSION);
	}

	/*
	public RMConfiguration(ModelStoreMap map, IResource res) {
		super(map, res);
	}
	*/

	private SupportedDB getSelectedDB() {
		int selectedDBInt = this.getInt(TargetDBPage.PROP_TARGETDB_DBNAME);
		SupportedDB selectedDB = null;
		for (SupportedDB db : SupportedDB.values()) {
			if (db.ordinal() == selectedDBInt) {
				selectedDB = db;
				break;
			}
		}
		
		return selectedDB;
	}
	
	public TableImporterOptions getTableImportOptions() {
		TableImporterOptions options = null;
		RMRGMode mode = ModeManager.getActiveMode();
		if (mode.getID().equals(ModeManager.CW_MODE_ID)) {
			options = CWImportOptionsPage.getTableImporterOptions(this);
		}
		if (mode.getID().equals(ModeManager.DEFAULT_MODE_ID)) {
			options = SAPPackImportOptionsPage.getTableImporterOptions(this);
		}
		if (mode.getID().equals(ModeManager.MODELLING_MODE_ID)) {
			options = SAPPackImportOptionsPage.getTableImporterOptions(this);
		}
		if (options == null) {
			throw new UnsupportedOperationException(MessageFormat.format(Messages.RMConfiguration_0, mode == null ? "UNKNOWN" : mode.getName())); //$NON-NLS-1$
		}
		
		options.setTargetDatabase(getSelectedDB());
		options.setDatabaseEntityMaxLength(getInt(TargetDBPage.PROP_TARGETDB_MAXLENGTH));
		return options;
	}

	public IDocImporterOptions getIDocOptions() {
		RMRGMode mode = this.getMode();
		IDocImporterOptions idocOptions = null;
		if (mode.getID().equals(ModeManager.CW_MODE_ID)) {
			TableImporterOptions options = getTableImportOptions();
			idocOptions = new IDocImporterOptions();
			idocOptions.setCreateIDocLoadModel(true);
			idocOptions.setCreateIDocExtractModel(true);
			idocOptions.setAbapTransferMethod(options.getAbapTransferMethod());
			idocOptions.setAddSegmentCheckTables(//
					options.getChecktableOption() == CHECKTABLEOPTIONS.CHECKTABLES //
							|| options.getChecktableOption() == CHECKTABLEOPTIONS.JOINED_CHECK_AND_TEXT_TABLES //
					);
			idocOptions.setAllowTechnicalFieldsToBeNullable(options.getAllowColumnsToBeNullable());
			idocOptions.setAtomicDomainPkgName(options.getAtomicDomainPkgName());
			idocOptions.setChecktableBlackList(options.getTableBlackList());
			idocOptions.setCheckTableOption(options.getChecktableOption());
			idocOptions.setCheckTablesPackageName("Package1"); // TODO //$NON-NLS-1$
			idocOptions.setColumnDefaultValue(options.getColumDefaultValue());
			// not needed: idocOptions.setCreateIDocExtractModel(false); 
			// not needed: idocOptions.setCreateIDocLoadModel(false);
			idocOptions.setCreateTechnicalFields(options.isCreateTechnicalFields());
			idocOptions.setCreateTranslationTables( //
					options.getChecktableOption() == CHECKTABLEOPTIONS.TRANSCODING_TABLES //
					);
			idocOptions.setDataTypeMode(options.getDataTypeMode());
			idocOptions.setEnforceForeignKeys(options.isEnforceForeignKey());
			idocOptions.setMakeFKFieldsNullable(options.isMakeFKFieldsNullable());
			idocOptions.setRelationMode(options.getRelationMode());
			idocOptions.setSegmentFieldsDefaultValue(options.getColumDefaultValue());
			idocOptions.setSegmentFieldsNullable(options.getAllowColumnsToBeNullable());
			idocOptions.setUseVarcharTypeOnly(options.getDataTypeMode() == DATATYPES.USE_VARCHAR_ONLY);
			idocOptions.setVarcharLengthFactor(options.getVarcharLengthFactor());
			idocOptions.setDatabaseEntityMaxLength(options.getDatabaseEntityMaxLength());
			idocOptions.setTargetDatabase(options.getTargetDatabase());
		} else {
			idocOptions = SAPPackImportOptionsPage.getIDocImporterOptions(this);
			idocOptions.setTargetDatabase(getSelectedDB());
			idocOptions.setDatabaseEntityMaxLength(getInt(TargetDBPage.PROP_TARGETDB_MAXLENGTH));
			
		}
		return idocOptions;
	}

	public String getPackagePath() {
		return LdmPackageSelectionPage.getPackagePath(this);
	}
	
	public String getCheckTablePackagePath() {
		return this.get(CheckTablePackageSelectionPage.CHECK_TABLE_PACKAGE_PATH);
	}


	public List<TechnicalField> getTechnicalFields() {
		TableImporterOptions options = getTableImportOptions();
		boolean cwCreateFields = options.isCreateTechnicalFields();
		
		boolean cwTechFieldsNullable = options.isTechnicalFieldsCanBeNullable();
		List<TechnicalField> tfs = new ArrayList<TechnicalField>();
		if (cwCreateFields) {
			List<TechnicalField> cwtfs = getCWTechnicalFields(cwCreateFields, cwTechFieldsNullable);
			tfs.addAll(cwtfs);
		}
		List<TechnicalField> tfs2 = TechnicalFieldsPage.getTechnicalFields(this);
		tfs.addAll(tfs2);
		return tfs;
	}

	public static List<TechnicalField> getCWTechnicalFields(boolean createAdditionalFields, boolean additionalFieldsNullable) {
		List<TechnicalField> technicalFields = new ArrayList<TechnicalField>();
		if (createAdditionalFields) {
			String desc = Messages.Utils_4;
			technicalFields.add(new TechnicalFieldVarchar(CWConstants.CW_TECHNICAL_FIELD_PREFIX + "LEGACY_ID", desc, 20, false, additionalFieldsNullable)); //$NON-NLS-1$
			technicalFields.add(new TechnicalFieldVarchar(CWConstants.CW_TECHNICAL_FIELD_PREFIX + "LEGACY_UK", desc, 120, false, additionalFieldsNullable)); //$NON-NLS-1$
			technicalFields.add(new TechnicalFieldVarchar(CWConstants.CW_TECHNICAL_FIELD_PREFIX + "LOB", desc, 10, false, additionalFieldsNullable)); //$NON-NLS-1$
			technicalFields.add(new TechnicalFieldVarchar(CWConstants.CW_TECHNICAL_FIELD_PREFIX + "RLOUT", desc, 30, false, additionalFieldsNullable)); //$NON-NLS-1$
			technicalFields.add(new TechnicalFieldVarchar(CWConstants.CW_TECHNICAL_FIELD_PREFIX + "CATEGORY", desc, 30, false, additionalFieldsNullable)); //$NON-NLS-1$
			technicalFields.add(new TechnicalFieldInteger(CWConstants.CW_TECHNICAL_FIELD_PREFIX + "VERSION", desc, false, additionalFieldsNullable)); //$NON-NLS-1$
			technicalFields.add(new TechnicalFieldInteger(CWConstants.CW_TECHNICAL_FIELD_PREFIX + "LOAD_ID", desc, false, additionalFieldsNullable)); //$NON-NLS-1$
		}
		return technicalFields;
	}

	@Override
	public PropertyInfoCollection createPropertyInfoCollection() {
		// TODO
		List<PropertyInfo> allProps = new ArrayList<PropertyInfo>();

		List<PropertyInfo> targetDBProps = Arrays.asList(new PropertyInfo[] {
				//
				new PropertyInfo(TargetDBPage.PROP_TARGETDB_DBNAME, Messages.RMConfiguration_2,
						Messages.RMConfiguration_3,
						SupportedDB.DB2.ordinal()),
				new PropertyInfo(TargetDBPage.PROP_TARGETDB_MAXLENGTH, Messages.RMConfiguration_4,
						Messages.RMConfiguration_5, Constants.DB_IDENTIFIER_MAX_LENGTH),
		//
				});
		setLocation(targetDBProps, TargetDBPage.TABNAME);
		allProps.addAll(targetDBProps);
		final String EMPTY = ""; //$NON-NLS-1$

		List<PropertyInfo> sapPackImportOptionsProps = Arrays.asList(new PropertyInfo[] { //
				new PropertyInfo(SAPPackImportOptionsPage.SETTING_IDOC_NULLABLE_FIELDS_OPTION, EMPTY, EMPTY, false),
						new PropertyInfo(SAPPackImportOptionsPage.SETTING_IDOC_FIELDS_OPTION_LENGTH_FACTOR, EMPTY, EMPTY, "0"), //$NON-NLS-1$
						new PropertyInfo(SAPPackImportOptionsPage.SETTING_IDOC_CHECK_TABLE_OPTION, EMPTY, EMPTY, false),
						new PropertyInfo(SAPPackImportOptionsPage.SETTING_IDOC_TEXT_TABLE_OPTION, EMPTY, EMPTY, false),
						new PropertyInfo(SAPPackImportOptionsPage.SETTING_IDOC_FIELD_OPTION_ALLOW_ALL_TYPES, EMPTY, EMPTY, 0),
						new PropertyInfo(SAPPackImportOptionsPage.SETTING_TABLES_CHECK_TABLE_OPTION, EMPTY, EMPTY, false),
						new PropertyInfo(SAPPackImportOptionsPage.SETTING_TABLES_CHECK_TABLE_RECURSIVE, EMPTY, EMPTY, false),

						new PropertyInfo(SAPPackImportOptionsPage.SETTING_TABLES_TEXT_TABLE_OPTION, EMPTY, EMPTY, false),
						new PropertyInfo(SAPPackImportOptionsPage.SETTING_TABLES_EXTRACT_RELATIONS, EMPTY, EMPTY, false),
						new PropertyInfo(SAPPackImportOptionsPage.SETTING_TABLES_ENFORCE_RELATIONS, EMPTY, EMPTY, false),
				//
				});
		setLocation(sapPackImportOptionsProps, SAPPackImportOptionsPage.TABNAME);
		allProps.addAll(sapPackImportOptionsProps);

		List<PropertyInfo> cwImportOptionsProps = Arrays.asList(new PropertyInfo[] {
				//
				new PropertyInfo(CWImportOptionsPage.SETTING_BLACK_OR_WHILTELIST_BUTTON, EMPTY, EMPTY, 0), 

				new PropertyInfo(CWImportOptionsPage.SETTING_RELATION_IDX, EMPTY, EMPTY, 0), //
				new PropertyInfo(CWImportOptionsPage.SETTING_COL_TYPE_IDX, EMPTY, EMPTY, DataTypes.VarCharType.ordinal()), //

				new PropertyInfo(CWImportOptionsPage.SETTING_CHK_TBL_OPT_IDX, EMPTY, EMPTY, CWImportOptionsPage.ChkTblOptions.JoinedAndCheckTables.ordinal()),
				new PropertyInfo(CWImportOptionsPage.SETTING_COL_LEN_FACTOR, EMPTY, EMPTY, 1), //
				new PropertyInfo(CWImportOptionsPage.SETTING_COL_IS_NULLABLE, EMPTY, EMPTY, false),

				new PropertyInfo(CWImportOptionsPage.SETTING_ADD_TECH_FIELDS, EMPTY, EMPTY, true),

				new PropertyInfo(CWImportOptionsPage.SETTING_TFLD_IS_NULLABLE, EMPTY, EMPTY, true),

		//
				});
		setLocation(cwImportOptionsProps, CWImportOptionsPage.TABNAME);
		allProps.addAll(cwImportOptionsProps);

		// package page
		List<PropertyInfo> packageProps = Arrays.asList(new PropertyInfo[] {//
				//
				new PropertyInfo(PropertiesConstants.KEY_PACKAGE_PATH, EMPTY, EMPTY, EMPTY), // LdmPackageSelectionPage.TREE_SEP + LdmPackageSelectionPage.DEFAULT_PACKAGE_NAME ), // 
				//
				});
		setLocation(packageProps, LdmPackageSelectionPage.TABNAME);
		allProps.addAll(packageProps);

		List<PropertyInfo> ctPackageProps = Arrays.asList(new PropertyInfo[] {//
				//
				new PropertyInfo(CheckTablePackageSelectionPage.CHECK_TABLE_PACKAGE_PATH, EMPTY, EMPTY, EMPTY), // LdmPackageSelectionPage.TREE_SEP + LdmPackageSelectionPage.DEFAULT_PACKAGE_NAME ), //
				//
				});
		setLocation(ctPackageProps, CheckTablePackageSelectionPage.TABNAME);
		allProps.addAll(ctPackageProps);

		
		// technical fields page
		List<PropertyInfo> techFieldsProps = Arrays.asList(new PropertyInfo[] {//
				//
						new PropertyInfo(TechnicalFieldsPage.SETTING_NUMBER_OF_TECH_FIELDS, Messages.RMConfiguration_7, Messages.RMConfiguration_8), //
						new PropertyInfo(TechnicalFieldsPage.SETTING_TECH_FIELD_OPTION, EMPTY, EMPTY, false), // 
				//
				});
		setLocation(techFieldsProps, TechnicalFieldsPage.TABNAME);
		allProps.addAll(techFieldsProps);

		PropertyInfoCollection result = new PropertyInfoCollection();
		for (PropertyInfo pi : allProps) {
			result.addPropertyInfo(pi);
		}
		return result;
	}

	@Override
	public ValidatorBase createValidator() {
		return new RMConfigurationValidator();
	}

	public static Map<String, String> createEmptyRMConfiguation(RMRGMode mode) {
		return null;
	}

	public String checkDBObjectLength() {
		ImporterOptionsBase options;
		int                 maxLen;
		String              errMsg;
		
		errMsg = null;
		try {
			options = getTableImportOptions();
			maxLen  = options.getDatabaseEntityMaxLength();
			if (maxLen < Constants.DB_IDENTIFIER_MIN_LENGTH) {
				errMsg = MessageFormat.format(Messages.RMConfiguration_9, Constants.DB_IDENTIFIER_MIN_LENGTH);
			}
			else {
				if (maxLen > Constants.DB_IDENTIFIER_MAX_LENGTH) {
					errMsg = MessageFormat.format(Messages.RMConfiguration_10, Constants.DB_IDENTIFIER_MAX_LENGTH);
				}
			}
		} // end of try
		catch(NumberFormatException numberFormatExcpt) {
			errMsg = MessageFormat.format(Messages.RMConfiguration_11, Constants.DB_IDENTIFIER_MIN_LENGTH, Constants.DB_IDENTIFIER_MAX_LENGTH);
		}
		
		return(errMsg);
	}
	
}
