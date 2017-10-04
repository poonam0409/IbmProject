//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2011, 2014                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.is.sappack.gen.server.jobgenerator.layout
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.server.jobgenerator.layout;


import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ASCLModel.JobComponent;
import DataStageX.DSStage;
import DataStageX.DSStageType;

import com.ibm.is.sappack.gen.common.JobGeneratorException;
import com.ibm.is.sappack.gen.common.trace.TraceLogger;
import com.ibm.is.sappack.gen.server.datastage.DataStageObjectFactory;
import com.ibm.is.sappack.gen.server.jobgenerator.stages.SAPIDocExtractStageProperties;
import com.ibm.is.sappack.gen.server.jobgenerator.stages.StageProperties;
import com.ibm.is.sappack.gen.server.util.GraphJobLink;
import com.ibm.is.sappack.gen.server.util.GraphJobNode;
import com.ibm.is.sappack.gen.server.util.GraphJobObjPosition;


public class IDocLoadJobLayout extends BaseLayout 
{
	// -------------------------------------------------------------------------------------
	//                                       Constants
	// -------------------------------------------------------------------------------------

   
	// -------------------------------------------------------------------------------------
	// Member Variables
	// -------------------------------------------------------------------------------------
   private boolean     _DoCreateIDOCLoadStatusInfo;
   
	
	static String copyright() {
		return com.ibm.is.sappack.gen.server.jobgenerator.layout.Copyright.IBM_COPYRIGHT_SHORT;
	}
	

	public IDocLoadJobLayout(boolean doCreateILSInfo) {
	   super();
	   
	   _DoCreateIDOCLoadStatusInfo = doCreateILSInfo;;
	   
	} // end of IDocLoadJobLayout()

	
	
