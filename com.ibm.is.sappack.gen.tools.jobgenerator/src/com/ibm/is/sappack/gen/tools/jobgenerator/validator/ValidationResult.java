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
// Module Name : com.ibm.is.sappack.gen.tools.jobgenerator.validator
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.tools.jobgenerator.validator;

import java.util.ArrayList;
import java.util.List;

public class ValidationResult implements IValidationResult{

	private String message;
	private String name;
	private String value;
	private Status status = Status.NULL;
	private List<IValidationResult> subResults;
	
	
	/**
	 * ValidationResult
	 * 
	 * @param name
	 * @param value
	 * @param message
	 * @param isSuccessful
	 */
	public ValidationResult(String name, String value, String message, Status status) {
		this.name = name;
		this.value = value;
		this.message = message;
		this.status = status;
		this.subResults = new ArrayList<IValidationResult>();
	}
	
	/**
	 * ValidationResult
	 */
	public ValidationResult() {
		this.subResults = new ArrayList<IValidationResult>();
	}
	
	public void setMessage(String message) {
		this.message = message;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	static String copyright() {
		return com.ibm.is.sappack.gen.tools.jobgenerator.validator.Copyright.IBM_COPYRIGHT_SHORT;
	}
	
	@Override
	public String getMessage() {
		return this.message;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public List<IValidationResult> getSubResults() {
		return this.subResults;
	}

	@Override
	public String getValue() {
		return this.value;
	}

	@Override
	public Status getStatus() {
		return this.status;
	}

	/**
	 * toFlatList
	 * @param validationResult
	 * @param flatList
	 */
	private void toFlatList(IValidationResult validationResult, List<IValidationResult> flatList) {
		
		// add root validation result
		flatList.add(validationResult);
		
		// add sub results recursively
		for(IValidationResult result:validationResult.getSubResults()) {
			toFlatList(result, flatList);
		}
		
	}
	
	
	@Override
	public List<IValidationResult> toFlatList() {
	
		List<IValidationResult> flatList = new ArrayList<IValidationResult>();
		this.toFlatList(this, flatList);
		
		return flatList;
	}

}
