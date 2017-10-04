//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2009, 2014                                              
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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.ibm.is.sappack.gen.common.FileStageData;
import com.ibm.is.sappack.gen.common.ODBCStageData;
import com.ibm.is.sappack.gen.common.StopWatch;
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
import com.ibm.is.sappack.gen.common.request.JobParamData;
import com.ibm.is.sappack.gen.common.request.RequestJobTypeABAPExtract;
import com.ibm.is.sappack.gen.common.request.RequestJobTypeIDocExtract;
import com.ibm.is.sappack.gen.common.request.RequestJobTypeIDocLoad;
import com.ibm.is.sappack.gen.common.request.RequestJobTypeMovement;
import com.ibm.is.sappack.gen.common.request.RequestJobTypeTranscoding;
import com.ibm.is.sappack.gen.common.request.SAPSystemData;
import com.ibm.is.sappack.gen.common.request.ServerRequestUtil;
import com.ibm.is.sappack.gen.common.util.DSFolder;
import com.ibm.is.sappack.gen.common.util.DSParamSet;
import com.ibm.is.sappack.gen.server.common.util.DSAccessException;
import com.ibm.is.sappack.gen.server.jobgenerator.FolderLister;
import com.ibm.is.sappack.gen.server.jobgenerator.JobGenerator;
import com.ibm.is.sappack.gen.server.jobgenerator.ParameterSetLister;
import com.ibm.is.sappack.gen.server.jobgenerator.ProjectLister;
import com.ibm.is.sappack.gen.server.jobgenerator.SapConnectionLister;

public class DebugTestClient {
	public static final String MODEL_FILE_PATH      = "..\\Miscellaneous Data Models\\";
	public static final int    SERVER_ID_DEBUG_TEST = 100;

	public static void main(String[] args) {
		StopWatch overAllStopWatch;
		JobGeneratorRequest jobGenReq;
		JobGenerator jg;
		JobGeneratorRequestResult result;
		String jobName = null;
		String jobSuffix;
		SimpleDateFormat dateFormatter;
		int serverId;

		dateFormatter = new SimpleDateFormat("dd_MM_HH_mm_ss");
		jobSuffix     = "_" + dateFormatter.format(Calendar.getInstance().getTime());
		jobName       = "aDbgTestJob" + jobSuffix;

		overAllStopWatch = new StopWatch(true);
		try {
			// create the JobRequest ...
			jobGenReq = new JobGeneratorRequest();
			serverId  = SERVER_ID_DEBUG_TEST;
			setServerData(jobGenReq, serverId);

			getAllProjects(jobGenReq);
			// getAllFolders(jobGenReq);
			// getAllSAPConnections(jobGenReq);
			// getAllParameterSets(jobGenReq);

			jobGenReq.addJobParameter(new JobParamData("MyCustomerNameVar",	"Customer Name", JobParamData.JOB_PARAM_TYPE_STRING,
			                          "Hallo", ""));
			jobGenReq.addJobParameter(new JobParamData("MySurrogateKeyFile", "MySurrogateKeyFile",
			                          JobParamData.JOB_PARAM_TYPE_PATHNAME,	"e:\\temp\\MySurrogateKeyFile.txt", "no description"));
			jobGenReq.addJobParameter(new JobParamData("MySAPConnection", "MySAPConnection",
			                          JobParamData.JOB_PARAM_TYPE_STRING, "BOCASAPIDES100", "SAP Connection"));
			jobGenReq.setJobDescription("HansX hello world abcdefghijklmnopqrstuvwxyz");

			// createABAPExtractTest(jobName, "aaMBEW1.dbm", jobGenReq);
			// createABAPExtractTest(jobName, "aaKNA1EOra.dbm", jobGenReq);
			// createABAPExtractTest(jobName, "aamaraDB2.dbm", jobGenReq);
			// createABAPExtractTest(jobName, "aamaraOra.dbm", jobGenReq);
			// createABAPExtractTest(jobName, "aaaABAPTest.dbm", jobGenReq);
			// createABAPExtractTest(jobName, "LDM_ZYSLTBL1.dbm", jobGenReq);
			// createABAPExtractTestOracle(jobName, "OracleTest.dbm",
			// jobGenReq);

			// * * * * * * * * A B A P - E X T R A C T * * * * * * * *
			// ---------------------------------------------------------
			createABAPExtractTest(jobName, "T001S-BaseDT.dbm", jobGenReq);

			// * * * * * * * * I D O C (Load & Extract) * * * * * * * *
			// ----------------------------------------------------------
			// createFullIDocLoadJob(jobName, "8MB-Test.dbm", jobGenReq);
			// createIDocExtractJob(jobName, "debmas06.dbm", jobGenReq, false);
			// createILSJob(jobName, "DEBMAS06-L-2Seg-V7.dbm", "DEBMAS06",
			// jobGenReq);

			// * * * * * * * * M O V E M E N T * * * * * * * *
			// ---------------------------------------------------------
			// createTransodingJob(jobName, jobGenReq);
			// createMovementJob(jobName, jobGenReq);

			jg = new JobGenerator(jobGenReq);
			try {
				result = jg.createJobs();
			} catch (Exception excpt) {
				result = new JobGeneratorRequestResult(jobGenReq);
				result.setException(excpt);
			}

			result = (JobGeneratorRequestResult) ServerRequestUtil.loadResponseFromHTTPResult(jobGenReq,
			                                                                                  result.toXML(),
			                                                                                  new ByteArrayInputStream(new byte[0]));

			overAllStopWatch.stop();
			System.out.println("Created jobs in " + overAllStopWatch
					+ ": extract (" + result.getIDocExtractJobNumber()
					+ ") - load (" + result.getIDocLoadJobNumber()
					+ ") - log.tables ("
					+ result.getLogicalTablesExtractJobNumber()
					+ ") - mvment (" + result.getMovementJobNumber() + ")");
			System.out.println(result.get1stMessage());
			String failedJobString = convertListToString(result.getFailedJobsList());
			if (failedJobString.length() > 0) {
				System.out.println("Failed jobs: " + failedJobString);
			}
			String msgArr[] = result.getMessages();
			for (int idx = 0; idx < msgArr.length; idx++) {
				System.out.println(" msg = " + msgArr[idx]);
			}
			System.out.println("result contains errors = " + result.containsErrors());
			if (result.containsErrors()) {
				System.out.println("details:" + result.getDetailedInfo());
				System.out.println(result.getDetailedInfo());
			}
		} // end of try
		catch (Exception pExcpt) {
			System.err.println("ERROR: " + pExcpt.getMessage());
			pExcpt.printStackTrace();
			if (pExcpt.getCause() != null) {
				System.err.println("ERROR: " + pExcpt.getCause().getMessage());
			}
		} // end of catch(JobGeneratorException pJobGenExcpt)
	} // end of main()


