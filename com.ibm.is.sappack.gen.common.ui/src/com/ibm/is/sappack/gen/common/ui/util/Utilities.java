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
// Module Name : com.ibm.is.sappack.gen.common.ui.util
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.common.ui.util;


import java.text.DateFormat;
import java.util.Date;
import java.util.logging.Level;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import com.ibm.iis.sappack.gen.common.ui.connections.SapSystem;
import com.ibm.iis.sappack.gen.common.ui.jco.RfcDestinationDataProvider;
import com.ibm.iis.sappack.gen.common.ui.util.ExceptionHandler;
import com.ibm.is.sappack.gen.common.ui.Activator;
import com.ibm.is.sappack.gen.common.ui.Messages;
import com.sap.conn.jco.JCo;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;


public class Utilities {

	static String copyright() {
		return Copyright.IBM_COPYRIGHT_SHORT;
	}

	public static boolean isUnicodeSystem(SapSystem sapSystem) throws JCoException {
		JCoDestination destination = RfcDestinationDataProvider.getDestination(sapSystem);
		boolean isUnicodeSapSystem = Boolean.valueOf(destination.getAttributes().getPartnerCharset().equals("UTF16")); //$NON-NLS-1$
		return isUnicodeSapSystem;
	}

	/*
		public static final boolean checkJCoAvailabilityNonGUI() {
			try {
				//System.loadLibrary(Constants.SAP_JCO_NATIVE_LIBRARY);
				JCo.getVersion();
				return true;
			}
			catch (NoClassDefFoundError noClassDefFoundError) {
				Activator.getLogger().log(Level.CONFIG, Messages.AboutJCoDialog_16, noClassDefFoundError);
				return false;
			}
			catch (UnsatisfiedLinkError unsatisfiedLinkError) {
				Activator.getLogger().log(Level.CONFIG, Messages.AboutJCoDialog_17, unsatisfiedLinkError);
				Activator.getLogger().config(Messages.AboutJCoDialog_18 + System.getProperty("java.library.path", "UNDEFINED")); //$NON-NLS-1$ //$NON-NLS-2$
				return false;
			}
			catch (Exception exception) {
				ExceptionHandler.handleException(exception, Display.getCurrent().getActiveShell());
				return false;
			}
		}
	*/
		public static final boolean checkJCoAvailabilityWithDialog() {
			try {
				//System.loadLibrary(Constants.SAP_JCO_NATIVE_LIBRARY);
				JCo.getVersion();
				return true;
			}
			catch (NoClassDefFoundError noClassDefFoundError) {
				MessageDialog.openError(Display.getCurrent().getActiveShell(), Messages.AboutJCoDialog_11,
				      Messages.AboutJCoDialog_14);
				Activator.getLogger().log(Level.CONFIG, Messages.AboutJCoDialog_19, noClassDefFoundError);
				return false;
			}
			catch (UnsatisfiedLinkError unsatisfiedLinkError) {
				MessageDialog.openError(Display.getCurrent().getActiveShell(), Messages.AboutJCoDialog_11,
				      Messages.AboutJCoDialog_15);
				Activator.getLogger().log(Level.CONFIG, Messages.AboutJCoDialog_20, unsatisfiedLinkError);
				Activator.getLogger().config(Messages.AboutJCoDialog_21 + System.getProperty("java.library.path", "UNDEFINED")); //$NON-NLS-1$ //$NON-NLS-2$
				return false;
			}
			catch (Exception exception) {
				ExceptionHandler.handleException(exception, Display.getCurrent().getActiveShell());
				return false;
			}
		}

		
	public static final String getDateString(Date d) {
		return DateFormat.getDateTimeInstance().format(d);
	}
}
