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

import com.ibm.is.sappack.gen.common.util.XMLUtils;




public class RequestJobTypeIDocExtract extends RequestJobTypeExtract
{
   // -------------------------------------------------------------------------------------
   //                                       Constants
   // -------------------------------------------------------------------------------------
   private static final boolean USE_OFFLINE_PROCESSING_DEFAULT      = false;
   
   private static final String  XML_ATTRIB_USE_OFFLINE_PROCESSING   = "useOfflineProcessing";    //$NON-NLS-1$
   
   
   // -------------------------------------------------------------------------------------
   //                                 Member Variables
   // -------------------------------------------------------------------------------------
   private boolean    _UseOfflineProcessing;

   static String copyright()
   { return com.ibm.is.sappack.gen.common.request.Copyright.IBM_COPYRIGHT_SHORT; }   
   
   public RequestJobTypeIDocExtract(String parJobName, String parPhysModelId)
   {
      super(parJobName, parPhysModelId);
      
      _UseOfflineProcessing = USE_OFFLINE_PROCESSING_DEFAULT;
   } // end of RequestJobTypeIDOCExtract()
   
   
   RequestJobTypeIDocExtract(Node parJobTypeExtractNode)
   {
      super(parJobTypeExtractNode);
      
      String tmpAttribValue;
      
      tmpAttribValue = XMLUtils.getNodeAttributeValue(parJobTypeExtractNode, XML_ATTRIB_USE_OFFLINE_PROCESSING);
      if (tmpAttribValue != null)
      {
         _UseOfflineProcessing = Boolean.valueOf(tmpAttribValue).booleanValue();
      }
      else
      {
         _UseOfflineProcessing = USE_OFFLINE_PROCESSING_DEFAULT;
      }
   } // end of RequestJobTypeIDOCExtract()

   
   public boolean doUseOfflineProcessing() 
   {
      return(_UseOfflineProcessing);
   }
   
   
   public int getJobType()
   {
      return(TYPE_IDOC_EXTRACT_ID);
   }

   
   public String getJobTypeAsString()
   {
      return(TYPE_IDOC_EXTRACT);
   }
   
   
   String getJobTypeAttribs()
   {
      StringBuffer xmlBuf;
      
      xmlBuf = new StringBuffer();
      
      xmlBuf.append(super.getJobTypeAttribs());
      xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_USE_OFFLINE_PROCESSING, 
                                                    String.valueOf(_UseOfflineProcessing)));
      
      return(xmlBuf.toString());      
   }

   
   void setSupportedTypes(SupportedColumnTypesMap parColumTypesMap, 
                          SupportedTableTypesMap parTableTypesMap)
   {
      parColumTypesMap.add(SupportedColumnTypesMap.COLUMN_TYPE_ENUM_IDOC_TYPE);
      parTableTypesMap.add(SupportedTableTypesMap.TABLE_TYPE_ENUM_IDOC_TYPE_EXTRACT);
      parTableTypesMap.add(SupportedTableTypesMap.TABLE_TYPE_ENUM_IDOC_TYPE_ALL);
   }

   
   public void setUseOflineProcessing(boolean parUseOfflineProcessing) 
   {
      _UseOfflineProcessing = parUseOfflineProcessing;
   }
   
} // end of class RequestJobTypeIDOCExtract
