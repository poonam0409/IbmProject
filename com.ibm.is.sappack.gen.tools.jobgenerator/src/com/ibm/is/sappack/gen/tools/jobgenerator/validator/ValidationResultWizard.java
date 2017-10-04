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

import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;

import com.ibm.is.sappack.gen.common.ui.Messages;

public class ValidationResultWizard extends Wizard{

	static String copyright() {
		return com.ibm.is.sappack.gen.tools.jobgenerator.validator.Copyright.IBM_COPYRIGHT_SHORT;
	}

	private ValidationResultPage validationResultPage;
	private List<IValidationResult> validationResults;

	/**
	 * ValidationResultWizard
	 * @param validationResults
	 */
	public ValidationResultWizard(List<IValidationResult> validationResults) {
		super();
		this.validationResults = validationResults;
		this.setWindowTitle(Messages.SapSystemWizardPage_0);
		setNeedsProgressMonitor(false);
	}
	
	public void init(IWorkbench arg0, IStructuredSelection arg1) {
		//
	}
	
	@Override
	public boolean performFinish() {
		
		return true;
	}

	@Override
	public boolean performCancel() {
		
		return true;
	}

	public void addPages() {
		this.validationResultPage = new ValidationResultPage(this.validationResults);
		addPage(this.validationResultPage);
	}
	
	
}
