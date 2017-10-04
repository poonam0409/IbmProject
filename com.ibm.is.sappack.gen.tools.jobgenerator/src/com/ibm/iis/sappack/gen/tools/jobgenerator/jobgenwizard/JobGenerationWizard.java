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
// Module Name : com.ibm.iis.sappack.gen.tools.jobgenerator.jobgenwizard
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.iis.sappack.gen.tools.jobgenerator.jobgenwizard;


import java.text.MessageFormat;
import java.util.logging.Level;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.progress.IProgressConstants;

import com.ibm.iis.sappack.gen.common.ui.connections.IISConnection;
import com.ibm.iis.sappack.gen.tools.jobgenerator.jobgen.JobGenerationSettings;
import com.ibm.iis.sappack.gen.tools.jobgenerator.jobgen.JobGenerator;
import com.ibm.iis.sappack.gen.tools.jobgenerator.rgconf.RGConfiguration;
import com.ibm.iis.sappack.gen.tools.jobgenerator.rgconf.RGConfiguration.CONFIGURATION_TYPE;
import com.ibm.is.sappack.gen.common.Constants;
import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.common.ui.util.LargeMessageDialog;
import com.ibm.is.sappack.gen.tools.jobgenerator.Activator;
import com.ibm.is.sappack.gen.tools.jobgenerator.wizard.helpers.JobGenerationSummaryCollector;


public class JobGenerationWizard extends Wizard {

  	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	


	// field set from context menu actions
  	private String dbmFileName;

	//
	private RGResourcePage                resourcesPage;
	private ObjectTypeSelectionPage       objectTypeSelectionPage;
	private ABAPExtractSettingsWizardPage abapPage;
	private IDocLoadSettingsPage          idocLoadPage;
	private JobGenSummaryPage             summaryPage;
	private IISDetailsWizardPage          iisPage;
//	private RGConfiguration               rgConfiguration;

	private boolean                       finishAllowed = false;
	private boolean                       selectIDocs = false;
	private boolean                       selectTable = false;


	public JobGenerationWizard() {
		this(null);
	}

	public JobGenerationWizard(String dbmFileName) {
		super();
		setWindowTitle(Messages.JobGenerationWizard_Title);
		this.setDialogSettings(Activator.getDefault().getDialogSettings());
		this.setNeedsProgressMonitor(true);
		this.dbmFileName = dbmFileName;
	}

	@Override
	public void addPages() {
		this.resourcesPage = new RGResourcePage(dbmFileName);
		this.addPage(resourcesPage);

		this.objectTypeSelectionPage = new ObjectTypeSelectionPage() {
			@Override
			public boolean nextPressedImpl() {
				if (tablesButton.isEnabled()) {
					JobGenerationWizard.this.selectTable = tablesButton.getSelection();
				}

				if (idocButton.isEnabled()) {
					JobGenerationWizard.this.selectIDocs = this.idocButton.getSelection();
				}

				if (!JobGenerationWizard.this.selectTable) {
					abapPage.setPageComplete(true);
				}
				if (!JobGenerationWizard.this.selectIDocs) {
					idocLoadPage.setPageComplete(true);
				}

				return true;
			}

		};
		addPage(objectTypeSelectionPage);

		this.abapPage = new ABAPExtractSettingsWizardPage(Constants.ABAP_TRANSFERMETHOD_RFC);
		addPage(this.abapPage);

		this.idocLoadPage = new IDocLoadSettingsPage();
		addPage(this.idocLoadPage);

		this.iisPage = new IISDetailsWizardPage();
		addPage(this.iisPage);

		this.summaryPage = new JobGenSummaryPage();
		addPage(this.summaryPage);

		this.resourcesPage.setABAPExtractSettings(this.abapPage);
		this.resourcesPage.setObjectTypeSelectionPage(this.objectTypeSelectionPage);
	}

	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		finishAllowed = false;

		if (page == this.resourcesPage) {
			return this.objectTypeSelectionPage;
		}
		
