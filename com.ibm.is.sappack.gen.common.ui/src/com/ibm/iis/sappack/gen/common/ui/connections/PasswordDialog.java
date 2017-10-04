//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2013, 2014                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.iis.sappack.gen.common.ui.connections
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.iis.sappack.gen.common.ui.connections;


import java.text.MessageFormat;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.ibm.iis.sappack.gen.common.ui.jco.RfcDestinationDataProvider;
import com.ibm.iis.sappack.gen.common.ui.util.ExceptionHandler;
import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.common.ui.WidgetIDConstants;
import com.ibm.is.sappack.gen.common.ui.WidgetIDUtils;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;


public class PasswordDialog extends Dialog {

	private String    password;
	private SapSystem sapSystem;
	private Text      textPassword;
	

	static String copyright() { 
	   return com.ibm.iis.sappack.gen.common.ui.connections.Copyright.IBM_COPYRIGHT_SHORT; 
	}


	public PasswordDialog(Shell parentShell, SapSystem sapSystem) {
		super(parentShell);
		this.sapSystem = sapSystem;
	}

	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);
		// create message

		Label label = new Label(composite, SWT.WRAP);
		label.setText(MessageFormat.format(Messages.PasswordDialog_0,
				new Object[] { this.sapSystem.getName(),
						this.sapSystem.getUsername() }));

		GridData data = new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL | GridData.HORIZONTAL_ALIGN_FILL
		                                                      | GridData.VERTICAL_ALIGN_CENTER);
		data.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);

		label.setLayoutData(data);
		label.setFont(parent.getFont());

		this.textPassword = new Text(composite, SWT.SINGLE | SWT.PASSWORD | SWT.BORDER);
		this.textPassword.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));

		WidgetIDUtils.assignID(this.textPassword, WidgetIDConstants.COMMON_SAPPWTEXT);
		
		applyDialogFont(composite);
		return composite;
	}

	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.OK_ID) {
			this.password = this.textPassword.getText();
		} 
		else {
			this.password = null;
		}
		super.buttonPressed(buttonId);
	}

	public String getPassword() {
		return this.password;
	}

	/** 
	 * This method checks if a password is provided.<p>
	 * If no password exist in the 'sapSystem' a dialog is opened and the user can enter the password. 
	 * Then the entered password is checked against the passed SAP system.
	 *  
	 * @param shell 
	 * @param sapSystem 
	 * 
	 * @return true if the password is valid, false if the password is invalid or not set
	**/
	public static final boolean checkForPassword(Shell shell, SapSystem sapSystem) {
		if (sapSystem.getPassword() == null) {
			PasswordDialog dialog = new PasswordDialog(shell, sapSystem);
			
			int dialogResult;
			do {
				dialogResult = dialog.open();
				if (dialogResult == Window.CANCEL) {
					return false;
				}
				String password = dialog.getPassword();
				sapSystem.setPassword(password);
				try {
					JCoDestination dest = RfcDestinationDataProvider.getDestination(sapSystem);
					dest.ping();
				} catch(JCoException exc ) {
					ExceptionHandler.handleJcoException(exc, sapSystem, shell);
					sapSystem.setPassword(null);
					continue;
				}
				return true;	
			} while (true);
		}

		return true;
	}

}
