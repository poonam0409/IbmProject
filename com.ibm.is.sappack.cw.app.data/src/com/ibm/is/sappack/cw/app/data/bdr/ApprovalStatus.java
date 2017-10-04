package com.ibm.is.sappack.cw.app.data.bdr;

public enum ApprovalStatus {
	DRAFT(0),
	PENDING_APPROVAL(1),
	APPROVED(2),
	MIXED(3),
	UNDEFINED(-1);
	
	private int value;
	
	private ApprovalStatus(int value) {
		this.value = value;
   }
	
	public static ApprovalStatus fromStatusCode(final int statusCode) {
		switch (statusCode) {
			case 0: return DRAFT;
			case 1: return PENDING_APPROVAL;
			case 2: return APPROVED;
			case 3: return MIXED;
			case -1: return UNDEFINED;
			default: return UNDEFINED;
		}
	}
	
	public static int toStatusCode(ApprovalStatus status) {
		return status.value;
	}
}
