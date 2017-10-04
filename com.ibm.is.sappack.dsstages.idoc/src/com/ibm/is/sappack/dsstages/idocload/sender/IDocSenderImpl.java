//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2009, 2013                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.is.sappack.dsstages.idocload.sender
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.dsstages.idocload.sender;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.is.sappack.dsstages.common.CCFResource;
import com.ibm.is.sappack.dsstages.common.Constants;
import com.ibm.is.sappack.dsstages.common.DSEnvironment;
import com.ibm.is.sappack.dsstages.common.IDocLoadConfiguration;
import com.ibm.is.sappack.dsstages.common.IDocTypeConfiguration;
import com.ibm.is.sappack.dsstages.common.RuntimeConfiguration;
import com.ibm.is.sappack.dsstages.common.StageLogger;
import com.ibm.is.sappack.dsstages.common.ccf.IDocConnection;
import com.ibm.is.sappack.dsstages.idoc.EncodingDetector;
import com.ibm.is.sappack.dsstages.idoc.IDocRuntimeException;
import com.ibm.is.sappack.dsstages.idocload.ControlRecordData;
import com.ibm.is.sappack.dsstages.idocload.IDocNode;
import com.ibm.is.sappack.dsstages.idocload.IDocSender;
import com.ibm.is.sappack.dsstages.idocload.IDocTree;
import com.ibm.is.sappack.dsstages.idocload.IDocTreeTraversal;
import com.ibm.is.sappack.dsstages.idocload.SegmentCollector;
import com.ibm.is.sappack.dsstages.idocload.util.IDocFileEntry;
import com.ibm.is.sappack.dsstages.idocload.util.IDocFileWriter;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoField;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoTable;

/**
 * OnlineIdocSender
 * 
 * Implementation of the IDocServer for online processing.
 * 
 * Parameterizes and calls SAP function module IDOC_INBOUND_ASYNCHRONOUS to send IDocs to SAP
 * 
 */
public class IDocSenderImpl implements IDocSender {

	/* Logger */
	private Logger logger;

	/* class name for logging purposes */
	static final String CLASSNAME = IDocSenderImpl.class.getName();

	/* IDoc Load Configuration for a specific IDocType */
	private IDocLoadConfiguration idocLoadConfig;

	/* SAP JCO destination */
	private JCoDestination destination = null;
	
	/* Map that stores the IDoc numbers that are used in a transaction */
	private Map<Integer, String> idocNumbers = null;

	/*
	 * idocPerTrans defines the number of IDocs that are transfered in one IDOC_INBOUND_ASYNCHRONOUS call
	 */
	private int idocsPerTrans = 100;

	/* IDoc Type name */
	private String idocTypeName = ""; //$NON-NLS-1$

	/* Basic Type name */
	private String basicTypeName = ""; //$NON-NLS-1$

	/* message type - read from stage properties */
	private String messageType = ""; //$NON-NLS-1$

	/* RFC function IDOC_INBOUND_ASYNCHRONOUS used to send IDocs */
	private static final String IDOC_INBOUND_ASYNCHRONOUS = "IDOC_INBOUND_ASYNCHRONOUS"; //$NON-NLS-1$
	private JCoFunction idocInboundAsynchronous = null;

	/* table parameters for IDOC_INBOUND_ASYNCHRONOUS */
	private static final String IDOC_CONTROL_REC_40 = "IDOC_CONTROL_REC_40"; // control record //$NON-NLS-1$
	private static final String IDOC_DATA_REC_40 = "IDOC_DATA_REC_40"; // data record //$NON-NLS-1$

	private static final String EMPTY_STRING = ""; //$NON-NLS-1$

	/* control record table IDOC_CONTROL_REC_40 */
	private JCoTable controlRecordTable;
	/* data records table IDOC_DATA_REC_40 */
	private JCoTable dataRecordTable;

	private IDocFileEntry ife;
	
	// file-based mode indicator
	private boolean isFileBasedMode = false;
	
	/* Platform encoding of the target SAP system for file based IDoc load */
	private String encoding = "";

	private String sapConnectionName = ""; //$NON-NLS-1$
	private int currentNodeNumber;
	private String jobInvocationID = ""; //$NON-NLS-1$

	// runtime configuration (for environment variables)
	private RuntimeConfiguration rtConfig;

	/* LS logical system */
	private final static String LOGICAL_SYSTEM = "LS"; //$NON-NLS-1$

	private static final String TABNAM = "TABNAM"; //$NON-NLS-1$
	private static final String TABNAM_VALUE = "EDI_DC40"; //$NON-NLS-1$
	private static final String TABNAM_VALUE_UNICODE = "EDI_DC40_U"; //$NON-NLS-1$

	/* IDoc Number DOCNUM used to link entries in IDOC_DATA_REC_40 and IDOC_CONTROL_REC_40 */
	private static final String DOCNUM = "DOCNUM"; //$NON-NLS-1$

	/* data records table IDOC_DATA_REC_40 field names */
	private static final String SEGNAM = "SEGNAM"; //$NON-NLS-1$
	private static final String SEGNUM = "SEGNUM"; //$NON-NLS-1$
	private static final String PSGNUM = "PSGNUM"; //$NON-NLS-1$
	private static final String HLEVEL = "HLEVEL"; //$NON-NLS-1$
	private static final String SDATA = "SDATA"; //$NON-NLS-1$

