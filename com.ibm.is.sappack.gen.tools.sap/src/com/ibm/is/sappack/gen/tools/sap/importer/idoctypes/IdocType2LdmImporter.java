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
// Module Name : com.ibm.is.sappack.gen.tools.sap.importer.idoctypes
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.tools.sap.importer.idoctypes;


import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;

import com.ibm.db.models.logical.Attribute;
import com.ibm.db.models.logical.Entity;
import com.ibm.db.models.logical.Package;
import com.ibm.iis.sappack.gen.common.ui.connections.SapSystem;
import com.ibm.is.sappack.gen.common.Constants;
import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.common.ui.ModeManager;
import com.ibm.is.sappack.gen.common.util.StringUtils;
import com.ibm.is.sappack.gen.tools.sap.activator.Activator;
import com.ibm.is.sappack.gen.tools.sap.importer.LogicalTable2LdmImporter;
import com.ibm.is.sappack.gen.tools.sap.importer.helper.configuration.ImporterOptionsBase.CHECKTABLEOPTIONS;
import com.ibm.is.sappack.gen.tools.sap.importer.helper.configuration.ImporterOptionsBase.RELATIONS;
import com.ibm.is.sappack.gen.tools.sap.importer.helper.configuration.TableImporterOptions;
import com.ibm.is.sappack.gen.tools.sap.importer.helper.field.IDocField;
import com.ibm.is.sappack.gen.tools.sap.importer.helper.field.TechnicalField;
import com.ibm.is.sappack.gen.tools.sap.importer.helper.field.TechnicalFieldInteger;
import com.ibm.is.sappack.gen.tools.sap.importer.helper.field.TechnicalFieldVarchar;
import com.ibm.is.sappack.gen.tools.sap.importer.helper.model.LdmAccessor;
import com.ibm.is.sappack.gen.tools.sap.model.SapTableSet;
import com.ibm.is.sappack.gen.tools.sap.utilities.JoinedCheckAndTextTableCleaner;
import com.ibm.is.sappack.gen.tools.sap.utilities.ReferenceTableImporter;
import com.ibm.is.sappack.gen.tools.sap.utilities.TranslationTableImporter;
import com.sap.conn.jco.JCoException;


public class IdocType2LdmImporter { //implements IRunnableWithProgress {

	private SapSystem             sapSystem;
	private IDocType              selectedIDocType;
	private List<String>          selectedIDocTypeSegments;
	private Package                segmentTablesPackage;
	private Package                checkTablesPackage;

	private LdmAccessor           ldmAccessor;
	//	private Package               rootPackage = null;
	private List<TechnicalField>  explicitTechnicalFields = null;
	private SapTableSet           valueTableSet;
	private boolean               isV7StageGeneration;
	private boolean               isExtensionEnabled;
	private LdmAccessor checkTableAccessor;

	private IDocImporterOptions options = null;


	static String copyright() {
		return com.ibm.is.sappack.gen.tools.sap.importer.idoctypes.Copyright.IBM_COPYRIGHT_SHORT;
	}

	public IdocType2LdmImporter(SapSystem sapSystem, IDocType selectedIDocType, 
			List<String> selectedIDocTypeSegments, IDocImporterOptions options,
			LdmAccessor ldmAccessor, Package segmentTablePackage,
			LdmAccessor checkTableLDMAccessor, Package checkTablePackage
			// Package rootPackage
	) throws JCoException {

		this.ldmAccessor               = ldmAccessor;
		this.sapSystem                 = sapSystem;
		this.selectedIDocType          = selectedIDocType;
		this.selectedIDocTypeSegments  = selectedIDocTypeSegments;
		//		this.valueTables               = new ArrayList<String>();
		this.segmentTablesPackage  = segmentTablePackage;
		this.checkTablesPackage    = checkTablePackage;
		//	this.rootPackage               = rootPackage;
		this.isV7StageGeneration       = ModeManager.generateV7Stages();
		this.isExtensionEnabled        = ModeManager.isCWEnabled();

		this.options = options;

		this.checkTableAccessor = checkTableLDMAccessor;
		
		if (this.checkTableAccessor == null || checkTablePackage == null) {
			this.checkTableAccessor = ldmAccessor;
			this.checkTablesPackage = segmentTablePackage;
		}
		
	}


	public void runImport(IProgressMonitor progressMonitor) throws JCoException {
		importMetadata(progressMonitor);
	}

