package com.ibm.is.sappack.cw.app.data.bdr.jpa;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
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

import com.ibm.is.sappack.cw.app.data.Resources;

@Entity(name = "BPHTABLE")
public class Table implements Serializable, Comparable<Table> {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	@Column(name = "ID", updatable = false)
	private Integer tableId;

	@ManyToMany(targetEntity = BusinessObject.class, mappedBy = "tables", cascade = { CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH })
	private List<BusinessObject> usedInBusinessObjects;

	@OneToMany(mappedBy = "table", fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
	private List<Field> fields;

	@OneToMany(mappedBy = "table", fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
	private List<TableUsage> usages;

	@Column(name = "NAME", length = Resources.BDR_LENGTH_TABLE_NAME * Resources.BDR_LENGTH_BYTE_MULTIPLIER)
	private String name;

	@Column(name = "DESCRIPTION", length = Resources.BDR_LENGTH_DESCRIPTION * Resources.BDR_LENGTH_BYTE_MULTIPLIER)
	private String description;

	@Temporal(value = TemporalType.TIMESTAMP)
	@Column(name = "UPDATED")
	private Date updated;

	@Transient
	private String type = Resources.BPH_TYPE_TABLE;

	// TODO make the constructor require a name
	public Table() {
	}
	
	@JsonProperty(value = "id")
	public void setTableId(Integer tableId) {
		this.tableId = tableId;
	}

	@JsonProperty(value = "id")
	public Integer getTableId() {
		return tableId;
	}

	public void setUsedInBusinessObjects(List<BusinessObject> usedInBusinessObjects) {
		this.usedInBusinessObjects = usedInBusinessObjects;
	}

	@JsonIgnore
	public List<BusinessObject> getUsedInBusinessObjects() {
		if (usedInBusinessObjects != null) {
			Collections.sort(usedInBusinessObjects);
		} else {
			usedInBusinessObjects = new ArrayList<BusinessObject>(0);
		}

		return usedInBusinessObjects;
	}

	@JsonManagedReference("field-table")
	public void setFields(List<Field> fields) {
		this.fields = fields;
	}

	/** 173166 added jsonignore to avoid large JSON response */
	@JsonIgnore
	@JsonManagedReference("field-table")
	public List<Field> getFields() {
		if (fields != null) {
			Collections.sort(fields);
		} else {
			fields = new ArrayList<Field>(0);
		}

		return fields;
	}

	@JsonManagedReference("table-tableusage")
	public void setUsages(List<TableUsage> usages) {
		this.usages = usages;
	}

	/** 173166 added jsonignore to avoid large JSON response */
	@JsonIgnore
	@JsonManagedReference("table-tableusage")
	public List<TableUsage> getUsages() {
		if (usages != null) {
			Collections.sort(usages);
		} else {
			usages = new ArrayList<TableUsage>(0);
		}

		return usages;
	}

	public void setName(String name) {
		if (name != null && name.length() > Resources.BDR_LENGTH_TABLE_NAME) {
			this.name = name.substring(0, Resources.BDR_LENGTH_TABLE_NAME);
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
		this.type = Resources.BPH_TYPE_TABLE;
	}

	public void updateFrom(Table bphTable) {
		this.name = bphTable.getName();
		this.description = bphTable.getDescription();
		this.fields = bphTable.getFields();
		this.updated = bphTable.getUpdated();
		setType(bphTable.getType());
	}

	@Override
	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (o == this)
			return true;
		if (o.getClass() != getClass())
			return false;
		return (((Table) o).getTableId().equals(tableId));
	}

	@Override
	public int hashCode() {
		return (tableId == null) ? 0 : tableId;
	}

	@Override
	public int compareTo(Table t) {
		if (name != null && t.getName() != null) {
			return name.compareTo(t.getName());
		}

		return 0;
	}
}
