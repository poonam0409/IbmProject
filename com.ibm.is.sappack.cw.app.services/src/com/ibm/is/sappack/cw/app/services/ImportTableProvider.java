package com.ibm.is.sappack.cw.app.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;

import com.ibm.is.sappack.cw.app.data.Resources;
import com.ibm.is.sappack.cw.app.data.bdr.jpa.BusinessObject;
import com.ibm.is.sappack.cw.app.data.bdr.jpa.Table;
import com.ibm.is.sappack.cw.app.services.bdr.threads.BphAbstractImporterThread;

public class ImportTableProvider extends ImportAbstractProvider {
	
	private static ImportTableProvider instanceOfTableProvider;
	private final String CLASS_NAME = ImportTableProvider.class.getName();
	
	private ImportTableProvider() {
		super();
	}
	
	public static synchronized ImportTableProvider getInstance() {
		if(instanceOfTableProvider == null) {
			instanceOfTableProvider = new ImportTableProvider();
		}
		return instanceOfTableProvider;
	}
	
	/**
	 * 
	 * @param parent: parent business object of the table
	 * @param tableName: name to search for
	 * @param tableDescription: will be used to create a new table
	 * @param creationDate: will be used to create a new table
	 * @return: Newly created table
	 * @throws NotSupportedException
	 * @throws SystemException
	 * @throws SecurityException
	 * @throws IllegalStateException
	 * @throws RollbackException
	 * @throws HeuristicMixedException
	 * @throws HeuristicRollbackException
	 */
	public Table provideTable(BusinessObject parent, String tableName, String tableDescription, Date creationDate) {
		final String METHOD_NAME = "getTable(BusinessObject parent, String tableName, String tableDescription, Date creationDate)";
		logger.entering(CLASS_NAME, METHOD_NAME);

		if(parent == null) {
			throw new IllegalArgumentException("The parent BusinessObject is null.");
		}
		
		updateTransientModeEnabled();
		
		Table table = null;
		
		// Get existing tables
		if(BphAbstractImporterThread.tableMap.isEmpty()) {
			TypedQuery<Table> query = manager.createNamedQuery("Table.getAll", Table.class);
			List<Table> tableList = query.getResultList();
			// Put tables into HashMap. The name will be the key.
			for(Table existingTable : tableList) {
				BphAbstractImporterThread.tableMap.put(existingTable.getName(), existingTable);
			}
		}
		
		
		// Find if table already exists
		table = BphAbstractImporterThread.tableMap.get(tableName);
		
		if(table == null) {
			// Creating new one
			table = createTable(tableName, tableDescription, creationDate);
			BphAbstractImporterThread.tableMap.put(tableName, table);
		}
		
		setRelations(parent, table);
		
		if(!this.transientMode) {
			// Persist depending on transientModeEnabled
			manager.joinTransaction();
			manager.persist(table);
		}
		
		logger.exiting(CLASS_NAME, METHOD_NAME);
		return table;
	}
	
	public Table provideTable(String tableName, String tableDescription, Date creationDate) {
		final String METHOD_NAME = "getTable(String tableName, String tableDescription, Date creationDate)";
		logger.entering(CLASS_NAME, METHOD_NAME);

		updateTransientModeEnabled();
		
		Table table = null;
		
		// Get existing tables
		if(BphAbstractImporterThread.tableMap.isEmpty()) {
			TypedQuery<Table> query = manager.createNamedQuery("Table.getAll", Table.class);
			List<Table> tableList = query.getResultList();
			// Put tables into HashMap. The name will be the key.
			for(Table existingTable : tableList) {
				BphAbstractImporterThread.tableMap.put(existingTable.getName(), existingTable);
			}
		}
		
		// Find if table already exists
		table = BphAbstractImporterThread.tableMap.get(tableName);
		
		if(table == null) {
			// Creating new one
			table = createTable(tableName, tableDescription, creationDate);
			BphAbstractImporterThread.tableMap.put(tableName, table);
		}
		
		if(!this.transientMode) {
			// Persist depending on transientModeEnabled
			manager.joinTransaction();
			manager.persist(table);
		}
		
		logger.exiting(CLASS_NAME, METHOD_NAME);
		return table;
	}
	
	private void setRelations(BusinessObject parent, Table table) {
		final String METHOD_NAME = "setRelations(BusinessObject parent, Table table)";
		logger.entering(CLASS_NAME, METHOD_NAME);
		
		// Set business object as parent
		List<BusinessObject> usedInBusinessObjects = table.getUsedInBusinessObjects();
		if(usedInBusinessObjects == null || usedInBusinessObjects.isEmpty()) {
			usedInBusinessObjects = new ArrayList<BusinessObject>();
			table.setUsedInBusinessObjects(usedInBusinessObjects);
		}
		boolean isInList = false;
		for(BusinessObject businessObject : usedInBusinessObjects) {
			if(businessObject.getName().equals(parent.getName())) {
				isInList = true;
				break;
			}
		}
		if(!isInList) {
			usedInBusinessObjects.add(parent);
		}
		
		// Add table to business object
		List<Table> children = parent.getTables();
		if(children == null) {
			children = new ArrayList<Table>();
			parent.setTables(children);
		}
		isInList = false;
		for(Table child : children) {
			if(child.getName().equals(table.getName())) {
				isInList = true;
				break;
			}
		}
		if(!isInList) {
			children.add(table);
		}
		
		logger.exiting(CLASS_NAME, METHOD_NAME);
	}

	private Table createTable(String tableName, String tableDescription, Date creationDate) {
		// Creates an returns a table without persisting
		final String METHOD_NAME = "createTable(String tableName, String tableDescription, Date creationDate)";
		logger.entering(CLASS_NAME, METHOD_NAME);
		
		// Prevent null values
		if(tableName == null) {
			tableName = "";
		}
		if(tableDescription == null) {
			tableDescription = "";
		}
		if(creationDate == null) {
			creationDate = new Date();
		}
		
		if(!transientMode) {
			// Update counterTables from BphCsvImporterThread
			BphAbstractImporterThread.counterTables++;
		}
		Table newTable = new Table();
		newTable.setName(tableName);
		newTable.setDescription(tableDescription);
		newTable.setUpdated(creationDate);
		newTable.setType(Resources.BPH_TYPE_TABLE);
		
		logger.exiting(CLASS_NAME, METHOD_NAME);
		return newTable;
	}
	
	@Override
	protected void updateTransientModeEnabled() {
		switch(BphAbstractImporterThread.importType) {
			case CSV_BOS:
			case CSV_COMPLETE:
			case CSV_TABLES:
				transientMode = false;
				break;
			case CSV_PROCESSES:
				transientMode = true;
				break;
		}
	}
}
