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
// Module Name : com.ibm.is.sappack.dsstages.idocload.util
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.dsstages.idocload.util;

import java.util.logging.Logger;

import com.ibm.is.sappack.dsstages.common.Constants;
import com.ibm.is.sappack.dsstages.common.StageLogger;

/**
 * Class that encapsulates the character data buffer handling for an IDoc file entry.
 */
public class IDocFileEntry {

	static String copyright() {
		return com.ibm.is.sappack.dsstages.idocload.util.Copyright.IBM_COPYRIGHT_SHORT;
	}
	
	// constants
	private static final String CLASSNAME = IDocFileEntry.class.getName();

	// members
	private Logger logger;
	private StringBuffer entry;

	/**
	 * Default constructor
	 */
	public IDocFileEntry() {
		logger = StageLogger.getLogger();
		entry = new StringBuffer();
	}

	/**
	 * This function appends the given character array to the existing data.
	 * 
	 * @param data
	 *           - the character array to append to the buffer
	 */
	public void append(char[] data) {
		final String METHODNAME = "append(char[] data)"; //$NON-NLS-1$
		logger.entering(CLASSNAME, METHODNAME);

		entry.append(data);
		
		logger.exiting(CLASSNAME, METHODNAME);
	}
	
	/**
	 * This function appends a newline character to the existing data.
	 */
	public void newline() {
		final String METHODNAME = "newline()"; //$NON-NLS-1$
		logger.entering(CLASSNAME, METHODNAME);

		entry.append(Constants.LINE_SEPARATOR);

		logger.exiting(CLASSNAME, METHODNAME);
	}

	/**
	 * This function retrieves the complete character data.
	 * 
	 * @return the character data buffer as a String object
	 */
	public String retrieve() {
		final String METHODNAME = "retrieve()"; //$NON-NLS-1$
		logger.entering(CLASSNAME, METHODNAME);
		logger.exiting(CLASSNAME, METHODNAME);

		return entry.toString();
	}
}
