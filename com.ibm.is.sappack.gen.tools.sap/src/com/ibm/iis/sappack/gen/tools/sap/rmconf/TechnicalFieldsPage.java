//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2009, 2014                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.iis.sappack.gen.tools.sap.rmconf
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.iis.sappack.gen.tools.sap.rmconf;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ICellEditorListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;

import com.ibm.iis.sappack.gen.common.ui.editors.ConfigurationBase;
import com.ibm.iis.sappack.gen.common.ui.editors.EditorEvent;
import com.ibm.iis.sappack.gen.common.ui.editors.EditorPageBase;
import com.ibm.iis.sappack.gen.common.ui.editors.IControlFactory;
import com.ibm.iis.sappack.gen.common.ui.util.Utils;
import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.common.ui.WidgetIDConstants;
import com.ibm.is.sappack.gen.common.ui.WidgetIDUtils;
import com.ibm.is.sappack.gen.tools.sap.importer.helper.field.TechnicalField;
import com.ibm.is.sappack.gen.tools.sap.importer.helper.field.TechnicalFieldInteger;
import com.ibm.is.sappack.gen.tools.sap.importer.helper.field.TechnicalFieldVarchar;


public class TechnicalFieldsPage extends EditorPageBase {

	static final String TYPE_VARCHAR = "VARCHAR"; //$NON-NLS-1$
	static final String TYPE_INTEGEGER = "INTEGER"; //$NON-NLS-1$

	static final String SETTING_PFX                          = "TechnicalFieldsWizardPage"; //$NON-NLS-1$
	static final String SETTING_TECH_FIELD_OPTION            = SETTING_PFX + ".ConfigureFieldsOption"; //$NON-NLS-1$
	static final String SETTING_NAME                         = SETTING_PFX + ".TechFieldName";         //$NON-NLS-1$
	static final String SETTING_DESC                         = SETTING_PFX + ".TechFieldDesc";         //$NON-NLS-1$
	static final String SETTING_TYPE                         = SETTING_PFX + ".TechFieldType";         //$NON-NLS-1$
	static final String SETTING_LENGTH                       = SETTING_PFX + ".TechFieldLength";       //$NON-NLS-1$
	static final String SETTING_ISKEY                        = SETTING_PFX + ".TechFieldIsKey";        //$NON-NLS-1$
	static final String SETTING_ISNULLABLE                   = SETTING_PFX + ".TechFieldIsNullable";   //$NON-NLS-1$
	public static final String SETTING_NUMBER_OF_TECH_FIELDS = SETTING_PFX + ".TechFieldNumber";       //$NON-NLS-1$

	public static final String TABNAME = Messages.TechnicalFieldsWizardPage_0;


  	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	

	// this class is just necessary to be able change the type of the TechnicalField object
	// dynamically. It is only used by the table content provider and editing support classes.
	private static class TechnicalFieldContainer {
		private TechnicalField techField;

		private boolean readOnly = false;

		TechnicalFieldContainer(TechnicalField tf) {
			this.techField = tf;
			this.readOnly  = false;
		}

		TechnicalFieldContainer(TechnicalField tf, boolean readOnly) {
			this.techField = tf;
			this.readOnly  = readOnly;
		}

		public String toString() {
			if (this.techField == null) {
				return "null"; //$NON-NLS-1$
			}

			return this.techField.getFieldName();
		}
	}

	ICellEditorListener ceListener = new ICellEditorListener() {

		@Override
		public void editorValueChanged(boolean oldValidState, boolean newValidState) {
		}

		@Override
		public void cancelEditor() {
		}

		@Override
		public void applyEditorValue() {
//			saveTable();
		}
	};

	public class TechnicalFieldTableContentProvider implements IStructuredContentProvider {

		@Override
		public Object[] getElements(Object inputElement) {
			return technicalFields.toArray();
		}

