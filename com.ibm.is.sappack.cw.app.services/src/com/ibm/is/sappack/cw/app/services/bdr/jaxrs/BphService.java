package com.ibm.is.sappack.cw.app.services.bdr.jaxrs;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.ibm.is.sappack.cw.app.data.JPAResourceFactory;
import com.ibm.is.sappack.cw.app.data.Resources;
import com.ibm.is.sappack.cw.app.data.bdr.IPathElement;
import com.ibm.is.sappack.cw.app.data.bdr.PathElement;
import com.ibm.is.sappack.cw.app.data.bdr.jpa.BusinessObject;
import com.ibm.is.sappack.cw.app.data.bdr.jpa.FieldUsage;
import com.ibm.is.sappack.cw.app.data.bdr.jpa.Process;
import com.ibm.is.sappack.cw.app.data.bdr.jpa.ProcessStep;
import com.ibm.is.sappack.cw.app.data.bdr.jpa.Table;
import com.ibm.is.sappack.cw.app.data.bdr.jpa.TableUsage;
import com.ibm.is.sappack.cw.app.data.bdr.jpa.UserRegistryObject;
import com.ibm.is.sappack.cw.app.services.Constants;
import com.ibm.is.sappack.cw.app.services.CwApp;
import com.ibm.is.sappack.cw.app.services.Util;
import com.ibm.is.sappack.cw.app.services.bdr.DBOperations;
import com.ibm.is.sappack.cw.app.services.bdr.DBOperations.ItemType;
import com.ibm.is.sappack.cw.app.services.logging.CWAppLogger;

@Path("/bdr")
public class BphService {

	private static final String CLASS_NAME = BphService.class.getName();

	private final Logger logger;
	private final EntityManager manager;
	private final UserTransaction transaction;

	public BphService() {
		this.logger = CwApp.getLogger();
		final String METHOD_NAME = "BphService()";
		logger.entering(CLASS_NAME, METHOD_NAME);
		this.manager = JPAResourceFactory.getEntityManager();
		this.transaction = JPAResourceFactory.getUserTransaction();
		logger.exiting(CLASS_NAME, METHOD_NAME);
	}

