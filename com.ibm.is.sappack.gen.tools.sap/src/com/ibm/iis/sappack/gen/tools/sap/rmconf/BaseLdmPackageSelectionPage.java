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
// Module Name : com.ibm.iis.sappack.gen.tools.sap.rmconf
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.iis.sappack.gen.tools.sap.rmconf;


import java.text.MessageFormat;
import java.util.StringTokenizer;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;

import com.ibm.iis.sappack.gen.common.ui.editors.EditorPageBase;
import com.ibm.iis.sappack.gen.common.ui.editors.IControlFactory;
import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.tools.sap.activator.Activator;
import com.ibm.is.sappack.gen.tools.sap.constants.Constants;
import com.ibm.is.sappack.gen.tools.sap.importer.helper.model.LdmAccessor;


public class BaseLdmPackageSelectionPage extends EditorPageBase {

	private static final String ROOT_PACKAGE_DISPLAY_NAME = Messages.BaseLdmPackageSelectionPage_8;
	
	private String propertyName;


  	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	

	public BaseLdmPackageSelectionPage(String tabname, String title, String description, String propertyName, String helpID) {
		super(tabname, title, description, helpID);
		this.propertyName = propertyName;
	}

	@Override
	public Image getImage() {
		final Image ldmPackageImage = Activator.getDefault().getImageRegistry().get(Constants.ICON_ID_LDM_PACKAGE);
		return ldmPackageImage;
	}

	LabelProvider labelProvider = new LabelProvider() {
		@Override
		public Image getImage(Object element) {
			final Image ldmPackageImage = Activator.getDefault().getImageRegistry().get(Constants.ICON_ID_LDM_PACKAGE);
			return ldmPackageImage;
		}

		@Override
		public String getText(Object element) {
			return ((LDMPackage) element).getName();
		}

	};

	ITreeContentProvider contentProvider = new ITreeContentProvider() {

		public void dispose() {
			//System.out.println("Disposing ...");
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			//System.out.println("Input changed: old=" + oldInput + ", new=" + newInput);
		}

		@Override
		public Object[] getChildren(Object parentElement) {
			LDMPackage p = (LDMPackage) parentElement;
			return p.getChildren();
		}

		@Override
		public Object getParent(Object element) {
			LDMPackage p = (LDMPackage) element;
			return p.getParent();
		}

		@Override
		public boolean hasChildren(Object element) {
			return getChildren(element).length > 0;
		}

		@Override
		public Object[] getElements(Object inputElement) {

			/* we cannot show the root package and 
			 * have to call getChilden due to this bug in eclipse
			 * 
			 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=224955
			 */

			return getChildren(inputElement);
		}

	};

	static class LDMPackage {
		LDMPackage kid = null;
		LDMPackage parent;
		String name;

		public LDMPackage(LDMPackage parent, String name) {
			this.parent = parent;
			if (parent != null) {
				this.parent.kid = this;
			}
			this.name = name;
		}

		public String getName() {
			return this.name;
		}

		public LDMPackage getParent() {
			return parent;
		}

		public void removeChild(LDMPackage p) {
			this.kid = null;
		}

		public LDMPackage[] getChildren() {
			if (kid == null) {
				return new LDMPackage[0];
			}
			return new LDMPackage[] { kid };
		}

	}

	LDMPackage rootPackage;

	Object createModel(String path) {
		LDMPackage invRoot = new LDMPackage(null, "Invisible root"); //$NON-NLS-1$
		
		rootPackage = new LDMPackage(invRoot, ROOT_PACKAGE_DISPLAY_NAME);
		if (path != null) {
			LDMPackage p = rootPackage;
			StringTokenizer tok = new StringTokenizer(path, TREE_SEP);
			while (tok.hasMoreTokens()) {
				String s = tok.nextToken();
				LDMPackage newPack = new LDMPackage(p, s);
				p = newPack;
			}
		}
		return invRoot;
	}

	class PackageNameDialog extends InputDialog {

