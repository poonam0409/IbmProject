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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Properties;

import com.ibm.iis.sappack.gen.common.ui.connections.SapSystem;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.ext.DestinationDataEventListener;
import com.sap.conn.jco.ext.DestinationDataProvider;
import com.sap.conn.jco.ext.Environment;

public class RfcDestinationDataProvider implements DestinationDataProvider {

	private static final Properties configuration;
	private static final NumberFormat format = new DecimalFormat("000"); //$NON-NLS-1$

	private static final RfcDestinationDataProvider destinationDataProvider;

	public static final String JCO_DESTINATION_ID = "JCO_DESTINATION_ID"; //$NON-NLS-1$
	
	static String copyright()
	{ return Copyright.IBM_COPYRIGHT_SHORT; }

	static {
		destinationDataProvider = new RfcDestinationDataProvider();
		Environment.registerDestinationDataProvider(destinationDataProvider);
		configuration = new Properties();
	}

	public static final JCoDestination getDestination(SapSystem sapSystem)
			throws JCoException {
	   
		if (sapSystem.getPassword() == null) {
			return null;
		}
		
		// since the configuration properties object is static
		// we have to clear it at this point to avoid any interferences
		// between properties for standalone SAP systems and message
		// server systems
		configuration.clear();
		
		if (! sapSystem.isMessageServerSystem()) {
			String clientNr = format.format(sapSystem.getClientId());
			configuration.setProperty(DestinationDataProvider.JCO_CLIENT, clientNr);
			configuration.setProperty(DestinationDataProvider.JCO_USER, sapSystem.getUsername());
			configuration.setProperty(DestinationDataProvider.JCO_PASSWD, sapSystem.getPassword());
			configuration.setProperty(DestinationDataProvider.JCO_ASHOST, sapSystem.getHost());
         configuration.setProperty(DestinationDataProvider.JCO_LANG, sapSystem.getUserLanguage());
	
			configuration.setProperty(DestinationDataProvider.JCO_SYSNR,			                          sapSystem.getSystemNumber());
		} else {
			String clientNr = format.format(sapSystem.getClientId());
			configuration.setProperty(DestinationDataProvider.JCO_CLIENT, clientNr);
			configuration.setProperty(DestinationDataProvider.JCO_USER, sapSystem.getUsername());
			configuration.setProperty(DestinationDataProvider.JCO_PASSWD, sapSystem.getPassword());
			configuration.setProperty(DestinationDataProvider.JCO_MSHOST, sapSystem.getHost());
         configuration.setProperty(DestinationDataProvider.JCO_LANG, sapSystem.getUserLanguage());
			configuration.setProperty(DestinationDataProvider.JCO_R3NAME, sapSystem.getSystemId());
			configuration.setProperty(DestinationDataProvider.JCO_GROUP, sapSystem.getGroupName());
			
			// only set the router string property if it has been set in the UI
			// do not attempt to use things like an empty string or even NULL
			if (sapSystem.getRouterString() != null) {
				configuration.setProperty(DestinationDataProvider.JCO_SAPROUTER, sapSystem.getRouterString());
			}
		}
		
		return JCoDestinationManager.getDestination(JCO_DESTINATION_ID);
	}
	

	public boolean supportsEvents() {
		return false;
	}

	public Properties getDestinationProperties(String arg0) {
		return configuration;
	}

	public void setDestinationDataEventListener(
			DestinationDataEventListener listener) {
		// Nothing to be done here
	}

}
