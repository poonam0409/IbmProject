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


import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

import com.ibm.iis.sappack.gen.common.ui.editors.IControlFactory;
import com.ibm.iis.sappack.gen.common.ui.util.Utils;
import com.ibm.is.sappack.gen.common.Constants;
import com.ibm.is.sappack.gen.common.DBSupport.DataBaseType;
import com.ibm.is.sappack.gen.common.DSTypeDBMMapping;
import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.common.ui.WidgetIDConstants;
import com.ibm.is.sappack.gen.common.ui.WidgetIDUtils;
import com.ibm.is.sappack.gen.tools.sap.utilities.Utilities;


public class CustomDerivationsPage extends RGConfPageBase {
	public static final String CUSTOM_DERIVATIONS_ENABLED = "USE_CUSTOM_DERIVATIONS"; //$NON-NLS-1$
	public static final String CUSTOM_DERIVATIONS_EXCEPTIONS_TEXT = "CUSTOM_DERIVATIONS_TEXT"; //$NON-NLS-1$
	public static final String DERIVATION_PFX     = "DERIVATION_MAPPING_"; //$NON-NLS-1$

	public static final String TABNAME            = Messages.CustomDerivationsPage_0;
	public static final String DEFAULT_DERIVATION = Constants.DERIVATION_REPLACEMENT;
	
	private Button useCustomDerivationsButton;
	private Text derivationExceptionsFile;
	private TableViewer tableViewer;
	private Button resetButton;
	private Text customDerivations;
	private List<DerivationMapping> derivationMappings;


  	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	
	

	public CustomDerivationsPage() {
		super(TABNAME, Messages.CustomDerivationsPage_1, Messages.CustomDerivationsPage_2,
		      Utils.getHelpID("rgconfeditor_derivations")); //$NON-NLS-1$
		this.derivationMappings = new ArrayList<CustomDerivationsPage.DerivationMapping>();
	}

	void initializeMappings() {
		this.derivationMappings.clear();
		Set<String> allDSTypeNames = DSTypeDBMMapping.getDataStageODBCTypeNames();
		List<String> allDSTypeNameList = new ArrayList<String>(allDSTypeNames);
		Collections.sort(allDSTypeNameList);
		for (String dsType : allDSTypeNameList) {
			this.derivationMappings.add(new DerivationMapping(dsType));
		}
	}

	class DerivationMapping {
		public DerivationMapping(String dsType) {
			this.dsType = dsType;
		}

		private String dsType;

		String getExpression() {
			String val = editor.getConfiguration().get(DERIVATION_PFX + dsType);
			if (val == null) {
				return DEFAULT_DERIVATION;
			}
			return val;
		}

		void setExpression(String exp) {
			editor.getConfiguration().put(DERIVATION_PFX + dsType, exp);
		}
	}

	class ExpressionEditingSupport extends EditingSupport {
		private CellEditor cellEditor;

		public ExpressionEditingSupport(TableViewer tableViewer) {
			super(tableViewer);
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
			if (element instanceof DerivationMapping) {
				return ((DerivationMapping) element).getExpression();
			}
			return null;
		}

		@Override
		protected void setValue(Object element, Object value) {
			if (element instanceof DerivationMapping) {
				DerivationMapping mapping = (DerivationMapping) element;
				mapping.setExpression(String.valueOf(value));
				getViewer().update(mapping, null);
			}
		}
	}

	class DerivationsContentProvider implements IStructuredContentProvider {

		@Override
		public void dispose() {
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

		@Override
		public Object[] getElements(Object inputElement) {
			return derivationMappings.toArray();
		}

	}

	class DerivationsLabelProvider implements ITableLabelProvider {

		@Override
		public void addListener(ILabelProviderListener listener) {
		}

		@Override
		public void dispose() {
		}

		@Override
		public boolean isLabelProperty(Object element, String property) {
			return false;
		}

