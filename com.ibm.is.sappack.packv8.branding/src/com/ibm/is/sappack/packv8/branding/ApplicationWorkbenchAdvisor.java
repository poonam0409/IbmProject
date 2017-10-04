//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2009, 2011                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.is.sappack.gen.branding
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.packv8.branding;

import org.eclipse.ui.application.WorkbenchAdvisor;

public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {

	static String copyright()
	{ return com.ibm.is.sappack.packv8.branding.Copyright.IBM_COPYRIGHT_SHORT; }

	public ApplicationWorkbenchAdvisor() {
		super();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.application.WorkbenchAdvisor#getInitialWindowPerspectiveId()
	 */
	@Override
	public String getInitialWindowPerspectiveId() {
		return "com.ibm.datatools.core.internal.ui.perspective";
	}

}
