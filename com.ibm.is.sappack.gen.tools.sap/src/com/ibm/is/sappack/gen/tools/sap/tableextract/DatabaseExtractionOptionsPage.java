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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.ibm.is.sappack.gen.common.ui.Messages;


public class DatabaseExtractionOptionsPage extends WizardBasePage {
   
   Button deleteCheckBox;
   Button continueOnErrorCheckBox;
   Text   commitCountText;
   Text   maxRowsCountText;

   
	static String copyright() {
		return com.ibm.is.sappack.gen.tools.sap.tableextract.Copyright.IBM_COPYRIGHT_SHORT;
	}


	public DatabaseExtractionOptionsPage() {
		super("DatabaseExtractionOptionsPage", //$NON-NLS-1$
		      Messages.DatabaseExtractionOptionsPage_0,
		      Messages.DatabaseExtractionOptionsPage_1);
	}

	@Override
   public Composite createPageControls(final Composite parent) {
		Composite mainComposite = new Composite(parent, SWT.NONE);
		GridLayout gl = new GridLayout();
		gl.numColumns = 1;
		gl.makeColumnsEqualWidth = true;
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		mainComposite.setLayout(gl);
		mainComposite.setLayoutData(gd);

		Group optionsGroup = new Group(mainComposite, SWT.NONE);
		optionsGroup.setText(Messages.DatabaseExtractionOptionsPage_2);
		optionsGroup.setLayout(new GridLayout(1, false));
		optionsGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		this.deleteCheckBox = new Button(optionsGroup, SWT.CHECK);
		this.deleteCheckBox.setText(Messages.DatabaseExtractionOptionsPage_3);
		this.deleteCheckBox.setSelection(true);
		
		this.continueOnErrorCheckBox = new Button(optionsGroup, SWT.CHECK);
		this.continueOnErrorCheckBox.setText(Messages.DatabaseExtractionOptionsPage_4);
		this.continueOnErrorCheckBox.setSelection(true);

		Composite commitComposite = new Composite(optionsGroup, SWT.NONE);
		commitComposite.setLayout(new GridLayout(2, false));
		commitComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Label commitLabel = new Label(commitComposite, SWT.NONE);
		commitLabel.setText(Messages.DatabaseExtractionOptionsPage_5);
		commitCountText = new Text(commitComposite, SWT.BORDER);
		commitCountText.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));

		ModifyListener listener = new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				update();
			}

		};

		commitCountText.addModifyListener(listener);

		
		Composite maxRowsComposite = new Composite(optionsGroup, SWT.NONE);
		maxRowsComposite.setLayout(new GridLayout(2, false));
		maxRowsComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Label maxRowsLabel = new Label(maxRowsComposite, SWT.NONE);
		maxRowsLabel.setText(Messages.DatabaseExtractionOptionsPage_6);
		maxRowsCountText = new Text(maxRowsComposite, SWT.BORDER);
		maxRowsCountText.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));

		this.maxRowsCountText.addModifyListener(listener);
		
		return(mainComposite);
	}


	public static int getNumber(Text t) {
		String s = t.getText().trim();
		if (!s.isEmpty()) {
			int i;
			try {
				i = Integer.parseInt(s);
			} catch(NumberFormatException exc) {
				return Integer.MIN_VALUE;
			}
			return i;
		} 
		return 0;
	}
	
	public static int getPositiveNumber(Text t) {
		int i = getNumber(t);
		if (i != Integer.MIN_VALUE) {
			if (i < 0 ) {
				i = Integer.MIN_VALUE;
			}
		}
		return i;
	}

	private void update() {
		boolean complete = true;
		setErrorMessage(null);
		if (complete) {
			int commitCount = getPositiveNumber(this.commitCountText);
			if (commitCount == Integer.MIN_VALUE) {
				complete = false;
				setErrorMessage(Messages.DatabaseExtractionOptionsPage_7);
			}
		}
		if (complete) {
			int maxRows = getPositiveNumber(this.maxRowsCountText);
			if (maxRows == Integer.MIN_VALUE) {
				complete = false;
				setErrorMessage(Messages.DatabaseExtractionOptionsPage_8);
			}
			
		}
		setPageComplete(complete);
	}

	public int getCommitCount() {
		return getPositiveNumber(commitCountText);
	}
	
	public int getMaxRows() {
		return getPositiveNumber(maxRowsCountText);
	}

	public boolean getDeleteTables() {
		return this.deleteCheckBox.getSelection();
	}

	public boolean getContinueOnError() {
		return this.continueOnErrorCheckBox.getSelection();
	}
}
