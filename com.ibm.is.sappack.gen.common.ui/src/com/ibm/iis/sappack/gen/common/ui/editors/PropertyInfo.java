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


public class PropertyInfo {
  	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	
	private String name;
	private String readableName;
	private String description;
//	private boolean required;
	private String location;
	private Object defaultValue;
	private String type;
	
	public static final String TYPE_NAME_ENCRYPTED = "Encrypted"; //$NON-NLS-1$

	public static final String TYPE_NAME_STRING = "String"; //$NON-NLS-1$
	
	
	public PropertyInfo(String name, String readableName, String description, String type, Object defaultValue) {
		
		this.name = name;
		this.readableName = readableName;
		this.description = description;
		this.type = type;
		this.defaultValue = defaultValue;
	}

	public PropertyInfo(String name, String readableName, String description) {
		this(name, readableName, description, null);
	}

	public PropertyInfo(String name, String readableName, String description, Object defaultValue) {
		super();
		this.name = name;
		this.readableName = readableName;
		this.description = description;
	//	this.required = required;
		this.defaultValue = defaultValue;
		/* default type String */
		this.type = TYPE_NAME_STRING;
	}

	public String getType() {
		return this.type;
	}
	
	public String getName() {
		return name;
	}

	public String getReadableName() {
		return readableName;
	}

	public String getDescription() {
		return description;
	}

	/*
	public boolean isRequired() {
		return required;
	}
	*/

	public String getLocation() {
		return this.location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
	
	public Object getDefault() {
		return this.defaultValue;
	}
}
