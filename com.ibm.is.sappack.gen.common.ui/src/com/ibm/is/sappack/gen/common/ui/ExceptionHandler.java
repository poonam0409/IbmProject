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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

public class ExceptionHandler {

	static String copyright() {
		return com.ibm.is.sappack.gen.common.ui.Copyright.IBM_COPYRIGHT_SHORT;
	}

	public static final void handleThrowable(Throwable throwable, Shell shell) {
		// Other error
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		throwable.printStackTrace(printWriter);

		MessageDialog.openError(shell, Messages.CommonUIExceptionHandler_4, MessageFormat.format(
		      Messages.CommonUIExceptionHandler_5, new Object[] { stringWriter.toString() }));
	}
}
