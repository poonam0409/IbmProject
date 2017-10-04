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

import com.ibm.is.sappack.gen.common.JobGeneratorException;


/**
 * GetAllParameterSetsRequest
 * 
 * Request to get all DataStage ParameterSets for a given DataStage project
 * 
 */
public class GetAllParameterSetsRequest extends GetAllRequestBase {

	static final String XML_TAG_GETALL_PARAMETER_SETS_TAG = "GetAllParameterSets"; //$NON-NLS-1$

	
	
	static String copyright() {
		return com.ibm.is.sappack.gen.common.request.Copyright.IBM_COPYRIGHT_SHORT;
	}

	public GetAllParameterSetsRequest() {
	} // end of GetAllParameterSetsRequest()
	
	
	/**
	 * initConfiguration
	 */
	public void initConfiguration(Element configNode) throws JobGeneratorException {

		super.initConfiguration(configNode);

		// get the nodes to be processed ...
		NodeList vConfigNodes = configNode.getChildNodes();

		for (int vNodeListIdx = 0; vNodeListIdx < vConfigNodes.getLength(); vNodeListIdx++) {
		   
			Node     curNode    = vConfigNodes.item(vNodeListIdx);
			NodeList childNodes = curNode.getChildNodes();
			
			for (int i = 0; i < childNodes.getLength(); i++) {
			   
				Node childNode = childNodes.item(i);
				if (childNode.getNodeName().equals(XML_TAG_GETALL_PARAMETER_SETS_TAG)) {
					readProjectParams(childNode);
				}
			}
		}
	} // end of initConfiguration()

	protected String getTraceString() {

		StringBuffer traceBuffer = new StringBuffer();
		traceBuffer.append(XML_TAG_GETALL_PARAMETER_SETS_TAG);
      traceBuffer.append(super.getTraceString());
      
		return (traceBuffer.toString());
	} // end of getTraceString()
	

	protected String toXML() {
		StringBuffer xmlBuf = new StringBuffer();

		xmlBuf.append("<");                                    //$NON-NLS-1$
		xmlBuf.append(XML_TAG_GETALL_PARAMETER_SETS_TAG);
		xmlBuf.append(">");                                    //$NON-NLS-1$
		
		/* DSProject */
		xmlBuf.append(super.toXML());
		
		/* </GetAllFolders> */
		xmlBuf.append("</");                                   //$NON-NLS-1$
		xmlBuf.append(XML_TAG_GETALL_PARAMETER_SETS_TAG);
		xmlBuf.append(">");                                    //$NON-NLS-1$
		
		return xmlBuf.toString();
	} // end of toXML()

} // end of class GetAllParameterSetsRequest
