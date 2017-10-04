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
import com.ibm.is.sappack.gen.common.FileStageData;
import com.ibm.is.sappack.gen.common.PersistenceData;
import com.ibm.is.sappack.gen.common.util.XMLUtils;


public class RequestJobTypeMovement extends RequestJobType
{
   // -------------------------------------------------------------------------------------
   //                                       Constants
   // -------------------------------------------------------------------------------------
   public  static final String XML_TAG_NAME_REJECT_FILE         = "RejectFile";          //$NON-NLS-1$
   public  static final String XML_ATTRIB_REJECT_FILE_FILE      = "file";                //$NON-NLS-1$
   
   
   // -------------------------------------------------------------------------------------
   //                                 Member Variables
   // -------------------------------------------------------------------------------------
   private String   _PhysicalModelIdSrc;
   private String   _PhysicalModelIdTrg;
   private String   _RejectFilePath;
   private int      _NumberFlowsPerJob;


   static String copyright()
   { 
   	return com.ibm.is.sappack.gen.common.request.Copyright.IBM_COPYRIGHT_SHORT; 
   }


   public RequestJobTypeMovement(String parJobName)
   {
      this(parJobName, null, null);
   } // end of RequestJobTypeMovement()
   
   
   public RequestJobTypeMovement(String parJobName, String parPhysicalModelIdSrc, 
                                 String parPhysicalModelIdTrg)
   {
      super(parJobName);
      
      // set default values ...
      _PhysicalModelIdSrc = parPhysicalModelIdSrc;
      _PhysicalModelIdTrg = parPhysicalModelIdTrg;
      _RejectFilePath     = null;
      _NumberFlowsPerJob  = Constants.JOB_GEN_DEFAULT_FLOWS_PER_JOB;
   } // end of RequestJobTypeMovement()
   
   
   RequestJobTypeMovement(Node parJobTypeMovementNode)
   {
      super(parJobTypeMovementNode);
      
      Node   childNode;
      Node   physModelNode;
      String tmpAttribValue;
      
      // +++++++++++++++++++++++++++++++++++++++++++++++++++
      // set attributes 
      // +++++++++++++++++++++++++++++++++++++++++++++++++++
      tmpAttribValue = XMLUtils.getNodeAttributeValue(parJobTypeMovementNode, 
                                                      RequestJobTypeABAPExtract.XML_ATTRIB_N_FLOWS_PER_JOB);
      if (tmpAttribValue != null)
      {
         _NumberFlowsPerJob = Integer.parseInt(tmpAttribValue);
      }
      
      // +++++++++++++++++++++++++++++++++++++++++++++++++++
      // set RejectFile path 
      // +++++++++++++++++++++++++++++++++++++++++++++++++++
      childNode = XMLUtils.getChildNode(parJobTypeMovementNode, XML_TAG_NAME_REJECT_FILE);
      if (childNode != null)
      {
         _RejectFilePath = XMLUtils.getNodeAttributeValue(childNode, XML_ATTRIB_REJECT_FILE_FILE);
      } // end of if (childNode != null)
      
      // ++++++++++++++++++++++++++++++++++++++++++++++++++++
      // Source: set Physical Model (id) and Persistence Data 
      // ++++++++++++++++++++++++++++++++++++++++++++++++++++
      childNode = XMLUtils.getChildNode(parJobTypeMovementNode, RequestJobType.XML_TAG_SOURCE);
      if (childNode != null)
      {
         physModelNode = XMLUtils.getChildNode(childNode, RequestJobType.XML_TAG_PHYSICAL_MODEL);
         if (physModelNode != null)
         {
            setPhysModelIdSrc(physModelNode);
         } // end of if (physModelNode != null)
         
         setPersistenceDataSrc(getPersistenceDataFromNode(childNode));
      } // end of if (childNode != null)
      
      // ++++++++++++++++++++++++++++++++++++++++++++++++++++
      // Target: set Physical Model (id) and Persistence Data 
      // ++++++++++++++++++++++++++++++++++++++++++++++++++++
      childNode = XMLUtils.getChildNode(parJobTypeMovementNode, RequestJobType.XML_TAG_TARGET);
      if (childNode != null)
      {
         physModelNode = XMLUtils.getChildNode(childNode, RequestJobType.XML_TAG_PHYSICAL_MODEL);
         if (physModelNode != null)
         {
            setPhysModelIdTrg(physModelNode);
         } // end of if (childNode != null)
         setPersistenceDataTrg(getPersistenceDataFromNode(childNode));
      } // end of if (childNode != null)
      
   } // end of RequestJobTypeMovement()

   
   public String getDisplayModelId()
   {
      return(getSrcPhysicalModelId());
   }
   
   
   public void setSAPSystem(SAPSystemData parSAPSystemData) 
   {
      throw new IllegalArgumentException("SAPSystem data is not required and not allowed for this class");
   }

   
   public int getFlowNumberPerJob() 
   {
      return(_NumberFlowsPerJob);
   }
   
   
   public int getJobType()
   {
      return(TYPE_MOVEMENT_ID);
   }

   
   public String getJobTypeAsString()
   {
      return(TYPE_MOVEMENT);
   }
   
   
   String getJobTypeAttribs()
   {
      StringBuffer xmlBuf;
      
      xmlBuf = new StringBuffer();
      
      xmlBuf.append(XMLUtils.createAttribPairString(RequestJobTypeABAPExtract.XML_ATTRIB_N_FLOWS_PER_JOB, 
                                                    String.valueOf(_NumberFlowsPerJob)));

      return(xmlBuf.toString());      
   }

   
   public PersistenceData getPersistenceDataSrc()
   {
      return(super.getPersistenceDataSrc());
   }

   
   public PersistenceData getPersistenceDataTrg()
   {
      return(super.getPersistenceDataTrg());
   }

   
   public String getSrcPhysicalModelId()
   {
      return(_PhysicalModelIdSrc);
   }


