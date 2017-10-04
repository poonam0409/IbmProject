package com.ibm.is.sappack.cw.app.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.ibm.is.sappack.cw.app.data.bdr.FieldUsageUseMode;
import com.ibm.is.sappack.cw.app.data.bdr.jpa.Field;
import com.ibm.is.sappack.cw.app.data.bdr.jpa.FieldUsage;
import com.ibm.is.sappack.cw.app.data.bdr.jpa.TableUsage;
import com.ibm.is.sappack.cw.app.services.bdr.BphImportType;
import com.ibm.is.sappack.cw.app.services.bdr.threads.BphAbstractImporterThread;

public class ImportFieldUsageProvider extends ImportAbstractProvider {

	private static ImportFieldUsageProvider instanceOfFieldUsageProvider;
	private final String CLASS_NAME = ImportFieldUsageProvider.class.getName();
	private static TableUsage lastUpdatedParent = null;

	public ImportFieldUsageProvider() {
		super();
	}

	public static synchronized ImportFieldUsageProvider getInstance() {
		if (instanceOfFieldUsageProvider == null) {
			instanceOfFieldUsageProvider = new ImportFieldUsageProvider();
		}
		lastUpdatedParent = null;
		return instanceOfFieldUsageProvider;
	}

	/**
	 * 
	 * @param field
	 *            : parent field
	 * @param tableUsage
	 *            : parent TableUsage
	 * @param fieldUsageComment
	 *            : will be set in case of creating a new FieldUsage
	 * @param fieldUsageRequired
	 *            : will be set in case of creating a new FieldUsage
	 * @param fieldUseMode
	 *            : will be set in case of creating a new FieldUsage
	 * @return: Newly created or existing usage
	 */
	public FieldUsage provideFieldUsage(Field field, TableUsage tableUsage, String fieldUsageComment, String fieldUsageRequired, String fieldUseMode, String globalTemplate) {
		final String METHOD_NAME = "provideFieldUsage(Field field, TableUsage tableUsage, String fieldUsageComment, String fieldUsageRequired, String fieldUseMode, String globalTemplate)";
		logger.entering(CLASS_NAME, METHOD_NAME);

		// Check for valid parents
		if (field == null || tableUsage == null) {
			// The parents are invalid
			throw new IllegalArgumentException("Passed Field and/or TableUsage is null.");
		}
		
		logger.fine("table usage: " + tableUsage.getFullName());

		updateTransientModeEnabled();

		Collection<FieldUsage> usagesFromTableUsage = tableUsage.getFieldUsages();
		FieldUsage fieldUsage = findFieldUsageInList(field.getName(), usagesFromTableUsage);

		boolean madeChanges = false;
		if (fieldUsage == null) {
			fieldUsage = createFieldUsage(field.getName(), field.getDescription(), fieldUsageComment, fieldUsageRequired, fieldUseMode, globalTemplate);
			madeChanges = true;
		} else {
			if (BphAbstractImporterThread.overwrite) {
				if (!fieldUsageComment.isEmpty() && !fieldUsageComment.equals(fieldUsage.getComment())) {
					fieldUsage.setComment(fieldUsageComment);
					logger.finest("comment is different");
					madeChanges = true;
				}
				if (Boolean.parseBoolean(fieldUsageRequired) != fieldUsage.getRequired()) {
					fieldUsage.setRequired(Boolean.parseBoolean(fieldUsageRequired));
					logger.finest("required is different");
					madeChanges = true;
				}
				FieldUsageUseMode mode = FieldUsageUseMode.fromString(fieldUseMode);
				if (mode != fieldUsage.getUseMode()) {
					fieldUsage.setUseMode(mode);
					logger.finest("mode is different");
					madeChanges = true;
				}
				if (globalTemplate != fieldUsage.getGlobalTemplate()) {
					fieldUsage.setGlobalTemplate(globalTemplate);
					logger.finest("GlobalTemplate is different");
					madeChanges = true;
				}
			}
		}
		if (madeChanges) {
			setRelations(field, tableUsage, fieldUsage);
			if (!transientMode) {
				manager.persist(fieldUsage);
				if (lastUpdatedParent == null || !lastUpdatedParent.equals(tableUsage)) {
					BphAbstractImporterThread.counterTableUsages++;
				}
				lastUpdatedParent = tableUsage;
			}
		}

		logger.exiting(CLASS_NAME, METHOD_NAME);
		return fieldUsage;
	}

