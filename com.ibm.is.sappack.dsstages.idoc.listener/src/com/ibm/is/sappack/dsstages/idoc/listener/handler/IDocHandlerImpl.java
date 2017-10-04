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
// Module Name : com.ibm.is.sappack.dsstages.idoc.listener.handler
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.dsstages.idoc.listener.handler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.is.sappack.dsstages.common.StageLogger;
import com.ibm.is.sappack.dsstages.idoc.listener.IDocServerImpl;
import com.ibm.is.sappack.dsstages.idoc.listener.IDocTypeConfigReader;
import com.ibm.is.sappack.dsstages.idoc.listener.IDocTypeConfiguration;
import com.ibm.is.sappack.dsstages.idoc.listener.jobrunner.DSJob;
import com.ibm.is.sappack.dsstages.idoc.listener.jobrunner.DSJobRunner;
import com.ibm.is.sappack.dsstages.idoc.listener.jobrunner.DSJobRunnerImpl;
import com.ibm.is.sappack.dsstages.idoc.listener.jobrunner.IDocCounter;
import com.ibm.is.sappack.dsstages.idoc.listener.jobrunner.UVAccess;
import com.ibm.is.sappack.dsstages.idoc.listener.jobrunner.UVAccessFactory;
import com.ibm.is.sappack.dsstages.idoc.listener.statussender.StatusIDocSender;
import com.ibm.is.sappack.dsstages.idoc.listener.statussender.StatusIDocSenderImpl;
import com.ibm.is.sappack.dsstages.idoc.listener.util.IDocServerConstants;
import com.ibm.is.sappack.dsstages.idoc.listener.util.IDocServerMessages;
import com.ibm.is.sappack.dsstages.idoc.listener.util.TransactionState;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoTable;

/**
 * IDocHandlerImpl
 * 
 * Implementation of an IDocHandler to handle incoming IDoc transactions from
 * SAP. Each transaction may result in multiple calls of the
 * processIDocs(JCoFunction) method.
 * 
 * We have to make sure that IDocHandlerImpl is implemented thread-safe since
 * processIDocs(JCoFunction) might be called by multiple threads at the same
 * time
 */
public class IDocHandlerImpl implements IDocHandler {

	/* logger */
	private Logger logger = null;

	/* class name for logging purposes */
	static final String CLASSNAME = IDocServerImpl.class.getName();

	/* IDocFiles of this IDocHandler instance - requires thread-safe implementation */
	private List<IDocFile> idocFiles = null;

	/* transaction state - should be updated by setTransactionState() only */
	private TransactionState transactionState = null;

	/* IDocType configurations stored in a synchronized Map */
	Map<String, IDocTypeConfiguration> idocTypeConfigurations = null;
	
	public static HashMap<String,String> initfileInfo;
	private final String TILDA = ""+(char)126;
	private final String BS = ""+(char)47;
	private final String AMPERSAND = ""+(char)38;
	private final String PERCENT = ""+(char)37;
	private final String JOBNAMETAG = AMPERSAND+"J"+AMPERSAND;
	private final String NOOFNODESTAG = AMPERSAND+"N"+AMPERSAND;

	/**
	 * IDocHandlerImpl
	 * 
	 * @param defaultIDocTypesDirectory
	 */
	public IDocHandlerImpl(String defaultIDocTypesDirectory) {

		/* initialize logger */
		this.logger = StageLogger.getLogger();
		/* thread-safe List of IDocFiles */
		this.idocFiles = Collections.synchronizedList(new ArrayList<IDocFile>());

		/* read IDocType configurations */
		IDocTypeConfigReader idocTypeConfigReader = IDocTypeConfigReader.getInstance();
		this.idocTypeConfigurations = idocTypeConfigReader.getIDocTypeConfigurations(defaultIDocTypesDirectory);

		/* update transaction state */
		this.setTransactionState(TransactionState.CREATED);
		initfileInfo = new HashMap<String,String>();

	}

