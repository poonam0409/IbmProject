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
// Module Name : com.ibm.is.sappack.gen.tools.sap.constants
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.tools.sap.constants;


import com.ibm.is.sappack.gen.tools.sap.activator.Activator;


public interface Constants {

	static final String IBM_COPYRIGHT_SHORT = Copyright.IBM_COPYRIGHT_SHORT;
	
	/** The file extension for a logical data model (usually "ldm") */
	public static final String LOGICAL_DATA_MODEL_FILE_EXTENSION  = ".ldm"; //$NON-NLS-1$
   public static final String PHYSICAL_DATA_MODEL_FILE_EXTENSION = ".dbm"; //$NON-NLS-1$

   /** Conversion Workbench Joined Lookup Table and Text Table prefix */
   public static final String CW_JLT_PREFIX                      = "JLT_";                //$NON-NLS-1$
   public static final String CW_JLT_NAME_TEMPLATE               = CW_JLT_PREFIX + "{0}"; //$NON-NLS-1$
   public static final String CW_TT_PREFIX                       = "TT_";                 //$NON-NLS-1$
   public static final String CW_TT_NAME_TEMPLATE                = CW_TT_PREFIX + "{0}";  //$NON-NLS-1$
   public static final String CW_TT_COLUMN_PREFIX_SOURCE         = "SOURCE_"; //$NON-NLS-1$
   public static final String CW_TT_COLUMN_PREFIX_TARGET         = "TARGET_"; //$NON-NLS-1$
   public static final String CW_DOMAIN_TABLE_PREFIX			     = "DT_"; //$NON-NLS-1$
   public static final String CW_DOMAIN_TABLE_TEMPLATE             = CW_DOMAIN_TABLE_PREFIX + "{0}"; //$NON-NLS-1$
   public static final String CW_DOMAIN_TRANSLATION_TABLE_PREFIX   = "DTT_";  //$NON-NLS-1$
   public static final String CW_DOMAIN_TRANSLATION_TABLE_TEMPLATE = CW_DOMAIN_TRANSLATION_TABLE_PREFIX + "{0}";  //$NON-NLS-1$
      
   /** Default package name for atomic domains */
   public static final String DEFAULT_PKG_NAME_ATOMIC_DOMAINS    = "AtomicDomains"; //$NON-NLS-1$
   
	/**The file extension for custom business objects */
	public static final String CUSTOM_BUSINESS_OBJECT_FILE_EXTENSION = "*.sapbomodel"; //$NON-NLS-1$

	/** The SAP JCo native library name */
	public static final String SAP_JCO_NATIVE_LIBRARY = "sapjco3"; //$NON-NLS-1$

   /** The SAP Data Type name prefix */
   public static final String SAP_DATA_TYPE_NAME_PREFIX   = "SAP_"; //$NON-NLS-1$
   
   /** The SAP User Default language */
   public  static final String   DEFAULT_USER_LANG = "EN";         //$NON-NLS-1$

   /** DB identifier minimum length */
   public  static final int      DB_IDENTIFIER_MIN_LENGTH         = 18;
   /** DB2 identifier maximum length */
   public  static final int      DB_IDENTIFIER_MAX_LENGTH         = 99;
   /** Oracle identifier maximum length */
   public  static final int      DB_IDENTIFIER_ORACLE_LENGTH      = 30;
   /** DB2 for Conversion Workbench identifier maximum length */
   public  static final int      DB_IDENTIFIER_DB2_FOR_CW_LENGTH  = 30;
   
