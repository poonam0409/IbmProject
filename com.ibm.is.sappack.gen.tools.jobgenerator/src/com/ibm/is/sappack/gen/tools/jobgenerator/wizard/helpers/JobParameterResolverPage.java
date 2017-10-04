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
// Module Name : com.ibm.is.sappack.gen.tools.jobgenerator.wizard.helpers
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.tools.jobgenerator.wizard.helpers;


import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import com.ibm.iis.sappack.gen.common.ui.connections.IISConnection;
import com.ibm.iis.sappack.gen.common.ui.wizards.INextActionWizardPage;
import com.ibm.iis.sappack.gen.tools.jobgenerator.rgconf.JobParameter;
import com.ibm.iis.sappack.gen.tools.jobgenerator.rgconf.ParameterPage;
import com.ibm.iis.sappack.gen.tools.jobgenerator.rgconf.RGConfiguration;
import com.ibm.is.sappack.gen.common.JobGeneratorException;
import com.ibm.is.sappack.gen.common.request.GetAllParameterSetsRequest;
import com.ibm.is.sappack.gen.common.request.GetAllParameterSetsResponse;
import com.ibm.is.sappack.gen.common.request.JobParamData;
import com.ibm.is.sappack.gen.common.request.ServerRequestUtil;
import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.common.ui.util.DSProjectUtils;
import com.ibm.is.sappack.gen.common.util.DSParamSet;
import com.ibm.is.sappack.gen.tools.jobgenerator.Activator;
import com.ibm.is.sappack.gen.tools.sap.constants.Constants;


/**
 * JobParameterResolverPage
 * 
 * Allows the user to enter values for 
 * job parameters before the validation starts
 */
public abstract class JobParameterResolverPage extends WizardPage implements INextActionWizardPage{
   private static final String HELP_ID = Activator.PLUGIN_ID + "." + "JobParameterResolverPage"; //$NON-NLS-1$ //$NON-NLS-2$
   
	
	/* table viewer for job parameter table */
	private TableViewer          tableViewer;

    private Button               serverJobParameterSetLoadButton;
    
    /* IIS connection and DataStage project */
    private IISConnection connection;
    private String project;

	private RGConfiguration configuration;
	
	/* columns for job parameter table */
	public static final int COLUMN_PARAMETER_NAME  = 0;
	public static final int COLUMN_PARAMETER_VALUE = 1;
	
	public static final String COLUMN_NAME  = Messages.JobParameterResolverPage_0;
	public static final String COLUMN_VALUE = Messages.JobParameterResolverPage_1;
	

	/**
	 * JobParameterResolverPage
	 * 
	 * @param configuration
	 */
	public JobParameterResolverPage(RGConfiguration configuration){
		super(Messages.JobParameterResolverPage_2);
		this.setDescription(Messages.JobParameterResolverPage_3);
		this.setTitle(Messages.JobParameterResolverPage_4);
		this.configuration = configuration;
	}
	
	/**
	 * setIISConnection
	 * 
	 * @param connection
	 */
	public void setIISConnection(IISConnection connection) {
		this.connection = connection;
	}
	
	/**
	 * setDSProject
	 * 
	 * @param dsProject
	 */
	public void setDSProject(String dsProject) {
		this.project = dsProject;
	}

	static String copyright() { 
	   return com.ibm.is.sappack.gen.tools.jobgenerator.wizard.helpers.Copyright.IBM_COPYRIGHT_SHORT; 
	}	

	@Override
	public void createControl(Composite parent) {
		
		Composite container = new Composite(parent, SWT.NONE);
		
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		container.setLayout(gridLayout);
		
		/* create job parameters table */
		this.tableViewer = new TableViewer(container, SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION);
		GridData gridDataList = new GridData(GridData.FILL_BOTH);
		this.tableViewer.getTable().setLayoutData(gridDataList);
		
		TableViewerColumn tableColumnName = new TableViewerColumn(this.tableViewer, SWT.LEFT);
		tableColumnName.getColumn().setText(COLUMN_NAME);
		tableColumnName.getColumn().setWidth(100);

		TableViewerColumn tableColumnValue = new TableViewerColumn(this.tableViewer, SWT.LEFT);
		tableColumnValue.getColumn().setText(COLUMN_VALUE);
		tableColumnValue.getColumn().setWidth(250);
		tableColumnValue.setEditingSupport(new JobParameterDefaultValuesEditingSupport(this.tableViewer, JobParameterStringValuesEditingSupport.EDIT_DEFAULT, this));
		
		this.tableViewer.getTable().setHeaderVisible(true);
		this.tableViewer.getTable().setLinesVisible(true);
		
		JobParameterResolverProvider contentProvider = new JobParameterResolverProvider();
		this.tableViewer.setColumnProperties(new String[] { COLUMN_NAME, COLUMN_VALUE });
		this.tableViewer.setContentProvider(contentProvider);
		this.tableViewer.setLabelProvider(contentProvider);
		this.tableViewer.setInput(ParameterPage.loadParameters(configuration, true));
		
      this.serverJobParameterSetLoadButton = new Button(container, SWT.PUSH);
      GridData buttonData = new GridData(GridData.END);
      this.serverJobParameterSetLoadButton.setLayoutData(buttonData);
      this.serverJobParameterSetLoadButton.setText(Messages.JobParameterResolverPage_5);
      this.serverJobParameterSetLoadButton.addSelectionListener(new SelectionAdapter() {
         public void widgetSelected(SelectionEvent e) {
            Display display = getShell().getDisplay();
            if (display == null || display.isDisposed()) {
               Activator.getLogger().log(Level.SEVERE, 
                                         "error loading display from shell() (it's probably disposed !?!?)"); //$NON-NLS-1$
            } else {
               Runnable runnable = new Runnable()
               {
                  public void run()
                  {
                     updateParamValuesWithServerData();
                  }
               };
               display.syncExec(runnable);
            }
         } // end of if (display == null || display.isDisposed())
      }); // end of new SelectionAdapter()
//      this.serverJobParameterSetLoadButton.addKeyListener(keyListener);
		
		setControl(container);
		// hansx ==> ValidationResultPage
	}
	
