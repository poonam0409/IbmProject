package com.ibm.iis.sappack.gen.common.ui.menus;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardDialog;

import com.ibm.iis.sappack.gen.common.ui.wizards.INextActionWizardPage;

public abstract class WizardHandlerBase extends AbstractHandler {
  	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	

	public WizardHandlerBase() {
		super();
	}

	protected abstract IWizard createWizard();
	
	protected boolean initialize() {
		return true;
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final IWizard wizard = createWizard();
		boolean initialized = initialize();
		if (!initialized) {
			return null;
		}
		WizardDialog dialog = new WizardDialog(null, wizard) {

			@Override
			protected void nextPressed() {
				boolean advancePage = true;
				IWizardPage page = this.getCurrentPage();
				if (page instanceof INextActionWizardPage) {
					advancePage = ((INextActionWizardPage) page).nextPressed();
				}
				if (advancePage) {
					super.nextPressed();
				}
			}

		};
		dialog.open();
		return null;
	}

}
