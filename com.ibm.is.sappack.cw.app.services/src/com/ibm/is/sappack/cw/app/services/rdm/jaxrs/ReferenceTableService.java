package com.ibm.is.sappack.cw.app.services.rdm.jaxrs;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;

import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;

import com.ibm.is.sappack.cw.app.data.JPAResourceFactory;
import com.ibm.is.sappack.cw.app.data.rdm.jpa.ReferenceTable;
import com.ibm.is.sappack.cw.app.services.Constants;
import com.ibm.is.sappack.cw.app.services.Util;
import com.ibm.is.sappack.cw.app.services.rdm.threads.ReferenceDataCleanupThread;
import com.ibm.websphere.webmsg.publisher.Publisher;

@Path("/reftables")
public class ReferenceTableService {

	private final EntityManager mgr;

	public ReferenceTableService() {
		this.mgr = JPAResourceFactory.getEntityManager();
	}

	@Path("/retrieve")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<ReferenceTable> getAllReferenceTables() {
		TypedQuery<ReferenceTable> query = mgr.createNamedQuery("ReferenceTable.retrieveAll", ReferenceTable.class);

		if (query != null) {
			return query.getResultList();
		}

		return new ArrayList<ReferenceTable>();
	}

	@Path("/cleanup")
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public StreamingOutput cleanupReferenceTables(@Context HttpServletRequest servletRequest,
	      @Context ServletContext servletContext, List<Integer> tableIds) {
		JSONObject response = new JSONObject();
		try {
			if (tableIds != null && tableIds.size() > 0) {
				Publisher publisher = Util.getJmsPublisher(servletContext);

				ReferenceDataCleanupThread cleanupThread =
				      new ReferenceDataCleanupThread(publisher, tableIds, servletRequest.getSession());
				Thread workerThread = new Thread(cleanupThread);
				workerThread.start();
				servletRequest.getSession()
				      .setAttribute(Constants.SESSION_ATTRIBUTE_NAME_RDM_CLEANUP_THREAD, cleanupThread);
			}

			if (tableIds != null) {
				response.put("maximum", tableIds.size());
			}
			else {
				response.put("maximum", 0);
			}

		}
		catch (JSONException e) {
			Util.throwInternalErrorToClient(e);
		}
		return Util.output(response.toString());
	}
}
