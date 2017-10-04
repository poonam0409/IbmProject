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


import java.text.MessageFormat;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.validation.ValidationResult;
import org.eclipse.wst.validation.ValidatorMessage;

import com.ibm.iis.sappack.gen.common.ui.editors.GeneralPageBase;
import com.ibm.iis.sappack.gen.common.ui.editors.IControlFactory;
import com.ibm.iis.sappack.gen.common.ui.editors.PropertiesConstants;
import com.ibm.iis.sappack.gen.common.ui.util.ImageProvider;
import com.ibm.iis.sappack.gen.common.ui.util.Utils;
import com.ibm.iis.sappack.gen.common.ui.validators.ValidatorBase;
import com.ibm.iis.sappack.gen.common.ui.wizards.NextActionWizardDialog;
import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.tools.jobgenerator.validator.ValidationWizard;


public class RGConfGeneralPage extends GeneralPageBase {
  	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	


	public RGConfGeneralPage() {
		super(Messages.RGConfGeneralPage_0, Messages.RGConfGeneralPage_1, Messages.RGConfGeneralPage_2,
		      Utils.getHelpID("rgconfeditor_general_sappack")); //$NON-NLS-1$
	}

	@Override
	public void createControls(IControlFactory controlFactory, Composite composite) {
		super.createControls(controlFactory, composite);
		
		Composite pageComp = this.getPageComposite();
		Composite group = controlFactory.createGroup(pageComp, Messages.RGConfGeneralPage_4, SWT.NONE);
		Label l = controlFactory.createLabel(group, SWT.NONE);
		String rgCfgType = this.editor.getConfiguration().get(PropertiesConstants.KEY_RGCFG_TYPE);
		if (PropertiesConstants.VALUE_RGCFG_TYPE_SAP_EXTRACT.equals(rgCfgType)) {
			l.setText(Messages.RGConfGeneralPage_5);
		} else if (PropertiesConstants.VALUE_RGCFG_TYPE_SAP_LOAD.equals(rgCfgType)) {
			l.setText(Messages.RGConfGeneralPage_6);
		} else if (PropertiesConstants.VALUE_RGCFG_TYPE_MOVE_TRANSCODE.equals(rgCfgType)) {
			l.setText(Messages.RGConfGeneralPage_7);
		}
			
		
		
		/* Add SAP settings validation button for ABAP extract jobs only */
		if (rgCfgType.equals(PropertiesConstants.VALUE_RGCFG_TYPE_SAP_EXTRACT)) {
			
			
			Composite validationGroup = controlFactory.createGroup(pageComp,
					Messages.RGConfGeneralPage_8, SWT.NONE);
			Button validateButton = controlFactory.createButton(
					validationGroup, SWT.NONE);
			validateButton.setText(Messages.RGConfGeneralPage_9);
			validateButton.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {

					validate();

				}
			});
		}
	}

	@Override
	public Image getImage() {
		return ImageProvider.getImage(ImageProvider.IMAGE_RGCONF_16);
	}
	
	/**
	 * validate
	 * 
	 * validate SAP settings. First the eclipse validation mechanism
	 * will be called to validate the Rapid Generator configuration.
	 * If the Rapid Generator configuration does not contain any basic
	 * validation errors, a ValidationWizard will be launched to
	 * validate the non-basic SAP connection settings
	 */
	private void validate() {

		RGConfiguration configuration = (RGConfiguration) editor.getConfiguration();
		ValidatorBase eclipseValidator = configuration.createValidator();
		try {
			/* run eclipse validation to make sure a necessary fields are filled */
			ValidationResult validationResult = eclipseValidator.validate(configuration, null);
			ValidatorMessage[] messages = validationResult.getMessages();
	
			ValidatorMessage firstErrorMessage = null;
			for (ValidatorMessage m : messages) {
				int sev = m.getAttribute(IMarker.SEVERITY, IMarker.SEVERITY_INFO);
				if (sev == IMarker.SEVERITY_ERROR) {
					firstErrorMessage = m;
					break;
				}
			}
			
			
			/*
			 * only continue if eclipse validation does not detect
			 * any errors
			 */
			if (firstErrorMessage == null) {

				/*
				 * open ValidationWizard to validate SAP connection
				 * settings
				 */
				Wizard wizard = new ValidationWizard((RGConfiguration) editor.getConfiguration());
				NextActionWizardDialog dialog = new NextActionWizardDialog(Display.getCurrent().getActiveShell(), wizard);
				dialog.open();
				
			} else {
				
				/* display first validation error */
				Shell parentShell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
				MessageBox errorMessageBox = new MessageBox(parentShell, SWT.ICON_ERROR | SWT.OK);
				String msg = MessageFormat.format(Messages.RGConfGeneralPage_10, firstErrorMessage.getAttribute(IMarker.LOCATION) ,(String) firstErrorMessage.getAttribute(IMarker.MESSAGE));
				errorMessageBox.setMessage(msg); 
				errorMessageBox.setText(Messages.RGConfGeneralPage_11);
				errorMessageBox.open();
			}

		} catch (Exception exc) {
			handleException(exc);
		}
		
	}
}
