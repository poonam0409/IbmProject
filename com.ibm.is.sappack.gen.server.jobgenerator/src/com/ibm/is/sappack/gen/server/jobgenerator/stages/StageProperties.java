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

import com.ibm.is.sappack.gen.common.Constants;
import com.ibm.is.sappack.gen.server.datastage.DataStageObjectFactory;
import com.ibm.is.sappack.gen.server.util.ObjectParamMap;


public abstract class StageProperties
{
   // -------------------------------------------------------------------------------------
   //                                         Constants
   // -------------------------------------------------------------------------------------
   public  static final String STAGE_TYPE_CLASS_NAME_CUSTOM_STAGE   = DataStageObjectFactory.STAGE_TYPE_CLASS_NAME_CUSTOM_STAGE;
   public  static final String STAGE_TYPE_CLASS_NAME_TRANSFORMER    = DataStageObjectFactory.STAGE_TYPE_CLASS_NAME_TRANSFORMER;
   public  static final String STAGE_TYPE_CLASS_NAME_CONTAINER      = DataStageObjectFactory.STAGE_TYPE_CLASS_NAME_CONTAINER;
   
   public  static final String  REMOVEDUPLICATES_KEY_NAME   = "key";                                    //$NON-NLS-1$
   public  static final String  REMOVEDUPLICATES_KEY_VALUE  = "\\(02)\\(02)0\\(01)\\(03)key\\(02){0}\\(02)0"; //$NON-NLS-1$

  
   static String copyright()
   { 
   	return com.ibm.is.sappack.gen.server.jobgenerator.stages.Copyright.IBM_COPYRIGHT_SHORT; 
   }


   public static ObjectParamMap getEmptyDefaultStageParams()
   {
      return(new ObjectParamMap(new HashMap(), DataStageObjectFactory.STAGE_TYPE_DEFAULT));
   }


   public static String convertNullToEmpty(String string2Check)
   {
      String retString = string2Check;
      if (string2Check == null)
      {
         retString = Constants.EMPTY_STRING;
      }
      
      return(retString);
   } // end of convertNullToEmpty()
   
} // end of class StageProperties 
