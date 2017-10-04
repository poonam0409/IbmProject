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
// Module Name : com.ibm.is.sappack.gen.tools.sap.importer.idoctypes
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.tools.sap.importer.idoctypes;


import com.ibm.is.sappack.gen.common.ui.Messages;


public class ControlRecord extends Segment {
	
	static String copyright() {
		return com.ibm.is.sappack.gen.tools.sap.importer.idoctypes.Copyright.IBM_COPYRIGHT_SHORT;
	}

	public ControlRecord(IDocType t) {
		super(t);
		this.parentFlag = false;
		this.parent = null;
		this.segmentNr = -1;
		this.segmentDef = "CONTROL_RECORD";  //$NON-NLS-1$
		this.segmentType = "CONTROL_RECORD"; //$NON-NLS-1$
		this.segmentDescription = Messages.ControlRecord_0; 
	}

}
