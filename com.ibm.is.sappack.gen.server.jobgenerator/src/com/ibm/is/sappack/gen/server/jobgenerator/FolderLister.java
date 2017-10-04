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
import com.ibm.is.sappack.gen.common.request.GetAllFoldersRequest;
import com.ibm.is.sappack.gen.common.request.GetAllFoldersResponse;
import com.ibm.is.sappack.gen.common.request.ResponseBase;
import com.ibm.is.sappack.gen.common.trace.TraceLogger;
import com.ibm.is.sappack.gen.common.util.DSFolder;
import com.ibm.is.sappack.gen.common.util.DSProject;
import com.ibm.is.sappack.gen.server.common.service.ServiceToken;
import com.ibm.is.sappack.gen.server.common.util.DSAccessException;
import com.ibm.is.sappack.gen.server.datastage.DataStageAccessManager;


/**
 * FolderLister
 * 
 * Gets all DataStage folders for a given DataStage project
 */
public class FolderLister {

   private static class FolderComparator implements Comparator<DSFolder> {

      public int compare(DSFolder folder1, DSFolder folder2) {
         int cmpRc;

         cmpRc = folder1.getDirectoryPath().compareTo(folder2.getDirectoryPath());

         return(cmpRc);
      } // end of compare()
   } // end of subclass FolderComparator
   
   
	/** request instance */
	private GetAllFoldersRequest request;
   /** response instance */
   private ResponseBase         response;
	
	
	static String copyright() { 
	   return com.ibm.is.sappack.gen.server.jobgenerator.Copyright.IBM_COPYRIGHT_SHORT; 
	}
	
	/**
	 * FolderLister
	 * 
	 * @param gaRequest
	 * @throws JobGeneratorException
	 * @throws DSAccessException
	 */
	public FolderLister(GetAllFoldersRequest gaRequest, GetAllFoldersResponse response) throws DSAccessException {
		this.request  = gaRequest;
      this.response = response;

      DataStageAccessManager.createInstance();
	}
	
	/**
	 * getAllFolders
	 *
	 * returns a list of all DataStage folders
	 * 
	 * @throws DSAccessException
	 * @return
	 * @throws JobGeneratorException 
	 */
	public List<DSFolder> getAllFolders() throws DSAccessException {
		
		DataStageAccessManager accessManager = DataStageAccessManager.getInstance();
		String                 hostName      = request.getDSHostName();
		String                 projectName   = request.getDSProjectName();
      Integer                dsRPCPort     = request.getDSServerRPCPort();

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.entry("Fetching DataStage folder for project " +hostName+":"+dsRPCPort+"/"+ projectName);
		}

		ServiceToken accessToken = accessManager.createServiceToken(request.getLocale());
      accessToken.loginIntoDomain(request.getISUsername(), request.getISPassword(), 
                                  request.getDomainServerName(), request.getDomainServerPort());

      List<DSFolder> folders = null;
      try {
         // get sub folder of the 'Jobs' folder for the DataStage project
         DSProject dsProject = accessManager.getDSProject(hostName, dsRPCPort, projectName, accessToken);
         if (dsProject == null)
         {
            throw new DSAccessException("103600E", new String[] { projectName, hostName } );
         }

         folders = accessManager.getDSAllFoldersFiltered(dsProject, accessToken);

         // store the DS version
         if (response != null) {
            response.setDSServerVersion(accessManager.getDSVersion());
         }
      }
      finally {
         accessToken.logoutFromDomain();
		}

		if (TraceLogger.isTraceEnabled()) {
			TraceLogger.trace(TraceLogger.LEVEL_FINEST, "Fetched folders: "+ folders);
			TraceLogger.exit();
		}

		return folders;
	}

	
   public List<DSFolder> getAllFoldersSorted() throws DSAccessException {
      List<DSFolder> resultList = getAllFolders();
      
      try {
         if (TraceLogger.isTraceEnabled()) {
            TraceLogger.trace(TraceLogger.LEVEL_FINEST, "Sorting folder list ...");
         }
         
         Collections.sort(resultList, new FolderComparator());
      }
      catch(Exception pExcpt) {
         if (TraceLogger.isTraceEnabled()) {
            TraceLogger.traceException(pExcpt);
         }
         throw new DSAccessException("105300E", new String[] { pExcpt.toString() } );
      }
      
      return(resultList);
   }
}
