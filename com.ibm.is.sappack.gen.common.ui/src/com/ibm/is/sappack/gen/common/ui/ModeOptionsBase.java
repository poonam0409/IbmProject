//------------------------------------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2011, 2014                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//------------------------------------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.is.sappack.gen.common.ui
//                                               
////////////////////////////////////////////////////////////////////////////////////////////////////////
//
//      Maintenence log - insert most recent change descriptions at top
//      Date      	 APAR Number/Defect WI     WHO  	Description.....................................
//		03-11-2016	 APAR-259595/WI 259553	  SumitK	ArrayOutofBounds exception due to uninitialized 
//														variable. Changed made to initialize the 
//														variables.
//**************************************-END OF SPECIFICATIONS-*****************************************
package com.ibm.is.sappack.gen.common.ui;


import java.util.logging.Level;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;


public abstract class ModeOptionsBase implements ModeOptions {

	public  enum  DSServiceVersion { v70, v71, v80};
	
	private static final int              PREF_STORE_INIT_CORRECTION   = 1; 
	public  static final  String           CURRENT_DS_SERVICE_VERSION   = "DS_SERVICE_VERSION"; //$NON-NLS-1$
	public  static final DSServiceVersion CW_DEFAULT_SERVICE_VERSION   = DSServiceVersion.v80;
	public  static final DSServiceVersion PACK_DEFAULT_SERVICE_VERSION = DSServiceVersion.v80;
	
	
	//WI 259553 : Initialized variable dsServiceVer with the Default version value
	private DSServiceVersion dsServiceVer = DSServiceVersion.v80;
	

	static String copyright() {
		return Copyright.IBM_COPYRIGHT_SHORT;
	}

	protected void setDSServiceVersion(DSServiceVersion newDSServiceVer) {
		this.dsServiceVer = newDSServiceVer;
	}

	@Override
	public void saveToPrefStore(IPreferenceStore prefStore) {
		if (this.dsServiceVer != null) {
			prefStore.setValue(CURRENT_DS_SERVICE_VERSION, this.dsServiceVer.ordinal() + PREF_STORE_INIT_CORRECTION); // + PREF_STORE_INIT_CORRECTION because of the 'not found behaviour' of setValue()
		}
	}

	@Override
	public void loadFromPrefStore(IPreferenceStore prefStore) {
		this.dsServiceVer = getVersionFromPrefStore(prefStore);
	}

	protected void createNoModeOptionsLabel(Composite parantComp) {
		Label l = new Label(parantComp, SWT.NONE);
		l.setText(Messages.EmptyModeOptions_0);
		parantComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	protected void createRFCServiceSelectionCombo(Composite parentComp) {
		parentComp.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL | GridData.FILL_BOTH));

		Label dsServiceVerComboLabel = new Label(parentComp, SWT.NONE);
		dsServiceVerComboLabel.setText(Messages.OptionPage_RFCVersion_Label);
		
		final Combo dsServiceVerCombo = new Combo(parentComp, SWT.SINGLE | SWT.READ_ONLY | SWT.BORDER);
		dsServiceVerCombo.setItems(new String[] { Messages.OptionPage_RFCVersion_V70,
                                                Messages.OptionPage_RFCVersion_V71, Messages.OptionPage_RFCVersion_V80 } );
		dsServiceVerCombo.select(getDSServiceVersion().ordinal());
		dsServiceVerCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int newIdx = dsServiceVerCombo.getSelectionIndex();
				setDSServiceVersion(DSServiceVersion.values()[newIdx]);
				Activator.getLogger().log(Level.INFO, "RFC Service version to be used was set to '" + DSServiceVersion.values()[newIdx] + "'.");
			}
		});
	}
	
	protected static DSServiceVersion getDefaultVersion() {
		DSServiceVersion retDefaultVersion;
		RMRGMode curMode = ModeManager.getActiveMode();
		if (curMode != null && curMode.getID().equals(ModeManager.CW_MODE_ID)) {
			// CW mode ==> use CW Default Service Version
			retDefaultVersion = CW_DEFAULT_SERVICE_VERSION;
		}
		else {
			// No mode or other mode selected ==> ==> use PACKS Default Service Version
			retDefaultVersion = PACK_DEFAULT_SERVICE_VERSION;
		} // end of (else) if (curMode != null && ... r.CW_MODE_ID))

		return(retDefaultVersion);
	}

	private static DSServiceVersion getVersionFromPrefStore(IPreferenceStore prefStore) {
		//WI 259553 : Initialized variable retDSSvcVer with the Default version value
		DSServiceVersion retDSSvcVer = DSServiceVersion.v80;

		if (prefStore.contains(CURRENT_DS_SERVICE_VERSION)) {
			int tmpSvcVer = prefStore.getInt(CURRENT_DS_SERVICE_VERSION) - PREF_STORE_INIT_CORRECTION;
			retDSSvcVer   = DSServiceVersion.values()[tmpSvcVer];
		}
		else {
			retDSSvcVer = getDefaultVersion();
		}
		return(retDSSvcVer);
	}

	public static DSServiceVersion getDSServiceVersion() {
		IPreferenceStore prefStore = Activator.getDefault().getPreferenceStore();
		return(getVersionFromPrefStore(prefStore));
	}
	
} // end of class ModeOptionsBase  
