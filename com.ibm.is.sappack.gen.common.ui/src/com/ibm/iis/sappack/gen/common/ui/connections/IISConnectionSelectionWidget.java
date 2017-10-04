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


public class IISConnectionSelectionWidget extends Composite {
	private Text iisConnectionName;


  	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	


	public IISConnectionSelectionWidget(Composite parent, int style) {
		super(parent, style);
		createControls();
	}

	private void createControls() {
		Composite comp = this;
		comp.setLayout(new GridLayout(1, false));
		comp.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		Group iisComp = new Group(comp, SWT.NONE);
		iisComp.setText(Messages.IISConnectionSelectionWidget_0);
		iisComp.setLayout(new GridLayout(3, false));
		iisComp.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		Label l = new Label(iisComp, SWT.NONE);
		l.setText(Messages.IISConnectionSelectionWidget_1);

		this.iisConnectionName = new Text(iisComp, SWT.BORDER);
		//	sapConnectionName.setEnabled(false);
		this.iisConnectionName.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		Button browse = new Button(iisComp, SWT.PUSH);
		browse.setText(Messages.IISConnectionSelectionWidget_2);
		browse.addSelectionListener(new SelectionAdapter() {

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
				dialog.setTitle(Messages.IISConnectionSelectionWidget_3);
				dialog.setMessage(Messages.IISConnectionSelectionWidget_4);
				dialog.setInput(ResourcesPlugin.getWorkspace().getRoot());
				dialog.setAllowMultiple(false);

				dialog.addFilter(new ViewerFilter() {

					@Override
					public boolean select(Viewer viewer, Object parentElement, Object element) {
						return (element instanceof IISConnection) || (element instanceof ConnectionsView.IISConnectionCategory);
					}
				});

				dialog.setValidator(new ISelectionStatusValidator() {

					@Override
					public IStatus validate(Object[] selection) {
						if (selection.length > 0) {
							Object o = selection[0];
							if (o instanceof IISConnection) {
								return new Status(IStatus.OK, Activator.PLUGIN_ID, ""); //$NON-NLS-1$
							}
						}
						return new Status(IStatus.ERROR, Activator.PLUGIN_ID, Messages.IISConnectionSelectionWidget_5);
					}

				});

				int result = dialog.open();
				if (result == ElementTreeSelectionDialog.CANCEL) {
					return;
				}
				Object[] connections = dialog.getResult();
				Object o = connections[0];
				if (o instanceof IISConnection) {
					IISConnection conn = (IISConnection) o;
					String s = conn.getName();
					iisConnectionName.setText(s);
				}

			}

		});
	}

	public Text getConnectionNameText() {
		return this.iisConnectionName;
	}

	public IISConnection getSelectedIISConnection() {
		String connName = Utils.getText(this.iisConnectionName);
		if (connName == null) {
			return null;
		}

		IISConnection conn = IISConnectionRepository.getRepository().retrieveConnection(connName);
		return conn;
	}
}
