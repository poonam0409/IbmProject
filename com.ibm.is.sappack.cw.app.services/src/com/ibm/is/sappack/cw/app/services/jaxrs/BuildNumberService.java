package com.ibm.is.sappack.cw.app.services.jaxrs;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/buildNumber")
public class BuildNumberService {

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getBuildNumber() {
		return com.ibm.is.sappack.gen.common.Constants.BUILD_ID;
	}
}

