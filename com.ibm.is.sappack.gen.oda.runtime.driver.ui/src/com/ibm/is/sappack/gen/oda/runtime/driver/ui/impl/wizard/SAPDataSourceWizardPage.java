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
// Module Name : com.ibm.is.sappack.gen.help
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.oda.runtime.driver.ui.impl.wizard;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.eclipse.datatools.connectivity.oda.design.ui.pages.impl.DefaultDataSourcePageHelper;
import org.eclipse.datatools.connectivity.oda.design.ui.pages.impl.DefaultDataSourceWizardPage;
import org.eclipse.swt.widgets.Composite;

import com.ibm.is.sappack.gen.oda.runtime.driver.impl.Connection;
import com.ibm.is.sappack.gen.oda.runtime.driver.ui.Messages;

/**
 * @author dsh
 *
 */
public class SAPDataSourceWizardPage extends DefaultDataSourceWizardPage {
	
	static String copyright()
    { return com.ibm.is.sappack.gen.oda.runtime.driver.ui.impl.wizard.Copyright.IBM_COPYRIGHT_SHORT; }

	/**
	 * @param pageName
	 */
	public SAPDataSourceWizardPage(String pageName) {
		super(pageName);
	}

    /* 
     * (non-Javadoc)
     * @see org.eclipse.datatools.connectivity.oda.design.ui.pages.impl.DefaultDataSourceWizardPage#createDataSourcePageHelper()
     */
    @Override
    protected DefaultDataSourcePageHelper createDataSourcePageHelper() {
        return new SAPDataSourcePageHelper(this);
    }

    /* 
     * (non-Javadoc)
     * @see org.eclipse.datatools.connectivity.internal.ui.wizards.BaseWizardPage#getSummaryData()
     */
    @Override
    public List<String[]> getSummaryData() {
        ArrayList<String[]> list = new ArrayList<String[]>(1);
        Properties props = ((SAPDataSourcePageHelper)this.getPageHelper()).collectCustomProperties(null);
        if (props != null) {
            String useLoadBalancingString = (String) props.getProperty(Connection.USE_LOAD_BALANCING_KEY);
            
            boolean useLoadBalancing = false;
            if (null != useLoadBalancingString && useLoadBalancingString.equals(Connection.USE_LOAD_BALANCING_DISABLED_KEY)) {
            	useLoadBalancing = Boolean.FALSE;
            } else if (null != useLoadBalancingString && useLoadBalancingString.equals(Connection.USE_LOAD_BALANCING_ENABLED_KEY)) {
            	useLoadBalancing = Boolean.TRUE;
            }
            
            if (useLoadBalancing) {
                list.add(new String[] {Messages.SAPDataSourceWizardPage_0,
                        (String) props.get(Connection.SAP_MESSAGE_SERVER_KEY)});
                list.add(new String[] {Messages.SAPDataSourceWizardPage_1,
                        (String) props.get(Connection.SAP_SYSTEM_ID_KEY)});
                list.add(new String[] {Messages.SAPDataSourceWizardPage_2,
                        (String) props.get(Connection.SAP_GROUP_NAME_KEY)});
                list.add(new String[] {Messages.SAPDataSourceWizardPage_3,
                        "true"}); //$NON-NLS-1$
                
                list.add(new String[] {Messages.SAPDataSourceWizardPage_5,
                		"N/A"}); //$NON-NLS-1$
                list.add(new String[] {Messages.SAPDataSourceWizardPage_7,
                		"N/A"}); //$NON-NLS-1$
            } else {
                list.add(new String[] {Messages.SAPDataSourceWizardPage_9,
                        (String) props.get(Connection.SAP_HOST_NAME_KEY)});
                list.add(new String[] {Messages.SAPDataSourceWizardPage_10,
                        (String) props.get(Connection.SAP_SYSTEM_NUMBER_KEY)});
                list.add(new String[] {Messages.SAPDataSourceWizardPage_11,
                        "false"}); //$NON-NLS-1$
                
                list.add(new String[] {Messages.SAPDataSourceWizardPage_13,
                        "N/A"}); //$NON-NLS-1$
                list.add(new String[] {Messages.SAPDataSourceWizardPage_15,
                		"N/A"}); //$NON-NLS-1$
                list.add(new String[] {Messages.SAPDataSourceWizardPage_17,
                		"N/A"}); //$NON-NLS-1$
            }
        	
            list.add(new String[] {Messages.SAPDataSourceWizardPage_19,
                    (String) props.get(Connection.SAP_SYSTEM_NAME_KEY)});
            list.add(new String[] {Messages.SAPDataSourceWizardPage_20,
                    (String) props.get(Connection.SAP_USER_CLIENT_NUMBER_KEY)});
            list.add(new String[] {Messages.SAPDataSourceWizardPage_21,
                    (String) props.get(Connection.SAP_USER_NAME_KEY)});
            list.add(new String[] {Messages.SAPDataSourceWizardPage_22,
                    (String) props.get(Connection.SAP_USER_PASSWORD_KEY)});
            list.add(new String[] {Messages.SAPDataSourceWizardPage_23,
                    (String) props.get(Connection.SAP_USER_LANGUAGE_KEY)});
            list.add(new String[] {Messages.SAPDataSourceWizardPage_24,
                    (String) props.get(Connection.SAP_ROUTER_STRING_KEY)});
        }
        return list;
    }
    
