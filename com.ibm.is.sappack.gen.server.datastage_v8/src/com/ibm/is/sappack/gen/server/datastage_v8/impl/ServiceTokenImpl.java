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
// Module Name : com.ibm.is.sappack.gen.server.datastage_v8.impl
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.server.datastage_v8.impl;


import java.util.List;
import java.util.Locale;
import java.util.Vector;

import javax.security.auth.login.LoginException;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import DataStageX.DSJobDef;
import DataStageX.DataStageXFactory;

import com.ascential.acs.security.auth.client.AuthenticationService;
import com.ascential.asb.util.security.DSEncryption;
import com.ascential.xmeta.client.repository.core.CoreRepositoryClientFactory;
import com.ascential.xmeta.client.repository.core.ISandboxClient;
import com.ascential.xmeta.client.repository.query.IQueryResult;
import com.ascential.xmeta.client.repository.query.QueryOptions;
import com.ascential.xmeta.exception.data.SessionLockException;
import com.ascential.xmeta.model.XMetaBasePackage.XMetaBasePackagePackage;
import com.ascential.xmeta.model.XMetaBasePackage.XMetaRepositoryObject;
import com.ibm.is.sappack.gen.common.Constants;
import com.ibm.is.sappack.gen.common.trace.TraceLogger;
import com.ibm.is.sappack.gen.common.util.DSFolder;
import com.ibm.is.sappack.gen.common.util.DSProject;
import com.ibm.is.sappack.gen.server.common.service.ServiceToken;
import com.ibm.is.sappack.gen.server.common.util.DSAccessException;
import com.ibm.is.sappack.gen.server.common.util.DomainAccessException;
import com.ibm.is.sappack.gen.server.datastage_v8.UVAccessor;


public final class ServiceTokenImpl implements ServiceToken
{
   // -------------------------------------------------------------------------------------
   //                                       Constants
   // -------------------------------------------------------------------------------------

   
   // -------------------------------------------------------------------------------------
   //                                 Member Variables
   // -------------------------------------------------------------------------------------
   private   ISandboxClient         _SandboxClient;
   private   DSProject              _DSProject;
   private   DSFolder               _DSFolder;
   private   AuthenticationService  _AuthenticationService;
   private   UVAccessor             _UniverseDBAccessor;
//   private        MessageCatalog        _MessageCatalog;


   static String copyright()
   { 
      return com.ibm.is.sappack.gen.server.datastage_v8.impl.Copyright.IBM_COPYRIGHT_SHORT; 
   }


   /**
    * This method creates the Token instance.
    * 
    * @param locale  locale to be used for messages
    */
   ServiceTokenImpl(Locale locale) throws DSAccessException
   {
      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.entry("message locale = " + locale);
      }
      
      // initialize the instance
      init(locale);
      
      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.exit();
      }
   } // end of ServiceToken()
   
   
   private void init(Locale locale)
           throws DSAccessException
   {
      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.entry("message locale = " + locale);
      }

      // create a message catalog with passed or default locale
      if (locale == null)
      {
         locale = Locale.getDefault();
      }
      
