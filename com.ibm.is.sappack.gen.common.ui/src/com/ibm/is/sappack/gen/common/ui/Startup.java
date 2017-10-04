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
// Module Name : com.ibm.is.sappack.gen.common.ui
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.common.ui;

import org.eclipse.core.runtime.IProduct;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PreferencesUtil;

public class Startup implements IStartup {
	
	static String copyright()
	{ return com.ibm.is.sappack.gen.common.ui.Copyright.IBM_COPYRIGHT_SHORT; }


	public Startup() {
		super();
	}

	public void earlyStartup() {
		IProduct product = Platform.getProduct();

		// simply return if we run a product that does not qualify to require initializing the SAP JCo middleware
		if (null != product && (! product.getId().equals("com.ibm.is.sappack.gen.branding.product")) && (! product.getId().equals("com.ibm.is.sappack.cw.branding.product"))) { //$NON-NLS-1$ //$NON-NLS-2$
			return;
		}
		
		checkJCoConfigurationWithUI();
	}


	public static boolean checkJCoConfiguration() {
		boolean openPrefsPane = false;
		com.ibm.is.sappack.gen.common.ui.Activator activator = com.ibm.is.sappack.gen.common.ui.Activator.getDefault();
		String[] fromEclipseConfig = activator.getJCoConfigFromEclipseINI();

		if (activator.getUseInstallerConfig()) {
			String lib_path = System.getProperty("java.library.path"); //$NON-NLS-1$
			String jco_reference = System.getProperty("com.ibm.is.3rdparty.sap.jco.jar.path"); //$NON-NLS-1$

			if (lib_path == null || lib_path.length() == 0) {
				openPrefsPane = true;
			}
			if (jco_reference == null || jco_reference.length() == 0) {
				openPrefsPane = true;
			}
		}
		else if (null == fromEclipseConfig[0] || fromEclipseConfig[0].equals("NONE SET")) { //$NON-NLS-1$
			openPrefsPane = true;
		}
		else if (null == fromEclipseConfig[1] || fromEclipseConfig[1].equals("NONE SET")) { //$NON-NLS-1$
			openPrefsPane = true;
		}
		return !openPrefsPane;
	}

	public static boolean checkJCoConfigurationWithUI() {
		boolean configured = checkJCoConfiguration();
		if (!configured) {
			final IWorkbench workbench = PlatformUI.getWorkbench();
			workbench.getDisplay().asyncExec(new Runnable() {
				public void run() {
					IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
					boolean result =
					      MessageDialog
					            .openQuestion(null, Messages.Startup_0,
					                  Messages.Startup_1);

					if (result && window != null) {
						PreferenceDialog pref =
						      PreferencesUtil.createPreferenceDialogOn(window.getShell(),
						            "com.ibm.is.sappack.gen.common.ui.prefpage", null, null); //$NON-NLS-1$
						if (null != pref)
							pref.open();
					}
				}
			});
		}
		return configured;
	}
}
