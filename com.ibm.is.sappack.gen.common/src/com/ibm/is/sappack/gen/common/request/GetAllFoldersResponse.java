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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ibm.is.sappack.gen.common.Constants;
import com.ibm.is.sappack.gen.common.util.DSFolder;
import com.ibm.is.sappack.gen.common.util.XMLUtils;

/**
 * GetAllFoldersResponse
 * 
 * Response for a GetAllFoldersRequest. Contains a list
 * of DataStage folders 
 *
 */
public class GetAllFoldersResponse extends ResponseBase {
   
   private  static final String  XML_TAG_GET_ALL_FOLDERS_RESULT = "GetAllFoldersResult"; //$NON-NLS-1$
   private  static final String  XML_ATTRIB_COUNT               = "count";               //$NON-NLS-1$
   private  static final String  XML_TAG_FOLDER_LIST            = "FolderList";          //$NON-NLS-1$

	/* list of DataStage folders */
	private List<DSFolder> folders;

	
   public GetAllFoldersResponse(GetAllFoldersRequest requestType) {
      super(requestType.getClass());
   }
   
   public GetAllFoldersResponse(Node xmlNode) {
      super(xmlNode);
      
      Node folderResultsNode;
      
      // get response parameter from DOM
      folderResultsNode = XMLUtils.getChildNode(xmlNode, XML_TAG_GET_ALL_FOLDERS_RESULT);
      if (folderResultsNode != null) {
         
         // get the Folder list ...
         folders = createFolderListFromXML(XML_TAG_FOLDER_LIST, folderResultsNode);
      } // end of if (folderResultsNode != null)
   }

   private List<DSFolder> createFolderListFromXML(String jobTypeXMLTagName, Node parentNode) {
      List<DSFolder>  retFolderList;
      NodeList        folderNodes;
      Node            folderNode;
      Node            folderListNode;
      int             idx;

      retFolderList = new ArrayList<DSFolder>();
      
      folderListNode = XMLUtils.getChildNode(parentNode, jobTypeXMLTagName);
      if (folderListNode != null) {
         folderNodes = folderListNode.getChildNodes();
         
         for (idx = 0; idx < folderNodes.getLength(); idx++) {
            
            folderNode = folderNodes.item(idx);
            retFolderList.add(new DSFolder(folderNode));
         }
      } // end of if (folderListNode != null)
      
      return(retFolderList);
   }

   
	/**
	 * getFolders
	 * @return
	 */
	public List<DSFolder> getFolders() {
		return folders;
	}

	/**
	 * setFolders
	 * @param folders
	 */
	public void setFolders(List<DSFolder> folders) {
		this.folders = folders;
	}

	static String copyright() {
		return com.ibm.is.sappack.gen.common.request.Copyright.IBM_COPYRIGHT_SHORT;
	}

   protected String getTraceString() {
      StringBuffer traceBuffer = new StringBuffer();

      traceBuffer.append("Folder cnt = ");                  //$NON-NLS-1$
      if (folders == null)
      {
         traceBuffer.append("null");                        //$NON-NLS-1$
      }
      else
      {
         traceBuffer.append(folders.size());
      }
      traceBuffer.append(Constants.NEWLINE);
      
      return(traceBuffer.toString());
   } // end of getTraceString()
   
   protected String getXML() {
      DSFolder curFolder;
      StringBuffer xmlBuf;
      Iterator     listIter;
      Integer      folderCount;
      
      if (folders == null) {
         folderCount = null;
      }
      else {
         folderCount = new Integer(folders.size());
      }

      // build result XML ...
      xmlBuf = new StringBuffer();

      xmlBuf.append("<");                                      //$NON-NLS-1$
      xmlBuf.append(XML_TAG_GET_ALL_FOLDERS_RESULT);
      xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_COUNT, folderCount));
      xmlBuf.append(">");                                      //$NON-NLS-1$
      
      // - - - - - - - - - - - - - - - - - - List Of Folders - - - - - - - - - - - - - - - -
      xmlBuf.append("<");                                      //$NON-NLS-1$
      xmlBuf.append(XML_TAG_FOLDER_LIST);
      xmlBuf.append(">");                                      //$NON-NLS-1$
      if (folders != null) {
         listIter = folders.iterator();
         while(listIter.hasNext()) {
            curFolder = (DSFolder) listIter.next();
            
            xmlBuf.append(curFolder.getXML());
         }
      }
      xmlBuf.append("</");                                     //$NON-NLS-1$
      xmlBuf.append(XML_TAG_FOLDER_LIST);
      xmlBuf.append(">");                                      //$NON-NLS-1$
      
      xmlBuf.append("</");                                     //$NON-NLS-1$
      xmlBuf.append(XML_TAG_GET_ALL_FOLDERS_RESULT);
      xmlBuf.append(">");                                      //$NON-NLS-1$

      return (xmlBuf.toString());
   } // end of getXML()
	
}
