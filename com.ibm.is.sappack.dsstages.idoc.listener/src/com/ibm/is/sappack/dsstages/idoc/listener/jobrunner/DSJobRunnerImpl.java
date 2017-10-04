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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.is.sappack.dsstages.common.StageLogger;
import com.ibm.is.sappack.dsstages.common.Utilities;
import com.ibm.is.sappack.dsstages.idoc.listener.IDocTypeConfiguration;
import com.ibm.is.sappack.dsstages.idoc.listener.util.ExecutableNameResolver;
import com.ibm.is.sappack.dsstages.idoc.listener.util.IDocServerConstants;
import com.ibm.is.sappack.dsstages.idoc.listener.util.IDocServerMessages;

/**
 * DSJobRunnerImpl
 * 
 * Implementation of a DSJobRunner to start DataStage jobs
 * using the dsjob command
 */
public class DSJobRunnerImpl implements DSJobRunner {

	
	/* logger */
	private Logger logger = null;
	
	/* IDoc Listener properties */
	private Map<String, String> idocServerProperties = null;
	
	/* IDoc type properties */
	private Map<String, IDocTypeConfiguration> idocTypeConfigurations = null;
	

	public DSJobRunnerImpl() {
		/* initialize logger */
		this.logger = StageLogger.getLogger();
	}
	
	@Override
	public void initialize(Map<String, String> idocServerProperties, Map<String, IDocTypeConfiguration> idocTypeConfigurations) {
		
		this.idocServerProperties = idocServerProperties;
		this.idocTypeConfigurations = idocTypeConfigurations;
	}

	@Override
	/**
	 * runDataStageJobs
	 * 
	 * run multiple DataStage jobs that are
	 * registered for the given IDocType
	 * 
	 * @param idocType
	 * @param jobs
	 */
	public void runDataStageJobs(String idocType, List<DSJob> jobs) {

		logger.log(Level.FINE, "Starting {0} DataStage jobs", jobs.size());
		
		for(int i=0; i<jobs.size(); i++) {
			/* delete call to runDataStageJob */
			this.runDataStageJob(idocType, jobs.get(i));
		}
	}
	
	/**
	 * assembleUserNameAndPasswordCommandLine
	 * 
	 * assemble a command line statement using
	 * -user and -password flags
	 * 
	 * for example:
	 * 
	 *  /opt/ibm/InformationServer/Server/DSEngine/bin/dsjobdsjob  -domain charon42:9080 -user isadmin 
	 *  -password pwd1 -server CHARON42 -run dstage1 DEBMAS06Extract
	 * 
	 * @param idocTypeConfig
	 * @param job
	 * @return
	 */
	private String assembleUserNameAndPasswordCommandLine(IDocTypeConfiguration idocTypeConfig, DSJob job) {
		
		/* assemble the command line statement */
		String fileSep = System.getProperty("file.separator");
		StringBuffer cmd = new StringBuffer();
		
		/* add DSEngine path */
		cmd.append(this.idocServerProperties.get(IDocServerConstants.UVHOME)).append(fileSep);
		/* add bin directory */
		cmd.append("bin").append(fileSep);
		/* get the platform dependent name of the 'dsjob' executable */
		cmd.append(ExecutableNameResolver.getDSJobExecutableName());
		/* append domain name if we have one specified */
		String domainName = this.idocServerProperties.get(IDocServerConstants.ISDOMAINNAME);
		if(!domainName.equals(IDocServerConstants.EMPTYSTRING)) {
			/* use specified domain name */
			cmd.append(" -domain ").append(domainName);
		} else {
			/* use localhost as domain name */
			cmd.append(" -domain localhost ");
		}
		
		/* DataStage user name and password */
		String username = "";
		String password = "";
		
		/* check if we are using the default user name and password */
		if(idocTypeConfig.useDefaultLogon()) {
			
			/* append default user name */
			username = this.idocServerProperties.get(IDocServerConstants.DSUSERNAME);
			cmd.append(" -user ").append(username);
			
			/* append default user password */
			password = Utilities.convertConfigFilePW(this.idocServerProperties.get(IDocServerConstants.DSPASSWORD));
			cmd.append(" -password ").append(password);
			
		} else {
			
			/* append IDoc type specific user name */
			username = idocTypeConfig.getDSUsername();
			cmd.append(" -user ").append(username);
			
			/* append IDoc type specific user password */
			password = Utilities.convertConfigFilePW(idocTypeConfig.getDSPassword());
			cmd.append(" -password ").append(password);
		}
		
		/* append DataStage server name */
		cmd.append(" -server ").append(this.idocServerProperties.get(IDocServerConstants.DSSERVERNAME));
		
		/* append -run -wait $project name $job name */
		String jobName = job.getJobName();
		String projectName = job.getProjectName(); 
		cmd.append(" -run -wait ").append(projectName).append(" ").append(jobName);
		
		String command = cmd.toString();
		/* do not log password in clear text */
		String logCommand = this.hidePassword(command);
		logger.log(Level.INFO, IDocServerMessages.RunDSJob, new Object[]{jobName, username, projectName});
		logger.log(Level.INFO, logCommand);
		
		return command;
	}
	
