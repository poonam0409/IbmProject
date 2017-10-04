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
// Module Name : com.ibm.is.sappack.gen.common
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.common;


import java.util.ResourceBundle;
import java.util.regex.Pattern;


public final class Constants {
	// -------------------------------------------------------------------------------------
	// J O B  G E N E R A T I O N
	// -------------------------------------------------------------------------------------
	public  static final String   EMPTY_STRING                       = "";               //$NON-NLS-1$
	public  static final int      JOB_GEN_DEFAULT_IS_SERVER_PORT     = 9080;
	public  static final int      JOB_GEN_HTTPS_IS_SERVER_PORT       = 9443;
	public  static final int      JOB_GEN_DEFAULT_DS_RPC_PORT        = 31538;
	public  static final int      JOB_GEN_DEFAULT_FLOWS_PER_JOB      = 1;
	public  static final int      JOB_GEN_DEFAULT_IDOC_PER_TRANS     = 1;
	public  static final char     MODEL_VERSION_ALWAYS_ALLOWED_CHAR  = '!';
	public  static final int      CHAR_DEFAULT_LENGTH                = 1;
	public  static final String   CODE_PAGE_ISO_8859_1               = "ASCL_ISO8859-1"; //$NON-NLS-1$
	public  static final String   CODE_PAGE_UTF_8                    = "UTF-8";          //$NON-NLS-1$
	public  static final String   CODE_PAGE_UTF_16                   = "UTF-16";         //$NON-NLS-1$
	public  static final String   STRING_ENCODING                    = CODE_PAGE_UTF_8;
	public  static final String   DEFAULT_UNICODE_CHARSET            = CODE_PAGE_UTF_8;
	public  static final boolean  DEFAULT_COLUMN_NULLABLE            = true;
	public  static final String   BUILD_ID                           = getBuildId();
	public  static final String   BUILD_NUMBER                       = getBuildNumber();
	public  static final String   CLIENT_SERVER_VERSION              = getClientServerVersion();
	public  static final String   MODEL_VERSION                      = getModelVersion();
	public  static final String   JOB_FAILED_OUTPUT_TEMPLATE         = "{0}: {1}";                                 //$NON-NLS-1$
	public  static final String   RESPONSE_ERROR_MESSAGE             = "ResponseErrorMessage";	                  //$NON-NLS-1$
	public  static final String   DS_JOBS_DEFAULT_FOLDER             = "Jobs";                                     //$NON-NLS-1$
	public  static final String   SERVLET_URL_TEMPLATE_PRODUCT       = "://{0}:{1}/cwrapidgenerator/JobGenerator"; //$NON-NLS-1$
	public  static final String   AUTHENTICATION_URL_TEMPLATE        = "https://{0}:{1}";                          //$NON-NLS-1$
	public static  final String   DERIVATION_REPLACEMENT             = "$1";                                       //$NON-NLS-1$
	public static  final String   JOB_PARAM_SEPARATOR                = "#";                                        //$NON-NLS-1$

