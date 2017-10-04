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
// Module Name : com.ibm.is.sappack.dsstages.xmeta
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.dsstages.xmeta;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.emf.common.util.EList;

import ASCLModel.ASCLModelFactory;
import ASCLModel.ASCLModelPackage;
import ASCLModel.DataCollection;
import ASCLModel.DataField;
import ASCLModel.DataFile;
import ASCLModel.DataItemDef;
import ASCLModel.Dimension;
import ASCLModel.HostSystem;
import ASCLModel.ODBCTypeEnum;
import ASCLModel.TypeCodeEnum;

import com.ibm.datastage.exception.DSException;
import com.ibm.datastage.mapping.MappingException;
import com.ibm.is.sappack.dsstages.common.IDocField;
import com.ibm.is.sappack.dsstages.common.IDocSegment;
import com.ibm.is.sappack.dsstages.common.IDocType;
import com.ibm.is.sappack.dsstages.common.impl.ControlRecord;
import com.ibm.is.sappack.dsstages.common.impl.IDocFieldImpl;
import com.ibm.is.sappack.dsstages.common.impl.IDocSegmentImpl;
import com.ibm.is.sappack.dsstages.common.impl.IDocTypeImpl;
import com.ibm.is.sappack.dsstages.common.util.IDocMetadataFileHandler;
import com.ibm.is.sappack.dsstages.xmeta.common.IDocXMetaLogger;
import com.ibm.is.sappack.dsstages.xmeta.common.ISVersionChecker;
import com.ibm.is.sappack.dsstages.xmeta.common.ISVersionChecker.ISEnvVersion;
import com.ibm.is.sappack.dsstages.xmeta.common.XMetaIDocHandlingException;
import com.ibm.is.sappack.dsstages.xmeta.common.XMetaIDocMetadataService;


@SuppressWarnings("nls")
/**
 * Handles the caching/saving of IDoc metadata in the Metadata Repository (XMeta).
 * IDoc elements are represented using standard Common Model (ASCLModel) objects.
 */
public class XMetaIDocMetadataHandler {
    
	private static final String CLASSNAME                 = XMetaIDocMetadataHandler.class.getName();

	private static final String XMETA_HOSTSYSTEM_TYPE     = XMetaIDocMetadataService.XMETA_HOSTSYSTEM_TYPE;
	private static final String XMETA_IDOC_TYPE           = "SAP_IDOC";
	private static final String XMETA_MESSAGE_TYPES       = "SAP_IDOC_MSG_TYPES";
	private static final String XMETA_MESSAGE_TYPE        = "SAP_IDOC_MSG_TYPE";
	private static final String XMETA_COLUMN_TYPE         = "SAP_IDOC_SEG_COL";
	private static final String XMETA_SEGMENT_TYPE        = "SAP_IDOC_SEG_TYPE";
	private static final String CONTROL_RECORD            = "CONTROL_RECORD";
	private static final String SAPTYPE_TIME              = "TIMS";
	private static final String SAPTYPE_DATE              = "DATS";
	private static final String SAPTYPE_CHAR              = "CHAR";

	private static final String LIST_SEPARATOR_REGEX      = "\\|";
	private static final String LIST_SEPARATOR            = "|";
	private static final String LIST_LINE_SEPARATOR       = "\n";
	private static final String LIST_LINE_SEPARATOR_REGEX = "\n";
	private static final String UNICODE                   = "Unicode";
	private static final String NON_UNICODE               = "Non-Unicode";

	private static Logger                   logger;
	private static XMetaIDocMetadataService serviceDelegator;
	private static boolean                  connectionInitialized = false;


   static {
   	ASCLModelFactory mdl1     = ASCLModelFactory.eINSTANCE;
   	ASCLModelPackage axmetaPk = ASCLModelPackage.eINSTANCE;
   }

	static {
   	logger = IDocXMetaLogger.getLogger();

  		// check what IS version is running and load the appropriate delegator class
  		if (ISVersionChecker.getISEnvironmentVersion(logger) == ISEnvVersion.v10x) {
  			serviceDelegator = new com.ibm.is.sappack.dsstages.xmeta_v10.XMetaIDocMetadataServiceImpl();
  		}
  		else {
  			serviceDelegator = new com.ibm.is.sappack.dsstages.xmeta_v8.XMetaIDocMetadataServiceImpl();
  		}
  		try {
  			serviceDelegator.initializeConnection(logger);
  		}
  		catch(XMetaIDocHandlingException xmetaHandlingExcpt) {
  			logger.log(Level.SEVERE, "", xmetaHandlingExcpt);
  		}
		connectionInitialized = true;
	}


	static String copyright() {
   	return com.ibm.is.sappack.dsstages.xmeta.Copyright.IBM_COPYRIGHT_SHORT;
   }

   public XMetaIDocMetadataHandler() throws MappingException, DSException, Exception {
   	logger.info("Constructor");
    }
    
    private void initializeConnection() throws MappingException, DSException, Exception {
        final String METHODNAME = "initializeConnection()";
        logger.entering(CLASSNAME, METHODNAME);

        // check what metadata service is to be loaded
        serviceDelegator.initializeConnection(logger);
        connectionInitialized = true;
        
        logger.exiting(CLASSNAME, METHODNAME);
    }

    private String generateIDocTypeNameQualifier(String idocTypeName, String basicTypeName, String release) {
        return idocTypeName + LIST_SEPARATOR + basicTypeName + LIST_SEPARATOR + release;
    }
    
    private String getBasicTypeNameFromNameQualifier(String nameQualifier) {
        logger.finer("Qualifier: " + nameQualifier);
        logger.finer("Parts: " + nameQualifier.split(LIST_SEPARATOR_REGEX).length);
        if (nameQualifier.split(LIST_SEPARATOR_REGEX).length > 1) {
            return nameQualifier.split(LIST_SEPARATOR_REGEX)[1];
        } else {
            return "";
        }
    }
    
    private String getReleaseFromNameQualifier(String nameQualifier) {
        if (nameQualifier.split(LIST_SEPARATOR_REGEX).length > 2) {
            return nameQualifier.split(LIST_SEPARATOR_REGEX)[2];
        } else {
            return "";
        }
    }
    
    private String generateHostSystemAttributeString(String hostRelease, int pcs) {
        logger.finer("Host system attributes: " + hostRelease + ", " + pcs);
        if (pcs == 2) {
            return hostRelease + LIST_SEPARATOR + UNICODE;
        } else {
            return hostRelease + LIST_SEPARATOR + NON_UNICODE;
        }
    }
    
