//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2011, 2013                                              
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


import java.util.logging.Level;

import org.eclipse.core.runtime.IProgressMonitor;

import com.ibm.iis.sappack.gen.common.ui.connections.SapSystem;
import com.ibm.iis.sappack.gen.common.ui.jco.RfcDestinationDataProvider;
import com.ibm.is.sappack.gen.common.SAPAccessException;
import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.tools.jobgenerator.jco.JCoService;
import com.ibm.is.sappack.gen.tools.sap.activator.Activator;
import com.ibm.is.sappack.gen.tools.sap.constants.Constants;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoRepository;
import com.sap.conn.jco.JCoTable;


/**
 * RfcCustomFunctionValidator
 * 
 * Validator for checking if the custom rfc function module 'JCoService.SAP_RFC_DS_SERVICE_NAME'
 * is installed on the SAP application server.
 */
public class RfcCustomFunctionValidator implements IValidator {
	private SapSystem         sapSystem;
	private IValidationResult validationResult;
	private JCoFunction       readTableFunction;
	private JCoDestination    destination;


	static String copyright() {
		return com.ibm.is.sappack.gen.tools.jobgenerator.validator.Copyright.IBM_COPYRIGHT_SHORT;
	}
	
	
	public RfcCustomFunctionValidator() {
		this.validationResult = new ValidationResult();
	}

	/**
	 * init
	 * 
	 * @param monitor
	 * @return
	 */
	private boolean init(IProgressMonitor monitor) {

		try {
			this.destination = RfcDestinationDataProvider.getDestination(this.sapSystem);
			JCoRepository repository = this.destination.getRepository();
			this.readTableFunction = repository.getFunction(Constants.JCO_FUNCTION_RFC_READ_TABLE);
			monitor.worked(1);

			return true;
		} 
		catch (JCoException e) {
			this.validationResult = new ValidationResult(Messages.RfcCustomFunctionValidator_1, 
			                                             JCoService.getSupportedServiceName(), 
			                                             e.getMessage(), Status.FAILURE); 
			return false;
		}
	}

	@Override
	public IValidationResult validate(SapSystem sapSystem, IProgressMonitor progressMonitor) {
		this.sapSystem = sapSystem;
		progressMonitor.beginTask(Constants.EMPTY_STRING, 2);
		progressMonitor.subTask(Messages.RfcCustomFunctionValidator_0);

		if (progressMonitor.isCanceled()) {
			return this.validationResult;
		}

		if (!this.init(progressMonitor)) {
			progressMonitor.worked(1);
			progressMonitor.done();
			return this.validationResult;
		}

		if (progressMonitor.isCanceled()) {
			return this.validationResult;
		}
		/* assemble parameters for remote function call */

		// import parameter
		this.readTableFunction.getImportParameterList().setValue("QUERY_TABLE", "TFDIR"); //$NON-NLS-1$ //$NON-NLS-2$ 

		// table parameters
		JCoTable optionsTable = this.readTableFunction.getTableParameterList().getTable("OPTIONS"); //$NON-NLS-1$
		optionsTable.appendRow();
		optionsTable.setValue("TEXT", "FUNCNAME = '" + JCoService.getSupportedServiceName() + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ 
		JCoTable dataTable = this.readTableFunction.getTableParameterList().getTable("DATA"); //$NON-NLS-1$

		try {
			// call remote function module
			this.readTableFunction.execute(this.destination);
		} 
		catch (JCoException e) {
			progressMonitor.worked(1);
			progressMonitor.done();
			this.validationResult = new ValidationResult(Messages.RfcCustomFunctionValidator_1, 
			                                             JCoService.getSupportedServiceName(), e.getMessage(), 
			                                             Status.FAILURE);
			return this.validationResult;
		}
		progressMonitor.worked(1);

		// data table should contain at least one record
		if (dataTable.getNumRows() > 0) {
			// custom function module exists
			this.validationResult = new ValidationResult(Messages.RfcCustomFunctionValidator_1, JCoService.getSupportedServiceName(),
			                                             Messages.RfcCustomFunctionValidator_2, Status.SUCCESS);
			
			// check if service version matches
			try {
				JCoService jcoService = JCoService.getJCoService(sapSystem);
				if (jcoService.checkVersion() != null) {
					this.validationResult = new ValidationResult(Messages.RfcCustomFunctionValidator_1, JCoService.getSupportedServiceName(),
					                                             Messages.RfcCustomFunctionValidator_4, Status.FAILURE);
				}
			}
			catch(SAPAccessException jobGenExcpt) {
				String    validationErrMsg = Messages.RfcCustomFunctionValidator_5;
				Throwable excptCause       = jobGenExcpt.getCause();

				if (excptCause != null && excptCause.getMessage().length() > 0) {
					validationErrMsg = excptCause.getMessage();
				}
            Activator.getLogger().log(Level.SEVERE, jobGenExcpt.toString());
				this.validationResult = new ValidationResult(Messages.RfcCustomFunctionValidator_1, JCoService.getSupportedServiceName(),
				                                             validationErrMsg, Status.FAILURE);
			}
		} 
		else {
			// custom function module does not exist
			this.validationResult = new ValidationResult(Messages.RfcCustomFunctionValidator_1, JCoService.getSupportedServiceName(),
			                                             Messages.RfcCustomFunctionValidator_3, Status.FAILURE);
		}

		progressMonitor.done();
		return this.validationResult;
	}

}
