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
// Module Name : com.ibm.is.sappack.gen.tools.sap.importer.helper.datatype.mapping
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.tools.sap.importer.helper.datatype.mapping;

import com.ibm.is.sappack.gen.tools.sap.importer.helper.datatype.ida.IdaDataType;
import com.ibm.is.sappack.gen.tools.sap.importer.helper.datatype.ida.IdaDataType.IDA_DATATYPES;
import com.ibm.is.sappack.gen.tools.sap.importer.helper.datatype.sap.SapDataType;

public class IDocVarcharDataTypeMapper implements IDataTypeMapper {
	static String copyright() {
		return Copyright.IBM_COPYRIGHT_SHORT;
	}

	int lengthFactor;

	public IDocVarcharDataTypeMapper(int varcharLengthFactor) {
		this.lengthFactor = varcharLengthFactor;
	}

	@Override
	public IdaDataType map(SapDataType sapDataType) {
		// Simply map everything to VARCHAR with the original length
		int len = sapDataType.getLength();
		IdaDataType dt = new IdaDataType(IDA_DATATYPES.VARCHAR, len * this.lengthFactor );
		return dt;
	}

}
