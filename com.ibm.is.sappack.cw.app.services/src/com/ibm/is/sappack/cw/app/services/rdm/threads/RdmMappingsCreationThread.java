package com.ibm.is.sappack.cw.app.services.rdm.threads;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;

import com.ibm.is.sappack.cw.app.data.rdm.jpa.RdmSet;
import com.ibm.is.sappack.cw.app.services.AbstractCancelableThread;
import com.ibm.is.sappack.cw.app.services.Constants;
import com.ibm.is.sappack.cw.app.services.CwAppException;
import com.ibm.is.sappack.cw.app.services.Util;
import com.ibm.is.sappack.cw.app.services.rdm.clients.RdmMappingClient;
import com.ibm.is.sappack.cw.app.services.rdm.clients.RdmSetClient;
import com.ibm.websphere.webmsg.publisher.Publisher;
import com.ibm.websphere.webmsg.publisher.PublisherException;
import com.ibm.websphere.webmsg.publisher.jndijms.BayeuxJmsTextMsg;

public class RdmMappingsCreationThread extends AbstractCancelableThread {

	private static final String MAPPING_FINISHED_TOPIC = "/mapping/success";
	private static final String FAILED_INTERNAL = "InternalErrorMsg";
	private static final String FAILED_CONFLICT = "conflictMsg";
	private static final String SET_MISSING_CWAPP = "setMissingCWMsg";
	private static final String SET_MISSING_RDM = "setMissingRDMMsg";
	private static final String FAILED_RDM_LOGIN = "UnAuthorizedMsg";
	private static final String MAPPING_EXISTS = "mappingExists";
	private static final String CREATING = "creatingMsg";
	private final static String JSON_SOURCE_SET_ID = "sourceSetId";
	private final static String JSON_TARGET_SET_ID = "targetSetId";
	private final static String STATUS_OK = "OK";
	private static final String CLASS_NAME = RdmMappingsCreationThread.class.getName();
	private final RdmMappingClient mappingClient;
	private final RdmSetClient setClient;
	private final JSONArray userSelection;
	private int overAllProgress = 0;

	public RdmMappingsCreationThread(Publisher publisher, JSONArray userSelection, HttpSession session) {
		super(session, publisher);
		this.mappingClient = new RdmMappingClient(
				(String) session.getAttribute(Constants.SESSION_ATTRIBUTE_NAME_RDMPASSWORD));
		this.setClient = new RdmSetClient((String) session.getAttribute(Constants.SESSION_ATTRIBUTE_NAME_RDMPASSWORD));
		this.userSelection = userSelection;
	}

