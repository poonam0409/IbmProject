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
// Module Name : com.ibm.is.sappack.gen.server.jobgenerator
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.server.jobgenerator;


import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import DataStageX.DSJobDef;
import DataStageX.DSLink;
import DataStageX.DSStage;

import com.ibm.is.sappack.gen.common.BaseException;
import com.ibm.is.sappack.gen.common.Constants;
import com.ibm.is.sappack.gen.common.JobGeneratorException;
import com.ibm.is.sappack.gen.common.ODBCStageData;
import com.ibm.is.sappack.gen.common.PersistenceData;
import com.ibm.is.sappack.gen.common.request.JobGeneratorRequest;
import com.ibm.is.sappack.gen.common.request.RequestJobTypeIDocLoad;
import com.ibm.is.sappack.gen.common.request.SupportedTableTypesMap;
import com.ibm.is.sappack.gen.common.trace.TraceLogger;
import com.ibm.is.sappack.gen.common.util.StringUtils;
import com.ibm.is.sappack.gen.server.common.service.ServiceToken;
import com.ibm.is.sappack.gen.server.common.util.DSAccessException;
import com.ibm.is.sappack.gen.server.datastage.DataStageObjectFactory;
import com.ibm.is.sappack.gen.server.jobgenerator.layout.BaseLayout;
import com.ibm.is.sappack.gen.server.jobgenerator.layout.IDocLoadJobLayout;
import com.ibm.is.sappack.gen.server.jobgenerator.stages.SAPIDocLoadStageProperties;
import com.ibm.is.sappack.gen.server.jobgenerator.stages.SAPStageProperties;
import com.ibm.is.sappack.gen.server.jobgenerator.stages.StageFactory;
import com.ibm.is.sappack.gen.server.jobgenerator.stages.TransformerStageProperties;
import com.ibm.is.sappack.gen.server.util.ColumnData;
import com.ibm.is.sappack.gen.server.util.GraphJobLink;
import com.ibm.is.sappack.gen.server.util.GraphJobNode;
import com.ibm.is.sappack.gen.server.util.ModelInfoBlock;
import com.ibm.is.sappack.gen.server.util.ObjectParamMap;
import com.ibm.is.sappack.gen.server.util.StageData;
import com.ibm.is.sappack.gen.server.util.TableData;


public class IDocLoadJob extends BaseIDocJob {
	// -------------------------------------------------------------------------------------
	// Constants
	// -------------------------------------------------------------------------------------
	private static final String IDOC_LOAD_STATUS_MAPPING_SCHEMA_MODEL_V65 	= "ILSMappingSchema-v6-5.dbm";
    private static final String IDOC_LOAD_STATUS_MAPPING_SCHEMA_MODEL_V7  	= "ILSMappingSchema.dbm";
	private static final String ILS_TRANSFORMER_NAME                      	= "ILS_Transformer";
	private static final String ILS_FUNNEL_NAME                    			= "Collect_ADMDocNums";
	private static final String ILS_REMOVE_DUPLICATES_NAME                 	= "Create_Distinct_ADMDocNums";
	private static final int LOAD_STATUS_TABLE_COUNT                      	= 7;

	// -------------------------------------------------------------------------------------
	// Member Variables
	// -------------------------------------------------------------------------------------
	private TableData  _SrcTableDataArr[];
	
	static String copyright() {
		return com.ibm.is.sappack.gen.server.jobgenerator.Copyright.IBM_COPYRIGHT_SHORT;
	}

	
	public IDocLoadJob(ServiceToken parContextToken, RequestJobTypeIDocLoad parJobType,
	                   JobGeneratorRequest parJobReqInfo, Map<String, ModelInfoBlock> physModelID2TableMap) throws BaseException {
		super(parContextToken, parJobType, parJobReqInfo, physModelID2TableMap);
	} // end of IDOCLoadJob()

   
	public static String addQuotationMarks(String origString) {
      return "\"" + origString + "\""; //$NON-NLS-1$ //$NON-NLS-2$
   }
	
	private int getNumberOfRootSegments(TableData[] allSegmentsTableData) {
		int numberOfRootSegments = 0;
		if (allSegmentsTableData != null) {
			for (int tblIdx = 0; tblIdx < allSegmentsTableData.length; tblIdx++) {
				if (allSegmentsTableData[tblIdx].isRootSegment()) 
					numberOfRootSegments++;
			}
		}
		return numberOfRootSegments;
	}
	
	private TableData[] getRootSegmentsTableData(TableData[] allSegmentsTableData) {
		TableData[] retRootSegmentsTableData = new TableData[getNumberOfRootSegments(allSegmentsTableData)];
		int currentRootSegmentsTableDataArrayPosition = 0;
		for (int i = 0; i < allSegmentsTableData.length; i++) {
			if (allSegmentsTableData[i].isRootSegment()) {
				retRootSegmentsTableData[currentRootSegmentsTableDataArrayPosition] = allSegmentsTableData[i];
				currentRootSegmentsTableDataArrayPosition++;
			}
		}
		return retRootSegmentsTableData;		
	}
   
	
	public List<String> create() throws BaseException {
		List<String>  retJobsCreated;

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.entry();
		}

		// ---------------------------
		// ==> process existing tables
		// ---------------------------
		retJobsCreated = new ArrayList<String>();

