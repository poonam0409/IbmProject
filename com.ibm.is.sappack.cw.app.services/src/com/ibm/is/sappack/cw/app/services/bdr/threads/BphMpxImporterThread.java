package com.ibm.is.sappack.cw.app.services.bdr.threads;

import java.util.ArrayList;

import javax.servlet.http.HttpSession;

import org.apache.wink.json4j.JSONException;

import com.ibm.is.sappack.cw.app.services.Constants;
import com.ibm.is.sappack.cw.app.services.bdr.MpxBusinessProcess;
import com.ibm.is.sappack.cw.app.services.bdr.MpxBusinessProcessStep;
import com.ibm.is.sappack.cw.app.services.bdr.MpxBusinessScenario;
import com.ibm.is.sappack.cw.app.services.bdr.MpxProject;
import com.ibm.is.sappack.cw.app.services.bdr.MpxRecord;
import com.ibm.websphere.webmsg.publisher.Publisher;

public class BphMpxImporterThread extends BphAbstractImporterThread {
	
	private final String CLASS_NAME = BphMpxImporterThread.class.getName();
	
	// Constants which represent folders in SAP Solution Manager
	private static final String SAP_BUSINESS_SCENARIO = "Business Scenarios";
	private static final String SAP_BUSINESS_PROCESS = "Business Processes";
	
	private static final String FILE_CREATION_FORMAT = "MPX";
	private static final String FILE_CREATION_SOURCE_SYSTEM = "SAP";
	
	private static final String BPH_RECORD_TASK = "70";

	private final int BUSINESS_PROCESS_PARENT_FOLDER_LEVEL = 4;
	private final int BUSINESS_PROCESS_LEVEL = 5;
	
	public BphMpxImporterThread(Publisher publisher, HttpSession session) {
		super(publisher, session);
	}

	@Override
	void handleImport(String[] lines) throws JSONException {
		final String METHOD_NAME = "handleImport(String[] lines)";
		this.logger.entering(this.CLASS_NAME, METHOD_NAME);
		
		// Read documentation on the MPX Project File Exchange Format here:
		// http://support.microsoft.com/kb/270139
		
		// We only support to import one project from one file
		MpxProject[] projectArray = parseProjectList(lines);
		if(projectArray.length > 1) {
			logger.finer("Several projects inside a MPX-file aren't supported.");
			this.statusInfo.add(Constants.BDR_IMPORT_ERROR_INVALID_FILE);
			this.logger.exiting(CLASS_NAME, METHOD_NAME);
			return;
		}
		// Iterate over projects
		for(MpxProject project : projectArray) {
			// Get Business Scenarios which are in scope
			MpxBusinessScenario[] businessScenarioArray = parseBusinessScenarios(project);
			logger.finer("businessScenarioList length = " + businessScenarioArray.length);
			for(MpxBusinessScenario businessScenario : businessScenarioArray) {
				// Get Business Processes which are in scope
				MpxBusinessProcess[] businessProcessArray = parseBusinessProcesses(businessScenario);
				logger.finer("businessProcessList length = " + businessProcessArray.length);
				for(MpxBusinessProcess businessProcess : businessProcessArray) {
					businessProcess.setParent(businessScenario.getProcess());
					// Leaf reached. After parsing ProcessStep all relations are set.
					MpxBusinessProcessStep[] processStepArray = parseProcessStep(businessProcess);
					for(MpxBusinessProcessStep businessProcessStep : processStepArray) {
						businessProcessStep.setParent(businessProcess.getProcess());
					}
				}
				// Persist completed BusinessScenario with subtree
				persistTopLevelProcessWithChildren(businessScenario.getProcess());
			}
		}
		
		this.logger.exiting(CLASS_NAME, METHOD_NAME);
	}

