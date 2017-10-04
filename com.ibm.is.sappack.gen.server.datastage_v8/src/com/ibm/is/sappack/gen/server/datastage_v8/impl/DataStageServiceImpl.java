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
// Module Name : com.ibm.is.sappack.gen.server.datastage_v8.impl
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.server.datastage_v8.impl;


import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;

import ASCLModel.TypeCodeEnum;
import DataStageX.DSExtendedParamTypeEnum;
import DataStageX.DSItem;
import DataStageX.DSJobDef;
import DataStageX.DSParameterDef;
import DataStageX.DSParameterSet;
import DataStageX.DSStageType;
import DataStageX.DataStageXPackage;

import com.ascential.acs.registration.ASBApplication;
import com.ascential.acs.registration.ASBApplicationType;
import com.ascential.acs.registration.ASBNode;
import com.ascential.acs.registration.ConfigProperty;
import com.ascential.acs.registration.RegistrationService;
import com.ascential.acs.registration.RegistrationServiceFactory;
import com.ascential.xmeta.exception.data.SessionLockException;
import com.ascential.xmeta.model.XMetaBasePackage.XMetaBasePackagePackage;
import com.ascential.xmeta.model.XMetaBasePackage.XMetaRepositoryObject;
import com.ibm.is.sappack.gen.common.Constants;
import com.ibm.is.sappack.gen.common.JobGeneratorException;
import com.ibm.is.sappack.gen.common.request.JobParamData;
import com.ibm.is.sappack.gen.common.trace.TraceLogger;
import com.ibm.is.sappack.gen.common.util.DSFolder;
import com.ibm.is.sappack.gen.common.util.DSParamSet;
import com.ibm.is.sappack.gen.common.util.DSProject;
import com.ibm.is.sappack.gen.common.util.StringUtils;
import com.ibm.is.sappack.gen.server.common.DSStageTypeEnum;
import com.ibm.is.sappack.gen.server.common.service.DataStageService;
import com.ibm.is.sappack.gen.server.common.service.ServiceToken;
import com.ibm.is.sappack.gen.server.common.service.ServiceToken.QryOptions;
import com.ibm.is.sappack.gen.server.common.util.DSAccessException;
import com.ibm.is.sappack.gen.server.common.util.DSAccessNotAllowedException;
import com.ibm.is.sappack.gen.server.common.util.SessionIsLockedException;
import com.ibm.is.sappack.gen.server.datastage_v8.UVAccessor;
import com.ibm.is.sappack.gen.server.datastage_v8.UserAccess;


public final class DataStageServiceImpl implements DataStageService {
	// -------------------------------------------------------------------------------------
	// Constants
	// -------------------------------------------------------------------------------------
   private static final String XMETA_USER            = "xmeta";     //$NON-NLS-1$
   private static final String DS_RPC_PORT_PROP_NAME = "dsrpcPort"; //$NON-NLS-1$

   
	// -------------------------------------------------------------------------------------
	// Member Variables
	// -------------------------------------------------------------------------------------
   private Set<String>    _FolderFilterSet;


	static String copyright() {
		return com.ibm.is.sappack.gen.server.datastage_v8.impl.Copyright.IBM_COPYRIGHT;
	}

