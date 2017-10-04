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


import org.eclipse.jface.action.Action;
import org.eclipse.ui.cheatsheets.ICheatSheetAction;
import org.eclipse.ui.cheatsheets.ICheatSheetManager;

import com.ibm.is.sappack.gen.tools.sap.activator.Activator;


public class JobgeneratorCheatSheetAction extends Action implements ICheatSheetAction {
  	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	

	@Override
	public void run(String[] params, ICheatSheetManager manager) {

		RGHandler handler = new RGHandler();
		boolean result = false;
		try {
			handler.execute(null);
			result = true;
		} catch (Exception exc) {
			Activator.logException(exc);
			result = false;
		}
		this.notifyResult(result);
	}

}
