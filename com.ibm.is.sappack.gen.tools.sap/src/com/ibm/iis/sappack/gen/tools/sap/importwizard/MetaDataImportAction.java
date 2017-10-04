//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2011, 2014                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.iis.sappack.gen.tools.sap.importwizard
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.iis.sappack.gen.tools.sap.importwizard;


import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import com.ibm.iis.sappack.gen.common.ui.menus.WizardHandlerBase;
import com.ibm.is.sappack.gen.common.ui.util.Utilities;
import com.ibm.is.sappack.gen.tools.sap.activator.Activator;


public class MetaDataImportAction implements IObjectActionDelegate {

	private ISelection selection;
	

	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}

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
			boolean isJCoInstalledProperly = Utilities.checkJCoAvailabilityWithDialog();
			if (!isJCoInstalledProperly) {
				return;
			}

			IStructuredSelection structuredSelection = ((IStructuredSelection) this.selection);
			IFile file = (IFile) structuredSelection.getFirstElement();
			final String fileName = file.getFullPath().toString();
			WizardHandlerBase handler = new WizardHandlerBase() {
				
				@Override
				protected IWizard createWizard() {
					return new MetaDataImportWizard(fileName);
				}
			};

			try {
				handler.execute(null);
			} catch (ExecutionException e) {
				Activator.logException(e);
			}
		}
	}

}
