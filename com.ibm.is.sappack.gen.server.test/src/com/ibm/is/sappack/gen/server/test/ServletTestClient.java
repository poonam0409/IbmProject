//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2009, 2010                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the US Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM Information Server Job Generator
//                                                                             
// Module Name : com.ibm.is.sappack.gen.server.test
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.server.test;


import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.ibm.is.sappack.gen.common.request.GetAllFoldersRequest;
import com.ibm.is.sappack.gen.common.request.GetAllFoldersResponse;
import com.ibm.is.sappack.gen.common.request.GetAllParameterSetsRequest;
import com.ibm.is.sappack.gen.common.request.GetAllParameterSetsResponse;
import com.ibm.is.sappack.gen.common.request.GetAllProjectsRequest;
import com.ibm.is.sappack.gen.common.request.GetAllProjectsResponse;
import com.ibm.is.sappack.gen.common.request.GetAllSapConnectionsRequest;
import com.ibm.is.sappack.gen.common.request.GetAllSapConnectionsResponse;
import com.ibm.is.sappack.gen.common.request.JobGeneratorRequest;
import com.ibm.is.sappack.gen.common.request.JobGeneratorRequestResult;
// import com.ibm.is.sappack.gen.common.request.RequestJobTypeIDocLoad;
import com.ibm.is.sappack.gen.common.request.ServerRequestUtil;
import com.ibm.is.sappack.gen.common.request.ValidateDataRequest;
import com.ibm.is.sappack.gen.common.request.ValidateDataResponse;
import com.ibm.is.sappack.gen.common.util.DSFolder;
import com.ibm.is.sappack.gen.common.util.DSParamSet;


public class ServletTestClient 
{
   static String copyright()
   { 
   	return com.ibm.is.sappack.gen.server.test.Copyright.IBM_COPYRIGHT_SHORT; 
   }


   public static void main(String[] args) throws Exception
   {
      JobGeneratorRequest jobGenReq;
      String              jobName = null;
      String              jobSuffix;
      SimpleDateFormat    dateFormatter;
      int                 serverId;

      dateFormatter = new SimpleDateFormat("dd_MM_HH_mm_ss");
      jobSuffix     = "_" + dateFormatter.format(Calendar.getInstance().getTime());
      jobName       = "aServletTestJob" + jobSuffix;             

		// create the JobRequest ...
		jobGenReq = new JobGeneratorRequest();
		serverId = DebugTestClient.SERVER_ID_DEBUG_TEST;
		DebugTestClient.setServerData(jobGenReq, serverId);

		DebugTestClient.createABAPExtractTest(jobName, "T001S (1 LT).dbm", jobGenReq);
		
		// send the request
		JobGeneratorRequestResult sendResult = (JobGeneratorRequestResult) ServerRequestUtil.send(jobGenReq);
		System.out.println("Created jobs in " + sendResult.getJobRuntime() + "s: extract ("
			+ sendResult.getIDocExtractJobNumber() + ") - load (" + sendResult.getIDocLoadJobNumber()
			+ ") - log.tables (" + sendResult.getLogicalTablesExtractJobNumber() + ") - MIHLoad ("
			+ sendResult.getMIHLoadJobNumber() + ") - mvment (" + sendResult.getMovementJobNumber()
			+ ")");
		if (sendResult.getFailedJobsList().size() > 0) {
			System.out.println("Failed jobs: " + sendResult.getFailedJobsList());
		}
		String msgArr[] = sendResult.getMessages();
		if (sendResult.containsErrors()) {
			System.out.println("result contains errors = " + sendResult.get1stMessage());
			System.out.println("details = " + sendResult.getDetailedInfo());
		}
		for (int idx = 0; idx < msgArr.length; idx++) {
			System.out.println("  msg = " + msgArr[idx]);
		}
		// System.out.println("Job creation zip    = " +
		// sendResult.getABAPCodeZipStream());
		// showZip(sendResult.getABAPCodeZipStream());
		// saveZip(sendResult.getABAPCodeZipStream(),
		// "E:\\temp\\MyAnotherZip.zip");
   }

	
   public static void saveZip(InputStream pInputStream, String filename)
   {
      final int BUF_LEN = 1024;
      byte buf[] = new byte[BUF_LEN];
      int  len;
      try
      {
         InputStream ois = pInputStream;
         
         FileOutputStream fos = new FileOutputStream(filename);
         len = ois.read(buf, 0, BUF_LEN);
         while(len > 0)
         {
            fos.write(buf, 0, len);
            len = ois.read(buf);
         }
         fos.close();
      }
      catch(IOException ioExcpt)
      {
         ioExcpt.printStackTrace();
      }
   }
   
