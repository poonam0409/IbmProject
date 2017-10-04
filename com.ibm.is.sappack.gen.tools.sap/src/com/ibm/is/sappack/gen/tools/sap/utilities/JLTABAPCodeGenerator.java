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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ibm.is.sappack.gen.tools.sap.importer.helper.model.LdmAccessor;
import com.ibm.is.sappack.gen.tools.sap.importer.idoctypes.IDocType;

public class JLTABAPCodeGenerator {
	
	static String copyright() {
		return com.ibm.is.sappack.gen.tools.sap.utilities.Copyright.IBM_COPYRIGHT_SHORT;
	}

	private String checkTable;
	private String textTable;
	private String rfcProgName;
	private String cpicProgName;
	private String textTableLanguageColumnName;
	private boolean takeOnlyFirstTextTableTuple = false;
	private List<ABAPColumn> columns = new ArrayList<ABAPColumn>();
	private List<ABAPColumn> joinColumns = new ArrayList<ABAPColumn>();

	private JLTABAPCodeGenerator(LdmAccessor ldmAccessor, IDocType idocType, String checkTable, String textTable) {
		super();
		this.checkTable = checkTable;
		this.textTable = textTable;
		rfcProgName = createRFCProgramName(ldmAccessor, idocType, checkTable);
		cpicProgName = createCPICProgramName(ldmAccessor, idocType, checkTable);
	}

	public String getRFCProgramName() {
		return rfcProgName;
	}

	public String getCPICProgramName() {
		return cpicProgName;
	}

	public void setTakeOnlyFirstTextTableTuple(boolean takeOnlyFirstTextTableTuple) {
		this.takeOnlyFirstTextTableTuple = takeOnlyFirstTextTableTuple;
	}

	public String getTextTableLanguageColumnName() {
		return textTableLanguageColumnName;
	}

	public void setTextTableLanguageColumnName(String textTableLanguageColumnName) {
		this.textTableLanguageColumnName = textTableLanguageColumnName;
	}

	public boolean isTakeOnlyFirstTextTableTuple() {
		return takeOnlyFirstTextTableTuple;
	}

	public List<ABAPColumn> getColumns() {
		return columns;
	}

	public List<ABAPColumn> getJoinColumns() {
		return joinColumns;
	}