	@GET
	@Path("/tree")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Process> getRootProcessesWithAcl(@Context HttpServletRequest request) {
		final String METHOD_NAME = "getRootProcessesWithAcl()";
		logger.entering(CLASS_NAME, METHOD_NAME);
		
		boolean hasSuperUserRole = request.isUserInRole(Constants.BDR_SUPERUSER_ROLE);
		boolean hasReadOnlyRole = request.isUserInRole(Constants.BDR_READONLY_ROLE);
		
		Principal user = request.getUserPrincipal();
		logger.fine("User: " + user.getName());
		//Initializing logger just after successful log-in
		CWAppLogger.initLogger(user.getName(),request.getSession().getId());
	
		if (hasSuperUserRole || hasReadOnlyRole) {
			logger.fine("User has the SuperUser role");
			CWAppLogger.log(Level.FINE,"CWApp_10002",null);
			long startTime = System.currentTimeMillis();
			logger.fine("Time: " + (System.currentTimeMillis() - startTime));
			TypedQuery<Process> pQuery = manager.createNamedQuery("Process.retrieveRoots", Process.class);
			List<Process> tree = pQuery.getResultList();
			logger.fine("Time: " + (System.currentTimeMillis() - startTime));
			
			// Comb the tree and make sure the table usage objects are only linked from the
			// Business Objects and only below the correct process step
			for (Process rootProcess : tree) {
				iterateSubprocesses(rootProcess, false);	
			}
			logger.fine("Time: " + (System.currentTimeMillis() - startTime));

//			accumulateFieldUsageStatus(tree, null, false);
			logger.fine("Time: " + (System.currentTimeMillis() - startTime));
//			accumulateApprovalStatus(tree, null, false);
			logger.fine("Time: " + (System.currentTimeMillis() - startTime));
			
			// in order to sort the list of results we need to make our own copy
			// as the original JPA result list is read-only
			List<Process> sortedTree = new ArrayList<Process>(tree);
			Collections.sort(sortedTree);
			logger.fine("Time: " + (System.currentTimeMillis() - startTime));
			cullTree(sortedTree);
			logger.fine("Time: " + (System.currentTimeMillis() - startTime));
			
			logger.exiting(CLASS_NAME, METHOD_NAME);
			return sortedTree;
		}
		logger.fine("User does not have the SuperUser role");
		CWAppLogger.log(Level.FINE,"CWApp_10003",null);
		
		// Get the groups the user is a member of
		List<String> groupsForUser = Util.getGroupsFromWASForUser(user.getName());
		
		// Convert the names into UserRegistryObjects
		Set<UserRegistryObject> registryObjectsForUser = new HashSet<UserRegistryObject>();
		for (String groupName : groupsForUser) {
			logger.finer("User is member of group: " + groupName);
			CWAppLogger.log(Level.FINE,"CWApp_10006",new Object[]{groupName});
			registryObjectsForUser.add(Util.getGroupFromWAS(groupName));
		}
		// Add the user itself to the set
		registryObjectsForUser.add(Util.getUserFromWAS(user.getName()));

		// Query the DB for all accessible process steps
		
		// Because of what seems to be a bug in JPA (the IN clause parameter list gets messed up during query execution),
		// We use a loop instead of an IN query.
		Set<ProcessStep> accessibleProcessSteps = new HashSet<ProcessStep>();
		TypedQuery<ProcessStep> query = manager.createNamedQuery("ProcessStep.getAllForSecurityId", ProcessStep.class);
		for (UserRegistryObject uro : registryObjectsForUser) {
			query.setParameter("uniqueId", uro.getUniqueId());
			accessibleProcessSteps.addAll(query.getResultList());
			logger.finer("UserRegistryObject: " + uro.getSecurityName() + ", id: " + uro.getUniqueId());
			for (ProcessStep step : query.getResultList()) {
				logger.finer("Found step: " + step.getName());
			}
		}
		
		List<IPathElement> paths = new ArrayList<IPathElement>();
		for (ProcessStep ps : accessibleProcessSteps) {
			logger.fine("Accessible process step: " + ps.getName());
			PathElement psPathElement = new PathElement();
			psPathElement.setId(ps.getProcessStepId());
			psPathElement.setName(ps.getName());
			psPathElement.setType(Resources.BPH_TYPE_PROCESSSTEP);
			
			Process parent = ps.getParentProcess();
			Util.iterateParentProcess(parent, psPathElement, paths, true);
		}
		
		List<Process> aclTree = new ArrayList<Process>();
		
		// Collect the relevant top level processes into a new tree
		for (IPathElement pathElem : paths) {
			if (pathElem.getType().equals(Resources.BPH_TYPE_PROCESS)) {
				aclTree.add(pathElem.getProcess());
			}
		}
		// Now we have all the correct top level processes, but the process steps under those are not filtered by ACL.
		// So we have potentially more items than the user should see.
		// Don't know if Util.iterateParentProcess is supposed to filter or not, I don't understand what it does.
		// Adding a cull method to remove the extras. -sersch
		
		// Cull the extra process steps from the tree
		cullTreeForAcl(aclTree, accessibleProcessSteps);
		
		// Comb the tree and make sure the table usage objects are only linked from the
		// Business Objects and only below the correct process step
		for (Process rootProcess : aclTree) {
			iterateSubprocesses(rootProcess, true);	
		}
		
//		accumulateFieldUsageStatus(aclTree, null, true);
//		accumulateApprovalStatus(aclTree, null, true);

		// in order to sort the list of results we need to make our own copy
		// as the original JPA result list is read-only
		List<Process> sortedAclTree = new ArrayList<Process>(aclTree);
		Collections.sort(sortedAclTree);
		cullTree(sortedAclTree);
		
		logger.exiting(CLASS_NAME, METHOD_NAME);
		return sortedAclTree;
	}
	
	private static void cullTreeForAcl (List<Process> aclTree, Set<ProcessStep> accessibleProcessSteps) {
		for (Process process : aclTree) {
			// Remove inaccessible process steps
			if (process.getProcessSteps() != null) {
				Iterator<ProcessStep> stepIter = process.getProcessSteps().iterator();
				while (stepIter.hasNext()) {
					ProcessStep step = stepIter.next();
					if (!accessibleProcessSteps.contains(step)) {
						stepIter.remove();
					}
				}
			}
			// Recursively apply to subprocesses
			if (process.getChildProcesses() != null) {
				cullTreeForAcl(process.getChildProcesses(), accessibleProcessSteps);
			}
		}
	}
	
	// Remove child objects irrelevant for the BPH tree for faster transmission
	// TODO: it would be much better to avoid retrieving these elements from the database,
	// but JPA does not provide an easy way to skip related items (lazy retrieval cannot be selectively and easily enforced).
	private static void cullTree(List<Process> tree) {
		for (Process process : tree) {
			if (process.getProcessSteps() != null) {
				for (ProcessStep processStep : process.getProcessSteps()) {
					for (BusinessObject bo : processStep.getUsedBusinessObjects()) {
						// Remove tables under BOs
						bo.setTables(null);
						// Remove all field usages
						for (TableUsage tableUsage : bo.getUsagesInternal()) {
							tableUsage.setFieldUsages(null);
						}
					}
				}
			}
			// Recursively apply to subprocesses
			if (process.getChildProcesses() != null) {
				cullTree(process.getChildProcesses());
			}
		}
	}
	
