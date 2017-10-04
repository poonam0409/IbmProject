package com.ibm.is.sappack.cw.app.services.rdm.jaxrs;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.NamingException;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.wink.common.model.multipart.BufferedInMultiPart;
import org.apache.wink.common.model.multipart.InPart;
import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;
import org.xml.sax.SAXException;

import com.ibm.is.sappack.cw.app.data.JPAResourceFactory;
import com.ibm.is.sappack.cw.app.data.rdm.ITable;
import com.ibm.is.sappack.cw.app.data.rdm.TableStatus;
import com.ibm.is.sappack.cw.app.data.rdm.jpa.ReferenceTable;
import com.ibm.is.sappack.cw.app.data.rdm.jpa.ReferenceTableFullName;
import com.ibm.is.sappack.cw.app.services.CWDBConnectionFactory;
import com.ibm.is.sappack.cw.app.services.Constants;
import com.ibm.is.sappack.cw.app.services.SchemaMismatchException;
import com.ibm.is.sappack.cw.app.services.Security;
import com.ibm.is.sappack.cw.app.services.rdm.parsers.PhysicalDataModelParser;

@Path("/datamodel")
public class DataModelService {

	private static final Pattern CD_SEMICOLON_PATTERN = Pattern.compile("\\s*+;\\s*+");
	private static final Pattern CD_NON_WHITESPACE_PATTERN = Pattern.compile("\\s*+(\\S*)\\s*+");
	private static final Pattern CD_BETWEEN_QUOTES_PATTERN = Pattern.compile("\"([^\"]*)\"");

	private static final String CD_HEADER = "Content-Disposition";
	private static final String CD_HEADER_NAME = "name";
	private static final String CD_HEADER_FILENAME = "filename";
	private static final String CD_IFRAME_TYPE_STRING = "uploadedfile";
	private static final String CD_HTML5_TYPE_STRING = "s[]";
	private static final String CD_FLASH_TYPE_STRING = "uploadedfileFlash";
	private static final String CD_FLASH_USERAGENT = "Shockwave Flash";

	enum UPLOAD_TYPE {
		HTML5, HTML, FLASH
	};

	enum UPLOAD_RESPONSE_CODE {
		NO_FILE_UPLOADED(4000, "no file selected"),
		FILE_NOT_VALID(5000, "file is no valid data model"),
		FILE_SUCCESSFULLY_PARSED(2000, "file has been successfully parsed"),
		TABLE_SCHEMA_MISMATCH(3000, "schema information does not match"),
		INTERNAL_ERROR(6000, "");

		private final int statusCode;
		private final String message;

		private UPLOAD_RESPONSE_CODE(int statusCode, String message) {
			this.statusCode = statusCode;
			this.message = message;
		}

		public int getStatusCode() {
			return this.statusCode;
		}

		public String getMessage() {
			return this.message;
		}
	};

	private UPLOAD_TYPE uploadType;

	public DataModelService() {
	}

	@SuppressWarnings("unchecked")
	@Path("/retrieve")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<ReferenceTable> getParsedDataModel(@Context HttpServletRequest httpServletRequest) {
		HttpSession session = httpServletRequest.getSession();
		HashMap<ReferenceTableFullName, ReferenceTable> tableMap = (HashMap<ReferenceTableFullName, ReferenceTable>) session
				.getAttribute(Constants.SESSION_ATTRIBUTE_NAME_DATA_MODEL);

		// the parsed data model could not be found in the session context so we simply return an empty list
		if (tableMap == null) {
			return (new ArrayList<ReferenceTable>());
		}

		// check whether the uploaded reference tables already exist in the database
		Collection<ReferenceTable> tables = checkForExistingTables(tableMap);

		return tables;
	}

