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


import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.text.MessageFormat;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.ibm.iis.sappack.gen.common.ui.connections.CWDBConnection;
import com.ibm.iis.sappack.gen.common.ui.connections.CWDBConnectionSelectionWidget;
import com.ibm.is.sappack.gen.common.ui.Activator;
import com.ibm.is.sappack.gen.common.ui.Messages;


public class SelectCWDBConnectionWizardPage extends PersistentWizardPageBase {
	protected CWDBConnectionSelectionWidget connectionWidget;


	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}


	public SelectCWDBConnectionWizardPage(String pageName, String title, String description, ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
		this.setDescription(description);
	}

	@Override
	protected Composite createControlImpl(Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayout(new GridLayout(1, false));
		connectionWidget = new CWDBConnectionSelectionWidget(comp, SWT.NONE);
		this.configureTextForProperty(connectionWidget.getConnectionNameText(), "CWDBCONNECTION_NAME"); //$NON-NLS-1$
		connectionWidget.getConnectionNameText().addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				update();
			}
		});

		update();
		return comp;
	}

	@Override
	public boolean nextPressedImpl() {
		final boolean[] result = new boolean[1];
		result[0] = true;
		try {
			this.getContainer().run(false, true, new IRunnableWithProgress() {
				
				@Override
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					CWDBConnection cwdbConn = getCWDBConnection();
					boolean isConnected = cwdbConn.ensureIsConnected();
					if (!isConnected) {
						setErrorMessage(MessageFormat.format(Messages.BrowseBDRForTablesWizard_3, cwdbConn.getName()));
						result[0] = false;
						return;
					}
					Connection conn = cwdbConn.getJDBCConnection();
					if (conn == null) {
						setErrorMessage(Messages.BrowseBDRForTablesWizard_4);
						result[0] = false;
						return;
					}
					result[0] = true;
				}
			});
		} catch (Exception e) {
			Activator.logException(e);
			e.printStackTrace();
			return false;
		} 
		/*
		CWDBConnection cwdbConn = getCWDBConnection();
		boolean isConnected = cwdbConn.ensureIsConnected();
		if (!isConnected) {
			setErrorMessage(MessageFormat.format(Messages.BrowseBDRForTablesWizard_3, cwdbConn.getName()));
			return false;
		}
		Connection conn = cwdbConn.getJDBCConnection();
		if (conn == null) {
			setErrorMessage(Messages.BrowseBDRForTablesWizard_4);
			return false;
		}
		return true;
		*/
		return result[0];
	}

	protected void update() {
		
		/* validate CWDB Connection */
		CWDBConnection conn = getCWDBConnection();
		if(conn == null) {
			setErrorMessage(Messages.SelectCWDBConnectionWizardPage_0);
			setPageComplete(false);
		} else {
			setErrorMessage(null);
			setPageComplete(true);
		}
	}

	public CWDBConnection getCWDBConnection() {
		return this.connectionWidget.getSelectedCWDBConnection();
	}
}
