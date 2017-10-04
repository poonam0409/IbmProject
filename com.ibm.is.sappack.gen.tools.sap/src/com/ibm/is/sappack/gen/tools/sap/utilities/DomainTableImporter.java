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
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;

import com.ibm.db.models.logical.Entity;
import com.ibm.db.models.logical.Package;
import com.ibm.iis.sappack.gen.common.ui.connections.SapSystem;
import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.tools.sap.constants.Constants;
import com.ibm.is.sappack.gen.tools.sap.importer.helper.field.AbstractField;
import com.ibm.is.sappack.gen.tools.sap.importer.helper.field.TechnicalFieldVarchar;
import com.ibm.is.sappack.gen.tools.sap.importer.helper.model.LdmAccessor;
import com.sap.conn.jco.JCoTable;

/**
 * DomainTableImporter 
 * 
 */
public class DomainTableImporter extends TableImporterBase {

	static String copyright() {
		return com.ibm.is.sappack.gen.tools.sap.utilities.Copyright.IBM_COPYRIGHT_SHORT;
	}

	/**
	 * DomainTableImporter
	 * 
	 * @param sapSystem
	 * @param ldmAccessor
	 * @param ldmPackage
	 */
	public DomainTableImporter(SapSystem sapSystem, LdmAccessor ldmAccessor, Package ldmPackage) {
		
		super(sapSystem, ldmAccessor, ldmPackage);
	}
	
	public static String createModelDomainTableName(String domainName) {
		return MessageFormat.format(Constants.CW_DOMAIN_TABLE_TEMPLATE, new Object[] { cleanFieldName(domainName) });
	}

	public static String createModelDomainTranslationTableName(String domainName) {
		return MessageFormat.format(Constants.CW_DOMAIN_TRANSLATION_TABLE_TEMPLATE, new Object[] { cleanFieldName(domainName) });
	}
	
	public static String createModelDomainTranslationTableColumnName(String domainName) {
		 return com.ibm.is.sappack.gen.common.Constants.TARGET_COLUMN_PREFIX + cleanFieldName(domainName);
	}
	
	public static String createModelDomainTranslationTableJoinCondition(String tableName, String colName, String domainName) {
		
		StringBuffer sb = new StringBuffer();
		sb.append(cleanFieldName(tableName)).append("."); //$NON-NLS-1$
		sb.append(cleanFieldName(colName)).append(" = "); //$NON-NLS-1$
		sb.append(createModelDomainTranslationTableName(cleanFieldName(domainName))).append("."); //$NON-NLS-1$
		sb.append(com.ibm.is.sappack.gen.common.Constants.SOURCE_COLUMN_PREFIX).append(cleanFieldName(domainName));
		
		return sb.toString();
	}
	