	public static String convertListToString(List list) {
		StringBuffer tmpBuffer = new StringBuffer();

		if (list != null) {
			Iterator listIter = list.iterator();
			boolean isFirst = true;
			while (listIter.hasNext()) {
				if (isFirst) {
					isFirst = false;
				} else {
					tmpBuffer.append(ServerRequestUtil.PARAM_SEPERATOR);
				}

				tmpBuffer.append(listIter.next().toString());
			}
		}
		return tmpBuffer.toString();
	}

	
	public static void createIDocExtractJob(String jobNamePrefix, String physModelId, JobGeneratorRequest jobGenReq,
	                                        boolean createCheckTables) throws Exception {
		String physModelFile;

		physModelFile = MODEL_FILE_PATH + physModelId;
		jobGenReq.addModel(physModelId, new File(physModelFile));

		RequestJobTypeIDocExtract jType = new RequestJobTypeIDocExtract(
				jobNamePrefix + "Extract", physModelId);
		jType.setSAPSystem(new SAPSystemData("PFUETZE"));
		ODBCStageData odbcData = new ODBCStageData("SAPTEST");
		jType.setPersistenceData(odbcData);
		FileStageData fileData = new FileStageData(
				"C:\\temp\\SAPSegmentX_{0}.txt");
		fileData.setCodePageId(FileStageData.CODE_PAGE_UTF8);
		// fsd.setQuote("--");
		// jType.setPersistenceData(fileData);
		// jType.addColumDerivation("VarChar", "UpCase({0})");
		// jType.addColumDerivation("CHAR", "LowCase({0})");
		// jType.setUseOflineProcessing(true);
		jobGenReq.addJobType(jType);
		jobGenReq.setCreateV7Stage(true);

		if (createCheckTables) {
			RequestJobTypeABAPExtract jtCTExtract = new RequestJobTypeABAPExtract(jobNamePrefix + "CheckTableExtract", physModelId);
			ODBCStageData osd = new ODBCStageData("MyDataSource");
			osd.setInsertMode(ODBCStageData.INSERT_MODE_TRUNCATE);
			jtCTExtract.setPersistenceData(osd);
			jtCTExtract.setPersistenceData(new FileStageData("E:/temp/CheckTablesExtract_{0}.txt", true,
			                                                 FileStageData.UPDATE_MODE_OVERWRITE));
			jtCTExtract.setSAPSystem(new SAPSystemData("BOCASAPIDES5"));
			jtCTExtract.setRFCGateway("bocasapides5.bocaraton.ibm.com", "sapgw00");
			jtCTExtract.addRFCDestinationProgramId("ZSAPPERFCN1",  "ZSAPPEIDN1");
			jtCTExtract.addRFCDestinationProgramId("ZSAPPERFCN2",  "ZSAPPEIDN2");
			jtCTExtract.addRFCDestinationProgramId("ZSAPPERFCN3",  "ZSAPPEIDN3");
			jtCTExtract.addRFCDestinationProgramId("ZSAPPERFCN4",  "ZSAPPEIDN4");
			jtCTExtract.addRFCDestinationProgramId("ZSAPPERFCN5",  "ZSAPPEIDN5");
			jtCTExtract.addRFCDestinationProgramId("ZSAPPERFCN6",  "ZSAPPEIDN6");
			jtCTExtract.addRFCDestinationProgramId("ZSAPPERFCN7",  "ZSAPPEIDN7");
			jtCTExtract.addRFCDestinationProgramId("ZSAPPERFCN8",  "ZSAPPEIDN8");
			jtCTExtract.addRFCDestinationProgramId("ZSAPPERFCN9",  "ZSAPPEIDN9");
			jtCTExtract.addRFCDestinationProgramId("ZSAPPERFCN10", "ZSAPPEIDN10");
			jtCTExtract.addRFCDestinationProgramId("ZSAPPERFCN11", "ZSAPPEIDN11");
			jtCTExtract.addRFCDestinationProgramId("ZSAPPERFCN12", "ZSAPPEIDN12");
			jtCTExtract.addRFCDestinationProgramId("ZSAPPERFCN13", "ZSAPPEIDN13");
			jtCTExtract.addRFCDestinationProgramId("ZSAPPERFCN14", "ZSAPPEIDN14");
			jtCTExtract.addRFCDestinationProgramId("ZSAPPERFCN15", "ZSAPPEIDN15");
			jtCTExtract.addRFCDestinationProgramId("ZSAPPERFCN16", "ZSAPPEIDN16");
			jtCTExtract.addRFCDestinationProgramId("ZSAPPERFCN17", "ZSAPPEIDN17");
			jtCTExtract.addRFCDestinationProgramId("ZSAPPERFCN18", "ZSAPPEIDN18");
			jtCTExtract.addRFCDestinationProgramId("ZSAPPERFCN19", "ZSAPPEIDN19");
			jtCTExtract.setFlowNumberPerJob(19);
			jobGenReq.addJobType(jtCTExtract);
		}
	}