	/** Version check results */
	public enum VersionCheckResult { CheckedVersionEqual, CheckedVersionBelow, CheckedVersionAbove };
	
	
	// -------------------------------------------------------------------------------------
	// A N N O T A T I O N S
	// -------------------------------------------------------------------------------------
	public final static String ANNOT_GENERATED_MODEL_VERSION         = "SAPPACK_GEN_MODEL_VERSION"; //$NON-NLS-1$
	public final static String ANNOT_ABAP_PROGRAM_NAME               = "SAPPACK_ABAP_PROGRAM_NAME"; //$NON-NLS-1$
	public final static String ANNOT_ABAP_PROGRAM_NAME_CPIC          = "SAPPACK_ABAP_PROGRAM_NAME_CPIC"; //$NON-NLS-1$
	public final static String ANNOT_ABAP_CODE                       = "SAPPACK_ABAP_CODE"; //$NON-NLS-1$
	public final static String ANNOT_ABAP_CODE_CPIC                  = "SAPPACK_ABAP_CODE_CPIC"; //$NON-NLS-1$
	public final static String ANNOT_COLUMN_DERIVATION_EXPRESSION    = "SAPPACK_DERIVATION_EXPRESSION"; //$NON-NLS-1$
	public final static String ANNOT_COLUMN_STAGE_VARIABLES          = "SAPPACK_STAGE_VARIABLES"; //$NON-NLS-1$
	public final static String ANNOT_FILTER_CONSTRAINT               = "SAPPACK_FILTER_CONSTRAINT"; //$NON-NLS-1$
	public final static String ANNOT_TRANSFORMER_SOURCE_MAPPING      = "SAPPACK_TRANSFORMER_SRC_MAPPING"; //$NON-NLS-1$
	public final static String ANNOT_LOGICAL_TBL_NAME                = "SAPPACK_LOGICAL_TBL_NAME"; //$NON-NLS-1$
	public final static String ANNOT_PHYSICAL_TBL_NAME               = "SAPPACK_PHYSICAL_TBL_NAME"; //$NON-NLS-1$
	public final static String ANNOT_TBL_DEV_CLASS                   = "SAPPACK_TBL_DEV_CLASS";  //$NON-NLS-1$
	public final static String ANNOT_SAP_COLUMN_NAME                 = "SAPPACK_SAP_COLUMN_NAME"; //$NON-NLS-1$
	public final static String ANNOT_IS_KEY_IN_SAP                   = "SAPPACK_IS_KEY_IN_SAP"; //$NON-NLS-1$
	public final static String ANNOT_DOMAIN_HAS_FIXED_VALUES         = "SAPPACK_SAP_DOMAIN_HAS_FIXED_VALUES"; //$NON-NLS-1$
	public final static String ANNOT_DOMAIN_TABLE                    = "SAPPACK_SAP_DOMAIN_TABLE"; //$NON-NLS-1$
	public final static String ANNOT_DOMAIN_TRANSLATION_TABLE        = "SAPPACK_SAP_DOMAIN_TRANSLATION_TABLE"; //$NON-NLS-1$
	public final static String ANNOT_DOMAIN_TRANSLATION_TABLE_COLUMN = "SAPPACK_SAP_DOMAIN_TRANSLATION_TABLE_COLUMN"; //$NON-NLS-1$
	public final static String ANNOT_DOMAIN_TRANSLATION_TABLE_JOIN_CONDITION = "SAPPACK_SAP_DOMAIN_TRANSLATION_TABLE_JOIN_CONDITION"; //$NON-NLS-1$

	/** TRUE or FALSE */
	public static final String ANNOT_VALUE_TRUE                      = "true"; //$NON-NLS-1$
	public final static String ANNOT_COLUMN_IS_UNICODE               = "SAPPACK_UNICODE"; //$NON-NLS-1$
	public final static String ANNOT_IS_ROOT_SEGMENT                 = "SAPPACK_IS_ROOT_SEGMENT"; //$NON-NLS-1$
	public static final String ANNOT_CW_IS_ROOT_TABLE                = "CW_ISROOT"; //$NON-NLS-1$

	/** Possible values: 'IDOC, 'SAPLogicalTable' , 'JoinedCheckAndTextTable' */
	public final static String ANNOT_DATA_OBJECT_SOURCE                   = "SAPPACK_DATA_OBJECT_SOURCE"; //$NON-NLS-1$
	public final static String ANNOT_SAPPACK_CHECK_TBL_DATA_OBJECT_SOURCE = "SAPPACK_CHECK_TABLE_DATA_OBJECT_SOURCE"; //$NON-NLS-1$
	public final static String ANNOT_IDOC_TYPE                            = "SAPPACK_IDOC_TYPE"; //$NON-NLS-1$
	public final static String ANNOT_IDOC_RELEASE                         = "SAPPACK_IDOC_RELEASE"; //$NON-NLS-1$
	public final static String ANNOT_IDOC_BASIC_TYPE                      = "SAPPACK_IDOC_BASIC_TYPE"; //$NON-NLS-1$
	public final static String ANNOT_IS_EXTENDED_IDOC_TYPE                = "SAPPACK_IS_EXTENDED_IDOC_TYPE"; //$NON-NLS-1$

