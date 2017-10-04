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
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.Section;

import com.ibm.iis.sappack.gen.common.ui.editors.ConfigurationBase;
import com.ibm.iis.sappack.gen.common.ui.editors.EditorPageBase;
import com.ibm.iis.sappack.gen.common.ui.editors.IControlFactory;
import com.ibm.iis.sappack.gen.common.ui.util.ImageProvider;
import com.ibm.iis.sappack.gen.common.ui.util.Utils;
import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.common.ui.WidgetIDConstants;
import com.ibm.is.sappack.gen.common.ui.WidgetIDUtils;
import com.ibm.is.sappack.gen.tools.sap.importer.helper.configuration.ImporterOptionsBase.CHECKTABLEOPTIONS;
import com.ibm.is.sappack.gen.tools.sap.importer.helper.configuration.ImporterOptionsBase.RELATIONS;
import com.ibm.is.sappack.gen.tools.sap.importer.helper.configuration.TableImporterOptions;
import com.ibm.is.sappack.gen.tools.sap.importer.idoctypes.IDocImporterOptions;


public class SAPPackImportOptionsPage extends EditorPageBase {

  	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	

	public static final String TABNAME = Messages.SAPPackImportOptionsPage_0;

	private static final int MAX_LEN_FACTOR = 4;

	protected static final String WIDGET_SETTINGS_PREFIX = "ImportOptions."; //$NON-NLS-1$
	protected static final String IDOC_WIDGET_SETTINGS_PREFIX = WIDGET_SETTINGS_PREFIX + "IDoc."; //$NON-NLS-1$
	protected static final String TABLES_WIDGET_SETTINGS_PREFIX = WIDGET_SETTINGS_PREFIX + "Tables."; //$NON-NLS-1$

	static final String SETTING_IDOC_FIELD_OPTION_ALLOW_ALL_TYPES = IDOC_WIDGET_SETTINGS_PREFIX + "allowAllTypes"; //$NON-NLS-1$
	public static final String SETTING_IDOC_NULLABLE_FIELDS_OPTION = IDOC_WIDGET_SETTINGS_PREFIX + "nullableFields"; //$NON-NLS-1$
	static final String SETTING_IDOC_FIELDS_OPTION_LENGTH_FACTOR = IDOC_WIDGET_SETTINGS_PREFIX + "lengthFactor"; //$NON-NLS-1$
	static final String SETTING_IDOC_CHECK_TABLE_OPTION = IDOC_WIDGET_SETTINGS_PREFIX + "checkTables"; //$NON-NLS-1$
	static final String SETTING_IDOC_TEXT_TABLE_OPTION = IDOC_WIDGET_SETTINGS_PREFIX + "textTables"; //$NON-NLS-1$
	private static final String SETTING_IDOC_CHECK_TABLE_BLACKLIST = IDOC_WIDGET_SETTINGS_PREFIX + "checkTableBlackList"; //$NON-NLS-1$

	static final String SETTING_TABLES_CHECK_TABLE_RECURSIVE = TABLES_WIDGET_SETTINGS_PREFIX + "nullableFields"; //$NON-NLS-1$
	static final String SETTING_TABLES_EXTRACT_RELATIONS = TABLES_WIDGET_SETTINGS_PREFIX + "extractRelations"; //$NON-NLS-1$
	static final String SETTING_TABLES_ENFORCE_RELATIONS = TABLES_WIDGET_SETTINGS_PREFIX + "enforceRelations"; //$NON-NLS-1$
	static final String SETTING_TABLES_CHECK_TABLE_OPTION = TABLES_WIDGET_SETTINGS_PREFIX + "checkTables"; //$NON-NLS-1$
	static final String SETTING_TABLES_TEXT_TABLE_OPTION = TABLES_WIDGET_SETTINGS_PREFIX + "textTables"; //$NON-NLS-1$
	private static final String SETTING_TABLES_CHECK_TABLE_BLACKLIST = TABLES_WIDGET_SETTINGS_PREFIX + "checkTableBlackList"; //$NON-NLS-1$

