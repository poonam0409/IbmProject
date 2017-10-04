package com.ibm.is.sappack.cw.app.services.bdr.jaxrs;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
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
import com.ibm.is.sappack.cw.app.services.bdr.threads.BdrExporterThread;
import com.ibm.is.sappack.cw.app.services.bdr.threads.BdrExporterThread.ExportMode;
import com.ibm.websphere.webmsg.publisher.Publisher;

@Path("/bdr/export")
public class BdrExportService extends AbstractThreadedService {

	private static final String SESSION_ATTRIBUTE_NAME = Constants.SESSION_ATTRIBUTE_NAME_BDR_EXPORT_THREAD;

	@Override
	protected String getSessionAttributeName() {
		return SESSION_ATTRIBUTE_NAME;
	}

	@GET
	@Path("/removeFile")
	public void removeFile(@Context HttpServletRequest servletRequest) {
		// remove the file content from session and free space
		servletRequest.getSession().removeAttribute(Constants.SESSION_ATTRIBUTE_NAME_BDR_EXPORT_FILE_CONTENT);
	}

	@GET
	@Path("/toFile")
	@Produces(MediaType.TEXT_PLAIN)
	public void downloadFile(@Context HttpServletRequest request, @Context HttpServletResponse response) {

		response.setHeader("Content-Disposition", "attachment;filename=\"" + "bdr" + ".csv\"");
		response.setCharacterEncoding("utf-8");
		// added
		response.setContentType("text/plain;charset=UTF-8");

		HttpSession session = request.getSession();
		String fileContent = (String) session.getAttribute(Constants.SESSION_ATTRIBUTE_NAME_BDR_EXPORT_FILE_CONTENT);

		try {
			if (fileContent != null && !fileContent.equals("")) {
				logger.finer("File size in bytes: " + fileContent.getBytes("UTF-8").length);
				response.setContentLength(fileContent.getBytes("UTF-8").length);
			} else {
				logger.finer("Empty file!");
			}
			response.getWriter().append(fileContent);
			response.getWriter().flush();
		} catch (IOException e) {
			Util.throwInternalErrorToClient(e);
		}
	}

	@GET
	@Path("/prepareExport")
	public StreamingOutput prepareExport(@Context HttpServletRequest servletRequest, @Context ServletContext servletContext, 
			@DefaultValue("FULL_BDR_EXPORT") @QueryParam("exportMode") String exportMode, @DefaultValue("WHOLE_BDR_TREE") @QueryParam("exportNodes") String exportNodes,
			@DefaultValue("false") @QueryParam("exportOnlyTables") String exportOnlyTables,@QueryParam("itemType") String itemType) {
		JSONObject response = new JSONObject();
		try {
			Publisher publisher = Util.getJmsPublisher(servletContext);
			HttpSession session = servletRequest.getSession();
			
			Boolean onlyTablesExport = Boolean.valueOf(exportOnlyTables);
			BdrExporterThread exporterThread = new BdrExporterThread(publisher, session, ExportMode.valueOf(exportMode), exportNodes, onlyTablesExport, itemType); // EXT230
			session.setAttribute(Constants.SESSION_ATTRIBUTE_NAME_BDR_EXPORT_THREAD, exporterThread);

			response.put("Preparing Thread", "successful");
		} catch (JSONException e) {
			Util.throwInternalErrorToClient(e);
		}
		return Util.output(response.toString());
	}
}
