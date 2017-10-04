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
// Module Name : com.ibm.is.sappack.dsstages.xmeta.common
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.dsstages.xmeta.common;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class IDocXMetaLogger {

	static String copyright() {
		return com.ibm.is.sappack.dsstages.xmeta.common.Copyright.IBM_COPYRIGHT_SHORT;
	}

	public static final String LOGGER_ID = "com.ibm.is.sappack.dsstages.xmeta"; //$NON-NLS-1$

	private static Logger logger = null;

	public static Logger getLogger() {
		if (logger == null) {
			logger = Logger.getLogger(LOGGER_ID);

			try {
				FileHandler fh = new FileHandler(System.getProperty("java.io.tmpdir") + File.separator + "idoc_xmeta.log");
				fh.setLevel(Level.ALL);
				fh.setFormatter(new SimpleFormatter());
				logger.addHandler(fh);
				logger.setLevel(Level.ALL);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return logger;
	}

}
