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

public class SapDataElement extends SapDataType {

	private String dataElementName;

	private String domainName;
	
	static String copyright()
	{ return com.ibm.is.sappack.gen.tools.sap.importer.helper.datatype.sap.Copyright.IBM_COPYRIGHT_SHORT; }

	public SapDataElement(String dataElementName, String dataTypeName, char sapBaseDataType, int length, int intLength, int decimals) {
		super(dataTypeName, sapBaseDataType, length, intLength, decimals);
		this.dataElementName = dataElementName;
	}

	public SapDataElement(String domainName, String dataElementName, String dataTypeName, char sapBaseDataType, int length, int intLength, int decimals) {
		this(dataElementName, dataTypeName, sapBaseDataType, length, intLength, decimals);
		this.domainName = domainName;
	}

	public String getDataElementName() {
		return this.dataElementName;
	}

	public boolean hasDomain() {
		return (this.domainName != null);
	}

	public String getDomainName() {
		return this.domainName;
	}

	public SapDomain getDomain() {
		if (hasDomain()) {
			return super.produceDomain(this.domainName);
		}
		return null;
	}
}
