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


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintStream;
import java.io.StringReader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ibm.is.sappack.dsstages.prereq.CheckMessage.TYPE;


public class PrereqCheck {

	static String copyright() {
		return com.ibm.is.sappack.dsstages.prereq.Copyright.IBM_COPYRIGHT_SHORT;
	}

	final static String NL = System.getProperty("line.separator"); //$NON-NLS-1$

	static String ISALiteSeparator = ","; //$NON-NLS-1$
	public final static String ISALiteNL = NL;

	public static final String MINIMAL_CLIENT_NW_RFC_SDK_VERSION = "7200"; //$NON-NLS-1$
	public static final String MINIMAL_ENGINE_NW_RFC_SDK_VERSION = "7200"; //$NON-NLS-1$

	// minimal version for patchlevel 2
	public static final String MINIMAL_ENGINE_RFC_SDK_VERSION = "7200.1.91"; //$NON-NLS-1$

	public static final String MINIMAL_CLIENT_JCO_VERSION = "3"; //$NON-NLS-1$
	public static final String MINIMAL_ENGINE_JCO_VERSION = "3"; //$NON-NLS-1$


	static final String TAB = "==> "; //$NON-NLS-1$

	static String[][] clientPatches = new String[][] {
	//
//	{ "9.1.2.0", "JR49202" }, //$NON-NLS-1$//$NON-NLS-2$ 
	};

	static String[][] enginePatches = new String[][] {
	//
//	{ "8.5.0.1", "JR39331" }, //$NON-NLS-1$ //$NON-NLS-2$ 
	};

	static String minimalIISVersion = "11.3.0.0"; //$NON-NLS-1$

	static Map<String, Collection<String>> getPatches(String[][] patches) {
		Map<String, Collection<String>> result = new HashMap<String, Collection<String>>();
		for (int i = 0; i < patches.length; i++) {
			String version = patches[i][0];
			List<String> patchList = new ArrayList<String>();
			for (int j = 1; j < patches[i].length; j++) {
				patchList.add(patches[i][j]);
			}
			result.put(version, patchList);
		}
		return result;
	}

	private String errorMessage;
	private String iishome;
	private File iisHomeDir = null;
	private String titleString;
	private String outputFile;
	private String tier;
	String javaExecutable;
	private PrintStream out = System.out;

