package com.ibm.is.sappack.cw.app.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ibm.is.sappack.cw.app.data.Resources;
import com.ibm.is.sappack.cw.app.data.bdr.ApprovalStatus;
import com.ibm.is.sappack.cw.app.data.bdr.jpa.BusinessObject;
import com.ibm.is.sappack.cw.app.data.bdr.jpa.ProcessStep;
import com.ibm.is.sappack.cw.app.data.bdr.jpa.Table;
import com.ibm.is.sappack.cw.app.data.bdr.jpa.TableUsage;
import com.ibm.is.sappack.cw.app.services.bdr.BphImportType;
import com.ibm.is.sappack.cw.app.services.bdr.threads.BphAbstractImporterThread;

public class ImportTableUsageProvider extends ImportAbstractProvider {
	
	private static ImportTableUsageProvider instanceOfTableUsageProvider;
	private final String CLASS_NAME = ImportTableUsageProvider.class.getName();
	
	public ImportTableUsageProvider() {
		super();
	}
	
	public static synchronized ImportTableUsageProvider getInstance() {
		if (instanceOfTableUsageProvider == null) {
			instanceOfTableUsageProvider = new ImportTableUsageProvider();
		}
		return instanceOfTableUsageProvider;
	}

	@Override
	protected void updateTransientModeEnabled() {
		switch(BphAbstractImporterThread.importType) {
			case CSV_COMPLETE:
				transientMode = false;
				break;
			case CSV_BOS:
			case CSV_PROCESSES:
				transientMode = true;
				break;
		}
	}

	/**
	 * 
	 * @param processStep: parent process step
	 * @param businessObject: parent business object
	 * @param table: parent table
	 * @param tableUsageApprovalStatus: will be set in case of creating a new TableUsage
	 * @param creationDate: will be set in case of creating a new TableUsage
	 * @return: new or existing TableUsage 
	 */
	public TableUsage provideTableUsage(ProcessStep processStep, BusinessObject businessObject, Table table,
			String tableUsageApprovalStatus, Date creationDate) {
		final String METHOD_NAME = "provideTableUsage(ProcessStep processStep, BusinessObject businessObject," +
				"Table table, String tableUsageApprovalStatus, Date creationDate)";
		logger.entering(CLASS_NAME, METHOD_NAME);

		if(table == null || businessObject == null) {
			throw new IllegalArgumentException("The parent BusinessObject and/or Table is null.");
		}
		
		updateTransientModeEnabled();
		
		TableUsage tableUsage = null;
		List<TableUsage> usagesFromStep = processStep.getUsages();
		List<TableUsage> usagesFromBo = businessObject.getUsagesInternal(); //173166
		List<TableUsage> usagesFromTable = table.getUsages();
		tableUsage = findTableUsage(table.getName(), usagesFromStep, usagesFromBo, usagesFromTable);
		
		if (tableUsage == null) {
			tableUsage = createTableUsage(table.getName(), tableUsageApprovalStatus, creationDate);
			logger.fine("Created new table usage object");
		}
		
		logger.fine("Table usage for table: " + table.getName());
		logger.fine("Table usage for BO: " + businessObject.getName());
		logger.fine("Table usage for process step: " + processStep.getName());
		
		// Set relations
		setRelations(processStep, businessObject, table, tableUsage);
		
		if(!transientMode) {
			manager.joinTransaction();
			manager.persist(tableUsage);
		}
		
		logger.exiting(CLASS_NAME, METHOD_NAME);
		return tableUsage;
	}

	private TableUsage findTableUsage(String tableUsageName, List<TableUsage> usagesFromStep, List<TableUsage> usagesFromBo,
			List<TableUsage> usagesFromTable) {
		final String METHOD_NAME = "findTableUsage(String tableUsageName, List<TableUsage> usagesFromStep," +
				"List<TableUsage> usagesFromBo, List<TableUsage> usagesFromTable)";
		logger.entering(this.CLASS_NAME, METHOD_NAME);
		for (TableUsage usageFromStep : usagesFromStep) {
			logger.finest("usage from step: " + usageFromStep.getFullName() + ", id: " + usageFromStep.getTableUsageId());
			if (usageFromStep.getName().equals(tableUsageName)) {
				logger.finest("match!");
				for (TableUsage usageFromBo : usagesFromBo) {
					logger.finest("usage from BO: " + usageFromBo.getFullName() + ", id: " + usageFromBo.getTableUsageId());
					if (usageFromBo.equals(usageFromStep)) {
						logger.finest("match!");
						for (TableUsage usageFromTable : usagesFromTable) {
							logger.finest("usage from table: " + usageFromTable.getFullName() + ", id: " + usageFromTable.getTableUsageId());
							if (usageFromTable.equals(usageFromStep)) {
								logger.finest("match!");
								// Identified exactly one usage with that name
								logger.exiting(CLASS_NAME, METHOD_NAME);
								return usageFromTable;
							}
						}
					}
				}
			}
		}
		logger.exiting(CLASS_NAME, METHOD_NAME);
		return null;
	}