		@Override
		public void dispose() {
			;
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

		public void removeTechnicalField(TechnicalFieldContainer field) {
			for (int i = 0; i < technicalFields.size(); i++) {
				TechnicalFieldContainer tfc = technicalFields.get(i);
				if (tfc.techField.getFieldName().equals(field.techField.getFieldName())) {
					technicalFields.remove(i);
					return;
				}
			}
		}
	}

	class TechnicalFieldTableLabelProvider extends LabelProvider implements ITableLabelProvider {

		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		@Override
		public String getColumnText(Object element, int columnIndex) {
			if (element instanceof TechnicalFieldContainer) {
				TechnicalFieldContainer tfc = (TechnicalFieldContainer) element;
				switch (columnIndex) {
				case 0: // name
					return tfc.techField.getFieldName();
				case 1: // description
					return tfc.techField.getFieldDescription();
				case 2: // type
					TechnicalField tf = tfc.techField;
					if (tf instanceof TechnicalFieldVarchar) {
						return TYPE_VARCHAR;
					}
					return TYPE_INTEGEGER;
				case 3: // length
					return Integer.toString(tfc.techField.getDataType().getLength());
				case 4: // key
					return Boolean.toString(tfc.techField.isKey());
				case 5: // nullable
					return Boolean.toString(tfc.techField.isNullable());
				default:
					return null;
				}
			}
			return null;
		}

	}

	abstract class BaseEditingSupport extends EditingSupport {
		protected CellEditor editor;

		public BaseEditingSupport(ColumnViewer viewer) {
			super(viewer);
		}

		@Override
		protected boolean canEdit(Object element) {
			TechnicalFieldContainer tfc = (TechnicalFieldContainer) element;
			return !tfc.readOnly;
		}

		@Override
		protected CellEditor getCellEditor(Object element) {
			return editor;
		}

		@Override
		protected void setValue(Object element, Object value) {
			setNewValue(element, value);
			getViewer().update(element, null);
			saveTable();
		}
		abstract protected void setNewValue(Object element, Object value);
		
	} // end of abstract class BaseEditingSupport()

	
	class DataTypeEditingSupport extends BaseEditingSupport {

		public DataTypeEditingSupport(ColumnViewer viewer) {
			super(viewer);

			if (viewer instanceof TableViewer) {
				editor = new ComboBoxCellEditor(((TableViewer) viewer).getTable(), new String[] { TYPE_VARCHAR, TYPE_INTEGEGER });
				editor.addListener(ceListener);
			} else {
				editor = null;
			}
		}

		@Override
		protected Object getValue(Object element) {
			TechnicalFieldContainer tfc = (TechnicalFieldContainer) element;
			TechnicalField tf = tfc.techField;
			if (tf instanceof TechnicalFieldVarchar) {
				return 0;
			}
			return 1;
		}

		@Override
		protected void setNewValue(Object element, Object value) {
			TechnicalFieldContainer tfc = (TechnicalFieldContainer) element;
			TechnicalField          tf  = tfc.techField;
			if (((Integer) value) == 0) {
				if (!(tf instanceof TechnicalFieldVarchar)) {
					tf = new TechnicalFieldVarchar(tf.getFieldName(), tf.getFieldDescription(), tf.getDataType().getLength(), tf.isKey(), tf.isNullable());
				}
			} else if (((Integer) value) == 1) {
				if (!(tf instanceof TechnicalFieldInteger)) {
					tf = new TechnicalFieldInteger(tf.getFieldName(), tf.getFieldDescription(), tf.isKey(), tf.isNullable());
				}
			}
			tfc.techField = tf;
		}

	}

	class LengthEditingSupport extends BaseEditingSupport {

		public LengthEditingSupport(ColumnViewer viewer) {
			super(viewer);
			editor = new TextCellEditor(((TableViewer) getViewer()).getTable());
			editor.addListener(ceListener);
		}

		@Override
		protected boolean canEdit(Object element) {
			TechnicalFieldContainer tfc = (TechnicalFieldContainer) element;
			if (tfc.readOnly) {
				return false;
			}
			TechnicalField tf = tfc.techField;
			return tf instanceof TechnicalFieldVarchar;
		}

