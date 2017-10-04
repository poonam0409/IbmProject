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
// Module Name : com.ibm.iis.sappack.gen.tools.sap.tablelist
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.iis.sappack.gen.tools.sap.tablelist;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.ibm.db.models.logical.Entity;
import com.ibm.iis.sappack.gen.common.ui.editors.ConfigurationBase;
import com.ibm.iis.sappack.gen.common.ui.editors.EditorPageBase;
import com.ibm.iis.sappack.gen.common.ui.editors.GeneralPageBase;
import com.ibm.iis.sappack.gen.common.ui.editors.IControlFactory;
import com.ibm.iis.sappack.gen.common.ui.util.ImageProvider;
import com.ibm.iis.sappack.gen.common.ui.util.Utils;
import com.ibm.iis.sappack.gen.common.ui.wizards.NextActionWizardDialog;
import com.ibm.iis.sappack.gen.common.ui.wizards.ResourceSelectionWidget;
import com.ibm.is.sappack.gen.common.Constants;
import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.common.ui.ModeManager;
import com.ibm.is.sappack.gen.common.ui.util.Utilities;
import com.ibm.is.sappack.gen.tools.sap.activator.Activator;
import com.ibm.is.sappack.gen.tools.sap.importer.helper.model.LdmAccessor;


public class TablesPage extends EditorPageBase {

	public static final String KEY_TABLES               = "KEY_TABLES";  //$NON-NLS-1$
	public static final String KEY_BUSINESS_OBJECT_NAME = "KEY_BUSINESS_OBJECT_NAME";  //$NON-NLS-1$
	public static final String HELP_ID                  = "table_list_editor";  //$NON-NLS-1$
	public static final String PAGE_NAME                = Messages.TablesPage_0;


