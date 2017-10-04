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
// Module Name : com.ibm.is.sappack.gen.tools.jobgenerator
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.iis.sappack.gen.common.ui.preferences;


import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.ibm.iis.sappack.gen.common.ui.iisclient.IISClient;
import com.ibm.is.sappack.gen.common.ui.Activator;
import com.ibm.is.sappack.gen.common.ui.Messages;


public class IISPreferencePage extends org.eclipse.jface.preference.PreferencePage implements IWorkbenchPreferencePage {

	private static final String IIS_CLIENT_DIRECTORY_PREFERENCE = "IIS_CLIENT_DIRECTORY_PREFERENCE"; //$NON-NLS-1$

	private Text dsClientDirectoryText;

	
	static String copyright() {
		return Copyright.IBM_COPYRIGHT_SHORT;
	}	
	
	
	public IISPreferencePage() {
		// TODO Auto-generated constructor stub
	}

	public IISPreferencePage(String title) {
		super(title);
		// TODO Auto-generated constructor stub
	}

	public IISPreferencePage(String title, ImageDescriptor image) {
		super(title, image);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Control createContents(final Composite parent) {
		Composite mainComposite = parent;
		Group dsClientComposite = new Group(mainComposite, SWT.NONE);
		dsClientComposite.setText(Messages.IISPreferencePage_0);
		GridLayout dsClientLayout = new GridLayout();
		dsClientLayout.numColumns = 3;
		dsClientComposite.setLayout(dsClientLayout);
		dsClientComposite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING));

		Label dsClientLabel = new Label(dsClientComposite, SWT.NONE);
		dsClientLabel.setText(Messages.IISPreferencePage_1);

		dsClientDirectoryText = new Text(dsClientComposite, SWT.BORDER);
		dsClientDirectoryText.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
		IPreferenceStore ps = getPreferenceStore();

		if (ps != null) {
			String storedDir = ps.getString(IIS_CLIENT_DIRECTORY_PREFERENCE);
			if (storedDir != null) {
				dsClientDirectoryText.setText(storedDir);
			}
		}

		Button dsClientFileButton = new Button(dsClientComposite, SWT.NONE);
		dsClientFileButton.setText(Messages.IISPreferencePage_2);

		dsClientFileButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dialog = new DirectoryDialog(parent.getShell(), SWT.OPEN);
				String path = dialog.open();
				dsClientDirectoryText.setText(path);
			}
		});

		Button testDSClientButton = new Button(dsClientComposite, SWT.NONE);
		testDSClientButton.setText(Messages.IISPreferencePage_3);
		testDSClientButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				IISClient client = new IISClient(dsClientDirectoryText.getText());
				client.checkWithDialogs();
			}

		});
		
		// add dummy composite to grab all the space
		Composite comp = new Composite(mainComposite, SWT.NONE); 
		comp.setLayout(new GridLayout());
		comp.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL | GridData.FILL_BOTH));
		return mainComposite;
	}

	@Override
	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub

	}

	private void storePreferences() {
		IPreferenceStore ps1 = this.getPreferenceStore();
		if (ps1 != null) {
			if (this.dsClientDirectoryText != null) {
				ps1.setValue(IIS_CLIENT_DIRECTORY_PREFERENCE, this.dsClientDirectoryText.getText());
			}
		}

	}

	@Override
	protected void performApply() {
		storePreferences();
		super.performApply();
	}

	@Override
	public boolean performOk() {
		storePreferences();
		return super.performOk();
	}

	protected IPreferenceStore doGetPreferenceStore() {
		return Activator.getDefault().getPreferenceStore();
	}
	
	public static String getIISClientDirectory() {
		return Activator.getDefault().getPreferenceStore().getString(IIS_CLIENT_DIRECTORY_PREFERENCE);
	}

}
