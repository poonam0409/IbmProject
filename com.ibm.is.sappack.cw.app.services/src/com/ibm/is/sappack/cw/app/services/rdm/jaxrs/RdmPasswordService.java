package com.ibm.is.sappack.cw.app.services.rdm.jaxrs;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.ibm.is.sappack.cw.app.services.Constants;
import com.ibm.is.sappack.cw.app.services.Security;

@Path("/pwd/rdm")
public class RdmPasswordService {

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getRdmPassword(@Context HttpServletRequest request, @Context HttpServletResponse response) {
		Security.setHeadersResponseNoCache(response);
		return (String) request.getSession().getAttribute(Constants.SESSION_ATTRIBUTE_NAME_RDMPASSWORD);
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public void setRdmPassword(String password, @Context HttpServletRequest request) {
		request.getSession().setAttribute(Constants.SESSION_ATTRIBUTE_NAME_RDMPASSWORD, password);
	}
}
