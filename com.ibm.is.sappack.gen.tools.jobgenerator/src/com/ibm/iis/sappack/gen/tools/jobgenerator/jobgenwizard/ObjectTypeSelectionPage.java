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
// Module Name : com.ibm.iis.sappack.gen.tools.jobgenerator.jobgenwizard
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.iis.sappack.gen.tools.jobgenerator.jobgenwizard;


import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.PlatformUI;

import com.ibm.iis.sappack.gen.common.ui.util.Utils;
import com.ibm.iis.sappack.gen.common.ui.wizards.PersistentWizardPageBase;
import com.ibm.iis.sappack.gen.tools.jobgenerator.jobgen.JobGenerationSettings;
import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.common.ui.ModeManager;


public abstract class ObjectTypeSelectionPage extends PersistentWizardPageBase {
	private   Composite abapSubOptionGroup;
	private   Control   firstInputDlgField = null;
	protected Button    checkBoxDataTables;
	protected Button    checkBoxReferenceCheckAndTextTables;
	protected Button    checkBoxNonReferenceCheckTables;
	protected Button    tablesButton;
	protected Button    idocButton;
	private   boolean   enableDataTables;
	private   boolean   enableRefCheckTables;
	private   boolean   enableNonRefCheckTables;
	private   boolean   enableIDocs;
	private   boolean   isTranscoding;


  	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	


	public ObjectTypeSelectionPage() {
		super("objecttypeselectionpage", Messages.ObjectTypeSelectionPage_0, null); //$NON-NLS-1$
		setDescription(Messages.ObjectTypeSelectionPage_1);
	}

	public void setEnablement(boolean enableDataTables, boolean enableRefCheckTables, boolean enableNonRefCheckTables,
		                       boolean enableIDocs, boolean isTranscoding) {
		this.enableDataTables        = enableDataTables;
		this.enableRefCheckTables    = enableRefCheckTables;
		this.enableNonRefCheckTables = enableNonRefCheckTables;
		this.enableIDocs             = enableIDocs;
		this.isTranscoding           = isTranscoding;
		updatePage();
		
		// set focus to the dialog field
	   setInitialFocusToDlgFld(this.firstInputDlgField);
	}

	SelectionListener tableButtonSelectionListener = new SelectionAdapter() {

		@Override
		public void widgetSelected(SelectionEvent e) {

			boolean enabled = tablesButton.getSelection();
			if (ModeManager.isCWEnabled()) {
				checkBoxDataTables.setEnabled(enabled);
				checkBoxNonReferenceCheckTables.setEnabled(enabled);
				checkBoxReferenceCheckAndTextTables.setEnabled(enabled);
			} // end of if (ModeManager.isCWEnabled())

			updatePage();
		}

	};

