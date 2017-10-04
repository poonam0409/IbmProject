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


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.ibm.is.sappack.gen.common.BaseException;
import com.ibm.is.sappack.gen.common.Constants;
import com.ibm.is.sappack.gen.common.JobGeneratorException;
import com.ibm.is.sappack.gen.common.StopWatch;
import com.ibm.is.sappack.gen.common.request.JobGeneratorRequest;
import com.ibm.is.sappack.gen.common.request.JobGeneratorRequestResult;
import com.ibm.is.sappack.gen.common.request.RequestJobType;
import com.ibm.is.sappack.gen.common.request.RequestJobTypeABAPExtract;
import com.ibm.is.sappack.gen.common.request.RequestJobTypeIDocExtract;
import com.ibm.is.sappack.gen.common.request.RequestJobTypeIDocLoad;
import com.ibm.is.sappack.gen.common.request.RequestJobTypeIDocMIHLoad;
import com.ibm.is.sappack.gen.common.request.RequestJobTypeMIHCodeTableLoad;
import com.ibm.is.sappack.gen.common.request.RequestJobTypeMIHLoad;
import com.ibm.is.sappack.gen.common.request.RequestJobTypeMovement;
import com.ibm.is.sappack.gen.common.request.RequestJobTypeTranscoding;
import com.ibm.is.sappack.gen.common.request.ResponseBase;
import com.ibm.is.sappack.gen.common.trace.TraceLogger;
import com.ibm.is.sappack.gen.common.util.DSFolder;
import com.ibm.is.sappack.gen.common.util.DSProject;
import com.ibm.is.sappack.gen.server.common.service.ServiceToken;
import com.ibm.is.sappack.gen.server.common.util.DSAccessException;
import com.ibm.is.sappack.gen.server.common.util.DSAccessNotAllowedException;
import com.ibm.is.sappack.gen.server.datastage.DataStageAccessManager;
import com.ibm.is.sappack.gen.server.util.ModelInfoBlock;
import com.ibm.is.sappack.gen.server.util.TableData;


public class JobGenerator
{
   // -------------------------------------------------------------------------------------
   //                                       Constants
   // -------------------------------------------------------------------------------------
   
   // -------------------------------------------------------------------------------------
   //                                 Member Variables
   // -------------------------------------------------------------------------------------
   private ServiceToken                 _SrvcToken;
   private JobGeneratorRequest          _JobRequest;
   private Map<String, ModelInfoBlock>  _PhysModelMap;

   
   static String copyright() { 
      return com.ibm.is.sappack.gen.server.jobgenerator.Copyright.IBM_COPYRIGHT_SHORT; 
   }   
   
   
   public JobGenerator(JobGeneratorRequest jobReq) throws JobGeneratorException, 
                                                          DSAccessException {
      _JobRequest   = jobReq;
      _PhysModelMap = new HashMap<String, ModelInfoBlock>();
      initTableMap();
      init();
   }


   private void initTableMap() throws JobGeneratorException {
	   TraceLogger.entry();

	   Map<String,String> models = this._JobRequest.getAdditionalParametersMap();

	   Iterator<Map.Entry<String,String>> mapIter = models.entrySet().iterator();
	   while(mapIter.hasNext()) 
	   {
		   Map.Entry<String,String> physModelEntry = mapIter.next();
		   String physModel                        = physModelEntry.getValue();
		   TraceLogger.trace(TraceLogger.LEVEL_FINER, "Found physical model: " + physModelEntry.getKey());
		   
		   if (physModel.length() == 0) {
	         TraceLogger.trace(TraceLogger.LEVEL_FINE, "Model Data are missing.");

	         throw new JobGeneratorException("100500E", 
	                                         new String[] { physModelEntry.getKey().toString() } );
		   }

		   ModelInfoBlock curInfoBlk = TableData.loadDataModel(physModel);
		   _PhysModelMap.put(physModelEntry.getKey(), curInfoBlk);
	   }

      TraceLogger.exit();
   }


   private void init() throws DSAccessException
   {
      StopWatch              stopWatch;
      
      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.entry();
      }
      
      // create and start the stop watch
      stopWatch = new StopWatch(true);
      
      // create the access manager singleton
      DataStageAccessManager.createInstance();

      // ... and initialize the DS environment
      initDSEnvironement();
      
