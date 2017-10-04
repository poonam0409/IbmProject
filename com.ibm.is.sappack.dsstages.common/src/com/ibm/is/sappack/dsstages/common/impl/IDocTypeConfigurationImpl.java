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

import com.ibm.is.sappack.dsstages.common.IDocExtractConfiguration;
import com.ibm.is.sappack.dsstages.common.IDocLoadConfiguration;
import com.ibm.is.sappack.dsstages.common.IDocTypeConfiguration;

public class IDocTypeConfigurationImpl implements IDocTypeConfiguration {

	static String copyright() {
		return com.ibm.is.sappack.dsstages.common.impl.Copyright.IBM_COPYRIGHT_SHORT;
	}

	String idocTypeName;
	IDocLoadConfiguration loadConfig;
	IDocExtractConfiguration extractConfig;
	String release;
	
	public IDocExtractConfiguration getExtractConfiguration() {
		return this.extractConfig;
	}
	
	public IDocLoadConfiguration getLoadConfiguration() {
		return this.loadConfig;
	}

	public String getIDocTypeName() {
		return this.idocTypeName;
	}
	
	public String getRelease() {
		return this.release;
	}

}
