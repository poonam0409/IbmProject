package com.ibm.is.sappack.cw.app.services.rdm.jaxrs;

import java.net.HttpURLConnection;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.naming.NamingException;
import javax.persistence.TypedQuery;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;

import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONObject;
import org.codehaus.jackson.map.ObjectMapper;

import com.ibm.is.sappack.cw.app.data.JPAResourceFactory;
import com.ibm.is.sappack.cw.app.data.config.LegacySystem;
import com.ibm.is.sappack.cw.app.data.rdm.ITable;
import com.ibm.is.sappack.cw.app.data.rdm.TableStatus;
import com.ibm.is.sappack.cw.app.data.rdm.jpa.ReferenceTable;
import com.ibm.is.sappack.cw.app.services.AbstractThreadedService;
import com.ibm.is.sappack.cw.app.services.CWDBConnectionFactory;
import com.ibm.is.sappack.cw.app.services.Constants;
import com.ibm.is.sappack.cw.app.services.CwApp;
import com.ibm.is.sappack.cw.app.services.CwAppException;
import com.ibm.is.sappack.cw.app.services.Util;
import com.ibm.is.sappack.cw.app.services.config.jaxrs.SettingService;
import com.ibm.is.sappack.cw.app.services.rdm.DBOperations;
import com.ibm.is.sappack.cw.app.services.rdm.threads.ReferenceDataLoadThread;
import com.ibm.websphere.webmsg.publisher.Publisher;

@Path("/rdmLoad")
public class LoadService extends AbstractThreadedService {
	
	private static final String SESSION_ATTRIBUTE_NAME = Constants.SESSION_ATTRIBUTE_NAME_RDM_LOAD_THREAD;
	private final static String CLASS_NAME = LoadService.class.getName();
	
	@Override
	protected String getSessionAttributeName() {
		return SESSION_ATTRIBUTE_NAME;
	}

	// Returns a JSON array with all existing rollout values
	@Path("/getRollouts/{sapSystemId}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> getRollouts(@PathParam("sapSystemId") String sapSystemId) {
		final String METHOD_NAME = "getRollouts(String sapSystemId)";
		logger.entering(CLASS_NAME, METHOD_NAME);

		logger.exiting(METHOD_NAME, CLASS_NAME);
		return DBOperations.getRolloutValuesFromCWDB(sapSystemId);
	}
	
	// Returns a JSON array with all existing LOB values
	@Path("/getBOs/{sapSystemId}/{rollout}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> getBOs(@PathParam("sapSystemId") String sapSystemId, @PathParam("rollout") String rollout) {
		final String METHOD_NAME = "getBOs(String sapSystemId, String rollout)";
		logger.entering(CLASS_NAME, METHOD_NAME);

		logger.exiting(METHOD_NAME, CLASS_NAME);
		return DBOperations.getLobValuesFromCWDB(sapSystemId, rollout);
	}
	
	// Retrieves the list of check tables and domain tables for user selection
	@Path("/preview/{sapSystemId}/{rollout}/{bo}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<ReferenceTable> preview(@PathParam("sapSystemId") String sapSystemId, @PathParam("rollout") String rollout, @PathParam("bo") String bo) {
		final String METHOD_NAME = "preview(String sapSystemId, String rollout, String bo)";
		logger.entering(CLASS_NAME, METHOD_NAME);

		HashMap<String, ReferenceTable> tableMap = DBOperations.getCheckTablePreviewFromCWDB(sapSystemId, rollout, bo);
		tableMap.putAll(DBOperations.getDomainTablePreviewFromCWDB(sapSystemId, rollout, bo));
		
		// Set table status (missing, empty, loaded)
		Collection<ReferenceTable> tables = setTableStatusValues(tableMap);

		logger.exiting(METHOD_NAME, CLASS_NAME);
		return tables;
	}
	
	// Check which tables exist in CWDB and CWApp DB. The status is set in each table object.
	private Collection<ReferenceTable> setTableStatusValues(HashMap<String, ReferenceTable> referenceTables) {
		// Empty input
		if (referenceTables.isEmpty()) {
			return referenceTables.values();
		}
		
		// Check which tables exist in CWDB
		checkCwdbForTables(referenceTables);

		// Check which tables exist in CWApp DB
		TypedQuery<ReferenceTable> query = JPAResourceFactory.getEntityManager().createNamedQuery("ReferenceTable.retrieveByTableNameList", ReferenceTable.class);
		query.setParameter("names", referenceTables.keySet());
		List<ReferenceTable> resultList = query.getResultList();

		for (ReferenceTable r : resultList) {
			TableStatus status = referenceTables.get(r.getName()).getTableStatus();
			if (status != TableStatus.MISSING_IN_CW && status != TableStatus.TEXT_TABLE_MISSING_IN_CW) {
				referenceTables.get(r.getName()).setTableStatus(TableStatus.LOADED);
			}
		}

		return referenceTables.values();
	}

