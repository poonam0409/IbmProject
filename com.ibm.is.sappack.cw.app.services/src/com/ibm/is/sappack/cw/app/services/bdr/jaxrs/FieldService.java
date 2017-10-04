package com.ibm.is.sappack.cw.app.services.bdr.jaxrs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.regex.Matcher;
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
import javax.servlet.http.HttpServletResponse;
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
import com.ibm.is.sappack.cw.app.data.bdr.FieldUsageReport;
import com.ibm.is.sappack.cw.app.data.bdr.jpa.BusinessObject;
import com.ibm.is.sappack.cw.app.data.bdr.jpa.Field;
import com.ibm.is.sappack.cw.app.data.bdr.jpa.FieldUsage;
import com.ibm.is.sappack.cw.app.data.bdr.jpa.Process;
import com.ibm.is.sappack.cw.app.data.bdr.jpa.ProcessStep;
import com.ibm.is.sappack.cw.app.data.bdr.jpa.Table;
import com.ibm.is.sappack.cw.app.data.bdr.jpa.TableUsage;
import com.ibm.is.sappack.cw.app.services.Constants;
import com.ibm.is.sappack.cw.app.services.Util;
import com.ibm.is.sappack.cw.app.services.logging.CWAppLogger;

@Path("/bdr/field")
public class FieldService {
	
	private static final String SORT_ORDER_PATTERN = "\\(([+-]+)(.+?)\\)";
	private static final String JPA_QUERY_FIELDNAME_VALUE = "nameValue";
	private static final String JPA_QUERY_TABLEID_VALUE = "tableIdValue";
	private static final String JPA_QUERY_SAPVIEW_VALUE = "sapViewValue";
	private static final String BACKSLASH = ""+(char)47;
	private final EntityManager manager;
	private final UserTransaction transaction;
	

	public FieldService() {
		this.manager = JPAResourceFactory.getEntityManager();
		this.transaction = JPAResourceFactory.getUserTransaction();
	}
	
	@GET
	@Path("/report/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<FieldUsageReport> getFieldUsageReport(@PathParam("id") int fieldId) {
		List<FieldUsageReport> reports = new ArrayList<FieldUsageReport>();
				
		Field field = manager.find(Field.class, Integer.valueOf(fieldId));
		
		if (field != null) {
			for (FieldUsage fieldUsage : field.getUsages()) {
				TableUsage tableUsage = fieldUsage.getTableUsage();
				BusinessObject bo = tableUsage.getBusinessObject();
				ProcessStep ps = tableUsage.getProcessStep();

				FieldUsageReport report = new FieldUsageReport();
				report.setUseMode(fieldUsage.getUseMode());
				report.setRequired(fieldUsage.getRequired());
				report.setStatus(fieldUsage.getStatus());
				report.setBusinessObjectName(bo.getName());
				report.setTableUsageId(tableUsage.getTableUsageId());
				report.setReportId(fieldUsage.getFieldUsageId());
				report.setGlobalTemplate(fieldUsage.getGlobalTemplate());
				
				Process process = ps.getParentProcess();
				String processChain = process.getName() + Constants.PROCESS_CHAIN_SEPARATOR + ps.getName();
				Process parent = process.getParentProcess();
				if (parent != null) {
					addParentProcessNamesToChain(parent, processChain);
				}
				
				report.setProcessChain(processChain);
				reports.add(report);
			}
		}
		// Give every report a unique id
		//generateReportIds(reports);
		
		//return computeFieldUsageStatus(reports);
		return reports;
	}
	
	private void generateReportIds(List<FieldUsageReport> reports) {
		int id = 0;
		for (FieldUsageReport report : reports) {
			report.setReportId(id);
			id++;
		}
	}

	@GET
	@Path("/{fieldId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Field getField(@PathParam("fieldId") int fieldId) {
		return manager.find(Field.class, Integer.valueOf(fieldId));
	}
	
	@GET
	@Path("/sapView/{tableId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Set<String> getUniqueSapViewsForTable(@PathParam("tableId") int tableId,@Context UriInfo ui, @Context HttpServletResponse response, @Context HttpServletRequest request) {
		final String METHOD_NAME = "createProcessStep(ProcessStep newProcessStep)";
		CWAppLogger.log(Level.FINE,"CWApp_Entering_Meth",new Object[]{METHOD_NAME});

		TypedQuery<String> typedQuery = this.manager.createNamedQuery("Field.getDistinctSapViewsForTable",String.class);
		List<String> queryResult = null;
		if (typedQuery != null) {
			typedQuery.setParameter("tableId", tableId);
			queryResult=typedQuery.getResultList();
			
		}
		SortedSet<String> sapView = new TreeSet<String>();
		sapView.add(Constants.UNDEFINED_TAG);
		sapView.add(Constants.REMOVE_FILTER_TAG);
		addUniqueSAPViews(sapView,queryResult);
		return sapView;
	}
	
