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
// Module Name : com.ibm.iis.sappack.gen.tools.sap.rmconf
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.iis.sappack.gen.tools.sap.rmconf;


import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.ibm.iis.sappack.gen.common.ui.CWConstants;
import com.ibm.iis.sappack.gen.common.ui.editors.ConfigurationBase;
import com.ibm.iis.sappack.gen.common.ui.editors.EditorPageBase;
import com.ibm.iis.sappack.gen.common.ui.editors.IControlFactory;
import com.ibm.iis.sappack.gen.common.ui.util.ImageProvider;
import com.ibm.iis.sappack.gen.common.ui.util.Utils;
import com.ibm.is.sappack.gen.common.Constants;
import com.ibm.is.sappack.gen.common.ui.Activator;
import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.common.ui.preferences.AdvancedSettingsPreferencePage;
import com.ibm.is.sappack.gen.tools.sap.importer.helper.configuration.TableImporterOptions;
import com.ibm.is.sappack.gen.tools.sap.utilities.Utilities;


public class CWImportOptionsPage extends EditorPageBase {

	private SelectionAdapter listener = new SelectionListener();

	IControlFactory controlFactory;

	private Button radioBlackListButton;
	private Button radioWhiteListButton;
	//	private Combo comboBlackListFile;
	//	private Button buttonBlackListFileBrowser;

	//private Combo comboProfiles;
	//	private Button buttonProfileSettings;

	private Button radioRelationsArr[];

	public static enum RelationTypes {
		SAPPKsNoFKsNotEnforced, NoPKsNoFKs, PKsNoFKs, PKsFKsNotEnforced, PKsFKsEnforced
	};

	private Button radioUseDataTypesArr[];

	public static enum DataTypes {
		AllTypes, VarCharType
	};

	private Button checkboxColumnsNullable;
	private Combo comboLengthFactor;
	private Text textColumnDefaultValue;

	private Button checkboxCreateAdditionalTechnicalColumns;
	private Button checkboxAdditionalTechnicalColumnsNullable;

	private Button radioCheckTablesOptionArr[];

	public static enum ChkTblOptions {
		NoCheckTables, JoinedAndCheckTables, TranscodingTables
	};

	/*
	private Composite stackComposite;
	private StackLayout stackLayout;
	private Composite emptyComposite;
	*/
	private Composite settingsComposite;

	private Text tableListField;

	private static final String WIDGET_SETTINGS_PREFIX  = "CWPrefix"; //$NON-NLS-1$
	public static final String SETTING_TABLE_LIST       = WIDGET_SETTINGS_PREFIX + ".TableList"; //$NON-NLS-1$
	public static final String SETTING_RELATION_IDX     = WIDGET_SETTINGS_PREFIX + ".relationIdx"; //$NON-NLS-1$
	public static final String SETTING_COL_TYPE_IDX     = WIDGET_SETTINGS_PREFIX + ".colTypeIdx"; //$NON-NLS-1$
	public static final String SETTING_COL_LEN_FACTOR   = WIDGET_SETTINGS_PREFIX + ".colLenFactor"; //$NON-NLS-1$
	public static final String SETTING_COL_DEFAULT_VAL  = WIDGET_SETTINGS_PREFIX + ".colDefaultValue"; //$NON-NLS-1$
	public static final String SETTING_COL_IS_NULLABLE  = WIDGET_SETTINGS_PREFIX + ".colInsNullable"; //$NON-NLS-1$
	public static final String SETTING_ADD_TECH_FIELDS  = WIDGET_SETTINGS_PREFIX + ".addTechFields"; //$NON-NLS-1$
	public static final String SETTING_TFLD_IS_NULLABLE = WIDGET_SETTINGS_PREFIX + ".techFieldIsNullable"; //$NON-NLS-1$
	public static final String SETTING_CHK_TBL_OPT_IDX  = WIDGET_SETTINGS_PREFIX + ".checkTablesOptionIdx";//$NON-NLS-1$

	public static final String SETTING_BLACK_OR_WHILTELIST_BUTTON = WIDGET_SETTINGS_PREFIX + ".blackOrWhiteListButton";//$NON-NLS-1$


	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	


	private class SelectionListener extends SelectionAdapter {
		public void widgetSelected(SelectionEvent e) {
			updateSelections();
		}
	}

