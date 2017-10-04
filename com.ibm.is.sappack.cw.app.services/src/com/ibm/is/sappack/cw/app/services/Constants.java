package com.ibm.is.sappack.cw.app.services;

@SuppressWarnings("nls")
public final class Constants {

	public static final String LOGGER_ID = "com.ibm.is.sappack.cw.app";
	
	/************************
	 * CW tables and columns
	 ************************/
	
	// Essential CW DB tables that are required for this app to function
	public static final String[] essentialTables = {
			Constants.CW_TABLE_CHECK_TABLES_METADATA,
			Constants.CW_TABLE_DOMAIN_TABLES_METADATA,
			Constants.CW_TABLE_DATA_TABLES_METADATA,
			Constants.CW_TABLE_FIELDS_METADATA,
			Constants.CW_TABLE_DATA_TABLES_CONFIG,
			Constants.CW_TABLE_FIELDS_CONFIG,
			Constants.CW_TABLE_FOREIGN_KEYS,
			Constants.CW_LEGACY_ID_TABLENAME,
			Constants.CW_LEGACY_ID_SAP_TABLENAME,
			Constants.CW_SCHEMAS_TABLENAME};
	
	// Common fields
	public static final String CW_COLUMN_LEGACY_ID = "CW_LEGACY_ID";
	public static final String CW_COLUMN_LOB = "CW_LOB";
	public static final String CW_COLUMN_ROLLOUT = "CW_RLOUT";
	public static final String CW_COLUMN_SAP_TABLENAME = "SAP_TABNAME";
	public static final String CW_COLUMN_SAP_FIELDNAME = "SAP_FIELDNAME";
	public static final String CW_COLUMN_CW_TABLENAME = "CW_TABNAME";
	public static final String CW_COLUMN_CW_FIELDNAME = "CW_FIELDNAME";
	public static final String CW_COLUMN_LOGICAL_TABLE = "LOGICALTABLE";
	public static final String CW_COLUMN_ACTIVE = "ACTIVE";
	public static final String CW_COLUMN_PRIMPOS = "PRIMPOS";
	
	// Table config
	public static final String CW_TABLE_DATA_TABLES_CONFIG = "AUX.SAP_DATATABLES_CONFIG";
	
	// Field config
	public static final String CW_TABLE_FIELDS_CONFIG = "AUX.SAP_FIELDS_CONFIG";
	public static final String CW_COLUMN_MANDATORY = "MANDATORY";
	public static final String CW_COLUMN_SAP_VIEW = "SAP_VIEW";
	
	// RDM metadata load

	public static final String CW_LOB_WILDCARD = "*";
	
	public static final String CW_TABLE_CHECK_TABLES_METADATA = "AUX.SAP_CHECKTABLES_METADATA";
	public static final String CW_COLUMN_SAP_CHECK_TABLE = "SAP_CHECKTABLE";
	public static final String CW_COLUMN_CT_CHECK_TABLE = "CT_CHECKTABLE";
	public static final String CW_COLUMN_DESCRIPTION = "DDTEXT";
	public static final String CW_COLUMN_TEXT_TABLE = "TEXTTABLE";
	public static final String CW_COLUMN_CW_TEXT_TABLE = "CW_TEXTTABLE";
	public static final String CW_COLUMN_TABLE_TYPE = "TABLE_TYPE";
	public static final String CW_TABLE_TYPE_VALUE = "ReferenceCheckTable"; // The value we need for RDM
	public static final String CW_COLUMN_TRANSCODING_TABLE = "TRANSCODING_TABLE";
	
	public static final String CW_TABLE_DOMAIN_TABLES_METADATA = "AUX.SAP_DOMAINTABLES_METADATA";
	public static final String CW_COLUMN_DOMAIN_TABLE = "CW_DOMAIN_TABLE";
	
	public static final String CW_TABLE_DATA_TABLES_METADATA = "AUX.SAP_DATATABLES_METADATA";
	public static final String CW_COLUMN_KEY_FIELD = "KEYFIELD";
	public static final String CW_KEY_FIELD_VALUE = "ADM_SEGNUM"; // The KEYFIELD value we filter out
	
