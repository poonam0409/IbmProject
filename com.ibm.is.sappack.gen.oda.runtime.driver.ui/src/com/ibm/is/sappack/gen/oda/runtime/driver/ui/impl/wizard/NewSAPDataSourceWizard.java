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

import org.eclipse.datatools.connectivity.oda.OdaException;
import org.eclipse.datatools.connectivity.oda.design.ui.wizards.NewDataSourceWizard;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;

import com.ibm.is.sappack.gen.oda.runtime.driver.ui.Messages;

/**
 * @author dsh
 *
 */
public class NewSAPDataSourceWizard extends NewDataSourceWizard {
	
	static String copyright()
    { return com.ibm.is.sappack.gen.oda.runtime.driver.ui.impl.wizard.Copyright.IBM_COPYRIGHT_SHORT; }

	/**
	 * TODO: Provide a SAP ERP specific wizard icon
	 */
	public NewSAPDataSourceWizard() {
		super();
	}

	/**
	 * @param odaDataSourceId
	 * @throws OdaException
	 */
	public NewSAPDataSourceWizard(String odaDataSourceId) throws OdaException {
		super(odaDataSourceId);
	}

    /* 
     * (non-Javadoc)
     * @see org.eclipse.datatools.connectivity.ui.wizards.NewConnectionProfileWizard#createPageControls(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void createPageControls(Composite pageContainer) {
        super.createPageControls(pageContainer);
        
        if (this.hasProfileNamePage()) {
            this.getProfileNamePage().setTitle(Messages.NewSAPDataSourceWizard_0);
            this.getProfileNamePage().setDescription(Messages.NewSAPDataSourceWizard_1);
        }
        
        // TODO: Provide help context ID
        PlatformUI.getWorkbench().getHelpSystem().setHelp(getShell(), 
            null);  
    }
}