	public static final String TABNAME = Messages.CWImportOptionsPage_0;
	
	public CWImportOptionsPage() {
		super(TABNAME, Messages.CWTblImpOptWzdPage_title, Messages.CWTblImpOptWzdPage_desc, Utils.getHelpID("rmconfeditor_import_options_cw")); //$NON-NLS-1$
	}

	@Override
	public Image getImage() {
		return ImageProvider.getImage(ImageProvider.IMAGE_TABLE_IMPORT_OPTIONS);
	}

	public void createControls(IControlFactory controlFactory, final Composite parent) {
		this.controlFactory     = controlFactory;
//		this.isCreatingControls = true;

		Composite mainComposite = controlFactory.createComposite(parent, SWT.NULL);

		mainComposite.setLayout(new GridLayout(1, false));
		mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		Composite fileSelectionGroup = controlFactory.createGroup(mainComposite, Messages.CWTblImpOptWzdPage_3, SWT.NONE);

		fileSelectionGroup.setLayout(new GridLayout(3, false));
		fileSelectionGroup.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));

		this.radioBlackListButton = controlFactory.createButton(fileSelectionGroup, SWT.RADIO);
		this.radioBlackListButton.setText(Messages.CWTblImpOptWzdPage_4);

		this.radioWhiteListButton = controlFactory.createButton(fileSelectionGroup, SWT.RADIO);
		this.radioWhiteListButton.setText(Messages.CWTblImpOptWzdPage_5);

		this.configureRadioButtonsForProperty(new Button[] { this.radioBlackListButton, this.radioWhiteListButton }, SETTING_BLACK_OR_WHILTELIST_BUTTON);

		//		this.radioBlackListButton.setSelection(true);

		controlFactory.createLabel(fileSelectionGroup, SWT.NONE);

		Label fileSelectionLabel = controlFactory.createLabel(fileSelectionGroup, SWT.NONE);
		fileSelectionLabel.setText(Messages.CWImportOptionsPage_1);
		fileSelectionLabel.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));

		/*
		comboBlackListFile = controlFactory.createCombo(fileSelectionGroup, SWT.SINGLE | SWT.BORDER);
		comboBlackListFile.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
		this.configureComboForProperty(comboBlackListFile, SETTING_BLACKLISTFILE, ""); //$NON-NLS-1$

		// browse (part of the original composite)
		buttonBlackListFileBrowser = controlFactory.createButton(fileSelectionGroup, SWT.PUSH);
		GridData buttonData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		buttonBlackListFileBrowser.setLayoutData(buttonData);
		buttonBlackListFileBrowser.setText(CWMessages.CWTblImpOptWzdPage_7);
		buttonBlackListFileBrowser.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(parent.getShell(), SWT.OPEN);
				String path = dialog.open();
				comboBlackListFile.setText(path);
			}
		})
		;
		*/

		tableListField = new Text(fileSelectionGroup, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL | SWT.H_SCROLL);
		GridData textGD = new GridData(SWT.DEFAULT, 100);
		textGD.horizontalAlignment = SWT.FILL;
		textGD.verticalAlignment = SWT.FILL;
		textGD.grabExcessHorizontalSpace = true;
		textGD.grabExcessVerticalSpace = true;
		this.tableListField.setLayoutData(textGD);
		this.configureTextForProperty(tableListField, SETTING_TABLE_LIST);

