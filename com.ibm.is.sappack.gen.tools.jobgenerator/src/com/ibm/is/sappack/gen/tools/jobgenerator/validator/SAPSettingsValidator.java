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
// Module Name : com.ibm.is.sappack.gen.tools.jobgenerator.validator
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.tools.jobgenerator.validator;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

import com.ibm.iis.sappack.gen.common.ui.connections.IISConnection;
import com.ibm.iis.sappack.gen.common.ui.connections.SapSystem;
import com.ibm.iis.sappack.gen.tools.jobgenerator.rgconf.ABAPExtractSettingsPage;
import com.ibm.iis.sappack.gen.tools.jobgenerator.rgconf.RGConfiguration;
import com.ibm.is.sappack.gen.common.Constants;
import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.tools.jobgenerator.wizard.helpers.ParameterResolver;

/**
 * SAPSettingsValidator
 *
 * Validate the SAP related configuration settings in a given
 * RGConfiguration against an SAP system that is specified in the RGConfiguration
 */
public class SAPSettingsValidator implements IRunnableWithProgress{

	/* the validators */
	private List<IValidator> validators;
	
	/* the validation results */
	private List<IValidationResult> validationResults;
	
	/* Rapid Generator configuration */
	private RGConfiguration configuration;
	
	/* Information Server connection */
	private IISConnection iisConnection;
	
	/* Parameter Resolver */
	private ParameterResolver pr;
	
	static String copyright() {
		return com.ibm.is.sappack.gen.tools.jobgenerator.validator.Copyright.IBM_COPYRIGHT_SHORT;
	}
	
	/**
	 * SAPSettingsValidator
	 * 
	 * @param configuration
	 * @param iisConnection
	 * @param parameterResolver
	 */
	public SAPSettingsValidator(RGConfiguration configuration, IISConnection iisConnection, ParameterResolver parameterResolver) {
		
		this.configuration = configuration;
		this.validationResults = new ArrayList<IValidationResult>();
		this.iisConnection = iisConnection;
		this.validators = new ArrayList<IValidator>();
		this.pr = parameterResolver;
		
	}

	
	/**
	 * validate
	 * 
	 * entry point to run all validations
	 * 
	 * @param monitor
	 */
	private void validate(IProgressMonitor monitor) {
		
		
		/* read rapid generator configuration */
		String sapConnection = pr.resolveParameter(this.configuration.getSourceSAPSystem().getSAPSystemName());
		String sapUser = pr.resolveParameter(this.configuration.getSourceSAPSystem().getSAPUserName());
		String sapPW = pr.resolveParameter(this.configuration.getSourceSAPSystem().getSAPPassword());
		String sapClient = pr.resolveParameter(this.configuration.getSourceSAPSystem().getSAPClientNumber());
		String sapLanguage = pr.resolveParameter(this.configuration.getSourceSAPSystem().getSAPUserLanguage());
		String sapGWHost = pr.resolveParameter(this.configuration.getGWHost());
		String sapGWService = pr.resolveParameter(this.configuration.getGWService());
		boolean generateRFCDestinationNames = this.configuration.getGenerateRFCDestinationNames();
		boolean createRFCDestination = this.configuration.getCreateRFCDestinationAutomatically();
		boolean deleteRFCDestination = configuration.getDeleteExistingRFCDestination();
		String rfcDestPrefix = this.configuration.getRFCDestinationNameGeneratorPrefix();
		
		/* read IIS credentials */
		String isDomain = this.iisConnection.getDomain();
		int isPort = this.iisConnection.getDomainServerPort();
		String isUser = this.iisConnection.getUser();
		String isPassword = this.iisConnection.getPassword();
		String isProject = Constants.FIRST_DS_PROJECT;
		
		/* use DataStageSAPConnectionValidator to validate SAP connection and get an SapSystem object */
		DataStageSAPConnectionValidator dsSAPConnectionValidator = new DataStageSAPConnectionValidator(new ValidationResult(), sapConnection, isDomain, isPort, isUser, isPassword, isProject);
		this.validationResults.add(dsSAPConnectionValidator.validate(null, monitor));
		
		SapSystem sapSystem = dsSAPConnectionValidator.getSapSystem();
		
		/* check if client ID is numeric */
		try {
			Integer.parseInt(sapClient);
		} catch (Exception e) {
			/* stop validation is client id is not numeric */
			this.validationResults.add(new ValidationResult(Messages.AbapExtractJobSapSettingsValidator_0, sapClient, Messages.SapSystemWizardPage_23, Status.FAILURE));
			return;
		}
		
		/* only continue if SAP connection exists on IIS server */
		if(sapSystem != null) {
			
			/* update SapSystem default values */
			sapSystem.setClientId(Integer.parseInt(sapClient));
			sapSystem.setUsername(sapUser);
			sapSystem.setPassword(sapPW);
			sapSystem.setUserLanguage(sapLanguage);
			
			/* validate connection to SAP application server */
			IValidator sapAppServerValidator = new SapAppServerConnectionValidator();
			this.validators.add(sapAppServerValidator);
			
			/* validate RFC custom function modules */
			this.validators.add(new RfcCustomFunctionValidator());
			
			/* validate RFC server settings */
			this.validators.add(new SapGatewayConnectionValidator(sapGWHost, sapGWService));
			
			/* validate RFC destinations */
			Map<String, String> rfcDestMap = new HashMap<String, String>();
			ABAPExtractSettingsPage.loadRFCDestinationNameMap(this.configuration, rfcDestMap);
			this.validators.add(new RfcDestinationValidator(generateRFCDestinationNames, rfcDestMap, rfcDestPrefix, createRFCDestination, deleteRFCDestination));
			
			
			// init ProgressMonitor
			monitor.beginTask(Messages.SapSettingsValidator_0, this.validators.size());

			/* run validation */
			for (IValidator validator : this.validators) {
				if (!monitor.isCanceled()) {
					this.validationResults.add(validator.validate(sapSystem, new SubProgressMonitor(monitor, 1)));
				}
			}
			
			// finish ProgressMonitor
			monitor.done();
		}

		
		
	}
	
	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

		/* validate SAP settings */
		this.validate(monitor);

	}
	
	/**
	 * getValidationResults
	 * 
	 * @return
	 */
	public List<IValidationResult> getValidationResults() {

		return this.validationResults;
	}
	
}