	public static final String CW_TABLE_FIELDS_METADATA = "AUX.SAP_FIELDS_METADATA";
	public static final String CW_COLUMN_DOMAIN_NAME = "DOMNAME";
	public static final String CW_COLUMN_LENGTH = "LENG";
	public static final String CW_COLUMN_KEY_FLAG = "KEYFLAG";
	public static final String CW_COLUMN_DOMAIN_TRANSCODING_TABLE = "CW_DOMAIN_TRANSCODING_TABLE";
	public static final String CW_COLUMN_TRANSCODING_TABLE_SOURCE_FIELD = "TRANSCODING_TABLE_SOURCE_FIELD";
	public static final String CW_COLUMN_TRANSCODING_TABLE_TARGET_FIELD = "TRANSCODING_TABLE_TARGET_FIELD";

	public static final String CW_TABLE_FOREIGN_KEYS = "AUX.SAP_FOREIGN_KEYS";
	public static final String CW_COLUMN_SAP_CHECK_FIELD = "SAP_CHECKFIELD";
	public static final String CW_COLUMN_CHECK_FIELD = "CHECKFIELD";
	public static final String CW_COLUMN_FOREIGN_TABLE = "FORTABLE";
	public static final String CW_COLUMN_FOREIGN_KEY = "FORKEY";
	
	// Replacement value for empty, non-null string values (Oracle lacks real support for these)
	public static final String ORACLE_EMPTY_STRING = " ";
	
	// Values for boolean columns in CW tables
	public static final String CW_TRUE_VALUE = "X";
	public static final String CW_FALSE_VALUE = "";
	
	/****************
	 * BDR constants
	 ****************/

	public static final String BDR_SUPERUSER_ROLE = "Administrator";
	public static final String BDR_READONLY_ROLE = "ReadOnly";
	public static final String SESSION_ATTRIBUTE_NAME_BPH_IMPORT_FILE = "bphImportFile";
	public static final String SESSION_ATTRIBUTE_NAME_BPH_IMPORT_THREAD = "bphImportThread";
	public static final String SESSION_ATTRIBUTE_NAME_BDR_FIELD_IMPORT_THREAD = "fieldDataImportThread";
	public static final String SESSION_ATTRIBUTE_NAME_BDR_TABLES_IMPORT_THREAD = "tablesDataImportThread";
	public static final String SESSION_ATTRIBUTE_NAME_BDR_EXPORT_THREAD = "bdrExportThread";
	public static final String SESSION_ATTRIBUTE_NAME_BDR_EXPORT_FILE_CONTENT = "brdExportFile";
	public static final String SESSION_ATTRIBUTE_NAME_BDR_DB_EXPORT_THREAD = "bdrDBExportThread";
	
