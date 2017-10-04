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
// Module Name : com.ibm.is.sappack.gen.jobgenerator.servlets
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.jobgenerator.servlets;


import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.is.sappack.gen.common.BaseException;
import com.ibm.is.sappack.gen.common.Constants;
import com.ibm.is.sappack.gen.common.StopWatch;
import com.ibm.is.sappack.gen.common.request.ErrorResult;
import com.ibm.is.sappack.gen.common.request.GetAllFoldersRequest;
import com.ibm.is.sappack.gen.common.request.GetAllFoldersResponse;
import com.ibm.is.sappack.gen.common.request.GetAllParameterSetsRequest;
import com.ibm.is.sappack.gen.common.request.GetAllParameterSetsResponse;
import com.ibm.is.sappack.gen.common.request.GetAllProjectsRequest;
import com.ibm.is.sappack.gen.common.request.GetAllProjectsResponse;
import com.ibm.is.sappack.gen.common.request.GetAllSapConnectionsRequest;
import com.ibm.is.sappack.gen.common.request.GetAllSapConnectionsResponse;
import com.ibm.is.sappack.gen.common.request.JobGeneratorRequest;
import com.ibm.is.sappack.gen.common.request.JobGeneratorRequestResult;
import com.ibm.is.sappack.gen.common.request.RequestBase;
import com.ibm.is.sappack.gen.common.request.ResponseBase;
import com.ibm.is.sappack.gen.common.request.ServerRequestUtil;
import com.ibm.is.sappack.gen.common.request.ValidateDataRequest;
import com.ibm.is.sappack.gen.common.request.ValidateDataResponse;
import com.ibm.is.sappack.gen.common.trace.TraceLogger;
import com.ibm.is.sappack.gen.common.util.DSFolder;
import com.ibm.is.sappack.gen.common.util.DSProject;
import com.ibm.is.sappack.gen.common.util.StringUtils;
import com.ibm.is.sappack.gen.server.common.service.ServiceToken;
import com.ibm.is.sappack.gen.server.common.util.DSAccessException;
import com.ibm.is.sappack.gen.server.common.util.ISVersionChecker;
import com.ibm.is.sappack.gen.server.datastage.DataStageAccessManager;
import com.ibm.is.sappack.gen.server.jobgenerator.FolderLister;
import com.ibm.is.sappack.gen.server.jobgenerator.JobGenerator;
import com.ibm.is.sappack.gen.server.jobgenerator.ParameterSetLister;
import com.ibm.is.sappack.gen.server.jobgenerator.ProjectLister;
import com.ibm.is.sappack.gen.server.jobgenerator.SapConnectionLister;


/**
 * Servlet implementation class for Servlet: JobGeneratorServlet
 */
public class JobGeneratorServlet extends HttpServlet implements Servlet {

	private static final long   serialVersionUID   = -1;
	

