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
// Module Name : com.ibm.is.sappack.dsstages.idoc.listener.statussender
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.dsstages.idoc.listener.statussender;

import java.io.IOException;
import java.util.List;

import com.ibm.is.sappack.dsstages.idoc.listener.handler.IDocFile;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;

/**
 * StatusIDocSender
 * 
 * Interface to send status
 * IDoc to SAP after receiving
 * IDocs from SAP
 */
public interface StatusIDocSender {

	static final String IBM_COPYRIGHT_SHORT = Copyright.IBM_COPYRIGHT_SHORT;

	/**
	 * initialize
	 * 
	 * initialize the StatusIDocSender with the
	 * given JCoDestination and SAP system name
	 * 
	 * @param destination
	 * @param sapSystemName
	 * @throws JCoException
	 * @throws IOException
	 */
	public void initialize(JCoDestination destination, String sapSystemName) throws JCoException, IOException;
	
	/**
	 * sendStatusIDocs
	 * 
	 * send status IDocs to SAP for the
	 * IDocs that are contained in the given
	 * List of IDocFiles
	 * 
	 * @param idocFiles
	 */
	public void sendStatusIDocs(List<IDocFile> idocFiles) throws JCoException;
}
