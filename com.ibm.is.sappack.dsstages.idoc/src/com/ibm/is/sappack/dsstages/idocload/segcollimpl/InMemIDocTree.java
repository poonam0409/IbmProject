//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2009, 2013                                              
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

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.ibm.is.sappack.dsstages.common.CCFResource;
import com.ibm.is.sappack.dsstages.common.IDocSegment;
import com.ibm.is.sappack.dsstages.common.StageLogger;
import com.ibm.is.sappack.dsstages.idocload.ControlRecordData;
import com.ibm.is.sappack.dsstages.idocload.IDocNode;
import com.ibm.is.sappack.dsstages.idocload.IDocTree;
import com.ibm.is.sappack.dsstages.idocload.SegmentCollector;
import com.ibm.is.sappack.dsstages.idocload.SegmentData;
import com.ibm.is.sappack.dsstages.idocload.SegmentValidationResult;
import com.ibm.is.sappack.dsstages.idocload.SegmentValidationResult.Type;

public class InMemIDocTree implements IDocTree {

	static String copyright() {
		return com.ibm.is.sappack.dsstages.idocload.segcollimpl.Copyright.IBM_COPYRIGHT_SHORT;
	}

	static final String CLASSNAME = InMemIDocTree.class.getName();

	String idocNumber;

	ControlRecordData controlRecordData;

	Map<String, InMemIDocNode> id2NodeMap;
	IDocNodeList rootNodes;

	Logger logger;
	
	SegmentCollector segColl;

	public InMemIDocTree(String idocNumber, SegmentCollector segColl) {
		this(idocNumber, createNewMap(), segColl);
	}

	public static Map<String, InMemIDocNode> createNewMap() {
		return InMemorySegmentCollector.createStringMap(10);
	}

	public InMemIDocTree(String idocNumber, Map<String, InMemIDocNode> id2NodeMap, SegmentCollector segColl) {
		this.logger = StageLogger.getLogger();
		this.id2NodeMap = id2NodeMap;
		this.rootNodes = new IDocNodeList(1);
		this.idocNumber = idocNumber;
		this.segColl = segColl;
	}

	private boolean equalsIDocSegments(IDocSegment seg1, IDocSegment seg2) {
		if (seg1 == null) {
			return seg2 == null;
		}
		return seg1.equals(seg2);
	}

	public IDocNode addNode(String nodeID, String parentID, IDocSegment seg, SegmentData segData) {
		final String METHODNAME = "addNode(String nodeID, String parentID, SegmentData segData)"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);
		InMemIDocNode n = id2NodeMap.get(nodeID);
		// look if the node for the ID already exists
		//   if it does exist this means that a child segment of this node
		//   was inserted before. If so, however, it must be incomplete, otherwise
		//   the segment was already inserted
		if (n == null) {
			n = new InMemIDocNode(nodeID);
			this.id2NodeMap.put(nodeID, n);
			n.segment = seg;
		} else {
			if (!n.incomplete) {
				// node was already "fully" inserted
				SegmentValidationResult vr = new SegmentValidationResult();
				vr.setIDocNode(n);
				vr.setIDocTree(this);
				String msg = CCFResource.getCCFMessage("CC_IDOC_SegmentAlreadySeen",   //$NON-NLS-1$
				                                       new Object[] { nodeID, parentID, this.idocNumber });
				vr.setType(SegmentValidationResult.Type.DUPLICATE_SEGMENT);
				vr.setMessage(msg);
				this.segColl.getSegmentValidationHandler().handleSegmentValidationMessage(vr);
				return null;
			}
		}
		// parentID must not be null
		if (parentID == null) {
			throw new NullPointerException();
		}
		n.data = segData;
		n.incomplete = false;
		n.segment = seg;

		// segment is root segment if its metadata does indicate so.
		// Note that in this case we don't consider the actual parent ID
		if (seg.getParent() == null) {
			this.rootNodes.add(n);
		} else {
			// get parent node, if it does not exist, create it anyway 
			//   but then it is incomplete
			InMemIDocNode parentNode = id2NodeMap.get(parentID);
			if (parentNode == null) {
				parentNode = new InMemIDocNode(parentID);
				parentNode.segment = seg.getParent();
				id2NodeMap.put(parentID, parentNode);
			} else {
				if (!equalsIDocSegments(parentNode.segment, seg.getParent())) {
					String segmentOfParentName = parentNode.segment == null ? "<null>" : parentNode.segment.getSegmentTypeName(); //$NON-NLS-1$
					String parentSegmentName = seg.getParent() == null ? "<null>" : seg.getParent().getSegmentTypeName(); //$NON-NLS-1$
					SegmentValidationResult valResult = new SegmentValidationResult();
					valResult.setIDocTree(this);
					valResult.setIDocNode(n);
					valResult.setType(Type.WRONG_PARENT_SEGMENT_TYPE);
					String msg = CCFResource.getCCFMessage("CC_IDOC_WrongSegmentMetadata", //$NON-NLS-1$
					                                       new Object[] { n.getSegNum(), this.getIDocNumber(), segmentOfParentName, parentSegmentName });
					valResult.setMessage(msg);
					this.segColl.getSegmentValidationHandler().handleSegmentValidationMessage(valResult);
					return null;
				}
			}
			n.parent = parentNode;		
			parentNode.getChildren().add(n);
		}
		
