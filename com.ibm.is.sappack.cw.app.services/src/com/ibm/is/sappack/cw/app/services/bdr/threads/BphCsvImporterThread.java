package com.ibm.is.sappack.cw.app.services.bdr.threads;

import java.util.ArrayList;
import java.util.Arrays;

import javax.servlet.http.HttpSession;

import org.apache.wink.json4j.JSONException;

import com.ibm.is.sappack.cw.app.data.bdr.jpa.BusinessObject;
import com.ibm.is.sappack.cw.app.data.bdr.jpa.Field;
import com.ibm.is.sappack.cw.app.data.bdr.jpa.Process;
import com.ibm.is.sappack.cw.app.data.bdr.jpa.ProcessStep;
import com.ibm.is.sappack.cw.app.data.bdr.jpa.Table;
import com.ibm.is.sappack.cw.app.data.bdr.jpa.TableUsage;
import com.ibm.is.sappack.cw.app.services.Constants;
import com.ibm.is.sappack.cw.app.services.Util;
import com.ibm.is.sappack.cw.app.services.bdr.BphImportType;
import com.ibm.is.sappack.cw.app.services.bdr.CsvProcessStep;
import com.ibm.is.sappack.cw.app.services.bdr.CsvTableUsage;
import com.ibm.websphere.webmsg.publisher.Publisher;

public class BphCsvImporterThread extends BphAbstractImporterThread {
	
	private final String CLASS_NAME = BphCsvImporterThread.class.getName();
	
	private String[] header; // First line in CSV file that defines the structure

	public BphCsvImporterThread(Publisher publisher, HttpSession session) {
		super(publisher, session);
	}

	@Override
	void updateStatus() {
		final String METHOD_NAME = "updateStatus()";
		this.logger.entering(this.CLASS_NAME, METHOD_NAME);
		
		// Only update if there's not already a message set
		if (this.statusInfo.size() == 0) {
			if (this.cancelled) {
				// Thread has been cancelled
				this.statusInfo.add(Constants.BDR_IMPORT_ERROR_THREAD_CANCELLED);
			} else if (counterProcessesAndSteps > 0 || counterBusinessObjects > 0 || counterTables > 0 || counterTableUsages > 0) {
				if (counterProcessesAndSteps > 0 && importType.getImportType().equalsIgnoreCase(BphImportType.CSV_COMPLETE.getImportType())) {
					// Successfully imported from CSV when tree was empty
					this.statusInfo.add(Constants.BDR_IMPORT_SUCCESS_CSV_EMPTY);
				} else if (counterProcessesAndSteps > 0 && importType.getImportType().equalsIgnoreCase(BphImportType.CSV_PROCESSES.getImportType())) {
					// Imported BPH from CSV file
					this.statusInfo.add(Constants.BDR_IMPORT_SUCCESS_CSV_PROCESSES);
				} else {
					// Successfully imported from CSV when tree had content before
					this.statusInfo.add(Constants.BDR_IMPORT_SUCCESS_CSV_BOS);
				}
				// Add counters to list
				this.statusInfo.add("" + counterProcessesAndSteps);
				this.statusInfo.add("" + counterBusinessObjects);
				this.statusInfo.add("" + counterTables);
				this.statusInfo.add("" + counterTableUsages);
			} else {
				// No processes, BOs, tables or table usages have been added
				this.statusInfo.add(Constants.BDR_IMPORT_SUCCESS_NO_CHANGES);
			}
		}
		this.logger.exiting(CLASS_NAME, METHOD_NAME);
	}

	@Override
	void handleImport(String[] lines) throws JSONException {
		final String METHOD_NAME = "handleImport(String[] lines)";
		this.logger.entering(this.CLASS_NAME, METHOD_NAME);
		
		// TODO: detect the file kind (BDR or tables) and display appropriate error message to the user if he uses the wrong option
		// Probably offer to still import
//		String header = lines[0];
//		int numColumns = header.split("\"" + Constants.BDR_EXPORT_CSV_PATH_SEPARATOR + "\"").length;
//		if (numColumns == Constants.BDR_TABLES_EXPORT_WIDTH) {
//			
//		}
		
		if (importType == BphImportType.CSV_TABLES) {
			handleTableMetadataImport(lines);
		} else {
			handleBDRImport(lines);
		}
		
		this.logger.exiting(this.CLASS_NAME, METHOD_NAME);
	}
	
