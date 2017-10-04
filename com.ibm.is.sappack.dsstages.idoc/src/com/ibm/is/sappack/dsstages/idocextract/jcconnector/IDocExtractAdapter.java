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
// Module Name : com.ibm.is.sappack.dsstages.idocextract.jcconnector
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.dsstages.idocextract.jcconnector;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.logging.Level;

import com.ascential.e2.common.CC_Exception;
import com.ascential.e2.connector.CC_DataSetProducer;
import com.ascential.e2.daapi.metadata.CC_LocatorInfo;
import com.ascential.e2.daapi.metadata.CC_LocatorTable;
import com.ascential.e2.propertyset.CC_ErrorList;
import com.ascential.e2.propertyset.CC_PropertySet;
import com.ibm.is.sappack.dsstages.common.Constants;
import com.ibm.is.sappack.dsstages.common.IDocType;
import com.ibm.is.sappack.dsstages.common.IDocTypeConfiguration;
import com.ibm.is.sappack.dsstages.common.RuntimeConfiguration;
import com.ibm.is.sappack.dsstages.common.StageLogger;
import com.ibm.is.sappack.dsstages.common.ccf.CCFUtils;
import com.ibm.is.sappack.dsstages.common.ccf.IDocConnection;
import com.ibm.is.sappack.dsstages.common.ccf.IDocStageCCAdapter;
import com.ibm.is.sappack.dsstages.idoc.IDocFieldAligner;
import com.ibm.is.sappack.dsstages.idocextract.util.IDocBookmark;
import com.ibm.is.sappack.dsstages.idocextract.util.IDocBookmarkFileHandler;
import com.ibm.is.sappack.dsstages.idocextract.util.IDocFileHandler;

/**
 * Adapter for IDoc Extract stage
 */
public class IDocExtractAdapter extends IDocStageCCAdapter {

	static String copyright() {
		return com.ibm.is.sappack.dsstages.idocextract.jcconnector.Copyright.IBM_COPYRIGHT_SHORT;
	}

	static private String CLASSNAME = IDocExtractAdapter.class.getName();

	IDocExtractDataSetProducer[] producers;
	private IDocType idocType = null;
	
	public IDocExtractAdapter(CC_PropertySet propSet, CC_ErrorList errList) {
		super(propSet, errList);
		final String METHODNAME = "<init>"; //$NON-NLS-1$
		this.logger = StageLogger.getLogger();
		this.logger.entering(CLASSNAME, METHODNAME);
		try {
			CCFUtils.logPropertySet(m_propSet);
			String outputLinks = getCCFCommonProperties().get(Constants.CCF_COMMON_PROPERTY_PATH_OUTPUT_LINKS);
			this.logger.log(Level.FINE, "Number of output links: {0}", outputLinks); //$NON-NLS-1$
			int numberOfOutputLinks = Integer.parseInt(outputLinks);
			this.producers = new IDocExtractDataSetProducer[numberOfOutputLinks];
		}
		catch (Throwable t) {
			CCFUtils.handleException(t);
		}
		this.logger.exiting(CLASSNAME, METHODNAME);
	}

	@Override
	public CC_DataSetProducer getDataSetProducer(int index, CC_PropertySet propSet, CC_ErrorList errList) {
		final String METHODNAME = "getDataSetProducer"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);

