//  General

function Help() {}

Help.ONLINEURL = "http://publib.boulder.ibm.com/infocenter/mdm/v10r0m0/index.jsp";
Help.IBMSUPPORTURL = "http://www.ibm.com/software/support/";

function General () {}

General.DATE_FORMAT = "yyyy-MM-dd kk:mm";
General.ISBDRSUPERUSER = undefined;
General.ISREADONLY = undefined;
General.ERROR_HTML_BEGIN = '<span style="color: red">';
General.ERROR_HTML_END = '</span>';

function UrlParams () {}

UrlParams.TAB = "tab";
UrlParams.TAB_BDR = "bdr";
UrlParams.TYPE = "type";
UrlParams.ID = "id";

// Config

function SapSystem () {}

SapSystem.NAME = "SAP_NAME";
SapSystem.HOST_NAME = "SAP_HOST";
SapSystem.DESCRIPTION = "SAP_DESC";
SapSystem.GROUPNAME = "SAP_GRPNAME";
SapSystem.LANGUAGE = "SAP_LANGUAGE";
SapSystem.LEGACYID = "SAP_LEGACYID";
SapSystem.SYSTEMID = "SAP_SYSID";
SapSystem.SYSTEMNUMBER = "SAP_SYSNUM";
SapSystem.MESSAGESERVER = "SAP_MSGSERVER";
SapSystem.ROUTERSTRING = "SAP_ROUTERSTRING";
SapSystem.USER_NAME = "SAP_USER";
SapSystem.PASSWORD = "TGT_SAP_PASSWORD";
SapSystem.USELOADBALANCING = "SAP_USELB";
SapSystem.CLIENT = "SAP_CLIENTNUM";

function Settings () {}

Settings.RDM_HOST_NAME = "RDM_HOST";
Settings.RDM_PORT_NAME = "RDM_PORT";
Settings.RDM_USER_NAME = "RDM_USER";
Settings.RDM_PASSWORD_NAME = "RDM_PASSWORD";
Settings.RDM_LANGUAGE_NAME = "RDM_LANGUAGE";

// BDR

function BdrTypes() {}

BdrTypes.PROCESS = "Process";
BdrTypes.PROCESS_STEP = "ProcessStep";
BdrTypes.BO = "BusinessObject";
BdrTypes.TABLE = "Table";
BdrTypes.TABLE_USAGE = "TableUsage";

// Field length limits, based on the CW tables where applicable
// Need to match the ones in Resources.java
function BdrAttributeLengths() {}

BdrAttributeLengths.PROCESS_NAME = "255";
BdrAttributeLengths.PROCESS_STEP_NAME = "255";
BdrAttributeLengths.BO_NAME = "255";
BdrAttributeLengths.BO_SHORT_NAME = "10"; // Workaround: We write BO names into the LOB field when exporting to CWDB. The LOB name length is 10.
BdrAttributeLengths.TABLE_NAME = "30";
BdrAttributeLengths.FIELD_NAME = "30";
BdrAttributeLengths.CW_LEGACY_ID = "20";
BdrAttributeLengths.ROLLOUT = "30";
BdrAttributeLengths.SAP_VIEW = "100";
BdrAttributeLengths.TRANSACTION_NAME = "100";
BdrAttributeLengths.DESCRIPTION = "255";
BdrAttributeLengths.COMMENT = "255";
BdrAttributeLengths.CHECKTABLE = "30";

function BdrApprovalStatusCodes() {}

BdrApprovalStatusCodes.DRAFT = 0;
BdrApprovalStatusCodes.PENDING_APPROVAL = 1;
BdrApprovalStatusCodes.APPROVED = 2;
BdrApprovalStatusCodes.MIXED = 3;
BdrApprovalStatusCodes.UNDEFINED = -1;

function BdrFieldUsageScopeCodes() {}

BdrFieldUsageScopeCodes.READ = 0; // Deprecated
BdrFieldUsageScopeCodes.WRITE = 1; // Deprecated
BdrFieldUsageScopeCodes.UNUSED = 3; // Deprecated
BdrFieldUsageScopeCodes.IN_SCOPE = 4;
BdrFieldUsageScopeCodes.NOT_IN_SCOPE = 5;
BdrFieldUsageScopeCodes.FOLLOW_UP = 2;
BdrFieldUsageScopeCodes.BLANK = 6;

function BdrFieldUsageStatusCodes() {}

BdrFieldUsageStatusCodes.REQUIRED_BUT_NEVER_WRITTEN = 0;
BdrFieldUsageStatusCodes.READ_BUT_NEVER_WRITTEN = 1;
BdrFieldUsageStatusCodes.MULTIPLE_WRITES = 2;
BdrFieldUsageStatusCodes.REQUIRED_BUT_FOLLOWUP = 3;
BdrFieldUsageStatusCodes.REQUIRED_BUT_NOT_IN_SCOPE = 4;
BdrFieldUsageStatusCodes.REQUIRED_BUT_BLANK = 5;
BdrFieldUsageStatusCodes.OK = -1;

