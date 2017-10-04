//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2011, 2013                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.is.sappack.dsstages.common.ccf
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************

package com.ibm.is.sappack.dsstages.common.ccf;


import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import com.ascential.e2.daapi.util.CC_Message;
import com.ascential.e2.daapi.util.CC_MessageLogger;
import com.ascential.e2.daapi.util.CC_MessageResources;
import com.ibm.is.sappack.dsstages.common.CCFResource;
import com.ibm.is.sappack.dsstages.common.Constants;
import com.ibm.is.sappack.dsstages.common.StageLogger;
import com.ibm.is.sappack.dsstages.common.util.BufferHandler;


public class CCFLogger {
	private static       CC_MessageResources _CCMsgResource  = null;
//	private static       CCFResourceProvider _CCFResProvider = null;


	static String copyright() {
		return com.ibm.is.sappack.dsstages.common.ccf.Copyright.IBM_COPYRIGHT_SHORT;
	}

	static class DSLogHandler extends Handler {

		@Override
		public void close() {
		}

		@Override
		public void flush() {
		}

		@Override
		public void publish(LogRecord record) {
			if (isLoggable(record)) {
				String     msg   = this.getFormatter().format(record);
				Level      lvl   = record.getLevel();
				CC_Message ccMsg = loadMessageFromResource(record);

				if (ccMsg == null) {
					// CC message doesn't exist ==> just log using common log method
					if (lvl == Level.SEVERE) {
						CC_MessageLogger.fatalFromText(msg);
					} 
					else if (lvl == Level.WARNING) {
						CC_MessageLogger.warningFromText(msg);
					} 
					else if (lvl == Level.INFO) {
						CC_MessageLogger.informationFromText(msg);
					} 
					else if (lvl == Level.CONFIG) {
						CC_MessageLogger.informationFromText(msg);
					} 
					else {
						// don't log anything for higher levels
					}
				}
				else {
					// CCmessage exists ==> log message
					if (lvl == Level.SEVERE) {
						CC_MessageLogger.fatal(ccMsg);
					} 
					else if (lvl == Level.WARNING) {
						CC_MessageLogger.warning(ccMsg);
					} 
					else if (lvl == Level.INFO) {
						CC_MessageLogger.information(ccMsg);
					} 
					else if (lvl == Level.CONFIG) {
						CC_MessageLogger.information(ccMsg);
					} 
					else {
						// don't log anything for higher levels
					}
					CC_Message.destroyInstance(ccMsg);
				} // end of (else) if (ccMsg == null)
			}
		}

	}

	static class CCFFormatter extends Formatter {

		@Override
		public String format(LogRecord r) {
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
			return msg;
		}

	}

	static class CCFResourceProvider extends CCFResource {
		
		private CCFResourceProvider () {
			super();
		}
		
		public String getText(String textId) {
			return(getText(textId, null));
		}
		
		public String getText(String textId, Object textParamArr[]) {
			CC_Message ccText;
			String     msgText;

			ccText = CC_Message.createInstance(getCCMsgResource(), textId.getBytes());

			// check if the text could be loaded: are there enclosing '<' and '>' ??
			msgText = null;
			if (ccText != null) {
				msgText = ccText.getMessageString();
				if (msgText.startsWith("<") && msgText.endsWith(">")) {
					// log missing message
					StringBuffer errBuf = new StringBuffer();
					errBuf.append(msgText.substring(0, msgText.length()-1));
					errBuf.append(": ");  //$NON-NLS-1$
					errBuf.append(textId); 
					errBuf.append(msgText.substring(msgText.length()-1)); 
					CC_MessageLogger.warningFromText(errBuf.toString());
					
					// text does not exist in the resource 
					msgText = null;
				}
				else {
					if (textParamArr != null) {
						for(int arrIdx = 0; arrIdx < textParamArr.length; arrIdx ++) {
							ccText.addArgument(textParamArr[arrIdx].toString());
						}
					}
					msgText = ccText.getFormattedMessage();
				} // end of (else) if (msgText.startsWith("<") && msgText.endsWith(">"))

				CC_Message.destroyInstance(ccText);
			}

			return(msgText);
		}

		public String getMessage(String msgId) {
			return(getMessage(msgId, null));
		}

