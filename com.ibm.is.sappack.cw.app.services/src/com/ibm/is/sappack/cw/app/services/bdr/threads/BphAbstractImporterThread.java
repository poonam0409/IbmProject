package com.ibm.is.sappack.cw.app.services.bdr.threads;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.servlet.http.HttpSession;

import com.ibm.is.sappack.cw.app.data.bdr.jpa.BusinessObject;
import com.ibm.is.sappack.cw.app.data.bdr.jpa.Process;
import com.ibm.is.sappack.cw.app.data.bdr.jpa.Table;
import com.ibm.is.sappack.cw.app.data.bdr.jpa.TableUsage;
import com.ibm.is.sappack.cw.app.services.AbstractCancelableThread;
import com.ibm.is.sappack.cw.app.services.Constants;
import com.ibm.is.sappack.cw.app.services.ImportBusinessObjectProvider;
import com.ibm.is.sappack.cw.app.services.ImportFieldProvider;
import com.ibm.is.sappack.cw.app.services.ImportFieldUsageProvider;
import com.ibm.is.sappack.cw.app.services.ImportProcessProvider;
import com.ibm.is.sappack.cw.app.services.ImportProcessStepProvider;
import com.ibm.is.sappack.cw.app.services.ImportTableProvider;
import com.ibm.is.sappack.cw.app.services.ImportTableUsageProvider;
import com.ibm.is.sappack.cw.app.services.Util;
import com.ibm.is.sappack.cw.app.services.bdr.BphImportFileContainer;
import com.ibm.is.sappack.cw.app.services.bdr.BphImportType;
import com.ibm.websphere.webmsg.publisher.Publisher;
import com.ibm.websphere.webmsg.publisher.PublisherException;
import com.ibm.websphere.webmsg.publisher.jndijms.BayeuxJmsObjectMsg;

public abstract class BphAbstractImporterThread extends AbstractCancelableThread {

	private final String CLASS_NAME = BphAbstractImporterThread.class.getName();
	
	public static BphImportType importType;
	public static Boolean overwrite;
	// Bayeux topic constants relating to CometD
	protected static final String TOPIC_BPH_IMPORT_STARTED = "/bdr/bphimport/started";
	protected static final String TOPIC_BPH_IMPORT_FINISHED = "/bdr/bphimport/finished";
	protected static final String BAYEUX_MESSAGE_STARTED = "started";

	// List for putting status into such as number of imported Processes, BO's, Tables
	// and a message which tells the client whether the import was successful or not
	protected ArrayList<String> statusInfo;
	
	protected Date creationDate;

	public static int counterProcessesAndSteps = 0; // Counts number of added top level processes
	public static int counterBusinessObjects = 0; // Counts number of added business objects
	public static int counterTables = 0; // Counts number of added tables
	public static int counterTableUsages = 0; // Counts number of added table usages
	
	protected ImportProcessProvider importProcessProvider;
	protected ImportProcessStepProvider importProcessStepProvider;
	protected ImportBusinessObjectProvider importBusinessObjectProvider;
	protected ImportTableProvider importTableProvider;
	protected ImportFieldProvider importFieldProvider;
	protected ImportFieldUsageProvider importFieldUsageProvider;
	protected ImportTableUsageProvider importTableUsageProvider;
	
	public static HashMap<String, BusinessObject> businessObjectMap;
	public static HashMap<String, Table> tableMap;
	
	
	public BphAbstractImporterThread(Publisher publisher, HttpSession session) {
		super(session, publisher);
		this.importProcessProvider = ImportProcessProvider.getInstance();
		this.importProcessStepProvider = ImportProcessStepProvider.getInstance();
		this.importBusinessObjectProvider = ImportBusinessObjectProvider.getInstance();
		this.importTableProvider = ImportTableProvider.getInstance();
		this.importFieldProvider = ImportFieldProvider.getInstance();
		this.importFieldUsageProvider = ImportFieldUsageProvider.getInstance();
		this.importTableUsageProvider = ImportTableUsageProvider.getInstance();
	}
	
