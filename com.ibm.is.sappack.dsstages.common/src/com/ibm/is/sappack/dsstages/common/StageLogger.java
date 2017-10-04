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
// Module Name : com.ibm.is.sappack.dsstages.common
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.dsstages.common;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.ibm.is.sappack.dsstages.common.util.BufferHandler;
import com.sap.conn.jco.JCo;

public class StageLogger {

	static String copyright() {
		return com.ibm.is.sappack.dsstages.common.Copyright.IBM_COPYRIGHT_SHORT;
	}
	
	public static final String LOGGER_ID = "com.ibm.is.sappack.dsstages.log"; //$NON-NLS-1$

	private static String traceFileNamePrefix = null;
	
	private static Logger logger = null;

	private static long traceID = System.currentTimeMillis();
	
	private static BufferHandler bufferedLogFileHandler = null;
	
	static {
		bufferedLogFileHandler = new BufferHandler();
		getLogger();
	}
	
	public static boolean initializeRFCTrace(Logger logger) {
		boolean rcSuccess = true;
		
		// get JCO trace level and trace file path from environment
		String jcoTraceLevel = System.getenv(Constants.ENV_VAR_JCO_RFC_TRACE);
		String jcoTraceDir   = System.getenv(Constants.ENV_VAR_JCO_RFC_TRACE_DIR);
		
		if (jcoTraceDir != null) {
			int jcoTraceLvLAsInt;

			try {
				jcoTraceLvLAsInt = Integer.parseInt(jcoTraceLevel);

				if (logger != null) {
					logger.log(Level.FINEST, "JCO RFC trace directory (trace level) = '" + jcoTraceDir + "' (" + jcoTraceLvLAsInt + ")."); //$NON-NLS-1$ $NON-NLS-2$ $NON-NLS-3$
				}
			}
			catch(NumberFormatException numberFormatExcpt) {
				if (logger != null) {
					logger.log(Level.FINE, "Invalid JCO RFC trace level value '" + jcoTraceLevel + "'. Assuming '5'."); //$NON-NLS-1$ $NON-NLS-2$
				}
				jcoTraceLvLAsInt = 5;
			}
			JCo.setTrace(jcoTraceLvLAsInt, jcoTraceDir);
		}
		else {
			rcSuccess =  false;
		}
		
		return(rcSuccess);
	}
	
	public static synchronized Logger getLogger() {
		if (logger == null) {
			logger = Logger.getLogger(LOGGER_ID);

			RuntimeConfiguration config = RuntimeConfiguration.getRuntimeConfiguration();
			if (config != null) {
				Level envVarTraceLevel = config.getSAPPacksTraceLevel(); 
				Level l = envVarTraceLevel;
				if (l == null) {
					l = Level.CONFIG;
				}
				if (l.intValue() > Level.CONFIG.intValue()) {
					l = Level.CONFIG;
				}

				// if prefix not set, this is the first time we enter this
				//   add buffer handler and set levels correctly
				if (traceFileNamePrefix == null) {
					bufferedLogFileHandler.setLevel(l);
					logger.addHandler(bufferedLogFileHandler);
					// always log CONFIG as minimum
					logger.setLevel(l);
					logger.log(Level.FINE, "Initializing logger at " + (new Date()).toString()); //$NON-NLS-1$
				} else {
					// remove buffer handler is it is there
					logger.removeHandler(bufferedLogFileHandler);
				
					// only write trace file if value is FINE, FINER, or FINEST
					if (envVarTraceLevel != null && envVarTraceLevel.intValue() < Level.CONFIG.intValue()) {
						FileHandler fh = null;

						String traceDir = config.getSAPPacksTraceDirectory();
						if (traceDir == null) {
							traceDir = "."; //$NON-NLS-1$
						}

						// osuhre, 104026:
						// If the trace dir env var does not contain an existing directory, use .
						File f = new File(traceDir);
						if (!f.isDirectory()) {
							traceDir = "."; //$NON-NLS-1$
						}

						String fulltraceFileName = traceDir + File.separator + traceFileNamePrefix + traceID + Constants.TRACE_FILE_EXTENSION;
						try {
							fh = new FileHandler(fulltraceFileName);
							fh.setEncoding("UTF-8"); //$NON-NLS-1$
						} catch (IOException e) {
							throw new RuntimeException(e);
						}
						fh.setLevel(l);
						fh.setFormatter(new SimpleFormatter());

						logger.addHandler(fh);
						
						// now write the buffered log records to the file
						for (LogRecord bufferedRecord : bufferedLogFileHandler.logRecordList) {
							fh.publish(bufferedRecord);
						}
						
					}
					bufferedLogFileHandler.logRecordList.clear();

				}
			}
		}
		return logger;
	}
	
	// call this function before the first call to getLogger() per process.
	// Otherwise it has no effect
	public static void setTraceFileNamePrefix(String prefix) {
		if (traceFileNamePrefix != null) {
			return;
		}
		if (prefix != null) {
			traceFileNamePrefix = prefix;
			// re-initialize logger
			logger = null;
			getLogger();
		}		
	}

	public static void logUnexpectedException(Throwable t) {
		getLogger().log(Level.SEVERE, "CC_IDOC_CommonUnexpectedException", t); //$NON-NLS-1$
	}

	@SuppressWarnings("nls")
	public static void main(String[] args) {
		try {
			Logger l = getLogger();
			
			l.config("config");
			l.fine("fine");
			
			setTraceFileNamePrefix("a");
			l.finer("finer");
			l.finest("finest");
			l.severe("severe");
			
			System.out.println("DONE");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
