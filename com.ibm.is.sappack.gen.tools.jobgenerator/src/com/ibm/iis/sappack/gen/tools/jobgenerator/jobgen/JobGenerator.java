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
// Module Name : com.ibm.iis.sappack.gen.tools.jobgenerator.jobgen
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.iis.sappack.gen.tools.jobgenerator.jobgen;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;

import com.ibm.iis.sappack.gen.common.ui.connections.IISConnection;
import com.ibm.iis.sappack.gen.common.ui.connections.SapSystem;
import com.ibm.iis.sappack.gen.common.ui.iisclient.IISClient;
import com.ibm.iis.sappack.gen.common.ui.util.Utils;
import com.ibm.iis.sappack.gen.tools.jobgenerator.rgconf.RGConfiguration;
import com.ibm.iis.sappack.gen.tools.jobgenerator.rgconf.RGConfiguration.CONFIGURATION_TYPE;
import com.ibm.is.sappack.gen.common.BaseException;
import com.ibm.is.sappack.gen.common.Constants;
import com.ibm.is.sappack.gen.common.JobGeneratorException;
import com.ibm.is.sappack.gen.common.SAPAccessException;
import com.ibm.is.sappack.gen.common.request.JobGeneratorRequest;
import com.ibm.is.sappack.gen.common.request.JobGeneratorRequestResult;
import com.ibm.is.sappack.gen.common.request.JobParamData;
import com.ibm.is.sappack.gen.common.request.RequestJobType;
import com.ibm.is.sappack.gen.common.request.RequestJobTypeABAPExtract;
import com.ibm.is.sappack.gen.common.request.RequestJobTypeIDocExtract;
import com.ibm.is.sappack.gen.common.request.RequestJobTypeIDocLoad;
import com.ibm.is.sappack.gen.common.request.RequestJobTypeMovement;
import com.ibm.is.sappack.gen.common.request.RequestJobTypeTranscoding;
import com.ibm.is.sappack.gen.common.request.ServerRequestUtil;
import com.ibm.is.sappack.gen.common.request.SupportedTableTypesMap;
import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.common.ui.ModeManager;
import com.ibm.is.sappack.gen.common.ui.preferences.AdvancedSettingsPreferencePage;
import com.ibm.is.sappack.gen.common.ui.util.DSProjectUtils;
import com.ibm.is.sappack.gen.common.util.StringUtils;
import com.ibm.is.sappack.gen.tools.jobgenerator.Activator;
import com.ibm.is.sappack.gen.tools.jobgenerator.wizard.helpers.AbapCodeUploader;
import com.ibm.is.sappack.gen.tools.jobgenerator.wizard.helpers.JobGenerationSummaryCollector;


public class JobGenerator {
  	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	

	public interface IPostJobGenerationAction {
		void performPostJobGenerationActions(IFile sourceDBMFile, IFile targetDBMFile, 
		                                     JobGeneratorRequestResult jobGenResponse, JobGenerationSummaryCollector summary, 
		                                     JobGenerationSettings settings, IProgressMonitor monitor) throws BaseException;
	}

	private static final int MAX_REPORTS_PER_OUTPUT_LINE = 4;

	private static void throwPostGenerationException(String messageTemplate, Exception e) 
	        throws JobGeneratorException, SAPAccessException {
		String msg;
		
		e.printStackTrace();

		if (e instanceof SAPAccessException) {
			msg = MessageFormat.format(messageTemplate, e.getLocalizedMessage());
//			Activator.getLogger().log(Level.SEVERE, msg, e);   // will be logged later
			throw (SAPAccessException) e;
		}
		else {
			msg = MessageFormat.format(messageTemplate, e.getMessage());
//			Activator.getLogger().log(Level.SEVERE, msg, e);   // will be logged later
			throw new JobGeneratorException(msg, e);
		}
	}

	static class ABAPUploadAction implements IPostJobGenerationAction {

