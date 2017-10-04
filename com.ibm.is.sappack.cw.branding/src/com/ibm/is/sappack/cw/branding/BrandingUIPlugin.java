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
// Module Name : com.ibm.is.sappack.cw.branding
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.cw.branding;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class BrandingUIPlugin extends AbstractUIPlugin {

	static String copyright() {
		return com.ibm.is.sappack.cw.branding.Copyright.IBM_COPYRIGHT_SHORT;
	}

	/**
	 * The plugin id.
	 */
	public static final String PLUGIN_ID = "com.ibm.is.sappack.gen.branding";

	/**
	 * The plugin.
	 */
	private static BrandingUIPlugin plugin;

	/**
	 * Returns the shared instance.
	 * 
	 * @return the shared instance.
	 */
	public static BrandingUIPlugin getDefault() {
		return BrandingUIPlugin.plugin;
	}

	/**
	 * Returns the workspace instance.
	 */
	public static IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}

	/**
	 * 
	 */
	public BrandingUIPlugin() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Initializes a preference store with default preference values for this plug-in.
	 * 
	 * @param store
	 *           the preference store to fill
	 * @deprecated per the API docs. Preferences will be initialized by the PreferencesInitializer
	 */
	protected void initializeDefaultPreferences(IPreferenceStore store) {
		// store.setDefault(PreferenceConstants.P_USE_INSTALLER_CONFIG, true);
		// store.setDefault(PreferenceConstants.P_JCO_JAR_LOCATION,
		// "<path value referencing the SAP JCo Java archive file>");
		// store.setDefault(PreferenceConstants.P_JCO_NATIVE_LIB_DIR,
		// "<path value referencing a directory hosting the SAP JCo DLL or shared library>");
	}

}
