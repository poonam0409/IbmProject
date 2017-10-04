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
// Module Name : com.ibm.is.sappack.gen.help
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.oda.runtime.driver.impl;

import java.util.Properties;

import org.eclipse.datatools.connectivity.oda.IConnection;
import org.eclipse.datatools.connectivity.oda.IDataSetMetaData;
import org.eclipse.datatools.connectivity.oda.IQuery;
import org.eclipse.datatools.connectivity.oda.OdaException;

import com.ibm.icu.util.ULocale;
import com.ibm.iis.sappack.gen.common.ui.connections.SapConnectionTester;
import com.ibm.iis.sappack.gen.common.ui.connections.SapSystem;
import com.sap.conn.jco.JCoException;

/**
 * Implementation class of IConnection for an ODA runtime driver.
 */
public class Connection implements IConnection
{
	public static final String USE_LOAD_BALANCING_KEY = "com.ibm.is.sappack.gen.oda.runtime.driver.sap.load.balancing"; //$NON-NLS-1$
	public static final String USE_LOAD_BALANCING_ENABLED_KEY = "com.ibm.is.sappack.gen.oda.runtime.driver.sap.load.balancing.enabled"; //$NON-NLS-1$
	public static final String USE_LOAD_BALANCING_DISABLED_KEY = "com.ibm.is.sappack.gen.oda.runtime.driver.sap.load.balancing.disabled"; //$NON-NLS-1$
	public static final String SAP_HOST_NAME_KEY = "com.ibm.is.sappack.gen.oda.runtime.driver.sap.host.name"; //$NON-NLS-1$
	public static final String SAP_SYSTEM_NUMBER_KEY = "com.ibm.is.sappack.gen.oda.runtime.driver.sap.system.number"; //$NON-NLS-1$
	public static final String SAP_MESSAGE_SERVER_KEY = "com.ibm.is.sappack.gen.oda.runtime.driver.sap.message.server"; //$NON-NLS-1$
	public static final String SAP_SYSTEM_ID_KEY = "com.ibm.is.sappack.gen.oda.runtime.driver.sap.system.id"; //$NON-NLS-1$
	public static final String SAP_GROUP_NAME_KEY = "com.ibm.is.sappack.gen.oda.runtime.driver.sap.group.name"; //$NON-NLS-1$
	public static final String SAP_SYSTEM_NAME_KEY = "com.ibm.is.sappack.gen.oda.runtime.driver.sap.system.name"; //$NON-NLS-1$
	public static final String SAP_ROUTER_STRING_KEY = "com.ibm.is.sappack.gen.oda.runtime.driver.sap.router.string"; //$NON-NLS-1$
	public static final String SAP_USER_NAME_KEY = "com.ibm.is.sappack.gen.oda.runtime.driver.sap.user.name"; //$NON-NLS-1$
	public static final String SAP_USER_PASSWORD_KEY = "com.ibm.is.sappack.gen.oda.runtime.driver.sap.user.password"; //$NON-NLS-1$
	public static final String SAP_USER_LANGUAGE_KEY = "com.ibm.is.sappack.gen.oda.runtime.driver.sap.user.language"; //$NON-NLS-1$
	public static final String SAP_USER_CLIENT_NUMBER_KEY = "com.ibm.is.sappack.gen.oda.runtime.driver.sap.user.client.number"; //$NON-NLS-1$
	
	static String copyright()
    { return com.ibm.is.sappack.gen.oda.runtime.driver.impl.Copyright.IBM_COPYRIGHT_SHORT; }
	
    private boolean m_isOpen = false;
    private boolean m_useLoadBalancing = false;
    
    // standard SAP connection properties
    private String m_sapHostName = null;
    private String m_sapSystemNumber = null;
    
    // load balancing SAP connection properties
    private String m_sapMessageServer = null;
    private String m_sapSystemID = null;
    private String m_sapGroupName = null;
    
    // standard SAP properties
    private String m_sapSystemName = null;
    private String m_sapRouterString = null;
    private String m_sapUserName = null;
    private String m_sapUserPassword = null;
    private String m_sapUserLanguage = null;
    private String m_sapUserClientNumber = null;
    
    
    
