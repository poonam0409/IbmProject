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


import java.util.Map;

import org.w3c.dom.Node;

import com.ibm.is.sappack.gen.common.Constants;
import com.ibm.is.sappack.gen.common.util.XMLUtils;


public class RequestJobTypeABAPExtract extends RequestJobTypeExtract
{
   // -------------------------------------------------------------------------------------
   //                                       Constants
   // -------------------------------------------------------------------------------------
   public  static final String XML_ATTRIB_N_FLOWS_PER_JOB  = "nFlowsPerJob";   //$NON-NLS-1$
   
   
   // -------------------------------------------------------------------------------------
   //                                 Member Variables
   // -------------------------------------------------------------------------------------
   private BGProcessingData _BackgroundProcData;
   private RFCData          _RFCData;
   private CPICData         _CPICData;
   private int              _NumberFlowsPerJob;


   static String copyright()
   { 
      return com.ibm.is.sappack.gen.common.request.Copyright.IBM_COPYRIGHT_SHORT; 
   }


   public RequestJobTypeABAPExtract(String parJobName, String parPhysModelId)
   {
      super(parJobName, parPhysModelId);
      
      _BackgroundProcData = new BGProcessingData();
      _RFCData            = new RFCData();
      _CPICData           = new CPICData();
      _NumberFlowsPerJob  = Constants.JOB_GEN_DEFAULT_FLOWS_PER_JOB;
   } // end of RequestJobTypeABAPExtract()
   
   
   public RequestJobTypeABAPExtract(Node parJobTypeExtractNode)
   {
      super(parJobTypeExtractNode);

      Node   bgProcessingNode;
      Node   rfcDataNode;
      Node   targetNode;
      Node   physModelNode;
      String tmpAttribValue;
      
      // +++++++++++++++++++++++++++++++++++++++++++++++++++
      // set attributes 
      // +++++++++++++++++++++++++++++++++++++++++++++++++++
      tmpAttribValue = XMLUtils.getNodeAttributeValue(parJobTypeExtractNode, XML_ATTRIB_N_FLOWS_PER_JOB);
      if (tmpAttribValue != null)
      {
         _NumberFlowsPerJob = Integer.parseInt(tmpAttribValue);
      }
      else
      {
         _NumberFlowsPerJob = Constants.JOB_GEN_DEFAULT_FLOWS_PER_JOB;
      }
      
      // ++++++++++++++++++++++++++++++++++++++++++++++++++++
      // set Physical Model (id) 
      // ++++++++++++++++++++++++++++++++++++++++++++++++++++
      physModelNode = XMLUtils.getChildNode(parJobTypeExtractNode, 
                                            RequestJobType.XML_TAG_PHYSICAL_MODEL);
      if (physModelNode != null)
      {
         setPhysModelId(physModelNode);
      } // end of if (physModelNode != null)
      
      // ++++++++++++++++++++++++++++++++++++++++++++++++++++
      // Source: Background processing Data 
      // ++++++++++++++++++++++++++++++++++++++++++++++++++++
      bgProcessingNode    = XMLUtils.getChildNode(parJobTypeExtractNode, 
                                                  BGProcessingData.XML_TAG_BG_PROC);
      _BackgroundProcData = new BGProcessingData(bgProcessingNode);
      
      // ++++++++++++++++++++++++++++++++++++++++++++++++++++
      // Source: RFC Data 
      // ++++++++++++++++++++++++++++++++++++++++++++++++++++
      rfcDataNode = XMLUtils.getChildNode(parJobTypeExtractNode, RFCData.XML_TAG_RFC_DATA);
      _RFCData    = new RFCData(rfcDataNode);
      
      Node cpicDataNode = XMLUtils.getChildNode(parJobTypeExtractNode, CPICData.XML_TAG_CPIC_DATA);
      _CPICData         = new CPICData(cpicDataNode);
      
      // ++++++++++++++++++++++++++++++++++++++++++++++++++++
      // Target: Persistence Data 
      // ++++++++++++++++++++++++++++++++++++++++++++++++++++
      targetNode = XMLUtils.getChildNode(parJobTypeExtractNode, RequestJobType.XML_TAG_TARGET);
      if (targetNode != null)
      {
         setPersistenceDataTrg(getPersistenceDataFromNode(targetNode));
      } // end of if (targetNode != null)
      
   } // end of RequestJobTypeABAPExtract()

   
   public void addRFCDestinationProgramId(String parRFCDestination, String parRFCProgramId)
   {
      _RFCData.addRFCDestinationProgramId(parRFCDestination, parRFCProgramId);
   }

   
   public BGProcessingData getBackgroundProcessingData()
   {
      return(_BackgroundProcData);
   }
   
   
   public int getFlowNumberPerJob() 
   {
      return(_NumberFlowsPerJob);
   }
   
   
   public int getJobType()
   {
      return(TYPE_ABAP_EXTRACT_ID);
   }

   
   public String getJobTypeAsString()
   {
      return(TYPE_ABAP_EXTRACT);
   }
   
   
   String getJobTypeAttribs()
   {
      StringBuffer xmlBuf;
      
      xmlBuf = new StringBuffer();
      
      xmlBuf.append(super.getJobTypeAttribs());
      xmlBuf.append(XMLUtils.createAttribPairString(XML_ATTRIB_N_FLOWS_PER_JOB, 
                                                    String.valueOf(_NumberFlowsPerJob)));

      return(xmlBuf.toString());      
   }

   
   String getJobTypeData()
   {
      return(super.getJobTypeData() + _RFCData.toXML() + _BackgroundProcData.toXML() + _CPICData.toXML());
   }
   
   
   public RFCData getRFCData()
   {
      return(_RFCData);
   }
   
