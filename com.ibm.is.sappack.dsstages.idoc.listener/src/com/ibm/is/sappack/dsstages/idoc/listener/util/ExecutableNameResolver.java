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
// Module Name : com.ibm.is.sappack.dsstages.idoc.listener.util
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.dsstages.idoc.listener.util;

/**
 * ExecutableNameResolver
 * 
 * Helper class to get the platform dependent names
 * of external executables such as uvsh.exe, dsjob.exe
 */
public class ExecutableNameResolver {
	
	/**
	 * getUVSHExecutableName
	 * 
	 * return the platform dependent name
	 * of the 'uvsh' executable.
	 * 'uvsh.exe' on windows and 'uvsh' on linux/unix
	 * platforms
	 * 
	 * @return
	 */
	public static String getUVSHExecutableName() {
		
		/* check if we are on windows or linux/unix */
		if (onWindows()) {
			/* windows */
			return "uvsh.exe";  //$NON-NLS-1$
		}
		/* linux/unix */
      return "uvsh";  //$NON-NLS-1$
	}
	
	/**
	 * getDSJobExecutableName
	 * 
	 * return the platform dependent name
	 * of the 'dsjob' executable.
	 * 'dsjob.exe' on windows and 'dsjob'
	 * on linux/unix platforms
	 * 
	 * @return
	 */
	public static String getDSJobExecutableName() {
		
		/* check if we are on windows or linux/unix */
		if (onWindows()) {
			/* windows */
			return "dsjob.exe";  //$NON-NLS-1$
		}
		/* linux/unix */
      return "dsjob";  //$NON-NLS-1$
	}
	
	/**
	 * onWindows
	 * 
	 * return true if are running
	 * on windows. Otherwise return
	 * false
	 * 
	 * @return
	 */
	private static boolean onWindows() {
		
		String osName = System.getProperty("os.name");  //$NON-NLS-1$
		
		if(osName.toLowerCase().indexOf("win") > -1) { //$NON-NLS-1$
			return true;
		}
		return false;
	}
	

	static String copyright() {
		return com.ibm.is.sappack.dsstages.idoc.listener.util.Copyright.IBM_COPYRIGHT_SHORT;
	}
}