	private void addUniqueSAPViews(SortedSet<String>sapView,List<String> queryResult)
	{
		for(String qr : queryResult)
		{
			if(qr==null)
				continue;
			String [] sapViewList=qr.split("\\"+Constants.SAPVIEW_SEPARATOR);
			for(String str:sapViewList)
			{
				sapView.add(str.trim());	
			}
		}	
	}
	
	@GET
	@Path("/table/{tableId}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Field> getAllFields(@PathParam("tableId") int tableId, @Context UriInfo ui, @Context HttpServletResponse response, @Context HttpServletRequest request) {
		String nameFilter = null;
		String sapViewFilter = null;
		Boolean SapViewNullFlag=false;
		final String METHOD_NAME = "getAllFields()";
		CWAppLogger.log(Level.FINE,"CWApp_Entering_Meth",new Object[]{METHOD_NAME});
		String range = request.getHeader("Range").replace("=", " ");
		// dynamically build the JPA query
		CriteriaBuilder cb = this.manager.getCriteriaBuilder();
		// construct a basic query
		// SELECT f FROM FIELD f
		CriteriaQuery<Field> preQuery = cb.createQuery(Field.class);
		Root<Field> field = preQuery.from(Field.class);
		preQuery.select(field);
		
		// attach the WHERE clause for the table id
		// SELECT f FROM FIELD f WHERE f.table.tableId = :tableId
		Expression<String> tableIdPath = field.get("table").get("tableId");
		Expression<Integer> tableIdValue = cb.parameter(Integer.class, JPA_QUERY_TABLEID_VALUE);
		Predicate tableIdClause = cb.equal(tableIdPath, tableIdValue);
		preQuery.where(tableIdClause);
		
		// expand the basic query defined above based on possible sorting and filtering parameters	
		// retrieve all query parameters and look for 'sort' or 'name'
		MultivaluedMap<String, String> queryParams = ui.getQueryParameters();
		for (Entry<String, List<String>> entry : queryParams.entrySet()) {
		
			if (Pattern.matches("name.*", entry.getKey())) {
				
				// add a WHERE condition to the query
				Expression<String> namePath = field.get("name");
				Expression<String> nameValue = cb.parameter(String.class, JPA_QUERY_FIELDNAME_VALUE);
				Predicate filterClause = cb.like(cb.lower(namePath), nameValue);
				
				// combine the tableId WHERE clause and the filtering WHERE clause
				Predicate combinedClause = cb.and(tableIdClause, filterClause);
				preQuery.where(combinedClause);
				
				// remember the filter string
				nameFilter = entry.getValue().get(0);
			}
			
			if (Pattern.matches("sapView.*", entry.getKey()) && !entry.getValue().get(0).toString().contains(Constants.REMOVE_FILTER_TAG)) {
				// add a WHERE condition to the query
				sapViewFilter = entry.getValue().get(0);
				
				ArrayList<String> sapViewAL=new ArrayList<String>();
				String[] sapViews=sapViewFilter.split(",");
				CriteriaBuilder cbTmp = this.manager.getCriteriaBuilder();
				CriteriaQuery<String> preQueryTmp = cbTmp.createQuery(String.class);
				Root<Field> fieldTmp = preQueryTmp.from(Field.class);
				preQueryTmp.multiselect(fieldTmp.get("sapView"));
				
			if(sapViews.length>0)
				for(String sapView: sapViews)
				{
					if(Constants.UNDEFINED_TAG.equals(sapView)){
						SapViewNullFlag=true;
						continue;
					}
					Expression<String> sapViewPathTmp = field.get("sapView");
					Expression<String> sapViewValueTmp = cb.parameter(String.class, JPA_QUERY_SAPVIEW_VALUE);
					Predicate filterClause =null;
					filterClause = cb.like(cb.lower(sapViewPathTmp), sapViewValueTmp);
					Predicate tmpCombinedClause = cb.and(tableIdClause, filterClause);
					preQueryTmp.where(tmpCombinedClause);
					TypedQuery<String> tQuery = manager.createQuery(preQueryTmp);
					if (tQuery != null) {
						Set<ParameterExpression<?>> params = preQueryTmp.getParameters();
						Iterator<ParameterExpression<?>> it = params.iterator();
						while (it.hasNext()) {
							ParameterExpression<?> ex = it.next();
							if (ex.getName().equalsIgnoreCase(JPA_QUERY_TABLEID_VALUE)) {
								tQuery.setParameter(JPA_QUERY_TABLEID_VALUE, tableId);
							}
							if (ex.getName().equalsIgnoreCase(JPA_QUERY_SAPVIEW_VALUE)) {
								tQuery.setParameter(JPA_QUERY_SAPVIEW_VALUE, Constants.SAPVIEW_WILDCARD+sapView.toLowerCase()+Constants.SAPVIEW_WILDCARD);
							}
						}
						sapViewAL.addAll(tQuery.getResultList());
					}
				}
				Expression<String> sapViewPath = field.get("sapView");
				Predicate combinedClause=null;
				Predicate filterClause1 = (sapViewPath).in(sapViewAL); 
				Predicate filterClause2 = cb.isNull(sapViewPath);
				Predicate filterClause3 = cb.or(filterClause1,filterClause2);
				if(SapViewNullFlag)
					combinedClause = cb.and(tableIdClause, filterClause3);
				else
					combinedClause = cb.and(tableIdClause, filterClause1);
					
				preQuery.where(combinedClause);
				
			}	
			// add order by clause to the query
			if (Pattern.matches("sort.*", entry.getKey())) {
				Pattern pattern = Pattern.compile(SORT_ORDER_PATTERN);
				Matcher matcher = pattern.matcher(entry.getKey());
				if (matcher.find()) {
					String direction = matcher.group(1);
					String column = matcher.group(2);
					if (Field.BDR_FIELD_SORTABLE_COLUMNS.contains(column)) {
						Expression<String> sortField = field.get(column);
						Order sortOrder;
						if ("+".equals(direction)) {
							sortOrder = cb.asc(sortField);
						} else {
							sortOrder = cb.desc(sortField);
						}
						preQuery.orderBy(sortOrder);
					}
				}
			}
		}
		
		// instantiate the predefined query into an actual typed query
		TypedQuery<Field> query = manager.createQuery(preQuery);
		
		// now we need to fill in the actual query parameter values
		// which are the table id and the filter string
		if (query != null) {
			Set<ParameterExpression<?>> params = preQuery.getParameters();
			Iterator<ParameterExpression<?>> it = params.iterator();
			
			// depending on the query construction above we have
			// to check whether the known parameters exist and need to be set
			while (it.hasNext()) {
				ParameterExpression<?> ex = it.next();
				
				// tableId is set in the basic WHERE clause therefore
				// it is not necessary to look for it here (it MUST be set)
				// but we do it nevertheless so that it falls in line
				// with the other parameter
				if (ex.getName().equalsIgnoreCase(JPA_QUERY_TABLEID_VALUE)) {
					query.setParameter(JPA_QUERY_TABLEID_VALUE, tableId);
				}
				
				if (ex.getName().equalsIgnoreCase(JPA_QUERY_FIELDNAME_VALUE)) {
					query.setParameter(JPA_QUERY_FIELDNAME_VALUE, nameFilter.toLowerCase());
				}
			}
			
			//System.out.println("Query Results="+query.toString()+query.getResultList());
			CWAppLogger.log(Level.FINE,"CWApp_10004",new Object[]{query.toString()});
			response.setHeader("Content-Range", range+BACKSLASH+query.getResultList().size());
			return query.getResultList();
		}
		
		// fallback (basic named query)
		query = manager.createNamedQuery("Field.getAllForTable", Field.class);

		if (query != null) {
			query.setParameter("tableId", tableId);
			return query.getResultList();
		}

		return new ArrayList<Field>();
	}
		