    private String getSapReleaseFromAttributeString(String attributeString) {
        logger.finer("Attr string: >>>" + attributeString + "<<<");
        logger.finer("Release: " + attributeString.split(LIST_SEPARATOR_REGEX)[0]);
        return attributeString.split(LIST_SEPARATOR_REGEX)[0];
    }
    
    private int getPcsFromAttributeString(String attributeString) {
        String unicodeStr = attributeString.split(LIST_SEPARATOR_REGEX)[1];
        if (unicodeStr.equals(UNICODE)) {
            return 2;
        }
        else {
            return 1;
        }
    }
    
    public String getIDocTypeMetadata(String hostName, String idocTypeName, String basicTypeName, String release) throws MappingException, DSException, Exception {
        final String METHODNAME = "getIDocAsString(String, String, String, String)";
        logger.entering(CLASSNAME, METHODNAME);
        
        if (!connectionInitialized) {
            initializeConnection();
        }

        logger.info("Getting hostSystem");
        HostSystem hostSystem = getHostSystem(hostName);
        if (hostSystem == null) {
            logger.severe("Error retrieving hostSystem");
            logger.exiting(CLASSNAME, METHODNAME);
            return "";
        }
        
        logger.info("Getting dataFile");
        DataFile dataFile = getIdocDataFile(hostSystem, idocTypeName);
        if (dataFile == null) {
            logger.severe("Error retrieving dataFile");
            logger.exiting(CLASSNAME, METHODNAME);
            return "";
        }
        
        String nameQualifier = generateIDocTypeNameQualifier(idocTypeName, basicTypeName, release);
        logger.info("nameQualifier: " + nameQualifier);
        logger.info("Getting dataCollection");
        DataCollection dataCollection = getIdocTypeDC(dataFile, XMETA_IDOC_TYPE, idocTypeName, nameQualifier, false);
        if (dataCollection == null) {
            logger.severe("Error retrieving dataCollection");
            logger.exiting(CLASSNAME, METHODNAME);
            return "";
        }

        IDocTypeImpl idocType = new IDocTypeImpl();
        idocType.setIDocTypeName(idocTypeName);
        idocType.setBasicTypeName(basicTypeName);
        idocType.setRelease(release);
        
        logger.info("Getting segment DataFields");
        EList rootSegmentDataFields = dataCollection.getContains_DataField();

        logger.info("Root segments including CR: " + rootSegmentDataFields.size());
        for (Object o : rootSegmentDataFields) {
            retrieveSegment(idocType, (DataField)o, null);
        }
        
        // Use the IMF handler for serialization to avoid code duplication
        IDocMetadataFileHandler handler = new IDocMetadataFileHandler();
        logger.info("Got handler");
        String result = handler.serializeMetadata(idocType);
        
        logger.exiting(CLASSNAME, METHODNAME);
        return result;
    }
    
    public String getIDocSegmentMetadata(String hostName, String idocTypeName, String basicTypeName, String release, String segmentType) throws MappingException, DSException, Exception {
        final String METHODNAME = "getIDocSegmentMetadata(String, String, String, String, String)";
        logger.entering(CLASSNAME, METHODNAME);
        
        if (!connectionInitialized) {
            initializeConnection();
        }
        
        logger.info("Getting hostSystem");
        HostSystem hostSystem = getHostSystem(hostName);
        if (hostSystem == null) {
            logger.severe("Error retrieving hostSystem");
            logger.exiting(CLASSNAME, METHODNAME);
            return "";
        }
        
        logger.info("Getting dataFile");
        DataFile dataFile = getIdocDataFile(hostSystem, idocTypeName);
        if (dataFile == null) {
            logger.severe("Error retrieving dataFile");
            logger.exiting(CLASSNAME, METHODNAME);
            return "";
        }
        
        String nameQualifier = generateIDocTypeNameQualifier(idocTypeName, basicTypeName, release);
        logger.info("nameQualifier: " + nameQualifier);
        logger.info("Getting dataCollection");
        DataCollection dataCollection = getIdocTypeDC(dataFile, XMETA_IDOC_TYPE, idocTypeName, nameQualifier, false);
        if (dataCollection == null) {
            logger.severe("Error retrieving dataCollection");
            logger.exiting(CLASSNAME, METHODNAME);
            return "";
        }

        IDocTypeImpl idocType = new IDocTypeImpl();
        idocType.setIDocTypeName(idocTypeName);
        idocType.setBasicTypeName(basicTypeName);
        idocType.setRelease(release);
        
        logger.info("Getting segment DataFields");
        EList rootSegmentDataFields = dataCollection.getContains_DataField();

        logger.info("Root segments including CR: " + rootSegmentDataFields.size());
        for (Object o : rootSegmentDataFields) {
            findAndRetrieveSegment(idocType, (DataField)o, null, segmentType);
        }
        
        // Use the IMF handler for serialization to avoid code duplication
        IDocMetadataFileHandler handler = new IDocMetadataFileHandler();
        logger.info("Got handler");
        String result = handler.serializeMetadata(idocType);
        
        logger.exiting(CLASSNAME, METHODNAME);
        return result;
    }
    
    public String getCachedIDocTypes(String hostSystemName) throws MappingException, DSException, Exception {
        final String METHODNAME = "getCachedIDocTypes(String)";
        logger.entering(CLASSNAME, METHODNAME);
        
        String result = "";
        
        if (!connectionInitialized) {
            initializeConnection();
        }

        logger.info("Getting hostSystem");
        HostSystem hostSystem = getHostSystem(hostSystemName);
        if (hostSystem == null) {
            logger.exiting(CLASSNAME, METHODNAME);
            return "";
        }
        logger.info("Getting dataFiles");
        EList dataFileList = hostSystem.getHosts_DataStore();
        for (Object o : dataFileList) {
            DataFile dataFile = (DataFile) o;
            logger.info("Getting data collection list");
            EList dataCollectionList = ((DataFile) dataFile).getContains_DataCollection();
            for (Object o2 : dataCollectionList) {
                DataCollection idocTypeDC = (DataCollection) o2;
                logger.fine(idocTypeDC.getNameQualifier() + ", subtype: " + idocTypeDC.getSubtype());
                if (XMETA_IDOC_TYPE.equals(idocTypeDC.getSubtype())) {
                    result += idocTypeDC.getName()
                        + LIST_SEPARATOR
                        + getBasicTypeNameFromNameQualifier(idocTypeDC.getNameQualifier())
                        + LIST_SEPARATOR
                        + idocTypeDC.getShortDescription()
                        + LIST_LINE_SEPARATOR;
                }
            }
        }

        logger.info("Result: " + result);
        logger.exiting(CLASSNAME, METHODNAME);
        return result;
    }
    
