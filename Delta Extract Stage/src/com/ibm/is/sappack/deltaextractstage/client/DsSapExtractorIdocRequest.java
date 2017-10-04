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
package com.ibm.is.sappack.deltaextractstage.client;

import com.ibm.is.sappack.deltaextractstage.commons.DsExtractorCommonResource;
import com.ibm.is.sappack.deltaextractstage.commons.DsExtractorProcessState;
import com.ibm.is.sappack.deltaextractstage.commons.DsSapExtractorParam;
import com.ibm.is.sappack.deltaextractstage.utils.DsSapExtractorUtility;
import com.sap.conn.idoc.IDocDocument;
import com.sap.conn.idoc.IDocDocumentList;
import com.sap.conn.idoc.IDocFactory;
import com.sap.conn.idoc.IDocMetaDataUnavailableException;
import com.sap.conn.idoc.IDocParseException;
import com.sap.conn.idoc.IDocRepository;
import com.sap.conn.idoc.IDocSyntaxException;
import com.sap.conn.idoc.IDocXMLProcessor;
import com.sap.conn.idoc.jco.JCoIDoc;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoTable;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;

public class DsSapExtractorIdocRequest {
	private final String BS = ""+(char)47;
    /**
     * SAP Idoc parameters
     */
    private String requestNumber;
    private String requestDate;
    private String requestTime;
    private String segmentTimeStamp; 
    private final String IDocXMLFilePath = "/com/ibm/is/sappack/deltaextractstage/resources/IdocTemplate_generic.xml";
    private JCoDestination extractorDestination;
    private IDocRepository extractorIdocRepository;
    private SimpleDateFormat sdfDate = new SimpleDateFormat("yyyyMMdd");
    private SimpleDateFormat sdfTime = new SimpleDateFormat("hhmmss");
    private String idocXmldata;
    private String requestTID;
    private final DsSapExtractorParam dssapextractorparam;
    private DsSapExtractorLogger extractorLogger;
    private HashMap<String,String> initfileInfo = null;
    private final String AMPERSAND = ""+(char)38;
	private final String PERCENT = ""+(char)37;
	private final String JOBNAMETAG = AMPERSAND+"J"+AMPERSAND;
	private final String NOOFNODESTAG = AMPERSAND+"N"+AMPERSAND;
    

    /**
     * SAP IDOC Request rfc's
     */
    private final String ZDS_DE_IDOC_STATUS_GET_V8_0 = "ZDS_DE_IDOC_STATUS_GET_V8_0";
  //  private final String ZDS_CDC_TOTAL_RECORDS = "ZDS_CDC_TOTAL_RECORD";
    private final String ZDS_DE_VALIDATE_TRANS_STR_V8_0 = "ZDS_DE_VALIDATE_TRANS_STR_V8_0";
    private final String RFC_READ_TABLE = "RFC_READ_TABLE";
    private final String ZDS_DE_HIER_EXTRACT_V8_0 = "ZDS_DE_HIER_EXTRACT_V8_0";
    
   // private final String ZDS_CDC_UPDMODE_CHECK = "ZDS_CDC_UPDMODE_CHECK";

    /**
     * Idoc segments constants
     */
    private final String TRANS_SEGMENT = ""
            + "<E1RSRIS SEGMENT=\"1\">"
            + " <ISOURCE><![CDATA[%s]]>"
            + " </ISOURCE>"
            + " <TSTAMPOLTP>%s</TSTAMPOLTP>"
            + " <UPDMODE>%s</UPDMODE>"
            + " %s"
            + "</E1RSRIS>";
    private final String ATTR$TEXT_SEGMENT = ""
            + "<E1RSRMD SEGMENT=\"1\">"
            + " <INFOOBJECT><![CDATA[%s]]></INFOOBJECT>"
            + " <TSTAMPOLTP>%s</TSTAMPOLTP>"
            + " <UPDMODE>%s</UPDMODE>"
            //            + " <DATETO>99991231</DATETO>"
            + " %s"
            + "</E1RSRMD>";
    private final String HIER_SEGMENT = ""
            + "<E1RSRHI SEGMENT=\"1\">"
            + " <INFOOBJECT><![CDATA[%s]]></INFOOBJECT>"
            + " %s"//HIER_SUB_SEGMENT & HIER_SUB_LANU_SEGMENT
            + "</E1RSRHI>";
    private final String HIER_SUB_SEGMENT = ""
            + "<E1RSRSH SEGMENT=\"1\">"
            + " <HIENM>%s</HIENM>"
            + " <HCLASS>%s</HCLASS>"
            + " <DATEFROM>%s</DATEFROM>"
            + " <DATETO>%s</DATETO>"
            + "</E1RSRSH>";
    private final String HIER_SUB_LANGU_SEGMENT = ""
            + "<E1RSRLH SEGMENT=\"1\">"
            + " <LANGU>%s</LANGU>"
            + "</E1RSRLH>";

