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
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import com.ibm.is.sappack.gen.common.ui.Activator;
import com.ibm.is.sappack.gen.common.ui.Messages;

public class JCoPreferenceSettingsWidget extends Composite {

	static String copyright() {
		return com.ibm.is.sappack.gen.common.ui.preferences.Copyright.IBM_COPYRIGHT_SHORT;
	}

	protected FileFieldEditor jcoJARFieldEditor = null;
	protected MyBooleanFieldEditor useInstallerConfigFieldEditor = null;
	protected DirectoryFieldEditor jcoNativeLibFieldEditor = null;
	private Label jcoSettingsDescLabel = null;
	private Composite useInstallSettingsComposite = null;
	private Composite installerSettingsComposite = null;
	private Composite runtimeComposite = null;
	private Label installJCoJARLabel = null;
	private Label installJCoSharedLibLabel = null;
	private Text installJCoJARText = null;
	private Text installJCoSharedLibText = null;

	/**
	 * You may want to read the following resource before touching the code found herein:
	 * http://gd.tuwien.ac.at/.vhost/www.eclipse.org/articles/Article-Field-Editors/field_editors.html
	 * 
	 * @param parent
	 * @param style
	 */
	public JCoPreferenceSettingsWidget(Composite parent, int style) {
		super(parent, style);
		// TODO Auto-generated constructor stub
		initialize(null, null);
	}

	/**
	 * @param parent
	 * @param style
	 */
	public JCoPreferenceSettingsWidget
	(
		PreferencePage page,
		IPreferenceStore store,
		Composite parent,
		int style
	)
	{
		super(parent, style);
		// TODO Auto-generated constructor stub
		initialize(page, store);
	}
	
	private void initialize(PreferencePage page, IPreferenceStore store) {
		// the "use install settings" composite must be created prior to the remaining composites 
		createUseInstallSettingsComposite(page, store);
		createInstallerSettingsComposite(page, store);
		this.setSize(new Point(856, 434));
		this.setLayout(new GridLayout());
	}

	/**
	 * This method initializes useInstallSettingsComposite	
	 *
	 */
	private void createUseInstallSettingsComposite(PreferencePage page, IPreferenceStore store) {
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = GridData.CENTER;
		useInstallSettingsComposite = new Composite(this, SWT.NONE);
		useInstallSettingsComposite.setLayout(new GridLayout());
		useInstallSettingsComposite.setLayoutData(gridData);
		
		// checkbox field editor
		this.useInstallerConfigFieldEditor = new MyBooleanFieldEditor(PreferenceConstants.P_USE_INSTALLER_CONFIG,
				Messages.JCoPreferenceSettingsWidget_0, this.useInstallSettingsComposite);
		
		Button checkbox = this.useInstallerConfigFieldEditor.getChangeControl(this.useInstallSettingsComposite);
		checkbox.addListener(SWT.Selection, new Listener() {
		      public void handleEvent(Event event) {
		          Button button = (Button) event.widget;
		          
	                if (button.getSelection()) {
	                    button.setSelection(true);
	                	jcoJARFieldEditor.setEnabled(true, runtimeComposite);
	                	jcoNativeLibFieldEditor.setEnabled(true, runtimeComposite);
	                } else {
	                    button.setSelection(false);
	                	jcoJARFieldEditor.setEnabled(false, runtimeComposite);
	                	jcoNativeLibFieldEditor.setEnabled(false, runtimeComposite);
	                }
		        }
		      });
		
		this.useInstallerConfigFieldEditor.setPreferencePage(page);
		this.useInstallerConfigFieldEditor.setPreferenceStore(store);
		this.useInstallerConfigFieldEditor.load();
	}

