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
// Module Name : com.ibm.is.sappack.gen.tools.sap.utilities
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.tools.sap.utilities;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;

import com.ibm.db.models.logical.Entity;
import com.ibm.db.models.logical.Package;
import com.ibm.iis.sappack.gen.common.ui.connections.SapSystem;
import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.tools.sap.activator.Activator;
import com.ibm.is.sappack.gen.tools.sap.constants.Constants;
import com.ibm.is.sappack.gen.tools.sap.importer.helper.datatype.sap.SapDataElement;
import com.ibm.is.sappack.gen.tools.sap.importer.helper.datatype.sap.SapDataType;
import com.ibm.is.sappack.gen.tools.sap.importer.helper.datatype.sap.SapDomain;
import com.ibm.is.sappack.gen.tools.sap.importer.helper.model.LdmAccessor;
import com.ibm.is.sappack.gen.tools.sap.importer.idoctypes.IDocType;
import com.ibm.is.sappack.gen.tools.sap.model.SapTable;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoTable;

public class ReferenceTableImporter extends TableImporterBase {
	private int    transferMethods      = com.ibm.is.sappack.gen.common.Constants.ABAP_TRANSFERMETHOD_RFC;


	static String copyright() {
		return com.ibm.is.sappack.gen.tools.sap.utilities.Copyright.IBM_COPYRIGHT_SHORT;
	}
	
	public ReferenceTableImporter(SapSystem sapSystem, LdmAccessor ldmAccessor, Package ldmPackage, int transferMethods) {
		super(sapSystem, ldmAccessor, ldmPackage);
		this.transferMethods = transferMethods;
	}

	
	
