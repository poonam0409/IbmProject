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
// Module Name : com.ibm.is.sappack.gen.tools.jobgenerator.validator
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.tools.jobgenerator.validator;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.PlatformUI;

import com.ibm.iis.sappack.gen.common.ui.wizards.INextActionWizardPage;
import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.tools.jobgenerator.Activator;

public class ValidationResultPage extends WizardPage implements INextActionWizardPage {

	private static final String HELP_ID = Activator.PLUGIN_ID + "." + "ValidationResultPage"; //$NON-NLS-1$ //$NON-NLS-2$

	private TreeViewer treeViewer;
	/* column names for table */
	private static final String COLUMN_NAME_EMPTY = ""; //$NON-NLS-1$

	public static final int COLUMN_NAME = 0;
	public static final int COLUMN_VALUE = 1;
	public static final int COLUMN_STATUS = 2;
	public static final int COLUMN_MESSAGE = 3;

	/* validation results */
	private List<IValidationResult> validationResults;

	static String copyright() {
		return com.ibm.is.sappack.gen.tools.jobgenerator.validator.Copyright.IBM_COPYRIGHT_SHORT;
	}

	/**
	 * ValidationResultPage
	 * @param validationResults
	 */
	protected ValidationResultPage(List<IValidationResult> validationResults) {
		super("sapvalidationresultpage", //$NON-NLS-1$
				Messages.ValidationResultPage_1, null);
		this.setDescription(Messages.ValidationResultPage_2);

		this.validationResults = validationResults;
	}

	/**
	 * ValidationResultPage
	 */
	protected ValidationResultPage() {
		super("sapvalidationresultpage", //$NON-NLS-1$
				Messages.ValidationResultPage_1, null);
		this.setDescription(Messages.ValidationResultPage_2);

		this.validationResults = new ArrayList<IValidationResult>();
		setPageComplete(false);
	}

	/**
	 * updateValidationResult
	 * 
	 * @param validationResults
	 */
	public void updateValidationResult(List<IValidationResult> validationResults) {
		this.validationResults = validationResults;
		this.refreshValidationResults();
		setPageComplete(true);
	}

	@Override
	public void createControl(Composite parent) {
		Composite mainComposite = new Composite(parent, SWT.NONE);

		// create a layout for this wizard page
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		mainComposite.setLayout(gridLayout);

		this.treeViewer = new TreeViewer(mainComposite, SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION);
		GridData gridDataList = new GridData(GridData.FILL_BOTH);
		this.treeViewer.getTree().setLayoutData(gridDataList);

		TreeColumn treeColumnName = new TreeColumn(this.treeViewer.getTree(), SWT.LEFT);
		treeColumnName.setText(COLUMN_NAME_EMPTY);
		treeColumnName.setWidth(135);

		TreeColumn treeColumnValue = new TreeColumn(this.treeViewer.getTree(), SWT.LEFT);
		treeColumnValue.setText(COLUMN_NAME_EMPTY);
		treeColumnValue.setWidth(100);

		TreeColumn treeColumnStatus = new TreeColumn(this.treeViewer.getTree(), SWT.LEFT);
		treeColumnStatus.setText(COLUMN_NAME_EMPTY);
		treeColumnStatus.setWidth(18);

		TreeColumn treeColumnMessage = new TreeColumn(this.treeViewer.getTree(), SWT.LEFT);
		treeColumnMessage.setText(COLUMN_NAME_EMPTY);
		treeColumnMessage.setWidth(145);

		final Tree tree = this.treeViewer.getTree();
		tree.setHeaderVisible(true);
		tree.setLinesVisible(true);

		// adding a listener for column resizing
		tree.addListener(SWT.Expand, new Listener() {
			@Override
			public void handleEvent(Event event) {
				getShell().getDisplay().asyncExec(new Runnable() {
					@Override
					public void run() {
						if (tree.isDisposed()) {
							return;
						}

						// resize columns to proper width (so that the columns width fits the content length)
						resizeColumns(tree);
					}
				});
			}
		});

		ValidationResultContentProvider contentProvider = new ValidationResultContentProvider();
		ValidationResultLabelProvider labelProvider = new ValidationResultLabelProvider();
		this.treeViewer.setColumnProperties(new String[] { COLUMN_NAME_EMPTY, COLUMN_NAME_EMPTY, COLUMN_NAME_EMPTY, COLUMN_NAME_EMPTY });
		this.treeViewer.setContentProvider(contentProvider);
		this.treeViewer.setLabelProvider(labelProvider);
		this.treeViewer.setInput(this.validationResults);

		this.refreshValidationResults();

		this.setControl(mainComposite);
	}

	@Override
	public void performHelp() {
		PlatformUI.getWorkbench().getHelpSystem().displayHelp(HELP_ID);
	}

	private void resizeColumns(Tree tree) {
		TreeColumn[] cols = tree.getColumns();
		for (int i = 0; i < cols.length; i++) {
			cols[i].pack();
		}
	}

	/**
	 * refreshValidationResults
	 * 
	 * refresh the validation results in the treeViewer
	 */
	private void refreshValidationResults() {

		this.treeViewer.setInput(this.validationResults);

		// show all elements
		this.treeViewer.expandAll();

		// resize columns to proper width (so that the columns width fits the content length)
		// the resize listener registered above does not work for code-wise tree actions (e.g. expand)
		// so we have to call the column resizing manually at this point
		resizeColumns(this.treeViewer.getTree());
	}

	// implementation of abstract methods to be implemented
	public void updateEnablement() {

	}

	public void init() {
	}

	public void saveWidgetValues() {
	}

	public boolean nextPressed() {
		return (true);
	}

	//   @Override
	//	public boolean isPageComplete() {
	//		
	//		return false;
	//	}

}
