//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2014                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.iis.sappack.gen.tools.jobgenerator.rgconf
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.iis.sappack.gen.tools.jobgenerator.rgconf;


import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.logging.Level;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;

import com.ibm.iis.sappack.gen.common.ui.connections.IISConnection;
import com.ibm.iis.sappack.gen.common.ui.editors.ConfigurationBase;
import com.ibm.iis.sappack.gen.common.ui.editors.EditorEvent;
import com.ibm.iis.sappack.gen.common.ui.editors.IControlFactory;
import com.ibm.iis.sappack.gen.common.ui.editors.PropertyInfo;
import com.ibm.iis.sappack.gen.common.ui.editors.PropertyInfoCollection;
import com.ibm.iis.sappack.gen.common.ui.util.Utils;
import com.ibm.is.sappack.gen.common.Constants;
import com.ibm.is.sappack.gen.common.JobGeneratorException;
import com.ibm.is.sappack.gen.common.request.GetAllParameterSetsRequest;
import com.ibm.is.sappack.gen.common.request.GetAllParameterSetsResponse;
import com.ibm.is.sappack.gen.common.request.ServerRequestUtil;
import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.common.ui.WidgetIDConstants;
import com.ibm.is.sappack.gen.common.ui.WidgetIDUtils;
import com.ibm.is.sappack.gen.common.ui.util.DSProjectUtils;
import com.ibm.is.sappack.gen.common.util.DSParamSet;
import com.ibm.is.sappack.gen.tools.jobgenerator.Activator;


public class ParameterPage extends RGConfPageBase {
	
	private TableViewer        tableViewer;
	//	private JobParametersContentProvider contentProvider;
	private Button             checkButton;
	private List<JobParameter> paramList;
	
	public static final String PROP_PARAMETER_PFX = "PARAMETER_PFX_"; //$NON-NLS-1$
	public static final String PROP_PARAMETER_NUM = "PARAMETER_NUM"; //$NON-NLS-1$
	public static final String TABNAME = Messages.ParameterPage_0;


  	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	

	public ParameterPage() {
		super(TABNAME, Messages.ParameterPage_1, Messages.ParameterPage_2,
		      Utils.getHelpID("rgconfeditor_job_parameters")); //$NON-NLS-1$
		this.paramList = new ArrayList<JobParameter>();
	}

	@Override
	public void createControls(IControlFactory controlFactory, Composite parent) {
		Composite jobParametersGroup = controlFactory.createGroup(parent, Messages.JobGeneratorJobParametersPage_10, SWT.NONE);
		GridLayout gridLayout = new GridLayout(1, false);
		jobParametersGroup.setLayout(gridLayout);
		jobParametersGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		loadParameters();

		createTable(jobParametersGroup);

		createButtons(jobParametersGroup);

	}

	class ParameterContentProvider implements IStructuredContentProvider {

		@Override
		public void dispose() {
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

		@Override
		public Object[] getElements(Object inputElement) {
			Object[] os = loadParameters(editor.getConfiguration(), false).toArray();
			return os;
		}

	}

	class ParameterLabelProvider implements ITableLabelProvider {

		@Override
		public void addListener(ILabelProviderListener listener) {
		}

		@Override
		public void dispose() {
		}

		@Override
		public boolean isLabelProperty(Object element, String property) {
			return false;
		}

		@Override
		public void removeListener(ILabelProviderListener listener) {
		}

		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		@Override
		public String getColumnText(Object element, int columnIndex) {
			if (element instanceof JobParameter) {
				JobParameter jobParameter = (JobParameter) element;
				switch (columnIndex) {
				case 0:
					return jobParameter.getName();
				case 1:
					return jobParameter.getPrompt();
				case 2:
					return jobParameter.getType();
				case 3:
					if (jobParameter.getType().equals(JobParameter.TYPE_NAME_ENCRYPTED)) {
						return jobParameter.getDefaultValue().replaceAll(".", "*"); //$NON-NLS-1$//$NON-NLS-2$
					} else {
						return jobParameter.getDefaultValue();
					}
				case 4:
					return jobParameter.getHelp();
				default:
					return null;
				}

			}
			return null;
		}

	}

	private void createTable(Composite composite) {

		this.tableViewer = new TableViewer(composite, SWT.FULL_SELECTION);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL);
		Table table = this.tableViewer.getTable();
		table.setLayoutData(gridData);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		WidgetIDUtils.assignID(table, WidgetIDConstants.GEN_JOBPARAMETERSTABLE);

