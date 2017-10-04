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

import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TableItem;

import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.tools.sap.activator.Activator;
import com.ibm.is.sappack.gen.tools.sap.constants.Constants;
import com.ibm.is.sappack.gen.tools.sap.importer.idoctypes.Segment;
import com.ibm.is.sappack.gen.tools.sap.model.IDocSegmentTableSet;
import com.ibm.is.sappack.gen.tools.sap.model.IDocTableSet;
import com.ibm.is.sappack.gen.tools.sap.model.SapTable;
import com.ibm.is.sappack.gen.tools.sap.model.SapTableSet;

public class SapProcessedIDocListProvider implements IStructuredContentProvider, ITableLabelProvider, ICellModifier {

	private static final String TABLE_LIST_SEPARATOR = ","; //$NON-NLS-1$

	private TableViewer tableViewer;
	private WizardPage  wizardPage;


   static String copyright() {
      return com.ibm.is.sappack.gen.tools.sap.provider.Copyright.IBM_COPYRIGHT_SHORT;
   }
   
	
	/**
	 * SapProcessedIDocListProvider
	 * @param tableViewer
	 */
	public SapProcessedIDocListProvider(TableViewer tableViewer, WizardPage wizardPage) {
		this.tableViewer = tableViewer;
		this.wizardPage = wizardPage;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof IDocTableSet) {
			IDocTableSet idocTableSet = (IDocTableSet) inputElement;
			return idocTableSet.toSortedArray();
		}
		return null;
	}

	@Override
	public void dispose() {
		// Nothing to be done here

	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		

	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		if (columnIndex == 0) {

			/* IDoc Segment */
			if (element instanceof Segment) {
				// osuhre: Segments are always selected
				ImageRegistry imageRegistry = Activator.getDefault().getImageRegistry();
				return imageRegistry.get(Constants.ICON_ID_CHECKBOX_CHECKED_DISABLED);
			}
			
			/* Table */
			if (element instanceof SapTable) {
				SapTable table = (SapTable) element;
				if (!table.existsOnSAPSystem()) {
					ImageRegistry imageRegistry = Activator.getDefault().getImageRegistry();
					return imageRegistry.get(Constants.ICON_ID_CHECKBOX_UNCHECKED_DISABLED);
				}
				ImageRegistry imageRegistry = Activator.getDefault().getImageRegistry();
				if (table.getSelected()) {
					return imageRegistry.get(Constants.ICON_ID_CHECKBOX_CHECKED);
				} else {
					return imageRegistry.get(Constants.ICON_ID_CHECKBOX_UNCHECKED);
				}
			}

		}
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {

		if (element instanceof Segment) {
			Segment segment = (Segment) element;
			switch (columnIndex) {
			case SapTable.COLUMN_NAME:
				return segment.getType();
			case SapTable.COLUMN_DESCRIPTION:
				return segment.getDescription();
			case SapTable.COLUMN_CHECK_TABLES:
			case SapTable.COLUMN_TEXT_TABLES:
			default:
				return Constants.EMPTY_STRING;
			}
		}

		if (element instanceof SapTable) {
			SapTable table = (SapTable) element;

			switch (columnIndex) {
			case SapTable.COLUMN_NAME:
				return table.getName();
			case SapTable.COLUMN_DESCRIPTION:
				return table.getDescription();
			case SapTable.COLUMN_CHECK_TABLES:
				String colEntry = new String();
				colEntry = colEntry.concat(getSegmentsForCheckTable(table));
				String baseTables = getBasetablesForCheckOrTextTable(table, true);
				if (baseTables.length() != 0) {
					colEntry = colEntry.concat(TABLE_LIST_SEPARATOR);
					colEntry = colEntry.concat(baseTables);
				}
				return colEntry;
			case SapTable.COLUMN_TEXT_TABLES:
				return getBasetablesForCheckOrTextTable(table, false);
			default:
				return Constants.EMPTY_STRING;
			}
		}

		return Constants.EMPTY_STRING;
	}

	
	public WizardPage getWizardPage() {
	   return(this.wizardPage);
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
	public Object getValue(Object element, String property) {

		if(element instanceof Segment) {
			Segment segment = (Segment) element;
			return segment.isSelected();
		}
		
		if(element instanceof SapTable) {
			SapTable table = (SapTable) element;
			return table.getSelected();
		}
	
		return true;
	}

	@Override
	public boolean canModify(Object element, String property) {
		if (element instanceof Segment) {
			return false;
		} 
		if (element instanceof SapTable) {
			SapTable st = (SapTable) element;
			return st.existsOnSAPSystem();
		}

		return true;
	}

	@Override
	public void modify(Object element, String property, Object value) {
		
		TableItem item = (TableItem) element;
		Object obj = item.getData();
		
		if(obj instanceof SapTable) {
			SapTable sapTable = (SapTable) obj;
			
	      // inform user that table already exists
	      if (!sapTable.getSelected() && sapTable.existsInModel()) {
	         MessageDialog.openWarning(Display.getCurrent().getActiveShell(), 
	                                   Messages.TitleWarning, 
	                                   Messages.TableListWizardPage_20);
	      }
	      
			sapTable.setSelected(((Boolean) value).booleanValue());
			this.tableViewer.update(sapTable, null);
		}
		
		if(obj instanceof Segment) {
			Segment segment = (Segment) obj;
			segment.setSelected(((Boolean) value).booleanValue());
			this.tableViewer.update(segment, null);
		}	
		
		/*
		// display a warning message when at least one item is unselected
		boolean showWarning = false;
		IDocTableSet idocTableSet = (IDocTableSet) this.tableViewer.getInput();
		for(IDocSegmentTableSet idocSegmentTableSet:idocTableSet.getIdocSegmentTableSetList()) {
			Segment segment = idocSegmentTableSet.getIdocSegment();
			if (segment == null) {
				continue;
			}
			if(!segment.isSelected()) {
				showWarning = true;
			}
		}
		
		if(showWarning) {
			this.wizardPage.setMessage(Messages.IDocSegmentListWizardPage_2, IStatus.WARNING);
		} else {
			this.wizardPage.setMessage(null);
		}
		*/
		
	}
	
	private String getSegmentsForCheckTable(SapTable table) {
		String segments = new String();
		IDocTableSet idoctblset = (IDocTableSet) this.tableViewer.getInput();
		List<IDocSegmentTableSet> idocstbllist = idoctblset.getIdocSegmentTableSetList();
		for (Iterator<IDocSegmentTableSet> idocstblsetit = idocstbllist.iterator(); idocstblsetit.hasNext();) {
			IDocSegmentTableSet idocstblset = idocstblsetit.next();
			Segment idocsgmnt = idocstblset.getIdocSegment();
			String[] checkOrTextTables = idocsgmnt.getAllCheckTablesAsString().split(TABLE_LIST_SEPARATOR);
			for (int i = 0; i < checkOrTextTables.length; i++) {
				if (checkOrTextTables[i].equalsIgnoreCase(table.getName())) {
					segments = segments.concat(idocsgmnt.getType());
					segments = segments.concat(TABLE_LIST_SEPARATOR);
				}
			}
		}

		if (segments.endsWith(TABLE_LIST_SEPARATOR)) {
			int seppos = segments.lastIndexOf(TABLE_LIST_SEPARATOR);
			segments = segments.substring(0, seppos);
		}

		return segments;
	}

	private String getBasetablesForCheckOrTextTable(SapTable table, boolean isForCheckTable) {
		String baseTables = new String();
		IDocTableSet idoctblset = (IDocTableSet) this.tableViewer.getInput();
		List<IDocSegmentTableSet> idocstbllist = idoctblset.getIdocSegmentTableSetList();
		for (Iterator<IDocSegmentTableSet> idocstblsetit = idocstbllist.iterator(); idocstblsetit.hasNext();) {
			IDocSegmentTableSet idocstblset = idocstblsetit.next();
			SapTableSet tblset = idocstblset.getSapTableSet();
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
		}

		if (baseTables.endsWith(TABLE_LIST_SEPARATOR)) {
			int seppos = baseTables.lastIndexOf(TABLE_LIST_SEPARATOR);
			baseTables = baseTables.substring(0, seppos);
		}

		return baseTables;
	}
}