	/* control records table IDOC_CONTROL_REC_40 field names */
	private static final String IDOCTYP = "IDOCTYP"; //$NON-NLS-1$
	private static final String CIMTYP = "CIMTYP"; //$NON-NLS-1$
	private static final String SNDPRN = "SNDPRN"; //$NON-NLS-1$
	private static final String RCVPRN = "RCVPRN"; //$NON-NLS-1$
	private static final String MESTYP = "MESTYP"; //$NON-NLS-1$
	private static final String SNDPRT = "SNDPRT"; //$NON-NLS-1$
	private static final String RCVPRT = "RCVPRT"; //$NON-NLS-1$
	private static final String DSR3_GENERATE_IDOC_NUM = "DSR3_GENERATE_IDOC_NUM";

	static String copyright() {
		return com.ibm.is.sappack.dsstages.idocload.sender.Copyright.IBM_COPYRIGHT_SHORT;
	}

	/**
	 * IDocSenderImpl
	 */
	public IDocSenderImpl() {
		/* initialize logger */
		this.logger = StageLogger.getLogger();
	}

	@Override
	/*
	 * * initialize
	 * 
	 * Initializes the IDocSenderImpl:
	 * 
	 * - stores the IDoc type - set the number of IDocs per transaction (package size) - initializes the JCo destination
	 * - retrieves remote function module meta data from SAP - reads the IDoc load configuration for the involved IDoc
	 * type
	 * 
	 * @param connection
	 * 
	 * @throws JCoException
	 */
	public void initialize(IDocConnection connection, Map<String, String> stageProperties, DSEnvironment dsEnvironment)
	      throws JCoException {

		final String METHODNAME = "initialize(DSSAPConnection connection)"; //$NON-NLS-1$
		logger.entering(CLASSNAME, METHODNAME);

		// initialize runtime configuration
		this.rtConfig = RuntimeConfiguration.getRuntimeConfiguration();

		/* initialize SAP JCo connection */
		// this.initJCoConnection(connection, stageProperties);
		this.destination = connection.getJCODestination();
		
		/* determine the platform encoding of the SAP system */
		EncodingDetector sapEncodingDetector = new EncodingDetector(this.destination);
		this.encoding = sapEncodingDetector.getEncoding();
		
		this.sapConnectionName = connection.getDSSAPConnection().getSapSystem().getName();

		/* read IDoc load configuration and release version of this idocType */
		this.idocTypeName = stageProperties.get(Constants.IDOCSTAGE_STAGE_PROP_IDOCTYP);
		this.basicTypeName = stageProperties.get(Constants.IDOCSTAGE_STAGE_PROP_BASTYP);
		this.idocLoadConfig = this.getIDocLoadConfiguration(connection.getDSSAPConnection().getIDocTypeConfigurations());

		/* determine the message type */
		this.messageType = stageProperties.get(Constants.IDOCSTAGE_STAGE_PROP_MESTYP);
		logger.log(Level.FINER, "Message type {0}", this.messageType); //$NON-NLS-1$

		/* initialize number of IDocs per transaction - package size */
		this.initIDocsPerTransaction(stageProperties);

		/* get repository function IDOC_INBOUND_ASYNCHRONOUS */
		logger.log(Level.FINER, "Retrieving metadata for remote function module {0}", IDOC_INBOUND_ASYNCHRONOUS); //$NON-NLS-1$
		this.idocInboundAsynchronous = destination.getRepository().getFunction(IDOC_INBOUND_ASYNCHRONOUS);

		/* check if IDOC_INBOUND_ASYNCHRONOUS exists on SAP system */
		if (this.idocInboundAsynchronous == null) {
			String msgId      = "CC_IDOC_RFMDoesNotExist"; //$NON-NLS-1$
			Object paramArr[] = new Object[] { IDOC_INBOUND_ASYNCHRONOUS, this.destination.getApplicationServerHost()} ;
			logger.log(Level.SEVERE, msgId, paramArr);
			throw new IDocRuntimeException(CCFResource.getCCFMessage(msgId, paramArr));
		}

		/* table parameters of IDOC_INBOUND_ASYNCHRONOUS */
		JCoParameterList tableParameters = this.idocInboundAsynchronous.getTableParameterList();
		if (tableParameters == null) {
			String msgId = "CC_IDOC_TableParamsListIsNull";  // //$NON-NLS-1$
			logger.log(Level.SEVERE, msgId);
			throw new IDocRuntimeException(CCFResource.getCCFMessage(msgId));
		}

		/* control record table */
		this.controlRecordTable = tableParameters.getTable(IDOC_CONTROL_REC_40);

		/* data records table */
		this.dataRecordTable = tableParameters.getTable(IDOC_DATA_REC_40);

		// determine regular or file-based mode
		int mode = Integer.valueOf(stageProperties.get(Constants.IDOCSTAGE_STAGE_PROP_USEFILEBASEDPROCESSING));
		this.isFileBasedMode = (mode != 0);

		// retrieve current node number
		this.currentNodeNumber = dsEnvironment.getCurrentNodeNumber();

		// retrieve job invocation ID
		// if the job invocation ID has been left empty by the user we simply assign a default value
		// ATTENTION: an empty invocation ID for a multi-instance job must be avoided,
		// assigning a default value does not solve the problem of multiple IDoc files
		// being generated with the same filename
		this.jobInvocationID = dsEnvironment.getInvocationID();
		if (this.jobInvocationID.equals(EMPTY_STRING) && this.isFileBasedMode) {
			this.jobInvocationID = dsEnvironment.getJobName();
		}

		logger.exiting(CLASSNAME, METHODNAME);
	}

