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
// Module Name : com.ibm.is.sappack.dsstages.idocload.jcconnector
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.dsstages.idocload.jcconnector;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Pattern;

import com.ascential.e2.common.CC_Exception;
import com.ascential.e2.connector.CC_DataSetConsumer;
import com.ascential.e2.daapi.metadata.CC_LocatorInfo;
import com.ascential.e2.daapi.metadata.CC_LocatorTable;
import com.ascential.e2.propertyset.CC_ErrorList;
import com.ascential.e2.propertyset.CC_PropertySet;
import com.ibm.is.sappack.dsstages.common.CCFResource;
import com.ibm.is.sappack.dsstages.common.Constants;
import com.ibm.is.sappack.dsstages.common.IDocType;
import com.ibm.is.sappack.dsstages.common.IDocTypeConfiguration;
import com.ibm.is.sappack.dsstages.common.RuntimeConfiguration;
import com.ibm.is.sappack.dsstages.common.StageLogger;
import com.ibm.is.sappack.dsstages.common.ccf.CCFUtils;
import com.ibm.is.sappack.dsstages.common.ccf.IDocConnection;
import com.ibm.is.sappack.dsstages.common.ccf.IDocStageCCAdapter;
import com.ibm.is.sappack.dsstages.idoc.IDocFieldAligner;
import com.ibm.is.sappack.dsstages.idoc.IDocRuntimeException;
import com.ibm.is.sappack.dsstages.idocload.IDocLoadFactory;
import com.ibm.is.sappack.dsstages.idocload.IDocSender;
import com.ibm.is.sappack.dsstages.idocload.SegmentCollector;
import com.sap.conn.jco.JCoException;

public class IDocLoadAdapter extends IDocStageCCAdapter {

	static String copyright() {
		return com.ibm.is.sappack.dsstages.idocload.jcconnector.Copyright.IBM_COPYRIGHT_SHORT;
	}

	// constants
	private static final String CLASSNAME = IDocLoadAdapter.class.getName();

	private static final String EMPTY = ""; //$NON-NLS-1$
	private static final String UNDERSCORE = "_"; //$NON-NLS-1$
	private static final String BEGIN_PATTERN = ".*"; //$NON-NLS-1$
	private static final String MID_PATTERN = "[0-9]+\\"; //$NON-NLS-1$
	private static final String CLOSE_PATTERN = "$"; //$NON-NLS-1$

	SegmentCollector segmentCollector;
	IDocLoadDataSetConsumer[] consumers;

	public IDocLoadAdapter(CC_PropertySet propSet, CC_ErrorList errList) {
		super(propSet, errList);
		final String METHODNAME = "<init>"; //$NON-NLS-1$
		this.logger = StageLogger.getLogger();
		this.logger.entering(CLASSNAME, METHODNAME);
		try {
			CCFUtils.logPropertySet(m_propSet);
			String inputLinks = getCCFCommonProperties().get(Constants.CCF_COMMON_PROPERTY_PATH_INPUT_LINKS);
			this.logger.log(Level.FINEST, "Number of input links: {0}", inputLinks); //$NON-NLS-1$
			int numberOfInputLinks = Integer.parseInt(inputLinks);
			this.consumers = new IDocLoadDataSetConsumer[numberOfInputLinks];
		}
		catch (Throwable t) {
			CCFUtils.handleException(t);
		}
		this.logger.exiting(CLASSNAME, METHODNAME);
	}

	@Override
	public CC_DataSetConsumer getDataSetConsumer(int index, CC_PropertySet propSet, CC_ErrorList errList) {
		final String METHODNAME = "getDataSetConsumer"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);
		IDocConnection connection = (IDocConnection) this.getConnection();
		IDocType idocType = connection.getIDocType();
		if (idocType == null) {
			// should never happen
			this.logger.log(Level.SEVERE, "CC_IDOC_IncorrectProgramLogic", new NullPointerException()); //$NON-NLS-1$
			return null;
		}
		if (this.segmentCollector == null) {
			this.segmentCollector = IDocLoadFactory.createSegmentCollector(this.getDSEnvironment(), connection.getDSSAPConnection(), idocType);
		}

