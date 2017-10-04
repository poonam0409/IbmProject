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
// Module Name : com.ibm.is.sappack.gen.tools.sap.importer
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.tools.sap.importer;


import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;

import com.ibm.db.models.logical.AlternateKey;
import com.ibm.db.models.logical.Attribute;
import com.ibm.db.models.logical.Entity;
import com.ibm.db.models.logical.ForeignKey;
import com.ibm.db.models.logical.Package;
import com.ibm.db.models.logical.Relationship;
import com.ibm.iis.sappack.gen.common.ui.connections.SapSystem;
import com.ibm.iis.sappack.gen.common.ui.jco.RfcDestinationDataProvider;
import com.ibm.iis.sappack.gen.common.ui.util.Pair;
import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.common.ui.ModeManager;
import com.ibm.is.sappack.gen.tools.sap.activator.Activator;
import com.ibm.is.sappack.gen.tools.sap.constants.Constants;
import com.ibm.is.sappack.gen.tools.sap.importer.helper.configuration.ImporterOptionsBase.CHECKTABLEOPTIONS;
import com.ibm.is.sappack.gen.tools.sap.importer.helper.configuration.ImporterOptionsBase.RELATIONS;
import com.ibm.is.sappack.gen.tools.sap.importer.helper.configuration.TableImporterOptions;
import com.ibm.is.sappack.gen.tools.sap.importer.helper.field.TableField;
import com.ibm.is.sappack.gen.tools.sap.importer.helper.field.TechnicalField;
import com.ibm.is.sappack.gen.tools.sap.importer.helper.field.TechnicalFieldInteger;
import com.ibm.is.sappack.gen.tools.sap.importer.helper.field.TechnicalFieldVarchar;
import com.ibm.is.sappack.gen.tools.sap.importer.helper.model.LdmAccessor;
import com.ibm.is.sappack.gen.tools.sap.importer.helper.model.LdmNameConverter;
import com.ibm.is.sappack.gen.tools.sap.model.SapTable;
import com.ibm.is.sappack.gen.tools.sap.model.SapTableSet;
import com.ibm.is.sappack.gen.tools.sap.utilities.ABAPColumn;
import com.ibm.is.sappack.gen.tools.sap.utilities.ABAPExtractCodeGenerator;
import com.ibm.is.sappack.gen.tools.sap.utilities.DomainTableImporter;
import com.ibm.is.sappack.gen.tools.sap.utilities.ReferenceTableImporter;
import com.ibm.is.sappack.gen.tools.sap.utilities.SAPTableExtractor;
import com.ibm.is.sappack.gen.tools.sap.utilities.TranslationTableImporter;
import com.ibm.is.sappack.gen.tools.sap.utilities.Utilities;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoRepository;
import com.sap.conn.jco.JCoTable;


public class LogicalTable2LdmImporter {

	private static final boolean CREATE_ALTERNATE_KEYS       = false;   // defect 128154 (don't create alternate keys)
	private static final String KEY_SEPARTOR                 = "."; //$NON-NLS-1$
	private static final String COL_SEPARTOR                 = ", "; //$NON-NLS-1$
	private static final String KEY_COLUMN_ID_TEMPLATE       = "{0}" + KEY_SEPARTOR + "{1}"; //$NON-NLS-1$ //$NON-NLS-2$
	private static final String ALTERNATE_KEY_NAME_TEMPLATE  = "{0}_CheckKey"; //$NON-NLS-1$


	private static class SAPRelationData {
		private String checkTableFieldName;
		private String checkTableName;
		private String fieldName;
		private String foreignKeyName;
		private String foreignKeyTableName;
		private String tableName;

		public SAPRelationData(JCoTable relationInformation) {
			checkTableName      = relationInformation.getString(Constants.JCO_PARAMETER_CHECKTABLE);
			checkTableFieldName = relationInformation.getString(Constants.JCO_PARAMETER_CHECKFIELD);
			fieldName           = relationInformation.getString(Constants.JCO_PARAMETER_FIELDNAME);
			foreignKeyName      = relationInformation.getString(Constants.JCO_PARAMETER_FORKEY);
			foreignKeyTableName = relationInformation.getString(Constants.JCO_PARAMETER_FORTABLE);
			tableName           = relationInformation.getString(Constants.JCO_PARAMETER_TABNAME);
		}

		public String getCheckTableFieldName() {
			return(checkTableFieldName);
		}

		public String getCheckTableName() {
			return(checkTableName);
		}

		public String getFieldName() {
			return(fieldName);
		}

		public String getForeignKeyName() {
			return(foreignKeyName);
		}

		public String getForeignKeyTableName() {
			return(foreignKeyTableName);
		}

		public String getTableName() {
			return(tableName);
		}

		@SuppressWarnings("nls")
		public String toString() {
			return(fieldName + ": " + tableName + "." + foreignKeyName + "-->" + checkTableName + "." + checkTableFieldName);
		}
	} // end of class SAPRelationData



	private StringBuffer                    errorMessage;
	private SapSystem                       sapSystem;
	private SapTableSet                     tableSet;
	private JCoDestination                  destination;
	private LdmAccessor                     ldmAccessor;
	private Package                         rootPackage;
	private LdmAccessor                     checkTableLDMAccessor;
	private TableImporterOptions            options;
	private Package                         checkTextTablesPackage;
	private int                             keyFieldLen;
	private Map<AlternateKey, List<String>> altKeyAnnotationList;
	private HashSet<String>                 dataTableList;                 // defect 128154

	private JCoFunction                     function_DDIF_FIELDINFO_GET;
	private JCoFunction                     function_DD_TABL_GET;

	private JCoFunction                     function_RFC_GET_STRUCTURE_DEFINITION;


	static String copyright() {
		return com.ibm.is.sappack.gen.tools.sap.importer.Copyright.IBM_COPYRIGHT_SHORT;
	}

	public LogicalTable2LdmImporter(SapSystem sapSystem, SapTableSet tableSet, //
			TableImporterOptions options, //
			LdmAccessor ldmAccessor, Package rootPackage, // 
			LdmAccessor checkTableLDMAccessor, Package checkTableRootPackage, // 	                                
			boolean isForIDocType) {
		this.ldmAccessor          = ldmAccessor;
		this.sapSystem            = sapSystem;
		this.tableSet             = tableSet;
		this.options              = options;
		this.rootPackage          = rootPackage;
		this.altKeyAnnotationList = new HashMap<AlternateKey, List<String>>();
		this.dataTableList        = new HashSet<String>();   // defect 128154
		this.checkTableLDMAccessor = checkTableLDMAccessor;
		this.checkTextTablesPackage = checkTableRootPackage;

		if (this.checkTableLDMAccessor == null) {
			this.checkTableLDMAccessor = this.ldmAccessor;
		}

		// determine the length of the key fields 
		if (ModeManager.generateV7Stages()) {
			keyFieldLen = com.ibm.is.sappack.gen.common.Constants.GEN_KEY_FIELD_LEN_V7; 
		}
		else {
			keyFieldLen = com.ibm.is.sappack.gen.common.Constants.GEN_KEY_FIELD_LEN_V6_5; 
		}

		if (this.rootPackage == null) {
			if (this.ldmAccessor != null) {
				this.rootPackage = this.ldmAccessor.getRootPackage();
			}
		}

		if (checkTextTablesPackage == null) {
			// only for CW do we store text and check tables in a subpackage
			if (ModeManager.isCWEnabled() && !isForIDocType) {
				this.checkTextTablesPackage = this.checkTableLDMAccessor.getPackageFor(this.checkTableLDMAccessor.getRootPackage(), 
						this.checkTableLDMAccessor.getRootPackage().getName() + com.ibm.is.sappack.gen.common.Constants.LDM_PACKAGE_SUFFIX_JLT);
			}
			else {
				this.checkTextTablesPackage = this.checkTableLDMAccessor.getRootPackage();
			}
		}
	}

	private final void init(IProgressMonitor progressMonitor) throws JCoException {
		try {
			if (this.destination == null) {

				this.destination = RfcDestinationDataProvider.getDestination(this.sapSystem);
				JCoRepository repository = this.destination.getRepository();
				this.function_DDIF_FIELDINFO_GET = repository.getFunction(Constants.JCO_FUNCTION_DDIF_FIELDINFO_GET);
				this.function_DD_TABL_GET = repository.getFunction(Constants.JCO_FUNCTION_DD_TABL_GET);
				this.function_RFC_GET_STRUCTURE_DEFINITION = repository.getFunction(Constants.JCO_FUNCTION_RFC_GET_STRUCTURE_DEFINITION);

				// DdmAccessor ddmAccessor = new
				// DdmAccessor(this.logicalDataModel);
				// IDataTypeMapper dataTypeMapper = new
				// LogicalTableDataTypeMapper();
				// this.ldmAccessor = new
				// LdmAccessor(ddmAccessor,this.sapSystem, dataTypeMapper);

				// this.rootPackage = this.ldmAccessor.getRootPackage();
			}

		} catch (JCoException e) {
			this.destination = null;
			throw e;
		}

	}

	public void runImport(IProgressMonitor progressMonitor) throws JCoException {
		init(progressMonitor);

		importMetadata(progressMonitor);
		/*
		} catch (JCoException jcoException) {
			InterruptedException interruptedException = new InterruptedException();
			interruptedException.initCause(jcoException);
			throw interruptedException;
		} catch (IOException ioException) {
			InterruptedException interruptedException = new InterruptedException();
			interruptedException.initCause(ioException);
			throw interruptedException;
		}
		 */
	}