//		Composite profileGroup = controlFactory.createGroup(mainComposite, CWMessages.CWTblImpOptWzdPage_1, SWT.NONE);
//		profileGroup.setLayout(new GridLayout(2, false));
//		profileGroup.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
//
//		Label profileLabel = controlFactory.createLabel(profileGroup, SWT.NONE);
//		profileLabel.setText(CWMessages.CWTblImpOptWzdPage_2);

		
//		this.comboProfiles = controlFactory.createCombo(profileGroup, SWT.SINGLE | SWT.READ_ONLY | SWT.BORDER);
//		this.comboProfiles.setItems(PROFILE_ARRAY);
//		this.comboProfiles.select(0);
//		this.comboProfiles.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				updateSettings();
//			}
//		});
//		this.configureComboForProperty(comboProfiles, SETTING_PROFILE_IDX);

		this.settingsComposite = controlFactory.createGroup(mainComposite, Messages.CWImportOptionsPage_2, SWT.NONE); // Section.TWISTIE | Section.COMPACT);
		this.settingsComposite.setLayout(new GridLayout(2, true));
		this.settingsComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		createRelationsOptionsGroup(controlFactory, this.settingsComposite);
		createColumnsOptionsGroup(controlFactory, this.settingsComposite);
		createAdditionalFieldsOptionsGroup(controlFactory, this.settingsComposite);
		createCheckTableOptions(controlFactory, this.settingsComposite);

		updateSettings();
	}

	private void createRelationsOptionsGroup(IControlFactory controlFactory, Composite container) {
		Button newButton;
		String buttonText;

		Composite optionsGroup = controlFactory.createGroup(container, Messages.CWTblImpOptWzdPage_8, SWT.NULL);

		optionsGroup.setLayout(new GridLayout(1, true));
		optionsGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		this.radioRelationsArr = new Button[RelationTypes.values().length];
		for (RelationTypes curRelType : RelationTypes.values()) {
			newButton = controlFactory.createButton(optionsGroup, SWT.RADIO);
			switch (curRelType) {
			case SAPPKsNoFKsNotEnforced:
				buttonText = Messages.CWTblImpOptWzdPage_relationSAPPKsNoFKsNotEnforced;
				break;
			case NoPKsNoFKs:
				buttonText = Messages.CWTblImpOptWzdPage_relationNoPKsNoFKs;
				break;
			case PKsNoFKs:
				buttonText = Messages.CWTblImpOptWzdPage_relationPKsNoFKs;
				break;
			case PKsFKsNotEnforced:
				buttonText = Messages.CWTblImpOptWzdPage_relationPKsFKsNotEnforced;
				break;
			case PKsFKsEnforced:
				buttonText = Messages.CWTblImpOptWzdPage_relationPKsFKsEnforced;
				break;
			default:
				buttonText = ""; //$NON-NLS-1$
			}
			newButton.setText(buttonText);
			this.radioRelationsArr[curRelType.ordinal()] = newButton;
		}

		this.configureRadioButtonsForProperty(this.radioRelationsArr, SETTING_RELATION_IDX);
	}

	private void createColumnsOptionsGroup(IControlFactory controlFactory, Composite container) {

		// Create the group, the one that contains the radio buttons
		Composite optionsGroup = controlFactory.createGroup(container, Messages.CWTblImpOptWzdPage_11, SWT.NULL);

		// Create the grid layout inside the options group, only 1 column
		optionsGroup.setLayout(new GridLayout(1, true));
		optionsGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		this.radioUseDataTypesArr = new Button[DataTypes.values().length];
		this.radioUseDataTypesArr[DataTypes.AllTypes.ordinal()] = controlFactory.createButton(optionsGroup, SWT.RADIO);
		this.radioUseDataTypesArr[DataTypes.AllTypes.ordinal()].setText(Messages.CWTblImpOptWzdPage_12);
		this.radioUseDataTypesArr[DataTypes.AllTypes.ordinal()].addSelectionListener(this.listener);
		this.radioUseDataTypesArr[DataTypes.VarCharType.ordinal()] = controlFactory.createButton(optionsGroup, SWT.RADIO);
		this.radioUseDataTypesArr[DataTypes.VarCharType.ordinal()].setText(Messages.CWTblImpOptWzdPage_13);
		this.radioUseDataTypesArr[DataTypes.VarCharType.ordinal()].addSelectionListener(this.listener);
		this.configureRadioButtonsForProperty(this.radioUseDataTypesArr, SETTING_COL_TYPE_IDX);

		Composite lengthFactorComposite = controlFactory.createComposite(optionsGroup, SWT.NULL);

		lengthFactorComposite.setLayout(new GridLayout(2, true));

		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalIndent = 10;
		lengthFactorComposite.setLayoutData(gridData);

		Label lengthFactorLabel = controlFactory.createLabel(lengthFactorComposite, SWT.NONE);
		lengthFactorLabel.setText(Messages.CWTblImpOptWzdPage_14);
		this.comboLengthFactor = controlFactory.createCombo(lengthFactorComposite, SWT.SINGLE | SWT.READ_ONLY | SWT.BORDER);
		String lengthFactorTemp[] = new String[CWConstants.MAX_LEN_FACTOR];
		for (int idx = 0; idx < CWConstants.MAX_LEN_FACTOR; idx++) {
			lengthFactorTemp[idx] = Integer.toString(idx + 1);
		}
		this.comboLengthFactor.setItems(lengthFactorTemp);
		//		this.comboLengthFactor.select(0);
		this.configureComboForPropertyStr(comboLengthFactor, SETTING_COL_LEN_FACTOR);

		Label defaultValueLabel = controlFactory.createLabel(lengthFactorComposite, SWT.NONE);
		defaultValueLabel.setText(Messages.CWTblImpOptWzdPage_19);
		this.textColumnDefaultValue = new Text(lengthFactorComposite, SWT.BORDER);
		this.configureTextForProperty(textColumnDefaultValue, SETTING_COL_DEFAULT_VAL);

		this.checkboxColumnsNullable = controlFactory.createButton(lengthFactorComposite, SWT.CHECK);
		this.checkboxColumnsNullable.setText(Messages.CWTblImpOptWzdPage_20);
		GridData spanGridData = new GridData();
		spanGridData.horizontalSpan = 2;
		this.checkboxColumnsNullable.setLayoutData(spanGridData);
		this.configureCheckboxForProperty(checkboxColumnsNullable, SETTING_COL_IS_NULLABLE);
	}

	private void createAdditionalFieldsOptionsGroup(IControlFactory controlFactory, final Composite container) {

		Composite optionsGroup = controlFactory.createGroup(container, Messages.CWTblImpOptWzdPage_21, SWT.NULL);

		// Create the grid layout inside the options group, only 1 column
		optionsGroup.setLayout(new GridLayout(1, true));
		optionsGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		this.checkboxCreateAdditionalTechnicalColumns = controlFactory.createButton(optionsGroup, SWT.CHECK);
		this.checkboxCreateAdditionalTechnicalColumns.setText(Messages.CWTblImpOptWzdPage_22);
		this.checkboxCreateAdditionalTechnicalColumns.addSelectionListener(this.listener);
		this.configureCheckboxForProperty(checkboxCreateAdditionalTechnicalColumns, SETTING_ADD_TECH_FIELDS);

		this.checkboxAdditionalTechnicalColumnsNullable = controlFactory.createButton(optionsGroup, SWT.CHECK);
		this.checkboxAdditionalTechnicalColumnsNullable.setText(Messages.CWTblImpOptWzdPage_23);
		GridData gridData = new GridData();
		gridData.horizontalIndent = 10;
		this.checkboxAdditionalTechnicalColumnsNullable.setLayoutData(gridData);
		this.checkboxAdditionalTechnicalColumnsNullable.addSelectionListener(listener);
		this.configureCheckboxForProperty(checkboxAdditionalTechnicalColumnsNullable, SETTING_TFLD_IS_NULLABLE);
	}

	private void createCheckTableOptions(IControlFactory controlFactory, final Composite container) {

		Composite optionsGroup = controlFactory.createGroup(container, Messages.CWTblImpOptWzdPage_24, SWT.NULL);

		// Create the grid layout inside the options group, only 1 column
		optionsGroup.setLayout(new GridLayout(1, true));
		optionsGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		this.radioCheckTablesOptionArr = new Button[ChkTblOptions.values().length];
		this.radioCheckTablesOptionArr[ChkTblOptions.NoCheckTables.ordinal()] = controlFactory.createButton(optionsGroup, SWT.RADIO);
		this.radioCheckTablesOptionArr[ChkTblOptions.NoCheckTables.ordinal()].setText(Messages.CWTblImpOptWzdPage_25);
		this.radioCheckTablesOptionArr[ChkTblOptions.NoCheckTables.ordinal()].addSelectionListener(this.listener);
		this.radioCheckTablesOptionArr[ChkTblOptions.JoinedAndCheckTables.ordinal()] = controlFactory.createButton(optionsGroup, SWT.RADIO);
		this.radioCheckTablesOptionArr[ChkTblOptions.JoinedAndCheckTables.ordinal()].setText(Messages.CWTblImpOptWzdPage_38);
		this.radioCheckTablesOptionArr[ChkTblOptions.JoinedAndCheckTables.ordinal()].addSelectionListener(this.listener);
		this.radioCheckTablesOptionArr[ChkTblOptions.TranscodingTables.ordinal()] = controlFactory.createButton(optionsGroup, SWT.RADIO);
		this.radioCheckTablesOptionArr[ChkTblOptions.TranscodingTables.ordinal()].setText(Messages.CWTblImpOptWzdPage_27);
		this.radioCheckTablesOptionArr[ChkTblOptions.TranscodingTables.ordinal()].addSelectionListener(this.listener);
		this.configureRadioButtonsForProperty(radioCheckTablesOptionArr, SETTING_CHK_TBL_OPT_IDX);
	}

	private void updateSelections() {

		// Enable/disable the "Allow technical fields to be nullable" checkbox
		this.checkboxAdditionalTechnicalColumnsNullable.setEnabled(this.checkboxCreateAdditionalTechnicalColumns.getSelection());
		// Unset "Allow technical fields to be nullable"
		if (!this.checkboxAdditionalTechnicalColumnsNullable.getEnabled()) {

			if (this.checkboxAdditionalTechnicalColumnsNullable.getSelection()) {
				this.checkboxAdditionalTechnicalColumnsNullable.setSelection(false);
				editor.getConfiguration().put(SETTING_TFLD_IS_NULLABLE, false);		
			}
			
		}

		// Disable Controls "Length factor" and "Default value" when data type
		// is set to varchar
		Button rbVarCharType = this.radioUseDataTypesArr[DataTypes.VarCharType.ordinal()];
		this.comboLengthFactor.setEnabled(rbVarCharType.getSelection());
		this.textColumnDefaultValue.setEnabled(rbVarCharType.getSelection());
		this.checkboxColumnsNullable.setEnabled(rbVarCharType.getSelection());
		if (!rbVarCharType.getSelection()) {
			this.selectButtonWithEvent(this.checkboxColumnsNullable, false);
		}
		CWTechnicalFieldsChangedEvent event = new CWTechnicalFieldsChangedEvent(this, this.checkboxCreateAdditionalTechnicalColumns.getSelection(),
		                                                                        this.checkboxAdditionalTechnicalColumnsNullable.getSelection());
		this.sendEvent(event);

	}

	private void updateSettings() {
		int profileIdx = RMConfiguration.CW_PROFILE_IDX_CUSTOM; //this.comboProfiles.getSelectionIndex();

		if (profileIdx != RMConfiguration.CW_PROFILE_IDX_CUSTOM) {
			resetSettings();
		}

		switch (profileIdx) {
		case RMConfiguration.CW_PROFILE_IDX_STAGING:
			// must match with the setting in RMConfiguration.createEmptyRMConfiguation()
			this.selectButtonWithEvent(this.radioRelationsArr[RelationTypes.SAPPKsNoFKsNotEnforced.ordinal()], true);
			this.selectButtonWithEvent(this.radioUseDataTypesArr[DataTypes.VarCharType.ordinal()], true);
			this.selectComboWithEvent(this.comboLengthFactor, 0);
			this.textColumnDefaultValue.setText(""); //$NON-NLS-1$
			this.selectButtonWithEvent(this.checkboxColumnsNullable, false);
			this.selectButtonWithEvent(this.checkboxCreateAdditionalTechnicalColumns, true);
			this.selectButtonWithEvent(this.checkboxAdditionalTechnicalColumnsNullable, true);
			this.selectButtonWithEvent(this.radioCheckTablesOptionArr[ChkTblOptions.JoinedAndCheckTables.ordinal()], true);
			updateSelections();
			Utilities.disableCompositeWithExceptions(this.settingsComposite);
			break;

		case RMConfiguration.CW_PROFILE_IDX_ALIGNMENT:
			this.selectButtonWithEvent(this.radioRelationsArr[RelationTypes.PKsNoFKs.ordinal()], true);
			this.selectButtonWithEvent(this.radioUseDataTypesArr[DataTypes.VarCharType.ordinal()], true);
			this.selectComboWithEvent(this.comboLengthFactor, 1);
			this.textColumnDefaultValue.setText(""); //$NON-NLS-1$
			this.selectButtonWithEvent(this.checkboxColumnsNullable, true);
			this.selectButtonWithEvent(this.checkboxCreateAdditionalTechnicalColumns, true);
			this.selectButtonWithEvent(this.checkboxAdditionalTechnicalColumnsNullable, false);
			this.selectButtonWithEvent(this.radioCheckTablesOptionArr[ChkTblOptions.TranscodingTables.ordinal()], true);
			updateSelections();
			Utilities.disableCompositeWithExceptions(this.settingsComposite);
			break;

		case RMConfiguration.CW_PROFILE_IDX_PRELOAD:
			this.selectButtonWithEvent(this.radioRelationsArr[RelationTypes.PKsFKsNotEnforced.ordinal()], true);
			this.selectButtonWithEvent(this.radioUseDataTypesArr[DataTypes.VarCharType.ordinal()], true);
			this.selectComboWithEvent(this.comboLengthFactor, 0);
			this.textColumnDefaultValue.setText("''"); //$NON-NLS-1$
			this.selectButtonWithEvent(this.checkboxColumnsNullable, false);
			this.selectButtonWithEvent(this.checkboxCreateAdditionalTechnicalColumns, true);
			this.selectButtonWithEvent(this.checkboxAdditionalTechnicalColumnsNullable, false);
			this.selectButtonWithEvent(this.radioCheckTablesOptionArr[ChkTblOptions.JoinedAndCheckTables.ordinal()], true);
			updateSelections();
			Utilities.disableCompositeWithExceptions(this.settingsComposite);
			break;

		case RMConfiguration.CW_PROFILE_IDX_CUSTOM:
		default:
			showProfileSettings(true);
			Utilities.setEnabledComposite(this.settingsComposite, true);
			updateSelections();
			this.settingsComposite.setEnabled(true);
			break;
		}
	}

	private void resetSettings() {
		for (RelationTypes curRelType : RelationTypes.values()) {
			this.selectButtonWithEvent(this.radioRelationsArr[curRelType.ordinal()], false);
		}

		this.selectButtonWithEvent(this.radioUseDataTypesArr[DataTypes.AllTypes.ordinal()], false);
		this.selectButtonWithEvent(this.radioUseDataTypesArr[DataTypes.VarCharType.ordinal()], false);

		this.selectButtonWithEvent(this.radioCheckTablesOptionArr[ChkTblOptions.NoCheckTables.ordinal()], false);
		this.selectButtonWithEvent(this.radioCheckTablesOptionArr[ChkTblOptions.JoinedAndCheckTables.ordinal()], false);
		this.selectButtonWithEvent(this.radioCheckTablesOptionArr[ChkTblOptions.TranscodingTables.ordinal()], false);
	}

	public static TableImporterOptions getTableImporterOptions(ConfigurationBase map) {
		TableImporterOptions tableImporterOptions = new TableImporterOptions();
		tableImporterOptions.setAtomicDomainPkgName(com.ibm.is.sappack.gen.tools.sap.constants.Constants.DEFAULT_PKG_NAME_ATOMIC_DOMAINS);

		// 1. black / white list
		int blackOrWhite = map.getInt(SETTING_BLACK_OR_WHILTELIST_BUTTON);
		String tableListText = map.get(SETTING_TABLE_LIST);
		/*
		String blackWhitelistFile = map.get(SETTING_BLACKLISTFILE);
		*/
		List<String> blackList = null;
		List<String> whiteList = null;
		if (blackOrWhite == 0) {
			blackList = Utils.getTableListFromTextField(tableListText);
		} else {
			whiteList = Utils.getTableListFromTextField(tableListText);
		}
		tableImporterOptions.setTableBlackList(blackList);
		tableImporterOptions.setTableWhiteList(whiteList);

		// 2. get other settings
		int relIx = map.getInt(SETTING_RELATION_IDX);
		RelationTypes selRelType = null;
		for (RelationTypes curRelType : RelationTypes.values()) {
			if (curRelType.ordinal() == relIx) {
				selRelType = curRelType;
				break;
			}
		}
		switch (selRelType) {
		case NoPKsNoFKs:
			tableImporterOptions.setRelationMode(TableImporterOptions.RELATIONS.NoPKs_NoFKs);
			break;
		case PKsFKsEnforced:
			tableImporterOptions.setEnforceForeignKeys(true);
			tableImporterOptions.setRelationMode(TableImporterOptions.RELATIONS.CW_PKs_FKs);
			break;
		case PKsFKsNotEnforced:
			tableImporterOptions.setRelationMode(TableImporterOptions.RELATIONS.CW_PKs_FKs);
			break;
		case PKsNoFKs:
			tableImporterOptions.setRelationMode(TableImporterOptions.RELATIONS.CW_PKs_NoFKs);
			break;
		case SAPPKsNoFKsNotEnforced:
			tableImporterOptions.setRelationMode(TableImporterOptions.RELATIONS.CW_SAP_PKs_FKs);
			break;

		default:
			tableImporterOptions.setRelationMode(TableImporterOptions.RELATIONS.NoPKs_NoFKs);
			Activator.getLogger().severe(Messages.CWImportOptionsPage_3 + selRelType.name());
		}

		int colTypeIdx = map.getInt(SETTING_COL_TYPE_IDX);

		if (colTypeIdx == DataTypes.AllTypes.ordinal()) {
			tableImporterOptions.setDataTypeMode(TableImporterOptions.DATATYPES.USE_ALL);
		} else {
			if (colTypeIdx == DataTypes.VarCharType.ordinal()) {
				tableImporterOptions.setDataTypeMode(TableImporterOptions.DATATYPES.USE_VARCHAR_ONLY);

				int lenFactor = map.getInt(SETTING_COL_LEN_FACTOR);
				tableImporterOptions.setVarcharLengthFactor(lenFactor);

				String defValue = map.get(SETTING_COL_DEFAULT_VAL);
				tableImporterOptions.setColumnDefaultValue(defValue);
//				tableImporterOptions.setAllowColumnsToBeNullable(true);
				tableImporterOptions.setAllowColumnsToBeNullable(map.getBoolean(SETTING_COL_IS_NULLABLE));
			}
		}

		// set the technical fields options to false, will be handles by technical fields page
		tableImporterOptions.setCreateTechnicalFields(map.getBoolean(SETTING_ADD_TECH_FIELDS));
		tableImporterOptions.setAllowTechnicalFieldsToBeNullable(map.getBoolean(SETTING_TFLD_IS_NULLABLE));

		// determine radio button that is selected 
		int chkTableOpt = map.getInt(SETTING_CHK_TBL_OPT_IDX);
		ChkTblOptions selChkTblOption = ChkTblOptions.NoCheckTables;
		for (ChkTblOptions curChkTblOption : ChkTblOptions.values()) {
			if (chkTableOpt == curChkTblOption.ordinal()) {
				selChkTblOption = curChkTblOption;
			}
		}

		switch (selChkTblOption) {
		case NoCheckTables:
			tableImporterOptions.setCheckTableOption(TableImporterOptions.CHECKTABLEOPTIONS.NO_CHECKTABLES);
			tableImporterOptions.setExtractRecursively(false);
			break;
		case JoinedAndCheckTables:
			tableImporterOptions.setCheckTableOption(TableImporterOptions.CHECKTABLEOPTIONS.JOINED_CHECK_AND_TEXT_TABLES);
			tableImporterOptions.setExtractRecursively(false);
			break;
		case TranscodingTables:
			tableImporterOptions.setCheckTableOption(TableImporterOptions.CHECKTABLEOPTIONS.TRANSCODING_TABLES);
			tableImporterOptions.setExtractRecursively(false);
			break;

		default:
			tableImporterOptions.setCheckTableOption(TableImporterOptions.CHECKTABLEOPTIONS.NO_CHECKTABLES);
			Activator.getLogger().severe(Messages.CWImportOptionsPage_4 + selChkTblOption.name());
		}

		if (isCPICAllowed()) {
			tableImporterOptions.setAbapTransferMethod(Constants.ABAP_TRANSFERMETHOD_RFC | Constants.ABAP_TRANSFERMETHOD_CPIC);
		}

		// 3. overwrite settings if predefined profile

		// TODO
		return tableImporterOptions;
	}

	private void showProfileSettings(boolean show) {
		controlFactory.expandGroup(settingsComposite, show);
	}
	
	public static boolean isCPICAllowed() {
		return AdvancedSettingsPreferencePage.isSettingEnabled("CW_CPIC_ENABLED"); //$NON-NLS-1$
	}
}
