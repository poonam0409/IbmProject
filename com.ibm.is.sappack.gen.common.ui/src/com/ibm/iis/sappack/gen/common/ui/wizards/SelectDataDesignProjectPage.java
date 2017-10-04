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
// Module Name : com.ibm.iis.sappack.gen.common.ui.wizards
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.iis.sappack.gen.common.ui.wizards;


import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.ibm.is.sappack.gen.common.ui.Messages;


public class SelectDataDesignProjectPage extends WizardPage {

	private IProject[] dbDesignProjects;
	private Combo projectCombo;
	private boolean treatExistingFilesAsWarnings = false;

	public static final String SELECTDATADESIGNPROJECTPAGE = "selectdatadesignprojectpage"; //$NON-NLS-1$

	private List<ModifyListener> listeners = new ArrayList<ModifyListener>();


	static String copyright() { 
		return Copyright.IBM_COPYRIGHT_SHORT; 
	}


	public SelectDataDesignProjectPage() {
		super(SELECTDATADESIGNPROJECTPAGE); 
		setTitle(Messages.SelectDataDesignProjectPage_1); 

	}

	public void addModifyListenerToProjectCombo(ModifyListener l) {
		if (projectCombo != null) {
			this.projectCombo.addModifyListener(l);
		} else {
			this.listeners.add(l);
		}
	}
	

	public boolean getTreatExistingFilesAsWarnings() {
		return this.treatExistingFilesAsWarnings;
	}

	public void setTreatExistingFilesAsWarnings(boolean on) {
		this.treatExistingFilesAsWarnings = on;
	}


	public static IProject[] getDatabaseDesignProjects() {
		List<IProject> result = new ArrayList<IProject>();
		try {
			IWorkspaceRoot wsRoot = ResourcesPlugin.getWorkspace().getRoot();
			IProject[] allProjects = wsRoot.getProjects();
			for (IProject proj : allProjects) {
				if (proj.isOpen()) {
					if (proj.hasNature(com.ibm.is.sappack.gen.common.Constants.COM_IBM_DATATOOLS_CORE_UI_DATABASE_DESIGN_NATURE)) { 
						result.add(proj);
					}
				}
			}
		} catch (CoreException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return result.toArray(new IProject[result.size()]);
	}

	@Override
	public void createControl(Composite parent) {
		Composite mainComposite = new Composite(parent, SWT.NONE);
		mainComposite.setLayout(new GridLayout(2, false));
		mainComposite.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL | GridData.FILL_BOTH));

		ModifyListener modifyListener = new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				updateEnablement();
			}

		};

		//	Group selectProjectComposite = new Group(mainComposite, SWT.NONE);
		//	selectProjectComposite.setText("Data Design Project"); //$NON-NLS-1$
		//	GridLayout infoServerLayout = new GridLayout();
		//	infoServerLayout.numColumns = 2;
		//	selectProjectComposite.setLayout(infoServerLayout);
		//	selectProjectComposite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));

		Composite selectProjectComposite = mainComposite;

		Label selectProjectLabel = new Label(selectProjectComposite, SWT.NONE);
		selectProjectLabel.setText(Messages.SelectDataDesignProjectPage_3); 

		this.projectCombo = new Combo(selectProjectComposite, SWT.BORDER | SWT.READ_ONLY);
		this.dbDesignProjects = getDatabaseDesignProjects();
		String[] items = new String[this.dbDesignProjects.length];
		for (int i = 0; i < items.length; i++) {
			items[i] = this.dbDesignProjects[i].getName();
		}
		this.projectCombo.setItems(items);
		this.projectCombo.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
		this.projectCombo.addModifyListener(modifyListener);

		for (ModifyListener l : this.listeners) {
			this.projectCombo.addModifyListener(l);
		}

		setControl(mainComposite);
		updateEnablement();
	}

	public IProject getSelectedProject() {
		int ix = this.projectCombo.getSelectionIndex();
		if (ix == -1) {
			return null;
		}
		return this.dbDesignProjects[ix];
	}

	private void updateEnablement() {
		boolean complete = true;
		if (getSelectedProject() == null) {
			setErrorMessage(Messages.SelectDataDesignProjectPage_4); 
			complete = false;
		}
		if (complete) {
			setErrorMessage(null);
		}
		setPageComplete(complete);
	}

}
