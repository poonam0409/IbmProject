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

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.ibm.iis.sappack.gen.common.ui.iisclient.IISClient;
import com.ibm.iis.sappack.gen.common.ui.iisclient.IISClient.IISClientVersion;
import com.ibm.iis.sappack.gen.common.ui.preferences.IISPreferencePage;
import com.ibm.iis.sappack.gen.common.ui.util.Utils;
import com.ibm.is.sappack.gen.common.Constants;
import com.ibm.is.sappack.gen.common.request.GetAllProjectsRequest;
import com.ibm.is.sappack.gen.common.request.GetAllProjectsResponse;
import com.ibm.is.sappack.gen.common.request.ResponseBase;
import com.ibm.is.sappack.gen.common.request.ServerRequestUtil;
import com.ibm.is.sappack.gen.common.ui.Activator;
import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.common.ui.util.LargeMessageDialog;
import com.ibm.is.sappack.gen.common.util.ServerMessageCatalog;


public class EditIISConnectionWizardPage extends WizardPage {

	private String                  connectionName;
	private boolean                 newConnection;
	private Text                    nameText;
	private Text                    domainText;
	private Button                  useHTTPsButton;
	private Text                    httpsPortText;
	private Text                    userText;
	private Text                    passwordText;
	private IISConnectionRepository iisConnRep;

	private IISConnection           currentConnection;
	private Button                  testConnectionButton;


  	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	


	public EditIISConnectionWizardPage(String connectionName) {
		super("editiisconnectionwizardpage"); //$NON-NLS-1$
		setTitle(Messages.EditIISConnectionWizardPage_0);
		setDescription(Messages.EditIISConnectionWizardPage_1);
		this.connectionName = connectionName;
		this.newConnection = connectionName == null;

		this.iisConnRep = IISConnectionRepository.getRepository();

		if (this.connectionName != null) {
			this.currentConnection = iisConnRep.retrieveConnection(connectionName);
		}
	}

	public void createControl(final Composite parent) {
		Composite pageComposite;

		initializeDialogUnits(parent);

		ScrolledComposite scrollComposite = new ScrolledComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL);
		scrollComposite.setExpandVertical(true);
		scrollComposite.setExpandHorizontal(true);

		// create the page controls
		pageComposite = createPageControls(scrollComposite);
		setControl(scrollComposite);
		scrollComposite.setContent(pageComposite);

		// last not least set the composite minimum size when the scroll bars are to be shown
		pageComposite.setSize(pageComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		scrollComposite.setMinSize(pageComposite.getSize());
	}