	private void importMetadata(IProgressMonitor progressMonitor) throws JCoException  {

		// initialize progress monitor
		// we use the number of selected segments and (+) number of distinct
		// check tables
		int numberOfSteps = selectedIDocTypeSegments.size() + this.valueTableSet.size();

		progressMonitor.beginTask(Messages.LogicalTable2LdmImporter_0, numberOfSteps);

		// Iterate over all segments and add the metadata to the Logical Data
		// Model

		/*
		Package segmentTablesPackage = null;

		if (null != this.segmentTablesPackage && null == this.rootPackage) {
			segmentTablesPackage = this.segmentTablesPackage;
		} 
		else if (null != this.rootPackage) {
			segmentTablesPackage = ldmAccessor.getPackageFor(this.rootPackage, segmentTablesPackageName);
		}
		 */

		Iterator<String> iterator = this.selectedIDocTypeSegments.iterator();
		while (iterator.hasNext() && !progressMonitor.isCanceled()) {
			String segmentName = iterator.next();
			Segment segment = selectedIDocType.getSegment(segmentName);
			progressMonitor.subTask(MessageFormat.format(Messages.LogicalTable2LdmImporter_1, segmentName));

			Map<String, String> entityFields = new HashMap<String, String>();

			Entity entity = ldmAccessor.findEntity(segmentTablesPackage, segment);
			if (entity == null) {
				entity = ldmAccessor.createEntity(segmentTablesPackage, segment);
			}

			if (ModeManager.generateV7Stages()) {
				// add technical fields and segment fields 
				addSegmentAdminFieldsToModel(ldmAccessor, entity, segment, entityFields);
				addSegmentMetadataToModel(ldmAccessor, entity, segmentTablesPackage, segment, entityFields);
			}
			else {
				// add segment fields and technical fields
				addSegmentMetadataToModel(ldmAccessor, entity, segmentTablesPackage, segment, entityFields);
				addSegmentAdminFieldsToModel(ldmAccessor, entity, segment, entityFields);
			}

			// if (createAdditionalFields) {
			addSegmentAdditionalFields(ldmAccessor, entity, segment, entityFields);
			// }
			// entity annotations
			addSegmentAnnotations(ldmAccessor, entity, segment, entityFields);

			progressMonitor.worked(1);
		}

		// import (non-joined) check and text tables
		if (this.valueTableSet != null && !options.isAddSegmentCheckTables()&& ! options.isCreateTranslationTables()) {
			if (this.valueTableSet.size() > 0) {

				/*
				 * reuse the LogicalTable2LdmImporter - but do not start an
				 * additional Thread
				 */
				TableImporterOptions options = new TableImporterOptions();
				// check tables are already included in valueTables
				options.setCheckTableOption(CHECKTABLEOPTIONS.NO_CHECKTABLES);

				/*
				Package checkTablesPackage = null;

				if (this.rootPackage == null) {
					checkTablesPackage = ldmAccessor.getPackage(checkTablesPackageName);
				} else {
					checkTablesPackage = ldmAccessor.getPackageFor(this.rootPackage, checkTablesPackageName);
				}
				 */

				// set 'explicit technical fields for check and text tables too !!!
				options.setExplicitTechnicalFields(explicitTechnicalFields);

				LogicalTable2LdmImporter tableImporter = new LogicalTable2LdmImporter(sapSystem, this.valueTableSet, options, checkTableAccessor, this.checkTablesPackage, null, null, true);

				// if (null != this.segmentTablesPackageName && null ==
				// this.rootPackage) {
				// tableImporter = new LogicalTable2LdmImporter(ldmAccessor,
				// sapSystem,
				// this.valueTableSet, options);
				// } else if (null != this.rootPackage) {
				// tableImporter = new LogicalTable2LdmImporter(ldmAccessor,
				// sapSystem,
				// this.valueTableSet, options, this.rootPackage);
				// }

				progressMonitor.subTask(Messages.SapLogicalTableCollector_4);
				tableImporter.runImport(new SubProgressMonitor(progressMonitor, this.valueTableSet.size()));
			}

		}

		// import checktables
		if (!progressMonitor.isCanceled()) {
			boolean jltOrTT = options.isAddSegmentCheckTables() || options.isCreateTranslationTables();

			if (jltOrTT) {
				progressMonitor.subTask(Messages.IdocType2LdmImporter_0);
				/*
				Package checkTablesPackage = null;

				if (null != this.checkTablesPackageName && null == this.rootPackage) {
					checkTablesPackage = ldmAccessor.getPackage(checkTablesPackageName);
				} 
				else 
				{
				   if (null != this.rootPackage) {
				      checkTablesPackage = ldmAccessor.getPackageFor(this.rootPackage, checkTablesPackageName);
				   }
				}
				 */

				if (options.isAddSegmentCheckTables()) {
					/*
					JoinedCheckAndTextTableImporter joinedCheckAndTextTableImporter = new JoinedCheckAndTextTableImporter(this.sapSystem, ldmAccessor, checkTablesPackage,
							options.getAbapTransferMethod());
					joinedCheckAndTextTableImporter.importJoinedCheckAndTextTable(valueTables, this.selectedIDocType, progressMonitor);
					 */
					ReferenceTableImporter importer = new ReferenceTableImporter(this.sapSystem, checkTableAccessor, checkTablesPackage,
							options.getAbapTransferMethod());
					importer.importReducedCheckAndTextTableTables(valueTableSet, this.selectedIDocType, progressMonitor);
				}

				if (options.isCreateTranslationTables()) {
					TranslationTableImporter translationTableImporter = new TranslationTableImporter(this.sapSystem, checkTableAccessor, checkTablesPackage, ldmAccessor);
					translationTableImporter.createTranslationTables(valueTableSet, this.selectedIDocType, progressMonitor);
					translationTableImporter.annotateTranslationTableReferences(this.valueTableSet);
				}

				// TODO create FK relations for the checktables                  
				switch(this.options.getRelationMode()) {
				case CW_PKs_FKs:
					break;

				default:
					; // nothing to do
				} // end of switch(this.relationMode)
			} // end of if (jltOrTT)
		} // end of if (!progressMonitor.isCanceled())

		// by snelke 		Hier geht's weiter!
		// If the option "create joined check and text tables" is selected, we
		// will add the annotations on each column that points to a JLT to which
		// column of the JLT this column points
		if (this.options.getCreateCorrectMIHModel()) {
			JoinedCheckAndTextTableCleaner cleaner = new JoinedCheckAndTextTableCleaner(this.ldmAccessor);
			cleaner.setJoinedCheckAndTextTableColumnAnnotations();
			cleaner.deleteUnrelatedJoinedCheckAndTextTables();
		}

	}


