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
import com.ibm.is.sappack.gen.common.ui.Messages;

public class CPICValidator implements IValidator {

	static String copyright() {
		return com.ibm.is.sappack.gen.tools.jobgenerator.validator.Copyright.IBM_COPYRIGHT_SHORT;
	}

	boolean useSAPLogonDetails;
	String cpicUser;
	String cpicPassword;

	public CPICValidator(boolean useSAPLogonDetails, String cpicUser, String cpicPassword) {
		this.useSAPLogonDetails = useSAPLogonDetails;
		this.cpicUser = cpicUser;
		this.cpicPassword = cpicPassword;
	}

	@Override
	public IValidationResult validate(SapSystem sapSystem, IProgressMonitor progressMonitor) {
		ValidationResult result = new ValidationResult();
		final String validationResultName = Messages.CPICValidator_0;
		final String validationMessageSuccess = Messages.CPICValidator_1;
		if (!this.useSAPLogonDetails) {
			SapSystem newSapSystem = sapSystem.clone();
			newSapSystem.setUsername(this.cpicUser);
			newSapSystem.setPassword(this.cpicPassword);
			SapAppServerConnectionValidator validator = new SapAppServerConnectionValidator();
			
			IValidationResult subResult = validator.validate(newSapSystem, progressMonitor);
			result.setStatus(subResult.getStatus());
			String msg = validationMessageSuccess;
			if (subResult.getStatus() == Status.WARNING) {
				msg = Messages.CPICValidator_2;
			} else if (subResult.getStatus() == Status.FAILURE) {
				msg = Messages.CPICValidator_3;
			} else {
				msg = Messages.CPICValidator_4;
			}
			result.setMessage(msg);
			result.setName(validationResultName);
			result.getSubResults().add(subResult);
		} else {
			result.setStatus(Status.SUCCESS);
			result.setName(validationResultName);
			result.setMessage(validationMessageSuccess);
		}
		return result;
	}

}
