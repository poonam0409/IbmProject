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
// Module Name : com.ibm.is.sappack.dsstages.common.impl
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.dsstages.common.impl;

import com.ibm.is.sappack.dsstages.common.IDocLoadConfiguration;

public class IDocLoadConfigurationImpl implements IDocLoadConfiguration {

	static String copyright() {
		return com.ibm.is.sappack.dsstages.common.impl.Copyright.IBM_COPYRIGHT_SHORT;
	}

	String connectionPartnerNumber;
	String sapPartnerNumber;
	String loadDataDirectory;
	String fileBasedLoadDirectory;
	
	public String getConnectionPartnerNumber() {
		return this.connectionPartnerNumber;
	}

	public String getLoadDataDirectory() {
		return this.loadDataDirectory;
	}

	public String getSAPPartnerNumber() {
		return this.sapPartnerNumber;
	}
	
	public String getFileBasedLoadDirectory() {
		return this.fileBasedLoadDirectory;
	}

}
