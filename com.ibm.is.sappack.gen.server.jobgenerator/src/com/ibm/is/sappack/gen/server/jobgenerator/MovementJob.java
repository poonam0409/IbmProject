//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2011, 2013                                              
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ASCLModel.LinkTypeEnum;
import DataStageX.DSJobDef;
import DataStageX.DSLink;

import com.ibm.is.sappack.gen.common.BaseException;
import com.ibm.is.sappack.gen.common.Constants;
import com.ibm.is.sappack.gen.common.FileStageData;
import com.ibm.is.sappack.gen.common.JobGeneratorException;
import com.ibm.is.sappack.gen.common.ODBCStageData;
import com.ibm.is.sappack.gen.common.PersistenceData;
import com.ibm.is.sappack.gen.common.request.JobGeneratorRequest;
import com.ibm.is.sappack.gen.common.request.RequestJobTypeMovement;
import com.ibm.is.sappack.gen.common.trace.TraceLogger;
import com.ibm.is.sappack.gen.server.common.service.ServiceToken;
import com.ibm.is.sappack.gen.server.common.util.DSAccessException;
import com.ibm.is.sappack.gen.server.datastage.DataStageObjectFactory;
import com.ibm.is.sappack.gen.server.jobgenerator.stages.StageFactory;
import com.ibm.is.sappack.gen.server.util.ColumnData;
import com.ibm.is.sappack.gen.server.util.GraphJobLink;
import com.ibm.is.sappack.gen.server.util.GraphJobNode;
import com.ibm.is.sappack.gen.server.util.ModelInfoBlock;
import com.ibm.is.sappack.gen.server.util.SourceColMapping;
import com.ibm.is.sappack.gen.server.util.StageData;
import com.ibm.is.sappack.gen.server.util.TableData;


public class MovementJob extends BaseJob {
	// -------------------------------------------------------------------------------------
	// Constants
	// -------------------------------------------------------------------------------------
	private static final String REJECT_FILE_NAME  = "Reject_File";

	// -------------------------------------------------------------------------------------
	// Member Variables
	// -------------------------------------------------------------------------------------

	
	static String copyright() {
		return com.ibm.is.sappack.gen.server.jobgenerator.Copyright.IBM_COPYRIGHT_SHORT;
	}


	public MovementJob(ServiceToken parServiceToken, RequestJobTypeMovement parJobType,
	                   JobGeneratorRequest parJobReqInfo, Map<String, ModelInfoBlock> physModelID2TableMapMap) throws BaseException {
		super(parServiceToken, parJobType, parJobReqInfo, physModelID2TableMapMap);
	} // end of MovementJob()

	
	
	public List<String> create() throws BaseException {
		TableData srcTableArr[];
		RequestJobTypeMovement jtMovement;
		jtMovement       = (RequestJobTypeMovement) getJobType();
		
		// process the required tables
		srcTableArr = loadRequiredTables(jtMovement.getSrcPhysicalModelId());
		return this.processSrcTables(srcTableArr);
	
	} // end of create()
	
	
	protected String getDescriptionPrefix() {
		return "Move tables: ";
	}
	