	// Wrapper for connection reuse 
	private void checkCwdbForTables(HashMap<String, ReferenceTable> referenceTables) {
		Connection connection = null;

		try {
			connection = CWDBConnectionFactory.getConnection();
			for (ReferenceTable referenceTable : referenceTables.values()) {
				checkCwdbForTable(referenceTable, connection);
			}
		}
		catch (SQLException se) {
			Util.throwInternalErrorToClient(se);
		}
		catch (NamingException e) {
			Util.throwInternalErrorToClient(e);
		}
		finally {
			Util.closeDBObjects(connection);
		}
	}
	
	// check whether the given REFERENCE or TEXT table exists in the CW database
	private void checkCwdbForTable(ITable table, Connection connection) {
		ResultSet rs = null;

		try {
			// get the metadata of the target database
			DatabaseMetaData md = connection.getMetaData();
			// get all tables with the given name
			rs = md.getTables(null, CwApp.getReferenceTableSchema(), table.getName(), null);
			// only if the table has been found the result set will contain rows
			if (!rs.next()) {
				table.setTableStatus(TableStatus.MISSING_IN_CW);
			}

			// after we have checked the existence of the reference table we
			// need to check the text table too
			if (table instanceof ReferenceTable) {
				if (((ReferenceTable) table).hasTextTable()) {
					ITable textTable = ((ReferenceTable) table).getTextTable();
					checkCwdbForTable(textTable, connection);
					if (textTable.getTableStatus() == TableStatus.MISSING_IN_CW) {
						table.setTableStatus(TableStatus.TEXT_TABLE_MISSING_IN_CW);
					}
				}
			}
		}
		catch (SQLException se) {
			Util.throwInternalErrorToClient(se);
		}
		finally {
			Util.closeDBObjects(rs);
		}
	}
	
	@POST
	@Path("/init")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_PLAIN)
	public StreamingOutput loadReferenceData(@Context HttpServletRequest servletRequest, @Context ServletContext servletContext,
			String parameterString) {
		final String METHOD_NAME = "loadReferenceData(String parameterString)";
		logger.entering(CLASS_NAME, METHOD_NAME);
		
		JSONObject response = new JSONObject();
		
		try {
			ObjectMapper jsonMapper = new ObjectMapper();

			JSONObject parametersJson = new JSONObject(parameterString);
			JSONArray tablesJson = parametersJson.getJSONArray("tables");
			String rollout = parametersJson.getString("rollout");
	
			List<ReferenceTable> selectedTables = new ArrayList<ReferenceTable>();
			for (int i = 0; i < tablesJson.size(); i++) {
				JSONObject table = tablesJson.getJSONObject(i);
				selectedTables.add(jsonMapper.readValue(table.toString(), ReferenceTable.class));
			}
	
			Publisher publisher = Util.getJmsPublisher(servletContext);
			SettingService settingService = new SettingService();
			String rdmLanguage = settingService.getSetting(Constants.SETTING_RDM_LANGUAGE);
			if (rdmLanguage == null || rdmLanguage.isEmpty()) {
				logger.severe("RDM Hub language setting is missing");
				throw new CwAppException(HttpURLConnection.HTTP_INTERNAL_ERROR);
			}
			
			ReferenceDataLoadThread importerThread = null;
			if (parametersJson.getBoolean("reload")) {
				importerThread = new ReferenceDataLoadThread(publisher, selectedTables, servletRequest.getSession(), rdmLanguage);
			} else {
				JSONObject sapSystemAsJson = parametersJson.getJSONObject("legacySystems");
				LegacySystem legacySystem = jsonMapper.readValue(sapSystemAsJson.toString(), LegacySystem.class);
				importerThread = new ReferenceDataLoadThread(publisher, selectedTables, legacySystem, rollout, servletRequest.getSession(), rdmLanguage);
			}
			servletRequest.getSession().setAttribute(getSessionAttributeName(), importerThread);
	
			response.put("maximum", selectedTables.size());
		} catch (Exception e){
			Util.throwInternalErrorToClient(e);
		}
		logger.exiting(CLASS_NAME, METHOD_NAME);
		return Util.output(response.toString());
	}
}
