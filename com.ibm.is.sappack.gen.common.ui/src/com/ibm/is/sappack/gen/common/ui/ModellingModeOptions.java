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
// Module Name : com.ibm.is.sappack.gen.common.ui
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.common.ui;


import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;


public class ModellingModeOptions extends ModeOptionsBase {

	static String copyright() {
		return Copyright.IBM_COPYRIGHT_SHORT;
	}

	@Override
	public Composite createControls(Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayout(new GridLayout(1, true));
		comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));		
		createNoModeOptionsLabel(comp);

		return comp;
	}

	@Override
	public void saveToPrefStore(IPreferenceStore prefStore) {
		;
	}

	@Override
	public void loadFromPrefStore(IPreferenceStore prefStore) {
		;
	}

}
