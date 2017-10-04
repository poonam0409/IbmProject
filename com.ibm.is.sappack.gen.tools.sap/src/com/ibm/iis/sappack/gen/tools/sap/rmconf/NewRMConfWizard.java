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


import com.ibm.iis.sappack.gen.common.ui.newwizards.NewWizardBase;
import com.ibm.iis.sappack.gen.common.ui.newwizards.NewWizardPageBase;
import com.ibm.is.sappack.gen.common.ui.Messages;


public class NewRMConfWizard extends NewWizardBase {
  	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	

	public NewRMConfWizard() {
		super();
		this.setWindowTitle(Messages.NewRMConfWizard_0);
	}

	@Override
	protected NewWizardPageBase createWizardPage() {
		return new NewRMConfPage();
	}

}