		@Override
		public void removeListener(ILabelProviderListener listener) {

		}

		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		@Override
		public String getColumnText(Object element, int columnIndex) {
			if (element instanceof DerivationMapping) {
				DerivationMapping mapping = (DerivationMapping) element;
				
				switch (columnIndex) {
				case 0: // DataStage type
					return mapping.dsType;
				case 1: // derivation expression
					return mapping.getExpression();
				case 2: // DB2 type
					return getMappedDBMTypes(DataBaseType.DB2, mapping);
				case 3: // Oracle type
					return getMappedDBMTypes(DataBaseType.Oracle, mapping);
				case 4: // Netezza type
					return getMappedDBMTypes(DataBaseType.Netezza, mapping);
				default:
					return null;
				}

			}
			return null;
		}

	}

	private String getMappedDBMTypes(DataBaseType dbType, DerivationMapping derivMapping) {
		Map<String, Set<String>> typeMapping = DSTypeDBMMapping.getInvertedDBMMapping(dbType);
		Set<String>              dbmTypes    = typeMapping.get(derivMapping.dsType);

		if (dbmTypes == null) {
			return ""; //$NON-NLS-1$
		}
		StringBuffer buf = new StringBuffer();
		for (Object o : dbmTypes) {
			String dbmType = (String) o;
			if (dbmType != null) {
				if (buf.length() > 0) {
					buf.append(", "); //$NON-NLS-1$
				}
				buf.append(dbmType);
			}
		}
		return buf.toString();
	}

	@Override
	public void createControls(IControlFactory controlFactory, Composite parent1) {
		Composite parent = controlFactory.createComposite(parent1, SWT.NONE);
		parent.setLayout(new GridLayout(1, false));
		parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Composite customDerivationsGroup = controlFactory.createGroup(parent, Messages.JobGeneratorCustomDerivationsPage_18, SWT.NONE);
		GridLayout gridLayout = new GridLayout(1, false);
		customDerivationsGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		customDerivationsGroup.setLayout(gridLayout);

		this.useCustomDerivationsButton = controlFactory.createButton(customDerivationsGroup, SWT.CHECK);
		this.useCustomDerivationsButton.setText(Messages.JobGeneratorCustomDerivationsPage_19);
		this.configureCheckboxForProperty(useCustomDerivationsButton, CUSTOM_DERIVATIONS_ENABLED);
		SelectionAdapter useCustomDerivAdapter = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean enabled = useCustomDerivationsButton.getSelection();
				tableViewer.getTable().setEnabled(enabled);
				resetButton.setEnabled(enabled);
				customDerivations.setEnabled(enabled);
			}
		};
		this.useCustomDerivationsButton.addSelectionListener(useCustomDerivAdapter);
		WidgetIDUtils.assignID(useCustomDerivationsButton, WidgetIDConstants.GEN_USECUSTOMDERIVATIONSBUTTON);

		createTable(controlFactory, customDerivationsGroup);
		createButtons(controlFactory, customDerivationsGroup);

		Label l = controlFactory.createLabel(customDerivationsGroup, SWT.NONE);
		l.setText(Messages.CustomDerivationsPage_3);
		customDerivations = controlFactory.createText(customDerivationsGroup, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL | SWT.H_SCROLL);
		GridData textGD = new GridData(SWT.DEFAULT, 100);
		textGD.horizontalAlignment = SWT.FILL;
		textGD.verticalAlignment = SWT.FILL;
		textGD.grabExcessHorizontalSpace = true;
		textGD.grabExcessVerticalSpace = true;
		customDerivations.setLayoutData(textGD);
		this.configureTextForProperty(customDerivations, CUSTOM_DERIVATIONS_EXCEPTIONS_TEXT);

