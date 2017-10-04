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
// Module Name : com.ibm.is.sappack.dsstages.common.ccf
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.dsstages.common.ccf;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ascential.e2.connector.CC_Adapter;
import com.ascential.e2.connector.CC_Connection;
import com.ascential.e2.connector.CC_DataSetConsumer;
import com.ascential.e2.connector.CC_DataSetProducer;
import com.ascential.e2.connector.CC_Partitioner;
import com.ascential.e2.daapi.CC_EventManager;
import com.ascential.e2.daapi.CC_RejectManager;
import com.ascential.e2.daapi.metadata.CC_DataSetDef;
import com.ascential.e2.daapi.metadata.CC_Environment;
import com.ascential.e2.daapi.metadata.CC_LocatorInfo;
import com.ascential.e2.daapi.metadata.CC_NodeDescriptor;
import com.ascential.e2.propertyset.CC_ErrorList;
import com.ascential.e2.propertyset.CC_Property;
import com.ascential.e2.propertyset.CC_PropertySet;
import com.ibm.is.sappack.dsstages.common.CCFResource;
import com.ibm.is.sappack.dsstages.common.Constants;
import com.ibm.is.sappack.dsstages.common.DSEnvironment;
import com.ibm.is.sappack.dsstages.common.StageLogger;

public abstract class IDocStageCCAdapter extends CC_Adapter {

	static String copyright() {
		return com.ibm.is.sappack.dsstages.common.ccf.Copyright.IBM_COPYRIGHT_SHORT;
	}

	private static final String CLASSNAME = IDocStageCCAdapter.class.getName();

	protected Logger logger = null;

	protected Map<String, String> stageProperties = null;
	protected Map<String, String> ccfCommonProperties = null;
	private DSEnvironment dsEnvironment = null;

	public IDocStageCCAdapter(CC_PropertySet propSet, CC_ErrorList errList) {
		super(propSet, errList);
		final String METHODNAME = "<init>"; //$NON-NLS-1$
		this.logger = StageLogger.getLogger();
		this.logger.entering(CLASSNAME, METHODNAME);
		CCFUtils.logPropertySet(m_propSet);
		initializeProperties(errList);
		this.logger.exiting(CLASSNAME, METHODNAME);
	}

	abstract protected String[] getRequiredStageProperties();
	
	private void initializeProperties(CC_ErrorList errList) {
		final String METHODNAME = "initializeProperties"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);
		CC_Property connectionProp = m_propSet.getProperty("/Connection"); //$NON-NLS-1$
		
		
		/* call createPropertyMap without requiredStageProperties - we need to check if IDOCTYPE is set */
		this.stageProperties = CCFUtils.createPropertyMap(connectionProp, errList, null);
		
		/* check if stage property IDOCTYPE is set */
		if(this.stageProperties.get(Constants.IDOCSTAGE_STAGE_PROP_IDOCTYP) == null) {
			String msgId = "CC_IDOC_IDOCTYPNotFound"; //$NON-NLS-1$
			this.logger.log(Level.SEVERE, msgId);
			throw new RuntimeException(CCFResource.getCCFMessage(msgId));
		}
		
		/* call createPropertyMap with requiredStageProperties after we have checked that IDOCTYPE is set */
		this.stageProperties = CCFUtils.createPropertyMap(connectionProp, errList, this.getRequiredStageProperties());

