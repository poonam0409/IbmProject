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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.ibm.iis.sappack.gen.common.ui.editors.IControlFactory;
import com.ibm.iis.sappack.gen.common.ui.util.Utils;
import com.ibm.is.sappack.gen.common.ui.Messages;


public class MoveTranscodeSettingsPage extends RGConfPageBase {
	public static final String TABNAME = Messages.MoveTranscodeSettingsPage_0;
	
	public static final String PROP_MOVE_REJECT_FILE_PATH          = "PROP_MOVE_REJECT_FILE_PATH";          //$NON-NLS-1$
	public static final String PROP_MOVE_MAX_NUMBER_FLOWS          = "PROP_MOVE_MAX_NUMBER_FLOWS";          //$NON-NLS-1$
	public static final String PROP_TRANSCODE_TARGET_LEGACY_ID     = "PROP_TRANSCODE_TARGET_LEGACY_ID";     //$NON-NLS-1$
	public static final String PROP_TRANSCODE_REFERENCE_FIELDS     = "PROP_TRANSCODE_REFERENCE_FIELDS";     //$NON-NLS-1$
	public static final String PROP_TRANSCODE_NON_REFERENCE_FIELDS = "PROP_TRANSCODE_NON_REFERENCE_FIELDS"; //$NON-NLS-1$
	public static final String PROP_TRANCODE_DOMAIN_VALUE_FIELDS   = "PROP_TRANSCODE_DOMAIN_VALUE_FIELDS";  //$NON-NLS-1$
	public static final String PROP_MARK_UNMAPPED_VALUES           = "PROP_MARK_UNMAPPED_VALUES";           //$NON-NLS-1$

	private Button transcodeReferenceFields    = null;
	private Button transcodeNonReferenceFields = null;
	private Button transcodeDomainValueFields  = null;
	private Button markUnmappedValues          = null;
	private Text   targetLegacyID              = null;
	

  	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	

	
	public MoveTranscodeSettingsPage() {
		super(TABNAME, Messages.MoveTranscodeSettingsPage_1, Messages.MoveTranscodeSettingsPage_2, Utils.getHelpID("rg_conf_movetranscode")); //$NON-NLS-1$
	}

	private void updateEnablement() {
		boolean isTransCodingEnabled;
		
		// enable target legacy ID if at least one transcoding option is selected
		isTransCodingEnabled = this.transcodeReferenceFields.getSelection() || 
		                       this.transcodeNonReferenceFields.getSelection() || 
		                       this.transcodeDomainValueFields.getSelection();
		this.targetLegacyID.setEnabled(isTransCodingEnabled);
		if (isTransCodingEnabled) {
			this.targetLegacyID.setBackground(null);
		}
		else {
			this.targetLegacyID.setBackground(Utils.COLOR_GRAY);
		}
		
	}
	
	
	
	
	@Override
	public void createControls(IControlFactory controlFactory, Composite parent) {
		parent = controlFactory.createComposite(parent, SWT.NONE);
		parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		parent.setLayout(new GridLayout(2, true));
		
		Composite moveGroup = controlFactory.createGroup(parent, Messages.MoveTranscodeSettingsPage_3, SWT.NONE);
		moveGroup.setLayout(new GridLayout(2, false));
		moveGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		GridData textGD = new GridData(SWT.FILL, SWT.FILL, true, false);
		
		SelectionListener standardSelectionListener = new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				updateEnablement();
			}

		};
		

		Text rejectFileText = this.createLabelAndTextField(controlFactory, moveGroup, PROP_MOVE_REJECT_FILE_PATH, SWT.BORDER, textGD);
		this.configureTextForJobParameterProperty(rejectFileText, PROP_MOVE_REJECT_FILE_PATH);

		Label l = controlFactory.createLabel(moveGroup, SWT.NONE);
		String s = getReadablePropertyNameForTextLabel(PROP_MOVE_MAX_NUMBER_FLOWS);
		l.setText(s);
		Combo maxFlowsPerJob = controlFactory.createCombo(moveGroup, SWT.SINGLE | SWT.READ_ONLY | SWT.BORDER);
		maxFlowsPerJob.setLayoutData(textGD); // new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
		this.configureComboForPropertyStr(maxFlowsPerJob, PROP_MOVE_MAX_NUMBER_FLOWS);
		maxFlowsPerJob.setItems(ABAPExtractSettingsPage.getMaxFlowsComboContent(ABAPExtractSettingsPage.MAX_FLOW_COUNT));
		String flowVal = this.editor.getConfiguration().get(PROP_MOVE_MAX_NUMBER_FLOWS);
		maxFlowsPerJob.select(Integer.parseInt(flowVal)-1);

		Composite transcodeGroup = controlFactory.createGroup(parent, Messages.MoveTranscodeSettingsPage_4, SWT.NONE);
		transcodeGroup.setLayout(new GridLayout(1, false));
		transcodeGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		transcodeReferenceFields = controlFactory.createButton(transcodeGroup, SWT.CHECK);
		transcodeReferenceFields.setText(Messages.MoveTranscodeSettingsPage_5);
		this.configureCheckboxForProperty(transcodeReferenceFields, PROP_TRANSCODE_REFERENCE_FIELDS);
		transcodeReferenceFields.addSelectionListener(standardSelectionListener);
		
		transcodeNonReferenceFields = controlFactory.createButton(transcodeGroup, SWT.CHECK);
		transcodeNonReferenceFields.setText(Messages.MoveTranscodeSettingsPage_6);
		this.configureCheckboxForProperty(transcodeNonReferenceFields, PROP_TRANSCODE_NON_REFERENCE_FIELDS);
		transcodeNonReferenceFields.addSelectionListener(standardSelectionListener);
		
		transcodeDomainValueFields = controlFactory.createButton(transcodeGroup, SWT.CHECK);
		transcodeDomainValueFields.setText(Messages.MoveTranscodeSettingsPage_7);
		this.configureCheckboxForProperty(transcodeDomainValueFields, PROP_TRANCODE_DOMAIN_VALUE_FIELDS);
		transcodeDomainValueFields.addSelectionListener(standardSelectionListener);

		markUnmappedValues = controlFactory.createButton(transcodeGroup, SWT.CHECK);
		markUnmappedValues.setText(Messages.MoveTranscodeSettingsPage_8);
		this.configureCheckboxForProperty(markUnmappedValues, PROP_MARK_UNMAPPED_VALUES);
		markUnmappedValues.addSelectionListener(standardSelectionListener);
		
		targetLegacyID = this.createLabelAndTextField(controlFactory, transcodeGroup, PROP_TRANSCODE_TARGET_LEGACY_ID, SWT.BORDER, textGD);
		this.configureTextForJobParameterProperty(targetLegacyID, PROP_TRANSCODE_TARGET_LEGACY_ID);
		
		updateEnablement();

		
	}
	

}
