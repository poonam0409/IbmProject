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


import org.eclipse.jface.action.Action;
import org.eclipse.ui.cheatsheets.ICheatSheetAction;
import org.eclipse.ui.cheatsheets.ICheatSheetManager;

import com.ibm.is.sappack.gen.tools.sap.activator.Activator;


public class MetadataImportCheatSheetAction extends Action implements ICheatSheetAction {

  	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	


	@Override
	public void run(String[] params, ICheatSheetManager manager) {
		
		RMHandler handler = new RMHandler();
		boolean result = false;
		try {
		handler.execute(null);
		result = true;
		} catch(Exception exc) {
			Activator.logException(exc);
			result = false;
		}
		/*
		boolean result = false;
		String projectName = params[0];
		String ldmName = params[1];
		IWorkspaceRoot wsRoot = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = wsRoot.getProject(projectName);
		if (project.exists()) {
			IFile file = project.getFile(ldmName);
			if (file.exists()) {
				MetadataImportWizard wizard = new MetadataImportWizard(file);
				MetadataImportWizardDialog dialog = new MetadataImportWizardDialog(Display.getCurrent().getActiveShell(), wizard);
				int dres = dialog.open();
				result = (dres == Dialog.OK);
			} else {
				MessageDialog.openWarning(null, Messages.MetadataImportCheatSheetAction_0, MessageFormat.format(Messages.MetadataImportCheatSheetAction_1, ldmName));
			}
		} else {
			MessageDialog.openWarning(null, Messages.MetadataImportCheatSheetAction_2, MessageFormat.format(Messages.MetadataImportCheatSheetAction_3, projectName));
		}
		*/
		this.notifyResult(result);
	}

}
