package com.ibm.is.sappack.cw.app.services.bdr.threads;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.servlet.http.HttpSession;

import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;

import com.ibm.is.sappack.cw.app.data.bdr.FieldUsageUseMode;
import com.ibm.is.sappack.cw.app.data.bdr.jpa.BusinessObject;
import com.ibm.is.sappack.cw.app.data.bdr.jpa.Field;
import com.ibm.is.sappack.cw.app.data.bdr.jpa.FieldUsage;
import com.ibm.is.sappack.cw.app.data.bdr.jpa.Process;
import com.ibm.is.sappack.cw.app.data.bdr.jpa.ProcessStep;
import com.ibm.is.sappack.cw.app.data.bdr.jpa.Table;
import com.ibm.is.sappack.cw.app.data.bdr.jpa.TableUsage;
import com.ibm.is.sappack.cw.app.data.bdr.jpa.Transaction;
import com.ibm.is.sappack.cw.app.services.AbstractCancelableThread;
import com.ibm.is.sappack.cw.app.services.Constants;
import com.ibm.is.sappack.cw.app.services.Util;
import com.ibm.websphere.webmsg.publisher.Publisher;
import com.ibm.websphere.webmsg.publisher.PublisherException;
import com.ibm.websphere.webmsg.publisher.jndijms.BayeuxJmsTextMsg;

public class BdrExporterThread extends AbstractCancelableThread {

	private static final String TOPIC_BDR_EXPORT_STARTED = "/bdr/bdrexport/started";
	private static final String TOPIC_BDR_EXPORT_FINISHED = "/bdr/bdrexport/finished";
	private static final String WHOLE_BDR_TREE_EXPORT = "WHOLE_BDR_TREE";
	private final int Index0= 0;
	
	public enum ExportMode {
		FULL_BDR_EXPORT, FOLLOW_UP_FIELDS_ONLY, IN_SCOPE_FIELDS_ONLY, NOT_IN_SCOPE_FIELDS_ONLY;
	}

	private Boolean exportOnlyTables = false;
	private ExportMode exportMode = ExportMode.FULL_BDR_EXPORT;
	private String[] tablesToExtract = null;
	private String exportNodes = WHOLE_BDR_TREE_EXPORT;
	private ArrayList<String[]> nodesToExtract = null;
	private String itemType = null;
	private int numberOfChildProcesses=0;
	private final String CLASS_NAME = BdrExporterThread.class.getName();
	private String STATUS_MESSAGE = "";
	private boolean selectedTables = false;

	// buffering content for the CSV-File
	private StringBuffer fileContent = new StringBuffer();

	// maximum depth of variable part of the tree hierarchy,
	// the last element can either be a process or a process step
	private int maxWidth = 0;

	public BdrExporterThread(Publisher publisher, HttpSession session, ExportMode exportMode, String exportNodes, Boolean exportOnlyTables, String itemType) {
		super(session, publisher);
		// if export mode is not set the setting remains unchanged (full export)
		if (exportMode != null) {
			this.exportMode = exportMode;
		}
		if (exportNodes != null) {
			this.exportNodes = exportNodes;
		}
		if (exportOnlyTables != null) {
			this.exportOnlyTables = exportOnlyTables;
		}
		if (itemType != null) {
			this.itemType=itemType;
		}
	}

	@Override
	public void run() {
		final String METHOD_NAME = "run()";
		logger.entering(CLASS_NAME, METHOD_NAME);

		// publish a message that we're starting the export
		this.publishTopic(new BayeuxJmsTextMsg(TOPIC_BDR_EXPORT_STARTED + this.sessionId, "started"));

		// start actual export
		if (this.exportOnlyTables){
			this.exportTablesToFile();
		} else {
			this.exportBdrToFile();
		}

		if (this.cancelled) {
			this.STATUS_MESSAGE = "cancel";
		} else if (this.STATUS_MESSAGE.length() == 0) {
			// status not yet set
			this.STATUS_MESSAGE = "success";
		}

		// publish the export status to the client
		this.publishTopic(new BayeuxJmsTextMsg(TOPIC_BDR_EXPORT_FINISHED + this.sessionId, this.STATUS_MESSAGE));

		// remove the the reference to this thread from the session, so the garbage collector can clean up
		session.removeAttribute(Constants.SESSION_ATTRIBUTE_NAME_BDR_EXPORT_THREAD);

		logger.exiting(CLASS_NAME, METHOD_NAME);
	}

