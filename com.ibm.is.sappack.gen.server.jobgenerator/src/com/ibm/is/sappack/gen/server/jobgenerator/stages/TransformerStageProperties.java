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


public abstract class TransformerStageProperties
{
   // ************************** Transformer Stage Property Keys **********************************************
   public static final String PROP_SURROGATE_KEY_STATE_FILE_KEY     = "SurKeyStateFile";            //$NON-NLS-1$
   public static final String PROP_SURROGATE_KEY_SOURCE_TYPE_KEY    = "SKKeySourceType";            //$NON-NLS-1$
   public static final String PROP_SURROGATE_KEY_SOURCE_TYPE_FILE   = "file";                       //$NON-NLS-1$
   public static final String PROP_REJECT_KEY                       = "Reject";                     //$NON-NLS-1$
   
   
   // -------------------------------------------------------------------------------------
   //                                 Member Variables
   // -------------------------------------------------------------------------------------
   private static final Map<String, String>   _DefaultStageParamsMap;
   private static final Map<String, String>   _DefaultLinkParamsMap;
   
   static String copyright()
   { return com.ibm.is.sappack.gen.server.jobgenerator.stages.Copyright.IBM_COPYRIGHT_SHORT; }   
   
   // -------------------------------------------------------------------------------------
   //                                 Static Block
   // -------------------------------------------------------------------------------------
   static
   {
      // ------------------------------------------------------
      //            Stage Parameters
      // ------------------------------------------------------
      _DefaultStageParamsMap = new HashMap<String,String>();
      _DefaultStageParamsMap.put("BlockSize", "0");
      _DefaultStageParamsMap.put("ValidationStatus", "0");
      
      // ------------------------------------------------------
      //            Link Parameters
      // ------------------------------------------------------
      _DefaultLinkParamsMap = new HashMap<String,String>();
      _DefaultLinkParamsMap.put("MultiRow", "0");
   } // end of static

   
   public static ObjectParamMap getDefaultStageParams()
   {
      return(new ObjectParamMap(_DefaultStageParamsMap, DataStageObjectFactory.STAGE_TYPE_DEFAULT));
   }


   public static ObjectParamMap getDefaultLinkParams()
   {
      // always return a copy of the 'default' values
      return(new ObjectParamMap(_DefaultLinkParamsMap, DataStageObjectFactory.LINK_TYPE_INPUT));
   }

} // end of class TransformerStageProperties 
