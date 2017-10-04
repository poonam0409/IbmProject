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


import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ASCLModel.JobComponent;
import ASCLModel.LinkTypeEnum;
import ASCLModel.MainObject;
import DataStageX.DSDesignView;
import DataStageX.DSInputPin;
import DataStageX.DSJobDef;
import DataStageX.DSLink;
import DataStageX.DSLocalContainerDef;
import DataStageX.DSMetaBag;
import DataStageX.DSOutputPin;
import DataStageX.DSStage;

import com.ibm.is.sappack.gen.common.JobGeneratorException;
import com.ibm.is.sappack.gen.common.trace.TraceLogger;
import com.ibm.is.sappack.gen.server.common.DSStageTypeEnum;
import com.ibm.is.sappack.gen.server.common.util.DSAccessException;
import com.ibm.is.sappack.gen.server.datastage.DataStageObjectFactory;
import com.ibm.is.sappack.gen.server.jobgenerator.stages.StageProperties;
import com.ibm.is.sappack.gen.server.util.GraphJobLink;
import com.ibm.is.sappack.gen.server.util.GraphJobNode;
import com.ibm.is.sappack.gen.server.util.GraphJobObjPosition;


public abstract class BaseLayout 
{
	// -------------------------------------------------------------------------------------
	//                                       Constants
	// -------------------------------------------------------------------------------------
   protected static final int    GRAPH_OBJECT_STRETCH_FACTOR        = 16;
   protected static final int    GRAPH_OBJECT_STAGE_SIZE_X          = 48;
   protected static final int    GRAPH_OBJECT_STAGE_SIZE_Y          = 48;
   protected static final int    GRAPH_OBJECT_CONTAINER_SIZE_X      = 36;
   protected static final int    GRAPH_OBJECT_CONTAINER_SIZE_Y      = 36;
   public    static final int    GRAPH_OBJECT_DEFAULT_DISTANCE_X    = (14 * GRAPH_OBJECT_STRETCH_FACTOR);
   public    static final int    GRAPH_OBJECT_DEFAULT_DISTANCE_Y    = (6 * GRAPH_OBJECT_STRETCH_FACTOR);
   protected static final int    GRAPH_OBJECT_DEFAULT_DISTANCE_LC_X = (10 * GRAPH_OBJECT_STRETCH_FACTOR);
   protected static final int    GRAPH_OBJECT_DEFAULT_DISTANCE_LC_Y = (5 * GRAPH_OBJECT_STRETCH_FACTOR);
   
   protected static final String CONTAINER_TYPE_ID               = "ID_PALETTECONTAINER";

   private   static final String LAZY_LI_SRC_STAGE_TEMPLATE      =   "StageID={0}|StageNames={1}|StageTypeIDs={2}|" 
                                                                   + DataStageObjectFactory.DS_LIST_SEPARATOR;
   private   static final String LAZY_LI_LINK_TEMPLATE           =   "LI=OutputPinID={0}|LI=LinkHasMetaDatas={1}|LI=LinkNamePositionXs={2}|"
                                                                   + "LI=LinkNamePositionYs={3}|LI=LinkNames={4}|LI=LinkTypes={5}|";
   private   static final String LAZY_LI_NEXT_STAGE_TEMPLATE     =   "LI=TargetStageIDs={0}|LI=SourceStageEffectiveExecutionModes={1}|"
                                                                   + "LI=SourceStageRuntimeExecutionModes={2}|LI=TargetStageEffectiveExecutionModes={3}|"
                                                                   + "LI=TargetStageRuntimeExecutionModes={4}|LI=LinkIsSingleOperatorLookup={5}|"
                                                                   + "LI=LinkIsSortSequential={6}|LI=LinkPartColMode={7}|LI=LinkSortMode={8}"
                                                                   + DataStageObjectFactory.DS_LIST_SEPARATOR;
   
   private   static final String DS_LINK_META_DATAS_DEFAULT              = "1";
   private   static final String DS_LINK_TYPE_DEFAULT                    = "1";
   private   static final String DS_LINK_TYPE_REFERENCE                  = "2";
   private   static final String DS_LINK_TYPE_REJECT                     = "3";
   private   static final String DS_LINK_SINGLE_OPERATION_LOOKUP_DEFAULT = "0";
   private   static final String DS_LINK_SORT_SEQUENTIAL_DEFAULT         = "0";
   private   static final String DS_LINK_PART_COL_MODE                   = "1";
   private   static final String DS_LINK_SORT_MODE                       = "0";
	
   
	// -------------------------------------------------------------------------------------
	// Member Variables
	// -------------------------------------------------------------------------------------
   protected StringBuffer    _StageListBuf;
   protected StringBuffer    _StageTypesBuf;
   protected StringBuffer    _StageXPosBuf;
   protected StringBuffer    _StageYPosBuf;
   protected StringBuffer    _StageXSizeBuf;
   protected StringBuffer    _StageYSizeBuf;
   protected StringBuffer    _LazyLoadInfoBuf;

	
	static String copyright() {
		return com.ibm.is.sappack.gen.server.jobgenerator.layout.Copyright.IBM_COPYRIGHT_SHORT;
	}
	

