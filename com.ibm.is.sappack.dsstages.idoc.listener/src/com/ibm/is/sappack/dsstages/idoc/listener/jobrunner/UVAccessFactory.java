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
// Module Name : com.ibm.is.sappack.dsstages.idoc.listener.jobrunner
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.dsstages.idoc.listener.jobrunner;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.is.sappack.dsstages.common.StageLogger;
import com.ibm.is.sappack.dsstages.idoc.listener.util.ExecutableNameResolver;
import com.ibm.is.sappack.dsstages.idoc.listener.util.IDocServerMessages;

/**
 * UVAccessFactory
 * 
 * Factory for platform specific UVAccessors
 */
public class UVAccessFactory {

	/* logger */
	private Logger logger = null;
	
	/* class name for logging purposes */
	static final String CLASSNAME = UVAccessFactory.class.getName();

	static String copyright() {
		return com.ibm.is.sappack.dsstages.idoc.listener.jobrunner.Copyright.IBM_COPYRIGHT_SHORT;
	}
	
	/**
	 * constructor
	 */
	public UVAccessFactory() {
		/* initialize logger */
		this.logger = StageLogger.getLogger();
	}

	/**
	 * create and return a platform dependent UVAccess instance
	 * 
	 * @return
	 */
	public UVAccess createUVAccess(String uvHome) {
		
		final String METHODNAME = "createUVAccess(String uvHome)"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);

		/* assemble platform dependent command template */
		String fileSeparator = System.getProperty("file.separator");

		/* get platform dependent uvsh executable name */
		String uvsh = ExecutableNameResolver.getUVSHExecutableName();

		/* assemble the command String [] - has to be an array - we ran into issues on AIX when we used a command string instead of a string array */
		String [] command = new String[2];
		/* command[0] for example C:\\IBM\\InformationServer\\Server\\DSEngine\bin\\uvsh.exe */
		command[0] = String.format("%s%sbin%s%s", new Object[] { uvHome, fileSeparator, fileSeparator, uvsh });
		/* command[1] RUN BP IDOCLIST.B */
		command[1] = "RUN BP IDOCLIST.B";
		
		this.logger.log(Level.FINER, "Initializing UVAccess instance to run {0}", new Object[]{command[0], command[1]});
		
		this.logger.exiting(CLASSNAME, METHODNAME);
		
