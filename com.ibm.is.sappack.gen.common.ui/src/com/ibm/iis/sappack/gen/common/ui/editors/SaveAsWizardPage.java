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


import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.swt.widgets.Composite;

import com.ibm.iis.sappack.gen.common.ui.newwizards.NewWizardPageBase;
import com.ibm.is.sappack.gen.common.ui.Messages;


public class SaveAsWizardPage extends NewWizardPageBase {
  	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	

	private String fileTemplate;


	public SaveAsWizardPage(String fileTemplate) {
		super("saveaswizardpage", Messages.SaveAsWizardPage_0, null); //$NON-NLS-1$
		this.fileTemplate = fileTemplate;
		this.setDescription(Messages.SaveAsWizardPage_1);
	}

	@Override
	protected void createAdditionalControls(Composite parent) {
		
	}

	@Override
	protected String getNewFileNameDefault() {
		return this.fileTemplate;
	}

	public IFile getSelectedFile() {
		return getSelectedProject().getFile(this.newFileName.getText().trim()); 
	}

	
	@Override
	protected String getNewFileExtension() {
		return null;
	}

	@Override
	public Map<String, String> getInitialProperties() {
		return null;
	}

}
