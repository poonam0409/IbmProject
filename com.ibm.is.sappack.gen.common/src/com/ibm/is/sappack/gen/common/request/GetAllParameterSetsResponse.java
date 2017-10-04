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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ibm.is.sappack.gen.common.Constants;
import com.ibm.is.sappack.gen.common.util.DSParamSet;
import com.ibm.is.sappack.gen.common.util.XMLUtils;

/**
 * GetAllParameterSetsResponse
 * 
 * Response for a GetAllParameterSetsRequest. Contains a list
 * of DataStage ParameterSets 
 *
 */
public class GetAllParameterSetsResponse extends ResponseBase {
   
   private  static final String  XML_TAG_GET_ALL_PARAMETER_SET_RESULT = "GetAllParameterSetsResult"; //$NON-NLS-1$
   private  static final String  XML_ATTRIB_COUNT                     = "count";                     //$NON-NLS-1$
   private  static final String  XML_TAG_SET_LIST                     = "ParameterSetList";          //$NON-NLS-1$

	/* list of DataStage Parameter sets */
	private List _ParameterSetList;
	private Map  _ParameterSetMap;

	
   static String copyright() {
      return com.ibm.is.sappack.gen.common.request.Copyright.IBM_COPYRIGHT_SHORT;
   }

	
   public GetAllParameterSetsResponse(GetAllParameterSetsRequest requestType) {
      super(requestType.getClass());
      
      _ParameterSetMap = null;
   } // end of GetAllParameterSetsResponse()
   
   
   public GetAllParameterSetsResponse(Node xmlNode) {
      super(xmlNode);
      
      Node paramSetResultsNode;
      
      // get response parameter from DOM
      paramSetResultsNode = XMLUtils.getChildNode(xmlNode, XML_TAG_GET_ALL_PARAMETER_SET_RESULT);
      if (paramSetResultsNode != null) {
         // get the ParameterSet list ...
         _ParameterSetList = createParameterSetListFromXML(XML_TAG_SET_LIST, paramSetResultsNode);
      } // end of if (paramSetResultsNode != null)
   } // end of GetAllParameterSetsResponse()

   
   private List createParameterSetListFromXML(String jobTypeXMLTagName, Node parentNode) {
      List     retParamSetList;
      NodeList paramSetNodes;
      Node     setNode;
      Node     setListNode;
      int      idx;

      retParamSetList = new ArrayList();
      
      setListNode = XMLUtils.getChildNode(parentNode, jobTypeXMLTagName);
      if (setListNode != null) {
         paramSetNodes = setListNode.getChildNodes();
         
         for (idx = 0; idx < paramSetNodes.getLength(); idx++) {
            
            setNode = paramSetNodes.item(idx);
            retParamSetList.add(new DSParamSet(setNode));
         }
      } // end of if (setListNode != null)
      
      return(retParamSetList);
   } // end of createParameterSetListFromXML()

   
	/**
	 * getParameterSets
	 * 
	 * @return List containing the parameter sets
	 */
	public List getParameterSets() {
		return _ParameterSetList;
	} // end of getParameterSets()

	
   /**
    * getParameterSets
    * 
    * @return List containing the parameter sets
    */
   public Map getParameterSetsAsMap() {
      DSParamSet paramSet;
      Iterator   listIter;
      
      if (_ParameterSetMap == null) {
         _ParameterSetMap = new HashMap();
         listIter         = _ParameterSetList.iterator();
         while(listIter.hasNext()) {
            paramSet = (DSParamSet) listIter.next();
            _ParameterSetMap.put(paramSet.getName(), paramSet);
         }
      } // end of if (_ParameterSetMap == null)
      
      return _ParameterSetMap;
   } // end of getParameterSetsAsMap()

   
	/**
	 * setParameterSets
	 * 
	 * @param parameter sets
	 */
	public void setParameterSets(List paramSets) {
		_ParameterSetList = paramSets;
	} // end of setParameterSets()
	

   protected String getTraceString() {
      DSParamSet   paramSet;
      StringBuffer traceBuffer = new StringBuffer();
      Iterator     listIter;

      traceBuffer.append("ParameterSet cnt = ");                  //$NON-NLS-1$
      if (_ParameterSetList == null)
      {
         traceBuffer.append("null");                              //$NON-NLS-1$
      }
      else
      {
         traceBuffer.append(_ParameterSetList.size());
         traceBuffer.append(" [");                                 //$NON-NLS-1$
         
         listIter = _ParameterSetList.iterator();
         while(listIter.hasNext()) {
            paramSet = (DSParamSet) listIter.next();
            traceBuffer.append(paramSet.getName());
            if (listIter.hasNext())
            {
               traceBuffer.append(", ");
            }
         }
         traceBuffer.append("]");                                 //$NON-NLS-1$
      }
      traceBuffer.append(Constants.NEWLINE);
      
      return(traceBuffer.toString());
   } // end of getTraceString()

   
   protected String getXML() {
      DSParamSet   curParamSet;
      StringBuffer xmlBuf;
      Iterator     listIter;
      Integer      setCount;
      
      if (_ParameterSetList == null) {
         setCount = null;
      }
      else {
         setCount = new Integer(_ParameterSetList.size());
      }

      // build result XML ...
      xmlBuf = new StringBuffer();

      xmlBuf.append("<");                                      //$NON-NLS-1$
      xmlBuf.append(XML_TAG_GET_ALL_PARAMETER_SET_RESULT);
      xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_COUNT, setCount));
      xmlBuf.append(">");                                      //$NON-NLS-1$
      
      // - - - - - - - - - - - - - - - - - - List Of Parameter Sets - - - - - - - - - - - - - - - -
      xmlBuf.append("<");                                      //$NON-NLS-1$
      xmlBuf.append(XML_TAG_SET_LIST);
      xmlBuf.append(">");                                      //$NON-NLS-1$
      if (_ParameterSetList != null) {
         listIter = _ParameterSetList.iterator();
         while(listIter.hasNext()) {
            curParamSet = (DSParamSet) listIter.next();
            
            xmlBuf.append(curParamSet.getXML());
         }
      }
      xmlBuf.append("</");                                     //$NON-NLS-1$
      xmlBuf.append(XML_TAG_SET_LIST);
      xmlBuf.append(">");                                      //$NON-NLS-1$
      
      xmlBuf.append("</");                                     //$NON-NLS-1$
      xmlBuf.append(XML_TAG_GET_ALL_PARAMETER_SET_RESULT);
      xmlBuf.append(">");                                      //$NON-NLS-1$

      return (xmlBuf.toString());
   } // end of getXML()
	
} // end of class GetAllParameterSetsResponse 
