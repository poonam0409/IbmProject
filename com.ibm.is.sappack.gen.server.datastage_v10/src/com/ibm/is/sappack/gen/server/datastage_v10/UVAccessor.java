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
// Module Name : com.ibm.is.sappack.gen.server.datastage_v10
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.server.datastage_v10;


import java.io.File;
import java.rmi.RemoteException;

import com.ibm.iis.ds4j.HostNotRespondingException;
import com.ibm.iis.ds4j.Project;
import com.ibm.iis.ds4j.ProjectOpenFailedException;
import com.ibm.iis.ds4j.Server;
import com.ibm.iis.ds4j.SessionConnectException;
import com.ibm.iis.ds4j.SessionDisconnectException;
import com.ibm.iis.ds4j.SubroutineCallException;
import com.ibm.iis.ds4j.impl.ServerImpl;
import com.ibm.iis.isf.security.auth.AuthorizationService;
import com.ibm.iis.isf.security.directory.DataStageCredential;
import com.ibm.iis.isf.service.ServiceFactory;
import com.ibm.iis.isf.util.security.DSEncryption;
import com.ibm.is.sappack.gen.common.BaseException;
import com.ibm.is.sappack.gen.common.Constants;
import com.ibm.is.sappack.gen.common.trace.TraceLogger;
import com.ibm.is.sappack.gen.common.util.DSProject;
import com.ibm.is.sappack.gen.common.util.StringUtils;
import com.ibm.is.sappack.gen.server.common.DSStageTypeEnum;
import com.ibm.is.sappack.gen.server.common.util.DSAccessException;
import com.ibm.is.sappack.gen.server.common.util.DSAccessJobLockedException;
import com.ibm.is.sappack.gen.server.common.util.DSSapConnectionFileConverter;
import com.ibm.is.sappack.gen.server.common.util.StageTypeNotInstalledException;


public class UVAccessor {
   // -------------------------------------------------------------------------------------
   // Constants
   // -------------------------------------------------------------------------------------
   private static final String DSR_JOB      = "DSR_JOB"; //$NON-NLS-1$
   private static final String DSR_SCHEDULE = "DSR_SCHEDULE"; //$NON-NLS-1$
   private static final String DSR_EXECJOB  = "DSR_EXECJOB"; //$NON-NLS-1$
   private static final String DSR_PROJECT  = "*DataStage*DSR_PROJECT"; //$NON-NLS-1$
   
   private static final int DSR_SUB_JOB_DELETE      = 3;
   private static final int DSR_SUB_SCH_REMOVE      = 2;
   private static final int DSR_SUB_EXE_RELEASE     = 2;
   private static final int DSR_SUB_JOB_RELEASE     = 4;
   private static final int DSR_SUB_JOB_CREATE      = 2;
   private static final int DSR_SUB_PRJ_ISPROTECTED = 23;

   private static final String DS_FILEUTIL = "*DataStage*DS_FILEUTIL"; //$NON-NLS-1$
   private static final String DS_SAPEXEC  = "*DataStage*DSSAPExec"; //$NON-NLS-1$

   private static final String PATH_SEPARATOR = "\\"; //$NON-NLS-1$

   private static final String OK = "0"; //$NON-NLS-1$
   private static final String UNIX = "UNIX"; //$NON-NLS-1$
   private static final String ECHO_DSSAPHOME_UNIX = "echo $DSSAPHOME"; //$NON-NLS-1$
   private static final String EMPTY_STRING = ""; //$NON-NLS-1$

   private static final String SAP_CONNECTIONS_FILE  = "DSSAPConnections" + File.separator + "DSSAPConnections.config"; //$NON-NLS-1$

   
   // -------------------------------------------------------------------------------------
   // Member Variables
   // -------------------------------------------------------------------------------------
	private boolean   connected = false;
	private Server    uvServer  = null;
	private Project   uvProject = null;
   private DSProject dsProject = null;
	

	static String copyright() {
		return com.ibm.is.sappack.gen.server.datastage_v10.Copyright.IBM_COPYRIGHT_SHORT;
	}


   public UVAccessor(DSProject dsProject) {
      if (dsProject == null) {
         throw new IllegalArgumentException("DSProject must not be null.");
      }
      
      this.dsProject = dsProject;
      
      if (TraceLogger.isTraceEnabled()) {
         TraceLogger.trace(TraceLogger.LEVEL_FINEST, "project = " + this.dsProject);
      }
   } // end of UVAccessor()
   
   
	private DataStageCredential getCredentials() throws RemoteException, DSAccessException {
		DataStageCredential dsCredentials = null;

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.entry();
		}

