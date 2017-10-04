//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2013, 2014                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.iis.sappack.gen.common.ui.connections
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.iis.sappack.gen.common.ui.connections;


import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.ui.ProfileImageRegistry;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.DrillDownAdapter;
import org.eclipse.ui.part.ViewPart;

import com.ibm.iis.sappack.gen.common.ui.iisclient.IISClient;
import com.ibm.iis.sappack.gen.common.ui.preferences.IISPreferencePage;
import com.ibm.iis.sappack.gen.common.ui.util.CachingTreeContentProvider;
import com.ibm.iis.sappack.gen.common.ui.util.ImageProvider;
import com.ibm.is.sappack.gen.common.Constants;
import com.ibm.is.sappack.gen.common.JobGeneratorException;
import com.ibm.is.sappack.gen.common.request.GetAllProjectsRequest;
import com.ibm.is.sappack.gen.common.request.GetAllProjectsResponse;
import com.ibm.is.sappack.gen.common.request.ServerRequestUtil;
import com.ibm.is.sappack.gen.common.ui.Activator;
import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.common.ui.ModeManager;
import com.ibm.is.sappack.gen.common.ui.preferences.AdvancedSettingsPreferencePage;
import com.ibm.is.sappack.gen.common.ui.util.Utilities;


public class ConnectionsView extends ViewPart {
	public static final String VIEW_ID = "com.ibm.iis.sappack.gen.common.ui.connectionsView"; //$NON-NLS-1$

	// this variable is set when the view is created. Since it only exists once per platform
	public static ConnectionsView theView = null;

	private TreeViewer viewer;

	private SAPConnectionRepository sapConnRep = SAPConnectionRepository.getRepository();
	private IISConnectionRepository iisConnRep = IISConnectionRepository.getRepository();
	private CWDBConnectionRepository cwdbConnRep = CWDBConnectionRepository.getRepository();


  	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	

	
	public ConnectionsView() {
		super();
		theView = this;
	}

	public static class SAPConnectionCategory {

	}

	public static class IISConnectionCategory {

	}

	public static class IISSAPConnectionCategory {
		IISSAPConnectionCategory(IISConnection conn) {
			this.conn = conn;
		}

		IISConnection conn;

		@Override
		public boolean equals(Object obj) {
			if (obj == this) {
				return true;
			}
			if (!(obj instanceof IISSAPConnectionCategory)) {
				return false;
			}
			IISSAPConnectionCategory other = (IISSAPConnectionCategory) obj;
			return this.conn.equals(other.conn);
		}

		@Override
		public int hashCode() {
			return this.conn.hashCode();
		}

	}
	
	public static class CWDBConnectionCategory {
		
	}

	
	public static class CWDBSAPConnectionCategory {
		CWDBSAPConnectionCategory(CWDBConnection conn) {
			this.conn = conn;
		}

		CWDBConnection conn;

		@Override
		public boolean equals(Object obj) {
			if (obj == this) {
				return true;
			}
			if (!(obj instanceof IISSAPConnectionCategory)) {
				return false;
			}
			CWDBSAPConnectionCategory other = (CWDBSAPConnectionCategory) obj;
			return this.conn.equals(other.conn);
		}

		@Override
		public int hashCode() {
			return this.conn.hashCode();
		}

	}
	
	
	static Map<IISConnection, List<SapSystem>> iisSAPConnectionCache = new HashMap<IISConnection, List<SapSystem>>();

	public static List<SapSystem> getAllDataStageSAPSystems(IISConnection conn) {
		List<SapSystem> result = iisSAPConnectionCache.get(conn);
		
		if (result == null) {
			result = new ArrayList<SapSystem>();
			boolean expand = conn.ensurePasswordIsSet();
			if (expand) {
				SapConnectionRetreiver retriever = new SapConnectionRetreiver(conn.getDomain(), conn.getDomainServerPort(), 
				                                                              conn.getUser(), conn.getPassword(), Constants.FIRST_DS_PROJECT,
				                                                              conn.useHTTPS(), conn.getHTTPSPort());

				Collection<String> dsConnections = retriever.getAllSapConnections();
				if (dsConnections == null) {
					if (retriever.getErrorMessage() != null) {
			         MessageDialog.openError(null, Messages.TitleError, retriever.getErrorMessage());
					}
				}
				else {
					for (String dsConn : dsConnections) {
						SapSystem sapSys = retriever.getSapSystem(dsConn);
						result.add(new IISSapSystem(sapSys, conn));
					}
					iisSAPConnectionCache.put(conn, result);
				}
			}
		} // if (result == null)

		return result;
	}

