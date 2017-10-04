package com.ibm.is.sappack.cw.app.services.rdm.jaxrs;

import java.net.HttpURLConnection;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;

import com.ibm.is.sappack.cw.app.services.CwAppException;
import com.ibm.is.sappack.cw.app.services.rdm.clients.RdmConnectionTestClient;

@Path("/rdmtest")
public class RdmConnectionTestService {

	private static final String MSG_ERROR_UNAUTHORIZED = "rdmConnectionTestServiceFailedUnauthorized";
	private static final String MSG_ERROR_WITH_STATUS = "rdmConnectionTestServiceFailedWithStatus";
	private static final String MSG_ERROR_INTERNAL_ERROR = "rdmConnectionTestServiceFailedInternalError";

	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public void testRdmConnection(String credentials)  {
		
		try {
			int status = RdmConnectionTestClient.testRdmConnection(new JSONObject(credentials));

			if (status != HttpURLConnection.HTTP_OK) {
				if (status == HttpURLConnection.HTTP_UNAUTHORIZED) {
					throw new CwAppException(MSG_ERROR_UNAUTHORIZED, status);
				}
				
				throw new CwAppException(MSG_ERROR_WITH_STATUS, status);
			}

		} catch (JSONException e) {
			throw new CwAppException(MSG_ERROR_INTERNAL_ERROR, HttpURLConnection.HTTP_INTERNAL_ERROR);
		}
	}
}
