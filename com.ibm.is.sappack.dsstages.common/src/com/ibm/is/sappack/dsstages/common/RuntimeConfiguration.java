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
// Module Name : com.ibm.is.sappack.dsstages.common
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.dsstages.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class provides access to additional settings changing the behaviour of
 * the runtime. For instance, this could contain special flags for debugging or
 * alternative behaviour. Typically, everything we did with environment variable
 * switches in the previous releases should go in here.
 * 
 * Note that when calling a get...() method the value will be fetched from the
 * environment every time, so don't call these functions in busy loops.
 * 
 * !!!!!!! Add a new entry to the supportedEnvVars field if you add a new parameter
 * to the configuration !!!!!!!!
 * 
 */
public class RuntimeConfiguration {

	static String copyright() {
		return com.ibm.is.sappack.dsstages.common.Copyright.IBM_COPYRIGHT_SHORT;
	}
	
	// this array contains string arrays with two members:
	// 1. environment variable Name
	// 2. env var description
	@SuppressWarnings("nls")
	static String[] supportedEnvVars = new String[] {
			Constants.ENV_VAR_DSSAP_TRACE + ": Switch on / off trace. Values should be one of FINE or FINEST.",
			Constants.ENV_VAR_DSSAP_TRACE_DIR + ": Absolute path to the directory where trace files are written. Multiple files with the .1, .2, etc. suffix might be created for multiple processes",
			Constants.ENV_VAR_DSSAP_IDOC_LOAD_SIMULATED_MODE + ": If set to 1, it will run IDoc load jobs in \"simulated mode\", i.e. IDocs will not be sent to SAP. Use for debugging only.",
			Constants.ENV_VAR_DSSAP_IDOC_LOAD_VALIDATION_WARNING_IS_ERROR + ": If set to 1, aborts an IDoc load job immediately if a validation warning occurs",
			Constants.ENV_VAR_DSSAP_IDOC_LOAD_SEGMENT_COLLECTION_MODE + ": [Advanced] Determines how segments are stored intermediately before sending them to SAP. Possible values are\n"
					+ "          " + Constants.ENV_VAR_VALUE_DSSAP_IDOC_LOAD_SEGMENT_COLLECTION_MODE_INMEM + ": collect segments in-memory\n" + "          "
					+ Constants.ENV_VAR_VALUE_DSSAP_IDOC_LOAD_SEGMENT_COLLECTION_MODE_FIXEDFILES + ": collect segments in a fixed set of files (see also "
					+ Constants.ENV_VAR_DSSAP_IDOC_LOAD_SEGMENT_COLLECTION_MODE_FIXEDFILES_FILENUM + ")\n" + "          "
					+ Constants.ENV_VAR_VALUE_DSSAP_IDOC_LOAD_SEGMENT_COLLECTION_MODE_VARIABLEFILES + ": collect segments in a variable/growing set of files (see also "
					+ Constants.ENV_VAR_VALUE_DSSAP_IDOC_LOAD_SEGMENT_COLLECTION_MODE_VARIABLEFILES + ")",
			Constants.ENV_VAR_DSSAP_IDOC_LOAD_SEGMENT_COLLECTION_MODE_FIXEDFILES_FILENUM + ": Number of files to be used for the "
					+ Constants.ENV_VAR_VALUE_DSSAP_IDOC_LOAD_SEGMENT_COLLECTION_MODE_FIXEDFILES + " segment collector",
			Constants.ENV_VAR_DSSAP_IDOC_LOAD_SEGMENT_COLLECTION_MODE_VARIABLEFILES_MAX_IDOCS_PER_FILE + ": maximum number of IDocs to be stored within one file for the "
					+ Constants.ENV_VAR_VALUE_DSSAP_IDOC_LOAD_SEGMENT_COLLECTION_MODE_VARIABLEFILES + " segment collector",
			Constants.ENV_VAR_DSSAP_IDOC_LOAD_FILES_DELETE_AT_JOB_START + ": If set to 1, all IDoc files from previous file-based IDoc load job runs will be deleted.",
			Constants.ENV_VAR_DSSAP_IDOC_LOAD_FILES_CREATE_UNICODE_FILEPORT + ": If set to 1, all IDoc load files will be prepared for processing by a UNICODE file port",
			Constants.ENV_VAR_DSSAP_IDOC_LOAD_FILES_CHUNK_SIZE + ": Determines how many IDocs are stored in an IDoc load file, defaults to the IDoc load package size.", 
			Constants.ENV_VAR_DS_PX_RESET + ": If set to 1, all auxiliary files of an IDoc extract job will be reset upon restart of the job.", 
	      Constants.ENV_VAR_ENABLE_PARALLEL_IDOC_EXTRACT + ": If set to 1, IDoc extract jobs will run in parallel mode.",		
	      Constants.ENV_VAR_DSSAP_IDOC_LOAD_MINIMUM_MESSAGES + ": If set to 1, only a summary message containing the number of IDocs and transactions is written to the log."		
	};

