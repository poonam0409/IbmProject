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


import java.text.MessageFormat;

import org.eclipse.jface.dialogs.IDialogConstants;

import com.ibm.iis.sappack.gen.common.ui.preferences.IISPreferencePage;
import com.ibm.iis.sappack.gen.common.ui.util.MissingPasswordDialog;
import com.ibm.is.sappack.gen.common.request.RequestBase;
import com.ibm.is.sappack.gen.common.ui.Messages;


public class IISConnection {
	private IISConnectionRepository repository;
	private String  name;
	private String  domain;
	private int     domainServerPort;
	private String  user;
	private String  password;
	private boolean useHTTPS = false;
	private int     httpsPort;

  	
	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	


  	IISConnection(String name, IISConnectionRepository repository) {
		super();
		this.name = name;
		this.repository = repository;
	}

	public IISConnectionRepository getRepository() {
		return this.repository;
	}
	
	void setName(String name) {
		this.name = name;
	}

	void setDomain(String domain) {
		this.domain = domain;
	}

	void setDomainServerPort(int domainServerPort) {
		this.domainServerPort = domainServerPort;
	}

	void setUser(String user) {
		this.user = user;
	}

	void setPassword(String password) {
		this.password = password;
	}

	void setUseHTTPS(boolean useHTTPS) {
		this.useHTTPS = useHTTPS;
	}

	public String getName() {
		return name;
	}

	public String getDomain() {
		return domain;
	}

	public int getDomainServerPort() {
		return domainServerPort;
	}

	public String getUser() {
		return user;
	}

	public String getPassword() {
		return password;
	}

	public void resetPassword() {
		this.password = null;
	}

	public boolean useHTTPS() {
		return this.useHTTPS;
	}
	
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}
		if (! (other instanceof IISConnection)) {
			return false;
		}
		IISConnection otherConn = (IISConnection) other;
		return otherConn.name.equals(name);
	}
	
	public int hashCode() {
		return this.name.hashCode();
	}
	
	public boolean ensurePasswordIsSet() {
		if (this.password == null || this.password.trim().length() == 0) {
			String msg = MessageFormat.format(Messages.IISConnection_0, this.name);
			MissingPasswordDialog pwd = new MissingPasswordDialog(null, msg, this.user);
			int res = pwd.open();
			if (res == IDialogConstants.CANCEL_ID) {
				return false;
			}
			this.password = pwd.getPassword();
		}
		return true;
	}

	public void setHTTPSPort(int httpsPort) {
		this.httpsPort = httpsPort;		
	}
	
	public int getHTTPSPort() {
		return this.httpsPort;
	}
	
	public static void initializeRequest(RequestBase request, IISConnection conn) {
		request.setIISClientLocation(IISPreferencePage.getIISClientDirectory());
		request.setDomainServerName(conn.getDomain());
		request.setISUsername(conn.getUser());
		request.setISPassword(conn.getPassword());
		request.setDomainServerPort(conn.getDomainServerPort());
		if (conn.useHTTPS()) {
			request.setHTTPSPort(conn.getHTTPSPort());
		}
	}
	
}
