package com.ibm.is.sappack.cw.app.services.rdm.jaxrs;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.StreamingOutput;

import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;

import com.ibm.is.sappack.cw.app.services.AbstractThreadedService;
import com.ibm.is.sappack.cw.app.services.Constants;
import com.ibm.is.sappack.cw.app.services.Util;
import com.ibm.is.sappack.cw.app.services.rdm.threads.RdmMappingsCreationThread;
import com.ibm.websphere.webmsg.publisher.Publisher;

@Path("/createMappings")
public class RdmMappingsCreationService extends AbstractThreadedService {
	
	private static final String SESSION_ATTRIBUTE_NAME = Constants.SESSION_ATTRIBUTE_NAME_RDM_CREATION_THREAD;
	private final String CLASS_NAME = RdmMappingsCreationService.class.getName();
	
	@Override
	protected String getSessionAttributeName() {
		return SESSION_ATTRIBUTE_NAME;
	}

	// this method takes the selected mappings that the user wants to create
	// creates a thread with all initional data and stores it in the session
	@POST
	public StreamingOutput createMappings(@Context HttpServletRequest request, @Context ServletContext servletContext, String userSelection) {
		final String METHOD_NAME = "createMappings(@Context HttpServletRequest request, @Context ServletContext servletContext, String userSelection)";
		logger.entering(CLASS_NAME, METHOD_NAME);

		JSONObject response = new JSONObject();
		try {
			Publisher publisher = Util.getJmsPublisher(servletContext);
			JSONArray userSelectionJSON = new JSONArray(userSelection);
			response.put("maximum", userSelectionJSON.size());
			RdmMappingsCreationThread thread = new RdmMappingsCreationThread(publisher, userSelectionJSON, request.getSession());
			request.getSession().setAttribute(getSessionAttributeName(), thread);
		} catch (JSONException e) {
			Util.throwInternalErrorToClient(e);
		}
		logger.exiting(CLASS_NAME, METHOD_NAME);
		return Util.output(response.toString());
	}
}
