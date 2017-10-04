package com.ibm.is.sappack.cw.app.services.rdm.clients;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;

import com.ibm.is.sappack.cw.app.data.rdm.IColumn;
import com.ibm.is.sappack.cw.app.data.rdm.RdmSetVersionInfo;
import com.ibm.is.sappack.cw.app.data.rdm.jpa.ReferenceTable;
import com.ibm.is.sappack.cw.app.services.Constants;
import com.ibm.is.sappack.cw.app.services.CwApp;
import com.ibm.is.sappack.cw.app.services.Util;
import com.ibm.websphere.webmsg.publisher.Publisher;

public class RdmSetClient extends AbstractRdmClient {

	public RdmSetClient(String password) {
		super(password);
	}

	static final String CLASS_NAME = RdmSetClient.class.getName();

	private static final int BATCH_SIZE = 10;

	/**
	 * Returns the ID and latest version of the RDM data set with the given name, null if it doesn't exist. Note: We use
	 * the internal rule that new versions have their number incremented, therefore the highest numbered version must be
	 * the latest one. RDM itself has no such restriction; the version numbers are arbitrary.
	 * 
	 * @param name
	 *            Set name
	 * @return A RdmSetVersionInfo object with the base ID of the data set and the latest version number. Null if none exists.
	 */
	public final RdmSetVersionInfo getSetByName(String name) {
		final String METHOD_NAME = "getSetByName(String name)"; 
		Logger logger = CwApp.getLogger();
		logger.entering(CLASS_NAME, METHOD_NAME);

		String requestUrl = urlPrefix + Constants.RDM_SET_URI + "?" + Constants.RDM_SET_NAME + "=" + name + Constants.RDM_SET_WILDCARD;
		String responseString = getRequest(requestUrl);

		RdmSetVersionInfo rdmSetVersionInfo = new RdmSetVersionInfo();
		String dataSetVersionId = null;
		String dataSetBaseId = null;
		int latestVersion = 0;
		try {
			JSONArray responseArray = new JSONArray(responseString);
			for (int i = 0; i < responseArray.size(); i++) {
				JSONObject jsonObject = (JSONObject) responseArray.get(i);
				if (name.equalsIgnoreCase((String) jsonObject.get(Constants.RDM_SET_NAME))) {
					int version = 0;
					try {
						version = Integer.parseInt((String) jsonObject.get(Constants.RDM_SET_VERSION));
					} catch (NumberFormatException e) {
						logger.fine("Got Rdm Set with unparsable version number: " + jsonObject);
					}
					if (version > latestVersion) {
						latestVersion = version;
						 // The Base ID values are all the same, we need to get the value at least once
						dataSetBaseId = (String) jsonObject.get(Constants.RDM_SET_BASE_ID);
						dataSetVersionId = jsonObject.getString(Constants.RDM_SET_VERSION_ID);
					}
				}
			}
		} catch (JSONException e) {
			Util.throwInternalErrorToClient(e);
		}
		if (dataSetBaseId == null) {
			logger.fine("Data set \"" + name + "\" doesn't exist");  
			logger.exiting(CLASS_NAME, METHOD_NAME);
			return null;
		}
		rdmSetVersionInfo.setBaseId(dataSetBaseId);
		rdmSetVersionInfo.setVersionId(dataSetVersionId);
		rdmSetVersionInfo.setVersionNumber(latestVersion + "");
		logger.fine("Data set \"" + name + "\" exists, id is:" + dataSetBaseId + ", latest version: " + latestVersion);   
		logger.exiting(CLASS_NAME, METHOD_NAME);
		return rdmSetVersionInfo;
	}

