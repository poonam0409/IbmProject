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


import org.w3c.dom.Node;

import com.ibm.is.sappack.gen.common.Constants;
import com.ibm.is.sappack.gen.common.PersistenceData;
import com.ibm.is.sappack.gen.common.util.XMLUtils;


abstract class RequestJobTypeLoad extends RequestJobType
{
   // -------------------------------------------------------------------------------------
   //                                       Constants
   // -------------------------------------------------------------------------------------
   
   
   // -------------------------------------------------------------------------------------
   //                                 Member Variables
   // -------------------------------------------------------------------------------------
   private String    _PhysicalModelId;


   static String copyright()
   { 
      return com.ibm.is.sappack.gen.common.request.Copyright.IBM_COPYRIGHT_SHORT; 
   }   


   public RequestJobTypeLoad(String parJobName, String parPhysModelId)
   {
      super(parJobName);
      
      // check Physical Model Id
      if (parPhysModelId == null || parPhysModelId.length() == 0)
      {
         throw new IllegalArgumentException("No Physical Model Id was specified !!!");
      }
      
      _PhysicalModelId  = parPhysModelId;
   } // end of RequestJobTypeLoad()

   
   RequestJobTypeLoad(Node parJobTypeLoadNode)
   {
      super(parJobTypeLoadNode);

      Node   childNode;
      Node   sourceNode;
      Node   physModelNode;
      
      // +++++++++++++++++++++++++++++++++++++++++++++++++++
      // set attributes 
      // +++++++++++++++++++++++++++++++++++++++++++++++++++

      // ++++++++++++++++++++++++++++++++++++++++++++++++++++
      // set Physical Model (id) 
      // ++++++++++++++++++++++++++++++++++++++++++++++++++++
      physModelNode = XMLUtils.getChildNode(parJobTypeLoadNode, RequestJobType.XML_TAG_PHYSICAL_MODEL);
      if (physModelNode != null)
      {
         setPhysModelId(physModelNode);
      } // end of if (physModelNode != null)
      
      // ++++++++++++++++++++++++++++++++++++++++++++++++++++
      // set SAP System 
      // ++++++++++++++++++++++++++++++++++++++++++++++++++++
      childNode = XMLUtils.getChildNode(parJobTypeLoadNode, SAPSystemData.XML_TAG_NAME_SAP_SYSTEM);
      if (childNode != null)
      {
         setSAPSystem(childNode);
      } // end of if (childNode != null)
      
      // ++++++++++++++++++++++++++++++++++++++++++++++++++++
      // Source: Persistence Data 
      // ++++++++++++++++++++++++++++++++++++++++++++++++++++
      sourceNode = XMLUtils.getChildNode(parJobTypeLoadNode, RequestJobType.XML_TAG_SOURCE);
      if (sourceNode != null)
      {
         setPersistenceDataSrc(getPersistenceDataFromNode(sourceNode));
      } // end of if (sourceNode != null)
   } // end of RequestJobTypeLoad()

   
   public String getDisplayModelId()
   {
      return(getPhysicalModelId());
   }

   
   String getJobTypeAttribs()
   {
      return(Constants.EMPTY_STRING); 
   }

   
   String getJobTypeData()
   {
      StringBuffer xmlBuf;
      
      xmlBuf = new StringBuffer();
      
      xmlBuf.append(super.getJobTypeData());
      
      xmlBuf.append(getPhysicalModelXML(_PhysicalModelId, Constants.EMPTY_STRING));
      xmlBuf.append(getSAPSystemXML());

      return(xmlBuf.toString());      
   }


   public PersistenceData getPersistenceData()
   {
      return(getPersistenceDataSrc());
   }

   
   public String getPhysicalModelId()
   {
      return(_PhysicalModelId);
   }
   
   public void setPersistenceData(PersistenceData parPersistData)
   {
      setPersistenceDataSrc(parPersistData);
   }
   
   
   void setPhysModelId(Node parNode)
   {
      _PhysicalModelId = XMLUtils.getNodeAttributeValue(parNode, XML_ATTRIB_PHY_MODEL_ID);
   }

   
   public String toString()
   {
      StringBuffer traceStringBuf;
      
      traceStringBuf = new StringBuffer();
      traceStringBuf.append("PhysModelId: ");
      traceStringBuf.append(_PhysicalModelId);
      
      return(super.toString() + traceStringBuf.toString());
   }

   
   public void validate() throws IllegalArgumentException
   {
      checkParamExists(_PhysicalModelId, "Physical Model Id");
      checkParamExists(getPersistenceDataSrc(), "ODBCStage or FileStage");
      checkParamExists(getSAPSystem(), "SAP System");
   }
   
} // end of class RequestJobTypeLoad
