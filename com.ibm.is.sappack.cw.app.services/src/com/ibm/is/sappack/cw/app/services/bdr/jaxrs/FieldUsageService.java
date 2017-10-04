package com.ibm.is.sappack.cw.app.services.bdr.jaxrs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.Set;
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
import javax.ws.rs.GET;
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
import com.ibm.is.sappack.cw.app.data.bdr.FieldUsageStatus;
import com.ibm.is.sappack.cw.app.data.bdr.jpa.Field;
import com.ibm.is.sappack.cw.app.data.bdr.jpa.FieldUsage;
import com.ibm.is.sappack.cw.app.data.bdr.jpa.TableUsage;
import com.ibm.is.sappack.cw.app.services.Constants;
import com.ibm.is.sappack.cw.app.services.Util;

@Path("/bdr/fieldusage")
public class FieldUsageService {

	private static final String SORT_ORDER_PATTERN = "\\(([+-]+)(.+?)\\)";
	private static final String JPA_QUERY_FIELDNAME_VALUE = "nameValue";
	private static final String JPA_QUERY_TABLEUSAGEID_VALUE = "tableUsageIdValue";
	private static final String JPA_QUERY_SAPVIEW_VALUE = "sapViewValue";
	private static final String BACKSLASH = ""+(char)47;
	
	private final EntityManager manager;
	private final UserTransaction transaction;

	public FieldUsageService() {
		this.manager = JPAResourceFactory.getEntityManager();
		this.transaction = JPAResourceFactory.getUserTransaction();
	}
	