		Observer tableEditorObserver = new Observer() {

			@Override
			public void update(Observable o, Object arg) {
				saveParameters();

			}
		};

		TableViewerColumn nameColumn = new TableViewerColumn(this.tableViewer, SWT.LEFT);
		nameColumn.getColumn().setText(Messages.JobGeneratorJobParametersPage_11);
		nameColumn.getColumn().setWidth(100);
		/*
		nameColumn.setEditingSupport( //new JobParameterStringValuesEditingSupport(this.tableViewer, JobParameterStringValuesEditingSupport.EDIT_NAME));
				new StringEditingSupport(tableViewer) {
					
					@Override
					protected void setStringValue(JobParameter param, String value) {
						param.setName(value);
					}
					
					@Override
					protected String getStringValue(JobParameter param) {
						return param.getName();
					}
				} );
		*/

		TableViewerColumn promptColumn = new TableViewerColumn(this.tableViewer, SWT.LEFT);
		promptColumn.getColumn().setText(Messages.JobGeneratorJobParametersPage_12);
		promptColumn.getColumn().setWidth(100);
		SimpleParameterEditingSupport promptEditingSupport = new SimpleParameterEditingSupport(tableViewer, false) {

			@Override
			protected boolean canEdit(Object element) {
				JobParameter param = (JobParameter) element;

				// !!!! parameter set parameters must not be edited !!!! 
				return(!param.isParameterSet());
			}

			@Override
			protected void setStringValue(JobParameter param, String value) {
				param.setPrompt(value);
			}

			@Override
			protected String getStringValue(JobParameter param) {
				return param.getPrompt();
			}
		};
		promptEditingSupport.addObserver(tableEditorObserver);
		promptColumn.setEditingSupport(//new JobParameterStringValuesEditingSupport(this.tableViewer, JobParameterStringValuesEditingSupport.EDIT_PROMPT));
				promptEditingSupport);

		TableViewerColumn typeColumn = new TableViewerColumn(this.tableViewer, SWT.LEFT);
		typeColumn.getColumn().setText(Messages.JobGeneratorJobParametersPage_13);
		typeColumn.getColumn().setWidth(100);
//		ObservableEditingSupport typeEditingSupport = new JobParameterTypeEditingSupport(this.tableViewer);
		ObservableEditingSupport typeEditingSupport = new JobParameterTypeEditingSupport(this.tableViewer) {

			@Override
			protected boolean canEdit(Object element) {
				JobParameter param = (JobParameter) element;

				// !!!! parameter set parameters must not be edited !!!! 
				return(!param.isParameterSet());
			}
		};
		typeColumn.setEditingSupport(typeEditingSupport);
		typeEditingSupport.addObserver(tableEditorObserver);

		TableViewerColumn defaultColumn = new TableViewerColumn(this.tableViewer, SWT.LEFT);
		defaultColumn.getColumn().setText(Messages.JobGeneratorJobParametersPage_14);
		defaultColumn.getColumn().setWidth(100);
		SimpleParameterEditingSupport defaultEditingSupport = new SimpleParameterEditingSupport(tableViewer, true) {

			@Override
			protected boolean canEdit(Object element) {
				JobParameter param = (JobParameter) element;

				// !!!! parameter set parameters must not be edited !!!! 
				return(!param.isParameterSet());
			}

			@Override
			protected void setStringValue(JobParameter param, String value) {
				param.setDefaultValue(value);
			}

			@Override
			protected String getStringValue(JobParameter param) {
				return param.getDefaultValue();
			}

		};

		defaultColumn.setEditingSupport(//new JobParameterStringValuesEditingSupport(this.tableViewer, JobParameterStringValuesEditingSupport.EDIT_HELP));
				//new JobParameterDefaultValuesEditingSupport(this.tableViewer, JobParameterStringValuesEditingSupport.EDIT_DEFAULT));

				defaultEditingSupport);
		defaultEditingSupport.addObserver(tableEditorObserver);

