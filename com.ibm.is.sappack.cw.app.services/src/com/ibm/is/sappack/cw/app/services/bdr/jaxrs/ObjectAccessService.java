package com.ibm.is.sappack.cw.app.services.bdr.jaxrs;

import java.net.HttpURLConnection;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Status;
import javax.transaction.UserTransaction;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONObject;

import com.ibm.is.sappack.cw.app.data.JPAResourceFactory;
import com.ibm.is.sappack.cw.app.data.Resources;
import com.ibm.is.sappack.cw.app.data.bdr.jpa.Process;
import com.ibm.is.sappack.cw.app.data.bdr.jpa.ProcessStep;
import com.ibm.is.sappack.cw.app.data.bdr.jpa.UserRegistryObject;
import com.ibm.is.sappack.cw.app.services.CwApp;
import com.ibm.is.sappack.cw.app.services.CwAppException;
import com.ibm.is.sappack.cw.app.services.UserRegistryFactory;
import com.ibm.is.sappack.cw.app.services.Util;
import com.ibm.websphere.security.Result;
import com.ibm.websphere.security.UserRegistry;

@Path("/bdr/objectaccess")
public class ObjectAccessService {
	
	private static final String CLASS_NAME = BphService.class.getName();

	private static final String SEARCH_PATTERN = "*";
	// private static final String SPLIT_PATTERN = "\\|";
	private static final String OBJECT_TYPE_USER = "user";
	private static final String OBJECT_TYPE_GROUP = "group";
	// private static final String MAP_KEY_USERS = "users";
	// private static final String MAP_KEY_GROUPS = "groups";
	private static final String WAS_ADMIN_ROLE_REQUIRED = "Administrator";
	private static final String WAS_ADMIN_ROEADONLY_ROLE = "ReadOnly";
	// private static final String SERVLET_CONTEXT_EAR_NAME_ATTRIBUTE = "com.ibm.websphere.servlet.enterprise.application.name";

	private final Logger logger;
	// private final AppManagement appMgmt;
	private final UserRegistry registry;
	private final EntityManager manager;
	private final UserTransaction transaction;
	private boolean checkGrantFlag=false;
	private static Map<String, Map<String, JSONObject>> grantObjMap= new HashMap<String, Map<String, JSONObject>>();

	public ObjectAccessService() {
		this.logger = CwApp.getLogger();
		final String METHOD_NAME = "BphService()";
		logger.entering(CLASS_NAME, METHOD_NAME);
		// this.appMgmt = AppManagementFactory.getAppManagement();
		this.registry = UserRegistryFactory.getUserRegistry();
		this.manager = JPAResourceFactory.getEntityManager();
		this.transaction = JPAResourceFactory.getUserTransaction();
		logger.exiting(CLASS_NAME, METHOD_NAME);
	}
	
	@GET
	@Path("/hasRequiredRole")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject hasRequiredRole(@Context HttpServletRequest request) {
		JSONObject roles = new JSONObject();
		
		try {
		roles.put("isAdmin", request.isUserInRole(WAS_ADMIN_ROLE_REQUIRED));
		roles.put("isReadOnly", request.isUserInRole(WAS_ADMIN_ROEADONLY_ROLE));
		}
		catch (Exception e) {
			Util.throwInternalErrorToClient(transaction, e);
		}
		
		return roles;
	}

