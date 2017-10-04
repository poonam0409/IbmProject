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
// Module Name : com.ibm.is.sappack.dsstages.prereq
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.dsstages.prereq;


public class PrereqCheckResult {

	static String copyright() {
		return com.ibm.is.sappack.dsstages.prereq.Copyright.IBM_COPYRIGHT_SHORT;
	}

	public enum TYPE {
		SUCCESS, FAILURE
	};

	TYPE type;
	String name;
	String message;
	String details;

	public static final String MSG_SUCCESS = PrereqCheckMessages.getString("PrereqCheckResult.0"); //$NON-NLS-1$
	public static final String MSG_FAILURE = PrereqCheckMessages.getString("PrereqCheckResult.1"); //$NON-NLS-1$
	
	public PrereqCheckResult(TYPE type, String name, String message) {
		super();
		this.type = type;
		this.name = name;
		this.message = message;
		this.details = null;
	}

	public PrereqCheckResult(TYPE type, String name, String message, String details) {
		super();
		this.type = type;
		this.name = name;
		this.message = message;
		this.details = details;
	}

	public PrereqCheckResult(TYPE type, String name, String message, Throwable t) {
		this(type, name, message, t.toString());
	}

	public String getType() {
		if (this.type == TYPE.SUCCESS) {
			return MSG_SUCCESS;
		}
		return MSG_FAILURE;
	}

	public String getName() {
		return name;
	}

	public String getMessage() {
		return message;
	}

	public String getDetails() {
		return this.details;
	}

}
