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
import java.util.List;

import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.ProfileManager;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.XMLMemento;

import com.ibm.is.sappack.gen.common.ui.Activator;


public class CWDBConnectionRepository {

	final static String CWDB_ROOT = "CWDBROOT"; //$NON-NLS-1$
	final static String CWDB_CONN = "CWDCONN"; //$NON-NLS-1$
	final static String CWDB_CONN_NAME = "CWDB_CONN_NAME"; //$NON-NLS-1$
	final static String CWDB_PROFILE_NAME = "CWDB_PROFILE_NAME"; //$NON-NLS-1$

	private List<CWDBConnection> connections;


	static String copyright() {
		return Copyright.IBM_COPYRIGHT_SHORT;
	}


	public CWDBConnectionRepository() {
		this.connections = new ArrayList<CWDBConnection>();
		load();
	}
	
	void load() {
		/*
		IConnectionProfile[] profs = ProfileManager.getInstance().getProfiles();
		for (IConnectionProfile p : profs) {
			this.connections.add(new CWDBConnection(p.getName(), p));
		}
		*/
		
		File file = Activator.getDefault().getStateLocation().append(CWDB_ROOT).toFile();
		if (!file.exists()) {
			return;
		}
		try {
			IConnectionProfile[] profs = ProfileManager.getInstance().getProfiles();
			FileReader fileReader = new FileReader(file);

			XMLMemento memento = XMLMemento.createReadRoot(fileReader);
			IMemento[] childMementos = memento.getChildren(CWDB_CONN);
			for (IMemento child : childMementos) {
				String name = child.getString(CWDB_CONN_NAME);
				String profileName = child.getString(CWDB_PROFILE_NAME);
				IConnectionProfile profile = null;
				for (IConnectionProfile p : profs) {
					if (p.getInstanceID().equals(profileName)) {
						profile = p;
						break;
					}
				}

				CWDBConnection conn = new CWDBConnection(name, profile);
				this.connections.add(conn);
			}
		} catch (Exception exc) {
			exc.printStackTrace();
			Activator.logException(exc);
		}
	
	}
	
	public void save() {
		try {
			XMLMemento memento = XMLMemento.createWriteRoot(CWDB_ROOT);
			for (CWDBConnection conn : this.connections) {
				XMLMemento childMemento = (XMLMemento) memento.createChild(CWDB_CONN);
				childMemento.putString(CWDB_CONN_NAME, conn.getName());
				childMemento.putString(CWDB_PROFILE_NAME, conn.getProfile().getInstanceID());
			}
			FileWriter fileWriter = new FileWriter(Activator.getDefault().getStateLocation().append(CWDB_ROOT).toFile());
			memento.save(fileWriter);
		} catch (Exception exc) {
			exc.printStackTrace();
			Activator.logException(exc);
		}
	}

	public CWDBConnection createNewConnection(String name, IConnectionProfile profile) {
		CWDBConnection newConn = new CWDBConnection(name, profile);
		this.connections.add(newConn);
		return newConn;
	}
	
	public List<CWDBConnection> getAllCWDBConnections() {
		return this.connections;
	}
	
	public CWDBConnection getConnection(String name) {
		for (CWDBConnection conn : this.connections) {
			if (conn.getName().equals(name)) {
				return conn;
			}
		}
		return null;
	}

	static CWDBConnectionRepository repository;
	public static CWDBConnectionRepository getRepository() {
		if (repository == null) {
			repository = new CWDBConnectionRepository();
		}
		return repository;
	}

	public void removeConnection(String name) {
		CWDBConnection conn = getConnection(name);
		if (conn != null) {
			this.connections.remove(conn);
		}
	}
}
