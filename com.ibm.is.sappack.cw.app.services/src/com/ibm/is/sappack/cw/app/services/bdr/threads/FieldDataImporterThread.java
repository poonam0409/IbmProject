package com.ibm.is.sappack.cw.app.services.bdr.threads;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;

import com.ibm.is.sappack.cw.app.data.SAPField;
import com.ibm.is.sappack.cw.app.data.bdr.jpa.Field;
import com.ibm.is.sappack.cw.app.data.bdr.jpa.Table;
import com.ibm.is.sappack.cw.app.data.config.LegacySystem;
import com.ibm.is.sappack.cw.app.services.AbstractCancelableThread;
import com.ibm.is.sappack.cw.app.services.Constants;
import com.ibm.is.sappack.cw.app.services.RfcDestinationDataProvider;
import com.ibm.is.sappack.cw.app.services.Util;
import com.ibm.is.sappack.cw.app.services.bdr.jaxrs.FieldService;
import com.ibm.is.sappack.gen.tools.sap.utilities.IResult;
import com.ibm.is.sappack.gen.tools.sap.utilities.SAPTableFieldsExtractor;
import com.ibm.websphere.webmsg.publisher.Publisher;
import com.ibm.websphere.webmsg.publisher.PublisherException;
import com.ibm.websphere.webmsg.publisher.jndijms.BayeuxJmsTextMsg;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;

public class FieldDataImporterThread extends AbstractCancelableThread {

	private static final String CLASS_NAME = FieldDataImporterThread.class.getName();
	
	private static enum LOAD_STATUS {
		LOADING(0), SUCCESSFUL(1), TABLE_NOT_FOUND(2) , FAILED(3);

		private final int value;

		LOAD_STATUS(int value) {
			this.value = value;
		}
	}

	private static final String SETTER_PREFIX = "set";
	
	private static final String TOPIC_FIELD_IMPORT_STARTED = "/bdr/fieldimport/started";
	private static final String TOPIC_FIELD_IMPORT_FINISHED = "/bdr/fieldimport/finished";
	private static final String TOPIC_TABLE_STATUS_UPDATE = "/bdr/tablefieldimport/updated";
	private static final String  TOPIC_TABLE_LOAD_UPDATE = "/bdr/tablefieldimport/loadupdate";
	
	private final List<Table> bdrTables;


	private HashMap<String, JCoDestination> sapSystemDestinations;
	private LegacySystem sapSystem;

	private final String loadLanguage;
	private int newFields;
	
	public FieldDataImporterThread(Publisher publisher, List<Table> tables, LegacySystem sapSystem, HttpSession session, String rdmLanguage) {
		super(session, publisher);
		this.bdrTables = tables;
		this.loadLanguage = rdmLanguage;
		this.sapSystem = sapSystem;
   }

