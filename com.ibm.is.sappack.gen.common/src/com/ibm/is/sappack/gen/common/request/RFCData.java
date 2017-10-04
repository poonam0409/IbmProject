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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ibm.is.sappack.gen.common.trace.TraceLogger;
import com.ibm.is.sappack.gen.common.util.ServerMessageCatalog;
import com.ibm.is.sappack.gen.common.util.StringUtils;
import com.ibm.is.sappack.gen.common.util.XMLUtils;



final public class RFCData
{
   // -------------------------------------------------------------------------------------
   //                                       Constants
   // -------------------------------------------------------------------------------------
   public  static final String XML_TAG_RFC_DATA                      = "RFCData";                    //$NON-NLS-1$
   public  static final String XML_ATTRIB_RFC_ENABLED                = "rfcEnabled";                 //$NON-NLS-1$
   private static final String XML_ATTRIB_RFC_DATA_GW_HOST           = "gatewayhost";                //$NON-NLS-1$
   private static final String XML_ATTRIB_RFC_DATA_GW_SRVCE          = "gatewayService";             //$NON-NLS-1$
   private static final String XML_ATTRIB_RFC_DATA_CLEANUP_DEST      = "cleanupDest";                //$NON-NLS-1$
   private static final String XML_ATTRIB_RFC_DATA_CREATE_DEST       = "createDest";                 //$NON-NLS-1$
   private static final String XML_ATTRIB_RFC_DATA_RETRY_COUNT       = "retryCount";                 //$NON-NLS-1$
   private static final String XML_ATTRIB_RFC_DATA_RETRY_INTERVAL    = "retryInterval";              //$NON-NLS-1$
   private static final String XML_TAG_RFC_DEST                      = "RFCDestination";             //$NON-NLS-1$ 
   private static final String XML_ATTRIB_RFC_DEST_NAME              = "name";                       //$NON-NLS-1$
   private static final String XML_ATTRIB_RFC_DEST_PROGRAMID         = "programId";                  //$NON-NLS-1$
   private static final String XML_ATTRIB_RFC_DATA_DELETE_LUW        = "deleteLUW";                  //$NON-NLS-1$
   private static final String XML_ATTRIB_RFC_SUPPRESS_ABAP_PROG_VAL = "suppressAbapProgValidation"; //$NON-NLS-1$
   private static final String XML_ATTRIB_RFC_DATA_GENERATE_DESTINATION_NAME = "generateRFCDestinationName"; //$NON-NLS-1$
   private static final String XML_ATTRIB_RFC_DATA_RFC_DESTINATION_NAME_GENERATION_PREFIX = "rfcDestinationNameGenerationPrefix"; //$NON-NLS-1$
   private static final String XML_ATTRIB_RFC_SUPPRESS_BACKGROUND_JOB	= "suppressbackgroundjob";

   public  static final int    RETRY_COUNT_DEFFAULT               = 30;
   public  static final int    RETRY_INTERVAL_DEFFAULT            = 1;

   
   // -------------------------------------------------------------------------------------
   //                                 Member Variables
   // -------------------------------------------------------------------------------------
   private boolean   _Enabled;
   private Map       _DestinationsMap;
   private Boolean   _DoCleanUpRFCDestAfterReq;
   private Boolean   _DoCreateRFCDest;
   private Boolean   _DoDeleteLUW;
   private Boolean   _SuppressAbapProgValidation;
   private String    _GatewayHost;
   private String    _GatewayService;
   private int       _RetryCount;
   private int       _RetryInterval;
   private Boolean   _GenerateRFCDestinationNames;
   private String    _RFCDestinationNameGenerationPrefix;
   private Boolean	_DoSuppressBackgroundjob;
   
