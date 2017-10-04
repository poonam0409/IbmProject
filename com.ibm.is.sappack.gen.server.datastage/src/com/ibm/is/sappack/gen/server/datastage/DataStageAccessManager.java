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
// Module Name : com.ibm.is.sappack.gen.server.datastage
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************

package com.ibm.is.sappack.gen.server.datastage;


import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;

import DataStageX.DSJobDef;
import DataStageX.DSParameterSet;
import DataStageX.DSStageType;

import com.ibm.is.sappack.gen.common.Constants;
import com.ibm.is.sappack.gen.common.JobGeneratorException;
import com.ibm.is.sappack.gen.common.trace.TraceLogger;
import com.ibm.is.sappack.gen.common.util.DSFolder;
import com.ibm.is.sappack.gen.common.util.DSParamSet;
import com.ibm.is.sappack.gen.common.util.DSProject;
import com.ibm.is.sappack.gen.server.common.DSStageTypeEnum;
import com.ibm.is.sappack.gen.server.common.service.DataStageService;
import com.ibm.is.sappack.gen.server.common.service.ServiceToken;
import com.ibm.is.sappack.gen.server.common.util.DSAccessException;
import com.ibm.is.sappack.gen.server.common.util.ISVersionChecker;
import com.ibm.is.sappack.gen.server.datastage.DataStageObjectFactory;


public final class DataStageAccessManager {
	// -------------------------------------------------------------------------------------
	//                                     Constants
	// -------------------------------------------------------------------------------------

   
	// -------------------------------------------------------------------------------------
	//                                   Member Variables
	// -------------------------------------------------------------------------------------
	private        DataStageService       _ServiceDelegator;

	private static DataStageAccessManager _Instance;
   private static Set<String>            _FilterSet;


   static {
      // build up the filter map
      _FilterSet = new HashSet<String>();
      _FilterSet.add("Data Elements");
      _FilterSet.add("Routines");
      _FilterSet.add("Stage Types");
      _FilterSet.add("Standardization Rules");
      _FilterSet.add("Table Definitions");
      _FilterSet.add("Transforms");
      _FilterSet.add("WAVES Rules");
      _FilterSet.add("IMS Databases (DBD)");
      _FilterSet.add("IMS Viewsets (PSB/PCB)");
      _FilterSet.add("Machine Profiles");
      _FilterSet.add("Match Specifications");
      _FilterSet.add("MNS Rules");
   } // end of static {} 
   
   
	static String copyright() {
		return com.ibm.is.sappack.gen.server.datastage.Copyright.IBM_COPYRIGHT;
	}


	/**
	 * This method creates the Singleton instance.
	 * 
	 */
	private DataStageAccessManager() throws DSAccessException {
		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.entry();
		}

		// check what IS version is running and load the appropriate delegator class
		switch(ISVersionChecker.getISEnvironmentVersion()) {
			case v8x:
				  _ServiceDelegator = new com.ibm.is.sappack.gen.server.datastage_v8.impl.DataStageServiceImpl(_FilterSet);
				  break;

			case v10x:
				  _ServiceDelegator = new com.ibm.is.sappack.gen.server.datastage_v10.impl.DataStageServiceImpl(_FilterSet);
				  break;
				  
			case NotAvailable:
			default:
			     if (TraceLogger.isTraceEnabled()) {
				     TraceLogger.trace(TraceLogger.LEVEL_FINE, "IS Version could not be detected.");    //$NON-NLS-1$
			     }
				  throw new DSAccessException("108300E", Constants.NO_PARAMS);
		}

