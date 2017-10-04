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
// Module Name : com.ibm.iis.sappack.gen.tools.sap.importer
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.iis.sappack.gen.tools.sap.importer;


import java.text.MessageFormat;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;

import com.ibm.iis.sappack.gen.common.ui.connections.PasswordDialog;
import com.ibm.iis.sappack.gen.common.ui.connections.SAPConnectionRepository;
import com.ibm.iis.sappack.gen.common.ui.connections.SapSystem;
import com.ibm.iis.sappack.gen.tools.sap.api.IProgressCallback;
import com.ibm.iis.sappack.gen.tools.sap.api.RapidModeler;
import com.ibm.iis.sappack.gen.tools.sap.api.RapidModelerException;
import com.ibm.iis.sappack.gen.tools.sap.idocseglist.IDocSegmentList;
import com.ibm.iis.sappack.gen.tools.sap.rmconf.RMConfiguration;
import com.ibm.iis.sappack.gen.tools.sap.tablelist.TableList;
import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.tools.sap.activator.Activator;
import com.ibm.is.sappack.gen.tools.sap.utilities.Utilities;


public class RapidModelerImpl implements RapidModeler {
  	static String copyright() { 
  	   return Copyright.IBM_COPYRIGHT_SHORT; 
  	}	

	public static IFile getFile(String path) {
		if (path == null) {
			return null;
		}
		IPath ipath = new Path(path);
		IFile f = null;
		try {
			f = ResourcesPlugin.getWorkspace().getRoot().getFile(ipath);
		} catch (Exception exc) {
			Activator.logException(exc);
			return null;
		}
		if (!f.exists()) {
			return null;
		}
		return f;
	}

	public static IFile getLDMFile(String ldmFileName) throws RapidModelerException {
		IFile ldmFile = getFile(ldmFileName);
		if (ldmFile == null) {
			IPath path = new Path(ldmFileName);
			ldmFile = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
			try {
				Utilities.createEmptyLDM(ldmFile);
			} catch (Exception e) {
				Activator.logException(e);
				throw new RapidModelerException(MessageFormat.format(Messages.RapidModelerImpl_0, ldmFileName));
			}
		}
		return ldmFile;
	}

	public static IProgressCallback createEmptyProgressCallback() {
		return new IProgressCallback() {

			@Override
			public void worked(int work) {
			}

			@Override
			public void subTask(String name) {
			}

			@Override
			public void setTaskName(String name) {
			}

			@Override
			public void setCanceled(boolean value) {
			}

			@Override
			public boolean isCanceled() {
				return false;
			}

			@Override
			public void exceptionOccurred(Exception exc) {
			}

			@Override
			public void done() {
			}

			@Override
			public void beginTask(String name, int totalWork) {
			}
		};

	}

	public static IProgressMonitor createDelegatingProgressMonitor(IProgressCallback callback) {
		final IProgressCallback fProgressMonitor = callback;
		return new NullProgressMonitor() {

			public void beginTask(String name, int totalWork) {
				fProgressMonitor.beginTask(name, totalWork);
			}

			public void done() {
				fProgressMonitor.done();
			}

			public boolean isCanceled() {
				return fProgressMonitor.isCanceled();
			}

			public void setCanceled(boolean value) {
				fProgressMonitor.setCanceled(value);
			}

			public void setTaskName(String name) {
				fProgressMonitor.setTaskName(name);
			}

			public void subTask(String name) {
				fProgressMonitor.subTask(name);
			}

			public void worked(int work) {
				fProgressMonitor.worked(work);
			}

		};

	}

	public RapidModelerImpl() {
	}

