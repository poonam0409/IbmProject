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
// Module Name : com.ibm.is.sappack.gen.common.ui.preferences
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.common.ui.preferences;

/**
 * Constant definitions for plug-in preferences
 */
public class PreferenceConstants {
	
	static String copyright() {
		return com.ibm.is.sappack.gen.common.ui.preferences.Copyright.IBM_COPYRIGHT_SHORT;
	}

	public static final String P_JCO_JAR_LOCATION = "fileJCoJARLocation"; //$NON-NLS-1$
	public static final String P_USE_INSTALLER_CONFIG = "booleanUseInstallerConfig"; //$NON-NLS-1$
	public static final String P_JCO_NATIVE_LIB_DIR = "pathJCoNativeLibDir"; //$NON-NLS-1$
	public static final String P_TRACE_FILE_LOC = "traceFileLocation"; //$NON-NLS-1$
	public static final String P_TRACE_COMPONENTS = "traceComponents"; //$NON-NLS-1$
	public static final String P_TRACE_ENABLED = "traceEnabled"; //$NON-NLS-1$
}