	/**
	 * @return the m_useLoadBalancing
	 */
	public boolean useLoadBalancing() {
		return m_useLoadBalancing;
	}

	/**
	 * @param m_useLoadBalancing the m_useLoadBalancing to set
	 */
	private void setUseLoadBalancing(boolean m_useLoadBalancing) {
		this.m_useLoadBalancing = m_useLoadBalancing;
	}

	/**
	 * @return the m_sapHostName
	 */
	public String getSapHostName() {
		return m_sapHostName;
	}

	/**
	 * @param m_sapHostName the m_sapHostName to set
	 */
	private void setSapHostName(String m_sapHostName) {
		this.m_sapHostName = m_sapHostName;
	}

	/**
	 * @return the m_sapSystemNumber
	 */
	public String getSapSystemNumber() {
		return m_sapSystemNumber;
	}

	/**
	 * @param m_sapSystemNumber the m_sapSystemNumber to set
	 */
	private void setSapSystemNumber(String m_sapSystemNumber) {
		this.m_sapSystemNumber = m_sapSystemNumber;
	}

	/**
	 * @return the m_sapMessageServer
	 */
	public String getSapMessageServer() {
		return m_sapMessageServer;
	}

	/**
	 * @param m_sapMessageServer the m_sapMessageServer to set
	 */
	private void setSapMessageServer(String m_sapMessageServer) {
		this.m_sapMessageServer = m_sapMessageServer;
	}

	/**
	 * @return the m_sapSystemID
	 */
	public String getSapSystemID() {
		return m_sapSystemID;
	}

	/**
	 * @param m_sapSystemID the m_sapSystemID to set
	 */
	private void setSapSystemID(String m_sapSystemID) {
		this.m_sapSystemID = m_sapSystemID;
	}

	/**
	 * @return the m_sapGroupName
	 */
	public String getSapGroupName() {
		return m_sapGroupName;
	}

	/**
	 * @param m_sapGroupName the m_sapGroupName to set
	 */
	private void setSapGroupName(String m_sapGroupName) {
		this.m_sapGroupName = m_sapGroupName;
	}

	/**
	 * @return the m_sapSystemName
	 */
	public String getSapSystemName() {
		return m_sapSystemName;
	}

	/**
	 * @param m_sapSystemName the m_sapSystemName to set
	 */
	private void setSapSystemName(String m_sapSystemName) {
		this.m_sapSystemName = m_sapSystemName;
	}

	/**
	 * @return the m_sapRouterString
	 */
	public String getSapRouterString() {
		return m_sapRouterString;
	}

	/**
	 * @param m_sapRouterString the m_sapRouterString to set
	 */
	private void setSapRouterString(String m_sapRouterString) {
		this.m_sapRouterString = m_sapRouterString;
	}

	/**
	 * @return the m_sapUserName
	 */
	public String getSapUserName() {
		return m_sapUserName;
	}

	/**
	 * @param m_sapUserName the m_sapUserName to set
	 */
	private void setSapUserName(String m_sapUserName) {
		this.m_sapUserName = m_sapUserName;
	}

	/**
	 * @return the m_sapUserPassword
	 */
	public String getSapUserPassword() {
		return m_sapUserPassword;
	}

	/**
	 * @param m_sapUserPassword the m_sapUserPassword to set
	 */
	private void setSapUserPassword(String m_sapUserPassword) {
		this.m_sapUserPassword = m_sapUserPassword;
	}

	/**
	 * @return the m_sapUserLanguage
	 */
	public String getSapUserLanguage() {
		return m_sapUserLanguage;
	}

	/**
	 * @param m_sapUserLanguage the m_sapUserLanguage to set
	 */
	private void setSapUserLanguage(String m_sapUserLanguage) {
		this.m_sapUserLanguage = m_sapUserLanguage;
	}

	/**
	 * @return the m_sapUserClientNumber
	 */
	public String getSapUserClientNumber() {
		return m_sapUserClientNumber;
	}

