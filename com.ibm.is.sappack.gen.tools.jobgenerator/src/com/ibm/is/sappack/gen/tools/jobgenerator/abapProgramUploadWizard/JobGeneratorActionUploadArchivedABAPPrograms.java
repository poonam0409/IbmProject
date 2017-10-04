package com.ibm.is.sappack.gen.tools.jobgenerator.abapProgramUploadWizard;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.Vector;
import java.util.zip.ZipFile;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.common.ui.util.Utilities;

public class JobGeneratorActionUploadArchivedABAPPrograms implements IObjectActionDelegate {

	private ISelection selection;

	static String copyright() {
		return Copyright.IBM_COPYRIGHT_SHORT;
	}
	
	@Override
	public void run(IAction action) {
		
	    boolean isJCoInstalledProperly = Utilities.checkJCoAvailabilityWithDialog();
	    if (!isJCoInstalledProperly) {
	       return;
	    }

		if (selection instanceof IStructuredSelection) {
			//TODO maybe allow multiple zip file selection for convenience SKABISCH
			Iterator<?> selectionIterator = ((IStructuredSelection) selection).iterator();
			
			Vector<ZipFile> zipFiles = new Vector<ZipFile>();
				
			try {
				while (selectionIterator.hasNext()) {
					IFile file = (IFile) selectionIterator.next();
					//open the zip file, get the content to check whether File is valid and add to zipFiles List
					ZipFile currentZipFile = new ZipFile(file.getLocation().toString());
					//if the zip file cration worked add it to the list
					zipFiles.add(currentZipFile);
				}
				
				// open the wizard if everything worked fine
				ABAPProgramUploadWizard wizard = new ABAPProgramUploadWizard(zipFiles);				
				WizardDialog dialog = new ABAPProgramUploadWizardDialog(Display.getCurrent().getActiveShell(), wizard);
				dialog.open();
			} catch (IOException e) {
				Shell parentShell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
				MessageBox errorMessageBox = new MessageBox(parentShell, SWT.ICON_ERROR | SWT.OK);
				errorMessageBox.setMessage(MessageFormat.format(Messages.ABAPProgramUploadWizard_4, e.getMessage()));
				errorMessageBox.setText(Messages.TitleError);
				errorMessageBox.open();
			}
				

		}
		
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {		
	}
}
