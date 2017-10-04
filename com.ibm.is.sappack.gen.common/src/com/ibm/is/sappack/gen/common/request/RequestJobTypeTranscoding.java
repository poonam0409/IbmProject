//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2011, 2013                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.is.sappack.gen.common.request
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************

package com.ibm.is.sappack.gen.common.request;


import org.w3c.dom.Node;

import com.ibm.is.sappack.gen.common.util.XMLUtils;


/**
 * RequestJobTypeTranscoding
 * 
 * @author gaege
 * 
 */
public class RequestJobTypeTranscoding extends RequestJobTypeMovement {
	private static final String XML_ATTRIB_CHECK_TABLES_MODEL_ID          = "CheckTablesModelID";
	private static final String XML_ATTRIB_TARGET_LEGACY_ID               = "targetLegacyID";
	private static final String XML_ATTRIB_TRANSCODE_REFERENCE_FIELDS     = "transcodeReferenceFields";
	private static final String XML_ATTRIB_TRANSCODE_NON_REFERENCE_FIELDS = "transcodeNonReferenceFields";
	private static final String XML_ATTRIB_TRANSCODE_DOMAIN_VALUE_FIELDS  = "transcodeDomainValueFields";
	private static final String XML_ATTRIB_MARK_UNMAPPED_VALUES           = "markUnmappedValues";

	// check tables model id
	private String  _CheckTablesModelID          = null;
	// legacyID of the target SAP system
	private String  _TargetLegacyID              = "";
	// transcode reference fields
	private boolean _TranscodeReferenceFields    = true;
	// transcode non-reference fields
	private boolean _TranscodeNonReferenceFields = true;
	// transcode domain value fields
	private boolean _TranscodeDomainValueFields  = true;
	// mark unmapped values during transcoding
	private boolean _MarkUnmappedValues          = true;


	static String copyright() {
		return com.ibm.is.sappack.gen.common.request.Copyright.IBM_COPYRIGHT_SHORT;
	}



	/**
	 * RequestJobTypeTranscoding
	 * 
	 * @param parJobName
	 */
	public RequestJobTypeTranscoding(String parJobName) {
		super(parJobName, null, null);
	}

	
	/**
	 * RequestJobTypeTranscoding
	 * 
	 * @param parJobTypeTranscodingNode
	 */
	RequestJobTypeTranscoding(Node parJobTypeTranscodingNode) {
		super(parJobTypeTranscodingNode);

		// target system legacy ID
		String legacyID = XMLUtils.getNodeAttributeValue(parJobTypeTranscodingNode,RequestJobTypeTranscoding.XML_ATTRIB_TARGET_LEGACY_ID);
		if (legacyID != null) {
			this._TargetLegacyID = legacyID;
		}

		// transcode reference fields
		this._TranscodeReferenceFields = Boolean.valueOf(XMLUtils.getNodeAttributeValue(parJobTypeTranscodingNode, 
		                                                                               RequestJobTypeTranscoding.XML_ATTRIB_TRANSCODE_REFERENCE_FIELDS)).booleanValue();
	
		// transcode non-reference fields
		this._TranscodeNonReferenceFields = Boolean.valueOf(XMLUtils.getNodeAttributeValue(parJobTypeTranscodingNode, 
		                                                                                  RequestJobTypeTranscoding.XML_ATTRIB_TRANSCODE_NON_REFERENCE_FIELDS)).booleanValue();
	
		// transcode domain value fields 
		this._TranscodeDomainValueFields = Boolean.valueOf(XMLUtils.getNodeAttributeValue(parJobTypeTranscodingNode, 
		                                                                                 RequestJobTypeTranscoding.XML_ATTRIB_TRANSCODE_DOMAIN_VALUE_FIELDS)).booleanValue();

		// mark unmapped values 
		this._MarkUnmappedValues = Boolean.valueOf(XMLUtils.getNodeAttributeValue(parJobTypeTranscodingNode, 
		                                                                          RequestJobTypeTranscoding.XML_ATTRIB_MARK_UNMAPPED_VALUES)).booleanValue();

		// check tables model id
		String modelId = XMLUtils.getNodeAttributeValue(parJobTypeTranscodingNode, RequestJobTypeTranscoding.XML_ATTRIB_CHECK_TABLES_MODEL_ID);
		if (modelId != null) {
			this._CheckTablesModelID = modelId;
		}
	}

	public String getCTPhysicalModelId() {
		return(this._CheckTablesModelID);
	}
	
