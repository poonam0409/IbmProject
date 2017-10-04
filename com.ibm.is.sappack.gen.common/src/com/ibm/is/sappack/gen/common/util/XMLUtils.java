//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2011, 2013                                              
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


import java.io.StringReader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;


public final class XMLUtils
{
   // -------------------------------------------------------------------------------------
   //                                       Constants
   // -------------------------------------------------------------------------------------
   private static final Map<String,String> EMPTY_MAP                 = new HashMap<String,String>();
   private static final String             EMPTY_STRING              = "";                                              //$NON-NLS-1$
   public  static final String             XML_HEADER                = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";    //$NON-NLS-1$
   public  static final String             XML_HEADER_UTF16          = "<?xml version=\"1.0\" encoding=\"UTF-16\"?>";   //$NON-NLS-1$
   private static final String             CDATA_HEADER              = "<![CDATA[";                                     //$NON-NLS-1$
   private static final String             CDATA_TAIL                = "]]>";                                           //$NON-NLS-1$
   public  static final String             XML_CDATA_TEMPLATE        = CDATA_HEADER + "{0}" + CDATA_TAIL;               //$NON-NLS-1$
   
   public  static final int                XPATH_RESULT_TYPE_STRING  = 0;
   public  static final int                XPATH_RESULT_TYPE_LIST    = 1;
   
   // -------------------------------------------------------------------------------------
   //                                 Member Variables
   // -------------------------------------------------------------------------------------

   static String copyright()
   { 
      return com.ibm.is.sappack.gen.common.util.Copyright.IBM_COPYRIGHT_SHORT; 
   }

   
   private static Map<String,String> buildConditionMap(String pConditionBuf) 
   {
      String             key;
      String             value;
      Map<String,String> retConditionMap = new HashMap<String,String>();
      int                startIdx;
      int                endIdx;

      startIdx = pConditionBuf.indexOf("@");                               //$NON-NLS-1$
      while(startIdx > -1)
      {
         // get the 'key'
         startIdx ++;
         endIdx = pConditionBuf.indexOf("=", startIdx);                    //$NON-NLS-1$
         if (endIdx < 0)
         {
            endIdx = pConditionBuf.length();
         }
         key = pConditionBuf.substring(startIdx, endIdx);

         // get the 'value'
         startIdx = pConditionBuf.indexOf("'", endIdx );                   //$NON-NLS-1$
         if (startIdx < 0)
         {
            value = null;
            System.err.println("Limiter ' von XPath variables missing.");  //$NON-NLS-1$
         }
         else
         {
            startIdx ++;
            endIdx = pConditionBuf.indexOf("'", startIdx);                 //$NON-NLS-1$
            if (endIdx < 0)
            {
               endIdx = pConditionBuf.length();
            }
            value = pConditionBuf.substring(startIdx, endIdx);
         }
         retConditionMap.put(key, value);

         // get next key/value pair
         startIdx = pConditionBuf.indexOf("@", endIdx);                    //$NON-NLS-1$
      } // end of while(startIdx > -1)

      return(retConditionMap);
   } // end of buildConditionMap()
   
   
   public static String createAttribPairString(String parAttribName, Boolean parBoolAttribValue)
   {
      String xmlAttribBuf;
      
      if (parBoolAttribValue == null)
      {
         xmlAttribBuf = "";                                             //$NON-NLS-1$                                          
      }
      else
      {
         xmlAttribBuf = createAttribPairString(parAttribName, parBoolAttribValue.toString());
      } // end of (else) if (parBoolAttribValue == null)
      
      return(xmlAttribBuf);
   } // end of createAttribPairString()

   
   public static String createAttribPairString(String parAttribName, Integer parIntAttribValue)
   {
      String xmlAttribBuf;
      
      if (parIntAttribValue == null)
      {
         xmlAttribBuf = "";                                             //$NON-NLS-1$
      }
      else
      {
         xmlAttribBuf = createAttribPairString(parAttribName, parIntAttribValue.toString());
      } // end of (else) if (parIntAttribValue == null)
      
      return(xmlAttribBuf);
   } // end of createAttribPairString()

   
   public static String createAttribPairString(String parAttribName, String parAttribValue)
   {
      StringBuffer xmlAttribBuf;
      
      xmlAttribBuf = new StringBuffer();
      
      if (parAttribValue != null)
      {
         xmlAttribBuf.append(" ");                                      //$NON-NLS-1$
         xmlAttribBuf.append(String.valueOf(parAttribName));
         xmlAttribBuf.append("=\"");                                    //$NON-NLS-1$
         xmlAttribBuf.append(replaceXMLCharacters(parAttribValue));
         xmlAttribBuf.append("\"");                                     //$NON-NLS-1$
      } // end of if (parAttribValue != null)
      
      return(xmlAttribBuf.toString());
   } // end of createAttribPairString()

   
   public static String createCDATAElement(String parSubTagName, String cdataValue)
   {
      StringBuffer xmlTagBuf;
      
      xmlTagBuf = new StringBuffer();
   	if (parSubTagName != null && parSubTagName.length() > 0)
   	{
         xmlTagBuf.append("<");                      //$NON-NLS-1$
         xmlTagBuf.append(parSubTagName);
         xmlTagBuf.append(">");                      //$NON-NLS-1$
         xmlTagBuf.append(MessageFormat.format(XMLUtils.XML_CDATA_TEMPLATE, 
                                               new Object[] { String.valueOf(cdataValue) } ));
         xmlTagBuf.append("</");                     //$NON-NLS-1$
         xmlTagBuf.append(parSubTagName);
         xmlTagBuf.append(">");                      //$NON-NLS-1$
   	} // end of if (parSubTagName != null && parSubTagName.length() > 0)
   	
   	return(xmlTagBuf.toString());
   } // end of createCDATAElement()


