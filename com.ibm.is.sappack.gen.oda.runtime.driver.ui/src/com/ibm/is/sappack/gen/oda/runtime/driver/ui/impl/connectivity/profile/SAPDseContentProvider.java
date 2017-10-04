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

import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.ui.navigator.ConnectionProfileContentProvider;

/**
 * @author dsh
 *
 */
public class SAPDseContentProvider extends ConnectionProfileContentProvider {
	
	static String copyright()
    { return com.ibm.is.sappack.gen.oda.runtime.driver.ui.impl.connectivity.profile.Copyright.IBM_COPYRIGHT_SHORT; }

	/**
	 * 
	 */
	public SAPDseContentProvider() {
		super();
	}

    /*
     * (non-Javadoc)
     * @see org.eclipse.datatools.connectivity.ui.navigator.ConnectionProfileContentProvider#getChildren(java.lang.Object)
     */
    @Override
    public Object[] getChildren(Object parentElement) {
        return super.getChildren(parentElement);
    }

    /* 
     * (non-Javadoc)
     * @see org.eclipse.datatools.connectivity.ui.navigator.ConnectionProfileContentProvider#hasChildren(java.lang.Object)
     */
    @SuppressWarnings("deprecation")
    @Override
    public boolean hasChildren(Object element) {
        if (element instanceof IConnectionProfile) {
            if(((IConnectionProfile)element).isConnected()) {
                return true;
            } else {
                return false;
            }
        } else {
            return super.hasChildren(element);            
        }
    }
}
