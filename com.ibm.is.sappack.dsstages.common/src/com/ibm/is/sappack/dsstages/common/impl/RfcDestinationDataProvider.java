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
// Module Name : com.ibm.is.sappack.dsstages.common.impl
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.dsstages.common.impl;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Properties;
import java.util.logging.Logger;

import com.ibm.is.sappack.dsstages.common.SapSystem;
import com.ibm.is.sappack.dsstages.common.StageLogger;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.ext.DestinationDataEventListener;
import com.sap.conn.jco.ext.DestinationDataProvider;
import com.sap.conn.jco.ext.Environment;

public class RfcDestinationDataProvider implements DestinationDataProvider {

	static String copyright() {
		return com.ibm.is.sappack.dsstages.common.impl.Copyright.IBM_COPYRIGHT_SHORT;
	}

	static final String CLASSNAME = RfcDestinationDataProvider.class.getName();

	private static Properties configuration;
	private static final NumberFormat format = new DecimalFormat("000"); //$NON-NLS-1$

	private static volatile RfcDestinationDataProvider destinationDataProvider;

	public static final String JCO_DESTINATION_ID = "JCO_DESTINATION_ID"; //$NON-NLS-1$

	static synchronized void init() {
		if (destinationDataProvider == null) {
			destinationDataProvider = new RfcDestinationDataProvider();
			Environment.registerDestinationDataProvider(destinationDataProvider);
			configuration = new Properties();
		}
	}

	public static synchronized JCoDestination getDestination(SapSystem sapSystem) throws JCoException {
		final String METHODNAME = "getDestination(SapSystem)"; //$NON-NLS-1$
		init();
		Logger logger = StageLogger.getLogger();
		logger.entering(CLASSNAME, METHODNAME);
		if (sapSystem.getPassword() == null) {
			logger.finer("Password is empty"); //$NON-NLS-1$
			logger.exiting(CLASSNAME, METHODNAME);
			return null;
		}
		logger.finer("Creating JCO destination..."); //$NON-NLS-1$

		// since the configuration properties object is static
		// we have to clear it at this point to avoid any interferences
		// between properties for standalone SAP systems and message
		// server systems
		configuration.clear();

		String clientNr = format.format(sapSystem.getClientId());
		configuration.setProperty(DestinationDataProvider.JCO_CLIENT, clientNr);
		configuration.setProperty(DestinationDataProvider.JCO_USER, sapSystem.getUserName());
		configuration.setProperty(DestinationDataProvider.JCO_PASSWD, sapSystem.getPassword());
		configuration.setProperty(DestinationDataProvider.JCO_LANG, sapSystem.getLanguage());

		if (!sapSystem.isMessageServerSystem()) {
			configuration.setProperty(DestinationDataProvider.JCO_ASHOST, sapSystem.getAppServer());
			configuration.setProperty(DestinationDataProvider.JCO_SYSNR, sapSystem.getSystemNumber() + ""); //$NON-NLS-1$
		} else {
			configuration.setProperty(DestinationDataProvider.JCO_MSHOST, sapSystem.getMsgServer());
			configuration.setProperty(DestinationDataProvider.JCO_R3NAME, sapSystem.getSystemId() + ""); //$NON-NLS-1$
			configuration.setProperty(DestinationDataProvider.JCO_GROUP, sapSystem.getGroupName());

			// only set the router string property if it has been set in the UI
			// do not attempt to use things like an empty string or even NULL
		}
		if (sapSystem.getRouterString() != null) {
			configuration.setProperty(DestinationDataProvider.JCO_SAPROUTER, sapSystem.getRouterString());
		}

		JCoDestination result = JCoDestinationManager.getDestination(JCO_DESTINATION_ID);
		logger.exiting(CLASSNAME, METHODNAME);
		return result;

	}

	public boolean supportsEvents() {
		return false;
	}

	public Properties getDestinationProperties(String arg0) {
		return configuration;
	}

	public void setDestinationDataEventListener(DestinationDataEventListener listener) {
		// Nothing to be done here
	}

}