	/**
	 * initJCoConnection
	 * 
	 * initialize the SAP JCo connection based on the given DSSAPConnection.
	 * 
	 * Check if we have to override the SAP connection default log on details with the values provided in the stage
	 * properties
	 * 
	 * 
	 * @param connection
	 * @param stageProperties
	 * @throws JCoException
	 */
	// private void initJCoConnectionDead(DSSAPConnection connection, Map<String, String> stageProperties) throws
	// JCoException {
	//
	//		final String METHODNAME = "initJCoConnection(DSSAPConnection connection, Map<String, String> stageProperties)"; //$NON-NLS-1$
	// logger.entering(CLASSNAME, METHODNAME);
	//
	// /* check overwritten stage properties */
	// if ("0".equals(stageProperties.get(Constants.IDOCSTAGE_STAGE_PROP_DEFSAPLOGON))) {
	//
	//			logger.log(Level.FINEST, "Overriding SAP connection logon defaults"); //$NON-NLS-1$
	//
	// /* update SAP log on details */
	// String username = stageProperties.get(Constants.IDOCSTAGE_STAGE_PROP_SAPUSERID);
	// if (username == null) {
	// username = "";
	// }
	// String password = stageProperties.get(Constants.IDOCSTAGE_STAGE_PROP_SAPPASSWORD);
	// if (password == null) {
	// password = "";
	// }
	// String clientID = stageProperties.get(Constants.IDOCSTAGE_STAGE_PROP_SAPCLIENTNUMBER);
	// if (clientID == null) {
	// clientID = "";
	// }
	//
	// SapSystem sapSystem = connection.getSapSystem();
	// sapSystem.setUsername(username);
	// sapSystem.setPassword(password);
	// try {
	// sapSystem.setClientId(Integer.parseInt(clientID));
	// } catch (Exception e) {
	// sapSystem.setClientId(0);
	// logger.severe(IDocLoadMessages.ClientIDNotNumeric);
	// }
	//
	// this.destination = sapSystem.getJCoDestination();
	// } else {
	// /* use connection defaults */
	//			logger.log(Level.FINEST, "Using SAP connection logon defaults"); //$NON-NLS-1$
	// this.destination = connection.getSapSystem().getJCoDestination();
	// }
	//
	// logger.exiting(CLASSNAME, METHODNAME);
	// }
	/**
	 * initIDocsPerTransaction
	 * 
	 * set the number of IDocs that are sent in each transaction
	 * 
	 * @param stageProperties
	 */
	private void initIDocsPerTransaction(Map<String, String> stageProperties) {

		final String METHODNAME = "initIDocsPerTransaction(Map<String, String> stageProperties)"; //$NON-NLS-1$
		logger.entering(CLASSNAME, METHODNAME);

		String idocsPerTransaction = stageProperties.get(Constants.IDOCSTAGE_STAGE_PROP_IDOCSPERTRANS);
		try {
			int numOfIDocsPerTrans = Integer.parseInt(idocsPerTransaction);
			if (numOfIDocsPerTrans > 0) {
				/* update number of IDocs that are send in each transaction */
				this.idocsPerTrans = numOfIDocsPerTrans;
			}
			else {
				logger
				      .log(
				            Level.FINE,
				            "Value for property {0} must be > 0. Falling back to default value {1}", new Object[] { Constants.IDOCSTAGE_STAGE_PROP_IDOCSPERTRANS, this.idocsPerTrans }); //$NON-NLS-1$
			}
		}
		catch (Exception e) {
			logger
			      .log(
			            Level.FINE,
			            "Invalid value {0} for property {1}. Falling back to default value {2}", new Object[] { idocsPerTransaction, Constants.IDOCSTAGE_STAGE_PROP_IDOCSPERTRANS, this.idocsPerTrans }); //$NON-NLS-1$
		}

		// in case of file-based IDoc load mode there might be an environment variable
		// which will override the number of IDocs per file
		if (this.isFileBasedMode && (rtConfig.getIDocFilesChunkSize() != -1)) {
			this.idocsPerTrans = rtConfig.getIDocFilesChunkSize();
		}

		logger.exiting(CLASSNAME, METHODNAME);
	}

	void setCRValueIfNotEmpty(JCoTable crTable, String key, String defaultValue, ControlRecordData crData) {
		String value = defaultValue;
		if (crData != null) {
			Map<String, String> crDataMap = crData.getCRData(); 
			if (crDataMap != null) {
				String crVal = crDataMap.get(key);
				if (crVal != null) {
					if (!crVal.trim().isEmpty()) {
						if (this.logger.isLoggable(Level.FINEST)) {
							this.logger.log(Level.FINEST, "Overwriting control record key {0} with {1}", new Object[]{key, crVal});
						}
						value = crVal;	
					}
				}
			}
		}
		crTable.setValue(key, value);
	}
	
