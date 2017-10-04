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
// Module Name : com.ibm.is.sappack.gen.common.ui.preferences
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.common.ui.preferences;


import java.text.MessageFormat;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.WorkbenchMessages;
import org.eclipse.ui.internal.dialogs.AboutDialog;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;
import org.eclipse.ui.progress.UIJob;

import com.ibm.is.sappack.gen.common.Constants;
import com.ibm.is.sappack.gen.common.request.ServerRequestUtil;
import com.ibm.is.sappack.gen.common.ui.Activator;
import com.ibm.is.sappack.gen.common.ui.Messages;

public class AltJCoConfigPage extends PreferencePage implements IWorkbenchPreferencePage {

	static String copyright() {
		return com.ibm.is.sappack.gen.common.ui.preferences.Copyright.IBM_COPYRIGHT_SHORT;
	}

	private TabbedJCoPreferenceSettingsWidget jcoSettings = null;
	private boolean hasChanges = false;
	private boolean widgetsCreated = false;
	private boolean restartPosted = false;

	/**
	 * 
	 */
	public AltJCoConfigPage() {
		super();
	}

	public boolean isWidgetsCreated() {
		return widgetsCreated;
	}

	public void setWidgetsCreated(boolean widgetsCreated) {
		this.widgetsCreated = widgetsCreated;
	}

	public boolean isHasChanges() {
		return hasChanges;
	}

	public void setHasChanges(boolean hasChanges) {
		this.hasChanges = hasChanges;
	}

