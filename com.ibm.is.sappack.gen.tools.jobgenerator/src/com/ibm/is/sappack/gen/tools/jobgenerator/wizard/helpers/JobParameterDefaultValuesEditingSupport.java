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


import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import com.ibm.iis.sappack.gen.tools.jobgenerator.rgconf.JobParameter;
import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.tools.sap.constants.Constants;


public class JobParameterDefaultValuesEditingSupport extends EditingSupport {

	public final static int EDIT_NAME = 0;
	public final static int EDIT_PROMPT = 1;
	public final static int EDIT_DEFAULT = 2;
	public final static int EDIT_HELP = 3;

	private final static String DEFAULT_EDITOR = "defaultEditor"; //$NON-NLS-1$
	private final static String NUMERIC_EDITOR = "numericEditor"; //$NON-NLS-1$
	private final static String ENCRYPTED_EDITOR = "encryptedEditor"; //$NON-NLS-1$

	private int attributeToEdit;
	private TableViewer tableViewer;

	private WizardPage wizardPage;

	private Map<String, CellEditor> cellEditors;

	static String copyright() {
		return com.ibm.is.sappack.gen.tools.jobgenerator.wizard.helpers.Copyright.IBM_COPYRIGHT_SHORT;
	}

	public JobParameterDefaultValuesEditingSupport(TableViewer tableViewer, int attributeToEdit, WizardPage wizardPage) {
		super(tableViewer);
		this.attributeToEdit = attributeToEdit;
		this.tableViewer = tableViewer;
		this.wizardPage = wizardPage;
		this.cellEditors = new HashMap<String, CellEditor>();

		this.createCellEditors();
	}

	/**
	 * createCellEditors
	 */
	private void createCellEditors() {

		/* cell editor for non-numeric, non-encrypted job parameters */
		CellEditor defaultEditor = new TextCellEditor((Composite) this.tableViewer.getControl());
		this.cellEditors.put(DEFAULT_EDITOR, defaultEditor); 

		/* cell editor for numeric job parameters */
		CellEditor numericEditor = new TextCellEditor((Composite) this.tableViewer.getControl());
		numericEditor.setValidator(new ICellEditorValidator() {

			@Override
			public String isValid(Object value) {

				if (value == null) {
					wizardPage.setErrorMessage(null);
					return null;
				}

				if (((String) value).equals(Constants.EMPTY_STRING)) {
					wizardPage.setErrorMessage(null);
					return null;
				}

				/* check if cell value is numeric */
				try {
					Integer.parseInt((String) value);
					wizardPage.setErrorMessage(null);
				} catch (Exception e) {
					wizardPage.setErrorMessage(Messages.JobParameterStringValuesEditingSupport_3);
					return null;
				}

				return null;
			}

		});
		this.cellEditors.put(NUMERIC_EDITOR, numericEditor);

		/* cell editor for encrypted job parameters */
		CellEditor encryptedEditor = new TextCellEditor( (Composite)this.tableViewer.getControl());
		Text text = (Text) encryptedEditor.getControl();
		text.setEchoChar('*'); 
		this.cellEditors.put(ENCRYPTED_EDITOR, encryptedEditor);

	}

	@Override
	protected boolean canEdit(Object element) {
		
		return true;
	}

	@Override
	protected CellEditor getCellEditor(Object element) {

		JobParameter param = (JobParameter) element;

		if (param.getType().equals(JobParameterTypeEditingSupport.TYPE_NAME_ENCRYPTED)) {
			return this.cellEditors.get(ENCRYPTED_EDITOR);
		}

		if (param.getType().equals(JobParameterTypeEditingSupport.TYPE_NAME_INTEGER)) {
			return this.cellEditors.get(NUMERIC_EDITOR);
		}

		return this.cellEditors.get(DEFAULT_EDITOR);
	}

	@Override
	protected Object getValue(Object element) {
		if (element instanceof JobParameter) {
			switch (attributeToEdit) {
			case EDIT_NAME:
				return ((JobParameter) element).getName();
			case EDIT_PROMPT:
				return ((JobParameter) element).getPrompt();
			case EDIT_DEFAULT:
				return ((JobParameter) element).getDefaultValue();
			case EDIT_HELP:
				return ((JobParameter) element).getHelp();
			default:
				throw new IllegalArgumentException();
			}
		}
		return null;
	}

	@Override
	protected void setValue(Object element, Object value) {

		if (value == null) {
			return;
		}

		if (element instanceof JobParameter) {
			JobParameter jobParameter = (JobParameter) element;
			switch (attributeToEdit) {
			case EDIT_NAME:
				jobParameter.setName(value.toString());
				break;
			case EDIT_PROMPT:
				jobParameter.setPrompt(value.toString());
				break;
			case EDIT_DEFAULT:
				jobParameter.setDefaultValue(value.toString());
				break;
			case EDIT_HELP:
				jobParameter.setHelp(value.toString());
				break;
			default:
				throw new IllegalArgumentException();
			}

			getViewer().update(jobParameter, null);
		}
	}
}
