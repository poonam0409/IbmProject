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

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.osgi.service.prefs.Preferences;

import com.ibm.is.sappack.gen.common.ui.Activator;
import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.common.ui.helper.LoggingHelper;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	static String copyright() {
		return com.ibm.is.sappack.gen.common.ui.preferences.Copyright.IBM_COPYRIGHT_SHORT;
	}

	public PreferenceInitializer() {
		super();
	}

	public void initializeDefaultPreferences() {
		Preferences node = new DefaultScope().getNode(Activator.PLUGIN_ID);
		node.putBoolean(PreferenceConstants.P_USE_INSTALLER_CONFIG, false);
		node.put(PreferenceConstants.P_JCO_JAR_LOCATION, Messages.PreferenceInitializer_0);
		node.put(PreferenceConstants.P_JCO_NATIVE_LIB_DIR,
		      Messages.PreferenceInitializer_1);

		setDefaultTraceFileLoc(node);
	}

	protected void setDefaultTraceFileLoc(Preferences prefNode) {
		IPath instanceLoc = Platform.getLocation();
		String traceFilePath = null;

		if (instanceLoc != null) {
			traceFilePath = instanceLoc.toOSString();
		}
		else {
			traceFilePath = System.getProperty("user.dir"); //$NON-NLS-1$
		}

		traceFilePath = traceFilePath + System.getProperty("file.separator"); //$NON-NLS-1$
		prefNode.put(PreferenceConstants.P_TRACE_FILE_LOC, traceFilePath + LoggingHelper.DEFAULT_TRACE_FILE_NAME);
	}
}
