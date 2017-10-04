package com.ibm.is.sappack.cw.app.data.rdm;

public class SourceDataId {

	private String legacyId;
	private String legacyIdDescription;
	private int referenceTableId;
	
	public void setLegacyId(String legacyId) {
		this.legacyId = legacyId;
	}
	
	public String getLegacyId() {
		return legacyId;
	}

	public void setLegacyIdDescription(String legacyIdDescription) {
		this.legacyIdDescription = legacyIdDescription;
	}

	public String getLegacyIdDescription() {
		return legacyIdDescription;
	}
	
	public void setReferenceTableId(int referenceTableId) {
		this.referenceTableId = referenceTableId;
	}
	
	public int getReferenceTableId() {
		return referenceTableId;
	}
}
