package com.ibm.is.sappack.cw.app.services.rdm.threads;

import java.net.HttpURLConnection;
import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.naming.NamingException;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpSession;

import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;

import com.ibm.is.sappack.cw.app.data.rdm.ReferenceTableType;
import com.ibm.is.sappack.cw.app.data.rdm.jpa.RdmMapping;
import com.ibm.is.sappack.cw.app.data.rdm.jpa.RdmSet;
import com.ibm.is.sappack.cw.app.data.rdm.jpa.ReferenceTable;
import com.ibm.is.sappack.cw.app.data.rdm.jpa.ReferenceTableColumn;
import com.ibm.is.sappack.cw.app.data.rdm.jpa.TranscodingTable;
import com.ibm.is.sappack.cw.app.services.AbstractCancelableThread;
import com.ibm.is.sappack.cw.app.services.CWDBConnectionFactory;
import com.ibm.is.sappack.cw.app.services.Constants;
import com.ibm.is.sappack.cw.app.services.CwApp;
import com.ibm.is.sappack.cw.app.services.CwAppException;
import com.ibm.is.sappack.cw.app.services.Util;
import com.ibm.is.sappack.cw.app.services.rdm.DBOperations;
import com.ibm.is.sappack.cw.app.services.rdm.clients.RdmMappingClient;
import com.ibm.websphere.webmsg.publisher.Publisher;
import com.ibm.websphere.webmsg.publisher.PublisherException;
import com.ibm.websphere.webmsg.publisher.jndijms.BayeuxJmsTextMsg;

public class RdmMappingsLoaderThread extends AbstractCancelableThread {
	private static final String MAPPING_FINISHED_TOPIC = "/mapping/success";

	private static final String FAILED_INVALID = "InvalidMappingMsg";
	private static final String FAILED_DUPLICATE = "DuplicateRelationMsg";
	private static final String FAILED_SQL = "SQLExceptionMsg";
	private static final String FAILED_CONFLICT = "ConflictMappingMsg";
	private static final String FAILED_INTERNAL = "InternalErrorMsg";
	private static final String FAILED_TT_MISSING = "NoTranscodingtableMsg";
	private static final String FAILED_EMPTY_TT = "EmptyTTMsg";
	private static final String FAILED_RDM_LOGIN = "RdmLoginMsg";
	private static final String RELATIONS_SIZE = "RELATIONS_SIZE";
	private static final String TABLE_STATUS_ROW_COUNT_TOPIC = "/table/rowcount";
	private static final String TABLE_FINISHED_TOPIC = "/table/success";

	private static final String STATUS_OK = "OK";
	private static final String STATUS_FAILED = "FAILED";
	private static final String STATUS_CANCELLED = "CANCELLED";
	private static final String OVERALLPROGRESS_ATT = "OVERALLPROGRESS";
	private static final String COUNT_ATT = "COUNT";

	private static final String TGTVALID = "targetValStdID";
	private static final String SRCVALID = "sourceValStdID";
	private static final String CODE = "Code";

	private static final String FIRST_THREE_TRANSCODINGTABLE_COLUMNS = "(SOURCE_LEGACY_ID, TARGET_LEGACY_ID, SOURCE_DESCRIPTION, ";
	private static final String PREPARED_STATEMENT_STATIC_VALUES = " VALUES (?,?,?,";

	private static final String CLASS_NAME = RdmMappingsLoaderThread.class.getName();

	private final Map<String, Map<String, JSONObject>> selectedMappings;

	private final RdmMappingClient client;
	private int overAllProgress;

	public RdmMappingsLoaderThread(RdmMappingClient client, Publisher publisher,
			Map<String, Map<String, JSONObject>> mappings, HttpSession session) {
		super(session, publisher);
		this.selectedMappings = mappings;
		this.client = client;
	}

	@Override
	public void run() {
		final String METHOD_NAME = "run()";
		logger.entering(CLASS_NAME, METHOD_NAME);
		
		try {
			// loop over all transcoding tables which the user has selected
			Iterator<Entry<String, Map<String, JSONObject>>> iter = selectedMappings.entrySet().iterator();
			
			while (iter.hasNext() && !this.cancelled) {
				// iterate over a HashMap with all mappings that are related to this tt
				Entry<String, Map<String, JSONObject>> entry = iter.next();
				Map<String, JSONObject> rdmMappingsHashMap = entry.getValue();
				JSONObject tableResponse = processTable(entry.getKey(), rdmMappingsHashMap);
				overAllProgress++;
				tableResponse.put(OVERALLPROGRESS_ATT, overAllProgress);
				
				if (tableResponse.getString(Constants.STATUS).equals(STATUS_OK)) {
					
					// status is OK we can save the entity into the cwappDB
					persistTT(entry.getKey(), tableResponse.getInt(Constants.NUMBER_OF_MAPPED_VALUES),
							tableResponse.getInt(Constants.NUMBER_OF_SRC_VALUES));
				}
				
				publishTableSuccess(tableResponse);
			}
		} catch (JSONException e) {
			Util.handleBatchException(e);
		} finally {
			
			// remove the the reference to this thread from the session, so
			// garbage collector can clean up
			session.removeAttribute(Constants.SESSION_ATTRIBUTE_NAME_RDM_TT_IMPORT_THREAD);
		}

		logger.exiting(CLASS_NAME, METHOD_NAME);
	}

