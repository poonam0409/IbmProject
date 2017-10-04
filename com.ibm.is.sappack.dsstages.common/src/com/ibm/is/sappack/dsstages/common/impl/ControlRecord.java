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
// Module Name : com.ibm.is.sappack.dsstages.common.impl
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.dsstages.common.impl;

import com.ibm.is.sappack.dsstages.common.CCFResource;
import com.ibm.is.sappack.dsstages.common.Constants;
import com.ibm.is.sappack.dsstages.common.IDocSegment;
import com.ibm.is.sappack.dsstages.common.IDocType;

public class ControlRecord extends IDocSegmentImpl implements IDocSegment  {

	static String copyright() {
		return com.ibm.is.sappack.dsstages.common.impl.Copyright.IBM_COPYRIGHT_SHORT;
	}

	public ControlRecord(IDocType t) {
		super(t);
		this.parentFlag            = false;
		this.parent                = null;
		this.segmentNr             = -1;
		this.segmentDefinitionName = Constants.IDOC_CONTROL_RECORD_SEGMENT_DEFINITION_NAME; 
		this.segmentTypeName       = Constants.IDOC_CONTROL_RECORD_SEGMENT_TYPE_NAME;
		this.segmentDescription    = CCFResource.getCCFText("CC_IDOC_ControlRecordDescription"); //$NON-NLS-1$
 
	}
}
