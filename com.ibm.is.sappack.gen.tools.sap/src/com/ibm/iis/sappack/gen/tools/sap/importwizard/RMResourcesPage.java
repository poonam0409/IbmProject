//---------------------------------------------------------------------------  
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
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.iis.sappack.gen.tools.sap.importwizard
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.iis.sappack.gen.tools.sap.importwizard;


import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.validation.ValidationResult;
import org.eclipse.wst.validation.ValidatorMessage;

import com.ibm.iis.sappack.gen.common.ui.connections.PasswordDialog;
import com.ibm.iis.sappack.gen.common.ui.connections.SAPConnectionSelectionWidget;
import com.ibm.iis.sappack.gen.common.ui.connections.SAPConnectionSelectionWidget.SapConnectionSelectedListener;
import com.ibm.iis.sappack.gen.common.ui.connections.SapSystem;
import com.ibm.iis.sappack.gen.common.ui.editors.ConfigurationBase;
import com.ibm.iis.sappack.gen.common.ui.util.Utils;
import com.ibm.iis.sappack.gen.common.ui.validators.ValidatorBase;
import com.ibm.iis.sappack.gen.common.ui.wizards.PersistentWizardPageBase;
import com.ibm.iis.sappack.gen.common.ui.wizards.ResourceSelectionWidget;
import com.ibm.iis.sappack.gen.tools.sap.idocseglist.IDocSegmentList;
import com.ibm.iis.sappack.gen.tools.sap.rmconf.RMConfiguration;
import com.ibm.iis.sappack.gen.tools.sap.tablelist.TableList;
import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.common.ui.preferences.AdvancedSettingsPreferencePage;
import com.ibm.is.sappack.gen.tools.sap.activator.Activator;


public class RMResourcesPage extends PersistentWizardPageBase {

	private static final String PFX = "RMResourcePage"; //$NON-NLS-1$

	private List<ResourceSelectionWidget> resourceWidgets;

	private   SAPConnectionSelectionWidget sapWidget;
	private   String ldmFileName;
	private   ResourceSelectionWidget socWidget;
	protected ResourceSelectionWidget rmCfgWidget;
	private   ResourceSelectionWidget ldmWidget;
	private   Button useSeparateCheckTableModel;
	private   ResourceSelectionWidget checktableLDMWidget;
	
