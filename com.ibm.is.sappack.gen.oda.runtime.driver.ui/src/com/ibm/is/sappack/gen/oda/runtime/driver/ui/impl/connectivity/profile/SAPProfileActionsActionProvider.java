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
package com.ibm.is.sappack.gen.oda.runtime.driver.ui.impl.connectivity.profile;

import org.eclipse.datatools.connectivity.ui.actions.DeleteAction;
import org.eclipse.datatools.connectivity.ui.navigator.actions.ProfileActionsActionProvider;

/**
 * @author dsh
 *
 */
public class SAPProfileActionsActionProvider extends
		ProfileActionsActionProvider {
	
	static String copyright()
    { return com.ibm.is.sappack.gen.oda.runtime.driver.ui.impl.connectivity.profile.Copyright.IBM_COPYRIGHT_SHORT; }

	/**
	 * 
	 */
	public SAPProfileActionsActionProvider() {
		super();
	}
	
    /* 
     * (non-Javadoc)
     * @see org.eclipse.datatools.connectivity.ui.navigator.actions.ProfileActionsActionProvider#createDeleteAction()
     */
    @Override
    protected DeleteAction createDeleteAction() {
        return new DeleteAction();
    }
}