	@Override
	public void run() {
		final String METHOD_NAME = "run()"; 
		logger.entering(CLASS_NAME, METHOD_NAME);
		int tableNumber = 1;
		for (Table table : bdrTables){
			
			this.publishTopic(new BayeuxJmsTextMsg(TOPIC_TABLE_STATUS_UPDATE + this.sessionId, composeTableJSONObjekt(table, LOAD_STATUS.LOADING, tableNumber, bdrTables.size())));
			this.newFields = 0;
			
			ArrayList<Map<String, String>> extractedSapTable;
		
			// publish a message that we're starting the import of all fields for a given SAP table
			this.publishTopic(new BayeuxJmsTextMsg(TOPIC_FIELD_IMPORT_STARTED + this.sessionId, "started"));
		
			try {
				this.sapSystemDestinations = new HashMap<String, JCoDestination>();
				JCoDestination dest = RfcDestinationDataProvider.getDestination(sapSystem);
				this.sapSystemDestinations.put(sapSystem.getLegacyId(), dest);
			
				extractedSapTable = extractSapTable(table);
			
				// do the actual import
				if (this.importFieldsForTable(extractedSapTable, table)) {
					
					// publish the import success to the client
					this.publishTopic(new BayeuxJmsTextMsg(TOPIC_FIELD_IMPORT_FINISHED + this.sessionId, "successful"));
					this.publishTopic(new BayeuxJmsTextMsg(TOPIC_TABLE_STATUS_UPDATE + this.sessionId, composeTableJSONObjekt(table, LOAD_STATUS.SUCCESSFUL, tableNumber, bdrTables.size())));
				}
				else {
				
					// publish the import failure to the client
					this.publishTopic(new BayeuxJmsTextMsg(TOPIC_FIELD_IMPORT_FINISHED + this.sessionId, "failed"));
					this.publishTopic(new BayeuxJmsTextMsg(TOPIC_TABLE_STATUS_UPDATE + this.sessionId, composeTableJSONObjekt(table, LOAD_STATUS.TABLE_NOT_FOUND, tableNumber, bdrTables.size())));
				}
			}
			catch (JCoException e) {
				if (e.getGroup() == JCoException.JCO_ERROR_ABAP_EXCEPTION) {
					logger.fine("ABAP Exception. Table may not exist in Target System.");
					this.publishTopic(new BayeuxJmsTextMsg(TOPIC_FIELD_IMPORT_FINISHED + this.sessionId, "table_not_found"));
					this.publishTopic(new BayeuxJmsTextMsg(TOPIC_TABLE_STATUS_UPDATE + this.sessionId, composeTableJSONObjekt(table, LOAD_STATUS.TABLE_NOT_FOUND, tableNumber, bdrTables.size())));
					
				} else {
					logger.fine("Unknown JCo error.");
					this.publishTopic(new BayeuxJmsTextMsg(TOPIC_FIELD_IMPORT_FINISHED + this.sessionId, "failed"));
				}
				Util.handleBatchException(e);
			}
			
			tableNumber++;
		}

		// remove the the reference to this thread from the session, so garbage collector can clean up
		session.removeAttribute(Constants.SESSION_ATTRIBUTE_NAME_BDR_FIELD_IMPORT_THREAD);
		logger.exiting(CLASS_NAME, METHOD_NAME);
	}

	private ArrayList<Map<String, String>> extractSapTable(Table bdrTable) throws JCoException {
		JCoDestination sapDestination = null;
		IResult result = null;

		sapDestination = this.sapSystemDestinations.get(this.sapSystem.getLegacyId());

		SAPTableFieldsExtractor sfExtractor = new SAPTableFieldsExtractor(sapDestination, bdrTable.getName(), this.loadLanguage);
		result = sfExtractor.performQuery();
		
		// we are building an ArrayList of the extracted values, because we
		// could iterate over the result only one time, but we need it twice
		// so we avoid to extract one more time
		ArrayList<Map<String, String>> sapExtract = new ArrayList<Map<String, String>>();
		
		if (result != null) {
			while (result.nextRow()) {
				sapExtract.add(result.getRow());
			}
		}
		
		return sapExtract;
	}
	
