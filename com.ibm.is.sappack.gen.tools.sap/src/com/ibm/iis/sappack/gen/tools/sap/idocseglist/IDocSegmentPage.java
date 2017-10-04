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


import java.util.List;

import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.ibm.iis.sappack.gen.common.ui.editors.EditorPageBase;
import com.ibm.iis.sappack.gen.common.ui.editors.IControlFactory;
import com.ibm.iis.sappack.gen.common.ui.util.ImageProvider;
import com.ibm.iis.sappack.gen.common.ui.util.Utils;
import com.ibm.iis.sappack.gen.common.ui.wizards.NextActionWizardDialog;
import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.common.ui.util.Utilities;
import com.ibm.is.sappack.gen.tools.sap.importer.idoctypes.IDocType;


public class IDocSegmentPage extends EditorPageBase {

	public  static final String KEY_IDOCTYPENAME                  = "KEY_IDOCTYPENAME"; //$NON-NLS-1$
	public  static final String KEY_IDOCRELEASE                   = "KEY_IDOCRELEASE"; //$NON-NLS-1$
	public  static final String KEY_IDOCSEGMENTS                  = "KEY_IDOCSEGMENTS"; //$NON-NLS-1$
	public  static final String PAGE_NAME                         = Messages.IDocSegmentPage_0;
	public  static final String PROP_IDOC_BASIC_OR_EXTENSION_TYPE = "PROP_IDOC_BASIC_OR_EXTENSION_TYPE"; //$NON-NLS-1$
	private static final String HELP_ID                           = "idocsegment_list_editor"; //$NON-NLS-1$
	
	private boolean isJCOConfigured;

	
  	static String copyright() { 
 	   return Copyright.IBM_COPYRIGHT_SHORT; 
 	}

	public IDocSegmentPage() {
		super(PAGE_NAME, Messages.IDocSegmentPage_1, Messages.IDocSegmentPage_2, Utils.getHelpID(HELP_ID));

		// check if JCO is configured properly
		this.isJCOConfigured = Utilities.checkJCoAvailabilityWithDialog();
	}

	@Override
	public Image getImage() {
		return ImageProvider.getImage(ImageProvider.IMAGE_IDOCS_16);
	}

	@Override
	public void createControls(IControlFactory controlFactory, Composite parent) {
		Composite mainComposite = controlFactory.createComposite(parent, SWT.NULL);

		GridLayout layout = new GridLayout(3, false);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		mainComposite.setLayout(layout);
		mainComposite.setLayoutData(gridData);

		final Label labelIDocTypeName = controlFactory.createLabel(mainComposite, SWT.NULL);
		GridData labelGridData = new GridData(SWT.NULL, SWT.BEGINNING, false, false);
		labelIDocTypeName.setLayoutData(labelGridData);
		labelIDocTypeName.setText(Messages.CustomIDocWizardPage_3);

		//	Composite textIdocComposite = new Composite(container, SWT.FILL);
		//	textIdocComposite.setLayout(new GridLayout(1, false));
		final Text textIDocTypeName = controlFactory.createText(mainComposite, SWT.BORDER | SWT.SINGLE);
		gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
		textIDocTypeName.setLayoutData(gridData);
		this.configureTextForProperty(textIDocTypeName, KEY_IDOCTYPENAME);

		Composite buttonComposite = controlFactory.createComposite(mainComposite, SWT.NONE);
		buttonComposite.setLayout(new GridLayout(1, false));
		gridData = new GridData(SWT.LEFT, SWT.TOP, false, false);
		gridData.verticalSpan = 3;
		buttonComposite.setLayoutData(gridData);
		Button browseButton = controlFactory.createButton(buttonComposite, SWT.PUSH);
		browseButton.setText(Messages.IDocSegmentPage_4);

		// dummy
		controlFactory.createComposite(mainComposite, SWT.BEGINNING);

		// basic type or extension type option
		Composite radioComposite = controlFactory.createComposite(mainComposite, SWT.BEGINNING);
		radioComposite.setLayout(new GridLayout(1, false));
		final Button basicTypeButton = controlFactory.createButton(radioComposite, SWT.RADIO);
		basicTypeButton.setText(Messages.CustomIDocWizardPage_7);
		basicTypeButton.setSelection(true);
		final Button extensionTypeButton = controlFactory.createButton(radioComposite, SWT.RADIO);
		extensionTypeButton.setText(Messages.CustomIDocWizardPage_8);
		this.configureRadioButtonsForProperty(new Button[] {basicTypeButton, extensionTypeButton }, PROP_IDOC_BASIC_OR_EXTENSION_TYPE);

		Label releaseLabel = controlFactory.createLabel(mainComposite, SWT.NONE);
		releaseLabel.setText(Messages.IDocSegmentPage_5);
		final Combo releaseCombo = controlFactory.createCombo(mainComposite, SWT.NONE);
		releaseCombo.add(""); //$NON-NLS-1$
		releaseCombo.add("700"); //$NON-NLS-1$
		releaseCombo.add("640"); //$NON-NLS-1$
		releaseCombo.add("620"); //$NON-NLS-1$
		releaseCombo.add("46D"); //$NON-NLS-1$
		releaseCombo.add("46C"); //$NON-NLS-1$
		releaseCombo.add("46B"); //$NON-NLS-1$
		releaseCombo.add("46A"); //$NON-NLS-1$
		this.configureComboForPropertyStr(releaseCombo, KEY_IDOCRELEASE);

		final Label labelIDocSegmentNames = controlFactory.createLabel(mainComposite, SWT.NULL);
		//	gridData = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		labelIDocSegmentNames.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
		labelIDocSegmentNames.setText(Messages.CustomIDocWizardPage_4);

		final Text textIDocSegmentNames = controlFactory.createText(mainComposite, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
		gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
			gridData.verticalSpan = 3;
		textIDocSegmentNames.setLayoutData(gridData);
		this.configureTextForProperty(textIDocSegmentNames, KEY_IDOCSEGMENTS);

		// dummy	
		Composite dummyComp = controlFactory.createComposite(mainComposite, SWT.NONE);

		dummyComp.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, false, true));
		dummyComp = controlFactory.createComposite(mainComposite, SWT.NONE);
		dummyComp.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, false, true));
		
		browseButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				IDocBrowserWizard wizard = new IDocBrowserWizard(textIDocTypeName.getText());
				WizardDialog dialog = new NextActionWizardDialog(null, wizard);
				int res = dialog.open();
				if (res == WizardDialog.CANCEL) {
					return;
				}
				IDocType idocType = wizard.getIDOCType();
				List<String> segments = wizard.getIDOCSegments();
				String segmentText = Utils.getTableTextFieldFromList(segments);
				
				textIDocTypeName.setText(idocType.getName());
				boolean ext = idocType.isExtendedIDocType();
				selectButtonWithEvent(basicTypeButton, !ext);
				selectButtonWithEvent(extensionTypeButton, ext);
				releaseCombo.setText(idocType.getRelease());
				
				textIDocSegmentNames.setText(segmentText);
				
			}
			
			
		});

		// enable/disable the IDocText field and the Browse button
		textIDocTypeName.setEnabled(this.isJCOConfigured);
		browseButton.setEnabled(this.isJCOConfigured);
	}

}