	// The help IDs
	public static final String HELP_ID_SELECT_SAP_SYSTEM = Activator.PLUGIN_ID + ".select_sap_system"; //$NON-NLS-1$
	public static final String HELP_ID_SELECT_BUSINESS_OBJECTS = Activator.PLUGIN_ID + ".select_business_objects"; //$NON-NLS-1$
	public static final String HELP_ID_BROWSE_SAP_SYSTEM = Activator.PLUGIN_ID + ".browse_sap_system"; //$NON-NLS-1$
	public static final String HELP_ID_SPECIFY_TABLES_MANUALLY = Activator.PLUGIN_ID + ".specify_tables_manually"; //$NON-NLS-1$
	public static final String HELP_ID_SPECIFY_IDOCS_MANUALLY = Activator.PLUGIN_ID + ".specify_idocs_manually"; //$NON-NLS-1$
	public static final String HELP_ID_IMPORT_OPTIONS = Activator.PLUGIN_ID + ".import_options"; //$NON-NLS-1$
	public static final String HELP_ID_IDOC_IMPORT_OPTIONS = Activator.PLUGIN_ID + ".idoc_import_options"; //$NON-NLS-1$
	public static final String HELP_ID_TABLE_LIST = Activator.PLUGIN_ID + ".tables_list"; //$NON-NLS-1$
	public static final String HELP_ID_SAP_SYSTEM = Activator.PLUGIN_ID + ".sap_system"; //$NON-NLS-1$
	public static final String HELP_ID_TECHNICAL_FIELDS = Activator.PLUGIN_ID + ".technical_fields"; //$NON-NLS-1$	
	public static final String HELP_ID_SEGMENT_LIST = Activator.PLUGIN_ID + ".segment_list"; //$NON-NLS-1$
	public static final String HELP_ID_BROWSE_SAP_SYSTEM_FOR_IDOCS = Activator.PLUGIN_ID + ".browse_sap_system_for_idocs"; //$NON-NLS-1$
	public static final String HELP_ID_SELECT_LDM_PACKAGE = Activator.PLUGIN_ID + ".select_ldm_package"; //$NON-NLS-1$
	public static final String HELP_ID_IMPORT_SUMMARY = Activator.PLUGIN_ID + ".import_summary"; //$NON-NLS-1$

	// replay wizard
	public static final String HELP_ID_REEXTRACT_DISPLAY_RUNS = Activator.PLUGIN_ID + ".reextract_display_runs"; //$NON-NLS-1$
	public static final String HELP_ID_REEXTRACT_OPTIONS = Activator.PLUGIN_ID + ".reextract_options"; //$NON-NLS-1$
	
	// Empty String
	public static final String EMPTY_STRING        = "";  //$NON-NLS-1$
	public static final String BLANK               = " "; //$NON-NLS-1$
	public static final String HASH                = "#"; //$NON-NLS-1$

	public static final String JOB_PARAM_SEPARATOR = com.ibm.is.sappack.gen.common.Constants.JOB_PARAM_SEPARATOR;

	public static final String ICON_RM_WIZARD_BANNER_IMAGE_ID = "icons/Rapid_modeler.png"; //$NON-NLS-1$

	public static final String RM_WIZARD_BANNER_IMAGE_ID = "rmWizardBannerImage"; //$NON-NLS-1$

	// Invalid characters for table names
	public static final String INVALID_CHARS_FOR_TABLE_NAMES = "[ \\t\\x0B\\f\\r\"']"; //$NON-NLS-1$

	// XPATH Constants
	public static final String XPATH_STRING_CONTENTS    = "/contents/package"; //$NON-NLS-1$	
	public static final String XPATH_STRING_LABEL       = "label"; //$NON-NLS-1$	
	public static final String XPATH_STRING_DESCRIPTION = "description"; //$NON-NLS-1$	
	public static final String XPATH_STRING_PACKAGE     = "package"; //$NON-NLS-1$	
	public static final String XPATH_STRING_TABLES      = "tables/table"; //$NON-NLS-1$	

	// Icon Constants
	public static final String ICON_ID_TABLE            = "tableIcon"; //$NON-NLS-1$	
	public static final String ICON_TABLE               = "icons/table.gif"; //$NON-NLS-1$

	public static final String DECORATOR_ID_MANDATORY_SEGMENT = "mandatorySegmentDecorator"; //$NON-NLS-1$	
	public static final String DECORATOR_MANDATORY_SEGMENT = "icons/mandatory_segment_decorator.gif"; //$NON-NLS-1$

	public static final String ICON_ID_TABLE_FOLDER = "tableFolderIcon"; //$NON-NLS-1$	
	public static final String ICON_TABLE_FOLDER = "icons/tableFolder.gif"; //$NON-NLS-1$

	public static final String ICON_ID_FOLDER = "folderIcon"; //$NON-NLS-1$	
	public static final String ICON_FOLDER = "icons/folder.gif"; //$NON-NLS-1$

	public static final String ICON_ID_CHECKBOX_CHECKED = "checkbox_checked"; //$NON-NLS-1$	
	public static final String ICON_CHECKBOX_CHECKED = "icons/checkbox_checked.gif"; //$NON-NLS-1$

	public static final String ICON_ID_CHECKBOX_CHECKED_DISABLED = "checkbox_checked_disabled"; //$NON-NLS-1$	
	public static final String ICON_CHECKBOX_CHECKED_DISABLED = "icons/checkbox_checked_disabled.gif"; //$NON-NLS-1$