	private void importMetadata(IProgressMonitor progressMonitor) throws JCoException {

		List<SapTable> checkTablesForJoinedCheckAndTextTable = new ArrayList<SapTable>();
		Map<String, JCoTable> tableMetadataMap = new HashMap<String, JCoTable>();

		RELATIONS curRelationMode = this.options.getRelationMode();
		int       numberOfSteps   = this.tableSet.size();

		if (curRelationMode == RELATIONS.CW_SAP_PKs_FKs) {
			numberOfSteps += this.tableSet.size();
		}
		else {
			if (curRelationMode == RELATIONS.CW_PKs_FKs) {
				numberOfSteps += (this.tableSet.size() * 3);
			}
		}

		// initialize progress monitor with calculated number of steps 
		progressMonitor.beginTask(Messages.LogicalTable2LdmImporter_0, numberOfSteps);

		
		// retrieve the name of the business object for all tables of this set
		String businessObjectName = this.tableSet.getBusinessObjectName();

		// Iterate over all tables and add the metadata to the Logical Data
		// Model
		Iterator<SapTable> iterator = this.tableSet.iterator();
		while (iterator.hasNext() && !progressMonitor.isCanceled()) {
			SapTable table = iterator.next();

			progressMonitor.subTask(MessageFormat.format(Messages.LogicalTable2LdmImporter_1, table.getName()));
			
			// ignore non-existing SAP tables
			if (!table.existsOnSAPSystem()) {
				continue;
			}

			// Check whether the option "create joined check and text tables" or
			// "create translation table" was selected. If yes, we skip this
			// table if it is a check table and
			// add it to the list of checktables. If the table is a text table,
			// the table can be skipped and no action must be taken.
			if (this.options.getChecktableOption() == TableImporterOptions.CHECKTABLEOPTIONS.JOINED_CHECK_AND_TEXT_TABLES || 
					this.options.getChecktableOption() == TableImporterOptions.CHECKTABLEOPTIONS.TRANSCODING_TABLES) {
				if (table.isCheckTable()) {
					// Add table to the list of checktables
					checkTablesForJoinedCheckAndTextTable.add(table);
					continue;
				}
				if (table.isTextTable()) {
					continue;
				}
			}

			JCoTable tableMetadata = getTableMetadata(table);
			addTableMetadataToModel(tableMetadata, businessObjectName, table);
			
			// store the table metadata - we will need it later
			tableMetadataMap.put(table.getName(), (JCoTable)tableMetadata.clone());

			// add current SAP table to list ...
			this.dataTableList.add(table.getName());  // defect 128154

			progressMonitor.worked(1);
		} // end of while (iterator.hasNext() && !progressMonitor.isCanceled())

		
		/* importer for domain tables and domain translation tables */
		DomainTableImporter domainTableImporter = new DomainTableImporter(this.sapSystem, this.checkTableLDMAccessor, this.checkTextTablesPackage);
		
		// Create Joined Check and Text tables
		if (!checkTablesForJoinedCheckAndTextTable.isEmpty()) {

			boolean createJoinedCheckAndTextTables = (this.options.getChecktableOption() == TableImporterOptions.CHECKTABLEOPTIONS.JOINED_CHECK_AND_TEXT_TABLES);
			if (createJoinedCheckAndTextTables) {
				ReferenceTableImporter importer = new ReferenceTableImporter(this.sapSystem, this.checkTableLDMAccessor, this.checkTextTablesPackage, this.options.getAbapTransferMethod());
				importer.importReducedCheckAndTextTableTables(checkTablesForJoinedCheckAndTextTable, null, progressMonitor);
				annotateCTJoinConditions(this.tableSet);
				
				/* create domain tables  for domain value fields */
				domainTableImporter.createDomainTables(tableMetadataMap, progressMonitor);
			}
	
		} // end of if (!checkTablesForJoinedCheckAndTextTable.isEmpty())

		/* create translation tables and domain translation tables if necessary */
		boolean createTranslationTables        = (this.options.getChecktableOption() == TableImporterOptions.CHECKTABLEOPTIONS.TRANSCODING_TABLES);
		if (createTranslationTables) {
			TranslationTableImporter ttImporter = new TranslationTableImporter(sapSystem, this.checkTableLDMAccessor, this.checkTextTablesPackage, this.ldmAccessor);
			/* create translation tables for reference fields and non-reference fields */
			ttImporter.createTranslationTables(checkTablesForJoinedCheckAndTextTable, progressMonitor);
			ttImporter.annotateTranslationTableReferences(this.tableSet);
			
			/* create domain translation tables for domain value fields */
			domainTableImporter.createDomainTranslationTables(tableMetadataMap, progressMonitor);
			
		}
		
		
		

		if (curRelationMode == TableImporterOptions.RELATIONS.SAP_PKs_FKs      ||
				curRelationMode == TableImporterOptions.RELATIONS.CW_SAP_PKs_FKs   ||
				curRelationMode == TableImporterOptions.RELATIONS.CW_PKs_FKs) {
			// Iterate over all tables and add the relations for each table to
			// the
			// Logical Data Model
			Iterator<SapTable> tableIterator = this.tableSet.iterator();
			while (tableIterator.hasNext() && !progressMonitor.isCanceled()) {
				SapTable table = tableIterator.next();

				progressMonitor.subTask(MessageFormat.format(Messages.LogicalTable2LdmImporter_2, table.getName()));

				// in CW and FK creation: do NOT import relations for Check and Text tables 
				boolean doImportRelations = true;
				if (curRelationMode == TableImporterOptions.RELATIONS.CW_SAP_PKs_FKs  ||
						curRelationMode == TableImporterOptions.RELATIONS.CW_PKs_FKs) {

					if (table.isCheckTable() || table.isTextTable()) {
						doImportRelations = false;
					}
				} // end of if (curRelationMode == ... .RELATIONS.CW_PKs_FKs

				if (doImportRelations) {
					importRelations(table);
				}

				progressMonitor.worked(1);
			} // end of while (tableIterator.hasNext() && !progressMonitor.isCanceled())

			// if there are ALTERNATE KEY annotation(s) 
			if (this.altKeyAnnotationList.size() > 0) {
				// ==> create them
				Iterator<Entry<AlternateKey, List<String>>> iter =	this.altKeyAnnotationList.entrySet().iterator();

				while(iter.hasNext()) {
					StringBuffer                      colBuf   = new StringBuffer();
					Entry<AlternateKey, List<String>> entry    = iter.next();
					AlternateKey 							 altKey   = entry.getKey();
					List<String>                      colList  = entry.getValue();
					Iterator<String>                  listIter = colList.iterator();

					while(listIter.hasNext()) {
						colBuf.append(listIter.next());
						if (listIter.hasNext()) {
							colBuf.append(COL_SEPARTOR);
						}
					}

					String annotationText = MessageFormat.format(Messages.LogicalTable2LdmImporter_15, new Object[] { colBuf.toString() } );
					this.ldmAccessor.addAnnotation(altKey, com.ibm.is.sappack.gen.common.Constants.ANNOT_ALTERNATE_CHECK_KEY,
							annotationText);
				} // end of while(iter.hasNext())
			} // end of if (altKeyAnnotationList.size() > 0)
		}

		// Create new keys and relations as needed for Staging, Alignment and
		// Preload
		if (curRelationMode == RELATIONS.NoPKs_NoFKs     || 
			 curRelationMode == RELATIONS.CW_PKs_NoFKs    || 
			 curRelationMode == RELATIONS.CW_SAP_PKs_FKs  ||
			 curRelationMode == RELATIONS.CW_PKs_FKs) {

			SapTable           table;
			List<SapTable>     tables2processList = new ArrayList<SapTable>();
			Iterator<SapTable> tableIterator;

			// Iterate through all the tables once more
			tableIterator = this.tableSet.iterator();
			while (tableIterator.hasNext() && !progressMonitor.isCanceled()) {
				table = tableIterator.next();
				progressMonitor.subTask(MessageFormat.format(Messages.LogicalTable2LdmImporter_14, table.getName()));

				// if the table is a checktable (i.e. joined check and text
				// table or translation table), nothing must be done
				if (!table.isCheckTable() && !table.isTextTable()) {
					// First a primary key for the current table is needed
					createTechnicalFieldPrimaryKey(table);

					// and SAP table to 'table process' list for later processing
					tables2processList.add(table);
				}

				progressMonitor.worked(1);
			} // end of while (tableIterator.hasNext() && !progressMonitor.isCanceled())

			tableIterator = tables2processList.iterator();
			while (tableIterator.hasNext() && !progressMonitor.isCanceled()) {
				String lastFieldName = Constants.EMPTY_STRING;

				table = tableIterator.next();
				progressMonitor.subTask(MessageFormat.format(Messages.LogicalTable2LdmImporter_2, table.getName()));

				// Now we find which table and column is related to the given
				// data table
				HashSet<String> relatedDataTableNames = table.getRelatedDataTableNames();

				JCoTable[] result = queryTableRelations(table.getName());
				// DD05M_TAB_A contains relation information
				JCoTable relationInformation = result[0];
				if ((relationInformation == null) || (relationInformation.isEmpty())) {
					continue;
				}

				do {
					String tableName      = relationInformation.getString(Constants.JCO_PARAMETER_TABNAME);
					String fieldName      = relationInformation.getString(Constants.JCO_PARAMETER_FIELDNAME);
					String checkTableName = relationInformation.getString(Constants.JCO_PARAMETER_CHECKTABLE);
					String foreignKeyName = relationInformation.getString(Constants.JCO_PARAMETER_FORKEY);

					tableName      = this.ldmAccessor.getNameConverter().convertEntityName(tableName);
					fieldName      = this.ldmAccessor.getNameConverter().convertAttributeName(fieldName);
					foreignKeyName = this.ldmAccessor.getNameConverter().convertAttributeName(foreignKeyName);

					if (this.options.getChecktableOption() == CHECKTABLEOPTIONS.NO_CHECKTABLES) {
						// Check whether the foreign table was also
						// extracted (only in that case a foreign key is needed)
						if (!this.tableSet.contains(checkTableName)) {
							continue;
						}
					} 
					else {
						if (!relatedDataTableNames.contains(checkTableName)) {
							continue;
						}
					}

					boolean isForeignKeyField = false;
					switch(curRelationMode) {
					case CW_PKs_FKs:
						// build the hashcode for the FK suffix (n "columns" + "table name")
						String relationshipName = null;

						// create the relationship name: check table name + field name + hashcode(tablename + fieldname)
						String fkSuffixString = null;
						StringBuffer fkSuffixBuf = new StringBuffer();
						fkSuffixBuf.append(tableName);
						fkSuffixBuf.append("_");        //$NON-NLS-1$
						fkSuffixBuf.append(fieldName);
						fkSuffixString   = String.valueOf(Math.abs(fkSuffixBuf.toString().hashCode()));
						relationshipName = MessageFormat.format(com.ibm.is.sappack.gen.common.Constants.RELATIONSHIP_NAME_TEMPLATE, 
								                                  new Object[] { checkTableName, fieldName, fkSuffixString } );
						
						// convert the field name and check table name
						fieldName = MessageFormat.format(com.ibm.is.sappack.gen.common.Constants.FOREIGN_KEY_NAME_TEMPLATE, 
						                                 new Object[] { checkTableName, 
						                                                fieldName } );
						foreignKeyName = fieldName;
						fieldName      = ldmAccessor.getNameConverter().convertAttributeName(fieldName);
						if (!fieldName.equals(lastFieldName)) 
						{
							checkTableName = ldmAccessor.getNameConverter().convertEntityName(checkTableName);
							createTechnicalFieldForeignKey(checkTableName, foreignKeyName, tableName, fieldName, true);

							// create just one relationship for a foreign key
							this.ldmAccessor.createRelationship(relationshipName, this.rootPackage, tableName, 
							                                    fieldName, checkTableName, 
							                                    fieldName, foreignKeyName, checkTableName,
							                                    this.options.isEnforceForeignKey(), null);
							lastFieldName = fieldName;
						}
						break;

					case CW_SAP_PKs_FKs:
						isForeignKeyField = true;
						// no "break;" statement here !!!!

					default:
						// convert the field name and check table name
						fieldName      = MessageFormat.format(com.ibm.is.sappack.gen.common.Constants.FOREIGN_KEY_NAME_TEMPLATE, 
						                                      new Object[] { checkTableName,
						                                                     fieldName } ); 
						fieldName      = ldmAccessor.getNameConverter().convertAttributeName(fieldName);
						checkTableName = ldmAccessor.getNameConverter().convertEntityName(checkTableName);
						createTechnicalFieldForeignKey(checkTableName, foreignKeyName, tableName, fieldName, isForeignKeyField);
					} // end of switch(curRelationMode)
				} while (relationInformation.nextRow());

				progressMonitor.worked(1);
			} // end of while (tableIterator.hasNext() && !progressMonitor.isCanceled())
		} // end of if (curRelationMode == RELATIONS.CW_PKs_NoFKs ...

	}

