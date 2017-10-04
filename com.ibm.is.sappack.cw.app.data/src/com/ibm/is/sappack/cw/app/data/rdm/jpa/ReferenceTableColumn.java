package com.ibm.is.sappack.cw.app.data.rdm.jpa;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.codehaus.jackson.annotate.JsonBackReference;

import com.ibm.is.sappack.cw.app.data.rdm.IColumn;

@Entity(name = "REFTABLECOL")
public class ReferenceTableColumn implements Serializable, IColumn, Comparable<ReferenceTableColumn> {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	@Column(name = "COLUMNID")
	private int columnId;

	@Column(name = "NAME")
	private String name;
	
	@Column(name="SAPNAME")
	private String sapName;
	
	@Column(name="DOMAIN")
	private String domain;
	
	@Column(name="TranscodingTable_Source")
	private String transcodingTableSrcName;
	
	@Column(name="TranscodingTable_Target")
	private String transcodingTableTgtName;
	
	@Transient
	private boolean isKey;
	
	@Transient
	private int length;
	
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private ReferenceTable referenceTable;

	// empty default constructor
	public ReferenceTableColumn() {
		
	}

	public int getColumnId() {
		return columnId;
	}
	
	public void setColumnId(int columnId) {
		this.columnId = columnId;
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public void setSapName(String sapName) {
		this.sapName = sapName;
	}
	
	@Override
	public String getSapName() {
		return this.sapName;
	}

	@JsonBackReference
	public ReferenceTable getReferenceTable() {
		return referenceTable;
	}

	@JsonBackReference
	public void setReferenceTable(ReferenceTable referenceTable) {
		this.referenceTable = referenceTable;
	}

	@Override
	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}
	
	public void setDomain(String domain) {
		this.domain = domain;
	}
	
	@Override
	public String getDomain() {
		return this.domain;
	}

	public boolean isKey() {
		return isKey;
	}

	public void setKey(boolean isKey) {
		this.isKey = isKey;
	}
	
	public void setTranscodingTableSrcName(String transcodingTableSrcName) {
		this.transcodingTableSrcName = transcodingTableSrcName;
	}

	public String getTranscodingTableSrcName() {
		return transcodingTableSrcName;
	}

	public void setTranscodingTableTgtName(String transcodingTableTgtName) {
		this.transcodingTableTgtName = transcodingTableTgtName;
	}

	public String getTranscodingTableTgtName() {
		return transcodingTableTgtName;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof ReferenceTableColumn) {
			if (this.compareTo((ReferenceTableColumn) o) == 0) {
				return true;
			}
		}
		
		return false;
	}

	@Override
	public int compareTo(ReferenceTableColumn o) {	
		return this.sapName.compareTo(o.sapName);
	}
}
