//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2009, 2014                                              
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


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ibm.is.sappack.gen.common.Constants;
import com.ibm.is.sappack.gen.common.util.XMLUtils;


public class GetAllProjectsResponse extends ResponseBase {
   private  static final String  XML_TAG_GET_ALL_PROJECTS_RESULT = "GetAllFoldersResult";    //$NON-NLS-1$
   private  static final String  XML_ATTRIB_COUNT                = "count";                  //$NON-NLS-1$
   private  static final String  XML_TAG_PROJECT_LIST            = "ProjectList";            //$NON-NLS-1$
   private  static final String  XML_TAG_PROJECT                 = "Project";                //$NON-NLS-1$

	private List<String> projects;

	
	static String copyright() { 
	   return com.ibm.is.sappack.gen.common.request.Copyright.IBM_COPYRIGHT_SHORT; 
	}

   public GetAllProjectsResponse(GetAllProjectsRequest requestType) {
      super(requestType.getClass());
   }
   
   public GetAllProjectsResponse(Node xmlNode) {
      super(xmlNode);
      
      Node projResultsNode;
      
      // get response parameter from DOM
      projResultsNode = XMLUtils.getChildNode(xmlNode, XML_TAG_GET_ALL_PROJECTS_RESULT);
      if (projResultsNode != null) {
         
         // get the Project list ...
         projects = createProjectListFromXML(XML_TAG_PROJECT_LIST, projResultsNode);
      } // end of if (projResultsNode != null)
   }

   private List<String> createProjectListFromXML(String jobTypeXMLTagName, Node parentNode) {
      List<String>  retProjectList;
      NodeList      projNodes;
      Node          projNode;
      Node          projListNode;
      int           idx;

      retProjectList = new ArrayList<String>();
      
      projListNode = XMLUtils.getChildNode(parentNode, jobTypeXMLTagName);
      if (projListNode != null) {
         projNodes = projListNode.getChildNodes();
         
         for (idx = 0; idx < projNodes.getLength(); idx++) {
            
            projNode = projNodes.item(idx);
            retProjectList.add(XMLUtils.getNodeTextValue(projNode));
         }
      } // end of if (projListNode != null)
      
      return(retProjectList);
   }

   public List<String> getProjects() {
      return projects;
   }

	public void setProjects(List<String> projects) {
		this.projects = projects;
	}
	
   protected String getTraceString() {
      StringBuffer traceBuffer = new StringBuffer();

      traceBuffer.append("Projects cnt = ");                                     //$NON-NLS-1$
      if (projects == null)
      {
         traceBuffer.append("null");                                             //$NON-NLS-1$
      }
      else
      {
         traceBuffer.append(projects.size());
      }
      traceBuffer.append(Constants.NEWLINE);
      
      return(traceBuffer.toString());
   } // end of getTraceString()

   
   protected String getXML() {
      StringBuffer xmlBuf;
      Iterator     listIter;
      Integer      projectCount;
      
      if (projects == null) {
         projectCount = null;
      }
      else {
         projectCount = new Integer(projects.size());
      }

      // build result XML ...
      xmlBuf = new StringBuffer();

      xmlBuf.append("<");                                                        //$NON-NLS-1$
      xmlBuf.append(XML_TAG_GET_ALL_PROJECTS_RESULT);
      xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_COUNT, projectCount));
      xmlBuf.append(">");                                                        //$NON-NLS-1$
      
      // - - - - - - - - - - - - - - - - - - List Of Projects - - - - - - - - - - - - - - - -
      xmlBuf.append("<");                                                        //$NON-NLS-1$
      xmlBuf.append(XML_TAG_PROJECT_LIST);
      xmlBuf.append(">");                                                        //$NON-NLS-1$
      if (projects != null) {
         listIter = projects.iterator();
         while(listIter.hasNext()) {
         	xmlBuf.append(XMLUtils.createCDATAElement(XML_TAG_PROJECT, (String) listIter.next()));
         }
      }
      xmlBuf.append("</");                                                       //$NON-NLS-1$
      xmlBuf.append(XML_TAG_PROJECT_LIST);
      xmlBuf.append(">");                                                        //$NON-NLS-1$
      
      xmlBuf.append("</");                                                       //$NON-NLS-1$
      xmlBuf.append(XML_TAG_GET_ALL_PROJECTS_RESULT);
      xmlBuf.append(">");                                                        //$NON-NLS-1$

      return (xmlBuf.toString());
   } // end of getXML()
	
}
