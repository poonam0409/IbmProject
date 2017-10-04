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

public class SegmentDataImpl implements SegmentData {

	static String copyright() {
		return com.ibm.is.sappack.dsstages.idocload.Copyright.IBM_COPYRIGHT_SHORT;
	}

	protected String segmentDefinitionName;
	protected char[] data;

	public SegmentDataImpl(String segmentDefinitionName, char[] data) {
		this.segmentDefinitionName = segmentDefinitionName;
		this.data = data;
	}
	
	public String getSegmentDefinitionName() {
		return segmentDefinitionName;
	}

	public char[] getSegmentDataBuffer() {
		return data;
	}

}
