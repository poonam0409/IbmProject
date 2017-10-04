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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.wst.validation.ValidationResult;
import org.eclipse.wst.validation.ValidatorMessage;

import com.ibm.iis.sappack.gen.common.ui.iisclient.IISClient;
import com.ibm.iis.sappack.gen.common.ui.iisclient.IISClient.IISClientVersion;
import com.ibm.iis.sappack.gen.common.ui.util.Utils;
import com.ibm.iis.sappack.gen.common.ui.wizards.PersistentWizardPageBase;
import com.ibm.iis.sappack.gen.tools.jobgenerator.rgconf.RGConfiguration;
import com.ibm.iis.sappack.gen.tools.jobgenerator.rgconf.RGConfiguration.CONFIGURATION_TYPE;
import com.ibm.is.sappack.gen.common.DBSupport;
import com.ibm.is.sappack.gen.common.request.ServerRequestUtil;
import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.common.ui.ModeManager;
import com.ibm.is.sappack.gen.common.ui.RMRGMode;
import com.ibm.is.sappack.gen.common.ui.preferences.AdvancedSettingsPreferencePage;
import com.ibm.is.sappack.gen.common.util.ServerMessageCatalog;
import com.ibm.is.sappack.gen.tools.jobgenerator.Activator;
import com.ibm.is.sappack.gen.tools.sap.constants.Constants;


public class RGResourcePage extends PersistentWizardPageBase {
	private class EntryFieldControl {
		private Button   _BrowseButton;
		private String   _FileExtArr[];
		private boolean  _IsEnabled;
		private Label    _Label;
		private Text     _InputField;
	
		public EntryFieldControl(Label label, Text inpFld, Button bttn, String fileExts[]) {
			_BrowseButton    = bttn;
			_FileExtArr      = fileExts;
			_InputField      = inpFld;
			_Label           = label;

			setEnabled(true);
		}
//		public Button   getButton()          { return(_BrowseButton); }
		public String[] getFileExts()        { return(_FileExtArr); }
		public Text     getInputField()      { return(_InputField); }
		public Label    getLabel()           { return(_Label); }
		public boolean  isEnabled()          { return(_IsEnabled); }
		public void     setEnabled(boolean enable) { 
		                                             setEnabled(_BrowseButton, enable);
		                                             setEnabled(_InputField, enable);
		                                             setEnabled(_Label, enable);
		                                             _IsEnabled = enable; 
		}
		private void setEnabled(Control ctrl, boolean enable) {
			if (ctrl != null) {
				ctrl.setEnabled(enable);
			}
		}
	} // end of class EntryFieldControl

	private class ControlCheckBox {
		Button            _CheckBox;
		EntryFieldControl _EntryFieldControl;

		public ControlCheckBox(Composite composite) {
			this(composite, null);
		}
		public ControlCheckBox(Composite composite, EntryFieldControl efCtrl) {
			if (composite == null) {
				_CheckBox          = null;
				_EntryFieldControl = null;
			}
			else {
				_CheckBox = new Button(composite, SWT.CHECK);

				_CheckBox.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent selectEvt) {
						boolean isSelected = ((Button) selectEvt.getSource()).getSelection();
						if (_EntryFieldControl != null) {
							_EntryFieldControl.setEnabled(isSelected);
						}
						dialogChanged();
					}
				});
			}
			_EntryFieldControl = efCtrl;
		}

