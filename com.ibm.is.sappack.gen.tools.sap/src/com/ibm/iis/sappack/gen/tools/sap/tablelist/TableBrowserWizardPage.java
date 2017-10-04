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
// Module Name : com.ibm.iis.sappack.gen.tools.sap.tablelist
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.iis.sappack.gen.tools.sap.tablelist;


import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import com.ibm.iis.sappack.gen.common.ui.connections.SapSystem;
import com.ibm.iis.sappack.gen.common.ui.wizards.SAPSystemSelectionWizardPage;
import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.common.ui.WidgetIDConstants;
import com.ibm.is.sappack.gen.common.ui.WidgetIDUtils;
import com.ibm.is.sappack.gen.tools.sap.activator.Activator;
import com.ibm.is.sappack.gen.tools.sap.constants.Constants;
import com.ibm.is.sappack.gen.tools.sap.importer.SapLogicalTableBrowser;
import com.ibm.is.sappack.gen.tools.sap.model.SapTable;
import com.ibm.is.sappack.gen.tools.sap.provider.SapTableListProvider;
import com.ibm.is.sappack.gen.tools.sap.utilities.ExceptionHandler;
import com.ibm.is.sappack.gen.tools.sap.utilities.Utilities;
import com.sap.conn.jco.JCoException;


public class TableBrowserWizardPage extends WizardPage {
	private TableViewer tableBrowsedTables;
	private TableViewer tableSelectedTables;

	private SapTableListProvider browsedTablesProvider;
	private SapTableListProvider selectedTablesProvider;

	private Text textTableSearchTerm;
	private Button buttonSearch;

	private Group logicalTablesGroup;
	private StackLayout stackLayout;
	private Composite stackComposite;

	// -----------------------------
	private SAPSystemSelectionWizardPage sapSystemSelectionPage;

	
  	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	

	
	public TableBrowserWizardPage(SAPSystemSelectionWizardPage sapSystemSelectionPage) {
		super(Messages.TableBrowserWizardPage_0, Messages.TableBrowserWizardPage_1, null);

		setDescription(Messages.TableBrowserWizardPage_2);
		this.sapSystemSelectionPage = sapSystemSelectionPage;
	}

	public void createControl(final Composite parent) {

		Composite mainComposite = new Composite(parent, SWT.NULL);

		// create a layout for this wizard page
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		gridLayout.makeColumnsEqualWidth = false;
		mainComposite.setLayout(gridLayout);

		createSearchField(mainComposite);

		this.stackComposite = new Composite(mainComposite, SWT.NULL);
		this.stackComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		this.stackLayout = new StackLayout();
		this.stackComposite.setLayout(this.stackLayout);

		this.logicalTablesGroup = createTables(this.stackComposite);
		this.stackLayout.topControl = this.logicalTablesGroup;
		// -----------------------------
		// createTables(container);

		//	restoreWidgetValues();

		/*
		// if we are in replay mode we make all children read only
		if (((IMetadataImportWizard) getWizard()).isReplayMode()) {
			makeAllChildrenReadOnly(container);
		}
		*/

		updateEnablement();
		dialogChanged();

		setControl(mainComposite);
	}