	public final static String ANNOT_PARENT_SEG                           = "SAPPACK_PARENT_SEG"; //$NON-NLS-1$
	public final static String ANNOT_SEGMENT_TYPE                         = "SAPPACK_SEG_TYPE"; //$NON-NLS-1$
	public final static String ANNOT_SEGMENT_DEFINITION                   = "SAPPACK_SEG_DEFINITION"; //$NON-NLS-1$
	public final static String ANNOT_SEG_OCCURRENCE_MIN                   = "SAPPACK_SEG_OCCURRENCE_MIN"; //$NON-NLS-1$
	public final static String ANNOT_SEG_OCCURRENCE_MAX                   = "SAPPACK_SEG_OCCURRENCE_MAX"; //$NON-NLS-1$

	public final static String ANNOT_TABLE_IS_MANDATORY                   = "SAPPACK_IS_MANDATORY"; //$NON-NLS-1$ 
	public static final String ANNOT_SAP_MESSAGE_TYPES                    = "SAPPACK_SAP_MESSAGE_TYPES"; //$NON-NLS-1$

	public static final String ANNOT_MODEL_PURPOSE                        = "SAPPACK_MODEL_PURPOSE";             //$NON-NLS-1$
	public static final String MODEL_PURPOSE_IDOC_ALL                     = "IDOC_ALL";                          //$NON-NLS-1$
	public static final String MODEL_PURPOSE_IDOC_EXTRACT                 = "IDOC_EXTRACT";                      //$NON-NLS-1$
	public static final String MODEL_PURPOSE_IDOC_LOAD                    = "IDOC_LOAD";                         //$NON-NLS-1$

	public static final String ANNOT_HAS_ADDITIONAL_TECHNICAL_FIELDS      = "SAPPACK_HAS_ADDITIONAL_TECHNICAL_FIELDS"; //$NON-NLS-1$
	public static final String ANNOT_VARCHARS_LENGTH_FACTOR               = "SAPPACK_VARCHARS_LENGTH_FACTOR"; //$NON-NLS-1$

	public static final String ANNOT_CHECKTABLE_COLUMNS                   = "SAPPACK_CHECKTABLE_COLUMNS"; //$NON-NLS-1$
	public static final String ANNOT_TEXTTABLE_COLUMNS                    = "SAPPACK_TEXTTABLE_COLUMNS"; //$NON-NLS-1$
	public static final String ANNOT_CHECKTABLE_TEXTTABLE_JOINCONDITION   = "SAPPACK_CHECKTABLE_TEXTTABLE_JOINCONDITION"; //$NON-NLS-1$
	public static final String ANNOT_TEXTTABLE_LANGUAGE_COLUMN            = "SAPPACK_TEXTTABLE_LANGUAGE_COLUMN"; //$NON-NLS-1$
	public static final String ANNOT_TEXTTABLE_TAKE_ONLY_FIRST_TUPLE      = "SAPPACK_TEXTTABLE_TAKE_ONLY_FIRST_TUPLE"; //$NON-NLS-1$
	public static final String ANNOT_IS_TEXTTABLE_LANGUAGE_COLUMN         = "SAPPACK_IS_TEXTTABLE_LANGUAGE_COLUMN"; //$NON-NLS-1$
	public static final String ANNOT_TRANSLATION_TABLE_NAME               = "SAPPACK_TRANSLATION_TABLE_NAME"; //$NON-NLS-1$
	public static final String ANNOT_INCLUDE_STRUCTURE                    = "SAPPACK_INCLUDESTRUCT"; //$NON-NLS-1$
	public static final String ANNOT_APPEND_STRUCTURE                     = "SAPPACK_APPENDSTRUCT"; //$NON-NLS-1$