	public static final String ICON_ID_CHECKBOX_UNCHECKED = "checkbox_unchecked"; //$NON-NLS-1$	
	public static final String ICON_CHECKBOX_UNCHECKED = "icons/checkbox_unchecked.gif"; //$NON-NLS-1$

	public static final String ICON_ID_CHECKBOX_UNCHECKED_DISABLED = "checkbox_unchecked_disabled"; //$NON-NLS-1$	
	public static final String ICON_CHECKBOX_UNCHECKED_DISABLED = "icons/checkbox_unchecked_disabled.gif"; //$NON-NLS-1$

	public static final String ICON_ID_MESSAGE_INFORMATION = "message_information"; //$NON-NLS-1$	
	public static final String ICON_MESSAGE_INFORMATION = "icons/message_information.gif"; //$NON-NLS-1$

	public static final String ICON_ID_MESSAGE_WARNING = "message_warning"; //$NON-NLS-1$	
	public static final String ICON_MESSAGE_WARNING = "icons/message_warning.gif"; //$NON-NLS-1$

	public static final String ICON_ID_MESSAGE_ERROR = "message_error"; //$NON-NLS-1$	
	public static final String ICON_MESSAGE_ERROR = "icons/message_error.gif"; //$NON-NLS-1$

	public static final String ICON_ID_LDM_PACKAGE = "ldm_package"; //$NON-NLS-1$	
	public static final String ICON_LDM_PACKAGE = "icons/ldm_package.gif"; //$NON-NLS-1$

	public static final String ICON_ID_VALIDATE_SUCCESS = "validate_success"; //$NON-NLS-1$
	public static final String ICON_VALIDATE_SUCCESS = "icons/s_passed.png"; //$NON-NLS-1$
	public static final String ICON_ID_VALIDATE_FAILURE = "validate_failure"; //$NON-NLS-1$
	public static final String ICON_VALIDATE_FAILURE = "icons/s_error.png"; //$NON-NLS-1$
	public static final String ICON_ID_VALIDATE_WARNING = "validate_warning"; //$NON-NLS-1$
	public static final String ICON_VALIDATE_WARNING = "icons/message_warning.gif"; //$NON-NLS-1$
	
	// JCO Constants (Function module names, parameter and constant names)
	public static final String JCO_FUNCTION_DD_TABL_GET = "DD_TABL_GET"; //$NON-NLS-1$
	public static final String JCO_FUNCTION_DDIF_FIELDINFO_GET = "DDIF_FIELDINFO_GET"; //$NON-NLS-1$
	public static final String JCO_FUNCTION_TABLE_GET_TEXTTABLE = "TABLE_GET_TEXTTABLE"; //$NON-NLS-1$
	public static final String JCO_FUNCTION_RFC_READ_TABLE = "RFC_READ_TABLE"; //$NON-NLS-1$
	public static final String JCO_FUNCTION_RFC_GET_STRUCTURE_DEFINITION = "RFC_GET_STRUCTURE_DEFINITION"; //$NON-NLS-1$