   static String copyright()
   { 
      return com.ibm.is.sappack.gen.common.request.Copyright.IBM_COPYRIGHT_SHORT; 
   }
   
   
   RFCData()
   {
      this(null, null);
   } // end of RFCData()
   
   
   RFCData(String parGWHost, String parGWService)
   {
	   _Enabled                    = true;
      _GatewayHost                = parGWHost;
      _GatewayService             = parGWService;
      _DestinationsMap            = new HashMap();
      _DoCreateRFCDest            = null;
      _DoCleanUpRFCDestAfterReq   = null;
      _DoDeleteLUW                = Boolean.TRUE;
      _RetryCount                 = RETRY_COUNT_DEFFAULT;
      _RetryInterval              = RETRY_INTERVAL_DEFFAULT;
      _SuppressAbapProgValidation = null;
      _GenerateRFCDestinationNames = Boolean.FALSE;
      _DoSuppressBackgroundjob = Boolean.FALSE;
   } // end of RFCData()
   
   
   RFCData(Node parRFCDataNode)
   {
      this(null, null);
      
      NodeList rfcDestinationsList;
      Node     curRFCDestinationNode;
      String   rfcDestination;
      String   rfcProgramId;
      String   tmpBoolString;
      String   tmpIntString;
      int      vNodeIdx;

      if (parRFCDataNode != null)
      {
    	 _Enabled = Boolean.valueOf(XMLUtils.getNodeAttributeValue(parRFCDataNode, XML_ATTRIB_RFC_ENABLED)).booleanValue(); 
    	 if (_Enabled) {
    	    
            // first get the attributes ...
            _GatewayHost    = XMLUtils.getNodeAttributeValue(parRFCDataNode, XML_ATTRIB_RFC_DATA_GW_HOST);
            _GatewayService = XMLUtils.getNodeAttributeValue(parRFCDataNode, XML_ATTRIB_RFC_DATA_GW_SRVCE);
            tmpBoolString   = XMLUtils.getNodeAttributeValue(parRFCDataNode, XML_ATTRIB_RFC_DATA_CLEANUP_DEST);
            if (tmpBoolString != null)
            {
               _DoCleanUpRFCDestAfterReq = new Boolean(tmpBoolString);
            }
            tmpBoolString = XMLUtils.getNodeAttributeValue(parRFCDataNode, XML_ATTRIB_RFC_DATA_CREATE_DEST);
            if (tmpBoolString != null)
            {
               _DoCreateRFCDest = new Boolean(tmpBoolString);
            }
            tmpIntString = XMLUtils.getNodeAttributeValue(parRFCDataNode, XML_ATTRIB_RFC_DATA_RETRY_COUNT);
            if (tmpIntString != null)
            {
               _RetryCount = Integer.parseInt(tmpIntString);
            }
            tmpIntString = XMLUtils.getNodeAttributeValue(parRFCDataNode, XML_ATTRIB_RFC_DATA_RETRY_INTERVAL);
            if (tmpIntString != null)
            {
               _RetryInterval = Integer.parseInt(tmpIntString);
            }
            tmpBoolString = XMLUtils.getNodeAttributeValue(parRFCDataNode, XML_ATTRIB_RFC_DATA_DELETE_LUW);
            if (tmpBoolString != null)
            {
               _DoDeleteLUW = new Boolean(tmpBoolString);
            }
            tmpBoolString = XMLUtils.getNodeAttributeValue(parRFCDataNode, XML_ATTRIB_RFC_SUPPRESS_ABAP_PROG_VAL);
            if (tmpBoolString != null)
            {
               _SuppressAbapProgValidation = new Boolean(tmpBoolString);
            }
            tmpBoolString = XMLUtils.getNodeAttributeValue(parRFCDataNode, XML_ATTRIB_RFC_DATA_GENERATE_DESTINATION_NAME);
            if (tmpBoolString != null)
            {
               _GenerateRFCDestinationNames = new Boolean(tmpBoolString);
            }
            tmpBoolString = XMLUtils.getNodeAttributeValue(parRFCDataNode, XML_ATTRIB_RFC_SUPPRESS_BACKGROUND_JOB);
            if (tmpBoolString != null)
            {
            	_DoSuppressBackgroundjob = new Boolean(tmpBoolString);
            }
            
            _RFCDestinationNameGenerationPrefix = XMLUtils.getNodeAttributeValue(parRFCDataNode, XML_ATTRIB_RFC_DATA_RFC_DESTINATION_NAME_GENERATION_PREFIX);
           
            

            rfcDestinationsList = parRFCDataNode.getChildNodes();
            for(vNodeIdx = 0; vNodeIdx < rfcDestinationsList.getLength(); vNodeIdx ++)
            {
               curRFCDestinationNode = rfcDestinationsList.item(vNodeIdx);
            
               rfcDestination = XMLUtils.getNodeAttributeValue(curRFCDestinationNode, 
                                                               XML_ATTRIB_RFC_DEST_NAME);
               rfcProgramId   = XMLUtils.getNodeAttributeValue(curRFCDestinationNode, 
                                                               XML_ATTRIB_RFC_DEST_PROGRAMID);
            
               _DestinationsMap.put(rfcDestination, rfcProgramId);
            } // end of for(vNodeIdx = 0; vNodeIdx < rfcDestinationsList.getLength(); vNodeIdx ++)
    	 }
      } // end of if (parRFCDataNode != null)
   }
   
   
   public void addRFCDestinationProgramId(String parRFCDestination, String parRFCProgramId)
   {
      if (parRFCDestination != null && parRFCProgramId != null)
      {
         _DestinationsMap.put(parRFCDestination, parRFCProgramId);
      }
   }
      
   
   public Boolean doCleanUpRFCDestAfterReq()
   {
      return(_DoCleanUpRFCDestAfterReq);
   }
   
   
   public Boolean doCreateRFCDest()
   {
      return(_DoCreateRFCDest);
   }
   
   
   public Boolean doDeleteLUW()
   {
      return(_DoDeleteLUW);
   }
   
   
   public Boolean doSuppressAbapProgValidation()
   {
      return(_SuppressAbapProgValidation);
   }
   
   
   public Map getDestinationsMap()
   {
      return(_DestinationsMap);
   }

   
   public boolean getEnabled()
   {
      return _Enabled;   
   }
   
