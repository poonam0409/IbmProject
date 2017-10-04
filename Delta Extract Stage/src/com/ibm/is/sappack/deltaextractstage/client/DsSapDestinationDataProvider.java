//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2015                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.is.sappack.dsstages.common
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************

package com.ibm.is.sappack.deltaextractstage.client;

import com.sap.conn.jco.ext.*;
import java.util.Hashtable;
import java.util.Properties;

public class DsSapDestinationDataProvider
    implements DestinationDataProvider
{

    private static Hashtable<String, Properties> conPoolHashTable = new Hashtable<String, Properties>();
    private static DsSapDestinationDataProvider dsSapDestinationDataProvider = null;

    public DsSapDestinationDataProvider()
    {
    }

    public boolean supportsEvents()
    {
        return false;
    }

    public static void defineDestination(String poolName, Properties conProperties)
    {
        if(dsSapDestinationDataProvider == null)
        {
        	dsSapDestinationDataProvider = new DsSapDestinationDataProvider();
            Environment.registerDestinationDataProvider(dsSapDestinationDataProvider);
        }
        if(conProperties == null)
        {
            conPoolHashTable.remove(poolName);
        } else
        {
            conPoolHashTable.put(poolName, conProperties);
        }
    }

    public Properties getDestinationProperties(String destinationName)
    {
        Properties p = (Properties)conPoolHashTable.get(destinationName);
        if(p == null)
        {
            throw new RuntimeException((new StringBuilder()).append("Destination ").append(destinationName).append(" is not defined.").toString());
        } else
        {
            return p;
        }
    }

    public void setDestinationDataEventListener(DestinationDataEventListener destinationdataeventlistener)
    {
    }

}
