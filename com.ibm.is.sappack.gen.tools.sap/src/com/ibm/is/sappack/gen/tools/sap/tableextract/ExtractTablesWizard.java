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

import java.sql.Connection;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.modelbase.sql.tables.Table;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.progress.IProgressConstants;

import com.ibm.iis.sappack.gen.common.ui.connections.SapSystem;
import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.tools.sap.activator.Activator;

public class ExtractTablesWizard extends Wizard {

	static String copyright() {
		return com.ibm.is.sappack.gen.tools.sap.tableextract.Copyright.IBM_COPYRIGHT_SHORT;
	}

	com.ibm.datatools.sqlxeditor.util.SelectConnectionProfileWizardPage connectionProfilePage;
	SelectSAPSystemWizardPage selectSAPSystemPage;
	CreateSQLScriptOptionsWizardPage createSQLScriptOptionsPage;
	List<Table> tables = new ArrayList<Table>();
	DatabaseExtractionOptionsPage dbOptionsPage;

	public ExtractTablesWizard() {
		super();
		this.setWindowTitle(Messages.ExtractTablesWizard_0);
	}

	public List<Table> getTables() {
		return this.tables;
	}

	public void setTables(List<Table> tables) {
		this.tables = new ArrayList<Table>();
		this.tables.addAll(tables);
	}

	@Override
	public void addPages() {
		this.selectSAPSystemPage = new SelectSAPSystemWizardPage();
		addPage(this.selectSAPSystemPage);
		this.createSQLScriptOptionsPage = new CreateSQLScriptOptionsWizardPage();
		addPage(this.createSQLScriptOptionsPage);

		this.dbOptionsPage = new DatabaseExtractionOptionsPage();
		addPage(this.dbOptionsPage);

		connectionProfilePage = new com.ibm.datatools.sqlxeditor.util.SelectConnectionProfileWizardPage(Messages.ExtractTablesWizard_1) {

			@Override
			public void createControl(Composite parent) {
				super.createControl(parent);
				update();
			}

			@Override
			public void handleEvent(Event event) {
				super.handleEvent(event);
				update();
			}

			void update() {
				boolean complete = true;
				setErrorMessage(null);
				if (getSelectedConnection() == null) {
					setErrorMessage(Messages.ExtractTablesWizard_2);
					complete = false;
				} else {
					int connectionState = getSelectedConnection().getConnectionState();
					if (connectionState != IConnectionProfile.CONNECTED_STATE) {
						// TODO
						//setErrorMessage("Select a connected database connection");
						//complete = false;
					}
				}
				setPageComplete(complete);

			}

		};
		addPage(connectionProfilePage);
	}

