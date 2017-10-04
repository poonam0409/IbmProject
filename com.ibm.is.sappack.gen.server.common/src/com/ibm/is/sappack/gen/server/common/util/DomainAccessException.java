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
// Module Name : com.ibm.is.sappack.gen.server.common.util
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.server.common.util;



public class DomainAccessException extends DSAccessException 
{
   // -------------------------------------------------------------------------------------
   //                                       Constants
   // -------------------------------------------------------------------------------------
   protected static final long serialVersionUID = DSAccessException.serialVersionUID + 4;

   
   // -------------------------------------------------------------------------------------
   //                                 Member Variables
   // -------------------------------------------------------------------------------------

   
	static String copyright()
	{ 
	   return com.ibm.is.sappack.gen.server.common.util.Copyright.IBM_COPYRIGHT_SHORT; 
	}   

	
   public DomainAccessException(String msgParam) {
      this(msgParam, null);
   } // end of DomainAccessException()
   
   
   public DomainAccessException(String msgParam, Throwable cause) {
      super("104600E", new String[] { msgParam }, cause);
   } // end of DomainAccessException()
	
} // end of class DomainAccessException
