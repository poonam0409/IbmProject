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
// Module Name : com.ibm.is.sappack.gen.server.jobgenerator
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************

package com.ibm.is.sappack.gen.server.jobgenerator;


import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.ibm.is.sappack.gen.common.JobGeneratorException;
import com.ibm.is.sappack.gen.common.request.GetAllParameterSetsRequest;
import com.ibm.is.sappack.gen.common.request.GetAllParameterSetsResponse;
import com.ibm.is.sappack.gen.common.request.ResponseBase;
import com.ibm.is.sappack.gen.common.trace.TraceLogger;
import com.ibm.is.sappack.gen.common.util.DSParamSet;
import com.ibm.is.sappack.gen.common.util.DSProject;
import com.ibm.is.sappack.gen.server.common.service.ServiceToken;
import com.ibm.is.sappack.gen.server.common.util.DSAccessException;
import com.ibm.is.sappack.gen.server.datastage.DataStageAccessManager;


/**
 * ParameterSetLister
 * 
 * Gets all Parameter sets
**/
public class ParameterSetLister {

   // -------------------------------------------------------------------------------------
   //                                       Subclasses
   // -------------------------------------------------------------------------------------
   private static class SetComparator implements Comparator<DSParamSet> {

      public int compare(DSParamSet parSet1, DSParamSet parSet2) {
         int  cmpRc;

         cmpRc = parSet1.getName().compareTo(parSet2.getName());

         return(cmpRc);
      } // end of compare()
   } // end of subclass SetComparator


   // -------------------------------------------------------------------------------------
   //                                       Member Variables
   // -------------------------------------------------------------------------------------
	private GetAllParameterSetsRequest request;
   private ResponseBase               response;


	static String copyright() { 
	   return com.ibm.is.sappack.gen.server.jobgenerator.Copyright.IBM_COPYRIGHT_SHORT; 
	}
	
	/**
	 * ParameterSetLister
	 * 
	 * @param gaRequest
	 * @throws JobGeneratorException
	 * @throws DSAccessException
	 */
	public ParameterSetLister(GetAllParameterSetsRequest gaRequest, GetAllParameterSetsResponse response) throws DSAccessException {
		this.request  = gaRequest;
      this.response = response;

      DataStageAccessManager.createInstance();
	} // end of ParameterSetLister()
	
	
	/**
	 * getAllParameterSets
	 *
	 * returns a list of all DataStage Parameter Sets for specified project
	 * 
	 * @throws DSAccessException
	 * 
	 * @return List
	 */
   public List getAllParameterSets() throws DSAccessException {
      
      DataStageAccessManager accessManager = DataStageAccessManager.getInstance();
      String                 hostName      = request.getDSHostName();
      String                 projectName   = request.getDSProjectName();
      Integer                dsRPCPort     = request.getDSServerRPCPort();
      
      if (TraceLogger.isTraceEnabled()) {
         TraceLogger.entry("Fetching DataStage parameter sets for project " +hostName+":"+dsRPCPort+"/"+ projectName);
      }
      
      ServiceToken srvcToken = accessManager.createServiceToken(request.getLocale());
      srvcToken.loginIntoDomain(request.getISUsername(), request.getISPassword(), 
                                  request.getDomainServerName(), request.getDomainServerPort());
      
      List<DSParamSet> paramSets = null;
      try {
         // get DS project
         DSProject dsProject = accessManager.getDSProject(hostName, dsRPCPort, projectName, srvcToken);
         if (dsProject == null)
         {
            throw new DSAccessException("103600E", new String[] { projectName, hostName } );
         }
         paramSets = accessManager.getDSParameterSetList(dsProject, srvcToken);

         if (TraceLogger.isTraceEnabled()) {
            TraceLogger.trace(TraceLogger.LEVEL_FINEST, "Sorting parameter sets ...");
         }

         Collections.sort(paramSets, new SetComparator());

         // store the DS version
         if (response != null) {
            response.setDSServerVersion(accessManager.getDSVersion());
         }
      } // end of try
      finally {
         srvcToken.logoutFromDomain();
      }

      if (TraceLogger.isTraceEnabled()) {
         TraceLogger.trace(TraceLogger.LEVEL_FINEST, "Fetched param sets: " + paramSets);
         TraceLogger.exit();
      }

      return(paramSets);
   } // end of getAllParameterSets()
   
} // end of class ParametersetLister
