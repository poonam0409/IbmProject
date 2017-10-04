//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2009, 2011                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.is.sappack.gen.common.ui.preferences
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.jco.panel;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.ibm.cic.agent.core.api.IAgent;
import com.ibm.cic.agent.core.api.IAgentJob;
import com.ibm.cic.agent.core.api.ILogger;
import com.ibm.cic.agent.core.api.IMLogger;
import com.ibm.cic.agent.core.api.IProfile;
import com.ibm.cic.agent.ui.api.IAgentUI;
import com.ibm.cic.agent.ui.extensions.CustomPanel;
import com.ibm.cic.agent.ui.extensions.ICustomPanelData;
import com.ibm.cic.common.core.model.IOffering;
import com.ibm.is.sappack.gen.jco.panel.internal.Messages;

/**
 * For more information, refer to: https://radical.rtp.raleigh.ibm.com/capilano/63796-ibm.html
 */
public class JCoClass extends CustomPanel implements IPanelConstants {
	
	private final ILogger log = IMLogger.getLogger(com.ibm.is.sappack.gen.jco.panel.JCoClass.class);
    
	private static final String JCO_JAR_LOCATION = IProfile.USER_DATA_PREFIX + "jcoJARLocation"; //$NON-NLS-1$
	private static final String JCO_NATIVE_DIR_LOCATION = IProfile.USER_DATA_PREFIX + "jcoNativeDirLocation"; //$NON-NLS-1$
    private static final String MyCustomId = "myCustomId"; // My custom parameter Id //$NON-NLS-1$
    private static final String MyOfferingId = "com.ibm.is.sappack.rapid.modeler"; // My offering id  //$NON-NLS-1$
    private Text path; // Only for sample
    
    private JCoWidgetClass widget = null;
    
	static String copyright() {
		return com.ibm.is.sappack.gen.jco.panel.Copyright.IBM_COPYRIGHT_SHORT;
	}
    
    /**
     * Default constructor
     */
    public JCoClass() {
        super(Messages.PanelName); //NON-NLS-1
		super.setDescription(Messages.PanelName);
		super.setHelpRef("com.ibm.is.sappack.gen.jco.panel.cw_help_test");
		this.log.info("JCoClass: Entered");
    }
    /**
     * Utility method to obtain IAgent instance.
     * @return IAgent instance
     */
    protected IAgent getAgent() {
        IAdaptable adaptable = this.getInitializationData();
        return (IAgent) adaptable.getAdapter(IAgent.class);
    }
    
    /**
     * Utility method to obtain FormToolkit instance, which will be used to create
     * UI components for this panel.
     * @return
     */
    protected FormToolkit getFormToolkit() {
        IAdaptable adaptable = this.getInitializationData();
        IAgentUI agentUI = (IAgentUI) adaptable.getAdapter(IAgentUI.class);
        return agentUI.getFormToolkit();
    }
    
    /**
     * TODO: Implement this method
     * 
     * @param parent Parent composite. A top level composite to hold the UI components
     * being created is already created createControl(Composite) method. In this method,
     * create only those UI components that need to be placed on this panel.
     * 
     * @param toolkit Use the toolkit to create UI components.
     */
    private void createPanelControls(Composite parent, FormToolkit toolkit) {
    	this.widget = new JCoWidgetClass(this, parent, toolkit, SWT.FILL);
    	this.widget.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
    	//this.validateComplete(this.widget.isValidationSucceeded());
    }
    
    protected void validateComplete(boolean validationSucceeded) {
    	Map<String, String> map = new HashMap<String, String>();
    	
    	map.put(JCoClass.JCO_JAR_LOCATION, this.widget.getSelectedJavaArchive().trim());
    	map.put(JCoClass.JCO_NATIVE_DIR_LOCATION, this.widget.getSelectedDirectory().trim());
    	
        ICustomPanelData data = this.getCustomPanelData();
        IAgentJob[] jobs = data.getAllJobs();
        
        IOffering myOffering = Util.findOffering(jobs, JCoClass.MyOfferingId);
        IStatus status = this.getAgent().validateOfferingUserData(myOffering, map);
        
        if (status.isOK()) {
        	// *** Save the user's input in the profile
        	IProfile profile = data.getProfile();
        	profile.setOfferingUserData(JCoClass.JCO_JAR_LOCATION, this.widget.getSelectedJavaArchive().trim(), JCoClass.MyOfferingId);
        	profile.setOfferingUserData(JCoClass.JCO_NATIVE_DIR_LOCATION, this.widget.getSelectedDirectory().trim(), JCoClass.MyOfferingId);
        	
            if (validationSucceeded) {
            	setErrorMessage(null);
            	setPageComplete(true);
        	} else {
        		setErrorMessage(Messages.JCoClass_4); //$NON-NLS-1$
        		setPageComplete(false);
        	}
        } else {
            setErrorMessage(status.getMessage());
            setPageComplete(false);
        }
    }
    
    /**
     * @see com.ibm.cic.agent.ui.extensions.BaseWizardPanel#performFinish(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public IStatus performFinish(IProgressMonitor monitor) {
        // TODO: Implement perform finish
        return Status.OK_STATUS;
    }
    /**
     * @see com.ibm.cic.agent.ui.extensions.CustomPanel#createControl(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void createControl(Composite parent) {
        FormToolkit toolkit = this.getFormToolkit();
        Composite topContainer = toolkit.createComposite(parent);
        topContainer.setLayout(new GridLayout());
        topContainer
                .setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        this.createPanelControls(topContainer, toolkit);
        this.setControl(topContainer);
    }
    
}