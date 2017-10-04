package com.ibm.is.sappack.cw.app.services.bdr.threads;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;

import javax.naming.NamingException;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpSession;
import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;

import com.ibm.is.sappack.cw.app.data.bdr.FieldUsageUseMode;
import com.ibm.is.sappack.cw.app.data.bdr.jpa.BusinessObject;
import com.ibm.is.sappack.cw.app.data.bdr.jpa.Field;
import com.ibm.is.sappack.cw.app.data.bdr.jpa.FieldUsage;
import com.ibm.is.sappack.cw.app.data.bdr.jpa.Table;
import com.ibm.is.sappack.cw.app.services.AbstractCancelableThread;
import com.ibm.is.sappack.cw.app.services.CWDBConnectionFactory;
import com.ibm.is.sappack.cw.app.services.Constants;
import com.ibm.is.sappack.cw.app.services.CwApp;
import com.ibm.is.sappack.cw.app.services.Util;
import com.ibm.websphere.webmsg.publisher.Publisher;
import com.ibm.websphere.webmsg.publisher.PublisherException;
import com.ibm.websphere.webmsg.publisher.jndijms.BayeuxJmsTextMsg;

public class BdrExportToCwdbThread extends AbstractCancelableThread{

	private static final String TOPIC_BDR_EXPORT_STARTED = "/bdr/bdrexport/started";
	private static final String TOPIC_BDR_EXPORT_FINISHED = "/bdr/bdrexport/finished";
	private static final int DB_BATCH_SIZE = 999;
	String sapSystemId = "";
	String rollout = "";
	boolean separateScopes = false;
	private String STATUS_MESSAGE = "";
	Logger logger = null;

	public BdrExportToCwdbThread(HttpSession session, Publisher publisher, String sapSystemId, String rollout, boolean separateScopes) {
		super(session, publisher);
		this.rollout = rollout;
		this.sapSystemId = sapSystemId;
		this.separateScopes = separateScopes;
		logger = CwApp.getLogger();
	}

	private static final String CLASS_NAME = BdrExportToCwdbThread.class.getName();