	private void setRelations(ProcessStep processStep, BusinessObject businessObject, Table table, TableUsage tableUsage) {
		final String METHOD_NAME = "setRelations(ProcessStep processStep, BusinessObject businessObject, Table table, TableUsage tableUsage)";
		logger.entering(this.CLASS_NAME, METHOD_NAME);
		
		if(tableUsage.getProcessStep() == null) {
			// Set parents
			tableUsage.setProcessStep(processStep);
			tableUsage.setBusinessObject(businessObject);
			tableUsage.setTable(table);
			tableUsage.updateNames();
		}
		
		if(!BphAbstractImporterThread.importType.getImportType().equals(BphImportType.CSV_PROCESSES.getImportType())
				&& !BphAbstractImporterThread.importType.getImportType().equals(BphImportType.CSV_BOS.getImportType())) {
			List<TableUsage> usagesFromStep = processStep.getUsages();
			List<TableUsage> usagesFromBo = businessObject.getUsagesInternal(); //173166
			List<TableUsage> usagesFromTable = table.getUsages();
			if(usagesFromStep == null) {
				usagesFromStep = new ArrayList<TableUsage>();
				processStep.setUsages(usagesFromStep);
			}
			if(usagesFromBo == null) {
				usagesFromBo = new ArrayList<TableUsage>();
				businessObject.setUsages(usagesFromBo);
			}
			if(usagesFromTable == null) {
				usagesFromTable = new ArrayList<TableUsage>();
				table.setUsages(usagesFromTable);
			}
			String tableUsageName = table.getName();
			for(TableUsage usageFromStep : usagesFromStep) {
				if(usageFromStep.getName().equals(tableUsageName)) {
					for(TableUsage usageFromBo : usagesFromBo) {
						if(usageFromBo.getName().equals(tableUsageName)) {
							for(TableUsage usageFromTable : usagesFromTable) {
								if(usageFromTable.getName().equals(tableUsageName)) {
									// TableUsage exists. Don't add the usage to any list.
									logger.exiting(this.CLASS_NAME, METHOD_NAME);
									return;
								}
							}
						}
					}
				}
			}
			// TableUsage didn't exist yet, so add the Usage to all lists
			usagesFromStep.add(tableUsage);
			usagesFromBo.add(tableUsage);
			usagesFromTable.add(tableUsage);
		}
		logger.exiting(this.CLASS_NAME, METHOD_NAME);
	}
	
	private TableUsage createTableUsage(String tableName, String tableUsageApprovalStatus, Date creationDate) {
		final String METHOD_NAME = "createTableUsage(String tableName, String tableUsageApprovalStatus, Date creationDate)";
		logger.entering(this.CLASS_NAME, METHOD_NAME);
		// Creates and returns a TableUsage with given parameters
		
		// Prevent null values
		if (tableName == null) {
			tableName = "";
		}
		if (creationDate == null) {
			creationDate = new Date();
		}
		
		// Update counterTableUsagess from BphCsvImporterThread
		BphAbstractImporterThread.counterTableUsages++;
		
		TableUsage tableUsage = new TableUsage();
		tableUsage.setName(tableName);
		ApprovalStatus status = ApprovalStatus.UNDEFINED;
		for (ApprovalStatus tmpStatus : ApprovalStatus.values()) {
			if (tmpStatus.toString().equals(tableUsageApprovalStatus)) {
				status = tmpStatus;
				break;
			}
		}
		tableUsage.setApprovalStatus(status);
		tableUsage.setType(Resources.BPH_TYPE_TABLE_USAGE);
		tableUsage.setUpdated(creationDate);
		
		logger.exiting(CLASS_NAME, METHOD_NAME);
		return tableUsage;
	}
	
}
