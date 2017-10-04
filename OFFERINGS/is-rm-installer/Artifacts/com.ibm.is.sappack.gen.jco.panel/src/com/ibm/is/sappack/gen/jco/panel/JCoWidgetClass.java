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
package com.ibm.is.sappack.gen.jco.panel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import com.ibm.cic.agent.core.api.ILogger;
import com.ibm.cic.agent.core.api.IMLogger;
import com.ibm.cic.agent.ui.extensions.CustomPanel;
import com.ibm.is.sappack.gen.jco.panel.internal.Messages;

/**
 * @author dsh
 *
 */
public class JCoWidgetClass extends Composite {
	
	private final ILogger log = IMLogger.getLogger(com.ibm.is.sappack.gen.jco.panel.JCoWidgetClass.class);
	
	private FormToolkit formToolkit = null;   //  @jve:decl-index=0:visual-constraint=""
	private Section jcoSettingsSection = null;
	private Section jcoSettingsValidationSection = null;
	private Section jcoInformationSection = null;
	private ScrolledForm jcoSettingsForm = null;
	private ScrolledForm jcoSettingsValidationForm = null;
	private ScrolledForm jcoInformationForm = null;
	private JCoArtifactChooserWidget chooser = null;
	private JCoSettingsValidationWidget validator = null;
	private JCoInformationWidget info = null;
	
	private boolean jcoJARLocationIsValid = false;
	private boolean jcoNativeDirectoryIsValid = false;
	private boolean validationSucceeded = false;
	
	private String selectedDirectory;
	private String selectedJavaArchive;
	private JCoClass myPanel = null;
	
	static String copyright() {
		return com.ibm.is.sappack.gen.jco.panel.Copyright.IBM_COPYRIGHT_SHORT;
	}
    
	
	/**
	 * @param parent
	 * @param style
	 */
	public JCoWidgetClass(Composite parent, int style) {
		super(parent, style);
		initialize();
	}
	
	public JCoWidgetClass(JCoClass panel, Composite parent, FormToolkit toolkit, int style) {
		super(parent, style);
		this.myPanel = panel;
		this.formToolkit = toolkit;
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
		GridLayout gridLayout2 = new GridLayout();
		gridLayout2.horizontalSpacing = 5;
		gridLayout2.marginWidth = 10;
		gridLayout2.marginHeight = 10;
		gridLayout2.numColumns = 1;
		gridLayout2.verticalSpacing = 5;
		this.setLayout(gridLayout2);
		createJcoSettingsSection();
		createJcoSettingsValidationSection();
		createJcoInformationSection();
		this.setSize(new Point(550, 644));
	}

	/**
	 * This method initializes formToolkit	
	 * 	
	 * @return org.eclipse.ui.forms.widgets.FormToolkit	
	 */
	private FormToolkit getFormToolkit() {
		if (this.formToolkit == null) {
			this.formToolkit = new FormToolkit(Display.getCurrent());
		}
		return this.formToolkit;
	}

	/**
	 * This method initializes jcoSettingsSection	
	 *
	 */
	private void createJcoSettingsSection() {
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalSpan = 5;
		gridData.verticalAlignment = GridData.CENTER;
		jcoSettingsSection = getFormToolkit().createSection(
				this,
				ExpandableComposite.TWISTIE | Section.DESCRIPTION
						| ExpandableComposite.TITLE_BAR);
		jcoSettingsSection.setExpanded(true);
		jcoSettingsSection.setText(Messages.JCoWidgetClass_0); //$NON-NLS-1$
		jcoSettingsSection.setDescription(Messages.JCoWidgetClass_1); //$NON-NLS-1$
		createJcoSettingsForm();
		jcoSettingsSection.setLayoutData(gridData);
		jcoSettingsSection.setClient(jcoSettingsForm);
	}