	// Creates a process under the given parent. A parent of NULL means the new process is at the top level (no parent). 
	@POST
	@Path("/process")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Process createProcess(@Context HttpServletRequest request,Process newProcess) {
		final String METHOD_NAME = "createProcess(Process newProcess)";
		logger.entering(CLASS_NAME, METHOD_NAME);
		Principal user = request.getUserPrincipal();
		boolean hasSuperUserRole = request.isUserInRole(Constants.BDR_SUPERUSER_ROLE);
		
		CWAppLogger.log(Level.FINE,"CWApp_Entering_Meth",new Object[]{METHOD_NAME});
		// infuse the current date before saving the new entity
		newProcess.setUpdated(new Date());

		try {
			transaction.begin();
			manager.joinTransaction();
			manager.persist(newProcess);
			if(!hasSuperUserRole)
			{
				ProcessStep newProcessStep=new ProcessStep();
				newProcessStep.setParentProcess(newProcess);
				newProcessStep.setName("dummy");
				newProcessStep.setUpdated(new Date());
				ArrayList<UserRegistryObject> allowed = new ArrayList<UserRegistryObject>();
				UserRegistryObject uro = Util.getUserFromWAS(user.getName());
				allowed.add(uro);
				newProcessStep.setAllowed(allowed);
				manager.persist(newProcessStep);
			}
			
			transaction.commit();

		} catch (NotSupportedException e) {
			Util.throwInternalErrorToClient(e);
		} catch (SystemException e) {
			Util.throwInternalErrorToClient(e);
		} catch (RollbackException e) {
			Util.throwInternalErrorToClient(e);
		} catch (HeuristicMixedException e) {
			Util.throwInternalErrorToClient(e);
		} catch (HeuristicRollbackException e) {
			Util.throwInternalErrorToClient(e);
		}
		logger.exiting(CLASS_NAME, METHOD_NAME);
		return newProcess;
	}

	@POST
	@Path("/processStep")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public ProcessStep createProcessStep(@Context HttpServletRequest request, ProcessStep newProcessStep) {
		final String METHOD_NAME = "createProcessStep(ProcessStep newProcessStep)";
		logger.entering(CLASS_NAME, METHOD_NAME);
		CWAppLogger.log(Level.FINE,"CWApp_Entering_Meth",new Object[]{METHOD_NAME});

		Principal user = request.getUserPrincipal();
		boolean hasSuperUserRole = request.isUserInRole(Constants.BDR_SUPERUSER_ROLE);
		
		// if the business object is created by a non-superuser we add him to the list of allowed objects
		if (!hasSuperUserRole) {
			ArrayList<UserRegistryObject> allowed = new ArrayList<UserRegistryObject>();
			UserRegistryObject uro = Util.getUserFromWAS(user.getName());
			allowed.add(uro);

			newProcessStep.setAllowed(allowed);
		}
		
		// infuse the current date before saving the new entity
		newProcessStep.setUpdated(new Date());

		try {
			transaction.begin();
			manager.joinTransaction();
			manager.persist(newProcessStep);
			transaction.commit();

		} catch (Exception e) {
			Util.throwInternalErrorToClient(e);
		}
		logger.exiting(CLASS_NAME, METHOD_NAME);
		return newProcessStep;
	}
	
	@GET
	@Path("/process/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Process getProcess(@PathParam("id") int id) {
		final String METHOD_NAME = "getProcess(int id)";
		logger.entering(CLASS_NAME, METHOD_NAME);
		
		// Find process in DB
		Process process = manager.find(Process.class, Integer.valueOf(id));
		
		// Copy a flat hierarchy in a new object without subprocesses to save time and
		// memory when delivering this object to the client
		Process procWithoutChildren = null;
		if(process != null) {
			procWithoutChildren = new Process();
			procWithoutChildren.setApprovalStatus(process.getApprovalStatus());
			procWithoutChildren.setDescription(process.getDescription());
			procWithoutChildren.setFieldUsageStatus(process.getFieldUsageStatus());
			procWithoutChildren.setName(process.getName());
			procWithoutChildren.setProcessId(process.getProcessId());
			procWithoutChildren.setType(process.getType());
			procWithoutChildren.setUpdated(process.getUpdated());
		}
		logger.exiting(CLASS_NAME, METHOD_NAME);
		return procWithoutChildren;
	}
	
	@GET
	@Path("/processStep/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public ProcessStep getProcessStep(@PathParam("id") int processStepId) {
		final String METHOD_NAME = "getProcessStep(int id)";
		logger.entering(CLASS_NAME, METHOD_NAME);
		
		// Find step in DB
		ProcessStep step = manager.find(ProcessStep.class, Integer.valueOf(processStepId));
		
		// Copy a flat hierarchy in a new object without subobjects to save time and
		// memory when delivering this object to the client
		ProcessStep stepWithoutChildren = null;
		if(step != null) {
			stepWithoutChildren = new ProcessStep();
			stepWithoutChildren.setAllowed(step.getAllowed());
			stepWithoutChildren.setApprovalStatus(step.getApprovalStatus());
			stepWithoutChildren.setDescription(step.getDescription());
			stepWithoutChildren.setFieldUsageStatus(step.getFieldUsageStatus());
			stepWithoutChildren.setName(step.getName());
			stepWithoutChildren.setProcessStepId(step.getProcessStepId());
			stepWithoutChildren.setTransactions(step.getTransactions());
			stepWithoutChildren.setType(step.getType());
			stepWithoutChildren.setUpdated(step.getUpdated());
		}
		logger.exiting(CLASS_NAME, METHOD_NAME);
		return stepWithoutChildren;
	}

