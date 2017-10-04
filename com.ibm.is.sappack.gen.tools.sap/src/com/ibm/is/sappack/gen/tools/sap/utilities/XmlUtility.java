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
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.tools.sap.model.SapTable;
import com.ibm.is.sappack.gen.tools.sap.model.SapTableSet;

public class XmlUtility {

	private static final String XML_ELEMENT_PACKAGE = "package";//$NON-NLS-1$
	private static final String XML_ELEMENT_NAME = "name";//$NON-NLS-1$
	private static final String XML_ATTRIBUTE_NAME = "name";//$NON-NLS-1$
	private static final String XML_ELEMENT_LABEL = "label";//$NON-NLS-1$
	private static final String XML_ELEMENT_DESCRIPTION = "description";//$NON-NLS-1$
	private static final String XML_ELEMENT_TABLES = "tables";//$NON-NLS-1$
	private static final String XML_ELEMENT_TABLE = "table";//$NON-NLS-1$

	private static final String XSI_NAMESPACE_URL = "http://www.w3.org/2001/XMLSchema-instance";//$NON-NLS-1$
	private static final String XSI_SCHEMA_LOCATION = "xsi:schemaLocation";//$NON-NLS-1$
	private static final String XSD = "http://www.ibm.com/sappack/metadata content.xsd";//$NON-NLS-1$
	private static final String TARGET_NAMESPACE = "tns:contents"; //$NON-NLS-1$
	private static final String TARGET_NAMESPACE_URI = "http://www.ibm.com/sappack/metadata"; //$NON-NLS-1$
	private Document document;
	private String fileName;
	
	static String copyright()
	{ return com.ibm.is.sappack.gen.tools.sap.utilities.Copyright.IBM_COPYRIGHT_SHORT; }

	public XmlUtility(String fileName) throws ParserConfigurationException {
		this.fileName = fileName;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder = factory.newDocumentBuilder();
		this.document = builder.newDocument();
	}

	private void save() throws TransformerException {
		TransformerFactory transformerFactory = TransformerFactory
				.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		File file = new File(this.fileName);
		StreamResult result = new StreamResult(file);
		DOMSource source = new DOMSource(this.document);

		transformer.transform(source, result);
	}

	public void saveTableSet(SapTableSet tableSet)
			throws ParserConfigurationException, TransformerException {

		// Element contentsElement = this.document.createElement("contents");
		Element contentsElement = this.document.createElementNS(
				TARGET_NAMESPACE_URI, TARGET_NAMESPACE);
		contentsElement.setAttributeNS(XSI_NAMESPACE_URL, XSI_SCHEMA_LOCATION,
				XSD);
		this.document.appendChild(contentsElement);

		Element packageElement = this.document
				.createElement(XML_ELEMENT_PACKAGE);
		packageElement.setAttribute(XML_ELEMENT_NAME, Messages.XmlUtility_0);
		contentsElement.appendChild(packageElement);

		Element labelElement = this.document.createElement(XML_ELEMENT_LABEL);
		labelElement.setTextContent(Messages.XmlUtility_1);
		packageElement.appendChild(labelElement);

		Element packageDescriptionElement = this.document
				.createElement(XML_ELEMENT_DESCRIPTION);
		packageDescriptionElement.setTextContent(Messages.XmlUtility_2);
		packageElement.appendChild(packageDescriptionElement);

		Element tablesElement = this.document.createElement(XML_ELEMENT_TABLES);
		packageElement.appendChild(tablesElement);

		Iterator<SapTable> iterator = tableSet.iterator();
		while (iterator.hasNext()) {
			SapTable table = iterator.next();
			if (!table.getSelected()) {
				continue;
			}

			Element tableElement = this.document
					.createElement(XML_ELEMENT_TABLE);
			tableElement.setAttribute(XML_ATTRIBUTE_NAME, table.getName());
			tablesElement.appendChild(tableElement);

			Element tableLabelElement = this.document
					.createElement(XML_ELEMENT_LABEL);
			tableLabelElement.setTextContent(table.getDescription());
			tableElement.appendChild(tableLabelElement);
		}

		save();
	}
}
