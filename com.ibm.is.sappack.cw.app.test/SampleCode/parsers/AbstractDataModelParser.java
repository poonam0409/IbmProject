package com.ibm.is.sappack.cw.app.services.rdm.parsers;

import java.util.HashMap;
import java.util.logging.Logger;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ibm.is.sappack.cw.app.data.rdm.GenericTable;
import com.ibm.is.sappack.cw.app.data.rdm.ReferenceTableType;
import com.ibm.is.sappack.cw.app.data.rdm.jpa.ReferenceTable;
import com.ibm.is.sappack.cw.app.data.rdm.jpa.ReferenceTableFullName;
import com.ibm.is.sappack.cw.app.services.CwApp;
import com.ibm.is.sappack.cw.app.services.SchemaMismatchException;
import com.ibm.is.sappack.cw.app.services.rdm.DBOperations;
import com.ibm.is.sappack.gen.common.Constants;

public abstract class AbstractDataModelParser {

	private final static String CLASS_NAME = AbstractDataModelParser.class.getName();
	private Logger logger;
	
	protected static final String ROOT_NODE = "xmi:xmi";

	protected static final String ATTRIBUTE_NAME = "name";
	protected static final String ATTRIBUTE_LABEL = "label";
	protected static final String ANNOTATIONS = "eAnnotations";
	protected static final String COLUMNS = "columns";

	private static final String ANNOTATION_DETAILS = "details";

	private static final String ANNOTATION_KEY = "key";
	private static final String ANNOTATION_VALUE = "value";

	private static final String ANNOTATION_KEY_SAP_CHECKTABLE_NAME = Constants.ANNOT_CHECK_TABLE_NAME;
	private static final String ANNOTATION_KEY_SAP_DOMAINTABLE_NAME = Constants.ANNOT_DATATYPE_DOMAIN;

	private static final String ANNOTATION_KEY_CHECKTABLE_TRANSCODING_TABLE = Constants.ANNOT_TRANSLATION_TABLE_NAME;
	private static final String ANNOTATION_KEY_DOMAINTABLE_TRANSCODING_TABLE = Constants.ANNOT_DOMAIN_TRANSLATION_TABLE;

	private static final String ANNOTATION_KEY_REFERENCE_TABLE = Constants.ANNOT_DATA_OBJECT_SOURCE;
	private static final String ANNOTATION_VALUE_CHECK_TABLE = Constants.DATA_OBJECT_SOURCE_TYPE_REFERENCE_CHECK_TABLE;
	private static final String ANNOTATION_VALUE_DOMAIN_TABLE = Constants.DATA_OBJECT_SOURCE_TYPE_DOMAIN_TABLE;
	private static final String ANNOTATION_VALUE_TEXT_TABLE = Constants.DATA_OBJECT_SOURCE_TYPE_REFERENCE_TEXT_TABLE;

	private static final String ANNOTATION_KEY_TEXTTABLE_NAME = Constants.ANNOT_MODEL_TEXT_TABLE_NAME;
	private static final String ANNOTATION_KEY_SAP_TEXTTABLE_NAME = Constants.ANNOT_TEXT_TABLE_NAME;
	private static final String ANNOTATION_KEY_CHECKTABLE_TEXTTABLE_JOINCONDITION = Constants.ANNOT_CHECKTABLE_TEXTTABLE_JOINCONDITION;
	
	protected static final String ANNOTATION_KEY_SAP_COLUMN_NAME = Constants.ANNOT_SAP_COLUMN_NAME;
	protected static final String ANNOTATION_KEY_SAP_DATATYPE_DOMAIN = Constants.ANNOT_DATATYPE_DOMAIN;
	protected static final String ANNOTATION_KEY_TT_SRC_FIELD = Constants.ANNOT_TRANSCODING_TBL_SRC_FLD;
	protected static final String ANNOTATION_KEY_TT_TGT_FIELD = Constants.ANNOT_TRANSCODING_TBL_TRG_FLD;
	protected static final String ANNOTATION_KEY_SAP_RELATED_CHECKTABLE = Constants.ANNOT_RELATED_CHECKTABLE;
	protected static final String ANNOTATION_KEY_SAP_RELATED_CT_JOIN = Constants.ANNOT_RELATED_CT_JOIN;
	
	protected HashMap<String, GenericTable> referenceTableMap;
	protected HashMap<String, GenericTable> textTableMap;

	public AbstractDataModelParser() {
		this.logger = CwApp.getLogger();
		final String METHOD_NAME = "AbstractDataModelParser()";
		logger.entering(CLASS_NAME, METHOD_NAME);
		
		this.referenceTableMap = new HashMap<String, GenericTable>();
		this.textTableMap = new HashMap<String, GenericTable>();
		
		logger.exiting(CLASS_NAME, METHOD_NAME);
	}

