//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2013, 2014                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.iis.sappack.gen.common.ui.connections
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.iis.sappack.gen.common.ui.connections;


public class IISSapSystem extends SapSystem {
	private IISConnection iisConnection;
	private SapSystem delegateSapSystem;


	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	


  	public IISSapSystem(SapSystem sapSystem, IISConnection iisConnection) {
		super(sapSystem.getName());
		this.delegateSapSystem = sapSystem;
		this.iisConnection = iisConnection;
	}

	public IISConnection getIISConnection() {
		return this.iisConnection;
	}

	public String getGroupName() {
		return delegateSapSystem.getGroupName();
	}

	public void setGroupName(String groupName) {
		delegateSapSystem.setGroupName(groupName);
	}

	public boolean isMessageServerSystem() {
		return delegateSapSystem.isMessageServerSystem();
	}

	public void setMessageServerSystem(boolean isMessageServerSystem) {
		delegateSapSystem.setMessageServerSystem(isMessageServerSystem);
	}

	public String getSystemId() {
		return delegateSapSystem.getSystemId();
	}

	public void setSystemId(String systemId) {
		delegateSapSystem.setSystemId(systemId);
	}

	public String getRouterString() {
		return delegateSapSystem.getRouterString();
	}

	public void setRouterString(String routerString) {
		delegateSapSystem.setRouterString(routerString);
	}

	public int getClientId() {
		return delegateSapSystem.getClientId();
	}

	public void setClientId(int clientId) {
		delegateSapSystem.setClientId(clientId);
	}

	public String getHost() {
		return delegateSapSystem.getHost();
	}

	public void setHost(String host) {
		delegateSapSystem.setHost(host);
	}

	public String getPassword() {
		return delegateSapSystem.getPassword();
	}

	public void setPassword(String password) {
		delegateSapSystem.setPassword(password);
	}

	public String getSystemNumber() {
		return delegateSapSystem.getSystemNumber();
	}

	public void setSystemNumber(String systemNumber) {
		delegateSapSystem.setSystemNumber(systemNumber);
	}

	public String getUserLanguage() {
		return delegateSapSystem.getUserLanguage();
	}

	public String getUsername() {
		return delegateSapSystem.getUsername();
	}

	public void setUsername(String username) {
		delegateSapSystem.setUsername(username);
	}

	public void setUserLanguage(String userLanguage) {
		delegateSapSystem.setUserLanguage(userLanguage);
	}

	public String getFullName() {
		return this.iisConnection.getName() + SAPConnectionRepository.IIS_SAP_CONN_SEP + delegateSapSystem.getName();
	}

	public String getName() {
		return delegateSapSystem.getName();
	}

	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (!(o instanceof IISSapSystem)) {
			return false;
		}
		IISSapSystem other = (IISSapSystem) o;
		return this.getFullName().equals(other.getFullName());
	}

	public int hashCode() {
		return getFullName().hashCode();
	}

	public Boolean isUnicode() {
		return delegateSapSystem.isUnicode();
	}

	public SapSystem clone() {
		return delegateSapSystem.clone();
	}

}
