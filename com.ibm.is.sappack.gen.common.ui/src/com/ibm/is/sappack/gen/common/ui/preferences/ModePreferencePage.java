//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2013                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.is.sappack.gen.common.ui.preferences
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.common.ui.preferences;


import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.ibm.is.sappack.gen.common.ui.Activator;
import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.common.ui.ModeManager;
import com.ibm.is.sappack.gen.common.ui.RMRGMode;


public class ModePreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	private Map<String, Composite> modeID2OptionsComposite;
	private StackLayout            stackLayout;
	private Combo                  combo;
	private Composite              stackComposite;
	private Label                  labelDescription;


   static String copyright() {
      return com.ibm.is.sappack.gen.common.ui.preferences.Copyright.IBM_COPYRIGHT_SHORT;
   }

	public ModePreferencePage() {
		this.modeID2OptionsComposite = new HashMap<String, Composite>();
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayout(new GridLayout());
		comp.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL | GridData.FILL_BOTH));

		RMRGMode[] installedModes = ModeManager.getInstalledModes();
		boolean isMultiMode = (installedModes.length > 1);
		combo = new Combo(comp, SWT.READ_ONLY);
		for (RMRGMode ext : installedModes) {
			combo.add(ext.getName());
		}

		combo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				refreshSelection();
			}
		});

		Label l = new Label(comp, SWT.NONE);
		l.setText(Messages.ModePreferencePage_0);
      labelDescription = new Label(comp, SWT.WRAP);
      labelDescription.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Group group = new Group(comp, SWT.NULL);
		group.setText(Messages.ModePreferencePage_1);
		group.setLayout(new GridLayout());
		group.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL | GridData.FILL_BOTH));

		stackComposite = new Composite(group, SWT.NONE);
		stackComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		stackLayout = new StackLayout();
		stackComposite.setLayout(stackLayout);

		for (RMRGMode ext : installedModes) {
			Composite extOptionsComp = ext.getModeOptions().createControls(stackComposite);
			this.modeID2OptionsComposite.put(ext.getID(), extOptionsComp);
		}
		this.combo.select(0);
		this.combo.setEnabled(isMultiMode);

		restorePreferences();
		return comp;
	}
	
	private void refreshSelection() {
		RMRGMode mode = getSelectedMode();
		String id = mode.getID();
		
		labelDescription.setText(mode.getDescription());
      labelDescription.getParent().layout();

		mode.getModeOptions().loadFromPrefStore(getPreferenceStore());
		Composite comp = modeID2OptionsComposite.get(id);
		stackLayout.topControl = comp;
		stackComposite.layout();
	}
	
	private RMRGMode getSelectedMode() {
		int ix = this.combo.getSelectionIndex();
		RMRGMode[] installedModes = ModeManager.getInstalledModes();
		return installedModes[ix];		
	}
	
	private String getSelectedID() {
		return getSelectedMode().getID();
	}

	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
	}

	@Override
	protected void performApply() {
		storePreferences();
		super.performApply();
	}

	@Override
	public boolean performOk() {
		storePreferences();
		return super.performOk();
	}

	public static final String CURRENT_MODE_ID = "RMRG_MODE_NAME"; //$NON-NLS-1$
	
	private void storePreferences() {
		IPreferenceStore prefStore = getPreferenceStore();
		prefStore.setValue(CURRENT_MODE_ID, getSelectedID());
		/*
		Map<String, String> values = getSelectedMode().getModeOptions().getValues();
		for (Map.Entry<String, String> e : values.entrySet()) {
			prefStore.setValue(e.getKey(), e.getValue());
		}
		*/
		getSelectedMode().getModeOptions().saveToPrefStore(prefStore);
	}

	private void restorePreferences() {
		IPreferenceStore prefStore = getPreferenceStore();
		String modeID = prefStore.getString(CURRENT_MODE_ID);
		// if nothing was stored in preferences
		if (modeID == null || modeID.isEmpty()) {
			// if modelling is installed, take it, else use CW
			
			if (ModeManager.getMode(ModeManager.MODELLING_MODE_ID) != null) {
				modeID = ModeManager.MODELLING_MODE_ID;
			} else if (ModeManager.getMode(ModeManager.CW_MODE_ID) != null) {
				modeID = ModeManager.CW_MODE_ID;
			} else {
				modeID = ModeManager.DEFAULT_MODE_ID;
			}
		}

		RMRGMode[] installedModes = ModeManager.getInstalledModes();
		for (int i=0; i<installedModes.length; i++) {
			RMRGMode mode = installedModes[i];
			if (mode.getID().equals(modeID)) {
			//	mode.getModeOptions().loadFromPrefStore(prefStore);
				this.combo.select(i);
				refreshSelection();
				break;
			}
		}

	}

}