	static Map<CWDBConnection, List<SapSystem>> cwdbSAPConnectionCache = new HashMap<CWDBConnection, List<SapSystem>>();

	public static List<SapSystem> getAllCWDBSAPSystems(CWDBConnection conn) {
		List<SapSystem> result = cwdbSAPConnectionCache.get(conn);
		if (result == null) {
			result = new ArrayList<SapSystem>();
			boolean expand = conn.ensureIsConnected();
			if (expand) {
				result.addAll(conn.getAllSAPSystems());
				cwdbSAPConnectionCache.put(conn, result);
			}
		}
		return result;
	}

	static boolean isCWDBEnabled() {
		return ModeManager.isCWEnabled(); // && AdvancedSettingsPreferencePage.isSettingEnabled("CWDB_ENABLED");
	}
	
	static SAPConnectionCategory sapConnectionsCategory = new SAPConnectionCategory();
	static IISConnectionCategory iisConnectionsCategoriy = new IISConnectionCategory();
	static CWDBConnectionCategory cwdbConnectionsCategory = new CWDBConnectionCategory();

	public static class ConnectionContentProvider implements IStructuredContentProvider, ITreeContentProvider {

		SAPConnectionRepository sapConnRep;
		IISConnectionRepository iisConnRep;
		CWDBConnectionRepository cwdbConnectionRepository;
		
		Object root = new Object();

		public ConnectionContentProvider(SAPConnectionRepository sapConnRep, IISConnectionRepository iisConnRep, CWDBConnectionRepository cwdbConnectionRepository) {
			this.sapConnRep = sapConnRep;
			this.iisConnRep = iisConnRep;
			this.cwdbConnectionRepository = cwdbConnectionRepository;
		}

		@Override
		public void dispose() {
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

		@Override
		public Object[] getChildren(Object parentElement) {
			List<Object> kids = new ArrayList<Object>();
			if (parentElement instanceof SAPConnectionCategory) {
				kids.addAll(sapConnRep.getLocalSAPConnections());
			} else if (parentElement instanceof IISConnectionCategory) {
				List<IISConnection> conns = iisConnRep.getAllConnections();
				kids.addAll(conns);
			} else if (parentElement instanceof IISSAPConnectionCategory) {
				IISConnection conn = ((IISSAPConnectionCategory) parentElement).conn;
				kids.addAll(getAllDataStageSAPSystems(conn));
			} else if (parentElement instanceof IISConnection) {
				IISConnection conn = (IISConnection) parentElement;
				kids.add(new IISSAPConnectionCategory(conn));
			} else if (parentElement instanceof CWDBConnectionCategory) {
				List<CWDBConnection> conns = cwdbConnectionRepository.getAllCWDBConnections();
				kids.addAll(conns);
			} else if (parentElement instanceof CWDBConnection) {
				CWDBConnection conn = (CWDBConnection) parentElement;
				IConnectionProfile prof = conn.getProfile();
				if (prof != null) {
					kids.add(prof);
				}
				if (AdvancedSettingsPreferencePage.isSettingEnabled("ENABLE_CWDB_SAP_CONNECTIONS")) { //$NON-NLS-1$
					kids.add(new CWDBSAPConnectionCategory(conn));
				}
				
			} else if (parentElement instanceof CWDBSAPConnectionCategory) {
				CWDBConnection conn = ((CWDBSAPConnectionCategory) parentElement).conn;
				kids.addAll( getAllCWDBSAPSystems(conn) );
			}
			return kids.toArray();
		}

		@Override
		public Object getParent(Object element) {
			if (element instanceof SapSystem) {
				return sapConnectionsCategory;
			} else if (element instanceof IISConnection) {
				return iisConnectionsCategoriy;
			} else if (element instanceof CWDBConnection) {
				return cwdbConnectionsCategory;
			}
			if (element == sapConnectionsCategory || element == iisConnectionsCategoriy || element == cwdbConnectionsCategory) {
				return root;
			}
			return null;
		}

		@Override
		public boolean hasChildren(Object element) {
			return getChildren(element).length > 0;
		}

		@Override
		public Object[] getElements(Object inputElement) {
			if (isCWDBEnabled()) {
				return new Object[] { sapConnectionsCategory, iisConnectionsCategoriy, cwdbConnectionsCategory };
			}
			return new Object[] { sapConnectionsCategory, iisConnectionsCategoriy};
		}

	}

