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


import java.text.Collator;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import com.ibm.iis.sappack.gen.common.ui.preferences.IISPreferencePage;
import com.ibm.is.sappack.gen.common.JobGeneratorException;
import com.ibm.is.sappack.gen.common.request.GetAllSapConnectionsRequest;
import com.ibm.is.sappack.gen.common.request.GetAllSapConnectionsResponse;
import com.ibm.is.sappack.gen.common.request.ServerRequestUtil;
import com.ibm.is.sappack.gen.common.ui.Activator;
import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.common.ui.util.DSProjectUtils;


/**
 * SapConnectionRetreiver
 * 
 * Gets all DataStage SAP R/3 connections for the given Information Server
 * domain and project
 */
public class SapConnectionRetreiver {

	/* information server logon credentials */
	private String domainServer;
	private int port;
	private String username;
	private String password;
	private String projectName;

	/* GetAllSapConnectionsResponse error message */
	private String errorMessage;
	
	/* constants */
	private static final String DEFAULTCLIENT = "DEFAULTCLIENT"; //$NON-NLS-1$
	private static final String SAPSYSNUM = "SAPSYSNUM"; //$NON-NLS-1$
	private static final String DEFAULTUSERNAME = "DEFAULTUSERNAME"; //$NON-NLS-1$
	private static final String SAPAPPSERVER = "SAPAPPSERVER"; //$NON-NLS-1$
	
	/* set of connections */
	private Collection<String> connections;
	private Map<?, ?> connectionsMap;
	private boolean useHTTPS;
	private int httpsPort;


	static String copyright() {
		return Copyright.IBM_COPYRIGHT_SHORT;
	}

	
	/**
	 * SapConnectionSelectionWidget
	 * 
	 * @param domainServer
	 * @param port
	 * @param username
	 * @param password
	 * @param projectName
	 */
	public SapConnectionRetreiver(String domainServer, int port, String username, String password, String projectName) {
		this(domainServer, port, username, password, projectName, false, 9443);
	}

	public SapConnectionRetreiver(String domainServer, int port, String username, String password, String projectName, boolean useHTTPS, int httpsPort) {
		this.domainServer = domainServer;
		this.port = port; 
		this.username = username;
		this.password = password;
		this.projectName = projectName;
		this.errorMessage = ""; //$NON-NLS-1$
		this.useHTTPS = useHTTPS;
		this.httpsPort = httpsPort;	
	}

	/**
	 * getAllSapConnections
	 * 
	 * returns a list of all SAP R/3 connections
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Collection<String> getAllSapConnections() {

		try {
			GetAllSapConnectionsRequest req = new GetAllSapConnectionsRequest();
			req.setDomainServerName(this.domainServer);
			req.setISUsername(this.username);
			req.setISPassword(this.password);
			req.setDomainServerPort(this.port);
			if (this.useHTTPS) {
				req.setHTTPSPort(this.httpsPort);
			}
			req.setIISClientLocation(IISPreferencePage.getIISClientDirectory());
			
	      // update request with project name, DS hostname and DS RPC port 
	      // (possibly contained in the project name)
			DSProjectUtils.updateRequestData(this.projectName, req);
			
			GetAllSapConnectionsResponse resp = (GetAllSapConnectionsResponse) ServerRequestUtil.send(req);
			if (resp.containsErrors()) {
	         this.errorMessage = resp.get1stMessage();
				Activator.getLogger().log(Level.SEVERE, this.errorMessage);
			}
			else
			{
            this.errorMessage = null;

   			this.connectionsMap = resp.getConnectionsMap();

   			// sort the set (list)
            List keyList = new ArrayList(this.connectionsMap.keySet());
            Collator c = Collator.getInstance();
            c.setStrength(Collator.PRIMARY);
            Collections.sort(keyList, c);
            
            this.connections = keyList;
			}
		} catch (JobGeneratorException e) {
			e.printStackTrace();
			String msg = MessageFormat.format(Messages.SapConnectionRetreiver_0, e.getLocalizedMessage());
			Activator.getLogger().log(Level.SEVERE, msg, e);
			this.errorMessage = msg;
		}

		return this.connections;
	}
	
	/**
	 * getSapSystem
	 * 
	 * creates a SapSystem object for
	 * the given connection name
	 * 
	 * @param connectionName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public SapSystem getSapSystem(String connectionName) {
		
		
		
		/* check if we know this connection */
		if(this.connections != null && this.connectionsMap != null) {
			if(this.connections.contains(connectionName)) {
				Object obj = this.connectionsMap.get(connectionName);
				if(obj instanceof Map) {
					Map<String, String> connectionData = (Map) obj;
					
					/* create a new SapSystem object */
					SapSystem sapSystem = new SapSystem(connectionName);
					sapSystem.setSystemNumber(connectionData.get(SAPSYSNUM)); 
					sapSystem.setHost(connectionData.get(SAPAPPSERVER));
					// if DEFAULTCLIENT and DEFAULTUSERNAME 
					// is not present, the connection was created thorugh, e.g., the BAPI GUI
					String defaultClient = connectionData.get(DEFAULTCLIENT);
					if (defaultClient != null) {
						int client = 0;
						try {
							client = Integer.parseInt(defaultClient);
							sapSystem.setClientId(client);
						} catch(NumberFormatException nfe) {
							// no default client available
							String msg = Messages.SapConnectionRetreiver_1;
							msg = MessageFormat.format(msg, connectionName);
							Activator.getLogger().log(Level.WARNING, msg);
						}						
					}
					String defaultUserName = connectionData.get(DEFAULTUSERNAME);
					if (defaultUserName != null && (!"".equals(defaultUserName))) { //$NON-NLS-1$
						sapSystem.setUsername(connectionData.get(DEFAULTUSERNAME));
					} else {
						// no default user name available
						String msg = Messages.SapConnectionRetreiver_3;
						msg = MessageFormat.format(msg, connectionName);
						Activator.getLogger().log(Level.WARNING, msg);						
					}
					return sapSystem;
				}
			}
		}
		
		/* invalid connectionName */
		return null;
	}

	public String getErrorMessage() {
		return this.errorMessage;
	}
	
}
