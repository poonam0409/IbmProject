//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2009, 2014                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.iis.sappack.gen.tools.sap.rmconf
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.iis.sappack.gen.tools.sap.rmconf;


import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.ibm.iis.sappack.gen.common.ui.editors.GeneralPageBase;
import com.ibm.iis.sappack.gen.common.ui.editors.IControlFactory;
import com.ibm.iis.sappack.gen.common.ui.util.ImageProvider;
import com.ibm.iis.sappack.gen.common.ui.util.Utils;
import com.ibm.is.sappack.gen.common.ui.Messages;


public class RMConfigurationGeneralPage extends GeneralPageBase {
  	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	

	public RMConfigurationGeneralPage() {
		super(Messages.RMConfigurationGeneralPage_0, Messages.RMConfigurationGeneralPage_1, Messages.RMConfigurationGeneralPage_2,
			   Utils.getHelpID("rmconfeditor_general_sappack")); //$NON-NLS-1$
	}

	@Override
	public void createControls(IControlFactory controlFactory, Composite composite) {
		super.createControls(controlFactory, composite);
		Composite pageComp = this.getPageComposite();
		Composite group = controlFactory.createGroup(pageComp, Messages.RMConfigurationGeneralPage_3, SWT.NONE);
		Label l = controlFactory.createLabel(group, SWT.NONE);
		l.setText(Messages.RMConfigurationGeneralPage_4);
	
	}

	@Override
	public Image getImage() {
		return ImageProvider.getImage(ImageProvider.IMAGE_RMCONF_16);
	}
	
	
}