    /**
     *
     * @param extractorLogger
     * @param dssapextractorparam
     */
    public DsSapExtractorIdocRequest(DsSapExtractorLogger extractorLogger, DsSapExtractorParam dssapextractorparam) {
        this.extractorLogger = extractorLogger;
        this.dssapextractorparam = dssapextractorparam;
        initfileInfo = new HashMap<String, String>();
    }

    public IDocRepository getExtractorIdocRepository() {
        return extractorIdocRepository;
    }

    public void setExtractorIdocRepository(IDocRepository extractorIdocRepository) {
        this.extractorIdocRepository = extractorIdocRepository;
    }

    public String getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(String requestDate) {
        this.requestDate = requestDate;
    }

    public String getRequestNumber() {
        return requestNumber;
    }

    public void setRequestNumber(String requestNumber) {
        this.requestNumber = requestNumber;
    }

    public String getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(String requestTime) {
        this.requestTime = requestTime;
    }

    public String getSegmentTimeStamp() {
        return segmentTimeStamp;
    }

    public void setSegmentTimeStamp(String segmentTimeStamp) {
        this.segmentTimeStamp = segmentTimeStamp;
    }

    /**
     * SAP connection initialization
     */
    public void initializationSAPConnection() {
        try {
            setExtractorIdocRepository(JCoIDoc.getIDocRepository(getExtractorDestination()));
        } catch (JCoException ex) {
            DsSapExtractorUtility.throwExtractorException(ex, extractorLogger);
        }
    }

    /**
     * SAP connection initialization with the help of JCoDestination cl
     *
     * @param manager
     */
    public void initializationSAPConnection(JCoDestination manager) {
        try {
            setExtractorDestination(manager);
            setExtractorIdocRepository(JCoIDoc.getIDocRepository(getExtractorDestination()));
        } catch (JCoException ex) {
            DsSapExtractorUtility.throwExtractorException(ex, extractorLogger);
        }
    }

    /**
     * DS Idoc internal variables initialization
     *
     */
    public void initializeInternalIDocParameters() {
        DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"Initialization of DS IDoc internal variables ..."});
//        try {
//            SimpleDateFormat inputsdf = new SimpleDateFormat("dd-MM-yyyy");
//            SimpleDateFormat outputsdf = new SimpleDateFormat("yyyyMMdd");
//            dssapextractorparam.setTrActDate(outputsdf.format(inputsdf.parse(dssapextractorparam.getTrActDate())));
//            if (dssapextractorparam.getDataFetchMode().equals("F")) {
//                dssapextractorparam.setDateFrom(outputsdf.format(inputsdf.parse(dssapextractorparam.getDateFrom())));
//                dssapextractorparam.setDateTo(outputsdf.format(inputsdf.parse(dssapextractorparam.getDateTo())));
//            }
        Date currentDate = new Date();
        setRequestDate(getSdfDate().format(currentDate));
        DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"Request Date : " + getRequestDate()});
        setRequestTime(getSdfTime().format(currentDate));
        DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"Request Time : " + getRequestTime()});
        setSegmentTimeStamp(dssapextractorparam.getTrActDate() + getRequestTime());
        setSegmentTimeStamp(fetchIdocTimeStamp(dssapextractorparam.getSegmentStructure()));
        DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"Segment Timestamp : " + getSegmentTimeStamp()});

        setRequestNumber(generateDynamicRequestNumber());
        DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"Dynamic Request Number : " + getRequestNumber()});

        dssapextractorparam.setIdocRequestNumber(getRequestNumber());
        int nodeId = dssapextractorparam.getDataStageNodeId();
        if(nodeId==0)
        {
        String datasourcetype = "A";
        if (dssapextractorparam.getDataSrcType().equals("HIER")) { datasourcetype = "H"; }
        File initfile = new File(dssapextractorparam.getFilePath()+dssapextractorparam.getJobName()+BS+getRequestNumber()+JOBNAMETAG+dssapextractorparam.getJobName()+NOOFNODESTAG+dssapextractorparam.getTotalNumberofNodes()+PERCENT+datasourcetype);
        File duplicateFile = new File(dssapextractorparam.getFilePath()+getRequestNumber()+JOBNAMETAG+dssapextractorparam.getJobName()+NOOFNODESTAG+dssapextractorparam.getTotalNumberofNodes()+PERCENT+datasourcetype);
        DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"initfile : " + initfile.getAbsolutePath()});
        DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"duplicateFile : " + duplicateFile.getAbsolutePath()});
        dssapextractorparam.setInitFile(duplicateFile);
        try
        {
        	DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"initfile.getAbsoluteFile().getParentFile().mkdir() : "+initfile.getAbsoluteFile().getParent()});
        	initfile.getAbsoluteFile().getParentFile().mkdirs();
        	initfile.createNewFile();
        	DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"initfile created : "});
        	Files.copy(initfile.toPath(), duplicateFile.toPath(),StandardCopyOption.REPLACE_EXISTING);
        	DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"duplicateFile created : "});
        }
        catch(Exception e)
        {
        	DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"Error in creating file" + e.getMessage()});
        	DsSapExtractorLogger.fatal("Error in Creating the file due to "+e.getMessage());
        }	
        }
        else
        {
        	while(! initfileInfo.containsKey(dssapextractorparam.getJobName()))
			{
				readinitfile(dssapextractorparam.getFilePath()+dssapextractorparam.getJobName()+BS);
			}
        	setRequestNumber(initfileInfo.get(dssapextractorparam.getJobName()));
        }
        createReceivingFolder(nodeId);
        //creating the common resource object for the current thread
        DsExtractorCommonResource commonResource = new DsExtractorCommonResource();
        commonResource.setOpenToolParameters(dssapextractorparam);
        commonResource.setExtractorLogger(extractorLogger);
        commonResource.updateExtractionsTimeout();
        //registering the shared resource object
        DsExtractorProcessState.getExtractorSharedObj().put(getRequestNumber(), commonResource);

        setIdocXmldata(formateIDocXMLRequest());
        if (!validateXMLFile()) {
            DsSapExtractorLogger.writeToLogFile(Level.SEVERE,"DeltaExtractIDocStage_01", new Object[]{getRequestNumber()});
            DsSapExtractorUtility.throwExtractorException("Invalid data file entry for Request no. : " + getRequestNumber(), extractorLogger);
        }
