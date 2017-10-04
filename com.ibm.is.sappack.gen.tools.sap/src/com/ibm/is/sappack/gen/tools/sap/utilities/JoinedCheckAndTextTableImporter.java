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
// Module Name : com.ibm.is.sappack.gen.tools.sap.utilities
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.tools.sap.utilities;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.eclipse.core.runtime.IProgressMonitor;

import com.ibm.db.models.logical.Entity;
import com.ibm.db.models.logical.Package;
import com.ibm.iis.sappack.gen.common.ui.connections.SapSystem;
import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.tools.sap.activator.Activator;
import com.ibm.is.sappack.gen.tools.sap.constants.Constants;
import com.ibm.is.sappack.gen.tools.sap.importer.helper.model.LdmAccessor;
import com.ibm.is.sappack.gen.tools.sap.importer.idoctypes.IDocType;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoTable;

public class JoinedCheckAndTextTableImporter extends TableImporterBase {

	public static final String NL = "\n"; //$NON-NLS-1$

	private int transferMethods = com.ibm.is.sappack.gen.common.Constants.ABAP_TRANSFERMETHOD_RFC;

	static String copyright() {
		return com.ibm.is.sappack.gen.tools.sap.utilities.Copyright.IBM_COPYRIGHT_SHORT;
	}

	public enum JLT_ABAP_METHOD {
		LEFT_OUTER_JOIN, NESTED_LOOP, CHECKTABLE_ONLY
	};

	protected JLT_ABAP_METHOD abapMethod = JLT_ABAP_METHOD.NESTED_LOOP;

	private JoinedCheckAndTextTableImporter(SapSystem sapSystem, LdmAccessor ldmAccessor, Package ldmPackage, int transferMethod) {
		super(sapSystem, ldmAccessor, ldmPackage);
		this.transferMethods = transferMethod;
	}