	/**
	 * processIDocs
	 * 
	 * process incoming IDocs from SAP. This method has to be implemented
	 * thread-safe since it might be called by different threads at the same
	 * time.
	 * 
	 * We also have to consider that one IDOC_INBOUND_ASYNCHRONOUS call may
	 * include multiple IDocs with different IDoc types. For example it is
	 * possible to send one MATMAS01 IDoc and two DEBMAS06 IDocs in one call.
	 * 
	 * @param jcoFunction
	 * @param connectionName
	 */
	public void processIDocs(String tid,JCoFunction jcoFunction, String connectionName, String filePath) {
		
		String idocRequestNumber = null;
        int idocStateNumber = 0;
        int idocSequenceNumber = 0;
        try{

		final String METHODNAME = "processIDoc(JCoFunction jcoFunction)"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);
		this.logger.log(Level.INFO,"entering METHODNAME: {0}",METHODNAME);

		/* get table parameters of IDOC_INBOUND_ASYCHRONOUS */
		JCoParameterList tableParams = jcoFunction.getTableParameterList();

		/* get IDoc control record table */
		JCoTable controlRecordTable = tableParams.getTable(IDocServerConstants.IDOC_CONTROL_REC_40);
		
		/* get IDoc segment data table */
		JCoTable segmentDataTable = tableParams.getTable(IDocServerConstants.IDOC_DATA_REC_40);
		
		/* get IDoc segment data */
		String segmentData=segmentDataTable.getString("SDATA");
		
		this.logger.log(Level.INFO,"Segment Data: {0}" ,segmentData);
		idocRequestNumber = segmentData.substring(0,30).trim();
		if(IDocServerImpl.errorReqNum.containsKey(idocRequestNumber))
		throw new RuntimeException("Request No. "+idocRequestNumber+" can't be processed. Error in Listener.");
		IDocServerImpl.initfileInfo.put(tid, idocRequestNumber);
        this.logger.log(Level.INFO,"IDocType: {0}" ,controlRecordTable.getString("IDOCTYP").toString());	
        if("RSINFO".equals(controlRecordTable.getString("IDOCTYP").toString())){
        	this.logger.log(Level.INFO,"entering METHODNAME: initializeRequestStateFileCreation()" );
        	StageLogger.getLogger().log(Level.INFO,"idocRequestNumber: {0}" ,idocRequestNumber);
    		idocSequenceNumber = Integer.parseInt(segmentData.substring(30,36).trim());
            this.logger.log(Level.INFO,"idocSequenceNumber: {0}" ,idocSequenceNumber);	
            idocStateNumber = Integer.parseInt(segmentData.substring(50,51).trim());
            this.logger.log(Level.INFO,"idocStateNumber: {0}" ,idocStateNumber);
        	initializeRequestStateFileCreation(idocRequestNumber,idocSequenceNumber, idocStateNumber,filePath);//IDocServerImpl.tidpath.toString());
        }else
        {
		/* create IDocFiles for all IDocs contained in controlRecordTable and segmentDataTable */
		IDocFileFactory idocFileFactory = new InMemoryIDocFileFactory(this.idocTypeConfigurations);
		List<IDocFile> newIDocFiles = idocFileFactory.createIDocFiles(connectionName, controlRecordTable, segmentDataTable);
		/* add newIDocFiles to already existing IDocFiles - keep in mind that processIDocs might be called several times by different threads */
		this.idocFiles.addAll(newIDocFiles);

		/* update transaction state */
		this.setTransactionState(TransactionState.EXECUTED);
		}
		this.logger.exiting(CLASSNAME, METHODNAME);
        }catch(Exception ex){
    		throw new RuntimeException(ex.getMessage());
    	}

	}

	static String copyright() {
		return com.ibm.is.sappack.dsstages.idoc.listener.handler.Copyright.IBM_COPYRIGHT_SHORT;
	}

	@Override
	/**
	 * commit transaction 
	 * and start DataStage jobs
	 * using the DSJobRunner. The
	 * list of DataStage jobs that need to be started
	 * can be determined using the uvAccess instance
	 * 
	 * @param idocServerProperties
	 */
	public void commit() {

		final String METHODNAME = "commit()"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);

		/* commit all IDocFiles and update the IDoc counter(s) */
		Iterator<IDocFile> idocFiles = this.idocFiles.iterator();
		logger.log(Level.FINER, "Committing {0} IDoc files", this.idocFiles.size());
		while (idocFiles.hasNext()) {
			/* process each IDocFile separately - not all IDocFiles do have the same IDocType necessarily */
			IDocFile idocFile = idocFiles.next();
			/* commit IDocFile */
			idocFile.commit();
			/* update the IDoc counter for the IDocType of this IDocFile */
			this.updateIDocCounter(idocFile);
		}

		/* update transaction state */
		this.setTransactionState(TransactionState.COMMITTED);

		this.logger.exiting(CLASSNAME, METHODNAME);
	}

	@Override
	/**
	 * rollback
	 * 
	 * roll back all IDocFiles. This method
	 * is called if any errors occurred while
	 * executing or committing the transaction.
	 * 
	 * We have to check whether the transaction was
	 * in state 'executed' or in state 'committed'
	 * and clean up accordingly 
	 * 
	 */
	public void rollback() {

		final String METHODNAME = "rollback()"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);

		/* roll back IDocFiles */
		Iterator<IDocFile> idocFiles = this.idocFiles.iterator();
		logger.log(Level.FINER, "Rolling back {0} IDoc files", this.idocFiles.size());
		while (idocFiles.hasNext()) {
			IDocFile idocFile = idocFiles.next();
			
			/* decrement IDoc counter for the IDocType of this IDocFile if counter has already been incremented in commit() */
			if(this.getTransactionState().equals(TransactionState.COMMITTED)) {
				IDocCounter idocCounter = this.idocTypeConfigurations.get(idocFile.getIDocType()).getIDocCounter();
				idocCounter.decrement(1);
			}
			
			/* roll back the IDocFile itself */
			idocFile.rollback();
		}

		/* roll back IDocFile list */
		this.idocFiles = Collections.synchronizedList(new ArrayList<IDocFile>());

		/* update transaction state */
		this.setTransactionState(TransactionState.ROLLED_BACK);

		this.logger.exiting(CLASSNAME, METHODNAME);
	}

	@Override
	/**
	 * getTransactionState
	 * 
	 * return the transaction state of the IDocHandler
	 * 
	 * @return
	 */
	public synchronized TransactionState getTransactionState() {

		return this.transactionState;
	}

	@Override
	/**
	 * confirmTransaction
	 * 
	 * this method is called if the transaction has been
	 * committed successfully.
	 * 
	 * Send confirmation IDocs and start
	 * DataStage jobs if necessary
	 *
	 * @param idocServerProperties
	 * @param destination
	 */
	public void confirmTransaction(Map<String, String> idocServerProperties, JCoDestination destination) {

		final String METHODNAME = "confirmTransaction()"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);

		logger.log(Level.FINE, "Sending {0} status IDocs to SAP", this.idocFiles.size());
		
		
		/* 
		 * check if the DataStage SAP connection that is used by this IDocServer instance
		 * is configured to auto-run DataStage jobs 
		 */
		boolean autoRunEnabled = Boolean.parseBoolean(idocServerProperties.get(IDocServerConstants.ALLOWSAPTORUNJOBS));
		logger.log(Level.FINE, "Allow SAP to auto-run DataStage jobs: {0}", autoRunEnabled);
		
		/* Initialize UVAccess */
		UVAccessFactory uvAccessFactory = new UVAccessFactory();
		UVAccess uvAccess = uvAccessFactory.createUVAccess(idocServerProperties.get(IDocServerConstants.UVHOME));

		/* initialize the DSJobRunner to start DataStage jobs after receiving IDocs from SAP */
		DSJobRunnerImpl dsJobRunner = new DSJobRunnerImpl();
		dsJobRunner.initialize(idocServerProperties, this.idocTypeConfigurations);
		String connectionName = idocServerProperties.get(IDocServerConstants.NAME);
		
		/* all incoming IDocs have been committed and the corresponding
		 * IDoc counters have been updated at this time. Now, iterate of
		 * the List of IDocFiles and start DataStage jobs if necessary.
		 * Keep in mind that the IDocFiles may have different IDoc types */
		
		if(autoRunEnabled) {
			Iterator<IDocFile> idocFiles = this.idocFiles.iterator();
			
			while (idocFiles.hasNext()) {
				IDocFile idocFile = idocFiles.next();
				/* start DataStage job for the IDocType of this IDocFile. The IDoc counter 
				 * will be reset after the DataStage job(s) of a specific IDoc type have been run.
				 * This will prevent us from running the DataStage job(s) for a specific IDoc type
				 * multiple times */
				 String timeDelayenv = System.getenv("DS_IDOC_EXTRACT_TIME_DELAY");
				if(!(timeDelayenv== null || timeDelayenv.equals("")))
				{
					this.logger.log(Level.INFO, "Delay Requested by user of {0} seconds", new Object[]{timeDelayenv});
					this.logger.log(Level.INFO, "Entering Delay block", new Object[]{});
					int pauseTime = Integer.parseInt(timeDelayenv);
					try {
						Thread.sleep(pauseTime*1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					this.logger.log(Level.INFO, "Exiting Delay block", new Object[]{});
				}
				this.startDataStageJobs(dsJobRunner, uvAccess, idocFile.getIDocType(), connectionName);
			}
		}
		
		/* 
		 * send status IDocs to SAP is transaction has been committed without any errors
		 */
		if ( this.sendStatusIDocs(idocServerProperties)) {
			try {

				StatusIDocSender statusIDocSender = new StatusIDocSenderImpl();
				statusIDocSender.initialize(destination, connectionName);

				/* send status IDocs to SAP */
				statusIDocSender.sendStatusIDocs(this.idocFiles);

			} catch (JCoException e) {
				/* log that we couldn't send the status IDocs */
				logger.warning(IDocServerMessages.FailedToSendStatusIDocs);
				logger.warning(e.getMessage());
			} catch (IOException e) {
				/* log that we couldn't retrieve IDoc meta data */
				logger.warning(IDocServerMessages.FailedToSendStatusIDocs);
				logger.warning(e.getMessage());
			}

		}
		
		/* update transaction state */
		this.setTransactionState(TransactionState.CONFIRMED);
		
		this.logger.exiting(CLASSNAME, METHODNAME);
	}

	/**
	 * updateIDocCounter
	 * 
	 * update the IDoc counter for the IDocType of the given IDocFile. This is
	 * only necessary if the IDocType is configured for automatic DataStage job
	 * execution
	 * 
	 * @param idocFile
	 */
	private void updateIDocCounter(IDocFile idocFile) {

		IDocTypeConfiguration idocTypeConfig = this.idocTypeConfigurations.get(idocFile.getIDocType());
		if (idocTypeConfig != null && idocTypeConfig.isAutorunEnabled()) {
			/* update the IDoc counter */
			IDocCounter idocCounter = idocTypeConfig.getIDocCounter();
			/* increment counter only with 1 since not all idocFiles of this
			 * IDocHandler have to be of the same IDocType necessarily
			 */
			idocCounter.increment(1);
		}

	}

	/**
	 * startDataStageJobs
	 * 
	 * start DataStage jobs if IDoc type is configured accordingly
	 * 
	 * @param dsJobRunner
	 * @param uvAccess
	 * @param idocType
	 * @param connectionName
	 */
	private void startDataStageJobs(DSJobRunner dsJobRunner, UVAccess uvAccess, String idocType, String connectionName) {

		final String METHODNAME = "startDataStageJobs(DSJobRunner dsJobRunner, UVAccess uvAccess)"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);
		
		/* check if this IDoc type is configured to run DataStage jobs automatically */
		IDocTypeConfiguration idocTypeConfig = this.idocTypeConfigurations.get(idocType);
		if (idocTypeConfig != null && idocTypeConfig.isAutorunEnabled()) {

			/* check IDoc counter and decide if we need to run DataStage jobs */
			IDocCounter idocCounter = idocTypeConfig.getIDocCounter();
			int threshold = idocTypeConfig.getIDocCountThreshold();
			int counterValue = idocCounter.getValue();

			/* run DataStage jobs if counterValue is bigger than the threshold
			 * of this IDoc type or when an error occurred while reading
			 * the IDoc counter file (counterValue == -1)
			 */
			if (counterValue >= threshold || counterValue == -1) {
			
				this.logger.log(Level.FINER, "IDoc counter value is {0}. The threshold of {1} IDocs has been reached - Starting DataStage jobs", new Object[] { counterValue, threshold });

				/* check if we need to run only one or multiple DataStage jobs */
				if (idocTypeConfig.isSelectJob()) {

					/* run only one specific DataStage jobs */
					String jobName = idocTypeConfig.getJobName();
					String projectName = idocTypeConfig.getProjectName();
					DSJob dsJob = new DSJob(projectName, jobName);
					dsJobRunner.runDataStageJob(idocType, dsJob);

				} else {

					/* run multiple DataStage jobs */
					List<DSJob> jobs = uvAccess.getJobList(idocType, connectionName);
					dsJobRunner.runDataStageJobs(idocType, jobs);
				}

				/* reset IDoc counter after running the DataStage jobs */
				idocCounter.reset();
			}

		}
		this.logger.exiting(CLASSNAME, METHODNAME);
	}

	/**
	 * sendStatusIDocs
	 * 
	 * return true if we need to send status IDocs back to SAP when receiving
	 * IDocs from SAP. Otherwise return false
	 * 
	 * @return
	 */
	private boolean sendStatusIDocs(Map<String, String> idocServerProperties) {

		if (idocServerProperties.get(IDocServerConstants.ACKNOWLEDGEIDOCRECEIPT).equalsIgnoreCase(IDocServerConstants.TRUE)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * setTransactionState
	 * 
	 * update the TransactionState in a thread-safe kind of way
	 * 
	 * @param state
	 */
	private synchronized void setTransactionState(TransactionState state) {
		this.transactionState = state;
	}
	
	public void initializeRequestStateFileCreation(String reqNumber,int packetId, int processState, String filepath){
		String folderpath;
		boolean isInitialised = initfileInfo.containsKey(reqNumber);
		if(!isInitialised) // initfileInfo.containsKey(reqNumber))
		{
			readinitfile(filepath);
			isInitialised = initfileInfo.containsKey(reqNumber);
		}

		if(isInitialised)
		{
			String jobdetail = initfileInfo.get(reqNumber);
			String[] detail = jobdetail.split(TILDA);
			String jobName = detail[0]; 
			folderpath = filepath+BS+jobName+BS+reqNumber+BS+"InfoIDoc"+BS;
			if(validateFolder(folderpath,processState))
			{
				String filename = folderpath+processState+BS+reqNumber+"_InfoIdocNum_"+packetId+"_RequestState_"+processState+".txt";
				File file = new File(filename);
				if (!file.exists()) {
					try{
						file.createNewFile();
					}catch(IOException e){
						e.printStackTrace();
					}
				}
			}
			else
			{
				throw new RuntimeException(IDocServerMessages.ExceptionRaisedinRequestInfoPacket);
			}
		}
		else
		{
			throw new RuntimeException(IDocServerMessages.ExceptionRaisedinRequestInfoPacket);
		}
	}
	
	public boolean validateFolder(String folderpath,int processState) {
		StageLogger.getLogger().log(Level.INFO, "folderpath inside validateFolder() method: {0}",folderpath);
		StageLogger.getLogger().log(Level.INFO, "processState inside validateFolder() method: {0}",processState);
		boolean folderexists = false;
		File file  = new File(folderpath);
		if(file.isDirectory())
		{
			folderexists= true;
			if(!(processState==2))
			{
				File file1 = new File (folderpath+processState+"/");
				file1.mkdirs();
			}
		}
		StageLogger.getLogger().log(Level.INFO, "validateFolder() method returns: {0}",folderexists);
		return folderexists;
	}

	public void readinitfile(String folderPath)
	{
		File folder = new File(folderPath);
		String[] numberOfFiles = folder.list();
		for(String fileName:numberOfFiles){
			try
			{
				String reqId = fileName.substring(0,fileName.indexOf(JOBNAMETAG));
				int nodeofnodes = Integer.parseInt(fileName.substring(fileName.indexOf(NOOFNODESTAG)+3,fileName.indexOf(PERCENT)));
				String jobName = fileName.substring(fileName.indexOf(JOBNAMETAG)+3,fileName.indexOf(NOOFNODESTAG));
				String dataSourceType = fileName.substring(fileName.length()-1);
				initfileInfo.put(reqId, jobName+TILDA+nodeofnodes+TILDA+dataSourceType);
			}
			catch(Exception e)
			{
				System.out.println("Correct file not found. Ignoring parsing");
			}
		}
	}
	
}