	static String copyright() {
		return com.ibm.is.sappack.gen.jobgenerator.servlets.Copyright.IBM_COPYRIGHT_SHORT;
	}

	
	/* (non-Java-doc)
	* @see javax.servlet.http.HttpServlet#HttpServlet()
	*/
	public JobGeneratorServlet() {
		super();

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.entry();
		}

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.exit();
		}
	}


	public void init() throws ServletException {

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.entry();
		}

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.exit();
		}
	}


	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out;
		String      versionInfoRMRG;
		String      versionInfoIS;

		// get RM/RG version information
		versionInfoRMRG = StringUtils.getVersionInfoString();

		// get IS family version information
		versionInfoIS = ISVersionChecker.checkISEnvironmentVersion();

      versionInfoRMRG = versionInfoRMRG + Constants.NEWLINE + Constants.NEWLINE + versionInfoIS;
		out = response.getWriter();
		out.println(versionInfoRMRG);
		out.close();
	}


	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		TraceLogger.refreshCachedSettings();

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.entry();
		}

		boolean      successfulExecution = true;
		RequestBase  serverRequest       = null;
      ResponseBase requestResult       = null;
		StopWatch stopWatchAll           = new StopWatch(true);

		try {
			Map<String, String> vNewParamMap = new HashMap<String, String>();
			String vValue;
			String vParamName = "";

			// 'decode' all parameters in the HTTP POST parameter map and 
			// store it in a new map
			Enumeration vParamNameEnum = request.getParameterNames();
			
			while (vParamNameEnum.hasMoreElements()) {
				vParamName = (String) vParamNameEnum.nextElement();
				vValue     = (String) request.getParameter(vParamName);

				vNewParamMap.put(vParamName, vValue);
			} // end of while (vParamNameEnum.hasMoreElements())

			TraceLogger.trace(TraceLogger.LEVEL_FINE, "Received parameter map from HTTP request");    //$NON-NLS-1$
			// create a new JobRequestInfo and new JobGenerator instance ...
			//			jobRequestInfo = new JobRequestInfo(request);

			String versionInfo = StringUtils.getVersionInfoString();
			TraceLogger.trace(TraceLogger.LEVEL_INFO, versionInfo);

			if (TraceLogger.isTraceEnabled())
				TraceLogger.trace(TraceLogger.LEVEL_FINER, "Creating job request object...");         //$NON-NLS-1$

			serverRequest = ServerRequestUtil.createRequestFromHTTPParameterMap(vNewParamMap);
			if (TraceLogger.isTraceEnabled()) {
				TraceLogger.trace(TraceLogger.LEVEL_FINER, 
				                  "Job request object created: " + serverRequest.getClass().getName()); //$NON-NLS-1$
			}
			
			
			if (serverRequest instanceof JobGeneratorRequest) {
				requestResult = generateJobs((JobGeneratorRequest) serverRequest);
			} else if (serverRequest instanceof GetAllProjectsRequest) {
				requestResult = sendAllProjects((GetAllProjectsRequest) serverRequest);
			} else if (serverRequest instanceof GetAllSapConnectionsRequest) {
			   requestResult = sendAllSapConnections((GetAllSapConnectionsRequest) serverRequest);
			} else if (serverRequest instanceof GetAllFoldersRequest) {
			   requestResult = sendAllFolders((GetAllFoldersRequest) serverRequest);
         } else if (serverRequest instanceof GetAllParameterSetsRequest) {
            requestResult = sendAllParameterSets((GetAllParameterSetsRequest) serverRequest);
         } else if (serverRequest instanceof ValidateDataRequest) {
            requestResult = validateProjectData((ValidateDataRequest) serverRequest);
			} else {
				throw new UnsupportedOperationException();
			}
		} // end of try 
		catch (Throwable excpt) {
		   String excptCauseMsg;
		   
			if (TraceLogger.isTraceEnabled()) {
				TraceLogger.traceException(excpt);
			}
			else {
	         excpt.printStackTrace();
			}
			
			if (serverRequest == null) {
	         requestResult = new ErrorResult(this.getClass());
			}
			else {
	         requestResult = new ErrorResult(serverRequest.getClass());
			}
			   
	      requestResult.setException(excpt);
	      if (excpt instanceof BaseException) {
	         excptCauseMsg = excpt.getClass().getName();
	      }
	      else {
	         excptCauseMsg = excpt.toString();
	      }
	      requestResult.addMessage("100100E", new String[] { InetAddress.getLocalHost().getHostName(),  //$NON-NLS-1$ 
	                                                         excptCauseMsg },
	                               ResponseBase.MESSAGE_TYPE_ERROR);
			successfulExecution = false;
		}
		finally {
		   stopWatchAll.stop();

         if (TraceLogger.isTraceEnabled()) {
            TraceLogger.trace(TraceLogger.LEVEL_FINEST, 
                              "Request processing overall runtime = " + stopWatchAll.toString() + ".");     //$NON-NLS-1$
         }
		}

		// trace result
		ResponseBase.traceResponse(requestResult);

		// set result into HTTP response field ...
		try {
	      response.setHeader(Constants.REQUEST_RESULT, ServerRequestUtil.encode(requestResult.toXML()));
		}
		catch(Exception excpt) {
		   System.err.println("Cannot answer client request. Error encoding request Result: ");           //$NON-NLS-1$
		   excpt.printStackTrace();
		}

      // we have to write the ZIP file here since the header must be set before
      if (serverRequest instanceof JobGeneratorRequest) {
         
         if (requestResult instanceof JobGeneratorRequestResult) {
            
            JobGeneratorRequestResult jobGenResult = (JobGeneratorRequestResult) requestResult;
            
            if (jobGenResult.getABAPCodeBytes() != null) {
               response.setContentType("image/zip");                    //$NON-NLS-1$
               response.getOutputStream().write(jobGenResult.getABAPCodeBytes());
               response.getOutputStream().flush();
            }
         }
      } // end of if (serverRequest instanceof JobGeneratorRequest)
      
      // and finally flush all buffers
      response.getOutputStream().flush();
		response.flushBuffer();
		
		if (TraceLogger.isTraceEnabled()) {
			if (successfulExecution) {
				TraceLogger.exit("result = " + requestResult.get1stMessage());                  //$NON-NLS-1$
			} else {
				TraceLogger.exit("error result = " + requestResult.get1stMessage());            //$NON-NLS-1$
			}
		}
	}

	
	ResponseBase generateJobs(JobGeneratorRequest jobRequest) 
	             throws IOException  {
	   
      JobGeneratorRequestResult jobGenResult = null;
		
      JobGenerator jobGenerator = null;
		try {
	      jobGenerator = new JobGenerator((JobGeneratorRequest) jobRequest);
	      
	      if (TraceLogger.isTraceEnabled()) {
	         TraceLogger.trace(TraceLogger.LEVEL_FINER, "Job generator instance has been created.");  //$NON-NLS-1$
	      }
	      
		   jobGenResult = jobGenerator.createJobs();
		} // end of try
		catch(BaseException baseExcpt) {
         jobGenResult = new JobGeneratorRequestResult(jobRequest);
         jobGenResult.setException(baseExcpt);
		}
		finally {
		   if (jobGenerator != null) {
		      try {
		         jobGenerator.cleanup();
		      }
		      catch(Exception excpt) {
		         if (TraceLogger.isTraceEnabled()) {
		            TraceLogger.traceException(excpt);
		         }
		      }
		   }
		}

		return(jobGenResult);
	} // end of generateJobs()


	/**
	 * sendAllSapConnections
	 * 
	 * get all DataStage SAP R/3 connections of a DataStage project
	 * 
	 * @param gaRequest
	 * @param response
	 * @return
	 */
	ResponseBase sendAllSapConnections(GetAllSapConnectionsRequest gaRequest) {
		
      StopWatch stopWatch = new StopWatch(true);
      
		TraceLogger.entry();
		
      GetAllSapConnectionsResponse reqResponse = new GetAllSapConnectionsResponse(gaRequest);
      
      try {
         /* get all SAP connections */
         SapConnectionLister sapConnectionLister = new SapConnectionLister(gaRequest, reqResponse);
         Map sapConnectionsMap = sapConnectionLister.getSAPConnections();
         reqResponse.setConnectionsMap(sapConnectionsMap);
         
         reqResponse.addMessage("00005I", String.valueOf(sapConnectionsMap.size()),       //$NON-NLS-1$
                                ResponseBase.MESSAGE_TYPE_INFO);
      }
      catch(Exception excpt) {
         reqResponse.setException(excpt);
      }
		
      stopWatch.stop();
      TraceLogger.trace(TraceLogger.LEVEL_FINEST, "'Send All SAP Connections' runtime = " + stopWatch.toString() + "."); //$NON-NLS-1$
		TraceLogger.exit();
		
      return reqResponse;
	}

	
	/**
	 * sendAllFolders
	 * 
	 * get all DataStage folders for a given DataStage project
	 * 
	 * @param gaRequest
	 * @param response
	 * @return
	 */
	ResponseBase sendAllFolders(GetAllFoldersRequest gaRequest) {
		
      StopWatch stopWatch = new StopWatch(true);
      
      TraceLogger.entry();
      
      GetAllFoldersResponse reqResponse = new GetAllFoldersResponse(gaRequest);
      
      try {
         // get DataStage folders
         FolderLister folderLister = new FolderLister(gaRequest, reqResponse);
         List<DSFolder> folders = folderLister.getAllFoldersSorted();
         reqResponse.setFolders(folders);
         reqResponse.addMessage("00003I", String.valueOf(folders.size()),                    //$NON-NLS-1$
                                ResponseBase.MESSAGE_TYPE_INFO);
         
      }
      catch(Exception excpt) {
         reqResponse.setException(excpt);
      }
		
      stopWatch.stop();
      TraceLogger.trace(TraceLogger.LEVEL_FINEST, "'Send All Folders' runtime = " + stopWatch.toString() + "."); //$NON-NLS-1$
      TraceLogger.exit();
      
		return reqResponse;
	}
	
	
   /**
    * sendAllParameterSets
    * 
    * get all DataStage parameter sets for a given DataStage project
    * 
    * @param gaRequest
    * @param response
    * @return
    */
   ResponseBase sendAllParameterSets(GetAllParameterSetsRequest gaRequest) {
      
      StopWatch stopWatch = new StopWatch(true);
      
      TraceLogger.entry();
      
      GetAllParameterSetsResponse reqResponse = new GetAllParameterSetsResponse(gaRequest);
      
      try {
         // get DataStage folders
         ParameterSetLister paramSetLister = new ParameterSetLister(gaRequest, reqResponse);
         List paramSetList = paramSetLister.getAllParameterSets();
         reqResponse.setParameterSets(paramSetList);
         reqResponse.addMessage("00006I", String.valueOf(paramSetList.size()),                    //$NON-NLS-1$
                                ResponseBase.MESSAGE_TYPE_INFO);
         
      }
      catch(Exception excpt) {
         reqResponse.setException(excpt);
      }
      
      stopWatch.stop();
      TraceLogger.trace(TraceLogger.LEVEL_FINEST, "'Send All ParameterSets' runtime = " + stopWatch.toString() + "."); //$NON-NLS-1$
      TraceLogger.exit();
      
      return reqResponse;
   }
   
   
	ResponseBase sendAllProjects(GetAllProjectsRequest gaRequest) { 

      StopWatch stopWatch = new StopWatch(true);
      
		TraceLogger.entry();
		
      GetAllProjectsResponse reqResponse = new GetAllProjectsResponse(gaRequest);
      try {
         ProjectLister projLister = new ProjectLister(gaRequest, reqResponse);
         List<String> projects = projLister.getAllProjectsSorted();
         reqResponse.setProjects(projects);
         reqResponse.addMessage("00004I", String.valueOf(projects.size()),                   //$NON-NLS-1$
                                ResponseBase.MESSAGE_TYPE_INFO);
         
      }
      catch(Exception excpt) {
         reqResponse.setException(excpt);
      }
		
		stopWatch.stop();
      TraceLogger.trace(TraceLogger.LEVEL_FINEST, "'Send All Projects' runtime = " + stopWatch.toString() + "."); //$NON-NLS-1$
		TraceLogger.exit();
		
		return reqResponse;
	}

	
   /**
    * This method validates some data like 'project exists ?', 'folder exists ?', etc ...
    * 
    * @param request   validation request
    * 
    * @return validation request result
    */
   ResponseBase validateProjectData(ValidateDataRequest request) {
      
      DataStageAccessManager dsAccessMgr;
      ServiceToken           srvcToken;
      DSFolder               dsFolder;
      DSProject              dsProject;
      StopWatch              stopWatch;
      String                 folderName;
      String                 hostName;
      String                 projectName;
      String                 jobNamePrefix;
      Integer                dsRPCPort;
      
      stopWatch = null;
      if (TraceLogger.isTraceEnabled()) {
         stopWatch = new StopWatch(true);
         TraceLogger.entry();
      }
      
      ValidateDataResponse reqResponse = new ValidateDataResponse(request);
      
      srvcToken = null;
      try {
         dsAccessMgr   = DataStageAccessManager.createInstance();
         hostName      = request.getDSHostName();
         jobNamePrefix = request.getDSJobPrefix();
         dsRPCPort     = request.getDSServerRPCPort();
         
         // login into IS server first
         srvcToken = dsAccessMgr.createServiceToken(request.getLocale());
         
         srvcToken.loginIntoDomain(request.getISUsername(), request.getISPassword(), 
                                   request.getDomainServerName(), request.getDomainServerPort());

         // check if the project exists ...
         projectName = request.getDSProjectName();

         if (TraceLogger.isTraceEnabled()) {
            TraceLogger.trace(TraceLogger.LEVEL_FINER,
                              "Validating project: " + hostName + ":" +   //$NON-NLS-1$ //$NON-NLS-2$ 
                              dsRPCPort + "/" + projectName +             //$NON-NLS-3$
                              " - job name prefix: " + jobNamePrefix);    //$NON-NLS-3$
         }

         reqResponse.setSuccessful(true);
         
         // first get the DS Server version ...
         reqResponse.setDSServerVersion(dsAccessMgr.getDSVersion());
         
         // and then check if the project exists ...
         dsProject = dsAccessMgr.getDSProject(hostName, dsRPCPort, projectName, srvcToken);
         
         if (dsProject == null) {
            reqResponse.addMessage("100300E", new String[]  { projectName, hostName },       //$NON-NLS-1$
                                   ResponseBase.MESSAGE_TYPE_ERROR);
            reqResponse.setSuccessful(false);
         }
         else {
            folderName = request.getDSFolder();
            
            if (TraceLogger.isTraceEnabled()) {
               TraceLogger.trace(TraceLogger.LEVEL_FINEST, "Validating folder: " + folderName); //$NON-NLS-1$
            }
            
            if (folderName != null) {
               // check if the folder exists ...
               dsFolder = dsAccessMgr.getDSFolder(dsProject, folderName, srvcToken);
               
               if (dsFolder == null) {
                  reqResponse.addMessage("100400E", new String[]  { folderName, projectName },  //$NON-NLS-1$
                                         ResponseBase.MESSAGE_TYPE_ERROR);
                  reqResponse.setSuccessful(false);
               }
            } // end of if (folderName != null) {
         } // end of if (dsProject == null)
       
         // check if job name prefix already exists ...
         if (jobNamePrefix != null) {
            srvcToken.setDSProject(dsProject);
            
            if (TraceLogger.isTraceEnabled()) {
               TraceLogger.trace(TraceLogger.LEVEL_FINER, "Check job name prefix: " + jobNamePrefix +    //$NON-NLS-1$
                                                           "ns = " + srvcToken.getProjectNameSpace()); //$NON-NLS-1$
            }
         	boolean jobsExist = dsAccessMgr.doJobsWithPrefixExist(jobNamePrefix, srvcToken);

            if (TraceLogger.isTraceEnabled()) {
               TraceLogger.trace(TraceLogger.LEVEL_FINEST, "Job Name prefix is in use: " + jobsExist); //$NON-NLS-1$
            }
            if (jobsExist) {
               reqResponse.addMessage("108200E", jobNamePrefix, ResponseBase.MESSAGE_TYPE_ERROR); //$NON-NLS-1$
               reqResponse.setSuccessful(false);
            }
         }
      } // end of try
      catch(DSAccessException pDSAccessExcpt) {
         if (TraceLogger.isTraceEnabled()) {
            TraceLogger.traceException(pDSAccessExcpt);
         }
         
         reqResponse.setException(pDSAccessExcpt);
      } // end of catch(DSAccessException pDSAccessExcpt)
      finally {
         try {
            if (srvcToken != null) {
               srvcToken.logoutFromDomain();
            }
         }
         catch(DSAccessException pDSAccessExcpt) {
            
            if (TraceLogger.isTraceEnabled()) {
               TraceLogger.traceException(pDSAccessExcpt);
            }
            
            reqResponse.setException(pDSAccessExcpt);
         } // end of catch(DSAccessException pDSAccessExcpt)
      } // end of finally
      
      if (TraceLogger.isTraceEnabled()) {
         stopWatch.stop();
         TraceLogger.trace(TraceLogger.LEVEL_FINEST, "'Validation' runtime = " + stopWatch.toString() + ".");  //$NON-NLS-1$
         TraceLogger.exit("Validation result: " + reqResponse.isSuccessful());                                 //$NON-NLS-1$
      }
      
      return reqResponse;
   } // end of validateProjectData()
   
} // end of class JobGeneratorServlet
