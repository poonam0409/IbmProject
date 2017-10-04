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
// Module Name : com.ibm.is.sappack.gen.server.common.service
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************

package com.ibm.is.sappack.gen.server.common.service;


import java.util.List;

import org.eclipse.emf.ecore.EObject;


import DataStageX.DSJobDef;
import DataStageX.DSParameterSet;
import DataStageX.DSStageType;

import com.ibm.is.sappack.gen.common.util.DSFolder;
import com.ibm.is.sappack.gen.common.util.DSParamSet;
import com.ibm.is.sappack.gen.common.util.DSProject;
import com.ibm.is.sappack.gen.server.common.DSStageTypeEnum;
import com.ibm.is.sappack.gen.server.common.util.DSAccessException;


public interface DataStageService {
   // -------------------------------------------------------------------------------------
   //                                       Constants
   // -------------------------------------------------------------------------------------
	public static final String COPYRIGHT = com.ibm.is.sappack.gen.server.common.service.Copyright.IBM_COPYRIGHT_SHORT; 


   // -------------------------------------------------------------------------------------
   //                                   Interface methods
   // -------------------------------------------------------------------------------------
	public ServiceToken     createServiceToken(String locale) throws DSAccessException;

	public boolean          deleteDSJob(String jobName, ServiceToken srvcToken) throws DSAccessException;
	public boolean          doesJobExist(String jobName, ServiceToken srvcToken) throws DSAccessException;
	public void             saveDSJob(DSJobDef jobDef, ServiceToken srvcToken) throws DSAccessException;
	public boolean          doJobsWithPrefixExist(String jobName, ServiceToken srvcToken) throws DSAccessException;

   public String           getDSVersion();
	public DSStageType      getDSStageTypeDefinition(DSStageTypeEnum stageType, ServiceToken srvcToken) throws DSAccessException ;

	public String           getAllSapConnections(String dsHostName, Integer dsRPCPort, String dsProjectName, ServiceToken srvcToken) throws DSAccessException;
	public DSFolder         getDSFolder(DSProject dsProject, String targetFolderName, ServiceToken srvcToken) throws DSAccessException;
	public List<DSFolder>   getDSAllFoldersFiltered(DSProject project, ServiceToken srvcToken) throws DSAccessException;
	public List<DSFolder>   getDSFolderList(String projectRid, boolean isSubFolder, ServiceToken srvcToken) throws DSAccessException;
	public DSParameterSet   getDSParameterSet(String paramSetName, ServiceToken srvcToken) throws DSAccessException;
   public List<DSParamSet> getDSParameterSetList(DSProject project, ServiceToken srvcToken) throws DSAccessException;
	public List<DSProject>  getAvailableDSProjects(ServiceToken srvcToken) throws DSAccessException;
	public DSProject        getDSProject(String hostName, Integer dsRPCPort, String projectName, ServiceToken srvcToken) throws DSAccessException;

   public void             setXMetaRepObjectBaseInfo(EObject xmetaEObj); 

} // end of interface DataStageService
