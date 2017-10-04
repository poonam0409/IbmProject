package com.ibm.iis.sappack.gen.common.ui.util;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.common.ui.WidgetIDConstants;
import com.ibm.is.sappack.gen.common.ui.WidgetIDUtils;

public class MissingPasswordDialog extends Dialog {
  	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	

	private String user;
	private String password;
	private String message;
	private Text textPassword;

	public MissingPasswordDialog(Shell parentShell, String message, String user) {
		super(parentShell);
		this.message = message;
		this.user = user;
		
	}

	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);
		// create message

		composite.setLayout(new GridLayout(2, false));

		Label label = new Label(composite, SWT.WRAP);
		GridData gd = new GridData(SWT.FILL, SWT.TOP, true, true);
		gd.horizontalSpan = 2;
		label.setLayoutData(gd);
		label.setText(message);

		/*
		GridData data = new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL | GridData.HORIZONTAL_ALIGN_FILL
		                                                      | GridData.VERTICAL_ALIGN_CENTER);
		data.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);/
		label.setLayoutData(data);
		*/
		label.setFont(parent.getFont());
		
		Label l = new Label(composite, SWT.NONE);
		l.setText(Messages.MissingPasswordDialog_0);
		
		Text textUser = new Text(composite, SWT.SINGLE | SWT.BORDER);
		textUser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		textUser.setText(user);
		textUser.setEnabled(false);
		
		l = new Label(composite, SWT.NONE);
		l.setText(Messages.MissingPasswordDialog_1);

		this.textPassword = new Text(composite, SWT.SINGLE | SWT.PASSWORD | SWT.BORDER);
		this.textPassword.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));

		WidgetIDUtils.assignID(this.textPassword, WidgetIDConstants.COMMON_SAPPWTEXT);

		applyDialogFont(composite);
		return composite;
	}

	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.OK_ID) {
			this.password = this.textPassword.getText();
		} else {
			this.password = null;
		}
		super.buttonPressed(buttonId);
	}

	public String getPassword() {
		return this.password;
	}

}
