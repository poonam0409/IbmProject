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
// Module Name : com.ibm.is.sappack.dsstages.idoc.listener.util
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.dsstages.idoc.listener.util;


/**
 * IDocServerConstants
 * 
 * contains property names and other constants used
 * by the IDoc Listener
 */
public class IDocServerConstants {

	static String copyright() {
		return com.ibm.is.sappack.dsstages.idoc.listener.util.Copyright.IBM_COPYRIGHT_SHORT;
	}

	/* connection properties provided by dsidocsvr */
	public static final String UVHOME = "UVHOME"; //$NON-NLS-1$
	public static final String USEDEFAULTILOADDIR = "USEDEFAULTILOADDIR"; //$NON-NLS-1$
	public static final String DSPASSWORD = "DSPASSWORD"; //$NON-NLS-1$
	public static final String RFCSERVERCOUNT = "RFCSERVERCOUNT"; //$NON-NLS-1$
	public static final String SAPROUTERSTRING = "SAPROUTERSTRING"; //$NON-NLS-1$
	public static final String ACKNOWLEDGEIDOCRECEIPT = "ACKNOWLEDGEIDOCRECEIPT"; //$NON-NLS-1$
	public static final String DEFAULTLANGUAGE = "DEFAULTLANGUAGE"; //$NON-NLS-1$
	public static final String DATAFILESPATH = "DATAFILESPATH"; //$NON-NLS-1$
	public static final String PASSWORDFILE = "PASSWORDFILE"; //$NON-NLS-1$
	public static final String SAPSYSNUM = "SAPSYSNUM"; //$NON-NLS-1$
	public static final String DEFAULTUSERNAME = "DEFAULTUSERNAME"; //$NON-NLS-1$
	public static final String DESCRIPTION = "DEFAULTUSERNAME"; //$NON-NLS-1$
	public static final String SAPGROUP = "SAPGROUP"; //$NON-NLS-1$
	public static final String DSUSERNAME = "DSUSERNAME"; //$NON-NLS-1$
	public static final String METADATAFILE = "METADATAFILE"; //$NON-NLS-1$
	public static final String SAPMESSERVER = "SAPMESSERVER"; //$NON-NLS-1$
	public static final String ILOADPARTNERNUMBER = "ILOADPARTNERNUMBER"; //$NON-NLS-1$
	public static final String IDOCPORTVERSION = "IDOCPORTVERSION"; //$NON-NLS-1$
	public static final String DEFAULTCLIENT = "DEFAULTCLIENT"; //$NON-NLS-1$
	public static final String READMETADATAFROMFILE = "READMETADATAFROMFILE"; //$NON-NLS-1$
	public static final String ALLOWSAPTORUNJOBS = "ALLOWSAPTORUNJOBS"; //$NON-NLS-1$
	public static final String SAPAPPSERVER = "SAPAPPSERVER"; //$NON-NLS-1$
	public static final String LISTENFORIDOCS = "LISTENFORIDOCS"; //$NON-NLS-1$
	public static final String NAME = "NAME"; //$NON-NLS-1$
	public static final String ISDOMAINNAME = "ISDOMAINNAME"; //$NON-NLS-1$
	public static final String DEFAULTILOADDIR = "DEFAULTILOADDIR"; //$NON-NLS-1$
	public static final String MSGSVRROUTERSTRING = "MSGSVRROUTERSTRING"; //$NON-NLS-1$
	public static final String SAPSYSID = "SAPSYSID"; //$NON-NLS-1$
	public static final String REFSERVERPROGID = "REFSERVERPROGID"; //$NON-NLS-1$
	public static final String ILOADDESTPARTNERNUMBER = "ILOADDESTPARTNERNUMBER"; //$NON-NLS-1$
	public static final String DSSERVERNAME = "DSSERVERNAME"; //$NON-NLS-1$
	public static final String USELOADBALANCING = "USELOADBALANCING"; //$NON-NLS-1$
	public static final String DEFAULTPASSWORD = "DEFAULTPASSWORD"; //$NON-NLS-1$
	public static final String EVENTNAME = "EVENTNAME"; //$NON-NLS-1$
	public static final String PACKHOME = "PACKHOME"; //$NON-NLS-1$
	
	/* constants */
	public static final String NEWLINE = "\n"; //$NON-NLS-1$
	public static final String EMPTYSTRING = ""; //$NON-NLS-1$
	public static final String BLANK = " "; //$NON-NLS-1$
	public static final String TRUE = "TRUE"; //$NON-NLS-1$
	public static final String FALSE = "FALSE"; //$NON-NLS-1$
	public static final String DSSAPConnections = "DSSAPConnections"; //$NON-NLS-1$
	public static final String IDocTypesConfigFile = "IDocTypes.config"; //$NON-NLS-1$
	public static final String IDocTypes = "IDocTypes"; //$NON-NLS-1$
	public static final String LOGICALSYSTEM = "LS"; //$NON-NLS-1$
	
	
	/* constants to read the IDocTypes.config file */
	public static final String DSIDOCTYPES = "DSIDOCTYPES"; //$NON-NLS-1$
	public static final String DSIDOCTYPES_NAME = "NAME"; //$NON-NLS-1$
	public static final String DSIDOCTYPES_IDOC_FILES_PATH = "IDOC_FILES_PATH"; //$NON-NLS-1$
	public static final String DSIDOCTYPES_USE_DEFAULT_PATH = "USE_DEFAULT_PATH"; //$NON-NLS-1$
	
