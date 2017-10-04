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
// Module Name : com.ibm.iis.sappack.gen.tools.jobgenerator.rgconf
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.iis.sappack.gen.tools.jobgenerator.rgconf;


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
import com.ibm.iis.sappack.gen.common.ui.editors.PropertiesConstants;
import com.ibm.iis.sappack.gen.common.ui.util.ImageProvider;
import com.ibm.iis.sappack.gen.common.ui.util.Utils;
import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.common.ui.ModeManager;
import com.ibm.is.sappack.gen.common.ui.RMRGMode;
import com.ibm.is.sappack.gen.tools.jobgenerator.Activator;


public class RGConfEditor extends MultiPageEditorBase {
	public static final String TAB_SAP_SOURCE = Messages.RGConfEditor_0;
	public static final String TAB_SAP_TARGET = Messages.RGConfEditor_1;
	public static final String TAB_DB_SOURCE = Messages.RGConfEditor_2;
	public static final String TAB_DB_TARGET = Messages.RGConfEditor_3;


  	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	


	public RGConfEditor() {
		super(true);
  		this.editorImage = ImageProvider.getImage(ImageProvider.IMAGE_RGCONF_16);
	}
	
	@Override
	protected EditorPageBase[] createPageList() {
		ConfigurationBase conf = this.getConfiguration();
		RMRGMode mode = conf.getMode();
		if (mode == null) {
//			mode = ModeManager.getActiveMode();
			String msg = MessageFormat.format(Messages.RGConfEditor_4, this.getEditedFile().getName());
			Activator.getLogger().log(Level.WARNING, msg);
			throw new RuntimeException(msg);
		}
		List<EditorPageBase> pagelist = new ArrayList<EditorPageBase>();
		pagelist.add(new RGConfGeneralPage());
		String rgcfgType = this.getConfiguration().get(PropertiesConstants.KEY_RGCFG_TYPE);
		if (PropertiesConstants.VALUE_RGCFG_TYPE_SAP_EXTRACT.equals(rgcfgType)) {
			pagelist.add(new SAPConnectionPage(TAB_SAP_SOURCE, Messages.RGConfEditor_5, Messages.RGConfEditor_6, Utils.getHelpID("rgconfeditor_sap_source"))); //$NON-NLS-1$
			pagelist.add(new DBPage(TAB_DB_TARGET, Messages.RGConfEditor_8, Messages.RGConfEditor_9, false));
			pagelist.add(new ABAPExtractSettingsPage());
		} else if (PropertiesConstants.VALUE_RGCFG_TYPE_SAP_LOAD.equals(rgcfgType)) {
			pagelist.add(new DBPage(TAB_DB_SOURCE, Messages.RGConfEditor_10, Messages.RGConfEditor_11, true));
			pagelist.add(new SAPConnectionPage(TAB_SAP_TARGET, Messages.RGConfEditor_12, Messages.RGConfEditor_13, Utils.getHelpID("rgconfeditor_sap_target"))); //$NON-NLS-1$
			if (mode.getID().equals(ModeManager.CW_MODE_ID)) {
				pagelist.add(new CWIDocLoadPage());
			}
		} else if (PropertiesConstants.VALUE_RGCFG_TYPE_MOVE_TRANSCODE.equals(rgcfgType)) {
			if (!ModeManager.isCWEnabled()) {
				String msg = Messages.RGConfEditor_7;
				Activator.getLogger().log(Level.WARNING, msg);
				throw new RuntimeException(msg);			
			}
			pagelist.add(new DBPage(TAB_DB_SOURCE, Messages.RGConfEditor_15, Messages.RGConfEditor_16, true));
			pagelist.add(new DBPage(TAB_DB_TARGET, Messages.RGConfEditor_17, Messages.RGConfEditor_18, false));
			pagelist.add(new MoveTranscodeSettingsPage());
		}

		pagelist.add(new CustomDerivationsPage());
		pagelist.add(new JobDescriptionPage());
		pagelist.add(new ParameterPage());
		return pagelist.toArray(new EditorPageBase[0]);
	}

	@Override
	protected ConfigurationBase createConfiguration(IResource res) throws IOException, CoreException {
		return new RGConfiguration(res);
	}

}
