//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2009, 2014                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.iis.sappack.gen.common.ui.editors
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.iis.sappack.gen.common.ui.editors;


import org.eclipse.wst.validation.ValidationResult;


public class ValidationEvent extends EditorEvent {
  	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}

	private ValidationResult validationResult;

	public ValidationEvent(ValidationResult validationResult, EditorPageBase sourcePage) {
		super(sourcePage);
		this.validationResult = validationResult;
	}

	public ValidationResult getValidationResult() {
		return this.validationResult;
	}
}