	protected void handleTableAnnotations(GenericTable genericTable, Node annotationsNode) {
		final String METHOD_NAME = "handleTableAnnotations(GenericTable genericTable, Node annotationsNode)";
		logger.entering(CLASS_NAME, METHOD_NAME);
		
		NodeList nodeList = annotationsNode.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node currentNode = nodeList.item(i);
			if (currentNode.getNodeName().equalsIgnoreCase(ANNOTATION_DETAILS)) {
				NamedNodeMap attributes = currentNode.getAttributes();
				String key = attributes.getNamedItem(ANNOTATION_KEY).getTextContent();
				String value = attributes.getNamedItem(ANNOTATION_VALUE).getTextContent();

				// SAP storage class
				if (key.equalsIgnoreCase(ANNOTATION_KEY_REFERENCE_TABLE)) {
					if (value.equalsIgnoreCase(ANNOTATION_VALUE_CHECK_TABLE)) {
						genericTable.setIsReferenceTable();
						genericTable.setReferenceTableType(ReferenceTableType.CHECK_TABLE);
					} else if (value.equalsIgnoreCase(ANNOTATION_VALUE_DOMAIN_TABLE)) {
						genericTable.setIsReferenceTable();
						genericTable.setReferenceTableType(ReferenceTableType.DOMAIN_TABLE);
					} else if (value.equalsIgnoreCase(ANNOTATION_VALUE_TEXT_TABLE)) {
						genericTable.setIsTextTable();
					}
				}

				// SAP name of check table (name of the table as it is used on the SAP side)
				if (key.equalsIgnoreCase(ANNOTATION_KEY_SAP_CHECKTABLE_NAME)) {
					logger.finer("Found check table name annotation, value: " + value);
					genericTable.setSapName(value);
					continue;
				}
				
				// SAP name of domain table (name of the table as it is used on the SAP side)
				if (key.equalsIgnoreCase(ANNOTATION_KEY_SAP_DOMAINTABLE_NAME)) {
					logger.finer("Found domain table name annotation, value: " + value);
					genericTable.setSapName(value);
					continue;
				}

				// transcoding table for check table  
				if (key.equalsIgnoreCase(ANNOTATION_KEY_CHECKTABLE_TRANSCODING_TABLE)) {
					String transcodingTableName = value;

					// Add the name of the transcoding table
					genericTable.setTranscodingTableName(transcodingTableName);
					genericTable.setTranscodingTableSchema(DBOperations.getCwSchemaFromCWDB(com.ibm.is.sappack.cw.app.services.Constants.CW_SCHEMAS_AREACODE_ALG0));
					continue;
				}
				
				// transcoding table for domain table  
				if (key.equalsIgnoreCase(ANNOTATION_KEY_DOMAINTABLE_TRANSCODING_TABLE)) {
					String transcodingTableName = value;

					// Add the name of the transcoding table
					genericTable.setTranscodingTableName(transcodingTableName);
					genericTable.setTranscodingTableSchema(DBOperations.getCwSchemaFromCWDB(com.ibm.is.sappack.cw.app.services.Constants.CW_SCHEMAS_AREACODE_ALG0));
					continue;
				}

				// Name of the text table (as used in the model/db)
				if (key.equalsIgnoreCase(ANNOTATION_KEY_TEXTTABLE_NAME)) {
					genericTable.setTextTableName(value);
					genericTable.setTextTableSchema(genericTable.getSchema());
					continue;
				}

				// Name of the text table (as used in SAP)
				if (key.equalsIgnoreCase(ANNOTATION_KEY_SAP_TEXTTABLE_NAME)) {
					genericTable.setTextTableSapName(value);
					continue;
				}

				// Join Condition
				if (key.equalsIgnoreCase(ANNOTATION_KEY_CHECKTABLE_TEXTTABLE_JOINCONDITION)) {
					genericTable.setTextTableJoinCondition(value);
					continue;
				}
			}
		}

		// Check whether table is a reference table, or text table (other table types will be ignored)
		if (genericTable.isReferenceTable()) {
			logger.finest("Adding reference table to map: " + genericTable.getName());
			this.referenceTableMap.put(genericTable.getName(), genericTable);
		} else if (genericTable.isTextTable()) {
			logger.finest("Adding text table to map: " + genericTable.getName());
			this.textTableMap.put(genericTable.getName(), genericTable);
		}
		logger.exiting(CLASS_NAME, METHOD_NAME);
	}
	
	protected String getColumnAnnotation(Node node, String key) {
		NodeList annotationNodes = node.getChildNodes();
		
		for (int i = 0; i < annotationNodes.getLength(); i++) {
			Node annotationNode = annotationNodes.item(i);
			NamedNodeMap annotationAttributes = annotationNode.getAttributes();
			if (annotationAttributes == null) {
				continue;
			}
			
			Node annotationKey = annotationAttributes.getNamedItem(ANNOTATION_KEY);
			if (annotationKey.getNodeValue().equalsIgnoreCase(key)) {
				Node annotationValue = annotationAttributes.getNamedItem(ANNOTATION_VALUE);
				return annotationValue.getTextContent();
			}
		}
		return null;
	}
	
	protected void checkForSchemaNameCompliance(String schemaName, String areaCode) throws SchemaMismatchException {
		if (!DBOperations.getCwSchemaFromCWDB(areaCode).equalsIgnoreCase(schemaName)) {
			throw new SchemaMismatchException(schemaName, areaCode);
		}
	}

	public HashMap<ReferenceTableFullName, ReferenceTable> getReferenceTables() {
		HashMap<ReferenceTableFullName , ReferenceTable> referenceTables = new HashMap<ReferenceTableFullName, ReferenceTable>();
		
		for (GenericTable genericReferenceTable: this.referenceTableMap.values()) {
			ReferenceTable referenceTable = new ReferenceTable(genericReferenceTable);
			referenceTables.put(referenceTable.getReferenceTableFullName(), referenceTable);
			
			if (referenceTable.getTextTable() == null) {
				continue;
			}
			
			GenericTable genericTextTable = this.textTableMap.get(referenceTable.getTextTable().getName());
			referenceTable.mixinGenericTextTable(genericTextTable);
		}
		
		return referenceTables;
	}
	
	public boolean hasReferenceTables() {
		if (this.referenceTableMap.size() > 0) {
			return true;
		}
		
		return false;
	}	
}
