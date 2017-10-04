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
// Module Name : com.ibm.is.sappack.gen.tools.sap.importer.helper.datatype.sap
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.tools.sap.importer.helper.datatype.sap;

public class SapDomain extends SapDataType {

	private String domainName;
	
	static String copyright()
	{ return com.ibm.is.sappack.gen.tools.sap.importer.helper.datatype.sap.Copyright.IBM_COPYRIGHT_SHORT; }

	protected SapDomain(String domainName, String dataTypeName, char sapBaseDataType, int length, int intLength, int decimals) {
		super(dataTypeName, sapBaseDataType, length, intLength, decimals);
		this.domainName = domainName;
	}

	public String getDomainName() {
		return this.domainName;
	}

}
