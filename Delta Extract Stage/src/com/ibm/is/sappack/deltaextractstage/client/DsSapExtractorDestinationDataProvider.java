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

import java.util.Properties;
import com.sap.conn.jco.ext.DestinationDataEventListener;
import com.sap.conn.jco.ext.DestinationDataProvider;
import java.util.Hashtable;


public class DsSapExtractorDestinationDataProvider implements DestinationDataProvider {

    private static final Hashtable<String, Properties> conPoolHashTable = new Hashtable<String, Properties>();
    private static DsSapExtractorDestinationDataProvider dsSapDestinationDataProvider = null;

    /**
     * Returns true if the implementation can support DestinationDataEvents that 
     * allow a better integration into the JCo runtime management. If the implementation 
     * cannot support the events it should return false. If you're not sure about the ability 
     * that your DestinationDataProvider can guarantee to fire all events you can return false 
     * to be on the safe side.
     * 
     * @return boolean - whether the implementation supports DestinationDataEvents completely
     */
    @Override
    public boolean supportsEvents() { //Interface (DestinationDataProvider) method
        return false;
    }

    /**
     * Registers a provider for destination data and store each connection pool name in 
     * Hashtable for communication with specified SAP server.
     * 
     * @param poolName
     * @param conProperties
     */
    public synchronized static void defineDestination(String poolName, Properties conProperties) {
        // Do we need to delete the connection pool?
        if (dsSapDestinationDataProvider == null) {
            dsSapDestinationDataProvider = new DsSapExtractorDestinationDataProvider();
            com.sap.conn.jco.ext.Environment.registerDestinationDataProvider(dsSapDestinationDataProvider);
        }
        if (conProperties == null) {
            conPoolHashTable.remove(poolName);
        } else {
        	//conPoolHashTable.put(new String(poolName), new Properties(conProperties));//changed due to unable to connect to SAP Message Server Logon Group Server
            conPoolHashTable.put(poolName, conProperties);
        }
    }//end of  defineDestination

    /**
     * getDestinationProperties should return a properties object that 
     * contains a subset of the supported properties mentioned in the 
     * DestinationDataProvider interface representing the configuration 
     * of the given destination in your destination configuration repository 
     * 
     * @param destinationName
     * @return Properties of connection information
     */
    @Override
    public Properties getDestinationProperties(String destinationName) { //Interface (DestinationDataProvider) method

        Properties p = (Properties) conPoolHashTable.get(destinationName);

        // Is this an unknown destinationName?
        if (p == null) {
            //alternatively throw runtime exception
            throw new RuntimeException("Destination " + destinationName + " is not defined.");
        };
        //return new Properties(p);//changed due to unable to connect to SAP Message Server Logon Group Server
        return (p);
    }//end of getDestinationProperties() method

    /**
     * This method sets a DestinationDataEventListener implemented 
     * by JCo that processes the fired events within the JCo runtime.
     * 
     * @param arg0
     */
    @Override
    public void setDestinationDataEventListener(DestinationDataEventListener arg0) { //Interface (DestinationDataProvider) method
        // TODO Auto-generated method stub
    }
}//end of class 