	private RuntimeConfiguration() {
	}

	static RuntimeConfiguration config = new RuntimeConfiguration();

	public static synchronized final RuntimeConfiguration getRuntimeConfiguration() {
		if (config == null) {
			config = new RuntimeConfiguration();
		}
		return config;
	}

//	public boolean isTraceEnabled() {
//		return getBooleanValue(Constants.ENV_VAR_DSSAP_TRACE, false);
//	}

	public String getSAPPacksTraceDirectory() {
		String traceFile = System.getenv(Constants.ENV_VAR_DSSAP_TRACE_DIR);
// traceFile = "C:\\temp\\dssaptrace-hansx";		
		return traceFile;
	}

	public Level getSAPPacksTraceLevel() {
		String traceLevel = System.getenv(Constants.ENV_VAR_DSSAP_TRACE);
// Level hansx = Level.FINEST;
// if (hansx != null) return(hansx);
		Level l = null;
		try {
			l = Level.parse(traceLevel);
		} catch (Exception exc) {
			l = null;
		}
		return l;
	}

	public boolean getIDocLoadSimulatedMode() {
		return getBooleanValue(Constants.ENV_VAR_DSSAP_IDOC_LOAD_SIMULATED_MODE, false);
	}

	// this one is needed for testing only (see junit testcases)
	// TODO: remove prior to shipment
	public static String overwrittenDSSAPHome = null;

	public String getDSSAPHOME() {
		if (RuntimeConfiguration.overwrittenDSSAPHome != null) {
			return RuntimeConfiguration.overwrittenDSSAPHome;
		}
		String dsSAPHome ="C:\\Users\\DSSAPConnections_folder_windows";// System.getenv(Constants.ENV_VAR_DSSAPHOME);
		return dsSAPHome;
	}

	public boolean treatIDocLoadValidationWarningsAsErrors() {
		return getBooleanValue(Constants.ENV_VAR_DSSAP_IDOC_LOAD_VALIDATION_WARNING_IS_ERROR, false);
	}

	public String getSegmentCollectionMode() {
		String var = System.getenv(Constants.ENV_VAR_DSSAP_IDOC_LOAD_SEGMENT_COLLECTION_MODE);
		if (var == null) {
			var = Constants.ENV_VAR_VALUE_DSSAP_IDOC_LOAD_SEGMENT_COLLECTION_MODE_DEFAULT;
		}
		return var;
	}

	static boolean getBooleanValue(String envVarName, boolean defaultValue) {
		String s = System.getenv(envVarName);
		if (s == null) {
			return defaultValue;
		}
		if (Constants.ENV_VAR_VALUE_ON.equals(s)) {
			return true;
		}
		return false;
	}

	static Integer getPositiveIntegerEnvVar(String envVarName) {
		String s = System.getenv(envVarName);
		if (s == null) {
			return null;
		}
		Logger logger = StageLogger.getLogger();
		Integer i = null;
		try {
			i = Integer.parseInt(s);

			if (i.intValue() > 0) {
				return i;
			} 
		}
		catch (NumberFormatException exc) {
			;
		}
		
		logger.log(Level.CONFIG, "CC_IDOC_EnvVarNotPositiveNumeric", new Object[] { envVarName, s } ); //$NON-NLS-1$
		return null;
	}

