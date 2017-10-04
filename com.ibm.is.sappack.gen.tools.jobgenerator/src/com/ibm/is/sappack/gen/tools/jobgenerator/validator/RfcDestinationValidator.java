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

import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;

import com.ibm.iis.sappack.gen.common.ui.connections.SapSystem;
import com.ibm.iis.sappack.gen.common.ui.jco.RfcDestinationDataProvider;
import com.ibm.is.sappack.gen.common.ui.Activator;
import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.tools.sap.constants.Constants;
import com.sap.conn.jco.AbapException;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoRepository;
import com.sap.conn.jco.rt.DefaultTable;

public class RfcDestinationValidator implements IValidator {

	/* RFC function modules we need to call */
	private static final String SALF_RFC_READ_DESTINATION = "SALF_RFC_READ_DESTINATION"; //$NON-NLS-1$
	private static final String TREX_RFC_CONNECT_CHECK_LOCAL = "TREX_RFC_CONNECT_CHECK_LOCAL"; //$NON-NLS-1$
	private JCoFunction connectionTestFunction;
	private JCoFunction readDestinationFunction;
	private JCoDestination destination;
	private IValidationResult validationResult;
	private Map<String, String> rfcDestMap;
	private boolean rfcCreateDestAutomatically;
	private boolean rfcDeleteDestIfExisting;
	private boolean generateRFCNames;
	private SapSystem sapSystem;

	static String copyright() {
		return com.ibm.is.sappack.gen.tools.jobgenerator.validator.Copyright.IBM_COPYRIGHT_SHORT;
	}

	public RfcDestinationValidator(boolean generateRFCNames, Map<String, String> rfcDestMap, String rfcDestNamePrefix, boolean rfcCreateDestAutomatically, boolean rfcDeleteDestIfExisting) {
		this.validationResult = new ValidationResult();
		this.rfcDestMap = rfcDestMap;
		this.rfcCreateDestAutomatically = rfcCreateDestAutomatically;
		this.rfcDeleteDestIfExisting = rfcDeleteDestIfExisting;
		this.generateRFCNames = generateRFCNames;
	}

	@Override
	public IValidationResult validate(SapSystem sapSystem,IProgressMonitor monitor) {
		this.sapSystem = sapSystem;

		this.validationResult.setName(Messages.RfcDestinationValidator_0);
		// set initial status to success
		this.validationResult.setStatus(Status.SUCCESS);
		this.validationResult.setMessage(Messages.RfcDestinationValidator_17);

		monitor.beginTask(Constants.EMPTY_STRING, 1);
		monitor.subTask(Messages.RfcDestinationValidator_11);
		if (!init()) {
			// we don't need to continue if init() fails
			monitor.worked(1);
			monitor.done();
			return this.validationResult;
		}

		this.validateRfcDestinations(new SubProgressMonitor(monitor, 1));
		monitor.done();
		// update overall status
		if (this.validationResult.getStatus() == Status.FAILURE) {
			this.validationResult.setMessage(Messages.RfcDestinationValidator_18);
		}

		if (this.validationResult.getStatus() == Status.WARNING) {
			this.validationResult.setMessage(Messages.RfcDestinationValidator_19);
		}

		return this.validationResult;
	}