	@Override
	public boolean performFinish() {
		Activator.getLogger().fine("Run extraction on tables: " + this.tables); //$NON-NLS-1$
		if (this.tables.isEmpty()) {
			return true;
		}

		final SapSystem sapSystem = this.selectSAPSystemPage.getSapSystem();

		String message = Messages.ExtractTablesWizard_3;
		TableExtractor tex = null;
		if (this.selectSAPSystemPage.insertTablesDirectly()) {
			boolean deleteTable = this.dbOptionsPage.getDeleteTables();
			boolean continueOnError = this.dbOptionsPage.getContinueOnError();
			int commitCount = this.dbOptionsPage.getCommitCount();
			int maxRows = this.dbOptionsPage.getMaxRows();
			final IConnectionProfile connProf = this.connectionProfilePage.getSelectedConnection();

			IStatus st = connProf.connect();
			if (!st.isOK()) {
				String msg = Messages.ExtractTablesWizard_4;
				msg = MessageFormat.format(msg, connProf.getName());
				com.ibm.is.sappack.gen.tools.sap.activator.Activator.getLogger().log(Level.WARNING, msg);
				return false;
			}
			Object o = connProf.getManagedConnection("java.sql.Connection").getConnection().getRawConnection(); //$NON-NLS-1$

			if (!(o instanceof Connection)) {
				return false;
			}
			Connection jdbcConnection = (Connection) o;
			tex = new DatabaseTableExtractor(this.tables, sapSystem, jdbcConnection, deleteTable, continueOnError, commitCount, maxRows);
			message = Messages.ExtractTablesWizard_5;
			message = MessageFormat.format(message, connProf.getName());
		} else {
			boolean generateDeleteStatement = this.createSQLScriptOptionsPage.generateDeleteStatement();
			int commitCount = this.createSQLScriptOptionsPage.getCommitCount();
			int maxRows = this.createSQLScriptOptionsPage.getMaxRows();
			IFile sqlFile = this.createSQLScriptOptionsPage.getSQScriptFile();
			tex = new CreateSQLScriptTableExtractor(this.tables, sapSystem, sqlFile, generateDeleteStatement, commitCount, maxRows);
			message = Messages.ExtractTablesWizard_6;
			message = MessageFormat.format(message, sqlFile.getName());
		}

		final TableExtractor tex1 = tex;
		final String successMessage = message;
		Job extractTablesJob = new Job(Messages.ExtractTablesWizard_7) {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				IStatus result = Status.OK_STATUS;

				try {
					tex1.extractTables(monitor);
					if (monitor.isCanceled()) {
						result = Status.CANCEL_STATUS;
					}
				} catch (Exception e) {
					e.printStackTrace();
					Activator.getLogger().log(Level.SEVERE, Messages.LogExceptionMessage, e);
					result = new Status(Status.ERROR, Activator.PLUGIN_ID, Messages.ExtractTablesWizard_8 + e.getClass().getName() + ": " + e.getMessage(), e); //$NON-NLS-1$
				}
				final IStatus finalResult = result;
				setProperty(IProgressConstants.KEEP_PROPERTY, Boolean.TRUE);
				setProperty(IProgressConstants.ACTION_PROPERTY, new Action() {
					@Override
					public void run() {
						if (finalResult.isOK()) {
							MessageDialog.openInformation(getShell(), Messages.ExtractTablesWizard_9, successMessage);
						} else {
							String msg = Messages.ExtractTablesWizard_10;
							msg = MessageFormat.format(msg, finalResult.getMessage());
							MessageDialog.openError(getShell(), Messages.ExtractTablesWizard_11, msg);
						}
					}
				});

				return result;
			}
		};
		extractTablesJob.setUser(true);
		extractTablesJob.schedule();
		return true;
	}

	List<String> parseListAnnot(String s) {
		StringTokenizer tok = new StringTokenizer(s, ","); //$NON-NLS-1$
		List<String> result = new ArrayList<String>();
		while (tok.hasMoreTokens()) {
			result.add(tok.nextToken());
		}
		return result;
	}

	List<String[]> parseJoinCondition(String s) {
		List<String> pairs = parseListAnnot(s);
		List<String[]> result = new ArrayList<String[]>();
		for (String pair : pairs) {
			int ix = pair.indexOf('=');
			if (ix > -1) {
				String[] p = new String[2];
				p[0] = pair.substring(0, ix);
				p[1] = pair.substring(ix + 1);
				result.add(p);
			}
		}
		return result;
	}

	@Override
	public boolean canFinish() {
		if (this.selectSAPSystemPage.isPageComplete()) {
			if (this.selectSAPSystemPage.insertTablesDirectly()) {
				return this.dbOptionsPage.isPageComplete() && this.connectionProfilePage.isPageComplete();
			}
			return this.createSQLScriptOptionsPage.isPageComplete();
		}
		return false;
	}

	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		if (page == selectSAPSystemPage) {
			if (selectSAPSystemPage.insertTablesDirectly()) {
				return this.dbOptionsPage;
			} else {
				return this.createSQLScriptOptionsPage;
			}
		} else if (page == this.createSQLScriptOptionsPage) {
			return null;
		} else if (page == this.connectionProfilePage) {
			return null;
		} else if (page == this.dbOptionsPage) {
			return this.connectionProfilePage;
		}
		throw new RuntimeException("Wrong wizard logic"); //$NON-NLS-1$
	}

}
