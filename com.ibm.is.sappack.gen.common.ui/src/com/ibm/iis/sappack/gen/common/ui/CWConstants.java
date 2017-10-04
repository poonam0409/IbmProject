//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2009, 2012                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.is.sappack.cw.tools.sap.constants
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************

package com.ibm.iis.sappack.gen.common.ui;


public interface CWConstants {

	static final String IBM_COPYRIGHT_SHORT = Copyright.IBM_COPYRIGHT_SHORT;

	// The plug-in ID
	public static final String PLUGIN_ID = "com.ibm.is.sappack.cw.tools.sap"; //$NON-NLS-1$

	// help IDs
	public static final String HELP_ID_IMPORT_OPTIONS = CWConstants.PLUGIN_ID + ".import_options"; //$NON-NLS-1$

	// job name suffix
	public static final String JOB_NAME_SUFFIX_IDOC_LOAD      = "_IDoc_Load";      //$NON-NLS-1$
	public static final String JOB_NAME_SUFFIX_MOVE           = "_Move";           //$NON-NLS-1$
	public static final String JOB_NAME_SUFFIX_IDOC_MIH_LOAD  = "_IDoc_MIH_Load";  //$NON-NLS-1$
	public static final String JOB_NAME_SUFFIX_MIH_CT_EXTRACT = "_MIH_CT_Load";    //$NON-NLS-1$
	public static final String JOB_NAME_SUFFIX_MIH_LOAD       = "_MIH_Load";       //$NON-NLS-1$

	public static final int    MAX_LEN_FACTOR            = 4;     

	public static final String CW_TECHNICAL_FIELD_PREFIX = "CW_"; //$NON-NLS-1$

}