		// create the DataStageObjectFactory instance
		DataStageObjectFactory.createInstance();

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.exit();
		}
	} // end of DataStageAccessManager()


	public static synchronized DataStageAccessManager createInstance() throws DSAccessException {
		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.entry();
		}

		if (_Instance == null) {
			_Instance = new DataStageAccessManager();
		}

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.exit();
		}

		return (_Instance);
	} // end of createInstance()

	
	public static synchronized void deleteInstance() {
		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.entry();
		}

		if (_Instance != null) {
			_Instance = null;
		}

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.exit();
		}
	} // end of deleteInstance()

	
	public static DataStageAccessManager getInstance() {
		return (_Instance);
	} // end of getInstance()


	public ServiceToken createServiceToken(String locale) throws DSAccessException {
		return(_ServiceDelegator.createServiceToken(locale));
	} // end of createServiceToken()


	/**
	 * This method deletes the specified job from the XMeta repository and Universe DB.
	 * 
	 * @param jobName
	 *           name of the job to be deleted
	 * @param srvcToken
	 *           service token
	 * 
	 * @return true if the job has been deleted, false if there was no existing job
	 * 
	 * @throws DSAccessException
	 *            if an error occurred
	 */
	public boolean deleteDSJob(String jobName, ServiceToken srvcToken) throws DSAccessException  {
		return(_ServiceDelegator.deleteDSJob(jobName, srvcToken));
	} // end of deleteDSJob()

	
	/**
	 * This method checks if the passed job exists in the XMeta repository.
	 * 
	 * @param jobName
	 *           name of the job to be checked
	 * @param srvcToken
	 *           service token
	 * 
	 * @return true if the job exists, false if there doesn't exist
	 * 
	 * @throws DSAccessException
	 *            if an error occurred
	 */
	public boolean doesJobExist(String jobName, ServiceToken srvcToken) throws DSAccessException {
		return(_ServiceDelegator.doesJobExist(jobName, srvcToken));
	} // end of doesJobExist()


	/**
	 * This method checks if there are jobs in the XMeta repository having the passed prefix.
	 * 
	 * @param jobNamePrefix
	 *           name of the job prefix to be checked
	 * @param srvcToken
	 *           service token
	 * 
	 * @return true if there are jobs, false if there aren't
	 * 
	 * @throws DSAccessException
	 *            if an error occurred
	 */
	public boolean doJobsWithPrefixExist(String jobName, ServiceToken srvcToken) throws DSAccessException {
		return(_ServiceDelegator.doJobsWithPrefixExist(jobName, srvcToken));
	} // end of doJobsWithPrefixExist()


	/**
	 * getAllSapConnections
	 * 
	 * returns all DataStage SAP connections
	 * of the given DataStage project
	 * 
	 * @return
	 * @param dsHostName
	 * @param dsProjectName
	 * @param srvcToken
	 * 
	 * @throws DSAccessException 
	 * @throws RemoteException 
	 * @throws JobGeneratorException 
	 */
	public String getAllSapConnections(String dsHostName, Integer dsRPCPort, String dsProjectName,
	                                   ServiceToken srvcToken) throws DSAccessException {
		return(_ServiceDelegator.getAllSapConnections(dsHostName, dsRPCPort, dsProjectName, srvcToken));
	}


	public DSParameterSet getDSParameterSet(String paramSetName, ServiceToken srvcToken) throws DSAccessException {
		return(_ServiceDelegator.getDSParameterSet(paramSetName, srvcToken));
	} // end of getDSStageTypeDefinition()

	
   public String getDSVersion() {
		return(_ServiceDelegator.getDSVersion());
   } // end of getDSVersion{}

   
	public DSStageType getDSStageTypeDefinition(DSStageTypeEnum stageType, ServiceToken srvcToken)
	      throws DSAccessException {
		return(_ServiceDelegator.getDSStageTypeDefinition(stageType, srvcToken));
	} // end of getDSStageTypeDefinition()

	
	public DSFolder getDSFolder(DSProject dsProject, String targetFolderName,
	                            ServiceToken srvcToken)
	      throws DSAccessException {
		return(_ServiceDelegator.getDSFolder(dsProject, targetFolderName, srvcToken));
	} // end of getDSFolder()

	
	
   /**
    * This method returns a filtered list of all folders of a project.
    * <p>
    * <b>Note: </b>The filter removes all folders that are not intended to 
    * be used for storing DataStage jobs.
    * 
    * @param project
    *           DataStage project
    * @param srvcToken
    *           service token
    * 
    * @return List of all DSFolder objects
    * 
    * @throws DSAccessException
    */
   public List<DSFolder> getDSAllFoldersFiltered(DSProject project, ServiceToken srvcToken)
          throws DSAccessException {
		return(_ServiceDelegator.getDSAllFoldersFiltered(project, srvcToken));
   } // end of getDSAllFoldersFiltered()

   
	/**
	 * This method returns the folders of a project.
	 * 
	 * @param projectRid
	 *           project RID
	 * @param isSubFolder
	 *           true if the folder is a sub folder of the default Job folder otherwise false
	 * @param srvcToken
	 *           service token
	 * 
	 * @return List of DSFolder objects
	 * 
	 * @throws DSAccessException
	 */
	public List<DSFolder> getDSFolderList(String projectRid, boolean isSubFolder, ServiceToken srvcToken)
	       throws DSAccessException {
		return(_ServiceDelegator.getDSFolderList(projectRid, isSubFolder, srvcToken));
	} // end of getDSFolderList()

	
   /**
    * This method returns the Parameter Sets of a project.
    * 
    * @param project      project 
    * @param srvcToken  service token
    * 
    * @return List of DSParameterSet objects
    * 
    * @throws DSAccessException
    */
   public List<DSParamSet> getDSParameterSetList(DSProject project, ServiceToken srvcToken)
         throws DSAccessException {
		return(_ServiceDelegator.getDSParameterSetList(project, srvcToken));
   } // end of getDSParameterSetList()

   
	/**
	 * This method returns the DSproject instance for the passed host and project name.
	 * 
	 * @param hostName
	 *           DataStage host name
    * @param dsRPCPort
    *           DataStage RPC port
	 * @param projectName
	 *           DataStage project name
	 * @param srvcToken
	 *           service token
	 * 
	 * @return If the project exists a DSProject instance is returned otherwise null is returned
	 * 
	 * @throws DSAccessException
	 *            if an error occurred
	 */
	public DSProject getDSProject(String hostName, Integer dsRPCPort,
	                              String projectName, ServiceToken srvcToken) throws DSAccessException {
		return(_ServiceDelegator.getDSProject(hostName, dsRPCPort, projectName, srvcToken));
	} // end of getDSProject()

	
	/**
	 * This method returns the list of 'available' DSProjects containing the reference to the Jobs folder.
	 * <p>
	 * <b>Note: </b> If 'jobsFolderName' is null then
	 * 
	 * @param hostName
	 *           DS host name (may be null)
	 * @param jobsFolderName
	 *           name of Jobs folder that must exist in the project
	 * @param srvcToken
	 *           service token
	 * 
	 * @return list of DataStage projects for the specified host and folder
	 * 
	 * @throws DSAccessException
	 *            if an error occurred
	 */
	public List<DSProject> getAvailableDSProjects(ServiceToken srvcToken) throws DSAccessException {
		return(_ServiceDelegator.getAvailableDSProjects(srvcToken));
	} // end of getAvailableDSProjects()

	
	/**
	 * This method saves the passed JobDef in the XMeta repository and in the Universe DB.
	 * 
	 * @param jobDef
	 *           JobDef to be saved
	 * @param srvcToken
	 *           service token
	 * 
	 * @return true if the job has been deleted, false if there was no existing job
	 * 
	 * @throws DSAccessException
	 *            if an error occurred
	 */
	public void saveDSJob(DSJobDef jobDef, ServiceToken srvcToken) throws DSAccessException {
		DSFolder               dsFolder;
		DataStageObjectFactory dsFactory;

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.entry();
		}

		try {
			dsFolder  = srvcToken.getDSFolder();
			dsFactory = DataStageObjectFactory.getInstance(srvcToken);

			srvcToken.uvDBConnect(); // to Universe DB

			srvcToken.startTransaction();
			srvcToken.save(); // JobDef rid is created

			dsFactory.createDSJobItem(jobDef, dsFolder.getId());
			srvcToken.save();

			srvcToken.uvDBCreateJob(jobDef, dsFolder.getDirectoryPath());

			srvcToken.uvDBDisconnect(); // from Universe DB
			srvcToken.commitTransaction();
		}
		catch (Exception pSaveExcpt) {
			DSAccessException newExcpt;

			if (TraceLogger.isTraceEnabled()) {
				TraceLogger.traceException(pSaveExcpt);
			}

			if (pSaveExcpt instanceof DSAccessException) {
				newExcpt = (DSAccessException) pSaveExcpt;
			}
			else {
				newExcpt = new DSAccessException("103700E", Constants.NO_PARAMS, pSaveExcpt);
			}

			srvcToken.uvDBDisconnect();
			srvcToken.abortTransaction();

			throw newExcpt;
		}

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.exit();
		}
	} // end of saveDSJob()
	
	
   public void setXMetaRepObjectBaseInfo(EObject xmetaEObj) {
		_ServiceDelegator.setXMetaRepObjectBaseInfo(xmetaEObj);
   } // end of setXMetaRepObjectBaseInfo()

} // end of class DataStageAccessManager

