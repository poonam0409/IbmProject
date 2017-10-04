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
// Module Name : com.ibm.iis.sappack.gen.common.ui.wizards
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.iis.sappack.gen.common.ui.wizards;


import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;


public class CreateSingleFileInDataDesignProjectWizard extends CreateFilesInDataDesignProjectWizard {

	private String fileName;


	static String copyright() { 
		return Copyright.IBM_COPYRIGHT_SHORT; 
	}

	
	public CreateSingleFileInDataDesignProjectWizard(String fileName) {
		this.fileName = fileName;
//		ImageDescriptor imgDesc = Activator.getDefault().getImageRegistry().getDescriptor(Constants.ICON_ID_SAP_PACKS);
//		this.setDefaultPageImageDescriptor(imgDesc);
	}

	@Override
	protected Map<String, byte[]> getFilesToBeCreated(IProject p) {
		Map<String, byte[]> result = new HashMap<String, byte[]>();
		InputStream is = this.getClass().getClassLoader().getResourceAsStream(fileName);
		if (is != null) {
			result.put(fileName, getContents(is));
		}
		return result;
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {

	}

}