	/**
	 * Returns a list of the RDM data sets (set versions, to be exact) with the given name substring.
	 * 
	 * @param nameSubstring
	 *            Set name substring
	 * @return A List of string IDs
	 */
	public final List<String> getSets(String nameSubstring) {
		final String METHOD_NAME = "getSets(String nameSubstring)"; 
		Logger logger = CwApp.getLogger();
		logger.entering(CLASS_NAME, METHOD_NAME);

		String requestUrl = urlPrefix + Constants.RDM_SET_URI + "?" + Constants.RDM_SET_NAME + "=" + nameSubstring + Constants.RDM_SET_WILDCARD;
		String responseString = getRequest(requestUrl);
		
		ArrayList<String> setList = new ArrayList<String>();
		try {
			JSONArray responseArray = new JSONArray(responseString);
			for (int i = 0; i < responseArray.size(); i++) {
				JSONObject jsonObject = (JSONObject) responseArray.get(i);
				setList.add((String) jsonObject.get(Constants.RDM_SET_VERSION_ID));
			}
		} catch (JSONException e) {
			Util.throwInternalErrorToClient(e);
		}
		return setList;
	}

	/**
	 * Creates a new source ReferenceDataSet for the given table and legacy ID.
	 * Assumes no set with the given name exists.
	 */
	public final String createSourceSet(ReferenceTable referenceTable, String typeId, String legacyId) {
		return createSet(referenceTable, typeId, legacyId);
	}
	
	/**
	 * Creates a new target ReferenceDataSet. 
	 * Assumes no set with the given name exists.
	 */
	public final String createTargetSet(ReferenceTable referenceTable, String typeId) {
		return createSet(referenceTable, typeId, null);
	}
	
	private final String createSet(ReferenceTable referenceTable, String typeId, String legacyId) {
		final String METHOD_NAME = "createSet(ReferenceTable referenceTable, String typeId)"; 
		Logger logger = CwApp.getLogger();
		logger.entering(CLASS_NAME, METHOD_NAME);

		String setName = null;
		if (legacyId == null) {
			setName = Util.generateTargetRdmSetName(referenceTable);
		} else {
			setName = Util.generateSourceRdmSetName(referenceTable, legacyId);
		}
		String requestUrl = urlPrefix + Constants.RDM_SET_URI;
		String newSetId = null;

		// Create the type
		JSONObject dataSetObject = new JSONObject();
		try {
			dataSetObject.put(Constants.RDM_SET_NAME, setName);
			dataSetObject.put(Constants.RDM_SET_DESCRIPTION, referenceTable.getDescription());
			dataSetObject.put(Constants.RDM_SET_VERSION, "1");
			dataSetObject.put(Constants.RDM_SET_TYPE_ID, typeId);
			// We declare this to be the state machine for all RDM sets we create
			dataSetObject.put(Constants.RDM_SET_STATE_MACHINE, Constants.RDM_SET_STATE_MACHINE_VALUE);
			 // TODO use the login user (which doesn't work so far in our dev environment)
			dataSetObject.put(Constants.RDM_SET_OWNER, Constants.RDM_SET_OWNER_VALUE);
			addRdmObjectDates(dataSetObject);

			logger.fine(dataSetObject.toString());

			String responseString = postRequest(requestUrl, dataSetObject.toString());
			
			newSetId = responseString.substring(responseString.indexOf('\'') + 2, responseString.lastIndexOf('\''));
			logger.fine("RDM Data set \"" + setName + "\" was created, id is:" + newSetId);  

		} catch (JSONException e) {
			Util.throwInternalErrorToClient(e);
		}
		logger.exiting(CLASS_NAME, METHOD_NAME);
		return newSetId;
	}

	/**
	 * Creates a new version of the given ReferenceDataSet
	 *
	 * @param dataSetIdAndVersion An array with 2 strings: the ID of the data set and the latest version.
	 * @param setName
	 * @param typeId
	 * @return
	 */
	public final String createSetVersion(RdmSetVersionInfo rdmSetVersionInfo, String setName, String typeId) {
		final String METHOD_NAME = "createSetVersion(String[] dataSetIdAndVersion, String setName, String typeId)"; 
		Logger logger = CwApp.getLogger();
		logger.entering(CLASS_NAME, METHOD_NAME);

		String requestString = 
				  Constants.RDM_SET_VERSION_ID + "=" + rdmSetVersionInfo.getBaseId() + "&"
				+ Constants.RDM_SET_NAME + "=" + setName + "&"
				+ Constants.RDM_SET_VERSION + "=" + rdmSetVersionInfo.getVersionNumber() + "&"
				+ Constants.RDM_SET_NEW_VERSION + "=" + (Integer.parseInt(rdmSetVersionInfo.getVersionNumber()) + 1) + "&"
				+ Constants.RDM_SET_COPY_OPTION;
		logger.fine(requestString);

		String requestUrl = urlPrefix + Constants.RDM_SET_URI + Constants.RDM_NEW_VERSION + "?" + requestString;
		String versionId = getRequest(requestUrl);

		logger.fine("New version (" + (Integer.parseInt(rdmSetVersionInfo.getVersionNumber()) + 1) + ") of data set \"" + setName  
				+ "\" was created, id is:" + versionId); 

		logger.exiting(CLASS_NAME, METHOD_NAME);
		return versionId;
	}

