//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2009, 2013                                              
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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.is.sappack.dsstages.common.StageLogger;
import com.ibm.is.sappack.dsstages.common.Utilities;
import com.ibm.is.sappack.dsstages.common.VersionInfo;
import com.ibm.is.sappack.dsstages.idoc.listener.handler.IDocHandler;
import com.ibm.is.sappack.dsstages.idoc.listener.handler.IDocHandlerImpl;
import com.ibm.is.sappack.dsstages.idoc.listener.util.IDocServerConstants;
import com.ibm.is.sappack.dsstages.idoc.listener.util.IDocServerException;
import com.ibm.is.sappack.dsstages.idoc.listener.util.IDocServerLogger;
import com.ibm.is.sappack.dsstages.idoc.listener.util.IDocServerMessages;
import com.ibm.is.sappack.dsstages.idoc.listener.util.TransactionState;
import com.sap.conn.jco.AbapException;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoRepository;
import com.sap.conn.jco.ext.DestinationDataEventListener;
import com.sap.conn.jco.ext.DestinationDataProvider;
import com.sap.conn.jco.ext.ServerDataEventListener;
import com.sap.conn.jco.ext.ServerDataProvider;
import com.sap.conn.jco.server.DefaultServerHandlerFactory;
import com.sap.conn.jco.server.JCoServer;
import com.sap.conn.jco.server.JCoServerContext;
import com.sap.conn.jco.server.JCoServerContextInfo;
import com.sap.conn.jco.server.JCoServerErrorListener;
import com.sap.conn.jco.server.JCoServerExceptionListener;
import com.sap.conn.jco.server.JCoServerFactory;
import com.sap.conn.jco.server.JCoServerFunctionHandler;
import com.sap.conn.jco.server.JCoServerState;
import com.sap.conn.jco.server.JCoServerStateChangedListener;
import com.sap.conn.jco.server.JCoServerTIDHandler;

/**
 * IDocServerImpl
 * 
 * IDocServer implementation to receive and process IDocs from SAP
 * 
 */
public class IDocServerImpl implements IDocServer {

	/* class name for logging purposes */
	static final String CLASSNAME = IDocServerImpl.class.getName();

	/* IDocTypes directory - for example C:\IBM\InformationServer\Server\DSSAPConnections\bocasapides5\IDocTypes\ */
	private String defaultIDocTypesDirectory = ""; //$NON-NLS-1$

	/* DSSAPConnections directory - for example C:\IBM\InformationServer\Server\DSSAPConnections\bocasapides5\ */
	private String connectionDir = ""; //$NON-NLS-1$
	
	/* IDoc Listener Logger */
	private Logger logger = null;
	
	/* IDoc Listener specific properties */
	private Map<String, String> idocServerProperties;

	/* JCo Server object */
	private JCoServer jcoServer = null;
	
	/* JCoRepository */
	private JCoRepository repository = null;
	
	/* JCoDestination */
	private JCoDestination destination = null;
	
	/* SAP connection name */
	private String connectionName = "";	 //$NON-NLS-1$
	
	/* State of the IDocServer - should be initialized with true */
	private boolean serverRunning = true;
	
	/* SAP packhome name */
	static String s_packhome = "";
	
	/* SAP connectionName name */
	static String s_conName = "";
	
	public static HashMap<String,String> initfileInfo;	

	public static HashMap<String,String> errorReqNum= new HashMap<String,String>();
	
	/**
	 * IDocServerImpl 
	 */
	public IDocServerImpl() {

		/* initialize the logger */
		this.logger = StageLogger.getLogger();
		
	}

	static String copyright() {
		return com.ibm.is.sappack.dsstages.idoc.listener.Copyright.IBM_COPYRIGHT_SHORT;
	}

	/**
	 * initialize
	 * 
	 * initialize the IDocServer with the properties
	 * provided by the dsidocsvr. Properties include
	 * the settings stored in DSSAPConnections.config
	 * under $InformationServerRoot\Server\DSSAPConnections
	 */
	public void initialize(String[] properties) throws IDocServerException {

		final String METHODNAME = "initialize(String [] properties)"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);
		
