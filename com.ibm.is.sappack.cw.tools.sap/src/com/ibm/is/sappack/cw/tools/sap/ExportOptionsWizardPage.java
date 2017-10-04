package com.ibm.is.sappack.cw.tools.sap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.ibm.iis.sappack.gen.common.ui.wizards.PersistentWizardPageBase;
import com.ibm.is.sappack.gen.common.ui.Messages;

public class ExportOptionsWizardPage extends PersistentWizardPageBase {
	static String copyright()
	{ return com.ibm.is.sappack.cw.tools.sap.Copyright.IBM_COPYRIGHT_SHORT; }

//	Button overwriteButton;
//	Button mergeButton;

	Combo legacyIDCombo;
	Combo rloutCombo;
	Combo boCombo;
	List<Entry<String, String>> legID2legName;
	Map<String, Map<String, List<String>>> legacyID2Rlout2BOMap;
	
	public ExportOptionsWizardPage(String pageName) {
		super(pageName, Messages.ExportOptionsWizardPage_0, null);
		this.setDescription(Messages.ExportOptionsWizardPage_1);
	}

	@Override
	protected Composite createControlImpl(Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayout(new GridLayout(2, false));
		Label l = new Label(comp, SWT.NONE);
		l.setText(Messages.ExportOptionsWizardPage_2);
		this.legacyIDCombo = new Combo(comp, SWT.READ_ONLY);
		this.legacyIDCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.legacyIDCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Collection<String> rlouts = legacyID2Rlout2BOMap.get(getSelectedLegacyID()).keySet();
				if (rlouts != null) {
					rloutCombo.setItems(rlouts.toArray(new String[0]));
				}
				setPageComplete(false);
			}
		});
		
		l = new Label(comp, SWT.NONE);
		l.setText(Messages.ExportOptionsWizardPage_3);
		
		this.rloutCombo = new Combo(comp, SWT.READ_ONLY);
		this.rloutCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.rloutCombo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				List<String> bos = legacyID2Rlout2BOMap.get(getSelectedLegacyID()).get(getSelectedRlout());
				if (bos != null) {
					boCombo.setItems(bos.toArray(new String[0]));
				}
				setPageComplete(false);
			}

			
		});
		
		l = new Label(comp, SWT.NONE);
		l.setText(Messages.ExportOptionsWizardPage_4);
		
		this.boCombo = new Combo(comp, SWT.READ_ONLY);
		this.boCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.boCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setPageComplete(true);
			}
		});

		setPageComplete(false);
		return comp;
	}

	public String getSelectedLegacySystemName() {
		int ix = this.legacyIDCombo.getSelectionIndex();
		if (ix <0) {
			return null;
		}
		String legacyName = this.legacyIDCombo.getItems()[ix];
		return legacyName;
	}
	
	public String getSelectedLegacyID() {
		int ix = this.legacyIDCombo.getSelectionIndex();
		if (ix <0) {
			return null;
		}
		String legacyName = this.legacyIDCombo.getItems()[ix];
		String legacyID = findKeyForValue(this.legID2legName, legacyName);
		return legacyID;
	}

	public String getSelectedRlout() {
		int ix = this.rloutCombo.getSelectionIndex();
		if (ix < 0) {
			return null;
		}
		String rlout = this.rloutCombo.getItems()[ix];
		return rlout;
	}
	
	public String getBusinessObjectName() {
		int ix = this.boCombo.getSelectionIndex();
		if (ix < 0) {
			return null;
		}
		String bo = this.boCombo.getItems()[ix];
		return bo;
	}
	
	@Override
	public boolean nextPressedImpl() {
		return true;
	}

	static String[] getArrayOfKeys( List<Entry<String, String>> listOfPairs ) {
		List<String> l = new ArrayList<String>();
		for (Entry<String, String> p : listOfPairs) {
			l.add(p.getKey());
		}
		return l.toArray(new String[0]);
	}

	static String[] getArrayOfValues( List<Entry<String, String>> listOfPairs ) {
		List<String> l = new ArrayList<String>();
		for (Entry<String, String> p : listOfPairs) {
			l.add(p.getValue());
		}
		return l.toArray(new String[0]);
	}
	
	static String findKeyForValue( List<Entry<String, String>> listOfPairs, String value ) {
		for (Entry<String, String> p : listOfPairs) {
			if (value.equals(p.getValue())) {
				return p.getKey();
			}
		}
		return null;
	}
	
	public void setLegacyID2Rlout2BOMap(Map<String, Map<String, List<String>>> legacyID2rlout2BOMap, List<Entry<String, String>> legID2legName) {
		this.legacyID2Rlout2BOMap = legacyID2rlout2BOMap;
		this.legID2legName = legID2legName;
		String[] legacyIDs = getArrayOfValues(legID2legName);  // this.legacyID2Rlout2BOMap.keySet().toArray(new String[0]);
		this.legacyIDCombo.setItems(legacyIDs);
		setPageComplete(false);
	}

}
