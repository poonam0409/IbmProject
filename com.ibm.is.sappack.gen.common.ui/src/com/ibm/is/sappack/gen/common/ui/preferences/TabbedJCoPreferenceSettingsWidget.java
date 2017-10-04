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
// Module Name : com.ibm.is.sappack.gen.common.ui.preferences
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.common.ui.preferences;

import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import com.ibm.is.sappack.gen.common.ui.Activator;
import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.common.ui.preferences.AltJCoPreferenceSettingsWidget.MyBooleanFieldEditor;

public class TabbedJCoPreferenceSettingsWidget extends Composite {

	static String copyright() {
		return com.ibm.is.sappack.gen.common.ui.preferences.Copyright.IBM_COPYRIGHT_SHORT;
	}

	protected TabFolder jcoTabFolder = null;
	protected FileFieldEditor jcoJARFieldEditor = null;
	protected MyBooleanFieldEditor useInstallerConfigFieldEditor = null;
	protected DirectoryFieldEditor jcoNativeLibFieldEditor = null;
	
	protected TraceSettingsWidget traceSettingsWidget = null;

	/**
	 * @param parent
	 * @param style
	 */
	public TabbedJCoPreferenceSettingsWidget(Composite parent, int style) {
		super(parent, style);
		// TODO Auto-generated constructor stub
		initialize(null, null);
	}

	/**
	 * @param parent
	 * @param style
	 */
	public TabbedJCoPreferenceSettingsWidget(PreferencePage page, IPreferenceStore store, Composite parent, int style) {
		super(parent, style);
		// TODO Auto-generated constructor stub
		initialize(page, store);
	}

	private void initialize(PreferencePage page, IPreferenceStore store) {
		createJcoTabFolder(page, store);
		GridLayout gridLayout = new GridLayout();
		gridLayout.horizontalSpacing = 5;
		gridLayout.verticalSpacing = 5;
		gridLayout.marginHeight = 10;
		gridLayout.marginWidth = 10;
		gridLayout.numColumns = 1;
		this.setLayout(gridLayout);
		this.setSize(new Point(290, 200));
	}

	/**
	 * This method initializes jcoTabFolder
	 * 
	 */
	private void createJcoTabFolder(final PreferencePage page, final IPreferenceStore store) {
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = GridData.FILL;

		jcoTabFolder = new TabFolder(this, SWT.NONE);
		jcoTabFolder.setLayoutData(gridData);
		//jcoTabFolder.setUnselectedCloseVisible(false);
		//jcoTabFolder.setSimple(false);

		TabItem jcoSettingsItem = new TabItem(jcoTabFolder, SWT.NONE);
		jcoSettingsItem.setText(Messages.TabbedJCoPreferenceSettingsWidget_0);

		AltJCoPreferenceSettingsWidget jcoSettings =
		      new AltJCoPreferenceSettingsWidget(page, Activator.getDefault().getPreferenceStore(), jcoTabFolder,
		            SWT.None);
		
		((AltJCoConfigPage) page).setWidgetsCreated(true);

		this.jcoJARFieldEditor = jcoSettings.jcoJARFieldEditor;
		this.jcoNativeLibFieldEditor = jcoSettings.jcoNativeLibFieldEditor;
		this.useInstallerConfigFieldEditor = jcoSettings.useInstallerConfigFieldEditor;

		jcoSettingsItem.setControl(jcoSettings);
		if (this.useInstallerConfigFieldEditor.getBooleanValue()) {
			if (Activator.checkJCoAvailabilityNonGUI()) {
				this.addJcoAboutTab();
			}
			else {
				page.setErrorMessage(Activator.getJCoErrorMessage());
			}
		}
	}

	public void addJcoAboutTab() {
		if (this.jcoJARFieldEditor.isValid() && this.jcoNativeLibFieldEditor.isValid() && jcoTabFolder.getItemCount() < 2) {
			TabItem jcoAboutsItem = new TabItem(jcoTabFolder, SWT.NONE);
			jcoAboutsItem.setText(Messages.TabbedJCoPreferenceSettingsWidget_1);
			try {
				JCoAboutWidget aboutWidget = new JCoAboutWidget(jcoTabFolder, SWT.NONE);
				jcoAboutsItem.setControl(aboutWidget);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else if (this.jcoJARFieldEditor.isValid() && this.jcoNativeLibFieldEditor.isValid()
		      && jcoTabFolder.getItemCount() == 2) {
			// remove the 2nd tab
			TabItem oldItem = jcoTabFolder.getItem(1);
			oldItem.dispose();
			jcoTabFolder.setSelection(0);
			// and re-add it to update the JCo about values
			TabItem jcoAboutsItem = new TabItem(jcoTabFolder, SWT.NONE);
			jcoAboutsItem.setText(Messages.TabbedJCoPreferenceSettingsWidget_2);
			JCoAboutWidget aboutWidget = new JCoAboutWidget(jcoTabFolder, SWT.NONE);
			jcoAboutsItem.setControl(aboutWidget);
		}
	}
} // @jve:decl-index=0:visual-constraint="10,10"
