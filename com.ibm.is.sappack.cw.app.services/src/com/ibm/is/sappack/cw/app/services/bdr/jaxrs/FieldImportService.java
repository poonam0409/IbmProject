package com.ibm.is.sappack.cw.app.services.bdr.jaxrs;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
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

import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONObject;
import org.codehaus.jackson.map.ObjectMapper;

import com.ibm.is.sappack.cw.app.data.bdr.jpa.Table;
import com.ibm.is.sappack.cw.app.data.config.LegacySystem;
import com.ibm.is.sappack.cw.app.services.AbstractThreadedService;
import com.ibm.is.sappack.cw.app.services.Constants;
import com.ibm.is.sappack.cw.app.services.CwApp;
import com.ibm.is.sappack.cw.app.services.CwAppException;
import com.ibm.is.sappack.cw.app.services.Util;
import com.ibm.is.sappack.cw.app.services.bdr.threads.FieldDataImporterThread;
import com.ibm.is.sappack.cw.app.services.config.jaxrs.SettingService;
import com.ibm.websphere.webmsg.publisher.Publisher;

@Path("/bdrFieldData")
public class FieldImportService extends AbstractThreadedService {
	
	private static final String CLASS_NAME = FieldImportService.class.getName();
	private static final String SESSION_ATTRIBUTE_NAME = Constants.SESSION_ATTRIBUTE_NAME_BDR_FIELD_IMPORT_THREAD;

	@Override
	protected String getSessionAttributeName() {
		return SESSION_ATTRIBUTE_NAME;
	}

	@POST
	@Path("/import")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_PLAIN)
	public StreamingOutput loadFieldData(@Context HttpServletRequest servletRequest, @Context ServletContext servletContext,
			String parameterString) {
		final String METHOD_NAME = "loadFieldData(String parameterString)";
		Logger logger = CwApp.getLogger();
		logger.entering(CLASS_NAME, METHOD_NAME);

		JSONObject response = new JSONObject();
		
		try {
			ObjectMapper jsonMapper = new ObjectMapper();
			JSONObject parameterObject = new JSONObject(parameterString);
					
			JSONArray tablesAsJson = parameterObject.getJSONArray("tables");
			List<Table> selectedTables = new ArrayList<Table>();
			for (int i = 0; i < tablesAsJson.size(); i++) {
				JSONObject table = tablesAsJson.getJSONObject(i);
				selectedTables.add(jsonMapper.readValue(table.toString(), Table.class));
			}

			JSONObject sapSystemAsJson = parameterObject.getJSONObject("sapSystem");
			LegacySystem legacySystem = jsonMapper.readValue(sapSystemAsJson.toString(), LegacySystem.class);
			Publisher publisher = Util.getJmsPublisher(servletContext);
			
			SettingService settingService = new SettingService();
			String rdmLanguage = settingService.getSetting(Constants.SETTING_RDM_LANGUAGE);
			if (rdmLanguage == null || rdmLanguage.isEmpty()) {
				logger.severe("RDM Hub language setting is missing");
				throw new CwAppException(HttpURLConnection.HTTP_INTERNAL_ERROR);
			}

			FieldDataImporterThread importerThread = new FieldDataImporterThread(publisher, selectedTables, legacySystem, servletRequest.getSession(), rdmLanguage);
			servletRequest.getSession().setAttribute(getSessionAttributeName(), importerThread);
			response.put("maximum", 1);
		} catch (Exception e){
			Util.throwInternalErrorToClient(e);
		}
		logger.exiting(CLASS_NAME, METHOD_NAME);
		return Util.output(response.toString());
	}
}
