package com.ibm.is.sappack.cw.app.services.config.jaxrs;

import java.net.HttpURLConnection;
import java.util.List;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.UserTransaction;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.ibm.is.sappack.cw.app.data.JPAResourceFactory;
import com.ibm.is.sappack.cw.app.data.config.jpa.Setting;
import com.ibm.is.sappack.cw.app.services.Constants;
import com.ibm.is.sappack.cw.app.services.CwApp;
import com.ibm.is.sappack.cw.app.services.CwAppException;
import com.ibm.is.sappack.cw.app.services.Util;

@Path("/settings")
public class SettingService {

	private final static String CLASS_NAME = SettingService.class.getName();
	
	private UserTransaction transaction;
	private EntityManager manager;

	public SettingService() {
		this.transaction = JPAResourceFactory.getUserTransaction();
		this.manager = JPAResourceFactory.getEntityManager();
	}

	// REST method called by the RESTStore for retrieving and storing all or single settings
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Setting> getSettings(@QueryParam("name") String name) {
		final String METHOD_NAME = "getSettings()";
		Logger logger = CwApp.getLogger();
		logger.entering(CLASS_NAME, METHOD_NAME);
		
		TypedQuery<Setting> query = null;
		List<Setting> result = null;

		if (name == null || name.isEmpty()) {
			query = manager.createNamedQuery("Setting.retrieveAll", Setting.class);
		} else {
			query = manager.createNamedQuery("Setting.retrieveByName", Setting.class);
			query.setParameter("name", name);
		}
			
		result = query.getResultList();
		
		logger.exiting(CLASS_NAME, METHOD_NAME);
		return result;
	}
	
	// Internal method used by other services directly
	public String getSetting(String name) {
		final String METHOD_NAME = "getSetting(String name)";
		Logger logger = CwApp.getLogger();
		logger.entering(CLASS_NAME, METHOD_NAME);
		
		if (name == null || name.isEmpty()) {
			logger.severe("Setting name is missing");
			throw new CwAppException(HttpURLConnection.HTTP_INTERNAL_ERROR);
		}
		
		TypedQuery<Setting> query = manager.createNamedQuery("Setting.retrieveByName", Setting.class);
		query.setParameter("name", name);
		List<Setting> resultList = query.getResultList();
		String result = null;
		if (!resultList.isEmpty()) {
			result = resultList.get(0).getValue();
		}
		// Provide default value if no language is set
		if (result == null && Constants.SETTING_RDM_LANGUAGE.equals(name)) {
			result = Constants.SETTING_RDM_LANGUAGE_DEFAULT;
		}
		
		logger.finer("Retrieved setting: Name: " + name + ", value: " + result);
		logger.exiting(CLASS_NAME, METHOD_NAME);
		return result;
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Setting addSetting(Setting setting) {
		final String METHOD_NAME = "addSetting(Setting setting)";
		Logger logger = CwApp.getLogger();
		logger.entering(CLASS_NAME, METHOD_NAME);
		try {
			transaction.begin();
			manager.joinTransaction();
			manager.persist(setting);
			transaction.commit();
		}
		catch (Exception e) {
			Util.throwInternalErrorToClient(transaction, e);
		}
		logger.finer("Added setting: Name: " + setting.getName() + ", value: " + setting.getValue());
		logger.exiting(CLASS_NAME, METHOD_NAME);
		return setting;
	}
	
	@PUT
	@Path("/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Setting updateSetting(@PathParam("id") int id, Setting setting) {
		final String METHOD_NAME = "updateSetting(int id, Setting setting)";
		Logger logger = CwApp.getLogger();
		logger.entering(CLASS_NAME, METHOD_NAME);
		try {
			transaction.begin();
			manager.joinTransaction();
			Setting s = manager.find(Setting.class, Integer.valueOf(id));
			s.updateFrom(setting);
			manager.merge(s);
			transaction.commit();
		}
		catch (Exception e) {
			Util.throwInternalErrorToClient(transaction, e);
		}
		logger.finer("Updated setting: Name: " + setting.getName() + ", id: " + id + ", value: " + setting.getValue());
		logger.exiting(CLASS_NAME, METHOD_NAME);
		return setting;
	}
	
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	public void removeSetting(Setting setting) {
		try {
			transaction.begin();
			manager.joinTransaction();
			Setting s = manager.find(Setting.class, setting.getSettingId());
			manager.remove(s);
			transaction.commit();
		}
		catch (Exception e) {
			Util.throwInternalErrorToClient(transaction, e);
		}		
	}
}
