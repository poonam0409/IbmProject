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
// Module Name : com.ibm.is.sappack.gen.common.util
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.common.util;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ibm.is.sappack.gen.common.request.JobParamData;



public final class DSParamSet 
{
   // -------------------------------------------------------------------------------------
   //                                       Constants
   // -------------------------------------------------------------------------------------
   private  static final String  XML_TAG_PARAMSET        = "ParamSet";          //$NON-NLS-1$
   private  static final String  XML_ATTRIB_ID           = "id";                //$NON-NLS-1$
   private  static final String  XML_ATTRIB_NAME         = "name";              //$NON-NLS-1$
   private  static final String  XML_ATTRIB_PROJECT_NS   = "projectnameSpace";  //$NON-NLS-1$
   private  static final String  XML_TAG_PARAMETERS      = "Parameters";        //$NON-NLS-1$
   

   // -------------------------------------------------------------------------------------
   //                                 Member Variables
   // -------------------------------------------------------------------------------------
   private String    _Id;
   private String    _Name;
   private String    _ProjectNameSpace;
   private Map       _JobParamMap;

   
   static String copyright()
   { 
      return com.ibm.is.sappack.gen.common.util.Copyright.IBM_COPYRIGHT_SHORT; 
   }

   
   /**
    * Constructor
    */
   public DSParamSet(String id, String name, String projectNS)
   {
      _Id               = id;
      _Name             = name;
      _ProjectNameSpace = projectNS;
      _JobParamMap     = new HashMap();
   } // end of DSParamSet()
   

   public DSParamSet(Node paramSetNode)
   {
      this(null, null, null);

      JobParamData newJobParam;
      Node         parametersNode;
      NodeList     paramNodeList;
      int          listIdx;
      
      // get ParamSet data from DOM
      if (paramSetNode.getNodeName().equals(XML_TAG_PARAMSET))
      {
         _Id               = XMLUtils.getNodeAttributeValue(paramSetNode, XML_ATTRIB_ID);
         _Name             = XMLUtils.getNodeAttributeValue(paramSetNode, XML_ATTRIB_NAME);
         _ProjectNameSpace = XMLUtils.getNodeAttributeValue(paramSetNode, XML_ATTRIB_PROJECT_NS);
         
         // get the parameters
         _JobParamMap  = new HashMap();
         parametersNode = XMLUtils.getChildNode(paramSetNode, XML_TAG_PARAMETERS);
         if (parametersNode != null)
         {
            paramNodeList = parametersNode.getChildNodes();
            for(listIdx = 0; listIdx < paramNodeList.getLength(); listIdx ++)
            {
               newJobParam = new JobParamData(paramNodeList.item(listIdx));
               _JobParamMap.put(newJobParam.getName(), newJobParam);
            }
         } // end of if (parametersNode != null)
      } // end of if (folderNode.getNodeName().equals(XML_TAG_FOLDER))
      
   } // end of DSParamSet()

   
   public void addJobParam(JobParamData pJobParam)
   {
      if (pJobParam != null)
      {
         _JobParamMap.put(pJobParam.getName(), pJobParam);
      }
   } // end of addJobParam()

   
   public String getId()
   {
      return(_Id);
   } // end of getId()

   
   public String getName()
   {
      return(_Name);
   } // end of getName()

   
   public Map getParams()
   {
      return(_JobParamMap);
   } // end of getParams()

   
   public String getProjectNameSpace()
   {
      return(_ProjectNameSpace);
   } // end of getProjectNameSpace()

   
   public String getXML()
   {
      StringBuffer xmlBuf;
      Iterator     paramListIter;
      
      // build XML ...
      xmlBuf = new StringBuffer();
      
      xmlBuf.append("<");                                                                    //$NON-NLS-1$
      xmlBuf.append(XML_TAG_PARAMSET);
      xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_ID, _Id));
      xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_NAME, _Name));
      xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_PROJECT_NS, _ProjectNameSpace));
      xmlBuf.append(">");                                                                    //$NON-NLS-1$
      
      // add parameter data ...
      xmlBuf.append("<");                                                                    //$NON-NLS-1$
      xmlBuf.append(XML_TAG_PARAMETERS);
      xmlBuf.append(">");                                                                    //$NON-NLS-1$
      paramListIter = _JobParamMap.values().iterator();
      while (paramListIter.hasNext())
      {
         xmlBuf.append(((JobParamData) paramListIter.next()).toXML());
      }
      xmlBuf.append("</");                                                                   //$NON-NLS-1$
      xmlBuf.append(XML_TAG_PARAMETERS);
      xmlBuf.append(">");                                                                    //$NON-NLS-1$
      
      xmlBuf.append("</" + XML_TAG_PARAMSET + ">");                                          //$NON-NLS-1$ //$NON-NLS-2$
      
      return(xmlBuf.toString());
   } // end of getXML()
   
   
   public void setId(String id)
   {
      _Id = id;
   } // end of setId()
   
   
   public String toString()
   {
      StringBuffer toStringBuf;
      
      toStringBuf = new StringBuffer();
      toStringBuf.append(_Name);
      toStringBuf.append(": ");                                                              //$NON-NLS-1$
      toStringBuf.append(_JobParamMap);
      
      return(toStringBuf.toString());
   } // end of toString()
   
} // end of class DSParamSet