	public void importMetadata(String sapSystemName, String sapUser, String sapPassword,
			String sapObjectCollectionFileName, String rapidModelerConfigurationFileName, String ldmFileName, String checkTableLDMFileName,
			boolean background, IProgressCallback progressCallback) throws RapidModelerException {

		if (progressCallback == null) {
			progressCallback = createEmptyProgressCallback();
		}
		final IProgressCallback fProgressCallback = progressCallback;
		final IProgressMonitor iprogressMonitor = createDelegatingProgressMonitor(progressCallback);

		SAPConnectionRepository rep = SAPConnectionRepository.getRepository();
		SapSystem sapSystem1 = rep.getSAPConnection(sapSystemName);
		if (sapSystem1 == null) {
			throw new RapidModelerException(MessageFormat.format(Messages.RapidModelerImpl_1, sapSystemName));
		}
		final SapSystem sapSystem = sapSystem1.clone();
		
		if (sapUser != null) {
			sapSystem.setUsername(sapUser);
		}
		if (sapPassword != null) {
			sapSystem.setPassword(sapPassword);
		}
		Display displ = Display.getCurrent();
		boolean passwordIsOK;
		if (displ != null) {
			passwordIsOK = PasswordDialog.checkForPassword(null, sapSystem);
		} else {
			passwordIsOK = sapSystem.getPassword() != null;
		}
		if (!passwordIsOK) {
			throw new RapidModelerException(MessageFormat.format(Messages.RapidModelerImpl_2, sapSystemName));
		}

		IFile socFile = getFile(sapObjectCollectionFileName);
		if (socFile == null) {
			throw new RapidModelerException(MessageFormat.format(Messages.RapidModelerImpl_3, sapObjectCollectionFileName));
		}

		TableList tableList = null;
		IDocSegmentList idocSegmentlist = null;

		if (Utilities.endsWith(socFile, TableList.TABLE_LIST_FILE_EXTENSION)) {
			try {
				tableList = new TableList(socFile);
			} catch (Exception e) {
				throw new RapidModelerException(MessageFormat.format(Messages.RapidModelerImpl_4, sapObjectCollectionFileName));
			}
		} else if (Utilities.endsWith(socFile, IDocSegmentList.IDOC_SEGMENT_LIST_FILE_EXTENSION)) {
			try {
				idocSegmentlist = new IDocSegmentList(socFile);
			} catch (Exception e) {
				throw new RapidModelerException(MessageFormat.format(Messages.RapidModelerImpl_5, sapObjectCollectionFileName));
			}
		} else {
			throw new RapidModelerException(MessageFormat.format(Messages.RapidModelerImpl_6, sapObjectCollectionFileName));
		}
		final TableList fTableList = tableList;
		final IDocSegmentList fIDocSegmentList = idocSegmentlist;

		IFile rmConfigFile = getFile(rapidModelerConfigurationFileName);
		if (rmConfigFile == null) {
			throw new RapidModelerException(MessageFormat.format(Messages.RapidModelerImpl_7, rapidModelerConfigurationFileName));
		}

		RMConfiguration rmConfig = null;
		try {
			rmConfig = new RMConfiguration(rmConfigFile);
		} catch (Exception e) {
			throw new RapidModelerException(MessageFormat.format(Messages.RapidModelerImpl_8, rapidModelerConfigurationFileName));
		}
		final RMConfiguration frmConfig = rmConfig;

		final IFile fLDMFile = getLDMFile(ldmFileName);
		IFile checkTableFile = null;
		if (checkTableLDMFileName != null && !checkTableLDMFileName.trim().isEmpty()) {
			checkTableFile = getLDMFile(checkTableLDMFileName);
		}
		final IFile fCheckTableFile = checkTableFile;

		if (background) {
			Job job = new Job(Messages.RapidModelerImpl_9) {

				@Override
				public IStatus run(IProgressMonitor pm) {
					IStatus result = new Status(Status.OK, Activator.PLUGIN_ID, Messages.RapidModelerImpl_10);
					try {
						DelegatingProgressMonitor dpm = new DelegatingProgressMonitor(new IProgressMonitor[] { pm, iprogressMonitor });
						MetaDataImporter importer = new MetaDataImporter(sapSystem, fTableList, fIDocSegmentList, frmConfig, dpm);
						importer.runImport(fLDMFile, fCheckTableFile);
						dpm.done();
					} catch (Exception exc) {
						result = new Status(Status.ERROR, Activator.PLUGIN_ID, Messages.RapidModelerImpl_11, exc);
						fProgressCallback.exceptionOccurred(exc);
					}
					return result;
				}

			};
			job.setUser(true);
			job.schedule();
		} else {
			try {
				MetaDataImporter importer = new MetaDataImporter(sapSystem, fTableList, fIDocSegmentList, frmConfig, iprogressMonitor);
				importer.runImport(fLDMFile, fCheckTableFile);
				iprogressMonitor.done();
			} catch (Exception e) {
				fProgressCallback.exceptionOccurred(e);
				throw new RapidModelerException(e);
			}
		}
	}
}
