/*
 *+------------------------------------------------------------------------+
 *| Licensed Materials - Property of IBM                                   |
 *| (C) Copyright IBM Corp. 2012.  All Rights Reserved.                    |
 *|                                                                        |
 *| US Government Users Restricted Rights - Use, duplication or disclosure |
 *| restricted by GSA ADP Schedule Contract with IBM Corp.                 |
 *+------------------------------------------------------------------------+
 */
package com.ibm.is.sappack.gen.jco.validation;

import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.ibm.cic.agent.core.api.ILogger;
import com.ibm.cic.agent.core.api.IMLogger;
import com.ibm.cic.agent.core.api.IMStatuses;
import com.ibm.cic.common.core.model.UserDataValidator;

public class JCoPropertyValidator extends UserDataValidator {
	
	private final ILogger log = IMLogger.getLogger(com.ibm.is.sappack.gen.jco.validation.JCoPropertyValidator.class);

    /**
     * @see com.ibm.cic.common.core.model.UserDataValidator#shouldSkipValidation(java.util.Map)
     */
    @Override
    public boolean shouldSkipValidation(@SuppressWarnings("rawtypes") Map map) {
        // TODO: Provide your implementation
        return false;
    }

    /**
     * @see com.ibm.cic.common.core.model.UserDataValidator#validateUserData(java.util.Map)
     */
    @Override
    public IStatus validateUserData(@SuppressWarnings("rawtypes") Map map) {
        // TODO: Provide your implementation
        String key = "dataKey"; // Your data key //$NON-NLS-1$
        Object obj = map.get(key);
        if(obj instanceof String == false) {
            // This should not happen since the map should contain key-value pairs.

            // New Method for reporting status for bundles that target IM 1.5 and above
            // See https://radical.rtp.raleigh.ibm.com/capilano/97534-ibm.html for more details

            return IMStatuses.ERROR.get("CRRSE1234E",    /* Example UID, use actual uid in your code */
                "Data type received was not valid",      /* Explanation of the error message */
                "Enter valid data.",                     /* User action for the error message */
                0, /* Not used by the Installation Manager.  If the custom panel which calls this validator would like to failure conditions, you can pass a non-zero value for this parameter. Otherwise, simply pass 0.*/ 
                "Obtained invalid data type");  
 
            // Old Method for Status 
            //return new Status(IStatus.ERROR, key, "Obtained invalid data type."); //$NON-NLS-1$
        }
        String val = (String) obj;
        
        // Sample verification: val is an integer value
        try {
            Integer.parseInt(val);
        } catch(NumberFormatException ex) {

            // New Method for reporting status for bundles that target IM 1.5 and above
            // See https://radical.rtp.raleigh.ibm.com/capilano/97534-ibm.html for more details

            return IMStatuses.ERROR.get("CRRSE1234E",    /* Example UID, use actual uid in your code */
                "You have put in a non integer value.",  /* Explanation of the error message */
                "You must put in an integer value.",     /* User action for the error message */
                0, /* Not used by the Installation Manager.  If the custom panel which calls this validator would like to enumerate failure conditions, you can pass a non-zero value for this parameter. Otherwise, simply pass 0.*/ 
                ex,                                      /* The exception that needs to be attached to the status */
                "Value {0} should be an integer", key);  

            // Old Method for Status 
            //return new Status(IStatus.ERROR, key, "Value should be an integer."); //$NON-NLS-1$
        }
        return Status.OK_STATUS;
    }
    
}