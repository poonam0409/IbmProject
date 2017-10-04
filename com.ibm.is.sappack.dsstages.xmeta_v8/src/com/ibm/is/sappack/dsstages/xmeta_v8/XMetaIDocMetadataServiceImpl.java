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
// Module Name : com.ibm.is.sappack.dsstages.xmeta_v8
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.dsstages.xmeta_v8;


import java.util.Vector;
import java.util.logging.Logger;

import org.eclipse.emf.ecore.EObject;

import ASCLModel.ASCLModelFactory;
import ASCLModel.ASCLModelPackage;
import ASCLModel.HostSystem;

import com.ascential.dstage.proxies.NewReposFactory;
import com.ascential.dstage.proxies.NewReposInt;
import com.ascential.xmeta.client.repository.core.ISandboxClient;
import com.ascential.xmeta.client.repository.query.IQueryResult;
import com.ascential.xmeta.client.repository.query.QueryOptions;
import com.ascential.xmeta.exception.ServiceException;
import com.ascential.xmeta.exception.usage.InvalidStateException;
import com.ascential.xmeta.proxy.ProxyStrategies;
import com.ibm.is.sappack.dsstages.xmeta.common.XMetaIDocHandlingException;
import com.ibm.is.sappack.dsstages.xmeta.common.XMetaIDocMetadataService;


/**
 * Handles the caching/saving of IDoc metadata in the Metadata Repository (XMeta).
 * IDoc elements are represented using standard Common Model (ASCLModel) objects.
 */
public class XMetaIDocMetadataServiceImpl implements XMetaIDocMetadataService {

	static final String CLASSNAME = XMetaIDocMetadataServiceImpl.class.getName();

	private Logger           logger;
	private ISandboxClient   sandboxClient;
   private ASCLModelFactory modelFactory;

	static String copyright() {
		return com.ibm.is.sappack.dsstages.xmeta_v8.Copyright.IBM_COPYRIGHT_SHORT;
	}

	public void abortTransaction() throws XMetaIDocHandlingException {

		try {
			sandboxClient.abortTransaction();
		}
		catch(Exception excpt) {
			throw new XMetaIDocHandlingException(excpt);
		}
	}

	public void beginTransaction() throws XMetaIDocHandlingException {

		try {
			sandboxClient.beginTransaction();
		}
		catch(Exception excpt) {
			throw new XMetaIDocHandlingException(excpt);
		}
	}

	public void commitTransaction() throws XMetaIDocHandlingException {

		try {
			sandboxClient.commitTransaction();
		}
		catch(Exception excpt) {
			throw new XMetaIDocHandlingException(excpt);
		}
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
			NewReposFactory factory = new NewReposFactory();
			NewReposInt repos = factory.create("");
			sandboxClient = (ISandboxClient) repos.getSandboxClient();
			if (sandboxClient == null) {
				throw new XMetaIDocHandlingException("No sandbox client! panic!");
			}

			// ... and the model factory
			modelFactory = (ASCLModelFactory) sandboxClient.getChangeTrackingFactory(ASCLModelFactory.eINSTANCE);
			if (modelFactory == null) {
				throw new XMetaIDocHandlingException("No factory! panic!");
			}
		}
   	catch(Exception excpt) {
			throw new XMetaIDocHandlingException(excpt);
   	}

		logger.exiting(CLASSNAME, METHODNAME);
	}

	public void releaseConnection() throws XMetaIDocHandlingException {
		final String METHODNAME = "releaseConnection()";
		logger.entering(CLASSNAME, METHODNAME);

		if (sandboxClient != null) {
			try {
				if (sandboxClient.isInTransaction()) {
					sandboxClient.commitTransaction();
				}
				sandboxClient.release();
			}
			catch(ServiceException srvcExcpt) {
				throw new XMetaIDocHandlingException(srvcExcpt);
			}
			catch(InvalidStateException invStateExcpt) {
				throw new XMetaIDocHandlingException(invStateExcpt);
			}
			sandboxClient = null;
		}

		logger.exiting(CLASSNAME, METHODNAME);
	}

	public HostSystem getHostSystem(String name) throws XMetaIDocHandlingException {
		final String METHODNAME = "getHostSystem(String)";
		logger.entering(CLASSNAME, METHODNAME);

		HostSystem hs = null;
		QueryOptions options = new QueryOptions();
		options.setProxyStrategy(ProxyStrategies.ROOT_ONLY);

		try {
			String query = "select x from x in HostSystem where x.name='" + name + "'" + " and x.subtype='" + XMETA_HOSTSYSTEM_TYPE + "'";
			IQueryResult res = sandboxClient.query(query, ASCLModelPackage.eINSTANCE.getNsURI(), options);

			if (res.hasNext()) {
				logger.info("Host System exists");
				hs = (HostSystem) res.next();
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
   

	public void markObjectForDelete(EObject eObjToBeSaved) throws XMetaIDocHandlingException {
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
		final String METHODNAME = "markObjectForSave(EObject)";
		logger.entering(CLASSNAME, METHODNAME);

		try {
			sandboxClient.markForSave(eObjToBeSaved);
		}
		catch(Exception excpt) {
			throw new XMetaIDocHandlingException(excpt);
		}

		logger.exiting(CLASSNAME, METHODNAME);
	}

	public void saveObject() throws XMetaIDocHandlingException {
		final String METHODNAME = "saveObject(EObject)";
		logger.entering(CLASSNAME, METHODNAME);

		try {
			sandboxClient.save();
		}
		catch(Exception excpt) {
			throw new XMetaIDocHandlingException(excpt);
		}

		logger.exiting(CLASSNAME, METHODNAME);
	}

} // end of class XMetaIDocMetadataServiceImpl 
