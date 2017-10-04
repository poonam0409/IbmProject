package com.ibm.is.sappack.cw.app.services.jaxrs;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

@Path("/session")
public class SessionService {

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getSessionId(@Context HttpServletRequest servletRequest) {
		return servletRequest.getSession().getId();
	}
}
