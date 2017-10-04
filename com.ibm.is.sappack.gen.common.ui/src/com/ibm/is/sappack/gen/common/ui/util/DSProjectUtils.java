//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2011, 2012                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.is.sappack.gen.common.ui.util
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.common.ui.util;


import java.text.MessageFormat;
import java.util.Iterator;
import java.util.List;

import com.ibm.is.sappack.gen.common.Constants;
import com.ibm.is.sappack.gen.common.request.GetAllRequestBase;
import com.ibm.is.sappack.gen.common.request.JobGeneratorRequest;
import com.ibm.is.sappack.gen.common.util.DSFolder;


public final class DSProjectUtils {
	// -------------------------------------------------------------------------------------
	// Constants
	// -------------------------------------------------------------------------------------
   private static final String  DS_PROJECT_NAME_FULL  = "{0}:{1}/{2}";        //$NON-NLS-1$


	// -------------------------------------------------------------------------------------
	// Member Variables
	// -------------------------------------------------------------------------------------



   static String copyright() { 
      return com.ibm.is.sappack.gen.common.ui.util.Copyright.IBM_COPYRIGHT_SHORT; 
   }

   
   public static boolean doesFolderExistInList(String dsFolderName, List<DSFolder> dsFolderList, 
                                               boolean checkInFolderTree) {
      boolean folderExist = false;

      if (checkInFolderTree) {
         DSFolder curFolder;
         String   fullFolderName;

         Iterator<DSFolder> it = dsFolderList.iterator();

         while (it.hasNext() && !folderExist) {
            curFolder      = it.next();
            fullFolderName = curFolder.getDirectoryPath();

            folderExist    = fullFolderName.equals(dsFolderName);
         }
      }
      else {
         folderExist = dsFolderList.contains(dsFolderName);
      }

      return(folderExist);
   } // end of doesProjectExistInList()


	public static boolean doesProjectExistInList(String isDomainName, String dsProjectName, 
	                                             List<String> dsProjectList) {
      boolean projectExist = false;
      
      dsProjectName = normalizeProjectName(isDomainName.toUpperCase(), Constants.JOB_GEN_DEFAULT_DS_RPC_PORT, 
                                           dsProjectName);
      
      Iterator<String> it = dsProjectList.iterator();

      while (it.hasNext() && !projectExist) {
         String curProjName = it.next().trim();
         
         curProjName = normalizeProjectName(isDomainName, Constants.JOB_GEN_DEFAULT_DS_RPC_PORT, curProjName);
         
         if (curProjName.equals(dsProjectName)) {
            projectExist= true;
         } 
      }
	   
	   return(projectExist);
	} // end of doesProjectExistInList()
	
	
   public static String getHostFromProjectName(String dsProjectName, String isDomainName) { 
      String retDSHostName;

      int    slashIdx;
      
      slashIdx   = dsProjectName.indexOf('/');
      
      if (slashIdx > -1) {
         retDSHostName  = dsProjectName.substring(0, slashIdx);
      }
      else {
         retDSHostName = isDomainName.toUpperCase();
      }

      return(retDSHostName);
   } // end of getHostFromProjectName()


   private static String normalizeProjectName(String defHostname, int defRPCPort, String dsProjectName) {
      String normProjName;
      String dsHostName;
      int    dsRPCPort;
      int    slashIdx;
      int    dotIdx;
      
      dsHostName = defHostname;
      dsRPCPort  = defRPCPort;
      slashIdx   = dsProjectName.indexOf('/');
      if (slashIdx > -1) {
         dsHostName    = dsProjectName.substring(0, slashIdx);
         dsProjectName = dsProjectName.substring(slashIdx + 1);
         
         dotIdx = dsHostName.indexOf(':');
         if (dotIdx > -1) {
            dsRPCPort  = Integer.parseInt(dsHostName.substring(dotIdx + 1));                
            dsHostName = dsHostName.substring(0, dotIdx);
         }
      }
         
      normProjName = MessageFormat.format(DS_PROJECT_NAME_FULL, 
                                          new Object[] { dsHostName, String.valueOf(dsRPCPort), dsProjectName});
      
      return(normProjName);
   } // end of normalizeProjectName()
   
	
   public static String updateRequestData(String projectName, GetAllRequestBase request) {
      
      String  dsHostName = null;
      Integer dsSrvrRPCPort = null;
      
      int slashIdx = projectName.indexOf('/');
      if (slashIdx > -1) {
         dsHostName  = projectName.substring(0, slashIdx);
         projectName = projectName.substring(slashIdx + 1);
         
         int dotIdx = dsHostName.indexOf(':');
         if (dotIdx > -1) {
            dsSrvrRPCPort = Integer.valueOf(dsHostName.substring(dotIdx + 1));                
            dsHostName    = dsHostName.substring(0, dotIdx);
         }
      }
      
      request.setDSProjectName(projectName);
      request.setDSHostName(dsHostName);
      request.setDSServerRPCPort(dsSrvrRPCPort);
      
      return(projectName);
   } // end of updateRequestData()

   
   public static String updateRequestData(String isDomain, String projectName, JobGeneratorRequest jobGenRequest) {
      
      String dsHostName;
      String dsProjectName;
      Integer dsSrvrRPCPort = null;
      
      dsHostName = isDomain.toUpperCase();
      dsProjectName = projectName;
      int slashIdx = dsProjectName.indexOf('/');
      if (slashIdx > -1) {
         dsHostName    = dsProjectName.substring(0, slashIdx);
         dsProjectName = dsProjectName.substring(slashIdx + 1);
         
         int dotIdx = dsHostName.indexOf(':');
         if (dotIdx > -1) {
            dsSrvrRPCPort = Integer.valueOf(dsHostName.substring(dotIdx + 1));                
            dsHostName    = dsHostName.substring(0, dotIdx);
         }
      }

      jobGenRequest.setDSProjectName(dsProjectName);
      jobGenRequest.setDSHostName(dsHostName);
      jobGenRequest.setDSServerRPCPort(dsSrvrRPCPort);
      
      return(projectName);
   } // end of updateRequestData()
   
} // end of class DSProjectUtils
