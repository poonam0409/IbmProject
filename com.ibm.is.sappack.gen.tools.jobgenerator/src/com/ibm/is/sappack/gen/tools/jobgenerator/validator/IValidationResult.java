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

import java.util.List;

public interface IValidationResult {
	
	static final String IBM_COPYRIGHT_SHORT = Copyright.IBM_COPYRIGHT_SHORT;

	/**
	 * setName
	 * @param name
	 */
	public void setName(String name);
	
	/**
	 * setMessage
	 * @param message
	 */
	public void setMessage(String message);
	
	/**
	 * setValue
	 * @param value
	 */
	public void setValue(String value);
	
	/**
	 * setStatus
	 * @param status
	 */
	public void setStatus(Status status);
	
	
	/**
	 * getName
	 * @return
	 */
	public String getName();
	
	/**
	 * getValue
	 * @return
	 */
	public String getValue();
	
	/**
	 * getMessage
	 * @return
	 */
	public String getMessage();
	
	/**
	 * getStatus
	 * @return
	 */
	public Status getStatus();
	
	/**
	 * getSubResults
	 * @return
	 */
	public List<IValidationResult> getSubResults();
	
	/**
	 * toFlatList
	 * @return
	 */
	public List<IValidationResult> toFlatList();
}