	private void exportBdrToFile() {
		final String METHOD_NAME = "exportBdrToFile()";
		logger.entering(CLASS_NAME, METHOD_NAME);

		try {
			jpaTransaction.begin();
			TypedQuery<Process> query = manager.createNamedQuery("Process.retrieveRoots", Process.class);
			List<Process> rootProcesses = query.getResultList();
			List<String> emptyPathList = new ArrayList<String>();

			// necessary call before being able to use the handleProcess method
			// finds out how long the variable tree part can get
			// result gets saved to global variable maxWidth
				determineLongestWidth();	
			
			// write first line (header)
			// column names will be defined there and written to fileContent variable
			writeHeaderLineToBuffer();
			
			// check if we extract the whole process or only a part of it
			if (this.exportNodes != WHOLE_BDR_TREE_EXPORT){
				String[] nodesToExtract = this.exportNodes.split(";");
				// save the nodes which we must extract, so we can use them later
				ArrayList<String[]> nodes = new ArrayList<String[]>();
				for (int i = 0; i<nodesToExtract.length; i++){
					nodes.add(nodesToExtract[i].split(","));
				}
				this.nodesToExtract = nodes;
			}

			for (Process process: rootProcesses) {
				handleProcess(process, emptyPathList);
				if (this.cancelled) {
					logger.exiting(CLASS_NAME, METHOD_NAME);
					return;
				}
			}

			// save fileContent for later file creation, seperate request required to download file
			// If an object of the same name is already bound to the session, the object is replaced.
			this.session.setAttribute(Constants.SESSION_ATTRIBUTE_NAME_BDR_EXPORT_FILE_CONTENT, fileContent.toString());

			// no changes done to DB, jpaTransaction needs to be closed
			jpaTransaction.rollback();
			logger.exiting(CLASS_NAME, METHOD_NAME);
		} catch (Exception e) {
			Util.handleBatchException(e);
			this.STATUS_MESSAGE = "internal_error";
		}
	}
	
	private void exportTablesToFile() {
		final String METHOD_NAME = "exportTablesToFile()";
		logger.entering(CLASS_NAME, METHOD_NAME);
		try {
			jpaTransaction.begin();	
			
			writeHeaderLineToBuffer();
			
			if (this.exportNodes != WHOLE_BDR_TREE_EXPORT){
				this.tablesToExtract = this.exportNodes.split(",");
			}
			
			TypedQuery<Table> query = manager.createNamedQuery("Table.getAll", Table.class);
			List<Table> tables = query.getResultList();

			for (Table table : tables) {
				this.selectedTables = false;
				if (this.exportNodes != WHOLE_BDR_TREE_EXPORT){
					for(int i=0;i<this.tablesToExtract.length;i++){
						if(this.tablesToExtract[i].equals(table.getName())){
							this.selectedTables = true;
						}
					}
				}
				if (this.exportNodes == WHOLE_BDR_TREE_EXPORT){
					this.selectedTables=true;
				}
				if(this.selectedTables){
					logger.fine("Table: " + table.getName());

					List<String> pathList = new ArrayList<String>();

					addValue(table.getName(), pathList);
					addValue(table.getDescription(), pathList);

					Collection<Field> fields = table.getFields();
					if (fields.isEmpty()){
						writeToStringBuffer(pathList);
					} else {
						for (Field field : fields) {
							List<String> localPathList = new ArrayList<String>(pathList);

							addValue(field.getName(), localPathList);
							addValue(field.getCheckTable(), localPathList);
							addValue(String.valueOf(field.getRecommended()), localPathList);
							addValue(field.getSapView(), localPathList);
							addValue(field.getDescription(), localPathList);

							writeToStringBuffer(localPathList);
						}
					}
				}
			}
			
			logger.finest("Export content:\n" + fileContent.toString());

			// save fileContent for later file creation, seperate request required to download file
			// If an object of the same name is already bound to the session, the object is replaced.
			this.session.setAttribute(Constants.SESSION_ATTRIBUTE_NAME_BDR_EXPORT_FILE_CONTENT, fileContent.toString());

			// check again if thread has been cancelled
			if (this.cancelled) {
				logger.exiting(CLASS_NAME, METHOD_NAME);
				return;
			}

			// no changes done to db, jpaTransaction needs to be closed
			jpaTransaction.rollback();
			logger.exiting(CLASS_NAME, METHOD_NAME);
		} catch (Exception e) {
			Util.handleBatchException(e);
			this.STATUS_MESSAGE = "internal_error";
		}
	}

