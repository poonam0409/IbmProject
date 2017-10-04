package com.ibm.is.sappack.gen.tools.jobgenerator.abapProgramUploadWizard;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.progress.IProgressConstants;

import com.ibm.iis.sappack.gen.common.ui.connections.SapSystem;
import com.ibm.is.sappack.gen.common.BaseException;
import com.ibm.is.sappack.gen.common.Constants;
import com.ibm.is.sappack.gen.common.JobGeneratorException;
import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.common.ui.util.LargeMessageDialog;
import com.ibm.is.sappack.gen.tools.jobgenerator.Activator;
import com.ibm.is.sappack.gen.tools.jobgenerator.wizard.helpers.AbapCodeUploader;

public class ABAPProgramUploadWizard extends Wizard {
	private Vector<ZipFile> zipFiles;
	private Vector<byte[]> zipFileContents;
	private int overallZipFileEntries;
	
	//pages
	private ABAPProgramUploadPage abapProgramUploadPage;
	private ABAPProgramUploadSummaryPage summaryPage;
	
	private SapSystem             sapSystem;
	private boolean               ctsEnabled;
	private String                sapPackage;
	private String                ctsRequest;
	private String 				  ctsRequestDesc;
	private boolean				  createSingleRequest;
	private static final int MAX_REPORTS_PER_OUTPUT_LINE = 4;
	
	private String finalSummary;


