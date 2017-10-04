//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2011, 2013                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.is.sappack.gen.tools.jobgenerator.wizard
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.tools.jobgenerator.abapProgramUploadWizard;


import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;

import com.ibm.iis.sappack.gen.common.ui.connections.PasswordDialog;
import com.ibm.iis.sappack.gen.common.ui.connections.SAPConnectionSelectionWidget;
import com.ibm.iis.sappack.gen.common.ui.connections.SapSystem;
import com.ibm.iis.sappack.gen.common.ui.wizards.PersistentWizardPageBase;
import com.ibm.is.sappack.gen.common.BaseException;
import com.ibm.is.sappack.gen.common.SAPAccessException;
import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.tools.jobgenerator.Activator;
import com.ibm.is.sappack.gen.tools.jobgenerator.jco.JCoService;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;


public class ABAPProgramUploadPage extends PersistentWizardPageBase {
	//TODO new help pages?
	private static final String HELP_ID = Activator.PLUGIN_ID + "." + "JobGeneratorAbapUploadPage"; //$NON-NLS-1$ //$NON-NLS-2$
	private static final String SETTING_SAP_SYSTEM_NAME = "ABAPProgramUploadPage.sapSystem"; //$NON-NLS-1$

	static String copyright() {
		return com.ibm.is.sappack.gen.tools.jobgenerator.abapProgramUploadWizard.Copyright.IBM_COPYRIGHT_SHORT;
	}

	private SAPConnectionSelectionWidget sapSystemWidget;
	private Button useCTSButton;
	private Combo packageCombo;
	private Combo requestCombo;
	private Button createSeparateTransports;

	private static final String TMP_PACKAGE = "$TMP"; //$NON-NLS-1$

	private String  jcoErrorMessage    = Messages.ABAPProgramUploadPage_0;
	private boolean isCreatingControls = false;

	public ABAPProgramUploadPage(ABAPProgramUploadWizard wizard) {
		super(Messages.ABAPProgramUploadPage_20, Messages.ABAPProgramUploadPage_1, null);
		setDescription(Messages.ABAPProgramUploadPage_2);
	}
	
	@Override
	public IWizardPage getNextPage() {
		//if still no password is provided don't leave the page
		if (!PasswordDialog.checkForPassword(getShell(), this.getSapSystem())) {
			return this;			
		}
		return super.getNextPage();
	}
 
	public boolean isPageComplete() {
		return super.isPageComplete();
	}

	@Override
	public boolean canFlipToNextPage() {
		return isPageComplete();
	}
	
