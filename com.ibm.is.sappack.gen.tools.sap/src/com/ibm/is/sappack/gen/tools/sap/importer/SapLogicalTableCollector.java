//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2009, 2014                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.is.sappack.gen.tools.sap.importer
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.tools.sap.importer;


import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

import com.ibm.iis.sappack.gen.common.ui.connections.SapSystem;
import com.ibm.iis.sappack.gen.common.ui.jco.RfcDestinationDataProvider;
import com.ibm.iis.sappack.gen.tools.sap.tablelist.ITableList;
import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.tools.sap.constants.Constants;
import com.ibm.is.sappack.gen.tools.sap.importer.helper.configuration.ImporterOptionsBase.CHECKTABLEOPTIONS;
import com.ibm.is.sappack.gen.tools.sap.importer.helper.configuration.TableImporterOptions;
import com.ibm.is.sappack.gen.tools.sap.model.SapTable;
import com.ibm.is.sappack.gen.tools.sap.model.SapTableSet;
import com.ibm.is.sappack.gen.tools.sap.utilities.Utilities;
import com.sap.conn.jco.AbapException;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoRepository;
import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.JCoTable;


public class SapLogicalTableCollector implements IRunnableWithProgress {

	private SapSystem sapSystem;
	private JCoDestination destination;
	private JCoFunction function_DD_TABL_GET;
	private JCoFunction function_TABLE_GET_TEXTTABLE;

	private Collection<String> rootTables;
	private SapTableSet sapTableSet;
	private String businessObjectName;
	private TableImporterOptions options;


	static String copyright() {
		return com.ibm.is.sappack.gen.tools.sap.importer.Copyright.IBM_COPYRIGHT_SHORT;
	}


	public SapLogicalTableCollector(SapSystem sapSystem, ITableList rootTables, TableImporterOptions options) throws JCoException {
		this.sapSystem = sapSystem;
		this.businessObjectName=rootTables.getBusinessObjectName();
		this.rootTables = rootTables.getTables();
		this.options = options;
	}

	private final void init() throws JCoException {
		if (this.destination == null) {
			this.destination = RfcDestinationDataProvider.getDestination(this.sapSystem);
			JCoRepository repository = this.destination.getRepository();
			this.function_DD_TABL_GET = repository.getFunction(Constants.JCO_FUNCTION_DD_TABL_GET);
			this.function_TABLE_GET_TEXTTABLE = repository.getFunction(Constants.JCO_FUNCTION_TABLE_GET_TEXTTABLE);
		}
	}

	private final SapTableSet collectTablesRecursively(IProgressMonitor progressMonitor) throws JCoException {
		SapTableSet tableSet = new SapTableSet(this.businessObjectName);
		Iterator<String> iterator = this.rootTables.iterator();
		for (int i = 0; iterator.hasNext(); i++) {
			String tableName = iterator.next();
			SapTable table = new SapTable(tableName, null);

			addRelatedTables(tableSet, table, 0, progressMonitor);
			progressMonitor.worked(1);

		}

		progressMonitor.subTask(Messages.SapLogicalTableCollector_0);
		return tableSet;
	}

	private final void addRelatedTables(SapTableSet tableSet, SapTable table, int recursionDepth, final IProgressMonitor progressMonitor) throws JCoException {
		if (table == null) {
			return;
		}
		// even if the table is already found, do not stop if this is a toplevel
		// call
		if (recursionDepth > 0 && (tableSet.contains(table))) {
			return;
		}
		progressMonitor.subTask(MessageFormat.format(Messages.SapLogicalTableCollector_1, table.getName()));

		if (progressMonitor.isCanceled()) {
			return;
		}

		/* we have to check if the table already exists in the tableSet */
		if (!tableSet.add(table)) {

			/* update the already existing table with the new table.
			 * We didn't collect the check tables for this table in 
			 * the first, because the table was handled as a check table.
			 * Now we handle the table as a root table and have to collect
			 * the check tables */
			if (recursionDepth == 0) {
				tableSet.remove(table);
				tableSet.add(table);
			}
		}

		boolean onlySetDescription = false;

		// check if recursive search is enabled
		if (recursionDepth > 0 && !this.options.isExtractRecursively()) {
			if (table.isCheckTablesDone()) {
				return;
			}
			onlySetDescription = true;
		}

		if (!table.isCheckTablesDone()) {
			SapTableSet checkTableSet = getCheckTables(table, onlySetDescription);
			if (onlySetDescription) {
				return;
			}

			// Search for check tables if selected
			if (getSearchCheckTables()) {
				Iterator<SapTable> iterator = checkTableSet.iterator();
				while (iterator.hasNext()) {
					SapTable currentTable = iterator.next();

					// osuhre, only consider white list if it is not empty
					List<String> tableWhiteList = this.options.getTableWhiteList();
					if (tableWhiteList != null && tableWhiteList.size() > 0) {
						if (Utilities.containsIgnoreCase(tableWhiteList, currentTable.getName())) {
							addRelatedTables(tableSet, currentTable, recursionDepth + 1, progressMonitor);
						}
					} else {
						// check if current table is blacklisted
						if (this.options.getTableBlackList() != null) {
							if (!Utilities.containsIgnoreCase(this.options.getTableBlackList(), currentTable.getName())) {
								addRelatedTables(tableSet, currentTable, recursionDepth + 1, progressMonitor);
							}
						}
					}
				}

				// CW only ==> get the text tables of the check tables
				if (this.options.getChecktableOption() == CHECKTABLEOPTIONS.JOINED_CHECK_AND_TEXT_TABLES) {
					for (SapTable chkTable : checkTableSet) {
						if (chkTable.isCheckTable()) {
							SapTable textTable = getTextTable(chkTable);
							addRelatedTables(tableSet, textTable, recursionDepth + 1, progressMonitor);
						}
					}
				}
			} // end of if (getSearchCheckTables())
		} // end of if (!table.isCheckTablesDone())

		// NON CW only ==> Search text tables if selected
		if (this.options.getExtractTextTables()) {
			if (!table.isTextTablesDone()) {
				SapTable textTable = getTextTable(table);
				addRelatedTables(tableSet, textTable, recursionDepth + 1, progressMonitor);
			}
		}
	}

