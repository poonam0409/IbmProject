//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2014                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.iis.sappack.gen.tools.jobgenerator.jobgenwizard
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.iis.sappack.gen.tools.jobgenerator.jobgenwizard;


import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.core.resources.IFile;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;

import com.ibm.iis.sappack.gen.common.ui.util.Utils;
import com.ibm.iis.sappack.gen.common.ui.wizards.PersistentWizardPageBase;
import com.ibm.is.sappack.gen.common.DBSupport;
import com.ibm.is.sappack.gen.common.JobGeneratorException;
import com.ibm.is.sappack.gen.common.ui.Messages;


public class IDocLoadSettingsPage extends PersistentWizardPageBase {
	private Combo   messageTypesCombo;


  	static String copyright() { 
  		return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	

	public IDocLoadSettingsPage() {
		super("idocloadsettingspage", Messages.IDocLoadSettingsPage_0, null); //$NON-NLS-1$
		setDescription(Messages.IDocLoadSettingsPage_1);
	}


	@Override
	public void performHelp() {
		PlatformUI.getWorkbench().getHelpSystem().displayHelp(Utils.getHelpID("rgwiz_idoc_settings")); //$NON-NLS-1$
	}


	@Override
	public Composite createControlImpl(Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayout(new GridLayout(2, false));

		Label l = new Label(comp, SWT.NONE);
		l.setText(Messages.IDocLoadSettingsPage_2);

		this.messageTypesCombo = new Combo(comp, SWT.NONE);
		this.messageTypesCombo.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
//		this.messageTypesCombo.addSelectionListener(new SelectionAdapter() {
//         public void widgetSelected(SelectionEvent e) {
//         	// call to update 'page complete' variable
//         	checkPageComplete();
//         }
//      });
		setInitialFocusToDlgFld(this.messageTypesCombo);
		
		return comp;
	}


//	private void checkPageComplete() {
//      setPageComplete(getIDocMessageType() != null);
//	}
	
	@Override
	public boolean nextPressedImpl() {
		boolean gotoNextPage;

		if (getIDocMessageType() == null) {
			setErrorMessage(Messages.IDocLoadSettingsPage_3);
			gotoNextPage = false;
		}
		else {
			setErrorMessage(null);
			gotoNextPage = true;
		}
		return(gotoNextPage);
	}


	public void setDBMFile(IFile dbmFile) {
		// get the message type
		DBSupport.DBInstance dbInst;
		try {
			dbInst = DBSupport.createDBInstance(new File(dbmFile.getLocation().toOSString()));
		} catch (JobGeneratorException e) {
			handleException(e);
			return;
		}
		String messageTypesAnnotation = dbInst.getMessageTypeAnnotation();
		List<String> messageTypes = new ArrayList<String>();
		StringTokenizer tok = new StringTokenizer(messageTypesAnnotation, ","); //$NON-NLS-1$
		while (tok.hasMoreTokens()) {
			messageTypes.add(tok.nextToken());
		}
		this.messageTypesCombo.setItems(messageTypes.toArray(new String[0]));
		this.messageTypesCombo.select(0);
//   	checkPageComplete();
	}


	public String getIDocMessageType() {
		String   idocMsgType;
		String[] items = this.messageTypesCombo.getItems();

		if (items == null || items.length == 0) {
			idocMsgType = null;
		}
		else {
			idocMsgType = items[this.messageTypesCombo.getSelectionIndex()];
		}
		
		if (idocMsgType == null) {
			idocMsgType = this.messageTypesCombo.getText();
			if (idocMsgType.length() == 0) {
				idocMsgType = null;
			}
		}

		return(idocMsgType);
	}

}
