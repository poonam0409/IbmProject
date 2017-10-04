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
// Module Name : com.ibm.is.sappack.dsstages.idocload
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.dsstages.idocload;

public class SegmentValidationResult {

	static String copyright() {
		return com.ibm.is.sappack.dsstages.idocload.Copyright.IBM_COPYRIGHT_SHORT;
	}

	public enum Type {
		ORPHAN,
		WRONG_PARENT_SEGMENT_TYPE,
		DUPLICATE_SEGMENT,
		NO_SEGMENTS_FOR_IDOC
	};

	Type type;
	IDocTree idocTree;
	IDocNode idocNode;
	String message;

	public void setType(Type type) {
		this.type = type;
	}

	public void setIDocTree(IDocTree idocTree) {
		this.idocTree = idocTree;
	}

	public void setIDocNode(IDocNode idocNode) {
		this.idocNode = idocNode;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public IDocTree getIDocTree() {
		return idocTree;
	}

	public IDocNode getIDocNode() {
		return idocNode;
	}

	public Type getType() {
		return type;
	}

	public String getMessage() {
		return message;
	}
	
	public static String typeToString(Type t) {
		// TODO better string representation of type
		return t.toString();
	}
	
	public String toString() {
		return typeToString(this.type) + ": " + message; //$NON-NLS-1$
	}

}
