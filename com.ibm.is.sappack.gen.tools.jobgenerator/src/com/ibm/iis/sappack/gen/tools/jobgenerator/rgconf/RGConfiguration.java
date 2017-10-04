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


import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

import com.ibm.iis.sappack.gen.common.ui.editors.ConfigurationBase;
import com.ibm.iis.sappack.gen.common.ui.editors.PropertiesConstants;
import com.ibm.iis.sappack.gen.common.ui.editors.PropertyInfo;
import com.ibm.iis.sappack.gen.common.ui.editors.PropertyInfoCollection;
import com.ibm.iis.sappack.gen.common.ui.util.Utils;
import com.ibm.iis.sappack.gen.common.ui.validators.ValidatorBase;
import com.ibm.iis.sappack.gen.tools.jobgenerator.rgconf.validator.RGConfValidator;
import com.ibm.is.sappack.gen.common.Constants;
import com.ibm.is.sappack.gen.common.DSTypeDBMMapping;
import com.ibm.is.sappack.gen.common.FileStageData;
import com.ibm.is.sappack.gen.common.ODBCStageData;
import com.ibm.is.sappack.gen.common.PersistenceData;
import com.ibm.is.sappack.gen.common.request.JobParamData;
import com.ibm.is.sappack.gen.common.request.SAPSystemData;
import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.common.ui.ModeManager;
import com.ibm.is.sappack.gen.common.ui.RMRGMode;
import com.ibm.is.sappack.gen.tools.jobgenerator.Activator;


public class RGConfiguration extends ConfigurationBase {
	public static final String RGCONF_FILE_EXTENSION = "rgcfg"; //$NON-NLS-1$

	public static enum CONFIGURATION_TYPE {
		SAP_EXTRACT, SAP_LOAD, MOVE
	};