		this.logger.exiting(CLASSNAME, METHODNAME);
		return n;
	}

	@Override
	public String getIDocNumber() {
		return this.idocNumber;
	}

	@Override
	public List<IDocNode> getRootNodes() {
		return this.rootNodes;
	}

	public boolean validateSegments() {
		final String METHODNAME = "validateSegments(List<SegmentValidationResult>)"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);
		boolean isValid = true;
		Set<InMemIDocNode> nodeSet = new HashSet<InMemIDocNode>(this.id2NodeMap.values());
		boolean allNodesIncomplete = true;
		for (InMemIDocNode n : nodeSet) {
			if (!n.incomplete) {
				allNodesIncomplete = false;
				if (n.parent == null) {
					if (!rootNodes.contains(n)) {
						// n is not a root node 
						SegmentValidationResult valResult = new SegmentValidationResult();
						valResult.setIDocTree(this);
						valResult.setIDocNode(n);
						valResult.setType(Type.ORPHAN);
						valResult.setMessage(CCFResource.getCCFMessage("CC_IDOC_SegmentIsOrphan", //$NON-NLS-1$
						                                               new Object[] { n.getSegNum(), this.getIDocNumber() }));
						this.segColl.getSegmentValidationHandler().handleSegmentValidationMessage(valResult);
						isValid = false;
					}
				} else {
					if (n.parent.incomplete) {
						// n doesn't have a parent
						SegmentValidationResult valResult = new SegmentValidationResult();
						valResult.setIDocTree(this);
						valResult.setIDocNode(n);
						valResult.setType(Type.ORPHAN);
						valResult.setMessage(CCFResource.getCCFMessage("CC_IDOC_SegmentIsOrphan", //$NON-NLS-1$
                                                                 new Object[] { n.getSegNum(), this.getIDocNumber() }));
						this.segColl.getSegmentValidationHandler().handleSegmentValidationMessage(valResult);
						isValid = false;
					} else {
						// check that the segment of the parent node is equal to the parent segment of this node
						IDocSegment seg = n.segment;
						if (seg != null) {
							IDocSegment segmentOfParent = n.parent.segment;
							IDocSegment parentOfSegment = seg.getParent();
							String segmentOfParentName = segmentOfParent == null ? null : segmentOfParent.getSegmentTypeName();
							String parentOfSegmentName = parentOfSegment == null ? null : parentOfSegment.getSegmentTypeName();
							boolean nonMatchingParentSegments = false;
							if (segmentOfParent == null) {
								if (parentOfSegment != null) {
									nonMatchingParentSegments = true;
								}
							} else {
								if (!segmentOfParent.equals(parentOfSegment)) {
									nonMatchingParentSegments = true;
								}
							}
							if (nonMatchingParentSegments) {
								SegmentValidationResult valResult = new SegmentValidationResult();
								valResult.setIDocTree(this);
								valResult.setIDocNode(n);
								valResult.setType(Type.WRONG_PARENT_SEGMENT_TYPE);
								String msg = CCFResource.getCCFMessage("CC_IDOC_WrongSegmentMetadata", //$NON-NLS-1$
								                                       new Object[] { n.getSegNum(), this.getIDocNumber(), segmentOfParentName, parentOfSegmentName });
								valResult.setMessage(msg);
								this.segColl.getSegmentValidationHandler().handleSegmentValidationMessage(valResult);
								isValid = false;
							}
						}
					}
					if (!nodeSet.contains(n.parent)) {
						// node's parent is not in this tree, can only happen with old schema
						SegmentValidationResult valResult = new SegmentValidationResult();
						valResult.setIDocTree(this);
						valResult.setIDocNode(n);
						valResult.setType(Type.ORPHAN);
						valResult.setMessage(CCFResource.getCCFMessage("CC_IDOC_SegmentHasParentInDifferentIDoc",   //$NON-NLS-1$ 
						                     new Object[] { n.getSegNum(), this.getIDocNumber(), n.getParent().getSegNum() }));
						this.segColl.getSegmentValidationHandler().handleSegmentValidationMessage(valResult);
						isValid = false;
					}
				}
			}
		}
		if (allNodesIncomplete) {
			SegmentValidationResult valResult = new SegmentValidationResult();
			valResult.setIDocTree(this);
			valResult.setIDocNode(null);
			valResult.setType(Type.NO_SEGMENTS_FOR_IDOC);
			valResult.setMessage(CCFResource.getCCFMessage("CC_IDOC_NoSegmentsForIDoc", this.getIDocNumber()));   //$NON-NLS-1$
			this.segColl.getSegmentValidationHandler().handleSegmentValidationMessage(valResult);
			isValid = false;
		}
		this.logger.exiting(CLASSNAME, METHODNAME);
		return isValid;
	}

	@Override
	public ControlRecordData getControlRecordData() {
		return this.controlRecordData;
	}
	
	static String NL = "\n";
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("================================="+NL+"IDOC Tree"+NL);
		buf.append("Number: " + this.getIDocNumber() + NL);
		buf.append("Root nodes:" + NL);
		List<IDocNode> roots = this.getRootNodes();
		for (IDocNode root : roots) {
			printIDocNode(0, buf, root);
		}
		
		return buf.toString();
	}

	private void printIDocNode(int level, StringBuffer buf, IDocNode root) {
		String tab = "";
		for (int i=0; i<level; i++) {
			tab += " ";
		}
		buf.append(tab + "SEGNUM: " +root.getSegNum() + ", seg def: " +root.getSegmentData().getSegmentDefinitionName() + NL);
		buf.append(tab + "  DATA: |" + new String(root.getSegmentData().getSegmentDataBuffer()) + "|" + NL);
		List<IDocNode> kids = root.getChildren();
		for (IDocNode kid : kids) {
			printIDocNode(level + 3, buf, kid);
		}
	}

}
