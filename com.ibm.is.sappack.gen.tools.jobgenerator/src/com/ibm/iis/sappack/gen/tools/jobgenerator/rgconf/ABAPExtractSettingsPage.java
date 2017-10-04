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


import java.io.BufferedReader;
import java.io.StringReader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
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

import com.ibm.iis.sappack.gen.common.ui.editors.ConfigurationBase;
import com.ibm.iis.sappack.gen.common.ui.editors.IControlFactory;
import com.ibm.iis.sappack.gen.common.ui.util.Utils;
import com.ibm.is.sappack.gen.common.Constants;
import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.common.ui.WidgetIDConstants;
import com.ibm.is.sappack.gen.common.ui.WidgetIDUtils;
import com.ibm.is.sappack.gen.tools.sap.utilities.Utilities;


public class ABAPExtractSettingsPage extends RGConfPageBase {
	public static final String TABNAME = Messages.ABAPExtractSettingsPage_0;

	public static final String TF_METHOD_NAME_RFC = "RFC"; //$NON-NLS-1$
	public static final String TF_METHOD_NAME_CPIC = "CPIC"; //$NON-NLS-1$

	public static final String[] TRANSFER_METHOD_RFC_CPIPC = new String[] { TF_METHOD_NAME_RFC, TF_METHOD_NAME_CPIC };
	public static final String[] TRANSFER_METHOD_RFC = new String[] { TF_METHOD_NAME_RFC };

	public static final int    MAX_FLOW_COUNT = 20;
	public static final String PROP_SAP_GATEWAYHOST = "SAP_GATEWAYHOST"; //$NON-NLS-1$
	public static final String PROP_SAP_GATEWAYSERVICE = "SAP_GATEWAYSERVICE"; //$NON-NLS-1$
	public static final String PROP_TRANSFER_METHOD = "PROP_TRANSFER_METHOD"; //$NON-NLS-1$
	public static final String PROP_CPIC_USE_SAP_LOGON_DETAILS = "PROP_CPIC_USE_SAP_LOGON_DETAILS"; //$NON-NLS-1$
	public static final String PROP_CPIC_USER = "PROP_CPIC_USER"; //$NON-NLS-1$
	public static final String PROP_CPIC_PASSWORD = "PROP_CPIC_PASSWORD"; //$NON-NLS-1$
	public static final String PROP_CPIC_MAX_FLOWS = "PROP_CPIC_MAX_FLOWS"; //$NON-NLS-1$
	public static final String PROP_CREATE_RFC_DEST_AUTOMATICALLY = "PROP_CREATE_RFC_DEST_AUTOMATICALLY"; //$NON-NLS-1$
	public static final String PROP_DELETE_RFC_DEST_IF_EXIST = "PROP_DELETE_RFC_DEST_IF_EXIST"; //$NON-NLS-1$
	public static final String PROP_RFC_NAME_DYN_OR_STATIC = "PROP_RFC_NAME_DYN_OR_STATIC"; //$NON-NLS-1$
	public static final String PROP_DYNAMIC_RFC_PREFIX = "PROP_DYNAMIC_RFC_PREFIX"; //$NON-NLS-1$
	public static final String PROP_MAX_FLOWS_RFC = "PROP_MAX_FLOWS_RFC"; //$NON-NLS-1$
	public static final String PROP_BG_ENABLED = "PROP_BG_ENABLED"; //$NON-NLS-1$
	public static final String PROP_BG_VARIANT_PREFIX = "PROP_BG_VARIANT_PREFIX"; //$NON-NLS-1$
	public static final String PROP_STARTUP_DELAY = "PROP_STARTUP_DELAY"; //$NON-NLS-1$
	public static final String PROP_RFC_DEST_TEXT = "PROP_RFC_DEST_TEXT"; //$NON-NLS-1$

