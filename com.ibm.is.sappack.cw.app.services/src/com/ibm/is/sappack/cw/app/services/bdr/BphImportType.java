package com.ibm.is.sappack.cw.app.services.bdr;

public enum BphImportType {
	CSV_COMPLETE("csvComplete"),
	CSV_BOS("csvBOs"),
	CSV_PROCESSES("csvProcesses"),
	CSV_TABLES("csvTables"),
	MPX("mpx"),
	UNDEFINED("undefined");

	private String importType;
	
	private BphImportType(String importType) {
		this.importType = importType;
	}
	
	public String getImportType() {
		return this.importType;
	}
	
	public static BphImportType fromString(String string) {
		if (string.equalsIgnoreCase(MPX.getImportType())) {
			return MPX;
		} else if (string.equalsIgnoreCase(CSV_BOS.getImportType())) {
			return CSV_BOS;
		} else if (string.equalsIgnoreCase(CSV_COMPLETE.getImportType())) {
			return CSV_COMPLETE;
		} else if (string.equalsIgnoreCase(CSV_PROCESSES.getImportType())) {
			return CSV_PROCESSES;
		} else if (string.equalsIgnoreCase(CSV_TABLES.getImportType())) {
			return CSV_TABLES;
		} else {
			return UNDEFINED;
		}
	}
}