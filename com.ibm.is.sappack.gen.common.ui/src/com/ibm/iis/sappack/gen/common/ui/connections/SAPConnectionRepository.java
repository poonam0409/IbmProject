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


import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.eclipse.ui.IMemento;
import org.eclipse.ui.XMLMemento;

import com.ibm.is.sappack.gen.common.ui.Activator;
import com.sap.conn.jco.ext.DestinationDataProvider;


public class SAPConnectionRepository {

	public static final String IIS_SAP_CONN_SEP = "/"; //$NON-NLS-1$
	public static final String SAP_SYSTEM_NAME  = "sap.system.name"; //$NON-NLS-1$
	public static final String SAP_IS_MSG_SRV   = "sap.is_message_server"; //$NON-NLS-1$
	private static final String SAP_SYSTEMS     = "sap_systems"; //$NON-NLS-1$
	private static final String SAP_SYSTEM      = "sap_system"; //$NON-NLS-1$

	private Collection<SapSystem> sapSystems;

  	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	


	private SAPConnectionRepository() {
		read();
	}

	public SapSystem getSAPConnection(String connName) {
		if (connName.startsWith(CWDBSAPSystem.CWDBPREFIX)) {
			int ix = connName.indexOf(CWDBSAPSystem.CWDB_SEPARATOR);
			String cwdbconnName = connName.substring(CWDBSAPSystem.CWDBPREFIX.length(), ix);
			String sapConnName = connName.substring(ix + 1);
			CWDBConnection cwdbConn =  CWDBConnectionRepository.getRepository().getConnection(cwdbconnName);
			if (cwdbConn == null) {
				return null;
			}
			List<SapSystem> saps = ConnectionsView.getAllCWDBSAPSystems(cwdbConn);
			for (SapSystem sap : saps) {
				if (sap.getName().equals(sapConnName)) {
					return sap;
				}
			}
			return null;
		}
		
		int ix = connName.indexOf(SAPConnectionRepository.IIS_SAP_CONN_SEP);
		if (ix == -1) {
			Iterator<SapSystem> iterator = sapSystems.iterator();
			while (iterator.hasNext()) {
				SapSystem sapSystem = (SapSystem) iterator.next();
				if (sapSystem.getName().equals(connName)) {
					return sapSystem;
				}
			}
		} else {
			String iisConn = connName.substring(0, ix);
			String sapConn = connName.substring(ix + 1);
			IISConnection conn = IISConnectionRepository.getRepository().retrieveConnection(iisConn);
			if (conn == null) {
				return null;
			}
			List<SapSystem> saps = ConnectionsView.getAllDataStageSAPSystems(conn);
			for (SapSystem sap : saps) {
				if (sap.getName().equals(sapConn)) {
					return sap;
				}
			}
		}
		return null;

	}

	public Collection<SapSystem> getLocalSAPConnections() {
		List<SapSystem> result = new ArrayList<SapSystem>(this.sapSystems);
		return result;
	}

	public void removeLocalSAPConnection(SapSystem sapSystem) {
		sapSystems.remove(sapSystem);
	}

	public SapSystem createNewLocalSAPConnection(String name) {
		return new SapSystem(name);
	}
	
	public void addLocalSAPConnection(SapSystem sapSystem) {
		if (sapSystems.contains(sapSystem)) {
			sapSystems.remove(sapSystem);
		}
		sapSystems.add(sapSystem);
	}

	public int getSize() {
		return sapSystems.size();
	}