   public String getGatewayHost()
   {
      return(_GatewayHost);
   }

   
   public String getGatewayService()
   {
      return(_GatewayService);
   }
   
   
   public int getRetryCount()
   {
      return(_RetryCount);
   }
   
   
   public int getRetryInterval()
   {
      return(_RetryInterval);
   }
   
   public Boolean doSuppressBackgroundJob()
   {
      return(_DoSuppressBackgroundjob);
   }
   
   
   public void setSuppressAbapProgValidation(boolean parSuppress)
   {
      _SuppressAbapProgValidation = new Boolean(parSuppress);
   }

   
   public void setDestinationsMap(Map parDestinationsMap) 
   {
      if (parDestinationsMap == null)
      {
         _DestinationsMap = new HashMap();
      }
      else
      {
         _DestinationsMap = parDestinationsMap;
      }
	}
   
   public void setDoCleanUpRFCDestAfterReq(boolean doCleanUp)
   {
      _DoCleanUpRFCDestAfterReq = new Boolean(doCleanUp);
   }

   
   public void setDoCreateRFCDest(boolean doCreate)
   {
      _DoCreateRFCDest = new Boolean(doCreate);
   }

   
   public void setDoDeleteLUW(boolean doDelete)
   {
      _DoDeleteLUW = new Boolean(doDelete);
   }

   
   public void setEnabled(boolean enabled)
   {
     _Enabled = enabled;
   }
   
   public void setGatewayHost(String parGWHost)
   {
      _GatewayHost = parGWHost;
   }

   public void setGatewayService(String parGWService)
   {
      _GatewayService = parGWService;
   }
   
   
   public void setRetryCount(int parRetryCount)
   {
      _RetryCount = parRetryCount;
   }
   
   
   public void setRetryInterval(int parRetryInterval)
   {
      _RetryInterval = parRetryInterval;
   }
   
   public void setSuppressBackgroundJob(boolean suppressBackgroundJob)
   {
	   _DoSuppressBackgroundjob = suppressBackgroundJob;
	   
   }
   