	public static final String ANNOT_CW_PARENT_TABLE                = "CW_PARENTTABLE"; //$NON-NLS-1$

	public static final String ANNOT_ATTRIBUTE_TYPE_SOURCE          = "SAPPACK_ATTRIBUTE_TYPE_SOURCE"; //$NON-NLS-1$
	public static final String ANNOT_ATTRIBUTE_TYPE_SOURCE_IDOCTYPE = "IDOCTYPE"; //$NON-NLS-1$
	public static final String ANNOT_ATTRIBUTE_TYPE_SOURCE_VARCHAR  = "VARCHAR"; //$NON-NLS-1$

	public static final String ANNOT_CHECK_TABLE_NAME          = "SAPPACK_CHECK_TABLE_NAME"; //$NON-NLS-1$
	public static final String ANNOT_TEXT_TABLE_NAME           = "SAPPACK_TEXT_TABLE_NAME"; //$NON-NLS-1$
	public static final String ANNOT_MODEL_TEXT_TABLE_NAME     = "SAPPACK_MODEL_TEXT_TABLE_NAME"; //$NON-NLS-1$

	public static final String ANNOT_BUSINESS_OBJECT_NAME      = "SAPPACK_BUSINESS_OBJECT_NAME";  //$NON-NLS-1$
	public static final String ANNOT_SAP_TABLE_NAME            = "SAPPACK_SAP_TABLE_NAME"; //$NON-NLS-1$

	public static final String ANNOT_TIME_CREATED              = "SAPPACK_TIME_CREATED"; //$NON-NLS-1$
	public static final String ANNOT_DATE_CREATED              = "SAPPACK_DATE_CREATED"; //$NON-NLS-1$
	public static final String ANNOT_OS_USERNAME               = "SAPPACK_OS_USERNAME"; //$NON-NLS-1$
	public static final String ANNOT_SAP_USERNAME              = "SAPPACK_SAP_USER_NAME"; //$NON-NLS-1$
	public static final String ANNOT_SAP_USERLANGUAGE          = "SAPPACK_SAP_USER_LANGUAGE"; //$NON-NLS-1$
	public static final String ANNOT_SAP_CLIENT_ID             = "SAPPACK_SAP_CLIENT_ID"; //$NON-NLS-1$
	public static final String ANNOT_SAP_SYSTEM_NUMBER         = "SAPPACK_SAP_SYSTEM_NUMBER"; //$NON-NLS-1$
	public static final String ANNOT_SAP_SYSTEM_IS_UNICODE     = "SAPPACK_SAP_SYSTEM_IS_UNICODE"; //$NON-NLS-1$
	public static final String ANNOT_SAP_SYSTEM_HOST           = "SAPPACK_SAP_SYSTEM_HOST"; //$NON-NLS-1$
	public static final String ANNOT_SAP_SYSTEM_NAME           = "SAPPACK_SAP_SYSTEM_NAME"; //$NON-NLS-1$
	public static final String ANNOT_RUN_ID                    = "SAPPACK_RUN_ID";          //$NON-NLS-1$
	public static final String ANNOT_LDM_ID                    = "SAPPACK_LDM_ID";          //$NON-NLS-1$
	public static final String ANNOT_PACKS_V7_MODE             = "SAPPACK_V7_MODE";         //$NON-NLS-1$

