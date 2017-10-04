package com.ibm.is.sappack.cw.app.services.rdm.clients;

import java.net.HttpURLConnection;

import javax.ws.rs.core.MediaType;

import org.apache.wink.client.ClientAuthenticationException;
import org.apache.wink.client.ClientResponse;
import org.apache.wink.client.ClientRuntimeException;
import org.apache.wink.client.Resource;
import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;

import com.ibm.is.sappack.cw.app.services.Constants;
import com.ibm.is.sappack.cw.app.services.Util;

public class RdmConnectionTestClient {

	public static final String CREDENTIALS_HOST_KEY = "host";
	public static final String CREDENTIALS_PORT_KEY = "port";
	public static final String CREDENTIALS_USER_KEY = "user";
	public static final String CREDENTIALS_PASSWORD_KEY = "pwd";
	
	public static final int testRdmConnection(JSONObject credentials) {
		int status = HttpURLConnection.HTTP_BAD_REQUEST;

		try {
			String url = Util.generateRdmUrl(credentials.getString(CREDENTIALS_HOST_KEY), credentials.getString(CREDENTIALS_PORT_KEY));
			String user = credentials.getString(CREDENTIALS_USER_KEY);
			String password = credentials.getString(CREDENTIALS_PASSWORD_KEY);

			Resource resource = AbstractRdmClient.getResource(user, password, url + Constants.RDM_SET_URI);
			ClientResponse response = resource.contentType(MediaType.APPLICATION_JSON_TYPE).get();
			status = response.getStatusCode();
		} catch (JSONException e) {
			Util.throwInternalErrorToClient(e);
		} catch (ClientAuthenticationException e) {
			return HttpURLConnection.HTTP_UNAUTHORIZED;
		} catch (ClientRuntimeException e){
			return HttpURLConnection.HTTP_NOT_FOUND;
		}

		return status;
	}
}