	private void addSegmentAnnotations(LdmAccessor ldmAccessor, Entity entity, Segment segment, Map<String, String> entityFields) throws JCoException {
		boolean isExtendedType = segment.getIDocType().isExtendedIDocType();

		ldmAccessor.addAnnotation(entity, Constants.ANNOT_LOGICAL_TBL_NAME, segment.getIDocType().getName() + 
				Constants.LDM_ENTITY_NAME_SEPARATOR + segment.getDefinition());
		ldmAccessor.addAnnotation(entity, Constants.ANNOT_PHYSICAL_TBL_NAME, entity.getName());
		ldmAccessor.addAnnotation(entity, Constants.ANNOT_IDOC_TYPE, segment.getIDocType().getName());
		ldmAccessor.addAnnotation(entity, Constants.ANNOT_IDOC_RELEASE, segment.getIDocType().getRelease());
		ldmAccessor.addAnnotation(entity, Constants.ANNOT_IS_EXTENDED_IDOC_TYPE, String.valueOf(isExtendedType));
		if (isExtendedType) {
			ldmAccessor.addAnnotation(entity, Constants.ANNOT_IDOC_BASIC_TYPE, segment.getIDocType().getBasicType());
		}
		ldmAccessor.addAnnotation(entity, Constants.ANNOT_SEGMENT_TYPE, segment.getType());
		ldmAccessor.addAnnotation(entity, Constants.ANNOT_SEGMENT_DEFINITION, segment.getDefinition());
		ldmAccessor.addAnnotation(entity, Constants.ANNOT_TABLE_IS_MANDATORY, Boolean.valueOf(segment.mandatory).toString());
		ldmAccessor.addAnnotation(entity, Constants.ANNOT_DATA_OBJECT_SOURCE, Constants.DATA_OBJECT_SOURCE_TYPE_IDOC);
		ldmAccessor.addAnnotation(entity, Constants.ANNOT_IS_ROOT_SEGMENT, Boolean.valueOf(segment.getParentSegment() == null).toString());
		ldmAccessor.addAnnotation(entity, Constants.ANNOT_VARCHARS_LENGTH_FACTOR, String.valueOf(options.getVarcharLengthFactor()));

		long minOccurence;
		long maxOccurence;

		// Check, whether it's a group segment
		if (segment.isParentFlag()) {
			minOccurence = segment.getGroupMinOccurence();
			maxOccurence = segment.getGroupMaxOccurence();
		}
		// Or a normal segment
		else {
			minOccurence = segment.getMinOccurrence();
			maxOccurence = segment.getMaxOccurrence();
		}
		ldmAccessor.addAnnotation(entity, Constants.ANNOT_SEG_OCCURRENCE_MIN, Long.toString(minOccurence));
		ldmAccessor.addAnnotation(entity, Constants.ANNOT_SEG_OCCURRENCE_MAX, Long.toString(maxOccurence));

		ldmAccessor.addAnnotation(entity, Constants.ANNOT_GENERATED_MODEL_VERSION, Constants.MODEL_VERSION);

		if (segment.getParentSegment() != null) {
			// 45553, osuhre: don't use the table name as parent segment name because it contains the IDoc type name
			ldmAccessor.addAnnotation(entity, Constants.ANNOT_PARENT_SEG, segment.getParentSegment().getDefinition());
		}

		if (options.isUseVarcharTypeOnly()) {
			ldmAccessor.addAnnotation(entity, Constants.ANNOT_ATTRIBUTE_TYPE_SOURCE, Constants.ANNOT_ATTRIBUTE_TYPE_SOURCE_VARCHAR);
		} else {
			ldmAccessor.addAnnotation(entity, Constants.ANNOT_ATTRIBUTE_TYPE_SOURCE, Constants.ANNOT_ATTRIBUTE_TYPE_SOURCE_IDOCTYPE);
		}

		if (options.isCreateIDocExtractModel() && options.isCreateIDocLoadModel()) {
			ldmAccessor.addAnnotation(entity, com.ibm.is.sappack.gen.common.Constants.ANNOT_MODEL_PURPOSE, com.ibm.is.sappack.gen.common.Constants.MODEL_PURPOSE_IDOC_ALL);
		}
		else {
			if (options.isCreateIDocExtractModel()) {
				ldmAccessor.addAnnotation(entity, com.ibm.is.sappack.gen.common.Constants.ANNOT_MODEL_PURPOSE, com.ibm.is.sappack.gen.common.Constants.MODEL_PURPOSE_IDOC_EXTRACT);
			} else if (options.isCreateIDocLoadModel()) {
				ldmAccessor.addAnnotation(entity, com.ibm.is.sappack.gen.common.Constants.ANNOT_MODEL_PURPOSE, com.ibm.is.sappack.gen.common.Constants.MODEL_PURPOSE_IDOC_LOAD);
			}
		}

		MessageType[] messageTypes = segment.getIDocType().getMessageTypes().toArray(new MessageType[segment.getIDocType().getMessageTypes().size()]);
		Arrays.sort(messageTypes, new Comparator() {
			public int compare(Object o1, Object o2) {
				MessageType mt1 = (MessageType) o1;
				MessageType mt2 = (MessageType) o2;

				return mt1.getName().compareTo(mt2.getName());
			}
		});

		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < messageTypes.length; i++) {
			buffer.append(messageTypes[i].getName().trim());
			if ((i + 1) < messageTypes.length) {
				buffer.append(',');
			}
		}

		ldmAccessor.addAnnotation(entity, Constants.ANNOT_SAP_MESSAGE_TYPES, buffer.toString());

