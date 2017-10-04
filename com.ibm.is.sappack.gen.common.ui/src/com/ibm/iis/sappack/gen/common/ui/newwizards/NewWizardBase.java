package com.ibm.iis.sappack.gen.common.ui.newwizards;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import com.ibm.iis.sappack.gen.common.ui.editors.PropertiesConstants;
import com.ibm.iis.sappack.gen.common.ui.util.FileHelper;
import com.ibm.is.sappack.gen.common.ui.Activator;
import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.common.ui.ModeManager;

public abstract class NewWizardBase extends Wizard implements INewWizard {
  	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	

	NewWizardPageBase newWizardPage;
	String project = null;


	public NewWizardBase() {
		super();
	}

	
	public void setPreselectedProject(String project) {
		this.project = project;
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}
	

	protected abstract NewWizardPageBase createWizardPage();

	@Override
	public void addPages() {
		newWizardPage = createWizardPage();
		newWizardPage.setPreselectedProject(project);
		addPage(newWizardPage);
	}
	
	public boolean performFinish() {
		final IFile f = this.newWizardPage.getSelectedFile();
		Map<String, String> initialProps = this.newWizardPage.getInitialProperties();
		if (initialProps == null) {
			initialProps = new HashMap<String, String>();
		}
		initialProps.put(PropertiesConstants.KEY_ID, this.newWizardPage.createID());
		String modeID = ModeManager.getActiveMode().getID();
		if (modeID != null) {
			initialProps.put(PropertiesConstants.PROP_KEY_MODE, modeID);
		}
		
		final Map<String, String> props = initialProps;

		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException {
				try {
					doFinish(f, props, monitor);
				} catch (Exception e) {
					throw new InvocationTargetException(e);
				} finally {
					monitor.done();
				}
			}
		};
		try {
			getContainer().run(true, false, op);
		} catch (InterruptedException e) {
			return false;
		} catch (InvocationTargetException e) {
			Throwable realException = e.getTargetException();
			String msg = realException.getMessage();
			if (msg == null) {
				msg = realException.getClass().getName();
			}
			Activator.logException(e);
			MessageDialog.openError(getShell(), Messages.NewWizardBase_0, msg);
			return false;
		}
		return true;
	}

	private void doFinish(final IFile f, Map<String, String> props, IProgressMonitor monitor) throws CoreException, IOException {
		String filename = f.getName();
		String msg = Messages.NewWizardBase_1;
		msg = MessageFormat.format(msg, filename);

		monitor.beginTask(msg, 2);
		FileHelper.save(f, props, true, monitor);
		monitor.worked(1);
		monitor.setTaskName(Messages.NewWizardBase_2);
		getShell().getDisplay().asyncExec(new Runnable() {
			public void run() {
				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				try {
					IDE.openEditor(page, f, true);
				} catch (PartInitException e) {
					Activator.logException(e);
				}
			}
		});
		monitor.worked(1);
	}

}
