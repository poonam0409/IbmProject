//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2009, 2011                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.is.sappack.dsstages.common
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.deltaextractstage.client;


@SuppressWarnings("nls")
public class Constants {

	
	/** Name of Common Connector text resource */
	public static final String   CC_TEXT_RESOURCE_NAME = "CC_IDOC"; //$NON-NLS-1$
	
	// Product name for OMD reporting
	public static final String OMD_PRODUCT_NAME = "SAP_Connector";
	public static final String OMD_DB_OWNER = "SAP";
	
	// Config file constants
	public static final String CONFIG_FOLDER_DSSAPCONNECTIONS = "DSSAPConnections";
	public static final String CONFIG_FILE_IDOC_LOAD = "IDocLoad.config";
	public static final String CONFIG_FILE_IDOC_TYPES = "IDocTypes.config";
	public static final String CONFIG_FOLDER_IDOC_TYPES = "IDocTypes";
	public static final String CONFIG_FILE_IDOC_TYPES_PROPERTY_DSIDOCTYPES = "DSIDOCTYPES";
	public static final String CONFIG_FILE_IDOC_TYPES_PROPERTY_NAME = "NAME";
	public static final String CONFIG_FILE_IDOC_TYPES_PROPERTY_USE_DEFAULT_PATH = "USE_DEFAULT_PATH";
	public static final String CONFIG_FILE_IDOC_TYPES_PROPERTY_IDOC_FILES_PATH = "IDOC_FILES_PATH";

	public static final String CONFIG_FILE_IDOC_LOAD_PROPERTY_ILOADDIR = "ILOADDIR";
	public static final String CONFIG_FILE_IDOC_LOAD_PROPERTY_ILOADDESTPARTNERNUMBER = "ILOADDESTPARTNERNUMBER";
	public static final String CONFIG_FILE_IDOC_LOAD_PROPERTY_ILOADPARTNERNUMBER = "ILOADPARTNERNUMBER";
	public static final String CONFIG_FILE_DSSAPCONNECTIONS_PROPERTY_DSSAPCONNECTIONS = "DSSAPCONNECTIONS";
	public static final String CONFIG_FILE_DSSAPCONNECTIONS = "DSSAPConnections.config";
	public static final String CONFIG_FILE_DSSAPCONNECTIONS_PROPERTY_DEFAULTPASSWORD = "DEFAULTPASSWORD";
	public static final String CONFIG_FILE_DSSAPCONNECTIONS_PROPERTY_DEFAULTLANGUAGE = "DEFAULTLANGUAGE";
	public static final String CONFIG_FILE_DSSAPCONNECTIONS_PROPERTY_DEFAULTCLIENT = "DEFAULTCLIENT";
	public static final String CONFIG_FILE_DSSAPCONNECTIONS_PROPERTY_DEFAULTUSERNAME = "DEFAULTUSERNAME";
	public static final String CONFIG_FILE_DSSAPCONNECTIONS_PROPERTY_SAPSYSNUM = "SAPSYSNUM";
	public static final String CONFIG_FILE_DSSAPCONNECTIONS_PROPERTY_SAPAPPSERVER = "SAPAPPSERVER";
	public static final String CONFIG_FILE_DSSAPCONNECTIONS_PROPERTY_SAPMESSERVER = "SAPMESSERVER";
	public static final String CONFIG_FILE_DSSAPCONNECTIONS_PROPERTY_SAPGROUP = "SAPGROUP";
	public static final String CONFIG_FILE_DSSAPCONNECTIONS_PROPERTY_SAPROUTERSTRING = "SAPROUTERSTRING";
	public static final String CONFIG_FILE_DSSAPCONNECTIONS_PROPERTY_DELTAEXTRACTSAPPROGRAMID = "DELTAEXTRACTSAPPROGRAMID";
	public static final String CONFIG_FILE_DSSAPCONNECTIONS_PROPERTY_DELTAEXTRACTGWHOST = "DELTAEXTRACTGWHOST";
	public static final String CONFIG_FILE_DSSAPCONNECTIONS_PROPERTY_DELTAEXTRACTDSPARTNERNUMBER = "DELTAEXTRACTDSPARTNERNUMBER";
	public static final String CONFIG_FILE_DSSAPCONNECTIONS_PROPERTY_DELTAEXTRACTSAPPARTNERNUMBER = "DELTAEXTRACTSAPPARTNERNUMBER";
	public static final String CONFIG_FILE_DSSAPCONNECTIONS_PROPERTY_MSGSVRROUTERSTRING = "MSGSVRROUTERSTRING";
	public static final String CONFIG_FILE_DSSAPCONNECTIONS_PROPERTY_SAPSYSID = "SAPSYSID";
	public static final String CONFIG_FILE_DSSAPCONNECTIONS_PROPERTY_USELOADBALANCING = "USELOADBALANCING";
	public static final String CONFIG_FILE_IDOC_TYPE_PROPERTY_R3VERSION = "R3_VERSION";
	public static final String CONFIG_FILE_PROPERTY_VALUE_TRUE = "TRUE";
	public static final String CONFIG_FILE_PROPERTY_VALUE_FALSE = "FALSE";