	void handleTableMetadataImport(String[] lines) {
		final String METHOD_NAME = "handleTableMetadataImport(String[] lines)";
		this.logger.entering(this.CLASS_NAME, METHOD_NAME);
		try{
		for (String line : lines){
			if (line == lines[0]){
				continue;
			}
			logger.finest("Line to extract table and fields from: " + line);

			String[] lineValues = splitLine(line);
			
			String tableName = lineValues[0];
			String tableDescription = lineValues[1];
			
			// create the table if necessary
			Table table = importTableProvider.provideTable(tableName, tableDescription, creationDate);
			
			String fieldName = lineValues[2];
			String fieldCheckTable = lineValues[3];
			String fieldRecommended = lineValues[4];
			String fieldSapView = lineValues[5];
			String fieldDescription = lineValues[6];
			
			// create or update the field
			if (!fieldName.equals("")) {
				importFieldProvider.provideField(table, fieldName, fieldDescription, fieldCheckTable, fieldRecommended, fieldSapView);
			}
		}}catch(Exception e)
		{
			Util.throwInternalErrorToClient(e);
		}
		
		this.logger.exiting(this.CLASS_NAME, METHOD_NAME);
	}
	
	void handleBDRImport(String[] lines) throws JSONException {
		final String METHOD_NAME = "handleBDRImport(String[] lines)";
		logger.entering(this.CLASS_NAME, METHOD_NAME);
		
		// Parse all processes and steps first
		CsvProcessStep[] csvProcessStepArray = parseProcessSteps(lines);
		logger.fine("Number of CsvProcessSteps: " + csvProcessStepArray.length);
		for (CsvProcessStep csvProcessStep : csvProcessStepArray) {
			logger.fine("CsvProcessStep: " + csvProcessStep.getProcessStep().getName());
			// The step contains the path such as its content
			CsvTableUsage[] csvTableUsageArray = parseTableUsage(csvProcessStep);
			logger.fine("Number of CsvTableUsages: " + csvTableUsageArray.length);
			for (CsvTableUsage csvTableUsage : csvTableUsageArray) {
				// Leaf almost reached. Remaining objects will be created and persisted here.
				importTableUsage(csvTableUsage);
			}
		}
		
		logger.exiting(this.CLASS_NAME, METHOD_NAME);
	}

	private void importTableUsage(CsvTableUsage csvTableUsage) {
		final String METHOD_NAME = "importTableUsage(CsvTableUsage csvTableUsage)";
		logger.entering(this.CLASS_NAME, METHOD_NAME);
		
		logger.fine("Table usage: " + csvTableUsage.getTableUsage().getFullName());
		/*
		 * Remaining content to parse in line (by given order):
		 * "FIELD";
		 * "FIELD REQUIRED";
		 * "FIELD USE MODE";
		 * "FIELD USAGE COMMENT";
		 * "FIELD CHECK TABLE";
		 * "FIELD RECOMMENDED";
		 * "FIELD SAP VIEW";
		 * "FIELD DESCRIPTION"
		 */
		
		// Define positions of the objects to parse.
		final int positionFieldName = 0;
		final int positionFieldUsageRequired = 1;
		final int positionFieldUseMode = 2;
		final int positionFieldUsageComment = 3;
		final int positionFieldCheckTable = 4;
		final int positionFieldRecommended = 5;
		final int positionFieldSapView = 6;
		final int positionFieldDescription = 7;
		final int positionGlobalTemplate = 8;
		
		String fieldName = "";
		String fieldUsageRequired = "";
		String fieldUseMode = "";
		String fieldUsageComment = "";
		String fieldCheckTable = "";
		String fieldRecommended = "";
		String fieldSapView = "";
		String fieldDescription = "";
		String globalTemplate = "";
		
		for (String[] line : csvTableUsage.getContent()) {
			if (line[positionFieldName].equals(Constants.BDR_EXPORT_GAP_FILLER)) {
				// The value is empty. No field exists.
				continue;
			}
			// logger.finest("Line to parse: "+line.toString()); // logs the object ID, not the strings
			
			// Get field/-Usage strings from line
			fieldName = line[positionFieldName];
			fieldUsageRequired = line[positionFieldUsageRequired];
			fieldUseMode = line[positionFieldUseMode];
			fieldUsageComment = line[positionFieldUsageComment];
			fieldCheckTable = line[positionFieldCheckTable];
			fieldRecommended = line[positionFieldRecommended];
			fieldSapView = line[positionFieldSapView];
			fieldDescription = line[positionFieldDescription];
			globalTemplate = line[positionGlobalTemplate];
			
			// Get table from csvTableUsage
			Table table = csvTableUsage.getTableUsage().getTable();
			
			// Provide field
			Field field = importFieldProvider.provideField(table, fieldName, fieldDescription, fieldCheckTable, fieldRecommended, fieldSapView);
			
			// Create FieldUsage
			importFieldUsageProvider.provideFieldUsage(field, csvTableUsage.getTableUsage(), fieldUsageComment, fieldUsageRequired, fieldUseMode, globalTemplate);
		}
		
		logger.exiting(this.CLASS_NAME, METHOD_NAME);
	}

