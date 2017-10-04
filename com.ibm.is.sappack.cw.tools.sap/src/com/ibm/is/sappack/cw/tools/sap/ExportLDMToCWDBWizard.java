package com.ibm.is.sappack.cw.tools.sap;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.progress.IProgressConstants;

import com.ibm.iis.sappack.gen.common.ui.connections.CWDBConnection;
import com.ibm.iis.sappack.gen.common.ui.util.Utils;
import com.ibm.iis.sappack.gen.common.ui.wizards.SelectCWDBConnectionWizardPage;
import com.ibm.is.sappack.cw.tools.sap.Model2CWDBExporter.Message;
import com.ibm.is.sappack.cw.tools.sap.Model2CWDBExporter.MessageList;
import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.common.ui.util.LargeMessageDialog;

public class ExportLDMToCWDBWizard extends Wizard {
	static String copyright() {
		return com.ibm.is.sappack.cw.tools.sap.Copyright.IBM_COPYRIGHT_SHORT;
	}

	List<IFile> ldmFiles;
	SelectCWDBConnectionWizardPage cwdbPage;
	ExportOptionsWizardPage exportOptionsPage;
	ExportSummaryWizardPage exportSummaryPage;
	Model2CWDBExporter exporter;

	public ExportLDMToCWDBWizard(List<IFile> ldmFiles) {
		super();
		this.setWindowTitle(Messages.ExportLDMToCWDBWizard_0);
		this.cwdbPage = new SelectCWDBConnectionWizardPage("selectcwdbconnectionforexport", Messages.ExportLDMToCWDBWizard_1, //$NON-NLS-1$
				Messages.ExportLDMToCWDBWizard_2, null) {

			@Override
			public boolean nextPressedImpl() {
				boolean b = super.nextPressedImpl();
				if (!b) {
					return false;
				}

				List<Map.Entry<String, String>> legID2legName = new ArrayList<Map.Entry<String,String>>();
				Map<String, Map<String, List<String>>> legacyID2rlout2BOMap = new HashMap<String, Map<String,List<String>>>();
				ResultSet rs = null;
				try {
					CWDBConnection cwdbConn = this.getCWDBConnection();
					Connection conn = cwdbConn.getJDBCConnection();
					String sqlRlouts = "SELECT DISTINCT T.CW_RLOUT, T.CW_LOB, T.CW_LEGACY_ID, S.DESCRIPTION "+ //  //$NON-NLS-1$
					                   " FROM AUX.SAP_DATATABLES_CONFIG T, AUX.LEGACY_SYSTEM S "+ //$NON-NLS-1$
					                   " WHERE S.CW_LEGACY_ID = T.CW_LEGACY_ID"; //$NON-NLS-1$
					
					try {
						rs = conn.createStatement().executeQuery(sqlRlouts);
						while (rs.next()) {
							String legacyID = rs.getString(3);
							Map<String, List<String>> rlout2BOMap = legacyID2rlout2BOMap.get(legacyID);
							if (rlout2BOMap == null) {
								rlout2BOMap = new HashMap<String, List<String>>();
								legacyID2rlout2BOMap.put(legacyID, rlout2BOMap);
								String legacyShortName = rs.getString(3);
								String legacyDescription = rs.getString(4);
								String legacySystemName = legacyShortName;
								if (legacyDescription != null) {
									legacySystemName += " - " + legacyDescription; //$NON-NLS-1$
								}
								legID2legName.add(new AbstractMap.SimpleEntry<String, String>(legacyID, legacySystemName));
							}
							
							String rlout = rs.getString(1);
							List<String> bos = rlout2BOMap.get(rlout);
							if (bos == null) {
								bos = new ArrayList<String>();
								rlout2BOMap.put(rlout, bos);
							}
							String bo = rs.getString(2);
							bos.add(bo);
							
						}
					} catch (SQLException exc) {
						setErrorMessage(Messages.ExportLDMToCWDBWizard_3);
						Activator.logException(exc);
						return false;
					}

				} finally {
					if (rs != null) {
						try {
							rs.close();
						} catch (SQLException e) {
							Activator.logException(e);
						}
						rs = null;
					}
				}
				exportOptionsPage.setLegacyID2Rlout2BOMap(legacyID2rlout2BOMap, legID2legName);
				return true;

				//				exportOptionsPage.setCWDBConnection(this.getCWDBConnection());
				//				return true;
			}
		};
		addPage(this.cwdbPage);
		this.exportOptionsPage = new ExportOptionsWizardPage("cwdbpublishoptionspage") { //$NON-NLS-1$

			@Override
			public boolean nextPressedImpl() {
				boolean b = super.nextPressedImpl();
				if (!b) {
					return false;
				}
				return initializeExport(this);
			}

		};

		addPage(this.exportOptionsPage);

		this.exportSummaryPage = new ExportSummaryWizardPage("exportsummarywizardpage"); //$NON-NLS-1$
		addPage(this.exportSummaryPage);
		this.ldmFiles = ldmFiles;
		this.setDialogSettings(Activator.getDefault().getDialogSettings());
		this.setNeedsProgressMonitor(true);
	}

	private static IAction getExportCompletedAction(final String s) {
		final String title = Messages.ExportLDMToCWDBWizard_4;
		return new Action(title) {
			public void run() {
				LargeMessageDialog.openLargeMessageDialog(Display.getCurrent().getActiveShell(), title, Messages.ExportLDMToCWDBWizard_5, s);
			}
		};
	}