	@PUT
	@Path("/process/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Process updateProcess(@PathParam("id") int id, Process newProcess) {
		final String METHOD_NAME = "updateProcess(int id, Process newProcess)";
		logger.entering(CLASS_NAME, METHOD_NAME);
		
		Process p = null;

		try {
			transaction.begin();
			manager.joinTransaction();
			p = manager.find(Process.class, id);
			if (p == null) {
				Util.throwNotFoundErrorToClient(id);
			}
			p.setName(newProcess.getName());
			p.setDescription(newProcess.getDescription());
			p.setUpdated(new Date());

			manager.merge(p);
			transaction.commit();
		} catch (Exception e) {
			Util.throwInternalErrorToClient(e);
		}
		logger.exiting(CLASS_NAME, METHOD_NAME);
		
		return p;
	}

	// This is called:
	// - When the details panel saves changes
	// - When a BO is attached/detached
	@PUT
	@Path("/processStep/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public ProcessStep updateProcessStep(@PathParam("id") int id, ProcessStep newProcessStep) {
		final String METHOD_NAME = "updateProcessStep(int id, ProcessStep newProcessStep)";
		logger.entering(CLASS_NAME, METHOD_NAME);
		logger.finer("Updating process step: " + newProcessStep.getName());
		
		ProcessStep processStep = null;
		boolean nameChanged = false;

		try {
			joinTransaction();
			processStep = manager.find(ProcessStep.class, id);

			if (processStep == null) {
				Util.throwNotFoundErrorToClient(id);
			}
			
			// Remove orphan table usage objects:
			// Since a BO may have been detached from the process step,
			// check every table usage if its used in any of the remaining BOs
			// and discard any orphans
			logger.finer("Number of attached table usage objects before this update: " + processStep.getUsages().size());
			
			Iterator<TableUsage> tuIterator = processStep.getUsages().iterator();
			
			while (tuIterator.hasNext()) {
				TableUsage tu = tuIterator.next();
				
				boolean inUse = false;
				
				for (BusinessObject bo : newProcessStep.getUsedBusinessObjects()) {
					logger.finer("This process step has the following BO attached to it:" + bo.getName());
					
					if (bo.getUsagesInternal().contains(tu)) { //173166 use the Internal version
						logger.finer("Table usage still in good standing: " + tu.getTable().getName());
						inUse = true;
						break;
					}
				}
				if (!inUse) {
					logger.finer("Removing orphaned table usage: " + tu.getTable().getName());
					tuIterator.remove();
					manager.remove(tu);
				}
			}
			
			
			if (!processStep.getName().equals(newProcessStep.getName())) {
				nameChanged = true;
			}
			
			// Process BO list
			// 1. Remove deleted ones
			// Since we're working with shortened/incomplete BO objects,
			// we need to make sure to keep the existing BO objects that are still attached
			logger.finer("Removing deleted BOs (if any)...");
			Iterator<BusinessObject> boIter = processStep.getUsedBusinessObjects().iterator();
			while (boIter.hasNext()) {
				BusinessObject bo = boIter.next();
				if (!newProcessStep.getUsedBusinessObjects().contains(bo)) {
					logger.finer("Removing BO: " + bo.getName());
					boIter.remove();
				}
			}
			// 2. Add new ones
			logger.finer("Adding new BOs (if any)...");
			boIter = newProcessStep.getUsedBusinessObjects().iterator();
			while (boIter.hasNext()) {
				BusinessObject bo = boIter.next();
				if (!processStep.getUsedBusinessObjects().contains(bo)) {
					logger.finer("Adding BO: " + bo.getName());
					processStep.getUsedBusinessObjects().add(bo);
				}
			}			
			
			processStep.setName(newProcessStep.getName());
			processStep.setDescription(newProcessStep.getDescription());
			processStep.setTransactions(newProcessStep.getTransactions());
			processStep.setAllowed(newProcessStep.getAllowed());
			
			// Save and reload the object to get the complete child objects back
			manager.merge(processStep);
			processStep = manager.find(ProcessStep.class, id);
			
			// Create table usage objects
			logger.finer("Creating table usage objects...");
			CWAppLogger.log(Level.FINE,"CWApp_10007",null);
			for (BusinessObject bo : processStep.getUsedBusinessObjects()) {
				logger.finer("Checking BO: " + bo.getName());
				for (Table table : bo.getTables()) {
					logger.finer("Checking table: " + table.getName());
					if (processStep.getUsage(bo, table) == null) {
						// New BO/Table combination, create Usage object
						TableUsage tableUsage = new TableUsage(processStep, bo, table);
						logger.finer("Persisting new table usage object.");
						manager.persist(tableUsage);
						processStep.getUsages().add(tableUsage);
					} else if (nameChanged) {
						TableUsage tableUsage = bo.getUsage(processStep, table);
						tableUsage.updateNames();
						manager.persist(tableUsage);
						logger.finer("Updated existing table usage object.");
					}
				}
			}

			processStep.setUpdated(new Date());
			
			manager.merge(processStep);
			transaction.commit();
		} catch (Exception e) {
			Util.throwInternalErrorToClient(e);
		}
		
		logger.exiting(CLASS_NAME, METHOD_NAME);

		return processStep;
	}
	
