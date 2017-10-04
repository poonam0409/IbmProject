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
// Module Name : com.ibm.is.sappack.dsstages.idoc.listener.util
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.dsstages.idoc.listener.util;


/**
 * The superclass of all exceptions in the IDoc Listener
 */
public class IDocServerException extends Exception {
	
	static String copyright() {
		return com.ibm.is.sappack.dsstages.idoc.listener.util.Copyright.IBM_COPYRIGHT_SHORT;
	}

	private static final long serialVersionUID = -3435994160827604767L;
	
	public IDocServerException(String msg) {
		super(msg);
	}

}
