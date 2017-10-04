//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2015                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.is.sappack.dsstages.common
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.deltaextractstage.commons;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;

import com.ibm.is.sappack.deltaextractstage.client.DsSapExtractorLogger;
import com.ibm.is.sappack.deltaextractstage.load.ReadFileImpl;
import com.ibm.is.sappack.deltaextractstage.load.RsinfoIdocMonitor;
import com.sap.conn.jco.JCoTable;

public class DsSapExtractorParam {

    /**
     * SAP Connection parameters
     *
     */
    private String SapClient = "";
    private String SapUser = "";
    private String SapPassword = "";
    private String SapLanguage = "";
    private String SapApplicationServerHostName = "";
    private String SapSystemNr = "";
    private String SapPoolId = "";
    private String msgServer = null; //HOSTNAME   3601/TCP   /H/200.201.202.203/S/3601
    private String groupName = null;//PUBLIC/SPACE
    private String routerString = null; //
    private String sapSystemId = null;
    private boolean isMessageServerSystem = false;  
    private ArrayList<?> idocFilterClause= null;
    private String dsinputfilters = null;
    public static int dataStageNodeId = 999999;
    private int totalNumberofNodes = -1;
    private String jobName = "";
    /**
     * SAP Idoc parameters
     */
//    private String sapIdocRelVersion = "";
    private String senderPort = "";
    private String senderPortNumber = "";
    private String receiverPortNumber = "";
    private String segmentStructure = "";
    private String trActDate = "";
    private String dataFetchMode = "D";
    private String dataSrcType;
    private String idocRequestNumber;
    private String sapTransferStructureName = "ZDS_CDC_HIERSTRUCT_EXT";
    public static final String DS_DELTA_EXTRACT_DATA_FETCHMODE = "DS_DELTA_EXTRACT_DATA_FETCHMODE";
    public static final String DS_DELTA_EXTRACT_TIME_OUT = "DS_DELTA_EXTRACT_TIME_OUT";
    
    private String filePath = "";
    private JCoTable tab;
    /**
     * SAP Idoc Full Load Parameter
     */
    private String dateFrom;
    private String dateTo;

    /**
     * SAP Hier Object
     */
    private String hierName;
    private String hierClass;
    private String hierLang;
    private String hierDetails;

    /**
     * SAP Idoc Listener parameter
     */
    private String tidFilePath = "";
    /**
     * Open Tool Log file entry
     */
    private String otLogFilePath = "";
    /**
     * Staging System Credential
     */
    private String sessionId = "";
//    String stageDriver;
//    String stageUsrName;
//    String stagePasswd;
//    String stageUrl;
    
    /**
     * Files Folder path
     */
    private File dataPacketFolderPath = null;
    private File infoIdocFolderPath = null;
    private File initFile = null;
    
    private String stageTabName = "";
    private String stageMapColName = "";

    private String logTableData;

    private DsSapExtractorLogger extractorLogger = null;
    public RsinfoIdocMonitor rsinfomonitor = null;
	public ReadFileImpl readfileimpl = null;

    private int extractionTimeOut = 10*60*1000;
    
    public DsSapExtractorParam() {
    	idocFilterClause = new ArrayList<String>();
	}

    public String getMsgServer() {
		return msgServer;
	}