	/**
	 * @param process
	 *            current process in tree
	 * @param pathList
	 *            list containing all tree elements relevant for this element's
	 *            path, starting on top level
	 */
	private void handleProcess(Process process, List<String> pathList) {
		logger.fine("Exporting process: " + process.getName());

		// check if thread has been cancelled
		if (this.cancelled) {
			return;
		}

		// Copy the list to make local modifications
		List<String> localPathList = new ArrayList<String>(pathList);

		addValue(process.getName(), localPathList);
		addValue(process.getDescription(), localPathList);
		
		Collection<Process> children = process.getChildProcesses();
		if (!children.isEmpty()) {
			for (Process proc : children) {
				handleProcess(proc, localPathList);
			}
		}
		Collection<ProcessStep> steps = process.getProcessSteps();
		if (!steps.isEmpty()) {
			// variable part of tree hierarchy reached its end,
			// fill up with gaps if necessary
			localPathList = fillWithGaps(localPathList);

			for (ProcessStep pStep : steps) {
				handleProcessStep(pStep, localPathList);
			}
		}
		if (children.isEmpty() && steps.isEmpty()) {
			// neither children processes nor process steps exist
			// leaf process reached, write line
			writeToStringBuffer(localPathList);
		}
	}

	private void handleProcessStep(ProcessStep processStep, List<String> pathList) {
		// Copy the list to make local modifications
		List<String> localPathList = new ArrayList<String>(pathList);
		
		addValue(processStep.getName(), localPathList);
		addValue(processStep.getDescription(), localPathList);

		Collection<BusinessObject> businessObjects = processStep.getUsedBusinessObjects();
		if (!businessObjects.isEmpty()) {
			addValue(formatTransactions(processStep), localPathList);
			for (BusinessObject bObject : businessObjects) {
				handleBusinessObject(bObject, processStep, localPathList);
			}
		} else {
			writeToStringBuffer(localPathList);
		}
	}

	private void handleBusinessObject(BusinessObject businessObject, ProcessStep processStep, List<String> pathList) {
		// Copy the list to make local modifications
		List<String> localPathList = new ArrayList<String>(pathList);
		
		addValue(businessObject.getName(), localPathList);
		addValue(businessObject.getShortName(), localPathList);
		addValue(businessObject.getDescription(), localPathList);

		Collection<Table> tables = businessObject.getTables();
		if (!tables.isEmpty()) {
			for (Table table : tables) {
				handleTable(table, processStep, businessObject, localPathList);
			}
		} else {
			writeToStringBuffer(localPathList);
		}
	}

	private void handleTable(Table table, ProcessStep processStep, BusinessObject bObject, List<String> pathList) {
		logger.finest("Table: " + table.getName());
		logger.finest("ProcessStep: " + processStep.getName());
		logger.finest("BO: " + bObject.getName());
		
		// Copy the list to make local modifications
		List<String> localPathList = new ArrayList<String>(pathList);
		
		addValue(table.getName(), localPathList);
		addValue(table.getDescription(), localPathList);

		Collection<Field> fields = table.getFields();
		if (!fields.isEmpty()) {
			TableUsage tableUsage = bObject.getUsage(processStep, table);
			handleTableUsage(tableUsage, localPathList);
		} else {
			writeToStringBuffer(localPathList);
		}
	}

	private void handleTableUsage(TableUsage tableUsage, List<String> pathList) {
		if (tableUsage == null) {
			logger.info("WARNING: An expected Table Usage object is missing.");
			return;
			// TODO: This means the BDR in the file is inconsistent, as a table usage should exist here.
			// We probably should display a warning about this to the user.
		}
		logger.finest("Table Usage: " + tableUsage.getName());
		
		// Copy the list to make local modifications
		List<String> localPathList = new ArrayList<String>(pathList);
		
		addValue(tableUsage.getApprovalStatus().toString(), localPathList);

		Iterator<FieldUsage> fieldUsageIterator = tableUsage.getFieldUsages().iterator();
		while (fieldUsageIterator.hasNext()) {
			FieldUsage fieldUsage = fieldUsageIterator.next();
			handleFieldUsage(fieldUsage, localPathList);
		}
	}