    public String getIDocTypeRelease(String hostName, String idocTypeName, String basicTypeName) throws MappingException, DSException, Exception {
        final String METHODNAME = "getIDocTypeRelease(String, String, String)";
        logger.entering(CLASSNAME, METHODNAME);
        
        String result = "";
        
        if (!connectionInitialized) {
            initializeConnection();
        }

        logger.info("Getting hostSystem");
        HostSystem hostSystem = getHostSystem(hostName);
        if (hostSystem == null) {
            logger.exiting(CLASSNAME, METHODNAME);
            return "";
        }
        
        logger.info("Getting dataFile");
        DataFile dataFile = getIdocDataFile(hostSystem, idocTypeName);
        if (dataFile == null) {
            logger.severe("Error retrieving dataFile");
            logger.exiting(CLASSNAME, METHODNAME);
            return "";
        }
        
        // Create a qualifier without a release value
        String nameQualifier = generateIDocTypeNameQualifier(idocTypeName, basicTypeName, "");
        logger.info("nameQualifier: " + nameQualifier);
        logger.info("Getting dataCollection");
        DataCollection dataCollection = getIdocTypeDC(dataFile, XMETA_IDOC_TYPE, idocTypeName, nameQualifier, true);
        if (dataCollection == null) {
            logger.severe("Error retrieving dataCollection");
            logger.exiting(CLASSNAME, METHODNAME);
            return "";
        }

        result = getReleaseFromNameQualifier(dataCollection.getNameQualifier());
 
        logger.info("Result: " + result);
        logger.exiting(CLASSNAME, METHODNAME);
        return result;
    }
    
    public String getHostSystemRelease(String hostSystemName) throws MappingException, DSException, Exception {
        final String METHODNAME = "getHostSystemRelease(String)";
        logger.entering(CLASSNAME, METHODNAME);
        
        if (!connectionInitialized) {
            initializeConnection();
        }

        logger.info("Getting hostSystem");
        HostSystem hostSystem = getHostSystem(hostSystemName);
        if (hostSystem == null) {
            return "";
        }
        String desc = hostSystem.getShortDescription();
        logger.finer("attribute string: " + desc);
        String result = getSapReleaseFromAttributeString(desc);

        logger.info("Result: " + result);
        logger.exiting(CLASSNAME, METHODNAME);
        return result;
    }
    
    public int getHostSystemPcs(String hostSystemName) throws MappingException, DSException, Exception {
        final String METHODNAME = "getHostSystemRelease(String)";
        logger.entering(CLASSNAME, METHODNAME);
        
        if (!connectionInitialized) {
            initializeConnection();
        }

        logger.info("Getting hostSystem");
        HostSystem hostSystem = getHostSystem(hostSystemName);
        if (hostSystem == null) {
            return 0;
        }
        int result = getPcsFromAttributeString(hostSystem.getShortDescription());

        logger.info("Result: " + result);
        logger.exiting(CLASSNAME, METHODNAME);
        return result;
    }
    
   public boolean saveIDocMetadata(String hostName, String hostRelease, int pcs, String metadata) throws MappingException, DSException, Exception {
   	final String METHODNAME = "getCachedIDocTypes(String)";
		logger.entering(CLASSNAME, METHODNAME);

		boolean result = true;

		// Use the IMF handler for deserialization to avoid code duplication
		IDocMetadataFileHandler handler = new IDocMetadataFileHandler();
		logger.info("Got handler");
		IDocTypeImpl idocType = handler.parseMetadataFile(new StringBuffer(metadata));

		if (!connectionInitialized) {
			initializeConnection();
		}

		// Action
		try {
			serviceDelegator.beginTransaction();
			generateASCLModel(hostName, hostRelease, pcs, idocType);

			logger.info("Saving transaction...");
			serviceDelegator.saveObject();

			logger.info("Committing transaction...");
			serviceDelegator.commitTransaction();
		}
		catch (XMetaIDocHandlingException e) {
			serviceDelegator.abortTransaction();

			// logger.log(Level.SEVERE,
			// "IDoc type metadata creation failed. Datastage session is locked. Please close all DataStage client programs.");
			logger.log(Level.SEVERE, e.getMessage(), e);
			throw new RuntimeException(e);
		}

		logger.exiting(CLASSNAME, METHODNAME);
		return result;
   }
    
   public boolean saveMessageTypes(String hostName, String idocType, String basicType, String release, String messageTypes) throws MappingException, DSException, Exception {
		final String METHODNAME = "saveMessageTypes(String, String, String, String, String)";
		logger.entering(CLASSNAME, METHODNAME);

		boolean result = true;

		String[] messageTypesArray = messageTypes.split(LIST_LINE_SEPARATOR_REGEX);
		if (messageTypesArray.length == 0) {
			logger.severe("Empty input");
			logger.exiting(CLASSNAME, METHODNAME);
			return false;
		}

		// Connect
		if (!connectionInitialized) {
			initializeConnection();
		}

		logger.info("Getting hostSystem");
		HostSystem hostSystem = getHostSystem(hostName);
		if (hostSystem == null) {
			logger.severe("Error retrieving hostSystem");
			logger.exiting(CLASSNAME, METHODNAME);
			return false;
		}

		logger.info("Getting dataFile");
		DataFile dataFile = getIdocDataFile(hostSystem, idocType);
		if (dataFile == null) {
			logger.severe("Error retrieving dataFile");
			logger.exiting(CLASSNAME, METHODNAME);
			return false;
		}

		String nameQualifier = generateIDocTypeNameQualifier(idocType, basicType, release);
		logger.info("nameQualifier: " + nameQualifier);
		logger.info("Getting dataCollection");
		DataCollection dataCollection = getIdocTypeDC(dataFile, XMETA_MESSAGE_TYPES, idocType,
			nameQualifier, false);

		// Action
		try {
			serviceDelegator.beginTransaction();

			if (dataCollection != null) {
				logger.info("Deleting existing data");
				serviceDelegator.markObjectForDelete(dataCollection);
			}

			logger.info("Creating new data collection");
			dataCollection = createIdocTypeDC(dataFile, XMETA_MESSAGE_TYPES, idocType, "",
				nameQualifier);

			// Create DataFields for the message types
			for (String messageType : messageTypesArray) {
				String messageTypeName = messageType.split(LIST_SEPARATOR_REGEX)[0];
				logger.info(messageTypeName);
				String messageTypeDesc = "";
				if (messageType.split(LIST_SEPARATOR_REGEX).length > 1) {
					messageTypeDesc = messageType.split(LIST_SEPARATOR_REGEX)[1];
				}
				logger.info(messageTypeDesc);
				logger.info("Creating new message type Data Field");
				DataField segmentDF = serviceDelegator.getModelFactory().createDataField();
				serviceDelegator.markObjectForSave(segmentDF);
				segmentDF.setName(messageTypeName);
				segmentDF.setShortDescription(messageTypeDesc);
				segmentDF.setSubtype(XMETA_MESSAGE_TYPE);
				logger.info("Setting parent data collection");
				segmentDF.setOf_DataCollection(dataCollection);
			}

			logger.info("Saving transaction...");
			serviceDelegator.saveObject();
			logger.info("Transaction saved.");

			serviceDelegator.commitTransaction();
			logger.info("Transaction committed.");
		}
		catch(Exception e) {
			logger.log(Level.SEVERE, "Could not save the IDoc message type list. Datastage session is locked. Please close all DataStage client programs.");
			serviceDelegator.abortTransaction();
			throw new RuntimeException(e);
			// } catch (XMetaIDocHandlingException e) {
			// sandboxClient.abortTransaction();
			// throw new RuntimeException(e);
		}

		logger.exiting(CLASSNAME, METHODNAME);
		return result;   	
   }
    
