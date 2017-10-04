package com.ibm.icu.impl.data;

import java.util.ListResourceBundle;
import com.ibm.icu.impl.ICUListResourceBundle;

public class Bundle_CC_IDOC_msgs_en_US extends ICUListResourceBundle {

    public Bundle_CC_IDOC_msgs_en_US  () {
          super.contents = data;
    }
    static final Object[][] data = new Object[][] { 
                {
                    "CC_COMMON_PRPDSC_JAVA_CATEGORY",
                    "CC_COMMON_PRPDSC_JAVA_CATEGORY",
                },
                {
                    "CC_COMMON_PRPDSC_JAVA_CLASSPATH",
                    "CC_COMMON_PRPDSC_JAVA_CLASSPATH",
                },
                {
                    "CC_COMMON_PRPDSC_JAVA_JVM_OPTIONS",
                    "CC_COMMON_PRPDSC_JAVA_JVM_OPTIONS",
                },
                {
                    "CC_COMMON_PRPLBL_JAVA_CATEGORY",
                    "CC_COMMON_PRPLBL_JAVA_CATEGORY",
                },
                {
                    "CC_COMMON_PRPLBL_JAVA_CLASSPATH",
                    "CC_COMMON_PRPLBL_JAVA_CLASSPATH",
                },
                {
                    "CC_COMMON_PRPLBL_JAVA_JVM_OPTIONS",
                    "CC_COMMON_PRPLBL_JAVA_JVM_OPTIONS",
                },
                {
                    "CC_IDOC_AdminFieldNotFound",
                    "The technical field '{0}' could not be found on the link.",
                },
                {
                    "CC_IDOC_AllSegmentDataCollected",
                    "All segment data collected, starting sending to SAP",
                },
                {
                    "CC_IDOC_BasicIDocTypeNotFoundInSAP",
                    "The basic IDoc type '{0}' could not be found in SAP.",
                },
                {
                    "CC_IDOC_ClassFoundInJar",
                    "The class '{0}' was found in jar file '{1}'",
                },
                {
                    "CC_IDOC_ClientIDNotNumeric",
                    "Client number must be numeric",
                },
                {
                    "CC_IDOC_CommonUnexpectedException",
                    "An unexpected exception occurred: {0}",
                },
                {
                    "CC_IDOC_ConfigFileEmpty",
                    "The configuration file '{0}' is empty.",
                },
                {
                    "CC_IDOC_ConfigFileNotFound",
                    "The configuration file '{0}' could not be found on the server.",
                },
                {
                    "CC_IDOC_ConfigFileWrongFormat",
                    "The configuration file '{0}' has a wrong format.",
                },
                {
                    "CC_IDOC_ControlRecordDescription",
                    "IDoc Control Record",
                },
                {
                    "CC_IDOC_DSSAPConnectionNotFound",
                    "The connection '{0}' could not be found.",
                },
                {
                    "CC_IDOC_DSSAPHOMENotSet",
                    "The environment variable DSSAPHOME is not set.",
                },
                {
                    "CC_IDOC_EXTRACT_BookmarkFileInitError",
                    "The IDoc bookmark file {0} could not be initialized properly.",
                },
                {
                    "CC_IDOC_EXTRACT_BookmarkFileReadError",
                    "Error reading the IDoc bookmark file {0}",
                },
                {
                    "CC_IDOC_EXTRACT_BookmarkFileSetupFailed",
                    "Setting up the IDoc bookmark file failed: {0}",
                },
                {
                    "CC_IDOC_EXTRACT_BookmarkFileUpdateFailed",
                    "Updating the IDoc bookmark file failed: {0}",
                },
                {
                    "CC_IDOC_EXTRACT_BookmarkFileWriteError",
                    "Error writing the IDoc bookmark file {0}",
                },
                {
                    "CC_IDOC_EXTRACT_IDocFileExtractSegmentData",
                    "Error when extracting segment data from the IDoc file {0}",
                },
                {
                    "CC_IDOC_EXTRACT_IDocFileReadError",
                    "Error reading the IDoc file {0}",
                },
                {
                    "CC_IDOC_EXTRACT_JobStart",
                    "IDoc extract job started at {0}",
                },
                {
                    "CC_IDOC_EXTRACT_LONG_DESC",
                    "Stage for processing IDocs received previously from SAP thr" +
                    "ough the IDoc listener service.",
                },
                {
                    "CC_IDOC_EXTRACT_LP_EXTRACTFIELDLIST",
                    "Extract Field List",
                },
                {
                    "CC_IDOC_EXTRACT_LP_IDOCCOMPONENTTOEXTRACT",
                    "IDoc Component to Extract",
                },
                {
                    "CC_IDOC_EXTRACT_NumberOfFiles",
                    "Processing {0} IDoc files ...",
                },
                {
                    "CC_IDOC_EXTRACT_SHORT_DESC",
                    "Stage for processing IDocs received from SAP",
                },
                {
                    "CC_IDOC_EXTRACT_SP_IDOC_EXTRACT_LIST",
                    "IDOC Extract List",
                },
                {
                    "CC_IDOC_EXTRACT_SP_MANUAL_BATCH_SIZE",
                    "Manual batch size",
                },
                {
                    "CC_IDOC_EXTRACT_SP_TESTMODE",
                    "Test Mode",
                },
                {
                    "CC_IDOC_EXTRACT_STAGE_DISPLAY_NAME",
                    "IDoc Extract Connector Stage",
                },
                {
                    "CC_IDOC_EXTRACT_SegmentDataTooShort",
                    "Mismatch between IDoc metadata and segment data length. Seg" +
                    "ment data is too short: {0},{1},{2}",
                },
                {
                    "CC_IDOC_EnvVarNotPositiveNumeric",
                    "The environment variable '{0}' is required to have a positi" +
                    "ve numeric value but has value '{1}'",
                },
                {
                    "CC_IDOC_ErrorCreatingDirectory",
                    "Could not create the directory {0} or one of its parents.",
                },
                {
                    "CC_IDOC_ExceptionWhileCreatingConnection",
                    "An unexpected exception occurred while creating the connect" +
                    "ion to SAP: {0}",
                },
                {
                    "CC_IDOC_ExtensionIDocTypeNotFoundInSAP",
                    "The extension IDoc type '{0}' could not be found in SAP.",
                },
                {
                    "CC_IDOC_FieldNotOnLink",
                    "The IDoc field '{0}' in segment '{1}' is not present on the" +
                    " link.",
                },
                {
                    "CC_IDOC_FieldTruncation",
                    "Field {0} of IDoc {1} was truncated.",
                },
                {
                    "CC_IDOC_FileReadException",
                    "An exception occurred when reading the IDoc file.",
                },
                {
                    "CC_IDOC_IDOCTYPNotFound",
                    "No IDoc type was selected",
                },
                {
                    "CC_IDOC_IncorrectPartitioningType",
                    "Problems during IDoc segment collection and validation have" +
                    " been detected. A likely cause for these problems might be " +
                    "the selection of an inappropriate partitioning type for the" +
                    " IDoc Load stage input links. Please check your configurati" +
                    "on accordingly.",
                },
                {
                    "CC_IDOC_IncorrectProgramLogic",
                    "An internal error occurred: incorrect program logic.",
                },
                {
                    "CC_IDOC_InitializingConnection",
                    "Initializing connection to SAP system as defined in connect" +
                    "ion '{0}'",
                },
                {
                    "CC_IDOC_InvalidSAPLogonDetailsInConnection",
                    "Invalid or empty SAP logon details were provided in the con" +
                    "nection: {0}",
                },
                {
                    "CC_IDOC_InvalidSAPLogonDetailsInStage",
                    "Invalid or empty SAP logon details were provided in the sta" +
                    "ge: {0}",
                },
                {
                    "CC_IDOC_JCOClassNotFound",
                    "The SAP JCo classes could not be found. Please ensure that " +
                    "the sapjco3.jar is on the classpath.",
                },
                {
                    "CC_IDOC_JCODLLNotFound",
                    "The SAP JCo native library could not be found. Please ensur" +
                    "e that the sapjco3 native library is present.",
                },
                {
                    "CC_IDOC_JCoCheckSuccessful",
                    "SAP JCo libraries found",
                },
                {
                    "CC_IDOC_LOAD_FileSummary",
                    "Summary: {0} IDocs have been stored to disk in {1} transact" +
                    "ions at node {2}",
                },
                {
                    "CC_IDOC_LOAD_FileWriteError",
                    "Error writing the IDoc file {0}",
                },
                {
                    "CC_IDOC_LOAD_FilesNamingConflict",
                    "Existing IDoc load files in {0} will not be deleted, result" +
                    "ing in a potential file naming conflict.",
                },
                {
                    "CC_IDOC_LOAD_FilesToBeDeleted",
                    "All existing IDoc load files in {0} will be deleted",
                },
                {
                    "CC_IDOC_LOAD_JobStart",
                    "IDoc load job started at {0}",
                },
                {
                    "CC_IDOC_LOAD_LONG_DESC",
                    "Stage for loading IDocs to SAP in parallel",
                },
                {
                    "CC_IDOC_LOAD_LP_FKEYCOLS",
                    "Foreign keys",
                },
                {
                    "CC_IDOC_LOAD_LP_FKEYCOUNT",
                    "FKEYCOUNT",
                },
                {
                    "CC_IDOC_LOAD_LP_IDOCCOMPONENTTOLOAD",
                    "IDOCCOMPONENTTOLOAD",
                },
                {
                    "CC_IDOC_LOAD_LP_LOADFIELDLIST",
                    "LOADFIELDLIST",
                },
                {
                    "CC_IDOC_LOAD_LP_PARENTOBJECTNAME",
                    "PARENTOBJECTNAME",
                },
                {
                    "CC_IDOC_LOAD_LP_PARENTOBJECTTYPE",
                    "PARENTOBJECTTYPE",
                },
                {
                    "CC_IDOC_LOAD_LP_PKEYCOLS",
                    "Primary keys",
                },
                {
                    "CC_IDOC_LOAD_LP_PKEYCOUNT",
                    "PKEYCOUNT",
                },
                {
                    "CC_IDOC_LOAD_SHORT_DESC",
                    "Stage for loading IDocs to SAP",
                },
                {
                    "CC_IDOC_LOAD_SP_MESSAGETYPE",
                    "IDoc message type (serialized)",
                },
                {
                    "CC_IDOC_LOAD_SP_MESTYP",
                    "IDoc message type",
                },
                {
                    "CC_IDOC_LOAD_STAGE_DISPLAY_NAME",
                    "IDoc Load Connector Stage",
                },
                {
                    "CC_IDOC_LOAD_SimulationModeOn",
                    "IDoc load simulation mode is on, IDocs will not be sent to SAP.",
                },
                {
                    "CC_IDOC_LOAD_Summary",
                    "Summary: {0} IDocs have been sent to SAP in {1} transactions",
                },
                {
                    "CC_IDOC_LOAD_TransactionClose",
                    "{0} IDocs have been sent to SAP in transaction #{1}",
                },
                {
                    "CC_IDOC_LOAD_TransactionOpen",
                    "Sending {0} IDocs to SAP in transaction #{1} ...",
                },
                {
                    "CC_IDOC_LOAD_UnexpectedException",
                    "An unexpected exception occurred while loading IDocs: {0}",
                },
                {
                    "CC_IDOC_LOAD_ValidationError",
                    "IDoc validation failed: {0}, {1}",
                },
                {
                    "CC_IDOC_LP_CONNECTION_NAME",
                    "CONNECTION_NAME",
                },
                {
                    "CC_IDOC_LP_DESCRIPTION",
                    "Description",
                },
                {
                    "CC_IDOC_LP_IDOC_TYPE_NAME",
                    "IDOC_TYPE_NAME",
                },
                {
                    "CC_IDOC_LP_IDOC_TYPE_VERSION",
                    "IDOC_TYPE_VERSION",
                },
                {
                    "CC_IDOC_LP_OBJECTNAME",
                    "OBJECTNAME",
                },
                {
                    "CC_IDOC_LP_OBJECTTYPE",
                    "OBJECTTYPE",
                },
                {
                    "CC_IDOC_LP_PDS_FIELDMAP",
                    "Public Data Source Field Map",
                },
                {
                    "CC_IDOC_LP_PDS_LOCATOR",
                    "Public Data Source Locator",
                },
                {
                    "CC_IDOC_LP_PORT_VERSION",
                    "PORT_VERSION",
                },
                {
                    "CC_IDOC_LP_RECORDTYP",
                    "RECORDTYP",
                },
                {
                    "CC_IDOC_LP_SEGNAM",
                    "SEGNAM",
                },
                {
                    "CC_IDOC_LP_SEGTYP",
                    "Segment type name",
                },
                {
                    "CC_IDOC_NoSegmentsForIDoc",
                    "The IDoc with number '{0}' does not contain any valid segments.",
                },
                {
                    "CC_IDOC_NodeNumberMessage",
                    "Current node number is {0} out of {1}",
                },
                {
                    "CC_IDOC_PX_REPOSITORY_LOCATION",
                    "\\Stage Types\\Parallel\\Packs",
                },
                {
                    "CC_IDOC_PropertyNotFound",
                    "Required stage property '{0}' could not be found",
                },
                {
                    "CC_IDOC_RFMDoesNotExist",
                    "Remote function module '{0}' does not exist on SAP system '{1}'",
                },
                {
                    "CC_IDOC_ReadingIDocTypeWithRelease",
                    "IDoc type '{0}' was configured for the stage. Release is '{1}'",
                },
                {
                    "CC_IDOC_SAPClientId",
                    "SAP Client Id",
                },
                {
                    "CC_IDOC_SAPLangue",
                    "SAP Language",
                },
                {
                    "CC_IDOC_SAPPassword",
                    "SAP Password",
                },
                {
                    "CC_IDOC_SAPUser",
                    "SAP User",
                },
                {
                    "CC_IDOC_SEGTYPNotFound",
                    "No IDoc segment type was selected",
                },
                {
                    "CC_IDOC_SP_BASTYP",
                    "Basic type name",
                },
                {
                    "CC_IDOC_SP_CLIENT",
                    "Client",
                },
                {
                    "CC_IDOC_SP_CONNECTIONNAME",
                    "Connection Name",
                },
                {
                    "CC_IDOC_SP_CONNECTIONNAMEDT",
                    "Designtime Connection Name",
                },
                {
                    "CC_IDOC_SP_DESTINATION",
                    "Destination",
                },
                {
                    "CC_IDOC_SP_DSSAPCONNECTION",
                    "DSSAPCONNECTION",
                },
                {
                    "CC_IDOC_SP_DSSAPCONNECTIONDT",
                    "Designtime Connection",
                },
                {
                    "CC_IDOC_SP_DSSAPCONNECTIONPARAMETER",
                    "Connection Parameter Name",
                },
                {
                    "CC_IDOC_SP_DSSAPCONNECTIONPARAMETERDT",
                    "Designtime Connection Parameter Name",
                },
                {
                    "CC_IDOC_SP_FILENAME",
                    "Filename",
                },
                {
                    "CC_IDOC_SP_GROUP",
                    "Group",
                },
                {
                    "CC_IDOC_SP_GWHOST",
                    "Gateway host",
                },
                {
                    "CC_IDOC_SP_IDOCTYP",
                    "IDoc type name",
                },
                {
                    "CC_IDOC_SP_IDOCTYPE",
                    "IDoc Type information",
                },
                {
                    "CC_IDOC_SP_IDOC_TYPE_VERSION",
                    "IDOC_TYPE_VERSION",
                },
                {
                    "CC_IDOC_SP_JOBNAME",
                    "Job Name",
                },
                {
                    "CC_IDOC_SP_LANGUAGE",
                    "Language",
                },
                {
                    "CC_IDOC_SP_LOADBLN",
                    "Load Balancing",
                },
                {
                    "CC_IDOC_SP_MSGSVR",
                    "Message Server",
                },
                {
                    "CC_IDOC_SP_OFFLINE_DESIGN",
                    "Offline Design",
                },
                {
                    "CC_IDOC_SP_PASSWORD",
                    "Password",
                },
                {
                    "CC_IDOC_SP_PLUGVSN",
                    "Plug-in version",
                },
                {
                    "CC_IDOC_SP_PORT_VERSION",
                    "PORT_VERSION",
                },
                {
                    "CC_IDOC_SP_ROUTERSTR",
                    "Router string",
                },
                {
                    "CC_IDOC_SP_SAPCLIENTNUMBER",
                    "Custom SAP Client Number",
                },
                {
                    "CC_IDOC_SP_SAPLANGUAGE",
                    "Custom SAP Language",
                },
                {
                    "CC_IDOC_SP_SAPPASSWORD",
                    "Custom SAP Password",
                },
                {
                    "CC_IDOC_SP_SAPUSERID",
                    "Custom SAP User ID",
                },
                {
                    "CC_IDOC_SP_SYSNAME",
                    "System ID",
                },
                {
                    "CC_IDOC_SP_SYSNBR",
                    "System number",
                },
                {
                    "CC_IDOC_SP_USEDEFAULTSAPLOGON",
                    "Default SAP Logon?",
                },
                {
                    "CC_IDOC_SP_USEOFFLINEPROCESSING",
                    "Enable file-based IDoc processing?",
                },
                {
                    "CC_IDOC_SP_USERNAME",
                    "User Name",
                },
                {
                    "CC_IDOC_SP_USESAPLOGONDT",
                    "Use this Connection for design time?",
                },
                {
                    "CC_IDOC_SegmentAlreadySeen",
                    "The segment with number '{0}' and parent number '{1}' withi" +
                    "n IDoc '{2}' was already inserted. Ignoring new segment dat" +
                    "a.",
                },
                {
                    "CC_IDOC_SegmentHasParentInDifferentIDoc",
                    "The segment with segment number '{0}' within IDoc '{1}' has" +
                    " parent segment number '{2}' which is associated with a dif" +
                    "ferent IDoc.",
                },
                {
                    "CC_IDOC_SegmentIsOrphan",
                    "The segment with segment number '{0}' within IDoc '{1}' has" +
                    " no parent.",
                },
                {
                    "CC_IDOC_StartingSegmentCollection",
                    "Collecting IDoc segments",
                },
                {
                    "CC_IDOC_TableParamsListIsNull",
                    "Table parameters list for IDOC_INBOUND_ASYNCHRONOUS is null",
                },
                {
                    "CC_IDOC_TypeMetadataDirectoryError",
                    "The parent directory for the IDoc metadata file {0} could n" +
                    "ot be created.",
                },
                {
                    "CC_IDOC_TypeMetadataFileFormatError",
                    "The format of the IDoc metadata file {0} is incorrect.",
                },
                {
                    "CC_IDOC_TypeMetadataFileNotFound",
                    "The metadata file for the IDoc type {0} does not exist on t" +
                    "he server.",
                },
                {
                    "CC_IDOC_TypeMetadataFileReadError",
                    "Error reading the IDoc metadata file {0}",
                },
                {
                    "CC_IDOC_TypeMetadataFileRetrieval",
                    "Retrieving metadata for IDoc type {0} from the IDoc metadat" +
                    "a file {1}",
                },
                {
                    "CC_IDOC_TypeMetadataFileRetrievalFailed",
                    "Retrieving IDoc metadata from the IDoc metadata file did no" +
                    "t succeed. Getting the IDoc metadata information from SAP i" +
                    "nstead.",
                },
                {
                    "CC_IDOC_TypeMetadataFileWriteError",
                    "Error writing the IDoc metadata file {0}",
                },
                {
                    "CC_IDOC_TypeMetadataSAPRetrieval",
                    "Retrieving metadata for IDoc type {0} from SAP",
                },
                {
                    "CC_IDOC_TypeMetadataSegmentTypeNotFound",
                    "The segment type '{0}' could not be found in the IDoc type " +
                    "'{1}'.",
                },
                {
                    "CC_IDOC_TypeNotConfigured",
                    "IDoc type '{0}' is not configured for the connection",
                },
                {
                    "CC_IDOC_TypeNotFoundInSAP",
                    "The IDoc type '{0}' could not be found in SAP.",
                },
                {
                    "CC_IDOC_UnexpectedSAPException",
                    "An unexpected exception occurred while communicating with S" +
                    "AP: {0}",
                },
                {
                    "CC_IDOC_UnknownSAPEncoding",
                    "JAVA cannot handle the encoding of the SAP system.",
                },
                {
                    "CC_IDOC_VersionInfo",
                    "Version info: {0}",
                },
                {
                    "CC_IDOC_WrongSegmentMetadata",
                    "The segment with number '{0}' within IDoc '{1}' has a paren" +
                    "t segment of type '{2}' (should be '{3}').",
                },
    };
}
