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


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.ibm.iis.sappack.gen.common.ui.editors.IControlFactory;
import com.ibm.iis.sappack.gen.common.ui.util.Utils;
import com.ibm.is.sappack.gen.common.Constants;
import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.common.ui.WidgetIDConstants;
import com.ibm.is.sappack.gen.common.ui.WidgetIDUtils;
import com.ibm.is.sappack.gen.tools.jobgenerator.Activator;


public class JobDescriptionPage extends RGConfPageBase {
	private static final String WIDGET_SETTINGS_PREFIX = "JobDescriptionPage."; //$NON-NLS-1$
	public static final String WIDGET_NAME_DESC_ENABLED = WIDGET_SETTINGS_PREFIX + "descEnabled"; //$NON-NLS-1$
	public static final String WIDGET_NAME_EDITOR_TEXT = WIDGET_SETTINGS_PREFIX + "editorText"; //$NON-NLS-1$

	public static final String TABNAME = Messages.JobDescriptionPage_0;
	
	private Button descEnabledButton;
	private Text longDescEditor;
	private Button descLoadButton;


	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	


  	public JobDescriptionPage() {
		super(TABNAME, Messages.JobDescriptionPage_1, Messages.JobDescriptionPage_2,
		      Utils.getHelpID("rgconfeditor_job_description")); //$NON-NLS-1$
	}

	@Override
	public void createControls(IControlFactory controlFactory, Composite parent) {

		// Job Description
		Composite longDescComposite = controlFactory.createGroup(parent, Messages.CWAdditionalJobSettingsPage_groupname, SWT.NONE);
		GridLayout longDescCompositeLayout = new GridLayout();
		longDescCompositeLayout.numColumns = 1;
		longDescComposite.setLayout(longDescCompositeLayout);
		longDescComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		// --- Enable/Disable button ---
		descEnabledButton = controlFactory.createButton(longDescComposite, SWT.CHECK);
		descEnabledButton.setText(Messages.CWAdditionalJobSettingsPage_enableBttnText);
		descEnabledButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean isEnabled = descEnabledButton.getSelection();

				longDescEditor.setEnabled(isEnabled);
				descLoadButton.setEnabled(isEnabled);
				//               updateEnablement();
			}
		});
		this.configureCheckboxForProperty(descEnabledButton, WIDGET_NAME_DESC_ENABLED);
		WidgetIDUtils.assignID(descEnabledButton, WidgetIDConstants.GEN_DESCRIPTION_ENABLED_BUTTON);

		// --- Headline & Multi Line Entry field ---
		Label textLabel = controlFactory.createLabel(longDescComposite, SWT.NONE);
		textLabel.setText(Messages.CWAdditionalJobSettingsPage_descLabel);
		textLabel.setLayoutData(new GridData(GridData.END));

		longDescEditor = controlFactory.createText(longDescComposite, SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
		longDescEditor.setLayoutData(new GridData(GridData.FILL_BOTH));
		longDescEditor.setEnabled(this.descEnabledButton.getSelection());
		this.configureTextForProperty(longDescEditor, WIDGET_NAME_EDITOR_TEXT);
		WidgetIDUtils.assignID(longDescEditor, WidgetIDConstants.GEN_DESCRIPTION_EDITOR);

		// --- Load From File button ---
		descLoadButton = controlFactory.createButton(longDescComposite, SWT.PUSH);
		GridData buttonData = new GridData(GridData.END);
		descLoadButton.setLayoutData(buttonData);
		descLoadButton.setText(Messages.CWAdditionalJobSettingsPage_loadBttnText);
		descLoadButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.OPEN);
				String path = dialog.open();

				// read text from file
				try {
					longDescEditor.setText(readTextFromFile(path));
				} catch (IOException pIOExcpt) {
					longDescEditor.setText(""); //$NON-NLS-1$
				}
			}
		});
		descLoadButton.setEnabled(this.descEnabledButton.getSelection());
		WidgetIDUtils.assignID(descLoadButton, WidgetIDConstants.GEN_DESCRIPTION_FILE_LOAD_BUTTON);

		// dummy composite at the end of the page
		controlFactory.createLabel(longDescComposite, SWT.NONE).setText(""); // dummy label  //$NON-NLS-1$
		Composite dummyComposite = controlFactory.createComposite(parent, SWT.NONE);
		GridLayout dummyCompositeLayout = new GridLayout();
		dummyCompositeLayout.numColumns = 1;
		dummyCompositeLayout.marginWidth = 0;
		dummyCompositeLayout.marginHeight = 0;
		dummyComposite.setLayout(dummyCompositeLayout);

	}

	private String readTextFromFile(String descriptionFileName) throws IOException {
		StringBuffer result = new StringBuffer();
		Activator.getLogger().info("Copyright file name: " + descriptionFileName); //$NON-NLS-1$

		if ((descriptionFileName != null) && !descriptionFileName.trim().isEmpty()) {
			File copyrightFile = new File(descriptionFileName);
			Activator.getLogger().info("Copyright file exists: " + copyrightFile.exists()); //$NON-NLS-1$
			if (copyrightFile.exists()) {
				FileInputStream fis = null;
				InputStreamReader isr = null;
				BufferedReader reader = null;

				try {
					fis = new FileInputStream(copyrightFile);
					isr = new InputStreamReader(fis);
					reader = new BufferedReader(isr);

					int lineCount = 0;
					String tmpLine = reader.readLine();
					while (tmpLine != null) {
						if (lineCount > 0) {
							result.append(Constants.NEWLINE);
						}
						result.append(tmpLine.trim());
						tmpLine = reader.readLine();
						lineCount++;
					}
				} catch (Exception exc) {
					throw new IOException(exc);
				} finally {
					try {
						if (reader != null) {
							reader.close();
						}
						if (isr != null) {
							isr.close();
						}
						if (fis != null) {
							fis.close();
						}
					} catch (IOException ioe) {
						Activator.getLogger().severe(ioe.toString());
						ioe.printStackTrace();
					}
				}
			}
		}

		Activator.getLogger().info("Copyright data read: len = " + result.length()); //$NON-NLS-1$

		return result.toString();
	}

}
