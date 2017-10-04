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


public class SapDataTypeFactory {
	static String copyright()
	{ return com.ibm.is.sappack.gen.tools.sap.importer.helper.datatype.sap.Copyright.IBM_COPYRIGHT_SHORT; }
	
	public static final SapDataType createDataType(String dataElementName, String domainName, String dataTypeName, char sapBaseDataType, int length, int intLength, int decimals) {

		SapDataType dataType;

		// If we have a SAP data element:
		if (!isEmpty(dataElementName)) {
			// Check, whether the data element is based on a domain
			if (!isEmpty(domainName)) {
				dataType = new SapDataElement(domainName,dataElementName, dataTypeName, sapBaseDataType, length, intLength, decimals);
			} else { // otherwise it is based on a SAP base data Type
				dataType = new SapDataElement(dataElementName, dataTypeName, sapBaseDataType, length, intLength, decimals);
			}
		} else {// we have a SAP base data type:
			dataType = new SapDataType(dataTypeName, sapBaseDataType, length, intLength, decimals);
		}

		return dataType;
	}

	private static final boolean isEmpty(String theString) {
		return ((theString == null) || (theString.trim().length() == 0));
	}
}
