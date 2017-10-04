package com.ibm.is.sappack.cw.app.services.bdr.jaxrs;

import java.util.HashMap;
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

import com.ibm.is.sappack.cw.app.data.rdm.jpa.ReferenceTable;
import com.ibm.is.sappack.cw.app.services.AbstractThreadedService;
import com.ibm.is.sappack.cw.app.services.Constants;
import com.ibm.is.sappack.cw.app.services.CwApp;
import com.ibm.is.sappack.cw.app.services.Util;
import com.ibm.is.sappack.cw.app.services.bdr.threads.TablesImporterThread;
import com.ibm.websphere.webmsg.publisher.Publisher;

// TODO move to debug package
@Path("/bdrTablesData")
public class TableImportService extends AbstractThreadedService {
	
	private static final String CLASS_NAME = TableImportService.class.getName();
	private static final String SESSION_ATTRIBUTE_NAME = Constants.SESSION_ATTRIBUTE_NAME_BDR_TABLES_IMPORT_THREAD;

	@Override
	protected String getSessionAttributeName() {
		return SESSION_ATTRIBUTE_NAME;
	}

	@SuppressWarnings("unchecked")
	@POST
	@Path("/import")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_PLAIN)
	public StreamingOutput loadTablesData(@Context HttpServletRequest servletRequest, @Context ServletContext servletContext) {
		final String METHOD_NAME = "loadTablesData()";
		Logger logger = CwApp.getLogger();
		logger.entering(CLASS_NAME, METHOD_NAME);
		JSONObject response = new JSONObject();
		try {
			HashMap<String, ReferenceTable> allTables = (HashMap<String, ReferenceTable>) servletRequest.getSession()
					.getAttribute(Constants.SESSION_ATTRIBUTE_NAME_DATA_MODEL);
			
			Publisher publisher = Util.getJmsPublisher(servletContext);

			TablesImporterThread importerThread = new TablesImporterThread(publisher, servletRequest.getSession(), allTables);
			servletRequest.getSession().setAttribute(getSessionAttributeName(), importerThread);

			response.put("maximum", 1);
		}
		catch (JSONException e){
			Util.throwInternalErrorToClient(e);
		}
		logger.exiting(CLASS_NAME, METHOD_NAME);
		return Util.output(response.toString());
	}

}
