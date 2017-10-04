package com.ibm.is.sappack.cw.app.services.rdm.jaxrs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;

import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;

import com.ibm.is.sappack.cw.app.data.JPAResourceFactory;
import com.ibm.is.sappack.cw.app.data.rdm.SourceDataId;
import com.ibm.is.sappack.cw.app.data.rdm.jpa.RdmMapping;
import com.ibm.is.sappack.cw.app.data.rdm.jpa.RdmSet;
import com.ibm.is.sappack.cw.app.data.rdm.jpa.ReferenceTable;
import com.ibm.is.sappack.cw.app.data.rdm.jpa.SourceDataCollectionRule;
import com.ibm.is.sappack.cw.app.services.AbstractThreadedService;
import com.ibm.is.sappack.cw.app.services.Constants;
import com.ibm.is.sappack.cw.app.services.Util;
import com.ibm.is.sappack.cw.app.services.rdm.DBOperations;
import com.ibm.is.sappack.cw.app.services.rdm.clients.RdmSetClient;
import com.ibm.is.sappack.cw.app.services.rdm.threads.SourceReferenceDataExporterThread;
import com.ibm.websphere.webmsg.publisher.Publisher;

@Path("/sourcedata")
public class SourceReferenceDataService extends AbstractThreadedService {
	
	private static final String SESSION_ATTRIBUTE_NAME = Constants.SESSION_ATTRIBUTE_NAME_RDM_SOURCE_EXPORT_THREAD;
	private final static String CLASS_NAME = SourceReferenceDataService.class.getName();
	
	@Override
	protected String getSessionAttributeName() {
		return SESSION_ATTRIBUTE_NAME;
	}
	
	private final static String JSON_UID = "uid";
	private final static String JSON_LEGACY_ID_NAME = "legacyIdName";
	private final static String JSON_LEGACY_ID_DESCRIPTION = "legacyIdDescription";
	private final static String JSON_REFERENCE_TABLE_ID = "referenceTableId";
	private final static String JSON_REFERENCE_TABLE = "refTable";
	private final static String JSON_TABLE_TYPE = "tableType";
	private final static String JSON_DESCRIPTION = "desc";
	private final static String JSON_SOURCE_SET = "sourceSet";
	private final static String JSON_SOURCE_SET_ID = "sourceSetId";
	private final static String JSON_TARGET_SET = "targetSet";
	private final static String JSON_TARGET_SET_ID = "targetSetId";
	private final static String JSON_MAPPING = "mapping";
	private final static String JSON_NO_RULES = "noRules";

	private final EntityManager manager;

	public SourceReferenceDataService() {
		super();
		final String METHOD_NAME = "SourceReferenceDataService()";
		logger.entering(CLASS_NAME, METHOD_NAME);
		this.manager = JPAResourceFactory.getEntityManager();
		logger.exiting(CLASS_NAME, METHOD_NAME);
	}
	
