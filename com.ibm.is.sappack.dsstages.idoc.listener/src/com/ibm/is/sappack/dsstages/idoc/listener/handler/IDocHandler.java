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
// Module Name : com.ibm.is.sappack.dsstages.idoc.listener.handler
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.dsstages.idoc.listener.handler;

import java.util.Map;

import com.ibm.is.sappack.dsstages.idoc.listener.util.TransactionState;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoFunction;

/**
 * IDocHandler
 * 
 * Interface to process
 * incoming IDocs from SAP
 */
public interface IDocHandler {
	
	static final String IBM_COPYRIGHT_SHORT = Copyright.IBM_COPYRIGHT_SHORT;

	/**
	 * processIDocs
	 * 
	 * process incoming IDocs from SAP
	 * 
	 * @param jcoFunction
	 * @param connectionName
	 */
	public void processIDocs(String tid,JCoFunction jcoFunction, String connectionName, String filePath);
	
	/**
	 * commit transaction 
	 */
	public void commit();
	
	/**
	 * rollback transaction
	 */
	public void rollback();
	
	/**
	 * getTransactionState
	 * 
	 * return the transaction state
	 * 
	 * @return
	 */
	public TransactionState getTransactionState();
	
	/**
	 * confirmTransaction
	 * 
	 * Send confirmation IDocs for all received
	 * and committed IDocs and start DataStage 
	 * jobs if necessary
	 *
	 * @param idocServerProperties
	 * @param destination
	 */
	public void confirmTransaction(Map<String, String> idocServerProperties, JCoDestination destination);
	
}