	private void handleFieldUsage(FieldUsage fieldUsage, List<String> pathList) {
		logger.finest("Field Usage: " + fieldUsage.getName());
		// Copy the list to make local modifications
		List<String> localPathList = new ArrayList<String>(pathList);
		Field field = fieldUsage.getField();

		addValue(field.getName(), localPathList);
		addValue(fieldUsage.getRequired(), localPathList);
		addValue(fieldUsage.getUseMode().toString(), localPathList);
		addValue(fieldUsage.getComment(), localPathList);
		addValue(field.getCheckTable(), localPathList);
		addValue(field.getRecommended(), localPathList);
		addValue(field.getSapView(), localPathList);
		addValue(field.getDescription(), localPathList);
		addValue(fieldUsage.getGlobalTemplate(), localPathList);

		// If we are exporting a report for a specific scope value, truncate the data
		switch (this.exportMode) {
		case FOLLOW_UP_FIELDS_ONLY :
		case IN_SCOPE_FIELDS_ONLY:
		case NOT_IN_SCOPE_FIELDS_ONLY:
			FieldUsageUseMode usage;
			
			if (this.exportMode == ExportMode.FOLLOW_UP_FIELDS_ONLY){
				usage = FieldUsageUseMode.FOLLOWUP;
			} else if (this.exportMode == ExportMode.IN_SCOPE_FIELDS_ONLY){
				usage = FieldUsageUseMode.INSCOPE;
			} else {
				usage = FieldUsageUseMode.NOTINSCOPE;
			}
				
			if (fieldUsage.getUseMode().equals(usage)) {
				// remove unnecessary columns
				List<String> partialPathList = new ArrayList<String>();
				int localPathListSize = localPathList.size();
				// add all process name columns
				for (int pos = localPathListSize; pos >= 20; pos -= 2) {
					partialPathList.add(localPathList.get(localPathListSize - pos));
				}
				partialPathList.add(localPathList.get(localPathListSize - 18));
				partialPathList.add(localPathList.get(localPathListSize - 15));
				partialPathList.add(localPathList.get(localPathListSize - 12));
				partialPathList.add(localPathList.get(localPathListSize - 9));
				partialPathList.add(localPathList.get(localPathListSize - 2));
				partialPathList.add(localPathList.get(localPathListSize - 1));
				writeToStringBuffer(partialPathList);
			}
			break;
		case FULL_BDR_EXPORT:
			writeToStringBuffer(localPathList);
			break;
		}
	}

	private void addValue(String value, List<String> localPathList) {
		if (value == null) {
			logger.finest("Null value");
			localPathList.add("");
		} else {
			// Double any "double quotes" (" becomes "") since this is how we (and Excel) represent a single double quote character
			value = value.replace("\"", "\"\"");
			logger.finest("Value: " + value);
			localPathList.add(value);
		}
	}
	
	private void addValue(boolean value, List<String> localPathList) {
		localPathList.add(Boolean.toString(value));
	}

	/**
	 * Iterates over all transactions of a given process step and returns them
	 * as a json formatted string. If there are no transactions, the return
	 * value will be an empty string. Since values in our CSV file are
	 * surrounded by double quotes, we do not want any double quotes inside the
	 * transactions array. Hence, double quotes are converted to single quotes.
	 */
	private String formatTransactions(ProcessStep processStep) {
		Collection<Transaction> transactions = processStep.getTransactions();
		Iterator<Transaction> it = transactions.iterator();
		JSONArray jsonArray = new JSONArray();
		String jsonObjectString = new String();
		
		while (it.hasNext()) {
			Transaction tx = it.next();

			JSONObject jsonObject = new JSONObject();
			try {
				jsonObject.put(Constants.BDR_EXPORT_TRANSACTION_NAME, tx.getName());
				if (tx.getComment() == null) {
					jsonObject.put(Constants.BDR_EXPORT_TRANSACTION_COMMENT, "");
				} else {
					jsonObject.put(Constants.BDR_EXPORT_TRANSACTION_COMMENT, tx.getComment());
				}
			} catch (JSONException e) {
				Util.handleBatchException(e);
				this.STATUS_MESSAGE = "internal_error";
			}
			// add transaction to json array that contains all transactions
			jsonObjectString = jsonObject.toString();
			jsonObjectString = jsonObjectString.replace(",", "-");
			jsonArray.add(jsonObjectString);
		}

		String strArr = jsonArray.toString();

		// convert double quotes to single quotes
		// Changes for EXT324/232599
		// Transaction String format changed from : 
		// "Name : , Comment : ,Name : , Comment : "
		// to
		// "Name : - Comment : |Name : - Comment : "
		// Since the comma separator isn't read correctly when the csv file is opened in Excel
		String jsonArrayString = strArr.replace("\"", "\'");
		jsonArrayString = jsonArrayString.replace(",", "|");
		jsonArrayString = jsonArrayString.replace("\\", "");

		if (jsonArray.isEmpty()) {
			return "";
		}
		return jsonArrayString;
	}

