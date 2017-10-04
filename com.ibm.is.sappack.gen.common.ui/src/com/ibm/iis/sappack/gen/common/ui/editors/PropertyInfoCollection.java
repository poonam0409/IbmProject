//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2009, 2014                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.iis.sappack.gen.common.ui.editors
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.iis.sappack.gen.common.ui.editors;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PropertyInfoCollection {
  	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	

	private Map<String, PropertyInfo> propertyInfos;

	public PropertyInfoCollection() {
		propertyInfos = new HashMap<String, PropertyInfo>();
		/*
		if (propertyDescriptions == null) {
			return;
		}
		for (String[] desc : propertyDescriptions) {
			PropertyInfo pi = new PropertyInfo(desc[0], desc[1], desc[2], Boolean.parseBoolean(desc[3]), desc[4]);
			this.propertyInfos.put(desc[0], pi);
		}*/
		
	}

	public void addPropertyInfo(PropertyInfo pi) {
		if (pi != null) {
			propertyInfos.put(pi.getName(), pi);
		}
	}
	
	public List<String> getAllPropertyNames() {
		return new ArrayList<String>( this.propertyInfos.keySet() );
	}
	
	public PropertyInfo getPropertyInfo(String propName) {
		return this.propertyInfos.get(propName);
	}
}
