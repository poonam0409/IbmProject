//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2009, 2011                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.is.sappack.dsstages.idoc.listener.statussender
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.dsstages.idoc.listener.statussender;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.is.sappack.dsstages.common.IDocField;
import com.ibm.is.sappack.dsstages.common.IDocMetadataFactory;
import com.ibm.is.sappack.dsstages.common.IDocSegment;
import com.ibm.is.sappack.dsstages.common.IDocType;
import com.ibm.is.sappack.dsstages.common.StageLogger;
import com.ibm.is.sappack.dsstages.idoc.listener.handler.IDocFile;
import com.ibm.is.sappack.dsstages.idoc.listener.util.IDocServerConstants;
import com.ibm.is.sappack.dsstages.idoc.listener.util.IDocServerMessages;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoRepository;
import com.sap.conn.jco.JCoTable;

/**
 * StatusIDocSenderImpl
 * 
 * Implementation of an StatusIDocSender
 * to send status IDocs to SAP after
 * receiving IDocs from SAP
 */
public class StatusIDocSenderImpl implements StatusIDocSender {
	
	/* logger */
	private Logger logger = null;
	
	/* class name for logging purposes */
	static final String CLASSNAME = StatusIDocSenderImpl.class.getName();
	
	/* JCo function IDOC_INBOUND_ASYNCHRONOUS to send IDocs to SAP */
	private JCoFunction idocInboundAsynchronous = null;
	
	/* control record table IDOC_CONTROL_REC_40 */
	private JCoTable controlRecordTable = null;
	
	/* segment data table IDOC_DATA_REC_40 */
	private JCoTable segmentDataTable = null;
	
	/* JCo destination */
	private JCoDestination destination = null;
	
	/* Root segment E1STATS of IDocType SYSTAT01 */
	private IDocSegment e1stats = null;
	
	/* SDATA buffer template holds the date of segment E1STATS */
	private String sdataBufferTemplate = "";

	/* Date formatter for IDoc field LOGDAT */
	private SimpleDateFormat dateFormat = null;

	/* Time formatter for IDoc field LOGTIM */
	private SimpleDateFormat timeFormat = null;
	
	/**
	 * StatusIDocSenderImpl
	 */
	public StatusIDocSenderImpl() {
		
		/* initialize logger */
		this.logger = StageLogger.getLogger();
	}

	@Override
	public void initialize(JCoDestination destination, String sapSystemName) throws JCoException, IOException {
		
		final String METHODNAME = "initialize(JCoDestination destination)"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);
		
		/* store the destination - we need it when calling IDOC_INBOUND_ASYNCHRONOUS */
		this.logger.log(Level.FINE, "Initializing StatusIDocSender for JCO destination {0}", destination.getDestinationName());
		this.destination = destination;
		
		/* retrieve meta data for IDoc type SYSTAT01 */
		this.logger.log(Level.FINER, "Retrieving meta data for IDoc type {0}", IDocServerConstants.SYSTAT01);
		IDocType systat01 = IDocMetadataFactory.createIDocType(destination, sapSystemName, IDocServerConstants.SYSTAT01, null, null);
		
		/* get root segment E1STATS of IDoc type SYSTAT01 */
		this.logger.log(Level.FINER, "Extracting root segment {0}",IDocServerConstants.E1STATS);
		Iterator<IDocSegment> rootSegments = systat01.getRootSegments().iterator();
		while(rootSegments.hasNext()) {
			IDocSegment segment = rootSegments.next();
			if(segment.getSegmentTypeName().equals(IDocServerConstants.E1STATS)) {
				this.e1stats = segment;
			}
		}
		/* create a buffer template of the SDATA field */
		this.sdataBufferTemplate = this.createSDATATemplate();
		this.logger.log(Level.FINER, "Creating SDATA buffer template: {0}", this.sdataBufferTemplate);
	
		/* get JCo repository */
		JCoRepository repository = destination.getRepository();
		
		/* get function IDOC_INBOUND_ASYNCHRONOUS */
		this.logger.log(Level.FINER, "Retrieving meta data for remote function {0}", IDocServerConstants.IDOC_INBOUND_ASYNCHRONOUS);
		this.idocInboundAsynchronous = repository.getFunction(IDocServerConstants.IDOC_INBOUND_ASYNCHRONOUS);
		