	// BDR export
	public static final String BDR_EXPORT_LINE_SEPARATOR = "\n";
	public static final String BDR_EXPORT_CSV_PATH_SEPARATOR = ";";
	public static final String BDR_EXPORT_GAP_FILLER = "";
	/* Width of the fixed part of the tree hierarchy,
	 * e.g. process step + description, transactions, business objects +
	 * description, table name, required, use, comment... */
	public static final int BDR_EXPORT_WIDTH_PROCESSSTEPS_TO_FIELDS = 18;
	public static final int BDR_SPECIAL_SCOPE_EXPORT_WIDTH_PROCESSSTEPS_TO_FIELDS = 4;
	public static final int BDR_TABLES_EXPORT_WIDTH = 7;
	public static final String BDR_EXPORT_PROCESS_NAME = "PROCESS";
	public static final String BDR_EXPORT_PROCESS_DESC = "PROCESS DESCRIPTION";
	public static final String BDR_EXPORT_PROCESS_STEP = "PROCESS STEP";
	public static final String BDR_EXPORT_PROCESS_STEP_DESC = "PROCESS STEP DESCRIPTION";
	public static final String BDR_EXPORT_TRANSACTIONS = "TRANSACTIONS";
	public static final String BDR_EXPORT_BUSINESS_OBJECT_NAME = "BUSINESS OBJECT";
	public static final String BDR_EXPORT_BUSINESS_OBJECT_SHORTNAME = "BUSINESS OBJECT SHORTNAME";
	public static final String BDR_EXPORT_BUSINESS_OBJECT_DESC = "BUSINESS OBJECT DESCRIPTION";
	public static final String BDR_EXPORT_TABLE_NAME = "TABLE";
	public static final String BDR_EXPORT_TABLE_DESC = "TABLE DESCRIPTION";
	public static final String BDR_EXPORT_TABLE_USAGE_APPROVAL_STATUS = "TABLE USAGE APPROVAL STATUS";
	public static final String BDR_EXPORT_FIELD_NAME = "FIELD";
	public static final String BDR_EXPORT_FIELD_REQUIRED = "FIELD REQUIRED";
	public static final String BDR_EXPORT_FIELD_USE_MODE = "FIELD USE MODE";
	public static final String BDR_EXPORT_FIELD_USAGE_COMMENT = "FIELD USAGE COMMENT";
	public static final String BDR_EXPORT_FIELD_CHECK_TABLE = "FIELD CHECK TABLE";
	public static final String BDR_EXPORT_FIELD_RECOMMENDED = "FIELD RECOMMENDED";
	public static final String BDR_EXPORT_FIELD_SAP_VIEW = "FIELD SAP VIEW";
	public static final String BDR_EXPORT_FIELD_DESC = "FIELD DESCRIPTION";
	public static final String BDR_EXPORT_TRANSACTION_NAME = "Name";
	public static final String BDR_EXPORT_TRANSACTION_COMMENT = "Comment";
	public static final String BDR_EXPORT_GT = "GLOBAL TEMPLATE";
	
	// BDR import
	public static final String BDR_IMPORT_ERROR_INTERNAL = "internal_error";
	public static final String BDR_IMPORT_ERROR_INVALID_FILE = "invalid_file";
	public static final String BDR_IMPORT_ERROR_INVALID_FILE_TYPE = "invalid_file_type";
	public static final String BDR_IMPORT_ERROR_INVALID_DATA_FILE = "invalid_data_file";
	public static final String BDR_IMPORT_ERROR_INVALID_HEADER = "invalid_data_header";
	public static final String BDR_IMPORT_ERROR_INVALID_PROCESS_HIERARCHY = "invalid_process_hierarchy";
	public static final String BDR_IMPORT_ERROR_THREAD_CANCELLED = "thread_cancelled";
	public static final String BDR_IMPORT_SUCCESS_SOLMAN = "success_solman";
	public static final String BDR_IMPORT_SUCCESS_CSV_EMPTY = "success_csv_empty";
	public static final String BDR_IMPORT_SUCCESS_CSV_PROCESSES = "success_csv_processes";
	public static final String BDR_IMPORT_SUCCESS_CSV_BOS = "success_csv_bos";
	public static final String BDR_IMPORT_SUCCESS_NO_CHANGES = "no_changes";
	
	// Used in the field usage reports
	public static final String PROCESS_CHAIN_SEPARATOR = " -> ";
	
	/****************
	 * RDM constants
	 ****************/

	// RDM Hub export
	
	public static final String TABLE_DATA_DESCRIPTION_TAG = "__descr__";
	public static final String TABLE_DATA_UNIQUE_ID_TAG = "__code__";
	
	public static final String RDM_TYPE_URI = "/RestAPI/jaxrs/dataType/";
	public static final String RDM_SET_URI = "/RestAPI/jaxrs/sets/";
	public static final String RDM_MAPPINGS_URI = "/RestAPI/jaxrs/relationships/";
	public static final String RDM_VALUES = "values";
	public static final String RDM_NEW_VERSION = "newVersion";
	
	public static final String RDM_CODE_CONCAT_SEP = "_"; // Concatenation separator in RDM value keys, e.g. "800_ABC_1"
	
