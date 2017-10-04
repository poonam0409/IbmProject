package com.ibm.is.sappack.cw.app.services.rdm.debug;

import java.net.HttpURLConnection;

import javax.ws.rs.core.MediaType;

import org.apache.wink.client.ClientAuthenticationException;
import org.apache.wink.client.Resource;
import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;

import com.ibm.is.sappack.cw.app.services.Constants;
import com.ibm.is.sappack.cw.app.services.CwAppException;
import com.ibm.is.sappack.cw.app.services.Util;
import com.ibm.is.sappack.cw.app.services.rdm.clients.AbstractRdmClient;

public class RdmMappingDebugClient extends AbstractRdmClient {
	public static final String RDM_SERVICE_URI = "/RestAPI/jaxrs/relationships/";

	private static final String RDMDATAERROR = "RdmDataErrorMsg";
	private static final String RDMERROR = "RdmErrorMsg";
	private static final String RDMLOGINERROR = "RdmLoginErrorMsg";

	public RdmMappingDebugClient(String pwd) {
		super(pwd);
	}

	public JSONArray getMappings() {
		String callUrl = urlPrefix + RDM_SERVICE_URI;
		return getArray(callUrl);
	}

	public JSONArray getValueRels(String id) {
		String callUrl = urlPrefix + RDM_SERVICE_URI + id + "/valueRels/";
		return getArray(callUrl);
	}

	public JSONArray getValuesForSet(String id) throws JSONException {
		final int BATCH_SIZE = 25;
		JSONArray allValues = new JSONArray();
		Long availableValues = Long.valueOf(BATCH_SIZE+1);
		Long startIndex = Long.valueOf(0);
		Long endIndex = Long.valueOf(startIndex + BATCH_SIZE);
		Boolean firstCall = Boolean.valueOf(true);
		while(endIndex<availableValues+BATCH_SIZE){
			String callUrl = "https://cobain:9444/RestAPI/jaxrs/sets/?ID=" + id;
			JSONArray rdmResponse = getArray(callUrl);
			if(firstCall){
				JSONObject obj = rdmResponse.getJSONObject(0);
				availableValues = obj.getLong("availableValues");
				rdmResponse.remove(0);
			}
			allValues.addAll(rdmResponse);
			startIndex = endIndex+1;
			endIndex = startIndex + BATCH_SIZE;
			firstCall = false;
		}
		return allValues;
	}

	public JSONArray getTypeOfSet(String id) {
		String callUrl = urlPrefix + Constants.RDM_SET_URI + id;
		return getArray(callUrl);
	}

	private JSONArray getArray(String callUrl) {
		JSONArray response = null;
		try {
			response = new JSONArray(getRequest(callUrl));
		} catch (JSONException e) {
			Util.throwInternalErrorToClient(e);
		} 
		return response;
	}

	public void deleteMapping(String id) {
		Resource resource = getClient(user, password).resource(urlPrefix + RDM_SERVICE_URI + id);
		resource.contentType(MediaType.APPLICATION_JSON_TYPE).delete();
	}

	// debug functions
	public void setRdmMapping(JSONObject object) {
		postToRDM(urlPrefix + RDM_SERVICE_URI, object);
	}

	private void postToRDM(String arg, JSONObject object) {
		Resource resource = getClient(user, password).resource(arg);
		resource.contentType(MediaType.APPLICATION_JSON_TYPE).post(object.toString());
	}

	private void putToRDM(String arg, JSONObject object) {
		Resource resource = getClient(user, password).resource(arg);
		resource.contentType(MediaType.APPLICATION_JSON_TYPE).put(object.toString());
	}

	public JSONObject getCompKey(String idMap, String idVal) {
		return getObject(urlPrefix + Constants.RDM_SET_URI + idMap + "/values/" + idVal);
	}

	public JSONArray getAllSets() {
		return getArray(urlPrefix + Constants.RDM_SET_URI);
	}

	public void setRdmRel(String id, JSONObject object) {
		postToRDM(urlPrefix + RDM_SERVICE_URI + id + "/valueRels/", object);
	}

	public void putRdmSet(String id, JSONObject object) {
		String callURL = urlPrefix + RDM_SERVICE_URI + id;
		putToRDM(callURL, object);
	}

	private JSONObject getObject(String callUrl) {
		try {
			return new JSONObject(getRequest(callUrl));
		} catch (JSONException e) {
			throw new CwAppException(RDMDATAERROR, HttpURLConnection.HTTP_INTERNAL_ERROR);
		} catch (ClientAuthenticationException e) {
			throw new CwAppException(RDMLOGINERROR, HttpURLConnection.HTTP_UNAUTHORIZED);
		} catch (Exception e) {
			throw new CwAppException(RDMERROR, HttpURLConnection.HTTP_INTERNAL_ERROR);
		}
	}
}
