package com.ibm.iis.sappack.gen.tools.jobgenerator.views.navigator;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.wizard.WizardDialog;

import com.ibm.iis.sappack.gen.common.ui.util.ImageProvider;
import com.ibm.iis.sappack.gen.tools.jobgenerator.rgconf.NewRGConfWizard;
import com.ibm.iis.sappack.gen.tools.sap.views.navigator.ActionProviderBase;
import com.ibm.iis.sappack.gen.tools.sap.views.navigator.ProjectCategory;
import com.ibm.is.sappack.gen.common.ui.Messages;

public class RGActionProvider extends ActionProviderBase {
  	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	

	@Override
	protected List<Action> createActions(ProjectCategory pc) {
		List<Action> actions = new ArrayList<Action>();
		IProject project = pc.getProject();
		String name = pc.getName();
		
		if (name.equals(NavigatorRGContentProvider.RGCAT_NAME)) {
			actions.add(createNewRGConfWizardAction(project));
		}
		return actions;
	}

	private Action createNewRGConfWizardAction(final IProject project) {
		Action act = new Action() {

			@Override
			public void run() {
				NewRGConfWizard wizard = new NewRGConfWizard();
				wizard.setPreselectedProject(project.getName());
				WizardDialog dialog = new WizardDialog(null, wizard);
				dialog.open();
				
			}

		};
		act.setText(Messages.RGActionProvider_0);
		act.setImageDescriptor(ImageProvider.getImageDescriptor(ImageProvider.IMAGE_RGCONF_16));
		return act;
	}

}
