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

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ibm.is.sappack.gen.common.Constants;
import com.ibm.is.sappack.gen.common.JobGeneratorException;

/**
 * GetAllSapConnectionsRequest
 *
 */
public class GetAllSapConnectionsRequest extends GetAllRequestBase {

	public static final String XML_TAG_GETALLSAPCONNECTIONS_TAG = "GetAllSapConnections"; //$NON-NLS-1$
	
	
	static String copyright()
	{ return com.ibm.is.sappack.gen.common.request.Copyright.IBM_COPYRIGHT_SHORT; }

	
	/**
	 * toXML
	 */
	protected String toXML() {
		
		StringBuffer xmlBuf = new StringBuffer();

		/* <GetAllSapConnections> */
		xmlBuf.append("<");
		xmlBuf.append(XML_TAG_GETALLSAPCONNECTIONS_TAG);
		xmlBuf.append(">");
		
		/* DSProject */
		xmlBuf.append(super.toXML());
		
		/* </GetAllSapConnections> */
		xmlBuf.append("</");
		xmlBuf.append(XML_TAG_GETALLSAPCONNECTIONS_TAG);
		xmlBuf.append(">");
		
		return xmlBuf.toString();
	}
	
	/**
	 * initConfiguration
	 */
	public void initConfiguration(Element configNode) throws JobGeneratorException {
	
		super.initConfiguration(configNode);

		// get the nodes to be processed ...
		NodeList vConfigNodes = configNode.getChildNodes();

		for (int vNodeListIdx = 0; vNodeListIdx < vConfigNodes.getLength(); vNodeListIdx++) {
			Node curNode = vConfigNodes.item(vNodeListIdx);
			NodeList childNodes = curNode.getChildNodes();
			for (int i = 0; i < childNodes.getLength(); i++) {
				Node childNode = childNodes.item(i);
				// <GetAllSapConnections> Node
				if (childNode.getNodeName().equals(XML_TAG_GETALLSAPCONNECTIONS_TAG)) {
					readProjectParams(childNode);	
				}
			}
			
		} 
		
	}
	
	protected String getTraceString() {
		
		StringBuffer traceBuffer = new StringBuffer();
	      
	      traceBuffer.append(XML_TAG_GETALLSAPCONNECTIONS_TAG);
	      traceBuffer.append(Constants.NEWLINE);
         traceBuffer.append(super.getTraceString());
	      
	      return(traceBuffer.toString());
		
	}
}
