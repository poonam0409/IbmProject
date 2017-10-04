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
// Module Name : com.ibm.is.sappack.gen.common.ui.util
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.common.ui.util;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

public class LargeMessageDialog {

	static String copyright() {
		return Copyright.IBM_COPYRIGHT_SHORT;
	}

	static boolean simpleDialog = false;

	public static void openLargeMessageDialog(Shell shell, final String title, final String description, final String message) {
		if (simpleDialog) {
			MessageDialog.openInformation(shell, title + ": " + description, message); //$NON-NLS-1$
			return;
		}
		Wizard wiz = new Wizard() {

			@Override
			public void addPages() {
				LargeTextWizardPage p = new LargeTextWizardPage(title, description, message);
				addPage(p);
			}

			@Override
			public boolean performFinish() {
				return true;
			}
		};
		wiz.setWindowTitle(title);

		WizardDialog dialog = new WizardDialog(shell, wiz) {

			@Override
			public void updateButtons() {
				super.updateButtons();
				getButton(IDialogConstants.FINISH_ID).setText(IDialogConstants.OK_LABEL);
			}
			
		};

		dialog.open();
	}

}
