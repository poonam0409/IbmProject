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

@Entity(name = "SRCDATACOLRULE")
public class SourceDataCollectionRule implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	@Column(name = "RULEID", updatable = false)
	private Integer sourceDataCollectionRuleId;

	@Column(name="DATATABLENAME")
	private String dataTableName;

	@Column(name="COLLECTIONRULE")
	private String collectionRule;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private ReferenceTable referenceTable;
	
	@Transient
	private String relatedCheckTableName;
	
	public Integer getSourceDataCollectionRuleId() {
		return sourceDataCollectionRuleId;
	}

	public void setSourceDataCollectionRuleId(Integer sourceDataCollectionRuleId) {
		this.sourceDataCollectionRuleId = sourceDataCollectionRuleId;
	}

	public String getDataTableName() {
		return dataTableName;
	}

	public void setDataTableName(String dataTableName) {
		this.dataTableName = dataTableName;
	}

	public String getCollectionRule() {
		return collectionRule;
	}

	public void setCollectionRule(String collectionRule) {
		this.collectionRule = collectionRule;
	}

	@JsonBackReference
	public ReferenceTable getReferenceTable() {
		return referenceTable;
	}

	@JsonBackReference
	public void setReferenceTable(ReferenceTable referenceTable) {
		this.referenceTable = referenceTable;
	}

	public String getRelatedCheckTableName() {
		return relatedCheckTableName;
	}

	public void setRelatedCheckTableName(String relatedCheckTableName) {
		this.relatedCheckTableName = relatedCheckTableName;
	}
}