	/**
	 * Determines the width from top level processes to processes without
	 * children (leaf). The length from process steps to fields
	 * (BDR_EXPORT_WIDTH_PROCESSSTEPS_TO_FIELDS) is constant and thus does not
	 * matter for this calculation.
	 */
	private void determineLongestWidth() {
		TypedQuery<Process> query = null;
		query = manager.createNamedQuery("Process.retrieveRoots", Process.class);
		List<Process> rootProcesses = query.getResultList();
		Iterator<Process> it = rootProcesses.iterator();

		int currentWidth = 0;
		while (it.hasNext()) {
			Process process = it.next();
			//EXT230 : Do not iterate with all the processes if the "Complete BDR" is not selected as export option
			if(this.exportNodes != WHOLE_BDR_TREE_EXPORT) {
				String exportNodeSubString=exportNodes.substring(0,exportNodes.indexOf(";"));				
				String[] exportNodeList=exportNodeSubString.split(",");
				if(exportNodeList[0].equals(process.getName()))
				{
					determineWidth(process, currentWidth);	
					break;
				}
			}
			else{
				determineWidth(process, currentWidth);
			}
		}
	}

	private void determineWidth(Process process, int currentWidth) {
		currentWidth++;
		Collection<Process> processes = process.getChildProcesses();
		if (!processes.isEmpty()) {
			Iterator<Process> it = processes.iterator();
			while (it.hasNext()) {
				Process proc = it.next();
				if(exportNodes!=WHOLE_BDR_TREE_EXPORT) {
					String[] nodesToExtract = this.exportNodes.split(";");
					// save the nodes which we must extract, so we can use them later
					ArrayList<String[]> nodes = new ArrayList<String[]>();
					for (int i = 0; i<nodesToExtract.length; i++){
						nodes.add(nodesToExtract[i].split(","));
					}
					this.nodesToExtract = nodes;
					determineWidth(proc, currentWidth);
					if(process.getName().equals(this.nodesToExtract.get(0)[0]))	{
						numberOfChildProcesses++;
					}
				}
				else {
					determineWidth(proc, currentWidth);
				}
			}
		} else {
			// no children processes, leaf reached
			// multiply by 2, because each process' description
			// has to be taken into account as well
			currentWidth *= 2;
		}

		if (currentWidth > maxWidth) {
			maxWidth = currentWidth;
		}
	}
	