	final String EMPTY = ""; //$NON-NLS-1$

  	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}


  	private List<PropertyInfo> createDBPropertyInfos(boolean isSource) {
		DBPageProperties properties = new DBPageProperties(isSource);
		List<PropertyInfo> propsDB = Arrays.asList(new PropertyInfo[] {
				//
				new PropertyInfo(properties.VALUE_SUBKEY_ODBC_DATASOURCE,
				                 Messages.RGConfiguration_0, Messages.RGConfiguration_1),
				new PropertyInfo(properties.VALUE_SUBKEY_ODBC_USER,
				                 Messages.RGConfiguration_2, Messages.RGConfiguration_3),
				new PropertyInfo(properties.VALUE_SUBKEY_ODBC_PASSWORD,
				                 Messages.RGConfiguration_4, Messages.RGConfiguration_5, PropertyInfo.TYPE_NAME_ENCRYPTED, EMPTY),
				new PropertyInfo(properties.VALUE_SUBKEY_ODBC_CODE_PAGE_NAME,
					              Messages.RGConfiguration_6, Messages.RGConfiguration_7),
				new PropertyInfo(properties.VALUE_SUBKEY_FILE_NAME,
					              Messages.RGConfiguration_8, Messages.RGConfiguration_9), //
				new PropertyInfo(properties.VALUE_SUBKEY_ODBC_TABLE_ACTION, EMPTY, EMPTY, 0),
				new PropertyInfo(properties.VALUE_SUBKEY_ODBC_CODE_PAGE_SEL, EMPTY, EMPTY, 0), //
				new PropertyInfo(properties.VALUE_SUBKEY_ODBC_USE_ALT_SCHEMA, EMPTY, EMPTY, false), //
				new PropertyInfo(properties.VALUE_SUBKEY_ODBC_ALT_SCHEMA_NAME,
					              Messages.RGConfiguration_10, Messages.RGConfiguration_11, EMPTY), //
  				new PropertyInfo(properties.VALUE_SUBKEY_ODBC_RECORD_COUNT,
					              Messages.RGConfiguration_92, Messages.RGConfiguration_93, ODBCStageData.DEFAULT_RECORD_COUNT), //
  				new PropertyInfo(properties.VALUE_SUBKEY_ODBC_AUTOCOMMIT_MODE, EMPTY, EMPTY, ODBCStageData.DEFAULT_AUTO_COMMIT_MODE), //
				new PropertyInfo(properties.VALUE_SUBKEY_ODBC_ARRAY_SIZE,
					              Messages.RGConfiguration_94, Messages.RGConfiguration_95, ODBCStageData.DEFAULT_ARRAY_SIZE), //
				new PropertyInfo(properties.VALUE_SUBKEY_ODBC_SQL_COND, EMPTY, EMPTY, EMPTY), //
				new PropertyInfo(properties.VALUE_SUBKEY_FILE_UPDATE_MODE, EMPTY, EMPTY, 0), //
				new PropertyInfo(properties.VALUE_SUBKEY_FILE_FIRST_LINE_COL, EMPTY, EMPTY, false), //
				new PropertyInfo(properties.VALUE_SUBKEY_FILE_CODE_PAGE_SEL, EMPTY, EMPTY, 0), //
				new PropertyInfo(properties.VALUE_SUBKEY_TYPES_COMBO, EMPTY, EMPTY, 0), //
		//		
		});
		
		return propsDB;
	}
	
	@Override
	protected PropertyInfoCollection createPropertyInfoCollection() {
		List<PropertyInfo> propertyDescriptions = new ArrayList<PropertyInfo>();
		List<PropertyInfo> propertyDescriptionsSAPConn = Arrays.asList(new PropertyInfo[] {
				//
				new PropertyInfo(PropertiesConstants.PROP_SAP_CONN_NAME, Messages.RGConfiguration_12, Messages.RGConfiguration_13),
				new PropertyInfo(PropertiesConstants.PROP_SAP_USER, Messages.RGConfiguration_14, Messages.RGConfiguration_15), 
				new PropertyInfo(PropertiesConstants.PROP_SAP_PASSWORD, Messages.RGConfiguration_16, Messages.RGConfiguration_17, PropertyInfo.TYPE_NAME_ENCRYPTED, EMPTY),
				new PropertyInfo(PropertiesConstants.PROP_SAP_CLIENT, Messages.RGConfiguration_18, Messages.RGConfiguration_19),
				new PropertyInfo(PropertiesConstants.PROP_SAP_LANGUAGE, Messages.RGConfiguration_20, Messages.RGConfiguration_21),
		//
				});

		List<PropertyInfo> propDescABAP = Arrays
				.asList(new PropertyInfo[] {
						//
						new PropertyInfo(ABAPExtractSettingsPage.PROP_RFC_DEST_TEXT, Messages.RGConfiguration_22,
								Messages.RGConfiguration_23),
						new PropertyInfo(ABAPExtractSettingsPage.PROP_BG_ENABLED, Messages.RGConfiguration_24,
								Messages.RGConfiguration_25, false),

						new PropertyInfo(ABAPExtractSettingsPage.PROP_SAP_GATEWAYHOST, Messages.RGConfiguration_26, Messages.RGConfiguration_27),
						new PropertyInfo(ABAPExtractSettingsPage.PROP_SAP_GATEWAYSERVICE, Messages.RGConfiguration_28, Messages.RGConfiguration_29),
						new PropertyInfo(ABAPExtractSettingsPage.PROP_TRANSFER_METHOD, Messages.RGConfiguration_30,
								Messages.RGConfiguration_31, 0),
						new PropertyInfo(ABAPExtractSettingsPage.PROP_CPIC_USE_SAP_LOGON_DETAILS, Messages.RGConfiguration_32, Messages.RGConfiguration_33, false),
						new PropertyInfo(ABAPExtractSettingsPage.PROP_CPIC_USER, Messages.RGConfiguration_34, Messages.RGConfiguration_35),
						new PropertyInfo(ABAPExtractSettingsPage.PROP_CPIC_PASSWORD, Messages.RGConfiguration_36, Messages.RGConfiguration_37),
						new PropertyInfo(ABAPExtractSettingsPage.PROP_CPIC_MAX_FLOWS, Messages.RGConfiguration_38, Messages.RGConfiguration_39, "2"), //$NON-NLS-1$
						new PropertyInfo(ABAPExtractSettingsPage.PROP_CREATE_RFC_DEST_AUTOMATICALLY, Messages.RGConfiguration_41,
								Messages.RGConfiguration_42, false),
						new PropertyInfo(ABAPExtractSettingsPage.PROP_DELETE_RFC_DEST_IF_EXIST, Messages.RGConfiguration_43,
								Messages.RGConfiguration_44, false),
						new PropertyInfo(ABAPExtractSettingsPage.PROP_RFC_NAME_DYN_OR_STATIC, EMPTY, EMPTY, 0),
						new PropertyInfo(ABAPExtractSettingsPage.PROP_DYNAMIC_RFC_PREFIX, Messages.RGConfiguration_45,
								Messages.RGConfiguration_46),
						new PropertyInfo(ABAPExtractSettingsPage.PROP_MAX_FLOWS_RFC, Messages.RGConfiguration_47, Messages.RGConfiguration_48, "2"), //$NON-NLS-1$
						new PropertyInfo(ABAPExtractSettingsPage.PROP_BG_VARIANT_PREFIX, Messages.RGConfiguration_50,
								Messages.RGConfiguration_51),
						new PropertyInfo(ABAPExtractSettingsPage.PROP_STARTUP_DELAY, Messages.RGConfiguration_52,
								Messages.RGConfiguration_53)
				//
				});

		List<PropertyInfo> propDescCWIDocLoad = Arrays.asList(new PropertyInfo[] {
			//
			new PropertyInfo(CWIDocLoadPage.PROP_IDOC_LOAD_STATUS_CHECKED,
			                 Messages.RGConfiguration_54, Messages.RGConfiguration_55, false),
			new PropertyInfo(CWIDocLoadPage.PROP_IDOC_LOAD_SURROGATE_KEY_FILE,
			                 Messages.RGConfiguration_56, Messages.RGConfiguration_57),
			new PropertyInfo(CWIDocLoadPage.PROP_IDOC_LOAD_OBJECT_NAME,
			                 Messages.RGConfiguration_58, Messages.RGConfiguration_59)
			//
		});

		// add properties that can appear on different tabs dependent on the configuration type
		if (getConfigurationType() == CONFIGURATION_TYPE.SAP_EXTRACT) {
			setLocation(propertyDescriptionsSAPConn, RGConfEditor.TAB_SAP_SOURCE);
			propertyDescriptions.addAll(propertyDescriptionsSAPConn);
			List<PropertyInfo> propsDB = createDBPropertyInfos(false);
			setLocation(propsDB, RGConfEditor.TAB_DB_TARGET);
			propertyDescriptions.addAll(propsDB);
		} else if (getConfigurationType() == CONFIGURATION_TYPE.SAP_LOAD) {
			setLocation(propertyDescriptionsSAPConn, RGConfEditor.TAB_SAP_TARGET);
			propertyDescriptions.addAll(propertyDescriptionsSAPConn);
			List<PropertyInfo> propsDB = createDBPropertyInfos(true);
			setLocation(propsDB, RGConfEditor.TAB_DB_SOURCE);
			propertyDescriptions.addAll(propsDB);
		} else if (getConfigurationType() == CONFIGURATION_TYPE.MOVE) {
			List<PropertyInfo> srcPropsDB = createDBPropertyInfos(true);
			setLocation(srcPropsDB, RGConfEditor.TAB_DB_SOURCE);
			propertyDescriptions.addAll(srcPropsDB);

			List<PropertyInfo> tgtPropsDB = createDBPropertyInfos(false);
			setLocation(tgtPropsDB, RGConfEditor.TAB_DB_TARGET);
			propertyDescriptions.addAll(tgtPropsDB);
		}

		// add properties that always appear on the same tab
		// if the configuration does not show this page, this doesn't hurt

		// move
		List<PropertyInfo> moveProps = Arrays.asList(new PropertyInfo[] {
			//
			new PropertyInfo(MoveTranscodeSettingsPage.PROP_MOVE_REJECT_FILE_PATH,
			                 Messages.RGConfiguration_60, Messages.RGConfiguration_61),
			new PropertyInfo(MoveTranscodeSettingsPage.PROP_MOVE_MAX_NUMBER_FLOWS,
			                 Messages.RGConfiguration_62, Messages.RGConfiguration_63, "1"), //$NON-NLS-1$
			//
		});
		setLocation(moveProps, MoveTranscodeSettingsPage.TABNAME);
		propertyDescriptions.addAll(moveProps);
		
		// transcode
		List<PropertyInfo> transcodeProps = Arrays.asList(new PropertyInfo[] {
			//
			new PropertyInfo(MoveTranscodeSettingsPage.PROP_TRANSCODE_TARGET_LEGACY_ID,
			                 Messages.RGConfiguration_65, Messages.RGConfiguration_66, EMPTY),
			new PropertyInfo(MoveTranscodeSettingsPage.PROP_TRANSCODE_REFERENCE_FIELDS,
			                 Messages.RGConfiguration_67, Messages.RGConfiguration_68, false),
			new PropertyInfo(MoveTranscodeSettingsPage.PROP_TRANSCODE_NON_REFERENCE_FIELDS,
			                 Messages.RGConfiguration_69, Messages.RGConfiguration_70, false),
			new PropertyInfo(MoveTranscodeSettingsPage.PROP_TRANCODE_DOMAIN_VALUE_FIELDS,
			                 Messages.RGConfiguration_71, Messages.RGConfiguration_72, false),
			new PropertyInfo(MoveTranscodeSettingsPage.PROP_MARK_UNMAPPED_VALUES,
			                 Messages.RGConfiguration_90, Messages.RGConfiguration_91, true),
			//	
		});
		setLocation(transcodeProps, MoveTranscodeSettingsPage.TABNAME);
		propertyDescriptions.addAll(transcodeProps);
		

		// ABAP
		setLocation(propDescABAP, ABAPExtractSettingsPage.TABNAME);
		propertyDescriptions.addAll(propDescABAP);

		RMRGMode mode = getMode();
		if (mode == null) {
			String msg = MessageFormat.format(Messages.RGConfEditor_4, getResource().getFullPath());
			Activator.getLogger().log(Level.WARNING, msg);
			throw new RuntimeException(msg);
		}

		if (mode.getID().equals(ModeManager.CW_MODE_ID)) {
			setLocation(propDescCWIDocLoad, CWIDocLoadPage.TABNAME);
			propertyDescriptions.addAll(propDescCWIDocLoad);
		}

		// Derivations
		List<PropertyInfo> propDerivations = Arrays.asList(new PropertyInfo[] {
			//
			new PropertyInfo(CustomDerivationsPage.CUSTOM_DERIVATIONS_ENABLED,
			                 Messages.RGConfiguration_73, Messages.RGConfiguration_74, false),
			new PropertyInfo(CustomDerivationsPage.CUSTOM_DERIVATIONS_EXCEPTIONS_TEXT,
			                 Messages.RGConfiguration_75, Messages.RGConfiguration_76),
			//
		});
		setLocation(propDerivations, CustomDerivationsPage.TABNAME);
		propertyDescriptions.addAll(propDerivations);

		// Job descriptions
		List<PropertyInfo> propsJobDescriptionPage = Arrays.asList(new PropertyInfo[] {
			//
			new PropertyInfo(JobDescriptionPage.WIDGET_NAME_DESC_ENABLED, 
			                 Messages.RGConfiguration_77, Messages.RGConfiguration_78, false),
			new PropertyInfo(JobDescriptionPage.WIDGET_NAME_EDITOR_TEXT,
			                 Messages.RGConfiguration_79, Messages.RGConfiguration_80),
			//
		});
		setLocation(propsJobDescriptionPage, JobDescriptionPage.TABNAME);
		propertyDescriptions.addAll(propsJobDescriptionPage);
		
		// parameters
		List<PropertyInfo> propsParameterPage = Arrays.asList(new PropertyInfo[] {
			new PropertyInfo(ParameterPage.PROP_PARAMETER_NUM, EMPTY, EMPTY, 0),
		});
		setLocation(propsParameterPage, ParameterPage.TABNAME);
		propertyDescriptions.addAll(propsParameterPage);
		

		// prepare result
		PropertyInfoCollection result = new PropertyInfoCollection();
		for (PropertyInfo pi : propertyDescriptions) {
			result.addPropertyInfo(pi);
		}
		return result;
	}

	public RGConfiguration(IResource res) throws IOException, CoreException {
		super(res, RGCONF_FILE_EXTENSION);
	}

	/*
	public RGConfiguration(ModelStoreMap map, IResource res) throws IOException, CoreException {
		super(map, res);
	}
	*/

	public CONFIGURATION_TYPE getConfigurationType() {
		String type = this.get(PropertiesConstants.KEY_RGCFG_TYPE);
		if (type.equals(PropertiesConstants.VALUE_RGCFG_TYPE_SAP_EXTRACT)) {
			return CONFIGURATION_TYPE.SAP_EXTRACT;
		}
		if (type.equals(PropertiesConstants.VALUE_RGCFG_TYPE_SAP_LOAD)) {
			return CONFIGURATION_TYPE.SAP_LOAD;
		}
		if (type.equals(PropertiesConstants.VALUE_RGCFG_TYPE_MOVE_TRANSCODE)) {
			return CONFIGURATION_TYPE.MOVE;
		}
		return null;
	}

	public static String getConfigurationTypeAsString(CONFIGURATION_TYPE configType) {
		String result = null;
		switch (configType) {
		case SAP_EXTRACT:
			result = Messages.RGConfiguration_81;
			break;
		case SAP_LOAD:
			result = Messages.RGConfiguration_82;
			break;
		case MOVE:
			result = Messages.RGConfiguration_83;
			break;
		default:
			result = Messages.RGConfiguration_84;

		}

		return result;
	}

	public Map<String, String> getCustomDerivations() {
		Map<String, String> result = new HashMap<String, String>();

		boolean customDerivationsEnabled = this.getBoolean(CustomDerivationsPage.CUSTOM_DERIVATIONS_ENABLED);
		Set<String>  allDSTypeNamesSet = DSTypeDBMMapping.getDataStageODBCTypeNames();
		List<String> allDSTypeNameList = new ArrayList<String>(allDSTypeNamesSet);
		Collections.sort(allDSTypeNameList);
		for (String dsType : allDSTypeNameList) {
			String exp = null;
			if (customDerivationsEnabled) {
				exp = this.get(CustomDerivationsPage.DERIVATION_PFX + dsType);
			}
			if (exp == null) {
				exp = CustomDerivationsPage.DEFAULT_DERIVATION;
			}
		
//			exp = exp.replaceAll("\\" + CustomDerivationsContentProvider.REPLACE_DERIVATION, "{0}"); //$NON-NLS-1$ //$NON-NLS-2$
			exp = exp.trim();
			
			result.put(dsType, exp);
		}
		return result;
	}

	public String getJobDescription() {
		String  jobDesc = null;
		String  isDescEnabled = this.get(JobDescriptionPage.WIDGET_NAME_DESC_ENABLED);
		
		if (isDescEnabled != null && isDescEnabled.equalsIgnoreCase(Boolean.TRUE.toString())) {
			jobDesc = this.get(JobDescriptionPage.WIDGET_NAME_EDITOR_TEXT);
		}
		
		return(jobDesc);
	}
	
	public Map<String, String> getDerivationExceptions() throws IOException {
		String derivExc = this.get(CustomDerivationsPage.CUSTOM_DERIVATIONS_EXCEPTIONS_TEXT);
		if (derivExc == null) {
			return new HashMap<String, String>();
		}
		return CustomDerivationsPage.getDerivationExceptions(derivExc);
	}

	public int getABAPTransferMethod() {
		int ix = Utils.getComboIndex(this, ABAPExtractSettingsPage.PROP_TRANSFER_METHOD, ABAPExtractSettingsPage.TRANSFER_METHOD_RFC_CPIPC);
		if (ix > 0 && ix < ABAPExtractSettingsPage.TRANSFER_METHOD_RFC_CPIPC.length) {
			if (ABAPExtractSettingsPage.TRANSFER_METHOD_RFC_CPIPC[ix].equals(ABAPExtractSettingsPage.TF_METHOD_NAME_CPIC)) {
				return Constants.ABAP_TRANSFERMETHOD_CPIC;
			}
		}
		return Constants.ABAP_TRANSFERMETHOD_RFC;
	}

	public List<JobParamData> getJobParams() {
		List<JobParamData> result = new ArrayList<JobParamData>();
		List<JobParameter> params = ParameterPage.loadParameters(this, false);
		for (JobParameter param : params) {
			int type = JobParamData.JOB_PARAM_TYPE_STRING;
			String typeString = param.getType();
			if (typeString.equals(JobParameter.TYPE_NAME_DATE)) {
				type = JobParamData.JOB_PARAM_TYPE_DATE;
			} else if (typeString.equals(JobParameter.TYPE_NAME_FLOAT)) {
				type = JobParamData.JOB_PARAM_TYPE_FLOAT;
			} else if (typeString.equals(JobParameter.TYPE_NAME_INTEGER)) {
				type = JobParamData.JOB_PARAM_TYPE_INTEGER;
			} else if (typeString.equals(JobParameter.TYPE_NAME_LIST)) {
				type = JobParamData.JOB_PARAM_TYPE_LIST;
			} else if (typeString.equals(JobParameter.TYPE_NAME_PARAMETER_SET)) {
				type = JobParamData.JOB_PARAM_TYPE_PARAM_SET;
			} else if (typeString.equals(JobParameter.TYPE_NAME_PATHNAME)) {
				type = JobParamData.JOB_PARAM_TYPE_PATHNAME;
			} else if (typeString.equals(JobParameter.TYPE_NAME_STRING)) {
				type = JobParamData.JOB_PARAM_TYPE_STRING;
			} else if (typeString.equals(JobParameter.TYPE_NAME_TIME)) {
				type = JobParamData.JOB_PARAM_TYPE_TIME;
			} else if (typeString.equals(JobParameter.TYPE_NAME_ENCRYPTED)) {
				type = JobParamData.JOB_PARAM_TYPE_ENCYRYPTED;
			}
			JobParamData jpd = new JobParamData(param.getName(), param.getPrompt(), type, param.getDefaultValue(), param.getHelp());
			result.add(jpd);
		}
		return result;
	}

	public boolean getIDocLoadStatus() {
		return this.getBoolean(CWIDocLoadPage.PROP_IDOC_LOAD_STATUS_CHECKED);
	}

	public String getIDocLoadStatusSurrogateKeyFile() {
		return this.get(CWIDocLoadPage.PROP_IDOC_LOAD_SURROGATE_KEY_FILE);
	}

	public String getIDocLoadStatusObjectName() {
		return this.get(CWIDocLoadPage.PROP_IDOC_LOAD_OBJECT_NAME);
	}

	private SAPSystemData getSAPSystem() {
		String systemName = this.get(PropertiesConstants.PROP_SAP_CONN_NAME);
		String user = this.get(PropertiesConstants.PROP_SAP_USER);
		String pw = this.get(PropertiesConstants.PROP_SAP_PASSWORD);
		String client = this.get(PropertiesConstants.PROP_SAP_CLIENT);
		String lang = this.get(PropertiesConstants.PROP_SAP_LANGUAGE);

		SAPSystemData result = new SAPSystemData(systemName, user, pw, client, lang);
		return result;
	}

	public SAPSystemData getSourceSAPSystem() {
		if (this.getConfigurationType() == CONFIGURATION_TYPE.SAP_EXTRACT) {
			return getSAPSystem();
		}
		return null;
	}

	public SAPSystemData getTargetSAPSystem() {
		if (this.getConfigurationType() == CONFIGURATION_TYPE.SAP_LOAD) {
			return getSAPSystem();
		}
		return null;
	}

	
	private PersistenceData getPersistenceData(boolean isSource) {
		DBPageProperties dbProperties = new DBPageProperties(isSource);
		int ix = Utils.getComboIndex(this, dbProperties.VALUE_SUBKEY_TYPES_COMBO, DBPage.PERSISTENCE_TYPES_ARR);
		if (ix == -1) {
			throw new RuntimeException(Messages.RGConfiguration_85);
		}
		PersistenceData result = null;
		if (DBPage.PERSISTENCE_TYPES_ARR[ix].equals(DBPage.PERSISTENCE_TYPE_ODBC)) {
			String odbcDSName = this.get(dbProperties.VALUE_SUBKEY_ODBC_DATASOURCE);
			String user = this.get(dbProperties.VALUE_SUBKEY_ODBC_USER);
			String pw = this.get(dbProperties.VALUE_SUBKEY_ODBC_PASSWORD);
			ODBCStageData odbc = new ODBCStageData(odbcDSName, user, pw);

			String altSchema = this.get(dbProperties.VALUE_SUBKEY_ODBC_ALT_SCHEMA_NAME);
			boolean useAltSchema = this.getBoolean(dbProperties.VALUE_SUBKEY_ODBC_USE_ALT_SCHEMA);
			if (useAltSchema) {
				odbc.setAlternateSchemaName(altSchema);
			}

			String recordCount = this.get(dbProperties.VALUE_SUBKEY_ODBC_RECORD_COUNT);
			odbc.setRecordCount(recordCount);
			String arraySize = this.get(dbProperties.VALUE_SUBKEY_ODBC_ARRAY_SIZE);
			odbc.setArraySize(arraySize);
			boolean isAutoCommit = this.getBoolean(dbProperties.VALUE_SUBKEY_ODBC_AUTOCOMMIT_MODE);
			odbc.setAutoCommit(isAutoCommit);
			
			String sqlCond = this.get(dbProperties.VALUE_SUBKEY_ODBC_SQL_COND);
			if (sqlCond != null && !sqlCond.trim().isEmpty()) {
				odbc.setSQLWhereCondition(sqlCond);
			}

			int cpIdx = Utils.getComboIndex(this, dbProperties.VALUE_SUBKEY_ODBC_CODE_PAGE_SEL, DBPage.ODBC_CODE_PAGE_TYPES_ARR);
			if (cpIdx > -1) {
				if (DBPage.ODBC_CODE_PAGE_TYPES_ARR[cpIdx].equals(DBPage.ODBC_CODE_PAGE_TYPE_DEFAULT)) {
					odbc.setCodePageId(ODBCStageData.CODE_PAGE_DEFAULT);
				} else if (DBPage.ODBC_CODE_PAGE_TYPES_ARR[cpIdx].equals(DBPage.ODBC_CODE_PAGE_TYPE_UNICODE)) {
					odbc.setCodePageId(ODBCStageData.CODE_PAGE_UNICODE);
				} else if (DBPage.ODBC_CODE_PAGE_TYPES_ARR[cpIdx].equals(DBPage.ODBC_CODE_PAGE_TYPE_USER_DEFINED)) {
					String cpName = this.get(dbProperties.VALUE_SUBKEY_ODBC_CODE_PAGE_NAME);
					odbc.setCodePageName(cpName);
				}
			}

			int tableActionIdx = Utils.getComboIndex(this, dbProperties.VALUE_SUBKEY_ODBC_TABLE_ACTION, DBPage.ODBC_TABLE_ACTIONS_ARR);
			if (tableActionIdx > -1) {
				if (DBPage.ODBC_TABLE_ACTIONS_ARR[tableActionIdx].equals(DBPage.ODBC_WRITE_TABLE_ACTION_APPEND)) {
					odbc.setInsertMode(ODBCStageData.INSERT_MODE_APPEND);
				} else if (DBPage.ODBC_TABLE_ACTIONS_ARR[tableActionIdx].equals(DBPage.ODBC_WRITE_TABLE_ACTION_TRUNCATE)) {
					odbc.setInsertMode(ODBCStageData.INSERT_MODE_TRUNCATE);
				}
			}
			result = odbc;

		} else {
			String filenamePrefix = this.get(dbProperties.VALUE_SUBKEY_FILE_NAME);
			boolean firstLineColNames = this.getBoolean(dbProperties.VALUE_SUBKEY_FILE_FIRST_LINE_COL);
			FileStageData file = new FileStageData(filenamePrefix, firstLineColNames);
			int updateIx = Utils.getComboIndex(this, dbProperties.VALUE_SUBKEY_FILE_UPDATE_MODE, DBPage.FILE_UPDATE_MODE_ARR);
			if (updateIx > -1) {
				if (DBPage.FILE_UPDATE_MODE_ARR[updateIx].equals(DBPage.FILE_UPDATE_MODE_APPEND)) {
					file.setUpdateMode(FileStageData.UPDATE_MODE_APPEND);
				} else if (DBPage.FILE_UPDATE_MODE_ARR[updateIx].equals(DBPage.FILE_UPDATE_MODE_CREATE)) {
					file.setUpdateMode(FileStageData.UPDATE_MODE_CREATE);
				} else if (DBPage.FILE_UPDATE_MODE_ARR[updateIx].equals(DBPage.FILE_UPDATE_MODE_OVERWRITE)) {
					file.setUpdateMode(FileStageData.UPDATE_MODE_OVERWRITE);
				}
			}

			int cpIx = Utils.getComboIndex(this, dbProperties.VALUE_SUBKEY_FILE_CODE_PAGE_SEL, DBPage.FILE_CODE_PAGE_TYPES_ARR);
			if (cpIx > -1) {
				if (DBPage.FILE_CODE_PAGE_TYPES_ARR[cpIx].equals(DBPage.FILE_CODE_PAGE_PROJECT_DEFAULT)) {
					file.setCodePageId(FileStageData.CODE_PAGE_DEFAULT);
				} else if (DBPage.FILE_CODE_PAGE_TYPES_ARR[cpIx].equals(DBPage.FILE_CODE_PAGE_PROJECT_ISO8859_1)) {
					file.setCodePageId(FileStageData.CODE_PAGE_ISO8859_1);
				} else if (DBPage.FILE_CODE_PAGE_TYPES_ARR[cpIx].equals(DBPage.FILE_CODE_PAGE_PROJECT_UTF8)) {
					file.setCodePageId(FileStageData.CODE_PAGE_UTF8);
				} else if (DBPage.FILE_CODE_PAGE_TYPES_ARR[cpIx].equals(DBPage.FILE_CODE_PAGE_PROJECT_UTF16)) {
					file.setCodePageId(FileStageData.CODE_PAGE_UTF16);
				}
			}
			result = file;
		}
		return result;
	}
	
	

	public PersistenceData getSourcePersistenceData() {
		if (this.getConfigurationType() == CONFIGURATION_TYPE.SAP_EXTRACT) {
			return null;
		}

		return getPersistenceData(true);
	}

	public PersistenceData getTargetPersistenceData() {
		if (this.getConfigurationType() == CONFIGURATION_TYPE.SAP_LOAD) {
			return null;
		}

		return getPersistenceData(false);
	}

	public String getCPICUser() {
		return this.get(ABAPExtractSettingsPage.PROP_CPIC_USER);
	}

	public String getCPICPassword() {
		return this.get(ABAPExtractSettingsPage.PROP_CPIC_PASSWORD);
	}

	public boolean getCPICUseSAPLogonDetails() {
		return this.getBoolean(ABAPExtractSettingsPage.PROP_CPIC_USE_SAP_LOGON_DETAILS);
	}

	public boolean getCreateRFCDestinationAutomatically() {
		return this.getBoolean(ABAPExtractSettingsPage.PROP_CREATE_RFC_DEST_AUTOMATICALLY);
	}

	public boolean getDeleteExistingRFCDestination() {
		return this.getBoolean(ABAPExtractSettingsPage.PROP_DELETE_RFC_DEST_IF_EXIST);
	}

	public boolean getGenerateRFCDestinationNames() {
		return this.getInt(ABAPExtractSettingsPage.PROP_RFC_NAME_DYN_OR_STATIC) != 0;
	}

	public int getNumberOfFlows_RFC() {
		return this.getInt(ABAPExtractSettingsPage.PROP_MAX_FLOWS_RFC);
	}

	public int getNumberOfFlows_CPIC() {
		return this.getInt(ABAPExtractSettingsPage.PROP_CPIC_MAX_FLOWS);
	}

	public String getBGVariantPrefix() {
		return this.get(ABAPExtractSettingsPage.PROP_BG_VARIANT_PREFIX);
	}

	public int getBGStartDelay() {
		return this.getInt(ABAPExtractSettingsPage.PROP_STARTUP_DELAY);
	}

	public boolean getBGUseSAPVariant() {
		return false;
	}

	public String getGWHost() {
		return this.get(ABAPExtractSettingsPage.PROP_SAP_GATEWAYHOST);
	}

	public String getGWService() {
		return this.get(ABAPExtractSettingsPage.PROP_SAP_GATEWAYSERVICE);
	}

	public String getRFCDestinationNameGeneratorPrefix() {
		return this.get(ABAPExtractSettingsPage.PROP_DYNAMIC_RFC_PREFIX);
	}

	public String getStaticRFCDestinations(Map<String, String> outResult) {
		return ABAPExtractSettingsPage.loadRFCDestinationNameMap(this, outResult);
	}

	@Override
	public ValidatorBase createValidator() {
		return new RGConfValidator();
	}

	public boolean getUseBackground() {
		return this.getBoolean(ABAPExtractSettingsPage.PROP_BG_ENABLED);
	}

	public String getRejectFilePath() {
		return this.get(MoveTranscodeSettingsPage.PROP_MOVE_REJECT_FILE_PATH);
	}

	public boolean isTranscodingConfig() {
		boolean isTranscodingCfg = isTranscodeDomainValueFields()  ||
		                           isTranscodeNonReferenceFields() ||
		                           isTranscodeReferenceFields();
		return(isTranscodingCfg);
	}
	
	public boolean isMarkUnmappedValues() {
		return this.getBoolean(MoveTranscodeSettingsPage.PROP_MARK_UNMAPPED_VALUES);
	}
	
	public boolean isTranscodeReferenceFields() {
		return this.getBoolean(MoveTranscodeSettingsPage.PROP_TRANSCODE_REFERENCE_FIELDS);
	}
	
	public boolean isTranscodeNonReferenceFields() {
		return this.getBoolean(MoveTranscodeSettingsPage.PROP_TRANSCODE_NON_REFERENCE_FIELDS);
	}
	
	public boolean isTranscodeDomainValueFields() {
		return this.getBoolean(MoveTranscodeSettingsPage.PROP_TRANCODE_DOMAIN_VALUE_FIELDS);
	}
	
	public String getTargetSystemLegacyID() {
		return this.get(MoveTranscodeSettingsPage.PROP_TRANSCODE_TARGET_LEGACY_ID);
	}
	
	public int getNumberOfFlows_Move() {
		String s = this.get(MoveTranscodeSettingsPage.PROP_MOVE_MAX_NUMBER_FLOWS);
		int result;
		try {
			result = Integer.parseInt(s);
		} catch (NumberFormatException exc) {
			result = -1;
		}
		return result;
	}

}
