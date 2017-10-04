/*package com.ibm.is.sappack.deltaextractstage.client;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Random;
import java.util.logging.Level;

import org.apache.commons.io.FileUtils;

import com.ibm.is.sappack.deltaextractstage.commons.DsSapExtractorParam;
import com.ibm.is.sappack.deltaextractstage.load.DsSapExtractorAdapter;
import com.ibm.is.sappack.deltaextractstage.load.ReadFileImpl;
import com.ibm.is.sappack.deltaextractstage.load.RsinfoIdocMonitor;
import com.sap.conn.jco.JCoException;

public class InitializeExecutionParamsBC {
	
	private DsSapExtractorParam dsExtractorParam = null;
	private DsSapExtractorConnectionManager sapManager= null;
	private DsSapExtractorLogger extractorLogger = null;
	private ArrayList<String> filterConditionInput = null;
	private RsinfoIdocMonitor rsinfomonitor = null;
	private ReadFileImpl readfileimpl = null;
	private DsSapExtractorIdocRequest client = null;
	private PreRunConfiguration prerunconfig = null;
	private DsSapExtractorAdapter clientadapter = null;
	
	public InitializeExecutionParamsBC(Properties userStageProperties, DsSapExtractorParam dsExtractorParam)
	{
		this.dsExtractorParam = dsExtractorParam;
		filterConditionInput = new ArrayList<String>();
	}
	
	public void initilizeExecutionParameters(int x)
	{
		DsSapExtractorLogger.information("DeltaExtract_57",new Object[]{"DeltaExtract_57"});
		dsExtractorParam.setSapClient("800");
		dsExtractorParam.setSapUser("ibm_vaibhav1");
		dsExtractorParam.setSapPassword("vaibhavb");
		dsExtractorParam.setSapLanguage("EN");
		dsExtractorParam.setSapApplicationServerHostName("10.30.2.13");
		dsExtractorParam.setSapSystemNr("00");
		dsExtractorParam.setSapPoolId("SAPPOOL");
		dsExtractorParam.setSenderPort("LS_DEMO2");
		dsExtractorParam.setSenderPortNumber("LS_DEMO2");
		dsExtractorParam.setReceiverPortNumber("IB1CLNT800");
		dsExtractorParam.setSegmentStructure("2LIS_02_HDR");
		dsExtractorParam.setDataFetchMode("F");
		dsExtractorParam.setDataSrcType("TRAN");
		dsExtractorParam.setTidFilePath("C:/Temp/");
		dsExtractorParam.setExtractionTimeOut();
		dsExtractorParam.setLogTableData("0");
		dsExtractorParam.setHierDetails("");
		dsExtractorParam.setHierName("");
		dsExtractorParam.setIdocFilterClause(filterConditionInput);
		DsSapExtractorResourceBundle extractorOTResourceBundle = new DsSapExtractorResourceBundle();
		extractorOTResourceBundle.initializeResource("_DsSapDeltaExtractor");
		dsExtractorParam.setOtLogFilePath("c:/temp/ot1.log");
		extractorLogger = new DsSapExtractorLogger();
		dsExtractorParam.setExtractorLogger(extractorLogger);
		DsSapExtractorLogger.initLogger();
		
	}
	
	public void initializeSAPConnection(boolean isServerInit)
	{
			sapManager = new DsSapExtractorConnectionManager(extractorLogger, dsExtractorParam);
			sapManager.setSapApplicationServerHostName(dsExtractorParam.getSapApplicationServerHostName());
			sapManager.setSapClient(dsExtractorParam.getSapClient());
			sapManager.setSapLanguage(dsExtractorParam.getSapLanguage());//
			sapManager.setSapPassword(dsExtractorParam.getSapPassword());
			sapManager.setSapPoolId(dsExtractorParam.getSapPoolId());
			sapManager.setSapSystemNr(dsExtractorParam.getSapSystemNr());
			sapManager.setSapUser(dsExtractorParam.getSapUser());
			sapManager.initializeClientConnection();
		
	}
	
	
	
	public void prepareFilterCondition()
	{
		boolean filterConditionExist = false;
		while(filterConditionExist) 
		{
		}
	}
	
	public void idocClientInitialization()
			throws JCoException
			{
		client = new DsSapExtractorIdocRequest(extractorLogger, dsExtractorParam);
		client.initializationSAPConnection(sapManager.getExtractorDestination());
		if(dsExtractorParam.getDataStageNodeId()==0)
		{
		performPrerunCleanup();
		}
		prerunconfig = new PreRunConfiguration(sapManager.getExtractorDestination(), dsExtractorParam);
		prerunconfig.updatetables();
		clientadapter = new DsSapExtractorAdapter(extractorLogger, client.getExtractorDestination(), dsExtractorParam);
		readfileimpl = new ReadFileImpl(clientadapter,dsExtractorParam,extractorLogger);
		rsinfomonitor = new RsinfoIdocMonitor(dsExtractorParam,extractorLogger);
		dsExtractorParam.setReadfileimpl(readfileimpl);
		dsExtractorParam.setRsinfomonitor(rsinfomonitor);
		client.initializeInternalIDocParameters();
		client.sendIDocXMLRequest();
		DsSapExtractorLogger.information("DeltaExtract_53",new Object[]{"DeltaExtract_53"});
			}

	public int randomNumberGenrator()
	{
		Random number = new Random();
		return number.nextInt();
	}
	
	public void performPrerunCleanup()
	{
		File file = new File(dsExtractorParam.getTidFilePath()+"/"+dsExtractorParam.getJobName());
		if(file.isDirectory()){
			try {
				DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"++++++ Deleting any previous run left Directory +++++++ for Node:"+dsExtractorParam.getDataStageNodeId()});
				FileUtils.deleteDirectory(file);
				DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"++++++ Creating Directory +++++++ for Node:"+dsExtractorParam.getDataStageNodeId()});
				file.mkdir();
				DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"++++++ Directory Created+++++++ for Node:"+dsExtractorParam.getDataStageNodeId()});
			} catch (IOException e) {
				DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"++++++ Exception Occured+++++++ for Node:"+e.getMessage()});
				e.printStackTrace();
			}
		}
		else
		{
			file.mkdir();
		}
	}
}*/
