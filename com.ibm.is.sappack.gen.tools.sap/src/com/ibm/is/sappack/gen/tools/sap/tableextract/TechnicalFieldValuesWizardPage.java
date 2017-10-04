//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2009, 2011                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.is.sappack.gen.tools.sap.tableextract
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.tools.sap.tableextract;

import java.util.List;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;

import com.ibm.is.sappack.gen.common.ui.Messages;


public class TechnicalFieldValuesWizardPage extends WizardBasePage {
	private TableViewer tableViewer;
	private Button      useTechincalFieldValues;
	private Combo       tableCombo;
	
	
   static String copyright() {
      return com.ibm.is.sappack.gen.tools.sap.tableextract.Copyright.IBM_COPYRIGHT_SHORT;
   }

   
	public TechnicalFieldValuesWizardPage() {
		super("TechnicalFieldValuesWizardPage", //$NON-NLS-1$
		      Messages.TechnicalFieldValuesWizardPage_0,
		      Messages.TechnicalFieldValuesWizardPage_1);
	}

	@Override
   public Composite createPageControls(final Composite parent) {
	   
		Group technicalFieldValuesGroup = new Group(parent, SWT.NONE);
		technicalFieldValuesGroup.setText(Messages.TechnicalFieldValuesWizardPage_2);
		GridLayout gridLayout = new GridLayout(1, false);
		technicalFieldValuesGroup.setLayout(gridLayout);

		this.useTechincalFieldValues = new Button(technicalFieldValuesGroup, SWT.CHECK);
		this.useTechincalFieldValues.setText(Messages.TechnicalFieldValuesWizardPage_3);
		this.useTechincalFieldValues.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				update();
			}
		});
		this.useTechincalFieldValues.setSelection(false);

		Composite comp = new Composite(technicalFieldValuesGroup, SWT.NONE);
		comp.setLayout(new GridLayout(2, false));
		comp.setLayoutData(new GridData(GridData.FILL));
		
		Label l = new Label(comp, SWT.NONE);
		l.setText(Messages.TechnicalFieldValuesWizardPage_4);

		List<org.eclipse.datatools.modelbase.sql.tables.Table> tables = ((ExtractTablesWizard) getWizard()).getTables();
		String[] tableNames = new String[tables.size()];
		for (int i=0; i<tableNames.length; i++) {
			tableNames[i] = tables.get(i).getName();
		}
		this.tableCombo = new Combo(comp, SWT.NONE);
		this.tableCombo.setItems(tableNames);
		this.tableCombo.select(0);
		
		
		this.tableViewer = new TableViewer(technicalFieldValuesGroup, SWT.FULL_SELECTION);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL);
		gridData.horizontalSpan = 1;
		Table table = this.tableViewer.getTable();
		table.setLayoutData(gridData);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		TableViewerColumn fieldNameColumn = new TableViewerColumn(this.tableViewer, SWT.LEFT);
		fieldNameColumn.getColumn().setText(Messages.TechnicalFieldValuesWizardPage_5);
		fieldNameColumn.getColumn().setWidth(200);

		TableViewerColumn dataTypeColumn = new TableViewerColumn(this.tableViewer, SWT.LEFT);
		dataTypeColumn.getColumn().setText(Messages.TechnicalFieldValuesWizardPage_6);
		dataTypeColumn.getColumn().setWidth(200);

		TableViewerColumn derivationColumn = new TableViewerColumn(this.tableViewer, SWT.LEFT);
		derivationColumn.getColumn().setText(Messages.TechnicalFieldValuesWizardPage_7);
		derivationColumn.getColumn().setWidth(250);

		update();
		
		return(technicalFieldValuesGroup);
	}

	void update() {
		boolean enabled = this.useTechincalFieldValues.getSelection();
		this.tableViewer.getTable().setEnabled(enabled);
		this.tableCombo.setEnabled(enabled);
	}

}