	public static final String SESSION_ATTRIBUTE_NAME_DATA_MODEL = "parsedDataModel";
	public static final String SESSION_ATTRIBUTE_NAME_RDM_LOAD_THREAD = "rdmLoadThread";
	public static final String SESSION_ATTRIBUTE_NAME_RDM_SOURCE_EXPORT_THREAD = "sourceReferenceDataExportThread";
	public static final String SESSION_ATTRIBUTE_NAME_RDM_EXPORT_THREAD = "referenceDataExportThread";
	public static final String SESSION_ATTRIBUTE_NAME_RDM_CLEANUP_THREAD = "referenceDataCleanupThread";
	public static final String SESSION_ATTRIBUTE_NAME_RDM_CREATION_THREAD = "rdmMappingsCreationThread";
	public static final String SESSION_ATTRIBUTE_NAME_RDM_TT_IMPORT_THREAD = "transcodingTablesImportThread";
	public static final String SESSION_ATTRIBUTE_NAME_RDMPASSWORD = "rdmpassword";
	public static final String SESSION_ATTRIBUTE_NAME_TARGETSAPPASSWORD = "tgtsappassword";
	
	public static final String SETTING_RDM_HOST = "RDM_HOST";
	public static final String SETTING_RDM_PORT = "RDM_PORT";
	public static final String SETTING_RDM_USER = "RDM_USER";
	public static final String SETTING_RDM_LANGUAGE = "RDM_LANGUAGE";
	public static final String SETTING_RDM_LANGUAGE_DEFAULT = "E";
	public static final String RDM_URL_PREFIX = "http://";
	
	public static final String COMETD_TOPIC_EXPORT_ROW_COUNT = "/export/rowcount";
	public static final String COMETD_TOPIC_EXPORT_ROW_PROGRESS = "/export/rowprogress";
	public static final String COMETD_TOPIC_EXPORT_TABLE_STATUS = "/export/status";
	
	public static final int EXPORT_STATUS_OK = 0;
	public static final int EXPORT_STATUS_INTERNAL_ERROR = 1;
	public static final int EXPORT_STATUS_TABLE_NOT_FOUND = 2;
	public static final int EXPORT_STATUS_IN_PROGRESS = 3;
	public static final int EXPORT_STATUS_SET_NOT_CHANGED = 4;
	
	// Source reference data page: JSON request value names
	public static final String JSON_LEGACY_ID = "legacyId";
	public static final String JSON_LEGACY_ID_DESCRIPTION = "legacyIdDescription";
	public static final String JSON_REFERENCE_TABLE_ID = "referenceTableId";
	
	// RDM REST API request constants
	public static final String RDM_INTERNAL_CLIENT_ID = "__clientId";
	public static final String RDM_INTERNAL_ID = "__id";
	public static final String RDM_IS_DIRTY = "__isDirty";
	public static final String RDM_REGEX = "RegEx";
	public static final String RDM_REST_OPERATION = "REST_operation";
	public static final String RDM_REST_OPERATION_ADD = "add";
	public static final String RDM_EFF_DATE = "EffDate";
	public static final String RDM_EXP_DATE = "ExpDate";
	public static final String RDM_REV_DATE = "RevDate";
	public static final String RDM_REVISED_DATE = "LastRevisedDate";
	
	public static final String RDM_TYPE_NAME = "Name";
	public static final String RDM_TYPE_WILDCARD = "%2C0";
	public static final String RDM_TYPE_ID = "TypeId";
	public static final String RDM_TYPE_DESCRIPTION = "Description";
	public static final String RDM_SUPPORTS_COMPOUND_KEY = "SupportsCompoundKey";
	public static final String RDM_TYPE_OF_TYPE = "typeOfType";
	public static final String RDM_TYPE_COLUMN_NAME = "TypeName";
	public static final String RDM_TYPE_COLUMN_DESCRIPTION = "Description";
	public static final String RDM_TYPE_COLUMN_CODE = "DataTypeCode";
	public static final String RDM_TYPE_COLUMN_CODE_VALUE = "2";
	public static final String RDM_TYPE_COLUMN_UNIQUE = "Unique";
	public static final String RDM_TYPE_COLUMN_IS_REQUIRED = "IsRequired";
	public static final String RDM_TYPE_COLUMN_IS_KEY = "IsKey";
	public static final String RDM_TYPE_COLUMN_VALUE_SET_ID = "ValueSetID";
	public static final String RDM_TYPE_COLUMN_VALIDATION_RULE = "ValidationRule";
	public static final String RDM_TYPE_COLUMN_DEFAULT_VALUE = "DefaultValue";
	public static final String RDM_TYPE_COLUMN_LENGTH = "Length";
	public static final String RDM_TYPE_COLUMN_DATA_TYPE = "DataType";
	public static final String RDM_TYPE_COLUMN_DATA_TYPE_STRING = "String";
	public static final String RDM_TYPE_COLUMN_INTERNAL_ID_STRING = "0/valueLevel/e8f50ba75da3@client";
	