	public BaseLayout() {
	   
		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.entry();
		}

      // create the needed buffers 
      _LazyLoadInfoBuf = new StringBuffer();
      _StageListBuf    = new StringBuffer();
      _StageTypesBuf   = new StringBuffer();
      _StageXPosBuf    = new StringBuffer();
      _StageYPosBuf    = new StringBuffer();
      _StageXSizeBuf   = new StringBuffer();
      _StageYSizeBuf   = new StringBuffer();
		
		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.exit();
		}
	} // end of BaseLayout()

	

	protected void appendSeparators()
	{
      // append the separator to each single buffer
      _StageListBuf.append(DataStageObjectFactory.DS_VIEW_VALUE_SEPARATOR);
      _StageTypesBuf.append(DataStageObjectFactory.DS_VIEW_VALUE_SEPARATOR);
      _StageXPosBuf.append(DataStageObjectFactory.DS_VIEW_VALUE_SEPARATOR);
      _StageYPosBuf.append(DataStageObjectFactory.DS_VIEW_VALUE_SEPARATOR);
      _StageXSizeBuf.append(DataStageObjectFactory.DS_VIEW_VALUE_SEPARATOR);
      _StageYSizeBuf.append(DataStageObjectFactory.DS_VIEW_VALUE_SEPARATOR);
	} // end of appendSeparators()
	
	
   /**
    * This method calculates the position of the name of a link dependent on the position of the associated source and
    * target stage.
    * 
    * @param srcStagePos
    *           source stage position
    * @param nextStagePos
    *           successor stage position
    * 
    * @return position of the link name
    */
   private GraphJobObjPosition calculateLinkNamePositions(GraphJobObjPosition srcStagePos, GraphJobObjPosition nextStagePos) {
      int linkNameXPos;
      int linkNameYPos;

      linkNameXPos = ((srcStagePos.getXPos() + nextStagePos.getXPos()) / 2);
      linkNameYPos = ((srcStagePos.getYPos() + nextStagePos.getYPos()) / 2);

      if (TraceLogger.isTraceEnabled()) {
         TraceLogger.trace(TraceLogger.LEVEL_FINEST, "Link pos (X) = " + linkNameXPos + " - (Y) = " + linkNameYPos);
      }

      return (new GraphJobObjPosition(linkNameXPos, linkNameYPos));
   } // end of calculateLinkNamePositions()

   
   public DSDesignView createLayout(MainObject parentContainerObj, List sourceNodesList, 
                                    DataStageObjectFactory dsFactory)
           throws DSAccessException, 
                  JobGeneratorException {

      GraphJobNode                           srcJobNode;
      DSDesignView                           newDesignView;
      String                                 lazyLoadInfo;
      Map<JobComponent, GraphJobObjPosition> stagePosMap;
      Iterator                               srcNodeListIter;
      int                                    startXPos;
      int                                    startYPos;
      boolean                                doAppendValueSeparator;
      boolean                                isParentJobObject;

      if (parentContainerObj == null) {
         throw new IllegalArgumentException("ParentContainerObj must not be null.");
      }
      if (sourceNodesList == null) {
         throw new IllegalArgumentException("SourceNodeList must not be null.");
      }
      
      if (TraceLogger.isTraceEnabled()) {
         TraceLogger.entry("Container = " + parentContainerObj.getName() + " - src nodes = " + sourceNodesList.size());
      }

      // determine if the parent is a JobObject or a LocalContainer
      if (parentContainerObj instanceof DSJobDef) {
         isParentJobObject = true;
      }
      else {
         if (parentContainerObj instanceof DSLocalContainerDef) {
            isParentJobObject = false;
         }
         else {
            throw new JobGeneratorException("120500E", new String[] { parentContainerObj.getClass().getName() });
         } // end of (else) if (parentContainerObj instanceof DSLocalContainerDef)
      } // end of (else) if (parentContainerObj instanceof DSJobDef)
      
      doAppendValueSeparator = false;

      // create the design view ...
      newDesignView = dsFactory.createDSDesignViewDefinition(parentContainerObj);
      
      // set DesignView for JobDefintion or ContainerDefinition ???
      if (isParentJobObject) {
         DSJobDef jobDef = (DSJobDef) parentContainerObj;
         jobDef.setHas_DSDesignView(newDesignView);
      }
      else {
         
         StringBuffer pinBuffer;
         Iterator     pinIter;
         
         DSLocalContainerDef contDef = (DSLocalContainerDef) parentContainerObj;
         contDef.setHas_DSDesignView(newDesignView);
         newDesignView.setName(contDef.getName());
         
         // update DesignView with names of the existing InputPins
         pinBuffer = new StringBuffer();
         pinIter   = contDef.getHas_InputPin().iterator();
         while(pinIter.hasNext()) {
            pinBuffer.append(((DSInputPin) pinIter.next()).getInternalID());
            
            if (pinIter.hasNext()) {
               pinBuffer.append(DataStageObjectFactory.DS_VIEW_VALUE_SEPARATOR);
            }
         } // end of while(pinIter.hasNext())
         newDesignView.setInputPins(pinBuffer.toString());
// System.out.println("hansx: newDesignView InputPins = " + pinBuffer.toString());
         
         // update DesignView with names of the existing OutputPins 
         pinBuffer.setLength(0);
         pinIter = contDef.getHas_OutputPin().iterator();
         while(pinIter.hasNext()) {
            pinBuffer.append(((DSOutputPin) pinIter.next()).getInternalID());
            
            if (pinIter.hasNext()) {
               pinBuffer.append(DataStageObjectFactory.DS_VIEW_VALUE_SEPARATOR);
            }
         } // end of while(pinIter.hasNext())
         
         newDesignView.setOutputPins(pinBuffer.toString());
// System.out.println("hansx: newDesignView OutputPins = " + pinBuffer.toString());
      } // end of (else) if (parentContainerObj instanceof DSJobDef)

      // create StagePosition map
      stagePosMap = new HashMap<JobComponent, GraphJobObjPosition>();

      // determine the Y start position dependent on the parent type
      if (isParentJobObject) {
         startYPos = 4 * GRAPH_OBJECT_STRETCH_FACTOR;
      }
      else {
         startYPos = GRAPH_OBJECT_STRETCH_FACTOR;
      }
      
      // process all source stages ...
      srcNodeListIter = sourceNodesList.iterator();
      while (srcNodeListIter.hasNext()) {
         
         srcJobNode  = (GraphJobNode) srcNodeListIter.next();

         if (isParentJobObject) {
            startXPos = 8 * GRAPH_OBJECT_STRETCH_FACTOR;
            startYPos = startYPos + (7 * GRAPH_OBJECT_STRETCH_FACTOR);
         }
         else {
            startXPos = 4 * GRAPH_OBJECT_STRETCH_FACTOR;
            startYPos = startYPos + GRAPH_OBJECT_DEFAULT_DISTANCE_LC_Y;
         }
         
         doAppendValueSeparator = computeDesignViewDataForGraphNode(srcJobNode, startXPos, startYPos, 
                                                                    stagePosMap, doAppendValueSeparator);
      } // end of while(outLinkListIter.hasNext())

      // prepare LazyLoadInfo buffer
      lazyLoadInfo = _LazyLoadInfoBuf.toString();
      lazyLoadInfo = lazyLoadInfo.replaceAll("/", "_");
      lazyLoadInfo = removeLastChar(lazyLoadInfo);
      
      // if there is a separator at the end ...
      if (_StageListBuf.length() > 0)
      {
         if (_StageListBuf.charAt(_StageListBuf.length()-1) == DataStageObjectFactory.DS_VIEW_VALUE_SEPARATOR)
         {
            // ==> remove the separator
            _StageListBuf.deleteCharAt(_StageListBuf.length()-1);
            _StageTypesBuf.deleteCharAt(_StageTypesBuf.length()-1);
            _StageXPosBuf.deleteCharAt(_StageXPosBuf.length()-1);
            _StageYPosBuf.deleteCharAt(_StageYPosBuf.length()-1);
            _StageXSizeBuf.deleteCharAt(_StageXSizeBuf.length()-1);
            _StageYSizeBuf.deleteCharAt(_StageYSizeBuf.length()-1);
         } // end of if (_StageListBuf.charAt(_StageListBuf.length()-1) == DataStageObjectFactory.DS_VIEW_VALUE_SEPARATOR)

         // remove final separator (if exists) 
         if (lazyLoadInfo.charAt(lazyLoadInfo.length()-1) == DataStageObjectFactory.DS_VIEW_VALUE_SEPARATOR)
         {
            lazyLoadInfo = removeLastChar(lazyLoadInfo);
         }
      } // end of if (_StageListBuf.length() > 0)
        
      // last not least update the DesignView instance
      newDesignView.setStageList(_StageListBuf.toString());
      newDesignView.setStageTypes(_StageTypesBuf.toString());
      newDesignView.setStageXPos(_StageXPosBuf.toString());
      newDesignView.setStageYPos(_StageYPosBuf.toString());
      newDesignView.setStageXSize(_StageXSizeBuf.toString());
      newDesignView.setStageYSize(_StageYSizeBuf.toString());
      newDesignView.setLazyLoadInfo(lazyLoadInfo);

/*      
System.out.println("Stages       = " + _StageListBuf);
System.out.println("Stage types  = " + _StageTypesBuf);
System.out.println("Stage X-Pos  = " + _StageXPosBuf);
System.out.println("Stage Y-Pos  = " + _StageYPosBuf);
System.out.println("Stage X-Size = " + _StageXSizeBuf);
System.out.println("Stage Y-Size = " + _StageYSizeBuf);
System.out.println("LazyLoadInfo = " + lazyLoadInfo + "<-----");
*/
      
      if (TraceLogger.isTraceEnabled()) {
         TraceLogger.trace(TraceLogger.LEVEL_FINEST, "Stages       = " + _StageListBuf);
         TraceLogger.trace(TraceLogger.LEVEL_FINEST, "Stage types  = " + _StageTypesBuf);
         TraceLogger.trace(TraceLogger.LEVEL_FINEST, "Stage X-Pos  = " + _StageXPosBuf);
         TraceLogger.trace(TraceLogger.LEVEL_FINEST, "Stage Y-Pos  = " + _StageYPosBuf);
         TraceLogger.trace(TraceLogger.LEVEL_FINEST, "Stage X-Size = " + _StageXSizeBuf);
         TraceLogger.trace(TraceLogger.LEVEL_FINEST, "Stage Y-Size = " + _StageYSizeBuf);
         TraceLogger.trace(TraceLogger.LEVEL_FINEST, "LazyLoadInfo = " + lazyLoadInfo);

         TraceLogger.exit("DesignView = " + newDesignView);
      }

      return (newDesignView);
   } // end of createLayout()


	protected String createLazyLoadInfoForSourceStage(String stageInternalId, String stageName, String stageType) {
	   
		String retBuf;

		// create 'LazyLoadInfo' for the source stage
		retBuf = MessageFormat.format(LAZY_LI_SRC_STAGE_TEMPLATE, 
		                              new Object[] { stageInternalId, stageName, stageType });
		
		return (retBuf);
	} // end of createLazyLoadInfoForSourceStage()

	
	/**
	 * This method creates the LazyLoadInfo (LLI) for the passed stage and Job Link list.
	 * <p>
	 * It queries the required values from the stage instance and the available link instances. Then it provides the
	 * values to the MessageFomatter and creates the LLI (link) data.
	 * 
	 * @param srcJobComponent  source JobComponent instance
	 * @param jobLinksList     list of JobLinks of the passed stage
	 * @param stagePosMap      stage positions map
	 */
	protected String createLazyLoadInfoPart(JobComponent srcJobComponent, List jobLinksList, Map stagePosMap) {
	   
      GraphJobNode        nextGraphNode;
	   GraphJobLink        graphJobLink;
		GraphJobObjPosition graphLinkNamePos;
		DSLink              outLink;
		JobComponent        nextJobComponent;
		StringBuffer        lazyLoadPartBuf;
		String              workBuf;
		String              outputPinIDs;
		String              linkHasMetaDatas;
		String              linkNamePositionXs;
		String              linkNamePositionYs;
		String              linkNames;
		String              linkTypes;
		String              targetStageIDs;
		String              sourceStageEffectiveExecutionModes;
		String              sourceStageRuntimeExecutionModes;
		String              targetStageEffectiveExecutionModes;
		String              targetStageRuntimeExecutionModes;
		String              linkIsSingleOperatorLookup;
		String              linkIsSortSequential;
		String              linkPartColMode;
		String              linkSortMode;
		String              internalId;
		Iterator            jobLinksIter;

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.entry("Starting with " + srcJobComponent.getName());
		}

		// allocate buffers
		lazyLoadPartBuf = new StringBuffer();

		// initialize link data variables
		outputPinIDs                       = "";
		linkHasMetaDatas                   = "";
		linkNamePositionXs                 = "";
		linkNamePositionYs                 = "";
		linkNames                          = "";
		linkTypes                          = "";
		targetStageIDs                     = "";
		sourceStageEffectiveExecutionModes = "";
		sourceStageRuntimeExecutionModes   = "";
		targetStageEffectiveExecutionModes = "";
		targetStageRuntimeExecutionModes   = "";
		linkIsSingleOperatorLookup         = "";
		linkIsSortSequential               = "";
		linkPartColMode                    = "";
		linkSortMode                       = "";

		// process all passed job links
		jobLinksIter = jobLinksList.iterator();
		while (jobLinksIter.hasNext()) {
		   graphJobLink     = (GraphJobLink) jobLinksIter.next();
			outLink          = graphJobLink.getDSOutputLink();
			nextGraphNode    = graphJobLink.getTargetGraphNode();
			nextJobComponent = nextGraphNode.getJobComponent();

			// go on if the output link exists
			if (outLink != null) {
				// setup the LazyLoadInfo data for the current link
			   graphLinkNamePos    = calculateLinkNamePositions((GraphJobObjPosition) stagePosMap.get(srcJobComponent), 
				                                                 (GraphJobObjPosition) stagePosMap.get(nextJobComponent));
		 		outputPinIDs       += ((DSOutputPin) outLink.getFrom_OutputPin()).getInternalID()
				                            + DataStageObjectFactory.DS_LAZY_LI_DATA_SEPARATOR;
				linkHasMetaDatas   += DS_LINK_META_DATAS_DEFAULT + DataStageObjectFactory.DS_LAZY_LI_DATA_SEPARATOR;
				linkNamePositionXs += Integer.toString(graphLinkNamePos.getXPos()) + DataStageObjectFactory.DS_LAZY_LI_DATA_SEPARATOR;
				linkNamePositionYs += Integer.toString(graphLinkNamePos.getYPos()) + DataStageObjectFactory.DS_LAZY_LI_DATA_SEPARATOR;
				linkNames          += outLink.getName() + DataStageObjectFactory.DS_LAZY_LI_DATA_SEPARATOR;

				// determine link type
				switch (outLink.getLinkType().getValue()) {
				   case LinkTypeEnum.REJECT:
					     linkTypes += DS_LINK_TYPE_REJECT + DataStageObjectFactory.DS_LAZY_LI_DATA_SEPARATOR;
					     break;

				   case LinkTypeEnum.REFERENCE:
					     linkTypes += DS_LINK_TYPE_REFERENCE + DataStageObjectFactory.DS_LAZY_LI_DATA_SEPARATOR;
					     break;

                case LinkTypeEnum.CONTROL:
                case LinkTypeEnum.PRIMARY:
                default:
					      linkTypes += DS_LINK_TYPE_DEFAULT + DataStageObjectFactory.DS_LAZY_LI_DATA_SEPARATOR;
				} // end of switch(outLink.getLinkType().getValue())

				// determine the 'Internal Id' of the 'next' JobComponemt
		      if (graphJobLink.getTargetGraphNode().isStage()) {
		         internalId = nextGraphNode.getDSStage().getInternalID();
		      }
		      else {
               internalId = nextGraphNode.getContainer().getShortDescription();
		      } // end of (else) if (curJobNode.isStage())
				
				targetStageIDs                     += internalId + DataStageObjectFactory.DS_LAZY_LI_DATA_SEPARATOR;
				sourceStageEffectiveExecutionModes +=  getSourceStageExecutionMode(srcJobComponent) + DataStageObjectFactory.DS_LAZY_LI_DATA_SEPARATOR;
				sourceStageRuntimeExecutionModes   +=  getSourceStageExecutionMode(srcJobComponent) + DataStageObjectFactory.DS_LAZY_LI_DATA_SEPARATOR;
				targetStageEffectiveExecutionModes +=  getTargetExecutionMode(nextJobComponent) + DataStageObjectFactory.DS_LAZY_LI_DATA_SEPARATOR;
				targetStageRuntimeExecutionModes   +=  getTargetExecutionMode(nextJobComponent) + DataStageObjectFactory.DS_LAZY_LI_DATA_SEPARATOR;
				linkIsSingleOperatorLookup         += DS_LINK_SINGLE_OPERATION_LOOKUP_DEFAULT + DataStageObjectFactory.DS_LAZY_LI_DATA_SEPARATOR;
				linkIsSortSequential               += DS_LINK_SORT_SEQUENTIAL_DEFAULT + DataStageObjectFactory.DS_LAZY_LI_DATA_SEPARATOR;
				linkPartColMode                    += DS_LINK_PART_COL_MODE + DataStageObjectFactory.DS_LAZY_LI_DATA_SEPARATOR;
				linkSortMode                       += DS_LINK_SORT_MODE + DataStageObjectFactory.DS_LAZY_LI_DATA_SEPARATOR;
			}
		} // end of while(jobLinksIter.hasNext())

		// saving link info is required only if there is some data
		if (outputPinIDs.length() > 0) {
			// remove the last character
			outputPinIDs                       = removeLastChar(outputPinIDs);
			linkHasMetaDatas                   = removeLastChar(linkHasMetaDatas);
			linkNamePositionXs                 = removeLastChar(linkNamePositionXs);
			linkNamePositionYs                 = removeLastChar(linkNamePositionYs);
			linkNames                          = removeLastChar(linkNames);
			linkTypes                          = removeLastChar(linkTypes);
			targetStageIDs                     = removeLastChar(targetStageIDs);
			sourceStageEffectiveExecutionModes = removeLastChar(sourceStageEffectiveExecutionModes);
			sourceStageRuntimeExecutionModes   = removeLastChar(sourceStageRuntimeExecutionModes);
			targetStageEffectiveExecutionModes = removeLastChar(targetStageEffectiveExecutionModes);
			targetStageRuntimeExecutionModes   = removeLastChar(targetStageRuntimeExecutionModes);
			linkIsSingleOperatorLookup         = removeLastChar(linkIsSingleOperatorLookup);
			linkIsSortSequential               = removeLastChar(linkIsSortSequential);
			linkPartColMode                    = removeLastChar(linkPartColMode);
			linkSortMode                       = removeLastChar(linkSortMode);

			// create 'LazyLoadInfo' for the 'next stage
			workBuf = MessageFormat.format(LAZY_LI_LINK_TEMPLATE, new Object[] { outputPinIDs, linkHasMetaDatas,
			                                                                     linkNamePositionXs, linkNamePositionYs, 
			                                                                     linkNames, linkTypes });
			lazyLoadPartBuf.append(workBuf);
			workBuf = MessageFormat.format(LAZY_LI_NEXT_STAGE_TEMPLATE, new Object[] { targetStageIDs,
			                                                                           sourceStageEffectiveExecutionModes, 
			                                                                           sourceStageRuntimeExecutionModes,
			                                                                           targetStageEffectiveExecutionModes, 
			                                                                           targetStageRuntimeExecutionModes, 
			                                                                           linkIsSingleOperatorLookup,
			                                                                           linkIsSortSequential, 
			                                                                           linkPartColMode, 
			                                                                           linkSortMode });
			lazyLoadPartBuf.append(workBuf);
		} // end of if (outputPinIDs.length() > 0)

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.trace(TraceLogger.LEVEL_FINEST, "LazyLoadInfo = " + lazyLoadPartBuf);
			TraceLogger.exit();
		}

		return (lazyLoadPartBuf.toString());
	} // end of createLazyLoadInfoPart()


	private String getSourceStageExecutionMode(JobComponent srcComponent) {
		String retSourceStageExecutionMode = "1";
		if (srcComponent instanceof DSStage) {
		   
		   DSStage srcStage = (DSStage) srcComponent; 
		   DSMetaBag metaBag = srcStage.getOf_DSMetaBag();
		   if (metaBag != null                                                                 && 
		       metaBag.getNames().contains(DataStageObjectFactory.METABAG_TF_EXECMODE_NAME) &&
		       metaBag.getValues().contains(DataStageObjectFactory.METABAG_TF_EXECMODE_VALUES_SEQ)) {
			   retSourceStageExecutionMode = "1";
			   return (retSourceStageExecutionMode);
		   } 
		   else if (metaBag != null                                                                 &&
		            metaBag.getNames().contains(DataStageObjectFactory.METABAG_TF_EXECMODE_NAME) &&
		            !metaBag.getValues().contains(DataStageObjectFactory.METABAG_TF_EXECMODE_VALUES_SEQ)) {
			   retSourceStageExecutionMode = "2";
			   return (retSourceStageExecutionMode);
		   }

//		   //in case of a funnel stage with no metaBag we have parallel execution 
		   if (metaBag == null && srcStage.getOf_StageType().getName().equals(DSStageTypeEnum.PX_FUNNEL_LITERAL.getName())) {
			   retSourceStageExecutionMode = "2";
			   return (retSourceStageExecutionMode);
		   }

		   if (srcStage.getStageTypeClassName().equals(StageProperties.STAGE_TYPE_CLASS_NAME_TRANSFORMER)) {
		      retSourceStageExecutionMode = "2";
		   }
		   
		   if (srcStage.getStageTypeClassName().equals(StageProperties.STAGE_TYPE_CLASS_NAME_CONTAINER)) {
              retSourceStageExecutionMode = "2";
           }
		}
		else {
		   // Container definition
         retSourceStageExecutionMode = "1";
		}
		   
		return (retSourceStageExecutionMode);
	} // end of getSourceStageExecutionMode()

	
	private String getTargetExecutionMode(JobComponent trgComponent) {
		String retTargetExecutionMode = "2";

		if (trgComponent instanceof DSStage) {
			   
			DSStage trgStage = (DSStage) trgComponent; 

			//if the metaBag contains the key Execution mode and the value seq
		   DSMetaBag metaBag = trgStage.getOf_DSMetaBag();
		   if (metaBag != null                                                                 &&
		       metaBag.getNames().contains(DataStageObjectFactory.METABAG_TF_EXECMODE_NAME) &&
		       metaBag.getValues().contains(DataStageObjectFactory.METABAG_TF_EXECMODE_VALUES_SEQ)) {
		    	retTargetExecutionMode = "1";
		    } 
		}

		return (retTargetExecutionMode);
/* not required at the moment		
      if (trgComponent instanceof DSStage) {
         DSStage trgStage = (DSStage) trgComponent;
         
         if (trgStage.getStageTypeClassName().equals(StageProperties.STAGE_TYPE_CLASS_NAME_TRANSFORMER)) {
            retTargetExecutionMode = "2";
         }
      }
      else {
         // Container definition
         retTargetExecutionMode = "2";
      }
*/
		

		/*
		 * if ((node.getNodeType() instanceof GraphInterfaceNodeType) || (node.getNodeType() instanceof
		 * GraphTransformerNodeType)) { if ((lnk.getTargetNode().getNodeType() instanceof
		 * GraphConnectionNodeType)&&(lnk.getTargetNode
		 * ().getNodeType().getStageTypeName().equals(DSStageTypeEnum.PX_SEQUENTIAL_FILE_LITERAL))){
		 * targetStageEffectiveExecutionModes = targetStageEffectiveExecutionModes + "1" + ","; //$NON-NLS-1$
		 * //$NON-NLS-2$ targetStageRuntimeExecutionModes = targetStageRuntimeExecutionModes + "1" + ","; //$NON-NLS-1$
		 * //$NON-NLS-2$ }else if ((lnk.getTargetNode().getNodeType() instanceof
		 * GraphConnectionNodeType)&&(lnk.getTargetNode
		 * ().getNodeType().getStageTypeName().equals(DSStageTypeEnum.PX_SYBASE_LITERAL))){
		 * targetStageEffectiveExecutionModes = targetStageEffectiveExecutionModes + "1" + ","; //$NON-NLS-1$
		 * //$NON-NLS-2$ targetStageRuntimeExecutionModes = targetStageRuntimeExecutionModes + "1" + ","; //$NON-NLS-1$
		 * //$NON-NLS-2$ }else if ((lnk.getTargetNode().getNodeType() instanceof
		 * GraphConnectionNodeType)&&(lnk.getTargetNode
		 * ().getNodeType().getStageTypeName().equals(DSStageTypeEnum.MQ_SERIES_PX_LITERAL))){
		 * targetStageEffectiveExecutionModes = targetStageEffectiveExecutionModes + "1" + ","; //$NON-NLS-1$
		 * //$NON-NLS-2$ targetStageRuntimeExecutionModes = targetStageRuntimeExecutionModes + "1" + ","; //$NON-NLS-1$
		 * //$NON-NLS-2$ }else { targetStageEffectiveExecutionModes = targetStageEffectiveExecutionModes + "2" + ",";
		 * //$NON-NLS-1$ //$NON-NLS-2$ targetStageRuntimeExecutionModes = targetStageRuntimeExecutionModes + "2" + ",";
		 * //$NON-NLS-1$ //$NON-NLS-2$ } for (Iterator outputs = node.getOutputLinks().iterator(); outputs.hasNext();){
		 * NodeLink output = (NodeLink)outputs.next(); if ((output.getSortable() != null) &&
		 * (output.getSortable().booleanValue())) { linkPartColMode = linkPartColMode + "3" + ","; //$NON-NLS-1$
		 * //$NON-NLS-2$ linkSortMode = linkSortMode + "1" + ","; //$NON-NLS-1$ //$NON-NLS-2$ } else if
		 * ((lnk.getTargetNode().getNodeType() instanceof GraphConnectionNodeType) && (lnk.getTargetNode().getNodeType()
		 * .getStageTypeName() .equals(DSStageTypeEnum.PX_DB2_LITERAL))) { linkPartColMode = linkPartColMode + "9" + ",";
		 * //$NON-NLS-1$ //$NON-NLS-2$ linkSortMode = linkSortMode + "0" + ","; //$NON-NLS-1$ //$NON-NLS-2$ } else {
		 * linkPartColMode = linkPartColMode + "1" + ","; //$NON-NLS-1$ //$NON-NLS-2$ linkSortMode = linkSortMode + "0" +
		 * ","; //$NON-NLS-1$ //$NON-NLS-2$ } } } else { if ((lnk.getTargetNode().getNodeType() instanceof
		 * GraphConnectionNodeType
		 * )&&(lnk.getTargetNode().getNodeType().getStageTypeName().equals(DSStageTypeEnum.PX_SYBASE_LITERAL))){
		 * targetStageEffectiveExecutionModes = targetStageEffectiveExecutionModes + "1" + ","; //$NON-NLS-1$
		 * //$NON-NLS-2$ targetStageRuntimeExecutionModes = targetStageRuntimeExecutionModes + "1" + ","; //$NON-NLS-1$
		 * //$NON-NLS-2$ linkPartColMode = linkPartColMode + "1" + ","; //$NON-NLS-1$ //$NON-NLS-2$ linkSortMode =
		 * linkSortMode + "0" + ","; //$NON-NLS-1$ //$NON-NLS-2$ }else if ((lnk.getTargetNode().getNodeType() instanceof
		 * GraphConnectionNodeType
		 * )&&(lnk.getTargetNode().getNodeType().getStageTypeName().equals(DSStageTypeEnum.PX_SEQUENTIAL_FILE_LITERAL))){
		 * targetStageEffectiveExecutionModes = targetStageEffectiveExecutionModes + "1" + ","; //$NON-NLS-1$
		 * //$NON-NLS-2$ targetStageRuntimeExecutionModes = targetStageRuntimeExecutionModes + "1" + ","; //$NON-NLS-1$
		 * //$NON-NLS-2$ linkPartColMode = linkPartColMode + "1" + ","; //$NON-NLS-1$ //$NON-NLS-2$ linkSortMode =
		 * linkSortMode + "0" + ","; //$NON-NLS-1$ //$NON-NLS-2$ }else if ((lnk.getTargetNode().getNodeType() instanceof
		 * GraphConnectionNodeType
		 * )&&(lnk.getTargetNode().getNodeType().getStageTypeName().equals(DSStageTypeEnum.MQ_SERIES_PX_LITERAL))){
		 * targetStageEffectiveExecutionModes = targetStageEffectiveExecutionModes + "1" + ","; //$NON-NLS-1$
		 * //$NON-NLS-2$ targetStageRuntimeExecutionModes = targetStageRuntimeExecutionModes + "1" + ","; //$NON-NLS-1$
		 * //$NON-NLS-2$ linkPartColMode = linkPartColMode + "1" + ","; //$NON-NLS-1$ //$NON-NLS-2$ linkSortMode =
		 * linkSortMode + "0" + ","; //$NON-NLS-1$ //$NON-NLS-2$ }else if ((lnk.getTargetNode().getNodeType() instanceof
		 * GraphConnectionNodeType
		 * )&&(lnk.getTargetNode().getNodeType().getStageTypeName().equals(DSStageTypeEnum.PX_DB2_LITERAL))){
		 * targetStageEffectiveExecutionModes = targetStageEffectiveExecutionModes + "2" + ","; //$NON-NLS-1$
		 * //$NON-NLS-2$ targetStageRuntimeExecutionModes = targetStageRuntimeExecutionModes + "2" + ","; //$NON-NLS-1$
		 * //$NON-NLS-2$ linkPartColMode = linkPartColMode + "9" + ","; //$NON-NLS-1$ //$NON-NLS-2$ linkSortMode =
		 * linkSortMode + "0" + ","; //$NON-NLS-1$ //$NON-NLS-2$ }else{ targetStageEffectiveExecutionModes =
		 * targetStageEffectiveExecutionModes + "2" + ","; //$NON-NLS-1$ //$NON-NLS-2$ targetStageRuntimeExecutionModes =
		 * targetStageRuntimeExecutionModes + "2" + ","; //$NON-NLS-1$ //$NON-NLS-2$ linkPartColMode = linkPartColMode +
		 * "1" + ","; //$NON-NLS-1$ //$NON-NLS-2$ linkSortMode = linkSortMode + "0" + ","; //$NON-NLS-1$ //$NON-NLS-2$ } }
		 */
	} // end of getTargetExecutionMode()

	
	protected boolean isLookupStageGraphNodeAndDoesExists(GraphJobNode curJobNode)
	{
	   DSStage dsStage;
	   String  stageType;
	   boolean retDoesExist;
	   
      retDoesExist = false;
      
      // 1st check: is a stage ??
      if (curJobNode.isStage()) {
         
         dsStage = curJobNode.getDSStage();
         
         // 2nd check: is a Lookup stage ??
         stageType = dsStage.getStageType(); 
         if (stageType != null && 
             stageType.equals(DSStageTypeEnum.PX_LOOKUP_LITERAL.getName())) {
            // 3rd check: does the stage already exists in the view
            if (_StageListBuf.indexOf(dsStage.getInternalID()) > -1) {
               
               retDoesExist = true;
            } // end of if (_StageListBuf.indexOf(dsStage.getInternalID()) > -1)
         } // end of if (stageType != null && stageType.equals(DSStageTypeEnum.PX_LOOKUP_LITERAL.getName()))
      } // end of if (curJobNode.isStage())
      
      if (TraceLogger.isTraceEnabled()) {
         TraceLogger.trace(TraceLogger.LEVEL_FINEST, "Is Lookup stage and it has already been processed: " + retDoesExist);
      }
      
      return(retDoesExist);
	} // end of isLookupStageGraphNodeAndDoesExists()


	/**
	 * This method removes the last character of the passes string.
	 * 
	 * @param str
	 *           string of that the last character is removed
	 * 
	 * @return modified string (without the last character)
	 */
	protected String removeLastChar(String str) {
		String retString;

		if (str.length() > 1) {
			retString = str.substring(0, str.length() - 1);
		}
		else {
			retString = str;
		}

		return (retString);
	} // end of removeLastChar()


	
   // -------------------------------------------------------------------------------------
   //                              Abstract Methods
   // -------------------------------------------------------------------------------------
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
   protected abstract boolean computeDesignViewDataForGraphNode(GraphJobNode curJobNode, int curXPos, int curYPos, 
                                                                Map<JobComponent, GraphJobObjPosition> stagePosMap,
                                                                boolean doAppendValueSeparator) 
             throws JobGeneratorException;
   
} // end of abstract class BaseLayout
