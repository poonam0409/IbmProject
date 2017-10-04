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
import javax.servlet.http.HttpServletRequest;
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

@Path("/bdr/tables")
public class TableService {

	private static final String CLASS_NAME = TableService.class.getName();

	private static final String JPA_QUERY_TABLENAME_VALUE = "nameValue";
	private static final String JPA_QUERY_TABLEID_VALUE = "tableIdValue";
	
	private static final String ORDER_BY_NAME_ASC = "(+name)";
	private static final String ORDER_BY_NAME_DESC = "(-name)";

	private final Logger logger;
	private final EntityManager manager;
	private final UserTransaction transaction;

	public TableService() {
		this.logger = CwApp.getLogger();
		final String METHOD_NAME = "TableService()";
		CWAppLogger.log(Level.FINE,"CWApp_Entering_Meth",new Object[]{METHOD_NAME});
		logger.entering(CLASS_NAME, METHOD_NAME);
		this.manager = JPAResourceFactory.getEntityManager();
		this.transaction = JPAResourceFactory.getUserTransaction();
		logger.exiting(CLASS_NAME, METHOD_NAME);
	}
	
	@GET
	@Path("isUsed/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public boolean isTableUsed(@PathParam("id") int tableId) {
		Table tab = manager.find(Table.class, Integer.valueOf(tableId));
		
		if (tab != null) {
			if (tab.getUsedInBusinessObjects() != null && tab.getUsedInBusinessObjects().size() > 0) {
				return true;
			}
		}
		
		return false;
	}
	
