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
// Module Name : com.ibm.is.sappack.gen.server.common.util
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.server.common.util;


import com.ibm.is.sappack.gen.common.trace.TraceLogger;
import com.ibm.is.sappack.gen.common.util.ServerMessageCatalog;


public final class ISVersionChecker {
	
	private static final String TEST_CLASS_IS_V8   = "com.ascential.asb.util.security.DSEncryption";
	private static final String TEST_CLASS_IS_V10  = "com.ibm.iis.isf.util.security.DSEncryption";


	public enum ISEnvVersion { NotAvailable, v8x, v10x };
	

	static String copyright() { 
	   return com.ibm.is.sappack.gen.server.common.util.Copyright.IBM_COPYRIGHT_SHORT; 
	}

	public static String checkISEnvironmentVersion() {
		ISEnvVersion curISVersion;
		String       retISFamilyVersion;

		TraceLogger.entry();

		// get the current version ...
		curISVersion = getISEnvironmentVersion();
		switch(curISVersion) {
			case v8x:
			case v10x:
				retISFamilyVersion = ServerMessageCatalog.getDefaultCatalog().getText("00103I");
				break;

			default:
				retISFamilyVersion = ServerMessageCatalog.getDefaultCatalog().getText("105700E");
		}

		TraceLogger.exit("ver = " + retISFamilyVersion);

		return(retISFamilyVersion);
	} // end of checkISEnvironmentVersion()


	public static ISEnvVersion getISEnvironmentVersion() {
		
		ISEnvVersion  retVersion;
		boolean       classCouldBeloaded;

		TraceLogger.entry();

		// check if it's a IS v8/9 environment
		retVersion = ISEnvVersion.NotAvailable;
		classCouldBeloaded = isClassAvailable(TEST_CLASS_IS_V8);
		if (classCouldBeloaded) {
			retVersion = ISEnvVersion.v8x;
			TraceLogger.trace(TraceLogger.LEVEL_FINER,  "IS DataStage v8.x/9.x environment detected.");
		}
		else {
			classCouldBeloaded = isClassAvailable(TEST_CLASS_IS_V10);
			if (classCouldBeloaded) {
				retVersion = ISEnvVersion.v10x;
				TraceLogger.trace(TraceLogger.LEVEL_FINER,  "IS DataStage v10.x environment detected.");
			} // end of if (classCouldBeloaded) (V10)
		} // end of (else) if (classCouldBeloaded) (V8)

		TraceLogger.exit("ver = " + retVersion.toString());

		return(retVersion);
	}


	private static boolean isClassAvailable(String className) {
		boolean retClassCouldBeloaded;

      retClassCouldBeloaded = false;
		try {
			Class tmpLoadClass;

	      tmpLoadClass = Class.forName(className);
	      TraceLogger.trace(TraceLogger.LEVEL_FINEST, "IS class '" + tmpLoadClass.getName() + "' was found.");
	      retClassCouldBeloaded = true;
		} // end of try
      catch(ClassNotFoundException pClassNotFoundExcpt) {
//			TraceLogger.trace(TraceLogger.LEVEL_FINEST, pClassNotFoundExcpt.toString());
	      TraceLogger.trace(TraceLogger.LEVEL_FINEST, "IS class '" + className + "' could not be found.");
      } // end of catch(ClassNotFoundException pClassNotFoundExcpt)
		catch(Exception pExcpt) {
			TraceLogger.traceException(pExcpt);
		} // end of catch(ClassNotFoundException classNotFoundExcpt)

      return(retClassCouldBeloaded);
	} // end of isClassAvailable()

} // end of class ISVersionChecker