		IDocFieldAligner fieldAligner = new IDocFieldAligner(connection.getJCODestination());
		
		this.logger.log(Level.FINE,
		                "Creating Data set consumer at index {0}, property set: {1}", 
		                new Object[] { Integer.toString(index), propSet }); //$NON-NLS-1$
		IDocLoadDataSetConsumer result = new IDocLoadDataSetConsumer(idocType, propSet, errList, this.segmentCollector, fieldAligner);
		this.consumers[index] = result;
		this.logger.exiting(CLASSNAME, METHODNAME);
		return result;
	}

	protected boolean collectSegments() {
		final String METHODNAME = "collectSegments"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);
		this.logger.log(Level.INFO, "CC_IDOC_StartingSegmentCollection"); //$NON-NLS-1$
		boolean segmentCollectionProblems = false;
		boolean moreData = true;
		while (moreData) {
			moreData = false;
			// simple round robin segment collection,
			for (IDocLoadDataSetConsumer consumer : consumers) {
				if (this.logger.isLoggable(Level.FINEST)) {
					this.logger.log(Level.FINEST,
					      "Processing row on link for segment type: ''{0}''", consumer.getSegmentName()); //$NON-NLS-1$
				}
				// if end of data was not received in some previous call
				if (!consumer.getEndOfDataReceived()) {
					// TODO check consumeItem on CC_CONSUMED or CC_NOTCONSUMED
					// TODO determine if CC_NOTCONSUMED can also mean "no data currently available"
					int consumeItem = consumer.consumeOneTopLevelDataItem();
					if (this.logger.isLoggable(Level.FINEST)) {
						this.logger.log(Level.FINEST, "Items consumed: {0}", Integer.toString(consumeItem)); //$NON-NLS-1$
					}
					if (!consumer.getEndOfDataReceived()) {
						moreData = true;
					}
				}
				
				segmentCollectionProblems = segmentCollectionProblems || consumer.segmentCollector.isValidationFailed();
			}
			if (this.logger.isLoggable(Level.FINEST)) {
				this.logger.log(Level.FINEST, "More segment data available: {0}", Boolean.toString(moreData)); //$NON-NLS-1$
			}
		}
		this.logger.log(Level.INFO, "CC_IDOC_AllSegmentDataCollected");   //$NON-NLS-1$

		this.logger.exiting(CLASSNAME, METHODNAME);
		
		return segmentCollectionProblems;
	}

	protected void sendIDocs() throws JCoException {
		final String METHODNAME = "sendIDocs"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);

		IDocSender idocSender = IDocLoadFactory.createIDocSender();
		IDocConnection connection = (IDocConnection) this.getConnection();
		idocSender.initialize(connection, this.getStageProperties(), this.getDSEnvironment());

		idocSender.sendIDocs(this.segmentCollector);

		this.logger.exiting(CLASSNAME, METHODNAME);
	}

	@Override
	public void preRun() {
		final String METHODNAME = "preRun"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);

		Map<String, String> stageProps = this.getStageProperties();
		String idocType = stageProps.get(Constants.IDOCSTAGE_STAGE_PROP_IDOCTYP);
		IDocConnection conn = (IDocConnection) this.getConnection();
		String idocFilePath = ""; //$NON-NLS-1$
		boolean found = false;

		for (IDocTypeConfiguration config : conn.getDSSAPConnection().getIDocTypeConfigurations()) {
			if (config.getIDocTypeName().equals(idocType)) {
				found = true;
				idocFilePath = config.getLoadConfiguration().getFileBasedLoadDirectory();
				break;
			}
		}

		if (!found) {
			this.logger.log(Level.SEVERE, "CC_IDOC_TypeNotConfigured", idocType);   //$NON-NLS-1$
			return;
		}

		RuntimeConfiguration rtConfig = RuntimeConfiguration.getRuntimeConfiguration();

		if (rtConfig.getDeleteAllIDocFiles()) {
			deleteIDocFiles(idocFilePath);
		}
		else {
			String msgId = "CC_IDOC_LOAD_FilesNamingConflict";     //$NON-NLS-1$
			this.logger.log(Level.SEVERE, msgId, idocFilePath);
			throw new IDocRuntimeException(CCFResource.getCCFMessage(msgId, idocFilePath));
		}

		this.logger.exiting(CLASSNAME, METHODNAME);
	}

	@Override
	public void run() {
		final String METHODNAME = "run"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);
		
		boolean hasSegmentCollectionProblems = false;

		try {
			int currentNodeNum = this.getDSEnvironment().getCurrentNodeNumber();
			int overallNodeNum = this.getDSEnvironment().getOverallNodeNumber();
			this.logger.log(Level.INFO, "CC_IDOC_NodeNumberMessage",        //$NON-NLS-1$
			                new Object[] { currentNodeNum, overallNodeNum }); 

			IDocConnection conn = (IDocConnection) this.getConnection();
			String idocType = conn.getIDocType().getIDocTypeName();
			this.logger.log(Level.FINE, "IDoc type name: {0}", idocType); //$NON-NLS-1$
			boolean found = false;
			for (IDocTypeConfiguration config : conn.getDSSAPConnection().getIDocTypeConfigurations()) {
				if (config.getIDocTypeName().equals(idocType)) {
					found = true;
					break;
				}
			}
			if (!found) {
				this.logger.log(Level.SEVERE, "CC_IDOC_TypeNotConfigured", idocType);   //$NON-NLS-1$
				return;
			}

			hasSegmentCollectionProblems = collectSegments();

			if (RuntimeConfiguration.getRuntimeConfiguration().getIDocLoadSimulatedMode()) {
				this.logger.log(Level.INFO, "CC_IDOC_LOAD_SimulationModeOn");   //$NON-NLS-1$
			}
			else {
				sendIDocs();
			}
			
			// if the segment collection for the given IDocs had some problems
			// (e.g. orphaned segments, IDoc has no segments at all etc.)
			// this might be caused by an inappropriate partitioning type configuration
			// for the IDoc load input links
			// (e.g. on a multi-node DataStage installation the execution mode 'parallel'
			// is set as default while the default partioning type for a link is set to
			// 'round robin' which will lead to segment collection problems unless it is
			// set to something like 'hash' etc.)
			if (hasSegmentCollectionProblems) {
				this.logger.log(Level.WARNING, "CC_IDOC_IncorrectPartitioningType");  //$NON-NLS-1$
			}
		}
		catch (JCoException e) {
			this.logger.log(Level.SEVERE, "CC_IDOC_UnexpectedSAPException", e); //$NON-NLS-1$
			CCFUtils.throwCC_Exception(e);
		}
		catch (CC_Exception e) {
			this.logger.log(Level.SEVERE, "CC_IDOC_LOAD_UnexpectedException", e); //$NON-NLS-1$
			throw e;
		}
		catch (Exception e) {
			this.logger.log(Level.SEVERE, "CC_IDOC_LOAD_UnexpectedException", e); //$NON-NLS-1$
			CCFUtils.throwCC_Exception(e);
		}
		this.logger.exiting(CLASSNAME, METHODNAME);
	}

	@Override
	protected String[] getRequiredStageProperties() {
		return new String[] {
				Constants.IDOCSTAGE_STAGE_PROP_IDOCTYP
				};
	}

	/**
	 * This function deletes all IDoc load files from previous IDoc load job runs from the given directory.
	 * 
	 * @param idocFilePath
	 *           - the path where the IDoc load files are stored
	 */
	protected void deleteIDocFiles(String idocFilePath) {
		final String METHODNAME = "deleteIDocFiles(String idocFilePath)"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);

		IDocConnection conn = (IDocConnection) this.getConnection();
		String sapSysName = conn.getDSSAPConnection().getSapSystem().getName();
		String invocationID = getDSEnvironment().getInvocationID();

		StringBuffer filePattern = new StringBuffer();

		filePattern.append(BEGIN_PATTERN);
		filePattern.append(sapSysName);
		filePattern.append(UNDERSCORE);

		// we check for the invocation ID to find out if we have a multi instance job
		// if this is the case, IDoc files do have a different naming schema
		// which we need to take into account
		if (!invocationID.equalsIgnoreCase(EMPTY)) {
			filePattern.append(invocationID);
		}
		else {
			filePattern.append(this.getDSEnvironment().getJobName());
		}

		filePattern.append(UNDERSCORE);
		filePattern.append(MID_PATTERN);
		filePattern.append(Constants.IDOC_FILE_SUFFIX);
		filePattern.append(CLOSE_PATTERN);

		// retrieve a list of all IDoc files in the given directory
		// where the filenames match the specific regular expression pattern
		IDocFileFilter filter = new IDocFileFilter(filePattern.toString().trim());
		File idocFileDir = new File(idocFilePath);
		String[] allFilesInDir = idocFileDir.list(filter);

		// go through the list of files
		if (allFilesInDir != null) {
			if (allFilesInDir.length != 0) {
				this.logger.log(Level.WARNING, "CC_IDOC_LOAD_FilesToBeDeleted", idocFilePath);  //$NON-NLS-1$
			}

			for (int i = 0; i < allFilesInDir.length; i++) {
				StringBuffer sb = new StringBuffer();
				sb.append(idocFilePath);
				sb.append(File.separator);
				sb.append(allFilesInDir[i]);
				File idocFile = new File(sb.toString());

				if (!idocFile.delete()) {
					this.logger.log(Level.FINEST, "Existing IDoc file could not be deleted: {0}", //$NON-NLS-1$
					      new Object[] { sb.toString() });
				}
			}
		}

		this.logger.exiting(CLASSNAME, METHODNAME);
	}

	/**
	 * Static IDocFileFilter class which implements a simple regular expression based FilenameFilter for
	 * IDoc file filtering.
	 */
	static class IDocFileFilter implements FilenameFilter {

		// members
		private Pattern filePattern;

		/*
		 * Constructor
		 */
		public IDocFileFilter(String pattern) {
			filePattern = Pattern.compile(pattern);
		}

		@Override
		public boolean accept(File dir, String name) {
			return filePattern.matcher(name).matches();
		}
	}
	
    /**
     * This is for reporting operational metadata (OMD).
     */
    @Override
    public void getLocatorInfo(CC_LocatorInfo locInfo) {
        final String METHODNAME = "getLocatorInfo"; //$NON-NLS-1$
        this.logger.entering(CLASSNAME, METHODNAME);

        locInfo.setDbProduct(Constants.OMD_PRODUCT_NAME);
        
        IDocConnection connection = (IDocConnection) this.getConnection();
        IDocType idocType = connection.getIDocType();

        CC_LocatorTable locTable = CC_LocatorTable.createInstance();
        locTable.setOwner(Constants.OMD_DB_OWNER);
        locTable.setTableName(idocType.getIDocTypeName());
        
        // Set host name and set SAP system number as DB name
        if (connection.getDSSAPConnection().getSapSystem().isMessageServerSystem()) {
            locInfo.setHostName(connection.getDSSAPConnection().getSapSystem().getMsgServer());
            locTable.setDbName(connection.getDSSAPConnection().getSapSystem().getSystemId());
        } else {
            locInfo.setHostName(connection.getDSSAPConnection().getSapSystem().getAppServer());
            locTable.setDbName(connection.getDSSAPConnection().getSapSystem().getSystemNumber());
        }
        
        locInfo.setTableInfo(new CC_LocatorTable[] {locTable});
        
        super.getLocatorInfo(locInfo);
        this.logger.exiting(CLASSNAME, METHODNAME);
    }
}