	@POST
	@Path("/table/{tableId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Field createField(@PathParam("tableId") int tableId, Field field) {
		try {
			transaction.begin();
			manager.joinTransaction();
			
			Table t = manager.find(Table.class, tableId);
			
			if (t == null) {
				Util.throwNotFoundErrorToClient(tableId);
			}
			
			field.setTable(t);
			manager.persist(field);
			manager.merge(t);
			
			// save and reload the table to get the current field list
			transaction.commit();
			transaction.begin();
			manager.joinTransaction();
			t = manager.find(Table.class, Integer.valueOf(tableId));
			
			// adding a new field to a table triggers a date update
			t.setUpdated(new Date());
			
			if (t.getUsages() != null) {
				for (TableUsage tu : t.getUsages()) {
					tu.updateFields();
					tu.setUpdated(new Date());					
					manager.merge(tu);
				}
			}
			
			manager.merge(t);
			transaction.commit();
		}
		catch (Exception e) {
			Util.throwInternalErrorToClient(transaction, e);
		}

		return field;
	}
	
	@PUT
	@Path("/table/{tableId}/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Field updateField(@PathParam("tableId") int tableId, @PathParam("id") int fieldId, Field field) {
		Field f = null;
		
		try {
			transaction.begin();
			manager.joinTransaction();
			Table t = manager.find(Table.class, tableId);
			
			if (t == null) {
				Util.throwNotFoundErrorToClient(tableId);
			}
			
			field.setTable(t);
			f = manager.find(Field.class, fieldId);
			
			if (f == null) {
				Util.throwNotFoundErrorToClient(fieldId);
			}
			
			f.updateFrom(field);
			manager.merge(f);
			transaction.commit();
		}
		catch (Exception e) {
			Util.throwInternalErrorToClient(transaction, e);
		}
		
		return f;
	}
	
