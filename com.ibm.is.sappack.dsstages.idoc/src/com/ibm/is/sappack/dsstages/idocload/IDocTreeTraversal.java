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
// Module Name : com.ibm.is.sappack.dsstages.idocload
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.dsstages.idocload;

import java.util.Collection;
import java.util.List;

public class IDocTreeTraversal {

	static String copyright() {
		return com.ibm.is.sappack.dsstages.idocload.Copyright.IBM_COPYRIGHT_SHORT;
	}

	public static interface Visitor {

		/**
		 * Visit the passed node with a new segmentNumber. The parameters
		 * hierarchyLevel, segmentNumber, and parentSegmentNumber are filled are
		 * indicated as required by IDOC_INBOUND_ASNYCHRONOUS.
		 */
		void visit(IDocNode node, int hierarchyLevel, int segmentNumber, int parentSegmentNumber);
	}

	private static void traverseIDocDepthFirstPreOrder(IDocNode node, Visitor visitor, int parentSegmentNum, int hierarchyLevel, int[] globalSegNum) {
		globalSegNum[0]++;
		int segNum = globalSegNum[0];
		visitor.visit(node, hierarchyLevel, segNum, parentSegmentNum);
		Collection<IDocNode> kids = node.getChildren();
		for (IDocNode kid : kids) {
			traverseIDocDepthFirstPreOrder(kid, visitor, segNum, hierarchyLevel + 1, globalSegNum); 
		}
	}

	/**
	 * traverse the IDoc tree calling the visitor on each node.
	 */
	public static void traverseIDocTree(IDocTree tree, Visitor visitor) {
		List<IDocNode> rootNodes = tree.getRootNodes();
		int[] globalSegNum = new int[1];
		globalSegNum[0] = 0;
		for (IDocNode node : rootNodes) {
			traverseIDocDepthFirstPreOrder(node, visitor, 0, 0, globalSegNum); 
		}
	}
}
