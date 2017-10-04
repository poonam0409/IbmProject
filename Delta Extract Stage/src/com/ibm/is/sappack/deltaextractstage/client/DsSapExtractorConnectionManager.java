//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2015                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.is.sappack.dsstages.common
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.deltaextractstage.client;

import com.ibm.is.sappack.deltaextractstage.commons.DsSapExtractorParam;
import com.ibm.is.sappack.deltaextractstage.utils.DsSapExtractorUtility;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoRepository;
import com.sap.conn.jco.ext.DestinationDataProvider;

import java.util.Properties;
import java.util.logging.Level;

public class DsSapExtractorConnectionManager {

    /**
     * SAP Connection type constants
     */
    public static final int SAP_CLNT_CONNECTION = 1;
    public static final int SAP_MSGSERV_CONNECTION = 2;
    public int SAP_CONNECTION_TYPE = 0;
    private int conntype= 1;
    /**
     * SAP Connection parameters
     */
    private int SapMaxConnections = 10;
    private String SapValidationErrorMessages = null;
    private String SapClient = null;
    private String SapUser = null;
    private String SapPassword = null;
    private String SapLanguage = null;
    private String SapApplicationServerHostName = null;
    private String SapSystemNr = null;
    private String SapPoolId = null;
    
    private String msgServer = null; //HOSTNAME   3601/TCP   /H/200.201.202.203/S/3601
    private String groupName = null;//PUBLIC/SPACE
    private String routerString = null; //
    private String sapSystemId = null;
    private boolean isMessageServerSystem = false;  
    
    
    
    private final Properties connectProperties = new Properties();
    private JCoDestination extractorDestination = null;
    private JCoRepository extratorRepository = null;
    private final DsSapExtractorLogger extractorOTLogger;
    public String getMsgServer() {
		return msgServer;
	}

