//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2009, 2013                                              
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

import com.ibm.is.sappack.gen.common.Constants;
import com.ibm.is.sappack.gen.common.JobGeneratorException;
import com.ibm.is.sappack.gen.common.util.XMLUtils;



public class ValidateDataRequest extends GetAllRequestBase {
	
   // -------------------------------------------------------------------------------------
   //                                       Constants
   // -------------------------------------------------------------------------------------
   private static final String XML_TAG_VALIDATE_DATA         = "ValidateData";         //$NON-NLS-1$
   private static final String XML_TAG_DS_FOLDER             = "DSFolder";             //$NON-NLS-1$
   private static final String XML_ATTRIB_NAME               = "name";                 //$NON-NLS-1$
   private static final String XML_TAG_DS_JOB_PREFIX         = "DSJobPrefix";          //$NON-NLS-1$


   // -------------------------------------------------------------------------------------
   //                                 Member Variables
   // -------------------------------------------------------------------------------------
   private String  _DSFolderName;
   private String  _DSJobNamePrefix;
   
	
	static String copyright() { 
	   return com.ibm.is.sappack.gen.common.request.Copyright.IBM_COPYRIGHT_SHORT; 
	}

	
	public ValidateDataRequest() {
		_DSJobNamePrefix  = null;
	   _DSFolderName     = null;
	} // end of ValidateDataRequest()

	
   /**
    * initConfiguration
    */
   public void initConfiguration(Element parentNode) throws JobGeneratorException {

      super.initConfiguration(parentNode);

      Node valProjDataNode = XMLUtils.getChildNode(parentNode, XML_TAG_VALIDATE_DATA);
      if (valProjDataNode != null) {
         
         // read common data ...
         readProjectParams(valProjDataNode);
         
         // read request attributes
         Node requestDataNode = XMLUtils.getChildNode(valProjDataNode, XML_TAG_DS_FOLDER);
         
         if(requestDataNode != null) {
            _DSFolderName     = XMLUtils.getNodeAttributeValue(requestDataNode, XML_ATTRIB_NAME);
         } // end of if(requestDatNode != null)

         requestDataNode = XMLUtils.getChildNode(valProjDataNode, XML_TAG_DS_JOB_PREFIX);
         if(requestDataNode != null) {
            _DSJobNamePrefix = XMLUtils.getNodeAttributeValue(requestDataNode, XML_ATTRIB_NAME);
         } // end of if(requestDatNode != null)
      } // end of if (valProjDataNode != null)
      
   } // end of initConfiguration()

   
   public String getDSFolder() {
      return(_DSFolderName);
   } // end of getDSFolder()
   
   
   public String getDSJobPrefix() {
      return(_DSJobNamePrefix);
   } // end of getDSJobPrefix()
   
   
   public void setDSFolderName(String dsFolder) {
      _DSFolderName  = dsFolder;
   } // end of setDSFolderName()

   
   public void setDSJobNamePrefix(String dsJobPrefix) {
      _DSJobNamePrefix = dsJobPrefix;
   } // end of setDSJobNamePrefix()

   
   protected String getTraceString() {

      StringBuffer traceBuffer = new StringBuffer();
      traceBuffer.append(XML_TAG_VALIDATE_DATA);
      traceBuffer.append(" - DS Job Name Prefix = ");                       //$NON-NLS-1$
      traceBuffer.append(_DSJobNamePrefix);
      traceBuffer.append(" - DS Folder = ");                                //$NON-NLS-1$
      traceBuffer.append(_DSFolderName);
      traceBuffer.append(Constants.NEWLINE);
      traceBuffer.append(super.getTraceString());

      return (traceBuffer.toString());
   } // end of getTraceString()
   
   
   protected String toXML() {
      StringBuffer xmlBuf = new StringBuffer();

      xmlBuf.append("<");                                    //$NON-NLS-1$
      xmlBuf.append(XML_TAG_VALIDATE_DATA);
      xmlBuf.append(">");                                    //$NON-NLS-1$
      xmlBuf.append("<");                                    //$NON-NLS-1$
      xmlBuf.append(XML_TAG_DS_JOB_PREFIX);
      xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_NAME, _DSJobNamePrefix));
      xmlBuf.append(" />");                                  //$NON-NLS-1$
      xmlBuf.append("<");                                    //$NON-NLS-1$
      xmlBuf.append(XML_TAG_DS_FOLDER);
      xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_NAME, _DSFolderName));
      xmlBuf.append(" />");                                  //$NON-NLS-1$
      
      // DSProject
      xmlBuf.append(super.toXML());
      
      xmlBuf.append("</");                                   //$NON-NLS-1$
      xmlBuf.append(XML_TAG_VALIDATE_DATA);
      xmlBuf.append(">");                                    //$NON-NLS-1$
      
      return xmlBuf.toString();
   } // end of toXML()

} // end of class ValidateDataRequest
