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

import java.util.logging.Level;

import com.ibm.db.models.logical.Attribute;
import com.ibm.db.models.logical.Entity;
import com.ibm.db.models.logical.Package;
import com.ibm.iis.sappack.gen.common.ui.connections.SapSystem;
import com.ibm.iis.sappack.gen.common.ui.jco.RfcDestinationDataProvider;
import com.ibm.is.sappack.gen.common.ui.preferences.AdvancedSettingsConstants;
import com.ibm.is.sappack.gen.common.ui.preferences.AdvancedSettingsPreferencePage;
import com.ibm.is.sappack.gen.tools.sap.activator.Activator;
import com.ibm.is.sappack.gen.tools.sap.constants.Constants;
import com.ibm.is.sappack.gen.tools.sap.importer.helper.field.TableField;
import com.ibm.is.sappack.gen.tools.sap.importer.helper.model.LdmAccessor;
import com.ibm.is.sappack.gen.tools.sap.model.SapTable;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoRepository;
import com.sap.conn.jco.JCoTable;

public class TableImporterBase {
	private String refTableDevClassList;

	protected SapSystem sapSystem;
	protected JCoDestination destination;
	protected JCoFunction function_DDIF_FIELDINFO_GET;
	protected JCoFunction function_TABLE_GET_TEXTTABLE;
	protected LdmAccessor ldmAccessor;
	protected Package targetPackage;


	static String copyright() {
		return com.ibm.is.sappack.gen.tools.sap.utilities.Copyright.IBM_COPYRIGHT_SHORT;
	}


	protected TableImporterBase(SapSystem sapSystem, LdmAccessor ldmAccessor, Package ldmPackage) {
		try {
			this.sapSystem = sapSystem;
			this.ldmAccessor = ldmAccessor;
			this.targetPackage = ldmPackage;
			this.destination = RfcDestinationDataProvider.getDestination(this.sapSystem);
			JCoRepository repository = this.destination.getRepository();
			this.function_DDIF_FIELDINFO_GET = repository.getFunction(Constants.JCO_FUNCTION_DDIF_FIELDINFO_GET);
			this.function_TABLE_GET_TEXTTABLE = repository.getFunction(Constants.JCO_FUNCTION_TABLE_GET_TEXTTABLE);
		} catch (JCoException e) {
			throw new RuntimeException(e);
		}

		refTableDevClassList = AdvancedSettingsPreferencePage.getSetting(AdvancedSettingsConstants.ADSET_REF_TABLE_DEV_CLASS_LIST);
		if (refTableDevClassList != null) {
			Activator.getLogger().info("Custom Reference table dev class list = " + refTableDevClassList);
		}
	}

	protected void trace(String s) {
		// System.out.println("JLTTrace: " + s);
		Activator.getLogger().log(Level.FINEST, s);
	}

