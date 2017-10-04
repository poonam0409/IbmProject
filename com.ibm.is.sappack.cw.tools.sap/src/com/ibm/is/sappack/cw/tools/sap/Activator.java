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
// Module Name : com.ibm.is.sappack.cw.tools.sap
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.cw.tools.sap;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.common.ui.helper.EclipseLogHandler;
import com.ibm.is.sappack.gen.common.ui.helper.LoggingHelper;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.ibm.is.sappack.gen.tools.sap.cw"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;

	static String copyright()
	{ return com.ibm.is.sappack.cw.tools.sap.Copyright.IBM_COPYRIGHT_SHORT; }
	
	/**
	 * The constructor
	 */
	public Activator() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	public static Logger getLogger() {
		Logger pluginLogger = Logger.getLogger(LoggingHelper.COM_IBM_IS_SAPPACK_CW_MODELER_ID);

		if (!LoggingHelper.loggerHasTraceFileHandler(pluginLogger)) {
			com.ibm.is.sappack.gen.common.ui.Activator prefActivator =
			      com.ibm.is.sappack.gen.common.ui.Activator.getDefault();
			IPreferenceStore prefStore = prefActivator.getPreferenceStore();
			if (prefStore != null) {
				LoggingHelper.initializeSingleLogger(prefStore, pluginLogger);
			}
		}

		if (!LoggingHelper.loggerHasEclipseLogHandler(pluginLogger)) {
			EclipseLogHandler pluginHandler = new EclipseLogHandler(getDefault().getLog());
			pluginHandler.setLevel(LoggingHelper.ECLIPSE_LOG_HANDLER_DEFAULT_LEVEL);
			pluginLogger.addHandler(pluginHandler);
		}

		return pluginLogger;
	}
	
	public static void logException(Throwable e) {
		
		getLogger().log(Level.SEVERE, Messages.Activator_7, e);
		e.printStackTrace();
	}
}