		/* get table parameters */
		JCoParameterList tables = idocInboundAsynchronous.getTableParameterList();
		
		/* control record table IDOC_CONTROL_REC_40 */
		this.controlRecordTable = tables.getTable(IDocServerConstants.IDOC_CONTROL_REC_40);
		
		/* segment data table IDOC_DATA_REC_40 */
		this.segmentDataTable = tables.getTable(IDocServerConstants.IDOC_DATA_REC_40);
		
		/* Formatter for date and time */ 
		this.dateFormat = new SimpleDateFormat("yyyyMMdd"); // 20110330 for example
		this.timeFormat = new SimpleDateFormat("kkmmss"); // 170834 for example
		
		this.logger.exiting(CLASSNAME, METHODNAME);
	}

	@Override
	/**
	 * sendStatusIDocs
	 * 
	 * send a status IDoc for each IDoc identified
	 * by the given List of IDoc numbers
	 * 
	 * @param idocFiles
	 */
	public void sendStatusIDocs(List<IDocFile> idocFiles) throws JCoException {
		
		final String METHODNAME = "sendStatusIDocs(List<char[]> idocNumbers)"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);
		
		/* clear control record and segment data table before adding new records */
		this.controlRecordTable.clear();
		this.segmentDataTable.clear();
		
		/* calculate and format date and time */
		Date timestamp = new Date();
		/* date */
		String logDat = this.dateFormat.format(timestamp);
		/* time */
		String logTim = this.timeFormat.format(timestamp);
		
		/* iterate over IDocFile list and create entries in control record and segment data table */
		Iterator<IDocFile> idocFileIt = idocFiles.iterator();
		while(idocFileIt.hasNext()) {
			IDocFile idocFile = idocFileIt.next();
			/* get IDoc number */
			String idocNumber = idocFile.getIDocNumber();
			/* get sender partner number */
			String senderPartnerNumber = idocFile.getSenderPartnerNumber();
			/* get receiver partner number */
			String receiverPartnerNumber = idocFile.getReceiverPartnerNumber();
			/* create control record table entry */
			logger.log(Level.FINER, "Creating entry in control record table {0} for IDoc {1}", new Object[]{IDocServerConstants.IDOC_CONTROL_REC_40, idocNumber});
			this.createEntriesInControlRecordTable(idocNumber, senderPartnerNumber, receiverPartnerNumber);
			/* create segment data table entry */
			logger.log(Level.FINER, "Creating entry in segment data table {0} for IDoc {1}", new Object[]{IDocServerConstants.IDOC_DATA_REC_40, idocNumber});
			this.createEntriesInSegmentDataTable(idocNumber, logDat, logTim);
		}
		
		/* send status IDocs to SAP */
		this.logger.log(Level.FINE, "Sending {0} status IDocs to SAP", idocFiles.size());
		this.idocInboundAsynchronous.execute(this.destination);
		logger.log(Level.INFO, IDocServerMessages.SendingStatusIDocs);
		
		this.logger.exiting(CLASSNAME, METHODNAME);
	}
	
	static String copyright() {
		return com.ibm.is.sappack.dsstages.idoc.listener.statussender.Copyright.IBM_COPYRIGHT_SHORT;
	}
	
	/**
	 * createEntriesInControlRecordTable
	 * 
	 * @param idocNumber
	 * @param senderPartnerNumber
	 * @param receiverPartnerNumber
	 */
	private void createEntriesInControlRecordTable(String idocNumber, String senderPartnerNumber, String receiverPartnerNumber) {
		
		/* create entry in control record table */
		controlRecordTable.appendRow();
		/* DOCNUM */
		controlRecordTable.setValue(IDocServerConstants.DOCNUM, idocNumber);
		/* IDOCTYP */
		controlRecordTable.setValue(IDocServerConstants.IDOCTYP, IDocServerConstants.SYSTAT01);
		/* MESTYP */
		controlRecordTable.setValue(IDocServerConstants.MESTYP, IDocServerConstants.STATUS);
		/* SNDPRN use receiver data of incoming IDocs as sender data for status IDocs */
		controlRecordTable.setValue(IDocServerConstants.SNDPRN, receiverPartnerNumber);
		/* RCVPRN use sender data of incoming IDocs as receiver data for status IDocs */
		controlRecordTable.setValue(IDocServerConstants.RCVPRN, senderPartnerNumber);
		/* SNDPRT */
		controlRecordTable.setValue(IDocServerConstants.SNDPRT, IDocServerConstants.LOGICALSYSTEM);
		/* RCVPRT */
		controlRecordTable.setValue(IDocServerConstants.RCVPRT, IDocServerConstants.LOGICALSYSTEM);
	}
	
	/**
	 * createEntriesInSegmentDataTable
	 * 
	 * @param idocNumber
	 * @param date
	 * @param time
	 */
	private void createEntriesInSegmentDataTable(String idocNumber, String date, String time) {
		
		segmentDataTable.appendRow();
		/* SEGNAM */
		segmentDataTable.setValue(IDocServerConstants.SEGNAM, IDocServerConstants.E1STATS);
		/* DOCNUM */
		segmentDataTable.setValue(IDocServerConstants.DOCNUM, idocNumber);
		/* SEGNUM */
		segmentDataTable.setValue(IDocServerConstants.SEGNUM, 0);
		/* PSGNUM */
		segmentDataTable.setValue(IDocServerConstants.PSGNUM, 0);
		/* HLEVEL */
		segmentDataTable.setValue(IDocServerConstants.HLEVEL, 0);
		/* fill SDATA template with given values */
		String sdata = String.format(this.sdataBufferTemplate, idocNumber, date, time);
		logger.log(Level.FINER, "SDATA: ", sdata);
		segmentDataTable.setValue(IDocServerConstants.SDATA, sdata);
	}

	
	/**
	 * fillWithBlanks
	 * 
	 * fill the given IDoc segment field
	 * with blanks if the given value does
	 * not fill the IDocField length completely
	 * 
	 * @param field
	 * @param value
	 * @return
	 */
	private String fillWithBlanks(IDocField field, String value) {
		
		int fieldLength = field.getLengthAsString();
		/* right pad value with blanks */
		return String.format("%1$-" + fieldLength + "s", value);  		
	}
	
	/**
	 * createSDATATemplate
	 * 
	 * @return
	 */
	private String createSDATATemplate() {
		
		StringBuffer sb = new StringBuffer();
		
		Iterator<IDocField> fields = this.e1stats.getFields().iterator();
		while(fields.hasNext()) {
			IDocField field = fields.next();
			String fieldName = field.getFieldName();
			
			/* append TABNAM */
			if(fieldName.equals(IDocServerConstants.TABNAM)) {
				String value = IDocServerConstants.EDI_DS40;
				sb.append(this.fillWithBlanks(field, value));
				continue;
			}
			
			/* append place holder for DOCNUM */
			if(fieldName.equals(IDocServerConstants.DOCNUM)) {
				sb.append("%s");
				continue;
			}
			
			/* append place holder for LOGDAT */
			if(fieldName.equals(IDocServerConstants.LOGDAT)) {
				sb.append("%s");
				continue;
			}
			
			/* append place holder for LOGTIM */
			if(fieldName.equals(IDocServerConstants.LOGTIM)) {
				sb.append("%s");
				continue;
			}
			
			/* append STATUS */
			if(fieldName.equals(IDocServerConstants.STATUS)) {
				String value = IDocServerConstants.STATUS_VALUE;
				sb.append(this.fillWithBlanks(field, value));
				continue;
			}
			
			/* append UNAME */
			if(fieldName.equals(IDocServerConstants.UNAME)) {
				String value = destination.getUser();
				sb.append(this.fillWithBlanks(field, value));
				continue;
			}
			
			/* append REPID */
			if(fieldName.equals(IDocServerConstants.REPID)) {
				String value = IDocServerConstants.DATASTAGE;
				sb.append(this.fillWithBlanks(field, value));
				continue;
			}
			
			/* we don't need this field - fill up with blanks */
			sb.append(this.fillWithBlanks(field, IDocServerConstants.EMPTYSTRING));
			
		}
		
		return sb.toString();
	}
	
}
