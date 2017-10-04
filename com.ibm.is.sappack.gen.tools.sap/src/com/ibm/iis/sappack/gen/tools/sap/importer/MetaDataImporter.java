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
// Module Name : com.ibm.iis.sappack.gen.tools.sap.importer
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.iis.sappack.gen.tools.sap.importer;


import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;

import com.ibm.db.models.logical.Entity;
import com.ibm.db.models.logical.Package;
import com.ibm.iis.sappack.gen.common.ui.connections.SapSystem;
import com.ibm.iis.sappack.gen.tools.sap.idocseglist.IDocSegmentList;
import com.ibm.iis.sappack.gen.tools.sap.rmconf.RMConfiguration;
import com.ibm.iis.sappack.gen.tools.sap.tablelist.ITableList;
import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.common.ui.ModeManager;
import com.ibm.is.sappack.gen.tools.sap.PreferencePageRMSettings;
import com.ibm.is.sappack.gen.tools.sap.activator.Activator;
import com.ibm.is.sappack.gen.tools.sap.constants.Constants;
import com.ibm.is.sappack.gen.tools.sap.importer.LogicalTable2LdmImporter;
import com.ibm.is.sappack.gen.tools.sap.importer.SapLogicalTableCollector;
import com.ibm.is.sappack.gen.tools.sap.importer.helper.configuration.ImporterOptionsBase.CHECKTABLEOPTIONS;
import com.ibm.is.sappack.gen.tools.sap.importer.helper.configuration.TableImporterOptions;
import com.ibm.is.sappack.gen.tools.sap.importer.helper.datatype.mapping.IDataTypeMapper;
import com.ibm.is.sappack.gen.tools.sap.importer.helper.datatype.mapping.IDocDataTypeMapper;
import com.ibm.is.sappack.gen.tools.sap.importer.helper.datatype.mapping.IDocVarcharDataTypeMapper;
import com.ibm.is.sappack.gen.tools.sap.importer.helper.datatype.mapping.LogicalTableDataTypeMapper;
import com.ibm.is.sappack.gen.tools.sap.importer.helper.datatype.mapping.VarcharDataTypeMapper;
import com.ibm.is.sappack.gen.tools.sap.importer.helper.field.TechnicalField;
import com.ibm.is.sappack.gen.tools.sap.importer.helper.model.DdmAccessor;
import com.ibm.is.sappack.gen.tools.sap.importer.helper.model.LdmAccessor;
import com.ibm.is.sappack.gen.tools.sap.importer.idoctypes.IDocCollector;
import com.ibm.is.sappack.gen.tools.sap.importer.idoctypes.IDocImporterOptions;
import com.ibm.is.sappack.gen.tools.sap.importer.idoctypes.IDocType;
import com.ibm.is.sappack.gen.tools.sap.importer.idoctypes.IDocTypeExtractException;
import com.ibm.is.sappack.gen.tools.sap.importer.idoctypes.IdocType2LdmImporter;
import com.ibm.is.sappack.gen.tools.sap.importer.idoctypes.SapIDocTypeBrowser;
import com.ibm.is.sappack.gen.tools.sap.importer.idoctypes.Segment;
import com.ibm.is.sappack.gen.tools.sap.model.IDocSegmentTableSet;
import com.ibm.is.sappack.gen.tools.sap.model.IDocTableSet;
import com.ibm.is.sappack.gen.tools.sap.model.SapTable;
import com.ibm.is.sappack.gen.tools.sap.model.SapTableSet;
import com.ibm.is.sappack.gen.tools.sap.utilities.IDocTypeNameConverter;
import com.ibm.is.sappack.gen.tools.sap.utilities.LogicalTableNameConverter;
import com.sap.conn.jco.JCoException;


public class MetaDataImporter {
	
