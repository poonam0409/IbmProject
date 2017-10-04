//---------------------------------------------------------------------------  
// IBM Confidential                                                            
//                                                                             
// OCO Source Materials                                                        
//                                                                             
// 5724-Q55                                                                 
//                                                                             
// (C) Copyright IBM Corporation 2009, 2014                                              
//                                                                             
// The source code for this program is not published or otherwise              
// divested of its trade secrets, irrespective of what has been                
// deposited with the U.S. Copyright Office.                                     
//---------------------------------------------------------------------------  
//-*-************************************************************************  
//                                                                             
// IBM InfoSphere Information Server Packs for SAP Applications 
//                                                                             
// Module Name : com.ibm.iis.sappack.gen.common.ui.wizards
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.iis.sappack.gen.common.ui.wizards;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.progress.IProgressConstants;

import com.ibm.is.sappack.gen.common.ui.Activator;
import com.ibm.is.sappack.gen.common.ui.Messages;


public abstract class CreateFilesInDataDesignProjectWizard extends Wizard implements INewWizard {

	protected SelectDataDesignProjectPage selectDataDesignProjectPage;


	static String copyright() {
		return Copyright.IBM_COPYRIGHT_SHORT;
	}


	protected CreateFilesInDataDesignProjectWizard() {
		super();
	}

	protected boolean fileAlreadyExists(IProject p, String fileName) {
		IFile f = p.getFile(fileName);
		if (f.exists()) {
			return true;
		}
		return false;
	}

	protected boolean createFile(IProject p, String fileName, byte[] b, boolean skipErrors) {
		ByteArrayInputStream bais = new ByteArrayInputStream(b);
		IFile f = p.getFile(fileName);
		try {
			f.create(bais, false, null);
		}
		catch (CoreException e) {
			e.printStackTrace();
			com.ibm.is.sappack.gen.common.ui.Activator.logException(e);
			if (!skipErrors) {
				String errorMessage = Messages.ExampleFileCreationWizard_0; 
				errorMessage = MessageFormat.format(errorMessage, fileName);
				MessageDialog.openError(getShell(), Messages.TitleError, errorMessage); 
				return false;
			}
		}

		return true;
	}

	public void addPages() {
		this.selectDataDesignProjectPage = new SelectDataDesignProjectPage();
		this.selectDataDesignProjectPage.setDescription(Messages.SelectDataDesignProjectPage_2);
		this.selectDataDesignProjectPage.addModifyListenerToProjectCombo(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				IProject p = selectDataDesignProjectPage.getSelectedProject();
				if (p != null) {
					Set<String> files = getFilesToBeCreated(p).keySet();
					for (String file : files) {
						if (fileAlreadyExists(p, file)) {
							String errorMessage = Messages.ExampleFileCreationWizard_2; 
							errorMessage = MessageFormat.format(errorMessage, file);
							if (!selectDataDesignProjectPage.getTreatExistingFilesAsWarnings()) {
								selectDataDesignProjectPage.setErrorMessage(errorMessage);
								selectDataDesignProjectPage.setPageComplete(false);
							} else {
								selectDataDesignProjectPage.setMessage(errorMessage, IMessageProvider.WARNING);
								selectDataDesignProjectPage.setPageComplete(true);
							}
						}
						else {
							selectDataDesignProjectPage.setMessage(null, IMessageProvider.WARNING);
							selectDataDesignProjectPage.setErrorMessage(null);
							selectDataDesignProjectPage.setPageComplete(true);
						}
					}
				}

			}

		});
		addPage(this.selectDataDesignProjectPage);
	}

	protected abstract Map<String, byte[]> getFilesToBeCreated(IProject p);

	public static byte[] getContents(InputStream is) {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		while (true) {
			int size;
			try {
				size = is.read(buffer);
			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
			if (size == -1) {
				break;
			}
			byteArrayOutputStream.write(buffer, 0, size);
		}
		return byteArrayOutputStream.toByteArray();
	}
	
	@Override
	public boolean performFinish() {
		final IProject p = this.selectDataDesignProjectPage.getSelectedProject();
		if (p == null) {
			return false;
		}

		final boolean skipFileCreationError = this.selectDataDesignProjectPage.getTreatExistingFilesAsWarnings();
		
		Job job = new Job(Messages.ExampleFileCreationWizard_1) {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				Map<String, byte[]> files = getFilesToBeCreated(p);
				monitor.beginTask(Messages.ExampleFileCreationWizard_3, files.size());
				
				Iterator<Entry<String, byte[]>> it = files.entrySet().iterator();
				int n = 0;
				while (it.hasNext()) {
					Map.Entry<String, byte[]> me = it.next();
					byte[] b = me.getValue();
					String fileName = me.getKey();
				
					monitor.setTaskName(MessageFormat.format(Messages.ExampleFileCreationWizard_4, fileName));
					if (!createFile(p, fileName, b, skipFileCreationError)) {
						return new Status(IStatus.ERROR, Activator.PLUGIN_ID, MessageFormat.format(Messages.ExampleFileCreationWizard_5, fileName));
					}
					n++;
					monitor.worked(n);
					if (monitor.isCanceled()) {
						return Status.CANCEL_STATUS;
					}
				}
				monitor.setTaskName(MessageFormat.format(Messages.CreateFilesInDataDesignProjectWizard_0, p.getName()));

				return Status.OK_STATUS;
			}
			
		};
		
		job.setUser(true);
		job.setProperty(IProgressConstants.KEEP_PROPERTY, Boolean.TRUE);
		job.schedule();
		return true;
	}

}
