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


public class CWDBSAPSystem extends SapSystem {

	public static final String CWDBPREFIX     = "cwdb:"; //$NON-NLS-1$
	public static final String CWDB_SEPARATOR = "/"; //$NON-NLS-1$
	
	private String cwdbName;


	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	


	public CWDBSAPSystem(String cwdbName, String legacyID) {
		super(legacyID);
		this.cwdbName = cwdbName;
	}

	@Override
	public String getFullName() {
		return CWDBPREFIX + cwdbName + CWDB_SEPARATOR + this.name;
	}
}
