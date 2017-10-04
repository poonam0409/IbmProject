package com.ibm.iis.sappack.ant;

import java.text.MessageFormat;
import java.util.Date;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;

import com.ibm.iis.sappack.gen.tools.jobgenerator.api.RapidGenerator;
import com.ibm.iis.sappack.gen.tools.jobgenerator.api.RapidGeneratorFactory;
import com.ibm.iis.sappack.gen.tools.jobgenerator.api.RapidGeneratorSAPSettings;


public class RapidGeneratorTask extends Task {
	/*
	public static final int OBJECT_TYPE_NONE = 0;
	public static final int OBJECT_TYPE_IDOC = 1;
	public static final int OBJECT_TYPE_TABLES_DATA = 2;
	public static final int OBJECT_TYPE_TABLES_REF_AND_TEXT = 4;
	public static final int OBJECT_TYPE_TABLES_OTHER = 8;
	// convenience
	public static final int OBJECT_TYPE_ALL_TABLES = OBJECT_TYPE_TABLES_DATA | OBJECT_TYPE_TABLES_REF_AND_TEXT | OBJECT_TYPE_TABLES_OTHER;
	public static final int OBJECT_TYPE_ALL = OBJECT_TYPE_ALL_TABLES | OBJECT_TYPE_IDOC;
*/
	
	private String iisConnection;
	private String rapidGeneratorConfiguration;

	// general
	private String dsFolder;
	private String dsProject;
	private String jobNamePrefix;
//	private int objectTypes = OBJECT_TYPE_ALL;
	private boolean overwriteJobs = false;

	private String sourceDBMFile;
	private String targetDBMFile;

	// ABAP
	private String abapProgramPrefix = "Z_SP_"; //$NON-NLS-1$
//	private String additionalABAPCode = "";
	private boolean saveABAPPrograms = true;
	private boolean uploadABAPPrograms = false;
	private String abapUploadConnection;
	private boolean useCTS = false;
	private String ctsPackage;
	private String ctsRequest;

	// IDoc load
	private String idocMessageType;

	public void setRapidGeneratorConfiguration(String rgConfig) {
		this.rapidGeneratorConfiguration = rgConfig;
	}
	
	public void setUseCTS(boolean useCTS) {
		this.useCTS = useCTS;
	}

	public void setCTSPackage(String ctsPackage) {
		this.ctsPackage = ctsPackage;
	}

	public void setCTSRequest(String ctsRequest) {
		this.ctsRequest = ctsRequest;
	}

	public void setSaveABAPPrograms(boolean saveABAPPrograms) {
		this.saveABAPPrograms = saveABAPPrograms;
	}

	public void setUploadABAPPrograms(boolean uploadABAPPrograms) {
		this.uploadABAPPrograms = uploadABAPPrograms;
	}

	public void setAbapUploadSAPConnection(String abapUploadConnection) {
		this.abapUploadConnection = abapUploadConnection;
	}

	public void setIisConnection(String iisConnection) {
		this.iisConnection = iisConnection;
	}

	public void setSourceDBMFile(String sourceDBMFile) {
		this.sourceDBMFile = sourceDBMFile;
	}

	public void setTargetDBMFile(String targetDBMFile) {
		this.targetDBMFile = targetDBMFile;
	}

	public void setJobNamePrefix(String jobNamePrefix) {
		this.jobNamePrefix = jobNamePrefix;
	}
/*
	public void setObjectTypes(int objectTypes) {
		this.objectTypes = objectTypes;
	}

	public void setOverwriteJob(boolean overwriteJob) {
		this.overwriteJob = overwriteJob;
	}
*/
	public void setAbapProgramPrefix(String abapProgramPrefix) {
		this.abapProgramPrefix = abapProgramPrefix;
	}
/*
	public void setAdditionalABAPCode(String additionalABAPCode) {
		this.additionalABAPCode = additionalABAPCode;
	}
*/
	public void setIdocLoadMessageType(String idocMessageType) {
		this.idocMessageType = idocMessageType;
	}

	public void setDsFolder(String dsFolder) {
		this.dsFolder = dsFolder;
	}

	public void setDsProject(String dsProject) {
		this.dsProject = dsProject;
	}
	
	public void setOverwriteJobs(boolean overwrite) {
		this.overwriteJobs = overwrite;
	}
	
	@SuppressWarnings("nls")
	@Override
	public void execute() throws BuildException {
		try {
			log("=============================================", Project.MSG_INFO); 
			log(MessageFormat.format("Starting Rapid Generator for SAP on ''{0}''", new Date().toString()), Project.MSG_INFO);
			RapidGenerator rg = RapidGeneratorFactory.createRapidGenerator();
			RapidGeneratorSAPSettings rgSAPSettings = RapidGeneratorFactory.createRapidGeneratorSettings();
			rgSAPSettings.setABAPProgramPrefix(abapProgramPrefix);
			rgSAPSettings.setSaveABAPPrograms(this.saveABAPPrograms);
			rgSAPSettings.setUploadABAPPrograms(this.uploadABAPPrograms);
			rgSAPSettings.setABAPUploadSAPConnection(this.abapUploadConnection);
			rgSAPSettings.setUseCTS(this.useCTS);
			rgSAPSettings.setCTSPackage(this.ctsPackage);
			rgSAPSettings.setCTSRequest(this.ctsRequest);
			rgSAPSettings.setIDocLoadMessageType(this.idocMessageType);
			
			rg.generateJobs(this.rapidGeneratorConfiguration, //
					this.sourceDBMFile,
					this.targetDBMFile, 
					rgSAPSettings,
					this.iisConnection,
					this.dsProject, 	
					this.dsFolder,
					this.jobNamePrefix,
					this.overwriteJobs,
					null);
			
			log(MessageFormat.format("Rapid Generator finished successully on ''{0}''", new Date().toString()), Project.MSG_INFO);
			log("=============================================", Project.MSG_INFO); //$NON-NLS-1$
		} catch (Exception exc) {
			exc.printStackTrace();
			exc.printStackTrace(System.out);
			log(exc, Project.MSG_ERR);
			throw new BuildException(exc);
		}
	}

}
