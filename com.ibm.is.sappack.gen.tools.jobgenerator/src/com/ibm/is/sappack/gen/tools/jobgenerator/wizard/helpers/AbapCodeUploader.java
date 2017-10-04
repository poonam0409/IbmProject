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
// Module Name : com.ibm.is.sappack.gen.tools.jobgenerator.wizard.helpers
//                                                                             
//*************************-END OF SPECIFICATIONS-***************************
package com.ibm.is.sappack.gen.tools.jobgenerator.wizard.helpers;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.eclipse.core.runtime.IProgressMonitor;

import com.ibm.iis.sappack.gen.common.ui.connections.SapSystem;
import com.ibm.is.sappack.gen.common.Constants;
import com.ibm.is.sappack.gen.common.SAPAccessException;
import com.ibm.is.sappack.gen.common.ui.Messages;
import com.ibm.is.sappack.gen.tools.jobgenerator.jco.JCoService;


public class AbapCodeUploader {
   // -------------------------------------------------------------------------------------
   //                                       Subclasses
   // -------------------------------------------------------------------------------------
	public static class SAPFuncModule {
		private String _FmName;
		private String _FragmentArr[];
		
		SAPFuncModule(String funcModName, String fragmArr[]) {
			this._FmName      = funcModName.trim().toUpperCase();
			this._FragmentArr = fragmArr;
		}
		
		String getName() {
			return(this._FmName);
		} // end of getName()
		
		String [] getFragments() {
			return(this._FragmentArr);
		} // end of getFragments()
	} // end of classSAPFuncModule

   // -------------------------------------------------------------------------------------
   //                                       Constants
   // -------------------------------------------------------------------------------------
	private static final int BYTE_BUFFER_SIZE 			= 1024;
	private static final int ABAP_CODE_FRAGMENT_LENGTH = 70;


   // -------------------------------------------------------------------------------------
   //                                 Member Variables
   // -------------------------------------------------------------------------------------
	private SapSystem 		 sapSystem;
	private byte[] 			 content;
	private IProgressMonitor monitor;
	private String 			 sapPackage;
	private String 			 ctsRequest;
	private String			 ctsRequestDesc;
   private List<String>		 uploadReportList;
   private boolean			 createSingleRequest;


	static String copyright() {
		return com.ibm.is.sappack.gen.tools.jobgenerator.wizard.helpers.Copyright.IBM_COPYRIGHT_SHORT;
	}


	public AbapCodeUploader(SapSystem sapSystem, String ctsPackage, String ctsRequest, String ctsRequestDesc, 
            byte[] content, IProgressMonitor monitor, boolean createSingleRequest) {
		this(sapSystem, content, monitor);
		this.sapPackage = ctsPackage;
		this.ctsRequest = ctsRequest;
		this.ctsRequestDesc=ctsRequestDesc;
		this.createSingleRequest = createSingleRequest;
	}


	public AbapCodeUploader(SapSystem sapSystem, byte[] content, IProgressMonitor monitor) {
		this.sapSystem        = sapSystem;
		this.content          = content;
		this.monitor          = monitor;
		this.uploadReportList = new ArrayList<String>();
		this.sapPackage 		 = null;
		this.ctsRequest 		 = null;
		this.ctsRequestDesc		 = null;
	}


