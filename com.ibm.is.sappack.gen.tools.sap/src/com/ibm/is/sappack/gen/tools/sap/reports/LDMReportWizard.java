//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2009, 2012                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.is.sappack.gen.tools.sap.reports
//                                                                             
//*************************-END OF SPECIFICATIONS-**************************
package com.ibm.is.sappack.gen.tools.sap.reports;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.Wizard;

import com.ibm.db.models.logical.Entity;
import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.tools.sap.activator.Activator;
import com.ibm.is.sappack.gen.tools.sap.utilities.ExceptionHandler;

public abstract class LDMReportWizard extends Wizard {
	static String copyright() {
		return Copyright.IBM_COPYRIGHT_SHORT;
	}

	SelectReportFileWizardPage selectReportFilePage;
	ReportOptionsPageBase reportOptionsPage;

	LDMReport report;
	List<Entity> entities;

	public LDMReportWizard(List<Entity> entities, LDMReport report) {
		super();
		this.report = report;
		this.entities = entities;
		this.setWindowTitle(report.getReportDescription());
	}

	@Override
	public void addPages() {
		this.selectReportFilePage = new SelectReportFileWizardPage(report.getReportDescription());
		this.addPage(this.selectReportFilePage);
		this.reportOptionsPage = this.createReportOptionsPage();
		this.addPage(this.reportOptionsPage);
	}

	@Override
	public boolean performFinish() {

		final File f = selectReportFilePage.getReportFile();
		final Map<String, String> reportOptions = this.reportOptionsPage.getReportOptions();
		reportOptions.put(CSVFile.OPTION_SEPARATOR_CHAR, Character.toString(this.selectReportFilePage.getSeparatorChar()));
		IRunnableWithProgress runnable = new IRunnableWithProgress() {

			@Override
			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
				try {
					report.createReport(entities, f, reportOptions, monitor);
				} catch (Exception exc) {
					Activator.getLogger().log(Level.SEVERE, Messages.LogExceptionMessage, exc);
					throw new InvocationTargetException(exc);
				}
			}

		};

		try {
			getContainer().run(true, true, runnable);
		} catch (Exception exc) {
			Activator.getLogger().log(Level.SEVERE, Messages.LogExceptionMessage, exc);
			ExceptionHandler.handleException(exc, getShell());
			return true;
		}
		return true;
	}

	protected abstract ReportOptionsPageBase createReportOptionsPage();

}
