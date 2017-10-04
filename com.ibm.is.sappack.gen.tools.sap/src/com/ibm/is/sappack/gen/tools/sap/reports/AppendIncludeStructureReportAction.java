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

import java.util.List;

import org.eclipse.jface.wizard.Wizard;

import com.ibm.db.models.logical.Entity;

public class AppendIncludeStructureReportAction extends ReportActionBase {

	static String copyright() {
		return Copyright.IBM_COPYRIGHT_SHORT;
	}

	@Override
	protected Wizard createWizard(List<Entity> entities) {
		return new LDMReportWizard(entities, new AppendIncludeStructureReport()) {

			@Override
			protected ReportOptionsPageBase createReportOptionsPage() {
				return new AppendIncludeReportOptionsPage();
			}

		};
	}

}
