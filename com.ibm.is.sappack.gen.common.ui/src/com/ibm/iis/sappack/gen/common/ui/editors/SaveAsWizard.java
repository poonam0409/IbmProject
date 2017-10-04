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


import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.Wizard;

import com.ibm.iis.sappack.gen.common.ui.util.FileHelper;
import com.ibm.is.sappack.gen.common.ui.Activator;


public class SaveAsWizard extends Wizard {
  	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	

	private SaveAsWizardPage page;
	private ModelStoreMap    map;


	public SaveAsWizard(String newFileDefault, ModelStoreMap map) {
		super();
		page = new SaveAsWizardPage(newFileDefault);
		this.addPage(page);
		this.map = map;
	}

	@Override
	public boolean performFinish() {
		final Map<String, String> copy = new HashMap<String, String>();
		copy.putAll(map);
		final IFile f = this.page.getSelectedFile();
		String id = FileHelper.createID(f);
		copy.put(PropertiesConstants.KEY_ID, id);
		
		
		try {
			this.getContainer().run(true, false, new IRunnableWithProgress() {
				
				@Override
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					try {
						FileHelper.save(f, copy, true, monitor);
					} catch (Exception e) {
						Activator.logException(e);
						throw new InvocationTargetException(e);
					} 
					
				}
			});
		} catch (Exception e) {
			Activator.logException(e);
			return false;
		} 
		
		return true;
	}

}
