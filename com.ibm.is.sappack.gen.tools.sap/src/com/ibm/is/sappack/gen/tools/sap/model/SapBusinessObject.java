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

import java.util.Arrays;
import java.util.logging.Level;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ibm.is.sappack.gen.tools.sap.activator.Activator;

public class SapBusinessObject extends AbstractBusinessObject {
	
	static String copyright()
	{ return com.ibm.is.sappack.gen.tools.sap.model.Copyright.IBM_COPYRIGHT_SHORT; }

	public SapBusinessObject(Node node) throws XPathExpressionException {
		super(node);
	}

	public boolean hasChildren() {
		try {
			// Check for sub-packages:
			NodeList nodeList = (NodeList) XPATH_EXPRESSION_PACKAGE.evaluate(this.node, XPathConstants.NODESET);
			if (nodeList.getLength() > 0) {
				return true;
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			Activator.getLogger().log(Level.SEVERE, e.getMessage(), e);
			// FIXME: Throw an Exception to the GUI?
			return false;
		}
	}

	public SapBusinessObject[] getChildren() {
		try {
			NodeList nodeList = (NodeList) XPATH_EXPRESSION_PACKAGE.evaluate(this.node, XPathConstants.NODESET);
			if (nodeList != null) {

				SapBusinessObject[] children = new SapBusinessObject[nodeList.getLength()];
				for (int i = 0; i < nodeList.getLength(); i++) {
					Node node = nodeList.item(i);
					children[i] = new SapBusinessObject(node);
				}
				Arrays.sort(children);
				return children;
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			Activator.getLogger().log(Level.SEVERE, e.getMessage(), e);
			// FIXME: Throw an Exception to the GUI?
			return null;
		}
	}

	public SapTableBusinessObject[] getTables() {
		try {
			NodeList nodeList = (NodeList) XPATH_EXPRESSION_TABLES.evaluate(this.node, XPathConstants.NODESET);
			if (nodeList.getLength() > 0) {
				SapTableBusinessObject[] tables = new SapTableBusinessObject[nodeList.getLength()];
				for (int i = 0; i < nodeList.getLength(); i++) {
					Node node = nodeList.item(i);
					tables[i] = new SapTableBusinessObject(node);
				}
				Arrays.sort(tables);
				return tables;
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			Activator.getLogger().log(Level.SEVERE, e.getMessage(), e);
			// FIXME: Throw an Exception to the GUI?
			return null;
		}
	}
}