	private void persistTT(String tableName, int numberOfMappedValues, int numberOfSourceValues) {
		try {
			jpaTransaction.begin();
			manager.joinTransaction();
			TranscodingTable tt = manager.find(TranscodingTable.class, tableName);
			tt.setLastLoad(new Date());
			char cond;
			
			if (numberOfMappedValues < numberOfSourceValues) {
				cond = '<';
			} else {
				if (numberOfSourceValues == 0) {
					// there have been no srcValues, so the TT is still empty
					cond = ' ';
				} else {
					cond = '=';
				}
			}
			
			tt.setCond(cond);
			manager.persist(tt);
			jpaTransaction.commit();
		} catch (Exception e) {
			Util.handleBatchException(jpaTransaction, e);
		}
	}

	private JSONObject buildTableResult(String tName, String status, JSONArray mappingsReport, int srcValues,
			int mappedValues) throws JSONException {
		final String METHOD_NAME = "buildTableResult()";
		logger.entering(CLASS_NAME, METHOD_NAME);
		
		JSONObject result = new JSONObject();
		result.put(Constants.T_TABLE_NAME, tName);
		result.put(Constants.STATUS, status);
		result.put("MAPPINGSREPORT", mappingsReport);
		result.put(Constants.NUMBER_OF_SRC_VALUES, srcValues);
		result.put(Constants.NUMBER_OF_MAPPED_VALUES, mappedValues);
		
		logger.exiting(CLASS_NAME, METHOD_NAME);
		return result;
	}
	
