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
// Module Name : com.ibm.iis.sappack.gen.tools.sap.rmconf
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.iis.sappack.gen.tools.sap.rmconf;


import com.ibm.iis.sappack.gen.common.ui.util.Utils;
import com.ibm.is.sappack.gen.common.ui.Messages;


public class CheckTablePackageSelectionPage extends BaseLdmPackageSelectionPage {
  	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	

	public static final String CHECK_TABLE_PACKAGE_PATH = "CHECK_TABLE_PACKAGE_PATH"; //$NON-NLS-1$
	
	public static final String TABNAME = Messages.CheckTablePackageSelectionPage_0;

	public CheckTablePackageSelectionPage() {
		super(TABNAME, Messages.CheckTablePackageSelectionPage_1, Messages.CheckTablePackageSelectionPage_2,
		      CHECK_TABLE_PACKAGE_PATH, Utils.getHelpID("rmconfeditor_ct_package_sappack")); //$NON-NLS-1$
	}

}