		Boolean hasAdditionalTechnicalFields = Boolean.valueOf(options. isCreateTechnicalFields());
		ldmAccessor.addAnnotation(entity, Constants.ANNOT_HAS_ADDITIONAL_TECHNICAL_FIELDS, hasAdditionalTechnicalFields.toString());
	}

	/**
	 * Adds the metadata of a segment to the model
	 * 
	 * @param segmentPackage
	 * 
	 * @param valueTables
	 * 
	 * @param tableMetadata
	 *            : The JCo table containing the table's metadata (the
	 *            "DFIES_TAB" result table of the
	 *            "JCO_FUNCTION_DDIF_FIELDINFO_GET" BAPI)
	 * @throws IDocTypeExtractException
	 */
	private Entity addSegmentMetadataToModel(LdmAccessor ldmAccessor, Entity entity, Package segmentPackage, Segment segment, Map<String, String> entityFields) throws JCoException {

		// Iterate over all columns for this table and add their metadata to the
		// table

		Iterator<IDocField> fields = segment.getFields().iterator();
		while (fields.hasNext()) {
			IDocField field = fields.next();
			/*
			if (this.useVarcharTypeOnly) {
				field.setVarcharLengthFactor(this.varCharLengthFactor);
			}
			 */

			// if the segment fields are supposed to be nullable
			// we have to set the flag for the field at this point
			field.setNullable(options.isSegmentFieldsNullable());

			// String attributeName = field.getName();
			// boolean isKeyColumn = false;
			// String columnDescription = field.getDescription();
			// String columnLabel = field.getName();
			// String sapDataTypeName = field.getDatatype();
			// int length = field.getExtLength();
			// int decimals = 0;
			//
			// // SapDataType sapDataType;
			// if (this.useVarcharTypeOnly) {
			// // System.out.println("varchars only sap data type: " +
			// // sapDataType);
			// // dataType =
			// // Sap2LdmDataTypeMapper.mapDataTypeToVarchar(sapDataType,
			// // length, decimals, this.varCharLengthFactor);
			// sapDataType = new SapDataType(sapDataTypeName, 'c', length,
			// length, decimals);
			// } else {
			// // System.out.println("non varchar only sap data type: "
			// // +sapDataType);
			// sapDataType = new SapDataType(sapDataTypeName, 'c', length,
			// length, decimals);
			// // dataType =
			// // Sap2LdmDataTypeMapper.mapDataTypeIdocType(ddmAccessor,
			// // segmentPackage, sapDataType, length, decimals,
			// // this.varCharLengthFactor, true);
			// }

			// Already done in the field!
			// we replace all / with _ in the column name
			// String cleanedAttributeName = attributeName.replace('/', '_');
			// if (cleanedAttributeName.startsWith("_")) {
			// cleanedAttributeName = cleanedAttributeName.substring(1);
			// }

			// IDocField idocField = new
			// IDocField(cleanedAttributeName,columnDescription,columnLabel,dataElementName,domainName,dataTypeName,sapBaseDataType,length,intLength,isKeyColumn,Configuration.getIDocSegmentFieldsNullable());
			// Attribute attribute =
			// ldmAccessor.addColumnMetadataToTable(entity,
			// cleanedAttributeName, isKeyColumn, columnDescription,
			// columnLabel, sapDataType,
			// Configuration.getIDocSegmentFieldsNullable());
			Attribute attribute = ldmAccessor.addColumnMetadataToTable(entity, field);
			if (attribute != null) {
				// attribute has been created
				String segmentFieldsDefaultValue = this.options.getSegmentFieldsDefaultValue();
				if ((segmentFieldsDefaultValue != null) && !segmentFieldsDefaultValue.trim().isEmpty()) {
					attribute.setDefaultValue(segmentFieldsDefaultValue); // not
					// yet
					// implemented
					// on
					// LDMAccessor.addColumnMetadataToTable
				}

				// We use the "getFieldName" here, not the "getCleanedFieldName"
				// because the original field name is needed for the derivation
				addFieldAnnotations(ldmAccessor, attribute, field.getFieldName());
			} else {
				// because the attribute already existed,
				// addColumnMetadataToTable returned null
				// however, wee need this column in the entityFields map in
				// order to create complete SQL
				String fieldName = field.getFieldName();
				attribute = ldmAccessor.findAttribute(entity, fieldName);
			}

			entityFields.put(attribute.getName(), attribute.getDataType());
		}

		return entity;
	}

	private void addSegmentAdminFieldsToModel(LdmAccessor ldmAccessor, Entity entity, Segment segment, 
			Map<String, String> entityFields) {

		Segment parentSegment = segment.getParentSegment();

		int docNumLen = Constants.IDOC_TECH_FLD_ADM_DOCNUM_LEN;
		int segNumLen = Constants.IDOC_TECH_FLD_ADM_SEGNUM_LEN;
		int psgNumLen = Constants.IDOC_TECH_FLD_ADM_PSGNUM_LEN;

		if (this.isV7StageGeneration) {
			docNumLen = Constants.IDOC_TECH_FLD_ADM_DOCNUM_LEN_V7;
			segNumLen = Constants.IDOC_TECH_FLD_ADM_SEGNUM_LEN_V7;
			psgNumLen = Constants.IDOC_TECH_FLD_ADM_PSGNUM_LEN_V7;
		}

		boolean isKeyField = this.isV7StageGeneration;
		RELATIONS idocRelationMode = this.options.getRelationMode();
		// Extension settings, for example CW, can overwrite 'isKeyField' setting 
		if (this.isExtensionEnabled) {
			isKeyField = idocRelationMode == RELATIONS.CW_PKs_FKs ||
			idocRelationMode == RELATIONS.CW_PKs_NoFKs;
		}
		boolean doCreateForeignKey = idocRelationMode == RELATIONS.CW_PKs_FKs;

		boolean segmentFieldsNullable = this.options.isSegmentFieldsNullable();
		// for primary key fields, the PK column value must be required
		boolean requirePKValue = !segmentFieldsNullable;
		if (!requirePKValue && isKeyField) {
			requirePKValue = true;
		}

		// for enforced foreign relation, the FK column value must be required
		boolean enforceForeignKey = this.options.isEnforceForeignKey();
		boolean requireFKValue = !this.options.isMakeFKFieldsNullable();

		boolean createIDocExtractModel = this.options.isCreateIDocExtractModel();
		if (createIDocExtractModel || isV7StageGeneration) {
			boolean isADMDOCNUMKey = isKeyField;
			boolean isADMDOCNUMNullable = !requirePKValue;
			
			boolean isADMSEGNUMKey = isKeyField;
			boolean isADMSEGNUMNullable = !requirePKValue;
			
			boolean isADMPSGNUMKey = false;
			boolean isADMPSGNUMNullable = !requireFKValue;
			
			if (this.isExtensionEnabled) {
				if (idocRelationMode == RELATIONS.CW_SAP_PKs_FKs || idocRelationMode == RELATIONS.NoPKs_NoFKs) {
					// staging
					isADMDOCNUMKey = true;
					isADMDOCNUMNullable = false;
					isADMSEGNUMKey = true;
					isADMSEGNUMNullable = false;
					isADMPSGNUMKey = false;
					isADMPSGNUMNullable = false;
				} else if (idocRelationMode == RELATIONS.CW_PKs_NoFKs) {
					// alignment
					isADMDOCNUMKey = true;
					isADMDOCNUMNullable = false;
					isADMSEGNUMKey = true;
					isADMSEGNUMNullable = false;
					isADMPSGNUMKey = false;
					isADMPSGNUMNullable = true;
				} else if (idocRelationMode == RELATIONS.CW_PKs_FKs) {
					// preload
					isADMDOCNUMKey = true;
					isADMDOCNUMNullable = false;
					isADMSEGNUMKey = true;
					isADMSEGNUMNullable = false;
					isADMPSGNUMKey = false;
					isADMPSGNUMNullable = false;					
				}
			}
			
			// control records have the ADM_DOCNUM field only for extract
			if (segment instanceof ControlRecord) {
				TechnicalFieldVarchar admDocnumField = new TechnicalFieldVarchar(Constants.IDOC_TECH_FLD_ADM_DOCNUM_NAME, 
						Messages.IdocType2LdmImporter_1, 
						docNumLen, 
						isADMDOCNUMKey, isADMDOCNUMNullable);
				Attribute admDocnumAttribute = ldmAccessor.addColumnMetadataToTable(entity, admDocnumField);

				addFieldAnnotations(ldmAccessor, admDocnumAttribute, Constants.IDOC_TECH_FLD_ADM_DOCNUM_ANNOT);
			}
			else {
				// Attribute admDocnumAttribute =
					// ldmAccessor.addColumnMetadataToTable(entity, "ADM_DOCNUM",
							// false,
							// "IDoc number", "ADM_DOCNUM", new
							// IDocDataTypeVarchar(ldmAccessor,
									// entity.getPackage(), "VARCHAR", "VARCHAR", 32, 0));
				// Attribute admSegnumAttribute =
				// ldmAccessor.addColumnMetadataToTable(entity, "ADM_SEGNUM",
				// false,
				// "Segment Number", "ADM_SEGNUM", new
				// IDocDataTypeVarchar(ldmAccessor, entity.getPackage(),
				// "VARCHAR",
				// "VARCHAR", 12, 0));
				// Attribute admPsgnumAttribute =
				// ldmAccessor.addColumnMetadataToTable(entity, "ADM_PSGNUM",
				// false,
				// "Number of superior parent segment", "ADM_PSGNUM", new
				// IDocDataTypeVarchar(ldmAccessor, entity.getPackage(),
				// "VARCHAR",
				// "VARCHAR", 12, 0));

				// SapDataType sapDataType = new SapDataType("VARC", 'C', 16,
				// 16,
				// 0);

				TechnicalFieldVarchar admDocnumField = new TechnicalFieldVarchar(Constants.IDOC_TECH_FLD_ADM_DOCNUM_NAME, 
						Messages.IdocType2LdmImporter_1, 
						docNumLen, 
						isADMDOCNUMKey, isADMDOCNUMNullable);
				Attribute admDocnumAttribute = ldmAccessor.addColumnMetadataToTable(entity, admDocnumField);

				// Attribute admDocnumAttribute =
				// ldmAccessor.addColumnMetadataToTable(entity, "ADM_DOCNUM",
				// false,
				// "IDoc number", "ADM_DOCNUM", sapDataType,
				// segmentFieldsNullable);
				// sapDataType = new SapDataType("VARC", 'C', 6, 6, 0);

				TechnicalFieldVarchar admSegnumField = new TechnicalFieldVarchar(Constants.IDOC_TECH_FLD_ADM_SEGNUM_NAME, 
						Messages.IdocType2LdmImporter_2, 
						segNumLen, 
						isADMSEGNUMKey, isADMSEGNUMNullable);
				Attribute admSegnumAttribute = ldmAccessor.addColumnMetadataToTable(entity, admSegnumField);
				// Attribute admSegnumAttribute =
				// ldmAccessor.addColumnMetadataToTable(entity, "ADM_SEGNUM",
				// false,
				// "Segment Number", "ADM_SEGNUM", sapDataType,
				// segmentFieldsNullable);

				TechnicalFieldVarchar admPsgnumField = new TechnicalFieldVarchar(Constants.IDOC_TECH_FLD_ADM_PSGNUM_NAME, 
						Messages.IdocType2LdmImporter_3, 
						psgNumLen, 
						isADMPSGNUMKey, isADMPSGNUMNullable);
				Attribute admPsgnumAttribute = ldmAccessor.addColumnMetadataToTable(entity, admPsgnumField);
				// Attribute admPsgnumAttribute =
				// ldmAccessor.addColumnMetadataToTable(entity, "ADM_PSGNUM",
				// false,
				// "Number of superior parent segment", "ADM_PSGNUM",
				// sapDataType,
				// segmentFieldsNullable);

				entityFields.put(Constants.IDOC_TECH_FLD_ADM_DOCNUM_NAME, Constants.IDOC_TECH_FLD_DEFAULT_TYPE);
				entityFields.put(Constants.IDOC_TECH_FLD_ADM_SEGNUM_NAME, Constants.IDOC_TECH_FLD_DEFAULT_TYPE);
				entityFields.put(Constants.IDOC_TECH_FLD_ADM_PSGNUM_NAME, Constants.IDOC_TECH_FLD_DEFAULT_TYPE);

				addFieldAnnotations(ldmAccessor, admDocnumAttribute, Constants.IDOC_TECH_FLD_ADM_DOCNUM_ANNOT);
				addFieldAnnotations(ldmAccessor, admSegnumAttribute, Constants.IDOC_TECH_FLD_ADM_SEGNUM_ANNOT);
				addFieldAnnotations(ldmAccessor, admPsgnumAttribute, Constants.IDOC_TECH_FLD_ADM_PSGNUM_ANNOT);

				// create foreign key constraints (child segments only)
				if (doCreateForeignKey) {
					if (parentSegment != null) {
						Attribute foreignKeyAttr[] = new Attribute[2];

						foreignKeyAttr[0] = admDocnumAttribute; 
						foreignKeyAttr[1] = admPsgnumAttribute;

						// build the hashcode for the FK suffix (n "columns" + "table name")
						StringBuffer fkSuffixBuf = new StringBuffer();
						fkSuffixBuf.append(segment.getTableName());
						for(int arrIdx = 0; arrIdx < foreignKeyAttr.length; arrIdx ++) {
							if (foreignKeyAttr[arrIdx] != null) {
								fkSuffixBuf.append("_");        //$NON-NLS-1$
								fkSuffixBuf.append(foreignKeyAttr[arrIdx].getName());
							}
						}
						String fkSuffixString = String.valueOf(fkSuffixBuf.hashCode());

						// ==> add the relationship (we have to filter unwanted characters from the names of
						//                           segments and key attributes for further processing)
						String cleanedTableName              = this.ldmAccessor.getNameConverter().convertEntityName(segment.getTableName());
						String cleanedParentSegmentTableName = this.ldmAccessor.getNameConverter().convertEntityName(parentSegment.getTableName());
						String foreignKeyName                = MessageFormat.format(com.ibm.is.sappack.gen.common.Constants.RELATIONSHIP_NAME_TEMPLATE, 
                                                                              new Object[] { parentSegment.getIDocType().getName(), 
                                                                                             parentSegment.getDefinition() } );
						String relationShipName              = MessageFormat.format(com.ibm.is.sappack.gen.common.Constants.RELATIONSHIP_NAME_TEMPLATE, 
						                                                            new Object[] { parentSegment.getIDocType().getName(), 
						                                                                           parentSegment.getDefinition(),
						                                                                           fkSuffixString } );
						
						for(int arrIdx = 0; arrIdx < foreignKeyAttr.length; arrIdx ++) {
							if (foreignKeyAttr[arrIdx] != null) {
								ldmAccessor.createRelationship(relationShipName, entity.getPackage(), cleanedTableName,
								                               foreignKeyName, cleanedParentSegmentTableName,
								                               null, foreignKeyAttr[arrIdx].getName(),
								                               cleanedParentSegmentTableName,
								                               enforceForeignKey, null);
							}
						}

						String cardinality = getCardinality(segment);
						ldmAccessor.createCardinality(relationShipName, entity.getPackage(), cleanedTableName, 
						                              cleanedParentSegmentTableName, 
						                              LdmAccessor.FOREIGN_KEY, cardinality, "1");  //$NON-NLS-1$
					} // end of if (parentSegment != null)
				} // end of if (doCreateForeignKeys)
			} // end of (else) if (segment instanceof ControlRecord)
		} // end of if (createIDocExtractModel || isV7StageGeneration) 

		boolean createIDocLoadModel = this.options.isCreateIDocLoadModel();
		if (createIDocLoadModel && !isV7StageGeneration) {
			if (segment instanceof ControlRecord) {
				String segmentPKeyAttribute = MessageFormat.format(Constants.IDOC_PRIMARY_KEY_ROOT_TEMPLATE, 
				                                                   new Object[] { segment.getIDocType().getName() });
				TechnicalFieldVarchar pKeyField = new TechnicalFieldVarchar(segmentPKeyAttribute, Messages.IdocType2LdmImporter_4, 
				                                                            30, isKeyField, !requirePKValue);
				Attribute pKeyAttribute = ldmAccessor.addColumnMetadataToTable(entity, pKeyField);
				entityFields.put(segmentPKeyAttribute, Constants.IDOC_TECH_FLD_DEFAULT_TYPE);
				addFieldAnnotations(ldmAccessor, pKeyAttribute, segmentPKeyAttribute);
			} 
			else {

				// create primary key
				String segmentPKeyAttribute = MessageFormat.format(Constants.IDOC_PRIMARY_KEY_TEMPLATE, 
				                                                   new Object[] { segment.getDefinition() });
				TechnicalFieldVarchar pKeyField = new TechnicalFieldVarchar(segmentPKeyAttribute, Messages.IdocType2LdmImporter_5, 
				                                                            30, isKeyField, !requirePKValue);
				Attribute pKeyAttribute = ldmAccessor.addColumnMetadataToTable(entity, pKeyField);
				entityFields.put(segmentPKeyAttribute, Constants.IDOC_TECH_FLD_DEFAULT_TYPE);
				addFieldAnnotations(ldmAccessor, pKeyAttribute, segmentPKeyAttribute);

				String segmentFKeyAttribute = MessageFormat.format(Constants.IDOC_FOREIGN_KEY_ROOT_TEMPLATE, 
				                                                   new Object[] { segment.getIDocType().getName() });

				if (parentSegment != null) {
					segmentFKeyAttribute = MessageFormat.format(Constants.IDOC_FOREIGN_KEY_SUB_TEMPLATE, 
					                                            new Object[] { parentSegment.getDefinition() });
				}

				TechnicalFieldVarchar fKeyField = new TechnicalFieldVarchar(segmentFKeyAttribute, Messages.IdocType2LdmImporter_6, 
				                                                            30, false, !requireFKValue);
				Attribute fKeyAttribute = ldmAccessor.addColumnMetadataToTable(entity, fKeyField);

				entityFields.put(segmentFKeyAttribute, Constants.IDOC_TECH_FLD_DEFAULT_TYPE);
				addFieldAnnotations(ldmAccessor, fKeyAttribute, segmentFKeyAttribute);

				// create foreign key relations (for child segments only
				if (doCreateForeignKey) {
					if (parentSegment != null) {

						// we have to filter unwanted characters from the names of
						// segments and key attributes for further processing
						String cleanedParentSegmentTableName = this.ldmAccessor.getNameConverter().convertEntityName(parentSegment.getTableName());
						String relationShipName              = MessageFormat.format(com.ibm.is.sappack.gen.common.Constants.FOREIGN_KEY_NAME_TEMPLATE, 
						                                                            new Object[] { parentSegment.getIDocType().getName(), 
						                                                                           parentSegment.getDefinition() } );
						String cleanedTableName              = this.ldmAccessor.getNameConverter().convertEntityName(segment.getTableName());
						String cleanedSegmentFKeyAttribute   = this.ldmAccessor.getNameConverter().convertAttributeName(segmentFKeyAttribute);
						segmentPKeyAttribute = MessageFormat.format(Constants.IDOC_PRIMARY_KEY_TEMPLATE, 
						                                            new Object[] { parentSegment.getDefinition() });

						ldmAccessor.createRelationship(relationShipName, entity.getPackage(), cleanedTableName, 
						                               cleanedSegmentFKeyAttribute, cleanedParentSegmentTableName, 
						                               this.ldmAccessor.getNameConverter().convertAttributeName(segmentPKeyAttribute),
						                               cleanedSegmentFKeyAttribute, "", enforceForeignKey, null); //$NON-NLS-1$

						String cardinality = getCardinality(segment);
						ldmAccessor.createCardinality(relationShipName, entity.getPackage(), cleanedTableName, 
						                              cleanedParentSegmentTableName, cleanedSegmentFKeyAttribute, 
						                              cardinality, "1"); //$NON-NLS-1$
					} // end of if (parentSegment != null)
				} // end of if (doCreateForeignKeys)
			} // end of (else) if (segment instanceof ControlRecord)
		} // end of if (createIDocLoadModel && !isV7StageGeneration)
	}


	private static String getCardinality(Segment segment) {

		long minOccurence;
		long maxOccurence;

		// Check, whether it's a group segment
		if (segment.isParentFlag()) {
			minOccurence = segment.getGroupMinOccurence();
			maxOccurence = segment.getGroupMaxOccurence();
		}
		// Or a normal segment
		else {
			minOccurence = segment.getMinOccurrence();
			maxOccurence = segment.getMaxOccurrence();
		}

		if (minOccurence > 1) {
			String msg = Messages.IdocType2LdmImporter_7;
			msg = MessageFormat.format(msg, segment.getDefinition(), minOccurence);
			Activator.getLogger().log(Level.WARNING, msg);
		}

		if (minOccurence == 0) {
			if (maxOccurence == 1) {
				return "C"; // 0...1 //$NON-NLS-1$
			} else {
				return "CN"; // 0...N //$NON-NLS-1$
			}
		} 
		else {
			if (maxOccurence == 1) {
				return "1"; // 1 //$NON-NLS-1$
			} else {
				return "N"; // 1...N //$NON-NLS-1$
			}
		}
	}

	private List<TechnicalField> getTechnicalFields() {
		if (this.explicitTechnicalFields != null) {
			return this.explicitTechnicalFields;
		}

		List<TechnicalField> result = new ArrayList<TechnicalField>();
		if (this.options.isCreateTechnicalFields()) {
			boolean additionalFieldsNullable = this.options.isTechnicalFieldsCanBeNullable();
			String description = Messages.IdocType2LdmImporter_8;
			result.add(new TechnicalFieldVarchar("LEGACY_ID", description, 20, false, additionalFieldsNullable)); //$NON-NLS-1$
			result.add(new TechnicalFieldVarchar("LEGACY_UK", description, 120, false, additionalFieldsNullable)); //$NON-NLS-1$
			result.add(new TechnicalFieldVarchar("LOB", description, 10, false, additionalFieldsNullable)); //$NON-NLS-1$
			result.add(new TechnicalFieldVarchar("RLOUT", description, 30, false, additionalFieldsNullable)); //$NON-NLS-1$
			result.add(new TechnicalFieldVarchar("CATEGORY", description, 30, false, additionalFieldsNullable)); //$NON-NLS-1$
			result.add(new TechnicalFieldInteger("VERSION", description, false, additionalFieldsNullable)); //$NON-NLS-1$
			result.add(new TechnicalFieldInteger("LOAD_ID", description, false, additionalFieldsNullable)); //$NON-NLS-1$
		}

		return result;
	}

	private void addSegmentAdditionalFields(LdmAccessor ldmAccessor, Entity entity, Segment segment, Map<String, String> entityFields) {
		List<TechnicalField> techFields = getTechnicalFields();
		for (TechnicalField tf : techFields) {
			Attribute attribute = ldmAccessor.addColumnMetadataToTable(entity, tf);
			if (attribute != null) {
				ldmAccessor.addAnnotation(attribute, Constants.ANNOT_DATA_OBJECT_SOURCE, Constants.DATA_OBJECT_SOURCE_TYPE_TECH_FIELD);
				ldmAccessor.addAnnotation(attribute, Constants.ANNOT_TRANSFORMER_SOURCE_MAPPING, attribute.getName());
				ldmAccessor.addAnnotation(attribute, Constants.ANNOT_COLUMN_IS_UNICODE, String.valueOf(this.sapSystem.isUnicode()));
			}
			entityFields.put(tf.getFieldName(), (tf instanceof TechnicalFieldVarchar) ? "VARCHAR" : "INTEGER"); //$NON-NLS-1$ //$NON-NLS-2$
		}

		/*
		 * TechnicalFieldVarchar legacyIdField = new
		 * TechnicalFieldVarchar("LEGACY_ID", "technical field", 20, false,
		 * additionalFieldsNullable); Attribute attribute =
		 * ldmAccessor.addColumnMetadataToTable(entity, legacyIdField); //
		 * Attribute attribute = ldmAccessor.addColumnMetadataToTable(entity, //
		 * "LEGACY_ID", false, "technical field", "LEGACY_ID", sapDataType, //
		 * additionalFieldsNullable); if (attribute != null) {
		 * ldmAccessor.addAnnotation(attribute,
		 * Constants.ANNOT_DATA_OBJECT_SOURCE,
		 * Constants.DATA_OBJECT_SOURCE_TYPE_TECH_FIELD); }
		 * 
		 * TechnicalFieldVarchar legacyUkField = new
		 * TechnicalFieldVarchar("LEGACY_UK", "technical field", 20, false,
		 * additionalFieldsNullable); attribute =
		 * ldmAccessor.addColumnMetadataToTable(entity, legacyUkField); //
		 * sapDataType = new SapDataType("VARC", 'c', 120, 120, 0); // attribute
		 * = ldmAccessor.addColumnMetadataToTable(entity, "LEGACY_UK", // false,
		 * "technical field", "LEGACY_UK", sapDataType, //
		 * additionalFieldsNullable); if (attribute != null) {
		 * ldmAccessor.addAnnotation(attribute,
		 * Constants.ANNOT_DATA_OBJECT_SOURCE,
		 * Constants.DATA_OBJECT_SOURCE_TYPE_TECH_FIELD); }
		 * 
		 * TechnicalFieldVarchar lobField = new TechnicalFieldVarchar("LOB",
		 * "technical field", 10, false, additionalFieldsNullable); attribute =
		 * ldmAccessor.addColumnMetadataToTable(entity, lobField); //
		 * sapDataType = new SapDataType("VARC", 'c', 10, 10, 0); // attribute =
		 * ldmAccessor.addColumnMetadataToTable(entity, "LOB", // false,
		 * "technical field", "LOB", sapDataType, // additionalFieldsNullable);
		 * if (attribute != null) { ldmAccessor.addAnnotation(attribute,
		 * Constants.ANNOT_DATA_OBJECT_SOURCE,
		 * Constants.DATA_OBJECT_SOURCE_TYPE_TECH_FIELD); }
		 * 
		 * TechnicalFieldVarchar rloutField = new TechnicalFieldVarchar("RLOUT",
		 * "technical field", 30, false, additionalFieldsNullable); attribute =
		 * ldmAccessor.addColumnMetadataToTable(entity, rloutField); //
		 * sapDataType = new SapDataType("VARC", 'c', 30, 30, 0); // attribute =
		 * ldmAccessor.addColumnMetadataToTable(entity, "RLOUT", // false,
		 * "technical field", "RLOUT", sapDataType, //
		 * additionalFieldsNullable); if (attribute != null) {
		 * ldmAccessor.addAnnotation(attribute,
		 * Constants.ANNOT_DATA_OBJECT_SOURCE,
		 * Constants.DATA_OBJECT_SOURCE_TYPE_TECH_FIELD); }
		 * 
		 * TechnicalFieldVarchar categoryField = new
		 * TechnicalFieldVarchar("CATEGORY", "technical field", 30, false,
		 * additionalFieldsNullable); attribute =
		 * ldmAccessor.addColumnMetadataToTable(entity, categoryField); //
		 * sapDataType = new SapDataType("VARC", 'c', 30, 30, 0); // attribute =
		 * ldmAccessor.addColumnMetadataToTable(entity, "CATEGORY", // false,
		 * "technical field", "CATEGORY", sapDataType, //
		 * additionalFieldsNullable); if (attribute != null) {
		 * ldmAccessor.addAnnotation(attribute,
		 * Constants.ANNOT_DATA_OBJECT_SOURCE,
		 * Constants.DATA_OBJECT_SOURCE_TYPE_TECH_FIELD); }
		 * 
		 * TechnicalFieldInteger versionField = new
		 * TechnicalFieldInteger("VERSION", "technical field", false,
		 * additionalFieldsNullable); attribute =
		 * ldmAccessor.addColumnMetadataToTable(entity, versionField); //
		 * sapDataType = new SapDataType("INT4", 'c', 0, 0, 0); // attribute =
		 * ldmAccessor.addColumnMetadataToTable(entity, "VERSION", // false,
		 * "technical field", "VERSION", sapDataType, //
		 * additionalFieldsNullable); if (attribute != null) {
		 * ldmAccessor.addAnnotation(attribute,
		 * Constants.ANNOT_DATA_OBJECT_SOURCE,
		 * Constants.DATA_OBJECT_SOURCE_TYPE_TECH_FIELD); }
		 * 
		 * TechnicalFieldInteger loadIdField = new
		 * TechnicalFieldInteger("LOAD_ID", "technical field", false,
		 * additionalFieldsNullable); attribute =
		 * ldmAccessor.addColumnMetadataToTable(entity, loadIdField); //
		 * sapDataType = new SapDataType("INT4", 'c', 0, 0, 0); // attribute =
		 * ldmAccessor.addColumnMetadataToTable(entity, "LOAD_ID", // false,
		 * "technical field", "LOAD_ID", sapDataType, //
		 * additionalFieldsNullable); if (attribute != null) {
		 * ldmAccessor.addAnnotation(attribute,
		 * Constants.ANNOT_DATA_OBJECT_SOURCE,
		 * Constants.DATA_OBJECT_SOURCE_TYPE_TECH_FIELD); }
		 * 
		 * entityFields.put("LEGACY_ID", "VARCHAR");
		 * entityFields.put("LEGACY_UK", "VARCHAR"); entityFields.put("LOB",
		 * "VARCHAR"); entityFields.put("RLOUT", "VARCHAR");
		 * entityFields.put("CATEGORY", "VARCHAR"); entityFields.put("VERSION",
		 * "INTEGER"); entityFields.put("LOAD_ID", "INTEGER");
		 */
	}

	private void addFieldAnnotations(LdmAccessor ldmAccessor, Attribute attribute, String derivationExpression) {
		if (attribute != null) {
			ldmAccessor.addAnnotation(attribute, Constants.ANNOT_DATA_OBJECT_SOURCE, Constants.DATA_OBJECT_SOURCE_TYPE_IDOC);
			ldmAccessor.addAnnotation(attribute, com.ibm.is.sappack.gen.common.Constants.ANNOT_COLUMN_IS_UNICODE, 
					selectedIDocType.getSapBrowser().getIsUnicodeSapSystem().toString());

			// build the source mapping from derivation
			String srcMapping;

			// exception for ADMIN columns
			if (derivationExpression.startsWith(Constants.IDOC_TECH_FLD_ADM_PREFIX)) {
				srcMapping = attribute.getName();

				// non-V7 stages require a derivation for "ADMIN." attributes
				if (!isV7StageGeneration) {
					ldmAccessor.addAnnotation(attribute, com.ibm.is.sappack.gen.common.Constants.ANNOT_COLUMN_DERIVATION_EXPRESSION,
							derivationExpression);
				}
			}
			else {
				// it's a regular field and not an ADMIN field 
				// ==> clean the derivation and convert the source mapping
				srcMapping           = ldmAccessor.getNameConverter().convertAttributeName(derivationExpression);
				derivationExpression = StringUtils.cleanFieldName(derivationExpression);

				ldmAccessor.addAnnotation(attribute, com.ibm.is.sappack.gen.common.Constants.ANNOT_COLUMN_DERIVATION_EXPRESSION,
						derivationExpression);
			}

			ldmAccessor.addAnnotation(attribute, com.ibm.is.sappack.gen.common.Constants.ANNOT_TRANSFORMER_SOURCE_MAPPING, 
					srcMapping);
		}
	}


	public void setExplicitTechnicalFields(List<TechnicalField> technicalFields) {
		this.explicitTechnicalFields = technicalFields;
	}

	public List<TechnicalField> getExplicitTechnicalFields() {
		return this.explicitTechnicalFields;
	}

	/**
	 * setValueTables
	 * 
	 * @param valueTables
	 */
	private void setValueTables(List<String> valueTables) {
		//	this.valueTables = valueTables;
	}

	/**
	 * setValueTableSet
	 * 
	 * @param valueTableSet
	 */
	public void setValueTableSet(SapTableSet valueTableSet) {
		this.valueTableSet = valueTableSet;
	}

}