	public ABAPProgramUploadWizard(Vector<ZipFile> zipFiles) throws IOException {
		this.setDialogSettings(Activator.getDefault().getDialogSettings());
		
		this.zipFiles = zipFiles;
		this.zipFileContents = new Vector<byte[]>();
		// get content of ABAP code zip files, if something goes wrong exception is thrown, dialog will not appear
		// store content into a vector to have it handy for further use 
		this.overallZipFileEntries = 0;
		
		for (ZipFile zipFile : zipFiles) {
			//count overall zip file entries
			this.overallZipFileEntries += zipFile.size();
			final byte[] zipFileContent;
			zipFileContent = zipFileToByteArray(zipFile);
			zipFileContents.add(zipFileContent);
		}
	    setDefaultPageImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor(Activator.WIZARD_BANNER_IMAGE_ID));
	    setWindowTitle(Messages.ABAPProgramUploadWizard_0);
	    setNeedsProgressMonitor(true);
	    this.abapProgramUploadPage = new ABAPProgramUploadPage(this);
	    this.addPage(abapProgramUploadPage);
	    this.summaryPage = new ABAPProgramUploadSummaryPage(this);
	    this.addPage(summaryPage);
	}

	@Override
	public boolean performFinish() {
		// get config values
		ctsEnabled = abapProgramUploadPage.isCTSEnabled();
		sapSystem = abapProgramUploadPage.getSapSystem();
		// get cts specific config values
		sapPackage = abapProgramUploadPage.getPackage();
		ctsRequest = abapProgramUploadPage.getRequest();
		
		

		Job jobGenJob = new Job(Messages.ABAPProgramUploadWizard_1) {

			@Override
			protected IStatus run(IProgressMonitor overallMonitor) {
				StringBuffer errorBuffer = new StringBuffer();
				StringBuffer resultBuffer = new StringBuffer();

				try {
					
					if (overallMonitor == null) {
						overallMonitor = new NullProgressMonitor();
					}

					// upload ABAP programs
					AbapCodeUploader abapCodeUploader = null;
					List<String> uploadList = new ArrayList<String>();
					int nUploadedReports;
					
					overallMonitor.beginTask(Messages.AbapCodeUploader_0, overallZipFileEntries);

					//iterate over the zipFileContents and upload the entries
					for (int fileNumber=0; fileNumber < zipFileContents.size(); fileNumber++) {
						
						SubProgressMonitor subProgressMonitor = new SubProgressMonitor(overallMonitor, zipFiles.get(fileNumber).size());
						if (ctsEnabled) {
							abapCodeUploader = new AbapCodeUploader(sapSystem,
									sapPackage, ctsRequest, ctsRequestDesc, zipFileContents.get(fileNumber), subProgressMonitor, createSingleRequest);
						} else {
							abapCodeUploader = new AbapCodeUploader(sapSystem,
									zipFileContents.get(fileNumber), subProgressMonitor);
						}

						try {
							abapCodeUploader.uploadAbapReports();
							uploadList.addAll(abapCodeUploader.getReportList());
						} catch (Exception e) {
							String msg = Messages.ABAPExtractConfigurationPageGroup_4;
							throwJobGeneratorException(msg, e);
						}				
						subProgressMonitor.done();
					}



					nUploadedReports = uploadList.size();
					resultBuffer.append(MessageFormat.format(
							Messages.ABAPExtractConfigurationPageGroup_5,
							nUploadedReports));
					if (nUploadedReports == 0) {
						resultBuffer.append(Constants.NEWLINE);
					} else {

						for (int idx = 0; idx < nUploadedReports; idx++) {

							if (idx % MAX_REPORTS_PER_OUTPUT_LINE == 0) {
								resultBuffer.append(Constants.NEWLINE);
							} else {
								resultBuffer.append(", "); //$NON-NLS-1$
							}

							resultBuffer.append(uploadList.get(idx));
						}

						resultBuffer.append(Constants.NEWLINE);
					}
					finalSummary = resultBuffer.toString();
					
					overallMonitor.done();
					   
					Boolean isModal = (Boolean) getProperty(IProgressConstants.PROPERTY_IN_DIALOG);
					if (isModal == null || isModal.booleanValue()) {
						Display.getDefault().syncExec(new Runnable() {
					           public void run() {
					        	   getGenerationCompletedAction().run();
					           }
					        });
					} else {
						setProperty(IProgressConstants.KEEP_PROPERTY, Boolean.TRUE);
				           setProperty(IProgressConstants.ACTION_PROPERTY, 
				                  getGenerationCompletedAction());
					}
				} catch (JobGeneratorException e) {
					e.printStackTrace();
					String msg = MessageFormat.format(
							Messages.ABAPProgramUploadWizard_5, e.getMessage());

					Activator.getLogger().log(Level.SEVERE, msg, e);
					errorBuffer.append(e.getMessage());
					return new Status(IStatus.ERROR, Activator.PLUGIN_ID, msg);

				} catch (Exception exc) {
					Activator.getLogger().log(Level.SEVERE, exc.getMessage(),
							exc);

					String msg;
					if (exc instanceof BaseException) {
						msg = exc.getLocalizedMessage();
					} else {
						msg = exc.getMessage();
					}
					String statusMsg = MessageFormat.format(
							Messages.ABAPProgramUploadWizard_6, exc.getClass()
									.getName() + ": " + msg); //$NON-NLS-1$
					errorBuffer.delete(0,
									errorBuffer.length())
							.append(statusMsg);
					return new Status(IStatus.ERROR, Activator.PLUGIN_ID,
							statusMsg);
				}

				return new Status(IStatus.OK, Activator.PLUGIN_ID,
						Messages.ABAPProgramUploadWizard_11);
			}

		};
		jobGenJob.setUser(true);
		jobGenJob.schedule();

		return true;
	}
	
	private IAction getGenerationCompletedAction() {
		final String title = Messages.ABAPProgramUploadWizard_2;
		return new Action(title) {
			public void run() {
				LargeMessageDialog.openLargeMessageDialog(getShell(), title,
						Messages.ABAPProgramUploadWizard_2,
						finalSummary);
			}
		};
	}
	
	private byte[] zipFileToByteArray(ZipFile zipFile) throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
        FileInputStream is = new FileInputStream(new File(zipFile.getName()));
 
        byte[] buf = new byte[4096];
 
        for (int length = is.read(buf); length > 0; length = is.read(buf)) {
            os.write(buf, 0, length);
        }
 
        is.close();
        return os.toByteArray();
	}
	
	public void setSummary() {
		StringBuffer buf = new StringBuffer();
		buf.append(Messages.ABAPProgramUploadWizard_7).append(Constants.NEWLINE).append(Constants.NEWLINE);
		buf.append(Messages.ABAPProgramUploadWizard_8).append(Constants.NEWLINE);
		buf.append(Constants.TAB).append(abapProgramUploadPage.getSapSystem().getHost()).append(Constants.NEWLINE).append(Constants.NEWLINE);
		buf.append(Messages.ABAPProgramUploadWizard_9).append(Constants.NEWLINE);
		for (ZipFile zipFile : zipFiles) {
			Enumeration<? extends ZipEntry> zipFileEntries = zipFile.entries();
			buf.append(Messages.ABAPProgramUploadWizard_10).append(Constants.TAB).append(zipFile.getName()).append(Constants.NEWLINE);
			while (zipFileEntries.hasMoreElements()) {
				ZipEntry zipEntry = (ZipEntry) zipFileEntries.nextElement();
				buf.append(Constants.TAB).append(zipEntry.getName()).append(Constants.NEWLINE);
			}
		}		
		this.summaryPage.setSummary(buf.toString());
	}	
	
	private void throwJobGeneratorException(String messageTemplate, Exception e) throws JobGeneratorException {
		String msg = MessageFormat.format(messageTemplate, e.getMessage());
		Activator.getLogger().log(Level.WARNING, msg, e);
		e.printStackTrace();
		throw new JobGeneratorException(msg, e);
	}
}