	private void importRelations(SapTable table) throws JCoException {
		// If the table was not selected or does not exist on the SAP table
		if (!table.existsOnSAPSystem() || !table.getSelected()) {
			return;
		}

		JCoTable[] result = queryTableRelations(table.getName());

		// DD05M_TAB_A
		JCoTable relationInformation = result[0];
		if (ModeManager.isCWEnabled()) {
			//         importRelations(relationInformation);
			importRelationsCW(relationInformation, table);
		}
		else {
			importRelations(relationInformation);
		}

		// DD08V_TAB_N
		JCoTable cardinalityInformation = result[1];
		importCardinalities(cardinalityInformation);
	}

	private void importCardinalities(JCoTable cardinalityInformation) {
		if (cardinalityInformation == null || cardinalityInformation.isEmpty()) {
			return;
		}

		do {
			String tableName       = cardinalityInformation.getString(Constants.JCO_PARAMETER_TABNAME);
			String checkTableName  = cardinalityInformation.getString(Constants.JCO_PARAMETER_CHECKTABLE);
			String fieldName       = cardinalityInformation.getString(Constants.JCO_PARAMETER_FIELDNAME);
			String cardinality     = cardinalityInformation.getString(Constants.JCO_PARAMETER_CARD);
			String cardinalityLeft = cardinalityInformation.getString(Constants.JCO_PARAMETER_CARDLEFT);

			if (Utilities.isEmpty(cardinality) || Utilities.isEmpty(cardinalityLeft)) {
				String errMsg = MessageFormat.format(Messages.LogicalTable2LdmImporter_5, 
				                                     new Object[] { tableName, checkTableName, fieldName, cardinality, cardinalityLeft });
				Activator.getLogger().log(Level.WARNING, errMsg);
				continue;
			}

			String cleanedTableName      = this.ldmAccessor.getNameConverter().convertEntityName(tableName);
			String cleanedCheckTableName = this.ldmAccessor.getNameConverter().convertEntityName(checkTableName);
			String cleanedFieldName      = this.ldmAccessor.getNameConverter().convertEntityName(fieldName);

			this.ldmAccessor.createCardinality(this.rootPackage, cleanedTableName, cleanedCheckTableName, cleanedFieldName, cardinality, cardinalityLeft);

		} while (cardinalityInformation.nextRow());
	}


	private Map<String, Map<String, List<SAPRelationData>>> buildRelationFieldInfo(JCoTable relationInformation, SapTable curTable) {
		SapTable                                        tmpSAPTable;
		String                                          dataColKeyName;
		Map<String, List<SAPRelationData>>              foreignKeyTableMap;
		Map<String, Map<String, List<SAPRelationData>>> retDataColumnList;
		List<SAPRelationData>                           fkFieldList;
		boolean                                         addToResultList;
		boolean                                         useFilter;

		useFilter         = this.options.getRelationMode() != RELATIONS.CW_SAP_PKs_FKs;
		retDataColumnList = new HashMap<String, Map<String, List<SAPRelationData>>>();
		if (relationInformation != null && !relationInformation.isEmpty()) {

			do {
				addToResultList = true;

				// save SAP relation information ...
				SAPRelationData sapRelData = new SAPRelationData(relationInformation);

				// apply filter if relation mode not equal to 'Use SAP PKs and FKs'
				if (useFilter) {

					// check if check table name exist in the 'tableset'
					tmpSAPTable = this.tableSet.getTable(sapRelData.getCheckTableName());

					// if the table exist in the tableset ...
					if (tmpSAPTable == null) {
						// ==> does not exit ==> don't add it !!
						addToResultList = false;
					}
					else {
						// ==> does exist ==> add it if it's a data table and not a check or text table
						addToResultList = tmpSAPTable.isCheckTable() || tmpSAPTable.isTextTable();
					} // end of (else) if (tmpSAPTable == null)
				} // end of if (this.options.getRelationMode() != RELATIONS.CW_SAP_PKs_FKs)

				if (addToResultList) {
					// check if current relation table exist
					// ... and create the (map) key column name
					dataColKeyName = MessageFormat.format(KEY_COLUMN_ID_TEMPLATE, new Object[] { sapRelData.getTableName(),
							sapRelData.getFieldName() } );

					// get the FK table map for current data column (create a new one if it doesn't exists)
					foreignKeyTableMap = retDataColumnList.get(dataColKeyName);
					if (foreignKeyTableMap == null) {
						foreignKeyTableMap = new HashMap<String, List<SAPRelationData>>();
						retDataColumnList.put(dataColKeyName, foreignKeyTableMap);
					}

					// get the FK field list for current FK table ... (create a new one if it doesn't exists)
					fkFieldList = foreignKeyTableMap.get(sapRelData.getForeignKeyTableName());
					if (fkFieldList == null) {
						fkFieldList = new ArrayList<SAPRelationData>();
						foreignKeyTableMap.put(sapRelData.getForeignKeyTableName(), fkFieldList);
					}
					fkFieldList.add(sapRelData);
				} // end of if (addToResultList)

			} while (relationInformation.nextRow());
		}

		return(retDataColumnList);
	}


	private Relationship createRelationshipFromColumnList(List<SAPRelationData> columnList, String altKeyName) {
		Relationship relationShip = null;

		if (columnList != null) {
			Iterator<SAPRelationData>colIter = columnList.iterator();
			while(colIter.hasNext()) {
				SAPRelationData sapRelData = colIter.next();
				// defect 128154: create relation ship to data tables only (if condition)
				if (this.dataTableList.contains(sapRelData.getCheckTableName())) {   // defect 128154   
					relationShip            = this.ldmAccessor.createRelationship(this.rootPackage, 
							sapRelData.getTableName(), 
							sapRelData.getFieldName(), 
							sapRelData.getCheckTableName(), 
							sapRelData.getCheckTableFieldName(), 
							sapRelData.getForeignKeyName(), 
							sapRelData.getForeignKeyTableName(),
							this.options.isEnforceForeignKey(), 
							altKeyName);
				} // defect 128154
			} // end of while(colIter.hasNext())
		} // end of if (columnList != null)

		return(relationShip);
	}

	private String getRelationshipAnnotationText(Map<String, List<SAPRelationData>> sapRelationDataMap,
			String keyTableName, boolean hasSAPSysTable) {

		String                                          annotationText;
		String                                          colConstant;
		List<SAPRelationData>                           columnList;
		Iterator<Entry<String, List<SAPRelationData>>>  entryIter;
		Iterator<SAPRelationData>                       colIter;

		// create the list of missing columns ...
		StringBuffer missingColumnsBuf = new StringBuffer();
		entryIter                      = sapRelationDataMap.entrySet().iterator();
		while(entryIter.hasNext()) {
			String curTableName = entryIter.next().getKey();

			colConstant = null;
			// get columns of all tables but the key table and SAP SYSx tables
			if (!curTableName.equalsIgnoreCase(keyTableName)                   &&
					!curTableName.equalsIgnoreCase(LdmAccessor.SAP_SYSTEM_TABLE_1) &&
					!curTableName.equalsIgnoreCase(LdmAccessor.SAP_SYSTEM_TABLE_2)) {

				if (curTableName.startsWith("'")) { //$NON-NLS-1$
					colConstant = curTableName;
				}

				columnList = sapRelationDataMap.get(curTableName);
				colIter    = columnList.iterator();
				while(colIter.hasNext()) {
					if (missingColumnsBuf.length() > 0) {
						missingColumnsBuf.append(LdmAccessor.COLUMN_LIST_SEPARATOR);
					}
					missingColumnsBuf.append(colIter.next().getCheckTableFieldName());

					// if there is a constant for that column ==> add it
					if (colConstant != null) {
						missingColumnsBuf.append("="); //$NON-NLS-1$
						missingColumnsBuf.append(colConstant);
					} // end of if (colConstant != null)
				} // end of while(colIter.hasNext())
			} // end of if (!curTableName.equalsIgnoreCase(keyTableName))
		} // end of while(entryIter.hasNext())

		annotationText = MessageFormat.format(Messages.LogicalTable2LdmImporter_16, 
				new Object[] { missingColumnsBuf.toString() } );

		return(annotationText);
	}

	private void addColumnsToAlternateKey(List<SAPRelationData> columnList, AlternateKey altKey) {
		if (columnList != null) {
			Iterator<SAPRelationData> colIter = columnList.iterator();

			while(colIter.hasNext()) {
				SAPRelationData sapRelData = colIter.next();
				this.ldmAccessor.addAttributeToKey(altKey.getEntity(), altKey, sapRelData.getCheckTableFieldName());
			} // end of while(colIter.hasNext())
		} // end of if (columnList != null)
	}

	private AlternateKey getExistingAlternateKey(String tableName, String altKeyName, List<SAPRelationData> columnList) {
		AlternateKey alternateKey = null;
		boolean      colMatch     = true;

		// first get the entity of the passed table
		Entity tableEntity = this.ldmAccessor.findEntity(tableName);
		if (tableEntity != null) {
			// search the alternate key with passed name
			alternateKey = this.ldmAccessor.findAlternateKey(tableEntity, altKeyName);

			// If not found, create a new Alternate key
			if (alternateKey != null) {
				EList<Attribute> attribList = alternateKey.getAttributes();

				// go on if number of columns matches ... 
				if (attribList.size() == columnList.size()) {
					Iterator<Attribute> listIter = attribList.iterator();
					while(listIter.hasNext() && colMatch) {
						Attribute curAttrib = listIter.next();

						// check if the current attribute exists in the passed column list
						Iterator<SAPRelationData> colListIter = columnList.iterator();
						boolean checkSuccessful = false;
						while(colListIter.hasNext() && !checkSuccessful) {
							SAPRelationData curCol = colListIter.next();
							checkSuccessful        = curCol.getCheckTableFieldName().equals(curAttrib.getName());
						} // end of while(colListIter.hasNext() && !checkSuccessful)

						colMatch = checkSuccessful;
					} // end of while(listIter.hasNext() && keyExists)
				} // end of if (attribList.size() == columnList.size())
			} // end of if (alternateKey != null)
		} // end of if (tableEntity != null)

		if (!colMatch) {
			alternateKey = null; 
		}

		return(alternateKey);
	}

