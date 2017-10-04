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
// Module Name : com.ibm.is.sappack.gen.tools.jobgenerator.validator
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.tools.jobgenerator.validator;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

public class ValidationDialog extends WizardDialog {

	/**
	 * ValidationResultWizardDialog
	 * @param parentShell
	 * @param newWizard
	 */
	public ValidationDialog(Shell parentShell, IWizard newWizard) {
		super(parentShell, newWizard);
		
	}
	
	/**
	 * overwrite createButtonsForButtonBar
	 */
	protected void createButtonsForButtonBar(Composite parent) {
		
		super.createButtonsForButtonBar(parent);
		/* remove finish Button */
		Button finishButton = getButton(IDialogConstants.FINISH_ID);
		finishButton.setVisible(false);
		
		/* rename Cancel Button */
		Button cancelButton = getButton(IDialogConstants.CANCEL_ID);
		cancelButton.setText(IDialogConstants.OK_LABEL);
	}

	/**
	 * The Next button has been pressed.
	 */
	@Override
	protected void nextPressed() {
		
		
		
		super.nextPressed();
	}

	static String copyright() {
		return com.ibm.is.sappack.gen.tools.jobgenerator.validator.Copyright.IBM_COPYRIGHT_SHORT;
	}
}
