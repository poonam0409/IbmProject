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

import org.eclipse.core.runtime.IProgressMonitor;

import com.ibm.iis.sappack.gen.common.ui.connections.SapSystem;
import com.ibm.iis.sappack.gen.common.ui.jco.RfcDestinationDataProvider;
import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.tools.sap.constants.Constants;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;

public class SapAppServerConnectionValidator implements IValidator {

	static String copyright() {
		return com.ibm.is.sappack.gen.tools.jobgenerator.validator.Copyright.IBM_COPYRIGHT_SHORT;
	}

	private SapSystem sapSystem;
	private IValidationResult validationResult;

	/**
	 * SapAppServerConnectionValidator
	 * @param validationResult
	 * @param sapSystem
	 */
	public SapAppServerConnectionValidator(){ //IValidationResult validationResult, SapSystem sapSystem) {		
		this.validationResult = new ValidationResult();
	}

	@Override
	public IValidationResult validate(SapSystem sapSystem, IProgressMonitor monitor) {
		this.sapSystem = sapSystem;
	
		monitor.beginTask(Messages.SapAppServerConnectionValidator_0, 1);
		monitor.subTask(Messages.SapAppServerConnectionValidator_0);
		this.validationResult.setName(Messages.SapAppServerConnectionValidator_0);
		this.validationResult.setValue(Constants.EMPTY_STRING);
		
		IValidationResult appServerResult = new ValidationResult();
		appServerResult.setName(Messages.SapAppServerConnectionValidator_1);
		appServerResult.setValue(this.sapSystem.getHost());
		this.validationResult.getSubResults().add(appServerResult);
		
		IValidationResult sysNoResult = new ValidationResult();
		sysNoResult.setName(Messages.SapAppServerConnectionValidator_2);
		sysNoResult.setValue(Constants.EMPTY_STRING+this.sapSystem.getSystemNumber());
		this.validationResult.getSubResults().add(sysNoResult);
		
		IValidationResult userResult = new ValidationResult();
		userResult.setName(Messages.SapAppServerConnectionValidator_3);
		userResult.setValue(this.sapSystem.getUsername());
		this.validationResult.getSubResults().add(userResult);
		
		IValidationResult passwordResult = new ValidationResult();
		passwordResult.setName(Messages.SapAppServerConnectionValidator_4);
		passwordResult.setValue("********"); //$NON-NLS-1$
		this.validationResult.getSubResults().add(passwordResult);
		
		IValidationResult clientResult = new ValidationResult();
		clientResult.setName(Messages.SapAppServerConnectionValidator_5);
		clientResult.setValue(Constants.EMPTY_STRING+this.sapSystem.getClientId());
		this.validationResult.getSubResults().add(clientResult);
		
      IValidationResult languageResult = new ValidationResult();
      languageResult.setName(Messages.SapAppServerConnectionValidator_7);
      languageResult.setValue(this.sapSystem.getUserLanguage());
      this.validationResult.getSubResults().add(languageResult);
      
		if(monitor.isCanceled()) {
			return this.validationResult;
		}
		
		try {
			JCoDestination destination = RfcDestinationDataProvider.getDestination(this.sapSystem);
			// test connection
			destination.ping();
			monitor.worked(1);
						
			// connection successfully
			this.validationResult.setMessage(Messages.SapAppServerConnectionValidator_6);
			this.validationResult.setStatus(Status.SUCCESS);
			
		} catch (JCoException e) {
			// connection failure
			this.validationResult.setMessage(e.getMessage());
			this.validationResult.setStatus(Status.FAILURE);
		} finally {
			monitor.done();
		}

		return this.validationResult;
	}


}
