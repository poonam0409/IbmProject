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
// Module Name : com.ibm.is.sappack.dsstages.idocextract.jcconnector
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.dsstages.idocextract.jcconnector;

import com.ascential.e2.daapi.metadata.CC_Environment;
import com.ascential.e2.propertyset.CC_ErrorList;
import com.ascential.e2.propertyset.CC_PropertySet;
import com.ibm.is.sappack.dsstages.common.ccf.CCFIDocConnection;

/**
 * This class is only needed to overwrite the setEnvironment() method to disable
 * parallelism.
 * @author suhre
 *
 */
public class IDocExtractConnection extends CCFIDocConnection {
	static String copyright() {
		return com.ibm.is.sappack.dsstages.idocextract.jcconnector.Copyright.IBM_COPYRIGHT_SHORT;
	}

	final static String CLASSNAME = IDocExtractConnection.class.getName();

	public IDocExtractConnection(CC_PropertySet propSet, CC_ErrorList errList) {
		super(propSet, errList);
		final String METHODNAME = "<init>"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);
		this.logger.exiting(CLASSNAME, METHODNAME);
	}
	
	@Override
	public boolean setEnvironment(CC_Environment env, boolean negotiable) {
		final String METHODNAME = "setEnvironment"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);
		boolean result = super.setEnvironment(env, negotiable);
		this.logger.exiting(CLASSNAME, METHODNAME);
		return result;
	}

}
