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
// Module Name : com.ibm.is.sappack.dsstages.common.impl
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.dsstages.common.impl;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.is.sappack.dsstages.common.Constants;
import com.ibm.is.sappack.dsstages.common.IDocField;
import com.ibm.is.sappack.dsstages.common.IDocType;
import com.ibm.is.sappack.dsstages.common.StageLogger;
import com.ibm.is.sappack.dsstages.common.util.IDocMetadataFileHandler;
import com.sap.conn.jco.AbapException;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.JCoTable;

public class SapIDocTypeRetriever {

	static String copyright() {
		return com.ibm.is.sappack.dsstages.common.impl.Copyright.IBM_COPYRIGHT_SHORT;
	}

	// constants
	static final String CLASSNAME = SapIDocTypeRetriever.class.getName();
	static final String EMPTY = ""; //$NON-NLS-1$

	// members
	private String system;
	private JCoDestination destination;
	private Logger logger;

	public SapIDocTypeRetriever(JCoDestination dest, String sapSystemName) {
		this.system = sapSystemName;
		this.destination = dest;
		this.logger = StageLogger.getLogger();
	}

	private Object[] queryIDoctype(String IDoctype, String Basictype, String release, boolean isExtendedType) throws JCoException {
		final String METHODNAME = "queryIDoctype(String IDoctype, String Basictype, String release, boolean isExtendedType)"; //$NON-NLS-1$
		this.logger.entering(CLASSNAME, METHODNAME);

		JCoTable segmentTable = null;
		JCoTable fieldTable = null;
		JCoStructure peHeaderStruct = null;

		JCoFunction function = destination.getRepository().getFunction(Constants.FUNC_IDOCTYPE_READ_COMPLETE);
		
		// for extension IDoc types we need different function parameters
		if (isExtendedType) {
			function.getImportParameterList().setValue(Constants.PARAM_PI_IDOCTYP, Basictype);
			function.getImportParameterList().setValue(Constants.PARAM_PI_CIMTYP, IDoctype);
		}
		else {
			function.getImportParameterList().setValue(Constants.PARAM_PI_IDOCTYP, IDoctype);
		}

		function.getImportParameterList().setValue(Constants.PARAM_PI_RELEASE, release != null ? release : EMPTY); 

		this.logger.fine("Calling IDOCTYPE_READ_COMPLETE..."); //$NON-NLS-1$
		function.execute(destination);
		this.logger.fine("IDOCTYPE_READ_COMPLETE finished"); //$NON-NLS-1$

		segmentTable = function.getTableParameterList().getTable(Constants.PARAM_PT_SEGMENTS);
		fieldTable = function.getTableParameterList().getTable(Constants.PARAM_PT_FIELDS);
		peHeaderStruct = function.getExportParameterList().getStructure(Constants.PARAM_PE_HEADER);
		Object[] result = new Object[] { segmentTable, fieldTable, peHeaderStruct };
		this.logger.exiting(CLASSNAME, METHODNAME);
		return result;
	}

	public IDocType retrieveIDocTypeFromSAP(String idocTypeName, String basicTypeName, String release) throws JCoException {
		final String METHODNAME = "retrieveIDocTypeFromSAP(String, String, String)"; //$NON-NLS-1$

		logger.entering(CLASSNAME, METHODNAME);

		logger.log(Level.INFO, "CC_IDOC_TypeMetadataSAPRetrieval", new Object[] { idocTypeName });  //$NON-NLS-1$
		
		IDocTypeImpl result = null;

		if (basicTypeName != null && !basicTypeName.equalsIgnoreCase(EMPTY)) {
			logger.log(Level.FINE, "Trying to retrieve IDocType {0} as extension of type {1}", new Object[]{idocTypeName, basicTypeName}); //$NON-NLS-1$
			try {
				result = getIDocType(idocTypeName, basicTypeName, release, true);
			}			
			catch (AbapException abapExc) {
				String s = abapExc.getKey();
				if (Constants.ABAP_EXCEPTION_KEY_IDOCTYPE_READ_COMPLETE_IDOC_NOT_FOUND.equals(s)) {
					// IDoc type does not exist
					this.logger.log(Level.WARNING, "CC_IDOC_ExtensionIDocTypeNotFoundInSAP", new Object[] { idocTypeName }); //$NON-NLS-1$
					this.logger.log(Level.WARNING, "", abapExc); //$NON-NLS-1$
					result = null;
				}
				else {
					// other exception
					throw abapExc;
				}
			}
		}
		else {
			logger.fine(MessageFormat.format("Trying to retrieve IDocType {0} as basic type", idocTypeName)); //$NON-NLS-1$

			try {
				result = getIDocType(idocTypeName, basicTypeName, release, false);
			}
			catch (AbapException abapExc) {
				String s = abapExc.getKey();
				if (Constants.ABAP_EXCEPTION_KEY_IDOCTYPE_READ_COMPLETE_IDOC_NOT_FOUND.equals(s)) {
					// IDoc type does not exist
					this.logger.log(Level.WARNING, "CC_IDOC_BasicIDocTypeNotFoundInSAP", new Object[] {idocTypeName } ); //$NON-NLS-1$
					this.logger.log(Level.WARNING, "",  abapExc); //$NON-NLS-1$

					result = null;
				}
				else {
					// other exception
					throw abapExc;
				}
			}
		}

		logger.exiting(CLASSNAME, METHODNAME);
		
		return result;
	}