function BphImportTypes() {}

BphImportTypes.MPX = "mpx";
BphImportTypes.CSV_COMPLETE = "csvComplete";
BphImportTypes.CSV_BOS = "csvBOs";
BphImportTypes.CSV_PROCESSES = "csvProcesses";
BphImportTypes.CSV_TABLES = "csvTables";
BphImportTypes.CSV_SAPVIEW = "csvSapViewTables";

// -------------- Services

function Services() {}

Services.BASEURL = "/com.ibm.is.sappack.cw.app.services/jaxrs/";

Services.SESSIONRESTURL = Services.BASEURL + "session";
Services.BUILD_NUMBER = Services.BASEURL + "buildNumber";
Services.COMETD = "/com.ibm.is.sappack.cw.app.services/webmsg";

// RDM

Services.REFTABLERESTURL        = Services.BASEURL + "reftables/retrieve";
Services.REFTABLECLEANUPRESTURL = Services.BASEURL + "reftables/cleanup";

Services.MAPPINGRESTURL        = Services.BASEURL + "mapping";
Services.MAPPINGDELETERESTURL  = Services.BASEURL + "mapping/cancel";
Services.MAPPINGDELETERESTURL  = Services.BASEURL + "mapping/cancel";
Services.MAPPINGTHREADSTARTURL = Services.BASEURL + "mapping/startThread";

Services.CREATION_URL         = Services.BASEURL + "createMappings";
Services.CREATION_CANCEL_URL  = Services.BASEURL + "createMappings/cancel";
Services.CREATION_STARTTHREAD = Services.BASEURL + "createMappings/startThread";

Services.EXPORT_URL         = Services.BASEURL + "reftable/export";
Services.EXPORT_STARTTHREAD = Services.BASEURL + "reftable/startThread";
Services.EXPORT_CANCEL_URL  = Services.BASEURL + "reftable/cancel";

Services.SOURCE_DATA                    = Services.BASEURL + "sourcedata/retrieve";
Services.SOURCE_DATA_EXPORT             = Services.BASEURL + "sourcedata/export";
Services.SOURCE_DATA_EXPORT_STARTTHREAD = Services.BASEURL + "sourcedata/startThread";
Services.SOURCE_DATA_EXPORT_CANCEL      = Services.BASEURL + "sourcedata/cancel";

Services.RDM_LOAD_GET_ROLLOUTS = Services.BASEURL + "rdmLoad/getRollouts";
Services.RDM_LOAD_GET_BOS      = Services.BASEURL + "rdmLoad/getBOs";
Services.RDM_LOAD_PREVIEW      = Services.BASEURL + "rdmLoad/preview";
Services.RDM_LOAD_INIT         = Services.BASEURL + "rdmLoad/init";
Services.RDM_LOAD_START_THREAD = Services.BASEURL + "rdmLoad/startThread";
Services.RDM_LOAD_CANCEL       = Services.BASEURL + "rdmLoad/cancel";

// Config

Services.OVERALLSYSTEMRESTURL = Services.BASEURL + "systems";
Services.SOURCESYSTEMRESTURL = Services.BASEURL + "systems/src";
Services.TARGETSAPSYSTEMRESTURL = Services.BASEURL + "systems/tgt";
Services.SYSTEMIDATTRIBUTE = "legacyId";

Services.EXPORTSYSTEMTOCXPURL = Services.BASEURL + "exportSAPConnection";

Services.SETTINGRESTURL = Services.BASEURL + "settings";
Services.SETTINGIDATTRIBUTE = "settingId";

Services.RDMPASSWORDRESTURL = Services.BASEURL + "pwd/rdm";
Services.SAPPASSWORDRESTURL = Services.BASEURL + "pwd/sap";

Services.RDMCONNECTIONTESTRESTURL = Services.BASEURL + "rdmtest";
Services.RDMCONNECTIONTESTRESTSTATUS = "200";

Services.SAPCONNECTIONTESTRESTURL = Services.BASEURL + "saptest";

// BDR

Services.BDR_BASEURL = Services.BASEURL + "bdr/";

