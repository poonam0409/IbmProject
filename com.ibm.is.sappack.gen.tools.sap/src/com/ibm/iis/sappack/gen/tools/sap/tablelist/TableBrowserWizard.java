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
// Module Name : com.ibm.iis.sappack.gen.tools.sap.tablelist
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.iis.sappack.gen.tools.sap.tablelist;


import java.util.Set;

import org.eclipse.jface.wizard.Wizard;

import com.ibm.iis.sappack.gen.common.ui.wizards.SAPSystemSelectionWizardPage;
import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.tools.sap.activator.Activator;


public class TableBrowserWizard extends Wizard {
	private SAPSystemSelectionWizardPage sapSystemPage;
	private TableBrowserWizardPage tableWizardPage;
	private Set<String> selectedTables = null;


  	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}


	public TableBrowserWizard() {
		this.setWindowTitle(Messages.TableBrowserWizard_0);
		this.setDialogSettings(Activator.getDefault().getDialogSettings());
	}
	
	@Override
	public void addPages() {
		this.sapSystemPage = new SAPSystemSelectionWizardPage();
		this.tableWizardPage = new TableBrowserWizardPage(this.sapSystemPage);
		addPage(this.sapSystemPage);
		addPage(this.tableWizardPage);

	}

	public Set<String> getSelectedTables() {
		return this.selectedTables;
	}

	@Override
	public boolean performFinish() {
		this.selectedTables = this.tableWizardPage.getSelectedTables();
		return true;
	}

}
