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
// Module Name : com.ibm.is.sappack.gen.tools.sap.model
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.tools.sap.model;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.logging.Level;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.tools.sap.activator.Activator;
import com.ibm.is.sappack.gen.tools.sap.constants.Constants;

public abstract class AbstractBusinessObject implements Comparable<AbstractBusinessObject> {

	private static final String ATTRIBUTE_NAME = "name"; //$NON-NLS-1$
	private static XPathExpression XPATH_EXPRESSION_CONTENTS;
	private static XPathExpression XPATH_EXPRESSION_LABEL;
	private static XPathExpression XPATH_EXPRESSION_DESCRIPTION;
	static XPathExpression XPATH_EXPRESSION_PACKAGE;
	static XPathExpression XPATH_EXPRESSION_TABLES;

	protected Node node;
	
	static String copyright()
	{ return com.ibm.is.sappack.gen.tools.sap.model.Copyright.IBM_COPYRIGHT_SHORT; }

	static {
		try {
			XPathFactory factory = XPathFactory.newInstance();
			XPath xPath = factory.newXPath();
			XPATH_EXPRESSION_CONTENTS = xPath.compile(Constants.XPATH_STRING_CONTENTS);
			XPATH_EXPRESSION_LABEL = xPath.compile(Constants.XPATH_STRING_LABEL);
			XPATH_EXPRESSION_DESCRIPTION = xPath.compile(Constants.XPATH_STRING_DESCRIPTION);
			XPATH_EXPRESSION_PACKAGE = xPath.compile(Constants.XPATH_STRING_PACKAGE);
			XPATH_EXPRESSION_TABLES = xPath.compile(Constants.XPATH_STRING_TABLES);
		} catch (Exception e) {
			e.printStackTrace();
			Activator.getLogger().log(Level.SEVERE, e.getMessage(), e);
			// FIXME: Throw an Exception to the GUI?
		}
	}

	public AbstractBusinessObject(Node node) throws XPathExpressionException {
		this.node = node;
	}

	public String getName() {
		return this.node.getAttributes().getNamedItem(ATTRIBUTE_NAME).getTextContent();
	}

	public String getLabel() {
		try {
			return (String) XPATH_EXPRESSION_LABEL.evaluate(this.node, XPathConstants.STRING);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
			Activator.getLogger().log(Level.SEVERE, e.getMessage(), e);
			// FIXME: Throw an Exception to the GUI?
			return null;
		}
	}

	public String getDescription() {
		try {
			return (String) XPATH_EXPRESSION_DESCRIPTION.evaluate(this.node, XPathConstants.STRING);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
			Activator.getLogger().log(Level.SEVERE, e.getMessage(), e);
			// FIXME: Throw an Exception to the GUI?
			return null;
		}
	}

	public abstract boolean hasChildren();


	public static final AbstractBusinessObject[] getInitalContent(String fileName) {
		try {
			InputStream inputStream = new FileInputStream(fileName);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(inputStream);

			NodeList nodeList = (NodeList) XPATH_EXPRESSION_CONTENTS.evaluate(document, XPathConstants.NODESET);

			AbstractBusinessObject[] sapBusinessObjects = new AbstractBusinessObject[nodeList.getLength()];

			for (int i = 0; i < nodeList.getLength(); i++) {
				sapBusinessObjects[i] = new SapBusinessObject(nodeList.item(i));
			}
			Arrays.sort(sapBusinessObjects);
			return sapBusinessObjects;
		} catch (Exception e) {
			e.printStackTrace();
			String msg = Messages.AbstractBusinessObject_0;
			Activator.getLogger().log(Level.SEVERE, msg, e);
			// FIXME: Throw an Exception to the GUI?
			throw new RuntimeException(msg);
		}

	}

	public static final AbstractBusinessObject[] getInitialContent() {
		return new AbstractBusinessObject[0];
	}

	@Override
	public int compareTo(AbstractBusinessObject other) {
		return this.getName().compareTo(other.getName());
	}

	@Override
   public boolean equals(Object o) {
		if (o instanceof AbstractBusinessObject) {
			return (this.compareTo((AbstractBusinessObject) o) == 0);
		}
		
		return false;
   }

	@Override
	public int hashCode() {
		
		// FindBugs will complain about this and suggest to change it to throw
		// an UnsupportedOperationException
		// do not listen to FindBugs - changing this will have unpredictable
		// side effects on various parts of the code
		return super.hashCode();
	}
}
