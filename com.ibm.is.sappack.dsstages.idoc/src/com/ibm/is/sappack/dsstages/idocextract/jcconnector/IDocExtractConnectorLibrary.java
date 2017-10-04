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
// Module Name : com.ibm.is.sappack.dsstages.idocextract.jcconnector
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.dsstages.idocextract.jcconnector;

import java.util.Date;
import java.util.logging.Level;

import com.ibm.is.sappack.dsstages.common.ccf.ConnectorLibrary;

public class IDocExtractConnectorLibrary extends ConnectorLibrary {

	static String copyright() {
		return com.ibm.is.sappack.dsstages.idocextract.jcconnector.Copyright.IBM_COPYRIGHT_SHORT;
	}

	static private String CLASSNAME = IDocExtractConnectorLibrary.class.getName();
	
	public IDocExtractConnectorLibrary() {
		super("DSSAP_IDOC_Extract_"); //$NON-NLS-1$
		final String METHODNAME = "<init>"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);
		this.logger.log(Level.INFO, "CC_IDOC_EXTRACT_JobStart", new Date()); //$NON-NLS-1$

		this.logger.exiting(CLASSNAME, METHODNAME);
	}

	@Override
	public Class getAdapterClass() {
		final String METHODNAME = "getAdapterClass"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);
		Class cl = IDocExtractAdapter.class;
		this.logger.exiting(CLASSNAME, METHODNAME);
		return cl;
	}

	@Override
	public Class getConnectionClass() {
		final String METHODNAME = "getConnectionClass()"; //$NON-NLS-1$
		logger.entering(CLASSNAME, METHODNAME);
		Class result = IDocExtractConnection.class;
		logger.exiting(CLASSNAME, METHODNAME);
		return result;
	}

}
