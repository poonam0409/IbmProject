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


public abstract class FileTargetStageProperties extends FileStageProperties
{
   // -------------------------------------------------------------------------------------
   //                                         Constants
   // -------------------------------------------------------------------------------------
   // ************************** File Stage Property Keys **********************************************
   public    static final String PROP_FILE_NAME_KEY           = "file";
   public    static final String PROP_FILE_NAME_TEMPLATE      = "\\(02)\\(02)0\\(01)\\(03)file\\(02){0}\\(02)0";
   public    static final String PROP_FILEMODE_KEY            = "append\\\\overwrite";   
   public    static final String PROP_FILEMODE_APPEND         = "append";   
   public    static final String PROP_FILEMODE_OVERWRITE      = "overwrite";   
   public    static final String PROP_FILEMODE_CREATE         = " ";   

   
   // -------------------------------------------------------------------------------------
   //                                 Member Variables
   // -------------------------------------------------------------------------------------
   private static final Map<String,String>      _DefaultLinkParamsMap;


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
      _DefaultLinkParamsMap.put(PROP_FILEMODE_KEY, PROP_FILEMODE_OVERWRITE);
   } // end of static

   
   public static ObjectParamMap getDefaultLinkParams()
   {
      return(new ObjectParamMap(_DefaultLinkParamsMap, DataStageObjectFactory.LINK_TYPE_INPUT));
   }
   
} // end of class FileStageProperties 
