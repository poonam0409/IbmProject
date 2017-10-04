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


import java.util.List;

import org.eclipse.emf.common.util.EList;

import com.ibm.is.sappack.gen.server.common.DSStageTypeEnum;

import ASCLModel.JobComponent;
import ASCLModel.MainObject;


public interface JobObject
{
   // -------------------------------------------------------------------------------------
   //                                  Constants
   // -------------------------------------------------------------------------------------
   public String COPYRIGHT = com.ibm.is.sappack.gen.server.util.Copyright.IBM_COPYRIGHT_SHORT; 


   // -------------------------------------------------------------------------------------
   //                                  Interface Methods
   // -------------------------------------------------------------------------------------
   public EList           getContains_FlowVariable();
   
   public EList           getHas_OutputPin();
   
   public EList           getHas_InputPin();
   
   public String          getInputPins();
   
   public String          getInternalID();
   
   public JobComponent    getJobComponent();
   
   public ObjectParamMap  getLinkParams();

   public List            getMetaBagList();
   
   public String          getName();
   
   public String          getOutputPins();
   
   public MainObject      getParent();
   
   public String          getStageTypeClassName();
   
   public String          getStageTypeName();
   
   public DSStageTypeEnum getType();
   
   public boolean         isContainer();
   
   public boolean         isContainerStage();

   public boolean         isCustomStage();
   
   public boolean         isTransformerStage();
   
   public void            setInputPins(String inputPins);
   
   public void            setName(String newName);
   
   public void            setOutputPins(String outputPins);
   
   public void            setStageTypeClassName(String stageTypeClassName);
   
   public void            setType(DSStageTypeEnum type);
   
} // end of interface JobObject
