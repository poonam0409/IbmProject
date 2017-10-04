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



public final class RequestJobTypeIDocLoad extends RequestJobTypeLoad
{
   // -------------------------------------------------------------------------------------
   //                                       Constants
   // -------------------------------------------------------------------------------------
   public  static final String XML_ATTRIB_N_IDOCS_PER_TRANS = "nIDocsPerTrans";                    //$NON-NLS-1$
   public  static final String XML_ATTRIB_SAP_MSG_TYPE      = "sapMsgType";                        //$NON-NLS-1$
   public  static final String XML_TAG_IDOC_LOAD_STATUS     = "IDOCLoadStatus";                    //$NON-NLS-1$
           static final String XML_ATTRIB_ILS_SURR_KEY_FILE = "surrogateKeyFile";                  //$NON-NLS-1$
   private static final String XML_ATTRIB_ILS_OBJECTNAME    = "objectName";                        //$NON-NLS-1$
   
   
   // -------------------------------------------------------------------------------------
   //                                 Member Variables
   // -------------------------------------------------------------------------------------
   private int      _IDOCsPerTrans; 
   private String   _SurrogateKeyFile; 
   private String   _VendorName; 
   private String   _SAPMessageType;

   
   static String copyright()
   { return com.ibm.is.sappack.gen.common.request.Copyright.IBM_COPYRIGHT_SHORT; }   
   
   
   public RequestJobTypeIDocLoad(String parJobName, String parPhysModelId, 
                                 String parSAPMsgType)
   {
      super(parJobName, parPhysModelId);
      
      // set default values ...
      _IDOCsPerTrans    = Constants.JOB_GEN_DEFAULT_IDOC_PER_TRANS;
      _SurrogateKeyFile = null;
      _VendorName       = null;
      _SAPMessageType   = parSAPMsgType;
   } // end of RequestJobTypeIDOCLoad()
   

