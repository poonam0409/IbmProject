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
// Module Name : com.ibm.iis.sappack.gen.tools.jobgenerator.jobgenwizard
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.iis.sappack.gen.tools.jobgenerator.jobgenwizard;


import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import com.ibm.iis.sappack.gen.common.ui.preferences.IISPreferencePage;
import com.ibm.iis.sappack.gen.common.ui.util.Utils;
import com.ibm.iis.sappack.gen.common.ui.wizards.INextActionWizardPage;
import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.common.ui.WidgetIDConstants;
import com.ibm.is.sappack.gen.common.ui.WidgetIDUtils;


public class JobGenSummaryPage extends WizardPage implements INextActionWizardPage {
  	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	

	private Text summaryText;
	private Button compileJobsButton;

	public JobGenSummaryPage() {
		super("jobgensummarypage", Messages.JobGenSummaryPage_0, null); //$NON-NLS-1$
		this.setDescription(Messages.JobGenSummaryPage_1);
	}
	
	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(1, false));
		summaryText = new Text(container, SWT.READ_ONLY | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		summaryText.setLayoutData(new GridData(GridData.FILL_BOTH));
		WidgetIDUtils.assignID(summaryText, WidgetIDConstants.GEN_SUMMARYTEXT);
		compileJobsButton = new Button(container, SWT.CHECK);
		compileJobsButton.setText(Messages.JobGeneratorSummaryPage_CompileJobs);
		WidgetIDUtils.assignID(compileJobsButton, WidgetIDConstants.GEN_COMPILEJOBSBUTTON);

		// we only enable the DataStage Multiple Job Compiler Wizard invocation
		// button if the DataStage client installation directory is set
		// on the preference page
		if (IISPreferencePage.getIISClientDirectory().isEmpty()) {
			compileJobsButton.setEnabled(false);
		}

		compileJobsButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
//				updateEnablement();
			}

		});
		setControl(container);
	}

	@Override
	public boolean nextPressed() {
		return true;
	}
	
	public boolean getCompileJobsButton() {
		return this.compileJobsButton.getSelection();
	}
	
	public void setSummary(String s) {
		this.summaryText.setText(s);
	}

	@Override
	public void performHelp() {
		PlatformUI.getWorkbench().getHelpSystem().displayHelp(Utils.getHelpID("rgwiz_summary")); //$NON-NLS-1$
	}
}