	@DELETE
	@Path("/process/{id}")
	public void deleteProcess(@PathParam("id") int id) {
		final String METHOD_NAME = "deleteProcess(int id)";
		logger.entering(CLASS_NAME, METHOD_NAME);
		DBOperations.jpaDeleteItem(id, ItemType.PROCESS);
		logger.exiting(CLASS_NAME, METHOD_NAME);
	}

	@DELETE
	@Path("/processStep/{id}")
	public void deleteProcessStep(@PathParam("id") int id) {
		final String METHOD_NAME = "deleteProcessStep(int id)";
		logger.entering(CLASS_NAME, METHOD_NAME);
		DBOperations.jpaDeleteItem(id, ItemType.PROCESS_STEP);
		logger.exiting(CLASS_NAME, METHOD_NAME);
	}
	
	private void iterateSubprocesses(Process process, boolean withAcl) {
		if (withAcl) {
			for (Process subprocess : process.getAclChildProcesses()) {
				iterateSubprocesses(subprocess, withAcl);
			}
		}
		else {
			for (Process subprocess : process.getChildProcesses()) {
				iterateSubprocesses(subprocess, withAcl);
			}
		}
		
		for (ProcessStep processStep : process.getProcessSteps()) {
			filterTableUsages(processStep);
//			accumulateFieldUsageStatus(process, true, withAcl);
//			accumulateApprovalStatus(process, true, withAcl);
		}
	}
	
	// Replaces all BO objects under the given process step with modified versions so that only
	// the table usage objects that connect this particular process step and business object
	// are linked at this location
	private void filterTableUsages (ProcessStep processStep) {
		List<BusinessObject> replacementBoList = new ArrayList<BusinessObject>();
		Iterator<BusinessObject> boIter = processStep.getUsedBusinessObjects().iterator();
		while(boIter.hasNext()) {
			BusinessObject bo = boIter.next();
			BusinessObject boClone = new BusinessObject();
			boClone.setBusinessObjectId(bo.getBusinessObjectId());
			boClone.updateFrom(bo);
			
			boClone.exposeTableUsages = true; // 173166
			
			// --- 173166 alternate implementation, preformance improvement not certain yet
			TypedQuery<TableUsage> tuQuery = manager.createNamedQuery("TableUsage.getAllForBoAndStep", TableUsage.class);
			tuQuery.setParameter("processStepId", processStep.getProcessStepId());
			tuQuery.setParameter("businessObjectId", bo.getBusinessObjectId());
			List<TableUsage> specificTableUsages = tuQuery.getResultList();
			boClone.setUsages(specificTableUsages);
			
//			for (TableUsage tu : boClone.getUsages()) {
//				accumulateFieldUsageStatus(tu);
//			}

			// 173166 this is the original code block replaced by the new version above			
//			boClone.setUsages(new ArrayList<TableUsage>());
//			bo.exposeTableUsages = true;
//			Iterator<TableUsage> iter = bo.getUsages().iterator();
//			while(iter.hasNext()) {
//				TableUsage tu = iter.next();
////				accumulateFieldUsageStatus(tu);
//				if (tu.getProcessStep().equals(processStep)) {
//					boClone.getUsages().add(tu);
//				}
//			}
			// 173166 end

//			accumulateFieldUsageStatus(boClone);
//			accumulateApprovalStatus(boClone);
			
			replacementBoList.add(boClone);
		}
		
		processStep.setUsages(new ArrayList<TableUsage>());
		processStep.setUsedBusinessObjects(replacementBoList);
		
//		accumulateFieldUsageStatus(processStep);
//		accumulateApprovalStatus(processStep);
	}
	
	private void joinTransaction() throws SystemException, NotSupportedException {
		
		// required as the process object (or one if its descendants gets modified)
		if (transaction.getStatus() == Status.STATUS_NO_TRANSACTION) {
			transaction.begin();
		}

		manager.joinTransaction();
	}

	@GET
	@Path("/process/{copiedItem}/{parentItem}")
	//@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Integer copyProcess(@Context HttpServletRequest request,@PathParam("copiedItem") String copiedItem, @PathParam("parentItem") String parentItem) {
		final String METHOD_NAME = "pasteProcess(String copiedItem, String parentItem)";
		logger.entering(CLASS_NAME, METHOD_NAME);
		Process parentProcess;
		