	private void createABAPUploadOptionsGroup(Composite comp) {
		SelectionListener selectionListener = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateEnablement();
			}
		};

		Group abapUploadOptionsGroup = new Group(comp, SWT.NONE);
		abapUploadOptionsGroup.setText(Messages.ABAPProgramUploadPage_19);
		GridLayout gridLayout = new GridLayout(1, false);
		abapUploadOptionsGroup.setLayout(gridLayout);
		abapUploadOptionsGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		this.sapSystemWidget = new SAPConnectionSelectionWidget(abapUploadOptionsGroup, SWT.NONE);
		this.configureTextForProperty(this.sapSystemWidget.getSAPConnectionNameText(), SETTING_SAP_SYSTEM_NAME);
		this.sapSystemWidget.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				//SKA
				ABAPProgramUploadPage.this.packageCombo.setItems(new String [] {});
				ABAPProgramUploadPage.this.requestCombo.setItems(new String [] {});
				updateEnablement();
			}
		});
		this.sapSystemWidget.addSapConnectionSelectedListener(sapSystemWidget.new SapConnectionSelectedPasswordCheckListener());
		
		Group ctsSettingsGroup = new Group(abapUploadOptionsGroup, SWT.NONE);
		ctsSettingsGroup.setText(Messages.ABAPProgramUploadPage_6);
		GridLayout ctsLayout = new GridLayout(2, false);
		ctsSettingsGroup.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL));
		ctsSettingsGroup.setLayout(ctsLayout);

		this.useCTSButton = new Button(ctsSettingsGroup, SWT.CHECK);
		this.useCTSButton.setText(Messages.ABAPProgramUploadPage_7);
		this.useCTSButton.addSelectionListener(selectionListener);
		new Label(ctsSettingsGroup, SWT.NONE);

		this.createSeparateTransports = new Button(ctsSettingsGroup, SWT.CHECK);
		this.createSeparateTransports.setText(Messages.ABAPProgramUploadPage_31);
		this.createSeparateTransports.addSelectionListener(selectionListener);
		new Label(ctsSettingsGroup, SWT.NONE);

		
		Label packageLabel = new Label(ctsSettingsGroup, SWT.NONE);
		packageLabel.setText(Messages.ABAPProgramUploadPage_8);
		this.packageCombo = new Combo(ctsSettingsGroup, SWT.DROP_DOWN | SWT.BORDER | SWT.READ_ONLY);
		this.packageCombo.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
		this.packageCombo.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDown(MouseEvent e) {
				super.mouseDown(e);
				String[] packages = getAllPackages();
				if (packages != null) {
					packageCombo.setItems(packages);
				}
			}
		});
		this.packageCombo.addSelectionListener(selectionListener);

		Label requestLabel = new Label(ctsSettingsGroup, SWT.NONE);
		requestLabel.setText(Messages.ABAPProgramUploadPage_9);
		this.requestCombo = new Combo(ctsSettingsGroup, SWT.DROP_DOWN | SWT.BORDER | SWT.READ_ONLY);
		this.requestCombo.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
		this.requestCombo.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDown(MouseEvent e) {
				super.mouseDown(e);
				String[] requests = getRequests();
				if (requests != null) {
					requestCombo.setItems(requests);
				}
			}
		});
		this.requestCombo.addSelectionListener(selectionListener);

	}
	

	private String[] getAllPackages() {
		try {
			return callRFCModuleForPackages();
		} catch (Exception e) {
			e.printStackTrace();
			String msg;
			if (e instanceof BaseException) {
				msg = e.getLocalizedMessage();
			}
			else {
				msg = MessageFormat.format(jcoErrorMessage, e.getMessage());
			}
			setErrorMessage(msg);
			Activator.getLogger().log(Level.SEVERE, msg, e);

			return null;
		}
	}

	private String[] getRequests() {
		String pack = this.packageCombo.getItems()[this.packageCombo.getSelectionIndex()];
		List<String> packages = new ArrayList<String>();
			packages.add("NEW_REQUEST");
		try {
			packages.addAll(Arrays.asList(callRFCModuleForRequests(pack)));
		} catch (Exception e) {
			e.printStackTrace();
			String msg; 
			if (e instanceof BaseException) {
				msg = e.getLocalizedMessage();
			}
			else {
				msg = MessageFormat.format(jcoErrorMessage, e.getMessage());
			}
			setErrorMessage(msg);
			Activator.getLogger().log(Level.SEVERE, msg, e);

			return null;
		}
		return packages.toArray(new String[0]);
	}

	public SapSystem getSapSystem() {
		return this.sapSystemWidget.getSelectedSAPSystem();
	}

	public boolean isCTSEnabled() {
		return this.useCTSButton.getSelection();
	}

	public String getPackage() {
		int ix = packageCombo.getSelectionIndex();
		if (ix == -1) {
			return null;
		}
		return packageCombo.getItem(ix);
	}

	public String getRequest() {
		String p = getPackage();
		if (TMP_PACKAGE.equals(p)) {
			return null;
		}
		int ix = requestCombo.getSelectionIndex();
		if (ix == -1) {
			return null;
		}
		return requestCombo.getItem(ix);
	}

	private void enableCTSGroup(boolean enabled) {
		this.packageCombo.setEnabled(enabled);
		this.requestCombo.setEnabled(enabled);
	}

	public void updateEnablement() {
		//defaults
		boolean complete = true;
		//SKA
		this.useCTSButton.setEnabled(false);
		//SKA
		enableCTSGroup(false);

		if (!this.isCreatingControls) {
			if (complete) {
				SapSystem system = sapSystemWidget.getSelectedSAPSystem();
				
				if (system == null) {
					setErrorMessage(Messages.ABAPProgramUploadPage_10);
					//SKA
					this.useCTSButton.setSelection(false);
					
					complete = false;
				} else {
					//SKA
					this.useCTSButton.setEnabled(true);

					if (this.useCTSButton.getSelection()) {
						this.packageCombo.setEnabled(true);
						String p = getPackage();
						if (p == null) {
							setErrorMessage(Messages.ABAPProgramUploadPage_11);
							complete = false;
						} else {
							if (TMP_PACKAGE.equals(p)) {
								this.requestCombo.setEnabled(false);
								//       newRequestText.setEnabled(false);
							} else {
								this.requestCombo.setEnabled(true);
								String req = getRequest();
								if (req == null) {
									setErrorMessage(Messages.ABAPProgramUploadPage_12);
									complete = false;
								}
							}
						}
					} else {
						enableCTSGroup(false);
					}
				}
			} else {
				enableCTSGroup(false);
			}
		}
		if (complete) {
			setErrorMessage(null);
		}
		setPageComplete(complete);
	}

	static class JCoDestFunctionPair {
		JCoDestination destination;
		JCoFunction function;
	}

	String[] callRFCModuleForPackages() throws SAPAccessException {
		JCoService   jcoService;
		SapSystem    sapSystem;
		String       retPackageArr[];
		List<String> devClasses;

		retPackageArr = null;
		sapSystem = this.sapSystemWidget.getSelectedSAPSystem();
		if (sapSystem != null) {
			boolean pwProvided = PasswordDialog.checkForPassword(getShell(), sapSystem);
			if (!pwProvided) {
				return new String[0];
			}
			// first get JCO Service and check version
			jcoService = JCoService.getJCoService(sapSystem, "", "", "", true); // force CTS use here //$NON-NLS-1$//$NON-NLS-2$
			String checkMsg = jcoService.checkVersion();
			if (checkMsg != null) {
				throw new SAPAccessException("126900E", new String[] { checkMsg }); //$NON-NLS-1$
			}

			// ... then get the list of packages ...
			devClasses = jcoService.getPackages();

			retPackageArr = devClasses.toArray(new String[0]);
		} // end of if (sapSystem != null)

		return(retPackageArr);
	}

	String[] callRFCModuleForRequests(String devClass) throws JCoException, SAPAccessException {
		JCoService   jcoService;
		SapSystem    sapSystem;
		String       retRequestArr[];
		List<String> requestList;

		retRequestArr = null;
		sapSystem = this.sapSystemWidget.getSelectedSAPSystem();
		if (sapSystem != null) {
			boolean pwProvided = PasswordDialog.checkForPassword(getShell(), sapSystem);
			if (!pwProvided) {
				return new String[0];
			}
			// first get JCO Service and check version
			jcoService = JCoService.getJCoService(sapSystem, "", "", "", true); // force CTS use for this call //$NON-NLS-1$ //$NON-NLS-2$
			String checkMsg = jcoService.checkVersion();
			if (checkMsg != null) {
				throw new SAPAccessException("126900E", new String[] { checkMsg }); //$NON-NLS-1$
			}

			// ... then get the list of packages ...
			requestList = jcoService.getRequests(devClass);

			retRequestArr = requestList.toArray(new String[0]);
		} // end of if (sapSystem != null)

		return(retRequestArr);
	}

	@Override
	public void performHelp() {
		PlatformUI.getWorkbench().getHelpSystem().displayHelp(HELP_ID);
	}

	@Override
	protected Composite createControlImpl(Composite parent) {
		this.isCreatingControls = true;
		
		Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayout(new GridLayout(1, false));
		comp.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		createABAPUploadOptionsGroup(comp);
		
		this.isCreatingControls = false;
		//SKA moved to here to show error messages correctly
		updateEnablement();
		
		return comp;
	}

	@Override
	public boolean nextPressedImpl() {
		boolean pwIsOK = PasswordDialog.checkForPassword(getShell(), getSapSystem());
		if (!pwIsOK) {
			setErrorMessage(Messages.ABAPProgramUploadPage_25);
		} 
		return pwIsOK;
	}

}
