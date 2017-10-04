package com.ibm.is.sappack.cw.app.services;

import java.util.Properties;

import com.ibm.is.sappack.cw.app.data.config.LegacySystem;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.ext.DestinationDataEventListener;
import com.sap.conn.jco.ext.DestinationDataProvider;
import com.sap.conn.jco.ext.Environment;

public class RfcDestinationDataProvider implements DestinationDataProvider {

	private static final String EMPTY_STRING = "";
	private static final Properties configuration;
	private static final RfcDestinationDataProvider destinationDataProvider;

	public static final String JCO_DESTINATION_ID = "JCO_DESTINATION_ID"; //$NON-NLS-1$

	static {
		destinationDataProvider = new RfcDestinationDataProvider();
		
		try {
			Environment.registerDestinationDataProvider(destinationDataProvider);
		}
		catch (IllegalStateException ise) {
			// nothing to done here as this indicates the case
			// where the provider has been already registered
			// in a non-WAS context this should not occur as this
			// is a singleton class but in WAS context a singleton handling
			// is not possible
		}
		
		configuration = new Properties();
	}

	public static final JCoDestination getDestination(LegacySystem legacySystem)
			throws JCoException {

		if (legacySystem.getSapPassword() == null) {
			return null;
		}

		// since the configuration properties object is static
		// we have to clear it at this point to avoid any interferences
		// between properties for standalone SAP systems and message
		// server systems
		configuration.clear();

		if (!legacySystem.getSapUseLoadBalancing()) {
			configuration.setProperty(DestinationDataProvider.JCO_ASHOST,
					legacySystem.getSapHost());
			configuration.setProperty(DestinationDataProvider.JCO_CLIENT,
					legacySystem.getSapClient());
			configuration.setProperty(DestinationDataProvider.JCO_USER,
					legacySystem.getSapUser());
			configuration.setProperty(DestinationDataProvider.JCO_PASSWD,
					legacySystem.getSapPassword());
			configuration.setProperty(DestinationDataProvider.JCO_LANG,
					legacySystem.getSapLanguage());
			configuration.setProperty(DestinationDataProvider.JCO_SYSNR,
					legacySystem.getSapSystemNumber() + EMPTY_STRING);
		} else {
			configuration.setProperty(DestinationDataProvider.JCO_CLIENT,
					legacySystem.getSapClient());
			configuration.setProperty(DestinationDataProvider.JCO_USER,
					legacySystem.getSapUser());
			configuration.setProperty(DestinationDataProvider.JCO_PASSWD,
					legacySystem.getSapPassword());
			configuration.setProperty(DestinationDataProvider.JCO_MSHOST,
					legacySystem.getSapMessageServer());
			configuration.setProperty(DestinationDataProvider.JCO_LANG,
					legacySystem.getSapLanguage());
			configuration.setProperty(DestinationDataProvider.JCO_R3NAME,
					legacySystem.getSapSystemId() + EMPTY_STRING);
			configuration.setProperty(DestinationDataProvider.JCO_GROUP,
					legacySystem.getSapGroupName());

			// only set the router string property if it has been set in the UI
			// do not attempt to use things like an empty string or even NULL
			if (legacySystem.getSapRouterString() != null) {
				configuration.setProperty(
						DestinationDataProvider.JCO_SAPROUTER,
						legacySystem.getSapRouterString());
			}
		}
		
		return JCoDestinationManager.getDestination(JCO_DESTINATION_ID);
	}

	@Override
	public Properties getDestinationProperties(String destinationName) {
		return configuration;
	}

	@Override
	public void setDestinationDataEventListener(DestinationDataEventListener eventListener) {
	}

	@Override
	public boolean supportsEvents() {
		return false;
	}
}
