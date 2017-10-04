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





public final class RequestJobTypeIDocMIHLoad extends RequestJobTypeIDocExtract
{
   // -------------------------------------------------------------------------------------
   //                                       Constants
   // -------------------------------------------------------------------------------------
   private   static final String XML_ATTRIB_INSTANCE_ID      = RequestJobTypeMIHLoad.XML_ATTRIB_INSTANCE_ID;
   private   static final String XML_ATTRIB_ONE_LOOKUP_STAGE = "oneLookupStage";
   
   
   // -------------------------------------------------------------------------------------
   //                                 Member Variables
   // -------------------------------------------------------------------------------------
   private int     _InstanceIdentifier;
   private boolean _UseOneLookupStage;

   
   static String copyright()
   { 
      return com.ibm.is.sappack.gen.common.request.Copyright.IBM_COPYRIGHT_SHORT; 
   }   
   
   
   public RequestJobTypeIDocMIHLoad(String parJobName, String parPhysModelId,
                                    int parInstanceIdentifier)
   {
      super(parJobName, parPhysModelId);
      
      // check the passed instance id
      RequestJobTypeMIHLoad.checkInstanceId(parInstanceIdentifier);
      
      // set values ...
      _InstanceIdentifier = parInstanceIdentifier;
      _UseOneLookupStage  = true;
   } // end of RequestJobTypeIDOCMIHLoad()
   
   
   RequestJobTypeIDocMIHLoad(Node parJobTypeLoadNode)
   {
      super(parJobTypeLoadNode);
      
      String tmpAttribValue;

      _UseOneLookupStage = false;
      
      // +++++++++++++++++++++++++++++++++++++++++++++++++++
      // set attributes 
      // +++++++++++++++++++++++++++++++++++++++++++++++++++
      tmpAttribValue = XMLUtils.getNodeAttributeValue(parJobTypeLoadNode, XML_ATTRIB_INSTANCE_ID);
      if (tmpAttribValue != null)
      {
         _InstanceIdentifier = Integer.parseInt(tmpAttribValue);
      }
      tmpAttribValue = XMLUtils.getNodeAttributeValue(parJobTypeLoadNode, XML_ATTRIB_ONE_LOOKUP_STAGE);
      if (tmpAttribValue != null)
      {
         _UseOneLookupStage = Boolean.valueOf(tmpAttribValue).booleanValue();
      }
      
      // validate the member variables
      validate();
   } // end of RequestJobTypeIDOCMIHLoad()

   
   public boolean useOneLookupStage()
   {
      return(_UseOneLookupStage);
   }

   
   public int getInstanceIdentifier()
   {
      return(_InstanceIdentifier);
   }
   
   
   public int getJobType()
   {
      return(TYPE_IDOC_MIH_LOAD_ID);
   }

   
   public String getJobTypeAsString()
   {
      return(TYPE_IDOC_MIH_LOAD);
   }

   
   String getJobTypeAttribs()
   {
      StringBuffer xmlBuf;
      
      xmlBuf = new StringBuffer();
      
      xmlBuf.append(super.getJobTypeAttribs()); 
      xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_INSTANCE_ID, String.valueOf(_InstanceIdentifier)));

      return(xmlBuf.toString());      
   }

   
   void setSupportedTypes(SupportedColumnTypesMap parColumTypesMap, 
                          SupportedTableTypesMap parTableTypesMap)
   {
      parColumTypesMap.add(SupportedColumnTypesMap.COLUMN_TYPE_ENUM_JOINED_CHECK_AND_TEXT_TABLE);
      parColumTypesMap.add(SupportedColumnTypesMap.COLUMN_TYPE_ENUM_IDOC_TYPE);
      parColumTypesMap.add(SupportedColumnTypesMap.COLUMN_TYPE_ENUM_IDOC_LOAD_JOB_FIELD);
      parTableTypesMap.add(SupportedTableTypesMap.TABLE_TYPE_ENUM_IDOC_TYPE_EXTRACT);
      parTableTypesMap.add(SupportedTableTypesMap.TABLE_TYPE_ENUM_IDOC_TYPE_ALL);
   }

   
   public void setUseOneLookupStage(boolean useOneLookupStage)
   {
      _UseOneLookupStage = useOneLookupStage;
   }

   
   public String toString()
   {
      StringBuffer traceStringBuf;
      
      traceStringBuf = new StringBuffer();
      traceStringBuf.append(" - instance Id: ");
      traceStringBuf.append(_InstanceIdentifier);
      traceStringBuf.append(" - use one Lookup Stage: ");
      traceStringBuf.append(_UseOneLookupStage);
      
      return(super.toString() + traceStringBuf.toString());
   }

   
   public void validate() throws IllegalArgumentException
   {
      super.validate();
      
      // check the passed instance id
      RequestJobTypeMIHLoad.checkInstanceId(_InstanceIdentifier);
   }
   
} // end of class RequestJobTypeIDOCMIHLoad
