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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

import com.ibm.is.sappack.dsstages.common.DSEnvironment;
import com.ibm.is.sappack.dsstages.common.DSSAPConnection;
import com.ibm.is.sappack.dsstages.common.IDocLoadConfiguration;
import com.ibm.is.sappack.dsstages.common.IDocSegment;
import com.ibm.is.sappack.dsstages.common.IDocType;
import com.ibm.is.sappack.dsstages.common.IDocTypeConfiguration;
import com.ibm.is.sappack.dsstages.common.StageLogger;
import com.ibm.is.sappack.dsstages.common.Utilities;
import com.ibm.is.sappack.dsstages.common.util.IDocSegmentTraversal;

public abstract class SegmentCollector {

	static String copyright() {
		return com.ibm.is.sappack.dsstages.idocload.Copyright.IBM_COPYRIGHT_SHORT;
	}

	protected Logger logger;
	protected DSEnvironment dsEnvironment;
	protected SegmentValidationHandler segmentValidationHandler;
	
	protected DSSAPConnection dsSAPConnection;
	protected IDocType idocType;
	protected Map<String, IDocSegment> segmentTypeNameToSegmentCache;
	
	protected boolean validationFailed = false;
	
	protected SegmentCollector(DSEnvironment env, DSSAPConnection dsSAPConnection, IDocType idocType, SegmentValidationHandler handler) {
		this.logger = StageLogger.getLogger();
		this.dsEnvironment = env;
		this.idocType = idocType;
		this.dsSAPConnection = dsSAPConnection;
		this.segmentValidationHandler = handler;
		this.segmentTypeNameToSegmentCache = new HashMap<String, IDocSegment>();
		IDocSegmentTraversal.traverseIDoc(idocType, new IDocSegmentTraversal.Visitor() {

			@Override
			public void visit(IDocSegment segment) {
				segmentTypeNameToSegmentCache.put(segment.getSegmentTypeName(), segment);
			}
			
		});
		
	}

	public void setSegmentValidationHandler(SegmentValidationHandler handler) {
		this.segmentValidationHandler = handler;
	}
	
	public SegmentValidationHandler getSegmentValidationHandler() {
		return this.segmentValidationHandler;
	}

	public IDocLoadConfiguration getIDocLoadConfiguration() {
		if (this.dsSAPConnection == null) {
			return null;
		}
		for (IDocTypeConfiguration config: this.dsSAPConnection.getIDocTypeConfigurations()) {
			if ( config.getIDocTypeName().equals(this.idocType.getIDocTypeName()) && config.getRelease().equals(idocType.getRelease()) ) {
				return config.getLoadConfiguration();
			}
		}
		return null;
	}
		
	public abstract void insertSegment(String idocNumber, String segmentID, String parentID, IDocSegment seg, SegmentData segData);

	public abstract void setControlRecord(String idocNumber, ControlRecordData controRecordData);
	
	public abstract Iterator<IDocTree> getAllIDocTrees();
	
	public boolean isValidationFailed() {
		return this.validationFailed;
	}
	
	public void cleanup() {
		// default implementation does nothing
	}
	
	protected IDocSegment findIDocSegment(String segmentTypeName) {
		return Utilities.findIDocSegment(idocType, segmentTypeName);
	//	return this.segmentTypeNameToSegmentCache.get(segmentTypeName);
	}

}