	private CsvTableUsage[] parseTableUsage(CsvProcessStep csvProcessStep) {
		final String METHOD_NAME = "parseTableUsage(CsvProcessStep csvProcessStep)";
		logger.entering(this.CLASS_NAME, METHOD_NAME);
		/*
		 * Remaining content to parse in line (by given order):
		 * "BUSINESS OBJECT";
		 * "BUSINESS OBJECT SHORTNAME";
		 * "BUSINESS OBJECT DESCRIPTION";
		 * "TABLE";
		 * "TABLE DESCRIPTION";
		 * "TABLE USAGE APPROVAL STATUS"; <-- End parsing at this point
		 * "FIELD";
		 * "FIELD REQUIRED";
		 * "FIELD USE MODE";
		 * "FIELD USAGE COMMENT";
		 * "FIELD CHECK TABLE";
		 * "FIELD RECOMMENDED";
		 * "FIELD SAP VIEW";
		 * "FIELD DESCRIPTION"
		 */
		
		ArrayList<CsvTableUsage> csvTableUsageList = new ArrayList<CsvTableUsage>();
		CsvTableUsage csvTableUsage = null;
		Table previousTable = null;
		// Define positions of the objects to parse.
		final int positionBusinessObjectName = 0;
		final int positionBusinessObjectShortName = 1;
		final int positionBusinessObjectDescription = 2;
		final int positionTable = 3;
		final int positionTableDescription = 4;
		final int positionTableUsageApprovalStatus = 5;
		
		for (String[] line : csvProcessStep.getContent()) {
			//logger.finest("Line to extract BO and Table from: " + line.toString()); // logs object ID, not the strings

			// Handle BusinessObject
			String boName = line[positionBusinessObjectName];
			if (boName.equals(Constants.BDR_EXPORT_GAP_FILLER)) {
				// The value is empty. Continue with next line
				continue;
			}
			String boShortName = line[positionBusinessObjectShortName];
			String boDescription = line[positionBusinessObjectDescription];
			BusinessObject businessObject = importBusinessObjectProvider.provideBusinessObject(csvProcessStep.getProcessStep(), boName, boShortName,
					boDescription, creationDate);
			
			logger.fine("BO: " + businessObject.getName());

			// Handle Table
			String tableName = line[positionTable];
			if (tableName.equals(Constants.BDR_EXPORT_GAP_FILLER)) {
				// The value is empty. Continue with next line
				continue;
			}
			String tableDescription = line[positionTableDescription];
			Table table = importTableProvider.provideTable(businessObject, tableName, tableDescription, creationDate);

			// Provide TableUsage and add to list if it's a new one
			String tableUsageApprovalStatus = line[positionTableUsageApprovalStatus];
			TableUsage tableUsage = importTableUsageProvider.provideTableUsage(csvProcessStep.getProcessStep(), businessObject, table,
					tableUsageApprovalStatus, creationDate);

			// Check uniqueness, if so add to tableUsageList. Always add content to csvTableUsage
			if (previousTable == null || !table.getName().equals(previousTable.getName())) {
				csvTableUsage = new CsvTableUsage(tableUsage);
				csvTableUsageList.add(csvTableUsage);
			}
			// Add Field and FieldUsage information to content
			String[] content = Arrays.copyOfRange(line, (positionTableUsageApprovalStatus + 1), line.length);
			csvTableUsage.getContent().add(content);
			previousTable = table;
		}
		
		this.logger.exiting(this.CLASS_NAME, METHOD_NAME);
		return csvTableUsageList.toArray(new CsvTableUsage[csvTableUsageList.size()]);
	}

