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

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import com.ibm.is.sappack.gen.common.ui.Activator;
import com.ibm.is.sappack.gen.common.ui.Messages;

public class AltJCoPreferenceSettingsWidget extends Composite {

	static String copyright() {
		return com.ibm.is.sappack.gen.common.ui.preferences.Copyright.IBM_COPYRIGHT_SHORT;
	}

	private Group useInstallSettingsGroup = null;
	private Group useRuntimeSettingsGroup = null;
	private Label jcoSettingsDescLabel = null;
	private Label installJCoJARLabel = null;
	private Label installJCoSharedLibLabel = null;
	private Text installJCoJARText = null;
	private Text installJCoSharedLibText = null;
	
	// field editors
	protected FileFieldEditor jcoJARFieldEditor = null;
	protected MyBooleanFieldEditor useInstallerConfigFieldEditor = null;
	protected DirectoryFieldEditor jcoNativeLibFieldEditor = null;
	private Composite useCustomConfigSettingsLabelComp = null;
	private Composite useCustomConfigSettingsEditorComp = null;
	private StyledText jcoSettingsExclamationLabel = null;
	/**
	 * You may want to read the following resource before touching the code found herein:
	 * http://gd.tuwien.ac.at/.vhost/www.eclipse.org/articles/Article-Field-Editors/field_editors.html
	 * 
	 * @param parent
	 * @param style
	 */
	public AltJCoPreferenceSettingsWidget(Composite parent, int style) {
		super(parent, style);
		// TODO Auto-generated constructor stub
		initialize(null, null);
	}

	/**
	 * @param parent
	 * @param style
	 */
	public AltJCoPreferenceSettingsWidget
	(
		final PreferencePage page,
		IPreferenceStore store,
		Composite parent,
		int style
	)
	{
		super(parent, style);
		// TODO Auto-generated constructor stub
		initialize(page, store);
	}


	private void initialize(final PreferencePage page, IPreferenceStore store) {
		GridLayout gridLayout2 = new GridLayout();
		gridLayout2.horizontalSpacing = 5;
		gridLayout2.marginWidth = 10;
		gridLayout2.marginHeight = 10;
		gridLayout2.verticalSpacing = 5;
		this.setLayout(gridLayout2);
		this.setSize(new Point(500, 400));
		
		// checkbox field editor
		this.useInstallerConfigFieldEditor = new MyBooleanFieldEditor(PreferenceConstants.P_USE_INSTALLER_CONFIG,
				Messages.AltJCoPreferenceSettingsWidget_0, this);
		
		Button checkbox = this.useInstallerConfigFieldEditor.getChangeControl(this);
		checkbox.addListener(SWT.Selection, new Listener() {
		      public void handleEvent(Event event) {
		          Button button = (Button) event.widget;
		          
	                if (button.getSelection()) {
	                    button.setSelection(true);
	                	jcoJARFieldEditor.setEnabled(true, useCustomConfigSettingsEditorComp);
	                	jcoNativeLibFieldEditor.setEnabled(true, useCustomConfigSettingsEditorComp);
	                } else {
	                    button.setSelection(false);
	                	jcoJARFieldEditor.setEnabled(false, useCustomConfigSettingsEditorComp);
	                	jcoNativeLibFieldEditor.setEnabled(false, useCustomConfigSettingsEditorComp);
	                }
		        }
		      });
		
		this.useInstallerConfigFieldEditor.setPage(page);
		this.useInstallerConfigFieldEditor.setPreferenceStore(store);
		this.useInstallerConfigFieldEditor.load();
		
		checkbox.setSelection(true);
		
		createUseInstallSettingsGroup();
		createUseRuntimeSettingsGroup(page, store);
	}

