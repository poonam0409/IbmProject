//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2011, 2014                                              
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
import java.util.List;

import com.ibm.is.sappack.gen.common.Constants;
import com.ibm.is.sappack.gen.common.ui.ModeOptionsBase;
import com.ibm.is.sappack.gen.tools.sap.importer.helper.model.LdmAccessor;


/**
 * Class for generating the ABAP code for extracting a single table
 */
public class ABAPExtractCodeGenerator {

	LdmAccessor ldmAccessor;
//	String programName;
	String tableName;
	List<ABAPColumn> columns = new ArrayList<ABAPColumn>();

	static final String NL = "\n"; //$NON-NLS-1$
	
	public static final CodePageCheck _CodePageCheck = new CodePageCheck();
	
	static String copyright()
	{ return com.ibm.is.sappack.gen.tools.sap.utilities.Copyright.IBM_COPYRIGHT_SHORT; }

	
	static String readABAPTemplate(int transferMethod) {
		try {
			String templateName = null;
			if (transferMethod == Constants.ABAP_TRANSFERMETHOD_RFC) {
				switch(ModeOptionsBase.getDSServiceVersion()) {
				case v71 :
					templateName = "ABAPExtractCodeTemplate.txt"; //$NON-NLS-1$
					break;
				case v80 :
					templateName = "ABAPExtractCodeTemplate_8_0.txt"; //$NON-NLS-1$
					break;
				case v70 :
					templateName = "ABAPCodeTemplate.txt"; 
					break;
				}
				
			} else if (transferMethod == Constants.ABAP_TRANSFERMETHOD_CPIC) {
				templateName = "ABAPExtractCodeCPICTemplate.txt"; //$NON-NLS-1$
			}
			InputStream is = ABAPExtractCodeGenerator.class.getClassLoader().getResourceAsStream("com/ibm/is/sappack/gen/tools/sap/utilities/"+ templateName); //$NON-NLS-1$
			byte[] result = Utilities.readInputStream(is);
			return new String(result, "ASCII"); //$NON-NLS-1$
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public ABAPExtractCodeGenerator(String tableName, LdmAccessor ldmAccessor) {
		this.tableName = tableName;
		this.ldmAccessor = ldmAccessor;
	}

	public void addColumn(ABAPColumn column) {
		this.columns.add(column);
	}

	public String generateABAPCode(int transferMethod) {
		int rowSize = 0;
		int columnNumber = 1;
		StringBuffer st1Declarations = new StringBuffer();
		StringBuffer st2Declarations = new StringBuffer();
		StringBuffer selectClause = new StringBuffer();
		StringBuffer intoClause = new StringBuffer();
		StringBuffer itSetCommands = new StringBuffer();

		for (int i = 0; i < columns.size(); i++) {
			ABAPColumn col = columns.get(i);
			boolean isLast = (i == columns.size() - 1);
			boolean isFirst = (i == 0);
			
			int length = col.getField().getDataType().getABAPDisplayLength(); 				
			String columnName = col.getColumnName();

			st1Declarations.append("F_" + columnNumber + "(" + length + ") TYPE C," + NL); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			st2Declarations.append("F_" + columnNumber + " LIKE " + tableName + "-" + columnName + "," + NL); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			selectClause.append("   " + tableName + "~" + columnName + NL);  //$NON-NLS-1$//$NON-NLS-2$
			intoClause.append((isFirst ? "" : "   ") + "ST_2-F_" + columnNumber + (isLast ? "" : (", " + NL))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
			itSetCommands.append("  IT_1-F_" + columnNumber + " = ST_2-F_" + columnNumber + "." + NL);   //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$

			columnNumber++;
			rowSize += length;
		}

		String abapCode = readABAPTemplate(transferMethod);

		String programName = createProgramName(transferMethod);
		
		String time = (new Date()).toString();
		abapCode = abapCode.replaceAll("#SAPPACK_PROGNAME#", programName); //$NON-NLS-1$
		abapCode = abapCode.replaceAll("#SAPPACK_TIME#", time); //$NON-NLS-1$
		abapCode = abapCode.replaceAll("#SAPPACK_ST_1DECLARATIONS#", st1Declarations.toString()); //$NON-NLS-1$
		abapCode = abapCode.replaceAll("#SAPPACK_ST_2DECLARATIONS#", st2Declarations.toString()); //$NON-NLS-1$
		abapCode = abapCode.replaceAll("#SAPPACK_TABLE#", tableName); //$NON-NLS-1$
		abapCode = abapCode.replaceAll("#SAPPACK_SELECTCLAUSE#", selectClause.toString()); //$NON-NLS-1$
		abapCode = abapCode.replaceAll("#SAPPACK_INTOCLAUSE#", intoClause.toString()); //$NON-NLS-1$
		abapCode = abapCode.replaceAll("#SAPPACK_ITSETCLAUSE#", itSetCommands.toString()); //$NON-NLS-1$
		abapCode = abapCode.replaceAll("#SAPPACK_OFFSET#", Integer.toString(rowSize)); //$NON-NLS-1$

		return abapCode;
	}

	public String createProgramName(int transferMethod) {
		if (transferMethod == Constants.ABAP_TRANSFERMETHOD_RFC) {
			return ABAPExtractCodeGenerator.createRFCProgramName(ldmAccessor, tableName);
		} else if (transferMethod == Constants.ABAP_TRANSFERMETHOD_CPIC) {
			return ABAPExtractCodeGenerator.createCPICProgramName(ldmAccessor, tableName);
		}
		throw new UnsupportedOperationException();
	}


	public static String createCPICProgramName(LdmAccessor ldmAccessor, String table) {
		StringBuffer programName = new StringBuffer(com.ibm.is.sappack.gen.tools.sap.constants.Constants.DEFAULT_ABAP_CODE_PREFIX_CPIC); 
		int hashCode = (ldmAccessor.getModelFile().getFullPath().toOSString() + table).hashCode();
		if (hashCode < 0) {
			programName.append("0"); //$NON-NLS-1$
		}
		programName.append(Integer.toString(Math.abs(hashCode), Character.MAX_RADIX));
		return programName.toString().toUpperCase();
	}


	public static String createRFCProgramName(LdmAccessor ldmAccessor, String table) {
		StringBuffer programName = new StringBuffer(com.ibm.is.sappack.gen.tools.sap.constants.Constants.DEFAULT_ABAP_CODE_PREFIX_RFC);
		String ldmFileName = ldmAccessor.getModelFile().getName();
		int length = 2;
		if (ldmFileName.length() < 2) {
			length = ldmFileName.length();
		}
		ldmFileName = ldmFileName.substring(0, length);

		// add LDM name only if used code page is ASCII
		if (_CodePageCheck.isASCII(ldmFileName)) {
			programName.append(ldmFileName.replace(" ", "_")); //$NON-NLS-1$ //$NON-NLS-2$
			programName.append("_"); //$NON-NLS-1$
		}

		length = 6;
		if (table.length() < 6) {
			length = table.length();
		}
		programName.append(table.substring(0, length));
		programName.append("_"); //$NON-NLS-1$
		int hashCode = (ldmAccessor.getModelFile().getFullPath().toOSString() + table).hashCode();
		if (hashCode < 0) {
			programName.append("0"); //$NON-NLS-1$
		}
		programName.append(Math.abs(hashCode));

		return programName.toString().toUpperCase().replace('/', '_');
	}
	
}
