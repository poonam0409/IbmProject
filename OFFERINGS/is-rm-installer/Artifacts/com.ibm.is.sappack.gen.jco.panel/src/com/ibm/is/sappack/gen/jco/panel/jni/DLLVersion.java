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
// Module Name : com.ibm.is.sappack.gen.common.ui.preferences
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.jco.panel.jni;

import java.io.File;

/**
 * @author dsh
 *
 */
public class DLLVersion {
	
	private static final String MSVCM80_DIR = "C:\\WINDOWS\\WinSxS\\x86_Microsoft.VC80.CRT_1fc8b3b9a1e18e3b_8.0.50727.4053_x-ww_e6967989";
	private static final String MSVCR80_DIR = "C:\\WINDOWS\\WinSxS\\x86_Microsoft.VC80.CRT_1fc8b3b9a1e18e3b_8.0.50727.4053_x-ww_e6967989";
	public static final String MSVCP60_VERSION_NEEDLE = "7.0.3790.1830";
	public static final String MSVCM80_VERSION_NEEDLE = "8.0.50727.4053";
	public static final String MSVCR80_VERSION_NEEDLE = "8.0.50727.4053";
	
	static String copyright() {
		return com.ibm.is.sappack.gen.jco.panel.jni.Copyright.IBM_COPYRIGHT_SHORT;
	}
	
	static {  
		System.loadLibrary("DLLVersion"); //$NON-NLS-1$
	}
	
	/**
	 * Simply pull the version number from a DLL/Executable by calling GetFileVersionInfo.
	 * 
	 * Note: You will be responsible to provide a fully qualified path to the DLL/Executable if it's not located in the defult search path.
	 * 
	 * @param A DLL/Executable name (may include a fully qualified path portion)
	 */
	public static native String getVersion(String fileName);
	
	/**
	 * Simply pull the version number from a DLL/Executable by calling GetFileVersionInfo.
	 * 
	 * Note: This method allows to provide a qualified path to the directory containing the DLL/Executable
	 * 
	 * @param Directory path containing the DLL/Executable
	 * @param The name of the DLL/Executable
	 */
	public static native String getVersion(String path, String fileName);
	
	/**
	 * Tries to pull the version info from either a DLL or Executable to have it compared against the version information provided.
	 * 
	 * Note: You will be responsible to provide a fully qualified path to the DLL/Executable if it's not located in the defult search path.
	 * 
	 * @param Version information that should be used to do an equality comparison
	 * @param A DLL/Executable name (may include a fully qualified path portion)
	 */
	public static native boolean validateVersion(String version, String fileName);
	
	/**
	 * Tries to pull the version info from either a DLL or Executable to have it compared against the version information provided.
	 * 
	 * Note: This method allows to provide a qualified path to the directory containing the DLL/Executable
	 * 
	 * @param Version information that should be used to do an equality comparison
	 * @param Directory path containing the DLL/Executable
	 * @param The name of the DLL/Executable
	 */
	public static native boolean validateVersion(String version, String path, String fileName);

	/**
	 * 
	 */
	public DLLVersion() {
		// TODO Auto-generated constructor stub
	}
	
	public static String getMsvcm80Version() {
		return DLLVersion.getVersion(DLLVersion.MSVCM80_DIR + File.separator + "msvcm80.dll");
	}
	
	public static String getMsvcm80Version(String path) {
		return DLLVersion.getVersion(path, "msvcm80.dll");
	}
	
	public static String getMsvcr80Version() {
		return DLLVersion.getVersion(DLLVersion.MSVCR80_DIR + File.separator + "msvcr80.dll");
	}
	
	public static String getMsvcr80Version(String path) {
		return DLLVersion.getVersion(path, "msvcr80.dll");
	}
	
	public static String getMsvcp60Version() {
		return DLLVersion.getVersion("msvcp60.dll");
	}
	
	public static boolean validateMsvcm80Version() {
		return DLLVersion.validateVersion(DLLVersion.MSVCM80_VERSION_NEEDLE, DLLVersion.MSVCM80_DIR + File.separator + "msvcm80.dll");
	}
	
	public static boolean validateMsvcm80Version(String path) {
		return DLLVersion.validateVersion(DLLVersion.MSVCM80_VERSION_NEEDLE, path, "msvcm80.dll");
	}
	
	public static boolean validateMsvcr80Version() {
		return DLLVersion.validateVersion(DLLVersion.MSVCR80_VERSION_NEEDLE, DLLVersion.MSVCR80_DIR + File.separator + "msvcr80.dll");
	}
	
	public static boolean validateMsvcr80Version(String path) {
		return DLLVersion.validateVersion(DLLVersion.MSVCR80_VERSION_NEEDLE, path, "msvcr80.dll");
	}
	
	public static boolean validateMsvcp60Version() {
		return DLLVersion.validateVersion(DLLVersion.MSVCP60_VERSION_NEEDLE, "msvcp60.dll");
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length == 1) {
			System.out.println("Version returned from native method: " + DLLVersion.getVersion(args[0]));
		} else {
			System.err.println("No file name specified. Getting versions for known files...");
			System.out.println("msvcm80.dll: " + DLLVersion.getMsvcm80Version());
			System.out.println("msvcr80.dll: " + DLLVersion.getMsvcr80Version());
			System.out.println("msvcp60.dll: " + DLLVersion.getMsvcp60Version());
			
			//now with separate path values
			System.out.println("msvcm80.dll (with path provided):" + DLLVersion.getMsvcm80Version(DLLVersion.MSVCM80_DIR));
			System.out.println("msvcr80.dll (with path provided): " + DLLVersion.getMsvcr80Version(DLLVersion.MSVCR80_DIR));
			
			// now validate against equality inside native code
			System.out.println("msvcm80.dll versions are equal: " + DLLVersion.validateMsvcm80Version());
			System.out.println("msvcr80.dll versions are equal: " + DLLVersion.validateMsvcr80Version());
			System.out.println("msvcp60.dll versions are equal: " + DLLVersion.validateMsvcp60Version());
			
			// the same equality comparison but with providing a separate path
			System.out.println("msvcm80.dll versions are equal (with path provided):" + DLLVersion.validateMsvcm80Version(DLLVersion.MSVCM80_DIR));
			System.out.println("msvcr80.dll versions are equal (with path provided): " + DLLVersion.validateMsvcr80Version(DLLVersion.MSVCR80_DIR));
		}
	}

}
