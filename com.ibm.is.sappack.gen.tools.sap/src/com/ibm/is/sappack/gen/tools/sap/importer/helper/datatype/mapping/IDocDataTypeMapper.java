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

import com.ibm.iis.sappack.gen.tools.sap.rmconf.RMConfiguration.SupportedDB;
import com.ibm.is.sappack.gen.tools.sap.PreferencePageRMSettings;
import com.ibm.is.sappack.gen.tools.sap.importer.helper.datatype.ida.IdaDataType;
import com.ibm.is.sappack.gen.tools.sap.importer.helper.datatype.ida.IdaDataType.IDA_DATATYPES;
import com.ibm.is.sappack.gen.tools.sap.importer.helper.datatype.sap.SapDataType;

public class IDocDataTypeMapper implements IDataTypeMapper {
   
   static String copyright() { 
      return com.ibm.is.sappack.gen.tools.sap.importer.helper.datatype.mapping.Copyright.IBM_COPYRIGHT_SHORT; 
   }

	@Override
	public IdaDataType map(SapDataType sapDataType) {
		// DATS -> DATE
		if (sapDataType.is("DATS")) { //$NON-NLS-1$
			return new IdaDataType(IDA_DATATYPES.DATE);
		}
		
		// TIMS -> TIME
      if (sapDataType.is("TIMS")) { //$NON-NLS-1$
         // Check whether target DB is "Oracle"
         SupportedDB targetDB = PreferencePageRMSettings.getTargetDBType();
         switch (targetDB)
         {
            case Oracle:
                 return new IdaDataType(IDA_DATATYPES.VARCHAR, sapDataType.getLength());
                 
            default:
                 return new IdaDataType(IDA_DATATYPES.TIME);
         }
      } // end of if (sapDataType.is("TIMS"))

		// Everything else -> VARCHAR
		return new IdaDataType(IDA_DATATYPES.VARCHAR, sapDataType.getLength());
	}

}
