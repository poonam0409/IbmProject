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
// Module Name : com.ibm.is.sappack.dsstages.idoc.listener
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.dsstages.idoc.listener;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.is.sappack.dsstages.common.StageLogger;
import com.ibm.is.sappack.dsstages.common.impl.ConfigFile;
import com.ibm.is.sappack.dsstages.common.impl.DSSAPConnectionImpl;
import com.ibm.is.sappack.dsstages.idoc.listener.jobrunner.IDocCounter;
import com.ibm.is.sappack.dsstages.idoc.listener.jobrunner.IDocCounterImpl;
import com.ibm.is.sappack.dsstages.idoc.listener.util.IDocServerConstants;
import com.ibm.is.sappack.dsstages.idoc.listener.util.IDocServerMessages;

/**
 * IDocTypeConfigReader
 * 
 * Helper class to read the IDoc type
 * configuration files that are stored
 * within the 'IDocTypes' sub folders.
 * 
 * For example DEBMAS06.config for IDoc type DEBMAS06
 */
public class IDocTypeConfigReader {

	/* logger */
	private Logger logger = null;
	
	/* class name for logging purposes */
	static final String CLASSNAME = IDocTypeConfigReader.class.getName();
	
	static String copyright() {
		return com.ibm.is.sappack.dsstages.idoc.listener.Copyright.IBM_COPYRIGHT_SHORT;
	}
	
	/* singleton instance of IDocTypeConfigReader */
	private static IDocTypeConfigReader instance = null;
	
	/* IDoc type configuration keys */
	private final static String JOBNAME = "JOBNAME"; //$NON-NLS-1$
	private final static String PROJECTNAME = "PROJECTNAME"; //$NON-NLS-1$
	private final static String SELECT_JOB = "SELECT_JOB"; //$NON-NLS-1$
	private final static String AUTORUN_ENABLED = "AUTORUN_ENABLED"; //$NON-NLS-1$
	private final static String IDOC_COUNT = "IDOC_COUNT"; //$NON-NLS-1$
	private final static String USE_DEFAULT_LOGON = "USE_DEFAULT_LOGON"; //$NON-NLS-1$
	private final static String DSUSERNAME = "DSUSERNAME"; //$NON-NLS-1$
	private final static String DSPASSWORD = "DSPASSWORD"; //$NON-NLS-1$

	
	/**
	 * private constructor
	 */
	private IDocTypeConfigReader() {
		
		/* initialize logger */
		this.logger = StageLogger.getLogger();
	}
	
	/**
	 * getInstance
	 * 
	 * return the singleton instance of IDocTypeConfigReader
	 * 
	 * @return
	 */
	public static IDocTypeConfigReader getInstance() {
	
		if(instance == null) {
			return new IDocTypeConfigReader();
		} else {
			return instance;
		}
	}
	
	/**
	 * getIDocTypeConfigurations
	 * 
	 * read the IDocType configurations of all registered IDocTypes.
	 * The configuration of an IDocType is distributed to two different
	 * configuration files. 
	 * The base configuration of all IDocTypes is stored
	 * in a configuration files named 'IDocTypes.config'. This file is located
	 * in the default IDocTypes directory. IDocTypes.config also holds the path
	 * to the extended configuration file of each IDocType.
	 * The extended configuration file of each IDocType is named '$IDocTypeName.config'.
	 * 
	 * We have to read both configuration files of each each registered IDocType
	 * 
	 * 
	 * @param defaultIDocTypesDirectory
	 * @return
	 */
	public Map<String, IDocTypeConfiguration> getIDocTypeConfigurations(String defaultIDocTypesDirectory) {
	
		
		final String METHODNAME = "getIDocTypeConfigurations(String idocTypesDirectory)"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);
		
		/* Map that holds IDocTypeConfigurations with IDoc type names as keys - must be thread-safe */
		Map<String, IDocTypeConfiguration> idocTypeConfigMap = Collections.synchronizedMap(new HashMap<String, IDocTypeConfiguration>());
		
