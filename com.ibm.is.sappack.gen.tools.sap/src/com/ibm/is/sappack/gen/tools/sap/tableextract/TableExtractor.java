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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.datatools.modelbase.sql.tables.Column;
import org.eclipse.datatools.modelbase.sql.tables.Table;
import org.eclipse.emf.ecore.EAnnotation;

import com.ibm.datatools.core.DataToolsPlugin;
import com.ibm.iis.sappack.gen.common.ui.connections.SapSystem;
import com.ibm.iis.sappack.gen.common.ui.jco.RfcDestinationDataProvider;
import com.ibm.is.sappack.gen.common.Constants;
import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.tools.sap.activator.Activator;
import com.ibm.is.sappack.gen.tools.sap.utilities.SAPTableExtractor;
import com.sap.conn.jco.JCoDestination;

public abstract class TableExtractor {

	static String copyright() {
		return com.ibm.is.sappack.gen.tools.sap.tableextract.Copyright.IBM_COPYRIGHT_SHORT;
	}

	List<Table> tables;
	SapSystem sapSystem;
	int maxRows;
	
	protected TableExtractor(List<Table> tables, SapSystem sapSystem, int maxRows) {
		this.tables = tables;
		this.sapSystem = sapSystem;
		this.maxRows = maxRows;
	}
	
	List<String> parseListAnnot(String s) {
		StringTokenizer tok = new StringTokenizer(s, ","); //$NON-NLS-1$
		List<String> result = new ArrayList<String>();
		while (tok.hasMoreTokens()) {
			result.add(tok.nextToken());
		}
		return result;
	}

	List<String[]> parseJoinCondition(String s) {
		List<String> pairs = parseListAnnot(s);
		List<String[]> result = new ArrayList<String[]>();
		for (String pair : pairs) {
			int ix = pair.indexOf('=');
			if (ix > -1) {
				String[] p = new String[2];
				p[0] = pair.substring(0, ix);
				p[1] = pair.substring(ix + 1);
				result.add(p);
			}
		}
		return result;
	}

	protected final static String NL = "\n"; //$NON-NLS-1$
	
