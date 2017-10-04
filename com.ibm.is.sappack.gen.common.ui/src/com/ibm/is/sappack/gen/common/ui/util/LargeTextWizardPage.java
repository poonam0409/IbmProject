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
// Module Name : com.ibm.is.sappack.gen.common.ui.util
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.common.ui.util;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import com.ibm.is.sappack.gen.common.ui.Messages;

public class LargeTextWizardPage extends WizardPage {
	static String copyright() {
		return Copyright.IBM_COPYRIGHT_SHORT;
	}

	private Text text;

	private String message;


	public LargeTextWizardPage(String title, String description, String message) {
		super(Messages.SummaryWizardPage_0);
		setTitle(title);
		setDescription(description);
		this.message = message;
	}

	@Override
	public void createControl(final Composite parent) {
		Composite mainComposite = new Composite(parent, SWT.NONE);

		mainComposite.setLayout(new GridLayout(1, false));
		text = new Text(mainComposite, SWT.READ_ONLY | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		text.setLayoutData(new GridData(GridData.FILL_BOTH));
		setControl(mainComposite);
		this.text.setText(message);
	}

}
