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


import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;


public abstract class SimpleParameterEditingSupport extends ObservableEditingSupport {
	private   boolean        useTypeSpecificEditor;
	protected TextCellEditor defaultEditor;
	protected TextCellEditor numericEditor;
	protected TextCellEditor encryptedEditor;


  	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	


	public SimpleParameterEditingSupport(TableViewer viewer, boolean useTypeSpecificEditor) {
		super(viewer);
		createCellEditors();
		this.useTypeSpecificEditor = useTypeSpecificEditor;
	}

	protected abstract String getStringValue(JobParameter param);

	protected abstract void setStringValue(JobParameter param, String value);

	private void createCellEditors() {

		/* cell editor for non-numeric, non-encrypted job parameters */
		defaultEditor = new TextCellEditor((Composite) this.getViewer().getControl());

		/* cell editor for numeric job parameters */
		numericEditor = new TextCellEditor((Composite) this.getViewer().getControl());
		numericEditor.setValidator(new ICellEditorValidator() {

			@Override
			public String isValid(Object value) {
				return null;
			}

		});

		/* cell editor for encrypted job parameters */
		encryptedEditor = new TextCellEditor((Composite) this.getViewer().getControl());
		Text text = (Text) encryptedEditor.getControl();
		text.setEchoChar('*');

	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		if (!useTypeSpecificEditor) {
			return this.defaultEditor;
		}
		JobParameter param = (JobParameter) element;
		if (param.getType().equals(JobParameter.TYPE_NAME_ENCRYPTED)) {
			return this.encryptedEditor;
		}
		if (param.getType().equals(JobParameter.TYPE_NAME_INTEGER)) {
			return this.numericEditor;
		}
		return this.defaultEditor;
	}

	@Override
	protected boolean canEdit(Object element) {
		return true;
	}

	@Override
	protected Object getValue(Object element) {
		return getStringValue((JobParameter) element);
	}

	@Override
	protected void setValue(Object element, Object value) {

	   setStringValue((JobParameter) element, (String) value);
		getViewer().update(element, null);
		updateJobParamListInViewer((JobParameter) element);
		
		this.observable.setChanged();
		this.observable.notifyObservers();
	}

	private void updateJobParamListInViewer(JobParameter newJobParamData) {
		List<JobParameter> viewerJobParamList = (List<JobParameter>) getViewer().getInput();
		int                listIdx;
		boolean            replaced;

		// search for the passed JobParam item ...
		replaced = false;
		listIdx  = 0;
		while (listIdx < viewerJobParamList.size() && !replaced) {
			JobParameter jp = viewerJobParamList.get(listIdx);
			
			if (jp.equals(newJobParamData)) {
				viewerJobParamList.set(listIdx, newJobParamData);
				replaced = true;
			}
			listIdx ++;
		}
	}	

}