	public static final String JCO_PARAMETER_FORTABLE = "FORTABLE"; //$NON-NLS-1$
	public static final String JCO_PARAMETER_FORKEY = "FORKEY"; //$NON-NLS-1$
	public static final String JCO_PARAMETER_CHECKFIELD = "CHECKFIELD"; //$NON-NLS-1$
	public static final String JCO_PARAMETER_CARD = "CARD"; //$NON-NLS-1$
	public static final String JCO_PARAMETER_CARDLEFT = "CARDLEFT"; //$NON-NLS-1$
	public static final String JCO_PARAMETER_FIELDNAME = "FIELDNAME"; //$NON-NLS-1$
	public static final String JCO_PARAMETER_CHECKTABLE = "CHECKTABLE"; //$NON-NLS-1$
	public static final String JCO_PARAMETER_TABNAME = "TABNAME"; //$NON-NLS-1$
	public static final String JCO_PARAMETER_CONTFLAG = "CONTFLAG"; //$NON-NLS-1$
	public static final String JCO_PARAMETER_TABCLASS = "TABCLASS"; //$NON-NLS-1$
	public static final String JCO_PARAMETER_VIEWCLASS = "VIEWCLASS"; //$NON-NLS-1$
	public static final String JCO_PARAMETER_TABL_NAME = "TABL_NAME"; //$NON-NLS-1$
	public static final String JCO_PARAMETER_WITHTEXT = "WITHTEXT"; //$NON-NLS-1$
	public static final String JCO_PARAMETER_LANGU = "LANGU"; //$NON-NLS-1$
	public static final String JCO_PARAMETER_ADD_TYPEINFO = "ADD_TYPEINFO"; //$NON-NLS-1$
	public static final String JCO_PARAMETER_DDTEXT = "DDTEXT"; //$NON-NLS-1$
	public static final String JCO_PARAMETER_TEXT = "TEXT"; //$NON-NLS-1$
	public static final String JCO_PARAMETER_KEYFLAG = "KEYFLAG";//$NON-NLS-1$
	public static final String JCO_PARAMETER_DOMNAME = "DOMNAME";//$NON-NLS-1$
	public static final String JCO_PARAMETER_DOMAIN_FIXED_VALUES = "VALEXI";//$NON-NLS-1$
	public static final String JCO_PARAMETER_ROLLNAME = "ROLLNAME";//$NON-NLS-1$
	public static final String JCO_PARAMETER_FIELDTEXT = "FIELDTEXT";//$NON-NLS-1$
	public static final String JCO_PARAMETER_SCRTEXT_L = "SCRTEXT_L";//$NON-NLS-1$
	public static final String JCO_PARAMETER_DECIMALS = "DECIMALS";//$NON-NLS-1$
	public static final String JCO_PARAMETER_LENG = "LENG";//$NON-NLS-1$
	public static final String JCO_PARAMETER_INTLEN = "INTLEN";//$NON-NLS-1$
	public static final String JCO_PARAMETER_OUTPUTLEN = "OUTPUTLEN";//$NON-NLS-1$
	public static final String JCO_PARAMETER_INTTYPE = "INTTYPE";//$NON-NLS-1$
	public static final String JCO_PARAMETER_DATATYPE = "DATATYPE"; //$NON-NLS-1$
	public static final String JCO_PARAMETER_WA = "WA"; //$NON-NLS-1$

	public static final String JCO_PARAMETER_VALUE_CARDINATLITY_ZERO_TO_ONE = "C";//$NON-NLS-1$
	public static final String JCO_PARAMETER_VALUE_CARDINATLITY_ONE = "1";//$NON-NLS-1$
	public static final String JCO_PARAMETER_VALUE_CARDINATLITY_ZERO_TO_MANY = "CN";//$NON-NLS-1$
	public static final String JCO_PARAMETER_VALUE_CARDINATLITY_ONE_TO_MANY = "N";//$NON-NLS-1$
	public static final String JCO_PARAMETER_VALUE_LANGUAGE_EN = "EN"; //$NON-NLS-1$
	public static final String JCO_PARAMETER_VALUE_TRUE = "X"; //$NON-NLS-1$	
	public static final String JCO_PARAMETER_VALUE_FALSE = " "; //$NON-NLS-1$

	public static final String JCO_RESULTTABLE_DD08_V_TAB_A = "DD08V_TAB_A"; //$NON-NLS-1$
	public static final String JCO_RESULTTABLE_DD05_M_TAB_A = "DD05M_TAB_A"; //$NON-NLS-1$
	public static final String JCO_RESULTTABLE_DFIES_TAB = "DFIES_TAB"; //$NON-NLS-1$
	public static final String JCO_RESULTSTRUCTURE_DD02_V_WA_A = "DD02V_WA_A"; //$NON-NLS-1$
	public static final String JCO_RESULTTABLE_OPTIONS = "OPTIONS"; //$NON-NLS-1$
	public static final String JCO_RESULTTABLE_DATA = "DATA"; //$NON-NLS-1$
	public static final String JCO_RESULTTABLE_FIELDS = "FIELDS"; //$NON-NLS-1$
	public static final String JCO_TABLE_DD02_T = "DD02T"; //$NON-NLS-1$
	public static final String JCO_TABLE_DD02_L = "DD02L"; //$NON-NLS-1$

	public static final String JCO_ERROR_KEY_NOT_FOUND = "NOT_FOUND"; //$NON-NLS-1$
	public static final String JCO_ERROR_KEY_TABLE_NOT_FOUND = "TABLE_NOT_FOUND"; //$NON-NLS-1$

   public static final String JCO_QUERY_PART_AND_DDLANGUAGE = "' AND DDLANGUAGE = '%s'"; //$NON-NLS-1$
	public static final String JCO_QUERY_PART_TABNAME_LIKE = "TABNAME LIKE '"; //$NON-NLS-1$
	
