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
// Module Name : com.ibm.is.sappack.gen.server.jobgenerator.layout
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.server.jobgenerator.layout;


import java.text.MessageFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ASCLModel.JobComponent;
import DataStageX.DSLocalContainerDef;
import DataStageX.DSStage;
import DataStageX.DSStageType;

import com.ibm.is.sappack.gen.common.JobGeneratorException;
import com.ibm.is.sappack.gen.common.trace.TraceLogger;
import com.ibm.is.sappack.gen.server.util.GraphJobLink;
import com.ibm.is.sappack.gen.server.util.GraphJobNode;
import com.ibm.is.sappack.gen.server.util.GraphJobObjPosition;


public class DefaultContainerLayout extends BaseLayout 
{
   
	// -------------------------------------------------------------------------------------
	//                                       Constants
	// -------------------------------------------------------------------------------------
   protected static final String CONTAINER_INPUT_ID       = "ID_PALETTEINPUT";
   protected static final String CONTAINER_OUTPUT_ID      = "ID_PALETTEOUTPUT";
   protected static final String CONTAINER_LL_INPUT_ID    = "{0}.IN";
   protected static final String CONTAINER_LL_OUTPUT_ID   = "{0}.OUT";
	
   
	// -------------------------------------------------------------------------------------
	// Member Variables
	// -------------------------------------------------------------------------------------

	
	static String copyright() {
		return com.ibm.is.sappack.gen.server.jobgenerator.layout.Copyright.IBM_COPYRIGHT_SHORT;
	}
	