		if (_SrcTableDataArr != null && _SrcTableDataArr.length > 0) {

			// we have to store the event of job creation failure
			boolean jobCreationError = false;

			try {
				// create a DS task container
				String curJobName = getJobType().getJobName();
				createJobDef(curJobName);
				_JobStructureData.setJobLayout(new IDocLoadJobLayout(((RequestJobTypeIDocLoad) getJobType()).doCreateIDOCLoadStatusInfo()));

				// job gets added to the list of (successfully) created jobs
				// though job creation actually takes place later
				retJobsCreated.add(curJobName);

				// create the flow
				createFlow(_SrcTableDataArr);

				// save the job
				prepareAndSaveJob();
			}
			catch (DSAccessException pDSAccessExcpt) {
				if (TraceLogger.isTraceEnabled()) {
					TraceLogger.traceException(pDSAccessExcpt);
				}

				// something went wrong during job creation
				jobCreationError = true;

            throw new JobGeneratorException("126000E", 
                                            new String[] { pDSAccessExcpt.getLocalizedMessage() }, 
                                            pDSAccessExcpt);
			}
			catch (JobGeneratorException pJobGenExcpt) {

				// something went wrong during job creation
				jobCreationError = true;

				throw pJobGenExcpt;
			}
			catch (Exception pExcpt) {
				if (TraceLogger.isTraceEnabled()) {
					TraceLogger.traceException(pExcpt);
				}

				// something went wrong during job creation
				jobCreationError = true;

            throw new JobGeneratorException("101500E", 
                                            new String[] { pExcpt.getMessage() }, 
                                            pExcpt);
			}
			finally {

				// in case of job creation problems we simply remove
				// the last job from the list of (successfully) created jobs
				// because we cannot be sure that it indeed has been created
				if (jobCreationError) {

					// possibly an exception occurred BEFORE the job could actually
					// be added to the list of created jobs, therefore we must
					// check for the size of the list
					if (retJobsCreated.size() != 0) {
						retJobsCreated.remove(retJobsCreated.size() - 1);
					}
				}
			}
		} // end of if (_SrcTableDataArr != null && _SrcTableDataArr.length > 0)

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.exit("Number of created jobs: " + retJobsCreated.size());
		}

		return (retJobsCreated);
	} // end of create()


	private void createFlow(TableData parTableArr[]) throws BaseException {
		RequestJobTypeIDocLoad jobType;
		GraphJobNode           sourceNode;
		GraphJobNode           newTransformerNodesOfRootSegments[];
		GraphJobNode           currentNewTransformerNode;
		GraphJobNode           idocNode;
		GraphJobLink           transformerLink;
		GraphJobLink           idocLink;
		StageData              newIDOCStage;
		StageData              newPersStageObj;
		StageData              newTransformerStagesOfRootSegments[];
		StageData              currentNewTransformerStage;
		TableData              curTable;
		DataStageObjectFactory dsFactory;
		DSJobDef               jobDef;
		DSLink                 stageLink;
		String                 stageIDOCTypeName;
		boolean                isExtendedIDocType;
      boolean                createIDocLoadStatusStages;
		ObjectParamMap         linkParamMap;
		ObjectParamMap         transformerParamMap;
		int                    tblIdx;

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.entry("SAP Load tables to be processed: " + parTableArr.length);
		}

		dsFactory                  = getDSFactory();
		jobDef                     = _JobStructureData.getJobDef();
		curTable                   = parTableArr[0];
		stageIDOCTypeName          = curTable.getIDocType();
		isExtendedIDocType         = curTable.getIsExtendedIDocType();
		jobType                    = (RequestJobTypeIDocLoad) getJobType();
        createIDocLoadStatusStages = jobType.doCreateIDOCLoadStatusInfo();

		// ***************************
		// OUTPUT: SAP IDOC Load Stage
		// ***************************
		newIDOCStage = StageFactory.createIDOCLoadStage(jobDef, stageIDOCTypeName, isExtendedIDocType, 
		                                                curTable.getIDocBasicType(), curTable.getSegmentType(), 
		                                                jobType.getJobName(), jobType, curTable.isUnicodeSystem(), 
		                                                _JobRequestInfo.doCreateV7Stage(), 
		                                                getServiceToken());

		//calculate idoc stage position
		int xPos = 3 * BaseLayout.GRAPH_OBJECT_DEFAULT_DISTANCE_X;
		int yPos;
		if (!createIDocLoadStatusStages) { //for non ILS jobs
			yPos = BaseLayout.GRAPH_OBJECT_DEFAULT_DISTANCE_Y * (parTableArr.length - 1) / 2;
		} else { //for ILS jobs
			if (getNumberOfRootSegments(parTableArr) == 1) { //for ILS jobs with one root segment
				yPos = (BaseLayout.GRAPH_OBJECT_DEFAULT_DISTANCE_Y * (parTableArr.length - 1) / 2) + BaseLayout.GRAPH_OBJECT_DEFAULT_DISTANCE_Y;
			} else { //for ILS jobs with more than one root segment
				yPos = (BaseLayout.GRAPH_OBJECT_DEFAULT_DISTANCE_Y * (parTableArr.length - 1) / 2) - BaseLayout.GRAPH_OBJECT_DEFAULT_DISTANCE_Y;				
			}
		}
		
		//create Graph node with position
		idocNode = new GraphJobNode(newIDOCStage, xPos, yPos);
		
		//create array for src transformer stages/nodes of root segments
		newTransformerStagesOfRootSegments = new StageData[getNumberOfRootSegments(parTableArr)];
		newTransformerNodesOfRootSegments = new GraphJobNode[getNumberOfRootSegments(parTableArr)];
		
		int rootSegmentsCounter = 0;
		
		// now create all (Persistence) source stages
		for (tblIdx = 0; tblIdx < parTableArr.length; tblIdx++) {
			curTable = parTableArr[tblIdx];

			// current IDOC type must match with first IDOC type
			// and there must be any columns to be processed
			if (stageIDOCTypeName.equals(curTable.getIDocType()) && 
			    jobType.doProcessTable(curTable.getTableType())  && 
			    curTable.getColumnData().length > 0) {
				// ************************
				// INPUT: Persistence stage
				// ************************
				newPersStageObj = StageFactory.createPersistenceSourceStage(jobDef, 
				                                                            curTable, 
				                                                            jobType.getPersistenceData(), 
				                                                            curTable.getSQLStatement(), 
				                                                            dsFactory);

				// update JobStructureData ...
				// ===========================
				//calculate source stage position
				xPos = BaseLayout.GRAPH_OBJECT_DEFAULT_DISTANCE_X;
				yPos = BaseLayout.GRAPH_OBJECT_DEFAULT_DISTANCE_Y * tblIdx;
				
				sourceNode = new GraphJobNode(newPersStageObj, xPos, yPos);
				_JobStructureData.getLayoutData().addSourceNode(sourceNode);

				// ******************
				// Transformer stage
				// ******************
				//calculate source transformer stage position
				xPos = 2 * BaseLayout.GRAPH_OBJECT_DEFAULT_DISTANCE_X;
				yPos = BaseLayout.GRAPH_OBJECT_DEFAULT_DISTANCE_Y * tblIdx;

				transformerParamMap = TransformerStageProperties.getDefaultStageParams();

				currentNewTransformerStage = StageFactory.createTransformerStage(jobDef, dsFactory, transformerParamMap);
				currentNewTransformerNode   = new GraphJobNode(currentNewTransformerStage, xPos, yPos);
				
				//add transformer to rootSegmentsTransformers array if current segment is a root sgment
				if (curTable.isRootSegment()) {
					newTransformerStagesOfRootSegments[rootSegmentsCounter] = currentNewTransformerStage;
					newTransformerNodesOfRootSegments[rootSegmentsCounter] = currentNewTransformerNode;
					rootSegmentsCounter++;
				}

				// ********************************************************
				// create link Persistence Stage <------> Transformer Stage
				// ********************************************************
				// create the column mapping (link)
				stageLink = createColumnMapping(newPersStageObj, currentNewTransformerStage, 
				                                curTable, newPersStageObj.getLinkParams(),
				                                dsFactory);

				// update JobStructureData ...
				// ===========================
				transformerLink = new GraphJobLink(stageLink, currentNewTransformerNode);
				sourceNode.addOutLink(transformerLink);
				
				// get a modified mapping for succeeding stages ...
				curTable = curTable.getCopyForSAPMapping();
				

				// ********************************************************
				// create link Transformer Stage <------> IDOC Load Stage
				// ********************************************************
				// -----------------------------------
				// set IDOC Load Stage Link properties
				// -----------------------------------
				linkParamMap = SAPIDocLoadStageProperties.getDefaultLinkParams();
				linkParamMap.put(SAPIDocLoadStageProperties.LNK_PROP_SEG_TYPE_KEY, curTable.getSegmentType());
				linkParamMap.put(SAPIDocLoadStageProperties.LNK_PROP_FKEYCOLS_KEY, curTable.getForeignKeyColumns());
				linkParamMap.put(SAPIDocLoadStageProperties.LNK_PROP_PKEYCOLS_KEY, curTable.getPrimaryKeyColumns());
				linkParamMap.put(SAPIDocLoadStageProperties.LNK_PROP_OBJECT_NAME_KEY, curTable.getName());
				linkParamMap.put(SAPIDocLoadStageProperties.LNK_PROP_SEG_NAME_KEY, curTable.getSegmentDefinition());
				linkParamMap.put(SAPIDocLoadStageProperties.LNK_PROP_CONNECTION_NAME_KEY, 
				                 jobType.getSAPSystem().getSAPSystemName());
				linkParamMap.put(SAPIDocLoadStageProperties.LNK_PROP_IDOC_TYPE_NAME_KEY, curTable.getIDocType());
				linkParamMap.put(SAPIDocLoadStageProperties.LNK_PROP_PKEY_COL_COUNT_KEY, 
				                 Integer.toString(curTable.getPrimaryKeyColumnCount()));
				linkParamMap.put(SAPIDocLoadStageProperties.LNK_PROP_FKEY_COL_COUNT_KEY, 
				                 Integer.toString(curTable.getForeignKeyColumnCount()));
			    // Locator and field map required for data lineage
			    linkParamMap.put(SAPIDocLoadStageProperties.LNK_PROP_PDR_LOCATOR_KEY, generatePDRLocator(jobType.getSAPSystem().getSAPSystemName(), stageIDOCTypeName, curTable, parTableArr));
			    linkParamMap.put(SAPIDocLoadStageProperties.LNK_PROP_PDR_FIELDMAP_KEY, generateFieldMap(jobType.getSAPSystem().getSAPSystemName(), stageIDOCTypeName, curTable, parTableArr));

			    // some ROOT Segment dependent link properties ...
				if (curTable.isRootSegment()) {
					linkParamMap.put(SAPIDocLoadStageProperties.LNK_PROP_PARENT_OBJECT_TYPE_KEY, "CControlRecord");
					linkParamMap.put(SAPIDocLoadStageProperties.LNK_PROP_PARENT_OBJECT_NAME_KEY, "CONTROL_RECORD");
				}
				else {
					linkParamMap.put(SAPIDocLoadStageProperties.LNK_PROP_PARENT_OBJECT_TYPE_KEY, "CSegment");
					linkParamMap.put(SAPIDocLoadStageProperties.LNK_PROP_PARENT_OBJECT_NAME_KEY, curTable.getParentSegment());
				}

		      // for V7 Stages ...
		      if (_JobRequestInfo.doCreateV7Stage()) {
		         // --> create a XML Property from the map
		         linkParamMap = SAPStageProperties.getXMLPropertyFromLinkMap(linkParamMap);
		      }
		      
            // create the column mapping (link)
				stageLink = createColumnMapping(currentNewTransformerStage, newIDOCStage, curTable, linkParamMap, dsFactory);

				// update JobStructureData ...
				// ===========================
				idocLink = new GraphJobLink(stageLink, idocNode);
				currentNewTransformerNode.addOutLink(idocLink);

//SKA not needed anymore here, move to some other place and/or do for all root segments
//has to be linked to all root segment input stages
//call createLoadStatus multiple times but create ILS transformer once and link it to all root segments
				
            // if IDOC Load Status Info creation is required ...

//SKA completely moved outside the loop, add ILS things later on
//TODO if (curTable.isRootSegment()) {// && createIDocLoadStatusStages) {
//	if (curTable.isRootSegment() && createIDocLoadStatusStages) {
//   createIDOCLoadStatusInfo(jobDef, curTable, newTransformerStage[tblIdx], transformerNode, 
//                            newIDOCStage, idocNode, dsFactory);
//   
//   // create the ILS stages only once
//   //SKA there have to be added links ect for all root segments so no check like this
//   //createIDOCLoadStatusInfo will take care the ILS transformer gets created only once, same for funnel + rem dup if there is more than one root seg
//   createIDocLoadStatusStages = false;
// } // end of if (curTable.isRootSegment() && createIDocLoadStatusStages)

			}
			else {
				if (curTable.getColumnData().length == 0) {
					if (TraceLogger.isTraceEnabled()) {
						TraceLogger.trace(TraceLogger.LEVEL_FINE, "Table '" + curTable.getName()
						                                        + "' has no colums to be processed.");
					}
				}
				else {
					if (stageIDOCTypeName.equals(curTable.getIDocType())) {
						if (TraceLogger.isTraceEnabled()) {
							TraceLogger.trace(TraceLogger.EVENT, "IDOC Type does not match with 'first' IDOC Type !!!!");
						}
					}
					else {
						if (TraceLogger.isTraceEnabled()) {
							TraceLogger.trace(TraceLogger.LEVEL_FINE, "Table '" + curTable.getName()
							                                        + "' is not a required table (filtered).");
						}
					}
				} // end of if (curTable.getColumnData().length == 0)
			} // end of (else) if (stageIDOCTypeName.equals(curTable.getIDocType()) && ... getColumnData().length > 0)
		} // end of for (tblIdx = 0; tblIdx < pTableArr.length; tblIdx++)
		
		//SKA ILS related things		
		if (createIDocLoadStatusStages) {
			createIDOCLoadStatusInfo(jobDef, getRootSegmentsTableData(parTableArr), newTransformerStagesOfRootSegments, newTransformerNodesOfRootSegments, 
				newIDOCStage, idocNode, dsFactory);
		}

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.exit();
		}
	} // end of createFlow()

	
	private void createIDOCLoadStatusInfo(DSJobDef parJobDef, TableData[] rootSegmentsTableDataArray,
	                                      StageData[] newTransformerStages,
	                                      GraphJobNode[] transformerNodes, StageData parIDOCTrgStage, 
	                                      GraphJobNode parIDOCJobTrgNode, DataStageObjectFactory parDSFactory) throws BaseException {
		RequestJobTypeIDocLoad jobType;
		GraphJobLink           transformerLink1;
		GraphJobLink           transformerLinkToFunnel1;
		GraphJobLink           funnelLinkToRemoveDuplicates1;
		GraphJobLink           removeDuplicatesToILSTransformerLink1;
		GraphJobLink           idocTrgLink;
		GraphJobLink           persTrgLink;
		GraphJobNode           extTransformerNode1;
		GraphJobNode           extFunnelNode;
		GraphJobNode           extRemoveDuplicatesNode;
		PersistenceData        tmpPersistData;
		TableData              internalTablesArr[];
		TableData              mappingFlowTransformer2ILSTransformer;
		TableData              mappingFlowTransformer2ILSFunnel;
		TableData			   mappingFlowILSFunnel2ILSRemoveDuplicates;
		TableData			   mappingRemoveDuplicates2ILSTransformer;
		TableData              mappingILSTransformer2IDocStage;
		TableData              mappingILSTransf2ODBCStageStatus;
        TableData              mappingILSTransf2ODBCStageRun;
		StageData              newILSTransformerStage1;
		StageData              newILSFunnelStage;
		StageData              newILSRemoveDuplicatesStage;
		StageData              newPersStage;
		ModelInfoBlock         modelInfoBlk;
		DSLink                 stageLink;
		InputStream            modelInpStream;
		String                 objectName;
		String                 curIDocTypeName;
        String                 curIDocSegDef; 
        String                 mappingModelName;
		Map                    extTableDataMap;
		ObjectParamMap         idocStageLinkParamMap;

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.entry();
		}

		// load the IDOCLoadStatus (physical) model ...
		jobType = (RequestJobTypeIDocLoad) getJobType();

      if (_JobRequestInfo.doCreateV7Stage()) {
         mappingModelName = IDOC_LOAD_STATUS_MAPPING_SCHEMA_MODEL_V7;
      }
      else {
         mappingModelName = IDOC_LOAD_STATUS_MAPPING_SCHEMA_MODEL_V65;
      }

		try {
			if (TraceLogger.isTraceEnabled()) {
				TraceLogger.trace(TraceLogger.LEVEL_FINEST, "Loading LoadStatus physical model: " + mappingModelName);
			}

         modelInpStream  = getClass().getClassLoader().getResourceAsStream(mappingModelName);
			modelInfoBlk    = TableData.loadDataModel(modelInpStream);
         extTableDataMap = modelInfoBlk.getTableMap();
			modelInpStream.close();
			
			// set the appropriate data type mapping
         getDSFactory().setDSTypeMapping(modelInfoBlk.getDatabaseId());
		} // end of try
		catch (IOException pIOExcpt) {
			if (TraceLogger.isTraceEnabled()) {
				TraceLogger.traceException(pIOExcpt);
			}

			extTableDataMap = null;
		} // end of catch(IOException pIOExcpt)

		if (extTableDataMap == null || extTableDataMap.size() == 0) {
			throw new JobGeneratorException("126400E", new String[] {  mappingModelName });
		}

		// check if there is the required number of tables exist in the model
		internalTablesArr = (TableData[]) extTableDataMap.get(SupportedTableTypesMap.TABLE_TYPE_ENUM_IDOC_LOAD_STATUS);

		if (internalTablesArr.length != LOAD_STATUS_TABLE_COUNT) {
			throw new JobGeneratorException("126500E", new String[] { String.valueOf(LOAD_STATUS_TABLE_COUNT), 
			                                                          String.valueOf(internalTablesArr.length)
			                                                        });
		} // end of if (internalTablesArr.length != LOAD_STATUS_TABLE_COUNT)
		
		curIDocTypeName = rootSegmentsTableDataArray[0].getIDocType();
		curIDocSegDef   = rootSegmentsTableDataArray[0].getSegmentDefinition();
 		      
      mappingFlowTransformer2ILSTransformer 	= internalTablesArr[0];
      mappingILSTransformer2IDocStage       	= internalTablesArr[1];
      mappingILSTransf2ODBCStageStatus      	= internalTablesArr[2];
      mappingILSTransf2ODBCStageRun         	= internalTablesArr[3];
      mappingFlowTransformer2ILSFunnel			= internalTablesArr[4];
      mappingFlowILSFunnel2ILSRemoveDuplicates	= internalTablesArr[5];
      mappingRemoveDuplicates2ILSTransformer    = internalTablesArr[6];
		
		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.trace(TraceLogger.LEVEL_FINEST, "Replacing template names with '" + curIDocTypeName + "'.");
		}
		