		// returns the dsCredentials for the current user
		AuthorizationService authorizationService = ServiceFactory.getInstance().getService(AuthorizationService.class);
		try {
			dsCredentials = authorizationService.getDataStageCredential(this.dsProject.getHostName());
		}
		catch (Exception unexpectedExcpt) {
			TraceLogger.traceException(unexpectedExcpt);
			throw new DSAccessException("121200E", new String[] { unexpectedExcpt.getMessage() });
		}

		if (TraceLogger.isTraceEnabled()) {
			if (dsCredentials == null) {
				TraceLogger.exit("error");
			}
			else {
				TraceLogger.exit("user = " + dsCredentials.getUserName());
			}
		}

		return dsCredentials;
	}


	public boolean connect() throws RemoteException, DSAccessException {

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.entry();
		}

		if (connected == false) {
			// returns the dsCredentials for the current user
			DataStageCredential dsCredentials = getCredentials();
			if (dsCredentials != null) {
				String dsUser = dsCredentials.getUserName();
				String dsPassword = dsCredentials.getPassword();
				// decrypt password
				dsPassword = DSEncryption.decrypt(dsPassword);

            // connect to universe project
				if (this.dsProject.getDSServerPort() == null) {
				   // use DS default RPC port
	            uvServer = new ServerImpl(this.dsProject.getHostName(), dsUser, dsPassword);
				}
				else {
	            uvServer = new ServerImpl(this.dsProject.getHostName(), this.dsProject.getDSServerPort().intValue(), 
	                                      dsUser, dsPassword);
				}
				
				try {
					uvProject = uvServer.openProject(this.dsProject.getName());
					connected = true;
				}
				catch (ProjectOpenFailedException prjOpenFailedExcpt) {
					if (TraceLogger.isTraceEnabled()) {

						Throwable usedExcpt = prjOpenFailedExcpt.getCause();
						if (usedExcpt == null) {
							usedExcpt = prjOpenFailedExcpt;
						}
						String excptMessage = usedExcpt.getMessage();

						TraceLogger.trace(TraceLogger.LEVEL_FINE, "DSProject open failed: " + excptMessage);
						TraceLogger.traceException(prjOpenFailedExcpt);
					}
				}
				catch(SessionConnectException sessionConnectExcpt) {
					if (TraceLogger.isTraceEnabled()) {
						TraceLogger.traceException(sessionConnectExcpt);
					}
					throw new DSAccessException("121100E", new String[] { sessionConnectExcpt.getMessage() });
				}
				catch (Exception excpt) {
					if (TraceLogger.isTraceEnabled()) {
						TraceLogger.traceException(excpt);
					}

					String excptMessage;
					Throwable usedExcpt = excpt.getCause();

					if (usedExcpt == null) {
						usedExcpt = excpt;
					}
					excptMessage = usedExcpt.getMessage();

               if (excpt instanceof HostNotRespondingException) {
                  throw new DSAccessException("120300E", new String[] { excptMessage });
               }
										
					throw new DSAccessException("120400E", Constants.NO_PARAMS, excpt);
				}
			}
		}

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.exit("connected = " + connected);
		}

		return (connected);
	}

	/**
	 * returns list of job names for the given project
	 * 
	 * @return
	 * @throws ProjectOpenFailedException
	 */
	public String[] getJobNames() throws ProjectOpenFailedException {
		return uvProject.getJobNames();
	}

	/**
	 * delete existing job from associated project within universe
	 * 
	 * @param jobName
	 * @throws SessionNotConnectedError
	 * @throws SubroutineCallException
	 * @throws Exception
	 */
	public void deleteJob(String jobName) throws DSAccessException {

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.entry("job name = " + jobName);
		}

		try {
			Object[] resultArray1 = this.uvProject.callSubroutine(DSR_SCHEDULE, 
			                                                      new Object[] { Integer.toString(DSR_SUB_SCH_REMOVE), 
			                                                                     uvProject.getName(), 
			                                                                     jobName, 
			                                                                     "*" });
			if (resultArray1.length > 1) {
				String errorCode1 = (String) resultArray1[0];
				if (errorCode1.length() != 0) {
					releaseUVJob(jobName);
					throw new DSAccessJobLockedException(jobName);
				}
			}
			Object[] resultArray2 = this.uvProject.callSubroutine(DSR_JOB, 
			                                                      new Object[] { Integer.toString(DSR_SUB_JOB_DELETE), 
			                                                                     jobName });
			
			if (resultArray2.length > 1) {
				Object errorObj = (Object) resultArray2[0];
				if (errorObj instanceof String) {
					String errorCode2 = (String) errorObj;
					if (errorCode2.length() != 0) {
						releaseUVJob(jobName);
						throw new DSAccessJobLockedException(jobName);
					}
				}
				else {
					// if the job has been viewed in the director there are
					// local files than will be deleted once the
					// director is closed
					// we ignore these errors currenlty since the job will be
					// deleted.
				}
			}
		}
		catch (SubroutineCallException e) {
			// this should never fail, exception from the individual actions are
			// caught above in the result arrays
			// exceptions here to catch if api has changed to call the sub
			// routines
			throw new RuntimeException(e.getMessage() == null ? "" : e.getMessage(), e); //$NON-NLS-1$
		}

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.exit();
		}
	}

	/**
	 * release existing job from associated project within universe
	 * 
	 * @param jobName
	 * @throws SessionNotConnectedError
	 * @throws SubroutineCallException
	 */
	public void releaseUVJob(String jobName) {
		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.entry("job name = " + jobName);
		}

		try {
			this.uvProject.callSubroutine(DSR_EXECJOB, 
			                              new Object[] { Integer.toString(DSR_SUB_EXE_RELEASE), jobName,
			                                             "1", "" });

			this.uvProject.callSubroutine(DSR_JOB, 
			                              new Object[] { Integer.toString(DSR_SUB_JOB_RELEASE), 
			                                             jobName });
		} catch (Exception e) {
			// ignore any errors
			if (TraceLogger.isTraceEnabled()) {
				TraceLogger.traceException(e);
			}
		}
		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.exit();
		}
	}

	/**
	 * create new job within associated project.
	 * 
	 * @param fullCategoryName
	 * @throws SubroutineCallException
	 * @throws SessionNotConnectedError
	 * 
	 * @throws JobLockedException
	 */
	public void createJob(String fullCategoryName, String jobName) throws DSAccessException {

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.entry("category = " + fullCategoryName + " - job name = " + jobName);
		}

	   // UV db cannot cope with double slashes in the path ==> replace them by single slashes 
	   String fullJobPath = StringUtils.replaceString(fullCategoryName + PATH_SEPARATOR + jobName, 
	                                                  PATH_SEPARATOR + PATH_SEPARATOR, 
	                                                  PATH_SEPARATOR);
	   
		try {
			Object[] resultArray = this.uvProject.callSubroutine(DSR_JOB, 
			                                                     new Object[] { Integer.toString(DSR_SUB_JOB_CREATE), fullJobPath });
			if (resultArray.length >= 1) {
				String errorCode = (String) resultArray[0];
				
				if (errorCode.length() != 0) {
					throw new DSAccessJobLockedException(jobName);
				}
			}
		} 
		catch (SubroutineCallException e) {
			if (TraceLogger.isTraceEnabled()) {
				TraceLogger.traceException(e);
			}

			// this should never fail, exception from the individual actions are
			// caught above in the result arrays
			// exceptions here to catch if api has changed to call the sub
			// routines
			throw new RuntimeException(e.getMessage() == null ? "" : e.getMessage(), e); //$NON-NLS-1$
		}

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.exit();
		}
	}

	/**
	 * Method to disconect from the universe server
	 * 
	 * @throws SessionDisconnectException
	 */
	public void disconnect() {
		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.entry();
		}

		try {
			if (uvServer != null && uvProject != null) {
				uvServer.closeProject(uvProject);
			}
		}
		catch (SessionDisconnectException e) {
			// this should never fail, exception from the individual actions are
			// caught above in the result arrays
			// exceptions here to catch if api has changed to call the sub
			// routines
			throw new RuntimeException(e.getMessage() == null ? "" : e.getMessage(), e); //$NON-NLS-1$
		}
		finally {
			uvServer = null;
		}

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.exit();
		}
	}

	
	/**
	 * Method returns whether the project is found to be protected or not.
	 * 
	 * @return true is the project is protected.
	 */
	public boolean isProjectProtected() {
		boolean isProtected = false;

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.entry();
		}

		try {
			Object[] resultArray = uvProject.callSubroutine(DSR_PROJECT, 
			                                                new Object[] { Integer.toString(DSR_SUB_PRJ_ISPROTECTED), 
			                                                                "",    //$NON-NLS-1$ 
			                                                                "" }); //$NON-NLS-2$
			if (resultArray.length >= 2) {
				String errorCode = getResultString(resultArray[0]);
				if (errorCode.length() == 0) {
					int success = Integer.parseInt((String) resultArray[1]);
					if (success == 1) {
						// item is protected
						isProtected = true;
					}
				}
			}
		}
		catch (SubroutineCallException e) {
			if (TraceLogger.isTraceEnabled()) {
				TraceLogger.traceException(e);
			}
			// this should never fail, exception from the individual actions are
			// caught above in the result arrays
			// exceptions here to catch if api has changed to call the sub
			// routines
			throw new RuntimeException(e.getMessage() == null ? "" : e.getMessage(), e); //$NON-NLS-1$
		}

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.exit("is protected: " + isProtected);
		}
		return(isProtected);
	}


	private String getResultString(Object result) {
		StringBuffer resultString = new StringBuffer();

		if (result instanceof String) {
			resultString.append((String) result);
		}
		else { 
			if (result instanceof String[]) {
				String[] resultArray = (String[]) result;
				for (int i = 0, length = resultArray.length; i < length; i++) {
					resultString.append(resultArray[i]);
				}
			}
		}

		return resultString.toString();
	}

	
	/**
	 * getSapConnections
	 * 
	 * returns all SAP R/3 connections of the data stage project
	 * 
	 * @return
	 * @throws DSAccessException 
	 */
	public String getSapConnections() throws DSAccessException {

		String connectionXML = "";

		TraceLogger.entry();

		try {
			/*
			 * get InformationServer SAP pack root directory - runs on both
			 * windows and unix
			 */
			TraceLogger.trace(TraceLogger.LEVEL_FINER, "Calling subroutine : " + DS_SAPEXEC + " "+ ECHO_DSSAPHOME_UNIX);

			Object[] result = this.uvProject.callSubroutine(DS_SAPEXEC, new Object[] { UNIX,
			                                                                           ECHO_DSSAPHOME_UNIX, 
			                                                                           EMPTY_STRING, 
			                                                                           Integer.toString(1) });

			String dsSapHome = "";
			if (result.length == 4) {
				// ok if return code is 0
				if (result[3].toString().equals(OK)) {
					dsSapHome = result[2].toString();
					/* remove newlines */
					dsSapHome = dsSapHome.replaceAll("\n", EMPTY_STRING);

					if (TraceLogger.isTraceEnabled()) {
						TraceLogger.trace(TraceLogger.LEVEL_FINER, "DSSAPHOME= "+dsSapHome);
						TraceLogger.trace(TraceLogger.LEVEL_FINER, "Calling subroutine: "+DS_FILEUTIL+" "+dsSapHome+ PATH_SEPARATOR+SAP_CONNECTIONS_FILE);
					}
				}
				else {
               // error occurred while reading env variable DSDSAPHOME
               TraceLogger.trace(TraceLogger.LEVEL_FINE, "DSSAPHOME is not set!");
			   }
			}
			else {
            TraceLogger.trace(TraceLogger.LEVEL_FINE, "Result object length is not valid!");
			}
			
         if (dsSapHome.length() == 0) {
            throw new DSAccessException("120100E", Constants.NO_PARAMS);
         }
					
         /* read connections file */
         result = this.uvProject.callSubroutine(DS_FILEUTIL, 
			                                       new Object[] { Integer.toString(1),
			                                                      dsSapHome + PATH_SEPARATOR + SAP_CONNECTIONS_FILE });

         // ok if result[0] = ""
			if (result[0].toString().equals(EMPTY_STRING)) {
//			// ok if result[0] = "" and result[1] != ""
//         if (result[0].toString().equals(EMPTY_STRING) && !(result[1].toString().equals(EMPTY_STRING))) {
            // ==> convert the content of the connections file to XML
            connectionXML = DSSapConnectionFileConverter.convertToXML(result[1].toString());
         } 
         else {
            // something went wrong while calling DS_FILEUTIL
            TraceLogger.trace(TraceLogger.LEVEL_FINE, "Error while calling " + DS_FILEUTIL + ": "+ result[0].toString());
               
            throw new DSAccessException("120200E", 
                                        new String[] { dsSapHome + PATH_SEPARATOR + SAP_CONNECTIONS_FILE } ); 
			}
		}
		catch(SubroutineCallException subRoutineExcpt ) {
			// subroutine could not be called - missing ???
			TraceLogger.traceException(subRoutineExcpt);

			throw new StageTypeNotInstalledException(DSStageTypeEnum.SAP_IDOC_EXTRACT_CONNECTOR_PX_LITERAL.getName()); 
		}
		catch (Exception e) {
			// something went wrong - return an empty String
			TraceLogger.traceException(e);
			TraceLogger.trace(TraceLogger.LEVEL_FINE, "DataStage SAP connections could not be determined");
			connectionXML = "";
			
			String errMsg;
			if (e instanceof BaseException) {
			   errMsg = ((BaseException) e).getLocalizedMessage();  
			}
			else {
			   errMsg = e.getMessage();
			}
			throw new DSAccessException("101500E", new String[] { errMsg}, e);
		}

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.exit();
		}

		return connectionXML;
	}

} // end of class UVAccessor  
