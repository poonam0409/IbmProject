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
// Module Name : com.ibm.is.sappack.gen.common.ui.preferences
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.jco.panel;

import com.ibm.cic.agent.core.api.IAgentJob;
import com.ibm.cic.common.core.model.IOffering;

public class Util {
	
	static String copyright() {
		return com.ibm.is.sappack.gen.jco.panel.Copyright.IBM_COPYRIGHT_SHORT;
	}
	
	private Util() {
	}
	
	/**
	 * Iterate through IAgentJob and return an offering instance from a job
	 * found in that array, which matches the specified offeringId. 
	 * 
	 * @param jobs
	 * @param offeringId
	 * @return IAgentJob that refers to the specified offeringId
	 */
	public static IOffering findOffering(IAgentJob[] jobs, String offeringId) {
		for(IAgentJob job : jobs) {
			IOffering offering = job.getOffering();
			if(offering != null && offering.getIdentity().getId().equals(offeringId) == true) {
				return offering;
			}
		}
		return null;
	}
}