	private MpxBusinessProcessStep[] parseProcessStep(MpxBusinessProcess businessProcess) throws JSONException {
		final String METHOD_NAME = "parseProcessStep(BusinessProcess businessProcess)";
		this.logger.entering(this.CLASS_NAME, METHOD_NAME);
		
		ArrayList<MpxBusinessProcessStep> businessProcessStepList = new ArrayList<MpxBusinessProcessStep>();
		MpxBusinessProcessStep step;
		
		for(MpxRecord record : businessProcess.getRecordList()) {
			// Only level 6 ProcessSteps are left
			step = new MpxBusinessProcessStep(record.getName(), creationDate, businessProcess.getProcess(), importProcessStepProvider);
			businessProcessStepList.add(step);
			logger.finest("Created new Step: " + record.getName());
		}
		
		this.logger.exiting(CLASS_NAME, METHOD_NAME);
		return businessProcessStepList.toArray(new MpxBusinessProcessStep[businessProcessStepList.size()]);
	}

	private MpxBusinessProcess[] parseBusinessProcesses(MpxBusinessScenario businessScenario) {
		final String METHOD_NAME = "parseBusinessProcesses(BusinessScenario businessScenario)";
		this.logger.entering(this.CLASS_NAME, METHOD_NAME);

		MpxBusinessProcess process = null;
		ArrayList<MpxBusinessProcess> businessProcessList = new ArrayList<MpxBusinessProcess>();
		
		// Remove organizational level four folders with subtrees
		ArrayList<MpxRecord> businessProcessFolderList = parseOrganizationalFolders(businessScenario.getRecordList(), BUSINESS_PROCESS_PARENT_FOLDER_LEVEL, SAP_BUSINESS_PROCESS);
		logger.finest("businessProcessFolderList length = " + businessProcessFolderList.size());
		for(MpxRecord record : businessProcessFolderList) {
			logger.finest("Parsing record " + record.getName() + " from businessProcessFolderList.");
			// Only records with level >= 5 left
			if(record.getLevel() == BUSINESS_PROCESS_LEVEL) {
				process = new MpxBusinessProcess(record.getName(), creationDate, businessScenario.getProcess(), importProcessProvider);
				businessProcessList.add(process);
				logger.finest("Created BusinessProcess from record " + record.getName());
			}
			else {
				// Subprocess which belongs to process
				process.getRecordList().add(record);
				logger.finest("BusinessProcess " + process.getProcess().getName() + " got child " + record.getName());
			}
		}
		
		this.logger.exiting(CLASS_NAME, METHOD_NAME);
		return businessProcessList.toArray(new MpxBusinessProcess[businessProcessList.size()]);
	}

	private MpxBusinessScenario[] parseBusinessScenarios(MpxProject project) {
		final String METHOD_NAME = "parseBusinessScenarios(MpxProject project)";
		this.logger.entering(this.CLASS_NAME, METHOD_NAME);
		
		ArrayList<MpxBusinessScenario> businessScenarioList = new ArrayList<MpxBusinessScenario>();
		final int businessScenarioParentFolderLevel = 2;
		final int businessScenarioLevel = 3;
		MpxBusinessScenario scenario = null;
		
		// Remove organizational level-two folders with subtrees
		// The only one we need is 'Business Scenarios' with subtree
		ArrayList<MpxRecord> businessScenarioFolderList = parseOrganizationalFolders(project.getRecordList(), businessScenarioParentFolderLevel, SAP_BUSINESS_SCENARIO);
		for(MpxRecord record : businessScenarioFolderList) {
			logger.finest("Parsing record " + record.getName() + " from businessScenarioFolderList.");
			// Only records with level >= 3 left
			if(record.getLevel() == businessScenarioLevel) {
				scenario = new MpxBusinessScenario(record.getName(), creationDate, importProcessProvider);
				businessScenarioList.add(scenario);
				logger.finest("Created BusinessScenario from record " + record.getName());
			}
			else {
				// Subprocess which belongs to scenario
				scenario.getRecordList().add(record);
				logger.finest("BusinessScenario " + scenario.getProcess().getName() + " got child " + record.getName());
			}
		}
		
		this.logger.exiting(CLASS_NAME, METHOD_NAME);
		return businessScenarioList.toArray(new MpxBusinessScenario[businessScenarioList.size()]);
	}

