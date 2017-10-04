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


import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.ibm.iis.sappack.gen.common.ui.connections.PasswordDialog;
import com.ibm.iis.sappack.gen.common.ui.connections.SAPConnectionSelectionWidget;
import com.ibm.iis.sappack.gen.common.ui.connections.SAPConnectionSelectionWidget.SapConnectionSelectedListener;
import com.ibm.iis.sappack.gen.common.ui.connections.SapSystem;
import com.ibm.iis.sappack.gen.common.ui.wizards.PersistentWizardPageBase;
import com.ibm.iis.sappack.gen.common.ui.wizards.ResourceSelectionWidget;
import com.ibm.iis.sappack.gen.tools.sap.importer.MetaDataImporter;
import com.ibm.iis.sappack.gen.tools.sap.importwizard.TableListWizardPage;
import com.ibm.iis.sappack.gen.tools.sap.rmconf.RMConfiguration;
import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.tools.sap.activator.Activator;
import com.ibm.is.sappack.gen.tools.sap.model.SapTableSet;


public class CheckTableBrowseWizard extends Wizard {
	static String copyright() {
		return Copyright.IBM_COPYRIGHT_SHORT;
	}

	class RMConfigSelectionPage extends PersistentWizardPageBase {

		String PFX = "RMConfigSelectionPage."; //$NON-NLS-1$
		ResourceSelectionWidget rmConfigWidget;
		SAPConnectionSelectionWidget sapWidget;

		public RMConfigSelectionPage() {
			super("rmconfigselectionpage", Messages.CheckTableBrowseWizard_0, null); //$NON-NLS-1$
			this.setDescription(Messages.CheckTableBrowseWizard_1);
		}

		@Override
		protected Composite createControlImpl(Composite parent) {

			Composite comp = new Composite(parent, SWT.NONE);
			comp.setLayout(new GridLayout(1, false));
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
			this.configureTextForProperty(sapWidget.getSAPConnectionNameText(), PFX + "sapconnection"); //$NON-NLS-1$

			Composite comp1 = new Composite(comp, SWT.NONE);
			GridLayout gl = new GridLayout(3, false);
			comp1.setLayout(gl);
			GridData gd2 = new GridData(SWT.FILL, SWT.FILL, true, true);
			comp1.setLayoutData(gd2);

			GridData textGD = new GridData(GridData.FILL_HORIZONTAL);

			rmConfigWidget = new ResourceSelectionWidget(comp1, SWT.NONE, Messages.CheckTableBrowseWizard_2, Messages.CheckTableBrowseWizard_3, new String[] { RMConfiguration.RMCONF_FILE_EXTENSION }, true, null);
			rmConfigWidget.getText().setLayoutData(textGD);
			rmConfigWidget.getText().addModifyListener(ml);
			this.configureTextForProperty(rmConfigWidget.getText(), PFX + "RMConfigname"); //$NON-NLS-1$

			dialogChanged();
			return comp;
		}

		@Override
		public boolean nextPressedImpl() {
			SapSystem sap = this.sapWidget.getSelectedSAPSystem();
			boolean pwIsOK = PasswordDialog.checkForPassword(getShell(), sap);
			if (!pwIsOK) {
				setErrorMessage(Messages.CheckTableBrowseWizard_4);
				return false;
			}
			try {
				RMConfiguration conf = new RMConfiguration(this.rmConfigWidget.getResource());
				MetaDataImporter importer = new MetaDataImporter(sap, tableList, null, conf, this.getContainer());
				SapTableSet tableSet = importer.collectTables();
				tableListPage.setTables(tableSet);
			} catch (Exception exc) {
				this.handleException(exc);
				return false;
			}
			canFinish = true;
			return true;
		}

		private void dialogChanged() {

			setErrorMessage(null);
			//			setPageComplete(true);

			boolean error = false;
			SapSystem sap = sapWidget.getSelectedSAPSystem();
			if (!error && sap == null) {
				setErrorMessage(Messages.CheckTableBrowseWizard_5);
				error = true;
			}
			if (!error) {
				String s = this.rmConfigWidget.validate();
				if (s != null) {
					setErrorMessage(s);
					error = true;
				}
			}
			setPageComplete(!error);

		}

	}

	RMConfigSelectionPage configSelectionPage;
	TableListWizardPage tableListPage;
	ITableList tableList;
	boolean canFinish = false;

	public CheckTableBrowseWizard(ITableList tableList) {
		super();
		this.setDialogSettings(Activator.getDefault().getDialogSettings());
		this.setNeedsProgressMonitor(true);
		this.setWindowTitle(Messages.CheckTableBrowseWizard_6);
		this.tableList = tableList;
		this.configSelectionPage = new RMConfigSelectionPage();
		this.addPage(this.configSelectionPage);
		this.tableListPage = new TableListWizardPage();
		this.addPage(this.tableListPage);
	}

	@Override
	public boolean canFinish() {
		return canFinish;
	}

	@Override
	public boolean performFinish() {
		return true;
	}

}
