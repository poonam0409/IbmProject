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

import java.util.ArrayList;
import java.util.List;

import com.ibm.is.sappack.dsstages.common.IDocSegment;
import com.ibm.is.sappack.dsstages.common.IDocType;

public class IDocTypeImpl implements IDocType {

	static String copyright() {
		return com.ibm.is.sappack.dsstages.common.impl.Copyright.IBM_COPYRIGHT_SHORT;
	}

	String iDocTypeName;
	String basicTypeName;
	String release;
	String description;

	List<IDocSegment> rootSegments;
	ControlRecord controlRecord;

	public IDocTypeImpl() {
		this.rootSegments = new ArrayList<IDocSegment>(1);
	}

	public String getIDocTypeName() {
		return iDocTypeName;
	}

	public void setIDocTypeName(String docTypeName) {
		iDocTypeName = docTypeName;
	}
	
	public String getBasicTypeName() {
		return basicTypeName;
	}
	
	public void setBasicTypeName(String basicTypeName) {
		this.basicTypeName = basicTypeName;
	}

	public String getRelease() {
		return release;
	}

	public void setRelease(String release) {
		this.release = release;
	}

	public String getIDocTypeDescription() {
		return description;
	}

	public void setIDocTypeDescription(String description) {
		this.description = description;
	}

	public List<IDocSegment> getRootSegments() {
		return rootSegments;
	}

	public void setRootSegments(List<IDocSegment> rootSegments) {
		this.rootSegments = rootSegments;
	}

	public void setControlRecord(ControlRecord cr) {
		this.controlRecord = cr;
	}

	public ControlRecord getControlRecord() {
		return this.controlRecord;
	}
}
