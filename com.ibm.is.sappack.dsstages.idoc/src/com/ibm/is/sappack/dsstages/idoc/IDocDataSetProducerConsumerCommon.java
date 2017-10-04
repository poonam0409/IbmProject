//---------------------------------------------------------------------------  
// IBM Confidential                                                           
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2009, 2013                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.is.sappack.dsstages.idoc
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.dsstages.idoc;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ascential.e2.daapi.CC_Accessor;
import com.ascential.e2.daapi.metadata.CC_DataField;
import com.ascential.e2.daapi.metadata.CC_DataSetDef;
import com.ascential.e2.propertyset.CC_ErrorList;
import com.ascential.e2.propertyset.CC_Property;
import com.ascential.e2.propertyset.CC_PropertySet;
import com.ibm.is.sappack.dsstages.common.CCFResource;
import com.ibm.is.sappack.dsstages.common.Constants;
import com.ibm.is.sappack.dsstages.common.IDocField;
import com.ibm.is.sappack.dsstages.common.IDocSegment;
import com.ibm.is.sappack.dsstages.common.IDocType;
import com.ibm.is.sappack.dsstages.common.StageLogger;
import com.ibm.is.sappack.dsstages.common.Utilities;
import com.ibm.is.sappack.dsstages.common.ccf.CCFUtils;

/**
 * This class shares code common to the DataSetProducer and DataSetConsumer implementations
 * for IDoc Extract / Load.
 * It is shared in this class because the Producer and Consumer must extend the respective classes
 * of the CCF and thus a common superclass cannot be created.
 */
public class IDocDataSetProducerConsumerCommon {

	static String copyright() {
		return com.ibm.is.sappack.dsstages.idoc.Copyright.IBM_COPYRIGHT_SHORT;
	}

	public static class LinkColumn {
		public CC_Accessor accessor;
		public int idocFieldLength;
		public String sapType;
		public CC_DataField field;
	}

	static private String CLASSNAME = IDocDataSetProducerConsumerCommon.class.getName();

	private Logger logger;

	public IDocType idocType = null;
	public IDocSegment segment = null;
	public boolean isControlRecord = false;
	public Map<String, String> linkProperties = null;
	public String segmentName = null;

	public CC_Accessor accessorADMDOCNUM = null;
	public CC_Accessor accessorADMSEGNUM = null;
	public CC_Accessor accessorADMPSEGNUM = null;

	public LinkColumn[] columnsInOrderOfIDocFields = null;

	public IDocDataSetProducerConsumerCommon(IDocType idocType, CC_PropertySet propSet, CC_ErrorList errList, String[] requiredLinkProperties) {
		super();
		this.logger = StageLogger.getLogger();
		final String METHODNAME = "<init>"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);
		this.idocType = idocType;
		CC_Property prop = propSet.getProperty("/Usage"); //$NON-NLS-1$
		this.logger.log(Level.FINE, "Link property: {0}", prop); //$NON-NLS-1$
		
		
		/* call createPropertyMap without requiredLinkProperties - we need to manually check if SEGTYP is set */
		this.linkProperties = CCFUtils.createPropertyMap(prop, errList, null);
		/* check if SEGTYP is set */
		if(this.linkProperties.get(Constants.IDOCSTAGE_LINK_PROP_SEGTYP) == null) {
			String msgId = "CC_IDOC_SEGTYPNotFound"; //$NON-NLS-1$
			this.logger.log(Level.SEVERE, msgId);
			throw new RuntimeException(CCFResource.getCCFMessage(msgId));
		}
		/* call createPropertyMap with requiredLinkProperties - we checked if SEGTYP is set */
		this.linkProperties = CCFUtils.createPropertyMap(prop, errList, requiredLinkProperties);
		