	/**
	 * storeControlRecordInTable
	 * 
	 * create an entry in table IDOC_CONTROL_REC_40 for the specified idocTree. A control record is generated if the
	 * idocTree does not have a control record included
	 * 
	 * @param idocTree
	 */
	private void storeControlRecordInTable(IDocTree idocTree) {

		final String METHODNAME = "storeControlRecordInTable(IDocTree idocTree, IDocType idocType)"; //$NON-NLS-1$
		logger.entering(CLASSNAME, METHODNAME);

		/* append new row in control records table */
		this.controlRecordTable.appendRow();

		String idocNumber = idocTree.getIDocNumber();

		/* check if the IDoc already has a control record included */
		ControlRecordData crData = idocTree.getControlRecordData();
		if (crData != null) {
			/* create control record table entry based on provided control record data */
			logger.log(Level.FINER, "Using given control record for idoc {0} of idoc type {1}", new Object[] { idocNumber,  //$NON-NLS-1$
			      this.idocTypeName });
			Map<String, String> crDataMap = crData.getCRData();
			Set<Entry<String, String>> crDataSet = crDataMap.entrySet();
			Iterator<Entry<String, String>> crDataSetEntries = crDataSet.iterator();
			while (crDataSetEntries.hasNext()) {
				Entry<String, String> entry = crDataSetEntries.next();
				String crFieldName = entry.getKey();
				String crFieldValue = entry.getValue();
				logger.log(Level.FINEST, "{0}: {1}", new Object[] { crFieldName, crFieldValue }); //$NON-NLS-1$
				controlRecordTable.setValue(crFieldName, crFieldValue);
			}			
		} else {
			/* no control record provided - we need to generate one */
			logger.log(Level.FINER, "Generating control record for idoc {0} of idoc type {1}", new Object[] { idocNumber, //$NON-NLS-1$
			      this.idocTypeName });
		}
		
		// now set standard fields (or overwrite the empty ones)

		// TABNAM
		if (this.isFileBasedMode) {
			setCRValueIfNotEmpty(controlRecordTable, TABNAM, TABNAM_VALUE, crData);

			// in case of file-based IDoc load mode there might be an environment variable
			// which will override the default TABNAM value (unicode file port handling)
			if (rtConfig.getCreateUnicodeFilePortIDocFiles()) {
				setCRValueIfNotEmpty(controlRecordTable, TABNAM, TABNAM_VALUE_UNICODE, crData);
			}
		}
		/* DOCNUM */
		String docNum = generateDocNum(idocNumber);
		// overwrite DOCNUM when environment variable "DSR3_GENERATE_IDOC_NUM" is not set to 1
		String docNumToSet = docNum;
		if(overwriteDocNum())
			docNumToSet = idocNumber;
		
		if (logger.isLoggable(Level.FINEST)) {
			logger.log(Level.FINEST, "Setting IDoc number: {0}", new Object[]{docNumToSet}); //$NON-NLS-1$
		}
		
		controlRecordTable.setValue(DOCNUM, docNumToSet);

		/* IDOCTYP and CIMTYP */
		logger.log(Level.FINEST, "IDoc type: {0}", this.idocTypeName); //$NON-NLS-1$

		// we have to check whether the basicTypeName property is empty or not
		// (indicating whether this is actually an extension IDoc type or not)
		// if it is not set we do have a simple basic IDoc type,
		// if it is set we do have an extension IDoc type which needs to be handled
		// differently in the IDoc control record
		if (this.basicTypeName != null) {
			if (this.basicTypeName.equalsIgnoreCase(EMPTY_STRING)) {
				setCRValueIfNotEmpty(controlRecordTable, IDOCTYP, this.idocTypeName, crData);
			}
			else {
				logger.log(Level.FINEST, "Basic type: {0}", this.basicTypeName); //$NON-NLS-1$

				// though it may look incorrect this is actually correct
				// the idocTypeName property must be set as extension type field CIMTYP
				// while the basicTypeName property must be set as 'Basic IDoc type' field IDOCTYP
				setCRValueIfNotEmpty(controlRecordTable, IDOCTYP, this.basicTypeName, crData);
				setCRValueIfNotEmpty(controlRecordTable, CIMTYP, this.idocTypeName, crData);
			}
		}
		else {
			setCRValueIfNotEmpty(controlRecordTable, IDOCTYP, this.idocTypeName, crData);
		}

		/* SNDPRN */
		String connPartnerNumber = this.idocLoadConfig.getConnectionPartnerNumber();
		logger.log(Level.FINEST, "Sender partner number: {0}", connPartnerNumber); //$NON-NLS-1$
		setCRValueIfNotEmpty(controlRecordTable, SNDPRN, connPartnerNumber, crData);

		/* SNDPRT */
		setCRValueIfNotEmpty(controlRecordTable, SNDPRT, LOGICAL_SYSTEM, crData);

		/* RCVPRT */
		setCRValueIfNotEmpty(controlRecordTable, RCVPRT, LOGICAL_SYSTEM, crData);

		/* RCVPRN */
		String receiverPartnerNumber = this.idocLoadConfig.getSAPPartnerNumber();
		logger.log(Level.FINEST, "Reveicer partner number: {0}", receiverPartnerNumber); //$NON-NLS-1$
		setCRValueIfNotEmpty(controlRecordTable, RCVPRN, receiverPartnerNumber, crData);

		/* MESTYP */
		logger.log(Level.FINEST, "Message type: {0}", this.messageType); //$NON-NLS-1$
		setCRValueIfNotEmpty(controlRecordTable, MESTYP, this.messageType, crData);

		logger.exiting(CLASSNAME, METHODNAME);
	}

