package com.ibm.is.sappack.cw.app.data.bdr.jpa;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.codehaus.jackson.annotate.JsonBackReference;
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

@Entity(name = "TABLEUSAGE")
public class TableUsage implements Serializable, Comparable<TableUsage> {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	@Column(name = "ID", updatable = false)
	private Integer tableUsageId;

	@OneToMany(mappedBy = "tableUsage", fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
	private Collection<FieldUsage> fieldUsages;

	@ManyToOne(fetch = FetchType.LAZY)
	private ProcessStep processStep;

	@ManyToOne(fetch = FetchType.LAZY)
	private BusinessObject businessObject;

	@ManyToOne(fetch = FetchType.LAZY)
	private Table table;

	// The short name shown in the tree
	@Column(name = "NAME", length = Resources.BDR_LENGTH_TABLE_NAME * Resources.BDR_LENGTH_BYTE_MULTIPLIER)
	private String name;

	// The full name shown in the details panel
	@Column(name = "FULLNAME", length = (Resources.BDR_LENGTH_PROCESS_STEP_NAME + Resources.BDR_LENGTH_BO_NAME + Resources.BDR_LENGTH_TABLE_NAME + Resources.BDR_TABLE_USAGE_NAME_SEPARATOR_LENGTH * 2) * Resources.BDR_LENGTH_BYTE_MULTIPLIER)
	private String fullName;

	@Temporal(value = TemporalType.TIMESTAMP)
	@Column(name = "UPDATED")
	private Date updated;

	@Enumerated(EnumType.STRING)
	private ApprovalStatus approvalStatus = ApprovalStatus.DRAFT;

	@Transient
	private String type = Resources.BPH_TYPE_TABLE_USAGE;

	@Transient
	private FieldUsageStatus fieldUsageStatus = FieldUsageStatus.OK;


	public TableUsage() {
	}

	// TODO make this the only constructor
	public TableUsage(ProcessStep processStep, BusinessObject bo, Table table) {
		this.processStep = processStep;
		this.businessObject = bo;
		this.table = table;
		this.updated = new Date();
		this.type = Resources.BPH_TYPE_TABLE_USAGE;
		this.updateNames();
		this.fieldUsages = new HashSet<FieldUsage>(0);
		this.updateFields();
	}

	@JsonProperty(value = "id")
	public void setTableUsageId(Integer tableUsageId) {
		this.tableUsageId = tableUsageId;
	}

	@JsonProperty(value = "id")
	public Integer getTableUsageId() {
		return tableUsageId;
	}

	public void setFieldUsages(Collection<FieldUsage> fieldUsages) {
		this.fieldUsages = fieldUsages;
	}

	public Collection<FieldUsage> getFieldUsages() {
		if (fieldUsages == null) {
			fieldUsages = new HashSet<FieldUsage>(0);
		}
		return fieldUsages;
	}

	@JsonBackReference("table-tableusage")
	public void setTable(Table table) {
		this.table = table;
	}

	@JsonBackReference("table-tableusage")
	public Table getTable() {
		return table;
	}

	@JsonBackReference("tableusage-processstep")
	public void setProcessStep(ProcessStep processStep) {
		this.processStep = processStep;
	}

	@JsonBackReference("tableusage-processstep")
	public ProcessStep getProcessStep() {
		return processStep;
	}

	@JsonBackReference("tableusage-businessobject")
	public void setBusinessObject(BusinessObject businessObject) {
		this.businessObject = businessObject;
	}

	@JsonBackReference("tableusage-businessobject")
	public BusinessObject getBusinessObject() {
		return businessObject;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getFullName() {
		return fullName;
	}

	public Date getUpdated() {
		return updated;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
		this.type = Resources.BPH_TYPE_TABLE_USAGE;
	}

	public void updateNames() {
		this.name = table.getName();
		this.fullName = processStep.getName() + Resources.BDR_TABLE_USAGE_NAME_SEPARATOR + table.getName();
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
	
	public void updateFrom(TableUsage tu) {
		this.name = tu.getName();
		this.fullName = tu.getFullName();
		this.table = tu.getTable();
		this.processStep = tu.getProcessStep();
		this.businessObject = tu.getBusinessObject();
		this.fieldUsages = tu.getFieldUsages();
		this.updated = tu.getUpdated();
		this.approvalStatus = tu.getApprovalStatus();
		setType(tu.getType());
	}

	// Updates the collection of field usages to match the table's fields
	public void updateFields() {

		// Create a set of all used fields
		Set<Field> usedFields = new HashSet<Field>();
		
		if(fieldUsages == null) {
			fieldUsages = new ArrayList<FieldUsage>();
		}
		
		for(FieldUsage fu : fieldUsages) {
			usedFields.add(fu.getField());
		}
		
		// Find missing usages and add them
		if (table.getFields() != null) {
			for (Field field : table.getFields()) {
				if (!usedFields.contains(field)) {
					FieldUsage newUsage = new FieldUsage();
					newUsage.setField(field);
					newUsage.setTableUsage(this);
					fieldUsages.add(newUsage);
				}
			}
		}
	}

	@Override
	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (o == this)
			return true;
		if (o.getClass() != getClass())
			return false;
		return (((TableUsage) o).getTableUsageId().equals(tableUsageId));
	}

	@Override
	public int hashCode() {
		return (tableUsageId == null) ? 0 : tableUsageId;
	}

	@Override
	public int compareTo(TableUsage tu) {
		if (fullName != null && tu.getFullName() != null) {
			return fullName.compareTo(tu.getFullName());
		}

		return 0;
	}
}