package com.ibm.is.sappack.cw.app.services.config.jaxrs;

import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;

import com.ibm.is.sappack.cw.app.data.config.LegacySystem;
import com.ibm.is.sappack.cw.app.services.Constants;
import com.ibm.is.sappack.cw.app.services.Security;
import com.ibm.is.sappack.cw.app.services.Util;

@Path("/systems")
public class LegacySystemService {
	
	// This method is called by the Dojo JsonStore, so we need to return single systems as a list as well
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<LegacySystem> getAllLegacySystems(@QueryParam("legacyId") String id, @Context HttpServletRequest request) {
		
		// handle the standard query parameter "legacyId"
		if (id != null && !id.isEmpty()) {
			return com.ibm.is.sappack.cw.app.services.config.DBOperations.getLegacySystem(id, request.getSession());
		}

		return com.ibm.is.sappack.cw.app.services.config.DBOperations.getLegacySystems(com.ibm.is.sappack.cw.app.services.config.DBOperations.DatabaseSortOptions.SORT_NONE, request.getSession());
	}
	
	@GET
	@Path("/filtering")
	@Produces(MediaType.APPLICATION_JSON)
	public String getAllLegacySystemsForFilteringSelect(@Context HttpServletRequest request) {
		List<LegacySystem> allSystems = com.ibm.is.sappack.cw.app.services.config.DBOperations.getLegacySystems(com.ibm.is.sappack.cw.app.services.config.DBOperations.DatabaseSortOptions.SORT_NONE, request.getSession());
		JSONObject filteringSelectInput = new JSONObject();
		
		try {
			filteringSelectInput.put("identifier", "legacyId");
			filteringSelectInput.put("label", "legacyIdName");
			
			JSONArray systemList = new JSONArray();
			
			for (LegacySystem system : allSystems) {
				JSONObject jsonSystem = new JSONObject();
				jsonSystem.put("legacyId", system.getLegacyId());
				jsonSystem.put("legacyIdName", system.getLegacyId());
				
				systemList.add(jsonSystem);
			}
			
			filteringSelectInput.put("items", systemList);
		}
		catch (JSONException e) {
			Util.throwInternalErrorToClient(e);
		}
		
		return filteringSelectInput.toString();
	}
	
	@GET
	@Path("/src")
	@Produces(MediaType.APPLICATION_JSON)
	public List<LegacySystem> getSourceLegacySystems(@QueryParam("legacyId") String id, @Context UriInfo ui, @Context HttpServletRequest request) {

		// first we'll handle the standard query parameter "legacyId"
		if (id != null && !id.isEmpty()) {
			return com.ibm.is.sappack.cw.app.services.config.DBOperations.getSourceLegacySystem(id, request.getSession());
		}

		// now we'll handle the non-standard query parameters, namely "sort"
		MultivaluedMap<String, String> queryParams = ui.getQueryParameters();
		for (Entry<String, List<String>> entry : queryParams.entrySet()) {
			if (Pattern.matches("sort.*", entry.getKey())) {
				if (entry.getKey().contains(Constants.SORT_BY_DESCRIPTION_ASC)) {
					return com.ibm.is.sappack.cw.app.services.config.DBOperations.getSourceLegacySystems(com.ibm.is.sappack.cw.app.services.config.DBOperations.DatabaseSortOptions.SORT_BY_DESCRIPTION_ASC, request.getSession());
				}
				if (entry.getKey().contains(Constants.SORT_BY_DESCRIPTION_DESC)) {
					return com.ibm.is.sappack.cw.app.services.config.DBOperations.getSourceLegacySystems(com.ibm.is.sappack.cw.app.services.config.DBOperations.DatabaseSortOptions.SORT_BY_DESCRIPTION_DESC, request.getSession());
				}
				if (entry.getKey().contains(Constants.SORT_BY_LEGACYID_ASC)) {
					return com.ibm.is.sappack.cw.app.services.config.DBOperations.getSourceLegacySystems(com.ibm.is.sappack.cw.app.services.config.DBOperations.DatabaseSortOptions.SORT_BY_LEGACYID_ASC, request.getSession());
				}
				if (entry.getKey().contains(Constants.SORT_BY_LEGACYID_DESC)) {
					return com.ibm.is.sappack.cw.app.services.config.DBOperations.getSourceLegacySystems(com.ibm.is.sappack.cw.app.services.config.DBOperations.DatabaseSortOptions.SORT_BY_LEGACYID_DESC, request.getSession());
				}
			}
		}
		
		return com.ibm.is.sappack.cw.app.services.config.DBOperations.getSourceLegacySystems(com.ibm.is.sappack.cw.app.services.config.DBOperations.DatabaseSortOptions.SORT_NONE, request.getSession());
	}
	
	@POST
	@Path("/src")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public LegacySystem addSourceLegacySystem(LegacySystem system) {
		Security.replaceHarmfulChars(system);
		return com.ibm.is.sappack.cw.app.services.config.DBOperations.addLegacySystem(system);
	}

	@PUT
	@Path("/src/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public LegacySystem updateSourceLegacySystem(@PathParam("id") String legacyId, LegacySystem system) {
		Security.replaceHarmfulChars(system);
		return com.ibm.is.sappack.cw.app.services.config.DBOperations.updateLegacySystem(legacyId, system);
	}
	
	@DELETE
	@Path("/src/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void removeSourceLegacySystem(@PathParam("id") String legacyId) {
		com.ibm.is.sappack.cw.app.services.config.DBOperations.deleteLegacySystem(legacyId);
	}
}
