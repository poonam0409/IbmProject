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


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import DataStageX.DSJobDef;
import DataStageX.DSLink;

import com.ibm.is.sappack.gen.common.BaseException;
import com.ibm.is.sappack.gen.common.Constants;
import com.ibm.is.sappack.gen.common.JobGeneratorException;
import com.ibm.is.sappack.gen.common.request.JobGeneratorRequest;
import com.ibm.is.sappack.gen.common.request.RFCData;
import com.ibm.is.sappack.gen.common.request.RequestJobTypeABAPExtract;
import com.ibm.is.sappack.gen.common.trace.TraceLogger;
import com.ibm.is.sappack.gen.common.util.ServerMessageCatalog;
import com.ibm.is.sappack.gen.common.util.StringUtils;
import com.ibm.is.sappack.gen.server.common.service.ServiceToken;
import com.ibm.is.sappack.gen.server.common.util.DSAccessException;
import com.ibm.is.sappack.gen.server.datastage.DataStageObjectFactory;
import com.ibm.is.sappack.gen.server.jobgenerator.stages.ABAPExtractStageProperties;
import com.ibm.is.sappack.gen.server.jobgenerator.stages.StageFactory;
import com.ibm.is.sappack.gen.server.util.GraphJobLink;
import com.ibm.is.sappack.gen.server.util.GraphJobNode;
import com.ibm.is.sappack.gen.server.util.ModelInfoBlock;
import com.ibm.is.sappack.gen.server.util.ObjectParamMap;
import com.ibm.is.sappack.gen.server.util.StageData;
import com.ibm.is.sappack.gen.server.util.TableData;


public class ABAPExtractJob extends BaseJob {
	// -------------------------------------------------------------------------------------
	// Subclasses
	// -------------------------------------------------------------------------------------
	class CreateStageResult {
		private StageData    _StageData;
		private GraphJobNode _GraphJobNode;

		public CreateStageResult(StageData parStageData, GraphJobNode parJobNode) {
			_StageData    = parStageData;
			_GraphJobNode = parJobNode;
		}

		public StageData getStageData() {
			return (_StageData);
		}

		public GraphJobNode getGraphJobNode() {
			return (_GraphJobNode);
		}
	} // end of class CreateStageResult


	// -------------------------------------------------------------------------------------
	// Member Variables
	// -------------------------------------------------------------------------------------
	private HashMap<String,String>    _ABAPCodeResultMap;
	private TableData                 _TrgTableDataArr[];

   private static String             _ShortDescPrefix;

   
   static {
      // load Short Description prefix from message catalog 
      _ShortDescPrefix = ServerMessageCatalog.getDefaultCatalog().getText("00102I");  //$NON-NLS-1$
      _ShortDescPrefix = _ShortDescPrefix + " ";  //$NON-NLS-1$
   } // end of static { }


	static String copyright() {
		return com.ibm.is.sappack.gen.server.jobgenerator.Copyright.IBM_COPYRIGHT_SHORT;
	}


	public ABAPExtractJob(ServiceToken parServiceToken, RequestJobTypeABAPExtract parJobType,
	                      JobGeneratorRequest parJobReqInfo, Map<String, ModelInfoBlock> physModelID2TableMap) throws BaseException {
		super(parServiceToken, parJobType, parJobReqInfo, physModelID2TableMap);

		_ABAPCodeResultMap = new HashMap<String,String>();
	} // end of ABAPExtractJob()


	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<String> create() throws BaseException {
		RequestJobTypeABAPExtract jtABAPExtract;
		TableData                 curTable;
		List<String>                retJobsCreated;
		Map                       rfcDestinationMap;
		Map.Entry                 rfcDestinationEntry;
		String                    curRFCDestination;
		String                    curRFCProgramId;
		String                    curJobName;
		Iterator                  rfcDestIter;
		int                       arrIdx;
		int                       processedFlowNumber;
		int                       jobSuffixCounter;
		int                       numberOfJobs;

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.entry();
		}

		retJobsCreated = new ArrayList();

