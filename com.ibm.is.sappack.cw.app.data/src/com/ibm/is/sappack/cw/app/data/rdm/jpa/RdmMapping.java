package com.ibm.is.sappack.cw.app.data.rdm.jpa;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.FetchType;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity(name = "RDMMAPPING")
public class RdmMapping implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "RDMID")
	private String rdmId;
	
	@Column(name = "RDMVERSIONID", nullable = false)
	private String rdmVersionId;
	
	@Column(name = "NAME", nullable = false)
	private String name;

	@Column(name = "VERSION")
	private String version;
	
	// Source RDM set base ID
	@Column(name = "SOURCERDMID")
	private String sourceRdmId;

	// Target RDM set base ID
	@Column(name = "TARGETRDMID")
	private String targetRdmId;

	@Temporal(value = TemporalType.TIMESTAMP)
	@Column(name = "LASTLOAD")
	private Date lastLoad;
	
	@OneToOne(optional = false, fetch = FetchType.LAZY)
	private TranscodingTable transcodingTable;
	
	@Column(name ="VALUEMAPPINGS") 
	private int valueMappings;
	
	@Column(name="SOURCEKEYS")
	private int sourceKeys;
	
	// empty default constructor
	public RdmMapping() {
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

	public String getSourceRdmId() {
		return this.sourceRdmId;
	}

	public void setSourceRdmId(String sourceRdmId) {
		this.sourceRdmId = sourceRdmId;
	}

	public String getTargetRdmId() {
		return this.targetRdmId;
	}

	public void setTargetRdmId(String targetRdmId) {
		this.targetRdmId = targetRdmId;
	}

	public TranscodingTable getTranscodingTable() {
		return this.transcodingTable;
	}

	public void setTranscodingTable(TranscodingTable transcodingTable) {
		this.transcodingTable = transcodingTable;
	}
	public void setLastLoad(Date lastLoad) {
		this.lastLoad = lastLoad;
	}

	public Date getLastLoad() {
		return lastLoad;
	}

	public void setValueMappings(int valueMappings) {
		this.valueMappings = valueMappings;
	}

	public int getValueMappings() {
		return valueMappings;
	}

	public void setSourceKeys(int sourceKeys) {
		this.sourceKeys = sourceKeys;
	}

	public int getSourceKeys() {
		return sourceKeys;
	}

	public void setRdmVersionId(String rdmVersionId) {
		this.rdmVersionId = rdmVersionId;
	}

	public String getRdmVersionId() {
		return rdmVersionId;
	}
}
