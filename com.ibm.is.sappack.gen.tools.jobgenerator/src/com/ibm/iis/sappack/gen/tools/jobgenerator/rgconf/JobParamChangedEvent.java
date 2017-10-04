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
// Module Name : com.ibm.iis.sappack.gen.tools.jobgenerator.rgconf
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.iis.sappack.gen.tools.jobgenerator.rgconf;


import com.ibm.iis.sappack.gen.common.ui.editors.EditorEvent;
import com.ibm.iis.sappack.gen.common.ui.editors.EditorPageBase;


public class JobParamChangedEvent extends EditorEvent {
  	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	


	public JobParamChangedEvent(EditorPageBase sourcePage) {
		super(sourcePage);
	}
	
	/*
	public static enum EVENT_TYPE {
		ADDED, REMOVED, CHANGED
	};

	JobParameter parameter;
	EVENT_TYPE type;

	public JobParamChangedEvent(EditorPageBase sourcePage, JobParameter parameter, EVENT_TYPE type) {
		super(sourcePage);
		this.parameter = parameter;
		this.type = type;
	}

	/ *
	public JobParameter getChangedParameter() {
		return this.parameter;
	}

	public EVENT_TYPE getType() {
		return type;
	}
	*/
}