	@SuppressWarnings("rawtypes")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray getAllGroupsWithUsersFromWAS() {
		Map<String, JSONObject> userMap = new HashMap<String, JSONObject>();
		JSONArray response = new JSONArray();

		try {
			// retrieve all groups from the user registry
			Result getGroups = registry.getGroups(SEARCH_PATTERN, 0);

			if (getGroups != null) {
				List allGroups = getGroups.getList();
				Iterator groupIter = allGroups.iterator();

				while (groupIter.hasNext()) {
					String groupName = (String) groupIter.next();
					String groupId = registry.getUniqueGroupId(groupName);

					if (!userMap.containsKey(groupId)) {
						JSONObject group = new JSONObject();

						group.put("uniqueId", groupId);
						group.put("securityName", groupName);
						group.put("type", OBJECT_TYPE_GROUP);
						
						userMap.put(groupId, group);
					}
				}
			}
			
			// retrieve all users from the user registry
			Result getUsers = registry.getUsers(SEARCH_PATTERN, 0);

			if (getUsers != null) {
				List allUsers = getUsers.getList();
				Iterator userIter = allUsers.iterator();

				// loop over the list of users and gather information to send to the client
				while (userIter.hasNext()) {
					JSONObject user = new JSONObject();

					String userName = (String) userIter.next();
					String userId = registry.getUniqueUserId(userName);
					user.put("uniqueId", userId);
					user.put("securityName", userName);
					user.put("type", OBJECT_TYPE_USER);

					userMap.put(userId, user);					
				}
			}
			response.addAll(userMap.values());
			
// Code disabled as for some installations the bean method call failed. from now all users from the user registry will be used 		
//			// filter the user and group lists if the role mapping check is available
//			if (request.isUserInRole(WAS_ADMIN_ROLE_REQUIRED)) {
//				String enterpriseAppName = (String) servletContext.getAttribute(SERVLET_CONTEXT_EAR_NAME_ATTRIBUTE);
//				HashMap<String, Collection<String>> mappedObjects = getMappedRegistryObjects(enterpriseAppName);
//
//				HashMap<String, JSONObject> filteredMp = new HashMap<String, JSONObject>();
//				Set<Entry<String, JSONObject>> mpSet = mp.entrySet();
//				
//				Iterator<Entry<String, JSONObject>> mpSetIt = mpSet.iterator();
//				
//				while (mpSetIt.hasNext()) {
//					Entry<String, JSONObject> mpSetEntry = mpSetIt.next();
//
//					String mpSetEntrySecurityName = (String) mpSetEntry.getValue().get("securityName");
//					
//					if (mpSetEntry.getValue().get("type").equals(OBJECT_TYPE_GROUP)) {
//						Collection<String> mappedGroups = mappedObjects.get(MAP_KEY_GROUPS);
//						
//						if (mappedGroups.contains(mpSetEntrySecurityName)) {
//							filteredMp.put(mpSetEntry.getKey(), mpSetEntry.getValue());
//						}				
//					}
//					else if (mpSetEntry.getValue().get("type").equals(OBJECT_TYPE_USER)) {
//						Collection<String> mappedUsers = mappedObjects.get(MAP_KEY_USERS);
//						
//						if (mappedUsers.contains(mpSetEntrySecurityName)) {
//							filteredMp.put(mpSetEntry.getKey(), mpSetEntry.getValue());
//						}				
//					}
//				}
//				
//				return new JSONArray(filteredMp.values());
//			}

		} catch (Exception e) {
			Util.throwInternalErrorToClient(e);
		}
		return response;
	}
	
	@GET
	@Path("/{type}/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection<UserRegistryObject> getUsersAndGroupsForBPHObject(@PathParam("type") String bphObjectType, @PathParam("id") int bphObjectId) {
		logger.finer("getUsersAndGroupsForBPHObject called for " + bphObjectType + ": " + bphObjectId);
		Collection<UserRegistryObject> response = null;
		
		if (bphObjectType.equals(Resources.BPH_TYPE_PROCESSSTEP)) {
			ProcessStep b = manager.find(ProcessStep.class, bphObjectId);
			if (b == null) {
				Util.throwNotFoundErrorToClient(bphObjectId);
			}
			response = b.getAllowed();
		} else {
			logger.severe("An internal error has occurred: Attempted to retrieve ACL for invalid object type");
			throw new CwAppException(HttpURLConnection.HTTP_INTERNAL_ERROR);
		}
		return response;
	}

	@POST
	@Path("/{type}/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public List<UserRegistryObject> updateBPHObjectAddAccessInfo(@PathParam("type") String bphObjectType,
	      @PathParam("id") int bphObjectId, List<UserRegistryObject> uroList) {
		if (bphObjectType.equals(Resources.BPH_TYPE_PROCESSSTEP)) {
			try {
				transaction.begin();
				manager.joinTransaction();
				ProcessStep ps = manager.find(ProcessStep.class, Integer.valueOf(bphObjectId));
				ps.setAllowed(uroList);
				ps.setUpdated(new Date());
				//manager.merge(ps); // Merging here would delete all related BOs
				transaction.commit();
			}
			catch (Exception e) {
				Util.throwInternalErrorToClient(transaction, e);
			}
		}

		return uroList;
	}
	
	@PUT
	@Path("/grantToACL/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void grantAccessToProcessStep(@PathParam("id") Integer processId, UserRegistryObject uro) {
		Process p = manager.find(Process.class, Integer.valueOf(processId));
		iterateSubProcesses(p, uro, true);
	}
	
	@PUT
	@Path("/revokeFromACL/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void revokeAccessFromProcessStep(@PathParam("id") Integer processId, UserRegistryObject uro) {
		Process p = manager.find(Process.class, Integer.valueOf(processId));
		iterateSubProcesses(p, uro, false);
	}
	
