package com.ibm.is.sappack.cw.app.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.TypedQuery;

import com.ibm.is.sappack.cw.app.data.Resources;
import com.ibm.is.sappack.cw.app.data.bdr.jpa.Process;
import com.ibm.is.sappack.cw.app.services.bdr.threads.BphAbstractImporterThread;

public final class ImportProcessProvider extends ImportAbstractProvider {
	
	private static ImportProcessProvider instanceOfProcessProvider;
	private final String CLASS_NAME = ImportProcessProvider.class.getName();
	
	private ImportProcessProvider() {
		super();
	}
	
	public static synchronized ImportProcessProvider getInstance() {
		if(instanceOfProcessProvider == null) {
			instanceOfProcessProvider = new ImportProcessProvider();
		}
		return instanceOfProcessProvider;
	}
	
	/**
	 * 
	 * @param parent: the parent process
	 * @param childName: name to search for
	 * @param childDescription: will be set in case of creating a new process
	 * @param childUpdated: will be set in case of creating a new process
	 * @return: new or existing process
	 */
	public Process provideProcess(Process parent, String childName, String childDescription, Date childUpdated) {
		final String METHOD_NAME = "getProcess(Process parent, String name, String description, Date updated)";
		logger.entering(CLASS_NAME, METHOD_NAME);
		
		// Check for valid parent
		if(parent == null) {
			// The parent process is no existing JPA process
			throw new IllegalArgumentException("Either the parent is null or has no valid process ID.");
		}
		
		// Update transientModeEnable
		updateTransientModeEnabled();
		
		Process child = null;
		
		// Find if process already exists as child of parent
		List<Process> children = parent.getChildProcesses();
		child = findProcessInList(childName, children);
		
		if(child == null) {
			child = createProcess(childName, childDescription, childUpdated);
		}
		// Make sure child knows his parent
		setRelations(parent, child);
		
		if(!transientMode) {
			// Persist process into DB
			manager.joinTransaction();
			manager.persist(child);
		}
		
		logger.exiting(CLASS_NAME, METHOD_NAME);
		return child;
	}

	private void setRelations(Process parent, Process child) {
		final String METHOD_NAME = "setRelations(Process parent, Process child)";
		logger.entering(CLASS_NAME, METHOD_NAME);
		
		child.setParentProcess(parent);
		List<Process> children = parent.getChildProcesses();
		if(children == null) {
			children = new ArrayList<Process>();
			parent.setChildProcesses(children);
		}
		boolean isInList = false;
		for(Process process : children) {
			if(process.getName().equals(child.getName())) {
				isInList = true;
				break;
			}
		}
		if(!isInList) {
			children.add(child);
		}
		logger.exiting(CLASS_NAME, METHOD_NAME);
	}

	/**
	 * 
	 * @param name: name to search for
	 * @param description: will be set in case of creating a new process
	 * @param updated: will be set in case of creating a new process
	 * @return: new or existing process
	 */
	public Process provideTopLevelProcess(String name, String description, Date updated) {
		final String METHOD_NAME = "getTopLevelProcess(String name, String description, Date updated)";
		logger.entering(CLASS_NAME, METHOD_NAME);
		
		// Update transientModeEnable
		updateTransientModeEnabled();
		
		// Get existing root processes
		TypedQuery<Process> query = manager.createNamedQuery("Process.retrieveRoots", Process.class);
		List<Process> rootProcesses = query.getResultList();
		
		Process topLevelProcess = findProcessInList(name, rootProcesses);
		
		// Create if the process hasn't been found
		if (topLevelProcess == null) {
			topLevelProcess = createProcess(name, description, updated);
		}
		
		if(!transientMode) {
			manager.joinTransaction();
			manager.persist(topLevelProcess);
		}
		
		logger.exiting(CLASS_NAME, METHOD_NAME);
		return topLevelProcess;
	}
	
	private Process createProcess(String name, String description, Date updated) {
		// Creates an returns a process without persisting
		final String METHOD_NAME = "createProcess(String name, String description, Date updated)";
		logger.entering(CLASS_NAME, METHOD_NAME);
		
		// Prevent null values
		if(name == null) {
			name = "";
		}
		if(description == null) {
			description = "";
		}
		if(updated == null) {
			updated = new Date();
		}
		
		if(!transientMode) {
			// Update Process counter
			BphAbstractImporterThread.counterProcessesAndSteps++;
		}
		Process newProcess = new Process();
		newProcess.setName(name);
		newProcess.setDescription(description);
		newProcess.setUpdated(updated);
		newProcess.setType(Resources.BPH_TYPE_PROCESS);
		
		logger.exiting(CLASS_NAME, METHOD_NAME);
		return newProcess;
	}
	
	private Process findProcessInList(String name, List<Process> processList) {
		final String METHOD_NAME = "findProcessInList(String name, List<Process> processList)";
		logger.entering(CLASS_NAME, METHOD_NAME);
		
		Process process = null;
		
		if(processList != null && !processList.isEmpty()) {
			// Compare each process in list with name
			for(Process existingProcess : processList) {
				if(existingProcess.getName().equals(name)) {
					// Match found. Process with the name exists
					process = existingProcess;
					break;
				}
			}
		}
		
		logger.exiting(CLASS_NAME, METHOD_NAME);
		return process;
	}
	
	@Override
	protected void updateTransientModeEnabled() {
		switch(BphAbstractImporterThread.importType) {
			case CSV_PROCESSES:
			case CSV_COMPLETE:
			case MPX:
				transientMode = false;
				break;
			case CSV_BOS:
				transientMode = true;
				break;
		}
	}
	
}
