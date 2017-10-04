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


import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.PlatformUI;

import com.ibm.iis.sappack.gen.common.ui.editors.PropertiesConstants;
import com.ibm.iis.sappack.gen.common.ui.newwizards.NewWizardPageBase;
import com.ibm.iis.sappack.gen.common.ui.util.ImageProvider;
import com.ibm.iis.sappack.gen.common.ui.util.Utils;
import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.common.ui.ModeManager;


public class NewRGConfWizardPage extends NewWizardPageBase {
	private Button sapExtract;
	private Button sapLoad;
	private Button moveTranscode;


	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	


	public NewRGConfWizardPage() {
		super("newrgconfpage", Messages.NewRGConfWizardPage_0, ImageProvider.getImageDescriptor(ImageProvider.IMAGE_RGCONF_45)); //$NON-NLS-1$
		setDescription(Messages.NewRGConfWizardPage_1);

	}

	@Override
	protected void createAdditionalControls(Composite parent) {
		Group comp = new Group(parent, SWT.NONE);
		comp.setText(Messages.NewRGConfWizardPage_2);
		comp.setLayout(new GridLayout(1, false));
		comp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false ));
		parent = comp;
		/*
		Label l = new Label(parent, SWT.NONE);
		l.setText("Job type:");
		*/
		this.sapExtract = new Button(parent, SWT.RADIO);
		this.sapExtract.setText(Messages.NewRGConfWizardPage_3);
		this.sapExtract.setSelection(true);
		
//		new Label(parent, SWT.NONE);
		this.sapLoad = new Button(parent, SWT.RADIO);
		this.sapLoad.setText(Messages.NewRGConfWizardPage_4);
		this.sapLoad.setSelection(false);

		if (ModeManager.isCWEnabled()) {
			this.moveTranscode = new Button(parent, SWT.RADIO);
			this.moveTranscode.setText(Messages.NewRGConfWizardPage_5);
			this.moveTranscode.setSelection(false);
		}
		PlatformUI.getWorkbench().getHelpSystem().setHelp(parent.getParent(), Utils.getHelpID("rgwizard_new_rgconf")); //$NON-NLS-1$
	}

	@Override
	protected String getNewFileNameDefault() {
		return "RapidGeneratorConfiguration"; //$NON-NLS-1$
	}

	@Override
	protected String getNewFileExtension() {
		return RGConfiguration.RGCONF_FILE_EXTENSION;
	}

	@Override
	public Map<String, String> getInitialProperties() {
		Map<String, String> props = new HashMap<String, String>();
		if (sapExtract.getSelection()) {
			props.put(PropertiesConstants.KEY_RGCFG_TYPE, PropertiesConstants.VALUE_RGCFG_TYPE_SAP_EXTRACT);
		} else if (sapLoad.getSelection()) {
			props.put(PropertiesConstants.KEY_RGCFG_TYPE, PropertiesConstants.VALUE_RGCFG_TYPE_SAP_LOAD);
		} else if (moveTranscode != null && moveTranscode.getSelection()) {
			props.put(PropertiesConstants.KEY_RGCFG_TYPE, PropertiesConstants.VALUE_RGCFG_TYPE_MOVE_TRANSCODE);
		}
		return props;
	}

}
