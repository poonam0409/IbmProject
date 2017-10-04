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
// Module Name : com.ibm.is.sappack.gen.tools.sap.wizard.importwizard.pages
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.iis.sappack.gen.tools.sap.rmconf;


import com.ibm.iis.sappack.gen.common.ui.editors.ConfigurationBase;
import com.ibm.iis.sappack.gen.common.ui.editors.PropertiesConstants;
import com.ibm.iis.sappack.gen.common.ui.util.Utils;
import com.ibm.is.sappack.gen.common.ui.Messages;


public class LdmPackageSelectionPage extends BaseLdmPackageSelectionPage {

  	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	

	public static final String TABNAME = Messages.LdmPackageSelectionPage_0;

	public LdmPackageSelectionPage() {
		super(TABNAME, Messages.LdmPackageSelectionPage_1, Messages.LdmPackageSelectionPage_2,
			   PropertiesConstants.KEY_PACKAGE_PATH, Utils.getHelpID("rmconfeditor_package_sappack")); //$NON-NLS-1$
	}


	public static String getPackagePath(ConfigurationBase map) {
		return map.get(PropertiesConstants.KEY_PACKAGE_PATH); // + TREE_SEP;
	}


}
