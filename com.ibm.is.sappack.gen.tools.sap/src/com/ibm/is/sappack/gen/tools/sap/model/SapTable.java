//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2011, 2012                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.is.sappack.gen.tools.sap.model
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.tools.sap.model;


import java.util.HashSet;
import java.util.Iterator;

import com.ibm.is.sappack.gen.tools.sap.constants.Constants;


public class SapTable implements Comparable<SapTable> {

	public static final int COLUMN_SELECTED     = 0;
	public static final int COLUMN_NAME         = COLUMN_SELECTED + 1;
	public static final int COLUMN_DESCRIPTION  = COLUMN_NAME + 1;
	public static final int COLUMN_CHECK_TABLES = COLUMN_DESCRIPTION + 1;
	public static final int COLUMN_TEXT_TABLES  = COLUMN_CHECK_TABLES + 1;

	private String            name;
	private String            description;
	private HashSet<SapTable> textTables;
	private HashSet<SapTable> checkTables;
	private boolean           checkTable = false;
	private boolean           textTable  = false;
	private boolean           checkTablesDone;
	private boolean           textTablesDone;
	private boolean           selected = true;
	private boolean           existsOnSAPSystem;
	private boolean           existsInModel;
    private String devClass;


	static String copyright() {
		return com.ibm.is.sappack.gen.tools.sap.model.Copyright.IBM_COPYRIGHT_SHORT;
	}

	public SapTable(String sapMetadata) {
		int delimiterPosition = sapMetadata.indexOf(Constants.SAP_COLUMN_DELIMITER);
		this.name = sapMetadata.substring(0, delimiterPosition);
		this.name = this.name.trim();
		this.description = sapMetadata.substring(delimiterPosition + 1);
	}

	public SapTable(String name, String description) {
		this.name              = name;
		this.description       = description;
		this.textTables        = new HashSet<SapTable>();
		this.checkTables       = new HashSet<SapTable>();
		this.selected          = true;
		this.existsOnSAPSystem = true;
		this.existsInModel     = false;
	}

	public String getName() {
		return this.name;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setCheckTable(boolean isCheckTable) {
		this.checkTable = isCheckTable;
	}

	public void setTextTable() {
		this.textTable = true;
	}

	public boolean isCheckTable() {
		return this.checkTable;
	}

	public boolean isTextTable() {
		return this.textTable;
	}

	public boolean isCheckTablesDone() {
		return this.checkTablesDone;
	}

	public void setCheckTablesDone(boolean checkTablesDone) {
		this.checkTablesDone = checkTablesDone;
	}

	public void setCheckTablesDone() {
		this.checkTablesDone = true;
	}

	public boolean isTextTablesDone() {
		return this.textTablesDone;
	}

	public void setTextTablesDone(boolean textTablesDone) {
		this.textTablesDone = textTablesDone;
	}

	public void setTextTablesDone() {
		this.textTablesDone = true;
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof SapTable) {
			SapTable other = (SapTable) object;
			return this.name.equals(other.name);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.name.hashCode();
	}

	public HashSet<SapTable> getTextTables() {
		return this.textTables;
	}

	public HashSet<SapTable> getCheckTables() {
		return this.checkTables;
	}

	public void addTextTable(SapTable table) {
		this.textTables.add(table);
		table.setTextTable();
	}

	public void addCheckTable(SapTable table) {
		this.checkTables.add(table);
		table.setCheckTable(true);
	}

	public String getTextTablesAsString() {
		return getTablesAsString(this.textTables);
	}

	public String getCheckTablesAsString() {
		return getTablesAsString(this.checkTables);
	}

	public boolean getSelected() {
		return this.selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public void setSelected() {
		this.selected = true;
	}

	public void setExistsOnSAPSystem(boolean exists) {
		this.existsOnSAPSystem = exists;
		if (!exists) {
			setSelected(false);
		}
	}

   public void setExistsInModel(boolean exists) {
      this.existsInModel = exists;
   }

	public boolean existsOnSAPSystem() {
		return this.existsOnSAPSystem;
	}

   public boolean existsInModel() {
      return this.existsInModel;
   }

	private static final String getTablesAsString(HashSet<SapTable> tables) {
		StringBuffer buffer = new StringBuffer();
		Iterator<SapTable> iterator = tables.iterator();
		for (int i = 0; iterator.hasNext(); i++) {
			SapTable table = iterator.next();
			buffer.append(table.getName());

			if (i < tables.size() - 1) {
				buffer.append(',');
			}
		}
		return buffer.toString();
	}

	@Override
	public int compareTo(SapTable other) {
		return this.name.compareTo(other.name);
	}

	public void setName(String name) {
		this.name = name;
	}

	@SuppressWarnings("nls")
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(name + "(" + description + ")");
		if (this.checkTables != null) {
			buffer.append(" checktables:[");

			Iterator<SapTable> checkTableIterator = this.checkTables.iterator();
			while (checkTableIterator.hasNext()) {
				SapTable checkTable = checkTableIterator.next();
				buffer.append(checkTable.getName());
				if (checkTableIterator.hasNext()) {
					buffer.append(",");
				}
			}
			buffer.append("]");
		}
		if (this.textTables != null) {

			buffer.append(" texttables:[");
			Iterator<SapTable> textTableIterator = this.textTables.iterator();
			while (textTableIterator.hasNext()) {
				SapTable textTable = textTableIterator.next();
				buffer.append(textTable.getName());
				if (textTableIterator.hasNext()) {
					buffer.append(",");
				}
			}
			buffer.append("]");
		}
		return buffer.toString();
	}

	public HashSet<String> getRelatedDataTableNames() {
		HashSet<String> dataTables = new HashSet<String>();

		// FIXME:
		// A data table is a table listed as checktable but has the flag
		// "isCheckTable" set to false
		// I know it's ugly :-(, we should introduce a new flag for data tables
		Iterator<SapTable> iterator = this.checkTables.iterator();
		while (iterator.hasNext()) {
			SapTable checkTable = iterator.next();
			if (!checkTable.isCheckTable()) {
				dataTables.add(checkTable.getName());
			}
		}
		return dataTables;
	}

	public String getDevClass() {
		return devClass;
	}

	public void setDevClass(String devClass) {
		this.devClass = devClass;
	}

}
