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


public class SAPIDocExtractStageProperties extends SAPStageProperties 
{ 
   // -------------------------------------------------------------------------------------
   //                                         Constants
   // -------------------------------------------------------------------------------------
   // ************************** IDOC EXTRACT STAGE Property Keys **********************************************
   public static final String PROP_IDOC_TYPE_KEY            = SAPIDocLoadStageProperties.PROP_IDOC_TYPE_KEY;
   public static final String PROP_IDOC_BASIC_TYPE_KEY      = SAPIDocLoadStageProperties.PROP_IDOC_BASIC_TYPE_KEY;
   public static final String PROP_JOBNAME_KEY              = SAPIDocLoadStageProperties.PROP_JOBNAME_KEY;
   public static final String PROP_SAP_CONNECTION_NAME_KEY  = SAPIDocLoadStageProperties.PROP_SAP_CONNECTION_NAME_KEY;
   
   
   // ************************** LINK Property Keys **************************************************
   public static final String LNK_PROP_SEG_TYPE_KEY                 = SAPIDocLoadStageProperties.LNK_PROP_SEG_TYPE_KEY;
   public static final String LNK_PROP_SEG_NAME_KEY                 = SAPIDocLoadStageProperties.LNK_PROP_SEG_NAME_KEY;
   public static final String LNK_PROP_OBJECT_NAME_KEY              = SAPIDocLoadStageProperties.LNK_PROP_OBJECT_NAME_KEY;
   public static final String LNK_PROP_CONNECTION_NAME_KEY          = SAPIDocLoadStageProperties.LNK_PROP_CONNECTION_NAME_KEY;
   public static final String LNK_PROP_IDOC_TYPE_NAME_KEY           = SAPIDocLoadStageProperties.LNK_PROP_IDOC_TYPE_NAME_KEY;
   public static final String LNK_PROP_PDR_LOCATOR_KEY              = SAPIDocLoadStageProperties.LNK_PROP_PDR_LOCATOR_KEY;
   public static final String LNK_PROP_PDR_FIELDMAP_KEY             = SAPIDocLoadStageProperties.LNK_PROP_PDR_FIELDMAP_KEY;
         
   
   // ********************************* LINK Property Templates *********************************
 //  public static final String LNK_PROP_IDOCCOMPONENTTOEXTRACT_TEMPLATE = "ISNULL=FALSE\nDATAFIELDLIST=<BEGIN>\n<END>\nDETAILSLOADED=TRUE\nDESCRIPTION=Master customer master basic data\nCHILDOBJECTSLOADED=FALSE\nCHILDSEGMENTLIST=<BEGIN>\n<END>\nDATETIMEMODIFIED=\nTYPE={0}\nEXISTS=TRUE\nMANDATORY={1}\nOBJECTTYPE=CSegment\nNAME={2}\nMIN=1\nMAX=9999\n";
   
   
   // -------------------------------------------------------------------------------------
   //                                 Member Variables
   // -------------------------------------------------------------------------------------
   private static final Map<String,String>   _DefaultStageParamsMap;
   private static final Map<String,String>   _DefaultLinkParamsMap;

   
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
      _DefaultLinkParamsMap.put("OBJECTTYPE", "CSegment");
      _DefaultLinkParamsMap.put("RECORDTYP", "DATA");
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

} // end of class SAPIDocExtractStageProperties