		try {
			joinTransaction();
			String[] copiedItems=copiedItem.split("~");
			String[] parentItems=parentItem.split("~");
			if(!copiedItems[0].trim().equalsIgnoreCase(Constants.BDR_EXPORT_PROCESS_NAME))
			{
				ProcessStep copiedProcessStep = manager.find(ProcessStep.class, Integer.valueOf(copiedItems[1]));
				parentProcess = manager.find(Process.class, Integer.valueOf(parentItems[1]));
				ProcessStep newProcessStep= new ProcessStep();
				newProcessStep.setParentProcess(parentProcess);
				newProcessStep.setName(copiedProcessStep.getName());
				newProcessStep.setTransactions(copiedProcessStep.getTransactions());
				newProcessStep.setAllowed(copiedProcessStep.getAllowed());
				manager.persist(newProcessStep);
				createBO(newProcessStep, copiedProcessStep);
				transaction.commit();
				return 1;
			}
			
			if(!"none".equals(parentItem)){
				parentProcess = manager.find(Process.class, Integer.valueOf(parentItems[1]));
			}else
				parentProcess=null;
			Process copiedProcess = manager.find(Process.class, Integer.valueOf(copiedItems[1]));
			createProcess(copiedProcess, parentProcess);
			transaction.commit();
		} catch (Exception e) {
			Util.throwInternalErrorToClient(e);
			return 0;
		}
		logger.exiting(CLASS_NAME, METHOD_NAME);
		return 1;
		
	}

	private void createProcess(Process copiedProcess, Process parentProcess) throws Exception
	{
		Process newProcess=new Process();
		newProcess.setParentProcess(parentProcess);
		newProcess.setName(copiedProcess.getName());
		newProcess.setDescription(copiedProcess.getDescription());
		newProcess.setUpdated(new Date());
		manager.persist(newProcess);
		List<Process> childP=copiedProcess.getChildProcesses();
		if(childP!=null && childP.size()>0)
			for(Process ps:childP)
			{
				createProcess(ps,newProcess);
				
			}
		createProcessStep(newProcess, copiedProcess);
	}
	
	private void createProcessStep(Process newProcess, Process copiedProcess)
	{
		
		List<ProcessStep> copiedProcessSteps=(List<ProcessStep>)copiedProcess.getProcessSteps();
		if(copiedProcessSteps!=null && copiedProcessSteps.size()>0)
			for(ProcessStep copiedProcessStep:copiedProcessSteps)
			{
				ProcessStep newProcessStep= new ProcessStep();
				newProcessStep.setParentProcess(newProcess);
				newProcessStep.setName(copiedProcessStep.getName());
				newProcessStep.setTransactions(copiedProcessStep.getTransactions());
				newProcessStep.setAllowed(copiedProcessStep.getAllowed());
				manager.persist(newProcessStep);
				createBO(newProcessStep, copiedProcessStep);
			}
	}
	
	private void createBO(ProcessStep newProcessStep,ProcessStep copiedProcessStep)
	{
		copiedProcessStep = manager.find(ProcessStep.class, copiedProcessStep.getProcessStepId());
		Iterator<BusinessObject> boIter = copiedProcessStep.getUsedBusinessObjects().iterator();
		while (boIter.hasNext()) {
			BusinessObject bo = boIter.next();
			if (!newProcessStep.getUsedBusinessObjects().contains(bo)) {
				logger.finer("Adding BO: " + bo.getName());
				System.out.println("BO: " + bo.getName());
				newProcessStep.getUsedBusinessObjects().add(bo);
				manager.persist(newProcessStep);

				// Create table usage objects
				logger.finer("Creating table usage objects...");
				logger.finer("Checking BO: " + bo.getName());
				System.out.println("Checking BO: " + bo.getName());
				for (Table table : bo.getTables()) {
					System.out.println("Copying Tables:"+table.getName());
					TableUsage tableUsage=copiedProcessStep.getUsage(bo, table);
					TableUsage newTableUsage = new TableUsage();
					newTableUsage.setApprovalStatus(tableUsage.getApprovalStatus());
					newTableUsage.setBusinessObject(bo);
					newTableUsage.setFieldUsageStatus(tableUsage.getFieldUsageStatus());
					newTableUsage.setFullName(tableUsage.getFullName());
					newTableUsage.setName(tableUsage.getName());
					newTableUsage.setProcessStep(newProcessStep);
					newTableUsage.setTable(tableUsage.getTable());
					newTableUsage.setType(tableUsage.getType());
					newTableUsage.setUpdated(new Date());
					manager.persist(newTableUsage);
					for(FieldUsage fieldUsage: tableUsage.getFieldUsages()){
						FieldUsage newFieldUsage= new FieldUsage();
						newFieldUsage.setCheckTable(fieldUsage.getCheckTable());
						newFieldUsage.setComment(fieldUsage.getComment());
						newFieldUsage.setDescription(fieldUsage.getDescription());
						newFieldUsage.setField(fieldUsage.getField());
						newFieldUsage.setGlobalTemplate(fieldUsage.getGlobalTemplate());
						newFieldUsage.setName(fieldUsage.getName());
						newFieldUsage.setRecommended(fieldUsage.getRecommended());
						newFieldUsage.setRequired(fieldUsage.getRequired());
						newFieldUsage.setSapView(fieldUsage.getSapView());
						newFieldUsage.setStatus(fieldUsage.getStatus());
						newFieldUsage.setTableUsage(newTableUsage);
						newFieldUsage.setUseMode(fieldUsage.getUseMode());
						manager.persist(newFieldUsage);

					}


				}
			}
		}
		
		manager.merge(newProcessStep);
		newProcessStep = manager.find(ProcessStep.class, newProcessStep.getProcessStepId());
		
		newProcessStep.setUpdated(new Date());
		manager.merge(newProcessStep);
	}
	
	
	@GET
	@Path("/process/moveToGT/{parentItem}")
	@Produces(MediaType.APPLICATION_JSON)
	public Integer moveToGTProcess(@PathParam("parentItem") String parentItem) {
		int rowsUpdated=DBOperations.moveToGT();
		System.out.println("number of rows updated="+rowsUpdated);
		return rowsUpdated;
	}
	
	
