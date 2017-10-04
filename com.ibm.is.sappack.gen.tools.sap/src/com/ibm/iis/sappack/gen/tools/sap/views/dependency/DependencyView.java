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
// Module Name : com.ibm.iis.sappack.gen.tools.sap.views.dependency
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.iis.sappack.gen.tools.sap.views.dependency;


import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.DrillDownAdapter;
import org.eclipse.ui.part.ViewPart;

import com.ibm.iis.sappack.gen.common.ui.connections.ConnectionsView;
import com.ibm.iis.sappack.gen.common.ui.connections.SAPConnectionRepository;
import com.ibm.iis.sappack.gen.common.ui.connections.SapSystem;
import com.ibm.iis.sappack.gen.common.ui.navigator.SAPPackNavigator;
import com.ibm.iis.sappack.gen.common.ui.util.ImageProvider;
import com.ibm.iis.sappack.gen.common.ui.util.Utils;
import com.ibm.iis.sappack.gen.tools.sap.idocseglist.IDocSegmentList;
import com.ibm.iis.sappack.gen.tools.sap.rmconf.RMConfiguration;
import com.ibm.iis.sappack.gen.tools.sap.tablelist.TableList;
import com.ibm.iis.sappack.gen.tools.sap.views.dependency.DependencyTreeFactory.ImportRun;
import com.ibm.iis.sappack.gen.tools.sap.views.dependency.DependencyTreeFactory.SAPConnectionStub;
import com.ibm.iis.sappack.gen.tools.sap.views.navigator.NavigatorRMContentProvider;
import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.tools.sap.activator.Activator;


public class DependencyView extends ViewPart {
	private TreeViewer viewer;

	private Object focusOnObject;
	private boolean downward;


	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	

	public DependencyView() {
		this.downward = true;
	}

	TreeNode rootNode;

	class DepViewContentProvider implements ITreeContentProvider {

		@Override
		public Object[] getElements(Object inputElement) {
			if (rootNode == null) {
				return new Object[0];
			}
			return new Object[] { rootNode };
		}

		@Override
		public void dispose() {
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

		@Override
		public Object[] getChildren(Object parentElement) {
			return ((TreeNode) parentElement).getChildren().toArray();
		}

		@Override
		public Object getParent(Object element) {
			return ((TreeNode) element).getParent();
		}

		@Override
		public boolean hasChildren(Object element) {
			return getChildren(element).length > 0;
		}
	}

	public static Image getFileImage(IFile f) {
		String name = f.getName();
		if (name.endsWith(RMConfiguration.RMCONF_FILE_EXTENSION)) {
			return ImageProvider.getImage(ImageProvider.IMAGE_RMCONF_16);
		} else if (name.endsWith(TableList.TABLE_LIST_FILE_EXTENSION)) {
			return ImageProvider.getImage(ImageProvider.IMAGE_ABAP_TABLES_16);
		} else if (name.endsWith(IDocSegmentList.IDOC_SEGMENT_LIST_FILE_EXTENSION)) {
			return ImageProvider.getImage(ImageProvider.IMAGE_IDOCS_16);
		} else if (name.endsWith("ldm")) { //$NON-NLS-1$
			return ImageProvider.getImage(ImageProvider.IMAGE_LDM_ICON);
		} else if (name.endsWith("dbm")) { //$NON-NLS-1$
			return ImageProvider.getImage(ImageProvider.IMAGE_DBM_ICON);
		}
		return null;
	}

	class DepViewLabelProvider extends LabelProvider {

		@Override
		public String getText(Object element) {
			Object o = ((TreeNode) element).getInfo();
			if (o instanceof IFile) {
				IFile f = (IFile) o;
				return f.getName() + " (" + f.getFullPath().toString() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
			}
			if (o instanceof ImportRun) {
				ImportRun ir = (ImportRun) o;
				Date d = ir.timeOfRun;
				return Messages.DependencyView_0 + DateFormat.getDateTimeInstance().format(d);
			}
			if (o instanceof SAPConnectionStub) {
				return ((SAPConnectionStub) o).getName();
			}
			return o.toString();
		}

		@Override
		public Image getImage(Object element) {
			Object o = ((TreeNode) element).getInfo();
			if (o instanceof IFile) {
				IFile f = (IFile) o;
				return getFileImage(f);
			} else if (o instanceof ImportRun) {
				return ImageProvider.getImage(ImageProvider.IMAGE_RAPID_MODELER_16); // PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
			} else if (o instanceof SAPConnectionStub) {
				return ImageProvider.getImage(ImageProvider.IMAGE_SAP_CONNECTION_ICON);
			}
			return null;
		}

	}

	@Override
	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(new DepViewContentProvider());
		viewer.setLabelProvider(new DepViewLabelProvider());
		DrillDownAdapter drillDownAdapter = new DrillDownAdapter(viewer);
		addToActionBars(drillDownAdapter);
		addToContextMenu();

		viewer.addDoubleClickListener(new IDoubleClickListener() {

			@Override
			public void doubleClick(DoubleClickEvent event) {
				Object o = ((IStructuredSelection) event.getSelection()).getFirstElement();
				if (o instanceof TreeNode) {
					Object info = ((TreeNode) o).getInfo();
					if (info instanceof IFile) {
						IFile f = (IFile) info;
						try {
							IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
							IDE.openEditor(page, f);
						} catch (PartInitException e) {
							handleException(e);
						}
					}
				}

			}
		});
		getSite().setSelectionProvider(viewer);
		viewer.setInput(getViewSite());
	}

	void handleException(Exception exc) {
		Utils.showUnexpectedException(null, exc);
	}

