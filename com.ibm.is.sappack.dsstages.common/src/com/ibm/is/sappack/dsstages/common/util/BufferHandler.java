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
// Module Name : com.ibm.is.sappack.dsstages.common.util
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.dsstages.common.util;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class BufferHandler extends Handler {
	
	static String copyright() {
		return com.ibm.is.sappack.dsstages.common.util.Copyright.IBM_COPYRIGHT_SHORT;
	}

	public List<LogRecord> logRecordList = new ArrayList<LogRecord>();
	
	@Override
	public void close() {
	}

	@Override
	public void flush() {
	}

	@Override
	public void publish(LogRecord record) {
		// call getSourceClassName() to force resolution of method and class names
		record.getSourceClassName();
		logRecordList.add(record);
	}
}
