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
// Module Name : com.ibm.is.sappack.gen.tools.sap.tableextract
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.tools.sap.tableextract;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

import com.ibm.is.sappack.gen.tools.sap.utilities.SAPTableExtractor;
import com.ibm.is.sappack.gen.tools.sap.utilities.SAPTableExtractor.Result;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;

public class JLTExtractor {

	static String copyright() {
		return com.ibm.is.sappack.gen.tools.sap.tableextract.Copyright.IBM_COPYRIGHT_SHORT;
	}

	JCoDestination destination;
	String checkTableName;
	List<String> checkTableColumns;
	String textTableName;
	List<String> textTableColumns;
	List<String[]> joinColumns;
	private String textTableLanguageColumn;
	private boolean takeOnlyFirstTextTableTuple;
	int maxRows;

	public void setTakeOnlyFirstTextTableTuple(boolean takeOnlyFirstTextTableTuple) {
		this.takeOnlyFirstTextTableTuple = takeOnlyFirstTextTableTuple;
	}

	public JLTExtractor(JCoDestination destination, String checkTableName, List<String> checkTableColumns, String textTableName, List<String> textTableColumns, List<String[]> joinColumns, int maxRows) {
		super();
		this.destination = destination;
		this.checkTableName = checkTableName;
		this.checkTableColumns = checkTableColumns;
		this.textTableName = textTableName;
		this.textTableColumns = textTableColumns;
		this.joinColumns = joinColumns;
		this.textTableLanguageColumn = "SPRAS"; //$NON-NLS-1$
		this.takeOnlyFirstTextTableTuple = false;
		this.maxRows = maxRows;
	}

	public void setTextTableLanguageColumn(String languageColumn) {
		this.textTableLanguageColumn = languageColumn;
	}

	private List<Map<String, String>> getWholeTable(SAPTableExtractor ex, IProgressMonitor monitor) throws JCoException {
		List<Map<String, String>> result = new ArrayList<Map<String, String>>();
		Result res = ex.performQuery(monitor);
		while (res.nextRow()) {
			Map<String, String> row = new HashMap<String, String>(res.getRow());
			result.add(row);
		}
		return result;
	}

	public Result performQuery(IProgressMonitor monitor) throws JCoException {
		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}
		if (this.textTableName == null || this.textTableName.trim().isEmpty()) {
			SAPTableExtractor ctex = new SAPTableExtractor(this.destination, this.checkTableName, this.checkTableColumns, null, this.maxRows);
			return ctex.performQuery(monitor);
		} else {
			SAPTableExtractor ctex = new SAPTableExtractor(this.destination, this.checkTableName, this.checkTableColumns, null, this.maxRows);
			List<String> allTextTableColumns = new ArrayList<String>();
			for (String[] joinColPair : this.joinColumns) {
				allTextTableColumns.add(joinColPair[1]);
			}
			allTextTableColumns.addAll(this.textTableColumns);

			// don't put maxRows into the text table to ensure the join works properly
			SAPTableExtractor ttex = new SAPTableExtractor(this.destination, this.textTableName, allTextTableColumns, textTableLanguageColumn + " = 'E'"); //$NON-NLS-1$
			List<Map<String, String>> checkTable = getWholeTable(ctex, monitor);
			List<Map<String, String>> textTable = getWholeTable(ttex, monitor);

			List<Map<String, String>> jlt = new ArrayList<Map<String, String>>();

			for (Map<String, String> ctrow : checkTable) {
				if (monitor != null) {
					if (monitor.isCanceled()) {
						return new SAPTableExtractor.EmptyResult();
					}
				}
				for (Map<String, String> ttrow : textTable) {
					boolean allJoinColumnsMatch = true;
					for (String[] joinColumnPair : joinColumns) {
						if (!ctrow.get(joinColumnPair[0]).equals(ttrow.get(joinColumnPair[1]))) {
							allJoinColumnsMatch = false;
							break;
						}
					}
					if (allJoinColumnsMatch) {
						Map<String, String> newRow = new HashMap<String, String>();
						for (String ctCol : this.checkTableColumns) {
							newRow.put(ctCol, ctrow.get(ctCol));
						}
						for (String ttCol : this.textTableColumns) {
							newRow.put(ttCol, ttrow.get(ttCol));
						}
						jlt.add(newRow);
						if (this.takeOnlyFirstTextTableTuple) {
							break;
						}
					}
				}
			}

			Result result = new SAPTableExtractor.CompositeResult(jlt.iterator());
			return result;
		}
	}
}