		IDocConnection connection = (IDocConnection) this.getConnection();
		this.idocType = connection.getIDocType();
		if (idocType == null) {
			// should never happen
			this.logger.log(Level.SEVERE, "CC_IDOC_IncorrectProgramLogic"); //$NON-NLS-1$
			throw new NullPointerException();
		}
		IDocFieldAligner fieldAligner = new IDocFieldAligner(connection.getJCODestination());
		this.logger.log(Level.FINE,
		            "Creating Data set producer at index {0}, property set: {1}", new Object[] { Integer.toString(index), propSet }); //$NON-NLS-1$
		IDocExtractDataSetProducer result = new IDocExtractDataSetProducer(this.idocType, propSet, errList, fieldAligner);
		this.producers[index] = result;
		this.logger.exiting(CLASSNAME, METHODNAME);
		return result;
	}
	
	@Override
	protected String[] getRequiredStageProperties() {
		return new String[] {
				Constants.IDOCSTAGE_STAGE_PROP_IDOCTYP
				};
	}

	/**
	 * In preRun (conductor node only) the bookmark is prepared for the job runs. If it does not exist    
	 * it is created and initialized.
	 * Then the bookmark file is updated with the current timestamp to log the current job run. 
	 * 
	 * @see com.ibm.is.sappack.dsstages.common.ccf.IDocStageCCAdapter#preRun()
	 */
	@Override
	public void preRun() {
		final String METHODNAME = "preRun"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);
		

		// what this function does in detail:
		// the conductor node of the IDoc extract job will take care
		// of the most critical parts of IDoc bookmark file handling
		// which is writing to that file (for initial bookmark creation, update and reset)
		// 
		// the worker nodes (which will do the actual IDoc extract logic in their run() methods)
		// AFTER this preRun() method has been run only read the bookmark file but do not write to
		// it - this is to keep code complexity in terms of concurrent file access and locking etc.
		// low and to prevent unforseen problems that would arise from such things
		//
		// so at job startup the conductor node looks for an existing bookmark file, creates one
		// if it doesn't exist (and sets an initial bookmark entry) and then updates it with
		// the current timestamp
		// exception: if the environment variable DS_PX_RESET is set to 1, indicating that a job
		// reset was triggered by the user, the conductor node will reset the bookmark entry
		// in the bookmark file first before updating it with the current timestamp
		//
		// the worker nodes indicate an error while extracting IDocs by throwing a new CC_Exception
		// which will trigger CCF to immediately abort the job
		// as this will render the job 'unusable' (the user can't start it until he triggers
		// a job reset) this is the best way to enforce a reset of the bookmark file to make sure
		// that all IDocs get picked up again the next time the IDoc extract job is run
		Map<String, String> stageProps = this.getStageProperties();
		String idocType = stageProps.get(Constants.IDOCSTAGE_STAGE_PROP_IDOCTYP);
		IDocConnection conn = (IDocConnection) this.getConnection();
		String idocBookmarkFilePath = ""; //$NON-NLS-1$
		boolean found = false;

		for (IDocTypeConfiguration config : conn.getDSSAPConnection().getIDocTypeConfigurations()) {
			if (config.getIDocTypeName().equals(idocType)) {
				found = true;
				idocBookmarkFilePath = config.getExtractConfiguration().getExtractDirectory();
				break;
			}
		}
		if (!found) {
			this.logger.log(Level.SEVERE, "CC_IDOC_TypeNotConfigured", idocType);   //$NON-NLS-1$
			return;
		}

		IDocBookmark bookMark = new IDocBookmark();

		// setup the bookmark file
		// if it does not exist a new file is created and initialized. 
		boolean setupInfo = bookMark.setup(this.getDSEnvironment().getJobName(), idocType, idocBookmarkFilePath);
		
		if (!setupInfo) { 
			logger.log(Level.SEVERE, "CC_IDOC_EXTRACT_BookmarkFileSetupFailed", bookMark.getFileName() ); //$NON-NLS-1$
		}
		
		RuntimeConfiguration rtConfig = RuntimeConfiguration.getRuntimeConfiguration();
		
		// now the first thing we need to do is to check whether a reset must be done
		// this can be determined by the state of the environment variable DS_PX_RESET
		if (rtConfig.getPXReset()) {
			bookMark.reset();
		}

		// finally, update the bookmark to indicate that the worker nodes are about to
		// begin extracting idocs
		if (!bookMark.update()){
			logger.log(Level.SEVERE, "CC_IDOC_EXTRACT_BookmarkFileUpdateFailed", idocBookmarkFilePath); //$NON-NLS-1$
		}
	
		this.logger.exiting(CLASSNAME, METHODNAME);
	}

	/** 
	 * In the run method the bookmark file is read and the IDoc files that must be processed in 
	 * this job run according to the bookmark file are extracted. 
	 * For each identified IDoc file the DataSetProducers are for the respective output link(s) 
	 * are called to parse the segment data. 
	 * 
	 */
	@Override
	public void run() {
		final String METHODNAME = "run"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);

		try {
			int currentNodeNum = this.getDSEnvironment().getCurrentNodeNumber();
			int overallNodeNum = this.getDSEnvironment().getOverallNodeNumber();
			this.logger.log(Level.INFO, "CC_IDOC_NodeNumberMessage",        //$NON-NLS-1$
			                new Object[] { currentNodeNum, overallNodeNum }); 

			// Retrieve the directory where the bookmark file and the IDoc files are taken from
			IDocConnection conn = (IDocConnection) this.getConnection();
			String idocTypeName = conn.getIDocType().getIDocTypeName();
			this.logger.log(Level.FINE, "IDoc type name: {0}", idocTypeName); //$NON-NLS-1$

			boolean found = false;
			String idocBookmarkFilePath = ""; //$NON-NLS-1$
			for (IDocTypeConfiguration config : conn.getDSSAPConnection().getIDocTypeConfigurations()) {
				if (config.getIDocTypeName().equals(idocTypeName)) {
					idocBookmarkFilePath = config.getExtractConfiguration().getExtractDirectory();
					this.logger.log(Level.FINE, "IDoc bookmark file path: {0}", idocBookmarkFilePath); //$NON-NLS-1$
					found = true;
					break;
				}
			}
			if (!found) {
				this.logger.log(Level.SEVERE, "CC_IDOC_TypeNotConfigured", idocTypeName);   //$NON-NLS-1$
				return;
			}

			//Extract the IDoc files that must be processed according to the bookmark file
			IDocBookmarkFileHandler bookmarkFileHandler = new IDocBookmarkFileHandler(this.getDSEnvironment().getJobName(), 
					idocTypeName, idocBookmarkFilePath);
			Iterator<File> idocFilesForThisNode = bookmarkFileHandler.getIDocFilesForNode(currentNodeNum, overallNodeNum).iterator();
			while(idocFilesForThisNode.hasNext()) {
				File idocFile = idocFilesForThisNode.next();
				if (idocFile == null){
					// should never happen
					this.logger.log(Level.SEVERE, "CC_IDOC_IncorrectProgramLogic"); //$NON-NLS-1$
					return;
				}

				IDocFileHandler idocFileHandler = new IDocFileHandler(idocFile);
				// parse the IDoc file to extract the different segments
				if (!idocFileHandler.initialize()){
					this.logger.log(Level.SEVERE, "CC_IDOC_EXTRACT_IDocFileExtractSegmentData", idocFile.getName()); //$NON-NLS-1$
				}

				for (IDocExtractDataSetProducer producer : producers) {
					String segmentName = producer.getSegmentName();
					this.logger.log(Level.FINEST,
						      "Processing row on link for segment type: ''{0}''", segmentName); //$NON-NLS-1$

					List <String> segmentDataList = idocFileHandler.getSegmentData(segmentName);
					if (segmentDataList != null){
						ListIterator<String> segmentDataIterator = segmentDataList.listIterator();
						while (segmentDataIterator.hasNext()){
							//pass the segment data to the DataSetProducer to parse the segment data
							boolean isControlRecord = segmentName.equalsIgnoreCase(Constants.IDOC_CONTROL_RECORD_SEGMENT_TYPE_NAME); 
							producer.setSegmentData(segmentDataIterator.next(), isControlRecord);
							//TODO check produceItem?
							int produceItem = producer.produceOneTopLevelDataItem();
							this.logger.log(Level.FINEST, "Items produced: {0}", Integer.toString(produceItem)); //$NON-NLS-1$
						}
					}else{
						this.logger.log(Level.FINEST,
							      "IDoc file {0} does not contain segments of type: {1}", new Object[]{idocFile.getName(), segmentName}); //$NON-NLS-1$
					}
				}
			}
		}
		catch (CC_Exception e) {
			this.logger.log(Level.SEVERE, "CC_IDOC_CommonUnexpectedException", e); //$NON-NLS-1$
			throw e;
		}
		catch (Exception e) {
			this.logger.log(Level.SEVERE, "CC_IDOC_CommonUnexpectedException", e); //$NON-NLS-1$
			CCFUtils.throwCC_Exception(e);
		}
		this.logger.exiting(CLASSNAME, METHODNAME);
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
