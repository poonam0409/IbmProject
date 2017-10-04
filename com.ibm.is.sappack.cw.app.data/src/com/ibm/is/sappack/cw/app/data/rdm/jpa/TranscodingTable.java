package com.ibm.is.sappack.cw.app.data.rdm.jpa;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity(name = "TCTABLE")
public class TranscodingTable implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "NAME")
	private String name;
	
	@Temporal(value = TemporalType.TIMESTAMP)
	@Column(name = "LASTLOAD")
	private Date lastLoad;
	
	// Status: empty, complete, incomplete
	@Column(name="CONDITION")
	private char cond = ' ';

	public TranscodingTable(String name) {
		this.setName(name);
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getLastLoad() {
		return this.lastLoad;
	}

	public void setLastLoad(Date lastLoad) {
		this.lastLoad = lastLoad;
	}

	public void setCond(char cond) {
		this.cond = cond;
	}

	public char getCond() {
		return cond;
	}
}
