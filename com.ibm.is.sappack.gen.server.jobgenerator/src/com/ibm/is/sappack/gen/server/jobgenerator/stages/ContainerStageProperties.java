//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2009, 2013                                              
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


public class ContainerStageProperties 
{
   // -------------------------------------------------------------------------------------
   //                                         Constants
   // -------------------------------------------------------------------------------------
   
   
   // -------------------------------------------------------------------------------------
   //                                 Member Variables
   // -------------------------------------------------------------------------------------
   private static final Map<String, String>  _DefaultStageParamsMap;

   
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
      _DefaultStageParamsMap = new HashMap<String,String>();
      _DefaultStageParamsMap.put("ContainerType", "1");

   } // end of static

   
   public static ObjectParamMap getDefaultStageParams()
   {
      return(new ObjectParamMap(_DefaultStageParamsMap, DataStageObjectFactory.STAGE_TYPE_DEFAULT));
   }
   
} // end of class ContainerStageProperties
