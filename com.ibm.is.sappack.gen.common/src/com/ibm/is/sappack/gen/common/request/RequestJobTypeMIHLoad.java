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




public final class RequestJobTypeMIHLoad extends RequestJobTypeMovement
{
   // -------------------------------------------------------------------------------------
   //                                       Constants
   // -------------------------------------------------------------------------------------
   private  static final String PHYSICAL_MODEL_ID_TRG_EXT     = "Trg";             //$NON-NLS-1$
            static final String XML_ATTRIB_INSTANCE_ID        = "instanceId";      //$NON-NLS-1$
   
   
   // -------------------------------------------------------------------------------------
   //                                 Member Variables
   // -------------------------------------------------------------------------------------
   private int _InstanceIdentifier;
   
   
   static String copyright()
   { return com.ibm.is.sappack.gen.common.request.Copyright.IBM_COPYRIGHT_SHORT; }   
   
   
   public RequestJobTypeMIHLoad(String parJobName, String parPhysModelId, 
                                int parInstanceIdentifier)
   {
      super(parJobName);
      
      // check the passed instance id
      checkInstanceId(parInstanceIdentifier);
      
      // set values ...
      _InstanceIdentifier = parInstanceIdentifier;
      super.setPhysicalModelIdSrc(parPhysModelId);
      super.setPhysicalModelIdTrg(parPhysModelId + PHYSICAL_MODEL_ID_TRG_EXT);
   } // end of RequestJobTypeMovement()
   
   
   RequestJobTypeMIHLoad(Node parJobTypeMIHLoadNode)
   {
      super(parJobTypeMIHLoadNode);
      
      String tmpAttribValue;
      
      // +++++++++++++++++++++++++++++++++++++++++++++++++++
      // set attributes 
      // +++++++++++++++++++++++++++++++++++++++++++++++++++
      tmpAttribValue = XMLUtils.getNodeAttributeValue(parJobTypeMIHLoadNode, XML_ATTRIB_INSTANCE_ID);
      if (tmpAttribValue != null)
      {
         _InstanceIdentifier = Integer.parseInt(tmpAttribValue);
      }
   } // end of RequestJobTypeMIHLoad()

   
   public static void checkInstanceId(int parInstanceId)
   {
      if (parInstanceId < 0 || parInstanceId > 99)
      {
         throw new IllegalArgumentException("Instance Identifier must be a value between 0 and 99");
      }
   } // end of checkInstanceId()
   
   
   public int getInstanceIdentifier()
   {
      return(_InstanceIdentifier);
   }
   
   
   public int getJobType()
   {
      return(TYPE_MIH_LOAD_ID);
   }

   
   public String getJobTypeAsString()
   {
      return(TYPE_MIH_LOAD);
   }
   
   
   String getJobTypeAttribs()
   {
      StringBuffer xmlBuf;
      
      xmlBuf = new StringBuffer();
      
      xmlBuf.append(super.getJobTypeAttribs()); 
      xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_INSTANCE_ID, 
                                                    String.valueOf(_InstanceIdentifier)));

      return(xmlBuf.toString());      
   }

   
   public void setSrcPhysicalModelId(String parModelId)
   {
      throw new IllegalArgumentException("Method is no allowed to be called.");
   }
   
   
   void setSupportedTypes(SupportedColumnTypesMap parColumTypesMap, 
                          SupportedTableTypesMap parTableTypesMap)
   {
      parColumTypesMap.add(SupportedColumnTypesMap.COLUMN_TYPE_ENUM_IDOC_TYPE);
      parColumTypesMap.add(SupportedColumnTypesMap.COLUMN_TYPE_ENUM_IDOC_LOAD_JOB_FIELD);
      parTableTypesMap.add(SupportedTableTypesMap.TABLE_TYPE_ENUM_IDOC_TYPE_EXTRACT);
      parTableTypesMap.add(SupportedTableTypesMap.TABLE_TYPE_ENUM_IDOC_TYPE_ALL);
   }
   
   
   public void setTrgPhysicalModelId(String parModelId)
   {
      throw new IllegalArgumentException("Method is no allowed to be called.");
   }

   
   public String toString()
   {
      StringBuffer traceStringBuf;
      
      traceStringBuf = new StringBuffer();
      traceStringBuf.append(" - instance Id: ");
      traceStringBuf.append(_InstanceIdentifier);
      
      return(super.toString() + traceStringBuf.toString());
   }
   
} // end of class RequestJobTypeMIHLoad
