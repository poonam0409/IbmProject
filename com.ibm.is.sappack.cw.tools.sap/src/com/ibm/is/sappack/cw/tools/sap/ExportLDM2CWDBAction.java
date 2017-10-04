package com.ibm.is.sappack.cw.tools.sap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import com.ibm.iis.sappack.gen.common.ui.wizards.NextActionWizardDialog;

public class ExportLDM2CWDBAction implements IObjectActionDelegate {
	static String copyright()
	{ return com.ibm.is.sappack.cw.tools.sap.Copyright.IBM_COPYRIGHT_SHORT; }

	private ISelection selection;

	public void setActivePart(IAction arg0, IWorkbenchPart arg1) {
		// Nothing to be done here
	}

	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
	}

	public void run(IAction arg0) {
		if (this.selection == null || this.selection.isEmpty()) {
			return;
		}

		if (this.selection instanceof IStructuredSelection) {
			// Iterate over all selected files
			IStructuredSelection structuredSelection = ((IStructuredSelection) this.selection);
			Iterator<?> it = structuredSelection.iterator();
			List<IFile> ldmFiles = new ArrayList<IFile>();
			while (it.hasNext()) {
				Object o = it.next();
				if (o instanceof IFile) {
					ldmFiles.add( (IFile) o);
				}
			}
			
			NextActionWizardDialog dialog = new NextActionWizardDialog(null, new ExportLDMToCWDBWizard(ldmFiles));
			dialog.open();
		}
	}

}