	/**
	 * Writes a header line to the fileContent variable
	 */
	private void writeHeaderLineToBuffer() {
		StringBuffer header = new StringBuffer();
		
		if (this.exportOnlyTables){
			header.append("\"").append(Constants.BDR_EXPORT_TABLE_NAME).append("\"").append(Constants.BDR_EXPORT_CSV_PATH_SEPARATOR);
			header.append("\"").append(Constants.BDR_EXPORT_TABLE_DESC).append("\"").append(Constants.BDR_EXPORT_CSV_PATH_SEPARATOR);
			header.append("\"").append(Constants.BDR_EXPORT_FIELD_NAME).append("\"").append(Constants.BDR_EXPORT_CSV_PATH_SEPARATOR);
			header.append("\"").append(Constants.BDR_EXPORT_FIELD_CHECK_TABLE).append("\"").append(Constants.BDR_EXPORT_CSV_PATH_SEPARATOR);
			header.append("\"").append(Constants.BDR_EXPORT_FIELD_RECOMMENDED).append("\"").append(Constants.BDR_EXPORT_CSV_PATH_SEPARATOR);
			header.append("\"").append(Constants.BDR_EXPORT_FIELD_SAP_VIEW).append("\"").append(Constants.BDR_EXPORT_CSV_PATH_SEPARATOR);
			header.append("\"").append(Constants.BDR_EXPORT_FIELD_DESC).append("\"");
		} else {
			if (this.exportMode != ExportMode.FULL_BDR_EXPORT) {
				// add additional header line in case of a partial export
				String usageMode = new String();
				switch (this.exportMode){
				case FOLLOW_UP_FIELDS_ONLY :
					usageMode += "'follow up'";
					break;
				case IN_SCOPE_FIELDS_ONLY :
					usageMode += "'in scope'";
					break;
				case NOT_IN_SCOPE_FIELDS_ONLY :
					usageMode += "'not in scope'";
					break;
				}
				
				// append the right usage mode
				header.append("PARTIAL EXPORT (").append(usageMode).append(" fields only) This export is for documentation use only!");
				header.append(Constants.BDR_EXPORT_LINE_SEPARATOR);
				for (int i = 0; i < maxWidth; i += 2) {
					header.append("\"").append(Constants.BDR_EXPORT_PROCESS_NAME).append("\"").append(Constants.BDR_EXPORT_CSV_PATH_SEPARATOR);
				}
				header.append("\"").append(Constants.BDR_EXPORT_PROCESS_STEP).append("\"").append(Constants.BDR_EXPORT_CSV_PATH_SEPARATOR);
				header.append("\"").append(Constants.BDR_EXPORT_BUSINESS_OBJECT_NAME).append("\"").append(Constants.BDR_EXPORT_CSV_PATH_SEPARATOR);
				header.append("\"").append(Constants.BDR_EXPORT_TABLE_NAME).append("\"").append(Constants.BDR_EXPORT_CSV_PATH_SEPARATOR);
				header.append("\"").append(Constants.BDR_EXPORT_FIELD_NAME).append("\"").append(Constants.BDR_EXPORT_CSV_PATH_SEPARATOR);
				header.append("\"").append(Constants.BDR_EXPORT_FIELD_DESC).append("\"").append(Constants.BDR_EXPORT_CSV_PATH_SEPARATOR);
				header.append("\"").append(Constants.BDR_EXPORT_GT).append("\"");
			} else {
				// full export header line
				for (int i = 0; i < maxWidth; i += 2) {
					header.append("\"").append(Constants.BDR_EXPORT_PROCESS_NAME).append("\"").append(Constants.BDR_EXPORT_CSV_PATH_SEPARATOR);
					header.append("\"").append(Constants.BDR_EXPORT_PROCESS_DESC).append("\"").append(Constants.BDR_EXPORT_CSV_PATH_SEPARATOR);
				}

				header.append("\"").append(Constants.BDR_EXPORT_PROCESS_STEP).append("\"").append(Constants.BDR_EXPORT_CSV_PATH_SEPARATOR);
				header.append("\"").append(Constants.BDR_EXPORT_PROCESS_STEP_DESC).append("\"").append(Constants.BDR_EXPORT_CSV_PATH_SEPARATOR);
				header.append("\"").append(Constants.BDR_EXPORT_TRANSACTIONS).append("\"").append(Constants.BDR_EXPORT_CSV_PATH_SEPARATOR);
				header.append("\"").append(Constants.BDR_EXPORT_BUSINESS_OBJECT_NAME).append("\"").append(Constants.BDR_EXPORT_CSV_PATH_SEPARATOR);
				header.append("\"").append(Constants.BDR_EXPORT_BUSINESS_OBJECT_SHORTNAME).append("\"").append(Constants.BDR_EXPORT_CSV_PATH_SEPARATOR);
				header.append("\"").append(Constants.BDR_EXPORT_BUSINESS_OBJECT_DESC).append("\"").append(Constants.BDR_EXPORT_CSV_PATH_SEPARATOR);
				header.append("\"").append(Constants.BDR_EXPORT_TABLE_NAME).append("\"").append(Constants.BDR_EXPORT_CSV_PATH_SEPARATOR);
				header.append("\"").append(Constants.BDR_EXPORT_TABLE_DESC).append("\"").append(Constants.BDR_EXPORT_CSV_PATH_SEPARATOR);
				header.append("\"").append(Constants.BDR_EXPORT_TABLE_USAGE_APPROVAL_STATUS).append("\"").append(Constants.BDR_EXPORT_CSV_PATH_SEPARATOR);
				header.append("\"").append(Constants.BDR_EXPORT_FIELD_NAME).append("\"").append(Constants.BDR_EXPORT_CSV_PATH_SEPARATOR);
				header.append("\"").append(Constants.BDR_EXPORT_FIELD_REQUIRED).append("\"").append(Constants.BDR_EXPORT_CSV_PATH_SEPARATOR);
				header.append("\"").append(Constants.BDR_EXPORT_FIELD_USE_MODE).append("\"").append(Constants.BDR_EXPORT_CSV_PATH_SEPARATOR);
				header.append("\"").append(Constants.BDR_EXPORT_FIELD_USAGE_COMMENT).append("\"").append(Constants.BDR_EXPORT_CSV_PATH_SEPARATOR);
				header.append("\"").append(Constants.BDR_EXPORT_FIELD_CHECK_TABLE).append("\"").append(Constants.BDR_EXPORT_CSV_PATH_SEPARATOR);
				header.append("\"").append(Constants.BDR_EXPORT_FIELD_RECOMMENDED).append("\"").append(Constants.BDR_EXPORT_CSV_PATH_SEPARATOR);
				header.append("\"").append(Constants.BDR_EXPORT_FIELD_SAP_VIEW).append("\"").append(Constants.BDR_EXPORT_CSV_PATH_SEPARATOR);
				header.append("\"").append(Constants.BDR_EXPORT_FIELD_DESC).append("\"").append(Constants.BDR_EXPORT_CSV_PATH_SEPARATOR);
				header.append("\"").append(Constants.BDR_EXPORT_GT).append("\"");
			}
		}

		fileContent.append(header.toString()).append(Constants.BDR_EXPORT_LINE_SEPARATOR);
	}

