package com.ibm.is.sappack.cw.app.services.rdm.clients;

import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

import javax.ws.rs.core.MediaType;

import org.apache.wink.client.ClientAuthenticationException;
import org.apache.wink.client.ClientConfig;
import org.apache.wink.client.ClientResponse;
import org.apache.wink.client.ClientRuntimeException;
import org.apache.wink.client.Resource;
import org.apache.wink.client.RestClient;
import org.apache.wink.client.handlers.BasicAuthSecurityHandler;
import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;

import com.ibm.is.sappack.cw.app.services.Constants;
import com.ibm.is.sappack.cw.app.services.CwApp;
import com.ibm.is.sappack.cw.app.services.CwAppException;
import com.ibm.is.sappack.cw.app.services.Util;
import com.ibm.is.sappack.cw.app.services.config.jaxrs.SettingService;
import com.ibm.websphere.webmsg.publisher.Publisher;
import com.ibm.websphere.webmsg.publisher.PublisherException;
import com.ibm.websphere.webmsg.publisher.jndijms.BayeuxJmsTextMsg;

public abstract class AbstractRdmClient {

	private static final String CLASS_NAME = AbstractRdmClient.class.getName();

	private static Logger logger;
	
	protected static String user;
	protected static String password;
	protected static String urlPrefix;
	protected static final String RDM_DATE_FORMAT = "yyyy-MM-dd'T'hh:mm:ss'Z'";
	protected static final int BATCH_SIZE = 25;

	public AbstractRdmClient(String password) {
		
		// Get all RDM connection parameters
		SettingService settingService = new SettingService();
		String rdmHost = settingService.getSetting(Constants.SETTING_RDM_HOST);
		String rdmPort = settingService.getSetting(Constants.SETTING_RDM_PORT);
		urlPrefix = Util.generateRdmUrl(rdmHost, rdmPort);
		user = settingService.getSetting(Constants.SETTING_RDM_USER);
		AbstractRdmClient.password = password;
		logger = CwApp.getLogger();
	}

	protected static void validateResponse(ClientResponse response, String serviceUrl) {
		String errorMessage = null;
		
		if (response != null && response.getStatusCode() != HttpURLConnection.HTTP_OK) {
			errorMessage = "Error while calling the service at " + serviceUrl + ", message: \"" + response.getMessage()
					+ ", details: " + response.getEntity(String.class) + "\".";
			throw new CwAppException(errorMessage, response.getStatusCode());
		} else if (response == null) {
			errorMessage = "Error while calling the service at " + serviceUrl + ", the service response was null.";
			throw new CwAppException(errorMessage);
		}
	}

	public static final RestClient getClient(String user, String password) {
		ClientConfig clientConfig = new ClientConfig();
		BasicAuthSecurityHandler basicAuthHandler = new BasicAuthSecurityHandler();
		basicAuthHandler.setUserName(user);
		basicAuthHandler.setPassword(password);
		basicAuthHandler.setSSLRequired(false);
		clientConfig.readTimeout(180000);
		clientConfig.handlers(basicAuthHandler);
		
		return new RestClient(clientConfig);
	}

	protected static Resource getResource(String user, String password, String url) {
		Resource resource = getClient(user, password).resource(url);
		// Required for RDM Hub's "security"
		resource.header("X-Requested-With", "XMLHttpRequest");
		resource.header("referer", urlPrefix);
		return resource;
	}

	public JSONArray getValuesForSetWithPaging(String id, String tableName, String sessiondId, Publisher pub,
			int progressStartValue, String pubTopic) throws JSONException {
		final String METHOD_NAME = "getValuesForSetWithPaging(String id, String tableName, String sessiondId, Publisher pub, int progressStartValue)";
		logger.entering(CLASS_NAME, METHOD_NAME);
		
		JSONArray allValues = new JSONArray();
		Integer availableValues = Integer.valueOf(BATCH_SIZE + 1);
		Integer startIndex = Integer.valueOf(0);
		Integer endIndex = Integer.valueOf(startIndex + BATCH_SIZE);
		String callUrl = urlPrefix + Constants.RDM_SET_URI + "list-values/" + id;
		ClientResponse rdmResponse;
		
		while (endIndex < availableValues + BATCH_SIZE) {
			rdmResponse = getRequestWithRange(callUrl, startIndex.toString() + "-" + endIndex.toString());
			
			if (rdmResponse.getHeaders().get("Content-range") != null) {
				
				// NOTE: range will be null if there are no values for this set in RDM
				// we need to parse a String which looks like:
				// "[items startIndex-endIndex/availableValues]"
				String availableValueString = rdmResponse.getHeaders().get("Content-range").get(0);
				availableValues = Integer
						.valueOf(availableValueString.substring(availableValueString.indexOf("/") + 1));
			} else {
				
				// TODO in the next release of rdm we can be sure that an error
				// occurred if the availableValues are not set
				// if there are just no values the range will be set anyway
				// throw new CwAppException(HttpURLConnection.HTTP_INTERNAL_ERROR);
				availableValues = 0;
			}
			
			allValues.addAll(new JSONArray(rdmResponse.getEntity(String.class)));
			
			// change the start and endIndex for the next call to retrieve other
			// values or to break the loop
			startIndex = endIndex + 1;
			endIndex = startIndex + BATCH_SIZE;
			publishTableProgress(pubTopic, tableName, pub, sessiondId, startIndex + progressStartValue);
		}
		
		logger.fine("Got " + allValues.size() + " values for RDM Set " + id);
		logger.exiting(CLASS_NAME, METHOD_NAME);
		return allValues;
	}