	private SapSystem        sapSystem;
	private ITableList       tableList;
	private IDocSegmentList  idocSegmentList;
	private RMConfiguration  conf;
	private IRunnableContext runnableContext;
	private IProgressMonitor monitor;

	
  	static String copyright() { 
  	   return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	

	public MetaDataImporter(SapSystem sapSystem, ITableList tableList, IDocSegmentList idocSegmentList, RMConfiguration conf, IRunnableContext runnableContext) {
		this.sapSystem = sapSystem;
		this.tableList = tableList;
		this.idocSegmentList = idocSegmentList;
		this.conf = conf;
		this.runnableContext = runnableContext;
		this.monitor = null;
	}

	public MetaDataImporter(SapSystem sapSystem, ITableList tableList, IDocSegmentList idocSegmentList, RMConfiguration conf, IProgressMonitor monitor) {
		this.sapSystem = sapSystem;
		this.tableList = tableList;
		this.idocSegmentList = idocSegmentList;
		this.conf = conf;
		this.runnableContext = null;
		this.monitor = monitor;
	}

	public IDocTableSet collectIDocs(IFile logicalDataModelFile) throws JCoException, 
	                                                                    IDocTypeExtractException, 
	                                                                    InvocationTargetException, 
	                                                                    InterruptedException {
		if (this.idocSegmentList == null || this.idocSegmentList.size() == 0) {
			throw new RuntimeException(Messages.MetaDataImporter_0);
		}

		// get selected IDoc type and segments
		// depending on this page's previous page (which should be either
		// CustomIDocWizardPage or IDocBrowserWizardPage)
		String idocTypeName     = this.idocSegmentList.getIDocType();
		String idocTypeRelease  = this.idocSegmentList.getIDocRelease();
		boolean isExtensionType = this.idocSegmentList.isExtensionIDocType();

		SapIDocTypeBrowser browser  = new SapIDocTypeBrowser(this.sapSystem, isExtensionType, idocTypeRelease);
		IDocType           idocType = new IDocType(idocTypeName, isExtensionType, "", idocTypeRelease, browser); //$NON-NLS-1$
		List<String>       segments = this.idocSegmentList.getIDocSegments();

		// check table blacklist
		IDocImporterOptions options       = this.conf.getIDocOptions();
		List<String>         blacklist     = options.getChecktableBlackList();
		IDocCollector        idocCollector = new IDocCollector(sapSystem, segments, idocType, logicalDataModelFile);

		if (blacklist != null) {
			idocCollector.setBlacklist(blacklist);
		}

		CHECKTABLEOPTIONS ctOption = options.getChecktableOption();

		idocCollector.setCheckTablesOption(ctOption);
		idocCollector.setTextTablesOption(options.getExtractTextTables());

		// TODO remove once old wizards are gone (currently no issue since input dialogs are modal)
		PreferencePageRMSettings.setImporterOptions(options);

		try {
			if (runnableContext == null) {
				if (monitor == null) {
					monitor = new NullProgressMonitor();
				}
				idocCollector.run(monitor);
			} else {
				runnableContext.run(true, true, idocCollector);
			}

			IDocTableSet idocTableSet = idocCollector.getIDocTableSet();
			return idocTableSet;
		} 
		// if there is a JCOException, an IDocTypeExtractException, or ... 
		catch(InvocationTargetException invocationTargetExcpt) {
			Throwable excptCause = invocationTargetExcpt.getCause();
			if (excptCause instanceof JCoException) {
				JCoException jcoExcpt = (JCoException) excptCause;
				
				// reset password if there is a logon failure
				if (jcoExcpt.getGroup() == JCoException.JCO_ERROR_LOGON_FAILURE) {
					sapSystem.resetPassword();
				}
				throw jcoExcpt;
			}
			if (excptCause instanceof IDocTypeExtractException) {
				// ==> unwrap it
				throw (IDocTypeExtractException) excptCause;
			}
			throw invocationTargetExcpt;
		}
		finally {
			// TODO remove once old wizards are gone
			PreferencePageRMSettings.setImporterOptions(null);
		}
	}

	public LdmAccessor[] performIDocImport(final IFile logicalDataModelFile, final IFile checkTableModelFile, final IDocTableSet idocTableSet) throws InvocationTargetException, InterruptedException {

		final List<LdmAccessor> ldmAccessorResult = new ArrayList<LdmAccessor>();

		IRunnableWithProgress runnable = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException {
				try {
					if (monitor == null) {
						monitor = new NullProgressMonitor();
					}
					DdmAccessor ddmAccessor = new DdmAccessor(logicalDataModelFile, Constants.DEFAULT_PKG_NAME_ATOMIC_DOMAINS);

					IDocType selectedIDocType = idocTableSet.getIDocType();
					if (selectedIDocType == null) {
						String idocTypeNotFoundMessage = Messages.MetadataImportWizard_3;
						throw new IDocTypeExtractException(idocTypeNotFoundMessage);
					}

					// determine the DataTypeMapper (dependent on the import options) ...
					IDocImporterOptions options = conf.getIDocOptions();

					// TODO remove once old wizards are gone
					PreferencePageRMSettings.setImporterOptions(options);

					IDataTypeMapper dataTypeMapper;

					if (options.isUseVarcharTypeOnly()) {
						dataTypeMapper = new IDocVarcharDataTypeMapper(options.getVarcharLengthFactor());
					} else {
						dataTypeMapper = new IDocDataTypeMapper();
					}

					LdmAccessor ldmAccessor = new LdmAccessor(ddmAccessor, sapSystem, dataTypeMapper, IDocTypeNameConverter.createIDocTypeNameConverter(selectedIDocType));
					ldmAccessor.setV7Mode(ModeManager.generateV7Stages());

					// 
					String selectedPackageName = conf.getPackagePath() + LdmAccessor.PACKAGE_HIERARCHY_SEPARATOR + selectedIDocType.getName()
							+ com.ibm.is.sappack.gen.common.Constants.LDM_PACKAGE_SUFFIX_IDOC_TYPE_SEGMENTS;
					Package selectedPackage = null;
					if (selectedPackageName != null) {
						selectedPackage = ldmAccessor.getPackageFromPathBelowRoot(selectedPackageName, true);
					}

					List<TechnicalField> technicalFields = conf.getTechnicalFields();

					List<String> selectedIDocTypeSegments = new ArrayList<String>();
					List<String> selectedIDocTypeSegmentsOrig = idocSegmentList.getIDocSegments();
					for (String segmentName : selectedIDocTypeSegmentsOrig) {
						Segment segment = selectedIDocType.getSegment(segmentName);
						if (segment == null) {
							String idocSegmentNotFoundMessage = Messages.MetadataImportWizard_5;
							idocSegmentNotFoundMessage = MessageFormat.format(idocSegmentNotFoundMessage, new Object[] { segmentName, selectedIDocType.getName() });
							// osuhre, 48365: Invalid segment names are just ignored
							Activator.getLogger().log(Level.WARNING, idocSegmentNotFoundMessage);

							/*
							MessageDialog md =
							      new MessageDialog(getShell(), Messages.MetadataImportWizard_6, null,
							            idocSegmentNotFoundMessage, MessageDialog.ERROR,
							            new String[] { Messages.MetadataImportWizard_7 }, 0);
							md.open();
							return false;
							*/
						} else {
							selectedIDocTypeSegments.add(segmentName);
						}
					}

					Package checkTablePackage = null;
					LdmAccessor checkTableAccessor = null;

					if (checkTableModelFile == null) {
						checkTableAccessor = ldmAccessor;
					} else {
						DdmAccessor ctDDMAccessor = new DdmAccessor(checkTableModelFile, Constants.DEFAULT_PKG_NAME_ATOMIC_DOMAINS);
						ctDDMAccessor.setAtomicDomainPackage(options.getAtomicDomainPkgName());
						checkTableAccessor = new LdmAccessor(ctDDMAccessor, sapSystem, dataTypeMapper, LogicalTableNameConverter.createLogicalTableNameConverter());
					}

					String checkTablesPackageName = conf.getCheckTablePackagePath() + LdmAccessor.PACKAGE_HIERARCHY_SEPARATOR + selectedIDocType.getName()
							+ com.ibm.is.sappack.gen.common.Constants.LDM_PACKAGE_SUFFIX_CHECK_TABLES;
					// only create check tables in separate package in CW Mode
					checkTablePackage = checkTableAccessor.getPackageFromPathBelowRoot(checkTablesPackageName, conf.getMode().getID().equals(ModeManager.CW_MODE_ID));

					IdocType2LdmImporter idocImporter = new IdocType2LdmImporter(sapSystem, selectedIDocType, selectedIDocTypeSegments, options, // 
							ldmAccessor, selectedPackage, //
							checkTableAccessor, checkTablePackage);

					if (technicalFields != null && !technicalFields.isEmpty()) {
						idocImporter.setExplicitTechnicalFields(technicalFields);
					}

					SapTableSet valueTables = new SapTableSet();
					for (IDocSegmentTableSet idocSegmentTableSet : idocTableSet.getIdocSegmentTableSetList()) {
						SapTableSet tableSet = idocSegmentTableSet.getSapTableSet();
						Iterator<SapTable> iterator = tableSet.iterator();
						while (iterator.hasNext()) {
							SapTable table = iterator.next();
							if (!valueTables.contains(table)) {
								valueTables.add(table);
							}
						}
					}

					idocImporter.setValueTableSet(valueTables);

					idocImporter.runImport(monitor);
					ldmAccessorResult.add(ldmAccessor);
					addIDs(ldmAccessor, idocSegmentList.getID());
					
					if (checkTableAccessor != null && checkTableAccessor != ldmAccessor) {
						ldmAccessorResult.add(checkTableAccessor);
					}
				} catch (Exception unexpectedExcpt) {
					Activator.getLogger().log(Level.SEVERE, unexpectedExcpt.getMessage(), unexpectedExcpt);
					throw new InvocationTargetException(unexpectedExcpt);
				} finally {
					// TODO remove once old wizards are gone
					PreferencePageRMSettings.setImporterOptions(null);
				}
			}
		};

		if (this.runnableContext == null) {
			if (monitor == null) {
				monitor = new NullProgressMonitor();
			}
			runnable.run(monitor);
		} else {
			this.runnableContext.run(true, true, runnable);
		}

		return ldmAccessorResult.toArray(new LdmAccessor[0]);

	}

