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
// Module Name : com.ibm.iis.sappack.gen.tools.sap.views.navigator
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.iis.sappack.gen.tools.sap.views.navigator;


import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.eclipse.ui.navigator.ICommonMenuConstants;

import com.ibm.is.sappack.gen.common.ui.Messages;


public abstract class ActionProviderBase extends CommonActionProvider {

	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	

	public ActionProviderBase() {
	}

	public void init(ICommonActionExtensionSite anExtensionSite) {
	}

	public void fillContextMenu(IMenuManager menu) {
		ISelection sel = this.getContext().getSelection();
		if (!(sel instanceof StructuredSelection)) {
			return;
		}
		StructuredSelection ssel = (StructuredSelection) sel;
		Object o = ssel.getFirstElement();
		if (o instanceof IFile) {
			return;
		}

		if (o instanceof ProjectCategory) {

			IMenuManager submenu = new MenuManager(Messages.ActionProviderBase_0);

			submenu.add(new Separator());

			ProjectCategory pc = (ProjectCategory) o;
			List<Action> acts = createActions(pc);
			for (Action act : acts) {
				submenu.add(act);
			}

			menu.insertAfter(ICommonMenuConstants.GROUP_ADDITIONS, submenu);
		}
	}

	protected abstract List<Action> createActions(ProjectCategory pc);

}