	/**
	 * This method initializes jcoSettingsValidationSection	
	 *
	 */
	private void createJcoSettingsValidationSection() {
		GridData gridData2 = new GridData();
		gridData2.grabExcessHorizontalSpace = true;
		gridData2.verticalAlignment = GridData.CENTER;
		gridData2.horizontalAlignment = GridData.FILL;
		jcoSettingsValidationSection = getFormToolkit().createSection(this, ExpandableComposite.TWISTIE | Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
		jcoSettingsValidationSection.setExpanded(true);
		jcoSettingsValidationSection.setText(Messages.JCoWidgetClass_2); //$NON-NLS-1$
		jcoSettingsValidationSection.setDescription(Messages.JCoWidgetClass_3); //$NON-NLS-1$
		createJcoSettingsValidationForm();
		jcoSettingsValidationSection.setLayoutData(gridData2);
		jcoSettingsValidationSection.setClient(jcoSettingsValidationForm);
	}

	/**
	 * This method initializes jcoInformationSection	
	 *
	 */
	private void createJcoInformationSection() {
		GridData gridData3 = new GridData();
		gridData3.horizontalAlignment = GridData.FILL;
		gridData3.grabExcessHorizontalSpace = true;
		gridData3.grabExcessVerticalSpace = true;
		gridData3.verticalAlignment = GridData.FILL;
		jcoInformationSection = getFormToolkit().createSection(
				this,
				ExpandableComposite.TWISTIE | Section.DESCRIPTION
						| ExpandableComposite.TITLE_BAR);
		jcoInformationSection.setExpanded(true);
		jcoInformationSection.setText(Messages.JCoWidgetClass_4); //$NON-NLS-1$
		jcoInformationSection.setDescription(Messages.JCoWidgetClass_5); //$NON-NLS-1$
		createJcoInformationForm();
		jcoInformationSection.setLayoutData(gridData3);
		jcoInformationSection.setClient(jcoInformationForm);
	}

	/**
	 * This method initializes jcoSettingsForm	
	 *
	 */
	private void createJcoSettingsForm() {
		GridData gridData1 = new GridData();
		gridData1.horizontalAlignment = GridData.FILL;
		gridData1.grabExcessHorizontalSpace = true;
		gridData1.grabExcessVerticalSpace = true;
		gridData1.verticalAlignment = GridData.FILL;
		jcoSettingsForm = getFormToolkit().createScrolledForm(jcoSettingsSection);
		jcoSettingsForm.getBody().setLayout(new GridLayout());
		
		chooser = new JCoArtifactChooserWidget(this, jcoSettingsForm.getBody(), getFormToolkit(), SWT.NONE);
		chooser.setLayoutData(gridData1);
	}

	/**
	 * This method initializes jcoSettingsValidationForm	
	 *
	 */
	private void createJcoSettingsValidationForm() {
		GridData gridData4 = new GridData();
		gridData4.horizontalAlignment = GridData.FILL;
		gridData4.grabExcessHorizontalSpace = true;
		gridData4.grabExcessVerticalSpace = true;
		gridData4.verticalAlignment = GridData.FILL;
		jcoSettingsValidationForm = getFormToolkit().createScrolledForm(
				jcoSettingsValidationSection);
		jcoSettingsValidationForm.getBody().setLayout(new GridLayout());
		
		validator = new JCoSettingsValidationWidget(
				this,
				jcoSettingsValidationForm.getBody(),
				getFormToolkit(),
				this.chooser.getSelectedJavaArchive(),
				this.chooser.getSelectedDirectory(),
				this.chooser.isJcoJARLocationIsValid(),
				this.chooser.isJcoNativeDirectoryIsValid(),
				SWT.NONE);
		validator.setLayoutData(gridData4);
	}

	/**
	 * This method initializes jcoInformationForm	
	 *
	 */
	private void createJcoInformationForm() {
		GridData gridData5 = new GridData();
		gridData5.horizontalAlignment = GridData.FILL;
		gridData5.grabExcessHorizontalSpace = true;
		gridData5.grabExcessVerticalSpace = true;
		gridData5.verticalAlignment = GridData.FILL;
		jcoInformationForm = getFormToolkit().createScrolledForm(
				jcoInformationSection);
		jcoInformationForm.getBody().setLayout(new GridLayout());
		
		info = new JCoInformationWidget(
				this,
				jcoInformationForm.getBody(),
				getFormToolkit(),
				this.chooser.getSelectedJavaArchive(),
				this.chooser.getSelectedDirectory(), 
				SWT.NONE);
		info.setLayoutData(gridData5);
	}

	/**
	 * @return the jcoJARLocationIsValid
	 */
	public boolean isJcoJARLocationIsValid() {
		return jcoJARLocationIsValid;
	}

	/**
	 * @param jcoJARLocationIsValid the jcoJARLocationIsValid to set
	 */
	public void setJcoJARLocationIsValid(boolean jcoJARLocationIsValid) {
		this.jcoJARLocationIsValid = jcoJARLocationIsValid;
	}

	/**
	 * @return the jcoNativeDirectoryIsValid
	 */
	public boolean isJcoNativeDirectoryIsValid() {
		return jcoNativeDirectoryIsValid;
	}

	/**
	 * @param jcoNativeDirectoryIsValid the jcoNativeDirectoryIsValid to set
	 */
	public void setJcoNativeDirectoryIsValid(boolean jcoNativeDirectoryIsValid) {
		this.jcoNativeDirectoryIsValid = jcoNativeDirectoryIsValid;
	}

	/**
	 * @return the selectedDirectory
	 */
	public String getSelectedDirectory() {
		return selectedDirectory;
	}

	/**
	 * @param selectedDirectory the selectedDirectory to set
	 */
	public void setSelectedDirectory(String selectedDirectory) {
		this.selectedDirectory = selectedDirectory;
	}

	/**
	 * @return the selectedJavaArchive
	 */
	public String getSelectedJavaArchive() {
		return selectedJavaArchive;
	}

	/**
	 * @param selectedJavaArchive the selectedJavaArchive to set
	 */
	public void setSelectedJavaArchive(String selectedJavaArchive) {
		this.selectedJavaArchive = selectedJavaArchive;
	}

	/**
	 * @return the validator
	 */
	protected JCoSettingsValidationWidget getValidator() {
		return validator;
	}

	/**
	 * @return the info
	 */
	protected JCoInformationWidget getInfo() {
		return info;
	}

	/**
	 * @return the validationSucceeded
	 */
	protected boolean isValidationSucceeded() {
		return validationSucceeded;
	}

	/**
	 * @param validationSucceeded the validationSucceeded to set
	 */
	protected void setValidationSucceeded(boolean validationSucceeded) {
		this.validationSucceeded = validationSucceeded;
	}

	/**
	 * @return the myPanel
	 */
	protected JCoClass getMyPanel() {
		return myPanel;
	}
}  //  @jve:decl-index=0:visual-constraint="10,10"