//        } catch (ParseException pe) {
//            DsSapDsSapExtractorLogger.writeToLogFile(Level.SEVERE,"DeltaExtract", new Object[]{"Invalid data format, please provide the Date as MM.DD.YYYY"});
//            DsSapExtractorUtility.throwExtractorException("Invalid data format, please provide the Date as MM.DD.YYYY " + pe);
//        }
    }

    /**
     * Reads the IDocXmlTemplate from the provided path
     *
     * @return
     */
    private StringBuilder getIDocXMLTemplate(String idocTemplatePath) throws URISyntaxException {
        DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"Reading the IDoc XML template file ..."});
        InputStream xmlFileReader;
        StringBuilder builder = new StringBuilder();
//        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Class<? extends DsSapExtractorIdocRequest> loader = this.getClass();

        DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"Fetching Idoc xml file as stream ..."});
        try {
            xmlFileReader = loader.getResourceAsStream(idocTemplatePath);
//            xmlFileReader = new FileReader(IDocXMLFilePath);
            BufferedReader bufferReader = new BufferedReader(new InputStreamReader(xmlFileReader));
            String line;
            while ((line = bufferReader.readLine()) != null) {
                builder.append(line);
            }
            bufferReader.close();
            xmlFileReader.close();
            DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"Reading Idoc template file is complete ..."});
        } catch (IOException ex) {
            DsSapExtractorUtility.throwExtractorException(ex, extractorLogger);
        }
        return builder;
    }

    /**
     * Sets the IDoc external & internal parameters into the idocXmlTemplate
     *
     * @return
     */
    private String formateIDocXMLRequest() {
        DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"Setting the internal & external parameters ..."});
        StringBuilder xmlDoc = new StringBuilder();
        String reqObjectType = "D";
        try {
//            if (!dssapextractorparam.getDataFetchMode().equals("F")) {
////                xmlDoc = String.format(getIDocXMLTemplate(IDocXMLFilePath).toString(), dssapextractorparam.getSapClient(), dssapextractorparam.getSapIdocRelVersion(), dssapextractorparam.getSenderPort(), dssapextractorparam.getSenderPortNumber(), dssapextractorparam.getReceiverPortNumber(), getRequestNumber(), getRequestDate(), getRequestTime(), dssapextractorparam.getRequestUser(), dssapextractorparam.getSegmentStructure(), getSegmentTimeStamp(), dssapextractorparam.getDataFetchMode());
//                xmlDoc = String.format(getIDocXMLTemplate(IDocXMLFilePath).toString(), dssapextractorparam.getSapClient(), dssapextractorparam.getSenderPort(), dssapextractorparam.getSenderPortNumber(), dssapextractorparam.getReceiverPortNumber(), getRequestNumber(), getRequestDate(), getRequestTime(), dssapextractorparam.getRequestUser(), dssapextractorparam.getSegmentStructure(), getSegmentTimeStamp(), dssapextractorparam.getDataFetchMode());
//            } else {
////                xmlDoc = String.format(getIDocXMLTemplate(IDocFLXMLFilePath).toString(), dssapextractorparam.getSapClient(), dssapextractorparam.getSapIdocRelVersion(), dssapextractorparam.getSenderPort(), dssapextractorparam.getSenderPortNumber(), dssapextractorparam.getReceiverPortNumber(), getRequestNumber(), getRequestDate(), getRequestTime(), dssapextractorparam.getRequestUser(), dssapextractorparam.getSegmentStructure(), getSegmentTimeStamp(), dssapextractorparam.getDataFetchMode(), dssapextractorparam.getDateFrom(), dssapextractorparam.getDateTo());
//                xmlDoc = String.format(getIDocXMLTemplate(IDocFLXMLFilePath).toString(), dssapextractorparam.getSapClient(), dssapextractorparam.getSenderPort(), dssapextractorparam.getSenderPortNumber(), dssapextractorparam.getReceiverPortNumber(), getRequestNumber(), getRequestDate(), getRequestTime(), dssapextractorparam.getRequestUser(), dssapextractorparam.getSegmentStructure(), getSegmentTimeStamp(), dssapextractorparam.getDataFetchMode(), dssapextractorparam.getDateFrom(), dssapextractorparam.getDateTo());
//            }            
            if (dssapextractorparam.getDataSrcType().equals("HIER")) {
                reqObjectType = "H";
            } else if (dssapextractorparam.getDataSrcType().equals("ATTR") || dssapextractorparam.getDataSrcType().equals("TEXT")) {
                reqObjectType = "M"; //getTransactionSubSegment
            }

            xmlDoc.append(String.format(getIDocXMLTemplate(IDocXMLFilePath).toString(),
                    dssapextractorparam.getSapClient(), dssapextractorparam.getSenderPort(), dssapextractorparam.getSenderPortNumber(),
                    dssapextractorparam.getReceiverPortNumber(), getRequestNumber(), reqObjectType, getRequestDate(),
                    getRequestTime(), dssapextractorparam.getSapUser(), prepareDataSegment()));

//            xmlDoc = String.format(getIDocXMLTemplate().toString(), mandt, sapIdocRelVersion, senderPort, senderPortType, senderPortNumber, receiverPortType, receiverPortNumber, requestNumber, requestDate, requestTime, requestUser, segmentStructure, segmentTimeStamp);
            DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{xmlDoc});
        } catch (URISyntaxException e) {
            DsSapExtractorUtility.throwExtractorException(e, extractorLogger);
        }
        return xmlDoc.toString();
    }

    private boolean validateXMLFile() {
        boolean flag = false;
        try {
            flag = true;
        } catch (Exception e) {
        }
        return flag;
    }

    /**
     * Generates dynamic request number Request Number Pattern :
     * REQU_DS<date><time><last end characters of TID>
     *
     * @return
     */
    private String generateDynamicRequestNumber() {
        String reqNumber = null;
        DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"Generating Dynamic Request Number ..."});
        try {
            setRequestTID(getExtractorDestination().createTID());
            StringBuilder buff = new StringBuilder();
            buff.append("REQU_DS");
            buff.append(getRequestDate());
            buff.append(getRequestTime());
            buff.append(getRequestTID().substring((getRequestTID().length() - 8)));
            reqNumber = buff.toString();
            DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"Dynamic Request Number : " + reqNumber});
            DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"Dynamic Request Number generated successfully ..."});
        } catch (JCoException e) {
            DsSapExtractorLogger.writeToLogFile(Level.SEVERE,"DeltaExtract_10040", new Object[]{e.toString()});
            DsSapExtractorUtility.throwExtractorException(e, extractorLogger);
        }
        return reqNumber;
    }

    /**
     * Sends the IDoc request
     */
    public void sendIDocXMLRequest() {
    	File f = dssapextractorparam.getdataPacketFolderPath().getParentFile();
    	boolean readytosend = true;
    	while(readytosend)
    	 {
    		if(f.list().length == dssapextractorparam.getTotalNumberofNodes())
    			readytosend = false;
    	if((dssapextractorparam.getDataStageNodeId()==0) && (! readytosend))
    	{
        try {
            //fetch count of delta record to be fetched
   //         DsExtractorFuntionHandler.actualNumRowCount = fetchDeltaRowCount();

            IDocFactory iDocFactory = JCoIDoc.getIDocFactory();
            IDocXMLProcessor processor = iDocFactory.getIDocXMLProcessor();
            IDocDocumentList iDocList = processor.parse(getExtractorIdocRepository(), getIdocXmldata());
            for (int i = 0; i < iDocList.size(); i++) {
                IDocDocument l_oIDocDocument = iDocList.get(i);
                l_oIDocDocument.checkSyntax();
            }
            DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"Sending the SAP IDoc Request to SAP ..."});
            JCoIDoc.send(iDocList, IDocFactory.IDOC_VERSION_DEFAULT, getExtractorDestination(), getRequestTID());
            getExtractorDestination().confirmTID(getRequestTID());
            DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"DeltaExtractIDocStage_07"});