      // stop the stop watch
      stopWatch.stop();
      
      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.exit("Job Generator init runtime = " + stopWatch.toString() + ".");
      }
   } // end of init()

   
   private void initDSEnvironement() throws DSAccessException
   {
      DataStageAccessManager accessManager;
      DSProject              curDSProject;
      DSFolder               curDSFolder;
      
      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.entry();
      }
      
      // create the request context
      _SrvcToken = DataStageAccessManager.getInstance().createServiceToken(_JobRequest.getLocale());
      
      // and login into domain server
      _SrvcToken.loginIntoDomain(_JobRequest.getISUsername(), _JobRequest.getISPassword(), 
                                 _JobRequest.getDomainServerName(), _JobRequest.getDomainServerPort());

      accessManager = DataStageAccessManager.getInstance();

      // check if the project exists
      try
      {
         curDSProject = accessManager.getDSProject(_JobRequest.getDSHostName(), _JobRequest.getDSServerRPCPort(), 
                                                   _JobRequest.getDSProjectName(), _SrvcToken);

         if (curDSProject == null)
         {
            throw new DSAccessException("100300E",                  //$NON-NLS-1$
                                         new String[] { _JobRequest.getDSProjectName(),   
                                                        _JobRequest.getDSHostName() } );
         }
      } // end of try
      catch(DSAccessNotAllowedException pAccessNotAllowedExcpt)
      {
         throw new DSAccessException("100200E",                  //$NON-NLS-1$
                                     new String[] { _JobRequest.getISUsername(),   
                                                    _JobRequest.getDSProjectName() }, 
                                     pAccessNotAllowedExcpt );
      }

      // check if the folder exists
      curDSFolder = accessManager.getDSFolder(curDSProject, _JobRequest.getDSTargetFolderName(), 
                                              _SrvcToken);

      if (curDSFolder == null)
      {
         throw new DSAccessException("100400E",                  //$NON-NLS-1$
                                     new String[] { _JobRequest.getDSTargetFolderName(),   
                                                    _JobRequest.getDSProjectName() } );
      }

      // save DSProject and DSFolder in the access token
      _SrvcToken.setDSProject(curDSProject);
      _SrvcToken.setDSFolder(curDSFolder);

      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.exit("project = " + curDSProject.getName() + " - folder = " + curDSFolder.getName());
      }
   } // end of initDSEnvironement() 


   public void cleanup() throws DSAccessException
   {
      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.entry();
      }
      
      if (_SrvcToken != null)
      {
         _SrvcToken.logoutFromDomain();
         _SrvcToken = null;
      }
      
      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.exit("Cleanup completed.");    //$NON-NLS-1$
      }
   } // end of cleanup()
   
   
   public JobGeneratorRequestResult createJobs() throws BaseException          
   {
      RequestJobType            curJobType;
      ABAPExtractJob            abapExtractJob;
      IDocExtractJob            idocExtractJob;
      IDocLoadJob               idocLoadJob;
      MovementJob               movementJob;
      TranscodingJob            transcodingJob;
      MIHCodeTablesLoadJob      mihCodeTableLoadJob;
      MIHLoadJob                mihLoadJob;
      IDocMIHLoadJob            idocMIHLoadJob;
      JobGeneratorRequestResult jobGenResult;
      StopWatch                 stopWatch;
      String                    failedJobInfo;
      List<String>              failedJobsList;
      List<String>              intermediateJobsList;
      List<String>              successfulJobsList;
      List                      warnings;
      Map<String,String>        abapCodeMap;
      Map<String,String>        curCodeMap;
      Iterator                  jobTypeIter;
      int                       nJobsSuccess;
      int                       nJobsCreated;
      int                       nMovementJobsCreated;
      int                       nTranscodingJobsCreated;
      int                       nMIHJobsCreated;
      int                       nIDocLoadJobsCreated;
      int                       nIDocExtractJobsCreated;
      int                       nLogTablesExtractJobsCreated;
      byte                      abapCodeAsByteArr[];

      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.entry("job types count = " + _JobRequest.getJobTypes().size());
      }

      // create and start the stop watch
      stopWatch = new StopWatch(true);
      
      // perform the job generation dependent on the passed job type
      failedJobsList               = new ArrayList<String>();
      successfulJobsList           = new ArrayList<String>();
      nJobsSuccess                 = 0;
      nJobsCreated                 = 0;
      nIDocExtractJobsCreated      = 0;
      nIDocLoadJobsCreated         = 0;
      nLogTablesExtractJobsCreated = 0;
      nMIHJobsCreated              = 0;
      nMovementJobsCreated         = 0;
      nTranscodingJobsCreated      = 0;
      abapCodeMap                  = new HashMap<String,String>();
      warnings                     = new ArrayList<String>();

      jobTypeIter = _JobRequest.getJobTypes().iterator();
      while(jobTypeIter.hasNext())
      {
         curJobType = (RequestJobType) jobTypeIter.next();
         
         try
         {
            switch(curJobType.getJobType())
            {
               case RequestJobType.TYPE_ABAP_EXTRACT_ID:
                    abapExtractJob = new ABAPExtractJob(_SrvcToken, 
                                                        (RequestJobTypeABAPExtract) curJobType, 
                                                        _JobRequest,
                                                        _PhysModelMap);
                    
                    // as there can be multiple job types per job generation request
                    // we add the names of successfully generated ABAP extraction jobs
                    // to the overall successful job list
                    intermediateJobsList = abapExtractJob.create();
                    successfulJobsList.addAll(intermediateJobsList);
                    nJobsCreated = intermediateJobsList.size();
                    nLogTablesExtractJobsCreated += nJobsCreated;
                    curCodeMap                = abapExtractJob.getABAPCodeMap();
                    abapCodeMap.putAll(curCodeMap);
                    break;
                    
               case RequestJobType.TYPE_IDOC_EXTRACT_ID:
                    idocExtractJob = new IDocExtractJob(_SrvcToken, 
                                                        (RequestJobTypeIDocExtract) curJobType, 
                                                        _JobRequest,
                                                        _PhysModelMap); 

                    // as there can be multiple job types per job generation request
                    // we add the names of successfully generated IDoc extraction jobs
                    // to the overall successful job list
                    intermediateJobsList = idocExtractJob.create();
                    successfulJobsList.addAll(intermediateJobsList);
                    nJobsCreated = intermediateJobsList.size();
                    nIDocExtractJobsCreated += nJobsCreated;
                    break;
                    
               case RequestJobType.TYPE_IDOC_LOAD_ID:
                    idocLoadJob = new IDocLoadJob(_SrvcToken, 
                                                  (RequestJobTypeIDocLoad) curJobType, 
                                                  _JobRequest,
                                                  _PhysModelMap); 

                    // as there can be multiple job types per job generation request
                    // we add the names of successfully generated IDoc load jobs
                    // to the overall successful job list
                    intermediateJobsList = idocLoadJob.create();
                    successfulJobsList.addAll(intermediateJobsList);
                    nJobsCreated = intermediateJobsList.size();
                    nIDocLoadJobsCreated += nJobsCreated;
                    break;
                    
               case RequestJobType.TYPE_IDOC_MIH_LOAD_ID:
                    idocMIHLoadJob   = new IDocMIHLoadJob(_SrvcToken, 
                                                          (RequestJobTypeIDocMIHLoad) curJobType, 
                                                          _JobRequest,
                                                          _PhysModelMap); 

                    // as there can be multiple job types per job generation request
                    // we add the names of successfully generated IDoc MIH load jobs
                    // to the overall successful job list
                    intermediateJobsList = idocMIHLoadJob.create();
                    successfulJobsList.addAll(intermediateJobsList);
                    nJobsCreated = intermediateJobsList.size();
                    nMIHJobsCreated += nJobsCreated;
                    break;
                    
               case RequestJobType.TYPE_MIH_LOAD_ID:
                    mihLoadJob  = new MIHLoadJob(_SrvcToken, (RequestJobTypeMIHLoad) curJobType, 
                                                 _JobRequest,
                                                 _PhysModelMap); 

                    // as there can be multiple job types per job generation request
                    // we add the names of successfully generated MIH load jobs
                    // to the overall successful job list
                    intermediateJobsList = mihLoadJob.create();
                    successfulJobsList.addAll(intermediateJobsList);
                    nJobsCreated = intermediateJobsList.size();
                    nMIHJobsCreated += nJobsCreated;
                    break;
                    
               case RequestJobType.TYPE_MOVEMENT_ID:
                    movementJob  = new MovementJob(_SrvcToken, (RequestJobTypeMovement) curJobType, 
                                                   _JobRequest,
                                                   _PhysModelMap);

                    // as there can be multiple job types per job generation request
                    // we add the names of successfully generated movement jobs
                    // to the overall successful job list
                    intermediateJobsList = movementJob.create();
                    successfulJobsList.addAll(intermediateJobsList);
                    nJobsCreated = intermediateJobsList.size();
                    nMovementJobsCreated += nJobsCreated;
                    break;
                    
               case RequestJobType.TYPE_TRANSCODING_ID:
                    transcodingJob  = new TranscodingJob(_SrvcToken, (RequestJobTypeTranscoding) curJobType, 
                                                         _JobRequest, _PhysModelMap);

                   // as there can be multiple job types per job generation request
                   // we add the names of successfully generated transcoding jobs
                   // to the overall successful job list
                   intermediateJobsList = transcodingJob.create();
                   successfulJobsList.addAll(intermediateJobsList);
                   warnings = transcodingJob.getGenerationWarnings();
                   nJobsCreated = intermediateJobsList.size();
                   nTranscodingJobsCreated += nJobsCreated;
                   break;
                    
               case RequestJobType.TYPE_MIH_CODETABLE_LOAD_ID:
                    mihCodeTableLoadJob = new MIHCodeTablesLoadJob(_SrvcToken, 
                                                                   (RequestJobTypeMIHCodeTableLoad) curJobType, 
                                                                    _JobRequest,
                                                                    _PhysModelMap);

                    // as there can be multiple job types per job generation request
                    // we add the names of successfully generated movement jobs
                    // to the overall successful job list
                    intermediateJobsList = mihCodeTableLoadJob.create();
                    successfulJobsList.addAll(intermediateJobsList);
                    nJobsCreated     = intermediateJobsList.size();
                    nMIHJobsCreated += nJobsCreated;
                    curCodeMap       = mihCodeTableLoadJob.getABAPCodeMap();
                    abapCodeMap.putAll(curCodeMap);
                    break;
                  
               default:
                    if (TraceLogger.isTraceEnabled())
                    {
                       TraceLogger.trace(TraceLogger.LEVEL_FINE, "Unkown Request Job Type '" 
                                                               + curJobType.getJobType() + "'. Ignored.");
                    }
            } // end of switch(curJobType.getJobType())
            
            nJobsSuccess += nJobsCreated;
         } // end of try
         catch(JobGeneratorException parJobGenExcpt)
         {
            if (TraceLogger.isTraceEnabled())
            {
               TraceLogger.traceException(parJobGenExcpt); 
               TraceLogger.trace(TraceLogger.LEVEL_FINE, 
                                 "Continue On Job Failure = " + _JobRequest.doContinueOnError());
            }

            if (!_JobRequest.doContinueOnError())
            {
               throw parJobGenExcpt;
            }
            
            // create the 'Failed Job Info'
            failedJobInfo = MessageFormat.format(Constants.JOB_FAILED_OUTPUT_TEMPLATE, 
                                                 new Object[] { curJobType.getJobName(),
                                                                parJobGenExcpt.getMessage()
                                                              });
            
            failedJobsList.add(failedJobInfo);
         } // end of catch(JobGeneratorException parJobGenExcpt)
      } // end of while(jobTypeIter.hasNext())
      
      // create a ZIP file from the ABAP Code (map)
      abapCodeAsByteArr = createZipFileFromABAPCode(abapCodeMap);
      
      // create Job Generation result ...
      jobGenResult = new JobGeneratorRequestResult(_JobRequest);
      jobGenResult.setSuccessfulJobsList(successfulJobsList);
      jobGenResult.setFailedJobsList(failedJobsList);
      
      // different message if there was no job was created ...
      if (successfulJobsList.size() == 0 && failedJobsList.size() == 0)
      {
         jobGenResult.addMessage("00002I", (String) null, ResponseBase.MESSAGE_TYPE_INFO);     //$NON-NLS-1$
      }
      else
      {
      	String folderName = com.ibm.is.sappack.gen.common.Constants.DS_JOBS_DEFAULT_FOLDER;
      	if (_JobRequest.getDSTargetFolderName() != null) {
      		folderName = _JobRequest.getDSTargetFolderName();
      	}
      	
         // osuhre, 47039
         jobGenResult.addMessage("00001I", new String [] { _JobRequest.getDSHostName(),        //$NON-NLS-1$
                                                           _JobRequest.getDSProjectName(),
                                                           folderName,
                                                           Integer.toString(nJobsSuccess),
                                                           Integer.toString(failedJobsList.size())
                                                          },
                                 ResponseBase.MESSAGE_TYPE_INFO);
         
         // add warnings
         if(warnings.size() > 0) {
        	 jobGenResult.addMessage(Constants.NEWLINE, ResponseBase.MESSAGE_TYPE_WARNING);
        	 jobGenResult.addMessage("001000W", new String [] {}, ResponseBase.MESSAGE_TYPE_WARNING);
         }
         for(int i=0; i<warnings.size(); i++) {
        	 jobGenResult.addMessage((String)warnings.get(i), ResponseBase.MESSAGE_TYPE_WARNING);
        	 
         }
         if(warnings.size() > 0) {
        	 jobGenResult.addMessage(Constants.NEWLINE, ResponseBase.MESSAGE_TYPE_WARNING);
         }
        
         
         jobGenResult.setByteArray(abapCodeAsByteArr);
         jobGenResult.setIDocExtractJobNumber(Integer.toString(nIDocExtractJobsCreated));
         jobGenResult.setIDocLoadJobNumber(Integer.toString(nIDocLoadJobsCreated));
         jobGenResult.setLogicalTablesExtractJobNumber(Integer.toString(nLogTablesExtractJobsCreated));
         jobGenResult.setMIHLoadJobNumber(Integer.toString(nMIHJobsCreated));
         jobGenResult.setMovementJobNumber(Integer.toString(nMovementJobsCreated));
      }

      // stop the stop watch
      stopWatch.stop();
      
      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.trace(TraceLogger.LEVEL_FINEST, "Jobs have been created");
         TraceLogger.exit("Job creation runtime = " + stopWatch.toString()  + ".");    //$NON-NLS-1$
      }

      return(jobGenResult);
   } // end of createJobs()
   
   private byte[] createZipFileFromABAPCode(Map parABAPCodeMap)
   {
      Entry                 mapEntry;
      Iterator              mapIter;
      CRC32                 checksumCalculator;
      ByteArrayOutputStream byteArrOutStream;
      ZipOutputStream       zipOutStream;
      ZipEntry              zipEntry;
      String                zipDataString;
      byte                  zipFileArr[];

      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.entry("map size = " + parABAPCodeMap.size());
      }
      
      if (parABAPCodeMap.size() == 0)
      {
         // nothing to process !!
         zipFileArr = new byte[0];
      }
      else
      {
         // create needed output streams ...
         byteArrOutStream = new ByteArrayOutputStream();
         zipOutStream     = new ZipOutputStream(byteArrOutStream);
         
         try
         {
            // process all entries of the map
            mapIter = parABAPCodeMap.entrySet().iterator();
            while(mapIter.hasNext())
            {
               mapEntry      = (Entry) mapIter.next();
               zipDataString = (String) mapEntry.getValue();
               
               // create a new ZIP entry
               zipEntry = new ZipEntry((String) mapEntry.getKey());
               zipOutStream.putNextEntry(zipEntry);

               // calculate CRC and put the map value into the ZIP entry
               checksumCalculator = new CRC32();
               checksumCalculator.update(zipDataString.getBytes());
               zipOutStream.write(zipDataString.getBytes());
               
               zipEntry.setSize(zipDataString.length());
               zipEntry.setCrc(checksumCalculator.getValue());

               // close the entry
               zipOutStream.closeEntry();
            } // end of while(mapIter.hasNext())
            
            // close the streams
            zipOutStream.close();
            zipFileArr = byteArrOutStream.toByteArray();
         } // end of try
         catch(IOException pIOExcpt)
         {
            if (TraceLogger.isTraceEnabled())
            {
               TraceLogger.traceException(pIOExcpt);
            }
            zipFileArr = new byte[0];
         } // end of catch(IOException pIOExcpt)
      } // end of (else) if (parABAPCodeMap.size() == 0)
      
      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.exit("zip file array len = " + zipFileArr.length);
      }
      
      return(zipFileArr);
   } // end of createZipFileFromABAPCode()
   
} // end of class JobGenerator
