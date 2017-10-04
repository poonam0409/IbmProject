package com.ibm.is.sappack.cw.app.services.bdr;

import java.util.ArrayList;
import java.util.Date;

import com.ibm.is.sappack.cw.app.services.Constants;
import com.ibm.is.sappack.cw.app.services.ImportProcessProvider;
import com.ibm.is.sappack.cw.app.data.bdr.jpa.Process;

public class MpxBusinessScenario {
	
	private ArrayList<MpxRecord> recordList;
	private Process process;
	
	public MpxBusinessScenario(String name, Date creationDate, ImportProcessProvider importProcessProvider) {
		this.process = importProcessProvider.provideTopLevelProcess(name, Constants.EMPTY_STRING, creationDate);
	}

	public void setRecordList(ArrayList<MpxRecord> recordList) {
		this.recordList = recordList;
	}

	public ArrayList<MpxRecord> getRecordList() {
		if(recordList == null) {
			recordList = new ArrayList<MpxRecord>();
		}
		return recordList;
	}

	public void setProcess(Process process) {
		this.process = process;
	}

	public Process getProcess() {
		return process;
	}
	
}