	public static final String ANNOT_DATATYPE_DATATYPE         = "SAPPACK_SAP_DATA_TYPE"; //$NON-NLS-1$
	public static final String ANNOT_DATATYPE_DATA_ELEMENT     = "SAPPACK_SAP_DATA_ELEMENT"; //$NON-NLS-1$	
	public static final String ANNOT_DATATYPE_LENGTH           = "SAPPACK_SAP_DATA_TYPE_LENGTH"; //$NON-NLS-1$
	public static final String ANNOT_DATATYPE_DECIMALS         = "SAPPACK_SAP_DATA_TYPE_DECIMALS"; //$NON-NLS-1$
	public static final String ANNOT_DATATYPE_DOMAIN           = "SAPPACK_SAP_DATA_TYPE_DOMAIN"; //$NON-NLS-1$
	public static final String ANNOT_DATATYPE_DISPLAY_LENGTH   = "SAPPACK_SAP_DATA_DISPLAY_LENGTH"; //$NON-NLS-1$
	public static final String ANNOT_RELATED_CHECKTABLE        = "SAPPACK_RELATED_CHECKTABLE"; //$NON-NLS-1$
	public static final String ANNOT_RELATED_CT_JOIN           = "SAPPACK_RELATED_CHECKTABLE_JOINCONDITION"; //$NON-NLS-1$
	public static final String ANNOT_RELATED_CHECKTABLE_COLUMN = "SAPPACK_RELATED_CHECKTABLE_COLUMN"; //$NON-NLS-1$
	public static final String ANNOT_RELATED_TT                = "SAPPACK_RELATED_TRANSLATIONTABLE"; //$NON-NLS-1$
	public static final String ANNOT_RELATED_TT_COLUMN         = "SAPPACK_RELATED_TRANSLATIONTABLE_COLUMN"; //$NON-NLS-1$
	public static final String ANNOT_RELATED_TT_JOIN           = "SAPPACK_RELATED_TRANSLATIONTABLE_JOINCONDITION"; //$NON-NLS-1$
	public static final String ANNOT_TRANSCODING_TBL_SRC_FLD   = "SAPPACK_TRANSCODING_TABLE_SOURCE_FIELD"; //$NON-NLS-1$
	public static final String ANNOT_TRANSCODING_TBL_TRG_FLD   = "SAPPACK_TRANSCODING_TABLE_TARGET_FIELD"; //$NON-NLS-1$

	public static final String ANNOT_EXTRACT_RELATIONS         = "SAPPACK_EXTRACT_RELATIONS"; //$NON-NLS-1$
	public static final String ANNOT_ALTERNATE_CHECK_KEY       = "SAPPACK_ALTERNATE_KEY"; //$NON-NLS-1$
	public static final String ANNOT_PROPERTY_USER_NAME        = "user.name"; //$NON-NLS-1$

	// -------------------------------------------------------------------------------------
	//                   I D o c  T e c h n i c a l   F i e l d   N a m e s
	// -------------------------------------------------------------------------------------
	public  static final int     GEN_KEY_FIELD_LEN_V6_5           = 30;
	public  static final int     GEN_KEY_FIELD_LEN_V7             = 250;
	public  static final String  IDOC_TECH_FLD_ADM_PREFIX         = "ADMIN."; //$NON-NLS-1$
	public  static final String  IDOC_TECH_FLD_ADM_DOCNUM_ANNOT   = IDOC_TECH_FLD_ADM_PREFIX + "DOCNUM";   //$NON-NLS-1$
	public  static final String  IDOC_TECH_FLD_ADM_DOCNUM_NAME    = "ADM_DOCNUM";                          //$NON-NLS-1$
	public  static final int     IDOC_TECH_FLD_ADM_DOCNUM_LEN     = 16;
	public  static final int     IDOC_TECH_FLD_ADM_DOCNUM_LEN_V7  = GEN_KEY_FIELD_LEN_V7;
	public  static final String  IDOC_TECH_FLD_ADM_SEGNUM_ANNOT   = IDOC_TECH_FLD_ADM_PREFIX + "SEGNUM";   //$NON-NLS-1$
	public  static final String  IDOC_TECH_FLD_ADM_SEGNUM_NAME    = "ADM_SEGNUM";                          //$NON-NLS-1$
	public  static final int     IDOC_TECH_FLD_ADM_SEGNUM_LEN     = 6;
	public  static final int     IDOC_TECH_FLD_ADM_SEGNUM_LEN_V7  = GEN_KEY_FIELD_LEN_V7;
	public  static final String  IDOC_TECH_FLD_ADM_PSGNUM_ANNOT   = IDOC_TECH_FLD_ADM_PREFIX + "PSGNUM";   //$NON-NLS-1$
	public  static final String  IDOC_TECH_FLD_ADM_PSGNUM_NAME    = "ADM_PSGNUM";                          //$NON-NLS-1$
	public  static final int     IDOC_TECH_FLD_ADM_PSGNUM_LEN     = 6;
	public  static final int     IDOC_TECH_FLD_ADM_PSGNUM_LEN_V7  = GEN_KEY_FIELD_LEN_V7;
	public  static final String  IDOC_TECH_FLD_DEFAULT_TYPE       = "VARCHAR";                             //$NON-NLS-1$



