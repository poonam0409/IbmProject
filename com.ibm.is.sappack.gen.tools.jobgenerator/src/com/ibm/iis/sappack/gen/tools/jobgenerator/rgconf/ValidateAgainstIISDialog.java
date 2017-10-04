//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2014                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.iis.sappack.gen.tools.jobgenerator.rgconf
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.iis.sappack.gen.tools.jobgenerator.rgconf;


import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Shell;

import com.ibm.iis.sappack.gen.common.ui.connections.IISConnection;
import com.ibm.iis.sappack.gen.common.ui.wizards.GenericTextWizardPage;
import com.ibm.iis.sappack.gen.common.ui.wizards.NextActionWizardDialog;
import com.ibm.iis.sappack.gen.common.ui.wizards.SelectIISConnectionProjectWizardPage;
import com.ibm.iis.sappack.gen.common.ui.wizards.SelectIISConnectionWizardPage;
import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.tools.jobgenerator.Activator;


public abstract class ValidateAgainstIISDialog { // extends NextActionWizardDialog {
	private Shell                 shell;
	private boolean               showProject;
	private GenericTextWizardPage textPage = null;


  	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	


	public ValidateAgainstIISDialog(Shell parentShell, boolean showProject) {
		this.shell = parentShell;
		this.showProject = showProject;
	}

	public int open() {
		textPage = new GenericTextWizardPage("validateagainstiistextpage", Messages.ValidateAgainstIISDialog_0, null); //$NON-NLS-1$
		Wizard wizard = null;
		if (this.showProject) {
			wizard = new ValidateAgainstIISWithProjectWizard();
		}
		else {
			wizard = new ValidateAgainstIISWizard();
		}

		NextActionWizardDialog dialog = new NextActionWizardDialog(shell, wizard);

		// there is no help for this dialog ==> hide HELP button
		dialog.setHelpAvailable(false);

		return dialog.open();
	}

	class ValidateAgainstIISWithProjectWizard extends Wizard {

		ValidateAgainstIISWithProjectWizard() {
			this.setDialogSettings(Activator.getDefault().getDialogSettings());
			this.setNeedsProgressMonitor(true);

			addPage(new SelectIISConnectionProjectWizardPage("validateagainstIISWizardpage1",   //$NON-NLS-1$
			                                                 Messages.ValidateAgainstIISDialog_1,
			                                                 Messages.ValidateAgainstIISDialog_2, 
			                                                 null) { 

				@Override
				public boolean nextPressedImpl() {
					final IISConnection conn = this.getIISConnection();
					boolean setPW = conn.ensurePasswordIsSet();
					if (!setPW) {
						return false;
					}
					final String project = this.getDSProject();
					final StringBuffer msgBuf = new StringBuffer();
					IRunnableWithProgress run = new IRunnableWithProgress() {

						@Override
						public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
							String msg = performValidationAction(conn, project, monitor);
							msgBuf.append(msg);
						}
					};
					try {
						getContainer().run(true, true, run);
					} catch (Exception e) {
						Activator.logException(e);
						return false;
					}
					textPage.setText(msgBuf.toString());
					return true;
				}

			});
			addPage(textPage);

		}

		@Override
		public boolean performFinish() {
			return true;
		}

	}

	class ValidateAgainstIISWizard extends Wizard {

		ValidateAgainstIISWizard() {
			this.setDialogSettings(Activator.getDefault().getDialogSettings());
			this.setNeedsProgressMonitor(true);

			addPage(new SelectIISConnectionWizardPage("validateagainstIISWizardpage2", Messages.ValidateAgainstIISDialog_3, Messages.ValidateAgainstIISDialog_4, //$NON-NLS-1$
					(ImageDescriptor) null) {

				@Override
				public boolean nextPressedImpl() {
					final IISConnection conn = this.getIISConnection();
					boolean setPW = conn.ensurePasswordIsSet();
					if (!setPW) {
						return false;
					}
					final String project = null;
					final StringBuffer msgBuf = new StringBuffer();
					IRunnableWithProgress run = new IRunnableWithProgress() {

						@Override
						public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
							String msg = performValidationAction(conn, project, monitor);
							msgBuf.append(msg);
						}
					};
					try {
						getContainer().run(true, true, run);
					} catch (Exception e) {
						Activator.logException(e);
						return false;
					}
					textPage.setText(msgBuf.toString());
					return true;
				}

			});
			addPage(textPage);

		}

		@Override
		public boolean performFinish() {
			return true;
		}

	}

	protected abstract String performValidationAction(IISConnection connection, String dsProject, IProgressMonitor monitor);

}