	public static void createILSJob(String jobNamePrefix, String physModelFile, String sapMsgType,
	                                JobGeneratorRequest jobGenReq) throws Exception {

		jobGenReq.addModel(physModelFile, new File(MODEL_FILE_PATH + physModelFile));

		RequestJobTypeIDocLoad jtLoad = new RequestJobTypeIDocLoad(jobNamePrefix + "LoadILS", physModelFile, sapMsgType);
		// jtLoad.setIDOCLoadStatus("c:\\TEMP\\idocloadstatus.txt",
		// "DANGEROUSGOODS");
		jtLoad.setIDOCLoadStatus("c:\\temp\\MySurrogateKeyFile.txt", "MyCustomerNameVar");
		jobGenReq.setCreateV7Stage(true);
		ODBCStageData odbcdata = new ODBCStageData("REMOTESAPDB");
		// odbcdata = new ODBCStageData("ORCL");
		odbcdata = new ODBCStageData("SAPTEST");
		// odbcdata.setInsertMode(ODBCStageData.INSERT_MODE_TRUNCATE);
		// odbcdata.setSQLWhereCondition("CW_LOAD_ID<2");
		// odbcdata.setAlternateSchemaName("HANSX3");
		jtLoad.setPersistenceData(odbcdata);
		jtLoad.setSAPSystem(new SAPSystemData("PFUETZE"));
		// jtLoad.addColumDerivation("VarChar", "UpCase({0})");

		jobGenReq.addJobType(jtLoad);
	}

