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
// Module Name : com.ibm.iis.sappack.gen.common.ui.editors
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.iis.sappack.gen.common.ui.editors;


@SuppressWarnings("nls")
public class PropertiesConstants {
  	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	

	// Misc
	public static final String KEY_ID = "ID";

	// SOC
	public static final String KEY_SOCTYPE = "SOCTYPE";
	public static final String VALUE_TABLES = "TABLES";
	public static final String VALUE_IDOC = "IDOC";

	// RM Conf
	public static final String KEY_PACKAGE_PATH = "KEY_PACKAGE_PATH";

	// RG COnf
	public static final String KEY_RGCFG_TYPE = "RGCFG_TYPE";
	public static final String VALUE_RGCFG_TYPE_SAP_EXTRACT = "SAP_EXTRACT";
	public static final String VALUE_RGCFG_TYPE_MOVE_TRANSCODE = "MOVE_TRANSCODE";
	public static final String VALUE_RGCFG_TYPE_SAP_LOAD = "SAP_LOAD";

	// SAP connection
	public static final String PROP_SAP_CONN_NAME = "CONN_NAME"; //$NON-NLS-1$
	public static final String PROP_SAP_USER = "SAP_USER"; //$NON-NLS-1$
	public static final String PROP_SAP_PASSWORD = "SAP_PASSWORD"; //$NON-NLS-1$
	public static final String PROP_SAP_CLIENT = "SAP_CLIENT"; //$NON-NLS-1$
	public static final String PROP_SAP_LANGUAGE = "SAP_LANGUAGE"; //$NON-NLS-1$

	public static final String PROP_KEY_MODE = "PROP_KEY_MODE";

	public static final String KEY_DOCUMENTATION = "DOCUMENTATION"; 
}
