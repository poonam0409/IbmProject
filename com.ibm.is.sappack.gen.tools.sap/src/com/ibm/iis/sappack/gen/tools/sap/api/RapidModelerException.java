//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2011, 2014                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.iis.sappack.gen.tools.sap.api
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.iis.sappack.gen.tools.sap.api;


/**
 * All exceptions occurring for Rapid Modeler for SAP
 */
public class RapidModelerException extends Exception {
	private static final long serialVersionUID = 1574894004296638577L;


	static String copyright() { 
 	   return Copyright.IBM_COPYRIGHT_SHORT; 
 	}	

	public RapidModelerException() {
		super();
	}

	public RapidModelerException(String message, Throwable t) {
		super(message, t);
	}

	public RapidModelerException(String message) {
		super(message);
	}

	public RapidModelerException(Throwable t) {
		super(t);
	}

}