		@Override
		protected Object getValue(Object element) {
			TechnicalFieldContainer tfc = (TechnicalFieldContainer) element;
			TechnicalField tf = tfc.techField;
			if (tf instanceof TechnicalFieldInteger) {
				return ""; //$NON-NLS-1$
			}
			return Integer.toString(tf.getDataType().getLength());
		}

		@Override
		protected void setNewValue(Object element, Object value) {
			TechnicalFieldContainer tfc = (TechnicalFieldContainer) element;
			TechnicalField tf = tfc.techField;
			if (tf instanceof TechnicalFieldInteger) {
				return;
			}
			int len = 0;
			try {
				len = Integer.valueOf((String) value);
			} catch (NumberFormatException nfe) {
				// do nothing
			}
			tf.getDataType().setLength(len);
		}

	}

	class NameEditingSupport extends BaseEditingSupport {

		public NameEditingSupport(ColumnViewer viewer) {
			super(viewer);
			editor = new TextCellEditor(((TableViewer) getViewer()).getTable());
			editor.addListener(ceListener);
		}

		@Override
		protected Object getValue(Object element) {
			TechnicalFieldContainer tfc = (TechnicalFieldContainer) element;
			TechnicalField tf = tfc.techField;
			return tf.getFieldName();
		}

		@Override
		protected void setNewValue(Object element, Object value) {
			TechnicalFieldContainer tfc = (TechnicalFieldContainer) element;
			TechnicalField tf = tfc.techField;
			tf.setFieldName((String) value);
			
			updateEnablement();
		}

	}

	class DescriptionEditingSupport extends BaseEditingSupport {

		public DescriptionEditingSupport(ColumnViewer viewer) {
			super(viewer);
			editor = new TextCellEditor(((TableViewer) getViewer()).getTable());
			editor.addListener(ceListener);
		}

		@Override
		protected Object getValue(Object element) {
			TechnicalFieldContainer tfc = (TechnicalFieldContainer) element;
			TechnicalField tf = tfc.techField;
			return tf.getFieldDescription();
		}

		@Override
		protected void setNewValue(Object element, Object value) {
			TechnicalFieldContainer tfc = (TechnicalFieldContainer) element;
			TechnicalField          tf  = tfc.techField;
			tf.setFieldDescription((String) value);
		}

	}

	class KeyEditingSupport extends BaseEditingSupport {
		public KeyEditingSupport(ColumnViewer viewer) {
			super(viewer);
		}

		@Override
		protected CellEditor getCellEditor(Object element) {
			CellEditor newEditor = new CheckboxCellEditor(((TableViewer) getViewer()).getTable());
			newEditor.addListener(ceListener);
			return newEditor;
		}

		@Override
		protected Object getValue(Object element) {
			TechnicalFieldContainer tfc = (TechnicalFieldContainer) element;
			TechnicalField tf = tfc.techField;
			return tf.isKey();
		}

		@Override
		protected void setNewValue(Object element, Object value) {
			TechnicalFieldContainer tfc = (TechnicalFieldContainer) element;
			TechnicalField tf = tfc.techField;
			tf.setIsKey(((Boolean) value));
		}

	}

	class NullableEditingSupport extends BaseEditingSupport {

		public NullableEditingSupport(ColumnViewer viewer) {
			super(viewer);
		}

		@Override
		protected CellEditor getCellEditor(Object element) {
			CellEditor newEditor = new CheckboxCellEditor(((TableViewer) getViewer()).getTable());
			newEditor.addListener(ceListener);
			return newEditor;
		}

		@Override
		protected Object getValue(Object element) {
			TechnicalFieldContainer tfc = (TechnicalFieldContainer) element;
			TechnicalField tf = tfc.techField;
			return tf.isNullable();
		}

		@Override
		protected void setNewValue(Object element, Object value) {
			TechnicalFieldContainer tfc = (TechnicalFieldContainer) element;
			TechnicalField tf = tfc.techField;
			tf.setNullable((Boolean) value);
		}
	}