//	private void accumulateApprovalStatus(BusinessObject bo) {
//		ApprovalStatus currentStatus = ApprovalStatus.UNDEFINED;
//		
//		for (TableUsage tu : bo.getUsagesInternal()) {   //173166 use the internal version
//			if (currentStatus != ApprovalStatus.UNDEFINED) {
//				if (currentStatus != tu.getApprovalStatus()) {
//					currentStatus = ApprovalStatus.MIXED;
//					break;
//				}
//			}
//			else {
//				currentStatus = tu.getApprovalStatus();
//			}
//		}
//		
//		bo.setApprovalStatus(currentStatus);
//   }
//	
//	private void accumulateApprovalStatus(ProcessStep ps) {
//		ApprovalStatus currentStatus = ApprovalStatus.UNDEFINED;
//		
//		for (BusinessObject bo : ps.getUsedBusinessObjects()) {
//			if (currentStatus != ApprovalStatus.UNDEFINED) {
//				if (currentStatus == ApprovalStatus.MIXED) {
//					break;
//				}
//				if (currentStatus != bo.getApprovalStatus()) {
//					currentStatus = ApprovalStatus.MIXED;
//					break;
//				}
//			}
//			else {
//				currentStatus = bo.getApprovalStatus();
//			}
//		}
//		
//		ps.setApprovalStatus(currentStatus);
//	}
	
//	private void accumulateApprovalStatus(Process p, boolean downwards, boolean withAcl) {
//		if (downwards) {
//			ApprovalStatus currentStatus = ApprovalStatus.UNDEFINED;
//			
//			for (ProcessStep ps : p.getProcessSteps()) {
//				if (currentStatus != ApprovalStatus.UNDEFINED) {
//					if (currentStatus == ApprovalStatus.MIXED) {
//						break;
//					}
//					if (currentStatus != ps.getApprovalStatus()) {
//						currentStatus = ApprovalStatus.MIXED;
//						break;
//					}
//				}
//				else {
//					currentStatus = ps.getApprovalStatus();
//				}
//			}
//			
//			p.setApprovalStatus(currentStatus);
//		}
//		else {
//			Process parentProcess = p.getParentProcess();
//			
//			if (parentProcess != null) {
//				ApprovalStatus currentStatus = ApprovalStatus.UNDEFINED;
//				Collection<Process> childProcesses = null;
//				
//				if (withAcl) {
//					childProcesses = parentProcess.getAclChildProcesses();
//				}
//				else {
//					childProcesses = parentProcess.getChildProcesses();
//				}
//				
//				for (Process childProcess : childProcesses) {
//					if (currentStatus != ApprovalStatus.UNDEFINED) {
//						if (currentStatus == ApprovalStatus.MIXED) {
//							break;
//						}
//						if (currentStatus != childProcess.getApprovalStatus()) {
//							currentStatus = ApprovalStatus.MIXED;
//							break;
//						}
//					}
//					else {
//						currentStatus = childProcess.getApprovalStatus();
//					}
//				}
//				
//				parentProcess.setApprovalStatus(currentStatus);
//				accumulateApprovalStatus(parentProcess, downwards, withAcl);
//			}
//		}
//	}
	
//	private void accumulateApprovalStatus(Collection<Process> tree, Process parentProcess, boolean withAcl) {
//		if (tree != null && !tree.isEmpty()) {
//			for (Process process : tree) {
//				if (withAcl) {
//					accumulateApprovalStatus(process.getAclChildProcesses(), process, withAcl);
//				}
//				else {
//					accumulateApprovalStatus(process.getChildProcesses(), process, withAcl);
//				}
//			}
//		}
//		else {
//			if (parentProcess != null) {
//				accumulateApprovalStatus(parentProcess, false, withAcl);
//			}
//		}
//	}
	
