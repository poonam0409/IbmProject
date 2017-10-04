//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2011, 2014                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.iis.sappack.gen.tools.sap.views.dependency
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.iis.sappack.gen.tools.sap.views.dependency;


import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;


public class TreeNode implements IAdaptable {
  	private TreeNode parent;
	private Object info;
	private List<TreeNode> children;


	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	

	public TreeNode(TreeNode parent) {
		this.parent = parent;
		this.children = new ArrayList<TreeNode>();
		if (parent != null) {
			parent.getChildren().add(this);
		}
	}

	public TreeNode(TreeNode parent, Object info) {
		this(parent);
		this.info = info;
	}

	public Object getInfo() {
		return this.info;
	}

	public void setInfo(Object o) {
		this.info = o;
	}

	public List<TreeNode> getChildren() {
		return children;
	}

	public Object getParent() {
		return this.parent;
	}

	@Override
	public Object getAdapter(Class adapter) {
		if (info == null) {
			return null;
		}		
		if (adapter.isAssignableFrom(info.getClass())) {
			return info;
		}
		return null;
	}

}