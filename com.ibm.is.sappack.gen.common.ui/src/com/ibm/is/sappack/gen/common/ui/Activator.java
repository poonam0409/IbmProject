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
// Module Name : com.ibm.is.sappack.gen.common.ui
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.common.ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

import com.ibm.is.sappack.gen.common.ui.helper.EclipseLogHandler;
import com.ibm.is.sappack.gen.common.ui.helper.LoggingHelper;
import com.ibm.is.sappack.gen.common.ui.preferences.PreferenceConstants;
import com.sap.conn.jco.JCo;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.ibm.is.sappack.gen.common.ui"; //$NON-NLS-1$

	// JVM arg to set the library path to the JCo DLL/SO location
	private static final String JAVA_LIB_PATH_ARG = "-Djava.library.path="; //$NON-NLS-1$

	// JVM arg to set the JCo Java archive location
	private static final String JCO_JAR_PATH_ARG = "-Dcom.ibm.is.3rdparty.sap.jco.jar.path="; //$NON-NLS-1$

	// Log Messages
	public static final String LOG_MESSAGE_EXCEPTION_OCCURED = "exception occured"; //$NON-NLS-1$

	// The SAP JCo native library name
	public static final String SAP_JCO_NATIVE_LIBRARY = "sapjco3"; //$NON-NLS-1$

	public static final String PROPERTY_JCO_MIDDLEWARE_NATIVE_LAYER_VERSION = "jco.middleware.native_layer_version"; //$NON-NLS-1$
	public static final String PROPERTY_JCO_MIDDLEWARE_NATIVE_LAYER_PATH = "jco.middleware.native_layer_path"; //$NON-NLS-1$
	public static final String PROPERTY_JCO_MIDDLEWARE_NAME = "jco.middleware.name"; //$NON-NLS-1$

	private static final String jcoClassName = "com.sap.conn.jco.JCo"; //$NON-NLS-1$
	private static final String jcoClassLocation = "com/sap/conn/jco/JCo.class"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;

	private String[] settings = new String[2];

	static String copyright() {
		return com.ibm.is.sappack.gen.common.ui.Copyright.IBM_COPYRIGHT_SHORT;
	}

	/**
	 * The constructor
	 */
	public Activator() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;

		// if the user ticked to use workspace specific SAP JCo settings instead of
		// install time specific settings, try to restart the JCo 3rd party plugin
		// using the workspace specific settings
		if (Activator.getDefault().getUseInstallerConfig()) {
			Activator.getDefault().restartJCoBundle(Activator.getDefault().getJCoJARLocation(),
			      Activator.getDefault().getJCoNativeLibDir());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance.
	 * 
	 * @return the shared instance.
	 */
	public static Activator getDefault() {
		return Activator.plugin;
	}

	/**
	 * Returns the workspace instance.
	 */
	public static IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}

	public boolean getUseInstallerConfig() {
		return this.getPreferenceStore().getBoolean(PreferenceConstants.P_USE_INSTALLER_CONFIG);
	}

	public boolean getDefaultUseInstallerConfig() {
		return this.getPreferenceStore().getDefaultBoolean(PreferenceConstants.P_USE_INSTALLER_CONFIG);
	}

	public String getJCoJARLocation() {
		return this.getPreferenceStore().getString(PreferenceConstants.P_JCO_JAR_LOCATION);
	}

	public String getDefaultJCoJARLocation() {
		return this.getPreferenceStore().getDefaultString(PreferenceConstants.P_JCO_JAR_LOCATION);
	}

	public String getJCoNativeLibDir() {
		return this.getPreferenceStore().getString(PreferenceConstants.P_JCO_NATIVE_LIB_DIR);
	}

	public String getDefaultJCoNativeLibDir() {
		return this.getPreferenceStore().getDefaultString(PreferenceConstants.P_JCO_NATIVE_LIB_DIR);
	}

	public void setJCoNativeLibDir(String value) {
		this.getPreferenceStore().setValue(PreferenceConstants.P_JCO_NATIVE_LIB_DIR, value);
	}

	public void setJCoJARLocation(String value) {
		this.getPreferenceStore().setValue(PreferenceConstants.P_JCO_JAR_LOCATION, value);
	}

	public void setUseInstallerConfig(boolean value) {
		this.getPreferenceStore().setValue(PreferenceConstants.P_USE_INSTALLER_CONFIG, value);
	}

	public String getJCoNativeLibDirFromEclipseINI() {
		return this.getJCoConfigFromEclipseINI()[0];
	}

	public String getJCoJARLocationFromEclipseINI() {
		return this.getJCoConfigFromEclipseINI()[1];
	}

	public void restartJCoBundle(String jarLoc, String nativeLibDir) {
		Bundle jcoBundle = null;
		try {
			String oldJavaLibPath = System.getProperty("java.library.path"); //$NON-NLS-1$

			if (null != oldJavaLibPath && oldJavaLibPath.length() > 0) {
				getLogger().log(Level.CONFIG, MessageFormat.format(Messages.Activator_0, nativeLibDir + File.pathSeparator + oldJavaLibPath));
				System.setProperty("java.library.path", nativeLibDir + File.pathSeparator + oldJavaLibPath); //$NON-NLS-1$
			}
			else {
				getLogger().log(Level.CONFIG, MessageFormat.format(Messages.Activator_1, nativeLibDir));
				System.setProperty("java.library.path", nativeLibDir); //$NON-NLS-1$
			}
			getLogger().log(Level.CONFIG, MessageFormat.format(Messages.Activator_2, jarLoc));
			System.setProperty("com.ibm.is.3rdparty.sap.jco.jar.path", jarLoc); //$NON-NLS-1$

			jcoBundle = Platform.getBundle("com.ibm.is.3rdparty.sap.jco"); //$NON-NLS-1$
			jcoBundle.stop();
			jcoBundle.update();
			getLogger().log(Level.CONFIG, MessageFormat.format(Messages.Activator_3, jcoBundle.getState()));
			jcoBundle.start();
			getLogger().log(Level.CONFIG, MessageFormat.format(Messages.Activator_4, jcoBundle.getState()));
		}
		catch (BundleException e) {
			e.printStackTrace();
		}
	}

	public String[] getJCoConfigFromEclipseINI() {
		if (null != this.settings[0] && null != this.settings[1]) {
			return this.settings;
		}

		settings[0] = Messages.Activator_5;
		settings[1] = Messages.Activator_6;

		InputStream eclipseINIStream = null;
		BufferedReader reader = null;

		try {
			URL eclipseINI = new URL(System.getProperty("eclipse.home.location") + "eclipse.ini"); //$NON-NLS-1$ //$NON-NLS-2$
			eclipseINIStream = eclipseINI.openStream();
			String line;

			if (null != eclipseINIStream) {
				InputStreamReader isr = new InputStreamReader(eclipseINIStream, "UTF-8"); //$NON-NLS-1$
				reader = new BufferedReader(isr);

				while ((line = reader.readLine()) != null) {
					if (line.startsWith(Activator.JAVA_LIB_PATH_ARG)) {
						String[] keyValPair = line.split(Activator.JAVA_LIB_PATH_ARG);
						settings[0] = keyValPair[1];
					}
					else if (line.startsWith(Activator.JCO_JAR_PATH_ARG)) {
						String[] keyValPair = line.split(Activator.JCO_JAR_PATH_ARG);
						settings[1] = keyValPair[1];
						;
					}
				}
			}
		}
		catch (MalformedURLException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (eclipseINIStream != null) {
					eclipseINIStream.close();
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			finally {
				try {
					if (reader != null) {
						reader.close();
					}
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return settings;
	}

	public static String locateJCoJAR() {
		URL location;
		ClassLoader loader = Activator.class.getClassLoader();
		String retval = ""; //$NON-NLS-1$

		if (loader == null) {
			location = ClassLoader.getSystemResource(jcoClassLocation);
		}
		else {
			location = loader.getResource(jcoClassLocation);
		}
		
		if (location != null) {
			URL newLocation;
			try {
				newLocation = FileLocator.resolve(location);

				Pattern p = Pattern.compile("^.*:(.*)!.*$"); //$NON-NLS-1$
				Matcher m = p.matcher(newLocation.toString());
				if (m.find()) {
					retval = m.group(1);
				}
				else {
					getLogger().log(Level.CONFIG, MessageFormat.format(Messages.Activator_10, location));
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		else {
			getLogger().log(Level.CONFIG, MessageFormat.format(Messages.Activator_11, jcoClassName));
		}
		
		return retval;
	}

	public static final String getJCoErrorMessage() {
		String retVal = null;
	    final String jcoJarMsg = Messages.Activator_12;

		try {
			// System.loadLibrary(SAP_JCO_NATIVE_LIBRARY);
			JCo.getVersion();
		}
		catch (NoClassDefFoundError noClassDefFoundError) {
			retVal = jcoJarMsg;
			getLogger().log(Level.CONFIG, retVal, noClassDefFoundError);
		}
		catch (UnsatisfiedLinkError unsatisfiedLinkError) {
			retVal =
			      Messages.Activator_13;
			getLogger().log(Level.CONFIG, retVal, unsatisfiedLinkError);
		}
		catch (Throwable throwable) {
			ExceptionHandler.handleThrowable(throwable, Display.getCurrent().getActiveShell());
			retVal = jcoJarMsg;
		}

		return retVal;
	}

	public static final boolean checkJCoAvailabilityNonGUI() {
		try {
//			System.loadLibrary(SAP_JCO_NATIVE_LIBRARY);
			JCo.getVersion();
			return true;
		}
		catch (NoClassDefFoundError noClassDefFoundError) {
			getLogger().log(Level.CONFIG, Messages.Activator_14, noClassDefFoundError);
			return false;
		}
		catch (UnsatisfiedLinkError unsatisfiedLinkError) {
			getLogger().log(Level.CONFIG, Messages.Activator_15, unsatisfiedLinkError);
			getLogger().log(Level.CONFIG, MessageFormat.format(Messages.Activator_16, System.getProperty("java.library.path", Messages.Activator_18))); //$NON-NLS-1$
			return false;
		}
		catch (Throwable throwable) {
			ExceptionHandler.handleThrowable(throwable, Display.getCurrent().getActiveShell());
			return false;
		}
	}

	public static Logger getLogger() {
		Logger pluginLogger = Logger.getLogger(LoggingHelper.COM_IBM_IS_SAPPACK_COMMONUI_ID);

		if (!LoggingHelper.loggerHasTraceFileHandler(pluginLogger)) {
			com.ibm.is.sappack.gen.common.ui.Activator prefActivator =
			      com.ibm.is.sappack.gen.common.ui.Activator.getDefault();
			IPreferenceStore prefStore = prefActivator.getPreferenceStore();
			if (prefStore != null) {
				LoggingHelper.initializeSingleLogger(prefStore, pluginLogger);
			}
		}

		if (!LoggingHelper.loggerHasEclipseLogHandler(pluginLogger)) {
			EclipseLogHandler pluginHandler = new EclipseLogHandler(getDefault().getLog());
			pluginHandler.setLevel(LoggingHelper.ECLIPSE_LOG_HANDLER_DEFAULT_LEVEL);
			pluginLogger.addHandler(pluginHandler);
		}

		return pluginLogger;
	}

	public static void logException(Throwable e) {
		
		getLogger().log(Level.SEVERE, Messages.Activator_7, e);
		
	}
	
	
}
