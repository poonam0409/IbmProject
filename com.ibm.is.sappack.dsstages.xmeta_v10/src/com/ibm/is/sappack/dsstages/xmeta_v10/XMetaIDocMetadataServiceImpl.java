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
// Module Name : com.ibm.is.sappack.dsstages.xmeta_v10
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.dsstages.xmeta_v10;


import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.eclipse.emf.ecore.EObject;

import ASCLModel.ASCLModelFactory;
import ASCLModel.ASCLModelPackage;
import ASCLModel.HostSystem;

import com.ibm.iis.xmeta.client.ISandboxClient;
import com.ibm.iis.xmeta.client.RemoteClientFactory;
import com.ibm.iis.xmeta.client.exception.ServiceException;
import com.ibm.iis.xmeta.client.exception.usage.InvalidParameterException;
import com.ibm.iis.xmeta.client.option.MarkForSaveOptions;
import com.ibm.iis.xmeta.client.option.ObjectQueryOptions;
import com.ibm.iis.xmeta.client.option.SaveOptions;
import com.ibm.is.sappack.dsstages.xmeta.common.XMetaIDocHandlingException;
import com.ibm.is.sappack.dsstages.xmeta.common.XMetaIDocMetadataService;


/**
 * Handles the caching/saving of IDoc metadata in the Metadata Repository (XMeta).
 * IDoc elements are represented using standard Common Model (ASCLModel) objects.
 */
public class XMetaIDocMetadataServiceImpl implements XMetaIDocMetadataService {
    
    static final String CLASSNAME = XMetaIDocMetadataServiceImpl.class.getName();

    private Logger           logger;
    private ISandboxClient   sandboxClient = null;
    private ASCLModelFactory modelFactory;
    

   static String copyright() {
       return com.ibm.is.sappack.dsstages.xmeta_v10.Copyright.IBM_COPYRIGHT_SHORT;
   }

	public void abortTransaction() throws XMetaIDocHandlingException {
		; // nothing to do
	}

	public void beginTransaction() throws XMetaIDocHandlingException {
		; // nothing to do
	}

	public void commitTransaction() throws XMetaIDocHandlingException {
		; // nothing to do
	}

   public void initializeConnection(Logger logger) throws XMetaIDocHandlingException {
   	if (logger == null) {
   		throw new IllegalArgumentException("Logger was not defined");
   	}
   	this.logger = logger;

   	final String METHODNAME = "initializeConnection()";
   	logger.entering(CLASSNAME, METHODNAME);

   	try {
   		// ==> create the SandboxClient ...
   		if (sandboxClient == null) {
   			sandboxClient = RemoteClientFactory.createDefaultClient();
   			if (sandboxClient == null) {
   				throw new XMetaIDocHandlingException("No sandbox client! panic!");
   			}
   		}

   		// ... and the model factory
   		modelFactory = (ASCLModelFactory)sandboxClient.getChangeTrackingFactory(ASCLModelFactory.eINSTANCE);
   		if (modelFactory == null) {
   			throw new XMetaIDocHandlingException("No factory! panic!");
   		}
   	}
   	catch(ServiceException srvcExcpt) {
			throw new XMetaIDocHandlingException(srvcExcpt);
   	}
   	catch(InvalidParameterException invalidParamExcpt) {
			throw new XMetaIDocHandlingException(invalidParamExcpt);
   	}
//		ASCLModelFactory mdl1     = ASCLModelFactory.eINSTANCE;
//		ASCLModelPackage axmetaPk = ASCLModelPackage.eINSTANCE;

		logger.exiting(CLASSNAME, METHODNAME);
   }

   public void releaseConnection() throws XMetaIDocHandlingException {
   	final String METHODNAME = "releaseConnection()";
   	logger.entering(CLASSNAME, METHODNAME);
   	
   	if (sandboxClient != null) {
			try {
				sandboxClient.release();
			}
			catch(ServiceException srvcExcpt) {
				throw new XMetaIDocHandlingException(srvcExcpt);
			}
   		sandboxClient = null;
   	}
		logger.exiting(CLASSNAME, METHODNAME);
   }


   public HostSystem getHostSystem(String name) throws XMetaIDocHandlingException {
   	final String METHODNAME = "getHostSystem(String)";
   	logger.entering(CLASSNAME, METHODNAME);
        
		HostSystem hs = null;
		// ObjectQueryOptions options = new ObjectQueryOptions();
		// options.setProxyStrategy(ProxyStrategies.ROOT_ONLY);

		try {
			String query = "select x from x in HostSystem where x.name='" + name + "'" + " and x.subtype='" + XMETA_HOSTSYSTEM_TYPE + "'";
			List<EObject> res = sandboxClient.executeObjectQuery(query, ASCLModelPackage.eINSTANCE.getNsURI(), ObjectQueryOptions.ROOT_ONLY, null);

			if (res.size() > 0) {
				logger.info("Host System exists");
				hs = (HostSystem) res.get(0);
			}
			else {
				logger.info("Host System '" + name + "' doesn't exist");
			}
		}
		catch(Exception excpt) {
			throw new XMetaIDocHandlingException(excpt);
		}
		
		logger.exiting(CLASSNAME, METHODNAME);
		return hs;
   }


   public ASCLModelFactory getModelFactory() {
   	return(modelFactory);
   }
   

   public void markObjectForDelete(EObject eObjToBeSaved) throws XMetaIDocHandlingException  {
   	final String METHODNAME = "markObjectForDelete(EObject)";
   	logger.entering(CLASSNAME, METHODNAME);

   	Vector<EObject> tmpVector = new Vector<EObject>();
   	tmpVector.add(eObjToBeSaved);
   	try {
   		sandboxClient.markForDelete(tmpVector);
   	}
		catch(Exception excpt) {
			throw new XMetaIDocHandlingException(excpt);
		}

      logger.exiting(CLASSNAME, METHODNAME);
   }


   public void markObjectForSave(EObject eObjToBeSaved) throws XMetaIDocHandlingException {
   	MarkForSaveOptions saveOpts;

   	final String METHODNAME = "markObjectForSave(EObject)";
   	logger.entering(CLASSNAME, METHODNAME);

   	saveOpts = new MarkForSaveOptions();
   	saveOpts.setAllObjectsAreNew(true);
   	Vector<EObject> tmpVector = new Vector<EObject>();
   	tmpVector.add(eObjToBeSaved);

   	try {
      	sandboxClient.markForSave(tmpVector, saveOpts);
   	}
		catch(Exception excpt) {
			throw new XMetaIDocHandlingException(excpt);
		}

      logger.exiting(CLASSNAME, METHODNAME);
   }


   public void saveObject() throws XMetaIDocHandlingException {
   	SaveOptions saveOpts;

   	final String METHODNAME = "saveObject(EObject)";
      logger.entering(CLASSNAME, METHODNAME);

   	saveOpts = new SaveOptions();
   	saveOpts.setFlushCacheAfterSave(true);
   	try {
      	sandboxClient.save(saveOpts);
   	}
		catch(Exception excpt) {
			throw new XMetaIDocHandlingException(excpt);
		}

      logger.exiting(CLASSNAME, METHODNAME);
   }

} // end of class XMetaIDocMetadataServiceImpl 
