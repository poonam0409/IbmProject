//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2009, 2012                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.is.sappack.gen.tools.sap.reports
//                                                                             
//*************************-END OF SPECIFICATIONS-**************************
package com.ibm.is.sappack.gen.tools.sap.reports;

import java.util.Map;

import org.eclipse.jface.wizard.WizardPage;

public abstract class ReportOptionsPageBase extends WizardPage {
	static String copyright() {
		return Copyright.IBM_COPYRIGHT_SHORT;
	}

	protected ReportOptionsPageBase(String pageName) {
		super(pageName);
	}

	public abstract Map<String, String> getReportOptions();

}
