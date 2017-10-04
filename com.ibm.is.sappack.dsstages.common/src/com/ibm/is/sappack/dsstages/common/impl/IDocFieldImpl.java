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
// Module Name : com.ibm.is.sappack.dsstages.common.impl
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.dsstages.common.impl;

import com.ibm.is.sappack.dsstages.common.IDocField;
import com.ibm.is.sappack.dsstages.common.IDocSegment;

public class IDocFieldImpl implements IDocField {

	static String copyright() {
		return com.ibm.is.sappack.dsstages.common.impl.Copyright.IBM_COPYRIGHT_SHORT;
	}

	String fieldName;
	int length;
	int lengthAsString;
	String sapType;
	IDocSegment segment;
	String description;

	public IDocFieldImpl(IDocSegment segment) {
		this.segment = segment;
	}
	
	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getLengthAsString() {
		return lengthAsString;
	}

	public void setLengthAsString(int lengthAsString) {
		this.lengthAsString = lengthAsString;
	}

	public String getSAPType() {
		return sapType;
	}

	public void setSAPType(String sapType) {
		this.sapType = sapType;
	}

	public IDocSegment getSegment() {
		return segment;
	}

	public String getFieldDescription() {
	    return description;
	}

	public void setFieldDescription(String description) {
	    this.description = description;
	}
}
