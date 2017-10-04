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
// Module Name : com.ibm.iis.sappack.gen.tools.sap.importwizard
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.iis.sappack.gen.tools.sap.importwizard;


import org.eclipse.jface.wizard.IWizard;

import com.ibm.iis.sappack.gen.common.ui.menus.WizardHandlerBase;
import com.ibm.is.sappack.gen.common.ui.util.Utilities;


public class RMHandler extends WizardHandlerBase {

	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	

	public RMHandler() {
		super();
	}

	@Override
	protected boolean initialize() {
		boolean isJCoInstalledProperly = Utilities.checkJCoAvailabilityWithDialog();
		if (!isJCoInstalledProperly) {
			return false;
		}
		return true;
	}

	@Override
	protected IWizard createWizard() {
		return new MetaDataImportWizard();
	}

}