//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2013, 2014                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.iis.sappack.gen.common.ui.connections
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.iis.sappack.gen.common.ui.connections;


import org.eclipse.jface.wizard.Wizard;

import com.ibm.is.sappack.gen.common.ui.Messages;


public class EditCWDBConnectionWizard extends Wizard {
	private EditCWDBConnectionWizardPage editPage;
	

  	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	


	public EditCWDBConnectionWizard(String connectionName) {
		this.setWindowTitle(Messages.EditCWDBConnectionWizard_0);
		this.editPage = new EditCWDBConnectionWizardPage(connectionName);
		addPage(this.editPage);
	}

	@Override
	public boolean performFinish() {
		this.editPage.save();
		return true;
	}
	


}
