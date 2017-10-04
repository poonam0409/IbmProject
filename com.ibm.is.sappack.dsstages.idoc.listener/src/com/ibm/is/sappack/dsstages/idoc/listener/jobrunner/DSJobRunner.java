//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2009, 2011                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.is.sappack.dsstages.idoc.listener.jobrunner
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.dsstages.idoc.listener.jobrunner;

import java.util.List;
import java.util.Map;

import com.ibm.is.sappack.dsstages.idoc.listener.IDocTypeConfiguration;

/**
 * DSJobRunner
 * 
 * Interface to automatically trigger
 * DataStage jobs upon incoming IDocs
 * from SAP
 */
public interface DSJobRunner {

	static final String IBM_COPYRIGHT_SHORT = Copyright.IBM_COPYRIGHT_SHORT;
	
	/**
	 * initialize
	 * 
	 * @param idocServerProperties
	 * @param idocTypeConfigurations
	 */
	public void initialize(Map<String, String> idocServerProperties, Map<String, IDocTypeConfiguration> idocTypeConfigurations);
	
	/**
	 * runDataStageJobs
	 * 
	 * run multiple DataStage jobs that
	 * are registered for the given IDocType
	 * 
	 * @param jobs
	 */
	public void runDataStageJobs(String idocType, List<DSJob> jobs);
	
	/**
	 * runDataStageJob
	 * 
	 * run a single DataStage job that 
	 * is registered for the given IDocType
	 * 
	 * @param job
	 */
	public void runDataStageJob(String idocType, DSJob job);
	
}


