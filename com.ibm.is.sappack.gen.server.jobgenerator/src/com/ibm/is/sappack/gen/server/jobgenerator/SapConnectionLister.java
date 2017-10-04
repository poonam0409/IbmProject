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
// Module Name : com.ibm.is.sappack.gen.server.jobgenerator
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************

package com.ibm.is.sappack.gen.server.jobgenerator;


import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ibm.is.sappack.gen.common.JobGeneratorException;
import com.ibm.is.sappack.gen.common.request.GetAllSapConnectionsRequest;
import com.ibm.is.sappack.gen.common.request.GetAllSapConnectionsResponse;
import com.ibm.is.sappack.gen.common.request.ResponseBase;
import com.ibm.is.sappack.gen.common.trace.TraceLogger;
import com.ibm.is.sappack.gen.common.util.XMLUtils;
import com.ibm.is.sappack.gen.server.common.service.ServiceToken;
import com.ibm.is.sappack.gen.server.common.util.DSAccessException;
import com.ibm.is.sappack.gen.server.common.util.DSSapConnectionFileConverter;
import com.ibm.is.sappack.gen.server.datastage.DataStageAccessManager;


/**
 * SapConnectionLister
 */
public class SapConnectionLister {

	private GetAllSapConnectionsRequest request;
   private ResponseBase                response;

	static String copyright() {
		return com.ibm.is.sappack.gen.server.jobgenerator.Copyright.IBM_COPYRIGHT_SHORT;
	}

	/**
	 * SapConnectionLister
	 * 
	 * @param gaRequest
	 * @throws JobGeneratorException
	 */
	public SapConnectionLister(GetAllSapConnectionsRequest gaRequest, GetAllSapConnectionsResponse  response) throws DSAccessException {
		this.request = gaRequest;
      this.response = response;

      DataStageAccessManager.createInstance();
      
	}

	
   /**
    * getSAPConnections
    * 
    * @throws JobGeneratorException
    */
   public Map getSAPConnections() throws DSAccessException {

      String curSAPConnectionsXML;
      Map    retSAPConnectionsMap;
      
      TraceLogger.entry();
      
      DataStageAccessManager accessManager = DataStageAccessManager.getInstance();
      ServiceToken           srvcToken     = accessManager.createServiceToken(request.getLocale());
      srvcToken.loginIntoDomain(request.getISUsername(), request.getISPassword(), 
                                request.getDomainServerName(), request.getDomainServerPort());
   
      curSAPConnectionsXML = accessManager.getAllSapConnections(request.getDSHostName(), 
                                                                request.getDSServerRPCPort(), 
                                                                request.getDSProjectName(), 
                                                                srvcToken);
      
      retSAPConnectionsMap = convertDSSapConnectionsXMLToMap(curSAPConnectionsXML);

      // store the DS version
      if (response != null) {
         response.setDSServerVersion(accessManager.getDSVersion());
      }
      
      srvcToken.logoutFromDomain();
      
      TraceLogger.exit("connections map size = " + retSAPConnectionsMap.size());
      
      return(retSAPConnectionsMap);
   }

   /**
    * convertDSSapConnectionsXMLToMap
    * 
    * converts the given DataStage SAP connections xml string into a HashMap
    * 
    * @param xml
    * @return
    */
   private Map convertDSSapConnectionsXMLToMap(String xml) {

      Map<String, Map<String,String>> connections = new HashMap<String, Map<String,String>>();

      TraceLogger.entry("xml = " + xml);
      
      if(!xml.equals(DSSapConnectionFileConverter.XMLTAG_NO_DSSAPCONNECTIONS)) {
         
         try {
            Node rootNode = XMLUtils.getRootElementFromXML(xml);

            NodeList connectionNodes = rootNode.getChildNodes();

            /* convert connections to map entries */
            for (int i = 0; i < connectionNodes.getLength(); i++) {
               Map<String,String> connectionMap = new HashMap<String,String>();
               Node node = connectionNodes.item(i);
               /* handle node attributes */
               NamedNodeMap attributes = node.getAttributes();
               String connectionName = "";
               for (int a = 0; a < attributes.getLength(); a++) {
                  Node attribute = attributes.item(a);
                  
                  connectionMap.put(attribute.getNodeName(), attribute.getNodeValue());
                  
                  /* use connection name as key for the map entry */
                  if (attribute.getNodeName().equals(GetAllSapConnectionsResponse.CONNECTION_NAME_ATTRIBUTE)) {
                     connectionName = attribute.getNodeValue();
                  }
               }
               if(!connectionName.equals("")) {
                  connections.put(connectionName, connectionMap); 
               }
               
            }

         } catch (Exception e) {
            // something went wrong - return an empty Map
            TraceLogger.traceException(e);
            connections.clear();
         }
      }
      
      TraceLogger.exit("n connections = " + connections.size());
      
      return connections;
   }
	
}