	@GET
	@Path("/report/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<FieldUsageReport> getFieldUsageReport(@PathParam("id") int fieldUsageId) {
		List<FieldUsageReport> reports = new ArrayList<FieldUsageReport>();

		FieldUsage fu = manager.find(FieldUsage.class, Integer.valueOf(fieldUsageId));
		
		if (fu != null) {
			Field f = fu.getField();
			FieldService fieldService = new FieldService();
			reports = fieldService.getFieldUsageReport(f.getFieldId());
		}
		return reports;
	}
	@GET
	@Path("/sapView/{tableUsageId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Set<String> getDistinctSapViewsForTableUsage(@PathParam("tableUsageId") int tableUsageId,@Context UriInfo ui, @Context HttpServletResponse response, @Context HttpServletRequest request) {
		
		TypedQuery<String> typedQuery = this.manager.createNamedQuery("FieldUsage.getDistinctSapViewsForTableUsage",String.class);
		List<String> queryResult = null;
		if (typedQuery != null) {
			typedQuery.setParameter("tableUsageId", tableUsageId);
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
	@Path("/tableusage/{tableUsageId}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<FieldUsage> getAllFieldUsages(@PathParam("tableUsageId") int tableUsageId, @Context UriInfo ui, @Context HttpServletResponse response, @Context HttpServletRequest request) {
		String nameFilter = null;
		String sapViewFilter = null;
		Boolean SapViewNullFlag=false;
		String range = request.getHeader("Range").replace("=", " ");
		// dynamically build the JPA query
		CriteriaBuilder cb = this.manager.getCriteriaBuilder();
		
		// construct a basic query
		// SELECT fu FROM FIELDUSAGE fu
		CriteriaQuery<FieldUsage> preQuery = cb.createQuery(FieldUsage.class);
		Root<FieldUsage> fieldUsage = preQuery.from(FieldUsage.class);
		preQuery.select(fieldUsage);
		
		// attach the WHERE clause for the table usage id
		// SELECT fu FROM FIELDUSAGE fu WHERE fu.tableUsage.tableUsageId = :tableUsageId
		Expression<String> tableUsageIdPath = fieldUsage.get("tableUsage").get("tableUsageId");
		Expression<Integer> tableUsageIdValue = cb.parameter(Integer.class, JPA_QUERY_TABLEUSAGEID_VALUE);
		Predicate tableUsageIdClause = cb.equal(tableUsageIdPath, tableUsageIdValue);
		preQuery.where(tableUsageIdClause);
		
		// expand the basic query defined above based on possible sorting and filtering parameters	
		// retrieve all query parameters and look for 'sort' or 'name'
		MultivaluedMap<String, String> queryParams = ui.getQueryParameters();
		for (Entry<String, List<String>> entry : queryParams.entrySet()) {
			if (Pattern.matches("name.*", entry.getKey())) {
				
				// add a WHERE condition to the query
				Expression<String> namePath = fieldUsage.get("field").get("name");
				Expression<String> nameValue = cb.parameter(String.class, JPA_QUERY_FIELDNAME_VALUE);
				Predicate filterClause = cb.like(cb.lower(namePath), nameValue);
				
				// combine the tableId WHERE clause and the filtering WHERE clause
				Predicate combinedClause = cb.and(tableUsageIdClause, filterClause);
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
				Root<FieldUsage> fieldUsageTmp = preQueryTmp.from(FieldUsage.class);
				preQueryTmp.multiselect(fieldUsageTmp.get("field").get("sapView"));
				
			if(sapViews.length>0)
				for(String sapView: sapViews)
				{
					if(Constants.UNDEFINED_TAG.equals(sapView)){
						SapViewNullFlag=true;
						continue;
					}
					Expression<String> sapViewPathTmp = fieldUsage.get("field").get("sapView");
					Expression<String> sapViewValueTmp = cb.parameter(String.class, JPA_QUERY_SAPVIEW_VALUE);
					Predicate filterClause =null;
					filterClause = cb.like(cb.lower(sapViewPathTmp), sapViewValueTmp);
					Predicate tmpCombinedClause = cb.and(tableUsageIdClause, filterClause);
					preQueryTmp.where(tmpCombinedClause);
					TypedQuery<String> tQuery = manager.createQuery(preQueryTmp);
					if (tQuery != null) {
						Set<ParameterExpression<?>> params = preQueryTmp.getParameters();
						Iterator<ParameterExpression<?>> it = params.iterator();
						while (it.hasNext()) {
							ParameterExpression<?> ex = it.next();
							
							if (ex.getName().equalsIgnoreCase(JPA_QUERY_TABLEUSAGEID_VALUE))
								tQuery.setParameter(JPA_QUERY_TABLEUSAGEID_VALUE, tableUsageId);
							if (ex.getName().equalsIgnoreCase(JPA_QUERY_SAPVIEW_VALUE))
								tQuery.setParameter(JPA_QUERY_SAPVIEW_VALUE, Constants.SAPVIEW_WILDCARD+sapView.toLowerCase()+Constants.SAPVIEW_WILDCARD);
						}
						sapViewAL.addAll(tQuery.getResultList());
					}
				}
				Expression<String> sapViewPath = fieldUsage.get("field").get("sapView");
				Predicate combinedClause=null;
				Predicate filterClause1 = (sapViewPath).in(sapViewAL); 
				Predicate filterClause2 = cb.isNull(sapViewPath);
				Predicate filterClause3 = cb.or(filterClause1,filterClause2);
				if(SapViewNullFlag)
					combinedClause = cb.and(tableUsageIdClause, filterClause3);
				else
					combinedClause = cb.and(tableUsageIdClause, filterClause1);
				preQuery.where(combinedClause);
			}
			
			// add order by clause to the query
			if (Pattern.matches("sort.*", entry.getKey())) {
				Pattern pattern = Pattern.compile(SORT_ORDER_PATTERN);
				Matcher matcher = pattern.matcher(entry.getKey());
				if (matcher.find()) {
					String direction = matcher.group(1);
					String column = matcher.group(2);
					Expression<String> sortField = null;
					if (FieldUsage.BDR_FIELD_USAGE_SORTABLE_COLUMNS.contains(column)) {
						sortField = fieldUsage.get(column);
					} else if (FieldUsage.BDR_FIELD_USAGE_SORTABLE_COLUMNS_FROM_FIELD.contains(column)) {
						sortField = fieldUsage.get("field").get(column);
					}
					if (sortField != null) {
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
		TypedQuery<FieldUsage> query = manager.createQuery(preQuery);
		
		// now we need to fill in the actual query parameter values
		// which are the table id and the filter string
		Set<ParameterExpression<?>> params = preQuery.getParameters();

		// depending on the query construction above we have
		// to check whether the known parameters exist and need to be set
		for (ParameterExpression<?> ex : params) {
			
			// tableId is set in the basic WHERE clause therefore
			// it is not necessary to look for it here (it MUST be set)
			// but we do it nevertheless so that it falls in line
			// with the other parameter
			if (ex.getName().equalsIgnoreCase(JPA_QUERY_TABLEUSAGEID_VALUE)) {
				query.setParameter(JPA_QUERY_TABLEUSAGEID_VALUE, tableUsageId);
			}
			
			// the filter string is not necessarily set so we definitely
			// need to check it here
			if (ex.getName().equalsIgnoreCase(JPA_QUERY_FIELDNAME_VALUE)) {
				query.setParameter(JPA_QUERY_FIELDNAME_VALUE, nameFilter.toLowerCase());
			}
		}
		List<FieldUsage> fieldUsageList = query.getResultList();
		response.setHeader("Content-Range", range+BACKSLASH+query.getResultList().size());
		return mergeFieldUsageListWithStatus(fieldUsageList, tableUsageId);
	}

	@PUT
	@Path("/tableusage/{tableUsageId}/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public FieldUsage updateFieldUsage(@PathParam("tableUsageId") int tableUsageId, @PathParam("id") int fieldUsageId, FieldUsage fieldUsage) {
		try {
			transaction.begin();
			manager.joinTransaction();
			TableUsage tableUsage = manager.find(TableUsage.class, tableUsageId);
			
			if (tableUsage == null) {
				
				Util.throwNotFoundErrorToClient(tableUsageId);
			}
			if(!fieldUsage.getUseMode().equals("INSCOPE"))
				fieldUsage.setGlobalTemplate("");
			
			fieldUsage.setTableUsage(tableUsage);
			FieldUsage oldFieldUsage = manager.find(FieldUsage.class, fieldUsageId);
			
			if (oldFieldUsage == null) {
				Util.throwNotFoundErrorToClient(fieldUsageId);
			}
			
			oldFieldUsage.updateFrom(fieldUsage);
			manager.merge(oldFieldUsage);
			transaction.commit();
		}
		catch (Exception e) {
			Util.throwInternalErrorToClient(transaction, e);
		}
		
		TypedQuery<FieldUsage> query = manager.createNamedQuery("FieldUsage.getById", FieldUsage.class);
		query.setParameter("fieldUsageId", fieldUsageId);
		try{
		FieldUsage fu = query.getSingleResult();
		return mergeFieldUsageWithStatus(fu, tableUsageId);
		}catch(Exception e){
			System.out.println(e.getMessage());
			return null;
		}
		}	
	
	@PUT
	@Path("/report/{reportId}/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public FieldUsage updateUsageStore(@PathParam("reportId") int tableUsageId, @PathParam("id") int fieldUsageId, FieldUsage fieldUsage) {
		try {
			transaction.begin();
			manager.joinTransaction();
			
			if(!fieldUsage.getUseMode().equals("INSCOPE"))
				fieldUsage.setGlobalTemplate("");
			
			FieldUsage oldFieldUsage = manager.find(FieldUsage.class, fieldUsageId);
			
			if (oldFieldUsage == null) {
				Util.throwNotFoundErrorToClient(fieldUsageId);
			}
			
			oldFieldUsage.updateFrom(fieldUsage);
			manager.merge(oldFieldUsage);
			transaction.commit();
		}
		catch (Exception e) {
			Util.throwInternalErrorToClient(transaction, e);
		}
		
		TypedQuery<FieldUsage> query = manager.createNamedQuery("FieldUsage.getById", FieldUsage.class);
		query.setParameter("fieldUsageId", fieldUsageId);
		try{
		FieldUsage fu = query.getSingleResult();
		return mergeFieldUsageWithStatus(fu, tableUsageId);
		}catch(Exception e){
			System.out.println(e.getMessage());
			return null;
		}
		}	


	protected FieldUsage mergeFieldUsageWithStatus(FieldUsage fieldUsage, int tableUsageId) {
		FieldUsageStatus status = FieldUsageStatus.OK;
		
		List<FieldUsageReport> fieldUsageReports = getFieldUsageReport(fieldUsage.getFieldUsageId());
		
		for (FieldUsageReport fur : fieldUsageReports) {
			if (fur.getStatus() != FieldUsageStatus.OK && fur.getTableUsageId() == tableUsageId) {
				status = fur.getStatus();
			}
		}
		
		fieldUsage.setStatus(status);
		
		return fieldUsage;
   }

	private List<FieldUsage> mergeFieldUsageListWithStatus(List<FieldUsage> fieldUsageList, int tableUsageId) {
		if (fieldUsageList != null) {
			Iterator<FieldUsage> fieldUsageIt = fieldUsageList.iterator();
			
			while (fieldUsageIt.hasNext()) {
				FieldUsage fu = fieldUsageIt.next();

				FieldUsageStatus status = FieldUsageStatus.OK;
				
				List<FieldUsageReport> fieldUsageReports = getFieldUsageReport(fu.getFieldUsageId());
				
				if (fieldUsageReports != null) {
					for (FieldUsageReport fur : fieldUsageReports) {
						if (fur.getStatus() != FieldUsageStatus.OK && fur.getTableUsageId() == tableUsageId) {
							status = fur.getStatus();
						}
					}
				}
				
				fu.setStatus(status);
			}
			
			return fieldUsageList;
		}
		
	   return new ArrayList<FieldUsage>();
   }
}