	public static void createFullIDocLoadJob(String jobNamePrefix, String physModelId,
	                                         JobGeneratorRequest jobGenReq) throws Exception {

		jobGenReq.addModel(physModelId, new File(MODEL_FILE_PATH + physModelId));

		RequestJobTypeIDocLoad jtLoad = new RequestJobTypeIDocLoad(jobNamePrefix + "FullIDocLoad", physModelId, "MATMAS");
		ODBCStageData odbcdata = new ODBCStageData("REMOTESAPDB");
		odbcdata.setInsertMode(ODBCStageData.INSERT_MODE_REPLACE);
		jtLoad.setPersistenceData(odbcdata);
		jtLoad.setSAPSystem(new SAPSystemData("BOCASAPIDES5"));

		jobGenReq.addJobType(jtLoad);

		RequestJobTypeABAPExtract jtCTExtract = new RequestJobTypeABAPExtract(jobNamePrefix + "FullIDocLoadABAP", physModelId);
		jtCTExtract.setPersistenceData(odbcdata);
		// jtCTExtract.setPersistenceData(new
		// FileStageData("E:/temp/CheckTablesExtract_{0}.txt", true,
		// FileStageData.UPDATE_MODE_OVERWRITE));
		jtCTExtract.setSAPSystem(new SAPSystemData("BOCASAPIDES5"));
		jtCTExtract.setRFCGateway("bocasapides5.bocaraton.ibm.com", "sapgw00");
		jtCTExtract.addRFCDestinationProgramId("ZSAPPERFCN1", "ZSAPPEIDN1");
		jtCTExtract.addRFCDestinationProgramId("ZSAPPERFCN2", "ZSAPPEIDN2");
		jtCTExtract.addRFCDestinationProgramId("ZSAPPERFCN3", "ZSAPPEIDN3");
		jtCTExtract.addRFCDestinationProgramId("ZSAPPERFCN4", "ZSAPPEIDN4");
		jtCTExtract.addRFCDestinationProgramId("ZSAPPERFCN5", "ZSAPPEIDN5");
		jtCTExtract.addRFCDestinationProgramId("ZSAPPERFCN6", "ZSAPPEIDN6");
		jtCTExtract.addRFCDestinationProgramId("ZSAPPERFCN7", "ZSAPPEIDN7");
		jtCTExtract.addRFCDestinationProgramId("ZSAPPERFCN8", "ZSAPPEIDN8");
		jtCTExtract.addRFCDestinationProgramId("ZSAPPERFCN9", "ZSAPPEIDN9");
		jtCTExtract.addRFCDestinationProgramId("ZSAPPERFCN10", "ZSAPPEIDN10");
		jtCTExtract.addRFCDestinationProgramId("ZSAPPERFCN11", "ZSAPPEIDN11");
		jtCTExtract.addRFCDestinationProgramId("ZSAPPERFCN12", "ZSAPPEIDN12");
		jtCTExtract.addRFCDestinationProgramId("ZSAPPERFCN13", "ZSAPPEIDN13");
		jtCTExtract.addRFCDestinationProgramId("ZSAPPERFCN14", "ZSAPPEIDN14");
		jtCTExtract.addRFCDestinationProgramId("ZSAPPERFCN15", "ZSAPPEIDN15");
		jtCTExtract.addRFCDestinationProgramId("ZSAPPERFCN16", "ZSAPPEIDN16");
		jtCTExtract.addRFCDestinationProgramId("ZSAPPERFCN17", "ZSAPPEIDN17");
		jtCTExtract.addRFCDestinationProgramId("ZSAPPERFCN18", "ZSAPPEIDN18");
		jtCTExtract.addRFCDestinationProgramId("ZSAPPERFCN19", "ZSAPPEIDN19");
		jtCTExtract.addRFCDestinationProgramId("ZSAPPERFCN20", "ZSAPPEIDN20");
		jtCTExtract.addRFCDestinationProgramId("ZSAPPERFCN21", "ZSAPPEIDN21");
		jtCTExtract.addRFCDestinationProgramId("ZSAPPERFCN22", "ZSAPPEIDN22");
		jtCTExtract.addRFCDestinationProgramId("ZSAPPERFCN23", "ZSAPPEIDN23");
		jtCTExtract.addRFCDestinationProgramId("ZSAPPERFCN24", "ZSAPPEIDN24");
		jtCTExtract.addRFCDestinationProgramId("ZSAPPERFCN25", "ZSAPPEIDN25");
		jtCTExtract.addRFCDestinationProgramId("ZSAPPERFCN26", "ZSAPPEIDN26");
		jtCTExtract.addRFCDestinationProgramId("ZSAPPERFCN27", "ZSAPPEIDN27");
		jtCTExtract.addRFCDestinationProgramId("ZSAPPERFCN28", "ZSAPPEIDN28");
		jtCTExtract.addRFCDestinationProgramId("ZSAPPERFCN29", "ZSAPPEIDN29");
		jtCTExtract.addRFCDestinationProgramId("ZSAPPERFCN30", "ZSAPPEIDN30");
		jtCTExtract.addRFCDestinationProgramId("ZSAPPERFCN31", "ZSAPPEIDN31");
		jtCTExtract.addRFCDestinationProgramId("ZSAPPERFCN32", "ZSAPPEIDN32");
		jtCTExtract.addRFCDestinationProgramId("ZSAPPERFCN33", "ZSAPPEIDN33");
		jtCTExtract.addRFCDestinationProgramId("ZSAPPERFCN34", "ZSAPPEIDN34");
		jtCTExtract.addRFCDestinationProgramId("ZSAPPERFCN35", "ZSAPPEIDN35");
		jtCTExtract.setFlowNumberPerJob(35);
		jobGenReq.addJobType(jtCTExtract);

	}