	private JSONObject processTable(String ttName, Map<String, JSONObject> rdmMappingsMap) throws JSONException {
		final String METHOD_NAME = "processTable(String ttName, Map<String, JSONObject> rdmMappingsMap)";
		logger.entering(CLASS_NAME, METHOD_NAME);
		
		if (!DBOperations.transcodingTableExistsInCWDB(ttName)) {
			// Transcoding table not in CWDB
			return buildTableResult(ttName, FAILED_TT_MISSING, new JSONArray(), 0, 0);
		}
		
		// a TT is in a one to one relationship to reference table
		ReferenceTable targetTable = findReferenceTableForTT(ttName);
		if (targetTable == null) {
			// no reference table found
			return buildTableResult(ttName, FAILED_INTERNAL, new JSONArray(), 0, 0);
		}
		
		Connection conn = null;
		PreparedStatement deleteStatement = null;
		
		String tableStatus = STATUS_OK;
		int overallNumSrc = 0;
		int overallNumMapped = 0;
		PreparedStatement preparedStatement = null;
		
		// create a hashmap and put there all mappings that have to be stored
		// key is the BaseID of the mapping
		HashMap<String, JSONObject> mapsToStore = new HashMap<String, JSONObject>();
		
		// mapping Ids that will be removed
		List<String> mapsToRemove = new ArrayList<String>();
		
		// create an array that will contain all mappings stats of a tt
		JSONArray ttResultJson = new JSONArray();
		
		// an object that contains information about the proccess of one mapping
		JSONObject mappingResultJson = null;
		
		JSONObject response = new JSONObject();
		
		try {
			// we need to publish the availableScrCountForTTs twice, because
			// each value can be mapped and we need to get all the relations
			// between the values
			publishTableSize(ttName, getAvailableSrcCountsForTT(rdmMappingsMap) * 2 + targetTable.getRowCount());
			conn = CWDBConnectionFactory.getConnection();
			
			// this part of the INSERT statement is equal for all mappings of
			// one transcodingtable
			String insertStmtFixPart = "INSERT INTO " + CwApp.getTranscodingTableSchema() + "." + ttName
					+ FIRST_THREE_TRANSCODINGTABLE_COLUMNS;
			
			// iterate over all mappings that are related to this
			// transcodingtable
			Entry<String, JSONObject> entry = null;
			Iterator<Entry<String, JSONObject>> mappingIter = rdmMappingsMap.entrySet().iterator();
			
			// we need to save the count of progressesValues
			// so we can publish the correct progress to the client
			Integer progressedValues = Integer.valueOf(0);
			HashMap<String, JSONObject> tgtHashMap = null;
			
			while (mappingIter.hasNext() && tableStatus.equals(STATUS_OK)) {
				
				// to make sure that the batch size of the prepared statement doesn't get too large
				// (which could be a problem for DB2) we simply execute the current batch at this point
				// we do it at this point because the current batch id from the previous loop iteration
				// which had a tableStatus of 'STATUS_OK' otherwise we wouldn't be here
				if (preparedStatement != null) {
					preparedStatement.executeBatch();
				}
				
				entry = mappingIter.next();
				JSONObject rdmMapJSON = entry.getValue();
				
				// create an object that will contain the status of one mapping
				mappingResultJson = new JSONObject();
				mappingResultJson.put(Constants.RDM_MAPPING_BASE_ID, entry.getKey());
				mappingResultJson.put(Constants.RDM_MAPPING_NAME, rdmMapJSON.getString(Constants.RDM_MAPPING_NAME));
				int numSrcValues = 0;
				int numMappedValues = 0;
				
				if ("".equals(entry.getKey())) {
					// the are no mappings for this tt
					tableStatus = FAILED_EMPTY_TT;
				} else if (rdmMapJSON.getString(Constants.STATUS).equals(Constants.STATUS_REMOVED)) {
					mappingResultJson.put(Constants.STATUS, Constants.STATUS_REMOVED);
					mapsToRemove.add(entry.getKey());
				} else {
					// we have to check again for conflicts, maybe there was a bug in the browser and the
					// user selected mappings that are in conflict
					String mappingStatus = checkMappingConflictAndType(ttName, entry.getKey(), rdmMappingsMap);
					
					if (mappingStatus.equals(STATUS_OK)) {
						// no conflicts have been found and the types of src and tgt are equal
						if (preparedStatement == null) {
							
							// the preparedStatement is null, that means this is the first mapping
							String finalSQLStatement = insertStmtFixPart + generateColumnStatement(findReferenceTableForTT(ttName));
							logger.fine(finalSQLStatement);
							preparedStatement = conn.prepareStatement(finalSQLStatement);
						}
						
						if (tgtHashMap == null) {
							String tgtRdmId = rdmMapJSON.getString(Constants.RDM_MAPPING_TGT_Version_ID);
							JSONArray valuesTgtSet = client.getValuesForSetWithPaging(tgtRdmId,
									ttName, sessionId, publisher, progressedValues, MAPPING_FINISHED_TOPIC);
							progressedValues = valuesTgtSet.size();
							
							// its the first mapping for this TT so we store
							// all targetValues to avoid
							// additional calls to RDM
							tgtHashMap = jsonToHashMap(CODE, valuesTgtSet);
						}
						
						// now write the new values in the preparedStatements batch
						writeInPreparedStatementBatch(rdmMapJSON, preparedStatement, findReferenceTableForTT(ttName), tgtHashMap, progressedValues);
						
						// the JSONrdmMap object has been updated
						numSrcValues = rdmMapJSON.getInt(Constants.NUMBER_OF_SRC_VALUES);
						progressedValues += numSrcValues;
						numMappedValues = rdmMapJSON.getInt(Constants.NUMBER_OF_MAPPED_VALUES);
						int numRelsSize = rdmMapJSON.getInt(RELATIONS_SIZE);
						
						// check if mapping has ambiguous relations
						if (numRelsSize > numMappedValues) {
							
							// there are ambiguous mappings
							mappingResultJson.put(Constants.STATUS, STATUS_FAILED);
							
							// since we are stopping here to process the
							// whole table lets set the reason for failing
							tableStatus = FAILED_DUPLICATE;
						} else {
							
							// everything is okay this statements can be executed later
							mapsToStore.put(entry.getKey(), rdmMapJSON);
							mappingResultJson.put(Constants.STATUS, STATUS_OK);
						}
					} else {
						
						// mapping is invalid or conflicts were found
						mappingResultJson.put(Constants.STATUS, STATUS_FAILED);
						
						// since we stopping here to process the whole table
						// lets set the reason for failing
						tableStatus = mappingStatus;
					}
				}
				
				mappingResultJson.put(Constants.NUMBER_OF_SRC_VALUES, numSrcValues);
				mappingResultJson.put(Constants.NUMBER_OF_MAPPED_VALUES, numMappedValues);
				
				// add the number of srcValues and the number of srcValues that are actually mapped
				// to the global number of all mappings that are related to this tt
				overallNumSrc += numSrcValues;
				overallNumMapped += numMappedValues;
				ttResultJson.put(mappingResultJson);
				
				// publish for each Mapping the progress when finished
				// lets check if the user canceled the progress
				if (this.cancelled) {
					// the progress has been canceled we set status to canceled
					// so the loop will break
					tableStatus = STATUS_CANCELLED;
				}
			}
			
			// did the loop break because of the size or status?
			// if it broke because of the status no statements should be executed
			if (tableStatus.equals(STATUS_OK)) {
				
				// delete the table content in the Database
				String deleteStmt = "DELETE FROM " + CwApp.getTranscodingTableSchema() + "." + ttName;
				
				deleteStatement = conn.prepareStatement(deleteStmt);
				deleteStatement.executeUpdate();
				
				// since we already executed the batch of SQL statements in the loop above
				// there can only be a small number of remaining statements in this last batch
				// but we execute it nevertheless
				if (preparedStatement != null) {
					
					// tableStatus could be good if there is just a mapping to remove...
					// the statement is null in this case
					preparedStatement.executeBatch();
				}
				
				// commit all database transactions
				conn.commit();
				
				// all mappings and values have been stored in the CW DB without errors
				// now the mapping entities should be updated or created
				saveMappingsInDB(mapsToStore);
				
				// delete all mappings that have been removed in RDM hub
				if (!mapsToRemove.isEmpty()) {
					deleteMappings(mapsToRemove);
				}
			} else {
				// the table status is not OK so we have to roll back
				conn.rollback();
			}
		} catch (BatchUpdateException e) {
			tableStatus = FAILED_SQL;
			Util.handleBatchException(conn, e);
			e.getNextException().printStackTrace();
		} catch (SQLException e) {
			tableStatus = FAILED_SQL;
			Util.handleBatchException(conn, e);
		} catch (NamingException e) {
			tableStatus = FAILED_INTERNAL;
			Util.handleBatchException(e);
		} catch (CwAppException e) {
			if (e.getResponse().getStatus() == HttpURLConnection.HTTP_UNAUTHORIZED) {
				tableStatus = FAILED_RDM_LOGIN;
			} else {
				tableStatus = FAILED_INTERNAL;
			}
			Util.handleBatchException(e);
		} catch (JSONException e) {
			tableStatus = FAILED_INTERNAL;
			Util.handleBatchException(e);
		} finally {
			// the response will be sent to the client so set the status
			response = buildTableResult(ttName, tableStatus, ttResultJson, overallNumSrc, overallNumMapped);
			Util.closeDBObjects(deleteStatement, preparedStatement, conn);
		}

		logger.exiting(CLASS_NAME, METHOD_NAME);
		return response;
	}

