package com.ibm.is.sappack.cw.tools.sap;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import com.ibm.iis.sappack.gen.common.ui.util.Utils;
import com.ibm.is.sappack.gen.common.ui.Messages;

public class ExportSummaryWizardPage extends WizardPage {
	static String copyright()
	{ return com.ibm.is.sappack.cw.tools.sap.Copyright.IBM_COPYRIGHT_SHORT; }

	protected ExportSummaryWizardPage(String pageName) {
		super(pageName);
		this.setTitle(Messages.ExportSummaryWizardPage_0);
		this.setDescription(Messages.ExportSummaryWizardPage_1);
	}

	protected Text text;

	@Override
	public void createControl(final Composite parent) {
		Composite mainComposite = new Composite(parent, SWT.NONE);

		mainComposite.setLayout(new GridLayout(1, false));
		text = new Text(mainComposite, SWT.READ_ONLY | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		text.setLayoutData(new GridData(GridData.FILL_BOTH));
		update();
		this.setControl(mainComposite);
	}
	
	public void setText(String s) {
		text.setText(s);
		update();
	}
	
	void update() {
		boolean complete = true;
		if (Utils.getText(text) == null) {
			complete = false;
		}
		setPageComplete(complete);
	}
}
