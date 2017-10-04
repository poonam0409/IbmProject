package com.ibm.is.sappack.cw.app.services.config.jaxrs;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.ibm.is.sappack.cw.app.data.config.LegacySystem;
import com.ibm.is.sappack.cw.app.services.CwApp;
import com.ibm.is.sappack.cw.app.services.CwAppException;
import com.ibm.is.sappack.cw.app.services.RfcDestinationDataProvider;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;

@Path("/saptest")
public class SapConnectionTestService {

	private static final String MSG_ERROR_UNAUTHORIZED = "sapConnectionTestServiceFailedUnauthorized";
	private static final String MSG_ERROR_INTERNAL_ERROR = "sapConnectionTestServiceFailedInternalError";
	private static final String MSG_ERROR_CONNECTION_ERROR = "sapConnectionTestServiceFailedConnectionError";

	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public void testSapConnection(String legacySystemJsonString) {
		Logger logger = CwApp.getLogger();
		try {
			ObjectMapper jsonMapper = new ObjectMapper();
			JSONObject sapSystemAsJson = new JSONObject(legacySystemJsonString);
			LegacySystem legacySystem = jsonMapper.readValue(sapSystemAsJson.toString(), LegacySystem.class);

			if (legacySystem != null) {
				logger.fine("Testing connection to SAP system: ID: " + legacySystem.getLegacyId() + ", host: " + legacySystem.getSapHost());
				JCoDestination destination = RfcDestinationDataProvider.getDestination(legacySystem);
				destination.ping();
				logger.fine("Connection successful.");
			} else {
				throw new CwAppException(MSG_ERROR_INTERNAL_ERROR, HttpURLConnection.HTTP_INTERNAL_ERROR);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			throw new CwAppException(MSG_ERROR_INTERNAL_ERROR, HttpURLConnection.HTTP_INTERNAL_ERROR);
		} catch (JsonParseException e) {
			e.printStackTrace();
			throw new CwAppException(MSG_ERROR_INTERNAL_ERROR, HttpURLConnection.HTTP_INTERNAL_ERROR);
		} catch (JsonMappingException e) {
			e.printStackTrace();
			throw new CwAppException(MSG_ERROR_INTERNAL_ERROR, HttpURLConnection.HTTP_INTERNAL_ERROR);
		} catch (IOException e) {
			e.printStackTrace();
			throw new CwAppException(MSG_ERROR_INTERNAL_ERROR, HttpURLConnection.HTTP_INTERNAL_ERROR);
		} catch (JCoException e) {
			if (e.getGroup() == JCoException.JCO_ERROR_LOGON_FAILURE) {
				logger.fine("Logon failed.");
				throw new CwAppException(MSG_ERROR_UNAUTHORIZED, HttpURLConnection.HTTP_UNAUTHORIZED);
			} else if (e.getGroup() == JCoException.JCO_ERROR_COMMUNICATION) {
				logger.fine("Connection error.");
				throw new CwAppException(MSG_ERROR_CONNECTION_ERROR, HttpURLConnection.HTTP_UNAVAILABLE);
			} else {
				logger.fine("Unknown JCo error.");
				throw new CwAppException(MSG_ERROR_INTERNAL_ERROR, HttpURLConnection.HTTP_INTERNAL_ERROR);
			}

		}
	}
}
