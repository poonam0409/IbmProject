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

import com.ibm.is.sappack.deltaextractstage.client.DsSapExtractorLogger;
import com.sap.conn.jco.JCoTable;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

public class DsExtractorCommonResource {

    private int idocDataStateCount;
    private int idocDataReceivedCount;
    private HashMap<Integer, Integer> idocReqState = new HashMap<Integer, Integer>();
    private DsSapExtractorParam openToolParameters;
    private int idocSequenceNumberCount;
    private boolean processComplete;
    private boolean processInterrupt;
    private boolean sapDataReceived;
    private DsSapExtractorLogger extractorLogger;
    private Calendar maxExtractionTimeout = Calendar.getInstance();
    private JCoTable dataTable;
    private LinkedList<JCoTable> dataList = new LinkedList<JCoTable>();
  //  private LinkedList queueA = new LinkedList();

    /**
     * @return the idocDataStateCount
     */
    public int getIdocDataStateCount() {
        return idocDataStateCount;
    }

    /**
     * @param idocDataStateCount the idocDataStateCount to set
     */
    public void setIdocDataStateCount(int idocDataStateCount) {
        this.idocDataStateCount = idocDataStateCount;
    }

    /**
     * increases the idocDataStateCount by (1)
     */
    public void increaseIdocDataStateCount() {
        this.setIdocDataStateCount(this.getIdocDataStateCount() + 1);
    }

    /**
     * @return the idocDataReceivedCount
     */
    public int getIdocDataReceivedCount() {
        return idocDataReceivedCount;
    }

    /**
     * @param idocDataReceivedCount the idocDataReceivedCount to set
     */
    public void setIdocDataReceivedCount(int idocDataReceivedCount) {
        this.idocDataReceivedCount = idocDataReceivedCount;
    }

    /**
     * increases the idocDataReceivedCount to set
     */
    public void increasesIdocDataReceivedCount() {
        this.setIdocDataReceivedCount(this.getIdocDataReceivedCount() + 1);
    }

    /**
     * @return the idocReqState
     */
    public HashMap<Integer, Integer> getIdocReqState() {
        return idocReqState;
    }

    /**
     * @param idocReqState the idocReqState to set
     */
    public void setIdocReqState(HashMap<Integer, Integer> idocReqState) {
        this.idocReqState = idocReqState;
    }

    /**
     * @return the processComplete
     */
    public boolean isProcessComplete() {
        return processComplete;
    }

    /**
     * @param processComplete the processComplete to set
     */
    public void setProcessComplete(boolean processComplete) {
        this.processComplete = processComplete;
    }

    /**
     * @return the idocSequenceNumberCount
     */
    public int getIdocSequenceNumberCount() {
        return idocSequenceNumberCount;
    }

    /**
     * @param idocSequenceNumberCount the idocSequenceNumberCount to set
     */
    public void increaseIdocSequenceNumberCount(int idocSequenceNumberCount) {
        this.idocSequenceNumberCount += idocSequenceNumberCount;
    }

    public DsSapExtractorParam getOpenToolParameters() {
        return openToolParameters;
    }

    public void setOpenToolParameters(DsSapExtractorParam openToolParameters) {
        this.openToolParameters = openToolParameters;
    }

    public DsSapExtractorLogger getExtractorLogger() {
        return extractorLogger;
    }

    public void setExtractorLogger(DsSapExtractorLogger extractorLogger) {
        this.extractorLogger = extractorLogger;
    }

    public boolean isProcessInterrupt() {
        return processInterrupt;
    }

    public void setProcessInterrupt(boolean processInterrupt) {
        this.processInterrupt = processInterrupt;
    }

    public Date getExtractionsTimeout() {
        return maxExtractionTimeout.getTime();
    }

    public void updateExtractionsTimeout() {
        this.maxExtractionTimeout.add(Calendar.MINUTE, openToolParameters.getExtractionTimeOut());
    }

    /**
     * @return the extractionTimeout
     */
    public boolean isExtractionTimeout() {
        Date currentTime = new Date();
        return currentTime.after(getExtractionsTimeout());
    }

    /**
     * @return the sapDataReceived
     */
    public synchronized boolean isSapDataReceived() {
        return sapDataReceived;
    }
    
    public synchronized void checkInSapDataReceived() {
        this.sapDataReceived = true;
    }
    public synchronized void checkOutSapDataReceived() {
        this.sapDataReceived = false;
    }

    /**
     * @return the dataTable
     */
    public JCoTable getDataTable() {
        return dataTable;
    }

    /**
     * @param dataTable the dataTable to set
     */
    public void setDataTable(JCoTable dataTable,int index) {
        this.dataTable = dataTable;
      //  dataTab.add(dataTable);
          dataList.add(dataTable);
      //  queueA.add(dataTable);
    }

	public synchronized LinkedList<JCoTable> getDataTab() {
		return dataList;
	}
}
