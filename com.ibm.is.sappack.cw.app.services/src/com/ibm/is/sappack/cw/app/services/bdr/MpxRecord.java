package com.ibm.is.sappack.cw.app.services.bdr;

public class MpxRecord {
	
	/*
	 * SAP Record looks like:
	 * 70;2;0050563F44341ED28CFFA72176F97856;Business_Scenarios;02;500;;0;;;;;0;0;0;;
	 *  ^											 ^			 ^
	 *  |											 |			 |
	 * recordIdentifier								name	   level
	 */
	
	private int level;
	private String name;
	private String recordIdentifier;
	
	public MpxRecord(String recordIdentifier, String name, int level) {
		this.recordIdentifier = recordIdentifier;
		this.name = name;
		this.level = level;
	}
	
	public MpxRecord() {
		this.recordIdentifier = "";
		this.name = "";
		this.level = 0;
	}
	
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getRecordIdentifier() {
		return recordIdentifier;
	}
	public void setRecordIdentifier(String recordIdentifier) {
		this.recordIdentifier = recordIdentifier;
	}
	
}
