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
// Module Name : com.ibm.is.sappack.gen.tools.sap.importer.idoctypes
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.tools.sap.importer.idoctypes;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ibm.iis.sappack.gen.common.ui.connections.SapSystem;
import com.ibm.iis.sappack.gen.common.ui.jco.RfcDestinationDataProvider;
import com.ibm.is.sappack.gen.tools.sap.activator.Activator;
import com.ibm.is.sappack.gen.tools.sap.constants.Constants;
import com.ibm.is.sappack.gen.tools.sap.importer.helper.field.IDocField;
import com.ibm.is.sappack.gen.tools.sap.utilities.SAPTableExtractor;
import com.sap.conn.jco.AbapException;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoTable;


public class SapIDocTypeBrowser {

	private JCoDestination destination;

	private Boolean isUnicodeSapSystem;
	private boolean isExtendedType;
	private String release;


	static String copyright() {
		return com.ibm.is.sappack.gen.tools.sap.importer.idoctypes.Copyright.IBM_COPYRIGHT_SHORT;
	}

	// private static final RfcDestinationDataProvider destinationDataProvider;
	//
	// static {
	// destinationDataProvider = new RfcDestinationDataProvider();
	// Environment.registerDestinationDataProvider(destinationDataProvider);
	// }
	public SapIDocTypeBrowser(SapSystem sapSystem, boolean extendedType, String release) throws JCoException {
		this.destination = RfcDestinationDataProvider.getDestination(sapSystem);
		isUnicodeSapSystem = Boolean.valueOf(destination.getAttributes().getPartnerCharset().equals("UTF16")); //$NON-NLS-1$
		this.isExtendedType = extendedType;
		this.release = release;
		if (this.release == null) {
			this.release = Constants.IDOC_EMPTY_RELEASE;
		}
	}

	public Boolean getIsUnicodeSapSystem() {
		return isUnicodeSapSystem;
	}

	/**
	 * Calls the "RFC_READ_TABLE" BAPI and returns the result tables "DATA"
	 * 
	 * @param nameFilter
	 * @return the result list of IDoctype with the nameFilter
	 * @throws IDocTypeExtractException
	 *             @
	 */
	/*
	 * private JCoTable selectIDoctypes(String nameFilter) throws
	 * IDocTypeExtractException { JCoTable idoctypes = null; try { String
	 * language = "E"; String option1 = "LANGUA = '" + language + "'"; String
	 * option2 = " AND IDOCTYP LIKE '" + nameFilter.toUpperCase() + "'";
	 * 
	 * JCoFunction function;
	 * 
	 * function = destination.getRepository().getFunction("RFC_READ_TABLE");
	 * 
	 * function.getImportParameterList().setValue("QUERY_TABLE", "EDBAST"); if
	 * (nameFilter.equalsIgnoreCase("")) {
	 * function.getTableParameterList().getTable("OPTIONS").appendRow();
	 * function.getTableParameterList().getTable("OPTIONS").setValue("TEXT",
	 * option1); } else {
	 * function.getTableParameterList().getTable("OPTIONS").appendRow();
	 * function.getTableParameterList().getTable("OPTIONS").setValue("TEXT",
	 * option1 + option2); } function.execute(destination); idoctypes =
	 * function.getTableParameterList().getTable("DATA"); } catch (JCoException
	 * e) { throw new IDocTypeExtractException(e); } return idoctypes;
	 * 
	 * }
	 */