	/**
	 * createDomainTables
 	 *
 	 * create empty domain tables for all domain
 	 * value fields (that have fixed values) 
 	 * in the given list of logical SAP tables
 	 *
	 * @param tableMetadataMap
	 * @param progressMonitor
	 */
	public void createDomainTables(Map<String, JCoTable> tableMetadataMap, IProgressMonitor progressMonitor) {
		
		Set<String> tableNames = tableMetadataMap.keySet();
		Iterator<String> tableNamesIt = tableNames.iterator();
		
		trace("createDomainTables sapLogicalTables: " + tableNames.size() + "(" + tableNames + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	
		/* for each logical SAP table */
		while(tableNamesIt.hasNext()) {
			if (progressMonitor.isCanceled()) {
				return;
			}
	
			String tableName = tableNamesIt.next();
			JCoTable tableMetadata = tableMetadataMap.get(tableName);
			tableMetadata.firstRow();
			
			/* iterate over all fields of the SAP logical table and create domain tables if domain has fixed values */
			do {
				String domainName = tableMetadata.getString(Constants.JCO_PARAMETER_DOMNAME);
				boolean domainHasFixedValues = tableMetadata.getString(Constants.JCO_PARAMETER_DOMAIN_FIXED_VALUES).contains(Constants.JCO_PARAMETER_VALUE_TRUE);
				
				if(domainName != null && !domainName.equals(Constants.EMPTY_STRING) && domainHasFixedValues) {
					
					/* assemble domain table name */
					String dtName = createModelDomainTableName(domainName);
					
					/* assemble domain translation table name */
					String dttName = createModelDomainTranslationTableName(domainName);
					
					/* create a domain table for the domain of this field */
					Entity dt = null;
					if (ldmAccessor.findEntity(this.targetPackage, dtName) == null) {
						
						String dtDesc = MessageFormat.format(Messages.DomainTableImporter_0, domainName);
						dt = ldmAccessor.createEntity(targetPackage, dtName, dtDesc);
						trace("Creating domain translation table " + dtName); //$NON-NLS-1$ 
						ldmAccessor.addAnnotation(dt, com.ibm.is.sappack.gen.common.Constants.ANNOT_DATATYPE_DOMAIN, domainName);
						ldmAccessor.addAnnotation(dt, com.ibm.is.sappack.gen.common.Constants.ANNOT_DOMAIN_TRANSLATION_TABLE, dttName);
						ldmAccessor.addAnnotation(dt, com.ibm.is.sappack.gen.common.Constants.ANNOT_DATA_OBJECT_SOURCE, com.ibm.is.sappack.gen.common.Constants.DATA_OBJECT_SOURCE_TYPE_DOMAIN_TABLE);
						ldmAccessor.addAnnotation(dt, com.ibm.is.sappack.gen.common.Constants.ANNOT_GENERATED_MODEL_VERSION, com.ibm.is.sappack.gen.common.Constants.MODEL_VERSION);
					
						/* VALPOS field */
						AbstractField valpos = new TechnicalFieldVarchar("VALPOS", Messages.DomainTableImporter_1, 4, true, false); //$NON-NLS-1$
						ldmAccessor.addColumnMetadataToTable(dt, valpos);
						
						/* DOMVALUE_L field */
						AbstractField domvalue_l = new TechnicalFieldVarchar("DOMVALUE_L", Messages.DomainTableImporter_2, 10, false, false); //$NON-NLS-1$
						ldmAccessor.addColumnMetadataToTable(dt, domvalue_l);	
						
						/* DOMVALUE_H field */
						AbstractField domvalue_h = new TechnicalFieldVarchar("DOMVALUE_H", Messages.DomainTableImporter_3, 10, false, true); //$NON-NLS-1$
						ldmAccessor.addColumnMetadataToTable(dt, domvalue_h);
						
						/* DDTEXT */
						AbstractField ddText = new TechnicalFieldVarchar("DDTEXT", Messages.DomainTableImporter_4, 60, false, true); //$NON-NLS-1$
						ldmAccessor.addColumnMetadataToTable(dt, ddText);

					}
				}

			} while (tableMetadata.nextRow());
		}
		
	}
	
	
	/**
	 * createDomainTranslationTables
	 * 
	 * create empty domainTranslationTables for all domain value fields
	 * (that have fixed values) in the given list of logical SAP tables
	 * 
	 * @param tableMetadataMap
	 * @param progressMonitor
	 */
	public void createDomainTranslationTables(Map<String, JCoTable> tableMetadataMap,  IProgressMonitor progressMonitor) {
		
		Set<String> tableNames = tableMetadataMap.keySet();
		Iterator<String> tableNamesIt = tableNames.iterator();
		
		trace("createDomainTranslationTables sapLogicalTables: " + tableNames.size() + "(" + tableNames + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	
		/* for each logical SAP table */
		while(tableNamesIt.hasNext()) {
			if (progressMonitor.isCanceled()) {
				return;
			}
	
			String tableName = tableNamesIt.next();
			JCoTable tableMetadata = (JCoTable) tableMetadataMap.get(tableName).clone();
			tableMetadata.firstRow();
			
			/* iterate over all fields of the SAP logical table and create domain translation tables if necessary */
			do {
				String domainName = tableMetadata.getString(Constants.JCO_PARAMETER_DOMNAME);
				boolean domainHasFixedValues = tableMetadata.getString(Constants.JCO_PARAMETER_DOMAIN_FIXED_VALUES).contains(Constants.JCO_PARAMETER_VALUE_TRUE);
				/* replace fieldName with domainName - the fields in the domain translation table have to reflect the domain name */
				tableMetadata.setValue(Constants.JCO_PARAMETER_FIELDNAME, domainName);
	
				if(domainName != null && !domainName.equals(Constants.EMPTY_STRING) && domainHasFixedValues) {
					
					/* assemble domain table name */
					String dtName = createModelDomainTableName(domainName);
					
					/* assemble domain translation table name */
					String dttName = createModelDomainTranslationTableName(domainName);
					
					/* create a domain translation table for the domain of this field */
					Entity dtt = null;
					if (ldmAccessor.findEntity(this.targetPackage, dttName) == null) {
						
						String dttDesc = MessageFormat.format(Messages.DomainTableImporter_5, domainName);
						dtt = ldmAccessor.createEntity(targetPackage, dttName, dttDesc);
						trace("Creating domain translation table " + dttName); //$NON-NLS-1$ 
						ldmAccessor.addAnnotation(dtt, com.ibm.is.sappack.gen.common.Constants.ANNOT_DATATYPE_DOMAIN, domainName);
						ldmAccessor.addAnnotation(dtt, com.ibm.is.sappack.gen.common.Constants.ANNOT_DOMAIN_TABLE, dtName);
						ldmAccessor.addAnnotation(dtt, com.ibm.is.sappack.gen.common.Constants.ANNOT_DATA_OBJECT_SOURCE, com.ibm.is.sappack.gen.common.Constants.DATA_OBJECT_SOURCE_TYPE_DOMAIN_TRANSLATION_TABLE);
						ldmAccessor.addAnnotation(dtt, com.ibm.is.sappack.gen.common.Constants.ANNOT_GENERATED_MODEL_VERSION, com.ibm.is.sappack.gen.common.Constants.MODEL_VERSION);
					
						/* SOURCE_LEGACY_ID field */
						AbstractField technicalFieldSourceLegacyId = new TechnicalFieldVarchar("SOURCE_LEGACY_ID", Messages.JoinedCheckAndTextTableImporter_4, 20, true, false); //$NON-NLS-1$
						ldmAccessor.addColumnMetadataToTable(dtt, technicalFieldSourceLegacyId);

						/* TARGET_LEGACY_ID field */
						AbstractField technicalFieldTargetLegacyId = new TechnicalFieldVarchar("TARGET_LEGACY_ID", Messages.JoinedCheckAndTextTableImporter_5, 20, true, false); //$NON-NLS-1$
						ldmAccessor.addColumnMetadataToTable(dtt, technicalFieldTargetLegacyId);

						/* SOURCE_DESCRIPTION field */
						AbstractField technicalFieldSourceDescription = new TechnicalFieldVarchar("SOURCE_DESCRIPTION", Messages.JoinedCheckAndTextTableImporter_6, 250, false, true); //$NON-NLS-1$
						ldmAccessor.addColumnMetadataToTable(dtt, technicalFieldSourceDescription);
						
						/* SOURCE domain value - has to be part of the primary key to allow 
						 * multiple source/target mappings for the system ID in
						 * the domain translation table 
						 */
						tableMetadata.setValue(Constants.JCO_PARAMETER_KEYFLAG, Constants.JCO_PARAMETER_VALUE_TRUE);
						addColumn(Constants.CW_TT_COLUMN_PREFIX_SOURCE + domainName, dtt, tableMetadata, com.ibm.is.sappack.gen.common.Constants.SOURCE_COLUMN_PREFIX, false, com.ibm.is.sappack.gen.common.Constants.DATA_OBJECT_SOURCE_TYPE_DOMAIN_TRANSLATION_TABLE);
						
						/* TARGET domain value */
						addColumn(Constants.CW_TT_COLUMN_PREFIX_TARGET + domainName, dtt, tableMetadata, com.ibm.is.sappack.gen.common.Constants.TARGET_COLUMN_PREFIX, true, com.ibm.is.sappack.gen.common.Constants.DATA_OBJECT_SOURCE_TYPE_DOMAIN_TRANSLATION_TABLE); 
					
					} 
				}
	
			} while (tableMetadata.nextRow());
		}
	}
	
}
