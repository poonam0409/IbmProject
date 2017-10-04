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
// Module Name : com.ibm.is.sappack.gen.server.jobgenerator
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************

package com.ibm.is.sappack.gen.server.jobgenerator;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import DataStageX.DSJobDef;
import DataStageX.DSLink;

import com.ibm.is.sappack.gen.common.BaseException;
import com.ibm.is.sappack.gen.common.Constants;
import com.ibm.is.sappack.gen.common.JobGeneratorException;
import com.ibm.is.sappack.gen.common.request.JobGeneratorRequest;
import com.ibm.is.sappack.gen.common.request.RequestJobTypeIDocExtract;
import com.ibm.is.sappack.gen.common.trace.TraceLogger;
import com.ibm.is.sappack.gen.server.common.service.ServiceToken;
import com.ibm.is.sappack.gen.server.common.util.DSAccessException;
import com.ibm.is.sappack.gen.server.datastage.DataStageObjectFactory;
import com.ibm.is.sappack.gen.server.jobgenerator.stages.SAPIDocExtractStageProperties;
import com.ibm.is.sappack.gen.server.jobgenerator.stages.SAPStageProperties;
import com.ibm.is.sappack.gen.server.jobgenerator.stages.StageFactory;
import com.ibm.is.sappack.gen.server.util.GraphJobLink;
import com.ibm.is.sappack.gen.server.util.GraphJobNode;
import com.ibm.is.sappack.gen.server.util.ModelInfoBlock;
import com.ibm.is.sappack.gen.server.util.ObjectParamMap;
import com.ibm.is.sappack.gen.server.util.StageData;
import com.ibm.is.sappack.gen.server.util.TableData;


public class IDocExtractJob extends BaseIDocJob {
	// -------------------------------------------------------------------------------------
	// Constants
	// -------------------------------------------------------------------------------------

	// -------------------------------------------------------------------------------------
	// Member Variables
	// -------------------------------------------------------------------------------------

	static String copyright() {
		return com.ibm.is.sappack.gen.server.jobgenerator.Copyright.IBM_COPYRIGHT_SHORT;
	}

	public IDocExtractJob(ServiceToken parSvcToken, RequestJobTypeIDocExtract parJobType,
	                      JobGeneratorRequest parJobReqInfo, Map<String, ModelInfoBlock> physModelID2TableMap) throws BaseException {
		super(parSvcToken, parJobType, parJobReqInfo, physModelID2TableMap);
	} // end of IDOCExtractJob()

	public List<String> create() throws BaseException {
		TableData      trgTableDataArr[];
		List<String>   retJobsCreated;

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.entry();
		}

		retJobsCreated = new ArrayList<String>();

		// load the required tables
		trgTableDataArr = loadRequiredTables(((RequestJobTypeIDocExtract) getJobType()).getPhysicalModelId());

		// ---------------------------
		// ==> process existing tables
		// ---------------------------
		if (trgTableDataArr != null && trgTableDataArr.length > 0) {

			// we have to store the event of job creation failure
			boolean jobCreationError = false;

			try {
				// create a DS task container
				String curJobName = getJobType().getJobName();
				createJobDef(curJobName);

				// job gets added to the list of (successfully) created jobs
				// though job creation actually takes place later
				retJobsCreated.add(curJobName);

				// create the flow
				createFlow(trgTableDataArr, trgTableDataArr);

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
		} // end of if (trgTableDataArr != null && _TrgTableDataArr.length >= 0)

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.exit("Number of created jobs: " + retJobsCreated.size());
		}

