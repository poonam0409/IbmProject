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
// Module Name : com.ibm.is.sappack.gen.common.ui.helper
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.common.ui.helper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.eclipse.jface.preference.IPreferenceStore;

import com.ibm.is.sappack.gen.common.ui.preferences.PreferenceConstants;

public final class LoggingHelper {

	static String copyright() {
		return com.ibm.is.sappack.gen.common.ui.helper.Copyright.IBM_COPYRIGHT_SHORT;
	}

	public static final Level TRACE_FILE_HANDLER_DEFAULT_LEVEL = Level.ALL;
	public static final Level ECLIPSE_LOG_HANDLER_DEFAULT_LEVEL = Level.INFO;
	public static final Level COMPONENT_LOGGER_DEFAULT_LEVEL = Level.ALL;

	public static final String DEFAULT_TRACE_FILE_NAME = "sappacks_trace.txt"; //$NON-NLS-1$
	public static final String TRACE_COMPONENTS_DELIMITER = ":"; //$NON-NLS-1$
	public static final String TRACE_COMPONENTS_LEVEL_DELIMITER = "="; //$NON-NLS-1$

	public static final String COM_IBM_IS_SAPPACK_COMMONUI_ID = "com.ibm.is.sappack.commonui"; //$NON-NLS-1$
	public static final String COM_IBM_IS_SAPPACK_MODELER_ID = "com.ibm.is.sappack.modeler"; //$NON-NLS-1$
	public static final String COM_IBM_IS_SAPPACK_GENERATOR_ID = "com.ibm.is.sappack.generator"; //$NON-NLS-1$
	public static final String COM_IBM_IS_SAPPACK_CW_MODELER_ID = "com.ibm.is.sappack.cw.modeler"; //$NON-NLS-1$
	public static final String COM_IBM_IS_SAPPACK_CW_GENERATOR_ID = "com.ibm.is.sappack.cw.generator"; //$NON-NLS-1$

	public static final Map<String, String> TRACE_MESSAGE_BUNDLE_HASH = new HashMap<String, String>() {
		private static final long serialVersionUID = 1L;
		{
			put(COM_IBM_IS_SAPPACK_COMMONUI_ID, "com.ibm.is.sappack.gen.common.ui"); //$NON-NLS-1$
			put(COM_IBM_IS_SAPPACK_MODELER_ID, "com.ibm.is.sappack.gen.tools.sap"); //$NON-NLS-1$
			put(COM_IBM_IS_SAPPACK_GENERATOR_ID, "com.ibm.is.sappack.gen.tools.jobgenerator"); //$NON-NLS-1$
			put(COM_IBM_IS_SAPPACK_CW_MODELER_ID, "com.ibm.is.sappack.cw.tools.sap"); //$NON-NLS-1$
			put(COM_IBM_IS_SAPPACK_CW_GENERATOR_ID, "com.ibm.is.sappack.cw.tools.jobgenerator"); //$NON-NLS-1$
		}
	};
	
	private static List<Logger> knownLoggers;
	
	static {
		knownLoggers = new ArrayList<Logger>();
	}
	
	public static List<Logger> getAllKnownLoggers() {
		return knownLoggers;
	}
	
	public static void addLoggerToKnownList(Logger logger) {
		if (knownLoggers != null) {
			if (!knownLoggers.contains(logger)) {
				knownLoggers.add(logger);
			}
		}
	}
	
	public static void resetAllKnownLoggers() {
		if (knownLoggers != null) {
			for (int i = 0; i < knownLoggers.size(); i++) {
				Logger logger = knownLoggers.get(i);
				Handler[] handlers = logger.getHandlers();
				for (int h = 0; h < handlers.length; h++) {
					logger.removeHandler(handlers[h]);
				}
			}
		}
	}
	
	public static void clearAllKnownLoggersList() {
		if (knownLoggers != null) {
			knownLoggers.clear();
		}
	}

	public static String getMessageBundleByString(String componentString) {
		if (TRACE_MESSAGE_BUNDLE_HASH.containsKey(componentString)) {
			return TRACE_MESSAGE_BUNDLE_HASH.get(componentString);
		}

		return null;
	}

	public static boolean loggerHasTraceFileHandler(Logger logger) {
		Handler[] registeredHandlers = logger.getHandlers();
		for (int i = 0; i < registeredHandlers.length; i++) {
			if (registeredHandlers[i] instanceof FileHandler) {
				return true;
			}
		}

		return false;
	}
	
	public static boolean loggerHasEclipseLogHandler(Logger logger) {
		Handler[] registeredHandlers = logger.getHandlers();
		for (int i = 0; i < registeredHandlers.length; i++) {
			if (registeredHandlers[i] instanceof EclipseLogHandler) {
				return true;
			}
		}

		return false;		
	}