	private Button                             configureTechnicalFieldsButton;
	private TableViewer                        techFieldsTableViewer;
	private TechnicalFieldTableContentProvider contentProvider;
	private Button                             addButton;
	private Button                             removeButton;
	private Button                             clearButton;
	private int                                numberOfPredefinedTechincalFields = 0;
	private List<TechnicalFieldContainer>      technicalFields;

	
	public TechnicalFieldsPage() {
		super(TABNAME, Messages.TechnicalFieldsWizardPage_1, Messages.TechnicalFieldsWizardPage_2,
		      Utils.getHelpID("rmconfeditor_techfields_sappack")); //$NON-NLS-1$
		this.technicalFields = new ArrayList<TechnicalFieldContainer>();
	}
	

	@Override
	public Image getImage() {
		return null; //ImageProvider.getImage(ImageProvider.IMAGE_TECHFIELDSPAGE);
	}

	public void createControls(IControlFactory controlFactory, final Composite parent) {

		Composite techFieldsGroup = controlFactory.createGroup(parent, Messages.TechnicalFieldsWizardPage_3, SWT.NONE);
		GridLayout gridLayout = new GridLayout(1, false);
		techFieldsGroup.setLayout(gridLayout);
		techFieldsGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		this.configureTechnicalFieldsButton = new Button(techFieldsGroup, SWT.CHECK);
		this.configureTechnicalFieldsButton.setText(Messages.TechnicalFieldsWizardPage_4);
		this.configureCheckboxForProperty(configureTechnicalFieldsButton, SETTING_TECH_FIELD_OPTION);
		this.configureTechnicalFieldsButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateEnablement();
			}
		});
		WidgetIDUtils.assignID(configureTechnicalFieldsButton, WidgetIDConstants.MOD_CONFIGURETECHNICALFIELDSBUTTON);

		// create table
		this.techFieldsTableViewer = new TableViewer(techFieldsGroup, SWT.FULL_SELECTION);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL);
		Table table = this.techFieldsTableViewer.getTable();
		table.setLayoutData(gridData);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		WidgetIDUtils.assignID(table, WidgetIDConstants.MOD_TECHNICALFIELDSTABLE);

		loadTable();

		TableViewerColumn nameColumn = new TableViewerColumn(this.techFieldsTableViewer, SWT.LEFT);
		nameColumn.getColumn().setText(Messages.TechnicalFieldsWizardPage_5);
		nameColumn.getColumn().setWidth(150);
		nameColumn.setEditingSupport(new NameEditingSupport(techFieldsTableViewer));

		TableViewerColumn descriptionColumn = new TableViewerColumn(this.techFieldsTableViewer, SWT.LEFT);
		descriptionColumn.getColumn().setText(Messages.TechnicalFieldsWizardPage_6);
		descriptionColumn.getColumn().setWidth(200);
		descriptionColumn.setEditingSupport(new DescriptionEditingSupport(this.techFieldsTableViewer));

		TableViewerColumn typeColumn = new TableViewerColumn(this.techFieldsTableViewer, SWT.LEFT);
		typeColumn.getColumn().setText(Messages.TechnicalFieldsWizardPage_7);
		typeColumn.getColumn().setWidth(80);
		typeColumn.setEditingSupport(new DataTypeEditingSupport(techFieldsTableViewer));

		TableViewerColumn lengthColumn = new TableViewerColumn(this.techFieldsTableViewer, SWT.LEFT);
		lengthColumn.getColumn().setText(Messages.TechnicalFieldsWizardPage_8);
		lengthColumn.getColumn().setWidth(70);
		lengthColumn.setEditingSupport(new LengthEditingSupport(techFieldsTableViewer));

		TableViewerColumn keyColumn = new TableViewerColumn(this.techFieldsTableViewer, SWT.LEFT);
		keyColumn.getColumn().setText(Messages.TechnicalFieldsWizardPage_9);
		keyColumn.getColumn().setWidth(60);
		keyColumn.setEditingSupport(new KeyEditingSupport(techFieldsTableViewer));

		TableViewerColumn nullableColumn = new TableViewerColumn(this.techFieldsTableViewer, SWT.LEFT);
		nullableColumn.getColumn().setText(Messages.TechnicalFieldsWizardPage_10);
		nullableColumn.getColumn().setWidth(60);
		nullableColumn.setEditingSupport(new NullableEditingSupport(techFieldsTableViewer));

		this.contentProvider = new TechnicalFieldTableContentProvider();

		this.techFieldsTableViewer.setContentProvider(this.contentProvider);
		this.techFieldsTableViewer.setLabelProvider(new TechnicalFieldTableLabelProvider());
		this.techFieldsTableViewer.setInput(this.contentProvider.getElements(null));

		createButtons(techFieldsGroup);

		updateEnablement();

		/*
		// if we are in replay mode we make all children read only
		if (((IMetadataImportWizard) getWizard()).isReplayMode()) {
			makeAllChildrenReadOnly(techFieldsGroup);
		}
		*/

		/*
		editor.getPropertiesMap().addObserver(new Observer() {

			@Override
			public void update(Observable observable, Object data) {
				if (CWTableImportOptionsPage.SETTING_ADD_TECH_FIELDS.equals(data) 
						|| CWTableImportOptionsPage.SETTING_TFLD_IS_NULLABLE.equals(data)
				|| CWTableImportOptionsPage.SETTING_PROFILE_IDX.equals(data)) {

					ObservableStringMap osm = (ObservableStringMap) observable;

					boolean createCWTechFields = osm.getBoolean(CWTableImportOptionsPage.SETTING_ADD_TECH_FIELDS);
					boolean cwTechFieldsNullable = osm.getBoolean(CWTableImportOptionsPage.SETTING_TFLD_IS_NULLABLE);

					List<TechnicalField> cwTechFields = Utils.getTechnicalFields(createCWTechFields, cwTechFieldsNullable);
					int fieldsToRemove = Math.min(numberOfPredefinedTechincalFields, technicalFields.size());
					for (int i = 0; i < fieldsToRemove; i++) {
						technicalFields.remove(0);
					}
					techFieldsTableViewer.refresh();
					int j = 0;
					for (TechnicalField tf : cwTechFields) {
						technicalFields.add(j, new TechnicalFieldContainer(tf, true));
						j++;
					}
					techFieldsTableViewer.refresh();
					numberOfPredefinedTechincalFields = j;
					// set the background of the predefined fields to gray
					Color gray = new Color(null, 211, 211, 211);
					for (int i = 0; i < numberOfPredefinedTechincalFields; i++) {
						techFieldsTableViewer.getTable().getItem(i).setBackground(gray);
					}
					techFieldsTableViewer.refresh();
					configureTechnicalFieldsButton.setSelection(!cwTechFields.isEmpty());
					updateEnablement();
					
					updateTechFields(osm);
				}
			}

		});
		//		String s = editor.getPropertiesMap().get(CWTableImportOptionsPage.SETTING_ADD_TECH_FIELDS);
		//		editor.getPropertiesMap().put(CWTableImportOptionsPage.SETTING_ADD_TECH_FIELDS, s);
		//		editor.getPropertiesMap().notifyObservers(); // CWTableImportOptionsPage.SETTING_ADD_TECH_FIELDS);
		updateTechFields(editor.getPropertiesMap());
		*/
	}

	@Override
	public void update(EditorEvent event) {
		super.update(event);
		if (event instanceof CWTechnicalFieldsChangedEvent) {
			CWTechnicalFieldsChangedEvent cwEvent = (CWTechnicalFieldsChangedEvent) event;

			boolean createCWTechFields = cwEvent.isCreateTechFields();
			boolean cwTechFieldsNullable = cwEvent.isTechFieldsNullable();

			List<TechnicalField> cwTechFields = RMConfiguration.getCWTechnicalFields(createCWTechFields, cwTechFieldsNullable);
			int fieldsToRemove = Math.min(numberOfPredefinedTechincalFields, technicalFields.size());

			for (int i = 0; i < fieldsToRemove; i++) {
				technicalFields.remove(0);
			}
			techFieldsTableViewer.refresh();
			int j = 0;
			for (TechnicalField tf : cwTechFields) {
				technicalFields.add(j, new TechnicalFieldContainer(tf, true));
				j++;
			}
			techFieldsTableViewer.refresh();
			numberOfPredefinedTechincalFields = j;
			// set the background of the predefined fields to gray
			for (int i = 0; i < numberOfPredefinedTechincalFields; i++) {
				techFieldsTableViewer.getTable().getItem(i).setBackground(Utils.COLOR_GRAY);
			}
			techFieldsTableViewer.refresh();
			configureTechnicalFieldsButton.setSelection(!cwTechFields.isEmpty());
			updateEnablement();
		}
	}

	static int nameSuffix = 0;

	private void createButtons(Composite composite) {
		Composite buttonsComposite = new Composite(composite, SWT.NONE);
		GridLayout buttonsLayout = new GridLayout(3, false);
		buttonsComposite.setLayout(buttonsLayout);

		this.addButton = new Button(buttonsComposite, SWT.NONE);
		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		gridData.widthHint = 80;
		this.addButton.setLayoutData(gridData);
		this.addButton.setText(Messages.TechnicalFieldsWizardPage_11);
		this.addButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				String newFieldName = getNextAvailableFieldName(Messages.TechnicalFieldsWizardPage_15);
				String desc = newFieldName;

				/*
				// get current 'isNullable' setting
				SharedWizardObjectsMap som = getSharedObjectsMap();
				ImporterOptionsBase options = (ImporterOptionsBase) som.get(SharedWizardObjectsMap.TABLE_IMPORT_OPTIONS);
				if (options == null) {
					options = (ImporterOptionsBase) som.get(SharedWizardObjectsMap.IDOC_IMPORT_OPTIONS);
				}
				*/
				boolean nullable = editor.getConfiguration().getBoolean(CWImportOptionsPage.SETTING_TFLD_IS_NULLABLE);

				TechnicalFieldVarchar newTechFldVarchar = new TechnicalFieldVarchar(newFieldName, desc, 10, false, nullable);
				technicalFields.add(new TechnicalFieldContainer(newTechFldVarchar));
				techFieldsTableViewer.refresh();
				updateEnablement();
				saveTable();
			}

		});
		WidgetIDUtils.assignID(addButton, WidgetIDConstants.MOD_ADDBUTTON);

		this.removeButton = new Button(buttonsComposite, SWT.NONE);
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		gridData.widthHint = 80;
		this.removeButton.setLayoutData(gridData);
		this.removeButton.setText(Messages.TechnicalFieldsWizardPage_12);
		this.removeButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				ISelection selection = techFieldsTableViewer.getSelection();
				if (selection instanceof IStructuredSelection) {
					IStructuredSelection structuredSelection = (IStructuredSelection) selection;
					boolean wasRemoved = false;
					Iterator<Object> iterator = structuredSelection.iterator();
					while (iterator.hasNext()) {
						TechnicalFieldContainer tfc = (TechnicalFieldContainer) iterator.next();
						if (!tfc.readOnly) {
							contentProvider.removeTechnicalField(tfc);
							wasRemoved = true;
						}
					}
					if (wasRemoved) {
						techFieldsTableViewer.refresh();
						Table table = techFieldsTableViewer.getTable();
						table.select(table.getItemCount() - 1);
					}
				}
				updateEnablement();
				saveTable();
			}

		});
		WidgetIDUtils.assignID(removeButton, WidgetIDConstants.MOD_REMOVEBUTTON);

		this.clearButton = new Button(buttonsComposite, SWT.NONE);
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		gridData.widthHint = 80;
		this.clearButton.setLayoutData(gridData);
		this.clearButton.setText(Messages.TechnicalFieldsWizardPage_13);
		this.clearButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				technicalFields.clear();
				numberOfPredefinedTechincalFields = 0;
				techFieldsTableViewer.refresh();
				saveTable();
			}
		});

	}

	private String getNextAvailableFieldName(String fieldNamePrefix) {
		String  nextFieldName    = null;
		boolean fldNameAvailable;

		do {
			nextFieldName    = fieldNamePrefix + (nameSuffix++);
			fldNameAvailable = true;

			// check if  'next' field name already exists
			for (TechnicalFieldContainer tfc : technicalFields) {
				if (tfc.toString().equals(nextFieldName)) {
					fldNameAvailable = false;;
				}
			}
		} while (!fldNameAvailable);
		
		return(nextFieldName);
	}
	
	void updateEnablement() {
		boolean enabled = this.configureTechnicalFieldsButton.getSelection();
		this.techFieldsTableViewer.getTable().setEnabled(enabled);
		this.addButton.setEnabled(enabled);
		this.removeButton.setEnabled(enabled);
		this.clearButton.setEnabled(enabled);
	}

	public static List<TechnicalField> getTechnicalFields(ConfigurationBase osm) {
		List<TechnicalField> technicalFields = new ArrayList<TechnicalField>();
		// load fields no matter if option is on or off
		int fieldNum = osm.getInt(SETTING_NUMBER_OF_TECH_FIELDS);
		
		for (int i = 0; i < fieldNum; i++) {
			String name = osm.get(SETTING_NAME + i);
			String desc = osm.get(SETTING_DESC + i);
			String type = osm.get(SETTING_TYPE + i);
			int len = osm.getInt(SETTING_LENGTH + i);
			boolean isKey = osm.getBoolean(SETTING_ISKEY + i);
			boolean isNullable = osm.getBoolean(SETTING_ISNULLABLE + i);
			TechnicalField tf = null;

			if (type == null || type.equals(TYPE_VARCHAR)) {
				tf = new TechnicalFieldVarchar(name, desc, len, isKey, isNullable);
			} else {
				tf = new TechnicalFieldInteger(name, desc, isKey, isNullable);
			}
			technicalFields.add(tf);
		}

		return technicalFields;
	}
	
	void loadTable() {
		ConfigurationBase osm = editor.getConfiguration();
		/*
		// load fields no matter if option is on or off
		int fieldNum = osm.getInt(SETTING_NUMBER_OF_TECH_FIELDS);
		for (int i = 0; i < fieldNum; i++) {
			String name = osm.get(SETTING_NAME + i);
			String desc = osm.get(SETTING_DESC + i);
			String type = osm.get(SETTING_TYPE + i);
			int len = osm.getInt(SETTING_LENGTH + i);
			boolean isKey = osm.getBoolean(SETTING_ISKEY + i);
			boolean isNullable = osm.getBoolean(SETTING_ISNULLABLE + i);
			TechnicalField tf = null;
			if (type.equals(TYPE_VARCHAR)) {
				tf = new TechnicalFieldVarchar(name, desc, len, isKey, isNullable);
			} else {
				tf = new TechnicalFieldInteger(name, desc, isKey, isNullable);
			}
			this.technicalFields.add(new TechnicalFieldContainer(tf));
		}
		*/
		List<TechnicalField> tfs = getTechnicalFields(osm);
		for (TechnicalField tf : tfs) {
			this.technicalFields.add(new TechnicalFieldContainer(tf));
		}
		numberOfPredefinedTechincalFields = 0;

		//this.techFieldsTableViewer.refresh();
	}

	public void saveTable() {
		ConfigurationBase osm = editor.getConfiguration();

		// remove old settings				
		int i = 0;
		osm.put(SETTING_NUMBER_OF_TECH_FIELDS, this.technicalFields.size() - numberOfPredefinedTechincalFields);
		for (int j = numberOfPredefinedTechincalFields; j < technicalFields.size(); j++) {
			TechnicalFieldContainer tfc = technicalFields.get(j);
			TechnicalField tf = tfc.techField;
			osm.put(SETTING_NAME + i, tf.getFieldName());
			osm.put(SETTING_DESC + i, tf.getFieldDescription());
			osm.put(SETTING_TYPE + i, (tf instanceof TechnicalFieldVarchar) ? TYPE_VARCHAR : TYPE_INTEGEGER);
			osm.put(SETTING_LENGTH + i, tf.getDataType().getLength());
			osm.put(SETTING_ISKEY + i, tf.isKey());
			osm.put(SETTING_ISNULLABLE + i, tf.isNullable());
			i++;
		}
	}

	/*
	void restoreWidgetValues() {
		try {
			IDialogSettings settings = getDialogSettings();
			if (settings != null) {
				String s = settings.get(SETTING_TECH_FIELD_OPTION);
				if (s != null) {
					boolean enabled = settings.getBoolean(SETTING_TECH_FIELD_OPTION);
					this.configureTechnicalFieldsButton.setSelection(enabled);
					// load fields no matter if option is on or off
					int fieldNum = settings.getInt(SETTING_NUMBER_OF_TECH_FIELDS);
					for (int i = 0; i < fieldNum; i++) {
						String name = settings.get(SETTING_NAME + i);
						String desc = settings.get(SETTING_DESC + i);
						String type = settings.get(SETTING_TYPE + i);
						int len = settings.getInt(SETTING_LENGTH + i);
						boolean isKey = settings.getBoolean(SETTING_ISKEY + i);
						boolean isNullable = settings.getBoolean(SETTING_ISNULLABLE + i);
						TechnicalField tf = null;
						if (type.equals(TYPE_VARCHAR)) {
							tf = new TechnicalFieldVarchar(name, desc, len, isKey, isNullable);
						} else {
							tf = new TechnicalFieldInteger(name, desc, isKey, isNullable);
						}
						this.technicalFields.add(new TechnicalFieldContainer(tf));
					}
					numberOfPredefinedTechincalFields = 0;
				}
			}
			this.techFieldsTableViewer.refresh();
		} catch (Exception e) {
			Activator.getLogger().log(Level.WARNING, Messages.LogExceptionMessage, e);
			e.printStackTrace();
		}
	}

	public void saveWidgetValues() {
		try {
			IDialogSettings settings = getDialogSettings();

			if (settings != null) {
				settings.put(SETTING_TECH_FIELD_OPTION, this.configureTechnicalFieldsButton.getSelection());
				// remove old settings				
				int i = 0;
				settings.put(SETTING_NUMBER_OF_TECH_FIELDS, this.technicalFields.size() - numberOfPredefinedTechincalFields);
				for (int j = numberOfPredefinedTechincalFields; j < technicalFields.size(); j++) {
					TechnicalFieldContainer tfc = technicalFields.get(j);
					TechnicalField tf = tfc.techField;
					settings.put(SETTING_NAME + i, tf.getFieldName());
					settings.put(SETTING_DESC + i, tf.getFieldDescription());
					settings.put(SETTING_TYPE + i, (tf instanceof TechnicalFieldVarchar) ? TYPE_VARCHAR : TYPE_INTEGEGER);
					settings.put(SETTING_LENGTH + i, tf.getDataType().getLength());
					settings.put(SETTING_ISKEY + i, tf.isKey());
					settings.put(SETTING_ISNULLABLE + i, tf.isNullable());
					i++;
				}

			}
		} catch (Exception e) {
			Activator.getLogger().log(Level.WARNING, Messages.LogExceptionMessage, e);
			e.printStackTrace();
		}
	}
	

	private List<TechnicalField> getTechnicalFields() {
		if (!this.configureTechnicalFieldsButton.getSelection()) {
			return null;
		}
		List<TechnicalField> result = new ArrayList<TechnicalField>();
		for (TechnicalFieldContainer tfc : this.technicalFields) {
			result.add(tfc.techField);
		}
		return result;
	}
*/
}