	public int getFixedFilesSegmentCollectionMode_FileNumber() {
		Integer i = getPositiveIntegerEnvVar(Constants.ENV_VAR_DSSAP_IDOC_LOAD_SEGMENT_COLLECTION_MODE_FIXEDFILES_FILENUM);
		if (i == null) {
			return 10;
		}
		return i.intValue();
	}

	public int getVariableFilesSegmentCollectionMode_MaximumNumberOfIDocsPerFile() {
		Integer i = getPositiveIntegerEnvVar(Constants.ENV_VAR_DSSAP_IDOC_LOAD_SEGMENT_COLLECTION_MODE_VARIABLEFILES_MAX_IDOCS_PER_FILE);
		if (i == null) {
			return 500;
		}
		return i.intValue();
	}

	public boolean getDeleteAllIDocFiles() {
		return getBooleanValue(Constants.ENV_VAR_DSSAP_IDOC_LOAD_FILES_DELETE_AT_JOB_START, true);
	}

	public boolean getCreateUnicodeFilePortIDocFiles() {
		return getBooleanValue(Constants.ENV_VAR_DSSAP_IDOC_LOAD_FILES_CREATE_UNICODE_FILEPORT, false);
	}

	public int getIDocFilesChunkSize() {
		int res = -1;
		Integer chunkSize = getPositiveIntegerEnvVar(Constants.ENV_VAR_DSSAP_IDOC_LOAD_FILES_CHUNK_SIZE);

		if (chunkSize != null) {
			if (chunkSize > 0) {
				res = chunkSize.intValue();
			}
		}

		return res;
	}
	
	public boolean getIDocExtractParallelismEnabled() {
		return getBooleanValue(Constants.ENV_VAR_ENABLE_PARALLEL_IDOC_EXTRACT, false);
	}
	
	public boolean getPXReset() {
		return getBooleanValue(Constants.ENV_VAR_DS_PX_RESET, false);
	}

	public boolean getMinimumMessage() {
		return getBooleanValue(Constants.ENV_VAR_DSSAP_IDOC_LOAD_MINIMUM_MESSAGES, false);
	}
	
	Properties buildProps = null;

	protected String getBuildProperty(String propName) {
		if (buildProps == null) {
			InputStream is = this.getClass().getClassLoader().getResourceAsStream("com/ibm/is/sappack/dsstages/common/build/dsstagesBuildID.properties"); //$NON-NLS-1$
			Properties prop = new Properties();
			try {
				prop.load(is);
			} catch (IOException e) {
				return null;
			}
			this.buildProps = prop;
		}
		return buildProps.getProperty(propName);
	}

	public String getBuildNumber() {
		return getBuildProperty("build.number"); //$NON-NLS-1$
	}

	public String getBuildDate() {
		return getBuildProperty("build.date"); //$NON-NLS-1$
	}
	
	public String getPatchLabel() {
		return getBuildProperty("patch.label"); //$NON-NLS-1$
	}

	// This class can be called to print out description of all environment variables
	// supported by this version
	@SuppressWarnings("nls")
	public static void main(String[] args) {
		PrintStream out = System.out;
		System.out.println("sghjagdujasytydiu");
		RuntimeConfiguration rc = 	RuntimeConfiguration.config;
		out.println("rc    "+	RuntimeConfiguration.config.getBuildDate());
	
		out.println("IBM InfoSphere Information Server Pack for SAP Applications Version 7");
		out.println("========================================================");
		RuntimeConfiguration conf = getRuntimeConfiguration();
		String buildNum = conf.getBuildNumber();
		String buildDate = conf.getBuildDate();
		String patchLabel = conf.getPatchLabel();
		out.println("Build ID: " + buildNum + ", build date: " + buildDate);
		out.println("Patch Label: " + patchLabel);
		out.println();
		out.println("Supported environment variables: ");
		for (String envVarEntry : supportedEnvVars) {
			out.println(envVarEntry);
		}

	}

}
