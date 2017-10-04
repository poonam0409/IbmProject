package com.ibm.is.sappack.cw.app.services.bdr.threads;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpSession;

import com.ibm.is.sappack.cw.app.data.bdr.jpa.Table;
import com.ibm.is.sappack.cw.app.data.rdm.ITable;
import com.ibm.is.sappack.cw.app.data.rdm.ReferenceTableType;
import com.ibm.is.sappack.cw.app.data.rdm.jpa.ReferenceTable;
import com.ibm.is.sappack.cw.app.services.AbstractCancelableThread;
import com.ibm.is.sappack.cw.app.services.Constants;
import com.ibm.is.sappack.cw.app.services.Util;
import com.ibm.websphere.webmsg.publisher.Publisher;
import com.ibm.websphere.webmsg.publisher.PublisherException;
import com.ibm.websphere.webmsg.publisher.jndijms.BayeuxJmsTextMsg;

public class TablesImporterThread extends AbstractCancelableThread {

	private static final String CLASS_NAME = TablesImporterThread.class.getName();

	private static final String TOPIC_TABLES_IMPORT_STARTED = "/bdr/tablesimport/started";
	private static final String TOPIC_TABLES_IMPORT_FINISHED = "/bdr/tablesimport/finished";

	private final HashMap<String, ReferenceTable> referenceTables;

	public TablesImporterThread(Publisher publisher, HttpSession session, HashMap<String, ReferenceTable> referenceTables) {
		super(session, publisher);
		this.referenceTables = referenceTables;
	}
	
	@Override
	public void run() {
		final String METHOD_NAME = "run()"; 
		logger.entering(CLASS_NAME, METHOD_NAME);
		
		// publish a message that we're starting the import of all fields for a given SAP table
		this.publishTopic(new BayeuxJmsTextMsg(TOPIC_TABLES_IMPORT_STARTED + this.sessionId, "started"));
		
		// loop over the list of reference tables and import the data originally
		// fetched from SAP into the target database
		Set<Entry<String, ReferenceTable>> set = referenceTables.entrySet();
		Iterator<Entry<String, ReferenceTable>> it = set.iterator();
		
		while (it.hasNext()) {
			if (this.cancelled) {
				break;
			}
			
			ReferenceTable table = it.next().getValue();
			
			// we only import tables of type CHECKTABLE and corresponding text tables
			if (table.getTableType() == ReferenceTableType.CHECK_TABLE) {
				importTable(table, null);

				if (table.hasTextTable()) {
					importTable(table.getTextTable(), table.getSapName());
				}
			}
		}
		
		// publish a message that we're finished
		this.publishTopic(new BayeuxJmsTextMsg(TOPIC_TABLES_IMPORT_FINISHED + this.sessionId, "successful"));
		session.removeAttribute(Constants.SESSION_ATTRIBUTE_NAME_BDR_TABLES_IMPORT_THREAD);
	}

	private boolean importTable(ITable table, String refTableName) {
		boolean successful = true;
		
		// create a new BPH table object and fill it with the relevant data from the RDM table
		Table newTable = new Table();
		newTable.setName(table.getSapName());
		if (table instanceof ReferenceTable) {
			// Reference table
			newTable.setDescription(((ReferenceTable)table).getDescription());
		} else {
			// Text table
			newTable.setDescription("Text table for reference table " + refTableName);
		}
		newTable.setUpdated(new Date());

		try {
			jpaTransaction.begin();
			manager.joinTransaction();
			manager.persist(newTable);
			
			// at this point we need to check whether we need to commit the changes to the database or not
			// in case the task has been cancelled we rollback all changes
			if (this.cancelled) {
				this.jpaTransaction.rollback();
				successful = false;
			}
			else {
				this.jpaTransaction.commit();
			}
		}
		catch (Exception e) {
			successful = false;
			Util.handleBatchException(jpaTransaction, e);
		}

		return successful;
	}

	private synchronized void publishTopic(BayeuxJmsTextMsg message) {
		try {
			this.publisher.publish(message);
		} catch (PublisherException e) {
			Util.handleBatchException(e);
		}
	}	
}
