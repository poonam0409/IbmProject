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

import com.ibm.is.sappack.dsstages.idoc.listener.jobrunner.IDocCounter;

/**
 * IDocTypeConfiguration
 * 
 * Interface to store the configuration
 * of a specific IDoc type.
 */
public interface IDocTypeConfiguration {

	static final String IBM_COPYRIGHT_SHORT = Copyright.IBM_COPYRIGHT_SHORT;
	
	/**
	 * return the name of the IDoc type
	 * 
	 * @return
	 */
	public String getIDocType();
	
	/**
	 * return true if the job is configured
	 * to be run automatically if an IDoc
	 * is received
	 *  
	 * @return
	 */
	public boolean isAutorunEnabled();
		
	/**
	 * return true if a specific DataStage
	 * job is configured to be run when an IDoc
	 * is received. Return false if all DataStage
	 * jobs that extract IDocs of a specific type
	 * should be run when a new IDoc is received
	 * 
	 * @return
	 */
	public boolean isSelectJob();
	
	/**
	 * return the name of the job that is
	 * configured to be run if a new IDoc
	 * is received.
	 * 
	 * @return
	 */
	public String getJobName();
	
	/**
	 * return the project name of the job
	 * that is configured to be run if a new 
	 * IDoc is received
	 * 
	 * @return
	 */
	public String getProjectName();
	
	/**
	 * return the threshold of IDocs
	 * that have to be received before
	 * the DataStage job is triggered
	 *  
	 * @return
	 */
	public int getIDocCountThreshold();
	
	/**
	 * return true if the DataStage
	 * default logon credentials should be used
	 * to run the DataStage job. Return false
	 * if non-default user name and password
	 * have been specified
	 * 
	 * @return
	 */
	public boolean useDefaultLogon();
	
	/**
	 * return the DataStage user name
	 * 
	 * @return
	 */
	public String getDSUsername();
	
	/**
	 * return the DataStage user password
	 * 
	 * @return
	 */
	public String getDSPassword();
	
	
	/**
	 * return the IDocCounter
	 * that counts the number of
	 * incoming IDocs
	 * 
	 * @return
	 */
	public IDocCounter getIDocCounter();
	
	/**
	 * getIDocTypDirectory
	 * 
	 * get the directory on the file system
	 * that holds the IDoc type configuration
	 * and IDocFiles of an IDocType
	 * 
	 * @return
	 */
	public String getIDocTypDirectory();
	
}
