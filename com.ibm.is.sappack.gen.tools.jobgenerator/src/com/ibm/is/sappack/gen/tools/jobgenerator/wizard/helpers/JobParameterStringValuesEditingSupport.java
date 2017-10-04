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
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;


public class JobParameterStringValuesEditingSupport extends EditingSupport {

	public final static int EDIT_NAME = 0;
	public final static int EDIT_PROMPT = 1;
	public final static int EDIT_DEFAULT = 2;
	public final static int EDIT_HELP = 3;
	
	private int attributeToEdit;
	private CellEditor cellEditor;
	
	static String copyright()
	{ return com.ibm.is.sappack.gen.tools.jobgenerator.wizard.helpers.Copyright.IBM_COPYRIGHT_SHORT; }	

	public JobParameterStringValuesEditingSupport(TableViewer tableViewer, int attributeToEdit) {
		super(tableViewer);
		this.attributeToEdit = attributeToEdit;
		this.cellEditor = new TextCellEditor(tableViewer.getTable());
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
		if (element instanceof JobParameter) {
			switch(attributeToEdit) {
			case EDIT_NAME: return ((JobParameter) element).getName();
			case EDIT_PROMPT: return ((JobParameter) element).getPrompt();
			case EDIT_DEFAULT: return ((JobParameter) element).getDefaultValue();
			case EDIT_HELP: return ((JobParameter) element).getHelp();
			default: throw new IllegalArgumentException();
			}
		}
		return null;
	}

	@Override
	protected void setValue(Object element, Object value) {
		if (element instanceof JobParameter) {
			JobParameter jobParameter = (JobParameter) element;
			switch(attributeToEdit) {
			case EDIT_NAME: jobParameter.setName(value.toString()); break;
			case EDIT_PROMPT: jobParameter.setPrompt(value.toString()); break;
			case EDIT_DEFAULT: jobParameter.setDefaultValue(value.toString()); break;
			case EDIT_HELP: jobParameter.setHelp(value.toString()); break;
			default: throw new IllegalArgumentException();
			}
						
			getViewer().update(jobParameter, null);
		}
	}
}
