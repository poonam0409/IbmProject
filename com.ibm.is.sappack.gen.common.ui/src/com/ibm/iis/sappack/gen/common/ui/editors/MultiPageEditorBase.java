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
// Module Name : com.ibm.iis.sappack.gen.common.ui.editors
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.iis.sappack.gen.common.ui.editors;


import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayDeque;
import java.util.Observable;
import java.util.Observer;
import java.util.Queue;
import java.util.logging.Level;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.ide.IGotoMarker;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.wst.validation.ValidationResult;
import org.eclipse.wst.validation.ValidatorMessage;

import com.ibm.iis.sappack.gen.common.ui.util.FileHelper;
import com.ibm.iis.sappack.gen.common.ui.util.Utils;
import com.ibm.iis.sappack.gen.common.ui.validators.ValidatorBase;
import com.ibm.is.sappack.gen.common.ui.Activator;
import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.common.ui.ModeManager;
import com.ibm.is.sappack.gen.common.ui.RMRGMode;


public abstract class MultiPageEditorBase extends MultiPageEditorPart implements IResourceChangeListener, IGotoMarker {
  	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	

	protected EditorPageBase[] editorPages;
	protected Image editorImage = null;
	
	private boolean dirty = false;
	private Queue<EditorEvent> events = new ArrayDeque<EditorEvent>();
	boolean allPagesCreated = false;
	private ConfigurationBase modelMap;
	private boolean isModeSensitive = false;
	
