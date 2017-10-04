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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.datatools.modelbase.sql.tables.Table;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.ibm.is.sappack.gen.common.Constants;
import com.ibm.is.sappack.gen.common.ui.Messages;

public class CreateSQLScriptOptionsWizardPage extends WizardBasePage {

	static String copyright() {
		return com.ibm.is.sappack.gen.tools.sap.tableextract.Copyright.IBM_COPYRIGHT_SHORT;
	}

	final static String SQLSUFFIX = ".sql"; //$NON-NLS-1$
	
	Text fileText;
	Button deleteCheckBox;
	Text commitCountText;
	Text maxRowsCountText;
	
	private IProject[] dbDesignProjects;
	private Combo projectCombo;

	
	public CreateSQLScriptOptionsWizardPage() {
		super("CreateSQLScriptOptionsWizardPage", //$NON-NLS-1$
		      Messages.CreateSQLScriptOptionsWizardPage_0,
		      Messages.CreateSQLScriptOptionsWizardPage_1);
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
		
		ModifyListener modifyListener = new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				update();
			}

		};
		
		Group fileGroup = new Group(mainComposite, SWT.NONE);
		fileGroup.setText(Messages.CreateSQLScriptOptionsWizardPage_2);
		fileGroup.setLayout(new GridLayout(2, false));
		fileGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Label selectProjectLabel = new Label(fileGroup, SWT.NONE);
		selectProjectLabel.setText(Messages.CreateSQLScriptOptionsWizardPage_3);

		this.projectCombo = new Combo(fileGroup, SWT.BORDER | SWT.READ_ONLY);
		this.dbDesignProjects = getDatabaseDesignProjects();
		String[] items = new String[this.dbDesignProjects.length];
		for (int i = 0; i < items.length; i++) {
			items[i] = this.dbDesignProjects[i].getName();
		}
		this.projectCombo.setItems(items);
		this.projectCombo.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
		this.projectCombo.select(0);
		this.projectCombo.addModifyListener(modifyListener);
		
		Label fileName = new Label(fileGroup, SWT.NONE);
		fileName.setText(Messages.CreateSQLScriptOptionsWizardPage_4);
		
		this.fileText = new Text(fileGroup, SWT.BORDER);
		this.fileText.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
		String fileDefault = "Script" + SQLSUFFIX; //$NON-NLS-1$
		List<Table> tables = ((ExtractTablesWizard) this.getWizard()).getTables();
		if (tables != null && tables.size() > 0) {
			fileDefault = tables.get(0).getName() + SQLSUFFIX;
		}
		this.fileText.setText(fileDefault);
		this.fileText.addModifyListener(modifyListener);
		
		
		Group optionsGroup = new Group(mainComposite, SWT.NONE);
		optionsGroup.setText(Messages.CreateSQLScriptOptionsWizardPage_5);
		optionsGroup.setLayout(new GridLayout(1, false));
		optionsGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		this.deleteCheckBox = new Button(optionsGroup, SWT.CHECK);
		this.deleteCheckBox.setText(Messages.CreateSQLScriptOptionsWizardPage_6);
		this.deleteCheckBox.setSelection(true);
		this.deleteCheckBox.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				update();
			}
			
		});
		
		Composite commitComposite = new Composite(optionsGroup, SWT.NONE);
		commitComposite.setLayout(new GridLayout(2, false));
		commitComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Label commitLabel = new Label(commitComposite, SWT.NONE);
		commitLabel.setText(Messages.CreateSQLScriptOptionsWizardPage_7);
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
		maxRowsLabel.setText(Messages.CreateSQLScriptOptionsWizardPage_8);
		maxRowsCountText = new Text(maxRowsComposite, SWT.BORDER);
		maxRowsCountText.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));

		maxRowsCountText.addModifyListener(listener);
		
		update();
		
		return(mainComposite);
	}
	
	
	public static IProject[] getDatabaseDesignProjects() {
		List<IProject> result = new ArrayList<IProject>();
		try {
			IWorkspaceRoot wsRoot = ResourcesPlugin.getWorkspace().getRoot();
			IProject[] allProjects = wsRoot.getProjects();
			for (IProject proj : allProjects) {
				if (proj.hasNature(Constants.COM_IBM_DATATOOLS_CORE_UI_DATABASE_DESIGN_NATURE)) { 
					result.add(proj);
				}
			}
		} catch (CoreException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return result.toArray(new IProject[result.size()]);
	}
	
	private IProject getSelectedProject() {
		int ix = this.projectCombo.getSelectionIndex();
		if (ix == -1) {
			return null;
		}
		return this.dbDesignProjects[ix];
	}
	
	private void update() {
		boolean complete = true;
		setErrorMessage(null);
		if (complete && getSelectedProject() == null) {
			complete = false;
			setErrorMessage(Messages.CreateSQLScriptOptionsWizardPage_9);
		}
		String file = getSQLScriptFileName();
		if (complete && file.trim().isEmpty() && !file.trim().equals(SQLSUFFIX)) {
			complete = false;
			setErrorMessage(Messages.CreateSQLScriptOptionsWizardPage_10);
		}
		if (complete && !file.endsWith(SQLSUFFIX)) {
			complete = false;
			String msg = Messages.CreateSQLScriptOptionsWizardPage_11;
			msg = MessageFormat.format(msg, SQLSUFFIX);
			setErrorMessage(msg);
		}
		if (complete) {
			IFile f = getSQScriptFile();
			if (f == null) {
				complete = false;
				setErrorMessage(Messages.CreateSQLScriptOptionsWizardPage_12);
			} else {
				if (f.exists()) {
					complete = false;
					setErrorMessage(Messages.CreateSQLScriptOptionsWizardPage_13);
				}
			}
		}
		if (complete) {
			String t = this.commitCountText.getText().trim();
			if (!t.isEmpty()) {
				int i = 1;
				try {
					i = Integer.parseInt(t);
				} catch (NumberFormatException nfe) {
					setErrorMessage(Messages.CreateSQLScriptOptionsWizardPage_14);
					complete = false;
				}
				if (complete && i < 0) {
					setErrorMessage(Messages.CreateSQLScriptOptionsWizardPage_15);
					complete = false;
				}
			}
		}
		if (complete) {
			int i = DatabaseExtractionOptionsPage.getPositiveNumber(this.maxRowsCountText);
			if (i == Integer.MIN_VALUE) {
				setErrorMessage(Messages.CreateSQLScriptOptionsWizardPage_16);
				complete = false;				
			}
		}
		
		setPageComplete(complete);
	}

	private String getSQLScriptFileName() {
		return this.fileText.getText();
	}
	
	public IFile getSQScriptFile() {
		IProject proj = getSelectedProject();
		if (proj == null) {
			return null;
		}
		return proj.getFile(getSQLScriptFileName());
	}
	
	public boolean generateDeleteStatement() {
		return this.deleteCheckBox.getSelection();
	}
	
	public int getCommitCount() {
		String t = this.commitCountText.getText();
		if (t.trim().isEmpty()) {
			return 0;
		}
		int i = Integer.parseInt(t);
		return i;
	}
	
	public int getMaxRows() {
		return DatabaseExtractionOptionsPage.getPositiveNumber(maxRowsCountText);
	}

}
