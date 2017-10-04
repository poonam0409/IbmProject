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
// Module Name : com.ibm.is.sappack.gen.server.jobgenerator.stages
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.server.jobgenerator.stages;


import java.util.HashMap;
import java.util.Map;

import com.ibm.is.sappack.gen.server.datastage.DataStageObjectFactory;
import com.ibm.is.sappack.gen.server.util.ObjectParamMap;


public class ABAPExtractStageProperties extends SAPStageProperties
{
   // -------------------------------------------------------------------------------------
   //                                       Constants
   // -------------------------------------------------------------------------------------
   public static final String LNK_PROP_SAP_USE_DEFAULT_LOGON     = PROP_SAP_USE_DEFAULT_LOGON;
   
   public static final String PROP_CONN_SAP_CONNECTION_NAME_KEY  = "CONNECTION_NAME";
   public static final String LNK_PROP_SAP_TABLE_NAME_KEY        = "TABLE"; 
	public static final String LNK_PROP_SAP_APP_SERVER_KEY        = "SAPAPPNAME";
	public static final String LNK_PROP_SAP_SYSTEM_NUMBER_KEY     = "SAPSN";
	public static final String LNK_PROP_SAP_CLIENT_NUMBER_KEY     = "SAPCN";
	public static final String LNK_PROP_SAP_USERNAME_KEY          = "SAPUID";
	public static final String LNK_PROP_SAP_PASSWORD_KEY          = "SAPPWD";
	public static final String LNK_PROP_SAP_LANGUAGE_KEY          = "SAPLANG";
   public static final String LNK_PROP_SAP_UPLOAD_ABAP_CODE_KEY  = "UPLOADEDABAP";   
   public static final String LNK_PROP_PROG_DEVELOP_STATUS_KEY   = "PROGDEVELSTATUS";
   public static final String LNK_PROP_PROG_UPLOAD_MODE_KEY      = "UPLOADMODE";
   public static final String LNK_PROP_PROG_GEN_MODE             = "GENMODE";
   public static final String LNK_PROP_SAP_ABAP_CODE             = "ABAPCODE";
   public static final String LNK_PROP_SAP_ABAP_PROGRAM_NAME     = "SAPPROGID";

   
   //                             RFC DATA Link Properties
   // -------------------------------------------------------------------------------------
   public static final String LNK_PROP_SAP_DATA_TRANSFER_METHOD_KEY  = "FILEACCESS";
	public static final String LNK_PROP_SAP_RFC_GATEWAY_HOST_KEY      = "DSRFC_GATEWAY_HOST";
	public static final String LNK_PROP_SAP_RFC_GATEWAY_SERVICE_KEY   = "DSRFC_GATEWAY_SERVICE";
	public static final String LNK_PROP_SAP_RFC_PROGID_KEY            = "DSRFC_PROGID";
	public static final String LNK_PROP_SAP_RFC_DESTINATION_NAME_KEY  = "DSRFC_SM59DESTINATION";
   public static final String LNK_PROP_SAP_RFC_QRFC_NAME_KEY         = "DSRFC_QRFCNAME";
   public static final String LNK_PROP_SAP_RFC_QRFC_ENABLE_KEY       = "DSRFC_QRFC";
   public static final String LNK_PROP_SAP_RFC_DEST_CREATE_KEY       = "DSRFC_AUTOM_RFC_DEST_CREAT";
   public static final String LNK_PROP_SAP_RFC_DEST_CLEANUP_KEY      = "DSRFC_ALWAYS_CLEANUP_RFC_DEST";
   public static final String LNK_PROP_SAP_RFC_RETRY_COUNT           = "DSRFC_RETRIES";   
   public static final String LNK_PROP_SAP_RFC_RETRY_INTERVAL        = "DSRFC_DELAY";
   public static final String LNK_PROP_SAP_RFC_DELETE_LUW            = "DSRFC_DELETE_LUW";  
   public static final String LNK_PROP_SAP_RFC_CHECK_ABAP_PROG       = "CHECK_SAP_ABAP_PROG";  
   public static final String LNK_PROP_SAP_RFC_SUPRESS_BACKGROUND_JOB       = "DSRFC_SUPPRESS";  
   
   // CPICData Link Properties
   public static final String LNK_PROP_SAP_CPIC_USER                      = "CPIC_USER";
   public static final String LNK_PROP_SAP_CPIC_PASSWORD                  = "CPIC_PSWD";
   public static final String LNK_PROP_SAP_CPIC_USERT                     = "CPIC_USERT";
   public static final String LNK_PROP_SAP_CPIC_PASSWORDT                 = "CPIC_PSWDT";
   public static final String LNK_PROP_SAP_CPIC_USE_DEFAULT_LOGON_DETAILS = "USELOGONDETAILS";
   
   //                      BACKGROUND PROCESSING Link Properties
   // -------------------------------------------------------------------------------------
   public static final String LNK_PROP_BG_PROC_ENABLED           = "BACKGROUNDPROCESS";
   public static final String LNK_PROP_BG_PROC_VARIANT_NAME      = "VARIANTNAME";
   public static final String LNK_PROP_BG_PROC_DELAY             = "PROGRAMDELAY";
   public static final String LNK_PROP_BG_PROC_KEEP_VARIANT      = "KEEPVARIANT";
   public static final String LNK_PROP_BG_PROC_SAP_VARIANT       = "SAPVARIANT";
   
	
	public static final String CONN_PROP_SAP_RFC_DATA_TRANSFER_METHOD_VALUE  = "3";
	public static final String CONN_PROP_SAP_CPIC_DATA_TRANSFER_METHOD_VALUE = "2";
	
	
   // -------------------------------------------------------------------------------------
   //                                 Member Variables
   // -------------------------------------------------------------------------------------
	private static final Map<String,String>  _DefaultStageParamsMap;
   private static final Map<String,String>  _DefaultLinkParamsMap;

   // -------------------------------------------------------------------------------------
   //                                 Static Block
   // -------------------------------------------------------------------------------------
	static
	{
      // ------------------------------------------------------
	   //            Stage Parameters
	   // ------------------------------------------------------
	   _DefaultStageParamsMap = new HashMap<String,String>();
	   _DefaultStageParamsMap.put("PLUGVSN", "1");	   
      _DefaultStageParamsMap.put("SAPDESCR", "");
      _DefaultStageParamsMap.put("ROUTER", "");
      _DefaultStageParamsMap.put(CHAR_SET_KEY, "");

      // ------------------------------------------------------
      //            Link Parameters
      // ------------------------------------------------------
	   _DefaultLinkParamsMap = new HashMap<String,String>();
      _DefaultLinkParamsMap.put("OBJECTTYPE", "CSegment");
      _DefaultLinkParamsMap.put("RECORDTYP", "DATA");
      _DefaultLinkParamsMap.put(LNK_PROP_SAP_USE_DEFAULT_LOGON, "1");
	} // end of static

	
   static String copyright()
   { 
      return com.ibm.is.sappack.gen.server.jobgenerator.stages.Copyright.IBM_COPYRIGHT_SHORT; 
   }   

   
	public static ObjectParamMap getDefaultStageParams()
	{
	   return(new ObjectParamMap(_DefaultStageParamsMap, DataStageObjectFactory.STAGE_TYPE_DEFAULT));
	}

	
   public static ObjectParamMap getDefaultLinkParams()
   {
      // always return a copy of the 'default' values
      return(new ObjectParamMap(_DefaultLinkParamsMap, DataStageObjectFactory.LINK_TYPE_OUTPUT));
   }

} // end of class ABAPExtraxtStageProperties 