	/**
	 * validateRfcDestination
	 * 
	 * @param destination
	 * @param programID
	 * @throws JCoException
	 */
	private void validateRfcDestination(String destination, String programID) throws JCoException {

		/* check if rfc destination exists on SAP server */
		IValidationResult destResult = new ValidationResult();
		this.validationResult.getSubResults().add(destResult);
		destResult.setName(Messages.RfcDestinationValidator_1);
		destResult.setValue(destination);
		try {

			this.readDestinationFunction.getImportParameterList().setValue("DESTINATION", destination); //$NON-NLS-1$
			this.readDestinationFunction.execute(this.destination);
			JCoParameterList exportParams = this.readDestinationFunction.getExportParameterList();

			/* program id should be the same as the specified one */
			String pID = (String) exportParams.getValue("PROGRAM"); //$NON-NLS-1$
			IValidationResult pidResult = new ValidationResult();
			pidResult.setName(Messages.RfcDestinationValidator_2);
			pidResult.setValue(programID);
			destResult.getSubResults().add(pidResult);
			if (!pID.equals(programID)) {
				// incorrect program ID
				pidResult.setMessage(MessageFormat.format(Messages.RfcDestinationValidator_3, pID));
				pidResult.setStatus(Status.FAILURE);
				// overall status
				this.validationResult.setStatus(Status.FAILURE);

			} else {
				// correct program ID
				pidResult.setMessage(Messages.RfcDestinationValidator_4);
				pidResult.setStatus(Status.SUCCESS);
			}

			/* method should be 'R' - registered server program */
			String method = (String) exportParams.getValue("METHOD"); //$NON-NLS-1$
			IValidationResult activationTypeResult = new ValidationResult();
			destResult.getSubResults().add(activationTypeResult);
			activationTypeResult.setName(Messages.RfcDestinationValidator_9);
			activationTypeResult.setValue(method);
			if (!method.equals("R")) { //$NON-NLS-1$
				activationTypeResult.setMessage(Messages.RfcDestinationValidator_5);
				activationTypeResult.setStatus(Status.FAILURE);
				// overall status
				this.validationResult.setStatus(Status.FAILURE);
			} else {
				activationTypeResult.setMessage(Messages.RfcDestinationValidator_6);
				activationTypeResult.setStatus(Status.SUCCESS);

				/* check if programID has registered servers */
				IValidationResult connectionTestResult = new ValidationResult();
				destResult.getSubResults().add(connectionTestResult);
				connectionTestResult.setName(Messages.RfcDestinationValidator_12);
				connectionTestResult.setValue(Constants.EMPTY_STRING);
				if (this.connectionTestFunction != null) {
					this.connectionTestFunction.getImportParameterList().setValue("TREX_DESTINATION", destination); //$NON-NLS-1$
					this.connectionTestFunction.execute(this.destination);
					DefaultTable rfcPingHostList = (DefaultTable) this.connectionTestFunction.getExportParameterList().getValue("RFC_PING_HOST_LIST"); //$NON-NLS-1$

					String hostname = (String) rfcPingHostList.getValue("HOST_NAME");//$NON-NLS-1$
					if (!hostname.equals(Constants.EMPTY_STRING)) {
						// another server is already registered
						connectionTestResult.setStatus(Status.FAILURE);
						connectionTestResult.setMessage(MessageFormat.format(Messages.RfcDestinationValidator_13, hostname));
						// overall status
						this.validationResult.setStatus(Status.FAILURE);

					} else {
						// no other server registered - ok
						connectionTestResult.setStatus(Status.SUCCESS);
						connectionTestResult.setMessage(Messages.RfcDestinationValidator_14);
					}
				} else {
					// function module TREX_RFC_CONNECT_CHECK_LOCAL does not exist
					connectionTestResult.setStatus(Status.WARNING);
					if (this.validationResult.getStatus() == Status.SUCCESS) {
						this.validationResult.setStatus(Status.WARNING);
					}
					connectionTestResult.setMessage(Messages.RfcDestinationValidator_15);
				}

			}

			// if the RFC destination exists and we checked to create
			// the destination automatically we must
			if (this.rfcCreateDestAutomatically) {
				if (this.rfcDeleteDestIfExisting) {
					destResult.setMessage(Messages.RfcDestinationValidator_20);
					destResult.setStatus(Status.WARNING);
				} else {
					destResult.setMessage(Messages.RfcDestinationValidator_21);
					destResult.setStatus(Status.FAILURE);
				}
			} else {

				/* destination is valid */
				destResult.setMessage(Messages.RfcDestinationValidator_7);
				destResult.setStatus(Status.SUCCESS);
			}

		}
		catch (AbapException abapExcpt) {
			// 'DESTINATION_NOT_EXIST' contains destination name as message text 
			if (destination.equals(abapExcpt.getMessageText())) {
				Activator.getLogger().info(abapExcpt.getMessage() + ": " + destination);  //$NON-NLS-1$

				// * SALF_RFC_READ_DESTINATION throws an AbapException if the destination does not exist
				destResult.setMessage(Messages.RfcDestinationValidator_8);

				if (this.rfcCreateDestAutomatically) {
					destResult.setStatus(Status.SUCCESS);
					// overall status
					this.validationResult.setStatus(Status.SUCCESS);
				}
				else {
					destResult.setStatus(Status.FAILURE);
					// overall status
					this.validationResult.setStatus(Status.FAILURE);
				}
			}
			else {
				// probably an SAP error or an SAP authorization issue
				if (abapExcpt.getMessage().indexOf("AUTHORITY") > -1 ) {     //$NON-NLS-1$
					// probably an SAP error or an SAP authorization issue
					destResult.setMessage(Messages.RfcDestinationValidator_24);
					
					Activator.getLogger().log(Level.INFO, abapExcpt.getMessage()); 
				}
				else {
					// probably an SAP error or an SAP authorization issue
					destResult.setMessage(Messages.RfcDestinationValidator_23);

//					abapExcpt.printStackTrace();
					Activator.getLogger().log(Level.INFO, abapExcpt.getMessage() + ": " + destination,  //$NON-NLS-1$
					                                      abapExcpt);
				}
				
				// validation failed
				destResult.setStatus(Status.FAILURE);
				this.validationResult.setStatus(Status.FAILURE);
			}
		}
	}