   public static void showZip(InputStream pInpStream)
   {
      ZipInputStream       vZipInputStream;
      ZipEntry             vZipEntry;
      String               vEntryName;
      byte                 vContentAsArr[];

      // create the required input streams
      vZipInputStream = new ZipInputStream(pInpStream);
      
      // ==> extract the content of the zip entry into a byte array
      // ==> add it to the process template (dependent on the file extension
      try
      {
         // check if the 1st entry is a valid ZipEntry ...
         vZipEntry = vZipInputStream.getNextEntry();
         if (vZipEntry == null)
         {
            throw new RuntimeException("no zip format or empty zip data");
         } // end of if (vZipEntry == null)

         // process all zip entries
         while(vZipEntry != null) 
         {
            // check the entry name i.e. it's file extentson
            vEntryName = vZipEntry.getName();

            // read and expand the zip entry content
            BufferedOutputStream  vBufOutStream;
            ByteArrayOutputStream vByteArrOutStream;
            byte                  vReadBuffer[];
            int                   vReadCounter;

            final int TMP_BUF_SIZE = 1024;
            
            // read the file content
            // create the required streams and buffers
            vByteArrOutStream = new ByteArrayOutputStream();
            vBufOutStream     = new BufferedOutputStream(vByteArrOutStream);
            vReadBuffer       = new byte[TMP_BUF_SIZE];

            // write the files to the byte array output stream
            while ((vReadCounter = vZipInputStream.read(vReadBuffer, 0, TMP_BUF_SIZE)) != -1) 
            {
               vBufOutStream.write(vReadBuffer, 0, vReadCounter);
            } // end of while ((vReadCounter = pZipInpStream.read(vReadBuffer, 0, TMP_BUF_SIZE)) != -1)

            // flush and close all streams
            vBufOutStream.flush();
            vBufOutStream.close();
            vByteArrOutStream.flush();
            vByteArrOutStream.close();
            vContentAsArr = vByteArrOutStream.toByteArray();

            
            System.out.println("entry = " + vEntryName); 
            System.out.println("Unpacked zip entry size = " + vContentAsArr.length); 
            System.out.println("content = " + new String(vContentAsArr).substring(0, 80) + " ..."); 

            // next Zip entry
            vZipEntry = vZipInputStream.getNextEntry(); 
         } // end of while(vZipEntry != null)
      } // end of try
      catch(IOException pIOExcpt)
      {
         throw new RuntimeException("Unexpected error: " + pIOExcpt);
      } // end of catch(IOException pIOExcpt)
   }

   public static void getAllFolders(JobGeneratorRequest jobGenReq) throws Exception
   {
      GetAllFoldersRequest gafr = new GetAllFoldersRequest();
      gafr.setDomainServerPort(jobGenReq.getDomainServerPort());
      gafr.setDomainServerName(jobGenReq.getDomainServerName());
      gafr.setISUsername(jobGenReq.getISUsername());
      gafr.setISPassword(jobGenReq.getISPassword());
      gafr.setDSHostName(jobGenReq.getDSHostName());
      gafr.setDSProjectName(jobGenReq.getDSProjectName());
      
      GetAllFoldersResponse reqResponse = (GetAllFoldersResponse) ServerRequestUtil.send(gafr);
      if (reqResponse.containsErrors()) {
         System.err.println(" msg = " + reqResponse.getDetailedInfo());
         String msgArr[] = reqResponse.getMessages();
         for(int idx = 0; idx < msgArr.length; idx ++)
         {
            System.err.println(" msg = " + msgArr[idx]);
         }
      }
      else {
//         ProjectLister projLister = new ProjectLister(gapr, reqResponse);
//         projects = projLister.getAllProjectsSorted();
//         reqResponse.setProjects(projects);
//         reqResponse = (GetAllProjectsResponse) ServerRequestUtil.loadResponseFromHTTPResult(gapr, 
//                                                                                             reqResponse.toXML(),
//                                                                                             null);
         List<DSFolder> folders = reqResponse.getFolders();
         System.out.println("number of folders: " + folders.size()); 
         System.out.println("folders: " + folders); 
      }
      System.exit(0);
   }

   public static void getAllProjects(JobGeneratorRequest jobGenReq) throws Exception
   {
      GetAllProjectsRequest gapr = new GetAllProjectsRequest();
      gapr.setDomainServerPort(jobGenReq.getDomainServerPort());
      gapr.setDomainServerName(jobGenReq.getDomainServerName());
      gapr.setISUsername(jobGenReq.getISUsername());
      gapr.setISPassword(jobGenReq.getISPassword());
      
      GetAllProjectsResponse reqResponse = (GetAllProjectsResponse) ServerRequestUtil.send(gapr);
      if (reqResponse.containsErrors()) {
         System.err.println(" msg = " + reqResponse.getDetailedInfo());
         String msgArr[] = reqResponse.getMessages();
         for(int idx = 0; idx < msgArr.length; idx ++)
         {
            System.err.println(" msg = " + msgArr[idx]);
         }
      }
      else {
         List<String> projects = reqResponse.getProjects();
         System.out.println("number of projects: " + projects.size()); 
         System.out.println("projects: " + projects); 
      }
      System.exit(0);
   }