	@Override
	public void run() {
		final String METHOD_NAME = "run()";
		logger.entering(CLASS_NAME, METHOD_NAME);

		// publish a message that we're starting the export
		this.publishTopic(new BayeuxJmsTextMsg(TOPIC_BDR_EXPORT_STARTED + this.sessionId, "started"));


		// start actual export
		try {
			this.exportBdr(sapSystemId, rollout, separateScopes);
			logger.finest("Export to CWDB Finished");
		}   catch (Exception e) {
			e.printStackTrace();
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
		session.removeAttribute(Constants.SESSION_ATTRIBUTE_NAME_BDR_DB_EXPORT_THREAD);

		logger.exiting(CLASS_NAME, METHOD_NAME);
	}


	// Exports the BDR tree to the CWDB tables.
	public void exportBdr(String sapSystemId, String rollout, boolean separateScopes) throws NotSupportedException, SystemException {
		jpaTransaction.begin();
		logger.finest("+++++++++++++++++++++ Starting BDR export to CWDB");
		final String METHOD_NAME = "exportBdr(String sapSystemId, String rollout)"; 
		logger.entering(CLASS_NAME, METHOD_NAME);
		int recordCounter = 0;
		Connection conn = null;
		PreparedStatement deleteTables = null;
		PreparedStatement deleteFields = null;
		PreparedStatement exportTable = null;
		PreparedStatement exportField = null;//

		try {
			conn = CWDBConnectionFactory.getConnection();

			// Delete existing data (we have the user's permission)
			// Note: the SAP system ID is ignored since CW doesn't currently support multiple
			// target systems throughout. We overwrite all the rollout's data every time. 
			String deleteTablesQuery = "DELETE FROM " + Constants.CW_TABLE_DATA_TABLES_CONFIG;
			deleteTablesQuery += " WHERE " + Constants.CW_COLUMN_ROLLOUT + " = ? ";
			deleteTables = conn.prepareStatement(deleteTablesQuery);
			deleteTables.setString(1, rollout);
			deleteTables.executeUpdate();

			String deleteFieldsQuery = "DELETE FROM " + Constants.CW_TABLE_FIELDS_CONFIG;
			deleteFieldsQuery += " WHERE " + Constants.CW_COLUMN_ROLLOUT + " = ? ";
			deleteFields = conn.prepareStatement(deleteFieldsQuery);
			deleteFields.setString(1, rollout);
			deleteFields.executeUpdate();


			String sqlStatementTableConfig = "INSERT INTO " + Constants.CW_TABLE_DATA_TABLES_CONFIG + "("
					+ Constants.CW_COLUMN_LEGACY_ID + ","
					+ Constants.CW_COLUMN_LOB + ","
					+ Constants.CW_COLUMN_ROLLOUT + ","
					+ Constants.CW_COLUMN_SAP_TABLENAME + ","
					+ Constants.CW_COLUMN_LOGICAL_TABLE + ","
					+ Constants.CW_COLUMN_ACTIVE
					+ ") VALUES (?,?,?,?,?,?)";
			exportTable = conn.prepareStatement(sqlStatementTableConfig);			

			String sqlStatementFieldConfig = "INSERT INTO " + Constants.CW_TABLE_FIELDS_CONFIG + "("
					+ Constants.CW_COLUMN_LEGACY_ID + ","
					+ Constants.CW_COLUMN_LOB + ","
					+ Constants.CW_COLUMN_ROLLOUT + ","
					+ Constants.CW_COLUMN_SAP_TABLENAME + ","
					+ Constants.CW_COLUMN_SAP_FIELDNAME + ","
					+ Constants.CW_COLUMN_MANDATORY + ","
					+ Constants.CW_COLUMN_ACTIVE + ","
					+ Constants.CW_COLUMN_SAP_VIEW
					+ ") VALUES (?,?,?,?,?,?,?,?)";
			exportField = conn.prepareStatement(sqlStatementFieldConfig);

			//	EntityManager manager = JPAResourceFactory.getEntityManager();

			TypedQuery<Table> tableQuery = manager.createNamedQuery("Table.getAll", Table.class);
			List<Table> tableList = tableQuery.getResultList();

			HashSet<String> tableSet= new HashSet<String>();
			for (Table table : tableList) {
				// Workaround for having tables attached to multiple BOs:
				// Use LOB as partitioning key
				List<BusinessObject> boList = table.getUsedInBusinessObjects();
				HashSet<String> fieldSet= new HashSet<String>();
				for (BusinessObject bo : boList) {
					// Only export tables that are attached to a business object
					String boShortName = bo.getShortName();
					boolean tableActive = false;

					// Export field config
					if (table.getFields() != null) {
						for (Field field : table.getFields()) {
							// Aggregate some field usage attributes
							boolean fieldMandatory = false;
							boolean fieldActive = false;
							if (field.getUsages() != null) {
								for (FieldUsage fieldUsage : field.getUsages()) {
									// If "separate scopes" is set, we only aggregate the in-scope values per BO
									// Otherwise, aggregate globally
									if (!separateScopes || bo.equals(fieldUsage.getTableUsage().getBusinessObject())) {
										if (fieldUsage.getRequired()) {
											// MANDATORY: Aggregation: at least one "required" value on a field usage
											fieldMandatory = true;
										}
										if (fieldUsage.getUseMode() == FieldUsageUseMode.INSCOPE) {
											// ACTIVE: Aggregation: at least one usage with READ or WRITE
											fieldActive = true;
											// ACTIVE on a table: at least one field is ACTIVE
											tableActive = true;
										}
									}
								}
							}

							logger.finest("Field config: " + table.getName() + "." + field.getName());
							logger.finest(sqlStatementFieldConfig);
							String paramData=sapSystemId + "," + boShortName + "," + rollout + ","
									+ field.getTable().getName() + "," + field.getName();
							logger.finest(paramData+ "," + (fieldMandatory ? Constants.CW_TRUE_VALUE : Constants.CW_FALSE_VALUE) + ","
									+ (fieldActive ? Constants.CW_TRUE_VALUE : Constants.CW_FALSE_VALUE) + "," + field.getSapView());

							int fieldSetSize=fieldSet.size();
							fieldSet.add(paramData);
							logger.finest("fieldSetSize:"+fieldSetSize+": fieldSetSize after add:"+fieldSet.size());
							if(fieldSetSize == (fieldSet.size()-1))
							{
								logger.finest("paramData adding...");
								exportField.setString(1, sapSystemId);
								exportField.setString(2, boShortName);
								exportField.setString(3, rollout);
								exportField.setString(4, field.getTable().getName());
								exportField.setString(5, field.getName());
								exportField.setString(6, (fieldMandatory ? Constants.CW_TRUE_VALUE : Constants.CW_FALSE_VALUE)); 
								exportField.setString(7, (fieldActive ? Constants.CW_TRUE_VALUE : Constants.CW_FALSE_VALUE));
								exportField.setString(8, field.getSapView());
								exportField.addBatch();
								recordCounter++;
							}else{
								logger.warning("Exception: Duplicate data found in export field:---> "+paramData+ "," + (fieldMandatory ? Constants.CW_TRUE_VALUE : Constants.CW_FALSE_VALUE) + ","
										+ (fieldActive ? Constants.CW_TRUE_VALUE : Constants.CW_FALSE_VALUE) + "," + field.getSapView());
								throw new SQLException("Exception: Duplicate data found in export field:---> "+paramData+ "," + (fieldMandatory ? Constants.CW_TRUE_VALUE : Constants.CW_FALSE_VALUE) + ","
										+ (fieldActive ? Constants.CW_TRUE_VALUE : Constants.CW_FALSE_VALUE) + "," + field.getSapView());
							}
							if (recordCounter > DB_BATCH_SIZE) {
								try{
								exportField.executeBatch();
								}catch(SQLException ex){
									logger.finest("execute Batch: "+ex.getNextException());
								}
								logger.finest("Batch Succesfully executed.");
								recordCounter = 0;
							}
						}
						try{
						exportField.executeBatch();
						}catch(SQLException e){
							System.out.println("Error in exportField.executeBatch()"+e.getNextException());
						}
					}

					// Export table config
					logger.finest("Table config: " + table.getName());
					logger.finest(sqlStatementTableConfig);
					String paramData=sapSystemId + "," + boShortName + "," + rollout + "," + table.getName() ;
					logger.finest(paramData+ "," + table.getName() + "," + (tableActive ? Constants.CW_TRUE_VALUE : Constants.CW_FALSE_VALUE));

					int tableSetSize=tableSet.size();
					tableSet.add(paramData);
					logger.finest("tableSetSize:"+tableSetSize+": TableSetSize after add:"+tableSet.size());
					if(tableSetSize == (tableSet.size()-1))
					{
						exportTable.setString(1, sapSystemId);
						exportTable.setString(2, boShortName);
						exportTable.setString(3, rollout);
						exportTable.setString(4, table.getName());
						exportTable.setString(5, table.getName());
						exportTable.setString(6, (tableActive ? Constants.CW_TRUE_VALUE : Constants.CW_FALSE_VALUE));
						exportTable.executeUpdate();
					}else{
						logger.warning("Exception: Duplicate data found in export table:---> "+paramData+ "," + table.getName() + "," + (tableActive ? Constants.CW_TRUE_VALUE : Constants.CW_FALSE_VALUE));
						throw new SQLException("Duplicate data found in export table:---> "+paramData+ "," + table.getName() + "," + (tableActive ? Constants.CW_TRUE_VALUE : Constants.CW_FALSE_VALUE));
					}
				}
				fieldSet=null;
			}
			tableSet=null;

			conn.commit();
			logger.finest("+++++++++++++++++++++ Finished BDR export to CWDB");
			logger.exiting(CLASS_NAME, METHOD_NAME);

		} catch (SQLException e){
			this.STATUS_MESSAGE = "SQL Error "+": "+e;
			Util.throwInternalErrorToClient(conn, e);
			this.publishTopic(new BayeuxJmsTextMsg(TOPIC_BDR_EXPORT_FINISHED + this.sessionId, this.STATUS_MESSAGE));
			session.removeAttribute(Constants.SESSION_ATTRIBUTE_NAME_BDR_DB_EXPORT_THREAD);
		} catch (NamingException e){
			this.STATUS_MESSAGE = "Naming Error "+": "+e;
			Util.throwInternalErrorToClient(e);
			this.publishTopic(new BayeuxJmsTextMsg(TOPIC_BDR_EXPORT_FINISHED + this.sessionId, this.STATUS_MESSAGE));
			session.removeAttribute(Constants.SESSION_ATTRIBUTE_NAME_BDR_DB_EXPORT_THREAD);
		} catch (Exception e) {
			this.STATUS_MESSAGE = "Internal Error "+": "+e;
			this.publishTopic(new BayeuxJmsTextMsg(TOPIC_BDR_EXPORT_FINISHED + this.sessionId, this.STATUS_MESSAGE));
			session.removeAttribute(Constants.SESSION_ATTRIBUTE_NAME_BDR_DB_EXPORT_THREAD);
		}
		finally {
			jpaTransaction.rollback();
			Util.closeDBObjects(deleteTables, deleteFields, exportTable, exportField, conn);
		}
	}

	private synchronized void publishTopic(BayeuxJmsTextMsg message) {
		try {
			this.publisher.publish(message);
		} catch (PublisherException e) {
			Util.handleBatchException(e);
		}
	}

}
