// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2011, 2013                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.is.sappack.dsstages.xmeta.common
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.dsstages.xmeta.common;


public class XMetaIDocHandlingException extends Exception {

	private static final long serialVersionUID = 1L;
	private Object[] objects;


	static String copyright() {
		return com.ibm.is.sappack.dsstages.xmeta.common.Copyright.IBM_COPYRIGHT_SHORT;
	}

	public XMetaIDocHandlingException(Exception cause) {
		super(cause);
	}

	public XMetaIDocHandlingException(String string) {
		this.objects = new Object[] { string };
	}

	public XMetaIDocHandlingException(Object[] objects) {
		this.objects = objects;
	}

	public Object[] getObjects() {
		return objects;
	}

}
