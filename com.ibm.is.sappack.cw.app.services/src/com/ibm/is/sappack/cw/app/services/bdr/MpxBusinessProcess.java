package com.ibm.is.sappack.cw.app.services.bdr;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ibm.is.sappack.cw.app.data.bdr.jpa.Process;
import com.ibm.is.sappack.cw.app.services.Constants;
import com.ibm.is.sappack.cw.app.services.ImportProcessProvider;

public class MpxBusinessProcess {
	
	private ArrayList<MpxRecord> recordList;
	private Process process;

	public MpxBusinessProcess(String name, Date creationDate, Process parent, ImportProcessProvider importProcessProvider) {
		this.process = importProcessProvider.provideProcess(parent, name, Constants.EMPTY_STRING, creationDate);
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
	
	public Process getProcess() {
		return process;
	}

	public void setProcess(Process process) {
		this.process = process;
	}

	public void setParent(Process parent) {
		if(parent != null) {
			this.process.setParentProcess(parent);
			List<Process> childProcesses = (List<Process>)parent.getChildProcesses();
			if(childProcesses == null || childProcesses.isEmpty()) {
				childProcesses = new ArrayList<Process>();
				parent.setChildProcesses(childProcesses);
			}
			childProcesses.add(process);
		}
	}
	
}