	public static void initializeSingleLogger(IPreferenceStore prefStore, Logger logger) {

		// if trace is enabled in the preferences we configure the given logger
		if (prefStore.getBoolean(PreferenceConstants.P_TRACE_ENABLED)) {

			// we retrieve the components to be traced from the preferences
			String storedTraceComponentsString = prefStore.getString(PreferenceConstants.P_TRACE_COMPONENTS);

			// could be that the component list is empty
			// if not, we configure the component loggers accordingly
			if (!storedTraceComponentsString.equals("")) { //$NON-NLS-1$
				String[] splitTraceComponents = storedTraceComponentsString.split(TRACE_COMPONENTS_DELIMITER);
				for (int i = 0; i < splitTraceComponents.length; i++) {
					String[] traceComponent = splitTraceComponents[i].split(TRACE_COMPONENTS_LEVEL_DELIMITER);
					
					// we only want to configure the given logger
					// so we check if it is in the list of to-be-traced components
					if (traceComponent[0].equalsIgnoreCase(logger.getName())) {

						// clear up the existing trace file handler for the component logger
						// (if existing)
						clearTraceFileHandler(logger);

						FileHandler traceFileHandler = null;
						try {
							traceFileHandler = new FileHandler(prefStore.getString(PreferenceConstants.P_TRACE_FILE_LOC), true);
							traceFileHandler.setFormatter(new SimpleFormatter());
						}
						catch (SecurityException se) {
							se.printStackTrace();
						}
						catch (IOException ioe) {
							ioe.printStackTrace();
						}

						// if there is a level specified for the component
						// then we'll set the trace file handler level accordingly
						if (traceComponent.length > 1) {
							if (traceFileHandler != null) {
								traceFileHandler.setLevel(Level.parse(traceComponent[1]));
							}
						}
						else {
							if (traceFileHandler != null) {
								traceFileHandler.setLevel(TRACE_FILE_HANDLER_DEFAULT_LEVEL);
							}
						}

						// the level of the logger itself is set to a default (usually ALL)
						// which ensures that all log requests will be processed but
						// - in conjunction with the trace file handler level - may not
						// end up in the trace file
						logger.setLevel(COMPONENT_LOGGER_DEFAULT_LEVEL);
						
						if (traceFileHandler != null) {
							logger.addHandler(traceFileHandler);
						}
						
						// add this logger to list of known loggers
						addLoggerToKnownList(logger);
					}
				}
			}
		}
	}
	
	public static void initializeLoggers(IPreferenceStore prefStore) {

		// if trace is enabled in the preferences we
		// create / configure the component specific loggers
		if (prefStore.getBoolean(PreferenceConstants.P_TRACE_ENABLED)) {

			// we retrieve the components to be traced from the preferences
			String storedTraceComponentsString = prefStore.getString(PreferenceConstants.P_TRACE_COMPONENTS);

			// could be that the component list is empty
			// if not, we configure the component loggers accordingly
			if (!storedTraceComponentsString.equals("")) { //$NON-NLS-1$
				String[] splitTraceComponents = storedTraceComponentsString.split(TRACE_COMPONENTS_DELIMITER);
				for (int i = 0; i < splitTraceComponents.length; i++) {
					String[] traceComponent = splitTraceComponents[i].split(TRACE_COMPONENTS_LEVEL_DELIMITER);
					Logger componentLogger = Logger.getLogger(traceComponent[0]);
					
					// clear up the existing trace file handler for the component logger
					// (if existing)
					clearTraceFileHandler(componentLogger);
					
					// configure the new trace file handler
					FileHandler traceFileHandler = null;
					try {
						traceFileHandler = new FileHandler(prefStore.getString(PreferenceConstants.P_TRACE_FILE_LOC), true);
						traceFileHandler.setFormatter(new SimpleFormatter());
					}
					catch (SecurityException se) {
						se.printStackTrace();
					}
					catch (IOException ioe) {
						ioe.printStackTrace();
					}

					// if there is a level specified for the current component
					// then we'll set the trace file handler level accordingly
					if (traceComponent.length > 1) {
						if (traceFileHandler != null) {
							traceFileHandler.setLevel(Level.parse(traceComponent[1]));
						}
					}
					else {
						if (traceFileHandler != null) {
							traceFileHandler.setLevel(TRACE_FILE_HANDLER_DEFAULT_LEVEL);
						}
					}

					// the level of the logger itself is set to a default (usually ALL)
					// which ensures that all log requests will be processed but
					// - in conjunction with the trace file handler level - may not
					// end up in the trace file
					componentLogger.setLevel(COMPONENT_LOGGER_DEFAULT_LEVEL);
					
					if (traceFileHandler != null) {
						componentLogger.addHandler(traceFileHandler);
					}
					
					// add this logger to list of known loggers
					addLoggerToKnownList(componentLogger);
				}
			}
		}
	}
	
	private static void clearTraceFileHandler(Logger logger) {
		Handler[] registeredHandlers = logger.getHandlers();
		for (int i = 0; i < registeredHandlers.length; i++) {
			if (registeredHandlers[i] instanceof FileHandler) {
				logger.removeHandler(registeredHandlers[i]);
			}
		}
	}
}
