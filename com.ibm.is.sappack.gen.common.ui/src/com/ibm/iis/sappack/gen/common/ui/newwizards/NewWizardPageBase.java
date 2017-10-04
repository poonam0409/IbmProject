package com.ibm.iis.sappack.gen.common.ui.newwizards;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.ibm.iis.sappack.gen.common.ui.util.FileHelper;
import com.ibm.is.sappack.gen.common.ui.Messages;

public abstract class NewWizardPageBase extends WizardPage {
  	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	

	private IProject[] dbDesignProjects;
	private Combo projectCombo;
	protected Text newFileName;
	String preSelectedProject = null;

	protected NewWizardPageBase(String pageName, String title, ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
	}
	
	public void setPreselectedProject(String project) {
		this.preSelectedProject = project;
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
		Composite parentComposite = new Composite(parent, SWT.NONE);
		parentComposite.setLayout(new GridLayout(1, false));
		parentComposite.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL | GridData.FILL_BOTH));

		/*
		Composite mainComposite = new Composite(parentComposite, SWT.NONE);
		mainComposite.setLayout(new GridLayout(2, false));
		mainComposite.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL | GridData.FILL_BOTH));
		*/

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

		Group selectProjectComposite = new Group(parentComposite, SWT.NONE);
		selectProjectComposite.setText(Messages.NewWizardPageBase_0);
		selectProjectComposite.setLayout(new GridLayout(2, false));
		selectProjectComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		Label selectProjectLabel = new Label(selectProjectComposite, SWT.NONE);
		selectProjectLabel.setText(Messages.SelectDataDesignProjectPage_3);

		this.projectCombo = new Combo(selectProjectComposite, SWT.BORDER | SWT.READ_ONLY);
		this.dbDesignProjects = getDatabaseDesignProjects();
		int preselect = -1;
		String[] items = new String[this.dbDesignProjects.length];
		for (int i = 0; i < items.length; i++) {
			items[i] = this.dbDesignProjects[i].getName();
			if (items[i].equals(preSelectedProject)) {
				preselect = i;
			}
		}
		this.projectCombo.setItems(items);
		this.projectCombo.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
		if (preselect != -1) {
			this.projectCombo.select(preselect);
		}
		this.projectCombo.addModifyListener(modifyListener);

		Composite fileComposite = selectProjectComposite; // new Composite(parentComposite, SWT.NONE);
		//		fileComposite.setLayout(new GridLayout(2, false));
		//	fileComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		Label l = new Label(fileComposite, SWT.NONE);
		l.setText(Messages.NewWizardPageBase_1);

		newFileName = new Text(fileComposite, SWT.BORDER);
		newFileName.setText(getNewFileNameDefault());
		newFileName.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		newFileName.addModifyListener(modifyListener);

		/*
		l = new Label(parentComposite, SWT.SEPARATOR | SWT.HORIZONTAL);
		GridData sepGD = new GridData(SWT.FILL, SWT.TOP, true, false);
		//		sepGD.horizontalSpan = 2;
		l.setLayoutData(sepGD);
		*/


		createAdditionalControls(parentComposite);

		setControl(parentComposite);
		updateEnablement();
	}

	protected abstract void createAdditionalControls(Composite parent);

	protected abstract String getNewFileNameDefault();

	protected abstract String getNewFileExtension();

	public abstract Map<String, String> getInitialProperties();

	public String createID() {
		return FileHelper.createID(getSelectedFile());
	}

	public IProject getSelectedProject() {
		int ix = this.projectCombo.getSelectionIndex();
		if (ix == -1) {
			return null;
		}
		return this.dbDesignProjects[ix];
	}

	public IFile getSelectedFile() {
		return getSelectedProject().getFile(this.newFileName.getText().trim() + "." + this.getNewFileExtension()); //$NON-NLS-1$
	}

	/*
	public String getModeID() {
		if (modeCombo == null) {
			return null;
		}
		String modeName = this.modeCombo.getItem(this.modeCombo.getSelectionIndex());
		for (RMRGMode m : ModeManager.getInstalledModes()) {
			if (modeName.equals(m.getName())) {
				return m.getID();
			}
		}
		return null;
		
	}
	*/
	
	protected void updateEnablement() {
		boolean complete = true;
		IProject project = getSelectedProject();
		if (project == null) {
			setErrorMessage(Messages.SelectDataDesignProjectPage_4);
			complete = false;
		}
		if (complete) {
			String s = newFileName.getText();
			if (s.trim().length() == 0) {
				setErrorMessage(Messages.NewWizardPageBase_2);
				complete = false;
			}
		}
		if (complete) {
			IFile f = getSelectedFile();
			if (f.exists()) {
				String msg = Messages.NewWizardPageBase_3;
				msg = MessageFormat.format(msg, new Object[] { f.getName(), project.getName() });
				setErrorMessage(msg);
				complete = false;
			}
		}
		if (complete) {
			setErrorMessage(null);
		}
		setPageComplete(complete);
	}

}
