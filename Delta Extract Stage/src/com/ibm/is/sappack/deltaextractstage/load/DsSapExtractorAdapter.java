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
package com.ibm.is.sappack.deltaextractstage.load;

import com.ibm.is.sappack.deltaextractstage.client.DsSapExtractorLogger;
import com.ibm.is.sappack.deltaextractstage.commons.DsSapExtractorParam;
import com.ibm.is.sappack.deltaextractstage.load.hier.ParseXML;
import com.ibm.is.sappack.deltaextractstage.utils.DsSapExtractorUtility;
import com.sap.conn.jco.JCo;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoRecordMetaData;
import com.sap.conn.jco.JCoRepository;
import com.sap.conn.jco.JCoTable;

import java.io.InputStream;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;


public class DsSapExtractorAdapter {

    private JCoDestination extractorDestination;//
    private JCoRepository extractorIdocRepository;
    private DsSapExtractorLogger extractorLogger;
    private final static String ZDS_DE_IDOC_STATUS_GET_V8_0 = "ZDS_DE_IDOC_STATUS_GET_V8_0";
    private final String RFC_READ_TABLE = "RFC_READ_TABLE";
    private final String ZDS_DE_HIER_UPDATE_V8_0 = "ZDS_DE_HIER_UPDATE_V8_0";
    ParseXML parsexml=null;
    DsSapExtractorParam param = null;
    public DsSapExtractorAdapter(DsSapExtractorLogger extractorLogger, JCoDestination destination, DsSapExtractorParam param) {
        try {
            this.extractorLogger = extractorLogger;
            extractorDestination = destination;
            this.param = param;
            extractorIdocRepository = extractorDestination.getRepository();
            parsexml = new ParseXML(param);
        } catch (JCoException ex) {
            DsSapExtractorUtility.throwExtractorException(ex, extractorLogger);
        }
    }

    public DsSapExtractorAdapter(DsSapExtractorLogger extractorLogger, JCoRepository extractorIdocRepository, DsSapExtractorParam param) {
        try {
        	this.param = param;
            this.extractorLogger = extractorLogger;
            this.extractorIdocRepository = extractorIdocRepository;
        } catch (Exception ex) {
            DsSapExtractorUtility.throwExtractorException(ex, extractorLogger);
        }
    }

    /**
     * Breaks the data received by the IDoc into its respective data length.
     *
     * @param pStructureName
     * @param dataBuffer
     * @return
     */
    public JCoTable getStructureFieldLengthList(String pStructureName, List<String> dataBuffer) {
        JCoTable requiredStructure = null;
        String data = "";
        String columnData = "";
        try {
            JCoRecordMetaData structureMetaData = extractorIdocRepository.getStructureDefinition(pStructureName);
            requiredStructure = JCo.createTable(structureMetaData);
            JCoRecordMetaData metadata = requiredStructure.getRecordMetaData();
            int fieldCount = metadata.getFieldCount();
            Iterator<String> dataItr = dataBuffer.iterator();
            int dataLenght = 0;
            int begin, end;
            while (dataItr.hasNext()) {
                begin = end = 0;
                int index = 0;
                requiredStructure.appendRow();
                data = dataItr.next();
                dataLenght = data.length();
                for (int i = 0; i < fieldCount; i++) {
                    index = metadata.getLength(i);
                    end += index;
                    end = (end < (dataLenght) ? end : dataLenght);
                    columnData = data.substring(begin, end);
                    requiredStructure.setValue(metadata.getName(i), columnData);
                    begin = end;
                    if (end == dataLenght) {
                        break;
                    }
                }
            }
            return requiredStructure;
        } catch (JCoException e) {
            DsSapExtractorUtility.throwExtractorException(e, extractorLogger);
        }

        return requiredStructure;

    }

