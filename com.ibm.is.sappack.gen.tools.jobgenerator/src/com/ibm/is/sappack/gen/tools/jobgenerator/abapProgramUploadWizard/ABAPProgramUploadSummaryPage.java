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

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import com.ibm.iis.sappack.gen.common.ui.wizards.PersistentWizardPageBase;
import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.common.ui.WidgetIDConstants;
import com.ibm.is.sappack.gen.common.ui.WidgetIDUtils;
import com.ibm.is.sappack.gen.tools.jobgenerator.Activator;

public class ABAPProgramUploadSummaryPage extends PersistentWizardPageBase {

   private static final String HELP_ID = Activator.PLUGIN_ID + "." + "JobGeneratorSummaryPage"; //$NON-NLS-1$ //$NON-NLS-2$
   
   private Text                         summaryText;
	
   // static inclusion of copyright statement
   static String copyright() {
      return com.ibm.is.sappack.gen.tools.jobgenerator.abapProgramUploadWizard.Copyright.IBM_COPYRIGHT_SHORT;
   }

	// non-default constructor
	public ABAPProgramUploadSummaryPage(ABAPProgramUploadWizard wizard) {
		super(Messages.JobGeneratorSummaryPage_Title, Messages.JobGeneratorSummaryPage_Title, null);
		setDescription(Messages.JobGeneratorSummaryPage_Description);
	}

	
	public void updateEnablement() {
		boolean pageComplete = true;
		String errorMessage = null;
		setErrorMessage(errorMessage);
		setPageComplete(pageComplete);
	}

	@Override
	public void performHelp() {
      PlatformUI.getWorkbench().getHelpSystem().displayHelp(HELP_ID);
	}

	public void setSummary(String s) {
		this.summaryText.setText(s);
	}

	@Override
	protected Composite createControlImpl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(1, false));
		summaryText = new Text(container, SWT.READ_ONLY | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		summaryText.setLayoutData(new GridData(GridData.FILL_BOTH));
		WidgetIDUtils.assignID(summaryText, WidgetIDConstants.GEN_SUMMARYTEXT);
		
		updateEnablement();
		
		return(container);
	}

	@Override
	public boolean nextPressedImpl() {
		//nothing to do
		return false;
	}
	
   
}
