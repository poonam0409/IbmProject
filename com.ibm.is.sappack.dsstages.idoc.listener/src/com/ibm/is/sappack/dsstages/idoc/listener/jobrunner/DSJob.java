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
// Module Name : com.ibm.is.sappack.dsstages.idoc.listener.jobrunner
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.dsstages.idoc.listener.jobrunner;

/**
 * DSJob
 * 
 * Helper class to store DataStage job
 * names and corresponding project names
 */
public class DSJob {

	static String copyright() {
		return com.ibm.is.sappack.dsstages.idoc.listener.jobrunner.Copyright.IBM_COPYRIGHT_SHORT;
	}

	/* jobName */
	private String jobName;
	
	/* projectName */
	private String projectName;
	
	
	/**
	 * DSJob
	 * 
	 * @param projectName
	 * @param jobName
	 */
	public DSJob(String projectName, String jobName) {
		this.jobName = jobName;
		this.projectName = projectName;	
	}
	
	
	/**
	 * getJobName
	 * 
	 * return the name of the DataStage job
	 * 
	 * @return
	 */
	public String getJobName() {
		return this.jobName;
	}
	
	/**
	 * getProjectName
	 * 
	 * return the project name of the DataStage job
	 * 
	 * @return
	 */
	public String getProjectName() {
		return this.projectName;
	}
}