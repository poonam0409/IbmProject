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
// Module Name : com.ibm.is.sappack.gen.common.ui
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.common.ui;

import java.text.MessageFormat;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

public abstract class ModeBasedWizardActionProxy implements IObjectActionDelegate {
	static String copyright() {
		return Copyright.IBM_COPYRIGHT_SHORT;
	}
	
	private IDialogSettings dialogSettings;

	protected abstract IObjectActionDelegate getCurrentAction(RMRGMode mode, IDialogSettings dialogSettings);

	RMRGMode getMode() {
		return ModeManager.getActiveMode();
	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		IObjectActionDelegate act = getCurrentAction(getMode(), this.dialogSettings);
		if (act != null) {
			act.setActivePart(action, targetPart);
		}
	}
	
	public void setDialogSettings(IDialogSettings dialogSettings) {
		this.dialogSettings = dialogSettings;
	}

	@Override
	public void run(IAction action) {
		IObjectActionDelegate act = getCurrentAction(getMode(), this.dialogSettings);
		if (act != null) {
			act.run(action);
		} else {
			String title = Messages.WizardProxy_0;
			String msg = Messages.WizardProxy_1;
			msg = MessageFormat.format(msg, ModeManager.getActiveMode().getName());
			MessageDialog.openError(null, title, msg);
		}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		IObjectActionDelegate act = getCurrentAction(getMode(), this.dialogSettings);
		if (act != null) {
			act.selectionChanged(action, selection);
		}
	}

}