	// Returns a JSON array with aggregated metadata on source reference data for all legacy systems
	@Path("/retrieve")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public StreamingOutput getSourceReferenceData() {
		final String METHOD_NAME = "getSourceReferenceData()";
		logger.entering(CLASS_NAME, METHOD_NAME);
		
		// Get reference table list
		TypedQuery<ReferenceTable> query = manager.createNamedQuery("ReferenceTable.retrieveAll", ReferenceTable.class);
		
		// Get the legacy ID list
		Map<String, String> legacyIdMap = com.ibm.is.sappack.cw.app.services.config.DBOperations.getSourceLegacyIdsFromCWDB();
		
		JSONArray result = new JSONArray();
		
		try {
			int idCounter = 0; // Sequential ID for referencing the data item on the client 
			for (ReferenceTable referenceTable : query.getResultList()) {
				logger.finer("Reference Table: " + referenceTable.getName());
				boolean noRulesFound = false;
				List<SourceDataCollectionRule> ruleList = DBOperations.getRulesForReferenceTable(referenceTable.getReferenceTableId(), manager);
				if (ruleList == null || ruleList.isEmpty()) {
					logger.finer("No data collection rules found.");
					noRulesFound = true;
				}
				for (Entry<String, String> legacyIdEntry : legacyIdMap.entrySet()) {
					String legacyIdName = legacyIdEntry.getKey();
					String legacyIdDescription = legacyIdEntry.getValue();
					logger.finer("Legacy id: " + legacyIdDescription);
					JSONObject resultRow = new JSONObject();
					resultRow.put(JSON_UID, idCounter);
					resultRow.put(JSON_LEGACY_ID_NAME, legacyIdName);
					resultRow.put(JSON_LEGACY_ID_DESCRIPTION, legacyIdDescription);
					resultRow.put(JSON_REFERENCE_TABLE_ID, referenceTable.getReferenceTableId());
					resultRow.put(JSON_REFERENCE_TABLE, referenceTable.getName());
					resultRow.put(JSON_TABLE_TYPE, referenceTable.getTableType().toString());
					resultRow.put(JSON_DESCRIPTION, referenceTable.getDescription());
					if (noRulesFound) {
						resultRow.put(JSON_NO_RULES, "true");
					}
					
					// Get the source set for this pairing of legacy ID and reference table
					RdmSet sourceSet = referenceTable.getSourceRdmSetForLegacyId(legacyIdName);
					String sourceSetId = "";
					String sourceSetName = "";
					String mappingName = "";
					if (sourceSet != null) {
						sourceSetId = sourceSet.getRdmId();
						sourceSetName = sourceSet.getName();
						mappingName = sourceSet.getInitialMappingName(); // If an initial mapping was created, we only know the name
					}
					resultRow.put(JSON_SOURCE_SET_ID, sourceSetId);
					resultRow.put(JSON_SOURCE_SET, sourceSetName);
					
					// Get the target set
					RdmSet targetSet = referenceTable.getTargetRdmSet();
					String targetSetId = "";
					String targetSetName = "";
					if (targetSet != null) {
						targetSetId = targetSet.getRdmId();
						targetSetName = targetSet.getName();
					}
					resultRow.put(JSON_TARGET_SET_ID, targetSetId);
					resultRow.put(JSON_TARGET_SET, targetSetName);
					
					// Get the mapping, if one has been loaded already.
					// In this case, we override the "initial mapping name" we may have saved previously.
					if (sourceSet != null && targetSet != null) {
						TypedQuery<RdmMapping> mappingQuery = manager.createNamedQuery("RdmMapping.retrieveBySetIds", RdmMapping.class);
						mappingQuery.setParameter("source", sourceSet.getRdmId());
						mappingQuery.setParameter("target", targetSet.getRdmId());
						List<RdmMapping> rdmMappingQueryResult = mappingQuery.getResultList();
						if (!rdmMappingQueryResult.isEmpty()) {
							RdmMapping mapping = rdmMappingQueryResult.get(0);
							mappingName = mapping.getName();
						}
					}
					resultRow.put(JSON_MAPPING, mappingName);

					result.add(resultRow);
					idCounter++;
				}
			}
		} catch (JSONException e) {
			Util.throwInternalErrorToClient(e);
		}
		logger.exiting(METHOD_NAME, CLASS_NAME);
		return Util.output(result.toString());
	}
	
	// Export source reference data
	@POST
	@Path("/export")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public StreamingOutput exportSourceReferenceData(@Context HttpServletRequest servletRequest, @Context ServletContext servletContext, String input) {
		final String METHOD_NAME = "exportSourceReferenceData(HttpServletRequest servletRequest, ServletContext servletContext, String input)";
		logger.entering(CLASS_NAME, METHOD_NAME);
	
		JSONObject response = new JSONObject();
		try {
			// Get RDM password from the session
			String rdmPassword = (String) servletRequest.getSession().getAttribute(Constants.SESSION_ATTRIBUTE_NAME_RDMPASSWORD);
			
			// Initialize RDM connection
			RdmSetClient rdmSetClient = new RdmSetClient(rdmPassword);
			
			Publisher publisher = Util.getJmsPublisher(servletContext);
			
			JSONArray inputJsonArray = new JSONArray(input);
			List<SourceDataId> inputList = parseJsonInput(inputJsonArray);
			
			// Create execution thread
			SourceReferenceDataExporterThread importerThread = new SourceReferenceDataExporterThread(publisher, inputList, rdmSetClient, servletRequest.getSession());
			servletRequest.getSession().setAttribute(getSessionAttributeName(), importerThread);
	
			response.put("maximum", inputJsonArray.size());
		} catch (JSONException e){
			Util.throwInternalErrorToClient(e);
		}
		logger.exiting(CLASS_NAME, METHOD_NAME);
		return Util.output(response.toString());
	}
	
	// Converts the JSON request data to a list of SourceDataIds 
	private List<SourceDataId> parseJsonInput(JSONArray inputJsonArray) {
		final String METHOD_NAME = "parseJsonInput(JSONArray inputJsonArray)";
		logger.entering(CLASS_NAME, METHOD_NAME);
		
		List<SourceDataId> result = new ArrayList<SourceDataId>();
		try {
			int i = 0;
			while (i < inputJsonArray.size()) {
				JSONObject row = inputJsonArray.getJSONObject(i);
				SourceDataId id = new SourceDataId();
				id.setLegacyId(row.getString(Constants.JSON_LEGACY_ID));
				id.setLegacyIdDescription(row.getString(Constants.JSON_LEGACY_ID_DESCRIPTION));
				id.setReferenceTableId(row.getInt(Constants.JSON_REFERENCE_TABLE_ID));
				result.add(id);
				i++;
			}
		} catch (JSONException e){
			Util.throwInternalErrorToClient(e);
		}
		logger.exiting(CLASS_NAME, METHOD_NAME);
		return result;
	}
}