	public static final String ABAP_EXCEPTION_KEY_IDOCTYPE_READ_COMPLETE_IDOC_NOT_FOUND = "OBJECT_UNKNOWN";

	public static final String IDOC_CONTROL_RECORD_SEGMENT_DEFINITION_NAME = "CONTROL_RECORD";
	public static final String IDOC_CONTROL_RECORD_SEGMENT_TYPE_NAME = "CONTROL_RECORD";
	public static final String IDOC_METADATA_FILE_ENDING = ".imf";

	public static final String FUNC_IDOCTYPE_READ_COMPLETE = "IDOCTYPE_READ_COMPLETE";
	public static final String PARAM_PI_IDOCTYP = "PI_IDOCTYP";
	public static final String PARAM_PI_CIMTYP = "PI_CIMTYP";
	public static final String PARAM_PI_RELEASE = "PI_RELEASE";
	public static final String PARAM_PT_SEGMENTS = "PT_SEGMENTS";
	public static final String PARAM_PT_FIELDS = "PT_FIELDS";
	public static final String PARAM_PE_HEADER = "PE_HEADER";
	public static final String FUNC_IDOC_RECORD_READ = "IDOC_RECORD_READ";
	public static final String PARAM_DC_FIELDS = "DC_FIELDS";

	public static final String IDOC_METADATA_IDOC_FIELD_IDOCTYP = "IDOCTYP";
	public static final String IDOC_METADATA_IDOC_FIELD_CIMTYP = "CIMTYP";
	public static final String IDOC_METADATA_IDOC_FIELD_STRUCTTYPE = "STRUCTTYPE";
	public static final String IDOC_METADATA_IDOC_FIELD_DESCRP = "DESCRP";
	public static final String IDOC_METADATA_IDOC_FIELD_RELEASED = "RELEASED";
	public static final String IDOC_METADATA_IDOC_FIELD_PRETYP = "PRETYP";
	public static final String IDOC_METADATA_IDOC_FIELD_PRESP = "PRESP";
	public static final String IDOC_METADATA_IDOC_FIELD_PWORK = "PWORK";
	public static final String IDOC_METADATA_IDOC_FIELD_PLAST = "PLAST";
	public static final String IDOC_METADATA_SEGM_FIELD_SEGMENTDEF = "SEGMENTDEF";
	public static final String IDOC_METADATA_SEGM_FIELD_SEGMENTTYP = "SEGMENTTYP";
	public static final String IDOC_METADATA_SEGM_FIELD_DESCRP = "DESCRP";
	public static final String IDOC_METADATA_SEGM_FIELD_SEGLEN = "SEGLEN";
	public static final String IDOC_METADATA_SEGM_FIELD_PARSEG = "PARSEG";
	public static final String IDOC_METADATA_SEGM_FIELD_PARPNO = "PARPNO";
	public static final String IDOC_METADATA_SEGM_FIELD_PARFLG = "PARFLG";
	public static final String IDOC_METADATA_SEGM_FIELD_MUSTFL = "MUSTFL";
	public static final String IDOC_METADATA_SEGM_FIELD_OCCMIN = "OCCMIN";
	public static final String IDOC_METADATA_SEGM_FIELD_OCCMAX = "OCCMAX";
	public static final String IDOC_METADATA_SEGM_FIELD_HLEVEL = "HLEVEL";
	public static final String IDOC_METADATA_SEGM_FIELD_GRP_MUSTFL = "GRP_MUSTFL";
	public static final String IDOC_METADATA_SEGM_FIELD_GRP_OCCMIN = "GRP_OCCMIN";
	public static final String IDOC_METADATA_SEGM_FIELD_GRP_OCCMAX = "GRP_OCCMAX";
	public static final String IDOC_METADATA_SEGM_FIELD_QUALIFIER = "QUALIFIER";
	public static final String IDOC_METADATA_SEGM_FIELD_REFSEGTYP = "REFSEGTYP";
	public static final String IDOC_METADATA_SEGM_FIELD_NR = "NR";
	public static final String IDOC_METADATA_SEGM_FIELD_NUMFIELDS = "NUMFIELDS";
	public static final String IDOC_METADATA_FLDS_FIELD_SEGMENTTYP = "SEGMENTTYP";
	public static final String IDOC_METADATA_FLDS_FIELD_FIELDNAME = "FIELDNAME";
	public static final String IDOC_METADATA_FLDS_FIELD_DESCRP = "DESCRP";
	public static final String IDOC_METADATA_FLDS_FIELD_INTLEN = "INTLEN";
	public static final String IDOC_METADATA_FLDS_FIELD_EXTLEN = "EXTLEN";
	public static final String IDOC_METADATA_FLDS_FIELD_DATATYPE = "DATATYPE";
	public static final String IDOC_METADATA_FLDS_FIELD_DOMNAME = "DOMNAME";

