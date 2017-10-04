//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2011, 2014                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.iis.sappack.gen.tools.sap.importwizard
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.iis.sappack.gen.tools.sap.importwizard;


import java.io.ByteArrayInputStream;
import java.text.MessageFormat;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.MessageDialog;
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
import org.eclipse.ui.dialogs.SaveAsDialog;

import com.ibm.iis.sappack.gen.common.ui.util.Utils;
import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.common.ui.WidgetIDConstants;
import com.ibm.is.sappack.gen.common.ui.WidgetIDUtils;
import com.ibm.is.sappack.gen.common.ui.preferences.AdvancedSettingsPreferencePage;
import com.ibm.is.sappack.gen.tools.sap.activator.Activator;
import com.ibm.is.sappack.gen.tools.sap.importer.helper.summary.SummaryEntry;


public abstract class SummaryWizardPage extends WizardPage {
	private Text text;


	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	

	public SummaryWizardPage() {
		super(Messages.SummaryWizardPage_0, Messages.SummaryWizardPage_1, null);
		setDescription(Messages.SummaryWizardPage_2);
	}

	@Override
	public void createControl(final Composite parent) {
		Composite mainComposite = new Composite(parent, SWT.NONE);

		mainComposite.setLayout(new GridLayout(1, false));
		text = new Text(mainComposite, SWT.READ_ONLY | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		text.setLayoutData(new GridData(GridData.FILL_BOTH));

		if (AdvancedSettingsPreferencePage.isSettingEnabled("SAVE_AS_ANT_FILE_ENABLED")) { //$NON-NLS-1$

			Button saveButton = new Button(mainComposite, SWT.PUSH);
			saveButton.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false));
			saveButton.setText(Messages.SummaryWizardPage_7);
			saveButton.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					SaveAsDialog dialog = new SaveAsDialog(getShell());
					dialog.setTitle(Messages.SummaryWizardPage_8);
					dialog.setOriginalName("build.xml"); //$NON-NLS-1$
					int result = dialog.open();
					if (result == SaveAsDialog.CANCEL) {
						return;
					}
					IPath path = dialog.getResult();
					IFile f = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
					byte[] b = createAntFile();
					String errorTitle = Messages.SummaryWizardPage_9;
					String errorMsg = MessageFormat.format(Messages.SummaryWizardPage_10, path.toString());

					if (b == null) {
						MessageDialog.openError(getShell(), errorTitle, errorMsg);
						return;
					}
					try {
						if (!f.exists()) {
							f.create(new ByteArrayInputStream(b), true, null);
						} else {
							f.setContents(new ByteArrayInputStream(b), true, true, null);
						}
					} catch (CoreException exc) {
						Activator.logException(exc);
						MessageDialog.openError(getShell(), errorTitle, errorMsg);
						return;
					}

				}

			});
		}

		setControl(mainComposite);
	}

	public abstract byte[] createAntFile();

	public void setSummary(List<SummaryEntry> entries) {
		final String NL = "\n"; //$NON-NLS-1$
		StringBuffer summaryText = new StringBuffer(Messages.SummaryWizardPage_3 + NL);
		for (SummaryEntry entry : entries) {
			String pfx = null;
			switch (entry.getMessageType()) {
			case ERROR:
				pfx = Messages.SummaryWizardPage_4;
				break;
			case WARNING:
				pfx = Messages.SummaryWizardPage_5;
				break;
			default:
				pfx = Messages.SummaryWizardPage_6;
			}
			summaryText.append(pfx + ": " + entry.getMessageText() + NL); //$NON-NLS-1$
		}
		this.text.setText(summaryText.toString());
		WidgetIDUtils.assignID(text, WidgetIDConstants.MOD_SUMMARYTEXT);
	}

	@Override
	public void performHelp() {
		PlatformUI.getWorkbench().getHelpSystem().displayHelp(Utils.getHelpID("rmwiz_import_summary")); //$NON-NLS-1$
	}
}