	private final SapTable getTextTable(final SapTable table) throws JCoException {

		this.function_TABLE_GET_TEXTTABLE.getImportParameterList().setValue(Constants.JCO_PARAMETER_CHECKTABLE, table.getName());

		try {
			table.setTextTablesDone();
			this.function_TABLE_GET_TEXTTABLE.execute(this.destination);
		} catch (AbapException e) {
			if (e.getKey().equalsIgnoreCase(Constants.JCO_ERROR_KEY_TABLE_NOT_FOUND)) {
				return null;
			}
			throw e;
		}
		String textTableName = this.function_TABLE_GET_TEXTTABLE.getExportParameterList().getString(Constants.JCO_PARAMETER_TABNAME);

		SapTable textTable = new SapTable(textTableName, null);
		table.addTextTable(textTable);

		return textTable;
	}

	private SapTableSet getCheckTables(SapTable table, boolean onlySetDescription) throws JCoException {

		SapTableSet checkTableSet = new SapTableSet();

		this.function_DD_TABL_GET.getImportParameterList().setValue(Constants.JCO_PARAMETER_TABL_NAME, table.getName());
		this.function_DD_TABL_GET.getImportParameterList().setValue(Constants.JCO_PARAMETER_WITHTEXT, Constants.JCO_PARAMETER_VALUE_TRUE);
		// removed to make the code also run on SAP 3.1H
		// this.function_DD_TABL_GET.getImportParameterList().setValue(Constants.JCO_PARAMETER_ADD_TYPEINFO,
		// Constants.JCO_PARAMETER_VALUE_FALSE);

		// snelke: 111769 (Japanese description of table isn't imported)
//		this.function_DD_TABL_GET.getImportParameterList().setValue(Constants.JCO_PARAMETER_LANGU, Constants.JCO_PARAMETER_VALUE_LANGUAGE_EN);
		this.function_DD_TABL_GET.getImportParameterList().setValue(Constants.JCO_PARAMETER_LANGU, this.destination.getLanguage());

		this.function_DD_TABL_GET.execute(this.destination);

		JCoStructure structure_DD02V_WA_A = this.function_DD_TABL_GET.getExportParameterList().getStructure(Constants.JCO_RESULTSTRUCTURE_DD02_V_WA_A);
		String description = structure_DD02V_WA_A.getString(Constants.JCO_PARAMETER_DDTEXT);
		String tableName = structure_DD02V_WA_A.getString(Constants.JCO_PARAMETER_TABNAME);
		String devClass = structure_DD02V_WA_A.getString(Constants.JCO_PARAMETER_CONTFLAG);
		
		if (Utilities.isEmpty(tableName)) {
			table.setExistsOnSAPSystem(false);
			table.setDescription(Messages.SapLogicalTableCollector_2);
		} else {
			table.setDescription(description);
		}
		table.setDevClass(devClass);

		/*
		if (table.isCheckTable()) {
			if (this.options.getChecktableOption() == CHECKTABLEOPTIONS.JOINED_CHECK_AND_TEXT_TABLES) {
				table.setDescription(Messages.SapLogicalTableCollector_5);
			} else if (this.options.getChecktableOption() == CHECKTABLEOPTIONS.TRANSCODING_TABLES) {
				table.setDescription(Messages.SapLogicalTableCollector_6);
			}
		}
		*/

		if (!onlySetDescription) {
			if (getSearchCheckTables()) {
				JCoTable table_DD05M_TAB_A = this.function_DD_TABL_GET.getTableParameterList().getTable(Constants.JCO_RESULTTABLE_DD05_M_TAB_A);
				if ((table_DD05M_TAB_A != null) && (!table_DD05M_TAB_A.isEmpty())) {
					do {
						String checkTableName = table_DD05M_TAB_A.getString(Constants.JCO_PARAMETER_CHECKTABLE);
						SapTable checkTable = new SapTable(checkTableName, null);
						table.addCheckTable(checkTable);
						if (this.rootTables.contains(checkTable.getName())) {
							checkTable.setCheckTable(false);
						}
						checkTableSet.add(checkTable);
					} while (table_DD05M_TAB_A.nextRow());
				}
			}
			table.setCheckTablesDone();
		}

		return checkTableSet;
	}

	@Override
	public void run(IProgressMonitor progressMonitor) throws InvocationTargetException {
		try {
			progressMonitor.beginTask(Messages.SapLogicalTableCollector_3, this.rootTables.size() + 1);
			progressMonitor.subTask(Messages.SapLogicalTableCollector_4);
			init();
			progressMonitor.worked(1);
			this.sapTableSet = collectTablesRecursively(progressMonitor);
			progressMonitor.done();
		} catch (Exception e) {
			throw new InvocationTargetException(e);
		}
	}

	boolean getSearchCheckTables() {
		return this.options.getChecktableOption() == CHECKTABLEOPTIONS.CHECKTABLES || this.options.getChecktableOption() == CHECKTABLEOPTIONS.JOINED_CHECK_AND_TEXT_TABLES
				|| this.options.getChecktableOption() == CHECKTABLEOPTIONS.TRANSCODING_TABLES;

	}

	public SapTableSet getRelatedTables() {
		return this.sapTableSet;
	}
}
