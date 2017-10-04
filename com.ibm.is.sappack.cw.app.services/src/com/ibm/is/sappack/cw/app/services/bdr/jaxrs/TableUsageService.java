package com.ibm.is.sappack.cw.app.services.bdr.jaxrs;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.UserTransaction;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.ibm.is.sappack.cw.app.data.JPAResourceFactory;
import com.ibm.is.sappack.cw.app.data.bdr.jpa.Table;
import com.ibm.is.sappack.cw.app.data.bdr.jpa.TableUsage;
import com.ibm.is.sappack.cw.app.services.Util;

@Path("/bdr/tableusage")
public class TableUsageService {

	private final EntityManager manager;
	private final UserTransaction transaction;

	public TableUsageService() {
		this.manager = JPAResourceFactory.getEntityManager();
		this.transaction = JPAResourceFactory.getUserTransaction();
	}

	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public TableUsage getBPHTableUsage(@PathParam("id") int tableUsageId) {
		// Get TableUsage from DB
		TableUsage tableUsage = manager.find(TableUsage.class, Integer.valueOf(tableUsageId));
		
		// Copy a flat hierarchy in a new object without subobjects to save time and
		// memory when delivering this object to the client
		TableUsage tu = null;
		if(tableUsage != null) {
			tu = new TableUsage();
			tu.setApprovalStatus(tableUsage.getApprovalStatus());
			tu.setFieldUsageStatus(tableUsage.getFieldUsageStatus());
			tu.setFullName(tableUsage.getFullName());
			tu.setName(tableUsage.getName());
			tu.setTableUsageId(tableUsage.getTableUsageId());
			tu.setType(tableUsage.getType());
			tu.setUpdated(tableUsage.getUpdated());
		}
		return tu;
	}
	
	@GET
	@Path("/tablefor/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Table getBPHTableForTableUsage(@PathParam("id") int tableUsageId) {
		TableUsage tu = manager.find(TableUsage.class, Integer.valueOf(tableUsageId));

		if (tu != null) {
			Table t = manager.find(Table.class, Integer.valueOf(tu.getTable().getTableId()));
			return t;
		}
		
		return null;
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public TableUsage createBPHTableUsage(TableUsage tableUsage) {
		
		// infuse the current date before saving the new entity
		tableUsage.setUpdated(new Date());
		
		try {
			transaction.begin();
			manager.joinTransaction();
			manager.persist(tableUsage);
			transaction.commit();
		}
		catch (Exception e) {
			Util.throwInternalErrorToClient(transaction, e);
		}

		return tableUsage;
	}
	
	@PUT
	@Path("/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public TableUsage updateBPHTableUsage(@PathParam("id") int tableUsageId, TableUsage tableUsage) {
		try {
			transaction.begin();
			manager.joinTransaction();
			TableUsage tu = manager.find(TableUsage.class, tableUsageId);
			
			if (tu == null) {
				Util.throwNotFoundErrorToClient(tableUsageId);
			}
			
			tu.setApprovalStatus(tableUsage.getApprovalStatus());
			tu.setUpdated(new Date());
			manager.merge(tu);
			transaction.commit();
		}
		catch (Exception e) {
			Util.throwInternalErrorToClient(transaction, e);
		}
		
		TypedQuery<TableUsage> query = manager.createNamedQuery("TableUsage.getById", TableUsage.class);
		query.setParameter("tableUsageId", tableUsageId);
		
		return query.getSingleResult();
	}
}
