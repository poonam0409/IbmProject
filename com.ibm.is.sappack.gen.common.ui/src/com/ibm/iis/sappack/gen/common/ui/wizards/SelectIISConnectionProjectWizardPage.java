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


import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import com.ibm.iis.sappack.gen.common.ui.connections.IISConnection;
import com.ibm.iis.sappack.gen.common.ui.util.Utils;
import com.ibm.is.sappack.gen.common.JobGeneratorException;
import com.ibm.is.sappack.gen.common.request.GetAllProjectsRequest;
import com.ibm.is.sappack.gen.common.request.GetAllProjectsResponse;
import com.ibm.is.sappack.gen.common.request.ServerRequestUtil;
import com.ibm.is.sappack.gen.common.ui.Activator;
import com.ibm.is.sappack.gen.common.ui.Messages;


public class SelectIISConnectionProjectWizardPage extends SelectIISConnectionWizardPage {
	private static final String SETTING_DS_PROJECT = "SETTING_DS_PROJECT"; //$NON-NLS-1$

	protected Combo        dsProjectCombo;
	protected List<String> dsProjectList;

	private static String errorMsgTemplate = Messages.InformationServerDetailsPage_6;


	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}


  	public SelectIISConnectionProjectWizardPage(String pageName, String title, String description, ImageDescriptor titleImage) {
		super(pageName, title, description, titleImage);
	}

	@Override
	protected Composite createControlImpl(Composite parent) {
		Composite comp = super.createControlImpl(parent);

		Group dsComposite = new Group(comp, SWT.NONE);
		dsComposite.setText(Messages.InformationServerDetailsPage_13);
		GridLayout dsLayout = new GridLayout(3, false);
		dsComposite.setLayout(dsLayout);
		dsComposite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));

		Label dsProjectLabel = new Label(dsComposite, SWT.NONE);
		dsProjectLabel.setText(Messages.InformationServerDetailsPage_14);
		// final Combo
		this.dsProjectCombo = new Combo(dsComposite, SWT.DROP_DOWN | SWT.BORDER);
		this.dsProjectCombo.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				super.mouseDown(e);

				Runnable connectJob = new Runnable() {
					@Override
					public void run() {
						
						IISConnection conn = getIISConnection();
						boolean passwdSet = conn.ensurePasswordIsSet();
						if(!passwdSet) {
							return;
						}
						
						if (dsProjectList == null) {
							List<String> tempProjectList = new ArrayList<String>();
							String error = getProjects(tempProjectList);

							if (error != null) {
								/* TODO maybe reset IISConnection passed when projects could not be retrieved because of an invalid password */
								MessageDialog.openError(getShell(), Messages.TitleError, error);
								return;
							}
							dsProjectList = tempProjectList;
							// get project name currently set ...
							String curProject = dsProjectCombo.getText();

							dsProjectCombo.removeAll();

							int selIdx = 0;
							int idxCnt = 0;
							if (dsProjectList.size() > 0) {
								Iterator<String> it = dsProjectList.iterator();
								while (it.hasNext()) {
									String project = it.next();
									dsProjectCombo.add(project);

									if (project.equals(curProject)) {
										selIdx = idxCnt;
									}
									idxCnt++;
								}
								dsProjectCombo.select(selIdx);
							}
						}
					}
				};

				BusyIndicator.showWhile(getShell().getDisplay(), connectJob);
			}

		});
		this.dsProjectCombo.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));

		this.configureComboForProperty(dsProjectCombo, SETTING_DS_PROJECT);

		return comp;
	}

	@Override
	protected void updateControlsAfterCreation() {
		super.updateControlsAfterCreation();  // SelectIISConnectionProjectWizardPage
		
		this.dsProjectCombo.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				String errMsg = null;
				if (dsProjectCombo.getText().isEmpty()) {
					errMsg = Messages.SelectIISConnectionProjectWizardPage_0;
				}
				setErrorMessage(errMsg);
				setPageComplete(errMsg == null);
			}
		});
	}

	
	protected String getProjects(List<String> outProjects) {
		GetAllProjectsRequest req = new GetAllProjectsRequest();
		IISConnection conn = this.connectionWidget.getSelectedIISConnection();
		if (conn == null) {
			return null;
		}
		if (!conn.ensurePasswordIsSet()) {
			return null;
		}
		IISConnection.initializeRequest(req, conn);

		GetAllProjectsResponse resp = null;
		try {
			resp = (GetAllProjectsResponse) ServerRequestUtil.send(req);
		} catch (JobGeneratorException e2) {
			e2.printStackTrace();
			String errorMsg = MessageFormat.format(errorMsgTemplate, new Object[] { conn.getDomain(), e2.getLocalizedMessage() });
			Activator.getLogger().log(Level.SEVERE, errorMsg, e2);
			return errorMsg;
		}
		String errorMsg = resp.get1stMessage();
		if (resp.containsErrors()) {
			//			errorMsg = MessageFormat.format(errorMsgTemplate, new Object[] { getIsHost(), errorMsg });
			return errorMsg;
		}
		List<String> projects = resp.getProjects();
		if (projects == null) {
			errorMsg = MessageFormat.format(errorMsgTemplate, new Object[] { conn.getDomain(), Messages.InformationServerDetailsPage_26 });
			return errorMsg;
		}
		outProjects.addAll(projects);
		return null;

	}

	public String getDSProject() {
		return Utils.getText(this.dsProjectCombo);
	}
	
	@Override
	protected void update() {
		
		/* Validate IISConnection */
		super.update();

		/* enable combo box only if we have a valid IISConnection */
		if (this.dsProjectCombo != null) {
			IISConnection conn = getIISConnection();

			if (conn == null) {
				this.dsProjectCombo.setEnabled(false);
			} 
			else {
				this.dsProjectCombo.setEnabled(true);
			}
			
			/* reset DataStage project list */
			dsProjectList = null;
			this.dsProjectCombo.removeAll();
		}

	}

}
