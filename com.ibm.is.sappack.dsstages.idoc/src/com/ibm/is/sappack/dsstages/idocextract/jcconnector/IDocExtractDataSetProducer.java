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
// Module Name : com.ibm.is.sappack.dsstages.idocextract.jcconnector
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.dsstages.idocextract.jcconnector;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.ascential.e2.connector.CC_RecordDataSetProducer;
import com.ascential.e2.daapi.CC_Accessor;
import com.ascential.e2.daapi.metadata.CC_DataField;
import com.ascential.e2.propertyset.CC_ErrorList;
import com.ascential.e2.propertyset.CC_PropertySet;
import com.ibm.is.sappack.dsstages.common.Constants;
import com.ibm.is.sappack.dsstages.common.IDocType;
import com.ibm.is.sappack.dsstages.common.StageLogger;
import com.ibm.is.sappack.dsstages.common.ccf.CCFUtils;
import com.ibm.is.sappack.dsstages.idoc.IDocDataSetProducerConsumerCommon;
import com.ibm.is.sappack.dsstages.idoc.IDocFieldAligner;
import com.ibm.is.sappack.dsstages.idoc.IDocDataSetProducerConsumerCommon.LinkColumn;
import com.ibm.is.sappack.dsstages.idoc.IDocFieldAlignmentResult;



public class IDocExtractDataSetProducer extends CC_RecordDataSetProducer {

	static String copyright() {
		return com.ibm.is.sappack.dsstages.idocextract.jcconnector.Copyright.IBM_COPYRIGHT_SHORT;
	}

	static private String CLASSNAME = IDocExtractDataSetProducer.class.getName(); 

	private Logger logger;
	IDocDataSetProducerConsumerCommon dataSetCommon;
	String segmentData = null; 
	boolean isControlRecord = false;
	private IDocFieldAligner fieldAligner = null;
		
