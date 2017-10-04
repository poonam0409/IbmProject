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
// Module Name : com.ibm.is.sappack.gen.server.jobgenerator.util
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.server.jobgenerator.util;

public class LogonInformation {

	public final static String FT_PROJECT_NAME = "projectname";
	public final static String FT_ADMIN = "adminuser";
	public final static String FT_ADMIN_PASSWORD = "adminpasswd";
	public final static String JOB_NAME = "jobname";
	
	private String host;
	private String projectname;
	private String username;
	private String password;
	private String jobname;
	
	private static String EMPTY = "";
	
	static String copyright()
	{ return com.ibm.is.sappack.gen.server.jobgenerator.util.Copyright.IBM_COPYRIGHT_SHORT; }	
	
	public LogonInformation() {
		host = EMPTY;
		projectname = EMPTY;
		username = EMPTY;
		password = EMPTY;
		jobname = EMPTY;
	}
	
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getJobname() {
		return jobname;
	}
	public void setJobname(String jobname) {
		this.jobname = jobname;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getProjectname() {
		return projectname;
	}
	public void setProjectname(String projectname) {
		this.projectname = projectname;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String toString() {
		StringBuffer logonStringBuffer = new StringBuffer();
		logonStringBuffer.append(FT_PROJECT_NAME+"="+getProjectname()+"&");
		logonStringBuffer.append(FT_ADMIN+"="+getUsername()+"&");
		logonStringBuffer.append(FT_ADMIN_PASSWORD+"="+getPassword()+"&");
		logonStringBuffer.append(JOB_NAME+"="+getJobname());
		return logonStringBuffer.toString();
	}
	
}
