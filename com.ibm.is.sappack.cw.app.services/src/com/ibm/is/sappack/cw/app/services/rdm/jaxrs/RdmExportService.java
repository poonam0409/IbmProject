package com.ibm.is.sappack.cw.app.services.rdm.jaxrs;

import java.net.HttpURLConnection;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;

import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;

import com.ibm.is.sappack.cw.app.services.AbstractThreadedService;
import com.ibm.is.sappack.cw.app.services.Constants;
import com.ibm.is.sappack.cw.app.services.CwApp;
import com.ibm.is.sappack.cw.app.services.CwAppException;
import com.ibm.is.sappack.cw.app.services.Util;
import com.ibm.is.sappack.cw.app.services.config.jaxrs.SettingService;
import com.ibm.is.sappack.cw.app.services.rdm.clients.RdmSetClient;
import com.ibm.is.sappack.cw.app.services.rdm.threads.ReferenceDataExporterThread;
import com.ibm.websphere.webmsg.publisher.Publisher;

@Path("/reftable")
public class RdmExportService extends AbstractThreadedService {
	
	private static final String SESSION_ATTRIBUTE_NAME = Constants.SESSION_ATTRIBUTE_NAME_RDM_EXPORT_THREAD;
	private static final String CLASS_NAME = RdmExportService.class.getName();
	
	@Override
	protected String getSessionAttributeName() {
		return SESSION_ATTRIBUTE_NAME;
	}
	
	// - Upload selected tables to RDM as sets
    // - Persist the RDM sets in the DB
    // - Set RDM set names on the ref tables
	@POST
	@Path("/export")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public StreamingOutput exportReferenceTables(@Context HttpServletRequest servletRequest, @Context ServletContext servletContext, List<Integer> tableIds) {
		final String METHOD_NAME = "exportReferenceTables (HttpServletRequest servletRequest, ServletContext servletContext, List<Integer> tableIds)";
		Logger logger = CwApp.getLogger();
		logger.entering(CLASS_NAME, METHOD_NAME);
	
		JSONObject response = new JSONObject();
		try {
			// Get RDM password from the session
			String rdmPassword = (String) servletRequest.getSession().getAttribute(Constants.SESSION_ATTRIBUTE_NAME_RDMPASSWORD);
			
			// Initialize RDM connection
			RdmSetClient rdmSetClient = new RdmSetClient(rdmPassword);
			
			Publisher publisher = Util.getJmsPublisher(servletContext);
			
			// Get the RDM language setting
			SettingService settingService = new SettingService();
			String rdmLanguage = settingService.getSetting(Constants.SETTING_RDM_LANGUAGE);
			if (rdmLanguage == null || rdmLanguage.isEmpty()) {
				logger.severe("RDM Hub language setting is missing");
				throw new CwAppException(HttpURLConnection.HTTP_INTERNAL_ERROR);
			}
			
			// Create execution thread
			ReferenceDataExporterThread exporterThread = new ReferenceDataExporterThread(publisher, tableIds, rdmSetClient, rdmLanguage, servletRequest.getSession());
			servletRequest.getSession().setAttribute(getSessionAttributeName(), exporterThread);

			response.put("maximum", tableIds.size());
		} catch (JSONException e){
			Util.throwInternalErrorToClient(e);
		}
		logger.exiting(CLASS_NAME, METHOD_NAME);
		return Util.output(response.toString());
	}
}
