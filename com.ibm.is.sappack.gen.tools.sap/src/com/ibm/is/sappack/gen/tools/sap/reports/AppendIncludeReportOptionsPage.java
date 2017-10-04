//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2009, 2012                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.is.sappack.gen.tools.sap.reports
//                                                                             
//*************************-END OF SPECIFICATIONS-**************************
package com.ibm.is.sappack.gen.tools.sap.reports;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.ibm.is.sappack.gen.common.ui.Messages;

public class AppendIncludeReportOptionsPage extends ReportOptionsPageBase {

	static String copyright() {
		return Copyright.IBM_COPYRIGHT_SHORT;
	}
	
	Text appendRegex;
	Text includeRegex;
	Combo operatorCombo;

	public AppendIncludeReportOptionsPage() {
		super("appendIncludeReportOptionsPage"); //$NON-NLS-1$
		setTitle(Messages.AppendIncludeReportOptionsPage_0);
		setDescription(Messages.AppendIncludeReportOptionsPage_1);
	}

	@Override
	public void createControl(Composite parent) {
		Composite mainComposite = new Composite(parent, SWT.NONE);
		mainComposite.setLayout(new GridLayout(2, false));
		mainComposite.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL | GridData.FILL_BOTH));

		ModifyListener textModifyListener = new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				update();
			}
		};

		Label regeExAppend = new Label(mainComposite, SWT.NONE);
		regeExAppend.setText(Messages.AppendIncludeReportOptionsPage_2);

		this.appendRegex = new Text(mainComposite, SWT.BORDER);
		this.appendRegex.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
		this.appendRegex.setText("(Z.*)|(Y.*)"); //$NON-NLS-1$
		this.appendRegex.addModifyListener(textModifyListener);

		Label l = new Label(mainComposite, SWT.NONE);
		l.setText(Messages.AppendIncludeReportOptionsPage_3);

		operatorCombo = new Combo(mainComposite, SWT.NONE);
		operatorCombo.setItems(new String[] { AppendIncludeStructureReport.OPERATOR_OR, AppendIncludeStructureReport.OPERATOR_AND });
		operatorCombo.select(0);

		Label regeExInclude = new Label(mainComposite, SWT.NONE);
		regeExInclude.setText(Messages.AppendIncludeReportOptionsPage_4);

		this.includeRegex = new Text(mainComposite, SWT.BORDER);
		this.includeRegex.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
		this.includeRegex.setText("(CI_.*)|(Z.*)|(Y.*)"); //$NON-NLS-1$
		this.includeRegex.addModifyListener(textModifyListener);

		update();
		setControl(mainComposite);
	}

	private String checkPattern(String s) {
		try {
			Pattern.compile(s);
		} catch (PatternSyntaxException exc) {
			String msg = Messages.AppendIncludeReportOptionsPage_5;
			msg = MessageFormat.format(msg, new Object[] { s, exc.getMessage() });
			return msg;
		}
		return null;
	}

	private boolean checkRegex(Text t) {
		if (!t.getText().isEmpty()) {
			String appendError = checkPattern(t.getText());
			if (appendError != null) {
				setErrorMessage(appendError);
				return false;
			}
		}
		return true;
	}

	private void update() {
		boolean canProceed = true;
		setErrorMessage(null);
		if (canProceed) {
			canProceed = checkRegex(this.appendRegex);
		}
		if (canProceed) {
			canProceed = checkRegex(this.includeRegex);
		}
		setPageComplete(canProceed);
	}

	@Override
	public Map<String, String> getReportOptions() {
		Map<String, String> m = new HashMap<String, String>();
		m.put(AppendIncludeStructureReport.APPENDREGEX_OPTION, this.appendRegex.getText());
		m.put(AppendIncludeStructureReport.INCLUDEREGEX_OPTION, this.includeRegex.getText());
		m.put(AppendIncludeStructureReport.OPERATOR_OPTION, this.operatorCombo.getItem(this.operatorCombo.getSelectionIndex()));
		return m;
	}

}
