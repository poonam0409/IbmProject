package com.ibm.is.sappack.cw.app.services.rdm.threads;

import java.util.List;

import javax.persistence.TypedQuery;
import javax.servlet.http.HttpSession;

import com.ibm.is.sappack.cw.app.data.rdm.jpa.RdmMapping;
import com.ibm.is.sappack.cw.app.data.rdm.jpa.ReferenceTable;
import com.ibm.is.sappack.cw.app.data.rdm.jpa.TranscodingTable;
import com.ibm.is.sappack.cw.app.services.AbstractCancelableThread;
import com.ibm.is.sappack.cw.app.services.Constants;
import com.ibm.is.sappack.cw.app.services.Util;
import com.ibm.is.sappack.cw.app.services.rdm.DBOperations;
import com.ibm.websphere.webmsg.publisher.Publisher;
import com.ibm.websphere.webmsg.publisher.PublisherException;
import com.ibm.websphere.webmsg.publisher.jndijms.BayeuxJmsTextMsg;

public class ReferenceDataCleanupThread extends AbstractCancelableThread {
	
	private static final String CLASS_NAME = ReferenceDataCleanupThread.class.getName();
	
	private static final String CLEANUP_FINISHED_TOPIC = "/cleanup/done";
	private static final String CLEANUP_FINISHED_MESSAGE = "done";
	private static final String CLEANUP_FAILED_MESSAGE = "failed";

	private final List<Integer> tableIds;
	
	public ReferenceDataCleanupThread(Publisher publisher, List<Integer> tableIds, HttpSession session) {
		super(session, publisher);
		this.tableIds = tableIds;
	}

	@Override
	public void run() {
		final String METHOD_NAME = "run()"; 
		logger.entering(CLASS_NAME, METHOD_NAME);
		
		boolean successful = true;
		
		try {
			for (int id : tableIds) {
				jpaTransaction.begin();
				manager.joinTransaction();
				ReferenceTable table = manager.find(ReferenceTable.class, id);

				// a reference table has a reference to a transcoding table that can also be referred to by an RDM mapping
				// this means that before we can delete a reference table
				// (which will then cascade the delete operation to its associated transcoding table),
				// we have to delete any related RDM mappings
				if (table != null) {
					logger.fine("Erasing table: " + table.getName());
					findAndDeleteRdmMappings(table);
					manager.remove(table);
					
					// additionally we erase the table content in the CW DB
					DBOperations.clearTableInCWDB(table);
						
					// if there is a text table associated with the reference table we need to clear it, too
					if (table.hasTextTable()) {
						DBOperations.clearTableInCWDB(table.getTextTable());
					}
				}
				jpaTransaction.commit();
			}
		} catch (Exception e) {
			Util.handleBatchException(jpaTransaction, e);
			successful = false;
		} finally {
			try {
				// publish the end of the erase activity to the client
				if (successful) {
					this.publisher.publish(new BayeuxJmsTextMsg(CLEANUP_FINISHED_TOPIC + this.sessionId, CLEANUP_FINISHED_MESSAGE));
				}
				else {
					this.publisher.publish(new BayeuxJmsTextMsg(CLEANUP_FINISHED_TOPIC + this.sessionId, CLEANUP_FAILED_MESSAGE));
				}
			}
			catch (PublisherException e) {
				Util.handleBatchException(e);
			}
			
			//remove the the reference to this thread from the session, so garbage collector can clean up
			session.removeAttribute(Constants.SESSION_ATTRIBUTE_NAME_RDM_CLEANUP_THREAD);
		}
		
		logger.exiting(CLASS_NAME, METHOD_NAME);
	}

	// delete any RDM mapping entities that point to the transcoding table of the given reference table
	private void findAndDeleteRdmMappings(ReferenceTable table) {
		final String METHOD_NAME = "findAndDeleteRdmMappings(ReferenceTable table)";
		logger.entering(CLASS_NAME, METHOD_NAME);

		TranscodingTable transTable = table.getTranscodingTable();

		if (transTable != null) {
			TypedQuery<RdmMapping> mappingQuery = manager.createNamedQuery("RdmMapping.retrieveAllMappingsForTT", RdmMapping.class);
			mappingQuery.setParameter("ttname", transTable.getName());
			List<RdmMapping> rdmMappings = mappingQuery.getResultList();

			if (rdmMappings != null) {
				for (RdmMapping m : rdmMappings) {
					manager.remove(m);
				}
			}
		} else {
			// there is no transcoding table associated with this reference table
		}
		
		logger.exiting(CLASS_NAME, METHOD_NAME);
	}
}
