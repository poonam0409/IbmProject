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


import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Pattern;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import com.ibm.iis.sappack.gen.common.ui.connections.IISConnection;
import com.ibm.iis.sappack.gen.common.ui.connections.IISConnectionSelectionWidget;
import com.ibm.iis.sappack.gen.common.ui.iisclient.IISClient;
import com.ibm.iis.sappack.gen.common.ui.util.Utils;
import com.ibm.iis.sappack.gen.common.ui.wizards.PersistentWizardPageBase;
import com.ibm.is.sappack.gen.common.Constants;
import com.ibm.is.sappack.gen.common.JobGeneratorException;
import com.ibm.is.sappack.gen.common.request.GetAllFoldersRequest;
import com.ibm.is.sappack.gen.common.request.GetAllFoldersResponse;
import com.ibm.is.sappack.gen.common.request.GetAllProjectsRequest;
import com.ibm.is.sappack.gen.common.request.GetAllProjectsResponse;
import com.ibm.is.sappack.gen.common.request.ServerRequestUtil;
import com.ibm.is.sappack.gen.common.request.ValidateDataRequest;
import com.ibm.is.sappack.gen.common.request.ValidateDataResponse;
import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.common.ui.WidgetIDConstants;
import com.ibm.is.sappack.gen.common.ui.WidgetIDUtils;
import com.ibm.is.sappack.gen.common.ui.util.DSProjectUtils;
import com.ibm.is.sappack.gen.common.util.DSFolder;
import com.ibm.is.sappack.gen.common.util.StringUtils;
import com.ibm.is.sappack.gen.tools.jobgenerator.Activator;
import com.ibm.is.sappack.gen.tools.sap.dialog.FolderTreeDialog;


public class IISDetailsWizardPage extends PersistentWizardPageBase {

	private static final String JOB_NAME_PREFIX = "SAP_Pack_Job_"; //$NON-NLS-1$

	private static final String SETTING_DS_FOLDER = "SETTING_DS_FOLDER"; //$NON-NLS-1$
	private static final String SETTING_DS_PROJECT = "SETTING_DS_PROJECT"; //$NON-NLS-1$

	private IISConnectionSelectionWidget connectionWidget;
	private Combo                        dsProjectCombo;
	private List<String>                 dsProjectList;
	private List<DSFolder>               dsFolderList;
	private String                       dsServerVersion;
	private String                       supportedModelVersion;
	private Label                        emptyLabel;
	private Text                         dsFolderText;
	private Button                       dsFolderSelectButton;
	private Text                         dsJobNamePrefixText;
	private Button                       dsOverWriteJobButton;
	private String                       currentModelVersion;

	private static final String errorMsgTemplate  = Messages.InformationServerDetailsPage_6;


  	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	


	public IISDetailsWizardPage() {
		super("iisconnectionselectionwizardpage", Messages.IISDetailsWizardPage_0, null); //$NON-NLS-1$
		setDescription(Messages.IISDetailsWizardPage_1);
	}

	@Override
	protected Composite createControlImpl(Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);