		/* read the IDocTypes.config file to get a list of all registered IDocTypes and
		 * the root directory on the file system of each IDocType. We need to keep in mind
		 * that IDocTypes may use a non-default directory on the file system
		 */
		String idocTypesConfigFile = defaultIDocTypesDirectory + IDocServerConstants.IDocTypesConfigFile;
		List<Map<String, String>> idocTypeBaseConfigs = new ArrayList<Map<String, String>>();
		try {
			this.logger.log(Level.FINE, "Reading IDoc type configurations from {0}", idocTypesConfigFile);
			idocTypeBaseConfigs = this.readIDocTypesConfig(idocTypesConfigFile);
		} catch (IOException e1) {
			/* configuration file IDocTypes.config could not be read - there's no way for us to continue*/
			this.logger.log(Level.SEVERE, IDocServerMessages.IDocTypesConfigFileReadError, defaultIDocTypesDirectory);
			this.logger.log(Level.SEVERE, e1.getMessage());
		}
		
		
		/* read IDoc type configuration files for each IDoc type contained in the List of IDocTypeBaseConfigs */
		for(int i=0; i<idocTypeBaseConfigs.size(); i++) {
			
			Map<String, String> idocTypeBaseConfig = idocTypeBaseConfigs.get(i);
			String idocType = idocTypeBaseConfig.get(IDocServerConstants.DSIDOCTYPES_NAME);
			try {
				IDocTypeConfiguration config = readExtendedIDocTypeConfig(defaultIDocTypesDirectory, idocTypeBaseConfig);
				/* bundle idocType name and configuration */
				idocTypeConfigMap.put(idocType, config);
			} catch (IOException e) {
				/* configuration file for IDoc type could not be read */
				this.logger.log(Level.WARNING, IDocServerMessages.IDocConfigurationFileReadError, idocType);
				this.logger.log(Level.WARNING, e.getMessage());
			}
			
		}
		