		return new UVAccessImpl(uvHome, command);
	}

	/**
	 * UVAccessImpl
	 * 
	 * UVAccess implementation to get the
	 * DataStage jobs that are registered
	 * for a specific IDoc type
	 * 
	 */
	public class UVAccessImpl implements UVAccess {

		/* logger */
		private Logger logger = null;
		
		/* class name for logging purposes */
		private final String CLASSNAME = UVAccessImpl.class.getName();
		
		/* working directory ($UVHOME) */
		private String workingDir = "";  //$NON-NLS-1$

		/* command array */
		private String [] commandArray = null;  //$NON-NLS-1$

		/* pass token for UV return values */
		private final static String PASSTOKEN = "Passed:";  //$NON-NLS-1$
		
		/* token that separates the various results */
		private final static String RESULTTOKEN = "|"; //$NON-NLS-1$
		
		/* token that separates project name and job name */
		private final static String PROJECTJOBNAMETOKEN = "."; //$NON-NLS-1$

		/**
		 * UVAccessImpl
		 * 
		 * UVAccessImpl should be created by UVAccessFactory only
		 * 
		 * @param workingDir
		 * @param commandTemplateArray
		 */
		private UVAccessImpl(String workingDir, String [] commandTemplateArray) {
			
			/* initialize logger */
			this.logger = StageLogger.getLogger();

			this.workingDir = workingDir;
			this.commandArray = commandTemplateArray;
		}

		/**
		 * assembleJobList
		 * 
		 * create and return a list of DSJob objects
		 * filled based on the given uvOutput String.
		 * 
		 * uvOutput typically looks like this:
		 * DEMO.IDocExtractDemo|dstage1.IDocExtract
		 * 
		 * we first have to split the String into the several
		 * projectname.jobname combinations:
		 * 
		 * DEMO.IDocExtractDemo
		 * dstage1.IDocExtract
		 * 
		 * Then we have to split the projectname.jobname
		 * combination into project name and job name
		 * 
		 * DEMO IDocExtractDemo
		 * dstage1 IDocExtract
		 * 
		 * 
		 * @param uvOutput
		 * @return
		 */
		private List<DSJob> assembleJobList(String uvOutput) {
			
			final String METHODNAME = " assembleJobList(String uvOutput)"; //$NON-NLS-1$
			this.logger.entering(CLASSNAME, METHODNAME);
			
			this.logger.log(Level.FINER, "Assembling DSJob list for UV output String {0}", uvOutput);
			
			List<DSJob> dsJobList = new ArrayList<DSJob>();
			
			/* projectName.jobName entries are separated by a '|' character */
			StringTokenizer resultTokenizer = new StringTokenizer(uvOutput, RESULTTOKEN);
			
			/* StringTokenizer to split projectname from jobname */
			StringTokenizer projectJobNameTokenizer = null;
			
			/* split by '|' */
			while(resultTokenizer.hasMoreTokens()) {
				String token = resultTokenizer.nextToken();
				projectJobNameTokenizer = new StringTokenizer(token, PROJECTJOBNAMETOKEN);
				
				/* split by '.' */
				String projectName = projectJobNameTokenizer.nextToken();
				String jobName = projectJobNameTokenizer.nextToken();
				
				/* create a DSJob instance for this token */
				this.logger.log(Level.FINEST, "Creating DSJob instance with project name {0} and job name {1}", new Object[]{projectName, jobName});
				DSJob dsJob = new DSJob(projectName, jobName);
				dsJobList.add(dsJob);
			}
			
			
			this.logger.log(Level.FINEST, "{0} DSJob instances have been created", dsJobList.size());
			this.logger.exiting(CLASSNAME, METHODNAME);
			return dsJobList;
		}
		
		
		@Override
		/**
		 * getJobList
		 * 
		 * return a list of DSJob objects that contains the project names
		 * and job names of all DataStage jobs that are registered for 
		 * the given IDocType and the given DataStage SAP connection
		 */
		public List<DSJob> getJobList(String idocType, String dsSAPConnection) {

			final String METHODNAME = "getJobList(String idocType, String dsSAPConnection)"; //$NON-NLS-1$
			this.logger.entering(CLASSNAME, METHODNAME);
			
			this.logger.log(Level.FINER, "Trying to get DataStage job list from UV database for IDoc type {0} and DataStage SAP connection {1}", new Object[]{idocType, dsSAPConnection});
			
			InputStreamReader isr = null;
			BufferedReader br = null;
			
			try {
				File file = new File(this.workingDir);
				Runtime runtime = Runtime.getRuntime();

				/* fill command template with idocType and SAP connection name */
				this.commandArray[1] = String.format("%s %s \"%s\"", new Object[] { this.commandArray[1], idocType, dsSAPConnection });
				/* run command */
				this.logger.log(Level.FINER, "Running {0}{1} in working directory {2}", new Object[]{this.commandArray[0], this.commandArray[1], file});
				
				
				Process process = runtime.exec(this.commandArray, null, file);

				/* typical output looks like this:
				 * 
				 * Illegal flavor keyword, IDEAL flavor DataStage assumed
				 * Passed:dstage1.IDocExtract|
				 * 
				 * 
				 * registered jobs are identified by $projectName.$JobName
				 * and separated by a '|' character
				 */

				/* check results */
				InputStream is = process.getInputStream();
				isr = new InputStreamReader(is);
				br = new BufferedReader(isr);
				String line;

				/* search for the output line that starts with the PASSTOKEN */
				while ((line = br.readLine()) != null) {
					logger.log(Level.FINER, "UV command returned: {0}", line);
					if(line.startsWith(PASSTOKEN)) {
						String results = line.substring(PASSTOKEN.length(), line.length());
						logger.exiting(CLASSNAME, METHODNAME);
						return this.assembleJobList(results);
					}
				}

			} catch (Exception e) {
				/* we were not able to get the job list */
				String msg = e.getMessage();
				this.logger.severe(msg);
				throw new RuntimeException(msg);
			} finally {
				if (br != null) {
					try {
	               br.close();
               }
               catch (IOException e) {
               	this.logger.finer(e.getMessage());
               } finally {
               	if (isr != null) {
               		try {
	                     isr.close();
                     }
                     catch (IOException e) {
                     	this.logger.finer(e.getMessage());
                     }
               	}
               }
				}
			}
			/* PASSTOKEN could not be found in UV command output */
			logger.log(Level.WARNING, IDocServerMessages.UVPASSTOKENNOTFOUND, new Object[]{PASSTOKEN, this.commandArray[0],this.commandArray[1]});
			/* return an empty list */
			return new ArrayList<DSJob>();
		}
	
	}
}
