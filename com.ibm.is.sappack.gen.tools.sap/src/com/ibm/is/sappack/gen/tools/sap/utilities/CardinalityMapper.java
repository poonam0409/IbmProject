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

import org.eclipse.osgi.util.NLS;

import com.ibm.db.models.logical.CardinalityType;
import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.tools.sap.activator.Activator;
import com.ibm.is.sappack.gen.tools.sap.constants.Constants;

public class CardinalityMapper {

	static String copyright() {
		return com.ibm.is.sappack.gen.tools.sap.utilities.Copyright.IBM_COPYRIGHT_SHORT;
	}

	public static final int mapCardinality(String sapCardinality) {
		if (sapCardinality.equalsIgnoreCase(Constants.JCO_PARAMETER_VALUE_CARDINATLITY_ONE)) {
			return CardinalityType.ONE;
		}

		if (sapCardinality.equalsIgnoreCase(Constants.JCO_PARAMETER_VALUE_CARDINATLITY_ZERO_TO_ONE)) {
			return CardinalityType.ZERO_TO_ONE;
		}

		if (sapCardinality.equalsIgnoreCase(Constants.JCO_PARAMETER_VALUE_CARDINATLITY_ONE_TO_MANY)) {
			return CardinalityType.ONE_TO_MANY;
		}

		if (sapCardinality.equalsIgnoreCase(Constants.JCO_PARAMETER_VALUE_CARDINATLITY_ZERO_TO_MANY)) {
			return CardinalityType.ZERO_TO_MANY;
		}

		Activator.getLogger().warning(NLS.bind(Messages.CardinalityMapper_0, sapCardinality));

		return CardinalityType.ONE;
	}
}
