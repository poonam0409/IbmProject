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

import java.lang.reflect.InvocationTargetException;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;

import com.ibm.iis.sappack.gen.common.ui.connections.IISConnection;
import com.ibm.iis.sappack.gen.common.ui.wizards.SelectIISConnectionProjectWizardPage;
import com.ibm.iis.sappack.gen.common.ui.wizards.SelectIISConnectionWizardPage;
import com.ibm.iis.sappack.gen.tools.jobgenerator.rgconf.RGConfiguration;
import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.tools.jobgenerator.Activator;
import com.ibm.is.sappack.gen.tools.jobgenerator.wizard.helpers.JobParameterResolverPage;
import com.ibm.is.sappack.gen.tools.jobgenerator.wizard.helpers.ParameterResolver;

public class ValidationWizard extends Wizard{

	static String copyright() {
		return com.ibm.is.sappack.gen.tools.jobgenerator.validator.Copyright.IBM_COPYRIGHT_SHORT;
	}

	private RGConfiguration configuration;
	private JobParameterResolverPage jobParameterResolverPage;
	private ValidationResultPage validationResultPage;
	private IISConnection connection;
	private String project;
	private SAPSettingsValidator validator;
	private ParameterResolver pr = new ParameterResolver();

	/**
	 * ValidationWizard
	 * @param validationResults
	 */
	public ValidationWizard(RGConfiguration configuration) {
		super();
		this.setWindowTitle(Messages.SapSystemWizardPage_0);
		this.setDialogSettings(Activator.getDefault().getDialogSettings());
		setNeedsProgressMonitor(true);
		this.configuration = configuration;
		
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

	@Override
	public void addPages() {
		
		/* add SelectIISConnectionWizardPage or SelectIISConnectionProjectWizardPage.
		 * If the rapid generator configuration contains any job parameter sets,
		 * we need the corresponding datastage project to load the parameter set
		 * default values from the server
		 */
		if(this.configuration.getJobParams().size() > 0) {
			
			/* add SelectIISConnectionProjectWizardPage - we need a datastage project */
			addPage(new SelectIISConnectionProjectWizardPage("validateagainstIISWizardpage", Messages.ValidationWizard_0, Messages.ValidationWizard_1, //$NON-NLS-1$
					(ImageDescriptor) null) {
				@Override
				public boolean nextPressedImpl() {

					connection = getIISConnection();
					project = getDSProject();
					jobParameterResolverPage.setIISConnection(connection);
					jobParameterResolverPage.setDSProject(project);
					
					boolean setPW = connection.ensurePasswordIsSet();
					if (!setPW) {
						return false;
					}
					return true;
				}
			});
			
			/* add ParameterResolverPage to resolve parameter values before validation */
			this.jobParameterResolverPage = new JobParameterResolverPage(this.configuration) {
				@Override
				public boolean nextPressedImpl() {
					
					pr = getParameterResolver();
					validate();
					return true;
				}
			};
			addPage(this.jobParameterResolverPage);
			
			
		} else {
			
			/* add SelectIISConnectionWizardPage - we don't need a datastage project */
			addPage(new SelectIISConnectionWizardPage("validateagainstIISWizardpage2", Messages.ValidationWizard_2, Messages.ValidationWizard_3, //$NON-NLS-1$
					(ImageDescriptor) null) {
				@Override
				public boolean nextPressedImpl() {

					connection = getIISConnection();
					boolean setPW = connection.ensurePasswordIsSet();
					
					if (!setPW) {
						return false;
					}
					validate();
					return true;
				}
			});
		}
		
		/* add ValidationResultPage */
		validationResultPage = new ValidationResultPage();
		addPage(this.validationResultPage);
	}
		
	
	private void validate() {
		
		/* initialize validator */
		validator = new SAPSettingsValidator(configuration, connection, pr);
		try {
			getContainer().run(true, true, validator);
		} catch (InvocationTargetException e1) {
			e1.printStackTrace();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		validationResultPage.updateValidationResult(validator.getValidationResults());
	}


	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		
		return super.getNextPage(page);
	}
	
}