   public static void getAllParameterSets(JobGeneratorRequest jobGenReq) throws Exception
   {
      GetAllParameterSetsRequest gapr = new GetAllParameterSetsRequest();
      gapr.setDomainServerPort(jobGenReq.getDomainServerPort());
      gapr.setDomainServerName(jobGenReq.getDomainServerName());
      gapr.setISUsername(jobGenReq.getISUsername());
      gapr.setISPassword(jobGenReq.getISPassword());
      gapr.setDSHostName(jobGenReq.getDSHostName());
      gapr.setDSProjectName(jobGenReq.getDSProjectName());
      
      GetAllParameterSetsResponse reqResponse = (GetAllParameterSetsResponse) ServerRequestUtil.send(gapr);
      if (reqResponse.containsErrors()) {
         System.err.println(" msg = " + reqResponse.getDetailedInfo());
         String msgArr[] = reqResponse.getMessages();
         for(int idx = 0; idx < msgArr.length; idx ++)
         {
            System.err.println(" msg = " + msgArr[idx]);
         }
      }
      else {
         List<DSParamSet> paramSets = reqResponse.getParameterSets();
         System.out.println("number of parameters sets: " + paramSets.size()); 
         if (paramSets.size() > 0)
         {
//            System.out.println("param sets: " + paramSets);
            DSParamSet curSet;
            Iterator<DSParamSet> iter = paramSets.iterator();
            while(iter.hasNext()) {
               curSet = (DSParamSet) iter.next();   
               System.out.println("name = " + curSet.getName());
               System.out.println("params = " + curSet.getParams());
            }
         }
      }
      
      
      System.exit(0);
   }

   public static void getAllSAPConnections(JobGeneratorRequest jobGenReq) throws Exception
   {
      GetAllSapConnectionsRequest gsapcr = new GetAllSapConnectionsRequest();
      gsapcr.setDomainServerPort(jobGenReq.getDomainServerPort());
      gsapcr.setDomainServerName(jobGenReq.getDomainServerName());
      gsapcr.setISUsername(jobGenReq.getISUsername());
      gsapcr.setISPassword(jobGenReq.getISPassword());
      String dsHostName = jobGenReq.getDSHostName();
      if (dsHostName == null)
      {
         dsHostName = jobGenReq.getDomainServerName().toUpperCase();
      }
      gsapcr.setDSHostName(dsHostName);
      gsapcr.setDSProjectName(jobGenReq.getDSProjectName());
      
      GetAllSapConnectionsResponse reqResponse = (GetAllSapConnectionsResponse) ServerRequestUtil.send(gsapcr);
      if (reqResponse.containsErrors()) {
         System.err.println(" msg = " + reqResponse.getDetailedInfo());
         String msgArr[] = reqResponse.getMessages();
         for(int idx = 0; idx < msgArr.length; idx ++)
         {
            System.err.println(" msg = " + msgArr[idx]);
         }
      }
      else {
         Map sapConnMap;

         sapConnMap = reqResponse.getConnectionsMap();
         System.out.println("Number of Connections: " + sapConnMap.size()); 
         System.out.println("SAP Connections: " + sapConnMap); 
      }

      System.exit(0);
   }

   public static void validateProject(JobGeneratorRequest jobGenReq) throws Exception
   {
      ValidateDataRequest vdr = new ValidateDataRequest();
      vdr.setDomainServerPort(jobGenReq.getDomainServerPort());
      vdr.setDomainServerName(jobGenReq.getDomainServerName());
      vdr.setISUsername(jobGenReq.getISUsername());
      vdr.setISPassword(jobGenReq.getISPassword());
      vdr.setDSJobNamePrefix("AServletTestJob_08_10_17_05_19ABAPExtract");
      vdr.setDSHostName(jobGenReq.getDSHostName());
      vdr.setDSProjectName(jobGenReq.getDSProjectName());
      
      ValidateDataResponse reqResponse = (ValidateDataResponse) ServerRequestUtil.send(vdr);
      if (reqResponse.containsErrors()) {
         System.err.println(" msg = " + reqResponse.getDetailedInfo());
         String msgArr[] = reqResponse.getMessages();
         for(int idx = 0; idx < msgArr.length; idx ++)
         {
            System.err.println(" msg = " + msgArr[idx]);
         }
      }
      else {
         System.out.println("Result: OK");
      }
      System.exit(0);
   }

}
