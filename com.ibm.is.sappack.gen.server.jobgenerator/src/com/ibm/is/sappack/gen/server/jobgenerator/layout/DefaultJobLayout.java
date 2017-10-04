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
import com.ibm.is.sappack.gen.server.jobgenerator.stages.StageProperties;
import com.ibm.is.sappack.gen.server.util.GraphJobLink;
import com.ibm.is.sappack.gen.server.util.GraphJobNode;
import com.ibm.is.sappack.gen.server.util.GraphJobObjPosition;


public class DefaultJobLayout extends BaseLayout 
{
   
	// -------------------------------------------------------------------------------------
	//                                       Constants
	// -------------------------------------------------------------------------------------
   private   static final String CONTAINER_TYPE_ID               = "ID_PALETTECONTAINER";
	
   
	// -------------------------------------------------------------------------------------
	// Member Variables
	// -------------------------------------------------------------------------------------

	
	static String copyright() {
		return com.ibm.is.sappack.gen.server.jobgenerator.layout.Copyright.IBM_COPYRIGHT_SHORT;
	}
	

	public DefaultJobLayout() {

	   super();
	   
		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.entry();
		}
		
		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.exit();
		}
	} // end of DefaultJobLayout()
	
	
	/**
	 * @see BaseLayout#computeDesignViewDataForGraphNode(GraphJobNode, int, int, Map, boolean)
	 */
	protected boolean computeDesignViewDataForGraphNode(GraphJobNode curJobNode, int curXPos, int curYPos, 
	                                                    Map<JobComponent, GraphJobObjPosition> stagePosMap, boolean doAppendValueSeparator) 
	          throws JobGeneratorException {
	   
	   GraphJobLink        nextGraphJobLink;
	   GraphJobNode        nextGraphJobNode;
	   GraphJobObjPosition curGraphStagePos;
		DSStage             srcStage;
		DSStage             nextStage;
		DSStageType         curStageType;
		String              llWorkBuf;
		List                outLinkList;
		Iterator            outLinkListIter;
		int                 minYPos;
		int                 incomingLinkCount;
		int                 outgoingLinkCount;
		int                 curLinkNo;

		if (TraceLogger.isTraceEnabled()) {
         TraceLogger.entry("JobNode = " + curJobNode);
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
			TraceLogger.trace(TraceLogger.LEVEL_FINEST, "src stage = " + srcStage.getName() + " -  out links = "
			      + outgoingLinkCount);
		}
// System.out.println("hansx STage: " + srcStage.getName());

		// do the 'internal Id' already exists in the stage list buf ???
		if (_StageListBuf.indexOf(srcStage.getInternalID()) < 0) {
		   String stageTypeName;
		   
			// no ==> update DesignView information (source stage)
		   _StageListBuf.append(srcStage.getInternalID());
			
			// determine stage type name
			curStageType = (DSStageType) srcStage.getOf_StageType();
			
			if (curStageType == null) {
			   // --> derived from stage type class name ...
			   if (srcStage.getStageTypeClassName().equals(StageProperties.STAGE_TYPE_CLASS_NAME_CONTAINER)) {
	            stageTypeName = CONTAINER_TYPE_ID;
			   }
			   else {
			      if (TraceLogger.isTraceEnabled()) {
			         TraceLogger.trace(TraceLogger.LEVEL_FINE, "Unknown stage type class name for stage '" + srcStage.getName() + "'.");
			      }

			      // unknown stage type class name
			      throw new JobGeneratorException("126800E", new String[] { srcStage.getName() });
			   }
			}
			else {
            // --> direct
            stageTypeName = curStageType.getStageOLEType();
			}
			
			_StageTypesBuf.append(stageTypeName);
			_StageXPosBuf.append(curXPos);

			// move Y position down if there is more than 1 outgoing link
			if (outgoingLinkCount > 1) {
				curYPos = curYPos + (((outgoingLinkCount - 1) * GRAPH_OBJECT_DEFAULT_DISTANCE_Y) / 2);
			}

			_StageYPosBuf.append(curYPos);
			_StageXSizeBuf.append(GRAPH_OBJECT_STAGE_SIZE_X);
			_StageYSizeBuf.append(GRAPH_OBJECT_STAGE_SIZE_Y);

			// save the current stage position in the position map
			stagePosMap.put(srcStage, new GraphJobObjPosition(curXPos, curYPos));

			// create 'LazyLoadInfo' for the source stage
			llWorkBuf = createLazyLoadInfoForSourceStage(srcStage.getInternalID(), srcStage.getName(), 
			                                           srcStage.getStageType());

			// the source stage has at least one link ...
			if (outgoingLinkCount > 0) {
				// ==> remove the trailing blank because we have to append some values below
				llWorkBuf = removeLastChar(llWorkBuf);
			}
			_LazyLoadInfoBuf.append(llWorkBuf);

			doAppendValueSeparator = true;
		} // end of (else) if (_StageListBuf.indexOf(srcStage.getInternalID()) < 0)

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
				nextGraphJobLink = (GraphJobLink) outLinkListIter.next();
				nextGraphJobNode = nextGraphJobLink.getTargetGraphNode();
				nextStage        = nextGraphJobNode.getDSStage();

				// get the number of 'input links' of the target stage
				incomingLinkCount = nextStage.getHas_InputPin().size();

				if (TraceLogger.isTraceEnabled()) {
					TraceLogger.trace(TraceLogger.LEVEL_FINEST, "link number = " + curLinkNo);
					TraceLogger.trace(TraceLogger.LEVEL_FINEST, "next stage = " + nextStage.getName() + 
					                                            " -  in links = " + incomingLinkCount);
				}
/*
System.out.println("next Link:    " + nextGraphJobLink);
System.out.println("next DSSTage: " + nextStage.getName());
System.out.println("next stage incoming links cnt: " + incomingLinkCount);
*/

				// if there is more than one input link ...
				if (incomingLinkCount > 1) {
					if (TraceLogger.isTraceEnabled()) {
						TraceLogger.trace(TraceLogger.LEVEL_FINEST, "next stage exists in StagePosMap: "
						                                          + stagePosMap.containsKey(nextStage));
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
					// save the next stage's position in the position map
					stagePosMap.put(nextStage, new GraphJobObjPosition(curXPos, curYPos));

					// update DesignView information (target stage Y position)
					curYPos = curYPos + GRAPH_OBJECT_DEFAULT_DISTANCE_Y;
				} // end of (else) if (inLinkCount > 1)
			} // end of while(outLinkListIter.hasNext())

			// ==> create 'LazyLoadInfo' for the link and the 'next' stage
			llWorkBuf = createLazyLoadInfoPart(srcStage, outLinkList, stagePosMap);
			_LazyLoadInfoBuf.append(llWorkBuf);

			// now process the 'next' stages
			outLinkListIter = outLinkList.iterator();
			while (outLinkListIter.hasNext()) {
			   
            boolean  continueNodeChainProcessing;
            
				// get 'next' JobNode
				nextGraphJobLink = (GraphJobLink) outLinkListIter.next();
				nextGraphJobNode = nextGraphJobLink.getTargetGraphNode();
				nextStage        = nextGraphJobNode.getDSStage();

            // IGNORE the link if it leads to an 'existing' Lookup stage
            continueNodeChainProcessing = !isLookupStageGraphNodeAndDoesExists(nextGraphJobNode);
            
            if (continueNodeChainProcessing) {
               // process the next JobNode
               curGraphStagePos       = (GraphJobObjPosition) stagePosMap.get(nextStage);
               doAppendValueSeparator = computeDesignViewDataForGraphNode(nextGraphJobNode, 
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
	
} // end of class DefaultJobLayout
