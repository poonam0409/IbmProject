//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2009, 2013                                              
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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;

import com.ibm.db.models.logical.Attribute;
import com.ibm.db.models.logical.Entity;
import com.ibm.db.models.logical.Package;
import com.ibm.iis.sappack.gen.common.ui.connections.SapSystem;
import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.tools.sap.constants.Constants;
import com.ibm.is.sappack.gen.tools.sap.importer.helper.field.AbstractField;
import com.ibm.is.sappack.gen.tools.sap.importer.helper.field.TechnicalFieldVarchar;
import com.ibm.is.sappack.gen.tools.sap.importer.helper.model.LdmAccessor;
import com.ibm.is.sappack.gen.tools.sap.importer.helper.model.LdmNameConverter;
import com.ibm.is.sappack.gen.tools.sap.importer.idoctypes.IDocType;
import com.ibm.is.sappack.gen.tools.sap.model.SapTable;
import com.ibm.is.sappack.gen.tools.sap.model.SapTableSet;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoTable;


public class TranslationTableImporter extends TableImporterBase {
	private static final String RELATED_TT_COL_MAP_KEY_TEMPLATE   = "%s.%s(%s)";
	private static final String CHECK_TABLE_TYPE_MAP_KEY_TEMPLATE = "%s.%s";
	private static final String SAP_CLIENT_DOMAIN_NAME            = "MANDT";

	private LdmAccessor dataTableLDMAccessor;

	
	static String copyright() {
		return com.ibm.is.sappack.gen.tools.sap.utilities.Copyright.IBM_COPYRIGHT_SHORT;
	}

	public TranslationTableImporter(SapSystem sapSystem, LdmAccessor ldmAccessor, Package ldmPackage, LdmAccessor dataTableLDMAccessor) {
		super(sapSystem, ldmAccessor, ldmPackage);
		this.dataTableLDMAccessor = dataTableLDMAccessor;
	}
	
	
	
	public static String createModelTranslationTableName(LdmAccessor ldmAccessor, String checkTableName) {
		LdmNameConverter nameConverter = ldmAccessor.getNameConverter();
		return nameConverter.convertAttributeName(Constants.CW_TT_PREFIX + checkTableName);
	}

	public static String createSourceColumnName(LdmAccessor ldmAccessor, String columnName) {
		return ldmAccessor.getNameConverter().convertAttributeName(com.ibm.is.sappack.gen.common.Constants.SOURCE_COLUMN_PREFIX + columnName); 
	}

	public static String createTargetColumnName(LdmAccessor ldmAccessor, String columnName) {
		return ldmAccessor.getNameConverter().convertAttributeName(com.ibm.is.sappack.gen.common.Constants.TARGET_COLUMN_PREFIX + columnName); 
	}

	
	
