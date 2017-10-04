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
// Module Name : com.ibm.is.sappack.dsstages.idocload.jcconnector
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.dsstages.idocload.jcconnector;


import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ascential.e2.connector.CC_RecordDataSetConsumer;
import com.ascential.e2.daapi.CC_Accessor;
import com.ascential.e2.daapi.CC_DataSet;
import com.ascential.e2.daapi.metadata.CC_DataField;
import com.ascential.e2.daapi.metadata.CC_DataSetDef;
import com.ascential.e2.propertyset.CC_ErrorList;
import com.ascential.e2.propertyset.CC_PropertySet;
import com.ibm.is.sappack.dsstages.common.Constants;
import com.ibm.is.sappack.dsstages.common.IDocType;
import com.ibm.is.sappack.dsstages.common.StageLogger;
import com.ibm.is.sappack.dsstages.common.ccf.CCFUtils;
import com.ibm.is.sappack.dsstages.idoc.IDocDataSetProducerConsumerCommon;
import com.ibm.is.sappack.dsstages.idoc.IDocFieldAligner;
import com.ibm.is.sappack.dsstages.idoc.IDocFieldAlignmentResult;
import com.ibm.is.sappack.dsstages.idoc.IDocDataSetProducerConsumerCommon.LinkColumn;
import com.ibm.is.sappack.dsstages.idocload.ControlRecordData;
import com.ibm.is.sappack.dsstages.idocload.ControlRecordDataImpl;
import com.ibm.is.sappack.dsstages.idocload.SegmentCollector;
import com.ibm.is.sappack.dsstages.idocload.SegmentData;
import com.ibm.is.sappack.dsstages.idocload.SegmentDataImpl;


/*
 * The DataSetConsumer for each ingoing link into the IDoc load stage.
 * Can handle normal segments as well as the control record.
 * 
 * During prepare(), LinkColumn objects are put into the columnsInOrderOfIDocFields array
 * in the order as defined in SAP (and as required by the IDocSender).
 */
public class IDocLoadDataSetConsumer extends CC_RecordDataSetConsumer {

	static String copyright() {
		return com.ibm.is.sappack.dsstages.idocload.jcconnector.Copyright.IBM_COPYRIGHT_SHORT;
	}

	private static String CLASSNAME = IDocLoadDataSetConsumer.class.getName();

	Logger logger = null;

	IDocDataSetProducerConsumerCommon linkBase;
	SegmentCollector segmentCollector;
	boolean endOfDataReceived = false;
	private IDocFieldAligner fieldAligner = null;


