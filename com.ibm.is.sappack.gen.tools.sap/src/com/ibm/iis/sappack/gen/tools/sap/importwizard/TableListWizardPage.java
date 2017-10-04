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
// Module Name : com.ibm.iis.sappack.gen.tools.sap.importwizard
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.iis.sappack.gen.tools.sap.importwizard;


import java.io.File;
import java.text.MessageFormat;
import java.util.logging.Level;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.PlatformUI;

import com.ibm.iis.sappack.gen.common.ui.util.Utils;
import com.ibm.iis.sappack.gen.common.ui.wizards.PersistentWizardPageBase;
import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.common.ui.WidgetIDConstants;
import com.ibm.is.sappack.gen.common.ui.WidgetIDUtils;
import com.ibm.is.sappack.gen.tools.sap.activator.Activator;
import com.ibm.is.sappack.gen.tools.sap.constants.Constants;
import com.ibm.is.sappack.gen.tools.sap.model.SapTable;
import com.ibm.is.sappack.gen.tools.sap.model.SapTableSet;
import com.ibm.is.sappack.gen.tools.sap.utilities.XmlUtility;


public class TableListWizardPage extends PersistentWizardPageBase {
	
	public static final int COLUMN_NAME = 0;
	public static final int COLUMN_DESCRIPTION = COLUMN_NAME + 1;
	public static final int COLUMN_CHECK_TABLES = COLUMN_DESCRIPTION + 1;
	public static final int COLUMN_TEXT_TABLES = COLUMN_CHECK_TABLES + 1;

	private static final String COLUMN_NAME_TEXT_TABLES = Messages.TableListWizardPage_0;
	private static final String COLUMN_NAME_CHECK_TABLES = Messages.TableListWizardPage_1;
	private static final String COLUMN_NAME_DESCRIPTION = Messages.TableListWizardPage_2;
	private static final String COLUMN_NAME_NAME = Messages.TableListWizardPage_3;

	private TableViewer tableViewer;
	private Button showCheckTablesButton;


	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	
	
	public TableListWizardPage() {
		super(Messages.TableListWizardPage_5, Messages.TableListWizardPage_6, null);
		setDescription(Messages.TableListWizardPage_7);
	}