	private Combo dsMaxFlowsInJobRFCCombo;
	private Button createRfcDestAutomatically;
	private Button deleteRfcDestIfExisting;
	private Text sapGatewayHostText;
	private Text sapGatewayServiceText;
	private Composite stackComposite;
	private StackLayout stackLayout;
	private Combo transferMethodCombo;
	private Composite transferMethodRFCComposite;
	private Composite transferMethodCPICComposite;
	private Text cpicUserText;
	private Text cpicPasswordText;
	private Button cpicUseSAPLogonDetailsButton;
	private Composite cpicUserPWComposite;
	private Combo dsMaxFlowsInJobCPICCombo;
	private String[] transferMethods = new String[] { TF_METHOD_NAME_RFC };
	private Button useStaticRFCNames;
	private Button useDynamicRFCNames;
	private Text dynamicRFCPrefix;
	private Button bgEnabledButton;
	private Text bgVariantPrefix;
	private Text bgProgStartupDelay;
	private Text rfcDestinationsText;


  	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	


	public ABAPExtractSettingsPage() {
		super(TABNAME, Messages.ABAPExtractSettingsPage_1, Messages.ABAPExtractSettingsPage_2,
		      Utils.getHelpID("rgconfeditor_abap_extract")); //$NON-NLS-1$
	}

