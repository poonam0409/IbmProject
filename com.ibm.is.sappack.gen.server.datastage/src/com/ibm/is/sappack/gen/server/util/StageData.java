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
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.EList;

import ASCLModel.JobComponent;
import ASCLModel.MainObject;
import DataStageX.DSLocalContainerDef;
import DataStageX.DSStage;

import com.ibm.is.sappack.gen.server.common.DSStageTypeEnum;
import com.ibm.is.sappack.gen.server.datastage.DataStageObjectFactory;


public final class StageData implements JobObject
{
   public static class MetaBagData
   {
      // ---------------------------------------------------------------
      //                      Constants
      // ---------------------------------------------------------------
      
      // ---------------------------------------------------------------
      //                      Member Variables
      // ---------------------------------------------------------------
      private String _Condition;
      private String _Name;
      private String _Owner;
      private String _Value;

      
      public MetaBagData(String owner, String name, String value)
      {
         this(owner, name, value, null);
      } // end of MetaBagData()
      
      public MetaBagData(String owner, String name, String value, String condition)
      {
         _Condition = condition;
         _Name      = name;
         _Owner     = owner;
         _Value     = value;
      } // end of MetaBagData()
      
      public String getCondition()
      {
         return(_Condition);
      }
      
      public String getName()
      {
         return(_Name);
      }
      
      public String getOwner()
      {
         return(_Owner);
      }
      
      public String getValue()
      {
         return(_Value);
      }
      
      public String toString()
      {
         return("Owner = " + _Owner + " - Name = " + _Name + " - Value = " + _Value + " - Condition = " + _Condition);
      }
   } // end of class MetaBagData
   
   // -------------------------------------------------------------------------------------
   //                                  Constants
   // -------------------------------------------------------------------------------------
   
   // -------------------------------------------------------------------------------------
   //                                  Member Variables
   // -------------------------------------------------------------------------------------
   private DSStage                     _Stage;
   private DSStageTypeEnum             _Type;
   private ObjectParamMap              _ParamMap;
   private List<StageData.MetaBagData> _MetaBagList;
   
   
   static String copyright()
   { 
      return com.ibm.is.sappack.gen.server.util.Copyright.IBM_COPYRIGHT_SHORT; 
   }

   
   public StageData(DSStage parStage)
   {
      this(parStage, (Map) null, DataStageObjectFactory.STAGE_TYPE_DEFAULT);
   } // end of StageData()
   
   
   public StageData(DSStage parStage, ObjectParamMap parLinkParamMap, 
                    int parDefaultUsageType)   
   {
      init(parStage, parLinkParamMap, parDefaultUsageType);
   } // end of StageData()

   
   public StageData(DSStage parStage, Map parLinkParamMap, int parUsageType)
   {
      ObjectParamMap paramMap = new ObjectParamMap(parLinkParamMap, parUsageType);
      
      init(parStage, paramMap, parUsageType);
   } // end of StageData()


