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

import java.util.Iterator;
import java.util.Map;

import com.ibm.is.sappack.dsstages.common.IDocSegment;
import com.ibm.is.sappack.dsstages.idocload.ControlRecordData;
import com.ibm.is.sappack.dsstages.idocload.IDocNode;
import com.ibm.is.sappack.dsstages.idocload.IDocTree;
import com.ibm.is.sappack.dsstages.idocload.SegmentData;

public class InMemIDocForestOldSchema  {

	static String copyright() {
		return com.ibm.is.sappack.dsstages.idocload.segcollimpl.Copyright.IBM_COPYRIGHT_SHORT;
	}

	Map<String, InMemIDocNode> id2NodeMap;
	Map<String, IDocTree> idocTrees;

	public InMemIDocForestOldSchema() {
		id2NodeMap = InMemorySegmentCollector.createStringMap(1000);
		idocTrees = InMemorySegmentCollector.createStringMap(1000);
	}

	
	public Iterator<IDocTree> getAllIDocTrees() {
		return this.idocTrees.values().iterator();
	}

	private InMemIDocTree findByIDocNumber(String idocNumber) {
		IDocTree result = this.idocTrees.get(idocNumber);
		if (result == null) {
			result = new InMemIDocTree(idocNumber, this.id2NodeMap, null);
			this.idocTrees.put(idocNumber, result);
		}
		return (InMemIDocTree) result;
	}

	
	public IDocNode insertSegment(String idocNumber, String segmentID, String parentID, IDocSegment seg, SegmentData segData) {
		IDocNode result = null;
		if (idocNumber == null) {
			// if inner node
			InMemIDocNode n = id2NodeMap.get(segmentID);
			if (n == null) {
				n = new InMemIDocNode(segmentID);
				this.id2NodeMap.put(segmentID, n);
			}
			InMemIDocNode parentNode = id2NodeMap.get(parentID);
			if (parentNode == null) {
				parentNode = new InMemIDocNode(parentID);
				id2NodeMap.put(parentID, parentNode);
			}
			parentNode.getChildren().add(n);
			n.parent = parentNode;
			n.incomplete = true;
			result = n;
		} else {
			// root node
			InMemIDocTree tree = findByIDocNumber(idocNumber);
			result = tree.addNode(segmentID, parentID, seg, segData);
		}
		return result;
	}

	/*
	public List<SegmentValidationResult> validateSegments() {
		List<SegmentValidationResult> result = new ArrayList<SegmentValidationResult>();
		Iterator<IDocTree> it = this.getAllIDocTrees();
		while (it.hasNext()) {
			InMemIDocTree tree = (InMemIDocTree) it.next();
			tree.validateSegments(result);
		}
		return result;
	}
	*/

	
	public void setControlRecord(String idocNumber, ControlRecordData controlRecordData) {
		InMemIDocTree tree = findByIDocNumber(idocNumber);
		tree.controlRecordData = controlRecordData;
	}

}