	/**
	 * storeIDocInDataRecordsTable
	 * 
	 * Create entries in the IDOC_DATA_REC_40 table that is needed for IDOC_INBOUND_ASYNCHRONOUS
	 * 
	 * The IDoc segment primary and foreign keys that are used within the given IDocTree are made up of Strings.
	 * 
	 * SAP expects the segment primary and foreign key relationships to be described with integer values. Therefore we
	 * have to convert the string keys into integer keys
	 * 
	 * Example:
	 * 
	 * RootSegment "Segment1" PrimaryKey "PK1" ParentKey "null"
	 * 
	 * ChildSegment "Segment2" PrimaryKey "PK2" ParentKey "PK1"
	 * 
	 * is converted into
	 * 
	 * RootSegment "Segment1" PrimaryKey 1 ParentKey 0
	 * 
	 * ChildSegment "Segment2" PrimaryKey 2 ParentKey 1
	 * 
	 * @param idoc
	 */
	private void storeIDocInDataRecordsTable(final IDocTree idoc) {

		final String METHODNAME = "storeIDocInDataRecordsTable(IDocTree tree)"; //$NON-NLS-1$
		logger.entering(CLASSNAME, METHODNAME);

		IDocTreeTraversal.traverseIDocTree(idoc, new IDocTreeTraversal.Visitor() {

			@Override
			public void visit(IDocNode node, int hierarchyLevel, int segmentNumber, int parentSegmentNumber) {

				/* create a new record in the data records table IDOC_DATA_REC_40 */
				dataRecordTable.appendRow();

				logger.log(Level.FINER, "Creating record in parameter table {0}", IDOC_DATA_REC_40); //$NON-NLS-1$

				/* DOCNUM */
				String admDocNum = idoc.getIDocNumber();
				String idocNumber = generateDocNum(admDocNum);
				
			//	logger.log(Level.FINEST, "IDoc number: {0}", idocNumber); //$NON-NLS-1$
				logger.log(Level.FINEST, "IDoc number from iDoc tree: {0}", admDocNum); 
				
				String docNumToSet = idocNumber;
				//overwrite DOCNUM when environment variable "DSR3_GENERATE_IDOC_NUM" is set to 1
				if(overwriteDocNum())
					docNumToSet = admDocNum;
								
				dataRecordTable.setValue(DOCNUM, docNumToSet);
				logger.log(Level.FINEST, "IDoc number: {0}", docNumToSet);
				if (logger.isLoggable(Level.FINEST)) {
					logger.log(Level.FINEST, "Value in dataRecordTable is: {0}", (dataRecordTable.getValue(DOCNUM)).toString()); //$NON-NLS-1$
				}

				/* SEGNAM */
				String segmentName = node.getSegmentData().getSegmentDefinitionName();
				logger.log(Level.FINEST, "Segment name: {0}", segmentName); //$NON-NLS-1$
				dataRecordTable.setValue(SEGNAM, segmentName);

				/* SDATA */
				final char[] segmentDataBuffer = node.getSegmentData().getSegmentDataBuffer();
				logger.log(Level.FINEST, "Segment data: {0}", new Object() { //$NON-NLS-1$
					      public String toString() {
						      return String.valueOf(segmentDataBuffer);
					      }
				      });
				dataRecordTable.setValue(SDATA, segmentDataBuffer);

				/* SEGNUM */
				logger.log(Level.FINEST, "Segment number: {0}", segmentNumber); //$NON-NLS-1$
				dataRecordTable.setValue(SEGNUM, segmentNumber);

				/* PSGNUM */
				logger.log(Level.FINEST, "Parent segment number: {0}", parentSegmentNumber); //$NON-NLS-1$
				dataRecordTable.setValue(PSGNUM, parentSegmentNumber);

				/* HLEVEL */
				logger.log(Level.FINEST, "Hierarchy level: {0}", hierarchyLevel); //$NON-NLS-1$
				dataRecordTable.setValue(HLEVEL, hierarchyLevel);
			}
		});
		logger.exiting(CLASSNAME, METHODNAME);
	}

