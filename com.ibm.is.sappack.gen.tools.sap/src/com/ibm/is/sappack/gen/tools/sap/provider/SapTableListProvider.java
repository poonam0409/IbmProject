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
// Module Name : com.ibm.is.sappack.gen.tools.sap.provider
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.tools.sap.provider;

import java.util.HashSet;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;

import com.ibm.is.sappack.gen.tools.sap.model.SapTable;

public class SapTableListProvider implements IStructuredContentProvider, ITableLabelProvider {

	private HashSet<SapTable> tables = new HashSet<SapTable>();
	
	static String copyright()
	{ return com.ibm.is.sappack.gen.tools.sap.provider.Copyright.IBM_COPYRIGHT_SHORT; }

	public void clear() {
		this.tables.clear();
	}

	public void addTable(SapTable sapTable) {
		this.tables.add(sapTable);
	}

	public void removeTable(SapTable sapTable) {
		this.tables.remove(sapTable);
	}

	public void addAll(SapTable[] sapTables) {
		for (int i = 0; i < sapTables.length; i++) {
			addTable(sapTables[i]);
		}
	}

	public HashSet<SapTable> getTables() {
		return this.tables;
	}

	@SuppressWarnings("unchecked")
	public Object[] getElements(Object inputElement) {
		HashSet<SapTable> tables = (HashSet<SapTable>) inputElement;
		return tables.toArray();
	}

	public String getColumnText(Object element, int columnIndex) {
		SapTable table = (SapTable) element;
		switch (columnIndex) {
		case 0:
			return table.getName();
		case 1:
			return table.getDescription();
		default:
			return null;
		}

	}

	@Override
	public void dispose() {
		// Nothing to be done here
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// Nothing to be done here
	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		// Nothing to be done here
		return null;
	}

	@Override
	public void addListener(ILabelProviderListener listener) {
		// Nothing to be done here
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		// Nothing to be done here
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
		// Nothing to be done here
	}
}
