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
// Module Name : com.ibm.iis.sappack.gen.tools.sap.views.navigator
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.iis.sappack.gen.tools.sap.views.navigator;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import com.ibm.iis.sappack.gen.common.ui.util.ExtensionFileFilter;
import com.ibm.iis.sappack.gen.common.ui.util.IFileFilter;
import com.ibm.iis.sappack.gen.common.ui.util.ImageProvider;
import com.ibm.iis.sappack.gen.tools.sap.idocseglist.IDocSegmentList;
import com.ibm.iis.sappack.gen.tools.sap.importwizard.MetaDataImportWizard;
import com.ibm.iis.sappack.gen.tools.sap.rmconf.RMConfiguration;
import com.ibm.iis.sappack.gen.tools.sap.tablelist.TableList;
import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.tools.sap.activator.Activator;


public class NavigatorRMContentProvider implements IStructuredContentProvider, ITreeContentProvider {
  	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	

	public static List<IProject> getProjects(IWorkspaceRoot root) {
		List<IProject> children = new ArrayList<IProject>();
		for (IProject p : root.getProjects()) {
			if (p.isOpen()) {
				IProjectNature nature = null;
				try {
					nature = p.getNature("com.ibm.datatools.core.ui.DatabaseDesignNature"); //$NON-NLS-1$
				} catch (CoreException e) {
					Activator.logException(e);
				}
				if (nature != null) {
					children.add(p);
				}
			}
		}
		return children;
	}

	public static final String RM_CAT = Messages.NavigatorRMContentProvider_0;