	@Override
	public void createControls(IControlFactory controlFactory, Composite parent) {

		Composite mainComposite = controlFactory.createComposite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(1, false);

		mainComposite.setLayout(layout);
		mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		ModifyListener standardModifyListener = new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				updateEnablement();
			}
		};

		SelectionListener standardSelectionListener = new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				updateEnablement();
			}

		};

		if (this.transferMethods.length > 1) {
			Composite transferMethodComposite = controlFactory.createGroup(mainComposite, Messages.JobGeneratorAbapPage_8, SWT.NONE);
			transferMethodComposite.setLayout(new GridLayout(1, false));
			transferMethodComposite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));

			transferMethodCombo = new Combo(transferMethodComposite, SWT.SINGLE | SWT.READ_ONLY | SWT.BORDER);
			transferMethodCombo.setItems(transferMethods);
			transferMethodCombo.select(0);
			this.configureComboForProperty(transferMethodCombo, PROP_TRANSFER_METHOD);
			transferMethodCombo.addModifyListener(standardModifyListener);

			stackComposite = controlFactory.createComposite(transferMethodComposite, SWT.NULL);
			stackComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
			stackLayout = new StackLayout();
			stackLayout.marginHeight = 0;
			stackLayout.marginWidth = 0;
			stackComposite.setLayout(stackLayout);

			transferMethodCPICComposite = controlFactory.createComposite(stackComposite, SWT.NONE);
			transferMethodCPICComposite.setLayout(new GridLayout(1, false));
			transferMethodCPICComposite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));

			cpicUseSAPLogonDetailsButton = controlFactory.createButton(transferMethodCPICComposite, SWT.CHECK);
			cpicUseSAPLogonDetailsButton.setText(Messages.JobGeneratorAbapPage_10);
			configureCheckboxForProperty(cpicUseSAPLogonDetailsButton, PROP_CPIC_USE_SAP_LOGON_DETAILS);
			cpicUseSAPLogonDetailsButton.addSelectionListener(standardSelectionListener);
			cpicUserPWComposite = controlFactory.createComposite(transferMethodCPICComposite, SWT.NONE);
			cpicUserPWComposite.setLayout(new GridLayout(2, false));
			cpicUserPWComposite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));

			Label cpicUserLabel = controlFactory.createLabel(cpicUserPWComposite, SWT.NONE);
			cpicUserLabel.setText(Messages.JobGeneratorAbapPage_11);
			cpicUserText = controlFactory.createText(cpicUserPWComposite, SWT.BORDER);
			cpicUserText.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
			this.configureTextForJobParameterProperty(cpicUserText, PROP_CPIC_USER);
			cpicUserText.addModifyListener(standardModifyListener);

			Label cpicPasswordLabel = controlFactory.createLabel(cpicUserPWComposite, SWT.NONE);
			cpicPasswordLabel.setText(Messages.JobGeneratorAbapPage_12);
			cpicPasswordText = controlFactory.createText(cpicUserPWComposite, SWT.BORDER | SWT.PASSWORD);
			cpicPasswordText.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
			configureTextForJobParameterProperty(cpicPasswordText, PROP_CPIC_PASSWORD);
			cpicPasswordText.addModifyListener(standardModifyListener);

			Composite additionalCPICOptionsComposite = controlFactory.createComposite(transferMethodCPICComposite, SWT.NONE);
			additionalCPICOptionsComposite.setLayout(new GridLayout(2, false));
			additionalCPICOptionsComposite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));

			Label dsMaxFlowsInJobCPICLabel = controlFactory.createLabel(additionalCPICOptionsComposite, SWT.NONE);
			dsMaxFlowsInJobCPICLabel.setText(Messages.JobGeneratorAbapPage_13);
			dsMaxFlowsInJobCPICCombo = new Combo(additionalCPICOptionsComposite, SWT.SINGLE | SWT.READ_ONLY | SWT.BORDER);
			dsMaxFlowsInJobCPICCombo.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
			dsMaxFlowsInJobCPICCombo.setItems(new String[] { "1", "2", "3", "4", "5" }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
			dsMaxFlowsInJobCPICCombo.select(0);
			this.configureComboForPropertyStr(dsMaxFlowsInJobCPICCombo, PROP_CPIC_MAX_FLOWS);
			dsMaxFlowsInJobCPICCombo.addModifyListener(standardModifyListener);
		} else {
			stackComposite = controlFactory.createComposite(mainComposite, SWT.NULL);
			stackComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			stackLayout = new StackLayout();
			stackLayout.marginHeight = 0;
			stackLayout.marginWidth = 0;
			stackComposite.setLayout(stackLayout);
		}

		// RFC transfer method 

		transferMethodRFCComposite = controlFactory.createComposite(stackComposite, SWT.NONE); // new Group(stackComposite, SWT.NONE);
		//			transferMethodRFCComposite.setText(Messages.JobGeneratorAbapPage_19);
		transferMethodRFCComposite.setLayout(new GridLayout(1, false));
		transferMethodRFCComposite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));

		Composite gwGroup = controlFactory.createGroup(transferMethodRFCComposite, Messages.JobGeneratorAbapTransferMethodPage_2, SWT.NONE);
		gwGroup.setLayout(new GridLayout(2, false));
		gwGroup.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));

		Label sapGatewayHostLabel = controlFactory.createLabel(gwGroup, SWT.NONE);
		sapGatewayHostLabel.setText(Messages.JobGeneratorAbapPage_20);
		sapGatewayHostText = controlFactory.createText(gwGroup, SWT.BORDER);
		sapGatewayHostText.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
		this.configureTextForJobParameterProperty(sapGatewayHostText, PROP_SAP_GATEWAYHOST);
		sapGatewayHostText.addModifyListener(standardModifyListener);
		WidgetIDUtils.assignID(sapGatewayHostText, WidgetIDConstants.GEN_SAPGATEWAYHOSTTEXT);

		Label sapGatewayServiceLabel = controlFactory.createLabel(gwGroup, SWT.NONE);
		sapGatewayServiceLabel.setText(Messages.JobGeneratorAbapPage_21);
		sapGatewayServiceText = controlFactory.createText(gwGroup, SWT.BORDER);
		sapGatewayServiceText.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
		this.configureTextForJobParameterProperty(sapGatewayServiceText, PROP_SAP_GATEWAYSERVICE);
		sapGatewayServiceText.addModifyListener(standardModifyListener);
		WidgetIDUtils.assignID(sapGatewayServiceText, WidgetIDConstants.GEN_SAPGATEWAYSERVICETEXT);

		Composite rfcCreationGroup = controlFactory.createGroup(transferMethodRFCComposite, Messages.JobGeneratorAbapTransferMethodPage_3, SWT.NONE);
		rfcCreationGroup.setLayout(new GridLayout(2, false));
		rfcCreationGroup.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));

		/*
		Label invisibleSpacer = controlFactory.createLabel(rfcCreationGroup, SWT.NONE);
		invisibleSpacer.setVisible(false);
		GridData invisibleSpacerLayout = new GridData();
		invisibleSpacerLayout.horizontalSpan = 2;
		invisibleSpacer.setLayoutData(invisibleSpacerLayout);
		*/
		createRfcDestAutomatically = controlFactory.createButton(rfcCreationGroup, SWT.CHECK);
		createRfcDestAutomatically.setText(Messages.JobGeneratorAbapPage_25);
		createRfcDestAutomatically.setSelection(false);
		this.configureCheckboxForProperty(createRfcDestAutomatically, PROP_CREATE_RFC_DEST_AUTOMATICALLY);
		createRfcDestAutomatically.addSelectionListener(standardSelectionListener);
		WidgetIDUtils.assignID(createRfcDestAutomatically, WidgetIDConstants.GEN_CREATERFCDESTAUTO);

		deleteRfcDestIfExisting = controlFactory.createButton(rfcCreationGroup, SWT.CHECK);
		deleteRfcDestIfExisting.setText(Messages.JobGeneratorAbapPage_26);
		deleteRfcDestIfExisting.setEnabled(createRfcDestAutomatically.getSelection());
		this.configureCheckboxForProperty(deleteRfcDestIfExisting, PROP_DELETE_RFC_DEST_IF_EXIST);
		deleteRfcDestIfExisting.addSelectionListener(standardSelectionListener);
		WidgetIDUtils.assignID(deleteRfcDestIfExisting, WidgetIDConstants.GEN_DELETERFCDESTIFEXIST);

		/*
		invisibleSpacer = controlFactory.createLabel(transferMethodRFCComposite, SWT.NONE);
		invisibleSpacer.setVisible(false);
		invisibleSpacerLayout = new GridData();
		invisibleSpacerLayout.horizontalSpan = 2;
		invisibleSpacer.setLayoutData(invisibleSpacerLayout);
		*/

		Composite rfcDestNamesGroup = controlFactory.createGroup(transferMethodRFCComposite, Messages.JobGeneratorAbapPage_14, SWT.NONE);
		rfcDestNamesGroup.setLayout(new GridLayout(2, false));
		GridData rfcDestNamesGroupGridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
		rfcDestNamesGroupGridData.horizontalSpan = 2;
		rfcDestNamesGroup.setLayoutData(rfcDestNamesGroupGridData);

		Composite rfcDestNamesRadioComposite = controlFactory.createComposite(rfcDestNamesGroup, SWT.NONE);
		rfcDestNamesRadioComposite.setLayout(new GridLayout(1, false));
		GridData radioCompGD = new GridData();
		radioCompGD.horizontalSpan = 2;
		rfcDestNamesRadioComposite.setLayoutData(radioCompGD);

		useStaticRFCNames = controlFactory.createButton(rfcDestNamesRadioComposite, SWT.RADIO);
		useStaticRFCNames.setText(Messages.JobGeneratorAbapPage_15);
		useStaticRFCNames.setSelection(true);
		useStaticRFCNames.addSelectionListener(standardSelectionListener);
		useDynamicRFCNames = controlFactory.createButton(rfcDestNamesRadioComposite, SWT.RADIO);
		useDynamicRFCNames.setText(Messages.JobGeneratorAbapPage_16);
		useDynamicRFCNames.setSelection(false);
		this.configureRadioButtonsForProperty(new Button[] { useStaticRFCNames, useDynamicRFCNames }, PROP_RFC_NAME_DYN_OR_STATIC);
		useDynamicRFCNames.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (useDynamicRFCNames.getSelection()) {
					dsMaxFlowsInJobRFCCombo.setItems(getMaxFlowsComboContent(MAX_FLOW_COUNT));
					dsMaxFlowsInJobRFCCombo.select(0);
				}
				updateEnablement();
			}
		});

		Label dynamicRFCPrefixLabel = controlFactory.createLabel(rfcDestNamesGroup, SWT.NONE);
		dynamicRFCPrefixLabel.setText(Messages.JobGeneratorAbapPage_17);
		dynamicRFCPrefix = controlFactory.createText(rfcDestNamesGroup, SWT.BORDER);
		dynamicRFCPrefix.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
		this.configureTextForProperty(dynamicRFCPrefix, PROP_DYNAMIC_RFC_PREFIX);

		/*
		Label fileSelectionLabel = controlFactory.createLabel(rfcDestNamesGroup, SWT.NONE);
		fileSelectionLabel.setText(Messages.JobGeneratorAbapPage_22);

		
		Composite fileSelectionComposite = controlFactory.createComposite(rfcDestNamesGroup, SWT.NONE);
		GridLayout fileSelectionCompositeLayout = new GridLayout();
		fileSelectionCompositeLayout.numColumns = 2;
		fileSelectionCompositeLayout.marginWidth = 0;
		fileSelectionCompositeLayout.marginHeight = 0;
		fileSelectionComposite.setLayout(fileSelectionCompositeLayout);
		fileSelectionComposite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));

		sapRfcDestinationsFileCombo = new Combo(fileSelectionComposite, SWT.SINGLE | SWT.BORDER);
		sapRfcDestinationsFileCombo.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
		sapRfcDestinationsFileCombo.addModifyListener(standardModifyListener);
		WidgetIDUtils.assignID(sapRfcDestinationsFileCombo, WidgetIDConstants.GEN_SAPRFCDESTINATIONSFILECOMBO);

		// browse (part of the original composite)
		rfcDestinationsFileBrowseButton = controlFactory.createButton(fileSelectionComposite, SWT.PUSH);
		GridData buttonData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		rfcDestinationsFileBrowseButton.setLayoutData(buttonData);
		rfcDestinationsFileBrowseButton.setText(Messages.JobGeneratorAbapPage_23);
		rfcDestinationsFileBrowseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.OPEN);
				String path = dialog.open();
				sapRfcDestinationsFileCombo.setText(path);
			}
		});
		WidgetIDUtils.assignID(rfcDestinationsFileBrowseButton, WidgetIDConstants.GEN_RFCDESTINATIONSFILEBROWSEBUTTON);
		*/
		Label rfcDestLabel = controlFactory.createLabel(rfcDestNamesGroup, SWT.NONE);
		rfcDestLabel.setText(Messages.ABAPExtractSettingsPage_3);
		rfcDestLabel.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
		rfcDestinationsText = controlFactory.createText(rfcDestNamesGroup, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
		GridData rfcDestGD = new GridData(SWT.FILL, SWT.TOP, true, false);
		rfcDestGD.heightHint = 100;
		rfcDestinationsText.setLayoutData(rfcDestGD);
		this.configureTextForProperty(rfcDestinationsText, PROP_RFC_DEST_TEXT);
		this.rfcDestinationsText.addModifyListener(standardModifyListener);

		Label dsMaxFlowsInJobLabel = controlFactory.createLabel(rfcDestNamesGroup, SWT.NONE);
		dsMaxFlowsInJobLabel.setText(Messages.JobGeneratorAbapPage_24);
		dsMaxFlowsInJobRFCCombo = new Combo(rfcDestNamesGroup, SWT.SINGLE | SWT.READ_ONLY | SWT.BORDER);
		dsMaxFlowsInJobRFCCombo.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
		this.configureComboForPropertyStr(dsMaxFlowsInJobRFCCombo, PROP_MAX_FLOWS_RFC);
		WidgetIDUtils.assignID(dsMaxFlowsInJobRFCCombo, WidgetIDConstants.GEN_DSMAXFLOWSINJOBCOMBO);

		//////
		Composite bgGroup = controlFactory.createGroup(transferMethodRFCComposite, Messages.ABAPExtractSettingsPage_4, SWT.NONE);
		bgGroup.setLayout(new GridLayout(1, false));
		bgGroup.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
		bgEnabledButton = controlFactory.createButton(bgGroup, SWT.CHECK);
		bgEnabledButton.setText(Messages.ABAPExtractSettingsPage_5);
		this.configureCheckboxForProperty(bgEnabledButton, PROP_BG_ENABLED);
		bgEnabledButton.addSelectionListener(standardSelectionListener);

		Composite bgOptions = controlFactory.createComposite(bgGroup, SWT.NONE);
		bgOptions.setLayout(new GridLayout(2, false));
		bgOptions.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
		Label varPrefixLabel = controlFactory.createLabel(bgOptions, SWT.NONE);
		varPrefixLabel.setText(Messages.ABAPExtractSettingsPage_6);
		bgVariantPrefix = controlFactory.createText(bgOptions, SWT.BORDER);
		bgVariantPrefix.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
		this.configureTextForProperty(bgVariantPrefix, PROP_BG_VARIANT_PREFIX);
		bgVariantPrefix.addModifyListener(standardModifyListener);

		Label startupDelayLabel = controlFactory.createLabel(bgOptions, SWT.NONE);
		startupDelayLabel.setText(Messages.ABAPExtractSettingsPage_7);
		bgProgStartupDelay = controlFactory.createText(bgOptions, SWT.BORDER);
		bgProgStartupDelay.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
		this.configureTextForProperty(bgProgStartupDelay, PROP_STARTUP_DELAY);
		bgProgStartupDelay.addVerifyListener(new Utilities.ForceNumericInput());
		bgProgStartupDelay.addModifyListener(standardModifyListener);

		updateEnablement();

	}

	private void updateEnablement() {
		if (this.transferMethodCombo == null || TF_METHOD_NAME_RFC.equals(transferMethods[transferMethodCombo.getSelectionIndex()])) {
			stackLayout.topControl = transferMethodRFCComposite;
			stackComposite.layout();

		} else if (TF_METHOD_NAME_CPIC.equals(transferMethods[transferMethodCombo.getSelectionIndex()])) {
			stackLayout.topControl = transferMethodCPICComposite;
			stackComposite.layout();

		}
		boolean bgEnabled = bgEnabledButton.getSelection();
		bgVariantPrefix.setEnabled(bgEnabled);
		bgProgStartupDelay.setEnabled(bgEnabled);
		if (bgEnabled) {
			bgVariantPrefix.setBackground(null);
			bgProgStartupDelay.setBackground(null);
		}
		else {
			bgVariantPrefix.setBackground(Utils.COLOR_GRAY);
			bgProgStartupDelay.setBackground(Utils.COLOR_GRAY);
		}

		boolean createAutoEnabled = this.createRfcDestAutomatically.getSelection();
		deleteRfcDestIfExisting.setEnabled(createAutoEnabled);
		if (!createAutoEnabled) {
			if (useDynamicRFCNames.getSelection()) {
				this.selectButtonWithEvent(useDynamicRFCNames, false);
				this.selectButtonWithEvent(useStaticRFCNames, true);
//				this.editor.getPropertiesMap().put(PROP_RFC_NAME_DYN_OR_STATIC, 0);
			}

		}
		useDynamicRFCNames.setEnabled(createAutoEnabled);

		boolean isUseDynRFCnamesEnabled = this.useDynamicRFCNames.getSelection(); 
		this.dynamicRFCPrefix.setEnabled(isUseDynRFCnamesEnabled);
		if (isUseDynRFCnamesEnabled) {
			this.dynamicRFCPrefix.setBackground(null);
		}
		else {
			this.dynamicRFCPrefix.setBackground(Utils.COLOR_GRAY);			
		}
		boolean isUseStaticRFCNamesEnabled = this.useStaticRFCNames.getSelection();
		this.rfcDestinationsText.setEnabled(isUseStaticRFCNamesEnabled);
		if (isUseStaticRFCNamesEnabled) {
			this.rfcDestinationsText.setBackground(null);
		}
		else {
			this.rfcDestinationsText.setBackground(Utils.COLOR_GRAY);			
		}

		// update Flow Combo
		updateFlowCombo();
	}

	private void updateFlowCombo() {

		int prevFlowCount = dsMaxFlowsInJobRFCCombo.getItemCount();
		int newSelIdx     = dsMaxFlowsInJobRFCCombo.getSelectionIndex();

		// set the maximum numbers of flows
		if (this.useStaticRFCNames.getSelection()) {
			// ==> 'Use STATIC RFC Destinations' is active 
			Map<String,String> rfcDestMap = new HashMap<String, String>();

			if (loadRFCDestinationNameMap(this.rfcDestinationsText.getText(), rfcDestMap) == null) {
				int newFlowCount = rfcDestMap.size();
				if (prevFlowCount != newFlowCount) {
					if (newFlowCount > MAX_FLOW_COUNT) {
						newFlowCount = MAX_FLOW_COUNT;
					}
		 			dsMaxFlowsInJobRFCCombo.setItems(getMaxFlowsComboContent(newFlowCount));
					newSelIdx = newFlowCount - 1;
				}
			}
			else {
				// some of the static destinations are invalid --> no update of the flow box
				;
			}
		}
		else {
			// ==> 'Use RFC Destinations Prefix' is active 
			if (prevFlowCount == 0) {
	 			dsMaxFlowsInJobRFCCombo.setItems(getMaxFlowsComboContent(MAX_FLOW_COUNT));
			}
		}

		if (prevFlowCount == 0) {
			String lastValue = this.editor.getConfiguration().get(PROP_MAX_FLOWS_RFC);
			newSelIdx        = Integer.parseInt(lastValue) - 1;
		}
		dsMaxFlowsInJobRFCCombo.select(newSelIdx);
	}

	public static String[] getMaxFlowsComboContent(int max) {
		List<String> dsMaxFlowsInJobComboContent = new ArrayList<String>();
		for (int i = 1; i <= max; i++) {
			dsMaxFlowsInJobComboContent.add(Integer.toString(i));
		}
		return dsMaxFlowsInJobComboContent.toArray(new String[dsMaxFlowsInJobComboContent.size()]);
	}

	public static String loadRFCDestinationNameMap(String rfcDestinationsAsTextFlow, Map<String, String> resultMapOut) {
		String errMsg = null;
		
		if (resultMapOut == null) {
			throw new IllegalArgumentException("outMap instance does not exist"); //$NON-NLS-1$
		}

		if (rfcDestinationsAsTextFlow != null) {
			String lastToken = Constants.EMPTY_STRING;

			try {
				BufferedReader reader = new BufferedReader(new StringReader(rfcDestinationsAsTextFlow));
				String rfcDestinationLine = null;

				// separate text lines
				while ((rfcDestinationLine = reader.readLine()) != null) {
					rfcDestinationLine = rfcDestinationLine.trim();

					// do not process empty lines
					if (!rfcDestinationLine.isEmpty()) {
						lastToken = rfcDestinationLine;
						StringTokenizer tokenizer = new StringTokenizer(rfcDestinationLine);

						// separate destination and program id
						String rfcDestination = tokenizer.nextToken();
						lastToken = rfcDestination;
						String programId = tokenizer.nextToken();
						lastToken = programId;
						if (!resultMapOut.containsKey(rfcDestination)) {
							resultMapOut.put(rfcDestination, programId);
						}
					}
				}
			}
			catch (Exception e) {
				errMsg = MessageFormat.format(Messages.ABAPExtractSettingsPage_8, lastToken);
			}
		}

		return errMsg;
	}

	public static String loadRFCDestinationNameMap(ConfigurationBase map, Map<String, String> outResult) {

		return(loadRFCDestinationNameMap(map.get(PROP_RFC_DEST_TEXT), outResult)); 
	}
}