	/**
	 * assembleFileBasedCommandLine
	 * 
	 * assemble a command line statement using
	 * the -file flag
	 * 
	 * for example:
	 * 
	 * /opt/ibm/InformationServer/Server/DSEngine/bin/dsjob -file /tmp/PASSWORDFILE 
	 * charon42:9080 CHARON42 -run dstage1 DEBMAS06Extract
	 * 
	 * @param idocTypeConfig
	 * @param job
	 * @param passwordFilePath
	 * @return
	 */
	private String assembleFileBasedCommandLine(IDocTypeConfiguration idocTypeConfig, DSJob job, String passwordFilePath) {
		
		/* assemble the command line statement */
		String fileSep = System.getProperty("file.separator");
		StringBuffer cmd = new StringBuffer();
		
		/* add DSEngine path */
		cmd.append(this.idocServerProperties.get(IDocServerConstants.UVHOME)).append(fileSep);
		/* add bin directory */
		cmd.append("bin").append(fileSep);
		/* get the platform dependent name of the 'dsjob' executable */
		cmd.append(ExecutableNameResolver.getDSJobExecutableName());
		/* append -file flag */
		cmd.append(" -file ").append(passwordFilePath);
		/* append domain name (but without the -domain flag) if we have one specified */
		String domainName = this.idocServerProperties.get(IDocServerConstants.ISDOMAINNAME);
		if(!domainName.equals(IDocServerConstants.EMPTYSTRING)) {
			/* use specified domain name */
			cmd.append(IDocServerConstants.BLANK).append(domainName);
		} else {
			/* use localhost as domain name */
			cmd.append(" localhost ");
		}
		/* append DataStage server name without -server flag*/
		cmd.append(IDocServerConstants.BLANK).append(this.idocServerProperties.get(IDocServerConstants.DSSERVERNAME));
		/* append -run -wait $project name $job name */
		String jobName = job.getJobName();
		String projectName = job.getProjectName(); 
		cmd.append(" -run -wait ").append(projectName).append(" ").append(jobName);
		
		String command = cmd.toString();
		
		/* log the command line */
		logger.log(Level.INFO, IDocServerMessages.RunDSJobWithFile, new Object[]{jobName, projectName});
		logger.log(Level.INFO, command);
		
		return command;
	}
	
	/**
	 * assemble the command line statement
	 * for the dsjob command
	 * 
	 * @param idocTypeConfig
	 * @param job
	 * @return
	 */
	private String assembleCommandLine(IDocTypeConfiguration idocTypeConfig, DSJob job) {
				
		/* check if we need to use a password file */
		String passwordFilePath = this.idocServerProperties.get(IDocServerConstants.PASSWORDFILE).trim();
		
		if(!passwordFilePath.equals(IDocServerConstants.EMPTYSTRING)) {
			
			/* create command line with -file flag */
			return this.assembleFileBasedCommandLine(idocTypeConfig, job, passwordFilePath);
			
		} else {
			
			/* create command line with -user and -password flags */
			return this.assembleUserNameAndPasswordCommandLine(idocTypeConfig, job);
		}
	}
	
	@Override
	/**
	 * runDataStageJob
	 * 
	 * run a single DataStage job
	 * 
	 * @param idocType
	 * @param job
	 */
	public void runDataStageJob(String idocType, DSJob job) {
		
		/* get IDoc type configuration for the given IDoc type */
		IDocTypeConfiguration idocTypeConfig = this.idocTypeConfigurations.get(idocType);
		
		/* assemble the command line statement for dsjob */
		String cmd = this.assembleCommandLine(idocTypeConfig, job);
		
		/* run DataStage job in background Thread */
		BackgroundJob backgroundJob = new BackgroundJob(cmd);
		new Thread(backgroundJob).start();
	}
	
	/**
	 * BackgroundJob
	 * 
	 * Helper class to run DataStage jobs 
	 * in a background Thread
	 *
	 */
	private class BackgroundJob implements Runnable {
		
		/* logger */
		private Logger logger = null;
		
		/* command line */
		private String command = "";

		/**
		 * BackgroundJob
		 * 
		 * @param cmd
		 */
		private BackgroundJob(String cmd) {
			
			this.command = cmd;
			this.logger = StageLogger.getLogger();
		}
		
		@Override
		/**
		 * run DataStage job in background Thread
		 */
		public void run() {
			
			BufferedReader reader = null;
			
			try {
				/* start dsjob in a background Thread */
				Process p = Runtime.getRuntime().exec(this.command);
				/* wait for dsjob to finish */
				p.waitFor();
				
				/* check output of dsjob for errors */
				InputStream errorStream = p.getErrorStream();
				reader = new BufferedReader(new InputStreamReader(errorStream));
				String error = IDocServerConstants.EMPTYSTRING;
				
				this.logger.info(hidePassword(this.command) + " returned: ");
				while((error = reader.readLine()) != null) {
					/* write dsjob errors to log */
					this.logger.warning(error);
				}
				
			} catch (IOException e) {
				logger.severe(e.getMessage());
			} catch (InterruptedException e) {
				logger.severe(e.getMessage());
			} finally {
				if (reader != null) {
					try {
	               reader.close();
               }
               catch (IOException e) {
      				logger.finer(e.getMessage());
               }
				}
			}
			
		}
		
	}
	
	/**
	 * hidePassword
	 * 
	 * replace the password in the
	 * given command line String with
	 * stars
	 * 
	 * @param command
	 * @return
	 */
	private String hidePassword(String command) {
		
		/* check if command line contains -password flag */
		int start = command.indexOf("-password");
		if(start > -1) {
			/* get start and end index of the password */
			int startOfPassword = command.indexOf(IDocServerConstants.BLANK, start);
			int endOfPassword = command.indexOf(IDocServerConstants.BLANK, startOfPassword+1);
			String password = command.substring(startOfPassword+1, endOfPassword);
			
			return command.replaceAll("-password " + password, "-password *******");
			
		} else {
			
			/* command line does not contain -password flag */
			return command;
		}
	}
	
	
	static String copyright() {
		return com.ibm.is.sappack.dsstages.idoc.listener.jobrunner.Copyright.IBM_COPYRIGHT_SHORT;
	}

}