	private ArrayList<MpxRecord> parseOrganizationalFolders(ArrayList<MpxRecord> recordList, int level, String folderName) {
		final String METHOD_NAME = "parseOrganizationalFolders(ArrayList<Record> recordList, int level, String folderName)";
		this.logger.entering(this.CLASS_NAME, METHOD_NAME);
		
		boolean belongsToProcess = false;
		ArrayList<MpxRecord> processList = new ArrayList<MpxRecord>();
		
		for(MpxRecord record : recordList) {
			// Search all records for passed folders and only add the subprocesses
			// which belong to them.
			logger.finer("Parsing record "+ record.getName());
			if(record.getLevel() == level) {
				if(record.getName().equals(folderName)) {
					belongsToProcess = true;
					// Skip the level 2 folder and only add subprocesses
					logger.finest("Found valid task (" + record.getName() + ". The following subprocesses belong to Business-Process-Hierarchy.");
					continue;
				}
				else {
					logger.finest("Found invalid task (" + record.getName() + ". The following subprocesses will be excluded from Business-Process-Hierarchy.");
					belongsToProcess = false;
				}
			}
			
			if(belongsToProcess) {
				// Record is a valid process
				processList.add(record);
				logger.finest("Record added to tree: " + record.getName());
			}
		}
		
		this.logger.exiting(CLASS_NAME, METHOD_NAME);
		return processList;
	}

	private MpxProject[] parseProjectList(String[] lines) {
		final String METHOD_NAME = "parseProjectList(String[] lines)";
		this.logger.entering(this.CLASS_NAME, METHOD_NAME);
		
		final int projectLevel = 1;
		ArrayList<MpxProject> projectList = new ArrayList<MpxProject>();
		MpxProject project = null;
		
		for(String line : lines) {
			// Get the record line
			MpxRecord record = parseLineToRecord(line);
			if(record != null) {
				// Process hierarchy scope
				if(record.getLevel() == projectLevel) {
					// Found a new project
					project = new MpxProject();
					projectList.add(project);
					logger.finest("Project added to ProjectList: " + record.getName());
				}
				// Add parsed record to list
				project.getRecordList().add(record);
				logger.finest("Record added to project: " + record.getName());
			}
		}
		
		this.logger.exiting(CLASS_NAME, METHOD_NAME);
		// Get the MpxProject[] and return
		return projectList.toArray(new MpxProject[projectList.size()]);
	}

	private MpxRecord parseLineToRecord(String line) {
		final String METHOD_NAME = "parseLineToRecord(String line)";
		this.logger.entering(this.CLASS_NAME, METHOD_NAME);
		
		// Define positions of the values to extract from line 
		final int positionRecordIdentifier = 0;
		final int positionName = 3;
		final int positionLevel = 4;
		// Second parameter of split() is needed to even split by a given separator when the split part would
		// be an empty string. For example: 
		// line = ';name;"";"value";'
		// line.split(";", -1) would create [ "", "name", """", ""value"", "" ]
		// while line.split(";") would return something like [ "", "name", """", ""value"" ]
		String[] values = line.split(Constants.BDR_EXPORT_CSV_PATH_SEPARATOR, -1);
		logger.finest("Values from line: " + values.toString());
		
		String recordIdentifier = values[positionRecordIdentifier];
		
		MpxRecord record = null;
		if(recordIdentifier.equals(BPH_RECORD_TASK)) {
			// Record number 70 describes that we have a task what means the
			// line is part of a BPH.
			// Example:
			// 70;13;0050563F44341ED28BFD5B6FBD5D303D;Purchasing_Group_Analysis;06;500;;0;;;;;0;0;0;;
			
			// Since all spaces are replaced by '_' from SAP when exporting the hierarchy,
			// the change has to be undone.
			String name = values[positionName].replace('_', ' ');
			int level = Integer.parseInt(values[positionLevel]);
			record = new MpxRecord(recordIdentifier, name, level);
			logger.finest("Created task record " + record.getName());
		}

		this.logger.exiting(CLASS_NAME, METHOD_NAME);
		return record;
	}

