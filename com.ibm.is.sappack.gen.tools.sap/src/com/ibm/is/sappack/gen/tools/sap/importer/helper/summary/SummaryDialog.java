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
// Module Name : com.ibm.is.sappack.gen.tools.sap.importer.helper.summary
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.tools.sap.importer.helper.summary;


import java.util.List;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import com.ibm.is.sappack.gen.common.ui.Messages;


public class SummaryDialog extends Dialog {

	private List<SummaryEntry> summary;


	static String copyright() {
		return com.ibm.is.sappack.gen.tools.sap.importer.helper.summary.Copyright.IBM_COPYRIGHT_SHORT;
	}

	public SummaryDialog(Shell parent, List<SummaryEntry> summary) {
		this(parent, SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM, summary);
	}

	public SummaryDialog(Shell parent, int style, List<SummaryEntry> summary) {
		super(parent, style);
		setText(Messages.SummaryDialog_0);
		this.summary = summary;
	}

	public void createContents(final Shell shell) {
		shell.setLayout(new GridLayout(1, true));

		TableViewer tableViewer = new TableViewer(shell, SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION);
		// this.tableViewer.setSorter(new ViewerSorter());
		GridData gridDataTable = new GridData(GridData.FILL_BOTH);
		gridDataTable.widthHint = 410;
		gridDataTable.heightHint = 150;
		tableViewer.getTable().setLayoutData(gridDataTable);

		final Table table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		TableColumn tableColumnCheck = new TableColumn(tableViewer.getTable(), SWT.LEFT);
		tableColumnCheck.setText(""); //$NON-NLS-1$
		tableColumnCheck.setWidth(25);

		TableColumn tableColumnName = new TableColumn(tableViewer.getTable(), SWT.LEFT);
		tableColumnName.setText(Messages.SummaryDialog_1);
		tableColumnName.setWidth(100);

		TableColumn tableColumnDescription = new TableColumn(tableViewer.getTable(), SWT.LEFT);
		tableColumnDescription.setText(Messages.SummaryDialog_2);
		tableColumnDescription.setWidth(280);

		Button buttonOk = new Button(shell, SWT.PUSH);
		buttonOk.setText(Messages.SummaryDialog_3);
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
		data.widthHint = 100;
		buttonOk.setLayoutData(data);
		buttonOk.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				shell.close();
			}
		});

		SummaryDialogProvider summaryProvider = new SummaryDialogProvider();
		tableViewer.setContentProvider(summaryProvider);
		tableViewer.setLabelProvider(summaryProvider);

		// List content = new ArrayList<SummaryEntry>();
		// content.add(new
		// SummaryEntry(SummaryEntry.MESSAGE_TYPE.INFORMATION,"table1",
		// "column abc is missing"));
		// content.add(new
		// SummaryEntry(SummaryEntry.MESSAGE_TYPE.WARNING,"table2",
		// "column def is missing"));
		// content.add(new
		// SummaryEntry(SummaryEntry.MESSAGE_TYPE.ERROR,"table3",
		// "column xyz is missing"));
		//
		tableViewer.setInput(this.summary);

		shell.setDefaultButton(buttonOk);
	}

	public void open() {
		Shell parent = getParent();
		Shell shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		shell.setText(getText());

		createContents(shell);

		shell.pack();
		shell.open();

		Rectangle bounds = parent.getBounds();
		Point shellSize = shell.getSize();
		shell.setLocation(bounds.x + (bounds.width - shellSize.x) / 2, bounds.y + (bounds.height - shellSize.y) / 2);

		Display display = parent.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		return;
	}
}
