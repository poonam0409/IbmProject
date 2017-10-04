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

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.is.sappack.dsstages.common.Constants;
import com.ibm.is.sappack.dsstages.common.DSEnvironment;
import com.ibm.is.sappack.dsstages.common.DSSAPConnection;
import com.ibm.is.sappack.dsstages.common.IDocType;
import com.ibm.is.sappack.dsstages.common.RuntimeConfiguration;
import com.ibm.is.sappack.dsstages.common.StageLogger;
import com.ibm.is.sappack.dsstages.idoc.IDocRuntimeException;
import com.ibm.is.sappack.dsstages.idocload.segcollimpl.FixedFilesSegmentCollector;
import com.ibm.is.sappack.dsstages.idocload.segcollimpl.InMemorySegmentCollector;
import com.ibm.is.sappack.dsstages.idocload.segcollimpl.VariableFilesSegmentCollector;
import com.ibm.is.sappack.dsstages.idocload.sender.IDocSenderImpl;

public class IDocLoadFactory {

	static String copyright() {
		return com.ibm.is.sappack.dsstages.idocload.Copyright.IBM_COPYRIGHT_SHORT;
	}

	private static final String CLASSNAME = IDocLoadFactory.class.getName();
	
	public static SegmentCollector createSegmentCollector(DSEnvironment env, DSSAPConnection conn, IDocType idocType) {
		final String METHODNAME = "createSegmentCollector(DSSAPConnection, IDocType)";  //$NON-NLS-1$
		Logger logger = StageLogger.getLogger();
		logger.entering(CLASSNAME, METHODNAME);
		SegmentValidationHandler segValidationHandler = new DSLogSegmentValidationHandler();
		RuntimeConfiguration config = RuntimeConfiguration.getRuntimeConfiguration();
		String segmentCollectorMode = config.getSegmentCollectionMode();
		logger.log(Level.FINE, "Segment collection mode: {0}", segmentCollectorMode); //$NON-NLS-1$
		SegmentCollector result = null;
		try {
			if (Constants.ENV_VAR_VALUE_DSSAP_IDOC_LOAD_SEGMENT_COLLECTION_MODE_INMEM.equals(segmentCollectorMode)) {
				result = new InMemorySegmentCollector(env, conn, idocType, segValidationHandler);
			} else if (Constants.ENV_VAR_VALUE_DSSAP_IDOC_LOAD_SEGMENT_COLLECTION_MODE_FIXEDFILES.equals(segmentCollectorMode)) {
				int numberOfFiles = config.getFixedFilesSegmentCollectionMode_FileNumber();
				result = new FixedFilesSegmentCollector(env, conn, idocType, segValidationHandler, numberOfFiles);
			} else if (Constants.ENV_VAR_VALUE_DSSAP_IDOC_LOAD_SEGMENT_COLLECTION_MODE_VARIABLEFILES.equals(segmentCollectorMode)) {
				int maximumIDocPerFile = config.getVariableFilesSegmentCollectionMode_MaximumNumberOfIDocsPerFile();
				result = new VariableFilesSegmentCollector(env, conn, idocType, segValidationHandler, maximumIDocPerFile);
			} else {
				// default is the variable file segment collector
				int maximumIDocPerFile = config.getVariableFilesSegmentCollectionMode_MaximumNumberOfIDocsPerFile();
				result = new VariableFilesSegmentCollector(env, conn, idocType, segValidationHandler, maximumIDocPerFile);
			}
			logger.log(Level.FINE, "Segment collector used: {0}", result.getClass().getName()); //$NON-NLS-1$
		} catch (IOException e) {
			StageLogger.logUnexpectedException(e);
			throw new IDocRuntimeException(e);
		}
		logger.exiting(CLASSNAME, METHODNAME);
		return result;
	}

	public static IDocSender createIDocSender() {
		return new IDocSenderImpl();
	}
}