	@GET
	@Path("pathsFor/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<IPathElement> getPathsForTable(@PathParam("id") int tableId) {
		ArrayList<IPathElement> paths = new ArrayList<IPathElement>();

	   	Table tab = manager.find(Table.class, Integer.valueOf(tableId));
	   	
	   	if (tab != null) {
	   		PathElement tabPathElement = new PathElement();
	   		tabPathElement.setId(tab.getTableId());
	   		tabPathElement.setName(tab.getName());
	   		tabPathElement.setType(Resources.BPH_TYPE_TABLE);
	   		
	   		Collection<BusinessObject> usedInBusinessObjects = tab.getUsedInBusinessObjects();
	   		Iterator<BusinessObject> boit = usedInBusinessObjects.iterator();
	   		
	   		while (boit.hasNext()) {
	   			BusinessObject bo = boit.next();
	   			
	   			if (bo != null) {
	   				ParentPathElement boPathElement = new ParentPathElement();
	   				boPathElement.setId(bo.getBusinessObjectId());
	   				boPathElement.setName(bo.getName());
	   				boPathElement.setType(Resources.BPH_TYPE_BUSINESSOBJECT);
	   				boPathElement.addChild(tabPathElement);
	   				
	   				Collection<ProcessStep> usedInProcessSteps = bo.getUsedInProcessSteps();
	   				Iterator<ProcessStep> psit = usedInProcessSteps.iterator();
	   				
	   				while (psit.hasNext()) {
	   					ProcessStep ps = psit.next();
	   					
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
	   		}
	   	}
	   	
	   	// Optimize tree arrangement
	   	Util.filterPaths(paths);
   	
		return paths;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Table> getAllBPHTables(@Context HttpServletRequest request, @Context UriInfo ui) {
		// The id parameter is set when checking if a table exists.
		// Use the appropriate method.
		String tableId = request.getParameter("id");
		if (tableId != null) {
			ArrayList<Table> result = new ArrayList<Table>();
			result.add(getBPHTable(Integer.valueOf(tableId)));
			return result;
		}
		
		String tableIdParameter = null;
		String nameFilter = null;
		
		// dynamically build the JPA query
		CriteriaBuilder cb = this.manager.getCriteriaBuilder();
		
		// construct a basic query which equals
		// SELECT t FROM BPHTABLE t
		CriteriaQuery<Table> preQuery = cb.createQuery(Table.class);
		Root<Table> table = preQuery.from(Table.class);
		preQuery.select(table);

		// expand the basic query defined above based on possible sort
		// and filtering parameters	
		// first we retrieve all query parameters and look for 'sort' or 'name'
		MultivaluedMap<String, String> queryParams = ui.getQueryParameters();
		for (Entry<String, List<String>> entry : queryParams.entrySet()) {
			Predicate tableIdClause = null;
			
			if (Pattern.matches("tableId.*", entry.getKey())) {
				
				// add a WHERE condition to the predefined query
				Expression<String> tableIdPath = table.get("tableId");
				Expression<Integer> tableIdValue = cb.parameter(Integer.class, JPA_QUERY_TABLEID_VALUE);
				tableIdClause = cb.equal(tableIdPath, tableIdValue);
				preQuery.where(tableIdClause);
				
				// remember the table id query parameter
				tableIdParameter = entry.getValue().get(0);
			}
			
			if (Pattern.matches("name.*", entry.getKey())) {
				
				// add a WHERE condition to the predefined query
				Expression<String> namePath = table.get("name");
				Expression<String> nameValue = cb.parameter(String.class, JPA_QUERY_TABLENAME_VALUE);
				Predicate filterClause = cb.like(cb.lower(namePath), nameValue);
				
				// we need to honor a possible WHERE clause for the table id
				if (tableIdClause != null) {

					// combine the tableId WHERE clause and the filtering WHERE clause
					Predicate combinedClause = cb.and(tableIdClause, filterClause);
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
				ArrayList<Integer> excludedTableIds = new ArrayList<Integer>();
				
				String[] excludedIdsAsString = entry.getValue().get(0).split(",");
				
				if (excludedIdsAsString.length != 0) {
					for (String idAsString : excludedIdsAsString) {
						excludedTableIds.add(Integer.valueOf(idAsString));
					}
					
					// add a WHERE condition to the predefined query
					Expression<String> exclusionPath = table.get("tableId");
					Predicate exclusionClause = exclusionPath.in(excludedTableIds);
					
					preQuery.where(exclusionClause.not());				
				}
			}

			if (Pattern.matches("sort.*", entry.getKey())) {
				
				// add corresponding order by clauses to the predefined query
				if (entry.getKey().contains(ORDER_BY_NAME_ASC)) {
					Expression<String> orderByClause = table.get("name");
					Order sortOrder = cb.asc(orderByClause);
					preQuery.orderBy(sortOrder);
				}
				if (entry.getKey().contains(ORDER_BY_NAME_DESC)) {
					Expression<String> orderByClause = table.get("name");
					Order sortOrder = cb.desc(orderByClause);
					preQuery.orderBy(sortOrder);
				}
			}
		}
		
		// instantiate the predefined query into an actual typed query
		TypedQuery<Table> query = manager.createQuery(preQuery);
		
		// now we need to fill in the actual query parameter values
		if (query != null) {
			Set<ParameterExpression<?>> params = preQuery.getParameters();
			Iterator<ParameterExpression<?>> it = params.iterator();
			
			// depending on the query construction above we have
			// to check whether the known parameters exist and need to be set
			while (it.hasNext()) {
				ParameterExpression<?> ex = it.next();

				// the table id query parameter is not necessarily set so we definitely
				// need to check it here
				if (ex.getName().equalsIgnoreCase(JPA_QUERY_TABLEID_VALUE)) {
					query.setParameter(JPA_QUERY_TABLEID_VALUE, Integer.valueOf(tableIdParameter));
				}

				// the filter string is not necessarily set so we definitely
				// need to check it here
				if (ex.getName().equalsIgnoreCase(JPA_QUERY_TABLENAME_VALUE)) {
					query.setParameter(JPA_QUERY_TABLENAME_VALUE, "%"+nameFilter.toLowerCase()+"%");
				}
			}
			
			return query.getResultList();
		}
		
		// fallback (basic named query)
		query = manager.createNamedQuery("Table.getAll", Table.class);
		
      if (query != null) {
        	return query.getResultList();
      }
      
      return new ArrayList<Table>();
	}
	
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Table getBPHTable(@PathParam("id") int tableId) {
		Table t = manager.find(Table.class, Integer.valueOf(tableId));
		
		return t;
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Table createBPHTable(Table bphTable) {
		
		// infuse the current date before saving the new entity
		bphTable.setUpdated(new Date());
		
		try {
			transaction.begin();
			manager.joinTransaction();
			manager.persist(bphTable);
			transaction.commit();
		}
		catch (Exception e) {
			Util.throwInternalErrorToClient(transaction, e);
		}

		return bphTable;
	}
	
	@PUT
	@Path("/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Table updateBPHTable(@PathParam("id") int tableId, Table newTable) {
		final String METHOD_NAME = "updateBPHTable(int tableId, Table newTable)";
		logger.entering(CLASS_NAME, METHOD_NAME);
		Table table = null;
		
		try {
			transaction.begin();
			manager.joinTransaction();
			table = manager.find(Table.class, tableId);
			
			if (table == null) {
				Util.throwNotFoundErrorToClient(tableId);
			}
			
			boolean nameChanged = !table.getName().equals(newTable.getName());
			table.updateFrom(newTable);
			table.setUpdated(new Date());
			if (nameChanged) {
				logger.fine("Table has been renamed, update usage objects");
				// Update the table usage names
				for (TableUsage tableUsage : table.getUsages()) {
					logger.finer("Updating usage, old name: " + tableUsage.getFullName());
					tableUsage.updateNames();
					manager.merge(tableUsage);
				}
			}
			manager.merge(table);
			transaction.commit();
		}
		catch (Exception e) {
			Util.throwInternalErrorToClient(transaction, e);
		}
		
		logger.exiting(CLASS_NAME, METHOD_NAME);
		return table;
	}

	@DELETE
	@Path("/{id}")
	public void deleteBPHTable(@PathParam("id") int tableId) {
		try {
			transaction.begin();
			manager.joinTransaction();
			Table t = manager.find(Table.class, Integer.valueOf(tableId));
			manager.remove(t);
			transaction.commit();
		}
		catch (Exception e) {
			Util.throwInternalErrorToClient(transaction, e);
		}		
	}
	
	protected int getNumberOfTablesDefined() {
		TypedQuery<Table> query = manager.createNamedQuery("Table.getAll", Table.class);
		
		List<Table> tables = query.getResultList();
		
		if (tables != null) {
			return tables.size();
		}
		
		return 0;
	}
}
