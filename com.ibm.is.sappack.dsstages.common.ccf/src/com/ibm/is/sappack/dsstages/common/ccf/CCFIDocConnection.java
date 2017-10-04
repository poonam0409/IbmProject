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
// Module Name : com.ibm.is.sappack.dsstages.common.ccf
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.dsstages.common.ccf;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ascential.e2.connector.CC_Connection;
import com.ascential.e2.propertyset.CC_ErrorList;
import com.ascential.e2.propertyset.CC_PropertySet;
import com.ibm.is.sappack.dsstages.common.CCFResource;
import com.ibm.is.sappack.dsstages.common.Constants;
import com.ibm.is.sappack.dsstages.common.DSSAPConnection;
import com.ibm.is.sappack.dsstages.common.IDocMetadataFactory;
import com.ibm.is.sappack.dsstages.common.IDocType;
import com.ibm.is.sappack.dsstages.common.IDocTypeConfiguration;
import com.ibm.is.sappack.dsstages.common.SapSystem;
import com.ibm.is.sappack.dsstages.common.StageLogger;
import com.ibm.is.sappack.dsstages.common.impl.RfcDestinationDataProvider;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;

public class CCFIDocConnection extends CC_Connection implements IDocConnection {

	static String copyright() {
		return com.ibm.is.sappack.dsstages.common.ccf.Copyright.IBM_COPYRIGHT_SHORT;
	}
	
	final static String CLASSNAME = CCFIDocConnection.class.getName();

	protected Logger logger = null;

	String dsSAPConnectionName = null;
	DSSAPConnection sapConnectionMetadata = null;
	JCoDestination jcoDest = null;
	IDocType idocType = null;

	Map<String, String> stageProperties = null;

	public CCFIDocConnection(CC_PropertySet propSet, CC_ErrorList errList) {
		super(propSet, errList);
		this.logger = StageLogger.getLogger();
		final String METHODNAME = "<init>"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);
		
		try {
	      /* call createPropertyMap without requiredConnectionProperties */
	      this.stageProperties = CCFUtils.createPropertyMap(propSet.getProperty("/Connection"), errList, null); //$NON-NLS-1$
		}
		catch(RuntimeException rtExcpt) {
         this.logger.log(Level.SEVERE, rtExcpt.toString());
         
         StringWriter sw = new StringWriter();
         rtExcpt.printStackTrace(new PrintWriter(sw));
         logger.log(Level.SEVERE, "stack trace = {0}", new Object[] { sw.toString() });
         
         throw rtExcpt;
		}
		
		/* check if IDOCTYP is set */
		if(this.stageProperties.get(Constants.IDOCSTAGE_STAGE_PROP_IDOCTYP) == null) {
			String msgId = "CC_IDOC_IDOCTYPNotFound";  //$NON-NLS-1$
			this.logger.log(Level.SEVERE, msgId);
			throw new RuntimeException(CCFResource.getCCFMessage(msgId));
		}
		
		/* call createPropertyMap with requiredConnectionProperties */
		this.stageProperties = CCFUtils.createPropertyMap(propSet.getProperty("/Connection"), errList, getRequiredConnectionPropeties()); //$NON-NLS-1$
		
		
		this.logger.log(Level.FINE, "Connection properties: {0}", this.stageProperties); //$NON-NLS-1$
		this.sapConnectionMetadata = IDocMetadataFactory.createDSSAPConnection();
		this.dsSAPConnectionName = stageProperties.get(Constants.IDOCSTAGE_STAGE_PROP_CONNECTION);
		this.logger.log(Level.INFO, "CC_IDOC_InitializingConnection", new Object[] { this.dsSAPConnectionName }); //$NON-NLS-1$
		
		if (this.dsSAPConnectionName != null) { // Uppercase the connection name
		    this.dsSAPConnectionName = this.dsSAPConnectionName.toUpperCase();
		}

