package com.ibm.is.sappack.cw.app.services.config.jaxrs;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

@Path("/pwd/sap")
public class SapPasswordService {

	@GET
	@Path("/{legacyId}")
	@Produces(MediaType.TEXT_PLAIN)
	public String getSapPassword(@Context HttpServletRequest request, @Context HttpServletResponse response, @PathParam("legacyId") String legacyId) {
		response.setHeader("Cache-Control","no-cache"); 
		response.setHeader("Pragma","no-cache"); 
		response.setDateHeader ("Expires", -1); 
		return (String) request.getSession().getAttribute(legacyId);
	}

	@POST
	@Path("/{legacyId}")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public void setSapPassword(String password, @Context HttpServletRequest request, @PathParam("legacyId") String legacyId) {
		request.getSession().setAttribute(legacyId, password);
	}
	
	@DELETE
	@Path("/{legacyId}")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public void deleteSapPassword(@Context HttpServletRequest request, @PathParam("legacyId") String legacyId) {
		request.getSession().removeAttribute(legacyId);
	}
}
