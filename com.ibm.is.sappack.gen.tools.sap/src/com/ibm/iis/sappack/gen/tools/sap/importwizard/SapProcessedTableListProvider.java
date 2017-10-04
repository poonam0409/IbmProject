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
// Module Name : com.ibm.iis.sappack.gen.tools.sap.importwizard
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.iis.sappack.gen.tools.sap.importwizard;


import java.util.Iterator;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TableItem;

import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.tools.sap.constants.Constants;
import com.ibm.is.sappack.gen.tools.sap.model.SapTable;
import com.ibm.is.sappack.gen.tools.sap.model.SapTableSet;


public class SapProcessedTableListProvider implements IStructuredContentProvider, ITableLabelProvider, ICellModifier {
	private static final String TABLE_LIST_SEPARATOR = ","; //$NON-NLS-1$

	private TableViewer tableViewer;
	

  	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	
	
	public SapProcessedTableListProvider(TableViewer tableViewer) {
		this.tableViewer = tableViewer;
	}

	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof SapTableSet) {
			SapTableSet sapTableSet = (SapTableSet) inputElement;
			return sapTableSet.toSortedArray();
		}
		return null;
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
		return null;
		/*
		if (columnIndex == 0) {
			SapTable table = (SapTable) element;
			ImageRegistry imageRegistry = Activator.getDefault().getImageRegistry();
			if (!table.existsOnSAPSystem()) {
				return imageRegistry.get(Constants.ICON_ID_CHECKBOX_UNCHECKED_DISABLED);
			}
			if (table.getSelected()) {
				return imageRegistry.get(Constants.ICON_ID_CHECKBOX_CHECKED);
			} else {
				return imageRegistry.get(Constants.ICON_ID_CHECKBOX_UNCHECKED);
			}
		}
		return null;
		*/
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		SapTable table = (SapTable) element;
		switch (columnIndex) {
		case TableListWizardPage.COLUMN_NAME:
			return table.getName();
		case TableListWizardPage.COLUMN_DESCRIPTION:
			return table.getDescription();
		case TableListWizardPage.COLUMN_CHECK_TABLES:
			return getBaseTablesForCheckOrTextTable(table, true);
		case TableListWizardPage.COLUMN_TEXT_TABLES:
			return getBaseTablesForCheckOrTextTable(table, false);
		default:
			return Constants.EMPTY_STRING;
		}
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

	@Override
	public boolean canModify(Object element, String property) {
	   boolean isModifiedAllowed = true;
	   
      if (element instanceof SapTable) {
         SapTable st = (SapTable) element;
         isModifiedAllowed = st.isCheckTable() || st.isTextTable();
      }

      return(isModifiedAllowed);
	}

	@Override
	public Object getValue(Object element, String property) {
		SapTable table = (SapTable) element;
		return Boolean.valueOf(table.getSelected());
	}

	@Override
	public void modify(Object element, String property, Object value) {
		TableItem item = (TableItem) element;
		SapTable sapTable = (SapTable) item.getData();

		// inform user that table already exists
      if (!sapTable.getSelected() && sapTable.existsInModel()) {
         MessageDialog.openWarning(Display.getCurrent().getActiveShell(), 
                                   Messages.TitleWarning, 
                                   Messages.TableListWizardPage_20);
      }
      
		sapTable.setSelected(((Boolean) value).booleanValue());
		this.tableViewer.update(sapTable, null);
	}
	
	private String getBaseTablesForCheckOrTextTable(SapTable table, boolean isForCheckTable) {
		String baseTables = new String();
		SapTableSet tblset = (SapTableSet) this.tableViewer.getInput();
		String[] checkOrTextTables = null;
		for (Iterator<SapTable> tblit = tblset.iterator(); tblit.hasNext();) {
			SapTable tbl = tblit.next();
			if (isForCheckTable) {
				checkOrTextTables = tbl.getCheckTablesAsString().split(TABLE_LIST_SEPARATOR);
			}
			else {
				checkOrTextTables = tbl.getTextTablesAsString().split(TABLE_LIST_SEPARATOR);
			}
			for (int i = 0; i < checkOrTextTables.length; i++) {
				if (checkOrTextTables[i].equalsIgnoreCase(table.getName())) {
					if (!baseTables.contains(tbl.getName())) {
						baseTables = baseTables.concat(tbl.getName());
						baseTables = baseTables.concat(TABLE_LIST_SEPARATOR);
					}
				}
			}			
		}
		
		if(baseTables.endsWith(TABLE_LIST_SEPARATOR)) {
			int seppos = baseTables.lastIndexOf(TABLE_LIST_SEPARATOR);
			baseTables = baseTables.substring(0, seppos);
		}
		
		return baseTables;
	}
}