	String getLDMFileNameList() {
		StringBuffer buf = new StringBuffer();
		for (IFile f : ldmFiles) {
			if (buf.length() != 0) {
				buf.append(", "); //$NON-NLS-1$
			}
			buf.append(f.getName());
		}
		return buf.toString();
	}

	private boolean processExportErrors(MessageList messages, WizardPage page) {
		List<Message> errorMessages = messages.getErrorMessages();
		if (errorMessages != null && !errorMessages.isEmpty()) {
			page.setErrorMessage(errorMessages.get(0).getMessage());
			page.setPageComplete(false);
			return false;
		}
		/*
		List<Message> severeWarningMessages = messages.getWarningMessages();
		if (severeWarningMessages != null && !severeWarningMessages.isEmpty()) {
			page.setMessage("Warnings were detected! Check the details.", IMessageProvider.WARNING);
		}
		*/
		return true;
	}

	public boolean initializeExport(WizardPage page) {
		String s = Messages.ExportLDMToCWDBWizard_6;

		CWDBConnection cwdbConnection = cwdbPage.getCWDBConnection();
		s = MessageFormat.format(s, ExportLDMToCWDBWizard.this.getLDMFileNameList(), 
				cwdbConnection.getName(),
				this.exportOptionsPage.getSelectedLegacySystemName(),
				this.exportOptionsPage.getBusinessObjectName(),
				this.exportOptionsPage.getSelectedRlout());
		try {
			exporter = new Model2CWDBExporter(ExportLDMToCWDBWizard.this.ldmFiles, cwdbConnection, this.exportOptionsPage.getSelectedLegacyID(), this.exportOptionsPage.getSelectedRlout(), this.exportOptionsPage.getBusinessObjectName());
			final MessageList[] msgList = new MessageList[1];
			try {
				this.getContainer().run(false, true, new IRunnableWithProgress() {

					@Override
					public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
						Model2CWDBExporter.MessageList messages;
						try {
							messages = exporter.initialize(monitor);
						} catch (SQLException e) {
							Activator.logException(e);
							throw new InvocationTargetException(e);
						}
						msgList[0] = messages;

					}
				});
			} catch (InvocationTargetException exc) {
				Activator.logException(exc);
				Throwable tge = exc.getTargetException();
				String excMsg = tge.getMessage();
				String statusMsg = MessageFormat.format(Messages.ExportLDMToCWDBWizard_7, excMsg);
				if (tge instanceof SQLException) {
					statusMsg = MessageFormat.format(Messages.ExportLDMToCWDBWizard_8, excMsg);
				}
				page.setErrorMessage(statusMsg);
				return false;
			}
			MessageList ml = msgList[0];
			
			if (!processExportErrors(ml, page)) {
				return false;
			}
			if (ml != null) {
				s += "\n\n" + ml.getAllMessagesAsString(); //$NON-NLS-1$
			}

			if (!ml.getWarningMessages().isEmpty()) {
				exportSummaryPage.setMessage(Messages.ExportLDMToCWDBWizard_15, IMessageProvider.WARNING); 
			}
			exportSummaryPage.setText(s);

		} catch (Exception e) {
			Activator.logException(e);
			page.setErrorMessage(MessageFormat.format(Messages.Utils_2, Utils.getExceptionMessage(e)));
			return false;
		}
		return true;
	}

	@Override
	public boolean performFinish() {
		final CWDBConnection cwdbConn = this.cwdbPage.getCWDBConnection();
		Job exportJob = new Job(MessageFormat.format(Messages.ExportLDMToCWDBWizard_9, this.getLDMFileNameList(), cwdbConn.getName())) {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				MessageList exportResult = null;
				long after, before;
				try {
					before = System.currentTimeMillis();
					exportResult = exporter.exportModel2CWDB(monitor);
					after = System.currentTimeMillis();
				} catch (Exception exc) {
					Activator.logException(exc);
					String s = exc.getMessage();
					String statusMsg = MessageFormat.format(Messages.ExportLDMToCWDBWizard_10, s);

					return new Status(IStatus.ERROR, Activator.PLUGIN_ID, statusMsg);
				}
				long secs = (after - before) / 1000;

				Status status = null;
				String result = null;
				if (monitor.isCanceled()) {
					result = MessageFormat.format(Messages.ExportLDMToCWDBWizard_11, Long.toString(secs));
					status = new Status(IStatus.OK, Activator.PLUGIN_ID, Messages.ExportLDMToCWDBWizard_12);
				} else {
					result = MessageFormat.format(Messages.ExportLDMToCWDBWizard_13, Long.toString(secs));
					String errorMsg = exportResult.getErrorMessagesAsString();
					if (errorMsg != null) {
						result = errorMsg;
					}
					status = new Status(IStatus.OK, Activator.PLUGIN_ID, Messages.ExportLDMToCWDBWizard_14);
				}
				final String resultMsg = result;

				Boolean isModal = (Boolean) getProperty(IProgressConstants.PROPERTY_IN_DIALOG);
				if (isModal == null || isModal.booleanValue()) {
					Display.getDefault().syncExec(new Runnable() {
						public void run() {
							getExportCompletedAction(resultMsg).run();
						}
					});
				} else {
					setProperty(IProgressConstants.KEEP_PROPERTY, Boolean.TRUE);
					setProperty(IProgressConstants.ACTION_PROPERTY, getExportCompletedAction(resultMsg));
				}
				return status;
			}
		};
		exportJob.setUser(true);
		exportJob.schedule();

		return true;
	}

}
