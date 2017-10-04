package com.ibm.is.sappack.cw.app.services.jaxrs;

import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.ibm.is.sappack.cw.app.services.Constants;
import com.ibm.is.sappack.cw.app.services.CwApp;

@Path("/configCheck")
public class ConfigCheckService {
	
	private final static String CLASS_NAME = ConfigCheckService.class.getName();
	private final Logger logger;

	public ConfigCheckService() {
		this.logger = CwApp.getLogger();
	}
	
	// Check the CW DB for required tables and definitions
	// Returns 0 if everything is OK, 1 otherwise
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String checkConfig() {
		final String METHOD_NAME = "checkConfig()";
		logger.entering(CLASS_NAME, METHOD_NAME);
		
		boolean weAreFine = true;
		
		// Check if all required tables exist
		for (String tableName : Constants.essentialTables) {
			if (!checkTable(tableName)) {
				weAreFine = false;
				break;
			}
		}

		// Check if all required area schemas are defined
		if (CwApp.getReferenceTableSchema() == null) {
			weAreFine = false;
		}
		if (CwApp.getTranscodingTableSchema() == null) {
			weAreFine = false;
		}
		if (CwApp.getDataTableSchema() == null) {
			weAreFine = false;
		}
		
		String resultCode = weAreFine ? "0" : "1";
		
		logger.exiting(METHOD_NAME, CLASS_NAME);
		return resultCode;
	}
	
	private boolean checkTable(String tableName) {
		boolean result = true;
		String schema = tableName.split("\\.")[0];
		String table = tableName.split("\\.")[1];
		if (!com.ibm.is.sappack.cw.app.services.rdm.DBOperations.tableExistsInCWDB(schema, table)) {
			logger.severe("Required CW DB table is missing: " + schema + "." + table);
			result = false;
		}
		return result;
	}
}