	private boolean checkForConflicts(String ttName, String mapKey,
			Map<String, JSONObject> rdmMappingsMap) throws JSONException {
		final String METHOD_NAME = "checkForConflicts()";
		logger.entering(CLASS_NAME, METHOD_NAME);

		// this method checks if two mappings are using the same pair of src and
		// tgt rdmsets ids
		JSONObject mapToCheck = rdmMappingsMap.get(mapKey);
		String srcSetID = mapToCheck.getString(Constants.RDM_MAPPING_SRC);
		List<RdmMapping> dbMappings = getAllMappingsForTT(ttName);
		
		// check for conflicts with Mappings that are already stored in the database
		for (RdmMapping dbMap : dbMappings) {
			if (dbMap.getSourceRdmId().equals(srcSetID)) {
				if (!dbMap.getRdmId().equals(mapKey)) {
					String tgtSet = mapToCheck.getString(Constants.RDM_MAPPING_TGT);
					
					if (dbMap.getTargetRdmId().equals(tgtSet)) {
						
						// conflict found
						return true;
					}
				}
			}
		}
		
		Iterator<Entry<String, JSONObject>> iter = rdmMappingsMap.entrySet().iterator();
		
		// iterate over all other mappings
		while (iter.hasNext()) {
			Entry<String, JSONObject> entry = iter.next();
			JSONObject otherMap = entry.getValue();
			String otherSrc = otherMap.getString(Constants.RDM_MAPPING_SRC);
			
			if (otherSrc.equals(srcSetID)) {
				if (!entry.getKey().equals(mapKey)) {
					String otherTgt = otherMap.getString(Constants.RDM_MAPPING_TGT);
					String tgtSet = mapToCheck.getString(Constants.RDM_MAPPING_TGT);
					
					if (otherTgt.equals(tgtSet)) {
						
						// there is a match return true to indicate that there is a conflict
						logger.exiting(CLASS_NAME, METHOD_NAME);
						return true;
					}
				}
			}
		}
		
		logger.exiting(CLASS_NAME, METHOD_NAME);
		
		// no conflict found return false
		return false;
	}
	
