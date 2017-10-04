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


import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.ibm.is.sappack.gen.common.StopWatch;
import com.ibm.is.sappack.gen.common.request.GetAllProjectsRequest;
import com.ibm.is.sappack.gen.common.request.GetAllProjectsResponse;
import com.ibm.is.sappack.gen.common.request.ResponseBase;
import com.ibm.is.sappack.gen.common.trace.TraceLogger;
import com.ibm.is.sappack.gen.common.util.DSProject;
import com.ibm.is.sappack.gen.server.common.service.ServiceToken;
import com.ibm.is.sappack.gen.server.common.util.DSAccessException;
import com.ibm.is.sappack.gen.server.datastage.DataStageAccessManager;


public class ProjectLister {

	private GetAllProjectsRequest request;
   private ResponseBase          response;

	static String copyright() { 
	   return com.ibm.is.sappack.gen.server.jobgenerator.Copyright.IBM_COPYRIGHT_SHORT; 
	}	
	
	
	public ProjectLister(GetAllProjectsRequest gaRequest, GetAllProjectsResponse response) throws DSAccessException {
	   
		this.request  = gaRequest;
		this.response = response;
		
		DataStageAccessManager.createInstance();
	}


	public List<String> getAllProjects() throws DSAccessException {
	   
      StopWatch stopWatch = new StopWatch(true);
		TraceLogger.entry();
		
		List<String> result = new ArrayList<String>();
		DataStageAccessManager accessManager = DataStageAccessManager.getInstance();
		ServiceToken           srvcToken     = accessManager.createServiceToken(request.getLocale());
      srvcToken.loginIntoDomain(request.getISUsername(), request.getISPassword(), 
                                request.getDomainServerName(), request.getDomainServerPort());
		
      try {
         stopWatch.stop();
         if (TraceLogger.isTraceEnabled()) {
            TraceLogger.trace(TraceLogger.LEVEL_FINEST, "'Service Token creation' runtime = " + stopWatch.toString() + ".");
         }
         stopWatch = new StopWatch(true);

         List projects = accessManager.getAvailableDSProjects(srvcToken);
         Iterator it = projects.iterator();
         while (it.hasNext()) {
            DSProject proj = (DSProject) it.next();
            String qualifiedProjectName = proj.getQualifiedName();
            result.add(qualifiedProjectName);
            TraceLogger.trace(TraceLogger.LEVEL_FINER, "Found project name: " + qualifiedProjectName);
         }
         srvcToken.commitTransaction();
         stopWatch.stop();
         
         // store the DS version
         if (response != null) {
            response.setDSServerVersion(accessManager.getDSVersion());
         }
      }
      finally {
         srvcToken.logoutFromDomain();
      }
		
      if (TraceLogger.isTraceEnabled()) {
         TraceLogger.trace(TraceLogger.LEVEL_FINEST, "'Get Available projects' runtime = " + stopWatch.toString() + ".");
         TraceLogger.exit();
      }
      
		return result;
	}

   public List<String> getAllProjectsSorted() throws DSAccessException {
      List<String> resultList = getAllProjects();
      
      Collator c = Collator.getInstance();
      c.setStrength(Collator.PRIMARY);
      
      Collections.sort(resultList, c);
      return(resultList);
   }

}
