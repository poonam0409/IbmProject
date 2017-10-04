//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2014                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.iis.sappack.gen.tools.sap.tablelist
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.iis.sappack.gen.tools.sap.tablelist;


import java.util.Map;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;

import com.ibm.iis.sappack.gen.common.ui.newwizards.NewWizardPageBase;
import com.ibm.iis.sappack.gen.common.ui.util.ImageProvider;
import com.ibm.iis.sappack.gen.common.ui.util.Utils;
import com.ibm.is.sappack.gen.common.ui.Messages;


public class NewTableListWizardPage extends NewWizardPageBase {
  	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	
	
	public NewTableListWizardPage() {
		super("abapTableList", Messages.NewTableListWizardPage_0, ImageProvider.getImageDescriptor(ImageProvider.IMAGE_ABAP_TABLES_45)); //$NON-NLS-1$
		setDescription(Messages.NewTableListWizardPage_1);
	}

	@Override
	protected void createAdditionalControls(Composite parent) {
		PlatformUI.getWorkbench().getHelpSystem().setHelp(parent.getParent(), Utils.getHelpID("rmwizard_new_abap_table_list")); //$NON-NLS-1$
	}

	@Override
	protected String getNewFileNameDefault() {
		return "ABAPTableList"; //$NON-NLS-1$
	}

	@Override
	protected String getNewFileExtension() {
		return TableList.TABLE_LIST_FILE_EXTENSION;
	}

	@Override
	public Map<String, String> getInitialProperties() {
		return null;
	}

}
