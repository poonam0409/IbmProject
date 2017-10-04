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

import com.ibm.is.sappack.gen.common.Constants;



public class GetAllProjectsRequest extends RequestBase {
	
	static final String XML_TAG_GETALLPROJECTS_TAG = "GetAllProjects";        //$NON-NLS-1$
	
	static String copyright()
	{ return com.ibm.is.sappack.gen.common.request.Copyright.IBM_COPYRIGHT_SHORT; }
	
	protected String toXML() {
		return "<" + XML_TAG_GETALLPROJECTS_TAG + "/>";                       //$NON-NLS-1$ //$NON-NLS-2$
	}

   protected String getTraceString()
   {
      StringBuffer traceBuffer = new StringBuffer();
      
      traceBuffer.append(XML_TAG_GETALLPROJECTS_TAG);
      traceBuffer.append(Constants.NEWLINE);
      
      return(traceBuffer.toString());
   }
	
}
