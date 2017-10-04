package com.ibm.is.sappack.cw.app.services.rdm.debug;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;

import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;

import com.ibm.is.sappack.cw.app.services.Util;

@Path("/bdrTree")
public class BDRTreeData {
@GET
@Produces(MediaType.APPLICATION_JSON)
public StreamingOutput getBphTree(){
		JSONObject tree = new JSONObject();
		
		JSONArray process = new JSONArray();
		
		JSONObject jsonObject1 = new JSONObject();
		JSONObject jsonObject2 = new JSONObject();
		JSONObject jsonObject3 = new JSONObject();
		
		try {
			tree.put("identifier", "id");
			tree.put("id", "root");
			tree.put("label", "");
			tree.put("name", "root");
			
			jsonObject1.put("id", "1");
			jsonObject1.put("label", "Product Supply to Pay");
			jsonObject1.put("name", "Product Supply to Pay");
			jsonObject1.put("type", "ProcessScenario");
			jsonObject1.put("description", "Description: Process Scenario");
			
			jsonObject2.put("id", "2");
			jsonObject2.put("label", "Product Order to Cash");
			jsonObject2.put("name", "Product Order to Cash");
			jsonObject2.put("type", "ProcessScenario");
			jsonObject2.put("description", "Description: Process Scenario");
				JSONArray	processesObject2 = new JSONArray();
				JSONObject process1Object2 = new JSONObject ();
				
				process1Object2.put("id", "2-1");
				process1Object2.put("label", "Sales Order Management");
				process1Object2.put("name", "Sales Order Management");
				process1Object2.put("type", "Process");
				process1Object2.put("description", "Description: Process");
				
					JSONArray	processVariant = new JSONArray();
					JSONObject processVariantObject1 = new JSONObject ();
					processVariantObject1.put("id", "2-1-1");
					processVariantObject1.put("label", "Standard SOP in ERP");
					processVariantObject1.put("name", "Standard SOP in ERP");
					processVariantObject1.put("type", "ProcessVariant");
					processVariantObject1.put("description", "Description: Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin eu leo nibh. Integer libero nisi, interdum eget vestibulum ac, vehicula et nulla. Aliquam eu neque velit. Donec id mauris et sem vulputate lobortis eu non neque. Cras at odio eros, in viverra erat. Morbi vehicula mi non lacus placerat porta.");
					
						JSONArray	processStep = new JSONArray();
						JSONObject processStepObject1 = new JSONObject ();
						processStepObject1.put("id", "2-1-1-1");
						processStepObject1.put("label", "Create Sales Order");
						processStepObject1.put("name", "Create Sales Order");
						processStepObject1.put("type", "ProcessStep");
						processStepObject1.put("description", "Description: Process Step");
						
							JSONArray	businessObjects = new JSONArray();
							JSONObject businessObject1 = new JSONObject ();
							businessObject1.put("id", "2-1-1-1");
							businessObject1.put("label", "Customer");
							businessObject1.put("name", "Customer");
							businessObject1.put("type", "BusinessObject");
							businessObject1.put("description", "Description: BusinessObject");
							
								JSONArray	tables = new JSONArray();
								JSONObject tableObject1 = new JSONObject ();
								tableObject1.put("id", "2-1-1-1-1");
								tableObject1.put("label", "KNA1");
								tableObject1.put("name", "KNA1");
								tableObject1.put("type", "Table");
								tableObject1.put("description", "Description: Table");
								
								JSONObject tableObject2 = new JSONObject ();
								tableObject2.put("id", "2-1-1-1-1");
								tableObject2.put("label", "KNA2");
								tableObject2.put("name", "KNA2");
								tableObject2.put("type", "Table");
								tableObject2.put("description", "KNA2");
								
								tables.add(tableObject1);
								tables.add(tableObject2);
								businessObject1.put("children", tables);
							
							businessObjects.add(businessObject1);
							processStepObject1.put("children", businessObjects);
						
						processStep.add(processStepObject1);
						processVariantObject1.put("children", processStep);
					
					processVariant.add(processVariantObject1);
					process1Object2.put("children", processVariant);
				processesObject2.add(process1Object2);
				jsonObject2.put("children", processesObject2);
				
				
			jsonObject3.put("id", "2");
			jsonObject3.put("label", "Order-toCash");
			jsonObject3.put("name", "Order-toCash");
			jsonObject3.put("type", "ProcessScenario");
			jsonObject3.put("description", "Description: ProcessScenario");
			
			process.add(jsonObject1);
			process.add(jsonObject2);
			process.add(jsonObject3);
			
			tree.put("items", process);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	return Util.output(tree.toString());
}

@DELETE
@Consumes(MediaType.APPLICATION_JSON)
public void deleteItem(String data){
	System.out.println("delete Item");
	System.out.println(data);
}

@POST
@Consumes(MediaType.APPLICATION_JSON)
public void addItem(String data){
	System.out.println("add Item");
	System.out.println(data);
}

@Path("/businessObjects")
@GET
@Produces(MediaType.APPLICATION_JSON)
public StreamingOutput getBusinessObjects(@Context HttpServletRequest request){
			JSONArray result = new JSONArray();
			
			JSONObject BO1 = new JSONObject();
			JSONObject BO2 = new JSONObject();
			JSONObject BO3 = new JSONObject();
			try {
				BO1.put("name", "BusinessObject1");
				BO2.put("name", "BusinessObject2");
				BO3.put("name", "BusinessObject3");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			result.add(BO1);
			result.add(BO2);
			result.add(BO3);
			request.getSession().setAttribute("data", result);
		return Util.output(result.toString());
	}

@Path("/tables")
@GET
@Produces(MediaType.APPLICATION_JSON)
public StreamingOutput getTables(@Context HttpServletRequest request){
			JSONArray result = new JSONArray();
			
			JSONObject Table1 = new JSONObject();
			JSONObject Table2 = new JSONObject();
			JSONObject Table3 = new JSONObject();
			try {
				Table1.put("name", "KNA1");
				Table2.put("name", "KNA1");
				Table3.put("name", "KNNR5");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			result.add(Table1);
			result.add(Table2);
			result.add(Table3);
			request.getSession().setAttribute("data", result);
		return Util.output(result.toString());
	}

}
