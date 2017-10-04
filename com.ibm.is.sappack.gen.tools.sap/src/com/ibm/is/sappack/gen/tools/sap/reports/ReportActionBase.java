//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2009, 2012                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.is.sappack.gen.tools.sap.reports
//                                                                             
//*************************-END OF SPECIFICATIONS-**************************
package com.ibm.is.sappack.gen.tools.sap.reports;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import com.ibm.db.models.logical.Entity;
import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.tools.sap.activator.Activator;
import com.ibm.is.sappack.gen.tools.sap.importer.helper.model.LdmAccessor;
import com.ibm.is.sappack.gen.tools.sap.utilities.ExceptionHandler;

public abstract class ReportActionBase implements IObjectActionDelegate {

	static String copyright() {
		return Copyright.IBM_COPYRIGHT_SHORT;
	}

	private ISelection selection;

	protected ReportActionBase() {
	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}

	@Override
	public void run(IAction action) {
		if (this.selection instanceof IStructuredSelection) {
			// Iterate over all selected files
			IStructuredSelection structuredSelection = ((IStructuredSelection) this.selection);
			List<Entity> entities = new ArrayList<Entity>();
			Iterator<?> it = structuredSelection.iterator();
			while (it.hasNext()) {
				Object o = it.next();
				if (o instanceof Entity) {
					entities.add((Entity) o);
				} else if (o instanceof IFile) {
					IFile f = (IFile) o;
					try {
						LdmAccessor acc = new LdmAccessor(f, null);
						entities.addAll(acc.getAllEntities());
					} catch (IOException exc) {
						Activator.getLogger().log(Level.SEVERE, Messages.LogExceptionMessage, exc);
						ExceptionHandler.handleException(exc, null);
						return;
					}
				}
			}
			Wizard wizard = createWizard(entities);
			WizardDialog wd = new WizardDialog(null, wizard);
			wd.open();
		}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
	}

	protected abstract Wizard createWizard(List<Entity> entities);

}
