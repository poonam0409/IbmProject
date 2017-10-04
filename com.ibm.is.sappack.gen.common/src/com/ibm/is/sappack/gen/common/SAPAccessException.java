//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2013                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.is.sappack.gen.common
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.common;


public class SAPAccessException extends BaseException {

	private static final long serialVersionUID = BaseException.serialVersionUID;
	
	
	static String copyright() { 
	   return com.ibm.is.sappack.gen.common.Copyright.IBM_COPYRIGHT_SHORT; 
	}
	
	public SAPAccessException(String message) {
		this(message, (Throwable) null);
   } // end of SAPAccessException()
	
	public SAPAccessException(String message, Throwable cause) {
		super(message, cause);
   } // end of SAPAccessException()

   public SAPAccessException(String msgId, String msgParamArr[]) {
      this(msgId, msgParamArr, null);
   } // end of SAPAccessException()
   
   public SAPAccessException(String msgId, String msgParamArr[], Throwable cause) {
      super(msgId, msgParamArr, cause);
   } // end of SAPAccessException()
	
} // end of class SAPAccessException