	public static boolean isWindows() {
		return System.getProperty("os.name", "unknown").toLowerCase().startsWith("windows"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	public static boolean isAIX() {
		return System.getProperty("os.name", "unknown").equalsIgnoreCase("aix"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
	
	public static boolean isHPIA64() {
		return System.getProperty("os.name", "unknown").toLowerCase().startsWith("hp"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	private String EMPTY_RECOMMENDATION = ""; // PrereqCheckMessages.getString("PrereqCheck.72"); //$NON-NLS-1$

	void trace(String s) {
		System.err.println("TRACE: " + s); //$NON-NLS-1$
	}

	void trace(Throwable t) {
		t.printStackTrace();
	}

	/**
	 * @return the errorMessage
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * @param errorMessage the errorMessage to set
	 */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public boolean hasErrorMessage() {
		return (null != this.errorMessage && this.errorMessage.length() > 0);
	}

	public boolean tierIsValid() {
		return (null != this.getTier() && this.getTier().length() > 0 && (PrereqCheck.TIER_CLIENT.equals(this.getTier()) || PrereqCheck.TIER_ENGINE.equals(this.getTier())));
	}

	/**
	 * @return the tier
	 */
	public String getTier() {
		return tier;
	}

	/**
	 * @param tier the tier to set
	 */
	public void setTier(String tier) {
		this.tier = tier;
	}

	/**
	 * @return the outputFile
	 */
	public String getOutputFile() {
		return outputFile;
	}

	/**
	 * @param outputFile the outputFile to set
	 */
	public void setOutputFile(String outputFile) {
		this.outputFile = outputFile;
	}

	/**
	 * @return the titleString
	 */
	public String getTitleString() {
		return titleString;
	}

	/**
	 * @param titleString the titleString to set
	 */
	public void setTitleString(String titleString) {
		this.titleString = titleString;
	}

	/**
	 * @return the iishome
	 */
	public String getIishome() {
		return iishome;
	}

	/**
	 * @param iishome the iishome to set
	 */
	public void setIishome(String iishome) {
		this.iishome = iishome;
	}

	/**
	 * @return the iisHomeDir
	 */
	public File getIisHomeDir() {
		return iisHomeDir;
	}

	/**
	 * @param iisHomeDir the iisHomeDir to set
	 */
	public void setIisHomeDir(File iisHomeDir) {
		this.iisHomeDir = iisHomeDir;
	}

	List<CheckMessage> messages;

	public boolean hasMessage() {
		return this.messages.size() >= 1;
	}

	public void startSection(String description) {
		print(description);
	}

	private void print(String s) {
		if (out != null) {
			out.println(s);
		}
	}

	private void addMessage(CheckMessage msg) {
		messages.add(msg);
		String s = null;
		String msgCode = null;
		switch (msg.getType()) {
		case ERROR:
			msgCode = "PrereqCheck.0"; //$NON-NLS-1$
			break;
		case WARNING:
			msgCode = "PrereqCheck.23"; //$NON-NLS-1$
			break;
		case PASSED:
			msgCode = "PrereqCheck.24"; //$NON-NLS-1$
		default:
			;
		}
		s = MessageFormat.format(PrereqCheckMessages.getString(msgCode), msg.getResultDescription());
		print(s);
	}

	public void addErrorMessage(String testDesc, String result, String recommendation) {
		CheckMessage msg = new CheckMessage(CheckMessage.TYPE.ERROR, testDesc, result, recommendation);
		addMessage(msg);
	}

	public void addInfoMessage(String s) {
		print(s);
	}

	public void addWarningMessage(String testDesc, String result, String recommendation) {
		CheckMessage msg = new CheckMessage(CheckMessage.TYPE.WARNING, testDesc, result, recommendation);
		addMessage(msg);
	}

	public void addSuccessMessage(String testDesc, String result, String recommendation) {
		CheckMessage msg = new CheckMessage(CheckMessage.TYPE.PASSED, testDesc, result, recommendation);
		addMessage(msg);
	}

	public String getIISHome() {
		return this.iishome;
	}

	List<LibraryProperties> getClassicRFCLibs() {
		List<LibraryProperties> libs = new ArrayList<LibraryProperties>();
		if (isWindows()) {
			libs.add(new LibraryProperties("librfc32.dll", true)); //$NON-NLS-1$
		} else {
			if (isAIX()) {	
				libs.add(new LibraryProperties("librfccm.o", true)); //$NON-NLS-1$
			}
			libs.add(new LibraryProperties("librfc.a", true, false)); //$NON-NLS-1$
			libs.add(new LibraryProperties("librfccm.so", true)); //$NON-NLS-1$
			libs.add(new LibraryProperties("libsapu16_mt.so", false)); //$NON-NLS-1$
		}
		return libs;
	}

	List<LibraryProperties> getNWRFCLibs() {
		List<LibraryProperties> libs = new ArrayList<LibraryProperties>();
		if (isWindows()) {
			libs.add(new LibraryProperties("sapnwrfc.dll", true)); //$NON-NLS-1$
			libs.add(new LibraryProperties("libsapucum.dll", false)); //$NON-NLS-1$
			libs.add(new LibraryProperties("libicudecnumber.dll", false)); //$NON-NLS-1$
		} else {
			libs.add(new LibraryProperties("libsapnwrfc.so", true)); //$NON-NLS-1$
			libs.add(new LibraryProperties("libsapucum.so", false)); //$NON-NLS-1$
			libs.add(new LibraryProperties("libicudecnumber.so", false)); //$NON-NLS-1$
		}
		return libs;
	}

	List<LibraryProperties> getICU34Libs() {
		List<LibraryProperties> libs = new ArrayList<LibraryProperties>();
		if (isWindows()) {
			libs.add(new LibraryProperties("icuin34.dll", false)); //$NON-NLS-1$
			libs.add(new LibraryProperties("icudt34.dll", false)); //$NON-NLS-1$
			libs.add(new LibraryProperties("icuuc34.dll", false)); //$NON-NLS-1$
		} else if (isAIX()){
			libs.add(new LibraryProperties("libicuuc34.a", false, false)); //$NON-NLS-1$
			libs.add(new LibraryProperties("libicudata34.a", false, false)); //$NON-NLS-1$
			libs.add(new LibraryProperties("libicui18n34.a", false, false)); //$NON-NLS-1$
		} else if (isHPIA64()) {
			libs.add(new LibraryProperties("libicuuc.sl.34", false)); //$NON-NLS-1$
			libs.add(new LibraryProperties("libicudata.sl.34", false)); //$NON-NLS-1$
			libs.add(new LibraryProperties("libicui18n.sl.34", false)); //$NON-NLS-1$
		} else {
			libs.add(new LibraryProperties("libicuuc.so.34", false)); //$NON-NLS-1$
			libs.add(new LibraryProperties("libicudata.so.34", false)); //$NON-NLS-1$
			libs.add(new LibraryProperties("libicui18n.so.34", false)); //$NON-NLS-1$
		}
		return libs;

	}

	static class LibraryProperties {
		String libraryName;
		boolean treatMissingVersionAsError = true;
		boolean canBeLoaded = true;

		public LibraryProperties(String libraryName, boolean treatMissingVersionAsError, boolean canBeLoaded) {
			super();
			this.libraryName = libraryName;
			this.treatMissingVersionAsError = treatMissingVersionAsError;
			this.canBeLoaded = canBeLoaded;
		}

		public LibraryProperties(String libraryName, boolean treatMissingVersionAsError) {
			this(libraryName, treatMissingVersionAsError, true);
		}
		
		public boolean canBeLoaded() {
			return this.canBeLoaded;
		}

	}

	private String getSAPJCO3Jar() {
		return "sapjco3.jar"; //$NON-NLS-1$
	}

	private LibraryProperties getSAPJCO3DLL() {
		if (isWindows()) {
			return new LibraryProperties("sapjco3.dll", true); //$NON-NLS-1$
		}
		return new LibraryProperties("libsapjco3.so", true); //$NON-NLS-1$
	}

	public PrereqCheck(String iishome) {
		this.titleString = PrereqCheckMessages.getString("PrereqCheck.56") + NL //$NON-NLS-1$
				+ PrereqCheckMessages.getString("PrereqCheck.57"); //$NON-NLS-1$
		this.iishome = iishome;
		this.messages = new ArrayList<CheckMessage>();
	}

	public PrereqCheck() {
		this(null);
	}

	private boolean checkFileExists(String file) {
		String testDescription = MessageFormat.format(PrereqCheckMessages.getString("PrereqCheck.73"), file); //$NON-NLS-1$
		String recommendation = PrereqCheckMessages.getString("PrereqCheck.74"); //$NON-NLS-1$
		File f = new File(file);
		boolean success = f.exists();
		if (!success) {
			String fileDoesNotExistMsg = PrereqCheckMessages.getString("PrereqCheck.1"); //$NON-NLS-1$
			fileDoesNotExistMsg = MessageFormat.format(fileDoesNotExistMsg, file);
			addErrorMessage(testDescription, fileDoesNotExistMsg, recommendation);
		} else {
			String fileExistsMsg = PrereqCheckMessages.getString("PrereqCheck.2"); //$NON-NLS-1$
			fileExistsMsg = MessageFormat.format(fileExistsMsg, file);
			addSuccessMessage(testDescription, fileExistsMsg, EMPTY_RECOMMENDATION);
		}
		return success;
	}

	private boolean checkDLLVersion(String file, LibraryVersion previousVersion, String previousFile,
	                                LibraryVersion minimalVersion, boolean treatMissingVersionAsError,
	                                LibraryVersion[] outFileVersion) {
		LibraryVersion version = LibraryVersion.getSAPSDKDLLVersion(file);
		boolean success = true;
		String testDescr = MessageFormat.format(PrereqCheckMessages.getString("PrereqCheck.75"), file); //$NON-NLS-1$
		String recommendation = PrereqCheckMessages.getString("PrereqCheck.76"); //$NON-NLS-1$

		if (version == null) {
			String msg = PrereqCheckMessages.getString("PrereqCheck.25"); //$NON-NLS-1$
			msg = MessageFormat.format(msg, file);
			if (treatMissingVersionAsError) {
				addErrorMessage(testDescr, msg, recommendation);
			} else {
				// osuhre, 154310: don't add message if library version could not be retrieved
			//	addSuccessMessage(testDescr, msg, EMPTY_RECOMMENDATION);
			}
		} else {
			String msg = PrereqCheckMessages.getString("PrereqCheck.26"); //$NON-NLS-1$
			msg = MessageFormat.format(msg, new Object[] { file, version.toString() });
			addSuccessMessage(PrereqCheckMessages.getString("PrereqCheck.77"), msg, EMPTY_RECOMMENDATION); //$NON-NLS-1$

			if (minimalVersion != null) {
				if (!version.hasAtLeastVersion(minimalVersion)) {
					msg = PrereqCheckMessages.getString("PrereqCheck.27"); //$NON-NLS-1$
					msg = MessageFormat.format(msg, new Object[] { file, minimalVersion.toString(), version.toString() });
					addErrorMessage(PrereqCheckMessages.getString("PrereqCheck.78"), msg, PrereqCheckMessages.getString("PrereqCheck.79")); //$NON-NLS-1$ //$NON-NLS-2$
					success = false;
				}
			}

			if (previousVersion != null) {
				String incompversionsRecommendation = PrereqCheckMessages.getString("PrereqCheck.80"); //$NON-NLS-1$
				// osuhre, 154310: check only major versions for DLL compatibility
				if (!version.hasSameMajorVersion(previousVersion)) {
					msg = PrereqCheckMessages.getString("PrereqCheck.28"); //$NON-NLS-1$
					msg = MessageFormat.format(msg, new Object[] { file, version.toString(), previousFile, previousVersion.toString() });
					String testdesc = PrereqCheckMessages.getString("PrereqCheck.81"); //$NON-NLS-1$
					if (version.hasSamePointVersion(previousVersion)) {
						addWarningMessage(testdesc, msg, incompversionsRecommendation);
					} else {
						addErrorMessage(testdesc, msg, incompversionsRecommendation);
						success = false;
					}
				}
			}
		}
		if (outFileVersion != null && outFileVersion.length > 0) {
			outFileVersion[0] = version;
		}
		return success;
	}


	private String getDLLPathFromSystemPath(String dllName) {
		String  fullSystemPath = System.getProperty("java.library.path"); //$NON-NLS-1$
		String  checkDLLPath   = null;
		String  retNewDLLPath  = null;
		int     arrIdx;
		
		// check if the DLL could be loaded from the SYSTEM path
		String pathPartArr[] = fullSystemPath.split(File.pathSeparator);
		arrIdx               = 0;
		while(arrIdx < pathPartArr.length && retNewDLLPath == null) {
			checkDLLPath = pathPartArr[arrIdx] + File.separator + dllName;
			File dllFileInst = new File(checkDLLPath);
			if (dllFileInst.canRead()) {
				retNewDLLPath = pathPartArr[arrIdx];
			}
			arrIdx ++;
		} // end of while(arrIdx < pathPartArr.length && retNewDLLPath == nuLL)

		return(retNewDLLPath);
	}

	private String getJavaExecutable() {
		String javaExe = this.javaExecutable;
		if (javaExe == null) {
			javaExe = System.getenv("JAVA_EXE"); //$NON-NLS-1$

			if (javaExe == null) {
				javaExe = "java"; //$NON-NLS-1$
			}
		}
		return javaExe;
	}

	private LibraryVersion checkJCoJARVersion(String jarFile, LibraryVersion minVersion) {
		String testDesc = PrereqCheckMessages.getString("PrereqCheck.86"); //$NON-NLS-1$
		try {
			LibraryVersion version = null;
			String javaExe = getJavaExecutable();

			String cmdLine = javaExe + " -jar " + jarFile + " -stdout"; //$NON-NLS-1$ //$NON-NLS-2$ 
			CommandLineExec cle = new CommandLineExec();
			CommandLineExec.ExecutionResult result = cle.runCommandLine(cmdLine);
			if (result.returnCode == 0) { //  && (result.stderr.length() == 0)) {
				String s = result.stdoutput;
				LineNumberReader reader = new LineNumberReader(new StringReader(s));
				String line = null;
				while ((line = reader.readLine()) != null) {
					line = line.trim();
					// this is the entry we are looking for in the JCo about output
					String entry = "JCo API:"; //$NON-NLS-1$
					int ix = line.indexOf(entry);
					if (ix > -1) {
						String versionString = line.substring(ix + entry.length()).trim();
						int ixSpace = versionString.indexOf(' ');
						if (ixSpace == -1) {
							ixSpace = versionString.length();
						}
						versionString = versionString.substring(0, ixSpace);
						version = new LibraryVersion(versionString);
						String msg = PrereqCheckMessages.getString("PrereqCheck.29"); //$NON-NLS-1$
						msg = MessageFormat.format(msg, new Object[] { jarFile, version.toString() });
						addSuccessMessage(testDesc, msg, EMPTY_RECOMMENDATION);
						break;
					}
				}
			} else {
				addErrorMessage(testDesc, PrereqCheckMessages.getString("PrereqCheck.94"), PrereqCheckMessages.getString("PrereqCheck.95")); //$NON-NLS-1$ //$NON-NLS-2$
			}
			if (version != null) {
				if (minVersion != null) {
					if (!version.hasAtLeastVersion(minVersion)) {
						String msg = PrereqCheckMessages.getString("PrereqCheck.31"); //$NON-NLS-1$
						msg = MessageFormat.format(msg, new Object[] { jarFile, minVersion.toString(), version.toString() });
						addErrorMessage(testDesc, msg, MessageFormat.format(PrereqCheckMessages.getString("PrereqCheck.88"), minVersion)); //$NON-NLS-1$
					}
				}

				String classCouldBeLoaded = PrereqCheckMessages.getString("PrereqCheck.8"); //$NON-NLS-1$
				classCouldBeLoaded = MessageFormat.format(classCouldBeLoaded, jarFile);
				addSuccessMessage(testDesc, classCouldBeLoaded, EMPTY_RECOMMENDATION);
			}
			return version;
		} catch (Exception exc) {
			exc.printStackTrace();
			addWarningMessage(testDesc, PrereqCheckMessages.getString("PrereqCheck.87"), EMPTY_RECOMMENDATION); //$NON-NLS-1$
			return null;
		}
	}

	private boolean checkDLLList(String dir, List<LibraryProperties> libs, LibraryVersion minimalVersion, List<LibraryVersion> outDLLVersions) {
		if (outDLLVersions == null) {
			// if no list provided, create one for debugging
			outDLLVersions = new ArrayList<LibraryVersion>();
		}

		outDLLVersions.clear();
		boolean success = true;
		LibraryVersion previousVersion = null;
		String previousFile = null;
		for (LibraryProperties lib : libs) {
			String msg = null;
			if (minimalVersion == null) {
				msg = PrereqCheckMessages.getString("PrereqCheck.32"); //$NON-NLS-1$
				msg = MessageFormat.format(msg, lib.libraryName);
			}
			else {
				msg = PrereqCheckMessages.getString("PrereqCheck.33"); //$NON-NLS-1$
				msg = MessageFormat.format(msg, new Object[] { lib.libraryName, minimalVersion.toString() });
			}

			this.addInfoMessage(msg);

			String fullDLLPath = checkDLLExistAndLoad(dir, lib.libraryName, lib.canBeLoaded);
			if (fullDLLPath == null) {
				success = false;
			}
			else {
				if (lib.canBeLoaded()) {
					LibraryVersion[] ver = new LibraryVersion[1];
					if (!checkDLLVersion(fullDLLPath, previousVersion, previousFile, minimalVersion, lib.treatMissingVersionAsError, ver)) {
						success = false;
					}
					if (ver[0] != null && previousVersion == null) {
						previousVersion = ver[0];
						previousFile = lib.libraryName;
					}
					outDLLVersions.add(ver[0]); // add version even if it is null
				}
				else {
					outDLLVersions.add(null);
				}
			}
		}

		return success;
	}


	/**
	 * This method performs the following checks ...
	 * <ol>
	 * <li>check if DLL exists in the specified client/engine IS folder</li>
	 * <li>if DLL does not exist check if it exists in the system library path</li>
	 * </ol>
	 * For all conditions an appropriate messages is stored. If the DLL exists and can be loaded 
	 * the path to DLL is returned.
	 * 
	 * @param dllFolder  IS folder path
	 * @param dllName    DLL name
	 * 
	 * @return full DLL folder path
	 */
	private String checkDLLExistAndLoad(String dllFolder, String dllName, boolean loadDLL) {
		File    dllFileInst;
		String  fullDLLPath;
		String  newDLLPath;
		String  testDescription;
		String  recommendation;
		String  dllCouldBeLoadedMsg;
		String  dllCouldNotBeLoadedMsg;
		boolean isDLLLoaded;
		boolean isDLLExist;

		fullDLLPath     = dllFolder + File.separator + dllName;
		testDescription = MessageFormat.format(PrereqCheckMessages.getString("PrereqCheck.73"), fullDLLPath); //$NON-NLS-1$
		recommendation  = PrereqCheckMessages.getString("PrereqCheck.74"); //$NON-NLS-1$

		// 1st check if DLL exist in passed IS folder
		isDLLLoaded = false;
		dllFileInst = new File(fullDLLPath);

		isDLLExist = dllFileInst.exists(); 
		if (isDLLExist) {
			String fileExistsMsg = PrereqCheckMessages.getString("PrereqCheck.2"); //$NON-NLS-1$
			fileExistsMsg = MessageFormat.format(fileExistsMsg, fullDLLPath);
			addSuccessMessage(testDescription, fileExistsMsg, EMPTY_RECOMMENDATION);
		}
		else {
			// DLL could not be found ==> check if DLL exists in SYSTEM LIBRARY PATH
			newDLLPath = getDLLPathFromSystemPath(dllName);
			
			if (newDLLPath == null) {
				// DLL does not exist neither in the IS folder path nor in the system path 
				String fileDoesNotExistMsg = PrereqCheckMessages.getString("PrereqCheck.1"); //$NON-NLS-1$
				fileDoesNotExistMsg = MessageFormat.format(fileDoesNotExistMsg, fullDLLPath);
				addErrorMessage(testDescription, fileDoesNotExistMsg, recommendation);

				fullDLLPath = null;
			}
			else {
				fullDLLPath = newDLLPath + File.separator + dllName;
				
				dllCouldBeLoadedMsg = PrereqCheckMessages.getString("PrereqCheck.5"); //$NON-NLS-1$
				testDescription     = MessageFormat.format(PrereqCheckMessages.getString("PrereqCheck.82"), new Object[] { dllName, dllFolder }); //$NON-NLS-1$

				dllCouldBeLoadedMsg = MessageFormat.format(dllCouldBeLoadedMsg, new Object[] { dllName, newDLLPath });
				addWarningMessage(testDescription, dllCouldBeLoadedMsg, PrereqCheckMessages.getString("PrereqCheck.83")); //$NON-NLS-1$
				isDLLExist = true;
			}
		}

		// if DLL exists ==> check if DLL could be loaded
		isDLLLoaded = false;
		if (isDLLExist && loadDLL) {
			String loadErrMsg = ""; //$NON-NLS-1$

			testDescription = MessageFormat.format(PrereqCheckMessages.getString("PrereqCheck.84"), fullDLLPath); //$NON-NLS-1$
			try {
//				Runtime.getRuntime().load(fullDLLPath);
				System.load(fullDLLPath);
				isDLLLoaded = true;
			}
			catch (Throwable t) {
				trace(t);
				loadErrMsg = t.getMessage();
			}

			if (isDLLLoaded) {
				dllCouldBeLoadedMsg = PrereqCheckMessages.getString("PrereqCheck.6"); //$NON-NLS-1$
				dllCouldBeLoadedMsg = MessageFormat.format(dllCouldBeLoadedMsg, fullDLLPath);
				addSuccessMessage(testDescription, dllCouldBeLoadedMsg, EMPTY_RECOMMENDATION);
			}
			else {
				dllCouldNotBeLoadedMsg = PrereqCheckMessages.getString("PrereqCheck.3"); //$NON-NLS-1$
				dllCouldNotBeLoadedMsg = MessageFormat.format(dllCouldNotBeLoadedMsg, fullDLLPath, loadErrMsg);
				addErrorMessage(testDescription, dllCouldNotBeLoadedMsg, PrereqCheckMessages.getString("PrereqCheck.85")); //$NON-NLS-1$
				fullDLLPath = null;
			}
		}

		return(fullDLLPath);
	}
	
	
	private boolean checkNWRFCSDK(String dir, String minVersion) {
		List<LibraryProperties> nwrfcLibs = this.getNWRFCLibs();

		List<LibraryVersion> nwrfcVersions = new ArrayList<LibraryVersion>();
		LibraryVersion minVer = new LibraryVersion(minVersion);

		boolean b = checkDLLList(dir, nwrfcLibs, minVer, nwrfcVersions);
		if (b) {
			LibraryVersion nwrfcsdkVersion = null;
			for (LibraryVersion v : nwrfcVersions) {
				if (v != null) {
					nwrfcsdkVersion = v;
					break;
				}
			}
			if (nwrfcsdkVersion != null) {
				// check ICU3.4 libs only if NW RFC SDK version is 7200
				if (nwrfcsdkVersion.hasSameMajorVersion(new LibraryVersion("7200"))) { //$NON-NLS-1$
					List<LibraryProperties> icu34Libs = this.getICU34Libs();
					b = checkDLLList(dir, icu34Libs, null, null);
				}
			}
		}

		return b;
	}

	
	private boolean checkJCO3(String dir, String minimalVersion) {
		boolean success = true;
		
		String sapjco3Dll = checkDLLExistAndLoad(dir, getSAPJCO3DLL().libraryName, true);
		if (sapjco3Dll == null) {
			success = false; 
		}
		else {
			if (!checkDLLVersion(sapjco3Dll, null, null, null, true, null)) {
				success = false;
			}
		}

		String sapjco3Jar = dir + File.separator + getSAPJCO3Jar();
		if (!checkFileExists(sapjco3Jar)) {
			success = false;
		}
		if (success) {
			LibraryVersion minJCoJarVersion = new LibraryVersion(minimalVersion);
			String msg = PrereqCheckMessages.getString("PrereqCheck.34"); //$NON-NLS-1$
			msg = MessageFormat.format(msg, new Object[] { getSAPJCO3Jar(), minJCoJarVersion.toString() });
			addInfoMessage(msg);
			if (checkJCoJARVersion(sapjco3Jar, minJCoJarVersion) == null) {
				success = false;
			}
		}
		return success;
	}

	private boolean checkClassicRFCLibs(String dir, String minimalVersion) {
		boolean success = true;
		List<LibraryProperties> classicRFCLibs = this.getClassicRFCLibs();
		LibraryVersion minVersion = new LibraryVersion(minimalVersion);
		success = checkDLLList(dir, classicRFCLibs, minVersion, null);
		return success;
	}


	public boolean checkClient() {
		boolean success = true;
		startSection(PrereqCheckMessages.getString("PrereqCheck.35")); //$NON-NLS-1$
		String clientsClassicDir = iishome + File.separator + "Clients" + File.separator + "Classic"; //$NON-NLS-1$ //$NON-NLS-2$

		if (!checkNWRFCSDK(clientsClassicDir, MINIMAL_CLIENT_NW_RFC_SDK_VERSION)) {
			success = false;
			addInfoMessage(TAB + PrereqCheckMessages.getString("PrereqCheck.63")); //$NON-NLS-1$
		}
		else {
			addInfoMessage(TAB + PrereqCheckMessages.getString("PrereqCheck.64")); //$NON-NLS-1$
		}

		String asbNodeLibJavaDir = iishome + File.separator + "ASBNode" + File.separator + "lib" + File.separator + "java"; //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
		startSection(PrereqCheckMessages.getString("PrereqCheck.36")); //$NON-NLS-1$
		if (!checkJCO3(asbNodeLibJavaDir, MINIMAL_CLIENT_JCO_VERSION)) {
			success = false;
			addInfoMessage(TAB + PrereqCheckMessages.getString("PrereqCheck.65")); //$NON-NLS-1$
		}
		else {
			addInfoMessage(TAB + PrereqCheckMessages.getString("PrereqCheck.66")); //$NON-NLS-1$
		}

		startSection(PrereqCheckMessages.getString("PrereqCheck.37")); //$NON-NLS-1$
		if (!checkIISPatches(getPatches(clientPatches))) {
			success = false;
			addInfoMessage(TAB + PrereqCheckMessages.getString("PrereqCheck.67")); //$NON-NLS-1$
		}
		else {
			addInfoMessage(TAB + PrereqCheckMessages.getString("PrereqCheck.68")); //$NON-NLS-1$
		}

		return success;
	}


	public boolean checkEngine() {
		boolean success = true;
		String dsComponentsBinDir = iishome + File.separator + "Server" + File.separator + "DSComponents" + File.separator + "bin"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		startSection(PrereqCheckMessages.getString("PrereqCheck.38")); //$NON-NLS-1$
		if (!checkNWRFCSDK(dsComponentsBinDir, MINIMAL_ENGINE_NW_RFC_SDK_VERSION)) {
			success = false;
			addInfoMessage(TAB + PrereqCheckMessages.getString("PrereqCheck.69")); //$NON-NLS-1$
		} else {
			addInfoMessage(TAB + PrereqCheckMessages.getString("PrereqCheck.70")); //$NON-NLS-1$
		}
		startSection(PrereqCheckMessages.getString("PrereqCheck.39")); //$NON-NLS-1$

		if (!checkClassicRFCLibs(dsComponentsBinDir, MINIMAL_ENGINE_RFC_SDK_VERSION)) {
			success = false;
			addInfoMessage(TAB + PrereqCheckMessages.getString("PrereqCheck.40")); //$NON-NLS-1$
		} else {
			addInfoMessage(TAB + PrereqCheckMessages.getString("PrereqCheck.41")); //$NON-NLS-1$
		}

		startSection(PrereqCheckMessages.getString("PrereqCheck.42")); //$NON-NLS-1$
		if (!checkJCO3(dsComponentsBinDir, MINIMAL_ENGINE_JCO_VERSION)) {
			success = false;
			addInfoMessage(TAB + PrereqCheckMessages.getString("PrereqCheck.43")); //$NON-NLS-1$
		} else {
			addInfoMessage(TAB + PrereqCheckMessages.getString("PrereqCheck.44")); //$NON-NLS-1$
		}

		startSection(PrereqCheckMessages.getString("PrereqCheck.45")); //$NON-NLS-1$
		if (!checkIISPatches(getPatches(PrereqCheck.enginePatches))) {
			success = false;
			addInfoMessage(TAB + PrereqCheckMessages.getString("PrereqCheck.46")); //$NON-NLS-1$
		} else {
			addInfoMessage(TAB + PrereqCheckMessages.getString("PrereqCheck.47")); //$NON-NLS-1$
		}

		return success;
	}


	private boolean isPatchInstalled(List<String> patchIDs, String patchName) {
		for (String s : patchIDs) {
			if (s.contains(patchName)) {
				return true;
			}
		}
		return false;
	}


	private Document getVersionXML() {
		try {
			File f = new File(this.iishome + File.separator + "Version.xml"); //$NON-NLS-1$
			if (!f.exists()) {
				/*
				String msg = PrereqCheckMessages.getString("PrereqCheck.49"); //$NON-NLS-1$
				msg = MessageFormat.format(msg, f.getAbsolutePath());
				addErrorMessage(PrereqCheckMessages.getString("PrereqCheck.89"), msg, PrereqCheckMessages.getString("PrereqCheck.90")); //$NON-NLS-1$ //$NON-NLS-2$
				*/
				return null;
			}

			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(f);
			return doc;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}


	private String getVersion(Document doc) {
		try {
			XPathExpression versionXPath = XPathFactory.newInstance().newXPath().compile("/LocalInstallRegistry/Products/Product[@productId=\"datastage\"]/@version"); //$NON-NLS-1$
			String version = (String) versionXPath.evaluate(doc, XPathConstants.STRING);
			trace("Found Version string with string 'datastage': " + version); //$NON-NLS-1$
			// if "datastage" attribute is not set, try "DataStageCommon"
			if (version == null || version.length() == 0) {
				versionXPath = XPathFactory.newInstance().newXPath().compile("/LocalInstallRegistry/Products/Product[@productId=\"DataStageCommon\"]/@version"); //$NON-NLS-1$
				version = (String) versionXPath.evaluate(doc, XPathConstants.STRING);
				trace("Found Version string with string 'DataStageCommon': " + version); //$NON-NLS-1$
				if (version == null || version.length() == 0) {
					versionXPath = XPathFactory.newInstance().newXPath().compile("/LocalInstallRegistry/InstallType/@currentVersion"); //$NON-NLS-1$
					version = (String) versionXPath.evaluate(doc, XPathConstants.STRING);
					trace("Found Version string under 'InstallType': " + version); //$NON-NLS-1$
					if (version == null || version.length() == 0) {
						return null;
					}
				}
			}
			return version;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}


	private List<String> getPatchIDs(Document doc) {
		try {
			XPathExpression patchIDsXP = XPathFactory.newInstance().newXPath().compile("/LocalInstallRegistry/History/HistoricalEvent[@installType=\"PATCH\"]"); //$NON-NLS-1$
			List<String> result = new ArrayList<String>();
			NodeList ns = (NodeList) patchIDsXP.evaluate(doc, XPathConstants.NODESET);
			for (int i = 0; i < ns.getLength(); i++) {
				Node n = ns.item(i);
				Element el = (Element) n;
				String id = el.getAttribute("installerId"); //$NON-NLS-1$
				result.add(id);
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}


	private boolean checkIISPatches(Map<String, Collection<String>> prereqPatches) {
		String testDesc = PrereqCheckMessages.getString("PrereqCheck.91"); //$NON-NLS-1$
		String genericRecommendation = PrereqCheckMessages.getString("PrereqCheck.92"); //$NON-NLS-1$
		Document doc = this.getVersionXML();
		if (doc == null) {
			//	String msg = PrereqCheckMessages.getString("PrereqCheck.49"); //$NON-NLS-1$
			//	msg = MessageFormat.format(msg, f.getAbsolutePath());
			//			addErrorMessage("Checking Information Server version", msg, "Check that you are running on a correctly installed Information Server.");

			String msg = PrereqCheckMessages.getString("PrereqCheck.50"); //$NON-NLS-1$
			addErrorMessage(testDesc, msg, genericRecommendation);
			return false;
		}
		String versionString = this.getVersion(doc);
		if (versionString != null) {
			String msg = PrereqCheckMessages.getString("PrereqCheck.51"); //$NON-NLS-1$
			msg = MessageFormat.format(msg, versionString);
			addSuccessMessage(testDesc, msg, EMPTY_RECOMMENDATION);
		} else {
			addWarningMessage(testDesc, PrereqCheckMessages.getString("PrereqCheck.52"), genericRecommendation); //$NON-NLS-1$
			return false;
		}

		LibraryVersion version = new LibraryVersion(versionString);

		if (!version.hasAtLeastVersion(new LibraryVersion(minimalIISVersion))) {
			String msg = PrereqCheckMessages.getString("PrereqCheck.71"); //$NON-NLS-1$
			msg = MessageFormat.format(msg, minimalIISVersion);
			addErrorMessage(testDesc, msg, PrereqCheckMessages.getString("PrereqCheck.48")); //$NON-NLS-1$
			return false;
		}

		Collection<String> patchesToBeChecked = null;
		for (Map.Entry<String, Collection<String>> entry : prereqPatches.entrySet()) {
			LibraryVersion foundVersion = new LibraryVersion(entry.getKey());
			trace("Patch list version: " + foundVersion + ", actual version: " + version); //$NON-NLS-1$ //$NON-NLS-2$
			if (version.compareTo(foundVersion) == 0) {
				patchesToBeChecked = entry.getValue();
				break;
			}

		}

		if (patchesToBeChecked != null) {
			List<String> patchIDs = getPatchIDs(doc);
			if (patchIDs == null) {
				addErrorMessage(testDesc, PrereqCheckMessages.getString("PrereqCheck.53"), genericRecommendation); //$NON-NLS-1$
				return false;
			}
			boolean success = true;
			for (String patch : patchesToBeChecked) {
				if (!isPatchInstalled(patchIDs, patch)) {
					String msg = PrereqCheckMessages.getString("PrereqCheck.54"); //$NON-NLS-1$
					msg = MessageFormat.format(msg, patch);
					addErrorMessage(testDesc, msg, PrereqCheckMessages.getString("PrereqCheck.93")); //$NON-NLS-1$
					success = false;
				} else {
					String msg = PrereqCheckMessages.getString("PrereqCheck.55"); //$NON-NLS-1$
					msg = MessageFormat.format(msg, patch);
					addSuccessMessage(testDesc, msg, EMPTY_RECOMMENDATION);
				}
			}
			return success;
		}
		else {
			return true;
		}
	}


	public List<CheckMessage> getMessages() {
		return this.messages;
	}

	static final String TIER_CLIENT = "client"; //$NON-NLS-1$
	static final String TIER_ENGINE = "engine"; //$NON-NLS-1$

	public boolean argumentsToMembers(Iterator<String> stdin) {
		int counter = 0;
		boolean success = true;

		while (stdin.hasNext()) {
			if (counter == 0) {
				this.setIishome(stdin.next());
				this.setIisHomeDir(new File(this.iishome));

				if (!this.iisHomeDir.isDirectory()) {
					String msg = PrereqCheckMessages.getString("PrereqCheck.58"); //$NON-NLS-1$
					msg = MessageFormat.format(msg, this.iishome);
					this.setErrorMessage(msg);
					success = false;
				}

				counter++;
			} else if (counter == 1) {
				this.setTier(stdin.next());

				if (!(TIER_CLIENT.equals(this.tier) || TIER_ENGINE.equals(this.tier))) {
					// TODO: Add an error message
					success = false;
				}

				counter++;
			} else if (counter == 2) {
				this.setOutputFile(stdin.next());
				counter++;
			} else if (counter == 3) {
				this.javaExecutable = stdin.next();
				counter++;
			}
		}

		return success;
	}


	private static void usage() {
		String titleString = PrereqCheckMessages.getString("PrereqCheck.56") + NL; //$NON-NLS-1$
		String usageString = titleString + NL + NL + PrereqCheckMessages.getString("PrereqCheck.9") + ": prereqcheck <ISHomeDir> " + TIER_CLIENT + "|" + TIER_ENGINE; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		System.err.println(usageString);
	}


	private boolean performChecks() {
		boolean success = false;

		this.addInfoMessage(this.titleString);
		this.addInfoMessage(""); //$NON-NLS-1$
		String msg = PrereqCheckMessages.getString("PrereqCheck.19"); //$NON-NLS-1$
		String user = System.getProperty("user.name"); //$NON-NLS-1$
		msg = MessageFormat.format(msg, new Object[] { (new Date()).toString(), user });
		this.addInfoMessage(msg);
		msg = PrereqCheckMessages.getString("PrereqCheck.20"); //$NON-NLS-1$
		String tierName = null;
		if (TIER_CLIENT.equals(this.tier)) {
			tierName = PrereqCheckMessages.getString("PrereqCheck.21"); //$NON-NLS-1$
		} else {
			tierName = PrereqCheckMessages.getString("PrereqCheck.22"); //$NON-NLS-1$
		}
		msg = MessageFormat.format(msg, tierName);
		this.addInfoMessage(msg);

		if (this.outputFile != null) {
			msg = PrereqCheckMessages.getString("PrereqCheck.59"); //$NON-NLS-1$
			msg = MessageFormat.format(msg, this.outputFile);
			this.addInfoMessage(msg);
		}

		String systemPath = System.getProperty("java.library.path"); //$NON-NLS-1$
		msg = PrereqCheckMessages.getString("PrereqCheck.60"); //$NON-NLS-1$
		msg = MessageFormat.format(msg, systemPath);
		this.addInfoMessage(msg);

		// pc.addInfoMessage(NL);

		if (TIER_CLIENT.equals(this.tier)) {
			success = this.checkClient();
		} else if (TIER_ENGINE.equals(this.tier)) {
			success = this.checkEngine();
		} else {
			PrereqCheck.usage();
			success = false;
		}
		this.addInfoMessage(NL
				+ "========> " + PrereqCheckMessages.getString("PrereqCheck.10") + (success ? PrereqCheckMessages.getString("PrereqCheck.61") : PrereqCheckMessages.getString("PrereqCheck.62"))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

		return success;
	}


	public static String typeToString(TYPE t) {
		switch (t) {
		case ERROR:
			return "FAILED"; //$NON-NLS-1$
		case WARNING:
			return "WARNING"; //$NON-NLS-1$
		case PASSED:
			return "PASSED"; //$NON-NLS-1$
		}
		return "UNKNOWN"; //$NON-NLS-1$
	}


	private String messageToString(boolean csvFormat, CheckMessage cm) {
		if (csvFormat) {
			return cm.getType() + ISALiteSeparator + cm.getTestDescription() + ISALiteSeparator + cm.getResultDescription() + ISALiteSeparator + cm.getRecommendation();
		}
		String NL = ISALiteNL;
		StringBuffer buf = new StringBuffer();
		buf.append("@@TEST@@" + NL); //$NON-NLS-1$
		buf.append("@@RESULT@@" + typeToString(cm.getType()) + "@@RESULTEND@@" + NL); //$NON-NLS-1$ //$NON-NLS-2$
		buf.append("@@TESTDESCR@@" + cm.getTestDescription() + "@@TESTDESCREND@@" + NL); //$NON-NLS-1$ //$NON-NLS-2$
		buf.append("@@RESULTDESCR@@" + cm.getResultDescription() + "@@RESULTDESCREND@@" + NL); //$NON-NLS-1$ //$NON-NLS-2$
		buf.append("@@RECOMMENDATION@@" + cm.getRecommendation() + "@@RECOMMENDATIONEND@@" + NL); //$NON-NLS-1$//$NON-NLS-2$
		buf.append("@@TESTEND@@" + NL); //$NON-NLS-1$
		return buf.toString();
	}


	private boolean populateOutputFile() {
		boolean success = false;
		String msg;

		if (this.outputFile != null) {
			StringBuffer sb = new StringBuffer();
			List<CheckMessage> messages = this.getMessages();
			for (CheckMessage s : messages) {
				sb.append(messageToString(false, s));
			}

			String output = sb.toString();

			try {
				writeToFile(this.outputFile, output);
				String outMsg = PrereqCheckMessages.getString("PrereqCheck.16"); //$NON-NLS-1$
				outMsg = MessageFormat.format(outMsg, this.outputFile);
				success = true;
			} catch (Exception exc) {
				msg = PrereqCheckMessages.getString("PrereqCheck.17"); //$NON-NLS-1$
				msg = MessageFormat.format(msg, this.outputFile);
				System.err.println(msg);
			}
		}

		return success;
	}


	public static void mainWrapper(Iterator<String> stdin, PrintStream stdout) {
		boolean success = false;

		PrereqCheck pc = new PrereqCheck();
		success = pc.argumentsToMembers(stdin);
		success = pc.performChecks();
		success = pc.populateOutputFile();

		if (success) {
			System.exit(0);
		} else if (null != pc.errorMessage) {
			System.exit(1);
		} else {
			System.exit(2);
		}
	}


	/*
	 * Command line argument: PrereqCheck <IISHomeFir> client|engine
	 */
	public static void main(String[] args) {
		if (args.length < 2 || args.length > 4) {
			PrereqCheck.usage();
			System.exit(2);
		}

		PrereqCheck.mainWrapper(Arrays.asList(args).iterator(), System.out);
	}

	static void writeToFile(String outputFile, String content) throws IOException {
		FileOutputStream fos = null;

		try {
			fos = new FileOutputStream(outputFile);
			fos.write(content.getBytes("UTF-8")); //$NON-NLS-1$
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fos != null) {
				fos.close();
			}
		}
	}
}
