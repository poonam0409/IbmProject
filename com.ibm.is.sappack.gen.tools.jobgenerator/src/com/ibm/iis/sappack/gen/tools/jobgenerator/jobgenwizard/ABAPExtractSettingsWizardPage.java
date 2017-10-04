//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
// (C) Copyright IBM Corporation 2011, 2014                                              
//                                                                             
// Module Name : com.ibm.is.sappack.gen.tools.jobgenerator.wizard
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.iis.sappack.gen.tools.jobgenerator.jobgenwizard;


import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import com.ibm.iis.sappack.gen.common.ui.connections.PasswordDialog;
import com.ibm.iis.sappack.gen.common.ui.connections.SAPConnectionSelectionWidget;
import com.ibm.iis.sappack.gen.common.ui.connections.SapSystem;
import com.ibm.iis.sappack.gen.common.ui.util.Utils;
import com.ibm.iis.sappack.gen.common.ui.wizards.INextActionWizardPage;
import com.ibm.iis.sappack.gen.common.ui.wizards.PersistentWizardPageBase;
import com.ibm.is.sappack.gen.common.BaseException;
import com.ibm.is.sappack.gen.common.SAPAccessException;
import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.common.ui.ModeManager;
import com.ibm.is.sappack.gen.common.ui.WidgetIDConstants;
import com.ibm.is.sappack.gen.common.ui.WidgetIDUtils;
import com.ibm.is.sappack.gen.common.ui.preferences.AdvancedSettingsPreferencePage;
import com.ibm.is.sappack.gen.tools.jobgenerator.Activator;
import com.ibm.is.sappack.gen.tools.jobgenerator.jco.JCoService;
import com.sap.conn.jco.JCoException;


public class ABAPExtractSettingsWizardPage extends PersistentWizardPageBase implements INextActionWizardPage {

	private Text                         abapPrefix;
	private Text                         additionalABAPCode;
	private Text 						 newRequestText;
	private Text						 numberOfRetries;
	private Text						 retryInterval;
	private Button                       saveAbapProgramsOption;
	private Button                       uploadProgramsButton;
	private Button 						 suppressBackgroundJob;
	private Button						 createSeparateTransports;	
	private boolean                      useCTS;
	private Button                       useCTSButton;
	private Combo                        ctsPackageCombo;
	private Combo                        ctsRequestCombo;
	private SAPConnectionSelectionWidget sapSystemWidget;
	private boolean isCreatingControls = false;
	private int     transferMethodType;

//	private static final String TMP_PACKAGE = "$TMP"; //$NON-NLS-1$
	private static final String NEW_REQUEST = "CREATE REQUEST";

	private static final String SETTINGS_SAVE_ABAP   = "ABAPProgramUploadPage.saveAbapProgramsOption"; //$NON-NLS-1$
	private static final String SETTINGS_UPLOAD_ABAP = "ABAPProgramUploadPage.uploadAbapProgramsOption"; //$NON-NLS-1$
	private static final String SETTINGS_SAP_SYSTEM  = "ABAPProgramUploadPage.sapSystem"; //$NON-NLS-1$
	private static final String SETTINGS_USE_CTS     = "ABAPProgramUploadPage.useCTS"; //$NON-NLS-1$
	private static final String SETTINGS_PACKAGE     = "ABAPProgramUploadPage.ctsPackage"; //$NON-NLS-1$
	private static final String SETTINGS_REQUEST     = "ABAPProgramUploadPage.ctsRequest"; //$NON-NLS-1$
	private static final String SETTINGS_REQUEST_DESC     = "ABAPProgramUploadPage.ctsRequestDesc"; //$NON-NLS-1$
	private static final String SETTINGS_SUPPRESS     = "ABAPProgramUploadPage.suppressBackgroundJob"; //$NON-NLS-1$ 
	private static final String SETTINGS_RETRY     = "ABAPProgramUploadPage.numberOfRetries"; //$NON-NLS-1$ 
	private static final String SETTINGS_RETRY_INTERVAL     = "ABAPProgramUploadPage.retryInterval"; //$NON-NLS-1$ 
	private static final String SETTINGS_ABAP_PREFIX = "ABAPProgramUploadPage.abapProgramPrefix"; //$NON-NLS-1$
	private static final String SETTINGS_ADDITIONAL_ABAP_CODE = "ABAPProgramUploadPage.additionalABAPCode"; //$NON-NLS-1$
	private static final String SETTINGS_CREATE_SEPARTATE_TRANSPORTS = "ABAPProgramUploadPage.createSeparateTransports"; //$NON-NLS-1$
	private static final String ABAP_PREFIX_INITIAL  = "Z_SP_"; //$NON-NLS-1$

