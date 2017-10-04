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
// Module Name : com.ibm.is.sappack.gen.server.util
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.server.util;

import java.util.Map;

import com.ibm.is.sappack.gen.common.DBSupport;
import com.ibm.is.sappack.gen.common.DBSupport.DataBaseType;


public final class ModelInfoBlock
{
   // ---------------------------------------------------------------
   //                      Constants
   // ---------------------------------------------------------------

   // ---------------------------------------------------------------
   //                      Member Variables
   // ---------------------------------------------------------------
   private Map           _TableMap;
   private DataBaseType  _DatabaseType;
   
   
   static String copyright()
   { 
      return com.ibm.is.sappack.gen.server.util.Copyright.IBM_COPYRIGHT_SHORT; 
   }

   
   public ModelInfoBlock(Map tableMap, DataBaseType databaseType)
   {
      _TableMap = tableMap;
      
      DBSupport.checkDatabaseType(databaseType);
      
      _DatabaseType = databaseType;
   } // end of ModelInfoBlock()
   
   
   public DataBaseType getDatabaseId()
   {
      return(_DatabaseType);
   } // end of getDatabaseType()

   
   public Map getTableMap()
   {
      return(_TableMap);
   } // end of getTableMap()
   
   
   public String toString()
   {
      return("db type = " + _DatabaseType + " - tbl cnt = " + _TableMap.size());
   } // end of toString()
   
} // end of class ModelInfoBlock