	public static void createABAPExtractTest(String jobNamePrefix,
			String physModelName, JobGeneratorRequest jobGenReq)
			throws Exception {
		jobGenReq.addModel(physModelName, new File(MODEL_FILE_PATH + physModelName));

		RequestJobTypeABAPExtract jtCTExtract = new RequestJobTypeABAPExtract(jobNamePrefix + "ABAPExtract", physModelName);
		ODBCStageData osd = new ODBCStageData("PDAA_DB");
		osd = new ODBCStageData("SAPTEST");
		osd.setInsertMode(ODBCStageData.INSERT_MODE_TRUNCATE);
		osd.setFailOnTypeMismatch(false);
		jtCTExtract.setPersistenceData(osd);
		FileStageData fsd = new FileStageData("C:/temp/ABAPExtract_{tab}.txt",
				true, FileStageData.UPDATE_MODE_OVERWRITE);
		fsd.setNullFieldValue(FileStageData.NULL_FIELD_DEFAULT_VALUE);
		// jtCTExtract.setPersistenceData(fsd);
		jtCTExtract.setSAPSystem(new SAPSystemData("KYOTO"));
		// jtCTExtract.setSAPSystem(new SAPSystemData("SATURN2"));
		// jtCTExtract.setSAPSystem(new SAPSystemData("#MySAPConnection#"));

		jtCTExtract.setRFCGateway("bocasapides5.bocaraton.ibm.com", "sapgw00");
		jtCTExtract.setRFCGateway("kyoto.boeblingen.de.ibm.com", "sapgw00");
		// jtCTExtract.getRFCData().setDoCleanUpRFCDestAfterReq(true);
		// jtCTExtract.getRFCData().setDoCreateRFCDest(true);
		// jtCTExtract.setBackgroundProcessOptions("Z_HANSXVAR", 30, false,
		// true);

		jtCTExtract.addRFCDestinationProgramId("ZSAPRFCHX01", "ZSAPRFCHX01");
		jtCTExtract.addRFCDestinationProgramId("ZSAPRFCHX02", "ZSAPRFCHX02");
		jtCTExtract.addColumDerivation("VARCHAR", "UpCase({0})");
		Map derivationExceptioMap = new HashMap();
		derivationExceptioMap.put("T001S.USNAM", "trim({0})");
		derivationExceptioMap.put("T001S.SNAME", "DownCase({0})");
		jtCTExtract.setDerivationExceptionsMap(derivationExceptioMap);

		// jtCTExtract.getRFCData().setSuppressAbapProgValidation(true);
		jtCTExtract.setRFCEnabled(true);
		jobGenReq.setCreateV7Stage(true);
		jtCTExtract.setFlowNumberPerJob(2);
		jtCTExtract.setFlowNumberPerJob(1);

		jobGenReq.addJobType(jtCTExtract);
		// jobGenReq.setJobDescription("\nhallo Hansx\n.This is a simple\nTEST\n!!!\n");
	}

