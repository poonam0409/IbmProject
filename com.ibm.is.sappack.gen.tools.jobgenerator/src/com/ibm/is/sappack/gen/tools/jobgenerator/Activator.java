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
// Module Name : com.ibm.is.sappack.gen.tools.jobgenerator
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.tools.jobgenerator;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.common.ui.helper.EclipseLogHandler;
import com.ibm.is.sappack.gen.common.ui.helper.LoggingHelper;
import com.ibm.is.sappack.gen.tools.sap.constants.Constants;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.ibm.is.sappack.gen.tools.jobgenerator"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;

	public static final String WIZARD_BANNER_IMAGE_ID = "wizardBannerImage"; //$NON-NLS-1$
	
	static String copyright() {
		return com.ibm.is.sappack.gen.tools.jobgenerator.Copyright.IBM_COPYRIGHT_SHORT;
	}

	/**
	 * The constructor
	 */
	public Activator() {
		super();
	}

	protected void initializeImageRegistry(ImageRegistry registry) {
		super.initializeImageRegistry(registry);
		Bundle bundle = Platform.getBundle(PLUGIN_ID);
		ImageDescriptor wizardBannerImage =
		      ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path("icons/Rapid_generator.png"), null)); //$NON-NLS-1$
		registry.put(WIZARD_BANNER_IMAGE_ID, wizardBannerImage);
		ImageDescriptor validateFailureIcon =
		      ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path(Constants.ICON_VALIDATE_FAILURE), null)); 
		registry.put(Constants.ICON_ID_VALIDATE_FAILURE, validateFailureIcon);
		ImageDescriptor validateSuccessIcon =
		      ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path(Constants.ICON_VALIDATE_SUCCESS), null)); 
		registry.put(Constants.ICON_ID_VALIDATE_SUCCESS, validateSuccessIcon);
		ImageDescriptor validateWarningIcon =
		      ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path(Constants.ICON_VALIDATE_WARNING), null)); 
		registry.put(Constants.ICON_ID_VALIDATE_WARNING, validateWarningIcon);

		//		ImageDescriptor applicationImage = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path("icons/MDMApplication.gif"), null)); //$NON-NLS-1$
		// registry.put(APPLICATION_IMAGE_ID, applicationImage);
		//		ImageDescriptor moduleImage = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path("icons/MDMModule.gif"), null)); //$NON-NLS-1$
		// registry.put(MODULE_IMAGE_ID, moduleImage);
		//		ImageDescriptor serviceImage = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path("icons/service.png"), null)); //$NON-NLS-1$
		// registry.put(SERVICE_IMAGE_ID, serviceImage);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * 
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

	/**
	 * Returns an image descriptor for the image file at the given plug-in relative path
	 * 
	 * @param path
	 *           the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	public static Logger getLogger() {
		Logger pluginLogger = Logger.getLogger(LoggingHelper.COM_IBM_IS_SAPPACK_GENERATOR_ID);

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
		getLogger().log(Level.SEVERE, Messages.JobGenActivator_0, e);
	}
}
