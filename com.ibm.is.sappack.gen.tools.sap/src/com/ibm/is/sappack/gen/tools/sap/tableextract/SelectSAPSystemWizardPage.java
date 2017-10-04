//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2009, 2011                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.is.sappack.gen.tools.sap.tableextract
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.tools.sap.tableextract;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import com.ibm.iis.sappack.gen.common.ui.connections.SAPConnectionSelectionWidget;
import com.ibm.iis.sappack.gen.common.ui.connections.SAPConnectionSelectionWidget.SapConnectionSelectedListener;
import com.ibm.iis.sappack.gen.common.ui.connections.SapSystem;
import com.ibm.is.sappack.gen.common.ui.Messages;

public class SelectSAPSystemWizardPage extends WizardBasePage {

	static String copyright() {
		return com.ibm.is.sappack.gen.tools.sap.tableextract.Copyright.IBM_COPYRIGHT_SHORT;
	}

//	SapSystemSelectionWidget sapSelectionWidget;
	SAPConnectionSelectionWidget sapConnSelectionWidget;

	Button insertDirectlyButton;
	Button createSQLScriptButton;
	
	public SelectSAPSystemWizardPage() {
		super("selectSAPSystem", //$NON-NLS-1$
		      Messages.SelectSAPSystemWizardPage_0,
		      Messages.SelectSAPSystemWizardPage_1);
	}

	@Override
   public Composite createPageControls(final Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout gl = new GridLayout();
		gl.numColumns = 1;
		gl.makeColumnsEqualWidth = true;
		container.setLayout(gl);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		
		container.setLayoutData(gd);
	
		this.sapConnSelectionWidget = new SAPConnectionSelectionWidget(container, SWT.NONE);
		this.sapConnSelectionWidget.addSapConnectionSelectedListener(new SapConnectionSelectedListener() {
			
			@Override
			public void sapConnectionSelected(SapSystem selectedSapConnection) {
				update();
			}
		});
//		this.sapConnSelectionWidget.gets
/*		
		this.sapSelectionWidget = new SapSystemSelectionWidget(container);
		this.sapSelectionWidget.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				update();
			}
			
		});
		this.sapSelectionWidget.setSelectionIndex(0);
	*/
		
		Group group = new Group(container, SWT.NONE);
		group.setText(Messages.SelectSAPSystemWizardPage_2);
		GridLayout groupLayout = new GridLayout();
		gl.numColumns = 1;
		gl.makeColumnsEqualWidth = true;
		group.setLayout(groupLayout);
		GridData groupGridData = new GridData(GridData.FILL_HORIZONTAL);
		group.setLayoutData(groupGridData);

		SelectionListener listener = new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				update();
			}
			
		};
		
		this.createSQLScriptButton = new Button(group, SWT.RADIO);
		this.createSQLScriptButton.setText(Messages.SelectSAPSystemWizardPage_3);
		this.createSQLScriptButton.setSelection(true);
		this.createSQLScriptButton.addSelectionListener(listener);

		this.insertDirectlyButton = new Button(group, SWT.RADIO);
		this.insertDirectlyButton.setText(Messages.SelectSAPSystemWizardPage_4);
		this.insertDirectlyButton.setSelection(false);
		this.insertDirectlyButton.addSelectionListener(listener);
		
		update();
		
		return(container);
	}
	
	private void update() {
		boolean complete = true;
		setErrorMessage(null);
		if (this.sapConnSelectionWidget.getSelectedSAPSystem() == null) {
//		if (!sapSelectionWidget.isSapSystemSelected()) {
			setErrorMessage(Messages.SelectSAPSystemWizardPage_5);
			complete = false;
		}
		setPageComplete(complete);
		
	}
	
	public SapSystem getSapSystem() {
//		return this.sapSelectionWidget.getSelectedSapSystem();
		return this.sapConnSelectionWidget.getSelectedSAPSystem();
	}
	
	public boolean createSQLScript() {
		return this.createSQLScriptButton.getSelection();
	}
	
	public boolean insertTablesDirectly() {
		return this.insertDirectlyButton.getSelection();
	}

}
