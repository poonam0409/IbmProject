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
package com.ibm.is.sappack.deltaextractstage.commons;

import java.io.IOException;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DsExtractorProcessState {

    private static final ConcurrentHashMap<String, DsExtractorCommonResource> extractorSharedObj = new ConcurrentHashMap<String, DsExtractorCommonResource>();
    private static final Map<String, Connection> stagingConnectionMap = new HashMap<String, Connection>();
//    private static final Map stagingConnectionMap = new WeakHashMap();
    private static boolean serverStopped = false;
    private static boolean serverStarted = false;

    public static void addStagingConnectionObject(String sessionId, Connection stageConnection) throws IOException {
        stagingConnectionMap.put(sessionId, stageConnection);
    }

    public static Connection fetchStagingConnectionObject(String sessionId) throws IOException {
        return (Connection) ((stagingConnectionMap.get(sessionId) == null) ? null : stagingConnectionMap.get(sessionId));

    }

    public static void removeStagingConnectionObject(String sessionId) {
        stagingConnectionMap.remove(sessionId);
    }

    public static int getStagingConnectionCount() {
        return stagingConnectionMap.size();
    }
    
    public static ConcurrentHashMap<String, DsExtractorCommonResource> getExtractorSharedObj() {
        return extractorSharedObj;
    }

    public static boolean isServerStopped() {
        return serverStopped;
    }

    public static void setServerStopped(boolean aServerStopped) {
        serverStopped = aServerStopped;
    }

    /**
     * @return the serverStarted
     */
    public static boolean isServerStarted() {
        return serverStarted;
    }

    /**
     * @param aServerStarted the serverStarted to set
     */
    public static void setServerStarted(boolean aServerStarted) {
        serverStarted = aServerStarted;
    }
}
