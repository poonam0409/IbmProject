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

import java.util.HashMap;
import java.util.Map;

import com.ibm.is.sappack.dsstages.common.Constants;
import com.ibm.is.sappack.dsstages.idocload.ControlRecordData;

/**
 * DummyControlRecordData
 * 
 * dummy implementation for testing purposes only
 *
 */
public class DummyControlRecordData implements ControlRecordData {

	static String copyright()
	{ return com.ibm.is.sappack.dsstages.idocload.online.test.Copyright.IBM_COPYRIGHT_SHORT; }

	private Map<String, String> props;
	
	
	public DummyControlRecordData(String IDOCNUM, String IDOCTYP, String MESTYP, String SNDPRN, String SNDPRT, String RCVPRN, String RCVPRT, String TEST) {
		this.props = new HashMap<String, String>();
		
		props.put("DOCNUM", IDOCNUM); //$NON-NLS-1$
		props.put("IDOCTYP", IDOCTYP); //$NON-NLS-1$
		props.put("MESTYP", MESTYP); //$NON-NLS-1$
		props.put("SNDPRN", SNDPRN); //$NON-NLS-1$
		props.put("SNDPRT", SNDPRT); //$NON-NLS-1$
		props.put("RCVPRN", RCVPRN); //$NON-NLS-1$
		props.put("RCVPRT", RCVPRT); //$NON-NLS-1$
		props.put("TEST", TEST); //$NON-NLS-1$
		
	}
	
	
	@Override
	public Map<String, String> getCRData() {
		return props;
	}


	@Override
	public char[] getSegmentDataBuffer() {
		throw new UnsupportedOperationException();
	}


	@Override
	public String getSegmentDefinitionName() {
		return Constants.IDOC_CONTROL_RECORD_SEGMENT_DEFINITION_NAME;
	}

}
