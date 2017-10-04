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
// Module Name : com.ibm.is.sappack.gen.tools.jobgenerator.wizard.helpers
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.tools.jobgenerator.wizard.helpers;


import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.custom.CCombo;


public class JobParameterTypeEditingSupport extends EditingSupport {

	public static final String TYPE_NAME_STRING = "String"; //$NON-NLS-1$
	public static final String TYPE_NAME_ENCRYPTED = "Encrypted"; //$NON-NLS-1$
	public static final String TYPE_NAME_INTEGER = "Integer"; //$NON-NLS-1$
	public static final String TYPE_NAME_FLOAT = "Float"; //$NON-NLS-1$
	public static final String TYPE_NAME_PATHNAME = "Pathname"; //$NON-NLS-1$
	public static final String TYPE_NAME_LIST = "List"; //$NON-NLS-1$
	public static final String TYPE_NAME_DATE = "Date"; //$NON-NLS-1$
	public static final String TYPE_NAME_TIME = "Time"; //$NON-NLS-1$
	public static final String TYPE_NAME_PARAMETER_SET = "Parameter Set"; //$NON-NLS-1$
	
	private CellEditor cellEditor;
	private TableViewer tableViewer;
	private static String[] dataTypes;
	
	static String copyright()
	{ return com.ibm.is.sappack.gen.tools.jobgenerator.wizard.helpers.Copyright.IBM_COPYRIGHT_SHORT; }	
	
	static {
		dataTypes = new String[] { TYPE_NAME_STRING, TYPE_NAME_ENCRYPTED, TYPE_NAME_INTEGER, TYPE_NAME_FLOAT, TYPE_NAME_PATHNAME, TYPE_NAME_LIST, TYPE_NAME_DATE, TYPE_NAME_TIME, TYPE_NAME_PARAMETER_SET};
//		Arrays.sort(dataTypes);
	}

	public JobParameterTypeEditingSupport(TableViewer tableViewer) {
		super(tableViewer);
		this.tableViewer = tableViewer;
		this.cellEditor = new ComboBoxCellEditor(tableViewer.getTable(), dataTypes);
	}

	public static final String getFirstDataType() {
		return dataTypes[0];
	}

	@Override
	protected boolean canEdit(Object element) {
		return true;
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		return this.cellEditor;
	}

	@Override
	protected Object getValue(Object element) {
		JobParameter jobParameter = (JobParameter) element;
		for (int i = 0; i < dataTypes.length; i++) {
			if (dataTypes[i].equals(jobParameter.getType())) {
				return Integer.valueOf(i);
			}
		}

		// return new Integer(this.dataTypes.length - 1);
		return null;
	}

	@Override
	protected void setValue(Object element, Object value) {
		JobParameter jobParameter = (JobParameter) element;
		Integer integerIndex = (Integer) value;
		int index = integerIndex.intValue();
		if (index == -1) {
			String customDataType = ((CCombo) this.cellEditor.getControl()).getText();

			String[] newDataTypes = new String[dataTypes.length + 1];
			System.arraycopy(dataTypes, 0, newDataTypes, 0, dataTypes.length);
			dataTypes = newDataTypes;
			index = dataTypes.length - 1;
			dataTypes[index] = customDataType;

			this.cellEditor = new ComboBoxCellEditor(this.tableViewer.getTable(), dataTypes);

		}
		jobParameter.setType(dataTypes[index]);
		getViewer().update(jobParameter, null);
	}

}