	/**
	 * storeIDocsInFile
	 * 
	 * Serializes the segment data to a StringBuffer and formats it according to the format required by SAP.   
	 * 
	 * @param idoc IDoc that is to be written to a file
	 */
	private void storeIDocsInFile(final IDocTree idoc, IDocFileWriter ifw) {

		final String METHODNAME = "storeIDocsInFile(IDocTree tree)"; //$NON-NLS-1$
		logger.entering(CLASSNAME, METHODNAME);

		this.ife = new IDocFileEntry();

		ControlRecordData crData = idoc.getControlRecordData();

		/* Add the control record for each IDoc that is written to the file for IDoc offline mode.*/
		if (crData == null){
			/* Create a control record and format it according to the SAP definitions in IDOC_CONTROL_REC_40 */

			/* TABNAM */
			// in case of file-based IDoc load mode there might be an environment variable
			// which will override the default TABNAM value (unicode file port handling)
			if (rtConfig.getCreateUnicodeFilePortIDocFiles()) {
				ife.append(String.format("%-10s",TABNAM_VALUE_UNICODE).toCharArray()); //$NON-NLS-1$
			}else{
				ife.append(String.format("%-10s",TABNAM_VALUE).toCharArray()); //$NON-NLS-1$
			}

			/* MANDT */
			ife.append("   ".toCharArray()); //$NON-NLS-1$

			/* DOCNUM */
			String docNum = generateDocNum(idoc.getIDocNumber());
			String idocNumber = String.format("%-16s", docNum); //$NON-NLS-1$
			logger.log(Level.FINEST, "IDoc number: {0}", idocNumber); //$NON-NLS-1$
			ife.append(idocNumber.toCharArray());

			/* DOCREL, STATUS, DIRECT, OUTMOD, EXPRSS, TEST (length 10) */
			ife.append(String.format("%10s", EMPTY_STRING).toCharArray()); //$NON-NLS-1$

			logger.log(Level.FINEST, "IDoc type: {0}", this.idocTypeName); //$NON-NLS-1$

			// we have to check whether the basicTypeName property is empty or not
			// (indicating whether this is actually an extension IDoc type or not)
			// if it is not set we do have a simple basic IDoc type,
			// if it is set we do have an extension IDoc type which needs to be handled
			// differently in the IDoc control record
			String idoctyp = this.idocTypeName;
			String cimtyp = EMPTY_STRING; 

			if (this.basicTypeName != null) {
				if (!this.basicTypeName.equalsIgnoreCase(EMPTY_STRING)) {
					// though it may look incorrect this is actually correct
					// the idocTypeName property must be set as extension type field CIMTYP
					// while the basicTypeName property must be set as 'Basic IDoc type' field IDOCTYP
					logger.log(Level.FINEST, "Basic type: {0}", this.basicTypeName); //$NON-NLS-1$
					idoctyp = this.basicTypeName;
					cimtyp = this.idocTypeName;
				}
			}
			ife.append(String.format("%-30s", idoctyp).toCharArray()); //$NON-NLS-1$
			ife.append(String.format("%-30s", cimtyp).toCharArray()); //$NON-NLS-1$

			/* MESTYP */
			logger.log(Level.FINEST, "Message type: {0}", this.messageType); //$NON-NLS-1$
			ife.append(String.format("%-30s", this.messageType).toCharArray()); //$NON-NLS-1$

			/* MESCOD, MESFCT, STD, STDVRS, STDMES, SNDPOR  (length 59)*/
			ife.append(String.format("%-29s", EMPTY_STRING).toCharArray()); //$NON-NLS-1$

			/* SNDPRT */
			ife.append(String.format("%-2s", LOGICAL_SYSTEM).toCharArray()); //$NON-NLS-1$

			/* SNDPFC  (length 2)*/
			ife.append(String.format("%-2s", EMPTY_STRING).toCharArray()); //$NON-NLS-1$

			/* SNDPRN */
			String connPartnerNumber = this.idocLoadConfig.getConnectionPartnerNumber();
			logger.log(Level.FINEST, "Sender partner number: {0}", connPartnerNumber); //$NON-NLS-1$
			ife.append(String.format("%-10s", connPartnerNumber).toCharArray()); //$NON-NLS-1$

			/* SNDSAD, SNDLAD, RCVPOR   (length 101)*/
			ife.append(String.format("%-101s", EMPTY_STRING).toCharArray()); //$NON-NLS-1$

			/* RCVPRT */
			ife.append(String.format("%-2s", LOGICAL_SYSTEM).toCharArray()); //$NON-NLS-1$

			/* RCVPFC (length 2)*/
			ife.append(String.format("%-2s", EMPTY_STRING).toCharArray()); //$NON-NLS-1$

			/* RCVPRN */
			String receiverPartnerNumber = this.idocLoadConfig.getSAPPartnerNumber();
			logger.log(Level.FINEST, "Reveicer partner number: {0}", receiverPartnerNumber); //$NON-NLS-1$
			ife.append(String.format("%-10s", receiverPartnerNumber).toCharArray()); //$NON-NLS-1$

			/* RCVSAD, RCVLAD (length 91)*/
			ife.append(String.format("%-91s", EMPTY_STRING).toCharArray()); //$NON-NLS-1$

			/* CREDAT, CRETIM (length 14)*/
			ife.append(String.format("%014d", 0).toCharArray()); //$NON-NLS-1$


			/* ... (length 237)*/
			//ife.append(String.format("%-237s", EMPTY_STRING).toCharArray()); //$NON-NLS-1$

			// append a newline character after segment
			ife.newline();

			logger.log(Level.FINEST, "control record: {0}", ife.retrieve()); //$NON-NLS-1$

		}else {
			/* create control record table entry based on provided control record data */
			logger.log(Level.FINER, "Using given control record for idoc {0} of idoc type {1}", new Object[] { idoc.getIDocNumber(), //$NON-NLS-1$
					this.idocTypeName });

			this.controlRecordTable.clear();
			storeControlRecordInTable(idoc);
			// get the control records
			this.controlRecordTable.setRow(0);
			Iterator<JCoField> cfields = this.controlRecordTable.iterator();
			while (cfields.hasNext()) {
				ife.append(cfields.next().getCharArray());
			}
			// append a newline character after the control record
			ife.newline();
		}


		IDocTreeTraversal.traverseIDocTree(idoc, new IDocTreeTraversal.Visitor() {


			/* Retrieve the segment data and format it according to the SAP definitions in IDOC_DATA_REC_40 */
			@Override
			public void visit(IDocNode node, int hierarchyLevel, int segmentNumber, int parentSegmentNumber) {

				/* SEGNAM */
				String segmentName = String.format("%-30s", node.getSegmentData().getSegmentDefinitionName()); //$NON-NLS-1$
				logger.log(Level.FINEST, "Segment name: {0}", segmentName); //$NON-NLS-1$
				ife.append(segmentName.toCharArray());

				/* MANDT */
				String clientNumber = "   "; //$NON-NLS-1$
				logger.log(Level.FINEST, "Client Number: {0}", clientNumber); //$NON-NLS-1$
				ife.append(clientNumber.toCharArray());

				/* DOCNUM */
				String idocNumber = String.format("%-16s", idoc.getIDocNumber()); //$NON-NLS-1$
				logger.log(Level.FINEST, "IDoc number: {0}", idocNumber); //$NON-NLS-1$
				ife.append(idocNumber.toCharArray());

				/* SEGNUM */
				String formattedSegmentNumber = String.format("%06d", segmentNumber); //$NON-NLS-1$
				logger.log(Level.FINEST, "Segment number: {0}", formattedSegmentNumber); //$NON-NLS-1$
				ife.append(formattedSegmentNumber.toCharArray());

				/* PSGNUM */
				String formattedParentSegmentNumber = String.format("%06d", parentSegmentNumber); //$NON-NLS-1$
				logger.log(Level.FINEST, "Parent segment number: {0}", formattedParentSegmentNumber); //$NON-NLS-1$
				ife.append(formattedParentSegmentNumber.toCharArray());

				/* HLEVEL */
				String formattedHirarchyLevel = String.format("%-2s", hierarchyLevel); //$NON-NLS-1$
				logger.log(Level.FINEST, "Hierarchy level: {0}", formattedHirarchyLevel); //$NON-NLS-1$
				ife.append(formattedHirarchyLevel.toCharArray());

				/* SDATA */
				final char[] segmentDataBuffer = node.getSegmentData().getSegmentDataBuffer();
				logger.log(Level.FINEST, "Segment data: {0}", new Object() { //$NON-NLS-1$
					public String toString() {
						return String.valueOf(segmentDataBuffer);
					}
				});
				ife.append(segmentDataBuffer);

				// append a newline character after segment
				ife.newline();
			}
		});

		ifw.add(ife);

		logger.exiting(CLASSNAME, METHODNAME);
	}
	
