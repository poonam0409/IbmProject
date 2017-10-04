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
// Module Name : com.ibm.is.sappack.dsstages.common.ccf
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.dsstages.common.ccf;

import java.net.URL;
import java.security.CodeSource;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ascential.e2.connector.CC_ConnectorLibrary;
import com.ibm.is.sappack.dsstages.common.StageLogger;
import com.ibm.is.sappack.dsstages.common.Utilities;
import com.ibm.is.sappack.dsstages.common.VersionInfo;

public abstract class ConnectorLibrary extends CC_ConnectorLibrary {

	static String copyright() {
		return com.ibm.is.sappack.dsstages.common.ccf.Copyright.IBM_COPYRIGHT_SHORT;
	}
	
	final static private String CLASSNAME = ConnectorLibrary.class.getName();

	protected Logger logger = null;

	protected ConnectorLibrary(String traceFilePrefix) {
		StageLogger.setTraceFileNamePrefix(traceFilePrefix);
		logger = StageLogger.getLogger();
		final String METHODNAME = "<init>"; //$NON-NLS-1$
		logger.entering(CLASSNAME, METHODNAME);
		CCFLogger.configureLoggerForCCF();
		
		String versionInfo = VersionInfo.getVersionInfo();
		logger.log(Level.INFO, "CC_IDOC_VersionInfo", versionInfo);  //$NON-NLS-1$
		
		try {
			boolean jcoCheck = Utilities.checkJCOLibs();
			if (!jcoCheck) {
				// severe errors have already been logged
				return;
			}
		} catch (Throwable t) {
			CCFUtils.handleException(t);
		}
		
		// initialize the JCO RFC trace from environment 
		StageLogger.initializeRFCTrace(this.logger);

		// Write the java classpath to the log
		Class<?> cl = this.getClass();
		CodeSource cs = cl.getProtectionDomain().getCodeSource();
		if (cs != null) {
			URL url = cs.getLocation();
			String codeLocation = url.getFile();
			this.logger.log(Level.INFO, "CC_IDOC_ClassFoundInJar", new Object[]{ cl.getName(), codeLocation });
		}
		
		this.logger.exiting(CLASSNAME, METHODNAME);
	}

	@Override
	public byte[] getAPIVersion() {
		final String METHODNAME = "getAPIVersion()"; //$NON-NLS-1$
		logger.entering(CLASSNAME, METHODNAME);
		byte[] result = "CC1.0".getBytes(); //$NON-NLS-1$
		logger.exiting(CLASSNAME, METHODNAME);
		return result;
	}

//	@SuppressWarnings("unchecked")
	@Override
	public Class getConnectionClass() {
		final String METHODNAME = "getConnectionClass()"; //$NON-NLS-1$
		logger.entering(CLASSNAME, METHODNAME);
		Class result = CCFIDocConnection.class;
		logger.exiting(CLASSNAME, METHODNAME);
		return result;
	}

}
