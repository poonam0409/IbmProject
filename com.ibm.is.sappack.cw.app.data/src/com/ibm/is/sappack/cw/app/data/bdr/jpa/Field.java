package com.ibm.is.sappack.cw.app.data.bdr.jpa;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.codehaus.jackson.annotate.JsonBackReference;
import org.codehaus.jackson.annotate.JsonManagedReference;

import com.ibm.is.sappack.cw.app.data.Resources;
import com.ibm.is.sappack.cw.app.data.SAPField;

@Entity(name = "FIELD")
public class Field implements Serializable, Comparable<Field> {

	private static final long serialVersionUID = 1L;
	
	// Sortable columns
	public static final List<String> BDR_FIELD_SORTABLE_COLUMNS
			= Arrays.asList("name", "checkTable", "recommended", "sapView", "description");

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	@Column(name = "ID", updatable = false)
	private Integer fieldId;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private Table table;

	@OneToMany(mappedBy = "field", fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
	private List<FieldUsage> usages;

	@Column(name = "NAME", length = Resources.BDR_LENGTH_FIELD_NAME * Resources.BDR_LENGTH_BYTE_MULTIPLIER)
	private String name;

	@Column(name = "CHECKTABLE", length = Resources.BDR_LENGTH_CHECK_TABLE * Resources.BDR_LENGTH_BYTE_MULTIPLIER)
	private String checkTable;

	@Column(name = "SAPVIEW", length = Resources.BDR_LENGTH_SAP_VIEW * Resources.BDR_LENGTH_BYTE_MULTIPLIER)
	private String sapView;

	@Column(name = "DESCRIPTION", length = Resources.BDR_LENGTH_DESCRIPTION * Resources.BDR_LENGTH_BYTE_MULTIPLIER)
	private String description;

	@Basic
	@Column(name = "REC")
	private boolean recommended = false;

	// TODO make the constructor require a name
	public Field() {
	}
	
	public void setFieldId(Integer fieldId) {
		this.fieldId = fieldId;
	}

	public Integer getFieldId() {
		return fieldId;
	}

	@JsonBackReference("field-table")
	public void setTable(Table table) {
		this.table = table;
	}

	@JsonBackReference("field-table")
	public Table getTable() {
		return table;
	}

	@JsonManagedReference("field-fieldusage")
	public void setUsages(List<FieldUsage> usages) {
		this.usages = usages;
	}

	@JsonManagedReference("field-fieldusage")
	public List<FieldUsage> getUsages() {
		if (usages != null) {
			Collections.sort(usages);
		} else{
			usages = new ArrayList<FieldUsage>(0);
		}

		return usages;
	}

	@SAPField(objectName = "name", sapName = "FIELDNAME")
	public void setName(String name) {
		if (name != null && name.length() > Resources.BDR_LENGTH_FIELD_NAME) {
			this.name = name.substring(0, Resources.BDR_LENGTH_FIELD_NAME);
		} else {
			this.name = name;
		}
	}

	@SAPField(objectName = "name", sapName = "FIELDNAME")
	public String getName() {
		return name;
	}

	@SAPField(objectName = "checkTable", sapName = "CHECKTABLE")
	public void setCheckTable(String checkTable) {
		this.checkTable = checkTable;
	}

	@SAPField(objectName = "checkTable", sapName = "CHECKTABLE")
	public String getCheckTable() {
		return checkTable;
	}

	public void setSapView(String sapView) {
		if (sapView != null && sapView.length() > Resources.BDR_LENGTH_SAP_VIEW) {
			this.sapView = sapView.substring(0, Resources.BDR_LENGTH_SAP_VIEW);
		} else {
			this.sapView = sapView;
		}
		this.sapView = sapView;
	}

	public String getSapView() {
		return sapView;
	}

	@SAPField(objectName = "description", sapName = "FIELDTEXT")
	public void setDescription(String description) {
		this.description = description;
	}

	@SAPField(objectName = "description", sapName = "FIELDTEXT")
	public String getDescription() {
		return description;
	}

	public void setRecommended(boolean recommended) {
		this.recommended = recommended;
	}

	public boolean getRecommended() {
		return recommended;
	}
	
	public void updateFrom(Field field) {
		this.table = field.getTable();
		this.usages = field.getUsages();
		this.name = field.getName();
		this.checkTable = field.getCheckTable();
		this.sapView = field.getSapView();
		this.description = field.getDescription();
		this.recommended = field.getRecommended();
	}

	@Override
	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (o == this)
			return true;
		if (o.getClass() != getClass())
			return false;
		return (((Field) o).getFieldId().equals(fieldId));
	}

	@Override
	public int hashCode() {
		return (fieldId == null) ? 0 : fieldId;
	}

	@Override
	public int compareTo(Field f) {
		if (name != null && f.getName() != null) {
			return name.compareTo(f.getName());
		}

		return 0;
	}
}