//      _MessageCatalog = MessageCatalog.createMessageCatalog(locale);
      
      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.exit();
      }
   } // end of init()


   public void abortTransaction() throws DSAccessException
   {
      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.entry();
      }

      try
      {
         _SandboxClient.abortTransaction();
      }  // end of try
      catch(Exception transactionExcpt)
      {
         if (TraceLogger.isTraceEnabled())
         {
            TraceLogger.trace(TraceLogger.LEVEL_FINEST, "Error when rolling back current transaction:" + transactionExcpt.getMessage());
            TraceLogger.traceException(transactionExcpt);
         }

         throw new DSAccessException("104000E", Constants.NO_PARAMS, transactionExcpt);
      } // end of catch(SystemException transactionExcpt)

      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.exit();
      }
   } // end of abortTransaction()
   
   
   public void commitTransaction() throws DSAccessException
   {
   	boolean transActionCommitted;

      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.entry();
      }

      transActionCommitted = false;
      try
      {
         if (_SandboxClient.isInTransaction()) {
         	_SandboxClient.commitTransaction();
            transActionCommitted = true;
         }
      }  // end of try
      catch(Exception transactionExcpt)
      {
         throw new DSAccessException("104200E", Constants.NO_PARAMS, transactionExcpt);
      } // end of catch(Exception sandBoxExcpt)

      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.exit("transaction commited: " + transActionCommitted);
      }
   } // end of commitTransaction()

   
   public String encrypt(String value) {
   	return(DSEncryption.encrypt(value));
   } // end of encrypt()


   public String decrypt(String value) {
   	return(DSEncryption.decrypt(value));
   } // end of decrypt()


	/**
	 * This method performs a XMETA object query.
	 * 
	 * @param query    query string
	 * @param ePackage used EPackage
	 * 
	 * @return         query result list
	 * 
	 * @throws DSAccessException
	 *            if an error occurred
	 */
	public List<EObject> executeObjectQuery(String query, EPackage ePackage, QryOptions queryOpts) throws DSAccessException {
		return(executeQuery(query, ePackage, queryOpts));
	}


	/**
	 * This method performs a XMETA value query.
	 * 
	 * @param query    query string
	 * @param ePackage used EPackage
	 * 
	 * @return         query result list
	 * 
	 * @throws DSAccessException
	 *            if an error occurred
	 */
	public List<Object[]> executeValueQuery(String query, EPackage ePackage) throws DSAccessException {
		return(executeQuery(query, ePackage, QryOptions.DEFAULT));
	}


	/**
	 * This method performs an XMETA query based on the passed SandBox.
	 * 
	 * @param query
	 *           query string
	 * @param ePackage
	 *           used EPackage
	 * @param queryOpts
	 *           query options
	 * @param accessToken
	 *           access token
	 * 
	 * @return query result list
	 * 
	 * @throws DSAccessException
	 *            if an error occurred
	 */
	private List executeQuery(String query, EPackage ePackage, QryOptions queryOpts)
	        throws DSAccessException {
	   
      IQueryResult queryResult;
		QueryOptions quryOptions;
		List         resultList;

		// translate the passed QueryOtopn into an ObjectQueryOption
		switch(queryOpts) {
			case ACQUIRE_LOCKS:
				quryOptions = QueryOptions.ACQUIRE_LOCKS;
				break;
			case CONTAINMENT_ONLY:
				quryOptions = QueryOptions.CONTAINMENT_ONLY;
				break;
			case NO_PROXIES:
				quryOptions = QueryOptions.NO_PROXIES;
				break;
			case ROOT_ONLY:
				quryOptions = QueryOptions.ROOT_ONLY;
				break;

			case DEFAULT:
			default:
				quryOptions = QueryOptions.DEFAULT;
		}
		try {
		   // execute the query ...
		   queryResult = _SandboxClient.query(query, ePackage.getNsURI(), quryOptions);
		   
		   // ... get the complete result ...
		   resultList = queryResult.getAll();
		   
		   // .. and release the query result
//		   queryResult.getQuery().release();
		   queryResult.release();
		}
		catch (Exception pExcpt) {
			if (TraceLogger.isTraceEnabled()) {
				TraceLogger.traceException(pExcpt);
			}
			
			throw new DSAccessException("101200E", new String[] { query, pExcpt.toString() }, pExcpt);
		}

		return (resultList);
	} // end of executeQuery()

	
	/**
	 * This method returns the DataStageX factory instance.
	 * 
	 * @return DataStageX factory
	 * 
	 * @throws DSAccessException if an unexpected error occurred
	**/
	public DataStageXFactory getDSXFactory() throws DSAccessException {
		DataStageXFactory retDSXFactory;

      try 
      {
         retDSXFactory = (DataStageXFactory) _SandboxClient.getChangeTrackingFactory(DataStageXFactory.eINSTANCE);
      } 
      catch (Exception pExcpt) 
      {
         if (TraceLogger.isTraceEnabled())
         {
            TraceLogger.traceException(pExcpt);
         }
         
         throw new DSAccessException("120800E", Constants.NO_PARAMS, pExcpt);
      }

		return(retDSXFactory);
	} // end of getDSXFactory()

	
   public DSFolder getDSFolder()
   {
      return (_DSFolder);
   } // end of getDSFolder()

   
   public DSProject getDSProject()
   {
      return (_DSProject);
   } // end of getDSProject()

   
   public String getProjectNameSpace()
   {
      String retNameSpace;
      
      if (_DSProject == null)
      {
        retNameSpace = null;
      }
      else
      {
        retNameSpace = _DSProject.getNameSpace();
      }
      
      return(retNameSpace);
   } // end of getProjectNameSpace()
   
   
	/**
	 * This method returns the XMeta Repository object for the passed DS rid.
	 * 
	 * @param objectRid
	 *           rid of the XMeta object to be returned
	 * @param srvcToken
	 *           access token
	 * 
	 * @return XMeta repository object
	 * 
	 * @throws DSAccessException
	 *            if an error occurred
	 */
	public XMetaRepositoryObject getXMetaObjectByRid(String dsRid) throws DSAccessException {
		XMetaRepositoryObject retXMetaObj;

		String query = "select x from x in " + XMetaBasePackagePackage.eINSTANCE.getName() + "::"
		             + XMetaBasePackagePackage.eINSTANCE.getEClassifier("XMetaRepositoryObject").getName() //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
		             + " where rid(x) = \"" + dsRid + "\""; //$NON-NLS-1$//$NON-NLS-2$

		List queryResult = executeQuery(query, XMetaBasePackagePackage.eINSTANCE, QryOptions.CONTAINMENT_ONLY);

		retXMetaObj = null;
		if (queryResult != null && !queryResult.isEmpty()) {
			retXMetaObj = (XMetaRepositoryObject) queryResult.get(0);
		}

		return (retXMetaObj);
	} // end of getXMetaObjectByRid()