	public IDocLoadDataSetConsumer(IDocType idocType, CC_PropertySet propSet, CC_ErrorList errList,
	      SegmentCollector segmentCollector, IDocFieldAligner fieldAligner) {
		super();
		this.logger = StageLogger.getLogger();
		final String METHODNAME = "<init>"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);
		try {
			linkBase = new IDocDataSetProducerConsumerCommon(idocType, propSet, errList, getRequiredLinkProperties());
			this.segmentCollector = segmentCollector;
		}
		catch (Exception exc) {
			logger.log(Level.SEVERE, "CC_IDOC_LOAD_UnexpectedException", exc); //$NON-NLS-1$
			CCFUtils.throwCC_Exception(exc);
		}
		this.fieldAligner = fieldAligner;
		this.logger.exiting(CLASSNAME, METHODNAME);
	}

	String[] getRequiredLinkProperties() {
		return new String[] { Constants.IDOCSTAGE_LINK_PROP_SEGTYP, };
	}

	@Override
	public void prepare() {
		final String METHODNAME = "prepare()"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);
		this.linkBase.prepare(m_dataSetDef, m_inAccessorMap);
		this.logger.exiting(CLASSNAME, METHODNAME);
	}

	void processOneTopLevelDataItemSegment() {
		final String METHODNAME = "processOneTopLevelDataItemSegment()"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);
		char[] segmentData = new char[Constants.RFM_IDOCTYPE_READ_COMPLETE_SIZEOF_SDATA];
		Arrays.fill(segmentData, ' ');

		// docNum, segNum and psegNum may be null
		String docNum = this.linkBase.accessorADMDOCNUM.isNull() ? null : new String(this.linkBase.accessorADMDOCNUM.getString());
		String segNum = null;
		String psegNum = null;
		if (!this.linkBase.isControlRecord) {
			segNum = this.linkBase.accessorADMSEGNUM.isNull() ? null : new String(this.linkBase.accessorADMSEGNUM.getString());
			psegNum = this.linkBase.accessorADMPSEGNUM.isNull() ? null : new String(this.linkBase.accessorADMPSEGNUM.getString());
		}

		// currentIndex indicates where in the segmentData char array to insert the next datum
		int currentIndex = 0;
		// process link columns in the order of the IDoc fields
		// this is crucial to fill the segmentData array correctly
		for (int i = 0; i < this.linkBase.columnsInOrderOfIDocFields.length; i++) {
			LinkColumn col = this.linkBase.columnsInOrderOfIDocFields[i];
			int length = col.idocFieldLength;
			if (this.logger.isLoggable(Level.FINEST)) {
				if (col.field == null) {
					this.logger.log(Level.FINEST, "IDoc field is not present on link, ignoring it"); //$NON-NLS-1$
				} else {
					this.logger.log(Level.FINEST, "Converting field ''{0}''", col.field.getName()); //$NON-NLS-1$
				}
			}
			char[] data = null;
			if (col.field != null) {
				data = this.convertColumnToString(col);

				// align IDoc fields - important if we're using on non-unicode multi byte SAP system
				IDocFieldAlignmentResult alignmentResult = this.fieldAligner.alignIDocFieldForLoad(col.field.getName(), docNum, data, length);
				// update the field length
				length = alignmentResult.getLength();
				// update the field data
				data = alignmentResult.getData();
			}
		
			// if data is null, this means that there is no column on the link for the ith
			// IDoc field. If this is the case, just skip it
			if (data != null) {
				// copy the string value into the segmentData array at currentIndex
				System.arraycopy(data, 0, segmentData, currentIndex, Math.min(data.length, length));
			}

			// set currentIndex to next field in any case
			currentIndex += length;
		}
		this.logger.log(Level.FINEST, "Index of finished row: {0}", currentIndex); //$NON-NLS-1$

		// segmentData is now completely filled, add it to the segment collector
		if (this.linkBase.isControlRecord) {
			if (this.logger.isLoggable(Level.FINEST)) {
				this.logger.log(Level.FINEST, "Inserting control record {0}", docNum); //$NON-NLS-1$
			}
			ControlRecordData crd = new ControlRecordDataImpl(this.linkBase.idocType.getControlRecord(), segmentData);
			this.segmentCollector.setControlRecord(docNum, crd);
		}
		else {
			if (this.logger.isLoggable(Level.FINEST)) {
				this.logger.log(Level.FINEST, "Inserting segment {0},{1},{2}", new Object[] { docNum, segNum, psegNum }); //$NON-NLS-1$
			}
			SegmentData segData = new SegmentDataImpl(this.linkBase.segment.getSegmentDefinitionName(), segmentData);
			this.segmentCollector.insertSegment(docNum, segNum, psegNum, this.linkBase.segment, segData);
		}
		this.logger.exiting(CLASSNAME, METHODNAME);
	}

	private char[] convertColumnToString(LinkColumn lc) {
		CC_Accessor acc = lc.accessor;
		char[] result = null;
		
		if (acc == null) {
			// if field is not on link, make a best guess for empty types based on the SAP type
			if (lc.sapType.equals("DATS")) { //$NON-NLS-1$
				if (lc.idocFieldLength != 8) {
					this.logger.log(Level.FINE, "IDoc field length is ''{0}'' although it is a DATS and should be 8", lc.idocFieldLength); //$NON-NLS-1$
				}
				final char[] emptyDate = "00000000".toCharArray(); //$NON-NLS-1$
				result = emptyDate;
			} else if (lc.sapType.equals("TIMS")) { //$NON-NLS-1$
				if (lc.idocFieldLength != 6) {
					this.logger.log(Level.FINE, "IDoc field length is ''{0}'' although it is a TIMS and should be 6", lc.idocFieldLength); //$NON-NLS-1$
				}
				final char[] emptyTimeStamp = "000000".toCharArray(); //$NON-NLS-1$
				result = emptyTimeStamp;
			}			
		} else if (!acc.isNull()) {
			// TODO distinguish other types
			switch (lc.field.getODBCType()) {
			case CC_DataField.CC_ODBCTYPE_DATE:
				result = CCFUtils.formatDateForSAP(acc.getCalendar(), lc.idocFieldLength);
				break;
			case CC_DataField.CC_ODBCTYPE_TIME:
				result = CCFUtils.formatTimeForSAP(acc.getString(), lc.idocFieldLength);
				break;
			case CC_DataField.CC_ODBCTYPE_TIMESTAMP:
				if (lc.sapType.equals("DATS")) { //$NON-NLS-1$
					result = CCFUtils.formatDateForSAP(acc.getCalendar(), lc.idocFieldLength);					
				} else if (lc.sapType.equals("TIMS")) { //$NON-NLS-1$
					result = CCFUtils.formatTimestampForSAP(acc.getCalendar(), lc.idocFieldLength);
				}
				break;
			default:
				result = acc.getString();
				break;
			}
		}

		return result;
	}

	@Override
	protected void processOneTopLevelDataItem(boolean isEOD) {
		final String METHODNAME = "processOneTopLevelDataItem(boolean)"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);
		try {
			if (!isEOD) {
				this.processOneTopLevelDataItemSegment();
			}
			else {
				logger.log(Level.FINE, "End of data received"); //$NON-NLS-1$
				endOfDataReceived = true;
			}
		}
		catch (Exception e) {
			logger.log(Level.SEVERE, "CC_IDOC_LOAD_UnexpectedException", e); //$NON-NLS-1$
			CCFUtils.throwCC_Exception(e);
		}
		this.logger.exiting(CLASSNAME, METHODNAME);
	}

	@Override
	public int consumeOneTopLevelDataItem() {
		final String METHODNAME = "consumeOneTopLevelDataItem"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);
		int result = super.consumeOneTopLevelDataItem();
		this.logger.exiting(CLASSNAME, METHODNAME);
		return result;
	}

	@Override
	public CC_DataSetDef getDataSetDef() {
		final String METHODNAME = "getDataSetDef"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);
		CC_DataSetDef result = super.getDataSetDef();
		this.logger.exiting(CLASSNAME, METHODNAME);
		return result;
	}

	@Override
	public synchronized void releaseInstance() {
		final String METHODNAME = "releaseInstance"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);
		super.releaseInstance();
		this.logger.exiting(CLASSNAME, METHODNAME);
	}

	@Override
	public void setDataSet(CC_DataSet ds) {
		final String METHODNAME = "setDataSet"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);
		super.setDataSet(ds);
		this.logger.exiting(CLASSNAME, METHODNAME);
	}

	@Override
	public boolean setDataSetDef(CC_DataSetDef dataSetDef, boolean isNegotiable) {
		final String METHODNAME = "setDataSetDef"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);
		boolean result = super.setDataSetDef(dataSetDef, isNegotiable);
		this.logger.exiting(CLASSNAME, METHODNAME);
		return result;
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
	public void term() {
		final String METHODNAME = "term"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);
		this.logger.exiting(CLASSNAME, METHODNAME);
	}

	public boolean getEndOfDataReceived() {
		return this.endOfDataReceived;
	}

	public String getSegmentName() {
		return this.linkBase.segment.getSegmentTypeName();
	}
}
