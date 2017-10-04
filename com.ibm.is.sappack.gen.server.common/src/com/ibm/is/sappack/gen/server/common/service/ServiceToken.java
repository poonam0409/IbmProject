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
// Module Name : com.ibm.is.sappack.gen.server.datastage
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.server.common.service;


import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import DataStageX.DSJobDef;
import DataStageX.DataStageXFactory;

import com.ibm.is.sappack.gen.common.util.DSFolder;
import com.ibm.is.sappack.gen.common.util.DSProject;
import com.ibm.is.sappack.gen.server.common.util.DSAccessException;
import com.ibm.is.sappack.gen.server.common.util.DomainAccessException;


public interface ServiceToken
{
   // -------------------------------------------------------------------------------------
   //                                       Constants
   // -------------------------------------------------------------------------------------
	public static final String COPYRIGHT = com.ibm.is.sappack.gen.server.common.service.Copyright.IBM_COPYRIGHT_SHORT; 


   // -------------------------------------------------------------------------------------
   //                                      Enumerations
   // -------------------------------------------------------------------------------------
	public enum QryOptions { DEFAULT, ROOT_ONLY, CONTAINMENT_ONLY, NO_PROXIES, ACQUIRE_LOCKS, CONTAINER };


   // -------------------------------------------------------------------------------------
   //                                    Interface methods
   // -------------------------------------------------------------------------------------
   public void                  loginIntoDomain(String isUserName, String isPassword, String domainServerName, int domainServerPort) throws DSAccessException;
   public void                  logoutFromDomain() throws DomainAccessException;

   public String                encrypt(String value);
   public String                decrypt(String value);

	public List<EObject>         executeObjectQuery(String query, EPackage ePackage, QryOptions queryOpts) throws DSAccessException;
	public List<Object[]>        executeValueQuery(String query, EPackage ePackage) throws DSAccessException;

	public DataStageXFactory     getDSXFactory() throws DSAccessException;
   public DSFolder              getDSFolder();
   public DSProject             getDSProject();
   public String                getProjectNameSpace();
   public void                  setDSFolder(DSFolder dsFolder); 
   public void                  setDSProject(DSProject dsProject); 

   public void                  abortTransaction() throws DSAccessException;
   public void                  commitTransaction() throws DSAccessException;
   public boolean               isInTransaction() throws DSAccessException;
   public void                  markForDelete(EObject eObject) throws DSAccessException;
   public void                  startTransaction() throws DSAccessException;
   public void                  save() throws DSAccessException;

   public void                  uvDBConnect();
   public void                  uvDBCreateJob(DSJobDef dsJobDef, String folderName) throws DSAccessException;
   public void                  uvDBDeleteJob(String jobName) throws DSAccessException;
   public void                  uvDBDisconnect();

} // end of interface ServiceToken