	/**
	 * This method initializes installerSettingsComposite	
	 *
	 */
	private void createInstallerSettingsComposite(PreferencePage page, IPreferenceStore store) {
		GridData gridData6 = new GridData();
		gridData6.horizontalAlignment = GridData.FILL;
		gridData6.grabExcessHorizontalSpace = true;
		gridData6.grabExcessVerticalSpace = false;
		gridData6.horizontalSpan = 3;
		gridData6.verticalAlignment = GridData.FILL;
		GridData gridData5 = new GridData();
		gridData5.horizontalAlignment = GridData.FILL;
		gridData5.grabExcessHorizontalSpace = true;
		gridData5.verticalAlignment = GridData.CENTER;
		GridData gridData1 = new GridData();
		gridData1.horizontalAlignment = GridData.FILL;
		gridData1.grabExcessHorizontalSpace = true;
		gridData1.verticalAlignment = GridData.CENTER;
		GridData gridData4 = new GridData();
		gridData4.horizontalAlignment = GridData.FILL;
		gridData4.grabExcessHorizontalSpace = true;
		gridData4.grabExcessVerticalSpace = true;
		gridData4.verticalAlignment = GridData.FILL;
		GridData gridData3 = new GridData();
		gridData3.horizontalAlignment = GridData.FILL;
		gridData3.grabExcessHorizontalSpace = true;
		gridData3.grabExcessVerticalSpace = true;
		gridData3.verticalAlignment = GridData.FILL;
		installerSettingsComposite = new Composite(this, SWT.NONE);
		installerSettingsComposite.setLayout(new GridLayout());
		installerSettingsComposite.setLayoutData(gridData3);
		
		ExpandBar bar = new ExpandBar(this.installerSettingsComposite, SWT.V_SCROLL);
		
	    // workspace aka runtime settings
	    runtimeComposite = new Composite(bar, SWT.NONE);
	    GridLayout runtimeLayout = new GridLayout();
	    runtimeLayout.marginLeft = runtimeLayout.marginTop = runtimeLayout.marginRight = runtimeLayout.marginBottom = 10;
	    runtimeLayout.verticalSpacing = 10;
	    runtimeLayout.numColumns = 3;
	    runtimeComposite.setLayout(runtimeLayout);
	    
		jcoSettingsDescLabel = new Label(runtimeComposite, SWT.WRAP);
		jcoSettingsDescLabel.setText(Messages.JCoPreferenceSettingsWidget_1);
		jcoSettingsDescLabel.setLayoutData(gridData6);
		jcoSettingsDescLabel.setToolTipText(Messages.JCoPreferenceSettingsWidget_2);
	    
		this.jcoJARFieldEditor = new FileFieldEditor(PreferenceConstants.P_JCO_JAR_LOCATION, Messages.JCoPreferenceSettingsWidget_3,
				this.runtimeComposite);
		this.jcoJARFieldEditor.setPreferencePage(page);
		this.jcoJARFieldEditor.setPreferenceStore(store);
		this.jcoJARFieldEditor.load();
		
		if (! this.useInstallerConfigFieldEditor.getBooleanValue()) {
			this.jcoJARFieldEditor.setEnabled(false, this.runtimeComposite);
		}
		
		this.jcoNativeLibFieldEditor = new DirectoryFieldEditor(PreferenceConstants.P_JCO_NATIVE_LIB_DIR, Messages.JCoPreferenceSettingsWidget_4,
				this.runtimeComposite);
		this.jcoNativeLibFieldEditor.setPreferencePage(page);
		this.jcoNativeLibFieldEditor.setPreferenceStore(store);
		this.jcoNativeLibFieldEditor.load();
		
		if (! this.useInstallerConfigFieldEditor.getBooleanValue()) {
			this.jcoNativeLibFieldEditor.setEnabled(false, this.runtimeComposite);
		}
	    
	    ExpandItem runtimeItem = new ExpandItem(bar, SWT.NONE, 0);
	    runtimeItem.setText(Messages.JCoPreferenceSettingsWidget_5);
	    runtimeItem.setHeight(runtimeComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
	    runtimeItem.setControl(runtimeComposite);
	    runtimeItem.setExpanded(true);
		
	    // install time settings
	    Composite installComposite = new Composite(bar, SWT.NONE);
	    installJCoJARLabel = new Label(installComposite, SWT.NONE);
	    installJCoJARLabel.setText(Messages.JCoPreferenceSettingsWidget_6);
	    installJCoJARText = new Text(installComposite, SWT.BORDER);
	    installJCoJARText.setEditable(false);
	    installJCoJARText.setText(Activator.getDefault().getJCoJARLocationFromEclipseINI().replaceAll("\"", "")); //$NON-NLS-1$ //$NON-NLS-2$
	    installJCoJARText.setLayoutData(gridData1);
	    installJCoSharedLibLabel = new Label(installComposite, SWT.NONE);
	    installJCoSharedLibLabel.setText(Messages.JCoPreferenceSettingsWidget_9);
	    installJCoSharedLibText = new Text(installComposite, SWT.BORDER);
	    installJCoSharedLibText.setEditable(false);
	    installJCoSharedLibText.setText(Activator.getDefault().getJCoNativeLibDirFromEclipseINI().replaceAll("\"", "")); //$NON-NLS-1$ //$NON-NLS-2$
	    installJCoSharedLibText.setLayoutData(gridData5);
	    GridLayout installLayout = new GridLayout();
	    installLayout.marginLeft = installLayout.marginTop = installLayout.marginRight = installLayout.marginBottom = 10;
	    installLayout.verticalSpacing = 10;
	    installLayout.makeColumnsEqualWidth = false;
	    installLayout.numColumns = 2;
	    installComposite.setLayout(installLayout);
	    
	    ExpandItem installItem = new ExpandItem(bar, SWT.NONE, 0);
	    installItem.setText(Messages.JCoPreferenceSettingsWidget_12);
	    installItem.setHeight(installComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
	    installItem.setControl(installComposite);
	    installItem.setExpanded(true);

	    bar.setSpacing(8);
	    bar.setLayoutData(gridData4);
			runtimeComposite.setSize(new Point(820, 109));
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

}  //  @jve:decl-index=0:visual-constraint="10,10"
