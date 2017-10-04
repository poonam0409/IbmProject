package com.ibm.is.sappack.cw.app.services.bdr.jaxrs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
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
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import com.ibm.is.sappack.cw.app.data.JPAResourceFactory;
import com.ibm.is.sappack.cw.app.data.Resources;
import com.ibm.is.sappack.cw.app.data.bdr.IPathElement;
import com.ibm.is.sappack.cw.app.data.bdr.ParentPathElement;
import com.ibm.is.sappack.cw.app.data.bdr.PathElement;
import com.ibm.is.sappack.cw.app.data.bdr.jpa.BusinessObject;
import com.ibm.is.sappack.cw.app.data.bdr.jpa.Process;
import com.ibm.is.sappack.cw.app.data.bdr.jpa.ProcessStep;
import com.ibm.is.sappack.cw.app.data.bdr.jpa.Table;
import com.ibm.is.sappack.cw.app.data.bdr.jpa.TableUsage;
import com.ibm.is.sappack.cw.app.services.CwApp;
import com.ibm.is.sappack.cw.app.services.Util;
import com.ibm.is.sappack.cw.app.services.logging.CWAppLogger;

@Path("/bdr/businessobjects")
public class BusinessObjectService {

	private static final String CLASS_NAME = BusinessObjectService.class.getName();
	private final Logger logger;
	
	private static final String JPA_QUERY_BONAME_VALUE = "nameValue";
	private static final String JPA_QUERY_BOID_VALUE = "businessObjectIdValue";
	
//	private static final String ORDER_BY_NAME_ASC = "(+name)";
//	private static final String ORDER_BY_NAME_DESC = "(-name)";

	private final EntityManager manager;
	private final UserTransaction transaction;

	public BusinessObjectService() {
		this.logger = CwApp.getLogger();
		this.manager = JPAResourceFactory.getEntityManager();
		this.transaction = JPAResourceFactory.getUserTransaction();
	}
	
	@GET
	@Path("isUsed/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public boolean isBusinessObjectUsed(@PathParam("id") int businessObjectId) {
		BusinessObject bo = manager.find(BusinessObject.class, Integer.valueOf(businessObjectId));
		
		if (bo != null) {
			if (bo.getUsedInProcessSteps() != null && bo.getUsedInProcessSteps().size() > 0) {
				return true;
			}
		}
		
		return false;
	}

	@GET
	@Path("pathsFor/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<IPathElement> getPathsForBusinessObject(@PathParam("id") int businessObjectId) {
		ArrayList<IPathElement> paths = new ArrayList<IPathElement>();

	   	BusinessObject bo = manager.find(BusinessObject.class, Integer.valueOf(businessObjectId));
	   	
	   	if (bo != null) {
	   		PathElement boPathElement = new PathElement();
	   		boPathElement.setId(bo.getBusinessObjectId());
	   		boPathElement.setName(bo.getName());
	   		boPathElement.setType(Resources.BPH_TYPE_BUSINESSOBJECT);
	   		
	   		Collection<ProcessStep> usedInProcessSteps = bo.getUsedInProcessSteps();
	   		Iterator<ProcessStep> it = usedInProcessSteps.iterator();
	   		
	   		while (it.hasNext()) {
	   			ProcessStep ps = it.next();
	   			
	   			if (ps != null) {
	   				ParentPathElement psPathElement = new ParentPathElement();
	   				psPathElement.setId(ps.getProcessStepId());
	   				psPathElement.setName(ps.getName());
	   				psPathElement.setType(Resources.BPH_TYPE_PROCESSSTEP);
	   				psPathElement.addChild(boPathElement);
	   				
	   				Process parent = ps.getParentProcess();
	   				Util.iterateParentProcess(parent, psPathElement, paths, false);
	   			}
	   		}
	   	}
	   	
	   	// Optimize tree arrangement
	   	Util.filterPaths(paths);
   	
		return paths;
	}
	
