package com.ibm.is.sappack.cw.app.data.config.jpa;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.Column;

@Entity(name = "SETTING")
public class Setting implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	@Column(name = "SETTINGID", updatable = false)
	private Integer settingId;
	
	@Column(name = "NAME", nullable = false, unique = true)
	private String name;
	
	@Column(name = "VALUE")
	private String value;
	
	// empty default constructor
	public Setting() {
		
	}

	// getter and setter methods
	public Integer getSettingId() {
		return this.settingId;
	}

	public void setSettingId(Integer settingId) {
		this.settingId = settingId;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void updateFrom(Setting setting) {
		this.name = setting.getName();
		this.value = setting.getValue();
	}
}
