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
// Module Name : com.ibm.iis.sappack.gen.tools.sap.views.navigator
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.iis.sappack.gen.tools.sap.views.navigator;


public class NameWrapper {
  	protected Object parent;
	protected String name;


	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	

	protected NameWrapper(Object parent, String name) {
		this.parent = parent;
		this.name = name;
	}

	public Object getParent() {
		return this.parent;
	}

	public String getName() {
		return this.name;
	}

	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (o == null) {
			return false;
		}

		if (!this.getClass().equals(o.getClass())) {
			return false;
		}
		return this.name.equals(((NameWrapper) o).name);
	}
}
