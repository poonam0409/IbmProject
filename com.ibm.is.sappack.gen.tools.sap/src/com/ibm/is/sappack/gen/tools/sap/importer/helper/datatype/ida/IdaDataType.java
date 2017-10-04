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
// Module Name : com.ibm.is.sappack.gen.tools.sap.importer.helper.datatype.ida
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.tools.sap.importer.helper.datatype.ida;


public class IdaDataType {
	
	static String copyright()
	{ return com.ibm.is.sappack.gen.tools.sap.importer.helper.datatype.ida.Copyright.IBM_COPYRIGHT_SHORT; }

	public enum IDA_DATATYPES {
		CHAR, VARCHAR, DECIMAL, SHORT, INTEGER, BINARY, DATE, TIME, CUSTOM, DOUBLE
	};

	private IDA_DATATYPES dataType;
	private int length;
	private int decimals;
	
//	private int varcharLengthFactor = 1;

	private String customTypeDefinition;

	public IdaDataType(String customTypeDefinition) {
		this.dataType = IDA_DATATYPES.CUSTOM;
		this.customTypeDefinition = customTypeDefinition;
	}

	
	public IdaDataType(IDA_DATATYPES dataType) {
		this.dataType = dataType;
	}

	public IdaDataType(IDA_DATATYPES dataType, int length) {
		this(dataType);
		this.length = length;
	}

	public IdaDataType(IDA_DATATYPES dataType, int length, int decimals) {
		this(dataType, length);
		this.decimals = decimals;
	}
	
/*
	public IdaDataType(IDA_DATATYPES dataType, SapDataType sapDataType) {
		this(dataType, sapDataType.getLength(), sapDataType.getDecimals());
	}
	*/

	/*
	public void setVarCharLengthFactor(int factor) {
		this.varcharLengthFactor = factor;
	}
	*/

	public String getIdaDataTypeDefinition() {
		switch (this.dataType) {
		case SHORT:
		case INTEGER:
		case DATE:
		case TIME:
		case DOUBLE:
			return this.dataType.name();
		case BINARY:
	         return this.dataType.name() + "(" + this.length + ")"; //$NON-NLS-1$ //$NON-NLS-2$
		case CHAR:
		case VARCHAR:
			int len = this.length;
			/*
		   if (this.decimals != 0.0) {
			   len += 1 /* sign * / + 1 /* decimal point * ;
		   }
		   len *= varcharLengthFactor;
		   */
		   return this.dataType.name() + "(" + len + ")"; //$NON-NLS-1$ //$NON-NLS-2$
		case DECIMAL:
			return this.dataType.name() + "(" + this.length + "," + this.decimals + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		case CUSTOM:
			return this.customTypeDefinition;
		default:
			return "VARCHAR(250)"; //$NON-NLS-1$
		}
	}

}