		// ---------------------------
		// ==> process existing tables
		// ---------------------------
		if (_TrgTableDataArr != null && _TrgTableDataArr.length > 0) {
			jtABAPExtract       = (RequestJobTypeABAPExtract) getJobType();
			processedFlowNumber = 0;
			jobSuffixCounter    = 0;

			// we have to store the event of job creation failure
			boolean jobCreationError = false;
			curJobName               = null;

			try {
				TraceLogger.trace(TraceLogger.LEVEL_FINE, "Number of Flows per job: {0}, number of tables: {1}", new Object[]{jtABAPExtract.getFlowNumberPerJob(), _TrgTableDataArr.length }); //$NON-NLS-1$

				numberOfJobs = _TrgTableDataArr.length / jtABAPExtract.getFlowNumberPerJob() + 
				(((_TrgTableDataArr.length % jtABAPExtract.getFlowNumberPerJob()) > 0) ? 1 : 0);
				TraceLogger.trace(TraceLogger.LEVEL_FINE, "Number of jobs: {0}", numberOfJobs); //$NON-NLS-1$
				// RFC jobs
				boolean useRFC = jtABAPExtract.getRFCData().getEnabled(); 
				boolean generateRFCDestinationNames = false;
				String rfcDestinationNamePrefix = null;
				if (useRFC) {
					generateRFCDestinationNames = jtABAPExtract.getRFCData().getGenerateRFCDestinationNames();
					if (generateRFCDestinationNames) {
						rfcDestinationNamePrefix = jtABAPExtract.getRFCData().getRFCDestinationNameGenerationPrefix();
						TraceLogger.trace(TraceLogger.LEVEL_FINE, "RFC destination names are generated with prefix {0}", rfcDestinationNamePrefix); //$NON-NLS-1$
						rfcDestinationMap = null;
						rfcDestIter       = null;
					} else {
						rfcDestinationMap = jtABAPExtract.getRFCData().getDestinationsMap();
						rfcDestIter       = rfcDestinationMap.entrySet().iterator();
					}
				} 
				else {
					rfcDestinationMap = null;
					rfcDestIter       = null;
				}

				// create a DS task container
				curJobName = getJobType().getJobName() + createCounterString(numberOfJobs, jobSuffixCounter) + "_"  + _TrgTableDataArr[0].getName();				 //$NON-NLS-1$
				createJobDef(curJobName);
				StringBuffer shortDescriptionBuf = new StringBuffer(_ShortDescPrefix);

				// job gets added to the list of (successfully) created jobs
				// though job creation actually takes place later
				retJobsCreated.add(curJobName);

				if (TraceLogger.isTraceEnabled()) {
					TraceLogger.trace(TraceLogger.LEVEL_FINE, "Start job creation of job '" + curJobName + "'."); //$NON-NLS-1$ //$NON-NLS-2$
				}

				for (arrIdx = 0; arrIdx < _TrgTableDataArr.length; arrIdx++) {
					curTable = _TrgTableDataArr[arrIdx];

					if (TraceLogger.isTraceEnabled()) {
						TraceLogger.trace(TraceLogger.LEVEL_FINE, "Create ABAP extract job for table = " + curTable.getName() + " - no of columns = " + curTable.getColumnData().length); //$NON-NLS-1$ //$NON-NLS-2$
						for (int i=0; i<curTable.getColumnData().length; i++) {
							TraceLogger.trace(TraceLogger.LEVEL_FINEST, "   column {0}: {1}", new Object[]{i, curTable.getColumnData()[i].getName()}); //$NON-NLS-1$
						}
					}

					// there must be at least one column to be processed
					if (curTable.getColumnData().length > 0) {
						if (useRFC) {
							if (TraceLogger.isTraceEnabled()) {
								TraceLogger.trace(TraceLogger.LEVEL_FINE, "Job uses RFC transfer method"); //$NON-NLS-1$
							}

							if (generateRFCDestinationNames) {
								String[] dest = RFCData.generateRFCDestinationName(rfcDestinationNamePrefix, _TrgTableDataArr[arrIdx].getDescriptiveTableName(), arrIdx);
								curRFCDestination = dest[0];
								curRFCProgramId = dest[1];
							} else {
								// get the RFC Destination and the RFC Program Id from the static map
								
								// osuhre, 51336: use the whole RFC destination map across jobs and don't reset the iterator for each job.
								if (!rfcDestIter.hasNext()) {
									rfcDestIter = rfcDestinationMap.entrySet().iterator();								
								}
								rfcDestinationEntry = (Map.Entry) rfcDestIter.next();
								curRFCDestination   = (String) rfcDestinationEntry.getKey();
								curRFCProgramId     = (String) rfcDestinationEntry.getValue();
							} 
							TraceLogger.trace(TraceLogger.LEVEL_FINE, "Using RFC destination ''{0}'' (program id: ''{1}''", new Object[]{curRFCDestination, curRFCProgramId}); //$NON-NLS-1$
						} 
						else {
							if (TraceLogger.isTraceEnabled()) {
								TraceLogger.trace(TraceLogger.LEVEL_FINE, "Job uses CPIC transfer method"); //$NON-NLS-1$
							}

							curRFCDestination = null;
							curRFCProgramId   = null;
						}

						// create the mapping specification for the current table
						createExtractFlow(curTable, arrIdx, curRFCDestination, curRFCProgramId);
						processedFlowNumber ++;

						boolean isAllTablesProcessed = (_TrgTableDataArr.length == (arrIdx + 1));
						boolean isCurrentJobFinished;

						// we have to create a new DataStage job if ...
						// -----------------------------------
						// - the processed flow number exceeds the allowed number of 'flows per job'
						// - the last table has been processed
						// - the type of next table is different to the current
						isCurrentJobFinished = processedFlowNumber >= jtABAPExtract.getFlowNumberPerJob()                   || 
						                       isAllTablesProcessed                                                         || 
						                       !curTable.getTableType().equals(_TrgTableDataArr[arrIdx + 1].getTableType());

						if (isCurrentJobFinished) {
							// --> set short description
							shortDescriptionBuf.append(curTable.getName());
							setShortAndLongJobDescription(shortDescriptionBuf.toString());
							shortDescriptionBuf = new StringBuffer(_ShortDescPrefix);

							prepareAndSaveJob();

							if (TraceLogger.isTraceEnabled()) {
								TraceLogger.trace(TraceLogger.LEVEL_FINE, "Job '" + curJobName + "' has been created successfully."); //$NON-NLS-1$ //$NON-NLS-2$
							}

							// if it is not the last table ...
							if (!isAllTablesProcessed) {
								jobSuffixCounter++;

								// ==> create a new DS Job (definition) ...
								String newJobName = getJobType().getJobName() + createCounterString(numberOfJobs, jobSuffixCounter) + "_"  + _TrgTableDataArr[arrIdx + 1].getName(); //$NON-NLS-1$
								curJobName = newJobName;
								createJobDef(newJobName);

								// job gets added to the list of
								// (successfully) created jobs
								// though job creation actually takes place
								// later
								retJobsCreated.add(newJobName);

								processedFlowNumber = 0;

								if (TraceLogger.isTraceEnabled()) {
									TraceLogger.trace(TraceLogger.LEVEL_FINE, "Continue job creation with job '" + curJobName + "'."); //$NON-NLS-1$ //$NON-NLS-2$
								}
							} // end of if (!isAllTabpleProcessed)
						} 
						else {
							// not the last table for current job --> add table name to description
							shortDescriptionBuf.append(curTable.getName() + ", "); //$NON-NLS-1$
						} // end of (else) if (isCurrentJobFinished)
					} // end of if (curTable.getColumnData().length > 0) 
				} // end of for (arrIdx = 0; arrIdx < _TrgTableDataArr.length; arrIdx++) 
			} // end of try
			catch (DSAccessException dsae) {
				if (TraceLogger.isTraceEnabled()) {
					TraceLogger.traceException(dsae);
				}

				// something went wrong during job creation
				jobCreationError = true;

				throw new JobGeneratorException("126200E", new String[] { dsae.getLocalizedMessage()},  //$NON-NLS-1$
						dsae);
			} catch (JobGeneratorException pJobGenExcpt) {

				// something went wrong during job creation
				jobCreationError = true;

				throw pJobGenExcpt;
			} catch (Exception pExcpt) {
				if (TraceLogger.isTraceEnabled()) {
					TraceLogger.traceException(pExcpt);
				}

				// something went wrong during job creation
				jobCreationError = true;

				throw new JobGeneratorException("101500E", new String[] { pExcpt.toString() }, pExcpt); //$NON-NLS-1$
			} finally {

				// in case of job creation problems we simply remove
				// the last job from the list of (successfully) created jobs
				// because we cannot be sure that it indeed has been created
				if (jobCreationError) {

					// possibly an exception occurred BEFORE the job could
					// actually
					// be added to the list of created jobs, therefore we must
					// check for the size of the list
					if (retJobsCreated.size() != 0) {
						retJobsCreated.remove(retJobsCreated.size() - 1);
					}
				}
			}
		} // end of if (_TrgTableDataArr != null && _TrgTableDataArr.length > 0)

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.exit("Number of created jobs: " + retJobsCreated.size()); //$NON-NLS-1$
		}

		return (retJobsCreated);
	} // end of create()


	protected CreateStageResult createAbapStageAndTransformer(TableData curTable, 
	                                                          int curTableIndex,
	                                                          String curRFCDestination, 
	                                                          String curRFCProgramId, 
	                                                          RequestJobTypeABAPExtract reqJobType) throws BaseException,
	                                                                                                       JobGeneratorException {
		GraphJobNode              sourceNode;
		GraphJobNode              transformerNode;
		GraphJobLink              transformerLink;
		DataStageObjectFactory dsFactory;
		DSJobDef                  jobDef;
		StageData                 newABAPStage;
		StageData                 newTransformerStage;
		DSLink                    stageLink;
		String                    tableName;
		String                    newABAPCode;
		ObjectParamMap            linkParamMap;

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.entry("table = " + curTable); //$NON-NLS-1$
		}

		tableName = curTable.getName();
		dsFactory = getDSFactory();
		jobDef    = _JobStructureData.getJobDef();

		// *****************************
		// INPUT: SAP ABAP Extract Stage
		// *****************************
		newABAPStage = StageFactory.createABAPExtractStage(jobDef, tableName, curTable.isUnicodeSystem(), dsFactory);

		// update JobStructureData ...
		// ===========================
		sourceNode = new GraphJobNode(newABAPStage);
		_JobStructureData.getLayoutData().addSourceNode(sourceNode);

		// *****************
		// Transformer stage
		// *****************
		newTransformerStage = StageFactory.createTransformerStage(jobDef, dsFactory, null);
		transformerNode = new GraphJobNode(newTransformerStage);

		String abapProgramName = null;

		// ************************************************************
		// create link ABAP Extract Stage <------> Transformer Stage
		// ************************************************************
		// --------------------------------------
		// set ABAP Extract Stage Link properties
		// --------------------------------------
		linkParamMap = ABAPExtractStageProperties.getDefaultLinkParams();
		linkParamMap.put(ABAPExtractStageProperties.LNK_PROP_SAP_TABLE_NAME_KEY, tableName);
		linkParamMap.put(ABAPExtractStageProperties.PROP_CONN_SAP_CONNECTION_NAME_KEY, reqJobType.getSAPSystem().getSAPSystemName());

		if (reqJobType.getRFCData().getEnabled()) {
			if (TraceLogger.isTraceEnabled()) {
				TraceLogger.trace(TraceLogger.LEVEL_FINEST, "Creating ABAP stage with RFC transfer method"); //$NON-NLS-1$
			}
			abapProgramName = curTable.getRFCABAPProgramName();
			linkParamMap.put(ABAPExtractStageProperties.LNK_PROP_SAP_DATA_TRANSFER_METHOD_KEY, ABAPExtractStageProperties.CONN_PROP_SAP_RFC_DATA_TRANSFER_METHOD_VALUE); // CONN_PROP_SAP_CPIC_DATA_TRANSFER_METHOD_VALUE);
			linkParamMap.put(ABAPExtractStageProperties.LNK_PROP_SAP_RFC_GATEWAY_HOST_KEY, reqJobType.getRFCData().getGatewayHost());
			linkParamMap.put(ABAPExtractStageProperties.LNK_PROP_SAP_RFC_GATEWAY_SERVICE_KEY, reqJobType.getRFCData().getGatewayService());
			linkParamMap.put(ABAPExtractStageProperties.LNK_PROP_SAP_RFC_PROGID_KEY, curRFCProgramId);
			linkParamMap.put(ABAPExtractStageProperties.LNK_PROP_SAP_RFC_DESTINATION_NAME_KEY, curRFCDestination);
			linkParamMap.put(ABAPExtractStageProperties.LNK_PROP_SAP_RFC_QRFC_NAME_KEY, curRFCDestination);

			// if available, set 'Create RFC Destination' and/or 'Cleanup RFC Destination'
			if (reqJobType.getRFCData().doCreateRFCDest() != null) {
				linkParamMap.put(ABAPExtractStageProperties.LNK_PROP_SAP_RFC_DEST_CREATE_KEY, reqJobType.getRFCData().doCreateRFCDest().toString().toUpperCase());
			}
			if (reqJobType.getRFCData().doCleanUpRFCDestAfterReq() != null) {
				linkParamMap.put(ABAPExtractStageProperties.LNK_PROP_SAP_RFC_DEST_CLEANUP_KEY, reqJobType.getRFCData().doCleanUpRFCDestAfterReq().toString().toUpperCase());
			}
			
			if (_JobRequestInfo.doCreateV7Stage()) {
            if (reqJobType.getRFCData().doSuppressAbapProgValidation() != null) {
               linkParamMap.put(ABAPExtractStageProperties.LNK_PROP_SAP_RFC_CHECK_ABAP_PROG, 
                                reqJobType.getRFCData().doSuppressAbapProgValidation().toString().toUpperCase());
            }
	         if (reqJobType.getRFCData().doDeleteLUW() != null) {
	            linkParamMap.put(ABAPExtractStageProperties.LNK_PROP_SAP_RFC_DELETE_LUW, 
	                             reqJobType.getRFCData().doDeleteLUW().toString().toUpperCase());
	         }
	         
	         if (reqJobType.getRFCData().doSuppressBackgroundJob() != null) {
		            linkParamMap.put(ABAPExtractStageProperties.LNK_PROP_SAP_RFC_SUPRESS_BACKGROUND_JOB, 
		                             reqJobType.getRFCData().doSuppressBackgroundJob().toString().toUpperCase());
		         }
	         
            linkParamMap.put(ABAPExtractStageProperties.LNK_PROP_SAP_RFC_RETRY_COUNT, String.valueOf(reqJobType.getRFCData().getRetryCount()));
            linkParamMap.put(ABAPExtractStageProperties.LNK_PROP_SAP_RFC_RETRY_INTERVAL, String.valueOf(reqJobType.getRFCData().getRetryInterval()));
			} // end of if (_JobRequestInfo.doCreateV7Stage())
			
			// put the current RFC destination into the available ABAP code
			if (TraceLogger.isTraceEnabled()) {
				TraceLogger.trace(TraceLogger.LEVEL_FINEST, "replace RFCDestination '" + curRFCDestination + "' in ABAP Code '" + curTable.getABAPCodeRFC() + "'."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
			newABAPCode = StringUtils.replaceString(curTable.getABAPCodeRFC(), Constants.ABAPCODE_PLACEHOLDER_RFC_DESTINATION_NAME, curRFCDestination);
			// newABAPCode = curTable.getABAPCode().replaceAll(Constants.ABAPCODE_PLACEHOLDER_RFC_DESTINATION_NAME,
			// curRFCDestination);
		} else if (reqJobType.getCPICData().isEnabled()) {
			if (TraceLogger.isTraceEnabled()) {
				TraceLogger.trace(TraceLogger.LEVEL_FINEST, "Creating ABAP stage with CPIC transfer method"); //$NON-NLS-1$
			}
			abapProgramName = curTable.getCPICABAPProgramName();
			linkParamMap.put(ABAPExtractStageProperties.LNK_PROP_SAP_DATA_TRANSFER_METHOD_KEY, ABAPExtractStageProperties.CONN_PROP_SAP_CPIC_DATA_TRANSFER_METHOD_VALUE);
			linkParamMap.put(ABAPExtractStageProperties.LNK_PROP_SAP_CPIC_USE_DEFAULT_LOGON_DETAILS, reqJobType.getCPICData().isUseSAPLogonDetails() ? "1" : "0"); //$NON-NLS-1$ //$NON-NLS-2$
			String cpicUser = reqJobType.getCPICData().getCpicUser();
			String cpicUserT = cpicUser;
			if (reqJobType.getCPICData().isUseSAPLogonDetails()) {
				cpicUser = ""; //$NON-NLS-1$
			}
			linkParamMap.put(ABAPExtractStageProperties.LNK_PROP_SAP_CPIC_USER, cpicUser);
			linkParamMap.put(ABAPExtractStageProperties.LNK_PROP_SAP_CPIC_USERT, cpicUserT);
			String cpicPassword = reqJobType.getCPICData().getCpicPassword();
			if (cpicPassword == null) {
				cpicPassword = ""; //$NON-NLS-1$
			}
			else {
				cpicPassword = getServiceToken().encrypt(cpicPassword);
			}
			String cpicPasswordT = cpicPassword;
			if (reqJobType.getCPICData().isUseSAPLogonDetails()) {
				cpicPassword = ""; //$NON-NLS-1$
			}
			linkParamMap.put(ABAPExtractStageProperties.LNK_PROP_SAP_CPIC_PASSWORD, cpicPassword);
			linkParamMap.put(ABAPExtractStageProperties.LNK_PROP_SAP_CPIC_PASSWORDT, cpicPasswordT);
			newABAPCode = curTable.getABAPCodeCPIC();
		}
		else {
			throw new JobGeneratorException("101700E", Constants.NO_PARAMS); //$NON-NLS-1$
		}
		linkParamMap.put(ABAPExtractStageProperties.LNK_PROP_SAP_ABAP_PROGRAM_NAME, abapProgramName);
		linkParamMap.put(ABAPExtractStageProperties.LNK_PROP_SAP_ABAP_CODE, newABAPCode);

		// if available set Background Processing options
		if (reqJobType.getBackgroundProcessingData().isEnabled()) {
			linkParamMap.put(ABAPExtractStageProperties.LNK_PROP_BG_PROC_ENABLED, String.valueOf(reqJobType.getBackgroundProcessingData().isEnabled()).toUpperCase());
			String variantName = String.valueOf(reqJobType.getBackgroundProcessingData().getVariant());
			variantName += "_" + curTable.getName(); //$NON-NLS-1$
			if (curTableIndex >= 0) {
				variantName += "_" + curTableIndex; //$NON-NLS-1$
			}
			if (TraceLogger.isTraceEnabled()) {
				TraceLogger.trace(TraceLogger.LEVEL_FINE, "Creating variant name ''{0}'' for {1}th table ''{2}''", new Object[]{variantName, curTable.getName(), curTableIndex }); //$NON-NLS-1$
			}
			linkParamMap.put(ABAPExtractStageProperties.LNK_PROP_BG_PROC_VARIANT_NAME, variantName);
			linkParamMap.put(ABAPExtractStageProperties.LNK_PROP_BG_PROC_DELAY, String.valueOf(reqJobType.getBackgroundProcessingData().getStartDelay()));
			linkParamMap.put(ABAPExtractStageProperties.LNK_PROP_BG_PROC_DELAY, String.valueOf(reqJobType.getBackgroundProcessingData().getStartDelay()));
			linkParamMap.put(ABAPExtractStageProperties.LNK_PROP_BG_PROC_SAP_VARIANT, String.valueOf(reqJobType.getBackgroundProcessingData().doUseSAPVariant()).toUpperCase());
			linkParamMap.put(ABAPExtractStageProperties.LNK_PROP_BG_PROC_KEEP_VARIANT, String.valueOf(reqJobType.getBackgroundProcessingData().doKeepVariant()).toUpperCase());
		} // end of if (reqJobType.getBackgroundProcessingData().isEnabled())

		// set the PROGDEVEL_STATUS and corresponding parameters
		linkParamMap.put(ABAPExtractStageProperties.LNK_PROP_PROG_DEVELOP_STATUS_KEY, "8"); //$NON-NLS-1$
		// linkParamMap.put(ABAPExtraxtStageProperties.CONN_PROP_SAP_UPLOAD_ABAP_CODE_KEY, newABAPCode);
		linkParamMap.put(ABAPExtractStageProperties.LNK_PROP_PROG_UPLOAD_MODE_KEY, "3"); //$NON-NLS-1$
		linkParamMap.put(ABAPExtractStageProperties.LNK_PROP_PROG_GEN_MODE, "3"); //$NON-NLS-1$

		// save current ABAP program name and corresponding ABAP code for job result
		_ABAPCodeResultMap.put(abapProgramName, newABAPCode);

		linkParamMap.put(ABAPExtractStageProperties.LNK_PROP_SAP_APP_SERVER_KEY, curTable.getSAPSSystemHost());

		// check for connection Job Parameter
		String connectionJobParamName = reqJobType.getSAPSystem().getSAPSystemName();
		if (StringUtils.isJobParamVariable(connectionJobParamName)) {
			// it's available ==> add the Job Parameter name to the link parameters
			// (first remove leading and trailing '#')
			connectionJobParamName = connectionJobParamName.substring(1, connectionJobParamName.length() - 1);
			linkParamMap.put(ABAPExtractStageProperties.PROP_CONN_SAP_CONNECTION_PARAM_KEY, connectionJobParamName);
		}

		// set current SAPSystemData for SAP System Logon on the link ...
		StageFactory.setSAPSystemData(linkParamMap, reqJobType.getSAPSystem(), true, getServiceToken());

		// different handling for default logon handling in ABAP stages
		if (reqJobType.getSAPSystem().getSAPUserName() == null) {
			// these parameters must be set (but the content is not important)
			linkParamMap.put(ABAPExtractStageProperties.LNK_PROP_SAP_USERNAME_KEY, "SAPUser"); //$NON-NLS-1$
			linkParamMap.put(ABAPExtractStageProperties.LNK_PROP_SAP_PASSWORD_KEY, "sapuser"); //$NON-NLS-1$
			linkParamMap.put(ABAPExtractStageProperties.LNK_PROP_SAP_CLIENT_NUMBER_KEY, "800"); //$NON-NLS-1$
			linkParamMap.put(ABAPExtractStageProperties.LNK_PROP_SAP_LANGUAGE_KEY, "EN"); //$NON-NLS-1$
			linkParamMap.put(ABAPExtractStageProperties.LNK_PROP_SAP_USE_DEFAULT_LOGON, "1"); //$NON-NLS-1$
		}

		// and then create the column mapping (link)
		stageLink = createColumnMapping(newABAPStage, newTransformerStage, curTable, linkParamMap, dsFactory);

		// update JobStructureData ...
		// ===========================
		transformerLink = new GraphJobLink(stageLink, transformerNode);
		sourceNode.addOutLink(transformerLink);

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.exit();
		}

		return (new CreateStageResult(newTransformerStage, transformerNode));
	} // end of createAbapStageAndTransformer()


	protected void createExtractFlow(TableData curTable, int tableIndex, String curRFCDestination, 
	                                 String curRFCProgramId) throws BaseException {
	   
	   TableData                 abapStageMapping;
		RequestJobTypeABAPExtract jtABAPExtract;
		CreateStageResult         lastStageCreateResult;
		GraphJobNode              transformerNode;
		GraphJobLink              odbcLink;
		DataStageObjectFactory    dsFactory;
		DSJobDef                  jobDef;
		StageData                 newPersStage;
		StageData                 newTransformerStage;
		DSLink                    stageLink;

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.entry("table = " + curTable); //$NON-NLS-1$
		}

		dsFactory     = getDSFactory();
		jobDef        = _JobStructureData.getJobDef();
		jtABAPExtract = (RequestJobTypeABAPExtract) getJobType();


		// create ABAP Stage and transformer
      abapStageMapping      = curTable.getCopyForSAPMapping();
		lastStageCreateResult = createAbapStageAndTransformer(abapStageMapping, tableIndex, curRFCDestination, 
		                                                      curRFCProgramId, jtABAPExtract);

		newTransformerStage = lastStageCreateResult.getStageData();
		transformerNode     = lastStageCreateResult.getGraphJobNode();

		// ******************
		// OUTPUT: ODBC stage
		// ******************
		newPersStage = StageFactory.createPersistenceTargetStage(jobDef, curTable, 
		                                                         jtABAPExtract.getPersistenceData(), 
		                                                         getServiceToken());

		// ******************************************************
		// create link Transformer Stage <------> ODBC Stage
		// ******************************************************
		stageLink = createColumnMapping(newTransformerStage, newPersStage, curTable, 
		                                newPersStage.getLinkParams(), dsFactory);

		// update JobStructureData ...
		// ===========================
		odbcLink = new GraphJobLink(stageLink, new GraphJobNode(newPersStage));
		transformerNode.addOutLink(odbcLink);

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.exit();
		}
	} // end of createExtractFlow()


	public Map<String,String> getABAPCodeMap() {
		return (_ABAPCodeResultMap);
	} // end of getABAPCodeMap()


	protected TableData[] getTargetTableArr() {
		return (_TrgTableDataArr);
	} // end of getTargetTableArr()

	protected void validate() throws BaseException {
		RequestJobTypeABAPExtract jtABAPExtract;
		RFCData                   rfcData;
		int                       minRFCDestinationsCount;

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.entry();
		}

		jtABAPExtract = (RequestJobTypeABAPExtract) getJobType();
		rfcData       = jtABAPExtract.getRFCData();

		// check if ABAP tables exist ...
		_TrgTableDataArr = loadRequiredTables(jtABAPExtract.getPhysicalModelId());
		if (_TrgTableDataArr == null) {
			// -----------------------------
			// ==> NO tables to be processed
			// -----------------------------
			if (TraceLogger.isTraceEnabled()) {
				TraceLogger.trace(TraceLogger.LEVEL_FINE, "No tables found to be processed"); //$NON-NLS-1$
			}
		} 
		else {
			if (rfcData.getEnabled()) {
				// determine the minimum of RFCDestinations
				if (!jtABAPExtract.getRFCData().getGenerateRFCDestinationNames()) {
					if (_TrgTableDataArr.length > jtABAPExtract.getFlowNumberPerJob()) {
						// there are more ABAP Extractions than flows
						minRFCDestinationsCount = jtABAPExtract.getFlowNumberPerJob();
					} 
					else {
						// there must only be the number of ABAPExtractipons
						minRFCDestinationsCount = _TrgTableDataArr.length;
					} // end of (else) if (_TrgTableDataArr.length > jtABAPExtract.getFlowNumberPerJob())

					if (TraceLogger.isTraceEnabled()) {
						TraceLogger.trace(TraceLogger.LEVEL_FINE, "number of flows per job = " + jtABAPExtract.getFlowNumberPerJob()); //$NON-NLS-1$
						TraceLogger.trace(TraceLogger.LEVEL_FINE, "number of extracts      = " + _TrgTableDataArr.length); //$NON-NLS-1$
						TraceLogger.trace(TraceLogger.LEVEL_FINE, "min number of RFC Destinations = " + minRFCDestinationsCount); //$NON-NLS-1$
						TraceLogger.trace(TraceLogger.LEVEL_FINE, "cur number of RFC Destinations = " + rfcData.getDestinationsMap().size()); //$NON-NLS-1$
					}

					// the number of RFC destinations must be greater or equal than the number of flows
					if (rfcData.getDestinationsMap().size() < minRFCDestinationsCount) {
						if (TraceLogger.isTraceEnabled()) {
							TraceLogger.trace(TraceLogger.LEVEL_FINE, "Error: Number of RFC Dest Ids (" + rfcData.getDestinationsMap().size()  //$NON-NLS-1$
									+ ") < Number of flows per Job (" + jtABAPExtract.getFlowNumberPerJob() + ")."); //$NON-NLS-1$ //$NON-NLS-2$
						}

						throw new JobGeneratorException("101800E",  //$NON-NLS-1$
								new String[] { String.valueOf(RequestJobTypeABAPExtract.XML_ATTRIB_N_FLOWS_PER_JOB) } );
					} // end of if (rfcData.getDestinationsMap().size() < minRFCDestinationsCount)
				}
			} 
			else {
				// TODO CPIC
			}
		} // end of (else) if (_TrgTableDataArr == null && _TrgTableDataArr.length == 0)

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.exit();
		}
	} // end of validate()

} // end of class ABAPExtractJob
