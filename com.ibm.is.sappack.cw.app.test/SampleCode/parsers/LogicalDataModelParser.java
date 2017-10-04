package com.ibm.is.sappack.cw.app.services.rdm.parsers;


public class LogicalDataModelParser extends AbstractDataModelParser{
//
//	public static final String FILENAME_EXTENSION=".ldm";
//	
//	private static final String NAMESPACE = "LogicalDataModel";
//	private static final String ENTITY = "Entity";
//	private static final String PACKAGE = "Package";
//
//	public LogicalDataModelParser(InputStream inputStream) throws Exception {
//		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//		DocumentBuilder builder = factory.newDocumentBuilder();
//
//		Document document = builder.parse(new InputSource(inputStream));
//
//		Node rootNode = document.getDocumentElement();
//		evaluateNode(rootNode);
//	}
//
//	private void handleEntity(Node entityNode) {
//		// System.out.println("Entity \"" + entityNode.getAttributes().getNamedItem("name") + "\" found");
//		
//		String tableName = entityNode.getAttributes().getNamedItem(ATTRIBUTE_NAME).getTextContent();
//		String tableDescription = entityNode.getAttributes().getNamedItem(ATTRIBUTE_LABEL).getTextContent();
//
//		// Iterate over Annotations
//		NodeList nodeList = entityNode.getChildNodes();
//		for (int i = 0; i < nodeList.getLength(); i++) {
//			Node currentNode = nodeList.item(i);
//			if (currentNode.getNodeName().equalsIgnoreCase(ANNOTATIONS)) {
//				handleAnnotations(tableName, tableDescription, currentNode);
//			}
//		}
//	}
//
//
//	private void evaluateNode(Node node) {
//		if (node.getNodeName().equalsIgnoreCase(NAMESPACE + ":" + ENTITY)) {
//			handleEntity(node);
//		} else if (node.getNodeName().equalsIgnoreCase(NAMESPACE + ":" + PACKAGE) || node.getNodeName().equalsIgnoreCase(ROOT_NODE)) {
//			// System.out.println("Package \"" +
//			// node.getAttributes().getNamedItem("name") + "\":");
//			NodeList nodeList = node.getChildNodes();
//			for (int i = 0; i < nodeList.getLength(); i++) {
//				evaluateNode(nodeList.item(i));
//			}
//		}
//	}
//
//	public static void main(String[] args) {
//		FileInputStream fileInputStream = null;
//
//		try {
//			fileInputStream = new FileInputStream("C:\\workspaces\\runtime-EclipseApplication\\TT_Tables\\sample1.ldm");
//			LogicalDataModelParser parser = new LogicalDataModelParser(fileInputStream);
//			List<ReferenceDataTable> referenceDataTables = parser.getReferenceDataTables();
//			
//			for (ReferenceDataTable referenceDataTable:referenceDataTables) {
//				System.out.println(referenceDataTable.toString());
//			}
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			if (fileInputStream != null) {
//				try {
//					fileInputStream.close();
//				} catch (Exception e) {
//					// Nothing to be done here
//				}
//
//			}
//		}
//	}
}
