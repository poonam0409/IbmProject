//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2013                                              
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

import java.util.logging.Level;
import java.util.logging.Logger;


public final class ISVersionChecker {
	
	private static final String TEST_CLASS_IS_V8   = "com.ascential.asb.util.security.DSEncryption";
	private static final String TEST_CLASS_IS_V10  = "com.ibm.iis.isf.util.security.DSEncryption";


	public enum ISEnvVersion { NotAvailable, v8x, v10x };
	

	static String copyright() { 
	   return com.ibm.is.sappack.dsstages.xmeta.common.Copyright.IBM_COPYRIGHT_SHORT; 
	}

	public static ISEnvVersion getISEnvironmentVersion(Logger logger) {
		
		ISEnvVersion  retVersion;
		boolean       classCouldBeloaded;

		logger.finer("ENTRY");

		// check if it's a IS v8/9 environment
		retVersion = ISEnvVersion.NotAvailable;
		classCouldBeloaded = isClassAvailable(TEST_CLASS_IS_V8, logger);
		if (classCouldBeloaded) {
			retVersion = ISEnvVersion.v8x;
			logger.finer("IS DataStage v8.x/9.x environment detected.");
		}
		else {
			classCouldBeloaded = isClassAvailable(TEST_CLASS_IS_V10, logger);
			if (classCouldBeloaded) {
				retVersion = ISEnvVersion.v10x;
				logger.finer("IS DataStage v10.x environment detected.");
			} // end of if (classCouldBeloaded) (V10)
		} // end of (else) if (classCouldBeloaded) (V8)

		logger.finer("EXIT: ver = " + retVersion.toString());

		return(retVersion);
	}


	private static boolean isClassAvailable(String className, Logger logger) {
		boolean retClassCouldBeloaded;

      retClassCouldBeloaded = false;
		try {
			Class tmpLoadClass;

	      tmpLoadClass = Class.forName(className);
	      logger.finest("IS class '" + tmpLoadClass.getName() + "' was found.");
	      retClassCouldBeloaded = true;
		} // end of try
      catch(ClassNotFoundException pClassNotFoundExcpt) {
//			TraceLogger.trace(TraceLogger.LEVEL_FINEST, pClassNotFoundExcpt.toString());
	      logger.finest("IS class '" + className + "' could not be found.");
      } // end of catch(ClassNotFoundException pClassNotFoundExcpt)
		catch(Exception pExcpt) {
			logger.log(Level.SEVERE, "", pExcpt);
		} // end of catch(ClassNotFoundException classNotFoundExcpt)

      return(retClassCouldBeloaded);
	} // end of isClassAvailable()

} // end of class ISVersionChecker