	/**
	 * Deletes the given ReferenceDataSet
	 */
	public final void deleteSet(String dataSetId) {
		final String METHOD_NAME = "deleteSet(String dataSetId)"; 
		Logger logger = CwApp.getLogger();
		logger.entering(CLASS_NAME, METHOD_NAME);

		logger.fine("Deleting data set \"" + dataSetId); 

		String requestUrl = urlPrefix + Constants.RDM_SET_URI + dataSetId;
		deleteRequest(requestUrl);

		logger.exiting(CLASS_NAME, METHOD_NAME);
	}	
	/**
	 * Checks if the codes(a code is a compound key) stored in the CW DB are equal with the codes that 
	 * are on RDM
	 */
	public boolean isSetUpToDate(String setVersionId, Set<Map<String, String>> cwDbValuesComplete, Publisher publisher,
			String sessionId) {
		final String METHOD_NAME = "isSetUpToDate(String setVersionId, Set<Map<String, String>> cwDbValuesComplete";
		Logger logger = CwApp.getLogger();
		logger.entering(CLASS_NAME, METHOD_NAME);
		boolean result = false;
		try {
			JSONArray completeRDMValues = getValuesForSetWithPaging(setVersionId, "", sessionId, publisher, 0, Constants.COMETD_TOPIC_EXPORT_ROW_PROGRESS);
			HashSet<String> rdmCodeValues = new HashSet<String>();
			HashSet<String> cwCodeValues = new HashSet<String>();
			for (Map<String, String> cwValues : cwDbValuesComplete) {
				Set<Entry<String, String>> entries = cwValues.entrySet();
				Iterator<Entry<String, String>> iter = entries.iterator();
				while (iter.hasNext()) {
					Entry<String, String> completeValues = iter.next();
					if (completeValues.getValue() != null && !(completeValues.getValue().isEmpty())
							&& completeValues.getKey().equals(Constants.TABLE_DATA_UNIQUE_ID_TAG)) {
						// its a code value... add it to the set
						cwCodeValues.add(completeValues.getValue());
					}
				}
			}
			if (cwCodeValues.size() != completeRDMValues.size()) {
				// the number of codes here and on rdm are not equal
				// so there are changes and we can create a new version of the set
				logger.exiting(CLASS_NAME, METHOD_NAME);
				return false;
			}
			int i = 0;
			while (i < completeRDMValues.size()) {
				JSONObject valueRow = completeRDMValues.getJSONObject(i);
				// add the "code" value to the rdmCodeValues set
				rdmCodeValues.add(valueRow.getString(Constants.RDM_SET_VALUE_CODE));
				i++;
			}
			result = cwCodeValues.equals(rdmCodeValues);
		} catch (JSONException e) {
			Util.throwInternalErrorToClient(e);
		}
		return result;
	}

