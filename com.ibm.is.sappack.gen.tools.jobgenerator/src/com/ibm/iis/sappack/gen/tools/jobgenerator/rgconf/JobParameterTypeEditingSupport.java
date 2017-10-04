//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2014                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.iis.sappack.gen.tools.jobgenerator.rgconf
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.iis.sappack.gen.tools.jobgenerator.rgconf;


import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.custom.CCombo;


public class JobParameterTypeEditingSupport extends ObservableEditingSupport {
	private CellEditor cellEditor;
	private TableViewer tableViewer;
	private static String[] dataTypes;
	
	static {
		dataTypes = new String[] { JobParameter.TYPE_NAME_STRING, JobParameter.TYPE_NAME_ENCRYPTED, JobParameter.TYPE_NAME_INTEGER, JobParameter.TYPE_NAME_FLOAT, JobParameter.TYPE_NAME_PATHNAME, JobParameter.TYPE_NAME_LIST, JobParameter.TYPE_NAME_DATE, JobParameter.TYPE_NAME_TIME, JobParameter.TYPE_NAME_PARAMETER_SET};
//		Arrays.sort(dataTypes);
	}


  	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
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
		this.observable.setChanged();
		this.observable.notifyObservers();
	}


}