	public DataStageServiceImpl() {
		this(null);
	} // end of DataStageServiceImpl()

	
	public DataStageServiceImpl(Set<String> folderFilterSet) {

		// initialization of the DataStageX and XMeta ECore packages is required
		DataStageXPackage       dsPk    = DataStageXPackage.eINSTANCE;
		XMetaBasePackagePackage xmetaPk = XMetaBasePackagePackage.eINSTANCE;

		if (folderFilterSet == null) {
		   _FolderFilterSet = new HashSet<String>();
		}
		else {
		   _FolderFilterSet = folderFilterSet;
		}

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.trace(TraceLogger.LEVEL_FINEST, "folder filter set size = " + _FolderFilterSet.size());
		}
	} // end of DataStageServiceImpl()


	public ServiceToken createServiceToken(String locale) throws DSAccessException {
	   Locale usedLocale;

	   if (locale == null) {
	      usedLocale = Locale.getDefault();
	   }
	   else {
	      usedLocale = new Locale(locale);
	   }
		ServiceToken newToken = new ServiceTokenImpl(usedLocale);

		return (newToken);
	} // end of createServiceToken()

	
	/**
	 * This method deletes the specified job from the XMeta repository and Universe DB.
	 * 
	 * @param jobName
	 *           name of the job to be deleted
	 * @param srvcToken
	 *           access token
	 * 
	 * @return true if the job has been deleted, false if there was no existing job
	 * 
	 * @throws DSAccessException
	 *            if an error occurred
	 */
	public boolean deleteDSJob(String jobName, ServiceToken srvcToken) throws DSAccessException  {
		String  dsJobDefRid;
		boolean jobDeleted = false;

		// get the DSJobDef rid
		dsJobDefRid = getRidForDSItem(jobName, srvcToken.getProjectNameSpace(), srvcToken);
		if (dsJobDefRid != null) {
			srvcToken.uvDBConnect(); // to Universe DB

			try {
				srvcToken.startTransaction();
				deleteDSObject(dsJobDefRid, srvcToken);

				// delete job from Universe DB too
				srvcToken.uvDBDeleteJob(jobName);

				srvcToken.commitTransaction();
				jobDeleted = true;
			}
			catch (SessionLockException pLockExcpt) {
				throw new SessionIsLockedException(jobName, pLockExcpt);
			}
			catch (DSAccessException pAccessExcpt) {
				throw pAccessExcpt;
			}
			catch (Exception pSaveExcpt) {
				try {
				   if (srvcToken.isInTransaction()) {
	               srvcToken.abortTransaction();
				   }
				}
            catch (DSAccessException dsAccessExcpt) {
               throw dsAccessExcpt;
            }
				catch (Exception excpt) {
					excpt.printStackTrace();
				}
				pSaveExcpt.printStackTrace();
			}

			srvcToken.uvDBDisconnect(); // from Universe DB
		} // end of if (dsJobDefRid != null)

		return (jobDeleted);
	} // end of deleteDSJob()

	
	/**
	 * This method deletes a DSItem (identified by its rid) from the repository.
	 * 
	 * @param dsItemRid
	 *           rid of the DS item to be deleted
	 * @param srvcToken
	 *           access token
	 * 
	 * @throws DSAccessException
	 *            if an error occurred
	 */
	protected void deleteDSObject(String dsItemRid, ServiceToken srvcToken) throws DSAccessException,
	                                                                               SessionLockException {
		String query = null;

		// get folder instance
		query = "select x  from x in " + DataStageXPackage.eINSTANCE.getName() + "::" //$NON-NLS-1$//$NON-NLS-2$
		      + DataStageXPackage.eINSTANCE.getEClassifier("DSItem").getName() + " where rid(x) = \"" + dsItemRid + "\""; //$NON-NLS-1$ //$NON-NLS-3$ //$NON-NLS-4$

		List result = srvcToken.executeObjectQuery(query, DataStageXPackage.eINSTANCE, QryOptions.ROOT_ONLY);

		if (result != null) {
			if (result.size() == 1) {
				DSItem dsItem = (DSItem) result.get(0);

				// desItem.getReposId references the DSJob object
				XMetaRepositoryObject dsObject = ((ServiceTokenImpl) srvcToken).getXMetaObjectByRid(dsItem.getReposId());
				// DSSharedContainerDef dsObject = getDSSharedContainerByRid(dsItem.getReposId());

				srvcToken.markForDelete(dsItem);
				srvcToken.markForDelete(dsObject);
				srvcToken.save();
			}

			// throws an error since the item exists more than once
			else if (result.size() > 1) {
				throw new RuntimeException("The metadata repository contains more than one instance of the selected item. Generation cannot continue. Correct the content of the metadata repository by removing this job item and the referenced job."); //$NON-NLS-1$      		
			}
			else {
				throw new DSAccessException("101300E", Constants.NO_PARAMS);
			}
		}
	} // end of deleteDSObject()


	/**
	 * This method checks if the passed job exists in the XMeta repository.
	 * 
	 * @param jobName
	 *           name of the job to be checked
	 * @param srvcToken
	 *           access token
	 * 
	 * @return true if the job exists, false if there doesn't exist
	 * 
	 * @throws DSAccessException
	 *            if an error occurred
	 */
	public boolean doesJobExist(String jobName, ServiceToken srvcToken) throws DSAccessException {
		String  dsJobDefRid;
		boolean jobExists = false;

		// get the DSJobDef rid
		dsJobDefRid = getRidForDSItem(jobName, srvcToken.getProjectNameSpace(), srvcToken);

		if (dsJobDefRid != null) {
			jobExists = true;
		} // end of if (dsJobDefRid != null)

		return (jobExists);
	} // end of doesJobExist()


	/**
	 * This method checks if there are jobs in the XMeta repository having the passed prefix.
	 * 
	 * @param jobNamePrefix
	 *           name of the job prefix to be checked
	 * @param srvcToken
	 *           access token
	 * 
	 * @return true if there are jobs, false if there aren't
	 * 
	 * @throws DSAccessException
	 *            if an error occurred
	 */
	public boolean doJobsWithPrefixExist(String jobName, ServiceToken srvcToken) throws DSAccessException {

		boolean jobsExist = false;

		if (jobName != null) {
			String  query;  
			String  tmpJobNameEndCriteria;
			List    queryResult = null;
			char    cArr[];
			int     lastIdx;

			// create the job name end criteria for the query
			tmpJobNameEndCriteria = jobName;
			cArr                  = tmpJobNameEndCriteria.toCharArray();
			lastIdx               = tmpJobNameEndCriteria.length() -1;
			cArr[lastIdx]         = (char) (cArr[lastIdx] + 1);
			tmpJobNameEndCriteria = new String(cArr);

			query = "select x.name from x in " + DataStageXPackage.eINSTANCE.getName() + "::" //$NON-NLS-1$ //$NON-NLS-2$
	              + DataStageXPackage.eINSTANCE.getEClassifier("DSJobDef").getName()        //$NON-NLS-1$ 
	              + " where x.DSNameSpace =\"" + srvcToken.getProjectNameSpace() + "\""     //$NON-NLS-1$ //$NON-NLS-2$
	              + " and x.name >= \"" + jobName + "\""                                    //$NON-NLS-1$ //$NON-NLS-2$
	              + " and x.name < \"" + tmpJobNameEndCriteria + "\"";                      //$NON-NLS-1$ //$NON-NLS-2$

			try {
				queryResult = srvcToken.executeValueQuery(query, DataStageXPackage.eINSTANCE);
				jobsExist   = queryResult.size() != 0;
			} 
			catch (Exception pExcpt) {
				throw new DSAccessException("101200E", new String[] { query, pExcpt.toString() }, pExcpt);
			}
		} // end of if (jobName != null)

		return (jobsExist);
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
		
		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.entry();
		}
		
		/* use DSProject to get the information server domain name.
		 * This is necessary because XMeta doesn't handly ip addresses
		 * and fully qualified hostnames 
		 * (for example 127.0.0.1 or server.boeblingen.de.ibm.com) properly */
		DSProject project = getDSProject(dsHostName, dsRPCPort, dsProjectName, srvcToken);
		
		if (TraceLogger.isTraceEnabled()) {
         TraceLogger.trace(TraceLogger.LEVEL_FINE, "project = " + project);
		}
		
		if (project == null) {
		   
		   // different exception dependent on if there is an special RPC port 
         if (dsRPCPort == null) {
            throw new DSAccessException("100800E", new String[] { dsProjectName, dsHostName } );
         }
         else
         {
            throw new DSAccessException("100900E", new String[] { dsProjectName, 
                                                                  dsHostName, 
                                                                  dsRPCPort.toString() 
                                                                } );
         } // end of if (dsRPCPort == null)
		}
		
		UVAccessor accessor = new UVAccessor(project);
		
      boolean connected = false;
		try {
		   connected = accessor.connect();
		}
		catch(RemoteException remoteExcpt) {
	      if (TraceLogger.isTraceEnabled()) {
	         TraceLogger.traceException(remoteExcpt);
	      }
	      
         throw new DSAccessException("101500E", new String[] { remoteExcpt.getMessage() }, 
                                     remoteExcpt);
		}
		
		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.exit();
		}
		
		if(connected) {
			String result = accessor.getSapConnections();
			accessor.disconnect();
			return result;
		}
		
		throw new DSAccessException("100700E", new String[] { dsProjectName, 
		                            project.getHostName() } );
	} // end of getAllSapConnections()


	public DSParameterSet getDSParameterSet(String paramSetName, ServiceToken srvcToken) throws DSAccessException {
		DSParameterSet resultDSParamSet;
		List queryResult;

		resultDSParamSet = null;
		String query = "select x from x in " + DataStageXPackage.eINSTANCE.getName() + "::" + //$NON-NLS-1$  
		      DataStageXPackage.eINSTANCE.getEClassifier("DSParameterSet").getName() + //$NON-NLS-1$ 
		      " where x.name = \"" + paramSetName + //$NON-NLS-2$  
		      "\" and x.DSNameSpace =\"" + srvcToken.getProjectNameSpace() + "\""; //$NON-NLS-1$   //$NON-NLS-2$

		// execute query
		queryResult = srvcToken.executeObjectQuery(query, DataStageXPackage.eINSTANCE, QryOptions.ROOT_ONLY);

		if (queryResult != null && !queryResult.isEmpty()) {
			resultDSParamSet = (DSParameterSet) queryResult.get(0);
		}

		return (resultDSParamSet);
	} // end of getDSStageTypeDefinition()

	
   private Map<String, Integer> getDSServerRPCPortMap() {
      Map<String, Integer>    serverPortMap;
      
      if (TraceLogger.isTraceEnabled()) {
         TraceLogger.entry();
      }
      
      serverPortMap = new HashMap<String, Integer>();
      try {
         // get the registration service
         RegistrationService regService = (new RegistrationServiceFactory()).getImplementation();

         // Returns all the registered ASB nodes that are hosting a DS engine (there might be more than one).
         ASBNode[] dsNodes = regService.getRegisteredNodes(ASBApplicationType.ASCENTIAL_DS_SERVER.getName());

         for(int nodeIdx = 0; nodeIdx < dsNodes.length; nodeIdx ++) {
            String nodeName = dsNodes[nodeIdx].getName(); // This is the host name for now, may change in the future to getHostName()
             
            // Get all dsServers registered for the node. Should only be one.
            ASBApplication[] dsServers = regService.getRegisteredApplications(nodeName, 
                                                                              ASBApplicationType.ASCENTIAL_DS_SERVER.getName());
            ASBApplication dsApp = dsServers[0];
      
            // look for the RPC port property
            for(int nProp = 0; nProp < dsApp.getProperties().length; nProp++) {
               ConfigProperty prop = dsApp.getProperties()[nProp];

               if(prop.getName().equals(DS_RPC_PORT_PROP_NAME)) {
                  // prop.getValue() holds the DS port number.
                  String curNodeName = nodeName.toUpperCase();
                  int    curRPCPort = Integer.parseInt(prop.getValue());
                  
                  // do not store the DS default RPC port
                  if (curRPCPort == Constants.JOB_GEN_DEFAULT_DS_RPC_PORT) {
                     if (TraceLogger.isTraceEnabled()) {
                        TraceLogger.trace(TraceLogger.LEVEL_FINER, 
                                          curNodeName + ": default port = " + curRPCPort + " (not stored)");
                     }
                  }
                  else {
                     serverPortMap.put(curNodeName, new Integer(curRPCPort));
                     
                     if (TraceLogger.isTraceEnabled()) {
                        TraceLogger.trace(TraceLogger.LEVEL_FINER, 
                                          curNodeName + ": port = " + curRPCPort);
                     }
                  } // end of if (curRPCPort != DS_RPC_PORT_DEFAULT)
               } 
               else {
                  // If "dsrpcPort" property is not found, it means that DS port is the default one 
                  // (will always be the case in 0.8.0)
                  ;
               }
            } // end of for(int nProp = 0; nProp < dsApp.getProperties().length; nProp++)
         } // end of for for(int nodeIdx = 0; nodeIdx < dsNodes.length; nodeIdx ++)
      } // end of try
      catch(Exception excpt) {
         if (TraceLogger.isTraceEnabled()) {
            TraceLogger.traceException(excpt);
         }
      } // end of catch(Exception excpt)
      
      if (TraceLogger.isTraceEnabled()) {
         TraceLogger.exit("port map size = " + serverPortMap.size());
      }
      
      return(serverPortMap);
   } // end of getDSServerRPCPortMap{}

   
   public String getDSVersion() {
   	String dsVersion;

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.entry();
		}

		dsVersion = null;
		try {
			// get the registration service
			RegistrationService regService = (new RegistrationServiceFactory()).getImplementation();

			dsVersion = regService.getASBServerConfiguration().getVersion();
		} // end of try
		catch (Exception excpt) {
			if (TraceLogger.isTraceEnabled()) {
				TraceLogger.traceException(excpt);
			}
		} // end of catch(Exception excpt)

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.exit("DS version = " + dsVersion);
		}

		return (dsVersion);
   } // end of getDSVersion{}

   
	public DSStageType getDSStageTypeDefinition(DSStageTypeEnum stageType, ServiceToken srvcToken)
	       throws DSAccessException {
		DSStageType resultDSStageType;
		List        queryResult;

		resultDSStageType = null;
		String query = "select x from x in " + DataStageXPackage.eINSTANCE.getName() + "::" + //$NON-NLS-1$  
		      DataStageXPackage.eINSTANCE.getEClassifier("DSStageType").getName() + //$NON-NLS-1$ 
		      " where x.name = \"" + stageType.getName() + //$NON-NLS-2$  
		      "\" and x.DSNameSpace =\"" + srvcToken.getProjectNameSpace() + "\""; //$NON-NLS-1$   //$NON-NLS-2$

		// execute query
		queryResult = srvcToken.executeObjectQuery(query, DataStageXPackage.eINSTANCE, QryOptions.ROOT_ONLY);

		if (queryResult != null && !queryResult.isEmpty()) {
			resultDSStageType = (DSStageType) queryResult.get(0);
		}

		return (resultDSStageType);
	} // end of getDSStageTypeDefinition()

	
	public DSFolder getDSFolder(DSProject dsProject, String targetFolderName,
	                            ServiceToken srvcToken)
	      throws DSAccessException {
		String         folderName;
		String         folderParentRid;
		List<DSFolder> dsFolderList;
		DSFolder       retFolder;
		Iterator       folderIter;

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.entry("Target folder name: " + targetFolderName);
		}

      // different handling for default and non-default folder 
      if (targetFolderName == null) {
         // ==> all jobs are stored in the DS Default folder
         folderName      = DSFolder.DEFAULT_DS_FOLDER_NAME;
         folderParentRid = dsProject.getId();
         
         // get all folders having that 'parent RID'
         dsFolderList = getDSFolderList(folderParentRid, false, srvcToken);
      }
      else {
         String folderCategory;
         int    idx;

         // ==> absolute folder path
         idx = targetFolderName.lastIndexOf(DSFolder.DIRECTORY_SEPARATOR);
         if (idx < 0) {
            idx = 0;
         }
         folderCategory = targetFolderName.substring(0, idx);
         if (folderCategory.length() == 0) {
            folderCategory = DSFolder.DIRECTORY_SEPARATOR;
         }
         folderName = targetFolderName.substring(idx + DSFolder.DIRECTORY_SEPARATOR.length()); 

         if (TraceLogger.isTraceEnabled()) {
            TraceLogger.trace(TraceLogger.LEVEL_FINEST, 
                              "folder category = " + folderCategory + " - foldername = " +  folderName);
         }

         // get all folders having that category
         dsFolderList = getDSFolderListByCategory(folderCategory, folderName,
                                                  dsProject, srvcToken);
      } // end of (else) if (targetFolderName == null)
      
		retFolder  = null;
		folderIter = dsFolderList.iterator();
		while (folderIter.hasNext() && retFolder == null) {
			DSFolder tmpFolder = (DSFolder) folderIter.next();
			if (tmpFolder.getName().equals(folderName)) {
				retFolder = tmpFolder;
			}
		} // end of while(projectIter.hasNext() && ...

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.exit("DsFolder = " + retFolder);
		}

		return (retFolder);
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
    *           access token
    * 
    * @return List of all DSFolder objects
    * 
    * @throws DSAccessException
    */
   public List<DSFolder> getDSAllFoldersFiltered(DSProject project, ServiceToken srvcToken)
          throws DSAccessException {
      DSFolder       curDSFolder;
      String         tmpCategory;
      String         tmpFolderName;
      String         tmpFolderRid;
      String         projectNameSpace;
      List<DSFolder> retFolderList;
      Object         dsFolderDataArr[];

      if (TraceLogger.isTraceEnabled()) {
         TraceLogger.entry("Project NS = " + project.getNameSpace());
      }
      
      // 1) add all contained folders as proxy folder instances
      projectNameSpace = project.getNameSpace();
      String query = "select rid(x), x.Name, x.ParentRID, x.Category from x in " + DataStageXPackage.eINSTANCE.getName() //$NON-NLS-1$   
                   + "::" + DataStageXPackage.eINSTANCE.getEClassifier("DSFolder").getName() //$NON-NLS-2$ //$NON-NLS-3$
                   + " where x.ProjectNameSpace = \"" + projectNameSpace + "\""; //$NON-NLS-1$ //$NON-NLS-2$

      if (TraceLogger.isTraceEnabled()) {
         TraceLogger.trace(TraceLogger.LEVEL_FINEST, "query = " + query);
      }

      Iterator queryResult = srvcToken.executeValueQuery(query, DataStageXPackage.eINSTANCE).iterator();

      retFolderList = new ArrayList<DSFolder>();
      if ((queryResult != null) && (queryResult.hasNext())) {
         while (queryResult.hasNext()) {
            dsFolderDataArr = (Object[]) queryResult.next();

            tmpCategory   = (String) dsFolderDataArr[3];
            tmpFolderName = (String) dsFolderDataArr[1];

            if (!(isCategoryFiltered(tmpFolderName) || isCategoryFiltered(tmpCategory))) {
               tmpFolderRid  = (String) dsFolderDataArr[0];
               curDSFolder   = new DSFolder(tmpFolderRid, tmpFolderName, project.getId(), 
                                            tmpCategory);
               curDSFolder.setParentId((String) dsFolderDataArr[2]);

               retFolderList.add(curDSFolder);
            } // end of if (!(isCategoryFiltered(tmpFolderName) || isCategoryFiltered(tmpCategory)))
         }
      }

      if (TraceLogger.isTraceEnabled()) {
         TraceLogger.exit(String.valueOf(retFolderList.size()));
      }

      return (retFolderList);
   } // end of getDSAllFoldersFiltered()

   
	/**
	 * This method returns the folders of a project.
	 * 
	 * @param projectRid
	 *           project RID
	 * @param isSubFolder
	 *           true if the folder is a sub folder of the default Job folder otherwise false
	 * @param srvcToken
	 *           access token
	 * 
	 * @return List of DSFolder objects
	 * 
	 * @throws DSAccessException
	 */
	public List<DSFolder> getDSFolderList(String projectRid, boolean isSubFolder, ServiceToken srvcToken)
	       throws DSAccessException {
		DSFolder       curDSFolder;
		String         tmpFolderName;
		String         tmpFolderRid;
		String         tmpCategory;
		List<DSFolder> retFolderList;
		Object         dsFolderDataArr[];

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.entry("proj RID = " + projectRid);
		}

		// 1) add all contained folders as proxy folder instances
      String query = "select rid(x), x.Name, x.ParentRID, x.Category from x in " + DataStageXPackage.eINSTANCE.getName() //$NON-NLS-1$   
		      + "::" + DataStageXPackage.eINSTANCE.getEClassifier("DSFolder").getName() //$NON-NLS-2$ //$NON-NLS-3$
		      + " where x.ParentRID = \"" + projectRid + "\""; //$NON-NLS-1$ //$NON-NLS-2$

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.trace(TraceLogger.LEVEL_FINEST, "query = " + query);
		}

		Iterator queryResult = srvcToken.executeValueQuery(query, DataStageXPackage.eINSTANCE).iterator();

		retFolderList = new ArrayList<DSFolder>();
		if ((queryResult != null) && (queryResult.hasNext())) {
			while (queryResult.hasNext()) {
				dsFolderDataArr = (Object[]) queryResult.next();

				tmpFolderRid  = (String) dsFolderDataArr[0];
				tmpFolderName = (String) dsFolderDataArr[1];
            tmpCategory   = (String) dsFolderDataArr[3];
				curDSFolder   = new DSFolder(tmpFolderRid, tmpFolderName, projectRid,
				                             tmpCategory);
				curDSFolder.setIsSubFolder(isSubFolder);
				retFolderList.add(curDSFolder);
			}
		}

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.exit(String.valueOf(retFolderList.size()));
		}

		return (retFolderList);
	} // end of getDSFolderList()

	
   /**
    * This method returns the folders of the passed project having the passed 
    * category.
    * 
    * @param folderCategory  folder category name
    * @param folderName      folder name (may be null)
    * @param dsProject       DSProject instance
    * @param srvcToken     access token
    * 
    * @return List of DSFolder objects
    * 
    * @throws DSAccessException
    */
   public List<DSFolder> getDSFolderListByCategory(String folderCategory, String folderName, 
                                                   DSProject dsProject, ServiceToken srvcToken)
          throws DSAccessException {
      DSFolder       curDSFolder;
      StringBuffer   queryBuf;
      String         tmpFolderName;
      String         tmpFolderCategory;
      String         tmpFolderRid;
      List<DSFolder> retFolderList;
      Object         dsFolderDataArr[];

      if (TraceLogger.isTraceEnabled()) {
         TraceLogger.entry("folder category = " + folderCategory + " - folder name = " + folderName);
      }

      // 'double' the directory separators ('escaping')
      folderCategory = StringUtils.replaceString(folderCategory, 
                                                 DSFolder.DIRECTORY_SEPARATOR, 
                                                 DSFolder.DIRECTORY_SEPARATOR + DSFolder.DIRECTORY_SEPARATOR);
      // 1) add all contained folders as proxy folder instances
      queryBuf = new StringBuffer(); 
      queryBuf.append("select rid(x), x.Name, x.Category from x in ");                                //$NON-NLS-1$
      queryBuf.append(DataStageXPackage.eINSTANCE.getName());
      queryBuf.append("::");                                                              //$NON-NLS-1$
      queryBuf.append(DataStageXPackage.eINSTANCE.getEClassifier("DSFolder").getName());  //$NON-NLS-1$
      queryBuf.append(" where");                                                          //$NON-NLS-1$
      queryBuf.append(" x.ProjectNameSpace = \"");                                        //$NON-NLS-1$
      queryBuf.append(dsProject.getNameSpace());
      queryBuf.append("\"");                                                              //$NON-NLS-1$
      queryBuf.append(" and");                                                            //$NON-NLS-1$
      queryBuf.append(" x.Category =\"");                                                 //$NON-NLS-1$
      queryBuf.append(folderCategory);
      queryBuf.append("\"");                                                              //$NON-NLS-1$
      if (folderName != null && folderName.length() > 0) {
         queryBuf.append(" and");                                                         //$NON-NLS-1$
         queryBuf.append(" x.Name =\"");                                                  //$NON-NLS-1$
         queryBuf.append(folderName);
         queryBuf.append("\"");                                                           //$NON-NLS-1$
      }

      if (TraceLogger.isTraceEnabled()) {
         TraceLogger.trace(TraceLogger.LEVEL_FINEST, "query = " + queryBuf);
      }

      Iterator queryResult = srvcToken.executeValueQuery(queryBuf.toString(), DataStageXPackage.eINSTANCE).iterator();

      retFolderList = new ArrayList<DSFolder>();
      if ((queryResult != null) && (queryResult.hasNext())) {
         while (queryResult.hasNext()) {
            dsFolderDataArr = (Object[]) queryResult.next();

            tmpFolderRid      = (String) dsFolderDataArr[0];
            tmpFolderName     = (String) dsFolderDataArr[1];
            tmpFolderCategory = (String) dsFolderDataArr[2];
            curDSFolder       = new DSFolder(tmpFolderRid, tmpFolderName, dsProject.getId(),
                                             tmpFolderCategory);
            retFolderList.add(curDSFolder);
         }
      }

      if (TraceLogger.isTraceEnabled()) {
         TraceLogger.exit(String.valueOf(retFolderList.size()));
      }

      return (retFolderList);
   } // end of getDSFolderListByCategory()
   

   /**
    * This method returns the Parameter Sets of a project.
    * 
    * @param project      project 
    * @param srvcToken  access token
    * 
    * @return List of DSParameterSet objects
    * 
    * @throws DSAccessException
    */
   public List<DSParamSet> getDSParameterSetList(DSProject project, ServiceToken srvcToken)
         throws DSAccessException {
      DSParamSet       newParamSet;
      DSParameterSet   dsParamSet;
      String           projectNameSpace;
      List<DSParamSet> retParamSetList;
      Iterator         paramDefListIter;

      if (TraceLogger.isTraceEnabled()) {
         TraceLogger.entry("Project NS = " + project.getNameSpace());
      }

      projectNameSpace = project.getNameSpace();

      String query = "select x from x in " + DataStageXPackage.eINSTANCE.getName()    //$NON-NLS-1$   
                   + "::" + DataStageXPackage.eINSTANCE.getEClassifier("DSParameterSet").getName() //$NON-NLS-2$ //$NON-NLS-3$
                   + " where x.DSNameSpace = \"" + projectNameSpace + "\"";                   //$NON-NLS-1$ //$NON-NLS-2$

      if (TraceLogger.isTraceEnabled()) {
         TraceLogger.trace(TraceLogger.LEVEL_FINEST, "query = " + query);
      }

      Iterator queryResult = srvcToken.executeObjectQuery(query, DataStageXPackage.eINSTANCE,
                                                          QryOptions.ROOT_ONLY).iterator();

      retParamSetList = new ArrayList<DSParamSet>();
      if ((queryResult != null) && (queryResult.hasNext())) {
         while (queryResult.hasNext()) {
            dsParamSet  = (DSParameterSet) queryResult.next();
            newParamSet = new DSParamSet(dsParamSet.get_xmeta_repos_object_id(), 
                                         dsParamSet.getName(), projectNameSpace);

            // process all ParameterDefs 
            paramDefListIter = dsParamSet.getHas_ParameterDef().iterator();
            while(paramDefListIter.hasNext()) {
               DSParameterDef dsParamDef = (DSParameterDef) paramDefListIter.next();

               // evaluate the type
               TypeCodeEnum typeEnum = dsParamDef.getTypeCode();
               String       value;
               int          jobParamType;

               value = dsParamDef.getDefaultValue(); 
               switch(typeEnum.getValue()) {
                  case TypeCodeEnum.STRING:
                       switch(dsParamDef.getExtendedType().getValue()) {
                          case DSExtendedParamTypeEnum.ENCRYPTED:
                               value        = srvcToken.decrypt(value); 
                               jobParamType = JobParamData.JOB_PARAM_TYPE_ENCYRYPTED;
                               break;
                          case DSExtendedParamTypeEnum.PARAMETERSET:
                               jobParamType = JobParamData.JOB_PARAM_TYPE_PARAM_SET;
                               break;
                          case DSExtendedParamTypeEnum.PATHNAME:
                               jobParamType = JobParamData.JOB_PARAM_TYPE_PATHNAME;
                               break;
                          case DSExtendedParamTypeEnum.STRINGLIST:
                               jobParamType = JobParamData.JOB_PARAM_TYPE_LIST;
                               break;
                          default:
                               jobParamType = JobParamData.JOB_PARAM_TYPE_STRING;
                       } // end of switch(dsParamDef.getExtendedType().getValue())
                       break;
                       
                  case TypeCodeEnum.SFLOAT:
                       jobParamType = JobParamData.JOB_PARAM_TYPE_FLOAT;
                       break;
                       
                  case TypeCodeEnum.INT64:
                       jobParamType = JobParamData.JOB_PARAM_TYPE_INTEGER;
                       break;
                     
                  case TypeCodeEnum.TIME:
                     jobParamType = JobParamData.JOB_PARAM_TYPE_TIME;
                     break;
                     
                  default:
                       jobParamType = JobParamData.JOB_PARAM_TYPE_STRING;
               }
               
               newParamSet.addJobParam(new JobParamData(dsParamDef.getName(), 
                                                        dsParamDef.getDisplayCaption(), 
                                                        jobParamType, value, 
                                                        dsParamDef.getLongDescription()));            
            }

            retParamSetList.add(newParamSet);
         } // end of while (queryResult.hasNext())
      } // end of if ((queryResult != null) && (queryResult.hasNext()))

      if (TraceLogger.isTraceEnabled()) {
         TraceLogger.exit(String.valueOf(retParamSetList.size()));
      }

      return (retParamSetList);
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
	 *           access token
	 * 
	 * @return If the project exists a DSProject instance is returned otherwise null is returned
	 * 
	 * @throws DSAccessException
	 *            if an error occurred
	 */
	public DSProject getDSProject(String hostName, Integer dsRPCPort,
	                              String projectName, ServiceToken srvcToken) throws DSAccessException {
		DSProject      retDSProject;
		String         tmpHostName;
		String         tmpProjectName;
		String         tmpProjectRid;
		Object         dsProjectDataArr[];
		List<Object[]> nativeDSProjectsList;
		boolean        isProjectAccessible;

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.entry("DS Host = " + hostName + "- RPC port = " + dsRPCPort + " - Project name = " + projectName);
		}

		if (projectName == null) {
			throw new IllegalArgumentException("Project name must be specified.");
		} // end of if (projectName == null)

		if (Constants.FIRST_DS_PROJECT.equals(projectName)) {
			projectName = null;
		}
		
		// get the native (unchecked) list of all existing DSProjects (for specified host) ...
		nativeDSProjectsList = getNativeDSProjectList(hostName, projectName, srvcToken);

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.trace(TraceLogger.LEVEL_FINEST, "project count = " + nativeDSProjectsList.size());
		}

		switch (nativeDSProjectsList.size()) {
         case 0:
			     // project could not be found
              retDSProject = null;

              if (TraceLogger.isTraceEnabled()) {
                 TraceLogger.trace(TraceLogger.LEVEL_FINEST, "Project '" + projectName + "' could not be found on host '"
                                                            + hostName + "'.");
              }
              
              break;

         //case 1:
              default:
              // there should exist exactly one project
              dsProjectDataArr = (Object[]) nativeDSProjectsList.get(0);
              tmpProjectRid    = (String) dsProjectDataArr[0];
              tmpProjectName   = (String) dsProjectDataArr[1];
              tmpHostName      = (String) dsProjectDataArr[2];

              retDSProject        = new DSProject(tmpProjectRid, tmpProjectName, tmpHostName, dsRPCPort);
              isProjectAccessible = isProjectAccessible(retDSProject);
         
              if (!isProjectAccessible) {
                 // project exists but is not accessible
                 throw new DSAccessNotAllowedException("101000E", new String[] { projectName } );
              } // end of (else) if (isProjectAccessible)
              break;

//         default:
              // more than one project exist
  //            throw new DSAccessException("101100E", new String[] { projectName } );
		} // end of switch(nativeDSProjectsList.size())

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.exit(retDSProject);
		}

		return (retDSProject);
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
	 *           access token
	 * 
	 * @return list of DataStage projects for the specified host and folder
	 * 
	 * @throws DSAccessException
	 *            if an error occurred
	 */
	public List<DSProject> getAvailableDSProjects(ServiceToken srvcToken) throws DSAccessException {
		DSProject            curDSProject;
		String               tmpHostName;
		String               tmpProjectName;
		String               tmpProjectRid;
		ArrayList<DSProject> retProjectList;
		Object               dsProjectDataArr[];
		List                 nativeDSProjectsList;
		Map<String, Integer> rpcPortMap;

		TraceLogger.trace(TraceLogger.LEVEL_FINER, "Don't use host name in query for projects");
		
		// get the native DSProjects list ...
		nativeDSProjectsList = getNativeDSProjectList(null, null, srvcToken);

		retProjectList = new ArrayList<DSProject>();
		if (nativeDSProjectsList != null && !nativeDSProjectsList.isEmpty()) {
		   // get a map that contains the available server and its RPC ports
		   rpcPortMap = getDSServerRPCPortMap();
		   
			// copy DSProject instances into the DSProject list
			Iterator it = nativeDSProjectsList.iterator();
			while (it.hasNext()) {
				dsProjectDataArr = (Object[]) it.next();
				tmpProjectRid    = (String) dsProjectDataArr[0];
				tmpProjectName   = (String) dsProjectDataArr[1];
				tmpHostName      = (String) dsProjectDataArr[2];

	         // ... get RPC port
	         Integer curRPCPort = (Integer) rpcPortMap.get(tmpHostName);
	         
            curDSProject = new DSProject(tmpProjectRid, tmpProjectName, 
                                         tmpHostName, curRPCPort);
	         
				if (isProjectAccessible(curDSProject)) {

					retProjectList.add(curDSProject);
					// set the default folder RID
					// getDefaultFolderRId(jobsFolderName, curDSProject, srvcToken);
				} // end of if (isProjectAccessible)
			}
		}
		/*
		 * else { // the selected project doesn't exist throw new DSAccessException("No projects exist."); }
		 */

		return (retProjectList);
	} // end of getAvailableDSProjects()

	
	/**
	 * This method returns all existing DSprojects for the passed host and project name.
	 * 
	 * @param hostName
	 *           DataStage host name
	 * @param projectName
	 *           DataStage project name
	 * @param srvcToken
	 *           access token
	 * 
	 * @return List containing the projects existing on the DS Server
	 * 
	 * @throws DSAccessException
	 *            if an error occurred
	 */
	private List<Object[]> getNativeDSProjectList(String hostName, String projectName, ServiceToken srvcToken)
	        throws DSAccessException {
		StringBuffer   queryBuf;
		List<Object[]> retQueryResult;

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.entry("host = " + hostName + " - project = " + projectName);
		}

		// setup the query
		queryBuf = new StringBuffer();

		// first the 'common' part ...
		queryBuf.append("select rid(x), x.Name, x.HostName from x in "); //$NON-NLS-1$
		queryBuf.append(DataStageXPackage.eINSTANCE.getName());
		queryBuf.append("::"); //$NON-NLS-1$ 
		queryBuf.append(DataStageXPackage.eINSTANCE.getEClassifier("DSProject").getName()); //$NON-NLS-1$

		if (hostName != null || projectName != null) {
			queryBuf.append(" where "); //$NON-NLS-1$

			if (hostName != null) {
				queryBuf.append("x.HostName = \"" + hostName.toUpperCase() + "\""); //$NON-NLS-1$//$NON-NLS-2$
			} // end of if (hostName != null)
		} // end of if (hostName != null || projectName != null)

		if (projectName != null) {
			if (hostName != null) {
				queryBuf.append(" and "); //$NON-NLS-1$
			}
			queryBuf.append(" x.Name = \"" + projectName + "\""); //$NON-NLS-1$//$NON-NLS-2$
		} // end of if (projectName != null)

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.trace(TraceLogger.LEVEL_FINEST, "query = " + queryBuf);
		}

		retQueryResult = srvcToken.executeValueQuery(queryBuf.toString(), DataStageXPackage.eINSTANCE);

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.exit(String.valueOf(retQueryResult.size()));
		}

		return (retQueryResult);
	} // end of getNativeDSProjectList()


	/**
	 * This method returns the RID of the specified DS Object.
	 * 
	 * @param dsObjectName
	 *           DS object name
	 * @param dsProjectNameSpace
	 *           DS project namespace
	 * @param srvcToken
	 *           access token
	 * 
	 * @return RID of the DS object or null if the object could not be found
	 * 
	 * @throws DSAccessException
	 *            if an error occurred
	 */
	public String getRidForDSItem(String dsObjectName, String dsProjectNameSpace, ServiceToken srvcToken)
	      throws DSAccessException {
		String query = "select x from x in " + DataStageXPackage.eINSTANCE.getName() + "::" //$NON-NLS-1$   //$NON-NLS-2$
		             + DataStageXPackage.eINSTANCE.getEClassifier("DSItem").getName() + " where x.Name = \"" //$NON-NLS-1$//$NON-NLS-2$
		             + dsObjectName + "\" and x.ProjectNameSpace = \"" + dsProjectNameSpace + "\""; //$NON-NLS-1$//$NON-NLS-2$

		Iterator queryResult = srvcToken.executeObjectQuery(query, DataStageXPackage.eINSTANCE,
		                                                    QryOptions.ROOT_ONLY).iterator();
		String dsItemRid = null;
		if (queryResult != null && queryResult.hasNext()) {
			while (queryResult.hasNext()) {
				DSItem dsItem = (DSItem) queryResult.next();
				dsItemRid = dsItem.get_xmeta_repos_object_id();
			}
		}

		return (dsItemRid);
	} // end of getRidForDSItem

	
   private boolean isCategoryFiltered(String category)  {
      String  lookupName;
      int     idxStart;
      int     idxNextSeparator;
      boolean isFiltered;

      isFiltered = false;
      
      // determine first word in the passed category
      idxStart = DSFolder.DIRECTORY_SEPARATOR.length();
      if (category.startsWith(DSFolder.DIRECTORY_SEPARATOR)) {
         idxNextSeparator = category.indexOf(DSFolder.DIRECTORY_SEPARATOR, idxStart);
         
         if (idxNextSeparator > -1) {
            lookupName = category.substring(idxStart, idxNextSeparator);
         }
         else {
            lookupName = category.substring(idxStart);
         }
         
      }
      else {
         lookupName = category;
      }
      
      isFiltered = _FolderFilterSet.contains(lookupName);

      return(isFiltered);
   } // end of isCategoryFiltered()
   
	
	private boolean isProjectAccessible(DSProject dsProject) throws DSAccessException {
		boolean isAccessible = true;

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.entry("project = " + dsProject); //$NON-NLS-1$
		}

		// osuhre, 48129:
		// Don't throw exceptions from the UVAccessor out, log the error and
		// mark the project as not accessible
		
		// First check if the project is protected
		UVAccessor uvAccessor = new UVAccessor(dsProject);

		try {
			if (uvAccessor.connect()) {
				if (TraceLogger.isTraceEnabled()) {
					TraceLogger.trace(TraceLogger.LEVEL_FINER, "UV connection succeeded");
				}
				isAccessible = !uvAccessor.isProjectProtected();
				if (TraceLogger.isTraceEnabled()) {
					TraceLogger.trace(TraceLogger.LEVEL_FINER, "Project protected: " + !isAccessible);
				}
				uvAccessor.disconnect();
			}
			else {
				if (TraceLogger.isTraceEnabled()) {
					TraceLogger.trace(TraceLogger.LEVEL_FINER, "UV connection failed");
				}
				isAccessible = false;
			}

			// Now determine if the current user is authorized to update the
			// project
			if (isAccessible) {
				UserAccess userAccess = new UserAccess();
				int dsUserRole = userAccess.getDSUserRole(dsProject.getHostName(), dsProject.getName());
				
				if (TraceLogger.isTraceEnabled()) {
					TraceLogger.trace(TraceLogger.LEVEL_FINER, "DS User role: '" + UserAccess.getUserRoleString(dsUserRole) + "'  (" + dsUserRole + ")");
				}

				// verify user role
				if (dsUserRole != UserAccess.DSR_UROLE_DEVELOPER && 
				    dsUserRole != UserAccess.DSR_UROLE_PRODMGR   &&
				    dsUserRole != UserAccess.DSR_UROLE_ADMINISTRATOR) {
					if (TraceLogger.isTraceEnabled()) {
						TraceLogger.trace(TraceLogger.LEVEL_FINER, "DS User role is not sufficient for job generation");
					}
					isAccessible = false;
				}
			}
		} // end of try
		catch (DSAccessException dsAccessExcpt) {
			if (TraceLogger.isTraceEnabled()) {
				TraceLogger.trace(TraceLogger.LEVEL_FINER, "Caught DSAccessException");
				TraceLogger.traceException(dsAccessExcpt);
			}
			isAccessible = false;
			throw dsAccessExcpt;
		}
		catch (Exception excpt) {
			if (TraceLogger.isTraceEnabled()) {
				TraceLogger.trace(TraceLogger.LEVEL_FINER, "Caught generic Exception");
				TraceLogger.traceException(excpt);
			}
			isAccessible = false;
			// throw new DSAccessException("Error checking project access: ", excpt);
		}

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.exit(String.valueOf(isAccessible));
		}

		return (isAccessible);
	} // end of isProjectAccessible

	
	/**
	 * This method saves the passed JobDef in the XMeta repository and in the Universe DB.
	 * 
	 * @param jobDef
	 *           JobDef to be saved
	 * @param srvcToken
	 *           access token
	 * 
	 * @return true if the job has been deleted, false if there was no existing job
	 * 
	 * @throws DSAccessException
	 *            if an error occurred
	 */
	public void saveDSJob(DSJobDef jobDef, ServiceToken srvcToken) throws DSAccessException {
		throw new DSAccessException("the DataStage Service user must implement this method.");
	} // end of saveDSJob()
	
	
   public void setXMetaRepObjectBaseInfo(EObject xmetaEObj) {
   	XMetaRepositoryObject xmetaRepositoryObj = (XMetaRepositoryObject) xmetaEObj;
   	
      Date timeStamp = new Date();
      xmetaRepositoryObj.set_xmeta_created_by_user(XMETA_USER);
      xmetaRepositoryObj.set_xmeta_modified_by_user(XMETA_USER);
      xmetaRepositoryObj.set_xmeta_creation_timestamp(timeStamp);
      xmetaRepositoryObj.set_xmeta_modification_timestamp(timeStamp);
   } // end of setXMetaRepObjectBaseInfo()


// this method is for XMETA test only
	public XMetaRepositoryObject getXMetaObjectByRid(String dsRid, ServiceToken srvcToken) {
		XMetaRepositoryObject xmro = null;

		try {
			xmro = ((ServiceTokenImpl) srvcToken).getXMetaObjectByRid(dsRid);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return (xmro);
	}
// this method is for XMETA test only
} // end of class DataStageServiceImpl