	private IDocTypeImpl getIDocType(String idocTypeName, String basicTypeName, String release, boolean isExtendedType) throws JCoException {
		final String METHODNAME = "getIDocType(String, String, String, boolean)"; //$NON-NLS-1$

		logger.entering(CLASSNAME, METHODNAME);
		// Set Segments to IDocTypeMetaData
		HashMap<String, IDocSegmentImpl> typeSegmentMap = new HashMap<String, IDocSegmentImpl>();

		IDocTypeImpl docType = new IDocTypeImpl();
		
		docType.iDocTypeName = idocTypeName;
		docType.basicTypeName = basicTypeName;
		docType.setRelease(release);

		// add control record
		ControlRecord cr = this.getControlRecordMetaData(docType);
		docType.setControlRecord(cr);

		// add other segments
		Object[] results = queryIDoctype(idocTypeName, basicTypeName, release, isExtendedType);
		JCoTable segmentTable = (JCoTable) results[0];
		JCoTable fieldTable = (JCoTable) results[1];
		JCoStructure peHeaderStruct = (JCoStructure) results[2];

		// fill additional metadata required for writing to an IDoc metadata file
		// later on
		if (peHeaderStruct == null) {
			logger.fine("PE_HEADER table is not defined, returning null"); //$NON-NLS-1$
			logger.exiting(CLASSNAME, METHODNAME);
			return null;
		}

		docType.setIDocTypeDescription(peHeaderStruct.getString(Constants.IDOC_METADATA_IDOC_FIELD_DESCRP).trim());

		// go over all segments
		if ((segmentTable != null) && (!segmentTable.isEmpty())) {
			do {
				IDocSegmentImpl segment = new IDocSegmentImpl(docType);
				segment.setSegmentNr(segmentTable.getLong(Constants.IDOC_METADATA_SEGM_FIELD_NR));
				String segmentTypeName = segmentTable.getString(Constants.IDOC_METADATA_SEGM_FIELD_SEGMENTTYP).trim();
				logger.log(Level.FINER, "Processing segment type: {0}", segmentTypeName); //$NON-NLS-1$
				segment.setSegmentTypeName(segmentTypeName);
				segment.setSegmentDefinitionName(segmentTable.getString(Constants.IDOC_METADATA_SEGM_FIELD_SEGMENTDEF)
				      .trim());
				segment.setSegmentDescription(segmentTable.getString(Constants.IDOC_METADATA_SEGM_FIELD_DESCRP).trim());
				segment.setMinOccurrence(segmentTable.getLong(Constants.IDOC_METADATA_SEGM_FIELD_OCCMIN));
				segment.setMaxOccurrence(segmentTable.getLong(Constants.IDOC_METADATA_SEGM_FIELD_OCCMAX));
				segment.setHierarchyLevel(segmentTable.getLong(Constants.IDOC_METADATA_SEGM_FIELD_HLEVEL));

				String mandatory = segmentTable.getString(Constants.IDOC_METADATA_SEGM_FIELD_MUSTFL).trim();

				if (mandatory.equalsIgnoreCase(Constants.IDOC_METADATA_SEGM_FIELD_MUSTFL_SET)) {
					segment.setMandatory(true);
				}

				addSegmentFields(segment, fieldTable);
				typeSegmentMap.put(segment.getSegmentTypeName(), segment);
			}
			while (segmentTable.nextRow());
		}

		// set parent child relationship

		if ((segmentTable != null) && (!segmentTable.isEmpty())) {
			segmentTable.firstRow();
			do {
				String parseg = segmentTable.getString(Constants.IDOC_METADATA_SEGM_FIELD_PARSEG);
				String segtype = segmentTable.getString(Constants.IDOC_METADATA_SEGM_FIELD_SEGMENTTYP).trim();
				IDocSegmentImpl currentSegment = typeSegmentMap.get(segtype);
				if (!parseg.trim().isEmpty()) {
					IDocSegmentImpl parentSegment = typeSegmentMap.get(parseg);
					parentSegment.getChildSegments().add(currentSegment);
					currentSegment.setParent(parentSegment);
					logger.log(Level.FINEST,
					      "Setting parent / child relationship between {0} and {1}", new Object[] { segtype, parseg }); //$NON-NLS-1$
				}
				else {
					logger.log(Level.FINEST, "Setting root segment: {0}", segtype); //$NON-NLS-1$
					docType.getRootSegments().add(currentSegment);
				}

			}
			while (segmentTable.nextRow());
		}

		// we want to store the IDoc metadata information in an IDoc metadata file
		IDocMetadataFileHandler metadataFileHandler = new IDocMetadataFileHandler();
		metadataFileHandler.initialize(this.system, idocTypeName, basicTypeName, release);

		// check if there is an existing IDoc metadata file for this IDoc type
		// just for sanity - at this point there should be no such file
		// only exception: concurrent job runs for the same IDoc type
		if (!metadataFileHandler.hasMetadataFile()) {

			// check if the metadata file could be written
			if (!metadataFileHandler.writeMetadataToFile(docType)) {
				logger.log(Level.WARNING, "CC_IDOC_TypeMetadataFileWriteError", new Object[] { metadataFileHandler.getFileName() });  //$NON-NLS-1$
			}
		}

		logger.exiting(CLASSNAME, METHODNAME);
		return docType;
	}