	private Button tables_checkboxCheckTables;
	private Button tables_recursiveSearch;
	private Button tables_checkboxTextTables;
	private Button tables_extractRelations;
	private Button tables_enforceRelations;
	private Text tables_checkTableBlackList;

	protected Button idoc_cbUseAllTypesOption;
	protected Button idoc_cbUseVarcharTypeOption;
	protected Combo idoc_comboLengthFactor;
	protected Button idoc_cbSegmentFieldsNullable;
	protected Composite idoc_dataTypeOptionsComposite;
	private Button idocs_checkboxCheckTables;
	private Button idocs_checkboxTextTables;
	private Text idocs_checkTableBlackList;

	public SAPPackImportOptionsPage() {
		super(TABNAME, Messages.SAPPackImportOptionsPage_1, Messages.SAPPackImportOptionsPage_2, Utils.getHelpID("rmconfeditor_import_options_sappack")); //$NON-NLS-1$
	}
	
	@Override
	public Image getImage() {
		return ImageProvider.getImage(ImageProvider.IMAGE_TABLE_IMPORT_OPTIONS);
	}

	private Text createTableListField(IControlFactory controlFactory, Composite parent, String labelText) {

		Composite blackListComp = controlFactory.createComposite(parent, SWT.NONE);
		blackListComp.setLayout(new GridLayout(2, false));
		blackListComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		Label l = controlFactory.createLabel(blackListComp, SWT.NONE);
		l.setText(labelText);
		l.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));

		Text tableListField = new Text(blackListComp, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL | SWT.H_SCROLL);
		GridData textGD = new GridData(); //SWT.DEFAULT, 100);
		textGD.horizontalAlignment = SWT.FILL;
		textGD.verticalAlignment = SWT.FILL;
		textGD.grabExcessHorizontalSpace = true;
		textGD.grabExcessVerticalSpace = true;
		tableListField.setLayoutData(textGD);
		return tableListField;
	}

	@Override
	public void createControls(IControlFactory controlFactory, Composite parent) {
		Composite mainComposite = controlFactory.createComposite(parent, SWT.NONE);
		mainComposite.setLayout(new GridLayout(2, false));
		//		mainComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		createTableGroup(controlFactory, mainComposite);
		createIDocGroup(controlFactory, mainComposite);

		update();
	}

	private void createIDocGroup(IControlFactory controlFactory, Composite mainComposite) {
		Composite idocGroup = controlFactory.createGroup(mainComposite, Messages.SAPPackImportOptionsPage_3, Section.TWISTIE | Section.COMPACT);

		// Create the group, the one that contains the radio buttons
		Composite fieldOptionsGroup = controlFactory.createGroup(idocGroup, Messages.IDocTypeImportOptionsBaseWizardPage_0, SWT.NONE);

		// Create the grid layout inside the options group, only 1 column
		fieldOptionsGroup.setLayout(new GridLayout(1, false));
		fieldOptionsGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		this.idoc_cbSegmentFieldsNullable = controlFactory.createButton(fieldOptionsGroup, SWT.CHECK);
		this.idoc_cbSegmentFieldsNullable.setText(Messages.IDocTypeImportOptionsBaseWizardPage_1);
		this.configureCheckboxForProperty(idoc_cbSegmentFieldsNullable, SETTING_IDOC_NULLABLE_FIELDS_OPTION);
		WidgetIDUtils.assignID(idoc_cbSegmentFieldsNullable, WidgetIDConstants.MOD_SEGMENTFIELDSNULLABLEOPTION);

		this.idoc_cbUseAllTypesOption = controlFactory.createButton(fieldOptionsGroup, SWT.RADIO);
		this.idoc_cbUseAllTypesOption.setText(Messages.IDocTypeImportOptionsBaseWizardPage_2);
		this.idoc_cbUseAllTypesOption.setSelection(true);
		WidgetIDUtils.assignID(idoc_cbUseAllTypesOption, WidgetIDConstants.MOD_USEALLTYPESOPTION);

		this.idoc_cbUseVarcharTypeOption = controlFactory.createButton(fieldOptionsGroup, SWT.RADIO);
		this.idoc_cbUseVarcharTypeOption.setText(Messages.IDocTypeImportOptionsBaseWizardPage_3);
		this.idoc_cbUseVarcharTypeOption.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				update();
			}
		});
		this.configureRadioButtonsForProperty(new Button[] { this.idoc_cbUseAllTypesOption, this.idoc_cbUseVarcharTypeOption }, SETTING_IDOC_FIELD_OPTION_ALLOW_ALL_TYPES);

		idoc_dataTypeOptionsComposite = controlFactory.createComposite(fieldOptionsGroup, SWT.NULL);
		idoc_dataTypeOptionsComposite.setLayout(new GridLayout(2, true));
		idoc_dataTypeOptionsComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label lengthFactorLabel = controlFactory.createLabel(idoc_dataTypeOptionsComposite, SWT.NONE);
		lengthFactorLabel.setText(Messages.IDocTypeImportOptionsBaseWizardPage_4);
		this.idoc_comboLengthFactor = controlFactory.createCombo(idoc_dataTypeOptionsComposite, SWT.SINGLE | SWT.READ_ONLY | SWT.BORDER);
		String lengthFactorTemp[] = new String[MAX_LEN_FACTOR];
		for (int idx = 0; idx < MAX_LEN_FACTOR; idx++) {
			lengthFactorTemp[idx] = Integer.toString(idx + 1);
		}
		this.idoc_comboLengthFactor.setItems(lengthFactorTemp);
		this.idoc_comboLengthFactor.select(0);
		this.configureComboForPropertyStr(idoc_comboLengthFactor, SETTING_IDOC_FIELDS_OPTION_LENGTH_FACTOR);

		// set the field order for TAB key
		fieldOptionsGroup.setTabList(new Control[] { this.idoc_cbSegmentFieldsNullable, this.idoc_cbUseAllTypesOption, this.idoc_cbUseVarcharTypeOption });

		Composite checkAndTextTablesGroup = controlFactory.createGroup(idocGroup, Messages.SAPPackImportOptionsPage_4, SWT.NONE);
		this.idocs_checkboxCheckTables = controlFactory.createButton(checkAndTextTablesGroup, SWT.CHECK);
		this.idocs_checkboxCheckTables.setText(Messages.ImportOptionsWizardPage_6);
		this.configureCheckboxForProperty(this.idocs_checkboxCheckTables, SETTING_IDOC_CHECK_TABLE_OPTION);
		this.idocs_checkboxCheckTables.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				update();
			}
		});

		this.idocs_checkboxTextTables = controlFactory.createButton(checkAndTextTablesGroup, SWT.CHECK);
		this.idocs_checkboxTextTables.setText(Messages.ImportOptionsWizardPage_7);
		this.idocs_checkboxTextTables.setEnabled(false);
		this.configureCheckboxForProperty(idocs_checkboxTextTables, SETTING_IDOC_TEXT_TABLE_OPTION);

		this.idocs_checkTableBlackList = createTableListField(controlFactory, checkAndTextTablesGroup, Messages.SAPPackImportOptionsPage_5);
		this.configureTextForProperty(idocs_checkTableBlackList, SETTING_IDOC_CHECK_TABLE_BLACKLIST);
	}

	private void createTableGroup(IControlFactory controlFactory, Composite mainComposite) {
		Composite tableGroup = controlFactory.createGroup(mainComposite, Messages.SAPPackImportOptionsPage_6, Section.TWISTIE | Section.COMPACT);

		Composite checkAndTextTablesGroup = controlFactory.createGroup(tableGroup, Messages.ImportOptionsWizardPage_5, SWT.NONE);
		checkAndTextTablesGroup.setLayout(new GridLayout(1, false));
		checkAndTextTablesGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		this.tables_checkboxCheckTables = controlFactory.createButton(checkAndTextTablesGroup, SWT.CHECK);
		this.tables_checkboxCheckTables.setText(Messages.ImportOptionsWizardPage_6);
		this.tables_checkboxCheckTables.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				update();
			}

		});
		this.configureCheckboxForProperty(tables_checkboxCheckTables, SETTING_TABLES_CHECK_TABLE_OPTION);
		WidgetIDUtils.assignID(tables_checkboxCheckTables, WidgetIDConstants.MOD_CHECKBOXCHECKTABLES);

		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalIndent = 10;

		/* recursion depth */
		this.tables_recursiveSearch = controlFactory.createButton(checkAndTextTablesGroup, SWT.CHECK);
		this.tables_recursiveSearch.setText(Messages.ImportOptionsWizardPage_12);
		this.tables_recursiveSearch.setLayoutData(gridData);
		this.configureCheckboxForProperty(tables_recursiveSearch, SETTING_TABLES_CHECK_TABLE_RECURSIVE);
		WidgetIDUtils.assignID(tables_recursiveSearch, WidgetIDConstants.MOD_RECURSIVESEARCH);

		/* text tables */
		this.tables_checkboxTextTables = controlFactory.createButton(checkAndTextTablesGroup, SWT.CHECK);
		this.tables_checkboxTextTables.setLayoutData(gridData);
		this.tables_checkboxTextTables.setText(Messages.ImportOptionsWizardPage_7);
		this.tables_checkboxTextTables.setEnabled(false);
		this.configureCheckboxForProperty(tables_checkboxTextTables, SETTING_TABLES_TEXT_TABLE_OPTION);
		WidgetIDUtils.assignID(tables_checkboxTextTables, WidgetIDConstants.MOD_CHECKBOXTEXTTABLES);

		this.tables_checkTableBlackList = createTableListField(controlFactory, checkAndTextTablesGroup, Messages.SAPPackImportOptionsPage_7);
		this.configureTextForProperty(tables_checkTableBlackList, SETTING_TABLES_CHECK_TABLE_BLACKLIST);

		Composite relationsGroup = controlFactory.createGroup(tableGroup, Messages.SAPPackImportOptionsPage_8, SWT.NONE);
		/* extract relations */
		this.tables_extractRelations = controlFactory.createButton(relationsGroup, SWT.CHECK);
		this.tables_extractRelations.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.tables_extractRelations.setText(Messages.ImportOptionsWizardPage_16);
		this.tables_extractRelations.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				update();
			}
		});
		this.configureCheckboxForProperty(this.tables_extractRelations, SETTING_TABLES_EXTRACT_RELATIONS);
		WidgetIDUtils.assignID(tables_extractRelations, WidgetIDConstants.MOD_EXTRACT_RELATIONS);

		/* enforce relations */
		//      gridData = new GridData(GridData.FILL_HORIZONTAL);
		//      gridData.horizontalIndent = 10;
		this.tables_enforceRelations = controlFactory.createButton(relationsGroup, SWT.CHECK);
		this.tables_enforceRelations.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.tables_enforceRelations.setText(Messages.ImportOptionsWizardPage_17);
		this.tables_enforceRelations.setLayoutData(gridData);
		this.configureCheckboxForProperty(tables_enforceRelations, SETTING_TABLES_ENFORCE_RELATIONS);
	}

	void update() {
		if (!this.tables_checkboxCheckTables.getSelection()) {
			this.tables_checkboxTextTables.setSelection(false);
			this.tables_recursiveSearch.setSelection(false);
		}
		this.tables_checkboxTextTables.setEnabled(this.tables_checkboxCheckTables.getSelection());
		this.tables_recursiveSearch.setEnabled(this.tables_checkboxCheckTables.getSelection());
		tables_enforceRelations.setEnabled(tables_extractRelations.getSelection());
		
		if (!this.idocs_checkboxCheckTables.getSelection()) {
			this.idocs_checkboxTextTables.setSelection(false);
		}
		this.idocs_checkboxTextTables.setEnabled(this.idocs_checkboxCheckTables.getSelection());
		
		this.idoc_comboLengthFactor.setEnabled(this.idoc_cbUseVarcharTypeOption.getSelection());
	}

	public static TableImporterOptions getTableImporterOptions(ConfigurationBase map) {
		TableImporterOptions options = new TableImporterOptions();

		boolean searchCheckTables = map.getBoolean(SETTING_TABLES_CHECK_TABLE_OPTION);
		List<String> blackListedTables = Utils.getTableListFromTextField(map.get(SETTING_TABLES_CHECK_TABLE_BLACKLIST));
		boolean searchTextTables = map.getBoolean(SETTING_TABLES_TEXT_TABLE_OPTION);
		boolean recursiveSearch = map.getBoolean(SETTING_TABLES_CHECK_TABLE_RECURSIVE);
		options.setCreateTechnicalFields(map.getBoolean(TechnicalFieldsPage.SETTING_TECH_FIELD_OPTION));

		boolean extractRelations = map.getBoolean(SETTING_TABLES_EXTRACT_RELATIONS);
		if (extractRelations) {
			options.setRelationMode(RELATIONS.SAP_PKs_FKs);
		} else {
			options.setRelationMode(RELATIONS.SAP_PKs_NoFKs);
		}
		boolean enforceRelations = map.getBoolean(SETTING_TABLES_ENFORCE_RELATIONS);
		options.setEnforceForeignKeys(enforceRelations);

		options.setCheckTableOption(searchCheckTables ? CHECKTABLEOPTIONS.CHECKTABLES : CHECKTABLEOPTIONS.NO_CHECKTABLES);
		options.setTableBlackList(blackListedTables);
		options.setExtractTextTables(searchTextTables);
		options.setExtractRecursively(recursiveSearch);
		options.setAtomicDomainPkgName(com.ibm.is.sappack.gen.tools.sap.constants.Constants.DEFAULT_PKG_NAME_ATOMIC_DOMAINS);
		return options;
	}

	public static IDocImporterOptions getIDocImporterOptions(ConfigurationBase map) {
		IDocImporterOptions options = new IDocImporterOptions();

		options.setCreateIDocExtractModel(true);
		options.setCreateIDocLoadModel(true);

		options.setUseVarcharTypeOnly(map.getInt(SETTING_IDOC_FIELD_OPTION_ALLOW_ALL_TYPES) == 1);
		options.setSegmentFieldsNullable(map.getBoolean(SETTING_IDOC_NULLABLE_FIELDS_OPTION));
		options.setSegmentFieldsDefaultValue(null);
		options.setVarcharLengthFactor(map.getInt(SETTING_IDOC_FIELDS_OPTION_LENGTH_FACTOR));

		options.setChecktableBlackList(Utils.getTableListFromTextField(map.get(SETTING_IDOC_CHECK_TABLE_BLACKLIST)));

		boolean checkTables = map.getBoolean(SETTING_IDOC_CHECK_TABLE_OPTION);
		if (checkTables) {
			options.setCheckTableOption(CHECKTABLEOPTIONS.CHECKTABLES);
		} else {
			options.setCheckTableOption(CHECKTABLEOPTIONS.NO_CHECKTABLES);
		}

		options.setExtractTextTables(map.getBoolean(SETTING_IDOC_TEXT_TABLE_OPTION));

		options.setAllowTechnicalFieldsToBeNullable(false);

		options.setCreateCorrectMIHModelOption(false);
		options.setAtomicDomainPkgName(com.ibm.is.sappack.gen.tools.sap.constants.Constants.DEFAULT_PKG_NAME_ATOMIC_DOMAINS);

		return options;
	}
}
