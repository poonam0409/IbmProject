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

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PreferencesUtil;

public class Startup implements IStartup {
	
	static String copyright() {
		return com.ibm.is.sappack.cw.branding.Copyright.IBM_COPYRIGHT_SHORT;
	}

	public Startup() {
		super();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IStartup#earlyStartup()
	 */
	public void earlyStartup() {
        boolean openPrefsPane = false;
        com.ibm.is.sappack.gen.common.ui.Activator activator =
        	com.ibm.is.sappack.gen.common.ui.Activator.getDefault();
        String[] fromEclipseConfig = activator.getJCoConfigFromEclipseINI();
        
        if (activator.getUseInstallerConfig()) {
        	String lib_path = System.getProperty("java.library.path");
        	String jco_reference = System.getProperty("com.ibm.is.3rdparty.sap.jco.jar.path");
        	
        	if (lib_path == null || lib_path.length() == 0) {
        		openPrefsPane = true;
        	}
        	if (jco_reference == null || jco_reference.length() == 0) {
        		openPrefsPane = true;
        	}
        } else if (null == fromEclipseConfig[0] || fromEclipseConfig[0].equals("NONE SET")) {
        	openPrefsPane = true;
        } else if (null == fromEclipseConfig[1] || fromEclipseConfig[1].equals("NONE SET")) {
        	openPrefsPane = true;
        }
        
        if (openPrefsPane) {
        	final IWorkbench workbench = PlatformUI.getWorkbench();
    	    workbench.getDisplay().asyncExec(new Runnable() {
    		      public void run() {
    		         IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
    		         boolean result = MessageDialog.openQuestion(null, "Configure SAP JCo Settings", "It looks like the SAP JCo system has not been initialized yet. Do you want to configure it right now?");
    		         
		             if (result && window != null) {
		            	PreferenceDialog pref = PreferencesUtil.createPreferenceDialogOn(window.getShell(), "com.ibm.is.sappack.gen.common.ui.prefpage", null, null);
		            	if (null != pref)
		            		pref.open(); 
		             }
    		      }
    		});        	
        }
	}
}
