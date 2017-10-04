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
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.ui.IMemento;
import org.eclipse.ui.XMLMemento;

import com.ibm.is.sappack.gen.common.ui.Activator;
import com.ibm.is.sappack.gen.common.ui.Messages;


public class IISConnectionRepository {
	private static final String IIS_ROOT = "IIS_ROOT"; //$NON-NLS-1$
	private static final String IIS_CONN = "IIS_CONN"; //$NON-NLS-1$
	private static final String IIS_CONN_NAME = "IIS_CONN_NAME"; //$NON-NLS-1$
	private static final String IIS_CONN_DOMAIN = "IIS_CONN_DOMAIN"; //$NON-NLS-1$
	private static final String IIS_CONN_PORT = "IIS_CONN_PORT"; //$NON-NLS-1$
	private static final String IIS_CONN_USER = "IIS_CONN_USER"; //$NON-NLS-1$
	private static final String IIS_CONN_USE_HTTPS = "IIS_CONN_USE_HTTPS"; //$NON-NLS-1$
	private static final String IIS_CONN_HTTPS_PORT = "IIS_CONN_HTTPS_PORT"; //$NON-NLS-1$

	private List<IISConnection> connections;


	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	

	
	private IISConnectionRepository() {
		super();
		this.connections = new ArrayList<IISConnection>();
		load();
	}
	
	void checkRepository(IISConnection conn) {
		if (conn.getRepository() != this) {
			throw new RuntimeException(Messages.IISConnectionRepository_0);
		}
	}

	public List<IISConnection> getAllConnections() {
		return this.connections;
	}

	private void load() {
		File file = Activator.getDefault().getStateLocation().append(IIS_ROOT).toFile();
		if (!file.exists()) {
			return;
		}
		try {
			FileReader fileReader = new FileReader(file);

			XMLMemento memento = XMLMemento.createReadRoot(fileReader);
			IMemento[] childMementos = memento.getChildren(IIS_CONN);
			for (IMemento child : childMementos) {
				String name = child.getString(IIS_CONN_NAME);
				IISConnection conn = new IISConnection(name, this);
				conn.setDomain(child.getString(IIS_CONN_DOMAIN));
				conn.setDomainServerPort(child.getInteger(IIS_CONN_PORT));
				conn.setUser(child.getString(IIS_CONN_USER));
				Boolean b = child.getBoolean(IIS_CONN_USE_HTTPS);
				if (b != null) {
					conn.setUseHTTPS(b);
					conn.setHTTPSPort(child.getInteger(IIS_CONN_HTTPS_PORT));
				}
				this.connections.add(conn);
			}
		} catch (Exception exc) {
			exc.printStackTrace();
			Activator.logException(exc);
		}
	}

	public void save() {
		try {
			XMLMemento memento = XMLMemento.createWriteRoot(IIS_ROOT);
			for (IISConnection conn : this.connections) {
				XMLMemento childMemento = (XMLMemento) memento.createChild(IIS_CONN);
				childMemento.putString(IIS_CONN_NAME, conn.getName());
				childMemento.putString(IIS_CONN_DOMAIN, conn.getDomain());
				childMemento.putInteger(IIS_CONN_PORT, conn.getDomainServerPort());
				childMemento.putString(IIS_CONN_USER, conn.getUser());
				childMemento.putBoolean(IIS_CONN_USE_HTTPS, conn.useHTTPS());
				childMemento.putInteger(IIS_CONN_HTTPS_PORT, conn.getHTTPSPort());
			}
			FileWriter fileWriter = new FileWriter(Activator.getDefault().getStateLocation().append(IIS_ROOT).toFile());
			memento.save(fileWriter);
		} catch (Exception exc) {
			exc.printStackTrace();
			Activator.logException(exc);
		}
	}

	
	
	public void removeConnection(IISConnection conn) {
		checkRepository(conn);
		this.connections.remove(conn);
	}

	public IISConnection createNewConnection(String name) {
		for (IISConnection conn : this.connections) {
			if (conn.getName().equals(name)) {
				throw new RuntimeException(MessageFormat.format(Messages.IISConnectionRepository_1, name));
			}
		}
		IISConnection conn = new IISConnection(name, this);
		return conn;
	}
	
	public void add(IISConnection conn) {
		checkRepository(conn);
		this.connections.add(conn);
	}

	public IISConnection retrieveConnection(String name) {
		for (IISConnection conn : this.connections) {
			if (conn.getName().equals(name)) {
				return conn;
			}
		}
		return null;
	}

	private static IISConnectionRepository repository;

	public static IISConnectionRepository getRepository() {
		if (repository == null) {
			repository = new IISConnectionRepository();
		}
		return repository;
	}
}
