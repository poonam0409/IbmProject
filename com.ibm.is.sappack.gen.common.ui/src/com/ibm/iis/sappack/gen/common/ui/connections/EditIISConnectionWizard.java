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


public class EditIISConnectionWizard extends Wizard {
	private String                     connectionName;
	private EditIISConnectionWizardPage editConnPage;

  	
	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}


  	public EditIISConnectionWizard(String connectionName) {
		this.connectionName = connectionName;
	}

	@Override
	public void addPages() {
		editConnPage = new EditIISConnectionWizardPage(this.connectionName);
		addPage(editConnPage);
	}

	@Override
	public boolean performFinish() {
		editConnPage.save();
		return true;
	}

}
