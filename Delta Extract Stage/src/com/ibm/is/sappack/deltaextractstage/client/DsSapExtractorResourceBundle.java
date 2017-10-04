//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2015                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.is.sappack.dsstages.common
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************

package com.ibm.is.sappack.deltaextractstage.client;
import java.util.Locale;
import java.util.ResourceBundle;

public class DsSapExtractorResourceBundle {

    private String resourceMessage = "";
    private String baseResourceName = null;
    private ResourceBundle resourceBundle = null;

    /**
     * This method is responsible to load the resource properties file. On getting
     * error in loading, it sets the custom message
     * @param parabaseResourceName
     */
    public void initializeResource(String parabaseResourceName) {
        try {
            baseResourceName = parabaseResourceName;
            resourceBundle = ResourceBundle.getBundle("com/ibm/is/sappack/deltaextractstage/resources/" + baseResourceName, Locale.getDefault(), this.getClass().getClassLoader());
            setResourceBundle(resourceBundle);
        } catch (Exception e) {
            setMessages("!!! BUNDLE " + baseResourceName + " !!!"); //$NON-NLS-1$//$NON-NLS-2$
        }
    }//end of initializeResource()

    /**
     * This method is responsible to get the string key value
     * @param pKeyName
     * @return
     */
    public String getResourceString(String pKeyName) {
        if (resourceMessage.equalsIgnoreCase("!!! BUNDLE " + baseResourceName + " !!!")) {
            return resourceMessage;
        } else {
            try {
                String keyValue = resourceBundle.getString(pKeyName);
                if (keyValue.trim().isEmpty()) {
                    keyValue = (pKeyName);
                }
                return keyValue;
            } catch (Exception e) {
                return "### KEY " + baseResourceName + " / " + pKeyName + "###"; //$NON-NLS-1$ //$NON-NLS-2$//$NON-NLS-3$
            }
        }
    }//end of getResourceString() method

    private void setMessages(String resourceMsg) {
        this.resourceMessage = resourceMsg;
    }

    /**
     * Get Resource Bundle instance
     * @return the messages
     */
    public ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    /**
     * Set Resource Bundle instance
     * @param resourceBundle
     */
    public void setResourceBundle(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
    }
}//END of DsSapExtractorResourceBundle class