		try {
			this.logger.log(Level.FINE, " inside CCFIDoc dsSAPConnectionName {0}", this.dsSAPConnectionName); //$NON-NLS-1$
			
			this.sapConnectionMetadata.initialize(this.dsSAPConnectionName);
		} catch (IOException e) {
			this.logger.log(Level.SEVERE, "CC_IDOC_ExceptionWhileCreatingConnection", //$NON-NLS-1$
			                new Object[] { this.dsSAPConnectionName });
			this.logger.log(Level.SEVERE, "CC_IDOC_CommonUnexpectedException", e);    //$NON-NLS-1$

			CCFUtils.throwCC_Exception(e);
		}
		this.logger.exiting(CLASSNAME, METHODNAME);
	}
	
	String[] getRequiredConnectionPropeties() {
		return new String[] {
				Constants.IDOCSTAGE_STAGE_PROP_CONNECTION,
				Constants.IDOCSTAGE_STAGE_PROP_DEFSAPLOGON,
				Constants.IDOCSTAGE_STAGE_PROP_IDOCTYP			
		};
	}

	void createJCODestination() throws JCoException {
		final String METHODNAME = "createJCODestination()"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);
		/* check overwritten stage properties */
		String invalidLogonDetailsMsgId = null;
		SapSystem sapSystem = this.sapConnectionMetadata.getSapSystem().copy();
		if ("0".equals(stageProperties.get(Constants.IDOCSTAGE_STAGE_PROP_DEFSAPLOGON))) { //$NON-NLS-1$
			invalidLogonDetailsMsgId = "CC_IDOC_InvalidSAPLogonDetailsInStage"; //$NON-NLS-1$
			logger.log(Level.FINEST, "Overriding SAP connection logon defaults"); //$NON-NLS-1$

			/* update SAP log on details */
			String username = stageProperties.get(Constants.IDOCSTAGE_STAGE_PROP_USERNAME);
			if (username == null) {
				username = ""; //$NON-NLS-1$
			}
			String password = stageProperties.get(Constants.IDOCSTAGE_STAGE_PROP_PASSWORD);
			if (password == null) {
				password = ""; //$NON-NLS-1$
			}
			String clientID = stageProperties.get(Constants.IDOCSTAGE_STAGE_PROP_CLIENT);
			if (clientID == null) {
				clientID = "0"; //$NON-NLS-1$
			}

			String language = stageProperties.get(Constants.IDOCSTAGE_STAGE_PROP_LANGUAGE);
			if (language == null) {
				language = ""; //$NON-NLS-1$
			}

			sapSystem.setUserName(username);
			sapSystem.setPassword(password);
			try {
				sapSystem.setClientId(Integer.parseInt(clientID));
			} catch (NumberFormatException e) {
				logger.fine(CCFResource.getCCFMessage("CC_IDOC_ClientIDNotNumeric")); //$NON-NLS-1$
				sapSystem.setClientId(0);
			}
			sapSystem.setLanguage(language);

		} else {
			invalidLogonDetailsMsgId = "CC_IDOC_InvalidSAPLogonDetailsInConnection"; //$NON-NLS-1$
			logger.log(Level.FINE, "Using SAP connection logon defaults"); //$NON-NLS-1$
		}

		checkString(sapSystem.getUserName(), invalidLogonDetailsMsgId, CCFResource.getCCFText("CC_IDOC_SAPUser"));
		checkString(sapSystem.getPassword(), invalidLogonDetailsMsgId, CCFResource.getCCFText("CC_IDOC_SAPPassword"));
		checkString(sapSystem.getLanguage(), invalidLogonDetailsMsgId, CCFResource.getCCFText("CC_IDOC_SAPLangue"));
		int clientID = sapSystem.getClientId();
		if (clientID == 0) {
			this.logger.log(Level.SEVERE, invalidLogonDetailsMsgId);
			throw new RuntimeException(CCFResource.getCCFMessage(invalidLogonDetailsMsgId));
		}

		this.jcoDest = RfcDestinationDataProvider.getDestination(sapSystem);
		this.logger.entering(CLASSNAME, METHODNAME);
	}

	private void checkString(String s, String errorString, String paramName) {
		if (s == null || s.trim().length() == 0) {
			if (paramName == null) {
				this.logger.log(Level.SEVERE, errorString);
			}
			else {
				this.logger.log(Level.SEVERE, errorString, paramName);
			}
			throw new RuntimeException(CCFResource.getCCFMessage(errorString, paramName));			
		}
		
	}
	
	void fetchIDocType() throws JCoException, IOException {
		final String METHODNAME = "fetchIDocType()"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);
		String idocTypeName = this.stageProperties.get(Constants.IDOCSTAGE_STAGE_PROP_IDOCTYP);
		String basicTypeName = this.stageProperties.get(Constants.IDOCSTAGE_STAGE_PROP_BASTYP);
		this.logger.log(Level.FINE, "Fetching IDoc Type {0}, basic type: {1}", new Object[]{ idocTypeName, basicTypeName } ); //$NON-NLS-1$
		String release = null;
		for (IDocTypeConfiguration idocConfig : this.sapConnectionMetadata.getIDocTypeConfigurations()) {
			if (idocConfig.getIDocTypeName().equals(idocTypeName)) {
				release = idocConfig.getRelease();
				this.logger.log(Level.FINE, "   Found release {0} for {1}", new Object[] { release, idocTypeName }); //$NON-NLS-1$
				break;
			}
		}
		this.logger.log(Level.INFO, "CC_IDOC_ReadingIDocTypeWithRelease",               //$NON-NLS-1$
		                new Object[] { idocTypeName, release == null ? "" : release }); //$NON-NLS-1$
		this.idocType = IDocMetadataFactory.createIDocType(this.jcoDest, this.dsSAPConnectionName, idocTypeName, basicTypeName, release);
		if (this.idocType == null) {
			String msgId = "CC_IDOC_TypeNotFoundInSAP"; //$NON-NLS-1$
			this.logger.log(Level.SEVERE, msgId, new Object[] { idocTypeName } );
			throw new RuntimeException(CCFResource.getCCFMessage(msgId, idocTypeName));
		}

		this.logger.exiting(CLASSNAME, METHODNAME);
	}

	@Override
	public void beginLocalTransaction() {
		final String METHODNAME = "beginLocalTransaction"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);
		this.logger.exiting(CLASSNAME, METHODNAME);
	}

	@Override
	public void connect() {
		final String METHODNAME = "connect"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);

		try {
			createJCODestination();

			jcoDest.ping();

			fetchIDocType();
		} catch (JCoException e) {
			this.logger.log(Level.SEVERE, "CC_IDOC_UnexpectedSAPException", e); //$NON-NLS-1$
			CCFUtils.throwCC_Exception(e);
		} catch (Throwable t) {
			CCFUtils.handleException(t);
		}
		this.logger.exiting(CLASSNAME, METHODNAME);
	}

	@Override
	public void disconnect() {
		final String METHODNAME = "disconnect"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);
		this.jcoDest = null;
		this.idocType = null;
		this.logger.exiting(CLASSNAME, METHODNAME);
	}

	@Override
	public void endLocalTransaction(int arg0) {
		final String METHODNAME = "endLocalTransaction"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);
		this.logger.exiting(CLASSNAME, METHODNAME);
	}

	@Override
	public void reset() {
		final String METHODNAME = "reset"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);
		// nothing to be done
		this.logger.exiting(CLASSNAME, METHODNAME);
	}

	@Override
	public boolean test() {
		final String METHODNAME = "test"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);
		boolean result = true;
		try {
			jcoDest.ping();
		} catch (JCoException e) {
			this.logger.log(Level.SEVERE, "CC_IDOC_UnexpectedSAPException", e); //$NON-NLS-1$
			result = false;
		} catch (Throwable t) {
			CCFUtils.handleException(t);
		}
		this.logger.exiting(CLASSNAME, METHODNAME);
		return result;
	}

	public DSSAPConnection getDSSAPConnection() {
		final String METHODNAME = "getDSSAPConnection"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);
		this.logger.exiting(CLASSNAME, METHODNAME);
		return this.sapConnectionMetadata;
	}

	public IDocType getIDocType() {
		final String METHODNAME = "getIDocType()"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);
		this.logger.exiting(CLASSNAME, METHODNAME);
		return this.idocType;

	}

	public JCoDestination getJCODestination() {
		return this.jcoDest;
	}
}
