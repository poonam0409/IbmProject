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

/**
 * UVAccess
 * 
 * Interface to provide access to 
 * DataStage UV database
 */
public interface UVAccess {

	static final String IBM_COPYRIGHT_SHORT = Copyright.IBM_COPYRIGHT_SHORT;
	
	/**
	 * getJobList
	 * 
	 * return a List of DSJob objects that are registered
	 * for the given IDoc type and the given DataStage
	 * SAP connection
	 * 
	 * @param idocType
	 * @param dsSAPConnection
	 * @return
	 */
	public List<DSJob> getJobList(String idocType, String dsSAPConnection);
	
}