	protected void addSegmentFields(IDocSegmentImpl segment, JCoTable fieldsTable) {
		if (segment != null) {
			if ((fieldsTable != null) && (!fieldsTable.isEmpty())) {
				fieldsTable.firstRow();
				do {
					String segmentTyp = fieldsTable.getString(Constants.IDOC_METADATA_FLDS_FIELD_SEGMENTTYP.trim());
					if (segmentTyp.equals(segment.getSegmentTypeName())) {
						IDocField field = createFieldFromEDI_IAPIStructure(segment, fieldsTable);
						this.logger.log(Level.FINEST, "Adding field {0}", field.getFieldName()); //$NON-NLS-1$
						segment.getFields().add(field);
					}
				}
				while (fieldsTable.nextRow());
			}
		}
		else {
			logger.log(Level.FINE, "Segment is null"); //$NON-NLS-1$
		}

	}

	/**
	 * This method extracts an IDoc field from a JCoTable of type EDI_IAPI12 or EDI_IAPI16. It is re-used to create
	 * fields form regular segments as well as form the control record.
	 * 
	 * @param segment
	 * @param fieldsTable
	 * @return
	 */
	private IDocField createFieldFromEDI_IAPIStructure(IDocSegmentImpl segment, JCoTable fieldsTable) {
		String fieldName = fieldsTable.getString(Constants.IDOC_METADATA_FLDS_FIELD_FIELDNAME).trim();
		String dataTypeName = fieldsTable.getString(Constants.IDOC_METADATA_FLDS_FIELD_DATATYPE).trim();
		int intLength = fieldsTable.getInt(Constants.IDOC_METADATA_FLDS_FIELD_INTLEN);
		int length = fieldsTable.getInt(Constants.IDOC_METADATA_FLDS_FIELD_EXTLEN);
		String description = fieldsTable.getString(Constants.IDOC_METADATA_FLDS_FIELD_DESCRP).trim();

		IDocFieldImpl field = new IDocFieldImpl(segment);
		field.setFieldName(fieldName);
		field.setLength(intLength);
		field.setLengthAsString(length);
		field.setSAPType(dataTypeName);
		field.setFieldDescription(description);

		return field;
	}

	private ControlRecord getControlRecordMetaData(IDocType idocType) throws JCoException {
		ControlRecord cr = new ControlRecord(idocType);
		JCoFunction function = destination.getRepository().getFunction(Constants.FUNC_IDOC_RECORD_READ);
		function.execute(destination);
		JCoTable dcFields = function.getTableParameterList().getTable(Constants.PARAM_DC_FIELDS);
		if (!dcFields.isEmpty()) {
			do {
				IDocField field = this.createFieldFromEDI_IAPIStructure(cr, dcFields);
				cr.getFields().add(field);
			}
			while (dcFields.nextRow());
		}
		return cr;
	}
}
