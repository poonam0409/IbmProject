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

import java.util.UUID;
import java.util.logging.Level;

import org.eclipse.core.runtime.IProgressMonitor;

import com.ibm.iis.sappack.gen.common.ui.connections.SapSystem;
import com.ibm.iis.sappack.gen.common.ui.jco.DummyConnectionHandler;
import com.ibm.iis.sappack.gen.common.ui.jco.RfcServerDataProvider;
import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.tools.jobgenerator.Activator;
import com.ibm.is.sappack.gen.tools.sap.constants.Constants;
import com.sap.conn.jco.JCoCustomRepository;
import com.sap.conn.jco.ext.Environment;
import com.sap.conn.jco.server.DefaultServerHandlerFactory;
import com.sap.conn.jco.server.JCoServer;
import com.sap.conn.jco.server.JCoServerContextInfo;
import com.sap.conn.jco.server.JCoServerExceptionListener;
import com.sap.conn.jco.server.JCoServerFactory;
import com.sap.conn.jco.server.JCoServerFunctionHandler;
import com.sap.conn.jco.server.JCoServerState;

public class SapGatewayConnectionValidator implements IValidator {

	static String copyright() {
		return com.ibm.is.sappack.gen.tools.jobgenerator.validator.Copyright.IBM_COPYRIGHT_SHORT;
	}

	private String gwHost;
	private String gwService;
	private IValidationResult validationResult;


	public SapGatewayConnectionValidator(String gwHost, String gwService) {
		this.gwHost = gwHost;
		this.gwService = gwService;
		this.validationResult = new ValidationResult();
	}

	
	@Override
	public IValidationResult validate(SapSystem sapSystem, IProgressMonitor monitor) {

		monitor.beginTask(Messages.SapGatewayConnectionValidator_0, 3);
		monitor.subTask(Messages.SapGatewayConnectionValidator_0);
		
		this.validationResult.setName(Messages.SapGatewayConnectionValidator_0);
		this.validationResult.setValue(Constants.EMPTY_STRING);
		
		IValidationResult gwHostResult = new ValidationResult();
		gwHostResult.setName(Messages.SapGatewayConnectionValidator_1);
		gwHostResult.setValue(this.gwHost);
		this.validationResult.getSubResults().add(gwHostResult);
		
		IValidationResult gwServiceResult = new ValidationResult();
		gwServiceResult.setName(Messages.SapGatewayConnectionValidator_2);
		gwServiceResult.setValue(this.gwService);
		this.validationResult.getSubResults().add(gwServiceResult);
		JCoServer server = null;
		RfcServerDataProvider rfcServerDataProvider = null;
		
		if(monitor.isCanceled()) {
			return this.validationResult;
		}
		
		
		try {
			/* generate a random programID */
			String programID = UUID.randomUUID().toString();
			
			// server connection using gwHost, gwService and progID
			rfcServerDataProvider = new RfcServerDataProvider();
			rfcServerDataProvider.setGatewayHost(this.gwHost);
			rfcServerDataProvider.setGatewayService(this.gwService);
			
			rfcServerDataProvider.setProgramID(programID);
			rfcServerDataProvider.setConnectionCount(2);

			// register the server data provider
			Environment.registerServerDataProvider(rfcServerDataProvider);
			monitor.worked(1);
		
			// sanity check
			server = JCoServerFactory.getServer(Constants.EMPTY_STRING);
			if(server.getState() != JCoServerState.STOPPED) {
				server.stop();
			}
			server.setConnectionCount(1);
			JCoServerFunctionHandler dummyConnectionHandler = new DummyConnectionHandler();
			DefaultServerHandlerFactory.FunctionHandlerFactory factory = new DefaultServerHandlerFactory.FunctionHandlerFactory();
			factory.registerHandler(programID, dummyConnectionHandler); // use programID as random identifier
			server.setCallHandlerFactory(factory);
			server.addServerExceptionListener(new ExceptionListener());
			
			/* use custom repository */
			JCoCustomRepository repository = new ValidationDummyRepository();
			server.setRepository(repository);
			
			// start JCoServer
			server.start();
			
			monitor.worked(1);
			
			// TODO we made need to give the RFC server some time
			// to switch from STARTED to DEAD if gateway host or
			// gateway service are incorrect
			
			// check the server state
			JCoServerState state = server.getState();
			// JCoServerStates may vary in different JCo versions - both ALIVE and STARTED are ok for us
			if(state == JCoServerState.ALIVE || state == JCoServerState.STARTED) {
				this.validationResult.setMessage(Messages.SapGatewayConnectionValidator_3);
				this.validationResult.setStatus(Status.SUCCESS);
			} 
			else {
				this.validationResult.setMessage(Messages.SapGatewayConnectionValidator_4);
				this.validationResult.setStatus(Status.FAILURE);
			}
		} 
		catch (Exception e) {
			this.validationResult.setMessage(e.getMessage());
			this.validationResult.setStatus(Status.FAILURE);
			
			Activator.getLogger().log(Level.SEVERE, Messages.SapGatewayConnectionValidator_5, e);
		} 
		finally {
			// ignore any errors in finally block
			try {
				// stop the server in any case
				if(server != null) {
					server.stop();
				}
				monitor.worked(1);
			}
			catch(Exception ignoreExcpt) {
				Activator.getLogger().log(Level.SEVERE, Messages.SapGatewayConnectionValidator_6, ignoreExcpt);
			}

			try {
				// unregister the ServerDataProvider
				if(rfcServerDataProvider != null) {
					Environment.unregisterServerDataProvider(rfcServerDataProvider);
				}
			}
			catch(Exception ignoreExcpt) {
				Activator.getLogger().log(Level.SEVERE, Messages.SapGatewayConnectionValidator_7, ignoreExcpt);
			}
			
			monitor.done();
		} // end of finally
		
		return this.validationResult;
	}

	/**
	 * ExceptionListener
	 * 
	 * Helper class to catch JCoServer exceptions
	 */
	private class ExceptionListener implements JCoServerExceptionListener {

		@Override
		public void serverExceptionOccurred(JCoServer server, String connectionID, JCoServerContextInfo serverCtx, Exception exception) {
			
			/* log exception */
			Activator.getLogger().log(Level.WARNING, exception.getMessage());
	
		}
		
	}

}
