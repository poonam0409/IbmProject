//	---------------------------------------------------------------------------  
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
// Module Name : com.ibm.is.sappack.gen.tools.sap.dialog
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.tools.sap.dialog;


import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.logging.Level;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.tools.sap.activator.Activator;
import com.ibm.is.sappack.gen.tools.sap.constants.Constants;
import com.ibm.is.sappack.gen.tools.sap.utilities.ExceptionHandler;
import com.sap.conn.jco.JCo;


public class AboutJCoDialog {

	static String copyright() {
		return com.ibm.is.sappack.gen.tools.sap.dialog.Copyright.IBM_COPYRIGHT_SHORT;
	}

	public static final void displayJCoInformation() {
		StringBuffer message = new StringBuffer();
		try {
			String jcoVersion = JCo.getVersion();
			String jcoMiddlewareName = JCo.getMiddlewareProperty(Constants.PROPERTY_JCO_MIDDLEWARE_NAME);
			String jcoSharedLibraryPath = JCo.getMiddlewareProperty(Constants.PROPERTY_JCO_MIDDLEWARE_NATIVE_LAYER_PATH);
			String jcoMiddlewareNativeVersion =
			      JCo.getMiddlewareProperty(Constants.PROPERTY_JCO_MIDDLEWARE_NATIVE_LAYER_VERSION);

			String sapPackVersion = com.ibm.is.sappack.gen.common.Constants.CLIENT_SERVER_VERSION;

			message.append(MessageFormat.format(Messages.AboutJCoDialog_12, sapPackVersion));

			message.append(Messages.AboutJCoDialog_0);
			message.append(MessageFormat.format(Messages.AboutJCoDialog_1, System
			      .getProperty(Constants.PROPERTY_JAVA_VERSION)));
			message.append(MessageFormat.format(Messages.AboutJCoDialog_2, System
			      .getProperty(Constants.PROPERTY_JAVA_RUNTIME_VERSION)));
			message.append(MessageFormat.format(Messages.AboutJCoDialog_3, System
			      .getProperty(Constants.PROPERTY_JAVA_VM_VERSION)));
			message.append(MessageFormat.format(Messages.AboutJCoDialog_4, System
			      .getProperty(Constants.PROPERTY_JAVA_VENDOR)));

			message.append(Messages.AboutJCoDialog_5);
			message.append(MessageFormat.format(Messages.AboutJCoDialog_6, jcoVersion));

			message.append(Messages.AboutJCoDialog_7);
			message.append(MessageFormat.format(Messages.AboutJCoDialog_8, jcoMiddlewareName));
			message.append(MessageFormat.format(Messages.AboutJCoDialog_9, jcoMiddlewareNativeVersion));
			message.append(MessageFormat.format(Messages.AboutJCoDialog_10, jcoSharedLibraryPath));

			MessageDialog.openInformation(Display.getCurrent().getActiveShell(), Messages.AboutJCoDialog_11, message
			      .toString());

		}
		catch (Error error) {
			if (error instanceof NoClassDefFoundError) {
				MessageDialog.openError(Display.getCurrent().getActiveShell(), Messages.AboutJCoDialog_11,
				      Messages.AboutJCoDialog_14);
				Activator.getLogger().log(Level.CONFIG, Messages.AboutJCoDialog_13, error);
				return;
			}

			StringWriter stringWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(stringWriter);
			error.printStackTrace(printWriter);
			message.append(stringWriter.getBuffer());
			MessageDialog.openError(Display.getCurrent().getActiveShell(), Messages.AboutJCoDialog_11, message.toString());
			throw error;
		}
	}

	public static final String getJCoErrorMessage() {
		String retVal = null;

		try {
			//System.loadLibrary(Constants.SAP_JCO_NATIVE_LIBRARY);
			JCo.getVersion();
		}
		catch (NoClassDefFoundError noClassDefFoundError) {
			retVal = Messages.AboutJCoDialog_14;
			Activator.getLogger().log(Level.CONFIG, retVal, noClassDefFoundError);
		}
		catch (UnsatisfiedLinkError unsatisfiedLinkError) {
			retVal = Messages.AboutJCoDialog_15;
			Activator.getLogger().log(Level.CONFIG, retVal, unsatisfiedLinkError);
		}
		catch (Exception exception) {
			ExceptionHandler.handleException(exception, Display.getCurrent().getActiveShell());
			retVal = Messages.AboutJCoDialog_14;
		}

		return retVal;
	}

	/*
	public static String locateJCoJAR() throws ClassNotFoundException {
		final URL location;
		final String classLocation = JCo.class.getName().replace('.', '/') + ".class"; //$NON-NLS-1$
		final ClassLoader loader = Activator.class.getClassLoader(); // jcoBundle.getClass().getClassLoader();
		String retval = ""; //$NON-NLS-1$

		if (loader == null) {
			location = ClassLoader.getSystemResource(classLocation);
		}
		else {
			location = loader.getResource(classLocation);
		}
		if (location != null) {
			URL newLocation;
			try {
				newLocation = FileLocator.resolve(location);

				Pattern p = Pattern.compile("^.*:(.*)!.*$"); //$NON-NLS-1$
				Matcher m = p.matcher(newLocation.toString());
				if (m.find()) {
					retval = m.group(1);
				} else {
					String msg = MessageFormat.format(Messages.AboutJCoDialog_22, location);

					Activator.getLogger().log(Level.CONFIG, msg);
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		else {
			String msg = MessageFormat.format(Messages.AboutJCoDialog_23, JCo.class.getName());
			Activator.getLogger().config(msg);
		}
		return retval;
	}
	*/
}