	public static class ConnectionLabelProvider extends LabelProvider {

		@Override
		public Image getImage(Object element) {
			if (element instanceof SAPConnectionCategory) {
				return ImageProvider.getImage(ImageProvider.IMAGE_SAP_CONNECTIONS_FOLDER);
			}
			if (element instanceof IISConnectionCategory) {
				return ImageProvider.getImage(ImageProvider.IMAGE_IIS_CONNECTION_FOLDER);
			}
			if (element instanceof IISSAPConnectionCategory) {
				return ImageProvider.getImage(ImageProvider.IMAGE_SAP_CONNECTIONS_FOLDER);
			}
			if (element instanceof CWDBConnectionCategory) {
				return ImageProvider.getImage(ImageProvider.IMAGE_CWDB_CONNECTION_FOLDER);
			}
			if (element instanceof CWDBSAPConnectionCategory) {
				return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FOLDER);
			}
			if (element instanceof SapSystem) {
				return ImageProvider.getImage(ImageProvider.IMAGE_SAP_CONNECTION_ICON);
			}
			if (element instanceof IISConnection) {
				return ImageProvider.getImage(ImageProvider.IMAGE_IIS_CONNECTION_16);
			} 
			if (element instanceof CWDBConnection) {
				return ImageProvider.getImage(ImageProvider.IMAGE_CWDB_CONNECTION_16);
			}
			if (element instanceof IConnectionProfile) {
				IConnectionProfile profile = (IConnectionProfile) element;
				Image image = ProfileImageRegistry.getInstance().getProfileImage(profile.getProvider());
				return image;
			}
			return null;
		}

		@Override
		public String getText(Object element) {
			if (element instanceof SAPConnectionCategory) {
				return Messages.ConnectionsView_0;
			}
			if (element instanceof IISConnectionCategory) {
				return Messages.ConnectionsView_1;
			}
			if (element instanceof CWDBConnectionCategory) {
				return Messages.ConnectionsView_26;
			}
			if (element instanceof IISSAPConnectionCategory) {
				return Messages.ConnectionsView_2;
			}
			if (element instanceof CWDBSAPConnectionCategory) {
				return Messages.ConnectionsView_27;
			}
			if (element instanceof SapSystem) {
				return ((SapSystem) element).getName();
			}
			if (element instanceof IISConnection) {
				return ((IISConnection) element).getName();
			}
			if (element instanceof CWDBConnection) {
				return ((CWDBConnection) element).getName();
			}
			if (element instanceof IConnectionProfile) {
				return ((IConnectionProfile) element).getName();
			}
			return null;
		}

	}

	@Override
	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(new CachingTreeContentProvider(new ConnectionContentProvider(this.sapConnRep, iisConnRep, this.cwdbConnRep)));
		viewer.setLabelProvider(new ConnectionLabelProvider());
		//viewer.setSorter(new NameSorter());
		viewer.setInput(getViewSite());

		DrillDownAdapter drillDownAdapter = new DrillDownAdapter(viewer);
		addToActionBars(drillDownAdapter);
		addToContextMenu();

