package com.ibm.iis.sappack.gen.tools.jobgenerator.views.navigator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import com.ibm.iis.sappack.gen.common.ui.util.ExtensionFileFilter;
import com.ibm.iis.sappack.gen.tools.jobgenerator.rgconf.RGConfiguration;
import com.ibm.iis.sappack.gen.tools.sap.views.navigator.NavigatorRMContentProvider;
import com.ibm.iis.sappack.gen.tools.sap.views.navigator.ProjectCategory;
import com.ibm.is.sappack.gen.common.ui.Messages;

public class NavigatorRGContentProvider implements IStructuredContentProvider, ITreeContentProvider {
  	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	

	static final String RGCAT_NAME = Messages.NavigatorRGContentProvider_0;
	static final String ABAP_ARCHIVES = Messages.NavigatorRGContentProvider_1;

	static final String ABAP_ARCHIVE_EXTENSION = "zip"; //$NON-NLS-1$

	ProjectCategory createRGConfCategory(IProject project) {
		ProjectCategory rgConf = new ProjectCategory(project, PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FOLDER), new ExtensionFileFilter(
				RGConfiguration.RGCONF_FILE_EXTENSION), RGCAT_NAME);
		return rgConf;
	}

	ProjectCategory createABAPArchiveCategory(IProject project) {
		ProjectCategory rgConf = new ProjectCategory(project, PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FOLDER), new ExtensionFileFilter(ABAP_ARCHIVE_EXTENSION),
				ABAP_ARCHIVES);
		return rgConf;
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof IWorkspaceRoot) {
			return NavigatorRMContentProvider.getProjects((IWorkspaceRoot) parentElement).toArray();
		}
		if (parentElement instanceof IProject) {
			IProject project = (IProject) parentElement;
			return new Object[] { createRGConfCategory(project), createABAPArchiveCategory(project) };
		}
		if (parentElement instanceof ProjectCategory) {
			ProjectCategory cat = (ProjectCategory) parentElement;
			String name = cat.getName();
			if (name.equals(RGCAT_NAME) || name.equals(ABAP_ARCHIVES)) {
				return cat.getChildren().toArray();
			}
		}
		return new Object[0];
	}

	@Override
	public Object getParent(Object element) {
		if (element instanceof IFile) {
			IFile f = (IFile) element;
			if (f.getName().endsWith(RGConfiguration.RGCONF_FILE_EXTENSION)) {
				return createRGConfCategory(f.getProject());
			}
		} else if (element instanceof ProjectCategory) {
			ProjectCategory cat = (ProjectCategory) element;
			String name = cat.getName();
			if (name.equals(RGCAT_NAME) || name.equals(ABAP_ARCHIVES)) {
				return cat.getProject();
			}
		}
		return null;
	}

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	@Override
	public boolean hasChildren(Object element) {
		return getChildren(element).length > 0;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

}