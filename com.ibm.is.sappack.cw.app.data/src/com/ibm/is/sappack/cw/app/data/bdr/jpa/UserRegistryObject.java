package com.ibm.is.sappack.cw.app.data.bdr.jpa;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class UserRegistryObject implements Serializable, Comparable<UserRegistryObject> {

	private static final long serialVersionUID = 1L;

	@Column(name = "UNIQUEID")
	private String uniqueId;

	@Column(name = "SECURITYNAME")
	private String securityName;

	@Column(name = "TYPE")
	private String type;

	// TODO make the constructor require a name, id and type
	
	public String getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}

	public String getSecurityName() {
		return securityName;
	}

	public void setSecurityName(String securityName) {
		this.securityName = securityName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public int compareTo(UserRegistryObject uro) {
		if (securityName != null && uro.getSecurityName() != null) {
			return securityName.compareTo(uro.getSecurityName());
		}

		return 0;
	}
}
