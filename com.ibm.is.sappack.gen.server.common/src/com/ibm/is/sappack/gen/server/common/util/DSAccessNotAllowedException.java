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



public class DSAccessNotAllowedException extends DSAccessException 
{
   // -------------------------------------------------------------------------------------
   //                                       Constants
   // -------------------------------------------------------------------------------------
   protected static final long serialVersionUID = DSAccessException.serialVersionUID + 1;

   
   // -------------------------------------------------------------------------------------
   //                                 Member Variables
   // -------------------------------------------------------------------------------------

   
	static String copyright()
	{ 
	   return com.ibm.is.sappack.gen.server.common.util.Copyright.IBM_COPYRIGHT_SHORT; 
	}   

	
	public DSAccessNotAllowedException(String message) 
	{
		super(message);
	}
	
	
	public DSAccessNotAllowedException(String message, Throwable cause) 
	{
		super(message, cause);
	}

	
   public DSAccessNotAllowedException(String msgId, String msgParamArr[]) {
      this(msgId, msgParamArr, null);
   } // end of DSAccessNotAllowedException()
   
   
   public DSAccessNotAllowedException(String msgId, String msgParamArr[], Throwable cause) {
      super(msgId, msgParamArr, cause);
   } // end of DSAccessNotAllowedException()
	
} // end of class DSAccessNotAllowedException