	// -------------------------------------------------------------------------------------
	//                   I D o c  K e y  T e m p l a t e (strings)
	// -------------------------------------------------------------------------------------
	private static final String  KEY_SUFFIX                       = "_KEY";                               //$NON-NLS-1$
	public  static final String  P_KEY_SUFFIX                     = "_P" + KEY_SUFFIX;                    //$NON-NLS-1$
	public  static final String  F_KEY_SUFFIX                     = "_F" + KEY_SUFFIX;                    //$NON-NLS-1$
	public  static final String  IDOC_PRIMARY_KEY_TEMPLATE        = "SEG_{0}" + P_KEY_SUFFIX;             //$NON-NLS-1$
	public  static final String  IDOC_PRIMARY_KEY_ROOT_TEMPLATE   = "IDOC_{0}" + P_KEY_SUFFIX;            //$NON-NLS-1$
	public  static final String  IDOC_FOREIGN_KEY_SUB_TEMPLATE    = "SEG_{0}" + F_KEY_SUFFIX;             //$NON-NLS-1$
	public  static final String  IDOC_FOREIGN_KEY_ROOT_TEMPLATE   = "IDOC_{0}" + F_KEY_SUFFIX;            //$NON-NLS-1$
	public  static final String  PRIMARY_KEY_NAME_TEMPLATE        = "PK_{0}";                             //$NON-NLS-1$
	public  static final String  FOREIGN_KEY_NAME_TEMPLATE        = "FK_{0}_{1}";                         //$NON-NLS-1$
	public  static final String  RELATIONSHIP_NAME_TEMPLATE       = "FK_{0}_{1}_{2}";                     //$NON-NLS-1$


	/** DataObject type ALL */
	public final static String DATA_OBJECT_SOURCE_TYPE_ALL                         = "ALL"; //$NON-NLS-1$ 
	/** DataObject type IDOC */
	public final static String DATA_OBJECT_SOURCE_TYPE_IDOC                        = "IDOC"; //$NON-NLS-1$ 
	/** DataObject type SapLogicalTable */
	public final static String DATA_OBJECT_SOURCE_TYPE_LOGICAL_TABLE               = "SAPLogicalTable"; //$NON-NLS-1$
	/** DataObject type JoinedAndCheckTable */
	public final static String DATA_OBJECT_SOURCE_TYPE_JOINED_CHECK_AND_TEXT_TABLE = "JoinedCheckAndTextTable"; //$NON-NLS-1$      
	/** DataObject type TranslationTable */
	public final static String DATA_OBJECT_SOURCE_TYPE_TRANSLATION_TABLE           = "TranslationTable"; //$NON-NLS-1$      
	/** DataObject type Domain TranslationTable */
	public final static String DATA_OBJECT_SOURCE_TYPE_DOMAIN_TRANSLATION_TABLE    = "DomainTranslationTable"; //$NON-NLS-1$
	/** DataObject type Domain Table */
	public final static String DATA_OBJECT_SOURCE_TYPE_DOMAIN_TABLE                = "DomainTable"; //$NON-NLS-1$ 
	/** DataObject type LoadStatus */
	public final static String DATA_OBJECT_SOURCE_TYPE_LOAD_STATUS                 = "SAPPackLoadStatus"; //$NON-NLS-1$ 
	/** DataObject type Technical Field */
	public final static String DATA_OBJECT_SOURCE_TYPE_TECH_FIELD                  = "TechField"; //$NON-NLS-1$ 
	/** DataObject type Load Job Field (internal only) */
	public final static String DATA_OBJECT_SOURCE_TYPE_LOAD_JOB_FIELD              = "LoadJobField"; //$NON-NLS-1$ 
	/** DataObject type Extract Job Field (internal only) */
	//	public final static String DATA_OBJECT_SOURCE_TYPE_EXTRACT_JOB_FIELD = "ExtractJobField"; //$NON-NLS-1$ 

