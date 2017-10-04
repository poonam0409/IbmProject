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
// Module Name : com.ibm.iis.sappack.gen.tools.sap.tablelist
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.iis.sappack.gen.tools.sap.tablelist;


import java.sql.Connection;
import java.text.MessageFormat;
import java.util.logging.Level;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;

import com.ibm.iis.sappack.gen.common.ui.wizards.INextActionWizardPage;
import com.ibm.is.sappack.gen.common.ui.Messages;


public abstract class SelectConnectionProfileForCWDBWizardPage extends com.ibm.datatools.sqlxeditor.util.SelectConnectionProfileWizardPage implements INextActionWizardPage {
  	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	


	public SelectConnectionProfileForCWDBWizardPage() {
		super(Messages.ExtractTablesWizard_1);
	}

	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);
		update();
	}

	@Override
	public void handleEvent(Event event) {
		super.handleEvent(event);
		update();
	}

	void update() {
		boolean complete = true;
		setErrorMessage(null);
		if (getSelectedConnection() == null) {
			setErrorMessage(Messages.ExtractTablesWizard_2);
			complete = false;
		} else {
			int connectionState = getSelectedConnection().getConnectionState();
			if (connectionState != IConnectionProfile.CONNECTED_STATE) {
				// TODO
				setErrorMessage("Select a connected database connection");
				complete = false;
			}
		}
		setPageComplete(complete);
	}

	public Connection getJDBCConnection() {
		IConnectionProfile connProf = getSelectedConnection();
		IStatus st = connProf.connect();
		if (!st.isOK()) {
			String msg = Messages.ExtractTablesWizard_4;
			msg = MessageFormat.format(msg, connProf.getName());
			com.ibm.is.sappack.gen.tools.sap.activator.Activator.getLogger().log(Level.WARNING, msg);
			return null;
		}
		Object o = connProf.getManagedConnection("java.sql.Connection").getConnection().getRawConnection(); //$NON-NLS-1$
		if (o instanceof Connection) {
			return (Connection) o;
		}
		return null;
	}
	
}