	private List<RdmMapping> getAllMappingsForTT(String ttName) {
		final String METHOD_NAME = "getAllMappinsForTT(String ttName)";
		logger.entering(CLASS_NAME, METHOD_NAME);
		
		// returns all Mappings that are in a relationship with the specified TranscodingTable
		List<RdmMapping> result = null;

		try {
			jpaTransaction.begin();
			manager.joinTransaction();
			result = DBOperations.getAllMappingsForTT(ttName, manager);
			jpaTransaction.commit();
		} catch (Exception e) {
			Util.handleBatchException(jpaTransaction, e);
		}

		logger.exiting(CLASS_NAME, METHOD_NAME);
		return result;
	}
	
	private void saveMappingsInDB(HashMap<String, JSONObject> rdmMappingsHashMap) {
		final String METHOD_NAME = "saveMappingsInDB()";
		logger.entering(CLASS_NAME, METHOD_NAME);
		
		Set<String> mappingKeys = rdmMappingsHashMap.keySet();
		
		try {
			jpaTransaction.begin();
			manager.joinTransaction();
			
			for (String mapKey : mappingKeys) {
				logger.fine("Map key: " + mapKey);
				JSONObject rdmMapJSON = rdmMappingsHashMap.get(mapKey);
				
				// is this mapping already stored?
				// if yes then just update it
				RdmMapping mapping = manager.find(RdmMapping.class, mapKey);
				
				if (mapping == null) {
					mapping = createMapping(rdmMapJSON);
				} else {
					mapping = updateMapping(mapping, rdmMapJSON);
				}
				
				if (mapping != null) {
					manager.persist(mapping);
				}
			}
			
			jpaTransaction.commit();
		} catch (Exception e) {
			Util.handleBatchException(jpaTransaction, e);
		}
		
		logger.exiting(CLASS_NAME, METHOD_NAME);
	}

	private void writeInPreparedStatementBatch(JSONObject rdmMapJSON, PreparedStatement prSt, ReferenceTable rTable,
			HashMap<String, JSONObject> tgtHashMap, int progressStartValue) throws JSONException, SQLException {
		final String METHOD_NAME = "writeInPreparedStatementBatch(JSONObject rdmMapJSON, PreparedStatement prSt, ReferenceTable rTable, HashMap<String, JSONObject> tgtHashMap, int progressStartValue)";
		logger.entering(CLASS_NAME, METHOD_NAME);
		
		// this method returns a prepared statement reference that can be executed via executeBatch();
		// all values will be written into the batch
		String srcRdmId = rdmMapJSON.getString(Constants.RDM_MAPPING_SRC_Version_ID);
		int srcSetSize = 0;
		int mappedSrcValues = 0;
		
		// get the Values for the source and the targetSet
		JSONArray valuesSrcSet = client.getValuesForSetWithPaging(srcRdmId,
				rdmMapJSON.getString(Constants.T_TABLE_NAME), sessionId, publisher, progressStartValue,
				MAPPING_FINISHED_TOPIC);
		
		// get all relations for this mapping
		JSONArray relations = client.getValuesRelsWithPaging(rdmMapJSON.getString(Constants.RDM_MAPPING_VERSION_ID),
				rdmMapJSON.getString(Constants.T_TABLE_NAME), sessionId, publisher, progressStartValue + valuesSrcSet.size(),
				MAPPING_FINISHED_TOPIC);
		
		// create a hashmap for all relations with sources values baseID as key
		HashMap<String, JSONObject> relationsHashMap = jsonToHashMap(SRCVALID, relations);
		String srcLegacyID = rdmMapJSON.getString(Constants.LEGACYID);
		String tgtLegacyID = rTable.getLegacyId();
		
		// set the values for src and tgt LegacyID
		prSt.setString(1, srcLegacyID);
		prSt.setString(2, tgtLegacyID);
		
		int srcCount = 0;
		srcSetSize = valuesSrcSet.size();
		
		// iterate over all srcValues and try to find a mapped tgtValue
		// if there is none, take the srcValue with a not-mapped-indicator
		while (srcCount < srcSetSize) {
			JSONObject srcValObj = valuesSrcSet.getJSONObject(srcCount);
			JSONObject tgtValObj = null;
			String srcID = srcValObj.getString(CODE);
			
			// check if this values are mapped
			JSONObject relation = relationsHashMap.get(srcID);
			if (relation != null) {
				// the values are mapped
				tgtValObj = tgtHashMap.get(relation.getString(TGTVALID));
				// increase the counter for mapped values
				mappedSrcValues++;
			} else {
				// skip mapping, if no target value was specified
				srcCount++;
				continue;
			}
			
			// the description of the values does not come from the RDM set's 'Descript'
			// field but from the 'Name' field instead
			String desc = (String) srcValObj.get(Constants.RDM_SET_VALUE_NAME);
			prSt.setString(3, desc);
			int i = 4;
			
			// iterate over all columns and take the value for this column
			// from the rdm response
			for (ReferenceTableColumn column : rTable.getNonMandtColumns()) {
				
				// Skip the VALPOS and DDTEXT columns for domain tables - they are not part of the transcoding process
				if (rTable.getTableType() == ReferenceTableType.DOMAIN_TABLE
						&& (column.getName().equals(Constants.DOMAIN_TABLE_COLUMNS[0])
								|| column.getName().equals(Constants.DOMAIN_TABLE_COLUMNS[2]))) {
					continue;
				}
			
				// get the values for the column name in target and srcValues
				String srcValue = srcValObj.getString(column.getName());
				String tgtValue = tgtValObj.getString(column.getName());
				
				// at this point we have to check whether the srcValue or tgtValue
				// parameters have a placeholder for empty values set
				if (srcValue.equalsIgnoreCase(Constants.RDM_EMPTY_VALUE_ID)) {
					srcValue = Constants.EMPTY_STRING;
				}
				
				if (tgtValue.equalsIgnoreCase(Constants.RDM_EMPTY_VALUE_ID)) {
					tgtValue = Constants.EMPTY_STRING;
				}
				
				// first put srcValue
				prSt.setString(i, srcValue);
				i++;
				
				// and then the tgtValue
				prSt.setString(i, tgtValue);
				i++;
			}
			
			prSt.addBatch();
			srcCount++;
		}
		
		rdmMapJSON.put(Constants.NUMBER_OF_SRC_VALUES, srcSetSize);
		rdmMapJSON.put(Constants.NUMBER_OF_MAPPED_VALUES, mappedSrcValues);
		rdmMapJSON.put(RELATIONS_SIZE, relations.size());
		
		logger.exiting(CLASS_NAME, METHOD_NAME);
	}