	private boolean iterateSubProcesses(Process p, UserRegistryObject uro, boolean grant) {
		boolean granted = false;
		
		try {
			// required as the process object (or one if its descendants gets modified)
			if (transaction.getStatus() == Status.STATUS_NO_TRANSACTION) {
				transaction.begin();
			}
			
			manager.joinTransaction();
			p = manager.find(Process.class, Integer.valueOf(p.getProcessId()));
			
			Collection<Process> children = p.getChildProcesses();
	
			if (children != null && children.size() > 0) {
				Iterator<Process> it = children.iterator();
				
				while (it.hasNext()) {
					Process sp = it.next();
					granted = granted || iterateSubProcesses(sp, uro, grant);
				}
			}
			else {
				Collection<ProcessStep> steps = p.getProcessSteps();
				
				if (steps != null && steps.size() > 0) {
					Iterator<ProcessStep> it = steps.iterator();
					
					while (it.hasNext()) {
						ProcessStep ps = it.next();
						ProcessStep managedPs = manager.find(ProcessStep.class, ps.getProcessStepId());
						
						Collection<UserRegistryObject> acl = managedPs.getAllowed();
						
						Iterator<UserRegistryObject> uroit = acl.iterator();
						
						boolean isMember = false;
						UserRegistryObject uroMember = null;
						
						while (uroit.hasNext()) {
							UserRegistryObject member = uroit.next();
							if (member.getUniqueId().equalsIgnoreCase(uro.getUniqueId())) {
								isMember = true;
								uroMember = member;
							}
						}
						
						if (!isMember && grant) {
							if (transaction.getStatus() == Status.STATUS_NO_TRANSACTION) {
								transaction.begin();
							}
							
							manager.joinTransaction();
							acl.add(uro);
							manager.merge(managedPs);
							transaction.commit();
						}
						else if (isMember && !grant) {
							if (transaction.getStatus() == Status.STATUS_NO_TRANSACTION) {
								transaction.begin();
							}
							
							manager.joinTransaction();
							acl.remove(uroMember);
							manager.merge(managedPs);
							transaction.commit();
						}
					}
				}
			}
		} catch (Exception e) {
			Util.throwInternalErrorToClient(e);
		}
		
		return granted;
	}
	
	
	@SuppressWarnings("rawtypes")
	@GET
	@Path("/{Id}")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray getAllGroupsWithUsersFromWAS(@PathParam("Id") String processId) {
		Map<String, JSONObject> userMap = new HashMap<String, JSONObject>();
		JSONArray response = new JSONArray();

		try {
			// retrieve all groups from the user registry
			Result getGroups = registry.getGroups(SEARCH_PATTERN, 0);

			if (getGroups != null) {
				List allGroups = getGroups.getList();
				Iterator groupIter = allGroups.iterator();

				while (groupIter.hasNext()) {
					String groupName = (String) groupIter.next();
					String groupId = registry.getUniqueGroupId(groupName);

					if (!userMap.containsKey(groupId)) {
						JSONObject group = new JSONObject();

						group.put("uniqueId", groupId);
						group.put("securityName", groupName);
						group.put("type", OBJECT_TYPE_GROUP);
						
						userMap.put(groupId, group);
					}
				}
			}
			
			// retrieve all users from the user registry
			Result getUsers = registry.getUsers(SEARCH_PATTERN, 0);

			if (getUsers != null) {
				List allUsers = getUsers.getList();
				Iterator userIter = allUsers.iterator();

				// loop over the list of users and gather information to send to the client
				while (userIter.hasNext()) {
					JSONObject user = new JSONObject();

					String userName = (String) userIter.next();
					String userId = registry.getUniqueUserId(userName);
					user.put("uniqueId", userId);
					user.put("securityName", userName);
					user.put("type", OBJECT_TYPE_USER);

					userMap.put(userId, user);					
				}
			}
			response.addAll(userMap.values());
			response.removeAll(grantObjMap.get(processId).values());
			grantObjMap.remove(processId);
		} catch (Exception e) {
			Util.throwInternalErrorToClient(e);
		}
		return response;
	}
			

	
	@GET
	@Path("/grantInfo/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONArray checkGrantProcess(@PathParam("id") String processId) {
		Map<String, JSONObject> userMap = new HashMap<String, JSONObject>();
		JSONArray response = new JSONArray();
		boolean granted=false;
		Process p = manager.find(Process.class, Integer.valueOf(processId));

		try {
			// retrieve all groups from the user registry
			Result getGroups = registry.getGroups(SEARCH_PATTERN, 0);

			if (getGroups != null) {
				List allGroups = getGroups.getList();
				Iterator groupIter = allGroups.iterator();

				while (groupIter.hasNext()) {
					String groupName = (String) groupIter.next();
					String groupId = registry.getUniqueGroupId(groupName);

					if (!userMap.containsKey(groupId)) {
						JSONObject group = new JSONObject();

						group.put("uniqueId", groupId);
						group.put("securityName", groupName);
						group.put("type", OBJECT_TYPE_GROUP);
						granted=checkGrant(p, groupId);
						if(granted){
						userMap.put(groupId, group);
						granted=false;
						}
					}
				}
			}
			// retrieve all users from the user registry
			Result getUsers = registry.getUsers(SEARCH_PATTERN, 0);

			if (getUsers != null) {
				List allUsers = getUsers.getList();
				Iterator userIter = allUsers.iterator();

				// loop over the list of users and gather information to send to the client
				while (userIter.hasNext()) {
					JSONObject user = new JSONObject();

					String userName = (String) userIter.next();
					String userId = registry.getUniqueUserId(userName);
					user.put("uniqueId", userId);
					user.put("securityName", userName);
					user.put("type", OBJECT_TYPE_USER);

					granted=checkGrant(p, userId);
					if(granted){
						userMap.put(userId, user);
						granted=false;
					}
				}
			}
			response.addAll(userMap.values());
			grantObjMap.put(processId.toString(), userMap);

		} catch (Exception e) {
			Util.throwInternalErrorToClient(e);
		}
		return response;
	}
	
	
	private boolean checkGrant(Process p, String userId){
		
		p = manager.find(Process.class, Integer.valueOf(p.getProcessId()));
		
		Collection<Process> children = p.getChildProcesses();

		if (children != null && children.size() > 0) {
			Iterator<Process> it = children.iterator();
			
			while (it.hasNext()) {
				Process sp = it.next();
				checkGrant(sp,userId);//iterateSubProcesses(sp, uro, grant);
			}
		}
		else {
			Collection<ProcessStep> steps = p.getProcessSteps();
			
			if (steps != null && steps.size() > 0) {
				Iterator<ProcessStep> it = steps.iterator();
				
				while (it.hasNext())
				{
					ProcessStep ps = it.next();
					ProcessStep managedPs = manager.find(ProcessStep.class, ps.getProcessStepId());
					
					Collection<UserRegistryObject> acl = managedPs.getAllowed();
					
					Iterator<UserRegistryObject> uroit = acl.iterator();
					checkGrantFlag=false;
					while (uroit.hasNext()) {
						UserRegistryObject member = uroit.next();
						if (member.getUniqueId().equalsIgnoreCase(userId)) {
							checkGrantFlag=true;
							break;
						}
					}
				}
				
			}}
		
	return checkGrantFlag;
	}
	/*
	@SuppressWarnings("unchecked")
	private HashMap<String, Collection<String>> getMappedRegistryObjects(String enterpriseAppName) {
		HashMap<String, Collection<String>> mappedObjects = new HashMap<String, Collection<String>>();
		
		try {
			Hashtable<String, Object> prefs = new Hashtable<String, Object>();
			prefs.put(AppConstants.APPDEPL_LOCALE, Locale.getDefault());
			
			if (appMgmt != null) {

				// retrieve a list of all application management tasks
				Vector<AppDeploymentTask> allTasks = appMgmt.getApplicationInfo(enterpriseAppName, prefs, null);
	
				Iterator<AppDeploymentTask> it = allTasks.iterator();
	
				// iterate over all defined application management tasks
				while (it.hasNext()) {
					AppDeploymentTask task = it.next();
	
					// we're looking for the 'MapRolesToUsers' task to get a list of all defined roles
					if (task.getName().equals("MapRolesToUsers") && !task.isTaskDisabled()) {
	
						// find out column index for role, user and group column
						int roleColumn = -1;
						int userColumn = -1;
						int groupColumn = -1;
	
						String[] colNames = task.getColumnNames();
	
						for (int i = 0; i < colNames.length; i++) {
							if (colNames[i].equals("role")) {
								roleColumn = i;
							}
							else if (colNames[i].equals("role.user")) {
								userColumn = i;
							}
							else if (colNames[i].equals("role.group")) {
								groupColumn = i;
							}
						}
	
						// iterate over task data starting at row 1 as row0 contains the column names
						String[][] data = task.getTaskData();
	
						for (int i = 1; i < data.length; i++) {
							if (data[i][roleColumn].equals("FunctionalDataAnalyst")) {
								ArrayList<String> groupList = new ArrayList<String>();
								ArrayList<String> userList = new ArrayList<String>();
								String[] groups = data[i][groupColumn].split(SPLIT_PATTERN);
								String[] users = data[i][userColumn].split(SPLIT_PATTERN);
								Collections.addAll(groupList, groups);
								Collections.addAll(userList, users);
								
								mappedObjects.put(MAP_KEY_GROUPS, groupList);
								mappedObjects.put(MAP_KEY_USERS, userList);
							}
						}
						
						// no need to continue the loop
						break;
					}
				}
			}
		}
		catch (Exception e) {
			Util.handleBatchException(e);
		}
		
		return mappedObjects;
	}
	*/
}