//	private void accumulateFieldUsageStatus(TableUsage tu) {
//		FieldUsageStatus currentStatus = FieldUsageStatus.OK;
//		FieldUsageService fuService = new FieldUsageService();
//
//		for (FieldUsage fu : tu.getFieldUsages()) {
//			FieldUsage mergedFu = fuService.mergeFieldUsageWithStatus(fu, tu.getTableUsageId());
//			
//			if (currentStatus != FieldUsageStatus.OK) {
//				if (FieldUsageStatus.toStatusCode(mergedFu.getStatus()) > FieldUsageStatus.toStatusCode(currentStatus)) {
//					currentStatus = mergedFu.getStatus();
//				}
//			}
//			else {
//				currentStatus = mergedFu.getStatus();
//			}
//		}
//		
//		tu.setFieldUsageStatus(currentStatus);
//	}
//	
//	private void accumulateFieldUsageStatus(BusinessObject bo) {
//		FieldUsageStatus currentStatus = FieldUsageStatus.OK;
//
//		for (TableUsage tu : bo.getUsagesInternal()) { //173166 use the internal version
//			if (currentStatus != FieldUsageStatus.OK) {
//				if (FieldUsageStatus.toStatusCode(tu.getFieldUsageStatus()) > FieldUsageStatus.toStatusCode(currentStatus)) {
//					currentStatus = tu.getFieldUsageStatus();
//				}
//			}
//			else {
//				currentStatus = tu.getFieldUsageStatus();
//			}
//		}
//		
//		bo.setFieldUsageStatus(currentStatus);
//	}
//
//	private void accumulateFieldUsageStatus(ProcessStep ps) {
//		FieldUsageStatus currentStatus = FieldUsageStatus.OK;
//
//		for (BusinessObject bo : ps.getUsedBusinessObjects()) {
//			if (currentStatus != FieldUsageStatus.OK) {
//				if (FieldUsageStatus.toStatusCode(bo.getFieldUsageStatus()) > FieldUsageStatus.toStatusCode(currentStatus)) {
//					currentStatus = bo.getFieldUsageStatus();
//				}
//			}
//			else {
//				currentStatus = bo.getFieldUsageStatus();
//			}
//		}
//		
//		ps.setFieldUsageStatus(currentStatus);
//	}
	
//	private void accumulateFieldUsageStatus(Process p, boolean downwards, boolean withAcl) {
//		if (downwards) {
//			FieldUsageStatus currentStatus = FieldUsageStatus.OK;
//			
//			for (ProcessStep ps : p.getProcessSteps()) {
//				if (currentStatus != FieldUsageStatus.OK) {
//					if (FieldUsageStatus.toStatusCode(ps.getFieldUsageStatus()) > FieldUsageStatus.toStatusCode(currentStatus)) {
//						currentStatus = ps.getFieldUsageStatus();
//					}
//				}
//				else {
//					currentStatus = ps.getFieldUsageStatus();
//				}
//			}
//			
//			p.setFieldUsageStatus(currentStatus);
//		}
//		else {
//			Process parentProcess = p.getParentProcess();
//			
//			if (parentProcess != null) {
//				FieldUsageStatus currentStatus = FieldUsageStatus.OK;
//				Collection<Process> childProcesses = null;
//				
//				if (withAcl) {
//					childProcesses = parentProcess.getAclChildProcesses();
//				}
//				else {
//					childProcesses = parentProcess.getChildProcesses();
//				}
//				
//				for (Process childProcess : childProcesses) {
//					if (currentStatus != FieldUsageStatus.OK) {
//						if (FieldUsageStatus.toStatusCode(childProcess.getFieldUsageStatus()) > FieldUsageStatus.toStatusCode(currentStatus)) {
//							currentStatus = childProcess.getFieldUsageStatus();
//						}
//					}
//					else {
//						currentStatus = childProcess.getFieldUsageStatus();
//					}
//				}
//				
//				parentProcess.setFieldUsageStatus(currentStatus);
//				accumulateFieldUsageStatus(parentProcess, downwards, withAcl);
//			}
//		}
//	}
	
//	private void accumulateFieldUsageStatus(Collection<Process> tree, Process parentProcess, boolean withAcl) {
//		if (tree != null && !tree.isEmpty()) {
//			for (Process process : tree) {
//				if (withAcl) {
//					accumulateFieldUsageStatus(process.getAclChildProcesses(), process, withAcl);
//				}
//				else {
//					accumulateFieldUsageStatus(process.getChildProcesses(), process, withAcl);
//				}
//			}
//		}
//		else {
//			if (parentProcess != null) {
//				accumulateFieldUsageStatus(parentProcess, false, withAcl);
//			}
//		}
//	}
}
