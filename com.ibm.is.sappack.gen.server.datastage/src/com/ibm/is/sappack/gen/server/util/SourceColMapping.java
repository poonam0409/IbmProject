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
// Module Name : com.ibm.is.sappack.gen.server.util
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.server.util;

import java.util.HashMap;
import java.util.Map;


public class SourceColMapping 
{
   // -------------------------------------------------------------------------------------
   //                                       Constants
   // -------------------------------------------------------------------------------------
   
   
   // -------------------------------------------------------------------------------------
   //                                 Member Variables
   // -------------------------------------------------------------------------------------
   private Map  _Mapping;

   
   static String copyright()
   { 
      return com.ibm.is.sappack.gen.server.util.Copyright.IBM_COPYRIGHT_SHORT; 
   }  

   
   public SourceColMapping()  
   {
      _Mapping = new HashMap();
   } // end of SourceColMapping()

   
   public FlowVarData getMapping(String colName)
   {
      return((FlowVarData) _Mapping.get(colName));
   }
   
   public int getSize()
   {
      return(_Mapping.size());
   }
   
   public void setMapping(String flowVarName, FlowVarData flowVar)
   {
      _Mapping.put(flowVarName, flowVar);
   }
   
   public String toString()
   {
      return("Mapping elements count = " + _Mapping.size());
   } 
   
} // end of class SourceColMapping
