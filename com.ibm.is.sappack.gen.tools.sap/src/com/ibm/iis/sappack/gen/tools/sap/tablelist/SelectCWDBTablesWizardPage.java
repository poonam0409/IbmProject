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
// Module Name : com.ibm.iis.sappack.gen.tools.sap.tablelist
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.iis.sappack.gen.tools.sap.tablelist;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.ibm.iis.sappack.gen.common.ui.wizards.INextActionWizardPage;
import com.ibm.is.sappack.gen.common.ui.Messages;


public class SelectCWDBTablesWizardPage extends WizardPage implements INextActionWizardPage {

	private Map<String, Map<String, Map<String, List<String>>>> legacyName2Rlout2BO2TablesMap;
	
	private Combo legacySystemCombo;
	private Combo rloutCombo;

	private org.eclipse.swt.widgets.List boList;
	private Text tableList;

	
  	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	


	public SelectCWDBTablesWizardPage() {
		super("selectcwdbtableswizardpage", Messages.SelectCWDBTablesWizardPage_0, null); //$NON-NLS-1$
		this.setDescription(Messages.SelectCWDBTablesWizardPage_1);
	}

	private String getSelectedLegacyID() {
		return this.legacySystemCombo.getItems()[this.legacySystemCombo.getSelectionIndex()];
	}
	
	@Override
	public void createControl(Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayout(new GridLayout(2, true));
		GridData gd = new GridData();
		gd.horizontalAlignment = GridData.HORIZONTAL_ALIGN_FILL;
		gd.verticalAlignment = GridData.VERTICAL_ALIGN_FILL;
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
	//	comp.setLayoutData(gd);

		
		Composite selectionComp = new Composite(comp, SWT.NONE);
		selectionComp.setLayout(new GridLayout(2, false));
		GridData gdLeg = new GridData(GridData.FILL_HORIZONTAL);
		selectionComp.setLayoutData(gdLeg);

		Label legacySystemLabel = new Label(selectionComp, SWT.NONE);
		legacySystemLabel.setText(Messages.SelectCWDBTablesWizardPage_5);
		
		this.legacySystemCombo = new Combo(selectionComp, SWT.READ_ONLY);
		GridData gdLegCombo = new GridData();
		gdLegCombo.grabExcessHorizontalSpace = true;
		gdLegCombo.horizontalAlignment = GridData.HORIZONTAL_ALIGN_FILL;
		this.legacySystemCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.legacySystemCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				Map<String, Map<String, List<String>>> rlout2BO2TablesMap = legacyName2Rlout2BO2TablesMap.get(getSelectedLegacyID());
				String[] rlouts = rlout2BO2TablesMap.keySet().toArray(new String[0]);
				rloutCombo.setItems(rlouts);
				
				boList.setItems(new String[0]);
				tableList.setText(""); //$NON-NLS-1$
				
				setPageComplete(false);
			}
		});

		Label rloutLabel = new Label(selectionComp, SWT.NONE);
		rloutLabel.setText(Messages.SelectCWDBTablesWizardPage_4);

		this.rloutCombo = new Combo(selectionComp, SWT.READ_ONLY);
		this.rloutCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.rloutCombo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Map<String, Map<String, List<String>>> rlout2BO2TablesMap = legacyName2Rlout2BO2TablesMap.get(getSelectedLegacyID());

				if (rlout2BO2TablesMap != null) {
					String rlout = getSelectedRlout();
					Map<String, List<String>> bo2TablesMap = rlout2BO2TablesMap.get(rlout);
					if (bo2TablesMap != null) {
						boList.removeAll();
						for (Map.Entry<String, List<String>> entry : bo2TablesMap.entrySet()) {
							boList.add(entry.getKey());
						}
					}
				}
				setPageComplete(false);
			}
			
		});

		new Label(comp, SWT.NONE);
		
		Label l = new Label(comp, SWT.NONE);
		l.setText(Messages.SelectCWDBTablesWizardPage_2);
		l = new Label(comp, SWT.NONE);
		l.setText(Messages.SelectCWDBTablesWizardPage_3);
		boList = new org.eclipse.swt.widgets.List(comp, SWT.MULTI | SWT.V_SCROLL | SWT.BORDER);
		boList.setLayoutData(new GridData(GridData.FILL_BOTH));
		boList.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				List<String> selectedTables = getSelectedTables();
				if (selectedTables != null) {
					tableList.setText(getTableListAsString(selectedTables, "\n")); //$NON-NLS-1$
				}
				setPageComplete(true);
			}
		});

		tableList = new Text(comp, SWT.BORDER | SWT.READ_ONLY | SWT.MULTI | SWT.BORDER);
		tableList.setLayoutData(new GridData(GridData.FILL_BOTH));
		setPageComplete(false);
		this.setControl(comp);

	}
	
	public static String getTableListAsString(List<String> tableList, String delimiter) {
		StringBuffer buf = new StringBuffer();
		if (tableList != null) {
			boolean first = true;
			for (String t : tableList) {
				if (!first) {
					buf.append(delimiter);
				}
				first = false;
				buf.append(t);
			}
		}
		return buf.toString();		
	}

	public String getSelectedRlout() {
		int ix = this.rloutCombo.getSelectionIndex();
		if (ix < 0) {
			return null;
		}
		String rlout = this.rloutCombo.getItems()[ ix];
		return rlout;
	}
	
	public String getSelectedBusinessObject() {
		return this.boList.getSelection()[0];
	}
	
	
	public List<String> getSelectedTables() {
		
		Map<String, Map<String, List<String>>> rlout2BO2TablesMap = legacyName2Rlout2BO2TablesMap.get(getSelectedLegacyID());		
		List<String> result = null;
		if (rlout2BO2TablesMap != null) {

			String rlout = getSelectedRlout();
			Map<String, List<String>> bo2TablesMap = rlout2BO2TablesMap.get(rlout);
			
			String[] selectedBOs = boList.getSelection();
			if (selectedBOs == null || selectedBOs.length == 0) {
				return null;
			}
			result = new ArrayList<String>();
			for (String t : selectedBOs) {
				List<String> tables = bo2TablesMap.get(t);
				if (tables != null) {
					result.addAll(tables);
				}
			}
		}
		return result;
	}