// update current IDOC type (primary and foreign keys)
		TableData tmpWorkTDArr[] = new TableData[] { mappingFlowTransformer2ILSTransformer }; 
      updateModelsVars(tmpWorkTDArr, curIDocSegDef,  Constants.IDOC_PRIMARY_KEY_TEMPLATE, 
                       rootSegmentsTableDataArray[0]);
      updateModelsVars(tmpWorkTDArr, curIDocTypeName, Constants.IDOC_PRIMARY_KEY_ROOT_TEMPLATE, 
                       rootSegmentsTableDataArray[0]);
		updateModelsVars(tmpWorkTDArr, curIDocTypeName, Constants.IDOC_FOREIGN_KEY_ROOT_TEMPLATE, 
		                 rootSegmentsTableDataArray[0]);

		// update current IDOC segment definition (primary)
		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.trace(TraceLogger.LEVEL_FINEST, "Replacing template names with '" + curIDocSegDef + "'.");
		}
      tmpWorkTDArr = new TableData[] { mappingILSTransformer2IDocStage, mappingILSTransf2ODBCStageStatus, 
                                       mappingILSTransf2ODBCStageRun }; 
      updateModelsVars(tmpWorkTDArr, curIDocSegDef,  Constants.IDOC_PRIMARY_KEY_TEMPLATE, rootSegmentsTableDataArray[0]);
      updateModelsVars(tmpWorkTDArr, curIDocTypeName, Constants.IDOC_PRIMARY_KEY_ROOT_TEMPLATE, rootSegmentsTableDataArray[0]);
		updateModelsVars(tmpWorkTDArr, rootSegmentsTableDataArray[0].getPhysicalName(), 
		                 TableData.TABLE_NAME_TEMPLATE, null);
		updateModelsVars(tmpWorkTDArr, jobType.getSAPMessageType(), TableData.MSG_TYPE_TEMPLATE, null);

		// set the 'Object Name' ==> use IDOC Type name if not available
		objectName = jobType.getILSVendorName();
		if (objectName == null) {
			objectName = addQuotationMarks(curIDocTypeName);
		} // end of if (objectName == null)
		updateModelsVars(tmpWorkTDArr, objectName, TableData.OBJECT_NAME_TEMPLATE, null);
		
		
		// *********************
		// ILS Transformer stage
		// *********************
		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.trace(TraceLogger.LEVEL_FINEST, "Creating Load Status transformer stage.");
		}
		
		//add the surrogate key file to the transformer
		ObjectParamMap ilsTransformerParamMap = TransformerStageProperties.getDefaultStageParams();
		ilsTransformerParamMap.put(TransformerStageProperties.PROP_SURROGATE_KEY_SOURCE_TYPE_KEY,
		                        TransformerStageProperties.PROP_SURROGATE_KEY_SOURCE_TYPE_FILE);
		ilsTransformerParamMap.put(TransformerStageProperties.PROP_SURROGATE_KEY_STATE_FILE_KEY, 
		                        jobType.getILSSurrogateKeyFile());
		
		newILSTransformerStage1 = StageFactory.createTransformerStage(parJobDef, parDSFactory, ilsTransformerParamMap);
		newILSTransformerStage1.setName(ILS_TRANSFORMER_NAME);
		SAPStageProperties.setSequentialExecutionMode((DSStage) newILSTransformerStage1.getJobComponent(), parDSFactory);
		
		//calculate ils transformer stage position
		int xPos;
		int yPos = (BaseLayout.GRAPH_OBJECT_DEFAULT_DISTANCE_Y * (_SrcTableDataArr.length - 1) / 2) - BaseLayout.GRAPH_OBJECT_DEFAULT_DISTANCE_Y;

			if (rootSegmentsTableDataArray.length == 1) { //for ILS jobs with one root segment
				xPos = 3 * BaseLayout.GRAPH_OBJECT_DEFAULT_DISTANCE_X;
			} else { //for ILS jobs with more than one root segment
				xPos = 4 * BaseLayout.GRAPH_OBJECT_DEFAULT_DISTANCE_X;
			}
		
		extTransformerNode1 = new GraphJobNode(newILSTransformerStage1, xPos, yPos);
		
		//there has to be at least 1 root segment
		if (rootSegmentsTableDataArray.length == 1) {
			//one root segment
			// *****************************************************************************
			// create link Transformer Stage <------> IDOC LOAD STATUS Transformer Stage
			// *****************************************************************************
			if (TraceLogger.isTraceEnabled()) {
				TraceLogger.trace(TraceLogger.LEVEL_FINEST, "Creating transformer-ILS Transformer stage link.");
			}

			// create the column mapping (link)
			stageLink = createColumnMapping(newTransformerStages[0], newILSTransformerStage1, mappingFlowTransformer2ILSTransformer, 
			                                null, parDSFactory);

			// update JobStructureData ...
			// ===========================
			transformerLink1 = new GraphJobLink(stageLink, extTransformerNode1);
			transformerNodes[0].addOutLink(transformerLink1);
	

		}	else {
			
			//more than one root segment
			// ******************
			// ILS Funnel stage
			// ******************
			if (TraceLogger.isTraceEnabled()) {
				TraceLogger.trace(TraceLogger.LEVEL_FINEST, "Creating Load Status funnel stage.");
			}
			newILSFunnelStage = StageFactory.createFunnelStage(parJobDef, parDSFactory, null);
			newILSFunnelStage.setName(ILS_FUNNEL_NAME);

			//calculate ils funnel stage position
			xPos = 3 * BaseLayout.GRAPH_OBJECT_DEFAULT_DISTANCE_X;
			yPos = (BaseLayout.GRAPH_OBJECT_DEFAULT_DISTANCE_Y * (_SrcTableDataArr.length - 1) / 2) + BaseLayout.GRAPH_OBJECT_DEFAULT_DISTANCE_Y;

			extFunnelNode = new GraphJobNode(newILSFunnelStage, xPos, yPos);
		
			// ***************************
			// ILS remove duplicates stage
			// ***************************
			if (TraceLogger.isTraceEnabled()) {
				TraceLogger.trace(TraceLogger.LEVEL_FINEST, "Creating Load Status remove duplicates stage.");
			}
			newILSRemoveDuplicatesStage = StageFactory.createRemoveDuplicatesStage(parJobDef, parDSFactory, null);
			newILSRemoveDuplicatesStage.setName(ILS_REMOVE_DUPLICATES_NAME);
			
			//calculate ils Remove duplicates stage position
			xPos = 4 * BaseLayout.GRAPH_OBJECT_DEFAULT_DISTANCE_X;
			yPos = (BaseLayout.GRAPH_OBJECT_DEFAULT_DISTANCE_Y * (_SrcTableDataArr.length - 1) / 2) + BaseLayout.GRAPH_OBJECT_DEFAULT_DISTANCE_Y;
			
			extRemoveDuplicatesNode = new GraphJobNode(newILSRemoveDuplicatesStage, xPos, yPos);
			// *******************************************************************************************
			// create link IDOC LOAD STATUS Funnel Stage <------> IDOC LOAD STATUS Remove Duplicates Stage
			// *******************************************************************************************
			if (TraceLogger.isTraceEnabled()) {
				TraceLogger.trace(TraceLogger.LEVEL_FINEST, "Creating Funnel stage to remove duplicates stage link.");
			}
			// create the column mapping (link)
			stageLink = createColumnMapping(newILSFunnelStage, newILSRemoveDuplicatesStage, mappingFlowILSFunnel2ILSRemoveDuplicates, 
			                                null, parDSFactory);

			SAPStageProperties.setSequentialExecutionMode((DSStage) newILSRemoveDuplicatesStage.getJobComponent(), parDSFactory);
			
			// update JobStructureData ...
			// ===========================
			funnelLinkToRemoveDuplicates1 = new GraphJobLink(stageLink, extRemoveDuplicatesNode);
			extFunnelNode.addOutLink(funnelLinkToRemoveDuplicates1);
			
			// *****************************************************************************
			// create link Remove Duplicates Stage <------> IDOC LOAD STATUS Transformer Stage
			// *****************************************************************************
			if (TraceLogger.isTraceEnabled()) {
				TraceLogger.trace(TraceLogger.LEVEL_FINEST, "Creating transformer-ILS Transformer stage link.");
			}

			// create the column mapping (link)
			stageLink = createColumnMapping(newILSRemoveDuplicatesStage, newILSTransformerStage1, mappingRemoveDuplicates2ILSTransformer, 
			                                null, parDSFactory);

			// update JobStructureData ...
			// ===========================
			removeDuplicatesToILSTransformerLink1 = new GraphJobLink(stageLink, extTransformerNode1);
			extRemoveDuplicatesNode.addOutLink(removeDuplicatesToILSTransformerLink1);
			
			
			
//TODO SKA create links from all input transformers of root segments to funnel	
			
			for (int currentTransformerNumber=0; currentTransformerNumber < rootSegmentsTableDataArray.length; currentTransformerNumber++) {
				// *****************************************************************************
				// create link Transformer Stages <------> IDOC LOAD STATUS Funnel Stage
				// *****************************************************************************
				if (TraceLogger.isTraceEnabled()) {
					TraceLogger.trace(TraceLogger.LEVEL_FINEST, "Creating transformer-ILS to Funnel stage link.");
				}
				// create the column mapping (link)
				stageLink = createColumnMapping(newTransformerStages[currentTransformerNumber], newILSFunnelStage, mappingFlowTransformer2ILSFunnel, 
				                                null, parDSFactory);
				
	
				// update JobStructureData ...
				// ===========================
				transformerLinkToFunnel1 = new GraphJobLink(stageLink, extFunnelNode);
				transformerNodes[currentTransformerNumber].addOutLink(transformerLinkToFunnel1);
			}			
		}
		
		

		
		// **************************************************************************
		// create link Transformer StageX <------> IDOC Load Stage (CONTROL RECORD)
		// **************************************************************************
		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.trace(TraceLogger.LEVEL_FINEST, "Creating transformer-IDOC stage link.");
		}

		// ------------------------------------------------------------
		// set IDOC Load Stage Link properties (for the Control Record)
		// ------------------------------------------------------------
		idocStageLinkParamMap = SAPIDocLoadStageProperties.getDefaultLinkParams();
		idocStageLinkParamMap.put(SAPIDocLoadStageProperties.LNK_PROP_SEG_TYPE_KEY, mappingILSTransformer2IDocStage.getSegmentType());
		idocStageLinkParamMap.put(SAPIDocLoadStageProperties.LNK_PROP_FKEYCOLS_KEY, "");
		idocStageLinkParamMap.put(SAPIDocLoadStageProperties.LNK_PROP_PKEYCOLS_KEY, 
		                          getPrimKeyFromIDocType(curIDocTypeName, rootSegmentsTableDataArray[0]));
		idocStageLinkParamMap.put(SAPIDocLoadStageProperties.LNK_PROP_PKEY_COL_COUNT_KEY, "1");
		idocStageLinkParamMap.put(SAPIDocLoadStageProperties.LNK_PROP_OBJECT_TYPE_KEY, "CControlRecord");
		idocStageLinkParamMap.put(SAPIDocLoadStageProperties.LNK_PROP_RECORD_TYPE_KEY, "CONTROL");

      // for V7 Stages ...
      if (_JobRequestInfo.doCreateV7Stage()) {
         // --> create a XML Property from the map
      	idocStageLinkParamMap = SAPStageProperties.getXMLPropertyFromLinkMap(idocStageLinkParamMap);
      }

		// create the column mapping (link)
		stageLink = createColumnMapping(newILSTransformerStage1, parIDOCTrgStage, 
		                                mappingILSTransformer2IDocStage, idocStageLinkParamMap,
		                                parDSFactory);

		// update JobStructureData ...
		// ===========================
		idocTrgLink = new GraphJobLink(stageLink, parIDOCJobTrgNode);
		extTransformerNode1.addOutLink(idocTrgLink);

		// ***************************
		// OUTPUT: Persistence stage 1
		// ***************************
		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.trace(TraceLogger.LEVEL_FINEST, "Creating Persistence stage 'RECORD STATUS'.");
		}

		// update WRITE MODE (ODBC Stages only)
		if (jobType.getPersistenceData() instanceof ODBCStageData) {
			ODBCStageData tmpODBCData = new ODBCStageData((ODBCStageData) jobType.getPersistenceData());
			tmpODBCData.setWriteMode(ODBCStageData.WRITE_MODE_INSERT_THEN_UPDATE);
			tmpODBCData.setAlternateSchemaName(null);  // use original schema name
			tmpODBCData.setArraySize(null);
			tmpODBCData.setRecordCount(null);

			tmpPersistData = tmpODBCData;
		}
		else {
			tmpPersistData = jobType.getPersistenceData();
		} // end of (else) if (jobType.getPersistenceData() instanceof ODBCStageData)

		newPersStage = StageFactory.createPersistenceTargetStage(parJobDef, mappingILSTransf2ODBCStageStatus, 
		                                                         tmpPersistData, getServiceToken());

		// ************************************************************************
		// create link Transformer Stage <------> Persistence Stage (IDOC DATA)
		// ************************************************************************
		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.trace(TraceLogger.LEVEL_FINEST, "Creating transformer - 'IDOC DATA' Persistence stage link.");
		}
		stageLink = createColumnMapping(newILSTransformerStage1, newPersStage, 
		                                mappingILSTransf2ODBCStageStatus, newPersStage.getLinkParams(),
		                                parDSFactory);

		// update JobStructureData ...
		// ===========================
		//calculate ils newPersStage stage position
		xPos = 5 * BaseLayout.GRAPH_OBJECT_DEFAULT_DISTANCE_X;
		yPos = (BaseLayout.GRAPH_OBJECT_DEFAULT_DISTANCE_Y * (_SrcTableDataArr.length - 1) / 2) - BaseLayout.GRAPH_OBJECT_DEFAULT_DISTANCE_Y;

		persTrgLink = new GraphJobLink(stageLink, new GraphJobNode(newPersStage, xPos, yPos));
		extTransformerNode1.addOutLink(persTrgLink);

		// ***************************
		// OUTPUT: Persistence stage 2
		// ***************************
		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.trace(TraceLogger.LEVEL_FINEST, "Creating Persistence stage 'JOB RUN DATA'.");
		}

		if (jobType.getPersistenceData() instanceof ODBCStageData) {
			ODBCStageData tmpODBCData = new ODBCStageData((ODBCStageData) jobType.getPersistenceData());
			tmpODBCData.setWriteMode(ODBCStageData.WRITE_MODE_INSERT_THEN_UPDATE);
			tmpODBCData.setAlternateSchemaName(null);  // use original schema name
			tmpODBCData.setArraySize(null);
			tmpODBCData.setRecordCount(null);

			tmpPersistData = tmpODBCData;
		}
		else {
			tmpPersistData = jobType.getPersistenceData();
		} // end of (else) if (jobType.getPersistenceData() instanceof ODBCStageData)

		newPersStage = StageFactory.createPersistenceTargetStage(parJobDef, mappingILSTransf2ODBCStageRun, 
		                                                         tmpPersistData, getServiceToken());

		// ***************************************************************************
		// create link Transformer Stage <------> Persistence Stage (JOB RUN DATA)
		// ***************************************************************************
		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.trace(TraceLogger.LEVEL_FINEST, "Creating transformer - 'JOB RUN DATA' Persistence stage link.");
		}

		// create a link with filter constraint if available
		if (mappingILSTransf2ODBCStageRun.getFilterConstraint() != null) {
			newPersStage.getLinkParams().put(TransformerStageProperties.PROP_REJECT_KEY, "1",
			                                 DataStageObjectFactory.LINK_TYPE_OUTPUT); // otherwise
		}
		stageLink = createColumnMapping(newILSTransformerStage1, newPersStage, mappingILSTransf2ODBCStageRun, 
		                                newPersStage.getLinkParams(), parDSFactory);

		// update JobStructureData ...
		// ===========================
		//calculate 2nd ils newPersStage stage position
		xPos = 5 * BaseLayout.GRAPH_OBJECT_DEFAULT_DISTANCE_X;
		yPos = (BaseLayout.GRAPH_OBJECT_DEFAULT_DISTANCE_Y * (_SrcTableDataArr.length - 1) / 2) + BaseLayout.GRAPH_OBJECT_DEFAULT_DISTANCE_Y;

		persTrgLink = new GraphJobLink(stageLink, new GraphJobNode(newPersStage, xPos, yPos));
		extTransformerNode1.addOutLink(persTrgLink);


		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.exit();
		}
	} // end of createIDOCLoadStatusInfo()

	
	private String getPrimKeyFromIDocType(String parIDocType, TableData parSegmentMapping) {
	   String retPrimKey;
	   
	   // different handling for V7 stages
	   if (_JobRequestInfo.doCreateV7Stage()) {
	      retPrimKey = StringUtils.cleanFieldName(Constants.IDOC_TECH_FLD_ADM_DOCNUM_NAME);
	   }
	   else {
	      retPrimKey = MessageFormat.format(Constants.IDOC_PRIMARY_KEY_ROOT_TEMPLATE, 
            										 new Object[] { parIDocType });
	      retPrimKey = StringUtils.cleanFieldName(retPrimKey);
	      retPrimKey = TableData.getSrcMappingForDerivation(retPrimKey, parSegmentMapping);
	   }

	   return(retPrimKey);
	} // end of getPrimKeyFromIDocType()

	
   private String searchMappingColumnName(ColumnData columnArr[], String searchName) {
      ColumnData curColumn;
      String     retMappingColName;
      int        colIdx;

      if (TraceLogger.isTraceEnabled()) {
         TraceLogger.entry("search name = " + searchName);
      }
      
      curColumn         = null;
      retMappingColName = null;
      colIdx            = 0;
      while(colIdx < columnArr.length && retMappingColName == null) {
         
         curColumn = columnArr[colIdx];
         if (curColumn.getName().equals(searchName)) {
            retMappingColName = curColumn.getTransformerSrcMapping();
         }
         
         colIdx++; 
      } // end of while(colIdx < columnArr.length && retMappingColName == null)
      
      if (TraceLogger.isTraceEnabled()) {
         String traceText;
         
         if (retMappingColName == null) {
            traceText = "No dedicated mapping found";
         }
         else {
            traceText = "Found mapping = '" + retMappingColName + "' on colum = " + curColumn.getName();
         }
         TraceLogger.exit(traceText);
      }
      
      return(retMappingColName);
   } // end of searchMappingColumnName()

   
	protected void validate() throws BaseException {
		RequestJobTypeIDocLoad jtIDocLoad;
		String vendorName;

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.entry();
		}

		jtIDocLoad = (RequestJobTypeIDocLoad) getJobType();

		// load all tables to be processed
		_SrcTableDataArr = loadRequiredTables(jtIDocLoad.getPhysicalModelId());

		// check if Vendor Name is a (string) constant or a Job Parameter
		vendorName = _JobRequestInfo.checkForConstantOrJobParam(jtIDocLoad.getILSVendorName());

		// replace the return string in the JobType data
		jtIDocLoad.setIDOCLoadStatus(jtIDocLoad.getILSSurrogateKeyFile(), vendorName);

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.exit();
		}
	} // end of validate()
	

	private void updateModelsVars(TableData tablesArr[], String varName, String keyTemplate, 
	                              TableData segmentMapping) {
		ColumnData colArr[];
		ColumnData curColumn;
		String     newValue;
		String     srcMapping;
		String     replaceValue;
		int        colIdx;
		int        tblIdx;

		// generate the IDOC key
		if (keyTemplate.equals(TableData.TABLE_NAME_TEMPLATE) || keyTemplate.equals(TableData.MSG_TYPE_TEMPLATE)) {
			newValue = addQuotationMarks(varName);
		}
		else {
			if (keyTemplate.equals(TableData.OBJECT_NAME_TEMPLATE)) {
				// take it as it is
				newValue = varName;
			}
			else {
            newValue = MessageFormat.format(keyTemplate, new Object[] { varName });

            // different handling for the key columns
            if (segmentMapping != null) {
               if (keyTemplate.startsWith(Constants.IDOC_PRIMARY_KEY_TEMPLATE)       || 
                   keyTemplate.startsWith(Constants.IDOC_PRIMARY_KEY_ROOT_TEMPLATE)  ||
                   keyTemplate.startsWith(Constants.IDOC_FOREIGN_KEY_ROOT_TEMPLATE)  ||
                   keyTemplate.startsWith(Constants.IDOC_FOREIGN_KEY_SUB_TEMPLATE)) {

                  // cleaning column name if it is created from a key template
                  String oldValue = newValue;
                  newValue = StringUtils.cleanFieldName(oldValue);
                  newValue = TableData.getSrcMappingForDerivation(newValue, segmentMapping);

                  if (TraceLogger.isTraceEnabled()) {
                     if (!oldValue.equals(newValue)) {
                        TraceLogger.trace(TraceLogger.LEVEL_FINEST, "Converting key column '" + oldValue + "' to '" + newValue + "'");
                     }
                  }
               }
            } // end of if (segmentMapping != null)
			} // end of if (keyTemplate.equals(TableData.OBJECT_NAME_TEMPLATE))
		} // end of if (keyTemplate.equals(... (TableData.MSG_TYPE_TEMPLATE))

		// process all tables ...
		for (tblIdx = 0; tblIdx < tablesArr.length; tblIdx++) {
			colArr = tablesArr[tblIdx].getColumnData();

			for (colIdx = 0; colIdx < colArr.length; colIdx++) {
				curColumn = colArr[colIdx];

				if (curColumn.getName().equals(keyTemplate)) {
				   curColumn.setName(newValue);
				}

				srcMapping = curColumn.getTransformerSrcMapping();
				if (srcMapping != null) {

					if (srcMapping.indexOf(keyTemplate) > -1) {
					   
	               if (segmentMapping != null) {
	                  String sapColumnName = searchMappingColumnName(segmentMapping.getColumnData(), newValue);
	                  if (sapColumnName != null) {
	                     newValue = sapColumnName;
	                  }
	               }

						// replace the complete mapping string or just a part of this ???
						if (srcMapping.equals(keyTemplate)) {
							// complete
							curColumn.setTransformerSrcMapping(newValue);
						}
						else {
							// part of
							replaceValue = StringUtils.replaceString(srcMapping, keyTemplate, newValue);
							curColumn.setTransformerSrcMapping(replaceValue);
						}
					}
				} // end of if (srcMapping != null)
			} // end of for (colIdx = 0; colIdx < colArr.length; colIdx ++)
		} // end of for (tblIdx = 0; tblIdx < tablesArr.length; tblIdx ++)
	} // end of updateModelsVars()

} // end of class IDOCLoadJob