	private void updateParamValuesWithServerData() {
	   
		
	   if (this.connection != null) {
	      
	      /* get Parameter Sets from InfoServer  */
	      String isDomainServer = this.connection.getDomain();
	      int isPort = this.connection.getDomainServerPort();
	      String isUser = this.connection.getUser();
	      String isUserPw = this.connection.getPassword();
	      String isProject = this.project;
	      
	      GetAllParameterSetsRequest req = new GetAllParameterSetsRequest();
	      req.setDomainServerName(isDomainServer);
	      req.setDomainServerPort(isPort);
	      req.setISUsername(isUser);
	      req.setISPassword(isUserPw);
	      req.setDSProjectName(isProject);
	      
         // update request with project name, DS hostname and DS RPC port 
         // (possibly contained in the project name)
         DSProjectUtils.updateRequestData(isProject, req);
         
	      String errMsg = null;
	      GetAllParameterSetsResponse resp = null;
	      try {
	         resp = (GetAllParameterSetsResponse) ServerRequestUtil.send(req);
	         
	         if (resp.containsErrors()) {
	            Activator.getLogger().log(Level.SEVERE, resp.getDetailedInfo());
	            errMsg = resp.get1stMessage();
	         }
	      }
	      catch(JobGeneratorException jobGenExcpt) {
	         errMsg = jobGenExcpt.getLocalizedMessage();
	         Activator.getLogger().log(Level.SEVERE, jobGenExcpt.getMessage(), jobGenExcpt);
	      }
	      if (errMsg == null) {
            DSParamSet              curParamSet;
            JobParamData            curJobParam;
            String                  jobParamName;
            String                  paramSetName;
            Map<?,?> paramSetsMap;
            
            paramSetsMap = resp.getParameterSetsAsMap();
            
	         // search for ParameterSet name in JobParam ...
	         int lineCount = tableViewer.getTable().getItemCount();
	         for (int itemIdx = 0; itemIdx < lineCount; itemIdx ++) {
	            JobParameter jobParam = (JobParameter) tableViewer.getElementAt(itemIdx);
	            jobParamName = jobParam.getName();
	            if (jobParamName != null) {
	               // check if the name includes a ParameterSet name
	               int startIdx = jobParamName.indexOf('.');
	               if (startIdx > -1) {
	                  
	                  paramSetName = jobParamName.substring(0, startIdx);
	                  jobParamName = jobParamName.substring(startIdx + 1);
	                  
	                  // check if the name exists in the map
	                  curParamSet = (DSParamSet) paramSetsMap.get(paramSetName);
	                  if (curParamSet != null) {
	                     // check if the name is found in the server's param list ...
	                     curJobParam = (JobParamData) curParamSet.getParams().get(jobParamName);

	                     // if the job parameter was found ...
	                     if (curJobParam != null) {
	                        // ==> update the value in the table !!!
	                        jobParam.setDefaultValue(curJobParam.getDefaultValue());
	                        tableViewer.update(jobParam, null);
	                     }
	                  } // end of if (curParamSet != null)
	               } // end of if (startIdx > -1)
	            } // end of if (jobParamName != null)
	         } // end of for (int itemIdx = 0; itemIdx < lineCount; itemIdx ++)
	      }
	      else {
	         // an error occurred ==> show error message in a message box
	         MessageDialog.openError(getShell(), Messages.TitleError, errMsg);
	      }
	   } // end of if (this.infoServerDetailsPage != null)
      
	}
	
	
	@Override
	public void performHelp() {
      PlatformUI.getWorkbench().getHelpSystem().displayHelp(HELP_ID);
	}
	
	
	/**
	 * getParameterResolver
	 * 
	 * create and fill a ParameterResolver
	 * object filled with the content of the 
	 * JobParameterResoverPage table GUI
	 * 
	 * @return parameter resolver
	 */
	public ParameterResolver getParameterResolver() {
		
		@SuppressWarnings("unchecked")
		List<JobParameter> content = (List<JobParameter>) this.tableViewer.getInput();
		
		ParameterResolver pr = new ParameterResolver();
		Iterator<JobParameter> it = content.iterator();
		
		while(it.hasNext()) {
			JobParameter jobParameter = it.next();
			
			pr.put(Constants.JOB_PARAM_SEPARATOR + jobParameter.getName() + Constants.JOB_PARAM_SEPARATOR,
			       jobParameter.getDefaultValue());
		}
		
		return pr;
	}

	@Override
	final public boolean nextPressed() {
		boolean result = nextPressedImpl();
		if (!result) {
			return false;
		}
		return true;
	}
	
	public abstract boolean nextPressedImpl();
	
}