		@Override
		public void performPostJobGenerationActions(IFile sourceDBM, IFile targetDBM, JobGeneratorRequestResult jobGenResponse, 
		                                            JobGenerationSummaryCollector summaryCollector, JobGenerationSettings settings, 
		                                            IProgressMonitor monitor)
				throws BaseException {
			InputStream inputStream = new ByteArrayInputStream(jobGenResponse.getABAPCodeBytes());
			byte[] content = null;
			try {
				content = com.ibm.is.sappack.gen.tools.sap.utilities.Utilities.readInputStream(inputStream);

			} catch (IOException e) {
				String msg = Messages.ABAPExtractConfigurationPageGroup_1;
				throwPostGenerationException(msg, e);
			}

			StringBuffer resultBuffer = summaryCollector.getSummaryBuffer();
			summaryCollector.setABAPProgramsUploaded(false);
			if (content.length == 0) {
				String msg = Messages.ABAPExtractConfigurationPageGroup_2;

				resultBuffer.append(msg);
				resultBuffer.append(Constants.NEWLINE);
				Activator.getLogger().log(Level.WARNING, msg);
			} else {
				boolean saveAbapPrograms = settings.isSaveABAPPrograms();
				if (saveAbapPrograms) {
					String dbmFilePath = targetDBM.getLocation().toOSString();
					String zipFileName = dbmFilePath.substring(0, dbmFilePath.lastIndexOf('.')) + ".zip"; //$NON-NLS-1$
					try {
						Utils.saveZip(content, zipFileName);
						targetDBM.getProject().refreshLocal(IResource.DEPTH_INFINITE, null);
					} catch (Exception e) {
						String msg = Messages.ABAPExtractConfigurationPageGroup_3;
						throwPostGenerationException(msg, e);
					}
					summaryCollector.setABAPCodeZipFilename(zipFileName);
				}

				boolean uploadAbapPrograms = settings.isUploadABAPPrograms();
				if (uploadAbapPrograms) {
					AbapCodeUploader abapCodeUploader = null;
					List<String> uploadList = null;
					int nUploadedReports;
					SapSystem sapSystem = settings.getAbapUploadConnection();
					boolean useCTS = settings.isUseCTS();
					
					if (useCTS) {
						String ctsRequest = settings.getCTSRequest();
						String ctsPackage = settings.getCTSPackage();
						String ctsRequestDesc = settings.getCTSRequestDescription();
						boolean createSeparateTransports = settings.getcreateSeparateTransports();
						abapCodeUploader = new AbapCodeUploader(sapSystem, ctsPackage, ctsRequest, ctsRequestDesc, content, monitor, createSeparateTransports);	
					} else {
						abapCodeUploader = new AbapCodeUploader(sapSystem, content, monitor);
					}
					try {
						Activator.getLogger().log(Level.FINE, Messages.JobGenerator_0);
						abapCodeUploader.uploadAbapReports();
						Activator.getLogger().log(Level.FINE, Messages.JobGenerator_1);
						uploadList = abapCodeUploader.getReportList();
					} catch (Exception e) {
						String msg = Messages.ABAPExtractConfigurationPageGroup_4;
						throwPostGenerationException(msg, e);
					}
					summaryCollector.setABAPProgramsUploaded(true);

					nUploadedReports = uploadList.size();
					resultBuffer.append(MessageFormat.format(Messages.ABAPExtractConfigurationPageGroup_5, nUploadedReports));
					if (nUploadedReports == 0) {
						resultBuffer.append(Constants.NEWLINE);
					} else {

						for (int idx = 0; idx < nUploadedReports; idx++) {

							if (idx % MAX_REPORTS_PER_OUTPUT_LINE == 0) {
								resultBuffer.append(Constants.NEWLINE);
							} else {
								resultBuffer.append(", "); //$NON-NLS-1$
							}

							resultBuffer.append(uploadList.get(idx));
						}

						resultBuffer.append(Constants.NEWLINE);
					}
				} // end of if (uploadAbapPrograms) {
			} // end of (else) if (nUploadedReports == 0)

		}

	}

	IRunnableContext                      runnableContext = null;
	private JobGenerationSummaryCollector summaryCollector;
	private JobGenerationSettings         settings;
	private RGConfiguration               generatorConfig;


	public JobGenerator(RGConfiguration generatorConfiguration) { //, IRunnableContext runnableContext) {
		this.generatorConfig = generatorConfiguration;
		//		this.runnableContext = runnableContext;
	}

	public JobGenerationSummaryCollector getSummaryCollector() {
		return this.summaryCollector;
	}

