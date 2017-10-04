package com.ibm.is.sappack.cw.app.services;

import java.util.ArrayList;
import java.util.List;
import com.ibm.is.sappack.cw.app.data.bdr.jpa.Field;
import com.ibm.is.sappack.cw.app.data.bdr.jpa.Table;
import com.ibm.is.sappack.cw.app.services.bdr.threads.BphAbstractImporterThread;

public class ImportFieldProvider extends ImportAbstractProvider {
	
	private static ImportFieldProvider importFieldProvider;
	private final String CLASS_NAME = ImportFieldProvider.class.getName();
	private static Table lastUpdatedParent;
	
	private ImportFieldProvider() {
		super();
	}
	
	public static synchronized ImportFieldProvider getInstance() {
		if(importFieldProvider == null) {
			importFieldProvider = new ImportFieldProvider();
		}
		lastUpdatedParent = null;
		return importFieldProvider;
	}

	/**
	 * @param parent: the parent Table
	 * @param fieldName: name to search for
	 * @param fieldDescription: will be set in case of creation
	 * @param fieldCheckTable: will be set in case of creation
	 * @param fieldRecommended: will be set in case of creation
	 * @param fieldSapView: will be set in case of creation
	 * @return: Newly created field or existing one
	 */
	public Field provideField(Table parent, String fieldName, String fieldDescription,
			String fieldCheckTable, String fieldRecommended, String fieldSapView) {
		final String METHOD_NAME = "provideField(Table parent, String fieldName, String fieldDescription," +
				"String fieldCheckTable, String fieldRecommended, String fieldSapView)";
		logger.entering(CLASS_NAME, METHOD_NAME);
		
		// Check for valid parent
		if (parent == null) {
			// A field can't exist without a table
			throw new IllegalArgumentException("The parent is null.");
		}
		
		updateTransientModeEnabled();

		List<Field> children = parent.getFields();
		Field field = findFieldInList(fieldName, children);
		
		boolean madeChanges = false;
		if (field == null) {
			field = createField(fieldName, fieldDescription, fieldCheckTable, fieldRecommended, fieldSapView);
			madeChanges = true;
		} else {
			if (BphAbstractImporterThread.overwrite) {
				if (!fieldDescription.isEmpty() && !fieldDescription.equals(field.getDescription())) {
					field.setDescription(fieldDescription);
					madeChanges = true;
				}
				if (!fieldCheckTable.isEmpty() && !fieldCheckTable.equals(field.getCheckTable())) {
					field.setCheckTable(fieldCheckTable);
					madeChanges = true;
				}
				if (!fieldSapView.isEmpty() && !fieldSapView.equals(field.getSapView())) {
					field.setSapView(fieldSapView);
					madeChanges = true;
				}
				if (Boolean.parseBoolean(fieldRecommended) != field.getRecommended()) {
					field.setRecommended(Boolean.parseBoolean(fieldRecommended));
					madeChanges = true;
				}
			}
		}
		if (madeChanges) {
			setRelations(parent, field);
			if (!transientMode) {
				manager.joinTransaction();
				manager.persist(field);
				if (lastUpdatedParent == null || !lastUpdatedParent.equals(parent)) {
					BphAbstractImporterThread.counterTables++;
				}
				lastUpdatedParent = parent;
			}
		}
		
		logger.exiting(CLASS_NAME, METHOD_NAME);
		return field;
	}
	
	private void setRelations(Table parent, Field field) {
		final String METHOD_NAME = "setRelation(Table parent, Field field)";
		logger.entering(CLASS_NAME, METHOD_NAME);
		
		field.setTable(parent);
		// Make sure table knows the field
		List<Field> children = parent.getFields();
		if (children == null) {
			children = new ArrayList<Field>();
			parent.setFields(children);
		}
		boolean isInList = false;
		for (Field child : children) {
			if (child.getName().equals(field.getName())) {
				isInList = true;
				break;
			}
		}
		if (!isInList) {
			children.add(field);
		}
		
		logger.exiting(CLASS_NAME, METHOD_NAME);
	}

	private Field findFieldInList(String fieldName, List<Field> children) {
		final String METHOD_NAME = "findFieldInList(String fieldName, List<Field> children)";
		logger.entering(CLASS_NAME, METHOD_NAME);
		
		Field field = null;
		
		if (children != null && !children.isEmpty()) {
			// Compare each field in list with name
			for (Field existingField : children) {
				if (existingField.getName().equals(fieldName)) {
					// Match found. Field with the name exists
					field = existingField;
					break;
				}
			}
		}
		
		logger.exiting(CLASS_NAME, METHOD_NAME);
		return field;
	}

	private Field createField(String fieldName, String fieldDescription, String fieldCheckTable, String fieldRecommended, String fieldSapView) {
		// Creates an returns a field without persisting
		final String METHOD_NAME = "createField(String fieldName, String fieldDescription, String fieldCheckTable, String fieldRecommended, String fieldSapView)";
		logger.entering(CLASS_NAME, METHOD_NAME);
		
		// Prevent null values
		if (fieldName == null) {
			fieldName = "";
		}
		if (fieldDescription == null) {
			fieldDescription = "";
		}
		
		Field newField = new Field();
		newField.setName(fieldName);
		newField.setDescription(fieldDescription);
		newField.setCheckTable(fieldCheckTable);
		newField.setRecommended(Boolean.parseBoolean(fieldRecommended));
		newField.setSapView(fieldSapView);
		
		logger.exiting(CLASS_NAME, METHOD_NAME);
		return newField;
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
