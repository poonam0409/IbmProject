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
// Module Name : com.ibm.is.sappack.gen.tools.sap.tableextract
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.tools.sap.tableextract;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.datatools.modelbase.sql.tables.Table;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import com.ibm.datatools.core.DataToolsPlugin;
import com.ibm.is.sappack.gen.common.Constants;
import com.ibm.is.sappack.gen.common.ui.Messages;

public class ExtractTableAction implements IObjectActionDelegate {
	static String copyright() {
		return com.ibm.is.sappack.gen.tools.sap.tableextract.Copyright.IBM_COPYRIGHT_SHORT;
	}

	ISelection selection;

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {

	}

	@Override
	public void run(IAction action) {
		if (this.selection instanceof IStructuredSelection) {
			Shell sh = Display.getCurrent().getActiveShell();
			boolean yes = MessageDialog.openQuestion(sh, Messages.ExtractTableAction_0, Messages.ExtractTableAction_1);
			if (!yes) {
				return;
			}
			
			// Iterate over all selected files
			IStructuredSelection structuredSelection = ((IStructuredSelection) this.selection);
			List<Table> tables = new ArrayList<Table>();
			Iterator<?> it = structuredSelection.iterator();
			StringBuffer incorrectTableNames = new StringBuffer();
			List<Table> correctTables = new ArrayList<Table>();
			while (it.hasNext()) {
				Object obj = it.next();
				if (obj instanceof Table) {
					Table table = (Table) obj;
					tables.add(table);
				}
			}
			for (Table table : tables) {
				EAnnotation annot = table.getEAnnotation(DataToolsPlugin.ANNOTATION_UDP);
				boolean tableCorrect = false;
				if (annot != null) {
					Map<?, ?> m = annot.getDetails().map();
					String dataObjectSource = (String) m.get(Constants.ANNOT_DATA_OBJECT_SOURCE);
					if ((Constants.DATA_OBJECT_SOURCE_TYPE_JOINED_CHECK_AND_TEXT_TABLE.equals(dataObjectSource) || Constants.DATA_OBJECT_SOURCE_TYPE_LOGICAL_TABLE.equals(dataObjectSource))
							|| Constants.DATA_OBJECT_SOURCE_TYPE_REFERENCE_CHECK_TABLE.equals(dataObjectSource) || Constants.DATA_OBJECT_SOURCE_TYPE_REFERENCE_TEXT_TABLE.equals(dataObjectSource) ) {
						tableCorrect = true;
					}
				}
				if (tableCorrect) {
					correctTables.add(table);
				} else {
					incorrectTableNames.append(table.getName() + " "); //$NON-NLS-1$
				}
			}

			if (incorrectTableNames.toString().trim().length() > 0) {
				if (correctTables.isEmpty()) {
					MessageDialog.openWarning(sh, Messages.TitleWarning, Messages.ExtractTableAction_3);
					return;
				}
				String msg = Messages.ExtractTableAction_4;
				msg = MessageFormat.format(msg, incorrectTableNames);
				boolean confirmed = MessageDialog.openConfirm(sh, Messages.ExtractTableAction_5, msg);
				if (!confirmed) {
					return;
				}
			}
			if (correctTables.isEmpty()) {
				MessageDialog.openWarning(sh, Messages.TitleWarning, Messages.ExtractTableAction_7);
				return;
			}

			ExtractTablesWizard wizz = new ExtractTablesWizard();
			wizz.setTables(correctTables);
			WizardDialog dialog = new WizardDialog(sh, wizz);
			dialog.open();
		}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
	}

}
