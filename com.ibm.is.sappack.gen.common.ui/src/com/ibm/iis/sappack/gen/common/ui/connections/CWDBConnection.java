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


import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.ProfileManager;
import org.eclipse.jface.dialogs.MessageDialog;

import com.ibm.iis.sappack.gen.common.ui.util.Utils;
import com.ibm.is.sappack.gen.common.ui.Activator;
import com.ibm.is.sappack.gen.common.ui.Messages;


public class CWDBConnection {
	private String name;
	private IConnectionProfile profile;


	static String copyright() {
		return Copyright.IBM_COPYRIGHT_SHORT;
	}


	public CWDBConnection(String name, IConnectionProfile profile) {
		this.name = name;
		this.profile = profile;
	}

	public IConnectionProfile getProfile() {
		return this.profile;
	}

	public void setProfile(IConnectionProfile profile) {
		this.profile = profile;
	}

	public String getName() {
		return this.name;
	}

	public static IConnectionProfile[] getAllConnectionProfiles() {
		IConnectionProfile[] profs = ProfileManager.getInstance().getProfiles();
		return profs;
	}

	public Connection getJDBCConnection() {
		Object o = profile.getManagedConnection("java.sql.Connection").getConnection().getRawConnection(); //$NON-NLS-1$
		if (o instanceof Connection) {
			return (Connection) o;
		}
		return null;
	}

	public boolean ensureIsConnected() {
	//	boolean connected = profile.isConnected(); 
		int connectionState = profile.getConnectionState();
		if (connectionState != IConnectionProfile.CONNECTED_STATE) {
			IStatus st = profile.connect();
			if (!st.isOK() || profile.getConnectionState() != IConnectionProfile.CONNECTED_STATE) {
				String name = profile.getName();
				String msg = MessageFormat.format(Messages.CWDBConnection_0, name);
				MessageDialog.openError(null, Messages.CWDBConnection_1, msg);
				return false;
			}
		}
		Connection jdbcConn = getJDBCConnection();
		if (jdbcConn == null) {
			return false;
		}
		boolean foundLegacySystems = false;
		boolean foundSAPSystems = false;
		try {
			DatabaseMetaData md = jdbcConn.getMetaData();
			ResultSet rs = md.getTables(null, "AUX", "%", null); //$NON-NLS-1$ //$NON-NLS-2$
			while (rs.next()) {
				String tableName = rs.getString("TABLE_NAME"); //$NON-NLS-1$
				if (tableName.equals("SAP_SYSTEM")) { //$NON-NLS-1$
					foundSAPSystems = true;
				} else if (tableName.equals("LEGACY_SYSTEM")) { //$NON-NLS-1$
					foundLegacySystems = true;
				}
			}
		} catch (SQLException exc) {
			Activator.logException(exc);
			MessageDialog.openError(null, Messages.CWDBConnection_2, Messages.CWDBConnection_3);
			return false;
		}
		if (!(foundLegacySystems && foundSAPSystems)) {
			MessageDialog.openError(null, Messages.CWDBConnection_4, MessageFormat.format(Messages.CWDBConnection_5, getName()));
			return false;
		}
		return true;
	}

	public List<SapSystem> getAllSAPSystems() {
		Connection jdbcConn = getJDBCConnection();
		List<SapSystem> result = new ArrayList<SapSystem>();
		if (jdbcConn == null) {
			return result;
		}
		try {
			PreparedStatement query = jdbcConn.prepareStatement("SELECT " + // //$NON-NLS-1$
					"T2.SHORTNAME, " + // 1 //$NON-NLS-1$
					"T.HOST, " + // 2 //$NON-NLS-1$
					"T.CLIENT, " + // 3 //$NON-NLS-1$
					"T.GROUPNAME, " + // 4 //$NON-NLS-1$
					"T.LANGUAGE, " + // 5 //$NON-NLS-1$
					"T.MESSAGESERVER, " + // 6 //$NON-NLS-1$
					"T.ROUTERSTRING, " + // 7 //$NON-NLS-1$
					"T.SAPSYSTEMID, " + // 8 //$NON-NLS-1$
					"T.SYSTEMNUMBER, " + // 9 //$NON-NLS-1$
					"T.USELOADBALANCING, " + // 10 //$NON-NLS-1$
					"T.USER " + // 11 //$NON-NLS-1$
					"FROM AUX.SAP_SYSTEM AS T, AUX.LEGACY_SYSTEM AS T2 WHERE T.CW_LEGACY_ID = T2.CW_LEGACY_ID"); //$NON-NLS-1$
			ResultSet rs = query.executeQuery();
			while (rs.next()) {
				CWDBSAPSystem sapSystem = new CWDBSAPSystem(getName(), rs.getString(1));
				short useLoadBalancing = rs.getShort(10);
				if (useLoadBalancing == 1) {
					sapSystem.setHost(rs.getString(6));
				} else {
					sapSystem.setHost(rs.getString(2));
				}
				sapSystem.setClientId(Integer.parseInt(rs.getString(3)));
				sapSystem.setGroupName(rs.getString(4));
				sapSystem.setUserLanguage(rs.getString(5));
				sapSystem.setRouterString(rs.getString(7));
				sapSystem.setSystemId(rs.getString(8));
				sapSystem.setSystemNumber(rs.getString(9));
				sapSystem.setUsername(rs.getString(11));
				result.add(sapSystem);
			}
		} catch (SQLException e) {
			Activator.logException(e);
			Utils.showUnexpectedException(null, e);
			return new ArrayList<SapSystem>();
		}
		return result;
	}
}