	public void performJobGeneration(JobGenerationSettings jobGenSettings, IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

		Activator.getLogger().log(Level.INFO, StringUtils.getVersionInfoString());
		this.settings = jobGenSettings;

		this.summaryCollector = new JobGenerationSummaryCollector();

		IRunnableWithProgress runnable = new IRunnableWithProgress() {

			@Override
			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
				try {
					IISConnection iisConnection = settings.getIisConnection();

					final String jobNamePrefix = settings.getJobNamePrefix();

					if (monitor == null) {
						monitor = new NullProgressMonitor();
					}
					monitor.beginTask(Messages.JobGeneratorWizard_3 + jobNamePrefix, IProgressMonitor.UNKNOWN);

					// create the JobRequest ...
					final JobGeneratorRequest jobGenRequest = new JobGeneratorRequest();
					boolean generateABAPExtractJobs = false;

					IISConnection.initializeRequest(jobGenRequest, iisConnection);

					jobGenRequest.setDSTargetFolderName(settings.getDsFolder());

					jobGenRequest.setJobDescription(generatorConfig.getJobDescription());

					// update request with project name, DS hostname and DS RPC ports
					// (possibly contained in the project name)
					DSProjectUtils.updateRequestData(iisConnection.getDomain(), settings.getDsProject(), jobGenRequest);

					//////////////////////////////////////////////////////////////////////
					// fill request by job type
					String sourceModelFileId = null;
					IFile sourceDBMFile = settings.getSourceDBMFile();
					String sourceModelContents = null;
					if (sourceDBMFile != null) {
						// we set the id to the full path
						sourceModelFileId = sourceDBMFile.getLocation().toOSString();
						sourceModelContents = JobGeneratorRequest.readFile(new File(sourceModelFileId));
						sourceModelContents = replaceABAPProgramNames(sourceModelContents, sourceDBMFile.getName());
					}
					String targetModelFileId = null;
					IFile targetDBMFile = settings.getTargetDBMFile();
					String targetModelContents = null;
					if (targetDBMFile != null) {
						targetModelFileId = targetDBMFile.getLocation().toOSString();
						targetModelContents = JobGeneratorRequest.readFile(new File(targetModelFileId));
						targetModelContents = replaceABAPProgramNames(targetModelContents, targetDBMFile.getName());
					}

					Map<String, String> columnDerivationMap = generatorConfig.getCustomDerivations();
					Map<String, String> derivationExceptions = generatorConfig.getDerivationExceptions();

					int objectTypes = settings.getObjectTypes();

					List<RequestJobType> requestTypes = new ArrayList<RequestJobType>();
					if (generatorConfig.getConfigurationType() == CONFIGURATION_TYPE.SAP_EXTRACT) {
						jobGenRequest.addModel(targetModelFileId, targetModelContents);
						if ((objectTypes & JobGenerationSettings.OBJECT_TYPE_ALL_TABLES) != 0) {
							// ABAP extract
							final String JOB_NAME_SUFFIX_ABAP_EXTRACT = "_Abap_Extract"; //$NON-NLS-1$
							String jobName = jobNamePrefix + JOB_NAME_SUFFIX_ABAP_EXTRACT;
							RequestJobTypeABAPExtract jobType = new RequestJobTypeABAPExtract(jobName, targetModelFileId);
							SupportedTableTypesMap tableTypesMap = new SupportedTableTypesMap();
							if ((objectTypes & JobGenerationSettings.OBJECT_TYPE_TABLES_REF_AND_TEXT) != 0) {
								tableTypesMap.add(SupportedTableTypesMap.TABLE_TYPE_ENUM_REFERENCE_CHECK_TABLE);
								tableTypesMap.add(SupportedTableTypesMap.TABLE_TYPE_ENUM_REFERENCE_TEXT_TABLE);
							}
							if ((objectTypes & JobGenerationSettings.OBJECT_TYPE_TABLES_DATA) != 0) {
								tableTypesMap.add(SupportedTableTypesMap.TABLE_TYPE_ENUM_LOGICAL_TABLE);
							}
							if ((objectTypes & JobGenerationSettings.OBJECT_TYPE_TABLES_OTHER) != 0) {
								tableTypesMap.add(SupportedTableTypesMap.TABLE_TYPE_ENUM_NON_REFERENCE_CHECK_TABLE);
							}
							jobType.setSupportedTableTypes(tableTypesMap);
							jobType.setSAPSystem(generatorConfig.getSourceSAPSystem());
							jobType.setPersistenceData(generatorConfig.getTargetPersistenceData());

							boolean cpicEnabled = (generatorConfig.getABAPTransferMethod() & Constants.ABAP_TRANSFERMETHOD_CPIC) != 0;
							jobType.setCPICEnabled(cpicEnabled);
							jobType.setCPICUser(generatorConfig.getCPICUser());
							jobType.setCPICPassword(generatorConfig.getCPICPassword());
							jobType.setCPICUseSAPLogonDetails(generatorConfig.getCPICUseSAPLogonDetails());

							
							jobType.setRetryCount(settings.getNumberOfRetries());
							jobType.setRetryInterval(settings.getRetryInterval());
							jobType.setSuppressBackgroundJob(settings.getSuppressBackgroundJob());
							
							jobType.setCreateRFCDestination(generatorConfig.getCreateRFCDestinationAutomatically());
							jobType.setDeleteExistingRFCDestination(generatorConfig.getDeleteExistingRFCDestination());

							jobType.setGenerateRFCDestinationNames(generatorConfig.getGenerateRFCDestinationNames());
							jobType.setRFCDestinationNameGeneratorPrefix(generatorConfig.getRFCDestinationNameGeneratorPrefix());
							if (cpicEnabled) {
								jobType.setFlowNumberPerJob(generatorConfig.getNumberOfFlows_CPIC());
							} else {
								jobType.setFlowNumberPerJob(generatorConfig.getNumberOfFlows_RFC());
							}
							jobType.setRFCEnabled((generatorConfig.getABAPTransferMethod() & Constants.ABAP_TRANSFERMETHOD_RFC) != 0);
							if (generatorConfig.getUseBackground()) {
								jobType.setBackgroundProcessOptions(generatorConfig.getBGVariantPrefix(), generatorConfig.getBGStartDelay(),
								                                    generatorConfig.getBGUseSAPVariant(), generatorConfig.getBGUseSAPVariant());
							}
							jobType.setRFCGateway(generatorConfig.getGWHost(), generatorConfig.getGWService());

							Map<String, String> rfcDestMap = new HashMap<String, String>();
							generatorConfig.getStaticRFCDestinations(rfcDestMap);
							jobType.setRFCDestinationProgramIdMap(rfcDestMap);
							jobType.setColumnDerivationMap(columnDerivationMap);
							jobType.setDerivationExceptionsMap(derivationExceptions);
							
							requestTypes.add(jobType);
							generateABAPExtractJobs = true;
						}
						if ((objectTypes & JobGenerationSettings.OBJECT_TYPE_IDOC) != 0) {
							final String JOB_NAME_SUFFIX_IDOC_EXTRACT = "_IDoc_Extract"; //$NON-NLS-1$
							String jobName = jobNamePrefix + JOB_NAME_SUFFIX_IDOC_EXTRACT;
							RequestJobTypeIDocExtract jobType = new RequestJobTypeIDocExtract(jobName, targetModelFileId);
							jobType.setSAPSystem(generatorConfig.getSourceSAPSystem());
							jobType.setPersistenceData(generatorConfig.getTargetPersistenceData());
							jobType.setColumnDerivationMap(columnDerivationMap);
							jobType.setDerivationExceptionsMap(getDerivationExceptions4IDocs(derivationExceptions));

							requestTypes.add(jobType);
						}
					} else if (generatorConfig.getConfigurationType() == CONFIGURATION_TYPE.SAP_LOAD) {
						jobGenRequest.addModel(sourceModelFileId, sourceModelContents);

						if ((objectTypes & JobGenerationSettings.OBJECT_TYPE_IDOC) != 0) {
							final String JOB_NAME_SUFFIX_IDOC_LOAD = "_IDoc_Load"; //$NON-NLS-1$
							String jobName = jobNamePrefix + JOB_NAME_SUFFIX_IDOC_LOAD;
							RequestJobTypeIDocLoad jobType = new RequestJobTypeIDocLoad(jobName, sourceModelFileId, settings.getIdocMessageType());
							jobType.setPersistenceData(generatorConfig.getSourcePersistenceData());
							jobType.setSAPSystem(generatorConfig.getTargetSAPSystem());
							if (generatorConfig.getIDocLoadStatus()) {
								jobType.setIDOCLoadStatus(generatorConfig.getIDocLoadStatusSurrogateKeyFile(), generatorConfig.getIDocLoadStatusObjectName());
							}
							jobType.setColumnDerivationMap(columnDerivationMap);
							jobType.setDerivationExceptionsMap(getDerivationExceptions4IDocs(derivationExceptions));

							requestTypes.add(jobType);
						}
					} else if (generatorConfig.getConfigurationType() == CONFIGURATION_TYPE.MOVE) {
						
						RequestJobTypeMovement jobType = null;
						
						/* Create Transcoding job if trancoding options are selected - otherwise create a Move job */
						if(generatorConfig.isTranscodingConfig()) {
							/* Transcoding Job specific */
							final String JOB_NAME_SUFFIX_TRANSCODING = "_Transcoding"; //$NON-NLS-1$
							String jobName = jobNamePrefix + JOB_NAME_SUFFIX_TRANSCODING;
							RequestJobTypeTranscoding tcJobType = new RequestJobTypeTranscoding(jobName);

							tcJobType.setTranscodeReferenceFields(generatorConfig.isTranscodeReferenceFields());
							tcJobType.setTranscodeNonReferenceFields(generatorConfig.isTranscodeNonReferenceFields());
							tcJobType.setTranscodeDomainValueFields(generatorConfig.isTranscodeDomainValueFields());
							tcJobType.setMarkUnmappedValues(generatorConfig.isMarkUnmappedValues());
							tcJobType.setTargetLegacyID(generatorConfig.getTargetSystemLegacyID());
							String checkTablesModelFileId = null;
							IFile checkTablesDBMFile = settings.getCheckTablesDBMFile();
							String checkTablesModelContents = null;
							if (checkTablesDBMFile != null) {
								// we set the id to the full path
								checkTablesModelFileId   = checkTablesDBMFile.getLocation().toOSString();
								checkTablesModelContents = JobGeneratorRequest.readFile(new File(checkTablesModelFileId));
								tcJobType.setCTPhysicalModelId(checkTablesModelFileId);
								jobGenRequest.addModel(checkTablesModelFileId, checkTablesModelContents);
							}

							tcJobType.setColumnDerivationMap(columnDerivationMap);
							tcJobType.setDerivationExceptionsMap(derivationExceptions);
							jobType = tcJobType;
						}
						else {
							/* Move Job specific */
							final String JOB_NAME_SUFFIX_MOVE = "_Move"; //$NON-NLS-1$
							String jobName = jobNamePrefix + JOB_NAME_SUFFIX_MOVE;
							jobType = new RequestJobTypeMovement(jobName, sourceModelFileId, targetModelFileId);

							jobType.setColumnDerivationMap(columnDerivationMap);
							jobType.setDerivationExceptionsMap(derivationExceptions);
						}

						jobGenRequest.addModel(sourceModelFileId, sourceModelContents);
						jobType.setSrcPhysicalModelId(sourceModelFileId);
						jobGenRequest.addModel(targetModelFileId, targetModelContents);
						jobType.setTrgPhysicalModelId(targetModelFileId);
						jobType.setSrcPersistenceData(generatorConfig.getSourcePersistenceData());
						jobType.setTrgPersistenceData(generatorConfig.getTargetPersistenceData());
						jobType.setRejectFilePath(generatorConfig.getRejectFilePath());
						jobType.setFlowNumberPerJob(generatorConfig.getNumberOfFlows_Move());
						requestTypes.add(jobType);
					}

					//////////////////////////////////////////////////////////////////////
					// set values for all job requests

					final List<IPostJobGenerationAction> postGenerationActions = new ArrayList<JobGenerator.IPostJobGenerationAction>();

					if (generateABAPExtractJobs) {
						postGenerationActions.add(new ABAPUploadAction());
					}
					
					for (RequestJobType jobType : requestTypes) {
						jobType.setDoOverwriteJob(settings.isOverwriteJob());
						jobGenRequest.addJobType(jobType);
					}
					Iterator<JobParamData> jobParams = generatorConfig.getJobParams().iterator();
					while (jobParams.hasNext()) {
						jobGenRequest.addJobParameter(jobParams.next());
					}

					jobGenRequest.setCreateV7Stage(ModeManager.generateV7Stages());

					/////////////////////////////////////////////////////////////////////////
					// create and schedule background jobs

					final Job dsCompileJob = createCompileWizardJob();

					JobGeneratorRequestResult response = (JobGeneratorRequestResult) ServerRequestUtil.send(jobGenRequest);
					for (int idx = 0; idx < response.getMessages().length; idx++) {
						summaryCollector.getSummaryBuffer().append(response.getMessages()[idx]);
						summaryCollector.getSummaryBuffer().append(Constants.NEWLINE);
					}

					if (response.containsErrors()) {
						StringBuffer msgBuf = new StringBuffer();

						for (int idx = 0; idx < response.getMessages().length; idx++) {
							msgBuf.append(response.getMessages()[idx]);
							msgBuf.append(Constants.NEWLINE);
						}
						String msg = MessageFormat.format(Messages.JobGeneratorWizard_4, response.getDetailedInfo());
						Activator.getLogger().log(Level.SEVERE, msg);

						throw new JobGeneratorException(msgBuf.toString());
					}

					summaryCollector.getSuccessfulJobList().clear();
					summaryCollector.getSuccessfulJobList().addAll(response.getSuccessfulJobsList());
					boolean isJobGenerationCanceled = monitor.isCanceled(); 

//					try {
						if (!isJobGenerationCanceled) {
							// no error occurred and user haven't pressed 'CANCEL' ==> continue with PostJobGeneration actions
							Iterator<IPostJobGenerationAction> actionIter = postGenerationActions.iterator();
							while(actionIter.hasNext() && !isJobGenerationCanceled) {
								IPostJobGenerationAction pga = actionIter.next();
								pga.performPostJobGenerationActions(sourceDBMFile, targetDBMFile, response, summaryCollector, settings, monitor);
								isJobGenerationCanceled = monitor.isCanceled(); 
							}
						}
						monitor.done();

						if (isJobGenerationCanceled) {
							summaryCollector.addAdditionalMessage(Messages.JobGeneratorWizard_9);
						}
						else {
							if (dsCompileJob != null) {
								dsCompileJob.schedule();
							}
						}
//					}
//					catch(JobGeneratorException jobGenExcpt) {
//						monitor.done();
//						summaryCollector.addAdditionalMessage(jobGenExcpt.getLocalizedMessage());
//					}

					// start DS Multiple job compiler if selected
					if (!isJobGenerationCanceled && dsCompileJob != null) {
						dsCompileJob.schedule();
					}
				} 
//				catch (JobGeneratorException jobGenExcpt) {
//					Throwable excptCause;
//					String    msg;
//
//					Activator.getLogger().log(Level.SEVERE, jobGenExcpt.getMessage(), jobGenExcpt);
//					excptCause = jobGenExcpt.getCause();
//					if (excptCause != null) {
//						msg = excptCause.getLocalizedMessage();
//						summaryCollector.getErrorBuffer().append(msg);
//						summaryCollector.getErrorBuffer().append(Constants.NEWLINE);
////						summaryCollector.getErrorBuffer().delete(0, summaryCollector.getErrorBuffer().length()).append(msg);
//					}
//					msg = jobGenExcpt.getLocalizedMessage();
//					summaryCollector.getErrorBuffer().append(msg);
//				}
				catch (Exception e) {
					String msg;

					if (e instanceof BaseException) {
						msg = e.getLocalizedMessage();
					} else {
						msg = e.getMessage();
					}

					Activator.getLogger().log(Level.SEVERE, msg, e);
					summaryCollector.getErrorBuffer().delete(0, summaryCollector.getErrorBuffer().length()).append(msg);
					throw new InvocationTargetException(e);
				}
			}
		};