		useCustomDerivAdapter.widgetSelected(null);
	}

	public Map<String, String> getDerivationExceptions() throws IOException {
		String text = Utilities.getTextFieldValue(this.derivationExceptionsFile);
		if (text == null) {
			return new HashMap<String, String>();
		}
		return getDerivationExceptions(text);
	}

	public static Map<String, String> getDerivationExceptions(String text) throws IOException {
		Map<String, String> result = new HashMap<String, String>();
		StringReader sr = new StringReader(text);
		LineNumberReader lnr = new LineNumberReader(sr);
		String line = null;
		while ((line = lnr.readLine()) != null) {
			line = line.trim();
			if (!line.isEmpty()) {
				int ix = line.indexOf('=');
				if (ix == -1) {
					int lineNum = lnr.getLineNumber();
					String msg = MessageFormat.format(Messages.JobGeneratorCustomDerivationsPage_4, lineNum);
					throw new IOException(msg);
				}
				String column = line.substring(0, ix);
				String derivValue = line.substring(ix + 1);
//				derivValue = derivValue.replaceAll("\\" + Constants.DERIVATION_REPLACEMENT, "{0}"); //$NON-NLS-1$ //$NON-NLS-2$
				derivValue = derivValue.trim();
				result.put(column, derivValue);
			}
		}

		return result;
	}

	boolean checkExceptionsFile() {
		return true;
	}

	private void createTable(IControlFactory controlFactory, Composite composite) {
		this.tableViewer = new TableViewer(composite, SWT.FULL_SELECTION);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL);
		Table table = this.tableViewer.getTable();
		table.setLayoutData(gridData);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		WidgetIDUtils.assignID(table, WidgetIDConstants.GEN_CUSTOMDERIVATIONSTABLE);

		// table column: DS Data Type
		// --------------------------
		TableViewerColumn dsDataTypeColumn = new TableViewerColumn(this.tableViewer, SWT.LEFT);
		dsDataTypeColumn.getColumn().setText(Messages.JobGeneratorCustomDerivationsPage_20);
		dsDataTypeColumn.getColumn().setWidth(150);

		// table column: Type Derivation
		// -----------------------------
		TableViewerColumn derivationColumn = new TableViewerColumn(this.tableViewer, SWT.LEFT);
		derivationColumn.getColumn().setText(Messages.JobGeneratorCustomDerivationsPage_22);
		derivationColumn.getColumn().setWidth(250);
		derivationColumn.setEditingSupport(new ExpressionEditingSupport(tableViewer));

		// table column: DB2 Types
		// -----------------------
		TableViewerColumn db2TypeColumn = new TableViewerColumn(this.tableViewer, SWT.LEFT | SWT.MULTI);
		db2TypeColumn.getColumn().setText(Messages.CustomDerivationsPage_4);
		db2TypeColumn.getColumn().setWidth(300);

		// table column: Oracle Types
		// -----------------------
		TableViewerColumn oraTypeColumn = new TableViewerColumn(this.tableViewer, SWT.LEFT | SWT.MULTI);
		oraTypeColumn.getColumn().setText(Messages.CustomDerivationsPage_5);
		oraTypeColumn.getColumn().setWidth(300);

		initializeMappings();
		this.tableViewer.setContentProvider(new DerivationsContentProvider());
		this.tableViewer.setLabelProvider(new DerivationsLabelProvider());
		this.tableViewer.setInput(this.derivationMappings);
	}

	void createButtons(IControlFactory controlFactory, Composite composite) {
		Composite buttonsComposite = controlFactory.createComposite(composite, SWT.NONE);
		GridLayout buttonsLayout = new GridLayout(1, false);
		buttonsComposite.setLayout(buttonsLayout);

		resetButton = controlFactory.createButton(buttonsComposite, SWT.NONE);
		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		//	gridData.widthHint = 120;
		resetButton.setLayoutData(gridData);
		resetButton.setText(Messages.JobGeneratorCustomDerivationsPage_23);
		resetButton.setToolTipText(Messages.JobGeneratorCustomDerivationsPage_23);
		resetButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				initializeMappings();
				for (DerivationMapping mapping : derivationMappings) {
					mapping.setExpression(DEFAULT_DERIVATION);
				}
				tableViewer.refresh();
			}

		});

	}

}
