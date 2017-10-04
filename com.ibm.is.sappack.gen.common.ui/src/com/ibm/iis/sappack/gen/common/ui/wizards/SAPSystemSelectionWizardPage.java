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
// Module Name : com.ibm.iis.sappack.gen.common.ui.wizards
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.iis.sappack.gen.common.ui.wizards;


import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.ibm.iis.sappack.gen.common.ui.connections.PasswordDialog;
import com.ibm.iis.sappack.gen.common.ui.connections.SAPConnectionSelectionWidget;
import com.ibm.iis.sappack.gen.common.ui.connections.SapSystem;
import com.ibm.iis.sappack.gen.common.ui.util.Utils;
import com.ibm.is.sappack.gen.common.ui.Messages;


public class SAPSystemSelectionWizardPage extends PersistentWizardPageBase {
	private static final String SETTING_SAP_SYSTEM_NAME = ".settingSAPSystemName"; //$NON-NLS-1$

	private SAPConnectionSelectionWidget sapConnWidget;

  	
	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	


  	public SAPSystemSelectionWizardPage() {
		super("sapsystemselectionwizardpage", Messages.SAPSystemSelectionWizardPage_0, null); //$NON-NLS-1$
		setDescription(Messages.SAPSystemSelectionWizardPage_1);
	}

	@Override
	public Composite createControlImpl(Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayout(new GridLayout(1, false));
		comp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		this.sapConnWidget = new SAPConnectionSelectionWidget(comp, SWT.NONE);
	//	this.sapConnWidget.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false));
		this.configureTextForProperty(this.sapConnWidget.getSAPConnectionNameText(), SETTING_SAP_SYSTEM_NAME);
		this.sapConnWidget.getSAPConnectionNameText().addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				updatePage();
			}
		});
	//	updatePage();
		return comp;
	}

	public SapSystem getSelectedSAPSystem() {
		return this.sapConnWidget.getSelectedSAPSystem();
	}

	void updatePage() {
		if (Utils.getText(this.sapConnWidget.getSAPConnectionNameText()) == null) {
			setErrorMessage(Messages.SAPSystemSelectionWizardPage_2);
			setPageComplete(false);
		} else {
			setErrorMessage(null);
			setPageComplete(true);
			//	saveSettings();
		}

	}

	@Override
	public boolean nextPressedImpl() {
		SapSystem sap = getSelectedSAPSystem();
		if (sap == null) {
			setErrorMessage(Messages.SAPSystemSelectionWizardPage_3);
			return false;
		}
		boolean pwIsOK = PasswordDialog.checkForPassword(getShell(), sap);
		if (!pwIsOK) {
			setErrorMessage(Messages.SAPSystemSelectionWizardPage_4);
		} 
		return pwIsOK;
	}

}
