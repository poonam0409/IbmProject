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


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;

import com.ibm.iis.sappack.gen.common.ui.connections.SapSystem;
import com.ibm.iis.sappack.gen.common.ui.util.Utils;
import com.ibm.iis.sappack.gen.tools.sap.idocseglist.IDocSegmentList;
import com.ibm.iis.sappack.gen.tools.sap.importer.MetaDataImporter;
import com.ibm.iis.sappack.gen.tools.sap.rmconf.RMConfiguration;
import com.ibm.iis.sappack.gen.tools.sap.tablelist.TableList;
import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.common.util.StringUtils;
import com.ibm.is.sappack.gen.tools.sap.activator.Activator;
import com.ibm.is.sappack.gen.tools.sap.importer.helper.model.LdmAccessor;
import com.ibm.is.sappack.gen.tools.sap.importer.helper.summary.SummaryCollector;
import com.ibm.is.sappack.gen.tools.sap.importer.helper.summary.SummaryEntry;
import com.ibm.is.sappack.gen.tools.sap.model.IDocTableSet;
import com.ibm.is.sappack.gen.tools.sap.model.SapTableSet;
import com.ibm.is.sappack.gen.tools.sap.utilities.Utilities;


public class MetaDataImportWizard extends Wizard {
	
/*
	public static final String ANNOT_IMPORT_RUN_ID = "SAPPACK_IMPORT_RUNID_"; //$NON-NLS-1$

	public static final String RUN_ID_SEP = ","; //$NON-NLS-1$

	public static final String ANNOT_LDM_ID = "SAPPACK_SAP_LDM_ID"; //$NON-NLS-1$
*/
	private SummaryWizardPage summaryPage;
	private RMResourcesPage resourcesPage;

	private boolean canFinish = false;
	private String socID;
	private String rmConfID;

	private SapTableSet tableSet;
	private IDocTableSet idocSet;

	private long collectionStartTime;
	private long collectionEndTime;

	private TableListWizardPage tlwp;
	private IDocSegmentListWizardPage idoclwp;
	protected LdmAccessor[] ldmAccessors;
	private String ldmFileName;
	private boolean writeLogFile = true;

	static enum SOC_TYPE { UNKNOWN, TABLES, IDOCS };

	private SOC_TYPE sapObjectType = SOC_TYPE.UNKNOWN;

	private MetaDataImporter importer;


	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	

	public MetaDataImportWizard() {
		this(null);
	}

	public MetaDataImportWizard(String ldmFileName) {
		this.setWindowTitle(Messages.MetaDataImportWizard_0);
		this.setDialogSettings(Activator.getDefault().getDialogSettings());
		this.setNeedsProgressMonitor(true);
		this.ldmFileName = ldmFileName;
		this.writeLogFile = true;
	}