	/**
	 * Inserts values to a RD set
	 */
	public final void insertIntoSet(String setId, Set<Map<String, String>> tableDataMapSet, Publisher publisher, String sessionId, int progressStartValue) {
		final String METHOD_NAME = "insertIntoSet(String setId, List<Map<String, String>> tableData)"; 
		Logger logger = CwApp.getLogger();
		logger.entering(CLASS_NAME, METHOD_NAME);

		String requestUrl = urlPrefix + Constants.RDM_SET_URI + setId + "/" + Constants.RDM_VALUES;
		JSONArray requestJsonArray = new JSONArray();
		int batchRowCount = 0;
		int totalRowCount = 0;

		try {
			for (Map<String, String> row : tableDataMapSet) {
				batchRowCount++;
				totalRowCount++;
				JSONObject rowObject = new JSONObject();
				String code = "";

				rowObject.put(Constants.RDM_SET_VALUE_DESCRIPTION, "");
				rowObject.put(Constants.RDM_SET_VALUE_ID, Constants.RDM_SET_VALUE_ID_VALUE); // Must be unique?
				rowObject.put(Constants.RDM_SET_VALUE_STATE_VALUE, Constants.RDM_SET_VALUE_STATE_VALUE_VALUE);
				rowObject.put(Constants.RDM_SET_VALUE_STATE_CODE, "1");
				rowObject.put(Constants.RDM_SET_VALUE_SEQNUM, "1");
				rowObject.put(Constants.RDM_SET_VALUE_NEW, true);
				addRdmObjectDates(rowObject);
				// Reverse engineered IDs, seem to be OK
				rowObject.put(Constants.RDM_INTERNAL_CLIENT_ID, Constants.RDM_SET_URI + setId + "/" + Constants.RDM_VALUES + "/" + Constants.RDM_SET_VALUE_ID_VALUE);
				rowObject.put(Constants.RDM_INTERNAL_ID, Constants.RDM_SET_URI + setId + "/" + Constants.RDM_VALUES + "/" + Constants.RDM_SET_VALUE_ID_VALUE);
				rowObject.put(Constants.RDM_IS_DIRTY, true);
				rowObject.put(Constants.RDM_REST_OPERATION, Constants.RDM_REST_OPERATION_ADD);
				boolean hasDescription = false;
				for (Map.Entry<String, String> entry : row.entrySet()) {
					// logger.finest("Entry key: " + entry.getKey());
					// logger.finest("Entry value: " + entry.getValue());
					if (entry.getKey().equals(Constants.TABLE_DATA_DESCRIPTION_TAG)) {
						// The description
						if (!(entry.getValue() == null) && !entry.getValue().isEmpty()) {
							logger.finer("Description: " + entry.getValue()); 
							rowObject.put(Constants.RDM_SET_VALUE_NAME, entry.getValue());
							hasDescription = true;
						}
					} else if (entry.getKey().equals(Constants.TABLE_DATA_UNIQUE_ID_TAG)) {
						// The (constructed) unique id / key (The RDM UI calls this "Code")
						code = entry.getValue();
						logger.finer("Unique ID / code: " + code);
						rowObject.put(Constants.RDM_SET_VALUE_STANDARD_ID, code);
					} else {
						rowObject.put(entry.getKey(), entry.getValue());
					}
				}
				
				if (!hasDescription) {
					rowObject.put(Constants.RDM_SET_VALUE_NAME, code); // No description available, use the code
				}
				
				// Skip rows with empty keys (rows where only the "client" column (MANDT) has a value in SAP)
				if (code.length() != 0) {
					requestJsonArray.put(rowObject);
				}
				
				logger.finest(rowObject.toString());
				
				// Send the data if the batch is full
				if (batchRowCount % BATCH_SIZE == 0) {
					postRequest(requestUrl, requestJsonArray.toString());
					batchRowCount = 0;
					requestJsonArray.clear();
					publishTableProgress(Constants.COMETD_TOPIC_EXPORT_ROW_PROGRESS,"", publisher, sessionId, totalRowCount+progressStartValue);
				}
			}
			// Send the final batch
			postRequest(requestUrl, requestJsonArray.toString());
			logger.fine("Data set \"" + setId + "\" has been filled with values.");  
		} catch (JSONException e) {
			Util.throwInternalErrorToClient(e);
		}

		logger.exiting(CLASS_NAME, METHOD_NAME);
	}

	// Publishes the current table row progress to the client using CometD
	
