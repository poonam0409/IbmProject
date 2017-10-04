package com.ibm.is.sappack.cw.app.services.rdm.clients;

import java.net.HttpURLConnection;
import java.util.List;
import java.util.logging.Logger;

import org.apache.wink.client.ClientResponse;
import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;

import com.ibm.is.sappack.cw.app.data.rdm.jpa.RdmSet;
import com.ibm.is.sappack.cw.app.services.Constants;
import com.ibm.is.sappack.cw.app.services.CwApp;
import com.ibm.is.sappack.cw.app.services.CwAppException;
import com.ibm.websphere.webmsg.publisher.Publisher;

public class RdmMappingClient extends AbstractRdmClient {
	private static final String RDMDATAERROR = "RdmDataErrorMsg";

	private static final String CLASS_NAME = RdmMappingClient.class.getName();
	public RdmMappingClient(String password) {
		super(password);
	}

	public JSONArray getMappings() {
		final String METHOD_NAME = "getMappings()";
		Logger logger = CwApp.getLogger();
		logger.entering(CLASS_NAME, METHOD_NAME);
		String callUrl = urlPrefix + Constants.RDM_MAPPINGS_URI;
		logger.exiting(CLASS_NAME, METHOD_NAME);
		return getArray(callUrl);
	}

	public JSONArray getValuesRelsWithPaging(String id, String tableName, String sessiondId, Publisher pub,
			int progressStartValue, String pubTopic) throws JSONException {
		final String METHOD_NAME = "getValuesRelsWithPaging(String id, String tableName, String sessiondId, Publisher pub, int progressStartValue, String pubTopic)";
		Logger logger = CwApp.getLogger();
		logger.entering(CLASS_NAME, METHOD_NAME);
		JSONArray allRelations = new JSONArray();
		Integer availableRelations = Integer.valueOf(BATCH_SIZE + 1);
		Integer startIndex = Integer.valueOf(0);
		Integer endIndex = Integer.valueOf(startIndex + BATCH_SIZE);
		String callUrl = urlPrefix + Constants.RDM_MAPPINGS_URI + id + "/valueRels/";
		ClientResponse rdmResponse;
		while (endIndex < availableRelations + BATCH_SIZE) {
			rdmResponse = getRequestWithRange(callUrl, startIndex.toString() + "-" + endIndex.toString());
			if (rdmResponse.getHeaders().get("Content-range") != null) {
				// NOTE: range will be null if there are no values for this set in RDM
				// we need to parse a String which looks like:
				// "[items startIndex-endIndex/availableRelations]"
				String availableValueString = rdmResponse.getHeaders().get("Content-range").get(0);
				availableRelations = Integer
						.valueOf(availableValueString.substring(availableValueString.indexOf("/") + 1));
			} else {
				//TODO rdm does not set the content range because of an exception
				//there will be a fix in the next version so we can be sure here that a real error occurred
				availableRelations = 0;
			}
			allRelations.addAll(new JSONArray(rdmResponse.getEntity(String.class)));
			// change the start and endIndex for the next call to retrieve other
			// values or to break the loop
			startIndex = endIndex + 1;
			endIndex = startIndex + BATCH_SIZE;
			publishTableProgress(pubTopic, tableName, pub, sessiondId, startIndex + progressStartValue);
		}
		logger.fine("Got " + allRelations.size() + "value relations for RDM mapping " + id);
		logger.exiting(CLASS_NAME, METHOD_NAME);
		return allRelations;
	}

	// Returns the sum of the sizes of the given RDM Hub sets (referenced by their IDs)
	public int getAvailableSrcCounts(List<String> setIds) {
		int result = 0;
		for (String id : setIds) {
			String callUrl = urlPrefix + Constants.RDM_SET_URI + "list-values/" + id;
			ClientResponse rdmResponse = getRequestWithRange(callUrl, "0-0");
			int availableValues = 0;
			if (rdmResponse.getHeaders().get("Content-range") != null) {
				// TODO null check can be removed after next rdm release
				// NOTE: range will be null if there are no values for this set in RDM
				// we need to parse a String which looks like:
				// "[items x-y/availableValues]"
				String availableValueString = rdmResponse.getHeaders().get("Content-range").get(0);
				availableValues = Integer
						.valueOf(availableValueString.substring(availableValueString.indexOf("/") + 1));
			} else {
				availableValues = 0;
			}
			result += availableValues;
		}
		return result;
	}
	public JSONArray getRdmSet(String id) {
		final String METHOD_NAME = "getRdmSet(String id)";
		Logger logger = CwApp.getLogger();
		logger.entering(CLASS_NAME, METHOD_NAME);
		String callUrl = urlPrefix + Constants.RDM_SET_URI + id;
		logger.exiting(CLASS_NAME, METHOD_NAME);
		return getArray(callUrl);
	}

	private JSONArray getArray(String callUrl) {
		final String METHOD_NAME = "getArray(String callUrl)";
		Logger logger = CwApp.getLogger();
		logger.entering(CLASS_NAME, METHOD_NAME);
		try {
			return new JSONArray(getRequest(callUrl));
		} catch (JSONException e) {
			throw new CwAppException(RDMDATAERROR, HttpURLConnection.HTTP_INTERNAL_ERROR);
		}
	}

	public void deleteMapping(String id) {
		final String METHOD_NAME = "deleteMapping(String id)";
		Logger logger = CwApp.getLogger();
		logger.entering(CLASS_NAME, METHOD_NAME);
		deleteRequest(urlPrefix + Constants.RDM_MAPPINGS_URI + id);
		logger.exiting(CLASS_NAME, METHOD_NAME);
	}

	public void createMapping(RdmSet srcSet, RdmSet tgtSet, String description) {
		final String METHOD_NAME = "createMapping(RdmSet srcSet, RdmSet tgtSet)";
		Logger logger = CwApp.getLogger();
		logger.entering(CLASS_NAME, METHOD_NAME);
		JSONObject mapping = new JSONObject();
		try {
			mapping.put("Comment", "");
			mapping.put("Desc", description);
			mapping.put("StateMachine", "1");
			mapping.put("Type", "Default Mapping Type");
			mapping.put("TypeCd", "2");
			mapping.put(Constants.RDM_MAPPING_VERSION, "1");
			mapping.put(Constants.RDM_MAPPING_SRC, srcSet.getRdmId());
			mapping.put(Constants.RDM_MAPPING_SRC_Version_ID, srcSet.getVersionId());
			mapping.put(Constants.RDM_MAPPING_TGT, tgtSet.getRdmId());
			mapping.put(Constants.RDM_MAPPING_TGT_Version_ID, tgtSet.getVersionId());
			mapping.put(Constants.RDM_MAPPING_NAME, srcSet.getName());
			// TODO use the login user (which doesn't work so far in our dev environment)
			mapping.put(Constants.RDM_SET_OWNER, Constants.RDM_SET_OWNER_VALUE);
			addRdmObjectDates(mapping);
			
			// New in RDM Hub 10.1
			mapping.put("TgtSetName", "");
			mapping.put("SrcSetName", "");
			
			logger.exiting(CLASS_NAME, METHOD_NAME);
			postRequest(urlPrefix + Constants.RDM_MAPPINGS_URI, mapping.toString());
		} catch (JSONException e) {
			throw new CwAppException(RDMDATAERROR, HttpURLConnection.HTTP_INTERNAL_ERROR);
		}
	}
}
