//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2014                                              
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


public class RetryRequestException extends BaseException {

	private static final long serialVersionUID = BaseException.serialVersionUID;
	private String _URLLocation;
	
	
	static String copyright() { 
	   return com.ibm.is.sappack.gen.common.Copyright.IBM_COPYRIGHT_SHORT; 
	}
	
	public RetryRequestException(String urlLocation) {
		super(null);
		_URLLocation = urlLocation;
   } // end of RetryRequestException()
	
   public String getURLLocation() {
   	return(_URLLocation);
   }
} // end of class RetryRequestException
