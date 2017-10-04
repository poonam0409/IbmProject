//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2013, 2014                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.iis.sappack.gen.common.ui.connections
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.iis.sappack.gen.common.ui.connections;


import java.text.MessageFormat;

import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.ibm.iis.sappack.gen.common.ui.util.Utils;
import com.ibm.is.sappack.gen.common.ui.Messages;


public class EditCWDBConnectionWizardPage extends WizardPage {
	private Text name;
	private Combo connectionProfile;
	private String connectionName;
	private IConnectionProfile[] profs;


  	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	


	public EditCWDBConnectionWizardPage(String connectionName) {
		super("editcwdbconnectionwizardpage"); //$NON-NLS-1$
		this.connectionName = connectionName;
		this.setTitle(Messages.EditCWDBConnectionWizardPage_0);
		this.setDescription(Messages.EditCWDBConnectionWizardPage_1);
	}

	@Override
	public void createControl(Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayout(new GridLayout(2, false));
		GridData gd = new GridData();
		gd.horizontalAlignment = GridData.HORIZONTAL_ALIGN_BEGINNING;
		
		Label l = new Label(comp, SWT.NONE);
		l.setText(Messages.EditCWDBConnectionWizardPage_2);

		ModifyListener ml = new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				update();
			}
		};
		boolean newConnection = this.connectionName == null;
		int options = SWT.BORDER;
		if (!newConnection) {
			options |= SWT.READ_ONLY;
		}
		name = new Text(comp, options);
		name.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.name.addModifyListener(ml);
		
		l = new Label(comp, SWT.NONE);
		l.setText(Messages.EditCWDBConnectionWizardPage_3);
		
		profs = CWDBConnection.getAllConnectionProfiles();
		
		
		CWDBConnection connection = null;
		if (this.connectionName != null) {
			CWDBConnectionRepository rep = CWDBConnectionRepository.getRepository();
			connection = rep.getConnection(this.connectionName);
		}
		if (connection != null) {
			this.name.setText(connectionName);
		}
		
		
		String[] items = new String[profs.length];
		int ix = -1;
		for (int i=0; i<profs.length; i++) {
//			this.connections.add(new CWDBConnection(p.getName(), p));
			items[i] = profs[i].getName();
			if (ix == -1 && connection != null) {
				if (profs[i].equals(connection.getProfile())) {
					ix = i;
				}
			}
		}
		connectionProfile = new Combo(comp, SWT.DROP_DOWN | SWT.READ_ONLY);
		connectionProfile.setItems(items);
		connectionProfile.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		connectionProfile.addModifyListener(ml);
		if (ix > -1) {
			connectionProfile.select(ix);
		} else {
			connectionProfile.select(0);
		}
		
		update();
		setControl(comp);
		
	}
	
	void update() {
		boolean complete = true;
		setErrorMessage(null);
		if (complete && Utils.getText(this.name) == null) {
			complete = false;
			setErrorMessage(Messages.EditCWDBConnectionWizardPage_4);
		}
		// if new connection
		if (this.connectionName == null) {
			if (complete) {
				String name = Utils.getText(this.name);
				if (CWDBConnectionRepository.getRepository().getConnection(name) != null) {
					setErrorMessage(MessageFormat.format(Messages.EditCWDBConnectionWizardPage_5, name));
					complete = false;
				}
			}
		}
		setPageComplete(complete);
	}
	
	public void save() {
		CWDBConnection connection = null;
		int ix = this.connectionProfile.getSelectionIndex();
		IConnectionProfile prof = profs[ix];
		if (this.connectionName == null) {
			connection = CWDBConnectionRepository.getRepository().createNewConnection(Utils.getText(name), prof);
		} else {
			connection = CWDBConnectionRepository.getRepository().getConnection(Utils.getText(name));
			connection.setProfile(prof);
		}
		CWDBConnectionRepository.getRepository().save();
	}

}