//            DsSapDsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{iDocList});

            //fetch posted idoc status
//            fetchInboundIdocStatus();
//            DsSapDsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"SAP IDoc Request sent successfully ..."});
        } catch (IDocParseException e) {
            DsSapExtractorLogger.writeToLogFile(Level.SEVERE,"DeltaExtract_10040", new Object[]{e.toString()});
            DsSapExtractorUtility.throwExtractorException(e, extractorLogger);
        } catch (IDocMetaDataUnavailableException e) {
            DsSapExtractorLogger.writeToLogFile(Level.SEVERE,"DeltaExtract_10040", new Object[]{e.toString()});
            DsSapExtractorUtility.throwExtractorException(e, extractorLogger);
        } catch (IDocSyntaxException e) {
            DsSapExtractorLogger.writeToLogFile(Level.SEVERE,"DeltaExtract_10040", new Object[]{e.toString()});
            DsSapExtractorUtility.throwExtractorException(e, extractorLogger);
        } catch (JCoException e) {
            DsSapExtractorLogger.writeToLogFile(Level.SEVERE,"DeltaExtract_10040", new Object[]{e.toString()});
            DsSapExtractorUtility.throwExtractorException(e, extractorLogger);
        }
    	}
    	 }
    	DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"Data Source type ..."+dssapextractorparam.getDataSrcType().equals("HIER")});
    	if(! dssapextractorparam.getDataSrcType().equals("HIER"))
        dssapextractorparam.setSapTransferStructureName(fetchSegmentName(dssapextractorparam.getSegmentStructure(), dssapextractorparam.getReceiverPortNumber(), dssapextractorparam.getSenderPortNumber()));
    	DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"Data Source type1 ..."+dssapextractorparam.getDataSrcType().equals("HIER")});
    }

    
    public void readinitfile(String folderPath)
	{
		File folder = new File(folderPath);
		if(folder.exists())
		{
		String[] numberOfFiles = folder.list();
		for(String fileName:numberOfFiles){
			try
			{
			String reqId = fileName.substring(0,fileName.indexOf(JOBNAMETAG));
			String jobName = fileName.substring(fileName.indexOf(JOBNAMETAG)+3,fileName.indexOf(NOOFNODESTAG));
			initfileInfo.put(jobName, reqId);
			}
			catch(Exception e)
			{
			}
		}
		}
		}
    
    private void createReceivingFolder(int dsStageNodeId){
    	DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"+++createReceivingFolder++++"});
    	String dataPacketFolderPath = dssapextractorparam.getFilePath()+BS+dssapextractorparam.getJobName()+BS+getRequestNumber()+BS+"DataPackets"+BS+"complete"+BS+dsStageNodeId+BS;
    	String infoIdocFolderPath = dssapextractorparam.getFilePath()+BS+dssapextractorparam.getJobName()+BS+getRequestNumber()+BS+"InfoIDoc"+BS;
		String datapacketInfoFolderPath = dssapextractorparam.getFilePath()+BS+dssapextractorparam.getJobName()+BS+getRequestNumber()+BS+"DataPackets"+BS+"packetcountinfo"+BS;
    	File dataPacketFolder = new File(dataPacketFolderPath);
		File requestInfoFolder = new File(infoIdocFolderPath);
		File datapacketInfoFolder = new File(datapacketInfoFolderPath);
		File requestInfoFolderrsInfoState2 = new File(infoIdocFolderPath+"2"+BS);
		try
		{
			dataPacketFolder.mkdirs();
			requestInfoFolderrsInfoState2.mkdirs();
			datapacketInfoFolder.mkdir();
			dssapextractorparam.setReceivedPacketFolders(dataPacketFolder,requestInfoFolder);
		}
		catch (Exception e)
		{
			DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"Error in creating the folder"});
			throw new RuntimeException(e.getMessage());
		}
		DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"----createReceivingFolder----"});
	}

	/**
     * fetch Posted Idoc status
     */
    public void fetchInboundIdocStatus() {
        try {
            DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{new StringBuilder().append("Fetching Status of ").append(getRequestNumber())});
            JCoFunction function = getExtractorDestination().getRepository().getFunction(ZDS_DE_IDOC_STATUS_GET_V8_0);
            function.getImportParameterList().setValue("IV_VAL", getRequestNumber());
            DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"Request Number : " + getRequestNumber()});
            function.getImportParameterList().setValue("I_DATE_FROM", new java.sql.Date(new Date().getTime()));
            DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"Idoc Creation Date : " + new java.sql.Date(new Date().getTime())});
            function.getImportParameterList().setValue("IV_DIRECTION", "2");
            DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"Idoc Direction : Inbound"});

            function.execute(getExtractorDestination());
            readETReturn(function);
        } catch (JCoException e) {
            DsSapExtractorLogger.writeToLogFile(Level.SEVERE,"DeltaExtractIDocStage_02", new Object[]{e.toString()});
            DsSapExtractorUtility.throwExtractorException(e, extractorLogger);
        }
    }

    /**
     * reads and log the ET_RETURN table for the the executed RFCs
     *
     * @param function
     */
    private void readETReturn(JCoFunction function) {
        JCoTable etReturn = function.getTableParameterList().getTable("ET_RETURN");
        etReturn.firstRow();
        String msgType = null;
        StringBuilder errorMsg = new StringBuilder();
        if (etReturn.getNumRows() >= 1) {
            etReturn.firstRow();
            while (etReturn.nextRow()) {
                msgType = etReturn.getValue("TYPE").toString();
                if (msgType.equalsIgnoreCase("E")) {
                    errorMsg.append("Error Details");
                    errorMsg.append(("" + (char) 10));
                    errorMsg.append("-----------------");
                    errorMsg.append(("" + (char) 10));
                    errorMsg.append("   Type: E ");
                    errorMsg.append(("" + (char) 10));
                    errorMsg.append("   ID: ").append(etReturn.getValue("ID"));
                    errorMsg.append(("" + (char) 10));
                    errorMsg.append("   Message: ").append(etReturn.getValue("MESSAGE"));
                } else {
                    DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{new StringBuilder().append("SAP Return Details(Inbound Idoc)")});
                    DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{new StringBuilder().append("   Type: ").append(msgType)});
                    DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{new StringBuilder().append("   ID: ").append(etReturn.getValue("ID"))});
                    DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{new StringBuilder().append("   Message: ").append(etReturn.getValue("MESSAGE"))});
                }
            }
        }
        if (errorMsg.length() > 0) {
        	DsSapExtractorLogger.writeToLogFile(Level.SEVERE,"DeltaExtract", new Object[]{errorMsg.toString()});
            DsSapExtractorLogger.fatal("DeltaExtract_FatalError_01",new Object[]{errorMsg.toString()});
        }
    }

    /**
     * @return the IDocXMLFilePath
     */
    public String getIDocXMLFilePath() {
        return IDocXMLFilePath;
    }

    /**
     * @return the extractorDestination
     */
    public JCoDestination getExtractorDestination() {
        return extractorDestination;
    }

    /**
     * @param extractorDestination the extractorDestination to set
     */
    public void setExtractorDestination(JCoDestination extractorDestination) {
        this.extractorDestination = extractorDestination;
    }

    /**
     * @return the sdfDate
     */
    public SimpleDateFormat getSdfDate() {
        return sdfDate;
    }

    /**
     * @param sdfDate the sdfDate to set
     */
    public void setSdfDate(SimpleDateFormat sdfDate) {
        this.sdfDate = sdfDate;
    }

    /**
     * @return the sdfTime
     */
    public SimpleDateFormat getSdfTime() {
        return sdfTime;
    }

    /**
     * @param sdfTime the sdfTime to set
     */
    public void setSdfTime(SimpleDateFormat sdfTime) {
        this.sdfTime = sdfTime;
    }

    /**
     * @return the idocXmldata
     */
    public String getIdocXmldata() {
        return idocXmldata;
    }

    /**
     * @param idocXmldata the idocXmldata to set
     */
    public void setIdocXmldata(String idocXmldata) {
        this.idocXmldata = idocXmldata;
    }

    /**
     * @return the requestTID
     */
    public String getRequestTID() {
        return requestTID;
    }

    /**
     * @param requestTID the requestTID to set
     */
    public void setRequestTID(String requestTID) {
        this.requestTID = requestTID;
        com.ibm.is.sappack.deltaextractstage.client.DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"requestTID: "+requestTID});
    }


    /**
     * @param extractorLogger the extractorLogger to set
     */
    public void setExtractorLogger(DsSapExtractorLogger extractorLogger) {
        this.extractorLogger = extractorLogger;
    }

    public void releaseResource() {
        extractorIdocRepository = null;
    }

    /**
     *
     * @return
     */
 /*   private int fetchDeltaRowCount() {
        try {
            com.ibm.is.sappack.deltaextractstage.client.DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"Fetching Number Delta record count"});
            JCoFunction function = extractorDestination.getRepository().getFunction(ZDS_CDC_TOTAL_RECORDS);
            function.getImportParameterList().setValue("IV_OLTPSOURCE", dssapextractorparam.getSegmentStructure());
            com.ibm.is.sappack.deltaextractstage.client.DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"    OLTP Source : " + dssapextractorparam.getSegmentStructure()});
            function.getImportParameterList().setValue("IV_SLOGSYS", dssapextractorparam.getReceiverPortNumber());
            com.ibm.is.sappack.deltaextractstage.client.DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"    Receiver Port No. : " + dssapextractorparam.getReceiverPortNumber()});
            function.getImportParameterList().setValue("IV_RLOGSYS", dssapextractorparam.getSenderPortNumber());
            com.ibm.is.sappack.deltaextractstage.client.DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"    Sender Port No. : " + dssapextractorparam.getSenderPortNumber()});
            function.getImportParameterList().setValue("IV_UPMOD", dssapextractorparam.getDataFetchMode());
            com.ibm.is.sappack.deltaextractstage.client.DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"    Data Fetch Mode : " + dssapextractorparam.getDataFetchMode()});

            function.execute(extractorDestination);
            readETReturn(function);
            com.ibm.is.sappack.deltaextractstage.client.DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"    Delta Record Count : " + function.getExportParameterList().getValue("EX_COUNT")});
            return Integer.parseInt(function.getExportParameterList().getValue("EX_COUNT").toString());

        } catch (JCoException e) {
            com.ibm.is.sappack.deltaextractstage.client.DsSapExtractorLogger.writeToLogFile(Level.SEVERE,"DeltaExtract", new Object[]{"Error Details(fetchDeltaRowCount): " + e.toString()});
            DsSapExtractorUtility.throwExtractorException(e, extractorLogger);
        }
        return 0;
    }*/

    /**
     * respect to the provided target system
     *
     * @param oltpSource
     * @return
     */
    public String fetchIdocTimeStamp(String oltpSource) {
        String idocTimeStamp = null;
        try {
            com.ibm.is.sappack.deltaextractstage.client.DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"Fetching Transfer Structure Idoc Date and Time ..."});
            JCoFunction function = extractorDestination.getRepository().getFunction(ZDS_DE_VALIDATE_TRANS_STR_V8_0);
            function.getImportParameterList().setValue("IV_OLTPSOURCE", oltpSource);
            com.ibm.is.sappack.deltaextractstage.client.DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"OLTP Source : " + oltpSource});
            String transferStruName = fetchSegmentName(dssapextractorparam.getSegmentStructure(), dssapextractorparam.getReceiverPortNumber(), dssapextractorparam.getSenderPortNumber());
            function.getImportParameterList().setValue("IV_TFSTRIDOC", transferStruName);
            com.ibm.is.sappack.deltaextractstage.client.DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"TR Name : " + transferStruName});

            function.execute(extractorDestination);
            idocTimeStamp = function.getExportParameterList().getValue("EX_TSTPDA").toString();
            readETReturn(function);
        } catch (JCoException e) {
            com.ibm.is.sappack.deltaextractstage.client.DsSapExtractorLogger.writeToLogFile(Level.SEVERE,"DeltaExtractIDocStage_03", new Object[]{e.toString()});
            DsSapExtractorUtility.throwExtractorException(e, extractorLogger);
        }
        return idocTimeStamp;
    }
    

    public String fetchSegmentName(String oltpName, String receiverPrtNr, String senderPrtNr) {
        try {
            com.ibm.is.sappack.deltaextractstage.client.DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"Fetching Transfer Structure for OLTP : " + oltpName});
            JCoFunction func = extractorDestination.getRepository().getFunction(RFC_READ_TABLE);
            func.getImportParameterList().setValue("QUERY_TABLE", "ROOSGEN");
            JCoTable optionTab = func.getTableParameterList().getTable("OPTIONS");
            optionTab.appendRow();
            optionTab.setValue("TEXT", new StringBuilder().append("OLTPSOURCE = '").append(oltpName).append("'").toString());
            optionTab.appendRow();
            optionTab.setValue("TEXT", new StringBuilder().append("AND").toString());
            optionTab.appendRow();
            optionTab.setValue("TEXT", new StringBuilder().append("SLOGSYS = '").append(receiverPrtNr).append("'").toString());
            com.ibm.is.sappack.deltaextractstage.client.DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"SLOGSYS =  " + receiverPrtNr});
            optionTab.appendRow();
            optionTab.setValue("TEXT", new StringBuilder().append("AND").toString());
            optionTab.appendRow();
            optionTab.setValue("TEXT", new StringBuilder().append("RLOGSYS = '").append(senderPrtNr).append("'").toString());
            com.ibm.is.sappack.deltaextractstage.client.DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"RLOGSYS =  " + senderPrtNr});

            JCoTable fieldTab = func.getTableParameterList().getTable("FIELDS");
            fieldTab.appendRow();
            fieldTab.setValue("FIELDNAME", "TFSTRUC");

            func.execute(extractorDestination);

            JCoTable dataTab = func.getTableParameterList().getTable("DATA");
            if (dataTab.isEmpty()) {
                com.ibm.is.sappack.deltaextractstage.client.DsSapExtractorLogger.writeToLogFile(Level.SEVERE,"DeltaExtractIDocStage_04", new Object[]{});
                DsSapExtractorUtility.throwExtractorException("No Transfer Structure found ...", extractorLogger);
            }
            return dataTab.getValue("WA").toString();

        } catch (JCoException e) {
            com.ibm.is.sappack.deltaextractstage.client.DsSapExtractorLogger.writeToLogFile(Level.SEVERE,"DeltaExtractIDocStage_05", new Object[]{});
            com.ibm.is.sappack.deltaextractstage.client.DsSapExtractorLogger.writeToLogFile(Level.SEVERE,"DeltaExtract_10040", new Object[]{e.toString()});
            DsSapExtractorUtility.throwExtractorException("Error occured while fetching Transfer Structure " + e.toString(), extractorLogger);
        }
        return null;
    }

    /**
     * prepare the data segment according to the data source type and delta mode
     *
     * @return
     */
    public String prepareDataSegment() {
        StringBuilder dataSegment = new StringBuilder();
        StringBuilder subSegment = new StringBuilder();
        if (dssapextractorparam.getDataSrcType().equals("TRAN")) {
        	subSegment = getTransactionSubSegment();
            dataSegment.append(String.format(TRANS_SEGMENT, dssapextractorparam.getSegmentStructure(), getSegmentTimeStamp(), dssapextractorparam.getDataFetchMode(), subSegment));
        } else if (dssapextractorparam.getDataSrcType().equals("TEXT") || dssapextractorparam.getDataSrcType().equals("ATTR")) {          
        	subSegment = getTransactionSubSegment();
        	dataSegment.append(String.format(ATTR$TEXT_SEGMENT, dssapextractorparam.getSegmentStructure(), getSegmentTimeStamp(), dssapextractorparam.getDataFetchMode(), subSegment));
        } else if (dssapextractorparam.getDataSrcType().equals("HIER")) {
        //    initializeHierData();
            subSegment.append(String.format(HIER_SUB_SEGMENT, dssapextractorparam.getHierName(), dssapextractorparam.getHierClass(), dssapextractorparam.getDateFrom(), dssapextractorparam.getDateTo()));
            String lang = dssapextractorparam.getSapLanguage();
            	if(!lang.equals("")) {
                subSegment.append(String.format(HIER_SUB_LANGU_SEGMENT, lang));
            }
            dataSegment.append(String.format(HIER_SEGMENT, dssapextractorparam.getSegmentStructure(), subSegment));
        }
        return dataSegment.toString();
    }

    /**
     * fetch the Hierarchy language for the provided hierarchy datasource
     *
     * @param dataSource [data source name]
     * @return Language
     */
    public String fetchHierLang(String dataSource) {
        StringBuffer langu = new StringBuffer();
        try {
            com.ibm.is.sappack.deltaextractstage.client.DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"Fetching hier Language for Data Source : " + dataSource});
            JCoFunction func = extractorDestination.getRepository().getFunction(ZDS_DE_HIER_EXTRACT_V8_0);
            func.getImportParameterList().setValue("IV_OLTPSOURCE", dataSource);
            func.execute(extractorDestination);
            JCoTable languTab = func.getTableParameterList().getTable("T_LANGU");
            while(languTab.nextRow())
            {
            	langu.append(languTab.getValue("LANGU")).append(",");
            }
            com.ibm.is.sappack.deltaextractstage.client.DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"Hier Language fetched is : " + langu});
            readETReturn(func);
        } catch (JCoException e) {
            com.ibm.is.sappack.deltaextractstage.client.DsSapExtractorLogger.writeToLogFile(Level.SEVERE,"DeltaExtractIDocStage_06", new Object[]{dataSource});
            com.ibm.is.sappack.deltaextractstage.client.DsSapExtractorLogger.writeToLogFile(Level.SEVERE,"DeltaExtract_10040", new Object[]{e.toString()});
            DsSapExtractorUtility.throwExtractorException("Error occured while fetching Data Transfer Mode for Data Source : " + dataSource + "\n" + e.toString(), extractorLogger);
        }
        return langu.toString();
    }
