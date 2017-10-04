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
// Module Name : com.ibm.is.sappack.dsstages.common.util
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.dsstages.common.util;

import java.util.List;

import com.ibm.is.sappack.dsstages.common.IDocSegment;
import com.ibm.is.sappack.dsstages.common.IDocType;

/**
 * IDocSegmentTraversal class that wraps an interface and static methods to traverse all segments of an IDoc.
 */
public class IDocSegmentTraversal {

	static String copyright() {
		return com.ibm.is.sappack.dsstages.common.util.Copyright.IBM_COPYRIGHT_SHORT;
	}

	/**
	 * Visitor interface that provides methods that are called for each visited IDoc segment.
	 */
	public static interface Visitor {
		void visit(IDocSegment segment);
	}

	/**
	 * This function acts as the IDoc segment traversal entrypoint. It goes through the list of all available segments
	 * (root and child segments) for the given IDoc so that the class implementing the Visitor interface has access to
	 * every single segment.
	 * 
	 * @param idoc
	 *           - the IDoc for which all segments shall be visited
	 * @param visitor
	 *           - the class implementing the Visitor interface and thus getting access to the IDoc segments
	 */
	public static void traverseIDoc(IDocType idoc, Visitor visitor) {
		List<IDocSegment> rootSegments = idoc.getRootSegments();
		for (IDocSegment rootSegment : rootSegments) {
			traverseIDocDepthFirstPreOrder(rootSegment, visitor);
		}
	}

	/**
	 * This function recusively goes through the list of IDoc segments and calls the visit() method of the class
	 * implementing the Visitor interface.
	 * 
	 * @param segment
	 *           - the IDoc segment to be traversed
	 * @param visitor
	 *           - the class implementing the Visitor interface and thus getting access to the IDoc segments
	 */
	private static void traverseIDocDepthFirstPreOrder(IDocSegment segment, Visitor visitor) {
		visitor.visit(segment);
		List<IDocSegment> childSegments = segment.getChildSegments();
		for (IDocSegment childSegment : childSegments) {
			traverseIDocDepthFirstPreOrder(childSegment, visitor);
		}
	}
}
