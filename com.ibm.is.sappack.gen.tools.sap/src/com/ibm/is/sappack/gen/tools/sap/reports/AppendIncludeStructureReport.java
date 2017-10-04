//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2009, 2012                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.is.sappack.gen.tools.sap.reports
//                                                                             
//*************************-END OF SPECIFICATIONS-**************************
package com.ibm.is.sappack.gen.tools.sap.reports;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

import com.ibm.db.models.logical.Attribute;
import com.ibm.db.models.logical.Entity;
import com.ibm.is.sappack.gen.common.Constants;
import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.tools.sap.importer.helper.model.LdmAccessor;

public class AppendIncludeStructureReport implements LDMReport {
	static String copyright() {
		return Copyright.IBM_COPYRIGHT_SHORT;
	}

	public static final String APPENDREGEX_OPTION = "APPENDREGEX_OPTION"; //$NON-NLS-1$
	public static final String INCLUDEREGEX_OPTION = "INCLUDEREGEX_OPTION"; //$NON-NLS-1$
	public static final String OPERATOR_OPTION = "OPERATOR_OPTION"; //$NON-NLS-1$

	public static final String OPERATOR_AND = "AND"; //$NON-NLS-1$
	public static final String OPERATOR_OR = "OR"; //$NON-NLS-1$

	static String COL_TABLENAME = "TABLENAME"; //$NON-NLS-1$
	static String COL_FIELDNAME = "FIELDNAME"; //$NON-NLS-1$
	static String COL_KEY = "KEY"; //$NON-NLS-1$
	static String COL_TYPE = "TYPE"; //$NON-NLS-1$
	static String COL_LENGTH = "LENGTH"; //$NON-NLS-1$
	static String COL_DATA_ELEM = "DATAELEMENT"; //$NON-NLS-1$
	static String COL_DOMAIN = "DOMAIN"; //$NON-NLS-1$
	static String COL_SHORT_TEXT = "SHORTTEXT"; //$NON-NLS-1$
	static String COL_INCLUDE_STRUCTURE = "INCLUDESTRUCTURE"; //$NON-NLS-1$
	static String COL_APPEND_STRUCTURE = "APPENDSTRUCTURE"; //$NON-NLS-1$

	Pattern appendRegExPattern;
	Pattern includeRegExPattern;

	boolean findMatch(Pattern p, String structureNames) {
		if (structureNames == null) {
			structureNames = ""; //$NON-NLS-1$
		}
		StringTokenizer tok = new StringTokenizer(structureNames, "" + com.ibm.is.sappack.gen.tools.sap.constants.Constants.INCLUDE_APPEND_STRUCTURE_SEPARATOR); //$NON-NLS-1$
		while (tok.hasMoreTokens()) {
			if (p.matcher(tok.nextToken()).matches()) {
				return true;
			}
		}
		return false;
		
	}
	
	boolean reportIncludeStructure(String includeStructureName) {
		return findMatch(this.includeRegExPattern, includeStructureName);
	}

