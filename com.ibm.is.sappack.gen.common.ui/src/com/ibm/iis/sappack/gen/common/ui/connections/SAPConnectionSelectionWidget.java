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


import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
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

import com.ibm.iis.sappack.gen.common.ui.util.Utils;
import com.ibm.is.sappack.gen.common.ui.Activator;
import com.ibm.is.sappack.gen.common.ui.Messages;


public class SAPConnectionSelectionWidget extends Composite {
	private SapConnectionSelectedListener sapConnectionSelectedListener = null;

	private Text   sapConnectionName;
	private Button browseConnection;

	private SapSystem cachedSAPConnection;

	private SAPConnectionRepository rep = SAPConnectionRepository.getRepository();


	public interface SapConnectionSelectedListener {
		void sapConnectionSelected(SapSystem selectedSapConnection);
	}


  	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	


	public class SapConnectionSelectedPasswordCheckListener implements SapConnectionSelectedListener {
		@Override
		public void sapConnectionSelected(SapSystem selectedSapConnection) {
			//ask for the password once if it is not present
			PasswordDialog.checkForPassword(getShell(), selectedSapConnection);
		}

	}
	
	public SAPConnectionSelectionWidget(Composite parent, int style) {
		super(parent, style);
		createControlImpl(this, style);
	}

	private void createControlImpl(Composite comp, int style) {
		comp.setLayout(new GridLayout(1, false));
		comp.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		Group sapComp = new Group(comp, style);
		sapComp.setText(Messages.SAPConnectionSelectionWidget_0);

		sapComp.setLayout(new GridLayout(3, false));
		sapComp.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		Label l = new Label(sapComp, SWT.NONE);
		l.setText(Messages.SAPConnectionSelectionWidget_1);

		sapConnectionName = new Text(sapComp, SWT.BORDER);
		sapConnectionName.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				//clear the cached sap system if someone changes the sap system name
				cachedSAPConnection = null;
			}
		});
		
		//	sapConnectionName.setEnabled(false);
		sapConnectionName.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		this.browseConnection = new Button(sapComp, SWT.PUSH);
		this.browseConnection.setText(Messages.SAPConnectionSelectionWidget_2);
		this.browseConnection.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				ITreeContentProvider provider = null;
				ConnectionsView view = ConnectionsView.theView;
				if (view != null) {
					provider = (ITreeContentProvider) view.getTreeViewer().getContentProvider();
				} else {
					provider = new ConnectionsView.ConnectionContentProvider(SAPConnectionRepository.getRepository(), IISConnectionRepository.getRepository(), CWDBConnectionRepository.getRepository());
				}

				ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(getShell(), new ConnectionsView.ConnectionLabelProvider(), provider);
				dialog.setTitle(Messages.SAPConnectionSelectionWidget_3);
				dialog.setMessage(Messages.SAPConnectionSelectionWidget_4);
				dialog.setInput(ResourcesPlugin.getWorkspace().getRoot());
				dialog.setAllowMultiple(false);

				dialog.addFilter(new ViewerFilter() {

					@Override
					public boolean select(Viewer viewer, Object parentElement, Object element) {
						return true;
					}
				});

				dialog.setValidator(new ISelectionStatusValidator() {

					@Override
					public IStatus validate(Object[] selection) {
						if (selection.length > 0) {
							Object o = selection[0];
							if (o instanceof SapSystem) {
								return new Status(IStatus.OK, Activator.PLUGIN_ID, ""); //$NON-NLS-1$
							}
						}
						return new Status(IStatus.ERROR, Activator.PLUGIN_ID, Messages.SAPConnectionSelectionWidget_5);
					}

				});

				int result = dialog.open();
				if (result == ElementTreeSelectionDialog.CANCEL) {
					return;
				}
				Object[] connections = dialog.getResult();
				Object o = connections[0];
				if (o instanceof IISSapSystem) {
					IISSapSystem sapSys = (IISSapSystem) o;
					sapConnectionName.setText(sapSys.getFullName());
				} else if (o instanceof CWDBSAPSystem) {
					CWDBSAPSystem sapSys = (CWDBSAPSystem) o;
					sapConnectionName.setText(sapSys.getFullName());
				} else if (o instanceof SapSystem) {
					SapSystem sapSys = (SapSystem) o;
					sapConnectionName.setText(sapSys.getName());
				}
				
				if (sapConnectionSelectedListener!=null) {
					sapConnectionSelectedListener.sapConnectionSelected(getSelectedSAPSystem());
				}
				cachedSAPConnection = null;
			}

		});

	}

	public Text getSAPConnectionNameText() {
		return this.sapConnectionName;
	}

	public SapSystem getSelectedSAPSystem() {
		if (cachedSAPConnection == null) {
			String connName = Utils.getText(this.sapConnectionName);
			if (connName == null) {
				return null;
			}
			cachedSAPConnection = rep.getSAPConnection(connName);
		}
		return cachedSAPConnection;

	}

	public void addModifyListener(ModifyListener modifyListener) {
		this.sapConnectionName.addModifyListener(modifyListener);
	}
	
	public void addSapConnectionSelectedListener(SapConnectionSelectedListener sapSystemConnectionSelectedListener) {
		this.sapConnectionSelectedListener = sapSystemConnectionSelectedListener;
	}

	@Override
	public void setEnabled(boolean enabled) {
		this.sapConnectionName.setEnabled(enabled);
		this.browseConnection.setEnabled(enabled);

		super.setEnabled(enabled);
	}
}