	private void importRelationsCW(JCoTable relationInformation, SapTable curTable) {
		if (relationInformation != null && !relationInformation.isEmpty()) {

			String                                            altKeyName;
			String                                            annotationText;
			String                                            dataColKeyName;
			String                                            keyTableName;
			Relationship                                      relationShip; 
			Map<String, Map<String, List<SAPRelationData>>>   relationFieldInfo;
			Map<String, List<SAPRelationData>>                foreignKeyTableMap;
			List<SAPRelationData>                             tableFKFieldList;
			List<SAPRelationData>                             sysFKFieldList;
			Entry<String, Map<String, List<SAPRelationData>>> mapEntry;
			Iterator<Entry<String, Map<String, List<SAPRelationData>>>>  dataColEntryIter;
			int                                               mapSize;
			boolean                                           hasSAPSysFKTable;

			// build the SAP Relations map ...
			relationFieldInfo = buildRelationFieldInfo(relationInformation, curTable);

			dataColEntryIter = relationFieldInfo.entrySet().iterator();
			while(dataColEntryIter.hasNext()) {
				mapEntry           = dataColEntryIter.next();
				dataColKeyName     = mapEntry.getKey();
				foreignKeyTableMap = mapEntry.getValue();

				// get table name from 'key' string
				keyTableName = dataColKeyName.substring(0, dataColKeyName.indexOf(KEY_SEPARTOR));

				// get the FK field list for current data table
				tableFKFieldList = foreignKeyTableMap.get(keyTableName);

				// check if there is more than one FK table for current data column
				altKeyName = null;
				mapSize    = foreignKeyTableMap.size(); 

				// determine the appropriate SAP SYSx table FK field list to be used ...
				sysFKFieldList = foreignKeyTableMap.get(LdmAccessor.SAP_SYSTEM_TABLE_1);
				if (sysFKFieldList == null) {
					sysFKFieldList = foreignKeyTableMap.get(LdmAccessor.SAP_SYSTEM_TABLE_2);
				}
				hasSAPSysFKTable = sysFKFieldList != null;

				if (mapSize > 1 && CREATE_ALTERNATE_KEYS) {
					// create an Alternate key if the 2nd table is not 'SAP_SYSTEM_TABLE'
					if (mapSize != 2 || !hasSAPSysFKTable) {
						SAPRelationData sapRelData;
						AlternateKey    altKey;

						// ==> create Alternate Key (check constraint)
						sapRelData = tableFKFieldList.get(0);  // one column must always exist
						altKeyName = dataColKeyName.substring(dataColKeyName.indexOf(KEY_SEPARTOR) + 1);
						altKeyName = MessageFormat.format(ALTERNATE_KEY_NAME_TEMPLATE, new Object[] { altKeyName } );

						if (hasSAPSysFKTable) {
							// there are SYS FK tables ==> must be processed first !!
							List<SAPRelationData> tmpList = new ArrayList<SAPRelationData>(sysFKFieldList);
							tmpList.addAll(tableFKFieldList);

							tableFKFieldList = tmpList;
						} // end of if (hasSAPSysFKTable)

						altKey = getExistingAlternateKey(sapRelData.getCheckTableName(), altKeyName, tableFKFieldList);
						if (altKey == null) {
							// alternate key for column list does not exist --> get a new  one
							altKey = this.ldmAccessor.getAlternateKey(sapRelData.getCheckTableName(), altKeyName);

							if (altKey != null) {
								// add SYS and table columns to the alternate key
								addColumnsToAlternateKey(tableFKFieldList, altKey);
							} // end of if (altKey != null)
						}

						if (altKey != null) {
							// save data column for alternate key annotation
							List<String> colList = altKeyAnnotationList.get(altKey);

							if (colList == null) {
								colList = new ArrayList<String>();
								altKeyAnnotationList.put(altKey, colList);
							}
							colList.add(dataColKeyName);
						} // end of if (altKey != null)

					} // end of if (mapSize != 2 || !hasSAPSysTable)
				} // end of if (mapSize > 1 && CREATE_ALTERNATE_KEYS)

				// add create relationship using the SYSx and table column lists
				createRelationshipFromColumnList(sysFKFieldList, altKeyName);  // result is not important here
				relationShip = createRelationshipFromColumnList(tableFKFieldList, altKeyName);

				// if an Alternate Key has been created and there is a RelationShip ...
				if (altKeyName != null && relationShip != null) {
					// ==> create RELATIONSHIP annotation
					annotationText = getRelationshipAnnotationText(foreignKeyTableMap,
							keyTableName, hasSAPSysFKTable);
					this.ldmAccessor.addAnnotation(relationShip, com.ibm.is.sappack.gen.common.Constants.ANNOT_ALTERNATE_CHECK_KEY,
							annotationText);
				} // end of if (altKeyName != null && relationShip != null)
			} // end of while(keyColEntryIter.hasNext())
		} // end of if (relationInformation != null && !relationInformation.isEmpty())
	}

	private void importRelations(JCoTable relationInformation) {
		if (relationInformation == null || relationInformation.isEmpty()) {
			return;
		}

		do {
			String tableName           = relationInformation.getString(Constants.JCO_PARAMETER_TABNAME);
			String fieldName           = relationInformation.getString(Constants.JCO_PARAMETER_FIELDNAME);
			String checkTableName      = relationInformation.getString(Constants.JCO_PARAMETER_CHECKTABLE);
			String checkTableFieldName = relationInformation.getString(Constants.JCO_PARAMETER_CHECKFIELD);
			String foreignKeyName      = relationInformation.getString(Constants.JCO_PARAMETER_FORKEY);
			String foreignKeyTableName = relationInformation.getString(Constants.JCO_PARAMETER_FORTABLE);

			if (Utilities.isEmpty(foreignKeyName)) {
				String msg  = MessageFormat.format("Special case (1): table \"{0}\" field \"{1}\" fieldName \"{2}\" checktable \"{3}\" checkTableName \"{4}\" checkfield \"{5}\"",  //$NON-NLS-1$
						new Object[] { tableName, fieldName, checkTableName, checkTableFieldName, 
						foreignKeyTableName, foreignKeyName } );
				Activator.getLogger().log(Level.FINEST, msg);
				// TODO: Special case, the "FORKEY" column is empty, (does
				// this mean a constant value, or a value from the SAP
				// environment, etc... ???)
				continue;
			}

			tableName = tableName.trim();
			
			// osuhre, 159858: always check table != forTable and not
			//                 only if table is system table
//			if (tableName.equalsIgnoreCase(LdmAccessor.SAP_SYSTEM_TABLE_1)  ||
//					tableName.equalsIgnoreCase(LdmAccessor.SAP_SYSTEM_TABLE_2)) {
//				
//				Activator.getLogger().log(Level.FINEST, "FORTABLE is " + tableName); //$NON-NLS-1$
//				
//				

			if (!tableName.equalsIgnoreCase(foreignKeyTableName.trim())) {
				String msg = MessageFormat.format("Special case (2): table \"{0}\" field \"{1}\" checktable \"{2}\" checkfield \"{3}\" fortable \"{4}\" forkey \"{5}\"", //$NON-NLS-1$
						new Object[] { tableName, fieldName, checkTableName, checkTableFieldName, foreignKeyTableName, foreignKeyName });
				Activator.getLogger().log(Level.FINEST, msg);

				// Special case, the value in "FORTABLE" column is not
				// identical with the one from "TABNAME". What does that
				// mean? 
				// osuhre, 159858: it means: ignore. This is a case of a "ternary" relationship
				//                 in SAP. Cannot be mapped to IDA's logical data model.
				continue;
			}
//			}

			String cleanedTableName           = this.ldmAccessor.getNameConverter().convertEntityName(tableName);
			String cleanedFieldName           = this.ldmAccessor.getNameConverter().convertAttributeName(fieldName);
			String cleanedCheckTableName      = this.ldmAccessor.getNameConverter().convertEntityName(checkTableName);
			String cleanedCheckTableFieldName = this.ldmAccessor.getNameConverter().convertAttributeName(checkTableFieldName);
			String cleanedForeignKeyName      = this.ldmAccessor.getNameConverter().convertAttributeName(foreignKeyName);
			String cleanedForeignKeyTableName = this.ldmAccessor.getNameConverter().convertEntityName(foreignKeyTableName);

			// this.ldmAccessor.createRelationship(this.rootPackage, tableName,
			// fieldName, checkTableName, checkTableFieldName, foreignKeyName,
			// foreignTableName);
			this.ldmAccessor.createRelationship(this.rootPackage, cleanedTableName, cleanedFieldName, cleanedCheckTableName, 
					cleanedCheckTableFieldName, cleanedForeignKeyName, cleanedForeignKeyTableName,
					this.options.isEnforceForeignKey(), null);

		} while (relationInformation.nextRow());

	}

	private JCoTable getTableMetadata(SapTable table) throws JCoException {

		// If the table was not selected or does not exist on the SAP system
		if (!table.existsOnSAPSystem() || !table.getSelected()) {
			return null;
		}

		JCoTable tableMetadata = queryTableMetadata(table.getName());

		// If there was no metadata found for this table
		if (tableMetadata == null) {
			return null;
		}

		return tableMetadata;
	}

