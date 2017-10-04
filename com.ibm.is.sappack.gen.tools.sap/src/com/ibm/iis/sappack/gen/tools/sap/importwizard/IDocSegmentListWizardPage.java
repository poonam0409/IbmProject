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


import java.util.Iterator;
import java.util.Set;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.PlatformUI;

import com.ibm.iis.sappack.gen.common.ui.util.Utils;
import com.ibm.iis.sappack.gen.common.ui.wizards.PersistentWizardPageBase;
import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.common.ui.WidgetIDConstants;
import com.ibm.is.sappack.gen.common.ui.WidgetIDUtils;
import com.ibm.is.sappack.gen.tools.sap.model.IDocSegmentTableSet;
import com.ibm.is.sappack.gen.tools.sap.model.IDocTableSet;
import com.ibm.is.sappack.gen.tools.sap.model.SapTable;
import com.ibm.is.sappack.gen.tools.sap.model.SapTableSet;


public class IDocSegmentListWizardPage extends PersistentWizardPageBase {
	// TODO create dedicated messages for IDocSegmentListWizardPage
	private static final String COLUMN_NAME_TEXT_TABLES = Messages.TableListWizardPage_0;
	private static final String COLUMN_NAME_CHECK_TABLES = Messages.TableListWizardPage_1;
	private static final String COLUMN_NAME_DESCRIPTION = Messages.TableListWizardPage_2;
	private static final String COLUMN_NAME_NAME = Messages.IDocSegmentListWizardPage_6;

	private TableViewer tableViewer;
	private Set<String> selectedTablesSet;


	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	

	/**
	 * IDocSegmentWizardPage
	 */
	public IDocSegmentListWizardPage() {
		super(Messages.IDocSegmentWizardPage_0, Messages.IDocSegmentListWizardPage_0, null);
		setDescription(Messages.IDocSegmentListWizardPage_1);
	}

	private IDocTableSet adjustSelectionOfCollectedTables(IDocTableSet idocTableSet) {
		IDocTableSet newIDocTableSet = new IDocTableSet(idocTableSet.getIDocType());
		;
		Iterator<IDocSegmentTableSet> segIter = idocTableSet.getIdocSegmentTableSetList().iterator();

		// only add selected segment tables to the result set
		while (segIter.hasNext()) {
			IDocSegmentTableSet curIDocSegTableSet = segIter.next();

			IDocSegmentTableSet newTableSet = new IDocSegmentTableSet(curIDocSegTableSet.getIdocSegment());
			Iterator<SapTable> tblIter = curIDocSegTableSet.getSapTableSet().iterator();
			while (tblIter.hasNext()) {
				SapTable curSAPTable = tblIter.next();
				if (this.selectedTablesSet.contains(curSAPTable.getName())) {
					newTableSet.add(curSAPTable);
				}
			}

			newIDocTableSet.add(newTableSet);
		}

		return (newIDocTableSet);
	}