	void addToContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	protected void fillContextMenu(IMenuManager manager) {
		ISelection selection = viewer.getSelection();
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection structSel = (IStructuredSelection) selection;
			Object o = structSel.getFirstElement();
			if (o == null) {
				return;
			}
			TreeNode tn = (TreeNode) o;
			final Object info = tn.getInfo();
			Action focusAction = new Action() {

				@Override
				public void run() {
					setFocusOnObject(info, false);
				}

			};

			focusAction.setText(MessageFormat.format(Messages.DependencyView_1, ((ILabelProvider) viewer.getLabelProvider()).getText(tn)));

			if (info instanceof IFile) {
				Action openAction = new Action() {
					@Override
					public void run() {
						if (info instanceof IFile) {
							IFile f = (IFile) info;
							try {
								IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
								IDE.openEditor(page, f);
							} catch (PartInitException e) {
								handleException(e);
							}
						}
					}
				};
				openAction.setText(Messages.DependencyView_2);
				openAction.setToolTipText(Messages.DependencyView_3);
				manager.add(openAction);

				Action showInNavigatorAction = new Action() {
					@Override
					public void run() {
						if (info instanceof IFile) {
							IFile f = (IFile) info;
							IViewPart v = null;
							try {
								v = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(Messages.DependencyView_4);

							} catch (PartInitException e) {
								Activator.logException(e);
								e.printStackTrace();
								v = null;
							}
							if (v instanceof SAPPackNavigator) {
								SAPPackNavigator nav = (SAPPackNavigator) v;
								NavigatorRMContentProvider prov = new NavigatorRMContentProvider();
								List<Object> path = new ArrayList<Object>();
								Object o = f;
								while (o != null) {
									path.add(0, o);
									o = prov.getParent(o);
								}
								TreePath tp = new TreePath(path.toArray());
								ITreeSelection sel = new TreeSelection(tp);
								nav.getCommonViewer().setSelection(sel, true);

								//StructuredSelection sel = new StructuredSelection(f);
								//CommonViewer viewer = nav.getCommonViewer();
								//.setSelection(sel, true);
								// TODO

							}
						}
					}
				};
				showInNavigatorAction.setText(Messages.DependencyView_5);
				showInNavigatorAction.setToolTipText(Messages.DependencyView_6);
				manager.add(showInNavigatorAction);
			} else if (info instanceof SAPConnectionStub) {
				final String fullname = ((SAPConnectionStub) info).getName();
				Action showInConnectionsViewAction = new Action() {
					@Override
					public void run() {

						SapSystem connection = SAPConnectionRepository.getRepository().getSAPConnection(fullname);
						if (connection == null) {
							return;
						}
						IViewPart v = null;
						try {
							v = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(ConnectionsView.VIEW_ID);
						} catch (PartInitException e) {
							Activator.logException(e);
							e.printStackTrace();
							v = null;
						}
						if (v instanceof ConnectionsView) {
							ConnectionsView cv = (ConnectionsView) v;
							cv.selectConnection(connection);
						}

					}
				};
				showInConnectionsViewAction.setText(Messages.DependencyView_7);
				showInConnectionsViewAction.setText(Messages.DependencyView_8);
				manager.add(showInConnectionsViewAction);
			}

			manager.add(focusAction);
		}
	}

	void addToActionBars(DrillDownAdapter drillDownAdapter) {
		IActionBars bars = getViewSite().getActionBars();

		IToolBarManager tbManager = bars.getToolBarManager();

		Action downAction = new Action() {

			@Override
			public void run() {
				downward = true;
				setFocusOnObject(focusOnObject, false);
				refreshViewer();
			}
		};
		downAction.setToolTipText(Messages.DependencyView_9);
		downAction.setText(Messages.DependencyView_10);
		downAction.setImageDescriptor(ImageProvider.getImageDescriptor(ImageProvider.IMAGE_DEPVIEW_DOWN));
		tbManager.add(downAction);

		Action upAction = new Action() {

			@Override
			public void run() {
				downward = false;
				setFocusOnObject(focusOnObject, false);
				refreshViewer();
			}

		};
		upAction.setToolTipText(Messages.DependencyView_11);
		upAction.setText(Messages.DependencyView_12);
		upAction.setImageDescriptor(ImageProvider.getImageDescriptor(ImageProvider.IMAGE_DEPVIEW_UP));

		tbManager.add(upAction);

	}

	public void setFocusOnObject(final Object o) {
		setFocusOnObject(o, true);
	}

	private void refreshViewer() {
		viewer.refresh();
		viewer.expandToLevel(3);
	}

	DependencyTreeFactory depCreator = null;

	private void setFocusOnObject(final Object o, boolean full) {

		if (full) {
			Job j = new Job(Messages.DependencyView_13) {

				@Override
				protected IStatus run(IProgressMonitor monitor) {
					try {
						depCreator = new DependencyTreeFactory(ResourcesPlugin.getWorkspace().getRoot());
						TreeNode t = depCreator.createDependencyTree(o, downward);
						focusOnObject = o;
						rootNode = t;
						Display.getDefault().asyncExec(new Runnable() {
							public void run() {
								refreshViewer();
							}
						});
						return new Status(Status.OK, Activator.PLUGIN_ID, Messages.DependencyView_14);
					} catch (Exception e) {
						e.printStackTrace();
						return new Status(Status.ERROR, Activator.PLUGIN_ID, Messages.DependencyView_15, e);
					}
				}

			};
			j.schedule();
		} else {
			try {
				if (depCreator == null) {
					depCreator = new DependencyTreeFactory(ResourcesPlugin.getWorkspace().getRoot());
				}
				TreeNode t;
				t = depCreator.createDependencyTree(o, downward);
				focusOnObject = o;
				rootNode = t;
				refreshViewer();
			} catch (Exception e) {
				handleException(e);
			}
		}

	}

	@Override
	public void setFocus() {
		viewer.refresh();
	}

}