	// Updates this.bdrTable using the given field list from SAP.
	// If there are existing fields, the field metadata is preserved as long as the field still exists in SAP.
	private boolean importFieldsForTable(ArrayList<Map<String, String>> extractedSapTable, Table table) {
		boolean successful = true;
		FieldService fieldService = new FieldService();
		
		// check for the state of the thread
		if (this.cancelled) {
			return successful;
		}

		Collection<Field> existingFields = new ArrayList<Field>(fieldService.getFieldsByTableId(table.getTableId()));
		
		try {
			logger.finest("Importing fields from SAP");
			
			// check for the state of the thread
			if (this.cancelled) {
				return successful;
			}

			// check the data fetching results from SAP and go through it one by one
			if (extractedSapTable != null) {
				int sapRowNumber = 1;
				for (Map<String, String> sapRow : extractedSapTable) {					
					
					// create a new field
					Field f = new Field();
					
					Method[] objectMethods = f.getClass().getDeclaredMethods();
					
					for (int i = 0; i < objectMethods.length; i++) {
						Method m = objectMethods[i];
						String methodName = m.getName();
						
						// we only check methods with the prefix 'set' 
						if (m.getName().startsWith(SETTER_PREFIX)) {
							String baseName = methodName.substring(SETTER_PREFIX.length());
							
							// check if our custom annotation 'SAPField' is present
							if (m.isAnnotationPresent(SAPField.class)) {
								Annotation annot = m.getAnnotation(SAPField.class);
								String objectName = ((SAPField) annot).objectName();
								
								// invoke the method on the new Field object
								if (baseName.equalsIgnoreCase(objectName)) {
									
									// as Oracle treats empty strings as NULL values we need to look for them
									// and convert them to strings containing something (in this case an arbitrary blank character)
									// please note that for DB2 this isn't necessary, but we do it anyway for consistency								
									String value = sapRow.get(objectName);
									
									if (value == null || value.isEmpty()) {
										value = Constants.ORACLE_EMPTY_STRING;
									}
									
									m.invoke(f, value);
								}
							}
						}
					}
					
					if (!existingFields.isEmpty()) {
						// check if the field existed before
						boolean found = false;
						Iterator<Field> existingFieldIterator = existingFields.iterator();
						
						while (existingFieldIterator.hasNext()) {
							Field existingField = existingFieldIterator.next();
							
							if (f.getName().equals(existingField.getName())) {
								found = true;
								existingFieldIterator.remove();
							}
						}
						
						// if the field didn't exist before we add it
						if (!found) {
							fieldService.createField(table.getTableId(), f);
							this.newFields++;
						}
					} else {
						fieldService.createField(table.getTableId(), f);
						this.newFields++;
					}
					
					if (sapRowNumber%10==0){
						this.publishTopic(new BayeuxJmsTextMsg(TOPIC_TABLE_LOAD_UPDATE + this.sessionId, composeTableLoadUpdateJSONObjekt(sapRowNumber, extractedSapTable.size())));

					}

					sapRowNumber++;
				}
				
				if (this.cancelled) {
					successful = false;
				}
				
				// see if there are any fields left that existed before
				// (the list is supposed to be empty if all the previously existing fields
				// were about to be retained, if it is not empty it only contains fields
				// that are to be deleted because they are not part of the SAP table definition)
				if (!existingFields.isEmpty()) {
					Iterator<Field> existingFieldIterator = existingFields.iterator();
					
					while (existingFieldIterator.hasNext()) {
						Field existingFieldToBeDeleted = existingFieldIterator.next();
						
						// delete the field
						fieldService.deleteField(table.getTableId(), existingFieldToBeDeleted.getFieldId());
					}
				}
			}
		} catch (IllegalArgumentException e) {
			successful = false;
			Util.handleBatchException(e);
		} catch (IllegalAccessException e) {
			successful = false;
			Util.handleBatchException(e);
		} catch (InvocationTargetException e) {
			successful = false;
			Util.handleBatchException(e);
		}

		return successful;
	}
	
	private String composeTableJSONObjekt (Table table, LOAD_STATUS loadStatus, int progress, int maximum){
		JSONObject updateJson = new JSONObject();
		try{
			updateJson.put("name", table.getName());
			updateJson.put("status", loadStatus.value);
			updateJson.put("rows", String.valueOf(this.newFields));
			updateJson.put("maximum", maximum);
			if (loadStatus == LOAD_STATUS.LOADING){
				progress--;
			}
			updateJson.put("progress", progress);
			
		}
	
		catch (JSONException e) {
			Util.handleBatchException(e);
		}
		
		return updateJson.toString();
	}
	
	private String composeTableLoadUpdateJSONObjekt (int progress, int maximum){
		JSONObject updateJson = new JSONObject();
		try{
			updateJson.put("maximum", maximum);
			updateJson.put("progress", progress);
		}
		catch (JSONException e) {
			Util.handleBatchException(e);
		}
		return updateJson.toString();
	}
	
	private synchronized void publishTopic(BayeuxJmsTextMsg message) {
		try {
			this.publisher.publish(message);
		} catch (PublisherException e) {
			Util.handleBatchException(e);
		}
	}	
}