	/**
	 * Adds the metadata of a table to the model
	 * 
	 * @param tableMetadata
	 *            : The JCo table containing the table's metadata (the
	 *            "DFIES_TAB" result table of the
	 *            "JCO_FUNCTION_DDIF_FIELDINFO_GET" BAPI)
	 * @throws JCoException
	 */
	private void addTableMetadataToModel(JCoTable tableMetadata, String businessObjectName , SapTable table) throws JCoException {

		if (tableMetadata == null || tableMetadata.isEmpty()) {
			System.err.println(Messages.LogicalTable2LdmImporter_9);
			return;
		}

		boolean isUnicode       = com.ibm.is.sappack.gen.common.ui.util.Utilities.isUnicodeSystem(this.sapSystem);
		String tableName        = tableMetadata.getString(Constants.JCO_PARAMETER_TABNAME);
		String cleanedTableName = this.ldmAccessor.getNameConverter().convertEntityName(tableName); //  StringUtils.cleanFieldName(tableName);

		Entity entity = null;

		boolean extractSAPRelations = false;
		if (this.options.getRelationMode() == TableImporterOptions.RELATIONS.SAP_PKs_FKs ||
				this.options.getRelationMode() == TableImporterOptions.RELATIONS.CW_SAP_PKs_FKs) {
			extractSAPRelations = true;
		}

		if (table.isCheckTable() || table.isTextTable()) {

			entity = this.ldmAccessor.findEntity(this.checkTextTablesPackage, cleanedTableName);
			if (entity == null) {

				entity = this.ldmAccessor.createEntity(this.checkTextTablesPackage, tableName, 
						cleanedTableName, table.getDescription());

				this.ldmAccessor.addAnnotation(entity, com.ibm.is.sappack.gen.common.Constants.ANNOT_LOGICAL_TBL_NAME,
						tableName);
				this.ldmAccessor.addAnnotation(entity, com.ibm.is.sappack.gen.common.Constants.ANNOT_PHYSICAL_TBL_NAME,
						tableName);
				this.ldmAccessor.addAnnotation(entity, com.ibm.is.sappack.gen.common.Constants.ANNOT_EXTRACT_RELATIONS,
						Boolean.toString(extractSAPRelations));
				this.ldmAccessor.addAnnotation(entity, com.ibm.is.sappack.gen.common.Constants.ANNOT_DATA_OBJECT_SOURCE,
						com.ibm.is.sappack.gen.common.Constants.DATA_OBJECT_SOURCE_TYPE_LOGICAL_TABLE);
				this.ldmAccessor.addAnnotation(entity, com.ibm.is.sappack.gen.common.Constants.ANNOT_SAP_TABLE_NAME,
						tableName);
				this.ldmAccessor.addAnnotation(entity, com.ibm.is.sappack.gen.common.Constants.ANNOT_GENERATED_MODEL_VERSION,
						com.ibm.is.sappack.gen.common.Constants.MODEL_VERSION);
				this.ldmAccessor.addAnnotation(entity, com.ibm.is.sappack.gen.common.Constants.ANNOT_TBL_DEV_CLASS, table.getDevClass());

				this.ldmAccessor.addAnnotation(entity, com.ibm.is.sappack.gen.common.Constants.ANNOT_BUSINESS_OBJECT_NAME, businessObjectName);
			}
		}
		else {
			entity = this.ldmAccessor.findEntity(this.rootPackage, cleanedTableName);
			if (entity == null) {
				entity = this.ldmAccessor.createEntity(this.rootPackage, tableName, cleanedTableName, table.getDescription());
				this.ldmAccessor.addAnnotation(entity, com.ibm.is.sappack.gen.common.Constants.ANNOT_EXTRACT_RELATIONS,
						Boolean.toString(extractSAPRelations));
				this.ldmAccessor.addAnnotation(entity, com.ibm.is.sappack.gen.common.Constants.ANNOT_DATA_OBJECT_SOURCE,
						com.ibm.is.sappack.gen.common.Constants.DATA_OBJECT_SOURCE_TYPE_LOGICAL_TABLE);
				this.ldmAccessor.addAnnotation(entity, com.ibm.is.sappack.gen.common.Constants.ANNOT_SAP_TABLE_NAME,
						tableName);
				this.ldmAccessor.addAnnotation(entity, com.ibm.is.sappack.gen.common.Constants.ANNOT_GENERATED_MODEL_VERSION,
						com.ibm.is.sappack.gen.common.Constants.MODEL_VERSION);
				this.ldmAccessor.addAnnotation(entity, com.ibm.is.sappack.gen.common.Constants.ANNOT_TBL_DEV_CLASS, table.getDevClass());
				this.ldmAccessor.addAnnotation(entity, com.ibm.is.sappack.gen.common.Constants.ANNOT_BUSINESS_OBJECT_NAME, businessObjectName);
			}
		}

		//		String rfcProgramName = createRFCProgramName(ldmAccessor, tableName);
		//		ABAPExtractCodeGenerator rfcCodeGenerator = new ABAPExtractCodeGenerator(tableName, ldmAccessor);
		ABAPExtractCodeGenerator codeGenerator = new ABAPExtractCodeGenerator(tableName, ldmAccessor);

		//		String cpicProgramName = createCPICProgramName(ldmAccessor, tableName);
		//		ABAPExtractCodeGenerator cpicCodeGenerator = new ABAPExtractCodeGenerator(tableName, cpicProgramName);

		// read metadata regarding nullable columns from DD03L
		Map<String, Map<String, String>> additionalTableMetadata = readAdditionalTableMetadata(tableName);

		// Iterate over all columns for this table and add their metadata to the
		// table
		do {
			String dataTypeName = tableMetadata.getString(Constants.JCO_PARAMETER_DATATYPE);
			String domainName = tableMetadata.getString(Constants.JCO_PARAMETER_DOMNAME);
			boolean domainHasFixedValues = tableMetadata.getString(Constants.JCO_PARAMETER_DOMAIN_FIXED_VALUES).contains(Constants.JCO_PARAMETER_VALUE_TRUE);
			String dataElementName = tableMetadata.getString(Constants.JCO_PARAMETER_ROLLNAME);
			char sapBaseDataType = tableMetadata.getChar(Constants.JCO_PARAMETER_INTTYPE);
			int length = tableMetadata.getInt(Constants.JCO_PARAMETER_LENG);
			int intLength = tableMetadata.getInt(Constants.JCO_PARAMETER_INTLEN);

			// output length will be used as the display length later
			//			int outputLength = tableMetadata.getInt(Constants.JCO_PARAMETER_OUTPUTLEN);

			int decimals = tableMetadata.getInt(Constants.JCO_PARAMETER_DECIMALS);
			String columnName = tableMetadata.getString(Constants.JCO_PARAMETER_FIELDNAME);
			String columnDescription = tableMetadata.getString(Constants.JCO_PARAMETER_SCRTEXT_L);
			String columnLabel = tableMetadata.getString(Constants.JCO_PARAMETER_FIELDTEXT);

			String checkTable = tableMetadata.getString(Constants.JCO_PARAMETER_CHECKTABLE);
			
			boolean isColumnKeyInSAP = tableMetadata.getString(Constants.JCO_PARAMETER_KEYFLAG).equalsIgnoreCase(Constants.JCO_PARAMETER_VALUE_TRUE);

			boolean isKeyColumn;
			switch(this.options.getRelationMode()) {
			case SAP_PKs_FKs:
			case SAP_PKs_NoFKs:
			case CW_SAP_PKs_FKs:
				isKeyColumn = isColumnKeyInSAP;
				break;

			default:
				isKeyColumn = false;
			}

			Map<String, String> additionalColumnMetadata = additionalTableMetadata.get(columnName); 
			String notNullValue = additionalColumnMetadata.get("NOTNULL"); //$NON-NLS-1$
			boolean isNotNull = notNullValue.contains("X"); //$NON-NLS-1$
			boolean isNullable = !isNotNull;


			// removed: double the size of the columns is a unicode system
			// int charFactor = 1;
			// if(isUnicode) {
			// charFactor = 2;
			// }

			// LdmDataType ltdt =
			// Sap2LdmDataTypeMapper.mapDataTypeLogicalTable(this.ddmAccessor,
			// this.rootPackage, sapDataType, sapBaseDataType, length, intlen,
			// decimals, charFactor, true);

			// Already done in the field!
			// // we replace all / with _ in the column name
			// String cleanedColumnName = columnName.replace('/', '_');
			// if (cleanedColumnName.startsWith("_")) {
			// cleanedColumnName = cleanedColumnName.substring(1);
			// }

			TableField field = new TableField(columnName, columnDescription, columnLabel, dataElementName, domainName, dataTypeName, sapBaseDataType, length, intLength, decimals, isKeyColumn, isNullable);
			if ((checkTable != null) && checkTable.trim().length() > 0) {
				field.setCheckTable(checkTable);
			}
			// field.setVarcharLengthFactor(this.options.getVarcharLengthFactor());

			if (this.options.getAllowColumnsToBeNullable()) {
				field.setNullable(true);
			}

			String cleanedColName = this.ldmAccessor.getNameConverter().convertAttributeName(columnName);

			// Attribute attribute =
			// this.ldmAccessor.addColumnMetadataToTable(entity,
			// cleanedColumnName, isKeyColumn, columnDescription, columnLabel,
			// sapDataType, isNullable);
			Attribute attribute = this.ldmAccessor.addColumnMetadataToTable(entity, field);
			if (attribute != null) {
				this.ldmAccessor.addAnnotation(attribute, com.ibm.is.sappack.gen.common.Constants.ANNOT_EXTRACT_RELATIONS, 
						Boolean.toString(extractSAPRelations));
				this.ldmAccessor.addAnnotation(attribute, com.ibm.is.sappack.gen.common.Constants.ANNOT_DATA_OBJECT_SOURCE, 
						com.ibm.is.sappack.gen.common.Constants.DATA_OBJECT_SOURCE_TYPE_LOGICAL_TABLE);
				this.ldmAccessor.addAnnotation(attribute, com.ibm.is.sappack.gen.common.Constants.ANNOT_COLUMN_IS_UNICODE, 
						Boolean.toString(isUnicode));
				this.ldmAccessor.addAnnotation(attribute, com.ibm.is.sappack.gen.common.Constants.ANNOT_COLUMN_DERIVATION_EXPRESSION, 
						cleanedColName);
				this.ldmAccessor.addAnnotation(attribute, com.ibm.is.sappack.gen.common.Constants.ANNOT_TRANSFORMER_SOURCE_MAPPING,  
						cleanedColName);
				this.ldmAccessor.addAnnotation(attribute, com.ibm.is.sappack.gen.common.Constants.ANNOT_SAP_COLUMN_NAME, columnName);
			
				/* domain specific annotations if domain has fixed values */
				if(domainName !=null && !domainName.equals(Constants.EMPTY_STRING) && domainHasFixedValues) {
					
					String domainTable = DomainTableImporter.createModelDomainTableName(domainName);
					this.ldmAccessor.addAnnotation(attribute, com.ibm.is.sappack.gen.common.Constants.ANNOT_DOMAIN_TABLE, domainTable);
					String domainTranslationTable = DomainTableImporter.createModelDomainTranslationTableName(domainName);
					this.ldmAccessor.addAnnotation(attribute, com.ibm.is.sappack.gen.common.Constants.ANNOT_DOMAIN_TRANSLATION_TABLE, domainTranslationTable);
					String domainTranslationTableColumn = DomainTableImporter.createModelDomainTranslationTableColumnName(domainName);
					this.ldmAccessor.addAnnotation(attribute, com.ibm.is.sappack.gen.common.Constants.ANNOT_DOMAIN_TRANSLATION_TABLE_COLUMN, domainTranslationTableColumn);
					String domainTranslationTableJoinCondition = DomainTableImporter.createModelDomainTranslationTableJoinCondition(cleanedTableName, cleanedColName, domainName);
					this.ldmAccessor.addAnnotation(attribute, com.ibm.is.sappack.gen.common.Constants.ANNOT_DOMAIN_TRANSLATION_TABLE_JOIN_CONDITION, domainTranslationTableJoinCondition);
					
				}
				
				this.ldmAccessor.addAnnotation(attribute, com.ibm.is.sappack.gen.common.Constants.ANNOT_IS_KEY_IN_SAP, Boolean.toString(isColumnKeyInSAP));
				
				
				
				// if (isUnicode) {
					// ltdt =
						// Sap2LdmDataTypeMapper.mapDataTypeLogicalTable(this.ldmAccessor,
								// this.rootPackage, sapDataType, sapBaseDataType, length,
				// decimals, 1, false);
				// codeGenerator.addColumn(columnName, ltdt);
				// this.ldmAccessor.addAnnotation(attribute,
				// com.ibm.is.sappack.gen.common.Constants.ANNOT_COLUMN_IS_UNICODE,
				// Boolean.toString(isUnicode));
				// }
			} else {
				// If the attribute already existed in the model, it will also
				// be added to the code generator
				attribute = ldmAccessor.findAttribute(entity, field.getFieldName());
			}

			String includeStruct = additionalColumnMetadata.get(com.ibm.is.sappack.gen.common.Constants.ANNOT_INCLUDE_STRUCTURE);
			if (includeStruct != null) {
				this.ldmAccessor.addAnnotation(attribute, com.ibm.is.sappack.gen.common.Constants.ANNOT_INCLUDE_STRUCTURE, includeStruct);
			}
			String appendStruct = additionalColumnMetadata.get(com.ibm.is.sappack.gen.common.Constants.ANNOT_APPEND_STRUCTURE);
			if (appendStruct != null) {
				this.ldmAccessor.addAnnotation(attribute, com.ibm.is.sappack.gen.common.Constants.ANNOT_APPEND_STRUCTURE, appendStruct);
			}

			if (this.options.hasColumnDefaultValue()) {
				attribute.setDefaultValue(this.options.getColumDefaultValue());
			}

			codeGenerator.addColumn(new ABAPColumn(columnName, field));

			//			rfcCodeGenerator.addColumn(new ABAPColumn(columnName, field));
			//		cpicCodeGenerator.addColumn(new ABAPColumn(columnName, field));

		} while (tableMetadata.nextRow());

		if ((this.options.getAbapTransferMethod() & com.ibm.is.sappack.gen.common.Constants.ABAP_TRANSFERMETHOD_RFC) > 0) {
			this.ldmAccessor.addAnnotation(entity, com.ibm.is.sappack.gen.common.Constants.ANNOT_ABAP_PROGRAM_NAME,
					codeGenerator.createProgramName(com.ibm.is.sappack.gen.common.Constants.ABAP_TRANSFERMETHOD_RFC));

			String abapCodeRFC = codeGenerator.generateABAPCode(com.ibm.is.sappack.gen.common.Constants.ABAP_TRANSFERMETHOD_RFC);
			this.ldmAccessor.addAnnotation(entity, com.ibm.is.sappack.gen.common.Constants.ANNOT_ABAP_CODE, abapCodeRFC);
		}
		if ((this.options.getAbapTransferMethod() & com.ibm.is.sappack.gen.common.Constants.ABAP_TRANSFERMETHOD_CPIC) > 0) {
			this.ldmAccessor.addAnnotation(entity, com.ibm.is.sappack.gen.common.Constants.ANNOT_ABAP_PROGRAM_NAME_CPIC,
					codeGenerator.createProgramName(com.ibm.is.sappack.gen.common.Constants.ABAP_TRANSFERMETHOD_CPIC));

			String abapCodeCPIC = codeGenerator.generateABAPCode(com.ibm.is.sappack.gen.common.Constants.ABAP_TRANSFERMETHOD_CPIC);
			this.ldmAccessor.addAnnotation(entity, com.ibm.is.sappack.gen.common.Constants.ANNOT_ABAP_CODE_CPIC, abapCodeCPIC);
		}

		// if (this.options.getCreateTechnicalFields()) {
		addTechnicalFields(ldmAccessor, entity);
		// }

	}

