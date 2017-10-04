//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2009, 2012                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.is.sappack.gen.tools.sap.reports
//                                                                             
//*************************-END OF SPECIFICATIONS-**************************
package com.ibm.is.sappack.gen.tools.sap.reports;

import java.io.File;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.ibm.is.sappack.gen.common.ui.Messages;

public class SelectReportFileWizardPage extends WizardPage {

	static String copyright() {
		return Copyright.IBM_COPYRIGHT_SHORT;
	}

	Text reportFile;
	Text separator;

	public SelectReportFileWizardPage(String reportDescription) {
		super("selectreportfilewizardpage"); //$NON-NLS-1$
		this.setTitle(Messages.SelectReportFileWizardPage_0);
		this.setDescription(Messages.SelectReportFileWizardPage_1);
	}

	@Override
	public void createControl(Composite parent) {
		Composite mainComposite = new Composite(parent, SWT.NONE);
		mainComposite.setLayout(new GridLayout(3, false));
		mainComposite.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL | GridData.FILL_BOTH));

		Label l = new Label(mainComposite, SWT.NONE);
		l.setText(Messages.SelectReportFileWizardPage_2);

		ModifyListener textModifyListener = new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				update();
			}
		};

		this.reportFile = new Text(mainComposite, SWT.BORDER);
		this.reportFile.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
		this.reportFile.addModifyListener(textModifyListener);

		Button fileChooseButton = new Button(mainComposite, SWT.NONE);
		fileChooseButton.setText(Messages.SelectReportFileWizardPage_3);

		fileChooseButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fd = new FileDialog(getShell(), SWT.SAVE);
				fd.setText(Messages.SelectReportFileWizardPage_4);
				fd.setFilterExtensions(new String[] { "*.csv" }); //$NON-NLS-1$
				String fileName = fd.open();
				reportFile.setText(fileName);
			}

		});

		Label sepLabel = new Label(mainComposite, SWT.NONE);
		sepLabel.setText(Messages.SelectReportFileWizardPage_5);
		separator = new Text(mainComposite, SWT.BORDER);
		separator.setTextLimit(1);
		separator.setText(";"); //$NON-NLS-1$

		update();
		setControl(mainComposite);
	}

	private void update() {
		boolean canProceed = true;
		setErrorMessage(null);
		File f = getReportFile();
		if (f == null || f.isDirectory() || !f.getName().endsWith(".csv")) { //$NON-NLS-1$
			setErrorMessage(Messages.SelectReportFileWizardPage_6);
			canProceed = false;
		} else {
			if (f.exists()) {
				setMessage(Messages.SelectReportFileWizardPage_7, WARNING);
			} else {
				setMessage(null);
			}
		}
		this.setPageComplete(canProceed);
	}

	public File getReportFile() {
		String fileName = this.reportFile.getText();
		if (fileName.trim().isEmpty()) {
			return null;
		}
		File f = new File(fileName);
		return f;
	}

	public char getSeparatorChar() {
		String s = this.separator.getText();
		if (s.isEmpty()) {
			return ',';
		}
		return s.charAt(0);
	}

}
