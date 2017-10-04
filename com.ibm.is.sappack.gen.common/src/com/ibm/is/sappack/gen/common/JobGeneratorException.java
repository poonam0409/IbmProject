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
// Module Name : com.ibm.is.sappack.gen.common
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.common;


public class JobGeneratorException extends BaseException {

	private static final long serialVersionUID = BaseException.serialVersionUID;
	
	
	static String copyright() { 
	   return com.ibm.is.sappack.gen.common.Copyright.IBM_COPYRIGHT_SHORT; 
	}
	
	public JobGeneratorException(String message) {
		this(message, (Throwable) null);
   } // end of JobGeneratorException()
	
	public JobGeneratorException(String message, Throwable cause) {
		super(message, cause);
   } // end of JobGeneratorException()

   public JobGeneratorException(String msgId, String msgParamArr[]) {
      this(msgId, msgParamArr, null);
   } // end of JobGeneratorException()
   
   public JobGeneratorException(String msgId, String msgParamArr[], Throwable cause) {
      super(msgId, msgParamArr, cause);
   } // end of JobGeneratorException()
	
} // end of class JobGeneratorException