	/**
	 * Calls the "IDOCTYPE_READ_COMPLETE" BAPI and returns the result tables
	 * "PT_SEGMENT".
	 * 
	 * @param IDoctype
	 *            : The IDoctype that structure information(segments) will be extracted.
	 * @return: The "PT_SEGMENTS" result table containing information like: <br>
	 *          <ul>
	 *          <li>The No. of the segment</li>
	 *          <li>The name of the segment type</li>
	 *          <li>The name of the segment definition</li>
	 *          <li>It's parent segment type</li>
	 *          <li>The minimal and maximal occurence</li>
	 *          <li>Mandatory flag</li>
	 *          <li>The description of the segment</li>
	 *          </ul>
	 * @throws IDocTypeExtractException
	 *             @
	 */
	private JCoTable[] queryIDoctype(IDocType docType) throws JCoException {
		JCoFunction function;
		JCoTable segmentTable = null;
		JCoTable messageTable = null;
		JCoTable fieldTable = null;

		String idoctypeParameter = "PI_IDOCTYP"; //$NON-NLS-1$
		if (this.isExtendedType) {
			idoctypeParameter = "PI_CIMTYP"; //$NON-NLS-1$
		}

		function = destination.getRepository().getFunction("IDOCTYPE_READ_COMPLETE"); //$NON-NLS-1$

		function.getImportParameterList().setValue(idoctypeParameter, docType.getName());
		function.getImportParameterList().setValue("PI_RELEASE", this.release); //$NON-NLS-1$ 

		try {
			function.execute(destination);
		} catch (AbapException exc) {
			Activator.logException(exc);
			String s = exc.getKey();
			
// exception catcher should handle this exception case
//			if ("OBJECT_UNKNOWN".equals(s)) { //$NON-NLS-1$
//				return null;
//			}
			
			throw exc;
		}
		segmentTable = function.getTableParameterList().getTable("PT_SEGMENTS"); //$NON-NLS-1$
		messageTable = function.getTableParameterList().getTable("PT_MESSAGES"); //$NON-NLS-1$
		fieldTable = function.getTableParameterList().getTable("PT_FIELDS"); //$NON-NLS-1$

		if (this.isExtendedType) {
			String basicType = function.getExportParameterList().getStructure("PE_HEADER").getString("IDOCTYP"); //$NON-NLS-1$ //$NON-NLS-2$
			docType.setBasicType(basicType);
		}

		return new JCoTable[] { segmentTable, messageTable, fieldTable };
	}

	/**
	 * Get IDoctype List
	 * 
	 * @param filter
	 * @return
	 * @throws IDocTypeExtractException
	 *             @
	 */
	/*
	 * public List<IDocType> listIDoctypesOld(String filter) throws
	 * IDocTypeExtractException {
	 * 
	 * ArrayList<IDocType> idoctypes = new ArrayList<IDocType>(); JCoTable
	 * idoctypelist = selectIDoctypes(filter); if ((idoctypelist != null) &&
	 * (!idoctypelist.isEmpty())) { do { String type =
	 * idoctypelist.getString("WA").substring(0, 29).trim(); String descrp =
	 * idoctypelist.getString("WA").substring(30).trim(); IDocType idocType =
	 * new IDocType(type, descrp, this); idoctypes.add(idocType); } while
	 * (idoctypelist.nextRow()); } else {
	 * System.err.println("ERORR! IDoc type not found!"); } return idoctypes;
	 * 
	 * }
	 */

	/**
	 * Read EDBAS and EDBAST to also catch IDoc types without descriptions
	 * (those are not in EDBAST)
	 */
	public List<IDocType> listIDoctypes(String filter) throws JCoException {

		ArrayList<IDocType> idoctypes = new ArrayList<IDocType>();

		String typeTable = "EDBAS"; //$NON-NLS-1$
		String idocTypeColumnTypeTable = "IDOCTYP"; //$NON-NLS-1$
		if (this.isExtendedType) {
			typeTable = "EDCIM"; //$NON-NLS-1$
			idocTypeColumnTypeTable = "CIMTYP"; //$NON-NLS-1$
		}
		String typeDescTable = "EDBAST"; //$NON-NLS-1$
		String idocTypeColumnTypeDescTable = "IDOCTYP"; //$NON-NLS-1$
		if (this.isExtendedType) {
			typeDescTable = "EDCIMT"; //$NON-NLS-1$
			idocTypeColumnTypeDescTable = "CIMTYP"; //$NON-NLS-1$
		}

		filter = filter.toUpperCase();
		SAPTableExtractor descrExtractor = new SAPTableExtractor(this.destination, typeDescTable, Arrays.asList(new String[] { idocTypeColumnTypeDescTable, "DESCRP" }), idocTypeColumnTypeDescTable //$NON-NLS-1$
				+ " LIKE '" + filter + "' AND LANGUA = 'EN'");  //$NON-NLS-1$//$NON-NLS-2$
		descrExtractor.setSkipLengthCheck(true);
		Map<String, String> idocType2Desc = new HashMap<String, String>();

		SAPTableExtractor.Result descResult = descrExtractor.performQuery();
		while (descResult.nextRow()) {
			idocType2Desc.put(descResult.getValue(idocTypeColumnTypeDescTable), descResult.getValue("DESCRP")); //$NON-NLS-1$
		}

		String whereClause = idocTypeColumnTypeTable + " LIKE '" + filter + "'"; //$NON-NLS-1$ //$NON-NLS-2$
		if (this.release != null && !this.release.trim().isEmpty()) {
			whereClause +=  " AND RELEASED = '" + this.release + "'";   //$NON-NLS-1$//$NON-NLS-2$
		}
		SAPTableExtractor idocTypeExtractor = new SAPTableExtractor(this.destination, typeTable, Arrays.asList(new String[] { idocTypeColumnTypeTable }), whereClause);
		idocTypeExtractor.setSkipLengthCheck(true);

		SAPTableExtractor.Result result = idocTypeExtractor.performQuery();
		while (result.nextRow()) {
			String idocTypeName = result.getValue(idocTypeColumnTypeTable);
			String desc = idocType2Desc.get(idocTypeName);
			IDocType idocType = new IDocType(idocTypeName, this.isExtendedType, desc, this.release, this);
			idoctypes.add(idocType);
		}

		return idoctypes;

	}

