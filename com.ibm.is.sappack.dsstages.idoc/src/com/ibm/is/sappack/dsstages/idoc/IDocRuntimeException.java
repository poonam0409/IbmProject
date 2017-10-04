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
// Module Name : com.ibm.is.sappack.dsstages.idoc
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.dsstages.idoc;

public class IDocRuntimeException extends RuntimeException {

	static String copyright() {
		return com.ibm.is.sappack.dsstages.idoc.Copyright.IBM_COPYRIGHT_SHORT;
	}

	private static final long serialVersionUID = 7937703459099421183L;

	public IDocRuntimeException() {
		super();
	}

	public IDocRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

	public IDocRuntimeException(String message) {
		super(message);
	}

	public IDocRuntimeException(Throwable cause) {
		super(cause);
	}

	

}