	public static final int IDOC_METADATA_SEGM_FIELD_PARSEG_LENGTH = 30;
	public static final String IDOC_METADATA_SEGM_FIELD_MUSTFL_SET = "X";

	public static final String ENV_VAR_DSSAPHOME = "DSSAPHOME";
	public static final String ENV_VAR_VALUE_ON = "1";
	public static final String ENV_VAR_VALUE_OFF = "0";
	public static final String ENV_VAR_DSSAP_TRACE = "DSSAP_STAGE_RUNTIME_TRACE";
	public static final String ENV_VAR_DSSAP_TRACE_DIR = "DSSAP_STAGE_RUNTIME_TRACE_DIR";
	public static final String ENV_VAR_JCO_RFC_TRACE     = "RFC_TRACE";
	public static final String ENV_VAR_JCO_RFC_TRACE_DIR = "RFC_TRACE_DIR";
	public static final String ENV_VAR_DSSAP_IDOC_LOAD_SIMULATED_MODE = "DSSAP_ILOAD_SIMULATE";
	public static final String ENV_VAR_DSSAP_IDOC_LOAD_VALIDATION_WARNING_IS_ERROR = "DSSAP_ILOAD_VALIDATION_IS_ERROR";
	public static final String ENV_VAR_DSSAP_IDOC_LOAD_SEGMENT_COLLECTION_MODE = "DSSAP_ILOAD_SEGCOLLMODE";
	public static final String ENV_VAR_VALUE_DSSAP_IDOC_LOAD_SEGMENT_COLLECTION_MODE_DEFAULT = "DEFAULT";
	public static final String ENV_VAR_VALUE_DSSAP_IDOC_LOAD_SEGMENT_COLLECTION_MODE_INMEM = "INMEM";
	public static final String ENV_VAR_VALUE_DSSAP_IDOC_LOAD_SEGMENT_COLLECTION_MODE_FIXEDFILES = "FIXEDFILES";
	public static final String ENV_VAR_VALUE_DSSAP_IDOC_LOAD_SEGMENT_COLLECTION_MODE_VARIABLEFILES = "VARIABLEFILES";
	public static final String ENV_VAR_DSSAP_IDOC_LOAD_SEGMENT_COLLECTION_MODE_FIXEDFILES_FILENUM = "DSSAP_ILOAD_FIXEDFILES_SEGCOLLMODE_FILENUM";
	public static final String ENV_VAR_DSSAP_IDOC_LOAD_SEGMENT_COLLECTION_MODE_VARIABLEFILES_MAX_IDOCS_PER_FILE = "DSSAP_ILOAD_VARIABLEFILES_SEGCOLLMODE_MAX_IDOCS_PER_FILE";
	public static final String ENV_VAR_DSSAP_IDOC_LOAD_FILES_DELETE_AT_JOB_START = "DSSAP_ILOAD_DELETE_IDOC_FILES";
	public static final String ENV_VAR_DSSAP_IDOC_LOAD_FILES_CREATE_UNICODE_FILEPORT = "DS_IDOC_PREPARE_UNICODE_FILE_PORT";
	public static final String ENV_VAR_DSSAP_IDOC_LOAD_FILES_CHUNK_SIZE = "DS_IDOC_EDI_NUM";
	public static final String ENV_VAR_DS_PX_RESET = "DS_PX_RESET";
	public static final String ENV_VAR_ENABLE_PARALLEL_IDOC_EXTRACT = "DSSAP_IEXTRACT_ENABLE_PARALLEL";
	public static final String ENV_VAR_DSSAP_IDOC_LOAD_MINIMUM_MESSAGES = "DSSAP_ILOAD_MINIMUM_MESSAGES";

