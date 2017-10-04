package com.ibm.is.sappack.cw.app.data.rdm.jpa;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonManagedReference;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.ibm.is.sappack.cw.app.data.Resources;
import com.ibm.is.sappack.cw.app.data.rdm.ITable;
import com.ibm.is.sappack.cw.app.data.rdm.ReferenceTableType;
import com.ibm.is.sappack.cw.app.data.rdm.TableStatus;
import com.ibm.is.sappack.cw.app.data.rdm.TableStatusSerializer;

@Entity(name = "TXTTABLE")
public class TextTable implements ITable, Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	@Column(name = "ID", updatable = false)
	private Integer textTableId;
	
	@Column(name = "NAME")
	private String name;
	
	@Column(name = "SAPNAME")
	private String sapName;

	@Column(name = "JOINCONDITION")
	private String joinCondition;

	@OneToMany(mappedBy = "textTable", fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	private Collection<TextTableColumn> columns;

	@Transient
	private TableStatus tableStatus = TableStatus.NOT_LOADED;

	@Transient
	private int rowCount = 0;

	// internally used variable - for a TextTable this is supposed to be always NULL
	@Transient
	private String legacyId = null;

	// empty default constructor
	public TextTable() {
		this.columns = new HashSet<TextTableColumn>();
	}

	public TextTable(String name) {
		this.setName(name);
	}

	public Integer getTextTableId() {
		return this.textTableId;
	}

	public void setTextTableId(Integer textTableId) {
		this.textTableId = textTableId;
	}
	
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getJoinCondition() {
		return this.joinCondition;
	}

	public void setJoinCondition(String joinCondition) {
		this.joinCondition = joinCondition;
	}

	public void setSapName(String sapName) {
		this.sapName = sapName;
	}

	@Override
	public String getSapName() {
		return this.sapName;
	}

	@Override
	public String getLegacyId() {
		return this.legacyId;
	}

	public void setLegacyId(String legacyId) {
		this.legacyId = legacyId;
		this.legacyId = null;
	}

	@JsonManagedReference
	public void setColumns(Collection<TextTableColumn> textTableColumns) {
		this.columns = textTableColumns;
	}

	@Override
	@JsonManagedReference
	public Collection<TextTableColumn> getColumns() {
		return this.columns;
	}

	@Override
	public Collection<TextTableColumn> getNonMandtColumns() {
		Collection<TextTableColumn> nonMandtColumns = new ArrayList<TextTableColumn>();
		for (TextTableColumn column : this.columns) {
			if (column.getDomain().equalsIgnoreCase(Resources.MANDT_DOMAIN)) {
				continue;
			}
			nonMandtColumns.add(column);
		}

		return nonMandtColumns;
	}

	public void addColumn(TextTableColumn textTableColumn) {
		textTableColumn.setTextTable(this);
		this.columns.add(textTableColumn);
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

	@Override
	public int getRowCount() {
		return this.rowCount;
	}

	@Override
	public void setRowCount(int rowCount) {
		this.rowCount = rowCount;
	}

	// internally used function - for a TextTable this is supposed to always return NULL
	@Override
	@JsonIgnore
	public ReferenceTableType getTableType() {
		return null;
	}
}
