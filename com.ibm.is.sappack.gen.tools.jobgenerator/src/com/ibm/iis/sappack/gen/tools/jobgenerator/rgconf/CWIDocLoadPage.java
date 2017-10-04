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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.ibm.iis.sappack.gen.common.ui.editors.IControlFactory;
import com.ibm.iis.sappack.gen.common.ui.util.Utils;
import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.tools.sap.utilities.Utilities;


public class CWIDocLoadPage extends RGConfPageBase {
	public static final String PROP_IDOC_LOAD_STATUS_CHECKED = "IDOC_LOAD_STATUS_CHECKED"; //$NON-NLS-1$
	public static final String PROP_IDOC_LOAD_SURROGATE_KEY_FILE = "IDOC_LOAD_SURROGATE_KEY_FILE"; //$NON-NLS-1$
	public static final String PROP_IDOC_LOAD_OBJECT_NAME = "IDOC_LOAD_OBJECT_NAME"; //$NON-NLS-1$

	public static final String TABNAME = Messages.CWIDocLoadPage_0;
	
	private Button idocLoadStatusOption;
	private Text idocLoadStatusObjectNameText;
	private Text idocLoadStatusSurrogateKeyFileText;


  	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	


	public CWIDocLoadPage() {
		super(TABNAME, Messages.CWIDocLoadPage_1, Messages.CWIDocLoadPage_2, Utils.getHelpID("rg_conf_idocload")); //$NON-NLS-1$
	}

	@Override
	public void createControls(IControlFactory controlFactory, Composite parent) {

		Composite idocLoadStatusComposite = controlFactory.createGroup(parent, Messages.CWJobGeneratorIdocLoadPage_12, SWT.NONE);
		GridLayout idocLoadStatusCompositeLayout = new GridLayout();
		idocLoadStatusCompositeLayout.numColumns = 2;
		idocLoadStatusComposite.setLayout(idocLoadStatusCompositeLayout);
		idocLoadStatusComposite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));

	//	Label idocLoadStatusLabel = controlFactory.createLabel(idocLoadStatusComposite, SWT.NONE);
	//	idocLoadStatusLabel.setText(Messages.CWJobGeneratorIdocLoadPage_13);
		idocLoadStatusOption = new Button(idocLoadStatusComposite, SWT.CHECK);
		idocLoadStatusOption.setText(Messages.CWIDocLoadPage_3);
		controlFactory.createLabel(idocLoadStatusComposite, SWT.NONE);
		this.configureCheckboxForProperty(idocLoadStatusOption, PROP_IDOC_LOAD_STATUS_CHECKED);
		SelectionAdapter loadStatusSelection = new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean enabled = idocLoadStatusOption.getSelection();
				idocLoadStatusSurrogateKeyFileText.setEnabled(enabled);
				idocLoadStatusObjectNameText.setEnabled(enabled);
			}

		};
		this.idocLoadStatusOption.addSelectionListener(loadStatusSelection);

		Label surrogateKeySourceFileLabel = controlFactory.createLabel(idocLoadStatusComposite, SWT.NONE);
		surrogateKeySourceFileLabel.setText(Messages.CWJobGeneratorIdocLoadPage_15);
		idocLoadStatusSurrogateKeyFileText = controlFactory.createText(idocLoadStatusComposite, SWT.BORDER);
		idocLoadStatusSurrogateKeyFileText.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
		this.configureTextForJobParameterProperty(idocLoadStatusSurrogateKeyFileText, PROP_IDOC_LOAD_SURROGATE_KEY_FILE);

		Label idocLoadStatusObjectNameLabel = controlFactory.createLabel(idocLoadStatusComposite, SWT.NONE);
		idocLoadStatusObjectNameLabel.setText(Messages.CWJobGeneratorIdocLoadPage_16);
		idocLoadStatusObjectNameText = controlFactory.createText(idocLoadStatusComposite, SWT.BORDER);
		idocLoadStatusObjectNameText.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
		this.configureTextForJobParameterProperty(idocLoadStatusObjectNameText, PROP_IDOC_LOAD_OBJECT_NAME);

		loadStatusSelection.widgetSelected(null);
	}

	public boolean getIDocLoadStatus() {
		return this.idocLoadStatusOption.getSelection();
	}

	public String getIDocLoadStatusSurrogateKeyFile() {
		return Utilities.getTextFieldValue(idocLoadStatusSurrogateKeyFileText);
	}

	public String getIDocLoadStatusObjectName() {
		return Utilities.getTextFieldValue(idocLoadStatusObjectNameText);
	}
	
}