		try {
			/* handle properties */
			this.storePropertiesInMap(properties);

			/* assemble path to IDocTypes directory of this DSSAPConnection */
			this.assembleDirectoryNames();
		
			/* initialize IDocListener.log */
			IDocServerLogger.configureLoggerForIDocServer(this.connectionDir);

			this.logger.log(Level.INFO, MessageFormat.format(IDocServerMessages.VersionInfo, VersionInfo.getVersionInfo()));  
			
			/* store SAP connection name */
			this.connectionName = this.idocServerProperties.get(IDocServerConstants.NAME);
			s_conName=this.connectionName;
			
			/* register ConnectionProvider for server connection */
			this.logger.log(Level.INFO, "Register ServerConnectionProvider"); //$NON-NLS-1$
			ServerConnectionProvider serverConnectionProvider = new ServerConnectionProvider(this.idocServerProperties);
			
			/* server connection to receive incoming calls from SAP */
			this.logger.log(Level.INFO, "Register ServerDataProvider"); //$NON-NLS-1$
			com.sap.conn.jco.ext.Environment.registerServerDataProvider(serverConnectionProvider);
			
			/* register ConnectionProvider for client connection */
			this.logger.log(Level.INFO, "Register DestinationConnectionProvider"); //$NON-NLS-1$
			DestinationConnectionProvider destinationConnectionProvider = new DestinationConnectionProvider(this.idocServerProperties);
			
			/* client connection to load repository meta data from SAP */
			this.logger.log(Level.INFO, "Register DestinationDataProvider"); //$NON-NLS-1$
			com.sap.conn.jco.ext.Environment.registerDestinationDataProvider(destinationConnectionProvider);

			/* create JCo destination */
			this.logger.log(Level.INFO, "Creating JCo destination"); //$NON-NLS-1$
			this.destination = JCoDestinationManager.getDestination(this.connectionName);
			
			/* create JCo repository */
			this.logger.log(Level.INFO, "Creating JCo repository"); //$NON-NLS-1$
			this.repository = destination.getRepository();
			
		} catch (JCoException e) {
			this.logger.log(Level.SEVERE, "A JCo exception occurred", e);
			throw new IDocServerException(e.getMessage());
		}
		