	@Override
	public Composite createControlImpl(Composite parent) {
		
		Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayout(new GridLayout(1, false));
		comp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		tablesButton = new Button(comp, SWT.CHECK);
		tablesButton.setText(Messages.ObjectTypeSelectionPage_2);
		this.configureCheckBoxForProperty(tablesButton, "CHECKBOX_TABLES"); //$NON-NLS-1$
		tablesButton.addSelectionListener(tableButtonSelectionListener);

		this.abapSubOptionGroup = new Composite(comp, SWT.NONE);
		this.abapSubOptionGroup.setLayout(new GridLayout(1, false));
		this.abapSubOptionGroup.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));

		if (ModeManager.isCWEnabled()) {
			this.checkBoxDataTables = new Button(this.abapSubOptionGroup, SWT.CHECK);
			this.checkBoxDataTables.setText(Messages.CWJobTypeSelectionPage_1);
			this.checkBoxDataTables.addSelectionListener(tableButtonSelectionListener);
			this.configureCheckBoxForProperty(checkBoxDataTables, "CHECKBOX_DATA_TABLES"); //$NON-NLS-1$

			this.checkBoxReferenceCheckAndTextTables = new Button(this.abapSubOptionGroup, SWT.CHECK);
			this.checkBoxReferenceCheckAndTextTables.setText(Messages.CWJobTypeSelectionPage_2);
			this.checkBoxReferenceCheckAndTextTables.addSelectionListener(tableButtonSelectionListener);
			this.configureCheckBoxForProperty(checkBoxReferenceCheckAndTextTables, "CHECK_BOX_CHECK_AND_TEXT_TABLES"); //$NON-NLS-1$

			this.checkBoxNonReferenceCheckTables = new Button(this.abapSubOptionGroup, SWT.CHECK);
			this.checkBoxNonReferenceCheckTables.setText(Messages.CWJobTypeSelectionPage_3);
			this.checkBoxNonReferenceCheckTables.addSelectionListener(tableButtonSelectionListener);
			this.configureCheckBoxForProperty(checkBoxNonReferenceCheckTables, "CHECK_BOX_NON_REF_CHECK_TABLES"); //$NON-NLS-1$
		} // end of if (ModeManager.isCWEnabled()) {

		idocButton = new Button(comp, SWT.CHECK);
		idocButton.setText(Messages.ObjectTypeSelectionPage_3);
		this.configureCheckBoxForProperty(idocButton, "CHECKBOX_IDOCS"); //$NON-NLS-1$
		idocButton.addSelectionListener(tableButtonSelectionListener);

		tableButtonSelectionListener.widgetSelected(null);
		updatePage();

		return comp;
	}

	/**
	 * get the object types as an integer (see class JobGenerationSettings).
	 * @return
	 */
	public int getObjectTypes() {
		int result = 0;

		if (idocButton.getSelection()) {
			result |= JobGenerationSettings.OBJECT_TYPE_IDOC;
		}

		if(tablesButton.getSelection()) {
			if (ModeManager.isCWEnabled()) {
				if (checkBoxDataTables.getSelection()) {
					result |= JobGenerationSettings.OBJECT_TYPE_TABLES_DATA;
				}
				if (checkBoxReferenceCheckAndTextTables.getSelection()) {
					result |= JobGenerationSettings.OBJECT_TYPE_TABLES_REF_AND_TEXT;
				}
				if (checkBoxNonReferenceCheckTables.getSelection()) {
					result |= JobGenerationSettings.OBJECT_TYPE_TABLES_OTHER;
				}
			}
			else {
				// SAPPack mode: if 'Tables' is selected then jobs data tables should be generated 
				if(tablesButton.isEnabled()) {
					result |= JobGenerationSettings.OBJECT_TYPE_TABLES_DATA;
				}
			} // end of (else) if (ModeManager.isCWEnabled())
		}

		return result;
	}

	private void updatePage() {

		boolean enableTables = this.enableDataTables || this.enableRefCheckTables || this.enableNonRefCheckTables;

		this.tablesButton.setEnabled(enableTables);

		if (ModeManager.isCWEnabled()) {
			this.checkBoxDataTables.setEnabled(this.enableDataTables);
			if (!this.enableDataTables) {
				this.checkBoxDataTables.setSelection(false);
			}
			this.checkBoxReferenceCheckAndTextTables.setEnabled(this.enableRefCheckTables);
			if (!this.enableRefCheckTables) {
				this.checkBoxReferenceCheckAndTextTables.setSelection(false);
			}
			this.checkBoxNonReferenceCheckTables.setEnabled(this.enableNonRefCheckTables);
			if (!this.enableNonRefCheckTables) {
				this.checkBoxNonReferenceCheckTables.setSelection(false);
			}

//			if (!enableDataTables) {
//			this.tablesButton.setSelection(false);
//		}
		
			if(!this.tablesButton.getSelection()) {
				this.checkBoxDataTables.setEnabled(false);
				this.checkBoxReferenceCheckAndTextTables.setEnabled(false);
				this.checkBoxNonReferenceCheckTables.setEnabled(false);
			}
		} // end of if (ModeManager.isCWEnabled())

		if (!enableTables) {
			this.tablesButton.setSelection(false);
		}
		
		if (enableIDocs) {
			this.idocButton.setEnabled(true);
		} else {
			this.idocButton.setEnabled(false);
			this.idocButton.setSelection(false);
		}
		
		
		if (!enableTables && !enableIDocs) {
			setErrorMessage(Messages.ObjectTypeSelectionPage_6);
			setPageComplete(false);
		}
		else {

			if (getObjectTypes() == JobGenerationSettings.OBJECT_TYPE_NONE) {
				setErrorMessage(Messages.ObjectTypeSelectionPage_4);
				setPageComplete(false);
			} else if (isTranscoding && idocButton.getSelection()) {
				setErrorMessage(Messages.ObjectTypeSelectionPage_5);
				setPageComplete(false);
			} else {
				setErrorMessage(null);
				setPageComplete(true);
			}

			// determine 1st button control that is enabled
			if (enableTables) {
				this.firstInputDlgField = this.tablesButton;
			}
			else {
				if (enableIDocs) {
					this.firstInputDlgField = this.idocButton;
				}
			}
		}
	}

	@Override
	public void performHelp() {
		PlatformUI.getWorkbench().getHelpSystem().displayHelp(Utils.getHelpID("rgwiz_object_types")); //$NON-NLS-1$
	}

}
