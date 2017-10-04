//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2011, 2014                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.iis.sappack.gen.tools.sap.importwizard
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.iis.sappack.gen.tools.sap.importwizard;


import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.tools.sap.importer.idoctypes.Segment;
import com.ibm.is.sappack.gen.tools.sap.model.SapTable;


public class AdditionalInfoDialog extends TitleAreaDialog {
	private Object entity;
	private String ctColumn;
	private String ttColumn;

	final static String NL = "\n"; //$NON-NLS-1$

  	static String copyright() { 
	   return Copyright.IBM_COPYRIGHT_SHORT; 
	}	

	public AdditionalInfoDialog(Object entity, String ctColumn, String ttColumn, Shell parentShell) {
		super(parentShell);
		this.entity = entity;
		this.ctColumn = ctColumn;
		this.ttColumn = ttColumn;
	}

	private String getEntityName() {
		if (entity instanceof SapTable) {
			return ((SapTable) entity).getName();
		} else if (entity instanceof Segment) {
			return ((Segment) entity).getType();
		}
		return null;
	}

	private Collection<String> getCheckTables() {
		Collection<String> result = new ArrayList<String>();
		if (entity instanceof SapTable) {
			SapTable t = (SapTable) this.entity;
			Collection<SapTable> cts = t.getCheckTables();
			for (SapTable ct : cts) {
				result.add(ct.getName());
			}
		} else if (entity instanceof Segment) {
			Segment s = (Segment) this.entity;
			for (String ct : s.getAllCheckTableNames()) {
				result.add(ct);
			}
		}
		return result;
	}

	private Collection<String> getTextTables() {
		Collection<String> result = new ArrayList<String>();
		if (entity instanceof SapTable) {
			SapTable t = (SapTable) this.entity;
			Collection<SapTable> cts = t.getTextTables();
			for (SapTable ct : cts) {
				result.add(ct.getName());
			}
		}
		// segments never have text tables
		return result;
	}

	protected Control createContents(Composite parent) {
		Control contents = super.createContents(parent);

		// Set the title
		setTitle(Messages.AdditionalInfoDialog_0);

		String msg = Messages.AdditionalInfoDialog_1;
		msg = MessageFormat.format(msg, getEntityName());
		// Set the message
		setMessage(msg);

		return contents;
	}

	private String getTablesAsString(Collection<String> tables) {
		if (tables == null || tables.isEmpty()) {
			return Messages.AdditionalInfoDialog_2 + NL;
		}
		StringBuffer sb = new StringBuffer();
		for (String ct : tables) {
			sb.append(ct + NL);
		}
		return sb.toString();
	}

	private String getInfoMessage() {
		if (this.entity instanceof SapTable) {
			String msg = Messages.AdditionalInfoDialog_3;
			msg = MessageFormat.format(msg, getEntityName());
			return msg;
		} else if (this.entity instanceof Segment) {
			String msg = Messages.AdditionalInfoDialog_4;
			msg = MessageFormat.format(msg, getEntityName());
			return msg;
		}
		return ""; //$NON-NLS-1$
	}

	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);

		container.setLayout(new GridLayout(1, false));
		Text text = new Text(container, SWT.READ_ONLY | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.widthHint = 200;
		gd.heightHint = 350;
		text.setLayoutData(gd);

		StringBuffer sb = new StringBuffer();
		String msg = getInfoMessage();
		sb.append(msg + NL + NL);

		String ctOfMsg = Messages.AdditionalInfoDialog_5;

		if (this.ctColumn != null && this.ctColumn.trim().length() > 0) {
			ctOfMsg = MessageFormat.format(ctOfMsg, getEntityName(), this.ctColumn);
			sb.append(ctOfMsg + NL + NL);
		}

		String ttOfMsg = Messages.AdditionalInfoDialog_6;
		if (this.ttColumn != null && this.ttColumn.trim().length() > 0) {
			ttOfMsg = MessageFormat.format(ctOfMsg, getEntityName(), this.ttColumn);
			sb.append(ttOfMsg + NL + NL);
		}

		String checkTableText = Messages.AdditionalInfoDialog_7;
		checkTableText = MessageFormat.format(checkTableText, this.getEntityName());
		sb.append(checkTableText);
		sb.append(NL);
		sb.append(getTablesAsString(this.getCheckTables()));
		sb.append(NL);

		if (entity instanceof SapTable) {
			String textTableText = Messages.AdditionalInfoDialog_8;
			textTableText = MessageFormat.format(textTableText, getEntityName());
			sb.append(textTableText);
			sb.append(NL);
			sb.append(getTablesAsString(this.getTextTables()));
		}

		text.setText(sb.toString());
		return container;
	}

	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.CLOSE_LABEL, true);
	}
}
