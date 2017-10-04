//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2011, 2012                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.is.sappack.gen.common.util
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.common.util;


import java.text.MessageFormat;

import org.w3c.dom.Node;

import com.ibm.is.sappack.gen.common.Constants;


public final class DSFolder 
{
   // -------------------------------------------------------------------------------------
   //                                       Constants
   // -------------------------------------------------------------------------------------
   public  static final String  DIRECTORY_SEPARATOR      = "\\\\";       //$NON-NLS-1$
   public  static final String  DEFAULT_DS_FOLDER_NAME   = Constants.DS_JOBS_DEFAULT_FOLDER;

   private  static final String  XML_TAG_FOLDER          = "Folder";     //$NON-NLS-1$
   private  static final String  XML_ATTRIB_ID           = "id";         //$NON-NLS-1$
   private  static final String  XML_ATTRIB_PARENT_ID    = "parentId";   //$NON-NLS-1$
   private  static final String  XML_ATTRIB_PROJECT_ID   = "projectId";  //$NON-NLS-1$
   private  static final String  XML_ATTRIB_CATEGORY     = "category";   //$NON-NLS-1$
   

   // -------------------------------------------------------------------------------------
   //                                 Member Variables
   // -------------------------------------------------------------------------------------
   private String    _Category;
   private String    _Id;
   private String    _Name;
   private String    _ProjectId;
   private boolean   _IsSubFolder;
   private String    _ParentId;

   
   static String copyright()
   { 
      return com.ibm.is.sappack.gen.common.util.Copyright.IBM_COPYRIGHT_SHORT; 
   }

   
   /**
    * Constructor
    */
   public DSFolder(String id, String name, String projectRid, String category)
   {
      _Category    = category;
      _Id          = id;
      _Name        = name;
      _ProjectId   = projectRid;
      _ParentId    = null;
      _IsSubFolder = false;
   } // end of DSFolder()
   

   public DSFolder(Node folderNode)
   {
      this(null, null, null, null);
      
      // get folder data from DOM
      if (folderNode.getNodeName().equals(XML_TAG_FOLDER))
      {
         _Category  = XMLUtils.getNodeAttributeValue(folderNode, XML_ATTRIB_CATEGORY);
         _Id        = XMLUtils.getNodeAttributeValue(folderNode, XML_ATTRIB_ID);
         _ParentId  = XMLUtils.getNodeAttributeValue(folderNode, XML_ATTRIB_PARENT_ID);
         _ProjectId = XMLUtils.getNodeAttributeValue(folderNode, XML_ATTRIB_PROJECT_ID);
         _Name      = XMLUtils.getNodeTextValue(folderNode);
      } // end of if (folderNode.getNodeName().equals(XML_TAG_FOLDER))
      
   } // end of DSFolder()

   
   public String getDirectoryPath()
   {
      String dirPath;
      
      if (_IsSubFolder)
      {
         dirPath = DIRECTORY_SEPARATOR + DEFAULT_DS_FOLDER_NAME; 
      }
      else
      {
         if (_Category == null) 
         {
            dirPath = "";
         }
         else
         {
            if (_Category.equals(DIRECTORY_SEPARATOR))
            {
               dirPath = "";
            }
            else
            {
               dirPath = _Category;
            }
         }
      }
      dirPath = dirPath + DIRECTORY_SEPARATOR + _Name;
      
      return(dirPath);
   } // end of getDirectoryPath()
   
   
   public String getCategory()
   {
      return(_Category);
   } // end of getCategory()


   public String getId()
   {
      return(_Id);
   } // end of getId()


   public boolean getIsSubFolder()
   {
      return(_IsSubFolder );
   } // end of setIsSubFolder()


   public String getName()
   {
      return(_Name);
   } // end of getName()


   public String getParentId()
   {
      return(_ParentId);
   } // end of getParentId()


   public String getProjectRid()
   {
      return(_ProjectId);
   } // end of getProjectRid()


   public String getXML()
   {
      StringBuffer xmlBuf;
      
      // build XML ...
      xmlBuf = new StringBuffer();
      
      xmlBuf.append("<");
      xmlBuf.append(XML_TAG_FOLDER);
      xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_CATEGORY, _Category));
      xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_ID, _Id));
      xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_PARENT_ID, _ParentId));
      xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_PROJECT_ID, _ProjectId));
      xmlBuf.append(">");
      xmlBuf.append(MessageFormat.format(XMLUtils.XML_CDATA_TEMPLATE, new Object[] { _Name } ));
      xmlBuf.append("</" + XML_TAG_FOLDER + ">");
      
      return(xmlBuf.toString());
   } // end of getXML()


   public void setId(String id)
   {
      _Id = id;
   } // end of setId()


   public void setIsSubFolder(boolean isSubFolder)
   {
      _IsSubFolder = isSubFolder;
   } // end of setIsSubFolder()


   public void setParentId(String parentId)
   {
      _ParentId = parentId;
   } // end of setParentId()


   public String toString()
   {
      return(String.valueOf(_Name));
   }
   
} // end of class DSFolder
