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


import java.text.MessageFormat;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;

import com.ibm.iis.sappack.gen.common.ui.connections.IISConnection;
import com.ibm.iis.sappack.gen.common.ui.connections.IISConnectionRepository;
import com.ibm.iis.sappack.gen.tools.jobgenerator.api.RapidGenerator;
import com.ibm.iis.sappack.gen.tools.jobgenerator.api.RapidGeneratorException;
import com.ibm.iis.sappack.gen.tools.jobgenerator.api.RapidGeneratorSAPSettings;
import com.ibm.iis.sappack.gen.tools.jobgenerator.rgconf.RGConfiguration;
import com.ibm.iis.sappack.gen.tools.jobgenerator.rgconf.RGConfiguration.CONFIGURATION_TYPE;
import com.ibm.iis.sappack.gen.tools.sap.api.IProgressCallback;
import com.ibm.iis.sappack.gen.tools.sap.importer.RapidModelerImpl;
import com.ibm.is.sappack.gen.common.ui.Messages;


public class RapidGeneratorImpl implements RapidGenerator {
  	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	

	@Override
	public void generateJobs(//
			String rapidGeneratorConfiguration, //
			String sourceModel, //
			String targetModel, //
			RapidGeneratorSAPSettings settings, //
			String iisConnectionName, //
			String iisProject, //
			String iisFolder, //
			String jobNamePrefix, //
			boolean overwriteJobs,
			IProgressCallback progressCallback //
	) throws RapidGeneratorException {
		try {
			if (progressCallback == null) {
				progressCallback = RapidModelerImpl.createEmptyProgressCallback();
			}
			final IProgressMonitor iprogressMonitor = RapidModelerImpl.createDelegatingProgressMonitor(progressCallback);

			JobGenerationSettings genSettings = new JobGenerationSettings();

			IFile rgConfig = RapidModelerImpl.getFile(rapidGeneratorConfiguration);
			if (rgConfig == null) {
				throw new RapidGeneratorException(MessageFormat.format(Messages.RapidGeneratorImpl_0, rapidGeneratorConfiguration));
			}
			RGConfiguration rgconf = new RGConfiguration(rgConfig);

			IFile source = RapidModelerImpl.getFile(sourceModel);
			IFile target = RapidModelerImpl.getFile(targetModel);

			String sourceModelErrorMsg = MessageFormat.format(Messages.RapidGeneratorImpl_1, sourceModel);
			String targetModelErrorMsg = MessageFormat.format(Messages.RapidGeneratorImpl_2, targetModel);
			CONFIGURATION_TYPE type = rgconf.getConfigurationType();
			if (type == CONFIGURATION_TYPE.SAP_EXTRACT) {
				if (target == null) {
					throw new RapidGeneratorException(targetModelErrorMsg);
				}
			} else if (type == CONFIGURATION_TYPE.SAP_LOAD) {
				if (source == null) {
					throw new RapidGeneratorException(sourceModelErrorMsg);

				}
			} else if (type == CONFIGURATION_TYPE.MOVE) {
				if (source == null) {
					throw new RapidGeneratorException(sourceModelErrorMsg);
				}
				if (target == null) {
					throw new RapidGeneratorException(targetModelErrorMsg);
				}

			}
			genSettings.setSourceDBMFile(source);
			genSettings.setTargetDBMFile(target);

			IISConnection iisConn = IISConnectionRepository.getRepository().retrieveConnection(iisConnectionName);
			if (iisConn == null) {
				throw new RapidGeneratorException(MessageFormat.format(Messages.RapidGeneratorImpl_3, iisConnectionName));
			}
			Display displ = Display.getCurrent();
			boolean passwordEmpty = true;
			if (displ != null) {
				passwordEmpty = iisConn.ensurePasswordIsSet();
			} else {
				passwordEmpty = iisConn.getPassword() == null;
			}
			if (passwordEmpty) {
				throw new RapidGeneratorException(MessageFormat.format(Messages.RapidGeneratorImpl_4, iisConnectionName));
			}
			genSettings.setIisConnection(iisConn);
			
			if (!isValidString(iisProject)) {
				throw new RapidGeneratorException(Messages.RapidGeneratorImpl_5);
			}
			genSettings.setDsProject(iisProject);
			
			if (!isValidString(iisFolder)) {
				throw new RapidGeneratorException(Messages.RapidGeneratorImpl_6);
			}
			genSettings.setDsFolder(iisFolder);
			
			if (!isValidString(jobNamePrefix)) {
				throw new RapidGeneratorException(Messages.RapidGeneratorImpl_7);
			}
			genSettings.setJobNamePrefix(jobNamePrefix);
			
			
			genSettings.setObjectTypes(JobGenerationSettings.OBJECT_TYPE_ALL);
			genSettings.setOverwriteJob(overwriteJobs);

			JobGenerator generator = new JobGenerator(rgconf);
			generator.performJobGeneration(genSettings, iprogressMonitor);
		} catch(RapidGeneratorException exc) {
			throw exc;
		} catch (Exception exc) {
			throw new RapidGeneratorException(exc);
		}
	}
	
	boolean isValidString(String s) {
		if (s == null) {
			return false;
		}
		return !s.trim().isEmpty();
	}

}
