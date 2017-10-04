//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2014                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.iis.sappack.gen.tools.jobgenerator.jobgen
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.iis.sappack.gen.tools.jobgenerator.jobgen;


import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ibm.is.sappack.gen.common.Constants;
import com.ibm.is.sappack.gen.common.DBSupport;
import com.ibm.is.sappack.gen.common.JobGeneratorException;
import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.common.util.StringUtils;
import com.ibm.is.sappack.gen.tools.jobgenerator.Activator;


public class TableAnnotationTraversal {
  	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	

	public static interface TableAnnotationVisitor {
		void visit(String tableName, Element eAnnotationsElement, List<Element> annotations);
	}

	public static void traverseDocument(Document doc, TableAnnotationVisitor visitor) {
		Element root = doc.getDocumentElement();
		String tableTagName = null;
		try {
			switch (DBSupport.getDatabaseType(doc)) {
			case Oracle:
				tableTagName = DBSupport.DBNAME_TYPE_ORACLE;
				break;
			case Netezza:
				tableTagName = DBSupport.DBNAME_TYPE_NETEZZA;
				break;
			case DB2:
			default:
				tableTagName = DBSupport.DBNAME_TYPE_DB2;
			}
		} catch (JobGeneratorException exc) {
			Activator.getLogger().log(Level.SEVERE, Messages.UnexpectedErrorOcurred, exc);
			throw new RuntimeException(exc.getMessage());
		}
	
		NodeList nl = root.getElementsByTagName(tableTagName);
	
		for (int i = 0; i < nl.getLength(); i++) {
			Element tableElement = (Element) nl.item(i);
			//			   String entityName = tableElement.getAttribute("name"); //$NON-NLS-1$
			NodeList allNodes = tableElement.getChildNodes();
			for (int j = 0; j < allNodes.getLength(); j++) {
				Node childNode = allNodes.item(j);
				if (childNode instanceof Element) {
					Element eAnnot = (Element) childNode;
					String elementName = eAnnot.getNodeName();
					if (elementName.equals("eAnnotations")) { //$NON-NLS-1$
						String source = eAnnot.getAttribute("source"); //$NON-NLS-1$
						if ("UDP".equals(source)) { //$NON-NLS-1$
							List<Element> annotationElements = new ArrayList<Element>();
							NodeList annotations = eAnnot.getElementsByTagName("details"); //$NON-NLS-1$
							String dataObjectSource = null;
							String checkTableName = null;
							String textTableName = null;
							String logicalTableName = null;
							for (int k = 0; k < annotations.getLength(); k++) {
								Element annot = (Element) annotations.item(k);
								annotationElements.add(annot);
								String key = annot.getAttribute(com.ibm.is.sappack.gen.tools.jobgenerator.Constants.DBM_ANNOTATION_XML_ATTRIBUTE_KEY);
								String value = annot.getAttribute(com.ibm.is.sappack.gen.tools.jobgenerator.Constants.DBM_ANNOTATION_XML_ATTRIBUTE_VALUE);
								if (Constants.ANNOT_DATA_OBJECT_SOURCE.equals(key)) {
									dataObjectSource = value;
								}
								if (Constants.ANNOT_CHECK_TABLE_NAME.equals(key)) {
									checkTableName = value;
								}
								if (Constants.ANNOT_TEXT_TABLE_NAME.equals(key)) {
									textTableName = value;
								}
								if (Constants.ANNOT_SAP_TABLE_NAME.equals(key)) {
									logicalTableName = value;
								}
							}
							String tableName = null;
							if (Constants.DATA_OBJECT_SOURCE_TYPE_JOINED_CHECK_AND_TEXT_TABLE.equals(dataObjectSource)
									|| Constants.DATA_OBJECT_SOURCE_TYPE_REFERENCE_CHECK_TABLE.equals(dataObjectSource)
									|| Constants.DATA_OBJECT_SOURCE_TYPE_NON_REFERENCE_CHECK_TABLE.equals(dataObjectSource)) {
								tableName = checkTableName;
							} else if (Constants.DATA_OBJECT_SOURCE_TYPE_LOGICAL_TABLE.equals(dataObjectSource)) {
								tableName = logicalTableName;
							} else if (Constants.DATA_OBJECT_SOURCE_TYPE_REFERENCE_TEXT_TABLE.equals(dataObjectSource)) {
								tableName = textTableName;
							}
							tableName = StringUtils.cleanFieldName(tableName);
							visitor.visit(tableName, eAnnot, annotationElements);
						}
					}
				}
			}
		}
	}

}