	public Composite createPageControls(Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayout(new GridLayout(1, false));
		comp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		ModifyListener ml = new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				update();
			}
		};

		GridData textGD = new GridData(SWT.FILL, SWT.TOP, true, false);

		Composite nameComp = new Composite(comp, SWT.NONE);
		nameComp.setLayout(new GridLayout(2, false));
		nameComp.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		Label l = new Label(nameComp, SWT.NONE);
		l.setText(Messages.EditIISConnectionWizardPage_2);

		this.nameText = new Text(nameComp, SWT.BORDER);
		this.nameText.setLayoutData(textGD);
		if (connectionName != null) {
			this.nameText.setText(connectionName);
		}
		//		nameText.setEnabled(this.newConnection);
		this.nameText.addModifyListener(ml);
		this.nameText.setEnabled(newConnection);

		Group serverGroup = new Group(comp, SWT.NONE);
		serverGroup.setLayout(new GridLayout(2, false));
		serverGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		serverGroup.setText(Messages.EditIISConnectionWizardPage_3);

		l = new Label(serverGroup, SWT.NONE);
		l.setText(Messages.EditIISConnectionWizardPage_4);

		this.domainText = new Text(serverGroup, SWT.BORDER);
		this.domainText.setLayoutData(textGD);
		this.domainText.addModifyListener(ml);

		this.useHTTPsButton = new Button(serverGroup, SWT.CHECK);
		this.useHTTPsButton.setText(Messages.EditIISConnectionWizardPage_5);
		this.useHTTPsButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				update();
			}

		});

		new Label(serverGroup, SWT.NONE);

		l = new Label(serverGroup, SWT.NONE);
		l.setText(Messages.EditIISConnectionWizardPage_6);
		this.httpsPortText = new Text(serverGroup, SWT.BORDER);
		this.httpsPortText.setLayoutData(textGD);
		this.httpsPortText.setText(String.valueOf(Constants.JOB_GEN_HTTPS_IS_SERVER_PORT));
		this.httpsPortText.addModifyListener(ml);

		Group credentialsGroup = new Group(comp, SWT.NONE);
		credentialsGroup.setText(Messages.EditIISConnectionWizardPage_7);
		credentialsGroup.setLayout(new GridLayout(2, false));
		credentialsGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		l = new Label(credentialsGroup, SWT.NONE);
		l.setText(Messages.EditIISConnectionWizardPage_8);

		this.userText = new Text(credentialsGroup, SWT.BORDER);
		this.userText.setLayoutData(textGD);
		this.userText.addModifyListener(ml);

		l = new Label(credentialsGroup, SWT.NONE);
		l.setText(Messages.EditIISConnectionWizardPage_9);

		this.passwordText = new Text(credentialsGroup, SWT.BORDER | SWT.PASSWORD);
		this.passwordText.setLayoutData(textGD);
		this.passwordText.addModifyListener(ml);

		this.testConnectionButton = new Button(comp, SWT.PUSH);
		this.testConnectionButton.setText(Messages.EditIISConnectionWizardPage_10);
		this.testConnectionButton.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false));

		this.testConnectionButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				String errorMessage = null;
				try {
					GetAllProjectsRequest request = new GetAllProjectsRequest();
					Object[] domainAndPort = getDomainStringAndPort();
					if (domainAndPort == null) {
						errorMessage = Messages.EditIISConnectionWizardPage_11;
					}
					else {
						String domain = (String) domainAndPort[0];
						int port = (Integer) domainAndPort[1];

						request.setDomainServerName(domain);
						request.setDomainServerPort(port);
						request.setISUsername(Utils.getText(EditIISConnectionWizardPage.this.userText));
						request.setISPassword(Utils.getText(EditIISConnectionWizardPage.this.passwordText));
						if (EditIISConnectionWizardPage.this.useHTTPsButton.getSelection()) {
							request.setHTTPSPort(Integer.parseInt(EditIISConnectionWizardPage.this.httpsPortText.getText()));
						}

						request.setIISClientLocation(IISPreferencePage.getIISClientDirectory());

						ResponseBase response = ServerRequestUtil.send(request);
						GetAllProjectsResponse gapResponse = (GetAllProjectsResponse) response;
						if (gapResponse.containsErrors()) {
							errorMessage = gapResponse.get1stMessage();
						}
					}
				} catch (Exception exc) {
					Activator.logException(exc);
					errorMessage = exc.getLocalizedMessage();
					if (errorMessage == null) {
						errorMessage = exc.getClass().getName();
					}
				}
				if (errorMessage == null) {
					MessageDialog.openInformation(getShell(), Messages.EditIISConnectionWizardPage_12, Messages.EditIISConnectionWizardPage_13);
				} else {
					LargeMessageDialog.openLargeMessageDialog(getShell(), Messages.EditIISConnectionWizardPage_14, Messages.EditIISConnectionWizardPage_15, errorMessage);
				}
			}

		});

		restoreValues();
		update();

		// Since v7.1 and IS Picasso (> 9.x): SSL is mandatory
		// ***************************************************
		if (ServerRequestUtil.isIISServerConnectionLibsUsed()) {
			if (IISClient.getVersion() == IISClientVersion.v11xOrLater) {
				this.useHTTPsButton.setEnabled(false);
			}
		}
		// *****************************************************

		return comp;
	}

	private void restoreValues() {
		boolean useSSL = true;   // SSL is recommended

		if (this.currentConnection != null) {
			Utils.setText(this.userText, this.currentConnection.getUser());
			Utils.setText(this.passwordText, this.currentConnection.getPassword());

			// Since v7.1 : For non-IS Picasso installations: SSL is not mandatory
			// *******************************************************************
			if (! (ServerRequestUtil.isIISServerConnectionLibsUsed() || IISClient.getVersion() == IISClientVersion.v11xOrLater)) {
				useSSL = this.currentConnection.useHTTPS();
			}

			String domainServerAndPort;
			if (useSSL) {
				Utils.setText(this.httpsPortText, String.valueOf(this.currentConnection.getHTTPSPort()));
				domainServerAndPort = this.currentConnection.getDomain();
			}
			else {
				domainServerAndPort = MessageFormat.format(ServerRequestUtil.DOMAIN_PORT_TEMPLATE,
                                                           new Object[] { this.currentConnection.getDomain(),
			                                                              String.valueOf(this.currentConnection.getDomainServerPort()) } );
			}
			this.domainText.setText(domainServerAndPort);
		}
		this.useHTTPsButton.setSelection(useSSL);
	}

	private boolean isValidConnection(String name) {
		boolean found = this.iisConnRep.retrieveConnection(name) != null;
		if (this.newConnection) {
			return !found;
		} else {
			return found;
		}
	}

	Object[] getDomainStringAndPort() {
		String dom = Utils.getText(this.domainText);
		if (dom == null) {
			return null;
		}
		int    port = Constants.JOB_GEN_DEFAULT_IS_SERVER_PORT;
		String portString = String.valueOf(port);
		try {
			int ix = dom.indexOf(ServerRequestUtil.DOMAIN_PORT_SEPARATOR);
			if (ix > -1) {
				portString = dom.substring(ix + 1);
				dom = dom.substring(0, ix);
			}

			port = Integer.parseInt(portString);
		}
		catch (Exception exc) {
			Activator.logException(exc);
			return null;
		}
		Object[] result = new Object[2];
		result[0] = dom;
		result[1] = (Integer) port;

		return result;
	}

	void update() {
		setErrorMessage(null);
		setPageComplete(true);
		boolean error = false;
		boolean useHTTPS = this.useHTTPsButton.getSelection();
		this.httpsPortText.setEnabled(useHTTPS);

		String name = Utils.getText(this.nameText);
		String[] msg = new String[0];
		if (name == null) {
			setErrorMessage(Messages.EditIISConnectionWizardPage_16);
			error = true;
			setPageComplete(false);
		}
		if (!error && newConnection && !isValidConnection(name)) {
			setErrorMessage(MessageFormat.format(Messages.EditIISConnectionWizardPage_17, name));
			error = true;
			setPageComplete(false);
		}

		if (!error) {
			String iisClientDir = IISPreferencePage.getIISClientDirectory();
			if (useHTTPS && (iisClientDir == null || iisClientDir.isEmpty())) {
				error = true;
				setErrorMessage(Messages.EditIISConnectionWizardPage_18);
			}
		}

		if (!error && !Utils.isValidConnectionName(name, msg)) {
			setErrorMessage(msg[0]);
			error = true;
		}
		if (!error && Utils.getText(this.domainText) == null) {
			setErrorMessage(Messages.EditIISConnectionWizardPage_19);
			error = true;
		}
		if (!error && getDomainStringAndPort() == null) {
			setErrorMessage(Messages.EditIISConnectionWizardPage_20);
			error = true;
		}

		if (!error && Utils.getText(this.userText) == null) {
			setErrorMessage(Messages.EditIISConnectionWizardPage_21);
			error = true;
		}

		if (!error && this.useHTTPsButton.getSelection()) {
			String httpsPortStr = Utils.getText(this.httpsPortText);
			if (httpsPortStr == null) {
				setErrorMessage(Messages.EditIISConnectionWizardPage_22);
				error = true;
			} else {
				try {
					Integer.parseInt(httpsPortStr);
				} catch (NumberFormatException nfe) {
					setErrorMessage(Messages.EditIISConnectionWizardPage_23);
					error = true;
				}
			}
		}

		setPageComplete(!error);
		getContainer().updateButtons();
		if (error) {
			this.testConnectionButton.setEnabled(false);
		} else {
			this.testConnectionButton.setEnabled(Utils.getText(this.passwordText) != null);
		}
	}

	public void save() {
		IISConnection conn = this.currentConnection;
		boolean newConnection = (this.currentConnection == null);
		if (newConnection) {
			conn = this.iisConnRep.createNewConnection(Utils.getText(this.nameText));
		}
		Object[] domainAndPort = getDomainStringAndPort();
		if (domainAndPort != null) {
			String domainString = (String) domainAndPort[0];
			int port = (Integer) domainAndPort[1];
			conn.setDomain(domainString);
			conn.setDomainServerPort(port);
		}
		conn.setUser(Utils.getText(this.userText));
		conn.setPassword(Utils.getText(this.passwordText));
		boolean useHTTPS = this.useHTTPsButton.getSelection();
		conn.setUseHTTPS(useHTTPS);
		if (useHTTPS) {
			conn.setHTTPSPort(Integer.valueOf(Utils.getText(this.httpsPortText)));
		} else {
			conn.setHTTPSPort(Constants.JOB_GEN_HTTPS_IS_SERVER_PORT);
		}

		if (newConnection) {
			this.iisConnRep.add(conn);
		}
		this.iisConnRep.save();
	}
	
	public void setVisible(boolean visible) {
		super.setVisible(visible);

		if (visible) {
			if (IISClient.getVersion() == IISClientVersion.v11xOrLater && !ServerRequestUtil.isIISServerConnectionLibsUsed()) {
				// IISServerConnectionLibs are not used but they are required for a IS Picasso installation
				String errMsg = ServerMessageCatalog.getDefaultCatalog().getText("141000E");
				MessageDialog.openError(getShell(), Messages.TitleError, errMsg);

				this.nameText.setEnabled(false);
				this.domainText.setEnabled(false);
				this.httpsPortText.setEnabled(false);
				this.userText.setEnabled(false);
				this.passwordText.setEnabled(false);
				this.useHTTPsButton.setEnabled(false);
//				this.testConnectionButton.setEnabled(false);

				setPageComplete(false);
			} // end of if (IISClient.getVersion() == IISClientVersion.v11xOrLater && !ServerRequestUtil.isIISServerConnectionLibsUsed())
		}
	}

}