	/**
	 * This method computes the DSDesignView data for the passed Graph Job Node.
	 * 
	 * @param curJobNode
	 *           JobNode instance
	 * @param curXPos
	 *           x-position to be set to the stage
	 * @param curYPos
	 *           y-position to be set to the stage
	 * @param stagePosMap
	 *           stage positions map
	 * @param doAppendValueSeparator
	 *           if true append a values separator before processing the JobNode
	 * 
	 * @return new 'doAppendValueSeparator' value to be set
	 */
	protected boolean computeDesignViewDataForGraphNode(GraphJobNode curJobNode, int curXPos, int curYPos, 
	                                                    Map<JobComponent, GraphJobObjPosition> stagePosMap, boolean doAppendValueSeparator) 
	          throws JobGeneratorException {
	   
      GraphJobLink          nextJobLink;
      GraphJobNode          nextJobNode;
      GraphJobObjPosition   curStagePos;
      DSStage               srcStage;
      DSStage               nextStage;
      DSStageType           curStageType;
      String                workBuf;
      List                  outLinkList;
      Iterator              outLinkListIter;
      int                   minYPos;
      int                   incomingLinkCount;
      int                   outgoingLinkCount;
      int                   curLinkNo;

      if (TraceLogger.isTraceEnabled()) {
         TraceLogger.entry();
      }

      
      if (doAppendValueSeparator) {
         // append the separator to each single buffer
         appendSeparators();
         
         doAppendValueSeparator = false;
      } // end of if (doAppendValueSeparator)

      minYPos           = curYPos;
      srcStage          = curJobNode.getDSStage();
      outLinkList       = curJobNode.getOutLinks();
      outgoingLinkCount = outLinkList.size();

      if (TraceLogger.isTraceEnabled()) {
         TraceLogger.trace(TraceLogger.LEVEL_FINEST, "src stage = " + srcStage.getName() +
         	                                         " - internalId = " + srcStage.getInternalID() +
                                                     " - out links = " + outgoingLinkCount);
         TraceLogger.trace(TraceLogger.LEVEL_FINEST, "StageListBuf = " + _StageListBuf);
      }
//System.out.println("hansx STage       : " + srcStage.getName() + " - internalId = " + srcStage.getInternalID() + " - out links = " + outgoingLinkCount);
//System.out.println("hansx StageListBuf: " + _StageListBuf);

      // do the 'internal Id' already exists in the stage list buf ???
      if (_StageListBuf.indexOf(srcStage.getInternalID() + DataStageObjectFactory.DS_VIEW_VALUE_SEPARATOR) < 0) {
    	 if (curJobNode.getPositionOnCanvas() != null) {
    		 curXPos = curJobNode.getPositionOnCanvas().getXPositionOnCanvas();
    		 curYPos = curJobNode.getPositionOnCanvas().getYPositionOnCanvas() + 2 * GRAPH_OBJECT_DEFAULT_DISTANCE_Y; //add GRAPH_OBJECT_DEFAULT_DISTANCE_Y to get space for the annotation
    	 }
         // no ==> update DesignView information (source stage)
         _StageListBuf.append(srcStage.getInternalID());
         curStageType = (DSStageType) srcStage.getOf_StageType();
         _StageTypesBuf.append(curStageType.getStageOLEType());
         _StageXPosBuf.append(curXPos);

         // move Y position down if there is more than 1 outgoing link
         if (outgoingLinkCount > 1) {
            // curYPos = curYPos + (((outgoingLinkCount-1) * GRAPH_OBJECT_DEFAULT_DISTANCE_Y) / 2);
         }

         _StageYPosBuf.append(curYPos);
         _StageXSizeBuf.append(GRAPH_OBJECT_STAGE_SIZE_X);
         _StageYSizeBuf.append(GRAPH_OBJECT_STAGE_SIZE_Y);

         // save the current stage position in the position map
         stagePosMap.put(srcStage, new GraphJobObjPosition(curXPos, curYPos));
// System.out.println("hansx: srcStage = " + srcStage.getName() + " - pos =" + curXPos + "/" + curYPos);         

         // create 'LazyLoadInfo' for the source stage
         workBuf = createLazyLoadInfoForSourceStage(srcStage.getInternalID(), srcStage.getName(), 
                                                    srcStage.getStageType());

         // the source stage has at least one link ...
         if (outgoingLinkCount > 0) {
            // ==> remove the trailing blank because we have to append some values below
            workBuf = removeLastChar(workBuf);
         }
         _LazyLoadInfoBuf.append(workBuf);

         doAppendValueSeparator = true;
      } // end of if (_StageListBuf.indexOf(srcStage.getInternalID()) < 0)

      // if there is at least one link ...
      if (outgoingLinkCount > 0) {
         // ==> determine the positions of all 'next' stages
         curLinkNo       = 0;
         curXPos         = curXPos + GRAPH_OBJECT_DEFAULT_DISTANCE_X;
         curYPos         = minYPos;
         outLinkListIter = outLinkList.iterator();
// System.out.println("links cnt: " + outLinkList.size());
         while (outLinkListIter.hasNext()) {
            // get 'next' JobNode
            curLinkNo++;
            nextJobLink = (GraphJobLink) outLinkListIter.next();
            nextJobNode = nextJobLink.getTargetGraphNode();
            nextStage   = nextJobNode.getDSStage();
// System.out.println("next STage: " + nextStage.getName());

            // get the number of 'input links' of the target stage
            incomingLinkCount = nextStage.getHas_InputPin().size();

            if (TraceLogger.isTraceEnabled()) {
               TraceLogger.trace(TraceLogger.LEVEL_FINEST, "link number = " + curLinkNo);
               TraceLogger.trace(TraceLogger.LEVEL_FINEST, "next stage = " + nextStage.getName() + 
                                                           " -  in links = " + incomingLinkCount);
            }

            // special case : createLoadStatusInfo = true AND
            // current stage a transformer AND
            // that links to a custom (IDOC) stage
            if (_DoCreateIDOCLoadStatusInfo                                                                && 
                srcStage.getStageTypeClassName().equals(StageProperties.STAGE_TYPE_CLASS_NAME_TRANSFORMER) &&
                nextStage.getStageTypeClassName().equals(SAPIDocExtractStageProperties.STAGE_TYPE_CLASS_NAME)) {
               
               // ==> ignore that incoming link for positioning
               incomingLinkCount--;

               if (TraceLogger.isTraceEnabled()) {
                  TraceLogger.trace(TraceLogger.LEVEL_FINEST, "new: in links = " + incomingLinkCount);
               }
            } // end of if (_JobRequestInfo.doCreateLoadStatusInfo() && ... STAGE_TYPE_CLASS_NAME))
// System.out.println("next Link: " + nextJobLink.getDSOutputLink().getName());
// System.out.println("next stage incoming links cnt: " + incomingLinkCount);

            // if there is more than one input link ...
            if (incomingLinkCount > 1) {
               if (TraceLogger.isTraceEnabled()) {
                  TraceLogger.trace(TraceLogger.LEVEL_FINEST, 
                                    "next stage exists in StagePosMap: " + !stagePosMap.containsKey(nextStage));
               }

               // check if target stage position has already been saved
               if (!stagePosMap.containsKey(nextStage)) {
                  // ==> not yes in the position map
                  // --> re-calculate the Y coordinate (dependent on the number of input links)
                  curYPos = curYPos + (((incomingLinkCount - 1) * GRAPH_OBJECT_DEFAULT_DISTANCE_Y)) / 2;

                  stagePosMap.put(nextStage, new GraphJobObjPosition(curXPos, curYPos));
               } // end of if (!stagePosMap.containsKey(nextStage))
            }
            else {
               // special case : createLoadStatusInfo = true AND
               // it's a transformer AND
               // the transformer is the StatusInfo transformer
               if (_DoCreateIDOCLoadStatusInfo                                                                 && 
                   nextStage.getStageTypeClassName().equals(StageProperties.STAGE_TYPE_CLASS_NAME_TRANSFORMER) &&
                   srcStage.getStageTypeClassName().equals(StageProperties.STAGE_TYPE_CLASS_NAME_TRANSFORMER)) {
                  
                  int newXPos = curXPos + (GRAPH_OBJECT_DEFAULT_DISTANCE_X / 2);
                  int newYPos = curYPos - (int) (GRAPH_OBJECT_DEFAULT_DISTANCE_Y * 0.7);
                  // int newYPos = curYPos - (GRAPH_OBJECT_DEFAULT_DISTANCE_Y / 2);

                  // save the next stage's position in the position map
                  stagePosMap.put(nextStage, new GraphJobObjPosition(newXPos, newYPos));
// System.out.println("Position of StatusInfo transformer = (" + newXPos + "/" + newYPos + ")");

                  if (TraceLogger.isTraceEnabled()) {
                     TraceLogger.trace(TraceLogger.LEVEL_FINEST, 
                                       "Position of StatusInfo transformer = (" + newXPos + "/" + newYPos + ")");
                  }
               }
               else {
                  // save the next stage's position in the position map
                  stagePosMap.put(nextStage, new GraphJobObjPosition(curXPos, curYPos));

                  // update DesignView information (target stage Y position)
                  curYPos = curYPos + GRAPH_OBJECT_DEFAULT_DISTANCE_Y;
               } // end of (else) if (_JobRequestInfo.doCreateLoadStatusInfo() && ... CLASS_NAME_TRANSFORMER))
            } // end of (else) if (inLinkCount > 1)
         } // end of while(outLinkListIter.hasNext())

         // ==> create 'LazyLoadInfo' for the link and the 'next' stage
         workBuf = createLazyLoadInfoPart(srcStage, outLinkList, stagePosMap);
         _LazyLoadInfoBuf.append(workBuf);

         // now process the 'next' stages
         outLinkListIter = outLinkList.iterator();
         while (outLinkListIter.hasNext()) {
            // get 'next' JobNode
            nextJobLink = (GraphJobLink) outLinkListIter.next();
            nextJobNode = nextJobLink.getTargetGraphNode();
            nextStage   = nextJobNode.getDSStage();

            // process the next JobNode
            curStagePos = (GraphJobObjPosition) stagePosMap.get(nextStage);
            // System.out.println("Retrieved Position of StatusInfo transformer = (" + curStagePos.getXPos() + "/" +
            // curStagePos.getYPos() + ")");
            doAppendValueSeparator = computeDesignViewDataForGraphNode(nextJobNode, curStagePos.getXPos(), 
                                                                       curStagePos.getYPos(),
                                                                       stagePosMap, doAppendValueSeparator);
         } // end of while(outLinkListIter.hasNext())
      } // end of if (outLinkCount > 0)

      if (TraceLogger.isTraceEnabled()) {
         TraceLogger.exit("Append value separator = " + doAppendValueSeparator);
      }
      
// System.out.println("Append value separator = " + doAppendValueSeparator);

      return (doAppendValueSeparator);
	} // end of computeDesignViewDataForGraphNode()

} // end of class IDocLoadJobLayout
