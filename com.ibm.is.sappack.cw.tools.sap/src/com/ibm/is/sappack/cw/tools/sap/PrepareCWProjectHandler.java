package com.ibm.is.sappack.cw.tools.sap;

import org.eclipse.jface.wizard.IWizard;

import com.ibm.iis.sappack.gen.common.ui.menus.WizardHandlerBase;

public class PrepareCWProjectHandler extends WizardHandlerBase {

	public PrepareCWProjectHandler() {
		super();
	}

	@Override
	protected IWizard createWizard() {
		return new PrepareCWProjectWizard();
	}

}
