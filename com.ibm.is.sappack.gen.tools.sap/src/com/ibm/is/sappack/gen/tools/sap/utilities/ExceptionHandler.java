//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2011, 2013                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.is.sappack.gen.tools.sap.utilities
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.tools.sap.utilities;

import java.text.MessageFormat;
import java.util.logging.Level;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import com.ibm.iis.sappack.gen.common.ui.connections.SapSystem;
import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.tools.sap.activator.Activator;
import com.sap.conn.jco.JCoException;

public class ExceptionHandler {
	
	static String copyright() { 
		return com.ibm.is.sappack.gen.tools.sap.utilities.Copyright.IBM_COPYRIGHT_SHORT; 
	}

	public static final void handleJcoException(JCoException jcoException, SapSystem sapSystem, Shell shell) {

		// logon failure
		if (jcoException.getGroup() == JCoException.JCO_ERROR_LOGON_FAILURE) {
			MessageDialog.openError(shell, Messages.ExceptionHandler_0, MessageFormat.format(Messages.ExceptionHandler_1, new Object[] { sapSystem.getName(), jcoException.getMessage() }));
			sapSystem.resetPassword();
			return;
		}
		
		// password empty
		if (jcoException.getGroup() == JCoException.JCO_ERROR_CONFIGURATION) {
			MessageDialog.openError(shell, Messages.ExceptionHandler_0, MessageFormat.format(Messages.ExceptionHandler_1, new Object[] { sapSystem.getName(), jcoException.getMessage() }));
//			sapSystem.setPassword(null);
			return;
		}

		// Other JCo error
		MessageDialog.openError(shell, MessageFormat.format(Messages.ExceptionHandler_1, new Object[] { sapSystem.getName(), jcoException.getMessage() }), MessageFormat.format(Messages.ExceptionHandler_2, new Object[] { sapSystem.getName(), jcoException.getMessage() }));
//		sapSystem.setPassword(null);
		return;
	}

	public static final void handleException(Exception exception, Shell shell) {
		// Other error
		
//		StringWriter stringWriter = new StringWriter();
//		PrintWriter printWriter = new PrintWriter(stringWriter);
//		exception.printStackTrace(printWriter);
		String msg = MessageFormat.format(Messages.ExceptionHandler_4, exception.getClass().getName());
		
		Activator.getLogger().log(Level.SEVERE, msg , exception);

//		MessageDialog.openError(shell, Messages.ExceptionHandler_3, MessageFormat.format(Messages.ExceptionHandler_4, new Object[] { stringWriter.toString() }));
		MessageDialog.openError(shell, Messages.ExceptionHandler_3, MessageFormat.format(Messages.ExceptionHandler_4, new Object[] { exception.getClass().getName() }));
		
		
		
	}
}