		CC_Property commonProp = m_propSet.getProperty("/Common"); //$NON-NLS-1$
		this.ccfCommonProperties = CCFUtils.createPropertyMap(commonProp, errList, new String[] { Constants.CCF_COMMON_PROPERTY_PATH_INPUT_LINKS });
		this.logger.exiting(CLASSNAME, METHODNAME);
	}

	@Override
	public CC_Connection getConnection() {
		final String METHODNAME = "getConnection"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);
		this.logger.exiting(CLASSNAME, METHODNAME);
		return super.getConnection();
	}

	@Override
	public CC_DataSetConsumer getDataSetConsumer(int index, CC_PropertySet propSet, CC_ErrorList errList) {
		final String METHODNAME = "getDataSetConsumer"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);
		this.logger.exiting(CLASSNAME, METHODNAME);
		return super.getDataSetConsumer(index, propSet, errList);
	}

	@Override
	public void run() {
		final String METHODNAME = "run"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);
		super.run();
		this.logger.exiting(CLASSNAME, METHODNAME);
	}

	@Override
	public CC_DataSetProducer getDataSetProducer(int index, CC_PropertySet propSet, CC_ErrorList errList) {
		final String METHODNAME = "getDataSetProducer"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);
		this.logger.exiting(CLASSNAME, METHODNAME);
		return super.getDataSetProducer(index, propSet, errList);
	}

	@Override
	public void getLocatorInfo(CC_LocatorInfo locInfo) {
		final String METHODNAME = "getLocatorInfo"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);
		super.getLocatorInfo(locInfo);
		this.logger.exiting(CLASSNAME, METHODNAME);
	}

	@Override
	public CC_Partitioner getPartitioner() {
		final String METHODNAME = "getPartitioner"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);
		this.logger.exiting(CLASSNAME, METHODNAME);
		return super.getPartitioner();
	}

	@Override
	public CC_DataSetProducer getRejectDataSetProducer(CC_RejectManager rejectManager, int index, int sourceIndex) {
		final String METHODNAME = "getRejectDataSetProducer"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);
		this.logger.exiting(CLASSNAME, METHODNAME);
		return super.getRejectDataSetProducer(rejectManager, index, sourceIndex);
	}

	@Override
	public void postRun() {
		final String METHODNAME = "postRun"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);
		super.postRun();
		this.logger.exiting(CLASSNAME, METHODNAME);
	}

	@Override
	public void preRun() {
		final String METHODNAME = "preRun"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);
		super.preRun();
		this.logger.exiting(CLASSNAME, METHODNAME);
	}

	@Override
	public synchronized void releaseInstance() {
		final String METHODNAME = "releaseInstance"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);
		super.releaseInstance();
		this.logger.exiting(CLASSNAME, METHODNAME);
	}

	@Override
	public void setConnection(CC_Connection con) {
		final String METHODNAME = "setConnection"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);
		super.setConnection(con);
		this.logger.exiting(CLASSNAME, METHODNAME);
	}

	@Override
	public boolean compareConnection(CC_Connection arg0) {
		final String METHODNAME = "compareConnection"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);
		this.logger.exiting(CLASSNAME, METHODNAME);
		return false;
	}

	@Override
	public CC_DataSetConsumer getDataSetConsumer() {
		final String METHODNAME = "getDataSetConsumer"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);
		this.logger.exiting(CLASSNAME, METHODNAME);
		return null;
	}

	@Override
	public CC_DataSetProducer getDataSetProducer() {
		final String METHODNAME = "getDataSetProducer"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);
		this.logger.exiting(CLASSNAME, METHODNAME);
		return null;
	}

	@Override
	public String getMetaData(String arg0) {
		final String METHODNAME = "getMetaData"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);
		this.logger.exiting(CLASSNAME, METHODNAME);
		return null;
	}

	@Override
	public CC_DataSetProducer getRejectDataSetProducer(CC_RejectManager arg0) {
		final String METHODNAME = "getRejectDataSetProducer"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);
		this.logger.exiting(CLASSNAME, METHODNAME);
		return null;
	}

	@Override
	public CC_DataSetDef getSchema(String arg0) {
		final String METHODNAME = "getSchema"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);
		this.logger.exiting(CLASSNAME, METHODNAME);
		return null;
	}

	@Override
	public void initEventHandling(CC_EventManager arg0) {
		final String METHODNAME = "initEventHandling"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);
		this.logger.exiting(CLASSNAME, METHODNAME);
	}

	@Override
	public void modifyPropertiesForViewData() {
		final String METHODNAME = "modifyPropertiesForViewData"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);
		this.logger.exiting(CLASSNAME, METHODNAME);
	}

	@Override
	public void setProperties(CC_PropertySet arg0, CC_ErrorList arg1) {
		final String METHODNAME = "setProperties"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);
		this.logger.exiting(CLASSNAME, METHODNAME);
	}

	@Override
	public void termEventHandling() {
		final String METHODNAME = "termEventHandling"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);
		this.logger.exiting(CLASSNAME, METHODNAME);
	}

	@Override
	public void validateCurrentProperties(CC_ErrorList arg0) {
		final String METHODNAME = "validateCurrentProperties"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);
		this.logger.exiting(CLASSNAME, METHODNAME);
	}

	@Override
	public int validateSchema(CC_DataSetDef arg0, CC_ErrorList arg1) {
		final String METHODNAME = "validateSchema"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);
		this.logger.exiting(CLASSNAME, METHODNAME);
		return 0;
	}

	public Map<String, String> getStageProperties() {
		final String METHODNAME = "getStageProperties()"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);
		this.logger.exiting(CLASSNAME, METHODNAME);
		return this.stageProperties;
	}

	public Map<String, String> getCCFCommonProperties() {
		final String METHODNAME = "getCCFCommonProperties()"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);
		this.logger.exiting(CLASSNAME, METHODNAME);
		return this.ccfCommonProperties;
	}
	
	private int getCurrentNodeNumber() {
		final String METHODNAME = "getCurrentNodeNumber()"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);
		CC_Environment env = this.getConnection().getEnvironment();
		int nodeNum = Integer.MIN_VALUE;
		if (env != null) {
			nodeNum = env.getNodeNumber();
			this.logger.log(Level.FINE, "current node number: {0}", nodeNum); //$NON-NLS-1$
		}
		this.logger.exiting(CLASSNAME, METHODNAME);
		return nodeNum;		
	}
	
	private int getOverallNodeNumber() {
		final String METHODNAME = "getOverallNodeNumber()"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);
		CC_Environment env = this.getConnection().getEnvironment();
		int overallNum = Integer.MIN_VALUE;
		if (env != null) {
			CC_NodeDescriptor[] descs = env.getNodeDescriptors();
			this.logger.log(Level.FINER, "Node Descriptor array: {0}", (Object) descs); //$NON-NLS-1$
			if (descs != null) {
				overallNum = descs.length;
				this.logger.log(Level.FINE, "overall node number: {0}", overallNum); //$NON-NLS-1$
			}
		}
		this.logger.exiting(CLASSNAME, METHODNAME);
		return overallNum;
	}

	public DSEnvironment getDSEnvironment() {
		final String METHODNAME = "getDSEnvironment()"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);
		if (this.dsEnvironment == null) {
			this.dsEnvironment = new DSEnvironment();
			// TODO set job name and instance ID
			this.dsEnvironment.setCurrentNodeNumber(getCurrentNodeNumber());
			this.dsEnvironment.setOverallNodeNumber(getOverallNodeNumber());
			CC_Environment env = this.getConnection().getEnvironment();
			String jobName = env.getEnvironmentAttribute("Job"); //$NON-NLS-1$
			this.logger.log(Level.FINE, "DS Environment job: " + jobName); //$NON-NLS-1$
			this.dsEnvironment.setJobName(jobName);
			
			String invocationID = env.getEnvironmentAttribute("InvocationID"); //$NON-NLS-1$
			this.logger.log(Level.FINE, "DS Environment invocation ID: " + invocationID); //$NON-NLS-1$
			this.dsEnvironment.setInvocationID(invocationID);
			
			String userName = env.getEnvironmentAttribute("User"); //$NON-NLS-1$
			this.logger.log(Level.FINE, "DS Environment user: " + userName); //$NON-NLS-1$
			this.dsEnvironment.setUserName(userName);
			
			String projectName = env.getEnvironmentAttribute("Project"); //$NON-NLS-1$
			this.logger.log(Level.FINE, "DS Environment project: " + projectName); //$NON-NLS-1$
			this.dsEnvironment.setProjectName(projectName);
			
			String hostName = env.getEnvironmentAttribute("Host"); //$NON-NLS-1$
			this.logger.log(Level.FINE, "DS Environment host: " + hostName); //$NON-NLS-1$
			this.dsEnvironment.setHostName(hostName);
			
			String version = env.getEnvironmentAttribute("Version"); //$NON-NLS-1$
			this.logger.log(Level.FINE, "DS Environment version: " + version); //$NON-NLS-1$
			this.dsEnvironment.setVersion(version);
			
			String stageName = env.getEnvironmentAttribute("Stage"); //$NON-NLS-1$
			this.logger.log(Level.FINE, "DS Environment stage: " + stageName); //$NON-NLS-1$
			this.dsEnvironment.setStageName(stageName);
		}
		this.logger.exiting(CLASSNAME, METHODNAME);
		return dsEnvironment;
	}
	
}
