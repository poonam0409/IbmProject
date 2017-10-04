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
// Module Name : com.ibm.is.sappack.gen.tools.jobgenerator.wizard
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.tools.jobgenerator.abapProgramUploadWizard;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

import com.ibm.iis.sappack.gen.common.ui.wizards.PersistentWizardPageBase;

public class ABAPProgramUploadWizardDialog extends WizardDialog {

	// default constructor
	public ABAPProgramUploadWizardDialog(Shell parentShell, ABAPProgramUploadWizard wizard) {
		super(parentShell, wizard);
	}

	// static inclusion of copyright statement
	static String copyright() {
		return com.ibm.is.sappack.gen.tools.jobgenerator.abapProgramUploadWizard.Copyright.IBM_COPYRIGHT_SHORT;
	}
	
	@Override
	protected void finishPressed() {
		super.finishPressed();
	}

	@Override
	protected void nextPressed() {
		IWizardPage currentPage = getCurrentPage();
		
		if (currentPage instanceof PersistentWizardPageBase) {
			((PersistentWizardPageBase)currentPage).nextPressed();
		}
		if (currentPage instanceof ABAPProgramUploadPage) {
			((ABAPProgramUploadWizard) this.getWizard()).setSummary();
		}
		super.nextPressed();
	}

}
