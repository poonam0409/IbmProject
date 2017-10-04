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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import com.ibm.is.sappack.gen.tools.sap.utilities.Utilities;

public final class SapTableSet implements Collection<SapTable>, Cloneable {

	private static final long                      serialVersionUID = 1L;
	private              HashMap<String, SapTable> tables;


	static String copyright() { 
	   return com.ibm.is.sappack.gen.tools.sap.model.Copyright.IBM_COPYRIGHT_SHORT; 
	}
	
	// The name of the business object these tables balong to;
	private String businessObjectName;
	
	public SapTableSet(String businessObjectName) {
		this();
		this.businessObjectName=businessObjectName;
	}

	public SapTableSet() {
		this.tables = new HashMap<String, SapTable>();
	}

	public boolean add(SapTable newTable) {
		if (newTable == null) {
			return false;
		}

		SapTable oldTable = this.tables.get(newTable.getName());
		if (oldTable == null) {
			this.tables.put(newTable.getName(), newTable);
			return true;
		}
//		System.out.println("old:" + oldTable + " new: " + newTable);
		// only replace the table, if the new table has a description
		if (!Utilities.isEmpty(newTable.getDescription())) {
			this.tables.put(newTable.getName(), newTable);
			return true;
		}

		return false;
	}
	
	public String getBusinessObjectName() {
		return this.businessObjectName;
	}
	
	public void setBusinessObjectName(String businessObjectName) {
		this.businessObjectName=businessObjectName;
	}

	@SuppressWarnings("unchecked")
	public boolean addAll(Collection<? extends SapTable> collection) {
		boolean changed = false;
		Iterator<SapTable> iterator = (Iterator<SapTable>) collection.iterator();
		while (iterator.hasNext()) {
			SapTable table = iterator.next();
			if (this.add(table)) {
				changed = true;
			}
		}
		return changed;
	}

	public Iterator<SapTable> iterator() {
		return this.tables.values().iterator();
	}

	@SuppressWarnings("unchecked")
	public Object clone() {
		SapTableSet tableSet = new SapTableSet();
		tableSet.tables = (HashMap<String, SapTable>) this.tables.clone();
		return tableSet;
	}

	@Override
	public void clear() {
		this.tables.clear();
	}

	@Override
	public boolean contains(Object object) {
		if (!(object instanceof SapTable)) {
			return false;
		}
		SapTable sapTable = (SapTable) object;
		return this.tables.containsKey(sapTable.getName());
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean containsAll(Collection<?> collection) {
		Iterator<SapTable> iterator = (Iterator<SapTable>) collection.iterator();
		while (iterator.hasNext()) {
			SapTable table = iterator.next();
			if (!this.tables.containsKey(table.getName())) {
				return false;
			}
		}
		return true;
	}

   public SapTable getTable(String tableName) {
      return(this.tables.get(tableName));
   }

	@Override
	public boolean isEmpty() {
		return this.tables.isEmpty();
	}

	@Override
	public boolean remove(Object object) {
		if (!(object instanceof SapTable)) {
			return false;
		}

		SapTable sapTable = (SapTable) object;
		return !(this.tables.remove(sapTable.getName()) == null);
	}

	@Override
	public boolean removeAll(Collection<?> collection) {
		// Nothing to be done here
		return false;
	}

	@Override
	public boolean retainAll(Collection<?> collection) {
		return this.tables.values().retainAll(collection);
	}

	@Override
	public int size() {
		return this.tables.size();
	}

	@Override
	public Object[] toArray() {
		return this.tables.values().toArray();
	}

	@Override
	public <T> T[] toArray(T[] array) {
		return this.tables.values().toArray(array);
	}

	public SapTable[] toSortedArray() {
		SapTable[] sapTables = this.tables.values().toArray(new SapTable[this.size()]);
		Arrays.sort(sapTables);
		return sapTables;
	}
	
	public boolean contains(String tableName) {
		return this.tables.containsKey(tableName);
	}

}