	/*
	 * The algorithm works as follows: For each check table c: 
	 *   - query matching text table t from SAP 
	 *   - query all sets of join fields for c, t from SAP 
	 *       - if there is more than one set -> error (special case: T002 there are two
	 *                                                 different set of join fields, the false one SPRAS is ignored (hard coded)) 
	 *       - else 
	 *           - create a new joined lookup table jtt_c 
	 *           - add all join columns from c to jtt_c 
	 *           - add all non join columns and non key fields of t to jtt_c
	 */
	private void importJoinedCheckAndTextTableTables(List<String> checkTables, IDocType idocType, IProgressMonitor progressMonitor) throws JCoException {
		trace("checkTables: " + checkTables.size() + "(" + checkTables + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		// create a map from check to text table names
		Map<String, String> check2TextTable = getCheck2TextTableMap(checkTables);

		int jttNum = 0;
		// for each check table
		for (String checkTable : checkTables) {
			if (progressMonitor.isCanceled()) {
				return;
			}
			String pmMsg = Messages.JoinedCheckAndTextTableImporter_0;
			pmMsg = MessageFormat.format(pmMsg, checkTable);
			progressMonitor.subTask(pmMsg);
			String textTable = check2TextTable.get(checkTable);

			// special case for T002 
			if (checkTable.equalsIgnoreCase("t002")) { //$NON-NLS-1$
				textTable = "T002T"; //$NON-NLS-1$
			}

			String joinedColumnTableName = MessageFormat.format(Constants.CW_JLT_NAME_TEMPLATE, new Object[] { cleanFieldName(checkTable) });

			JLTABAPCodeGenerator abapCodeGenerator = null; // new JLTABAPCodeGenerator(ldmAccessor, idocType, checkTable, textTable);

			// the lists joinColumnsCheckTable and joinColumnsTextTable
			// must be of the same length:
			// joinColumnsCheckTable[i] of the check table must be joined with joinColumnsTextTable[i] of the text table
			List<String> joinColumnsCheckTable = new ArrayList<String>();
			List<String> joinColumnsTextTable = new ArrayList<String>();
			boolean joinColumnsOK = true;

			if (textTable != null) {
				joinColumnsOK = computeJoinColumns(checkTable, textTable, joinColumnsCheckTable, joinColumnsTextTable);
			} 

			// if there was more than one join condition, skip this check table
			if (!joinColumnsOK) {
				continue;
			}

			trace("joinColumns of check table: " + joinColumnsCheckTable); //$NON-NLS-1$
			trace("joinColumns of text table: " + joinColumnsTextTable); //$NON-NLS-1$

			JCoTable checkTableFields = null;
			checkTableFields = queryTableMetadata(checkTable);
			if (checkTableFields == null) {
				continue;
			}

			Entity jtt = null;
			if (ldmAccessor.findEntity(targetPackage, joinedColumnTableName) == null) {
				jtt = createJoinedLookupTable(joinedColumnTableName, jttNum, checkTable, textTable, idocType, abapCodeGenerator);
			}

			if (jtt != null) {
				if (!checkTableFields.isEmpty()) {
					addCheckTableColumns(checkTable, checkTableFields, jtt, joinColumnsCheckTable, joinColumnsTextTable, abapCodeGenerator);

					// add the fields for each texttable
					if (textTable != null) {
						addTextTableColumns(textTable, jtt, joinColumnsTextTable, abapCodeGenerator);
					}
				}

				addAbapProgram(ldmAccessor, jtt, abapCodeGenerator);
			}
			jttNum++;
			progressMonitor.worked(1);
		}

	}

	private void addTextTableColumns(String textTable, Entity jtt, List<String> joinColumnsTextTable, JLTABAPCodeGenerator abapCode) throws JCoException {
		JCoTable textTableFields = queryTableMetadata(textTable);
		trace("Text table JCO fields: " + textTableFields); //$NON-NLS-1$
		if (!textTableFields.isEmpty()) {
			do {
				String column = textTableFields.getString(Constants.JCO_PARAMETER_FIELDNAME);
				boolean isKeyColumn = textTableFields.getString(Constants.JCO_PARAMETER_KEYFLAG).equals(Constants.JCO_PARAMETER_VALUE_TRUE);
				String domainName = textTableFields.getString(Constants.JCO_PARAMETER_DOMNAME);
				boolean isLanguageColumn = "SPRAS".equals(domainName); //$NON-NLS-1$
				trace("Processing text table column " + column); //$NON-NLS-1$

				// add only non-key columns which are not join columns
				if (!joinColumnsTextTable.contains(column)) {
					trace("Adding text table column " + column); //$NON-NLS-1$
					boolean addColumn = !isKeyColumn;
					if (isKeyColumn && !isLanguageColumn) {
						addColumn = false;
						// osuhre, 50658, 95118:
						// if there is a key column in the text table
						// which is neither a join column nor the language column
						// do not add it to the schema and mark the ABAP code that only the first record
						// should be extracted
						abapCode.setTakeOnlyFirstTextTableTuple(true);
					}
					if (addColumn) {
						ABAPColumn col = addColumn(textTable, jtt, textTableFields, "", false, com.ibm.is.sappack.gen.common.Constants.DATA_OBJECT_SOURCE_TYPE_JOINED_CHECK_AND_TEXT_TABLE); //$NON-NLS-1$
						addColumnToCodeGenerator(col, false, abapCode);
					}
				}
				if (isLanguageColumn) {
					trace("Adding language column of text table " + column); //$NON-NLS-1$
					// special case: for T002T always take the SPRAS field
					if (textTable.equals("T002T")) { //$NON-NLS-1$
						trace("Special case for T002T: add field SPRAS"); //$NON-NLS-1$
						abapCode.setTextTableLanguageColumnName("SPRAS"); //$NON-NLS-1$
					} else {
						abapCode.setTextTableLanguageColumnName(column);
					}
				}
			} while (textTableFields.nextRow());
		}
	}

	private Entity createJoinedLookupTable(String joinedLookupTableName, int jttNum, String checkTable, String textTable, IDocType idocType, JLTABAPCodeGenerator abapCode) {
		Entity jtt;
		String fullCheckTableName = MessageFormat.format(Constants.CW_JLT_NAME_TEMPLATE, new Object[] { checkTable });
		String jltDesc = Messages.JoinedCheckAndTextTableImporter_2;
		jltDesc = MessageFormat.format(jltDesc, textTable, checkTable);
		jtt = ldmAccessor.createEntity(targetPackage, joinedLookupTableName, jltDesc);
		ldmAccessor.addAnnotation(jtt, com.ibm.is.sappack.gen.common.Constants.ANNOT_CHECK_TABLE_NAME, checkTable);
		ldmAccessor.addAnnotation(jtt, com.ibm.is.sappack.gen.common.Constants.ANNOT_LOGICAL_TBL_NAME, fullCheckTableName);
		ldmAccessor.addAnnotation(jtt, com.ibm.is.sappack.gen.common.Constants.ANNOT_PHYSICAL_TBL_NAME, fullCheckTableName);
		if (textTable != null) {
			ldmAccessor.addAnnotation(jtt, com.ibm.is.sappack.gen.common.Constants.ANNOT_TEXT_TABLE_NAME, textTable);
		}
		ldmAccessor.addAnnotation(jtt, com.ibm.is.sappack.gen.common.Constants.ANNOT_DATA_OBJECT_SOURCE, com.ibm.is.sappack.gen.common.Constants.DATA_OBJECT_SOURCE_TYPE_JOINED_CHECK_AND_TEXT_TABLE);
		if (idocType != null) {
			boolean isExtendedType = idocType.isExtendedIDocType();

			ldmAccessor.addAnnotation(jtt, com.ibm.is.sappack.gen.common.Constants.ANNOT_IDOC_TYPE, idocType.getName());
			ldmAccessor.addAnnotation(jtt, com.ibm.is.sappack.gen.common.Constants.ANNOT_IS_EXTENDED_IDOC_TYPE, String.valueOf(isExtendedType));
			if (isExtendedType) {
				ldmAccessor.addAnnotation(jtt, com.ibm.is.sappack.gen.common.Constants.ANNOT_IDOC_BASIC_TYPE, idocType.getBasicType());
			}
		}
		if ((this.transferMethods & com.ibm.is.sappack.gen.common.Constants.ABAP_TRANSFERMETHOD_RFC) > 0) {
			ldmAccessor.addAnnotation(jtt, com.ibm.is.sappack.gen.common.Constants.ANNOT_ABAP_PROGRAM_NAME, abapCode.getRFCProgramName());
		}
		if ((this.transferMethods & com.ibm.is.sappack.gen.common.Constants.ABAP_TRANSFERMETHOD_CPIC) > 0) {
			ldmAccessor.addAnnotation(jtt, com.ibm.is.sappack.gen.common.Constants.ANNOT_ABAP_PROGRAM_NAME_CPIC, abapCode.getCPICProgramName());
		}
		ldmAccessor.addAnnotation(jtt, com.ibm.is.sappack.gen.common.Constants.ANNOT_GENERATED_MODEL_VERSION, com.ibm.is.sappack.gen.common.Constants.MODEL_VERSION);

		trace("Creating JLT table " + jttNum + "(" + joinedLookupTableName + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		return jtt;
	}

	private boolean computeJoinColumns(String checkTable, String textTable, List<String> outJoinColumnsCheckTable, List<String> outJoinColumnsTextTable) throws JCoException {
		boolean joinColumnsOK = true;

		SAPTableExtractor joinCondExtractor = new SAPTableExtractor(this.destination, "DD05Q", //$NON-NLS-1$ 
				Arrays.asList(new String[] { "FORKEY", "CHECKFIELD", "FIELDNAME" }), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ 
				"CHECKTABLE = '" + checkTable + //$NON-NLS-1$ 
						"' AND FORTABLE = '" + textTable + "'"); //$NON-NLS-1$ //$NON-NLS-2$
		SAPTableExtractor.Result joinCondResult = joinCondExtractor.performQuery();
		String joinCondFieldName = null;

		// add the join conditions to joinColumnsCheckTable and joinColumnsTextTable
		// these two lists must be of the same length because it means that
		// joinColumnsCheckTable[i] must be joined with joinColumnsTextTable[i]
		while (joinCondResult.nextRow()) {
			String textTableColumn = joinCondResult.getValue("FORKEY"); //$NON-NLS-1$
			String checkTableColumn = joinCondResult.getValue("CHECKFIELD"); //$NON-NLS-1$
			String fieldName = joinCondResult.getValue("FIELDNAME"); //$NON-NLS-1$

			// special case: For T002 the join columns for SPRAS must be
			// ignored
			if (checkTable.equals("T002")) { //$NON-NLS-1$
				if (fieldName.equals("SPRAS")) { //$NON-NLS-1$
					continue;
				}
			}

			// if this is the first pass through the DD05Q result
			if (joinCondFieldName == null) {
				joinCondFieldName = fieldName;
			} else {
				// if there is a second join condition

				// check if the field name belongs to the current foreign key (join condition field name)
				if (!joinCondFieldName.equals(fieldName)) {
					// NO ==> then check if the field name exist in the join column list
					if (outJoinColumnsTextTable.indexOf(fieldName) > -1) {
						// yes it exists ==> use this name as new foreign key name
						joinCondFieldName = fieldName;
					} else {
						// no, doesn't exist ==> more than one foreign key
						String msg = Messages.JoinedCheckAndTextTableImporter_1;
						msg = MessageFormat.format(msg, textTable);
						Activator.getLogger().warning(msg);
						System.out.println(msg);
						joinColumnsOK = false;
						break;
					}
				} // end of if (!joinCondFieldName.equals(fieldName))
			} // end of (else) if (joinCondFieldName == null)

			// Defect 117541 (hschoen): add columns if they don't exist yet
			// ------------------------------------------------------------
			// TODO OLIX ensure that joinColumnsCheckTable and joinColumnsTextTable are of same length
			if (outJoinColumnsCheckTable.indexOf(checkTableColumn) < 0) {
				outJoinColumnsCheckTable.add(checkTableColumn);
			}
			if (outJoinColumnsTextTable.indexOf(textTableColumn) < 0) {
				outJoinColumnsTextTable.add(textTableColumn);
			}
		}
		return joinColumnsOK;
	}

	private void addCheckTableColumns(String checkTable, JCoTable checkTableFields, Entity jtt, List<String> joinColumnsCheckTable, List<String> joinColumnsTextTable, JLTABAPCodeGenerator abapCode) {
		do {
			String checkTableCol = checkTableFields.getString("FIELDNAME"); //$NON-NLS-1$
			boolean isKeyColumn = checkTableFields.getString(Constants.JCO_PARAMETER_KEYFLAG).equals(Constants.JCO_PARAMETER_VALUE_TRUE);
			int ix = joinColumnsCheckTable.indexOf(checkTableCol);
			if (ix > -1 && !isKeyColumn) {
				trace("Found check table column " + checkTableCol + " as join column"); //$NON-NLS-1$ //$NON-NLS-2$
				ABAPColumn joinColumn = addColumn(checkTable, jtt, checkTableFields, "", false, com.ibm.is.sappack.gen.common.Constants.DATA_OBJECT_SOURCE_TYPE_JOINED_CHECK_AND_TEXT_TABLE); //$NON-NLS-1$
				addColumnToCodeGenerator(joinColumn, true, abapCode);
				String foreignKeyName = joinColumnsTextTable.get(ix);
				joinColumn.setForeignKeyColumnName(foreignKeyName);
			} else if (isKeyColumn) {
				boolean isJoinColumn = ix > -1;
				if (isJoinColumn) {
					ABAPColumn joinColumn = addColumn(checkTable, jtt, checkTableFields, "", false, com.ibm.is.sappack.gen.common.Constants.DATA_OBJECT_SOURCE_TYPE_JOINED_CHECK_AND_TEXT_TABLE); //$NON-NLS-1$
					addColumnToCodeGenerator(joinColumn, true, abapCode);
					String foreignKeyName = null;
					foreignKeyName = joinColumnsTextTable.get(ix);
					joinColumn.setForeignKeyColumnName(foreignKeyName);
					trace("Check table column " + checkTableCol + " is key column"); //$NON-NLS-1$//$NON-NLS-2$
				} else {
					// osuhre, 48609:
					// If textTable != null this case might happen if there are key fields in the check table
					// which don't exist in the text table.
					// Compare also with work item 47122.
					ABAPColumn nonJoinColumn = addColumn(checkTable, jtt, checkTableFields, "", false, com.ibm.is.sappack.gen.common.Constants.DATA_OBJECT_SOURCE_TYPE_JOINED_CHECK_AND_TEXT_TABLE); //$NON-NLS-1$
					addColumnToCodeGenerator(nonJoinColumn, false, abapCode);
					trace("Check table column " + checkTableCol + " is key column but does not exist in text table"); //$NON-NLS-1$//$NON-NLS-2$
				}
			} else {
				trace("Check table column " + checkTableCol + " is not a join or key column"); //$NON-NLS-1$ //$NON-NLS-2$
			}
		} while (checkTableFields.nextRow());
	}
	
	private void addColumnToCodeGenerator(ABAPColumn col, boolean isJoinColumn, JLTABAPCodeGenerator abapCodeGenerator) {
		abapCodeGenerator.getColumns().add(col);
		if (isJoinColumn) {
			abapCodeGenerator.getJoinColumns().add(col);
		}
	}

	private Map<String, String> getCheck2TextTableMap(List<String> checkTables) throws JCoException {
		Map<String, String> result = new HashMap<String, String>();
		StringBuffer whereClause = new StringBuffer();
		whereClause.append("AS4LOCAL = 'A' AND FRKART = 'TEXT' AND CHECKTABLE IN ("); //$NON-NLS-1$
		boolean first = true;
		for (String checkTable : checkTables) {
			if (first) {
				first = false;
			} else {
				whereClause.append(", "); //$NON-NLS-1$
			}
			whereClause.append("'" + checkTable + "'"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		whereClause.append(")"); //$NON-NLS-1$

		String[] columns = new String[] { "CHECKTABLE", "TABNAME" }; //$NON-NLS-1$ //$NON-NLS-2$

		// osuhre: text tables are ignored if only check tables are used for
		// JLTs
		if (abapMethod != JLT_ABAP_METHOD.CHECKTABLE_ONLY) {
			SAPTableExtractor extractor = new SAPTableExtractor(this.destination, "DD08L", Arrays.asList(columns), whereClause.toString()); //$NON-NLS-1$
			extractor.setSkipLengthCheck(true);
			SAPTableExtractor.Result dd08lResult = extractor.performQuery();
			while (dd08lResult.nextRow()) {
				String textTable = dd08lResult.getValue("TABNAME"); //$NON-NLS-1$
				String checkTable = dd08lResult.getValue("CHECKTABLE"); //$NON-NLS-1$
				result.put(checkTable, textTable);
			}
		}
		return result;
	}
	
	/**
	 * Discovers the text table for the given check
	 * 
	 * @param idocType
	 * 
	 * @param progressMonitor
	 * @throws JCoException
	 */
	public void importJoinedCheckAndTextTable(List<String> checkTables, IDocType idocType, IProgressMonitor progressMonitor) {
		try {
		//	importJoinedCheckAndTextTableTables(checkTables, idocType, progressMonitor);
			ReferenceTableImporter imp = new ReferenceTableImporter(sapSystem, ldmAccessor, targetPackage, transferMethods);
		//	imp.importReducedCheckAndTextTableTables(checkTables, idocType, progressMonitor);
		} catch (Exception e) {
			e.printStackTrace();
			Activator.getLogger().log(Level.SEVERE, e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	public void importJoinedCheckAndTextTable(List<String> checkTables, IProgressMonitor progressMonitor) {
		this.importJoinedCheckAndTextTable(checkTables, null, progressMonitor);
	}

	/**
	 * Adds the ABAP program as an annotation to the entity
	 */
	private void addAbapProgram(LdmAccessor ldmAccessor, Entity entity, JLTABAPCodeGenerator abapCode) {
		Map<String, String> annotations = abapCode.generateCode(this.transferMethods);
		for (Map.Entry<String, String> annot : annotations.entrySet()) {
			ldmAccessor.addAnnotation(entity, annot.getKey(), annot.getValue());
		}

	}

}