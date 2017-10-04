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


import java.util.List;

import org.eclipse.jface.wizard.Wizard;

import com.ibm.iis.sappack.gen.common.ui.wizards.SAPSystemSelectionWizardPage;
import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.tools.sap.activator.Activator;
import com.ibm.is.sappack.gen.tools.sap.importer.idoctypes.IDocType;
import com.sap.conn.jco.JCoException;


public class IDocBrowserWizard extends Wizard {

  	private SAPSystemSelectionWizardPage sapSystemPage;

	private IDocBrowserWizardPage        idocWizardPage;

	private IDocType                     idocType;
	private List<String>                 idocSegments;
//	private String                       release;


  	static String copyright() { 
 	   return Copyright.IBM_COPYRIGHT_SHORT; 
 	}
  	
  	
	public IDocBrowserWizard(String idocTypName) {
		this.setWindowTitle(Messages.IDocBrowserWizard_0);
		this.setDialogSettings(Activator.getDefault().getDialogSettings());

		this.sapSystemPage  = new SAPSystemSelectionWizardPage();
		this.idocWizardPage = new IDocBrowserWizardPage(sapSystemPage, idocTypName);
		addPage(this.sapSystemPage);
		addPage(this.idocWizardPage);
	}

	public IDocType getIDOCType() {
		return idocType;
	}

	public List<String> getIDOCSegments() {
		return idocSegments;
	}

	@Override
	public boolean performFinish() {
		this.idocType = this.idocWizardPage.getSelectedIDocType();
		try {
			this.idocSegments = this.idocWizardPage.getSelectedSegments();
		} catch (JCoException e) {
			Activator.logException(e);
			return false;
		}
		return true;
	}

}