	ProjectCategory createRMCategory(IProject project) {
		return new ProjectCategory(project, PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FOLDER), null, RM_CAT);
	}

	public static final String RMCFG_CAT = Messages.NavigatorRMContentProvider_1;

	ProjectCategory createRMConfCategory(IProject project) {
		return new ProjectCategory(project, PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FOLDER), new ExtensionFileFilter(RMConfiguration.RMCONF_FILE_EXTENSION),
				RMCFG_CAT);
	}

	public static final String MODELS_CAT = Messages.NavigatorRMContentProvider_2;

	ProjectCategory createDataModelsCategory(IProject project) {
		return new ProjectCategory(project, ImageProvider.getImage(ImageProvider.IMAGE_MODELS_FOLDER), null, MODELS_CAT);
	}

	public static final String LDM_CAT = Messages.NavigatorRMContentProvider_3;

	ProjectCategory createLDMCategory(IProject project) {
		IFileFilter ldmFilter = new ExtensionFileFilter("ldm"); //$NON-NLS-1$
		ProjectCategory ldm = new ProjectCategory(project, ImageProvider.getImage(ImageProvider.IMAGE_MODELS_FOLDER), ldmFilter, LDM_CAT);
		return ldm;

	}

	public static final String DBM_CAT = Messages.NavigatorRMContentProvider_4;

	ProjectCategory createDBMCategory(IProject project) {

		IFileFilter dbmFilter = new ExtensionFileFilter("dbm");  //$NON-NLS-1$
		ProjectCategory dbm = new ProjectCategory(project, ImageProvider.getImage(ImageProvider.IMAGE_MODELS_FOLDER), dbmFilter, DBM_CAT);
		return dbm;
	}

	public static final String SAP_OBJECT_LIST_CAT = Messages.NavigatorRMContentProvider_5;

	ProjectCategory createSAPObjectListsCategory(IProject project) {
		return new ProjectCategory(project, PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FOLDER), null, SAP_OBJECT_LIST_CAT);
	}

	public static final String TABLE_LIST_CAT = Messages.NavigatorRMContentProvider_6;

	ProjectCategory createTableListCategory(IProject project) {
		return new ProjectCategory(project, PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FOLDER), new ExtensionFileFilter(TableList.TABLE_LIST_FILE_EXTENSION),
				TABLE_LIST_CAT);
	}

	static final String IDOC_SEGMENT_LIST_CAT = Messages.NavigatorRMContentProvider_7;

	ProjectCategory createIDocSegmentListCategory(IProject project) {
		return new ProjectCategory(project, PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FOLDER), new ExtensionFileFilter(
				IDocSegmentList.IDOC_SEGMENT_LIST_FILE_EXTENSION), IDOC_SEGMENT_LIST_CAT);
	}

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	public static class NavTable extends NameWrapper {

		protected NavTable(Object parent, String name) {
			super(parent, name);
		}
	}

	public static class NavIDocSegment extends NameWrapper {

		protected NavIDocSegment(Object parent, String name) {
			super(parent, name);
		}
	}

	public static class NavIDocType extends NameWrapper {
		List<NavIDocSegment> segments;

		public NavIDocType(Object parent, String name, List<String> segmentNames) {
			super(parent, name);
			this.segments = new ArrayList<NavigatorRMContentProvider.NavIDocSegment>();
			for (String s : segmentNames) {
				segments.add(new NavIDocSegment(this, s));
			}
		}

		public List<NavIDocSegment> getSegments() {
			return this.segments;
		}

	}

	@Override
	public Object[] getChildren(Object parentElement) {
		List<Object> children = new ArrayList<Object>();
		if (parentElement instanceof IWorkspaceRoot) {
			children.addAll(getProjects((IWorkspaceRoot) parentElement));
		} else if (parentElement instanceof IFolder) {
			IContainer cont = (IContainer) parentElement;
			try {
				children.addAll(Arrays.asList(cont.members()));
			} catch (CoreException e) {
				Activator.logException(e);
			}
		} else if (parentElement instanceof IProject) {
			IProject project = (IProject) parentElement;

			children.add(createRMCategory(project));
			children.add(createDataModelsCategory(project));
		} else if (parentElement instanceof ProjectCategory) {
			ProjectCategory cat = (ProjectCategory) parentElement;
			String name = cat.getName();
			if (name.equals(RMCFG_CAT) || name.equals(TABLE_LIST_CAT) || name.equals(IDOC_SEGMENT_LIST_CAT) || name.equals(LDM_CAT) || name.equals(DBM_CAT)) {
				children.addAll(cat.getChildren());
			} else if (name.equals(SAP_OBJECT_LIST_CAT)) {
				children.add(createTableListCategory(cat.getProject()));
				children.add(createIDocSegmentListCategory(cat.getProject()));
			} else if (name.equals(MODELS_CAT)) {
				children.add(createLDMCategory(cat.getProject()));
				children.add(createDBMCategory(cat.getProject()));
			} else if (name.equals(RM_CAT)) {
				children.add(createSAPObjectListsCategory(cat.getProject()));
				children.add(createRMConfCategory(cat.getProject()));
			}
		} else if (parentElement instanceof IFile) {
			IFile f = (IFile) parentElement;
			if (f.getName().endsWith(TableList.TABLE_LIST_FILE_EXTENSION)) {
				TableList tl = null;
				try {
					tl = new TableList(f);
				} catch (Exception e) {
					Activator.logException(e);
				}
				if (tl != null) {
					for (String t : tl.getTables()) {
						children.add(new NavTable(f, t));
					}
				}
			} else if (f.getName().endsWith(IDocSegmentList.IDOC_SEGMENT_LIST_FILE_EXTENSION)) {
				IDocSegmentList isl = null;
				try {
					isl = new IDocSegmentList(f);
				} catch (Exception e) {
					Activator.logException(e);
				}
				if (isl != null) {
					String idocType = isl.getIDocType();
					if (idocType != null && !idocType.trim().isEmpty()) {
						children.add(new NavIDocType(f, idocType, isl.getIDocSegments()));
					}
				}

			} else if (f.getName().endsWith("ldm")) { //$NON-NLS-1$
				IFile logFile = MetaDataImportWizard.getLDMLogFile(f);
				if (logFile.exists()) {
					children.add(logFile);
				}
				
			}
		} else if (parentElement instanceof NavIDocType) {
			children.addAll(((NavIDocType) parentElement).getSegments());
		}

		return children.toArray();
	}

	@Override
	public Object getParent(Object element) {
		if (element instanceof IFile) {
			IFile f = (IFile) element;
			if (f.getName().endsWith(RMConfiguration.RMCONF_FILE_EXTENSION)) {
				return createRMConfCategory(f.getProject());
			}
			if (f.getName().endsWith(TableList.TABLE_LIST_FILE_EXTENSION)) {
				return createTableListCategory(f.getProject());
			}
			if (f.getName().endsWith(IDocSegmentList.IDOC_SEGMENT_LIST_FILE_EXTENSION)) {
				return createIDocSegmentListCategory(f.getProject());
			}
			if (f.getName().endsWith(".ldm")) { //$NON-NLS-1$
				return createLDMCategory(f.getProject());
			}
			if (f.getName().endsWith(".dbm")) { //$NON-NLS-1$
				return createDBMCategory(f.getProject());
			}
		} else if (element instanceof ProjectCategory) {
			ProjectCategory cat = (ProjectCategory) element;
			String name = cat.getName();
			if (name.equals(RM_CAT)) {
				return cat.getProject();
			} else if (name.equals(SAP_OBJECT_LIST_CAT) || name.equals(RMCFG_CAT)) {
				return createRMCategory(cat.getProject());
			} else if (name.equals(TABLE_LIST_CAT) || name.equals(IDOC_SEGMENT_LIST_CAT)) {
				return createSAPObjectListsCategory(cat.getProject());
			} else if (name.equals(LDM_CAT) || name.equals(DBM_CAT)) {
				return createDataModelsCategory(cat.getProject());
			} else if (name.equals(MODELS_CAT)) {
				return cat.getProject();
			}
		} else if (element instanceof NameWrapper) {
			return ((NameWrapper) element).getParent();
		}
		return null;
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