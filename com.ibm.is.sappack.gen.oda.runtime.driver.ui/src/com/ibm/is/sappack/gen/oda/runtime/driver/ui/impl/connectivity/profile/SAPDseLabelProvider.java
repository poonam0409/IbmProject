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

import org.eclipse.datatools.connectivity.ui.navigator.ConnectionProfileLabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * @author dsh
 *
 */
public class SAPDseLabelProvider extends ConnectionProfileLabelProvider {
	
	static String copyright()
    { return com.ibm.is.sappack.gen.oda.runtime.driver.ui.impl.connectivity.profile.Copyright.IBM_COPYRIGHT_SHORT; }

    /**
     * Constructs a new <code>SAPDseLabelProvider</code> instance.
     */
    public SAPDseLabelProvider() {
        super();
    }

    @Override
    public Image getImage(Object element) {
        return super.getImage(element);
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.datatools.connectivity.ui.navigator.ConnectionProfileLabelProvider#getDescription(java.lang.Object)
     */
    @Override
    public String getDescription(Object element) {
        return super.getDescription(element);
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.datatools.connectivity.ui.navigator.ConnectionProfileLabelProvider#getText(java.lang.Object)
     */
    @Override
    public String getText(Object element) {
        return super.getText(element);
    }

}
