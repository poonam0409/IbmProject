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
// Module Name : com.ibm.is.sappack.dsstages.idoc.listener
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.dsstages.idoc.listener;

import com.ibm.is.sappack.dsstages.idoc.listener.util.IDocServerException;

/**
 * IDocServer 
 */
public interface IDocServer {

	static final String IBM_COPYRIGHT_SHORT = Copyright.IBM_COPYRIGHT_SHORT;
	
	/**
	 * initialize the IDocServer
	 * 
	 * @param args
	 * @throws IDocServerException
	 */
	public void initialize(String [] args) throws IDocServerException;
	
	/**
	 * start the IDocServer
	 * 
	 * @throws IDocServerException
	 */
	public void start() throws IDocServerException;
	
	/**
	 * stop the IDocServer
	 * 
	 * @throws IDocServerException
	 */
	public void stop() throws IDocServerException;
	
	/**
	 * isServerRunning
	 * 
	 * returns true if the server is running
	 * properly. Return false if the server
	 * has crashed or stopped
	 * 
	 * @return
	 */
	public boolean isServerRunning();
}
