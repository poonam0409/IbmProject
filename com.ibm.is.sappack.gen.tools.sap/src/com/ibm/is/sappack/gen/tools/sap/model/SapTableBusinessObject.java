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

import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Node;

public class SapTableBusinessObject extends AbstractBusinessObject {
	
	static String copyright()
	{ return com.ibm.is.sappack.gen.tools.sap.model.Copyright.IBM_COPYRIGHT_SHORT; }
	
	public SapTableBusinessObject(Node node) throws XPathExpressionException{
		super(node);
	}

	@Override
	public boolean hasChildren() {
		return false;
	}
	
	

}