	@Override
	public void run() {
		try {
			final String METHOD_NAME = "run()";
			this.logger.entering(this.CLASS_NAME, METHOD_NAME);
			
			// Create Date object that will be used for every new object (Process, Table,...)
			creationDate = new Date();
			// This list is used to return status to client
			statusInfo = new ArrayList<String>();
			
			// Initialise HashMaps that contain all BusinessObjects and Tables for the providers
			businessObjectMap = new HashMap<String, BusinessObject>();
			tableMap = new HashMap<String, Table>();
			
			// Reset counters
			counterProcessesAndSteps = 0;
			counterBusinessObjects = 0;
			counterTables = 0;
			counterTableUsages = 0;
			
			// Publish a message that we're starting the import of
			// the BPH from the import file
			this.publishTopic(new BayeuxJmsObjectMsg(TOPIC_BPH_IMPORT_STARTED + this.sessionId, "started"));
			
			// Get file from session
			BphImportFileContainer container = (BphImportFileContainer)this.session
					.getAttribute(Constants.SESSION_ATTRIBUTE_NAME_BPH_IMPORT_FILE);
			byte[] fileContent = container.getFileContent();
			String fileString = null;
			
			// The Charset used in the POST will match that of the Charset specified in the HTML hosting the form.
			// Hence if the form is sent using UTF-8 encoding that is the encoding used for the posted content.
			// The URL encoding is applied after the values are converted to the set of octets for the character encoding.
			fileString = new String(fileContent, "UTF-8");
			// Remove all carriage return (CR, "\r") symbols to normalize the file
			// for compatibility between different operating systems.
			fileString = fileString.replaceAll("\r", "");
			String[] lines = fileString.split(Constants.BDR_EXPORT_LINE_SEPARATOR);
			lines = combineBrokenLines(lines);
			
			// Check validation of file
			if(checkValidationAndSetStatus(lines)) {
			try
			{
				// Increase timeout to allow large imports
				jpaTransaction.setTransactionTimeout(300);
				jpaTransaction.begin();
				// Starting the import processing (main part)
				handleImport(lines);
				jpaTransaction.commit();
				// After parsing a CSV file, problems with FieldUsages may appear.
				// Scenario: Existing BPH with TableUsage 'KNA1' and exactly one Field 'Field1'
				// The Table is used in the BPH at least once.
				// Now import a BPH with TableUsage 'KNA1' which has many fields.
				// The newly imported 'KNA1' will have all FieldUsages including 'Field1'
				// while the old one will still only have 'Field1'.
				// Solution: Get all TableUsages and call TableUsage.updateFields()
				// This must happen after the commit because the query will only return
				// persisted TableUsages and otherwise a JPA error will be thrown.
				if(importType.getImportType().equals(BphImportType.CSV_COMPLETE.getImportType())
						|| importType.getImportType().equals(BphImportType.CSV_BOS.getImportType())) {
					jpaTransaction.begin();
					manager.joinTransaction();
					TypedQuery<TableUsage> query = manager.createNamedQuery("TableUsage.getAll", TableUsage.class);
					List<TableUsage> allTableUsages = query.getResultList();
					if(allTableUsages != null) {
						for(TableUsage tableUsage : allTableUsages) {
							tableUsage.updateFields();
							manager.merge(tableUsage);
						}
					}
					jpaTransaction.commit();
					
			}}catch(Exception e)
			{
				jpaTransaction.rollback();
				this.statusInfo.add(Constants.BDR_IMPORT_ERROR_INVALID_DATA_FILE);	
			}
			}
			
			// Set status which will be returned to client in the next step
			updateStatus();
			
			// Publish the import status to the client
			this.publishTopic(new BayeuxJmsObjectMsg(TOPIC_BPH_IMPORT_FINISHED + this.sessionId, formatStatus()));
			
			// Remove the the reference to this thread from the session, so garbage
			// collector can clean up
			try{
			session.removeAttribute(Constants.SESSION_ATTRIBUTE_NAME_BPH_IMPORT_THREAD);
			}catch(Exception e)
			{
				this.logger.fine("Error while remove session attribute.");
			}
			this.logger.exiting(this.CLASS_NAME, METHOD_NAME);
		}
		catch (Exception e) {
			// Set error message
			this.statusInfo.clear();
			this.statusInfo.add(Constants.BDR_IMPORT_ERROR_INTERNAL);
			// Try to inform user about the error and then roll back the transaction
			try {
				this.publishTopic(new BayeuxJmsObjectMsg(TOPIC_BPH_IMPORT_FINISHED + this.sessionId, formatStatus()));
			}
			catch (PublisherException publisherException) {
				publisherException.printStackTrace();
			}
			
			Util.handleBatchException(jpaTransaction, e);
		}
	}

