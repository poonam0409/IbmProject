//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2014                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.iis.sappack.gen.tools.jobgenerator.jobgenwizard
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.iis.sappack.gen.tools.jobgenerator.jobgenwizard;


import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import com.ibm.iis.sappack.gen.common.ui.menus.WizardHandlerBase;
import com.ibm.is.sappack.gen.tools.sap.activator.Activator;


public class JobGeneratorAction implements IObjectActionDelegate {
  	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	

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
			Object o = structuredSelection.getFirstElement();
			if (!(o instanceof IFile)) {
				return;
			}
			IFile f = (IFile) o;
			final String dbmName = f.getFullPath().toString();
			WizardHandlerBase handler = new WizardHandlerBase() {
				
				@Override
				protected IWizard createWizard() {
					return new JobGenerationWizard(dbmName);
				}
			};
			try {
				handler.execute(null);
			} catch (ExecutionException e) {
				Activator.logException(e);
			} catch (Exception e) {
				Activator.logException(e);
			}
		}
	}
	
	//protected abstract WizardHandlerBase createHandler(IStructuredSelection selection);

}