	@GET
	@Path("getAllBusinessObjects")
	@Produces(MediaType.APPLICATION_JSON)
	public List<BusinessObject> getAllBusinessObjects(@Context UriInfo ui) {
		String businessObjectIdParameter = null;
		String nameFilter = null;
		CWAppLogger.log(Level.FINE,"CWApp_Entering_Meth",new Object[]{"getAllBusinessObjects()"});
		// dynamically build the JPA query
		CriteriaBuilder cb = this.manager.getCriteriaBuilder();
		CriteriaQuery<BusinessObject> preQuery = cb.createQuery(BusinessObject.class);
		Root<BusinessObject> bo = preQuery.from(BusinessObject.class);
		preQuery.select(bo);
		
		// expand the basic query defined above based on possible sort
		// and filtering parameters	
		// first we retrieve all query parameters and look for 'name'
		MultivaluedMap<String, String> queryParams = ui.getQueryParameters();
		for (Entry<String, List<String>> entry : queryParams.entrySet()) {
			Predicate businessObjectIdClause = null;
			
			if (Pattern.matches("businessObjectId.*", entry.getKey())) {
				
				// add a WHERE condition to the predefined query
				Expression<String> businessObjectIdPath = bo.get("businessObjectId");
				Expression<Integer> businessObjectIdValue = cb.parameter(Integer.class, JPA_QUERY_BOID_VALUE);
				businessObjectIdClause = cb.equal(businessObjectIdPath, businessObjectIdValue);
				
				preQuery.where(businessObjectIdClause);
				
				// remember the business object id query parameter
				businessObjectIdParameter = entry.getValue().get(0);
			}

			if (Pattern.matches("name.*", entry.getKey())) {
				
				// add a WHERE condition to the predefined query
				Expression<String> namePath = bo.get("name");
				Expression<String> nameValue = cb.parameter(String.class, JPA_QUERY_BONAME_VALUE);
				Predicate filterClause = cb.like(cb.lower(namePath), nameValue);
				
				// we need to honor a possible WHERE clause for the business object id
				if (businessObjectIdClause != null) {

					// combine the businessObjectId WHERE clause and the filtering WHERE clause
					Predicate combinedClause = cb.and(businessObjectIdClause, filterClause);
					preQuery.where(combinedClause);
				}
				else {
					
					// set the filtering WHERE clause
					preQuery.where(filterClause);
				}
				
				// remember the filter string
				nameFilter = entry.getValue().get(0);
			}
			
			if (Pattern.matches("exclude.*", entry.getKey())) {
				ArrayList<Integer> excludedBusinessObjectIds = new ArrayList<Integer>();
				
				String[] excludedIdsAsString = entry.getValue().get(0).split(",");
				
				if (excludedIdsAsString.length != 0) {
					for (String idAsString : excludedIdsAsString) {
						excludedBusinessObjectIds.add(Integer.valueOf(idAsString));
					}
					
					// add a WHERE condition to the predefined query
					Expression<String> exclusionPath = bo.get("businessObjectId");
					Predicate exclusionClause = exclusionPath.in(excludedBusinessObjectIds);
					
					preQuery.where(exclusionClause.not());				
				}
			}
			
			// Always sort by name
			//if (Pattern.matches("sort.*", entry.getKey())) {
				// add corresponding order by clauses to the predefined query
				//if (entry.getKey().contains(ORDER_BY_NAME_ASC)) {
					Expression<String> orderByClause = bo.get("name");
					Order sortOrder = cb.asc(orderByClause);
					preQuery.orderBy(sortOrder);
				//}
//				if (entry.getKey().contains(ORDER_BY_NAME_DESC)) {
//					Expression<String> orderByClause = bo.get("name");
//					Order sortOrder = cb.desc(orderByClause);
//					preQuery.orderBy(sortOrder);
//				}
//			}
		}
		
		// instantiate the predefined query into an actual typed query
		TypedQuery<BusinessObject> query = manager.createQuery(preQuery);
		
		// now we need to fill in the actual query parameter values
		if (query != null) {
			Set<ParameterExpression<?>> params = preQuery.getParameters();
			Iterator<ParameterExpression<?>> it = params.iterator();
			
			// depending on the query construction above we have
			// to check whether the known parameters exist and need to be set
			while (it.hasNext()) {
				ParameterExpression<?> ex = it.next();
				
				// the business object id query parameter is not necessarily set so we definitely
				// need to check it here
				if (ex.getName().equalsIgnoreCase(JPA_QUERY_BOID_VALUE)) {
					query.setParameter(JPA_QUERY_BOID_VALUE, Integer.valueOf(businessObjectIdParameter));
				}

				// the filter string is not necessarily set so we definitely
				// need to check it here
				if (ex.getName().equalsIgnoreCase(JPA_QUERY_BONAME_VALUE)) {
					query.setParameter(JPA_QUERY_BONAME_VALUE,"%"+ nameFilter.toLowerCase()+"%");
				}
			}
			
			return query.getResultList();
		}
      
      return new ArrayList<BusinessObject>();
	}
	
