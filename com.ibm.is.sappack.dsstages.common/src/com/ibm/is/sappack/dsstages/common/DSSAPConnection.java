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
// Module Name : com.ibm.is.sappack.dsstages.common
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.dsstages.common;

import java.io.IOException;
import java.util.List;

import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;

public interface DSSAPConnection {

	static final String IBM_COPYRIGHT_SHORT = Copyright.IBM_COPYRIGHT_SHORT;

	void initialize(String dsSAPConnectionName) throws IOException;

	List<IDocTypeConfiguration> getIDocTypeConfigurations();

	SapSystem getSapSystem();
	
	JCoDestination createJCODestinationWithConnectionDefaults() throws JCoException;
}