	public void extractTables(IProgressMonitor monitor) throws Exception {
		monitor.beginTask(Messages.TableExtractor_0, this.tables.size() * 3 );

		for (Table table : this.tables) {
			List<?> emfColumns = table.getColumns();
			List<Column> columns = new ArrayList<Column>();
			List<String> columnNames = new ArrayList<String>();
			String fullTableName = table.getSchema().getName() + "." + table.getName(); //$NON-NLS-1$
			String insertStatement = "INSERT INTO " + fullTableName + "("; //$NON-NLS-1$ //$NON-NLS-2$
			boolean first = true;
			Iterator<?> colit = emfColumns.iterator();
			while (colit.hasNext()) {
				if (!first) {
					insertStatement += ", "; //$NON-NLS-1$
				}
				first = false;
				Column col = (Column) colit.next();
				insertStatement += col.getName();
				columnNames.add(col.getName());
				columns.add(col);
			}
			insertStatement += ") "; //$NON-NLS-1$
			
			if (monitor.isCanceled()) {
				return;
			}
			monitor.worked(1);
			
			String msg = Messages.TableExtractor_1;
			msg = MessageFormat.format(msg, table.getName());
			monitor.subTask(msg);
			JCoDestination destination = RfcDestinationDataProvider.getDestination(sapSystem);
			long beforeFetchingSAPData = System.currentTimeMillis();
			SAPTableExtractor.Result sapResult = null;
			EAnnotation annot = table.getEAnnotation(DataToolsPlugin.ANNOTATION_UDP);
			Map<?, ?> m = annot.getDetails().map();
			String dataObjectSource = (String) m.get(Constants.ANNOT_DATA_OBJECT_SOURCE);
			if (Constants.DATA_OBJECT_SOURCE_TYPE_JOINED_CHECK_AND_TEXT_TABLE.equals(dataObjectSource)) {
				List<String> ctColumns = parseListAnnot((String) m.get(Constants.ANNOT_CHECKTABLE_COLUMNS));
				List<String> ttColumns = parseListAnnot((String) m.get(Constants.ANNOT_TEXTTABLE_COLUMNS));
				List<String[]> joinCond = parseJoinCondition((String) m.get(Constants.ANNOT_CHECKTABLE_TEXTTABLE_JOINCONDITION));
				String checkTable = (String) m.get(Constants.ANNOT_CHECK_TABLE_NAME);
				String textTable = (String) m.get(Constants.ANNOT_TEXT_TABLE_NAME);
				String ttLangColumn = (String) m.get(Constants.ANNOT_TEXTTABLE_LANGUAGE_COLUMN);
				String takeOnlyFirstTTTupleStr = (String) m.get(Constants.ANNOT_TEXTTABLE_TAKE_ONLY_FIRST_TUPLE);
				boolean takeOnlyFirstTTTuple = Boolean.parseBoolean(takeOnlyFirstTTTupleStr);

				JLTExtractor jltex = new JLTExtractor(destination, checkTable, ctColumns, textTable, ttColumns, joinCond, this.maxRows);
				jltex.setTextTableLanguageColumn(ttLangColumn);
				jltex.setTakeOnlyFirstTextTableTuple(takeOnlyFirstTTTuple);
				sapResult = jltex.performQuery(monitor);
			} else if (Constants.DATA_OBJECT_SOURCE_TYPE_LOGICAL_TABLE.equals(dataObjectSource)) {
				SAPTableExtractor ex = new SAPTableExtractor(destination, table.getName(), columnNames, null, this.maxRows);
				sapResult = ex.performQuery(monitor);
			} else if (Constants.DATA_OBJECT_SOURCE_TYPE_REFERENCE_CHECK_TABLE.equals(dataObjectSource)) {
				String sapTableName = (String) m.get(Constants.ANNOT_CHECK_TABLE_NAME);
				SAPTableExtractor ex = new SAPTableExtractor(destination, sapTableName, columnNames, null, this.maxRows);
				sapResult = ex.performQuery(monitor);
			} else if (Constants.DATA_OBJECT_SOURCE_TYPE_REFERENCE_TEXT_TABLE.equals(dataObjectSource)) {
				String sapTableName = (String) m.get(Constants.ANNOT_TEXT_TABLE_NAME);
				SAPTableExtractor ex = new SAPTableExtractor(destination, sapTableName, columnNames, null, this.maxRows);
				sapResult = ex.performQuery(monitor);
			} else {

				Activator.getLogger().fine(Messages.TableExtractor_2);
				monitor.worked(2);
				continue;
			}
			long afterFetchingSAPData = System.currentTimeMillis();
			msg = Messages.TableExtractor_3;
			msg = MessageFormat.format(msg, (afterFetchingSAPData - beforeFetchingSAPData));
			Activator.getLogger().log(Level.INFO, msg); 
			
			if (monitor.isCanceled()) {
				return;
			}
			monitor.worked(1);
			
			monitor.subTask(Messages.TableExtractor_4 + table.getName());
			long beforeProcessing = System.currentTimeMillis();
			processTable(table, insertStatement, sapResult, monitor);
			long afterProcessing = System.currentTimeMillis();

			String msg2 = Messages.TableExtractor_5;
			msg2 = MessageFormat.format(msg2, (afterProcessing - beforeProcessing));
			Activator.getLogger().log(Level.INFO, msg2); 
						
			if (monitor.isCanceled()) {
				return;
			}
			monitor.worked(1);
		}
		monitor.done();
	}

	protected abstract void processTable(Table table, String insertStatementPrefix, SAPTableExtractor.Result sapResult, IProgressMonitor monitor) throws Exception;

	
}
