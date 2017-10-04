package com.ibm.is.sappack.cw.app.services;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class CwAppException extends WebApplicationException {

	private static final long serialVersionUID = 1L;
	
	public CwAppException(String message, int status) {
		super(Response.status(status).entity(message).type(MediaType.TEXT_PLAIN).build());
	}
	
	public CwAppException(String message){
		super(Response.status(400).entity(message).type(MediaType.TEXT_PLAIN).build());
	}
	
	public CwAppException(int status){
		super(Response.status(status).type(MediaType.TEXT_PLAIN).build());
	}
}
