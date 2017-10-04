//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2009, 2010                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the US Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM Information Server R/3 Pack IDoc Load 
//                                                                             
// Module Name : com.ibm.is.sappack.dsstages.idocload.online.test
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.dsstages.idocload.online.test;

import com.ibm.is.sappack.dsstages.idocload.SegmentData;

public class DummySegment implements SegmentData{

	
	private String segmentName;
	private String segmentData;
	
	/**
	 * DummySegment
	 * 
	 * @param segmentName
	 * @param segmentData
	 */
	public DummySegment(String segmentName, String segmentData) {
		super();
		this.segmentName = segmentName;
		this.segmentData = segmentData;
	}

	static String copyright()
	{ return com.ibm.is.sappack.dsstages.idocload.online.test.Copyright.IBM_COPYRIGHT_SHORT; }

	@Override
	public char[] getSegmentDataBuffer() {
		return this.segmentData.toCharArray();
	}

	@Override
	public String getSegmentDefinitionName() {
		return this.segmentName;
	}
}