	public List<String> uploadAbapReports() throws SAPAccessException {
		JCoService              jcoService;
		ZipEntry                zipEntry;
		List<SAPFuncModule>     funcModuleList;
		Iterator<SAPFuncModule> listIter;
		boolean                 isUploadCancelled;

		jcoService = JCoService.getJCoService(this.sapSystem, this.sapPackage, this.ctsRequest, this.ctsRequestDesc, this.createSingleRequest);
		String checkMsg = jcoService.checkVersion();
		if (checkMsg != null) {
			throw new SAPAccessException("126900E", new String[] { checkMsg }); //$NON-NLS-1$
		}

		isUploadCancelled = false;
		try {
			ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(this.content));
			this.monitor.beginTask(Messages.AbapCodeUploader_0, getNumberOfReports());

			// Go trough all the ZIP entries
			zipEntry = zipInputStream.getNextEntry();
			while(zipEntry != null && !isUploadCancelled) {
				funcModuleList = extractAbapReports(zipInputStream, zipEntry);

				// upload all ABAP reports
				listIter = funcModuleList.iterator();
				while(listIter.hasNext()) {
					SAPFuncModule curFuncModule = listIter.next();

					if (this.monitor != null) {
						String msg = Messages.AbapCodeUploader_3;
						msg = MessageFormat.format(msg, curFuncModule.getName());
						this.monitor.subTask(msg);
					}

					jcoService.uploadAbapReport(curFuncModule.getName(), curFuncModule.getFragments());

					if (this.monitor != null) {
						this.monitor.worked(1);
						isUploadCancelled = monitor.isCanceled();
					}

					// save name of function module that has successfully uploaded
					this.uploadReportList.add(curFuncModule.getName());
				} // end of while(listIter.hasNext())

				zipInputStream.closeEntry();

				// next ZIP entry ...
				zipEntry = zipInputStream.getNextEntry();
			} // end of while(zipEntry != null)
			zipInputStream.close();
		} // end of try
		catch(IOException ioExcpt) {
			throw new SAPAccessException("126900E", new String[] { ioExcpt.getMessage() }, ioExcpt); //$NON-NLS-1$
		}

		return(uploadReportList);
	}


	private List<SAPFuncModule> extractAbapReports(ZipInputStream zipInputStream, ZipEntry zipEntry) 
	        throws SAPAccessException {

		String              codeFragmentArr[];
		StringBuffer        contentBuffer = new StringBuffer();
		List<SAPFuncModule> moduleList    = new ArrayList<SAPFuncModule>();

		try {
			// get the content for each ZIP entry
			byte[] buffer = new byte[BYTE_BUFFER_SIZE];
			int size;
			while (true) {
				size = zipInputStream.read(buffer, 0, buffer.length);
				if (size == -1) {
					break;
				}
				contentBuffer.append(new String(buffer, 0, size, Constants.STRING_ENCODING));
			}

			codeFragmentArr = getFragments(contentBuffer, ABAP_CODE_FRAGMENT_LENGTH);
			moduleList.add(new SAPFuncModule(zipEntry.getName(), codeFragmentArr));
		} // end of try
		catch(IOException ioExcpt) {
			throw new SAPAccessException("126900E", new String[] { ioExcpt.getMessage() }, ioExcpt); //$NON-NLS-1$
		}
		
		return(moduleList);
	}


	private String[] getFragments(StringBuffer buffer, int fragmentLength) {
		// int numFragments = (int) Math.ceil(buffer.length() / (fragmentLength
		// * 1f));
		// String[] fragments = new String[numFragments];
		// for (int i = 0; i < numFragments; i++) {
		// int start = i * fragmentLength;
		// int end = start + fragmentLength;
		// end = (end > buffer.length()) ? buffer.length() : end;
		//
		// fragments[i] = buffer.substring(start, end);
		// }
		// return fragments;
		String[] frags = buffer.toString().split("\n"); //$NON-NLS-1$
		for (int i = 0; i < frags.length; i++) {
			frags[i] = frags[i].replaceAll("\r", "");  //$NON-NLS-1$//$NON-NLS-2$
		}
		return frags;
	}


	public List<String> getReportList() {
      return(uploadReportList);
   }
   

	public int getNumberOfReports() throws IOException {
		ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(content));
		int count = 0;
		while (true) {
			ZipEntry zipEntry = zipInputStream.getNextEntry();
			if (zipEntry == null) {
				break;
			}
			count++;
			zipInputStream.closeEntry();
		}
		zipInputStream.close();
		return count;
	}

}
