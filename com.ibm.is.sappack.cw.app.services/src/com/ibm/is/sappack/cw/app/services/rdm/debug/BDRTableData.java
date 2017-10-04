package com.ibm.is.sappack.cw.app.services.rdm.debug;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;

import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;

import com.ibm.is.sappack.cw.app.services.Util;

@Path("/bdrTable")
public class BDRTableData {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public StreamingOutput getTables(@Context HttpServletRequest request) {
		JSONArray result = (JSONArray) request.getSession().getAttribute("data");
		if (result == null) {
			result = new JSONArray();
			JSONObject kna1 = new JSONObject();
			JSONObject mara = new JSONObject();
			JSONObject knvv = new JSONObject();
			try {
				kna1.put("name", "KNA1");
				mara.put("name", "MARA");
				knvv.put("name", "KNVV");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			result.add(kna1);
			result.add(mara);
			result.add(knvv);
			request.getSession().setAttribute("data", result);
		}
		return Util.output(result.toString());
	}

	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public void addTable(String data, @Context HttpServletRequest request) throws JSONException {
		JSONArray datas = (JSONArray) request.getSession().getAttribute("data");
		JSONObject obj = new JSONObject();
		System.out.println(datas);
		obj.put("name", data);
		if (datas != null) {
			datas.add(obj);
			request.getSession().setAttribute("data", datas);
		}
		System.out.println("Got data from client " + data);
	}

	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteTables(String data) {
		System.out.println(data);
	}

	@Path("/fieldData")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public StreamingOutput getDataField(@Context HttpServletRequest request, @QueryParam("table") String table) {
		JSONArray result = new JSONArray();
		JSONObject kna1 = new JSONObject();
		JSONObject mara = new JSONObject();
		JSONObject knvv = new JSONObject();
		try {
			kna1.put("status", "**** " + table);
			kna1.put("pos", "**** " + table);
			kna1.put("fName", "First Field " + table);
			kna1.put("checkTable", "**** " + table);
			kna1.put("owner", "**** " + table);
			kna1.put("source", "**** " + table);
			kna1.put("text", "**** " + table);
			kna1.put("description", "**** " + table);
			kna1.put("comment", "**** " + table);
			mara.put("status", "**** " + table);
			mara.put("pos", "**** " + table);
			mara.put("fName", "Second Field " + table);
			mara.put("checkTable", "**** " + table);
			mara.put("owner", "****" + table);
			mara.put("source", "****" + table);
			mara.put("text", "****" + table);
			mara.put("description", "****" + table);
			mara.put("comment", "**** " + table);
			knvv.put("status", "****" + table);
			knvv.put("pos", "**** " + table);
			knvv.put("fName", "Third Field " + table);
			knvv.put("checkTable", "****" + table);
			knvv.put("owner", "**** " + table);
			knvv.put("source", "**** " + table);
			knvv.put("text", "**** " + table);
			knvv.put("description", "**** " + table);
			knvv.put("comment", "**** " + table);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		result.add(kna1);
		result.add(mara);
		result.add(knvv);
		request.getSession().setAttribute("fieldData", result);
		return Util.output(result.toString());
	}
	@Path("/fieldUsage")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public StreamingOutput getDataUsage(@Context HttpServletRequest request, @QueryParam("field") String field) {
		JSONArray result = new JSONArray();
		JSONObject a = new JSONObject();
		JSONObject b = new JSONObject();
		JSONObject c = new JSONObject();
		try {
			a.put("proccess", "1" + field);
			a.put("partOfBo", "#####" + field);
			a.put("rde", "true");
			a.put("use", "read");
			b.put("proccess", "2" + field);
			b.put("partOfBo", "#####" + field);
			b.put("rde", "false" );
			b.put("use", "write");
			c.put("proccess", "3" + field);
			c.put("partOfBo", "#####" + field);
			c.put("rde", "true" );
			c.put("use", "write");

		} catch (JSONException e) {
			e.printStackTrace();
		}
		result.add(a);
		result.add(b);
		result.add(c);
		request.getSession().setAttribute("fieldData", result);
		return Util.output(result.toString());
	}
}