	public static final String JCO_TABCLASS_TRANSP = "TRANSP"; //$NON-NLS-1$
	public static final String JCO_TABCLASS_CLUSTER = "CLUSTER"; //$NON-NLS-1$
	public static final String JCO_TABCLASS_POOL = "POOL"; //$NON-NLS-1$
	public static final String JCO_TABCLASS_VIEW = "VIEW"; //$NON-NLS-1$
	public static final String JCO_VIEWCLASS_D = "D"; //$NON-NLS-1$
	public static final String JCO_VIEWCLASS_P = "P"; //$NON-NLS-1$
	
	public static final String DELIMITER = "DELIMITER"; //$NON-NLS-1$

	public static final String ROWCOUNT = "ROWCOUNT"; //$NON-NLS-1$

	public static final String QUERY_TABLE = "QUERY_TABLE"; //$NON-NLS-1$

	public static final int MAX_ROWS_RETURNED = 0;
	public static final char SAP_COLUMN_DELIMITER = '|';

	// Property names for the "about JCo" dialog
	public static final String PROPERTY_JCO_MIDDLEWARE_NATIVE_LAYER_VERSION = "jco.middleware.native_layer_version"; //$NON-NLS-1$
	public static final String PROPERTY_JCO_MIDDLEWARE_NATIVE_LAYER_PATH = "jco.middleware.native_layer_path"; //$NON-NLS-1$
	public static final String PROPERTY_JCO_MIDDLEWARE_NAME = "jco.middleware.name"; //$NON-NLS-1$
	public static final String PROPERTY_JAVA_VERSION = "java.version"; //$NON-NLS-1$
	public static final String PROPERTY_JAVA_RUNTIME_VERSION = "java.runtime.version"; //$NON-NLS-1$
	public static final String PROPERTY_JAVA_VM_VERSION = "java.vm.version"; //$NON-NLS-1$
	public static final String PROPERTY_JAVA_VENDOR = "java.vendor"; //$NON-NLS-1$

	// Constants for the SAP to DA datatype mapping
	public static final String SAP_DATATYPE_CHAR = "CHAR"; //$NON-NLS-1$
	public static final String SAP_DATATYPE_DECIMAL = "DEC"; //$NON-NLS-1$
	public static final char SAP_BASETYPE_CHARACTER = 'C';
	public static final char SAP_BASETYPE_NUMERIC_CHARACTER = 'N';
	public static final char SAP_BASETYPE_DATE = 'D';
	public static final char SAP_BASETYPE_TIME = 'T';

	public static final char SAP_BASETYPE_HEXADECIMAL = 'X';

	public static final char SAP_BASETYPE_INTEGER = 'I';
	public static final char SAP_BASETYPE_SHORT = 'S';
	public static final char SAP_BASETYPE_BYTE = 'B';
	public static final char SAP_BASETYPE_FLOAT = 'F';
	public static final char SAP_BASETYPE_PACKED_NUMBER = 'P';

	//	public static final String CONFIGURATION_RESOURCE_BUNDLE = "com.ibm.is.sappack.gen.tools.sap.configuration"; //$NON-NLS-1$
	//	public static final String IDOC_TYPE_FEATURES_ENABLED_CONFIGURATION_PROPERTY = "IDOC_TYPE_FEATURES_ENABLED"; //$NON-NLS-1$
	//	public static final String SAP_PE_FEATURES_ENABLED_CONFIGURATION_PROPERTY = "SAP_PE_FEATURES_ENABLED"; //$NON-NLS-1$

	// IDOC extension
	//public static final String CHECK_TABLES_LDM_PACKAGE = "CheckTables"; //$NON-NLS-1$

	// Log Messages
	public static final String LOG_MESSAGE_EXCEPTION_OCCURED = "exception occured"; //$NON-NLS-1$
	
	public static final String LDM_KEY_VALUE_BOOLEAN_TRUE = "true"; //$NON-NLS-1$
	public static final String LDM_KEY_VALUE_BOOLEAN_FALSE = "false"; //$NON-NLS-1$
	
	public static final String DEFAULT_ABAP_CODE_PREFIX_RFC = "Z_SP_"; //$NON-NLS-1$
	public static final String DEFAULT_ABAP_CODE_PREFIX_CPIC = "Z";    //$NON-NLS-1$
	
	public static final int MAX_ABAP_PROGRAMNAME_LENGTH_RFC = 30;
	public static final int MAX_ABAP_PROGRAMNAME_LENGTH_CPIC = 8;
	
	public static final char   INCLUDE_APPEND_STRUCTURE_SEPARATOR = '|';
	public static final String REF_TABLE_DEV_CLASS_LIST_SEPARATOR = "\\|";                     //$NON-NLS-1$

	static final String IDOC_EMPTY_RELEASE = ""; //$NON-NLS-1$
}
