package com.ibm.is.sappack.cw.app.services.bdr;

public enum BphImportFileType {
	SOLUTION_MANAGER(".mpx"),
	BDR_CSV(".csv");

	private String fileExtension;
	
	private BphImportFileType(String fileExtension) {
		this.fileExtension = fileExtension;
	}
	
	public String getFileExtension() {
		return this.fileExtension;
	}
}
