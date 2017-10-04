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
// Module Name : com.ibm.is.sappack.gen.common.request
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.common.request;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ibm.is.sappack.gen.common.Constants;
import com.ibm.is.sappack.gen.common.util.XMLUtils;

/**
 * GetAllSapConnectionsResponse
 * 
 */
public class GetAllSapConnectionsResponse extends ResponseBase {

   private  static final String  XML_TAG_GET_ALL_CONNECTIONS_RESULT = "GetAllSAPConnectionsResult"; //$NON-NLS-1$
   private  static final String  XML_ATTRIB_COUNT                   = "count";                      //$NON-NLS-1$
   private  static final String  XML_TAG_CONNECTION_LIST            = "SAPConnectionList";          //$NON-NLS-1$
   private  static final String  XML_TAG_CONNECTION                 = "SAPConnection";              //$NON-NLS-1$
   private  static final String  XML_TAG_PARAM                      = "Param";                      //$NON-NLS-1$
   private  static final String  XML_ATTRIB_NAME                    = "name";                       //$NON-NLS-1$
   private  static final String  XML_ATTRIB_VALUE                   = "value";                      //$NON-NLS-1$
   
	public static final String GETALLSAPCONNECTIONS_CONNECTIONLIST = "SapConnectionList"; //$NON-NLS-1$
	public static final String CONNECTION_NAME_ATTRIBUTE = "NAME"; //$NON-NLS-1$
	public static final String SAP_APP_SERVER_ATTRIBUTE = "SAPAPPSERVER"; //$NON-NLS-1$
	public static final String SAP_MESS_SERVER_ATTRIBUTE = "SAPMESSERVER"; //$NON-NLS-1$
	public static final String LOAD_BALANCING_ATTRIBUTE = "USELOADBALANCING"; //$NON-NLS-1$
	public static final String LOAD_BALANCING_TRUE = "TRUE"; //$NON-NLS-1$
	
	private Map connectionsMap;

	public Map getConnectionsMap() {
		return connectionsMap;
	}

   public GetAllSapConnectionsResponse(GetAllSapConnectionsRequest requestType) {
      super(requestType.getClass());
   }
   
	
   public GetAllSapConnectionsResponse(Node xmlNode) {
      super(xmlNode);
      
      Node connectionResultsNode;
      
      // get response parameter from DOM
      connectionResultsNode = XMLUtils.getChildNode(xmlNode, XML_TAG_GET_ALL_CONNECTIONS_RESULT);
      if (connectionResultsNode != null) {
         
         // get the Connections list ...
         connectionsMap = createConnectionsMapFromXML(XML_TAG_CONNECTION_LIST, connectionResultsNode);
      } // end of if (connectionResultsNode != null)
   }

   private Map createConnectionsMapFromXML(String jobTypeXMLTagName, Node parentNode) {
      Map      retConnectionsMap;
      Map      connParamMap;
      NodeList connectionNodes;
      NodeList paramNodeList;
      Node     connNode;
      Node     parNode;
      Node     connectionListNode;
      String   connName;
      String   paramName;
      String   paramValue;
      int      connIdx;
      int      paramIdx;

      retConnectionsMap = new HashMap();
      
      connectionListNode = XMLUtils.getChildNode(parentNode, jobTypeXMLTagName);
      if (connectionListNode != null) {

         // process all connections
         connectionNodes = connectionListNode.getChildNodes();
         for (connIdx = 0; connIdx < connectionNodes.getLength(); connIdx++) {
            
            connNode      = connectionNodes.item(connIdx);
            connName      = XMLUtils.getNodeAttributeValue(connNode, XML_ATTRIB_NAME);
            paramNodeList = connNode.getChildNodes();  // parameters
            
            // process all connection parameters
            connParamMap = new HashMap();
            for (paramIdx = 0; paramIdx < paramNodeList.getLength(); paramIdx++) {
               
               parNode    = paramNodeList.item(paramIdx);
               paramName  = XMLUtils.getNodeAttributeValue(parNode, XML_ATTRIB_NAME);
               paramValue = XMLUtils.getNodeAttributeValue(parNode, XML_ATTRIB_VALUE);
               connParamMap.put(paramName, paramValue);
            } // end of for for (paramIdx = 0; paramIdx < paramNodeList.getLength(); paramIdx++)
            
            retConnectionsMap.put(connName, connParamMap);
         } // end of for (connIdx = 0; connIdx < connectionNodes.getLength(); connIdx++)
      } // end of if (connectionListNode != null)
      
      return(retConnectionsMap);
   }

   
	public void setConnectionsMap(Map connectionsMap) {
		this.connectionsMap = connectionsMap;
	}

