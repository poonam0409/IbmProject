package com.ibm.is.sappack.cw.app.services.bdr.jaxrs;

import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;

import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;

import com.ibm.is.sappack.cw.app.data.Resources;
import com.ibm.is.sappack.cw.app.services.AbstractThreadedService;
import com.ibm.is.sappack.cw.app.services.Constants;
import com.ibm.is.sappack.cw.app.services.CwApp;
import com.ibm.is.sappack.cw.app.services.Util;
import com.ibm.is.sappack.cw.app.services.bdr.DBOperations;
import com.ibm.is.sappack.cw.app.services.bdr.threads.BdrExportToCwdbThread;
import com.ibm.websphere.webmsg.publisher.Publisher;

@Path("/bdr/exportToCwdb")
public class ExportToCwdbService extends AbstractThreadedService {
	
	private static final String SESSION_ATTRIBUTE_NAME = Constants.SESSION_ATTRIBUTE_NAME_BDR_DB_EXPORT_THREAD;

	
	private final static String CLASS_NAME = ExportToCwdbService.class.getName();
	private final Logger logger;
	
	private static final String REQUEST_PARAM_ROLLOUT = "rollout";
	private static final String REQUEST_PARAM_SYSTEM_ID = "legacyId";
	private static final String REQUEST_PARAM_SEPARATE_SCOPES = "separateScopes";

	public ExportToCwdbService() {
		this.logger = CwApp.getLogger();
	}
	
	@Override
	protected String getSessionAttributeName() {
		return SESSION_ATTRIBUTE_NAME;
	}

	// Returns a JSON array with all existing parameter values
	@Path("/getParams/{sapSystemId}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> getParams(@PathParam("sapSystemId") String sapSystemId) {
		final String METHOD_NAME = "getParams()";
		logger.entering(CLASS_NAME, METHOD_NAME);

		logger.exiting(METHOD_NAME, CLASS_NAME);
		return DBOperations.getExportParamValuesFromCWDB(sapSystemId);
	}
	
	@GET
	@Path("/checkExisting/{rollout}")
	@Produces(MediaType.TEXT_PLAIN)
	public String checkExisting(@PathParam("rollout") String rollout) {
		final String METHOD_NAME = "checkExisting(String rollout)";
		logger.entering(CLASS_NAME, METHOD_NAME);

		if (rollout != null && rollout.length() > Resources.BDR_LENGTH_ROLLOUT) {
			rollout = rollout.substring(0, Resources.BDR_LENGTH_ROLLOUT);
		}
		
		logger.exiting(CLASS_NAME, METHOD_NAME);
		return (DBOperations.checkExistingExportInCWDB(rollout) ? Boolean.toString(true) : "");
	}
	
	@POST
	@Path("/export")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public StreamingOutput export(@Context HttpServletRequest servletRequest, @Context ServletContext servletContext, String input) throws JSONException {
		final String METHOD_NAME = "export(String input)";
		logger.entering(CLASS_NAME, METHOD_NAME);
		
		Publisher publisher = Util.getJmsPublisher(servletContext);
		JSONObject inputJson = new JSONObject(input);
		try {
			// Parse input
			HttpSession session = servletRequest.getSession();
			
			String sapSystemId = inputJson.getString(REQUEST_PARAM_SYSTEM_ID);
			String rollout = inputJson.getString(REQUEST_PARAM_ROLLOUT);
			boolean separateScopes = inputJson.getBoolean(REQUEST_PARAM_SEPARATE_SCOPES);
			
			BdrExportToCwdbThread exportThread = new BdrExportToCwdbThread(servletRequest.getSession(), publisher, sapSystemId, rollout, separateScopes);
			session.setAttribute(Constants.SESSION_ATTRIBUTE_NAME_BDR_DB_EXPORT_THREAD, exportThread);
			inputJson.put("Preparing Thread", "successful");
		} catch (JSONException e){
			Util.throwInternalErrorToClient(e);
		}
		
		logger.exiting(CLASS_NAME, METHOD_NAME);
		return Util.output(inputJson.toString());
	}
}
