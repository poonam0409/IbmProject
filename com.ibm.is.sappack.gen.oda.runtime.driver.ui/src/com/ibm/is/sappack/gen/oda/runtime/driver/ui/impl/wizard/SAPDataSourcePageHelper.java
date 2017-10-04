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

import java.util.Properties;

import org.eclipse.datatools.connectivity.oda.design.ui.pages.impl.DefaultDataSourcePageHelper;
import org.eclipse.datatools.connectivity.oda.design.ui.pages.impl.DefaultDataSourcePropertyPage;
import org.eclipse.datatools.connectivity.oda.design.ui.pages.impl.DefaultDataSourceWizardPage;
import org.eclipse.jface.dialogs.IMessageProvider;

import com.ibm.is.sappack.gen.oda.runtime.driver.impl.Connection;
import com.ibm.is.sappack.gen.oda.runtime.driver.ui.Messages;

/**
 * @author dsh
 *
 */
public class SAPDataSourcePageHelper extends DefaultDataSourcePageHelper {
	
	static String copyright()
    { return com.ibm.is.sappack.gen.oda.runtime.driver.ui.impl.wizard.Copyright.IBM_COPYRIGHT_SHORT; }
	
    /**
     * wizard page
     */
    private SAPDataSourceWizardPage wizardPage;

	/**
	 * @param page
	 */
	public SAPDataSourcePageHelper(DefaultDataSourceWizardPage page) {
		super(page);
		this.wizardPage = (SAPDataSourceWizardPage) page;
		this.wizardPage.disableTestButton();
	}

	/**
	 * @param page
	 */
	public SAPDataSourcePageHelper(DefaultDataSourcePropertyPage page) {
		super(page);
	}

    /* 
     * (non-Javadoc)
     * @see org.eclipse.datatools.connectivity.oda.design.ui.pages.impl.DefaultDataSourcePageHelper#validatePropertyFields()
     */
    @Override
    protected void validatePropertyFields() {
        super.validatePropertyFields();
        this.wizardPage.disableTestButton();
        
        Properties props = this.collectCustomProperties(null);
        
        String sapSystemName = (String) props.getProperty(Connection.SAP_SYSTEM_NAME_KEY);
        String useLoadBalancingString = (String) props.getProperty(Connection.USE_LOAD_BALANCING_KEY);
        
        boolean useLoadBalancing = false;
        if (null != useLoadBalancingString && useLoadBalancingString.equals(Connection.USE_LOAD_BALANCING_DISABLED_KEY)) {
        	useLoadBalancing = Boolean.FALSE;
        } else if (null != useLoadBalancingString && useLoadBalancingString.equals(Connection.USE_LOAD_BALANCING_ENABLED_KEY)) {
        	useLoadBalancing = Boolean.TRUE;
        } else if (null == useLoadBalancingString || useLoadBalancingString.trim().length() == 0) {
            this.setMessage(Messages.SAPDataSourcePageHelper_0, IMessageProvider.ERROR);
            this.setPageComplete(true);
            return;
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
            	this.setMessage(Messages.SAPDataSourcePageHelper_1, IMessageProvider.ERROR);
                this.setPageComplete(true);
                return;
            }
            
            if (null == sapSystemID  || sapSystemID.trim().length() == 0) {
            	this.setMessage(Messages.SAPDataSourcePageHelper_2, IMessageProvider.ERROR);
                this.setPageComplete(true);
                return;
            }
            
            if (null == sapGroupName || sapGroupName.trim().length() == 0) {
            	this.setMessage(Messages.SAPDataSourcePageHelper_3, IMessageProvider.ERROR);
                this.setPageComplete(true);
                return;
            }
        } else {
            if(null == sapHostName || sapHostName.trim().length() == 0) {
            	this.setMessage(Messages.SAPDataSourcePageHelper_4, IMessageProvider.ERROR);
                this.setPageComplete(true);
                return;
            }
            
            if (null == sapSystemNumber || sapSystemNumber.trim().length() == 0) {
            	this.setMessage(Messages.SAPDataSourcePageHelper_5, IMessageProvider.ERROR);
                this.setPageComplete(true);
                return;
            } else {
                try {
                	Integer.parseInt(sapSystemNumber);
                } catch (NumberFormatException nfe) {
                    this.setMessage(Messages.SAPDataSourcePageHelper_6,
                                    IMessageProvider.ERROR);
                    this.setPageComplete(true);
                    return;
                }
            }
        }
        
        if (null == sapSystemName || sapSystemName.trim().length() == 0) {
        	this.setMessage(Messages.SAPDataSourcePageHelper_7, IMessageProvider.ERROR);
            this.setPageComplete(true);
            return;
        }
        
        if (null == sapUserClientNumber || sapUserClientNumber.trim().length() == 0) {
        	this.setMessage(Messages.SAPDataSourcePageHelper_8, IMessageProvider.ERROR);
            this.setPageComplete(true);
            return;
        } else {
            try {
                if (sapUserClientNumber != null && !sapUserClientNumber.trim().equals("")) //$NON-NLS-1$
                    Integer.parseInt(sapUserClientNumber);
            } catch (NumberFormatException nfe) {
                this.setMessage(Messages.SAPDataSourcePageHelper_10,
                                IMessageProvider.ERROR);
                this.setPageComplete(true);
                return;
            }
        }
        
        if (null == sapUserName || sapUserName.trim().length() == 0) {
        	this.setMessage(Messages.SAPDataSourcePageHelper_11, IMessageProvider.ERROR);
            this.setPageComplete(true);
            return;
        }
        
        if (null == sapUserPassword || sapUserPassword.trim().length() == 0) {
        	this.setMessage(Messages.SAPDataSourcePageHelper_12, IMessageProvider.ERROR);
            this.setPageComplete(true);
            return;
        }
        
        if (null == sapUserLanguage || sapUserLanguage.trim().length() == 0) {
        	this.setMessage(Messages.SAPDataSourcePageHelper_13, IMessageProvider.ERROR);
            this.setPageComplete(true);
            return;
        }
        
        this.wizardPage.enableTestButton();
        this.setPageComplete(true);
        this.wizardPage.setMessage(Messages.SAPDataSourcePageHelper_14);
    }
	
    /* 
     * (non-Javadoc)
     * @see org.eclipse.datatools.connectivity.oda.design.ui.pages.impl.DefaultDataSourcePageHelper#collectCustomProperties(java.util.Properties)
     */
    @Override
    protected Properties collectCustomProperties(Properties props) {
        return super.collectCustomProperties(props);
    }
}