  /*  public void decrementExtractorNumber(String trnsferStruc, String targetSystem) {
        try {
            JCoFunction func = extractorIdocRepository.getFunction("ZDS_CDC_TRFC_QUEUE_DELETE");
            func.getImportParameterList().setValue("I_TFSTRUC", trnsferStruc);
            func.getImportParameterList().setValue("I_RLOGSYS", targetSystem);
            func.execute(extractorDestination);

            JCoTable stateTable = func.getTableParameterList().getTable("ET_FILE_RETURN");
            int numRows = stateTable.getNumRows();
            if (numRows > 0) {
                String msgType = stateTable.getString("TYPE");
                String uploadInfo = "";
                for (int r = 0; r < numRows; r++) {
                    stateTable.setRow(r);
                    uploadInfo = stateTable.getString("MESSAGE");
                    DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"Message Type : " + msgType});
                    DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"Message : " + uploadInfo});

                }
            }
        } catch (JCoException ex) {
            DsSapExtractorUtility.throwExtractorException(ex, extractorLogger);
        }
    }*/

    /*public String fetchSegmentName(String oltpName, String receiverPrtNr, String senderPrtNr) {
        try {
            DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"Fetching Transfer Structure for OLTP : " + oltpName});
            JCoFunction func = extractorDestination.getRepository().getFunction(RFC_READ_TABLE);
            func.getImportParameterList().setValue("QUERY_TABLE", "ROOSGEN");
            JCoTable optionTab = func.getTableParameterList().getTable("OPTIONS");
            optionTab.appendRow();
            optionTab.setValue("TEXT", new StringBuilder().append("OLTPSOURCE = '").append(oltpName).append("'").toString());
            optionTab.appendRow();
            optionTab.setValue("TEXT", new StringBuilder().append("AND").toString());
            optionTab.appendRow();
            optionTab.setValue("TEXT", new StringBuilder().append("SLOGSYS = '").append(receiverPrtNr).append("'").toString());
            DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"SLOGSYS =  " + receiverPrtNr});
            optionTab.appendRow();
            optionTab.setValue("TEXT", new StringBuilder().append("AND").toString());
            optionTab.appendRow();
            optionTab.setValue("TEXT", new StringBuilder().append("RLOGSYS = '").append(senderPrtNr).append("'").toString());
            DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"RLOGSYS =  " + senderPrtNr});

            JCoTable fieldTab = func.getTableParameterList().getTable("FIELDS");
            fieldTab.appendRow();
            fieldTab.setValue("FIELDNAME", "TFSTRUC");

            func.execute(extractorDestination);

            JCoTable dataTab = func.getTableParameterList().getTable("DATA");
            if (dataTab.isEmpty()) {
                DsSapExtractorLogger.writeToLogFile(Level.SEVERE,"DeltaExtract", new Object[]{"No Transfer Structure found ..."});
                DsSapExtractorUtility.throwExtractorException("No Transfer Structure found ...", extractorLogger);
            }
            return dataTab.getValue("WA").toString();

        } catch (JCoException e) {
            DsSapExtractorLogger.writeToLogFile(Level.SEVERE,"DeltaExtract", new Object[]{"Error occured while fetching Transfer Structure ..."});
            DsSapExtractorLogger.writeToLogFile(Level.SEVERE,"DeltaExtract", new Object[]{e.toString()});
            DsSapExtractorUtility.throwExtractorException("Error occured while fetching Transfer Structure " + e.toString(), extractorLogger);
        }
        return null;
    }
*/
    /**
     * fetch Posted Idoc status
     *
     * @param requestNumber
     * @param extractorLogger
     * @param extractorDestination
     */
    public static void fetchOutboundIdocStatus(String requestNumber, DsSapExtractorLogger extractorLogger, JCoDestination extractorDestination) {
        try {
            DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"Fetching Status of " + (requestNumber)});
            JCoFunction function = extractorDestination.getRepository().getFunction(ZDS_DE_IDOC_STATUS_GET_V8_0);
            function.getImportParameterList().setValue("IV_VAL", requestNumber);
            DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"Request Number : " + requestNumber});
            function.getImportParameterList().setValue("I_DATE_FROM", new java.sql.Date(new Date().getTime()));
            DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"Idoc Creation Date : " + new java.sql.Date(new Date().getTime())});
            function.getImportParameterList().setValue("IV_DIRECTION", "1");
            DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"Idoc Direction : Outbound"});

            function.execute(extractorDestination);
            readETReturn(function, extractorLogger, "Idoc Posted Document");
        } catch (JCoException e) {
            DsSapExtractorLogger.writeToLogFile(Level.SEVERE,"DeltaExtract", new Object[]{"Error Details(fetchOutboundIdocStatus) : " + e.toString()});
            DsSapExtractorUtility.throwExtractorException(e, extractorLogger);
        }
    }

    /**
     * fetch Posted Idoc status
     *
     * @param tid
     */
  /*  public void fetchTIDStatus(String tid) {
        try {
            DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"Fetching Status of TID: " + (tid)});
            JCoFunction function = extractorDestination.getRepository().getFunction(ZDS_CDC_ARFC_STATE_CHECK);
            function.getImportParameterList().setValue("IV_TID", tid);
            DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"TID Number : " + tid});
            function.getImportParameterList().setValue("IV_RLOGSYS", otPara.getSenderPortNumber());
            DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"Sender Port: " + otPara.getSenderPortNumber()});

            function.execute(extractorDestination);
            readETReturn(function, extractorLogger, "TID Status");
        } catch (JCoException e) {
            DsSapExtractorLogger.writeToLogFile(Level.SEVERE,"DeltaExtract", new Object[]{"Error Details(fetchTIDStatus) : " + e.toString()});
            DsSapExtractorUtility.throwExtractorException(e, extractorLogger);
        }
    }*/

    /**
     * reads and log the ET_RETURN table for the the executed RFCs
     *
     * @param function
     */
    private static void readETReturn(JCoFunction function, DsSapExtractorLogger extractorLogger, String processname) {
        JCoTable etReturn = function.getTableParameterList().getTable("ET_RETURN");
        etReturn.firstRow();
        String msgType = null;
        StringBuilder errorMsg = new StringBuilder();
        while (etReturn.nextRow()) {
            msgType = etReturn.getValue("TYPE").toString();
            if (msgType.equalsIgnoreCase("E")) {
                errorMsg.append("Error Details(").append(processname).append(")");
                errorMsg.append(("" + (char) 10));
                errorMsg.append("-----------------");
                errorMsg.append(("" + (char) 10));
                errorMsg.append("   Type: E ");
                errorMsg.append(("" + (char) 10));
                errorMsg.append("   ID: ").append(etReturn.getValue("ID"));
                errorMsg.append(("" + (char) 10));
                errorMsg.append("   Message: ").append(etReturn.getValue("MESSAGE"));
            } else {
                DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{new StringBuilder().append("SAP Return Details(" + processname + ")")});
                DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{new StringBuilder().append("   Type: ").append(msgType)});
                DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{new StringBuilder().append("   ID: ").append(etReturn.getValue("ID"))});
                DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{new StringBuilder().append("   Message: ").append(etReturn.getValue("MESSAGE"))});
            }
        }
        if (errorMsg.length() > 0) {
            DsSapExtractorLogger.writeToLogFile(Level.SEVERE,"DeltaExtract", new Object[]{errorMsg.toString()});
            DsSapExtractorLogger.fatal("DeltaExtract_FatalError_01",new Object[]{errorMsg.toString()});
        }
    }

    /**
     *
     * @param jtab
     * @return
     */
    public JCoTable setHierStructureDetails(InputStream is) {
        JCoTable hierTab = null;
        try {
            DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"Parsing the Hierarchy data into common table ..."});
            JCoFunction function = extractorDestination.getRepository().getFunction(ZDS_DE_HIER_UPDATE_V8_0);
            JCoTable jtab = function.getTableParameterList().getTable("I_T_DATA");
            if(parsexml.getdatafromXML(is,jtab)){
            	jtab = param.getTab();
            }
            DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"setHierStructureDetails ..."});
            function.getTableParameterList().setValue("I_T_DATA", jtab);
            function.execute(extractorDestination);

            hierTab = function.getTableParameterList().getTable("I_T_FINAL");
        } catch (JCoException e) {
            DsSapExtractorLogger.writeToLogFile(Level.SEVERE,"DeltaExtract", new Object[]{"Error Details(fetchTIDStatus) : " + e.toString()});
            DsSapExtractorUtility.throwExtractorException(e, extractorLogger);
        }
        return hierTab;
    }
}
