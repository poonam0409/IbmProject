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


public class SapSystem {

	static String copyright() {
		return com.ibm.is.sappack.dsstages.common.Copyright.IBM_COPYRIGHT_SHORT;
	}
	
	private String name;

	private String appServer;

	private String msgServer;
	
	private String systemNumber;

	private int clientId;

	private String username;

	private String password;

	private Boolean isUnicode;

	private String routerString;

	private String systemId;

	private boolean isMessageServerSystem = false;

	private String groupName;

	private String language;
	
	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public boolean isMessageServerSystem() {
		return isMessageServerSystem;
	}

	public void setMessageServerSystem(boolean isMessageServerSystem) {
		this.isMessageServerSystem = isMessageServerSystem;
	}

	public String getSystemId() {
		return systemId;
	}

	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}

	public String getRouterString() {
		return routerString;
	}

	public void setRouterString(String routerString) {
		this.routerString = routerString;
	}

	public int getClientId() {
		return this.clientId;
	}

	public void setClientId(int clientId) {
		this.clientId = clientId;
	}

	public String getAppServer() {
		return this.appServer;
	}

	public void setAppServer(String appServer) {
		this.appServer = appServer;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSystemNumber() {
		return this.systemNumber;
	}

	public void setSystemNumber(String systemNumber) {
		this.systemNumber = systemNumber;
	}

	public String getUserName() {
		return this.username;
	}

	public void setUserName(String username) {
		this.username = username;
	}

	public String getLanguage() {
		return this.language;
	}
	
	public void setLanguage(String language) {
		this.language = language;
	}
	
	public String getName() {
		return this.name;
	}

	public SapSystem(String name) {
		this.name = name;
	}

	public String getMsgServer() {
		return msgServer;
	}

	public void setMsgServer(String msgServer) {
		this.msgServer = msgServer;
	}


	public boolean equals(Object o) {
		if (o instanceof SapSystem) {
			SapSystem other = (SapSystem) o;
			return this.name.equals(other.name);
		}
		return false;
	}

	public int hashCode() {
		return this.name.hashCode();
	}

	public SapSystem copy() {
		SapSystem copy = new SapSystem(this.name);
		copy.appServer = this.appServer;
		copy.msgServer = this.msgServer;
		copy.systemNumber = systemNumber;
		copy.clientId = clientId;
		copy.username = username;
		copy.password = password;
		copy.isUnicode = isUnicode;
		copy.routerString = routerString;
		copy.systemId = systemId;
		copy.isMessageServerSystem = isMessageServerSystem;
		copy.groupName = groupName;
		copy.language = this.language;
		return copy;
	}
}
