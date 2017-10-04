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
// Module Name : com.ibm.is.sappack.dsstages.common.impl
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.dsstages.common.impl;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.is.sappack.dsstages.common.IDocType;
import com.ibm.is.sappack.dsstages.common.StageLogger;
import com.ibm.is.sappack.dsstages.common.util.IDocMetadataFileHandler;

/**
 * Retrieves IDoc type metadata from an IDoc metadata file. This is still independent of the SAP connection at the
 * moment, for backwards compatibility.
 */
public class MetadataFileIDocTypeRetriever {

	static String copyright() {
		return com.ibm.is.sappack.dsstages.common.impl.Copyright.IBM_COPYRIGHT_SHORT;
	}

	// constants
	static final String CLASSNAME = MetadataFileIDocTypeRetriever.class.getName();

	// members
	private Logger logger;
	private String system;

	/**
	 * Default constructor
	 */
	public MetadataFileIDocTypeRetriever(String sapSystemName) {
		logger = StageLogger.getLogger();
		system = sapSystemName;
	}

	/**
	 * This function constructs an IDocType object by reading metadata information for the given IDoc type and release
	 * version from an IDoc metadata file.
	 * 
	 * @param idocTypeName
	 *           - the IDoc type name
	 * @param basicTypeName
	 *           - the name of the basic type for this IDoc type
	 * @param release
	 *           - the release version of the IDoc type
	 * @return a valid IDocType object constructed from the information in an IDoc metadata file
	 * @throws IOException
	 *            in case the IDoc metadata file could not be read
	 */
	public IDocType retrieveIDocType(String idocTypeName, String basicTypeName, String release) throws IOException {
		final String METHODNAME = "retrieveIDocType(String idocTypeName, String basicTypeName, String release)"; //$NON-NLS-1$
		logger.entering(CLASSNAME, METHODNAME);

		IDocMetadataFileHandler metadataFileHandler = new IDocMetadataFileHandler();
		metadataFileHandler.initialize(system, idocTypeName, basicTypeName, release);

		logger.log(Level.INFO, "CC_IDOC_TypeMetadataSAPRetrieval",            //$NON-NLS-1$
		                        new Object[] { idocTypeName, metadataFileHandler.getFileName() });

		IDocTypeImpl idocType = metadataFileHandler.readMetadataFromFile();

		// we have to check whether the IDocTypeImpl object returned is NULL
		// this could be the case when the metadata information could not be retrieved
		// from the file
		if (idocType != null) {
			idocType.setIDocTypeName(idocTypeName);
		}

		logger.exiting(CLASSNAME, METHODNAME);

		return idocType;
	}
}
