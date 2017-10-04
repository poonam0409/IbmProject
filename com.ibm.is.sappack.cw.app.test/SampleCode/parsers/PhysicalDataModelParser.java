package com.ibm.is.sappack.cw.app.services.rdm.parsers;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.ibm.is.sappack.cw.app.data.rdm.GenericColumn;
import com.ibm.is.sappack.cw.app.data.rdm.GenericTable;
import com.ibm.is.sappack.cw.app.data.rdm.ReferenceTableType;
import com.ibm.is.sappack.cw.app.data.rdm.jpa.SourceDataCollectionRule;
import com.ibm.is.sappack.cw.app.services.Constants;
import com.ibm.is.sappack.cw.app.services.CwApp;
import com.ibm.is.sappack.cw.app.services.SchemaMismatchException;

public class PhysicalDataModelParser extends AbstractDataModelParser {

	private final static String CLASS_NAME = PhysicalDataModelParser.class.getName();
	private Logger logger;
	
	public static final String EMPTY_STRING = "";
	public static final String FILENAME_EXTENSION = ".dbm";

	private static final String DB2_NAMESPACE = "LUW";
	private static final String DB2_TABLE = "LUWTable";
	private static final String DB2_SCHEMA = "DB2Model:DB2Schema";

	private static final String ORACLE_NAMESPACE = "OracleModel";
	private static final String ORACLE_TABLE = "OracleTable";
	private static final String ORACLE_SCHEMA = "SQLSchema:Schema";

	protected static final String CONSTRAINTS = "constraints";
	protected static final String PRIMARY_KEY_CONSTRAINT = "SQLConstraints:PrimaryKey";
	protected static final String CONSTRAINT_MEMBERS = "members";

	private static final String ATTRIBUTE_XMI_ID = "xmi:id";
	private static final String ATTRIBUTE_NAME = "name";
	private static final String ATTRIBUTE_LENGTH = "length";
	private static final String ATTRIBUTE_SCHEMA = "schema";

	private static final String CONTAINED_TYPE = "containedType";
	
	private static final String E_ANNOTATIONS =	"eAnnotations";
	
	// Apparently it is a good idea to have a default column length in model files.
	private static final int DEFAULT_COLUMN_LENGTH = 1;

	// key = schema id
	// value = schema name;
	private HashMap<String, String> schemaMap;
	
	private List<SourceDataCollectionRule> rules;
	public PhysicalDataModelParser() {
		this.logger = CwApp.getLogger();
		final String METHOD_NAME = "PhysicalDataModelParser()";
		logger.entering(CLASS_NAME, METHOD_NAME);

		schemaMap = new HashMap<String, String>();
		rules = new ArrayList<SourceDataCollectionRule>();
		
		logger.exiting(CLASS_NAME, METHOD_NAME);
	}
	
	public void parseModel(InputStream inputStream) throws SchemaMismatchException, ParserConfigurationException, SAXException, IOException {
		final String METHOD_NAME = "parseModel(InputStream inputStream)";
		logger.entering(CLASS_NAME, METHOD_NAME);
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(new InputSource(inputStream));
		Node rootNode = document.getDocumentElement();

		// Parse the model
		evaluateNode(rootNode);
		
		logger.finer("Finding matches for source data processing rules...");
		for (SourceDataCollectionRule rule : rules) {
			logger.finest("Rule: Data table: " + rule.getDataTableName() + ", Check table: " + rule.getRelatedCheckTableName());
			Set<Entry<String, GenericTable>> refTableEntrySet = referenceTableMap.entrySet();
			Iterator<Entry<String, GenericTable>> it = refTableEntrySet.iterator();
			while (it.hasNext()) {
				Entry<String, GenericTable> refTableEntry = it.next();
				if (refTableEntry.getValue().getSapName().equalsIgnoreCase(rule.getRelatedCheckTableName())) {
					logger.finest("Attaching rule to reference table: " + rule.getRelatedCheckTableName());
					refTableEntry.getValue().addSourceDataCollectionRule(rule);
				}
			}
		}
		logger.exiting(CLASS_NAME, METHOD_NAME);
	}

