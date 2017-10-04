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
// Module Name : com.ibm.iis.sappack.gen.common.ui.wizards
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.iis.sappack.gen.common.ui.wizards;


import java.text.MessageFormat;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import com.ibm.is.sappack.gen.common.ui.Activator;
import com.ibm.is.sappack.gen.common.ui.Messages;


public class SelectResourcePage extends PersistentWizardPageBase {
	private static final String SETTING_TEXT = ".setting_text"; //$NON-NLS-1$
	private String label;
	private String[] fileExtensions;
	protected Text text;
	
	protected String predefinedValue = null;


	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	


	public SelectResourcePage(String pageName, String title, String description, ImageDescriptor image, String label, String[] fileExtensions) {
		this(pageName, title, description, image, label, fileExtensions, null);
	}
	
	public SelectResourcePage(String pageName, String title, String description, ImageDescriptor image, String label, String[] fileExtensions, String predefinedValue) {
		super(pageName, title, image);
		setDescription(description);
		this.label = label;
		this.fileExtensions = fileExtensions;
		this.predefinedValue = predefinedValue;
	}

	@Override
	public Composite createControlImpl(Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
		GridLayout gl = new GridLayout(3, false);
		comp.setLayout(gl);
		GridData gd2 = new GridData(SWT.FILL, SWT.FILL, true, true);
		comp.setLayoutData(gd2);

		Label label = new Label(comp, SWT.NULL);
		label.setText(this.label);

		text = new Text(comp, SWT.BORDER | SWT.SINGLE);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		text.setLayoutData(gd);
		text.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}

		});
		if (this.predefinedValue != null) {
			this.text.setText(predefinedValue);
			this.text.setEnabled(false);
		} else {
			this.configureTextForProperty(text, this.getName() + SETTING_TEXT);			
		}

		Button button = new Button(comp, SWT.PUSH);
		button.setText(Messages.SelectResourcePage_0);
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleBrowse();
			}
		});

		//		loadEntry();
		dialogChanged();
		return comp;
		//		setControl(comp);
	}

	protected void handleBrowse() {
		ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(getShell(), new WorkbenchLabelProvider(), new BaseWorkbenchContentProvider());
		dialog.setTitle(this.getTitle());
		dialog.setMessage(this.getTitle());
		dialog.setInput(ResourcesPlugin.getWorkspace().getRoot());
		dialog.setAllowMultiple(false);

		IResource selectedResource = getResource();
		if (selectedResource != null && selectedResource.exists()) {
			dialog.setInitialSelection(selectedResource);
		}

		dialog.addFilter(new ViewerFilter() {

			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				if (element instanceof IFile) {
					IFile f = (IFile) element;
					for (String ext : fileExtensions) {
						if (f.getName().endsWith(ext)) {
							return true;
						} else {
							return false;
						}
					}
				}
				return true;
			}
		});

		String ext = fileExtensions[0];
		final String errorMsg = MessageFormat.format(Messages.SelectResourcePage_1, ext);
		dialog.setValidator(new ISelectionStatusValidator() {

			@Override
			public IStatus validate(Object[] selection) {
				if (selection.length > 0) {
					Object o = selection[0];
					if (o instanceof IFile) {
						return new Status(IStatus.OK, Activator.PLUGIN_ID, ""); //$NON-NLS-1$
					}
				}
				return new Status(IStatus.ERROR, Activator.PLUGIN_ID, errorMsg);
			}
		});

		int r = dialog.open();

		if (r == Window.CANCEL) {
			return;
		}

		Object[] selectedFiles = dialog.getResult();
		Object o = selectedFiles[0];
		if (o instanceof IFile) {
			IFile f = (IFile) o;
			String s = f.getFullPath().toString();
			this.text.setText(s);
		}
	}

	// returns true if error
	protected boolean dialogChanged() {
		setPageComplete(true);
		setErrorMessage(null);
		boolean error = false;
		String s = this.text.getText().trim();
		if (s.length() == 0) {
			setErrorMessage(Messages.SelectResourcePage_2);
			setPageComplete(false);
			error = true;
		} 
		
		IFile res = getResource();
		
		if (!error && (res == null || !res.exists()) ) {
			setErrorMessage(Messages.SelectResourcePage_3);
			setPageComplete(false);
			error = true;			
		} 
		
		return error;
	}

	public IFile getResource() {
		String file = this.text.getText().trim();
		if (file == null || file.isEmpty()) {
			return null;
		}
		IPath p = new Path(file);
		if (!p.isValidPath(file)) {
			return null;
		}

		IFile result = ResourcesPlugin.getWorkspace().getRoot().getFile(p);
		return result;
	}

	public void setResourceName(String name, boolean enabled) {
		if (name != null) {
			this.text.setText(name);
			this.text.setEnabled(enabled);
		}
	}
	
	@Override
	public boolean nextPressedImpl() {
		return true;
	}

}