	public SapTableSet collectTables() throws JCoException, 
	                                          InvocationTargetException, 
	                                          InterruptedException {
		if (this.tableList == null || this.tableList.getTables().size() == 0) {
			throw new RuntimeException(Messages.MetaDataImporter_2);
		}

		TableImporterOptions options = conf.getTableImportOptions();
		PreferencePageRMSettings.setImporterOptions(options); // TODO remove once old wizards are gone
		SapTableSet result = null;
		try {
			SapLogicalTableCollector collector = new SapLogicalTableCollector(sapSystem, tableList, options);

			if (runnableContext == null) {
				if (monitor == null) {
					monitor = new NullProgressMonitor();
				}
				collector.run(monitor);
			} else {
				runnableContext.run(true, true, collector);
			}
			result = collector.getRelatedTables();
			
		}
		catch(InvocationTargetException invocationTargetExcpt) {
			if (invocationTargetExcpt.getCause() instanceof JCoException) {
				JCoException jcoExcpt = (JCoException) invocationTargetExcpt.getCause();
				
				// reset password if there is a logon failure
				if (jcoExcpt.getGroup() == JCoException.JCO_ERROR_LOGON_FAILURE) {
					sapSystem.resetPassword();
				}
				throw jcoExcpt;
			}

			throw invocationTargetExcpt;
		}
		finally {
			PreferencePageRMSettings.setImporterOptions(null);
		}

		return result;
	}