   public ContainerData getContainerData()
   {
      ContainerData retContainer;
      
      if (isContainerStage())
      {
         retContainer = new ContainerData((DSLocalContainerDef) _Stage.getHas_ContainerDef());
      }
      else
      {
         retContainer = null;
      }
      
      return(retContainer);
   } // end of getContainerData()
   
   
   public EList getContains_FlowVariable()
   {
      return(_Stage.getContains_FlowVariable());
   } // end of getContains_FlowVariable()
   
   
   public EList getHas_InputPin()
   {
      return(_Stage.getHas_InputPin());
   } // end of getHas_InputPin()
   
   
   public EList getHas_OutputPin()
   {
      return(_Stage.getHas_OutputPin());
   } // end of getHas_OutputPin()
   
   
   public String getInputPins()
   {
      return(_Stage.getInputPins());
   } // end of getInputPins()
   
   
   public String getInternalID()
   {
      return(_Stage.getInternalID());
   } // end of getInternalID()

   
   public JobComponent getJobComponent()
   {
      return(_Stage);
   }
   
   
   public ObjectParamMap getLinkParams()
   {
      return(_ParamMap);
   } // end of getLinkParams()

   
   public List<StageData.MetaBagData> getMetaBagList()
   {
      return(_MetaBagList);
   } // end of getMetaBagList()
   
   
   public String getName()
   {
      return(_Stage.getName());
   } // end of getName()
   
   
   public String getOutputPins()
   {
      return(_Stage.getOutputPins());
   } // end of getOutputPins()
   
   
   public MainObject getParent()
   {
      MainObject retParent;
      
      // is child of a DSJobDef ?
      if (_Stage.getOf_JobDef() == null)
      {
         // or is child of a DSLocalContainerDef ?
         if (_Stage.getOf_ContainerDef() == null)
         {
            retParent = null;
         }
         else
         {
            retParent = _Stage.getOf_ContainerDef();
         } // end of (else) if (_Stage.getOf_ContainerDef() == null)
      }
      else
      {
         retParent = _Stage.getOf_JobDef();
      } // end of (else) if (_Stage.getOf_JobDef() == null)
      
      return(retParent);
   } // end of getParent()

   
   public String getStageTypeClassName()
   {
      return(_Stage.getStageTypeClassName());
   } // end of getStageTypeClassName()
   
   
   public String getStageTypeName()
   {
      return(_Stage.getStageType());
   } // end of getStageTypeName()
   
   
   public DSStageTypeEnum getType()
   {
      return(_Type);
   } // end of getType()
   
   
   private void init(DSStage parStage, ObjectParamMap parLinkParamMap, 
                     int parDefaultUsageType)   
   {
      if (parStage == null)
      {
         throw new IllegalArgumentException("DSStage must not be null.");
      }

      _Stage = parStage;
      
      if (parLinkParamMap == null)
      {
         _ParamMap = new ObjectParamMap(null, parDefaultUsageType);
      }
      else
      {
         _ParamMap = parLinkParamMap;
      }
      
      _MetaBagList = new ArrayList<StageData.MetaBagData>();
      
      // for non container stages ...
      if (_Stage.getHas_ContainerDef() == null)
      {
         // --> set DSStage Type enumeration
         _Type = DSStageTypeEnum.get(_Stage.getStageType());
      }
      else
      {
         _Type = DSStageTypeEnum.CONTAINER_STAGE_LITERAL;
      }
   } // end of init()

 
   public boolean isDBStage()
   {
      boolean isDataBaseStage;
      
      switch(_Type.getValue())
      {
         case DSStageTypeEnum.ODBC_CONNECTOR_PX_STAGE:
         case DSStageTypeEnum.PX_DB2_STAGE:
         case DSStageTypeEnum.PX_ORACLE_STAGE:
         case DSStageTypeEnum.PX_SYBASE_STAGE:
              isDataBaseStage = true;
              break;
         default:
              isDataBaseStage = false;
      }
      
      return(isDataBaseStage);
   } // end of isDBStage()
   
   
   public boolean isContainer()
   {
      return(false);
   } // end of isContainer()
   

   public boolean isContainerStage()
   {
      return(_Type == DSStageTypeEnum.CONTAINER_STAGE_LITERAL);
   } // end of isContainerStage()
   

   public boolean isCustomStage()
   {
      return(_Stage.getStageTypeClassName().equals(DataStageObjectFactory.STAGE_TYPE_CLASS_NAME_CUSTOM_STAGE));
   } // end of isCustomStage()

   
   public boolean isLookupStage()
   {
      return(_Type == DSStageTypeEnum.PX_LOOKUP_LITERAL);
   } // end of isLookupStage()
   
   
   public boolean isTransformerStage()
   {
      return(_Type == DSStageTypeEnum.CTRANSFORMER_STAGE_LITERAL);
   } // end of isTransformerStage()
   
   
   public void setInputPins(String inputPins)
   {
      _Stage.setInputPins(inputPins);
   } // end of setInputPins()
   
   
   public void setNLSMapName(String nlsMapName)
   {
      _Stage.setNLSMapName(nlsMapName);
   } // end of setNLSMapName()
   
   
   public void setName(String newName)
   {
      _Stage.setName(newName);
   } // end of setName()
   
   
   public void setOutputPins(String outputPins)
   {
      _Stage.setOutputPins(outputPins);
   } // end of setOutputPins()
   
   
   public void setShortDescription(String description)
   {
      _Stage.setShortDescription(description);
   } // end of setShortDescription()
   
   
   public void setStageTypeClassName(String stageTypeClassName)
   {
      if (_Stage != null)
      {
         _Stage.setStageTypeClassName(stageTypeClassName);
      }
   } // end of setStageTypeClassName()
   
   
   public void setType(DSStageTypeEnum type)
   {
      _Type = type;
   } // end of setType()
   
   
   public String toString()
   {
      StringBuffer tmpBuf;
      
      tmpBuf = new StringBuffer();
      
      tmpBuf.append(_Stage.getName());
      tmpBuf.append(" - type = " + _Type);
      tmpBuf.append(" - obj pars = " + _ParamMap.size());
      tmpBuf.append(" - meta bag list = " + _MetaBagList.size());
      tmpBuf.append(" - parent = " + getParent());
      
      return(tmpBuf.toString());
   } // end of toString()
   
} // end of class StageData
