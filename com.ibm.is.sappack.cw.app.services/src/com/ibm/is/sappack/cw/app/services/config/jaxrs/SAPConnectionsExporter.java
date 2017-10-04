package com.ibm.is.sappack.cw.app.services.config.jaxrs;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import javax.naming.NamingException;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.ibm.is.sappack.cw.app.services.CWDBConnectionFactory;
import com.ibm.is.sappack.cw.app.services.Constants;
import com.ibm.is.sappack.cw.app.services.CwApp;
import com.ibm.is.sappack.cw.app.services.Util;

@Path("/exportSAPConnection")
public class SAPConnectionsExporter {

	private static final String CLASS_NAME = SAPConnectionsExporter.class.getName();
	private static Logger logger = CwApp.getLogger();

	private static final String TEMPLATE_PATH = "resources/SAP_Export_Template.cxp";

	private enum TEMPLATES {

		NAME("#NAME#", "CW_LEGACY_ID"),
		DESCRIPTION("#DESCRIPTION#", "DESCRIPTION"),
		LANGUAGE("#LANGUAGE#", "LANGUAGE"),
		SAPSYSNUM("#SAPSYSNUM#", "SYSTEMNUMBER"),
		USER("#USER#", "USER"),
		CLIENT("#CLIENT#", "CLIENT"),
		SAPSERVER("#SAPSERVER#", "HOST"),
		ROUTERSTRING("#ROUTERSTRING#", "ROUTERSTRING"),
		LOADBALANCING("#LOADBALANCING#", "USELOADBALANCING"),
		MSG_SERVER("#MSGServerSTRING#", "MESSAGESERVER"),
		SAP_GROUP("#SAPGROUP#", "GROUPNAME"),
		SAPSYSID("#SAPSYSID#", "SAPSYSTEMID");

		private final String tempString;
		private final String cwdbColumnName;

		private TEMPLATES(String tempString, String cwdbColumnName) {
			this.tempString = tempString;
			this.cwdbColumnName = cwdbColumnName;
		}

		public String getTempString() {
			return this.tempString;
		}

		public String getCwDbColumnName() {
			return this.cwdbColumnName;
		}
	};

	@GET
	@Path("/{id}")
	@Produces(MediaType.TEXT_PLAIN)
	public void exportSAPConnectionToFile(@Context HttpServletResponse response, @PathParam("id") String legacyId) {
		String sapConnectionString = getSAPConnectionAsString(legacyId);
		
		if (sapConnectionString != null) {
			response.setHeader("Content-Disposition", "attachment;filename=\"" + legacyId + ".cxp\"");
			response.setCharacterEncoding("utf-8");
			response.setContentLength(sapConnectionString.length());
			
			try {
				response.getWriter().append(sapConnectionString);
				response.getWriter().flush();
			}
			catch (IOException e) {
				Util.throwInternalErrorToClient(e);
			}
		}
	}
	
	public String getSAPConnectionAsString(String name) {
		logger.entering(CLASS_NAME, "getSAPConnectionAsString(String name)");
		Connection conn = null;
		PreparedStatement retrieveSapConnection = null;
		ResultSet sapConnection = null;
		BufferedReader reader = null;
		DataInputStream dataInput = null;
		
		try {
			conn = CWDBConnectionFactory.getConnection();

			String line;
			StringBuffer sBuffer = new StringBuffer();
			dataInput = new DataInputStream(this.getClass().getClassLoader().getResourceAsStream(TEMPLATE_PATH));
			reader = new BufferedReader(new InputStreamReader(dataInput, "UTF-8"));

			String sqlQuery = "SELECT * FROM "
				+ Constants.CW_LEGACY_ID_TABLENAME + ", "
				+ Constants.CW_LEGACY_ID_SAP_TABLENAME + " WHERE "
				+ Constants.CW_LEGACY_ID_TABLENAME + "." + Constants.CW_LEGACY_ID_COLUMN + " = ? AND "
				+ Constants.CW_LEGACY_ID_TABLENAME + "." + Constants.CW_LEGACY_ID_COLUMN + " = "
				+ Constants.CW_LEGACY_ID_SAP_TABLENAME + "." + Constants.CW_LEGACY_ID_COLUMN;
			
			logger.fine(sqlQuery);
			
			retrieveSapConnection = conn.prepareStatement(sqlQuery);
			retrieveSapConnection.setString(1, name);
			sapConnection = retrieveSapConnection.executeQuery();
			
			if (sapConnection != null && sapConnection.next()) {
				String lnSeperator = System.getProperty("line.separator");

				while ((line = reader.readLine()) != null) {

					// read the whole template line by line
					sBuffer.append(line + lnSeperator);
				}
				
				String resultString = sBuffer.toString();

				// replace all the template strings with the real values
				for (TEMPLATES template : TEMPLATES.values()) {
					resultString = resultString.replace(template.getTempString(), sapConnection.getString(template.getCwDbColumnName()));
				}

				return resultString;
			}
		}
		catch (SQLException e) {
			Util.throwInternalErrorToClient(e);
		}
		catch (NamingException e) {
			Util.throwInternalErrorToClient(e);
		}
		catch (FileNotFoundException e) {
			Util.throwInternalErrorToClient(e);
		}
		catch (IOException e) {
			Util.throwInternalErrorToClient(e);
		}
		finally {
			try {
				Util.closeDBObjects(sapConnection, retrieveSapConnection, conn);
			}
			finally {
				try {
					if (reader != null) {
						reader.close();
					}
				}
				catch (IOException ioe) {
					Util.throwInternalErrorToClient(ioe);
				}
				finally {
					try {
						if (dataInput != null) {
							dataInput.close();
						}
					}
					catch (IOException ioe) {
						Util.throwInternalErrorToClient(ioe);
					}
				}
			}
		}
		
		return null;
	}
}