	static String copyright() {
		return com.ibm.is.sappack.gen.common.request.Copyright.IBM_COPYRIGHT_SHORT;
	}

	
   protected String getTraceString() {
      StringBuffer traceBuffer = new StringBuffer();

      traceBuffer.append("Connection Map size = ");                                 //$NON-NLS-1$
      if (connectionsMap == null)
      {
         traceBuffer.append("null");                                                //$NON-NLS-1$
      }
      else
      {
         traceBuffer.append(connectionsMap.size());
      }
      traceBuffer.append(Constants.NEWLINE);
      
      return(traceBuffer.toString());
   } // end of getTraceString()

   
   protected String getXML() {
      StringBuffer xmlBuf;
      Map          tmpPramMap;
      Iterator     connectionIter;
      Iterator     paramIter;
      Map.Entry    connectionEntry;
      Map.Entry    paramEntry;
      Integer      connectionCount;
      
      if (connectionsMap == null) {
         connectionCount = null;
      }
      else {
         connectionCount = new Integer(connectionsMap.size());
      }

      // build result XML ...
      xmlBuf = new StringBuffer();

      xmlBuf.append("<");                                               //$NON-NLS-1$
      xmlBuf.append(XML_TAG_GET_ALL_CONNECTIONS_RESULT);
      xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_COUNT, connectionCount));
      xmlBuf.append(">");                                               //$NON-NLS-1$
      
      // - - - - - - - - - - - - - - - - - - List Of Projects - - - - - - - - - - - - - - - -
      xmlBuf.append("<");                                               //$NON-NLS-1$
      xmlBuf.append(XML_TAG_CONNECTION_LIST);
      xmlBuf.append(">");                                               //$NON-NLS-1$
      if (connectionsMap != null) {
         connectionIter = connectionsMap.entrySet().iterator();
         while(connectionIter.hasNext()) {
            connectionEntry = (Map.Entry) connectionIter.next();
            
            xmlBuf.append("<");                                         //$NON-NLS-1$
            xmlBuf.append(XML_TAG_CONNECTION);
            xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_NAME, 
                          (String) connectionEntry.getKey()));
            xmlBuf.append(">");                                         //$NON-NLS-1$
            tmpPramMap = (Map) connectionEntry.getValue();
            
            // get all connection parameters
            paramIter = tmpPramMap.entrySet().iterator();
            while(paramIter.hasNext()) {
               paramEntry = (Map.Entry) paramIter.next();
               xmlBuf.append("<");                                      //$NON-NLS-1$
               xmlBuf.append(XML_TAG_PARAM);
               xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_NAME, (String) paramEntry.getKey()));
               xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_VALUE, (String) paramEntry.getValue()));
               xmlBuf.append("/>");                                      //$NON-NLS-1$
            }
            
            xmlBuf.append("</" + XML_TAG_CONNECTION + ">");             //$NON-NLS-1$ $NON-NLS-2$
         }
      }
      xmlBuf.append("</");                                              //$NON-NLS-1$
      xmlBuf.append(XML_TAG_CONNECTION_LIST);
      xmlBuf.append(">");                                               //$NON-NLS-1$
      
      xmlBuf.append("</");                                              //$NON-NLS-1$
      xmlBuf.append(XML_TAG_GET_ALL_CONNECTIONS_RESULT);
      xmlBuf.append(">");                                               //$NON-NLS-1$

      return (xmlBuf.toString());
   } // end of getXML()
   
}
