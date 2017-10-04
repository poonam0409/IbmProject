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


import com.ibm.is.sappack.gen.common.ui.util.Utilities;
import com.sap.conn.jco.JCoException;


public class SapSystem {
	protected String name;
	protected String host;
	protected String systemNumber;
	protected int clientId;
	protected String userLanguage;
	protected String username;
	protected String password;
	protected Boolean isUnicode;
	protected String routerString;
	protected String systemId;
	protected String groupName;

	protected boolean isMessageServerSystem = false;

	static String copyright() {
		return Copyright.IBM_COPYRIGHT_SHORT;
	}

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

	public String getHost() {
		return this.host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPassword() {
		return this.password;
	}

	public void resetPassword() {
		setPassword(null);
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

	public String getUserLanguage() {
		String language = this.userLanguage;

		if (language == null) {
			language = "EN"; //$NON-NLS-1$
		}

		return language;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setUserLanguage(String userLanguage) {
		this.userLanguage = userLanguage;
	}

	public String getName() {
		return this.name;
	}

	public String getFullName() {
		return getName();
	}

	public SapSystem(String name) {
		this.name = name;
	}

	public boolean equals(Object o) {
		if (o instanceof SapSystem) {
			SapSystem other = (SapSystem) o;
			return this.getFullName().equals(other.getFullName());
		}
		return false;
	}

	public int hashCode() {
		return getFullName().hashCode();
	}

	public Boolean isUnicode() {
		try {
			if (isUnicode == null) {
				isUnicode = Boolean.valueOf(Utilities.isUnicodeSystem(this));
			}
		} catch (JCoException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return isUnicode;
	}

	public SapSystem clone() {
		SapSystem copy = new SapSystem(this.name);
		copy.host = this.host;
		copy.systemNumber = systemNumber;
		copy.userLanguage = this.userLanguage;
		copy.clientId = clientId;
		copy.username = username;
		copy.password = password;
		copy.isUnicode = isUnicode;
		copy.routerString = routerString;
		copy.systemId = systemId;
		copy.isMessageServerSystem = isMessageServerSystem;
		copy.groupName = groupName;
		return copy;
	}
}