	public void setMsgServer(String msgServer) {
		this.msgServer = msgServer;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getRouterString() {
		return routerString;
	}

	public void setRouterString(String routerString) {
		this.routerString = routerString;
	}

	public String getSapSystemId() {
		return sapSystemId;
	}

	public void setSapSystemId(String sapSystemId) {
		this.sapSystemId = sapSystemId;
	}

	public boolean isMessageServerSystem() {
		return isMessageServerSystem;
	}

	public void setMessageServerSystem(boolean isMessageServerSystem) {
		this.isMessageServerSystem = isMessageServerSystem;
	}

	public String getSapApplicationServerHostName() {
        return SapApplicationServerHostName;
    }

    public void setSapApplicationServerHostName(String SapApplicationServerHostName) {
        this.SapApplicationServerHostName = SapApplicationServerHostName;
    }

    public String getSapClient() {
        return SapClient;
    }

    public void setSapClient(String SapClient) {
        this.SapClient = SapClient;
    }

    public String getSapLanguage() {
        return SapLanguage;
    }

    public void setSapLanguage(String SapLanguage) {
        this.SapLanguage = SapLanguage;
    }

 /*   public int getSapMaxConnections() {
        return SapMaxConnections;
    }

    public void setSapMaxConnections(int SapMaxConnections) {
        this.SapMaxConnections = SapMaxConnections;
    }*/

    public String getSapPoolId() {
        return SapPoolId;
    }

    public void setSapPoolId(String SapPoolId) {
        this.SapPoolId = SapPoolId;
    }

    public String getSapPassword() {
        return SapPassword;
    }

    public void setSapPassword(String SapPassword) {
        this.SapPassword = SapPassword;
    }

    public String getSapSystemNr() {
        return SapSystemNr;
    }

    public void setSapSystemNr(String SapSystemNr) {
        this.SapSystemNr = SapSystemNr;
    }

    public String getSapUser() {
        return SapUser;
    }

    public void setSapUser(String SapUser) {
        this.SapUser = SapUser;
    }

    public JCoDestination getExtractorDestination() throws JCoException {
        if (extractorDestination == null) {
            extractorDestination = JCoDestinationManager.getDestination(SapPoolId);
        }
        return extractorDestination;
    }

    public JCoRepository getExtratorRepository() throws JCoException {
        if (extratorRepository == null) {
            extratorRepository = getExtractorDestination().getRepository();
        }
        return extratorRepository;
    }

    public DsSapExtractorConnectionManager(DsSapExtractorLogger extractorLogger, DsSapExtractorParam otPara) {
        this.extractorOTLogger = extractorLogger;
    }

    public DsSapExtractorConnectionManager(JCoDestination extratorDestination, DsSapExtractorLogger extractorLogger) {
        this.extractorDestination = extratorDestination;
        this.extractorOTLogger = extractorLogger;
    }

    /**
     * Validates the data existence into the parameters depending upon the type
     * of connection.
     *
     * @param contype
     * @return
     */
    private boolean validateParameterEntries(int contype) {
        boolean status = false;
        switch (contype) {
            case SAP_CLNT_CONNECTION: {
                DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"Validating the Client Connection parameters ..."});
                if (SapApplicationServerHostName.isEmpty()) {
                    SapValidationErrorMessages = "SAP Application Server Name or IP is missing ...";
                } else if (SapSystemNr.isEmpty()) {
                    SapValidationErrorMessages = "SAP System Number is missing ...";
                } else if (SapClient.isEmpty()) {
                    SapValidationErrorMessages = "SAP Client is missing ...";
                } else if (SapUser.isEmpty()) {
                    SapValidationErrorMessages = "SAP User is missing ...";
                } else if (SapPassword.isEmpty()) {
                    SapValidationErrorMessages = "SAP Password is missing ...";
                } else if (SapLanguage.isEmpty()) {
                    SapValidationErrorMessages = "SAP Language is missing ...";
                } else {
                    status = true;
                    DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"Client Connection parameters are valid ..."});
                }
            }
            ;
            break;
            
            case SAP_MSGSERV_CONNECTION: {
            	DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"Validating the Client Connection parameters for Load Balancing..."});
            	if (msgServer.isEmpty()) {
                    SapValidationErrorMessages = "SAP Message Server Name or IP is missing ...";
                } else if (sapSystemId.isEmpty()) {
                    SapValidationErrorMessages = "SAP System ID is missing ...";
                } else if (groupName.isEmpty()) {
                    SapValidationErrorMessages = "SAP Group Name is missing ...";
                } else if (SapClient.isEmpty()) {
                    SapValidationErrorMessages = "SAP Client is missing ...";
                } else if (SapUser.isEmpty()) {
                    SapValidationErrorMessages = "SAP User is missing ...";
                } else if (SapPassword.isEmpty()) {
                    SapValidationErrorMessages = "SAP Password is missing ...";
                } else if (SapLanguage.isEmpty()) {
                    SapValidationErrorMessages = "SAP Language is missing ...";
                } else {
                    status = true;
                    DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"Client Connection parameters are valid ..."});
                }
            
            };
            break;
        }
        return status;
    }

    /**
     * Initializes the SAP Client connection for the extractor.
     */
    public void initializeClientConnection() {
    	 if(isMessageServerSystem())
    		 conntype = 2;
        if (validateParameterEntries(conntype)) {
            DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"Initializing the Client Connection pool ..."});
            try {
                connectProperties.setProperty(DestinationDataProvider.JCO_CLIENT, SapClient);
                connectProperties.setProperty(DestinationDataProvider.JCO_USER, SapUser);
                connectProperties.setProperty(DestinationDataProvider.JCO_PASSWD, SapPassword);
                connectProperties.setProperty(DestinationDataProvider.JCO_LANG, SapLanguage);
      
                if (!isMessageServerSystem()) {
                	connectProperties.setProperty(DestinationDataProvider.JCO_ASHOST, SapApplicationServerHostName);
                	connectProperties.setProperty(DestinationDataProvider.JCO_SYSNR, SapSystemNr); //$NON-NLS-1$
                	 connectProperties.setProperty(DestinationDataProvider.JCO_MAX_GET_TIME, "5000");
                     connectProperties.setProperty(DestinationDataProvider.JCO_POOL_CAPACITY, Integer.toString(SapMaxConnections));
        		} else {
        			connectProperties.setProperty(DestinationDataProvider.JCO_MSHOST, msgServer);
        			connectProperties.setProperty(DestinationDataProvider.JCO_R3NAME, sapSystemId); //$NON-NLS-1$
        			connectProperties.setProperty(DestinationDataProvider.JCO_GROUP, groupName);

        			// only set the router string property if it has been set in the UI
        			// do not attempt to use things like an empty string or even NULL
        		}
        		if (getRouterString() != null) {
        			connectProperties.setProperty(DestinationDataProvider.JCO_SAPROUTER, routerString);
        		}
 
                com.ibm.is.sappack.deltaextractstage.client.DsSapDestinationDataProvider.defineDestination(SapPoolId, connectProperties);
            } catch (Exception e) {
                DsSapExtractorUtility.throwExtractorException(e, extractorOTLogger);
            }
            DsSapExtractorLogger.writeToLogFile(Level.FINE,"DeltaExtract_10000", new Object[]{"Client Connection pool initialization done ..."});
        } else {
            DsSapExtractorUtility.throwExtractorException(SapValidationErrorMessages, extractorOTLogger);
        }
    }  

    public void releaseResource() {
        extratorRepository = null;
        extractorDestination = null;
    }
    
}
