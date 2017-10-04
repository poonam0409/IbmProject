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
// Module Name : com.ibm.is.sappack.dsstages.xmeta.common
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.dsstages.xmeta.common;


import java.util.logging.Logger;

import org.eclipse.emf.ecore.EObject;

import ASCLModel.ASCLModelFactory;
import ASCLModel.HostSystem;


@SuppressWarnings("nls")
/**
 * Handles the caching/saving of IDoc metadata in the Metadata Repository (XMeta).
 * IDoc elements are represented using standard Common Model (ASCLModel) objects.
 */
public interface XMetaIDocMetadataService {
	// -------------------------------------------------------------------------------------
	//                                      Constants
	//-------------------------------------------------------------------------------------
	public static final String COPYRIGHT             = com.ibm.is.sappack.dsstages.xmeta.common.Copyright.IBM_COPYRIGHT_SHORT;
	       static final String XMETA_HOSTSYSTEM_TYPE = "SAP";


	//-------------------------------------------------------------------------------------
	//                                  Interface methods
	//-------------------------------------------------------------------------------------
   public void             beginTransaction() throws XMetaIDocHandlingException;
   public void             commitTransaction() throws XMetaIDocHandlingException;
   public void             abortTransaction() throws XMetaIDocHandlingException;
   public void             initializeConnection(Logger logger) throws XMetaIDocHandlingException;
   public void             releaseConnection() throws XMetaIDocHandlingException;
   public HostSystem       getHostSystem(String name) throws XMetaIDocHandlingException;
   public ASCLModelFactory getModelFactory();
   public void             markObjectForDelete(EObject eObjToBeDeleted) throws XMetaIDocHandlingException;
   public void             markObjectForSave(EObject eObjToBeSaved) throws XMetaIDocHandlingException;
   public void             saveObject() throws XMetaIDocHandlingException;

} // end of interface XMetaIDocMetadataService 