	/**
	 * Returns the ID of the RDM data type with the given name, null if it doesn't exist.
	 * 
	 * @param name
	 *            Type name
	 * @return ID of the data type; null if none exists.
	 */
	private final String getTypeByName(String name) {
		final String METHOD_NAME = "getTypeByName(String name)"; 
		Logger logger = CwApp.getLogger();
		logger.entering(CLASS_NAME, METHOD_NAME);

		String requestUrl = urlPrefix + Constants.RDM_TYPE_URI + "?" + Constants.RDM_TYPE_NAME + "=" + name + Constants.RDM_TYPE_WILDCARD;
		String responseString = getRequest(requestUrl);

		String dataTypeId = null;
		try {
			JSONArray responseArray = new JSONArray(responseString);
			logger.fine(responseArray.size() + " objects retrieved"); 
			for (int i = 0; i < responseArray.size(); i++) {
				JSONObject jsonObject = (JSONObject) responseArray.get(i);
				logger.fine(jsonObject.get(Constants.RDM_TYPE_NAME) + ", Id: " + jsonObject.get(Constants.RDM_TYPE_ID)); 
				if (name.equals(jsonObject.get(Constants.RDM_TYPE_NAME))) {
					dataTypeId = (String) jsonObject.get(Constants.RDM_TYPE_ID);
				}
			}
		} catch (JSONException e) {
			Util.throwInternalErrorToClient(e);
		}
		if (dataTypeId != null) {
			logger.fine("Data type \"" + name + "\" exists, id is:" + dataTypeId);  
		} else {
			logger.fine("Data type \"" + name + "\" doesn't exist.");  
		}
		logger.exiting(CLASS_NAME, METHOD_NAME);
		return dataTypeId;
	}

	/**
	 * Returns a list of the RDM data types with the given name substring.
	 * 
	 * @param nameSubstring
	 *            Type name substring
	 * @return A List of string IDs
	 */
	public final List<String> getTypes(String nameSubstring) {
		final String METHOD_NAME = "getTypes(String nameSubstring)"; 
		Logger logger = CwApp.getLogger();
		logger.entering(CLASS_NAME, METHOD_NAME);

		String requestUrl = urlPrefix + Constants.RDM_TYPE_URI + "?" + Constants.RDM_TYPE_NAME + "=" + nameSubstring + Constants.RDM_TYPE_WILDCARD;
		String responseString = getRequest(requestUrl);

		ArrayList<String> typeList = new ArrayList<String>();
		try {
			JSONArray responseArray = new JSONArray(responseString);
			for (int i = 0; i < responseArray.size(); i++) {
				JSONObject jsonObject = (JSONObject) responseArray.get(i);
				typeList.add((String) jsonObject.get(Constants.RDM_TYPE_ID));
			}
		} catch (JSONException e) {
			Util.throwInternalErrorToClient(e);
		}
		return typeList;
	}