	/**
	 * This method initializes useInstallSettingsGroup	
	 *
	 */
	private void createUseInstallSettingsGroup() {
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = GridData.CENTER;
		useInstallSettingsGroup = new Group(this, SWT.NONE);
		useInstallSettingsGroup.setLayoutData(gridData);
		useInstallSettingsGroup.setLayout(gridLayout);
		useInstallSettingsGroup.setText(Messages.AltJCoPreferenceSettingsWidget_1);
		
	    installJCoJARLabel = new Label(useInstallSettingsGroup, SWT.NONE);
	    installJCoJARLabel.setText(Messages.AltJCoPreferenceSettingsWidget_2);
	    installJCoJARText = new Text(useInstallSettingsGroup, SWT.BORDER);
	    installJCoJARText.setEditable(false);
	    installJCoJARText.setLayoutData(gridData);
	    installJCoJARText.setText(Activator.getDefault().getJCoJARLocationFromEclipseINI().replaceAll("\"", "")); //$NON-NLS-1$ //$NON-NLS-2$
	    installJCoSharedLibLabel = new Label(useInstallSettingsGroup, SWT.NONE);
	    installJCoSharedLibLabel.setText(Messages.AltJCoPreferenceSettingsWidget_5);
	    installJCoSharedLibText = new Text(useInstallSettingsGroup, SWT.BORDER);
	    installJCoSharedLibText.setEditable(false);
	    installJCoSharedLibText.setLayoutData(gridData);
	    installJCoSharedLibText.setText(Activator.getDefault().getJCoNativeLibDirFromEclipseINI().replaceAll("\"", "")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * This method initializes useRuntimeSettingsGroup	
	 *
	 */
	private void createUseRuntimeSettingsGroup(final PreferencePage page, IPreferenceStore store) {
		GridLayout gridLayout1 = new GridLayout();
		GridData gridData1 = new GridData();
		gridData1.horizontalAlignment = GridData.FILL;
		gridData1.grabExcessHorizontalSpace = true;
		gridData1.grabExcessVerticalSpace = true;
		gridData1.verticalAlignment = GridData.FILL;
		useRuntimeSettingsGroup = new Group(this, SWT.NONE);
		useRuntimeSettingsGroup.setText(Messages.AltJCoPreferenceSettingsWidget_8);
		useRuntimeSettingsGroup.setLayout(gridLayout1);
		useRuntimeSettingsGroup.setLayoutData(gridData1);
		createUseCustomConfigSettingsLabelComp();		
		createUseCustomConfigSettingsEditorComp(page, store);
	}
	
	public class MyBooleanFieldEditor extends BooleanFieldEditor {
	    /**
	     * Creates a new boolean field editor 
	     */
	    protected MyBooleanFieldEditor() {
	    }

	    /**
	     * Creates a boolean field editor in the given style.
	     * 
	     * @param name the name of the preference this field editor works on
	     * @param labelText the label text of the field editor
	     * @param style the style, either <code>DEFAULT</code> or
	     *   <code>SEPARATE_LABEL</code>
	     * @param parent the parent of the field editor's control
	     * @see #DEFAULT
	     * @see #SEPARATE_LABEL
	     */
	    public MyBooleanFieldEditor(String name, String labelText, int style,
	            Composite parent) {
	    	super(name, labelText, style, parent);
	    }

	    /**
	     * Creates a boolean field editor in the default style.
	     * 
	     * @param name the name of the preference this field editor works on
	     * @param label the label text of the field editor
	     * @param parent the parent of the field editor's control
	     */
	    public MyBooleanFieldEditor(String name, String label, Composite parent) {
	        super(name, label, parent);
	    }
	    
	    /**
	     * Returns the change button for this field editor.
	     * @param parent The Composite to create the receiver in.
	     *
	     * @return the change button
	     */
	    public Button getChangeControl(Composite parent) {
	    	return super.getChangeControl(parent);
	    }	
	}

	/**
	 * This method initializes useCustomConfigSettingsLabelComp	
	 *
	 */
	private void createUseCustomConfigSettingsLabelComp() {
		GridLayout gridLayout3 = new GridLayout();
		gridLayout3.horizontalSpacing = 0;
		gridLayout3.marginWidth = 0;
		gridLayout3.marginHeight = 0;
		gridLayout3.verticalSpacing = 0;
		GridData gridData2 = new GridData();
		gridData2.horizontalAlignment = GridData.FILL;
		gridData2.grabExcessHorizontalSpace = true;
		gridData2.grabExcessVerticalSpace = false;
		gridData2.verticalAlignment = GridData.CENTER;
		gridData2.widthHint = 200;
		gridData2.verticalSpan = 2;
		gridData2.heightHint = 60;
		useCustomConfigSettingsLabelComp = new Composite(useRuntimeSettingsGroup,
				SWT.NONE);
		useCustomConfigSettingsLabelComp.setLayoutData(gridData2);
		useCustomConfigSettingsLabelComp.setLayout(gridLayout3);
		
		jcoSettingsDescLabel = new Label(useCustomConfigSettingsLabelComp, SWT.WRAP);
		jcoSettingsDescLabel.setText(Messages.AltJCoPreferenceSettingsWidget_9);
		jcoSettingsDescLabel.setLayoutData(gridData2);
		jcoSettingsDescLabel.setToolTipText(""); //$NON-NLS-1$
		
		jcoSettingsExclamationLabel = new StyledText(useRuntimeSettingsGroup, SWT.READ_ONLY | SWT.SINGLE);
		jcoSettingsExclamationLabel.setText(Messages.AltJCoPreferenceSettingsWidget_11);
		jcoSettingsExclamationLabel.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		jcoSettingsExclamationLabel.setEnabled(true);
	}

	/**
	 * This method initializes useCustomConfigSettingsEditorComp	
	 *
	 */
	private void createUseCustomConfigSettingsEditorComp(final PreferencePage page, IPreferenceStore store) {
		GridData gridData3 = new GridData();
		gridData3.horizontalAlignment = GridData.FILL;
		gridData3.grabExcessHorizontalSpace = true;
		gridData3.grabExcessVerticalSpace = true;
		gridData3.verticalAlignment = GridData.FILL;
		useCustomConfigSettingsEditorComp = new Composite(useRuntimeSettingsGroup,
				SWT.NONE);
		useCustomConfigSettingsEditorComp.setLayout(new GridLayout());
		useCustomConfigSettingsEditorComp.setLayoutData(gridData3);
		
		this.jcoJARFieldEditor = new FileFieldEditor(PreferenceConstants.P_JCO_JAR_LOCATION, Messages.AltJCoPreferenceSettingsWidget_12,
				this.useCustomConfigSettingsEditorComp);
		
		this.jcoJARFieldEditor.getTextControl(this.useCustomConfigSettingsEditorComp).addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent arg0) {
				if (((AltJCoConfigPage) page).isWidgetsCreated()) {
					((AltJCoConfigPage) page).setHasChanges(true);
				}
			}
		});
		