	@Override
	public void run() {
		final String METHOD_NAME = "run()";
		logger.entering(CLASS_NAME, METHOD_NAME);
		JSONArray allMappings = mappingClient.getMappings();
		logger.finest("Got these mappings from RDM Hub: " + allMappings);
		String mappingName = "";
		String status = "";
		String srcName = "";
		String tgtName = "";
		String description = "";
		
		try {
			HashMap<String, JSONObject> mappingsMap = jsonToNameMap(allMappings);
			while (!cancelled && overAllProgress < userSelection.size()) {
				// each mapping needs a status to display the creation success
				JSONObject selectedRow;
				selectedRow = userSelection.getJSONObject(overAllProgress);
				// we will set the mappingName to the refTable name, just in case we
				// don´t find the sets, in this case we at least publish a name for this mapping
				mappingName = selectedRow.getString("refTable");
				description = selectedRow.getString("desc");
				String srcSetId = selectedRow.getString(JSON_SOURCE_SET_ID);
				String tgtSetId = selectedRow.getString(JSON_TARGET_SET_ID);
				jpaTransaction.begin();
				manager.joinTransaction();
				RdmSet srcSet = manager.find(RdmSet.class, srcSetId);
				RdmSet tgtSet = manager.find(RdmSet.class, tgtSetId);
				if (srcSet != null && tgtSet != null) {
					if (rdmSetExists(srcSet) && rdmSetExists(tgtSet)) {
						srcName = srcSet.getName();
						tgtName = tgtSet.getName();
						// set the mappingName to the name of the src set
						mappingName = srcSet.getName();
						if (selectedRow.containsKey(Constants.RDM_MAPPING_BASE_ID) || mappingsMap.containsKey(mappingName)) {
							// the mapping exists already, we do not need to create a new one
							srcSet.setInitialMappingName(mappingName);
							manager.merge(srcSet);
							status = MAPPING_EXISTS;
						} else {
							// publish that the creation started
							publishOverallProgress(mappingName, srcName, tgtName, CREATING, MAPPING_FINISHED_TOPIC);
							mappingClient.createMapping(srcSet, tgtSet, description);
							srcSet.setInitialMappingName(mappingName);
							status = STATUS_OK;
						}
					} else {
						// source or target set is missing on rdm
						logger.finer("A set for " + mappingName + " is missing in RDM Hub");
						status = SET_MISSING_RDM;
					}
				} else {
					logger.finer("A set for " + mappingName + " is missing in CW App DB");
					// target or sourceSet is not stored in the cwAppDB
					if (srcSet != null) {
						srcName = srcSet.getName();
					} else {
						// srcSet is null, check if tgtSet is null too
						if (tgtSet != null) {
							tgtName = tgtSet.getName();
						}
					}
					status = SET_MISSING_CWAPP;
				}
				jpaTransaction.commit();
				overAllProgress++;
				publishOverallProgress(mappingName, srcName, tgtName, status, MAPPING_FINISHED_TOPIC);
			}
		} catch (CwAppException e) {
			Util.handleBatchException(jpaTransaction, e);
			if (e.getResponse().getStatus() == HttpURLConnection.HTTP_UNAUTHORIZED) {
				status = FAILED_RDM_LOGIN;
			} else if (e.getResponse().getStatus() == HttpURLConnection.HTTP_CONFLICT) {
				status = FAILED_CONFLICT;
			} else {
				status = FAILED_INTERNAL;
			}
		} catch (Exception e) {
			Util.handleBatchException(jpaTransaction, e);
			status = FAILED_INTERNAL;
		} finally {
			if (status != STATUS_OK && status != SET_MISSING_RDM && status != SET_MISSING_CWAPP && status != MAPPING_EXISTS) {
				overAllProgress++;
				publishOverallProgress(mappingName, srcName, tgtName, status, MAPPING_FINISHED_TOPIC);
			}
			// remove thread from the session
			session.removeAttribute(Constants.SESSION_ATTRIBUTE_NAME_RDM_CREATION_THREAD);
			logger.exiting(CLASS_NAME, METHOD_NAME);
		}
	}

	private void publishOverallProgress(String mappingName, String srcName, String tgtName, String status, String topic) {
		final String METHOD_NAME = "publishOverallProgress(String mappingName, String srcName, String tgtName, String status, String topic)";
		logger.entering(CLASS_NAME, METHOD_NAME);
		logger.finest(mappingName + ": " + status);
		try {
			JSONObject publishMessage = new JSONObject();
			publishMessage.put(Constants.STATUS, status);
			publishMessage.put(Constants.RDM_MAPPING_NAME, mappingName);
			publishMessage.put("COUNT", overAllProgress);
			publishMessage.put("srcSetName", srcName);
			publishMessage.put("tgtSetName", tgtName);
			publisher.publish(new BayeuxJmsTextMsg(topic + session.getId(), publishMessage.toString()));
		} catch (PublisherException e) {
			Util.handleBatchException(e);
		} catch (JSONException e) {
			Util.handleBatchException(e);
		}
		logger.exiting(CLASS_NAME, METHOD_NAME);
	}

	private HashMap<String, JSONObject> jsonToNameMap(JSONArray mappings) throws JSONException {
		final String METHOD_NAME = "jsonToNameMap(JSONArray mappings)";
		logger.entering(CLASS_NAME, METHOD_NAME);
		// mapping names are unique in RDM so we can build a map with the name as key
		int i = 0;
		HashMap<String, JSONObject> result = new HashMap<String, JSONObject>();
		while (i < mappings.size()) {
			JSONObject mapping = mappings.getJSONObject(i);
			String name = mapping.getString(Constants.RDM_MAPPING_NAME);
			result.put(name, mapping);
			i++;
		}
		logger.exiting(CLASS_NAME, METHOD_NAME);
		return result;
	}
	private boolean rdmSetExists(RdmSet set) {
		final String METHOD_NAME = "existsSetInRdm(RdmSet set)";
		logger.entering(CLASS_NAME, METHOD_NAME);
		List<String> ids = setClient.getSets(set.getName());
		logger.exiting(CLASS_NAME, METHOD_NAME);
		return ids.contains(set.getVersionId());
	}
}