	private void evaluateNode(Node node) throws SchemaMismatchException {
		final String METHOD_NAME = "evaluateNode(Node node)";
		logger.entering(CLASS_NAME, METHOD_NAME);
		String nodeName = node.getNodeName();

		// If the current DOM node is a "TABLE" element (DB2 table or Oracle table)
		if (nodeName.equalsIgnoreCase(DB2_NAMESPACE + ":" + DB2_TABLE) || nodeName.equalsIgnoreCase(ORACLE_NAMESPACE + ":" + ORACLE_TABLE)) {
			handleTable(node);
		} else if (nodeName.equalsIgnoreCase(DB2_SCHEMA) || nodeName.equalsIgnoreCase(ORACLE_SCHEMA)) {

			// if current node is a SCHEMA element (DB2 or Oracle), we determine
			// the schema name and then iterate over its child elements
			handleSchema(node);
			NodeList nodeList = node.getChildNodes();
			for (int i = 0; i < nodeList.getLength(); i++) {
				evaluateNode(nodeList.item(i));
			}
		} else if (nodeName.equalsIgnoreCase(ROOT_NODE)) {
			NodeList nodeList = node.getChildNodes();
			for (int i = 0; i < nodeList.getLength(); i++) {
				evaluateNode(nodeList.item(i));
			}
		}

		logger.exiting(CLASS_NAME, METHOD_NAME);
	}

	private void handleSchema(Node node) throws SchemaMismatchException {
		
		// <DB2Model:DB2Schema xmi:id="_MMWiUG0IEeGLobq67gfUpQ" name="Schema"
		// tables="_OzY2AG0IEeGLobq67gfUpQ" database="_MJ0ywG0IEeGLobq67gfUpQ">
		NamedNodeMap attributes = node.getAttributes();

		String xmiId = null;
		String schemaName = null;

		Node xmiIdNode = attributes.getNamedItem(ATTRIBUTE_XMI_ID);
		if (xmiIdNode != null) {
			xmiId = xmiIdNode.getTextContent();
		}

		Node nameNode = attributes.getNamedItem(ATTRIBUTE_NAME);
		if (nameNode != null) {
			schemaName = nameNode.getTextContent();
			
			// we need to check whether the schema name is equal to the one configured in the
			// CW context (in CWDB: AUX.CW_SCHEMAS in column PLD)
			// if the schema names don't match an exception is thrown
			checkForSchemaNameCompliance(schemaName, Constants.CW_SCHEMAS_AREACODE_PLD);
		}

		if (xmiId != null && schemaName != null) {
			this.schemaMap.put(xmiId, schemaName);
		}
	}

	private void handleTable(Node entityNode) {
		final String METHOD_NAME = "handleTable(Node entityNode)";
		logger.entering(CLASS_NAME, METHOD_NAME);
		List<GenericColumn> columns = new ArrayList<GenericColumn>();
		HashSet<String> keyColumnIds = new HashSet<String>();

		String tableId = entityNode.getAttributes().getNamedItem(ATTRIBUTE_XMI_ID).getTextContent();
		String tableName = entityNode.getAttributes().getNamedItem(ATTRIBUTE_NAME).getTextContent();
		String tableDescription = entityNode.getAttributes().getNamedItem(ATTRIBUTE_LABEL).getTextContent();
		String schemaId = entityNode.getAttributes().getNamedItem(ATTRIBUTE_SCHEMA).getTextContent();
		String schemaName = this.schemaMap.get(schemaId);

		// As we don't yet know the reference table's name and we don't even
		// know whether the table really is a reference table,
		// we set its name to null (and the name will be set to the correct
		// name later - or in case the table isn't a reference table - it will
		// be disposed)
		GenericTable genericTable = new GenericTable(tableId, schemaName, tableName);
		genericTable.setDescription(tableDescription);

		// Iterate over the table's annotations
		NodeList nodeList = entityNode.getChildNodes();
		
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node currentNode = nodeList.item(i);
			
			if (currentNode.getNodeName().equalsIgnoreCase(ANNOTATIONS)) {
				handleTableAnnotations(genericTable, currentNode);
			} else if (currentNode.getNodeName().equalsIgnoreCase(COLUMNS)) {
				handleColumns(tableName, currentNode, columns, genericTable);
			} else if (currentNode.getNodeName().equalsIgnoreCase(CONSTRAINTS)) {
				handleConstraints(currentNode, keyColumnIds);
			}
		}