	public static final String CCF_COMMON_PROPERTY_PATH_INPUT_LINKS = "InputLinks"; //$NON-NLS-1$
	public static final String CCF_COMMON_PROPERTY_PATH_OUTPUT_LINKS = "OutputLinks"; //$NON-NLS-1$
	
	// IDoc stage properties
	// These stage properties are the ones as in the old dspropdisc.h with the prefix IDOCSTAGE_
	public static final String IDOCSTAGE_STAGE_PROP_USERNAME = "USERNAME"; //$NON-NLS-1$
	public static final String IDOCSTAGE_STAGE_PROP_PASSWORD = "PASSWORD"; //$NON-NLS-1$
	public static final String IDOCSTAGE_STAGE_PROP_CLIENT = "CLIENT"; //$NON-NLS-1$
	public static final String IDOCSTAGE_STAGE_PROP_DEFSAPLOGON = "USEDEFAULTSAPLOGON"; //$NON-NLS-1$
	public static final String IDOCSTAGE_STAGE_PROP_LANGUAGE = "LANGUAGE"; //$NON-NLS-1$
	public static final String IDOCSTAGE_STAGE_PROP_CONNECTION = "CONNECTIONNAME"; //$NON-NLS-1$
	public static final String IDOCSTAGE_STAGE_PROP_IDOCTYP = "IDOCTYP"; //$NON-NLS-1$
	public static final String IDOCSTAGE_STAGE_PROP_BASTYP = "BASTYP"; //$NON-NLS-1$

	public static final String IDOCSTAGE_TECHNICAL_FIELD_ADM_DOCNUM = "ADM_DOCNUM"; //$NON-NLS-1$
	public static final String IDOCSTAGE_TECHNICAL_FIELD_ADM_SEGNUM = "ADM_SEGNUM"; //$NON-NLS-1$
	public static final String IDOCSTAGE_TECHNICAL_FIELD_ADM_PSGNUM = "ADM_PSGNUM"; //$NON-NLS-1$

