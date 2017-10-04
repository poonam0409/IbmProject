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


import com.ibm.is.sappack.gen.common.BaseException;


public class DSAccessException extends BaseException 
{
   // -------------------------------------------------------------------------------------
   //                                       Constants
   // -------------------------------------------------------------------------------------
   protected static final long serialVersionUID = BaseException.serialVersionUID + 1;

   
   // -------------------------------------------------------------------------------------
   //                                 Member Variables
   // -------------------------------------------------------------------------------------

	static String copyright() 
	{ 
	   return com.ibm.is.sappack.gen.server.common.util.Copyright.IBM_COPYRIGHT_SHORT; 
	}
	
	public DSAccessException(String message) 
	{
		super(message);
	}
	
	
	public DSAccessException(String message, Throwable cause) 
	{
		super(message, cause);
	}

   public DSAccessException(String msgId, String msgParamArr[]) {
      this(msgId, msgParamArr, null);
   } // end of DSAccessException()
   
   
   public DSAccessException(String msgId, String msgParamArr[], Throwable cause) {
      super(msgId, msgParamArr, cause);
   } // end of DSAccessException()
	
} // end of class DSAccessException
