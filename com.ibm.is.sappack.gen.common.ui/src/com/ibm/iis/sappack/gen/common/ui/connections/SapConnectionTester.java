//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2009, 2014                                             
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.is.sappack.gen.tools.sap.importer
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.iis.sappack.gen.common.ui.connections;


import com.ibm.iis.sappack.gen.common.ui.jco.RfcDestinationDataProvider;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;


public class SapConnectionTester {

	static String copyright() { 
		return Copyright.IBM_COPYRIGHT_SHORT; 
	}
	
	public static final void ping(SapSystem sapSystem) throws JCoException {
		JCoDestination destination = RfcDestinationDataProvider.getDestination(sapSystem);
		destination.ping();
//		JCoAttributes a = destination.getAttributes();
//		System.out.println(a.getPartnerCharset());
//		System.out.println(a.getPartnerCodepage());
//		System.out.println(a.getPartnerEncoding());
	}

}