		this.jcoJARFieldEditor.setPage(page);
		this.jcoJARFieldEditor.setPreferenceStore(store);
		this.jcoJARFieldEditor.setFileExtensions(new String[] { "*.jar" }); //$NON-NLS-1$
		this.jcoJARFieldEditor.load();
		
		if (! this.useInstallerConfigFieldEditor.getBooleanValue()) {
			this.jcoJARFieldEditor.setEnabled(false, this.useCustomConfigSettingsEditorComp);
		}
		
		this.jcoNativeLibFieldEditor = new DirectoryFieldEditor(PreferenceConstants.P_JCO_NATIVE_LIB_DIR, Messages.AltJCoPreferenceSettingsWidget_14,
				this.useCustomConfigSettingsEditorComp);
		
		this.jcoNativeLibFieldEditor.getTextControl(this.useCustomConfigSettingsEditorComp).addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent arg0) {
				if (((AltJCoConfigPage) page).isWidgetsCreated()) {
					((AltJCoConfigPage) page).setHasChanges(true);
				}
			}
		});
		
		this.jcoNativeLibFieldEditor.setPage(page);
		this.jcoNativeLibFieldEditor.setPreferenceStore(store);
		this.jcoNativeLibFieldEditor.load();
		
		if (! this.useInstallerConfigFieldEditor.getBooleanValue()) {
			this.jcoNativeLibFieldEditor.setEnabled(false, this.useCustomConfigSettingsEditorComp);
		}
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