   public String getMessageTypes(String hostName, String idocType, String basicType) throws MappingException, DSException, Exception {
        final String METHODNAME = "getMessageTypes(String, String, String)";
        logger.entering(CLASSNAME, METHODNAME);
        
        String result = "";
        
        if (!connectionInitialized) {
            initializeConnection();
        }

        logger.info("Getting hostSystem");
        HostSystem hostSystem = getHostSystem(hostName);
        if (hostSystem == null) {
            logger.exiting(CLASSNAME, METHODNAME);
            return "";
        }
        
        logger.info("Getting dataFile");
        DataFile dataFile = getIdocDataFile(hostSystem, idocType);
        if (dataFile == null) {
            logger.severe("Error retrieving dataFile");
            logger.exiting(CLASSNAME, METHODNAME);
            return "";
        }
        
        String nameQualifier = generateIDocTypeNameQualifier(idocType, basicType, "");
        logger.info("nameQualifier: " + nameQualifier);
        logger.info("Getting dataCollection");
        DataCollection dataCollection = getIdocTypeDC(dataFile, XMETA_MESSAGE_TYPES, idocType, nameQualifier, true);
        if (dataCollection == null) {
            logger.severe("Error retrieving dataCollection");
            logger.exiting(CLASSNAME, METHODNAME);
            return "";
        }
        
        logger.info("Getting dataFields");
        EList messageTypeDataFields = dataCollection.getContains_DataField();

        logger.info("Number of message types: " + messageTypeDataFields.size());
        for (Object o : messageTypeDataFields) {
            DataField df = (DataField) o;
            result += df.getName() + LIST_SEPARATOR + df.getShortDescription() + LIST_LINE_SEPARATOR;
        }

        logger.info("Result: " + result);
        logger.exiting(CLASSNAME, METHODNAME);
        return result;
   }    
    
    // For test mode only at this time
//    public void generateMetadata(IDocType idocType, String hostRelease, boolean unicodeFlag) throws MappingException, DSException, Exception {
//        final String METHODNAME = "generateMetadata(IDocType)";
//        logger.entering(CLASSNAME, METHODNAME);
//        
//        // Initialize repository connection
//        if (!testMode) {
//            // Real mode, use the pre-authenticated sandbox client from DS Designer
//            initializeConnection();
//        } else {
//            // Test mode, do an explicit login with the credentials provided
//            AuthenticationService authenticationService = new AuthenticationService();
//            logger.info("Logging on to DataStage server...");
//            authenticationService.doLogin(dsUserName, dsPassword.toCharArray(), dsHostName, dsPort, null);
//            logger.info("Logon done.");
//    
//            sandboxClient = CoreRepositoryClientFactory.createSandboxClient();
//            myFactory = (ASCLModelFactory)sandboxClient.getChangeTrackingFactory(ASCLModelFactory.eINSTANCE);
//        }
//
//        try {
//            sandboxClient.beginTransaction();
//    
//            generateASCLModel(sapConn.getSapSystem().getAppServer(), hostRelease, unicodeFlag, idocType);
//            
//            logger.info("Saving transaction...");
//            sandboxClient.save();
//            logger.info("Transaction saved.");
//            sandboxClient.commitTransaction();
//            logger.info("Transaction committed.");
//        } catch (SessionLockException e) {
//            logger.log(Level.SEVERE, "IDoc type metadata creation failed. Datastage session is locked. Please close all DataStage client programs.");
//            sandboxClient.abortTransaction();
//            throw new RuntimeException(e);
//        }
//        
//        logger.exiting(CLASSNAME, METHODNAME);
//    }
    
   public boolean deleteIDocMetadata(String hostName, String idocTypeName, String basicTypeName, String release) throws MappingException, DSException, Exception {
		final String METHODNAME = "deleteIDocMetadata(String, String, String, String)";
		logger.entering(CLASSNAME, METHODNAME);

		boolean result = true;

		// Connect
		if (!connectionInitialized) {
			initializeConnection();
		}

		logger.info("Getting hostSystem");
		HostSystem hostSystem = getHostSystem(hostName);
		if (hostSystem == null) {
			logger.severe("Error retrieving hostSystem");
			logger.exiting(CLASSNAME, METHODNAME);

			return false;
		}

		logger.info("Getting dataFile");
		DataFile dataFile = getIdocDataFile(hostSystem, idocTypeName);
		if (dataFile == null) {
			logger.severe("Error retrieving dataFile");
			logger.exiting(CLASSNAME, METHODNAME);

			return false;
		}

		String nameQualifier = generateIDocTypeNameQualifier(idocTypeName, basicTypeName, release);
		logger.info("nameQualifier: " + nameQualifier);

		logger.info("Getting dataCollections");
		DataCollection dataCollection = getIdocTypeDC(dataFile, XMETA_IDOC_TYPE, idocTypeName, nameQualifier, true);
		if (dataCollection == null) {
			logger.severe("Error retrieving dataCollection");
			logger.exiting(CLASSNAME, METHODNAME);

			return false;
		}
		DataCollection dataCollectionMsgTypes = getIdocTypeDC(dataFile, XMETA_MESSAGE_TYPES, idocTypeName, nameQualifier, true);
		if (dataCollectionMsgTypes == null) {
			logger.severe("Error retrieving dataCollection");
			logger.exiting(CLASSNAME, METHODNAME);

			return false;
		}

		// Action
		try {
			serviceDelegator.beginTransaction();

			logger.info("Marking dataFile for deletion...");
			serviceDelegator.markObjectForDelete(dataCollection);
			serviceDelegator.markObjectForDelete(dataCollectionMsgTypes);

			logger.info("Saving transaction...");
			serviceDelegator.saveObject();
			logger.info("Transaction saved.");
			
			serviceDelegator.commitTransaction();
			logger.info("Transaction committed.");
		}
		catch(Exception e) {
			logger.log(Level.SEVERE, "IDoc type metadata deletion failed. Datastage session is locked. Please close all DataStage client programs.");
			serviceDelegator.abortTransaction();
			throw new RuntimeException(e);
		}

		logger.exiting(CLASSNAME, METHODNAME);
		return result;
   }
    