   private static boolean doesConditionMatch(Map pConditionMap, Node pNode) 
   {
      boolean retConditionMatch;
      
      retConditionMatch = true;
      if (pConditionMap != null) {
         Node         attribNode;
         NamedNodeMap attribList;
         String       attribName;
         String       attribValue;
         Map.Entry    mapEntry;
         Iterator     mapIter;
         
         attribList = pNode.getAttributes();
         mapIter    = pConditionMap.entrySet().iterator();
         while(mapIter.hasNext() && retConditionMatch) {
            mapEntry = (Map.Entry) mapIter.next();
            attribName = (String) mapEntry.getKey();
            
            // check if there is a node attribute having that name
            attribNode  = attribList.getNamedItem(attribName);
            if (attribNode == null) {
               // attribute doesn't exist
               retConditionMatch = false;
            }
            else {
               // attribute was found ==> check the attribute value
               attribValue = (String) mapEntry.getValue();
// System.out.println("attribNode.getNode = " + attribNode.getNodeName() + " - value = " + attribNode.getNodeValue());               
               if (!attribValue.equals(attribNode.getNodeValue())) {
                  // attribute doesn't match
                  retConditionMatch = false;
               } // end of if (!attribValue.equals(attribNode.getNodeValue()))
            } // end of if (attribNode == null)
         } // end of while(mapIter.hasNext() && retConditionMatch)
      } // end of if (pConditionMap != null)
      
      return(retConditionMatch);
   } // end of doesConditionMatch()
   
   
   public static Object evaluate(Node pParentNode, String pXPath, int pResultType)
   {
      Object  vResultObj;

/*      
      if (TraceLogger.isTraceEnabled()) {
         TraceLogger.entry("nodename = " + pParentNode.getNodeName() + 
                           " - xpath = " + pXPath + " - type = " + pResultType);
      }
*/
      
      // call the 'evaluate()' method for XPATH resolution
      vResultObj = evaluate(pParentNode, pXPath);

      switch(pResultType) {
         case XPATH_RESULT_TYPE_LIST:
              if (!(vResultObj instanceof List)) {
                 throw new IllegalArgumentException("Resolved XPath does not return a List object as expected."); //$NON-NLS-1$
              }
              break;
                 
         case XPATH_RESULT_TYPE_STRING:
              if (vResultObj instanceof List) {
                 List listObj = (List) vResultObj;
                 
                 if (listObj.size() == 0) {
                    vResultObj = EMPTY_STRING;
                 }
                 else {
                    vResultObj = ((Node) listObj.get(0)).getNodeValue();
                 }
              }
              else {
                 if (!(vResultObj instanceof String)) {
                    throw new IllegalArgumentException("Resolved XPath does not return a String object as expected."); //$NON-NLS-1$
                 }
              }
              break;
                 
         default:
              throw new IllegalArgumentException("Invalid XPATH Result type '" + pResultType + "' specified."); //$NON-NLS-1$
         } // end of switch(pResultType)

/*      
      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.exit("result type = " + vResultObj.getClass().getName());
      }
*/

      return(vResultObj);
   } // end of evaluate()
   