	private void setRelations(Field field, TableUsage tableUsage, FieldUsage fieldUsage) {
		final String METHOD_NAME = "setRelations(Field field, TableUsage tableUsage, FieldUsage fieldUsage)";
		logger.entering(CLASS_NAME, METHOD_NAME);

		// Set parents
		if (fieldUsage.getField() == null) {
			fieldUsage.setField(field);
			fieldUsage.setTableUsage(tableUsage);
		}

		Collection<FieldUsage> usagesFromTableUsage = tableUsage.getFieldUsages();
		List<FieldUsage> usagesFromField = field.getUsages();

		if (usagesFromTableUsage == null) {
			usagesFromTableUsage = new ArrayList<FieldUsage>();
			tableUsage.setFieldUsages(usagesFromTableUsage);
		}
		if (usagesFromField == null) {
			usagesFromField = new ArrayList<FieldUsage>();
			field.setUsages(usagesFromField);
		}

		for (FieldUsage usageFromTableUsage : usagesFromTableUsage) {
			if (usageFromTableUsage.getName().equals(field.getName())) {
				for (FieldUsage usageFromField : usagesFromField) {
					if (usageFromField.getName().equals(field.getName())) {
						// FieldUsage exists. Don't add the usage to any list.
						logger.exiting(this.CLASS_NAME, METHOD_NAME);
						return;
					}
				}
			}
		}

		// FieldUsage didn't exist yet, so add the Usage to all lists
		usagesFromTableUsage.add(fieldUsage);
		if (!BphAbstractImporterThread.importType.getImportType().equals(BphImportType.CSV_BOS.getImportType())
				&& !BphAbstractImporterThread.importType.getImportType().equals(BphImportType.CSV_PROCESSES.getImportType())) {
			usagesFromField.add(fieldUsage);
		}
		logger.exiting(CLASS_NAME, METHOD_NAME);
	}

	private FieldUsage findFieldUsageInList(String name, Collection<FieldUsage> usagesFromTableUsage) {
		final String METHOD_NAME = "findFieldUsageInList(String name, List<FieldUsage> usageList, Collection<FieldUsage> usagesFromTableUsage)";
		logger.entering(CLASS_NAME, METHOD_NAME);

		for (FieldUsage usageFromTableUsage : usagesFromTableUsage) {
			if (usageFromTableUsage.getName().equals(name)) {
				// field usage exists and is linked from table usage
				return usageFromTableUsage;
			}
		}

		logger.exiting(CLASS_NAME, METHOD_NAME);
		return null;
	}

	private FieldUsage createFieldUsage(String fieldName, String fieldDescription, String fieldUsageComment, String fieldUsageRequired, String fieldUseMode, String globalTemplate) {
		final String METHOD_NAME = "createFieldUsage(String fieldName, String fieldDescription, String fieldUsageComment, String fieldUsageRequired, String fieldUseMode)";
		logger.entering(this.CLASS_NAME, METHOD_NAME);
		// Creates and returns a FieldUsage with given parameters

		// Prevent null values
		if (fieldName == null) {
			fieldName = "";
		}
		if (fieldDescription == null) {
			fieldDescription = "";
		}
		if (fieldUsageComment == null) {
			fieldUsageComment = "";
		}

		FieldUsage fieldUsage = new FieldUsage();
		fieldUsage.setName(fieldName);
		fieldUsage.setDescription(fieldDescription);
		fieldUsage.setComment(fieldUsageComment);
		fieldUsage.setRequired(Boolean.parseBoolean(fieldUsageRequired));
		fieldUsage.setUseMode(FieldUsageUseMode.fromString(fieldUseMode));
		fieldUsage.setGlobalTemplate(globalTemplate);

		logger.exiting(CLASS_NAME, METHOD_NAME);
		return fieldUsage;
	}

	@Override
	protected void updateTransientModeEnabled() {
		switch (BphAbstractImporterThread.importType) {
		case CSV_COMPLETE:
			transientMode = false;
			break;
		case CSV_BOS:
		case CSV_PROCESSES:
		case CSV_TABLES:
			transientMode = true;
			break;
		}
	}
}