Services.BDR_GET_TREE            = Services.BDR_BASEURL + "tree";
Services.BDR_PROCESS             = Services.BDR_BASEURL + "process";
Services.BDR_PROCESS_STEP        = Services.BDR_BASEURL + "processStep";
Services.BDR_TABLE               = Services.BDR_BASEURL + "tables";
Services.BDR_TABLE_FIELD         = Services.BDR_BASEURL + "field/table";
Services.BDR_TABLE_FIELD_REPORT  = Services.BDR_BASEURL + "field/report";
Services.BDR_TABLE_FIELD_SAPVIEW  = Services.BDR_BASEURL + "field/sapView";
Services.BDR_TABLE_USAGE_FIELD_SAPVIEW = Services.BDR_BASEURL + "fieldusage/sapView";
Services.BDR_SAPVIEWIMPORT_UPLOAD = Services.BDR_BASEURL + "bphimportfile/sapViewUpload";
Services.BDR_TABLE_FIELDUSAGE_REPORT  = Services.BDR_BASEURL + "fieldusage/report";
Services.BDR_TABLE_USAGE_FIELD   = Services.BDR_BASEURL + "fieldusage/tableusage";
Services.BDR_TABLE_USAGE         = Services.BDR_BASEURL + "tableusage";
Services.BDR_TABLE_USAGE_TABLE   = Services.BDR_BASEURL + "tableusage/tablefor";
Services.BDR_BO                  = Services.BDR_BASEURL + "businessobjects/";
Services.BDR_BO_ALL				 = Services.BDR_BO + "getAllBusinessObjects";
Services.BDR_BO_DELETE			 = Services.BDR_BO + "deleteBusinessObject";
Services.BDR_BO_UPDATE			 = Services.BDR_BO + "updateBusinessObject";
Services.BDR_ACCESSINFO_SERVICE  = Services.BDR_BASEURL + "objectaccess";
Services.BDR_USER_HAS_REQUIRED_ROLE = Services.BDR_ACCESSINFO_SERVICE + "/hasRequiredRole";
Services.BDR_EXPORT_BDR_TO_FILE = Services.BDR_BASEURL + "export/toFile";
Services.BDR_PREPARE_EXPORT_TO_FILE = Services.BDR_BASEURL + "export/prepareExport";
Services.BDR_START_EXPORT_TO_FILE = Services.BDR_BASEURL + "export/startThread";
Services.BDR_REMOVE_EXPORT_FILE = Services.BDR_BASEURL + "export/removeFile";

Services.BDR_FIELD_IMPORT = Services.BASEURL + "bdrFieldData/import";
Services.BDR_FIELD_IMPORT_START = Services.BASEURL + "bdrFieldData/startThread";
Services.BDR_FIELD_CANCEL = Services.BASEURL + "bdrFieldData/cancel";

Services.BDR_FIELD_REPLACE = Services.BASEURL + "bdrFieldData/replace";

Services.BDR_TABLES_IMPORT = Services.BASEURL + "bdrTablesData/import";
Services.BDR_TABLES_IMPORT_START = Services.BASEURL + "bdrTablesData/startThread";

Services.BDR_BPH_IMPORTFILE        = Services.BDR_BASEURL + "bphimportfile/retrieve";
Services.BDR_BPH_IMPORTFILE_UPLOAD = Services.BDR_BASEURL + "bphimportfile/upload";
Services.BDR_BPH_IMPORTFILE_DELETE = Services.BDR_BASEURL + "bphimportfile/delete";

Services.BDR_BPH_IMPORT = Services.BDR_BASEURL + "bphimport/import";
Services.BDR_BPH_IMPORT_START = Services.BDR_BASEURL + "bphimport/startThread";

Services.BDR_EXPORT_TO_CWDB_GET_PARAMS     = Services.BDR_BASEURL + "exportToCwdb/getParams";
Services.BDR_EXPORT_TO_CWDB                = Services.BDR_BASEURL + "exportToCwdb/export";
Services.BDR_START_EXPORT_TO_CWDB		   = Services.BDR_BASEURL + "exportToCwdb/startThread";
Services.BDR_EXPORT_TO_CWDB_CHECK_EXISTING = Services.BDR_BASEURL + "exportToCwdb/checkExisting";

Services.BDR_DATAMODELUPLOADRESTURL = Services.BASEURL + "datamodel/upload";
Services.BDR_DATAMODELDELETERESTURL = Services.BASEURL + "datamodel/delete";

Services.BDR_GETNUMBEROFBOSANDTABLES = Services.BDR_BASEURL + "bphimport/numberOfBusinessObjectsAndTablesDefined";

//----------- Dojo topics

function Topics() {}

// RDM

Topics.REFERENCEDATALOADWIZARD_NEXT = "nextPressed";
Topics.REFERENCEDATALOADWIZARD_BACK = "backPressed";

Topics.RDM_REFRESH_TARGET_PAGE_NOW = "rdmRefreshTargetPageNow";
Topics.RDM_REFRESH_SOURCE_PAGE_NOW = "rdmRefreshSourcePageNow";
Topics.RDM_REFRESH_MAPPING_PAGE_NOW = "rdmRefreshMappingPageNow";

