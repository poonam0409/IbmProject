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

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import com.ibm.is.sappack.dsstages.common.StageLogger;
import com.ibm.is.sappack.dsstages.common.Utilities;


/**
 * IDocServerLogger
 * 
 * Logger for the IDocListener.log file.
 * The logger will log all log entries above
 * the log level 'CONFIG'
 */
public class IDocServerLogger {

	static String copyright() {
		return com.ibm.is.sappack.dsstages.idoc.listener.util.Copyright.IBM_COPYRIGHT_SHORT;
	}
	
	static class IDocServerLogHandler extends FileHandler {
		
		public IDocServerLogHandler(String path) throws IOException {
			/* append to already existing log file */
			super(path, true);
		}
	}

	
	static class IDocServerLogFormatter extends Formatter {
		
		/* create date string */
		private String calcDate(long millisecs)
		{
			SimpleDateFormat date_format = new SimpleDateFormat("MMM dd,yyyy HH:mm:ss"); //$NON-NLS-1$
			Date resultdate = new Date(millisecs);
			return date_format.format(resultdate);
		}

		@Override
		public String format(LogRecord r) {
		
			StringBuffer buffer = new StringBuffer();
			
			/* append time stamp */
			buffer.append(this.calcDate(r.getMillis()));
			buffer.append(" : "); //$NON-NLS-1$
			
			String msg = r.getMessage();
			Object[] params = r.getParameters();
			if (params != null && params.length > 0) {
				msg = MessageFormat.format(msg, params);
			}
			
			Throwable t = r.getThrown();
			if (t != null) {
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				t.printStackTrace(pw);
				pw.close();
				String s = sw.toString();
				msg += "\n" + s; //$NON-NLS-1$
			}
			
			/* append the actual log message */
			buffer.append(msg).append("\n"); //$NON-NLS-1$
			
			return buffer.toString();
		}

	}
	
	/**
	 * configure the logger for use with IDocServer
	 */
	public static void configureLoggerForIDocServer(String dir) {
		Logger logger = StageLogger.getLogger();
		logger.log(Level.FINE, "Configuring logger for IDocServer"); //$NON-NLS-1$
		for (Handler h : logger.getHandlers()) {
			if (h instanceof IDocServerLogHandler) {
				// logger already configured
				return;
			}
		}
		/* path to the IDocListener.log file */
		String path = dir + "IDocListener.log"; //$NON-NLS-1$
		
		String enc = Utilities.getIDocListenerLogEncoding();
		
		FileHandler idocServerLoghandler;
		try {
			idocServerLoghandler = new IDocServerLogHandler(path);
			idocServerLoghandler.setEncoding(enc);
			IDocServerLogFormatter formatter = new IDocServerLogFormatter();
			idocServerLoghandler.setFormatter(formatter);
			// only write CONFIG level to the IDocServer log
			idocServerLoghandler.setLevel(Level.CONFIG);
			logger.addHandler(idocServerLoghandler);
		} catch (IOException e) {
			logger.severe(e.getMessage());
			throw new RuntimeException(e);
		}
		
		StageLogger.setTraceFileNamePrefix("DSSAP_IDOC_Listener_"); //$NON-NLS-1$
	}
	
	
}
