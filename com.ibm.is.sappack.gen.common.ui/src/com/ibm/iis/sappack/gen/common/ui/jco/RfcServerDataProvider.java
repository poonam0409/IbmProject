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
// Module Name : com.ibm.is.sappack.gen.tools.sap.jco
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.iis.sappack.gen.common.ui.jco;

import java.util.Properties;

import com.sap.conn.jco.ext.ServerDataEventListener;
import com.sap.conn.jco.ext.ServerDataProvider;

public class RfcServerDataProvider implements ServerDataProvider {	
	
	static String copyright()
	{ return Copyright.IBM_COPYRIGHT_SHORT; }

	
	private Properties props = new Properties();
	
	public RfcServerDataProvider() {
		
	}
	
	/**
	 * setConnectionCount
	 * @param connectionCount
	 */
	public void setConnectionCount(int connectionCount) {
		
		this.props.put(ServerDataProvider.JCO_CONNECTION_COUNT, connectionCount);
	}
	
	/**
	 * setProgramID
	 * @param programID
	 */
	public void setProgramID(String programID) {
		this.props.put(ServerDataProvider.JCO_PROGID, programID);
	}
	
	/**
	 * setGatewayHost
	 * @param gatewayHost
	 */
	public void setGatewayHost(String gatewayHost) {
		this.props.put(ServerDataProvider.JCO_GWHOST, gatewayHost);
	}
	
	/**
	 * setGatewayService
	 * @param gatewayService
	 */
	public void setGatewayService(String gatewayService) {
		this.props.put(ServerDataProvider.JCO_GWSERV, gatewayService);
	}
	
	
	@Override
	public Properties getServerProperties(String s) {

		
		return this.props;
		
	}

	@Override
	public void setServerDataEventListener(ServerDataEventListener serverdataeventlistener) {
		// nothing needs to be done here
	}

	@Override
	public boolean supportsEvents() {
		// nothing needs to be done here
		return false;
	}
	
}
