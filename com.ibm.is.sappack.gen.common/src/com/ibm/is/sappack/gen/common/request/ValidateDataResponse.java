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

import com.ibm.is.sappack.gen.common.Constants;
import com.ibm.is.sappack.gen.common.util.XMLUtils;


/**
 * ValidateProjectDataResponse
 *
 */
public class ValidateDataResponse extends ResponseBase {
 
	static String copyright() { 
	   return com.ibm.is.sappack.gen.common.request.Copyright.IBM_COPYRIGHT_SHORT; 
	}
	
   // -------------------------------------------------------------------------------------
   //                                       Constants
   // -------------------------------------------------------------------------------------
   private  static final String  XML_TAG_VALIDATE_DATA_RESULT = "ValidateProjectDataResult";   //$NON-NLS-1$
   private  static final String  XML_ATTRIB_SUCCESS           = "success";                     //$NON-NLS-1$


   // -------------------------------------------------------------------------------------
   //                                 Member Variables
   // -------------------------------------------------------------------------------------
	private boolean _ReqSuccessful;

	
   public ValidateDataResponse(ValidateDataRequest requestType) {
      super(requestType.getClass());
   } // end of ValidateDataResponse()
   
   
   public ValidateDataResponse(Node xmlNode) {
      super(xmlNode);
      
      Node requestResultNode;
      
      // get response parameter from DOM
      requestResultNode = XMLUtils.getChildNode(xmlNode, XML_TAG_VALIDATE_DATA_RESULT);
      if (requestResultNode != null) {
         
         // get the Folder list ...
         _ReqSuccessful = Boolean.valueOf(XMLUtils.getNodeAttributeValue(requestResultNode, XML_ATTRIB_SUCCESS)).booleanValue();
      } // end of if (folderResultsNode != null)
   } // end of ValidateDataResponse()
   

	public boolean isSuccessful() {
		return(_ReqSuccessful);
	} // end of isSuccessful()
	

   protected String getXML() {
      StringBuffer xmlBuf;
      
      // build result XML ...
      xmlBuf = new StringBuffer();

      xmlBuf.append("<");                                      //$NON-NLS-1$
      xmlBuf.append(XML_TAG_VALIDATE_DATA_RESULT);
      xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_SUCCESS, Boolean.valueOf(_ReqSuccessful)));
      xmlBuf.append(" />");                                      //$NON-NLS-1$
      
      return (xmlBuf.toString());
   } // end of getXML()


   public void setSuccessful(boolean success) {
      _ReqSuccessful = success;
   } // end of setSuccessful()

   
   protected String getTraceString() {
      StringBuffer traceBuffer = new StringBuffer();

      traceBuffer.append("success = ");                  //$NON-NLS-1$
      traceBuffer.append(_ReqSuccessful);
      traceBuffer.append(Constants.NEWLINE);
      
      return(traceBuffer.toString());
   } // end of getTraceString()
   
} // end of class ValidateDataResponse 