	public void setMsgServer(String msgServer) {
		this.msgServer = msgServer;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getRouterString() {
		return routerString;
	}

	public void setRouterString(String routerString) {
		this.routerString = routerString;
	}

	public String getSapSystemId() {
		return sapSystemId;
	}

	public void setSapSystemId(String sapSystemId) {
		this.sapSystemId = sapSystemId;
	}

	public boolean isMessageServerSystem() {
		return isMessageServerSystem;
	}

	public void setMessageServerSystem(boolean isMessageServerSystem) {
		this.isMessageServerSystem = isMessageServerSystem;
	}

	/**
     * @return the SapClient
     */
    public String getSapClient() {
        return SapClient;
    }

    /**
     * @param SapClient the SapClient to set
     */
    public void setSapClient(String SapClient) {
        this.SapClient = SapClient;
    }

    /**
     * @return the SapUser
     */
    public String getSapUser() {
        return SapUser;
    }

    /**
     * @param SapUser the SapUser to set
     */
    public void setSapUser(String SapUser) {
        this.SapUser = SapUser;
    }

    /**
     * @return the SapPassword
     */
    public String getSapPassword() {
        return SapPassword;
    }

    /**
     * @param SapPassword the SapPassword to set
     */
    public void setSapPassword(String SapPassword) {
        this.SapPassword = SapPassword;
    }

    /**
     * @return the SapLanguage
     */
    public String getSapLanguage() {
        return SapLanguage;
    }

    /**
     * @param SapLanguage the SapLanguage to set
     */
    public void setSapLanguage(String SapLanguage) {
        this.SapLanguage = SapLanguage;
    }

    /**
     * @return the SapApplicationServerHostName
     */
    public String getSapApplicationServerHostName() {
        return SapApplicationServerHostName;
    }

    /**
     * @param SapApplicationServerHostName the SapApplicationServerHostName to
     * set
     */
    public void setSapApplicationServerHostName(String SapApplicationServerHostName) {
        this.SapApplicationServerHostName = SapApplicationServerHostName;
    }

    /**
     * @return the SapSystemNr
     */
    public String getSapSystemNr() {
        return SapSystemNr;
    }

    /**
     * @param SapSystemNr the SapSystemNr to set
     */
    public void setSapSystemNr(String SapSystemNr) {
        this.SapSystemNr = SapSystemNr;
    }

    /**
     * @return the SapPoolId
     */
    public String getSapPoolId() {
        return SapPoolId;
    }

    /**
     * @param SapPoolId the SapPoolId to set
     */
    public void setSapPoolId(String SapPoolId) {
        this.SapPoolId = SapPoolId;
    }
   
    /**
     * @return the sapIdocRelVersion
     */
//    public String getSapIdocRelVersion() {
//        return sapIdocRelVersion;
//    }
    /**
     * @param sapIdocRelVersion the sapIdocRelVersion to set
     */
//    public void setSapIdocRelVersion(String sapIdocRelVersion) {
//        this.sapIdocRelVersion = sapIdocRelVersion;
//    }
    /**
     * @return the senderPort
     */
    public String getSenderPort() {
        return senderPort;
    }

    /**
     * @param senderPort the senderPort to set
     */
    public void setSenderPort(String senderPort) {
        this.senderPort = senderPort;
    }

    /**
     * @return the senderPortNumber
     */
    public String getSenderPortNumber() {
        return senderPortNumber;
    }

    /**
     * @param senderPortNumber the senderPortNumber to set
     */
    public void setSenderPortNumber(String senderPortNumber) {
        this.senderPortNumber = senderPortNumber;
    }

    /**
     * @return the receiverPortNumber
     */
    public String getReceiverPortNumber() {
        return receiverPortNumber;
    }

    /**
     * @param receiverPortNumber the receiverPortNumber to set
     */
    public void setReceiverPortNumber(String receiverPortNumber) {
        this.receiverPortNumber = receiverPortNumber;
    }

 /*   *//**
     * @return the requestUser
     *//*
    public String getRequestUser() {
        return requestUser;
    }

    *//**
     * @param requestUser the requestUser to set
     *//*
    public void setRequestUser(String requestUser) {
        this.requestUser = requestUser;
    }*/

    /**
     * @return the segmentStructure
     */
    public String getSegmentStructure() {
        return segmentStructure;
    }

    /**
     * @param segmentStructure the segmentStructure to set
     */
    public void setSegmentStructure(String segmentStructure) {
        this.segmentStructure = segmentStructure;
    }

    public String getSapTransferStructureName() {
		return sapTransferStructureName;
	}

	public void setSapTransferStructureName(String sapTransferStructureName) {
		this.sapTransferStructureName = sapTransferStructureName;
	}

	/**
     * @return the trActDate
     */
    public String getTrActDate() {
        return trActDate;
    }

    /**
     * @param trActDate the trActDate to set
     */
    public void setTrActDate(String trActDate) {
        this.trActDate = trActDate;
    }

    /**
     * @return the dataFetchMode
     */
    public String getDataFetchMode() {
        return dataFetchMode;
    }

    /**
     * @param dataFetchMode the dataFetchMode to set
     */
    public void setDataFetchMode(String dataFetchMode) {
    	String datafetchmode = System.getenv(DS_DELTA_EXTRACT_DATA_FETCHMODE);
    	if("R".equals(datafetchmode))
    		this.dataFetchMode = "R";
    	else
    		this.dataFetchMode = dataFetchMode;
    }

    /**
     * @return the tidFilePath
     */
    public String getTidFilePath() {
        return tidFilePath;
    }

    /**
     * @param tidFilePath the tidFilePath to set
     */
    public void setTidFilePath(String tidFilePath) {
        this.tidFilePath = tidFilePath;
    }

    /**
     * @return the otLogFilePath
     */
    public String getOtLogFilePath() {
        return otLogFilePath;
    }

    /**
     * @param otLogFilePath the otLogFilePath to set
     */
    public void setOtLogFilePath(String otLogFilePath) {
        this.otLogFilePath = otLogFilePath;
    }

    public File getdataPacketFolderPath() {
		return dataPacketFolderPath;
	}

	public void setReceivedPacketFolders(File dataPacketFolder, File infoIdocFolderPath) {
		this.dataPacketFolderPath = dataPacketFolder;
		this.infoIdocFolderPath = infoIdocFolderPath;
	}

	public File getinfoIdocFolderPath() {
		return infoIdocFolderPath;
	}

	/**
     * @return the sessionId
     */
    public String getSessionId() {
        return sessionId;
    }

    /**
     * @param sessionId the sessionId to set
     */
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    /**
     * @return the stageTabName
     */
    public String getStageTabName() {
        return stageTabName;
    }

    /**
     * @param stageTabName the stageTabName to set
     */
    public void setStageTabName(String stageTabName) {
        this.stageTabName = stageTabName;
    }

    /**
     * @return the stageMapColName
     */
    public String getStageMapColName() {
        return stageMapColName;
    }

    /**
     * @param stageMapColName the stageMapColName to set
     */
    public void setStageMapColName(String stageMapColName) {
        this.stageMapColName = stageMapColName;
    }

    /**
     * @return the logTableData
     */
    public String getLogTableData() {
        return logTableData;
    }

    /**
     * @param logTableData the logTableData to set
     */
    public void setLogTableData(String logTableData) {
        this.logTableData = logTableData;
    }

    public void logDebugLevelLog(String msg) {
        if (!logTableData.equals("0")) {
            DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{msg});
        }
    }

