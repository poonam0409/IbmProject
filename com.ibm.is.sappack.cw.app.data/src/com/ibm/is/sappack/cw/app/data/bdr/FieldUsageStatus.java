package com.ibm.is.sappack.cw.app.data.bdr;

public enum FieldUsageStatus {
	REQUIRED_BUT_NEVER_WRITTEN(0),
	READ_BUT_NEVER_WRITTEN(1),
	MULTIPLE_WRITES(2),
	REQUIRED_BUT_FOLLOWUP(3),
	REQUIRED_BUT_NOT_IN_SCOPE(4),
	REQUIRED_BUT_BLANK(5),
	OK(-1);
	
	private int value;
	
	private FieldUsageStatus(int value) {
		this.value = value;
	}
	
	public static FieldUsageStatus fromStatusCode(int statusCode) {
		switch (statusCode) {
			case 0: return REQUIRED_BUT_NEVER_WRITTEN;
			case 1: return READ_BUT_NEVER_WRITTEN;
			case 2: return MULTIPLE_WRITES;
			case 3: return REQUIRED_BUT_FOLLOWUP;
			case 4: return REQUIRED_BUT_NOT_IN_SCOPE;
			case 5: return REQUIRED_BUT_BLANK;
			case -1: return OK;
			default:
				throw new IllegalArgumentException();
		}
	}
	
	public static int toStatusCode(FieldUsageStatus status) {
		return status.value;
	}
}