	public static final String RDM_SET_NAME = "Name";
	public static final String RDM_SET_WILDCARD = "*";
	public static final String RDM_SET_VERSION = "Version";
	public static final String RDM_SET_NEW_VERSION = "NewVersion";
	public static final String RDM_SET_BASE_ID = "BaseID";
	public static final String RDM_SET_VERSION_ID = "ID";
	public static final String RDM_SET_DESCRIPTION = "Desc";
	public static final String RDM_SET_TYPE_ID = "TypeID";
	public static final String RDM_SET_STATE_MACHINE= "StateMachine";
	public static final String RDM_SET_STATE_MACHINE_VALUE = "1";
	public static final String RDM_SET_OWNER = "Owner";
	public static final String RDM_SET_OWNER_VALUE = "crm,enterprise,mdm";
	public static final String RDM_SET_COPY_OPTION = "CopyOption=nothing";
	
	public static final String RDM_SET_VALUE_NAME = "Name";
	public static final String RDM_SET_VALUE_DESCRIPTION = "Desc";
	public static final String RDM_SET_VALUE_CODE = "Code";
	public static final String RDM_SET_VALUE_ID = "ID";
	public static final String RDM_SET_VALUE_ID_VALUE = "new - value 1";
	public static final String RDM_SET_VALUE_STATE_VALUE = "StateValue";
	public static final String RDM_SET_VALUE_STATE_VALUE_VALUE = "Draft";
	public static final String RDM_SET_VALUE_STATE_CODE = "StateCode";
	public static final String RDM_SET_VALUE_SEQNUM = "SeqNum";
	public static final String RDM_SET_VALUE_NEW = "New";
	public static final String RDM_SET_VALUE_STANDARD_ID = "Standard ID";
	
	public static final String RDM_MAPPING_NAME = "Name";
	public static final String RDM_MAPPING_BASE_ID = "BaseID";
	public static final String RDM_MAPPING_VERSION_ID = "ID";
	public static final String RDM_MAPPING_VERSION = "Version";
	public static final String RDM_MAPPING_SRC_Version_ID = "SrcSetIdPK";
	public static final String RDM_MAPPING_SRC_NAME = "SrcSetName";
	public static final String RDM_MAPPING_TGT_Version_ID = "TgtSetIdPK";
	public static final String RDM_MAPPING_TGT = "TgtSet";
	public static final String RDM_MAPPING_SRC = "SrcSet";
	public static final String RDM_MAPPING_TGT_NAME = "TgtSetName";
	public static final String AVAILABLE_RDM_VERSION = "AvailableVersion";
	
	// Other RDM constants (Mappings page etc.)
	
	public static final String STATUS_CONFLICT = "CONFLICT";
	public static final String LEGACYID = "LEGACYID";
	public static final String STATUS ="STATUS";
	public static final String STATUS_REMOVED = "REMOVED";
	public static final String T_TABLE_NAME = "TTName";
	public static final String NUMBER_OF_SRC_VALUES = "SrcValues";
	public static final String NUMBER_OF_MAPPED_VALUES = "MappedSrcValues";
	public static final String EMPTY_STRING = "";
	
	// The domain table layout is fixed. Only the "lower" domain value is extracted.
	// The extraction of range values (stored in DOMVALUE_L and DOMVALUE_H) is not supported.
	// Warning: some code currently relies on the positions of the column names in the array.
	public static final String[] DOMAIN_TABLE_COLUMNS = {"VALPOS", "DOMVALUE_L", "DDTEXT"};
	// There can be empty values, those will be replaced with a placeholder.
	public static final String RDM_EMPTY_VALUE_ID = "<empty>";
	// Since all domain tables have the same column layout, use one type for them all in RDM Hub
	public static final String DOMAIN_TABLE_RDM_TYPE = "CW_SAP_DOMAIN";

