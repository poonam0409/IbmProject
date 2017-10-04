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


import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.ibm.iis.sappack.gen.common.ui.connections.IISConnection;
import com.ibm.iis.sappack.gen.common.ui.connections.IISConnectionSelectionWidget;
import com.ibm.is.sappack.gen.common.ui.Messages;


public class SelectIISConnectionWizardPage extends PersistentWizardPageBase {
	protected IISConnectionSelectionWidget connectionWidget;


	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}


  	public SelectIISConnectionWizardPage(String pageName, String title, String description, ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
		this.setDescription(description);
	}

	@Override
	protected Composite createControlImpl(Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayout(new GridLayout(1, false));
		this.connectionWidget = new IISConnectionSelectionWidget(comp, SWT.NONE);
		this.configureTextForProperty(this.connectionWidget.getConnectionNameText(), "CONNECTION_NAME"); //$NON-NLS-1$

		return comp;
	}

	@Override
	protected void updateControlsAfterCreation() {
		this.connectionWidget.getConnectionNameText().addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				update();
			}
		});

// commented in since it forces an unexpected update of the projects combo box in class "SelectIISConnectionProjectWizardPage" 		
//		update();
	}

	
	@Override
	public boolean nextPressedImpl() {
		return true;
	}

	protected void update() {
		
		/* validate IISConnection */
		IISConnection conn = getIISConnection();
		if(conn == null) {
			setErrorMessage(Messages.SelectIISConnectionWizardPage_0);
			setPageComplete(false);
		} else {
			setErrorMessage(null);
			setPageComplete(true);
		}
	}

	public IISConnection getIISConnection() {
		return this.connectionWidget.getSelectedIISConnection();
	}
}