	public final static String DATA_OBJECT_SOURCE_TYPE_NON_REFERENCE_CHECK_TABLE   = "NonReferenceCheckTable"; //$NON-NLS-1$
	public final static String DATA_OBJECT_SOURCE_TYPE_REFERENCE_CHECK_TABLE       = "ReferenceCheckTable"; //$NON-NLS-1$
	public final static String DATA_OBJECT_SOURCE_TYPE_REFERENCE_TEXT_TABLE        = "ReferenceTextTable"; //$NON-NLS-1$

	public final static String SAP_DATA_TYPE_CLIENT_ID      = "CLNT";  //$NON-NLS-1$
	public final static String SAP_CLIENT_ID_JOB_PARAM_NAME = "CLIENTID"; //$NON-NLS-1$

	public static final String SOURCE_COLUMN_PREFIX = "SOURCE_"; //$NON-NLS-1$
	public static final String TARGET_COLUMN_PREFIX = "TARGET_"; //$NON-NLS-1$
	

	// -------------------------------------------------------------------------------------
	// M I S C
	// -------------------------------------------------------------------------------------

	public static final String   LDM_ENTITY_NAME_SEPARATOR             = "_";                                  //$NON-NLS-1$
	public static final String   REQUEST_RESULT                        = "RequestResult";                      //$NON-NLS-1$
	public static final String   NEWLINE                               = System.getProperty("line.separator"); //$NON-NLS-1$
	public static final String   NEWLINE_WINDOWS                       = "\r\n";                               //$NON-NLS-1$
	public static final String   TAB                                   = "   ";                                //$NON-NLS-1$
	public static final String[] NO_PARAMS                             = new String[] {};
	public static final String   LDM_PACKAGE_SUFFIX_IDOC_TYPE_SEGMENTS = "_Segments";                          //$NON-NLS-1$
	public static final String   LDM_PACKAGE_SUFFIX_CHECK_TABLES       = "_Tables";                            // osuhre //$NON-NLS-1$
	public static final String   LDM_PACKAGE_SUFFIX_JLT                = "_CheckTextTables";                   //$NON-NLS-1$
	//   public static final String   LDM_PACKAGE_SUFFIX_MIH_LOAD = "_MIHLoad";
	public static final String   LDM_PACKAGE_SUFFIX_MOVEMENT           = "_Movement";                          //$NON-NLS-1$

	// place holders for the ABAP code template annotation
	//	public static final String ABAPCODE_PLACEHOLDER_PROGRAMNAME = "#SAPPACK_PROGNAME#"; //$NON-NLS-1$
	public static final String ABAPCODE_PLACEHOLDER_RFC_DESTINATION_NAME = "#SAPPACK_RFCDEST#"; //$NON-NLS-1$