	private boolean checkTypes(String idSrc, String idTgt) {
		final String METHOD_NAME = "checkTypes()";
		logger.entering(CLASS_NAME, METHOD_NAME);
		
		// checks if two types for a RDM Mapping are equal
		// because RDM returns an array of JSONObjects we have to
		// search for the object containing the needed information.
		try {
			JSONArray srcSet = client.getRdmSet(idSrc);
			JSONArray tgtSet = client.getRdmSet(idTgt);
			int i = 0;
			
			while (i < srcSet.size()) {
				JSONObject srcType = srcSet.getJSONObject(i);
				
				if (srcType.containsKey(Constants.RDM_SET_TYPE_ID)) {
					JSONObject tgtType = tgtSet.getJSONObject(i);
					String srcTypeID = srcType.getString(Constants.RDM_SET_TYPE_ID);
					String tgtTypeID = tgtType.getString(Constants.RDM_SET_TYPE_ID);
					return srcTypeID.equals(tgtTypeID);
				}
				
				i++;
			}
		} catch (JSONException e) {
			Util.handleBatchException(e);
		}
		logger.exiting(CLASS_NAME, METHOD_NAME);
		return false;
	}

	private HashMap<String, JSONObject> jsonToHashMap(String key, JSONArray array) throws JSONException {
		final String METHOD_NAME = "jsonToHashMap(String key, JSONArray array)";
		logger.entering(CLASS_NAME, METHOD_NAME);
		
		// builds a hashmap for each object in the array with the arguments key
		int i = 0;
		HashMap<String, JSONObject> result = new HashMap<String, JSONObject>();
		
		while (i < array.size()) {
			JSONObject obj = array.getJSONObject(i);
			result.put(obj.getString(key), obj);
			i++;
		}
		
		logger.exiting(CLASS_NAME, METHOD_NAME);
		return result;
	}

