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
import com.ibm.iis.sappack.gen.tools.sap.rmconf.CWImportOptionsPage.ChkTblOptions;
import com.ibm.iis.sappack.gen.tools.sap.rmconf.CWImportOptionsPage.DataTypes;
import com.ibm.iis.sappack.gen.tools.sap.rmconf.CWImportOptionsPage.RelationTypes;
import com.ibm.iis.sappack.gen.tools.sap.rmconf.RMConfiguration.SupportedDB;
import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.common.ui.ModeManager;
import com.ibm.is.sappack.gen.tools.sap.constants.Constants;


public class NewRMConfPage extends NewWizardPageBase {

	private Button stagingButton;
	private Button alignmentButton;
	private Button preloadButton;


	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	

	public NewRMConfPage() {
		super("newrmconfpage", Messages.NewRMConfPage_0, ImageProvider.getImageDescriptor(ImageProvider.IMAGE_RMCONF_45)); //$NON-NLS-1$
		setDescription(Messages.NewRMConfPage_1);
	}

	@Override
	protected void createAdditionalControls(Composite parent) {
		if (ModeManager.isCWEnabled()) {
			Group comp = new Group(parent, SWT.NONE);
			comp.setLayout(new GridLayout(1, true));
			comp.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL | GridData.FILL_BOTH));
			comp.setText(Messages.NewRMConfPage_2);
			stagingButton = new Button(comp, SWT.RADIO);
			stagingButton.setText(Messages.NewRMConfPage_3);
			alignmentButton = new Button(comp, SWT.RADIO);
			alignmentButton.setText(Messages.NewRMConfPage_4);
			preloadButton = new Button(comp, SWT.RADIO);
			preloadButton.setText(Messages.NewRMConfPage_5);
			stagingButton.setSelection(true);
			alignmentButton.setSelection(false);
			preloadButton.setSelection(false);
		}
		PlatformUI.getWorkbench().getHelpSystem().setHelp(parent.getParent(), Utils.getHelpID("rmwizard_new_rmconf")); //$NON-NLS-1$
	}

	@Override
	protected String getNewFileNameDefault() {
		return "RapidModelerConfiguration"; //$NON-NLS-1$
	}

	@Override
	protected String getNewFileExtension() {
		return RMConfiguration.RMCONF_FILE_EXTENSION;
	}

	@Override
	public Map<String, String> getInitialProperties() {
		Map<String, String> initialProps = RMConfiguration.createEmptyRMConfiguation(ModeManager.getActiveMode());
		if (initialProps == null) {
			initialProps = new HashMap<String, String>();
		}
		initialProps.put("AUTHOR", System.getProperty("user.name"));  //$NON-NLS-1$//$NON-NLS-2$
		initialProps.put("VERSION", "1.0"); //$NON-NLS-1$ //$NON-NLS-2$

		// if buttons were created
		if (stagingButton != null) {
			if (stagingButton.getSelection()) {				
				initialProps.put(CWImportOptionsPage.SETTING_RELATION_IDX, String.valueOf(RelationTypes.SAPPKsNoFKsNotEnforced.ordinal()));
				
				initialProps.put(CWImportOptionsPage.SETTING_COL_TYPE_IDX, String.valueOf(DataTypes.VarCharType.ordinal()));
				initialProps.put(CWImportOptionsPage.SETTING_COL_LEN_FACTOR, String.valueOf(1));
				initialProps.put(CWImportOptionsPage.SETTING_COL_DEFAULT_VAL, ""); //$NON-NLS-1$
				initialProps.put(CWImportOptionsPage.SETTING_COL_IS_NULLABLE, String.valueOf(false));
				
				initialProps.put(CWImportOptionsPage.SETTING_ADD_TECH_FIELDS, String.valueOf(true));
				initialProps.put(CWImportOptionsPage.SETTING_TFLD_IS_NULLABLE, String.valueOf(true));
				
				initialProps.put(CWImportOptionsPage.SETTING_CHK_TBL_OPT_IDX, String.valueOf(ChkTblOptions.JoinedAndCheckTables.ordinal()));
				initialProps.put(PropertiesConstants.KEY_DOCUMENTATION, Messages.NewRMConfPage_6);
			} else if (alignmentButton.getSelection()) {				
				initialProps.put(CWImportOptionsPage.SETTING_RELATION_IDX, String.valueOf(RelationTypes.PKsNoFKs.ordinal()));
				
				initialProps.put(CWImportOptionsPage.SETTING_COL_TYPE_IDX, String.valueOf(DataTypes.VarCharType.ordinal()));
				initialProps.put(CWImportOptionsPage.SETTING_COL_LEN_FACTOR, String.valueOf(2));
				initialProps.put(CWImportOptionsPage.SETTING_COL_DEFAULT_VAL, ""); //$NON-NLS-1$
				initialProps.put(CWImportOptionsPage.SETTING_COL_IS_NULLABLE, String.valueOf(true));
				
				initialProps.put(CWImportOptionsPage.SETTING_ADD_TECH_FIELDS, String.valueOf(true));
				initialProps.put(CWImportOptionsPage.SETTING_TFLD_IS_NULLABLE, String.valueOf(false));
				
				initialProps.put(CWImportOptionsPage.SETTING_CHK_TBL_OPT_IDX, String.valueOf(ChkTblOptions.TranscodingTables.ordinal()));				
				initialProps.put(PropertiesConstants.KEY_DOCUMENTATION, Messages.NewRMConfPage_7);
			} else if (preloadButton.getSelection()) {
				initialProps.put(CWImportOptionsPage.SETTING_RELATION_IDX, String.valueOf(RelationTypes.PKsFKsEnforced.ordinal()));

				initialProps.put(CWImportOptionsPage.SETTING_COL_TYPE_IDX, String.valueOf(DataTypes.VarCharType.ordinal()));
				initialProps.put(CWImportOptionsPage.SETTING_COL_LEN_FACTOR, String.valueOf(1));
				initialProps.put(CWImportOptionsPage.SETTING_COL_DEFAULT_VAL, "''"); //$NON-NLS-1$
				initialProps.put(CWImportOptionsPage.SETTING_COL_IS_NULLABLE, String.valueOf(false));
				
				initialProps.put(CWImportOptionsPage.SETTING_ADD_TECH_FIELDS, String.valueOf(true));
				initialProps.put(CWImportOptionsPage.SETTING_TFLD_IS_NULLABLE, String.valueOf(false));
				
				initialProps.put(CWImportOptionsPage.SETTING_CHK_TBL_OPT_IDX, String.valueOf(ChkTblOptions.JoinedAndCheckTables.ordinal()));
				initialProps.put(PropertiesConstants.KEY_DOCUMENTATION, Messages.NewRMConfPage_8);

			}
			int db = TargetDBPage.getSupportedDBIndex(TargetDBPage.DB_ARR_CW, SupportedDB.DB2forCW);
			initialProps.put(TargetDBPage.PROP_TARGETDB_DBNAME, String.valueOf(db) );
			initialProps.put(TargetDBPage.PROP_TARGETDB_MAXLENGTH, String.valueOf(Constants.DB_IDENTIFIER_DB2_FOR_CW_LENGTH));
		} 
		return initialProps;
	}

	@Override
	public String createID() {
		return createLocalRMConfigID(super.createID());
	}
	
	public static String createLocalRMConfigID(String id) {
		return "localrmcfg:" + id; //$NON-NLS-1$
	}

}