	private CsvProcessStep[] parseProcessSteps(String[] lines) throws JSONException {
		final String METHOD_NAME = "parseProcessSteps(String[] lines)";
		this.logger.entering(this.CLASS_NAME, METHOD_NAME);
		
		ArrayList<CsvProcessStep> csvProcessStepList = new ArrayList<CsvProcessStep>();
		ProcessStep previousProcessStep = null;
		ProcessStep processStep = null;
		CsvProcessStep csvProcessStep = null;
		
		// Define where to find which field
		final int POSITION_STEP_NAME = header.length - Constants.BDR_EXPORT_WIDTH_PROCESSSTEPS_TO_FIELDS;
		final int POSITION_STEP_DESCRIPTION = POSITION_STEP_NAME + 1;
		final int POSITION_TRANSACTIONS = POSITION_STEP_DESCRIPTION + 1;
		final int POSITION_REMAINING_VALUES = POSITION_TRANSACTIONS + 1;
		
		for (String line : lines) {
			// Skip header
			if (line == lines[0]) {
				continue;
			}
			
			logger.finer("Line to extract CsvProcessStep from: " + line);
			
			String[] lineValues = splitLine(line);
			
			// Get the direct parent process of the step
			Process parent = provideDeepestProcess(lineValues);
			
			// Only create step if the value is not empty
			if (!lineValues[POSITION_STEP_NAME].equals(Constants.BDR_EXPORT_GAP_FILLER)) {
				// Get new or existing process step
				processStep = importProcessStepProvider.provideProcessStep(parent, lineValues[POSITION_STEP_NAME], lineValues[POSITION_STEP_DESCRIPTION],
						creationDate, lineValues[POSITION_TRANSACTIONS]);

				// Check whether the process step has already been created.
				if (previousProcessStep == null || !processStepsAreEqual(previousProcessStep, processStep)) {
					logger.finer("Creating new CsvProcessStep " + processStep.getName());
					csvProcessStep = new CsvProcessStep(processStep);
					csvProcessStepList.add(csvProcessStep);
				}
				// Add content (remaining objects from line) to csvProcessStep
				String[] content = Arrays.copyOfRange(lineValues, POSITION_REMAINING_VALUES, lineValues.length);
				csvProcessStep.getContent().add(content);
				// Update previousCsvProcessStep
				previousProcessStep = processStep;
			}
		}
		this.logger.exiting(this.CLASS_NAME, METHOD_NAME);
		return csvProcessStepList.toArray(new CsvProcessStep[csvProcessStepList.size()]);
	}

	private String[] splitLine(String line) {
		// Split line by expression. The second parameter is needed to split even when
		// the split part would be an empty string
		// Example: line = ';;process;description;object name;field;'
		// line.split(";", -1) returns: ["","","process","description","object name","field",""] 
		// line.split(";") returns something like: ["","","process","description","object name","field"]
		String[] lineValues = line.split("\"" + Constants.BDR_EXPORT_CSV_PATH_SEPARATOR + "\"");
		
		// Remove leading quote
		lineValues[0] = lineValues[0].substring(1);
		// Remove trailing quote (but check if it's a quote to be sure)
		String lastValue = lineValues[lineValues.length - 1];
		if (!lastValue.isEmpty() && "\"".equals(lastValue.substring(lastValue.length() - 1))) {
			lastValue = lastValue.substring(0, lastValue.length() - 1);
			lineValues[lineValues.length - 1] = lastValue;
		}
		
		// Collapse double "double quotes" ("") since this is how we (and Excel) represent a single double quote character
		for (int i = 0; i < lineValues.length; i++) {
			logger.finest("Value: " + lineValues[i]);
			lineValues[i] = lineValues[i].replace("\"\"", "\"");
		}
		return lineValues;
	}