    public void setExtractorLogger(DsSapExtractorLogger extractorLogger) {
        this.extractorLogger = extractorLogger;
    }

    public String getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(String dateFrom) {
        this.dateFrom = dateFrom;
    }

    public String getDateTo() {
        return dateTo;
    }

    public void setDateTo(String dateTo) {
        this.dateTo = dateTo;
    }

    /**
     * @return the idocRequestNumber
     */
    public String getIdocRequestNumber() {
        return idocRequestNumber;
    }

    /**
     * @param idocRequestNumber the idocRequestNumber to set
     */
    public void setIdocRequestNumber(String idocRequestNumber) {
        this.idocRequestNumber = idocRequestNumber;
    }

    /**
     * @return the extractorLogger
     */
    public DsSapExtractorLogger getExtractorLogger() {
        return extractorLogger;
    }

    public RsinfoIdocMonitor getRsinfomonitor() {
		return rsinfomonitor;
	}

	public void setRsinfomonitor(RsinfoIdocMonitor rsinfomonitor) {
		this.rsinfomonitor = rsinfomonitor;
	}

	public ReadFileImpl getReadfileimpl() {
		return readfileimpl;
	}

	public void setReadfileimpl(ReadFileImpl readfileimpl) {
		this.readfileimpl = readfileimpl;
	}

	/**
     * @return the dataSrcType
     */
    public String getDataSrcType() {
        return dataSrcType;
    }

    /**
     * @param dataSrcType the dataSrcType to set
     */
    public void setDataSrcType(String dataSrcType) {
        this.dataSrcType = dataSrcType;
    }

    /**
     * @return the extractionTimeOut
     */
    public int getExtractionTimeOut() {
        return extractionTimeOut;
    }

    /**
     * @param extractionTimeOut the extractionTimeOut to set
     */
    public void setExtractionTimeOut() {
    	String envTimeoutVar = System.getenv(DS_DELTA_EXTRACT_TIME_OUT);
    	if(!(envTimeoutVar == null || envTimeoutVar == ""))
    		try{
        this.extractionTimeOut = Integer.parseInt(envTimeoutVar)*1000;
    		}
    	catch (NumberFormatException e)
    	{
    		 DsSapExtractorLogger.fatal("DeltaExtract_72",new Object[] {"INVALIDVALUEFORTIMEOUT", e.getMessage()});
    	}
    }

    /**
     * @return the hierName
     */
    public String getHierName() {
        return hierName;
    }

    /**
     * @param hierName the hierName to set
     */
    public void setHierName(String hierName) {
        this.hierName = hierName;
    }

    /**
     * @return the hierClass
     */
    public String getHierClass() {
        return hierClass;
    }

    /**
     * @param hierClass the hierClass to set
     */
    public void setHierClass(String hierClass) {
        this.hierClass = hierClass;
    }

    /**
     * @return the hierLang
     */
    public String getHierLang() {
        return hierLang;
    }

    /**
     * @param hierLang the hierLang to set
     */
    public void setHierLang(String hierLang) {
        this.hierLang = hierLang;
    }

    /**
     * @return the hierDetails
     */
    public String getHierDetails() {
        return hierDetails;
    }

    /**
     * @param hierDetails the hierDetails to set
     */
    public void setHierDetails(String hierDetails) {
        this.hierDetails = hierDetails;
    }

	public JCoTable getTab() {
		return tab;
	}

	public void setTab(JCoTable tab) {
		this.tab = tab;
	}

	public String getDsinputfilters() {
		return dsinputfilters;
	}

	public void setDsinputfilters(String dsinputfilters) {	
		this.dsinputfilters = dsinputfilters;
	}

	public ArrayList<?> getIdocFilterClause() {
		return idocFilterClause;
	}

	public void setIdocFilterClause(ArrayList<String> idocFilterClause) {
		this.idocFilterClause = idocFilterClause;
		DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"+++++++ idocFilterClause+++++++:"+idocFilterClause});
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public File getInitFile() {
		return initFile;
	}

	public void setInitFile(File initFile) {
		this.initFile = initFile;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public int getDataStageNodeId() {
		return dataStageNodeId;
	}

	public void setDataStageNodeId(int dataStageNodeId) {
		DsSapExtractorParam.dataStageNodeId = dataStageNodeId;
	}

	public int getTotalNumberofNodes() {
		return totalNumberofNodes;
	}

	public void setTotalNumberofNodes(int totalNumberofNodes) {
		this.totalNumberofNodes = totalNumberofNodes;
	}

}