	protected List<String> processSrcTables(TableData[] srcTableArr) throws BaseException {
		
		TableData              srcTable;
		TableData              trgTable;
		String                 curJobName;
		List                   moveJobMapping;
		TableData[]            mapEntry;
		Iterator               mapIter;
		int                    flowCounter;
		int                    processedNumber;
		int                    jobSuffixCounter;
		int                    numberOfJobs;
		List<String>           retJobsCreated;
		RequestJobTypeMovement jtMovement;
		
		jtMovement       = (RequestJobTypeMovement) getJobType();
		processedNumber  = 0;
		jobSuffixCounter = 0;
		retJobsCreated   = new ArrayList<String>();

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.entry();
		}
		
		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.trace(TraceLogger.LEVEL_FINER, "source tables = " + srcTableArr.length);
		}
		// check if there is at least one table to be processed
		if (srcTableArr.length > 0) {

			// we have to store the event of job creation failure
			boolean jobCreationError = false;

			// create the mapping of source and target tables
			moveJobMapping = createMapping(jtMovement, srcTableArr);
			List<TableData> tablesOfCurrentJob = new ArrayList<TableData>();
			
			numberOfJobs = srcTableArr.length / jtMovement.getFlowNumberPerJob()
			                           + (((srcTableArr.length % jtMovement.getFlowNumberPerJob()) > 0) ? 1 : 0);
			curJobName = null;
			try {
				flowCounter = 0;
				if (TraceLogger.isTraceEnabled()) {
					TraceLogger.trace(TraceLogger.LEVEL_FINEST, "Start job creation of job '" + curJobName + "'.");
				}

				processedNumber = 0;
				String firstTableOfCurrentJob = null;
				// process all movement mappings
				mapIter = moveJobMapping.iterator();
				while (mapIter.hasNext()) {
					mapEntry = (TableData[]) mapIter.next();
					srcTable = mapEntry[0]; 
					srcTable = this.prepareSourceTable(srcTable);

					// if new job is about to be started
					if (processedNumber == 0) {
						firstTableOfCurrentJob = srcTable.getName();						
						// create DS Job (definition)
						curJobName = getJobType().getJobName() + createCounterString(numberOfJobs, jobSuffixCounter) + "_"  + firstTableOfCurrentJob;
						createJobDef(curJobName);

						// job gets added to the list of (successfully) created jobs
						// though job creation actually takes place later
						retJobsCreated.add(curJobName);

						jobSuffixCounter++;
						
					}
					tablesOfCurrentJob.add(srcTable);
										
					trgTable = mapEntry[1]; 
					flowCounter++;

					if (TraceLogger.isTraceEnabled()) {
						TraceLogger.trace(TraceLogger.LEVEL_FINEST, "src table = " + srcTable.getName()
						                                          + "- no of columns = " + srcTable.getColumnData().length);
						TraceLogger.trace(TraceLogger.LEVEL_FINEST, "src table = " + trgTable.getName()
						                                          + "- no of columns = " + trgTable.getColumnData().length);
					}

					// there must be at least one column to be processed
					if (srcTable.getColumnData().length > 0) {
						// create the mapping specification for the current table
						createMovementFlow(jtMovement, srcTable, trgTable);
						processedNumber++;
						// check if the maximum number of flows per job is reached or the last table has been processed
						if (processedNumber >= jtMovement.getFlowNumberPerJob() || flowCounter == moveJobMapping.size()) {
							// set short description
							StringBuffer shortDesc = null;
							Iterator it = tablesOfCurrentJob.iterator();
							while (it.hasNext()) {
								if (shortDesc == null) {
									shortDesc = new StringBuffer(getDescriptionPrefix());
								} else {
									shortDesc.append(", ");
								}
								shortDesc.append(it.next());
							}
							setShortAndLongJobDescription(shortDesc.toString());
							tablesOfCurrentJob.clear();
							
							prepareAndSaveJob();

							if (TraceLogger.isTraceEnabled()) {
								TraceLogger.trace(TraceLogger.LEVEL_FINEST, "Job '" + curJobName
								                                          + "' has been created successfully.");
							}

							// if it is not the last table ...
							if (flowCounter < moveJobMapping.size()) {
								processedNumber = 0;

								if (TraceLogger.isTraceEnabled()) {
									TraceLogger.trace(TraceLogger.LEVEL_FINEST, "Continue job creation with job '" + curJobName
									                                          + "'.");
								}
							} // end of if (flowCounter < moveJobMapping.size())
						}

					} // end of if (curSrcTable.getColumnData().length > 0)
				} // end of while(mapIter.hasNext())
			} // end of try
			catch (DSAccessException pDSAccessExcpt) {
				String errText;
				if (TraceLogger.isTraceEnabled()) {
					TraceLogger.traceException(pDSAccessExcpt);
				}

				// something went wrong during job creation
				jobCreationError = true;
				errText          = pDSAccessExcpt.getMessage();
				if (errText == null) {
					errText = pDSAccessExcpt.getLocalizedMessage();

					if (errText == null) {
						errText = pDSAccessExcpt.toString();
					}
				}
            throw new JobGeneratorException("102900E", new String[] { errText }, 
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

            throw new JobGeneratorException("101500E", new String[] { pExcpt.toString() }, pExcpt);
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
		} // end of if (srcTableArr.length > 0)

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.exit("Number of created jobs: " + retJobsCreated.size());
		}

		return (retJobsCreated);
	}
	

	// returns a List of TableData[] where each array is of length 2.
	// the first element of the array is the source where the second is the target 
	private List createMapping(RequestJobTypeMovement pJobType, TableData pSrcTableArr[]) 
	        throws BaseException {
	   
		TableData          trgMovementTableArr[];
		TableData          srcTable;
		TableData          trgTable;
		List<TableData[]>  retMapping;
		int                srcArrIdx;
		int                trgArrIdx;
		boolean            hasTableFound;

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.entry();
		}

		retMapping = new ArrayList<TableData[]>();

		// get target table array
		trgMovementTableArr = loadRequiredTables(pJobType.getTrgPhysicalModelId());

		// create source-target-table mapping ....
		for (srcArrIdx = 0; srcArrIdx < pSrcTableArr.length; srcArrIdx++) {
			srcTable = pSrcTableArr[srcArrIdx];

			// check if there is a target for the source table
			hasTableFound = false;
			trgArrIdx     = 0;
			while (trgArrIdx < trgMovementTableArr.length && !hasTableFound) {
				trgTable = trgMovementTableArr[trgArrIdx];

				if (srcTable.getName().equalsIgnoreCase(trgTable.getName())) {
					// corresponding mapping found ==> store table and leave the loop
					retMapping.add(new TableData[]{srcTable, trgTable});
					hasTableFound = true;
				}
				else {
					trgArrIdx++; // next target table
				}
			}
		} // end of for (srcArrIdx = 0; srcArrIdx < pSrcTableArr.length; srcArrIdx ++)

		// check if there is at least one mapping
		if (retMapping.size() < 1) {
			if (TraceLogger.isTraceEnabled()) {
				TraceLogger.trace(TraceLogger.LEVEL_FINEST, "No target mapping specified.");
			}
			
         throw new JobGeneratorException("105500E", Constants.NO_PARAMS); 
		} // end of if (retMapping.size() < 1)

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.exit("mapping count = " + retMapping.size());
		}

		return (retMapping);
	} // end of createMapping()

	
   private void createMovementFlow(RequestJobTypeMovement parJobType, TableData parSrcTable,  
                                   TableData parTrgTable) 
           throws BaseException {
      SourceColMapping       columnMapping;
      GraphJobNode           sourceNode;
      GraphJobNode           transformerNode;
      GraphJobNode           trgPersistenceNode;
      GraphJobLink           transformerLink;
      GraphJobLink           persLink;
      GraphJobLink           rejectLink;
      StageData              newSrcPersistenceStage;
      StageData              newTrgPersistenceStage;
      StageData              newTransformerStage;
      StageData              newRejectSeqFileStage;
      DataStageObjectFactory dsFactory;
      DSJobDef               jobDef;
      DSLink                 stageLink;
      String                 rejectFilePath;

      if (TraceLogger.isTraceEnabled()) {
         TraceLogger.entry("src table = " + parSrcTable + " - trg table = " + parTrgTable);
      }

      dsFactory = getDSFactory();
      jobDef    = _JobStructureData.getJobDef();

      // we need a different mapping (source and target columns has to have the same name)
      parSrcTable = getPersistanceStageMapping(parSrcTable);
      parTrgTable = getPersistanceStageMapping(parTrgTable);
      
      // ************************
      // INPUT: Persistence stage
      // ************************
      newSrcPersistenceStage = StageFactory.createPersistenceSourceStage(jobDef, 
                                                                         parSrcTable, 
                                                                         parJobType.getPersistenceDataSrc(), 
                                                                         parSrcTable.getSQLStatement(), 
                                                                         dsFactory);

      // update JobStructureData ...
      // ===========================
      sourceNode = new GraphJobNode(newSrcPersistenceStage);
      _JobStructureData.getLayoutData().addSourceNode(sourceNode);

      // ******************
      // Transformer stage
      // ******************
      newTransformerStage = StageFactory.createTransformerStage(jobDef, dsFactory, null);
      transformerNode = new GraphJobNode(newTransformerStage);

      // *****************************************************************
      // create link Source Persistence Stage <------> Transformer Stage
      // *****************************************************************
      // create the column mapping (link)
      stageLink = createColumnMapping(newSrcPersistenceStage, newTransformerStage, parSrcTable, 
                                      newSrcPersistenceStage.getLinkParams(), dsFactory);

      // update JobStructureData ...
      // ===========================
      transformerLink = new GraphJobLink(stageLink, transformerNode);
      sourceNode.addOutLink(transformerLink);

      // *************************
      // OUTPUT: Persistence stage
      // *************************
      newTrgPersistenceStage = StageFactory.createPersistenceTargetStage(jobDef, 
                                                                         parTrgTable, 
                                                                         parJobType.getPersistenceDataTrg(), 
                                                                         getServiceToken());

      // ********************************************************************
      // create link Transformer Stage <------> Target Persistence Stage
      // ********************************************************************
      stageLink = createColumnMapping(newTransformerStage, newTrgPersistenceStage, parTrgTable, 
                                      newTrgPersistenceStage.getLinkParams(), dsFactory);

      // update JobStructureData ...
      // ===========================
      trgPersistenceNode = new GraphJobNode(newTrgPersistenceStage);
      persLink           = new GraphJobLink(stageLink, trgPersistenceNode);
      transformerNode.addOutLink(persLink);

      // check if there is a Reject File Path ...
      rejectFilePath = parJobType.getRejectFilePath();
      if (rejectFilePath != null) {
         // ******************************
         // Sequential File (Reject) stage
         // ******************************
         newRejectSeqFileStage = StageFactory.createFileTargetStage(jobDef, REJECT_FILE_NAME, 
                                                                    parTrgTable.getName(), 
                                                                    new FileStageData(rejectFilePath), dsFactory);

         // ********************************************************************************
         // create Reject link Persistence Stage <------> Reject Sequential File Stage
         // ********************************************************************************
         // create a link between source and target stages
         stageLink = dsFactory.createDSLink(jobDef, null, true, LinkTypeEnum.REJECT_LITERAL, 
                                            newRejectSeqFileStage.getLinkParams());


         // create the column mapping object ...
         columnMapping = TableData.createSourceColumnMapping(parTrgTable);

         // update the link (associations with stages and flow variables, etc ...)
         dsFactory.updateLink(stageLink, newTrgPersistenceStage, newRejectSeqFileStage, columnMapping, null, null);

         // update JobStructureData ...
         // ===========================
         rejectLink = new GraphJobLink(stageLink, new GraphJobNode(newRejectSeqFileStage));
         trgPersistenceNode.addOutLink(rejectLink);
      } // end of if (rejectFilePath != null)

      if (TraceLogger.isTraceEnabled()) {
         TraceLogger.exit();
      }
   } // end of createMovementFlow()

   
   private TableData getPersistanceStageMapping(TableData parMapping) {
      TableData  retNewMapping;
      ColumnData colArr[];
      int        arrIdx;
      
      // first get a copy of the passed mapping ...
      retNewMapping = parMapping.getCopy();
      
      
      // and then swap two columns attributes: NAME <---> TRANSFORMER SRC MAPPING
      colArr = retNewMapping.getColumnData();
      for(arrIdx = 0; arrIdx < colArr.length; arrIdx ++)
      {
         // note: we 'misuse' the original name field here
         colArr[arrIdx].setTransformerSrcMapping(colArr[arrIdx].getName());
      } // end of for(arrIdx = 0; arrIdx < colArr.length; arrIdx ++)

      return(retNewMapping);
   } // end of getPersistanceStageMapping()
   
	protected void validate() throws JobGeneratorException {
		RequestJobTypeMovement jtMovement;
		TableData              srcTableArr[];
		TableData              trgTableArr[];
		FileStageData          srcFileStageData;
		FileStageData          trgFileStageData;
		ODBCStageData          srcODBCStageData;
		ODBCStageData          trgODBCStageData;
		PersistenceData        tmpSrcPersistenceData;
		PersistenceData        tmpTrgPersistenceData;
      ModelInfoBlock         srcModelInfoBlk;
      ModelInfoBlock         trgModelInfoBlk;
      String                 sourceSchema;
      String                 targetSchema;
		Map                    srcTableMap;
		Map                    trgTableMap;
		boolean                isParamEqual;

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.entry();
		}

		jtMovement = (RequestJobTypeMovement) getJobType();

		// check if there are at least two models in the map ...
		sourceSchema    = null;
		targetSchema    = null;
		srcModelInfoBlk = (ModelInfoBlock) _PhysModelID2TableMap.get(jtMovement.getSrcPhysicalModelId());
      srcTableMap     = srcModelInfoBlk.getTableMap();
		if (srcTableMap == null || srcTableMap.size() < 1) {

			if (TraceLogger.isTraceEnabled()) {
				TraceLogger.trace(TraceLogger.LEVEL_FINEST, "Movement Jobs require source model to be defined.");
			}
			throw new JobGeneratorException("103000E", Constants.NO_PARAMS);
		} // end of if (srcTableMap == null || srcTableMap.size() < 1)

		trgModelInfoBlk = (ModelInfoBlock) _PhysModelID2TableMap.get(jtMovement.getTrgPhysicalModelId());
      trgTableMap     = trgModelInfoBlk.getTableMap();
		if (trgTableMap == null || trgTableMap.size() < 1) {
			if (TraceLogger.isTraceEnabled()) {
				TraceLogger.trace(TraceLogger.LEVEL_FINEST, "Movement Jobs require target model to be defined.");
			}
			throw new JobGeneratorException("103100E", Constants.NO_PARAMS);
		} // end of if (trgTableMap == null || trgTableMap.size() < 1)

		// either Datasource or Schema must be different
		tmpSrcPersistenceData = jtMovement.getPersistenceDataSrc();
		tmpTrgPersistenceData = jtMovement.getPersistenceDataTrg();

		// first check if source persistence stage type is equal
		// to target persistence stage type
		isParamEqual = false;
		if (tmpSrcPersistenceData instanceof ODBCStageData && tmpTrgPersistenceData instanceof ODBCStageData) {
			srcODBCStageData = (ODBCStageData) tmpSrcPersistenceData;
			trgODBCStageData = (ODBCStageData) tmpTrgPersistenceData;

			if (srcODBCStageData.getDataSourceName().equals(trgODBCStageData.getDataSourceName())) {
				// source and target datasources are equal
				isParamEqual = true;

				// get alternate schema names
				sourceSchema = srcODBCStageData.getAlternameSchemaName();
            targetSchema = trgODBCStageData.getAlternameSchemaName();
			} // end of if (srcODBCStageData.getDataSourceName().equals(trgODBCStageData.getDataSourceName()))
		}
		else {
			if (tmpSrcPersistenceData instanceof FileStageData && tmpTrgPersistenceData instanceof FileStageData) {
				srcFileStageData = (FileStageData) tmpSrcPersistenceData;
				trgFileStageData = (FileStageData) tmpTrgPersistenceData;

				if (srcFileStageData.getFilenamePrefix().equals(trgFileStageData.getFilenamePrefix())) {
					// source and target file prefixes are equal
					isParamEqual = true;
				} // end of if (srcFileStageData.getFilenamePrefix().equals(trgFileStageData.getFilenamePrefix()))
			} // end of if ((tmpSrcPersistenceData instanceof FileStageData && ... instanceof FileStageData))
		} // end of (else) if ((tmpSrcPersistenceData instanceof ODBCStageData && ... instanceof ODBCStageData))

		if (isParamEqual) {
			isParamEqual = false;
			srcTableArr  = (TableData[]) srcTableMap.get(jtMovement.getSupportedTableTypes().getFirstType());
			trgTableArr  = (TableData[]) trgTableMap.get(jtMovement.getSupportedTableTypes().getFirstType());

			if (srcTableArr.length > 0 && trgTableArr.length > 0) {
			   if (sourceSchema == null) {
	            sourceSchema = srcTableArr[0].getDBSchema();
			   }
            if (targetSchema == null) {
               targetSchema = trgTableArr[0].getDBSchema();
            }
			   
				if (sourceSchema.equals(targetSchema)) {
					// source and target schemas are equal
					isParamEqual = true;
				} // end of if (sourceSchema.equals(targetSchema))
			} // end of if (srcTableArr.length > 0 && trgTableArr.length > 0)
		} // end of if (isParamEqual)

		if (isParamEqual) {
			if (TraceLogger.isTraceEnabled()) {
				TraceLogger.trace(TraceLogger.LEVEL_FINEST, 
				                  "Datasource/Filename or Schema of source model must be different of to Datasource/Filename or Schema of target model.");
			}
			throw new JobGeneratorException("103200E", Constants.NO_PARAMS);
		} // end of if (isParamEqual)

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.exit();
		}
	} // end of validate()
	
	/**
	 * prepareSourceTable
	 * 
	 * This is just a hook to allow Subclasses
	 * to manipulate the SQL select statement
	 * of the source table
	 * 
	 * @param tableData
	 * @return
	 */
	protected TableData prepareSourceTable(TableData tableData) {
		
		return tableData;
	}
	

} // end of class MovementJob
