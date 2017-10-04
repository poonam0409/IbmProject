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
import java.util.logging.Level;
import java.util.regex.Matcher;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.ibm.iis.sappack.gen.common.ui.util.Utils;
import com.ibm.is.sappack.gen.common.Constants;
import com.ibm.is.sappack.gen.common.ui.Activator;
import com.ibm.is.sappack.gen.common.ui.Messages;
import com.sap.conn.jco.JCoException;


public class EditSAPConnectionWizardPage extends WizardPage {
	private static final int        CLIENT_MAX_LEN   = 3;
	private static final int        LANGUAGE_MAX_LEN = 2;
	
	private boolean                 readOnly;
	private boolean                 newConnection;
	private Text                    userText;
	private Text                    passwordText;
	private Button                  testConnectionButton;
	private Text                    clientText;
	private Text                    languageText;
	private Text                    nameText;
	private SapSystem               sapConnection;
	private Button                  isMessageServerCheckbox;
	private Composite               serverSettings;
	private Composite               appServerComposite;
	private Composite               messageServerComposite;
	private StackLayout             stackLayout;
	private Text                    systemNumText;
	private Text                    appServerText;
	private Text                    messageSrvText;
	private Text                    systemIDText;
	private Text                    groupNameText;
	private SAPConnectionRepository repository;

	
  	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	

	public EditSAPConnectionWizardPage(SAPConnectionRepository rep, String existingName) {
		super("editsapconnectionwizardpage"); //$NON-NLS-1$
		this.setTitle(Messages.EditSAPConnectionWizardPage_0);
		this.setDescription(Messages.EditSAPConnectionWizardPage_1);
		this.repository = rep;
		this.newConnection = existingName == null;
		if (existingName != null) {
			this.sapConnection = rep.getSAPConnection(existingName);
			this.readOnly = this.sapConnection instanceof IISSapSystem;
		} else {
			this.readOnly = false;
		}
	}

	@Override
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