	static class SAPStructure {
		String name;
		int end;
		boolean isInclude;

		public SAPStructure(String name, int end, boolean isInclude) {
			super();
			this.name = name;
			this.end= end;
			this.isInclude = isInclude;
		}

		@SuppressWarnings("nls")
		public String toString() {
			return (this.isInclude ? "INCLUDE" : "APPEND" )+ " " + this.name + " (" + this.end + ")"; 
		}

	}

	static void push(Stack<SAPStructure> stack, SAPStructure struct) {
		stack.push(struct);
		// all end position of the other structures must be incremented by 1
		// because the length does not take into account the "invisible" positions of DD03L
		// denoting INCLUDE or APPEND structures
		if (stack.size() >= 2) {
			for (int i=stack.size()-2; i>=0; i--) {
				stack.get(i).end++;
			}
		}
	}

	static SAPStructure pop(Stack<SAPStructure> stack) {
		return stack.pop();
	}

	static SAPStructure peek(Stack<SAPStructure> stack) {
		if (stack.isEmpty()) {
			return null;
		}
		return stack.peek();
	}

	static String getAllStructures(Stack<SAPStructure> stack, boolean isInclude) {
		StringBuffer result = new StringBuffer();
		for (int i= stack.size() - 1; i >=0; i--) {
			SAPStructure struct = stack.get(i);
			if (struct.isInclude == isInclude) {
				if (result.length() != 0) {
					result.append(Constants.INCLUDE_APPEND_STRUCTURE_SEPARATOR);
				}
				result.append(struct.name);
			}
		}
		if (result.length() == 0) {
			return null;
		}
		return result.toString();
	}

	Map<String, Integer> struct2LengthMap = new HashMap<String, Integer>();

	int getStructureLength(String structName) {
		Integer i = struct2LengthMap.get(structName);
		if ( i != null) {
			return i.intValue();
		}
		try {
			this.function_RFC_GET_STRUCTURE_DEFINITION.getImportParameterList().setValue("TABNAME", structName); //$NON-NLS-1$
			this.function_RFC_GET_STRUCTURE_DEFINITION.execute(destination);
			JCoTable tab = this.function_RFC_GET_STRUCTURE_DEFINITION.getTableParameterList().getTable("FIELDS"); //$NON-NLS-1$
			int structureLength = tab.getNumRows();
			struct2LengthMap.put(structName, structureLength);
			return structureLength;
		} catch(JCoException exc) {
			String msg = "Length of structure ''{0}'' could not be determined, assuming length 0"; //$NON-NLS-1$
			msg = MessageFormat.format(msg, structName);
			Activator.getLogger().log(Level.FINE, msg);
			Activator.getLogger().log(Level.FINE, Messages.LogExceptionMessage, exc);
			exc.printStackTrace();
			return 0;
		}
	}


