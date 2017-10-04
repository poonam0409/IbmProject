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
// Module Name : com.ibm.iis.sappack.gen.tools.jobgenerator.jobgenwizard
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.iis.sappack.gen.tools.jobgenerator.jobgenwizard;


import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.ibm.iis.sappack.gen.common.ui.wizards.PersistentWizardPageBase;
import com.ibm.iis.sappack.gen.common.ui.wizards.ResourceSelectionWidget;
import com.ibm.is.sappack.gen.common.ui.Messages;


public class RGResourceSelectionPage extends PersistentWizardPageBase {
	private static final String PFX = "RGResourceSelectionPage"; //$NON-NLS-1$
	
	private ResourceSelectionWidget rgConfig;
//	private ResourceSelectionWidget sourceModel;
//	private ResourceSelectionWidget targetModel;


  	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}


  	protected RGResourceSelectionPage() {
		super("rgresourceselectionpage", Messages.RGResourceSelectionPage_0, null); //$NON-NLS-1$
		this.setDescription(Messages.RGResourceSelectionPage_1);
	}

	@Override
	protected Composite createControlImpl(Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayout(new GridLayout(1, false));
		
		rgConfig = new ResourceSelectionWidget(comp, SWT.NONE, null, Messages.RGResourceSelectionPage_2, ".dbm", true, null); //$NON-NLS-1$
		this.configureTextForProperty(rgConfig.getText(), PFX + ".rgconfig"); //$NON-NLS-1$
		
		return comp;
	}

	@Override
	public boolean nextPressedImpl() {
		return false;
	}
	

	

}