/*
    public void initializeHierData() {
        String val = "";
        for (String arrStr : dssapextractorparam.getHierDetails().split("AND")) {
            if (arrStr.contains("HCLASS")) {
                val = arrStr.split("=")[1];
                val = val.substring(2, val.lastIndexOf("'"));
                dssapextractorparam.setHierClass(val);
                com.ibm.is.sappack.deltaextractstage.client.DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"Hierarcy Class : " + dssapextractorparam.getHierClass()});
            } else if (arrStr.contains("DATETO")) {
                val = arrStr.split("=")[1];
                val = val.substring(2, val.lastIndexOf("'")).replaceAll("-", "");
                dssapextractorparam.setDateTo(val);
                com.ibm.is.sappack.deltaextractstage.client.DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"Hierarcy End Date : " + dssapextractorparam.getDateTo()});
            } else if (arrStr.contains("DATEFROM")) {
                val = arrStr.split("=")[1];
                val = val.substring(2, val.lastIndexOf("'")).replaceAll("-", "");
                dssapextractorparam.setDateFrom(val);
                com.ibm.is.sappack.deltaextractstage.client.DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"Hierarcy Start Date : " + dssapextractorparam.getDateFrom()});
            } else if (arrStr.contains("LANGU")) {
                val = arrStr.split("=")[1];
                val = val.substring(2, val.lastIndexOf("'"));
                dssapextractorparam.setHierLang(val);
                com.ibm.is.sappack.deltaextractstage.client.DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"Hierarcy Supported Language : " + dssapextractorparam.getHierLang()});
            }
        }
    }*/
    public StringBuilder getTransactionSubSegment() {
        StringBuilder builder = new StringBuilder();
        Iterator<?> filterItr = dssapextractorparam.getIdocFilterClause().iterator();
        while (filterItr.hasNext()) {
            Object object = filterItr.next();
//            String[] data = object.toString().split(",");
            if(dssapextractorparam.getDataSrcType().equals("TRAN"))
            builder.append(String.format("<E1RSRSL SEGMENT=\"1\">%s</E1RSRSL>", object));
            else if(dssapextractorparam.getDataSrcType().equals("TEXT") || dssapextractorparam.getDataSrcType().equals("ATTR")) {
            builder.append(String.format("<E1RSRSM SEGMENT=\"1\">%s</E1RSRSM>", object));	
            }
        }
        return builder;
    }
}
