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


import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.NewWizardAction;

import com.ibm.iis.sappack.gen.common.ui.newwizards.NewWizardBase;
import com.ibm.iis.sappack.gen.common.ui.util.ImageProvider;
import com.ibm.iis.sappack.gen.tools.sap.idocseglist.NewIDocSegmentListWizard;
import com.ibm.iis.sappack.gen.tools.sap.rmconf.NewRMConfWizard;
import com.ibm.iis.sappack.gen.tools.sap.tablelist.NewTableListWizard;
import com.ibm.is.sappack.gen.common.ui.Messages;


public class RMActionProvider extends ActionProviderBase {
  	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	

	public RMActionProvider() {
		super();
	}


	protected List<Action> createActions(ProjectCategory pc) {
		IProject p = pc.getProject();
		List<Action> actions = new ArrayList<Action>();
		String name = pc.getName();
		if (name.equals(NavigatorRMContentProvider.IDOC_SEGMENT_LIST_CAT)) {
			actions.add(createNewIDocSegmentListAction(p));
		} else if (name.equals(NavigatorRMContentProvider.TABLE_LIST_CAT)) {
			actions.add(createNewTableListAction(p));			
		} else if (name.equals(NavigatorRMContentProvider.SAP_OBJECT_LIST_CAT)) {
			actions.add(createNewTableListAction(p));			
			actions.add(createNewIDocSegmentListAction(p));			
		} else if (name.equals(NavigatorRMContentProvider.RMCFG_CAT)) {
			actions.add(createNewRMConfAction(p));			
		} else if (name.equals(NavigatorRMContentProvider.RM_CAT)) {
			actions.add(createNewTableListAction(p));			
			actions.add(createNewIDocSegmentListAction(p));			
			actions.add(createNewRMConfAction(p));			
		} else if (name.equals(NavigatorRMContentProvider.LDM_CAT)) {
		//	actions.add(createNewLDMAction(p));
			actions.add(createNewOtherAction());
		} else if (name.equals(NavigatorRMContentProvider.DBM_CAT)) {
	//		actions.add(createNewDBMAction(p));
			actions.add(createNewOtherAction());
		} else if (name.equals(NavigatorRMContentProvider.MODELS_CAT)) {
//			actions.add(createNewLDMAction(p));
//			actions.add(createNewDBMAction(p));			
			actions.add(createNewOtherAction());
		} 
		
		return actions;
	}


	
	private Action createNewOtherAction() {
		NewWizardAction act = new NewWizardAction(PlatformUI.getWorkbench().getActiveWorkbenchWindow());
		act.setCategoryId("org.eclipse.wst.rdb"); //$NON-NLS-1$
		act.setText(Messages.RMActionProvider_0);
		return act;
		
	}

	private Action createNewTableListAction(IProject p) {
		final NewWizardBase fWizard = new NewTableListWizard();
		fWizard.setPreselectedProject(p.getName());
		Action act = new Action() {

			@Override
			public void run() {
				WizardDialog dialog = new WizardDialog(null, fWizard);
				dialog.open();
			}

		};
		act.setText(Messages.RMActionProvider_1);
		act.setImageDescriptor(ImageProvider.getImageDescriptor(ImageProvider.IMAGE_ABAP_TABLES_16));
		return act;
	}

	private Action createNewRMConfAction(IProject p) {
		final NewWizardBase fWizard = new NewRMConfWizard();
		fWizard.setPreselectedProject(p.getName());
		Action act = new Action() {

			@Override
			public void run() {
				WizardDialog dialog = new WizardDialog(null, fWizard);
				dialog.open();
			}

		};
		act.setText(Messages.RMActionProvider_2);
		act.setImageDescriptor(ImageProvider.getImageDescriptor(ImageProvider.IMAGE_RMCONF_16));
		return act;
	}

	private Action createNewIDocSegmentListAction(IProject p) {
		final NewWizardBase fWizard = new NewIDocSegmentListWizard();
		fWizard.setPreselectedProject(p.getName());
		Action act = new Action() {

			@Override
			public void run() {
				WizardDialog dialog = new WizardDialog(null, fWizard);
				dialog.open();
			}

		};
		act.setText(Messages.RMActionProvider_3);
		act.setImageDescriptor(ImageProvider.getImageDescriptor(ImageProvider.IMAGE_IDOCS_16));
		return act;
	}
}