	@POST
	@Path("/upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.TEXT_HTML)
	public String uploadDataModel(@Context HttpHeaders httpHeaders, @Context HttpServletRequest httpServletRequest,
			BufferedInMultiPart bimp) throws JSONException {
		PhysicalDataModelParser parser = new PhysicalDataModelParser();

		// standard upload type is supposed to be HTML
		// (e.g. anything send by dojo.io.frame.send)
		uploadType = UPLOAD_TYPE.HTML;

		JSONArray jsonArray = new JSONArray();
		List<InPart> parts = bimp.getParts();
		
		// compare webApp context root with referer context root to prevent CSRF
		// abort when not the same
		if(!Security.checkRefererURL(httpServletRequest)){
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("statusCode", UPLOAD_RESPONSE_CODE.INTERNAL_ERROR.getStatusCode());
				jsonObject.put("message", UPLOAD_RESPONSE_CODE.INTERNAL_ERROR.getMessage());
				jsonArray.add(jsonObject);
				
				return jsonArray.toString();
		}
		
		try {
			for (int i = 0; i < parts.size(); i++) {
				if (parts.get(i).getHeaders().containsKey(CD_HEADER)) {
					ArrayList<String> list = (ArrayList<String>) parts.get(i).getHeaders().get(CD_HEADER);
					Map<String, String> cdHeaderMap = parseContentDispositionHeader(list.get(0));
					String name = cdHeaderMap.get(CD_HEADER_NAME);

					// we need to determine the client type for
					// dojox.form.Uploader
					// so that we are able to give the correct response
					if (name != null && name.contains(CD_HTML5_TYPE_STRING)) {
						uploadType = UPLOAD_TYPE.HTML5;
					} else if (name != null && name.equals(CD_FLASH_TYPE_STRING)) {
						uploadType = UPLOAD_TYPE.FLASH;
					} else if (name != null && name.equals(CD_IFRAME_TYPE_STRING)) {
						uploadType = UPLOAD_TYPE.HTML;
				    } else {
						continue;
					}

					String filename = cdHeaderMap.get(CD_HEADER_FILENAME);

					if (filename != null) {
						if (filename.toLowerCase().endsWith(PhysicalDataModelParser.FILENAME_EXTENSION.toLowerCase())) {

							// parse the uploaded file and save the parsed
							// information in the session context
							parser.parseModel(parts.get(i).getInputStream());

							// if the uploaded data model contains no reference tables
							// we simply return the FILE_NOT_VALID message
							if (!parser.hasReferenceTables()) {
								JSONObject jsonObject = new JSONObject();
								jsonObject.put("statusCode", UPLOAD_RESPONSE_CODE.FILE_NOT_VALID.getStatusCode());
								jsonObject.put("message", UPLOAD_RESPONSE_CODE.FILE_NOT_VALID.getMessage());
								jsonArray.add(jsonObject);
							}
							else {
								httpServletRequest.getSession().setAttribute(Constants.SESSION_ATTRIBUTE_NAME_DATA_MODEL,
										parser.getReferenceTables());
								JSONObject jsonObject = new JSONObject();
								jsonObject.put("statusCode", UPLOAD_RESPONSE_CODE.FILE_SUCCESSFULLY_PARSED.getStatusCode());
								jsonObject.put("message", UPLOAD_RESPONSE_CODE.FILE_SUCCESSFULLY_PARSED.getMessage());
								jsonArray.add(jsonObject);
							}
						}
						else {
							JSONObject jsonObject = new JSONObject();
							jsonObject.put("statusCode", UPLOAD_RESPONSE_CODE.FILE_NOT_VALID.getStatusCode());
							jsonObject.put("message", UPLOAD_RESPONSE_CODE.FILE_NOT_VALID.getMessage());
							jsonArray.add(jsonObject);
						}
					}
					else {
						JSONObject jsonObject = new JSONObject();
						jsonObject.put("statusCode", UPLOAD_RESPONSE_CODE.NO_FILE_UPLOADED.getStatusCode());
						jsonObject.put("statusCode", UPLOAD_RESPONSE_CODE.NO_FILE_UPLOADED.getMessage());
						jsonArray.add(jsonObject);
					}
				}
			}

			List<String> userAgentHeaders = httpHeaders.getRequestHeader(HttpHeaders.USER_AGENT);
			String userAgent = userAgentHeaders == null ? null : userAgentHeaders.get(0);

			if (uploadType == UPLOAD_TYPE.FLASH || CD_FLASH_USERAGENT.equals(userAgent)) {
				return composeFlashReturn(jsonArray);
			}
			else if (uploadType == UPLOAD_TYPE.HTML5) {
				return jsonArray.toString();
			}

			String result = "<html><body><textarea>" + jsonArray.toString() + "</textarea></body></html>";
			return result;
		}
		catch (SchemaMismatchException sme) {
			String[] schemaNames = new String[2];
			schemaNames[0] = sme.getModelSchemaName();
			schemaNames[1] = sme.getPreConfiguredAreaCode();
			
			return returnError(UPLOAD_RESPONSE_CODE.TABLE_SCHEMA_MISMATCH.getStatusCode(), schemaNames);
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
			return returnError(UPLOAD_RESPONSE_CODE.INTERNAL_ERROR.getStatusCode(), pce.getStackTrace());
		} catch (SAXException se) {
			se.printStackTrace();
			return returnError(UPLOAD_RESPONSE_CODE.INTERNAL_ERROR.getStatusCode(), se.getStackTrace());
		} catch (IOException ioe) {
			ioe.printStackTrace();
			return returnError(UPLOAD_RESPONSE_CODE.INTERNAL_ERROR.getStatusCode(), ioe.getStackTrace());
		}
	}

	@DELETE
	@Path("/delete")
	public void deleteDataModel(@Context HttpServletRequest request) {
		request.getSession().removeAttribute(Constants.SESSION_ATTRIBUTE_NAME_DATA_MODEL);
	}

	private Map<String, String> parseContentDispositionHeader(String value) {
		String[] all = CD_SEMICOLON_PATTERN.split(value);
		int i = 0;
		Matcher m = (CD_NON_WHITESPACE_PATTERN).matcher(all[i]);
		String headerType = "";

		if (m.find()) {
			headerType = m.group(1);
		}

		Map<String, String> mapToReturn = new HashMap<String, String>();

		mapToReturn.put(CD_HEADER, headerType);

		for (i = 1; i < all.length; i++) {

			if (all[i].startsWith(CD_HEADER_NAME)) {
				m = (CD_BETWEEN_QUOTES_PATTERN).matcher(all[i]);

				if (m.find()) {
					mapToReturn.put(CD_HEADER_NAME, m.group(1));
				}
			}
			else if (all[i].startsWith(CD_HEADER_FILENAME)) {
				m = (CD_BETWEEN_QUOTES_PATTERN).matcher(all[i]);

				if (m.find()) {
					mapToReturn.put(CD_HEADER_FILENAME, m.group(1));
				}
			}
		}

		return mapToReturn;
	}

	@SuppressWarnings("unchecked")
	private String composeFlashReturn(JSONArray jsonArray) throws JSONException {
		StringBuffer flashString = new StringBuffer();

		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject jsonObject = ((JSONObject) jsonArray.get(i));
			Set<String> set = jsonObject.keySet();

			for (Iterator<String> it = set.iterator(); it.hasNext();) {
				String key = it.next();
				flashString.append(key).append("=").append(jsonObject.get(key)).append(",");
			}
		}

		String returnString = flashString.toString();
		returnString = returnString.substring(0, returnString.length() - 1);

		return returnString;
	}

