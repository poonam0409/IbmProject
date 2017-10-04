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


import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.tools.sap.importer.helper.field.AbstractField;


public class SummaryCollector {

	private int numberOfExtractedTables;
	private String currentTableName;
	private String modelName;

	private List<SummaryEntry> summaryMessages;
	
	
	static String copyright() {
		return com.ibm.is.sappack.gen.tools.sap.importer.helper.summary.Copyright.IBM_COPYRIGHT_SHORT;
	}

	public SummaryCollector(String modelName) {
		this.summaryMessages = new ArrayList<SummaryEntry>();
		this.modelName = modelName;
	}

	public void addTable(String tableName) {
		// osuhre, 47036
		String msg = Messages.SummaryCollector_0;
		msg = MessageFormat.format(msg, tableName);
		this.summaryMessages.add(SummaryEntry.createInformationEntry(tableName, msg));
		this.currentTableName = tableName;
		this.numberOfExtractedTables++;
	}

	public void addTableRenameMessage(String originalTableName, String renamedTableName) {
		String msg = Messages.SummaryCollector_1;
		msg = MessageFormat.format(msg, originalTableName, renamedTableName);
		this.summaryMessages.add(SummaryEntry.createWarningEntry(originalTableName, msg));
	}

	public void addColumnRenameMessage(AbstractField field, String renamedColumnName) {
		String msg = Messages.SummaryCollector_2;
		msg = MessageFormat.format(msg, field.getFieldName(), renamedColumnName);
		this.summaryMessages.add(SummaryEntry.createWarningEntry(this.currentTableName, msg)); 
	}
	
	public void addMessage(String msg) {
		this.summaryMessages.add(SummaryEntry.createInformationEntry("", msg)); //$NON-NLS-1$
	}

	public List<SummaryEntry> getSummary() {
		List<SummaryEntry> result = new ArrayList<SummaryEntry>();
		result.addAll(this.summaryMessages);
		String msg = Messages.SummaryCollector_3;
		msg = MessageFormat.format(msg, numberOfExtractedTables, modelName);
		result.add(SummaryEntry.createInformationEntry("", msg)); //$NON-NLS-1$
		return result;
	}
	
	public void addJltDeleteMessage(String tableName) {
		String msg = Messages.SummaryCollector_4;
		msg = MessageFormat.format(msg, tableName);
		this.summaryMessages.add(SummaryEntry.createInformationEntry(tableName, msg));	
	}
	
	public void addCharToVarcharExtensionMessage(String columnName) {
		String msg = Messages.SummaryCollector_5;
		msg = MessageFormat.format(msg, columnName);
		this.summaryMessages.add(SummaryEntry.createInformationEntry(columnName, msg));	
	}
}
