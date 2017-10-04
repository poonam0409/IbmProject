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


import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.ibm.iis.sappack.gen.common.ui.editors.EditorPageBase;
import com.ibm.iis.sappack.gen.common.ui.editors.IControlFactory;
import com.ibm.iis.sappack.gen.common.ui.util.Utils;
import com.ibm.iis.sappack.gen.tools.sap.rmconf.RMConfiguration.SupportedDB;
import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.common.ui.ModeManager;
import com.ibm.is.sappack.gen.tools.sap.constants.Constants;


public class TargetDBPage extends EditorPageBase {
  	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	

	public static final String TABNAME = Messages.TargetDBPage_0;

	public static final String PROP_TARGETDB_DBNAME = "PROP_TARGETDB_DBNAME"; //$NON-NLS-1$
	public static final String PROP_TARGETDB_MAXLENGTH = "PROP_TARGETDB_MAXLENGTH"; //$NON-NLS-1$

	
	public static SupportedDB getSupportedDB(String str) {
		SupportedDB selectedDB = null;
		for (SupportedDB db : SupportedDB.values()) {
			if (db.toString().equals(str)) {
				selectedDB = db;
				break;
			}
		}
		return selectedDB;
	}

	static SupportedDB[] DB_ARR = new SupportedDB[] { SupportedDB.DB2, SupportedDB.Oracle, SupportedDB.Custom };

	public static SupportedDB[] DB_ARR_CW = new SupportedDB[] { SupportedDB.DB2, SupportedDB.DB2forCW, SupportedDB.Oracle, SupportedDB.Custom };

	public static int getSupportedDBIndex(SupportedDB[] dbs, SupportedDB db) {
		for (int i=0; i<dbs.length; i++) {
			if (dbs[i] == db) {
				return i;
			}
		}
		return -1;
	}
	
	private Combo dbCombo;

	public TargetDBPage() {
		super(TABNAME, Messages.TargetDBPage_1, Messages.TargetDBPage_2, Utils.getHelpID("rmconfeditor_dbtype_sappack")); //$NON-NLS-1$
	}

	private String[] createDBItems() {
		SupportedDB[] ar = DB_ARR;
		if (ModeManager.isCWEnabled()) {
			ar = DB_ARR_CW;
		}
		String[] result = new String[ar.length];
		for (int i = 0; i < result.length; i++) {
			String dbItemStr = Constants.EMPTY_STRING;
			
			switch(ar[i]) {
			   case Custom:
			   	  dbItemStr = Messages.TargetDBPage_It_CUST;
			   	  break;
			   case DB2:
			   	  dbItemStr = Messages.TargetDBPage_It_DB2;
			   	  break;
			   case DB2forCW:
			   	  dbItemStr = Messages.TargetDBPage_It_DB2_CW;
			   	  break;
			   case Oracle:
			   	  dbItemStr = Messages.TargetDBPage_It_ORA;
			   	  break;
			}
			result[i] = dbItemStr;
		}
		return result;
	}

	@Override
	public void createControls(IControlFactory controlFactory, Composite parent) {

		Composite comp = controlFactory.createComposite(parent, SWT.NONE);
		comp.setLayout(new GridLayout(2, false));
		comp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		GridData textGD = new GridData(SWT.FILL, SWT.FILL, true, false);

		Label l = controlFactory.createLabel(comp, SWT.NONE);
		l.setText(Messages.TargetDBPage_3);

		dbCombo = controlFactory.createCombo(comp, SWT.READ_ONLY);
		dbCombo.setItems(createDBItems());
		this.configureComboForProperty(dbCombo, PROP_TARGETDB_DBNAME);
		dbCombo.setLayoutData(textGD);

		final Text maxLengthtext = this.createLabelAndTextField(controlFactory, comp, PROP_TARGETDB_MAXLENGTH, SWT.BORDER, textGD);
		maxLengthtext.setEnabled(getSelectedDB() == SupportedDB.Custom);
		
		SelectionAdapter comboSelectionListener = new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				SupportedDB selectedDB = getSelectedDB();
				int length;
				switch (selectedDB) {
				case DB2:
					length = Constants.DB_IDENTIFIER_MAX_LENGTH;
					break;
				case Oracle:
					length = Constants.DB_IDENTIFIER_ORACLE_LENGTH;
					break;
				case DB2forCW:
					length = Constants.DB_IDENTIFIER_DB2_FOR_CW_LENGTH;
					break;
				default:
					length = -1;
				}

				boolean lengthFieldEnabled = selectedDB == SupportedDB.Custom;
				maxLengthtext.setEnabled(lengthFieldEnabled);

				if (length > -1) {
					maxLengthtext.setText(String.valueOf(length));
				}
			}

		};
		dbCombo.addSelectionListener(comboSelectionListener);
		//	comboSelectionListener.widgetSelected(null);
	}

	private SupportedDB getSelectedDB() {
		int ix = dbCombo.getSelectionIndex();
		String[] items = dbCombo.getItems();
		String selected = items[ix];
		SupportedDB selectedDB = getSupportedDB(selected);
		return selectedDB;

	}
}
