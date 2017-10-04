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
// Module Name : com.ibm.iis.sappack.gen.tools.sap.views.dependency
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.iis.sappack.gen.tools.sap.views.dependency;


import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.ibm.iis.sappack.gen.common.ui.connections.SapSystem;
import com.ibm.is.sappack.gen.tools.sap.activator.Activator;


public class ShowInDependencyViewAction implements IObjectActionDelegate {
	private Object object;


	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	
  	
	public ShowInDependencyViewAction() {
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		final String viewID = Activator.PLUGIN_ID + ".dependencyView"; //$NON-NLS-1$
		IViewPart v = null;
		try {
			v = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(viewID);
		} catch (PartInitException e) {
			Activator.logException(e);
			e.printStackTrace();
			v = null;
		}
		if (v instanceof DependencyView) {
			DependencyView depView = (DependencyView) v;
			Object o = object;
			if (o instanceof SapSystem) {
				o = new DependencyTreeFactory.SAPConnectionStub( ((SapSystem) o).getFullName() );
			}
			depView.setFocusOnObject(o);
		}
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection ssel = (IStructuredSelection) selection;
			object = ssel.getFirstElement();
		}
	}

}
