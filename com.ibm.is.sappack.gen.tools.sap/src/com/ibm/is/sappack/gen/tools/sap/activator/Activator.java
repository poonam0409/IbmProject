//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2011, 2014                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.is.sappack.gen.tools.sap.activator
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.tools.sap.activator;


import java.util.logging.Level;
import java.util.logging.Logger;

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

	private static Activator plugin;

	static String copyright() {
		return com.ibm.is.sappack.gen.tools.sap.activator.Copyright.IBM_COPYRIGHT_SHORT;
	}

	/** id of the plug-in */
	public static final String PLUGIN_ID = "com.ibm.is.sappack.gen.tools.sap"; //$NON-NLS-1$

	public Activator() {
		// Try to start the common ui bundle by accessing it's activator class.
		// The common UI bundle currently initializes the path to the JCo JAR and DLL
		// but does not run (state == active) until it will be accessed for the first
		// time. Instead of calling a class from the bundle it would as well be
		// possible to force a bundle start by adding the bundle ID to the config.ini
		// file as part of the osgi.bundle directive.
		com.ibm.is.sappack.gen.common.ui.Activator.getDefault();
	}

	public static Activator getDefault() {
		return plugin;
	}

	@Override
	protected void initializeImageRegistry(ImageRegistry registry) {
		super.initializeImageRegistry(registry);
		Bundle bundle = Platform.getBundle(Activator.PLUGIN_ID);
		ImageDescriptor tableIcon = ImageDescriptor.createFromURL(bundle.getResource(Constants.ICON_TABLE));
		registry.put(Constants.ICON_ID_TABLE, tableIcon);

		ImageDescriptor mandatorySegmentDecorator =
		      ImageDescriptor.createFromURL(bundle.getResource(Constants.DECORATOR_MANDATORY_SEGMENT));
		registry.put(Constants.DECORATOR_ID_MANDATORY_SEGMENT, mandatorySegmentDecorator);

		ImageDescriptor tableFolderObjectIcon =
		      ImageDescriptor.createFromURL(bundle.getResource(Constants.ICON_TABLE_FOLDER));
		registry.put(Constants.ICON_ID_TABLE_FOLDER, tableFolderObjectIcon);

		ImageDescriptor folderObjectIcon = ImageDescriptor.createFromURL(bundle.getResource(Constants.ICON_FOLDER));
		registry.put(Constants.ICON_ID_FOLDER, folderObjectIcon);

		ImageDescriptor checkboxCheckedIcon =
		      ImageDescriptor.createFromURL(bundle.getResource(Constants.ICON_CHECKBOX_CHECKED));
		registry.put(Constants.ICON_ID_CHECKBOX_CHECKED, checkboxCheckedIcon);

		ImageDescriptor checkboxCheckedIconDisabled =
		      ImageDescriptor.createFromURL(bundle.getResource(Constants.ICON_CHECKBOX_CHECKED_DISABLED));
		registry.put(Constants.ICON_ID_CHECKBOX_CHECKED_DISABLED, checkboxCheckedIconDisabled);

		ImageDescriptor checkboxUncheckedIcon =
		      ImageDescriptor.createFromURL(bundle.getResource(Constants.ICON_CHECKBOX_UNCHECKED));
		registry.put(Constants.ICON_ID_CHECKBOX_UNCHECKED, checkboxUncheckedIcon);

		ImageDescriptor checkboxUncheckedIconDisabled =
		      ImageDescriptor.createFromURL(bundle.getResource(Constants.ICON_CHECKBOX_UNCHECKED_DISABLED));
		registry.put(Constants.ICON_ID_CHECKBOX_UNCHECKED_DISABLED, checkboxUncheckedIconDisabled);

		ImageDescriptor informationMessageIcon =
		      ImageDescriptor.createFromURL(bundle.getResource(Constants.ICON_MESSAGE_INFORMATION));
		registry.put(Constants.ICON_ID_MESSAGE_INFORMATION, informationMessageIcon);

		ImageDescriptor warningMessageIcon =
		      ImageDescriptor.createFromURL(bundle.getResource(Constants.ICON_MESSAGE_WARNING));
		registry.put(Constants.ICON_ID_MESSAGE_WARNING, warningMessageIcon);

		ImageDescriptor errorMessageIcon =
		      ImageDescriptor.createFromURL(bundle.getResource(Constants.ICON_MESSAGE_ERROR));
		registry.put(Constants.ICON_ID_MESSAGE_ERROR, errorMessageIcon);

		ImageDescriptor ldmPackageIcon = ImageDescriptor.createFromURL(bundle.getResource(Constants.ICON_LDM_PACKAGE));
		registry.put(Constants.ICON_ID_LDM_PACKAGE, ldmPackageIcon);

		ImageDescriptor rmWizardBanner = ImageDescriptor.createFromURL(bundle.getResource(Constants.ICON_RM_WIZARD_BANNER_IMAGE_ID));
		registry.put(Constants.RM_WIZARD_BANNER_IMAGE_ID, rmWizardBanner);
		
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	public static Logger getLogger() {
		Logger pluginLogger = Logger.getLogger(LoggingHelper.COM_IBM_IS_SAPPACK_MODELER_ID);

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
		getLogger().log(Level.SEVERE, Messages.Activator_RMActivator_0, e);
	}
}