		SelectionListener sl = new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				update();
			}

		};

		GridData textGD = new GridData(SWT.FILL, SWT.TOP, true, false);

		Composite nameComp = new Composite(comp, SWT.NONE);
		nameComp.setLayout(new GridLayout(2, false));
		nameComp.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		Label l = new Label(nameComp, SWT.NONE);
		l.setText(Messages.EditSAPConnectionWizardPage_2);
		nameText = new Text(nameComp, SWT.BORDER);
		nameText.setLayoutData(textGD);
		if (this.sapConnection != null) {
			nameText.setText(this.sapConnection.getFullName());
		}
		nameText.addModifyListener(ml);
		nameText.setEnabled(newConnection);

		Group hostGroup = new Group(comp, SWT.NONE);
		hostGroup.setLayout(new GridLayout(1, false));
		hostGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		hostGroup.setText(Messages.EditSAPConnectionWizardPage_3);

		isMessageServerCheckbox = new Button(hostGroup, SWT.CHECK);
		isMessageServerCheckbox.setText(Messages.EditSAPConnectionWizardPage_4);
		isMessageServerCheckbox.addSelectionListener(sl);
		isMessageServerCheckbox.setEnabled(!readOnly);

		serverSettings = new Composite(hostGroup, SWT.NONE);
		stackLayout = new StackLayout();
		serverSettings.setLayout(stackLayout);
		serverSettings.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		appServerComposite = new Composite(serverSettings, SWT.NONE);
		appServerComposite.setLayout(new GridLayout(2, false));

		l = new Label(appServerComposite, SWT.NONE);
		l.setText(Messages.EditSAPConnectionWizardPage_5);

		appServerText = new Text(appServerComposite, SWT.BORDER);
		appServerText.setLayoutData(textGD);
		appServerText.setEnabled(!readOnly);
		appServerText.addModifyListener(ml);

		l = new Label(appServerComposite, SWT.NONE);
		l.setText(Messages.EditSAPConnectionWizardPage_6);

		systemNumText = new Text(appServerComposite, SWT.BORDER);
		systemNumText.setLayoutData(textGD);
		systemNumText.setEnabled(!readOnly);
		systemNumText.addModifyListener(ml);

		messageServerComposite = new Composite(serverSettings, SWT.NONE);
		messageServerComposite.setLayout(new GridLayout(2, false));

		l = new Label(messageServerComposite, SWT.NONE);
		l.setText(Messages.EditSAPConnectionWizardPage_7);
		messageSrvText = new Text(messageServerComposite, SWT.BORDER);
		messageSrvText.setLayoutData(textGD);
		messageSrvText.setEnabled(!readOnly);
		messageSrvText.addModifyListener(ml);

		l = new Label(messageServerComposite, SWT.NONE);
		l.setText(Messages.EditSAPConnectionWizardPage_8);
		systemIDText = new Text(messageServerComposite, SWT.BORDER);
		systemIDText.setLayoutData(textGD);
		systemIDText.setEnabled(!readOnly);
		systemIDText.addModifyListener(ml);

		l = new Label(messageServerComposite, SWT.NONE);
		l.setText(Messages.EditSAPConnectionWizardPage_9);
		groupNameText = new Text(messageServerComposite, SWT.BORDER);
		groupNameText.setLayoutData(textGD);
		groupNameText.setEnabled(!readOnly);
		groupNameText.addModifyListener(ml);

		stackLayout.topControl = appServerComposite;

		Composite routerComp = new Composite(hostGroup, SWT.NONE);
		routerComp.setLayout(new GridLayout(2, false));
		routerComp.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		l = new Label(routerComp, SWT.NONE);
		l.setText(Messages.EditSAPConnectionWizardPage_10);

		Text routerStringText = new Text(routerComp, SWT.BORDER);
		routerStringText.setLayoutData(textGD);
		routerStringText.setEnabled(!readOnly);
		routerStringText.addModifyListener(ml);

		Group credentialsGroup = new Group(comp, SWT.NONE);
		credentialsGroup.setText(Messages.EditSAPConnectionWizardPage_11);
		credentialsGroup.setLayout(new GridLayout(2, false));
		credentialsGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		l = new Label(credentialsGroup, SWT.NONE);
		l.setText(Messages.EditSAPConnectionWizardPage_12);

		userText = new Text(credentialsGroup, SWT.BORDER);
		userText.setLayoutData(textGD);
		userText.addModifyListener(ml);

		l = new Label(credentialsGroup, SWT.NONE);
		l.setText(Messages.EditSAPConnectionWizardPage_13);

		passwordText = new Text(credentialsGroup, SWT.BORDER | SWT.PASSWORD);
		passwordText.setLayoutData(textGD);
		passwordText.addModifyListener(ml);

		l = new Label(credentialsGroup, SWT.NONE);
		l.setText(Messages.EditSAPConnectionWizardPage_14);
		clientText = new Text(credentialsGroup, SWT.BORDER);
		clientText.setTextLimit(CLIENT_MAX_LEN);
		clientText.setLayoutData(textGD);
		clientText.addModifyListener(ml);
		clientText.addVerifyListener(new VerifyListener() {  // only numeric values are accepted
			@Override
			public void verifyText(VerifyEvent verifyEv) {
				Matcher patMatcher  = Constants.NUMERIC_PATTERN.matcher(verifyEv.text);
				if (!patMatcher.matches()) {
					verifyEv.doit = false;
				}
			}
		});

		l = new Label(credentialsGroup, SWT.NONE);
		l.setText(Messages.EditSAPConnectionWizardPage_15);
		languageText = new Text(credentialsGroup, SWT.BORDER);
		languageText.setTextLimit(LANGUAGE_MAX_LEN);
		languageText.setLayoutData(textGD);
		languageText.addModifyListener(ml);
		languageText.addVerifyListener(new VerifyListener() {  // force uppercase letters
			@Override
			public void verifyText(VerifyEvent verifyEv) {
				verifyEv.text = verifyEv.text.toUpperCase();
			}
		});

		testConnectionButton = new Button(comp, SWT.PUSH);
		testConnectionButton.setText(Messages.EditSAPConnectionWizardPage_16);
		testConnectionButton.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false));

		testConnectionButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					SapSystem copy = repository.createNewLocalSAPConnection(Utils.getText(nameText));
					commitChanges(copy);
					SapConnectionTester.ping(copy);
					MessageDialog.openInformation(getShell(), Messages.SapSystemWizardPage_15, MessageFormat.format(Messages.SapSystemWizardPage_16, copy.getName()));
				} catch (JCoException exc) {
					Activator.getLogger().log(Level.SEVERE, exc.getMessage(), exc);
					String msg = MessageFormat.format(Messages.ABAPProgramUploadPage_0, exc.getMessage());
					MessageDialog.openError(getShell(), Messages.TitleError, msg);
				}
			}
		});

		restoreValues();
		update();
		return comp;
	}

	void restoreValues() {
		if (this.sapConnection != null) {
			Utils.setText(this.nameText, this.sapConnection.getName());
			boolean isMessServer = this.sapConnection.isMessageServerSystem();
			this.isMessageServerCheckbox.setSelection(isMessServer);
			if (!isMessServer) {
				Utils.setText(this.appServerText, this.sapConnection.getHost());
				Utils.setText(this.systemNumText, this.sapConnection.getSystemNumber());
			} else {
				Utils.setText(this.messageSrvText, this.sapConnection.getHost());
				Utils.setText(this.systemIDText, this.sapConnection.getSystemId());
				Utils.setText(this.groupNameText, this.sapConnection.getGroupName());
			}
			Utils.setText(this.userText, this.sapConnection.getUsername());
			Utils.setText(this.passwordText, this.sapConnection.getPassword());
			Utils.setText(this.languageText, this.sapConnection.getUserLanguage());
			Utils.setText(this.clientText, Integer.toString(this.sapConnection.getClientId()));
		}
	}

	void update() {
		if (this.isMessageServerCheckbox.getSelection()) {
			this.stackLayout.topControl = messageServerComposite;
		} else {
			this.stackLayout.topControl = appServerComposite;
		}
		serverSettings.layout();

		setErrorMessage(null);
		setPageComplete(true);
		boolean error = false;
		boolean allDataProvided = true;
		String name = Utils.getText(nameText);
		if (!error && name == null) {
			error = true;
			setErrorMessage(Messages.EditSAPConnectionWizardPage_17);
			setPageComplete(false);
			allDataProvided = false;
		}
		if (!error) {
			if (this.newConnection && this.repository.getSAPConnection(name) != null) {
				setErrorMessage(MessageFormat.format(Messages.EditSAPConnectionWizardPage_18, name));
				error = true;
				setPageComplete(false);
			}
		}
		if (!error && !isMessageServerCheckbox.getSelection()) {
			if (!error && Utils.getText(appServerText) == null) {
				error = true;
				setErrorMessage(Messages.EditSAPConnectionWizardPage_19);
				allDataProvided = false;
			}
			String sysNum = Utils.getText(systemNumText);
			if (!error && sysNum == null) {
				error = true;
				setErrorMessage(Messages.EditSAPConnectionWizardPage_20);
				allDataProvided = false;
			}
			if (!error) {
				try {
					Integer.parseInt(sysNum);
				} catch (NumberFormatException exc) {
					setErrorMessage(Messages.EditSAPConnectionWizardPage_21);
					allDataProvided = false;
					error = true;
				}
			}
		} else {
			if (!error && Utils.getText(messageSrvText) == null) {
				error = true;
				setErrorMessage(Messages.EditSAPConnectionWizardPage_22);
				allDataProvided = false;
			}
			String sysID = Utils.getText(systemIDText);
			if (!error && sysID == null) {
				error = true;
				setErrorMessage(Messages.EditSAPConnectionWizardPage_23);
				allDataProvided = false;
			}
			String groupName = Utils.getText(groupNameText);
			if (!error && groupName == null) {
				error = true;
				setErrorMessage(Messages.EditSAPConnectionWizardPage_24);
				allDataProvided = false;
			}
		}

		String[] msg = new String[1];
		if (!error && !Utils.isValidConnectionName(Utils.getText(this.nameText), msg)) {
			setErrorMessage(msg[0]);
			error = true;
			allDataProvided = false;
		}

		if (!error && Utils.getText(this.userText) == null) {
			error = true;
			setErrorMessage(Messages.EditSAPConnectionWizardPage_25);
			allDataProvided = false;
		}

		if (!error && Utils.getText(this.passwordText) == null) {
			//	error = true;
			allDataProvided = false;
		}
		if (!error && Utils.getText(this.languageText) == null) {
			error = true;
			setErrorMessage(Messages.EditSAPConnectionWizardPage_26);
			allDataProvided = false;
		}

		String client = Utils.getText(this.clientText);
		if (!error && client == null) {
			error = true;
			setErrorMessage(Messages.EditSAPConnectionWizardPage_27);
			allDataProvided = false;
		}
		if (!error) {
			if (client != null) {
				try {
					Integer.parseInt(client);
				} catch (NumberFormatException exc) {
					error = true;
					setErrorMessage(Messages.EditSAPConnectionWizardPage_28);
					allDataProvided = false;
				}
			}
		}

		if (error) {
			testConnectionButton.setEnabled(false);
		} else {
			testConnectionButton.setEnabled(allDataProvided);
		}
	}

	private void commitChanges(SapSystem sapConnection) {
		sapConnection.setMessageServerSystem(isMessageServerCheckbox.getSelection());
		if (sapConnection.isMessageServerSystem()) {
			sapConnection.setHost(Utils.getText(messageSrvText));
			sapConnection.setSystemId(Utils.getText(systemIDText));
			sapConnection.setGroupName(Utils.getText(groupNameText));
		} else {
			sapConnection.setSystemNumber(Utils.getText(systemNumText));
			sapConnection.setHost(Utils.getText(appServerText));
		}

		sapConnection.setUsername(Utils.getText(userText));
		sapConnection.setPassword(Utils.getText(passwordText));
		sapConnection.setUserLanguage(Utils.getText(languageText));
		sapConnection.setClientId(Utils.getIntValue(clientText, 0));
	}

	public void commitChanges() {
		if (this.sapConnection != null) {
			this.commitChanges(this.sapConnection);
		} else {
			SapSystem newSys = repository.createNewLocalSAPConnection(Utils.getText(nameText));
			this.commitChanges(newSys);
			repository.addLocalSAPConnection(newSys);
		}
	}

}
