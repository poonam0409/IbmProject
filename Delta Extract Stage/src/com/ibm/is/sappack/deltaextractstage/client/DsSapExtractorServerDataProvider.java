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


import java.util.Hashtable;
import java.util.Properties;

import com.sap.conn.jco.ext.ServerDataEventListener;
import com.sap.conn.jco.ext.ServerDataProvider;

/**
 * ServerDataProvider provides server configurations to the JCo runtime. 
 * The JCo runtime uses the registered instance to get all server 
 * configuration information.
 * @author 
 *
 */
public class DsSapExtractorServerDataProvider implements ServerDataProvider {

    /**SAPALEServerDataProvider instance */
    private static DsSapExtractorServerDataProvider sapAleServerDataProvider = null;
    /**SAP Server pool list*/
    private static Hashtable<String, Properties> serverConnectionPoolList = new Hashtable<String, Properties>();
    /**
     * Registers a provider for server data and store each connection pool name in 
     * Hashtable for communication with specified SAP server.
     * 
     * @param poolName
     * @param conProperties
     */
    public static void defineServer(String poolName, Properties conProperties) {
        // Do we need to delete the connection pool?
        if (sapAleServerDataProvider == null) {
            sapAleServerDataProvider = new DsSapExtractorServerDataProvider();
            com.sap.conn.jco.ext.Environment.registerServerDataProvider(sapAleServerDataProvider);
        }
        if (conProperties == null) {
            serverConnectionPoolList.remove(poolName);
        } else {
            serverConnectionPoolList.put(poolName, conProperties);
        }
    }//end of defineServer() method

    @Override
    /**
     * Return a properties object that contains a subset of the supported server 
     * properties representing the configuration of the given server. null means 
     * that the server configuration is not available. In that case JCoException 
     * with key JCO_ERROR_RESOURCE will be thrown.
     */
    public Properties getServerProperties(String serverName) {

        Properties p = (Properties) serverConnectionPoolList.get(serverName);

        // Is this an unknown SAP server?
        if (p == null) {
            //alternatively throw runtime exception
            throw new RuntimeException("Server " + serverName + " is not defined.");
        };
        return (p);
    }

    @Override
    /**
     * This method sets a ServerDataEventListener implemented by JCo that processes 
     * the fired events within the JCo runtime. The JCo runtime will register only 
     * a single ServerDataEventListener instance.
     */
    public void setServerDataEventListener(ServerDataEventListener eventListener) {

    }

    @Override
    /**
     * Returns true if the implementation can support ServerDataEvents that allow 
     * a better integration into the JCo runtime resource management. If the 
     * implementation cannot support events it should return false.
     */
    public boolean supportsEvents() {
        // TODO Auto-generated method stub
        return true;
    }
}