   public CPICData getCPICData()
   {
	  return(_CPICData);
   }
   
   
   public void setRFCEnabled(boolean enabled) 
   {
	  _RFCData.setEnabled(enabled);   
   }
   
   public void setCPICEnabled(boolean enabled) 
   {
	  _CPICData.setEnabled(enabled);
   }
   
   public void setCPICUseSAPLogonDetails(boolean useSAPLogonDetails)
   {
	  _CPICData.setUseSAPLogonDetails(useSAPLogonDetails);
   }
   
   public void setCPICUser(String cpicUser) 
   {
	  _CPICData.setCpicUser(cpicUser); 
   }
   
   public void setCPICPassword(String cpicPassword) 
   {
	  _CPICData.setCpicPassword(cpicPassword);
   }
   
   public void setFlowNumberPerJob(int parFlowsPerJob) 
   {
      if (parFlowsPerJob > 0)
      {
         _NumberFlowsPerJob = parFlowsPerJob;
      }
   }
   
   
   public void setBackgroundProcessOptions(String parVariantName, int parStartDelay, 
                                           boolean doUseSAPVariant, boolean doKeepVariant) 
   {
      _BackgroundProcData.setBackgroundProcessOptions(parVariantName, parStartDelay, 
                                                      doUseSAPVariant, doKeepVariant);
   }

   
   public void setRFCDestinationProgramIdMap(Map destinationsMap) 
   {
      _RFCData.setDestinationsMap(destinationsMap);
   }
   
   public void setGenerateRFCDestinationNames(boolean generateRFCDestNames) {
	   _RFCData.setGenerateRFCDestinationNames(generateRFCDestNames);
   }
   
   public void setRFCDestinationNameGeneratorPrefix(String prefix) {
	   _RFCData.setRFCDestinationNameGenerationPrefix(prefix);
   }

   public void setRFCGateway(String parGatewayHostName, String parGatewayService)
   {
	   _RFCData.setGatewayHost(parGatewayHostName);
	   _RFCData.setGatewayService(parGatewayService);
   }

   public void setCreateRFCDestination(boolean createRfcDestAutomatically) {
	   _RFCData.setDoCreateRFCDest(createRfcDestAutomatically);
   }