	// Returns a flat hierarchy without subobjects to save time and
	// memory when delivering this object to the client
	@GET
	@Path("flatBusinessObject/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public BusinessObject getBusinessObjectWithoutChildren(@PathParam("id") int businessObjectId) {
		// Get BO from DB
		BusinessObject businessObject = manager.find(BusinessObject.class, Integer.valueOf(businessObjectId));
		// Make a flat copy
		BusinessObject bo = null;
		if(businessObject != null) {
			bo = new BusinessObject();
			bo.setApprovalStatus(businessObject.getApprovalStatus());
			bo.setBusinessObjectId(businessObject.getBusinessObjectId());
			bo.setDescription(businessObject.getDescription());
			bo.setFieldUsageStatus(businessObject.getFieldUsageStatus());
			bo.setName(businessObject.getName());
			bo.setShortName(businessObject.getShortName());
			bo.setType(businessObject.getType());
			bo.setUpdated(businessObject.getUpdated());
		}
		return bo;
	}
	
	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public BusinessObject getBusinessObject(@PathParam("id") int businessObjectId) {
		return manager.find(BusinessObject.class, Integer.valueOf(businessObjectId));
	}
	
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public BusinessObject createBusinessObject(BusinessObject bo) {
		
		// infuse the current date before saving the new entity
		bo.setUpdated(new Date());
		
		try {
			transaction.begin();
			manager.joinTransaction();
			manager.persist(bo);
			transaction.commit();
		}
		catch (Exception e) {
			Util.throwInternalErrorToClient(transaction, e);
		}

		return bo;
	}
	
	// Saves a flat object without touching the children


	@PUT
	@Path("{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public BusinessObject updateBusinessObjectWithoutChildren(@PathParam("id") int businessObjectId, BusinessObject newBo) {
		final String METHOD_NAME = "updateBusinessObjectWithoutChildren(int businessObjectId, BusinessObject newBo)";
		logger.entering(CLASS_NAME, METHOD_NAME);
		logger.finer("Updating business object: " + newBo.getName());
		
		BusinessObject bo = null;
		
		try {
			transaction.begin();
			manager.joinTransaction();
			bo = manager.find(BusinessObject.class, businessObjectId);
			
			if (bo == null) {
				Util.throwNotFoundErrorToClient(businessObjectId);
			}
			
			bo.updateFrom(newBo);
			bo.setUpdated(new Date());
			
			// Save and reload the object to get the complete child objects back
			manager.merge(bo);
			transaction.commit();
		}
		catch (Exception e) {
			Util.throwInternalErrorToClient(transaction, e);
		}
		
		logger.exiting(CLASS_NAME, METHOD_NAME);
		return bo;
	}
	
