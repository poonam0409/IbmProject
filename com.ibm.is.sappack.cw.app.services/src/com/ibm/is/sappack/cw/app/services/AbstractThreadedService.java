package com.ibm.is.sappack.cw.app.services;

import java.net.HttpURLConnection;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

public abstract class AbstractThreadedService {

	private static final String CLASS_NAME = AbstractThreadedService.class.getName();
	private static final String SUCCESS_RESPONSE = "Success"; // Currently not interpreted on the client

	protected final Logger logger;
	
	// Override to return the correct session attribute name for the thread type
	abstract protected String getSessionAttributeName();
	
	public AbstractThreadedService() {
		this.logger = CwApp.getLogger();
	}
	
	@POST
	@Path("/startThread")
	public String startThread(@Context HttpServletRequest request){
		final String METHOD_NAME = "startThread()";
		Logger logger = CwApp.getLogger();
		logger.entering(CLASS_NAME, METHOD_NAME);
		
		AbstractCancelableThread thread = (AbstractCancelableThread) request.getSession().getAttribute(getSessionAttributeName());
		
		if (thread != null) {
			thread.start();
		} else {
			logger.severe("Internal error: no thread reference found in session for " + getSessionAttributeName());
			throw new CwAppException(HttpURLConnection.HTTP_INTERNAL_ERROR);
		}
		
		logger.exiting(CLASS_NAME, METHOD_NAME);
		return SUCCESS_RESPONSE;
	}
	
	@DELETE
	@Path("/cancel")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public void cancel(@Context HttpServletRequest servletRequest) {
		final String METHOD_NAME = "cancel()";
		Logger logger = CwApp.getLogger();
		logger.entering(CLASS_NAME, METHOD_NAME);
		
		AbstractCancelableThread thread = (AbstractCancelableThread) servletRequest.getSession().getAttribute(getSessionAttributeName());
		
		if (thread != null) { 
			thread.cancel();
			servletRequest.getSession().removeAttribute(getSessionAttributeName());
		}
		logger.exiting(CLASS_NAME, METHOD_NAME);
	}
}