	@Override
	public Composite createControlImpl(final Composite parent) {

		Composite mainComposite = new Composite(parent, SWT.NULL);

		// create a layout for this wizard page
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		mainComposite.setLayout(gridLayout);

		this.tableViewer = new TableViewer(mainComposite, SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION);
		GridData gridDataList = new GridData(GridData.FILL_BOTH);
		this.tableViewer.getTable().setLayoutData(gridDataList);
		gridDataList.widthHint = 140;

		/*
		CellEditor cellEditor = new CheckboxCellEditor(this.tableViewer.getTable());
		this.tableViewer.setCellEditors(new CellEditor[] { cellEditor });
		this.tableViewer.getTable().addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent keyEvt) {
				if (keyEvt.character == ' ') {
					Table curTable = (Table) keyEvt.getSource();
					TableItem[] selectedSAPTablesArr = curTable.getSelection();

					// toggle selected row (SAP Tables only !!!)
					if (selectedSAPTablesArr.length > 0) {
						Object tmpObject = selectedSAPTablesArr[0].getData();

						if (tmpObject instanceof SapTable) {
							SapTable sapTblElement = (SapTable) tmpObject;
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

		SapProcessedIDocListProvider provider = new SapProcessedIDocListProvider(this.tableViewer, this);
		this.tableViewer.setColumnProperties(new String[] { COLUMN_NAME_NAME, COLUMN_NAME_DESCRIPTION, COLUMN_NAME_CHECK_TABLES, COLUMN_NAME_TEXT_TABLES });
		this.tableViewer.setContentProvider(provider);
		this.tableViewer.setLabelProvider(provider);
		this.tableViewer.setCellModifier(provider);

		WidgetIDUtils.assignID(this.tableViewer.getTable(), WidgetIDConstants.MOD_SEGMENTLISTTABLE);

		/*
		// if we are in replay mode we make all children read only
		if (((IMetadataImportWizard) getWizard()).isReplayMode()) {
			makeAllChildrenReadOnly(container);
		}
		*/

		final Button showCheckTablesButton = new Button(mainComposite, SWT.NONE);
		showCheckTablesButton.setText(Messages.IDocSegmentListWizardPage_5);
		showCheckTablesButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TableItem[] selection = tableViewer.getTable().getSelection();
				if (selection != null && selection.length == 1) {
					Object o = selection[0].getData();
					String ctColumn = selection[0].getText(SapTable.COLUMN_CHECK_TABLES);
					String ttColumn = selection[0].getText(SapTable.COLUMN_TEXT_TABLES);
					AdditionalInfoDialog d = new AdditionalInfoDialog(o, ctColumn, ttColumn, getShell());
					d.open();
				}
			}
		});

		this.tableViewer.getTable().addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				TableItem[] selection = tableViewer.getTable().getSelection();
				boolean enableButton = (selection != null && selection.length == 1);
				showCheckTablesButton.setEnabled(enableButton);
			}

		});

		/*
		this.getSharedObjectsMap().addObserver(new Observer() {

			@Override
			public void update(Observable observable, Object data) {
				ObservableMap<String, Object> om = (ObservableMap<String, Object>) observable;
				IDocTableSet its = (IDocTableSet) om.get(IDocPageGroup.COLLECTED_ENTITIES);

				if (its != null) {
					// in REPLAY mode, adjust the selection of the collected tables
					if (IDocSegmentListWizardPage.this.replayMode) {
						its = adjustSelectionOfCollectedTables(its);
					}

					setIDocTableSet(its);

					TableItem[] selection = tableViewer.getTable().getSelection();
					boolean enabled = (selection != null && selection.length == 1);
					showCheckTablesButton.setEnabled(enabled);
				} // end of if (its != null)
			}

		});
		*/

		//	restoreWidgetValues();

		//		setInitialFocusToDlgFld(this.tableViewer.getTable());

		return (mainComposite);
	}

	/**
	 * setIDocTableSet
	 * @param idocTableSet
	 */
	public void setIDocTableSet(IDocTableSet idocTableSet) {
		if (idocTableSet.getIdocSegmentTableSetList().isEmpty()) {
			setErrorMessage(Messages.IDocSegmentListWizardPage_4);
			this.tableViewer.setInput(null);
		} else {
			setErrorMessage(null);
			this.tableViewer.setInput(idocTableSet);
		}
	}

	@Override
	public boolean canFlipToNextPage() {
		if (getIDocTableSet() == null) {
			return false;
		}
		return super.canFlipToNextPage();
	}

	/**
	 * getIdocTableSet
	 * @return
	 */
	private IDocTableSet getIDocTableSet() {
		return (IDocTableSet) this.tableViewer.getInput();
	}

	/**
	 * getSelectedValueTableNames
	 * @return
	 */
	/*
	private List<String> getSelectedValueTableNames() {
		
		IDocTableSet idocTableSet = (IDocTableSet) this.tableViewer.getInput();
		List<String> valueTables = new ArrayList<String>();
		for(IDocSegmentTableSet idocSegmentTableSet:idocTableSet.getIdocSegmentTableSetList()) {
			SapTableSet tableSet = idocSegmentTableSet.getSapTableSet();
			Iterator<SapTable> tableIterator = tableSet.iterator();
			 while(tableIterator.hasNext()) {
				 SapTable table = tableIterator.next();
				 // only add selected tables
				 if(table.getSelected() && !valueTables.contains(table.getName())) {
					valueTables.add(table.getName());
				 }
			 }
		}
		return valueTables;
	}
	*/

	/**
	 * getSelectedValueTables
	 * @return
	 */
	private SapTableSet getSelectedValueTables() {

		SapTableSet valueTables = new SapTableSet();
		IDocTableSet idocTableSet = (IDocTableSet) this.tableViewer.getInput();
		for (IDocSegmentTableSet idocSegmentTableSet : idocTableSet.getIdocSegmentTableSetList()) {
			SapTableSet tableSet = idocSegmentTableSet.getSapTableSet();
			Iterator<SapTable> iterator = tableSet.iterator();
			while (iterator.hasNext()) {
				SapTable table = iterator.next();
				// only add selected tables
				if (table.getSelected() && !valueTables.contains(table)) {
					valueTables.add(table);
				}
			}
		}
		return valueTables;
	}

	public boolean nextPressedImpl() {
		SapTableSet tables = getSelectedValueTables();
		int entities = getIDocTableSet().getIdocSegmentTableSetList().size() + tables.size();
		if (!TableListWizardPage.askForImport(this, entities)) {
			return false;
		}
		return true;
	}
	
	@Override
	public void performHelp() {
		PlatformUI.getWorkbench().getHelpSystem().displayHelp(Utils.getHelpID("rmwiz_segments_list")); //$NON-NLS-1$
	}

}
