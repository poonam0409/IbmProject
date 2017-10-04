//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2009, 2011                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.is.sappack.gen.tools.sap.importer.helper.field
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.tools.sap.importer.helper.field;

import com.ibm.is.sappack.gen.tools.sap.importer.helper.datatype.sap.SapDataType;
import com.ibm.is.sappack.gen.tools.sap.importer.helper.datatype.sap.SapDataTypeFactory;

public abstract class AbstractField {

	protected SapDataType dataType;
	private String fieldName;
	private String originalFieldName;
	private String fieldDescription;
	private String fieldLabel;
//	private int length;
//	private int decimals;
	boolean isKey;
	boolean isNullable;
	protected int displayLength = -1;
//	private int varcharLengthFactor = 1;
	private String checkTable;

	static String copyright() {
		return com.ibm.is.sappack.gen.tools.sap.importer.helper.field.Copyright.IBM_COPYRIGHT_SHORT;
	}

	public AbstractField(String fieldName, String fieldDescription, String fieldLabel, String dataElementName, String domainName, String dataTypeName, char sapBaseDataType, int length, int intLength, int decimals, boolean isKey, boolean isNullable) {
		this.fieldName = fieldName;
		this.fieldDescription = fieldDescription;
		this.fieldLabel = fieldLabel;
//		this.length = length;
//		this.decimals = decimals;
		this.isKey = isKey;
		this.isNullable = isNullable;
		// updateDataType(dataTypeName, sapBaseDataType);
		this.dataType = SapDataTypeFactory.createDataType(dataElementName, domainName, dataTypeName, sapBaseDataType, length, intLength, decimals);
	}

	/*
	 * public void updateDataType(String dataTypeName, char sapBaseDataType) {
	 * this.dataType = SapDataTypeFactory.createDataType(dataElementName,
	 * domainName, dataTypeName, sapBaseDataType, this.length, intLength,
	 * decimals); }
	 */

	public String getFieldName() {
		return this.fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getFieldDescription() {
		return this.fieldDescription;
	}

	public void setFieldDescription(String fieldDescription) {
		this.fieldDescription = fieldDescription;
	}

	public String getFieldLabel() {
		return this.fieldLabel;
	}

	public void setFieldLabel(String fieldLabel) {
		this.fieldLabel = fieldLabel;
	}

	public void setNullable(boolean nullable) {
		this.isNullable = nullable;
	}

	/*
	public int getLength() {
		if (this.dataType instanceof SapDataElement) {
			return ((SapDataElement) this.dataType).getLength();
		}
		return this.length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getDecimals() {
		if (this.dataType instanceof SapDataElement) {
			return ((SapDataElement) this.dataType).getDecimals();
		}
		return this.decimals;
	}

	public void setDecimals(int decimals) {
		this.decimals = decimals;
	}
	*/

	public SapDataType getDataType() {
		return this.dataType;
	}

	public boolean isKey() {
		return this.isKey;
	}

	public boolean isNullable() {
		return this.isNullable;
	}

	public void setIsKey(boolean isKey) {
		this.isKey = isKey;
	}

	public void setCheckTable(String checkTable) {
		this.checkTable = checkTable;
	}

	public String getCheckTable() {
		return this.checkTable;
	}

	public boolean hasCheckTable() {
		return ((this.checkTable != null) && (this.checkTable.trim().length() > 0));
	}
	
	public void setOriginalSAPFieldName(String fieldName) {
		this.originalFieldName = fieldName;
	}
	
	public String getOriginalSAPFieldName() {
		return this.originalFieldName;
	}

}
