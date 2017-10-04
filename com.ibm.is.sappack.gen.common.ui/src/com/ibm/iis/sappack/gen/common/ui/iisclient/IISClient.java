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
// Module Name : com.ibm.is.sappack.gen.tools.jobgenerator
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.iis.sappack.gen.common.ui.iisclient;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.logging.Level;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.w3c.dom.Document;

import com.ibm.iis.sappack.gen.common.ui.preferences.IISPreferencePage;
import com.ibm.is.sappack.gen.common.ui.Activator;
import com.ibm.is.sappack.gen.common.ui.Messages;


public class IISClient {
	public enum IISClientVersion { undefined, v9x, v11xOrLater };

	public static final String IIS_CLIENT_CHECK_TITLE = Messages.IISClient_0;

	private String directory;
	private static IISClientVersion bufferedClientVersion = null;


	// Shell shell;
	static String copyright() {
		return Copyright.IBM_COPYRIGHT_SHORT;
	}

	public IISClient(String directory) {
		this.directory = directory.trim();
		// this.shell = sh;
	}

	public IISClient() {
	   this(IISPreferencePage.getIISClientDirectory());
		// this.shell = sh;
	}


	private Shell getShell() {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window != null) {
			return window.getShell();
		}
		return null;
	}

	private String getDSSAPAdminString() {
		return getClientFolder() + "\\dsadmsapr3.exe"; //$NON-NLS-1$
	}
	
	public String[] check() {
		String[] result = new String[2];
		File f          = new File(this.directory);
		
		if (!f.exists()) {
			result[0] = MessageFormat.format(Messages.IISClient_1, this.directory);
			return result;
		}
		if (!f.isDirectory()) {
			result[0] = MessageFormat.format(Messages.IISClient_2, this.directory);
			return result;
		}
		
      String dsAdminForSAP = getDSSAPAdminString();
		   
		f = new File(dsAdminForSAP);
		if (!f.exists()) {
			result[0] = MessageFormat.format(Messages.IISClient_4, dsAdminForSAP);
			return result;
		}

		String dsDesignExe = getClientFolder() + "\\DSDesign.exe"; //$NON-NLS-1$
		f = new File(dsDesignExe);
		if (!f.exists()) {
			result[0] = MessageFormat.format(Messages.IISClient_3, dsDesignExe);
			return result;
		}

		String versionXML = this.directory + "\\Version.xml"; //$NON-NLS-1$
		f = new File(versionXML);
		if (!f.exists()) {
			result[0] = MessageFormat.format(Messages.IISClient_5, versionXML);
			return result;
		}
		String version = null;
		try {
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(f);
			XPathExpression xp85 =
			      XPathFactory.newInstance().newXPath().compile(
			            "/LocalInstallRegistry/Products/Product[@productId=\"datastage\"]/@version"); //$NON-NLS-1$
			version = (String) xp85.evaluate(doc, XPathConstants.STRING);
			
			// if version is empty, check again for 8.1
			if (version == null || version.length() == 0) {
				XPathExpression xp81 =
				      XPathFactory.newInstance().newXPath().compile(
				            "/ProductVersion/Products/Product[@key=\"informationserver\"]/@version"); //$NON-NLS-1$
				
				version = (String) xp81.evaluate(doc, XPathConstants.STRING);				
			}
			
			result[1] = version;
		}
		catch (Exception exc) {
			exc.printStackTrace();
			String msg = MessageFormat.format(Messages.IISClient_6, new Object[]{versionXML, exc.getMessage()});
			Activator.getLogger().log(Level.FINE, msg, exc);
			result[0] = msg;
			return result;
		}
		return result;
	}

	public void checkWithDialogs() {
		String[] checkResult = check();
		String   version;

		if (checkResult[0] != null) {
			MessageDialog.openError(getShell(), IIS_CLIENT_CHECK_TITLE, checkResult[0]);
			version = null;
		}
		else {
			MessageDialog.openInformation(getShell(), IIS_CLIENT_CHECK_TITLE,
			                              MessageFormat.format(Messages.IISClient_7, checkResult[1]) );
			version = checkResult[1];
		}
		bufferedClientVersion = determineVersion(version);
	}

	protected String getClientFolder() {
		return this.directory + "\\Clients\\Classic"; //$NON-NLS-1$
	}

	public static class IISClientResult {
		public int errorCode;
		public String commandOutput;
	}

	
	public boolean startAndWaitForMultiJobCompileWizard(String domain, String host, String user, String password,
	      String project, String[] jobsNames, IProgressMonitor monitor) throws IOException {

		File tempFile = null;
		FileOutputStream fos = null;

		try {
			// we need to create a temporary XML file containing the names of all jobs
			// to be compiled by the wizard
			tempFile = File.createTempFile("RapidGeneratorMultipleJobCompile", null); //$NON-NLS-1$

			// write XML structure to the temporary file
			StringBuffer tmpJobsFile = new StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\"?><DataStageJobCompilationScript>"); //$NON-NLS-1$
			for (String job : jobsNames) {
				tmpJobsFile.append("<JOB>" + job + "</JOB>");  //$NON-NLS-1$//$NON-NLS-2$
			}

			tmpJobsFile.append("</DataStageJobCompilationScript>"); //$NON-NLS-1$
			byte[] contents = tmpJobsFile.toString().getBytes("UTF-8"); //$NON-NLS-1$
			fos = new FileOutputStream(tempFile);
			fos.write(contents);
			fos.close();
			fos = null;

			// composing the system call for the job compiler wizard
			String[] command =
			      new String[] { getClientFolder() + "\\DSCompilerWizard.exe", "/U=" + user, "/P=" + password, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			            "/D=" + domain, "/H=" + host, project, "-s", tempFile.getAbsolutePath() };   //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$

			CommandLineExec exec = new CommandLineExec();

			// actually running the task / job and maintaining the job monitor status
			if (monitor != null) {
				monitor.beginTask(Messages.IISClient_8, IProgressMonitor.UNKNOWN);
			}

			exec.runCommandLine(command, monitor);

			if (monitor != null) {
				monitor.done();
			}
		}
		finally {

			// as this is always called before exiting the function it is a good place to clean
			// up the temporary XML file created earlier
			if (fos != null) {
				fos.close();
			}

			if (tempFile != null) {
				boolean result = tempFile.delete();
				if (!result) {
					Activator.getLogger().fine("Temporary file could not be deleted: " + tempFile.getCanonicalPath()); //$NON-NLS-1$
				}
			}
		}

		return true;
	}
	
	public void openAndWaitForDSAdminForSAP(String domain, String host, String user, String password,
			String project, String connectionName, IProgressMonitor monitor) throws IOException {
		String[] command = new String[] { getDSSAPAdminString() };

		// the V7 admin understands those command line parameters
		command = new String[] { getDSSAPAdminString(), "/U=" + user, "/P=" + password,    //$NON-NLS-1$//$NON-NLS-2$
		                         "/D=" + domain, "/H=" + host, project, connectionName };  //$NON-NLS-1$//$NON-NLS-2$
		CommandLineExec exec = new CommandLineExec();

		// actually running the task / job and maintaining the job monitor status
		if (monitor != null) {
			monitor.beginTask(Messages.IISClient_9, IProgressMonitor.UNKNOWN);
		}

		exec.runCommandLine(command, monitor);

		if (monitor != null) {
			monitor.done();
		}

	}
	
	public String getClientVersion() {
		String[] checkResult = check();
		
      String version;
		if (checkResult[0] == null) {
			version = checkResult[1];
		}
		else {
         Activator.getLogger().log(Level.SEVERE, MessageFormat.format(Messages.IISClient_10, checkResult[0]));
         version = null;
		}
		
		return(version);
	}

	public static String getMajorVersion(String dsVersion) {
		int ix = dsVersion.indexOf('.');
		if (ix < 0) {
			return dsVersion;
		}
		int ix2 = dsVersion.indexOf('.', ix + 1);
		if (ix2 < 0) {
			return dsVersion;
		}
		int ix3 = dsVersion.indexOf('.', ix2 + 1);
		if (ix3 < 0) {
			return dsVersion;
		}
		String res = dsVersion.substring(0, ix3);
		return res;
	}
	
	public static boolean matchesVersion(String clientVersion, String serverVersion) {
		if (clientVersion == null || serverVersion == null) {
			return false;
		}
		clientVersion = getMajorVersion(clientVersion);
		serverVersion = getMajorVersion(serverVersion);
		return clientVersion.equals(serverVersion);
	}
	
	public static boolean matchesServerVersion(String serverVersion) {
		IISClient iisClient = new IISClient();
		String clientVersion = iisClient.getClientVersion();
		return matchesVersion(clientVersion, serverVersion);
	}

	private static IISClientVersion determineVersion(String clientVersion) {
		IISClientVersion retVersion;
		
		retVersion = IISClientVersion.undefined;
		if (clientVersion != null) {
			try {
				String mainVersionAsString;
				int    mainVersion;

				// get number before 1st dot
				int idx = clientVersion.indexOf('.');
				if (idx < 0) {
					idx = clientVersion.length();
				}
				mainVersionAsString = clientVersion.substring(0, idx);

				mainVersion = Integer.parseInt(mainVersionAsString);
				if (mainVersion < 11) {
					retVersion = IISClientVersion.v9x;
				}
				else {
					retVersion = IISClientVersion.v11xOrLater;
				}
				Activator.getLogger().log(Level.INFO, retVersion.toString());
			}
			catch(NumberFormatException numberFormatExcpt) {
				Activator.getLogger().log(Level.SEVERE, numberFormatExcpt.toString(), numberFormatExcpt);
			}
		} // end of if (clientVersion != null)

		return(retVersion);
	}

	public static IISClientVersion getVersion() {

		if (bufferedClientVersion == null || bufferedClientVersion == IISClientVersion.undefined) {
			IISClient iisClient = new IISClient();
			String clientVersion = iisClient.getClientVersion();

			bufferedClientVersion = determineVersion(clientVersion);
		} // end of if (bufferedClientVersion == null || bufferedClientVersion == IISClientVersion.undefined)

		return(bufferedClientVersion);
	}

}