	private RdmMapping createMapping(JSONObject object) throws JSONException {
		final String METHOD_NAME = "createMapping()";
		logger.entering(CLASS_NAME, METHOD_NAME);
		
		// creates a Java RdmMapping entity with the values from a JSONObject
		RdmMapping newMap = new RdmMapping();
		newMap.setRdmId(object.getString(Constants.RDM_MAPPING_BASE_ID));
		newMap.setRdmVersionId(object.getString(Constants.RDM_MAPPING_VERSION_ID));
		newMap.setName(object.getString(Constants.RDM_MAPPING_NAME));
		newMap.setVersion(object.getString(Constants.AVAILABLE_RDM_VERSION));
		newMap.setTargetRdmId(object.getString(Constants.RDM_MAPPING_TGT));
		newMap.setLastLoad(new Date());
		ReferenceTable table = DBOperations.findReferenceTableForRDMSet(object.getString(Constants.RDM_MAPPING_TGT), manager);
		
		if (table == null) {
			return null;
		}
		
		newMap.setTranscodingTable(table.getTranscodingTable());
		newMap.setSourceRdmId(object.getString(Constants.RDM_MAPPING_SRC));
		newMap.setValueMappings(object.getInt(Constants.NUMBER_OF_MAPPED_VALUES));
		newMap.setSourceKeys(object.getInt(Constants.NUMBER_OF_SRC_VALUES));
		
		logger.exiting(CLASS_NAME, METHOD_NAME);
		return newMap;
	}

	private RdmMapping updateMapping(RdmMapping map, JSONObject object) throws JSONException {
		final String METHOD_NAME = "updateMapping(RdmMapping map, JSONObject object)";
		logger.entering(CLASS_NAME, METHOD_NAME);
		
		// updates a RdmMapping object with the values from a JSONObject
		map.setRdmVersionId(object.getString(Constants.RDM_MAPPING_VERSION_ID));
		map.setVersion(object.getString(Constants.AVAILABLE_RDM_VERSION));
		map.setLastLoad(new Date());
		map.setName(object.getString(Constants.RDM_MAPPING_NAME));
		map.setValueMappings(object.getInt(Constants.NUMBER_OF_MAPPED_VALUES));
		map.setSourceKeys(object.getInt(Constants.NUMBER_OF_SRC_VALUES));
		
		logger.exiting(CLASS_NAME, METHOD_NAME);
		return map;
	}

	// finds the ReferenceTable linked to the given transcoding table
	// result is unique because of a one to one relationship between
	// ReferenceTable and TranscodingTable
	private ReferenceTable findReferenceTableForTT(String ttName) {
		ReferenceTable resultTable = null;
		try {
			jpaTransaction.begin();
			manager.joinTransaction();
			TypedQuery<ReferenceTable> query = manager.createNamedQuery("ReferenceTable.retrieveByTranscodingTableName",
					ReferenceTable.class);
			query.setParameter("ttname", ttName);
			jpaTransaction.commit();
			// Executing the query outside the transaction makes the table object stay "alive",
			// so that related objects (e.g. columns) can be retrieved later.
			// Changing this to execute inside the trnsaction leaves us with a "shell" object with some null properties.
			resultTable = query.getSingleResult();
		} catch (Exception e) {
			Util.handleBatchException(jpaTransaction, e);
		}
		return resultTable;
	}

	// the key specifies the baseID of the mapping that will be checked
	// this Method checks if there are conflicts between the mappings
	// checks if the types of src and tgt set are equal
	private String checkMappingConflictAndType(String ttName, String key,
			Map<String, JSONObject> rdmMappingsMap) throws JSONException {
		final String METHOD_NAME = "checkMappingConflictAndType(String key, HashMap<String, JSONObject> rdmMappingsMap)";
		logger.entering(CLASS_NAME, METHOD_NAME);

		String result = null;
		JSONObject rdmMapJSON = rdmMappingsMap.get(key);
		
		if (!checkForConflicts(ttName, key, rdmMappingsMap)) {
			String srcRdmId = rdmMapJSON.getString(Constants.RDM_MAPPING_SRC_Version_ID);
			String tgtRdmId = rdmMapJSON.getString(Constants.RDM_MAPPING_TGT_Version_ID);
			if (checkTypes(srcRdmId, tgtRdmId)) {
				result = STATUS_OK;
			} else {
				result = FAILED_INVALID;
			}
		} else {
			result = FAILED_CONFLICT;
		}
		
		logger.exiting(CLASS_NAME, METHOD_NAME);
		return result;
	}

	private void publishTableSize(String tableName, int values) throws JSONException {
		final String METHOD_NAME = "publishTableSize(String tableName, int mappings)";
		logger.entering(CLASS_NAME, METHOD_NAME);
		JSONObject tableStatus = new JSONObject();
		logger.fine("Publishing TableSize for Table " + tableName + " size is " + values);
		
		try {
			tableStatus.put(Constants.T_TABLE_NAME, tableName);
			tableStatus.put(Constants.RDM_MAPPING_NAME, "");
			tableStatus.put(COUNT_ATT, values);
			this.publisher.publish(new BayeuxJmsTextMsg(TABLE_STATUS_ROW_COUNT_TOPIC + this.sessionId, tableStatus.toString()));
		} catch (PublisherException e) {
			Util.handleBatchException(e);
		}
		
		logger.exiting(CLASS_NAME, METHOD_NAME);
	}
	