	/**
	 * @param title
	 */
	public AltJCoConfigPage(String title) {
		super(title);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param title
	 * @param image
	 */
	public AltJCoConfigPage(String title, ImageDescriptor image) {
		super(title, image);
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createContents(Composite parent) {
		
		Label l = new Label(parent, SWT.NONE);
		l.setText(MessageFormat.format(Messages.ModelVersion, Constants.MODEL_VERSION));

		l = new Label(parent, SWT.NONE);
		l.setText(MessageFormat.format(Messages.BuildNumber, Constants.BUILD_ID));

		l = new Label(parent, SWT.NONE);
		l.setText(MessageFormat.format(Messages.ClientServerVersion, Constants.CLIENT_SERVER_VERSION));
		
		l = new Label(parent, SWT.NONE);
		String isIISLibsUsedAsText;
		if (ServerRequestUtil.isIISServerConnectionLibsUsed()) {
			isIISLibsUsedAsText = Messages.Enabled;
		}
		else {
			isIISLibsUsedAsText = Messages.NotEnabled;
		}
		l.setText(MessageFormat.format(Messages.UsePicassoClienLibs, isIISLibsUsedAsText));

		jcoSettings =
		// new AltJCoPreferenceSettingsWidget
		      // (
		      // this,
		      // Activator.getDefault().getPreferenceStore(),
		      // parent,
		      // SWT.None
		      // );
		      new TabbedJCoPreferenceSettingsWidget(this, Activator.getDefault().getPreferenceStore(), parent, SWT.None);
		jcoSettings.jcoTabFolder.setSelection(0);

		// only show error messages if we want to use custom settings
		if (!Activator.getDefault().getUseInstallerConfig()) {
			this.setErrorMessage(null);
		}

		return jcoSettings;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
		// Initialize the preference store we wish to use
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		// only show error messages if we want to use custom settings
		if (!Activator.getDefault().getUseInstallerConfig()) {
			this.setErrorMessage(null);
		}

		// default settings will be initialized by the PreferencesInitializer
		// Activator.getDefault().getPreferenceStore().setDefault(PreferenceConstants.P_USE_INSTALLER_CONFIG, true);
		// Activator.getDefault().getPreferenceStore().setDefault(PreferenceConstants.P_JCO_JAR_LOCATION,
		// "<path value referencing the SAP JCo Java archive file>");
		// Activator.getDefault().getPreferenceStore().setDefault(PreferenceConstants.P_JCO_NATIVE_LIB_DIR,
		// "<path value referencing a directory hosting the SAP JCo DLL or shared library>");
	}

	/**
	 * Performs special processing when this page's Restore Defaults button has been pressed. Sets the contents of the
	 * nameEntry field to be the default
	 */
	protected void performDefaults() {
		this.jcoSettings.jcoJARFieldEditor.loadDefault();
		this.jcoSettings.jcoNativeLibFieldEditor.loadDefault();
		this.jcoSettings.useInstallerConfigFieldEditor.loadDefault();

		super.performDefaults();
	}

	/**
	 * Method declared on IPreferencePage. Save the author name to the preference store.
	 */
	public boolean performOk() {
		
		// reset this preference page's error message
		this.setErrorMessage(null);

		// only store custom settings if the checkbox has been ticked, else set default values
		if (this.jcoSettings.useInstallerConfigFieldEditor.getBooleanValue() && this.hasChanges) {
			//boolean result = MessageDialog.openQuestion(null, Messages.AltJCoConfigPage_0, Messages.AltJCoConfigPage_1);

			if (true) {
				String jcoJarString = jcoSettings.jcoJARFieldEditor.getStringValue();
				String jcoNativeLibString = jcoSettings.jcoNativeLibFieldEditor.getStringValue();

				Activator.getLogger().config(Messages.AltJCoConfigPage_2);

				this.jcoSettings.jcoJARFieldEditor.store();
				this.jcoSettings.jcoNativeLibFieldEditor.store();
				this.jcoSettings.useInstallerConfigFieldEditor.store();

				if ((!jcoJarString.equals("") && !jcoJarString.equals(Messages.PreferenceInitializer_0)) //$NON-NLS-1$
				      && (!jcoNativeLibString.equals("") && !jcoNativeLibString.equals(Messages.PreferenceInitializer_1))) { //$NON-NLS-1$
					Activator.getDefault().restartJCoBundle(this.jcoSettings.jcoJARFieldEditor.getStringValue(),
					      this.jcoSettings.jcoNativeLibFieldEditor.getStringValue());
				}

				Activator.getLogger().config(Messages.AltJCoConfigPage_3);
				Activator.getLogger().config(Messages.AltJCoConfigPage_4 + Activator.locateJCoJAR());

				Activator.getDefault().savePluginPreferences();
				
				// we can get here through performApply, in that case only post one
				// restart
				if (!restartPosted) {
					if (getContainer() instanceof IWorkbenchPreferenceContainer) {
						IWorkbenchPreferenceContainer container = (IWorkbenchPreferenceContainer) getContainer();
						UIJob job = new UIJob(
								WorkbenchMessages.ViewsPreference_restartRequestJobName) {
							public IStatus runInUIThread(IProgressMonitor monitor) {
								// make sure they really want to do this
								int really = new MessageDialog(
										null,
										Messages.AltJCoConfigPage_0,
										null,
										Messages.AltJCoConfigPage_1,
										MessageDialog.QUESTION,
										new String[] {
												WorkbenchMessages.ViewsPreference_presentationConfirm_yes,
												WorkbenchMessages.ViewsPreference_presentationConfirm_no },
										1).open();
								if (really == Window.OK) {
									PlatformUI.getWorkbench().restart();
								}
								return Status.OK_STATUS;
							}
						};
						job.setSystem(true);
						container.registerUpdateJob(job);
						restartPosted = true;
					}
				}

				//org.eclipse.ui.PlatformUI.getWorkbench().restart();
				return true;
			}
		}
		else if (! this.jcoSettings.useInstallerConfigFieldEditor.getBooleanValue()) {
			this.jcoSettings.jcoJARFieldEditor.loadDefault();
			this.jcoSettings.jcoNativeLibFieldEditor.loadDefault();
			this.jcoSettings.useInstallerConfigFieldEditor.loadDefault();
			this.jcoSettings.jcoJARFieldEditor.store();
			this.jcoSettings.jcoNativeLibFieldEditor.store();
			this.jcoSettings.useInstallerConfigFieldEditor.store();
		}

		Activator.getDefault().savePluginPreferences();

		return super.performOk();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#contributeButtons(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void contributeButtons(Composite parent) {
	    Button aboutButton = new Button(parent, SWT.PUSH);
	    aboutButton.setText(Messages.AltJCoConfigPage_5);
	    aboutButton.addSelectionListener(new SelectionAdapter() {
	      public void widgetSelected(SelectionEvent event) {
	    	  AboutDialog about = new AboutDialog(event.widget.getDisplay().getActiveShell());
	    	  about.open();
	      }
	    });

	    // Add two columns to the parent's layout
	    ((GridLayout) parent.getLayout()).numColumns += 1;
	}

}