	public static void createABAPExtractTestOracle(String jobNamePrefix, String physModelName,
	                                               JobGeneratorRequest jobGenReq) throws Exception {
		jobGenReq.addModel(physModelName, new File(MODEL_FILE_PATH + physModelName));

		RequestJobTypeABAPExtract jtCTExtract = new RequestJobTypeABAPExtract(jobNamePrefix + "ABAPExtract", physModelName);
		ODBCStageData osd = new ODBCStageData("ORCL", "SYSTEM", "Oracle11g");
		osd.setInsertMode(ODBCStageData.INSERT_MODE_TRUNCATE);
		osd.setFailOnTypeMismatch(false);
		jtCTExtract.setPersistenceData(osd);
		jtCTExtract.setSAPSystem(new SAPSystemData("pfuetze", "hans", "saptest", "800", "EN"));
		jtCTExtract.setRFCGateway("pfuetze.boeblingen.de.ibm.com", "sapgw00");
		jtCTExtract.getRFCData().setDoCleanUpRFCDestAfterReq(true);
		jtCTExtract.getRFCData().setDoCreateRFCDest(true);

		jtCTExtract.addRFCDestinationProgramId("ZSAPRFCHX01ORA", "ZSAPRFCHX01ORA");
		jtCTExtract.addRFCDestinationProgramId("ZSAPRFCHX02ORA","ZSAPRFCHX02ORA");

		// jtCTExtract.getRFCData().setSuppressAbapProgValidation(true);
		jtCTExtract.setRFCEnabled(true);
		jobGenReq.setCreateV7Stage(true);
		jtCTExtract.setFlowNumberPerJob(1);

		jobGenReq.addJobType(jtCTExtract);
	}

	public static void createMovementJob(String jobNamePrefix, JobGeneratorRequest jobGenReq) throws Exception {
		String physModelIdSrc = null;
		String physModelIdTrg = null;

		physModelIdSrc = MODEL_FILE_PATH + "aaaMakt.dbm";
		physModelIdTrg = MODEL_FILE_PATH + "aaaMakt.dbm";
		physModelIdSrc = MODEL_FILE_PATH + "MARD.dbm";
		physModelIdTrg = physModelIdSrc;
		jobGenReq.addModel(physModelIdSrc, new File(physModelIdSrc));
		jobGenReq.addModel(physModelIdTrg, new File(physModelIdTrg));

		RequestJobTypeMovement jType = new RequestJobTypeMovement(jobNamePrefix + "Movement");
		jType.setFlowNumberPerJob(4);
		jType.setSrcPhysicalModelId(physModelIdSrc);
		jType.setTrgPhysicalModelId(physModelIdTrg);
		ODBCStageData srcODBCData = new ODBCStageData("ORCL");
		// srcODBCData.setSQLWhereCondition("MATNR = '5'");
		jType.setSrcPersistenceData(srcODBCData);
		ODBCStageData trgODBCData = new ODBCStageData("SAPTEST");
		trgODBCData.setInsertMode(ODBCStageData.INSERT_MODE_CREATE);
		trgODBCData.setAlternateSchemaName("HANSX99");
		jType.setTrgPersistenceData(trgODBCData);
		jType.setTrgPersistenceData(trgODBCData);
		// jType.setSrcPersistenceData(new FileStageData("E:/temp/src/",
		// false));
		// jType.setTrgPersistenceData(new FileStageData("E:/temp/trg/", false,
		// FileStageData.UPDATE_MODE_OVERWRITE));
		// jType.setRejectFilePath("E:\\temp\\rejects");

		jobGenReq.addJobType(jType);
	}

