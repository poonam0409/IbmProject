package com.ibm.is.sappack.cw.app.services;

public class SchemaMismatchException extends Exception {

	private static final long serialVersionUID = 1L;
	
	private String modelSchemaName = null;
	private String preConfiguredAreaCode = null;
	
	public SchemaMismatchException(String schemaName, String areaCode) {
		this.modelSchemaName = schemaName;
		this.preConfiguredAreaCode = areaCode;
	}
	
	public String getModelSchemaName() {
		return this.modelSchemaName;
	}
	
	public String getPreConfiguredAreaCode() {
		return this.preConfiguredAreaCode;
	}
}
