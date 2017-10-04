package com.ibm.is.sappack.cw.app.data.rdm.jpa;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.codehaus.jackson.annotate.JsonBackReference;

import com.ibm.is.sappack.cw.app.data.rdm.IColumn;

@Entity(name = "TXTTABLECOL")
public class TextTableColumn implements Serializable, IColumn {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	@Column(name = "COLUMNID")
	private int columnId;

	@Column(name = "NAME")
	private String name;

	@Column(name = "SAPNAME")
	private String sapName;

	@Column(name = "LENGTH")
	private int length;

	@Column(name = "DOMAIN")
	private String domain;

	@Column(name = "ISKEY")
	private boolean isKey;
	
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private TextTable textTable;

	// empty default constructor
	public TextTableColumn() {

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

	@Override
    public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	@JsonBackReference
	public TextTable getTextTable() {
		return this.textTable;
	}

	@JsonBackReference
	public void setTextTable(TextTable textTable) {
		this.textTable = textTable;
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
}