		if (runnableContext == null) {
			if (monitor == null) {
				monitor = new NullProgressMonitor();
			}
			runnable.run(monitor);
		} else {
			runnableContext.run(true, true, runnable);
		}
	}

	private Map<String,String> getDerivationExceptions4IDocs(Map<String,String> derivationExceptions) {
		Map<String,String>             resultMap;
		Iterator<Entry<String,String>> mapIter;           
		boolean                        admColsExist;

		// check if here is an ADM column in the derivation map
		mapIter      = derivationExceptions.entrySet().iterator();
		admColsExist = false;
		while(mapIter.hasNext() && !admColsExist) {
			Entry<String,String> mapEntry = mapIter.next();

			if (mapEntry.getKey().indexOf(Constants.IDOC_TECH_FLD_ADM_DOCNUM_NAME) > -1 ||
				 mapEntry.getKey().indexOf(Constants.IDOC_TECH_FLD_ADM_SEGNUM_NAME) > -1 ||
				 mapEntry.getKey().indexOf(Constants.IDOC_TECH_FLD_ADM_PSGNUM_NAME) > -1) {
				admColsExist = true;
			}
		}

		if (admColsExist) {
			// there are already the ADM columns in the map ==> use it as it is
			resultMap = derivationExceptions;
		}
		else {
			// there are no ADM columns in the map ==> add the columns
			resultMap = new HashMap<String, String>(derivationExceptions);
			resultMap.put(Constants.IDOC_TECH_FLD_ADM_DOCNUM_NAME, Constants.DERIVATION_REPLACEMENT);
			resultMap.put(Constants.IDOC_TECH_FLD_ADM_SEGNUM_NAME, Constants.DERIVATION_REPLACEMENT);
			resultMap.put(Constants.IDOC_TECH_FLD_ADM_PSGNUM_NAME, Constants.DERIVATION_REPLACEMENT);
		}

		return(resultMap);
	}

	private Job createCompileWizardJob() {

		Job dsCompileWizardJob = null;

		// launch the DataStage Multiple Job Compiler Wizard
		if (settings.isLaunchJobCompiler()) {

			IISConnection iisConnection = settings.getIisConnection();
			// prepare the different connection variables required for the
			// wizard
			final String infoServerDomain = iisConnection.getDomain() + ":" //$NON-NLS-1$
					+ iisConnection.getDomainServerPort();
			String fullProjectName = settings.getDsProject();
			String[] domainAndProject = Utils.getDomainAndProjectFromFullProjectName(fullProjectName);
			
			final String infoServerHost = domainAndProject[0];
			final String infoServerProject = domainAndProject[1];
			final String infoServerUser = iisConnection.getUser();
			final String infoServerPwd = iisConnection.getPassword();

			final IISClient infoServerClient = new IISClient();
			dsCompileWizardJob = new Job(Messages.JobGeneratorWizard_RunDsMultipleJobCompileWizard) {
				public IStatus run(IProgressMonitor mon) {
					String[] jobsToCompileArray = new String[summaryCollector.getSuccessfulJobList().size()];
					summaryCollector.getSuccessfulJobList().toArray(jobsToCompileArray);

					IStatus result = Status.OK_STATUS;
					try {
						infoServerClient.startAndWaitForMultiJobCompileWizard(infoServerDomain, infoServerHost, infoServerUser, infoServerPwd, infoServerProject, jobsToCompileArray, mon);
					} catch (Throwable e) {
						e.printStackTrace();
						Activator.getLogger().log(Level.WARNING, Messages.LogExceptionMessage, e);
						result = new Status(Status.ERROR, Activator.PLUGIN_ID, MessageFormat.format(Messages.JobGeneratorWizard_2, e.getClass().getName() + ": " //$NON-NLS-1$
								+ e.getMessage()), e);
					}
					return result;
				}

			};
		}

		return dsCompileWizardJob;
	}

	class ProgramNameReplacer implements TableAnnotationTraversal.TableAnnotationVisitor {

		String modelFileName;
		String programNameAnnotName;
		String programCodeAnnotName;

		String oldProgramPrefix;
		String newProgramPrefix;
		int maxLength;

		public ProgramNameReplacer(String modelFileName, String programNameAnnotName, String programCodeAnnotName, String oldProgramPrefix, String newProgramPrefix, int maxLength) {
			super();
			this.modelFileName = modelFileName;
			this.programNameAnnotName = programNameAnnotName;
			this.programCodeAnnotName = programCodeAnnotName;
			this.oldProgramPrefix = oldProgramPrefix;
			this.newProgramPrefix = newProgramPrefix;
			this.maxLength = maxLength;
		}

		String createABAPProgramName(String programPrefix, String modelFileName, int modelNameLength, String tableName, int tableNameLength, int index) {
			modelFileName = StringUtils.cleanFieldName(modelFileName);
			if (modelFileName.length() > modelNameLength) {
				modelFileName = modelFileName.substring(0, modelNameLength);
			}
			tableName = StringUtils.cleanFieldName(tableName);
			if (tableName.length() > tableNameLength) {
				tableName = tableName.substring(0, tableNameLength);
			}

			String name = programPrefix + modelFileName + "_" + tableName + "_" + index; //$NON-NLS-1$ //$NON-NLS-2$
			return name.toUpperCase();
		}

		@Override
		public void visit(String tableName, Element eAnnotationsElement, List<Element> annotations) {
			Element programNameAnnot = null;
			Element programAnnot = null;
			for (Element annot : annotations) {
				String key = annot.getAttribute(com.ibm.is.sappack.gen.tools.jobgenerator.Constants.DBM_ANNOTATION_XML_ATTRIBUTE_KEY);
				if (key.equals(programNameAnnotName)) {
					programNameAnnot = annot;
				} else if (key.equals(programCodeAnnotName)) {
					programAnnot = annot;
				}
			}
			if (programAnnot != null && programNameAnnot != null) {
				String oldABAPProgramName = programNameAnnot.getAttribute(com.ibm.is.sappack.gen.tools.jobgenerator.Constants.DBM_ANNOTATION_XML_ATTRIBUTE_VALUE);
				String newABAPProgramName = newProgramPrefix + oldABAPProgramName.substring(oldProgramPrefix.length());
				if (AdvancedSettingsPreferencePage.isSettingEnabled("RG_SPECIAL_ABAP_PROGRAM_NAME_GEN")) { //$NON-NLS-1$
					// String modelFileName = file.getName().toUpperCase();
					int modelFileNameLength = AdvancedSettingsPreferencePage.getSettingAsInt("RG_SPECIAL_ABAP_PROGRAM_NAME_GEN_MODEL_LENGTH", 2); //$NON-NLS-1$
					int tableNameLength = AdvancedSettingsPreferencePage.getSettingAsInt("RG_SPECIAL_ABAP_PROGRAM_NAME_GEN_TABLE_LENGTH", 4); //$NON-NLS-1$
					int i = 1;
					newABAPProgramName = createABAPProgramName(newProgramPrefix, modelFileName, modelFileNameLength, tableName, tableNameLength, i);
					while (programNamesAlreadyUsed.contains(newABAPProgramName)) {
						i++;
						newABAPProgramName = createABAPProgramName(newProgramPrefix, modelFileName, modelFileNameLength, tableName, tableNameLength, i);
					}

					// tableCounter++;
				}
				programNameAnnot.setAttribute(com.ibm.is.sappack.gen.tools.jobgenerator.Constants.DBM_ANNOTATION_XML_ATTRIBUTE_VALUE, newABAPProgramName);
				programNamesAlreadyUsed.add(newABAPProgramName);

				String programCode = programAnnot.getAttribute(com.ibm.is.sappack.gen.tools.jobgenerator.Constants.DBM_ANNOTATION_XML_ATTRIBUTE_VALUE);
				if (newABAPProgramName.length() > maxLength) {
					String tmp = newABAPProgramName.substring(0, maxLength);
					String msg = Messages.JobGeneratorWizard_12;
					msg = MessageFormat.format(msg, new Object[] { newABAPProgramName, tmp });
					summaryCollector.addAdditionalMessage(msg);
					newABAPProgramName = tmp;
				}
				String newProgramCode = programCode.replaceAll(oldABAPProgramName, newABAPProgramName);
				programAnnot.setAttribute(com.ibm.is.sappack.gen.tools.jobgenerator.Constants.DBM_ANNOTATION_XML_ATTRIBUTE_VALUE, newProgramCode);
			}
		}
	}

	private Set<String> programNamesAlreadyUsed = null;

	private String replaceABAPProgramNames(String modelFileContents, String modelFileName) {
		String originalModelFile = modelFileContents;
		try {
			int objectTypes = settings.getObjectTypes();
			if ((objectTypes & JobGenerationSettings.OBJECT_TYPE_ALL_TABLES) != 0) {
				this.programNamesAlreadyUsed = new HashSet<String>();
				String programNameAnnot = null;
				String programCodeAnnot = null;
				String defaultProgramNamePrefix = null;
				int maxProgramNameLength = -1;

				if (generatorConfig.getABAPTransferMethod() == com.ibm.is.sappack.gen.common.Constants.ABAP_TRANSFERMETHOD_RFC) {
					programNameAnnot = Constants.ANNOT_ABAP_PROGRAM_NAME;
					programCodeAnnot = Constants.ANNOT_ABAP_CODE;
					defaultProgramNamePrefix = com.ibm.is.sappack.gen.tools.sap.constants.Constants.DEFAULT_ABAP_CODE_PREFIX_RFC;
					maxProgramNameLength = com.ibm.is.sappack.gen.tools.sap.constants.Constants.MAX_ABAP_PROGRAMNAME_LENGTH_RFC;
				} else if (generatorConfig.getABAPTransferMethod() == com.ibm.is.sappack.gen.common.Constants.ABAP_TRANSFERMETHOD_CPIC) {
					programNameAnnot = Constants.ANNOT_ABAP_PROGRAM_NAME_CPIC;
					programCodeAnnot = Constants.ANNOT_ABAP_CODE_CPIC;
					defaultProgramNamePrefix = com.ibm.is.sappack.gen.tools.sap.constants.Constants.DEFAULT_ABAP_CODE_PREFIX_CPIC;
					maxProgramNameLength = com.ibm.is.sappack.gen.tools.sap.constants.Constants.MAX_ABAP_PROGRAMNAME_LENGTH_CPIC;
				}

				String newPrefix = settings.getAbapProgramPrefix();
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				// factory.setNamespaceAware(true);
				DocumentBuilder builder = factory.newDocumentBuilder();

				Document doc = null;
				if (newPrefix != null) {
					doc = builder.parse(new ByteArrayInputStream(modelFileContents.getBytes(Constants.STRING_ENCODING)));
					ProgramNameReplacer nameReplacer = new ProgramNameReplacer(modelFileName, programNameAnnot, programCodeAnnot, defaultProgramNamePrefix, newPrefix, maxProgramNameLength);
					TableAnnotationTraversal.traverseDocument(doc, nameReplacer);
				}

				final String additionalCode = settings.getAdditionalABAPCode();
				if (additionalCode != null) {
					if (doc == null) {
						doc = builder.parse(new ByteArrayInputStream(modelFileContents.getBytes(Constants.STRING_ENCODING)));
					}
					final String codeAnnot = programCodeAnnot;
					TableAnnotationTraversal.TableAnnotationVisitor additionalCodeReplacer = new TableAnnotationTraversal.TableAnnotationVisitor() {

						@Override
						public void visit(String tableName, Element eAnnotationsElement, List<Element> annotations) {
							for (Element annot : annotations) {
								String key = annot.getAttribute(com.ibm.is.sappack.gen.tools.jobgenerator.Constants.DBM_ANNOTATION_XML_ATTRIBUTE_KEY);
								if (key.equals(codeAnnot)) {
									String code = annot.getAttribute(com.ibm.is.sappack.gen.tools.jobgenerator.Constants.DBM_ANNOTATION_XML_ATTRIBUTE_VALUE);
									code = code.replaceAll("\\*#ADDITIONAL_ABAP_CODE#", additionalCode); //$NON-NLS-1$
									annot.setAttribute(com.ibm.is.sappack.gen.tools.jobgenerator.Constants.DBM_ANNOTATION_XML_ATTRIBUTE_VALUE, code);
								}
							}
						}
					};
					TableAnnotationTraversal.traverseDocument(doc, additionalCodeReplacer);
				}

				if (doc != null) {
					modelFileContents = serializeNode(doc);
				}

			}
			if (Activator.getLogger().isLoggable(Level.FINE)) {
				String tempFileName = System.getProperty("java.io.tmpdir") + "RGModifiedModel.tmp"; //$NON-NLS-1$ //$NON-NLS-2$
				String tmpMsg = MessageFormat.format("Temp model file written to ''{0}''", tempFileName); //$NON-NLS-1$
				Activator.getLogger().log(Level.FINE, tmpMsg);

				FileOutputStream fos = new FileOutputStream(tempFileName);
				fos.write(modelFileContents.getBytes(Constants.STRING_ENCODING));
				fos.close();
			}
			return modelFileContents;
		} catch (Exception exc) {
			Activator.getLogger().log(Level.SEVERE, Messages.UnexpectedErrorOcurred, exc);
		}
		return originalModelFile;
	}

	static String serializeNode(Document doc) {
		try {
			DOMImplementationRegistry reg = DOMImplementationRegistry.newInstance();
			String feature = ""; //$NON-NLS-1$
			DOMImplementation impl = reg.getDOMImplementation(feature);
			if (impl instanceof DOMImplementationLS) {
				DOMImplementationLS implLS = (DOMImplementationLS) impl;
				LSSerializer ser = implLS.createLSSerializer();
				LSOutput out = implLS.createLSOutput();
				out.setEncoding(Constants.STRING_ENCODING);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				out.setByteStream(baos);
				ser.write(doc, out);
				baos.close();
				byte[] b = baos.toByteArray();
				String s = new String(b, Constants.STRING_ENCODING);
				return s;
			}
		} catch (Exception exc) {
			Activator.getLogger().log(Level.SEVERE, Messages.UnexpectedErrorOcurred, exc);
		}
		return null;
	}
}
