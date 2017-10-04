package com.ibm.is.sappack.cw.app.services.rdm.debug;

import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;

import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;

import com.ibm.is.sappack.cw.app.services.Constants;
import com.ibm.is.sappack.cw.app.services.CwApp;
import com.ibm.is.sappack.cw.app.services.Util;
import com.ibm.is.sappack.cw.app.services.rdm.clients.RdmMappingClient;
import com.ibm.is.sappack.cw.app.services.rdm.clients.RdmSetClient;

/**
 * Deletes RDM sets or types by name substring and all mappings or one mapping by ID
 * 
 * For internal use only.
 * This service should be called manually by constructing the URL of a GET request, e.g.
 * http://<host>:9080/com.ibm.is.sappack.cw.app.services/jaxrs/rdmclean/sets?name=CT
 * http://<host>:9080/com.ibm.is.sappack.cw.app.services/jaxrs/rdmclean/types?name=CT
 * http://<host>:9080/com.ibm.is.sappack.cw.app.services/jaxrs/rdmclean/mappings
 * http://<host>:9080/com.ibm.is.sappack.cw.app.services/jaxrs/rdmclean/mapping?id=999999999999999
 * 
 * The RDM password has to be present in the session.
 */
@Path("/rdmclean")
public class RdmCleanupService {
	private static final String CLASS_NAME = RdmCleanupService.class.getName();
	
	@GET
	@Path("/sets")
	@Produces(MediaType.APPLICATION_JSON)
	public StreamingOutput deleteDataSets(@Context HttpServletRequest servletRequest, @QueryParam("name") String nameSubstring) {
		final String METHOD_NAME = "deleteDataSets(@Context HttpServletRequest servletRequest, @Context ServletContext servletContext, @QueryParam(\"name\") String nameSubstring)";
		Logger logger = CwApp.getLogger();
		logger.entering(CLASS_NAME, METHOD_NAME);
	
		JSONObject response = new JSONObject();
		try {
			// Get RDM password from the session
			String rdmPassword = (String) servletRequest.getSession().getAttribute(Constants.SESSION_ATTRIBUTE_NAME_RDMPASSWORD);
			
			// Initialize RDM connection
			RdmSetClient rdmSetClient = new RdmSetClient(rdmPassword);
			
			List<String> ids = rdmSetClient.getSets(nameSubstring);
			for (String id : ids) {
				rdmSetClient.deleteSet(id);
			}

			response.put("number", ids.size());
		}
		catch (JSONException e){
			Util.throwInternalErrorToClient(e);
		}
		logger.exiting(CLASS_NAME, METHOD_NAME);
		return Util.output(response.toString());
	}
	
	@GET
	@Path("/types")
	@Produces(MediaType.APPLICATION_JSON)
	public StreamingOutput deleteDataTypes(@Context HttpServletRequest servletRequest, @QueryParam("name") String nameSubstring) {
		final String METHOD_NAME = "deleteDataTypes(@Context HttpServletRequest servletRequest, @Context ServletContext servletContext, @QueryParam(\"name\") String nameSubstring)";
		Logger logger = CwApp.getLogger();
		logger.entering(CLASS_NAME, METHOD_NAME);
	
		JSONObject response = new JSONObject();
		try {
			// Get RDM password from the session
			String rdmPassword = (String) servletRequest.getSession().getAttribute(Constants.SESSION_ATTRIBUTE_NAME_RDMPASSWORD);
			
			// Initialize RDM connection
			RdmSetClient rdmSetClient = new RdmSetClient(rdmPassword);
			
			List<String> ids = rdmSetClient.getTypes(nameSubstring);
			for (String id : ids) {
				rdmSetClient.deleteType(id);
			}

			response.put("number", ids.size());
		}
		catch (JSONException e){
			Util.throwInternalErrorToClient(e);
		}
		logger.exiting(CLASS_NAME, METHOD_NAME);
		return Util.output(response.toString());
	}

	@GET
	@Path("/mappings")
	@Produces(MediaType.APPLICATION_JSON)
	public boolean deleteMappingsInRDM(@Context HttpServletRequest servletRequest){
		final String METHOD_NAME = "deleteMappingsInRDM(@Context HttpServletRequest servletRequest)";
		Logger logger = CwApp.getLogger();
		logger.entering(CLASS_NAME, METHOD_NAME);
		boolean result = false;
		try{
			String rdmPassword = (String) servletRequest.getSession().getAttribute(Constants.SESSION_ATTRIBUTE_NAME_RDMPASSWORD);
			RdmMappingClient client = new RdmMappingClient(rdmPassword);
			JSONArray mappings = client.getMappings();
			int i=0;
			while(i<mappings.size()){
				JSONObject mapping = mappings.getJSONObject(i);
				String id = mapping.getString(Constants.RDM_MAPPING_VERSION_ID);
				client.deleteMapping(id);
				i++;
			}
			logger.exiting(CLASS_NAME, METHOD_NAME);
			result = (i == mappings.size());
		}
		catch(JSONException e){
			Util.throwInternalErrorToClient(e);
		}
		return result;
	}

	@GET
	@Path("/mapping")
	public void deleteMapping(@Context HttpServletRequest servletRequest,@QueryParam("id") String id){
		final String METHOD_NAME = "deleteMapping(@Context HttpServletRequest servletRequest, String id)";
		Logger logger = CwApp.getLogger();
		logger.entering(CLASS_NAME, METHOD_NAME);
		String rdmPassword = (String) servletRequest.getSession().getAttribute(Constants.SESSION_ATTRIBUTE_NAME_RDMPASSWORD);
		RdmMappingClient client = new RdmMappingClient(rdmPassword);
		client.deleteMapping(id);
		logger.exiting(CLASS_NAME, METHOD_NAME);
	}

}