		RGConfiguration rgConfig = this.resourcesPage.getRGConfig();
		if (page == this.objectTypeSelectionPage) {

			if (selectTable) {
				if (rgConfig.getConfigurationType().equals(CONFIGURATION_TYPE.SAP_EXTRACT)) {
					return this.abapPage;
				}
			}
			if (selectIDocs) {
				if (rgConfig.getConfigurationType() == CONFIGURATION_TYPE.SAP_LOAD) {
					this.idocLoadPage.setDBMFile(this.resourcesPage.getResource(RGResourcePage.IDX_SOURCE_MODEL_FILE));
					return this.idocLoadPage;
				}
			}
			return this.iisPage;
		}
		if (page == this.abapPage) {
			if (selectIDocs) {
				if (rgConfig.getConfigurationType() == CONFIGURATION_TYPE.SAP_LOAD) {
					this.idocLoadPage.setDBMFile(this.resourcesPage.getResource(RGResourcePage.IDX_SOURCE_MODEL_FILE));
					return this.idocLoadPage;
				}
			}
			return this.iisPage;
		}
		if (page == this.idocLoadPage) {
			return this.iisPage;
		}
		if (page == this.iisPage) {
			finishAllowed = true;
			setSummary();
			return this.summaryPage;
		}
		if (page == this.summaryPage) {
			finishAllowed = true;
			return null;
		}
		return super.getNextPage(page);
	}

	void setSummary() {
		StringBuffer buf = new StringBuffer();
		final String NL = Constants.NEWLINE;
		RGConfiguration rgConfig = this.resourcesPage.getRGConfig();
		
		buf.append(Messages.JobGenerationWizard_Info_GenDetails + NL);
		buf.append(MessageFormat.format(Messages.JobGenerationWizard_Info_RGConfig, rgConfig.getResource().getName())).append(NL);
		buf.append(MessageFormat.format(Messages.JobGenerationWizard_Info_ConfType, RGConfiguration.getConfigurationTypeAsString(rgConfig.getConfigurationType()))).append(NL);

		IISConnection conn = iisPage.getSelectedIISConnection();
		if (conn == null) {
			return;
		}
		buf.append(Messages.JobGenerationWizard_Info_DataTables).append(Constants.NEWLINE).append(Constants.NEWLINE);
		buf.append(MessageFormat.format(Messages.JobGenerationWizard_Info_RefCheckTables,
		                                new Object[] { conn.getDomain(), String.valueOf(conn.getDomainServerPort()) })).append(Constants.NEWLINE);
		buf.append(MessageFormat.format(Messages.JobGenerationWizard_Info_NonRefCheckTables, iisPage.getDSProject())).append(Constants.NEWLINE);
		buf.append(Constants.NEWLINE);
		buf.append(Messages.JobGenerationWizard_Info_SelectedObjects).append(Constants.NEWLINE);
		int jobTypes = objectTypeSelectionPage.getObjectTypes();
		if ((jobTypes & JobGenerationSettings.OBJECT_TYPE_TABLES_DATA) != 0) {
			buf.append(Constants.TAB).append(Messages.JobGenerationWizard_Info_DataTables).append(NL);
		}
		if ((jobTypes & JobGenerationSettings.OBJECT_TYPE_TABLES_REF_AND_TEXT) != 0) {
			buf.append(Constants.TAB).append(Messages.JobGenerationWizard_Info_RefCheckTables).append(NL);
		}
		if ((jobTypes & JobGenerationSettings.OBJECT_TYPE_TABLES_OTHER) != 0) {
			buf.append(Constants.TAB).append(Messages.JobGenerationWizard_Info_NonRefCheckTables).append(NL);
		}
		if ((jobTypes & JobGenerationSettings.OBJECT_TYPE_IDOC) != 0) {
			buf.append(Constants.TAB).append(Messages.JobGenerationWizard_Info_IDocs).append(NL);
		}
		buf.append(Constants.NEWLINE);
		buf.append(Messages.JobGeneratorWizard_24).append(Constants.NEWLINE);

		this.summaryPage.setSummary(buf.toString());
	}

	@Override
	public boolean canFinish() {
		if (!super.canFinish()) {
			return false;
		}
		return this.finishAllowed;
	}

	private IAction getGenerationCompletedAction(final JobGenerationSummaryCollector summaryCollector) {
		final String title = Messages.JobGeneratorWizard_15;
		return new Action(title) {
			public void run() {
				LargeMessageDialog.openLargeMessageDialog(Display.getCurrent().getActiveShell(), title, Messages.JobGeneratorWizard_16, summaryCollector.getSummary());
			}
		};
	}

	@Override
	public boolean performFinish() {
		final JobGenerationSettings settings = new JobGenerationSettings();
		settings.setLaunchJobCompiler(this.summaryPage.getCompileJobsButton());
		settings.setObjectTypes(this.objectTypeSelectionPage.getObjectTypes());

		final RGConfiguration rgConfig   = this.resourcesPage.getRGConfig();
		CONFIGURATION_TYPE    configType = rgConfig.getConfigurationType();
		if (configType == CONFIGURATION_TYPE.SAP_LOAD || configType == CONFIGURATION_TYPE.MOVE) {
			settings.setSourceDBMFile(this.resourcesPage.getResource(RGResourcePage.IDX_SOURCE_MODEL_FILE));
			
			// for transcoding configurations ...
			if (rgConfig.isTranscodingConfig()) {
				// ==> set check tables file
				settings.setCheckTablesDBMFile(this.resourcesPage.getResource(RGResourcePage.IDX_CHECK_TABLE_MODEL_FILE));
			}
		}

		if (configType == CONFIGURATION_TYPE.SAP_EXTRACT || configType == CONFIGURATION_TYPE.MOVE) {
			settings.setTargetDBMFile(this.resourcesPage.getResource(RGResourcePage.IDX_TARGET_MODEL_FILE));
		}

		// IDoc settings
		if (JobGenerationWizard.this.selectIDocs) {
			settings.setIdocMessageType(this.idocLoadPage.getIDocMessageType());
		}

		// IIS settings
		settings.setIisConnection(this.iisPage.getSelectedIISConnection());
		settings.setDsFolder(this.iisPage.getDSFolder());
		settings.setDsProject(this.iisPage.getDSProject());
		settings.setJobNamePrefix(this.iisPage.getJobNamePrefix());
		settings.setOverwriteJob(this.iisPage.getOverwriteJob());

		// ABAP settings
		if (JobGenerationWizard.this.selectTable) {
			settings.setAbapProgramPrefix(this.abapPage.getABAPProgramPrefix());
			settings.setAdditionalABAPCode(this.abapPage.getAdditionalABAPCode());
			settings.setSaveABAPPrograms(this.abapPage.getSaveAbapProgramsOption());
			settings.setNumberOfRetries(this.abapPage.getNumberOfRetries());
			settings.setRetryInterval(this.abapPage.getRetryInterval());
			settings.setSuppressBackgroundJob(this.abapPage.getSuppressBackgroundJob());
			boolean upload = this.abapPage.getUploadAbapProgramsOption();
			settings.setUploadABAPPrograms(upload);
			if (upload) {
				settings.setAbapUploadConnection(this.abapPage.getSapSystem());
			}
			boolean useCTS = this.abapPage.isCTSEnabled();
			settings.setUseCTS(useCTS);
			if (useCTS) {
				settings.setCTSPackage(this.abapPage.getPackage());
				settings.setCTSRequest(this.abapPage.getRequest());
				settings.setCTSRequestDescription(this.abapPage.getRequestDescription());
				settings.setcreateSeparateTransports(this.abapPage.getcreateSeparateTransports());
			}
		} // end of if (JobGenerationWizard.this.selectTable)

		Job generationJob = new Job(Messages.JobGeneratorWizard_14) {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				JobGenerator generator = new JobGenerator(rgConfig);
				try {

					generator.performJobGeneration(settings, monitor);
					final JobGenerationSummaryCollector summaryCollector = generator.getSummaryCollector();
					Boolean isModal = (Boolean) getProperty(IProgressConstants.PROPERTY_IN_DIALOG);
					if (isModal == null || isModal.booleanValue()) {
						Display.getDefault().syncExec(new Runnable() {
							public void run() {
								getGenerationCompletedAction(summaryCollector).run();
							}
						});

						//						Thread.sleep(2000);
					} else {
						setProperty(IProgressConstants.KEEP_PROPERTY, Boolean.TRUE);
						setProperty(IProgressConstants.ACTION_PROPERTY, getGenerationCompletedAction(summaryCollector));
					}
				} catch (Exception exc) {
					Activator.getLogger().log(Level.SEVERE, exc.getMessage(), exc);

					JobGenerationSummaryCollector summaryCollector = generator.getSummaryCollector();

					String statusMsg = MessageFormat.format(Messages.JobGeneratorWizard_11, summaryCollector.getErrorBuffer().toString());
					return new Status(IStatus.ERROR, Activator.PLUGIN_ID, statusMsg);
				}
				String msg = Messages.JobGeneratorWizard_10;
				Activator.getLogger().info(msg);
				//					   summaryPage.setSummary(overallResult, readyToFinish);
				//					   summaryPage.setSummary(summaryCollector.getSummary());
				return new Status(IStatus.OK, Activator.PLUGIN_ID, msg);
			}

		};
		generationJob.setUser(true);
		generationJob.schedule();

		return true;
	}

}
