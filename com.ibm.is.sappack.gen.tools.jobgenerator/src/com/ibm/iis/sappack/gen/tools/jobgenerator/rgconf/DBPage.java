//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2011, 2014                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.iis.sappack.gen.tools.jobgenerator.rgconf
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.iis.sappack.gen.tools.jobgenerator.rgconf;


import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
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

import com.ibm.iis.sappack.gen.common.ui.editors.IControlFactory;
import com.ibm.iis.sappack.gen.common.ui.util.ImageProvider;
import com.ibm.iis.sappack.gen.common.ui.util.Utils;
import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.common.ui.WidgetIDConstants;
import com.ibm.is.sappack.gen.common.ui.WidgetIDUtils;
import com.ibm.is.sappack.gen.common.util.StringUtils;
import com.ibm.is.sappack.gen.tools.sap.constants.Constants;
import com.ibm.is.sappack.gen.tools.sap.utilities.Utilities;


public class DBPage extends RGConfPageBase {
	
  	private static final String JOB_PARAM_HIDE_SEQUENCE = "$/$";
	public  static enum  DBType { SOURCE, TARGET };
	
	private Combo            persistenceTypesCombo;
	private Composite        stackComposite;
	private StackLayout      stackLayout;
	private Composite        odbcComposite;
	private Text             odbcDataSourceText;
	private Text             odbcUserText;
	private Text             odbcPasswordText;
	boolean                  sourceMode;
	private Text             odbcSqlWhereCondText;
	private Combo            tableActionsCombo;
	private Combo            odbcCodePageCombo;
	private Text             odbcCodePageName;
	private Button           odbcUseAlternateSchema;
	private Text             odbcAlternateSchemaName;
	private Button           odbcAutoCommitOption;
	private Text             odbcRecordCount;
	private Text             odbcArraySize;
	private Composite        fileComposite;
	private Text             fileText;
	private Combo            fileUpdateModeCombo;
	private Button           firstLineColumnNamesOption;
	private Combo            fileCodePageCombo;
	private DBPageProperties properties;
	private boolean          isCreatingControls;          

	public final static String PERSISTENCE_TYPE_ODBC = Messages.PersistenceComposite_0;
	public final static String PERSISTENCE_TYPE_FILE = Messages.PersistenceComposite_1;
	public final static String PERSISTENCE_TYPES_ARR[] = new String[] { PERSISTENCE_TYPE_ODBC, PERSISTENCE_TYPE_FILE };

	public final static String ODBC_WRITE_TABLE_ACTION_APPEND = Messages.PersistenceComposite_27;
	public final static String ODBC_WRITE_TABLE_ACTION_TRUNCATE = Messages.PersistenceComposite_28;
	public final static String ODBC_TABLE_ACTIONS_ARR[] = new String[] { ODBC_WRITE_TABLE_ACTION_APPEND, ODBC_WRITE_TABLE_ACTION_TRUNCATE };

	public static final String ODBC_CODE_PAGE_TYPE_DEFAULT = Messages.PersistenceComposite_17;
	public static final String ODBC_CODE_PAGE_TYPE_UNICODE = Messages.PersistenceComposite_18;
	public static final String ODBC_CODE_PAGE_TYPE_USER_DEFINED = Messages.PersistenceComposite_19;
	
	public final static String ODBC_CODE_PAGE_TYPES_ARR[] = new String[] { ODBC_CODE_PAGE_TYPE_DEFAULT , ODBC_CODE_PAGE_TYPE_UNICODE , ODBC_CODE_PAGE_TYPE_USER_DEFINED }; //  (User-Specified) must always be the last
	public enum ODBCCodePageType { Default, Unicode, UserSpecified	};

//	public enum FileCodePageType {
//		Default, ISO8859_1, UTF8, UTF16
//	};

	public static final String FILE_CODE_PAGE_PROJECT_DEFAULT = Messages.PersistenceComposite_22;
	public static final String FILE_CODE_PAGE_PROJECT_ISO8859_1 = Messages.PersistenceComposite_25;
	public static final String FILE_CODE_PAGE_PROJECT_UTF8 = Messages.PersistenceComposite_23;
	public static final String FILE_CODE_PAGE_PROJECT_UTF16 = Messages.PersistenceComposite_24;
	public final static String FILE_CODE_PAGE_TYPES_ARR[] = new String[] {
		//
		FILE_CODE_PAGE_PROJECT_DEFAULT,
		FILE_CODE_PAGE_PROJECT_ISO8859_1,
		FILE_CODE_PAGE_PROJECT_UTF8,
		FILE_CODE_PAGE_PROJECT_UTF16
		//
		};