	/**
	 * validateRfcDestinations
	 * 
	 * @param monitor
	 * @return
	 * @throws IOException
	 * @throws JCoException
	 */
	private boolean validateRfcDestinations(IProgressMonitor monitor) {

		if (this.readDestinationFunction == null) {
			// function module SALF_RFC_READ_DESTINATION does not exist
			this.validationResult.setStatus(Status.WARNING);
			this.validationResult.setMessage(Messages.RfcDestinationValidator_16);
			if (this.validationResult.getStatus() == Status.SUCCESS) {
				this.validationResult.setStatus(Status.WARNING);
			}
			monitor.worked(1);
			return false;
		}

		try {
			Map<String, String> rfcDestinationMap = null;
			if (this.rfcCreateDestAutomatically) {
				rfcDestinationMap = new HashMap<String, String>();
				this.validationResult.getSubResults().add(new ValidationResult("", "", Messages.RfcDestinationValidator_22, Status.WARNING)); //$NON-NLS-1$ //$NON-NLS-2$
			} else {
				rfcDestinationMap = this.rfcDestMap;
			}
			// init progress monitor 
			int numberOfDestinations = rfcDestinationMap.size();
			monitor.beginTask(Constants.EMPTY_STRING, numberOfDestinations);
			monitor.subTask(Messages.RfcDestinationValidator_1);

			if (monitor.isCanceled()) {
				this.validationResult.setName(Constants.EMPTY_STRING);
			}

			
			/* validate rfc destinations */
			int i = 0;
			for (Map.Entry<String, String> entry : rfcDestinationMap.entrySet()) {
				if (!monitor.isCanceled()) {
					monitor.subTask(MessageFormat.format(Messages.RfcDestinationValidator_10, entry.getKey()));
					this.validateRfcDestination(entry.getKey().trim(), entry.getValue().trim());
					monitor.worked(1);
				}
				i++;
			}
		} 
		catch (JCoException e) {
			Activator.getLogger().log(Level.SEVERE, e.getMessage(), e);
			this.validationResult = new ValidationResult(Messages.RfcDestinationValidator_1, Constants.EMPTY_STRING, e.getMessage(), Status.FAILURE);

			return false;
		} 
		// rfc destinations are configured properly
		return true;
	}

	/**
	 * init
	 * 
	 * @return
	 */
	private boolean init() {

		try {
			this.destination = RfcDestinationDataProvider.getDestination(this.sapSystem);
			JCoRepository repository = this.destination.getRepository();
			this.readDestinationFunction = repository.getFunction(SALF_RFC_READ_DESTINATION);
			this.connectionTestFunction = repository.getFunction(TREX_RFC_CONNECT_CHECK_LOCAL);

			return true;
		} catch (JCoException e) {
			this.validationResult = new ValidationResult(Messages.RfcDestinationValidator_1, Constants.EMPTY_STRING, e.getMessage(), Status.FAILURE);
			return false;
		}

	}

}