	private void publishTableSuccess(JSONObject tableResponse) {
		final String METHOD_NAME = "publishTableSucess()";
		logger.entering(CLASS_NAME, METHOD_NAME);
		
		try {
			logger.fine("TableSuccess is: " + tableResponse);
			this.publisher.publish(new BayeuxJmsTextMsg(TABLE_FINISHED_TOPIC + this.sessionId, tableResponse.toString()));
		} catch (PublisherException e) {
			Util.handleBatchException(e);
		}
		
		logger.exiting(CLASS_NAME, METHOD_NAME);
	}
	
	private Integer getAvailableSrcCountsForTT(Map<String, JSONObject> rdmMappings) throws JSONException {
		ArrayList<String> srcRdmSetIds = new ArrayList<String>();
		
		for (Entry<String, JSONObject> entry : rdmMappings.entrySet()) {
			JSONObject mapping = entry.getValue();

			// the status should not be "REMOVED", otherwise the call to rdm will fail
			if (mapping.containsKey(Constants.RDM_MAPPING_SRC_Version_ID)
					&& !mapping.getString(Constants.STATUS).equals(Constants.STATUS_REMOVED)) {
				srcRdmSetIds.add(mapping.getString(Constants.RDM_MAPPING_SRC_Version_ID));
			}
		}
		
		return client.getAvailableSrcCounts(srcRdmSetIds);
	}
	
	private String generateColumnStatement(ReferenceTable table) {
		final String METHOD_NAME = "generateColumnStatement(ReferenceTable table)";
		logger.entering(CLASS_NAME, METHOD_NAME);

		final char comma = ',';
		final char qMark = '?';
		final char closeP = ')';
		StringBuffer insertStatementBuffer = new StringBuffer();
		
		// a transcodingTable starts always with the source legacy id, target legacy id, and the description
		// this string is passed as argument to this function
		// the value-placeholders for this values are static
		StringBuffer valuesBuffer = new StringBuffer();
		valuesBuffer.append(PREPARED_STATEMENT_STATIC_VALUES);
		
		Collection<ReferenceTableColumn> columns = table.getNonMandtColumns();
		for (ReferenceTableColumn column : columns) {

			// Skip the VALPOS and DDTEXT columns for domain tables - they are not part of the transcoding process
			if (table.getTableType() == ReferenceTableType.DOMAIN_TABLE
					&& (column.getName().equals(Constants.DOMAIN_TABLE_COLUMNS[0])
							|| column.getName().equals(Constants.DOMAIN_TABLE_COLUMNS[2]))) {
				continue;
			}
			
			// Append a comma unless it's the first column
			if (insertStatementBuffer.length() != 0) {
				insertStatementBuffer.append(comma);
				valuesBuffer.append(comma);
			}

			insertStatementBuffer.append(column.getTranscodingTableSrcName());
			insertStatementBuffer.append(comma);
			insertStatementBuffer.append(column.getTranscodingTableTgtName());
			valuesBuffer.append(qMark);
			valuesBuffer.append(comma);
			valuesBuffer.append(qMark);
		}
		
		insertStatementBuffer.append(closeP);
		valuesBuffer.append(closeP);
		insertStatementBuffer.append(valuesBuffer.toString());
		
		logger.exiting(CLASS_NAME, METHOD_NAME);
		return insertStatementBuffer.toString();
	}

	private void deleteMappings(List<String> mapIds) {
		final String METHOD_NAME = "deleteMappings(List<String> mapIds)";
		logger.entering(CLASS_NAME, METHOD_NAME);
		
		try {
			jpaTransaction.begin();
			manager.joinTransaction();
			
			for (String mapId : mapIds) {
				logger.fine("Removing deleted mapping: " + mapId);
				RdmMapping mapping = manager.find(RdmMapping.class, mapId);
				RdmSet srcSet = manager.find(RdmSet.class, mapping.getSourceRdmId());
				
				if (srcSet != null) {
					srcSet.setInitialMappingName("");
					manager.merge(srcSet);
				}
				
				manager.remove(mapping);
			}

			jpaTransaction.commit();
		} catch (Exception e) {
			Util.handleBatchException(jpaTransaction, e);
		}
		logger.exiting(CLASS_NAME, METHOD_NAME);
	}
}