		this.logger.exiting(CLASSNAME, METHODNAME);
	}
	
	/**
	 * storePropertiesInMap
	 * 
	 * take the String[] properties that we got from the IDoc Manager Service
	 * and convert them into a Map<String, String> that contains the key value
	 * pairs
	 * 
	 * @param properties
	 */
	private void storePropertiesInMap(String[] properties) {

		final String METHODNAME = "storePropertiesInMap(String [] properties)"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);

		/* idocServerProperties should be thread-safe */
		this.idocServerProperties = Collections.synchronizedMap(new HashMap<String, String>());

		/* sanity test - should never happen but who knows */
		if(properties == null || properties.length == 0) {
			String msg = IDocServerMessages.PropertiesEmpty;
			logger.log(Level.SEVERE, msg);
			throw new RuntimeException(msg);
		}
		
		/* process properties */
		for (int i = 0; i < properties.length; i++) {

			/* keys and values are separated by a '=' */
			String property = properties[i];
			if (property != null) {
				int splitIndex = property.indexOf('=');
				String key = property.substring(0, splitIndex);
				String value = property.substring(splitIndex + 1);

				/* store key value pair in map */
				this.idocServerProperties.put(key, value);
				this.logger.log(Level.INFO, "{0} = {1}", new Object[] { key, value }); //$NON-NLS-1$
			}
		}
		this.logger.exiting(CLASSNAME, METHODNAME);
	}


	@Override
	/**
	 * start
	 * 
	 * start the IDocServer
	 */
	public void start() throws IDocServerException {

		final String METHODNAME = "start()"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);
		
		/* register RFC function handler to handle incoming RFC calls */
		TRFCFunctionHandler rfcFunctionHandler = new TRFCFunctionHandler();
		DefaultServerHandlerFactory.FunctionHandlerFactory factory = new DefaultServerHandlerFactory.FunctionHandlerFactory();
		factory.registerHandler(IDocServerConstants.IDOC_INBOUND_ASYNCHRONOUS, rfcFunctionHandler);
		factory.registerHandler(IDocServerConstants.RSAR_TRFC_DATA_RECEIVED, rfcFunctionHandler);

		// initialize the JCO RFC trace from environment 
		// StageLogger.initializeRFCTrace(this.logger);

		try {
			/* create the JCo server */
			this.logger.log(Level.INFO, "Creating JCo server"); //$NON-NLS-1$
			this.jcoServer = JCoServerFactory.getServer(this.connectionName + "_server");
			this.logger.log(Level.INFO, "Setting JCo CallHandlerFactory"); //$NON-NLS-1$
			this.jcoServer.setCallHandlerFactory(factory);
			this.logger.log(Level.INFO, "Setting JCo Repository"); //$NON-NLS-1$
			this.jcoServer.setRepository(repository);
			this.logger.log(Level.INFO, "Setting JCo TIDHandler"); //$NON-NLS-1$
			this.jcoServer.setTIDHandler(rfcFunctionHandler);
			this.logger.log(Level.INFO, "Registering JCo ServerStateChangeListener"); //$NON-NLS-1$
			JCoServerStateChangedListener stateChangeListener = new IDocServerStateChangedListener();
			this.jcoServer.addServerStateChangedListener(stateChangeListener);
			this.logger.log(Level.INFO, "Registering JCo ServerExceptionListener and ErrorListener"); //$NON-NLS-1$
			IDocServerExceptionListener exceptionListener = new IDocServerExceptionListener();
			this.jcoServer.addServerExceptionListener(exceptionListener);
			this.jcoServer.addServerErrorListener(exceptionListener);
			
			/* start the JCo server */
			this.logger.log(Level.INFO, "Starting JCo server ..."); //$NON-NLS-1$
			this.jcoServer.start();
			this.logger.log(Level.INFO, "JCo server has been started"); //$NON-NLS-1$

			/* log status message */
			this.logger.log(Level.INFO, IDocServerMessages.StartedStatus);
			String enc = Utilities.getIDocListenerLogEncoding();
			this.logger.log(Level.INFO, "CP: " + enc); //$NON-NLS-1$
			this.logger.log(Level.INFO, IDocServerMessages.NumberOfIDocServerThreads, this.jcoServer.getConnectionCount()); // number of IDocServer Threads
			this.logger.log(Level.INFO, IDocServerMessages.DSSAPConnection, this.connectionName);
			
		} catch (JCoException e) {
			this.logger.log(Level.SEVERE, "A JCo exception occurred", e);
			throw new IDocServerException(e.getMessage());
		}

		this.logger.exiting(CLASSNAME, METHODNAME);
	}

	@Override
	/**
	 * stop
	 * 
	 * stop the IDocServer
	 */
	public void stop() throws IDocServerException {

		final String METHODNAME = "stop()"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);

		this.logger.log(Level.INFO, "Stopping the IDocServer"); //$NON-NLS-1$
		
		/* stop the JCo server */
		this.jcoServer.stop();

		/* log status message */
		this.logger.log(Level.INFO, IDocServerMessages.StoppedStatus);

		this.logger.exiting(CLASSNAME, METHODNAME);
	}
	
	@Override
	/**
	 * isServerRunning
	 * 
	 * return true if the server is running properly.
	 * Otherwise return false
	 */
	public boolean isServerRunning() {
		
		return this.serverRunning;
	}

	
	/**
	 * assembleDirectoryNames
	 * 
	 * assemble the path to the DSSAPConnection directory
	 * and IDocTypes directory of this DSSAPConnection.
	 * 
	 * For example DSSAPConnection directory
	 * 
	 * C:\IBM\InformationServer\Server\DSSAPConnections\BOCASAPERP5\
	 * 
	 * /opt/IBM/InformationServer/Server/DSSAPConnections/BOCASAPERP5/
	 * 
	 * For example IDocTypes directory
	 * 
	 * C:\IBM\InformationServer\Server\DSSAPConnections\BOCASAPERP5\IDocTypes\
	 * 
	 * /opt/IBM/InformationServer/Server/DSSAPConnections/BOCASAPERP5/IDocTypes\
	 */
	private void assembleDirectoryNames() {

		/* get $PACKHOME and make sure it is set */
		s_packhome = this.idocServerProperties.get(IDocServerConstants.PACKHOME);
		if(s_packhome == null || s_packhome.length() == 0) {
			String msg = IDocServerMessages.PackHomeNotSet;
			logger.log(Level.SEVERE, msg);
			throw new RuntimeException(msg);
		}
		
		String fileSeparator = File.separator;
		StringBuffer path = new StringBuffer();
		path.append(s_packhome);
		path.append(IDocServerConstants.DSSAPConnections);
		path.append(fileSeparator);
		String connName = this.idocServerProperties.get(IDocServerConstants.NAME);
		path.append(connName);
		path.append(fileSeparator);
		
		/* connection directory */
		String conDir = path.toString();
		this.logger.log(Level.INFO, "DSSAPConnection directory = {0}", conDir); //$NON-NLS-1$
		this.connectionDir = conDir;
		
		/* IDocTypes default directory */
		path.append(IDocServerConstants.IDocTypes);
		path.append(fileSeparator);

		this.defaultIDocTypesDirectory = path.toString();
		this.logger.log(Level.INFO, "IDocTypes default directory= {0}", this.defaultIDocTypesDirectory); //$NON-NLS-1$
	}

	
	/**
	 * TRFCFunctionHandler
	 * 
	 * Handles incoming tRFC calls from SAP. Implements transactional behavior
	 * since SAP sends IDocs in transactions
	 * 
	 */
	class TRFCFunctionHandler implements JCoServerFunctionHandler, JCoServerTIDHandler {

		/* class name for logging purposes */
		private final String CLASSNAME = TRFCFunctionHandler.class.getName();
		
		/* IDocHandlerMap - each transaction (tid) has its own IDoc Handler */
		Map<String, IDocHandler> idocHandlerMap = Collections.synchronizedMap(new HashMap<String, IDocHandler>());
		ExecutorService tRFCExecutor = null;
		int numOfThreads = Integer.parseInt(idocServerProperties.get(IDocServerConstants.RFCSERVERCOUNT));
		boolean isMultiThreaded = true;
		
		public TRFCFunctionHandler() {
			
			String threadEnabled=System.getenv(IDocServerConstants.DS_IDOC_LISTENER_ENABLE_MULTI_THREADING);
			try
			{
				String noOfThreads = System.getenv(IDocServerConstants.DS_IDOC_LISTENER_NUM_OF_THREADS);
				if(!(noOfThreads==null))
				numOfThreads=Integer.parseInt(System.getenv(IDocServerConstants.DS_IDOC_LISTENER_NUM_OF_THREADS));
			}catch(Exception ex)
			{
				logger.log(Level.INFO, "Value of env. var. DS_IDOC_LISTENER_NUM_OF_THREADS is improper. Running with multi threads");
			}
			
			if("0".equals(threadEnabled))
				isMultiThreaded = false;
			else {
				tRFCExecutor = Executors.newFixedThreadPool(numOfThreads);
			    logger.log(Level.INFO, "Number of threads started is: {0}",numOfThreads);
			}
		}
		@Override
		/**
		 * handleRequest
		 * 
		 * is called when an incoming request 
		 * from SAP is received.
		 * 
		 * Within one transaction (LUW) this method
		 * may be called multiple times
		 * 
		 * @param jcoservercontext
		 * @param jcofunction
		 */
		public void handleRequest(JCoServerContext jcoservercontext, JCoFunction jcofunction) throws AbapException {

			String tid = jcoservercontext.getTID();
			try{
				String functionName = jcofunction.getName();
				logger.log(Level.INFO, "Incoming inside handleRequest(): {0} from SAP", functionName);
				final String METHODNAME = "handleRequest(JCoServerContext jcoservercontext, JCoFunction jcofunction)"; //$NON-NLS-1$
				logger.entering(CLASSNAME, METHODNAME);
				logger.log(Level.INFO, "Current Thread name: {0}", Thread.currentThread().getName());
				logger.log(Level.INFO, "TID Manager: Executing IDocHandler for transaction {0}", tid); //$NON-NLS-1$
				/* get the IDocHandler for this transaction ID */
				IDocHandler idocHandler = this.idocHandlerMap.get(tid);

				if(isMultiThreaded)
				{
					processPacketinThread(tid,jcofunction,idocHandler);
				}
				else
				{
					processPacketSequentially(tid,jcofunction,idocHandler);
				}
			}catch(Exception e)
			{
				logger.log(Level.INFO, "Exception in method handleRequest(): {0}", e.getMessage());
				throw new RuntimeException("Transaction failed for tid: "+tid);
			}
		}
		
		
		public void processPacketinThread(String tid,JCoFunction jcofunction,IDocHandler idocHandler)
		{
			logger.log(Level.INFO, "Multi-Threading is enabled.");
			//Creating a thread pool that reuses a number of threads operating off a shared unbounded queue. 
			//process is handled to a new thread created by executer framework, for quick release of handler method and faster response to the next request. 
			Future<String> future=tRFCExecutor.submit(new TRFCFunctionHandlerThread(tid, jcofunction, connectionName, idocHandler));
			logger.log(Level.INFO, "Process is handed over to a new thread. Now exiting from method.");
			boolean listen = true;
			while (listen) {
				if (future.isDone()) {
					String result=null;
					try {
						result = future.get();
						logger.log(Level.INFO, "Thread future object: {0}", result); 

						if(!"success".equals(result))
						{
							logger.log(Level.INFO, "Error occurred in Listener: {0}", result); 
							writeProcessInterruptFile(tid);
							throw new RuntimeException(result);
						}
						listen = false;
					} catch (Exception e) {
						e.printStackTrace();
						throw new RuntimeException(IDocServerMessages.ExceptionRaised+tid);
					}
				}
			}
		}
		
		
		public void processPacketSequentially (String tid,JCoFunction jcofunction,IDocHandler idocHandler) throws IOException
		{
			logger.log(Level.INFO, "Number of threads started is: 1");
			String errMssg=new TRFCFunctionHandlerThread(tid, jcofunction, connectionName, idocHandler).call();
			if(!"success".equals(errMssg))
			{
				logger.log(Level.INFO, "Error occurred in Listener: {0}", errMssg); 
				writeProcessInterruptFile(tid);
				throw new RuntimeException(errMssg);
			}
		}
		
		@Override
		/**
		 * This function will be invoked when a transactional RFC is being
		 * called from a SAP R/3 system. The function has to store the TID in
		 * permanent storage and return <code>true</code>. The method has to
		 * return <code>false</code> if the a transaction with this ID has
		 * already been process. Throw an exception if anything goes wrong. The
		 * transaction processing will be aborted thereafter. <b>Derived servers
		 * must override this method to actually implement the transaction ID
		 * management.
		 * 
		 * @param tid
		 *            the transaction ID
		 * @return <code>true</code> if the ID is valid and not in use
		 *         otherwise, <code>false</code> otherwise
		 */
		public boolean checkTID(JCoServerContext jcoservercontext, String tid) {

			final String METHODNAME = "checkTID(JCoServerContext jcoservercontext, String tid)"; //$NON-NLS-1$
			logger.entering(CLASSNAME, METHODNAME);
			
			logger.log(Level.INFO, "TID Manager: Checking TID {0}...", tid); //$NON-NLS-1$
			
			/* check if we already have an IDocHandler for this TID */
			IDocHandler idocHandler = this.idocHandlerMap.get(tid);
			if(idocHandler == null) {
				
				/* create new IDocHandler for this transaction */
				logger.log(Level.INFO, "TID Manager: Creating new IDocHandler for transaction {0}", tid); //$NON-NLS-1$
				idocHandler = new IDocHandlerImpl(defaultIDocTypesDirectory);
				
				/* bundle IDocHandler with transactionID */
				idocHandlerMap.put(tid, idocHandler);
				return true;
			}
			
			/* IDocHandler with this TID has already been executed */
			logger.log(Level.INFO, "TID Manager: Transaction {0} has already been executed by the IDocHandler", tid); //$NON-NLS-1$
			logger.exiting(CLASSNAME, METHODNAME);
			return false;
		}

		@Override
		/**
		 * This function will be called after <em>all</em> RFC functions
		 * belonging to a certain transaction have been successfully completed.
		 * <b>Derived servers can override this method to locally commit the
		 * transaction.
		 * 
		 * Any RuntimeException in commit will cause a rollback triggered by SAP
		 * 
		 * @param tid
		 *the transaction ID
		 */
		public void commit(JCoServerContext jcoservercontext, String tid) {

			final String METHODNAME = "commit(JCoServerContext jcoservercontext, String s)"; //$NON-NLS-1$
			logger.entering(CLASSNAME, METHODNAME);
			
			/* get IDocHandler for this transaction ID */
			IDocHandler idocHandler = this.idocHandlerMap.get(tid);
			
			if(idocHandler != null) {
				/* commit transaction and start DataStage jobs if necessary */
				idocHandler.commit();
				logger.log(Level.INFO, "TID Manager: Committing IDocHandler for transaction {0}", tid); //$NON-NLS-1$
			}
			
			logger.exiting(CLASSNAME, METHODNAME);

		}

		@Override
		/**
		 * This function will be called after the <em>local</em> transaction
		 * has been completed. All resources assiciated with this TID can be
		 * released. <b>Derived servers must override this method to actually
		 * implement the transaction ID management.
		 * 
		 * @param tid
		 *            the transaction ID
		 */
		public void confirmTID(JCoServerContext jcoservercontext, String tid) {

			final String METHODNAME = "confirmTID(JCoServerContext jcoservercontext, String s)"; //$NON-NLS-1$
			logger.entering(CLASSNAME, METHODNAME);
		
			/* get IDocHandler for this transaction */
			IDocHandler handler = this.idocHandlerMap.get(tid);
		
			/* only confirm if IDocHandler is in TransactionState.COMMITED - do nothing if transaction has been rolled back */
			if(handler != null && handler.getTransactionState().equals(TransactionState.COMMITTED)) {
				/* confirm transaction - send status IDocs and start DataStage jobs */
				handler.confirmTransaction(idocServerProperties, destination);
			}
			
			/* transaction has been completed - remove IDocHandler */
			logger.log(Level.INFO, "TID Manager: Deleting IDocHandler for completed transaction {0}", tid); //$NON-NLS-1$
			this.idocHandlerMap.remove(tid);
			
			/* Log status message: Waiting for outbound IDocs ... */
			logger.log(Level.INFO, IDocServerMessages.DSSAPConnection, connectionName);
			
			logger.exiting(CLASSNAME, METHODNAME);
		}

		@Override
		/**
		 * This function will be called if an error in one of the RFC functions
		 * belonging to a certain transaction has occurred. <b>Derived servers
		 * can override this method to locally rollback the transaction.
		 * 
		 * @param tid
		 *            the transaction ID
		 */
		public void rollback(JCoServerContext jcoservercontext, String tid) {

			final String METHODNAME = "rollback(JCoServerContext jcoservercontext, String s)"; //$NON-NLS-1$
			logger.entering(CLASSNAME, METHODNAME);
			
			/* roll back IDoc Handler */
			IDocHandler idocHandler = this.idocHandlerMap.get(tid);
			if(idocHandler != null) {
				idocHandler.rollback();
				logger.log(Level.INFO, "TID Manager: Rolling back IDocHandler for transaction {0}", tid); //$NON-NLS-1$
			}
		
			logger.exiting(CLASSNAME, METHODNAME);
		}

	}

	/**
	 * ServerConnectionProvider
	 * 
	 * Stores the configuration for a JCo server connection. The
	 * server connection is used to receive incoming calls from SAP.
	 * 
	 */
	class ServerConnectionProvider implements ServerDataProvider {

		private final String CLASSNAME = ServerConnectionProvider.class.getName();
		private Map<String, Properties> serverProperties = null;

		private ServerConnectionProvider(Map<String, String> dsProperties) {
			final String METHODNAME = "ServerConnectionProvider(Map<String, String> properties)"; //$NON-NLS-1$
			logger.entering(CLASSNAME, METHODNAME);

			serverProperties = new HashMap<String, Properties>();
			Properties props = new Properties();

			logger.log(Level.INFO, "Setting JCo server connection properties"); //$NON-NLS-1$
			
			/* JCO_GWHOST */
			String gwHost = dsProperties.get(IDocServerConstants.SAPAPPSERVER);
			logger.log(Level.INFO, "JCO_GWHOST = {0}", gwHost); //$NON-NLS-1$
			props.put(ServerDataProvider.JCO_GWHOST, gwHost);
			
			/* JCO_SAPROUTER */
			String sapRouter = dsProperties.get(IDocServerConstants.SAPROUTERSTRING);
			logger.log(Level.INFO, "JCO_SAPROUTER = {0}", sapRouter); //$NON-NLS-1$
			props.put(ServerDataProvider.JCO_SAPROUTER, sapRouter);

			/* JCO_GWSERV */
			String gwService = "sapgw" + dsProperties.get(IDocServerConstants.SAPSYSNUM); //$NON-NLS-1$
			logger.log(Level.INFO, "JCO_GWSERV = {0}", gwService); //$NON-NLS-1$
			props.put(ServerDataProvider.JCO_GWSERV, gwService);
			
			/* JCO_PROGID */
			String progID = dsProperties.get(IDocServerConstants.REFSERVERPROGID);
			logger.log(Level.INFO, "JCO_PROGID = {0}", progID); //$NON-NLS-1$
			props.put(ServerDataProvider.JCO_PROGID, progID);
			
			/* JCO_CONNECTION_COUNT */
			String conCount = dsProperties.get(IDocServerConstants.RFCSERVERCOUNT);
			logger.log(Level.INFO, "JCO_CONNECTION_COUNT = {0}", conCount); //$NON-NLS-1$
			props.put(ServerDataProvider.JCO_CONNECTION_COUNT, conCount); 
			
			/* store properties with key connection name */
			//serverProperties.put(dsProperties.get(IDocServerConstants.NAME), props);
			serverProperties.put(dsProperties.get(IDocServerConstants.NAME) + "_server", props);

			logger.exiting(CLASSNAME, METHODNAME);
		}

		@Override
		public Properties getServerProperties(String key) {
			return serverProperties.get(key);
		}

		@Override
		public void setServerDataEventListener(ServerDataEventListener arg0) {
			// Nothing needs to be done here
		}

		@Override
		public boolean supportsEvents() {
			return false;
		}
	}
	
	/**
	 * DestinationConnectionProvider
	 * 
	 * Stores the configuration for a JCo client connection. The
	 * client connection is used to retrieve repository meta data from the SAP
	 * application server.
	 * 
	 */
	class DestinationConnectionProvider implements DestinationDataProvider {

		private final String CLASSNAME = DestinationConnectionProvider.class.getName();
		private Map<String, Properties> destinationProperties = null;
		
		private DestinationConnectionProvider(Map<String, String> dsProperties) {
			final String METHODNAME = "DestinationConnectionProvider(Map<String, String> properties)"; //$NON-NLS-1$
			logger.entering(CLASSNAME, METHODNAME);

			destinationProperties = new HashMap<String, Properties>();
			Properties props = new Properties();

			logger.log(Level.INFO, "Setting JCo client connection properties"); //$NON-NLS-1$
			
			boolean useLoadBalancing = IDocServerConstants.TRUE.equalsIgnoreCase(dsProperties.get(IDocServerConstants.USELOADBALANCING)); 
			
			if (!useLoadBalancing) {
				/* JCO_ASHOST */
				String asHost = dsProperties.get(IDocServerConstants.SAPAPPSERVER);
				logger.log(Level.INFO, "JCO_ASHOST = {0}", asHost); //$NON-NLS-1$
				props.put(DestinationDataProvider.JCO_ASHOST, asHost);
				
				/* JCO_SYSNR */
				String sysNr = dsProperties.get(IDocServerConstants.SAPSYSNUM);
				logger.log(Level.INFO, "JCO_SYSNR = {0}", sysNr); //$NON-NLS-1$
				props.put(DestinationDataProvider.JCO_SYSNR, sysNr);
			} else {
				/* JCO_MSHOST */
				String msHost = dsProperties.get(IDocServerConstants.SAPMESSERVER);
				logger.log(Level.INFO, "JCO_MSHOST = {0}", msHost); //$NON-NLS-1$
				props.put(DestinationDataProvider.JCO_MSHOST, msHost);
				
				/* JCO_R3NAME */
				String sysId = dsProperties.get(IDocServerConstants.SAPSYSID);
				logger.log(Level.INFO, "JCO_R3NAME = {0}", sysId); //$NON-NLS-1$
				props.put(DestinationDataProvider.JCO_R3NAME, sysId);
				
				/* JCO_GROUP */
				String group = dsProperties.get(IDocServerConstants.SAPGROUP);
				logger.log(Level.INFO, "JCO_GROUP = {0}", group); //$NON-NLS-1$
				props.put(DestinationDataProvider.JCO_GROUP, group);
			}
			
			/* JCO_SAPROUTER */
			String sapRouter = dsProperties.get(IDocServerConstants.SAPROUTERSTRING);
			logger.log(Level.INFO, "JCO_SAPROUTER = {0}", sapRouter); //$NON-NLS-1$
			props.put(DestinationDataProvider.JCO_SAPROUTER, sapRouter);
			
			/* JCO_USER */
			String user = dsProperties.get(IDocServerConstants.DEFAULTUSERNAME);
			logger.log(Level.INFO, "JCO_USER = {0}", user); //$NON-NLS-1$
			props.put(DestinationDataProvider.JCO_USER, user);
			
			/* JCO_PASSWD */
			String encryptedPW = dsProperties.get(IDocServerConstants.DEFAULTPASSWORD);
			String decryptedPW = com.ibm.is.sappack.dsstages.common.Utilities.convertConfigFilePW(encryptedPW);
			logger.log(Level.INFO, "JCO_PASSWD = {0}", encryptedPW); //$NON-NLS-1$
			props.put(DestinationDataProvider.JCO_PASSWD, decryptedPW);
			
			/* JCO_CLIENT */
			String client = dsProperties.get(IDocServerConstants.DEFAULTCLIENT);
			logger.log(Level.INFO, "JCO_CLIENT = {0}", client); //$NON-NLS-1$
			props.put(DestinationDataProvider.JCO_CLIENT, client);
			
			/* JCO_LANG */
			String lang = dsProperties.get(IDocServerConstants.DEFAULTLANGUAGE);
			logger.log(Level.INFO, "JCO_LANG = {0}", lang); //$NON-NLS-1$
			props.put(DestinationDataProvider.JCO_LANG, lang);
			
			/* store properties with key connection name */
			destinationProperties.put(dsProperties.get(IDocServerConstants.NAME), props);

			logger.exiting(CLASSNAME, METHODNAME);
		}

		@Override
		public boolean supportsEvents() {
			return false;
		}

		@Override
		public Properties getDestinationProperties(String key) {
			return destinationProperties.get(key);
		}

		@Override
		public void setDestinationDataEventListener(DestinationDataEventListener destinationdataeventlistener) {
			// Nothing needs to be done here
		}
	}

	/**
	 * IDocServerExceptionListener
	 * 
	 * Listener for IDocServer Exceptions and Errors
	 *
	 */
	class IDocServerExceptionListener implements JCoServerExceptionListener, JCoServerErrorListener {

		/* class name for logging purposes */
		private final String CLASSNAME = IDocServerExceptionListener.class.getName();
		
		@Override
		public void serverExceptionOccurred(JCoServer server, String connection, JCoServerContextInfo ctx, Exception e) {
			
			final String METHODNAME = "serverExceptionOccurred(JCoServer server, String connection, JCoServerContextInfo ctx, Exception e)"; //$NON-NLS-1$
			logger.entering(CLASSNAME, METHODNAME);
			
			logger.log(Level.WARNING, e.getMessage());
			
			logger.exiting(CLASSNAME, METHODNAME);
		}

		@Override
		public void serverErrorOccurred(JCoServer server, String connection, JCoServerContextInfo ctx, Error e) {

			final String METHODNAME = "serverErrorOccurred(JCoServer server, String connection, JCoServerContextInfo ctx, Error e)"; //$NON-NLS-1$
			logger.entering(CLASSNAME, METHODNAME);
			
			logger.log(Level.WARNING, e.getMessage());
			
			logger.exiting(CLASSNAME, METHODNAME);
		}
		
	}
	
	/**
	 * IDocServerStateChangedListener
	 * 
	 * Listener for IDocServer state changes
	 *
	 */
	class IDocServerStateChangedListener implements JCoServerStateChangedListener {

		/* class name for logging purposes */
		private final String CLASSNAME = IDocServerStateChangedListener.class.getName();
		
		
		@Override
		/**
		 * serverStateChangeOccurred
		 * 
		 * @param server
		 * @param oldState
		 * @param newState
		 */
		public void serverStateChangeOccurred(JCoServer server, JCoServerState oldState, JCoServerState newState) {
			
			final String METHODNAME = "serverStateChangeOccurred(JCoServer server, JCoServerState oldState, JCoServerState newState)"; //$NON-NLS-1$
			logger.entering(CLASSNAME, METHODNAME);
			
			/* log state changes */
			logger.log(Level.INFO, IDocServerMessages.ServerStateChange, new Object[]{oldState.toString(), newState.toString()});
			
			/* check if the server is still running and update the 'serverRunning' global variable */
			if(newState.equals(JCoServerState.ALIVE) || newState.equals(JCoServerState.STARTED)) {
				serverRunning = true;
			} else {
				/* server is not running anymore */
				serverRunning = false;
			}
			
			logger.exiting(CLASSNAME, METHODNAME);
		}
		
	}
	private void writeProcessInterruptFile(String tid) throws IOException
	{
		String requestId=IDocServerImpl.initfileInfo.get(tid);
		errorReqNum.put(requestId,"");
		String location=IDocServerImpl.initfileInfo.get(requestId.trim());
		logger.log(Level.INFO, "requestId: {0} , location: {1}", new Object[]{requestId,location}); 
		File file=new File(location+"ProcessInterrupted");
		if(!file.exists()){
			file.mkdirs();
			logger.log(Level.INFO, "Directory created on location: {0} ", location+"ProcessInterrupted");
		}
		FileWriter fw=new FileWriter(location+"ProcessInterrupted/ProcessInterrupted.log");
		fw.write(IDocServerMessages.ProcessInterrupted+requestId+IDocServerMessages.ProcessInterrupted1);
		fw.close();
	}

}