   public String toString()
   {
      StringBuffer traceStringBuf;
      
      traceStringBuf = new StringBuffer();
      traceStringBuf.append("RFC Destinations: "); 
      traceStringBuf.append(_DestinationsMap);
      traceStringBuf.append(" - GW Host: ");
      traceStringBuf.append(_GatewayHost);
      traceStringBuf.append(" - GW Service: ");
      traceStringBuf.append(_GatewayService);
      
      if (_DoCreateRFCDest != null)
      {
         traceStringBuf.append(" - Create RFC Dest: ");
         traceStringBuf.append(_DoCreateRFCDest);
         traceStringBuf.append(" - Retry Count: ");
         traceStringBuf.append(_RetryCount);
         traceStringBuf.append(" - Retry Interval: ");
         traceStringBuf.append(_RetryInterval);
      }
      if (_DoCleanUpRFCDestAfterReq != null)
      {
         traceStringBuf.append(" - Cleanup RFC Dest: ");
         traceStringBuf.append(_DoCleanUpRFCDestAfterReq);
      }
      if (_DoDeleteLUW != null)
      {
         traceStringBuf.append(" - Delete LUW: ");
         traceStringBuf.append(_DoDeleteLUW);
      }
      if (_SuppressAbapProgValidation != null)
      {
         traceStringBuf.append(" - Suppress ABAP Program validation: ");
         traceStringBuf.append(_SuppressAbapProgValidation);
      }
      if(_DoSuppressBackgroundjob != null)
      {
    	  traceStringBuf.append(" - Suppress BackGround Job: ");
          traceStringBuf.append(_DoSuppressBackgroundjob);
      }

      return(traceStringBuf.toString());
   }

   
   public String toXML()
   {
      String       rfcDestName;
      String       rfcProgramId;
      StringBuffer xmlBuf;
      Map.Entry    mapEntry;
      Iterator     entryIter;
      
      xmlBuf = new StringBuffer();
      xmlBuf.append("<"); //$NON-NLS-1$
      xmlBuf.append(XML_TAG_RFC_DATA);
      xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_RFC_ENABLED, Boolean.valueOf(_Enabled)));
      xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_RFC_DATA_GW_HOST, _GatewayHost));
      xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_RFC_DATA_GW_SRVCE, _GatewayService));
      xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_RFC_DATA_CLEANUP_DEST, 
                                                    _DoCleanUpRFCDestAfterReq));
      xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_RFC_DATA_CREATE_DEST, 
                                                    _DoCreateRFCDest));
      
      xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_RFC_DATA_GENERATE_DESTINATION_NAME, _GenerateRFCDestinationNames));
      xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_RFC_DATA_RFC_DESTINATION_NAME_GENERATION_PREFIX, _RFCDestinationNameGenerationPrefix));
      xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_RFC_SUPPRESS_BACKGROUND_JOB, _DoSuppressBackgroundjob));
      
      if (_DoCreateRFCDest != null)
      {
         xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_RFC_DATA_RETRY_COUNT, 
                                                       String.valueOf(_RetryCount)));
         xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_RFC_DATA_RETRY_INTERVAL, 
                                                       String.valueOf(_RetryInterval)));
      }
      xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_RFC_DATA_DELETE_LUW, 
    		                                         _DoDeleteLUW));
      xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_RFC_SUPPRESS_ABAP_PROG_VAL, 
                                                    _SuppressAbapProgValidation));
      xmlBuf.append(">"); //$NON-NLS-1$
      
      entryIter = _DestinationsMap.entrySet().iterator();
      while(entryIter.hasNext())
      {
         mapEntry = (Map.Entry) entryIter.next();
         
         // RFC destination and RFC program id are required to be in uppercase letters
         rfcDestName  = ((String) mapEntry.getKey()).toUpperCase();
         rfcProgramId = ((String) mapEntry.getValue()).toUpperCase();
            
         xmlBuf.append("<"); //$NON-NLS-1$
         xmlBuf.append(XML_TAG_RFC_DEST);
         
         xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_RFC_DEST_NAME, 
                                                       rfcDestName));
         xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_RFC_DEST_PROGRAMID, 
                                                       rfcProgramId));
         xmlBuf.append("/>"); //$NON-NLS-1$
      }
      
      xmlBuf.append("</"); //$NON-NLS-1$
      xmlBuf.append(XML_TAG_RFC_DATA);
      xmlBuf.append(">"); //$NON-NLS-1$
      
      
      return(xmlBuf.toString());
   }


   public void setGenerateRFCDestinationNames(boolean generateRFCDestNames) {
	   _GenerateRFCDestinationNames = Boolean.valueOf(generateRFCDestNames);

   }

   public void setRFCDestinationNameGenerationPrefix(String prefix) {
	   _RFCDestinationNameGenerationPrefix = prefix;
   }

   public boolean getGenerateRFCDestinationNames() {
	   return _GenerateRFCDestinationNames.booleanValue();
   }

   public String getRFCDestinationNameGenerationPrefix() {
	   return _RFCDestinationNameGenerationPrefix;
   }

   
   public static String[] generateRFCDestinationName(String prefix, String tableName, int number) {
	   tableName = StringUtils.cleanFieldName(tableName);
	   String[] result = new String[2];
	   // add a "T" to the number, otherwise the RFC destination name may be invalid:
	   /*
	    This is the error message from SAP
	    Message no. SR047

		Diagnosis
		The destination has an incorrect ID. Destination IDs in the form

		<hostname>_<systemname>_<system_number>,

		<hostname>_<systemname>_<system_number>_TRUSTED

		<hostname>_<systemname>_TRUSTED[groupname]

		NONE[groupname]

		and the names 'a_rfc' and 'b_rfc'

		are reserved for internal use.

	    */
	   String rfcDestName = prefix + "_" + tableName + "_T" + number; //$NON-NLS-1$ //$NON-NLS-2$
	   rfcDestName = rfcDestName.toUpperCase();
	   
	   if (rfcDestName.length() > 32) {
		   String newRFCDestName = prefix + Math.abs(rfcDestName.hashCode()); 
		   TraceLogger.trace(TraceLogger.LEVEL_INFO, ServerMessageCatalog.getDefaultCatalog().getMessage("00116I", new Object[]{rfcDestName, newRFCDestName})); //$NON-NLS-1$
		   rfcDestName = newRFCDestName;
	   }
	   
	   String programID = rfcDestName;
	   result[0] = rfcDestName;
	   result[1] = programID;
	   return result;
   }
   
} // end of class RFCData
