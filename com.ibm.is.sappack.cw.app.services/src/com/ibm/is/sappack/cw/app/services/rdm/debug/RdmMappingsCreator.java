package com.ibm.is.sappack.cw.app.services.rdm.debug;

import java.util.ArrayList;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;

import com.ibm.is.sappack.cw.app.services.Constants;
import com.ibm.is.sappack.cw.app.services.Util;
import com.ibm.is.sappack.cw.app.services.rdm.clients.RdmMappingClient;
import com.ibm.websphere.webmsg.publisher.Publisher;
@Path("/debug")
/**
 * For internal use only
 *  * http://<host>:9080/com.ibm.is.sappack.cw.app.services/jaxrs/createMappings?num=xxx
 *  * http://<host>:9080/com.ibm.is.sappack.cw.app.services/jaxrs/createMappings?num=xxx?maxRels=xxx
 * 	 */
public class RdmMappingsCreator {
	@GET
	@Path("/createMappings")
	@Produces(MediaType.APPLICATION_JSON)
	/**
	 * creates mappings for each set in RDM. This Method uses each sets base id just once.
	 */
	public void createRandomMappingsInRDM(@Context HttpServletRequest servletRequest, @QueryParam("num") int number) {
		try {
			String rdmPassword = (String) servletRequest.getSession().getAttribute(
					Constants.SESSION_ATTRIBUTE_NAME_RDMPASSWORD);
			RdmMappingDebugClient client = new RdmMappingDebugClient(rdmPassword);
			JSONArray sets = client.getAllSets();
			int i = 0;
			ArrayList<String> usedSetIDs = new ArrayList<String>();
			while (i < number && i < sets.size()) {
				JSONObject set = sets.getJSONObject(i);
				String setID = set.getString("BaseID");
				if (usedSetIDs.contains(setID)) {
					i++;
					continue;
				}
				usedSetIDs.add(setID);
				String srcName = set.getString("Name");
				JSONObject mapping = new JSONObject();
				mapping.put("Actions", "Update,Request_Approval,Delete,Create");
				mapping.put("Comment", "");
				mapping.put("Desc", "");
				mapping.put("State", "Draft");
				mapping.put("StateCd", "1");
				mapping.put("StateMachine", "1");
				mapping.put("Type", "Default Mapping Type");
				mapping.put("TypeCd", "2");
				mapping.put("Version", "1");
				mapping.put("SrcSet", setID);
				mapping.put("SrcSetIdPK", setID);
				mapping.put("SrcSetVersion", setID);
				mapping.put("TgtSet", setID);
				mapping.put("TgtSetIdPK", setID);
				mapping.put("TgtSetVersion", setID);
				mapping.put("Name", srcName + " Mapping " + i);
				mapping.put("Owner", "crm,enterprise,mdm");
				mapping.put("RevDate", "");
				mapping.put("EffDate", "1799-12-31T23:00:00Z");
				mapping.put("ExpDate", "9999-12-30T23:00:00Z");
				mapping.put("LastRevisedUser", "tabs");
				client.setRdmMapping(mapping);
				i++;
			}
			// createValueRels();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 
	 * creates value relations for rdm mappings
	 * 
	 * @param maxRels
	 *            maximum relations for one mapping
	 * @param number
	 *            maximum mappings that will be processed
	 */
	@GET
	@Path("createRels")
	@Produces(MediaType.APPLICATION_JSON)
	public void createValueRels(@Context ServletContext servletContext, @QueryParam("maxRels") int maxRels,
			@QueryParam("num") int number) {
		try {
			RdmMappingClient client = new RdmMappingClient("tabs");
			RdmMappingDebugClient client2 = new RdmMappingDebugClient("tabs");
			JSONArray mappings = client.getMappings();
			System.out.println(mappings);
			int i = 0;
			while (i < mappings.size() && i < number) {
				int j = 0;
				JSONObject mapping = mappings.getJSONObject(i);
				Publisher publisher = Util.getJmsPublisher(servletContext);
				JSONArray valuesSrcSet = client.getValuesForSetWithPaging(mapping.getString(Constants.RDM_MAPPING_SRC),
						"", "dsfsdf", publisher, 5,	"fsdf");
				while (j < valuesSrcSet.size() && j < maxRels) {
					JSONObject valueSrc = valuesSrcSet.getJSONObject(j);
					System.out.println(valueSrc);
					JSONObject valueTgt = valuesSrcSet.getJSONObject(j % 10);
					JSONObject relation = new JSONObject();
					relation.put("valRelID", "0");
					relation.put("sourceValStdID", valueSrc.getString("ID"));
					relation.put("sourceValName", valueSrc.getString("Name"));
					relation.put("sourceValBaseID", valueSrc.getString("ID"));
					relation.put("sourceValKeyValues", valueSrc.getString("ID"));
					relation.put("sourceValKey",
							client2.getCompKey(mapping.getString("BaseID"), valueSrc.getString("ID")));
					relation.put("targetValStdID", valueTgt.getString("ID"));
					relation.put("targetValName", valueTgt.getString("Name"));
					relation.put("targetValBaseID", valueTgt.getString("ID"));
					relation.put("targetValKeyValues", valueTgt.getString("ID"));
					relation.put("targetValKey",
							client2.getCompKey(mapping.getString("BaseID"), valueTgt.getString("ID")));
					relation.put("valRelType", "Canonical to Application Specific");
					relation.put("valRelTypeCd", "1");
					relation.put("fromTime", "1799-12-31T23:00:00Z");
					relation.put("toTime", "9999-12-30T23:00:00Z");
					client2.setRdmRel(mapping.getString("ID"), relation);
					client2.putRdmSet(mapping.getString("ID"), mapping);
					j++;
				}
				i++;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
