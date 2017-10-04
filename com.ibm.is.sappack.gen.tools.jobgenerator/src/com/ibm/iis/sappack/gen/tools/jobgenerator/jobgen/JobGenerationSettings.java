//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2014                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.iis.sappack.gen.tools.jobgenerator.jobgen
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.iis.sappack.gen.tools.jobgenerator.jobgen;


import org.eclipse.core.resources.IFile;

import com.ibm.iis.sappack.gen.common.ui.connections.IISConnection;
import com.ibm.iis.sappack.gen.common.ui.connections.SapSystem;


public class JobGenerationSettings {
	public static final int OBJECT_TYPE_NONE = 0;
	public static final int OBJECT_TYPE_IDOC = 1;
	public static final int OBJECT_TYPE_TABLES_DATA = 2;
	public static final int OBJECT_TYPE_TABLES_REF_AND_TEXT = 4;
	public static final int OBJECT_TYPE_TABLES_OTHER = 8;
	// convenience
	public static final int OBJECT_TYPE_ALL_TABLES = OBJECT_TYPE_TABLES_DATA | OBJECT_TYPE_TABLES_REF_AND_TEXT | OBJECT_TYPE_TABLES_OTHER;
	public static final int OBJECT_TYPE_ALL = OBJECT_TYPE_ALL_TABLES | OBJECT_TYPE_IDOC;

	private IISConnection iisConnection;

	
	// general
	private String dsFolder;
	private String dsProject;
	private String jobNamePrefix = "SAPPack_Job_" + System.currentTimeMillis() + "_"; //$NON-NLS-1$ //$NON-NLS-2$
	private int objectTypes = OBJECT_TYPE_ALL;
	private boolean overwriteJob = false;
	private boolean launchJobCompiler = false;

	private IFile checkTablesDBMFile;
	private IFile sourceDBMFile;
	private IFile targetDBMFile;

	// ABAP
	private String abapProgramPrefix = "Z_SP_"; //$NON-NLS-1$
	private String additionalABAPCode = ""; //$NON-NLS-1$
	boolean saveABAPPrograms = true;
	boolean uploadABAPPrograms = true;
	private int numberOfRetries = 30;
	private int retryInterval = 1;
	boolean suppressBackgroundJob = false;
	SapSystem abapUploadConnection;
	boolean useCTS;
	boolean createSeparateTransports = false;
	String ctsPackage;
	String ctsRequest;
	String ctsRequestDesc;
	

	// IDoc load
	private String idocMessageType;


  	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}

	public boolean isUseCTS() {
		return useCTS;
	}

	public void setUseCTS(boolean useCTS) {
		this.useCTS = useCTS;
	}

	public boolean getcreateSeparateTransports()	{
		return createSeparateTransports;
	}
	
	public void setcreateSeparateTransports(boolean createSeparateTransports) {
		this.createSeparateTransports = createSeparateTransports;
	}
	
	public String getCTSPackage() {
		return ctsPackage;
	}

	public void setCTSPackage(String ctsPackage) {
		this.ctsPackage = ctsPackage;
	}

	public String getCTSRequest() {
		return ctsRequest;
	}

	public void setCTSRequest(String ctsRequest) {
		this.ctsRequest = ctsRequest;
	}
	
	public String getCTSRequestDescription() {
		return ctsRequestDesc;
	}

	public void setCTSRequestDescription(String ctsRequestDesc) {
		this.ctsRequestDesc = ctsRequestDesc;
	}

	public boolean isSaveABAPPrograms() {
		return saveABAPPrograms;
	}

	public void setSaveABAPPrograms(boolean saveABAPPrograms) {
		this.saveABAPPrograms = saveABAPPrograms;
	}

	public boolean isUploadABAPPrograms() {
		return uploadABAPPrograms;
	}

	public void setUploadABAPPrograms(boolean uploadABAPPrograms) {
		this.uploadABAPPrograms = uploadABAPPrograms;
	}

	public SapSystem getAbapUploadConnection() {
		return abapUploadConnection;
	}

	public void setAbapUploadConnection(SapSystem abapUploadConnection) {
		this.abapUploadConnection = abapUploadConnection;
	}

	public IISConnection getIisConnection() {
		return iisConnection;
	}

	public void setIisConnection(IISConnection iisConnection) {
		this.iisConnection = iisConnection;
	}

	public IFile getCheckTablesDBMFile() {
		return(this.checkTablesDBMFile);
	}

	public void setCheckTablesDBMFile(IFile chkTblDBMFile) {
		this.checkTablesDBMFile = chkTblDBMFile;
	}

	public IFile getSourceDBMFile() {
		return(this.sourceDBMFile);
	}

	public void setSourceDBMFile(IFile sourceDBMFile) {
		this.sourceDBMFile = sourceDBMFile;
	}

	public IFile getTargetDBMFile() {
		return(this.targetDBMFile);
	}

	public void setTargetDBMFile(IFile targetDBMFile) {
		this.targetDBMFile = targetDBMFile;
	}

	public String getJobNamePrefix() {
		return jobNamePrefix;
	}

	public void setJobNamePrefix(String jobNamePrefix) {
		this.jobNamePrefix = jobNamePrefix;
	}

	public int getObjectTypes() {
		return objectTypes;
	}

	public void setObjectTypes(int objectTypes) {
		this.objectTypes = objectTypes;
	}

	public boolean isOverwriteJob() {
		return overwriteJob;
	}

	public void setOverwriteJob(boolean overwriteJob) {
		this.overwriteJob = overwriteJob;
	}

	public String getAbapProgramPrefix() {
		return abapProgramPrefix;
	}

	public void setAbapProgramPrefix(String abapProgramPrefix) {
		this.abapProgramPrefix = abapProgramPrefix;
	}

	public int getNumberOfRetries() {
		return numberOfRetries;
	}

	public void setNumberOfRetries(int numberOfRetries) {
		this.numberOfRetries = numberOfRetries;
	}

	public int getRetryInterval() {
		return retryInterval;
	}

	public void setRetryInterval(int retryInterval) {
		this.retryInterval = retryInterval;
	}

	public boolean getSuppressBackgroundJob() {
		return suppressBackgroundJob;
	}
	
	public void setSuppressBackgroundJob(boolean suppressBackgroundJob){
		this.suppressBackgroundJob = suppressBackgroundJob;
	}

	public String getAdditionalABAPCode() {
		return additionalABAPCode;
	}

	public void setAdditionalABAPCode(String additionalABAPCode) {
		this.additionalABAPCode = additionalABAPCode;
	}

	public boolean isLaunchJobCompiler() {
		return launchJobCompiler;
	}

	public void setLaunchJobCompiler(boolean launchJobCompiler) {
		this.launchJobCompiler = launchJobCompiler;
	}

	public String getIdocMessageType() {
		return idocMessageType;
	}

	public void setIdocMessageType(String idocMessageType) {
		this.idocMessageType = idocMessageType;
	}

	public String getDsFolder() {
		return dsFolder;
	}

	public void setDsFolder(String dsFolder) {
		this.dsFolder = dsFolder;
	}

	public String getDsProject() {
		return dsProject;
	}

	public void setDsProject(String dsProject) {
		this.dsProject = dsProject;
	}

}