	public IDocExtractDataSetProducer(IDocType idocType, CC_PropertySet propSet, CC_ErrorList errList, IDocFieldAligner aligner) {
		super();
		this.logger = StageLogger.getLogger();
		final String METHODNAME = "<init>"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);
		try {
			dataSetCommon = new IDocDataSetProducerConsumerCommon(idocType, propSet, errList, getRequiredLinkProperties());
		}
		catch (Exception exc) {
			logger.log(Level.SEVERE, "CC_IDOC_CommonUnexpectedException", exc); //$NON-NLS-1$
			CCFUtils.throwCC_Exception(exc);
		}
		this.fieldAligner = aligner;
		this.logger.exiting(CLASSNAME, METHODNAME);
	}

	String[] getRequiredLinkProperties() {
		// TODO add required link properties
		return new String[0];
	}
	
	@Override
	protected boolean hasOneTopLevelDataItem() {
		final String METHODNAME = "hasOneTopLevelDataItem"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);
		this.logger.exiting(CLASSNAME, METHODNAME);
		return true;
	}

	@Override
	protected void processOneTopLevelDataItem() {
		final String METHODNAME = "processOneTopLevelDataItem"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);

		if (this.isControlRecord) {
			/*
			 * TABNAM Name of table structure CHAR(10)
			 * MANDT Client number CHAR(3)
			 * DOCNUM IDoc number CHAR(16)
			 * ...
			 */
			this.logger.log(Level.FINEST, "Control record data: {0}", segmentData); //$NON-NLS-1$

			String docnum = this.segmentData.substring(13, 29);
			this.logger.log(Level.FINEST, "docnum: {0}", docnum); //$NON-NLS-1$

			this.dataSetCommon.accessorADMDOCNUM.setString(docnum);

			int start = 0;
			int end = start;
			for (int i = 0; i < this.dataSetCommon.columnsInOrderOfIDocFields.length; i++) {
				String     value = ""; //$NON-NLS-1$
				LinkColumn col   = this.dataSetCommon.columnsInOrderOfIDocFields[i];
				int length       = col.idocFieldLength;
				
				end = end + length;
				if (col.field == null) {
					this.logger.log(Level.FINEST, "Control record field does not exist on link"); //$NON-NLS-1$					
				} else {
					this.logger.log(Level.FINEST, "Control record field: 0", col.field.getName()); //$NON-NLS-1$
					if (end <= this.segmentData.length()) {
						value = this.segmentData.substring(start, end);
						this.logger.log(Level.FINEST, "Column name: {0}, value {1}, start {2}, end {3}, field length {4}", //$NON-NLS-1$
								new Object[] { col.field.getName(), value, start, end, length });
						passColumnValueToAccessor(col, value);
					} else {
						this.logger.log(Level.WARNING, "CC_IDOC_EXTRACT_SegmentDataTooShort", //$NON-NLS-1$ 
								new Object[] { docnum, end, this.segmentData.length() });
					}
				}
				start = end;
			}
		} 
		else {
			/*
			 * Each line represents a segment with the following format:
			 * <SEGNAM><MANDT><DOCNUM><SEGNUM><PSGNUM><HLEVEL><SDATA>
			 * 
			 * where:
			 * 
			 * SEGNAM name of the segment CHAR(30)
			 * MANDT client CHAR(3)
			 * DOCNUM IDoc number CHAR(16)
			 * SEGNUM segment number CHAR(6)
			 * PSGNUM parent segment number CHAR(6)
			 * HLEVEL hierarchy level CHAR(2)
			 * SDATA actual segment data CHAR(1000)
			 */
			this.logger.log(Level.FINEST, "Segment data: {0}", segmentData); //$NON-NLS-1$

			String docnum = this.segmentData.substring(33, 49);
			this.logger.log(Level.FINEST, "docnum: {0}", docnum); //$NON-NLS-1$

			String segnum = this.segmentData.substring(49, 55);
			this.logger.log(Level.FINEST, "segnum: {0}", segnum); //$NON-NLS-1$

			String psgnum = this.segmentData.substring(55, 61);
			this.logger.log(Level.FINEST, "psgnum: {0}", psgnum); //$NON-NLS-1$

			this.dataSetCommon.accessorADMDOCNUM.setString(docnum);
			this.dataSetCommon.accessorADMSEGNUM.setString(segnum);
			this.dataSetCommon.accessorADMPSEGNUM.setString(psgnum);

			// process link columns in the order of the IDoc fields
			this.logger.log(Level.FINEST, "Length of segmentData: {0}", segmentData.length()); //$NON-NLS-1$

			int start = 63;
			int end = start;
			for (int i = 0; i < this.dataSetCommon.columnsInOrderOfIDocFields.length; i++) {
				String value = ""; //$NON-NLS-1$
				LinkColumn col = this.dataSetCommon.columnsInOrderOfIDocFields[i];
				int length = col.idocFieldLength;
				end = end + length;
				if (col.field == null) {
					this.logger.log(Level.FINEST, "Column field does not exist on link"); //$NON-NLS-1$					
				} else {
					this.logger.log(Level.FINEST, "Column field: " + col.field.getName()); //$NON-NLS-1$

					/* container alignment for non-unicode multi byte characters */
					char[] data = this.segmentData.substring(start, start + length).toCharArray();
					IDocFieldAlignmentResult result = this.fieldAligner.alignIDocFieldForExtract(col.field.getName(), docnum, data, length);
					String alignedData = new String(result.getData());
					int alignedLength = result.getLength();
					String unalignedData = segmentData.substring(start, start + alignedLength);
					this.segmentData = this.segmentData.replace(unalignedData, alignedData);

					if (end <= this.segmentData.length()) {
						value = this.segmentData.substring(start, end);

						this.logger.log(Level.FINEST, "Column name: {0}, value \"{1}\", start {2}, end {3}, field length {4}", new Object[] { col.field.getName(), value, start, end, length }); //$NON-NLS-1$
						passColumnValueToAccessor(col, value);
					} else {
						this.logger.log(Level.WARNING, "CC_IDOC_EXTRACT_SegmentDataTooShort", //$NON-NLS-1$ 
								new Object[] { docnum, segnum, psgnum });
					}
				}
				start = end;
			}
			this.logger.exiting(CLASSNAME, METHODNAME);
		}
	}

	@Override
	public void complete() {
		final String METHODNAME = "complete"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);
		this.logger.exiting(CLASSNAME, METHODNAME);
	}

	@Override
	public void init() {
		final String METHODNAME = "init"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);
		this.logger.exiting(CLASSNAME, METHODNAME);
	}

	@Override
	public void prepare() {
		final String METHODNAME = "prepare"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);
		this.dataSetCommon.prepare(m_dataSetDef, m_outAccessorMap);
		
		// trace out all columns and some of their attributes
		try {
			StringBuffer traceBuf = new StringBuffer();

			for(LinkColumn col : this.dataSetCommon.columnsInOrderOfIDocFields) {
				traceBuf.append(Constants.LINE_SEPARATOR);	 
				traceBuf.append("Column = "); //$NON-NLS-1$
				if (col.field == null) {
					traceBuf.append("(DS field does not exist)"); //$NON-NLS-1$
				} else {
				   traceBuf.append(col.field.getName()); 
				   traceBuf.append(" - ODBC type = "); 				//$NON-NLS-1$
				   traceBuf.append(col.field.getODBCType());
				}
				traceBuf.append(" - isNull = "); 					//$NON-NLS-1$
				if (col.accessor != null) {
					traceBuf.append(col.accessor.isNull());
				}
				else {
					traceBuf.append("(null)");							//$NON-NLS-1$
				}
			}
			traceBuf.append(Constants.LINE_SEPARATOR);						
			this.logger.finest(traceBuf.toString());
		}
		catch(Exception e) {
			this.logger.log(Level.FINEST, "Unexpected trace exception", e);   //$NON-NLS-1$
		}
		
		this.logger.exiting(CLASSNAME, METHODNAME);
	}
	
	@Override
	public void term() {
		final String METHODNAME = "term"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);
		this.logger.exiting(CLASSNAME, METHODNAME);
	}

	/**
	 * Return the value of the SEGNAM property, 
	 * e.g. a DEBMAS segment: 
	 * 			SEGNAM: E2KNA1M005
	 * 			SEGTYP: E1KNA1M   
	 */
	public String getSegmentName() {
		return this.dataSetCommon.segmentName;
	}

	/**
	 * @param segmentData a string of ~1000 characters containing the actual data of a segment
	 */
	public void setSegmentData(String segmentData, boolean isControlRecord) {
		this.segmentData = segmentData;
		this.isControlRecord = isControlRecord;
	}

	/**
	 * Pass the value to the accessor and convert it if necessary. 
	 * 
	 * @param lc link column 
	 * @param value field value from the segment
	 */
	private void passColumnValueToAccessor(LinkColumn lc, String value) {
		final String METHODNAME = "passColumnValueToAccessor(LinkColumn lc, String value)"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME, new Object[]{lc.field.getName(), value, lc.sapType});

		CC_Accessor acc = lc.accessor;

		// defect 130849, osuhre
		// remove check for nullable fields
		if (acc != null) { 

			switch (lc.field.getODBCType()) {
			case CC_DataField.CC_ODBCTYPE_DATE:
				acc.setCalendar(CCFUtils.formatDateForDS(value));
				break;
			case CC_DataField.CC_ODBCTYPE_TIME:
				acc.setCalendar(CCFUtils.formatTimeForDS(value));
				break;
			case CC_DataField.CC_ODBCTYPE_TIMESTAMP:
				acc.setCalendar(CCFUtils.formatTimeStampForDS(value));
				break;
			case CC_DataField.CC_ODBCTYPE_CHAR:
			case CC_DataField.CC_ODBCTYPE_VARCHAR:
				int maxLength = lc.field.getMaxLength();
				String val = value;
				if (maxLength < value.length()) {
					val = value.substring(0, maxLength);
				}
				acc.setString(val);

			default:
				lc.accessor.setString(value); 
				break;
			}
		} 

		this.logger.exiting(CLASSNAME, METHODNAME);

	}	
}