	public LdmAccessor[] performTableImport(final IFile logicalDataModelFile, final IFile checkTableDataModelFile, final SapTableSet tableSet) throws InvocationTargetException, InterruptedException {
		if (this.tableList == null) {
			throw new RuntimeException(Messages.MetaDataImporter_3);
		}

		final List<LdmAccessor> ldmAccessorResult = new ArrayList<LdmAccessor>();

		IRunnableWithProgress runnable = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException {
				try {
					String selectedPackageName = conf.getPackagePath();
					List<TechnicalField> technicalFields = conf.getTechnicalFields();
					SapTableSet selectedTables = tableSet;

					// determine the DataTypeMapper (dependent on the import options) ...
					TableImporterOptions options = conf.getTableImportOptions();
					// TODO remove once old wizards are gone
					PreferencePageRMSettings.setImporterOptions(options);

					IDataTypeMapper dataTypeMapper;

					if (TableImporterOptions.DATATYPES.USE_VARCHAR_ONLY.equals(options.getDataTypeMode())) {
						dataTypeMapper = new VarcharDataTypeMapper(options.getVarcharLengthFactor());
					} else {
						dataTypeMapper = new LogicalTableDataTypeMapper();
					}
					DdmAccessor ddmAccessor = new DdmAccessor(logicalDataModelFile, Constants.DEFAULT_PKG_NAME_ATOMIC_DOMAINS);
					ddmAccessor.setAtomicDomainPackage(options.getAtomicDomainPkgName());
					LdmAccessor ldmAccessor = new LdmAccessor(ddmAccessor, sapSystem, dataTypeMapper, LogicalTableNameConverter.createLogicalTableNameConverter());
					ldmAccessor.setV7Mode(ModeManager.generateV7Stages());
					Package selectedPackage = null;
					if (selectedPackageName != null) {
						selectedPackage = ldmAccessor.getPackageFromPathBelowRoot(selectedPackageName, true);
					}

					LdmAccessor ctAccessor = null;
					Package ctPackage = null;
					if (checkTableDataModelFile != null) {
						DdmAccessor ctDDMAccessor = new DdmAccessor(checkTableDataModelFile, Constants.DEFAULT_PKG_NAME_ATOMIC_DOMAINS);
						ctDDMAccessor.setAtomicDomainPackage(options.getAtomicDomainPkgName());
						ctAccessor = new LdmAccessor(ctDDMAccessor, sapSystem, dataTypeMapper, LogicalTableNameConverter.createLogicalTableNameConverter());
					} else {
						ctAccessor = ldmAccessor;
					}
					String ctPackageName = conf.getCheckTablePackagePath();
					if (ctPackageName != null) {
						ctPackage = ctAccessor.getPackageFromPathBelowRoot(ctPackageName, true);
					}

					options.setExplicitTechnicalFields(technicalFields);

					LogicalTable2LdmImporter importer = new LogicalTable2LdmImporter(sapSystem, selectedTables, options, ldmAccessor, selectedPackage, ctAccessor, ctPackage, false);
					importer.runImport(monitor);

					ldmAccessorResult.add(ldmAccessor);
					addIDs(ldmAccessor, tableList.getID());
					
					if (ctAccessor != null && ctAccessor != ldmAccessor) {
						ldmAccessorResult.add(ctAccessor);
					}
				} catch (Exception unexpectedExcpt) {
					Activator.getLogger().log(Level.SEVERE, unexpectedExcpt.getMessage(), unexpectedExcpt);
					throw new InvocationTargetException(unexpectedExcpt);
				} finally {
					// TODO remove once old wizards are gone
					PreferencePageRMSettings.setImporterOptions(null);
				}
			}
		};
		if (this.runnableContext == null) {
			if (monitor == null) {
				monitor = new NullProgressMonitor();
			}
			runnable.run(monitor);
		} else {
			this.runnableContext.run(true, true, runnable);
		}
		return ldmAccessorResult.toArray(new LdmAccessor[0]);
	}

	public static final String ANNOT_IMPORT_RUN_ID = "SAPPACK_IMPORT_RUNID_"; //$NON-NLS-1$

	public static final String RUN_ID_SEP     = ","; //$NON-NLS-1$
	public static final String RM_VERSION_SEP = ",RMver="; //$NON-NLS-1$

	public static final String ANNOT_LDM_ID = "SAPPACK_SAP_LDM_ID"; //$NON-NLS-1$

	private void addIDs(LdmAccessor ldmAccessor, String id) {
		long runID = ldmAccessor.getRunID();
		String annotKey = ANNOT_IMPORT_RUN_ID + runID;
		Package p = ldmAccessor.getRootPackage();
		String runIDValue = id                      + RUN_ID_SEP + 
		                    this.conf.getID()       + RUN_ID_SEP +
		                    sapSystem.getFullName() + RM_VERSION_SEP +
		                    com.ibm.is.sappack.gen.common.Constants.getBuildId();
		ldmAccessor.addAnnotation(p, annotKey, runIDValue);
		addLDMIDIfNotPresent(ldmAccessor);
	}
	
	private void addLDMIDIfNotPresent(LdmAccessor ldmAccessor) {
		List<Entity> allEntities = ldmAccessor.getAllEntities();
		String ldmID = null;
		for (Entity e : allEntities) {
			String tableLDMID = LdmAccessor.getAnnotationValue(e, ANNOT_LDM_ID);
			if (tableLDMID != null) {
				ldmID = tableLDMID;
				break;
			}
		}

		if (ldmID == null) {
			ldmID = "LDM" + System.currentTimeMillis(); //$NON-NLS-1$
		}
		for (Entity e : allEntities) {
			String tableLDMID = LdmAccessor.getAnnotationValue(e, ANNOT_LDM_ID);
			if (tableLDMID == null) {
				ldmAccessor.addAnnotation(e, ANNOT_LDM_ID, ldmID);
			} else {
				if (!ldmID.equals(tableLDMID)) {
					throw new RuntimeException(Messages.MetaDataImporter_4);
				}
			}
		}
	}
	
	public void runImport(IFile logicalDataModelFile, IFile checkTableDataModelFile) throws JCoException, 
	                                                                                        IDocTypeExtractException,
	                                                                                        InvocationTargetException, 
	                                                                                        InterruptedException {
		IProgressMonitor savedMonitor = this.monitor;
		try {
			String monitorMsg = Messages.MetaDataImporter_5;
			LdmAccessor[] ldmAccessors = null;
			if (this.idocSegmentList != null) {
				savedMonitor.beginTask(monitorMsg, 100);
				this.monitor = new SubProgressMonitor(savedMonitor, 50, SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK);
				IDocTableSet idocSet = this.collectIDocs(logicalDataModelFile);
				this.monitor = new SubProgressMonitor(savedMonitor, 50, SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK);
				ldmAccessors = this.performIDocImport(logicalDataModelFile, checkTableDataModelFile, idocSet);
			} else {
				savedMonitor.beginTask(monitorMsg, 100);
				this.monitor = new SubProgressMonitor(savedMonitor, 50, SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK);
				SapTableSet tableSet = this.collectTables();
				this.monitor = new SubProgressMonitor(savedMonitor, 50, SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK);
				ldmAccessors = this.performTableImport(logicalDataModelFile, checkTableDataModelFile, tableSet);
			}
			for (LdmAccessor acc : ldmAccessors) {
				acc.saveModel();
			}
		} finally {
			this.monitor = savedMonitor;
		}
	}

}
