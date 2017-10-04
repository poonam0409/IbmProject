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
// Module Name : com.ibm.iis.sappack.gen.tools.sap.rmconf
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.iis.sappack.gen.tools.sap.rmconf;


import com.ibm.iis.sappack.gen.common.ui.editors.EditorEvent;
import com.ibm.iis.sappack.gen.common.ui.editors.EditorPageBase;


public class CWTechnicalFieldsChangedEvent extends EditorEvent {
	
  	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	
	
	private boolean createTechFields;
	private boolean techFieldsNullable;

	public CWTechnicalFieldsChangedEvent(EditorPageBase sourcePage, boolean createTechFields, boolean techFieldsNullable) {
		super(sourcePage);
		this.createTechFields = createTechFields;
		this.techFieldsNullable = techFieldsNullable;
	}

	public boolean isCreateTechFields() {
		return createTechFields;
	}

	public boolean isTechFieldsNullable() {
		return techFieldsNullable;
	}
	
	

}
