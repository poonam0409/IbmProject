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
// Module Name : com.ibm.is.sappack.dsstages.common
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.dsstages.common;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.is.sappack.dsstages.common.impl.DSSAPConnectionImpl;
import com.ibm.is.sappack.dsstages.common.impl.MetadataFileIDocTypeRetriever;
import com.ibm.is.sappack.dsstages.common.impl.SapIDocTypeRetriever;
import com.ibm.is.sappack.dsstages.common.util.IDocMetadataFileHandler;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;

public class IDocMetadataFactory {

	static String copyright() {
		return com.ibm.is.sappack.dsstages.common.Copyright.IBM_COPYRIGHT_SHORT;
	}

	public static IDocType createIDocType(JCoDestination jcoDest, String sapSystemName, String idocTypeName, String basicTypeName, String release)
	      throws JCoException, IOException {
		IDocType result = null;
		IDocMetadataFileHandler metadataFileHandler = new IDocMetadataFileHandler();
		metadataFileHandler.initialize(sapSystemName, idocTypeName, basicTypeName, release);

		if (metadataFileHandler.hasMetadataFile()) {
			MetadataFileIDocTypeRetriever metadataRetriever = new MetadataFileIDocTypeRetriever(sapSystemName);
			result = metadataRetriever.retrieveIDocType(idocTypeName, basicTypeName, release);
			
			// we have to check if the IDocType object returned is NULL
			// which could be the case that we were not able to retrieve the IDoc metadata
			// information from the metadata file
			if (result == null) {
				Logger logger = StageLogger.getLogger();
				logger.log(Level.INFO, "CC_IDOC_TypeMetadataFileRetrievalFailed"); //$NON-NLS-1$
				
				// as a fallback solution we retrieve the metadata information from SAP directly
				SapIDocTypeRetriever idocRetriever = new SapIDocTypeRetriever(jcoDest, sapSystemName);
				result = idocRetriever.retrieveIDocTypeFromSAP(idocTypeName, basicTypeName, release);
			}
		}
		else {
			SapIDocTypeRetriever idocRetriever = new SapIDocTypeRetriever(jcoDest, sapSystemName);
			result = idocRetriever.retrieveIDocTypeFromSAP(idocTypeName, basicTypeName, release);
		}

		return result;
	}

	public static DSSAPConnection createDSSAPConnection() {
		return new DSSAPConnectionImpl();
	}
}