	public static final char    DERIVATION_EXCEPTION_TABLE_COLUMN_DELIMITER = '.'; //$NON-NLS-1$
	public static final Pattern NUMERIC_PATTERN                             = Pattern.compile("[0-9]*"); //$NON-NLS-1$
   

	// -------------------------------------------------------------------------------------
	// B U I L D  N U M B E R  /  B U I L D  V E R S I O N
	// -------------------------------------------------------------------------------------
	private static final String BUILD_BUNDLE_NAME        = "com.ibm.is.sappack.gen.build.buildID"; //$NON-NLS-1$
	private static final String BUILD_NUMBER_ID          = "build.number"; //$NON-NLS-1$
	private static final String BUILD_DATE_ID            = "build.date"; //$NON-NLS-1$
	private static final String CLIENT_SERVER_VERSION_ID = "client.server.version"; //$NON-NLS-1$
	private static final String MODEL_VERSION_ID         = "model.version"; //$NON-NLS-1$
	private static final String BUILD_DATE_UNKNOWN       = "unknown"; //$NON-NLS-1$

	public static final String FIRST_DS_PROJECT = "FIRST_DS_PROJECT_IF_SPECIFIC_NAME_IS_NOT_REQUIRED"; //$NON-NLS-1$
	
	/** DO NEVER CHANGE that prefix since it's evaluated by SAP Packs Runtime */
	public static final String JOB_GEN_SAP_STAGE_ID = "IBM InfoSphere Rapid Generator for SAP Applications (" + getBuildNumber() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
	public static final String JOB_GEN_EXTENDED_IDOC_TYPE = "CExtendedIDocType"; //$NON-NLS-1$
	public static final String JOB_GEN_BASIC_IDOC_TYPE = "CBasicIDocType"; //$NON-NLS-1$

	public static final String getBuildNumber() {
		try {
			ResourceBundle bundle = ResourceBundle.getBundle(BUILD_BUNDLE_NAME);
			return(bundle.getString(BUILD_NUMBER_ID));
			//			return Integer.parseInt(bundle.getString(BUILD_NUMBER_ID));
		} catch (Exception e) {
			e.printStackTrace();
			return "-1"; //$NON-NLS-1$
		}
	}

	public static final String getClientServerVersion() {
		try {
			ResourceBundle bundle = ResourceBundle.getBundle(BUILD_BUNDLE_NAME);
			return bundle.getString(CLIENT_SERVER_VERSION_ID);
		} catch (Exception e) {
			e.printStackTrace();
			return(CLIENT_SERVER_VERSION_ID + " not found!!!!");
		}
	}

	public static final String getModelVersion() {
		try {
			ResourceBundle bundle = ResourceBundle.getBundle(BUILD_BUNDLE_NAME);
			return bundle.getString(MODEL_VERSION_ID);
		} catch (Exception e) {
			e.printStackTrace();
			return(MODEL_VERSION_ID + " not found!!!!");
		}
	}

	public static final String getBuildDate() {
		try {
			ResourceBundle bundle = ResourceBundle.getBundle(BUILD_BUNDLE_NAME);
			return bundle.getString(BUILD_DATE_ID);
		} catch (Exception e) {
			e.printStackTrace();
			return BUILD_DATE_UNKNOWN;
		}
	}

	public static final String getBuildId() {
		return getBuildNumber() + "-" + getBuildDate(); //$NON-NLS-1$
	}

	public static int ABAP_TRANSFERMETHOD_CPIC = 0x01;
	public static int ABAP_TRANSFERMETHOD_RFC = 0x02;
	public static final String COM_IBM_DATATOOLS_CORE_UI_DATABASE_DESIGN_NATURE = "com.ibm.datatools.core.ui.DatabaseDesignNature"; //$NON-NLS-1$


	static String copyright() { 
		return com.ibm.is.sappack.gen.common.Copyright.IBM_COPYRIGHT_SHORT; 
	}


} // end of class Constants
