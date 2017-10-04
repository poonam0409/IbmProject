//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2011, 2014                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.is.sappack.gen.tools.sap.utilities
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.tools.sap.utilities;


import com.ibm.db.models.logical.Attribute;
import com.ibm.is.sappack.gen.tools.sap.importer.helper.field.AbstractField;


public class ABAPColumn {

	static String copyright() {
		return com.ibm.is.sappack.gen.tools.sap.utilities.Copyright.IBM_COPYRIGHT_SHORT;
	}

	private String tableName;
	private AbstractField field;
	// set this field only if the name of the matching foreign key column is different
	// than columnName
	// e. g. T002~SPRAS must be joined with T002T~SPRSL
	private String foreignKeyColumnName;
	private Attribute attribute;

	public ABAPColumn(String tableName, AbstractField field) {
		this.tableName = tableName;
		this.field = field;
	}

	public String getTableName() {
		return this.tableName;
	}

	public String getColumnName() {
		return this.field.getFieldName();
	}

	public AbstractField getField() {
		return this.field;
	}

	public String getForeignKeyColumnName() {
		return this.foreignKeyColumnName;
	}

	public void setForeignKeyColumnName(String foreignKeyColumnName) {
		this.foreignKeyColumnName = foreignKeyColumnName;
	}

	public void setAttribute(Attribute attr) {
		this.attribute = attr;
	}

	public Attribute getAttribute() {
		return this.attribute;
	}

}