	static Image DECORATION_IMAGE_INFO = FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_INFORMATION).getImage();
	static Image DECORATION_IMAGE_WARNING = FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_WARNING).getImage();
	static Image DECORATION_IMAGE_ERROR = FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_ERROR).getImage();
	static Image DECORATION_IMAGE_REQUIRED = FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_REQUIRED).getImage();

	class DirtyObserver implements Observer {

		@Override
		public void update(Observable map, Object key) {
			setDirty(true);
			validate();
		}

	}

	protected MultiPageEditorBase(boolean isModeSensitive) {
		this.isModeSensitive = isModeSensitive;
	}

	protected abstract ConfigurationBase createConfiguration(IResource resource) throws IOException, CoreException;

    public ConfigurationBase getConfiguration() {
		return this.modelMap;
	}
    
    public boolean isModeSensitive() {
    	return this.isModeSensitive;
    }
	
	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		super.init(site, input);
		
		IFile f = this.getEditedFile();
		try {
			this.modelMap = createConfiguration(f);
		} catch (Exception e) {
			Activator.logException(e);
			throw new PartInitException(Messages.MultiPageEditorBase_0, e);
		} 
		String fileName = f.getName();
		setPartName(fileName);

		if (this.isModeSensitive) {
			RMRGMode thisMode = this.modelMap.getMode();
			RMRGMode activeMode = ModeManager.getActiveMode();
			
			if (thisMode == null) {
				throw new PartInitException(Messages.MultiPageEditorBase_2);
			}

			if (!thisMode.equals(activeMode)) {
				String uimsg = MessageFormat.format(Messages.MultiPageEditorBase_1,
				                                    new Object[] { thisMode.getName(), activeMode.getName() });
				boolean confirmed = MessageDialog.openConfirm(null, Messages.MultiPageEditorBase_2, uimsg);
				if (!confirmed) {
					String msg = MessageFormat.format(Messages.MultiPageEditorBase_3,
							new Object[] { thisMode.getName(), activeMode.getName() });
					throw new PartInitException(msg);
				}
			}
		}

		this.modelMap.addObserver(new DirtyObserver());

	}

	protected abstract EditorPageBase[] createPageList();

	public EditorPageBase[] getEditorPages() {
		return this.editorPages;
	}

	private Image getPageImage(EditorPageBase page) {
		if (this.editorImage != null) {
			return this.editorImage;
		}
		Image img = page.getImage();
		return img;
	}
	
	@Override
	protected void createPages() {
		this.editorPages = createPageList();
		for (EditorPageBase page : this.editorPages) {
			page.setEditor(this);

			Composite parent = getContainer();
			Composite composite = new Composite(parent, SWT.NONE);
			composite.setLayout(new FillLayout());
			FormToolkit toolkit = new FormToolkit(composite.getDisplay());
			ScrolledForm form = toolkit.createScrolledForm(composite);
			form.setText(page.getTitle());
			form.setImage(getPageImage(page));

			Composite formComp = form.getBody();
			PlatformUI.getWorkbench().getHelpSystem().setHelp(formComp, page.getHelpID());

			formComp.setLayout(new GridLayout(1, false));
			toolkit.createLabel(formComp, page.getDescription());

			GridData gd = new GridData();
			gd.horizontalAlignment = SWT.FILL;
			gd.grabExcessHorizontalSpace = true;
			//			gd.grabExcessVerticalSpace = true;
			formComp.setLayoutData(gd);
			Label line = toolkit.createLabel(formComp, null, SWT.SEPARATOR | SWT.HORIZONTAL);
			line.setLayoutData(gd);

			page.createControls(new FormToolkitControlFactory(toolkit), formComp);

			form.reflow(true);

			int i = this.addPage(composite);
			setPageText(i, page.getName());

		}
		allPagesCreated = true;

		this.doSendAllQueuedEvents();
		validate();
	}

	@Override
	public void resourceChanged(IResourceChangeEvent event) {
	}

	public IFile getEditedFile() {
		IEditorInput ei = this.getEditorInput();
		if (ei instanceof IFileEditorInput) {
			IFileEditorInput fei = (IFileEditorInput) ei;
			return fei.getFile();
		}
		throw new UnsupportedOperationException();
	}

	private void validate() {
		try {
			ConfigurationBase config = this.modelMap;
			ValidatorBase validator = config.createValidator();
			if (validator == null) {
				return;
			}
			ValidationResult valResult = validator.validate(config, null);
			if (valResult == null) {
				return;
			}

			for (EditorPageBase page : editorPages) {
				// first clear all validation decorations
				for (ControlDecoration dec : page.widgetToValidationDecorationsMap.values()) {
					dec.setDescriptionText(null);
					dec.hide();
				}
				ValidatorMessage[] vMessages = valResult.getMessages();
				for (ValidatorMessage m : vMessages) {
					String prop = (String) m.getAttribute(ValidatorBase.MARKER_ATTR_MODEL_PROPERTY);
					if (prop != null) {
						Widget w = page.propertyToWidgetMap.get(prop);
						if (w != null) {
							ControlDecoration dec = page.widgetToValidationDecorationsMap.get(w);
							if (dec != null) {
								dec.setDescriptionText((String) m.getAttribute(IMarker.MESSAGE));
								Integer sevO = (Integer) m.getAttribute(IMarker.SEVERITY);
								if (sevO != null) {
									int sev = sevO;
									switch (sev) {
									case IMarker.SEVERITY_ERROR:
										dec.setImage(MultiPageEditorBase.DECORATION_IMAGE_ERROR);
										break;
									case IMarker.SEVERITY_WARNING:
										dec.setImage(DECORATION_IMAGE_WARNING);
										break;
									case IMarker.SEVERITY_INFO:
										dec.setImage(MultiPageEditorBase.DECORATION_IMAGE_INFO);
										break;
									}

								}
								dec.show();
							}
						}
					}
				}
			}

			//			ValidationEvent valEvent = new ValidationEvent(valResult, null);
			//		sendEvent(valEvent);
		} catch (Exception exc) {
			handleException(exc);
		}
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		try {
			IFile f = getEditedFile();
			FileHelper.save(f, this.getConfiguration(), false, monitor);
			this.setDirty(false);

			validate();
		} catch (Exception exc) {
			handleSaveException(exc);
		}
	}

	void handleSaveException(Exception exc) {
		String msg = Messages.MultiPageEditorBase_4;
		Activator.getLogger().log(Level.SEVERE, msg, exc);
		MessageDialog.openError(null, Messages.MultiPageEditorBase_5, msg);
	}

	void handleLoadException(String fileName, Exception exc) {
		String msg = MessageFormat.format(Messages.MultiPageEditorBase_6, fileName);
		Activator.getLogger().log(Level.SEVERE, msg, exc);
		MessageDialog.openError(null, Messages.MultiPageEditorBase_7, msg);
	}

	protected void handleException(Exception exc) {
		Utils.showUnexpectedException(null, exc);
	}

	@Override
	public void doSaveAs() {
		String newFileName = "CopyOf" + this.getEditedFile().getName(); //$NON-NLS-1$
		SaveAsWizard saveAsWizard = new SaveAsWizard(newFileName, this.modelMap);
		WizardDialog wd = new WizardDialog(null, saveAsWizard);
		wd.open();
	}

	@Override
	public boolean isSaveAsAllowed() {
		return true;
	}

	public String getObjectName() {
		IFile f = getEditedFile();
		if (f != null) {
			String s = f.getName();
			return s;
		}
		return null;
	}

	public void setDirty() {
		setDirty(true);
	}

	public void setDirty(boolean dirty) {
		this.dirty = dirty;
		firePropertyChange(PROP_DIRTY);
	}

	@Override
	public boolean isDirty() {
		return this.dirty;
	}

	@Override
	public void setActivePage(int pageIndex) {
		super.setActivePage(pageIndex);
	}

	private void doSendEvent(EditorEvent event) {
		EditorPageBase source = event.getSourcePage();
		for (EditorPageBase page : this.editorPages) {
			if (source != page) {
				page.update(event);
			}
		}
	}

	private void doSendAllQueuedEvents() {
		EditorEvent e = null;
		while ((e = events.poll()) != null) {
			doSendEvent(e);
		}
	}

	public void sendEvent(EditorEvent event) {
		if (this.allPagesCreated) {
			doSendEvent(event);
		} else {
			events.add(event);
		}

	}

	@Override
	public void gotoMarker(IMarker marker) {
		String location;
		String property;
		try {
			property = (String) marker.getAttribute(ValidatorBase.MARKER_ATTR_MODEL_PROPERTY);
			location = (String) marker.getAttribute(IMarker.LOCATION);
		} catch (CoreException e) {
			Activator.logException(e);
			return;
		}
		EditorPageBase[] pages = this.getEditorPages();
		for (int i = 0; i < pages.length; i++) {
			if (pages[i].getName().equals(location)) {
				setActivePage(i);
				Widget w = pages[i].propertyToWidgetMap.get(property);
				if (w instanceof Control) {
					Control c = (Control) w;
					c.setFocus();
				}
				return;
			}
		}

	}

}