	/**
	 * getIDocLoadConfiguration
	 * 
	 * reads the IDocLoadConfiguration and release version of an IDocType
	 * 
	 * @param idocTypeConfigurations
	 * @return IDocLoadConfiguration
	 */
	private IDocLoadConfiguration getIDocLoadConfiguration(List<IDocTypeConfiguration> idocTypeConfigurations) {

		final String METHODNAME = "getIDocLoadConfiguration()"; //$NON-NLS-1$
		logger.entering(CLASSNAME, METHODNAME);

		logger.log(Level.FINER, "Reading IDoc load configuration for IDoc type {0}", this.idocTypeName); //$NON-NLS-1$

		/* look for the right IDoc type configuration */
		for (IDocTypeConfiguration idocTypeConfig : idocTypeConfigurations) {
			if (idocTypeConfig.getIDocTypeName().equals(this.idocTypeName)) {

				/* OK get the IDoc load configuration */
				logger.log(Level.FINER, "Found IDoc load configuration for IDoc type {0}", this.idocTypeName); //$NON-NLS-1$
				logger.exiting(CLASSNAME, METHODNAME);
				return idocTypeConfig.getLoadConfiguration();
			}
		}

		/* if we come across this point we didn't find an IDoc type configuration for this IDoc type */
		logger.log(Level.SEVERE, "CC_IDOC_TypeNotConfigured", this.idocTypeName);   //$NON-NLS-1$
		return null;
	}

	@Override
	/*
	 * * sendIDocs
	 * 
	 * send all IDocs contained in the given SegmentCollector to SAP.
	 * 
	 * IDocs are sent in transactions with the number of IDocs specified in idocsPerTrans
	 * 
	 * @param segmentCollector
	 * 
	 * @throws JCoException
	 */
	public void sendIDocs(SegmentCollector segmentCollector) throws JCoException {

		final String METHODNAME = "sendIDocs(SegmentCollector segmentCollector)"; //$NON-NLS-1$
		logger.entering(CLASSNAME, METHODNAME);


		/* extract all IDocs we need to send from the segmentCollector */
		final Iterator<IDocTree> idocTrees = segmentCollector.getAllIDocTrees();
		

		/* total numbers of IDocs sent to SAP */
		int totalNumberOfIDocs = 0;
		/* numbers of IDocs in this transaction */
		int numberOfIDocsInTrans = 0;
		/* transaction number */
		int transactionNumber = 0;

		/* process each IDocTree (IDoc) */
		while (idocTrees.hasNext()) {

			/* update number of transaction counter */
			transactionNumber++;

			/* reset Map that stores the IDoc numbers of a transaction */
			this.idocNumbers = new HashMap<Integer, String>();
			
			// depending on the flag for regular or file-based mode we either call
			// the IDoc send RFM on SAP (IDOC_INBOUND_ASYNCHRONOUS) or write the IDocs
			// to a file on the filesystem
			if (this.isFileBasedMode) {

				
				IDocFileWriter ifw = null;
				if(this.encoding.equalsIgnoreCase(Constants.UTF16)) {
					/* On unicode SAP systems, the file needs to be written in UTF-8 encoding */
					ifw = new IDocFileWriter(Constants.UTF8);
				} else {
					/* On non-unicode SAP systems, the file needs to be written in SAP platform encoding */
					ifw = new IDocFileWriter(this.encoding);
				}
				
				
				ifw.initialize(this.idocLoadConfig.getFileBasedLoadDirectory(), transactionNumber, this.currentNodeNumber, this.idocTypeName,
				      this.sapConnectionName, this.jobInvocationID);

				while (numberOfIDocsInTrans < this.idocsPerTrans && idocTrees.hasNext()) {

					IDocTree idoc = idocTrees.next();
					logger.log(Level.FINER, "Creating file entry for IDoc {0}", idoc.getIDocNumber()); //$NON-NLS-1$
					numberOfIDocsInTrans++;
					storeIDocsInFile(idoc, ifw);
				}
				// do the actual writing to file
				if (!ifw.flush(numberOfIDocsInTrans)) {
					logger.log(Level.WARNING, "CC_IDOC_LOAD_FileWriteError", ifw.getFileName());  //$NON-NLS-1$
				}


			}
			else {
				/* clear data records table IDOC_DATA_REC_40 */
				this.dataRecordTable.clear();

				/* clear control records table IDOC_CONTROL_REC_40 */
				this.controlRecordTable.clear();

				/*
				 * process IDocs in transactions using the specified number of IDocs per transaction
				 */
				while (numberOfIDocsInTrans < this.idocsPerTrans && idocTrees.hasNext()) {

					IDocTree idoc = idocTrees.next();
					logger.log(Level.FINER, "Creating table entries for IDoc {0}", idoc.getIDocNumber()); //$NON-NLS-1$
					if (logger.isLoggable(Level.FINEST)) {
						logger.log(Level.FINEST, "OLIX: Sending IDoc Tree: " + idoc.toString() + "\n");
						final StringBuffer buf = new StringBuffer();
						
						IDocTreeTraversal.Visitor visitor = new IDocTreeTraversal.Visitor() {
							
							@Override
							public void visit(IDocNode node, int hierarchyLevel, int segmentNumber,
									int parentSegmentNumber) {
								for (int i=0; i<hierarchyLevel; i++) {
									buf.append(" ");
								}
								buf.append("SEGNUM: " + segmentNumber + ", pSGNUM: " + parentSegmentNumber + ", hier: " + hierarchyLevel + ", segment def: " + node.getSegmentData().getSegmentDefinitionName() + ", Data: " + new String(node.getSegmentData().getSegmentDataBuffer()) + "\n\n"); 								
							}
						};
						IDocTreeTraversal.traverseIDocTree(idoc, visitor);
						this.logger.log(Level.FINEST, "OLIX: Re-ordered IDoc Tree: " + buf.toString() + "\n");
					}

					/* store IDoc segments in table IDOC_DATA_REC_40 */
					this.storeIDocInDataRecordsTable(idoc);
					/* store control record in table IDOC_CONTROL_REC_40 */
					this.storeControlRecordInTable(idoc);
					/* update number of IDocs in current transaction */
					numberOfIDocsInTrans++;
				}

				/* send all IDocs of this transaction in a IDOC_INBOUND_ASYCHRONOUS call to SAP */
				if (!this.rtConfig.getMinimumMessage()){
					logger.log(Level.INFO, "CC_IDOC_LOAD_TransactionOpen",   //$NON-NLS-1$
					           new Object[] { numberOfIDocsInTrans, transactionNumber });
				}
				this.idocInboundAsynchronous.execute(this.destination);

				if (!this.rtConfig.getMinimumMessage()){
					logger.log(Level.INFO, "CC_IDOC_LOAD_TransactionClose",   //$NON-NLS-1$
					           new Object[] { numberOfIDocsInTrans, transactionNumber });
				}
			}

			/* update total number of IDocs sent */
			totalNumberOfIDocs = totalNumberOfIDocs + numberOfIDocsInTrans;
			/* reset IDocs in transaction counter */
			numberOfIDocsInTrans = 0;

		}

		// the summary message will be different depending on regular or file-based mode
		if (this.isFileBasedMode) {
			logger.log(Level.INFO, "CC_IDOC_LOAD_FileSummary",  //$NON-NLS-1$
			           new Object[] { totalNumberOfIDocs, transactionNumber, this.currentNodeNumber });
		}
		else {
			/* All IDocs have been sent to SAP */
			logger.log(Level.INFO, "CC_IDOC_LOAD_Summary",  //$NON-NLS-1$
			           new Object[] { totalNumberOfIDocs, transactionNumber });
		}

		logger.exiting(CLASSNAME, METHODNAME);
	}
	