   public static Object evaluate(Node pParentNode, String pXPath)
   {
      NodeList           childNodeList;
      Node               curChildNode;
      Node               curAttribNode;
      String             curElementName;
      String             remainElementName;
      String             attribName;
      List               vResultNodeList;
      Map<String,String> vConditionMap;
      Object             retObject;
      int                vElementIdx;
      int                vNodeIdx;
      int                vEndIdx;
      int                vStartIdx;
      int                vMatchIdx;
      boolean            endOfXPathReached;
      boolean            endOfLoop;
      
/*      
      if (TraceLogger.isTraceEnabled()) {
         TraceLogger.entry("nodename = " + pParentNode.getNodeName() + " - xpath = " + pXPath + 
                           " - attrib name = " + pAttribName + " - attrib value = " + pAttribValue + 
                           " - FirstOnly = " +pFirstOnly);
      }
*/      
      vResultNodeList = new ArrayList();
      vMatchIdx       = -1;
      vConditionMap   = EMPTY_MAP;
      retObject       = vResultNodeList;
      
      // remove leading '/'
      if (pXPath.startsWith("/")) {
         pXPath = pXPath.substring(1);
      }
      
      // get index of first XPath element
      vElementIdx = pXPath.indexOf('/');
      if (vElementIdx > -1) {
         curElementName    = pXPath.substring(0, vElementIdx);
         remainElementName = pXPath.substring(vElementIdx+1);
         endOfXPathReached = false;
      }
      else {
         curElementName    = pXPath;
         endOfXPathReached = true;
         remainElementName = "";
      } // end of (else) if (vElementIdx > -1)

      endOfLoop = false;
      
      // check if there is an certain element to be found ...
      vStartIdx = curElementName.indexOf('[');                          //$NON-NLS-1$ 
      if (vStartIdx > -1) {
         String tmpSearchString;
         
         vEndIdx = curElementName.indexOf(']', vStartIdx);              //$NON-NLS-1$
         if (vEndIdx < 0) {
            vEndIdx = curElementName.length();
         }
         tmpSearchString  = curElementName.substring(vStartIdx +1, vEndIdx);

         // check if it's an attribute or an index ...
         if (tmpSearchString.startsWith("@")) {                         //$NON-NLS-1$
// System.out.println("search attrib = " + tmpSearchString);
            // ==> it's an attribute --> build a condition map from search string ...
            vConditionMap = buildConditionMap(tmpSearchString);
         }
         else {
            // ==> it's an index
            vMatchIdx = Integer.parseInt(tmpSearchString);
            
            if (vMatchIdx > 0) {
               vMatchIdx --;   // we start at 0
            }
// System.out.println("match idx = " + vMatchIdx);
         } // end of if (tmpSearchString.startsWith("@")) {
         
         // remove index from element name
         curElementName = curElementName.substring(0, vStartIdx);
      }

      // check if there is an certain attribute value to get ...
      if (remainElementName.startsWith("@")) {                          //$NON-NLS-1$
         endOfXPathReached = true;
      } // end of if (remainElementName.startsWith("@"))
      
      // check if element name is child of the current node
      childNodeList = pParentNode.getChildNodes();
      vNodeIdx      = 0;
      vElementIdx   = 0;

// System.out.println("element = " + curElementName);            
      while(vNodeIdx < childNodeList.getLength() && !endOfLoop) {
         curChildNode = childNodeList.item(vNodeIdx);
         if (curChildNode.getNodeType() == Node.ELEMENT_NODE) {
            
// System.out.println("node = " + curChildNode.getNodeName());            
            if (curChildNode.getNodeName().equals(curElementName)) {
               
               if (endOfXPathReached) {
                  if (doesConditionMatch(vConditionMap, curChildNode)) {
                     
                     if (remainElementName.startsWith("@")) {           //$NON-NLS-1$
                        // get the attribute name ...
                        attribName    = remainElementName.substring(1);
                        curAttribNode = curChildNode.getAttributes().getNamedItem(attribName);
                        
                        if (curAttribNode != null) {
                           if (vMatchIdx == -1) {
                              vResultNodeList.add(curAttribNode);
                              
                              // we assume that this value is unique for 'current child node'
                              endOfLoop = true;
                           }
                           else {
                              retObject = curAttribNode.getNodeValue();
                              
                              // one value was expected to be found ==> leave the loop
                              endOfLoop = true;
                           }
                        }
                     }
                     else {
                        if (vMatchIdx == -1) {
                           vResultNodeList.add(curChildNode);
                        }
                        else {
                           if (vMatchIdx == vElementIdx) {
                              vResultNodeList.add(curChildNode);
                              
                              endOfLoop = true;    // one node only
                           } // end of if (vElementMatchIdx == vElementIdx || ... curChildNode)))
                        } // end of (else) if (vMatchIdx == -1)
                     } // end of (else) if (remainElementName.startsWith("@"))
                  } // end of if (doesConditionMatch(vConditionMap, curChildNode)) {
                  
               }
               else {
                  Object resultObj = null;
                  
                  if (vMatchIdx == -1) {
                     resultObj = evaluate(curChildNode, remainElementName);
                  }
                  else {
                     if (vMatchIdx == vElementIdx) {
                        resultObj = evaluate(curChildNode, remainElementName);
                        
                        endOfLoop = true;    // one node only
                     } // end of if (vElementMatchIdx == vElementIdx)
                  } // end of (else) if (vElementMatchIdx == -1)
                  
                  if (resultObj != null) {
                     if (resultObj instanceof List) {
                        vResultNodeList.addAll((List) resultObj);
                     }
                     else {
                        retObject = resultObj;
                        endOfLoop = true;         // result found it's not a list
                     }
                  } // end of if (resultObj != null)
               } // end of (else) if (endOfXPathReached)
               
               vElementIdx ++;
            } // end of if (curChildNode.getNodeName().equals(curElementName))
         } // end of if (curChildNode.getNodeType() == Node.ELEMENT_NODE)
         
         vNodeIdx ++;
      } // end of while(vNodeIdx < childNodeList.getLength() && !endOfLoop)

/*      
      if (TraceLogger.isTraceEnabled()) {
         if (retObject instanceof List ) {
            TraceLogger.exit("result node count = " + ((List) retObject).size());
         }
         else {
            TraceLogger.exit("result object = " + retObject);
         }
      }
*/

      return(retObject);
   } // end of evaluate()
   
   
   public static Node getChildNode(Node parNode, String parChildNodeName)
   {
      Node     curChildNode;
      NodeList childNodeList;
      Node     retNode;
      int      vNodeListIdx;

      retNode       = null;
      childNodeList = parNode.getChildNodes();
      vNodeListIdx  = 0;
      while(vNodeListIdx <  childNodeList.getLength() &&
            retNode      == null)
      {
         curChildNode = childNodeList.item(vNodeListIdx);
         if (curChildNode.getNodeName().equals(parChildNodeName))
         {
            retNode = curChildNode;
         }
         else
         {
            vNodeListIdx ++;
         }
      } // end of while(vNodeListIdx < childNodeList.getLength() && ...
      
      // if the child has not been found ...
      vNodeListIdx = 0;
      while(vNodeListIdx <  childNodeList.getLength() &&
            retNode      == null)
      {
         // ==> search in the child nodes for passed node name
         retNode = getChildNode(childNodeList.item(vNodeListIdx), parChildNodeName);
         vNodeListIdx ++;
      } // end of while(vNodeListIdx < childNodeList.getLength() && ...
      
      return(retNode);
   } // end of getChildNode()

   
   public static List getChildNodeList(Node pParentNode, String pXPath) 
   {
      return((List) evaluate(pParentNode, pXPath, XPATH_RESULT_TYPE_LIST));
   } // end of getChildNodeList()
   
   
   public static String getChildNodeText(Node parNode, String childXMLTagName)
   {
      Node     childNode;
      String   childText;

      childText = null;
      childNode = XMLUtils.getChildNode(parNode, childXMLTagName);
      if (childNode != null)
      {
      	childText = XMLUtils.getNodeTextValue(childNode);
      } // end of if (childNode != null)

      return(childText);
   } // end of getChildNodeText()