	/**
	 * Write completed paths to the fileContent variable. The delimiter is
	 * determined by the constant Constants.BDR_EXPORT_CSV_PATH_SEPARATOR. If
	 * the path is shorter than the longest one, empty elements will be added at
	 * the end until they have the same size. This for example might occur when
	 * a specific path has a business object attached to the process step, but
	 * there are no tables attached to the object.
	 */
	private void writeToStringBuffer(List<String> pathList) {
		final String METHOD_NAME = "writeToStringBuffer(List<String> pathList)";
		logger.entering(CLASS_NAME, METHOD_NAME);
		
		Boolean rowMustBeAppended = false;
		
		// when we export only the selected part of the tree we must check if a row must be appended
		if (this.exportNodes != WHOLE_BDR_TREE_EXPORT && this.selectedTables==false){
			String process;
			String pStep;
			String bObject;
			String table;
			int Index1= maxWidth;
			int Index2= maxWidth/2;
			//TODO: This doesn't work for x level bdrs  wee need to fix the whole selection export
			// treat the different structure of the pahtList in FULL_BDR_EXPORT and special usage export
			// EXT230: Above TODO has been fixed . Indexes has been made dynamic based on export option (complete BDR or selection in BDR)
			if (this.exportMode == ExportMode.FULL_BDR_EXPORT){
				process = pathList.size()> Index0 ? pathList.get(Index0) : "";
				pStep = pathList.size()> Index1 ? pathList.get(Index1) : "";
				bObject = pathList.size()> (Index1+3) ? pathList.get(Index1+3) : "";
				table = pathList.size()> (Index1+6) ? pathList.get(Index1+6) : "";
			} else {
				process = pathList.size()> Index0 ? pathList.get(Index0) : "";
				pStep = pathList.size()> Index2 ? pathList.get(Index2) : "";
				bObject = pathList.size()> (Index2+1) ? pathList.get(Index2+1) : "";
				table = pathList.size()> (Index2+2) ? pathList.get(Index2+2) : "";
			}
			// EXT230: check if the selected option is for Process and append when selected Process matches
			if(this.itemType.equals("Process"))
			{
				for (String[] strArray : this.nodesToExtract) {
					if(numberOfChildProcesses!=0){
						if (this.exportMode != ExportMode.FULL_BDR_EXPORT) {
							if (pathList.get(Index0).equals(strArray[Index0])) {
								if (pathList.get(strArray.length-1).equals(strArray[strArray.length-1])) {
									rowMustBeAppended = true;
								}
							}
						}
						else
							if (pathList.get(Index0).equals(strArray[Index0])) {
								if (pathList.get(((strArray.length-1)*2)).equals(strArray[strArray.length-1])) {
									rowMustBeAppended = true;
								}
							}
					}
					else{
						if (pathList.get(Index0).equals(strArray[Index0])) {
							rowMustBeAppended = true;
						}
					}
				}
			}
			//check if the selected option is for Process Step and append when selected Process Step matches
			else if(this.itemType.equals("ProcessStep")) {
				for (String[] strArray : this.nodesToExtract) {
					{
						if (process.equals(strArray[0])) {
							if (pStep.equals(strArray[Index2])) {						      
								rowMustBeAppended = true;
							}
						}
					}
				}
			}
			//check if the selected option is for Business Object and append when selected Business Object matches
			else if(this.itemType.equals("BusinessObject")){
				for (String[] strArray : this.nodesToExtract) {
					{
						if (process.equals(strArray[0])) {
							if (pStep.equals(strArray[Index2])) {
								if (bObject.equals(strArray[Index2+1])) {
									rowMustBeAppended = true;
								}
							}
						}
					}
				}
			}
			// check if the selected option is for Table and append when selected Table matches
			else if(this.itemType.equals("TableUsage")){
				for (String[] strArray : this.nodesToExtract) {
					if (process.equals(strArray[0])) {
						if (pStep.equals(strArray[Index2])) {
							if (bObject.equals(strArray[Index2+1])) {
								if (table.equals(strArray[Index2+2])) {
									rowMustBeAppended = true;
								}
							}
						}
					}
				}
			}
		}
		// END EXT230
		
		if (this.exportNodes == WHOLE_BDR_TREE_EXPORT || rowMustBeAppended || this.selectedTables) {
			StringBuffer newLine = new StringBuffer();
			int pathElements = 0;

			for (String columnElement : pathList) {
				pathElements++;
				if (pathElements == 1) {
					// no Constants.BDR_EXPORT_CSV_PATH_SEPARATOR at first position
					newLine.append("\"").append(columnElement).append("\"");
				} else {
					newLine.append(Constants.BDR_EXPORT_CSV_PATH_SEPARATOR).append("\"").append(columnElement).append("\"");
				}
			}

			// set the length of the items in a row in the csv file
			int rowLength = Constants.BDR_EXPORT_WIDTH_PROCESSSTEPS_TO_FIELDS;
			if (this.exportOnlyTables) {
				rowLength = Constants.BDR_TABLES_EXPORT_WIDTH;
			}
			if (this.exportMode != ExportMode.FULL_BDR_EXPORT) {
				rowLength = Constants.BDR_SPECIAL_SCOPE_EXPORT_WIDTH_PROCESSSTEPS_TO_FIELDS;
			}

			while (pathElements < (maxWidth + rowLength)) {
				pathElements++;
				newLine.append(Constants.BDR_EXPORT_CSV_PATH_SEPARATOR).append("\"\"");
			}
			
			logger.finest("Appending line: " + newLine.toString());
			fileContent.append(newLine.toString()).append(Constants.BDR_EXPORT_LINE_SEPARATOR);		
		}
		logger.exiting(CLASS_NAME, METHOD_NAME);
	}

	/**
	 * The length of the different paths in the tree might vary. In order to
	 * export the paths to a CSV-File, all of them should have the same length.
	 * Thus, some paths need to be filled up with placeholders (gap fillers).
	 * The difference to what the writeToStringBuffer function does is, that the
	 * gaps should be added between process and process step.
	 */
	private List<String> fillWithGaps(List<String> pathList) {
		// maximum variable depth of tree - current pathlist size (number of elements/columns)
		// determines how many gaps need to be added to have the same length for all rows
		int gapsToFill = maxWidth - pathList.size();

		if (gapsToFill > 0) {
			for (int i = 0; i < gapsToFill; i++) {
				pathList.add(Constants.BDR_EXPORT_GAP_FILLER);
			}
		}
		return pathList;
	}

	private synchronized void publishTopic(BayeuxJmsTextMsg message) {
		try {
			this.publisher.publish(message);
		} catch (PublisherException e) {
			Util.handleBatchException(e);
		}
	}
}