	/**
	 * generateDocNum
	 * 
	 * generate the IDoc number for the DOCNUM field
	 * of IDOC_DATA_REC_40 and IDOC_CONTROL_REC_40. The
	 * IDoc number must be unique and not longer than
	 * 16 characters. 
	 * 
	 * @param admDocNum
	 * @return unique IDoc number with a max length of 16 characters
	 */
	private String generateDocNum(String admDocNum) {		
		final String METHODNAME = "generateDocNum(String admDocNum)"; //$NON-NLS-1$
		logger.entering(CLASSNAME, METHODNAME);
				
		/* get hashCode of the given admDocNum */
		int docNumInt = admDocNum.hashCode();

		String result = null;
		/* Loop until we found a unique docNum for this admDocNum */
		do {
			/* check if we already have used this docNumInt in this transaction */
			String existingAdmDocNum = (String) this.idocNumbers.get(docNumInt);

			/*
			 * if the generated docNumInt has not been used yet, or it has been used by
			 * the same IDoc (identified by the admDocNum), we can use the generated
			 * docNumInt as DOCNUM.
			 */
			if (existingAdmDocNum == null) {
				this.idocNumbers.put(docNumInt, admDocNum);
				result = Integer.toString(docNumInt);
				if (this.logger.isLoggable(Level.FINEST)) {
					this.logger.log(Level.FINEST, "New DOCNUM {0} created for ADM_DOCNUM {1}", new Object[]{result, admDocNum }); //$NON-NLS-1$
					this.logger.log(Level.FINEST, "New DOCNUM {0} will be used only if environment variable DSR3_GENERATE_IDOC_NUM is not set to 1", new Object[]{result}); //$NON-NLS-1$
				}
			} else {
				if (existingAdmDocNum.equals(admDocNum)) {
					result = Integer.toString(docNumInt);	
					if (this.logger.isLoggable(Level.FINEST)) {
						logger.log(Level.FINEST, "Found DOCNUM {0} is already is use for ADM_DOCNUM {1}", new Object[] {result, admDocNum}); //$NON-NLS-1$
						this.logger.log(Level.FINEST, "DOCNUM {0} will be used only if environment variable DSR3_GENERATE_IDOC_NUM is not set to 1", new Object[]{result}); //$NON-NLS-1$
					}
				} else {
					if (this.logger.isLoggable(Level.FINEST)) {
						logger.log(Level.FINEST, "DOCNUM {0} is already is use for other ADM_DOCNUM {1}", new Object[] {docNumInt, admDocNum}); //$NON-NLS-1$
						this.logger.log(Level.FINEST, "DOCNUM {0} will be used only if environment variable DSR3_GENERATE_IDOC_NUM is not set to 1", new Object[]{docNumInt}); //$NON-NLS-1$
					}
					docNumInt++;
				}
			}

		} while (result == null);

		logger.exiting(CLASSNAME, METHODNAME);
		return result;
	}
	
	private boolean overwriteDocNum(){
		boolean retVal = true;
		String iDocNumEnvVar=System.getenv(DSR3_GENERATE_IDOC_NUM);
		if (logger.isLoggable(Level.FINEST)) {
			logger.log(Level.FINEST, "Value of environment variable DSR3_GENERATE_IDOC_NUM is: {0}", iDocNumEnvVar );
		}
		if("1".equals(iDocNumEnvVar)) 
			retVal = false;
		
		return retVal;
	}

}