	private Group createTables(Composite parent) {
		Group group = new Group(parent, SWT.NULL);
		group.setText(Messages.TableBrowserWizardPage_7);

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		group.setLayout(gridLayout);
		group.setLayoutData(new GridData(GridData.FILL_BOTH));

		Label labelBrowsedTables = new Label(group, SWT.NONE);
		labelBrowsedTables.setText(Messages.TableBrowserWizardPage_8);

		Label labelSelectedTables = new Label(group, SWT.NONE);
		labelSelectedTables.setText(Messages.TableBrowserWizardPage_9);

		this.tableBrowsedTables = new TableViewer(group, SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.BORDER);
		this.tableBrowsedTables.setSorter(new ViewerSorter());
		this.tableBrowsedTables.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		WidgetIDUtils.assignID(tableBrowsedTables.getTable(), WidgetIDConstants.MOD_TABLEBROWSEDTABLES);

		this.browsedTablesProvider = new SapTableListProvider();
		this.tableBrowsedTables.setContentProvider(this.browsedTablesProvider);
		this.tableBrowsedTables.setLabelProvider(this.browsedTablesProvider);

		TableColumn browsedTableColumnName = new TableColumn(this.tableBrowsedTables.getTable(), SWT.LEFT);
		browsedTableColumnName.setText(Messages.TableBrowserWizardPage_22);

		TableColumn browsedTableColumnDescription = new TableColumn(this.tableBrowsedTables.getTable(), SWT.LEFT);
		browsedTableColumnDescription.setText(Messages.TableBrowserWizardPage_21);

		this.tableBrowsedTables.setInput(this.browsedTablesProvider.getTables());

		final Table browsedTable = this.tableBrowsedTables.getTable();
		// for (int i = 0; i < browsedTable.getColumnCount(); i++) {
		// browsedTable.getColumn(i).pack();
		// }
		browsedTable.setHeaderVisible(true);
		browsedTable.setLinesVisible(true);

		this.tableBrowsedTables.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				addTables();
			}
		});

		this.tableSelectedTables = new TableViewer(group, SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.BORDER);
		this.tableSelectedTables.setSorter(new ViewerSorter());
		this.tableSelectedTables.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		WidgetIDUtils.assignID(tableSelectedTables.getTable(), WidgetIDConstants.MOD_TABLESELECTEDTABLES);

		this.selectedTablesProvider = new SapTableListProvider();
		this.tableSelectedTables.setContentProvider(this.selectedTablesProvider);
		this.tableSelectedTables.setLabelProvider(this.selectedTablesProvider);

		TableColumn selectedTableColumnName = new TableColumn(this.tableSelectedTables.getTable(), SWT.LEFT);
		selectedTableColumnName.setText(Messages.TableBrowserWizardPage_22);

		TableColumn selectedTableColumnDescription = new TableColumn(this.tableSelectedTables.getTable(), SWT.LEFT);
		selectedTableColumnDescription.setText(Messages.TableBrowserWizardPage_21);

		this.tableSelectedTables.setInput(this.selectedTablesProvider.getTables());

		final Table selectedTable = this.tableSelectedTables.getTable();
		// for (int i = 0; i < selectedTable.getColumnCount(); i++) {
		// selectedTable.getColumn(i).pack();
		// }
		selectedTable.setHeaderVisible(true);
		selectedTable.setLinesVisible(true);

		this.tableSelectedTables.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				removeTables();
			}
		});

		Button buttonAddTables = new Button(group, SWT.PUSH);
		buttonAddTables.setText(Messages.TableBrowserWizardPage_10);
		buttonAddTables.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TableBrowserWizardPage.this.addTables();
			}

		});
		WidgetIDUtils.assignID(buttonAddTables, WidgetIDConstants.MOD_BUTTONADDTABLES);

		Button buttonRemoveTables = new Button(group, SWT.PUSH);
		buttonRemoveTables.setText(Messages.TableBrowserWizardPage_11);
		buttonRemoveTables.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TableBrowserWizardPage.this.removeTables();
			}

		});
		WidgetIDUtils.assignID(buttonRemoveTables, WidgetIDConstants.MOD_BUTTONREMOVETABLES);

		browsedTable.addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent e) {
				int width = browsedTable.getSize().x - (2 * selectedTable.getBorderWidth());
				if (browsedTable.getVerticalBar().isVisible()) {
					width -= browsedTable.getVerticalBar().getSize().x;
				}
				browsedTable.getColumn(0).setWidth(width / 2);
				browsedTable.getColumn(1).setWidth(width / 2);
			}
		});

		selectedTable.addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent e) {
				int width = selectedTable.getSize().x - (2 * selectedTable.getBorderWidth());
				if (selectedTable.getVerticalBar().isVisible()) {
					width -= selectedTable.getVerticalBar().getSize().x;
				}
				selectedTable.getColumn(0).setWidth(width / 2);
				selectedTable.getColumn(1).setWidth(width / 2);
			}
		});

		return group;
		// -----------------------------
	}

	private void createSearchField(Composite parent) {
		Group group = new Group(parent, SWT.NULL);
		group.setText(Messages.TableBrowserWizardPage_12);

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		gridLayout.makeColumnsEqualWidth = false;

		group.setLayout(gridLayout);
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		this.textTableSearchTerm = new Text(group, SWT.BORDER);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		this.textTableSearchTerm.setLayoutData(gridData);
		this.textTableSearchTerm.addControlListener(new ControlAdapter() {

		});
		this.textTableSearchTerm.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.CR) {
					TableBrowserWizardPage.this.buttonSearch.notifyListeners(SWT.Selection, new Event());
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				// Nothing to be done here
			}
		});

		this.textTableSearchTerm.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				updateEnablement();
			}
		});
		WidgetIDUtils.assignID(textTableSearchTerm, WidgetIDConstants.MOD_TEXTTABLESEARCHTERM);

		this.buttonSearch = new Button(group, SWT.PUSH);
		this.buttonSearch.setText(Messages.TableBrowserWizardPage_13);
		this.buttonSearch.forceFocus();
		this.buttonSearch.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {

				if (Utilities.containsOnlyWildcardChar(textTableSearchTerm.getText())) {
					MessageDialog.openInformation(getShell(), Messages.TitleInfo, Messages.BrowserWizardPage_1);
					textTableSearchTerm.setFocus();
				} else {
					Cursor originalCursor = TableBrowserWizardPage.this.getShell().getCursor();
					Cursor waitCursor = new Cursor(TableBrowserWizardPage.this.getShell().getDisplay(), SWT.CURSOR_WAIT);

					TableBrowserWizardPage.this.getShell().setCursor(waitCursor);
					TableBrowserWizardPage.this.buttonSearch.setEnabled(false);

					try {
						TableBrowserWizardPage.this.performBrowseTables();
					} catch (Exception exception) {
						exception.printStackTrace();
						Activator.getLogger().log(Level.WARNING, Messages.LogExceptionMessage, exception);
					} finally {
						TableBrowserWizardPage.this.buttonSearch.setEnabled(true);
						TableBrowserWizardPage.this.getShell().setCursor(originalCursor);
					}
				}

			}
		});
		WidgetIDUtils.assignID(buttonSearch, WidgetIDConstants.MOD_BUTTONSEARCH);
	}

	private String getSearchTerm() {
		String searchTerm = this.textTableSearchTerm.getText();
		searchTerm = searchTerm.toUpperCase().replaceAll(Constants.INVALID_CHARS_FOR_TABLE_NAMES, Constants.EMPTY_STRING);
		searchTerm = searchTerm.replace('*', '%');
		this.textTableSearchTerm.setText(searchTerm);
		this.textTableSearchTerm.selectAll();

		return searchTerm;
	}

	void performBrowseTables() {

		SapSystem selectedSapSystem = (SapSystem) this.sapSystemSelectionPage.getSelectedSAPSystem(); //getSharedObjectsMap().get(SharedWizardObjectsMap.SELECTED_SAP_SYSTEM); 
		try {
			String searchTerm = this.getSearchTerm();

			if (Utilities.isEmpty(searchTerm)) {
				return;
			}

			SapTable[] browsedTables = SapLogicalTableBrowser.getTables(searchTerm, selectedSapSystem);
			if ((browsedTables == null) || (browsedTables.length == 0)) {
				MessageDialog.openInformation(getShell(), Messages.TableBrowserWizardPage_14, MessageFormat.format(Messages.TableBrowserWizardPage_15, searchTerm));
			} else {
				this.browsedTablesProvider.clear();
				this.browsedTablesProvider.addAll(browsedTables);
				setEnabled(true);
			}
		} catch (JCoException jcoException) {
			ExceptionHandler.handleJcoException(jcoException, selectedSapSystem, this.getShell());
			this.browsedTablesProvider.clear();
		}

		this.tableBrowsedTables.refresh();
	}

	void addTables() {
		TableItem[] selectedItems = this.tableBrowsedTables.getTable().getSelection();
		for (int i = 0; i < selectedItems.length; i++) {
			SapTable table = (SapTable) selectedItems[i].getData();
			this.browsedTablesProvider.removeTable(table);
			this.selectedTablesProvider.addTable(table);
		}
		this.tableBrowsedTables.refresh();
		this.tableSelectedTables.refresh();
		dialogChanged();
	}

	void removeTables() {
		TableItem[] selectedItems = this.tableSelectedTables.getTable().getSelection();

		for (int i = 0; i < selectedItems.length; i++) {
			SapTable table = (SapTable) selectedItems[i].getData();
			this.selectedTablesProvider.removeTable(table);
		}
		this.tableSelectedTables.refresh();
		dialogChanged();
	}

	private void setEnabled(boolean enabled) {
		this.textTableSearchTerm.setEnabled(enabled);
		this.buttonSearch.setEnabled(enabled);
	}

	public void dialogChanged() {
		//	((IMetadataImportWizard) getWizard()).setAllowFinish(false);
		// Make sure at least one table was selected
		if (noTablesSelected()) {
			updateStatus(Messages.TableBrowserWizardPage_19);
			return;
		} else {
			updateStatus(null);
		}

	}

	private void updateStatus(String message) {
		if (message == null) {
			setErrorMessage(null);
			setPageComplete(true);
			return;
		}
		setErrorMessage(message);
		setPageComplete(false);
	}

	private boolean noTablesSelected() {
		return (this.selectedTablesProvider.getTables().size() == 0);
	}

	public Set<String> getSelectedTables() {
		HashSet<SapTable> tables = this.selectedTablesProvider.getTables();
		HashSet<String> tableNames = new HashSet<String>(tables.size());

		Iterator<SapTable> iterator = tables.iterator();
		while (iterator.hasNext()) {
			SapTable currentTable = (SapTable) iterator.next();
			tableNames.add(currentTable.getName());
		}
		return tableNames;
	}

	/*
	public boolean nextPressed() {
		HashSet<SapTable> tables = this.selectedTablesProvider.getTables();
		HashSet<String> tableNames = new HashSet<String>(tables.size());

		Iterator<SapTable> iterator = tables.iterator();
		while (iterator.hasNext()) {
			SapTable currentTable = (SapTable) iterator.next();
			tableNames.add(currentTable.getName());
		}
		this.getSharedObjectsMap().put(SharedWizardObjectsMap.TABLE_INITIAL_TABLES, tableNames);
		this.saveWidgetValues();
		return true;
	}
	*/

	/*
	// -----------------------------
	public final void saveWidgetValues() {
		IDialogSettings settings = getDialogSettings();
		if (settings != null) {
			settings.put("TableBrowserWizardPage.textTableSearchTerm", this.textTableSearchTerm.getText()); //$NON-NLS-1$

		}
	}

	protected void restoreWidgetValues() {
		if (this.logicalTablesGroup == null) {
			return;
		}

		IDialogSettings settings = getDialogSettings();
		if (settings != null) {

			String searchTerm = settings.get("TableBrowserWizardPage.textTableSearchTerm"); //$NON-NLS-1$
			if (searchTerm != null) {
				this.textTableSearchTerm.setText(searchTerm);
				textTableSearchTerm.selectAll();
			}

		}
	}
	*/

	/**
	 * updateEnablement
	 */
	private void updateEnablement() {

		/* search field should contain at least
		 * one alphanumeric character or numeric 
		 * character
		 */
		if (Utilities.containsAllowedChars(this.textTableSearchTerm.getText())) {
			this.buttonSearch.setEnabled(true);
		} else {
			this.buttonSearch.setEnabled(false);
		}

	}

}