	public DefaultContainerLayout() {

      super();
      
		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.entry();
		}
		
		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.exit();
		}
	} // end of DefaultContainerLayout()

	

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
	                                                    Map stagePosMap, boolean doAppendValueSeparator) 
	          throws JobGeneratorException {
	   
	   GraphJobLink        nextJobLink;
	   GraphJobNode        nextJobNode;
	   GraphJobObjPosition curGraphStagePos;
      DSLocalContainerDef srcContainer;
      DSLocalContainerDef nextContainer;
		DSStage             srcStage;
		DSStage             nextStage;
		DSStageType         curStageType;
		String              llWorkBuf;
      String              internalId;
		List                outLinkList;
		Iterator            outLinkListIter;
		JobComponent        srcJobComponent;
      JobComponent        nextJobComponent;
		int                 minYPos;
		int                 incomingLinkCount;
		int                 outgoingLinkCount;
		int                 curLinkNo;
		boolean             doForceAppendStage;

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.entry("JobNode = " + curJobNode);
		}

		if (doAppendValueSeparator) {
			// append the separator to each single buffer
		   appendSeparators();
		   
	      doAppendValueSeparator = false;
		} // end of if (doAppendValueSeparator)

		minYPos            = curYPos;
      outLinkList        = curJobNode.getOutLinks();
      outgoingLinkCount  = outLinkList.size();
      doForceAppendStage = false;
      
		if (curJobNode.isStage()) {
		   srcStage        = curJobNode.getDSStage();
		   internalId      = srcStage.getInternalID();
		   srcContainer    = null;
         srcJobComponent = srcStage;
		}
		else {
		   srcStage        = null;
         srcContainer    = curJobNode.getContainer();
         internalId      = srcContainer.getShortDescription();
         srcJobComponent = srcContainer;
         
         if (outgoingLinkCount == 0) {
            // it seems to be the OUTPUT part of the container
            doForceAppendStage = true;
         }
		} // end of (else) if (curJobNode.isStage())

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.trace(TraceLogger.LEVEL_FINEST, "src component id = " + internalId + " - out links = "
			                                          + outgoingLinkCount);
		}
               
		// do the 'internal Id' already exists in the stage list buf ???
		if (_StageListBuf.indexOf(internalId) < 0 ||
		    doForceAppendStage) {
		   
		   String stageTypeName;
	      String lazyLoadId;
	      String lazyLoadType;
	      String lazyLoadName;
	      int    xSize;
         int    ySize;
		   
			// no ==> update DesignView information (source stage)
		   _StageListBuf.append(internalId);
			
			// determine stage type name
		   if (curJobNode.isStage()) {
	         curStageType  = (DSStageType) srcStage.getOf_StageType();
            stageTypeName = curStageType.getStageOLEType();
            lazyLoadId    = internalId; 
            lazyLoadName  = srcStage.getName();
            lazyLoadType  = srcStage.getStageType();
            
            xSize         = GRAPH_OBJECT_STAGE_SIZE_X;
            ySize         = GRAPH_OBJECT_STAGE_SIZE_Y;
		   }
		   else {
		      // correction of the Y position for container entries and exists because of the smaller size
            curYPos = curYPos + (GRAPH_OBJECT_STAGE_SIZE_Y - GRAPH_OBJECT_CONTAINER_SIZE_Y) /2;
            
		      // start container or end container ???
		      if (outgoingLinkCount == 0) {
		         // --> end container
               stageTypeName = CONTAINER_OUTPUT_ID;
               lazyLoadId    = MessageFormat.format(CONTAINER_LL_OUTPUT_ID, new Object[] { internalId } ); 
               lazyLoadName  = CONTAINER_OUTPUT_ID;
               lazyLoadType  = CONTAINER_OUTPUT_ID;
		      }
		      else {
		         // --> start container
		         stageTypeName = CONTAINER_INPUT_ID;
               lazyLoadId    = MessageFormat.format(CONTAINER_LL_INPUT_ID, new Object[] { internalId } ); 
               lazyLoadName  = CONTAINER_INPUT_ID;
               lazyLoadType  = CONTAINER_INPUT_ID;
		      }
		      
		      xSize = GRAPH_OBJECT_CONTAINER_SIZE_X;
            ySize = GRAPH_OBJECT_CONTAINER_SIZE_Y;
		   } // end of (else) if (curJobNode.isStage())
			
			_StageTypesBuf.append(stageTypeName);
			_StageXPosBuf.append(curXPos);

			// move Y position down if there is more than 1 outgoing link
			if (outgoingLinkCount > 1) {
				curYPos = curYPos + (((outgoingLinkCount - 1) * GRAPH_OBJECT_DEFAULT_DISTANCE_LC_Y) / 2);
			}

			_StageYPosBuf.append(curYPos);
			_StageXSizeBuf.append(xSize);
			_StageYSizeBuf.append(ySize);

			// save the current stage position in the position map
			stagePosMap.put(srcJobComponent, new GraphJobObjPosition(curXPos, curYPos));

			// create 'LazyLoadInfo' for the source stage
			llWorkBuf = createLazyLoadInfoForSourceStage(lazyLoadId, lazyLoadName, lazyLoadType);

			// the source stage has at least one link ...
			if (outgoingLinkCount > 0) {
				// ==> remove the trailing blank because we have to append some values below
				llWorkBuf = removeLastChar(llWorkBuf);
			}
			else {
            // it seems to be the OUTPUT part of the container
			   if (curJobNode.isContainer()) {
	            if (TraceLogger.isTraceEnabled()) {
	               TraceLogger.trace(TraceLogger.LEVEL_FINEST, "'Exit Container' detected. End building link chain");
	            }
			   }
			}
			_LazyLoadInfoBuf.append(llWorkBuf);

			doAppendValueSeparator = true;
      } // end of if (_StageListBuf.indexOf(internalId) < 0 || doForceAppendStage) {

		// if there is at least one link ...
		if (outgoingLinkCount > 0) {
			// ==> determine the positions of all 'next' stages
			curLinkNo       = 0;
//			curXPos         = curXPos + GRAPH_OBJECT_DEFAULT_DISTANCE_LC_X;
			curYPos         = minYPos;
			outLinkListIter = outLinkList.iterator();
// System.out.println("hansx(contr): links cnt: " + outLinkList.size());
			while (outLinkListIter.hasNext()) {
				// get 'next' JobNode
				curLinkNo++;
				nextJobLink = (GraphJobLink) outLinkListIter.next();
				nextJobNode = nextJobLink.getTargetGraphNode();
				
				switch(nextJobLink.getDirection())
				{
				   case GraphJobLink.LINK_DIRECTION_DOWN:
				        curYPos = curYPos + GRAPH_OBJECT_DEFAULT_DISTANCE_LC_Y;
				        break;
				   case GraphJobLink.LINK_DIRECTION_LEFT:
				        curXPos = curXPos - GRAPH_OBJECT_DEFAULT_DISTANCE_LC_X;
				        break;
				   case GraphJobLink.LINK_DIRECTION_UP:
				        curYPos = curYPos - GRAPH_OBJECT_DEFAULT_DISTANCE_LC_Y;
				        break;
				   case GraphJobLink.LINK_DIRECTION_RIGHT:
				   default:
				        curXPos = curXPos + GRAPH_OBJECT_DEFAULT_DISTANCE_LC_X;
				} // end of switch(nextJobLink.getDirection())
				
	         if (nextJobNode.isStage()) {
	            nextStage        = nextJobNode.getDSStage();
               nextJobComponent = nextStage;
	            nextContainer    = null;
	            
	            // get the number of 'input links' of the target stage
	            incomingLinkCount = nextStage.getHas_InputPin().size();
	         }
	         else {
	            nextContainer    = nextJobNode.getContainer();
	            nextJobComponent = nextContainer;
	            nextStage        = null;
	            
	            // the 'end stage' can have only one input link 
	            incomingLinkCount = 1;
	         } // end of (else) if (nextJobNode.isStage())
				
				if (TraceLogger.isTraceEnabled()) {
					TraceLogger.trace(TraceLogger.LEVEL_FINEST, "link number = " + curLinkNo);
					TraceLogger.trace(TraceLogger.LEVEL_FINEST, "next stage = " + nextJobComponent.getName() + 
					                                            " -  in links = " + incomingLinkCount);
				}
/*
System.out.println("hansx(contr): next Link:        " + nextJobLink);
System.out.println("hansx(contr): next DSComponent: " + nextJobComponent.getName());
System.out.println("hansx(contr): next stage incoming links cnt: " + incomingLinkCount);
*/

				// if there is more than one input link ...
				if (incomingLinkCount > 1) {
					if (TraceLogger.isTraceEnabled()) {
						TraceLogger.trace(TraceLogger.LEVEL_FINEST, "next stage exists in StagePosMap: "
						                                          + stagePosMap.containsKey(nextJobComponent));
					}

					// check if target stage position has already been saved
					if (!stagePosMap.containsKey(nextJobComponent)) {
						// ==> not yes in the position map
						// --> re-calculate the Y coordinate (dependent on the number of input links)
						curYPos = curYPos + (((incomingLinkCount - 1) * GRAPH_OBJECT_DEFAULT_DISTANCE_LC_Y)) / 2;

						stagePosMap.put(nextJobComponent, new GraphJobObjPosition(curXPos, curYPos));
					} // end of if (!stagePosMap.containsKey(nextStage))
				}
				else {
					// save the next stage's position in the position map
					stagePosMap.put(nextJobComponent, new GraphJobObjPosition(curXPos, curYPos));

					// update DesignView information (target stage Y position)
					curYPos = curYPos + GRAPH_OBJECT_DEFAULT_DISTANCE_LC_Y;
				} // end of (else) if (inLinkCount > 1)
			} // end of while(outLinkListIter.hasNext())

			// ==> create 'LazyLoadInfo' for the link and the 'next' stage
			llWorkBuf = createLazyLoadInfoPart(srcJobComponent, outLinkList, stagePosMap);
			_LazyLoadInfoBuf.append(llWorkBuf);

			// now process the 'next' stages
			outLinkListIter = outLinkList.iterator();
			while (outLinkListIter.hasNext()) {
			   
			   boolean  continueNodeChainProcessing;
			   
				// get 'next' JobNode
				nextJobLink      = (GraphJobLink) outLinkListIter.next();
				nextJobNode      = nextJobLink.getTargetGraphNode();
				nextJobComponent = nextJobNode.getJobComponent();

				// IGNORE the link if it leads to an 'existing' Lookup stage
				continueNodeChainProcessing = !isLookupStageGraphNodeAndDoesExists(nextJobNode);
				
            if (continueNodeChainProcessing) {
               // process the next JobNode
               curGraphStagePos       = (GraphJobObjPosition) stagePosMap.get(nextJobComponent);
               doAppendValueSeparator = computeDesignViewDataForGraphNode(nextJobNode, 
                                                                          curGraphStagePos.getXPos(), 
                                                                          curGraphStagePos.getYPos(),
                                                                          stagePosMap, doAppendValueSeparator);
            }
			} // end of while(outLinkListIter.hasNext())
		} // end of if (outLinkCount > 0)

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.exit("Append value separator = " + doAppendValueSeparator);
		}

		return (doAppendValueSeparator);
	} // end of computeDesignViewDataForGraphNode()
	
} // end of class DefaultContainerLayout
