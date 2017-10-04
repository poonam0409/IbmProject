//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2011, 2014                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.is.sappack.gen.tools.jobgenerator.wizard.helpers
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.tools.jobgenerator.wizard.helpers;


import java.util.HashMap;
import java.util.Map;


/**
 * ParameterResolver
 * 
 * Helper class to resolve DataStage
 * job parameters and parameter sets
 *
 */
public class ParameterResolver {

	/* map with parameters and default values */
	private Map<String, String> parameters;
	
	/**
	 * ParameterResolver
	 */
	public ParameterResolver() {
		this.parameters = new HashMap<String, String>();
	}
	
	/**
	 * put
	 * 
	 * put a new parameter, defaultValue pair
	 * to the parameter resolver. For example
	 * #SAP.USER#, DEV0032
	 * 
	 * @param parameter
	 * @param defaultValue
	 */
	public void put(String parameter, String defaultValue) {
		
		this.parameters.put(parameter, defaultValue);
	}
	
	/**
	 * resolveParameter
	 * 
	 * resolve the given parameter. If the given
	 * String is not a parameter, or there is no
	 * known value to resolve the parameter, the parameter
	 * will be returned.
	 * 
	 * @param the parameter s
	 * @return the resolved parameter s
	 */
	public String resolveParameter(String s) {
	
		String value = parameters.get(s);
		
		/* return value if it is not a known parameter */	
		if(value == null) {
			return s;
		} else {
			return value; 
		}
	}
	
	static String copyright() { 
		return com.ibm.is.sappack.gen.tools.jobgenerator.wizard.helpers.Copyright.IBM_COPYRIGHT_SHORT;
	} 
}
