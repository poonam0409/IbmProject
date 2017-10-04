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


public class SAPIDocLoadStageProperties extends SAPStageProperties 
{
   // -------------------------------------------------------------------------------------
   //                                         Constants
   // -------------------------------------------------------------------------------------
   // ************************** IDOC LOAD STAGE Property Keys **********************************************
   public static final String PROP_IDOC_TYPE_KEY               = "IDOCTYP";
   public static final String PROP_IDOC_BASIC_TYPE_KEY         = "BASTYP";
   public static final String PROP_MES_TYPE_KEY                = "MESTYP";
   public static final String PROP_JOBNAME_KEY                 = "JOBNAME";
   public static final String PROP_SAP_CONNECTION_NAME_KEY     = "DSSAPCONNECTION";
   public static final String PROP_SAP_CONNECTION_NAME_DT_KEY  = "CONNECTIONNAMEDT";
   public static final String PROP_SAP_CONNECTION_DT_KEY       = "DSSAPCONNECTIONDT";

   public static final String PROP_SAP_IDOC_PER_TRANS          = "IDOCSPERTRANS";
  
   // ********************************* STAGE Property Templates *********************************
  // public static final String PROP_SAP_MSG_TYPE_TEMPLATE    = "ISNULL=FALSE\nDETAILSLOADED=FALSE\nDESCRIPTION=Customer master data distribution\nCHILDOBJECTSLOADED=FALSE\nDATETIMEMODIFIED=\nEXISTS=TRUE\nOBJECTTYPE=CSAPMessageType\nNAME={0}\n";
 //  public static final String PROP_SAP_IDOC_TYPE_TEMPLATE   = "ISNULL=FALSE\nCONFIGINFO=<BEGIN>\nJOBNAME=\nPROJECTNAME=\nDSPASSWORD=\nUSE_DEFAULT_PATH=TRUE\nSELECT_JOB=FALSE\nAUTORUN_ENABLED=TRUE\nIDOC_FILES_PATH=\nDSUSERNAME=\nIDOC_COUNT=1\nEXISTS=TRUE\nARCHIVE_IDOC_FILES=FALSE\nUSE_DEFAULT_LOGON=TRUE\n<END>\nDETAILSLOADED=TRUE\nDESCRIPTION=Customer master data distribution\nCHILDOBJECTSLOADED=FALSE\nOBJECTTYPE=CBasicIDocType\nNAME={0}\n";
   
   
   // ************************** LINK Property Keys **************************************************
   public static final String LNK_PROP_SEG_TYPE_KEY              = "SEGTYP";
   public static final String LNK_PROP_SEG_NAME_KEY              = "SEGNAM";
   public static final String LNK_PROP_FKEYCOLS_KEY              = "FKEYCOLS";
   public static final String LNK_PROP_PKEYCOLS_KEY              = "PKEYCOLS";
   public static final String LNK_PROP_OBJECT_NAME_KEY           = "OBJECTNAME";
   public static final String LNK_PROP_CONNECTION_NAME_KEY       = "CONNECTION_NAME";;
   public static final String LNK_PROP_IDOC_TYPE_NAME_KEY        = "IDOC_TYPE_NAME";
   public static final String LNK_PROP_FKEY_COL_COUNT_KEY        = "FKEYCOUNT";
   public static final String LNK_PROP_PKEY_COL_COUNT_KEY        = "PKEYCOUNT";
   public static final String LNK_PROP_PARENT_OBJECT_TYPE_KEY    = "PARENTOBJECTTYPE";
   public static final String LNK_PROP_PARENT_OBJECT_NAME_KEY    = "PARENTOBJECTNAME";
   public static final String LNK_PROP_OBJECT_TYPE_KEY           = "OBJECTTYPE";
   public static final String LNK_PROP_RECORD_TYPE_KEY           = "RECORDTYP";
   public static final String LNK_PROP_PDR_LOCATOR_KEY           = "PublicDataSourceLocator";
   public static final String LNK_PROP_PDR_FIELDMAP_KEY          = "PublicDataSourceFieldMap";
         
   
   // ********************************* LINK Property Templates *********************************
 //  public static final String LNK_PROP_IDOCCOMPONENTTOLOAD_TEMPLATE = "ISNULL=FALSE\nDATAFIELDLIST=<BEGIN>\n<END>\nDETAILSLOADED=TRUE\nDESCRIPTION=Master customer master basic data\nCHILDOBJECTSLOADED=FALSE\nCHILDSEGMENTLIST=<BEGIN>\n<END>\nDATETIMEMODIFIED=\nTYPE={0}\nEXISTS=TRUE\nMANDATORY={1}\nOBJECTTYPE=CSegment\nNAME={2}\nMIN=1\nMAX=9999\n";
   
   
   // -------------------------------------------------------------------------------------
   //                                 Member Variables
   // -------------------------------------------------------------------------------------
   private static final Map<String, String>   _DefaultStageParamsMap;
   private static final Map<String,String>    _DefaultLinkParamsMap;
   
   
   // -------------------------------------------------------------------------------------
   //                                 Static Block
   // -------------------------------------------------------------------------------------
   static
   {
      // ------------------------------------------------------
      //            Stage Parameters
      // ------------------------------------------------------
      _DefaultStageParamsMap = new HashMap<String,String>();
      _DefaultStageParamsMap.put("TESTMODE", "0");
      _DefaultStageParamsMap.put(PROP_SAP_USE_DEFAULT_LOGON, "1");
      _DefaultStageParamsMap.put("PLUGVSN", "1");     
      _DefaultStageParamsMap.put("USESAPLOGONDT", "0");
      _DefaultStageParamsMap.put(PROP_USE_OFFLINE_PROCESSING, "0");

      
      // ------------------------------------------------------
      //            Link Parameters
      // ------------------------------------------------------
      _DefaultLinkParamsMap = new HashMap<String,String>();
      
      _DefaultLinkParamsMap.put(LNK_PROP_OBJECT_TYPE_KEY, "CSegment");
      _DefaultLinkParamsMap.put(LNK_PROP_RECORD_TYPE_KEY, "DATA");
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
      return(new ObjectParamMap(_DefaultLinkParamsMap, DataStageObjectFactory.LINK_TYPE_INPUT));
   }
   
} // end of class SAPIDocLoadStageProperties
