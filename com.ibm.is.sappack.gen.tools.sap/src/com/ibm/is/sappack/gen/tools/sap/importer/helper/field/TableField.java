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

public class TableField extends AbstractField {
	
	static String copyright()
	{ return com.ibm.is.sappack.gen.tools.sap.importer.helper.field.Copyright.IBM_COPYRIGHT_SHORT; }
	
	public TableField(String fieldName, String fieldDescription, String fieldLabel, String dataElementName, String domainName, String dataTypeName, char sapBaseDataType, int length, int intLength, int decimals, boolean isKey, boolean isNullable) {
		super(fieldName, fieldDescription, fieldLabel, dataElementName, domainName, dataTypeName, sapBaseDataType, length, intLength, decimals, isKey, isNullable);
	}	
}
