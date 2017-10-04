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

import com.ibm.is.sappack.cw.app.data.Resources;
import com.ibm.is.sappack.cw.app.data.config.LegacySystem;
import com.ibm.is.sappack.cw.app.services.Constants;
import com.ibm.is.sappack.cw.app.services.Security;

@Path("/systems/tgt")
public class TargetSapSystemService {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<LegacySystem> getTargetSapSystems(@QueryParam("legacyId") String id, @Context UriInfo ui, @Context HttpServletRequest request) {

		// first we'll handle the standard query parameter "legacyId"
		if (id != null && !id.equals(Resources.EMPTY_STRING)) {
			return com.ibm.is.sappack.cw.app.services.config.DBOperations.getTargetLegacySystem(id, request.getSession());
		}

		// now we'll handle the non-standard query parameters, namely "sort"
		MultivaluedMap<String, String> queryParams = ui.getQueryParameters();
		for (Entry<String, List<String>> entry : queryParams.entrySet()) {
			if (Pattern.matches("sort.*", entry.getKey())) {
				if (entry.getKey().contains(Constants.SORT_BY_DESCRIPTION_ASC)) {
					return com.ibm.is.sappack.cw.app.services.config.DBOperations.getTargetLegacySystems(com.ibm.is.sappack.cw.app.services.config.DBOperations.DatabaseSortOptions.SORT_BY_DESCRIPTION_ASC, request.getSession());
				}
				if (entry.getKey().contains(Constants.SORT_BY_DESCRIPTION_DESC)) {
					return com.ibm.is.sappack.cw.app.services.config.DBOperations.getTargetLegacySystems(com.ibm.is.sappack.cw.app.services.config.DBOperations.DatabaseSortOptions.SORT_BY_DESCRIPTION_DESC, request.getSession());
				}
				if (entry.getKey().contains(Constants.SORT_BY_LEGACYID_ASC)) {
					return com.ibm.is.sappack.cw.app.services.config.DBOperations.getTargetLegacySystems(com.ibm.is.sappack.cw.app.services.config.DBOperations.DatabaseSortOptions.SORT_BY_LEGACYID_ASC, request.getSession());
				}
				if (entry.getKey().contains(Constants.SORT_BY_LEGACYID_DESC)) {
					return com.ibm.is.sappack.cw.app.services.config.DBOperations.getTargetLegacySystems(com.ibm.is.sappack.cw.app.services.config.DBOperations.DatabaseSortOptions.SORT_BY_LEGACYID_DESC, request.getSession());
				}
			}
		}
		
		return com.ibm.is.sappack.cw.app.services.config.DBOperations.getTargetLegacySystems(com.ibm.is.sappack.cw.app.services.config.DBOperations.DatabaseSortOptions.SORT_NONE, request.getSession());
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public LegacySystem addTargetSapSystem(LegacySystem system) {
		Security.replaceHarmfulChars(system);
		return com.ibm.is.sappack.cw.app.services.config.DBOperations.addLegacySystem(system);
	}

	@PUT
	@Path("/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public LegacySystem updateTargetSapSystem(@PathParam("id") String legacyId, LegacySystem system) {
		Security.replaceHarmfulChars(system);
		return com.ibm.is.sappack.cw.app.services.config.DBOperations.updateLegacySystem(legacyId, system);
	}
	
	@DELETE
	@Path("/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void removeTargetSapSystem(@PathParam("id") String legacyId) {
		com.ibm.is.sappack.cw.app.services.config.DBOperations.deleteLegacySystem(legacyId);
	}
}
