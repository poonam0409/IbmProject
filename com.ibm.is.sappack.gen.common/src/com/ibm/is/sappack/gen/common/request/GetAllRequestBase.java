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

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ibm.is.sappack.gen.common.Constants;
import com.ibm.is.sappack.gen.common.util.XMLUtils;

/**
 * GetAllFolderRequest
 * 
 * Request to get all DataStage Folders for a given DataStage project
 * 
 */
public abstract class GetAllRequestBase extends RequestBase {

   public static final String XML_TAG_DS_PROJECT = JobGeneratorRequest.XML_TAG_DS_PROJECT;
   public static final String XML_ATTRIB_DSHOST_NAME = JobGeneratorRequest.XML_ATTRIB_DSHOST_NAME;
   public static final String XML_ATTRIB_DSSRVR_RPC_PORT = JobGeneratorRequest.XML_ATTRIB_DSSRVR_RPC_PORT;
   public static final String XML_ATTRIB_PROJECT_NAME = JobGeneratorRequest.XML_ATTRIB_PROJECT_NAME;
   
	private String  dsProjectName = "";
   private String  dsHostName = "";
   private Integer dsServerRPCPort = null;

   
   /**
    * getDSHostName
    * 
    * @return
    */
   public String getDSHostName() {
      return this.dsHostName;
   }

	/**
	 * getDSProjectName
	 * 
	 * @return
	 */
	public String getDSProjectName() {
		return this.dsProjectName;
	}

   /**
    * getDSServerRPCPort
    * 
    * @return
    */
   public Integer getDSServerRPCPort() {
      return this.dsServerRPCPort;
   }

	/**
	 * setDSHostName
	 * 
	 * @param dsHostName
	 */
	public void setDSHostName(String dsHostName) {
		this.dsHostName = dsHostName;
	}

   /**
    * setDSProjectName
    * 
    * @param dsProjectName
    */
   public void setDSProjectName(String dsProjectName) {
      this.dsProjectName = dsProjectName;
   }

   /**
    * setDSServerRPCPort
    * 
    * @param dsSrvrRPCPort
    */
   public void setDSServerRPCPort(Integer dsSrvrRPCPort) {
      this.dsServerRPCPort = dsSrvrRPCPort;
   }

	static String copyright() {
		return com.ibm.is.sappack.gen.common.request.Copyright.IBM_COPYRIGHT_SHORT;
	}

	/**
	 * readGetAllParameterSets Params
	 * 
	 * get the xml child nodes and attributes of the 'server and project data' node
	 * 
	 * @param node
	 */
	protected void readProjectParams(Node node) {

		NodeList childNodes = node.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node childNode = childNodes.item(i);
			if (childNode.getNodeName().equals(XML_TAG_DS_PROJECT)) {
            this.dsHostName = getNodeAttributeValue(childNode, XML_ATTRIB_DSHOST_NAME);
            String vTmpIntString = getNodeAttributeValue(childNode, XML_ATTRIB_DSSRVR_RPC_PORT);
            if (vTmpIntString != null) {
               this.dsServerRPCPort = Integer.valueOf(vTmpIntString);
            }
            this.dsProjectName = getNodeAttributeValue(childNode, XML_ATTRIB_PROJECT_NAME);
			}
		}
	}

	protected String getTraceString() {

		StringBuffer traceBuffer = new StringBuffer();
      traceBuffer.append("DS Host = ");                           //$NON-NLS-1$
      traceBuffer.append(dsHostName);
      traceBuffer.append(Constants.NEWLINE);
      traceBuffer.append("DS Server RPC Port = ");                //$NON-NLS-1$
      if (this.dsServerRPCPort == null) {
         traceBuffer.append("-");                                 //$NON-NLS-1$
      }
      else {
         traceBuffer.append(this.dsServerRPCPort);
      }
      traceBuffer.append(Constants.NEWLINE);
      traceBuffer.append("Project Name = ");                      //$NON-NLS-1$
      traceBuffer.append(dsProjectName);
		traceBuffer.append(Constants.NEWLINE);
		return (traceBuffer.toString());
	}

	protected String toXML() {
		StringBuffer xmlBuf = new StringBuffer();

		/* DSProject */
		xmlBuf.append("<");                                          //$NON-NLS-1$
		xmlBuf.append(XML_TAG_DS_PROJECT);
      xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_DSHOST_NAME, this.dsHostName));
      xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_DSSRVR_RPC_PORT, this.dsServerRPCPort));
		xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_PROJECT_NAME, this.dsProjectName));
		xmlBuf.append(" />");                                        //$NON-NLS-1$
		
		return xmlBuf.toString();
	}

} // end of class GetAllRequestBase