	// The values in the input file can contain newlines, so join any such pieces back together
	private String[] combineBrokenLines(String[] lines) {
		final String METHOD_NAME = "combineBrokenLines(String[] lines)";
		this.logger.entering(this.CLASS_NAME, METHOD_NAME);
		
		List<String> fixedLines = new ArrayList<String>();
		String tempLine = "";
		for (String line : lines) {
			// Split at quotes
			this.logger.finest("line: " + line);
			String[] pieces = line.split("\"", -1);
			this.logger.finest("pieces: " + pieces.length);
			if (pieces.length % 2 == 0) {
				// Even number of pieces means uneven number of quotes,
				// so a value in the contains a newline and continues in the next line
				if (tempLine.equals("")) {
					// First piece, remember it
					tempLine = line;
				} else {
					// Last piece of a split line, concatenate
					tempLine += "\n" + line;
					this.logger.finest("Adding fixed line: " + tempLine);
					fixedLines.add(tempLine);
					tempLine = "";
				}
			} else {
				// Either a regular line or a piece of a split line
				if (tempLine.equals("")) {
					// regular line
					this.logger.finest("Adding regular line: " + line);
					fixedLines.add(line);
				} else {
					// Piece of a split line, concatenate
					tempLine += "\n" + line;
				}
			}
		}
		this.logger.exiting(this.CLASS_NAME, METHOD_NAME);
		return fixedLines.toArray(new String[1]);
	}
	
	abstract void updateStatus();
	
	abstract void handleImport(String[] lines) throws Exception;
	
	abstract boolean checkValidationAndSetStatus(String[] lines);
	
	protected void removeQuotes(String[] columns) {
		final String METHOD_NAME = "removeQuotes(String[] columns)";
		this.logger.entering(this.CLASS_NAME, METHOD_NAME);
		
		for(int i = 0; i < columns.length; i++) {
			columns[i] = columns[i].replaceAll("\"", "");
		}
		logger.exiting(CLASS_NAME, METHOD_NAME);
	}
	
	protected String formatStatus() {
		final String METHOD_NAME = "formatStatus()";
		this.logger.entering(this.CLASS_NAME, METHOD_NAME);
		
		// Helper method to format the message before returning it
		String status = this.statusInfo.toString();
		status = status.replace("[", "");
		status = status.replace("]", "");
		status = status.replace(" ", "");
		
		this.logger.exiting(CLASS_NAME, METHOD_NAME);
		return status;
	}
	
	protected void persistTopLevelProcessWithChildren(Process process) {
		final String METHOD_NAME = "persistTopLevelProcessWithChildren(Process process)";
		this.logger.entering(this.CLASS_NAME, METHOD_NAME);
		
		manager.joinTransaction();
		manager.persist(process);
		logger.finest("Persisted top level process: " + process.getName()+ "("+process.getProcessId()+")");
		
		logger.exiting(CLASS_NAME, METHOD_NAME);
	}
	
	protected void persist(Object obj) throws SecurityException, IllegalStateException {
		final String METHOD_NAME = "persist(Object obj)";
		this.logger.entering(this.CLASS_NAME, METHOD_NAME);
		
		manager.joinTransaction();
		manager.persist(obj);
		logger.exiting(CLASS_NAME, METHOD_NAME);
	}

	protected synchronized void publishTopic(BayeuxJmsObjectMsg bayeuxJmsObjectMsg) throws PublisherException {
		this.publisher.publish(bayeuxJmsObjectMsg);
	}
	
}