	private boolean reportAppendStructure(String appendStructureName) {
		return findMatch(this.appendRegExPattern, appendStructureName);
	}


	
	@Override
	public void createReport(List<Entity> entities, File reportFile, Map<String, String> reportOptions, IProgressMonitor pm) throws IOException, CoreException {
		if (pm == null) {
			pm = new NullProgressMonitor();
		}

		String app = reportOptions.get(APPENDREGEX_OPTION);
		if (app.isEmpty()) {
			app = ".*"; //$NON-NLS-1$
		}
		this.appendRegExPattern = Pattern.compile(app);
		String incl = reportOptions.get(INCLUDEREGEX_OPTION);
		if (incl.isEmpty()) {
			incl = ".*"; //$NON-NLS-1$
		}
		this.includeRegExPattern = Pattern.compile(incl);

		List<String> columnNames = new ArrayList<String>();
		columnNames.add(COL_TABLENAME);
		columnNames.add(COL_FIELDNAME);
		columnNames.add(COL_KEY);
		columnNames.add(COL_TYPE);
		columnNames.add(COL_LENGTH);
		columnNames.add(COL_DATA_ELEM);
		columnNames.add(COL_DOMAIN);
		columnNames.add(COL_SHORT_TEXT);
		columnNames.add(COL_INCLUDE_STRUCTURE);
		columnNames.add(COL_APPEND_STRUCTURE);

		CSVFile csvFile = new CSVFile(columnNames, true, reportOptions);

		String op = reportOptions.get(OPERATOR_OPTION);

		pm.beginTask(Messages.AppendIncludeStructureReport_0, entities.size());
		for (Entity e : entities) {
			String msg = Messages.AppendIncludeStructureReport_1;
			msg = MessageFormat.format(msg, e.getName());
			pm.setTaskName(msg);
			List<?> attributes = e.getAttributes();
			for (Object attrO : attributes) {
				Attribute attr = (Attribute) attrO;
				String includeStructureName = LdmAccessor.getAnnotationValue(attr, Constants.ANNOT_INCLUDE_STRUCTURE);
				boolean reportInclude = reportIncludeStructure(includeStructureName);
				String appendStructureName = LdmAccessor.getAnnotationValue(attr, Constants.ANNOT_APPEND_STRUCTURE);
				boolean reportAppend = reportAppendStructure(appendStructureName);

				boolean addLine = false;
				if (op.equals(OPERATOR_AND)) {
					addLine = reportInclude && reportAppend;
				} else {
					addLine = reportInclude || reportAppend;
				}

				if (addLine) {
					addAttributeToCSV(csvFile, e, attr);
					csvFile.setInCurrentRow(COL_INCLUDE_STRUCTURE, includeStructureName);
					csvFile.setInCurrentRow(COL_APPEND_STRUCTURE, appendStructureName);
					csvFile.finishRow();
				}

			}
			if (pm.isCanceled()) {
				return;
			}
		}
		pm.worked(1);

		String csvFileContents = csvFile.getContents();
		FileOutputStream fos = new FileOutputStream(reportFile);
		fos.write(csvFileContents.getBytes("UTF-8")); //$NON-NLS-1$
		fos.close();
	}

	String booleanToCSVString(boolean b) {
		return b ? "X" : ""; //$NON-NLS-1$ //$NON-NLS-2$
	}

	String toCSVString(String s) {
		if (s == null) {
			return ""; //$NON-NLS-1$
		}
		return s;
	}

	private void addAttributeToCSV(CSVFile csvFile, Entity e, Attribute attr) {
		csvFile.setInCurrentRow(COL_TABLENAME, toCSVString(e.getName()));

		csvFile.setInCurrentRow(COL_FIELDNAME, toCSVString(attr.getName()));

		csvFile.setInCurrentRow(COL_KEY, booleanToCSVString(attr.isPartOfPrimaryKey()));

		String dataType = LdmAccessor.getAnnotationValue(attr, Constants.ANNOT_DATATYPE_DATATYPE);
		csvFile.setInCurrentRow(COL_TYPE, toCSVString(dataType));

		String length = LdmAccessor.getAnnotationValue(attr, Constants.ANNOT_DATATYPE_LENGTH);
		csvFile.setInCurrentRow(COL_LENGTH, toCSVString(length));

		String dataElementName = LdmAccessor.getAnnotationValue(attr, Constants.ANNOT_DATATYPE_DATA_ELEMENT);
		csvFile.setInCurrentRow(COL_DATA_ELEM, toCSVString(dataElementName));

		String domainName = LdmAccessor.getAnnotationValue(attr, Constants.ANNOT_DATATYPE_DOMAIN);
		csvFile.setInCurrentRow(COL_DOMAIN, toCSVString(domainName));

		csvFile.setInCurrentRow(COL_SHORT_TEXT, toCSVString(attr.getDescription()));
	}

	@Override
	public String getReportDescription() {
		return Messages.AppendIncludeStructureReport_2;
	}

}
