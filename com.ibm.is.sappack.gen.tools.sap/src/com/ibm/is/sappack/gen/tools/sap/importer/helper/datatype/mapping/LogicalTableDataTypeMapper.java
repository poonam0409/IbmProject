//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2009, 2013                                              
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


import com.ibm.iis.sappack.gen.tools.sap.rmconf.RMConfiguration.SupportedDB;
import com.ibm.is.sappack.gen.tools.sap.PreferencePageRMSettings;
import com.ibm.is.sappack.gen.tools.sap.importer.helper.datatype.ida.IdaDataType;
import com.ibm.is.sappack.gen.tools.sap.importer.helper.datatype.ida.IdaDataType.IDA_DATATYPES;
import com.ibm.is.sappack.gen.tools.sap.importer.helper.datatype.sap.SapDataType;


public class LogicalTableDataTypeMapper implements IDataTypeMapper {


	static String copyright() {
		return com.ibm.is.sappack.gen.tools.sap.importer.helper.datatype.mapping.Copyright.IBM_COPYRIGHT_SHORT;
	}

	@Override
	public IdaDataType map(SapDataType sapDataType) {
		if (sapDataType == null) {
			return null;
		}

		// ACCP -> CHAR
		if (sapDataType.is("ACCP")) { //$NON-NLS-1$
			return new IdaDataType(IDA_DATATYPES.CHAR, sapDataType.getLength() );
		}

		// CHAR -> CHAR
		if (sapDataType.is("CHAR")) { //$NON-NLS-1$
			int length = sapDataType.getLength();

			// Check whether target DB is "DB2" or "DB2forCW"
			SupportedDB targetDB = PreferencePageRMSettings.getTargetDBType();
			if ((targetDB == SupportedDB.DB2)
					|| (targetDB == SupportedDB.DB2forCW)) {

				// If the length is <=254 -> CHAR else -> VARCHAR
				if (length <= 254) {
					return new IdaDataType(IDA_DATATYPES.CHAR, length);
				} else {
					sapDataType.charToVarcharExtension();
					return new IdaDataType(IDA_DATATYPES.VARCHAR, length);
				}
			}

			// If not DB2, then always use "CHAR" with the specified length
			return new IdaDataType(IDA_DATATYPES.CHAR, length);
		}

		// CLNT -> CHAR
		if (sapDataType.is("CLNT")) { //$NON-NLS-1$
			return new IdaDataType(IDA_DATATYPES.CHAR, sapDataType.getLength() );
		}

		// CUKY -> CHAR
		if (sapDataType.is("CUKY")) { //$NON-NLS-1$
			return new IdaDataType(IDA_DATATYPES.CHAR, sapDataType.getLength() );
		}

		// CUKY -> DECIMAL
		if (sapDataType.is("CURR")) { //$NON-NLS-1$
			return new IdaDataType(IDA_DATATYPES.DECIMAL, sapDataType.getLength(), sapDataType.getDecimals());
		}

		// DATS -> CHAR
		if (sapDataType.is("DATS")) { //$NON-NLS-1$
			return new IdaDataType(IDA_DATATYPES.CHAR, sapDataType.getLength());
		}

		// DEC -> DECIMAL
		if (sapDataType.is("DEC")) { //$NON-NLS-1$
			return new IdaDataType(IDA_DATATYPES.DECIMAL, sapDataType.getLength(), sapDataType.getDecimals());
		}

		// FLTP -> DECIMAL
		if (sapDataType.is("FLTP")) { //$NON-NLS-1$
			// osuhre, 52708, map FLTP to DOUBLE
			// return new IdaDataType(IDA_DATATYPES.DECIMAL, sapDataType);
			return new IdaDataType(IDA_DATATYPES.DOUBLE);
		}

		// INT1 -> SHORT
		if (sapDataType.is("INT1")) { //$NON-NLS-1$
			return new IdaDataType(IDA_DATATYPES.SHORT);
		}

		// INT2 -> INTEGER
		if (sapDataType.is("INT2")) { //$NON-NLS-1$
			return new IdaDataType(IDA_DATATYPES.INTEGER);
		}

		// INT4 -> INTEGER
		if (sapDataType.is("INT4")) { //$NON-NLS-1$
			return new IdaDataType(IDA_DATATYPES.INTEGER);
		}

		// LANG -> CHAR
		if (sapDataType.is("LANG")) { //$NON-NLS-1$
			return new IdaDataType(IDA_DATATYPES.CHAR, sapDataType.getLength() );
		}

		// LCHR -> VARCHAR
		if (sapDataType.is("LCHR")) { //$NON-NLS-1$
			return new IdaDataType(IDA_DATATYPES.VARCHAR, sapDataType.getLength());
		}

		// LRAW -> BINARY
		if (sapDataType.is("LRAW")) { //$NON-NLS-1$
			// TODO
			return new IdaDataType(IDA_DATATYPES.BINARY, sapDataType.getLength());
		}

		// NUMC -> VARCHAR
		if (sapDataType.is("NUMC")) { //$NON-NLS-1$
			return new IdaDataType(IDA_DATATYPES.VARCHAR, sapDataType.getLength());
		}

		// PREC -> CHAR
		if (sapDataType.is("PREC")) { //$NON-NLS-1$
			return new IdaDataType(IDA_DATATYPES.CHAR, sapDataType.getLength());
		}

		// QUAN -> DECIMAL
		if (sapDataType.is("QUAN")) { //$NON-NLS-1$
			return new IdaDataType(IDA_DATATYPES.DECIMAL, sapDataType.getLength(), sapDataType.getDecimals());
		}

		// RAW -> BINARY
		if (sapDataType.is("RAW")) { //$NON-NLS-1$
			// TODO
			return new IdaDataType(IDA_DATATYPES.BINARY, sapDataType.getLength());
		}

		// RAWSTRING -> VARCHAR
		if (sapDataType.is("RAWSTRING") || sapDataType.is("RSTR")) {//$NON-NLS-1$ //$NON-NLS-2$
			return new IdaDataType(IDA_DATATYPES.VARCHAR, sapDataType.getLength());
		}

		// SSTRING -> VARCHAR
		if (sapDataType.is("SSTRING") || sapDataType.is("SSTR")) { //$NON-NLS-1$ //$NON-NLS-2$
			return new IdaDataType(IDA_DATATYPES.VARCHAR, sapDataType.getLength());
		}

		// STRING -> VARCHAR
		if (sapDataType.is("STRING") || sapDataType.is("STRG")) {//$NON-NLS-1$ //$NON-NLS-2$
			return new IdaDataType(IDA_DATATYPES.VARCHAR, sapDataType.getLength());
		}

		// TIMS -> CHAR
		if (sapDataType.is("TIMS")) {//$NON-NLS-1$
			return new IdaDataType(IDA_DATATYPES.CHAR, sapDataType.getLength());
		}

		// UNIT -> CHAR
		if (sapDataType.is("UNIT")) {//$NON-NLS-1$
			return new IdaDataType(IDA_DATATYPES.CHAR, sapDataType.getLength());
		}

		// VARC -> VARCHAR
		if (sapDataType.is("VARC")) {//$NON-NLS-1$
			return new IdaDataType(IDA_DATATYPES.VARCHAR, sapDataType.getLength());
		}

		throw new IllegalArgumentException(
				"No data type mapping defined in \"" + this.getClass().getName() + "\" for SAP data type \"" + sapDataType.getDataTypeName() + "\""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
}