	@Override
	boolean checkValidationAndSetStatus(String[] lines) {
		final String METHOD_NAME = "checkValidationAndSetStatus(String[] lines)";
		this.logger.entering(this.CLASS_NAME, METHOD_NAME);
		
		/*
		 * Example file:
		 * 
		 * 	MPX;SAP;4.0;ANSI		<-- header (file creation): FORMAT;SOURCE_SYSTEM;VERSION;ENCODING
		 *	11;2;1;2
		 *	12;1;1;480;.;:;am;pm
		 *	30;TEST;;ALEXEJ;;01.11.2012;15.11.2012;0
		 *	41;40;49;2;1;11;41
		 *	50;1;1;ALEXEJ;Walz;;100%
		 *	61;98;4;1;3;95;5;81;56;57;58;59;40;41;42;73;72
		 *	70;1;0050563F44341EE28ACDBAFCB1F7D37A;Sales_Project;01;500;In Process;0;;;;;0;0;0;;
		 *	...
		 */
		
		// Ensure lines has useful content
		if(lines == null || lines.length < 2) {
			logger.finer("File is empty or doesn't have at least to lines of content.");
			this.statusInfo.add(Constants.BDR_IMPORT_ERROR_INVALID_FILE);
			this.logger.exiting(CLASS_NAME, METHOD_NAME);
			return false;
		}
		
		// Check for right header
		final int positionFileCreationFormat = 0;
		final int positionFileCreationSourceSystem = 1;
		String fileCreation = lines[0];
		String[] fileCreationValues = fileCreation.split(Constants.BDR_EXPORT_CSV_PATH_SEPARATOR, -1);
		
		// fileCreation should look like 'MPX;SAP;4.0;ANSI'
		// If 'MPX' or 'SAP' are missing, the import will be aborted
		if( !FILE_CREATION_FORMAT.equals(fileCreationValues[positionFileCreationFormat])
		 || !FILE_CREATION_SOURCE_SYSTEM.equals(fileCreationValues[positionFileCreationSourceSystem])) {
			logger.finer("FileCreation (header) of MPX file is invalid.");
			this.statusInfo.add(Constants.BDR_IMPORT_ERROR_INVALID_FILE);
			this.logger.exiting(CLASS_NAME, METHOD_NAME);
			return false;
		}
		
		this.logger.exiting(CLASS_NAME, METHOD_NAME);
		return true;
	}
	
	@Override
	void updateStatus() {
		final String METHOD_NAME = "updateStatus()";
		this.logger.entering(this.CLASS_NAME, METHOD_NAME);
		
		// Only update if there's not already a message set
		if(this.statusInfo.size() == 0) {
			if(this.cancelled) {
				logger.finer("Thread has been cancelled.");
				// Thread has been cancelled
				this.statusInfo.add(Constants.BDR_IMPORT_ERROR_THREAD_CANCELLED);
			}
			else if(counterProcessesAndSteps > 0) {
				logger.finer("Successfully imported " + counterProcessesAndSteps + " top level Processes");
				// Successfully imported from SolMan without BO's/tables
				this.statusInfo.add(Constants.BDR_IMPORT_SUCCESS_SOLMAN);
				// Add counters to list
				this.statusInfo.add("" + counterProcessesAndSteps);
			}
			else {
				// No processes have been added
				this.statusInfo.add(Constants.BDR_IMPORT_SUCCESS_NO_CHANGES);
				logger.finer("No errors occured but also nothing has been imported.");
			}
		}
		this.logger.exiting(CLASS_NAME, METHOD_NAME);
	}
	
}
