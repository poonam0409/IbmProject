//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2014                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.iis.sappack.gen.tools.sap.idocseglist
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.iis.sappack.gen.tools.sap.idocseglist;


import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.tools.sap.constants.Constants;


public class IDocReleaseSelectionWidget {

	private Combo versionCombo;


	static String copyright() {
		return Copyright.IBM_COPYRIGHT_SHORT;
	}


	public IDocReleaseSelectionWidget(Composite parent, boolean addComposite) {
		Composite versionComposite = parent;
		if (addComposite) {
			versionComposite = new Composite(parent, SWT.NULL);
			versionComposite.setLayout(new GridLayout(2, false));
		}

		Label l = new Label(versionComposite, SWT.NONE);
		l.setText(Messages.IDocReleaseSelectionWidget_0);

		versionCombo = new Combo(versionComposite, SWT.NONE);
		versionCombo.add(""); //$NON-NLS-1$
		versionCombo.add("700"); //$NON-NLS-1$
		versionCombo.add("640"); //$NON-NLS-1$
		versionCombo.add("620"); //$NON-NLS-1$
		versionCombo.add("46D"); //$NON-NLS-1$
		versionCombo.add("46C"); //$NON-NLS-1$
		versionCombo.add("46B"); //$NON-NLS-1$
		versionCombo.add("46A"); //$NON-NLS-1$
	}

	public String getRelease() {
		int ix = this.versionCombo.getSelectionIndex();
		if (ix == -1 || ix > this.versionCombo.getItemCount()) {
			String manuallyEnteredText = this.versionCombo.getText();
			if (manuallyEnteredText == null) {
				manuallyEnteredText = Constants.IDOC_EMPTY_RELEASE;
			}
			return manuallyEnteredText;
		}
		return this.versionCombo.getItem(ix);
	}

	public void setRelease(String release) {
		for (int i = 0; i < versionCombo.getItemCount(); i++) {
			if (versionCombo.getItem(i).equals(release)) {
				versionCombo.select(i);
				return;
			}
		}
		versionCombo.setText(release);
	}

	public void addListener(Listener l) {
		this.versionCombo.addListener(SWT.Selection, l);
		this.versionCombo.addListener(SWT.KeyDown, l);
	}

}
