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

public class DSEnvironment {

	static String copyright() {
		return com.ibm.is.sappack.dsstages.common.Copyright.IBM_COPYRIGHT_SHORT;
	}

	String jobName;
	String invocationID;
	int overallNodeNumber;
	int currentNodeNumber;
	String stageName;
	String projectName;
	String userName;
	String hostName;
	String version;

	public DSEnvironment() {
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public String getInvocationID() {
		return invocationID;
	}

	public void setInvocationID(String invocationID) {
		this.invocationID = invocationID;
	}

	public int getOverallNodeNumber() {
		return overallNodeNumber;
	}

	public void setOverallNodeNumber(int overallNodeNumber) {
		this.overallNodeNumber = overallNodeNumber;
	}

	public int getCurrentNodeNumber() {
		return currentNodeNumber;
	}

	public void setCurrentNodeNumber(int currentNodeNumber) {
		this.currentNodeNumber = currentNodeNumber;
	}

	public String getStageName() {
		return stageName;
	}

	public void setStageName(String stageName) {
		this.stageName = stageName;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

}