	public static boolean processStepsAreEqual(ProcessStep step, ProcessStep stepToCompare) {
		
		if(step.getProcessStepId() != null && stepToCompare.getProcessStepId() != null) {
			// Both have an ID from JPA so use the standard equals-method
			return step.equals(stepToCompare);
		}
		// ProcessSteps are also equal when their process hierarchy path
		// and name are the same.
		
		// The name will be tested first
		if(!step.getName().equals(stepToCompare.getName())) {
			return false;
		}
		
		// Compare paths
		Process stepParent = step.getParentProcess();
		Process stepToCompareParent = stepToCompare.getParentProcess();
		// Compare each upper process till one (or both) reached the top level process.
		if(!processPathsAreEqual(stepParent, stepToCompareParent)) {
			return false;
		}
		// The paths of both steps are equal such as their names,
		// hence they both are equal.
		return true;
	}

	public static boolean processPathsAreEqual(Process process, Process processToCompare) {
		
		if(process == null && processToCompare == null) {
			// Since both are null, they are equal
			return true;
		}
		if((process == null && processToCompare != null)
				|| (process != null && processToCompare == null)) {
			// One is null and the other not, so they can't be equal
			return false;
		}
		// Both are not null
		if(!process.getName().equals(processToCompare.getName())) {
			// Names aren't equal
			return false;
		}
		// Continue recursion
		return processPathsAreEqual(process.getParentProcess(), processToCompare.getParentProcess());
	}

	private Process provideDeepestProcess(String[] lineValues) {
		final String METHOD_NAME = "getDeepestProcess(String[] lineValues)";
		this.logger.entering(this.CLASS_NAME, METHOD_NAME);
		
		// lineValues will be split where the process path ends to only iterate over processes
		int processDepth = lineValues.length - Constants.BDR_EXPORT_WIDTH_PROCESSSTEPS_TO_FIELDS;
		logger.finer("Process depth: " + processDepth);
		String[] processValues = Arrays.copyOfRange(lineValues, 0, processDepth);
		//logger.finer("Processes (name, description) to parse: " + processValues.toString()); // logs the object ID, not the strings
		Process parent = null;
		String name, description;
		
		for (int i = 0; i < processValues.length; i += 2) {
			// Parse ("PROCESS","PROCESS DESCRIPTION") value pairs
			name = processValues[i];
			description = processValues[i + 1];
			logger.finest("Name and description of process " + i + " '" + name + ", " + description + "'");

			// Processes with empty name won't be created
			if (!name.equals(Constants.BDR_EXPORT_GAP_FILLER)) {
				if (parent == null) {
					logger.finest("Parsing top level process '" + name + "'");
					// First Iteration. Get top level process
					parent = importProcessProvider.provideTopLevelProcess(name, description, creationDate);
				} else {
					logger.finest("Parsing subprocess '" + name + "' at level " + i);
					parent = importProcessProvider.provideProcess(parent, name, description, creationDate);
				}
			}
		}
		
		this.logger.exiting(this.CLASS_NAME, METHOD_NAME);
		return parent;
	}

