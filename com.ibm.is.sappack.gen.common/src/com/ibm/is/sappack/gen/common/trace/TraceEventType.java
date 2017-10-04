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
// Module Name : com.ibm.is.sappack.gen.common.trace
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.common.trace;

import java.util.logging.Level;



/**
 * This class provides methods for the trace event type. 
 *
 * @since 1.0.0
 * @version 1.0.0
**/
public final class TraceEventType extends Level
{
   // ======================================================================
   //                             constants
   // ======================================================================
   private static final long    serialVersionUID       = 1L;

   
   static String copyright()
   { 
      return com.ibm.is.sappack.gen.common.trace.Copyright.IBM_COPYRIGHT_SHORT; 
   }
   
   /**
    * This constructor initializes the instance and calls the constructor of the 
    * base class.
    * 
    * @param pTraceLevel  initial trace level
   **/
   public TraceEventType(Level pTraceLevel)
   {
      super(pTraceLevel.toString(), pTraceLevel.intValue());
   } // end of TraceEventType()
   
   
   /**
    * This method returns the (java) trace level.
    * 
    * @return trace level
   **/
   public Level getLevel()
   {
      return(this); 
   } // end of getLevel()
   
} // end of class TraceEventType