   RequestJobTypeIDocLoad(Node parJobTypeLoadNode)
   {
      super(parJobTypeLoadNode);

      Node   childNode;
      Node   targetNode;
      String tmpAttribValue;
      
      // +++++++++++++++++++++++++++++++++++++++++++++++++++
      // set attributes 
      // +++++++++++++++++++++++++++++++++++++++++++++++++++
      tmpAttribValue = XMLUtils.getNodeAttributeValue(parJobTypeLoadNode, XML_ATTRIB_N_IDOCS_PER_TRANS);
      if (tmpAttribValue != null)
      {
         _IDOCsPerTrans = Integer.parseInt(tmpAttribValue);
      }
      _SAPMessageType = XMLUtils.getNodeAttributeValue(parJobTypeLoadNode, XML_ATTRIB_SAP_MSG_TYPE);

      // ++++++++++++++++++++++++++++++++++++++++++++++++++++
      // Target: IDOC Load Status 
      // ++++++++++++++++++++++++++++++++++++++++++++++++++++
      targetNode = XMLUtils.getChildNode(parJobTypeLoadNode, RequestJobType.XML_TAG_TARGET);
      if (targetNode != null)
      {
         childNode = XMLUtils.getChildNode(targetNode, XML_TAG_IDOC_LOAD_STATUS);
         if (childNode != null)
         {
            _SurrogateKeyFile = XMLUtils.getNodeAttributeValue(childNode, XML_ATTRIB_ILS_SURR_KEY_FILE);
            _VendorName       = XMLUtils.getNodeAttributeValue(childNode, XML_ATTRIB_ILS_OBJECTNAME);
         } // end of if (childNode != null)
      } // end of if (targetNode != null)
      
   } // end of RequestJobTypeIDOCLoad()

   
   public boolean doCreateIDOCLoadStatusInfo()
   {
      return(_SurrogateKeyFile != null && _SurrogateKeyFile.length() > 0);
   }
   
   
   public String getILSSurrogateKeyFile()
   {
      return(_SurrogateKeyFile);
   }
   
   
   public String getILSVendorName()
   {
      return(_VendorName);
   }
   
   
   public final int getJobType()
   {
      return(TYPE_IDOC_LOAD_ID);
   }

   
   public final String getJobTypeAsString()
   {
      return(TYPE_IDOC_LOAD);
   }
   
   
   String getJobTypeAttribs()
   {
      StringBuffer xmlBuf;
      
      xmlBuf = new StringBuffer();
      
      xmlBuf.append(super.getJobTypeAttribs());
      xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_N_IDOCS_PER_TRANS, 
                                                    String.valueOf(_IDOCsPerTrans)));
      xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_SAP_MSG_TYPE, 
                                                    _SAPMessageType));

      return(xmlBuf.toString());      
   }

   
   public int getIDOCsPerTrans()
   {
      return(_IDOCsPerTrans);
   }
   
   
   public String getSAPMessageType()
   {
      return(_SAPMessageType);
   }

   
   String getTargetData()
   {
      StringBuffer xmlBuf;
      
      xmlBuf = new StringBuffer();
      
      xmlBuf.append(super.getTargetData());
      
      if (_SurrogateKeyFile != null && _SurrogateKeyFile.length() > 0)
      {
         xmlBuf.append("<");
         xmlBuf.append(XML_TAG_IDOC_LOAD_STATUS);
         
         xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_ILS_SURR_KEY_FILE, _SurrogateKeyFile));
         xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_ILS_OBJECTNAME, _VendorName));
         
         xmlBuf.append("/>");
      }

      return(xmlBuf.toString());
   }


   public void setIDOCLoadStatus(String parSurrogateKeyFile, String parVendorName)
   {
      _SurrogateKeyFile = parSurrogateKeyFile;
      _VendorName       = parVendorName;
   }
   
   
   public void setIDOCsPerTrans(int parCountIDOCsPerTrans)
   {
      if (parCountIDOCsPerTrans > 0)
      {
         _IDOCsPerTrans = parCountIDOCsPerTrans;
      }
   }

   
   void setSupportedTypes(SupportedColumnTypesMap parColumTypesMap, 
                          SupportedTableTypesMap parTableTypesMap)
   {
      parColumTypesMap.add(SupportedColumnTypesMap.COLUMN_TYPE_ENUM_IDOC_TYPE);
      parColumTypesMap.add(SupportedColumnTypesMap.COLUMN_TYPE_ENUM_IDOC_LOAD_STATUS);
      parColumTypesMap.add(SupportedColumnTypesMap.COLUMN_TYPE_ENUM_IDOC_LOAD_JOB_FIELD);
      parTableTypesMap.add(SupportedTableTypesMap.TABLE_TYPE_ENUM_IDOC_TYPE_LOAD);
      parTableTypesMap.add(SupportedTableTypesMap.TABLE_TYPE_ENUM_IDOC_TYPE_ALL);
      parTableTypesMap.add(SupportedTableTypesMap.TABLE_TYPE_ENUM_IDOC_LOAD_STATUS);
   }

   
   public String toString()
   {
      StringBuffer traceStringBuf;
      
      traceStringBuf = new StringBuffer();
      traceStringBuf.append(" - IdocsPerTrans: ");
      traceStringBuf.append(_IDOCsPerTrans);
      traceStringBuf.append(" - SAPMsgType: ");
      traceStringBuf.append(_SAPMessageType);
      traceStringBuf.append(" - SurrogateKeyFile: ");
      traceStringBuf.append(_SurrogateKeyFile);
      traceStringBuf.append(" - VendorName: ");
      traceStringBuf.append(_VendorName);
      
      return(super.toString() + traceStringBuf.toString());
   }

   
   public void validate() throws IllegalArgumentException
   {
      super.validate();
      
      checkParamExists(_SAPMessageType, "SAP Message Type");
   }

} // end of class RequestJobTypeIDOCLoad