	// returns the DD03L rows for all field names of the specified table
	// has two special fields
	//   com.ibm.is.sappack.gen.common.Constants.ANNOT_INCLUDE_STRUCTURE
	//   com.ibm.is.sappack.gen.common.Constants.ANNOT_APPEND_STRUCTURE
	// indicating the name of the include or append structure of the field (if applicable)
	private Map<String, Map<String, String>> readAdditionalTableMetadata(String tableName) {
		try {
			SAPTableExtractor ex = new SAPTableExtractor(destination, "DD03L", Arrays.asList(new String[] { "FIELDNAME", "MANDATORY", "NOTNULL", "PRECFIELD", "POSITION", "ADMINFIELD" }), "TABNAME = '" + tableName + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$
			SAPTableExtractor.Result result = ex.performQuery();
			Map<Integer, Map<String, String>> appendStructureMap = new HashMap<Integer, Map<String, String>>();
			Map<Integer, Map<String, String>> includeStructureMap = new HashMap<Integer, Map<String, String>>();
			HashMap<String, Map<String, String>> rows = new HashMap<String, Map<String, String>>();
			Vector<Map<String, String>> rowsList = new Vector<Map<String,String>>(); 
			while (result.nextRow()) {
				int pos = Integer.parseInt(result.getValue("POSITION")); //$NON-NLS-1$
				String fieldName = result.getValue("FIELDNAME"); //$NON-NLS-1$
				Map<String, String> row = result.getRow();
				if (".INCLUDE".equals(fieldName)) { //$NON-NLS-1$
					includeStructureMap.put(pos, row);
				} else if (".INCLU--AP".equals(fieldName)) { //$NON-NLS-1$
					appendStructureMap.put(pos, row);
				} else {
					rows.put(fieldName, row);
					if (pos >= rowsList.size()) {
						rowsList.setSize(pos + 1);
					}
					rowsList.set(pos, row);
				}
			}	
			Stack<SAPStructure> currentStructures = new Stack<LogicalTable2LdmImporter.SAPStructure>();

			for (int i=0; i<rowsList.size(); i++) {
				Map<String, String> row = rowsList.get(i);
				Map<String, String> inclStruct = includeStructureMap.get(i);
				Map<String, String> appendStruct = appendStructureMap.get(i);
				assert ! (inclStruct != null && appendStruct != null);
				// if this row denotes INCLUDE or APPEND structure 
				//   -> put it onto the stack and indicate its length as this position plus its length 
				if (inclStruct != null) {
					String currentIncludeStructure = inclStruct.get("PRECFIELD"); //$NON-NLS-1$
					int length = getStructureLength(currentIncludeStructure);
					push(currentStructures, new SAPStructure(currentIncludeStructure, i + length, true));
				} else {
					if (appendStruct != null) {
						String currentAppendStructure = appendStruct.get("PRECFIELD"); //$NON-NLS-1$
						int length = getStructureLength(currentAppendStructure);
						push(currentStructures,new SAPStructure(currentAppendStructure, i + length, false));
					}
				}

				// we are at an actual field -> annotation INCLUDE and APPEND structure
				if (row != null) {
					String allIncludes = getAllStructures(currentStructures, true);
					if (allIncludes != null) {
						row.put(com.ibm.is.sappack.gen.common.Constants.ANNOT_INCLUDE_STRUCTURE , allIncludes);
					}
					String allAppends = getAllStructures(currentStructures, false);
					if (allAppends != null) {
						row.put(com.ibm.is.sappack.gen.common.Constants.ANNOT_APPEND_STRUCTURE , allAppends);
					}
				}

				// pop all structures from stack that end here 
				SAPStructure topStruct = null;
				while ( (((topStruct = peek(currentStructures)) != null)) && (i == topStruct.end)) {
					pop(currentStructures);
				}
			}

			return rows;
		} catch (JCoException e) {
			Activator.getLogger().log(Level.SEVERE, Messages.LogExceptionMessage, e);
			e.printStackTrace();
			throw new RuntimeException(e);
		}

	}




	/**
	 * Calls the "JCO_FUNCTION_DDIF_FIELDINFO_GET" BAPI and returns the
	 * "DFIES_TAB" table
	 * 
	 * @param tableName
	 *            : The name of the table for which metadata will be extracted
	 * @return: The "DFIES_TAB" table containing information about the table's
	 *          fields like e.g. <br>
	 *          <ul>
	 *          <li>its datatype</li>
	 *          <li>length</li>
	 *          <li>SAP base datatype (e.g. C,D,N,...)</li>
	 *          <li>its description</li>
	 *          </ul>
	 * @throws JCoException
	 */
	private JCoTable queryTableMetadata(String tableName) throws JCoException {
		try {
			this.function_DDIF_FIELDINFO_GET.getImportParameterList().setValue(Constants.JCO_PARAMETER_TABNAME, tableName);
			this.function_DDIF_FIELDINFO_GET.execute(this.destination);

			JCoTable metadataTable = this.function_DDIF_FIELDINFO_GET.getTableParameterList().getTable(Constants.JCO_RESULTTABLE_DFIES_TAB);
		
			return metadataTable;
		} catch (JCoException e) {
			// System.out.println(e.getGroup());
			// System.out.println(e.getKey());
			if (e.getGroup() == JCoException.JCO_ERROR_ABAP_EXCEPTION && e.getKey().equalsIgnoreCase(Constants.JCO_ERROR_KEY_NOT_FOUND)) {
				if (this.errorMessage == null) {
					this.errorMessage = new StringBuffer();
				}
				this.errorMessage.append(MessageFormat.format(Messages.LogicalTable2LdmImporter_10, tableName));
				return null;
			}
			throw e;
		}
	}

	/**
	 * Calls the "JCO_FUNCTION_DD_TABL_GET" BAPI and returns the "DD05M_TAB_A"
	 * result table.
	 * 
	 * @param tableName
	 *            : The name of the table for which the check table information
	 *            will be extracted.
	 * @return: The "DD05M_TAB_A" result table containing information like: <br>
	 *          <ul>
	 *          <li>The name of the check table</li>
	 *          <li>The name of the foreign key field in the table and the
	 *          corresponding check field in the check table defining the
	 *          relation</li>
	 *          <li>The name of the relation</li>
	 *          <li>The domain, data type and length of the field</li>
	 *          </ul>
	 * @throws JCoException
	 */
	private JCoTable[] queryTableRelations(String tableName) throws JCoException {

		this.function_DD_TABL_GET.getImportParameterList().setValue(Constants.JCO_PARAMETER_TABL_NAME, tableName);
		this.function_DD_TABL_GET.getImportParameterList().setValue(Constants.JCO_PARAMETER_WITHTEXT, Constants.JCO_PARAMETER_VALUE_TRUE);

		this.function_DD_TABL_GET.execute(this.destination);

		// DD05M_TAB_A
		JCoTable relationInformation = this.function_DD_TABL_GET.getTableParameterList().getTable(Constants.JCO_RESULTTABLE_DD05_M_TAB_A);

		// DD08V_TAB_N
		JCoTable cardinalityInformation = this.function_DD_TABL_GET.getTableParameterList().getTable(Constants.JCO_RESULTTABLE_DD08_V_TAB_A);

		return new JCoTable[] { relationInformation, cardinalityInformation };
	}

	public String getErrorMessage() {
		return (this.errorMessage == null) ? null : this.errorMessage.toString();
	}

	private List<TechnicalField> getTechnicalFields() {
		List<TechnicalField> explicitTechFields = this.options.getExplicitTechnicalFields();
		if (explicitTechFields != null) {
			return explicitTechFields;
		}
		explicitTechFields = new ArrayList<TechnicalField>();
		if (this.options.isCreateTechnicalFields()) {
			String description = Messages.LogicalTable2LdmImporter_6;
			explicitTechFields.add(new TechnicalFieldVarchar("LEGACY_ID", description, 20, false, this.options.isTechnicalFieldsCanBeNullable())); //$NON-NLS-1$
			explicitTechFields.add(new TechnicalFieldVarchar("LEGACY_UK", description, 120, false, this.options.isTechnicalFieldsCanBeNullable())); //$NON-NLS-1$
			explicitTechFields.add(new TechnicalFieldVarchar("LOB", description, 10, false, this.options.isTechnicalFieldsCanBeNullable())); //$NON-NLS-1$
			explicitTechFields.add(new TechnicalFieldVarchar("RLOUT", description, 30, false, this.options.isTechnicalFieldsCanBeNullable())); //$NON-NLS-1$
			explicitTechFields.add(new TechnicalFieldVarchar("CATEGORY", description, 30, false, this.options.isTechnicalFieldsCanBeNullable())); //$NON-NLS-1$
			explicitTechFields.add(new TechnicalFieldInteger("VERSION", description, false, this.options.isTechnicalFieldsCanBeNullable())); //$NON-NLS-1$
			explicitTechFields.add(new TechnicalFieldInteger("LOAD_ID", description, false, this.options.isTechnicalFieldsCanBeNullable())); //$NON-NLS-1$
		}
		return explicitTechFields;
	}

	private void addTechnicalFields(LdmAccessor ldmAccessor, Entity entity) {

		List<TechnicalField> technicalFields = getTechnicalFields();
		for (TechnicalField tf : technicalFields) {
			
			Attribute attribute = ldmAccessor.addColumnMetadataToTable(entity, tf);
			if (attribute != null) {
				attribute.setLabel(attribute.getDescription());
				ldmAccessor.addAnnotation(attribute, com.ibm.is.sappack.gen.common.Constants.ANNOT_DATA_OBJECT_SOURCE, 
				                          com.ibm.is.sappack.gen.common.Constants.DATA_OBJECT_SOURCE_TYPE_TECH_FIELD);
				ldmAccessor.addAnnotation(attribute, com.ibm.is.sappack.gen.common.Constants.ANNOT_COLUMN_IS_UNICODE, 
				                          String.valueOf(this.sapSystem.isUnicode()));
			}

		}

		/*
		 * TechnicalFieldVarchar legacyIdField = new
		 * TechnicalFieldVarchar("LEGACY_ID", "technical field", 20, false,
		 * this.options.getAllowTechnicalFieldsToBeNullable()); Attribute
		 * attribute = ldmAccessor.addColumnMetadataToTable(entity,
		 * legacyIdField); if (attribute != null) {
		 * ldmAccessor.addAnnotation(attribute,
		 * com.ibm.is.sappack.gen.common.Constants.ANNOT_DATA_OBJECT_SOURCE,
		 * com.
		 * ibm.is.sappack.gen.common.Constants.DATA_OBJECT_SOURCE_TYPE_TECH_FIELD
		 * ); }
		 * 
		 * TechnicalFieldVarchar legacyUkField = new
		 * TechnicalFieldVarchar("LEGACY_UK", "technical field", 120, false,
		 * this.options.getAllowTechnicalFieldsToBeNullable()); attribute =
		 * ldmAccessor.addColumnMetadataToTable(entity, legacyUkField); if
		 * (attribute != null) { ldmAccessor.addAnnotation(attribute,
		 * com.ibm.is.sappack.gen.common.Constants.ANNOT_DATA_OBJECT_SOURCE,
		 * com.
		 * ibm.is.sappack.gen.common.Constants.DATA_OBJECT_SOURCE_TYPE_TECH_FIELD
		 * ); }
		 * 
		 * TechnicalFieldVarchar lobField = new TechnicalFieldVarchar("LOB",
		 * "technical field", 10, false,
		 * this.options.getAllowTechnicalFieldsToBeNullable()); attribute =
		 * ldmAccessor.addColumnMetadataToTable(entity, lobField); if (attribute
		 * != null) { ldmAccessor.addAnnotation(attribute,
		 * com.ibm.is.sappack.gen.common.Constants.ANNOT_DATA_OBJECT_SOURCE,
		 * com.
		 * ibm.is.sappack.gen.common.Constants.DATA_OBJECT_SOURCE_TYPE_TECH_FIELD
		 * ); }
		 * 
		 * TechnicalFieldVarchar rloutField = new TechnicalFieldVarchar("RLOUT",
		 * "technical field", 30, false,
		 * this.options.getAllowTechnicalFieldsToBeNullable()); attribute =
		 * ldmAccessor.addColumnMetadataToTable(entity, rloutField); if
		 * (attribute != null) { ldmAccessor.addAnnotation(attribute,
		 * com.ibm.is.sappack.gen.common.Constants.ANNOT_DATA_OBJECT_SOURCE,
		 * com.
		 * ibm.is.sappack.gen.common.Constants.DATA_OBJECT_SOURCE_TYPE_TECH_FIELD
		 * ); }
		 * 
		 * TechnicalFieldVarchar categoryField = new
		 * TechnicalFieldVarchar("CATEGORY", "technical field", 30, false,
		 * this.options.getAllowTechnicalFieldsToBeNullable()); attribute =
		 * ldmAccessor.addColumnMetadataToTable(entity, categoryField); if
		 * (attribute != null) { ldmAccessor.addAnnotation(attribute,
		 * com.ibm.is.sappack.gen.common.Constants.ANNOT_DATA_OBJECT_SOURCE,
		 * com.
		 * ibm.is.sappack.gen.common.Constants.DATA_OBJECT_SOURCE_TYPE_TECH_FIELD
		 * ); }
		 * 
		 * TechnicalFieldInteger versionField = new
		 * TechnicalFieldInteger("VERSION", "technical field", false,
		 * this.options.getAllowTechnicalFieldsToBeNullable()); attribute =
		 * ldmAccessor.addColumnMetadataToTable(entity, versionField); if
		 * (attribute != null) { ldmAccessor.addAnnotation(attribute,
		 * com.ibm.is.sappack.gen.common.Constants.ANNOT_DATA_OBJECT_SOURCE,
		 * com.
		 * ibm.is.sappack.gen.common.Constants.DATA_OBJECT_SOURCE_TYPE_TECH_FIELD
		 * ); }
		 * 
		 * TechnicalFieldInteger loadIdField = new
		 * TechnicalFieldInteger("LOAD_ID", "technical field", false,
		 * this.options.getAllowTechnicalFieldsToBeNullable()); attribute =
		 * ldmAccessor.addColumnMetadataToTable(entity, loadIdField); if
		 * (attribute != null) { ldmAccessor.addAnnotation(attribute,
		 * com.ibm.is.sappack.gen.common.Constants.ANNOT_DATA_OBJECT_SOURCE,
		 * com.
		 * ibm.is.sappack.gen.common.Constants.DATA_OBJECT_SOURCE_TYPE_TECH_FIELD
		 * ); }
		 * 
		 * // entityFields.put("LEGACY_ID", "VARCHAR"); //
		 * entityFields.put("LEGACY_UK", "VARCHAR"); // entityFields.put("LOB",
		 * "VARCHAR"); // entityFields.put("RLOUT", "VARCHAR"); //
		 * entityFields.put("CATEGORY", "VARCHAR"); //
		 * entityFields.put("VERSION", "INTEGER"); //
		 * entityFields.put("LOAD_ID", "INTEGER");
		 */
	}

	public static String createTechnicalPKName(String sapTableName) {
		String pkFieldName = MessageFormat.format(com.ibm.is.sappack.gen.common.Constants.PRIMARY_KEY_NAME_TEMPLATE, 
				new Object[] { sapTableName} );
		return pkFieldName;
	}
	
	private boolean createTechnicalFieldPrimaryKey(SapTable sapTable) {
		Entity checkTable = this.ldmAccessor.findEntity(this.rootPackage, sapTable.getName());
		if (checkTable == null) {
			// Checktable not found, as e.g for joined check and text tables
			return true;
		}
		String pkFieldName = createTechnicalPKName(sapTable.getName()); 

		Attribute attribute = this.ldmAccessor.findAttribute(checkTable, pkFieldName);
		if (attribute != null) {
			return true;
		}

		// determine if a PK is to be created
		boolean addPK;
		switch(this.options.getRelationMode()) {
		case CW_PKs_FKs:
		case CW_PKs_NoFKs:
			addPK = true;
			break;

		case NoPKs_NoFKs:
		case SAP_PKs_FKs:
		case SAP_PKs_NoFKs:
		case CW_SAP_PKs_FKs:
		default:
			addPK = false;
		}

		TechnicalFieldVarchar pkIdField = new TechnicalFieldVarchar(pkFieldName, Messages.LogicalTable2LdmImporter_11, 
				this.keyFieldLen, addPK, !addPK);
		Attribute pkIdAttribute = this.ldmAccessor.addColumnMetadataToTable(checkTable, pkIdField);
		if (pkIdAttribute != null) {
			this.ldmAccessor.addAnnotation(pkIdAttribute, com.ibm.is.sappack.gen.common.Constants.ANNOT_COLUMN_IS_UNICODE, 
					Boolean.toString(sapSystem.isUnicode()));
		}

		if (addPK) {
			this.ldmAccessor.addPrimaryKeyToAttribute(checkTable, pkIdAttribute);
		}

		return false;
	}


	private void createTechnicalFieldForeignKey(String checktableName, String fieldName, String tableName, 
			String foreignKeyName, boolean isForeignKey) {
		Entity checkTable = this.ldmAccessor.findEntity(this.rootPackage, tableName);
		if (checkTable == null) {
			String msg =  Messages.LogicalTable2LdmImporter_12;
			msg = MessageFormat.format(msg, tableName);
			Activator.getLogger().log(Level.FINE, msg);
			return;
		}

		// Check whether the foreign key already exists
		ForeignKey foreignKey = this.ldmAccessor.findForeignKey(checkTable, foreignKeyName);
		if (foreignKey != null) {
			return;
		}

		Attribute attribute = this.ldmAccessor.findAttribute(checkTable, foreignKeyName);
		if (attribute != null) {
			return;
		}

		TechnicalFieldVarchar fkIdField = new TechnicalFieldVarchar(foreignKeyName, Messages.LogicalTable2LdmImporter_13, 
				this.keyFieldLen, false, this.options.isMakeFKFieldsNullable());
		Attribute fkIdAttribute = this.ldmAccessor.addColumnMetadataToTable(checkTable, fkIdField);
		if (fkIdAttribute != null) {
			this.ldmAccessor.addAnnotation(fkIdAttribute, com.ibm.is.sappack.gen.common.Constants.ANNOT_COLUMN_IS_UNICODE, 
					Boolean.toString(sapSystem.isUnicode()));
		}

		if (isForeignKey) {
			foreignKey = this.ldmAccessor.getForeignKey(checkTable, foreignKeyName);
			this.ldmAccessor.addAttributeToKey(checkTable, foreignKey, foreignKeyName);
		}
	}

	public static List<Pair<String, String>> parseCTJoinCondition(String joinCondition) {
		List<Pair<String, String>> relations = new ArrayList<Pair<String,String>>();
		StringTokenizer tok = new StringTokenizer(joinCondition, ","); //$NON-NLS-1$
		while (tok.hasMoreTokens()) {
			String r = tok.nextToken();
			int ix = r.indexOf("="); //$NON-NLS-1$
			relations.add(new Pair<String, String>(r.substring(0, ix), r.substring(ix + 1))); 
		}
		return relations;
	}
	
	private void annotateCTJoinConditions(SapTableSet sapTables) throws JCoException {


		LdmNameConverter nameConverter = this.ldmAccessor.getNameConverter();
		// iterate over all SAP value tables and collect information about check tables
		// and set the join conditions

		Iterator<SapTable> sapTableIterator = sapTables.iterator();
		while (sapTableIterator.hasNext()) {
			SapTable sapTable  = sapTableIterator.next();
			String   tableName = sapTable.getName();

			if (!sapTable.existsOnSAPSystem()) {
				continue;
			}

			// do not handle check and text tables 
			if(!sapTable.isCheckTable() && !sapTable.isTextTable()) {

				// a map from the LDM field name to a pair of the join condition (a) with SAP Names, and (b) with LDM names 
				Map<String, Pair<StringBuffer, StringBuffer> > joinConditionMap = new HashMap<String, Pair<StringBuffer,StringBuffer>>();

				// Find sap table in logical data model
				Entity tableEntity = this.ldmAccessor.findEntity(tableName);

				// sap table fields in logical data model
				EList<Attribute> fieldList = tableEntity.getAttributes();

				// sanity check
				if (fieldList != null && !fieldList.isEmpty()) {
					Map<String, Attribute> ldmFieldMap = new HashMap<String, Attribute>();

					// build a map from the field list: key = field name
					Iterator<Attribute> fieldIterator = fieldList.iterator();
					while(fieldIterator.hasNext()) {
						Attribute field = fieldIterator.next();
						ldmFieldMap.put(field.getName(), field);
					} // end of while(fieldIterator.hasNext())

					// query SAP table DD05Q to get the check table and check fields of the SAP value table
					SAPTableExtractor dd05qExtractor = new SAPTableExtractor(this.destination, "DD05Q", //$NON-NLS-1$ 
							Arrays.asList(new String[] { "FORTABLE", "FIELDNAME", "FORKEY", "CHECKTABLE", "CHECKFIELD", "CHECKTABLE" }), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ 
							"TABNAME = '" + tableName + "'"); //$NON-NLS-1$ //$NON-NLS-2$ 
					SAPTableExtractor.Result dd05qResult = dd05qExtractor.performQuery();
					while (dd05qResult.nextRow()) {

						String tabName    = dd05qResult.getValue("TABNAME");    //$NON-NLS-1$
						String fieldName  = dd05qResult.getValue("FIELDNAME");  //$NON-NLS-1$
						String forTable   = dd05qResult.getValue("FORTABLE");   //$NON-NLS-1$
						String forKey     = dd05qResult.getValue("FORKEY");     //$NON-NLS-1$
						String checkTable = dd05qResult.getValue("CHECKTABLE"); //$NON-NLS-1$
						String checkField = dd05qResult.getValue("CHECKFIELD"); //$NON-NLS-1$

						// ignore internal references, such as T1.C1 references T1.C2
						if(forTable.equals(checkTable)) {
							continue;
						}

						String ldmFieldName = nameConverter.convertAttributeName(fieldName);
						// get the field attribute from field map and check if a CheckTable exists
						Attribute field = ldmFieldMap.get(ldmFieldName);
						if (field != null) {
							String relatedCheckTableName = LdmAccessor.getAnnotationValue(field, 
									com.ibm.is.sappack.gen.common.Constants.ANNOT_RELATED_CHECKTABLE);
							if (relatedCheckTableName != null) {
								Pair<StringBuffer, StringBuffer> joinCondPair = joinConditionMap.get(ldmFieldName);
								if (joinCondPair == null) {
									joinCondPair = new Pair<StringBuffer, StringBuffer>(new StringBuffer(), new StringBuffer());
									joinConditionMap.put(ldmFieldName, joinCondPair);
								}
								else {
									joinCondPair.getFirst().append(","); //$NON-NLS-1$
									joinCondPair.getSecond().append(","); //$NON-NLS-1$
								}

								String convertedForKey = null;
								// if there is a 'global' or 'constant' FORTABLE ...
								if (forTable.equals("*") || forTable.startsWith("'")) {   //$NON-NLS-1$ //$NON-NLS-2$
									// ==> use the FORTABLE as FORKEY
									forKey = forTable;
									convertedForKey = forKey;
								}
								else {
									convertedForKey = nameConverter.convertAttributeName(forKey);
									// if there is a different FORTABLE name...
									if (!forTable.equals(tabName)) {
										// ==> add it as a prefix
										joinCondPair.getFirst().append(forTable).append("."); //$NON-NLS-1$
										joinCondPair.getSecond().append(nameConverter.convertEntityName(forTable)).append("."); //$NON-NLS-1$
									}
								} // end of (else) if (forTable.equals("*") || forTable.startsWith("'"))

								joinCondPair.getFirst().append(forKey).append("=").append(checkField); //$NON-NLS-1$
								joinCondPair.getSecond().append(convertedForKey).append("=").append(nameConverter.convertAttributeName(checkField)); //$NON-NLS-1$
							} // end of if (relatedCheckTableName != null)
						} // end of if (field != null)
					} // end of while (dd05qResult.nextRow())

					for (Entry<String, Pair<StringBuffer, StringBuffer>> entry : joinConditionMap.entrySet()) {
						String    fieldName = entry.getKey();
						Attribute field     = ldmFieldMap.get(fieldName);

						String sapCondition = entry.getValue().getFirst().toString(); 
						String ldmCondition = entry.getValue().getSecond().toString(); 

						
					//	ldmAccessor.addAnnotation(field, com.ibm.is.sappack.gen.common.Constants.ANNOT_RELATED_CT_JOIN_SAP, sapCondition);
						ldmAccessor.addAnnotation(field, com.ibm.is.sappack.gen.common.Constants.ANNOT_RELATED_CT_JOIN, ldmCondition);
					}
					
				} // end of if (fieldList != null && !fieldList.isEmpty())

			} // end of if(!sapTable.isCheckTable() && !sapTable.isTextTable())
		} // end of while (sapTableIterator.hasNext())

	}

	@SuppressWarnings("nls")
	public static void main(String[] args) {
		String s = Integer.toString(Integer.MAX_VALUE, Character.MAX_RADIX);
		System.out.println("S: " + s);
	}


}