	private Collection<ReferenceTable> checkForExistingTables(HashMap<ReferenceTableFullName, ReferenceTable> referenceTables) {

		// sanity check that the parsed data model exists and is not empty
		if ((referenceTables == null) || (referenceTables.size() == 0)) {
			return null;
		}

		String schemaName = "";
		List<String> tableNames = new ArrayList<String>();

		java.util.Iterator<ReferenceTable> iterator = referenceTables.values().iterator();

		for (int i = 0; iterator.hasNext(); i++) {
			ReferenceTable referenceTable = iterator.next();

			// check if the table exists in CW
			checkCWDBForTable(referenceTable);

			if (i == 0) {
				schemaName = referenceTable.getSchema();
			}

			tableNames.add(referenceTable.getName());
		}

		TypedQuery<ReferenceTable> query = JPAResourceFactory.getEntityManager().createNamedQuery("ReferenceTable.retrieveByTableNameList", ReferenceTable.class);
		query.setParameter("schema", schemaName);
		query.setParameter("names", tableNames);

		List<ReferenceTable> resultList = query.getResultList();

		for (ReferenceTable r : resultList) {
			if (referenceTables.get(r.getReferenceTableFullName()).getTableStatus() != TableStatus.MISSING_IN_CW) {
				referenceTables.get(r.getReferenceTableFullName()).setTableStatus(TableStatus.LOADED);
			}
		}

		return referenceTables.values();
	}

	// check whether the given table exists in the CW database
	private void checkCWDBForTable(ITable table) {
		boolean existsInCW = false;
		Connection conn = null;
		ResultSet rs = null;

		try {

			// get the connection
			conn = CWDBConnectionFactory.getConnection();

			if (conn != null) {

				// get the metadata of the target database
				DatabaseMetaData md = conn.getMetaData();

				if (md != null) {

					// return all tables with the given schema and name
					rs = md.getTables(null, table.getSchema(), table.getName(), null);

					// only if the table has been found the result set contains
					// any rows
					while (rs.next()) {
						existsInCW = true;
					}

					// close the result set
					rs.close();
				}

				if (!existsInCW) {
					table.setTableStatus(TableStatus.MISSING_IN_CW);
				}

				// close the connection
				conn.close();

				// after we have checked the existence of the reference table we
				// need to check the text table too
				if (table instanceof ReferenceTable) {
					if (((ReferenceTable) table).hasTextTable()) {
						ITable textTable = ((ReferenceTable) table).getTextTable();

						checkCWDBForTable(textTable);
					}
				}
			}
		}
		catch (SQLException se) {
			se.printStackTrace();
		}
		catch (NamingException e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (rs != null) {
					if (!rs.isClosed()) {
						rs.close();
					}
				}
			}
			catch (SQLException se) {
				se.printStackTrace();
			}
			finally {
				try {
					if (conn != null) {
						if (!conn.isClosed()) {
							conn.close();
						}
					}
				}
				catch (SQLException se) {
					se.printStackTrace();
				}
			}
		}
	}

	private String returnError(int statusCode, Object errorObject) throws JSONException {
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("statusCode", statusCode);
		jsonObject.put("message", errorObject);
		jsonArray.add(jsonObject);

		switch (uploadType) {
			case HTML: {
				return ("<html><body><textarea>" + jsonArray.toString() + "</textarea></body></html>");
			}
			case HTML5: {
				return jsonArray.toString();
			}
			case FLASH: {
				return composeFlashReturn(jsonArray);
			}
			default: {
				return null;
			}
		}
	}
}