		processColumns(genericTable, keyColumnIds, columns);
		logger.exiting(CLASS_NAME, METHOD_NAME);
	}

	private void processColumns(GenericTable genericTable, HashSet<String> keyColumnIds, List<GenericColumn> columns) {
		final String METHOD_NAME = "processColumns(GenericTable genericTable, HashSet<String> keyColumnIds, List<GenericColumn> columns)";
		logger.entering(CLASS_NAME, METHOD_NAME);
		
		// for domain tables we haven't gathered any columns yet as the column handling is totally different
		// from the ones for check tables and text tables
		// in fact for domain tables there is only one relevant column
		if (columns.size() == 0 && genericTable.isReferenceTable() && genericTable.getReferenceTableType() == ReferenceTableType.DOMAIN_TABLE) {
			logger.finest("Processing columns for a domain table: " + genericTable.getSapName());
			GenericColumn column = new GenericColumn(genericTable.getSapName());
			column.setDomain(genericTable.getSapName());
			column.setModelId(EMPTY_STRING);
			column.setSapName(genericTable.getSapName());
			column.setKey(false);
			column.setLength(DEFAULT_COLUMN_LENGTH);
			column.setTranscodingTableSrcName("SOURCE_" + genericTable.getSapName());
			column.setTranscodingTableTgtName("TARGET_" + genericTable.getSapName());
			
			genericTable.addColumn(column);
		} else {
			for (GenericColumn genericColumn : columns) {
				
				// Mark key columns
				if (keyColumnIds.contains(genericColumn.getModelId())) {
					genericColumn.setKey(true);
				}
				
				// For reference tables, we only add columns that are part of the primary key; for Text tables we add all columns
				if ((genericTable.isReferenceTable() && genericTable.getReferenceTableType() == ReferenceTableType.CHECK_TABLE && genericColumn.isKey()) || genericTable.isTextTable()) {
					genericTable.addColumn(genericColumn);
				}
			}
		}
		logger.exiting(CLASS_NAME, METHOD_NAME);
	}
	
	private void handleColumns(String tableName, Node columnsNode, List<GenericColumn> columns, GenericTable table) {
		final String METHOD_NAME = "handleColumns(String tableName, Node columnsNode, List<GenericColumn> columns, GenericTable table)";
		logger.entering(CLASS_NAME, METHOD_NAME);
		logger.finer("Table: " + tableName);
		
		// retrieve name, data type, length and xmi:id for all columns
		// <columns xsi:type="LUW:LUWColumn" xmi:id="_QSAwIG0IEeGLobq67gfUpQ" name="Column1" nullable="false">
		// <containedType xsi:type="SQLDataTypes:CharacterStringDataType" xmi:id="_QSAwIW0IEeGLobq67gfUpQ" name="CHAR" length="5"/>
		// </columns>
		String name = null;
		String sapName = null;
		String xmiId = null;
		String domain = null;
		String relatedCheckTable = null;
		String sourceDataCollectionRule = null;
		String transcodingTableSrcField = null;
		String transcodingTableTgtField = null;
		String length = null;
		
		NamedNodeMap attributes = columnsNode.getAttributes();

		// name
		Node nameNode = attributes.getNamedItem(ATTRIBUTE_NAME);
		if (nameNode != null) {
			name = nameNode.getTextContent();
			logger.finest("Column: " + name);
		}

		// id
		Node xmiIdNode = attributes.getNamedItem(ATTRIBUTE_XMI_ID);
		if (xmiIdNode != null) {
			xmiId = xmiIdNode.getTextContent();
		}

		// data type and length
		NodeList childNodes = columnsNode.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node childNode = childNodes.item(i);
			
			if (childNode.getNodeName().equalsIgnoreCase(CONTAINED_TYPE)) {
				NamedNodeMap containedTypeAttributes = childNode.getAttributes();
				
				if (containedTypeAttributes != null) {
					Node dataTypeLengthNode = containedTypeAttributes.getNamedItem(ATTRIBUTE_LENGTH);
					
					if (dataTypeLengthNode != null) {
						length = dataTypeLengthNode.getTextContent();
					}
				}
			}

			// For all tables except domains, parse the column annotations
			if (!(table.isReferenceTable() && table.getReferenceTableType() == ReferenceTableType.DOMAIN_TABLE)) {
				if (childNode.getNodeName().equalsIgnoreCase(E_ANNOTATIONS)) {
					logger.finest("Parsing annotations...");
					if (sapName == null) {
						sapName = getColumnAnnotation(childNode, ANNOTATION_KEY_SAP_COLUMN_NAME);
					}
					if (domain == null) {
						domain = getColumnAnnotation(childNode, ANNOTATION_KEY_SAP_DATATYPE_DOMAIN);
					}
					if (transcodingTableSrcField == null) {
						transcodingTableSrcField = getColumnAnnotation(childNode, ANNOTATION_KEY_TT_SRC_FIELD);
					}
					if (transcodingTableTgtField == null) {
						transcodingTableTgtField = getColumnAnnotation(childNode, ANNOTATION_KEY_TT_TGT_FIELD);
					}
					if (relatedCheckTable == null) {
						relatedCheckTable = getColumnAnnotation(childNode, ANNOTATION_KEY_SAP_RELATED_CHECKTABLE);
						
						if (relatedCheckTable != null) {
							logger.finest("Found related check table: " + relatedCheckTable);
							sourceDataCollectionRule = getColumnAnnotation(childNode, ANNOTATION_KEY_SAP_RELATED_CT_JOIN);
							
							if (sourceDataCollectionRule != null) {
								logger.finer("Creating source data collection rule for: " + relatedCheckTable + " from data table: " + tableName);
								SourceDataCollectionRule rule = new SourceDataCollectionRule();
								rule.setRelatedCheckTableName(relatedCheckTable);
								rule.setDataTableName(tableName);
								rule.setCollectionRule(sourceDataCollectionRule);
								
								rules.add(rule);
							}
						}
					}
				}
			}
		}

		// we only gather columns for check tables and text tables at this point
		if ((table.isReferenceTable() && table.getReferenceTableType() == ReferenceTableType.CHECK_TABLE) || table.isTextTable()) {
			GenericColumn genericColumn = new GenericColumn(name);
			genericColumn.setSapName(sapName);
			genericColumn.setDomain(domain);
			genericColumn.setModelId(xmiId);
			genericColumn.setTranscodingTableSrcName(transcodingTableSrcField);
			genericColumn.setTranscodingTableTgtName(transcodingTableTgtField);
			
			int realLength = (length == null) ? DEFAULT_COLUMN_LENGTH : Integer.parseInt(length);
			genericColumn.setLength(realLength);
			
			columns.add(genericColumn);
		} else if (table.isReferenceTable() && table.getReferenceTableType() == ReferenceTableType.DOMAIN_TABLE) {
			table.addDomainTableLoadColumn(name);
		}
		logger.exiting(CLASS_NAME, METHOD_NAME);
	}

	private void handleConstraints(Node constraintsNode, HashSet<String> columnIds) {
		if (isPrimaryKeyConstraint(constraintsNode)) {
			NamedNodeMap attributes = constraintsNode.getAttributes();
			Node constraintMemberNode = attributes.getNamedItem(CONSTRAINT_MEMBERS);
			
			if (constraintMemberNode != null) {
				String primaryKeyColumnIdString = constraintMemberNode.getTextContent();
				
				if (primaryKeyColumnIdString != null) {
					String[] primaryKeyColumnIds = primaryKeyColumnIdString.split(" ");
					
					// Here are the primary key columns!
					for (String id : primaryKeyColumnIds) {
						columnIds.add(id);
					}
				}
			}
		}
	}

	private boolean isPrimaryKeyConstraint(Node constraintsNode) {
		NamedNodeMap attributes = constraintsNode.getAttributes();
		if (attributes == null || attributes.getLength() == 0) {
			return false;
		}

		Node xsiTypeNode = attributes.getNamedItem("xsi:type");
		if (xsiTypeNode == null) {
			return false;
		}
		
		String xsiType = xsiTypeNode.getTextContent();
		if (xsiType == null) {
			return false;
		}

		if (xsiType.equalsIgnoreCase(PRIMARY_KEY_CONSTRAINT)) {
			return true;
		}
		
		return false;
	}
}
