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
// Module Name : com.ibm.is.sappack.gen.common.ui.preferences
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.common.ui.preferences;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class SAPPacksWorkbenchPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	static String copyright()
    { return com.ibm.is.sappack.gen.common.ui.preferences.Copyright.IBM_COPYRIGHT_SHORT; }
	
	public SAPPacksWorkbenchPreferencePage() {
		// TODO Auto-generated constructor stub
	}

	public SAPPacksWorkbenchPreferencePage(String title) {
		super(title);
		// TODO Auto-generated constructor stub
	}

	public SAPPacksWorkbenchPreferencePage(String title, ImageDescriptor image) {
		super(title, image);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Control createContents(Composite parent) {
		// add dummy composite to grab all the space
		Composite comp = new Composite(parent, SWT.NONE); 
		comp.setLayout(new GridLayout());
		comp.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL | GridData.FILL_BOTH));
		return comp;
	}

	@Override
	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub

	}

}
