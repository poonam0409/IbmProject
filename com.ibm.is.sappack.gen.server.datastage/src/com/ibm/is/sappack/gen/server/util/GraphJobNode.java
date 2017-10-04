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
// Module Name : com.ibm.is.sappack.gen.server.util
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.server.util;


import java.util.ArrayList;
import java.util.List;

import ASCLModel.JobComponent;
import DataStageX.DSLocalContainerDef;
import DataStageX.DSStage;


public final class GraphJobNode 
{
   public class PositionOnCanvas {
	   private int xPosOnCanvas, yPosOnCanvas;
	   
	   public PositionOnCanvas(int xPosOnCanvas, int yPosOnCanvas) {
		   this.setPositionOnCanvas(xPosOnCanvas, yPosOnCanvas);
	   }
	   
	   public void setPositionOnCanvas(int xPosOnCanvas, int yPosOnCanvas) {
		   this.xPosOnCanvas = xPosOnCanvas;
		   this.yPosOnCanvas = yPosOnCanvas;
	   }
	      
	   public int getXPositionOnCanvas() {
		   return this.xPosOnCanvas;
	   }

	   public int getYPositionOnCanvas() {
		   return this.yPosOnCanvas;
	   }
   }

   // -------------------------------------------------------------------------------------
   //                                 Member Variables
   // -------------------------------------------------------------------------------------
   private JobObject   _JobObject;
   private List        _OutLinkList;
   private PositionOnCanvas positionOnCanvas;
   
   
   static String copyright()
   { 
      return com.ibm.is.sappack.gen.server.util.Copyright.IBM_COPYRIGHT_SHORT; 
   }
   
   
   public GraphJobNode(JobObject jobObject)
   {
      _JobObject   = jobObject;
      _OutLinkList = new ArrayList();
   }
   
   public GraphJobNode(JobObject jobObject, int xPosOnCanvas, int yPosOnCanvas) {
	   this(jobObject);
	   this.positionOnCanvas = new PositionOnCanvas(xPosOnCanvas, yPosOnCanvas);
   }
   
   public PositionOnCanvas getPositionOnCanvas()
   {
	   return this.positionOnCanvas;
   }
   
   public GraphJobNode(JobObject jobObject, GraphJobLink outLink)
   {
      this(jobObject);
      addOutLink(outLink);
   }

   
   public GraphJobLink addOutLink(GraphJobLink newOutLink)
   {
      if (newOutLink != null)
      {
         _OutLinkList.add(newOutLink);
      }
      
      return(newOutLink);
   } // end of addOutLink()
   
   
   public DSLocalContainerDef getContainer()
   {
      DSLocalContainerDef container;
      
      if (_JobObject.isContainer())
      {
         container = (DSLocalContainerDef) _JobObject.getJobComponent();
      }
      else
      {
         container = null;
      }
      
      return(container);
   } // end of getContainer()
   

   public JobComponent getJobComponent()
   {
      return(_JobObject.getJobComponent());
   } // end of getJobComponent()

   
   public List getOutLinks()
   {
      return(_OutLinkList);
   }

   
   public DSStage getDSStage()
   {
      DSStage stage;
      
      if (!_JobObject.isContainer())
      {
         stage = (DSStage) _JobObject.getJobComponent();
      }
      else
      {
         stage = null;
      }
      
      return(stage);
   } // end of getStage()

   
   public boolean isContainer()
   {
      return(_JobObject.isContainer());
   }

   
   public boolean isStage()
   {
      return(!_JobObject.isContainer());
   }
   

   public String toString()
   {
      StringBuffer tmpBuf = new StringBuffer();
      
      tmpBuf.append("Name = ");
      tmpBuf.append(_JobObject.getName());
      tmpBuf.append(" - type = ");
      tmpBuf.append(_JobObject.getStageTypeName());
      if (_JobObject.isContainer())
         tmpBuf.append(" - isContainer");
      if (_JobObject.isContainerStage())
         tmpBuf.append(" - isContainerStage");
      if (_JobObject.isCustomStage())
         tmpBuf.append(" - isCustomStage");
      if (_JobObject.isTransformerStage())
         tmpBuf.append(" - isTransformerStage");
      tmpBuf.append(" - outlinks = ");
      tmpBuf.append(_OutLinkList.size());
      
      return(tmpBuf.toString());
   }
   
} // end of class GraphJobNode