	public synchronized void save() {
		try {
			XMLMemento memento = XMLMemento.createWriteRoot(SAP_SYSTEMS);
			Iterator<SapSystem> iterator = sapSystems.iterator();
			while (iterator.hasNext()) {
				SapSystem sapSystem = iterator.next();
				XMLMemento childMemento = (XMLMemento) memento.createChild(SAP_SYSTEM);

				if (sapSystem.isMessageServerSystem()) {
					childMemento.putBoolean(SAP_IS_MSG_SRV, true);
					childMemento.putString(DestinationDataProvider.JCO_MSHOST, sapSystem.getHost());
					childMemento.putString(DestinationDataProvider.JCO_R3NAME, sapSystem.getSystemId());
					childMemento.putString(DestinationDataProvider.JCO_GROUP, sapSystem.getGroupName());
					childMemento.putString(DestinationDataProvider.JCO_CLIENT, String.valueOf(sapSystem.getClientId()));
				} else {
					childMemento.putBoolean(SAP_IS_MSG_SRV, false);
					childMemento.putString(DestinationDataProvider.JCO_ASHOST, sapSystem.getHost());
					childMemento.putString(DestinationDataProvider.JCO_SYSNR, sapSystem.getSystemNumber() );
					childMemento.putString(DestinationDataProvider.JCO_CLIENT, String.valueOf(sapSystem.getClientId()));
				}

				// Don't store the password!
				// childMemento.putString(DestinationDataProvider.JCO_PASSWD,
				// sapSystem.getPassword());

				childMemento.putString(DestinationDataProvider.JCO_SAPROUTER, sapSystem.getRouterString());
				childMemento.putString(DestinationDataProvider.JCO_USER, sapSystem.getUsername());
				childMemento.putString(DestinationDataProvider.JCO_LANG, sapSystem.getUserLanguage());
				childMemento.putString(SAP_SYSTEM_NAME, sapSystem.getName());
			}
			FileWriter fileWriter = new FileWriter(Activator.getDefault().getStateLocation().append(SAP_SYSTEMS).toFile());
			memento.save(fileWriter);
		} catch (Exception e) {
			e.printStackTrace();
			Activator.logException(e);
		}
	}

	private synchronized void read() {
		if (sapSystems != null) {
			return;
		}

		sapSystems = new HashSet<SapSystem>();

		try {
			File file = Activator.getDefault().getStateLocation().append(SAP_SYSTEMS).toFile();
			if (!file.exists()) {
				return;
			}
			FileReader fileReader = new FileReader(file);

			XMLMemento memento = XMLMemento.createReadRoot(fileReader);
			IMemento[] childMementos = memento.getChildren(SAP_SYSTEM);
			for (int i = 0; i < childMementos.length; i++) {
				IMemento childMemento = childMementos[i];
				SapSystem sapSystem = new SapSystem(childMemento.getString(SAP_SYSTEM_NAME));

				boolean isMessageServer = (null == childMemento.getBoolean(SAP_IS_MSG_SRV)) ? false : childMemento.getBoolean(SAP_IS_MSG_SRV)
						.booleanValue();

				if (isMessageServer) {
					sapSystem.setMessageServerSystem(true);
					sapSystem.setGroupName(childMemento.getString(DestinationDataProvider.JCO_GROUP));
					sapSystem.setSystemId(childMemento.getString(DestinationDataProvider.JCO_R3NAME));
					sapSystem.setHost(childMemento.getString(DestinationDataProvider.JCO_MSHOST));
					sapSystem.setClientId(childMemento.getInteger(DestinationDataProvider.JCO_CLIENT).intValue());
				} else {
					sapSystem.setMessageServerSystem(false);
					sapSystem.setClientId(childMemento.getInteger(DestinationDataProvider.JCO_CLIENT).intValue());
					sapSystem.setHost(childMemento.getString(DestinationDataProvider.JCO_ASHOST));
					sapSystem.setSystemNumber(childMemento.getString(DestinationDataProvider.JCO_SYSNR));
				}

				// Don't load the password!
				// sapSystem.setPassword(childMemento.getString(DestinationDataProvider.JCO_PASSWD));
				sapSystem.setRouterString(childMemento.getString(DestinationDataProvider.JCO_SAPROUTER));
				sapSystem.setUsername(childMemento.getString(DestinationDataProvider.JCO_USER));
				sapSystem.setUserLanguage(childMemento.getString(DestinationDataProvider.JCO_LANG));
				sapSystems.add(sapSystem);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Activator.logException(e);
		}

	}

	private static SAPConnectionRepository repository = null;

	public static SAPConnectionRepository getRepository() {
		if (repository == null) {
			repository = new SAPConnectionRepository();
		}
		return repository;
	}

}