	private boolean rememberResources = false;


	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}

	protected RMResourcesPage(String ldmFileName) {
		super("rmresourcespage", Messages.RMResourcesPage_0, null); //$NON-NLS-1$

		setDescription(Messages.RMResourcesPage_1);
		this.resourceWidgets = new ArrayList<ResourceSelectionWidget>();
		this.ldmFileName = ldmFileName;
		this.rememberResources = AdvancedSettingsPreferencePage.isSettingEnabled("REMEMBER_RM_SETTINGS"); //$NON-NLS-1$
	}

	@Override
	protected Composite createControlImpl(Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
			comp.setLayout(new GridLayout(1, false));
		//		comp.setLayout(new RowLayout());
		//	comp.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false));
			ModifyListener ml = new ModifyListener() {

				@Override
				public void modifyText(ModifyEvent e) {
					dialogChanged();
				}
			};

		sapWidget = new SAPConnectionSelectionWidget(comp, SWT.NONE);
		sapWidget.addSapConnectionSelectedListener(new SapConnectionSelectedListener() {

			@Override
			public void sapConnectionSelected(SapSystem selectedSapConnection) {
				dialogChanged();
			}
		});
		sapWidget.getSAPConnectionNameText().addModifyListener(ml);
		this.configureTextForProperty(sapWidget.getSAPConnectionNameText(), PFX + ".sapconnection"); //$NON-NLS-1$


		Composite comp1 = new Composite(comp, SWT.NONE);
		GridLayout gl = new GridLayout(3, false);
		comp1.setLayout(gl);
		GridData gd2 = new GridData(SWT.FILL, SWT.FILL, true, true);
		comp1.setLayoutData(gd2);

		GridData textGD = new GridData(GridData.FILL_HORIZONTAL);

		socWidget = new ResourceSelectionWidget(comp1, SWT.NONE, null, Messages.RMResourcesPage_2, new String[] { TableList.TABLE_LIST_FILE_EXTENSION, IDocSegmentList.IDOC_SEGMENT_LIST_FILE_EXTENSION }, true, null);
		socWidget.addModifyListener(ml);
		socWidget.getText().setLayoutData(textGD);
		if (rememberResources) {
			this.configureTextForProperty(socWidget.getText(), PFX + ".socvalue"); //$NON-NLS-1$
		}
		this.resourceWidgets.add(socWidget);

		this.rmCfgWidget = new ResourceSelectionWidget(comp1, SWT.NONE, null, Messages.RMResourcesPage_3, RMConfiguration.RMCONF_FILE_EXTENSION, true, null);
		this.rmCfgWidget.addModifyListener(ml);
		if (rememberResources) {
			this.configureTextForProperty(rmCfgWidget.getText(), PFX + ".rmcfgvalue"); //$NON-NLS-1$
		}
		this.resourceWidgets.add(rmCfgWidget);

		this.ldmWidget = new ResourceSelectionWidget(comp1, NONE, null, Messages.RMResourcesPage_4, ".ldm", true, null); //$NON-NLS-1$
		this.ldmWidget.addModifyListener(ml);
		if (this.ldmFileName == null) {
			if (rememberResources) {
				this.configureTextForProperty(this.ldmWidget.getText(), PFX + ".ldmvalue"); //$NON-NLS-1$
			}
		} else {
			this.ldmWidget.setText(ldmFileName);
			this.ldmWidget.setEnabled(false);
		}
		this.resourceWidgets.add(ldmWidget);

		this.useSeparateCheckTableModel = new Button(comp1, SWT.CHECK);
		this.useSeparateCheckTableModel.setText(Messages.RMResourcesPage_5);
		this.useSeparateCheckTableModel.setSelection(false);
		if (rememberResources) {
			this.configureCheckBoxForProperty(useSeparateCheckTableModel, PFX + ".useSeparateCheckTableModel"); //$NON-NLS-1$
		}	
		
		new Label(comp1, SWT.NONE); new Label(comp1, SWT.NONE);
		
		this.checktableLDMWidget = new ResourceSelectionWidget(comp1, NONE, null, Messages.RMResourcesPage_6, ".ldm", true, null); //$NON-NLS-1$
		this.checktableLDMWidget.addModifyListener(ml);
		if (rememberResources) {
			this.configureTextForProperty(this.checktableLDMWidget.getText(), PFX + ".ctldmvalue"); //$NON-NLS-1$
		}
		
//		this.resourceWidgets.add(checktableLDMWidget);
		
		this.useSeparateCheckTableModel.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				checktableLDMWidget.setEnabled(useSeparateCheckTableModel.getSelection());
				dialogChanged();
			}
			
		});

		dialogChanged();
		return comp;
	}
	
	protected void updateControlsAfterCreation() {
		if (checktableLDMWidget != null) {
			boolean enabled = this.useSeparateCheckTableModel.getSelection();
			this.checktableLDMWidget.setEnabled(enabled);
		}
	}

	private void dialogChanged() {
		
		setErrorMessage(null);
//		setPageComplete(true);
		
		boolean error = false;
		SapSystem sap = sapWidget.getSelectedSAPSystem();
		if (!error && sap == null) {
			setErrorMessage(Messages.RMResourcesPage_7);
			error = true;
		}
		if (!error) {
			for (ResourceSelectionWidget w : this.resourceWidgets) {
				String s = w.validate();
				if (s != null) {
					setErrorMessage(s);
					error = true;
					break;
				}
			}
			if (!error) {
				if (useSeparateCheckTableModel.getSelection()) {
					String s = this.checktableLDMWidget.validate();
					if (s != null) {
						setErrorMessage(s);
						error = true;
					}
				}
			}
			if (!error) {
				try {
					List<ConfigurationBase> configurations = new ArrayList<ConfigurationBase>();
					IFile tableList = getTableListFile();
					if (tableList != null) {
						configurations.add(new TableList(tableList));
					} else {
						IFile segList = getIDocSegmentListFile();
						if (segList != null) {
							configurations.add(new IDocSegmentList(segList));
						}
					}
					IFile rmConfFile = getRMConfigurationFile();
					if (rmConfFile != null) {
						configurations.add(new RMConfiguration(rmConfFile));
					}
					for (ConfigurationBase conf : configurations) {
						ValidatorBase validator = conf.createValidator();
						ValidationResult validationResult = validator.validate(conf, null);
						for (ValidatorMessage vm : validationResult.getMessages()) {
							int sev = (Integer) vm.getAttribute(IMarker.SEVERITY);
							if (sev == IMarker.SEVERITY_ERROR) {
								String msg = MessageFormat.format(Messages.RMResourcesPage_10, conf.getResource().getName());
								setMessage(msg, IMessageProvider.WARNING);
								break;
							}
						}
					}
				} catch (Exception exc) {
					Activator.logException(exc);
					//					error = true;
					throw new RuntimeException(exc);
				}
			}
		}
		setPageComplete(!error);

	}

	public SapSystem getSelectedSAPSystem() {
		return this.sapWidget.getSelectedSAPSystem();
	}

	
	private boolean endsWith(IFile f, String suffix) {
		if (f == null) {
			return false;
		}
		return f.toString().endsWith(suffix);
		
	}
	
	public IFile getTableListFile() {
		IFile f = this.socWidget.getResource();
		if (endsWith(f, TableList.TABLE_LIST_FILE_EXTENSION)) {
			return f;
		}
		return null;
	}
	
	public IFile getIDocSegmentListFile() {
		IFile f = this.socWidget.getResource();
		if (endsWith(f, IDocSegmentList.IDOC_SEGMENT_LIST_FILE_EXTENSION)) {
			return f;
		}
		return null;
	}
	
	
	public IFile getRMConfigurationFile() {
		return this.rmCfgWidget.getResource();
	}
	
	public IFile getLDMFile() {
		return this.ldmWidget.getResource();
	}
	
	public IFile getCheckTableLDMFile() {
		if (this.useSeparateCheckTableModel.getSelection()) {
			return this.checktableLDMWidget.getResource();
		}
		return null;
	}
	
	@Override
	public boolean nextPressedImpl() {
		setErrorMessage(null);
		SapSystem sap = getSelectedSAPSystem();
		if (sap == null) {
			setErrorMessage(Messages.RMResourcesPage_8);
			return false;
		}
		boolean pwIsOK = PasswordDialog.checkForPassword(getShell(), sap);
		if (!pwIsOK) {
			setErrorMessage(Messages.RMResourcesPage_9);
			return false;
		} 

		return true;	
	}

	@Override
	public void performHelp() {
		PlatformUI.getWorkbench().getHelpSystem().displayHelp(Utils.getHelpID("rmwiz_rmresources")); //$NON-NLS-1$
	}

}
