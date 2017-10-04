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


import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

import com.ibm.iis.sappack.gen.common.ui.editors.ConfigurationBase;
import com.ibm.iis.sappack.gen.common.ui.editors.EditorPageBase;
import com.ibm.iis.sappack.gen.common.ui.editors.MultiPageEditorBase;
import com.ibm.iis.sappack.gen.common.ui.util.ImageProvider;
import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.common.ui.ModeManager;
import com.ibm.is.sappack.gen.common.ui.RMRGMode;
import com.ibm.is.sappack.gen.tools.sap.activator.Activator;


public class RMConfigurationEditor extends MultiPageEditorBase {
  	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	

  	
  	public RMConfigurationEditor() {
  		super(true);
  		this.editorImage = ImageProvider.getImage(ImageProvider.IMAGE_RMCONF_16);
  	}
  	
	@Override
	protected EditorPageBase[] createPageList() {
		ConfigurationBase conf = this.getConfiguration();
		RMRGMode mode = conf.getMode();
		if (mode == null) {
			String msg = MessageFormat.format(Messages.RMConfigurationEditor_0, this.getEditedFile().getName());
			Activator.getLogger().log(Level.WARNING, msg);
			throw new RuntimeException(msg);
		}
		mode = ModeManager.getActiveMode();
		List<EditorPageBase> pageList = new ArrayList<EditorPageBase>();
		pageList.add(new RMConfigurationGeneralPage());
		if (mode.getID().equals(ModeManager.CW_MODE_ID)) {
			pageList.add(new CWImportOptionsPage());
		} else {
			pageList.add(new SAPPackImportOptionsPage());
		}
		pageList.add(new TargetDBPage());
		pageList.add(new TechnicalFieldsPage());
		pageList.add(new LdmPackageSelectionPage());
		pageList.add(new CheckTablePackageSelectionPage());
		return pageList.toArray(new EditorPageBase[0]);
	}

	@Override
	protected ConfigurationBase createConfiguration(IResource res) throws IOException, CoreException {
		return new RMConfiguration(res);
	}

}