Topics.RDM_REFRESH_SOURCE_PAGE_WHEN_SHOWN = "rdmRefreshSourcePageWhenShown";
Topics.RDM_REFRESH_MAPPING_PAGE_WHEN_SHOWN = "rdmRefreshMappingPageWhenShown";

// Config

Topics.RUNSAPSYSTEMPASSWORDCHECK = "runSapSystemPasswordCheck";
Topics.SAPSYSTEMPASSWORDCHECKDONE = "sapSystemPasswordCheckDone";

// BDR

Topics.REFRESH_BPH = "refreshBPH";
Topics.REFRESH_BPH_WHEN_SHOWN = "refreshBPHwhenShown";
Topics.REFRESH_BO_WHEN_SHOWN = "refreshBO";
Topics.REFRESH_TABLES_WHEN_SHOWN = "refreshTableswhenShown";
Topics.ATTACH_BO = "attachBO";
Topics.REMOVE_BPH_ITEM = "removeBphItem";
Topics.TABLESTOATTACHSELECTED = "tablesToAttachSelected";
Topics.DETAILS_TAB_SELECTED = "detailsTabSelected";
Topics.REFRESH_DETAILSPANEL_BO=" refreshDetailsPanelBO";
Topics.REFRESH_DETAILSPANEL_TABLES= "refreshDetailsPanelTables";
// ----------- CometD / Bayeux topics

function CometD() {}

// RDM

CometD.TABLE_FINISHED_TOPIC = "/table/success";
CometD.MAPPING_FINISHED_TOPIC = "/mapping/success";

CometD.TABLE_STATUS_ROW_COUNT_TOPIC = "/table/rowcount";
CometD.TABLE_STATUS_ROW_NUMBER_TOPIC = "/table/rownumber";
CometD.TABLE_STATUS_TOPIC = "/table/status";

CometD.TOPIC_EXPORT_ROW_COUNT = "/export/rowcount";
CometD.TOPIC_EXPORT_ROW_PROGRESS = "/export/rowprogress";
CometD.TOPIC_EXPORT_TABLE_STATUS = "/export/status";

CometD.EXPORT_STATUS_OK = 0;
CometD.EXPORT_STATUS_INTERNAL_ERROR = 1;
CometD.EXPORT_STATUS_TABLE_NOT_FOUND = 2;
CometD.EXPORT_STATUS_IN_PROGRESS = 3;
CometD.EXPORT_STATUS_SET_NOT_CHANGED = 4;

CometD.InternalError = "InternalErrorMsg";
CometD.UnAuthorized = "UnAuthorizedMsg";
CometD.OK = "OK";
CometD.CREATING = "creatingMsg";
CometD.SET_MISSING_IN_CWAPP = "setMissingCWMsg";
CometD.SET_MISSING_IN_RDM_HUB = "setMissingRDMMsg";
CometD.CONFLICT = "conflictMsg";
CometD.MAPPING_EXISTS = "mappingExists";

CometD.NO_PROGRESS_TIMEOUT = 30000;

CometD.TOPIC_CLEANUP_FINISHED = "/cleanup/done";

// BDR
CometD.TOPIC_TABLE_STATUS_UPDATE = "/bdr/tablefieldimport/updated";
CometD.TOPIC_TABLE_LOAD_UPDATE = "/bdr/tablefieldimport/loadupdate";

CometD.TOPIC_FIELD_IMPORT_STARTED = "/bdr/fieldimport/started";
CometD.TOPIC_FIELD_IMPORT_FINISHED = "/bdr/fieldimport/finished";
CometD.TOPIC_TABLES_IMPORT_STARTED = "/bdr/tablesimport/started";
CometD.TOPIC_TABLES_IMPORT_FINISHED = "/bdr/tablesimport/finished";
CometD.TOPIC_BPH_IMPORT_STARTED = "/bdr/bphimport/started";
CometD.TOPIC_BPH_IMPORT_FINISHED = "/bdr/bphimport/finished";
CometD.TOPIC_BDR_EXPORT_STARTED  = "/bdr/bdrexport/started";
CometD.TOPIC_BDR_EXPORT_FINISHED = "/bdr/bdrexport/finished";
CometD.TOPIC_BDR_EXPORT_DB_STARTED  = "/bdr/bdrdbexport/started";
CometD.TOPIC_BDR_EXPORT_DB_FINISHED = "/bdr/bdrdbexport/finished";

function CWAppConfiguration() {}

CWAppConfiguration.BASE_URL = "/ibm/iis/cw/CWApp";
CWAppConfiguration.SERVLET_URL = CWAppConfiguration.BASE_URL + "/config";