   public static String getNodeAttributeValue(Node parNode, String parAttribName)
   {
      Node   curAttrib;
      String retAttribValue;

      retAttribValue = null;
      if (parNode != null)
      {
         curAttrib = parNode.getAttributes().getNamedItem(parAttribName);
         
         if (curAttrib != null)
         {
            retAttribValue = curAttrib.getNodeValue();
         } // end of if (curAttrib !== null)
      }

      return(retAttribValue);
   } // end of getNodeAttributeValue()
   
   
   public static String getNodeTextValue(Node parNode)
   {
      Node     textNode;
      NodeList nodeList;
      String   retTextValue;
      int      textNodesCnt;
      int      textNodesType;
      int      idx;

      retTextValue = null;
      if (parNode != null)
      {
         if (parNode.getNodeType() == Node.ELEMENT_NODE)
         {
            nodeList     = parNode.getChildNodes();
            textNodesCnt = nodeList.getLength();
            idx         = 0;
            while(idx < textNodesCnt && retTextValue == null)
            {
               textNode      = nodeList.item(idx);
               textNodesType = textNode.getNodeType();
               retTextValue  = textNode.getTextContent();

               switch(textNodesType) {
               	case Node.TEXT_NODE:
                     // remove an existing CDATA frame
                     if (retTextValue.startsWith(CDATA_HEADER) && retTextValue.endsWith(CDATA_TAIL))
                     {
                     	retTextValue = retTextValue.substring(CDATA_HEADER.length(), retTextValue.length()- CDATA_TAIL.length());
                     }
                     break;

                  default:
                  	;
               }
               idx ++;
            } // end of while(idx < textNodesCnt && retTextValue == null)
         } // end of if (parNode.getNodeType() == Node.ELEMENT_NODE)
      } // end of if (parNode != null)

      return(retTextValue);
   } // end of getNodeTextValue()
   
   
   public static final Element getRootElementFromXML(String xml) throws Exception {
      
      DocumentBuilderFactory factory;
      DocumentBuilder        docBuilder;
      Document               xmlDocument;
      InputSource            inputSource;
      Element                rootElement = null;
      
      // create a new DocumentBuilder to parse ... 
      factory     = DocumentBuilderFactory.newInstance();
      docBuilder  = factory.newDocumentBuilder();
      
      // and then parse the XML ...
      inputSource = new InputSource();
      inputSource.setCharacterStream(new StringReader(xml));
      xmlDocument = docBuilder.parse(inputSource);
      rootElement = xmlDocument.getDocumentElement();
      
      return(rootElement);
   } // end of getRootNodeFromXML()
   
   
   public static String replaceXMLCharacters(String parValue)
   {  
      String retValue;

      if (parValue == null || parValue.length() == 0)
      {
         retValue = parValue;
      }
      else
      {
          // '&' must always be the first replacement !!!!! 
         retValue = StringUtils.replaceString(parValue, "&", "&amp;");        //$NON-NLS-1$
         
         retValue = StringUtils.replaceString(retValue, "<", "&lt;");         //$NON-NLS-1$
         retValue = StringUtils.replaceString(retValue, ">", "&gt;");         //$NON-NLS-1$
         retValue = StringUtils.replaceString(retValue, "\"", "&quot;");      //$NON-NLS-1$
         retValue = StringUtils.replaceString(retValue, "'", "&apos;");       //$NON-NLS-1$
      }
      
      return(retValue);
   } // end of  replaceXMLCharacters()
   
   
   public static String restoreXMLCharacters(String parValue)
   {  
      String retValue;

      if (parValue == null || parValue.length() == 0)
      {
         retValue = parValue;
      }
      else
      {
         retValue = StringUtils.replaceString(parValue, "&lt;", "<");         //$NON-NLS-1$
         retValue = StringUtils.replaceString(retValue, "&gt;", ">");         //$NON-NLS-1$
         retValue = StringUtils.replaceString(retValue, "&quot;", "\"");      //$NON-NLS-1$
         retValue = StringUtils.replaceString(retValue, "&apos;", "'");       //$NON-NLS-1$
         
         // '&' must always be the last replacement !!!!! 
         retValue = StringUtils.replaceString(retValue, "&amp;", "&");        //$NON-NLS-1$
      }

      return(retValue);
   } // end of  restoreXMLCharacters()
   
} // end of class XMLUtils