	private String generateCodeNestedLoop(int transferMethod) {
		String template = null;
		String progName = null;
		if ((transferMethod & com.ibm.is.sappack.gen.common.Constants.ABAP_TRANSFERMETHOD_RFC) > 0) {
			template = "ABAPCodeTemplateNestedLoop.txt"; //$NON-NLS-1$
			progName = this.rfcProgName;

		} else if ((transferMethod & com.ibm.is.sappack.gen.common.Constants.ABAP_TRANSFERMETHOD_CPIC) > 0) {
			template = "ABAPCodeTemplateNestedLoopCPIC.txt"; //$NON-NLS-1$
			progName = this.cpicProgName;
		}

		String abapCode = readABAPTemplate(template);
		StringBuffer st1 = new StringBuffer();
		StringBuffer st2 = new StringBuffer();
		StringBuffer iteratorSetCode = new StringBuffer();
		StringBuffer selectClauseCheckTable = new StringBuffer();
		StringBuffer selectClauseTextTable = new StringBuffer();
		StringBuffer nestedLoopTempTableSetClause = new StringBuffer();
		StringBuffer joinCondition = new StringBuffer();
		int rowSize = 0;
		int checkTableColumnCount = 0;
		int textTableColumnCount = 0;

		for (int i = 0; i < columns.size(); i++) {
			ABAPColumn col = columns.get(i);
			int columnLength = col.getField().getDataType().getABAPDisplayLength();
			int j = i + 1;
			String abapColName = col.getTableName() + "-" + col.getField().getOriginalSAPFieldName(); //$NON-NLS-1$
			String selectColName = col.getTableName() + "~" + col.getField().getOriginalSAPFieldName(); //$NON-NLS-1$
			if (j > 1) {
				iteratorSetCode.append(JoinedCheckAndTextTableImporter.NL);
				st1.append(JoinedCheckAndTextTableImporter.NL);
				st2.append(JoinedCheckAndTextTableImporter.NL);
			}
			st1.append("    F_" + j + "(" + columnLength + ") TYPE C, \" for " + abapColName + JoinedCheckAndTextTableImporter.NL); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ 
			st2.append("    " + col.getField().getOriginalSAPFieldName() + " LIKE " + abapColName + ", \" for " + abapColName + JoinedCheckAndTextTableImporter.NL); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			iteratorSetCode.append("  IT_1-F_" + j + " = LINE3-" + col.getField().getOriginalSAPFieldName() + ". " + JoinedCheckAndTextTableImporter.NL); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
			// select clause only selects check table fields
			if (col.getTableName().equals(checkTable)) {
				if (checkTableColumnCount > 0) {
					selectClauseCheckTable.append(JoinedCheckAndTextTableImporter.NL);
					nestedLoopTempTableSetClause.append(JoinedCheckAndTextTableImporter.NL);
				}
				nestedLoopTempTableSetClause.append("   LINE2-" + col.getField().getOriginalSAPFieldName() + " = LINE1-" + col.getField().getOriginalSAPFieldName() + "."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				selectClauseCheckTable.append(selectColName);
				checkTableColumnCount++;
			} else if (col.getTableName().equals(textTable)) {
				if (textTableColumnCount > 0) {
					selectClauseTextTable.append(JoinedCheckAndTextTableImporter.NL);
				}
				selectClauseTextTable.append(selectColName);
				textTableColumnCount++;
			}

			rowSize += columnLength;
		}

		// join condition for select clause in nested loop
		for (int i = 0; i < joinColumns.size(); i++) {
			ABAPColumn joinCol = joinColumns.get(i);
			String foreignKeyColumn = joinCol.getForeignKeyColumnName();
			if (foreignKeyColumn == null) {
				foreignKeyColumn = joinCol.getField().getOriginalSAPFieldName();
			}
			joinCondition.append(textTable + "~" + foreignKeyColumn + " = LINE1-" + joinCol.getField().getOriginalSAPFieldName()); //$NON-NLS-1$ //$NON-NLS-2$
			joinCondition.append(" AND" + JoinedCheckAndTextTableImporter.NL); //$NON-NLS-1$
		}
		// language clause
		joinCondition.append(textTable + "~" + textTableLanguageColumnName + " = 'E'"); //$NON-NLS-1$ //$NON-NLS-2$

		String time = (new Date()).toString();
		abapCode = abapCode.replaceAll("#SAPPACK_PROGNAME#", progName); //$NON-NLS-1$
		abapCode = abapCode.replaceAll("#SAPPACK_TIME#", time); //$NON-NLS-1$
		abapCode = abapCode.replaceAll("#SAPPACK_ST_1DECLARATIONS#", st1.toString()); //$NON-NLS-1$
		abapCode = abapCode.replaceAll("#SAPPACK_ST_2DECLARATIONS#", st2.toString()); //$NON-NLS-1$
		abapCode = abapCode.replaceAll("#SAPPACK_CHECKTABLE#", checkTable); //$NON-NLS-1$

		abapCode = abapCode.replaceAll("#SAPPACK_CHECKTABLE_COLUMNS#", selectClauseCheckTable.toString()); //$NON-NLS-1$
		abapCode = abapCode.replaceAll("#SAPPACK_ITERATOR_SETCLAUSE#", iteratorSetCode.toString()); //$NON-NLS-1$
		abapCode = abapCode.replaceAll("#SAPPACK_OFFSET#", Integer.toString(rowSize)); //$NON-NLS-1$

		if (textTable != null) {
			abapCode = abapCode.replaceAll("#SAPPACK_COND_TEXTTABLE_EXISTS_COMMENT#", ""); //$NON-NLS-1$ //$NON-NLS-2$
			abapCode = abapCode.replaceAll("#SAPPACK_COND_TEXTTABLE_DOESNOT_EXIST_COMMENT#", "*"); //$NON-NLS-1$ //$NON-NLS-2$
			abapCode = abapCode.replaceAll("#SAPPACK_CHECKTABLE_DECL#", checkTable + ","); //$NON-NLS-1$ //$NON-NLS-2$
			abapCode = abapCode.replaceAll("#SAPPACK_TEXTTABLE#", textTable); //$NON-NLS-1$
			abapCode = abapCode.replaceAll("#SAPPACK_JOINCONDITION#", joinCondition.toString()); //$NON-NLS-1$
			abapCode = abapCode.replaceAll("#SAPPACK_LOOP_TEMPTABLE_SET_CLAUSE#", nestedLoopTempTableSetClause.toString()); //$NON-NLS-1$
			abapCode = abapCode.replaceAll("#SAPPACK_TEXTTABLE_COLUMNS#", selectClauseTextTable.toString()); //$NON-NLS-1$
			if (this.takeOnlyFirstTextTableTuple) {
				abapCode = abapCode.replaceAll("#SAPPACK_USE_ONLY_FIRST_TEXTTABLE_ENTRY_COMMENT#", ""); //$NON-NLS-1$ //$NON-NLS-2$
			} else {
				abapCode = abapCode.replaceAll("#SAPPACK_USE_ONLY_FIRST_TEXTTABLE_ENTRY_COMMENT#", "*"); //$NON-NLS-1$ //$NON-NLS-2$
			}
		} else {
			abapCode = abapCode.replaceAll("#SAPPACK_COND_TEXTTABLE_EXISTS_COMMENT#", "*"); //$NON-NLS-1$ //$NON-NLS-2$
			abapCode = abapCode.replaceAll("#SAPPACK_COND_TEXTTABLE_DOESNOT_EXIST_COMMENT#", ""); //$NON-NLS-1$ //$NON-NLS-2$
			abapCode = abapCode.replaceAll("#SAPPACK_CHECKTABLE_DECL#", checkTable); //$NON-NLS-1$
			abapCode = abapCode.replaceAll("#SAPPACK_TEXTTABLE#", ""); //$NON-NLS-1$//$NON-NLS-2$
			abapCode = abapCode.replaceAll("#SAPPACK_JOINCONDITION#", ""); //$NON-NLS-1$ //$NON-NLS-2$
			abapCode = abapCode.replaceAll("#SAPPACK_LOOP_TEMPTABLE_SET_CLAUSE#", ""); //$NON-NLS-1$ //$NON-NLS-2$
			abapCode = abapCode.replaceAll("#SAPPACK_TEXTTABLE_COLUMNS#", ""); //$NON-NLS-1$//$NON-NLS-2$
		}
		return abapCode;
	}

	public Map<String, String> generateCode(int transferMethod) {

		Map<String, String> result = new HashMap<String, String>();
		if ((transferMethod & com.ibm.is.sappack.gen.common.Constants.ABAP_TRANSFERMETHOD_RFC) > 0) {
			String abapCode = generateCodeNestedLoop(com.ibm.is.sappack.gen.common.Constants.ABAP_TRANSFERMETHOD_RFC);
			result.put(com.ibm.is.sappack.gen.common.Constants.ANNOT_ABAP_CODE, abapCode);
		}
		if ((transferMethod & com.ibm.is.sappack.gen.common.Constants.ABAP_TRANSFERMETHOD_CPIC) > 0) {
			String abapCode = generateCodeNestedLoop(com.ibm.is.sappack.gen.common.Constants.ABAP_TRANSFERMETHOD_CPIC);
			result.put(com.ibm.is.sappack.gen.common.Constants.ANNOT_ABAP_CODE_CPIC, abapCode);
		}

		// get columns
		StringBuffer ctColumns = new StringBuffer();
		StringBuffer ttColumns = new StringBuffer();
		for (int i = 0; i < this.columns.size(); i++) {
			ABAPColumn col = this.columns.get(i);
			String ctFieldName = col.getField().getOriginalSAPFieldName();
			StringBuffer buf = ctColumns;
			if (col.getTableName().equals(this.textTable)) {
				buf = ttColumns;
			}
			buf.append(ctFieldName);
			buf.append(","); //$NON-NLS-1$
		}
		result.put(com.ibm.is.sappack.gen.common.Constants.ANNOT_CHECKTABLE_COLUMNS, ctColumns.toString());
		result.put(com.ibm.is.sappack.gen.common.Constants.ANNOT_TEXTTABLE_COLUMNS, ttColumns.toString());

		// get join columns
		StringBuffer joinCols = new StringBuffer();
		for (ABAPColumn joinCol : this.joinColumns) {
			joinCols.append(joinCol.getField().getOriginalSAPFieldName() + "=" + joinCol.getForeignKeyColumnName() + ","); //$NON-NLS-1$ //$NON-NLS-2$
		}
		result.put(com.ibm.is.sappack.gen.common.Constants.ANNOT_CHECKTABLE_TEXTTABLE_JOINCONDITION, joinCols.toString());

		if (this.textTable != null) {
			result.put(com.ibm.is.sappack.gen.common.Constants.ANNOT_TEXTTABLE_LANGUAGE_COLUMN, this.textTableLanguageColumnName);
		}
		result.put(com.ibm.is.sappack.gen.common.Constants.ANNOT_TEXTTABLE_TAKE_ONLY_FIRST_TUPLE, Boolean.toString(this.takeOnlyFirstTextTableTuple));

		return result;
	}

	private static String readABAPTemplate(String abapCodeTemplate) {
		try {
			InputStream is = JoinedCheckAndTextTableImporter.class.getClassLoader().getResourceAsStream("com/ibm/is/sappack/gen/tools/sap/utilities/" + abapCodeTemplate); //$NON-NLS-1$
			byte[] result = new byte[0];
			final int SIZE = 1024;
			byte[] tmp = new byte[SIZE];
			int i;
			while ((i = is.read(tmp)) != -1) {
				byte[] old = result;
				result = new byte[old.length + i];
				System.arraycopy(old, 0, result, 0, old.length);
				System.arraycopy(tmp, 0, result, old.length, i);
			}
			return new String(result, "ASCII"); //$NON-NLS-1$
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static String createRFCProgramName(LdmAccessor ldmAccessor, IDocType idocType, String checkTable) {
		StringBuffer programName = new StringBuffer("Z_SP_"); //$NON-NLS-1$
		String ldmFileName = ldmAccessor.getModelFile().getName();
		ldmFileName = ldmFileName.substring(0, ldmFileName.length() - 4);
		int length = 4;
		if (ldmFileName.length() < 4) {
			length = ldmFileName.length();
		}
		programName.append(ldmFileName.substring(0, length));
		programName.append("_"); //$NON-NLS-1$

		String idocTypeName = ""; //$NON-NLS-1$
		if (idocType != null) {
			idocTypeName = idocType.getName();
			length = 4;
			if (idocTypeName.length() < 4) {
				length = idocTypeName.length();
			}
			programName.append(idocTypeName.substring(0, length));
			programName.append("_"); //$NON-NLS-1$
		}

		length = 4;
		if (checkTable.length() < 4) {
			length = checkTable.length();
		}
		programName.append(checkTable.substring(0, length));
		programName.append("_"); //$NON-NLS-1$
		int hashCode = (ldmAccessor.getModelFile().getFullPath().toOSString() + idocTypeName + checkTable).hashCode();
		programName.append(hashCode & Integer.MAX_VALUE);
		String result = cleanProgramName(programName.toString().trim().toUpperCase());
		return result;
	}

	private static String cleanProgramName(String programName) {
		if (programName == null || programName.trim().length() == 0) {
			return programName;
		}
		String cleanedName = programName.replace('/', '_');
		return cleanedName;
	}

	private static String createCPICProgramName(LdmAccessor ldmAccessor, IDocType idocTypeName, String checkTable) {
		StringBuffer programName = new StringBuffer("Z"); //$NON-NLS-1$
		int hashCode = (ldmAccessor.getModelFile().getFullPath().toOSString() + idocTypeName + checkTable).hashCode();
		if (hashCode < 0) {
			programName.append("0"); //$NON-NLS-1$
		}
		programName.append(Integer.toString(Math.abs(hashCode), Character.MAX_RADIX));
		return programName.toString().toUpperCase();
	}

}