	String getJobTypeAttribs() {
		StringBuffer xmlBuf = new StringBuffer();

		// target legacy ID
		xmlBuf.append(XMLUtils.createAttribPairString(RequestJobTypeTranscoding.XML_ATTRIB_TARGET_LEGACY_ID,
		                                              this._TargetLegacyID));

		// transcode reference fields
		xmlBuf.append(XMLUtils.createAttribPairString(RequestJobTypeTranscoding.XML_ATTRIB_TRANSCODE_REFERENCE_FIELDS,
		                                              Boolean.toString(this._TranscodeReferenceFields)));
		
		// transcode non-reference fields
		xmlBuf.append(XMLUtils.createAttribPairString(RequestJobTypeTranscoding.XML_ATTRIB_TRANSCODE_NON_REFERENCE_FIELDS,
		                                              Boolean.toString(this._TranscodeNonReferenceFields)));
		
		// transcode domain value fields
		xmlBuf.append(XMLUtils.createAttribPairString(RequestJobTypeTranscoding.XML_ATTRIB_TRANSCODE_DOMAIN_VALUE_FIELDS,
		                                              Boolean.toString(this._TranscodeDomainValueFields)));
		
		// mark unmapped values
		xmlBuf.append(XMLUtils.createAttribPairString(RequestJobTypeTranscoding.XML_ATTRIB_MARK_UNMAPPED_VALUES,
		                                              Boolean.toString(this._MarkUnmappedValues)));
		
		// check tables model id
		xmlBuf.append(XMLUtils.createAttribPairString(RequestJobTypeTranscoding.XML_ATTRIB_CHECK_TABLES_MODEL_ID,
		                                              this._CheckTablesModelID));
		
		
		// movement job attributes
		xmlBuf.append(super.getJobTypeAttribs());
		
		return (xmlBuf.toString());
	}
	
	public int getJobType() {
		return (TYPE_TRANSCODING_ID);
	}

	public String getJobTypeAsString() {
		return (TYPE_TRANSCODING);
	}

	public String getTargetLegacyID() {
		return this._TargetLegacyID;
	}
	
	/**
	 * isMarkedUnmappedValues
	 * @return
	 */
	public boolean isMarkUnmappedValues() {
		return _MarkUnmappedValues;
	}
	
	/**
	 * isTranscodeDomainValueFields
	 * @return
	 */
	public boolean isTranscodeDomainValueFields() {
		return _TranscodeDomainValueFields;
	}
	
	/**
	 * isTranscodeReferenceFields
	 * @return
	 */
	public boolean isTranscodeReferenceFields() {
		return _TranscodeReferenceFields;
	}

	/**
	 * isTranscodeNonReferenceFields
	 * @return
	 */
	public boolean isTranscodeNonReferenceFields() {
		return _TranscodeNonReferenceFields;
	}


	public void setTargetLegacyID(String legacyID) {
		if(legacyID != null) {
			this._TargetLegacyID = legacyID;
		}
	}
	
	public void setCTPhysicalModelId(String checkTablesModelFileId) {
		this._CheckTablesModelID = checkTablesModelFileId;
	}
	
   void setSupportedTypes(SupportedColumnTypesMap parColumTypesMap,
                          SupportedTableTypesMap parTableTypesMap)
   {
   	parColumTypesMap.add(SupportedColumnTypesMap.COLUMN_TYPE_ENUM_ALL_TYPES);
   	parTableTypesMap.add(SupportedTableTypesMap.TABLE_TYPE_ENUM_LOGICAL_TABLE);
   }

	/**
	 * setMarkUnmappedValues
	 * @param markUnmappedValues
	 */
	public void setMarkUnmappedValues(boolean markUnmappedValues) {
		this._MarkUnmappedValues = markUnmappedValues;
	}
	
	/**
	 * setTranscodeDomainValueFields
	 * @param transcodeDomainValueFields
	 */
	public void setTranscodeDomainValueFields(boolean transcodeDomainValueFields) {
		this._TranscodeDomainValueFields = transcodeDomainValueFields;
	}
	
	/**
	 * setTranscodeNonReferenceFields
	 * @param transcodeNonReferenceFields
	 */
	public void setTranscodeNonReferenceFields(
			boolean transcodeNonReferenceFields) {
		this._TranscodeNonReferenceFields = transcodeNonReferenceFields;
	}

	/**
	 * setTranscodeReferenceFields
	 * @param transcodeReferenceFields
	 */
	public void setTranscodeReferenceFields(
			boolean transcodeReferenceFields) {
		this._TranscodeReferenceFields = transcodeReferenceFields;
	}

   public String toString()
   {
      StringBuffer traceStringBuf;
      
      traceStringBuf = new StringBuffer();
      traceStringBuf.append(" - CT models Id: ");
      traceStringBuf.append(_CheckTablesModelID);
      traceStringBuf.append(" - target Legacy Id: ");
      traceStringBuf.append(_TargetLegacyID);
      traceStringBuf.append(" - transcode domain value fields: ");
      traceStringBuf.append(_TranscodeDomainValueFields);
      traceStringBuf.append(" - transcode reference fields: ");
      traceStringBuf.append(_TranscodeReferenceFields);
      traceStringBuf.append(" -  transcode non-reference fields: ");
      traceStringBuf.append(_TranscodeNonReferenceFields);
      traceStringBuf.append(" - mark unmapped values: ");
      traceStringBuf.append(_MarkUnmappedValues);
      
      return(super.toString() + traceStringBuf.toString());
   }
   
}