/*   
   public MessageCatalog getMessageCatalog()
   {
      return (k);
   } // end of getMessageCatalog()
*/
   
   
   public boolean isInTransaction() throws DSAccessException
   {
      boolean inTransaction;

      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.entry();
      }

      inTransaction = false;
      try
      {
         inTransaction = _SandboxClient.isInTransaction();

         if (TraceLogger.isTraceEnabled())
         {
            TraceLogger.trace(TraceLogger.LEVEL_FINEST, "in transaction = " + inTransaction);
         }
      }  // end of try
      catch(Exception transactionExcpt)
      {
         if (TraceLogger.isTraceEnabled())
         {
            TraceLogger.trace(TraceLogger.LEVEL_FINEST, "Error when retrieving transaction status:" + transactionExcpt.getMessage());
            TraceLogger.traceException(transactionExcpt);
         }

         throw new DSAccessException("105600E", Constants.NO_PARAMS, transactionExcpt);
      } // end of catch(SystemException transactionExcpt)

      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.exit("in transaction = " + inTransaction);
      }

      return(inTransaction);
   } // end of isInTransaction()
// */

   public void loginIntoDomain(String isUserName, String isPassword, 
                               String domainServerName, int domainServerPort) 
          throws DSAccessException {

      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.entry();
      }

      try 
      {
         _AuthenticationService = new AuthenticationService();
         _AuthenticationService.doLogin(isUserName, isPassword.toCharArray(), 
                                        domainServerName, domainServerPort, null);
      } 
      catch (LoginException loginExcpt) 
      {
         _AuthenticationService = null;

         if (TraceLogger.isTraceEnabled())
         {
            TraceLogger.traceException(loginExcpt);
         }

         throw new DomainAccessException(loginExcpt.getMessage());
      }
      catch (Exception excpt) 
      {
         _AuthenticationService = null;

         if (TraceLogger.isTraceEnabled())
         {
            TraceLogger.traceException(excpt);
         }

         throw new DomainAccessException(excpt.getMessage(), excpt);
      }

      // if the login was successful ... 
      try 
      {
         // ==> create the SandboxClient ...
         _SandboxClient = CoreRepositoryClientFactory.createSandboxClient();
      } 
      catch(Exception pExcpt) 
      {
         if (TraceLogger.isTraceEnabled())
         {
            TraceLogger.traceException(pExcpt);
         }

         // logout from domain ...
         logoutFromDomain();
         
         throw new DSAccessException("103900E", Constants.NO_PARAMS, pExcpt);
      } // end of catch(Exception pExcpt)
      
      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.exit();
      }
   } // end of loginIntoDomain()


   public void logoutFromDomain() throws DomainAccessException {
      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.entry();
      }

      try 
      {
      	// commit the current user transaction
      	commitTransaction();

         // release the sandbox client ... 
         if (_SandboxClient != null) {
            _SandboxClient.flushCache();
            _SandboxClient.release();
            
            _SandboxClient = null;
         }

         // and logout from domain server
         if (_AuthenticationService != null) {
            _AuthenticationService.logout();
            
            _AuthenticationService = null;
         }
      } 
      catch (Exception excpt) 
      {
         if (TraceLogger.isTraceEnabled())
         {
            TraceLogger.traceException(excpt);
         }

         throw new DomainAccessException(excpt.getMessage(), excpt);
      }

      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.exit();
      }
   } // end of logoutFromDomain()


   public void markForDelete(EObject eObject) throws DSAccessException
   {
      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.entry("EObject = " + eObject);
      }

      try
      {
      	Vector<EObject> tmpVector = new Vector<EObject>();
      	tmpVector.add(eObject);
         _SandboxClient.markForDelete(tmpVector);
      }  // end of try
      catch(Exception sandBoxExcpt)
      {
         throw new DSAccessException("104300E", Constants.NO_PARAMS, sandBoxExcpt);
      } // end of catch(Exception sandBoxExcpt)

      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.exit();
      }
   } // end of markForDelete()


   public void save() throws DSAccessException
   {
      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.entry();
      }
      
      try
      {
         _SandboxClient.save();
      }  // end of try
      catch(SessionLockException lockExcpt)
      {
         throw new DSAccessException("101600E", new String[] { lockExcpt.getMessage()} );
      } // end of catch(SessionLockException lockExcpt)
      catch(Exception sandBoxExcpt)
      {
         throw new DSAccessException("103800E", Constants.NO_PARAMS, sandBoxExcpt);
      } // end of catch(Exception sandBoxExcpt)
      
      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.exit();
      }
   } // end of save()

   
   public void setDSFolder(DSFolder dsFolder) 
   { 
      _DSFolder = dsFolder; 
   } // end of setDSFolder()


   public void setDSProject(DSProject dsProject) 
   {
      _DSProject = dsProject; 
   } // end of setDSProject()


   public void startTransaction() throws DSAccessException
   {
   	boolean isInTransaction;

      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.entry();
      }

      isInTransaction = false;
      try
      {
         // commit an existing transaction before creating a new one
         if (isInTransaction()) {
            if (TraceLogger.isTraceEnabled())
            {
               TraceLogger.trace(TraceLogger.LEVEL_FINER, "The open transaction will be closed first.");
            }
            commitTransaction();
         } // end of if (_UserTransaction != null)

         _SandboxClient.beginTransaction();
         isInTransaction = _SandboxClient.isInTransaction();

      }  // end of try
      catch(Exception sandBoxExcpt)
      {
         throw new DSAccessException("104100E", Constants.NO_PARAMS, sandBoxExcpt);
      } // end of catch(Exception sandBoxExcpt)

      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.exit("transaction started = " + isInTransaction);
      }
   } // end of startTransaction()
   
   
   public String toString()
   {
      return("Project NS = '" + getProjectNameSpace() + "' - Folder = '" + _DSFolder.getName() + "' - " + super.toString());
   } // end of to String()


   /**
    * This method connects to the Universe DB.
    *  
    * @throws DSAccessException if an error occurred
    */
   public void uvDBConnect()
   {
      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.entry();
      }
      
      if (_UniverseDBAccessor != null)
      {
         _UniverseDBAccessor.disconnect();
      }
      
      //3. drop and create job from universe DB, make sure drop of job and drop of UV instance is within same transaction
      try
      {
         _UniverseDBAccessor = new UVAccessor(_DSProject);
         _UniverseDBAccessor.connect();
      }
      catch(Exception excpt)
      {
         if (TraceLogger.isTraceEnabled())
         {
            TraceLogger.trace(TraceLogger.LEVEL_FINE, "UvDB Connection error: " + excpt);
            TraceLogger.traceException(excpt);
         }
         _UniverseDBAccessor = null;
      }