		viewer.addDoubleClickListener(new IDoubleClickListener() {

			@Override
			public void doubleClick(DoubleClickEvent event) {
				Object o = ((IStructuredSelection) event.getSelection()).getFirstElement();
				if (o instanceof SapSystem) {
					EditSAPConnectionDialog d = new EditSAPConnectionDialog(null, sapConnRep, ((SapSystem) o).getFullName());
					int result = d.open();
					if (result == WizardDialog.OK) {
						sapConnRep.save();
						refresh();
					}
				} else if (o instanceof IISConnection) {
					EditIISConnectionWizardDialog d = new EditIISConnectionWizardDialog(null, ((IISConnection) o).getName());
					int res = d.open();
					if (res == WizardDialog.OK) {
						refresh();
					}
				} else if (o instanceof CWDBConnection) {
					EditCWDBConnectionWizard wi = new EditCWDBConnectionWizard( ((CWDBConnection) o).getName());
					WizardDialog d = new WizardDialog(null, wi);
					int result = d.open();
					if (result == WizardDialog.OK) {
						refresh();
					}
				
				}

			}
		});

	}

	public TreeViewer getTreeViewer() {
		return this.viewer;
	}

	void addToActionBars(DrillDownAdapter drillDownAdapter) {
		IActionBars bars = getViewSite().getActionBars();

		Action refreshAction = new Action() {

			@Override
			public void run() {
				iisSAPConnectionCache.clear();
				cwdbSAPConnectionCache.clear();
				refresh();
			}

		};
		refreshAction.setText(Messages.ConnectionsView_3);
		refreshAction.setToolTipText(Messages.ConnectionsView_4);
		ImageDescriptor desc = ImageProvider.getImageDescriptor(ImageProvider.IMAGE_VIEW_REFRESH);
		refreshAction.setImageDescriptor(desc);
		IToolBarManager tbManager = bars.getToolBarManager();
		tbManager.add(refreshAction);
		//	drillDownAdapter.addNavigationActions(tbManager);
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

	void refresh() {
		IContentProvider prov = viewer.getContentProvider();
		if (prov instanceof CachingTreeContentProvider) {
			((CachingTreeContentProvider) prov).refresh();
		}

		viewer.refresh();
	}

	/*
	class SAPConnectionNameDialog extends InputDialog {
		public SAPConnectionNameDialog(Shell parentShell, final SAPConnectionRepository repository) {
			super(parentShell, "SAP Connection Name", "Enter SAP connectoin Name", "", new IInputValidator() {

				@Override
				public String isValid(String newText) {
					if (newText == null || newText.isEmpty()) {
						return "Connection name must not be empty";
					}
					SapSystem existingConnection = repository.getSAPConnection(newText);
					if (existingConnection != null) {
						return MessageFormat.format("A local SAP connection with name ''{0}'' already exists", newText);
					}
					return null;
				}
			});

		}

	}
	*/

	protected void fillContextMenu(IMenuManager manager) {
		ISelection selection = viewer.getSelection();
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection structSel = (IStructuredSelection) selection;
			Object o = structSel.getFirstElement();
			if (o instanceof SAPConnectionCategory) {
				Action newSAPConnAction = new Action() {

					@Override
					public void run() {
						boolean isJCoInstalledProperly = Utilities.checkJCoAvailabilityWithDialog();
						if (!isJCoInstalledProperly) {
							return;
						}
						
						SAPConnectionRepository sapRep = SAPConnectionRepository.getRepository();
						EditSAPConnectionDialog d = new EditSAPConnectionDialog(null, sapRep, null);
						int result = d.open();
						if (result == WizardDialog.OK) {
							//							sapRep.addLocalSAPConnection(conn);
							sapRep.save();
							refresh();
						}
					}

				};
				newSAPConnAction.setText(Messages.ConnectionsView_5);
				newSAPConnAction.setDescription(Messages.ConnectionsView_6);

				manager.add(newSAPConnAction);
			} else if (o instanceof IISSAPConnectionCategory) {
				final IISConnection conn = ((IISSAPConnectionCategory) o).conn;
				Action openInAdmin = new Action() {
					public void run() {
						final IISClient cl = new IISClient();
						String[] checkResult = cl.check();
						if (checkResult[0] != null) {
							MessageDialog.openWarning(null, IISClient.IIS_CLIENT_CHECK_TITLE, checkResult[0]);
							return;
						}
						if (!conn.ensurePasswordIsSet()) {
							return;
						}

						Job job = new Job(Messages.SapConnectionsWidget_4) {

							public IStatus run(IProgressMonitor mon) {
								IStatus result = Status.OK_STATUS;
								try {
									GetAllProjectsRequest projRequest = new GetAllProjectsRequest();
									IISConnection.initializeRequest(projRequest, conn);
									GetAllProjectsResponse resp = (GetAllProjectsResponse) ServerRequestUtil.send(projRequest);
									if (resp.containsErrors()) {
										throw new JobGeneratorException(resp.get1stMessage());
									}
									List<?> projects = resp.getProjects();
									if (projects == null || projects.isEmpty()) {
										throw new JobGeneratorException(Messages.ConnectionsView_7);
									}
									String firstProject = (String) projects.get(0);
									int ix = firstProject.indexOf('/');
									String hostTemp = ""; //$NON-NLS-1$
									String projectTemp = ""; //$NON-NLS-1$
									if (ix != -1) {
										hostTemp = firstProject.substring(0, ix);
										projectTemp = firstProject.substring(ix + 1);
									}
									final String host = hostTemp;
									final String project = projectTemp;

									final String domain = conn.getDomain();
									final String user = conn.getUser();
									final String password = conn.getPassword();
									final String connection = conn.getName();
									cl.openAndWaitForDSAdminForSAP(domain, host, user, password, project, connection, mon);
								} catch (Throwable e) {
									e.printStackTrace();
									Activator.getLogger().log(Level.WARNING, Messages.LogExceptionMessage, e);
									result = new Status(Status.ERROR, Activator.PLUGIN_ID, MessageFormat.format(Messages.SapConnectionsWidget_5, e.getClass().getName() + ": " //$NON-NLS-1$
											+ e.getMessage()), e);
								}
								return result;
							}
						};
						job.setUser(true);
						job.schedule();
					}
				};
				openInAdmin.setText(Messages.ConnectionsView_8);
				openInAdmin.setDescription(Messages.ConnectionsView_9);

				boolean add = true;
				if (IISPreferencePage.getIISClientDirectory().isEmpty()) {
					add = false;
				}
				if (add) {
					manager.add(openInAdmin);
				}
			} else if (o instanceof IISConnectionCategory) {
				Action newIISConnAction = new Action() {
					@Override
					public void run() {
						EditIISConnectionWizardDialog d = new EditIISConnectionWizardDialog(null, null);
						int res = d.open();
						if (res == WizardDialog.OK) {
							refresh();
						}
					}

				};
				newIISConnAction.setText(Messages.ConnectionsView_10);
				newIISConnAction.setDescription(Messages.ConnectionsView_11);
				manager.add(newIISConnAction);
			} else if (o instanceof IISSapSystem) {
				final SapSystem sapConn = (SapSystem) o;
				Action propertiesAction = new Action() {
					@Override
					public void run() {
						EditSAPConnectionDialog d = new EditSAPConnectionDialog(null, sapConnRep, sapConn.getFullName());
						int result = d.open();
						if (result == WizardDialog.OK) {
							sapConnRep.save();
							refresh();
						}
					}
				};
				propertiesAction.setText(Messages.ConnectionsView_12);
				propertiesAction.setDescription(Messages.ConnectionsView_13);
				manager.add(propertiesAction);
			} else if (o instanceof CWDBSAPSystem) {
				final SapSystem sapConn = (SapSystem) o;
				Action propertiesAction = new Action() {
					@Override
					public void run() {
						EditSAPConnectionDialog d = new EditSAPConnectionDialog(null, sapConnRep, sapConn.getFullName());
						int result = d.open();
						if (result == WizardDialog.OK) {
							sapConnRep.save();
							refresh();
						}
					}
				};
				propertiesAction.setText(Messages.ConnectionsView_12);
				propertiesAction.setDescription(Messages.ConnectionsView_28);
				manager.add(propertiesAction);
				
			} else if (o instanceof SapSystem) {
				final SapSystem sapConn = (SapSystem) o;
				Action deleteSAPConnAction = new Action() {
					@Override
					public void run() {
						boolean confirmed = MessageDialog.openConfirm(null, Messages.ConnectionsView_14, MessageFormat.format(Messages.ConnectionsView_15, sapConn.getName()));
						if (!confirmed) {
							return;
						}

						sapConnRep.removeLocalSAPConnection(sapConn);
						sapConnRep.save();
						refresh();
					}

				};
				deleteSAPConnAction.setText(Messages.ConnectionsView_16);
				deleteSAPConnAction.setDescription(Messages.ConnectionsView_17);
				manager.add(deleteSAPConnAction);

				
				Action editSAPConnAction = new Action() {
					@Override
					public void run() {
						EditSAPConnectionDialog d = new EditSAPConnectionDialog(null, sapConnRep, sapConn.getFullName());
						int result = d.open();
						if (result == WizardDialog.OK) {
							sapConnRep.save();
							refresh();
						}
					}

				};
				editSAPConnAction.setText(Messages.ConnectionsView_18);
				editSAPConnAction.setDescription(Messages.ConnectionsView_19);
				
				manager.add(editSAPConnAction);
				
			} else if (o instanceof IISConnection) {
				final IISConnection iisConn = (IISConnection) o;
				Action deleteIISConnAction = new Action() {
					@Override
					public void run() {
						boolean confirmed = MessageDialog.openConfirm(null, Messages.ConnectionsView_20, MessageFormat.format(Messages.ConnectionsView_21, iisConn.getName()));
						if (!confirmed) {
							return;
						}

						IISConnectionRepository rep = IISConnectionRepository.getRepository();
						IISConnection conn = rep.retrieveConnection(iisConn.getName());
						rep.removeConnection(conn);
						rep.save();
						refresh();
					}

				};
				deleteIISConnAction.setText(Messages.ConnectionsView_22);
				deleteIISConnAction.setDescription(Messages.ConnectionsView_23);
				manager.add(deleteIISConnAction);

				Action editIISConnAction = new Action() {

					@Override
					public void run() {
						EditIISConnectionWizardDialog d = new EditIISConnectionWizardDialog(null, iisConn.getName());
						d.open();
						refresh();
					}

				};
				editIISConnAction.setText(Messages.ConnectionsView_24);
				editIISConnAction.setDescription(Messages.ConnectionsView_25);
				manager.add(editIISConnAction);
			} else if (o instanceof CWDBConnectionCategory) {
				Action newCWDBConnAction = new Action() {
					@Override
					public void run() {
						EditCWDBConnectionWizard wi = new EditCWDBConnectionWizard(null);
						WizardDialog d = new WizardDialog(null, wi);
						int result = d.open();
						if (result == WizardDialog.OK) {
							refresh();
						}
					}
				};
				
				newCWDBConnAction.setText(Messages.ConnectionsView_29);
				newCWDBConnAction.setDescription(Messages.ConnectionsView_30);
				manager.add(newCWDBConnAction);
				
			} else if (o instanceof CWDBConnection) {
				final CWDBConnection conn = (CWDBConnection) o;
				Action editAction = new Action() {
					@Override
					public void run() {
						EditCWDBConnectionWizard wi = new EditCWDBConnectionWizard(conn.getName());
						WizardDialog d = new WizardDialog(null, wi);
						int result = d.open();
						if (result == WizardDialog.OK) {
							refresh();
						}
					}
					
				};
				editAction.setText(Messages.ConnectionsView_31);
				editAction.setDescription(Messages.ConnectionsView_32);
				manager.add(editAction);
				
				Action deleteCWDBConnAction = new Action() {
					@Override
					public void run() {
						String name = conn.getName();
						boolean confirmed = MessageDialog.openConfirm(null, Messages.ConnectionsView_20, MessageFormat.format(Messages.ConnectionsView_33, name));
						if (!confirmed) {
							return;
						}
						CWDBConnectionRepository rep = CWDBConnectionRepository.getRepository();
						rep.removeConnection(name);
						rep.save();
						refresh();
						
					}
				};
				deleteCWDBConnAction.setText(Messages.ConnectionsView_34);
				deleteCWDBConnAction.setDescription(Messages.ConnectionsView_35);
				manager.add(deleteCWDBConnAction);
			}

		}
	}

	@Override
	public void setFocus() {
	}

	public void selectConnection(SapSystem connection) {
		List<Object> path = new ArrayList<Object>();
		if (connection instanceof IISSapSystem) {
			path.add(iisConnectionsCategoriy);
			IISSapSystem sap = (IISSapSystem) connection;
			IISConnection iisConn = sap.getIISConnection();
			path.add(iisConn);
			path.add(new IISSAPConnectionCategory(iisConn));
			path.add(connection);
		} else {
			path.add(sapConnectionsCategory);
			path.add(connection);
		}
		TreePath tp = new TreePath(path.toArray());
		//	this.viewer.expandToLevel(tp, TreeViewer.ALL_LEVELS);
		//	this.viewer.setExpandedState(tp, true);
		ITreeSelection sel = new TreeSelection(tp);
		this.viewer.setSelection(sel);
		//		this.viewer.setExpandedState(connection, true);
		//		this.viewer.setExpandedElements(new Object[]{ connection});

	}

}