	@PUT
	@Path("updateBusinessObject/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public BusinessObject updateBusinessObject(@PathParam("id") int businessObjectId, BusinessObject newBo) {
		final String METHOD_NAME = "updateBusinessObject(int businessObjectId, BusinessObject newBo)";
		logger.entering(CLASS_NAME, METHOD_NAME);
		logger.finer("Updating business object: " + newBo.getName());
		CWAppLogger.log(Level.FINE,"CWApp_Entering_Meth",new Object[]{METHOD_NAME});
		BusinessObject bo = null;
		
		try {
			transaction.begin();
			manager.joinTransaction();
			bo = manager.find(BusinessObject.class, businessObjectId);
			
			if (bo == null) {
				Util.throwNotFoundErrorToClient(businessObjectId);
			}
			
			// Remove orphan table usage objects
			logger.finer("Number of attached tables: " + newBo.getTables().size());
			CWAppLogger.log(Level.FINE,"CWApp_10005",new Object[]{newBo.getTables().size()});
			Iterator<TableUsage> iter = bo.getUsagesInternal().iterator(); //173166 use the internal version
			while (iter.hasNext()) {
				TableUsage tu = iter.next();
				logger.finer("Checking existing table usage: " + tu.getTable().getName());
				if (!newBo.getTables().contains(tu.getTable())) {
					logger.finer("Removing orphaned table usage: " + tu.getTable().getName());
					iter.remove();
					manager.remove(tu);
				}
			}
			
			bo.updateFrom(newBo);
			
			bo.setTables(newBo.getTables());
			
			bo.setUpdated(new Date());
			
			// Save and reload the object to get the complete child objects back
			manager.merge(bo);
			transaction.commit();
			
			transaction.begin();
			manager.joinTransaction();
			
			bo = manager.find(BusinessObject.class, Integer.valueOf(businessObjectId));

			// Create/update table usage objects
			logger.finer("Creating table usage objects...");
			CWAppLogger.log(Level.FINE,"CWApp_10004",null);
			logger.finer("Number of attached tables: " + bo.getTables().size());
			logger.finer("Number of process steps this BO is used in: " + bo.getUsedInProcessSteps().size());
			for (ProcessStep processStep : bo.getUsedInProcessSteps()) {
				logger.finer("This BO is used in process step: " + processStep.getName());
				for (Table table : bo.getTables()) {
					if (bo.getUsage(processStep, table) == null) {
						// New BO/Table combination, create Usage object
						TableUsage tableUsage = new TableUsage(processStep, bo, table);
						logger.finer("Persisting new table usage object for table: " + table.getName());
						manager.persist(tableUsage);
						bo.getUsagesInternal().add(tableUsage); //173166 use the Internal version of the getusages method
					}
				}
			}
			
			logger.finer("Pre-merge: Number of attached tables: " + bo.getTables().size());
			logger.finer("Pre-merge: Number of process steps this BO is used in: " + bo.getUsedInProcessSteps().size());
			manager.merge(bo);
			transaction.commit();
		}
		catch (Exception e) {
			Util.throwInternalErrorToClient(transaction, e);
		}
		
		logger.exiting(CLASS_NAME, METHOD_NAME);
		return bo;
	}

	@DELETE
	@Path("deleteBusinessObject/{id}")
	public void deleteBusinessObject(@PathParam("id") int businessObjectId) {
		try {
			transaction.begin();
			manager.joinTransaction();
			BusinessObject b = manager.find(BusinessObject.class, Integer.valueOf(businessObjectId));
			if (b != null) {
				// Delete the table usages since they are only relevant for the BO
				for (TableUsage tu : b.getUsagesInternal()) { //173166 use the Internal version
					manager.remove(tu);
				}
				manager.remove(b);
			}
			transaction.commit();
		}
		catch (Exception e) {
			Util.throwInternalErrorToClient(transaction, e);
		}
	}
	
	protected int getNumberOfBusinessObjectsDefined() {
		TypedQuery<BusinessObject> query = manager.createNamedQuery("BusinessObject.getAll", BusinessObject.class);
		
		List<BusinessObject> businessObjects = query.getResultList();
		
		if (businessObjects != null) {
			return businessObjects.size();
		}
		
		return 0;
	}
}