		ModifyListener modifyListener = new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				update();
			}
		};

		comp.setLayout(new GridLayout(1, false));

		connectionWidget = new IISConnectionSelectionWidget(comp, SWT.NONE);
		this.configureTextForProperty(this.connectionWidget.getConnectionNameText(), getName() + ".IIS_CONNECTION"); //$NON-NLS-1$
		this.connectionWidget.getConnectionNameText().addModifyListener(modifyListener);
		this.connectionWidget.getConnectionNameText().addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				dsProjectList = null;
				dsFolderList = null;
				dsProjectCombo.removeAll();
				dsFolderText.setText(Constants.EMPTY_STRING);

				update();
			}
		});

		Group dsComposite = new Group(comp, SWT.NONE);
		dsComposite.setText(Messages.InformationServerDetailsPage_13);
		GridLayout dsLayout = new GridLayout(3, false);
		dsComposite.setLayout(dsLayout);
		dsComposite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));

		Label dsProjectLabel = new Label(dsComposite, SWT.NONE);
		dsProjectLabel.setText(Messages.InformationServerDetailsPage_14);
		// final Combo
		dsProjectCombo = new Combo(dsComposite, SWT.DROP_DOWN | SWT.BORDER);
		dsProjectCombo.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				super.mouseDown(e);

				Runnable connectJob = new Runnable() {
					@Override
					public void run() {

						if (dsProjectList == null) {
							List<String> tempProjectList = new ArrayList<String>();
							String error = getProjects(tempProjectList);

							if (error != null) {
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
						// clear DSFolder text field box 
						dsFolderText.setText(Constants.EMPTY_STRING);
						dsFolderList = null;
					}
				};

				BusyIndicator.showWhile(getShell().getDisplay(), connectJob);
				update();
			}

		});
		dsProjectCombo.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
		this.configureComboForProperty(dsProjectCombo, SETTING_DS_PROJECT);
		dsProjectCombo.addModifyListener(modifyListener);
		
		WidgetIDUtils.assignID(dsProjectCombo, WidgetIDConstants.GEN_DSPROJECTTEXT);
		emptyLabel = new Label(dsComposite, SWT.NONE);
		emptyLabel.setVisible(false);

		Label dsFolderLabel = new Label(dsComposite, SWT.NONE);
		dsFolderLabel.setText(Messages.InformationServerDetailsPage_16);
		dsFolderText = new Text(dsComposite, SWT.BORDER);
		dsFolderText.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
		dsFolderText.addModifyListener(modifyListener);
		this.configureTextForProperty(dsFolderText, SETTING_DS_FOLDER);
		WidgetIDUtils.assignID(dsFolderText, WidgetIDConstants.GEN_DSFOLDERTEXT);

		dsFolderSelectButton = new Button(dsComposite, SWT.PUSH);
		dsFolderSelectButton.setText(Messages.InformationServerDetailsPage_17);
		dsFolderSelectButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String curFolder;

				// get the folders names from server 
				Runnable fetchFolderJob = new Runnable() {
					@Override
					public void run() {
						// fetch folders from DataStage server

						// get folder name currently set ...
						dsFolderList = new ArrayList<DSFolder>();

						String error = getFolders(dsFolderList);
						if (error != null) {
							MessageDialog.openError(getShell(), Messages.TitleError, error);
							return;
						}
					} // end of run()
				};

				BusyIndicator.showWhile(getShell().getDisplay(), fetchFolderJob);
				curFolder = FolderTreeDialog.getSelectedFolderName(getShell(), dsFolderList, dsFolderText.getText());
				if (curFolder != null) {
					dsFolderText.setText(curFolder);
				}
			} // end of widgetSelected()
		});
		WidgetIDUtils.assignID(dsFolderSelectButton, WidgetIDConstants.GEN_DSFOLDERSELECTBUTTON);

		Label dsJobLabel = new Label(dsComposite, SWT.NONE);
		dsJobLabel.setText(Messages.InformationServerDetailsPage_18);
		dsJobNamePrefixText = new Text(dsComposite, SWT.BORDER);
		dsJobNamePrefixText.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
		dsJobNamePrefixText.addModifyListener(modifyListener);
		emptyLabel = new Label(dsComposite, SWT.NONE);
		emptyLabel.setVisible(false);
		WidgetIDUtils.assignID(dsJobNamePrefixText, WidgetIDConstants.GEN_DSJOBTEXT);

		emptyLabel = new Label(dsComposite, SWT.NONE);
		emptyLabel.setVisible(false);
		dsOverWriteJobButton = new Button(dsComposite, SWT.CHECK);
		dsOverWriteJobButton.setText(Messages.InformationServerDetailsPage_19);
		WidgetIDUtils.assignID(dsOverWriteJobButton, WidgetIDConstants.GEN_DSOVERWRITEJOBBUTTON);

		// set defaults
		dsJobNamePrefixText.setText(JOB_NAME_PREFIX + System.currentTimeMillis());
		dsJobNamePrefixText.selectAll();
		dsOverWriteJobButton.setSelection(false);

		update();

		// set focus to first input field
		setInitialFocusToDlgFld(this.connectionWidget);

		return comp;
	}


	private String getFolders(List<DSFolder> outFolders) {

		GetAllFoldersRequest req = new GetAllFoldersRequest();
		IISConnection conn = this.connectionWidget.getSelectedIISConnection();
		if (conn == null) {
			return null;
		}
		if (!conn.ensurePasswordIsSet()) {
			return null;
		}

		IISConnection.initializeRequest(req, conn);

		/* project name may contain DS Server Name */
		String projectName = Utils.getText(this.dsProjectCombo);

		// update request with project name, DS hostname and DS RPC port 
		// (possibly contained in the project name)
		DSProjectUtils.updateRequestData(projectName, req);

		GetAllFoldersResponse resp = null;
		String errorMsg = null;
		try {
			resp = (GetAllFoldersResponse) ServerRequestUtil.send(req);
		} catch (JobGeneratorException e1) {
			e1.printStackTrace();
			errorMsg = MessageFormat.format(errorMsgTemplate, new Object[] { DSProjectUtils.getHostFromProjectName(projectName, conn.getDomain()), e1.getLocalizedMessage() });
		}

		if (errorMsg == null) {
			// check for error messages
			if (resp.containsErrors()) {
				Activator.getLogger().log(Level.SEVERE, errorMsg);
				Activator.getLogger().log(Level.SEVERE, resp.getDetailedInfo());
				errorMsg = resp.get1stMessage();
			} else {
				List<DSFolder> folders = resp.getFolders();

				if (folders == null) {
					errorMsg = MessageFormat.format(errorMsgTemplate, new Object[] { conn.getDomain(), Messages.InformationServerDetailsPage_25 });
				} else {
					for (Iterator<DSFolder> folderIter = folders.iterator(); folderIter.hasNext();) {
						outFolders.add(folderIter.next());
					}

					this.dsServerVersion = resp.getDSServerVersion();
				} // end of (else) if (folders == null)
			} // end of (else) if (resp.containsErrors())
		} // end of if (errorMsg == null)

		return (errorMsg);
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
		this.dsServerVersion = resp.getDSServerVersion();
		this.supportedModelVersion = resp.getSupportedModelVersion();

		return null;

	}

	void update() {
		setErrorMessage(null);
		boolean error = false;
		String connName = Utils.getText(this.connectionWidget.getConnectionNameText());
		if (!error && connName == null) {
			error = true;
			setErrorMessage(Messages.IISDetailsWizardPage_2);
		}
		
		if (!error) {
			String dsproject = Utils.getText(this.dsProjectCombo);
			if (dsproject == null || dsproject.trim().equals("")) { //$NON-NLS-1$
				error = true;
				setErrorMessage(Messages.IISDetailsWizardPage_3);
			}
		}
		
		if (!error) {
			String dsfolder = Utils.getText(this.dsFolderText);
			if (dsfolder == null) {
				error = true;
				setErrorMessage(Messages.IISDetailsWizardPage_4);
			}
		}

		if (!error) {
			String dsJobNamePrefix = Utils.getText(this.dsJobNamePrefixText);
			if (dsJobNamePrefix == null) {
				error = true;
				setErrorMessage(Messages.IISDetailsWizardPage_5);
			} else if (!(Pattern.compile("^[a-zA-Z][a-zA-Z_0-9]*$").matcher(dsJobNamePrefix).matches())) { //$NON-NLS-1$
				error = true;
				setErrorMessage(Messages.IISDetailsWizardPage_7);
			} else if (dsJobNamePrefix.length() > 200) {
				error = true;
				setErrorMessage(Messages.IISDetailsWizardPage_8);
			}
		}
		setPageComplete(!error);
	}

	public IISConnection getSelectedIISConnection() {
		return this.connectionWidget.getSelectedIISConnection();
	}

	@Override
	public boolean nextPressedImpl() {
		IISConnection conn = getSelectedIISConnection();
		if (conn == null) {
			setErrorMessage(Messages.IISDetailsWizardPage_9);
			return false;
		}
		if (!conn.ensurePasswordIsSet()) {
			return false;
		}
		boolean result = validateProjectAndFolder();
		if (!result) {
			return false;
		}

		// if server has provided the model version ==> check if current model version matches 
		if (supportedModelVersion != null && currentModelVersion != null) {
			try {
				StringUtils.checkModelVersion(supportedModelVersion, currentModelVersion);
			} catch (JobGeneratorException jobGenExcpt) {

				setErrorMessage(MessageFormat.format(Messages.InformationServerDetailsPage_ModelVersionMatchFailed, new Object[] { currentModelVersion, supportedModelVersion }));
				setPageComplete(false);

				return false;
			}
		}

		IISClient iisClient = new IISClient();
		String clientVersion = iisClient.getClientVersion();
		if (!IISClient.matchesVersion(clientVersion, dsServerVersion)) {

			// client doesn't seem to be configured --> tell user 'not configured'
			if (clientVersion == null) {
				clientVersion = Messages.InformationServerDetailsPage_30;
			}

			// wrong version
			String msg = MessageFormat.format(Messages.InformationServerDetailsPage_0, new Object[] { this.dsServerVersion, clientVersion });
			boolean confirmed = MessageDialog.openQuestion(null, Messages.InformationServerDetailsPage_1, msg);
			return confirmed;
		}

		return true;
	}

	private boolean validateProjectAndFolder() {
		boolean validationResult;

		validationResult = true;
		String dsProjectName = getDSProject();
		String dsFolderName = getDSFolder();

		final IISConnection conn = this.getSelectedIISConnection();
		// check if 'project' and/or 'folder' is to be validated on the server ...
		if (dsProjectList == null || dsFolderList == null) {
			final ValidateDataRequest valDataReq = new ValidateDataRequest();
			IISConnection.initializeRequest(valDataReq, conn);

			// insert project name ...
			valDataReq.setDSProjectName(dsProjectName);

			// update request with project name, DS hostname and DS RPC port 
			// (possibly contained in the project name)
			DSProjectUtils.updateRequestData(dsProjectName, valDataReq);

			// and folder name (if required)
			if (dsFolderList == null) {
				valDataReq.setDSFolderName(dsFolderName);
			}

			// also check job name prefix if job may not be replaced ...
			if (!dsOverWriteJobButton.getSelection()) {
				valDataReq.setDSJobNamePrefix(getJobNamePrefix());
			}

			final StringBuffer requestRcAsString = new StringBuffer();

			// process validation request on the server
			Runnable validationJob = new Runnable() {
				@Override
				public void run() {

					ValidateDataResponse result = null;
					try {
						result = (ValidateDataResponse) ServerRequestUtil.send(valDataReq);
						requestRcAsString.append(result.isSuccessful());

						if (result.containsErrors()) {
							setErrorMessage(result.get1stMessage());
							
							if (isPasswordError(result.getMessages())) {
								conn.resetPassword();
							}
						}
						dsServerVersion = result.getDSServerVersion();
						supportedModelVersion = result.getSupportedModelVersion();
					} catch (JobGeneratorException pJobGenExcpt) {
						pJobGenExcpt.printStackTrace();
						String errorMsg = MessageFormat.format(errorMsgTemplate, new Object[] { conn.getDomain(), pJobGenExcpt.getLocalizedMessage() });
						setErrorMessage(errorMsg);
						Activator.getLogger().log(Level.SEVERE, errorMsg, pJobGenExcpt);
						Activator.logException(pJobGenExcpt);
					} catch (Throwable t) {
						System.out.println(t.toString());
						t.printStackTrace();
						Activator.logException(t);
					}
				}
			};

			BusyIndicator.showWhile(getShell().getDisplay(), validationJob);
			validationResult = Boolean.parseBoolean(requestRcAsString.toString());
		} else {

			// project list and folder list have already been loaded

			// ==> check if current project name exists in the project list
			boolean found = DSProjectUtils.doesProjectExistInList(conn.getDomain(), dsProjectName, dsProjectList);

			if (!found) {
				setErrorMessage(MessageFormat.format(errorMsgTemplate,
						new Object[] { DSProjectUtils.getHostFromProjectName(dsProjectName, conn.getDomain()), Messages.InformationServerDetailsPage_27 }));
				validationResult = false;
			} else {
				// ==> check if current folder name exists in the folder list
				if (!DSProjectUtils.doesFolderExistInList(dsFolderName, dsFolderList, true)) {
					//	            if (!dsFolderList.contains(dsFolderName)) {
					setErrorMessage(MessageFormat.format(errorMsgTemplate, new Object[] { conn.getDomain(), MessageFormat.format(Messages.InformationServerDetailsPage_28, dsFolderName) }));
					validationResult = false;
				}
			} // end of (else) if (!found)
		} // end of (else) if (curDSProjectList == null || curDSFolderList == null)

		return (validationResult);
	} // end of validateProjectAndFolder()

	public String getDSFolder() {
		return Utils.getText(dsFolderText);
	}

	public String getDSProject() {
		return Utils.getText(dsProjectCombo);
	}

	public String getJobNamePrefix() {
		return Utils.getText(dsJobNamePrefixText);
	}

	public boolean getOverwriteJob() {
		return this.dsOverWriteJobButton.getSelection();
	}


	/**
	 * This method checks if one of the passed messages is a 'password error' message
	 *
	 * @param msgArr messages (string) array
	 *
	 * @return true if a password error has occurred otherwise false
	 */
	private boolean isPasswordError(String msgArr[]) {
		boolean isPwError;

		isPwError = false;
		for (String curMsg : msgArr) {
			if (curMsg.startsWith("(104600E)")) {   //$NON-NLS-1$
				isPwError = true;
			}
		}
		return(isPwError);
	}

	@Override
	public void performHelp() {
		PlatformUI.getWorkbench().getHelpSystem().displayHelp(Utils.getHelpID("rgwiz_iisdetails")); //$NON-NLS-1$
	}

}
