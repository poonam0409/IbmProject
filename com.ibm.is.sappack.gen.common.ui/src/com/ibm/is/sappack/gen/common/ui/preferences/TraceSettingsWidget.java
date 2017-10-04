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
// Module Name : com.ibm.is.sappack.gen.common.ui.preferences
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.common.ui.preferences;

import java.io.File;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.common.ui.helper.LoggingHelper;

public class TraceSettingsWidget extends Composite {

	private Group traceSettingsGroup = null;
	private Button enableTrace = null;
	private Label traceFileLocLabel = null;
	private Text traceFileLocText = null;
	private ControlDecoration traceFileLocTextDeco = null;
	private Label traceComponentsInfoLabel = null;
	private Label traceComponentsLabel = null;
	private Text traceComponentsText = null;

	private TraceSettingsPreferencePage tracePrefsPage = null;

	static String copyright() {
		return com.ibm.is.sappack.gen.common.ui.preferences.Copyright.IBM_COPYRIGHT_SHORT;
	}

	public TraceSettingsWidget(Composite parent, int style, TraceSettingsPreferencePage page, IPreferenceStore store) {
		super(parent, style);

		tracePrefsPage = page;
		initialize(store);
	}

	private void initialize(IPreferenceStore store) {
		GridLayout overallGridLayout = new GridLayout();
		this.setLayout(overallGridLayout);
		this.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL
		      | GridData.VERTICAL_ALIGN_BEGINNING | GridData.GRAB_VERTICAL));

		enableTrace = new Button(this, SWT.CHECK);
		enableTrace.setText(Messages.TraceSettingsWidget_0);
		enableTrace.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				toggleTraceWidgets(((Button) e.getSource()).getSelection());
			}
		});

		// empty and invisible label for layout beautification
		Label emptyLabel = new Label(this, SWT.NONE);
		emptyLabel.setVisible(false);

		GridLayout groupGridLayout = new GridLayout();
		traceSettingsGroup = new Group(this, SWT.NONE);
		traceSettingsGroup.setText(Messages.TraceSettingsWidget_1);
		traceSettingsGroup.setLayout(groupGridLayout);
		traceSettingsGroup.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.FILL_HORIZONTAL
		      | GridData.VERTICAL_ALIGN_BEGINNING));

		GridLayout basicCompGridLayout = new GridLayout();
		basicCompGridLayout.numColumns = 2;
		Composite traceBasicComp = new Composite(traceSettingsGroup, SWT.NONE);
		traceBasicComp.setLayout(basicCompGridLayout);
		traceBasicComp.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING
		      | GridData.GRAB_HORIZONTAL));

		traceFileLocLabel = new Label(traceBasicComp, SWT.NONE);
		traceFileLocLabel.setText(Messages.TraceSettingsWidget_2);
		traceFileLocText = new Text(traceBasicComp, SWT.BORDER);
		traceFileLocText.setEditable(true);
		traceFileLocText.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
		traceFileLocText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				validateTraceFileLoc(((Text) e.getSource()).getText());
			}
		});
		traceFileLocTextDeco = new ControlDecoration(traceFileLocText, SWT.TOP | SWT.LEFT);
		FieldDecoration fd = FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_ERROR);
		traceFileLocTextDeco.setImage(fd.getImage());
		traceFileLocTextDeco.setDescriptionText(Messages.TraceSettingsWidget_6);

		GridLayout filterCompGridLayout = new GridLayout();
		filterCompGridLayout.numColumns = 1;
		filterCompGridLayout.marginTop = 10;
		Composite traceFilterComp = new Composite(traceSettingsGroup, SWT.NONE);
		traceFilterComp.setLayout(filterCompGridLayout);
		traceFilterComp.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING
		      | GridData.FILL_VERTICAL | GridData.GRAB_HORIZONTAL));

		traceComponentsInfoLabel = new Label(traceFilterComp, SWT.WRAP);
		traceComponentsInfoLabel.setText(Messages.TraceSettingsWidget_4);
		GridData traceComponentsInfoLabelGridData = new GridData(GridData.FILL_HORIZONTAL);
		traceComponentsInfoLabelGridData.widthHint = 300;
		traceComponentsInfoLabel.setLayoutData(traceComponentsInfoLabelGridData);

		// empty and invisible label for layout beautification
		Label emptyLabel1 = new Label(traceFilterComp, SWT.NONE);
		emptyLabel1.setVisible(false);

		traceComponentsLabel = new Label(traceFilterComp, SWT.NONE);
		traceComponentsLabel.setText(Messages.TraceSettingsWidget_5);
		traceComponentsText = new Text(traceFilterComp, SWT.WRAP | SWT.MULTI | SWT.BORDER | SWT.V_SCROLL);
		traceComponentsText.setEditable(true);
		GridData traceComponentsTextGridData = new GridData(GridData.FILL_HORIZONTAL);
		traceComponentsTextGridData.heightHint = 100;
		traceComponentsText.setLayoutData(traceComponentsTextGridData);

		if (store != null) {
			String storedTraceFileLoc = store.getString(PreferenceConstants.P_TRACE_FILE_LOC);
			setTraceFileLocText(storedTraceFileLoc);

			String storedTraceComponents = store.getString(PreferenceConstants.P_TRACE_COMPONENTS);
			setTraceComponentsText(storedTraceComponents);

			boolean storedTraceEnabled = store.getBoolean(PreferenceConstants.P_TRACE_ENABLED);
			setEnableTrace(storedTraceEnabled);
		}

		toggleTraceWidgets(enableTrace.getSelection());
	}

	private void toggleTraceWidgets(boolean toggle) {
		traceSettingsGroup.setEnabled(toggle);
		traceFileLocLabel.setEnabled(toggle);
		traceFileLocText.setEnabled(toggle);
		traceComponentsInfoLabel.setEnabled(toggle);
		traceComponentsLabel.setEnabled(toggle);
		traceComponentsText.setEnabled(toggle);
	}

	private void validateTraceFileLoc(String traceFileLoc) {
		String traceFilePath = null;
		int fileSeparatorIndex = traceFileLoc.lastIndexOf(System.getProperty("file.separator")); //$NON-NLS-1$
		if (fileSeparatorIndex != -1) {
			traceFilePath = traceFileLoc.substring(0, fileSeparatorIndex);
		}
		else {
			traceFilePath = traceFileLoc;
		}

		File dir = new File(traceFilePath);
		if (!dir.isDirectory()) {
			if (traceFileLocTextDeco != null) {
				traceFileLocTextDeco.show();
			}

			tracePrefsPage.setValid(false);
		}
		else {
			if (traceFileLocTextDeco != null) {
				traceFileLocTextDeco.hide();
			}

			tracePrefsPage.setValid(true);
		}
	}

	protected Text getTraceFileLocText() {
		return traceFileLocText;
	}

	protected void setTraceFileLocText(String loc) {
		if (traceFileLocText != null) {
			traceFileLocText.setText(loc);
			traceFileLocText.notifyListeners(SWT.Modify, new Event());
		}
	}

	protected Text getTraceComponentsText() {
		return traceComponentsText;
	}

	protected void setTraceComponentsText(String components) {
		if (traceComponentsText != null) {
			if (components != null) {
				String[] componentsList = components.split(LoggingHelper.TRACE_COMPONENTS_DELIMITER);
				StringBuffer componentsText = new StringBuffer();
				for (int i = 0; i < componentsList.length; i++) {
					if (i == (componentsList.length - 1)) {
						componentsText.append(componentsList[i]);
					}
					else {
						componentsText.append(componentsList[i] + Text.DELIMITER);
					}
				}

				traceComponentsText.setText(componentsText.toString());
			}
			else {
				traceComponentsText.setText(""); //$NON-NLS-1$
			}
		}
	}

	protected Button getEnableTrace() {
		return enableTrace;
	}

	protected void setEnableTrace(boolean enabled) {
		if (enableTrace != null) {
			enableTrace.setSelection(enabled);
			enableTrace.notifyListeners(SWT.Selection, new Event());
		}
	}
}
