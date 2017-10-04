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

import org.eclipse.datatools.connectivity.oda.IConnection;
import org.eclipse.datatools.connectivity.oda.IDataSetMetaData;
import org.eclipse.datatools.connectivity.oda.IResultSet;
import org.eclipse.datatools.connectivity.oda.OdaException;

/**
 * Implementation class of IDataSetMetaData for an ODA runtime driver.
 * <br>
 * For demo purpose, the auto-generated method stubs have
 * hard-coded implementation that assume this custom ODA data set
 * is capable of handling a query that returns a single result set and 
 * accepts scalar input parameters by index.
 * A custom ODA driver is expected to implement own data set specific
 * behavior in its place. 
 */
public class DataSetMetaData implements IDataSetMetaData
{
	private IConnection m_connection;
	
	static String copyright()
    { return com.ibm.is.sappack.gen.oda.runtime.driver.impl.Copyright.IBM_COPYRIGHT_SHORT; }
	
	DataSetMetaData( IConnection connection )
	{
		m_connection = connection;
	}
	
	/*
	 * @see org.eclipse.datatools.connectivity.oda.IDataSetMetaData#getConnection()
	 */
	public IConnection getConnection() throws OdaException
	{
        // TODO Auto-generated method stub
		return m_connection;
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IDataSetMetaData#getDataSourceObjects(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public IResultSet getDataSourceObjects( String catalog, String schema, String object, String version ) throws OdaException
	{
	    throw new UnsupportedOperationException();
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IDataSetMetaData#getDataSourceMajorVersion()
	 */
	public int getDataSourceMajorVersion() throws OdaException
	{
        // TODO Auto-generated method stub
		return 7;
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IDataSetMetaData#getDataSourceMinorVersion()
	 */
	public int getDataSourceMinorVersion() throws OdaException
	{
        // TODO Auto-generated method stub
		return 0;
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IDataSetMetaData#getDataSourceProductName()
	 */
	public String getDataSourceProductName() throws OdaException
	{
        // TODO Auto-generated method stub
		return Messages.DataSetMetaData_0; //$NON-NLS-1$
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IDataSetMetaData#getDataSourceProductVersion()
	 */
	public String getDataSourceProductVersion() throws OdaException
	{
		return Integer.toString( getDataSourceMajorVersion() ) + "." +   //$NON-NLS-1$
			   Integer.toString( getDataSourceMinorVersion() );
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IDataSetMetaData#getSQLStateType()
	 */
	public int getSQLStateType() throws OdaException
	{
        // TODO Auto-generated method stub
		return IDataSetMetaData.sqlStateSQL99;
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IDataSetMetaData#supportsMultipleResultSets()
	 */
	public boolean supportsMultipleResultSets() throws OdaException
	{
		return false;
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IDataSetMetaData#supportsMultipleOpenResults()
	 */
	public boolean supportsMultipleOpenResults() throws OdaException
	{
		return false;
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IDataSetMetaData#supportsNamedResultSets()
	 */
	public boolean supportsNamedResultSets() throws OdaException
	{
		return false;
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IDataSetMetaData#supportsNamedParameters()
	 */
	public boolean supportsNamedParameters() throws OdaException
	{
        // TODO Auto-generated method stub
		return false;
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IDataSetMetaData#supportsInParameters()
	 */
	public boolean supportsInParameters() throws OdaException
	{
        // TODO Auto-generated method stub
		return true;
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IDataSetMetaData#supportsOutParameters()
	 */
	public boolean supportsOutParameters() throws OdaException
	{
        // TODO Auto-generated method stub
		return false;
	}

	/*
	 * @see org.eclipse.datatools.connectivity.oda.IDataSetMetaData#getSortMode()
	 */
	public int getSortMode()
	{
        // TODO Auto-generated method stub
		return IDataSetMetaData.sortModeNone;
	}
    
}
