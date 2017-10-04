//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2009, 2014                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.iis.sappack.gen.common.ui.wizards
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.iis.sappack.gen.common.ui.wizards;


import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;


public class NextActionWizardDialog extends WizardDialog {
	private static final String VALIDATION_RESULT_PAGE_ID = "sapvalidationresultpage"; //$NON-NLS-1$

	private Point  _PrevValidationPageShellSize;


  	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	


	public NextActionWizardDialog(Shell parentShell, IWizard newWizard) {
		super(parentShell, newWizard);	
	}
	
	@Override
	protected void nextPressed() {
		Point   prevPageShellSize;
		boolean advancePage = true;
		boolean updateSize  = false;
		IWizardPage page = this.getCurrentPage();

		if (page instanceof INextActionWizardPage) {
			advancePage = ((INextActionWizardPage) page).nextPressed();
		}

		if (advancePage) {
			// for IIS Connection page --> save current shell size
			prevPageShellSize = getShell().getSize();

			super.nextPressed();

			if (getSelectedPage().toString().equalsIgnoreCase(VALIDATION_RESULT_PAGE_ID)) {
				_PrevValidationPageShellSize = prevPageShellSize;
				updateSize();
			}
		}
		
		// update the dialog to required size of the new page
		if (updateSize) {
		}
	}

	@Override
	protected void backPressed() {
		WizardPage   prevWizardPage = (WizardPage) getSelectedPage();

		super.backPressed();

		// for IIS Connection page --> set previous shell size
		if (prevWizardPage.toString().equalsIgnoreCase(VALIDATION_RESULT_PAGE_ID)) {
			getShell().setSize(_PrevValidationPageShellSize); 
//			updateSize();
		}
	}

}
