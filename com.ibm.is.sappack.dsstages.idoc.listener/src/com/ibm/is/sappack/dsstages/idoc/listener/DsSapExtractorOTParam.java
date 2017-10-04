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
package com.ibm.is.sappack.dsstages.idoc.listener;

import java.io.File;
import java.util.logging.Level;

import com.ibm.is.sappack.dsstages.common.StageLogger;

public class DsSapExtractorOTParam {

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
    private String SapServiceName = "";
    private String SapProgID = "";
    private int SapMaxConnections;

    private boolean is10gOr11g = true;
    /**
     * SAP Idoc parameters
     */
//    private String sapIdocRelVersion = "";
    private String senderPort = "";
    private String senderPortNumber = "";
    private String receiverPortNumber = "";
    private String requestUser = "";
    private String segmentStructure = "";
    private String trActDate = "";
    private String dataFetchMode = "D";
    private String dataSrcType;
    private String idocRequestNumber;
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
    private String stageTabName = "";
    private String stageMapColName = "";

    private String logTableData;

    private int extractionTimeOut = 10;
    
    private File dataPacketFolder = null;
    
    private File idocReqStateFolder = null;

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
     * @return the SapServiceName
     */
    public String getSapServiceName() {
        return SapServiceName;
    }

    /**
     * @param SapServiceName the SapServiceName to set
     */
    public void setSapServiceName(String SapServiceName) {
        this.SapServiceName = SapServiceName;
    }

    /**
     * @return the SapProgID
     */
    public String getSapProgID() {
        return SapProgID;
    }

    /**
     * @param SapProgID the SapProgID to set
     */
    public void setSapProgID(String SapProgID) {
        this.SapProgID = SapProgID;
    }

    /**
     * @return the SapMaxConnections
     */
    public int getSapMaxConnections() {
        return SapMaxConnections;
    }

    /**
     * @param SapMaxConnections the SapMaxConnections to set
     */
    public void setSapMaxConnections(int SapMaxConnections) {
        this.SapMaxConnections = SapMaxConnections;
    }

    /**
     * @return the is10gOr11g
     */
    public boolean isIs10gOr11g() {
        return is10gOr11g;
    }

    /**
     * @param is10gOr11g the is10gOr11g to set
     */
    public void setIs10gOr11g(boolean is10gOr11g) {
        this.is10gOr11g = is10gOr11g;
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

    /**
     * @return the requestUser
     */
    public String getRequestUser() {
        return requestUser;
    }

    /**
     * @param requestUser the requestUser to set
     */
    public void setRequestUser(String requestUser) {
        this.requestUser = requestUser;
    }

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

    public File getDataPacketFolder() {
		return dataPacketFolder;
	}

	public void setReceivedPacketFolders(File dataPacketFolder, File idocReqStateFolder) {
		this.dataPacketFolder = dataPacketFolder;
		this.idocReqStateFolder = idocReqStateFolder;
	}

	public File getIdocReqState() {
		return idocReqStateFolder;
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
            StageLogger.getLogger().log(Level.INFO,"Debug: {0}",msg);
        }
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
    public void setExtractionTimeOut(int extractionTimeOut) {
        this.extractionTimeOut = extractionTimeOut;
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
        this.hierName = hierName.split("AS")[0].trim();
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

}