	public static void createTransodingJob(String jobNamePrefix, JobGeneratorRequest jobGenReq) throws Exception {
		String physModelIdSrc = null;
		String physModelIdTrg = null;
		String physModelIdChkTbl = null;

		physModelIdSrc = MODEL_FILE_PATH + "Def-TransCT-KNA1.dbm";
		physModelIdChkTbl = MODEL_FILE_PATH + "Def-TransCT-KNA1-CTs.dbm";
		physModelIdSrc = MODEL_FILE_PATH + "Def-TransCT-KNA1-All.dbm";
		physModelIdChkTbl = null;
		physModelIdTrg = MODEL_FILE_PATH + "Def-TransCT-KNA1-Target.dbm";
		jobGenReq.addModel(physModelIdSrc, new File(physModelIdSrc));
		jobGenReq.addModel(physModelIdTrg, new File(physModelIdTrg));
		// jobGenReq.addModel(physModelIdChkTbl, new File(physModelIdChkTbl));

		RequestJobTypeTranscoding jType = new RequestJobTypeTranscoding(jobNamePrefix + "Transcoding");
		jType.setFlowNumberPerJob(2);
		jType.setSrcPhysicalModelId(physModelIdSrc);
		jType.setTrgPhysicalModelId(physModelIdTrg);
		jType.setCTPhysicalModelId(physModelIdChkTbl);
		jType.setTargetLegacyID("RLOUT");
		jType.setTranscodeDomainValueFields(true);
		jType.setTranscodeReferenceFields(true);
		jType.setMarkUnmappedValues(true);

		jType.setSrcPersistenceData(new ODBCStageData("SAPTEST"));

		ODBCStageData trgODBCData = new ODBCStageData("SAPTEST");
		trgODBCData.setInsertMode(ODBCStageData.INSERT_MODE_CREATE);
		trgODBCData.setAlternateSchemaName("HANSX_TRG");
		jType.setTrgPersistenceData(trgODBCData);

		jobGenReq.addJobType(jType);
	}

	public static void getAllProjects(JobGeneratorRequest jobGenReq) throws Exception {
		GetAllProjectsRequest gapr = new GetAllProjectsRequest();
		gapr.setDomainServerPort(jobGenReq.getDomainServerPort());
		gapr.setDomainServerName(jobGenReq.getDomainServerName());
		gapr.setISUsername(jobGenReq.getISUsername());
		gapr.setISPassword(jobGenReq.getISPassword());

		GetAllProjectsResponse reqResponse = new GetAllProjectsResponse(gapr);
		ProjectLister projLister = new ProjectLister(gapr, reqResponse);
		List<String> projects = projLister.getAllProjectsSorted();
		reqResponse.setProjects(projects);
		reqResponse = (GetAllProjectsResponse) ServerRequestUtil.loadResponseFromHTTPResult(gapr, reqResponse.toXML(), null);
		projects = reqResponse.getProjects();
		System.out.println("number of projects: " + projects.size());
		System.out.println("projects: " + projects);
		System.exit(0);
	}

	public static void getAllFolders(JobGeneratorRequest jobGenReq)	throws Exception {
		GetAllFoldersRequest gapr = new GetAllFoldersRequest();
		gapr.setDomainServerPort(jobGenReq.getDomainServerPort());
		gapr.setDomainServerName(jobGenReq.getDomainServerName());
		gapr.setISUsername(jobGenReq.getISUsername());
		gapr.setISPassword(jobGenReq.getISPassword());
		gapr.setDSHostName(jobGenReq.getDSHostName());
		gapr.setDSProjectName(jobGenReq.getDSProjectName());

		GetAllFoldersResponse reqResponse = new GetAllFoldersResponse(gapr);
		List<DSFolder> folders;

		try {
			FolderLister folderLister = new FolderLister(gapr, reqResponse);
			folders = folderLister.getAllFoldersSorted();
			reqResponse.setFolders(folders);
		} catch (DSAccessException dsae) {
			reqResponse.setException(dsae);
		}
		reqResponse = (GetAllFoldersResponse) ServerRequestUtil.loadResponseFromHTTPResult(gapr, reqResponse.toXML(), null);
		folders = reqResponse.getFolders();
		System.out.println("number of folders: " + folders.size());
		String msgArr[] = reqResponse.getMessages();
		for (int idx = 0; idx < msgArr.length; idx++) {
			System.out.println(" msg = " + msgArr[idx]);
		}

		if (folders.size() > 0) {
			System.out.println("folders: " + folders);
			DSFolder curFolder;
			Iterator<DSFolder> iter = folders.iterator();

			while (iter.hasNext()) {
				curFolder = (DSFolder) iter.next();
				System.out.println("path = " + curFolder.getDirectoryPath());
				// System.out.println("pid = " + curFolder.getParentId());
			}
		}
		System.exit(0);
	}

