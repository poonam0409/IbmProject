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

import com.ibm.is.sappack.dsstages.common.IDocField;
import com.ibm.is.sappack.dsstages.common.IDocSegment;
import com.ibm.is.sappack.dsstages.common.IDocType;

public class IDocSegmentImpl implements IDocSegment {

	static String copyright() {
		return com.ibm.is.sappack.dsstages.common.impl.Copyright.IBM_COPYRIGHT_SHORT;
	}

	protected long segmentNr;
	protected String segmentTypeName;
	protected String segmentDefinitionName;
	protected String segmentDescription;
	protected boolean mandatory = false;
	protected boolean parentFlag = false;
	protected IDocSegment parent = null;
	protected IDocType idocType = null;
	protected long minOccurrence;
	protected long maxOccurrence;

	private ArrayList<IDocSegment> children;
	protected List<IDocField> fields;
	private long hierarchyLevel;

	public IDocSegmentImpl(IDocType docType) {
		this.idocType = docType;
		this.children = new ArrayList<IDocSegment>(2);
		this.fields = new ArrayList<IDocField>(50);
	}

	public long getSegmentNr() {
		return segmentNr;
	}

	public void setSegmentNr(long segmentNr) {
		this.segmentNr = segmentNr;
	}

	public void setSegmentTypeName(String segmentType) {
		this.segmentTypeName = segmentType;
	}

	public void setSegmentDefinitionName(String segmentDef) {
		this.segmentDefinitionName = segmentDef;
	}

	public String getSegmentDescription() {
		return segmentDescription;
	}

	public void setSegmentDescription(String segmentDescription) {
		this.segmentDescription = segmentDescription;
	}

	public boolean isMandatory() {
		return mandatory;
	}

	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}

	public boolean isParentFlag() {
		return parentFlag;
	}

	public void setParentFlag(boolean parentFlag) {
		this.parentFlag = parentFlag;
	}

	public IDocSegment getParent() {
		return parent;
	}

	public void setParent(IDocSegment parent) {
		this.parent = parent;
	}

	public IDocType getIdocType() {
		return idocType;
	}

	public void setIdocType(IDocType idocType) {
		this.idocType = idocType;
	}

	public List<IDocSegment> getChildSegments() {
		return this.children;
	}

	public List<IDocField> getFields() {
		return this.fields;
	}

	public long getHierarchyLevel() {
		return this.hierarchyLevel;
	}

	public void setHierarchyLevel(long hierLevel) {
		this.hierarchyLevel = hierLevel;
	}

	public IDocType getIDocType() {
		return this.idocType;
	}

	public String getSegmentDefinitionName() {
		return this.segmentDefinitionName;
	}

	public String getSegmentTypeName() {
		return this.segmentTypeName;
	}

	public long getMinOccurrence() {
		return minOccurrence;
	}

	public void setMinOccurrence(long minOccurrence) {
		this.minOccurrence = minOccurrence;
	}

	public long getMaxOccurrence() {
		return maxOccurrence;
	}

	public void setMaxOccurrence(long maxOccurrence) {
		this.maxOccurrence = maxOccurrence;
	}
}
