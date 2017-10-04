package com.ibm.is.sappack.cw.app.data.rdm.jpa;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonManagedReference;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.ibm.is.sappack.cw.app.data.Resources;
import com.ibm.is.sappack.cw.app.data.rdm.ITable;
import com.ibm.is.sappack.cw.app.data.rdm.ReferenceTableType;
import com.ibm.is.sappack.cw.app.data.rdm.TableStatus;
import com.ibm.is.sappack.cw.app.data.rdm.TableStatusSerializer;

@Entity(name = "REFTABLE")
public class ReferenceTable implements ITable, Serializable {

	private static final String EMPTY_STRING = "";

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	@Column(name = "ID", updatable = false)
	private Integer referenceTableId;

	@Column(name = "NAME")
	private String name;
	
	@Column(name = "DESCRIPTION")
	private String description;

	@Column(name = "SAPNAME")
	private String sapName;

	@Column(name = "LEGACYID")
	private String legacyId;

	@Column(name = "TYPE")
	@Enumerated(EnumType.STRING)
	private ReferenceTableType tableType;

	@Temporal(value = TemporalType.TIMESTAMP)
	@Column(name = "LASTLOAD")
	private Date lastLoad;

	@Column(name = "ROWCOUNT")
	private int rowCount = 0;

	@OneToOne(fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
	private RdmSet targetRdmSet;

	@OneToMany(mappedBy = "referenceTable", fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
	private Collection<RdmSet> sourceRdmSets;

	@OneToOne(optional = false, fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
	private TranscodingTable transcodingTable;

	@OneToOne(fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
	private TextTable textTable;

	@OneToMany(mappedBy = "referenceTable", fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
	private Collection<ReferenceTableColumn> columns;

	@OneToMany(mappedBy = "referenceTable", fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
	private Collection<SourceDataCollectionRule> sourceDataCollectionRules;

	@Transient
	private boolean selected = false;

	@Transient
	private TableStatus tableStatus = TableStatus.NOT_LOADED;

	// empty default constructor
	public ReferenceTable() {
		this.columns = new TreeSet<ReferenceTableColumn>();
		this.sourceDataCollectionRules = new HashSet<SourceDataCollectionRule>();
		this.sourceRdmSets = new HashSet<RdmSet>();
	}

	public Integer getReferenceTableId() {
		return this.referenceTableId;
	}

	public void setReferenceTableId(Integer referenceTableId) {
		this.referenceTableId = referenceTableId;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String getLegacyId() {
		return this.legacyId;
	}

	public void setLegacyId(String legacyId) {
		this.legacyId = legacyId;
	}

	public Date getLastLoad() {
		return this.lastLoad;
	}

	public void setLastLoad(Date lastLoad) {
		this.lastLoad = lastLoad;
	}

	public void setTargetRdmSet(RdmSet rdmSet) {
		this.targetRdmSet = rdmSet;
	}

	public RdmSet getTargetRdmSet() {
		return targetRdmSet;
	}

	@JsonManagedReference
	public void setSourceRdmSets(Collection<RdmSet> sourceRdmSets) {
		this.sourceRdmSets = sourceRdmSets;
	}

	@JsonManagedReference
	public Collection<RdmSet> getSourceRdmSets() {
		return sourceRdmSets;
	}

	public RdmSet getSourceRdmSetForLegacyId(String legacyId) {
		if (sourceRdmSets != null) {
			for (RdmSet rdmSet : sourceRdmSets) {
				if (legacyId.equals(rdmSet.getLegacyId())) {
					return rdmSet;
				}
			}
		}

		return null;
	}

	public void setSapName(String sapName) {
		this.sapName = sapName;
	}

	@Override
	public String getSapName() {
		return this.sapName;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setTranscodingTable(TranscodingTable transcodingTable) {
		this.transcodingTable = transcodingTable;
	}

	public TranscodingTable getTranscodingTable() {
		return this.transcodingTable;
	}

	public void setTextTable(TextTable textTable) {
		this.textTable = textTable;
	}

	public TextTable getTextTable() {
		return this.textTable;
	}

	@JsonManagedReference
	public void setColumns(Collection<ReferenceTableColumn> referenceTableColumns) {
		this.columns = referenceTableColumns;
	}

	@Override
	@JsonManagedReference
	public Collection<ReferenceTableColumn> getColumns() {
		return this.columns;
	}

	@Override
	@JsonIgnore
	public Collection<ReferenceTableColumn> getNonMandtColumns() {
		if (this.columns == null) {
			return null;
		}

		Collection<ReferenceTableColumn> nonMandtColumns = new ArrayList<ReferenceTableColumn>();
		for (ReferenceTableColumn column : this.columns) {
			if (column.getDomain().equalsIgnoreCase(Resources.MANDT_DOMAIN)) {
				continue;
			}

			nonMandtColumns.add(column);
		}

		return nonMandtColumns;
	}

	@JsonIgnore
	public Collection<ReferenceTableColumn> getMandtColumns() {
		if (this.columns == null) {
			return null;
		}

		Collection<ReferenceTableColumn> mandtColumns = new ArrayList<ReferenceTableColumn>();
		for (ReferenceTableColumn column : this.columns) {
			if (!column.getDomain().equalsIgnoreCase(Resources.MANDT_DOMAIN)) {
				continue;
			}

			mandtColumns.add(column);
		}

		return mandtColumns;
	}

	public void addColumn(ReferenceTableColumn referenceTableColumn) {
		referenceTableColumn.setReferenceTable(this);
		this.columns.add(referenceTableColumn);
	}

	public boolean hasTextTable() {
		return (this.textTable != null);
	}

	@Override
	@JsonSerialize(using = TableStatusSerializer.class)
	public TableStatus getTableStatus() {
		return this.tableStatus;
	}

	@Override
	public void setTableStatus(TableStatus tableStatus) {
		this.tableStatus = tableStatus;
	}

	@JsonProperty(value = "selected")
	public boolean isSelected() {
		return this.selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	@Override
	public int getRowCount() {
		return this.rowCount;
	}

	@Override
	public void setRowCount(int rowCount) {
		this.rowCount = rowCount;
	}

	@JsonProperty(value = "targetRdmSetName")
	public String getTargetRdmSetName() {
		if (this.getTargetRdmSet() != null) {
			return this.getTargetRdmSet().getName();
		}

		return EMPTY_STRING;
	}

	public void setTargetRdmSetName(String name) {
		if (this.getTargetRdmSet() == null) {
			this.setTargetRdmSet(new RdmSet());
		}

		this.getTargetRdmSet().setName(name);
	}

	@JsonProperty(value = "rdmSetCreated")
	public Date getRdmSetCreated() {
		if (this.getTargetRdmSet() != null) {
			return this.getTargetRdmSet().getCreated();
		}

		return null;
	}

	public void setRdmSetCreated(Date created) {
		if (this.getTargetRdmSet() == null) {
			this.setTargetRdmSet(new RdmSet());
		}

		this.getTargetRdmSet().setCreated(created);
	}

	@JsonManagedReference
	public Collection<SourceDataCollectionRule> getSourceDataCollectionRules() {
		return sourceDataCollectionRules;
	}

	@JsonManagedReference
	public void setSourceDataCollectionRules(Collection<SourceDataCollectionRule> sourceDataCollectionRules) {
		this.sourceDataCollectionRules = sourceDataCollectionRules;
	}

	public void addSourceDataCollectionRule(SourceDataCollectionRule rule) {
		rule.setReferenceTable(this);
		this.sourceDataCollectionRules.add(rule);
	}

	public void setTableType(ReferenceTableType tableType) {
		this.tableType = tableType;
	}

	@Override
	public ReferenceTableType getTableType() {
		return this.tableType;
	}
}