		this.logger.log(Level.FINE, "Link properties: {0}", linkProperties); //$NON-NLS-1$
		this.logger.exiting(CLASSNAME, METHODNAME);
	}

	public void prepare(CC_DataSetDef dataSetDef, Map<?, ?> accessorMap) {
		final String METHODNAME = "prepare()"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);
		try {
			String segmentTypeName = this.linkProperties.get(Constants.IDOCSTAGE_LINK_PROP_SEGTYP);
			this.logger.log(Level.FINE, "Finding segment type {0}", segmentTypeName); //$NON-NLS-1$

			this.segmentName = this.linkProperties.get(Constants.IDOCSTAGE_LINK_PROP_SEGNAM);

			// for control record, the segment namer can be null --> assign directly
			if (segmentName == null && Constants.IDOC_CONTROL_RECORD_SEGMENT_TYPE_NAME.equalsIgnoreCase(segmentTypeName)) {
				segmentName = Constants.IDOC_CONTROL_RECORD_SEGMENT_DEFINITION_NAME;
			}
			this.logger.log(Level.FINE, "Set segment name (SEGNAM) {0}", segmentName); //$NON-NLS-1$

			// determine which IDocSegment to process
			IDocSegment seg = null;
			if (com.ibm.is.sappack.dsstages.common.Constants.IDOC_CONTROL_RECORD_SEGMENT_TYPE_NAME.equals(segmentTypeName)) {
				seg = this.idocType.getControlRecord();
				this.isControlRecord = true;
				this.logger.log(Level.FINE, "Found control record"); //$NON-NLS-1$
			} else {
				seg = Utilities.findIDocSegment(this.idocType, segmentTypeName);
				this.logger.log(Level.FINEST, "seg = " + seg); //$NON-NLS-1$

				this.isControlRecord = false;
				this.accessorADMSEGNUM = (CC_Accessor) accessorMap.get("/" + Constants.IDOCSTAGE_TECHNICAL_FIELD_ADM_SEGNUM); //$NON-NLS-1$
				this.logger.log(Level.FINEST, "Accessor for ADM_SEGNUM: {0}", this.accessorADMSEGNUM); //$NON-NLS-1$
				if (this.accessorADMSEGNUM == null) {
					String msgId = "CC_IDOC_AdminFieldNotFound"; //$NON-NLS-1$
					this.logger.log(Level.SEVERE, msgId, Constants.IDOCSTAGE_TECHNICAL_FIELD_ADM_SEGNUM);
					throw new IDocRuntimeException(CCFResource.getCCFMessage(msgId, Constants.IDOCSTAGE_TECHNICAL_FIELD_ADM_SEGNUM));
				}
				this.accessorADMPSEGNUM = (CC_Accessor) accessorMap.get("/" + Constants.IDOCSTAGE_TECHNICAL_FIELD_ADM_PSGNUM); //$NON-NLS-1$
				this.logger.log(Level.FINEST, "Accessor for ADM_PSGNUM: {0}", this.accessorADMPSEGNUM); //$NON-NLS-1$
				if (this.accessorADMPSEGNUM == null) {
					String msgId = "CC_IDOC_AdminFieldNotFound"; //$NON-NLS-1$
					this.logger.log(Level.SEVERE, msgId, Constants.IDOCSTAGE_TECHNICAL_FIELD_ADM_PSGNUM);
					throw new IDocRuntimeException(CCFResource.getCCFMessage(msgId, Constants.IDOCSTAGE_TECHNICAL_FIELD_ADM_PSGNUM));
				}
			}
			this.accessorADMDOCNUM = (CC_Accessor) accessorMap.get("/" + Constants.IDOCSTAGE_TECHNICAL_FIELD_ADM_DOCNUM); //$NON-NLS-1$
			this.logger.log(Level.FINEST, "Accessor for ADM_DOCNUM: {0}", this.accessorADMDOCNUM); //$NON-NLS-1$
			if (this.accessorADMDOCNUM == null) {
				String msgId = "CC_IDOC_AdminFieldNotFound"; //$NON-NLS-1$
				this.logger.log(Level.SEVERE, msgId, Constants.IDOCSTAGE_TECHNICAL_FIELD_ADM_DOCNUM);
				throw new IDocRuntimeException(CCFResource.getCCFMessage(msgId, Constants.IDOCSTAGE_TECHNICAL_FIELD_ADM_DOCNUM));
			}
			if (seg == null) {
				String msgId = "CC_IDOC_TypeMetadataSegmentTypeNotFound"; //$NON-NLS-1$
				this.logger.log(Level.SEVERE, msgId, new Object[]{ segmentTypeName, this.idocType.getIDocTypeName() });
				throw new IDocRuntimeException(CCFResource.getCCFMessage(msgId, new Object[]{ segmentTypeName, 
				                                                                              this.idocType.getIDocTypeName() }));
			}
			this.segment = seg;
			this.prepareIDocSegment(dataSetDef, accessorMap);
		} catch (Exception exc) {
			logger.log(Level.SEVERE, "CC_IDOC_LOAD_UnexpectedException", exc); //$NON-NLS-1$
			CCFUtils.throwCC_Exception(exc);
		}
		this.logger.exiting(CLASSNAME, METHODNAME);
	}

	private void prepareIDocSegment(CC_DataSetDef dataSetDef, Map<?, ?> accessorMap) {
		final String METHODNAME = "prepareIDocSegment(IDocSegment)"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);

		List<IDocField> idocFields = this.segment.getFields();
		this.columnsInOrderOfIDocFields = new LinkColumn[idocFields.size()];

		// iterate through the IDoc fields
		for (int i = 0; i < idocFields.size(); i++) {
			IDocField idocField = idocFields.get(i);
			String idocFieldName = idocField.getFieldName();
			this.logger.log(Level.FINEST, "looking for accessor for IDoc field name: {0}", idocFieldName); //$NON-NLS-1$

			LinkColumn col = new LinkColumn();
			this.columnsInOrderOfIDocFields[i] = col;

			int strLength = idocField.getLengthAsString();
			col.idocFieldLength = strLength;

			// set the SAP data type
			col.sapType = idocField.getSAPType();

			CC_DataField root = dataSetDef.getRoot();
			this.logger.log(Level.FINEST, "root data field: {0}", root); //$NON-NLS-1$
			if (root != null) {
				this.logger.log(Level.FINEST, "root data field name: {0}", root.getName()); //$NON-NLS-1$
			}

			// now find the link column corresponding to idocField
			CC_Accessor currentAcc = null;
			CC_DataField currentDataField = null;
			if (root != null) {
				for (CC_DataField field = root.getFirstChild(); field != null; field = field.getNextSibling()) {
					String path = field.getPathName();
					String name = field.getName();
					String convertedFieldName = Utilities.cleanFieldName(idocField.getFieldName());
					if (convertedFieldName.equals(name)) {
						this.logger.log(Level.FINEST, "Found field name: {0}", name); //$NON-NLS-1$
						currentAcc = (CC_Accessor) accessorMap.get(path);
						currentDataField = field;
						break;
					}
				}
			}
			// accessor and field might be null here, that means that there is no link column
			// for the IDoc field. Doesn't matter, add the LinkColumn entry anyway just with
			// the field length set above
			this.logger.log(Level.FINEST, "Found accessor: {0}", currentAcc); //$NON-NLS-1$
			if (currentAcc == null) {
				this.logger.log(Level.INFO, "CC_IDOC_FieldNotOnLink", new Object[]{idocFieldName, this.segmentName}); //$NON-NLS-1$
			}
			col.accessor = currentAcc;
			col.field = currentDataField;
		}
		this.logger.log(Level.FINEST, "Accessor fields lengths: {0}", this.columnsInOrderOfIDocFields.length); //$NON-NLS-1$

		this.logger.exiting(CLASSNAME, METHODNAME);
	}

}
