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
// Module Name : com.ibm.is.sappack.dsstages.idocload.jcconnector
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.dsstages.idocload.jcconnector;

import java.util.Date;
import java.util.logging.Level;

import com.ibm.is.sappack.dsstages.common.ccf.ConnectorLibrary;

public class IDocLoadConnectorLibrary extends ConnectorLibrary {

	static String copyright() {
		return com.ibm.is.sappack.dsstages.idocload.jcconnector.Copyright.IBM_COPYRIGHT_SHORT;
	}

	static String CLASSNAME = IDocLoadConnectorLibrary.class.getName();

	public IDocLoadConnectorLibrary() {
		super("DSSAP_IDOC_Load_"); //$NON-NLS-1$
		final String METHODNAME = "<init>"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);
		this.logger.log(Level.INFO, "CC_IDOC_LOAD_JobStart", new Date());  //$NON-NLS-1$
		this.logger.exiting(CLASSNAME, METHODNAME);
	}

	@Override
	public Class getAdapterClass() {
		final String METHODNAME = "getAdapterClass()"; //$NON-NLS-1$
		logger.entering(CLASSNAME, METHODNAME);
		Class result = IDocLoadAdapter.class;
		logger.exiting(CLASSNAME, METHODNAME);
		return result;
	}


}
