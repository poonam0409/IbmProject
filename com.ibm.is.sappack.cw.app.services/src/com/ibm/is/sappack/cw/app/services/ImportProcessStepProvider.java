package com.ibm.is.sappack.cw.app.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;

import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;

import com.ibm.is.sappack.cw.app.data.Resources;
import com.ibm.is.sappack.cw.app.data.bdr.jpa.Process;
import com.ibm.is.sappack.cw.app.data.bdr.jpa.ProcessStep;
import com.ibm.is.sappack.cw.app.data.bdr.jpa.Transaction;
import com.ibm.is.sappack.cw.app.services.bdr.threads.BphAbstractImporterThread;

public class ImportProcessStepProvider extends ImportAbstractProvider {
	
	private static ImportProcessStepProvider instanceOfProcessStepProvider;
	private final String CLASS_NAME = ImportProcessStepProvider.class.getName();
	
	private double transactionId = 0; // Is needed to create transactions with different IDs
	
	private ImportProcessStepProvider() {
		super();
	}
	
	public static synchronized ImportProcessStepProvider getInstance() {
		if(instanceOfProcessStepProvider == null) {
			instanceOfProcessStepProvider = new ImportProcessStepProvider();
		}
		return instanceOfProcessStepProvider;
	}

	/**
	 * 
	 * @param parent: the parent process of the searched step
	 * @param stepName: step name to look for
	 * @param stepDescription: will be set in case of creation
	 * @param creationDate: will be set in case of creation
	 * @param transactions: Transactions to be added to a new ProcessStep
	 * @return: Newly created or existing ProcessStep
	 * @throws SystemException 
	 * @throws NotSupportedException 
	 * @throws HeuristicRollbackException 
	 * @throws HeuristicMixedException 
	 * @throws RollbackException 
	 * @throws IllegalStateException 
	 * @throws SecurityException 
	 * @throws JSONException 
	 */
	public ProcessStep provideProcessStep(Process parent, String stepName, String stepDescription, Date creationDate, String transactions) throws JSONException {
		final String METHOD_NAME = "getProcessStep(Process parent, String stepName, String stepDescription, Date creationDate, String transactions)";
		logger.entering(CLASS_NAME, METHOD_NAME);

		if(parent == null) {
			throw new IllegalArgumentException("Either the parent is null or has no valid process ID.");
		}
		
		// Update transientModeEnable
		updateTransientModeEnabled();
		
		ProcessStep step = null;
		
		// Find if step already exists as child of parent process
		Collection<ProcessStep> children = parent.getProcessSteps();
		step = findProcessStepInList(stepName, children);
		
		if(step == null) {
			step = createProcessStep(stepName, stepDescription, creationDate, transactions);
		}
		
		setRelations(parent, step);
		
		if(!transientMode) {
			// Persist ProcessStep into DB
			manager.joinTransaction();
			manager.persist(step);
		}
		
		logger.exiting(CLASS_NAME, METHOD_NAME);
		return step;
	}
	
	private void setRelations(Process parent, ProcessStep step) {
		final String METHOD_NAME = "setRelations(Process parent, ProcessStep step)";
		logger.entering(CLASS_NAME, METHOD_NAME);
		// Set process as parent
		step.setParentProcess(parent);
		
		// Set step as child
		Collection<ProcessStep> children = parent.getProcessSteps();
		if(children == null) {
			children = new ArrayList<ProcessStep>();
			parent.setProcessSteps((List<ProcessStep>)children);
		}
		boolean isInList = false;
		for(ProcessStep child : children) {
			if(child.getName().equals(step.getName())) {
				isInList = true;
				break;
			}
		}
		if(!isInList) {
			children.add(step);
		}
		
		logger.exiting(CLASS_NAME, METHOD_NAME);
	}

	private ProcessStep createProcessStep(String name, String description, Date updated, String transactions) throws JSONException {
		// Creates an returns a process without persisting
		final String METHOD_NAME = "createProcessStep(String name, String description, Date updated)";
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
			// Update Process/-step counter
			BphAbstractImporterThread.counterProcessesAndSteps++;
		}
		ProcessStep newProcessStep = new ProcessStep();
		newProcessStep.setName(name);
		newProcessStep.setDescription(description);
		newProcessStep.setUpdated(updated);
		newProcessStep.setType(Resources.BPH_TYPE_PROCESSSTEP);
		if(transactions.length() > 0) {
			newProcessStep.setTransactions(addTransactionsToProcessStep(newProcessStep, transactions));
		}
		
		logger.exiting(CLASS_NAME, METHOD_NAME);
		return newProcessStep;
	}
	
	private ProcessStep findProcessStepInList(String name, Collection<ProcessStep> children) {
		final String METHOD_NAME = "findProcessStepInList(String name, Collection<ProcessStep> children)";
		logger.entering(CLASS_NAME, METHOD_NAME);
		
		ProcessStep processStep = null;
		
		if(children != null && !children.isEmpty()) {
			// Compare each step in list with name
			for(ProcessStep existingProcessStep : children) {
				if(existingProcessStep.getName().equals(name)) {
					// Match found. Step with the name exists
					processStep = existingProcessStep;
					break;
				}
			}
		}
		
		logger.exiting(CLASS_NAME, METHOD_NAME);
		return processStep;
	}
	
	// "[{'Name':'tx500','Comment':'comment'},{'Name':'tx101','Comment':'is not a comment'}]"
	private List<Transaction> addTransactionsToProcessStep(ProcessStep processStep, String transactions) throws JSONException {
		final String METHOD_NAME = "addTransactionsToProcessStep(ProcessStep processStep, String transactions)";
		logger.entering(this.CLASS_NAME, METHOD_NAME);
		
		List<Transaction> txList = new ArrayList<Transaction>();
		transactions=transactions.replaceAll("\'\\{", "\\{");
		transactions=transactions.replaceAll("\\}\'", "\\}");
		transactions=transactions.replaceAll("[-,|]", ",");
		JSONArray jsonArray = new JSONArray(transactions);
		int arrLength = jsonArray.size();
		
		for(int i = 0; i < arrLength; i++) {
			Transaction tx = new Transaction();
			JSONObject object = jsonArray.getJSONObject(i);
			
			tx.setName(object.get(Constants.BDR_EXPORT_TRANSACTION_NAME).toString());
			tx.setComment(object.get(Constants.BDR_EXPORT_TRANSACTION_COMMENT).toString());
			tx.setTransactionId(this.transactionId);
			this.transactionId++;
			
			txList.add(tx);
		}
		processStep.setTransactions(txList);
		
		logger.exiting(CLASS_NAME, METHOD_NAME);
		return txList;
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
