//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2011, 2012                                              
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


import java.text.MessageFormat;
import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;

import com.ibm.iis.sappack.gen.common.ui.connections.SapConnectionRetreiver;
import com.ibm.iis.sappack.gen.common.ui.connections.SapSystem;
import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.tools.sap.utilities.Utilities;


/**
 * DataStageSAPConnectionValidator
 * 
 * Validates if the given DataStage SAP R/3 connection exists on the DataStage
 * server
 */
public class DataStageSAPConnectionValidator implements IValidator {

	/* InformationServer logon credentials */
	private String						 isDomainServer;
	private int							 isPort;
	private String						 isUserName;
	private String						 isPassword;
	private String						 isProject;
	private String						 errorMsg;
	private SapConnectionRetreiver connectionRetreiver;

	/* existing DataStage SAP connections */
	private boolean connectionExists = false;
	private SapSystem sapSystem;

	/* DataStage SAP R/3 connection name */
	private String connectionName;

	/* validation result */
	private IValidationResult validationResult;

	
	static String copyright() {
		return com.ibm.is.sappack.gen.tools.jobgenerator.validator.Copyright.IBM_COPYRIGHT_SHORT;
	}

	/**
	 * DataStageSAPConnectionValidator
	 * 
	 * @param informationServerDetailsPage
	 * @param connectionName
	 */
//	public DataStageSAPConnectionValidator(InformationServerDetailsPage informationServerDetailsPage, String connectionName) {
//
//		this.isDomainServer = informationServerDetailsPage.getIsHost();
//		this.isPort = Integer.parseInt(informationServerDetailsPage.getIsPort());
//		this.isUserName = informationServerDetailsPage.getIsUser();
//		this.isPassword = informationServerDetailsPage.getIsPassword();
//		this.isProject = informationServerDetailsPage.getDsProject();
//		this.connectionName = connectionName;
//		this.validationResult = new ValidationResult();
//		init();
//	}

	/**
	 * DataStageSAPConnectionValidator
	 * 
	 * @param validationResult
	 * @param connectionName
	 * @param isDomainServer
	 * @param isPort
	 * @param isUserName
	 * @param isPassword
	 * @param isProject
	 */
	public DataStageSAPConnectionValidator(IValidationResult validationResult, String connectionName, String isDomainServer, int isPort, String isUserName, String isPassword, String isProject) {
		this.isDomainServer = isDomainServer;
		this.isPort = isPort;
		this.isUserName = isUserName;
		this.isPassword = isPassword;
		this.isProject = isProject;
		this.validationResult = validationResult;
		this.connectionName = connectionName;
		init();
	}

	void init() {
		if (!Utilities.isJobParameter(this.connectionName)) {
			connectionRetreiver = new SapConnectionRetreiver(this.isDomainServer, this.isPort, this.isUserName, this.isPassword, this.isProject);
			Collection<String> dsSapConnections = connectionRetreiver.getAllSapConnections();
			/* check for error messages */
			if (connectionRetreiver.getErrorMessage() != null) {
				this.connectionExists = false;
				this.errorMsg = connectionRetreiver.getErrorMessage();
			} else {
				this.connectionExists = dsSapConnections.contains(this.connectionName);
				this.sapSystem = connectionRetreiver.getSapSystem(this.connectionName);
			}
		} else {
			this.connectionExists = true;
		}
	}

	/**
	 * checkIfExists
	 * 
	 * check if a specified DataStage SAP connection
	 * exists. Returns null if the connection exists
	 * or the connection name is a job parameter.
	 * Otherwise an error message is returned
	 * 
	 * @return
	 */
	public String checkIfExists() {

		//		/* check if connection name is job parameter * /
		//		if(!Utilities.isJobParameter(this.connectionName)) {
		//			SapConnectionRetreiver connectionRetreiver = new SapConnectionRetreiver(this.isDomainServer, this.isPort,
		//					this.isUserName, this.isPassword, this.isProject);
		//			/* fetch DataStage SAP R/3 connections * /
		//			Set<String> dsSapConnections = connectionRetreiver.getAllSapConnections();
		//			
		//			/* check for error messages */
		//			if(connectionRetreiver.getErrorMessage() != null) {
		//				return connectionRetreiver.getErrorMessage();
		//			}
		//			
		//			/* check if connection exists */
		//			if(!dsSapConnections.contains(this.connectionName)) {
		//				return MessageFormat.format(Messages.DataStageSAPConnectionValidator_3, this.connectionName, this.isDomainServer);
		//			}
		//			
		//		}

		/* connection exists or could not be checked because
		 * connectionName is a job parameter
		 */
		if (connectionExists) {
			return null;
		} else {
			if (this.errorMsg == null) {
				return MessageFormat.format(Messages.DataStageSAPConnectionValidator_3, this.connectionName, this.isDomainServer);
			}
		}
		return this.errorMsg;
	}

	/**
	 * getSapSystem
	 * @return
	 */
	public SapSystem getSapSystem() {
		return this.sapSystem;
	}

	@Override
	public IValidationResult validate(SapSystem sapSystem, IProgressMonitor monitor) {

		/* fill validation result values */
		this.validationResult.setName(Messages.DataStageSAPConnectionValidator_0);
		this.validationResult.setValue(this.connectionName);

		/* check if a connection with the given names exists on DS server */
		if (connectionExists) {
			/* connections exists */
			this.validationResult.setStatus(Status.SUCCESS);
			this.validationResult.setMessage(Messages.DataStageSAPConnectionValidator_1);

		} 
		else {
			/* connection does not exist */
			this.validationResult.setStatus(Status.FAILURE);
			
			String msg;
			if (this.errorMsg == null) {
				msg = MessageFormat.format(Messages.DataStageSAPConnectionValidator_3, 
													new Object[]{ this.connectionName, this.isDomainServer});
			}
			else {
				msg = MessageFormat.format(Messages.DataStageSAPConnectionValidator_4, 
													new Object[]{ this.connectionName, this.errorMsg});
			}
			this.validationResult.setMessage(msg);
		}

		return this.validationResult;
	}

}
