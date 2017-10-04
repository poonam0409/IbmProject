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
// Module Name : com.ibm.is.sappack.dsstages.idocload.segcollimpl
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.dsstages.idocload.segcollimpl;

import java.util.List;

import com.ibm.is.sappack.dsstages.common.IDocSegment;
import com.ibm.is.sappack.dsstages.idocload.IDocNode;
import com.ibm.is.sappack.dsstages.idocload.SegmentData;

public class InMemIDocNode implements IDocNode {

	static String copyright() {
		return com.ibm.is.sappack.dsstages.idocload.segcollimpl.Copyright.IBM_COPYRIGHT_SHORT;
	}

	String segNum;
	IDocNodeList children;
	InMemIDocNode parent;
	SegmentData data;
	IDocSegment segment;
	boolean incomplete = true;
	
	public InMemIDocNode(String segNum) {
		this.segNum = segNum;
		this.children = null;
	}

	@Override
	public List<IDocNode> getChildren() {
		if (children == null) {
			children = new IDocNodeList(2); // new ArrayList<IDocNode>(2);
		}
		return this.children;
	}

	@Override
	public SegmentData getSegmentData() {
		return this.data;
	}

	@Override
	public String getSegNum() {
		return this.segNum;
	}

	public void setSegmentData(SegmentData data) {
		this.data = data;
	}

	@Override
	public IDocNode getParent() {
		return this.parent;
	}

	@Override
	public IDocSegment getSegment() {
		return this.segment;
	}

}
