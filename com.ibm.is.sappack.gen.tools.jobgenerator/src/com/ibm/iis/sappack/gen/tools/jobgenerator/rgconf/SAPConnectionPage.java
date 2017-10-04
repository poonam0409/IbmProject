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


import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.ibm.iis.sappack.gen.common.ui.editors.IControlFactory;
import com.ibm.iis.sappack.gen.common.ui.editors.PropertiesConstants;
import com.ibm.iis.sappack.gen.common.ui.util.ImageProvider;
import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.common.util.StringUtils;
import com.ibm.is.sappack.gen.tools.sap.utilities.Utilities;


public class SAPConnectionPage extends RGConfPageBase {
	private Text sapUserText;
	private Text sapUserPasswordText;
	private Text sapUserClientNumberText;
	private Text sapUserLanguageText;
	private Text dsSAPConnectionNameText;

	
  	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	

	public SAPConnectionPage(String tabName, String title, String descriptionText, String helpID) {
		super(tabName, title, descriptionText, helpID);
	}

	@Override
	public void createControls(IControlFactory controlFactory, Composite parent) {
		Composite sourceComposite = controlFactory.createGroup(parent, Messages.SAPConnectionPage_0, SWT.NONE);
		GridLayout sourceLayout = new GridLayout();
		sourceLayout.numColumns = 2;
		sourceComposite.setLayout(sourceLayout);
		sourceComposite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));

		GridData textGD = new GridData(SWT.FILL, SWT.FILL, true, false);

		Label sapConnectionLabel = controlFactory.createLabel(sourceComposite, SWT.NONE);
		sapConnectionLabel.setText(this.getReadablePropertyNameForTextLabel(PropertiesConstants.PROP_SAP_CONN_NAME));
		
		this.dsSAPConnectionNameText = controlFactory.createText(sourceComposite, SWT.BORDER);
		this.dsSAPConnectionNameText.setLayoutData(textGD);
		this.configureTextForJobParameterProperty(this.dsSAPConnectionNameText, PropertiesConstants.PROP_SAP_CONN_NAME);	
		this.dsSAPConnectionNameText.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				// TODO Auto-generated method stub	
			}
			@Override
			public void focusLost(FocusEvent e) {
				String selection = dsSAPConnectionNameText.getText();
	            if (!StringUtils.isJobParamVariable(selection)) {
					dsSAPConnectionNameText.setText(selection.toUpperCase());
				}
			}
		});

		Label sapUserLabel = controlFactory.createLabel(sourceComposite, SWT.NONE);
		sapUserLabel.setText(this.getReadablePropertyNameForTextLabel(PropertiesConstants.PROP_SAP_USER));
		sapUserText = controlFactory.createText(sourceComposite, SWT.BORDER);
		sapUserText.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
		this.configureTextForJobParameterProperty(sapUserText, PropertiesConstants.PROP_SAP_USER);

		Label sapUserPasswordLabel = controlFactory.createLabel(sourceComposite, SWT.NONE);
		sapUserPasswordLabel.setText(this.getReadablePropertyNameForTextLabel(PropertiesConstants.PROP_SAP_PASSWORD));
		sapUserPasswordText = controlFactory.createText(sourceComposite, SWT.BORDER); // | SWT.PASSWORD);
		sapUserPasswordText.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
		sapUserPasswordText.addModifyListener(new ChangeEchoCharWhenJobParam());
		this.configureTextForJobParameterProperty(sapUserPasswordText, PropertiesConstants.PROP_SAP_PASSWORD);

		Label sapUserClientNumberLabel = controlFactory.createLabel(sourceComposite, SWT.NONE);
		sapUserClientNumberLabel.setText(this.getReadablePropertyNameForTextLabel(PropertiesConstants.PROP_SAP_CLIENT));
		sapUserClientNumberText = controlFactory.createText(sourceComposite, SWT.BORDER);
		sapUserClientNumberText.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
		sapUserClientNumberText.addVerifyListener(new Utilities.ForceNumericInput());
		this.configureTextForJobParameterProperty(sapUserClientNumberText, PropertiesConstants.PROP_SAP_CLIENT);

		Label sapUserLanguageLabel = controlFactory.createLabel(sourceComposite, SWT.NONE);
		sapUserLanguageLabel.setText(this.getReadablePropertyNameForTextLabel(PropertiesConstants.PROP_SAP_LANGUAGE));
		this.sapUserLanguageText = controlFactory.createText(sourceComposite, SWT.BORDER);
		this.sapUserLanguageText.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
		this.configureTextForJobParameterProperty(this.sapUserLanguageText, PropertiesConstants.PROP_SAP_LANGUAGE);
		this.sapUserLanguageText.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				// TODO Auto-generated method stub
			}
			@Override
			public void focusLost(FocusEvent e) {
				String selection = sapUserLanguageText.getText();
	            if (!StringUtils.isJobParamVariable(selection)) {
					sapUserLanguageText.setText(selection.toUpperCase());
				}				
			}
		});
	}

	@Override
	public Image getImage() {
		return ImageProvider.getImage(ImageProvider.IMAGE_SAP_CONNECTION_ICON);
	}
}