	@Override
	public Composite createControlImpl(final Composite parent) {

		Composite mainComposite = new Composite(parent, SWT.NULL);

		// create a layout for this wizard page
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		mainComposite.setLayout(gridLayout);

		this.tableViewer = new TableViewer(mainComposite, SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION);
		// this.tableViewer.setSorter(new ViewerSorter());
		GridData gridDataList = new GridData(GridData.FILL_BOTH);
		this.tableViewer.getTable().setLayoutData(gridDataList);
		gridDataList.widthHint = 140;

		// this.tableBrowsedTables.setContentProvider(new
		// SapTableListContentProvider());
		// this.tableBrowsedTables.setLabelProvider(new
		// SapTableListLabelProvider());
		/*
		CellEditor cellEditor = new CheckboxCellEditor(this.tableViewer.getTable());
		this.tableViewer.setCellEditors(new CellEditor[] { cellEditor });
		this.tableViewer.getTable().addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent keyEvt) {
				if (keyEvt.character == ' ') {
					Table curTable = (Table) keyEvt.getSource();
					TableItem[] selectedSAPTablesArr = curTable.getSelection();

					// toggle selected row (check or text tables only !!!)
					if (selectedSAPTablesArr.length > 0) {
						SapTable sapTblElement = (SapTable) selectedSAPTablesArr[0].getData();

						if (sapTblElement.isCheckTable() || sapTblElement.isTextTable()) {
							sapTblElement.setSelected(!sapTblElement.getSelected());

							// and update GUI
							tableViewer.refresh(true);
						}
					} // end of if (selectedSAPTablesArr.length > 0)
				} // end of if (keyEvt.character == ' ')
			} // end of keyPressed()

		});

		
		TableColumn tableColumnCheck = new TableColumn(this.tableViewer.getTable(), SWT.LEFT);
		tableColumnCheck.setText(COLUMN_NAME_EMPTY);
		tableColumnCheck.setWidth(20);
		*/

		TableColumn tableColumnName = new TableColumn(this.tableViewer.getTable(), SWT.LEFT);
		tableColumnName.setText(COLUMN_NAME_NAME);
		tableColumnName.setWidth(50);

		TableColumn tableColumnDescription = new TableColumn(this.tableViewer.getTable(), SWT.LEFT);
		tableColumnDescription.setText(COLUMN_NAME_DESCRIPTION);
		tableColumnDescription.setWidth(200);

		TableColumn tableColumnCheckTables = new TableColumn(this.tableViewer.getTable(), SWT.LEFT);
		tableColumnCheckTables.setText(COLUMN_NAME_CHECK_TABLES);
		tableColumnCheckTables.setWidth(80);

		TableColumn tableColumnTextTables = new TableColumn(this.tableViewer.getTable(), SWT.LEFT);
		tableColumnTextTables.setText(COLUMN_NAME_TEXT_TABLES);
		tableColumnTextTables.setWidth(60);

		final Table table = this.tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		WidgetIDUtils.assignID(tableViewer.getTable(), WidgetIDConstants.MOD_TABLELISTTABLE);

		SapProcessedTableListProvider provider = new SapProcessedTableListProvider(this.tableViewer);
		//		this.tableViewer.setColumnProperties(new String[] { COLUMN_NAME_EMPTY, COLUMN_NAME_NAME, COLUMN_NAME_DESCRIPTION, COLUMN_NAME_CHECK_TABLES, COLUMN_NAME_TEXT_TABLES });
		this.tableViewer.setColumnProperties(new String[] { COLUMN_NAME_NAME, COLUMN_NAME_DESCRIPTION, COLUMN_NAME_CHECK_TABLES, COLUMN_NAME_TEXT_TABLES });
		this.tableViewer.setContentProvider(provider);
		this.tableViewer.setLabelProvider(provider);
		this.tableViewer.setCellModifier(provider);

		Composite buttonComp = mainComposite;

		showCheckTablesButton = new Button(buttonComp, SWT.NONE);
		GridData addInfoGD = new GridData();
		addInfoGD.horizontalAlignment = SWT.LEFT;
		showCheckTablesButton.setLayoutData(addInfoGD);
		showCheckTablesButton.setText(Messages.TableListWizardPage_12);
		showCheckTablesButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TableItem[] selection = tableViewer.getTable().getSelection();
				if (selection != null && selection.length == 1) {
					Object o = selection[0].getData();
					if (o instanceof SapTable) {
						String ctColumn = selection[0].getText(SapTable.COLUMN_CHECK_TABLES);
						String ttColumn = selection[0].getText(SapTable.COLUMN_TEXT_TABLES);
						SapTable sapTable = (SapTable) o;
						AdditionalInfoDialog d = new AdditionalInfoDialog(sapTable, ctColumn, ttColumn, getShell());
						d.open();
					}
				}
			}
		});

		showCheckTablesButton.setEnabled(false);

		this.tableViewer.getTable().addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				TableItem[] selection = tableViewer.getTable().getSelection();
				boolean enableButton = (selection != null && selection.length == 1);
				showCheckTablesButton.setEnabled(enableButton);
			}

		});

		/*
		Button saveSelectedTablesButton = new Button(buttonComp, SWT.NONE);
		GridData saveSelecctionButtonGD = new GridData();
		saveSelecctionButtonGD.horizontalAlignment = SWT.RIGHT;
		saveSelectedTablesButton.setLayoutData(saveSelecctionButtonGD);
		saveSelectedTablesButton.setText(Messages.TableListWizardPage_8);
		saveSelectedTablesButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				saveSelectedTables();
			}
		});
		WidgetIDUtils.assignID(saveSelectedTablesButton, WidgetIDConstants.MOD_SAVESELECTEDTABLESBUTTON);
		GridData gridDataButton = new GridData(GridData.HORIZONTAL_ALIGN_END);

		saveSelectedTablesButton.setLayoutData(gridDataButton);
		*/
		//		restoreWidgetValues();

		return mainComposite;
	}

	public void setTables(SapTableSet tables) {
		tableViewer.setInput(tables);
		TableItem[] selection = tableViewer.getTable().getSelection();
		boolean enableButton = (selection != null && selection.length == 1);
		showCheckTablesButton.setEnabled(enableButton);
	}

	void saveSelectedTables() {
		try {

			FileDialog fileDialog = new FileDialog(getShell(), SWT.SAVE);
			fileDialog.setText(Messages.TableListWizardPage_9);
			String[] filterExtenstions = { Constants.CUSTOM_BUSINESS_OBJECT_FILE_EXTENSION };
			fileDialog.setFilterExtensions(filterExtenstions);
			String selectedFileName = fileDialog.open();
			if (selectedFileName == null) {
				return;
			}

			File file = new File(selectedFileName);
			if (file.exists()) {
				boolean overwrite = MessageDialog.openQuestion(getShell(), Messages.TableListWizardPage_10, MessageFormat.format(Messages.TableListWizardPage_11, selectedFileName));
				if (!overwrite) {
					return;
				}
			}

			XmlUtility xmlSaver = new XmlUtility(selectedFileName);
			xmlSaver.saveTableSet(this.getSelectedTables());
		} catch (Exception e) {
			e.printStackTrace();
			Activator.getLogger().log(Level.WARNING, Messages.LogExceptionMessage, e);
		}
	}

	/*	public void setTableList(SapTableSet tableSet) {
		
			this.tableViewer.setInput(tableSet);
		}
	*/

	public static boolean askForImport(WizardPage p, int numberOfEntities) {
		String msg = MessageFormat.format(Messages.LongTimeImportWarning, numberOfEntities);
		if (numberOfEntities > 1000) {
			boolean answer = MessageDialog.openQuestion(p.getShell(), Messages.LongTimeImportWarningTitle, msg);
			return answer;
		}
		return true;
	}

	@Override
	public boolean nextPressedImpl() {
		SapTableSet sapTables = getSelectedTables();

		if (!askForImport(this, sapTables.size())) {
			return false;
		}

		//	this.getSharedObjectsMap().put(SharedWizardObjectsMap.TABLE_SELECTED_TABLES, sapTables);
		//	saveWidgetValues(sapTables);

		return true;
	}
	
	@Override
	public void performHelp() {
		PlatformUI.getWorkbench().getHelpSystem().displayHelp(Utils.getHelpID("rmwiz_tables_list")); //$NON-NLS-1$
	}
	

	private SapTableSet getSelectedTables() {
		// osuhre, 50216: only retrieve tables that are selected
		//   product path checks for the getSelected() flag anyway but CW path does not, that shouldn't matter
		//   as long as the correct set is returned from here.
		SapTableSet result = new SapTableSet();
		for (SapTable t : (SapTableSet) this.tableViewer.getInput()) {
			if (t.getSelected()) {
				result.add(t);
			}
		}
		return result;
	}

	/*
	private void restoreWidgetValues() {
		
		IDialogSettings settings = this.getDialogSettings();

		if (settings != null) {
			String sapTablesBuf = settings.get(SELECTED_TABLE_LIST);

			if (sapTablesBuf != null && this.replayMode) {
				StringTokenizer strTokenizer = new StringTokenizer(sapTablesBuf, TABLE_NAME_SEPARATOR);

				this.selectedTableSet = new HashSet<String>();
				while (strTokenizer.hasMoreElements()) {
					this.selectedTableSet.add(strTokenizer.nextToken());
				}
			}
		}
		
	}

	private void saveWidgetValues(SapTableSet sapTables) {
		IDialogSettings settings = this.getDialogSettings();

		if (settings != null) {
			StringBuffer sapTablesBuf = new StringBuffer();

			Iterator<SapTable> tblSetIter = sapTables.iterator();
			while (tblSetIter.hasNext()) {
				sapTablesBuf.append(tblSetIter.next().getName());
				if (tblSetIter.hasNext()) {
					sapTablesBuf.append(TABLE_NAME_SEPARATOR);
				}
			}

			settings.put(SELECTED_TABLE_LIST, sapTablesBuf.toString());
		} // end of if (settings != null)
	}
	*/

} // end of class TableListWizardPage 