   public boolean clearCache(String hostName) throws MappingException, DSException, Exception {
		final String METHODNAME = "clearCache(String)";
		logger.entering(CLASSNAME, METHODNAME);

		// Connect
		if (!connectionInitialized) {
			initializeConnection();
		}

		logger.info("Getting hostSystem");
		HostSystem hostSystem = getHostSystem(hostName);
		if (hostSystem == null) {
			// Nothing to erase
			logger.info("Host System '" + hostName + "' doesn't exist");
			logger.exiting(CLASSNAME, METHODNAME);

			return true;
		}

		logger.info("Getting dataFiles");
		EList dataFileList = hostSystem.getHosts_DataStore();
		int listSize = dataFileList.size();

		// Action
		try {
			serviceDelegator.beginTransaction();

			if (listSize > 0) {
				for (int i = 0; i < listSize; i++) {
					DataFile dataFile = (DataFile) dataFileList.get(0);
					logger.info("Marking dataFile for deletion: " + dataFile.getName());
					serviceDelegator.markObjectForDelete(dataFile);
				}
			}

			logger.info("Marking hostSystem for deletion...");
			serviceDelegator.markObjectForDelete(hostSystem);

			logger.info("Saving transaction...");
			serviceDelegator.saveObject();
			logger.info("Transaction saved.");

			serviceDelegator.commitTransaction();
			logger.info("Transaction committed.");
		}
		catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage());
			// TODO try to improve exception tracing
			int size = e.getStackTrace().length;
			for (int i = 0; i < size; i++) {
				logger.log(Level.SEVERE, e.getStackTrace()[i].getClassName() + "." + e.getStackTrace()[i].getMethodName());
			}

