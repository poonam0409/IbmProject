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


import java.util.Map;

import com.ibm.is.sappack.gen.server.datastage.DataStageObjectFactory;
import com.ibm.is.sappack.gen.server.util.ObjectParamMap;


public abstract class FileSourceStageProperties extends FileStageProperties
{
   // -------------------------------------------------------------------------------------
   //                                         Constants
   // -------------------------------------------------------------------------------------
   // ************************** File Stage Property Keys **********************************************
   public    static final String PROP_FILE_NAME_KEY            = "file +";
   public    static final String PROP_FILE_NAME_TEMPLATE       = "\\(02)\\(02)0\\(01)\\(03)file \\(02){0}\\(02)0";

   
   // -------------------------------------------------------------------------------------
   //                                 Member Variables
   // -------------------------------------------------------------------------------------
   private static final Map<String,String>  _DefaultLinkParamsMap;


   static String copyright()
   {
   	return com.ibm.is.sappack.gen.server.jobgenerator.stages.Copyright.IBM_COPYRIGHT_SHORT;
   }   

   // -------------------------------------------------------------------------------------
   //                                 Static Block
   // -------------------------------------------------------------------------------------
   static
   {
      // ------------------------------------------------------
      //            Stage Parameters
      // ------------------------------------------------------
      _DefaultLinkParamsMap = getBaseLinkParams();
      
      _DefaultLinkParamsMap.put("selection", "file");
      _DefaultLinkParamsMap.put("rejects", "continue");
      _DefaultLinkParamsMap.put("reportProgress", "yes");
   } // end of static

   
   public static ObjectParamMap getDefaultLinkParams()
   {
      return(new ObjectParamMap(_DefaultLinkParamsMap, DataStageObjectFactory.LINK_TYPE_OUTPUT));
   }
   
} // end of class FileSourceStageProperties 
