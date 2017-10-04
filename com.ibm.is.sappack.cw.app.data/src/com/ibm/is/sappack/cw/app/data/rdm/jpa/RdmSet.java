package com.ibm.is.sappack.cw.app.data.rdm.jpa;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.codehaus.jackson.annotate.JsonBackReference;
import org.codehaus.jackson.annotate.JsonProperty;

@Entity(name = "RDMSET")
public class RdmSet implements Serializable {

	private static final long serialVersionUID = 1L;

	// This is the RDM set's base ID
	@Id
	@Column(name = "RDMID")
	private String rdmId;

	@Column(name = "NAME", nullable = false)
	private String name;

	@Column(name = "VERSION")
	private String version;

	@Column(name = "VERSIONID")
	private String versionId;

	// Used for source sets
	@Column(name = "LEGACYID")
	private String legacyId;

	// Used for source sets
	@ManyToOne(fetch = FetchType.LAZY)
	private ReferenceTable referenceTable;

	// Used for source sets. The initial (empty) mappings we create don'T get stored as a JPA entity until we load their
	// content, so we just store the name here.
	@Column(name = "INITIALMAPPINGNAME")
	private String initialMappingName;

	@Temporal(value = TemporalType.TIMESTAMP)
	@Column(name = "CREATED")
	private Date created;

	@Basic
	@Column(name = "UPTODATE")
	private boolean uptodate;

	// empty default constructor
	public RdmSet() {
	}

	public String getRdmId() {
		return this.rdmId;
	}

	public void setRdmId(String rdmId) {
		this.rdmId = rdmId;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVersion() {
		return this.version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public void setVersionId(String versionId) {
		this.versionId = versionId;
	}

	public String getVersionId() {
		return versionId;
	}

	public String getLegacyId() {
		return legacyId;
	}

	public void setLegacyId(String legacyId) {
		this.legacyId = legacyId;
	}

	public Date getCreated() {
		return this.created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	@JsonProperty(value = "uptodate")
	public boolean isUptodate() {
		return this.uptodate;
	}

	@JsonProperty(value = "uptodate")
	public void setUptodate(boolean uptodate) {
		this.uptodate = uptodate;
	}

	@JsonBackReference
	public void setReferenceTable(ReferenceTable referenceTable) {
		this.referenceTable = referenceTable;
	}

	@JsonBackReference
	public ReferenceTable getReferenceTable() {
		return referenceTable;
	}

	public void setInitialMappingName(String initialMappingName) {
		this.initialMappingName = initialMappingName;
	}

	public String getInitialMappingName() {
		return initialMappingName;
	}
}