		this.logger.exiting(CLASSNAME, METHODNAME);
		return idocTypeConfigMap;
	}
	
	/**
	 * readIDocTypesConfig
	 * 
	 * read the IDocTypes.config file that stores information
	 * about all registered IDocTypes + the root directory of
	 * each IDocType on the file system
	 * 
	 * @param configFilePath
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	private List<Map<String, String>> readIDocTypesConfig(String configFilePath) throws IOException {
	
		final String METHODNAME = "readIDocTypesConfig(String configFilePath)"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);
		this.logger.log(Level.FINER, "Reading IDoc Types configuration file {0}", configFilePath);
		
		/* the return object - a List of Maps that holds the IDocType configuration of each registered IDocType */
		List<Map<String, String>> configurations = new ArrayList<Map<String, String>>();
		
		/* read the IDocTypes.config file */
		ConfigFile configFile = new ConfigFile(configFilePath);
		List<Object> content = configFile.getConfiguration();
		
		for(int i=0; i<content.size(); i++) {
			Object o = content.get(i);
			if(o instanceof ConfigFile.Entry) {
				ConfigFile.Entry entry = (ConfigFile.Entry) o;
				String key = entry.key;
				/* we're looking for the value of the 'DSIDOCTYPES' key */
				if(key.equals(IDocServerConstants.DSIDOCTYPES)) {
					Object value = entry.value;
					if(value instanceof List) {
						List idocTypes = (List) value;
						/* read configuration of each registered IDocType */
						for(int j=0; j<idocTypes.size(); j++) {
							Object idocTypeConfigEntries = idocTypes.get(j);
							/* idocTypeConfigurationEntries should be a List */
							if(idocTypeConfigEntries instanceof List) {
								List idocTypeConfigurationEntries = (List) idocTypeConfigEntries;
								/* create a Map thats holds the configuration entries of this IDocType */
								Map<String, String> idocTypeConfigMap = new HashMap<String, String>();
								/* read all configuration entries of this IDocType configuration */
								for(int k=0; k<idocTypeConfigurationEntries.size(); k++) {
									Object obj = idocTypeConfigurationEntries.get(k);
									/* process each ConfigFile.Entry separately */
									if(obj instanceof ConfigFile.Entry) {
										ConfigFile.Entry cfEntry = (ConfigFile.Entry) obj;
										idocTypeConfigMap.put(cfEntry.key, cfEntry.value.toString());
									}
								}
								/* add the configuration Map of this IDocType to our configurationMaps List */
								configurations.add(idocTypeConfigMap);
								
							} else {
								/* invalid IDocTypes.config file format */
								String msg = MessageFormat.format(IDocServerMessages.IDocTypesConfigFileFormatError, configFilePath);
								logger.log(Level.SEVERE, msg);
								throw new IOException(msg);
							}
							
						}
					} else {
						/* invalid IDocTypes.config file format */
						String msg = MessageFormat.format(IDocServerMessages.IDocTypesConfigFileFormatError, configFilePath);
						logger.log(Level.SEVERE, msg);
						throw new IOException(msg);
					}
				}
			}
		}
		
		this.logger.exiting(CLASSNAME, METHODNAME);
		return configurations;
	}
	
	
	/**
	 * readExtendedIDocTypeConfig
	 * 
	 * read the extended IDocType configuration
	 * of the IDocType specified in the given
	 * IDocType base configuration
	 * 
	 * @param defaultIDocTypesDirectory
	 * @param idocTypeBaseConfig
	 * @return
	 * @throws IOException 
	 */
	private IDocTypeConfiguration readExtendedIDocTypeConfig(String defaultIDocTypesDirectory, Map<String, String> idocTypeBaseConfig) throws IOException {
		
		final String METHODNAME = "readExtendedIDocTypeConfig(String defaultIDocTypesDirectory, Map<String, String> idocTypeBaseConfig)"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);
		
		/* get the name of the IDocType */
		String idocType = idocTypeBaseConfig.get(IDocServerConstants.DSIDOCTYPES_NAME);
		/* check if this IDocType is configured to use a non-default directory to store IDocs and configuration files */
		boolean useDefaultDirectory = Boolean.valueOf(idocTypeBaseConfig.get(IDocServerConstants.DSIDOCTYPES_USE_DEFAULT_PATH));
		StringBuffer idocTypeDirectory = new StringBuffer();
		String fileSeparator = System.getProperty("file.separator");
		if(useDefaultDirectory) {
			/* use default IDocType directory */
			idocTypeDirectory.append(defaultIDocTypesDirectory).append(DSSAPConnectionImpl.convertIDocTypeNameToFileName(idocType)).append(fileSeparator);
		} else {
			/* use non-default IDocType directory */
			idocTypeDirectory.append(idocTypeBaseConfig.get(IDocServerConstants.DSIDOCTYPES_IDOC_FILES_PATH)).append(fileSeparator);
		}
		String idocTypeDir = idocTypeDirectory.toString();
		this.logger.log(Level.FINER, "Directory for IDocType {0} is {1}", new Object[]{idocType, idocTypeDir});
		this.logger.log(Level.FINER, "Reading IDoc type configuration for {0}", idocType);
		
		/* create a new IDocTypeConfiguration for this IDoc type */
		IDocTypeConfigImpl config = new IDocTypeConfigImpl();
		config.setIdocType(idocType);
		config.setIDocTypeDirectory(idocTypeDir);
		
		/* create an IDocCounter for this IDoc type */
		IDocCounter idocCounter = new IDocCounterImpl();
		idocCounter.init(idocTypeDir, idocType);
		config.setIDocCounter(idocCounter);
		
		/* Assemble the path to the configuration file */
		String path = String.format("%s%s.config", new Object[]{idocTypeDir, DSSAPConnectionImpl.convertIDocTypeNameToFileName(idocType)});
		logger.log(Level.FINER, "Path to {0} config file: {1}", new Object[]{DSSAPConnectionImpl.convertIDocTypeNameToFileName(idocType), path});
		
		/* read configuration file content and get IDocTypeCofiguration values */
		ConfigFile configFile = new ConfigFile(path);
		this.logger.log(Level.FINER, "Reading config file {0}", path);
		List<Object> content = configFile.getConfiguration();
		for(int i=0; i<content.size(); i++) {
			Object o = content.get(i);
			if(o instanceof ConfigFile.Entry) {
				ConfigFile.Entry entry = (ConfigFile.Entry) o;
				String key = entry.key;
				String value = entry.value.toString();
				
				
				/* JOBNAME */
				if(key.equals(IDocTypeConfigReader.JOBNAME)) {
					logger.log(Level.FINEST, "{0} = {1}", new Object[]{key, value});
					config.setJobName(value);
					continue;
				}
				
				/* PROJECTNAME */
				if(key.equals(IDocTypeConfigReader.PROJECTNAME)) {
					logger.log(Level.FINEST, "{0} = {1}", new Object[]{key, value});
					config.setProjectName(value);
					continue;
				}
				
				/* DSPASSWORD */
				if(key.equals(IDocTypeConfigReader.DSPASSWORD)) {
					logger.log(Level.FINEST, "{0} = {1}", new Object[]{key, value});
					config.setDsPassword(value);
					continue;
				}
				
				/* SELECT_JOB */
				if(key.equals(IDocTypeConfigReader.SELECT_JOB)) {
					logger.log(Level.FINEST, "{0} = {1}", new Object[]{key, value});
					config.setSelectJob(Boolean.parseBoolean(value));
					continue;
				}
				
				/* AUTORUN_ENABLED */
				if(key.equals(IDocTypeConfigReader.AUTORUN_ENABLED)) {
					logger.log(Level.FINEST, "{0} = {1}", new Object[]{key, value});
					config.setAutorunEnabled(Boolean.parseBoolean(value));
					continue;
				}
				
				/* DSUSERNAME */
				if(key.equals(IDocTypeConfigReader.DSUSERNAME)) {
					logger.log(Level.FINEST, "{0} = {1}", new Object[]{key, value});
					config.setDsUsername(value);
					continue;
				}
				
				/* IDOC_COUNT */
				if(key.equals(IDocTypeConfigReader.IDOC_COUNT)) {
					logger.log(Level.FINEST, "{0} = {1}", new Object[]{key, value});
					config.setIdocCount(Integer.valueOf(value));
					continue;
				}
				
				/* USE_DEFAULT_LOGON */
				if(key.equals(IDocTypeConfigReader.USE_DEFAULT_LOGON)) {
					logger.log(Level.FINEST, "{0} = {1}", new Object[]{key, value});
					config.setUseDefaultLogon(Boolean.parseBoolean(value));
					continue;
				}
				
			}
		}
		logger.exiting(CLASSNAME, METHODNAME);
		
		return config;
	}
	

	/**
	 * IDocTypeConfigImpl
	 * 
	 * Implementation of an IDocTypeConfiguration.
	 * All instances should be created by the IDocTypeConfigurationReader only
	 *
	 */
	public class IDocTypeConfigImpl implements IDocTypeConfiguration {

		/* IDocType configuration properties */
		private String dsUsername;
		private String dsPassword;
		private String jobName;
		private String projectName;
		private String idocType;
		private boolean autorunEnabled = false;
		private boolean selectJob = false;
		private boolean useDefaultLogon = true;
		private int idocCount = 0;
		private IDocCounter idocCounter = null;
		private String idocTypeDirectory = "";
		
		
		/**
		 * private constructor to prevent other classes 
		 * from creating IDocTypeConfigImpl instances.
		 */
		private IDocTypeConfigImpl() {
			
		}
		
		private void setDsUsername(String dsUsername) {
			this.dsUsername = dsUsername;
		}



		private void setDsPassword(String dsPassword) {
			this.dsPassword = dsPassword;
		}



		private void setJobName(String jobName) {
			this.jobName = jobName;
		}



		private void setProjectName(String projectName) {
			this.projectName = projectName;
		}



		private void setIdocType(String idocType) {
			this.idocType = idocType;
		}



		private void setAutorunEnabled(boolean autorunEnabled) {
			this.autorunEnabled = autorunEnabled;
		}



		private void setSelectJob(boolean selectJob) {
			this.selectJob = selectJob;
		}



		private void setUseDefaultLogon(boolean useDefaultLogon) {
			this.useDefaultLogon = useDefaultLogon;
		}



		private void setIdocCount(int idocCount) {
			this.idocCount = idocCount;
		}
		
		private void setIDocCounter(IDocCounter idocCounter) {
			this.idocCounter = idocCounter;
		}

		private void setIDocTypeDirectory(String idocTypeDirectory) {
			this.idocTypeDirectory = idocTypeDirectory;
		}

		@Override
		public String getDSPassword() {
			return this.dsPassword;
		}

		@Override
		public String getDSUsername() {
			return this.dsUsername;
		}

		@Override
		public int getIDocCountThreshold() {
			return this.idocCount;
		}

		@Override
		public String getIDocType() {
			return this.idocType;
		}

		@Override
		public String getJobName() {
			return this.jobName;
		}

		@Override
		public String getProjectName() {
			return this.projectName;
		}

		@Override
		public boolean isAutorunEnabled() {
			return this.autorunEnabled;
		}

		@Override
		public boolean isSelectJob() {
			return this.selectJob;
		}

		@Override
		public boolean useDefaultLogon() {
			return this.useDefaultLogon;
		}

		@Override
		public IDocCounter getIDocCounter() {
			return this.idocCounter;
		}

		@Override
		public String getIDocTypDirectory() {
			return this.idocTypeDirectory;
		}
		
		
	}
	
}