    /* 
     * (non-Javadoc)
     * @see org.eclipse.datatools.connectivity.oda.design.internal.ui.DataSourceWizardPageCore#isPageComplete()
     */
    @Override
    public boolean isPageComplete() {
        Properties props = this.collectCustomProperties();
        
        String sapSystemName = (String) props.getProperty(Connection.SAP_SYSTEM_NAME_KEY);
        String useLoadBalancingString = (String) props.getProperty(Connection.USE_LOAD_BALANCING_KEY);
        
        boolean useLoadBalancing = false;
        if (null != useLoadBalancingString) {
        	useLoadBalancing = Boolean.parseBoolean(useLoadBalancingString);
        }
        
        String sapHostName = (String) props.getProperty(Connection.SAP_HOST_NAME_KEY);
        String sapSystemNumber = (String) props.getProperty(Connection.SAP_SYSTEM_NUMBER_KEY);
        String sapUserClientNumber = (String) props.getProperty(Connection.SAP_USER_CLIENT_NUMBER_KEY);
        //String sapRouterString = (String) props.getProperty(Connection.SAP_ROUTER_STRING_KEY);
        String sapUserName = (String) props.getProperty(Connection.SAP_USER_NAME_KEY);
        String sapUserPassword = (String) props.getProperty(Connection.SAP_USER_PASSWORD_KEY);
        String sapUserLanguage = (String) props.getProperty(Connection.SAP_USER_LANGUAGE_KEY);
        String sapMessageServer = (String) props.getProperty(Connection.SAP_MESSAGE_SERVER_KEY);
        String sapSystemID = (String) props.getProperty(Connection.SAP_SYSTEM_ID_KEY);
        String sapGroupName = (String) props.getProperty(Connection.SAP_GROUP_NAME_KEY);
        
        if (useLoadBalancing) {
        	if (null == sapMessageServer || sapMessageServer.trim().length() == 0) {
        		return false;
        	}
        	if (null == sapSystemID  || sapSystemID.trim().length() == 0) {
        		return false;
        	}
        	if (null == sapGroupName || sapGroupName.trim().length() == 0) {
        		return false;
        	}
        } else {
        	if(null == sapHostName || sapHostName.trim().length() == 0) {
        		return false;
        	}
        	if (null == sapSystemNumber || sapSystemNumber.trim().length() == 0) {
        		return false;
        	}
        }
        
        if (null == sapSystemName || sapSystemName.trim().length() == 0) {
        	return false;
        }
        if (null == sapUserClientNumber || sapUserClientNumber.trim().length() == 0) {
        	return false;
        }
        if (null == sapUserName || sapUserName.trim().length() == 0) {
        	return false;
        }
        if (null == sapUserPassword || sapUserPassword.trim().length() == 0) {
        	return false;
        }
        if (null == sapUserLanguage || sapUserLanguage.trim().length() == 0) {
        	return false;
        }
        
        return super.isPageComplete();
    }
    
    /* 
     * (non-Javadoc)
     * @see org.eclipse.datatools.connectivity.oda.design.ui.wizards.DataSourceWizardPage#createCustomControl(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void createCustomControl(Composite parent) {
        setAutoConnectOnFinishDefault( false );
        setShowAutoConnectOnFinish( false );
        setShowAutoConnect( false );    // auto connect at workbench startup
        super.createCustomControl(parent);
    }
    
    /* 
     * (non-Javadoc)
     * @see org.eclipse.datatools.connectivity.oda.design.internal.ui.DataSourceWizardPageCore#createControl(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void createControl(Composite parent) {
        super.createControl(parent);
        this.setMessage(Messages.SAPDataSourceWizardPage_25);
    }
    

    /**
     * Disable test button
     */
    public void disableTestButton() {
        this.setPingButtonEnabled(false);
    }
    
    /**
     * Enable test button
     */
    public void enableTestButton() {
        this.setPingButtonEnabled(true);
    }
}
