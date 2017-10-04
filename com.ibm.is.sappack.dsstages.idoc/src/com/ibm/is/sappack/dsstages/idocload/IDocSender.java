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
// Module Name : com.ibm.is.sappack.dsstages.idocload
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.dsstages.idocload;

import java.util.Map;

import com.ibm.is.sappack.dsstages.common.DSEnvironment;
import com.ibm.is.sappack.dsstages.common.ccf.IDocConnection;
import com.sap.conn.jco.JCoException;

public interface IDocSender {

	static final String IBM_COPYRIGHT_SHORT = Copyright.IBM_COPYRIGHT_SHORT;

	void initialize(IDocConnection connection, Map<String,String> stageProperties, DSEnvironment dsEnvironment) throws JCoException;
	
	void sendIDocs(SegmentCollector segmentCollector) throws JCoException;
	
}