		public PackageNameDialog(Shell parentShell, String initialValue) {
			super(parentShell, Messages.BaseLdmPackageSelectionPage_0, Messages.BaseLdmPackageSelectionPage_1, initialValue, new IInputValidator() {

				@Override
				public String isValid(String newText) {
					String errMsg = null;
					if (newText != null) {
						newText = newText.trim();
						if (newText.length() == 0) {
							errMsg = MessageFormat.format(Messages.BaseLdmPackageSelectionPage_7, TREE_SEP);
						}
						else {
							if (newText.contains(TREE_SEP)) {
								errMsg = MessageFormat.format(Messages.BaseLdmPackageSelectionPage_2, TREE_SEP);
							}
						}
					}
					return(errMsg);
				}
			});
		}

	}

	class AddPackageAction extends Action {
		private LDMPackage pack;

		public AddPackageAction(LDMPackage p) {
			super(Messages.BaseLdmPackageSelectionPage_3);
			pack = p;
		}

		@Override
		public void run() {
			PackageNameDialog dialog = new PackageNameDialog(null, Messages.BaseLdmPackageSelectionPage_4);

			int result = dialog.open();
			if (result == Window.CANCEL) {
				return;
			}

			String val = dialog.getValue();
			if (val != null) {
				val = val.trim();
			}
			new LDMPackage(pack, val);
			refreshTree();
		}

	};

	class RenamePackageAction extends Action {
		private LDMPackage pack;

		public RenamePackageAction(LDMPackage p) {
			super(Messages.BaseLdmPackageSelectionPage_5);
			this.pack = p;
		}

		public void run() {
			PackageNameDialog dialog = new PackageNameDialog(null, pack.getName());

			int result = dialog.open();
			if (result == Window.CANCEL) {
				return;
			}
			String val = dialog.getValue();

			pack.name = val;
			refreshTree();
		}

	}

	class RemovePackageAction extends Action {
		private LDMPackage pack;

		public RemovePackageAction(LDMPackage p) {
			super(Messages.BaseLdmPackageSelectionPage_6);
			this.pack = p;
		}

		public void run() {
			this.pack.getParent().removeChild(pack);
			refreshTree();
		}

	}

	TreeViewer treeViewer;

	public static String TREE_SEP = LdmAccessor.PACKAGE_HIERARCHY_SEPARATOR;

	String getTreePath() {
		StringBuffer buf = new StringBuffer();
		LDMPackage p = rootPackage.kid;
		while (p != null) {
			buf.append(TREE_SEP + p.getName());
			p = p.kid;
		}
		return buf.toString();
	}

	void refreshTree() {
		treeViewer.setAutoExpandLevel(TreeViewer.ALL_LEVELS);
		treeViewer.refresh();
		editor.getConfiguration().put(this.propertyName, getTreePath());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControls(IControlFactory controlFactory, final Composite parent) {
		Composite comp = controlFactory.createComposite(parent, SWT.NONE);
		comp.setLayout(new GridLayout(1, true));
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		comp.setLayoutData(gd);

		Tree packageSelectionTree = new Tree(comp, SWT.BORDER);
		packageSelectionTree.setHeaderVisible(false);
		packageSelectionTree.setEnabled(true);
		packageSelectionTree.setLayoutData(gd);
		packageSelectionTree.setLinesVisible(false);

		treeViewer = new TreeViewer(packageSelectionTree);
		treeViewer.setLabelProvider(labelProvider);
		treeViewer.setContentProvider(contentProvider);
		treeViewer.setInput(this.createModel(editor.getConfiguration().get(propertyName)));

		final MenuManager mgr = new MenuManager();
		mgr.setRemoveAllWhenShown(true);

		mgr.addMenuListener(new IMenuListener() {

			public void menuAboutToShow(IMenuManager manager) {
				IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
				if (!selection.isEmpty()) {
					LDMPackage p = (LDMPackage) selection.getFirstElement();
					if (p != null) {
						if (p.getChildren().length == 0) {
							mgr.add(new AddPackageAction(p));
						}
						if (p != rootPackage) {
							mgr.add(new RenamePackageAction(p));
							mgr.add(new RemovePackageAction(p));
						}
					}
				}
			}
		});
		treeViewer.getControl().setMenu(mgr.createContextMenu(treeViewer.getControl()));

	}

}