/*      
      //retrieve the targeted DS job folder via its rid
      this.serializerContext.setDsJobFolder(queryFactory.getDSFolderByRid(dsFolderRid));
      if(dsTableDefFolderRid != null){
         this.serializerContext.setDsTableDefFolder(queryFactory.getDSFolderByRid(dsTableDefFolderRid));
      }
*/      
      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.exit();
      }
   } // end of uvDBConnect()
   
   
   /**
    * This private method creates a JobItem in the Universe DB.
    * 
    * @param folderName  folder name
    * @param dsJobDef    job definition
    * 
    * @throws JobLockedException
    */
   public void uvDBCreateJob(DSJobDef dsJobDef, String folderName) throws DSAccessException
   {
      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.entry("DSJobDef = " + dsJobDef.getName());
      }
      
      if (_UniverseDBAccessor == null)
      {
         uvDBConnect();
      }
      
      _UniverseDBAccessor.createJob(folderName, dsJobDef.getName());
      
      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.exit();
      }
   } // end of uvDBCreateJob()

   
   public void uvDBDeleteJob(String jobName) throws DSAccessException
   {
      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.entry("Job = " + jobName);
         TraceLogger.trace(TraceLogger.LEVEL_FINEST, "UVAccessor = " + _UniverseDBAccessor);
      }

      if (_UniverseDBAccessor != null)
      {
         _UniverseDBAccessor.deleteJob(jobName);
      }
      
      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.exit();
      }
   } // end of uvDBDeleteJob()
   
   
   /**
    * This methods disconnects from the Universe database.
    */
   public void uvDBDisconnect()
   {
      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.entry("UVAccessor = " + _UniverseDBAccessor);
      }
      
      if (_UniverseDBAccessor != null)
      {
         _UniverseDBAccessor.disconnect();
      }
      
      if (TraceLogger.isTraceEnabled())
      {
         TraceLogger.exit();
      }
   } // end of uvDBDisconnect()

} // end of class ServiceTokenImpl