//		public EntryFieldControl getEntryFiledControl()                         { return(_EntryFieldControl); }
		public void setEnabled(boolean enable) {
			if (_CheckBox != null) {
				boolean efEnabled;
				_CheckBox.setEnabled(enable);

				efEnabled = enable && _CheckBox.getSelection();
				if (_EntryFieldControl != null) {
					_EntryFieldControl.setEnabled(efEnabled);
				}
			} // end of if (_CheckBox != null)
		}
		public void setEntryFieldControl(EntryFieldControl efCtrl) { _EntryFieldControl = efCtrl; }
		public void setLayoutData(Object layoutData)               { _CheckBox.setLayoutData(layoutData); }
		public void setText(String checkBoxLabel)                  { _CheckBox.setText(checkBoxLabel); }
	} // end of class ControlCheckBox
	
	private static final String   PROPERTY_TEXT_TEMPLATE         = "%s.setting_text_%d"; //$NON-NLS-1$
	private static final String[] RGCONF_FILE_EXT_LIST          = new String[] { "." + RGConfiguration.RGCONF_FILE_EXTENSION }; //$NON-NLS-1$
	private static final String[] PHYS_DATA_MODEL_FILE_EXT_LIST = new String[] { Constants.PHYSICAL_DATA_MODEL_FILE_EXTENSION };
	private static final String   LABEL_SUFFIX                  = ":";   //$NON-NLS-1$
	private static final String   LABEL_TEMPLATE                = "%s" + LABEL_SUFFIX; //$NON-NLS-1$
	private static final String   REMEMBER_SETIINGS_KEY         = "REMEMBER_RG_SETTINGS"; //$NON-NLS-1$
	private static final int      INPUT_FIELD_COUNT             = 4;
	public  static final int      IDX_RG_CONFIG_FILE            = 0;
	public  static final int      IDX_SOURCE_MODEL_FILE         = 1;
	public  static final int      IDX_CHECK_TABLE_MODEL_FILE    = 2;
	public  static final int      IDX_TARGET_MODEL_FILE         = 3;
	
	private String                        dbmFileName;
	private Label                         configTypeText;
	private RGConfiguration               rgConfiguration;
	private ObjectTypeSelectionPage       objectTypeSelectionPage;
	private ABAPExtractSettingsWizardPage abapPage;
	private EntryFieldControl             EFControlsArr[];
	private ControlCheckBox               ControlChkBoxArr[];
	private boolean                       initComplete = false;
	

  	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	


  	// public RResourcePage(RGConfiguration rgConfiguration, String dbmFileName) {
	public RGResourcePage(String dbmFileName) {
		super("jobgenresourcespage", Messages.RGResourcePage_Title, null);
		this.rgConfiguration  = null;
		this.dbmFileName      = dbmFileName;
		this.EFControlsArr    = new EntryFieldControl[INPUT_FIELD_COUNT];
		this.ControlChkBoxArr = new ControlCheckBox[INPUT_FIELD_COUNT];

		setDescription(Messages.RGResourcePage_Desc);
	}


	@Override
	public Composite createControlImpl(Composite parent) {
		boolean rememberSettings = AdvancedSettingsPreferencePage.isSettingEnabled(REMEMBER_SETIINGS_KEY); //$NON-NLS-1$

		Composite tmpComp;
		Composite baseComp = new Composite(parent, SWT.NONE);
		initComplete       = false;

		// *****************************************************
		// RG Configuration file: label and 'Browse' button
		createLabelTextButtonCombination(baseComp, Messages.RGResourcePage_RGConf, null, 
		                                 RGCONF_FILE_EXT_LIST, rememberSettings, null, IDX_RG_CONFIG_FILE);

		// Source model file: label and 'Browse' button
		createLabelTextButtonCombination(baseComp, Messages.RGResourcePage_SrcTblModel, this.dbmFileName, 
                                       PHYS_DATA_MODEL_FILE_EXT_LIST, rememberSettings, null, IDX_SOURCE_MODEL_FILE);

		if (ModeManager.isCWEnabled()) {
			// CW mode: Create the 'Check Table Model' part
			tmpComp = baseComp;
		}
		else {
			// NON-CW mode: DO NOT create the 'Check Table Model' part
			tmpComp = null;
		} // end of (else) if (ModeManager.isCWEnabled())
		createLabelTextButtonCombination(tmpComp, Messages.RGResourcePage_ChkTblModel, this.dbmFileName, 
                                       PHYS_DATA_MODEL_FILE_EXT_LIST, rememberSettings,
                                       Messages.RGResourcePage_CBox_AdditionalModel, IDX_CHECK_TABLE_MODEL_FILE);

		// Target model file: label and 'Browse' button
		createLabelTextButtonCombination(baseComp, Messages.RGResourcePage_TrgTblModel, this.dbmFileName, 
                                       PHYS_DATA_MODEL_FILE_EXT_LIST, rememberSettings, null, IDX_TARGET_MODEL_FILE);
		
		
		GridData gd2 = new GridData(SWT.FILL, SWT.TOP, true, false);
		gd2.horizontalSpan = 3;
		Label line = new Label(baseComp, SWT.SEPARATOR | SWT.HORIZONTAL);
		line.setLayoutData(gd2);

		Composite comp = new Composite(baseComp, SWT.NONE);

		GridLayout gl = new GridLayout(2, false);
		comp.setLayout(gl);
		GridData gd3 = new GridData(SWT.FILL, SWT.FILL, true, false);
		gd3.horizontalSpan = 3;
		comp.setLayoutData(gd3);

		Label l = new Label(comp, SWT.NONE);
		l.setText(Messages.RGResourcePage_Label_ConfType);
		configTypeText = new Label(comp, SWT.NONE);
		configTypeText.setText(Messages.RGResourcePage_ConfTypeNone);
		configTypeText.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		initComplete = true;
		dialogChanged();

		// determine the 1st text field that is enabled ...
		for (int idx = 0; idx < EFControlsArr.length; idx ++) {
			if (this.EFControlsArr[idx].isEnabled()) {
				// ==> set the focus to that field
				setInitialFocusToDlgFld(this.EFControlsArr[idx].getInputField());
				break;
			}
		} // end of for (int idx = 0; idx < EFControlsArr.length; idx ++)

		return baseComp;
	}


	private void createLabelTextButtonCombination(Composite comp, String labelName, String predefValue,
	                                              String fileExtensions[], boolean rememberResources,
	                                              String  ctrlChkBoxLabel, final int efIdx) {

		EntryFieldControl efControl; 
		ControlCheckBox   controlCheckBox;
		Text              textModelInputFile;

		if (comp == null) {
			this.ControlChkBoxArr[efIdx] = new ControlCheckBox(null, null);
		}
		else {
			controlCheckBox = null;
			if (ctrlChkBoxLabel != null) {
				GridData gd2x = new GridData(SWT.FILL, SWT.TOP, true, false);
				gd2x.horizontalSpan = 3;

				controlCheckBox = new ControlCheckBox(comp);
				controlCheckBox.setText(ctrlChkBoxLabel);
				controlCheckBox.setLayoutData(gd2x);
			}
			
			GridLayout gl = new GridLayout(3, false);
			comp.setLayout(gl);
			GridData gd2 = new GridData(SWT.FILL, SWT.FILL, true, true);
			comp.setLayoutData(gd2);

			Label label = new Label(comp, SWT.NULL);
			label.setText(String.format(LABEL_TEMPLATE, labelName));

			textModelInputFile = new Text(comp, SWT.BORDER | SWT.SINGLE);
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			textModelInputFile.setLayoutData(gd);
			textModelInputFile.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					dialogChanged();
				}

			});
			if (predefValue != null) {
				textModelInputFile.setText(predefValue);
				textModelInputFile.setEnabled(false);
			} 
			else {
				if (rememberResources) {
					this.configureTextForProperty(textModelInputFile, String.format(PROPERTY_TEXT_TEMPLATE, this.getName(), efIdx));
				}
			}

			Button button = new Button(comp, SWT.PUSH);
			button.setText(Messages.RGResourcePage_Bttn_Browse);
			button.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					handleBrowse(efIdx);
				}
			});

			efControl                 = new EntryFieldControl(label, textModelInputFile, button, fileExtensions); 
			this.EFControlsArr[efIdx] = efControl;

			// if there is a check box ...
			if (ctrlChkBoxLabel != null) {
				// ==> associate the EntryFieldControl with that check box
				controlCheckBox.setEntryFieldControl(efControl);
				this.ControlChkBoxArr[efIdx] = controlCheckBox;
			}
		} // end of (else) if (comp == null)  

		return;
	} // end of createLabelTextButtonCombination()
	

	protected boolean dialogChanged() {
		boolean success = false;

		if (initComplete) {
			if (configTypeText != null) {
				configTypeText.setText(Messages.RGResourcePage_ConfTypeNone); 
			}
			this.EFControlsArr[IDX_SOURCE_MODEL_FILE].setEnabled(false);
			this.ControlChkBoxArr[IDX_CHECK_TABLE_MODEL_FILE].setEnabled(false);
			this.EFControlsArr[IDX_TARGET_MODEL_FILE].setEnabled(false);

			setMessage(null, WizardPage.WARNING);
			
			// this will only check the first text field (the RG conf)
			success = checkEnabledEntryFldControls();

			if (success) {
				try {
					IFile rgConfigFile = this.getResource(IDX_RG_CONFIG_FILE);
					if (rgConfigFile != null && rgConfigFile.exists()) {
						RGConfiguration conf     = new RGConfiguration(rgConfigFile);
						RMRGMode        confMode = conf.getMode();
						RMRGMode        thisMode = ModeManager.getActiveMode();

						if (!thisMode.equals(confMode)) {
							String msg = Messages.RGResourcePage_Err_WrongConfigMode;
							msg = MessageFormat.format(msg, confMode.getName(), thisMode.getName());
							setErrorMessage(msg);
							success = false;
						}

						if (success) {
							ValidationResult valResult = conf.createValidator().validate(conf, null);
							for (ValidatorMessage msg : valResult.getMessages()) {
								int sev = (Integer) msg.getAttribute(IMarker.SEVERITY);
								if (sev == IMarker.SEVERITY_ERROR) {
									setMessage(Messages.RGResourcePage_ErrorInConfig, WizardPage.WARNING);
									success = false;
									break;
								}
							}
						} // end of if (success)

						if (success) {
							if (conf.getConfigurationType() == CONFIGURATION_TYPE.SAP_EXTRACT) {
								this.EFControlsArr[IDX_SOURCE_MODEL_FILE].setEnabled(false);
								this.ControlChkBoxArr[IDX_CHECK_TABLE_MODEL_FILE].setEnabled(false);
								this.EFControlsArr[IDX_TARGET_MODEL_FILE].setEnabled(true);
								if (configTypeText != null) {
									configTypeText.setText(Messages.RGResourcePage_ConfTypeSAPExtract);
								}
							} else if (conf.getConfigurationType() == CONFIGURATION_TYPE.SAP_LOAD) {
								this.EFControlsArr[IDX_SOURCE_MODEL_FILE].setEnabled(true);
								this.EFControlsArr[IDX_TARGET_MODEL_FILE].setEnabled(false);
								this.ControlChkBoxArr[IDX_CHECK_TABLE_MODEL_FILE].setEnabled(false);
								if (configTypeText != null) {
									configTypeText.setText(Messages.RGResourcePage_ConfTypeSAPLoad);
								}
							} else if (conf.getConfigurationType() == CONFIGURATION_TYPE.MOVE) {
								if (ModeManager.isCWEnabled()) {
									this.EFControlsArr[IDX_SOURCE_MODEL_FILE].setEnabled(true);
									this.EFControlsArr[IDX_TARGET_MODEL_FILE].setEnabled(true);
									
									// enable check box for transcoding configurations only
									if (conf.isTranscodingConfig()) {
										this.ControlChkBoxArr[IDX_CHECK_TABLE_MODEL_FILE].setEnabled(true);
									}
									if (configTypeText != null) {
										configTypeText.setText(Messages.RGResourcePage_ConfTypeMoveTranscode);
									}

									// source and target model must be different
									IFile srcModelFile = getResource(IDX_SOURCE_MODEL_FILE);
									if (srcModelFile != null) {
										if (srcModelFile.equals(getResource(IDX_TARGET_MODEL_FILE))) {
											setErrorMessage(Messages.RGResourcePage_Err_SrcTrgModel_Different);
											success = false;
										}
										else {
											// check table model must be different from source and target
											IFile checkModelFile = getResource(IDX_CHECK_TABLE_MODEL_FILE);
											if (checkModelFile != null) {
												if (checkModelFile.equals(getResource(IDX_SOURCE_MODEL_FILE)) ||
													 checkModelFile.equals(getResource(IDX_TARGET_MODEL_FILE))) {
													setErrorMessage(Messages.RGResourcePage_Err_ChkModel_Different);
													success = false;
												}
											}
										}
									}
								}
								else {
									setErrorMessage(Messages.RGResourcePage_Hint_CW_Move_Transcode);
									success = false;
								} // end of (else) if (ModeManager.isCWEnabled())
							} // end of(else) if (conf.getConfigurationType() == CONFIGURATION_TYPE.SAP_EXTRACT)
						} // end of if (success)
					} // end of if (rgConfigFile != null && rgConfigFile.exists())
				} // end of try 
				catch(Exception exc) {
					Activator.logException(exc);
					success = false;
				}
			} // end of if (success)

			// now check all fields again
			if (success) {
				success = checkEnabledEntryFldControls();
			}

			setPageComplete(success);
		}

		return(success);
	} // end of dialogChanged()


	public IFile getResource(int efIdx) {
		IFile             iFileRes = null;
		EntryFieldControl efCtrl   = this.EFControlsArr[efIdx];

		if (efCtrl.isEnabled()) {
			String fileName = efCtrl.getInputField().getText().trim();

			if (fileName != null && !fileName.isEmpty()) {
				IPath p = new Path(fileName);
				if (p.isValidPath(fileName)) {
					iFileRes = ResourcesPlugin.getWorkspace().getRoot().getFile(p);
				}
			}
		}

		return(iFileRes);
	}

	RGConfiguration getRGConfig() {
		return(this.rgConfiguration);
	}


	protected void handleBrowse(int idx) {
		ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(getShell(), 
		                                                                   new WorkbenchLabelProvider(), 
		                                                                   new BaseWorkbenchContentProvider());
		dialog.setTitle(this.getTitle());
		dialog.setMessage(this.getTitle());
		dialog.setInput(ResourcesPlugin.getWorkspace().getRoot());
		dialog.setAllowMultiple(false);

		IResource selectedResource = getResource(idx);
		if (selectedResource != null && selectedResource.exists()) {
			dialog.setInitialSelection(selectedResource);
		}

		final String[] fileExtensions = this.EFControlsArr[idx].getFileExts();
		dialog.addFilter(new ViewerFilter() {

			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				if (element instanceof IFile) {
					IFile f = (IFile) element;
					for (String ext : fileExtensions) {
						if (f.getName().endsWith(ext)) {
							return true;
						} else {
							return false;
						}
					}
				}
				return true;
			}
		});

		String ext = fileExtensions[0];
		final String errorMsg = MessageFormat.format(Messages.RGResourcePage_Select_File, ext);
		dialog.setValidator(new ISelectionStatusValidator() {

			@Override
			public IStatus validate(Object[] selection) {
				if (selection.length > 0) {
					Object o = selection[0];
					if (o instanceof IFile) {
						return new Status(IStatus.OK, Activator.PLUGIN_ID, ""); //$NON-NLS-1$
					}
				}
				return new Status(IStatus.ERROR, Activator.PLUGIN_ID, errorMsg);
			}
		});

		int r = dialog.open();

		if (r == Window.CANCEL) {
			return;
		}

		Object[] selectedFiles = dialog.getResult();
		Object o = selectedFiles[0];
		if (o instanceof IFile) {
			IFile f = (IFile) o;
			String s = f.getFullPath().toString();
			Text text = this.EFControlsArr[idx].getInputField();
			text.setText(s);
		}
	}


	@Override
	public boolean nextPressedImpl() {
		try {
			boolean isTranscoding                   = false;
			boolean containsReferenceCheckTables    = false;
			boolean containsLogicalTables           = false;
			boolean containsNonReferenceCheckTables = false;
			boolean containsIDocs                   = false;
			
			this.rgConfiguration = new RGConfiguration(getResource(IDX_RG_CONFIG_FILE));
			CONFIGURATION_TYPE confType = this.rgConfiguration.getConfigurationType();

			DBSupport.DBInstance dbInst;
			if (confType == CONFIGURATION_TYPE.SAP_LOAD) {
				dbInst        = Utils.getDBInstance(getResource(IDX_SOURCE_MODEL_FILE));
				containsIDocs = dbInst.containsIDocs();
			} 
			else {   // else of if (confType == CONFIGURATION_TYPE.SAP_LOAD)
				if (confType == CONFIGURATION_TYPE.MOVE) {
					if (rgConfiguration.isTranscodingConfig()) {
						isTranscoding = true;
					}

					if (this.abapPage != null) {
						this.abapPage.setPageComplete(true);
					}
				}

				// same logic for extract and move
				dbInst = Utils.getDBInstance(getResource(IDX_TARGET_MODEL_FILE));

				// NOTE: this means that the possible object selections for move jobs depend only
				//       on the available objects in the target model.
				containsReferenceCheckTables    = dbInst.containsReferenceChecktables();
				containsLogicalTables           = dbInst.containsLogicalTables();
				containsNonReferenceCheckTables = dbInst.containsNonReferenceChecktables();
				containsIDocs                   = dbInst.containsIDocs();
			} // end of (else) if (confType == CONFIGURATION_TYPE.SAP_LOAD)

			if (this.objectTypeSelectionPage != null) {
				this.objectTypeSelectionPage.setEnablement(containsLogicalTables, containsReferenceCheckTables, 
                                                       containsNonReferenceCheckTables, containsIDocs, isTranscoding);
			}
		}
		catch (Exception e) {
			handleException(e);
			return false;
		}

		return true;
	}


	// returns false if error
	private boolean checkEnabledEntryFldControls() {
		boolean chkSuccess     = true;
		boolean isPageComplete = true;
		String  errMsg         = null;

 		setPageComplete(true);
		setErrorMessage(null);

		for (int idx = 0; idx < this.EFControlsArr.length; idx++) {
			EntryFieldControl efCtrl = EFControlsArr[idx];

			if (efCtrl != null) {
				if (efCtrl.isEnabled()) {
					String efText        = efCtrl.getInputField().getText().trim();
					String labelName     = efCtrl.getLabel().getText().trim();
					String pureLabelName = labelName;
					if (labelName.endsWith(LABEL_SUFFIX)) {
						pureLabelName = labelName.substring(0, labelName.length() - LABEL_SUFFIX.length());
					}
					if (efText.length() == 0) {
						errMsg         = MessageFormat.format(Messages.RGResourcePage_Err_FieldIsEmpty, pureLabelName);
						isPageComplete = false;
						chkSuccess     = false;
						break;
					}

					IFile res = getResource(idx);
					if (chkSuccess && (res == null || !res.exists())) {
						errMsg         = MessageFormat.format(Messages.RGResourcePage_Err_FileDoesNotExist, pureLabelName);
						isPageComplete = false;
						chkSuccess     = false;
						break;
					}
				} // end of if (efCtrl.isEnabled())
			} // end of if (efCtrl != null)
		} // end of for (int idx = 0; idx < this.EFControlsArr.length; idx++)

		setErrorMessage(errMsg);
		setPageComplete(isPageComplete);

		return(chkSuccess);
	}


	@Override
	public void performHelp() {
		PlatformUI.getWorkbench().getHelpSystem().displayHelp(Utils.getHelpID("rgwiz_select_models")); //$NON-NLS-1$
	}


	public void setABAPExtractSettings(ABAPExtractSettingsWizardPage abapPage) {
		this.abapPage = abapPage;
	}


	public void setObjectTypeSelectionPage(ObjectTypeSelectionPage objectTypeSelectionPage) {
		this.objectTypeSelectionPage = objectTypeSelectionPage;
	}

	public void setVisible(boolean visible) {
		super.setVisible(visible);

		if (visible) {
			if (IISClient.getVersion() == IISClientVersion.v11xOrLater && !ServerRequestUtil.isIISServerConnectionLibsUsed()) {
				// IISServerConnectionLibs are not used but they are required for a IS Picasso installation
				String errMsg = ServerMessageCatalog.getDefaultCatalog().getText("141000E");
				MessageDialog.openError(getShell(), Messages.TitleError, errMsg);
				setPageComplete(false);

				this.initComplete = false;
				this.EFControlsArr[IDX_RG_CONFIG_FILE].setEnabled(false);
			} // end of if (IISClient.getVersion() == IISClientVersion.v11xOrLater && !ServerRequestUtil.isIISServerConnectionLibsUsed())
		}
	}

} // end of class RGResourcesPage
