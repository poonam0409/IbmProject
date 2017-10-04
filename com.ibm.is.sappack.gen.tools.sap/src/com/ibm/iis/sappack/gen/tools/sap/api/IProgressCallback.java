//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2011, 2014                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.iis.sappack.gen.tools.sap.api
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.iis.sappack.gen.tools.sap.api;


/**
 * An object implementing this interface is used to respond to progress changes.
 * All methods (except {@link #setCanceled(boolean)}) are called by the framework.
 * 
 */
public interface IProgressCallback {

	public void beginTask(String name, int totalWork);

	/**
	 * Called by the framework if finished.
	 */
	public void done();

	public boolean isCanceled();

	/**
	 * This method can be called by the client directly to indicate the process to stop running.
	 */
	public void setCanceled(boolean value);

	public void setTaskName(String name);

	public void subTask(String name);

	public void worked(int work);

	/**
	 * Indicates that an exception has occurred that will lead the process to terminate.
	 */
	public void exceptionOccurred(Exception exc);
}