	private String jcoErrorMessage = Messages.ABAPProgramUploadPage_0;
	private String defaultRetryInterval = "1";
	private String defaultNumberOfRetries = "30";
	
	

  	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}
  	
	public ABAPExtractSettingsWizardPage(int transferMethodType) {
		super("jobGeneratorABAPUploadPage", Messages.ABAPExtractSettingsWizardPage_0, null); //$NON-NLS-1$
		this.setDescription(Messages.ABAPExtractSettingsWizardPage_1);
		this.useCTS             = true;
		this.transferMethodType = transferMethodType;
	}

	SelectionListener selectionListener = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			updateEnablement();
		}
	};

	ModifyListener ml = new ModifyListener() {

		@Override
		public void modifyText(ModifyEvent e) {
			updateEnablement();
		}

	};
	

	@Override
	public Composite createControlImpl(final Composite parent) {
		
		this.isCreatingControls = true;
		
		Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayout(new GridLayout(1, false));
		comp.setLayoutData(new GridData(GridData.FILL_BOTH));

		createABAPOptionsGroup(comp);
		createABAPUploadOptionsGroup(comp);

		updateEnablement();

		setInitialFocusToDlgFld(this.abapPrefix);

		this.isCreatingControls = false;

		return comp;

	}

	private void createABAPUploadOptionsGroup(Composite comp) {

		Group abapUploadOptionsGroup = new Group(comp, SWT.NONE);
		abapUploadOptionsGroup.setText(Messages.ABAPProgramUploadPage_19);
		GridLayout gridLayout = new GridLayout(1, false);
		abapUploadOptionsGroup.setLayout(gridLayout);
		abapUploadOptionsGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		this.saveAbapProgramsOption = new Button(abapUploadOptionsGroup, SWT.CHECK);
		this.saveAbapProgramsOption.setText(Messages.ABAPProgramUploadPage_4);
		this.configureCheckBoxForProperty(saveAbapProgramsOption, SETTINGS_SAVE_ABAP);
		this.saveAbapProgramsOption.addSelectionListener(selectionListener);
		WidgetIDUtils.assignID(saveAbapProgramsOption, WidgetIDConstants.GEN_SAVEABAPPROGRAMSOPTION);

		this.uploadProgramsButton = new Button(abapUploadOptionsGroup, SWT.CHECK);
		this.uploadProgramsButton.setText(Messages.ABAPProgramUploadPage_5);
		this.configureCheckBoxForProperty(uploadProgramsButton, SETTINGS_UPLOAD_ABAP);
		this.uploadProgramsButton.addSelectionListener(selectionListener);
		WidgetIDUtils.assignID(this.uploadProgramsButton, WidgetIDConstants.GEN_UPLOADABAPPROGRAMSOPTION);

		this.sapSystemWidget = new SAPConnectionSelectionWidget(abapUploadOptionsGroup, SWT.NONE);
		this.configureTextForProperty(this.sapSystemWidget.getSAPConnectionNameText(), SETTINGS_SAP_SYSTEM);
		this.sapSystemWidget.addModifyListener(ml);
		
		if (this.useCTS) {
			Group ctsSettingsGroup = new Group(abapUploadOptionsGroup, SWT.NONE);
			ctsSettingsGroup.setText(Messages.ABAPProgramUploadPage_6);
			GridLayout ctsLayout = new GridLayout(2, false);
			ctsSettingsGroup.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL));
			ctsSettingsGroup.setLayout(ctsLayout);

			this.useCTSButton = new Button(ctsSettingsGroup, SWT.CHECK);
			this.useCTSButton.setText(Messages.ABAPProgramUploadPage_7);
			this.useCTSButton.addSelectionListener(selectionListener);
			this.configureCheckBoxForProperty(useCTSButton, SETTINGS_USE_CTS);
			new Label(ctsSettingsGroup, SWT.NONE);
			
			this.createSeparateTransports = new Button(ctsSettingsGroup, SWT.CHECK);
			this.createSeparateTransports.setText(Messages.ABAPProgramUploadPage_31);
			this.createSeparateTransports.setSelection(false);
			this.createSeparateTransports.addSelectionListener(selectionListener);
			this.configureCheckBoxForProperty(createSeparateTransports, SETTINGS_CREATE_SEPARTATE_TRANSPORTS);
			new Label(ctsSettingsGroup, SWT.NONE);

			Label packageLabel = new Label(ctsSettingsGroup, SWT.NONE);
			packageLabel.setText(Messages.ABAPProgramUploadPage_8);
			this.ctsPackageCombo = new Combo(ctsSettingsGroup, SWT.DROP_DOWN | SWT.BORDER | SWT.READ_ONLY);
			this.ctsPackageCombo.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
			this.configureComboForProperty(ctsPackageCombo, SETTINGS_PACKAGE);
			this.ctsPackageCombo.addMouseListener(new MouseAdapter() {
				
				@Override
				public void mouseDown(MouseEvent e) {
					super.mouseDown(e);	
					String[] packages = getAllPackages();
					if (packages != null) {
						ctsPackageCombo.setItems(packages);
					}
				}
			});
			this.ctsPackageCombo.addSelectionListener(selectionListener);

			Label requestLabel = new Label(ctsSettingsGroup, SWT.NONE);
			requestLabel.setText(Messages.ABAPProgramUploadPage_9);
			this.ctsRequestCombo = new Combo(ctsSettingsGroup, SWT.DROP_DOWN | SWT.BORDER);
			this.ctsRequestCombo.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
			this.configureComboForProperty(ctsRequestCombo, SETTINGS_REQUEST);
			this.ctsRequestCombo.addMouseListener(new MouseAdapter() {

				@Override
				public void mouseDown(MouseEvent e) {
					super.mouseDown(e);
					String[] requests = getRequests();
					if (requests != null) {
						ctsRequestCombo.setItems(requests);
					}
				}
			});
			this.ctsRequestCombo.addSelectionListener(selectionListener);
			
			Label newRequestDescriptionLabel = new Label(ctsSettingsGroup, SWT.NONE);
			newRequestDescriptionLabel.setText(Messages.ABAPProgramUploadPage_26);
			newRequestText = new Text(ctsSettingsGroup, SWT.BORDER);
			newRequestText.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
			configureTextForProperty(newRequestText, SETTINGS_REQUEST_DESC);
			newRequestText.addModifyListener(new ModifyListener() {
				@Override
				public void modifyText(ModifyEvent e) {
					updateEnablement();

				}
			});
		}
		
		Group retrySettingsGroup = new Group(abapUploadOptionsGroup, SWT.NONE);
		retrySettingsGroup.setText(Messages.ABAPProgramUploadPage_30);
		GridLayout retryLayout = new GridLayout(2, false);
		retrySettingsGroup.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
		retrySettingsGroup.setLayout(retryLayout);
		
		this.suppressBackgroundJob = new Button(retrySettingsGroup, SWT.CHECK);
		this.suppressBackgroundJob.setText(Messages.ABAPProgramUploadPage_27);
		this.suppressBackgroundJob.setSelection(false);
		this.configureCheckBoxForProperty(suppressBackgroundJob, SETTINGS_SUPPRESS);
		this.suppressBackgroundJob.addSelectionListener(selectionListener);
		WidgetIDUtils.assignID(suppressBackgroundJob, WidgetIDConstants.GEN_SUPPRESSBACKGROUNDJOB);
		Button space = new Button(retrySettingsGroup, SWT.NONE);
		space.setVisible(false);
		
		Label numOfRetries = new Label(retrySettingsGroup, SWT.NONE);
		numOfRetries.setText(Messages.ABAPProgramUploadPage_28);
		numOfRetries.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
		numberOfRetries = new Text(retrySettingsGroup, SWT.BORDER);
		numberOfRetries.setTextLimit(3);
		numberOfRetries.setText(defaultNumberOfRetries);
		numberOfRetries.addListener(SWT.Verify, new Listener() {
			  
            @Override
			public void handleEvent(org.eclipse.swt.widgets.Event e) {
				  String string = e.text;
	               char[] chars = new char[string.length()];
	               string.getChars(0, chars.length, chars, 0);
	               for (int i = 0; i < chars.length; i++) {
	                 if (!('0' <= chars[i] && chars[i] <= '9')) {
	                 e.doit = false;
	                 return;
	               }
	               }
			}
	           });
		
		configureTextForProperty(numberOfRetries, SETTINGS_RETRY);
		numberOfRetries.addModifyListener(ml);
		WidgetIDUtils.assignID(numberOfRetries, WidgetIDConstants.GEN_RETRIES);
		
		Label retryInt = new Label(retrySettingsGroup, SWT.NONE);
		retryInt.setText(Messages.ABAPProgramUploadPage_29);
		retryInt.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
		retryInterval = new Text(retrySettingsGroup, SWT.BORDER);
		retryInterval.setTextLimit(4);
		retryInterval.setText(defaultRetryInterval);
		retryInterval.addListener(SWT.Verify, new Listener() {
			  
            @Override
			public void handleEvent(org.eclipse.swt.widgets.Event e) {
				  String string = e.text;
	               char[] chars = new char[string.length()];
	               string.getChars(0, chars.length, chars, 0);
	               for (int i = 0; i < chars.length; i++) {
	                 if (!('0' <= chars[i] && chars[i] <= '9')) {
	                 e.doit = false;
	                 return;
	               }
	               }
			}
	           });
		configureTextForProperty(retryInterval, SETTINGS_RETRY_INTERVAL);
		retryInterval.addModifyListener(ml);
		WidgetIDUtils.assignID(retryInterval, WidgetIDConstants.GEN_RETRYINTERVAL);
		
	}

	
	protected void updateControlsAfterCreation() {
		this.isCreatingControls = true;
		updateEnablement();
		this.isCreatingControls = false;
	}


	private void createABAPOptionsGroup(Composite comp) {
		Group abapOptionsGroup = new Group(comp, SWT.NONE);
		abapOptionsGroup.setText(Messages.ABAPProgramUploadPage_3);
		GridLayout gridLayout = new GridLayout(1, false);
		abapOptionsGroup.setLayout(gridLayout);

		
		if (ModeManager.isCWEnabled()) {
			Composite abapPrefixgroup = new Composite(abapOptionsGroup, SWT.NONE);
			GridLayout abapPrefixLayout = new GridLayout(2, false);
			GridData abapPrefixLayoutData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
			abapPrefixgroup.setLayout(abapPrefixLayout);
			abapPrefixgroup.setLayoutData(abapPrefixLayoutData);

			Label abapPrefixLabel = new Label(abapPrefixgroup, SWT.NONE);
			abapPrefixLabel.setText(Messages.ABAPProgramUploadPage_14);
			this.abapPrefix = new Text(abapPrefixgroup, SWT.BORDER);
			this.abapPrefix.setLayoutData(abapPrefixLayoutData);
			this.abapPrefix.setText(ABAP_PREFIX_INITIAL);
			this.configureTextForProperty(abapPrefix, SETTINGS_ABAP_PREFIX);
			this.abapPrefix.addModifyListener(ml);
		}

		if (!ModeManager.generateV7Stages() || AdvancedSettingsPreferencePage.isSettingEnabled("RG_ENABLE_ADDITIONAL_ABAP_CODE")) { //$NON-NLS-1$
			// only fill this group if text field is set
			abapOptionsGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
			Group additionalABAPCodeGroup = new Group(abapOptionsGroup, SWT.NONE);
			additionalABAPCodeGroup.setText(Messages.ABAPProgramUploadPage_18);
			this.additionalABAPCode = new Text(additionalABAPCodeGroup, SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
			this.additionalABAPCode.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.FILL_BOTH));
			configureTextForProperty(this.additionalABAPCode, SETTINGS_ADDITIONAL_ABAP_CODE);
			this.additionalABAPCode.addModifyListener(ml);
			
			additionalABAPCodeGroup.setLayout(new GridLayout(1, true));
			additionalABAPCodeGroup.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.FILL_BOTH));
		} else {
			abapOptionsGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		}
		
	   setInitialFocusToDlgFld(abapPrefix);
	}

	@Override
	public boolean nextPressedImpl() {
		if (this.getUploadAbapProgramsOption()) {
			SapSystem sap = this.getSapSystem();
			if (sap == null) {
				setErrorMessage(Messages.ABAPExtractSettingsWizardPage_2);
				return false;
			}
			boolean pwIsOK = PasswordDialog.checkForPassword(getShell(), sap);
			if (!pwIsOK) {
				setErrorMessage(Messages.ABAPExtractSettingsWizardPage_3);
			} 
			return pwIsOK;
		}
		return true;
	}

	private String[] getAllPackages() {
		try {
			return callRFCModuleForPackages();
		} 
		catch (Exception e) {
			e.printStackTrace();
			String msg;
			if (e instanceof BaseException) {
				msg = e.getLocalizedMessage();
			} 
			else {
				msg = MessageFormat.format(jcoErrorMessage, e.getMessage());
			}
			setErrorMessage(msg);
			Activator.getLogger().log(Level.SEVERE, msg, e);

			return null;
		}
	}

	private String[] getRequests() {
		String pack = this.ctsPackageCombo.getItems()[this.ctsPackageCombo.getSelectionIndex()];
		List<String> packages = new ArrayList<String>();
	//	packages.add(NEW_REQUEST);
		try {
			packages.addAll(Arrays.asList(callRFCModuleForRequests(pack)));
			packages.remove(0); // for removing space added by SAP
			packages.add(0,NEW_REQUEST);
		}
		catch (Exception e) {
			e.printStackTrace();
			String msg;
			if (e instanceof BaseException) {
				msg = e.getLocalizedMessage();
			} 
			else {
				msg = MessageFormat.format(jcoErrorMessage, e.getMessage());
			}
			setErrorMessage(msg);
			Activator.getLogger().log(Level.SEVERE, msg, e);

			return null;
		}

		return packages.toArray(new String[0]);
	}

	public boolean getSaveAbapProgramsOption() {
		return this.saveAbapProgramsOption.getSelection();
	}

	public boolean getUploadAbapProgramsOption() {
		return this.uploadProgramsButton.getSelection();
	}

	private String getTextFieldValue(Text text) {
		String textValue = null;
		if (text != null) {
			String s = text.getText().trim();

			if (!s.isEmpty()) {
				textValue = s;
			}
		}
		return textValue;
	}
	
	private int getIntegerValue(Text text) {
		int textInteger=0;
		if(text!=null) {
			String s = text.getText().trim();
			if (!s.isEmpty()) {
				textInteger = Integer.parseInt(s);
			}
		}
		return textInteger;
	}

	public String getABAPProgramPrefix() {
		return getTextFieldValue(this.abapPrefix);
	}

	public String getAdditionalABAPCode() {
		return getTextFieldValue(additionalABAPCode);
	}
	public int getNumberOfRetries() {
		return getIntegerValue(this.numberOfRetries);
	}

	public int getRetryInterval() {
		return getIntegerValue(this.retryInterval);
	}

	public boolean getSuppressBackgroundJob() {
		return this.suppressBackgroundJob.getSelection();
	}
	public SapSystem getSapSystem() {
		return this.sapSystemWidget.getSelectedSAPSystem();
	}

	public boolean isCTSEnabled() {
		if (!this.useCTS) {
			return false;
		}
		return useCTS;
	}

	public boolean getcreateSeparateTransports()	{					
		return this.createSeparateTransports.getSelection();
	}
	
	public String getPackage() {
		int ix = ctsPackageCombo.getSelectionIndex();
		if (ix == -1) {
			return null;
		}
		return ctsPackageCombo.getItem(ix);
	}

	public String getRequest() {
		String p = getPackage();
		if (JCoService.SAP_RFC_TEMP_DEV_CLASS.equals(p)) {
			return null;
		}
		int ix = ctsRequestCombo.getSelectionIndex();
		if (ix == -1) {
			return null;
		}
		return ctsRequestCombo.getItem(ix);
	}

	
	public String getRequestDescription() {
		String req = getRequest();
		if (!NEW_REQUEST.equals(req)) {
			return null;
		}
		return newRequestText.getText();
	}
	 

	private void enableCTSGroup(boolean enabled) {
		if (useCTS) {
			this.ctsPackageCombo.setEnabled(enabled);
			this.ctsRequestCombo.setEnabled(enabled);
			this.newRequestText.setEnabled(enabled);
		}
	}

	public void updateEnablement() {
		boolean complete = true;
		boolean isUploadSelected = this.uploadProgramsButton.getSelection();

		this.sapSystemWidget.setEnabled(isUploadSelected);
		if (this.useCTS) {
			this.useCTSButton.setEnabled(isUploadSelected);
			this.createSeparateTransports.setEnabled(isUploadSelected);
			this.ctsPackageCombo.setEnabled(isUploadSelected);
			this.ctsRequestCombo.setEnabled(isUploadSelected);
			this.newRequestText.setEnabled(isUploadSelected);
		}

		boolean isSuppressSelected = this.suppressBackgroundJob.getSelection();
		this.numberOfRetries.setEnabled(!isSuppressSelected);
		this.retryInterval.setEnabled(!isSuppressSelected);
		
		if (!this.isCreatingControls) {			
			if (this.abapPrefix != null) {
				String prefix = this.abapPrefix.getText();
				if (prefix == null || prefix.trim().length() == 0) {
					setErrorMessage(Messages.ABAPProgramUploadPage_15);
					complete = false;
				} else {
					setMessage(null, DialogPage.WARNING);
					int tm = transferMethodType;
					int maxLength = -1;
					if (tm == com.ibm.is.sappack.gen.common.Constants.ABAP_TRANSFERMETHOD_CPIC) {
						if (!prefix.startsWith("Z") && !prefix.startsWith("Y")) { //$NON-NLS-1$ //$NON-NLS-2$
							setErrorMessage(Messages.ABAPProgramUploadPage_16);
							complete = false;
						}
						if (prefix.length() > 2) {
							maxLength = 2;
						}
					} else {
						if (prefix.length() > 5) {
							maxLength = 5;
						}
					}
					if (maxLength != -1) {
						String msg = Messages.ABAPProgramUploadPage_17;
						msg = MessageFormat.format(msg, maxLength);
						setMessage(msg, DialogPage.WARNING);
					}

				}
			}
			if (complete) {
				if (isUploadSelected) {
					SapSystem system = sapSystemWidget.getSelectedSAPSystem();
					if (system == null) {
						setErrorMessage(Messages.ABAPProgramUploadPage_10);
						complete = false;
					}

					if (this.useCTS) {
						if (this.useCTSButton.getSelection()) {
							this.ctsPackageCombo.setEnabled(true);
							String p = getPackage();

							if (p == null) {
								setErrorMessage(Messages.ABAPProgramUploadPage_11);
								complete = false;
							} 
							else {
								if (JCoService.SAP_RFC_TEMP_DEV_CLASS.equals(p)) {
									this.ctsRequestCombo.setEnabled(false);
									       this.newRequestText.setEnabled(false);
								} 
								else {
									this.newRequestText.setEnabled(true);
									this.ctsRequestCombo.setEnabled(true);
									String req = getRequest();
									if (req == null) {
										setErrorMessage(Messages.ABAPProgramUploadPage_12);
										complete = false;
									}
									
									else {
									   newRequestText.setEnabled(NEW_REQUEST.equals(req));
									}
								}
							}
						} 
						else {
							enableCTSGroup(false);
						}
					}
				}
			} 
			else {
				enableCTSGroup(false);
			}
		}
		if (complete) {
			setErrorMessage(null);
		}

		setPageComplete(complete);
	}


	String[] callRFCModuleForPackages() throws SAPAccessException {
		JCoService   jcoService;
		SapSystem    sapSystem;
		String       retPackageArr[];
		List<String> devClasses;
		
		retPackageArr = null;
		sapSystem = this.sapSystemWidget.getSelectedSAPSystem();
		if (sapSystem != null) {
			boolean pwProvided = PasswordDialog.checkForPassword(getShell(), sapSystem);
			if (!pwProvided) {
				return new String[0];
			}
			// first get JCO Service and check version
			jcoService = JCoService.getJCoService(sapSystem, "", "", "", true); // hansx // force CTS use //$NON-NLS-1$ //$NON-NLS-2$
			String checkMsg = jcoService.checkVersion();
			if (checkMsg != null) {
				throw new SAPAccessException("126900E", new String[] { checkMsg }); //$NON-NLS-1$
			}

			// ... then get the list of packages ...
			devClasses = jcoService.getPackages();

			retPackageArr = devClasses.toArray(new String[0]);
		} // end of if (sapSystem != null)

		return(retPackageArr);
	}

	String[] callRFCModuleForRequests(String devClass) throws JCoException, SAPAccessException {
		JCoService   jcoService;
		SapSystem    sapSystem;
		String       retRequestArr[];
		List<String> requestList;

		retRequestArr = null;
		sapSystem = this.sapSystemWidget.getSelectedSAPSystem();
		if (sapSystem != null) {
			boolean pwProvided = PasswordDialog.checkForPassword(getShell(), sapSystem);
			if (!pwProvided) {
				return new String[0];
			}
			// first get JCO Service and check version
			jcoService = JCoService.getJCoService(sapSystem, "", "", "", true); // hansx // force CTS use  //$NON-NLS-1$//$NON-NLS-2$
			String checkMsg = jcoService.checkVersion();
			if (checkMsg != null) {
				throw new SAPAccessException("126900E", new String[] { checkMsg }); //$NON-NLS-1$
			}

			// ... then get the list of packages ...
			requestList = jcoService.getRequests(devClass);

			retRequestArr = requestList.toArray(new String[0]);
			
		} // end of if (sapSystem != null)

		return(retRequestArr);
	}
	
	@Override
	public void performHelp() {
		PlatformUI.getWorkbench().getHelpSystem().displayHelp(Utils.getHelpID("rgwiz_abap_programs")); //$NON-NLS-1$
	}

	
}
