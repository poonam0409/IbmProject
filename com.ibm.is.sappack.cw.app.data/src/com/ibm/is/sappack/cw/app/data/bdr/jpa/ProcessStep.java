package com.ibm.is.sappack.cw.app.data.bdr.jpa;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.codehaus.jackson.annotate.JsonBackReference;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonManagedReference;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.ibm.is.sappack.cw.app.data.Resources;
import com.ibm.is.sappack.cw.app.data.bdr.ApprovalStatus;
import com.ibm.is.sappack.cw.app.data.bdr.ApprovalStatusDeserializer;
import com.ibm.is.sappack.cw.app.data.bdr.ApprovalStatusSerializer;
import com.ibm.is.sappack.cw.app.data.bdr.FieldUsageStatus;
import com.ibm.is.sappack.cw.app.data.bdr.FieldUsageStatusDeserializer;
import com.ibm.is.sappack.cw.app.data.bdr.FieldUsageStatusSerializer;

@Entity(name = "PROCESSSTEP")
public class ProcessStep implements Serializable, Comparable<ProcessStep> {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	@Column(name = "ID", updatable = false)
	private Integer processStepId;

	@ManyToOne(fetch = FetchType.LAZY)
	private Process parentProcess;

	@ManyToMany(targetEntity = BusinessObject.class, fetch = FetchType.LAZY, cascade = { CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH })
	private List<BusinessObject> usedBusinessObjects;

	@ElementCollection
	@CollectionTable(name = "BPHTRANSACTION")
	private List<Transaction> transactions;

	@ElementCollection
	@CollectionTable(name = "PROCESSSTEPACL")
	private List<UserRegistryObject> allowed;

	@OneToMany(mappedBy = "processStep")
	private List<TableUsage> usages;

	@Column(name = "NAME", length = Resources.BDR_LENGTH_PROCESS_STEP_NAME * Resources.BDR_LENGTH_BYTE_MULTIPLIER)
	private String name;

	@Column(name = "DESCRIPTION", length = Resources.BDR_LENGTH_DESCRIPTION * Resources.BDR_LENGTH_BYTE_MULTIPLIER)
	private String description;

	@Temporal(value = TemporalType.TIMESTAMP)
	@Column(name = "UPDATED")
	private Date updated;

	@Transient
	private String type = Resources.BPH_TYPE_PROCESSSTEP;

	@Transient
	private ApprovalStatus approvalStatus = ApprovalStatus.UNDEFINED;

	@Transient
	private FieldUsageStatus fieldUsageStatus = FieldUsageStatus.OK;

	// TODO make the constructor require a field
	public ProcessStep() {
	}
	
	// Setters / getters

	public void setType(String type) {
		this.type = type;
		this.type = Resources.BPH_TYPE_PROCESSSTEP;
	}

	public String getType() {
		return type;
	}

	@JsonProperty(value = "id")
	public void setProcessStepId(Integer processStepId) {
		this.processStepId = processStepId;
	}

	@JsonProperty(value = "id")
	public Integer getProcessStepId() {
		return processStepId;
	}

	@JsonBackReference("process-processstep")
	public void setParentProcess(Process parentProcess) {
		this.parentProcess = parentProcess;
	}

	@JsonBackReference("process-processstep")
	public Process getParentProcess() {
		return parentProcess;
	}

	public void setUsedBusinessObjects(List<BusinessObject> usedBusinessObjects) {
		this.usedBusinessObjects = usedBusinessObjects;
	}

	public List<BusinessObject> getUsedBusinessObjects() {
		if (usedBusinessObjects != null) {
			Collections.sort(usedBusinessObjects);
		} else {
			usedBusinessObjects = new ArrayList<BusinessObject>(0);
		}

		return usedBusinessObjects;
	}

	public void setTransactions(List<Transaction> transactions) {
		this.transactions = transactions;
	}

	public List<Transaction> getTransactions() {
		if (transactions != null) {
			Collections.sort(transactions);
		} else {
			transactions = new ArrayList<Transaction>(0);
		}

		return transactions;
	}

	public List<UserRegistryObject> getAllowed() {
		if (allowed != null) {
			Collections.sort(allowed);
		} else {
			allowed = new ArrayList<UserRegistryObject>(0);
		}

		return allowed;
	}

	public void setAllowed(List<UserRegistryObject> allowed) {
		this.allowed = allowed;
	}

	@JsonManagedReference("tableusage-processstep")
	public void setUsages(List<TableUsage> usages) {
		this.usages = usages;
	}

	/** 173166 added jsonignore to avoid large JSON response in BPH tree */
	@JsonIgnore
	@JsonManagedReference("tableusage-processstep")
	public List<TableUsage> getUsages() {
		if (usages != null) {
			Collections.sort(usages);
		} else {
			usages = new ArrayList<TableUsage>(0);
		}

		return usages;
	}

	public void setName(String name) {
		if (name != null && name.length() > Resources.BDR_LENGTH_PROCESS_STEP_NAME) {
			this.name = name.substring(0, Resources.BDR_LENGTH_PROCESS_STEP_NAME);
		} else {
			this.name = name;
		}
	}

	public String getName() {
		return name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public Date getUpdated() {
		return updated;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}

	@JsonSerialize(using = ApprovalStatusSerializer.class)
	public ApprovalStatus getApprovalStatus() {
		return approvalStatus;
	}

	@JsonDeserialize(using = ApprovalStatusDeserializer.class)
	public void setApprovalStatus(ApprovalStatus approvalStatus) {
		this.approvalStatus = approvalStatus;
	}

	@JsonSerialize(using = FieldUsageStatusSerializer.class)
	public FieldUsageStatus getFieldUsageStatus() {
		return fieldUsageStatus;
	}
	
	@JsonDeserialize(using = FieldUsageStatusDeserializer.class)
	public void setFieldUsageStatus(FieldUsageStatus fieldUsageStatus) {
		this.fieldUsageStatus = fieldUsageStatus;
	}

	public TableUsage getUsage(BusinessObject bo, Table table) {
		for (TableUsage tableUsage : usages) {
			if (tableUsage.getBusinessObject().equals(bo) && tableUsage.getTable().equals(table)) {
				return tableUsage;
			}
		}
		return null; // TODO Throw an exception? 
	}

	@Override
	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (o == this)
			return true;
		if (o.getClass() != getClass())
			return false;
		return (((ProcessStep) o).getProcessStepId().equals(processStepId));
	}

	@Override
	public int hashCode() {
		return (processStepId == null) ? 0 : processStepId;
	}

	@Override
	public int compareTo(ProcessStep ps) {
		if (name != null && ps.getName() != null) {
			return name.compareTo(ps.getName());
		}

		return 0;
	}
}
