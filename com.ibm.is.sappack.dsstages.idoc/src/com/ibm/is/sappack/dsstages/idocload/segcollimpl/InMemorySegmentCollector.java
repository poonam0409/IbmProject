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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import com.ibm.is.sappack.dsstages.common.DSEnvironment;
import com.ibm.is.sappack.dsstages.common.DSSAPConnection;
import com.ibm.is.sappack.dsstages.common.IDocSegment;
import com.ibm.is.sappack.dsstages.common.IDocType;
import com.ibm.is.sappack.dsstages.idocload.ControlRecordData;
import com.ibm.is.sappack.dsstages.idocload.IDocNode;
import com.ibm.is.sappack.dsstages.idocload.IDocTree;
import com.ibm.is.sappack.dsstages.idocload.SegmentCollector;
import com.ibm.is.sappack.dsstages.idocload.SegmentData;
import com.ibm.is.sappack.dsstages.idocload.SegmentValidationHandler;

/**
 * A straghtforward in-memory implementation of a segment collector.
 * All technical fields (docnum, segnum, psegum), as well as the actual
 * segment data is kept together in an InMemIDocNode object.
 */
public class InMemorySegmentCollector extends SegmentCollector {

	static String copyright() {
		return com.ibm.is.sappack.dsstages.idocload.segcollimpl.Copyright.IBM_COPYRIGHT_SHORT;
	}

	final String CLASSNAME = InMemorySegmentCollector.class.getName();

	Map<String, IDocTree> idocTrees;
	
	public InMemorySegmentCollector(DSEnvironment env, DSSAPConnection dsSAPConnection, IDocType idocType, SegmentValidationHandler handler) {
		super(env, dsSAPConnection, idocType, handler);
		this.idocTrees = createStringMap(1000);
	}

	private InMemIDocTree findByIDocNumber(String idocNumber) {
		InMemIDocTree result = (InMemIDocTree) this.idocTrees.get(idocNumber);
		if (result == null) {
			result = new InMemIDocTree(idocNumber, this);
			this.idocTrees.put(idocNumber, result);
		}
		return result;
	}
	
	@Override
	public Iterator<IDocTree> getAllIDocTrees() {
		// skip those trees that have validation errors
		// TODO add more sophisticated validation handling
		List<IDocTree> validIdocTrees = new ArrayList<IDocTree>();
		for (IDocTree tree : this.idocTrees.values()) {
			if (((InMemIDocTree) tree).validateSegments()) {
				validIdocTrees.add(tree);
			}
			else {
				this.validationFailed = true;
			}
		}
		return validIdocTrees.iterator();
	}

	public static <K> Map<String, K> createStringMap(int capacity) {
		return new HashMap<String, K>(capacity);
	}

	@Override
	public void insertSegment(String idocNumber, String segmentID, String parentID, IDocSegment segment, SegmentData segData) {
		final String METHODNAME = "insertSegment(String idocNumber, String segmentID, String parentID, SegmentData segData)";  //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);
		if (this.logger.isLoggable(Level.FINER)) {
			this.logger.log(Level.FINER, "Inserting segment: IDoc Number: ''{0}'', segment ID: ''{1}'', parent segment ID: ''{2}''", new Object[]{idocNumber, segmentID, parentID}); //$NON-NLS-1$
			this.logger.log(Level.FINEST, "   Segment data: {0}", new String(segData.getSegmentDataBuffer())); //$NON-NLS-1$
		}
		InMemIDocTree t = findByIDocNumber(idocNumber);
		this.logger.log(Level.FINEST, "Found IDocTree {0}", t); //$NON-NLS-1$
		IDocNode n = t.addNode(segmentID, parentID, segment, segData);
		this.logger.log(Level.FINEST, "Created IDocNode {0}", n); //$NON-NLS-1$
		this.logger.exiting(CLASSNAME, METHODNAME);
	}

	@Override
	public void setControlRecord(String idocNumber, ControlRecordData controRecordData) {
		InMemIDocTree tree = findByIDocNumber(idocNumber);
		tree.controlRecordData = controRecordData;
	}
	
}
