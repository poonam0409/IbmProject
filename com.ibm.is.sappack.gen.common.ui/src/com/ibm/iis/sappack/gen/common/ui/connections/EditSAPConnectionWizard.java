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


public class EditSAPConnectionWizard extends Wizard {
  	private EditSAPConnectionWizardPage sapConnPage;


  	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}


	public EditSAPConnectionWizard(SAPConnectionRepository rep, String existingName) {
		this.sapConnPage = new EditSAPConnectionWizardPage(rep, existingName);
		addPage(this.sapConnPage);
	}

	@Override
	public boolean performFinish() {
		this.sapConnPage.commitChanges();
		return true;
	}

}