   public void setSuppressAbapProgValidation(boolean selectSuppress) {
	   _RFCData.setSuppressAbapProgValidation(selectSuppress);
   }
   
   public void setRetryCount(int retryCount) {
		  _RFCData.setRetryCount(retryCount);
	  }
   
  public void setRetryInterval(int retryInt) {
	  _RFCData.setRetryInterval(retryInt);
  }
   
  public void setSuppressBackgroundJob(boolean suppressBackgroundJob) {
	  _RFCData.setSuppressBackgroundJob(suppressBackgroundJob);
  }
  
	public void setDeleteExistingRFCDestination(boolean deleteRfcDestIfExisting) {
		_RFCData.setDoCleanUpRFCDestAfterReq(deleteRfcDestIfExisting);
   }
   
   void setSupportedTypes(SupportedColumnTypesMap parColumTypesMap, 
                          SupportedTableTypesMap parTableTypesMap)
   {
      parColumTypesMap.add(SupportedColumnTypesMap.COLUMN_TYPE_ENUM_LOGICAL_TABLE);
      parColumTypesMap.add(SupportedColumnTypesMap.COLUMN_TYPE_ENUM_JOINED_CHECK_AND_TEXT_TABLE);
      parColumTypesMap.add(SupportedColumnTypesMap.COLUMN_TYPE_ENUM_REFERENCE_CHECK_TABLE);
      parColumTypesMap.add(SupportedColumnTypesMap.COLUMN_TYPE_ENUM_NON_REFERENCE_CHECK_TABLE);
      parColumTypesMap.add(SupportedColumnTypesMap.COLUMN_TYPE_ENUM_REFERENCE_TEXT_TABLE);
      parTableTypesMap.add(SupportedTableTypesMap.TABLE_TYPE_ENUM_LOGICAL_TABLE);
      parTableTypesMap.add(SupportedTableTypesMap.TABLE_TYPE_ENUM_JOINED_CHECK_AND_TEXT_TABLE);
      parTableTypesMap.add(SupportedTableTypesMap.TABLE_TYPE_ENUM_REFERENCE_CHECK_TABLE);
      parTableTypesMap.add(SupportedTableTypesMap.TABLE_TYPE_ENUM_NON_REFERENCE_CHECK_TABLE);
      parTableTypesMap.add(SupportedTableTypesMap.TABLE_TYPE_ENUM_REFERENCE_TEXT_TABLE);
   }

   
   public String toString()
   {
      StringBuffer traceStringBuf;
      
      traceStringBuf = new StringBuffer();
      traceStringBuf.append(" - flows per job: ");
      traceStringBuf.append(_NumberFlowsPerJob);
      traceStringBuf.append(Constants.NEWLINE);
      traceStringBuf.append("RFC Data: ");
      traceStringBuf.append(_RFCData);
      traceStringBuf.append(Constants.NEWLINE);
      traceStringBuf.append("CPIC Data: ");
      traceStringBuf.append(_CPICData);
      traceStringBuf.append(Constants.NEWLINE);
      traceStringBuf.append("Background Progr Data: ");
      traceStringBuf.append(_BackgroundProcData);
      
      return(super.toString() + traceStringBuf.toString());
   }

   
   public void validate() throws IllegalArgumentException
   {
      super.validate();
      
      if (_RFCData.getEnabled()) {
			checkParamExists(_RFCData.getGatewayHost(), "RFC Gateway Host");
			checkParamExists(_RFCData.getGatewayService(), "RFC Gateway Service");
			
			if (_RFCData.getGenerateRFCDestinationNames()) {
				checkParamExists(_RFCData.getRFCDestinationNameGenerationPrefix(), "RFC Destination Name Prefix");
			} else {
				if (_RFCData.getDestinationsMap().size() < 1) {
					throw new IllegalArgumentException("RFC Destination/Program Id missing.");
				}
			}
		}
      // TODO: CPIC
      
   }
} // end of class RequestJobTypeABAPExtract
