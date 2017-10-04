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
// Module Name : com.ibm.is.sappack.dsstages.idoc.listener.util
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.dsstages.idoc.listener.util;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * IDocServerMessages
 * 
 * Externalized String for the IDoc Listener
 */
public class IDocServerMessages {
	
	static String copyright() {
		return com.ibm.is.sappack.dsstages.idoc.listener.util.Copyright.IBM_COPYRIGHT_SHORT;
	}

	private static final String BUNDLE_NAME = "com.ibm.is.sappack.dsstages.idoc.listener.util.idocservermessages"; //$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

	/* externalized strings */
	public static final String StartedStatus = getString("StartedStatus"); //$NON-NLS-1$
	public static final String StoppedStatus = getString("StoppedStatus"); //$NON-NLS-1$
	public static final String DSSAPConnection = getString("DSSAPConnection"); //$NON-NLS-1$
	public static final String PropertiesEmpty = getString("PropertiesEmpty"); //$NON-NLS-1$
	public static final String PackHomeNotSet = getString("PackHomeNotSet"); //$NON-NLS-1$
	public static final String FunctionModuleNotSupported = getString("FunctionModuleNotSupported"); //$NON-NLS-1$
	public static final String IDocReceived = getString("IDocReceived"); //$NON-NLS-1$
	public static final String IDocTypeNotRegistered = getString("IDocTypeNotRegistered"); //$NON-NLS-1$
	public static final String IDocIncoming = getString("IDocIncoming");//$NON-NLS-1$
	public static final String FailedToSendStatusIDocs = getString("FailedToSendStatusIDocs"); //$NON-NLS-1$
	public static final String SendingStatusIDocs = getString("SendingStatusIDocs"); //$NON-NLS-1$
	public static final String UVPASSTOKENNOTFOUND = getString("UVPASSTOKENNOTFOUND"); //$NON-NLS-1$
	public static final String IDocCounterFileReadError = getString("IDocCounterFileReadError"); //$NON-NLS-1$
	public static final String IDocCounterFileWriteError = getString("IDocCounterFileWriteError"); //$NON-NLS-1$
	public static final String IDocCounterFileNotFound = getString("IDocCounterFileNotFound"); //$NON-NLS-1$
	public static final String IDocCounterValue = getString("IDocCounterValue"); //$NON-NLS-1$
	public static final String RunDSJob = getString("RunDSJob"); //$NON-NLS-1$
	public static final String RunDSJobWithFile = getString("RunDSJobWithFile"); //$NON-NLS-1$
	public static final String DSJOBOutput = getString("DSJOBOutput"); //$NON-NLS-1$
	public static final String IDocConfigurationFileReadError = getString("IDocConfigurationFileReadError"); //$NON-NLS-1$
	public static final String ProcessingIncomingIDocs = getString("ProcessingIncomingIDocs");//$NON-NLS-1$
	public static final String RollbackIDocFile = getString("RollbackIDocFile");//$NON-NLS-1$
	public static final String ServerStateChange = getString("ServerStateChange");//$NON-NLS-1$
	public static final String IDocTypesConfigFileReadError = getString("IDocTypesConfigFileReadError");//$NON-NLS-1$
	public static final String IDocTypesConfigFileFormatError = getString("IDocTypesConfigFileFormatError");//$NON-NLS-1$
	public static final String TIDUnknownTransation = getString("TIDUnknownTransation"); //$NON-NLS-1$
	public static final String NumberOfIDocServerThreads = getString("NumberOfIDocServerThreads"); //$NON-NLS-1$
	public static final String VersionInfo = getString("VersionInfo"); //$NON-NLS-1$
	public static final String ProcessInterrupted = getString("ProcessInterrupted"); //$NON-NLS-1$
	public static final String ProcessInterrupted1 = getString("ProcessInterrupted1"); //$NON-NLS-1$
	public static final String ExceptionRaised = getString("ExceptionRaised"); //$NON-NLS-1$
	public static final String ExceptionRaisedinDataPacket = getString("ExceptionRaisedinDataPacket"); //$NON-NLS-1$
	public static final String ExceptionRaisedinRequestInfoPacket = getString("ExceptionRaisedinRequestInfoPacket"); //$NON-NLS-1$
	
	
	
	private static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
	
}