		public String getMessage(String msgId, Object paramArr[]) {
			CC_Message ccMsg;
			String     msgText;

			msgText = null;
			ccMsg   = loadMessageFromResource(msgId, paramArr);
			if (ccMsg != null) {
				msgText = ccMsg.getFormattedMessage();
				CC_Message.destroyInstance(ccMsg);
			}

			return(msgText);
		}
		
	}
	
	/**
	 * configure the logger for use within CCF / DataStage
	 */
	public static void configureLoggerForCCF() {

		new CCFResourceProvider();   // DO NOT DELETE (is used for access to CCF classes from common package classes)
//		_CCFResProvider = new CCFResourceProvider();
		
		// we retrieve our named stage logger
		Logger logger = StageLogger.getLogger();
		
		// we don't want our logger to use parent log handlers
		logger.setUseParentHandlers(false);
		
		// retrieve all existing log handlers
		Handler[] handlers = logger.getHandlers();
		ArrayList<Handler> oddHandlers = new ArrayList<Handler>();
		logger.finest("logger debug: size of handlers[]: " + handlers.length); //$NON-NLS-1$
		
		// scan through the list of existing handlers
		// and get all BufferHandler and FileHandler handlers
		for (Handler h : handlers) {
			if (!(h instanceof BufferHandler) && !(h instanceof FileHandler)) {
				oddHandlers.add(h);
				logger.finest("logger debug: odd handler added: " + h.getClass()); //$NON-NLS-1$
			}
		}

		logger.finest("logger debug: size of oddHandlers[]: " + oddHandlers.size()); //$NON-NLS-1$

		// now get rid of the odd handlers
		for (Handler oh : oddHandlers) {
			try {
				oh.close();
			}
			catch (Exception e) {
				// ignore any exception
			}
			finally {
				logger.removeHandler(oh);
				logger.finest("logger debug: odd handler removed: " + oh.getClass()); //$NON-NLS-1$
			}
		}
		
		// now we create a new log handler for the DS log
		// and configure it accordingly
		DSLogHandler dsLoghandler = new DSLogHandler();
		CCFFormatter formatter = new CCFFormatter();
		dsLoghandler.setFormatter(formatter);
		
		// set the minimum log level of the DS log handler to CONFIG
		dsLoghandler.setLevel(Level.CONFIG);
		
		// attach the DS log handler to the stage logger
		logger.addHandler(dsLoghandler);
	}

	private static CC_Message loadMessageFromResource(String msgKey, Object paramArr[]) {
		CC_Message newCCMsg;
		String     msgText;

		newCCMsg = CC_Message.createInstance(getCCMsgResource(), msgKey.getBytes());

		// check if message could be loaded: are there enclosing '<' and '>' ??
		if (newCCMsg != null) {
			msgText = newCCMsg.getMessageString();
			if (msgText.startsWith("<") && msgText.endsWith(">")) {
				
				// message does not exist in the resource 
				newCCMsg = null;
				CC_Message.destroyInstance(newCCMsg);
			}
			else {
				if (paramArr != null) {
					for(int arrIdx = 0; arrIdx < paramArr.length; arrIdx ++) {
						String argString;
						if (paramArr[arrIdx] == null) {
							argString = " ";  //$NON-NLS-1$
						}
						else {
							argString = paramArr[arrIdx].toString();
						}
						newCCMsg.addArgument(argString);
					}
				}
			} // end of (else) if (msgText.startsWith("<") && msgText.endsWith(">"))
		}

		return(newCCMsg);
	}

	private static CC_Message loadMessageFromResource(LogRecord record) {
		CC_Message newCCMsg;
		Throwable  throwble;

		newCCMsg = loadMessageFromResource(record.getMessage(), record.getParameters());
		
		if (newCCMsg != null) {
			// if there is an exception --> add the stacktrace as argument
			throwble = record.getThrown(); 
			if (throwble != null) {
				Writer      sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				throwble.printStackTrace(pw);
				
				newCCMsg.addArgument(sw.toString());
			} // end of if (throwble != null)
		}
		
		return(newCCMsg);
	}

	static CC_MessageResources getCCMsgResource() {
		if (_CCMsgResource == null) {
			_CCMsgResource = CC_MessageResources.createInstance(Constants.CC_TEXT_RESOURCE_NAME.getBytes());
		}

		return(_CCMsgResource);
	} // end of getCCMsgResource {}
	
} // end of class CCFLogger