	public IDocTypeMetaData getIDocTypeMetaData(IDocType docType) throws JCoException {

		if (docType == null) {
			Activator.getLogger().fine("The IDocType is null!"); //$NON-NLS-1$
			return null;
		}
		// Set Segments to IDocTypeMetaData
		HashMap<String, Segment> typeSegmentMap = new HashMap<String, Segment>();
		ArrayList<Segment> segments = new ArrayList<Segment>();

		// add control record
		ControlRecord cr = this.getControlRecordMetaData(docType);
		typeSegmentMap.put("CONTROL_RECORD", cr); //$NON-NLS-1$
		segments.add(cr);

		// add other segments
		JCoTable[] results      = queryIDoctype(docType);
		if (results == null) {
			return null;
		}
		JCoTable   segmentTable = results[0];
		JCoTable   messageTable = results[1];
		JCoTable   fieldTable   = results[2];

		if ((segmentTable != null) && (!segmentTable.isEmpty())) {
			do {
				Segment segment = new Segment(docType);
				segment.segmentNr = segmentTable.getInt("NR"); //$NON-NLS-1$
				segment.segmentType = segmentTable.getString("SEGMENTTYP").trim(); //$NON-NLS-1$
				segment.segmentDef = segmentTable.getString("SEGMENTDEF").trim(); //$NON-NLS-1$
				segment.segmentDescription = segmentTable.getString("DESCRP").trim(); //$NON-NLS-1$
				segment.setMinOccurrence(segmentTable.getLong("OCCMIN")); //$NON-NLS-1$
				segment.setMaxOccurrence(segmentTable.getLong("OCCMAX")); //$NON-NLS-1$
				// added for 37220
				segment.setParentFlag(segmentTable.getString("PARFLG")); //$NON-NLS-1$
				segment.setGroupMinOccurence(segmentTable.getLong("GRP_OCCMIN")); //$NON-NLS-1$
				segment.setGroupMaxOccurence(segmentTable.getLong("GRP_OCCMAX")); //$NON-NLS-1$
				segment.mandatory = false;
				// osuhre, 114843: fix mandatory segment logic
				// if segment is a parent segment
				if (segmentTable.getString("PARFLG").trim().equalsIgnoreCase("x")) { //$NON-NLS-1$ //$NON-NLS-2$
					if (segmentTable.getString("GRP_MUSTFL").trim().equalsIgnoreCase("x")) { //$NON-NLS-1$ //$NON-NLS-2$
						segment.mandatory = true;
					}	
				} else {
					if (segmentTable.getString("MUSTFL").trim().equalsIgnoreCase("x")) {  //$NON-NLS-1$ //$NON-NLS-2$
						segment.mandatory = true;
					}
				}
				addSegmentFields(segment, fieldTable);
				segment.children = new ArrayList<Segment>();
				typeSegmentMap.put(segment.segmentType, segment);
				segments.add(segment);
			} while (segmentTable.nextRow());
		}

		// set parent child relationship

		if ((segmentTable != null) && (!segmentTable.isEmpty())) {
			segmentTable.firstRow();
			do {
				String parseg = segmentTable.getString("PARSEG"); //$NON-NLS-1$
				if (!parseg.trim().isEmpty()) {
					String segtype = segmentTable.getString("SEGMENTTYP").trim(); //$NON-NLS-1$
					Segment parentSegment = typeSegmentMap.get(parseg);
					Segment currentSegment = typeSegmentMap.get(segtype);

					parentSegment.children.add(currentSegment);
					currentSegment.parent = parentSegment;
				}

			} while (segmentTable.nextRow());
		}

		// Set MessageTypes to IDocTypeMetaData
		List<MessageType> messageTypes = new ArrayList<MessageType>();
		if ((messageTable != null) && (!messageTable.isEmpty())) {
			do {

				MessageType message = new MessageType(messageTable.getString("MESTYP"), messageTable.getString("DESCRP"), messageTable.getString("IDOCTYP")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				messageTypes.add(message);

			} while (messageTable.nextRow());
		}

		IDocTypeMetaData metaData = new IDocTypeMetaData(segments, messageTypes);

		return metaData;
	}

	protected void addSegmentFields(Segment segment, JCoTable fieldsTable) {

		if (segment != null) {
			if ((fieldsTable != null) && (!fieldsTable.isEmpty())) {

				fieldsTable.firstRow();

				do {
					String segmentTyp = fieldsTable.getString("SEGMENTTYP").trim(); //$NON-NLS-1$
					if (segmentTyp.equals(segment.getType())) {
						IDocField field = createFieldFromEDI_IAPIStructure(segment, fieldsTable);
						segment.addField(field);
					}

				} while (fieldsTable.nextRow());
			}
		} else {
			Activator.getLogger().fine("The segment is null!"); //$NON-NLS-1$
		}
	}

	/**
	 * This method extracts an IDoc field from a JCoTable of type EDI_IAPI12 or
	 * EDI_IAPI16. It is re-used to create fields form regular segments as well
	 * as form the control record.
	 * 
	 * @param segment
	 * @param fieldsTable
	 * @return
	 */
	private IDocField createFieldFromEDI_IAPIStructure(Segment segment, JCoTable fieldsTable) {
		String fieldName = fieldsTable.getString("FIELDNAME").trim(); //$NON-NLS-1$
		String fieldDescription = fieldsTable.getString("DESCRP").trim(); //$NON-NLS-1$
		String fieldLabel = fieldsTable.getString("DESCRP").trim(); //$NON-NLS-1$

		String dataElementName = fieldsTable.getString("ROLLNAME").trim(); //$NON-NLS-1$
		String domainName = fieldsTable.getString("DOMNAME").trim(); //$NON-NLS-1$
		String dataTypeName = fieldsTable.getString("DATATYPE").trim(); //$NON-NLS-1$

		int intLength = fieldsTable.getInt("INTLEN"); //$NON-NLS-1$
		int length = fieldsTable.getInt("EXTLEN"); //$NON-NLS-1$

		// TODO: Read the real number of decimals and the SAP
		// base type from a DD0XYZ
		// table. As we currently map everything to varchar
		// (except tims and dats which have no decimals
		// anyway))this is not an issue.
		int decimals = 0;
		char sapBaseDataType = 'C';

		String checkTable = fieldsTable.getString("VALUETAB").trim(); //$NON-NLS-1$
		IDocField field = new IDocField(fieldName, fieldDescription, fieldLabel, dataElementName, domainName, dataTypeName, sapBaseDataType, length, intLength, decimals, false, false, segment);

		field.setNullable(true);

		if (!checkTable.isEmpty()) {
			field.setCheckTable(checkTable);
		}
		return field;
	}

	private ControlRecord getControlRecordMetaData(IDocType idocType) throws JCoException {
		ControlRecord cr = new ControlRecord(idocType);
		JCoFunction function = destination.getRepository().getFunction("IDOC_RECORD_READ"); //$NON-NLS-1$
		function.execute(destination);
		JCoTable dcFields = function.getTableParameterList().getTable("DC_FIELDS"); //$NON-NLS-1$
		if (!dcFields.isEmpty()) {
			do {
				IDocField field = this.createFieldFromEDI_IAPIStructure(cr, dcFields);
				cr.addField(field);
			} while (dcFields.nextRow());
		}

		return cr;
	}

}
