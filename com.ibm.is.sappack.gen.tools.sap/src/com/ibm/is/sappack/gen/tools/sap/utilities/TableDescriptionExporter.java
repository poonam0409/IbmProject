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

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ibm.iis.sappack.gen.common.ui.connections.SapSystem;
import com.ibm.iis.sappack.gen.common.ui.jco.RfcDestinationDataProvider;
import com.ibm.is.sappack.gen.tools.sap.constants.Constants;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoStructure;

public class TableDescriptionExporter {

	
	static String copyright()
	{ return com.ibm.is.sappack.gen.tools.sap.utilities.Copyright.IBM_COPYRIGHT_SHORT; }

	@SuppressWarnings("nls")
	public static void main(String[] args) {
		try {

			String fileName = "D:/workspace/rda/com.ibm.is.sappack.gen.tools.sap/src/SAPBusinessObjects_en_new.xml";

//			SapSystem sapSystem = new SapSystem("dummy");
//			sapSystem.setClientId(2);
//			sapSystem.setHost("walldorf02.boeblingen.de.ibm.com");
//			sapSystem.setPassword("saptest");
//			sapSystem.setSystemNumber(0);
//			sapSystem.setUsername("sebastian");
			
			SapSystem sapSystem = new SapSystem("dummy");
			sapSystem.setClientId(800);
			sapSystem.setHost("bocasapides5.bocaraton.ibm.com");
			sapSystem.setPassword("cody50");
			sapSystem.setSystemNumber("00");
			sapSystem.setUsername("rguberud");
         sapSystem.setUserLanguage("EN");

			Document document = loadXmlDocument(fileName);

			fetchDescriptions(sapSystem, document);

			saveXmlDocument(fileName, document);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Document loadXmlDocument(String fileName) throws Exception {
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
		return docBuilder.parse(new File(fileName));
	}

	public static void saveXmlDocument(String fileName, Document document) throws Exception {
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		File file = new File(fileName);
		StreamResult result = new StreamResult(file);
		DOMSource source = new DOMSource(document);

		transformer.transform(source, result);
	}

	public static void fetchDescriptions(SapSystem sapSystem, Document document) throws Exception {
		NodeList nodeList = document.getElementsByTagName("table"); //$NON-NLS-1$
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node tableNode = nodeList.item(i);

			// Check whether description already exists
			boolean skip = false;
			NodeList childNodes = tableNode.getChildNodes();
			for (int j = 0; j < childNodes.getLength(); j++) {
				Node childNode = childNodes.item(j);
				if (childNode.getNodeName().equalsIgnoreCase("description")) { //$NON-NLS-1$
					skip = true;
					break;
				}
			}
			if (skip) {
				continue;
			}

			String tableName = tableNode.getAttributes().getNamedItem("name").getNodeValue(); //$NON-NLS-1$
			String description = getDescription(sapSystem, tableName);
			
			if (description == null || description.trim().equals("")) { //$NON-NLS-1$
				continue;
			}

			Element descriptionElement = document.createElement("description"); //$NON-NLS-1$
			descriptionElement.setTextContent(description);
			tableNode.appendChild(descriptionElement);
		}
	}

	private static String getDescription(SapSystem sapSystem, String tableName) throws JCoException {
		JCoDestination destination = RfcDestinationDataProvider.getDestination(sapSystem);
		return getDescriptionAndDevClass(destination, tableName)[0];
	}

	public static String[] getDescriptionAndDevClass(JCoDestination destination, String tableName) throws JCoException {
		JCoFunction function_DD_TABL_GET = destination.getRepository().getFunction(Constants.JCO_FUNCTION_DD_TABL_GET);

		function_DD_TABL_GET.getImportParameterList().setValue(Constants.JCO_PARAMETER_TABL_NAME, tableName);
		function_DD_TABL_GET.getImportParameterList().setValue(Constants.JCO_PARAMETER_WITHTEXT, Constants.JCO_PARAMETER_VALUE_TRUE);
		function_DD_TABL_GET.getImportParameterList().setValue(Constants.JCO_PARAMETER_LANGU, destination.getLanguage());
		function_DD_TABL_GET.execute(destination);

		JCoStructure structure_DD02V_WA_A = function_DD_TABL_GET.getExportParameterList().getStructure(Constants.JCO_RESULTSTRUCTURE_DD02_V_WA_A);
		String description = structure_DD02V_WA_A.getString(Constants.JCO_PARAMETER_DDTEXT);
		String devClass = structure_DD02V_WA_A.getString(Constants.JCO_PARAMETER_CONTFLAG);

		String[] result = new String[2];
		result[0] = description;
		result[1] = devClass;
		return result;
	}


}
