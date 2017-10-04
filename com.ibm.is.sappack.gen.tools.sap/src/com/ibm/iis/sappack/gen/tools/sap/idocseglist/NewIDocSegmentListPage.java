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
// Module Name : com.ibm.iis.sappack.gen.tools.sap.idocseglist
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.iis.sappack.gen.tools.sap.idocseglist;


import java.util.Map;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;

import com.ibm.iis.sappack.gen.common.ui.newwizards.NewWizardPageBase;
import com.ibm.iis.sappack.gen.common.ui.util.ImageProvider;
import com.ibm.iis.sappack.gen.common.ui.util.Utils;
import com.ibm.is.sappack.gen.common.ui.Messages;


public class NewIDocSegmentListPage extends NewWizardPageBase {
  	static String copyright() { 
 	   return Copyright.IBM_COPYRIGHT_SHORT; 
 	}	

	protected NewIDocSegmentListPage() {
		super("idocSegmentList", Messages.NewIDocSegmentListPage_0, ImageProvider.getImageDescriptor(ImageProvider.IMAGE_IDOCS_45)); //$NON-NLS-1$
		setDescription(Messages.NewIDocSegmentListPage_1);
	}

	@Override
	protected void createAdditionalControls(Composite parent) {
		PlatformUI.getWorkbench().getHelpSystem().setHelp(parent.getParent(), Utils.getHelpID("rmwizard_new_idoc_segment_list")); //$NON-NLS-1$
	}

	@Override
	protected String getNewFileNameDefault() {
		return "IDocSegmentList"; //$NON-NLS-1$
	}

	@Override
	protected String getNewFileExtension() {
		return IDocSegmentList.IDOC_SEGMENT_LIST_FILE_EXTENSION;
	}

	@Override
	public Map<String, String> getInitialProperties() {
		return null;
	}


}