	/**
	 * adds the column to the entity. 
	 * Returns an ABAPColumn object which can be used for code generators
	 */
	protected ABAPColumn addColumn(String originalTable, Entity entity, JCoTable tableDFIES, String columnPrefix, boolean dontSetKeyAttribute, String objectSourceAnnotationValue) {
		String sapDataType = tableDFIES.getString(Constants.JCO_PARAMETER_DATATYPE);
		char sapBaseDataType = tableDFIES.getChar(Constants.JCO_PARAMETER_INTTYPE);
		int length = tableDFIES.getInt(Constants.JCO_PARAMETER_LENG);
		int intlen = tableDFIES.getInt(Constants.JCO_PARAMETER_INTLEN);
		int decimals = tableDFIES.getInt(Constants.JCO_PARAMETER_DECIMALS);
		String originalSAPFieldName = tableDFIES.getString(Constants.JCO_PARAMETER_FIELDNAME);
		String columnName = columnPrefix + originalSAPFieldName;
		//columnName = cleanFieldName(columnName);
		String dataElement = tableDFIES.getString(Constants.JCO_PARAMETER_ROLLNAME);
		String domainName = tableDFIES.getString(Constants.JCO_PARAMETER_DOMNAME);
		boolean isKeyColumn = false;
		if (!dontSetKeyAttribute) {
			isKeyColumn = tableDFIES.getString(Constants.JCO_PARAMETER_KEYFLAG).equalsIgnoreCase(Constants.JCO_PARAMETER_VALUE_TRUE);
		}

		trace("Adding column " + columnName + ", length: " + length); //$NON-NLS-1$ //$NON-NLS-2$ 

		TableField tableField = new TableField(columnName, null, null, dataElement, domainName, sapDataType, sapBaseDataType, length, intlen, decimals, isKeyColumn, true);
		tableField.setOriginalSAPFieldName(originalSAPFieldName);
		ABAPColumn col = new ABAPColumn(originalTable, tableField);
		Attribute attr = ldmAccessor.addColumnMetadataToTable(entity, tableField);
		if (attr == null) {
			trace("attribute already exists"); //$NON-NLS-1$
			attr = ldmAccessor.findAttribute(entity, columnName);
		}

		col.setAttribute(attr);
		ldmAccessor.addAnnotation(attr, com.ibm.is.sappack.gen.common.Constants.ANNOT_DATA_OBJECT_SOURCE, objectSourceAnnotationValue);
		
		// need to add a derivation since we manipulated the columnName ('/' -> '_')
		String cleanedColName = ldmAccessor.getNameConverter().convertAttributeName(columnName);
		ldmAccessor.addAnnotation(attr, com.ibm.is.sappack.gen.common.Constants.ANNOT_COLUMN_DERIVATION_EXPRESSION, cleanedColName);
		ldmAccessor.addAnnotation(attr, com.ibm.is.sappack.gen.common.Constants.ANNOT_COLUMN_IS_UNICODE, Boolean.toString(this.sapSystem.isUnicode()));
		ldmAccessor.addAnnotation(attr, com.ibm.is.sappack.gen.common.Constants.ANNOT_TRANSFORMER_SOURCE_MAPPING, cleanedColName);
		ldmAccessor.addAnnotation(attr, com.ibm.is.sappack.gen.common.Constants.ANNOT_SAP_COLUMN_NAME, columnName);

		trace("Column added"); //$NON-NLS-1$
		return col;
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
	protected JCoTable queryTableMetadata(String tableName) throws JCoException {
		try {
			this.function_DDIF_FIELDINFO_GET.getImportParameterList().setValue(Constants.JCO_PARAMETER_TABNAME, tableName);
			this.function_DDIF_FIELDINFO_GET.execute(this.destination);

			JCoTable metadataTable = this.function_DDIF_FIELDINFO_GET.getTableParameterList().getTable(Constants.JCO_RESULTTABLE_DFIES_TAB);
			return metadataTable;
		} catch (JCoException e) {
			if (e.getGroup() == JCoException.JCO_ERROR_ABAP_EXCEPTION && e.getKey().equalsIgnoreCase(Constants.JCO_ERROR_KEY_NOT_FOUND)) {
				// this means we have an invalid check table
				// log the exception and skip the check table
				Activator.getLogger().log(Level.SEVERE, e.getMessage(), e);
				return null;
			}
			throw e;
		}

	}

	protected static String cleanFieldName(String fieldName) {
		if (fieldName == null || fieldName.trim().length() == 0) {
			return fieldName;
		}
		String cleanedName = fieldName.replace('/', '_');
		// if (cleanedName.charAt(0) == '_') {
		// cleanedName = 'A' + cleanedName.substring(1);
		// }
		return cleanedName;

	}

	public String determineCheckTableType(SapTable checkTable) {
		String devClass = checkTable.getDevClass();
		String retChkTableType;

		// default check table type
		retChkTableType = com.ibm.is.sappack.gen.common.Constants.DATA_OBJECT_SOURCE_TYPE_NON_REFERENCE_CHECK_TABLE;

		// check if there an 'advanced setting' REF_TABLE_DEV_CLASSES
		if (this.refTableDevClassList == null) {
			// NO user list ==> default handling
			if ("C".equals(devClass)   ||  //$NON-NLS-1$ 
			    "E".equals(devClass)   ||  //$NON-NLS-1$
			    "G".equals(devClass)   ||  //$NON-NLS-1$
			    "S".equals(devClass)) {    //$NON-NLS-1$
				retChkTableType = com.ibm.is.sappack.gen.common.Constants.DATA_OBJECT_SOURCE_TYPE_REFERENCE_CHECK_TABLE;		
			}
		}
		else {
			// USER values ==> check list values
			for (String chkDevClass : this.refTableDevClassList.split(Constants.REF_TABLE_DEV_CLASS_LIST_SEPARATOR)) {
				if (chkDevClass.equals(devClass)) { 
					retChkTableType = com.ibm.is.sappack.gen.common.Constants.DATA_OBJECT_SOURCE_TYPE_REFERENCE_CHECK_TABLE;
					break;
				}
			}
		}

		return(retChkTableType);
	}
}
