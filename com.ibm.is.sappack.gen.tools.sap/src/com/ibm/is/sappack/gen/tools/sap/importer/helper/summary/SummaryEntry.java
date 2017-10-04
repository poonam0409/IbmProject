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
// Module Name : com.ibm.is.sappack.gen.tools.sap.importer.helper.summary
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.tools.sap.importer.helper.summary;


public class SummaryEntry {

	private MESSAGE_TYPE messageType;
	private String tableName;
	private String messageText;


	public enum MESSAGE_TYPE {
		INFORMATION, WARNING, ERROR
	};

	static String copyright() {
		return com.ibm.is.sappack.gen.tools.sap.importer.helper.summary.Copyright.IBM_COPYRIGHT_SHORT;
	}

	public SummaryEntry(MESSAGE_TYPE messageType, String tableName, String messageText) {
		this.messageType = messageType;
		this.tableName = tableName;
		this.messageText = messageText;
	}
	
	public static SummaryEntry createInformationEntry(String tableName, String messageText) {
		return new SummaryEntry(MESSAGE_TYPE.INFORMATION,tableName,messageText);
	}
	
	public static SummaryEntry createWarningEntry(String tableName, String messageText) {
		return new SummaryEntry(MESSAGE_TYPE.WARNING,tableName,messageText);
	}
	
	public static SummaryEntry createErrorEntry(String tableName, String messageText) {
		return new SummaryEntry(MESSAGE_TYPE.ERROR,tableName,messageText);
	}



	public MESSAGE_TYPE getMessageType() {
		return this.messageType;
	}

	public String getTableName() {
		return this.tableName;
	}

	public String getMessageText() {
		return this.messageText;
	}

	public boolean isTypeInformation() {
		return (this.messageType == MESSAGE_TYPE.INFORMATION);
	}

	public boolean isTypeWarning() {
		return (this.messageType == MESSAGE_TYPE.WARNING);
	}

	public boolean isTypeError() {
		return (this.messageType == MESSAGE_TYPE.ERROR);
	}

}
