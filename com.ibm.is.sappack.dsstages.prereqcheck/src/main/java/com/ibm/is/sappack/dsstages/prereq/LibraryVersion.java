//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2011, 2014                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.is.sappack.dsstages.prereq
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.dsstages.prereq;


import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;


public class LibraryVersion implements Comparable<LibraryVersion> {

	static String copyright() {
		return com.ibm.is.sappack.dsstages.prereq.Copyright.IBM_COPYRIGHT_SHORT;
	}

	String fullVersion;

	public LibraryVersion(String version) {
		this.fullVersion = version;
		// call this just to trigger exceptions if the format is wrong
		getVersion();
	}

	public int[] getVersion() {
		StringTokenizer tok = new StringTokenizer(this.fullVersion, "."); //$NON-NLS-1$
		List<Integer> versionList = new ArrayList<Integer>();
		while (tok.hasMoreTokens()) {
			String s = tok.nextToken();
			versionList.add(Integer.parseInt(s));
		}
		int[] result = new int[versionList.size()];
		for (int i = 0; i < result.length; i++) {
			result[i] = versionList.get(i);
		}
		return result;
	}

	public int compareTo(LibraryVersion ver) {
		int[] thisVer = getVersion();
		int[] otherVer = ver.getVersion();
		for (int i = 0; i < Math.max(thisVer.length, otherVer.length); i++) {
			if (i >= thisVer.length) {
				if (i >= otherVer.length) {
					// should never happen
					return 0;
				} else {
					return -1;
				}
			}
			if (i >= otherVer.length) {
				return 1;
			}
			if (thisVer[i] < otherVer[i]) {
				return -1;
			}
			if (thisVer[i] > otherVer[i]) {
				return 1;
			}
		}
		return 0;
	}

	public boolean hasSameVersion(LibraryVersion ver2) {
		return this.compareTo(ver2) == 0;
	}

	public boolean hasAtLeastVersion(LibraryVersion ver) {
		return this.compareTo(ver) >= 0;
	}

	// checks that the first two version numbers are equal
	public boolean hasSamePointVersion(LibraryVersion ver) {
		int[] thisVer = getVersion();
		int[] otherVer = ver.getVersion();
		if (thisVer.length < 2 || otherVer.length < 2) {
			return false;
		}
	    if (thisVer[0] != otherVer[0]) {
	    	return false;
	    }
	    return thisVer[1] == otherVer[1];
	}
	
	public boolean hasSameMajorVersion(LibraryVersion ver) {
		int[] thisVer = getVersion();
		int[] otherVer = ver.getVersion();
		
		if (thisVer.length < 1 || otherVer.length < 1) {
			return false;
		}
	    return thisVer[0] == otherVer[0];
	}

	public String toString() {
		return this.fullVersion;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof LibraryVersion)) {
			return false;
		}
		return this.compareTo((LibraryVersion) obj) != 0;
	}

	@Override
	public int hashCode() {
		return this.fullVersion.hashCode();
	}

	public static LibraryVersion getSAPSDKDLLVersion(String fileName) {
		LibraryVersion ver = null;

		if (PrereqCheck.isWindows()) {
			ver = getSAPSDKDLLVersionWithWMICCommand(fileName);
			if (ver == null) {
				ver = getSAPSDKDLLVersionWithFileVerCommand(fileName);
			}
		}
		else {
			ver = getSAPSDKDLLVersionWithStringsCommand(fileName);
		}

		return ver;
	}


	private static LibraryVersion getSAPSDKDLLVersionWithFileVerCommand(String fileName) {
		try {
			if (PrereqCheck.isWindows()) {
				CommandLineExec cle = new CommandLineExec();
				CommandLineExec.ExecutionResult er = cle.runCommandLine(new String[] { "filever.exe", "-v", fileName }); //$NON-NLS-1$ //$NON-NLS-2$
				String stdout = er.stdoutput;
				String lineStart = "File Version:"; //$NON-NLS-1$
				int ix = stdout.indexOf(lineStart);
				int ix2 = stdout.indexOf("\n", ix + lineStart.length()); //$NON-NLS-1$
				String versionString = stdout.substring(ix + lineStart.length() + 1, ix2);
				versionString = versionString.trim();
				return new LibraryVersion(versionString);
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static LibraryVersion getSAPSDKDLLVersionWithStringsCommand(String command, String fileName) {
		try {
			CommandLineExec cle = new CommandLineExec();
			CommandLineExec.ExecutionResult er = cle.runCommandLine(command + " " + fileName); //$NON-NLS-1$
			String stdout = er.stdoutput;
			String lineStart = "#[%]SAPFileVersion:"; //$NON-NLS-1$
			int ix = stdout.indexOf(lineStart);
			if (ix == -1) {
				return null;
			}
			int ix2 = stdout.indexOf("\n", ix + lineStart.length()); //$NON-NLS-1$
			String versionString = stdout.substring(ix + lineStart.length() + 1, ix2);
			versionString = versionString.trim();
			StringTokenizer tok = new StringTokenizer(versionString, ", "); //$NON-NLS-1$
			StringBuffer ver = new StringBuffer();
			while (tok.hasMoreTokens()) {
				if (ver.length() != 0) {
					ver.append("."); //$NON-NLS-1$
				}
				ver.append(tok.nextToken());
			}

			LibraryVersion result = new LibraryVersion(ver.toString());
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}


	private static LibraryVersion getSAPSDKDLLVersionWithStringsCommand(String fileName) {
		// first try without -a for downward compatibility
		LibraryVersion result = getSAPSDKDLLVersionWithStringsCommand("strings", fileName); //$NON-NLS-1$
		if (result == null) {
			result = getSAPSDKDLLVersionWithStringsCommand("strings -a", fileName); //$NON-NLS-1$
		}
		return result;
	}

	public static LibraryVersion getSAPSDKDLLVersionWithWMICCommand(String fileName) {
		LibraryVersion dllVersion = null;

		if (PrereqCheck.isWindows()) {
			CommandLineExec                 cle;
			CommandLineExec.ExecutionResult er;
			try {
				fileName = fileName.replace("\\", "\\\\");

				cle = new CommandLineExec();
				er  = cle.runCommandLineNoInput("wmic.exe datafile where name=\"" + fileName + "\" get version");

				String stdout = er.stdoutput;
				String lineStart = "\n"; //$NON-NLS-1$
				int ix = stdout.indexOf(lineStart);
				String versionString = stdout.substring(ix + lineStart.length());
				versionString = versionString.replace(", ", "."); //$NON-NLS-1$ $NON-NLS-2$
				versionString = versionString.trim();

				dllVersion = new LibraryVersion(versionString);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}

		return(dllVersion);
	}


	@SuppressWarnings("nls")
	public static void main(String[] args) {
		LibraryVersion ver1 = new LibraryVersion("9.1.2.0");
		LibraryVersion ver2 = new LibraryVersion("9.1.2.0");
		int c = ver1.compareTo(ver2);

		System.out.println("COMP: " + c);

	}
}
