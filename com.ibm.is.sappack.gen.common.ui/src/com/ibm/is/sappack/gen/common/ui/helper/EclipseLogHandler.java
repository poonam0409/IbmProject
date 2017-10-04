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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public class EclipseLogHandler extends Handler {

	static String copyright() {
		return com.ibm.is.sappack.gen.common.ui.helper.Copyright.IBM_COPYRIGHT_SHORT;
	}

	private ILog eclipsePluginLog = null;

	public EclipseLogHandler(ILog pluginLog) {
		eclipsePluginLog = pluginLog;
	}

	@Override
	public void close() {
		eclipsePluginLog = null;
	}

	@Override
	public void flush() {
	}

	@Override
	public void publish(LogRecord record) {
		if (record.getLevel().intValue() >= getLevel().intValue()) {
			int     statusSeverity = mapSeverity(record.getLevel());
			IStatus logStatus      = new Status(statusSeverity, eclipsePluginLog.getBundle().getSymbolicName(), 
			                                    record.getMessage());
			eclipsePluginLog.log(logStatus);
			
			Throwable t = record.getThrown();
			if (t != null) {
				StringWriter sw = new StringWriter();
				t.printStackTrace(new PrintWriter( sw ) );
				String s = sw.toString();
				logStatus = new Status(statusSeverity, eclipsePluginLog.getBundle().getSymbolicName(), s);
				eclipsePluginLog.log(logStatus);
			}
		}
	}

	private int mapSeverity(Level logLevel) {
		if (logLevel.equals(Level.INFO)) {
			return IStatus.INFO;
		}
		else if (logLevel.equals(Level.SEVERE)) {
			return IStatus.ERROR;
		}
		else if (logLevel.equals(Level.WARNING)) {
			return IStatus.WARNING;
		}

		return IStatus.OK;
	}
}