//	void update1() {
//		boolean complete = false;
//		
////		if (this.rlout2BO2TablesMap != null) {
////			String rlout = getSelectedRlout();
////			Map<String, List<String>> bo2TablesMap = this.rlout2BO2TablesMap.get(rlout);
////			if (bo2TablesMap != null) {
////				this.boList.removeAll();
////				for (Map.Entry<String, List<String>> e : bo2TablesMap.entrySet()) {
////					boList.add(e.getKey());
////				}
//
//				List<String> selectedTables = getSelectedTables();
//				if (selectedTables != null) {
//					complete = true;
//					StringBuffer buf = new StringBuffer();
//					for (String t : selectedTables) {
//						buf.append(t + "\n"); //$NON-NLS-1$
//					}
//					tableList.setText(buf.toString());
//				}
//			}
//			
////		}
//		
//		setPageComplete(complete);
//	}

	/*
	public void setBO2TableListMap(Map<String, List<String>> bo2TablesMap) {
		this.bo2TablesMap = bo2TablesMap;
		if (this.bo2TablesMap != null) {
			boList.removeAll();
			tableList.setText(""); //$NON-NLS-1$
			for (Map.Entry<String, List<String>> e : this.bo2TablesMap.entrySet()) {
				boList.add(e.getKey());
			}
		}
		update();
	}
	*/
	
	/*
	public void setRlout2BO2TablesMap(Map<String, Map<String, List<String>>> rlout2BO2TablesMap) {
		this.rlout2BO2TablesMap = rlout2BO2TablesMap;
		if (this.rlout2BO2TablesMap != null) {
			String[] rlouts = rlout2BO2TablesMap.keySet().toArray(new String[0]);
			this.rloutCombo.setItems(rlouts);
		}
		setPageComplete(false);
//		update();
	}
	*/
	
	public void setLegacyID2Rlout2BO2TablesMap(Map<String, Map<String, Map<String, List<String>>>> legacyId2Rlout2BO2TablesMap) {
		this.legacyName2Rlout2BO2TablesMap = legacyId2Rlout2BO2TablesMap;
		if (this.legacyName2Rlout2BO2TablesMap != null) {
			String[] legacyNames = this.legacyName2Rlout2BO2TablesMap.keySet().toArray(new String[0]);
			this.legacySystemCombo.setItems(legacyNames);
			this.rloutCombo.setItems(new String[0]);
			this.boList.setItems(new String[0]);
			this.tableList.setText(""); //$NON-NLS-1$
		}
		setPageComplete(false);
	}

	@Override
	public boolean nextPressed() {
		return true;
	}

}