	public static final String IDOCSTAGE_STAGE_PROP_DESTINATION = "DESTINATION"; //$NON-NLS-1$
	public static final String IDOCSTAGE_STAGE_PROP_ROUTERSTR = "ROUTERSTR"; //$NON-NLS-1$
	public static final String IDOCSTAGE_STAGE_PROP_FILENAME = "FILENAME"; //$NON-NLS-1$
	public static final String IDOCSTAGE_STAGE_PROP_GWHOST = "GWHOST"; //$NON-NLS-1$
	public static final String IDOCSTAGE_STAGE_PROP_SYSNBR = "SYSNBR"; //$NON-NLS-1$
	public static final String IDOCSTAGE_STAGE_PROP_IDOCSPERTRANS = "IDOCSPERTRANS"; //$NON-NLS-1$
	public static final String IDOCSTAGE_STAGE_PROP_TESTMODE = "TESTMODE"; //$NON-NLS-1$
	public static final String IDOCSTAGE_STAGE_PROP_MSGSVR = "MSGSVR"; //$NON-NLS-1$
	public static final String IDOCSTAGE_STAGE_PROP_SYSNAME = "SYSNAME"; //$NON-NLS-1$
	public static final String IDOCSTAGE_STAGE_PROP_GROUP = "GROUP"; //$NON-NLS-1$
	public static final String IDOCSTAGE_STAGE_PROP_LOADBLN = "LOADBLN"; //$NON-NLS-1$
	public static final String IDOCSTAGE_STAGE_PROP_PLUGVSN = "PLUGVSN"; //$NON-NLS-1$
	public static final String IDOCSTAGE_STAGE_PROP_JOBNAME = "JOBNAME"; //$NON-NLS-1$
	//additional props for design time xi, and file-based processing
	public static final String IDOCSTAGE_STAGE_PROP_CONNECTIONDT = "CONNECTIONNAMEDT"; //$NON-NLS-1$
	public static final String IDOCSTAGE_STAGE_PROP_DSSAPCONNECTIONDT = "DSSAPCONNECTIONDT"; //$NON-NLS-1$
	public static final String IDOCSTAGE_STAGE_PROP_USESAPLOGONDT = "USESAPLOGONDT"; //$NON-NLS-1$
	public static final String IDOCSTAGE_STAGE_PROP_USEFILEBASEDPROCESSING = "USEOFFLINEPROCESSING"; //$NON-NLS-1$
	public static final String IDOCSTAGE_STAGE_PROP_CONNECTIONPARAMETERDT = "DSSAPCONNECTIONPARAMETERDT"; //$NON-NLS-1$

	public static final String IDOCSTAGE_STAGE_PROP_MESTYP = "MESTYP"; //$NON-NLS-1$

	public static final String IDOCSTAGE_STAGE_PROP_IDOC_EXTRACT_LIST = "IDOC_EXTRACT_LIST"; //$NON-NLS-1$
	public static final String IDOCSTAGE_STAGE_PROP_MANUAL_BATCH_SIZE = "MANUAL_BATCH_SIZE"; //$NON-NLS-1$;

	// GUI only stage properties
	public static final String IDOCSTAGE_ALL_PROP_PORT_VERSION = "PORT_VERSION"; //$NON-NLS-1$
	public static final String IDOCSTAGE_ALL_PROP_IDOC_TYPE_VERSION = "IDOC_TYPE_VERSION"; //$NON-NLS-1$
	public static final String IDOCSTAGE_ALL_PROP_DESCRIPTION = "DESCRIPTION"; //$NON-NLS-1$ // ecase 38661 + ecase 40527
	public static final String IDOCSTAGE_STAGE_PROP_IDOCTYPE = "IDOCTYPE"; //$NON-NLS-1$

	public static final String IDOCSTAGE_STAGE_PROP_SAPUSERID = "SAPUSERID"; //$NON-NLS-1$ // ecase 38661
	public static final String IDOCSTAGE_STAGE_PROP_SAPPASSWORD = "SAPPASSWORD"; //$NON-NLS-1$ // ecase 38661
	public static final String IDOCSTAGE_STAGE_PROP_SAPCLIENTNUMBER = "SAPCLIENTNUMBER"; //$NON-NLS-1$ // ecase 38661
	public static final String IDOCSTAGE_STAGE_PROP_SAPLANGUAGE = "SAPLANGUAGE"; //$NON-NLS-1$ // ecase 38661

