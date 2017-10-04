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
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import com.ibm.iis.sappack.gen.common.ui.util.Utils;
import com.ibm.is.sappack.gen.common.ui.Activator;
import com.ibm.is.sappack.gen.common.ui.Messages;


public class ResourceSelectionWidget {
	private boolean mandatory;
	private String groupName;
	private String labelName;
	String predefinedValue;
	String[] fileExtensions;
	private Text text;
	private Button button;

  	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	

	/**
	 * When groupName == null, no new Composite object is created, the widgets are directly
	 * created within the parent. 
	 */
	public ResourceSelectionWidget(Composite parent, int style, String groupName, String labelName, String[] fileExtensions, boolean mandatory, String predefinedValue) {
//		Composite  super(parent, style);
		this.groupName = groupName;
		this.labelName = labelName;
		this.mandatory = mandatory;
		this.fileExtensions = fileExtensions;
		this.predefinedValue = predefinedValue;
		if (this.groupName != null) {
			Composite comp = new Composite(parent, style);
			createControl(comp, style);
		} else {
			createControl(parent, style);
		}
	}

	public ResourceSelectionWidget(Composite parent, int style, String groupName, String labelName, String fileExtension, boolean mandatory, String predefinedValue) {
		this(parent, style, groupName, labelName, new String[] { fileExtension }, mandatory, predefinedValue);
	}
	
	private void createControl(Composite parent, int style) {
		Composite comp = parent;
		if (this.groupName != null) {
			parent.setLayout(new GridLayout(1, false));
			parent.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
			Group group = new Group(parent, style);
			group.setText(groupName);

			group.setLayout(new GridLayout(3, false));
			group.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
			comp = group;
		}
		
		Label label = new Label(comp, SWT.NULL);
		label.setText(labelName + ":"); //$NON-NLS-1$

		text = new Text(comp, SWT.BORDER | SWT.SINGLE);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		text.setLayoutData(gd);
		if (this.predefinedValue != null) {
			this.text.setText(predefinedValue);
			this.text.setEnabled(false);
		}

		button = new Button(comp, SWT.PUSH);
		button.setText(Messages.ResourceSelectionWidget_0);
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleBrowse();
			}
		});

		//		loadEntry();
	}

	protected void handleBrowse() {
		Object[] os = browseForFiles(this.fileExtensions, Utils.getText(text));
		if (os != null && os.length > 0) {
			Object o = os[0];
		if (o instanceof IFile) {
			IFile f = (IFile) o;
			String s = f.getFullPath().toString();
			this.text.setText(s);
		}
		}
	}
	
	public static Object[] browseForFiles(final String[] fileExtensions, String preSelectedResource) {
		ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(null, new WorkbenchLabelProvider(), new BaseWorkbenchContentProvider());
		dialog.setTitle(Messages.ResourceSelectionWidget_1);
		dialog.setMessage(Messages.ResourceSelectionWidget_2);
		dialog.setInput(ResourcesPlugin.getWorkspace().getRoot());
		dialog.setAllowMultiple(false);

		IResource selectedResource = getResource(preSelectedResource);
		if (selectedResource != null && selectedResource.exists()) {
			dialog.setInitialSelection(selectedResource);
		}

		dialog.addFilter(new ViewerFilter() {

			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				if (element instanceof IFile) {
					IFile f = (IFile) element;
					boolean match = false;
					for (String fileExt : fileExtensions) {
						if (f.getName().endsWith(fileExt)) {
							match = true;
							break;
						}
					}
					return match;
				}
				return true;
			}
		});

		final String errorMsg = MessageFormat.format(Messages.ResourceSelectionWidget_3, getReadableFileExtensions(fileExtensions));
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
			return null;
		}

		Object[] selectedFiles = dialog.getResult();
		return selectedFiles;
	}
	
	static String getReadableFileExtensions(String[] fileExtensions) {
		StringBuffer buf = new StringBuffer();
		for (int i=0; i<fileExtensions.length; i++) {
			if (i > 0) {
				buf.append(","); //$NON-NLS-1$
			}
			buf.append(fileExtensions[i]);
		}
		return buf.toString();
	}

	public void addModifyListener(ModifyListener listener) {
		this.text.addModifyListener(listener);
	}

	public String validate() {
		String t = Utils.getText(this.text);
		if (t == null && mandatory) {
			return MessageFormat.format(Messages.ResourceSelectionWidget_4, this.labelName);
		}
		if (t != null) {
//			if (mandatory) {
				IFile f = getResource();
				if (f == null || !f.exists()) {
					return MessageFormat.format(Messages.ResourceSelectionWidget_5, t);
				}
//			}
		}
		return null;
	}

	public Text getText() {
		return this.text;
	}

	public IFile getResource() {
		String t = Utils.getText(text);
		return getResource(t);
	}
	
	public static IFile getResource(String t) {
		if (t == null) {
			return null;
		}
		IPath ip = new Path(t);
		try {
		IFile f = ResourcesPlugin.getWorkspace().getRoot().getFile(ip);
		return f;
		} catch(Exception exc) {
			Activator.logException(exc);
			return null;
		}
	}

	public void setEnabled(boolean b) {
		this.text.setEnabled(b);
		this.button.setEnabled(b);
	}
	
	public void setText(String text) {
		this.text.setText(text);
	}

}