	public static void getAllSAPConnections(JobGeneratorRequest jobGenReq) throws Exception {
		GetAllSapConnectionsRequest gsapcr = new GetAllSapConnectionsRequest();
		gsapcr.setDomainServerPort(jobGenReq.getDomainServerPort());
		gsapcr.setDomainServerName(jobGenReq.getDomainServerName());
		gsapcr.setISUsername(jobGenReq.getISUsername());
		gsapcr.setISPassword(jobGenReq.getISPassword());
		String dsHostName = jobGenReq.getDSHostName();
		if (dsHostName == null) {
			dsHostName = jobGenReq.getDomainServerName().toUpperCase();
		}
		gsapcr.setDSHostName(dsHostName);
		gsapcr.setDSProjectName(jobGenReq.getDSProjectName());

		GetAllSapConnectionsResponse reqResponse = new GetAllSapConnectionsResponse(gsapcr);
		Map sapConnMap;
		try {
			SapConnectionLister connLister = new SapConnectionLister(gsapcr, reqResponse);
			sapConnMap = connLister.getSAPConnections();
			reqResponse.setConnectionsMap(sapConnMap);
		} catch (DSAccessException dsae) {
			reqResponse.setException(dsae);
		}
		reqResponse = (GetAllSapConnectionsResponse) ServerRequestUtil.loadResponseFromHTTPResult(gsapcr, reqResponse.toXML(), null);
		sapConnMap = reqResponse.getConnectionsMap();
		System.out.println("Number of Connections: " + sapConnMap.size());
		System.out.println("SAP Connections: " + sapConnMap);
		System.out.println("result contains errors = " + reqResponse.get1stMessage());
		String msgArr[] = reqResponse.getMessages();
		for (int idx = 0; idx < msgArr.length; idx++) {
			System.out.println(" msg = " + msgArr[idx]);
		}
		System.exit(0);
	}

	public static void getAllParameterSets(JobGeneratorRequest jobGenReq) throws Exception {
		GetAllParameterSetsRequest gapr = new GetAllParameterSetsRequest();
		gapr.setDomainServerPort(jobGenReq.getDomainServerPort());
		gapr.setDomainServerName(jobGenReq.getDomainServerName());
		gapr.setISUsername(jobGenReq.getISUsername());
		gapr.setISPassword(jobGenReq.getISPassword());
		gapr.setDSHostName(jobGenReq.getDSHostName());
		gapr.setDSProjectName(jobGenReq.getDSProjectName());

		GetAllParameterSetsResponse reqResponse = new GetAllParameterSetsResponse(gapr);
		List paramSets;

		try {
			ParameterSetLister paramSetLister = new ParameterSetLister(gapr, reqResponse);
			paramSets = paramSetLister.getAllParameterSets();
			reqResponse.setParameterSets(paramSets);
		} catch (DSAccessException dsae) {
			reqResponse.setException(dsae);
		}
		reqResponse = (GetAllParameterSetsResponse) ServerRequestUtil.loadResponseFromHTTPResult(gapr, reqResponse.toXML(), null);
		paramSets = reqResponse.getParameterSets();
		System.out.println("number of parameters sets: " + paramSets.size());
		String msgArr[] = reqResponse.getMessages();
		for (int idx = 0; idx < msgArr.length; idx++) {
			System.out.println(" msg = " + msgArr[idx]);
		}

		if (paramSets.size() > 0) {
			// System.out.println("param sets: " + paramSets);
			DSParamSet curSet;
			Iterator iter = paramSets.iterator();
			while (iter.hasNext()) {
				curSet = (DSParamSet) iter.next();
				System.out.println("name = " + curSet.getName());
				System.out.println("params = " + curSet.getParams());
			}
		}

		System.exit(0);
	}

	public static void setServerData(JobGeneratorRequest jobGenReq, int serverId) {
		String info = "Setting DataStage credentials for domain: ";

		switch (serverId) {
		case SERVER_ID_DEBUG_TEST: // SERVER_ID_DEBUG_TEST:
			jobGenReq.setDomainServerName("myisdomainname");
			jobGenReq.setDomainServerPort(9080);
			jobGenReq.setDSProjectName("TestProject");
			jobGenReq.setDSHostName("MYISSERVERNAME");
			jobGenReq.setISUsername("isadmin");
			jobGenReq.setISPassword("pw4isadmin");
			jobGenReq.setDSTargetFolderName("\\\\Jobs\\\\CRMTests");
			jobGenReq.setDoContinueOnError(false);
			info = info + "IS Server (v9.1.2)";
			break;

		default:
			System.err.println("Invalid server id '" + serverId + "'.");
			System.exit(13);
		}
		// create the JobRequest ...
		System.out.println(info + "\n");
	}
}