	/* JCo constants */
	public static final String IDOC_INBOUND_ASYNCHRONOUS = "IDOC_INBOUND_ASYNCHRONOUS"; //$NON-NLS-1$
	public static final String IDOC_CONTROL_REC_40 = "IDOC_CONTROL_REC_40"; //$NON-NLS-1$
	public static final String IDOC_DATA_REC_40 = "IDOC_DATA_REC_40"; //$NON-NLS-1$
	public static final String RSAR_TRFC_DATA_RECEIVED = "RSAR_TRFC_DATA_RECEIVED";
	public static final String DeltaExtract = "DeltaExtract"; //$NON-NLS-1$
	
	
	/* special control record fields */
	public static final String IDOCTYP = "IDOCTYP"; // IDoc type  //$NON-NLS-1$
	public static final String CIMTYP = "CIMTYP"; // Extended type //$NON-NLS-1$
	public static final String DOCREL = "DOCREL"; // Release version //$NON-NLS-1$
	public static final String DOCNUM = "DOCNUM"; // IDoc number //$NON-NLS-1$
	public static final String SNDPRN = "SNDPRN"; // Partner number of sender //$NON-NLS-1$
	public static final String SNDPOR = "SNDPRO"; // Port of sender //$NON-NLS-1$
	public static final String RCVPOR = "SNDPRO"; // Port of receiver //$NON-NLS-1$
	public static final String RCVPRN = "RCVPRN"; // Partner number of receiver //$NON-NLS-1$
	public static final String MESTYP = "MESTYP"; //$NON-NLS-1$
	public static final String SNDPRT = "SNDPRT"; // partner type of sender  //$NON-NLS-1$
	public static final String RCVPRT = "RCVPRT"; // partner type of receiver //$NON-NLS-1$
	
	/* segment data fields */
	public static final String SEGNAM = "SEGNAM"; // segment name //$NON-NLS-1$
	public static final String SDATA = "SDATA"; // segment data buffer //$NON-NLS-1$
	public static final String SEGNUM = "SEGNUM"; //$NON-NLS-1$
	public static final String PSGNUM = "PSGNUM"; //$NON-NLS-1$
	public static final String HLEVEL = "HLEVEL"; //$NON-NLS-1$
	public static final String TABNAM = "TABNAM"; //$NON-NLS-1$
	
	/* constants for SYSTAT01 status IDocs */
	public static final String SYSTAT01 = "SYSTAT01"; //$NON-NLS-1$
	public static final String STATUS = "STATUS"; // Message type for SYSTAT01 //$NON-NLS-1$
	public static final String E1STATS = "E1STATS"; // Root segment for SYSTAT01
	public static final String EDI_DS40 = "EDI_DS40"; //$NON-NLS-1$
	public static final String LOGDAT = "LOGDAT"; //$NON-NLS-1$
	public static final String LOGTIM = "LOGTIM"; //$NON-NLS-1$
	public static final String UNAME = "UNAME"; //$NON-NLS-1$
	public static final String REPID = "REPID"; //$NON-NLS-1$
	public static final String STATUS_VALUE = "12"; // default status of status IDocs //$NON-NLS-1$
	public static final String DATASTAGE = "DataStage"; // default value for field REPID //$NON-NLS-1$
	
	public static final String TILDA = ""+(char)126;
	public static final String BACKSLASH = ""+(char)47;
	public static final String AMPERSAND = ""+(char)38;
	public static final String PERCENT = ""+(char)37;
	public static final String JOBNAMETAG = AMPERSAND+"J"+AMPERSAND;
	public static final String NOOFNODESTAG = AMPERSAND+"N"+AMPERSAND;
	public static final String UNDERSCORE = ""+(char)95;
	public static final String DATAPACKETFOLDERNAME = BACKSLASH+"DataPackets"+BACKSLASH;
	public static final String DATAPACKETCOMPLETEFOLDER = BACKSLASH+"complete"+BACKSLASH;
	public static final String FILEREADYFLAG = UNDERSCORE+"ready";
	public static final String DATAPACKETCOUNTINFOFOLDER = BACKSLASH+"packetcountinfo"+BACKSLASH;
	public static final String DS_IDOC_LISTENER_ENABLE_MULTI_THREADING = "DS_IDOC_LISTENER_ENABLE_MULTI_THREADING";
	public static final String DS_IDOC_LISTENER_NUM_OF_THREADS = "DS_IDOC_LISTENER_NUM_OF_THREADS";
	
}