	@Override
	public boolean canFinish() {
		return this.canFinish;
	}

	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		if (page == this.resourcesPage) {
			if (this.resourcesPage.getIDocSegmentListFile() != null) {
				return this.idoclwp;
			}
			return this.tlwp;
		}
		if (page == this.idoclwp) {
			return this.summaryPage;
		}
		if (page == this.tlwp) {
			return this.summaryPage;
		}
		return super.getNextPage(page);
	}

	@Override
	public void addPages() {
		this.resourcesPage = new RMResourcesPage(ldmFileName) {
			@Override
			public boolean nextPressedImpl() {
				boolean np = super.nextPressedImpl();
				if (!np) {
					return false;
				}

				try {
					evaluatePages();
					collectionStartTime = System.currentTimeMillis();
					if (sapObjectType == SOC_TYPE.TABLES) {
						tableSet = importer.collectTables();
						tlwp.setTables(tableSet);
					} else if (sapObjectType == SOC_TYPE.IDOCS) {
						idocSet = importer.collectIDocs(this.rmCfgWidget.getResource());
						idoclwp.setIDocTableSet(idocSet);
					} else {
						throw new UnsupportedOperationException();
					}
					collectionEndTime = System.currentTimeMillis();

					return true;
				} catch (Exception exc) {
					handleException(exc);
					return false;
				}
			}

		};
		addPage(this.resourcesPage);

		this.idoclwp = new IDocSegmentListWizardPage() {
			@Override
			public boolean nextPressedImpl() {
				boolean result = super.nextPressedImpl();
				if (!result) {
					return false;
				}
				canFinish = true;
				result = performImport();
				return result;
			}
		};
		addPage(this.idoclwp);

		tlwp = new TableListWizardPage() {
			@Override
			public boolean nextPressedImpl() {
				boolean result = super.nextPressedImpl();
				if (!result) {
					return false;
				}
				canFinish = true;
				result = performImport();
				return result;
			}
		};
		addPage(tlwp);

		this.summaryPage = new SummaryWizardPage() {

			@Override
			public byte[] createAntFile() {
				try {
					byte[] b = Utilities.readInputStream(this.getClass().getResourceAsStream("rmantfile.template")); //$NON-NLS-1$
					String file = new String(b, "UTF-8"); //$NON-NLS-1$
					String date = new Date().toString();
					String id = Long.toString(System.currentTimeMillis());
					String user = System.getProperty("user.name"); //$NON-NLS-1$
					String sapConn = resourcesPage.getSelectedSAPSystem().getFullName();
					String soc = null; 
					if (sapObjectType == SOC_TYPE.TABLES) {
						soc = Utils.getWSRelativePath(resourcesPage.getTableListFile());
					} else if (sapObjectType == SOC_TYPE.IDOCS) {
						soc = Utils.getWSRelativePath(resourcesPage.getIDocSegmentListFile());
					} else {
						throw new UnsupportedOperationException();
					}
					String rmcfg = Utils.getWSRelativePath(resourcesPage.getRMConfigurationFile());
					String ldm = Utils.getWSRelativePath(resourcesPage.getLDMFile());
					IFile checktableLDMFile = resourcesPage.getCheckTableLDMFile();
					String ctldmFile = "none"; //$NON-NLS-1$
					String ctldmAttr = ""; //$NON-NLS-1$
					if (checktableLDMFile != null) {
						ctldmFile = Utils.getWSRelativePath(checktableLDMFile);
						ctldmAttr = "checkTableLDMFile=\"" + ctldmFile + "\""; //$NON-NLS-1$ //$NON-NLS-2$
					}

					file = file.replace("#DATE#", date); //$NON-NLS-1$
					file = file.replace("#ID#", id); //$NON-NLS-1$
					file = file.replace("#USER#", user); //$NON-NLS-1$

					file = file.replace("#SAP#", sapConn); //$NON-NLS-1$
					file = file.replace("#SOC#", soc); //$NON-NLS-1$
					file = file.replace("#RMCFG#", rmcfg); //$NON-NLS-1$
					file = file.replace("#LDM#", ldm); //$NON-NLS-1$
					file = file.replace("#CTLDM#", ctldmFile); //$NON-NLS-1$
					file = file.replace("#CTLDMATTR#", ctldmAttr); //$NON-NLS-1$
					return file.getBytes("UTF-8"); //$NON-NLS-1$
				} catch (Exception exc) {
					Activator.logException(exc);
					return null;
				}
			}

		};
		addPage(this.summaryPage);

	}

	protected void handleException(Exception exc) {
		Utils.showUnexpectedException(getShell(), exc);
	}

	void evaluatePages() throws IOException, CoreException {
		RMConfiguration conf = new RMConfiguration(this.resourcesPage.getRMConfigurationFile());
		this.rmConfID = conf.getID();
		SapSystem sapSystem = this.resourcesPage.getSelectedSAPSystem();
		IFile tableListFile = this.resourcesPage.getTableListFile();
		if (tableListFile != null) {
			this.sapObjectType = SOC_TYPE.TABLES;
			TableList tableList = new TableList(tableListFile);
			this.socID = tableList.getID();
			this.importer = new MetaDataImporter(sapSystem, tableList, null, conf, getContainer());
		} else {
			IFile idocSegmentListFile = this.resourcesPage.getIDocSegmentListFile();
			if (idocSegmentListFile != null) {
				this.sapObjectType = SOC_TYPE.IDOCS;
				IDocSegmentList idocSegmentList;
				idocSegmentList = new IDocSegmentList(idocSegmentListFile);
				this.socID = idocSegmentList.getID();
				this.importer = new MetaDataImporter(sapSystem, null, idocSegmentList, conf, getContainer());
			}
		}
	}

	/*
	private void addLDMIDIfNotPresent(LdmAccessor ldmAccessor) {
		List<Entity> allEntities = ldmAccessor.getAllEntities();
		String ldmID = null;
		for (Entity e : allEntities) {
			String tableLDMID = ldmAccessor.getAnnotationValue(e, ANNOT_LDM_ID);
			if (tableLDMID != null) {
				ldmID = tableLDMID;
				break;
			}
		}

		if (ldmID == null) {
			ldmID = "LDM" + System.currentTimeMillis(); //$NON-NLS-1$
		}
		for (Entity e : allEntities) {
			String tableLDMID = ldmAccessor.getAnnotationValue(e, ANNOT_LDM_ID);
			if (tableLDMID == null) {
				ldmAccessor.addAnnotation(e, ANNOT_LDM_ID, ldmID);
			} else {
				if (!ldmID.equals(tableLDMID)) {
					throw new RuntimeException("Internal error: LDM IDs do not match");
				}
			}
		}
	}
	*/

	@Override
	public boolean performFinish() {
		for (int i = 0; i < ldmAccessors.length; i++) {
			LdmAccessor ldmAccessor = this.ldmAccessors[i];
			/*
			// only add ID for first model which is the base model
			if (i == 0) {
				long runID = ldmAccessor.getRunID();
				String annotKey = ANNOT_IMPORT_RUN_ID + runID;
				Package p = ldmAccessor.getRootPackage();
				String runIDValue = this.socID + RUN_ID_SEP + this.rmConfID + RUN_ID_SEP + this.resourcesPage.getSelectedSAPSystem().getFullName();
				ldmAccessor.addAnnotation(p, annotKey, runIDValue);
				addLDMIDIfNotPresent(ldmAccessor);
			}
			*/
			ldmAccessor.saveModel();
			if (writeLogFile) {
				writeLogFile();
			}
		}
		return true;
	}

	private boolean performImport() {
		try {
			final IFile logicalDataModelFile = this.resourcesPage.getLDMFile();
			IFile checkTableModelFileTmp = this.resourcesPage.getCheckTableLDMFile();
			if (logicalDataModelFile.equals(checkTableModelFileTmp)) {
				checkTableModelFileTmp = null;
			}

			final IFile checkTableModelFile = checkTableModelFileTmp;
				
			long beforeImport = System.currentTimeMillis();
			if (this.sapObjectType ==  SOC_TYPE.TABLES) {
				ldmAccessors = importer.performTableImport(logicalDataModelFile, checkTableModelFile, tableSet);
			} else if (this.sapObjectType == SOC_TYPE.IDOCS){
				ldmAccessors = importer.performIDocImport(logicalDataModelFile, checkTableModelFile, idocSet);
			} else {
				throw new UnsupportedOperationException();
			}

			List<SummaryEntry> summaryEntries = new ArrayList<SummaryEntry>();
			for (LdmAccessor ldmAccessor : this.ldmAccessors) {
				SummaryCollector summaryCollector = ldmAccessor.getSummaryCollector();
				summaryEntries.addAll(summaryCollector.getSummary());
			}

			long afterImport = System.currentTimeMillis();
			double seconds = ((double) (afterImport - beforeImport)) / ((double) 1000);
			String timeMsg = MessageFormat.format(Messages.MetadataImportWizard_4, seconds, com.ibm.is.sappack.gen.common.ui.util.Utilities.getDateString(new Date(beforeImport)), com.ibm.is.sappack.gen.common.ui.util.Utilities.getDateString(new Date(afterImport)));
			String collectionMsg = Messages.MetadataImportWizard_8;
			double collectionTime = ((double) (collectionEndTime - collectionStartTime)) / 1000d;
			collectionMsg = MessageFormat.format(collectionMsg, collectionTime, com.ibm.is.sappack.gen.common.ui.util.Utilities.getDateString(new Date(collectionStartTime)), com.ibm.is.sappack.gen.common.ui.util.Utilities.getDateString(new Date(collectionEndTime)));

			summaryEntries.add(SummaryEntry.createInformationEntry("", collectionMsg)); //$NON-NLS-1$
			summaryEntries.add(SummaryEntry.createInformationEntry("", timeMsg)); //$NON-NLS-1$

			this.summaryPage.setSummary(summaryEntries);
		} catch (Exception exc) {
			handleException(exc);
			return false;
		}
		return true;
	}
	
	public static IFile getLDMLogFile(IFile ldmFile) {
		String ldmName = ldmFile.getName();
		IFile logFile = ldmFile.getProject().getFile(ldmName + ".rmlog"); //$NON-NLS-1$
		return logFile;
	}

	private void writeLogFile() {
		IFile ldmFile = this.resourcesPage.getLDMFile();
		IFile logFile = getLDMLogFile(ldmFile);
		String NL = "\n"; //$NON-NLS-1$
		StringBuffer summaryText = new StringBuffer("=========================================================" + NL); //$NON-NLS-1$
		String dateString = new Date().toString();
		String startMsg = MessageFormat.format(Messages.MetadataImportAction_0, new Object[] { ldmFile.getLocation().toOSString(), dateString });
		summaryText.append(startMsg + NL);
		summaryText.append("--------------------------------------" + NL + NL); //$NON-NLS-1$
		summaryText.append(StringUtils.getVersionInfoString() + NL);
		for (LdmAccessor ldmAccessor : this.ldmAccessors) {
			for (SummaryEntry entry : ldmAccessor.getSummaryCollector().getSummary()) {
				String pfx = null;
				switch (entry.getMessageType()) {
				case ERROR:
					pfx = Messages.MetadataImportAction_1;
					break;
				case WARNING:
					pfx = Messages.MetadataImportAction_2;
					break;
				default:
					pfx = Messages.MetadataImportAction_3;
				}
				String msg = MessageFormat.format(pfx, entry.getMessageText());
				summaryText.append(msg + NL);
			}
		}
		String endMsg = MessageFormat.format(Messages.MetadataImportAction_4, dateString);
		summaryText.append(endMsg + NL);
		summaryText.append("=========================================================" + NL + NL); //$NON-NLS-1$
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(summaryText.toString().getBytes("UTF-8")); //$NON-NLS-1$
			if (logFile.exists()) {
				logFile.appendContents(bais, true, true, null);
			} else {
				logFile.create(bais, true, null);
			}
		} catch (Exception e) {
			com.ibm.is.sappack.gen.tools.sap.activator.Activator.getLogger().log(Level.WARNING, Messages.MetadataImportAction_5, e);
		}

	}

}