	/**
	 * @param m_sapUserClientNumber the m_sapUserClientNumber to set
	 */
	private void setSapUserClientNumber(String m_sapUserClientNumber) {
		this.m_sapUserClientNumber = m_sapUserClientNumber;
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IConnection#open(java.util.Properties)
	 */
	public void open( Properties connProperties ) throws OdaException
	{
        if (connProperties == null) {
            throw new IllegalArgumentException(Messages.Connection_14);
        }
        
		org.eclipse.datatools.connectivity.ProfileManager manager = new org.eclipse.datatools.connectivity.ProfileManager();
		org.eclipse.datatools.connectivity.IConnectionProfile[] profiles = manager.getProfilesByCategory("com.ibm.is.sappack.gen.oda.runtime.driver"); //$NON-NLS-1$
		org.eclipse.datatools.connectivity.IConnectionProfile bocasap6Profile = manager.getProfileByName("bocasap6"); //$NON-NLS-1$

	    m_isOpen = this.populateMembers(connProperties);
	    SapSystem sapSystem = new SapSystem(this.getSapSystemName());
	    
	    if (this.useLoadBalancing()) {
			sapSystem.setClientId(Integer.parseInt(this.getSapUserClientNumber()));
			sapSystem.setHost(this.getSapMessageServer());
			sapSystem.setGroupName(this.getSapGroupName());
			sapSystem.setSystemId(this.getSapSystemID());
			sapSystem.setMessageServerSystem(true);
			sapSystem.setRouterString(this.getSapRouterString());
			sapSystem.setPassword(this.getSapUserPassword());
			sapSystem.setUsername(this.getSapUserName());
			sapSystem.setUserLanguage(this.getSapUserLanguage());
	    } else {
			sapSystem.setMessageServerSystem(false);
			sapSystem.setClientId(Integer.parseInt(this.getSapUserClientNumber()));
			sapSystem.setHost(this.getSapHostName());
			sapSystem.setPassword(this.getSapUserPassword());
			sapSystem.setSystemNumber(this.getSapSystemNumber());
			sapSystem.setRouterString(this.getSapRouterString());
			sapSystem.setUsername(this.getSapUserName());
			sapSystem.setUserLanguage(this.getSapUserLanguage());
	    }
	    
	    try {
			SapConnectionTester.ping(sapSystem);
		} catch (JCoException e) {
			m_isOpen = false;
			throw new OdaException(e);
		}
 	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IConnection#setAppContext(java.lang.Object)
	 */
	public void setAppContext( Object context ) throws OdaException
	{
	    // do nothing; assumes no support for pass-through context
	}
	
	private boolean populateMembers(Properties connProperties) throws OdaException
	{
		boolean retVal = true;
		
		String useLoadBalancing = connProperties.getProperty(Connection.USE_LOAD_BALANCING_KEY);
		if (null == useLoadBalancing || useLoadBalancing.trim().length() == 0) {
			throw new OdaException(Messages.Connection_17);
		} else if (useLoadBalancing.equals(Connection.USE_LOAD_BALANCING_DISABLED_KEY)) {
			this.setUseLoadBalancing(Boolean.FALSE);
		} else if (useLoadBalancing.equals(Connection.USE_LOAD_BALANCING_ENABLED_KEY)) {
			this.setUseLoadBalancing(Boolean.TRUE);
		}
		
		//
		// required properties first
		//
		if (this.useLoadBalancing()) {
			String sapMessageServer = connProperties.getProperty(Connection.SAP_MESSAGE_SERVER_KEY);
			if (null == sapMessageServer || sapMessageServer.trim().length() == 0) {
				retVal = false;
				throw new OdaException(Messages.Connection_18);
			}
			this.setSapMessageServer(sapMessageServer);
			
			String sapSystemID = connProperties.getProperty(Connection.SAP_SYSTEM_ID_KEY);
			if (null == sapSystemID  || sapSystemID.trim().length() == 0) {
				retVal = false;
				throw new OdaException(Messages.Connection_19);
			}
			this.setSapSystemID(sapSystemID);
			
			String sapGroupName = connProperties.getProperty(Connection.SAP_GROUP_NAME_KEY);
			if (null == sapGroupName || sapGroupName.trim().length() == 0) {
				retVal = false;
				throw new OdaException(Messages.Connection_20);
			}
			this.setSapGroupName(sapGroupName);
		} else {
			String sapHostName = connProperties.getProperty(Connection.SAP_HOST_NAME_KEY);
			if(null == sapHostName || sapHostName.trim().length() == 0) {
				retVal = false;
				throw new OdaException(Messages.Connection_21);
			}
			this.setSapHostName(sapHostName);
			
			String sapSystemNumber = connProperties.getProperty(Connection.SAP_SYSTEM_NUMBER_KEY);
			if (null == sapSystemNumber || sapSystemNumber.trim().length() == 0) {
				retVal = false;
				throw new OdaException(Messages.Connection_22); 
			}
			this.setSapSystemNumber(sapSystemNumber);
		}
		
		String sapSystemName = connProperties.getProperty(Connection.SAP_SYSTEM_NAME_KEY);
		if (null == sapSystemName || sapSystemName.trim().length() == 0) {
			retVal = false;
			throw new OdaException(Messages.Connection_23);
		}
		this.setSapSystemName(sapSystemName);
		
		
		String sapUserName = connProperties.getProperty(Connection.SAP_USER_NAME_KEY);
		if (null == sapUserName || sapUserName.trim().length() == 0) {
			retVal = false;
			throw new OdaException(Messages.Connection_24);
		}
		this.setSapUserName(sapUserName);
		
		String sapUserPassword = connProperties.getProperty(Connection.SAP_USER_PASSWORD_KEY);
		if (null == sapUserPassword || sapUserPassword.trim().length() == 0) {
			retVal = false;
			throw new OdaException(Messages.Connection_25);
		}
		this.setSapUserPassword(sapUserPassword);
		
		String sapUserLanguage = connProperties.getProperty(Connection.SAP_USER_LANGUAGE_KEY);
		if (null == sapUserLanguage || sapUserLanguage.trim().length() == 0) {
			retVal = false;
			throw new OdaException(Messages.Connection_26);
		}
		this.setSapUserLanguage(sapUserLanguage);
		
		String sapUserClientNumber = connProperties.getProperty(Connection.SAP_USER_CLIENT_NUMBER_KEY);
		if (null == sapUserClientNumber || sapUserClientNumber.trim().length() == 0) {
			retVal = false;
			throw new OdaException(Messages.Connection_27);
		}
		this.setSapUserClientNumber(sapUserClientNumber);
		
		//
		// optional properties second
		//
		String sapRouterString = connProperties.getProperty(Connection.SAP_ROUTER_STRING_KEY);
		if (null != sapRouterString  && useLoadBalancing.trim().length() > 0) {
			this.setSapRouterString(sapRouterString);
		}
		
		return retVal;
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IConnection#close()
	 */
	public void close() throws OdaException
	{
	    m_isOpen = false;
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IConnection#isOpen()
	 */
	public boolean isOpen() throws OdaException
	{
		return m_isOpen;
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IConnection#getMetaData(java.lang.String)
	 */
	public IDataSetMetaData getMetaData( String dataSetType ) throws OdaException
	{
	    // assumes that this driver supports only one type of data set,
        // ignores the specified dataSetType
		return new DataSetMetaData( this );
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IConnection#newQuery(java.lang.String)
	 */
	public IQuery newQuery( String dataSetType ) throws OdaException
	{
        // assumes that this driver supports only one type of data set,
        // ignores the specified dataSetType
		return new Query();
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IConnection#getMaxQueries()
	 */
	public int getMaxQueries() throws OdaException
	{
		return 0;	// no limit
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IConnection#commit()
	 */
	public void commit() throws OdaException
	{
	    // do nothing; assumes no transaction support needed
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IConnection#rollback()
	 */
	public void rollback() throws OdaException
	{
        // do nothing; assumes no transaction support needed
	}

    /* (non-Javadoc)
     * @see org.eclipse.datatools.connectivity.oda.IConnection#setLocale(com.ibm.icu.util.ULocale)
     */
    public void setLocale( ULocale locale ) throws OdaException
    {
        // do nothing; assumes no locale support
    }
    
}