	public void importReducedCheckAndTextTableTables(Collection<SapTable> checkTables, IDocType idocType, IProgressMonitor progressMonitor) throws JCoException {
		trace("checkTables: " + checkTables.size() + "(" + checkTables + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		// create a map from check to text table names
		Map<String, String> check2TextTable = getCheck2TextTableMap(checkTables);

		int jttNum = 0;
		// for each check table
		for (SapTable checkTable : checkTables) {
			if (progressMonitor.isCanceled()) {
				return;
			}
			String pmMsg = Messages.ReferenceTableImporter_0; 
			pmMsg = MessageFormat.format(pmMsg, checkTable.getName());
			progressMonitor.subTask(pmMsg);
			String textTable = check2TextTable.get(checkTable.getName());

			// special case for T002 
			if (checkTable.getName().equalsIgnoreCase("t002")) { //$NON-NLS-1$
				textTable = "T002T"; //$NON-NLS-1$
			}

			// the lists joinColumnsCheckTable and joinColumnsTextTable
			// must be of the same length:
			// joinColumnsCheckTable[i] of the check table must be joined with joinColumnsTextTable[i] of the text table
			List<String> joinColumnsCheckTable = new ArrayList<String>();
			List<String> joinColumnsTextTable = new ArrayList<String>();
			boolean joinColumnsOK = true;

			if (textTable != null) {
				joinColumnsOK = computeJoinColumns(checkTable.getName(), textTable, joinColumnsCheckTable, joinColumnsTextTable);
			}

			// if there was more than one join condition, skip this check table
			if (!joinColumnsOK) {
				continue;
			}

			trace("joinColumns of check table: " + joinColumnsCheckTable); //$NON-NLS-1$
			trace("joinColumns of text table: " + joinColumnsTextTable); //$NON-NLS-1$

			Entity checkTableEntity = createCheckTableEntity(checkTable, textTable, idocType);
			if (checkTableEntity != null) {
				ABAPExtractCodeGenerator checkTableCodeGen = new ABAPExtractCodeGenerator(checkTable.getName(), ldmAccessor);
				addCheckTableColumns(checkTable.getName(), checkTableEntity, joinColumnsCheckTable, checkTableCodeGen);
				addAbapProgram(checkTableEntity, checkTableCodeGen);

				// annotate potential translation table name
				String translationTableName = TranslationTableImporter.createModelTranslationTableName(ldmAccessor, checkTable.getName());
				ldmAccessor.addAnnotation(checkTableEntity, com.ibm.is.sappack.gen.common.Constants.ANNOT_TRANSLATION_TABLE_NAME, translationTableName);
				
				// add the fields for each texttable
				if (textTable != null) {
					StringBuffer buf = new StringBuffer();
					for (int i = 0; i < joinColumnsCheckTable.size(); i++) {
						if (i > 0) {
							buf.append(","); //$NON-NLS-1$
						}
						buf.append(joinColumnsCheckTable.get(i) + "=" + joinColumnsTextTable.get(i)); //$NON-NLS-1$
					}
					String joinColumnsCondition = buf.toString();
					ldmAccessor.addAnnotation(checkTableEntity, com.ibm.is.sappack.gen.common.Constants.ANNOT_CHECKTABLE_TEXTTABLE_JOINCONDITION, joinColumnsCondition);

					Entity textTableEntity = createTextTableEntity(textTable, idocType);
					if (textTableEntity != null) {
						ABAPExtractCodeGenerator textTableCodeGen = new ABAPExtractCodeGenerator(textTable, ldmAccessor);
						addTextTableColumns(textTable, textTableEntity, textTableCodeGen);
						addAbapProgram(textTableEntity, textTableCodeGen);
					}
				}
			}
			jttNum++;
			progressMonitor.worked(1);
		}
	}

	public static String createModelTextTableName(String textTable) {
		return "TX_" + textTable; //$NON-NLS-1$
	}

	private Entity createTextTableEntity(String textTable, IDocType idocType) throws JCoException {
		String modelTableName = createModelTextTableName(textTable);
		Entity entity = this.ldmAccessor.findEntity(modelTableName);
		// entity already exists
		if (entity != null) {
			return null;
		}
		String[] descAndDevClass = TableDescriptionExporter.getDescriptionAndDevClass(destination, textTable);
		String desc = descAndDevClass[0];
		String devClass = descAndDevClass[1];
		entity = this.ldmAccessor.createEntity(this.targetPackage, modelTableName, desc);
		ldmAccessor.addAnnotation(entity, com.ibm.is.sappack.gen.common.Constants.ANNOT_TEXT_TABLE_NAME, textTable);
		ldmAccessor.addAnnotation(entity, com.ibm.is.sappack.gen.common.Constants.ANNOT_LOGICAL_TBL_NAME, modelTableName);
		ldmAccessor.addAnnotation(entity, com.ibm.is.sappack.gen.common.Constants.ANNOT_PHYSICAL_TBL_NAME, entity.getName());
		ldmAccessor.addAnnotation(entity, com.ibm.is.sappack.gen.common.Constants.ANNOT_DATA_OBJECT_SOURCE, com.ibm.is.sappack.gen.common.Constants.DATA_OBJECT_SOURCE_TYPE_REFERENCE_TEXT_TABLE);
		if (idocType != null) {
			boolean isExtendedType = idocType.isExtendedIDocType();
			ldmAccessor.addAnnotation(entity, com.ibm.is.sappack.gen.common.Constants.ANNOT_IDOC_TYPE, idocType.getName());
			ldmAccessor.addAnnotation(entity, com.ibm.is.sappack.gen.common.Constants.ANNOT_IDOC_RELEASE, idocType.getRelease());			
			ldmAccessor.addAnnotation(entity, com.ibm.is.sappack.gen.common.Constants.ANNOT_IS_EXTENDED_IDOC_TYPE, String.valueOf(isExtendedType));
			if (isExtendedType) {
				ldmAccessor.addAnnotation(entity, com.ibm.is.sappack.gen.common.Constants.ANNOT_IDOC_BASIC_TYPE, idocType.getBasicType());
			}
		}
		ldmAccessor.addAnnotation(entity, com.ibm.is.sappack.gen.common.Constants.ANNOT_GENERATED_MODEL_VERSION, com.ibm.is.sappack.gen.common.Constants.MODEL_VERSION);
		ldmAccessor.addAnnotation(entity, com.ibm.is.sappack.gen.common.Constants.ANNOT_TBL_DEV_CLASS, devClass);

		return entity;
	}

	public static String createModelCheckTableName(String checkTableName, LdmAccessor ldmAccessor) {
		checkTableName = ldmAccessor.getNameConverter().convertEntityName(checkTableName);
		return "CT_" + checkTableName; //$NON-NLS-1$
	}
	
	private Entity createCheckTableEntity(SapTable checkTable, String textTable, IDocType idocType) {
		String modelTableName = createModelCheckTableName(checkTable.getName(), this.ldmAccessor);
		Entity entity = this.ldmAccessor.findEntity(modelTableName);
		// entity already exists
		if (entity != null) {
			return null;
		}

		String desc = checkTable.getDescription();
		entity = this.ldmAccessor.createEntity(this.targetPackage, modelTableName, desc);
		ldmAccessor.addAnnotation(entity, com.ibm.is.sappack.gen.common.Constants.ANNOT_CHECK_TABLE_NAME, checkTable.getName());
		ldmAccessor.addAnnotation(entity, com.ibm.is.sappack.gen.common.Constants.ANNOT_LOGICAL_TBL_NAME, modelTableName);
		ldmAccessor.addAnnotation(entity, com.ibm.is.sappack.gen.common.Constants.ANNOT_PHYSICAL_TBL_NAME, entity.getName());
		if (textTable != null) {
			ldmAccessor.addAnnotation(entity, com.ibm.is.sappack.gen.common.Constants.ANNOT_TEXT_TABLE_NAME, textTable);
			ldmAccessor.addAnnotation(entity, com.ibm.is.sappack.gen.common.Constants.ANNOT_MODEL_TEXT_TABLE_NAME,
			                          ldmAccessor.getNameConverter().convertEntityName(createModelTextTableName(textTable)));
		}

		String dataObjectSource = determineCheckTableType(checkTable);
		ldmAccessor.addAnnotation(entity, com.ibm.is.sappack.gen.common.Constants.ANNOT_DATA_OBJECT_SOURCE, dataObjectSource);
		ldmAccessor.addAnnotation(entity, com.ibm.is.sappack.gen.common.Constants.ANNOT_TBL_DEV_CLASS, checkTable.getDevClass());

		if (idocType != null) {
			boolean isExtendedType = idocType.isExtendedIDocType();
			ldmAccessor.addAnnotation(entity, com.ibm.is.sappack.gen.common.Constants.ANNOT_IDOC_TYPE, idocType.getName());
			ldmAccessor.addAnnotation(entity, com.ibm.is.sappack.gen.common.Constants.ANNOT_IDOC_RELEASE, idocType.getRelease());			
			ldmAccessor.addAnnotation(entity, com.ibm.is.sappack.gen.common.Constants.ANNOT_IS_EXTENDED_IDOC_TYPE, String.valueOf(isExtendedType));
			if (isExtendedType) {
				ldmAccessor.addAnnotation(entity, com.ibm.is.sappack.gen.common.Constants.ANNOT_IDOC_BASIC_TYPE, idocType.getBasicType());
			}
		}
		ldmAccessor.addAnnotation(entity, com.ibm.is.sappack.gen.common.Constants.ANNOT_GENERATED_MODEL_VERSION, com.ibm.is.sappack.gen.common.Constants.MODEL_VERSION);

		return entity;
	}

	private void addAbapProgram(Entity entity, ABAPExtractCodeGenerator codeGen) {
		if ((this.transferMethods & com.ibm.is.sappack.gen.common.Constants.ABAP_TRANSFERMETHOD_RFC) > 0) {
			this.ldmAccessor.addAnnotation(entity, com.ibm.is.sappack.gen.common.Constants.ANNOT_ABAP_PROGRAM_NAME,
					codeGen.createProgramName(com.ibm.is.sappack.gen.common.Constants.ABAP_TRANSFERMETHOD_RFC));
			this.ldmAccessor.addAnnotation(entity, com.ibm.is.sappack.gen.common.Constants.ANNOT_ABAP_CODE, codeGen.generateABAPCode(com.ibm.is.sappack.gen.common.Constants.ABAP_TRANSFERMETHOD_RFC));

		}
		if ((this.transferMethods & com.ibm.is.sappack.gen.common.Constants.ABAP_TRANSFERMETHOD_CPIC) > 0) {
			this.ldmAccessor.addAnnotation(entity, com.ibm.is.sappack.gen.common.Constants.ANNOT_ABAP_PROGRAM_NAME_CPIC,
					codeGen.createProgramName(com.ibm.is.sappack.gen.common.Constants.ABAP_TRANSFERMETHOD_CPIC));
			this.ldmAccessor.addAnnotation(entity, com.ibm.is.sappack.gen.common.Constants.ANNOT_ABAP_CODE_CPIC,
					codeGen.generateABAPCode(com.ibm.is.sappack.gen.common.Constants.ABAP_TRANSFERMETHOD_CPIC));
		}
	}

	private void addCheckTableColumns(String table, Entity e, List<String> joinColumnsCheckTable, ABAPExtractCodeGenerator codeGenerator) throws JCoException {
		JCoTable checkTableFields = queryTableMetadata(table);
		do {
			String checkTableCol = checkTableFields.getString("FIELDNAME"); //$NON-NLS-1$
			boolean isKeyColumn = checkTableFields.getString(Constants.JCO_PARAMETER_KEYFLAG).equals(Constants.JCO_PARAMETER_VALUE_TRUE);
			if (isKeyColumn || joinColumnsCheckTable.contains(checkTableCol)) {
				ABAPColumn abapColumn = addColumn(table, e, checkTableFields, "", false, com.ibm.is.sappack.gen.common.Constants.DATA_OBJECT_SOURCE_TYPE_REFERENCE_CHECK_TABLE); //$NON-NLS-1$
				codeGenerator.addColumn(abapColumn);
				
				// osuhre, 135416: add annotations for translation tables
				String colName = abapColumn.getField().getFieldName();
				String srcColName = TranslationTableImporter.createSourceColumnName(ldmAccessor, colName);
				ldmAccessor.addAnnotation(abapColumn.getAttribute(), com.ibm.is.sappack.gen.common.Constants.ANNOT_TRANSCODING_TBL_SRC_FLD, srcColName);
				String tgtColName = TranslationTableImporter.createTargetColumnName(ldmAccessor, colName);
				ldmAccessor.addAnnotation(abapColumn.getAttribute(), com.ibm.is.sappack.gen.common.Constants.ANNOT_TRANSCODING_TBL_TRG_FLD, tgtColName);								
				ldmAccessor.addAnnotation(abapColumn.getAttribute(), com.ibm.is.sappack.gen.common.Constants.ANNOT_IS_KEY_IN_SAP, Boolean.toString(isKeyColumn));
			} else {
				trace("Check table column " + checkTableCol + " is not a join or key column"); //$NON-NLS-1$ //$NON-NLS-2$
			}
		} while (checkTableFields.nextRow());

	}

	private void addTextTableColumns(String table, Entity e, ABAPExtractCodeGenerator codeGenerator) throws JCoException {
		JCoTable textTableFields = queryTableMetadata(table);
		do {
			ABAPColumn abapColumn = addColumn(table, e, textTableFields, "", false, com.ibm.is.sappack.gen.common.Constants.DATA_OBJECT_SOURCE_TYPE_REFERENCE_TEXT_TABLE); //$NON-NLS-1$
			codeGenerator.addColumn(abapColumn);
			// special case for T002T: SPRAS is the language field
			if ("T002T".equalsIgnoreCase(table)) { //$NON-NLS-1$
				if ("SPRAS".equalsIgnoreCase(abapColumn.getColumnName())) { //$NON-NLS-1$ 
					ldmAccessor.addAnnotation(abapColumn.getAttribute(), com.ibm.is.sappack.gen.common.Constants.ANNOT_IS_TEXTTABLE_LANGUAGE_COLUMN,
							com.ibm.is.sappack.gen.common.Constants.ANNOT_VALUE_TRUE);
				}
			} else {
				SapDataType sdt = abapColumn.getField().getDataType();
				String domainName = null;
				if (sdt instanceof SapDomain) {
					SapDomain sd = (SapDomain) sdt;
					domainName = sd.getDomainName();
				} else if (sdt instanceof SapDataElement) {
					SapDataElement sde = (SapDataElement) sdt;
					domainName = sde.getDomainName();
				}
				if (domainName != null) {
					if ("SPRAS".equals(domainName)) { //$NON-NLS-1$
						ldmAccessor.addAnnotation(abapColumn.getAttribute(), com.ibm.is.sappack.gen.common.Constants.ANNOT_IS_TEXTTABLE_LANGUAGE_COLUMN,
								com.ibm.is.sappack.gen.common.Constants.ANNOT_VALUE_TRUE);
					}
				}
			}
			boolean isKeyColumn = textTableFields.getString(Constants.JCO_PARAMETER_KEYFLAG).equals(Constants.JCO_PARAMETER_VALUE_TRUE);
			ldmAccessor.addAnnotation(abapColumn.getAttribute(), com.ibm.is.sappack.gen.common.Constants.ANNOT_IS_KEY_IN_SAP, Boolean.toString(isKeyColumn));

		} while (textTableFields.nextRow());

	}

	private Map<String, String> getCheck2TextTableMap(Collection<SapTable> checkTables) throws JCoException {
		Map<String, String> result = new HashMap<String, String>();
		for (SapTable checkTable : checkTables) {
			this.function_TABLE_GET_TEXTTABLE.getImportParameterList().setValue(Constants.JCO_PARAMETER_CHECKTABLE, checkTable.getName());
			try {
				this.function_TABLE_GET_TEXTTABLE.execute(this.destination);
			} catch (JCoException e) {
				if (Constants.JCO_ERROR_KEY_TABLE_NOT_FOUND.equals(e.getKey())) {
					// there is no text table for this check table, skip it
					continue;
				}
				throw e;
			}
			String textTable = this.function_TABLE_GET_TEXTTABLE.getExportParameterList().getString(Constants.JCO_PARAMETER_TABNAME);
			result.put(checkTable.getName(), textTable);
		}
		return result;
	}

	// old implementation using DD08L directly
	private Map<String, String> getCheck2TextTableMapDD08L(Collection<SapTable> checkTables) throws JCoException {
		Map<String, String> result = new HashMap<String, String>();
		StringBuffer whereClause = new StringBuffer();
		whereClause.append("AS4LOCAL = 'A' AND FRKART = 'TEXT' AND CHECKTABLE IN ("); //$NON-NLS-1$
		boolean first = true;
		for (SapTable checkTable : checkTables) {
			if (first) {
				first = false;
			} else {
				whereClause.append(", "); //$NON-NLS-1$
			}
			whereClause.append("'" + checkTable.getName() + "'"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		whereClause.append(")"); //$NON-NLS-1$

		String[] columns = new String[] { "CHECKTABLE", "TABNAME" }; //$NON-NLS-1$ //$NON-NLS-2$

		SAPTableExtractor extractor = new SAPTableExtractor(this.destination, "DD08L", Arrays.asList(columns), whereClause.toString()); //$NON-NLS-1$
		extractor.setSkipLengthCheck(true);
		SAPTableExtractor.Result dd08lResult = extractor.performQuery();
		while (dd08lResult.nextRow()) {
			String textTable = dd08lResult.getValue("TABNAME"); //$NON-NLS-1$
			String checkTable = dd08lResult.getValue("CHECKTABLE"); //$NON-NLS-1$
			result.put(checkTable, textTable);
		}
		return result;
	}

	private boolean computeJoinColumns(String checkTable, String textTable, List<String> outJoinColumnsCheckTable, List<String> outJoinColumnsTextTable) throws JCoException {
		boolean joinColumnsOK = true;

		SAPTableExtractor joinCondExtractor = new SAPTableExtractor(this.destination, "DD05Q", //$NON-NLS-1$ 
				Arrays.asList(new String[] { "FORKEY", "CHECKFIELD", "FIELDNAME" }), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ 
				"CHECKTABLE = '" + checkTable + //$NON-NLS-1$ 
						"' AND FORTABLE = '" + textTable + "'"); //$NON-NLS-1$ //$NON-NLS-2$
		SAPTableExtractor.Result joinCondResult = joinCondExtractor.performQuery();
		String joinCondFieldName = null;

		// add the join conditions to joinColumnsCheckTable and joinColumnsTextTable
		// these two lists must be of the same length because it means that
		// joinColumnsCheckTable[i] must be joined with joinColumnsTextTable[i]
		while (joinCondResult.nextRow()) {
			String textTableColumn = joinCondResult.getValue("FORKEY"); //$NON-NLS-1$
			String checkTableColumn = joinCondResult.getValue("CHECKFIELD"); //$NON-NLS-1$
			String fieldName = joinCondResult.getValue("FIELDNAME"); //$NON-NLS-1$

			// special case: For T002 the join columns for SPRAS must be
			// ignored
			if (checkTable.equals("T002")) { //$NON-NLS-1$
				if (fieldName.equals("SPRAS")) { //$NON-NLS-1$
					continue;
				}
			}

			// if this is the first pass through the DD05Q result
			if (joinCondFieldName == null) {
				joinCondFieldName = fieldName;
			} else {
				// if there is a second join condition

				// check if the field name belongs to the current foreign key (join condition field name)
				if (!joinCondFieldName.equals(fieldName)) {
					// NO ==> then check if the field name exist in the join column list
					if (outJoinColumnsTextTable.indexOf(fieldName) > -1) {
						// yes it exists ==> use this name as new foreign key name
						joinCondFieldName = fieldName;
					} else {
						// no, doesn't exist ==> more than one foreign key
						String msg = Messages.JoinedCheckAndTextTableImporter_1;
						msg = MessageFormat.format(msg, textTable);
						Activator.getLogger().warning(msg);
						System.out.println(msg);
						joinColumnsOK = false;
						break;
					}
				} // end of if (!joinCondFieldName.equals(fieldName))
			} // end of (else) if (joinCondFieldName == null)

//			checkTableColumn = this.ldmAccessor.getNameConverter().convertAttributeName(checkTableColumn);
//			textTableColumn = this.ldmAccessor.getNameConverter().convertAttributeName(textTableColumn);

			// Defect 117541 (hschoen): add columns if they don't exist yet
			// ------------------------------------------------------------
			// TODO OLIX ensure that joinColumnsCheckTable and joinColumnsTextTable are of same length
			if (outJoinColumnsCheckTable.indexOf(checkTableColumn) < 0) {
				outJoinColumnsCheckTable.add(checkTableColumn);
			}
			if (outJoinColumnsTextTable.indexOf(textTableColumn) < 0) {
				outJoinColumnsTextTable.add(textTableColumn);
			}
		}
		return joinColumnsOK;
	}
	
}
