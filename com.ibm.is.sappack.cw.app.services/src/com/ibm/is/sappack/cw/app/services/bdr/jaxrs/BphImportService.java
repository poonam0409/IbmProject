package com.ibm.is.sappack.cw.app.services.bdr.jaxrs;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;

import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;

import com.ibm.is.sappack.cw.app.services.AbstractThreadedService;
import com.ibm.is.sappack.cw.app.services.Constants;
import com.ibm.is.sappack.cw.app.services.Util;
import com.ibm.is.sappack.cw.app.services.bdr.BphImportFileContainer;
import com.ibm.is.sappack.cw.app.services.bdr.BphImportType;
import com.ibm.is.sappack.cw.app.services.bdr.threads.BphAbstractImporterThread;
import com.ibm.is.sappack.cw.app.services.bdr.threads.BphCsvImporterThread;
import com.ibm.is.sappack.cw.app.services.bdr.threads.BphMpxImporterThread;
import com.ibm.websphere.webmsg.publisher.Publisher;

@Path("/bdr/bphimport")
public class BphImportService extends AbstractThreadedService {
	
	private static final String SESSION_ATTRIBUTE_NAME = Constants.SESSION_ATTRIBUTE_NAME_BPH_IMPORT_THREAD;
	
	@Override
	protected String getSessionAttributeName() {
		return SESSION_ATTRIBUTE_NAME;
	}
	
	@GET
	@Path("/numberOfBusinessObjectsAndTablesDefined")
	@Produces(MediaType.TEXT_PLAIN)
	public String getNumberOfBusinessObjectsAndTablesDefined() {
		BusinessObjectService boSrvc = new BusinessObjectService();
		TableService tblSrvc = new TableService();
		
		int numberOfBusinessObjectsDefined = boSrvc.getNumberOfBusinessObjectsDefined();
		int numberOfTablesDefined = tblSrvc.getNumberOfTablesDefined();
		
		return String.valueOf(numberOfBusinessObjectsDefined + numberOfTablesDefined);
	}
	
	@POST
	@Path("/import/{importType}")
	public StreamingOutput importBPHFromFile(@PathParam("importType") String importType,@QueryParam ("override") String override ,@Context HttpServletRequest servletRequest, @Context ServletContext servletContext) {
		JSONObject response = new JSONObject();
		try {
			Publisher publisher = Util.getJmsPublisher(servletContext);
			HttpSession session = servletRequest.getSession();
			
			// Get file from session
			BphImportFileContainer container = (BphImportFileContainer)session.getAttribute(Constants.SESSION_ATTRIBUTE_NAME_BPH_IMPORT_FILE);
			
			BphAbstractImporterThread importerThread = null;
			// Set import type
			BphAbstractImporterThread.importType = BphImportType.fromString(importType);
			
			BphAbstractImporterThread.overwrite = Boolean.parseBoolean(override);
			// Choose which thread to start by file type
			switch(container.getFileType()) {
				case SOLUTION_MANAGER:
					importerThread = new BphMpxImporterThread(publisher, session);
					break;
				case BDR_CSV:
					importerThread = new BphCsvImporterThread(publisher, session);
					break;
			}
			
			session.setAttribute(getSessionAttributeName(), importerThread);
			response.put("maximum", 1);
		} catch (JSONException e) {
			Util.throwInternalErrorToClient(e);
		}
		return Util.output(response.toString());
	}
}
