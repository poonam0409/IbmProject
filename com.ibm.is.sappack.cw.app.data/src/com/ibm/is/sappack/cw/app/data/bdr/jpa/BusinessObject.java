package com.ibm.is.sappack.cw.app.data.bdr.jpa;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

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

@Entity(name = "BOBJECT")
public class BusinessObject implements Serializable, Comparable<BusinessObject> {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	@Column(name = "ID", updatable = false)
	private Integer businessObjectId;

	@ManyToMany(targetEntity = ProcessStep.class, mappedBy = "usedBusinessObjects", fetch = FetchType.LAZY)
	private List<ProcessStep> usedInProcessSteps;

	@ManyToMany(targetEntity = Table.class, fetch = FetchType.LAZY)
	private List<Table> tables;

	@OneToMany(mappedBy = "businessObject", fetch = FetchType.LAZY)
	private List<TableUsage> usages;

	@Column(name = "NAME", length = Resources.BDR_LENGTH_BO_NAME * Resources.BDR_LENGTH_BYTE_MULTIPLIER)
	private String name;
	
	@Column(name = "SHORTNAME", length = Resources.BDR_LENGTH_BO_SHORTNAME * Resources.BDR_LENGTH_BYTE_MULTIPLIER)
	private String shortName;

	@Column(name = "DESCRIPTION", length = Resources.BDR_LENGTH_DESCRIPTION * Resources.BDR_LENGTH_BYTE_MULTIPLIER)
	private String description;

	@Temporal(value = TemporalType.TIMESTAMP)
	@Column(name = "UPDATED")
	private Date updated;

	@Transient
	private String type = Resources.BPH_TYPE_BUSINESSOBJECT;

	@Transient
	private ApprovalStatus approvalStatus = ApprovalStatus.UNDEFINED;

	@Transient
	private FieldUsageStatus fieldUsageStatus = FieldUsageStatus.OK;

	//173166
	@Transient
	public boolean exposeTableUsages = false;
	
	// TODO make the constructor require a name
	public BusinessObject() {
	}
	
	@JsonProperty(value = "id")
	public void setBusinessObjectId(Integer businessObjectId) {
		this.businessObjectId = businessObjectId;
	}

	@JsonProperty(value = "id")
	public Integer getBusinessObjectId() {
		return businessObjectId;
	}

	public void setUsedInProcessSteps(List<ProcessStep> usedInProcessSteps) {
		this.usedInProcessSteps = usedInProcessSteps;
	}

	@JsonIgnore
	public List<ProcessStep> getUsedInProcessSteps() {
		if (usedInProcessSteps != null) {
			Collections.sort(usedInProcessSteps);
		} else {
			usedInProcessSteps = new ArrayList<ProcessStep>(0);
		}

		return usedInProcessSteps;
	}

	public void setTables(List<Table> tables) {
		this.tables = tables;
	}

	public List<Table> getTables() {
		if (tables != null) {
			Collections.sort(tables);
		} else {
			tables = new ArrayList<Table>(0);
		}

		return tables;
	}

	@JsonManagedReference("tableusage-businessobject")
	public void setUsages(List<TableUsage> usages) {
		this.usages = usages;
	}
	
	/** (173166) we do not want to send the usages all the time because this generates a huge JSON response for the BO tab.
	*   the isTreeObject field is just a quick fix - we probably want multiple REST calls to get the details instead of putting it all into one JSON response*/ 
	@JsonManagedReference("tableusage-businessobject")
	public List<TableUsage> getUsages() {		
		if(usages == null) {
			usages = new ArrayList<TableUsage>(0);
		}
		
//		Collections.sort(usages);    //(173166)	already sorted by named query TableUsage.getAllForBoAndStep
		
		//(173166) quick and dirty (!) way to avoid the usages are also transferred to the business object list tab, currently we need the usage in the tree only.
		//         I had to introduce the getUsagesInternal method to avoide side effects in ImportTableUsageProvider (OutOfMemory) BphService and BusinessObjectService
		if(!this.exposeTableUsages) {
			return new ArrayList<TableUsage>(0);
		}

		return usages;
	}

	//173166 - introduced this method to allow the usages can be omitted in the public version  
	@JsonIgnore
	public List<TableUsage> getUsagesInternal() {
		if(usages == null) {
			usages = new ArrayList<TableUsage>(0);
		}
		
		return usages;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		if (name != null && name.length() > Resources.BDR_LENGTH_BO_NAME) {
			this.name = name.substring(0, Resources.BDR_LENGTH_BO_NAME);
		} else {
			this.name = name;
		}
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		if (shortName != null && shortName.length() > Resources.BDR_LENGTH_BO_SHORTNAME) {
			this.shortName = shortName.substring(0, Resources.BDR_LENGTH_BO_SHORTNAME);
		} else {
			this.shortName = shortName;
		}
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
		this.type = Resources.BPH_TYPE_BUSINESSOBJECT;
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

	public void updateFrom(BusinessObject bo) {
		this.name = bo.getName();
		this.shortName = bo.getShortName();
		this.description = bo.getDescription();
		this.updated = bo.getUpdated();
		this.approvalStatus = bo.getApprovalStatus();
		setType(bo.getType());
	}

	public TableUsage getUsage(ProcessStep processStep, Table table) {
		for (TableUsage tableUsage : usages) {
			if (tableUsage.getProcessStep().equals(processStep) && tableUsage.getTable().equals(table)) {
				return tableUsage;
			}
		}
		return null;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (o == this)
			return true;
		if (o.getClass() != getClass())
			return false;
		return (((BusinessObject) o).getBusinessObjectId().equals(businessObjectId));
	}

	@Override
	public int hashCode() {
		return (businessObjectId == null) ? 0 : businessObjectId;
	}

	@Override
	public int compareTo(BusinessObject bo) {
		if (name != null && bo.getName() != null) {
			return name.compareTo(bo.getName());
		}

		return 0;
	}
}