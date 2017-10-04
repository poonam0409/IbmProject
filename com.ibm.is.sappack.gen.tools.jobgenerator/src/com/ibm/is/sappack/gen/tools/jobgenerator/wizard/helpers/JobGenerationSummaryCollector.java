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


import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import com.ibm.is.sappack.gen.common.Constants;
import com.ibm.is.sappack.gen.common.ui.Messages;


public class JobGenerationSummaryCollector {

	private StringBuffer buffer;
	private StringBuffer errorBuffer;
	private String       abapCodeZipFileName = null;
	private List<String> successfulJobsList;
	private List<String> additionalMessages;
	private boolean      abapProgramsWereUploaded = true;


	static String copyright() { 
		return com.ibm.is.sappack.gen.tools.jobgenerator.wizard.helpers.Copyright.IBM_COPYRIGHT_SHORT; 
	}	

	public JobGenerationSummaryCollector() {
		this.buffer = new StringBuffer();
		this.errorBuffer = new StringBuffer();
		this.successfulJobsList = new ArrayList<String>();
		this.additionalMessages = new ArrayList<String>();
	}

	public List<String> getSuccessfulJobList() {
		return this.successfulJobsList;
	}

	public void setABAPCodeZipFilename(String zipFileName) {
		this.abapCodeZipFileName = zipFileName;
	}

	public void setABAPProgramsUploaded(boolean uploaded) {
		this.abapProgramsWereUploaded = uploaded;
	}

	public StringBuffer getSummaryBuffer() {
		return this.buffer;
	}

	public StringBuffer getErrorBuffer() {
		return this.errorBuffer;
	}

	public String getSummary() {
		String overallResult = null;
		if (errorBuffer.length() > 0) {
			overallResult = MessageFormat.format(Messages.JobGeneratorWizard_7, errorBuffer.toString());
		} else {
			overallResult = MessageFormat.format(Messages.JobGeneratorWizard_8, this.buffer.toString());
		}

		StringBuffer t = new StringBuffer(overallResult);
		if (!this.successfulJobsList.isEmpty()) {
			t.append(Constants.NEWLINE + Messages.JobGeneratorSummaryPage_1 + Constants.NEWLINE);
			for (String job : this.successfulJobsList) {
				t.append(job + Constants.NEWLINE);
			}
		}
		if (this.abapCodeZipFileName != null) {
			String msg = Messages.JobGeneratorSummaryPage_2;
			msg = MessageFormat.format(msg, this.abapCodeZipFileName);
			t.append(Constants.NEWLINE + msg);
		}
		if (!this.abapProgramsWereUploaded) {
			String msg = Messages.JobGeneratorSummaryPage_3;
			t.append(Constants.NEWLINE + msg);
		}
		t.append(Constants.NEWLINE);
		for (String m : this.additionalMessages) {
			t.append(Constants.NEWLINE + m);
		}
		return t.toString();
	}

	public void addAdditionalMessage(String msg) {
		this.additionalMessages.add(msg);
	}
}