	@Override
	boolean checkValidationAndSetStatus(String[] lines) {
		/*
		 * This method checks for the following conditions:
		 * 1) the file is not empty
		 * 2) there are at least two lines of content
		 * 3) the header is valid
		 * 4) all lines have the same number of columns
		 * 
		 * Other checks would be too complicated and the time consumption would be too high.
		 * 
		 * Example valid header:
		 * "PROCESS";
		 * "PROCESS DESCRIPTION";
		 * "PROCESS STEP";
		 * "PROCESS STEP DESCRIPTION";
		 * "TRANSACTIONS";
		 * "BUSINESS OBJECT";
		 * "BUSINESS OBJECT SHORTNAME";
		 * "BUSINESS OBJECT DESCRIPTION";
		 * "TABLE";
		 * "TABLE DESCRIPTION";
		 * "TABLE USAGE APPROVAL STATUS";
		 * "FIELD";
		 * "FIELD REQUIRED";
		 * "FIELD USE MODE";
		 * "FIELD USAGE COMMENT";
		 * "FIELD CHECK TABLE";
		 * "FIELD RECOMMENDED";
		 * "FIELD SAP VIEW";
		 * "FIELD DESCRIPTION"
		 * 
		 */
		final String METHOD_NAME = "checkValidationAndSetStatus(String[] lines)";
		this.logger.entering(this.CLASS_NAME, METHOD_NAME);
		
		// 1), 2)
		// Ensure lines has useful content
		if(lines == null || lines.length < 2) {
			this.statusInfo.add(Constants.BDR_IMPORT_ERROR_INVALID_FILE);
			logger.finer("File is empty or doesn't have at least two lines of content.");
			this.logger.exiting(this.CLASS_NAME, METHOD_NAME);
			return false;
		}
		
		// Check if header is valid
		this.header = splitLine(lines[0]);
		
		logger.finer("First line from CSV: " + lines[0]);
		// Minimum header length is defined by all Objects (Step, BO, Table,...)
		// with their description and attributes such as a minimum of one process
		// with its description. That's why two is added to the constant.
		
		int minLength;
		if (importType == BphImportType.CSV_TABLES){
			minLength = Constants.BDR_TABLES_EXPORT_WIDTH;
		} else {
			minLength = Constants.BDR_EXPORT_WIDTH_PROCESSSTEPS_TO_FIELDS + 2;
		}
		
		if(header == null || header.length < minLength) {
			this.statusInfo.add(Constants.BDR_IMPORT_ERROR_INVALID_HEADER);
			logger.finer((header == null) ? "Header is null" : "Header length is too short. Expected: " + minLength
					+ ", actually: " + header.length);
			this.logger.exiting(this.CLASS_NAME, METHOD_NAME);
			return false;
		}
		
		// 3)
		// All the values are mandatory for import. The order in the array
		// is exactly the same as it should be in the CSV file.
		// Import will be cancelled if the order isn't the same, columns are missing
		// or unexpected columns appear. This is only the fix part.
		
		String[] searchNames;
		// check if it's table metadata or bdr import
		if(importType != BphImportType.CSV_TABLES){
			searchNames = new String[Constants.BDR_EXPORT_WIDTH_PROCESSSTEPS_TO_FIELDS];
			searchNames[0] = Constants.BDR_EXPORT_PROCESS_STEP;
			searchNames[1] = Constants.BDR_EXPORT_PROCESS_STEP_DESC;
			searchNames[2] = Constants.BDR_EXPORT_TRANSACTIONS;
			searchNames[3] = Constants.BDR_EXPORT_BUSINESS_OBJECT_NAME;
			searchNames[4] = Constants.BDR_EXPORT_BUSINESS_OBJECT_SHORTNAME;
			searchNames[5] = Constants.BDR_EXPORT_BUSINESS_OBJECT_DESC;
			searchNames[6] = Constants.BDR_EXPORT_TABLE_NAME;
			searchNames[7] = Constants.BDR_EXPORT_TABLE_DESC;
			searchNames[8] = Constants.BDR_EXPORT_TABLE_USAGE_APPROVAL_STATUS;
			searchNames[9] = Constants.BDR_EXPORT_FIELD_NAME;
			searchNames[10] = Constants.BDR_EXPORT_FIELD_REQUIRED;
			searchNames[11] = Constants.BDR_EXPORT_FIELD_USE_MODE;
			searchNames[12] = Constants.BDR_EXPORT_FIELD_USAGE_COMMENT;
			searchNames[13] = Constants.BDR_EXPORT_FIELD_CHECK_TABLE;
			searchNames[14] = Constants.BDR_EXPORT_FIELD_RECOMMENDED;
			searchNames[15] = Constants.BDR_EXPORT_FIELD_SAP_VIEW;
			searchNames[16] = Constants.BDR_EXPORT_FIELD_DESC;
			searchNames[17] = Constants.BDR_EXPORT_GT;
		} else {
			searchNames = new String[Constants.BDR_TABLES_EXPORT_WIDTH];
			searchNames[0] = Constants.BDR_EXPORT_TABLE_NAME;
			searchNames[1] = Constants.BDR_EXPORT_TABLE_DESC;
			searchNames[2] = Constants.BDR_EXPORT_FIELD_NAME;
			searchNames[3] = Constants.BDR_EXPORT_FIELD_CHECK_TABLE;
			searchNames[4] = Constants.BDR_EXPORT_FIELD_RECOMMENDED;
			searchNames[5] = Constants.BDR_EXPORT_FIELD_SAP_VIEW;
			searchNames[6] = Constants.BDR_EXPORT_FIELD_DESC;
		}
		
		// In case that we have table export mode the header must always have exactly the same length 
		if (importType == BphImportType.CSV_TABLES){
			if(header.length != Constants.BDR_TABLES_EXPORT_WIDTH){
				this.statusInfo.add(Constants.BDR_IMPORT_ERROR_INVALID_HEADER);
				logger.finer("The length of the header is invalid for metadata import");
				this.logger.exiting(this.CLASS_NAME, METHOD_NAME);
				return false;
			}
		}
		
		// Perform mandatory value check
		//if the import mode is not tables metadata import check if the process part is legitimate 
		if(importType != BphImportType.CSV_TABLES){
			int processWidth = header.length - Constants.BDR_EXPORT_WIDTH_PROCESSSTEPS_TO_FIELDS;
			if((processWidth % 2) != 0) {
				// It must be even, because there are always "Process";"Process Description" pairs
				logger.finer("Process Hierarchy is invalid.");
				logger.finer("There is at least one item whithout a partner");
				this.statusInfo.add(Constants.BDR_IMPORT_ERROR_INVALID_PROCESS_HIERARCHY);
				this.logger.exiting(this.CLASS_NAME, METHOD_NAME);
				return false;
			}
			// Check processes
			for(int i = 0; i < processWidth; i+=2) {
				if(!header[i].equals(Constants.BDR_EXPORT_PROCESS_NAME)
						|| !header[i+1].equals(Constants.BDR_EXPORT_PROCESS_DESC)) {
					// Header is invalid
					logger.finer("Process Hierarchy is invalid.");
					logger.finer("Non-conform header");
					this.statusInfo.add(Constants.BDR_IMPORT_ERROR_INVALID_HEADER);
					this.logger.exiting(this.CLASS_NAME, METHOD_NAME);
					return false;
				}
			}
			// Check remaining objects (fix part)
			for(int i = processWidth; i < header.length; i++) {
				if(!header[i].equals(searchNames[(i-processWidth)])) {
					logger.finer("Fix object part (Steps to Fields) is invalid.");
					this.statusInfo.add(Constants.BDR_IMPORT_ERROR_INVALID_FILE);
					this.logger.exiting(this.CLASS_NAME, METHOD_NAME);
					return false;
				}
			}
		}
		
		// 4)
		// Check every line for its length.
		// It must be the same as header.length.
		for (String line : lines) {
			String[] values = splitLine(line);
			if (values.length != header.length && !line.equals("")) {
				this.statusInfo.add(Constants.BDR_IMPORT_ERROR_INVALID_DATA_FILE);
				logger.finer("Line " + line + " doesn't fit the expected length (columns).");
				logger.finer("Header length: " + header.length);
				for (int i = 0; i < header.length; i++) {
					logger.finer(header[i]);
				}
				logger.finer("Line length: " + values.length);
				for (int i = 0; i < values.length; i++) {
					logger.finer(values[i]);
				}
				this.logger.exiting(this.CLASS_NAME, METHOD_NAME);
				return false;
			}
		}
		
		this.logger.exiting(this.CLASS_NAME, METHOD_NAME);
		return true;
	}
}