		return (retJobsCreated);
	} // end of create()

	
	protected void createFlow(TableData parSrcTableArr[], TableData parTrgTableArr[]) throws BaseException {
		RequestJobTypeIDocExtract jobType;
		GraphJobNode              sourceNode;
		TableData                 curSrcTable;
		TableData                 curTrgTable;
		StageData                 newIDOCStage;
		DSJobDef                  jobDef;
		String                    stageIDOCTypeName;
		boolean                   isExtendedIDocType;
		int                       tblIdx;

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.entry("SAP IDoc Extract tables to be processed: " + parSrcTableArr.length);
		}

		// number of source and target tables must be equal ...
		if (parSrcTableArr.length != parTrgTableArr.length) {
			if (TraceLogger.isTraceEnabled()) {
				TraceLogger.trace(TraceLogger.LEVEL_FINE, "Number of source and target tables are not equal");
			}
			throw new JobGeneratorException("126300E", Constants.NO_PARAMS);
		} // end of if (parSrcTableArr.length != parTrgTableArr.length)

		jobDef             = _JobStructureData.getJobDef();
		curSrcTable        = parSrcTableArr[0];
		stageIDOCTypeName  = curSrcTable.getIDocType();
		isExtendedIDocType = curSrcTable.getIsExtendedIDocType();
		jobType            = (RequestJobTypeIDocExtract) getJobType();

		// *****************************
		// INPUT: SAP IDOC Extract Stage
		// *****************************
		newIDOCStage = StageFactory.createIDOCExtractStage(jobDef, stageIDOCTypeName, isExtendedIDocType,
		                                                   curSrcTable.getIDocBasicType(),
		                                                   curSrcTable.getSegmentType(), 
		                                                   getJobType().getJobName(), jobType, 
		                                                   curSrcTable.isUnicodeSystem(),
		                                                   _JobRequestInfo.doCreateV7Stage(),
		                                                   getServiceToken());

		// update JobStructureData ...
		// ===========================
		sourceNode = new GraphJobNode(newIDOCStage);
		_JobStructureData.getLayoutData().addSourceNode(sourceNode);

		for (tblIdx = 0; tblIdx < parSrcTableArr.length; tblIdx++) {
			curSrcTable = parSrcTableArr[tblIdx];
			curTrgTable = parTrgTableArr[tblIdx];

			// current IDOC type must match with first IDOC type
			// and there must be any columns to be processed
			if (stageIDOCTypeName.equals(curSrcTable.getIDocType()) && 
			    jobType.doProcessTable(curSrcTable.getTableType())  && 
			    curSrcTable.getColumnData().length > 0) {

			   // create a 'Single Extract Flow' with IDocStage ---> Transformer ---> PersistenceStage
			   // ------------------------------------------------------------------------------------
			   curSrcTable = curSrcTable.getCopyForSAPMapping();
			   createSingleExtractFlow(curSrcTable, curTrgTable, stageIDOCTypeName, 
			                           jobType, newIDOCStage, sourceNode, parSrcTableArr);
			}
			else {
				if (curSrcTable.getColumnData().length == 0) {
					if (TraceLogger.isTraceEnabled()) {
						TraceLogger.trace(TraceLogger.LEVEL_FINE, "Table '" + curSrcTable.getName()
						                                        + "' has no colums to be processed.");
					}
				}
				else {
					if (!jobType.doProcessTable(curSrcTable.getTableType())) {
						if (TraceLogger.isTraceEnabled()) {
							TraceLogger.trace(TraceLogger.LEVEL_FINE, "Table '" + curSrcTable.getName()
							                                        + "' is not a required table.");
						}
					}
					else {
						if (TraceLogger.isTraceEnabled()) {
							TraceLogger.trace(TraceLogger.EVENT, "IDOC Type does not match with 'first' IDOC Type !!!!");
						}
					}
				} // end of if (pTable.getColumnData().length > 0)
			} // end of (else) if (stageIDOCTypeName.equals(curTable.getIDocType()) ... curTable.getColumnData().length >
		} // end of for (tblIdx = 0; tblIdx < pTableArr.length; tblIdx++)

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.exit();
		}
	} // end of createFlow)


   protected void createSingleExtractFlow(TableData curSrcTable, TableData curTrgTable, 
                                          String stageIDOCTypeName, 
                                          RequestJobTypeIDocExtract jobType,
                                          StageData idocStage,
                                          GraphJobNode idocGraphNode, TableData[] srcTableArr) 
             throws BaseException { 
      
      GraphJobNode              transformerNode;
      GraphJobLink              transformerLink;
      GraphJobLink              odbcLink;
      StageData                 newPersStage;
      StageData                 newTransformerStage;
      DataStageObjectFactory    dsFactory;
      DSJobDef                  jobDef;
      DSLink                    stageLink;
      ObjectParamMap            linkParamMap;

      if (TraceLogger.isTraceEnabled()) {
         TraceLogger.entry("table = " + curSrcTable.getName()); //$NON-NLS-1$
      }

      dsFactory = getDSFactory();
      jobDef    = _JobStructureData.getJobDef();

      // ******************
      // Transformer stage
      // ******************
      newTransformerStage = StageFactory.createTransformerStage(jobDef, dsFactory, null);
      transformerNode     = new GraphJobNode(newTransformerStage);

      // *************************************************************
      // create links IDOC Extract Stage <------> Transformer Stages
      // *************************************************************
      // --------------------------------------
      // set IDOC Extract Stage Link properties
      // --------------------------------------
      linkParamMap = SAPIDocExtractStageProperties.getDefaultLinkParams();
      linkParamMap.put(SAPIDocExtractStageProperties.LNK_PROP_SEG_TYPE_KEY, curSrcTable.getSegmentType());
      linkParamMap.put(SAPIDocExtractStageProperties.LNK_PROP_OBJECT_NAME_KEY, curSrcTable.getName());
      linkParamMap.put(SAPIDocExtractStageProperties.LNK_PROP_SEG_NAME_KEY, curSrcTable.getSegmentDefinition());
      linkParamMap.put(SAPIDocExtractStageProperties.LNK_PROP_CONNECTION_NAME_KEY, 
                       jobType.getSAPSystem().getSAPSystemName());
      linkParamMap.put(SAPIDocExtractStageProperties.LNK_PROP_IDOC_TYPE_NAME_KEY, stageIDOCTypeName);
      // Locator and field map required for data lineage
      linkParamMap.put(SAPIDocExtractStageProperties.LNK_PROP_PDR_LOCATOR_KEY, generatePDRLocator(jobType.getSAPSystem().getSAPSystemName(), stageIDOCTypeName, curSrcTable, srcTableArr));
      linkParamMap.put(SAPIDocExtractStageProperties.LNK_PROP_PDR_FIELDMAP_KEY, generateFieldMap(jobType.getSAPSystem().getSAPSystemName(), stageIDOCTypeName, curSrcTable, srcTableArr));
      
      // for V7 Stages ...
      if (_JobRequestInfo.doCreateV7Stage()) {
         // --> create a XMP Property from the map
         linkParamMap = SAPStageProperties.getXMLPropertyFromLinkMap(linkParamMap);
      }
      
      // and then create the column mapping (link)
      stageLink = createColumnMapping(idocStage, newTransformerStage, curSrcTable, 
                                      linkParamMap, dsFactory);

      // update JobStructureData ...
      // ===========================
      transformerLink = new GraphJobLink(stageLink, transformerNode);
      idocGraphNode.addOutLink(transformerLink);

      // ******************
      // OUTPUT: ODBC stage
      // ******************
      newPersStage = StageFactory.createPersistenceTargetStage(jobDef, curTrgTable, 
                                                               jobType.getPersistenceData(), 
                                                               getServiceToken());

      // ************************************************************
      // create link Transformer Stage <------> Persistence Stage
      // ************************************************************
      stageLink = createColumnMapping(newTransformerStage, newPersStage, curTrgTable, 
                                      newPersStage.getLinkParams(), dsFactory);

      // update JobStructureData ...
      // ===========================
      odbcLink = new GraphJobLink(stageLink, new GraphJobNode(newPersStage));
      transformerNode.addOutLink(odbcLink);

      if (TraceLogger.isTraceEnabled()) {
         TraceLogger.exit();
      }
   } // end of createSingleExtractFlow()


	protected void validate() throws JobGeneratorException {
		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.entry();
		}

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.exit();
		}
	} // end of validate()

} // end of class IDOCExtractJob
