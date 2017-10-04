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
// Module Name : com.ibm.is.sappack.dsstages.idocload
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.dsstages.idocload;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.is.sappack.dsstages.common.CCFResource;
import com.ibm.is.sappack.dsstages.common.RuntimeConfiguration;
import com.ibm.is.sappack.dsstages.common.StageLogger;
import com.ibm.is.sappack.dsstages.idoc.IDocRuntimeException;

public class DSLogSegmentValidationHandler implements SegmentValidationHandler {

	static String copyright() {
		return com.ibm.is.sappack.dsstages.idocload.Copyright.IBM_COPYRIGHT_SHORT;
	}

	final static String CLASSNAME = DSLogSegmentValidationHandler.class.getName();

	protected Logger logger;

	boolean treatAsErrors = false;

	public DSLogSegmentValidationHandler() {
		this.logger = StageLogger.getLogger();
		treatAsErrors = RuntimeConfiguration.getRuntimeConfiguration().treatIDocLoadValidationWarningsAsErrors();
	}

	@Override
	public void handleSegmentValidationMessage(SegmentValidationResult svr) {
		final String METHODNAME = "handleSegmentValidationMessage"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);
		String msgId = "CC_IDOC_LOAD_ValidationError";   //$NON-NLS-1$
		Object paramArr[] = new Object[] { SegmentValidationResult.typeToString(svr.getType()), svr.getMessage() };
		if (treatAsErrors) {
			this.logger.log(Level.SEVERE, msgId, paramArr);
			throw new IDocRuntimeException(CCFResource.getCCFMessage(msgId, paramArr));
		}
		
		this.logger.log(Level.WARNING, msgId, paramArr);
		
		this.logger.exiting(CLASSNAME, METHODNAME);
	}

}