		TableViewerColumn helpColumn = new TableViewerColumn(this.tableViewer, SWT.LEFT);
		helpColumn.getColumn().setText(Messages.JobGeneratorJobParametersPage_15);
		helpColumn.getColumn().setWidth(100);
		SimpleParameterEditingSupport helpEditingSupport = new SimpleParameterEditingSupport(tableViewer, false) {

			@Override
			protected boolean canEdit(Object element) {
				JobParameter param = (JobParameter) element;

				// !!!! parameter set parameters must not be edited !!!! 
				return(!param.isParameterSet());
			}

			@Override
			protected void setStringValue(JobParameter param, String value) {
				param.setHelp(value);
			}

			@Override
			protected String getStringValue(JobParameter param) {
				return param.getHelp();
			}
		};
		helpColumn.setEditingSupport( // new JobParameterStringValuesEditingSupport(this.tableViewer, JobParameterStringValuesEditingSupport.EDIT_HELP));
				helpEditingSupport);
		helpEditingSupport.addObserver(tableEditorObserver);

		this.tableViewer.setContentProvider(new ParameterContentProvider());
		this.tableViewer.setLabelProvider(new ParameterLabelProvider());
		this.tableViewer.setInput(this.paramList);
	}

	
	private void createButtons(Composite composite) {
		Composite buttonsComposite = new Composite(composite, SWT.NONE);
		GridLayout buttonsLayout = new GridLayout(3, false);
		buttonsComposite.setLayout(buttonsLayout);

		//		Button addButton = new Button(buttonsComposite, SWT.NONE);
		//		addButton.setText(Messages.JobGeneratorJobParametersPage_16);
		//		addButton.setToolTipText(Messages.JobGeneratorJobParametersPage_16);
		//		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		//		gridData.widthHint = 80;
		//		addButton.setLayoutData(gridData);
		//		WidgetIDUtils.assignID(addButton, WidgetIDConstants.GEN_JOBPARAMETERSADDBUTTON);
		//		addButton.addSelectionListener(new SelectionAdapter() {
		//
		//			@Override
		//			public void widgetSelected(SelectionEvent e) {
		//
		//				// Before we allow the user to create a new entry, we validate
		//				// the existing ones.
		//				JobParameter jobParameter = (JobParameter) ((IStructuredSelection) ParameterPage.this.tableViewer.getSelection()).getFirstElement();
		//				if (jobParameter != null) {
		//					if (!ParameterPage.this.rowChangedListener.validate(ParameterPage.this.tableViewer, jobParameter)) {
		//						return;
		//					}
		//				}
		//
		//				String newDataType = JobParameterTypeEditingSupport.getFirstDataType();
		//				JobParameter newJobParameter = new JobParameter("", "", newDataType, "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		//				newJobParameter.setUserCreated();
		//
		//				ParameterPage.this.contentProvider.addJobParameter(newJobParameter);
		//				ParameterPage.this.tableViewer.refresh();
		//				// JobGeneratorCustomDerivationsPage.this.tableViewer.setSelection(new
		//				// StructuredSelection(newMapping));
		//				ParameterPage.this.tableViewer.editElement(newJobParameter, 0);
		//
		//				Table table = ParameterPage.this.tableViewer.getTable();
		//				boolean isButtonEnabled = (table.getItemCount() > 0);
		//				checkButton.setEnabled(isButtonEnabled);
		//				removeButton.setEnabled(isButtonEnabled);
		//			} // end of widgetSelected()
		//		});
		//
		//		removeButton = new Button(buttonsComposite, SWT.NONE);
		//		removeButton.setText(Messages.JobGeneratorJobParametersPage_21);
		//		removeButton.setToolTipText(Messages.JobGeneratorJobParametersPage_21);
		//		gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		//		gridData.widthHint = 80;
		//		removeButton.setLayoutData(gridData);
		//		WidgetIDUtils.assignID(removeButton, WidgetIDConstants.GEN_JOBPARAMETERSREMOVEBUTTON);
		//		removeButton.addSelectionListener(new SelectionAdapter() {
		//
		//			@Override
		//			public void widgetSelected(SelectionEvent e) {
		//				ISelection selection = ParameterPage.this.tableViewer.getSelection();
		//				if (selection instanceof IStructuredSelection) {
		//					IStructuredSelection structuredSelection = (IStructuredSelection) selection;
		//					Iterator iterator = structuredSelection.iterator();
		//					while (iterator.hasNext()) {
		//						JobParameter jobParameter = (JobParameter) iterator.next();
		//
		//						boolean deleteParam = true;
		//						// warn user when deleting a non-user-created job parameter
		//						if (!jobParameter.isUserCreated()) {
		//							deleteParam = MessageDialog.openQuestion(null, Messages.TitleWarning, Messages.JobGeneratorJobParametersPage_WarnDelete);
		//						}
		//
		//						if (deleteParam) {
		//							ParameterPage.this.contentProvider.removeJobParameter(jobParameter);
		//						}
		//					}
		//					ParameterPage.this.tableViewer.refresh();
		//					Table table = ParameterPage.this.tableViewer.getTable();
		//					int itemCount = table.getItemCount();
		//					table.select(itemCount - 1);
		//
		//					if (itemCount < 1) {
		//						checkButton.setEnabled(false);
		//						removeButton.setEnabled(false);
		//					}
		//				}
		//			} // end of widgetSelected()
		//		});

		this.checkButton = new Button(buttonsComposite, SWT.NONE);
		this.checkButton.setText(Messages.JobGeneratorJobParametersPage_17);
		this.checkButton.setToolTipText(Messages.JobGeneratorJobParametersPage_17);
		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		//gridData.widthHint = 140;
		this.checkButton.setLayoutData(gridData);

		WidgetIDUtils.assignID(this.checkButton, WidgetIDConstants.GEN_JOBPARAMETERSCHECKBUTTON);
		this.checkButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Display display = Display.getCurrent();
				if (display == null || display.isDisposed()) {
					Activator.getLogger().log(Level.SEVERE, "error loading display from shell() (it's probably disposed !?!?)"); //$NON-NLS-1$
				} else {
					Runnable runnable = new Runnable() {
						public void run() {

							ValidateAgainstIISDialog dialog = new ValidateAgainstIISDialog(null, true) {
								
								@Override
								protected String performValidationAction(IISConnection connection, String dsProject, IProgressMonitor monitor) {
									return checkJobParamsWithServerData(connection, dsProject);
								}
							};
							dialog.open();
						}
					};
					display.syncExec(runnable);
				}
			} // end of widgetSelected()

		});

		// Button printButton = new Button(buttonsComposite, SWT.NONE);
		// printButton.setText("print");
		// printButton.addSelectionListener(new SelectionAdapter() {
		// @Override
		// public void widgetSelected(SelectionEvent e) {
		// contentProvider.print();
		// }
		// });
	}

	private String checkJobParamsWithServerData(IISConnection connection, String dsProject) {
		
		StringBuffer resultBuf = new StringBuffer();
		resultBuf.append(Messages.JobGeneratorJobParametersPage_22 + Constants.NEWLINE + Constants.NEWLINE);

		GetAllParameterSetsRequest req = new GetAllParameterSetsRequest();

		IISConnection.initializeRequest(req, connection);
		
		req.setDSProjectName(dsProject);

		// update request with project name, DS hostname and DS RPC port 
		// (possibly contained in the project name)
		DSProjectUtils.updateRequestData(dsProject, req);

		String errMsg = null;
		GetAllParameterSetsResponse resp = null;
		try {
			resp = (GetAllParameterSetsResponse) ServerRequestUtil.send(req);

			if (resp.containsErrors()) {
				Activator.getLogger().log(Level.SEVERE, resp.getDetailedInfo());
				errMsg = resp.get1stMessage();
			}
		} catch (JobGeneratorException jobGenExcpt) {
			errMsg = jobGenExcpt.getLocalizedMessage();
			Activator.getLogger().log(Level.SEVERE, jobGenExcpt.getMessage(), jobGenExcpt);
		}

		if (errMsg == null) {
			DSParamSet curParamSet;
			String jobParamName;
			String paramSetName;
			StringBuffer errMsgBuf;
			String tmpMsg;
			List<JobParameter> curJobParamsList;
			Map<?, ?> existingParamSetsMap;

			// get existing parameter sets ...
			existingParamSetsMap = resp.getParameterSetsAsMap();

			// get current parameter (set) that are in use 
			curJobParamsList = this.paramList;
			errMsgBuf = new StringBuffer();
			for (JobParameter jobParam : curJobParamsList) {

				jobParamName = jobParam.getName();
				if (jobParamName != null) {
					// check if the name includes a ParameterSet name
					int startIdx = jobParamName.indexOf('.');
					if (startIdx > -1) {

						paramSetName = jobParamName.substring(0, startIdx);
						jobParamName = jobParamName.substring(startIdx + 1);

						// check if the name exists in the map
						curParamSet = (DSParamSet) existingParamSetsMap.get(paramSetName);
						if (curParamSet == null) {
							tmpMsg = MessageFormat.format(Messages.JobGeneratorJobParametersPage_23, new Object[] { paramSetName });
							errMsgBuf.append(tmpMsg);
							errMsgBuf.append(Constants.NEWLINE);
						} else {
							// parameter set exists ==> check if parameter exists
							if (curParamSet.getParams().get(jobParamName) == null) {
								tmpMsg = MessageFormat.format(Messages.JobGeneratorJobParametersPage_24, new Object[] { jobParamName, paramSetName });

								errMsgBuf.append(tmpMsg);
								errMsgBuf.append(Constants.NEWLINE);
							}
						} // end of (else) if (curParamSet == null)
					} // end of if (startIdx > -1)
				} // end of if (jobParamName != null)
			} // end of for(JobParameter jobParam: curJobParamsList)

			if (errMsgBuf.length() > 0) {
				tmpMsg = errMsgBuf.toString();
			} else {
				tmpMsg = Messages.JobGeneratorJobParametersPage_25;
			}
			resultBuf.append(tmpMsg);
		} else {
			resultBuf.append(errMsg);
		}

		return resultBuf.toString();
	}

	private void saveParameters() {
		ConfigurationBase map = editor.getConfiguration();
		map.put(PROP_PARAMETER_NUM, this.paramList.size());
		for (int i = 0; i < this.paramList.size(); i++) {
			JobParameter p = this.paramList.get(i);
			map.put(PROP_PARAMETER_PFX + i, p.convertToString());
		}
	}

	private void loadParameters() {
		this.paramList.clear();
		this.paramList.addAll(loadParameters(editor.getConfiguration(), true));
	}
	
	public static List<JobParameter> loadParameters(ConfigurationBase map, boolean expandParameterSets) {
		List<JobParameter> expandedParams = new ArrayList<JobParameter>();
		int n = map.getInt(PROP_PARAMETER_NUM);
		for (int i = 0; i < n; i++) {
			String s = map.get(PROP_PARAMETER_PFX + i);
			JobParameter param = JobParameter.createFromString(s);
			expandedParams.add(param);
		}
		if (expandParameterSets) {
			return expandedParams;
		}
		Set<JobParameter> sets = new HashSet<JobParameter>();
		List<JobParameter> unexpanded = new ArrayList<JobParameter>();
		for (JobParameter param : expandedParams) {
			String name = param.getName();
			int ix = name.indexOf('.');
			if (ix != -1) {
				String psName = name.substring(0, ix);
				/* TODO check if we need to localize the '(As pre-defined)' string on non english Information Server installations */
				JobParameter set = new JobParameter(psName, psName, JobParameter.TYPE_NAME_PARAMETER_SET, Messages.ParameterPage_3, ""); //$NON-NLS-1$
				if (!sets.contains(set)) {
					sets.add(set);
				}
			} else {
				unexpanded.add(param);
			}
		}
		unexpanded.addAll(sets);
		return unexpanded;
	}
	

	@Override
	public void update(EditorEvent event) {
		try {
			super.update(event);
			if (event instanceof JobParamChangedEvent) {

				Collection<JobParameter> newParams = new HashSet<JobParameter>();
				List<JobParameter> parametersToBeRemoved = new ArrayList<JobParameter>(this.paramList);
				PropertyInfoCollection propInfoCol = ((RGConfiguration)editor.getConfiguration()).createPropertyInfoCollection();
				List<String> parameterProperties = this.getPropertyNamesContainingParameters();
				for (String prop : parameterProperties) {

					PropertyInfo propInfo = propInfoCol.getPropertyInfo(prop);
					if (propInfo == null) {
						continue;
					}
					String type = propInfo.getType();

					String s = editor.getConfiguration().get(prop);
					String[] params = Utils.getJobParameters(s);
					for (String param : params) {
						boolean found = false;
						for (JobParameter p : paramList) {
							if (p.getName() == null) {
								Activator.getLogger().log(Level.INFO, Messages.ParameterPage_5);
								continue;
							}
							if (p.getName().equals(param)) {
								found = true;
								parametersToBeRemoved.remove(p);
								break;
							}
						}
						if (!found) {
							JobParameter newParam = new JobParameter(param, param, type, "", ""); //$NON-NLS-1$ //$NON-NLS-2$
							newParams.add(newParam);
						}
					}
				}
				this.paramList.removeAll(parametersToBeRemoved);
				this.paramList.addAll(newParams);
				saveParameters();
				this.tableViewer.refresh();
			}
		} catch(Exception exc) {
			Activator.logException(exc);
		}
	}
}