	@DELETE
	@Path("/table/{tableId}/{id}")
	public void deleteField(@SuppressWarnings("unused") @PathParam("tableId") int tableId, @PathParam("id") int fieldId) {
		try {
			transaction.begin();
			manager.joinTransaction();
			Field f = manager.find(Field.class, Integer.valueOf(fieldId));
			manager.remove(f);
			transaction.commit();
		}
		catch (Exception e) {
			Util.throwInternalErrorToClient(transaction, e);
		}		
	}

	@DELETE
	@Path("/table/{tableId}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteFieldsById(@SuppressWarnings("unused") @PathParam("tableId") int tableId, Collection<Integer> fieldIds) {
		try {
			transaction.begin();
			manager.joinTransaction();
			
			Iterator<Integer> fieldIdsIterator = fieldIds.iterator();
			
			while (fieldIdsIterator.hasNext()) {
				Field f = manager.find(Field.class, fieldIdsIterator.next());
				manager.remove(f);
			}

			transaction.commit();
		}
		catch (Exception e) {
			Util.throwInternalErrorToClient(transaction, e);
		}		
	}
	
	public List<Field> getFieldsByTableId(int tableId) {
		List<Field> result = null;
		try {
			transaction.begin();
			manager.joinTransaction();

			TypedQuery<Field> getAllFieldsForTable = manager.createNamedQuery("Field.getAllForTable", Field.class);
			getAllFieldsForTable.setParameter("tableId", tableId);
			result = getAllFieldsForTable.getResultList();
			transaction.commit();
		}
		catch (Exception e) {
			Util.throwInternalErrorToClient(transaction, e);
		}
		
		return result;
	}

	private void addParentProcessNamesToChain(Process parentProcess, String processChain) {
		processChain = parentProcess.getName() + Constants.PROCESS_CHAIN_SEPARATOR + processChain;
		Process parent = parentProcess.getParentProcess();
		if (parent != null) {
			addParentProcessNamesToChain(parent, processChain);
		}
	}
	
	// Not used since we removed the read/write differentiation
//	private List<FieldUsageReport> computeFieldUsageStatus(List<FieldUsageReport> inputReportList) {
//		List<FieldUsageReport> resultReportList = new ArrayList<FieldUsageReport>();
//		FieldUsageUseMode useMode = FieldUsageUseMode.UNSPECIFIED;
//		
//		for (FieldUsageReport usageReport : inputReportList) {
//			if (usageReport.isRequired()) {
//				if (useMode != FieldUsageUseMode.WRITE) {
//					usageReport.setStatus(FieldUsageStatus.REQUIRED_BUT_NEVER_WRITTEN);
//				}
//			}
//
//			if (usageReport.getUseMode() == FieldUsageUseMode.READ) {
//				if (useMode != FieldUsageUseMode.WRITE) {
//					usageReport.setStatus(FieldUsageStatus.READ_BUT_NEVER_WRITTEN);
//				}
//				
//				useMode = usageReport.getUseMode();
//			}
//			else if (usageReport.getUseMode() == FieldUsageUseMode.WRITE) {
//				if (useMode == FieldUsageUseMode.WRITE) {
//					usageReport.setStatus(FieldUsageStatus.MULTIPLE_WRITES);
//					
//					for (FieldUsageReport fur : resultReportList) {
//						fur.setStatus(FieldUsageStatus.MULTIPLE_WRITES);
//					}
//				}
//				else {
//					usageReport.setStatus(FieldUsageStatus.OK);
//					
//					for (FieldUsageReport fur : resultReportList) {
//						fur.setStatus(FieldUsageStatus.OK);
//					}
//				}
//				
//				useMode = usageReport.getUseMode();
//			}
//			
//			resultReportList.add(usageReport);
//		}
//		
//		return resultReportList;
//	}
}