	public void createTranslationTables(Collection<SapTable> checkTables, IDocType idocType, IProgressMonitor progressMonitor) throws JCoException {
		trace("createTranslationTables checkTables: " + checkTables.size() + "(" + checkTables + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		LdmNameConverter nameConverter = ldmAccessor.getNameConverter();
		
		// for each check table
		for (SapTable checkTable : checkTables) {
			if (progressMonitor.isCanceled()) {
				return;
			}

			String translationTableName = createModelTranslationTableName(ldmAccessor, checkTable.getName());

			Entity tt = null;
			if (ldmAccessor.findEntity(this.targetPackage, translationTableName) == null) {
				String ttDesc = Messages.JoinedCheckAndTextTableImporter_3;
				ttDesc = MessageFormat.format(ttDesc, checkTable.getName(), checkTable.getName());
				tt = ldmAccessor.createEntity(targetPackage, translationTableName, ttDesc);
				trace("Creating translation table " + translationTableName); //$NON-NLS-1$ 
				ldmAccessor.addAnnotation(tt, com.ibm.is.sappack.gen.common.Constants.ANNOT_CHECK_TABLE_NAME, checkTable.getName());
				ldmAccessor.addAnnotation(tt, com.ibm.is.sappack.gen.common.Constants.ANNOT_SAPPACK_CHECK_TBL_DATA_OBJECT_SOURCE, determineCheckTableType(checkTable));
				ldmAccessor.addAnnotation(tt, com.ibm.is.sappack.gen.common.Constants.ANNOT_TBL_DEV_CLASS, checkTable.getDevClass());
				ldmAccessor.addAnnotation(tt, com.ibm.is.sappack.gen.common.Constants.ANNOT_DATA_OBJECT_SOURCE, com.ibm.is.sappack.gen.common.Constants.DATA_OBJECT_SOURCE_TYPE_TRANSLATION_TABLE);
				ldmAccessor.addAnnotation(tt, com.ibm.is.sappack.gen.common.Constants.ANNOT_GENERATED_MODEL_VERSION, com.ibm.is.sappack.gen.common.Constants.MODEL_VERSION);
				if (idocType != null) {
					ldmAccessor.addAnnotation(tt, com.ibm.is.sappack.gen.common.Constants.ANNOT_IDOC_TYPE, idocType.getName());
					ldmAccessor.addAnnotation(tt, com.ibm.is.sappack.gen.common.Constants.ANNOT_IDOC_RELEASE, idocType.getRelease());					
					ldmAccessor.addAnnotation(tt, com.ibm.is.sappack.gen.common.Constants.ANNOT_IS_EXTENDED_IDOC_TYPE, String.valueOf(idocType.isExtendedIDocType()));
				}

				AbstractField technicalFieldSourceLegacyId = new TechnicalFieldVarchar("SOURCE_LEGACY_ID", Messages.JoinedCheckAndTextTableImporter_4, 20, true, false); //$NON-NLS-1$
				ldmAccessor.addColumnMetadataToTable(tt, technicalFieldSourceLegacyId);

				AbstractField technicalFieldTargetLegacyId = new TechnicalFieldVarchar("TARGET_LEGACY_ID", Messages.JoinedCheckAndTextTableImporter_5, 20, true, false); //$NON-NLS-1$
				ldmAccessor.addColumnMetadataToTable(tt, technicalFieldTargetLegacyId);

				AbstractField technicalFieldSourceDescription = new TechnicalFieldVarchar("SOURCE_DESCRIPTION", Messages.JoinedCheckAndTextTableImporter_6, 250, false, true); //$NON-NLS-1$
				ldmAccessor.addColumnMetadataToTable(tt, technicalFieldSourceDescription);
			}

			// get fields of checktable
			JCoTable checkTableFields = null;
			checkTableFields = queryTableMetadata(checkTable.getName());
			if (checkTableFields == null) {
				continue;
			}

			if (!checkTableFields.isEmpty()) {
				// add source fields
				do {
					boolean isKeyColumn = checkTableFields.getString(Constants.JCO_PARAMETER_KEYFLAG).equals(Constants.JCO_PARAMETER_VALUE_TRUE);
					boolean isClientField = SAP_CLIENT_DOMAIN_NAME.equalsIgnoreCase(checkTableFields.getString(Constants.JCO_PARAMETER_DOMNAME));
					if (isKeyColumn && !isClientField && tt != null) {
						String cleanTableName = nameConverter.convertAttributeName(checkTable.getName());
						addColumn(cleanTableName, tt, checkTableFields, com.ibm.is.sappack.gen.common.Constants.SOURCE_COLUMN_PREFIX, false, com.ibm.is.sappack.gen.common.Constants.DATA_OBJECT_SOURCE_TYPE_TRANSLATION_TABLE); 
					}
				} while (checkTableFields.nextRow());

				// add target fields
				checkTableFields.firstRow();
				do {
					boolean isKeyColumn = checkTableFields.getString(Constants.JCO_PARAMETER_KEYFLAG).equals(Constants.JCO_PARAMETER_VALUE_TRUE);
					boolean isClientField = SAP_CLIENT_DOMAIN_NAME.equalsIgnoreCase(checkTableFields.getString(Constants.JCO_PARAMETER_DOMNAME));
					if (isKeyColumn && !isClientField && tt != null) {
						String cleanTableName = nameConverter.convertAttributeName(checkTable.getName());
						addColumn(cleanTableName, tt, checkTableFields, com.ibm.is.sappack.gen.common.Constants.TARGET_COLUMN_PREFIX, true, com.ibm.is.sappack.gen.common.Constants.DATA_OBJECT_SOURCE_TYPE_TRANSLATION_TABLE); 
					}
				} while (checkTableFields.nextRow());
			}
		}
	}


	public void createTranslationTables(List<SapTable> checkTablesForJoinedCheckAndTextTable, IProgressMonitor progressMonitor) throws JCoException {
		this.createTranslationTables(checkTablesForJoinedCheckAndTextTable, null, progressMonitor);
	}


	/**
	 * annotateTranslationTableReferences
	 * 
	 * create annotations for referenced translation tables 
	 * and the respective translation table columns including 
	 * the join conditions that are necessary to join the 
	 * SAP value table with the translation tables. The following
	 * annotations are created:
	 * 
	 * SAPPACK_RELATED_TRANSLATIONTABLE stores the referenced
	 * translation table of the annotated SAP value table column. 
	 * 
	 * SAPPACK_RELATED_TRANSLATIONTABLE_COLUMN stores the referenced
	 * translation table column of the annotated SAP value table column.
	 * 
	 * SAPPACK_RELATED_TRANSLATIONTABLE_JOINCONDITION stores the join
	 * condition that is used to join the SAP value table with the 
	 * translation table.
	 * 
	 * SAPPACK_CHECK_TABLE_DATA_OBJECT_SOURCE stores the type of the referenced
	 * check table: ReferenceCheckTable of NonReferenceCheckTable
	 * 
	 * @param sapTableSet containing the SAP value tables
	 */
	public void annotateTranslationTableReferences(SapTableSet sapTableSet) throws JCoException {
		Iterator<SapTable> sapTableIterator = sapTableSet.iterator();

		LdmNameConverter nameConverter = this.ldmAccessor.getNameConverter();
		
		/* store the referenced translation table column of a sap table column */
		Map<String, String> relatedTTColumnMap = new HashMap<String, String>();
		
		/* store the referenced check table type (ReferenceCheckTable or NonReferenceCheckTable) of a sap table column */
		Map<String, String> relatedCheckTableTypeMap = new HashMap<String, String>();

		/* store the join condition of a sap table to a translation table */
		Map<String, StringBuffer> relatedTTJoinConditionMap = new HashMap<String, StringBuffer>();

		/* iterate over all SAP value tables and collect information about translation tables, the
		 * involved translation table columns and the join conditions */
		while (sapTableIterator.hasNext()) {
			SapTable sapTable = sapTableIterator.next();
			String tableName = sapTable.getName();

			/* Do not handle check and text tables */
			if(sapTable.isCheckTable() || sapTable.isTextTable()) {
				continue;
			}

			// build a set of columns that processing it to be ignored
			// --> look into all columns of the current data table
			Set<String> ignoreColumnsSet = new HashSet<String>();
			Entity tableEnt              = dataTableLDMAccessor.findEntity(tableName);
			EList<Attribute> fieldList   = tableEnt.getAttributes();
			if (fieldList != null) {      // sanity check
				Iterator<Attribute> fieldIter = fieldList.iterator();

				while(fieldIter.hasNext()) {
					Attribute field = fieldIter.next();

					// ignore join conditions for client data columns
					String clientSAPDomainType = LdmAccessor.getAnnotationValue(field, 
					                                                            com.ibm.is.sappack.gen.common.Constants.ANNOT_DATATYPE_DOMAIN);
					if (SAP_CLIENT_DOMAIN_NAME.equalsIgnoreCase(clientSAPDomainType)) {
						// get original SAP column name
						String origSAPColumnName = LdmAccessor.getAnnotationValue(field, 
                                                                            com.ibm.is.sappack.gen.common.Constants.ANNOT_SAP_COLUMN_NAME);

						if (origSAPColumnName == null) {
							origSAPColumnName = field.getName();
						}
						ignoreColumnsSet.add(origSAPColumnName);
					}
				}
			} // end of if (fieldList != null)

			/* Query SAP table DD05Q to get the check table and check fields of the SAP value table */
			SAPTableExtractor dd05qExtractor = new SAPTableExtractor(this.destination, "DD05Q",                    //$NON-NLS-1$ 
			                                                         Arrays.asList(new String[] { "FORTABLE",      //$NON-NLS-1$
			                                                                                      "FIELDNAME",     //$NON-NLS-1$
			                                                                                      "FORKEY",        //$NON-NLS-1$
			                                                                                      "CHECKTABLE",    //$NON-NLS-1$
			                                                                                      "CHECKFIELD",    //$NON-NLS-1$
			                                                         									  "CHECKTABLE" }), //$NON-NLS-1$ 
			                                                         "TABNAME = '" + tableName + "'"); //$NON-NLS-1$ 
			SAPTableExtractor.Result dd05qResult = dd05qExtractor.performQuery();

			StringBuffer joinCondition = new StringBuffer();
			while (dd05qResult.nextRow()) {

				String tabName    = dd05qResult.getValue("TABNAME");//$NON-NLS-1$
				String fieldName  = dd05qResult.getValue("FIELDNAME");//$NON-NLS-1$
				String forTable   = dd05qResult.getValue("FORTABLE"); //$NON-NLS-1$
				String forKey     = dd05qResult.getValue("FORKEY"); //$NON-NLS-1$
				String checkTable = dd05qResult.getValue("CHECKTABLE");//$NON-NLS-1$
				String checkField = dd05qResult.getValue("CHECKFIELD"); //$NON-NLS-1$

				/* determine the check table type: ReferenceCheckTable of NonReferenceCheckTable */
				if(checkTable != null && !checkTable.equals(Constants.EMPTY_STRING)) {
					SapTable ct = sapTableSet.getTable(checkTable);
					if(ct != null) {
						String checkTableType = determineCheckTableType(ct);
						String mapKey         = String.format(CHECK_TABLE_TYPE_MAP_KEY_TEMPLATE, nameConverter.convertAttributeName(forTable),
						                                                                         nameConverter.convertAttributeName(fieldName));
						relatedCheckTableTypeMap.put(mapKey, checkTableType);
					}
				}
				
				// ignore 'SAP_CLIENT_DOMAIN_NAME' join conditions
				if(ignoreColumnsSet.contains(forKey) || ignoreColumnsSet.contains(checkField)) {
					continue;
				}

				/* ignore internal references, such as T1.C1 references T1.C2 */
				if(forTable.equals(checkTable)) {
					continue;
				}
				
				// osuhre, 164218: create join conditions with constant values
				
				/* different processing for constant value FORTABLES, e.g. 'OR' */
				boolean isConstantValue = false;
				if( forTable.startsWith("'") && forTable.endsWith("'")) { //$NON-NLS-1$ //$NON-NLS-2$
					isConstantValue = true;
				}
				
				if (forTable.equals("*")) { //$NON-NLS-1$
					isConstantValue = true;
					// * is a special value in SAP, 
					// but we can treat it like any other constant value in the join condition
					forTable = "'*'"; //$NON-NLS-1$
				}
				
				/* ignore join conditions that require a third table C to join table A and B */
				if(!forTable.equals(tabName) && !isConstantValue) {
					continue;
				}

				if (!isConstantValue) {
					String mapKey = String.format(RELATED_TT_COL_MAP_KEY_TEMPLATE, nameConverter.convertAttributeName(forTable),
					                                                               nameConverter.convertAttributeName(forKey),
					                                                               checkTable); 

					// use FORTABLE.FORKEY as key to store the referenced CHECKFIELD
					relatedTTColumnMap.put(mapKey, checkField);
				}
				
				/* use TABNAME.FIELDNAME to store the join conditions to join 
				 * the SAP table with the check table. The join condition may
				 * comprise more than one column, such as T1.C1=T2.C1 and T1.C2=T2.C2.
				 * We use the '|' character the separate the various join conditions
				 */
				String key = String.format(CHECK_TABLE_TYPE_MAP_KEY_TEMPLATE, nameConverter.convertAttributeName(tabName),
				                                                              nameConverter.convertAttributeName(fieldName));

				// check if there is already a join condition 
				if(relatedTTJoinConditionMap.containsKey(key)) {
					joinCondition = relatedTTJoinConditionMap.get(key);
					/* append separation character ' | ' */
					joinCondition.append(" | "); //$NON-NLS-1$
				} 
				else {
					joinCondition = new StringBuffer();
				}
				
				// assemble/append join condition
				if(isConstantValue) {
					
					// join condition for constant values
					joinCondition.append(forTable).append(" = ")  //$NON-NLS-1$
					.append(createModelTranslationTableName(ldmAccessor, checkTable))  
					.append(".").append(nameConverter.convertAttributeName(Constants.CW_TT_COLUMN_PREFIX_SOURCE + checkField)); //$NON-NLS-1$
					
				}
				else {
					// join condition for non-constant values
					joinCondition.append(nameConverter.convertAttributeName(forTable)).append(".").append(nameConverter.convertAttributeName(forKey)).append(" = ")  //$NON-NLS-1$//$NON-NLS-2$
					.append(createModelTranslationTableName(ldmAccessor, checkTable))  
					.append(".").append(nameConverter.convertAttributeName(Constants.CW_TT_COLUMN_PREFIX_SOURCE + checkField)); //$NON-NLS-1$
				}

				/* store the join condition */
				relatedTTJoinConditionMap.put(key, joinCondition);
			} // end of while (dd05qResult.nextRow())

			/*
			 * At this point we have stored all required information
			 * about the referenced translation tables, the involved
			 * translation table columns and the join conditions in the
			 * Maps 'relatedTTColumnMap' and 'relatedTTJoinConditionMap'.
			 * 
			 * We can now search the SAP value tables in our logical
			 * data model and annotate the fields.
			 */

			// Find sap table in logical data model
			Entity tableEntity = dataTableLDMAccessor.findEntity(tableName);

			// sap table fields in logical data model
			EList<Attribute> fields = tableEntity.getAttributes();

			// sanity check
			if (fields == null || fields.isEmpty()) {
				continue;
			}

			Iterator<Attribute> fieldIterator = fields.iterator();

			/* iterate over fields of the SAP value table and create annotations
			 * for translation table, translation table column and join
			 * conditions */
			while(fieldIterator.hasNext()) {
				Attribute field = fieldIterator.next();
				
				// don't create join conditions for columns that are not to be processed
//				if(ignoreColumnsSet.contains(field.getName())) {
//					continue;
//				}

				// check if field has a reference to a check table
				String relatedCheckTableName = LdmAccessor.getAnnotationValue(field,
																								  com.ibm.is.sappack.gen.common.Constants.ANNOT_RELATED_CHECKTABLE);

				// ignore fields without check table reference
				if (relatedCheckTableName == null) {
					continue;
				}

				// retrieve translation table column from relatedCheckTableColumnMap
				String mapKey   = String.format(RELATED_TT_COL_MAP_KEY_TEMPLATE,
                                            tableName, field.getName(), relatedCheckTableName);
				String ttColumn = relatedTTColumnMap.get(mapKey);

				// sanity test - might happen if checktable = fortable
				if(ttColumn == null) {
					continue;
				}

				mapKey = String.format(CHECK_TABLE_TYPE_MAP_KEY_TEMPLATE, tableName, field.getName());

				// annotate related check table type - ReferenceCheckTable or NonReferenceCheckTable
				String checkTableType = relatedCheckTableTypeMap.get(mapKey);
				ldmAccessor.addAnnotation(field, com.ibm.is.sappack.gen.common.Constants.ANNOT_SAPPACK_CHECK_TBL_DATA_OBJECT_SOURCE, checkTableType);
				
				// annotate related translation table
				String translationTable = nameConverter.convertAttributeName(Constants.CW_TT_PREFIX + relatedCheckTableName);
				ldmAccessor.addAnnotation(field, com.ibm.is.sappack.gen.common.Constants.ANNOT_RELATED_TT, translationTable);

				// annotate related translation table column
				String translationTableColumn = nameConverter.convertAttributeName(Constants.CW_TT_COLUMN_PREFIX_TARGET + ttColumn);
				ldmAccessor.addAnnotation(field, com.ibm.is.sappack.gen.common.Constants.ANNOT_RELATED_TT_COLUMN, translationTableColumn);

				// annotate related translation table join condition on sap table column
				String joinCond = null;
				StringBuffer buf = relatedTTJoinConditionMap.get(mapKey);
				if ( buf != null) {
					joinCond = buf.toString();
				}

				// sanity test - should not happen
				if(joinCond == null) {
					continue;
				}

				// annotate related translation table join condition
				ldmAccessor.addAnnotation(field, com.ibm.is.sappack.gen.common.Constants.ANNOT_RELATED_TT_JOIN, joinCond);
			} // end of while(fieldIterator.hasNext())		

		} // end of while (sapTableIterator.hasNext())
	}

}
