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

import org.eclipse.datatools.connectivity.oda.IParameterMetaData;
import org.eclipse.datatools.connectivity.oda.OdaException;

/**
 * Implementation class of IParameterMetaData for an ODA runtime driver.
 * <br>
 * For demo purpose, the auto-generated method stubs have
 * hard-coded implementation that returns a pre-defined set
 * of meta-data and query results.
 * A custom ODA driver is expected to implement own data source specific
 * behavior in its place. 
 */
public class ParameterMetaData implements IParameterMetaData 
{
	static String copyright()
    { return com.ibm.is.sappack.gen.oda.runtime.driver.impl.Copyright.IBM_COPYRIGHT_SHORT; }

	/* 
	 * @see org.eclipse.datatools.connectivity.oda.IParameterMetaData#getParameterCount()
	 */
	public int getParameterCount() throws OdaException 
	{
        // TODO replace with data source specific implementation

        // hard-coded for demo purpose
        return 1;
	}

    /*
	 * @see org.eclipse.datatools.connectivity.oda.IParameterMetaData#getParameterMode(int)
	 */
	public int getParameterMode( int param ) throws OdaException 
	{
        // TODO Auto-generated method stub
		return IParameterMetaData.parameterModeIn;
	}

    /* (non-Javadoc)
     * @see org.eclipse.datatools.connectivity.oda.IParameterMetaData#getParameterName(int)
     */
    public String getParameterName( int param ) throws OdaException
    {
        // TODO Auto-generated method stub
        return null;    // name is not available
    }

	/* 
	 * @see org.eclipse.datatools.connectivity.oda.IParameterMetaData#getParameterType(int)
	 */
	public int getParameterType( int param ) throws OdaException 
	{
        // TODO replace with data source specific implementation

        // hard-coded for demo purpose
        return java.sql.Types.CHAR;   // as defined in data set extension manifest
	}

	/* 
	 * @see org.eclipse.datatools.connectivity.oda.IParameterMetaData#getParameterTypeName(int)
	 */
	public String getParameterTypeName( int param ) throws OdaException 
	{
        int nativeTypeCode = getParameterType( param );
        return Driver.getNativeDataTypeName( nativeTypeCode );
	}

	/* 
	 * @see org.eclipse.datatools.connectivity.oda.IParameterMetaData#getPrecision(int)
	 */
	public int getPrecision( int param ) throws OdaException 
	{
        // TODO Auto-generated method stub
		return -1;
	}

	/* 
	 * @see org.eclipse.datatools.connectivity.oda.IParameterMetaData#getScale(int)
	 */
	public int getScale( int param ) throws OdaException 
	{
        // TODO Auto-generated method stub
		return -1;
	}

	/* 
	 * @see org.eclipse.datatools.connectivity.oda.IParameterMetaData#isNullable(int)
	 */
	public int isNullable( int param ) throws OdaException 
	{
        // TODO Auto-generated method stub
		return IParameterMetaData.parameterNullableUnknown;
	}

}