	protected void publishTableProgress(String topic, String tableName, Publisher publisher, String sessionId,
			int rowCount) {
		logger.fine("Publishing row progress: " + rowCount);

		JSONObject tableProgress = new JSONObject();

		try {
			tableProgress.put("rowcount", rowCount);
			tableProgress.put(Constants.T_TABLE_NAME, tableName);
			publisher.publish(new BayeuxJmsTextMsg(topic + sessionId, tableProgress.toString()));
		} catch (JSONException e) {
			Util.throwInternalErrorToClient(e);
		} catch (PublisherException e) {
			Util.throwInternalErrorToClient(e);
		}
	}

	// Adds dummy date attributes to an RDM JSon object. We always use these settings.
	protected static void addRdmObjectDates(JSONObject dataSetObject) throws JSONException {
		dataSetObject.put(Constants.RDM_EFF_DATE, new SimpleDateFormat(RDM_DATE_FORMAT).format(new Date()));
		dataSetObject.put(Constants.RDM_EXP_DATE, "");
		dataSetObject.put(Constants.RDM_REV_DATE, "");
	}

	protected String getRequest(String requestUrl) {
		final String METHOD_NAME = "getRequest(String requestUrl)";
		logger.entering(CLASS_NAME, METHOD_NAME);
		
		Resource resource = getResource(user, password, requestUrl);
		String result = null;
		
		try {
			ClientResponse response = resource.contentType(MediaType.APPLICATION_JSON_TYPE).get();
			logger.exiting(CLASS_NAME, METHOD_NAME);
			validateResponse(response, requestUrl);
			result = response.getEntity(String.class);
		} catch (ClientRuntimeException e) {
			handleClientRuntimeException(e);
		}
		
		return result;
	}

	protected ClientResponse getRequestWithRange(String requestUrl, String range) {
		final String METHOD_NAME = "getRequestResponse(String requestUrl, String range)";
		logger.entering(CLASS_NAME, METHOD_NAME);
		
		Resource resource = getResource(user, password, requestUrl);
		ClientResponse result = null;
		
		try {
			resource = resource.header("Range", "items=" + range);
			ClientResponse response = resource.contentType(MediaType.APPLICATION_JSON_TYPE).get();
			logger.exiting(CLASS_NAME, METHOD_NAME);
			validateResponse(response, requestUrl);
			result = response;
		} catch (ClientRuntimeException e) {
			handleClientRuntimeException(e);
		}
		
		return result;
	}

	protected String postRequest(String requestUrl, String request) {
		final String METHOD_NAME = "postRequest(String requestUrl, String request)";
		logger.entering(CLASS_NAME, METHOD_NAME);
		
		Resource resource = getResource(user, password, requestUrl);
		String result = null;
		
		try {
			logger.finest("Request URL: " + requestUrl);
			logger.finest("Request payload: " + request);
			ClientResponse response = resource.contentType(MediaType.APPLICATION_JSON_TYPE).post(request);
			validateResponse(response, requestUrl);
			logger.exiting(CLASS_NAME, METHOD_NAME);
			result = response.getEntity(String.class);
		} catch (ClientRuntimeException e) {
			handleClientRuntimeException(e);
		}
		
		return result;
	}

	protected String deleteRequest(String requestUrl) {
		final String METHOD_NAME = "deleteRequest(String requestUrl)";
		logger.entering(CLASS_NAME, METHOD_NAME);
		
		Resource resource = getResource(user, password, requestUrl);
		String result = null;
		
		try {
			ClientResponse response = resource.contentType(MediaType.APPLICATION_JSON_TYPE).delete();
			validateResponse(response, requestUrl);
			logger.exiting(CLASS_NAME, METHOD_NAME);
			result = response.getEntity(String.class);
		} catch (ClientRuntimeException e) {
			handleClientRuntimeException(e);
		}
		
		return result;
	}

	private void handleClientRuntimeException(ClientRuntimeException e) {
		Util.handleBatchException(e);
		if (e instanceof ClientAuthenticationException) {
			throw new CwAppException(Constants.RDMLOGINERROR, HttpURLConnection.HTTP_UNAUTHORIZED);
		}
		throw new CwAppException(HttpURLConnection.HTTP_NOT_FOUND);
	}
}