	private Text    textBusinessObjectName;
	private Text    tableText;
	private boolean isJCOConfigured;
	

  	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}
  	

	public TablesPage() {
		super(PAGE_NAME, Messages.TablesPage_1, Messages.TablesPage_2, Utils.getHelpID(HELP_ID));

		// check if JCO is configured properly
		this.isJCOConfigured = Utilities.checkJCoAvailabilityWithDialog();
	}

	@Override
	public Image getImage() {
		return ImageProvider.getImage(ImageProvider.IMAGE_ABAP_TABLES_16);
	}

	public static List<String> getTables(ConfigurationBase map ) {
		String s = map.get(KEY_TABLES);
		if (s == null) {
			return new ArrayList<String>();
		}
		s = s.trim();
		return Utils.getTableListFromTextField(s);
	}
	
	public static String getBusinessObjectName(ConfigurationBase map ) {
		String businessObjectName = map.get(KEY_BUSINESS_OBJECT_NAME);
		if (businessObjectName == null || businessObjectName.isEmpty()) {
			return "unknown";
		}		
		return businessObjectName.trim();
	}
	
	
	
	
	public String getBusinessObject() {
		return this.textBusinessObjectName.getText().trim();
	}

	public List<String> getTables() {
		String s = this.tableText.getText().trim();
		return Utils.getTableListFromTextField(s);
	}
	
	void setBusinessObjectName(String businessObjectName) {
		this.textBusinessObjectName.setText(businessObjectName);
	}

	
	void setTables(Collection<String> tables, boolean merge) {
		List<String> newTableCollection = null;
		if (merge) {
			Collection<String> newTables = new TreeSet<String>(tables);
			
			newTableCollection = new ArrayList<String>(getTables());
			
			newTables.removeAll(newTableCollection);
/*			
			for (int i=0; i<newTableCollection.size(); i++) {
				String t = newTableCollection.get(i);
				if (newTables.contains(t)) {
					newTables.remove(t);
				}
			}
			*/
			newTableCollection.addAll(newTables);
		} else {
			newTableCollection = new ArrayList<String>(tables);
		}
		String s = Utils.getTableTextFieldFromList(newTableCollection);
		this.tableText.setText(s);
	}

	@Override
	public void createControls(IControlFactory controlFactory, Composite parent) {
		Composite comp = controlFactory.createComposite(parent, SWT.NONE);

		GridLayout gl = new GridLayout(2, true);
		comp.setLayout(gl);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		comp.setLayoutData(gd);

		
		// added by snelke
		Label labelBusinessObjectName = controlFactory.createLabel(comp, SWT.NONE);
		labelBusinessObjectName.setText(Messages.TablesPage_16);
		
		
		// empty label
		controlFactory.createLabel(comp, SWT.NONE);
		
		this.textBusinessObjectName = controlFactory.createText(comp, SWT.BORDER);
		this.textBusinessObjectName.setText("unknown");
		this.textBusinessObjectName.setLayoutData(new GridData(SWT.FILL, SWT.NONE, false, false));
		configureTextForProperty(textBusinessObjectName, KEY_BUSINESS_OBJECT_NAME);

		// empty label
		controlFactory.createLabel(comp, SWT.NONE);
		
		// added by snelke
		
		
		
		Label l = controlFactory.createLabel(comp, SWT.NONE);
		l.setText(Messages.TablesPage_4);

		// empty label
		controlFactory.createLabel(comp, SWT.NONE);

		tableText = controlFactory.createText(comp, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
		tableText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		configureTextForProperty(tableText, KEY_TABLES);

		Composite buttonComp = controlFactory.createComposite(comp, SWT.NONE);
		buttonComp.setLayout(new GridLayout(1, true));
		buttonComp.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true));

		Button browseButton = controlFactory.createButton(buttonComp, SWT.NONE);
		browseButton.setText(Messages.TablesPage_5);
		//		GridData buttonGD = new GridData(SWT.FILL, SWT.TOP, true, true);
		//	b.setLayoutData(buttonGD);
		browseButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				TableBrowserWizard tableBrowserWizard = new TableBrowserWizard();
				WizardDialog dialog = new NextActionWizardDialog(null, tableBrowserWizard);
				int result = dialog.open();
				if (result == WizardDialog.OK) {
					Set<String> tables = tableBrowserWizard.getSelectedTables();
					mergeOrOverwriteTables(tables);
				}
			}

		});
		Button importButton = controlFactory.createButton(buttonComp, SWT.NONE);
		importButton.setText(Messages.TablesPage_6);
		importButton.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				Collection<String> ldmTables = new HashSet<String>();
				Object[] files = ResourceSelectionWidget.browseForFiles(new String[] {"ldm"}, null); //$NON-NLS-1$
				if (files != null && files.length > 0) {
					Object o = files[0];
					if (o instanceof IFile) {
						LdmAccessor acc;
						try {
							acc = new LdmAccessor((IFile) o, null);
						} catch (IOException e1) {
							e1.printStackTrace();
							Activator.logException(e1);
							return;
						}
						List<Entity> allEntities = acc.getAllEntities();
						for (Entity ent : allEntities) {
							String sapTableName = LdmAccessor.getAnnotationValue(ent, Constants.ANNOT_SAP_TABLE_NAME);
							if (sapTableName != null) {
								String purpose = LdmAccessor.getAnnotationValue(ent, Constants.ANNOT_DATA_OBJECT_SOURCE);
								// add the old SapLogicalTable annotation to support Beta models
								if (Constants.DATA_OBJECT_SOURCE_TYPE_LOGICAL_TABLE.equals(purpose) || "SapLogicalTable".equals(purpose)) { //$NON-NLS-1$
									ldmTables.add(sapTableName);
								}
							}
						}
					}
					mergeOrOverwriteTables(ldmTables);
				}
			}
		});
	
		Button showCheckTablesButton = controlFactory.createButton(buttonComp, SWT.NONE);
		showCheckTablesButton.setText(Messages.TablesPage_8);
		showCheckTablesButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				ITableList tl = new ITableList() {
					
					@Override
					public Collection<String> getTables() {
						return TablesPage.this.getTables();
					}
					
					@Override
					public String getID() {
						return "dummy"; //$NON-NLS-1$
					}
					
					@Override
					public String getBusinessObjectName() {
						return "unknown"; //$NON-NLS-1$
					}

				};
				CheckTableBrowseWizard wizard = new CheckTableBrowseWizard( tl );
				WizardDialog dialog = new NextActionWizardDialog(null, wizard);
				dialog.open();
			}
			
		});
		
		if (ModeManager.isCWEnabled()) {
			Button importFromBDRButton = controlFactory.createButton(buttonComp, SWT.NONE);
			importFromBDRButton.setText(Messages.TablesPage_3);
			importFromBDRButton.setToolTipText(Messages.TablesPage_7);
			importFromBDRButton.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					BrowseBDRForTablesWizard bdrWiz = new BrowseBDRForTablesWizard();
					WizardDialog dialog = new NextActionWizardDialog(null, bdrWiz);
					int result = dialog.open();
					if (result == org.eclipse.jface.dialogs.Dialog.OK) {
						setBusinessObjectName(bdrWiz.getSelectedBusinessObjectName());
						List<String> selectedTables = bdrWiz.getSelectedTables();
						if (mergeOrOverwriteTables(selectedTables)) {
							addDescriptionToDocumentation(bdrWiz.getRloutDescription());
						}
					}
				}
				
			});
		}

		// enable/disable the IDocText field and the Browse button
		tableText.setEnabled(this.isJCOConfigured);
		browseButton.setEnabled(this.isJCOConfigured);
		
	}

	
	protected void addDescriptionToDocumentation(String description) {
		boolean append = MessageDialog.openQuestion(null, Messages.TablesPage_14, Messages.TablesPage_15);
		if (append) {
			this.editor.sendEvent(new GeneralPageBase.DocumentationAppendEvent(this, description));
		}
	}

	private boolean mergeOrOverwriteTables(Collection<String> tables) {
		boolean result = false;
		if (tables != null) {
			int ix = 0;
			if (!tables.isEmpty() && !getTables().isEmpty()) {
				MessageDialog md = new MessageDialog(null, Messages.TablesPage_9, null, Messages.TablesPage_10, MessageDialog.QUESTION, new String[] { Messages.TablesPage_11, Messages.TablesPage_12, Messages.TablesPage_13 }, 0);
				ix = md.open();
			}
			if (ix == 0) {
				// overwrite
				setTables(tables, false);
				result = true;
			} else if (ix == 1) {
				// merge
				setTables(tables, true);
				result = true;
			} else {
				// cancel
			}
		}
		return result;
	}

}
