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
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

import com.ibm.iis.sappack.gen.common.ui.connections.SapSystem;
import com.ibm.is.sappack.gen.common.ui.Messages;

public class AbapExtractJobSapSettingsValidator implements IRunnableWithProgress {

	/* sap logon credentials */
	private String sapUserName;
	private String sapUserPassword;
	private String clientID;
   private String sapUserLanguage;

	/* Sap System */
	private SapSystem sapSystem;

	/* InformationServer logon credentials */
	private String isDomainServer;
	private int isPort;
	private String isUserName;
	private String isPassword;
	private String isProject;

	/* DataStage SAP R/3 connection name */
	private String dsSapConnectionName;

	/* the validation results */
	private List<IValidationResult> validationResults;

	/* List of IValidators */
	private List<IValidator> validators;

	public AbapExtractJobSapSettingsValidator(String isDomainServer, int isPort, 
	                                          String isUserName, String isPassword, 
	                                          String isProject, String dsSapConnectionName, 
	                                          String sapUserName, String sapUserPassword, 
	                                          String clientID, String sapUserLanguage) {
		this.isDomainServer = isDomainServer;
		this.isPort = isPort;
		this.isUserName = isUserName;
		this.isPassword = isPassword;
		this.isProject = isProject;

		this.dsSapConnectionName = dsSapConnectionName;

		this.sapUserName = sapUserName;
		this.sapUserPassword = sapUserPassword;
		this.clientID = clientID;
      this.sapUserLanguage = sapUserLanguage;

		this.validators = new ArrayList<IValidator>();
		this.validationResults = new ArrayList<IValidationResult>();

	}

	public void addValidator(IValidator validator) {
		this.validators.add(validator);

	}

	/**
	 * validate
	 * 
	 * entry point to run all validations
	 * 
	 * @param monitor
	 */
	private void validate(IProgressMonitor monitor) {

		/* create validators */

		// validate if DataStage SAP Connection exists
		DataStageSAPConnectionValidator dsSAPConnectionValidator = new DataStageSAPConnectionValidator(new ValidationResult(), this.dsSapConnectionName, this.isDomainServer, this.isPort,
				this.isUserName, this.isPassword, this.isProject);
		// don't add this to the validators
		validationResults.add(dsSAPConnectionValidator.validate(null, monitor));

		/* get SAP system - null if connection with dsSapConnectionName does not exist */
		this.sapSystem = dsSAPConnectionValidator.getSapSystem();

		/* check if client ID is numeric */
		try {
			Integer.parseInt(this.clientID);
		} catch (Exception e) {
			/* stop validation is client id is not numeric */
			this.validationResults.add(new ValidationResult(Messages.AbapExtractJobSapSettingsValidator_0, this.clientID, Messages.SapSystemWizardPage_23, Status.FAILURE));
			return;
		}

		if (this.sapSystem != null) {
			/* update SapSystem default values */
			this.sapSystem.setClientId(Integer.parseInt(this.clientID));
			this.sapSystem.setUsername(this.sapUserName);
			this.sapSystem.setPassword(this.sapUserPassword);
         this.sapSystem.setUserLanguage(this.sapUserLanguage);
		}

		// validate connection to SAP application server
		if (this.sapSystem != null) {
			IValidator sapAppServerValidator = new SapAppServerConnectionValidator();
			this.validators.add(sapAppServerValidator);
		}

		if (sapSystem != null) {

			// init ProgressMonitor
			monitor.beginTask(Messages.SapSettingsValidator_0, this.validators.size());

			/* run validation */
			for (IValidator validator : this.validators) {
				if (!monitor.isCanceled()) {
					this.validationResults.add(validator.validate(sapSystem, new SubProgressMonitor(monitor, 1)));
				}
			}
		}

		// finish ProgressMonitor
		monitor.done();

	}

	/**
	 * setSapSystem
	 * 
	 * @param sapSystem
	 */
	public void setSapSystem(SapSystem sapSystem) {
		this.sapSystem = sapSystem;
	}

	/**
	 * getValidationResults
	 * 
	 * @return
	 */
	public List<IValidationResult> getValidationResults() {

		return this.validationResults;
	}

	static String copyright() {
		return com.ibm.is.sappack.gen.tools.jobgenerator.validator.Copyright.IBM_COPYRIGHT_SHORT;
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

		/* check for job parameters */

		/* validate SAP settings */
		this.validate(monitor);

	}

}