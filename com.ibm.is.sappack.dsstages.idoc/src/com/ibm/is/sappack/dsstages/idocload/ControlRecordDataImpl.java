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

import java.util.HashMap;
import java.util.Map;

import com.ibm.is.sappack.dsstages.common.Constants;
import com.ibm.is.sappack.dsstages.common.IDocField;
import com.ibm.is.sappack.dsstages.common.impl.ControlRecord;

public class ControlRecordDataImpl extends SegmentDataImpl implements ControlRecordData {

	static String copyright() {
		return com.ibm.is.sappack.dsstages.idocload.Copyright.IBM_COPYRIGHT_SHORT;
	}

	ControlRecord crMetaData;

	public ControlRecordDataImpl(ControlRecord crMetadata, char[] crData) {
		super(Constants.IDOC_CONTROL_RECORD_SEGMENT_DEFINITION_NAME, crData);
		this.crMetaData = crMetadata;
	}

	static class ReadOnlyMap<K, V> extends HashMap<K, V> {
		private static final long serialVersionUID = -6473385429568960867L;
		
		boolean writeable = true;

		@Override
		public V put(K key, V value) {
			if (writeable) {
				return super.put(key, value);
			}
			throw new UnsupportedOperationException("Map is read only"); //$NON-NLS-1$
		}
	}

	@Override
	public Map<String, String> getCRData() {
		ReadOnlyMap<String, String> result = new ReadOnlyMap<String, String>();
		
		int currentIndex = 0;
		
		for (IDocField field : crMetaData.getFields()) {
			int len = field.getLengthAsString();
			String fieldName = field.getFieldName();
			String fieldValue = new String(data, currentIndex, len);
			result.put(fieldName, fieldValue);
			
			// incrementing the character index for correct positioning
			// inside the char[] buffer
			currentIndex = currentIndex + len;
		}
		result.writeable = false;
		return result;
	}
}