   public String getTrgPhysicalModelId()
   {
      return(_PhysicalModelIdTrg);
   }


   public String getRejectFilePath()
   {
      return(_RejectFilePath);
   }

   
   String getSourceData()
   {
      return(getPhysicalModelXML(_PhysicalModelIdSrc, super.getSourceData()));
   }
   
   
   String getTargetData()
   {
      StringBuffer targetDataXMLBuf;
      
      targetDataXMLBuf = new StringBuffer();
      targetDataXMLBuf.append(getPhysicalModelXML(_PhysicalModelIdTrg, Constants.EMPTY_STRING));
      
      if (_RejectFilePath != null)
      {
         targetDataXMLBuf.append("<");
         targetDataXMLBuf.append(XML_TAG_NAME_REJECT_FILE);
         targetDataXMLBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_REJECT_FILE_FILE, 
                                 _RejectFilePath));
         targetDataXMLBuf.append(" />");
      }
      
      targetDataXMLBuf.append(super.getTargetData());
      
      return(targetDataXMLBuf.toString());
   }
   
   
   public void setFlowNumberPerJob(int parFlowsPerJob) 
   {
      if (parFlowsPerJob > 0)
      {
         _NumberFlowsPerJob = parFlowsPerJob;
      }
   }
   
   
   public void setSrcPersistenceData(PersistenceData parPersistData)
   {
      super.setPersistenceDataSrc(parPersistData);
   }
   
   
   void setPhysModelIdSrc(Node parNode)
   {
      setPhysicalModelIdSrc(XMLUtils.getNodeAttributeValue(parNode, XML_ATTRIB_PHY_MODEL_ID));
   }
   
   
   void setPhysicalModelIdSrc(String parModelId)
   {
      _PhysicalModelIdSrc = parModelId;
   }


   public void setTrgPersistenceData(PersistenceData parPersistData)
   {
      super.setPersistenceDataTrg(parPersistData);
   }
   
   
   void setPhysModelIdTrg(Node parNode)
   {
      setPhysicalModelIdTrg(XMLUtils.getNodeAttributeValue(parNode, XML_ATTRIB_PHY_MODEL_ID));
   }
   
   
   void setPhysicalModelIdTrg(String parModelId)
   {
      _PhysicalModelIdTrg = parModelId;
   }

   
   public void setSrcPhysicalModelId(String parModelId)
   {
      setPhysicalModelIdSrc(parModelId);
   }
   
   
   public void setTrgPhysicalModelId(String parModelId)
   {
      setPhysicalModelIdTrg(parModelId);
   }


   public void setRejectFilePath(String rejectFilePath) {
		_RejectFilePath = rejectFilePath;

		if (_RejectFilePath != null && _RejectFilePath.trim().length() == 0) {
			_RejectFilePath = null;
		}
	}

   
   void setSupportedTypes(SupportedColumnTypesMap parColumTypesMap, 
                          SupportedTableTypesMap parTableTypesMap)
   {
      parColumTypesMap.add(SupportedColumnTypesMap.COLUMN_TYPE_ENUM_ALL_TYPES);
//      parColumTypesMap.add(SupportedColumnTypesMap.COLUMN_TYPE_ENUM_IDOC_TYPE);
//      parColumTypesMap.add(SupportedColumnTypesMap.COLUMN_TYPE_ENUM_CW_TECH_FIELD);
//      parColumTypesMap.add(SupportedColumnTypesMap.COLUMN_TYPE_ENUM_LOGICAL_TABLE);
      parTableTypesMap.add(SupportedTableTypesMap.TABLE_TYPE_ENUM_IDOC_TYPE_EXTRACT);
      parTableTypesMap.add(SupportedTableTypesMap.TABLE_TYPE_ENUM_IDOC_TYPE_LOAD);
      parTableTypesMap.add(SupportedTableTypesMap.TABLE_TYPE_ENUM_IDOC_TYPE_ALL);
      parTableTypesMap.add(SupportedTableTypesMap.TABLE_TYPE_ENUM_LOGICAL_TABLE);
   }


   public String toString()
   {
      StringBuffer traceStringBuf;
      
      traceStringBuf = new StringBuffer();
      traceStringBuf.append(" - flows per job: ");
      traceStringBuf.append(_NumberFlowsPerJob);
      traceStringBuf.append(" - PhysicalModelIdSrc: ");
      traceStringBuf.append(_PhysicalModelIdSrc);
      traceStringBuf.append(" - PhysicalModelIdTrg: ");
      traceStringBuf.append(_PhysicalModelIdTrg);
      traceStringBuf.append(" -  RejectFilePath: ");
      traceStringBuf.append(_RejectFilePath);
      
      return(super.toString() + traceStringBuf.toString());
   }

   
   public void validate() throws IllegalArgumentException
   {
      PersistenceData tmpTrgPersistenceData;
      
      checkParamExists(_PhysicalModelIdSrc, "Source Model Id");
      checkParamExists(getPersistenceDataSrc(), "Source ODBCStage or FileStage");
      checkParamExists(_PhysicalModelIdTrg, "Target Model Id");
//      checkParamExists(getPersistenceDataTrg(), "Target ODBCStage or FileStage");
      
      // if there is no target persistence specified ...
      tmpTrgPersistenceData = getPersistenceDataTrg();
      if (tmpTrgPersistenceData == null)
      {
         super.setPersistenceDataTrg(getPersistenceDataSrc());
      }
      
      // REJECT link is not allowed for target FileStages
      if (getPersistenceDataTrg() instanceof FileStageData)
      {
         if (getRejectFilePath() != null)
         {
            throw new IllegalArgumentException("Reject links are not allowed when using File stages for target persistence.");  
         }
      }
      if (tmpTrgPersistenceData == null)
      {
         super.setPersistenceDataTrg(getPersistenceDataSrc());
      }
   }
   
} // end of class JobTypeMovement