	public final static String FILE_UPDATE_MODE_APPEND = Messages.PersistenceComposite_2;
	public final static String FILE_UPDATE_MODE_CREATE = Messages.PersistenceComposite_3;
	public final static String FILE_UPDATE_MODE_OVERWRITE = Messages.PersistenceComposite_4;
	public final static String FILE_UPDATE_MODE_ARR[] = new String[] { FILE_UPDATE_MODE_APPEND, FILE_UPDATE_MODE_CREATE, FILE_UPDATE_MODE_OVERWRITE };

	
	
  	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	

	public DBPage(String tabName, String title, String descriptionText, boolean sourceMode) {
		super(tabName, title, descriptionText,
		      sourceMode ? Utils.getHelpID("rgconfeditor_db_source") : Utils.getHelpID("rgconfeditor_db_target")); //$NON-NLS-1$ //$NON-NLS-2$
		this.sourceMode         = sourceMode;
		this.properties         = new DBPageProperties(sourceMode);
		this.isCreatingControls = false;
	}


	@Override
	public void createControls(IControlFactory controlFactory, Composite parent) {

		this.isCreatingControls = true;

		ModifyListener modifyListener = new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
			}
		};

		KeyListener keyListener = new KeyListener() {

			@Override
			public void keyReleased(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}
		};

		Composite comp = controlFactory.createGroup(parent, Messages.DBPage_0, SWT.NONE);

		GridLayout targetLayout = new GridLayout(1, false);
		targetLayout.marginHeight = 0; //!
		targetLayout.marginWidth = 0; //!
		comp.setLayout(targetLayout);
		comp.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));

		// persistence stage selection combo

		Composite persistenceSelectionComposite = controlFactory.createComposite(comp, SWT.NONE);
		GridLayout persistenceSelectionCompositeLayout = new GridLayout(2, false);
		persistenceSelectionCompositeLayout.marginWidth = 0;
		persistenceSelectionCompositeLayout.marginHeight = 0;
		persistenceSelectionComposite.setLayout(persistenceSelectionCompositeLayout);
		persistenceSelectionComposite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));

		this.stackLayout = new StackLayout();
		this.stackLayout.marginHeight = 0;
		this.stackLayout.marginWidth = 0;

		Label profileLabel = controlFactory.createLabel(persistenceSelectionComposite, SWT.NONE);
		profileLabel.setText(Messages.PersistenceComposite_5);
		this.persistenceTypesCombo = controlFactory.createCombo(persistenceSelectionComposite, SWT.SINGLE | SWT.READ_ONLY | SWT.BORDER);
		this.persistenceTypesCombo.setItems(PERSISTENCE_TYPES_ARR);
		this.persistenceTypesCombo.select(0);
		WidgetIDUtils.assignID(this.persistenceTypesCombo, WidgetIDConstants.GEN_PERSISTENCETYPESCOMBO);

		// persistence stage stack composite
		this.stackComposite = controlFactory.createComposite(comp, SWT.NULL);
		this.stackComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		this.stackComposite.setLayout(stackLayout);

		// -------------
		// ODBC Settings
		// -------------
		this.odbcComposite = controlFactory.createComposite(stackComposite, SWT.NULL);
		GridLayout odbcCompositeLayout = new GridLayout(2, false);
		odbcCompositeLayout.marginHeight = 0;
		odbcCompositeLayout.marginWidth = 0;
		this.odbcComposite.setLayout(odbcCompositeLayout);
		this.odbcComposite.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL | GridData.FILL_BOTH));

		Label odbcDataSourceLabel = controlFactory.createLabel(this.odbcComposite, SWT.NONE);
		odbcDataSourceLabel.setText(Messages.PersistenceComposite_6);
		this.odbcDataSourceText = controlFactory.createText(this.odbcComposite, SWT.BORDER);
		this.odbcDataSourceText.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
		this.odbcDataSourceText.addModifyListener(modifyListener);
		this.odbcDataSourceText.addKeyListener(keyListener);
		this.configureTextForJobParameterProperty(this.odbcDataSourceText, properties.VALUE_SUBKEY_ODBC_DATASOURCE);
		WidgetIDUtils.assignID(this.odbcDataSourceText, WidgetIDConstants.GEN_ODBCDATASOURCETEXT);

		Label odbcUserLabel = controlFactory.createLabel(this.odbcComposite, SWT.NONE);
		odbcUserLabel.setText(Messages.PersistenceComposite_7);
		this.odbcUserText = controlFactory.createText(this.odbcComposite, SWT.BORDER);
		this.odbcUserText.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
		this.odbcUserText.addModifyListener(modifyListener);
		this.odbcUserText.addKeyListener(keyListener);
		this.configureTextForJobParameterProperty(this.odbcUserText, properties.VALUE_SUBKEY_ODBC_USER);
		WidgetIDUtils.assignID(this.odbcUserText, WidgetIDConstants.GEN_ODBCUSERTEXT);

		Label odbcPasswordLabel = controlFactory.createLabel(this.odbcComposite, SWT.NONE);
		odbcPasswordLabel.setText(Messages.PersistenceComposite_8);
		this.odbcPasswordText = controlFactory.createText(this.odbcComposite, SWT.BORDER);
		this.odbcPasswordText.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
		this.odbcPasswordText.addModifyListener(modifyListener);
		this.odbcPasswordText.addKeyListener(keyListener);
		this.odbcPasswordText.addModifyListener(new ChangeEchoCharWhenJobParam());
		this.configureTextForJobParameterProperty(this.odbcPasswordText, properties.VALUE_SUBKEY_ODBC_PASSWORD);
		WidgetIDUtils.assignID(this.odbcPasswordText, WidgetIDConstants.GEN_ODBCPASSWORDTEXT);

		if (sourceMode) {
			Label dsOdbcSqlWhereCondLabel = controlFactory.createLabel(this.odbcComposite, SWT.NONE);
			dsOdbcSqlWhereCondLabel.setText(Messages.PersistenceComposite_9);
			this.odbcSqlWhereCondText = controlFactory.createText(this.odbcComposite, SWT.BORDER);
			this.odbcSqlWhereCondText.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
			this.odbcSqlWhereCondText.addModifyListener(modifyListener);
			this.configureTextForJobParameterProperty(this.odbcSqlWhereCondText, properties.VALUE_SUBKEY_ODBC_SQL_COND);
			WidgetIDUtils.assignID(this.odbcSqlWhereCondText, WidgetIDConstants.GEN_ODBCSQLWHERECONDTEXT);
		} else {
			Label tableActionLabel = controlFactory.createLabel(this.odbcComposite, SWT.NONE);
			tableActionLabel.setText(Messages.PersistenceComposite_10);
			this.tableActionsCombo = controlFactory.createCombo(this.odbcComposite, SWT.SINGLE | SWT.READ_ONLY | SWT.BORDER);
			this.tableActionsCombo.setItems(ODBC_TABLE_ACTIONS_ARR);
			this.tableActionsCombo.select(0);
			this.configureComboForProperty(this.tableActionsCombo, properties.VALUE_SUBKEY_ODBC_TABLE_ACTION);
			WidgetIDUtils.assignID(this.tableActionsCombo, WidgetIDConstants.GEN_TABLEACTIONSCOMBO);
		}

		// ODBC Codepage settings
		Label codePageLabel = controlFactory.createLabel(this.odbcComposite, SWT.NONE);
		codePageLabel.setText(Messages.PersistenceComposite_16);
		Composite topComposite = controlFactory.createComposite(this.odbcComposite, SWT.NONE);
		GridLayout tmpGridLayout = new GridLayout(3, false);
		tmpGridLayout.marginLeft = 0 - tmpGridLayout.horizontalSpacing; // move control to the most-left position
		topComposite.setLayout(tmpGridLayout);
		topComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		this.odbcCodePageCombo = controlFactory.createCombo(topComposite, SWT.SINGLE | SWT.READ_ONLY | SWT.BORDER);
		this.odbcCodePageCombo.setItems(ODBC_CODE_PAGE_TYPES_ARR);
		this.odbcCodePageCombo.select(0);
		this.odbcCodePageCombo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				int selectionIdx = ((Combo) e.getSource()).getSelectionIndex();

				ODBCCodePageType selectionType = ODBCCodePageType.values()[selectionIdx];
				switch (selectionType) {
				case UserSpecified:
					setODBCCodePageName(true);
					break;

				default:
					setODBCCodePageName(false);
				}

			} // end of widgetSelected()
		}); // end of new SelectionAdapter()
		this.configureComboForProperty(odbcCodePageCombo, properties.VALUE_SUBKEY_ODBC_CODE_PAGE_SEL);
		
		codePageLabel = controlFactory.createLabel(topComposite, SWT.NONE);
		codePageLabel.setText(Messages.PersistenceComposite_20);
		this.odbcCodePageName = controlFactory.createText(topComposite, SWT.BORDER);
		this.odbcCodePageName.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
		this.odbcCodePageName.addModifyListener(modifyListener);
		this.odbcCodePageName.addKeyListener(keyListener);
		Boolean isUserSpecCP = ODBCCodePageType.values()[this.odbcCodePageCombo.getSelectionIndex()] == ODBCCodePageType.UserSpecified;
 		setODBCCodePageName(isUserSpecCP);
		this.configureTextForProperty(odbcCodePageName, properties.VALUE_SUBKEY_ODBC_CODE_PAGE_NAME);

		// Checkbox: 'Auto-commit mode'
		this.odbcAutoCommitOption = controlFactory.createButton(this.odbcComposite, SWT.CHECK);
		this.odbcAutoCommitOption.setText(Messages.PersistenceComposite_33);
		this.configureCheckboxForProperty(this.odbcAutoCommitOption, properties.VALUE_SUBKEY_ODBC_AUTOCOMMIT_MODE);
		Label dummyLabel = controlFactory.createLabel(this.odbcComposite, SWT.NONE);
		dummyLabel.setText(Constants.EMPTY_STRING);
		
		GridData numFieldGridData = new GridData(GridData.GRAB_HORIZONTAL | GridData.END);
		numFieldGridData.minimumWidth = 200;

		// 'Array size'
		Label arraySizeLabel = controlFactory.createLabel(this.odbcComposite, SWT.NONE);
		arraySizeLabel.setText(Messages.PersistenceComposite_34);
		this.odbcArraySize = controlFactory.createText(this.odbcComposite, SWT.BORDER | SWT.RIGHT);
		// this.odbcArraySize.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
		this.odbcArraySize.setLayoutData(numFieldGridData);
		this.odbcArraySize.addModifyListener(modifyListener);
		this.odbcArraySize.addVerifyListener(new Utilities.ForceNumericInput());
		this.configureTextForJobParameterProperty(this.odbcArraySize, properties.VALUE_SUBKEY_ODBC_ARRAY_SIZE);

		// 'Record count'
		Label recordCountLabel = controlFactory.createLabel(this.odbcComposite, SWT.NONE);
		recordCountLabel.setText(Messages.PersistenceComposite_35);
		this.odbcRecordCount = controlFactory.createText(this.odbcComposite, SWT.BORDER | SWT.RIGHT);
		// this.odbcRecordCount.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
		this.odbcRecordCount.setLayoutData(numFieldGridData);
		this.odbcRecordCount.addModifyListener(modifyListener);
		this.odbcRecordCount.addVerifyListener(new Utilities.ForceNumericInput());
		this.configureTextForJobParameterProperty(this.odbcRecordCount, properties.VALUE_SUBKEY_ODBC_RECORD_COUNT);
		
		// Checkbox: Alternative schema name 
		this.odbcUseAlternateSchema = controlFactory.createButton(this.odbcComposite, SWT.CHECK);
		this.odbcUseAlternateSchema.setText(Messages.PersistenceComposite_30);
		this.odbcUseAlternateSchema.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setODBCSchemaEnabled(DBPage.this.odbcUseAlternateSchema.getSelection());
			}
		});
		this.configureCheckboxForProperty(odbcUseAlternateSchema, properties.VALUE_SUBKEY_ODBC_USE_ALT_SCHEMA);
		
		dummyLabel = controlFactory.createLabel(this.odbcComposite, SWT.NONE);
		dummyLabel.setText(Constants.EMPTY_STRING);

		// 'Alternative schema name'
		Label schemaLabel = controlFactory.createLabel(this.odbcComposite, SWT.NONE);
		schemaLabel.setText(Messages.PersistenceComposite_32);
		this.odbcAlternateSchemaName = controlFactory.createText(this.odbcComposite, SWT.BORDER);
		this.odbcAlternateSchemaName.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
		this.odbcAlternateSchemaName.addModifyListener(modifyListener);
		setODBCSchemaEnabled(this.odbcUseAlternateSchema.getSelection());
		this.configureTextForJobParameterProperty(this.odbcAlternateSchemaName, properties.VALUE_SUBKEY_ODBC_ALT_SCHEMA_NAME);

		// -------------
		// FILE settings
		// -------------
		this.fileComposite = controlFactory.createComposite(stackComposite, SWT.NULL);
		GridLayout fileCompositeLayout = new GridLayout(2, false);
		fileCompositeLayout.marginWidth = 0;
		fileCompositeLayout.marginHeight = 0;
		this.fileComposite.setLayout(fileCompositeLayout);
		this.fileComposite.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL | GridData.FILL_BOTH));

		Label fileLabel = controlFactory.createLabel(this.fileComposite, SWT.NONE);
		fileLabel.setText(Messages.PersistenceComposite_11);
		this.fileText = controlFactory.createText(this.fileComposite, SWT.BORDER);
		this.fileText.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
		this.fileText.addModifyListener(modifyListener);
		this.configureTextForJobParameterProperty(this.fileText, properties.VALUE_SUBKEY_FILE_NAME);
		WidgetIDUtils.assignID(this.fileText, WidgetIDConstants.GEN_FILETEXT);

		if (!sourceMode) {
			Label fileUpdateModeLabel = controlFactory.createLabel(this.fileComposite, SWT.NONE);
			fileUpdateModeLabel.setText(Messages.PersistenceComposite_12);
			this.fileUpdateModeCombo = controlFactory.createCombo(this.fileComposite, SWT.SINGLE | SWT.READ_ONLY | SWT.BORDER);
			this.fileUpdateModeCombo.setItems(FILE_UPDATE_MODE_ARR);
			this.fileUpdateModeCombo.select(0);
			this.configureComboForProperty(this.fileUpdateModeCombo, properties.VALUE_SUBKEY_FILE_UPDATE_MODE);
			WidgetIDUtils.assignID(this.fileUpdateModeCombo, WidgetIDConstants.GEN_FILEUPDATEMODECOMBO);
		}

		Label firstLineColumnNamesLabel = controlFactory.createLabel(this.fileComposite, SWT.NONE);
		firstLineColumnNamesLabel.setText(Messages.PersistenceComposite_13);
		this.firstLineColumnNamesOption = controlFactory.createButton(this.fileComposite, SWT.CHECK);
		this.configureCheckboxForProperty(this.firstLineColumnNamesOption, properties.VALUE_SUBKEY_FILE_FIRST_LINE_COL);

		// File Codepage settings
		codePageLabel = controlFactory.createLabel(this.fileComposite, SWT.NONE);
		codePageLabel.setText(Messages.PersistenceComposite_21);

		this.fileCodePageCombo = controlFactory.createCombo(this.fileComposite, SWT.SINGLE | SWT.READ_ONLY | SWT.BORDER);
		this.fileCodePageCombo.setItems(FILE_CODE_PAGE_TYPES_ARR);
		this.fileCodePageCombo.select(0);
		this.configureComboForProperty(this.fileCodePageCombo, properties.VALUE_SUBKEY_FILE_CODE_PAGE_SEL);

		this.stackLayout.topControl = this.odbcComposite;

		persistenceTypesCombo.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (DBPage.this.persistenceTypesCombo.getSelectionIndex() == 0) {          // ==> ODBC composite selected
					if (!DBPage.this.isCreatingControls) {     // prevent field update while 'creating controls'
						// shows the job parameters on ODBC composite and hides these on FILE composite
						// ==> this is to avoid active job Parameters on "Parameter Page" for non-visible FILE composite
						showODBCCompositeJobParameters(true);
						showFILECompositeJobParameters(false);
					}
					DBPage.this.stackLayout.topControl = DBPage.this.odbcComposite;
					
				} else if (DBPage.this.persistenceTypesCombo.getSelectionIndex() == 1) {   // ==> FILE composite selected
					if (!DBPage.this.isCreatingControls) {     // prevent field update while 'creating controls'
						// hides the job parameters on ODBC composite and shows these on FILE composite 
						// ==> this is to avoid active job Parameters on "Parameter Page" for non-visible ODBC composite
						showODBCCompositeJobParameters(false);
						showFILECompositeJobParameters(true);
					}
					DBPage.this.stackLayout.topControl = DBPage.this.fileComposite;
				}
				DBPage.this.stackComposite.layout();
			}
		});
		this.configureComboForProperty(this.persistenceTypesCombo, properties.VALUE_SUBKEY_TYPES_COMBO);

		this.isCreatingControls = false;
	}


	public static final String getProperty(DBType type, String prop) {
		switch (type) {
		case SOURCE:
				return "SOURCE_" + prop; //$NON-NLS-1$
		case TARGET:
			return "TARGET_" + prop; //$NON-NLS-1$
		default:
			 return prop;
		}
	}
	
	private void setODBCCodePageName(boolean enabled) {
		this.odbcCodePageName.setEnabled(enabled);

		if (enabled) {
			DBPage.this.odbcCodePageName.setBackground(null);
			DBPage.this.odbcCodePageName.forceFocus();
		}
		else {
			DBPage.this.odbcCodePageName.setBackground(Utils.COLOR_GRAY);
			DBPage.this.odbcCodePageName.setText(com.ibm.is.sappack.gen.common.Constants.EMPTY_STRING);
		}
	}

	private void setODBCSchemaEnabled(boolean enabled) {
		this.odbcAlternateSchemaName.setEnabled(enabled);
		if (enabled) {
			DBPage.this.odbcAlternateSchemaName.setBackground(null);
			DBPage.this.odbcAlternateSchemaName.forceFocus();
		}
		else {
			DBPage.this.odbcAlternateSchemaName.setText(com.ibm.is.sappack.gen.common.Constants.EMPTY_STRING);
			DBPage.this.odbcAlternateSchemaName.setBackground(Utils.COLOR_GRAY);
		}
	}

	/**
	 * This method shows or hides the job parameter values that are found in the entry 
	 * fields on the ODBC composite.
	 * <p>
	 * Note: When the ODBC composite is shown all available job parameters have to appear on the 
	 *       Job Parameter page, when the ODBC composite is hidden no job parameters must appear 
	 *       on the Job Parameter page.  
	 *  
	 * @param show true = show or false = hide
	 */
	private void showODBCCompositeJobParameters(boolean show) {
		showJobParams(this.odbcAlternateSchemaName, show);
		showJobParams(this.odbcCodePageName, show);
		showJobParams(this.odbcDataSourceText, show);
		showJobParams(this.odbcPasswordText, show);
		showJobParams(this.odbcRecordCount, show);
		showJobParams(this.odbcArraySize, show);
		if (this.odbcSqlWhereCondText != null) {
			showJobParams(this.odbcSqlWhereCondText, show);
		}
		showJobParams(this.odbcUserText, show);
	}

	/**
	 * This method shows or hides the job parameter values that are found in the entry 
	 * fields on the FILE composite.
	 * <p>
	 * Note: When the FILE composite is shown all available job parameters have to appear on the 
	 *       Job Parameter page, when the ODBC composite is hidden no job parameters must appear 
	 *       on the Job Parameter page.  
	 *  
	 * @param show true = show or false = hide
	 */
	private void showFILECompositeJobParameters(boolean show) {
		showJobParams(this.fileText, show);
	}

	/**
	 * This method shows or and hides the job parameters by replacing or un-replacing 
	 * the job parameter characters.<br>
	 * Dependent on the 'show flag' the job parameter character is replaced by a different character (sequence).
	 * <p>
	 * Note: This string modification triggers  an update of current job parameter list (using 
	 * the existing ModificationHandler).  
	*/
	private void showJobParams(Text textField, boolean show) {
		String fieldText;
		String replacedText;

		fieldText = textField.getText();
		if (fieldText != null && fieldText.length() > 0) {
			if (show) {
				replacedText = StringUtils.replaceString(fieldText, JOB_PARAM_HIDE_SEQUENCE, Constants.JOB_PARAM_SEPARATOR);
			}
			else {
				replacedText = StringUtils.replaceString(fieldText, Constants.JOB_PARAM_SEPARATOR, JOB_PARAM_HIDE_SEQUENCE);
			}
			textField.setText(replacedText);
		}
	}

	@Override
	public Image getImage() {
		return ImageProvider.getImage(ImageProvider.IMAGE_DB_ICON);
	}

}
