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


public class SapDataType {
	protected String dataTypeName;
	protected char sapBaseDataType;
	protected int length;
	protected int intLength;
	protected int decimals;
	// Needed on some databases in case a "CHAR" with a length >=255 is used and
	// the data type is then upgraded tO VARCHAR instead of CHAR
	private boolean charToVarcharExtension = false;

	static String copyright() {
		return com.ibm.is.sappack.gen.tools.sap.importer.helper.datatype.sap.Copyright.IBM_COPYRIGHT_SHORT;
	}

	public SapDataType(String dataTypeName, char sapBaseDataType, int length, int intLength, int decimals) {
		this.dataTypeName = dataTypeName;
		this.sapBaseDataType = sapBaseDataType;
		this.length = length;
		this.intLength = intLength;
		this.decimals = decimals;
	}

	public char getSapBaseDataType() {
		return this.sapBaseDataType;
	}

	public String getDataTypeName() {
		return this.dataTypeName;
	}

	public int getLength() {
		return this.length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getIntLength() {
		return this.intLength;
	}

	public int getDecimals() {
		return this.decimals;
	}

	protected SapDomain produceDomain(String domainName) {
		return new SapDomain(domainName, this.dataTypeName, this.sapBaseDataType, this.length, this.intLength, this.decimals);
	}

	public boolean is(String sapDataTypeName) {
		return this.dataTypeName.trim().equalsIgnoreCase(sapDataTypeName.trim());
	}

	public void charToVarcharExtension() {
		this.charToVarcharExtension = true;
	}

	public boolean isCharToVarcharExtension() {
		return this.charToVarcharExtension;
	}

	public int getABAPDisplayLength() {
		int len = getLength();

		if (is("CURR") || is("QUAN")) { //$NON-NLS-1$ //$NON-NLS-2$
			len += 2; // 2 for sign and point
		} else if (is("DEC")) { //$NON-NLS-1$
			len += 2;
		} else if (is("FLTP")) { //$NON-NLS-1$
			len = 23;
		} else if (is("INT1")) { //$NON-NLS-1$
			len = 3;
		} else if (is("INT2")) { //$NON-NLS-1$
			len = 5;
		} else if (is("INT4")) { //$NON-NLS-1$
			len = 10;
		} else if (is("RAW") || is("LRAW")) {  //$NON-NLS-1$//$NON-NLS-2$
			len *= 2;
		}
		return len;
	}

	public String toString() {
		StringBuffer resString = new StringBuffer();
		resString.append("SAPDataType(");
		resString.append(dataTypeName);
		resString.append("/");
		resString.append(sapBaseDataType);
		resString.append("/");
		resString.append(length);
//		resString.append("/");
//		resString.append(intLength);
//		resString.append("/");
//		resString.append(decimals);
		resString.append(")");
		return(resString.toString());
	}
}
