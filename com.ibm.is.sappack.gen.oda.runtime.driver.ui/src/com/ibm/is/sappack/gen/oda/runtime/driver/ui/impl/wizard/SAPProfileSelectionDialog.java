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

import java.text.MessageFormat;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.eclipse.datatools.connectivity.ui.dse.dialogs.ProfileSelectionDialog;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.ibm.is.sappack.gen.oda.runtime.driver.ui.Messages;

/**
 * @author dsh
 *
 */
public class SAPProfileSelectionDialog extends ProfileSelectionDialog implements
		ChangeListener {
	
	static String copyright()
    { return com.ibm.is.sappack.gen.oda.runtime.driver.ui.impl.wizard.Copyright.IBM_COPYRIGHT_SHORT; }
	
    /**
     * Indicate whether to warn when a profile is selected
     */
    private boolean warnDataOverWrite = false;

	public SAPProfileSelectionDialog(Shell parentShell) {
		super(parentShell);
        this.setBlockOnOpen(true);
        this.setCategoryName(com.ibm.is.sappack.gen.oda.runtime.driver.impl.Driver.ODA_DATA_SOURCE_ID);
        this.setLimitToProfiles(true);
        this.setShowNew(true);
        this.setShowSelectButtons(false);
        this.setHelpAvailable(false);
	}
	
    /* 
     * (non-Javadoc)
     * @see org.eclipse.datatools.connectivity.ui.dse.dialogs.ProfileSelectionDialog#configureShell(org.eclipse.swt.widgets.Shell)
     */
    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText(Messages.SAPProfileSelectionDialog_0);
    }

	/* (non-Javadoc)
	 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
	 */
	@Override
	public void stateChanged(ChangeEvent e) {
        String cp = this.getCPName();
        if (cp != null && this.warnDataOverWrite) {
            this.setMessage(MessageFormat.format(
                Messages.SAPProfileSelectionDialog_1, 
                new Object[] {cp}), 
                IMessageProvider.WARNING);
        } else {
            if (this.getShowNew()) {
                this.setMessage(Messages.SAPProfileSelectionDialog_2);            
            } else {
                this.setMessage(Messages.SAPProfileSelectionDialog_3);
            }
        }
        this.validate();
	}

    /* 
     * (non-Javadoc)
     * @see org.eclipse.datatools.connectivity.ui.dse.dialogs.ProfileSelectionDialog#fillInDefaultValues()
     */
    @Override
    public void fillInDefaultValues() {
        super.fillInDefaultValues();
        this.getComposite().addChangeListener(this);
    }    
    
    /**
     * Set whether to warn data overwrite when a connection is selected
     */
    public void setWarnDataOverWrite(boolean warn) {
        this.warnDataOverWrite = warn;
    }

    /* 
     * (non-Javadoc)
     * @see org.eclipse.datatools.connectivity.ui.dse.dialogs.ProfileSelectionDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        Control control = super.createDialogArea(parent);
        this.setTitle(Messages.SAPProfileSelectionDialog_4);
        if (this.getShowNew()) {
            this.setMessage(Messages.SAPProfileSelectionDialog_5);            
        } else {
            this.setMessage(Messages.SAPProfileSelectionDialog_6);
        }
        return control;
    }
}
