package com.ibm.is.sappack.cw.app.data.rdm;

public class RdmSetVersionInfo {
	private String versionNumber;
	private String baseId;
	private String versionId;
	
	public void setVersionNumber(String versionNumber) {
		this.versionNumber = versionNumber;
	}
	
	public String getVersionNumber() {
		return versionNumber;
	}

	public void setBaseId(String baseId) {
		this.baseId = baseId;
	}

	public String getBaseId() {
		return baseId;
	}

	public void setVersionId(String versionId) {
		this.versionId = versionId;
	}

	public String getVersionId() {
		return versionId;
	}

}
