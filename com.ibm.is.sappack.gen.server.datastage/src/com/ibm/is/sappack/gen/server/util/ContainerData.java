//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2009, 2013                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.is.sappack.gen.server.util
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************

package com.ibm.is.sappack.gen.server.util;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.common.util.EList;

import ASCLModel.JobComponent;
import ASCLModel.MainObject;
import DataStageX.DSInputPin;
import DataStageX.DSLink;
import DataStageX.DSLocalContainerDef;
import DataStageX.DSOutputPin;
import DataStageX.DSStage;

import com.ibm.is.sappack.gen.server.common.DSStageTypeEnum;
import com.ibm.is.sappack.gen.server.datastage.DataStageObjectFactory;


public final class ContainerData implements JobObject
{
   // -------------------------------------------------------------------------------------
   //                                  Constants
   // -------------------------------------------------------------------------------------
   
   // -------------------------------------------------------------------------------------
   //                                  Member Variables
   // -------------------------------------------------------------------------------------
   private DSLocalContainerDef   _Container;
   private DSStageTypeEnum       _Type;
   private ObjectParamMap        _ParamMap;
   private List                  _MetaBagList;
   
   
   static String copyright()
   { 
      return com.ibm.is.sappack.gen.server.util.Copyright.IBM_COPYRIGHT_SHORT; 
   }

   
   public ContainerData(DSLocalContainerDef parContainer)
   {
      this(parContainer, null);
   } // end of ContainerData()
   
   
   public ContainerData(DSLocalContainerDef parContainer, ObjectParamMap parLinkParamMap) 
   {
      if (parContainer == null)
      {
         throw new IllegalArgumentException("Container instance must not be null.");
      }
      _Container   = parContainer;
      _Type        = DSStageTypeEnum.LOCAL_CONTAINER_DEF_LITERAL;
      _ParamMap    = parLinkParamMap;
      _MetaBagList = new ArrayList();
   } // end of ContainerData()
   
   
   public StageData getContainerStage()
   {
      StageData retContainerStage;
      
      if (_Container.getDefines_Stage().size() == 0)
      {
         retContainerStage = null;
      }
      else
      {
         retContainerStage = new StageData((DSStage) _Container.getDefines_Stage().get(0));
      }
      
      return(retContainerStage);
   } // end of getContainerStage()
   
   
   public EList getContains_FlowVariable()
   {
      return(_Container.getContains_FlowVariable());
   } // end of getContains_FlowVariable()
   
   
   public EList getHas_InputPin()
   {
      return(_Container.getHas_InputPin());
   } // end of getHas_InputPin()
   
   
   public EList getHas_OutputPin()
   {
      return(_Container.getHas_OutputPin());
   } // end of getHas_OutputPin()
   
   
   public EList getInputFlowVars()
   {
      StageData  containerStage;
      DSInputPin tmpInputPin;
      DSLink     tmpInputLink;
      EList      retFlowVarList;
      
      // get the FlowVar list from the container's stage Input Pin link
      containerStage = getContainerStage();
      tmpInputPin    = (DSInputPin) containerStage.getHas_InputPin().get(0);
      tmpInputLink   = (DSLink) tmpInputPin.getIsTargetOf_Link();
      retFlowVarList = tmpInputLink.getContains_FlowVariable();
      
      return(retFlowVarList);
   } // end of getInputFlowVars()
   
   
   public String getInputPins()
   {
      StringBuffer pinBuffer;
      Iterator     pinIter;
      
      // update DesignView with names of the existing InputPins
      pinBuffer = new StringBuffer();
      pinIter   = _Container.getHas_InputPin().iterator();
      while(pinIter.hasNext()) {
         pinBuffer.append(((DSInputPin) pinIter.next()).getInternalID());
         
         if (pinIter.hasNext()) {
            pinBuffer.append(DataStageObjectFactory.DS_VIEW_VALUE_SEPARATOR);
         }
      } // end of while(pinIter.hasNext())
      
      return(pinBuffer.toString());
   } // end of getInputPins()
   
   
   public String getInternalID()
   {
      StageData containerStage;
      String    retInternalID;

      containerStage = getContainerStage();
      retInternalID  = containerStage.getInternalID();
      
      // replace the current id prefix by the container id prefix
      if (retInternalID.startsWith(DataStageObjectFactory.CONTAINER_STAGE_ID_PREFIX))
      {
         int prefixLen = DataStageObjectFactory.CONTAINER_STAGE_ID_PREFIX.length();
         
         retInternalID = DataStageObjectFactory.CONTAINER_ID_PREFIX + retInternalID.substring(prefixLen);
      }
      
      return(retInternalID);
//    return(_Container.getShortDescription());
   } // end of getInternalID()

   
   public JobComponent getJobComponent()
   {
      return(_Container);
   }
   
   
   public ObjectParamMap getLinkParams()
   {
      return(_ParamMap);
   } // end of getLinkParams()

   
   public List getMetaBagList()
   {
      return(_MetaBagList);
   }
   
   
   public String getName()
   {
      return(_Container.getName());
   } // end of getName()
   
   
   public EList getOutputFlowVars()
   {
      DSInputPin tmpInputPin;
      DSLink     tmpInputLink;
      EList      retFlowVarList;
      
      // get the FlowVar list from the container's Input Pin link
      tmpInputPin    = (DSInputPin) _Container.getHas_InputPin().get(0);
      tmpInputLink   = (DSLink) tmpInputPin.getIsTargetOf_Link();
      retFlowVarList = tmpInputLink.getContains_FlowVariable();
      
      return(retFlowVarList);
   } // end of getOutputFlowVars()
   
   
   public String getOutputPins()
   {
      StringBuffer pinBuffer;
      Iterator     pinIter;
      
      // update DesignView with names of the existing InputPins
      pinBuffer = new StringBuffer();
      pinIter   = _Container.getHas_OutputPin().iterator();
      while(pinIter.hasNext()) {
         pinBuffer.append(((DSOutputPin) pinIter.next()).getInternalID());
         
         if (pinIter.hasNext()) {
            pinBuffer.append(DataStageObjectFactory.DS_VIEW_VALUE_SEPARATOR);
         }
      } // end of while(pinIter.hasNext())
      
      return(pinBuffer.toString());
   } // end of getOutputPins()
   
   
   public MainObject getParent()
   {
      MainObject retContainer;
      
      // is child of a DSJobDef ?
      if (_Container.getOf_JobDef() == null)
      {
         // or is child of a DSLocalContainerDef ?
         if (_Container.getOf_ContainerDef() == null)
         {
            retContainer = null;
         }
         else
         {
            retContainer = _Container.getOf_ContainerDef();
         } // end of (else) if (_Stage.getOf_ContainerDef() == null)
      }
      else
      {
         retContainer = _Container.getOf_JobDef();
      } // end of (else) if (_Container.getOf_JobDef() == null)
      
      return(retContainer);
   } // end of getParent()

   
   public String getStageTypeClassName()
   {
      return(null);
   } // end of getStageTypeClassName()
   
   
   public String getStageTypeName()
   {
      return(null);
   } // end of getStageTypeName()
   
   
   public DSStageTypeEnum getType()
   {
      return(_Type);
   } // end of getType()
   
   
   public boolean isContainer()
   {
      return(true);
   } // end of isContainer()
   

   public boolean isContainerStage()
   {
      return(false);
   } // end of isContainerStage()
   

   public boolean isCustomStage()
   {
      return(false);
   } // end of isCustomStage()

   
   public boolean isTransformerStage()
   {
      return(false);
   } // end of isTransformerStage()
   
   
   public void setInputPins(String inputPins)
   {
   } // end of setInputPins()
   

   public void setName(String newName)
   {
      _Container.setName(newName);
   } // end of setName()
   
   
   public void setOutputPins(String outputPins)
   {
   } // end of setOutputPins()
   
   
   public void setStageTypeClassName(String stageTypeClassName)
   {
   } // end of setStageTypeClassName()
   
   
   public void setType(DSStageTypeEnum type)
   {
      _Type = type;
   } // end of setType()
   
   
   public String toString()
   {
      StringBuffer tmpBuf;
      
      tmpBuf = new StringBuffer();
      
      tmpBuf.append(_Container.getName());
      tmpBuf.append(" - type = " + _Type);
      tmpBuf.append(" - parent = " + getParent());
      
      return(tmpBuf.toString());
   } // end of toString()
   
} // end of class ContainerData
