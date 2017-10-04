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
// Module Name : com.ibm.is.sappack.gen.common.request
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.common.request;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ibm.is.sappack.gen.common.util.XMLUtils;


abstract class SupportedTypesBaseMap
{
   // -------------------------------------------------------------------------------------
   //                                       Constants
   // -------------------------------------------------------------------------------------
           static final String XML_ATTRIB_TYPE_TYPE_ID  = "typeId";
   
   
   // -------------------------------------------------------------------------------------
   //                                 Member Variables
   // -------------------------------------------------------------------------------------
   private Map<Integer,Integer>     _TypesMap;


   static String copyright()
   { 
      return com.ibm.is.sappack.gen.common.request.Copyright.IBM_COPYRIGHT_SHORT; 
   }


   SupportedTypesBaseMap()
   {
      _TypesMap = new HashMap<Integer,Integer>();
   } // end of SupportedTypesBaseMap()


   SupportedTypesBaseMap(SupportedTypesBaseMap instance)
   {
      _TypesMap = new HashMap<Integer, Integer>(instance._TypesMap);
   } // end of SupportedTypesBaseMap()


   SupportedTypesBaseMap(Node parParentNode)
   {
      NodeList supportedTypesNodeList;
      Node     supportedTypeNode;
      Integer  typeId;
      int      vNodeIdx;

      _TypesMap = new HashMap<Integer,Integer>();

      if (parParentNode != null)
      {
         // ==> Supported Table Types and Column Types
         supportedTypesNodeList = parParentNode.getChildNodes();
         
         for(vNodeIdx = 0; vNodeIdx < supportedTypesNodeList.getLength(); vNodeIdx ++)
         {
            supportedTypeNode = supportedTypesNodeList.item(vNodeIdx);
            
            if (supportedTypeNode.getNodeName().equals(getXMLTag()))
            {
               typeId = Integer.valueOf(XMLUtils.getNodeAttributeValue(supportedTypeNode, XML_ATTRIB_TYPE_TYPE_ID));
               _TypesMap.put(typeId, typeId);
            }
         } // end of for(vNodeIdx = 0; vNodeIdx < supportedTypesNodeList.getLength(); vNodeIdx ++)
      } // end of if (parParentNode != null)
   } // end of SupportedTypesBaseMap()


   public void add(Integer parType)
   {
      // check passed type ...
      checkType(parType);
      
      _TypesMap.put(parType, parType);
   } // end of add()
      
   
   public boolean contains(Integer parType)
   {
      return(_TypesMap.containsKey(parType));
   } // end of contains()

   
   public Integer getFirstType()
   {
      Iterator tmpIter;
      Integer  retSupportedType;
      
      tmpIter = _TypesMap.values().iterator();
      if (tmpIter.hasNext())
      {
         retSupportedType = (Integer) tmpIter.next();
      }
      else
      {
         retSupportedType = null;
      } // end of (else) if (tmpIter.hasNext())

      return(retSupportedType);
   } // end of getFirstType()


   public SupportedTypesBaseMap getTypes()
   {
      return(this);
   } // end of getTypes()

   
   public Iterator iterator()
   {
      return(_TypesMap.values().iterator());
   } // end of iterator()
   
   
   public void remove(Integer parType)
   {
      // check passed type ...
      checkType(parType);
      
      _TypesMap.remove(parType);
   } // end of remove()
      
   
   public String toString()
   {
      StringBuffer tmpBuf;
      Map.Entry    mapEntry;
      Iterator     entryIter;
      int          intTypeId;
      boolean      isFirst;
      
      tmpBuf  = new StringBuffer();
      isFirst = true;
      
      entryIter = _TypesMap.entrySet().iterator();
      while(entryIter.hasNext())
      {
         if (isFirst)
         {
            isFirst = false;
         }
         else
         {
            tmpBuf.append("/");
         }
         
         mapEntry = (Map.Entry) entryIter.next();
         intTypeId = Integer.parseInt(mapEntry.getKey().toString());
      
         tmpBuf.append(getTypeAsString(intTypeId));
      }
      
      return(tmpBuf.toString());
   } // end of toString()
   
   
   public String toXML()
   {
      StringBuffer xmlBuf;
      Map.Entry    mapEntry;
      Iterator     entryIter;
      
      xmlBuf = new StringBuffer();
      
      entryIter = _TypesMap.entrySet().iterator();
      while(entryIter.hasNext())
      {
         xmlBuf.append("<");
         xmlBuf.append(getXMLTag());
         
         mapEntry = (Map.Entry) entryIter.next();
         xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_TYPE_TYPE_ID,
                                                          mapEntry.getKey().toString()));
         xmlBuf.append("/>");
      }
      
      return(xmlBuf.toString());
   }

   
   // -------------------------------------------------------------------------------------
   //                                 Abstract Methods
   // -------------------------------------------------------------------------------------
   abstract void   checkType(Integer parType) throws IllegalArgumentException;
   abstract String getXMLTag();
   abstract String getTypeAsString(int parTypeId);
   
} // end of class SupportedTypesBaseMap