	/******************
	 * Config constants
	 ******************/
	
	// CW Legacy system table: target system tag column name
	public static final String CW_LEGACY_ID_TARGET_SYSTEM_COLUMN = "ISTARGETSYSTEM";
	public static final String CW_LEGACY_ID_COLUMN = "CW_LEGACY_ID";
	public static final String CW_LEGACY_ID_DESCRIPTION_COLUMN = "DESCRIPTION";
	public static final String CW_LEGACY_ID_SHORTNAME_COLUMN = "SHORTNAME";
	public static final String CW_LEGACY_ID_SAP_HOST_COLUMN = "HOST";
	public static final String CW_LEGACY_ID_SAP_CLIENT_COLUMN = "CLIENT";
	public static final String CW_LEGACY_ID_SAP_GROUPNAME_COLUMN = "GROUPNAME";
	public static final String CW_LEGACY_ID_SAP_LANGUAGE_COLUMN = "LANGUAGE";
	public static final String CW_LEGACY_ID_SAP_MESSAGESERVER_COLUMN = "MESSAGESERVER";
	public static final String CW_LEGACY_ID_SAP_ROUTERSTRING_COLUMN = "ROUTERSTRING";
	public static final String CW_LEGACY_ID_SAP_SAPSYSTEMID_COLUMN = "SAPSYSTEMID";
	public static final String CW_LEGACY_ID_SAP_SYSTEMNUMBER_COLUMN = "SYSTEMNUMBER";
	public static final String CW_LEGACY_ID_SAP_USELOADBALANCING_COLUMN = "USELOADBALANCING";
	public static final String CW_LEGACY_ID_SAP_USER_COLUMN = "\"USER\"";
	public static final String CW_LEGACY_ID_SAP_USER_COLUMN_GENERIC = "USER";
	public static final String CW_LEGACY_ID_TABLENAME = "AUX.LEGACY_SYSTEM";
	public static final String CW_LEGACY_ID_SAP_TABLENAME = "AUX.SAP_SYSTEM";
	
	public static final String SORT_BY_DESCRIPTION_ASC = "(+description)";
	public static final String SORT_BY_DESCRIPTION_DESC = "(-description)";
	public static final String SORT_BY_LEGACYID_ASC = "(+legacyId)";
	public static final String SORT_BY_LEGACYID_DESC = "(-legacyId)";
	
	// CW schemas table and area schema codes
	public static final String CW_SCHEMAS_TABLENAME = "AUX.CW_SCHEMAS";
	public static final String CW_SCHEMAS_SCHEMA_TYPE_COLUMN = "SCHEMA_TYPE";
	public static final String CW_SCHEMAS_SCHEMA_NAME_COLUMN = "SCHEMA_NAME";
	public static final String CW_SCHEMAS_AREACODE_ALG0 = "ALG0";
	public static final String CW_SCHEMAS_AREACODE_ALG1 = "ALG1";
	public static final String CW_SCHEMAS_AREACODE_PLD = "PLD";
	public static final String CW_SCHEMAS_AREACODE_PLD_IDOC = "PLD_IDOC";
	public static final String CW_SCHEMAS_AREACODE_PSTLD = "PSTLD";
	
	/******************
	 * Exception codes
	 * if a exception is thrown to the client there are some possible combinations of HTTP code and message
	 * HTTP code 500 (internal error) is always thrown without message
	 ******************/
	
	//status code 401, HttpURLConnection.HTTP_UNAUTHORIZED
	public static final String RDMLOGINERROR = "RdmLoginErrorMsg";
	//status code 404, HttpURLConnection.HTTP_NOT_FOUND appears when the schema in the settings is not set
	public static final String SETTINGSSCHEMAERROR = "SchemaNotSetMsg";
	public static final String SAPVIEW_SEPARATOR="|";
	public static final String REMOVE_FILTER_TAG="--Remove Filter--";
	public static final String UNDEFINED_TAG="--Undefined--";
	public static final String SAPVIEW_WILDCARD="%";
}