			try {
				// TODO: apparently there is no transaction to abort here.
				// BUT, after this happens (normally it won't), the next call to
				// getHostSystem()
				// produces a query result of size 1 with a null element inside. No
				// idea how this works.
				serviceDelegator.abortTransaction();
			}
			catch (Exception e2) {
				int size2 = e2.getStackTrace().length;
				for (int i = 0; i < size2; i++) {
					logger.log(Level.SEVERE, e2.getStackTrace()[i].getClassName() + "." + e2.getStackTrace()[i].getMethodName());
				}
			}
			throw new RuntimeException(e);
		}

		logger.exiting(CLASSNAME, METHODNAME);
		return true;
   }
    
    
   public boolean typeIsCached(String hostName, String idocTypeName, String basicTypeName, String release) throws MappingException, DSException, Exception {
		final String METHODNAME = "typeisCached(String, String, String, String)";
		logger.entering(CLASSNAME, METHODNAME);

		boolean result = true;

		// Connect
		if (!connectionInitialized) {
			initializeConnection();
		}

		logger.info("Getting hostSystem");
		HostSystem hostSystem = getHostSystem(hostName);
		if (hostSystem == null) {
			logger.severe("Error retrieving hostSystem");
			logger.exiting(CLASSNAME, METHODNAME);

			return false;
		}

		logger.info("Getting dataFile");
		DataFile dataFile = getIdocDataFile(hostSystem, idocTypeName);
		if (dataFile == null) {
			logger.severe("Error retrieving dataFile");
			logger.exiting(CLASSNAME, METHODNAME);

			return false;
		}

		String nameQualifier = generateIDocTypeNameQualifier(idocTypeName, basicTypeName, release);
		logger.info("nameQualifier: " + nameQualifier);
		logger.info("Getting dataCollection");
		DataCollection dataCollection = getIdocTypeDC(dataFile, XMETA_IDOC_TYPE, idocTypeName, nameQualifier, false);
		result = (dataCollection != null);

		logger.exiting(CLASSNAME, METHODNAME);
		return result;
   }

   private void retrieveSegment(IDocTypeImpl idocType, DataField dataField, IDocSegment parentSegment) throws XMetaIDocHandlingException {
		final String METHODNAME = "retrieveSegment(IDocType, DataField, IDocSegmentImpl)";
		logger.entering(CLASSNAME, METHODNAME);

		if (dataField.getSubtype().equals(XMETA_SEGMENT_TYPE)) {
			// This is a segment
			IDocSegmentImpl segment = new IDocSegmentImpl(idocType);
			// Retrieve DataItemDef for the segment definition
			DataItemDef dataItemDef = dataField.getBasedOn_DataItemDef();

			if (dataItemDef.getName().equals(CONTROL_RECORD)) {
				// We found the CR
				retrieveCR(idocType, dataField);
				logger.exiting(CLASSNAME, METHODNAME);

				return;
			}

			// Assign parent
			if (parentSegment != null) {
				parentSegment.getChildSegments().add(segment);
				segment.setParent(parentSegment);
			}
			else {
				idocType.getRootSegments().add(segment);
			}

			logger.info("Processing main properties");
			// Set main properties
			segment.setSegmentTypeName(dataField.getName());
			logger.info("Processing segment: " + segment.getSegmentTypeName());
			segment.setSegmentDescription(dataField.getShortDescription());
			segment.setMandatory(dataField.getAllowsEmptyValue());

			logger.info("Processing DataItemDef for the segment definition");
			segment.setSegmentDefinitionName(dataItemDef.getName());

			logger.info("Processing dimension");
			// We only ever have one dimension attached, so use the first one
			Dimension dimension = (Dimension) dataField.getHas_Dimension().get(0);
			segment.setMinOccurrence(dimension.getMinimumSize());
			segment.setMaxOccurrence(dimension.getMaximumSize());

			logger.info("Processing child segments");
			EList childSegmentDataFields = dataItemDef.getContains_DataField();
			for (Object o : childSegmentDataFields) {
				retrieveSegment(idocType, (DataField) o, segment);
			}
		}
		else
			if (dataField.getSubtype().equals(XMETA_COLUMN_TYPE)) {
				// This is a field/column
				IDocFieldImpl field = new IDocFieldImpl(parentSegment);
				parentSegment.getFields().add(field);
				field.setFieldName(dataField.getName());
				logger.info("Processing field: " + field.getFieldName());
				field.setFieldDescription(dataField.getShortDescription());
				field.setLength(dataField.getMaximumLength());

				TypeCodeEnum type = dataField.getTypeCode();
				if (type != null && type != TypeCodeEnum.get(TypeCodeEnum.UNKNOWN)) {
					if (type == TypeCodeEnum.get(TypeCodeEnum.STRING)) {
						field.setSAPType(SAPTYPE_CHAR);
					}
					else
						if (type == TypeCodeEnum.get(TypeCodeEnum.DATE)) {
							field.setSAPType(SAPTYPE_DATE);
						}
						else
							if (type == TypeCodeEnum.get(TypeCodeEnum.TIME)) {
								field.setSAPType(SAPTYPE_TIME);
							}
							else {
								String message = "Unsupported data type (type code) on dataField "
									+ dataField.getName() + ": " + type.toString();
								logger.severe(message);
								throw new XMetaIDocHandlingException(message);
							}
				} 
				else {
					String message = "Missing data type (type code) on dataField " + dataField.getName()
						+ ": " + type.toString();
					logger.severe(message);
					throw new XMetaIDocHandlingException(message);
				}
			} 
			else {
				String message = "Incorrect subtype on dataField " + dataField.getName() + ": "
					+ dataField.getSubtype();
				logger.severe(message);
				throw new XMetaIDocHandlingException(message);
			}

		logger.exiting(CLASSNAME, METHODNAME);
		return;
   }
    
    /**
     * This method recursively traverses the DataField/DataItemDef tree and finds the segment
     * with the type targetSegmentType. It then traverses the subtree to retrieve the segment fields.
     * The resulting segment is attached to the idocType as a root segment.
     * @param idocType
     * @param dataField
     * @param parentSegment
     * @param targetSegmentType
     */
    private void findAndRetrieveSegment(IDocTypeImpl idocType, DataField dataField, IDocSegment parentSegment, String targetSegmentType) {
        final String METHODNAME = "findAndRetrieveSegment(IDocType, DataField, IDocSegmentImpl)";
        logger.entering(CLASSNAME, METHODNAME);
        
        if (dataField.getSubtype().equals(XMETA_SEGMENT_TYPE)) {
            String segmentType = dataField.getName();
            if (segmentType.equals(targetSegmentType)) {
                // This is the segment we're looking for
                IDocSegmentImpl segment = new IDocSegmentImpl(idocType);
                // Retrieve DataItemDef for the segment definition
                DataItemDef dataItemDef = dataField.getBasedOn_DataItemDef();
                
                // Make this a root segment since we're only retrieving one
                idocType.getRootSegments().add(segment);
    
                logger.info("Processing main properties");
                // Set main properties
                segment.setSegmentTypeName(dataField.getName());
                logger.info("Processing segment: " + segment.getSegmentTypeName());
                segment.setSegmentDescription(dataField.getShortDescription());
        
                logger.info("Processing DataItemDef for the segment definition");
                segment.setSegmentDefinitionName(dataItemDef.getName());
        
                logger.info("Processing child segments");
                EList childSegmentDataFields = dataItemDef.getContains_DataField();
                for (Object o : childSegmentDataFields) {
                    findAndRetrieveSegment(idocType, (DataField)o, segment, targetSegmentType);
                }
            } else {
                // This is not the segment we're looking for
                // Retrieve DataItemDef for the segment definition
                DataItemDef dataItemDef = dataField.getBasedOn_DataItemDef();
                logger.info("Searching child segments");
                EList childSegmentDataFields = dataItemDef.getContains_DataField();
                for (Object o : childSegmentDataFields) {
                    findAndRetrieveSegment(idocType, (DataField)o, null, targetSegmentType);
                }
            }
        } else if (dataField.getSubtype().equals(XMETA_COLUMN_TYPE)) {
            // This is a field
            if (parentSegment != null) { // the field belongs to the target segment, add it
                IDocFieldImpl field = new IDocFieldImpl(parentSegment);
                parentSegment.getFields().add(field);
                field.setFieldName(dataField.getName());
                logger.info("Processing field: " + field.getFieldName());
                field.setFieldDescription(dataField.getShortDescription());
                field.setLength(dataField.getMaximumLength());
            }
        } else {
            logger.severe("Incorrect subtype on dataField: " + dataField.getSubtype());
        }
        
        logger.exiting(CLASSNAME, METHODNAME);
        return;
    }
    
    private void retrieveCR(IDocTypeImpl idocType, DataField dataField) throws XMetaIDocHandlingException {
        final String METHODNAME = "retrieveCR(IDocType, DataField)";
        logger.entering(CLASSNAME, METHODNAME);

        ControlRecord cr = new ControlRecord(idocType);
        idocType.setControlRecord(cr);
        // Retrieve DataItemDef for the segment definition
        DataItemDef dataItemDef = dataField.getBasedOn_DataItemDef();

        logger.info("Processing child segments");
        EList childSegmentDataFields = dataItemDef.getContains_DataField();
        for (Object o : childSegmentDataFields) {
            retrieveSegment(idocType, (DataField)o, cr);
        }
        
        logger.exiting(CLASSNAME, METHODNAME);
        return;
    }
    
    
    private void generateASCLModel(String hostName, String hostRelease, int pcs, IDocType idocType) throws Exception, XMetaIDocHandlingException {
        final String METHODNAME = "generateASCLModel(IDocType)";
        logger.entering(CLASSNAME, METHODNAME);
        
        // Host System
        HostSystem hostSystem = getHostSystem(hostName);
        if (hostSystem == null) {
            logger.info("Creating new host system");
            hostSystem = createHostSystem(hostName, hostRelease, pcs);
        }
        
        // IDoc DataFile "dummy object"
        DataFile idocDataFile = getIdocDataFile(hostSystem, idocType.getIDocTypeName());
        if (idocDataFile == null) {
            logger.info("Creating new data file");
            idocDataFile = createIdocDataFile(hostSystem, idocType.getIDocTypeName());
        }
        
        String nameQualifier = generateIDocTypeNameQualifier(idocType.getIDocTypeName(), idocType.getBasicTypeName(), idocType.getRelease());
        
        // IDoc type (DataCollection)
        DataCollection idocTypeDC = getIdocTypeDC(idocDataFile, XMETA_IDOC_TYPE, idocType.getIDocTypeName(), nameQualifier, false);
        if (idocTypeDC == null) {
            logger.info("Creating new data collection");
            idocTypeDC = createIdocTypeDC(idocDataFile, XMETA_IDOC_TYPE, idocType.getIDocTypeName(), idocType.getIDocTypeDescription(), nameQualifier);
        }

        // Create control record
        createControlRecordDF(idocTypeDC, null, idocType.getControlRecord());
        
        for (IDocSegment segment : idocType.getRootSegments()) {
            createSegmentDF(idocTypeDC, null, segment); // This recursively creates all segments
        }
       
        logger.exiting(CLASSNAME, METHODNAME);
   }
    
   private HostSystem getHostSystem(String name) throws Exception {
		final String METHODNAME = "getHostSystem(String)";
		logger.entering(CLASSNAME, METHODNAME);

		HostSystem hs = serviceDelegator.getHostSystem(name);

		logger.exiting(CLASSNAME, METHODNAME);
		return hs;
   }
    
   private HostSystem createHostSystem(String name, String hostRelease, int pcs) throws Exception {
		final String METHODNAME = "createHostSystem(String)";
		logger.entering(CLASSNAME, METHODNAME);

		HostSystem hostSystem = serviceDelegator.getModelFactory().createHostSystem();
		serviceDelegator.markObjectForSave(hostSystem);
		hostSystem.setName(name);
		hostSystem.setSubtype(XMETA_HOSTSYSTEM_TYPE);
		hostSystem.setShortDescription(generateHostSystemAttributeString(hostRelease, pcs));

		logger.exiting(CLASSNAME, METHODNAME);
		return hostSystem;
   }
    
   private DataFile getIdocDataFile(HostSystem hostSystem, String idocTypeName) {
		final String METHODNAME = "getIdocDataFile(HostSystem, String)";
		logger.entering(CLASSNAME, METHODNAME);

		DataFile result = null;
		EList resultList = hostSystem.getHosts_DataStore();
		if (resultList.size() > 0) {
			for (int i = 0; i < resultList.size(); i++) {
				DataFile candidate = (DataFile) resultList.get(i);

				if (candidate.getName().equals(idocTypeName)) {
					result = candidate;
					logger.info("Found existing DataFile");

					break;
				}
				else {
					logger.finer("Non-matching DataFile: " + candidate.getName());
				}
			}
		}
		if (result == null) {
			logger.info("IDoc Data File doesn't exist yet");
		}

		logger.exiting(CLASSNAME, METHODNAME);
		return result;
   }
    
   private DataFile createIdocDataFile(HostSystem hostSystem, String idocTypeName) throws Exception {
		final String METHODNAME = "createIdocDataFile(HostSystem, String)";
		logger.entering(CLASSNAME, METHODNAME);

		DataFile dataFile = serviceDelegator.getModelFactory().createDataFile();
		serviceDelegator.markObjectForSave(dataFile);
		dataFile.setName(idocTypeName);
		dataFile.setSubtype(XMETA_IDOC_TYPE);
		dataFile.setHostedBy_HostSystem(hostSystem);
		// TODO: add DataConnections?

		logger.exiting(CLASSNAME, METHODNAME);
		return dataFile;
   }
    
   // ignoreRelease is used when deleting the type and when actually retrieving the release
   private DataCollection getIdocTypeDC(DataFile parentDataFile, String subtype, String name, String nameQualifier, boolean ignoreRelease) {
		final String METHODNAME = "getIdocTypeDC(DataFile, String, String, String, boolean)";
		logger.entering(CLASSNAME, METHODNAME);

		DataCollection result = null;
		EList resultList = parentDataFile.getContains_DataCollection();
		if (resultList.size() > 0) {
			for (int i = 0; i < resultList.size(); i++) {
				DataCollection candidate = (DataCollection) resultList.get(i);
				String candidateQualifier = candidate.getNameQualifier();

				if (candidate.getName().equals(name) && subtype.equals(candidate.getSubtype()) && 
					 (candidateQualifier.equals(nameQualifier) || 
					  (ignoreRelease && getBasicTypeNameFromNameQualifier(candidateQualifier).equals(getBasicTypeNameFromNameQualifier(nameQualifier))))) {
					result = candidate;
					logger.info("Found existing DataCollection: " + nameQualifier);

					break;
				}
				else {
					logger.finer("Non-matching DataCollection: " + candidate.getName() + ", " + candidate.getNameQualifier());
				}
			}
		}
		if (result == null) {
			logger.info("DataCollection doesn't exist yet");
		}

		logger.exiting(CLASSNAME, METHODNAME);
		return result;
   }
    
   private DataCollection createIdocTypeDC(DataFile parentDataFile, String subtype, String name, String description, String nameQualifier) throws Exception {
		final String METHODNAME = "createIdocTypeDC(DataFile, String, String)";
		logger.entering(CLASSNAME, METHODNAME);

		DataCollection dataCollection = serviceDelegator.getModelFactory().createDataCollection();
		serviceDelegator.markObjectForSave(dataCollection);
		dataCollection.setName(name);
		dataCollection.setSubtype(subtype);
		dataCollection.setNameQualifier(nameQualifier);
		dataCollection.setShortDescription(description);
		dataCollection.setOf_DataFile(parentDataFile);

		logger.exiting(CLASSNAME, METHODNAME);
		return dataCollection;
   }
    
    private DataField getSegmentDF(DataCollection parentDC, DataItemDef parentSegment, String segmentType) {
        final String METHODNAME = "getSegmentDF(DataCollection, DataItemDef, String)";
        logger.entering(CLASSNAME, METHODNAME);
        
        EList result;
        if (parentDC != null) { // Root segment
            result = parentDC.getContains_DataField();
        } else {
            result = parentSegment.getContains_DataField();
        }
        for (Object df : result) {
            if (((DataField)df).getName().equals(segmentType)) {
                logger.exiting(CLASSNAME, METHODNAME);
                return (DataField) df;
            }
        }
        
        logger.exiting(CLASSNAME, METHODNAME);
        return null;
    }
    
   private void createControlRecordDF(DataCollection idocTypeDC, DataItemDef parentSegment, ControlRecord cr) throws XMetaIDocHandlingException, Exception {
		final String METHODNAME = "createControlRecordDF(DataCollection, DataItemDef, IDocSegment)";
		logger.entering(CLASSNAME, METHODNAME);

		DataField crDF = null;
		DataItemDef segmentTypeDefDID = null;

		if (crDF == null) {
			logger.info("Creating new control record Data Field");
			// Create DataField for the CR
			crDF = serviceDelegator.getModelFactory().createDataField();
			serviceDelegator.markObjectForSave(crDF);
			crDF.setName(CONTROL_RECORD);
			crDF.setSubtype(XMETA_SEGMENT_TYPE);

			// Assign parent item
			logger.info("Setting parent data collection");
			crDF.setOf_DataCollection(idocTypeDC);
		}
		else {
			logger.info("CR Data Field exists, skipping"); // TODO don't allow
																			// updates
		}

		// Retrieve or create DataItemDef for the segment definition
		segmentTypeDefDID = crDF.getBasedOn_DataItemDef();
		if (segmentTypeDefDID == null) // this is a new segment type
		{
			logger.info("Creating new CR segment definition Data Item Definition");
			segmentTypeDefDID = serviceDelegator.getModelFactory().createDataItemDef();
			serviceDelegator.markObjectForSave(segmentTypeDefDID);
			segmentTypeDefDID.setName(CONTROL_RECORD);
			segmentTypeDefDID.setSubtype(XMETA_IDOC_TYPE);
			segmentTypeDefDID.setIsTypeDef(true); // This is a type definition
		}
		else {
			logger.info("Segment definition Data Item Definition exists, skipping");
		}

		crDF.setBasedOn_DataItemDef(segmentTypeDefDID);

		// Assign fields
		createSegmentDataFields(cr, segmentTypeDefDID);

		logger.exiting(CLASSNAME, METHODNAME);
   }
    
    private void createSegmentDF(DataCollection idocTypeDC, DataItemDef parentSegment, IDocSegment segment) throws XMetaIDocHandlingException, Exception {
        final String METHODNAME = "createSegmentDF(DataCollection, DataItemDef, IDocSegment)";
        logger.entering(CLASSNAME, METHODNAME);
        
        logger.info("Seg type: " + segment.getSegmentTypeName() + ", Definition: " + segment.getSegmentDefinitionName());
        DataField segmentDF = null;
        if (parentSegment != null) {
            logger.info("Parent: " + parentSegment.getName());
            segmentDF = getSegmentDF(null, parentSegment, segment.getSegmentTypeName());
        } else {
            segmentDF = getSegmentDF(idocTypeDC, null, segment.getSegmentTypeName());
        }

        DataItemDef segmentTypeDefDID = null;
        
        if (segmentDF == null) {
            logger.info("Creating new segment Data Field");
            // Create DataField for the segment
            segmentDF = serviceDelegator.getModelFactory().createDataField();
            serviceDelegator.markObjectForSave(segmentDF);
            segmentDF.setName(segment.getSegmentTypeName());
            segmentDF.setSubtype(XMETA_SEGMENT_TYPE);
            segmentDF.setShortDescription(segment.getSegmentDescription()); // necessary?
            segmentDF.setAllowsEmptyValue(segment.isMandatory()); // TODO add to doc

            // Create dimension (number of occurrences)
            Dimension dimension = serviceDelegator.getModelFactory().createDimension();
            serviceDelegator.markObjectForSave(dimension);
            dimension.setSubtype(XMETA_IDOC_TYPE);
            Integer maxOccurrence = 1;
            if (segment.getMaxOccurrence() > Integer.MAX_VALUE)
                maxOccurrence = Integer.MAX_VALUE;
            else
                maxOccurrence = (int) segment.getMaxOccurrence();
            dimension.setMaximumSize(maxOccurrence); // cast is ok, there is no such occurrence
            dimension.setMinimumSize((int) segment.getMinOccurrence());
            dimension.setOf_DataItemBase(segmentDF); // assign dimension to segment

            // Assign parent item
            if (parentSegment == null) { // Root segment
                logger.info("Setting parent data collection");
                segmentDF.setOf_DataCollection(idocTypeDC);
            } else {
                logger.info("Setting parent data item def");
                segmentDF.setOf_DataItemDef(parentSegment);
            }
        } else {
            logger.info("Segment Data Field exists, skipping");
        }
        
        // Retrieve or create DataItemDef for the segment definition
        segmentTypeDefDID = segmentDF.getBasedOn_DataItemDef();
        if (segmentTypeDefDID == null) // this is a new segment type
        {
            logger.info("Creating new segment definition Data Item Definition");
            segmentTypeDefDID = serviceDelegator.getModelFactory().createDataItemDef();
            serviceDelegator.markObjectForSave(segmentTypeDefDID);
            segmentTypeDefDID.setName(segment.getSegmentDefinitionName());
            segmentTypeDefDID.setSubtype(XMETA_IDOC_TYPE);
            segmentTypeDefDID.setShortDescription(segment.getSegmentDescription());
            segmentTypeDefDID.setIsTypeDef(true); // Its segment typedef
        } else {
            logger.info("Segment definition Data Item Definition exists, skipping");
        }
        
        segmentDF.setBasedOn_DataItemDef(segmentTypeDefDID);
        
        // Assign fields
        createSegmentDataFields(segment, segmentTypeDefDID);
    
        // Process child segments
        for (IDocSegment child : segment.getChildSegments()) {
            createSegmentDF(null, segmentTypeDefDID, child);
        }
        
        logger.exiting(CLASSNAME, METHODNAME);
    }

    private void createSegmentDataFields(IDocSegment segment, DataItemDef segmentDID) throws XMetaIDocHandlingException, Exception {
        final String METHODNAME = "assignSegmentDataFields(IDocSegment, DataItemDef)";
        logger.entering(CLASSNAME, METHODNAME);
        
        for (IDocField field : segment.getFields()) {
            logger.fine("Assigning field: " + field.getFieldName());
            DataField df = serviceDelegator.getModelFactory().createDataField();
            serviceDelegator.markObjectForSave(df);
            
            df.setOf_DataItemDef(segmentDID);
            
            df.setSubtype(XMETA_COLUMN_TYPE);
            df.setName(field.getFieldName());
            df.setShortDescription(field.getFieldDescription());
            df.setMaximumLength(field.getLength());

            // Assign type, IDOCs use only three types
            String fieldType = field.getSAPType();
            if (fieldType != null && !"".equals(fieldType)) {
                if (fieldType.equals(SAPTYPE_CHAR)) {
                    df.setTypeCode(TypeCodeEnum.get(TypeCodeEnum.STRING));
                    df.setODBCType(ODBCTypeEnum.get(ODBCTypeEnum.VARCHAR));
                } else if (fieldType.equals(SAPTYPE_DATE)) {
                    df.setTypeCode(TypeCodeEnum.get(TypeCodeEnum.DATE));
                    df.setODBCType(ODBCTypeEnum.get(ODBCTypeEnum.DATE));
                } else if (fieldType.equals(SAPTYPE_TIME)) {
                    df.setTypeCode(TypeCodeEnum.get(TypeCodeEnum.TIME));
                    df.setODBCType(ODBCTypeEnum.get(ODBCTypeEnum.TIME));
                } else {
                    df.setTypeCode(TypeCodeEnum.get(TypeCodeEnum.STRING));
                    df.setODBCType(ODBCTypeEnum.get(ODBCTypeEnum.VARCHAR));
                }
            } else {
                String message = "No type set on field: " + field.getFieldName(); 
                logger.severe(message);
                throw new XMetaIDocHandlingException(message);
            }
        }
        
        logger.exiting(CLASSNAME, METHODNAME);
    }
}