	public static final String IDOCSTAGE_STAGE_PROP_DSSAPCONNECTION = "DSSAPCONNECTION"; //$NON-NLS-1$  // ecase 43186
	public static final String IDOCSTAGE_STAGE_PROP_MESSAGETYPE = "MESSAGETYPE"; //$NON-NLS-1$  // ecase 43186
	public static final String IDOCSTAGE_STAGE_PROP_CONNECTIONPARAMETER = "DSSAPCONNECTIONPARAMETER"; //$NON-NLS-1$

	public static final String IDOCSTAGE_INLINK_PROP_IDOCCOMPONENTTOLOAD = "IDOCCOMPONENTTOLOAD"; //$NON-NLS-1$
	public static final String IDOCSTAGE_INLINK_PROP_OBJECTTYPE = "OBJECTTYPE"; //$NON-NLS-1$
	public static final String IDOCSTAGE_INLINK_PROP_OBJECTNAME = "OBJECTNAME"; //$NON-NLS-1$
	public static final String IDOCSTAGE_INLINK_PROP_RECORDTYP = "RECORDTYP"; //$NON-NLS-1$
	public static final String IDOCSTAGE_INLINK_PROP_CONNECTION_NAME = "CONNECTION_NAME"; //$NON-NLS-1$
	public static final String IDOCSTAGE_INLINK_PROP_IDOC_TYPE_NAME = "IDOC_TYPE_NAME"; //$NON-NLS-1$
	public static final String IDOCSTAGE_INLINK_PROP_LOADFIELDLIST = "LOADFIELDLIST"; //$NON-NLS-1$
	public static final String IDOCSTAGE_INLINK_PROP_PARENTOBJECTTYPE = "PARENTOBJECTTYPE"; //$NON-NLS-1$
	public static final String IDOCSTAGE_INLINK_PROP_PARENTOBJECTNAME = "PARENTOBJECTNAME"; //$NON-NLS-1$
	public static final String IDOCSTAGE_INLINK_PROP_PKEYCOUNT = "PKEYCOUNT"; //$NON-NLS-1$
	public static final String IDOCSTAGE_INLINK_PROP_FKEYCOUNT = "FKEYCOUNT"; //$NON-NLS-1$

	// Link properties for input and output links
	public static final String IDOCSTAGE_LINK_PROP_SEGTYP = "SEGTYP"; //$NON-NLS-1$
	public static final String IDOCSTAGE_LINK_PROP_SEGNAM = "SEGNAM"; //$NON-NLS-1$

	// Link properties for output links
	public static final String IDOCSTAGE_OUTLINK_PROP_IDOCCOMPONENTTOEXTRACT = "IDOCCOMPONENTTOEXTRACT"; //$NON-NLS-1$ // ecase 38661
	public static final String IDOCSTAGE_OUTLINK_PROP_EXTRACTFIELDLIST = "EXTRACTFIELDLIST"; //$NON-NLS-1$ // ecase 38661

	// Link properties for input links
	public static final String IDOCSTAGE_INLINK_PROP_FKEYCOLS = "FKEYCOLS"; //$NON-NLS-1$
	public static final String IDOCSTAGE_INLINK_PROP_PKEYCOLS = "PKEYCOLS"; //$NON-NLS-1$

	public static final int RFM_IDOCTYPE_READ_COMPLETE_SIZEOF_SDATA = 1000;

	// File-based IDoc load constants
	public static final String IDOC_FILE_SUFFIX = ".edi"; //$NON-NLS-1$
	
	// trace file suffix
	public static final String TRACE_FILE_EXTENSION = ".log"; //$NON-NLS-1$
	public static final String LINE_SEPARATOR       = System.getProperty("line.separator"); //$NON-NLS-1$
	
	// encoding
	public static final String UTF16 = "UTF-16"; //$NON-NLS-1$
	public static final String UTF8 = "UTF-8"; //$NON-NLS-1$

	public static final String EMPTY_STRING = ""; //$NON-NLS-1$
	public static final String BACKSLASH = ""+(char)47;
	public static final String PACKETCOUNTFOLDER = "DataPackets"+BACKSLASH+"packetcountinfo";
}