	/**
	 * Creates a new ReferenceDataType and its attributes
	 */
	public final String createType(ReferenceTable referenceTable) {
		final String METHOD_NAME = "createType(ReferenceTable referenceTable)"; 
		Logger logger = CwApp.getLogger();
		logger.entering(CLASS_NAME, METHOD_NAME);

		String typeName = Util.generateRdmTypeName(referenceTable);
		
		// Check if the type exists
		String dataTypeId = getTypeByName(typeName);
		if (dataTypeId != null) {
			return dataTypeId; // The type already exists
		}

		String requestUrl = urlPrefix + Constants.RDM_TYPE_URI;

		JSONArray jsonArray = new JSONArray();

		// Create the type
		JSONObject dataTypeObject = new JSONObject();
		try {
			jsonArray.put(dataTypeObject);
			dataTypeObject.put(Constants.RDM_TYPE_DESCRIPTION, referenceTable.getDescription());
			dataTypeObject.put(Constants.RDM_TYPE_NAME, typeName);
			dataTypeObject.put(Constants.RDM_TYPE_ID, "0");
			dataTypeObject.put(Constants.RDM_INTERNAL_CLIENT_ID, Constants.RDM_TYPE_URI + "0");
			dataTypeObject.put(Constants.RDM_INTERNAL_ID, Constants.RDM_TYPE_URI + "0");
			dataTypeObject.put(Constants.RDM_IS_DIRTY, true);
			dataTypeObject.put(Constants.RDM_REGEX, "");
			dataTypeObject.put(Constants.RDM_SUPPORTS_COMPOUND_KEY, true);
			dataTypeObject.put(Constants.RDM_TYPE_OF_TYPE, "1");
			dataTypeObject.put(Constants.RDM_REST_OPERATION, Constants.RDM_REST_OPERATION_ADD);

			// Create the type's attributes (from the reference table's columns)
			switch(referenceTable.getTableType()) {
			case CHECK_TABLE:
				for (IColumn column : referenceTable.getColumns()) {
					addColumnToJsonArray(jsonArray, column);
				}
				logger.fine(jsonArray.toString());
				break;
			case DOMAIN_TABLE:
				for (IColumn column : referenceTable.getColumns()) {
					if (column.getName().equals(Constants.DOMAIN_TABLE_COLUMNS[1])) {
						addColumnToJsonArray(jsonArray, column);
					}
				}
				break;
			}

			dataTypeId = postRequest(requestUrl, jsonArray.toString());

			logger.fine("Data type \"" + referenceTable.getName() + "\" was created, id is: " + dataTypeId);  

		} catch (JSONException e) {
			Util.throwInternalErrorToClient(e);
		}

		logger.exiting(CLASS_NAME, METHOD_NAME);
		return dataTypeId;
	}

	/**
	 * Deletes the given Data Type
	 */
	public final void deleteType(String dataTypeId) {
		final String METHOD_NAME = "deleteType(String dataTypeId)"; 
		Logger logger = CwApp.getLogger();
		logger.entering(CLASS_NAME, METHOD_NAME);

		logger.fine("Deleting data type " + dataTypeId); 
		String requestUrl = urlPrefix + Constants.RDM_TYPE_URI + dataTypeId;
		deleteRequest(requestUrl);

		logger.exiting(CLASS_NAME, METHOD_NAME);
	}

	private static void addColumnToJsonArray(JSONArray jsonArray, IColumn column) throws JSONException {
		JSONObject attributeObject = new JSONObject();
		jsonArray.put(attributeObject);
		attributeObject.put(Constants.RDM_TYPE_COLUMN_NAME, column.getName());
		attributeObject.put(Constants.RDM_TYPE_COLUMN_DESCRIPTION, column.getSapName());
		attributeObject.put(Constants.RDM_TYPE_COLUMN_CODE, Constants.RDM_TYPE_COLUMN_CODE_VALUE);
		attributeObject.put(Constants.RDM_TYPE_COLUMN_UNIQUE, false);
		attributeObject.put(Constants.RDM_TYPE_COLUMN_IS_REQUIRED, false);
		attributeObject.put(Constants.RDM_TYPE_COLUMN_IS_KEY, false);
		attributeObject.put(Constants.RDM_TYPE_COLUMN_VALUE_SET_ID, "");
		attributeObject.put(Constants.RDM_TYPE_COLUMN_VALIDATION_RULE, "");
		attributeObject.put(Constants.RDM_TYPE_COLUMN_DEFAULT_VALUE, "");
		attributeObject.put(Constants.RDM_TYPE_COLUMN_LENGTH, "");
		attributeObject.put(Constants.RDM_TYPE_COLUMN_DATA_TYPE, Constants.RDM_TYPE_COLUMN_DATA_TYPE_STRING);
		// Reverse engineered IDs, seem to be OK
		attributeObject.put(Constants.RDM_INTERNAL_CLIENT_ID, Constants.RDM_TYPE_URI + Constants.RDM_TYPE_COLUMN_INTERNAL_ID_STRING);
		attributeObject.put(Constants.RDM_INTERNAL_ID, Constants.RDM_TYPE_URI + Constants.RDM_TYPE_COLUMN_INTERNAL_ID_STRING);
		attributeObject.put(Constants.RDM_REST_OPERATION, Constants.RDM_REST_OPERATION_ADD);
	}
}
