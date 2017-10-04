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
// Module Name : com.ibm.is.sappack.gen.tools.sap.utilities
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.tools.sap.utilities;


import com.ibm.is.sappack.gen.common.util.StringUtils;
import com.ibm.is.sappack.gen.tools.sap.constants.Constants;
import com.ibm.is.sappack.gen.tools.sap.importer.helper.model.LdmNameConverter;


public class DefaultNameConverter implements LdmNameConverter{
	// -------------------------------------------------------------------------------------
	// Constants
	// -------------------------------------------------------------------------------------
	protected static final int HASHCODE_LEN = 4;


	// -------------------------------------------------------------------------------------
	// Member Variables
	// -------------------------------------------------------------------------------------
	private static final DefaultNameConverter singleInst = new DefaultNameConverter();


	static String copyright() {
		return com.ibm.is.sappack.gen.tools.sap.utilities.Copyright.IBM_COPYRIGHT_SHORT;
	}


	// protected constructor
	protected DefaultNameConverter () {
		;
	} // end of DefaultNameConverter


	@Override
	public String convertAttributeName(String attributeName) {
		return(StringUtils.cleanFieldName(attributeName));
	}


	@Override
	public String convertEntityName(String entityName) {
		return(StringUtils.cleanFieldName(entityName));
	}


	@Override
	public String convertRelationShipName(String relationShipName) {
		return(StringUtils.cleanFieldName(relationShipName));
	}


	@Override
	public String convertRelationShipName(String relationShipName, int suffixLen) {
		return(StringUtils.cleanFieldName(relationShipName));
	}


	public static DefaultNameConverter getConverter() {
		return(singleInst);
	} // end of getConverter()


	@Override
	public String convertAtomicDomainName(String domain) {
		return Constants.SAP_DATA_TYPE_NAME_PREFIX + domain;
	}



} // end of class DefaultNameConverter
